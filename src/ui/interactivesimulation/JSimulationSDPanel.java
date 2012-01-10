/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
* Class JSimulationSDPanel
* Generic panel for displaying simulation transactions in the form
* of a Sequence Diagram
* Creation: 26/05/2011
* @version 1.0 26/05/2011
* @author Ludovic APVRILLE
* @see
*/


package ui.interactivesimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import java.awt.image.*;

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import myutil.*;
import ui.*;

public class JSimulationSDPanel extends JPanel implements MouseMotionListener, Runnable  {
	
	private static int MAX_X = 800;
	private static int MAX_Y = 200;
	private static long stamp = 0;
	
	// Drawing area
	private int minLimit = 10;
	private int maxX = MAX_X;
	private int maxY = MAX_Y;
	private final int limit = 10;
	
	// Drawing parameters
	private int minSpaceBetweenLifeLines = 5;
	private int spaceBetweenLifeLines = 150;
	private boolean spaceBetweenLifeLinesComputed = false;
	private int spaceAtEnd = 50;
	private int spaceAtTop = 50;
	private int verticalSpaceUnderBlocks = 15;
	private int spaceVerticalText = 2;
	private int spaceHorizontalText = 2;
	private int spaceStop = 20;
	private int verticalLink = 7;
	private int lengthAsync = 50;
	private int spaceBroadcast = 25;
	
	// Transactions
	protected String fileReference;
	
	private int maxNbOfTransactions = 10000;
	private int drawnTransactions = 10000;
	
	// My scroll panel
	private JScrollPane jsp;
	private boolean mustScroll = true;
	
	// Mouse
	private int xMouse, yMouse;
	private boolean drawInfo = false;
	private long clockValueMouse;
	private long clockDiviser = 1000000; //ms
	private Vector<Point> points;
	private Vector<GenericTransaction> transactionsOfPoints;
	private Hashtable<String, Point> asyncMsgs;
	
	// List of entities ... List is discovered progressively
	// Or the list is described in the trace (header information)
	Vector <String> entityNames;
	
	private final int NO_MODE = 0;
	private final int FILE_MODE = 1;
	private int mode;
	private boolean go;
	private Thread t;
	
	private int excluOnList = 1; 
	
	Vector<GenericTransaction> transactions;
	
	JFrameSimulationSDPanel jfssdp;
	
	
	public JSimulationSDPanel(JFrameSimulationSDPanel _jfssdp) {
		//points = new Vector<Point>();
		//transactionsOfPoints = new Vector<AvatarSimulationTransaction>();
		jfssdp = _jfssdp;
		
		entityNames = new Vector <String>();
		transactions = new Vector<GenericTransaction>();
		transactionsOfPoints = new Vector<GenericTransaction>();
		points = new Vector<Point>();
		
		asyncMsgs = new Hashtable<String, Point>();
		
		mode = NO_MODE;
		
		setBackground(Color.WHITE);
		setNewSize();
		addMouseMotionListener(this);
		
	}
	
	//Return true iff exclu was obtained
	private synchronized boolean tryToGetExclu() {
		if (excluOnList == 1) {
			excluOnList = 0;
			return true;
		}
		return false;
	}
	
	private synchronized void getExclu() {
		//TraceManager.addDev("Trying to get exlu");
		while(excluOnList == 0) {
			try {
				//TraceManager.addDev("Waiting");
				wait();
			} catch (Exception e) {
			}
		}
		//TraceManager.addDev("got the exlu");
		excluOnList = 0;
	}
	
	private synchronized void removeExclu() {
		excluOnList = 1;
		notifyAll();
	}
	
	public void setMyScrollPanel(JScrollPane _jsp) {
		jsp = _jsp;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int currentY = spaceAtTop;
		int currentX = spaceAtEnd;
		int oldMaxY = maxY;
		int oldMaxX = maxX;
		
		boolean mustDraw = tryToGetExclu();
		if (mustDraw) {
			if (!spaceBetweenLifeLinesComputed) {
				computeSpaceBetweenLifeLines(g);
			}
			
			
			currentY = paintTopElements(g, currentX, currentY);
			paintTransactions(g, currentX, currentY);
			stamp ++;
			
			if ((oldMaxY != maxY) || (oldMaxX != maxX)) {
				maxX = Math.max(maxX, MAX_X);
				maxY = Math.max(maxY, MAX_Y);
				if ((oldMaxY != maxY) || (oldMaxX != maxX)) {
					setNewSize();
					//repaint();
				}
			} else {
				if (mustScroll) {
					scrollToLowerPosition();
					mustScroll = false;
				}
			}
			
			if (drawInfo) {
				drawInfo(g);
			}
			
			removeExclu();
		}
	}
	
	protected void scrollToLowerPosition() {
		if (jsp != null) {
			jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
		}
	}
	
	// Returns the currentY position
	protected int paintTopElements(Graphics g, int currentX, int currentY) {
		int w;
		
		for(String name : entityNames) {
			g.drawLine(currentX + (spaceBetweenLifeLines/4), currentY, currentX + (3*spaceBetweenLifeLines/4), currentY);
			g.drawLine(currentX + (spaceBetweenLifeLines/2), currentY, currentX + (spaceBetweenLifeLines/2), currentY + verticalSpaceUnderBlocks);
			w = g.getFontMetrics().stringWidth(name);
			g.drawString(name, currentX + ((spaceBetweenLifeLines-w)/2), currentY - spaceVerticalText);
			currentX += spaceBetweenLifeLines;
		}
		
		maxX = currentX;
		
		return currentY + verticalSpaceUnderBlocks;
	}
	
	private void computeSpaceBetweenLifeLines(Graphics g) {
		int w;
		
		spaceBetweenLifeLinesComputed = true;
		
		for(String name : entityNames) {
			w = g.getFontMetrics().stringWidth(name);
			if ((w+minSpaceBetweenLifeLines) > spaceBetweenLifeLines) {
				spaceBetweenLifeLines = w+minSpaceBetweenLifeLines;
			}
		}
		
	}
	
	public void setNewSize() {
		setPreferredSize(new Dimension(maxX + limit, maxY + limit));
		mustScroll = true;
		revalidate();
	}
	
	// returns the currentY position
	protected int paintTransactions(Graphics g, int currentX, int currentY) {
		long clockValue = -1;
		int index;
		int xOfBlock;
		int newCurrentY = currentY;
		GenericTransaction gt;
		
		
		if (transactions.size() == 0) {
			return currentY;
		}
		
		// Draw only the last "drawnTransactions"
		
		if (transactions.size() > 0) {
			gt = transactions.get(Math.max(0, transactions.size()-1000));
			clockValue = gt.startingTime;
			clockValueMouse = clockValue;
			g.drawString("@" + clockValue/clockDiviser, 10, currentY+g.getFontMetrics().getHeight()/2); 
		}
		
		for(int i=Math.max(transactions.size()-drawnTransactions, 0); i<transactions.size(); i++) {
			gt = transactions.get(i);
			gt.stamp = stamp;
			
			index = getIndexOfEntityName(gt.entityName);
			xOfBlock = currentX + (index * spaceBetweenLifeLines) + spaceBetweenLifeLines/2;
			
			points.clear();
			transactionsOfPoints.clear();
			//TraceManager.addDev("Clearing hash");
			//asyncMsgs.clear();
			
			
			if (gt.type == gt.STATE_ENTERING) {
				newCurrentY = drawState(g, gt, xOfBlock, currentY); 
			}  else if (gt.type == gt.FUNCTION_CALL) {
				newCurrentY = drawFunctionCall(g, gt, xOfBlock, currentY); 
			} else if (gt.type == gt.SEND_SYNCHRO) {
				//newCurrentY = drawSendSynchro(g, gt, xOfBlock, currentY); 
			} else if (gt.type == gt.SYNCHRO) {
				newCurrentY = drawSendSynchro(g, gt, xOfBlock, currentY); 
			} else if (gt.type == gt.SEND_ASYNCHRO) {
				newCurrentY = drawSendAsynchro(g, gt, xOfBlock, currentY); 
			} else if (gt.type == gt.RECEIVE_ASYNCHRO) {
				newCurrentY = drawReceiveAsynchro(g, gt, xOfBlock, currentY); 
			} 
			
			if ((yMouse>= currentY) && (yMouse <= newCurrentY)) {
				for(int cpt = 0; cpt<points.size(); cpt++) {
					drawIDInfo(g, points.get(cpt).x, points.get(cpt).y, transactionsOfPoints.get(cpt).ID);
				}
			}
			
			
			// Draw the line of other blocks
			if (currentY != newCurrentY) {
				xOfBlock = currentX + spaceBetweenLifeLines/2;
				for(String s: entityNames) {
					if (s.compareTo(gt.entityName) != 0) {
						g.drawLine(xOfBlock, currentY, xOfBlock, newCurrentY);
					}
					xOfBlock += spaceBetweenLifeLines;
				}
				if (gt.finishTime != clockValue) {
					boolean alsoText = false;
					if ((gt.finishTime / clockDiviser) != (clockValue / clockDiviser)) {
						alsoText = true;
					}
					clockValue = gt.finishTime;
					if (yMouse >= newCurrentY) {
						clockValueMouse = clockValue;
					}
					if (alsoText) {
						g.drawString("@" + clockValue/clockDiviser, 10, newCurrentY+g.getFontMetrics().getHeight()/2);
					}
				}
			}
			
			// Update currentY;
			currentY = newCurrentY;
			
		}
		maxY = currentY;
		return currentY;
	}
	
	private int drawState(Graphics g, GenericTransaction _gt, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		w = g.getFontMetrics().stringWidth(_gt.action);
		Color c = g.getColor();
		Color avat = ColorManager.AVATAR_STATE;
		g.setColor(avat);
		
		x = currentX - w/2 - spaceHorizontalText;
		y = currentY;
		width = w + 2*spaceHorizontalText;
		height = g.getFontMetrics().getHeight() + spaceVerticalText * 2;
		
		g.fillRoundRect(x, y, width, height, 5, 5);
		points.add(new Point(x+width, y));
		transactionsOfPoints.add(_gt);
		g.setColor(c);
		g.drawRoundRect(x, y, width, height, 5, 5);
		
		g.drawString(_gt.action, x + spaceHorizontalText, y+height-2*spaceVerticalText);
		
		currentY += height;
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		return currentY + verticalLink;
	}
	
	private int drawFunctionCall(Graphics g, GenericTransaction _gt, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		w = g.getFontMetrics().stringWidth(_gt.action);
		Color c = g.getColor();
		
		x = currentX - w/2 - spaceHorizontalText;
		y = currentY;
		width = w + 2*spaceHorizontalText;
		height = g.getFontMetrics().getHeight() + spaceVerticalText * 2;
		
		
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y, width, height, 5, 5);
		g.setColor(c);
		g.drawRoundRect(x, y, width, height, 5, 5);
		points.add(new Point(x+width, y));
		transactionsOfPoints.add(_gt);
		g.setColor(c);
		g.drawString(_gt.action, x + spaceHorizontalText, y+height-2*spaceVerticalText);
		
		currentY += height;
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		return currentY + verticalLink;
	}
	
	private int drawSendSynchro(Graphics g, GenericTransaction _gt, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		String messageName;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		messageName = _gt.name + "(" + _gt.params + ")";
		
		Color c = g.getColor();
		
		x = currentX;
		y = currentY;
		
		int index = getIndexOfEntityName(_gt.otherEntityName);
		int xOf2ndBlock = spaceAtEnd + (index * spaceBetweenLifeLines) + spaceBetweenLifeLines/2;
		
		g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
		g.drawLine(xOf2ndBlock, currentY-1, currentX, currentY-1);
		g.setColor(c);
		GraphicLib.arrowWithLine(g, 1, 0, 10, currentX, currentY, xOf2ndBlock, currentY, true);
		transactionsOfPoints.add(_gt);
		points.add(new Point(currentX, currentY));
		
		// Putting the message name
		w = g.getFontMetrics().stringWidth(messageName);
		int xtmp = (xOf2ndBlock + currentX)/2 - w/2;
		g.drawString(messageName, xtmp, currentY-2); 
		
		currentY += 10;
		
		// Vertical line of receiving block
		g.drawLine(currentX, currentY-20, currentX, currentY);
		return currentY;
	}
	
	private int drawSendAsynchro(Graphics g, GenericTransaction _gt, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		String messageName;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		messageName = _gt.name + "(" + _gt.params + ")";
		
		Color c = g.getColor();
		
		x = currentX;
		y = currentY;
		
		int xOf2ndBlock = x + 2*spaceBetweenLifeLines/3;
		
		g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
		g.drawLine(xOf2ndBlock, currentY-1, currentX, currentY-1);
		g.setColor(c);
		GraphicLib.arrowWithLine(g, 1, 2, 10, currentX, currentY, xOf2ndBlock, currentY, false);
		transactionsOfPoints.add(_gt);
		points.add(new Point(currentX, currentY));
		//TraceManager.addDev("Putting " + _gt.messageID + " in hash");
		asyncMsgs.put(_gt.messageID, new Point(currentX, currentY));
		
		// Putting the message name
		w = g.getFontMetrics().stringWidth(messageName);
		int xtmp = (xOf2ndBlock + currentX)/2 - w/2;
		g.drawString(messageName, xtmp, currentY-2); 
		
		currentY += 10;
		
		// Vertical line of receiving block
		g.drawLine(currentX, currentY-20, currentX, currentY);
		return currentY;
	}
	
	private int drawReceiveAsynchro(Graphics g, GenericTransaction _gt, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		String messageName;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		messageName = _gt.name + "(" + _gt.params + ")";
		
		Color c = g.getColor();
		
		x = currentX;
		y = currentY;
		
		int xOf2ndBlock = x - 2*spaceBetweenLifeLines/3;
		
		g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
		g.drawLine(xOf2ndBlock, currentY-1, currentX, currentY-1);
		g.setColor(c);
		GraphicLib.arrowWithLine(g, 1, 2, 10, xOf2ndBlock, currentY, currentX, currentY, false);
		transactionsOfPoints.add(_gt);
		points.add(new Point(currentX, currentY));
		
		// Putting the message name
		w = g.getFontMetrics().stringWidth(messageName);
		int xtmp = (xOf2ndBlock + currentX)/2 - w/2;
		g.drawString(messageName, xtmp, currentY-2);
		
		
		// Linking to sender?
		Point p = asyncMsgs.get(_gt.messageID);
		//TraceManager.addDev("Testing " + _gt.messageID + " in hash = " + p + " hashsize=" + asyncMsgs.size() );
		if (p != null) {
			x = p.x;
			y = p.y;
			int lengthAsync = 2*spaceBetweenLifeLines/3;
			
			if ((x +  lengthAsync) < (currentX-lengthAsync)) {
				// Forward
				g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
				GraphicLib.dashedLine(g, x + lengthAsync - 1, y, x + lengthAsync-1, currentY);
				GraphicLib.dashedLine(g, x + lengthAsync, currentY-1, currentX-lengthAsync, currentY-1);
				g.setColor(c);
				GraphicLib.dashedLine(g, x + lengthAsync, y, x + lengthAsync, currentY);
				GraphicLib.dashedLine(g, x + lengthAsync, currentY, currentX-lengthAsync, currentY);
			} else {
				// Backward
				g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
				GraphicLib.dashedLine(g, x + lengthAsync-1, y, x + lengthAsync-1, y+7);
				GraphicLib.dashedLine(g, x + lengthAsync, y+6, currentX-lengthAsync, y+6);
				GraphicLib.dashedLine(g, currentX-lengthAsync-1, currentY, currentX-lengthAsync-1, y+7);
				g.setColor(c);
				GraphicLib.dashedLine(g, x + lengthAsync, y, x + lengthAsync, y+7);
				GraphicLib.dashedLine(g, x + lengthAsync, y+7, currentX-lengthAsync, y+7);
				GraphicLib.dashedLine(g, currentX-lengthAsync, currentY, currentX-lengthAsync, y+7);
			}
		}
		
		currentY += 10;
		
		// Vertical line of receiving block
		g.drawLine(currentX, currentY-20, currentX, currentY);
		return currentY;
	}
	
	/*private int drawTransition(Graphics g, AvatarTransition at, AvatarSimulationTransaction ast, int currentX, int currentY) {
	int w;
	int x, y, width, height;
	int cpt;
	Color c = g.getColor();
	
	// Duration ?
	if (ast.duration > 0) {
	g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
	currentY += verticalLink;
	g.drawRect(currentX-5, currentY, 10, 30);
	points.add(new Point(currentX+10, currentY));
	transactionsOfPoints.add(ast);
	g.drawString(""+ ast.duration, currentX+6, currentY+17);
	currentY += 30;
	g.setColor(ColorManager.AVATAR_TIME);
	g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
	g.setColor(c);
	currentY += verticalLink;
	}
	
	if (ast.actions == null) {
	return currentY;
	}
	
	if (ast.actions.size() == 0) {
	return currentY;
	}
	
	
	w = 0;
	for(String action: ast.actions) {
	w = Math.max(g.getFontMetrics().stringWidth(action), w);
	}
	
	x = currentX - w/2 - spaceHorizontalText;
	y = currentY;
	width = w + 2*spaceHorizontalText;
	height = (g.getFontMetrics().getHeight() + spaceVerticalText * 2) * ast.actions.size();
	
	g.setColor(Color.WHITE);
	g.fillRoundRect(x, y, width, height, 5, 5);
	g.setColor(c);
	g.drawRoundRect(x, y, width, height, 5, 5);
	points.add(new Point(x+width, y));
	transactionsOfPoints.add(ast);
	
	cpt = 1;
	
	Color avat = ColorManager.AVATAR_ACTION;
	g.setColor(avat);
	int decVert = height / ast.actions.size();
	for(String action: ast.actions) {
	g.drawString(action, x + (width - g.getFontMetrics().stringWidth(action))/2, y+(decVert*cpt) - (spaceVerticalText * 2));
	cpt ++;
	}
	g.setColor(c);
	
	currentY += height;
	g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
	
	return currentY + verticalLink;
	}
	
	private int drawRandom(Graphics g, AvatarRandom arandom, AvatarSimulationTransaction ast, int currentX, int currentY) {
	int w;
	int x, y, width, height;
	Color c = g.getColor();
	
	if (ast.actions == null) {
	return currentY;
	}
	
	String action = ast.actions.get(0);
	
	g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
	currentY += verticalLink;
	
	w = g.getFontMetrics().stringWidth(action);
	x = currentX - w/2 - spaceHorizontalText;
	y = currentY;
	width = w + 2*spaceHorizontalText;
	height = g.getFontMetrics().getHeight() + spaceVerticalText * 2;
	g.setColor(Color.WHITE);
	g.fillRoundRect(x, y, width, height, 5, 5);
	points.add(new Point(x+width, y));
	transactionsOfPoints.add(ast);
	g.setColor(c);
	g.drawRoundRect(x, y, width, height, 5, 5);
	
	g.drawString(action, x + spaceHorizontalText, y+height-2*spaceVerticalText);
	
	currentY += height;
	g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
	return currentY + verticalLink;
	}
	
	private int drawAvatarActionOnSignal(Graphics g, AvatarActionOnSignal aaos, AvatarSimulationTransaction ast, int currentX, int currentY, int startX) {
	int w;
	Color c = g.getColor();
	
	avatartranslator.AvatarSignal sig = aaos.getSignal();
	avatartranslator.AvatarRelation rel = ass.getAvatarSpecification().getAvatarRelationWithSignal(sig);
	if (sig.isIn()) {
	if (!(rel.isAsynchronous())) {
	
	//Synchronous
	if (ast.linkedTransaction != null) {
	// Computing message name
	AvatarActionOnSignal otherAaos = (AvatarActionOnSignal)(ast.linkedTransaction.executedElement);
	String messageName = otherAaos.getSignal().getName();
	if (messageName.compareTo(sig.getName()) != 0) {
	messageName += "_" + sig.getName();
	}
	messageName += "(";
	
	if(ast.actions != null) {
	messageName += ast.actions.get(0); 
	}
	messageName += ")";
	
	//TraceManager.addDev("Working on message name:" + messageName);
	// Drawing the arrow
	// Assume a different block in the two transactions
	int index = ass.getSimulationBlocks().indexOf(ast.linkedTransaction.asb);
	int xOf2ndBlock = startX + (index * spaceBetweenLifeLines) + spaceBetweenLifeLines/2;
	
	currentY += 10;
	g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
	g.drawLine(xOf2ndBlock, currentY-1, currentX, currentY-1);
	g.setColor(c);
	GraphicLib.arrowWithLine(g, 1, 0, 10, xOf2ndBlock, currentY, currentX, currentY, true);
	points.add(new Point(xOf2ndBlock, currentY));
	transactionsOfPoints.add(ast.linkedTransaction);
	transactionsOfPoints.add(ast);
	points.add(new Point(currentX, currentY));
	
	
	// Putting the message name
	w = g.getFontMetrics().stringWidth(messageName);
	int xtmp = (xOf2ndBlock + currentX)/2 - w/2;
	g.drawString(messageName, xtmp, currentY-2); 
	
	
	currentY += 10;
	
	// Vertical line of receiving block
	g.drawLine(currentX, currentY-20, currentX, currentY);
	return currentY;
	}
	} else {
	// In, asynchronous
	String messageName = sig.getName() + "(";
	if(ast.actions != null) {
	messageName += ast.actions.get(0); 
	}
	messageName += ")";
	
	currentY += 10;
	g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
	g.drawLine(currentX-lengthAsync, currentY-1, currentX, currentY-1);
	g.setColor(c);
	GraphicLib.arrowWithLine(g, 1, 1, 10, currentX-lengthAsync, currentY, currentX, currentY, false);
	points.add(new Point(currentX, currentY));
	transactionsOfPoints.add(ast);
	
	// Putting the message name
	w = g.getFontMetrics().stringWidth(messageName);
	g.drawString(messageName, currentX-lengthAsync+w/2, currentY-2); 
	
	// Search for sender
	if (ast.linkedTransaction != null) {
	if (ast.linkedTransaction.stamp == ast.stamp) {
	if ((ast.linkedTransaction.x >0) && (ast.linkedTransaction.y >0)) {
	int x = ast.linkedTransaction.x;
	int y = ast.linkedTransaction.y;
	
	if (x + lengthAsync < currentX-lengthAsync) {
	// Forward
	g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
	GraphicLib.dashedLine(g, x + lengthAsync - 1, y, x + lengthAsync-1, currentY);
	GraphicLib.dashedLine(g, x + lengthAsync, currentY-1, currentX-lengthAsync, currentY-1);
	g.setColor(c);
	GraphicLib.dashedLine(g, x + lengthAsync, y, x + lengthAsync, currentY);
	GraphicLib.dashedLine(g, x + lengthAsync, currentY, currentX-lengthAsync, currentY);
	} else {
	// Backward
	g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
	GraphicLib.dashedLine(g, x + lengthAsync-1, y, x + lengthAsync-1, y+7);
	GraphicLib.dashedLine(g, x + lengthAsync, y+6, currentX-lengthAsync, y+6);
	GraphicLib.dashedLine(g, currentX-lengthAsync-1, currentY, currentX-lengthAsync-1, y+7);
	g.setColor(c);
	GraphicLib.dashedLine(g, x + lengthAsync, y, x + lengthAsync, y+7);
	GraphicLib.dashedLine(g, x + lengthAsync, y+7, currentX-lengthAsync, y+7);
	GraphicLib.dashedLine(g, currentX-lengthAsync, currentY, currentX-lengthAsync, y+7);
	}
	
	//g.drawLine(x + lengthAsync, y, currentX-lengthAsync, currentY);
	}
	}
	} else {
	//TraceManager.addDev("No linked transaction");
	}
	
	currentY += 10;
	
	// Vertical line of receiving block
	g.drawLine(currentX, currentY-20, currentX, currentY);
	
	
	
	return currentY;
	}
	} else {
	if (rel.isAsynchronous()) {
	// Out, asynchronous
	String messageName = sig.getName() + "(";
	if(ast.actions != null) {
	messageName += ast.actions.get(0); 
	}
	messageName += ")";
	
	currentY += 10;
	g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
	g.drawLine(currentX+lengthAsync, currentY-1, currentX, currentY-1);
	g.setColor(c);
	GraphicLib.arrowWithLine(g, 1, 2, 10, currentX, currentY, currentX+lengthAsync, currentY, false);
	points.add(new Point(currentX, currentY));
	transactionsOfPoints.add(ast);
	
	// Putting the message name
	w = g.getFontMetrics().stringWidth(messageName);
	g.drawString(messageName, currentX+lengthAsync-w/2, currentY-2); 
	
	ast.x = currentX;
	ast.y = currentY;
	
	currentY += 10;
	
	// Vertical line of receiving block
	g.drawLine(currentX, currentY-20, currentX, currentY);
	return currentY;
	
	// Solo Broadcast Sending?
	} else if (ast.isSolo) {
	// Draw a lost message
	String messageName = sig.getName() + "(";
	if(ast.actions != null) {
	messageName += ast.actions.get(0); 
	}
	messageName += ")";
	
	currentY += 10;
	g.setColor(ColorManager.AVATAR_SEND_SIGNAL);
	g.drawLine(currentX+spaceBetweenLifeLines-spaceBroadcast, currentY-1, currentX, currentY-1);
	g.setColor(c);
	GraphicLib.arrowWithLine(g, 1, 0, 10, currentX, currentY, currentX+spaceBetweenLifeLines-spaceBroadcast, currentY, true);
	points.add(new Point(currentX, currentY));
	transactionsOfPoints.add(ast);
	g.fillOval(currentX+spaceBetweenLifeLines-spaceBroadcast, currentY-5, 10, 10);
	
	// Putting the message name
	w = g.getFontMetrics().stringWidth(messageName);
	g.drawString(messageName, currentX+10, currentY-2); 
	
	ast.x = currentX;
	ast.y = currentY;
	
	currentY += 10;
	
	// Vertical line of receiving block
	g.drawLine(currentX, currentY-20, currentX, currentY);
	return currentY;
	
	}
	}
	
	return currentY;
	
	}
	
	private int drawAvatarStartState(Graphics g, int currentX, int currentY, int startX) {
	currentX -= 7;
	g.fillOval(currentX, currentY, 15, 15);
	g.drawLine(currentX, currentY, currentX, currentY+20);
	
	currentY += 20;
	return currentY;
	}
	
	private int drawAvatarStopState(Graphics g, AvatarSimulationTransaction _ast, int currentX, int currentY, int startX) {
	g.drawLine(currentX, currentY, currentX, currentY+spaceStop+3);
	currentX -= (spaceStop/2);
	g.drawLine(currentX, currentY, currentX+spaceStop, currentY+spaceStop);
	g.drawLine(currentX, currentY+spaceStop, currentX+spaceStop, currentY);
	points.add(new Point(currentX+spaceStop, currentY));
	transactionsOfPoints.add(_ast);
	
	currentY += spaceStop + 3;
	return currentY;
	}
	
	public void setNewSize() {
	setPreferredSize(new Dimension(maxX + limit, maxY + limit));
	mustScroll = true;
	revalidate();
	}*/
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		xMouse = e.getX();
		yMouse = e.getY();
		if ((xMouse > minLimit) && (xMouse<maxX) && (yMouse> spaceAtTop) && (yMouse<(maxY))) {
			drawInfo = true;
			repaint();
			return;
		}
		
		if( drawInfo == true) {
			drawInfo = false;
			repaint();
			return;
		}
		
		drawInfo = false;
	}
	
	private void drawInfo(Graphics g) {
		String timeValue = "@" + clockValueMouse/clockDiviser;
		Color c = g.getColor();
		g.setColor(ColorManager.AVATAR_ACTION);
		GraphicLib.dashedLine(g, spaceAtEnd, yMouse, maxX-spaceAtEnd, yMouse);
		g.drawString(timeValue, 10, yMouse+g.getFontMetrics().getHeight()/2);
		g.drawString(timeValue, maxX-spaceAtEnd + 1, yMouse+g.getFontMetrics().getHeight()/2);
		
		int w;
		int x = spaceAtEnd;
		
		for(String name: entityNames) {
			w = g.getFontMetrics().stringWidth(name);
			g.drawString(name, x + ((spaceBetweenLifeLines-w)/2), yMouse - spaceVerticalText);
			x += spaceBetweenLifeLines;
		}
		g.setColor(c);
	}
	
	private void drawIDInfo(Graphics g, int _x, int _y, long _id) {
		Color c = g.getColor();
		g.setColor(ColorManager.AVATAR_EXPIRE_TIMER);
		g.fillOval(_x-3, _y-3, 6, 6);
		g.drawLine(_x, _y, _x+6, _y-6);
		g.drawLine(_x+6, _y-6, _x+12, _y-6);
		g.drawString(""+_id, _x+13, _y-6);
		g.setColor(c);
	}
	
	public BufferedImage performCapture() {
		int w = this.getWidth();
		int h = this.getHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		paintComponent(g);
		g.dispose();
		return image;
	}
	
	public void setFileReference(String _fileReference) {
		fileReference = _fileReference;
		
		mode = FILE_MODE;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public synchronized void refresh() {
		if (mode == FILE_MODE) {
			entityNames.clear();
			transactions.clear();
			transactionsOfPoints.clear();
			points.clear();
			if (t == null) {
				Thread t = new Thread(this);
				t.start();
			}
		}
	}
	
	public void run() {
		//TraceManager.addDev("Reading file");
		
		go = true;
		Thread t;
		
		if (mode == NO_MODE) {
			go = false;
			t = null;
			return;
		}
		
		if (mode == FILE_MODE) {
			// Open the file
			// Read the content of the file
			// Read line by line
			// Upate the graphic regularly
			getExclu();
			jfssdp.setStatus("Reading " + fileReference);
			try{
				// Open the file that is the first 
				// command line parameter
				FileInputStream fstream = new FileInputStream(fileReference);
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				//Read File Line By Line
				while ((strLine = br.readLine()) != null)   {
					// Print the content on the console
					//TraceManager.addDev("Computing transaction:" + strLine);
					addGenericTransaction(strLine);
				}
				//Close the input stream
				in.close();
			} catch (Exception e){//Catch exception if any
				TraceManager.addDev("Reading file Error: " + e.getMessage());
			}
			
			if (jfssdp != null) {
				updateInfoOnTransactions();
			}
			removeExclu();
			
			repaint();
		} 
		
		t = null;
	}
	
	private void updateInfoOnTransactions() {
		if (transactions.size() == 0) {
			jfssdp.setNbOfTransactions(transactions.size(), 0, 0);
		} else {
			jfssdp.setNbOfTransactions(transactions.size(), transactions.get(0).startingTime/clockDiviser, transactions.get(transactions.size()-1).finishTime/clockDiviser);
		}
	}
	
	public void addGenericTransaction(String trans) {
		int index0, index1;
		String tmp, tmp1, tmp2;
		long value;
		int value1, value2;
		
		if (!(trans.startsWith("#"))) {
			return;
		}
		
		
		GenericTransaction gt = new GenericTransaction();
		
		// Adding the ID
		index0 = trans.indexOf(" ");
		if (index0 == -1) {
			return;
		}
		tmp = trans.substring(1, index0);
		try {
			gt.ID = Integer.decode(tmp).intValue(); 
		} catch (Exception e) {
		}
		
		
		//TraceManager.addDev("1");
		
		// Time
		tmp = extract(trans, "time");
		if (tmp == null) {
			return;
		}
		
		//TraceManager.addDev("2 tmp=" + tmp);
		
		try {
			index0 = tmp.indexOf('.');
			if (index0 == -1) {
				TraceManager.addDev("Invalid time value");
				return;
			}
			tmp1 = tmp.substring(0, index0);
			tmp2 = Conversion.removeStartingCharacters(tmp.substring(index0+1, tmp.length()), "0");
			//TraceManager.addDev("2 tmp1=" + tmp1 + " tmp2=" + tmp2);
			value1 = Integer.decode(tmp1).intValue();
			if (tmp2.length() == 0) {
				value2 = 0;
			} else {
				value2 = Integer.decode(tmp2).intValue();
			}
			value = ((long)value1)*1000000000+value2;
			gt.startingTime = value;
			gt.finishTime = value;
		} catch (Exception e) {
			TraceManager.addDev("Exception: " + e.getMessage() + " on transaction " + trans);
			return;
		}
		
		//TraceManager.addDev("3");
		
		// Name of the block
		tmp = extract(trans, "block");
		if (tmp == null) {
			return;
		}
		
		//TraceManager.addDev("4");
		
		addEntityNameIfApplicable(tmp);
		gt.entityName = tmp;
		
		// Type of the transaction
		tmp = extract(trans, "type");
		if (tmp == null) {
			return;
		}
		
		if (tmp.compareTo("state_entering") == 0) {
			gt.type = GenericTransaction.STATE_ENTERING;
		}
		
		if (tmp.compareTo("function_call") == 0) {
			gt.type = GenericTransaction.FUNCTION_CALL;
		}
		
		if (tmp.compareTo("send_synchro") == 0) {
			gt.type = GenericTransaction.SEND_SYNCHRO;
		}
		
		if (tmp.compareTo("synchro") == 0) {
			gt.type = GenericTransaction.SYNCHRO;
		}
		
		if (tmp.compareTo("send_async") == 0) {
			gt.type = GenericTransaction.SEND_ASYNCHRO;
		}
		
		if (tmp.compareTo("receive_async") == 0) {
			gt.type = GenericTransaction.RECEIVE_ASYNCHRO;
		}
		
		// State of the transaction?
		tmp = extract(trans, "state");
		
		if (tmp != null) {
			gt.action = tmp;
		}
		
		// Function of the transaction?
		tmp = extract(trans, "func");
		
		if (tmp != null) {
			gt.action = tmp;
		}
		
		// Parameters of the function
		tmp = extract(trans, "parameters");
		gt.action = gt.action + "(";
		if( tmp != null) {
			if (!(tmp.startsWith("-"))) {
				gt.action = gt.action + tmp;
			}
		} else {
		}
		gt.action = gt.action + ")"; 
		
		// Destination of the transaction?
		tmp = extract(trans, "blockdestination");
		if (tmp != null) {
			gt.otherEntityName = tmp;
			addEntityNameIfApplicable(tmp);
		}
		
		// Channel of the transaction?
		tmp = extract(trans, "channel");
		if (tmp != null) {
			gt.name = tmp;
		}
        
        tmp = extract(trans, "params");
		if (tmp != null) {
			gt.params = tmp;
		}
		
		tmp = extract(trans, "msgid");
		if (tmp != null) {
			gt.messageID = tmp;
		}
		
		transactions.add(gt);
		//TraceManager.addDev("One transactions added");
		
	}
	
	public String extract(String main, String selector) {
		String sel = selector+"=";
		int index = main.indexOf(sel);
		if (index == -1) {
			return null;
		}
		
		String ret = main.substring(index+sel.length(), main.length());
		index = ret.indexOf(' ');
		
		if (index != -1) {
			ret = ret.substring(0, index);
		}
		
		return ret;
	}
	
	public void addEntityNameIfApplicable(String _entityName) {
		for(String name: entityNames) {
			//TraceManager.addDev("Examining name= " + name + " entityName=" + _entityName);
			if (name.compareTo(_entityName) ==0) {
				return;
			}
		}
		
		//TraceManager.addDev("Adding name: " + _entityName);
		entityNames.add(_entityName);
	}
	
	public int getIndexOfEntityName(String _entityName) {
		int cpt = 0;
		for(String name: entityNames) {
			if (name.compareTo(_entityName) ==0) {
				return cpt;
			}
			cpt ++;
		}
		
		return -1;
	}
	
	public void setClockDiviser(long _clockDiviser) {
		clockDiviser = _clockDiviser;
		updateInfoOnTransactions();
	}
}
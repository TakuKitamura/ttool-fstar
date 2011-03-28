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
 * Class AvatarSpecificationSimulationSDPanel
 * Avatar: panel for displaying simulation transactions in the form
 * of a Sequence Diagram
 * Creation: 03/02/2011
 * @version 1.0 03/02/2011
 * @author Ludovic APVRILLE
 * @see
 */


package ui.avatarinteractivesimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

import java.awt.image.*;

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import myutil.*;
import ui.*;

public class AvatarSpecificationSimulationSDPanel extends JPanel implements MouseMotionListener  {
	
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
	private int verticalLink = 10;
	private int lengthAsync = 50;
	
	private AvatarSpecificationSimulation ass;
	
	// Transactions
	private int maxNbOfTransactions = 1000;
	private int drawnTransactions = 1000;
	
	// My scroll panel
	private JScrollPane jsp;
	private boolean mustScroll = true;
	
	// Mouse
	private int xMouse, yMouse;
	private boolean drawInfo = false;
	private long clockValueMouse;
	private Vector<Point> points;
	private Vector<AvatarSimulationTransaction> transactionsOfPoints;
	
	
	
    public AvatarSpecificationSimulationSDPanel(AvatarSpecificationSimulation _ass) {
		ass = _ass;
		points = new Vector<Point>();
		transactionsOfPoints = new Vector<AvatarSimulationTransaction>();
		
		setBackground(Color.WHITE);
		
		setNewSize();
		
		addMouseMotionListener(this);
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
    }
	
	protected void scrollToLowerPosition() {
		if (jsp != null) {
			jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
		}
	}
	
	// Returns the currentY position
	protected int paintTopElements(Graphics g, int currentX, int currentY) {
		String name;
		int w;
		
		Vector<AvatarSimulationBlock> blocks = ass.getSimulationBlocks();
		
		for(AvatarSimulationBlock block: blocks) {
			g.drawLine(currentX + (spaceBetweenLifeLines/4), currentY, currentX + (3*spaceBetweenLifeLines/4), currentY);
			g.drawLine(currentX + (spaceBetweenLifeLines/2), currentY, currentX + (spaceBetweenLifeLines/2), currentY + verticalSpaceUnderBlocks);
			name = block.getBlock().getName();
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
		
		for(AvatarSimulationBlock block: ass.getSimulationBlocks()) {
			w = g.getFontMetrics().stringWidth(block.getBlock().getName());
			if ((w+minSpaceBetweenLifeLines) > spaceBetweenLifeLines) {
				spaceBetweenLifeLines = w+minSpaceBetweenLifeLines;
			}
		}
		
	}
	
	// returns the currentY position
	protected int paintTransactions(Graphics g, int currentX, int currentY) {
		AvatarSimulationTransaction ast;
		long clockValue = -1;
		int index;
		int xOfBlock;
		int newCurrentY = currentY;
		
		// Draw only the last drawnTransactions
		Vector<AvatarSimulationTransaction> allTransactions = ass.getAllTransactions();
		Vector<AvatarSimulationBlock> blocks = ass.getSimulationBlocks();
		
		if (allTransactions.size() > 0) {
			ast = allTransactions.get(Math.max(0, allTransactions.size()-1000));
			clockValue = ast.clockValueWhenFinished-ast.duration;
			clockValueMouse = clockValue;
			g.drawString("@" + clockValue, 10, currentY+g.getFontMetrics().getHeight()/2); 
		}
		
		for(int i=Math.max(allTransactions.size()-drawnTransactions, 0); i<allTransactions.size(); i++) {
			ast = allTransactions.get(i);
			ast.stamp = stamp;
			
			index = blocks.indexOf(ast.asb);
			xOfBlock = currentX + (index * spaceBetweenLifeLines) + spaceBetweenLifeLines/2;
			
			points.clear();
			transactionsOfPoints.clear();
			
			if (ast.executedElement instanceof AvatarState) {
				newCurrentY = drawState(g, ast, (AvatarState)(ast.executedElement), xOfBlock, currentY); 
			} else if (ast.executedElement instanceof AvatarTransition) {
				newCurrentY = drawTransition(g, (AvatarTransition)(ast.executedElement), ast, xOfBlock, currentY); 
			} else if (ast.executedElement instanceof AvatarActionOnSignal) {
				newCurrentY = drawAvatarActionOnSignal(g, (AvatarActionOnSignal)(ast.executedElement), ast, xOfBlock, currentY, currentX); 
			} else if (ast.executedElement instanceof AvatarStopState) {
				newCurrentY = drawAvatarStopState(g, ast, xOfBlock, currentY, currentX); 
			} else if (ast.executedElement instanceof AvatarRandom) {
				newCurrentY = drawRandom(g, (AvatarRandom)(ast.executedElement), ast, xOfBlock, currentY); 
			} 
			
			if ((yMouse>= currentY) && (yMouse <= newCurrentY)) {
				for(int cpt = 0; cpt<points.size(); cpt++) {
					drawIDInfo(g, points.get(cpt).x, points.get(cpt).y, transactionsOfPoints.get(cpt).id);
				}
			}
				
			
			// Draw the line of other blocks
			if (currentY != newCurrentY) {
				xOfBlock = currentX + spaceBetweenLifeLines/2;
				for(AvatarSimulationBlock block: blocks) {
					if (block != ast.asb) {
						g.drawLine(xOfBlock, currentY, xOfBlock, newCurrentY);
					}
					xOfBlock += spaceBetweenLifeLines;
				}
				if (ast.clockValueWhenFinished != clockValue) {
					clockValue = ast.clockValueWhenFinished;
					if (yMouse >= newCurrentY) {
						clockValueMouse = clockValue;
					}
					g.drawString("@" + clockValue, 10, newCurrentY+g.getFontMetrics().getHeight()/2); 
				}
			}
			
			// Update currentY;
			currentY = newCurrentY;
			
		}
		maxY = currentY;
		return currentY;
	}
	
	private int drawState(Graphics g, AvatarSimulationTransaction _ast, AvatarState as, int currentX, int currentY) {
		int w;
		int x, y, width, height;
		
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		currentY += verticalLink;
		
		w = g.getFontMetrics().stringWidth(as.getName());
		Color c = g.getColor();
		Color avat = ColorManager.AVATAR_STATE;
		g.setColor(avat);
		
		x = currentX - w/2 - spaceHorizontalText;
		y = currentY;
		width = w + 2*spaceHorizontalText;
		height = g.getFontMetrics().getHeight() + spaceVerticalText * 2;
		
		g.fillRoundRect(x, y, width, height, 5, 5);
		points.add(new Point(x+width, y));
		transactionsOfPoints.add(_ast);
		g.setColor(c);
		g.drawRoundRect(x, y, width, height, 5, 5);
		
		g.drawString(as.getName(), x + spaceHorizontalText, y+height-2*spaceVerticalText);
		
		currentY += height;
		g.drawLine(currentX, currentY, currentX, currentY+verticalLink);
		return currentY + verticalLink;
	}
	
	private int drawTransition(Graphics g, AvatarTransition at, AvatarSimulationTransaction ast, int currentX, int currentY) {
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
					transactionsOfPoints.add(ast);
					points.add(new Point(currentX, currentY));
					transactionsOfPoints.add(ast.linkedTransaction);
					
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
				GraphicLib.arrowWithLine(g, 1, 1, 10, currentX, currentY, currentX+lengthAsync, currentY, false);
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
	}
	
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
		GraphicLib.dashedLine(g, spaceAtEnd, yMouse, maxX-spaceAtEnd, yMouse);
		g.drawString("@" + clockValueMouse, 10, yMouse+g.getFontMetrics().getHeight()/2);
		/*if (minIdValueMouse == maxIdValueMouse) {
			g.drawString("ID: " + minIdValueMouse, 10, yMouse+(g.getFontMetrics().getHeight()/2)+12);
		} else {
			g.drawString("ID: " + minIdValueMouse + " to " + maxIdValueMouse, 10, yMouse+(g.getFontMetrics().getHeight()/2)+12);
		}*/
		
		String name;
		int w;
		Vector<AvatarSimulationBlock> blocks = ass.getSimulationBlocks();
		int x = spaceAtEnd;
		
		for(AvatarSimulationBlock block: blocks) {
			name = block.getBlock().getName();
			w = g.getFontMetrics().stringWidth(name);
			g.drawString(name, x + ((spaceBetweenLifeLines-w)/2), yMouse - spaceVerticalText);
			x += spaceBetweenLifeLines;
		}
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
	
}
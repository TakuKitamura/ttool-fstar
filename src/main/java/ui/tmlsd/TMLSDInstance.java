/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package ui.tmlsd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

//Abstract class, getType() and editOndoubleClick( JFrame ) are abstract
/**
 * Class TMLSDInstance
 * Instance of a TML sequence diagram
 * Creation: 17/02/2004
 * @version 1.1 10/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public abstract class TMLSDInstance extends TGCWithInternalComponent implements SwallowTGComponent {

	//protected int lineLength = 5;
	//protected int textX, textY;
	protected int spacePt = 10;
	protected int wText = 10, hText = 15;
	protected int increaseSlice = 250;
	protected boolean isActor;
	protected static int heightActor = 30;
	protected static int widthActor = 16;
	protected LinkedList<TAttribute> myAttributes;
	protected String mappedUnit = "";	//The arch unit where the instance is mapped to


	public TMLSDInstance(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		width = 10;
		height = 500;
		//textX = 0;
		//textY = 2;
		minWidth = 10;
		maxWidth = 10;
		minHeight = 250;
		maxHeight = 1500;
		makeTGConnectingPoints();
		//addTGConnectingPointsComment();
		nbInternalTGComponent = 0;
		moveable = true;
		editable = true;
		removable = true;
		userResizable = true;
		value = "Instance name";
		name = "instance";
		myAttributes = new LinkedList<TAttribute> ();
		myImageIcon = IconManager.imgic500;
	}

	public void internalDrawing(Graphics g) {
		if (!tdp.isScaled()) {
			wText  = g.getFontMetrics().stringWidth(name);
			hText = g.getFontMetrics().getHeight();
		}
		g.drawString(name, x - (wText / 2) + width/2, y - 3);
		g.drawLine(x - (wText / 2) + width/2, y-2, x + (wText / 2) + width/2, y-2);
		g.drawLine(x+(width/2), y, x+(width/2), y +height);

		if (isActor) {
			int xtmp = x + (width-widthActor) / 2;
			int ytmp = y-hText;
			// Head
			g.drawOval(xtmp+(widthActor/4)-1, ytmp-heightActor, 2+widthActor/2, 2+widthActor/2);
			//Body
			g.drawLine(xtmp+widthActor/2, ytmp-heightActor/3, xtmp+widthActor/2, ytmp-(2*heightActor)/3);
			//Arms
			g.drawLine(xtmp, ytmp-(heightActor/2) - 2, xtmp+widthActor, ytmp-(heightActor/2) - 2);
			//Left leg
			g.drawLine(xtmp+widthActor, ytmp, xtmp+widthActor/2, ytmp-heightActor/3);
			//right leg
			g.drawLine(xtmp, ytmp, xtmp+widthActor/2, ytmp-heightActor/3);
		}
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}

		if (GraphicLib.isInRectangle(_x, _y, x + (width/2) - (wText/2) , y-hText, wText, hText)) {
			return this;
		}

		if (isActor) {
			if (GraphicLib.isInRectangle(_x, _y, x + (width-widthActor) / 2, y-heightActor-hText, widthActor, heightActor)) {
				return this;
			}
		}
		return null;
	}

	public int getMyCurrentMinX() {
		return Math.min(x + (width/2) - (wText/2), x);

	}

	public int getMyCurrentMaxX() {
		return Math.max(x + (width/2) + (wText/2), x + width);
	}

	public int getMyCurrentMinY() {
		return Math.min(y-hText-heightActor, y);
	}

	public String getInstanceName() {
		return getName();
	}

	public abstract int getType();

	protected void makeTGConnectingPoints() {

		nbConnectingPoint = ( (height - (2 * spacePt) ) / spacePt ) + 1;
		connectingPoint = new TGConnectingPoint[ nbConnectingPoint ];

		int yh = spacePt;

		for(int i = 0; i < nbConnectingPoint; i++, yh += spacePt ) {
			connectingPoint[i] = new TGConnectingPointTMLSD(this, ( width/2), yh, true, true );
		}

	}

	public abstract boolean editOndoubleClick( JFrame frame );

	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return (tgc instanceof TMLSDActionState);
    }

	public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		if (!acceptSwallowedTGComponent(tgc)) {
			return false;
		}


		//
		// Choose its position
		int realY = Math.max(y, getY() + spacePt);
		realY = Math.min(realY, getY() + height + spacePt);
		int realX = tgc.getX();


		// Make it an internal component
		// It's one of my son
		tgc.setFather(this);
		tgc.setDrawingZone(true);



		if ((tgc instanceof TMLSDActionState)) {
			realX = getX()+(width/2);
			//tgc.setCdRectangle((width/2), (width/2), spacePt, height-spacePt-tgc.getHeight());
			tgc.setCd(realX, realY);
		}


		setCDRectangleOfSwallowed(tgc);


		//add it
		addInternalComponent(tgc, 0);

		return true;
	}

	public void removeSwallowedTGComponent(TGComponent tgc) {
		removeInternalComponent(tgc);
	}



	// previous in the sense of with y the closer and before
	public TGComponent getPreviousTGComponent(TGComponent tgcToAnalyse) {
		int close = Integer.MAX_VALUE;
		TGComponent tgc;
		TGComponent tgcfound = null;
		int diff;

		for(int i=0; i<nbInternalTGComponent; i++) {
			tgc = tgcomponent[i];
			if (tgc != tgcToAnalyse) {
				diff = tgcToAnalyse.getY() - tgc.getY();
				if ((diff > 0) && (diff < close)) {

					close = diff;
					tgcfound = tgc;


				}
			}
		}

		return tgcfound;
	}

	public TGComponent getTGComponentActionCloserTo(TGComponent tgc) {
		/* action : message send, message receive, other ? */
		/* timers ? */
		/* now: only message! */

		return ((TMLSDPanel)tdp).messageActionCloserTo(tgc, this);

	}

	public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
		componentMenu.addSeparator();

		JMenuItem decrease = new JMenuItem("Decrease size");
		decrease.addActionListener(menuAL);
		componentMenu.add(decrease);
		decrease.setEnabled(canDecreaseSize());
		JMenuItem increase = new JMenuItem("Increase size");
		increase.addActionListener(menuAL);
		componentMenu.add(increase);
	}

	public boolean eventOnPopup(ActionEvent e) {
		if ((e.getActionCommand().compareTo("Decrease size")) == 0) {
			decreaseSize();
		} else {
			increaseSize();
		}
		return true;
	}

	public void updateMinMaxSize() {
		minHeight = 250;
		int i;

		for(i=0; i<connectingPoint.length ; i++) {
			if (!connectingPoint[i].isFree()) {
				minHeight = Math.max(minHeight, connectingPoint[i].getY() - y);
			}
		} 

		for(i=0; i<nbInternalTGComponent ; i++) {
			minHeight = Math.max(minHeight, tgcomponent[i].getY() + tgcomponent[i].getHeight()- y);
		}
	}

	public boolean canDecreaseSize() {
		if (height <= increaseSlice) {
			return false;
		}

		int newNbConnectingPoint = (((height-increaseSlice) - (2 * spacePt)) / spacePt) + 1;
		int i;

		for(i=newNbConnectingPoint; i<connectingPoint.length ; i++) {
			if (!connectingPoint[i].isFree()) {
				//
				return false;
			}
		} 

		//SwallowedComponents
		for(i=0; i<nbInternalTGComponent ; i++) {
			//
			if ((tgcomponent[i].getY() + tgcomponent[i].getHeight()) > (getY() + getHeight() - increaseSlice)) {
				//
				return false;
			}
		}

		return true;
	}

	public void decreaseSize() {
		//
		//Check whether it is possible or not (swallowed components and tgconnecting points used
		if (!canDecreaseSize()) {
			return;
		}
		// new nb of connectingPoints

		// If ok, do the modification
		height = height - increaseSlice;
		hasBeenResized();
	}

	public void increaseSize() {
		//
		height = height + increaseSlice;
		hasBeenResized();
	}

	public void hasBeenResized(){
		int i;

		TGConnectingPoint [] connectingPointTmp = connectingPoint;
		makeTGConnectingPoints();
		for(i=0; i<Math.min(connectingPointTmp.length, connectingPoint.length) ; i++) {
			connectingPoint[i] = connectingPointTmp[i];
		} 

		// Increase tdp if necessary?

		// Reposition each swallowed component
		for(i=0; i<nbInternalTGComponent ; i++) {
			setCDRectangleOfSwallowed(tgcomponent[i]);
		}
	}

	protected void setCDRectangleOfSwallowed(TGComponent tgc) {


		if ((tgc instanceof TMLSDActionState)) {
			tgc.setCdRectangle((width/2), (width/2), spacePt, height-spacePt-tgc.getHeight());
		}


	}

	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer( "<extraparam>\n" );
		sb.append( "<Mapping mappedOn=\"" );
		sb.append( "" + mappedUnit );
		sb.append( "\" />\n" );
		sb.append( "<Actor data=\"" );
		sb.append( ""+isActor );
		sb.append( "\" />\n" );
		for (TAttribute a: myAttributes) {
			sb.append( "<Attribute access=\"" );
			sb.append( a.getAccess() );
			sb.append( "\" id=\"" );
			sb.append( a.getId() );
			sb.append( "\" value=\"" );
			sb.append( a.getInitialValue() );
			sb.append( "\" type=\"" );
			sb.append( a.getType() );
			sb.append( "\" typeOther=\"" );
			sb.append( a.getTypeOther() );
			sb.append( "\" />\n" );
		}
		sb.append( "</extraparam>\n" );
		return new String(sb);
	}

	@Override
	public void loadExtraParam( NodeList nl, int decX, int decY, int decId ) throws MalformedModelingException{
		//
		try {
			NodeList nli;
			Node n1, n2;
			Element elt;
			int access, type;
			String typeOther;
			String id, valueAtt;

			for( int i = 0; i < nl.getLength(); i++ ) {
				n1 = nl.item(i);
				//
				if( n1.getNodeType() == Node.ELEMENT_NODE ) {
					nli = n1.getChildNodes();

					// Issue #17 copy-paste error on j index
					for( int j = 0; j < nli.getLength(); j++ ) {
						n2 = nli.item(j);
						//
						if( n2.getNodeType() == Node.ELEMENT_NODE ) {
							elt = (Element) n2;
							if( elt.getTagName().equals("Mapping") ) {
								mappedUnit = elt.getAttribute("mappedOn");
							}
							if( elt.getTagName().equals("Actor") ) {
								if( elt.getAttribute("data").compareTo("true") == 0 ) {
									isActor = true;
								}
							}
							//TraceManager.addDev( "I am analyzing " + elt.getTagName() );	
							if( elt.getTagName().equals("Attribute") )	{
								//TraceManager.addDev("Analyzing attribute");
								access = Integer.decode(elt.getAttribute("access")).intValue();
								type = Integer.decode(elt.getAttribute("type")).intValue();
								try {
									typeOther = elt.getAttribute("typeOther");
								}
								catch ( Exception e )	{
									typeOther = "";
								}
								id = elt.getAttribute("id");
								valueAtt = elt.getAttribute("value");
								if( valueAtt.equals("null") )	{
									valueAtt = "";
								}
								if( (TAttribute.isAValidId(id, false, false, false) ) && ( TAttribute.isAValidInitialValue(type, valueAtt)) )	{
									//TraceManager.addDev("Adding attribute " + id + " typeOther=" + typeOther);
									TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
									myAttributes.add (ta);
								}
							}
						}
					}
				}
			}
		}
		catch ( Exception e ) {
			throw new MalformedModelingException();
		}
	}

	public void setActor(boolean b) {
		isActor = b;
	}

	public int getNumberInternalComponents()	{
		return nbInternalTGComponent;
	}

	public TGComponent[] getInternalComponents()	{
		return tgcomponent;
	}

	public TGConnectingPoint[] getConnectingPoint()	{
		return connectingPoint;
	}

	public LinkedList<TAttribute> getAttributes()	{
		return myAttributes;
	}
}

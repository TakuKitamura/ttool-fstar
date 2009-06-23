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
 * Class TMLCCompositePort
 * Composite port. To be used in TML component task diagrams
 * Creation: 11/03/2008
 * @version 1.0 11/03/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class TMLCCompositePort extends TGCScalableWithInternalComponent implements SwallowedTGComponent, WithAttributes {
	private Color myColor, portColor;
	private int orientation;
	private int oldx, oldy;
	private int halfwidth = 13;
	private int currentOrientation = GraphicLib.NORTH;
	protected int decPoint = 3;
	
	protected TMLCPrimitivePort inp, outp;
	protected int inpIndex, outpIndex; 
	protected boolean conflict = false;
	protected String conflictMessage;
	
    
    public TMLCCompositePort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(2*halfwidth, 2*halfwidth);
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 10;
        connectingPoint = new TGConnectingPoint[10];
		int i;
		for (i=0; i<5; i++) {
			connectingPoint[i] = new TMLCPortConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
		}
		for(i=5; i<10; i++) {
			connectingPoint[i] = new TMLCPortConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
		}
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = false;
        removable = true;
        userResizable = false;
        
		value = "";
		name = "Composite port";
		
		//insides = new ArrayList<TMLCPrimitivePort>();
		//outsides = new ArrayList<TMLCPrimitivePort>();
		
        myImageIcon = IconManager.imgic1204;
    }
    
    public void internalDrawing(Graphics g) {
		if ((x != oldx) | (oldy != y)) {
			// Component has moved!
			manageMove();
			oldx = x;
			oldy = y;
		}
		
		if (rescaled) {
			rescaled = false;	
		}
		
		calculatePortColor();
		
		// Zoom is assumed to be computed
		Color c = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(c);
		}
		
		// Draw arrow showing the connection if necessary
		//if (outp != null ){
		//	System.out.println("non null outp CurrentOrientation=" + currentOrientation);
		//}
		if ((!conflict) && (outp != null || inp != null)) {
			int wayTo = currentOrientation;
			if (inp != null) {
				if (inpIndex > 4) {
					wayTo = (wayTo + 2 )% 4;
				} 
			} else {
				if (outpIndex < 5) {
					wayTo = (wayTo + 2 )% 4;
				}
			}
			
			int []px = new int[3];
			int []py = new int[3];
			switch(wayTo) {
				case GraphicLib.NORTH:
					px[0] = x + decPoint;
					px[1] = x + width - decPoint;
					px[2] = x + width/2;
					py[0] = y + height - decPoint;
					py[1] = y + height - decPoint;
					py[2] = y + decPoint;
					break;
				case GraphicLib.SOUTH:
					px[0] = x + decPoint;
					px[1] = x + width - decPoint;
					px[2] = x + width/2;
					py[0] = y + decPoint;
					py[1] = y + decPoint;
					py[2] = y + height - decPoint;
					break;
				case GraphicLib.WEST:
					px[0] = x + width - decPoint;
					px[1] = x + width - decPoint;
					px[2] = x + decPoint;
					py[0] = y + decPoint;
					py[1] = y + height - decPoint;
					py[2] = y + height/2;
					break;
				case GraphicLib.EAST:
				default:
					px[0] = x + decPoint;
					px[1] = x + decPoint;
					px[2] = x + width - decPoint;
					py[0] = y + decPoint;
					py[1] = y + height - decPoint;
					py[2] = y + height/2;
			}
			g.drawPolygon(px, py, 3);
			g.fillPolygon(px, py, 3);
		}
    }
	
	public void manageMove() {
		if (father != null) {
			Point p = GraphicLib.putPointOnRectangle(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
			
			x = p.x - width/2;
			y = p.y - height/2;
			
			setMoveCd(x, y);
			
			int orientation = GraphicLib.getCloserOrientation(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
			if (orientation != currentOrientation) {
				setOrientation(orientation);
			}
		}
	}
	
	// TGConnecting points ..
	public void setOrientation(int orientation) {
		currentOrientation = orientation;
		double w0, h0,w1, h1; 
		
		switch(orientation) {
			case GraphicLib.NORTH:
				w0 = 0.5;
				h0 = 0.0;
				w1 = 0.5;
				h1 = 1.0;
				break;
			case GraphicLib.WEST:
				w0 = 0.0;
				h0 = 0.5;
				w1 = 1.0;
				h1 = 0.5;
				break;
			case GraphicLib.SOUTH:
				w1 = 0.5;
				h1 = 0.0;
				w0 = 0.5;
				h0 = 1.0;
				break;
			case GraphicLib.EAST:
			default:
				w1 = 0.0;
				h1 = 0.5;
				w0 = 1.0;
				h0 = 0.5;
		}
		
		for (int i=0; i<5; i++) {
			((TMLCPortConnectingPoint)(connectingPoint[i])).setW(w0);
			((TMLCPortConnectingPoint)(connectingPoint[i])).setH(h0);
			((TMLCPortConnectingPoint)(connectingPoint[i+5])).setW(w1);
			((TMLCPortConnectingPoint)(connectingPoint[i+5])).setH(h1);
		}
	}
    
    public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    
    public int getType() {
        return TGComponentManager.TMLCTD_CPORT;
    }
	
	public void wasSwallowed() {
		myColor = null;
	}
	
	public void wasUnswallowed() {
		myColor = null;
		setFather(null);
		TDiagramPanel tdp = getTDiagramPanel();
		setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
			
	}
	
	public void resizeWithFather() {
		//System.out.println("Resize port with father");
        if ((father != null) && (father instanceof TMLCCompositeComponent)) {
			// Too large to fit in the father? -> resize it!
			//resizeToFatherSize();
			
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
			oldx = -1;
			oldy = -1;
        }
    }
	
	public void purge() {
		//System.out.println("purging port id=" + getId());
		inp = null;
		outp = null;
		conflict = false;
		inpIndex = -1;
		outpIndex = -1;
		myColor = null;
		calculatePortColor();
	}
	
	public void calculatePortColor() {
		if (conflict) {
			myColor = Color.red;
		} else {
			
			TMLCPrimitivePort port = inp;
			if (port == null) {
				port = outp;
				//System.out.println("Outp =" + outp + " id=" + getId());
			}
			if (port == null) {
				portColor = null;
				if (myColor == null) {
					myColor = new Color(251, 252, 155- (getMyDepth() * 10));
				}
			} else {
				int typep = port.getPortType();
				if (typep == 0) {
					myColor = ColorManager.TML_PORT_CHANNEL;
				} else if (typep == 1) {
					myColor = ColorManager.TML_PORT_EVENT;
				} else {
					myColor = ColorManager.TML_PORT_REQUEST;
				}
			}
		}
		portColor = myColor;
	}
	
	public void setInPort(TMLCPrimitivePort _inp) {
		inp = _inp;
		calculatePortColor();
	}
	
	public void setOutPort(TMLCPrimitivePort _outp) {
		outp = _outp;
		calculatePortColor();
		//System.out.println("outp is set outp=" + outp + "id=" + +getId());
	}
	
	public TMLCPrimitivePort getInPort() {
		return inp;
	}
	
	public TMLCPrimitivePort getOutPort() {
		return outp;
	}
	
	public int getInpIndex() {
		return inpIndex;
	}
	
	public int getOutpIndex() {
		return outpIndex;
	}
	
	public void setInpIndex(int _inpIndex) {
		inpIndex = _inpIndex;
	}
	
	public void setOutpIndex(int _outpIndex) {
		outpIndex = _outpIndex;
	}
	
	public boolean getConflict() {
		return conflict;
	}
	
	public void setConflict(boolean _conflict, String _msg) {
		conflict = _conflict;
		myColor = null;
		conflictMessage = _msg;
		calculatePortColor();
	}
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_PORT_TMLC;
     }
	 
	 public String getAttributes() {
		 if (conflict) {
			 return conflictMessage;
		 }
		 
		 String s = "";
		 if (inp != null) {
			 s = s + inp.getAttributes();
			 if (outp != null) {
				 s = s + "\n";
			 }
		 }
		 
		 if (outp != null) {
			  s = s + outp.getAttributes();
		 }
		 
		 return s;
	 }
	 
	 public void myActionWhenRemoved() {
		tdp = null;
		TGComponent tgc =  getFather();
		if (tgc instanceof TMLCCompositeComponent) {
			((TMLCCompositeComponent)(tgc)).portRemoved();
		}
		father = null;
	}
	
	public Color getPortColor() {
		return portColor;
	}
	 
	 
}

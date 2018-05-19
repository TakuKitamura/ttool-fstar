/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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

package ui.syscams;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogSysCAMSPortConverter;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Class SysCAMSCompositePort
 * Composite port. To be used in SystemC-AMS diagrams
 * Creation: 29/04/2018
 * @version 1.0 29/04/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSCompositePort extends SysCAMSChannelFacility implements SwallowedTGComponent, WithAttributes {
	private int oldx, oldy;
	private int halfwidth = 13;
	private int currentOrientation = GraphicLib.NORTH;
	protected int decPoint = 3;
	private ImageIcon portImageIconW, portImageIconE, portImageIconN, portImageIconS;
	public String commName;
    
	// Attributes
    public String portName;
    public int period;
    public int rate;
    public int delay;
    public String type;
    public String origin;
	
    public SysCAMSCompositePort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(2*halfwidth, 2*halfwidth);
		
        minWidth = 10;
        minHeight = 10;
        
        nbConnectingPoint = 10;
        connectingPoint = new TGConnectingPoint[10];
		int i;
		for (i=0; i<5; i++) {
			connectingPoint[i] = new SysCAMSPortConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
		}
		for(i=5; i<10; i++) {
			connectingPoint[i] = new SysCAMSPortConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
		}
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
		value = "";
		name = "Composite port";
		commName = "port";
		
        myImageIcon = IconManager.imgic1204;
        portImageIconW = IconManager.imgic8002; 
        portImageIconE = IconManager.imgic8003; 
        portImageIconN = IconManager.imgic8004; 
        portImageIconS = IconManager.imgic8005; 
        
        // Initialization of port attributes
    	((SysCAMSPortConverter) this).setPeriod(0);
    	((SysCAMSPortConverter) this).setDelay(0);
    	((SysCAMSPortConverter) this).setRate(0);
    	((SysCAMSPortConverter) this).setConvType("");
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
		g.setColor(c);
		
		TGComponent tgc = getFather();
		
        if ((tgc != null) && (tgc instanceof SysCAMSCompositeComponent)) {
        	if (tgc instanceof SysCAMSCompositeComponent && this instanceof SysCAMSCompositePort) {
        		switch(currentOrientation) {
                case GraphicLib.NORTH:
            		g.drawRect(x-1+width/2-portImageIconN.getIconWidth()/2, y-1+height/2-portImageIconN.getIconHeight()/2, portImageIconN.getIconWidth()+2, portImageIconN.getIconHeight()+2);
             		g.drawImage(portImageIconN.getImage(), x+width/2-portImageIconN.getIconWidth()/2, y+height/2-portImageIconN.getIconHeight()/2, null);
                	break;
                case GraphicLib.SOUTH:
            		g.drawRect(x+width/2-portImageIconS.getIconWidth()/2, y+height/2-portImageIconS.getIconHeight()/2, portImageIconS.getIconWidth(), portImageIconS.getIconHeight());
             		g.drawImage(portImageIconS.getImage(), x+width/2-portImageIconS.getIconWidth()/2, y+height/2-portImageIconS.getIconHeight()/2, null);
                	break;
                case GraphicLib.WEST:
            		g.drawRect(x+width/2-portImageIconW.getIconWidth()/2, y+height/2-portImageIconW.getIconHeight()/2, portImageIconW.getIconWidth(), portImageIconW.getIconHeight());
             		g.drawImage(portImageIconW.getImage(), x+width/2-portImageIconW.getIconWidth()/2, y+height/2-portImageIconW.getIconHeight()/2, null);
                	break;
                case GraphicLib.EAST:
                default:
            		g.drawRect(x+width/2-portImageIconE.getIconWidth()/2, y+height/2-portImageIconE.getIconHeight()/2, portImageIconE.getIconWidth(), portImageIconE.getIconHeight());
             		g.drawImage(portImageIconE.getImage(), x+width/2-portImageIconE.getIconWidth()/2, y+height/2-portImageIconE.getIconHeight()/2, null);
                }
        	}
        }
		
        int ft = 10;
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
        	ft = ((SysCAMSBlockTDF)tgc).getCurrentFontSize();
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockDE)) {
            ft = ((SysCAMSBlockDE)tgc).getCurrentFontSize();
        }
        int w;
        Font f = g.getFont();
        Font fold = f;

        int si = Math.min(8, (int)((float)ft - 2));
        f = f.deriveFont((float)si);
        g.setFont(f);
        w = g.getFontMetrics().stringWidth(commName);
        if (w < ((int)(width * 1.5))) {
            g.drawString(commName, x, y-1);
        }
        
        g.setFont(fold);
        
		// Draw arrow showing the connection if necessary
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
				case GraphicLib.SOUTH:
					px[0] = x+width/2;
					px[1] = x +width/2;
					py[0] = y;
					py[1] = y + height;
					break;
				case GraphicLib.EAST:
				case GraphicLib.WEST:
				default:
					px[0] = x;
					px[1] = x +width;
					py[0] = y+height/2;
					py[1] = y + height/2;
					break;
			}
			g.drawPolygon(px, py, 2);
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
			((SysCAMSPortConnectingPoint)(connectingPoint[i])).setW(w0);
			((SysCAMSPortConnectingPoint)(connectingPoint[i])).setH(h0);
			((SysCAMSPortConnectingPoint)(connectingPoint[i+5])).setW(w1);
			((SysCAMSPortConnectingPoint)(connectingPoint[i+5])).setH(h1);
		}
	}
    
    public int getType() {
        return TGComponentManager.CAMS_PORT_CONVERTER;
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
        if ((father != null) && (father instanceof SysCAMSCompositeComponent)) {
			// Too large to fit in the father? -> resize it!
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
			oldx = -1;
			oldy = -1;
        }
    }
	
	public boolean editOndoubleClick(JFrame frame) {
		JDialogSysCAMSPortConverter jtdf = new JDialogSysCAMSPortConverter((SysCAMSPortConverter) this);
		jtdf.setVisible(true);
    	
        ((SysCAMSComponentTaskDiagramPanel)tdp).updatePorts();
        return true;
    }
	
	public String getPortName() {
        return commName;
    }
	
	public void setPortName(String s) {
		commName = s;
	}
	
	public void purge() {
		inp = null;
		outp = null;
		conflict = false;
		inpIndex = -1;
		outpIndex = -1;
		myColor = null;
		calculatePortColor();
	}
	
	public void myActionWhenRemoved() {
		tdp = null;
		TGComponent tgc =  getFather();
		if (tgc instanceof SysCAMSCompositeComponent) {
			((SysCAMSCompositeComponent)(tgc)).portRemoved();
		}
		father = null;
	}
}
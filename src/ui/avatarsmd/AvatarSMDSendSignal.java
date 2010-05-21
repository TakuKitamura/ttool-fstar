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
 * Class AvatarSMDSendSignal
 * Action of sending a signal
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarsmd;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class AvatarSMDSendSignal extends AvatarSMDBasicComponent implements CheckableAccessibility, BasicErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
    
	
	protected int stateOfError = 0; // Not yet checked
    
    public AvatarSMDSendSignal(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, lineLength, false, true, 0.5, 1.0);
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "Send signal";
		value = "sig()";
        //makeValue();
        
        myImageIcon = IconManager.imgic904;
    }
    
    public void internalDrawing(Graphics g) {
		
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;            //updateConnectingPoints();
        }
		
		
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.AVATAR_SEND_SIGNAL);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			// Making the polygon
			int [] px1 = {x, x+width-linebreak, x+width, x+width-linebreak, x};
			int [] py1 = {y, y, y+(height/2), y+height, y+height};
			g.fillPolygon(px1, py1, 5);
			g.setColor(c);
		}
		
        //g.drawRoundRect(x, y, width, height, arc, arc);
		Color c = g.getColor();
		//System.out.println("Color=" + c);
		
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
		
		/*if (g.getColor().equals(ColorManager.NORMAL_0)) {
			g.setColor(ColorManager.TML_PORT_EVENT);
		}*/
		
		int x1 = x + 1;
		int y1 = y + 1;
		int height1 = height;
		int width1 = width;
		g.setColor(ColorManager.AVATAR_SEND_SIGNAL);
		g.drawLine(x1, y1, x1+width1-linebreak, y1);
        g.drawLine(x1, y1+height1, x1+width1-linebreak, y1+height1);
        g.drawLine(x1, y1, x1, y1+height1);
        g.drawLine(x1+width1-linebreak, y1, x1+width1, y1+height1/2);
        g.drawLine(x1+width1-linebreak, y1+height1, x1+width1, y1+height1/2);
		g.setColor(c);
		
		g.drawLine(x, y, x+width-linebreak, y);
        g.drawLine(x, y+height, x+width-linebreak, y+height);
        g.drawLine(x, y, x, y+height);
        g.drawLine(x+width-linebreak, y, x+width, y+height/2);
        g.drawLine(x+width-linebreak, y+height, x+width, y+height/2);
		
		   
        //g.drawString("sig()", x+(width-w) / 2, y);
        g.drawString(value, x + (width - w) / 2 , y + textY);
		
		
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
    public void makeValue() {
        /*boolean first = true;
        value = eventName + "(";
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                if (!first) {
                    value += ", " + params[i];
                } else {
                    first = false;
                    value += params[i];
                }
                
            }
        }
        value += ")";*/
        
    }
    
    public String getSignalName() {
        if (value == null) {
			return null;
		}
		
		if (value.length() == 0) {
			return "";
		}
		
		int index = value.indexOf('(');
		if (index == -1) {
			return value;
		}
		return value.substring(0, index).trim();
    }
    
    /*public String getParamValue(int i) {
        return params[i];
    }
    
    public int nbOfParams() {
        return nParam;
    }*/
	
	// Return -1 in case of error
	public int getNbOfValues() {
		return AvatarSignal.getNbOfValues(value);
	}
	
	// Return null in case of error
	public String getValue(int _index) {
		return AvatarSignal.getValue(value, _index);
	}
	
    
    public boolean editOndoubleClick(JFrame frame) {
		Vector signals = tdp.getMGUI().getAllSignals();
		TraceManager.addDev("Nb of signals:" + signals.size());
		
		JDialogAvatarSignal jdas = new JDialogAvatarSignal(frame, "Setting receiving signal",  value, signals, true);
		jdas.setSize(350, 300);
        GraphicLib.centerOnParent(jdas);
        jdas.show(); // blocked until dialog has been closed
		
		if (jdas.hasBeenCancelled()) {
			return false;
		}
		
		String val = jdas.getSignal();
		
		if (val.indexOf('(') == -1) {
			val += "()";
		}
		
		// valid signal?
		if (AvatarSignal.isAValidUseSignal(val)) {
			value = val;
			return true;
		}
		
		JOptionPane.showMessageDialog(frame,
			"Could not change the setting of the signal: invalid declaration",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
		return false;
         
    }
    
    /*protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data eventName=\"");
        sb.append(getEventName());
        sb.append("\" nbOfParams=\"");
        sb.append(nbOfParams());
        sb.append("\" />\n");
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                sb.append("<Param index=\"");
                sb.append(i);
                sb.append("\" value=\"");
                sb.append(params[i]);
                sb.append("\" />\n");
            }
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro *** " + getId());
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;
            
            //System.out.println("Loading Synchronization gates");
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                eventName = elt.getAttribute("eventName");
                                nParam = Integer.decode(elt.getAttribute("nbOfParams")).intValue();
                            }   
                             if (elt.getTagName().equals("Param")) {
                                s = elt.getAttribute("value");
                                k = Integer.decode(elt.getAttribute("index")).intValue();
                                params[k] = s;
                             }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        makeValue();
    }*/
    

    public int getType() {
        return TGComponentManager.AVATARSMD_SEND_SIGNAL;
    }
    
     public int getDefaultConnector() {
      return TGComponentManager.AVATARSMD_CONNECTOR;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}

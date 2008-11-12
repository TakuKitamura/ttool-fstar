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
* Class TGCTimeDelay
* Internal component that is a time interval / or a single time with a unit
* Creation: 10/11/2008
* @version 1.0 10/11/2008
* @author Ludovic APVRILLE
* @see
*/

package ui;

import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;
import org.w3c.dom.*;

import myutil.*;
import ui.window.*;

public class TGCTimeDelay extends TGCWithoutInternalComponent{
    protected int minWidth = 10;
    private String minDelay = "";
    private String maxDelay = "";
	private String unit = "ns"; // can be "ns" or "us" or "ms" or "s";
	private boolean hasMaxDelay;
    
    public TGCTimeDelay(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbConnectingPoint = 0;
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = false;
        
        name = "";
        value = "interval value";
        
        myImageIcon = IconManager.imgic302;
    }
    
    public void internalDrawing(Graphics g) {
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 1);
        if ((w1 != width) && (!tdp.isScaled())) {
			//System.out.println("x=" + x + " y=" + y + " width=" + width + " height=" + height);
            //setCd(x + width/2 - w1/2, g.getFontMetrics().getHeight());
            width = w1;
            height = g.getFontMetrics().getHeight();
            //updateConnectingPoints();
        }
       
        if (value.equals("")) {
            g.drawString("unspecified value", x, y);
            if (!tdp.isScaled())
				width = g.getFontMetrics().stringWidth("unspecified value");
        } else {
			 g.drawString(value, x, y);
		}
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(width, minWidth), height)) {
            return this;
        }
        return null;
    }
    
    public void makeValue() {
		if (hasMaxDelay) {
			if (minDelay.equals("") && maxDelay.equals("")) {
				value = "";
				return;
			}
			if ((minDelay.equals("")) && (!maxDelay.equals(""))){
				minDelay = "0";
			}
			
			value = "[" + minDelay + ", " + maxDelay + "] " + getUnit();
		} else {
			if (minDelay.equals("")) {
				value = "";
				return;
			}
			
			value = minDelay + " " + getUnit();
		}
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldMin = getMinDelay();
        String oldMax = getMaxDelay();
		String oldUnit = getUnit();
        String[] array = new String[3];
		String ind;
		if (hasMaxDelay) {
			ind = "min";
		} else {
			ind = "value";
		}
        array[0] = getMinDelay(); array[1] = getMaxDelay(); array[2] = getUnit();
        
        JDialogTimeIntervalUnit jdti = new JDialogTimeIntervalUnit(frame, array, hasMaxDelay, "Setting time parameters", ind, "max");
        jdti.setSize(350, 250);
        GraphicLib.centerOnParent(jdti);
        jdti.show(); // blocked until dialog has been closed
        
        minDelay = array[0]; maxDelay = array[1]; unit = array[2];
        
		if (hasMaxDelay) {
			if ((minDelay != null) && (maxDelay != null) && (unit != null) && ((!minDelay.equals(oldMin)) || (!maxDelay.equals(oldMax) || (!unit.equals(oldUnit))))){
				makeValue();
				return true;
			}
		} else {
			if ((minDelay != null) && (unit != null) && ((!minDelay.equals(oldMin)) || (!unit.equals(oldUnit)))){
				makeValue();
				return true;
			}
		}
		
        minDelay = oldMin;
        maxDelay = oldMax;
		unit = oldUnit;
        return false;
    }
    
    public String getMinDelay() {
        return minDelay;
    }
    
    public String getMaxDelay() {
        return maxDelay;
    }
	
	public void setHasMaxValue(boolean b) {
		hasMaxDelay = b;
	}
    
    public void setMinDelay(String del) {
        minDelay = del;
        makeValue();
    }
    
    public void setMaxDelay(String del) {
        maxDelay = del;
        makeValue();
    }
	
	public void setUnit(String _unit) {
		unit = _unit;
	}
	
	public String getUnit() {
		return unit;
	}
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<TimeDelay minDelay=\"");
        sb.append(getMinDelay());
        sb.append("\" maxDelay=\"");
        sb.append(getMaxDelay());
		sb.append("\" hasMaxDelay=\"");
		sb.append(hasMaxDelay);
		sb.append("\" unit=\"");
		sb.append(getUnit());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            
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
                            if (elt.getTagName().equals("TimeDelay")) {
                                minDelay = elt.getAttribute("minDelay");
                                maxDelay = elt.getAttribute("maxDelay");
								if (elt.getAttribute("hasMaxDelay") != null) {
									if (elt.getAttribute("hasMaxDelay").compareTo("true") == 0) {
										hasMaxDelay = true;
									} else {
										hasMaxDelay = false;
									}
								}
								unit = elt.getAttribute("unit");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        makeValue();
    }
}
/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TMLADRandom
 * Random operator of a TML activity diagram
 * Creation: 10/06/2008
 * @version 1.0 10/06/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlad;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class TMLADRandom extends TGCWithoutInternalComponent implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
	protected String valueRandom = "";
	protected String variable;
	protected String minValue;
	protected String maxValue;
	protected int functionId;
	
	protected int stateOfError = 0; // Not yet checked
    
    public TMLADRandom(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, lineLength, false, true, 0.5, 1.0); // after lopp
        
        moveable = true;
        editable = true;
        removable = true;
        
		variable = "x";
		minValue = "0";
		maxValue = "10";
		functionId = 0;
        
        myImageIcon = IconManager.imgic912;
    }
	
	public void makeValue() {
		valueRandom = variable + " = RANDOM" + functionId + "(" + minValue + ", " + maxValue + ")";
	}
    
    public void internalDrawing(Graphics g) {
		
		if (valueRandom.length() == 0) {
			makeValue();
		}
		
        int w  = g.getFontMetrics().stringWidth(valueRandom);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
		
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.RANDOM);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRoundRect(x, y, width, height, arc, arc);
			g.setColor(c);
		}
		
        g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        //g.drawLine(x+width, y+height/2, x+width +lineLength, y+height/2);
        
        g.drawString(valueRandom, x + (width - w) / 2 , y + textY);
    }
	
	public boolean editOndoubleClick(JFrame frame) {
        boolean error = false;
		String errors = "";
		int tmp;
		String tmpName;
        
		JDialogTMLADRandom dialog = new JDialogTMLADRandom(frame, "Setting RANDOM attributes", this);
		dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getVariable().length() != 0) {
			tmpName = dialog.getVariable();
			tmpName = tmpName.trim();
			 if (!TAttribute.isAValidId(tmpName, false, false)) {
                error = true;
				errors += "Variable  ";
			 } else {
				 variable = tmpName;
			 }
		}
		
		if (dialog.getMinValue().length() != 0) {
			minValue = dialog.getMinValue();
		} else {
			error = true;
			errors += "Min value  ";
		}
		
		if (dialog.getMaxValue().length() != 0) {
			maxValue = dialog.getMaxValue();
		} else {
			error = true;
			errors += "Max value  ";
		}
		
		functionId = dialog.getFunctionId();
		
        if (error) {
			JOptionPane.showMessageDialog(frame,
                "Invalid value for the following attributes: " + errors,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
		}
		
		makeValue();
		
        return true;
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
		
		if ((int)(Line2D.ptSegDistSq(x+width, y+height/2, x+width +lineLength, y+height/2, _x, _y)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
	
	public String getVariable() {
		return variable;
	}
	
	public String getMinValue() {
		return minValue;
	}
	
	public String getMaxValue() {
		return maxValue;
	}
	
	public int getFunctionId() {
		return functionId;
	}
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data variable=\"");
        sb.append(getVariable());
        sb.append("\" minValue=\"");
        sb.append(getMinValue());
		sb.append("\" maxValue=\"");
        sb.append(getMaxValue());
		sb.append("\" functionId=\"");
        sb.append(getFunctionId());
        sb.append("\" />\n");
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
                                variable = elt.getAttribute("variable");
                                minValue = elt.getAttribute("minValue");
								maxValue = elt.getAttribute("maxValue");
								s = elt.getAttribute("functionId");
								if (s != null) {
									try {
										functionId = new Integer(s).intValue();
									} catch (Exception e){
									}
								}
                                //System.out.println("eventName=" +eventName + " variable=" + result);
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
    
    
    public int getType() {
        return TGComponentManager.TMLAD_RANDOM;
    }
    
    public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_TMLAD;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
	
    
}

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
 * Class EBRDDERB
 * Event Reaction Block an EBRDD
 * Creation: 09/09/2009
 * @version 1.0 09/09/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ebrdd;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import myutil.*;
import ui.*;
import ui.window.*;

import org.w3c.dom.*;

public class EBRDDERB extends TGCOneLineText implements SwallowedTGComponent {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int diffY = 20;
	
	protected int wid;
	
	protected int stateOfError = 0; // Not yet checked
	
	protected String evt = "evt1", condition = "x==0", action = "x=x+1"; 
    
    public EBRDDERB(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 60;
        height = 60;
        minWidth = 30;
        
        nbConnectingPoint = 1;
        connectingPoint = new TGConnectingPoint[1];
        connectingPoint[0] = new TGConnectingPointEBRDDERC(this, 0, -lineLength, true, false, 0.5, 0.0);
		
        moveable = true;
        editable = true;
        removable = true;
        
        name = "Event Reaction Block";
		
		makeValue();
        
        myImageIcon = IconManager.imgic1056;
    }
	
	public void makeValue() {
		 setValue("evt:" + evt + " / cond:" + condition + " / action:" + action);
	}
    
    public void internalDrawing(Graphics g) {
		if (wid != width) {
			resizeWithFather();
		}
		
		String val1 = "evt: " + evt;
		String val2 = "cond: " + condition;
		String val3 = "action: " + action;
		
        int w11  = g.getFontMetrics().stringWidth(val1);
		int w22 = g.getFontMetrics().stringWidth(val2);
		int w33 = g.getFontMetrics().stringWidth(val3);
		
		int w1 = Math.max(w11, w22);
		w1 = Math.max(w1, w33);
        int w = Math.max(minWidth, w1 + (2 * textX));
        if ((w != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w/2, y);
            width = w;
            //updateConnectingPoints();
        }
		
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.ATTRIBUTE_BOX_ACTION);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRect(x, y, width, height);
			g.setColor(c);
		}
		
        g.drawRect(x, y, width, height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        //g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        g.drawString(val1, x + textX - 2, y + textY);
		g.drawLine(x, y + textY + 2, x + width, y + textY + 2);
		g.drawString(val2, x + textX - 2, y + textY + diffY);
		g.drawString(val3, x + textX - 2, y + textY + (2 * diffY));
		
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y, _x, _y)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
	
	public boolean editOndoubleClick(JFrame frame) {
		boolean error = false;
		String errors = "";
		String val;
        
		JDialogERB dialog = new JDialogERB(frame, this);
		dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		val = dialog.getEvent().trim();
		if (val.length() == 0) {
			error = true;
			errors += "event ";
		}
		
		val = dialog.getCondition().trim();
		if (val.length() == 0) {
			error = true;
			errors += "condition ";
		}
		
		val = dialog.getAction().trim();
		if (val.length() == 0) {
			error = true;
			errors += "action ";
		}
		
		
		if (error) {
			JOptionPane.showMessageDialog(frame,
                "Invalid value for the following attributes: " + errors,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
		}
		
		evt = dialog.getEvent().trim();
		condition = dialog.getCondition().trim();
		action = dialog.getAction().trim();
		makeValue();
		
		return true;
	}
    
    public int getType() {
        return TGComponentManager.EBRDD_ERB;
    }
    
    public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_EBRDD_ERC;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
	
	public void resizeWithFather() {
        if ((father != null) && (father instanceof EBRDDERC)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<elements evt=\"" + evt + "\" ");
		sb.append("cond=\"" + condition + "\" ");
		sb.append("action=\"" + action + "\" ");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
    
            String val = null;
            
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
                            if (elt.getTagName().equals("elements")) {
                                val = elt.getAttribute("evt");
								//System.out.println("val=" + val);
								if (val != null) {
									evt = val;
								}
								val = elt.getAttribute("cond");
								//System.out.println("val=" + val);
								if (val != null) {
									condition = val;
								}
								val = elt.getAttribute("action");
								//System.out.println("val=" + val);
								if (val != null) {
									action = val;
								}
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
	
	public String getEvent() {
		return evt;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public String getAction() {
		return action;
	}
    
    
}

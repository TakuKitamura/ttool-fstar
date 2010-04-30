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
* Class AvatarPDToggle
* component with a toggle part
* Creation: 30/04/2010
* @version 1.0 30/04/2010
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarpd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public abstract class  AvatarPDToggle extends TGCScalableWithInternalComponent {
	
    protected String oldValue = "";
	protected String oldToggle = "";
	protected String toggle;
	protected final String TOGGLE = "toggle";
	protected int toggleHeight = 35;
	protected int toggleDecY = 3;
	protected int textX = 2;
	protected float decXToggle = 0;
	protected boolean valueChanged = false;
	
	
    
    public AvatarPDToggle(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		multieditable = true;
		toggle = "";
		valueChanged = true;
    }
    
	
	
	public String getFullToggle() {
		String s = TOGGLE;
		if ((toggle != null) && (toggle.length() > 0)) {
			s += " to: " + toggle;
		}
		return s;
	}
    
	public void setValueWidth(Graphics g) {
		
		valueChanged = false;
		
        int w  = g.getFontMetrics().stringWidth(value);
		int w2 = g.getFontMetrics().stringWidth(getFullToggle());
		w2 = Math.max(w, (int)((w2 + 4)/(1-decXToggle)));
		int w1 = Math.max((int)(minWidth*tdp.getZoom()), w2 + 2 * textX );
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }
    
    public void resizeWithFather() {
        if ((father != null) && (father instanceof AvatarPDBlock)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
	
	public boolean editToggle(JFrame _frame) {
		oldToggle = toggle;
		String s = (String)JOptionPane.showInputDialog(_frame, "Toggle to:",
			"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
			null,
			getToggle());
		if (s == null) {
			s = oldToggle;
		}
		setToggle(s);
		if (toggle.compareTo(oldToggle) !=0) {
			valueChanged = true;
			return true;
		}
		return false;
	}
    
	
	
	public String getToggle() {
		return toggle;
	}
	
	public void setToggle(String _toggle) {
		toggle = _toggle;
	}
	
	protected String translateExtraParam() {
		
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<Toggle value=\"");
		sb.append(getToggle());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;
			toggle = "";
			
			valueChanged = true;
            
            //System.out.println("Loading attributes");
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Toggle")) {
                                //System.out.println("Analyzing attribute");
								
                                s = elt.getAttribute("value");
								if (s!=null) {
									toggle = s;
								}
							}
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
	
	
}

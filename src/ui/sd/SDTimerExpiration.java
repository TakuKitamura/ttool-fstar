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
 * Class SDTimerExpiration
 * Setting of a timer for a given duration. To be used in Sequence Diagrams.
 * Creation: 06/10/2004
 * @version 1.0 06/10/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

import java.awt.*;
import javax.swing.*;
import org.w3c.dom.*;

import myutil.*;
import ui.*;

public class SDTimerExpiration extends TGCWithoutInternalComponent implements SwallowedTGComponent {
    private String timer = "myTimer";
    private int widthValue, heightValue;
    private int lineWidth = 20;
    
    public SDTimerExpiration(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 15;
        height = 25;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "timer expiration";
        makeValue();
        widthValue = 0; heightValue=0;
        
        myImageIcon = IconManager.imgic516;
    }
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            widthValue  = g.getFontMetrics().stringWidth(value);
            heightValue = g.getFontMetrics().getHeight();
        }
        
        g.drawString(value, x+width, y+height/2+3);
        
        g.drawLine(x, y, x+width, y+height);
        g.drawLine(x, y, x+width, y);
        g.drawLine(x, y+height, x+width, y+height);
        g.drawLine(x+width, y, x, y+height);
        
        GraphicLib.arrowWithLine(g, 2, 0, 10, x+width/2-lineWidth, y+height/2, x+width/2, y+height/2, true);
    }
    
    public int getLineLength() {
        return lineWidth;
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        /* text */
        if (GraphicLib.isInRectangle(_x, _y, x+width, y+height/2-heightValue+3, widthValue, heightValue)) {
            return this;
        }
        
        /* line */
        if (GraphicLib.isInRectangle(_x, _y, x+width/2-lineWidth, y+height/2-2, lineWidth, 4)) {
            return this;
        }
        return null;
    }
    
    public int getMyCurrentMaxX() {
        return x+width + widthValue;
    }
    
    public int getType() {
        return TGComponentManager.SD_TIMER_EXPIRATION;
    }
    
    public String getTimer() {
        return timer;
    }
    
    public int getYOrder() {
        return y+height/2;
    }
    
    public void makeValue() {
        value = "{timer=" + timer + "}";
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = timer;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting timer value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getTimer());
        
        if (s != null) {
            s = s.trim();
        }
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not perform any change: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            timer = s;
            makeValue();
            return true;
        }
        return false;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Interval timer=\"");
        sb.append(getTimer());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
		boolean timerSet = false;
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
                            if (elt.getTagName().equals("Interval")) {
                                timer = elt.getAttribute("timer");
								timerSet = true;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
			//System.out.println("Exception =" + e.getMessage());
			if (!timerSet) {
				throw new MalformedModelingException();
			}
        }
        makeValue();
    }
}
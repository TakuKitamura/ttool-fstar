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
 * Class SDAbsoluteTimeConstraint
 * Absolute time constraint of the form @{1..2}. To be used in Sequence Diagrams.
 * Creation: 30/09/2004
 * @version 1.0 30/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

import java.awt.*;
import javax.swing.*;
import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class SDAbsoluteTimeConstraint extends TGCWithoutInternalComponent implements SwallowedTGComponent {
    private String minConstraint = "0";
    private String maxConstraint = "0";
    private int widthValue, heightValue;
    
    public SDAbsoluteTimeConstraint(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 40;
        height = 15;
        
        nbConnectingPoint = 0;
        nbInternalTGComponent = 0;
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "absolute time constraint";
        value = "@ {0..0}";
        widthValue = 0; heightValue=0;
        
        myImageIcon = IconManager.imgic506;
    }
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            widthValue  = g.getFontMetrics().stringWidth(value);
            heightValue = g.getFontMetrics().getHeight();
        }
        g.drawString(value, x-widthValue-2, y);
        g.drawLine(x, y, x+width, y);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y-height/2, width, height)) {
            return this;
        }
        
        if (GraphicLib.isInRectangle(_x, _y, x-widthValue-2, y-heightValue, widthValue, heightValue)) {
            return this;
        }
        return null;
    }
    
    public int getMyCurrentMinX() {
        return Math.min(x-widthValue, x);

    }
    
    
    public int getMyCurrentMinY() {
        return y-heightValue;
    }
    
    public int getType() {
        return TGComponentManager.SD_ABSOLUTE_TIME_CONSTRAINT;
    }
    
    public String getMinConstraint() {
        return minConstraint;
    }
    
    public String getMaxConstraint() {
        return maxConstraint;
    }
    
    public void makeValue() {
        value = "@ {" + minConstraint + ".." + maxConstraint + "}";
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldMin = getMinConstraint();
        String oldMax = getMaxConstraint();
        String[] array = new String[2];
        array[0] = getMinConstraint(); array[1] = getMaxConstraint();
        
        JDialogTimeInterval jdti = new JDialogTimeInterval(frame, array, "Setting absolute time constraints");
        jdti.setSize(350, 250);
        GraphicLib.centerOnParent(jdti);
        jdti.show(); // blocked until dialog has been closed
        
        minConstraint = array[0]; maxConstraint = array[1];
        
        if ((minConstraint != null) && (maxConstraint != null) && ((!minConstraint.equals(oldMin)) || (!maxConstraint.equals(oldMax)))){
            makeValue();
            return true;
        }
        minConstraint = oldMin;
        maxConstraint = oldMax;
        return false;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Interval minConstraint=\"");
        sb.append(getMinConstraint());
        sb.append("\" maxConstraint=\"");
        sb.append(getMaxConstraint());
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
                            if (elt.getTagName().equals("Interval")) {
                                minConstraint = elt.getAttribute("minConstraint");
                                maxConstraint = elt.getAttribute("maxConstraint");
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
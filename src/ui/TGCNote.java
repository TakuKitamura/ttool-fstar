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
 * Class TGCNote
 * Generic text box for displaying notes
 * Creation: 06/12/2003
 * @version 1.0 06/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;
import javax.swing.*;


import org.w3c.dom.*;

import myutil.*;
import ui.window.*;

public class TGCNote extends TGCWithoutInternalComponent {
    
    protected String[] values;
    protected int textX = 5;
    protected int textY = 20;
    protected int marginY = 20;
    protected int marginX = 20;
    protected int limit = 15;
    protected Graphics myg;
    
    protected Color myColor;
    
    public TGCNote(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 150;
        height = 30;
        minWidth = 50;
        minHeight = 20;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "UML Note";
        value = "UML note:\nDouble-click to edit";
        
        myImageIcon = IconManager.imgic320;
    }
    
    
    public void internalDrawing(Graphics g) {
        
        if ((values == null) || (myg == null)){
            myg = g;
            makeValue();
        }
        
        int h  = g.getFontMetrics().getHeight();
        Color c = g.getColor();
		
        
        g.drawLine(x, y, x+width, y);
        g.drawLine(x, y, x, y+height);
        g.drawLine(x, y+height, x+width-limit, y+height);
        g.drawLine(x+width, y, x+width, y+height - limit);
        
		g.setColor(ColorManager.UML_NOTE_BG);
		int [] px1 = {x, x+width, x + width, x + width-limit, x};
        int [] py1 = {y, y, y+height-limit, y+height, y+height};
		g.fillPolygon(px1, py1, 5);
		g.setColor(c);
		
        int [] px = {x+width, x + width - 4, x+width-10, x + width-limit};
        int [] py = {y+height-limit, y + height - limit + 3, y + height - limit + 2, y +height};
        g.drawPolygon(px, py, 4);
		
        if (g.getColor() == ColorManager.NORMAL_0) {
            g.setColor(ColorManager.UML_NOTE);
        }
        g.fillPolygon(px, py, 4);
		
		g.setColor(Color.black);
        for (int i = 0; i<values.length; i++) {
            g.drawString(values[i], x + textX, y + textY + i* h);
        }
		g.setColor(c);
		
		
		
		
    }
    
    public void makeValue() {
        values = Conversion.wrapText(value);
        checkMySize();
    }
    
    public void checkMySize() {
        if (myg == null) {
            return;
        }
        int desiredWidth = minWidth;
        for(int i=0; i< values.length; i++) {
            desiredWidth = Math.max(desiredWidth, myg.getFontMetrics().stringWidth(values[i]) + marginX);
        }
        
        int desiredHeight = values.length * myg.getFontMetrics().getHeight() + marginY;
        
        if ((desiredWidth != width) || (desiredHeight != height)) {
            resize(desiredWidth, desiredHeight);
        }
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        
        JDialogNote jdn = new JDialogNote(frame, "Setting the note", value);
        //jdn.setLocation(200, 150);
        GraphicLib.centerOnParent(jdn);
        jdn.show(); // blocked until dialog has been closed
        
        String s = jdn.getText();
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            String tmp = s;
            /*s = GTURTLEModeling.removeForbiddenCharactersFromInput(s);
            s = Conversion.replaceAllChar(s, '&', " ");
            s = Conversion.replaceAllChar(s, '"', " ");
            
            if(s.compareTo(tmp) != 0) {
                JOptionPane.showMessageDialog(frame, "Forbidden characters have been removed from the note", "Error", JOptionPane.INFORMATION_MESSAGE);
            }*/
            setValue(s);
            makeValue();
            return true;
        }
        return false;
    }
    
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.UML_NOTE;
    }
    
    protected String translateExtraParam() {
        if (values == null) {
            makeValue();
        }
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(int i=0; i<values.length; i++) {
            sb.append("<Line value=\"");
            sb.append(GTURTLEModeling.transformString(values[i]));
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        value = "";
        values = null;
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;
            
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
                            if (elt.getTagName().equals("Line")) {
                                //System.out.println("Analyzing line");
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                value += GTURTLEModeling.decodeString(s) + "\n";
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








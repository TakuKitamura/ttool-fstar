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
 * Class Requirement
 * Turtle requirement: to be used in requirement diagram
 * Creation: 30/05/2006
 * @version 1.0 30/05/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.req;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;

public class Requirement extends TGCWithInternalComponent {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
    protected boolean formal = false;
    protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;
    
    protected final static String REGULAR_REQ = "<<Requirement>>";
    protected final static String FORMAL_REQ = "<<Formal Requirement>>";
    
    public final static int HIGH = 0;
    public final static int MEDIUM = 1;
    public final static int LOW = 2;
    
    public Requirement(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 200; height = 30;
        minWidth = 200;
        minDesiredWidth = 200;
        minDesiredHeight = 30;
        
        nbConnectingPoint = 10;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, .5);
        connectingPoint[1] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[2] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[3] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[4] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[5] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, .5);
        connectingPoint[6] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[7] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[8] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[9] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.75, 0.0);
        addTGConnectingPointsCommentTop();    
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        TAttributeRequirement tgc0;
        tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        
        moveable = true;
        editable = true;
        removable = true;
        
        formal = false;
        
        // Name of the requirement
        name = "Requirement";
        value = tdp.findRequirementName("Requirement_");
        oldValue = value;
        
        myImageIcon = IconManager.imgic104;
        
        actionOnAdd();
    }
    
    public void recalculateSize() {
        //System.out.println("Recalculate size of " + this);
        int i, j;
     
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].calculateMyDesiredSize();
        }
        
        int minW = getMyDesiredWidth();
        for(i=0; i<nbInternalTGComponent; i++) {
            minW = Math.max(minW, tgcomponent[i].getMinDesiredWidth());
        }
        
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].forceSize(minW, tgcomponent[i].getMinDesiredHeight());
        }
        
        forceSize(minW, getHeight());
        
        // Reposition all internal components
        int h = getHeight();
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].setCdRectangle(0, 0, h, h);
            tgcomponent[i].setCd(tgcomponent[i].getX(), h);
            h += tgcomponent[i].getHeight();
        }
    }
    
    public int getMyDesiredWidth() {
        if (graphics == null) {
            graphics = tdp.getGraphics();
        }
        if (graphics == null) {
            return minWidth;
        }
        int size = graphics.getFontMetrics().stringWidth(value) + iconSize + 5;
        minDesiredWidth = Math.max(size, minWidth);
        return minDesiredWidth;
    }
    
    
    private int calculateDesiredWidth() {
        return Math.max(minDesiredWidth, tgcomponent[0].getMinDesiredWidth());
    }
    
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            graphics = g;
        }
        
        Font f = g.getFont();
        int size = f.getSize();
        g.drawRect(x, y, width, height);
        g.setColor(Color.yellow);
        g.fillRect(x+1, y+1, width-1, height-1);
        g.drawImage(IconManager.img8, x + width - 20, y + 6, Color.yellow, null);
        ColorManager.setColor(g, getState(), 0);
        g.setFont(f.deriveFont(Font.BOLD));
        int w = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + (width - w)/2, y + textY);
        g.setFont(f);
        g.setFont(f.deriveFont((float)startFontSize));
        if (formal) {
            w  =  g.getFontMetrics().stringWidth(FORMAL_REQ);
            g.drawString(FORMAL_REQ, x + (width - w)/2, y + startFontSize);
        } else {
            w  =  g.getFontMetrics().stringWidth(REGULAR_REQ);
            g.drawString(REGULAR_REQ, x + (width-w)/2, y + startFontSize);
        }
        g.setFont(f);
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        oldValue = value;
        
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Requirement: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isRequirementNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Requirement: the new name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
           
            int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                newSizeForSon(null);
            }
            setValue(s);
  
            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Requirement: this name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                setValue(oldValue);
            }
        }
        return false;
    }
    
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getRequirementName() {
        return value;
    }
    
    public boolean isFormal() {
        return formal;
    }
    
    public void setFormal(boolean b) {
        formal = b;
    }
    
    public  int getType() {
        return TGComponentManager.TREQ_REQUIREMENT;
    }
    
    public void checkSizeOfSons() {
        ((TAttributeRequirement)(tgcomponent[0])).checkMySize();
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        JMenuItem isFormal = null;
        if (formal) {
            isFormal = new JMenuItem("Set a regular requirement");
        } else {
            isFormal = new JMenuItem("Set as formal requirement");
        }
        
        isFormal.addActionListener(menuAL);
        componentMenu.add(isFormal);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.indexOf("regular") > -1) {
            //System.out.println("Set to regular");
            formal = false;
        } else {
            //System.out.println("Set to formal");
            formal = true;
        }
        return true;
    }
    
    public String toString() {
        String ret =  getValue();
        if (formal) {
            ret = ret + " " + FORMAL_REQ;
        }  else {
            ret = ret + " " + REGULAR_REQ;
        }
 
        return ret;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Formal isFormal=\"");
        if (isFormal()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
 
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String startS;
            
            //System.out.println("Loading tclass " + getValue());
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Formal")) {
                                startS = elt.getAttribute("isFormal");
                                if (startS.equals("true")) {
                                    formal = true;
                                } else {
                                    formal = false;
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
    
    public String getViolatedAction() {
        return ((TAttributeRequirement)(tgcomponent[0])).getViolatedAction();
    }
    
    public String getText() {
        return ((TAttributeRequirement)(tgcomponent[0])).getText();
    }
    
    public int getCriticality() {
         return ((TAttributeRequirement)(tgcomponent[0])).getCriticality();
    }
    
}

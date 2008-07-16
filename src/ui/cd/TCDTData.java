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
 * Class TCDTData
 * TObject to be used in class diagram
 * Creation: 12/12/2005
 * @version 1.0 12/12/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;


import java.awt.*;
//import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//import org.w3c.dom.*;

import myutil.*;
import ui.*;
//import ui.ad.*;
//import ui.window.*;

public class TCDTData extends TGCWithInternalComponent {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 18;
    protected boolean start = false;
    protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;
    
    
    public final static String TDATA_REAL_SEPARATOR = "_";
    
    protected final static String DATA_STRING = "<<TData>>";
    
    public TCDTData(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 150; height = 30;
        minWidth = 150;
        minDesiredWidth = 150;
        minDesiredHeight = 30;
        
        addTGConnectingPointsCommentTop();
        
        nbInternalTGComponent = 2;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        TGComponent tgc;
        TCDAttributeBox tgc0;
        tgc0 = new TCDAttributeBoxNoConnection(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        h += tgcomponent[0].getHeight() + 1;
        tgc = new TCDOperationBoxNoConnection(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[1] = tgc;
        
        moveable = true;
        editable = true;
        removable = true;
        
        start = false;
        
        // Name of the Tclass
        name = "Tdata";
        value = tdp.findTClassName("TData_");
        oldValue = value;
        
        myImageIcon = IconManager.imgic130;
        
        actionOnAdd();
    }
    
    public void reset() {
        ((TCDReducedAttributeBox)tgcomponent[0]).setAttributes(new Vector());
        ((TCDReducedAttributeBox)tgcomponent[0]).checkMySize();
    }
    
    /*protected void newSizeForSon(TGComponent tgc) {
        
        int desiredWidth = calculateDesiredWidth();
        int i;
        
        forceSize(desiredWidth, height);
        
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].forceSize(desiredWidth, tgcomponent[i].getMinDesiredHeight());
        }
        
        int h = height;
        int index;
        
        if(tgc == null) {
            index = -1;
        } else {
            index = getMyNum(tgc);
        }
        
        for(i=0; i<index+1; i++) {
            h += tgcomponent[i].getHeight();
        }
        h = h + 1;
        for(i=index +1; i<nbInternalTGComponent; i++) {
            tgcomponent[i].setCdRectangle(0, 0, h, h);
            tgcomponent[i].setCd(tgcomponent[i].getX(), h);
            h += 1 + tgcomponent[i].getHeight();
        }
    }
    
    private void newWidthForSon() {
        int i;
        int w = getWidth();
        int h = getHeight();
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].forceSize(getWidth(), tgcomponent[i].getHeight());
        }
        
        for(i=0; i<nbInternalTGComponent; i++) {
            tgcomponent[i].setCdRectangle(0, 0, h, h);
            tgcomponent[i].setCd(tgcomponent[i].getX(), h);
            h += 1 + tgcomponent[i].getHeight();
        }
    }
    
    private int calculateDesiredWidth() {
        int w = Math.max(minDesiredWidth, tgcomponent[0].getMinDesiredWidth());
        w = Math.max(w, tgcomponent[1].getMinDesiredWidth());
        return w;
    }*/
    
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
        int size = (int)(graphics.getFontMetrics().getStringBounds(value, graphics).getWidth() + iconSize + 5);
        //System.out.println("size=" + size);
        minDesiredWidth = Math.max(size, minWidth);
        return minDesiredWidth;
    }
    
    private int calculateDesiredWidth() {
        int w = Math.max(minDesiredWidth, tgcomponent[0].getMinDesiredWidth());
        w = Math.max(w, tgcomponent[1].getMinDesiredWidth());
        return w;
    }
    
    
    public void internalDrawing(Graphics g) {
        if (graphics == null) {
            graphics = g;
            int size = graphics.getFontMetrics().stringWidth(value) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                newSizeForSon(null);
            }
        }
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
        g.drawString(value, x + textX, y + textY);
        int w  = g.getFontMetrics().stringWidth(value);
        //g.drawLine(x+textX, y + textY + 1, x+textX+w, y + textY + 1);
        g.setFont(f);
        g.setFont(f.deriveFont((float)startFontSize));
        w  =  g.getFontMetrics().stringWidth(DATA_STRING);
        g.drawString(DATA_STRING, x + width - w - 5, y + textY + startFontSize);
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
                "Could not change the name of the TData: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isTClassNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the TData: the new name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            /*int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                newSizeForSon(null);
            }*/
            setValue(s);
            recalculateSize();
            return true;
        }
              
        return false;
    }
    
    public void resetAttributes(Vector v) {
        Vector setV = new Vector();
        TAttribute ta;
        for(int i=0; i<v.size(); i++) {
            ta = (TAttribute)(v.elementAt(i));
            setV.addElement(ta.makeClone());
        }
        ((TCDReducedAttributeBox)tgcomponent[0]).setAttributes(setV);
        ((TCDReducedAttributeBox)tgcomponent[0]).checkMySize();
    }
    
    public void updateAttributes(Vector v) {
        Vector setV = ((TCDReducedAttributeBox)tgcomponent[0]).getAttributes();
        int size = setV.size();
        TAttribute ta1, ta2 = null;
        int i, j;
        boolean found;
        
        // adapt old vector to the new attributes
        for(i=0; i<v.size(); i++) {
            ta1 = (TAttribute)(v.elementAt(i));
            found = false;
            
            // is in vector?
            for(j=0; j<setV.size(); j++) {
                ta2 = (TAttribute)(setV.elementAt(j));
                if (ta1.getId().compareTo(ta2.getId()) == 0) {
                    if ((ta2.isSet()) && (ta1.getType() == ta2.getType())) {
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) {
                setV.addElement(ta1.makeClone());
            } else {
                setV.addElement(ta2);
            }
        }
        
        // Remove first elements
        for(i=0; i<size; i++) {
            setV.removeElementAt(0);
        }
        
        ((TCDReducedAttributeBox)tgcomponent[0]).setAttributes(setV);
        ((TCDReducedAttributeBox)tgcomponent[0]).checkMySize();
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    
    public  int getType() {
        return TGComponentManager.TCD_TDATA;
    }
    
    public Vector getAttributes(){
        return ((TGCAttributeBox)(tgcomponent[0])).getAttributeList();
    }
    

    public String toString() {    
       return getValue(); 
    }
    
 	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_ASSOCIATION;
      }
}
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
 * Class RequirementObserver
 * Turtle observer: to be used in requirement diagram
 * Creation: 02/05/2006
 * @version 1.0 02/05/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.req;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public class RequirementObserver extends TGCWithInternalComponent {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
    protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;
    
    protected final static String TOBSERVER = "<<TObserver>";
    
    public RequirementObserver(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 200; height = 30;
        minWidth = 200;
        minDesiredWidth = 200;
        minDesiredHeight = 30;
        
        nbConnectingPoint = 5;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.0, .5);
        connectingPoint[1] = new TGConnectingPointVerify(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[2] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[3] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[4] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.75, 0.0);
        addTGConnectingPointsCommentTop();
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        TAttributeObserver tgc0;
        tgc0 = new TAttributeObserver(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        
        moveable = true;
        editable = true;
        removable = true;
 
        // Name of the requirement
        name = "RequirementObserver";
        value = "RequirementObserver";
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
        g.setFont(f.deriveFont((float)startFontSize));
        w  =  g.getFontMetrics().stringWidth(TOBSERVER);
        g.drawString(TOBSERVER, x + (width - w)/2, y + startFontSize);
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
    
    public String getRequirementObserverName() {
        return value;
    }
    
    
    public  int getType() {
        return TGComponentManager.TREQ_OBSERVER;
    }
    
    public void checkSizeOfSons() {
        ((TAttributeObserver)(tgcomponent[0])).checkMySize();
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        JMenuItem generate = null;
        // Should verify first whether it is connected to a formal requirement with a verify relation, or not
        generate = new JMenuItem("Generate on diagrams");
        
        generate.addActionListener(menuAL);
        componentMenu.add(generate);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.indexOf("diagrams") > -1) {
           // To be implemented!
        }
        return true;
    }
    
    public String toString() {
        String ret =  getValue() + TOBSERVER;
        return ret;
    }
    
    public String getDiagramName() {
        return ((TAttributeObserver)(tgcomponent[0])).getValue().trim();
    }
    
    public String[] getDiagramNames() {
        TAttributeObserver tao = (TAttributeObserver)(tgcomponent[0]);
        String []ret = tao.getValue().trim().split("((\\s)+(\\r)*(\\n)*)+");
        return ret;
    }
    
}
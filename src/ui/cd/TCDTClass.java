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
 * Class TCDTClass
 * TClass to be used in class diagram
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import ui.ad.*;
import myutil.*;
import ui.*;

public class TCDTClass extends TGCWithInternalComponent implements TClassInterface, TClassSynchroInterface {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 18;
    protected boolean start = true;
    protected boolean observer = false;
    protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;
    
    protected final static String START_STRING = "<<start>>";
    protected final static String OBSERVER_STRING = "<<tobserver>>";
    
    public TCDTClass(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 150; height = 30;
        minWidth = 150;
        minDesiredWidth = 150;
        minDesiredHeight = 30;
        
        nbConnectingPoint = 5;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointTClasses(this, 0, 0, true, true, 0.0, .5);
        connectingPoint[1] = new TGConnectingPointTClasses(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[2] = new TGConnectingPointTClasses(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[3] = new TGConnectingPointTClasses(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[4] = new TGConnectingPointTClasses(this, 0, 0, true, true, 0.75, 0.0);
        addTGConnectingPointsCommentTop();
        
        
        nbInternalTGComponent = 4;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        TCDAttributeGateBox tgc1;
        TCDAttributeBox tgc0;
        tgc0 = new TCDAttributeBox(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        h += tgcomponent[0].getHeight() + 1;
        tgc1 = new TCDAttributeGateBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[1] = tgc1;
        h += tgcomponent[1].getHeight() + 1;
        TGComponent tgc = new TCDOperationBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[2] = tgc;
        h += tgcomponent[2].getHeight() + 1;
        tgc = new TCDActivityDiagramBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[3] = tgc;
        
                /* setting attributes dependences */
        tgc0.setForbiddenNames(tgc1.getAttributeList());
        tgc1.setForbiddenNames(tgc0.getAttributeList());
        
        moveable = true;
        editable = true;
        removable = true;
        
        start = true;
        
        // Name of the Tclass
        name = "Tclass";
        value = tdp.findTClassName("TClass_");
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
        int w = Math.max(minDesiredWidth, tgcomponent[0].getMinDesiredWidth());
        w = Math.max(w, tgcomponent[1].getMinDesiredWidth());
        return w;
    }
    
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            graphics = g;
        }
        
        //System.out.println("My width = " + width + " this=" + this);
        Font f = g.getFont();
        int size = f.getSize();
        g.drawRect(x, y, width, height);
        g.setColor(Color.yellow);
        g.fillRect(x+1, y+1, width-1, height-1);
        g.drawImage(IconManager.img8, x + width - 20, y + 6, Color.yellow, null);
        ColorManager.setColor(g, getState(), 0);
        g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(value, x + textX, y + textY);
        g.setFont(f);
        if (start) {
            g.setFont(f.deriveFont((float)startFontSize));
            int w  =  g.getFontMetrics().stringWidth(START_STRING);
            g.drawString(START_STRING, x + width - w - 5, y + textY + startFontSize);
            g.setFont(f);
        }
        
        if (observer) {
            g.setFont(f.deriveFont((float)startFontSize));
            g.drawString(OBSERVER_STRING, x + 2, y + textY + startFontSize);
            g.setFont(f);
        }
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
                "Could not change the name of the TClass: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isTClassNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the TClass: the new name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
           
           setValue(s);
           recalculateSize();
            /*int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                //forceSize(w, getHeight());
                //newWidthForSon();
                newSizeForSon(null);
            }*/
            
            
            
            
            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the TClass: this name is already in use",
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
    
    public String getClassName() {
        return value;
    }
    
    public boolean isStart() {
        return start;
    }
    
    public void setStart(boolean b) {
        start = b;
    }
    
    public boolean isObserver() {
        return observer;
    }
    
    public void setObserver(boolean b) {
        observer = b;
    }
    
    public  int getType() {
        return TGComponentManager.TCD_TCLASS;
    }
    
    public Vector getAttributes(){
        return ((TGCAttributeBox)(tgcomponent[0])).getAttributeList();
    }
    
    public Vector getGates() {
        return ((TGCAttributeBox)(tgcomponent[1])).getAttributeList();
    }
    
    public void checkSizeOfSons() {
        ((TGCAttributeBox)(tgcomponent[0])).checkMySize();
        ((TGCAttributeBox)(tgcomponent[1])).checkMySize();
    }
    
    public void setAttributes(Vector attributes) {
        ((TGCAttributeBox)(tgcomponent[0])).setAttributeList(attributes);
    }
    
    public void setGates(Vector gates) {
        ((TGCAttributeBox)(tgcomponent[1])).setAttributeList(gates);
    }
    
    // builds a new Vector
    public Vector gatesNotSynchronizedOn(TCDSynchroGateList tcdsgl) {
        Vector v = (Vector)(getGates().clone());
        tdp.removeSynchronizedGates(v, this, tcdsgl);
        return v;
    }
    
    public TAttribute getGateById(String name) {
        TAttribute ta;
        Vector list = ((TGCAttributeBox)(tgcomponent[1])).getAttributeList();
        for(int i=0; i<list.size(); i++) {
            ta = (TAttribute)(list.elementAt(i));
            if (ta.getId().equals(name)) {
                return ta;
            }
        }
        return null;
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        JMenuItem isStart = null;
        if (start) {
            isStart = new JMenuItem("Remove <<start>>");
        } else {
            isStart = new JMenuItem("Add <<start>>");
        }
        
        isStart.addActionListener(menuAL);
        componentMenu.add(isStart);
        
        JMenuItem isObserver = null;
        if (observer) {
            isObserver = new JMenuItem("Remove <<tobserver>>");
        } else {
            isObserver = new JMenuItem("Add <<tobserver>>");
        }
        
        isObserver.addActionListener(menuAL);
        componentMenu.add(isObserver);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.indexOf("observer") == -1) {
            start = !start;
        } else {
            observer = !observer;
        }
        return true;
    }
    
    public String toString() {
        String ret =  getValue();
        if (start) {
            ret = ret + " " + START_STRING;
        } 
        
        if (observer) {
            ret = ret + " " + OBSERVER_STRING;
        } 
        
        return ret;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Start isStart=\"");
        if (isStart()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" />\n");
        sb.append("<Observer isObserver=\"");
        if (isObserver()) {
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
                            if (elt.getTagName().equals("Start")) {
                                startS = elt.getAttribute("isStart");
                                if (startS.equals("true")) {
                                    start = true;
                                } else {
                                    start = false;
                                }
                            } else if (elt.getTagName().equals("Observer")) {
                                startS = elt.getAttribute("isObserver");
                                if (startS.equals("true")) {
                                    observer = true;
                                } else {
                                    observer = false;
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
    
    public TActivityDiagramPanel getActivityDiagramPanel() {
        return ((DesignPanel)(tdp.tp)).getActivityDiagramPanel(getClassName());
    }
    
    public ActivityDiagramPanelInterface getBehaviourDiagramPanel() {
        return (ActivityDiagramPanelInterface)getActivityDiagramPanel();
    }
    
 	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_ASSOCIATION;
      }
    
}

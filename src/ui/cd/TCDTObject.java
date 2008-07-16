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
 * Class TCDTObject
 * TObject to be used in class diagram
 * Creation: 10/05/2004
 * @version 1.0 10/05/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.ad.*;
import ui.window.*;

public class TCDTObject extends TGCWithInternalComponent implements TClassInterface, TClassSynchroInterface {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 18;
    protected boolean start = false;
    protected boolean observer = false;
    protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;
    
    protected String valueg = "";
    
    protected String TOBJECT_G_SEPARATOR = ":";
    public final static String TOBJECT_REAL_SEPARATOR = "_";
    public final static String DEFAULT_TCLASS = "No_TClass";
    
    protected String firstName;
    
    protected TCDTClass masterTClass;
    protected String tmpMaster = "";
    protected boolean toBeChecked = false;
    
    protected final static String START_STRING = "<<start>>";
    protected final static String OBSERVER_STRING = "<<tobserver>>";
    
    public TCDTObject(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
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
        TCDReducedAttributeGateBox tgc1;
        TCDReducedAttributeBox tgc0;
        tgc0 = new TCDReducedAttributeBox(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        h += tgcomponent[0].getHeight() + 1;
        tgc1 = new TCDReducedAttributeGateBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[1] = tgc1;
        h += tgcomponent[1].getHeight() + 1;
        TGComponent tgc = new TCDOperationBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[2] = tgc;
        h += tgcomponent[2].getHeight() + 1;
        tgc = new TCDActivityDiagramBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[3] = tgc;
        
        moveable = true;
        editable = true;
        removable = true;
        
        start = false;
        
        // Name of the TObject
        name = "Tobject";
        firstName =tdp.findTObjectName("TObject_", TOBJECT_REAL_SEPARATOR +  DEFAULT_TCLASS);
        makeValue();
        oldValue = value;
        
        myImageIcon = IconManager.imgic104;
        
        actionOnAdd();
    }
    
    public void reset() {
        masterTClass = null;
        makeValue();
        ((TCDReducedAttributeBox)tgcomponent[0]).setAttributes(new Vector());
        ((TCDReducedAttributeBox)tgcomponent[0]).checkMySize();
        ((TCDReducedAttributeGateBox)tgcomponent[1]).setAttributes(new Vector());
        ((TCDReducedAttributeGateBox)tgcomponent[1]).checkMySize();
    }
    
    protected void makeValue() {
        if (masterTClass == null) {
            value = firstName + TOBJECT_REAL_SEPARATOR + DEFAULT_TCLASS;
            valueg = firstName + TOBJECT_G_SEPARATOR + DEFAULT_TCLASS;
        } else {
            value = firstName + TOBJECT_REAL_SEPARATOR + masterTClass.getValue();
            valueg = firstName + TOBJECT_G_SEPARATOR + masterTClass.getValue();;
        }
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
    
    
    public void internalDrawing(Graphics g) {
        makeValue();
        if (graphics == null) {
            graphics = g;
            recalculateSize();
            /*int size = graphics.getFontMetrics().stringWidth(value) + iconSize + 5;
            minDesiredWidth = Math.max(size, minWidth);
            if (minDesiredWidth != width) {
                newSizeForSon(null);
            }*/
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
        g.drawString(valueg, x + textX, y + textY);
        int w  = g.getFontMetrics().stringWidth(valueg);
        g.drawLine(x+textX, y + textY + 1, x+textX+w, y + textY + 1);
        g.setFont(f);
        if (start) {
            g.setFont(f.deriveFont((float)startFontSize));
            w  =  g.getFontMetrics().stringWidth(START_STRING);
            g.drawString(START_STRING, x + width - w - 5, y + textY + startFontSize);
            g.setFont(f);
        }
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldFirstName = firstName;
        TCDTClass oldMasterTClass = masterTClass;
        
        JDialogTObjectName jd = new JDialogTObjectName(this, frame, "Setting the name and class");
        jd.setSize(325, 275);
        GraphicLib.centerOnParent(jd);
        jd.show(); // blocked until dialog has been closed
        
        // check for any change
        
        // Is new name valid?
        makeValue();
        if ((firstName == null) || (firstName.length() == 0)) {
            JOptionPane.showMessageDialog(frame,
            "Could not perform changes: the new name is not a valid name",
            "Error",
            JOptionPane.INFORMATION_MESSAGE);
            firstName = oldFirstName;
            masterTClass = oldMasterTClass;
            makeValue();
            return false;
            
        }
        
        if (!TAttribute.isAValidId(firstName, false, false)) {
            JOptionPane.showMessageDialog(frame,
            "Could not perfom changes: the new name is not a valid name",
            "Error",
            JOptionPane.INFORMATION_MESSAGE);
            firstName = oldFirstName;
            masterTClass = oldMasterTClass;
            makeValue();
            return false;
        }
        
        // Is the name already in use?
        if (tdp.hasAlreadyAnInstance(this)) {
            JOptionPane.showMessageDialog(frame,
            "Could not perfom changes: the new name is already in use",
            "Error",
            JOptionPane.INFORMATION_MESSAGE);
            firstName = oldFirstName;
            masterTClass = oldMasterTClass;
            makeValue();
            return false;
        }
        
        // Should the size of the box be changed?
        /*int size = graphics.getFontMetrics().stringWidth(value) + iconSize + 5;
        minDesiredWidth = Math.max(size, minWidth);
        if (minDesiredWidth != width) {
            newSizeForSon(null);
        }*/
        recalculateSize();
        
        // Should attributes and Gates evolve
        if (masterTClass != oldMasterTClass) {
            if (masterTClass == null) {
                resetAttributes(new Vector());
                resetGates(new Vector());
            } else {
                resetAttributes(masterTClass.getAttributes());
                resetGates(masterTClass.getGates());
            }
        }
        
        // Should the name of synchronization gates evolve?
        // ...
        
        return true;
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
    
    public void resetGates(Vector v) {
        Vector setV = new Vector();
        TAttribute ta;
        for(int i=0; i<v.size(); i++) {
            ta = (TAttribute)(v.elementAt(i));
            setV.addElement(ta.makeClone());
        }
        ((TCDReducedAttributeGateBox)tgcomponent[1]).setAttributes(setV);
        ((TCDReducedAttributeGateBox)tgcomponent[1]).checkMySize();
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
    
    public void updateGates(Vector v) {
        Vector setV = ((TCDReducedAttributeGateBox)tgcomponent[1]).getAttributes();
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
                if (ta1.compareTo(ta2) == 0) {
                    found = true;
                    break;
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
        
        ((TCDReducedAttributeGateBox)tgcomponent[1]).setAttributes(setV);
        ((TCDReducedAttributeGateBox)tgcomponent[1]).checkMySize();
    }
    
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    // for translation issue
    public String getClassName() {
        return Conversion.replaceAllChar(value, ':', "_");
    }
    
    public String getObjectName() {
        return firstName;
    }
    
    public TCDTClass getMasterTClass() {
        return masterTClass;
    }
    
    public void setObjectName(String s) {
        firstName = s;
    }
    
    public void setMasterTClass(TCDTClass t) {
        masterTClass = t;
    }
    
    public boolean isStart() {
        return start;
    }
    
    public void setStart(boolean b) {
        start = b;
    }
    
    public  int getType() {
        return TGComponentManager.TCD_TOBJECT;
    }
    
    public Vector getAttributes(){
        return ((TGCReducedAttributeBox)(tgcomponent[0])).getAttributes();
    }
    
    public Vector getGates() {
        return ((TGCReducedAttributeBox)(tgcomponent[1])).getAttributes();
    }
    
    // builds a new Vector
    public Vector gatesNotSynchronizedOn(TCDSynchroGateList tcdsgl) {
        Vector v = (Vector)(getGates().clone());
        tdp.removeSynchronizedGates(v, this, tcdsgl);
        return v;
    }
    
    public TAttribute getGateById(String name) {
        TAttribute ta;
        Vector list = ((TGCReducedAttributeBox)(tgcomponent[1])).getAttributes();
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
        if (isStart()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" />\n");
        sb.append("<MasterTClass name=\"");
        if (masterTClass == null) {
            sb.append("null");
        } else {
            sb.append(masterTClass.getClassName());
            
        }
        sb.append("\" />\n");
        sb.append("<FirstName name=\"");
        sb.append(firstName);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
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
                            }
                             if (elt.getTagName().equals("Observer")) {
                                startS = elt.getAttribute("isObserver");
                                if (startS.equals("true")) {
                                    observer = true;
                                } else {
                                    observer = false;
                                }
                            }
                            if (elt.getTagName().equals("MasterTClass")) {
                                startS = elt.getAttribute("name");
                                tmpMaster = startS;
                                toBeChecked = true;
                                //System.out.println("Setting tmpMaster to " + startS);
                                //System.out.println("Loading masterclass of TObject");
                                if (startS.compareTo("null") == 0) {
                                    masterTClass = null;
                                } else {
                                    //System.out.println("Searching for " + startS);
                                    masterTClass = tdp.findTClassByName(startS);
                                }
                                makeValue();
                            }
                            if (elt.getTagName().equals("FirstName")) {
                                firstName = elt.getAttribute("name");
                                makeValue();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    
/*    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Start isStart=\"");
        if (isStart()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("\" />\n");
        sb.append("<Observer isObserver=\"");
        if (isStart()) {
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
                            }
                            if (elt.getTagName().equals("Observer")) {
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
    }*/
    
    // used for setting mastertclass
    public void postLoadingProcessing() throws MalformedModelingException {
        if (toBeChecked == false) {
            return;
        }
        toBeChecked = false;
        //System.out.println("Post Loading processing");
        //System.out.println("Searching for " + tmpMaster + " on diagram " + tdp.getName());
        if ((tmpMaster ==  null) || (tmpMaster.equals("")) || (tmpMaster.equals(" null")) || (tmpMaster.equals("null"))) {
            makeValue();
            return;
        }
        masterTClass = tdp.findTClassByName(tmpMaster);
        if (masterTClass == null) {
            System.out.println("Raising exception!");
            throw new MalformedModelingException();
        }
        makeValue();
    }
    
    public TActivityDiagramPanel getActivityDiagramPanel() {
        if (masterTClass == null) {
            return null;
        } else {
            return masterTClass.getActivityDiagramPanel();
        }
    }
    
    public ActivityDiagramPanelInterface getBehaviourDiagramPanel() {
        return (ActivityDiagramPanelInterface)getActivityDiagramPanel();
    }
    
 	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_ASSOCIATION;
      }
    
}
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
 * Class TOSClass
 * TClass to be used in TURTLEOS class diagram
 * Creation: 03/10/2006
 * @version 1.0 03/10/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.oscd;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import ui.osad.*;
import myutil.*;
import ui.*;
import ui.window.*;

public class TOSClass extends TGCWithInternalComponent implements TClassInterface {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 18;
    protected int stereotypeFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;

    protected int myStereotype = 0;
    protected int period;
    protected int deadline;

    public static final String [] stereotypes = {"unset", "periodic", "sporadic", "env", "sche. engine", "protected obj.", "protected obj. man.", "Evt manager"} ;
    public final static int PERIODIC = 1;
    public final static int SPORADIC = 2;
    public final static int ENV = 3;
    public final static int SCHEDULING_ENGINE = 4;
    public final static int PROTECTED_OBJECT = 5;
    public final static int PROTECTED_OBJECT_MANAGER = 6;
    public final static int EVT_MANAGER = 7;


    public TOSClass(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 150; height = 30;
        minWidth = 150;
        minDesiredWidth = 150;
        minDesiredHeight = 30;

        nbConnectingPoint = 5;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 0.0, .5);
        connectingPoint[1] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[2] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[3] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[4] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 0.75, 0.0);
        addTGConnectingPointsCommentTop();


        nbInternalTGComponent = 4;
        tgcomponent = new TGComponent[nbInternalTGComponent];

        int h = 1;
        TOSCDAttributeGateBox tgc1;
        TOSCDAttributeBox tgc0;
        tgc0 = new TOSCDAttributeBox(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        tgcomponent[0] = tgc0;
        h += tgcomponent[0].getHeight() + 1;
        tgc1 = new TOSCDAttributeGateBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[1] = tgc1;
        h += tgcomponent[1].getHeight() + 1;
        TGComponent tgc = new TOSCDOperationBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[2] = tgc;
        h += tgcomponent[2].getHeight() + 1;
        tgc = new TOSCDActivityDiagramBox(x, y+height + h, 0, 0, height + h, height + h, true, this, _tdp);
        tgcomponent[3] = tgc;

        /* setting attributes dependences */
        tgc0.setForbiddenNames(tgc1.getAttributeList());
        tgc1.setForbiddenNames(tgc0.getAttributeList());

        moveable = true;
        editable = true;
        removable = true;

        // Name of the Tclass
        name = "Tclass";
        value = tdp.findTOSClassName("TClass_");
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

        // Stereotype
        String s =getStereotypeFullString();
        int w  =  g.getFontMetrics().stringWidth(s);
        g.drawString(s, x + width - w - 5, y + textY + stereotypeFontSize);
        g.setFont(f);
    }

    public boolean editOndoubleClick(JFrame frame) {
        oldValue = value;

        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }

        JDialogTOSClass jdtosc = new JDialogTOSClass(frame, text, this);
        jdtosc.setSize(350, 400);
        GraphicLib.centerOnParent(jdtosc);
        jdtosc.setVisible(true);
        //System.out.println("toto");

        if (jdtosc.changeMade()) {
          //System.out.println("Change made");
          boolean ret = true;
          String s = jdtosc.getName();
          if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false)) {
                  JOptionPane.showMessageDialog(frame,
                  "Could not change the name of the TClass: the new name is not a valid name",
                  "Error",
                  JOptionPane.INFORMATION_MESSAGE);
                  ret = false;
            }
  
            if (!tdp.isTOSClassNameUnique(s)) {
                  JOptionPane.showMessageDialog(frame,
                  "Could not change the name of the TClass: the new name is already in use",
                  "Error",
                  JOptionPane.INFORMATION_MESSAGE);
                  ret = false;
            }
  
            setValue(s);
            recalculateSize();
  
            if (!tdp.actionOnDoubleClick(this)) {
                  JOptionPane.showMessageDialog(frame,
                  "Could not change the name of the class: this name is already in use",
                  "Error",
                  JOptionPane.INFORMATION_MESSAGE);
                  setValue(oldValue);
                  ret = false;
            }
          }

          myStereotype =  jdtosc.getIndexStereotype();

          try {
              period = Integer.decode(jdtosc.getPeriod()).intValue();
          } catch (Exception e) {
            ret = false;
          }

          try {
              deadline = Integer.decode(jdtosc.getDeadline()).intValue();
          } catch (Exception e) {
            ret = false;
          }

          return ret;
        }
        //System.out.println("returning false");
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

    public int getPeriod() {
        return period;
    }

    public int getDeadline() {
        return deadline;
    }

    public String getStereotypeFullString() {
        return "<<" + stereotypes[myStereotype] + ">>";
    }

    public int setStereotype(String s) {
           int i=0;
           while(i<stereotypes.length) {
             //System.out.println("Comparing " + s + "with " + stereotypes[i]);
             if (stereotypes[i].compareTo(s) == 0) {
                return i;
             }
             i++;
           }
           return 0;
    }

    public  int getType() {
        return TGComponentManager.TOSCD_TCLASS;
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
    /*public Vector gatesNotSynchronizedOn(TCDSynchroGateList tcdsgl) {
        Vector v = (Vector)(getGates().clone());
        tdp.removeSynchronizedGates(v, this, tcdsgl);
        return v;
    } */

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
        /*componentMenu.addSeparator();
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
        componentMenu.add(isObserver);   */
    }

    public boolean eventOnPopup(ActionEvent e) {
        /*String s = e.getActionCommand();
        if (s.indexOf("observer") == -1) {
            start = !start;
        } else {
            observer = !observer;
        } */
        return true;
    }

    public String toString() {
        return getValue() + " " + stereotypes[myStereotype];
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Stereotype data=\"");
        sb.append(stereotypes[myStereotype]);
        sb.append("\" />\n");
        sb.append("<Periodic period=\"");
        sb.append(period);
        sb.append("\" deadline=\"");
        sb.append(deadline);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }


    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String ste, ste1, ste2;

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
                          //System.out.println("Loading tag=" + elt.getTagName());

                            if (elt.getTagName().equals("Stereotype")) {
                                ste = elt.getAttribute("data");
                                myStereotype = setStereotype(ste);

                            }
                            if (elt.getTagName().equals("Periodic")) {
                                ste1 = elt.getAttribute("period");
                                ste2 = elt.getAttribute("deadline");
                                period = Integer.decode(ste1).intValue();
                                deadline = Integer.decode(ste2).intValue();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
    public int getStereotype() {
      return myStereotype;
    }

    public TURTLEOSActivityDiagramPanel getActivityDiagramPanel() {
        return ((TURTLEOSDesignPanel)(tdp.tp)).getTURTLEOSActivityDiagramPanel(getClassName());
    }
    
    public ActivityDiagramPanelInterface getBehaviourDiagramPanel() {
      return (ActivityDiagramPanelInterface)(getActivityDiagramPanel());
    }
    
    public boolean isStart() {
        return true;
    }
    




}

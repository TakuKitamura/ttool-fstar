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
 * Class TCDWatchdogGateList
 * Internal component that represents a list of Gates
 * Creation: 06/07/2004
 * @version 1.0 06/07/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.cd;

import java.awt.*;
//import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;

public class TCDWatchdogGateList extends TGCWithoutInternalComponent {
    protected TClassInterface t1;
    protected TClassInterface oldt1;
    protected Vector gates, gatesTmp; // Vector of TOneAttribute
    
    protected int minWidth = 10;
    protected int minHeight = 15;
    protected int h;
    
    protected String defaultValue;
    
    public TCDWatchdogGateList(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        moveable = true;
        editable = true;
        removable = false;
        
        gates = new Vector();
        
        oldt1 = null; t1 = null;
        
        myImageIcon = IconManager.imgic302;
    }
    
    public Vector getGates() {
        return gates;
    }
    
    public void setDefaultValue(String value) {
        defaultValue = value;
    }
    
    public void internalDrawing(Graphics g) {
        width = minWidth;
        h  = g.getFontMetrics().getHeight();
        ColorManager.setColor(g, getState(), 0);
        if ((gates == null) || (gates.size() == 0)) {
            g.drawString(defaultValue, x, y);
            width = Math.max(g.getFontMetrics().stringWidth(defaultValue), width);
            height = g.getFontMetrics().getHeight();
        } else {
            h = h + 2;
            TOneAttribute tt;
            String s;
            for(int i=0; i<gates.size(); i++) {
                tt = (TOneAttribute)(gates.elementAt(i));
                s = tt.toShortString();
                if (i == 0) {
                    s = "{ " + s;
                }
                if (i == (gates.size() - 1)) {
                    s = s + " }";
                }
                width = Math.max(g.getFontMetrics().stringWidth(s), width);
                g.drawString(s, x, y + i* h);
            }
            height = gates.size() * h;
        }
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y - h + 2, width, height)) {
            return this;
        }
        return null;
    }
    
    public void valueChanged() {
        TOneAttribute tt;
        
        //System.out.println("Checking if gates are still declared");
        
        for(int i=0; i<gates.size(); i++) {
            tt = (TOneAttribute)(gates.elementAt(i));
            if ((tt.t1.getGateById(tt.ta1.getId()) == null)) {
                //System.out.println("Removing gate " + tt.ta1.getId();
                gates.remove(tt);
                i --;
            }
        }
    }
    
    public void setTClass(TClassInterface _t1) {
        t1 = _t1;
        if (t1 != oldt1)  {
            gates = new Vector();
            //System.out.println("New gates");
            makeValue();
        }
        oldt1 = t1;
    }
    
    private void makeValue() {
        value = "";
        for(int i=0; i<gates.size(); i++) {
            if (i != 0) {
                value += "\n";
            }
            value += (gates.elementAt(i)).toString();
        }
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        if (t1 ==null) {
            JOptionPane.showMessageDialog(frame, "This composition operator is not connected to an association connecting two TClasses", "Edit error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        /*String oldValue = value;
        JDialogSynchro jda = new JDialogSynchro(frame, t1, t2, gates, this, "Setting synchronization gates")
        jda.setSize(750, 400);
        GraphicLib.centerOnParent(jda);
        jda.show(); // blocked until dialog has been closed
        
        makeValue();
        
        if (!oldValue.equals(value)) {
            return true;
        }*/
        
        return false;
    }
    
    protected String translateExtraParam() {
        TOneAttribute tt;
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(int i=0; i<gates.size(); i++) {
            tt = (TOneAttribute)(gates.elementAt(i));
            sb.append("<Watchdog t1=\"");
            sb.append(tt.t1.getId());
            sb.append("\" g1=\"");
            sb.append(tt.ta1.getId());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            gatesTmp = new Vector();
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String g1;
            TOneAttribute tt;
            
            //System.out.println("Loading Synchronization gates");
            //System.out.println(nl.toString());
            
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
                            t1id = -1;  g1 = null;
                            if (elt.getTagName().equals("Synchro")) {
                                t1id = Integer.decode(elt.getAttribute("t1")).intValue();
                                g1 = elt.getAttribute("g1");
                            }
                            if ((t1id != -1) && (g1 != null)) {
                                t1id += decId;
                             
                                //System.out.println("New Gate");
                                tt = new TOneAttribute(t1id, g1);
                                gatesTmp.add(tt);
                            } else {
                                throw new MalformedModelingException();
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
    
    public void postLoading(int decId) throws MalformedModelingException {
        //System.out.println("Post loading");
        TOneAttribute tt;
        TAttribute a;
              
        try {
            for(int i=0; i<gatesTmp.size(); i++) {
                tt = (TOneAttribute)(gatesTmp.elementAt(i));
                if (tdp.findComponentWithId(tt.t1id) != t1) {
                    throw new MalformedModelingException();
                }
                tt.t1 = t1;
                a = t1.getGateById(tt.ta1s);
                if (a == null) {
                    throw new MalformedModelingException();
                }
                tt.ta1 = a;
                gates.add(tt);
            }
        } catch (Exception e) {
            if (decId ==0) {
                throw new MalformedModelingException();
            }
        }
        gatesTmp = null;
        
    }
    
}
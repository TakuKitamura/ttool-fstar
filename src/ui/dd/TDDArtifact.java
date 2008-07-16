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
 * Class TDDArtifact
 * Artifact of a deployment diagram
 * Creation: 02/05/2005
 * @version 1.0 02/05/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.dd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;
import ui.cd.*;

public class TDDArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int textY2 =  40;
    protected int space = 5;
    protected int fileX = 15;
    protected int fileY = 20;
    protected int cran = 5;
    protected String oldValue = "";
    protected String jarValue, totalValue;
    protected String oldJarValue = "";
    protected String oldTotalValue = "";
    protected boolean userCode;
    protected boolean oldUserCode;
    
    public TDDArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 75;
        height = 50;
        minWidth = 75;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "artifact's name";
        name = "artifact";
        jarValue = "foo.jar";
        userCode = true;
        oldUserCode = true;
        
        makeFullValue();
        
        myImageIcon = IconManager.imgic702;
    }
    
    public void internalDrawing(Graphics g) {
        
        if ((oldValue.compareTo(value) != 0) || (oldTotalValue.compareTo(totalValue) != 0)) {
            setValue(value, g);
        }
        
        
        g.drawRect(x, y, width, height);
        
        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-cran, y+space);
        g.drawLine(x+width-space-cran, y+space, x+width-space, y+space + cran);
        g.drawLine(x+width-space, y+space + cran, x+width-space, y+space+fileY);
        g.drawLine(x+width-space, y+space+fileY, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-cran, y+space, x+width-space-cran, y+space+cran);
        g.drawLine(x+width-space-cran, y+space+cran, x + width-space, y+space+cran);
        
        g.drawString(value, x + textX , y + textY);
        
        g.drawString(totalValue, x + textX , y + textY2);
    }
    
    public void setValue(String val, Graphics g) {
        oldValue = value;
        oldTotalValue = totalValue;
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
        int w2 = g.getFontMetrics().stringWidth(totalValue) + 2 * textX;
        w1 = Math.max(w1, w2);
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }
    
    public void resizeWithFather() {
        if ((father != null) && (father instanceof TDDNode)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
    
    
     public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click");

        boolean ret = true;

        oldValue = value;
        oldJarValue = jarValue;
        oldUserCode = userCode;
        
        JDialogArtifact jda = new JDialogArtifact(frame, value, jarValue, userCode);
        jda.setSize(350, 250);
        GraphicLib.centerOnParent(jda);
        jda.show(); // blocked until dialog has been closed
        
        value = jda.getName();
        jarValue = jda.getJarName();
        userCode = jda.getUserCode();
        
        if ((value != null) && (jarValue != null)) {
            if (value.length() < 0) {
                value = oldValue;
            }
            if (jarValue.length() < 0) {
                jarValue = oldJarValue;
            }
            ret = true;
        } else {
            ret = false;
            value = oldValue;
            jarValue = oldJarValue;
            userCode = oldUserCode;
        }

        if (ret) {
            makeFullValue();
        }
        
        return ret;
    }
    
    private void makeFullValue() {
        totalValue = "{" + jarValue + ", ";
        totalValue += userCode ? "user code}" : "no user code}";
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.TDD_ARTIFACT;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info value=\"" + value + "\" jarValue=\"" + jarValue + "\" userCode=\"");
        if(userCode) {
            sb.append("true");
        } else {
            sb.append("false");
        }
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
            int t1id;
            String svalue = null, sjarValue = null, suserCode = null;
            
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
                            if (elt.getTagName().equals("info")) {
                                svalue = elt.getAttribute("value");
                                sjarValue = elt.getAttribute("jarValue");
                                suserCode = elt.getAttribute("userCode");
                            }
                            if (svalue != null) {
                                value = svalue;
                            } 
                            if (sjarValue != null){
                                jarValue = sjarValue;
                            }
                            if (suserCode.compareTo("true") == 0) {
                                userCode = true;
                            } else {
                                userCode = false;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        makeFullValue();
    }
    
    public DesignPanel getDesignPanel() {
        return tdp.getGUI().getDesignPanel(value);
    }
    
    public Vector getListOfATG() {
        Vector v = new Vector();
        DesignPanel dp = tdp.getGUI().getDesignPanel(value);
        
        if (dp == null) {
            return v;
        }
        
        //System.out.println("DesignPanel ok");
        LinkedList ll = dp.tcdp.getComponentList();
        ListIterator iterator = ll.listIterator();
        TGComponent tgc;
        TCDTClass tc;
        ArtifactTClassGate atg;
        Vector listGates;
        int i;
        TAttribute ta;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TCDTClass) {
                tc = (TCDTClass)tgc;
                //System.out.println("Found class = " + tc.getClassName());
                listGates = tc.getGates();
                for(i=0; i<listGates.size(); i++) {
                    ta = (TAttribute)(listGates.elementAt(i));
                    if (ta.getAccess() == TAttribute.PUBLIC) {
                        // Verify if it is already involved in a synchronization internal to the component
                        if (!dp.tcdp.isASynchronizedGate(ta)) {
                            atg = new ArtifactTClassGate(value, tc.getClassName(), ta.getId());
                            v.add(atg);
                        }
                    }
                }
            }
        }
        
        return v;
        
    }
    
    
}

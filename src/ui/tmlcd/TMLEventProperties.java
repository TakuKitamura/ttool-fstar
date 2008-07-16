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
 * Class TMLEventProperties
 * Internal component that represents a list of event properties
 * Creation: 03/11/2005
 * @version 1.0 03/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcd;

import java.awt.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class TMLEventProperties extends TGCWithoutInternalComponent {
    
    protected int nbMaxAttribute = 3;
    protected TType list[];
    protected String eventName;
    protected int h;
    protected String valueOCL;
    protected int maxSamples = 4;
    protected boolean isFinite = false;
    protected boolean isBlocking = false;

    
    public TMLEventProperties(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        moveable = true;
        editable = true;
        removable = false;
        
        eventName = "evt";
        list = new TType[nbMaxAttribute];
        for(int i=0; i<nbMaxAttribute; i++) {
            list[i] = new TType();
        }
        
        makeValue();
        
        myImageIcon = IconManager.imgic302;
    }
    
    
    
    public void internalDrawing(Graphics g) {
        if (((TMLTaskDiagramPanel)(tdp)).areEventsVisible()) {
            ColorManager.setColor(g, getState(), 0);
            //System.out.println("value=" + value);
            h = g.getFontMetrics().getHeight();
            g.drawString(valueOCL, x, y + h);
            
            if (!tdp.isScaled()) {
                width = g.getFontMetrics().stringWidth(valueOCL);
                width = Math.max(minWidth, width);
                height  = h;
            }
        }
        
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getMycurrentMinY() {
        return Math.min(y, y - h + 2);
    }
    
    public int getMycurrentMaxY() {
        return Math.min(y, y - h + 2 + height);
    }
    
    
    
    public void makeValue() {
        valueOCL = "{" + getEventName() + "(" + getAttributeString() + "); ";
        if (!isFinite) {
           valueOCL += "infinite FIFO";
        } else {
          valueOCL += "finite FIFO(" + maxSamples + ")";
          if (isBlocking) {
	          valueOCL += "/blocking";
          } else {
	          valueOCL += "/non-blocking";
          }
        }
        valueOCL += "}";
        value = valueOCL;
        //System.out.println("Make value=" + value + " valueOCL=" + valueOCL);
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = valueOCL;
        int oldSample = maxSamples;
        JDialogParamFIFO jda = new JDialogParamFIFO(eventName, list[0].getType(), list[1].getType(), list[2].getType(), isFinite, isBlocking, ""+maxSamples, frame, "Setting properties");
        jda.setSize(350, 350);
        GraphicLib.centerOnParent(jda);
        jda.show(); // blocked until dialog has been closed
        
        if (jda.hasNewData()) {
          try {
            maxSamples = Integer.decode(jda.getMaxSamples()).intValue();
            if (maxSamples < 1) {
               maxSamples = oldSample;
               JOptionPane.showMessageDialog(frame, "Non valid value: " + maxSamples + ": Should be at least 1", "Error", JOptionPane.INFORMATION_MESSAGE);
               return false;
            }
            isFinite = jda.isFinite();
            isBlocking = jda.isBlocking();
            eventName = jda.getParamName();
            for(int i=0; i<nbMaxAttribute; i++) {
                list[i].setType(jda.getType(i));
            }
          } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Non valid value: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
          }
        }
        
        makeValue();
        
        if (!oldValue.equals(valueOCL)) {
            return true;
        }
        
        return false;
    }
    
    protected String translateExtraParam() {
        TType a;
        //String val = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Prop name=\"");
        sb.append(getEventName());
        sb.append("\" finite=\"");
        if (isFinite) {
           sb.append("true");
        } else {
          sb.append("false");
        }
        sb.append("\" blocking=\"");
        if (isBlocking) {
           sb.append("true");
        } else {
          sb.append("false");
        }
        sb.append("\" maxSamples=\"" + maxSamples);
        sb.append("\" />\n");
        for(int i=0; i<nbMaxAttribute; i++) {
            //System.out.println("Attribute:" + i);
            a = list[i];
            //System.out.println("Attribute:" + i + " = " + a.getId());
            //val = val + a + "\n";
            sb.append("<Type");
            sb.append(" type=\"");
            sb.append(a.getType());
            sb.append("\" typeOther=\"");
            sb.append(a.getTypeOther());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //int access;
            int type;
            String typeOther;
            //String id, valueAtt;

            int nbAttribute = 0;
            
            //System.out.println("Loading attributes");
            //System.out.println(nl.toString());
            
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
                            if ((elt.getTagName().equals("Type")) && (nbAttribute < nbMaxAttribute)) {
                                //System.out.println("Analyzing attribute");
                                type = Integer.decode(elt.getAttribute("type")).intValue();
                                try {
                                    typeOther = elt.getAttribute("typeOther");
                                } catch (Exception e) {
                                    typeOther = "";
                                }
                                
                                TType ta = new TType(type, typeOther);
                                list[nbAttribute] = ta;
                                nbAttribute ++;
                                
                            }
                            
                            if (elt.getTagName().equals("Prop")) {
                                eventName = elt.getAttribute("name");
                                isFinite = (elt.getAttribute("finite").compareTo("true") ==0);
                                try {
                                    maxSamples = Integer.decode(elt.getAttribute("maxSamples")).intValue();
                                } catch (Exception e) {}
                                try {
                                    isBlocking = (elt.getAttribute("blocking").compareTo("true") ==0);
                                } catch (Exception e) {}
                                
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
    
    public String getEventName() {
        return eventName;
    }
    
    public boolean isFinite() {
           return isFinite;
    }
    
    public boolean isBlocking() {
           return isBlocking;
    }
    
    public int getMaxSamples() {
           return maxSamples;
    }
    
    public String getAttributeString() {
        TType tt;
        String s = "";
        int nb = 0;
        for(int i=0; i<nbMaxAttribute; i++) {
            //System.out.println("Attribute:" + i);
            tt = list[i];
            if (tt.getType() != TType.NONE) {
                if (nb != 0) {
                    s +=", ";
                }
                s +=  tt.toString();
                nb ++;
            }
        }
        //System.out.println("Returning=" + s);
        return s;
    }
    
    public int getNbMaxParam() {
        return nbMaxAttribute;
    }
    
    public TType getParamAt(int _index) {
        if (_index < nbMaxAttribute) {
            return list[_index];
        } else {
            return null;
        }
    }
    
}
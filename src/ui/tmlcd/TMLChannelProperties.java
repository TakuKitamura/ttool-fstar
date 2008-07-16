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
 * Class TMLChannelProperties
 * Internal component that represents a list of channel properties
 * Creation: 28/10/2005
 * @version 1.0 28/10/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcd;

import java.awt.*;
//import java.awt.geom.*;
//import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class TMLChannelProperties extends TGCWithoutInternalComponent {
    
    public static final int BRBW = 0;
    public static final int BRNBW = 1;
    public static final int NBRNBW = 2;
    
    protected String channelName;
    protected int type;
    protected int size;
    protected int maxElt;

    protected int minWidth = 10;
    protected int minHeight = 15;
    protected int h;


    public TMLChannelProperties(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        moveable = true;
        editable = true;
        removable = false;

        type = 0;
        size = 4;
        maxElt = 8;
        channelName = "channel";
        makeValue();
        
        myImageIcon = IconManager.imgic302;
    }
    
    
    public void internalDrawing(Graphics g) {
        if (((TMLTaskDiagramPanel)(tdp)).areChannelsVisible()) {
            ColorManager.setColor(g, getState(), 0);
            //System.out.println("value=" + value);
            h = g.getFontMetrics().getHeight();
            g.drawString(value, x, y + h);
            
            if (!tdp.isScaled()) {
                width = g.getFontMetrics().stringWidth(value);
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
      if (type == BRBW) {
        value = "{" + getChannelName() + ", " + getChannelSize() + ", " + getChannelMax() + ", " + getStringChannelType() + "}";
      } else {
        value = "{" + getChannelName() + ", " + getChannelSize() + ", " + getStringChannelType() + "}";
      }
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        
        String oldValue = value;
        String oldName = channelName;
        int oldType = type;
        int oldSize = size;
        int oldMax = maxElt;
        JDialogChannel jda = new JDialogChannel(channelName, size, type, maxElt, frame, "Setting channel's properties");
        jda.setSize(350, 300);
        GraphicLib.centerOnParent(jda);
        jda.show(); // blocked until dialog has been closed
        
        if (jda.hasNewData()) {
            try {
                size = Integer.decode(jda.getSizeText()).intValue();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid size", "Value error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            try {
                maxElt = Integer.decode(jda.getMaxText()).intValue();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid max size", "Value error", JOptionPane.INFORMATION_MESSAGE);
                size = oldSize;
                return false;
            }
            channelName = jda.getChannelName();
            type = jda.getType();
        }
        
        makeValue();
        
        if (!oldValue.equals(value)) {
            return true;
        }
        
        return false;
        
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Prop type=\"");
        sb.append(getChannelType());
        sb.append("\" name=\"");
        sb.append(getChannelName());
        sb.append("\" size=\"");
        sb.append(getChannelSize());
        sb.append("\" maxElt=\"");
        sb.append(getChannelMax());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro *** " + getId());
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            
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
                            if (elt.getTagName().equals("Prop")) {
                                type = Integer.decode(elt.getAttribute("type")).intValue();
                                size = Integer.decode(elt.getAttribute("size")).intValue();
                                //System.out.println("before error?");
                                if (elt.getAttribute("maxElt").toString().compareTo("") != 0) { // test for compatibility with older versions
                                   maxElt = Integer.decode(elt.getAttribute("maxElt")).intValue();
                                }
                                //System.out.println("after error?");
                                channelName = elt.getAttribute("name");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
          //System.out.println("Exception ...");
            throw new MalformedModelingException();
        }
        makeValue();
    }
    
    public int getChannelType() {
        return type;
    }
    
    public String getStringChannelType() {
        return getStringChannelType(getChannelType());
    }
    
    public static String getStringChannelType(int ty) {
        switch(ty) {
            case BRBW:
                return "BR-BW";
            case BRNBW:
                return "BR-NBW";
            case NBRNBW:
                return "NBR-NBW";
            default:
                return "unknown type";
        }
    }
    
    public int getChannelSize() {
        return size;
    }
    
    public int getChannelMax() {
        return maxElt;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
}
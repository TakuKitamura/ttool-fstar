/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package ui.avatarsmd;

import myutil.Conversion;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAvatarState;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
   * Class AvatarSMDState
   * State. To be used in AVATAR State Machine Diagrams
   * Creation: 13/04/2010
   * @version 1.1 13/04/2010
   * @author Ludovic APVRILLE
 */
public class AvatarSMDState extends TGCScalableWithInternalComponent implements AllowedBreakpoint, CheckableAccessibility, CheckableLatency, CheckableInvariant, SwallowTGComponent, SwallowedTGComponent, PartOfInvariant, PartOfHighInvariant, WithAttributes {
    //private static String GLOBAL_CODE_INFO = "(global code)";
    private static String ENTRY_CODE_INFO = "(entry code)";

    private int textY1 = 3;

    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    private int textX = 7;

    //protected String [] globalCode;
    protected String [] entryCode;


    // Security
    public final static int NOT_VERIFIED = 0;
    public final static int REACHABLE = 1;
    public final static int NOT_REACHABLE = 2;

    protected int securityInformation;

    public String oldValue;

    protected Vector<AvatarSMDState> mutexStates;

    public AvatarSMDState(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 100;
        height = 50;
        minWidth = 40;
        minHeight = 30;

        nbConnectingPoint = 32;
        connectingPoint = new TGConnectingPoint[32];

        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);

        connectingPoint[8] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);

        connectingPoint[16] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.12, 0.0);
        connectingPoint[17] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.37, 0.0);

        connectingPoint[18] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.62, 0.0);
        connectingPoint[19] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.87, 0.0);

        connectingPoint[20] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.12);
        connectingPoint[21] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.37);

        connectingPoint[22] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.12);
        connectingPoint[23] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.37);

        connectingPoint[24] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.62);
        connectingPoint[25] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.0, 0.87);

        connectingPoint[26] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.62);
        connectingPoint[27] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 1.0, 0.87);

        connectingPoint[28] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.12, 1.0);
        connectingPoint[29] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.37, 1.0);

        connectingPoint[30] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.62, 1.0);
        connectingPoint[31] = new AvatarSMDConnectingPoint(this, 0, 0, true, true, 0.87, 1.0);


        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        multieditable = true;
        removable = true;
        userResizable = true;

        name = tdp.findAvatarSMDStateName("state");
        setValue(name);
        //name = "State";
        oldValue = value;

        currentFontSize = maxFontSize;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic700;

        //actionOnAdd();
    }

    public void addMutexState(AvatarSMDState state) {
        if (mutexStates == null) {
            mutexStates = new Vector<AvatarSMDState>();
        }

        mutexStates.add(state);
    }

    public void reinitMutualExclusionStates() {
        resetMutexStates();

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDState) {
                ((AvatarSMDState)tgcomponent[i]).reinitMutualExclusionStates();
            }
        }

    }

    public void resetMutexStates() {
        mutexStates = null;
    }

    @Override
    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        Font fold = f;

        f = f.deriveFont(minFontSize);
        //

        if ((rescaled) && (!tdp.isScaled())) {

            if (currentFontSize == -1) {
                currentFontSize = f.getSize();
            }
            rescaled = false;
            // Must set the font size ..
            // Find the biggest font not greater than max_font size
            // By Increment of 1
            // Or decrement of 1
            // If font is less than 4, no text is displayed

            int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
            int w0;//, w1, w2;
            f = f.deriveFont((float)maxCurrentFontSize);
            g.setFont(f);
            //
            while(maxCurrentFontSize > (minFontSize-1)) {
                w0 = g.getFontMetrics().stringWidth(value);
                if (w0 < (width - (2*textX))) {
                    break;
                }
                maxCurrentFontSize --;
                f = f.deriveFont((float)maxCurrentFontSize);
                g.setFont(f);
            }
            currentFontSize = maxCurrentFontSize;

            if(currentFontSize <minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                f = f.deriveFont((float)currentFontSize);
                g.setFont(f);
            }

        }

        Color c = g.getColor();
        //g.setColor(ColorManager.AVATAR_STATE);
        
        // Issue #69
    	if ( isEnabled() ) {
            Color avat = ColorManager.AVATAR_STATE;
    		g.setColor(new Color(avat.getRed(), avat.getGreen(), Math.min(255, avat.getBlue() + (getMyDepth() * 10))));
    	}
    	else {
	    	g.setColor( ColorManager.DISABLED_FILLING );
    	}

        g.fillRoundRect(x, y, width, height, 5, 5);
        g.setColor(c);
        g.drawRoundRect(x, y, width, height, 5, 5);

        // Strings
        int w;
        int h = 0;
        if (displayText) {
            f = f.deriveFont((float)currentFontSize);
            Font f0 = g.getFont();
            g.setFont(f.deriveFont(Font.BOLD));

            w = g.getFontMetrics().stringWidth(value);
            h =  currentFontSize + (int)(textY1 * tdp.getZoom());
            if ((w < (2*textX + width)) && (h < height)) {
                g.drawString(value, x + (width - w)/2, y +h);
            }


            g.setColor(ColorManager.AVATAR_CODE);
            int step = h + h;
            /*if (hasGlobalCode()) {
              w = g.getFontMetrics().stringWidth(GLOBAL_CODE_INFO);
              if ((w < (2*textX + width)) && (step + 1 < height)) {
              g.drawString(GLOBAL_CODE_INFO, x + (width - w)/2, y +step);
              }
              step = step + h;
              }*/
            if (hasEntryCode()) {
                w = g.getFontMetrics().stringWidth(ENTRY_CODE_INFO);
                if ((w < (2*textX + width)) && (step + 1 < height)) {
                    g.drawString(ENTRY_CODE_INFO, x + (width - w)/2, y +step);
                }
                step = step + h;
            }
            g.setColor(c);


            g.setFont(f0);
        }

        g.setFont(fold);

        h = h +2;
        if (h < height) {
            g.drawLine(x, y+h, x+width, y+h);
        }

        // Icon

        g.setFont(fold);

        /*if ((mutexStates != null) && (state == TGState.POINTER_ON_ME)){
          String s = "Mutually exclusive states:\n";
          for(AvatarSMDState st: mutexStates) {
          s += st.getTDiagramPanel().getName() + "/" + st.getStateName() + "\n";
          }
          drawAttributes(g, s);
          }*/

        drawSecurityInformation(g);
    }

    private void drawSecurityInformation(Graphics g) {
        if (securityInformation > 0) {

            Color c = g.getColor();
            Color c1;
            switch(securityInformation) {
            case REACHABLE:
                c1 = Color.green;
                break;
            case NOT_REACHABLE:
                c1 = Color.red;
                break;
            default:
                return;
            }

            GraphicLib.arrowWithLine(g, 1, 0, 10, x-30, y+4, x-15, y+4, true);
            g.drawOval(x-11, y-3, 7, 9);
            g.setColor(c1);
            g.fillRect(x-12, y, 9, 7);
            g.setColor(c);
            g.drawRect(x-12, y, 9, 7);

        }
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public String getStateName() {
        return value;
    }

    @Override
    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
        oldValue = value;

        //String text = getName() + ": ";
        /*String s = (String)JOptionPane.showInputDialog(frame, "State name",
          "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
          null,
          getValue());*/

        JDialogAvatarState jdas = new JDialogAvatarState(frame, "Setting state parameters", value, entryCode);
       // jdas.setSize(600, 550);
        GraphicLib.centerOnParent(jdas, 600, 550 );
        jdas.setVisible( true ); // blocked until dialog has been closed


        if (jdas.hasBeenCancelled()) {
            return false;
        }

        String s = jdas.getStateName();

        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false, false)) {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the state: the new name is not a valid name",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            /*if (!tdp.isStateNameUnique(s)) {
              JOptionPane.showMessageDialog(frame,
              "Could not change the name of the state: the new name is already in use",
              "Error",
              JOptionPane.INFORMATION_MESSAGE);
              return false;
              }*/

            setValue(s);
            recalculateSize();

            /*if (tdp.actionOnDoubleClick(this)) {
              return true;
              } else {
              JOptionPane.showMessageDialog(frame,
              "Could not change the name of the Block: this name is already in use",
              "Error",
              JOptionPane.INFORMATION_MESSAGE);
              setValue(oldValue);
              }*/
        }

        //globalCode = jdas.getGlobalCode();
        entryCode =  jdas.getEntryCode();

        return true;
    }

    @Override
    public void recalculateSize() {
        width = Math.max(width, value.length()*11);
    }

    @Override
    public int getType() {
        return TGComponentManager.AVATARSMD_STATE;
    }

    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        if (tgc instanceof AvatarSMDBasicComponent) {
            return true;
        }

        return tgc instanceof AvatarSMDState;

    }

    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        boolean swallowed = false;

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SwallowTGComponent) {
                if (tgcomponent[i].isOnMe(x, y) != null) {
                    swallowed = true;
                    ((SwallowTGComponent)tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
                    break;
                }
            }
        }

        if (swallowed) {
            return true;
        }

        if (!acceptSwallowedTGComponent(tgc)) {
            return false;
        }

        //
        // Choose its position

        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);

        //Set its coordinates
        if (tgc instanceof AvatarSMDBasicComponent) {
            tgc.resizeWithFather();
        }

        if (tgc instanceof AvatarSMDState) {
            tgc.resizeWithFather();
        }

        // else unknown*/

        //add it
        addInternalComponent(tgc, 0);

        return true;
    }

    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeMyInternalComponent(tgc, false);
    }

    public boolean removeMyInternalComponent(TGComponent tgc, boolean actionOnRemove) {
        //TGComponent tgc;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                nbInternalTGComponent = nbInternalTGComponent - 1;
                if (nbInternalTGComponent == 0) {
                    tgcomponent = null;
                } else {
                    TGComponent [] tgcomponentbis = new TGComponent[nbInternalTGComponent];
                    for(int j=0; j<nbInternalTGComponent; j++) {
                        if (j<i) {
                            tgcomponentbis[j] = tgcomponent[j];
                        }
                        if (j>=i) {
                            tgcomponentbis[j] = tgcomponent[j+1];
                        }
                    }
                    tgcomponent = tgcomponentbis;
                }
                if (actionOnRemove) {
                    tgc.actionOnRemove();
                    tdp.actionOnRemove(tgc);
                }
                return true;
            } else {
                if (tgcomponent[i] instanceof AvatarSMDState) {
                    if (((AvatarSMDState)tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDBasicComponent) {
                tgcomponent[i].resizeWithFather();
            }
            if (tgcomponent[i] instanceof AvatarSMDState) {
                tgcomponent[i].resizeWithFather();
            }
        }

        if (getFather() != null) {
            resizeWithFather();
        }

    }

    @Override
    public void resizeWithFather() {

        if ((father != null) && (father instanceof AvatarSMDState)) {
            // Too large to fit in the father? -> resize it!
            resizeToFatherSize();

            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }

    public List<AvatarSMDState> getStateList() {
        List<AvatarSMDState> list = new LinkedList<AvatarSMDState>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDState) {
                list.add((AvatarSMDState)(tgcomponent[i]));
            }
        }
        return list;
    }

    public List<AvatarSMDState> getFullStateList() {
        List<AvatarSMDState> list = new LinkedList<AvatarSMDState>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDState) {
                list.add((AvatarSMDState)(tgcomponent[i]));
                list.addAll(((AvatarSMDState)tgcomponent[i]).getFullStateList());
            }
        }
        return list;
    }

    public boolean hasInternalStateWithName(String name) {
        List<AvatarSMDState> list  = getFullStateList();
        for(AvatarSMDState s: list) {
            if (s.getValue().compareTo(name) ==0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.AVATARSMD_CONNECTOR;
    }

    public AvatarSMDState checkForStartStateOfCompositeStates() {
        AvatarSMDState tgc;
        List<AvatarSMDState> list  = getFullStateList();
        for(AvatarSMDState s: list) {
            tgc = s.checkForStartStateOfCompositeStates();
            if (tgc != null) {
                return tgc;
            }
        }

        int cpt = 0;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDStartState) {
                cpt ++;
            }
        }

        if (cpt > 1) {
            return this;
        }
        
        return null;
    }

    public boolean isACompositeState() {
        return (nbInternalTGComponent > 0);
    }

    public String getAttributes() {
        if (mutexStates == null) {
            return null;
        }

        String s = "Mutually exclusive states:\n";
        for(AvatarSMDState st: mutexStates) {
            s += "  " + st.getTDiagramPanel().getName() + "/" + st.getStateName() + "\n";
        }

        return s;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");

        /*if (hasGlobalCode()) {
          for(int i=0; i<globalCode.length; i++) {
          sb.append("<globalCode value=\"");
          sb.append(GTURTLEModeling.transformString(globalCode[i]));
          sb.append("\" />\n");
          }
          }*/

        if (hasEntryCode()) {
            for(int i=0; i<entryCode.length; i++) {
                sb.append("<entryCode value=\"");
                sb.append(GTURTLEModeling.transformString(entryCode[i]));
                sb.append("\" />\n");
            }
        }

        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        //String tmpGlobalCode = "";
        String tmpEntryCode = "";

        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element)n2;


                            /*if (elt.getTagName().equals("globalCode")) {
                            //
                            s = elt.getAttribute("value");
                            if (s.equals("null")) {
                            s = "";
                            }
                            tmpGlobalCode += GTURTLEModeling.decodeString(s) + "\n";
                            }*/

                            if (elt.getTagName().equals("entryCode")) {
                                //
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                tmpEntryCode += GTURTLEModeling.decodeString(s) + "\n";
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }

        /*if (tmpGlobalCode.trim().length() == 0) {
          globalCode = null;
          } else {
          globalCode = Conversion.wrapText(tmpGlobalCode);
          }*/
        if (tmpEntryCode.trim().length() == 0) {
            entryCode = null;
        } else {
            entryCode = Conversion.wrapText(tmpEntryCode);
            //TraceManager.addDev("Entry code = " + entryCode);
        }
    }

    /*public boolean hasGlobalCode() {
      if (globalCode == null) {
      return false;
      }

      if (globalCode.length == 0) {
      return false;
      }

      String tmp;
      for(int i=0; i<globalCode.length; i++) {
      tmp = globalCode[i].trim();
      if (tmp.length()>0) {
      if (!(tmp.equals("\n"))) {
      return true;
      }
      }
      }

      return false;
      }*/

    public boolean hasEntryCode() {
        if (entryCode == null) {
            return false;
        }

        if (entryCode.length == 0) {
            return false;
        }

        String tmp;
        for(int i=0; i<entryCode.length; i++) {
            tmp = entryCode[i].trim();
            if (tmp.length()>0) {
                if (!(tmp.equals("\n"))) {
                    return true;
                }
            }
        }

        return false;
    }

    /*public String getGlobalCode() {
      if (globalCode == null) {
      return null;
      }
      String ret = "";
      for(int i=0; i<globalCode.length; i++) {
      ret += globalCode[i] + "\n";
      }
      return ret;
      }*/

    public String getEntryCode() {
        if (entryCode == null) {
            return null;
        }
        String ret = "";
        for(int i=0; i<entryCode.length; i++) {
            ret += entryCode[i] + "\n";
        }
        return ret;
    }

    public void resetSecurityInfo() {
        securityInformation = NOT_VERIFIED;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDState) {
                ((AvatarSMDState)tgcomponent[i]).resetSecurityInfo();
            }
        }

    }

    public void setSecurityInfo(int _info, String _name) {
        //TraceManager.addDev("Testing " + getValue() + " with state " + _name);
        if (getValue().compareTo(_name) == 0) {
            //TraceManager.addDev("Setting state " + _name + " as info=" + _info);
            securityInformation = _info;
        }
        // FIXME: does it really work? Name comes in the form "s1__s2__s3" and we compare only to "s1", "s2" and "s3"
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDState) {
                ((AvatarSMDState)tgcomponent[i]).setSecurityInfo(_info, _name);
            }
        }
    }

	/* Issue #69
	 * (non-Javadoc)
	 * @see ui.AbstractCDElement#canBeDisabled()
	 */
	@Override
    public boolean canBeDisabled() {
		if ( getFather() instanceof AvatarSMDState ) {
			return getFather().isEnabled();
		}
		
    	return true;
    }
	
	private List<AvatarSMDConnector> getContainedConnectors() {
		final List<AvatarSMDConnector> connectors = new ArrayList<AvatarSMDConnector>();
		
    	for ( final TGComponent component : tdp.getComponentList() ) {
    		if ( component instanceof AvatarSMDConnector ) {
    			final AvatarSMDConnector smdCon = (AvatarSMDConnector) component;
    			
    			if ( smdCon.isContainedBy( this ) ) {
    				connectors.add( smdCon );
    			}
    		}
    	}
    	
    	return connectors;
	}
	
    /**
     * Issue #69
     * @param _enabled boolean data
     */
    @Override
    public void setEnabled( final boolean _enabled ) {
    	if ( _enabled ) { 
        	super.setEnabled( _enabled );
    	}
    	
    	// Enabling for composite states
    	for ( final TGComponent component : tgcomponent ) {
    		if ( component.canBeDisabled() ) {
    			component.setEnabled( _enabled );
    		}
    	}
    	
    	for ( final AvatarSMDConnector containedConnector : getContainedConnectors() ) {
    		containedConnector.getAvatarSMDTransitionInfo().setEnabled( _enabled );
    	}

    	if ( !_enabled ) { 
        	super.setEnabled( _enabled );
    	}
    	
    	// Enabling of states with the same name
    	for ( final AvatarSMDState sameState : getSameStates() ) {
    		sameState.doSetEnabled( _enabled );
    	}
    }
    
    public List<AvatarSMDState> getSameStates() {
    	final List<AvatarSMDState> states = new ArrayList<AvatarSMDState>();
    	
    	for ( final TGComponent component : tdp.getComponentList() ) {
    		if ( 	component != this && getValue() != null && component instanceof AvatarSMDState && 
    				getFather() == component.getFather() && getValue().equals( component.getValue() ) ) {
    			states.add( (AvatarSMDState) component );
    		}
    	}
    	
    	return states;
    }
    
    public AvatarSMDStartState getCompositeStartState() {
		if ( tgcomponent ==  null ) {
			return null;
		}
		
		for ( final TGComponent subCompo : tgcomponent ) {
			if ( subCompo instanceof AvatarSMDStartState ) {
				return (AvatarSMDStartState) subCompo;
			}
		}

		return null;
    }
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptForward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptForward( final ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			final AvatarSMDStartState subStartElement = getCompositeStartState();
			
			if ( subStartElement != null ) {
				subStartElement.acceptForward( visitor );
			}
			
			if ( connectingPoint !=  null ) {
				for ( final TGConnectingPoint point : connectingPoint ) {
					final TGConnector connector = getConnectorConnectedTo( point );
					
					if ( connector != null && point == connector.getTGConnectingPointP1() ) {
						point.acceptForward( visitor );
					}
				}
			}
			
			// Also visit the states with the same name
			for ( final AvatarSMDState state : getSameStates() ) {
				state.acceptForward( visitor );
			}
		}
	}
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptBackward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptBackward( final ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			if ( connectingPoint !=  null ) {
				for ( final TGConnectingPoint point : connectingPoint ) {
					final TGConnector connector = getConnectorConnectedTo( point );
					
					if ( connector != null && point == connector.getTGConnectingPointP2() ) {
						point.acceptBackward( visitor );
					}
				}
			}
		}
		
		// Also visit the states with the same name
		for ( final AvatarSMDState state : getSameStates() ) {
			state.acceptBackward( visitor );
		}
	}
}

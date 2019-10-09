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

package ui.avatarbd;

import myutil.Conversion;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import proverifspec.ProVerifResultTrace;
import proverifspec.ProVerifResultTraceStep;
import ui.*;
import ui.interactivesimulation.JFrameSimulationSDPanel;
import ui.util.IconManager;
import ui.window.JDialogPragma;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Class Pragma
 * Like a Note but with Pragma
 * Creation: 06/12/2003
 *
 * @author Ludovic APVRILLE, Letitia LI
 * @version 1.0 06/12/2003
 */
public class AvatarBDPragma extends TGCScalableWithoutInternalComponent {

    protected String[] values;
    protected List<String> models;
    protected List<String> properties;
    public List<String> syntaxErrors;
    protected int textX = 25;
    protected int textY = 5;
    protected int marginY = 20;
    protected int marginX = 20;
    protected int limit = 15;
    protected int lockX = 1;
    protected int lockY = 5;
    protected Graphics myg;

    protected Color myColor;

    private Font myFont;//, myFontB;
    // private int maxFontSize = 30;
    // private int minFontSize = 4;
    private int currentFontSize = -1;
    private final String[] mPragma = {"#PublicConstant", "#PrivateConstant", "#InitialSystemKnowledge", "#InitialSessionKnowledge", "#PrivatePublicKeys", "#Public"};
    private final String[] pPragma = {"#Confidentiality", "#Secret", "#SecrecyAssumption", "#Authenticity"};
    protected Graphics graphics;
    public final static int NOT_VERIFIED = 0;
    public final static int PROVED_TRUE = 1;
    public final static int PROVED_FALSE = 2;
    public final static int NOT_PROVED = 3;
    public HashMap<String, Integer> authStrongMap = new HashMap<String, Integer>();
    public HashMap<String, Integer> authWeakMap = new HashMap<String, Integer>();

    protected HashMap<String, Integer> pragmaLocMap = new HashMap<String, Integer>();

    public HashMap<String, ProVerifResultTrace> pragmaTraceMap = new HashMap<String, ProVerifResultTrace>();

    public AvatarBDPragma(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 200;
        height = 30;
        minWidth = 80;
        minHeight = 10;
        models = new LinkedList<String>();
        properties = new LinkedList<String>();
        authStrongMap = new HashMap<String, Integer>();
        authWeakMap = new HashMap<String, Integer>();
        oldScaleFactor = tdp.getZoom();

        nbConnectingPoint = 0;
        //addTGConnectingPointsComment();
        int len = makeTGConnectingPointsComment(16);
        int decw = 0;
        int dech = 0;
        for (int i = 0; i < 2; i++) {
            connectingPoint[len] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
            connectingPoint[len + 1] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
            connectingPoint[len + 2] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
            connectingPoint[len + 3] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.5 + dech);
            connectingPoint[len + 4] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.5 + dech);
            connectingPoint[len + 5] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
            connectingPoint[len + 6] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 1.0 + dech);
            connectingPoint[len + 7] = new TGConnectingPointComment(this, 0, 0, true, true, 0.9 + decw, 1.0 + dech);
            len += 8;
        }

        moveable = true;
        editable = true;
        removable = true;
        syntaxErrors = new ArrayList<String>();
        name = "Proverif Pragma";
        value = "";

        myImageIcon = IconManager.imgic6000;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] v) {
        values = v;
        makeValue();
    }

    public List<String> getProperties() {
        return properties;
    }

    public List<String> getModels() {
        return this.models;
    }

    @Override
    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        Font fold = f;

        /*if (!tdp.isScaled()) {
          graphics = g;
          }*/

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize() + 1;
            //
            //            myFont = f.deriveFont((float)currentFontSize);
            //myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        if (values == null) {
            makeValue();
        }

//        int h  = g.getFontMetrics().getHeight();
        Color c = g.getColor();

        /* !!! WARNING !!!
         * Note that here we use TDiagramPanel.stringWidth instead of graph.getFontMetrics().stringWidth
         * Indeed, TGComponent (and so TGCNote) objects are drawn twice when the bird view is enabled.
         * First, they are drawn on the TDiagramPanel and then on the bird view.
         * Problem is that we compute the width for this element (which will be further used for isOnMe
         * for instance) so that it matches the text inside of it. It is thus important that the width of
         * the strings - that will affect the width of the component - is the same each time it is drawn.
         * For some unknown reasons, even if the current Font of the Graphics object is the same, the
         * FontMetrics object derived from it is not the same (ascent and descent are different) so for
         * the same text and the same Font, width would still change if we used the FontMetrics fetched
         * from the Graphics object.
         * Thus we use a saved FontMetrics object in TDiagramPanel that only changes when zoom changes.
         */
        if (!(this.tdp.isScaled())) {
            int desiredWidth = Math.max(this.minWidth, 2 * this.tdp.stringWidth(g, "Property Pragmas") + marginX + textX);
            for (int i = 0; i < values.length; i++)
                desiredWidth = Math.max(desiredWidth, this.tdp.stringWidth(g, values[i]) + marginX + textX);

            //	currentFontSize= 5;
            int desiredHeight = ((models.size() + properties.size() + 4) * currentFontSize) + textY + 1;

            //TraceManager.addDev("resize: " + desiredWidth + "," + desiredHeight);

            if ((desiredWidth != width) || (desiredHeight != height)) {
                resize(desiredWidth, desiredHeight);
            }
        }

        g.drawLine(x, y, x + width, y);
        g.drawLine(x, y, x, y + height);
        g.drawLine(x, y + height, x + width - limit, y + height);
        g.drawLine(x + width, y, x + width, y + height - limit);

        g.setColor(ColorManager.PRAGMA_BG);

        int[] px1 = {x + 1, x + width, x + width, x + width - limit, x + 1};
        int[] py1 = {y + 1, y + 1, y + height - limit, y + height, y + height};
        g.fillPolygon(px1, py1, 5);
        g.setColor(c);

        int[] px = {x + width, x + width - 4, x + width - 10, x + width - limit};
        int[] py = {y + height - limit, y + height - limit + 3, y + height - limit + 2, y + height};
        g.drawPolygon(px, py, 4);

        if (g.getColor() == ColorManager.NORMAL_0) {
            g.setColor(ColorManager.PRAGMA);
        }
        g.fillPolygon(px, py, 4);

        g.setColor(c);

        int i = 1;
        Font heading = new Font("heading", Font.BOLD, this.tdp.getFontSize() * 7 / 6);
        g.setFont(heading);
        drawSingleString(g, "Security features", x + textX, y + textY + currentFontSize);
        g.setFont(fold);
        for (String s : models) {
            pragmaLocMap.put(s, y + textY + (i + 1) * currentFontSize);
            drawSingleString(g, s, x + textX, y + textY + (i + 1) * currentFontSize);
            if (syntaxErrors.contains(s)) {
                Color ctmp = g.getColor();
                g.setColor(Color.red);
                g.drawLine(x + textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + width - textX / 2, y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.drawLine(x + width - textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + textX / 2, y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.setColor(ctmp);
            }
            i++;
        }
        // FIXME: why the empty string ? 
        //I forget...
        drawSingleString(g, " ", x + textX, y + textY + (i + 1) * currentFontSize);
        i++;
        g.drawLine(x, y + textY / 2 + i * currentFontSize, x + width, y + textY / 2 + i * currentFontSize);
        g.setFont(heading);
        drawSingleString(g, "Security Property", x + textX, y + textY + (i + 1) * currentFontSize);
        g.setFont(fold);
        i++;
//		
        for (String s : properties) {
            if (authStrongMap.containsKey(s) || authWeakMap.containsKey(s)) {
                g.setFont(new Font("tmp", Font.PLAIN, 7));
                drawConfidentialityVerification(s, g, x + lockX, y + lockY + (i + 1) * currentFontSize);
                g.setFont(fold);
            }
            drawSingleString(g, s, x + textX, y + textY + (i + 1) * currentFontSize);
            pragmaLocMap.put(s, y + textY + i * currentFontSize);
            if (syntaxErrors.contains(s)) {
                Color ctmp = g.getColor();
                g.setColor(Color.red);
                g.drawLine(x + textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + width - textX / 2, y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.drawLine(x + width - textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + textX / 2, y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.setColor(ctmp);
            }
            i++;
        }

/*        for (int i = 0; i<values.length; i++) {
            //TraceManager.addDev("x+texX=" + (x + textX) + " y+textY=" + y + textY + i* h + ": " + values[i]);
            drawSingleString(g, values[i], x + textX, y + textY + (i+1)* currentFontSize);
        }
*/
        g.setColor(c);
    }

    public void makeValue() {
        values = Conversion.wrapText(value);
        models.clear();
        properties.clear();
        for (String s : values) {
            if (s.isEmpty() || s.split(" ").length < 1) {
                //Ignore
            } else if (Arrays.asList(mPragma).contains(s.split(" ")[0])) {
                models.add(s);
            } else if (Arrays.asList(pPragma).contains(s.split(" ")[0])) {
                properties.add(s);
            } else {
                //Pretend it's a model pragma
                models.add(s);
                //Warning Message
                //Do not show this: //JOptionPane.showMessageDialog(null, s + " is not a valid pragma.", "Invalid Pragma",
                //              JOptionPane.INFORMATION_MESSAGE);
            }
        }
        //checkMySize();
    }

    /*public void checkMySize() {
      if (myg == null) {
      return;
      }
      int desiredWidth = minWidth;
      for(int i=0; i< values.length; i++) {
      desiredWidth = Math.max(desiredWidth, myg.getFontMetrics().stringWidth(values[i]) + marginX);
      }

      int desiredHeight = values.length * myg.getFontMetrics().getHeight() + marginY;

      if ((desiredWidth != width) || (desiredHeight != height)) {
      resize(desiredWidth, desiredHeight);
      }
      }*/
    private void drawConfidentialityVerification(String s, Graphics g, int _x, int _y) {
        Color c = g.getColor();
        Color c1;
        Color c2;
        int confStatus;
        if (authStrongMap.containsKey(s)) {
            confStatus = authStrongMap.get(s);
            switch (confStatus) {
                case PROVED_TRUE:
                    c1 = Color.green;
                    break;
                case PROVED_FALSE:
                    c1 = Color.red;
                    break;
                case NOT_PROVED:
                    c1 = Color.gray;
                    break;
                default:
                    return;
            }
        } else {
            c1 = Color.gray;
        }
        if (authWeakMap.containsKey(s)) {
            confStatus = authWeakMap.get(s);
            switch (confStatus) {
                case PROVED_TRUE:
                    c2 = Color.green;
                    break;
                case PROVED_FALSE:
                    c2 = Color.red;
                    break;
                case NOT_PROVED:
                    c2 = Color.gray;
                    break;
                default:
                    return;
            }
        } else {
            c2 = c1;
        }

        g.drawOval(_x + 6, _y - 16, 10, 15);
        g.setColor(c1);
        int[] xps = new int[]{_x + 4, _x + 4, _x + 20};
        int[] yps = new int[]{_y - 10, _y + 4, _y + 4};
        int[] xpw = new int[]{_x + 20, _x + 20, _x + 4};
        int[] ypw = new int[]{_y + 4, _y - 10, _y - 10};
        g.fillPolygon(xps, yps, 3);

        g.setColor(c2);
        g.fillPolygon(xpw, ypw, 3);

//        g.fillRect(_x+4, _y-7, 18, 14);
        g.setColor(c);
//        g.drawRect(_x+4, _y-7, 18, 14);
        g.drawPolygon(xps, yps, 3);
        g.drawPolygon(xpw, ypw, 3);
        drawSingleString(g, "S", _x + 6, _y + 2);
        drawSingleString(g, "W", _x + 13, _y - 2);
//	if (c1==Color.gray){
//	    drawSingleString(g, "?", _x+4, _y+2);
//	}
    }

    @Override
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;

        JDialogPragma jdn = new JDialogPragma(frame, "Setting the security pragmas", value);
        //jdn.setLocation(200, 150);
        AvatarBDPanel abdp = (AvatarBDPanel) tdp;
        jdn.blockAttributeMap = abdp.getBlockStrings(true, false, false);
        jdn.blockStateMap = abdp.getBlockStrings(false, true, false);
        GraphicLib.centerOnParent(jdn);
        jdn.setVisible(true); // blocked until dialog has been closed

        String s = jdn.getText();
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            // String tmp = s;
            setValue(s);
            makeValue();
            return true;
        }
        return false;
    }

    @Override
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public String getToolTipText() {
        return "The lock shows status of weak and strong authenticity. Green: Proved True, Red: Proved False, Grey: Cannot be proved";
    }

    @Override
    public void rescale(double scaleFactor) {
        //TraceManager.addDev("Rescaling BD Pragma");
        /*dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
          lineHeight = (int)(dlineHeight);
          dlineHeight = dlineHeight - lineHeight;
          minHeight = lineHeight;*/

        values = null;

        super.rescale(scaleFactor);
    }

    @Override
    public int getType() {
        return TGComponentManager.PRAGMA;
    }

    @Override
    protected String translateExtraParam() {
        if (values == null) {
            makeValue();
        }
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for (int i = 0; i < values.length; i++) {
            sb.append("<Line value=\"");
            sb.append(GTURTLEModeling.transformString(values[i]));
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        value = "";
        values = null;
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Line")) {
                                //
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                value += GTURTLEModeling.decodeString(s) + "\n";
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public void showTrace(int y) {

        //TraceManager.addDev(pragmaTraceMap + " " + pragmaLocMap + " " + y);
        //On right click, display verification trace
        for (String pragma : pragmaLocMap.keySet()) {
            if (pragmaLocMap.get(pragma) < y && y < pragmaLocMap.get(pragma) + currentFontSize && pragmaTraceMap.get(pragma) != null) {
                PipedOutputStream pos = new PipedOutputStream();
                try {
                    PipedInputStream pis = new PipedInputStream(pos, 4096);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(pos));

                    JFrameSimulationSDPanel jfssdp = new JFrameSimulationSDPanel(null, tdp.getMGUI(), pragma);
                    jfssdp.setIconImage(IconManager.img8);
                    GraphicLib.centerOnParent(jfssdp, 600, 600);
                    jfssdp.setFileReference(new BufferedReader(new InputStreamReader(pis)));
                    jfssdp.setVisible(true);
                    //jfssdp.setModalExclusionType(ModalExclusionType
                    //      .APPLICATION_EXCLUDE);
                    jfssdp.toFront();

                    // TraceManager.addDev("\n--- Trace ---");
                    int i = 0;
                    for (ProVerifResultTraceStep step : pragmaTraceMap.get(pragma).getTrace()) {
                        step.describeAsTMLSDTransaction(bw, i);
                        i++;
                    }
                    bw.close();
                } catch (IOException e) {
                    TraceManager.addDev("Error when writing trace step SD transaction");
                } finally {
                    try {
                        pos.close();
                    } catch (IOException ignored) {
                    }
                }

                break;
            }
        }
    }
}

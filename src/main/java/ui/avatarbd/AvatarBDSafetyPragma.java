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

import myutil.*;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogSafetyPragma;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;

/**
 * Class Pragma Like a Note but with Pragma Creation: 06/12/2003
 *
 * @author Ludovic APVRILLE, Letitia LI
 * @version 1.0 06/12/2003
 */
public class AvatarBDSafetyPragma extends TGCScalableWithoutInternalComponent {

    protected String[] values;
    protected List<String> properties;
    // protected int textX = 25;
    // protected int textY = 5;
    protected int marginY = 20;
    protected int marginX = 20;
    protected int limit = 15;
    protected int lockX = 1;
    protected int lockY = 5;
    protected Graphics myg;
    public List<String> syntaxErrors;
    protected Color myColor;

    private Font myFont;// , myFontB;
    // private int maxFontSize = 30;
    // private int minFontSize = 4;
    public final static int PROVED_TRUE = 1;
    public final static int PROVED_FALSE = 0;
    public final static int PROVED_ERROR = 2;
    private int currentFontSize = -1;
    private final String[] pPragma = { "A[]", "A<>", "E[]", "E<>" };
    public Map<String, Integer> verifMap = new HashMap<String, Integer>();

    protected Graphics graphics;

    public AvatarBDSafetyPragma(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 200;
        textY = 5;
        textX = 25;
        height = 30;
        minWidth = 80;
        minHeight = 10;
        initScaling(200, 30);

        properties = new LinkedList<String>();
        // oldScaleFactor = tdp.getZoom();

        nbConnectingPoint = 0;
        // addTGConnectingPointsComment();
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
        name = "UPPAAL Pragma";
        value = "";

        myImageIcon = IconManager.imgic6000;
    }

    public String[] getValues() {
        return values;
    }

    public List<String> getProperties() {
        return properties;
    }

    @Override
    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        Font fold = f;

        /*
         * if (!tdp.isScaled()) { graphics = g; }
         */

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize() + 1;
            //
            // myFont = f.deriveFont((float)currentFontSize);
            // myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        if (values == null) {
            makeValue();
        }

        // int h = g.getFontMetrics().getHeight();
        Color c = g.getColor();

        if (!(this.tdp.isScaled())) {
            int desiredWidth = minWidth;
            desiredWidth = Math.max(desiredWidth,
                    2 * g.getFontMetrics().stringWidth("Safety Pragmas") + marginX + textX);

            for (int i = 0; i < values.length; i++) {
                desiredWidth = Math.max(desiredWidth, g.getFontMetrics().stringWidth(values[i]) + marginX + textX);
            }

            int desiredHeight = (properties.size() + 2) * currentFontSize + textY + 1;

            // TraceManager.addDev("resize: " + desiredWidth + "," + desiredHeight);

            if ((desiredWidth != width) || (desiredHeight != height)) {
                resize(desiredWidth, desiredHeight);
            }
        }

        // TraceManager.addDev("x+Width=" + (x+width));
        g.drawLine(x, y, x + width, y);
        g.drawLine(x, y, x, y + height);
        g.drawLine(x, y + height, x + width - limit, y + height);
        g.drawLine(x + width, y, x + width, y + height - limit);

        g.setColor(ColorManager.SAFETY_PRAGMA_BG);
        int[] px1 = { x + 1, x + width, x + width, x + width - limit, x + 1 };
        int[] py1 = { y + 1, y + 1, y + height - limit, y + height, y + height };
        g.fillPolygon(px1, py1, 5);
        g.setColor(c);

        int[] px = { x + width, x + width - 4, x + width - 10, x + width - limit };
        int[] py = { y + height - limit, y + height - limit + 3, y + height - limit + 2, y + height };
        g.drawPolygon(px, py, 4);

        if (g.getColor() == ColorManager.NORMAL_0) {
            g.setColor(ColorManager.PRAGMA);
        }
        g.fillPolygon(px, py, 4);

        g.setColor(Color.black);

        int i = 1;
        // Font heading = new Font("heading", Font.BOLD, 14);
        // g.setFont(heading);
        drawSingleString(g, "Safety Pragmas", x + textX, y + textY + currentFontSize);
        g.setFont(fold);
        for (String s : properties) {
            drawSingleString(g, s, x + textX, y + textY + (i + 1) * currentFontSize);
            if (syntaxErrors.contains(s)) {
                Color ctmp = g.getColor();
                g.setColor(Color.red);
                g.drawLine(x + textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + width - textX / 2,
                        y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.drawLine(x + width - textX / 2, y + textY * 3 / 2 + i * currentFontSize, x + textX / 2,
                        y + textY * 3 / 2 + (i + 1) * currentFontSize);
                g.setColor(ctmp);
            }
            drawVerification(s, g, x + textX, y + textY + (i + 1) * currentFontSize);
            i++;
        }

        /*
         * for (int i = 0; i<values.length; i++) { //TraceManager.addDev("x+texX=" + (x
         * + textX) + " y+textY=" + y + textY + i* h + ": " + values[i]);
         * drawSingleString(g, values[i], x + textX, y + textY + (i+1)*
         * currentFontSize); }
         */
        g.setColor(c);

    }

    private void makeValue() {
        values = Conversion.wrapText(value);
        properties.clear();
        for (String s : values) {
            if (s.isEmpty()) {
                // Ignore
            } else if (Arrays.asList(pPragma).contains(s.split(" ")[0])) {
                properties.add(s);
            } else if (s.contains("-->")) {
                properties.add(s);
            } else {
                properties.add(s);
                // Warning Message
                // Never show this: JOptionPane.showMessageDialog(null, s + " is not a valid
                // pragma.", "Invalid Pragma",
                // JOptionPane.INFORMATION_MESSAGE);
            }
        }
        // checkMySize();
    }

    /*
     * public void checkMySize() { if (myg == null) { return; } int desiredWidth =
     * minWidth; for(int i=0; i< values.length; i++) { desiredWidth =
     * Math.max(desiredWidth, myg.getFontMetrics().stringWidth(values[i]) +
     * marginX); }
     * 
     * int desiredHeight = values.length * myg.getFontMetrics().getHeight() +
     * marginY;
     * 
     * if ((desiredWidth != width) || (desiredHeight != height)) {
     * resize(desiredWidth, desiredHeight); } }
     */

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;

        AvatarBDPanel abdp = (AvatarBDPanel) tdp;
        Map<String, List<String>> blockAttributeMap = abdp.getBlockStrings(true, true, true);
        JDialogSafetyPragma jdn = new JDialogSafetyPragma(frame, getTDiagramPanel().getMainGUI(),
                "Setting the safety pragmas", value, blockAttributeMap);
        // jdn.setLocation(200, 150);
        jdn.setSize(500, 500);
        GraphicLib.centerOnParent(jdn);

        // jdn.blockAttributeMap = abdp.getBlockStrings(true, true, true);
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
        // TraceManager.addDev("x+width=" + (x+width) + " x1=" + x1);
        // TraceManager.addDev("y+height=" + (y+height) + " y1=" + y1);
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public void rescale(double scaleFactor) {
        /*
         * dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
         * lineHeight = (int)(dlineHeight); dlineHeight = dlineHeight - lineHeight;
         * minHeight = lineHeight;
         */

        values = null;

        super.rescale(scaleFactor);
    }

    @Override
    public int getType() {
        return TGComponentManager.SAFETY_PRAGMA;
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

    private void drawVerification(String s, Graphics g, int _x, int _y) {
        Color c = g.getColor();
        // Color c1;
        int status;
        if (verifMap.containsKey(s)) {
            status = verifMap.get(s);
            if (status == PROVED_TRUE) {
                g.setColor(Color.green);
                int[] xp1 = new int[] { _x - 20, _x - 18, _x - 12, _x - 14 };
                int[] yp1 = new int[] { _y - 3, _y - 5, _y - 1, _y + 1 };
                int[] xp2 = new int[] { _x - 14, _x - 12, _x - 3, _x - 5 };
                int[] yp2 = new int[] { _y - 1, _y + 1, _y - 8, _y - 10 };
                g.fillPolygon(xp1, yp1, 4);
                g.fillPolygon(xp2, yp2, 4);
            } else if (status == PROVED_ERROR) {
                Font f = g.getFont();
                g.setFont(new Font("TimesRoman", Font.BOLD, 14));
                drawSingleString(g, "?", _x - 15, _y);
                g.setFont(f);
            } else {
                g.setColor(Color.red);
                int[] xp1 = new int[] { _x - 17, _x - 15, _x - 6, _x - 8 };
                int[] yp1 = new int[] { _y - 12, _y - 10, _y - 2, _y };
                int[] xp2 = new int[] { _x - 15, _x - 17, _x - 8, _x - 6 };
                int[] yp2 = new int[] { _y, _y - 2, _y - 12, _y - 10 };
                g.fillPolygon(xp1, yp1, 4);
                g.fillPolygon(xp2, yp2, 4);
            }
        }
        g.setColor(c);
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
}

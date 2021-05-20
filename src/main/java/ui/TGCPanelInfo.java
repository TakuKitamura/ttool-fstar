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

package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import ui.util.IconManager;

/**
 * Class TGCPanelInfo Component for displaying an informatio Creation: 8/03/2016
 * 
 * @version 1.0 8/03/2016
 * @author Ludovic APVRILLE
 */
public class TGCPanelInfo extends TGCScalableWithoutInternalComponent {

    public final static int UPPER_LEFT = 1;
    public final static int UPPER_MIDDLE = 2;
    public final static int UPPER_RIGHT = 3;
    public final static int MIDDLE_LEFT = 4;
    public final static int MIDDLE_MIDDLE = 5;
    public final static int MIDDLE_RIGHT = 6;
    public final static int LOWER_LEFT = 7;
    public final static int LOWER_MIDDLE = 8;
    public final static int LOWER_RIGHT = 9;

    // Issue #31
    private static final int MARGIN_X = 5;
    private static final int MARGIN_Y = 5;
    // protected int marginY = 5;
    // protected int marginX = 5;

    protected Graphics myg;

    protected Color myColor;

    private Font myFont;// , myFontB;
    // private int maxFontSize = 30;
    // private int minFontSize = 4;
    private int currentFontSize = -1;

    private int stringPos = 2; // Upperleft: 1; Upper: 2; UpperRight: 3, etc.
    private Color fillColor;
    private Color textColor;

    protected Graphics graphics;

    public TGCPanelInfo(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
            TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 150;
        height = 150;
        minWidth = 20;
        minHeight = 20;

        // Issue #31
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
        editable = false;
        removable = false;
        userResizable = true;

        name = "Info";
        value = "Info";
        fillColor = Color.LIGHT_GRAY;
        textColor = Color.RED;
        myImageIcon = IconManager.imgic320;
    }

    @Override
    protected void internalDrawing(Graphics g) {
        Font f = g.getFont();
        // Font fold = f;

        /*
         * if (!tdp.isScaled()) { graphics = g; }
         */

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize();
            //
            // myFont = f.deriveFont((float)currentFontSize);
            // myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        Color c = g.getColor();

        g.setColor(fillColor);
        g.fillRect(x, y, width, height);
        g.setColor(c);
        g.drawRect(x, y, width, height);

        int xStr = x, yStr = y;
        Font f0 = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        // int h = g.getFontMetrics().getHeight();
        int w = g.getFontMetrics().stringWidth(value);

        // Issue #31
        final int marginX = scale(MARGIN_X);
        final int marginY = scale(MARGIN_Y);

        switch (stringPos) {
            case UPPER_LEFT:
                xStr += marginX;
                yStr += marginY + currentFontSize;
                break;
            case UPPER_MIDDLE:
                xStr += width / 2 - w / 2;
                yStr += marginY + currentFontSize;
                break;
            case UPPER_RIGHT:
                xStr += width - marginX - w;
                yStr += marginY + currentFontSize;
                break;
            case MIDDLE_LEFT:
                xStr += marginX;
                yStr += height / 2 + currentFontSize;
                break;
            case MIDDLE_MIDDLE:
                xStr += width / 2 - w / 2;
                yStr += height / 2 + currentFontSize;
                break;
            case MIDDLE_RIGHT:
                xStr += width - marginX - w;
                yStr += height / 2 + currentFontSize;
                break;
            case LOWER_LEFT:
                xStr += marginX;
                yStr += height - marginY;
                break;
            case LOWER_MIDDLE:
                xStr += width / 2 - w / 2;
                yStr += height - marginY;
                break;
            case LOWER_RIGHT:
                xStr += width - marginX - w;
                yStr += height - marginY;
                break;
            default:
                xStr += width / 2 - w / 2;
                yStr += marginY + currentFontSize;
                break;
        }

        // TraceManager.addDev("Color=" + c);
        if (!(c == ColorManager.POINTER_ON_ME_0)) {
            g.setColor(textColor);
        }

        g.drawString(value, xStr, yStr);
        g.setFont(f0);
        g.setColor(c);
    }

    @Override
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    // @Override
    // public void rescale(double scaleFactor){
    /*
     * dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
     * lineHeight = (int)(dlineHeight); dlineHeight = dlineHeight - lineHeight;
     * minHeight = lineHeight;
     */

    // super.rescale(scaleFactor);
    // }

    @Override
    public int getType() {
        return TGComponentManager.INFO_PANEL;
    }

    public void setStringPos(int _pos) {
        stringPos = _pos;
    }

    public void setFillColor(Color _c) {
        fillColor = _c;
    }

    public void setTextColor(Color _c) {
        textColor = _c;
    }

    protected String translateExtraParam() {
        // TAttribute a;
        // AvatarMethod am;
        // AvatarSignal as;

        //
        // value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<TextColor value=\"" + textColor.getRGB() + "\" />\n");
        sb.append("<FillColor value=\"" + textColor.getRGB() + "\" />\n");
        sb.append("</extraparam>\n");

        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        String s;

        try {
            NodeList nli;
            Node n1, n2;
            Element elt;

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
                            if (elt.getTagName().equals("TextColor")) {
                                s = elt.getAttribute("value");
                                textColor = new Color(Integer.decode(s).intValue());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
}// Class

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

import myutil.*;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.util.*;
import ui.window.JDialogNote;

import javax.swing.*;
import java.awt.*;

/**
 * Class TGCNote
 * Generic text box for displaying notes
 * Creation: 06/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 06/12/2003
 */
public class TGCNote extends TGCScalableWithoutInternalComponent {

    protected String[] values;
    protected int textX = 1;
    protected int textY = 2;
    protected int marginY = 20;
    protected int marginX = 20;
    protected int limit = 15;
    protected Graphics myg;

    protected Color myColor;

    //    private Font myFontB;
    //   private int maxFontSize = 30;
    //   private int minFontSize = 4;
    private int currentFontSize;

    protected Graphics graphics;

    public TGCNote(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        this.width = 150;
        this.height = 30;
        this.minWidth = 20;
        this.minHeight = 10;

        this.oldScaleFactor = tdp.getZoom();

        this.nbConnectingPoint = 0;
        int len = this.makeTGConnectingPointsComment(16);
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

        this.moveable = true;
        this.editable = true;
        this.removable = true;

        this.currentFontSize = tdp.getFontSize();

        this.name = "UML Note";
        this.value = "UML note:\nDouble-click to edit";

        this.myImageIcon = IconManager.imgic320;
    }

    public String[] getValues() {
        return this.values;
    }

    @Override
    public void internalDrawing(Graphics graph) {
        if (this.rescaled && !this.tdp.isScaled()) {
            this.rescaled = false;
            this.currentFontSize = this.tdp.getFontSize();
        }

        graph.setFont(graph.getFont().deriveFont(this.currentFontSize));

        if (this.values == null)
            this.makeValue();

        // int h  = graph.getFontMetrics ().getHeight();
        Color c = graph.getColor();

        int desiredWidth = this.minWidth;
        for (int i = 0; i < this.values.length; i++)
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
            desiredWidth = Math.max(desiredWidth, this.tdp.stringWidth(graph, this.values[i]) + this.marginX);


        int desiredHeight = (this.values.length * this.currentFontSize) + this.textY + 1;

        if (desiredWidth != this.width || desiredHeight != this.height)
            this.resize(desiredWidth, desiredHeight);

        graph.drawLine(this.x, this.y, this.x + this.width, this.y);
        graph.drawLine(this.x, this.y, this.x, this.y + this.height);
        graph.drawLine(this.x, this.y + this.height, this.x + this.width - this.limit, this.y + this.height);
        graph.drawLine(this.x + this.width, this.y, this.x + this.width, this.y + this.height - this.limit);

        graph.setColor(ColorManager.UML_NOTE_BG);
        int[] px1 = {this.x + 1, this.x + this.width, this.x + this.width, this.x + this.width - this.limit, this.x + 1};
        int[] py1 = {this.y + 1, this.y + 1, this.y + this.height - this.limit, this.y + this.height, this.y + this.height};
        graph.fillPolygon(px1, py1, 5);
        graph.setColor(c);

        int[] px = {this.x + this.width, this.x + this.width - 4, this.x + this.width - 10, this.x + this.width - this.limit};
        int[] py = {this.y + this.height - this.limit, this.y + this.height - this.limit + 3, this.y + this.height - this.limit + 2, this.y + this.height};
        graph.drawPolygon(px, py, 4);

        if (c == ColorManager.NORMAL_0)
            graph.setColor(ColorManager.UML_NOTE);

        graph.fillPolygon(px, py, 4);

        graph.setColor(c);
       // Graphics2D g2 = (Graphics2D)graph;
        for (int i = 0; i < this.values.length; i++) {
            //TraceManager.addDev("Value #" + i + " = " + this.values[i]);
        	graph.drawString(this.values[i], this.x + this.textX, this.y + this.textY + (i + 1) * this.currentFontSize);
        }
    }

    public void makeValue() {
        values = Conversion.wrapText(value);
    }

    @Override
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = this.value;

        JDialogNote jdn = new JDialogNote(frame, "Setting the note", value);
        GraphicLib.centerOnParent(jdn);
        jdn.setVisible(true); // blocked until dialog has been closed

        String s = jdn.getText();
        if (s != null && s.length() > 0 && !s.equals(oldValue)) {
            this.setValue(s);
            this.makeValue();
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
    public void rescale(double scaleFactor) {
        /*dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
          lineHeight = (int)(dlineHeight);
          dlineHeight = dlineHeight - lineHeight;
          minHeight = lineHeight;*/

        values = null;

        super.rescale(scaleFactor);
    }

    @Override
    public int getType() {
        return TGComponentManager.UML_NOTE;
    }

    @Override
    protected String translateExtraParam() {
        if (values == null)
            this.makeValue();

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
                                //TraceManager.addDev("Adding " + s + " in decoded format:" + GTURTLEModeling.decodeString(s));
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

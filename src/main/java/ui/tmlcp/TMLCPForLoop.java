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

package ui.tmlcp;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import ui.BasicErrorHighlight;
import ui.ColorManager;
import ui.EmbeddedComment;
import ui.ErrorHighlight;
import ui.GTURTLEModeling;
import ui.MalformedModelingException;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.ad.TADForLoop;
import ui.window.JDialogMultiString;

/**
 * Class TMLCPForLoop
 * For loop of a TML activity diagram
 * Creation: 03/06/2015
 *
 * @author Ludovic APVRILLE
 * @version 1.0 03/06/2015
 */
public class TMLCPForLoop extends TADForLoop /* Issue #69 TGCWithoutInternalComponent*/ implements EmbeddedComment, BasicErrorHighlight {
//    protected int lineLength = 5;
//    protected int textX = 5;
//    protected int textY = 15;
//    protected int arc = 5;

    protected String init = "i=0";
    protected String condition = "i<5";
    protected String increment = "i = i+1";

  //  protected int stateOfError = 0; // Not yet checked

    public TMLCPForLoop(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

//        width = 30;
//        height = 20;
//        minWidth = 30;

//        nbConnectingPoint = 3;
//        connectingPoint = new TGConnectingPoint[3];
//        connectingPoint[0] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.5, 0.0);
//        connectingPoint[1] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 1.0, 0.45); // loop
//        connectingPoint[2] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 0.5, 1.0); // after lopp

//        moveable = true;
//        editable = true;
//        removable = true;

        makeValue();

        name = "for loop";

//        myImageIcon = IconManager.imgic912;
    }

    @Override
    protected void createConnectingPoints() {
        nbConnectingPoint = 3;
        connectingPoint = new TGConnectingPoint[3];
        connectingPoint[0] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 1.0, 0.45); // loop
        connectingPoint[2] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 0.5, 1.0); // after lopp
    }

    @Override
    protected void internalDrawing(Graphics g) {
    	
    	// Issue #31
        final int textWidth = checkWidth( g );//g.getFontMetrics().stringWidth(value);
//        int w1 = Math.max(minWidth, textWidth + 2 * textX);
//        if ((w1 != width) & (!tdp.isScaled())) {
//            setCd(x + width / 2 - w1 / 2, y);
//            width = w1;
//            //updateConnectingPoints();
//        }

        if (stateOfError > 0) {
            Color c = g.getColor();
            switch (stateOfError) {
                case ErrorHighlight.OK:
                    g.setColor(ColorManager.FOR);
                    break;
                default:
                    g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
            }
            g.fillRoundRect(x, y, width, height, arc, arc);
            g.setColor(c);
        }

        g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x + (width / 2), y, x + (width / 2), y - lineLength);
        g.drawLine(x + (width / 2), y + height, x + (width / 2), y + lineLength + height);
        
        // Issue #31 Useless line
        //g.drawLine(x + width, y + height / 2, x + width + lineLength, y + height / 2);

        drawSingleString(g,value, x + (width - textWidth) / 2, y + textY);
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String[] labels = new String[3];
        String[] values = new String[3];
        labels[0] = "Initialisation of variable";
        values[0] = init;
        labels[1] = "Condition to stay in loop";
        values[1] = condition;
        labels[2] = "Increment at each loop";
        values[2] = increment;

        JDialogMultiString jdms = new JDialogMultiString(frame, "Setting loop's properties", 3, labels, values);
        //    jdms.setSize(400, 300);
        GraphicLib.centerOnParent(jdms, 400, 300);
        jdms.setVisible(true); // blocked until dialog has been closed

        if (jdms.hasBeenSet()) {
            init = jdms.getString(0);
            condition = jdms.getString(1);
            increment = jdms.getString(2);

            makeValue();
            return true;
        }

        return false;
    }

//    public TGComponent isOnMe(int _x, int _y) {
//        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
//            return this;
//        }
//
//        if ((int) (Line2D.ptSegDistSq(x + (width / 2), y - lineLength, x + (width / 2), y + lineLength + height, _x, _y)) < distanceSelected) {
//            return this;
//        }
//
//        if ((int) (Line2D.ptSegDistSq(x + width, y + height / 2, x + width + lineLength, y + height / 2, _x, _y)) < distanceSelected) {
//            return this;
//        }
//
//        return null;
//    }

    public void makeValue() {
        value = "for(" + init + ";" + condition + ";" + increment + ")";
    }

    public String getAction() {
        return value;
    }

    public String getInit() {
        return init;
    }

    public String getCondition() {
        return condition;
    }

    public String getIncrement() {
        return increment;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data init=\"");
        sb.append(getInit());
        sb.append("\" condition=\"");
        sb.append(GTURTLEModeling.transformString(getCondition()));
        sb.append("\" increment=\"");
        sb.append(getIncrement());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
//            int k;
//            String s;
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
                            if (elt.getTagName().equals("Data")) {
                                init = elt.getAttribute("init");
                                condition = elt.getAttribute("condition");
                                increment = elt.getAttribute("increment");
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
        makeValue();
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLCP_FOR_LOOP;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLCP;
    }

//    public void setStateAction(int _stateAction) {
//        stateOfError = _stateAction;
//    }
}

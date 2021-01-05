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

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.window.JDialogMultiString;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

//import java.awt.geom.*;


/**
   * Class TGConnectorWithMultiplicity
   * Generic (abstract class) Connector With multiplicity
   * Creation: 05/01/2021
   * @version 1.0 05/01/2021
   * @author Ludovic APVRILLE
 */
public abstract class TGConnectorWithMultiplicity extends TGConnectorWithCommentConnectionPoints {

    public final static int MULTIPLICITY_X = 5;
    public final static int MULTIPLICITY_Y = 5;


    protected String originMultiplicity, destinationMultiplicity;

    public TGConnectorWithMultiplicity(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);

        originMultiplicity = "";
        destinationMultiplicity = "";
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");

        sb.append("<multiplicity origin=\"");
        sb.append(originMultiplicity);
        sb.append("\" destination=\"");
        sb.append(destinationMultiplicity);
        sb.append("\" />\n");

        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String [] labels = new String[2];
        String [] values = new String[2];

        labels[0] = "origin (" + getTGComponent1().getName() + ")";
        labels[1] = "destination (" + getTGComponent2().getName() + ")";
        values[0] = originMultiplicity;
        values[1] = destinationMultiplicity;

        JDialogMultiString jdms = new JDialogMultiString(frame, "Multiplicity", 2, labels, values);
        GraphicLib.centerOnParent(jdms, 300, 200);
        jdms.setVisible(true); // blocked until dialog has been closed

        if (jdms.hasBeenSet()) {
            originMultiplicity = jdms.getString(0);
            destinationMultiplicity = jdms.getString(1);
            return true;
        }

        return false;
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String valO, valD;

            //
            //

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
                            if (elt.getTagName().equals("multiplicity")) {
                                valO = elt.getAttribute("origin");
                                if ((valO != null) && (!(valO.equals("null")))) {
                                    originMultiplicity = valO;
                                }
                                valD = elt.getAttribute("destination");
                                if ((valD != null) && (!(valD.equals("null")))) {
                                    destinationMultiplicity = valD;
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException(e);
        }
    }

    protected void internalDrawing(Graphics g) {
        super.internalDrawing(g);

        int length = g.getFontMetrics().stringWidth(originMultiplicity);
        int modifierX = getModifierX(getTGComponent1(), getTGConnectingPointP1(), length);
        int modifierY = getModifierY(getTGComponent1(), getTGConnectingPointP1(), g.getFont().getSize());

        g.drawString(originMultiplicity, getTGConnectingPointP1().getX() + modifierX, getTGConnectingPointP1().getY() + modifierY);

        length = g.getFontMetrics().stringWidth(destinationMultiplicity);
        modifierX = getModifierX(getTGComponent2(), getTGConnectingPointP2(), length);
        modifierY = getModifierY(getTGComponent2(), getTGConnectingPointP2(), g.getFont().getSize());

        g.drawString(destinationMultiplicity, getTGConnectingPointP2().getX() + modifierX, getTGConnectingPointP2().getY() + modifierY);
    }

    protected int getModifierX(TGComponent tgc, TGConnectingPoint p, int length) {
        if (p.getX() <= tgc.getX()+width/2) {
            return -MULTIPLICITY_X - length;
        }
        return MULTIPLICITY_X;
    }

    protected int getModifierY(TGComponent tgc, TGConnectingPoint p, int fontSize) {
        if (p.getY() <= tgc.getY()+height/2) {
            return -MULTIPLICITY_Y - fontSize;
        }
        return MULTIPLICITY_Y + fontSize;
    }

}

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

package ui.avatarmad;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogVersioningConnector;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Class AvatarMADVersioningConnector Connector to be used in Avatar MAD.
 * Connects two assumptions Creation: 27/08/2013
 * 
 * @version 1.0 27/08/2013
 * @author Ludovic APVRILLE
 */
public class AvatarMADVersioningConnector extends TGConnectorWithCommentConnectionPoints {
    int w, h, w1;

    int oldVersion = 1;
    int newVersion = 2;

    public AvatarMADVersioningConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2,
            Vector<Point> _listPoint) {
        super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        value = "<<versioning>>";

        editable = true;

        myImageIcon = IconManager.imgic1008;
    }

    @Override
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {

        // g.drawLine(x1, y1, x2, y2);
        GraphicLib.dashedArrowWithLine(g, 1, 1, 0, x1, y1, x2, y2, false);

        // Indicate semantics

        Font f = g.getFont();
        Font old = f;
        if (f.getSize() != tdp.getFontSize()) {
            f = f.deriveFont((float) tdp.getFontSize());
            g.setFont(f);
        }

        w = g.getFontMetrics().stringWidth(value);
        h = g.getFontMetrics().getHeight();
        g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
        String s = "{" + oldVersion + "->" + newVersion + "}";
        w1 = g.getFontMetrics().stringWidth(s);
        g.drawString(s, (x1 + x2 - w1) / 2, ((y1 + y2) / 2) + h);
        g.setFont(old);
    }

    @Override
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, (p1.getX() + p2.getX() - w) / 2, (p1.getY() + p2.getY()) / 2 - h, w, h)) {
            return this;
        }
        if (GraphicLib.isInRectangle(x1, y1, (p1.getX() + p2.getX() - w1) / 2, (p1.getY() + p2.getY()) / 2, w1, h)) {
            return this;
        }

        return null;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        JDialogVersioningConnector jdvc = new JDialogVersioningConnector(frame, oldVersion, newVersion);
        // jdvc.setSize(400, 300);
        GraphicLib.centerOnParent(jdvc, 400, 300);
        jdvc.setVisible(true); // blocked until dialog has been closed

        if (jdvc.hasBeenCancelled()) {
            return false;
        }

        try {
            oldVersion = Integer.decode(jdvc.getOldVersion().trim()).intValue();
        } catch (Exception e) {

        }

        try {
            newVersion = Integer.decode(jdvc.getNewVersion().trim()).intValue();
        } catch (Exception e) {

        }

        return true;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<oldVersion data=\"");
        sb.append(oldVersion);
        sb.append("\" />\n");
        sb.append("<newVersion data=\"");
        sb.append(newVersion);
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
            String s;

            //
            //

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("oldVersion")) {

                                //
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    oldVersion = 0;
                                } else {
                                    try {
                                        oldVersion = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        oldVersion = 0;
                                    }
                                }

                            } else if (elt.getTagName().equals("newVersion")) {
                                //
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    newVersion = 1;
                                } else {
                                    try {
                                        newVersion = Integer.decode(s).intValue();
                                    } catch (Exception e) {
                                        newVersion = 1;
                                    }
                                }

                            }
                            //
                        }
                    }
                }
            }

        } catch (Exception e) {
            TraceManager.addError("Failed when loading requirement extra parameters (AVATARRD)");
            throw new MalformedModelingException();
        }

    }

    @Override
    public int getType() {
        return TGComponentManager.AVATARMAD_VERSIONING_CONNECTOR;
    }

}

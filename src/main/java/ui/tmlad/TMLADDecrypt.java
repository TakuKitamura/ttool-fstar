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


package ui.tmlad;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogMultiString;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Class TMLADDecrypt
 * Create decryption. To be used in TML activity diagrams
 * Creation: 21/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/11/2005
 */
public class TMLADDecrypt extends TGCWithoutInternalComponent implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    private int lineLength = 5;
    //    private int textX, textY;
    private int ilength = 20;
    private int ex = 5;
    private int lineLength1 = 2;
    public String securityContext = "";
    protected int stateOfError = 0; // Not yet checked

    public TMLADDecrypt(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 15;
        height = 35;
//        textX = width + 5;
//        textY = height/2 + 5;

        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, +lineLength + ex, false, true, 0.5, 1.0);


        moveable = true;
        editable = true;
        removable = true;

        name = "decrypt";

        myImageIcon = IconManager.imgic214;
    }

    public void internalDrawing(Graphics g) {
        if (stateOfError > 0) {
            Color c = g.getColor();
            switch (stateOfError) {
                case ErrorHighlight.OK:
                    g.setColor(ColorManager.EXEC);
                    break;
                default:
                    g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
            }
            g.fillRect(x, y, width, height);
            int[] xP = new int[]{x, x + width, x + width / 2};
            int[] yP = new int[]{y + height, y + height, y + height + ex};
            g.fillPolygon(xP, yP, 3);
            g.setColor(c);
        }
        g.drawLine(x, y, x + width, y);
        g.drawLine(x, y, x, y + height);
        g.drawLine(x + width, y, x + width, y + height);
        g.drawLine(x, y + height, x + width / 2, y + height + ex);
        g.drawLine(x + width / 2, y + height + ex, x + width, y + height);
        g.drawLine(x + (width / 2), y, x + (width / 2), y - lineLength);
        g.drawLine(x + (width / 2), y + height + ex, x + (width / 2), y + lineLength + height + ex);

        g.drawLine(x + (width / 2) - lineLength1, y + (height - ilength) / 2, x + (width / 2) - lineLength1, y + (height + ilength) / 2);
        g.drawArc(x - ex, y + ex, width, height - 2 * ex, 270, 180);

/*
        g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2,  x + (width/2) + lineLength1, y+(height-ilength)/2);


        g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2 + ilength,  x + (width/2) + lineLength1, y+(height-ilength)/2 + ilength);

        g.drawLine(x + (width/2)+ lineLength1, y+(height-ilength)/2, x + (width/2)+ lineLength1, y+(height+ilength)/2);
*/
        g.drawImage(IconManager.imgic7000.getImage(), x - 22, y + height / 2, null);
        g.drawString("sec:" + securityContext, x + 3 * width / 2, y + height / 2);
    }

    public boolean editOndoubleClick(JFrame frame) {
        String[] labels = new String[1];
        String[] values = new String[1];
        labels[0] = "Security Pattern";
        values[0] = securityContext;

        ArrayList<String[]> help = new ArrayList<String[]>();
        help.add(tdp.getMGUI().getCurrentCryptoConfig());
        //JDialogTwoString jdts = new JDialogTwoString(frame, "Setting channel's properties", "Channel name", channelName, "Nb of samples", nbOfSamples);
        JDialogMultiString jdms = new JDialogMultiString(frame, "Setting Decryption", 1, labels, values, help);
        // jdms.setSize(600, 300);
        GraphicLib.centerOnParent(jdms, 600, 300);
        jdms.setVisible(true); // blocked until dialog has been closed

        if (jdms.hasBeenSet() && (jdms.hasValidString(0))) {
            securityContext = jdms.getString(0);
            return true;
        }

        return false;

    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int) (Line2D.ptSegDistSq(x + (width / 2), y - lineLength, x + (width / 2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        if ((int) (Line2D.ptSegDistSq(x + width, y + height / 2, x + width + lineLength, y + height / 2, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data secPattern=\"");
        sb.append(securityContext);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        //System.out.println("*** load extra synchro *** " + getId());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
//            int k;
//            String s;

            //System.out.println("Loading Synchronization gates");
            //System.out.println(nl.toString());

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                securityContext = elt.getAttribute("secPattern");
                                //System.out.println("eventName=" +eventName + " variable=" + result);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public int getType() {
        return TGComponentManager.TMLAD_DECRYPT;
    }

    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLAD;
    }

    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }
}


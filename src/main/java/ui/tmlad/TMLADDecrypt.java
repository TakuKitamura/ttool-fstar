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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import ui.AllowedBreakpoint;
import ui.BasicErrorHighlight;
import ui.ColorManager;
import ui.EmbeddedComment;
import ui.ErrorHighlight;
import ui.MalformedModelingException;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.ad.TADComponentWithoutSubcomponents;
import ui.util.IconManager;
import ui.window.JDialogMultiString;

/**
 * Class TMLADDecrypt
 * Create decryption. To be used in TML activity diagrams
 * Creation: 21/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/11/2005
 */
public class TMLADDecrypt extends TADComponentWithoutSubcomponents/* Issue #69 TGCWithoutInternalComponent*/ implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {

	// Issue #31
//    private int lineLength = 5;
    //    private int textX, textY;
//    private int ilength = 20;
//    private int ex = 5;
//    private int lineLength1;// = 2;
	private static final int MARGIN = 5;
	private static final int DEC_SYMBOL_MARGIN_Y = 6;
	
    public String securityContext = "";
    protected int stateOfError = 0; // Not yet checked

    public TMLADDecrypt(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, +lineLength + MARGIN, false, true, 0.5, 1.0);
//        width = 15;
//        height = 35;
//        textX = width + 5;
//        textY = height/2 + 5;
        initScaling( 15, 35 );

        moveable = true;
        editable = true;
        removable = true;

        name = "decrypt";

        myImageIcon = IconManager.imgic214;
    }

    @Override
    protected void internalDrawing(Graphics g) {
    	final int scaledMargin = scale( MARGIN );
    	
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
            int[] yP = new int[]{y + height, y + height, y + height + scaledMargin };
            g.fillPolygon(xP, yP, 3);
            g.setColor(c);
        }
        g.drawLine(x, y, x + width, y);
        g.drawLine(x, y, x, y + height);
        g.drawLine(x + width, y, x + width, y + height);
        g.drawLine(x, y + height, x + width / 2, y + height + scaledMargin);
        g.drawLine(x + width / 2, y + height + scaledMargin, x + width, y + height);
        g.drawLine(x + (width / 2), y, x + (width / 2), y - lineLength);
        g.drawLine(x + (width / 2), y + height + scaledMargin, x + (width / 2), y + lineLength + height + scaledMargin);

        // D
        final int xPosOffset = width / 3 ;
        final int scaledSymbolMarginY = scale( DEC_SYMBOL_MARGIN_Y );
        g.drawLine(x + xPosOffset, y + scaledSymbolMarginY, x + xPosOffset, y + height - scaledSymbolMarginY );
        g.drawArc(x - scaledMargin, y + scaledMargin, width, height - 2 * scaledMargin, 270, 180);

        g.drawImage( scale( IconManager.imgic7000.getImage() ), x - scale( 22 ), y + height / 2, null );
        
        drawSingleString(g,"sec:" + securityContext, x + 3 * width / 2, y + height / 2);
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String[] labels = new String[1];
        String[] values = new String[1];
        labels[0] = "Security Pattern";
        values[0] = securityContext;

        List<String[]> help = new ArrayList<String[]>();
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

    @Override
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

    @Override
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
                            if (elt.getTagName().equals("Data")) {
                                securityContext = elt.getAttribute("secPattern");
                                //
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLAD_DECRYPT;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLAD;
    }

    @Override
    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }
}

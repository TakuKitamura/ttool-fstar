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
import ui.ad.TADComponentWithoutSubcomponents;
import ui.util.IconManager;
import ui.window.JDialogMultiString;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
   * Class TMLADSendRequest
   * Action of sending a request
   * Creation: 18/11/2005
   * @version 1.0 18/11/2005
   * @author Ludovic APVRILLE
 */
public class TMLADSendRequest extends TADComponentWithoutSubcomponents/* Issue #69TGCWithoutInternalComponent*/ implements CheckableAccessibility, CheckableLatency, EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {

	// Issue #31
//    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
//    protected int arc = 5;
//    protected int linebreak = 10;

    protected String requestName = "req";
    int nParam = 5;
    protected String [] params = new String[nParam];

    protected int stateOfError = 0; // Not yet checked

    public final static int NOT_VERIFIED = 0;
    public final static int REACHABLE = 1;
    public final static int NOT_REACHABLE = 2;

    public int reachabilityInformation;

    public TMLADSendRequest(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    	// Issue #31
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, lineLength, false, true, 0.5, 1.0);
//        width = 30;
//        height = 20;
        initScaling( 30, 20 );
        minWidth = scale( 30 );
        
        // Issue #31
        //textX = 5;

        for(int i=0; i<nParam; i++) {
            params[i] = "";
        }
        //params[0] = "1";

        moveable = true;
        editable = true;
        removable = true;

        name = "send request";
        makeValue();

        myImageIcon = IconManager.imgic902;
    }

    @Override
    protected void internalDrawing(Graphics g) {
       	
    	// Issue #31
        final int w = checkWidth( g );//g.getFontMetrics().stringWidth(value);
//        int w1 = Math.max(minWidth, w + 2 * textX);
//        if ( w1 > width & !tdp.isScaled() ) {
//            setCd(x - (w1 - width) / 2 , y);
//            width = w1;
//            //updateConnectingPoints();
//        }
        //g.drawRoundRect(x, y, width, height, arc, arc);

        // Issue #69
        if ( isEnabled() && stateOfError > 0 ) {
            Color c = g.getColor();
            switch(stateOfError) {
            case ErrorHighlight.OK:
                g.setColor(ColorManager.TML_PORT_REQUEST);
                break;
            default:
                g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
            }
            // Making the polygon
            int [] px1 = {x, x+width-linebreak, x+width, x+width-linebreak, x};
            int [] py1 = {y, y, y+(height/2), y+height, y+height};
            g.fillPolygon(px1, py1, 5);
            g.setColor(c);
        }

        int x1 = x + 1;
        int y1 = y + 1;
        int height1 = height;
        int width1 = width;
        Color c = g.getColor();
        
        // Issue #69
        if ( isEnabled() ) {
        	g.setColor(ColorManager.TML_PORT_REQUEST);
        }
        
        g.drawLine(x1, y1, x1+width1-linebreak, y1);
        g.drawLine(x1, y1+height1, x1+width1-linebreak, y1+height1);
        g.drawLine(x1, y1, x1, y1+height1);
        g.drawLine(x1+width1-linebreak, y1, x1+width1, y1+height1/2);
        g.drawLine(x1+width1-linebreak, y1+height1, x1+width1, y1+height1/2);
        g.setColor(c);

        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        g.drawLine(x, y, x+width-linebreak, y);
        g.drawLine(x, y+height, x+width-linebreak, y+height);
        g.drawLine(x, y, x, y+height);
        g.drawLine(x+width-linebreak, y, x+width, y+height/2);
        g.drawLine(x+width-linebreak, y+height, x+width, y+height/2);

        drawSingleString(g,"req", x+(width-w) / 2, y);
        drawSingleString(g,value, x + (width - w) / 2 , y + textY);

        drawReachabilityInformation(g);
    }
    
    private void drawReachabilityInformation(Graphics g) {
        if (reachabilityInformation > 0) {
            Color c = g.getColor();
            Color c1;
            switch(reachabilityInformation) {
            case REACHABLE:
                c1 = Color.green;
                break;
            case NOT_REACHABLE:
                c1 = Color.red;
                break;
            default:
                return;
            }

            GraphicLib.arrowWithLine(g, 1, 0, 10, x-30, y-3, x-15, y-3, true);
            g.drawOval(x-11, y-10, 7, 9);
            g.setColor(c1);
            g.fillRect(x-12, y-7, 9, 7);
            g.setColor(c);
            g.drawRect(x-12, y-7, 9, 7);
        }
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }

    public void makeValue() {
        boolean first = true;
        value = requestName + "(";
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                if (!first) {
                    value += ", " + params[i];
                } else {
                    first = false;
                    value += params[i];
                }

            }
        }

        value += ")";
    }

    public String getRequestName() {
        return requestName;
    }

	public void setRequestName(String name){
		requestName = name;
		makeValue();
	}	

    public String getParamValue(int i) {
        return params[i];
    }

	public void setParam(int i, String s){
		params[i] = s;
		nParam = Math.max(i,nParam);
	}
    public int nbOfParams() {
        return nParam;
    }

    public String getRealParamValue(int index) {
        int cpt = 0;
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                if (cpt == index) {
                    return params[i];
                }
                cpt ++;
            }
        }
        return "";
    }

    public int realNbOfParams() {
        int cpt = 0;
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                cpt ++;
            }
        }
        return cpt;
    }

    public String getAction() {
        return value;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String [] labels = new String[nParam + 1];
        String [] values = new String[nParam + 1];
        labels[0] = "Request name";
        values[0] = requestName;
        for(int i=0; i<nParam; i++) {
            labels[i+1] = "Param #" + (i+1);
            values[i+1] = params[i];
        }

        List<String []> help = new ArrayList<String []>();
        String[] allOutRequests = tdp.getMGUI().getAllOutRequests();
        help.add(allOutRequests);

        JDialogMultiString jdms = new JDialogMultiString(frame, "Setting request's properties", nParam+1, labels, values, help);
    //    jdms.setSize(350, 300);
        GraphicLib.centerOnParent(jdms, 350, 300);
        jdms.setVisible( true ); // blocked until dialog has been closed

        if (jdms.hasBeenSet() && (jdms.hasValidString(0))) {
            requestName = jdms.getString(0);
            for(int i=0; i<nParam; i++) {
                params[i] = jdms.getString(i+1);
            }

            makeValue();
            return true;
        }

        return false;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data requestName=\"");
        sb.append(getRequestName());
        sb.append("\" nbOfParams=\"");
        sb.append(nbOfParams());
        sb.append("\" />\n");
        for(int i=0; i<nParam; i++) {
            if (params[i].length() > 0) {
                sb.append("<Param index=\"");
                sb.append(i);
                sb.append("\" value=\"");
                sb.append(params[i]);
                sb.append("\" />\n");
            }
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                requestName = elt.getAttribute("requestName");
                                nParam = Math.max(nParam, Integer.decode(elt.getAttribute("nbOfParams")).intValue());
                            }
                            if (elt.getTagName().equals("Param")) {
                                s = elt.getAttribute("value");
                                k = Integer.decode(elt.getAttribute("index")).intValue();
                                params[k] = s;
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
        return TGComponentManager.TMLAD_SEND_REQUEST;
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

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

import myutil.Conversion;
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
import java.util.HashMap;

import java.util.concurrent.ConcurrentHashMap;

/**
   * Class TMLADWriteChannel
   * Action of writting data in channel
   * Creation: 17/11/2005
   * @version 1.0 17/11/2005
   * @author Ludovic APVRILLE
 */
public class TMLADWriteChannel extends TGCWithoutInternalComponent implements CheckableAccessibility, LinkedReference, CheckableLatency, EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;


	private ConcurrentHashMap<String, String> latencyVals;
		
	protected int latencyX=30;
	protected int latencyY=25;
	protected int textWidth=10;
	protected int textHeight=20;


    protected String channelName = "ch";
    protected String nbOfSamples= "1";
    protected String securityContext = "";
	protected boolean isAttacker = false; 

    protected int stateOfError = 0; // Not yet checked
    
    public final static int NOT_VERIFIED = 0;
    public final static int REACHABLE = 1;
    public final static int NOT_REACHABLE = 2;

    public int reachabilityInformation;

    public TMLADWriteChannel(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 30;
        height = 20;
        minWidth = 30;



        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, lineLength, false, true, 0.5, 1.0);

        moveable = true;
        editable = true;
        removable = true;

        makeValue();
        name = "write channel";

        myImageIcon = IconManager.imgic900;
		latencyVals = new ConcurrentHashMap<String, String>();
    }

	public ConcurrentHashMap<String, String> getLatencyMap(){
		return latencyVals;
	}

    public void internalDrawing(Graphics g) {
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
        //g.drawRoundRect(x, y, width, height, arc, arc);

        if (stateOfError > 0)  {
            Color c = g.getColor();
            switch(stateOfError) {
            case ErrorHighlight.OK:
                g.setColor(ColorManager.TML_PORT_CHANNEL);
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

        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);

        int x1 = x + 1;
        int y1 = y + 1;
        int height1 = height;
        int width1 = width;
        Color c = g.getColor();
        g.setColor(ColorManager.TML_PORT_CHANNEL);
        g.drawLine(x1, y1, x1+width1-linebreak, y1);
        g.drawLine(x1, y1+height1, x1+width1-linebreak, y1+height1);
        g.drawLine(x1, y1, x1, y1+height1);
        g.drawLine(x1+width1-linebreak, y1, x1+width1, y1+height1/2);
        g.drawLine(x1+width1-linebreak, y1+height1, x1+width1, y1+height1/2);
        g.setColor(c);

        g.drawLine(x, y, x+width-linebreak, y);
        g.drawLine(x, y+height, x+width-linebreak, y+height);
        g.drawLine(x, y, x, y+height);
        g.drawLine(x+width-linebreak, y, x+width, y+height/2);
        g.drawLine(x+width-linebreak, y+height, x+width, y+height/2);
		if (isAttacker){
	        g.drawString("attack", x+(width-w) / 2, y);
		}
		else {
	        g.drawString("chl", x+(width-w) / 2, y);
		}
        g.drawString(value, x + (width - w) / 2 , y + textY);
	if (!securityContext.equals("")){
	    g.drawString("sec:"+securityContext, x+3*width/4, y+height+textY);
	}
				
		if (getCheckLatency()){
			ConcurrentHashMap<String, String> latency =tdp.getMGUI().getLatencyVals(getDIPLOID());
			//System.out.println(latency);
			if (latency!=null){
				latencyVals=latency;
				drawLatencyInformation(g);
			}
		}
		drawReachabilityInformation(g);
    }

	public void drawLatencyInformation(Graphics g){
		int index=1;
		for (String s:latencyVals.keySet()){
			int w  = g.getFontMetrics().stringWidth(s);
			g.drawString(s, x-latencyX-w+1, y-latencyY*index-2);
			g.drawRect(x-latencyX-w, y-latencyY*index-textHeight, w+4, textHeight); 
			g.drawLine(x,y,x-latencyX, y-latencyY*index);
			g.drawString(latencyVals.get(s), x-latencyX/2, y-latencyY*index/2);
			index++;
		}
	}


	public void addLatency(String name, String num){
		latencyVals.put(name,num);
	}

    public void drawReachabilityInformation(Graphics g) {
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
	    if (reachabilityInformation==NOT_REACHABLE){
	        g.drawLine(x-14, y-9, x-1, y+3);
	    	g.drawLine(x-14, y+3, x-1, y-9);
	    }
        }
    }
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
        value = channelName + "(" + nbOfSamples + ")";
    }

    /*public String getChannelName() {
      return channelName;
      }*/

    public String[] getChannelsByName() {
        //int nbOfChannels = Conversion.nbChar(channelName, ',') + 1;
        String tmp = Conversion.replaceAllChar(channelName, ' ', "");
        String[] channels = tmp.split(",");
        return channels;
    }

    public String getChannelName(int _index) {
        return  getChannelsByName()[_index];
    }

    public String getSamplesValue() {
        return nbOfSamples;
    }

    public String getAction() {
        return value;
    }

    public boolean editOndoubleClick(JFrame frame) {
    	String [] labels = new String[4];
        String [] values = new String[4];
        labels[0] = "Channel name";
        values[0] = channelName;
        labels[1] = "Nb of samples";
        values[1] = nbOfSamples;
        labels[2] = "Security Pattern";
        values[2] = securityContext;
		labels[3] = "Attacker?";
		values[3] = isAttacker ? "Yes" : "No";
        ArrayList<String []> help = new ArrayList<String []>();
		String[] allOutChannels = tdp.getMGUI().getAllOutChannels();
		if (isAttacker){
			allOutChannels =tdp.getMGUI().getAllCompOutChannels();
		}
		String[] choice = new String[]{"Yes", "No"};
		help.add(allOutChannels);
		help.add(null);
		help.add(tdp.getMGUI().getCurrentCryptoConfig());
		help.add(choice);

        //JDialogTwoString jdts = new JDialogTwoString(frame, "Setting channel's properties", "Channel name", channelName, "Nb of samples", nbOfSamples);
		JDialogMultiString jdms = new JDialogMultiString(frame, "Setting channel's properties", 4, labels, values, help);
        //jdms.setSize(600, 300);
        GraphicLib.centerOnParent(jdms, 600, 300);
        jdms.setVisible( true ); // blocked until dialog has been closed

        if (jdms.hasBeenSet() && (jdms.hasValidString(0))) {
            channelName = jdms.getString(0);
            nbOfSamples = jdms.getString(1);
		    securityContext = jdms.getString(2);
			isAttacker=jdms.getString(3).equals("Yes");
            makeValue();
            return true;
        }

        return false;

    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data channelName=\"");
        sb.append(channelName);
        sb.append("\" nbOfSamples=\"");
        sb.append(getSamplesValue());
        sb.append("\" secPattern=\"");
        sb.append(securityContext);
		sb.append("\" isAttacker=\"");
        sb.append(isAttacker ? "Yes": "No");
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro *** " + getId());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;

            //System.out.println("Loading Synchronization gates");
            //System.out.println(nl.toString());

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                channelName = elt.getAttribute("channelName");
                                nbOfSamples = elt.getAttribute("nbOfSamples");
                                securityContext = elt.getAttribute("secPattern");
								isAttacker = elt.getAttribute("isAttacker").equals("Yes");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        makeValue();
    }


    public int getType() {
        return TGComponentManager.TMLAD_WRITE_CHANNEL;
    }

    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLAD;
    }
    public String getChannelName(){
	return channelName;
    }
    public void setChannelName(String s){
	channelName=s;
	makeValue();
    } 
    public void setSamples(String sp){
	nbOfSamples=sp;
	makeValue();
    }

	public String getSecurityContext(){
		return securityContext;
	}

	public void setSecurityContext(String sc){
		securityContext=sc;
	}


	public boolean isAttacker(){
		return isAttacker;
	}

    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }

}

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




package ui.avatarsmd;

import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAvatarSignal;

import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ui.tmlad.TMLADReadChannel;
/**
 * Class AvatarSMDReceiveSignal
 * Action of receiving a signal
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 */
public class AvatarSMDReceiveSignal extends AvatarSMDBasicComponent implements CheckableAccessibility, CheckableLatency, BasicErrorHighlight, PartOfInvariant {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
	protected int textX1 = 2;
   
	private ConcurrentHashMap<String, String> latencyVals;
	private TGComponent reference;	

	protected int latencyX=30;
	protected int latencyY=25;
	protected int textWidth=10;
	protected int textHeight=20;

	protected int stateOfError = 0; // Not yet checked
    
    public AvatarSMDReceiveSignal(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, lineLength, false, true, 0.5, 1.0);
        
		addTGConnectingPointsComment();
		
        moveable = true;
        editable = true;
        removable = true;
        
        name = "Receive signal";
		value = "sig()";
        //makeValue();
        
        myImageIcon = IconManager.imgic908;
		latencyVals = new ConcurrentHashMap<String, String>();
		//latencyVals.put("sendChannel: sensorData", "15");
    }
    

	public void addLatency(String name, String num){
		latencyVals.put(name,num);
	}
    public void internalDrawing(Graphics g) {
		
        int w  = g.getFontMetrics().stringWidth(value + textX1);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
		
		
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			// Making the polygon
			int [] px1 = {x, x+width, x+width, x, x+linebreak};
			int [] py1 = {y, y, y+height, y+height, y+(height/2)};
			g.fillPolygon(px1, py1, 5);
			g.setColor(c);
		}
		
        //g.drawRoundRect(x, y, width, height, arc, arc);
		Color c = g.getColor();
		//System.out.println("Color=" + c);
		
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
		
		/*if (g.getColor().equals(ColorManager.NORMAL_0)) {
			g.setColor(ColorManager.TML_PORT_EVENT);
		}*/
		
		int x1 = x + 1;
		int y1 = y + 1;
		int height1 = height;
		int width1 = width;
		g.setColor(ColorManager.AVATAR_RECEIVE_SIGNAL);
		g.drawLine(x1, y1, x1+width1, y1);
        g.drawLine(x1+width1, y1, x1+width1, y1+height1);
        g.drawLine(x1, y1+height1, x1+width1, y1+height1);
        g.drawLine(x1, y1, x1+linebreak, y1+height1/2);
        g.drawLine(x1, y1+height1, x1+linebreak, y1+height1/2);
		g.setColor(c);
		
        g.drawLine(x, y, x+width, y);
        g.drawLine(x+width, y, x+width, y+height);
        g.drawLine(x, y+height, x+width, y+height);
        g.drawLine(x, y, x+linebreak, y+height/2);
        g.drawLine(x, y+height, x+linebreak, y+height/2);
		
		   
        //g.drawString("sig()", x+(width-w) / 2, y);
        g.drawString(value, x + linebreak + textX1, y + textY);
		//System.out.println(getDIPLOID());
		if (getCheckLatency()){
			ConcurrentHashMap<String, String> latency =tdp.getMGUI().getLatencyVals(getAVATARID());
			if (latency!=null){
				latencyVals=latency;
				drawLatencyInformation(g);
			}
		}
		
    }
	public void drawLatencyInformation(Graphics g){
		int index=1;
		for (String s:latencyVals.keySet()){
			int w  = g.getFontMetrics().stringWidth(s);
			g.drawString(s, x-latencyX-w+1, y-latencyY*index-2);
			g.drawRect(x-latencyX-w, y-latencyY*index-textHeight, w+4, textHeight); 
			g.drawLine(x,y,x-latencyX, y-latencyY*index);
			Color c = g.getColor();
			if (reference instanceof TMLADReadChannel){
			//	System.out.println("ref " + reference.toString().split(": ")[1].split("\\(")[0] + " " + s.split("-")[1].split(":")[0]);
				TMLADReadChannel rc = (TMLADReadChannel) reference;
				ConcurrentHashMap<String, String> refLats =rc.getLatencyMap();
				//System.out.println("referencelats " + refLats);
				for (String checkpoint:refLats.keySet()){
					if (s.split("\\-")[1].split(":")[0].equals(checkpoint.split("channel:")[1].split(" ")[0])){
						String time=refLats.get(checkpoint);
						int tdip= Integer.valueOf(time);
						int tav=Integer.valueOf(latencyVals.get(s));
						if (Math.abs(tdip-tav)>tdip){
							g.setColor(Color.RED);		
						}
						else {
							g.setColor(Color.GREEN);
						}
					}
				}
			}
			g.drawString(latencyVals.get(s), x-latencyX/2, y-latencyY*index/2);
			g.setColor(c);
			index++;
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
        /*boolean first = true;
        value = eventName + "(";
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
        value += ")";*/
        
    }
    
    public String getSignalName() {
       if (value == null) {
			return null;
		}
		
		if (value.length() == 0) {
			return "";
		}
		
		int index = value.indexOf('(');
		if (index == -1) {
			return value;
		}
		return value.substring(0, index).trim();
    }
	
	// Return -1 in case of error
	public int getNbOfValues() {
		return AvatarSignal.getNbOfValues(value);
	}
	
	// Return null in case of error
	public String getValue(int _index) {
		return AvatarSignal.getValue(value, _index);
	}
    
    /*public String getParamValue(int i) {
        return params[i];
    }
    
    public int nbOfParams() {
        return nParam;
    }*/
	
    
    public boolean editOndoubleClick(JFrame frame) {
		LinkedList<AvatarSignal> signals = tdp.getMGUI().getAllSignals();
		TraceManager.addDev("Nb of signals:" + signals.size());
		

		ArrayList<TGComponent> comps = tdp.getMGUI().getAllLatencyChecks();
		Vector<TGComponent> refs = new Vector<TGComponent>();
		for (TGComponent tg:comps){
			if (tg instanceof TMLADReadChannel){
				refs.add(tg);
			}
		}
		JDialogAvatarSignal jdas = new JDialogAvatarSignal(frame, "Setting receive signal",  value, signals, false, reference, refs);
		//jdas.setSize(350, 300);
        GraphicLib.centerOnParent(jdas, 550, 300);
        jdas.setVisible( true ); // blocked until dialog has been closed
		
		if (jdas.hasBeenCancelled()) {
			return false;
		}
		
		String val = jdas.getSignal();
		if (jdas.getReference()!=null){
			reference = jdas.getReference();
		}
		if (val.indexOf('(') == -1) {
			val += "()";
		}
		
		// valid signal?
		if (AvatarSignal.isAValidUseSignal(val)) {
			value = val;
			return true;
		}
		
		JOptionPane.showMessageDialog(frame,
			"Could not change the setting of the signal: invalid declaration",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
		return false;
         
    }
    

    public int getType() {
        return TGComponentManager.AVATARSMD_RECEIVE_SIGNAL;
    }
    
     public int getDefaultConnector() {
      return TGComponentManager.AVATARSMD_CONNECTOR;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}

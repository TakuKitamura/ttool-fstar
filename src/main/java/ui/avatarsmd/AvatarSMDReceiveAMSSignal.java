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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import myutil.GraphicLib;
import myutil.TraceManager;
import ui.AvatarSignal;
import ui.BasicErrorHighlight;
import ui.CheckableAccessibility;
import ui.CheckableLatency;
import ui.ColorManager;
import ui.ErrorHighlight;
import ui.LinkedReference;
import ui.PartOfInvariant;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.avatarrd.AvatarRDRequirement;
import ui.tmlad.TMLADReadChannel;
import ui.util.IconManager;
import ui.window.JDialogAvatarSignal;
/**
 * Class AvatarSMDReceiveAMSSignal
 * Action of receiving a signal
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 */
public class AvatarSMDReceiveAMSSignal extends AvatarSMDBasicCanBeDisabledComponent /* Issue #69 AvatarSMDBasicComponent*/ implements CheckableAccessibility, LinkedReference, CheckableLatency, BasicErrorHighlight, PartOfInvariant {
    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
//	protected int textX1 = 2;
   
	private Map<String, String> latencyVals;

	protected int latencyX=30;
	protected int latencyY=25;
	protected int textWidth=10;
	protected int textHeight=20;

	protected int stateOfError = 0; // Not yet checked
    
    public AvatarSMDReceiveAMSSignal(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        textX =  5;
        textY =  15;
        initScaling(30, 20);
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, lineLength, false, true, 0.5, 1.0);
        
		addTGConnectingPointsComment();
		
        moveable = true;
        editable = true;
        removable = true;
        
        name = "Receive AMS signal";
		value = "sig()";
        //makeValue();
        
        myImageIcon = IconManager.imgic908;
		latencyVals = new ConcurrentHashMap<String, String>();
		//latencyVals.put("sendChannel: sensorData", "15");
    }
    
	public void addLatency(String name, String num){
		latencyVals.put(name,num);
	}
    
	@Override
	public void internalDrawing(Graphics g) {
        int w  = g.getFontMetrics().stringWidth(value + textX);
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
				g.setColor(ColorManager.AVATAR_RECEIVE_AMS_SIGNAL);
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

		//Color c = g.setColor(ColorManager.AVATAR_RECEIVE_AMS_SIGNAL);
		//
		
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
		
		/*if (g.getColor().equals(ColorManager.NORMAL_0)) {
			g.setColor(ColorManager.TML_PORT_EVENT);
		}*/
		
		int x1 = x + 1;
		int y1 = y + 1;
		int height1 = height;
		int width1 = width;
		g.setColor(ColorManager.AVATAR_RECEIVE_AMS_SIGNAL);
		g.drawLine(x1, y1, x1+width1, y1);
        g.drawLine(x1+width1, y1, x1+width1, y1+height1);
        g.drawLine(x1, y1+height1, x1+width1, y1+height1);
        g.drawLine(x1, y1, x1+linebreak, y1+height1/2);
        g.drawLine(x1, y1+height1, x1+linebreak, y1+height1/2);
		g.setColor(c);

		final Polygon shape = new Polygon();
		shape.addPoint( x, y );
		shape.addPoint( x + width, y );
		shape.addPoint( x + width, y + height );
		shape.addPoint( x, y + height );
		shape.addPoint( x + linebreak, y + height / 2 );
		g.drawPolygon( shape );
		
//        g.drawLine(x, y, x+width, y);
//        g.drawLine(x+width, y, x+width, y+height);
//        g.drawLine(x, y+height, x+width, y+height);
//        g.drawLine(x, y, x+linebreak, y+height/2);
//        g.drawLine(x, y+height, x+linebreak, y+height/2);
		
        // Issue #69
    	if ( !isEnabled() && isContainedInEnabledState() ) {
	    	g.setColor( ColorManager.DISABLED_FILLING );
	    	g.fillPolygon( shape );
	    	g.setColor( c );
    	}
		   
        //g.drawString("sig()", x+(width-w) / 2, y);
        
//    	g.drawString(value, x + linebreak + textX, y + textY);
		drawSingleString(g, value, x + linebreak + textX, y + textY);
    	//
		if (getCheckLatency()){
			ConcurrentHashMap<String, String> latency =tdp.getMGUI().getLatencyVals(getAVATARID());
			if (latency!=null){
				latencyVals=latency;
				drawLatencyInformation(g);
			}
		}
		
		if (reference!=null){
			if (reference instanceof AvatarRDRequirement){
				AvatarRDRequirement refReq = (AvatarRDRequirement) reference;
				//Issue #31
//				g.drawString("ref: "+ refReq.getValue(), x, y+height1+textY);
				drawSingleString(g,"ref: "+ refReq.getValue(), x, y+height1+textY);
			}
		}
    }
	
	private void drawLatencyInformation(Graphics g){
		int index=1;
		for (String s:latencyVals.keySet()){
			int w  = g.getFontMetrics().stringWidth(s);
//			g.drawString(s, x-latencyX-w+1, y-latencyY*index-2);
			drawSingleString(g, s, x-latencyX-w+1, y-latencyY*index-2);
			
			g.drawRect(x-latencyX-w, y-latencyY*index-textHeight, w+4, textHeight); 
			g.drawLine(x,y,x-latencyX, y-latencyY*index);
			Color c = g.getColor();
			if (reference !=null){
				//References must be in the form "The max delay between send/recieve signal:(signalname) and send/receive signal (signalname) is (less than/greater than) X.
				if (reference instanceof AvatarRDRequirement){
					String req= ((AvatarRDRequirement) reference).getText().trim();
					if (req.contains("The max delay between")){
						//Attempt to parse string
						boolean lessThan= req.contains(" less than ");
						String sig1 = req.split(" between ")[1].split(" and ")[0].trim();
						String sig2 = req.split(" and ")[1].split(" is ")[0].trim();
						String num = req.split(" than ")[1];
	
						num = num.replaceAll("\\.","");
						
						int refNum = -1;
						try { 
							refNum = Integer.valueOf(num);
						}
						catch(Exception e){
						}
						
						if (sig1.equals("receive signal: " + value.split("\\(")[0])){
							if (sig2.replaceAll(": ","-").equalsIgnoreCase(s)){
								//Compare times
								int tActual=Integer.valueOf(latencyVals.get(s.split(":")[0]));
								if (refNum>0){
									if (lessThan){
										if (tActual < refNum){
											g.setColor(Color.GREEN);
										}
										else {
											g.setColor(Color.RED);	
										}	
									}
									else {
										if (tActual> refNum){
											g.setColor(Color.GREEN);
										}
										else {
											g.setColor(Color.RED);	
										}
									}
								}
							}
						}
						else if (sig2.equals("receive signal: " + value.split("\\(")[0])){
							if (sig1.replaceAll(": ","-").trim().equalsIgnoreCase(s.split(":")[0].trim())){
								//Compare times
								int tActual=Integer.valueOf(latencyVals.get(s));
								//
								if (refNum>0){
									if (lessThan){
										if (tActual < refNum){
											g.setColor(Color.GREEN);
										}
										else {
											g.setColor(Color.RED);	
										}	
									}
									else {
										if (tActual> refNum){
											g.setColor(Color.GREEN);
										}
										else {
											g.setColor(Color.RED);	
										}
									}
								}
							}
						}
					}
				}
				if (reference instanceof TMLADReadChannel){
					TMLADReadChannel rc = (TMLADReadChannel) reference;
					Map<String, String> refLats =rc.getLatencyMap();
					//
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
			}
			
//			g.drawString(latencyVals.get(s), x-latencyX/2, y-latencyY*index/2);
			drawSingleString(g, latencyVals.get(s), x-latencyX/2, y-latencyY*index/2);
			g.setColor(c);
			index++;
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
    
    //public void makeValue() {
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
        
  //  }
    
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
	

	@Override
    public boolean editOndoubleClick(JFrame frame) {
		List<AvatarSignal> signals = tdp.getMGUI().getAllSignals();
		TraceManager.addDev("Nb of signals:" + signals.size());
		

		List<TGComponent> comps = tdp.getMGUI().getAllLatencyChecks();
		Vector<TGComponent> refs = new Vector<TGComponent>();
		for (TGComponent req: tdp.getMGUI().getAllRequirements()){
            //
            if (req instanceof AvatarRDRequirement){
                refs.add(((AvatarRDRequirement) req));
            }
        }
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
		//if (jdas.getReference()!=null){
			reference = jdas.getReference();
		//}
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
    
	@Override
    public int getType() {
        return TGComponentManager.AVATARSMD_RECEIVE_AMS_SIGNAL;
    }
    
	@Override
    public int getDefaultConnector() {
		return TGComponentManager.AVATARSMD_CONNECTOR;
    }
	
	@Override
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}

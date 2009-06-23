/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TMLADWriteChannel
 * Action of writting data in channel
 * Creation: 17/11/2005
 * @version 1.0 17/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlad;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class TMLADWriteChannel extends TGCWithoutInternalComponent implements CheckableAccessibility, EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
    
    protected String channelName = "ch";
    protected String nbOfSamples= "1";
	
	protected int stateOfError = 0; // Not yet checked
    
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
        
        g.drawString("chl", x+(width-w) / 2, y);
        g.drawString(value, x + (width - w) / 2 , y + textY);
		
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
    
    public String getChannelName() {
        return channelName;
    }
    
    public String getSamplesValue() {
        return nbOfSamples;
    }
    
    public String getAction() {
        return value;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        
        JDialogTwoString jdts = new JDialogTwoString(frame, "Setting channel's properties", "Channel name", channelName, "Nb of samples", nbOfSamples);
        jdts.setSize(350, 300);
        GraphicLib.centerOnParent(jdts);
        jdts.show(); // blocked until dialog has been closed
        
        if (jdts.hasBeenSet() && (jdts.hasValidString())) {
           channelName = jdts.getString1();
           nbOfSamples = jdts.getString2();
           
           makeValue();
           return true;
        }
        
        return false;
         
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data channelName=\"");
        sb.append(getChannelName());
        sb.append("\" nbOfSamples=\"");
        sb.append(getSamplesValue());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
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
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                channelName = elt.getAttribute("channelName");
                                nbOfSamples = elt.getAttribute("nbOfSamples");
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
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
    
}

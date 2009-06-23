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
 * Class TADTimeLimitedOfferWithLatency
 * Action which must happen before a duration + jitter has elasped. Otherwise, anoterh activity is executed.
 * To be used in activity diagrams
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ad;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public class TADTimeLimitedOfferWithLatency extends TGCWithInternalComponent implements ActionStateErrorHighlight {
    protected int lineLength = 25;
    protected int lineLengthLatency = 5;
    protected int lineDistance = 10;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int distanceStateLine = 20;
    protected int distanceTwoLines = 15;
    protected int minWidth;
    protected int widthLatency = 8;
    protected int heightLatency = 32;
    protected int upperLineLength = 20;
    protected int arrowLength = 10;
    private int incrementY = 4;
    private int segment = 4;
	
	protected int stateAction = 0; // 0: unchecked 1: attribute; 2: gate; 3:unknown
    
    //private boolean first;
    
    public TADTimeLimitedOfferWithLatency(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        
        nbConnectingPoint = 3;
        connectingPoint = new TGConnectingPoint[3];
        connectingPoint[0] = new TGConnectingPointAD(this, 0,  -heightLatency - lineLengthLatency - upperLineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointAD(this, 0, lineLength, false, true, 0.5, 1.0);
        connectingPoint[2] = new TGConnectingPointAD(this, distanceTwoLines, lineLength, false, true, 0.5, 1.0);
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 2;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        TGComponent tgc = new TGCOneLineText(x + textX + width + distanceStateLine, y + textY, width + distanceStateLine + 2, width + distanceStateLine + 10, textY - 10, textY + 10, true, this, _tdp);
        tgc.setValue("delay value");
        tgc.setName("value of the delay");
        tgcomponent[0] = tgc;
        
        int posx = width/2 + widthLatency + 2;
        int posy = textY/2 - heightLatency /2 - lineLengthLatency;
        tgc = new TGCOneLineText(x + posx + 3, y + posy, posx, posx+10, posy - 10, posy + 10, true, this, _tdp);
        tgc.setValue("latency value");
        tgc.setName("value of the latency");
        tgcomponent[1] = tgc;
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "action";
        name = "time-limited offer with latency";
        
        minWidth = 30;
        
        myImageIcon = IconManager.imgic220;
        
    }
    
    public void internalDrawing(Graphics g) {
        //System.out.println("Value=" + value);
        int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        int w2 = width;
        int x1 = x;
        if (w1 != width) {
            x = x + width/2 - w1/2;
            width = w1;
            //updateConnectingPoints();
            updateInternalComponents(width - w2, x - x1);
        }
		
		if (stateAction > 0)  {
			Color c = g.getColor();
			switch(stateAction) {
			case ErrorHighlight.GATE:
				g.setColor(ColorManager.GATE_BOX_ACTION);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRoundRect(x, y, width, height, arc, arc);
			g.setColor(c);
		}
        // action state
        g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawString(value, x + (width - w) / 2 , y + textY);
        
        // lines of the state
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        g.drawLine(x+(width/2), y -heightLatency - lineLengthLatency - upperLineLength, x+(width/2), y -heightLatency - lineLengthLatency);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLengthLatency);
        
        // time limited offer
        g.drawLine(x+(width/2), y -heightLatency - lineLengthLatency - upperLineLength + lineDistance, x+ width + distanceStateLine, y -heightLatency - lineLengthLatency - upperLineLength + lineDistance);
        g.drawLine(x+ width + distanceStateLine, y - lineLength + lineDistance - heightLatency, x+ width + distanceStateLine, y + height + lineLength - lineDistance);
        GraphicLib.arrowWithLine(g, 1, 1, arrowLength, x + width + distanceStateLine, y + height + lineLength - lineDistance, x + (width/2) + distanceTwoLines, y + height + lineLength - lineDistance, false);
        g.drawLine(x + (width/2) + distanceTwoLines, y + height + lineLength - lineDistance, x + (width/2) + distanceTwoLines, y + lineLength + height);
        
        int y1 = y - heightLatency - lineLengthLatency;
        x1 = x + width / 2;
        // latency
        for (int i=0; i<segment; i++) {
            g.drawLine(x1, y1, x1+widthLatency, y1 + incrementY);
            y1 += incrementY;
            g.drawLine(x1+widthLatency, y1, x1, y1+incrementY);
            y1 += incrementY;
        }
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        // state
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        
        // latency
        if (GraphicLib.isInRectangle(x1, y1, x + width/2, y - heightLatency - lineLengthLatency, widthLatency, heightLatency)) {
            return this;
        }
        
        // vertical line
        if ((int)(Line2D.ptSegDistSq(x + (width/2), y -heightLatency - lineLengthLatency - upperLineLength, x + (width/2), y + height + lineLength, x1, y1)) < distanceSelected) {
            return this;
        }
        // high horizontal line
        if ((int)(Line2D.ptSegDistSq(x + (width/2), y -heightLatency - lineLengthLatency - upperLineLength + lineDistance, x+ width + distanceStateLine, y -heightLatency - lineLengthLatency - upperLineLength + lineDistance, x1, y1)) < distanceSelected) {
            return this;
        }
        // vertical line on the right
        if ((int)(Line2D.ptSegDistSq(x+ width + distanceStateLine, y -heightLatency - lineLengthLatency - upperLineLength + lineDistance, x+ width + distanceStateLine, y + height + lineLength - lineDistance, x1, y1)) < distanceSelected) {
            return this;
        }
        // horizontal lower line
        if ((int)(Line2D.ptSegDistSq(x + width + distanceStateLine, y + height + lineLength - lineDistance, x + (width/2) + distanceTwoLines, y + height + lineLength - lineDistance, x1, y1)) < distanceSelected) {
            return this;
        }
        // vertical lower line
        if ((int)(Line2D.ptSegDistSq(x + (width/2) + distanceTwoLines, y + height + lineLength - lineDistance, x + (width/2) + distanceTwoLines, y + lineLength + height, x1, y1)) < distanceSelected) {
            return this;
        }
        return null;
    }
    
        /*public void updateConnectingPoints() {
                connectingPoint[0].setCdX(width / 2);
                connectingPoint[1].setCdX(width / 2);
                connectingPoint[2].setCdX(width / 2 + distanceTwoLines);
        }*/
    
    public void updateInternalComponents(int diffWidth, int diffX) {
        int x1 = tgcomponent[0].getX();
        int y1 = tgcomponent[0].getY();
        
        tgcomponent[0].setCdRectangle(width + distanceStateLine + 2, width + distanceStateLine + 10, textY - 10, textY + 10);
        tgcomponent[0].setCd(x1 + diffWidth + diffX, y1);
        
        int posx = width/2 + widthLatency + 2;
        int posy = textY/2 - heightLatency /2 - lineLengthLatency;
        
        tgcomponent[1].setCdRectangle(posx, posx+10, posy - 10, posy + 10);
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            setValue(s);
            return true;
        }
        return false;
    }
    
    public String getAction() {
        return value;
    }
    
    public String getDelay() {
        return tgcomponent[0].getValue();
    }
    
    public String getLatency() {
        return tgcomponent[1].getValue();
    }
    
    public void setAction(String s) {
        setValue(s);
    }
    
    public void setLatency(String lat) {
        tgcomponent[1].setValue(lat);
    }
    
    public void setDelay(String delay) {
        tgcomponent[0].setValue(delay);
    }
    
    public int getType() {
        return TGComponentManager.TAD_TIME_LIMITED_OFFER_WITH_LATENCY;
    }
    
   	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_AD_DIAGRAM;
    }
	
	public void setStateAction(int _stateAction) {
		stateAction = _stateAction;
	}
 
}

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
 * Class TADTimeCapture
 * Capturing the time in an activity diagram
 * Creation: 23/07/2009
 * @version 1.0 23/07/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ad;

import java.awt.*;
import java.awt.geom.*;

import myutil.*;
import ui.*;

public class TADTimeCapture extends TGCOneLineText implements ActionStateErrorHighlight {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
	
	protected int stateAction = 0; // 0: unchecked 1: attribute; 3:unknown

	
    public TADTimeCapture(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointAD(this, 0, lineLength, false, true, 0.5, 1.0);
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "x";
        name = "Capture time in variable:";
        
        myImageIcon = IconManager.imgic204;
    }
    
    public void internalDrawing(Graphics g) {
		String myVal = "time -> " + value;
        int w  = g.getFontMetrics().stringWidth(myVal);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
		
		
		if (stateAction > 0)  {
			Color c = g.getColor();
			switch(stateAction) {
			case ErrorHighlight.ATTRIBUTE:
				g.setColor(ColorManager.ATTRIBUTE_BOX_ACTION);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRoundRect(x, y, width, height, arc, arc);
			g.setColor(c);
		}
        g.drawRoundRect(x, y, width, height, arc, arc);
		
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        g.drawString(myVal, x + (width - w) / 2 , y + textY);
		
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
        

        return null;
    }
    
    
    public int getType() {
        return TGComponentManager.TAD_TIME_CAPTURE;
    }
    
   	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_AD_DIAGRAM;
    }
	
	public void setStateAction(int _stateAction) {
		stateAction = _stateAction;
	}
    
}

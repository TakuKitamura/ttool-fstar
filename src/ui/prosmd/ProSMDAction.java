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
 * Class  ProSMDAction
 * 
 * Creation: 25/07/2006
 * @version 1.0 25/07/2006
 * @author Emil Salageanu, Ludovic APVRILLE
 * @see
 */

package ui.prosmd;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class   ProSMDAction extends TGCOneLineText {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    /*protected int arc = 5;*/
    protected int linebreak = 10;
    
    public   ProSMDAction(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "action state";
        value = "...";
        
        myImageIcon = IconManager.imgic2010;
    }
    
    public void internalDrawing(Graphics g) {
       
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
    	
    	int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX + linebreak);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
        
        // Lines to connecting points
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        // Drawing in clockwise fashion
       
        g.drawRect(x,y,width,height);
         //g.drawString("chl", x+(width-w) / 2, y);
        g.drawString(value, x + textX , y + textY);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    
    public String getAction() {
        return value;
    }
    

    public int getType() {
        return TGComponentManager.PROSMD_ACTION;
    } 
}

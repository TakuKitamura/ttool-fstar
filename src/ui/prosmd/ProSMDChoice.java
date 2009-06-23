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
 * Class ProSMDGetMsg
 * Action of getting a message
 * Creation: 10/07/2006
 * @version 1.1 11/07/2006
 * @author Ludovic APVRILLE, Emil Salageanu
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
import ui.ad.TGConnectingPointAD;
import ui.window.*;

public class ProSMDChoice extends TGCWithInternalComponent {
    protected int lineLength = 0;
    private int lineOutLength = 25;
    protected int textX =  5;
    protected int textY =  15;
    /*protected int arc = 5;*/
    protected int linebreak = 10;
    private int textX1, textY1, textX2, textY2, textX3, textY3;
    public ProSMDChoice(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 20;
        height = 20;
        
        textX1 = -lineOutLength;
        textY1 = height/2 - 5;
        textX2 = width + 5;
        textY2 = height/2 - 5;
        textX3 = width /2 + 5;
        textY3 = height + 15;
        
        minWidth = 5;
        
        nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
       
        //up point
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.5, 0.0);
        
        //left point
     //   connectingPoint[1] = new TGConnectingPointProSMD(this, width/2, -height/2, false, true, 0.5, 1.0);
        //right point 
     //   connectingPoint[2] = new TGConnectingPointProSMD(this, -width/2, -height/2, false, true, 0.5, 1.0);
        
        //down point
     //   connectingPoint[3] = new TGConnectingPointProSMD(this, 0, height, false, true, 0.5, 0.0);
        
        connectingPoint[1] = new TGConnectingPointProSMD(this, -lineOutLength, 0, false, true, 0.0, 0.5);
        connectingPoint[2] = new TGConnectingPointProSMD(this, lineOutLength, 0, false, true, 1.0, 0.5);
        connectingPoint[3] = new TGConnectingPointProSMD(this, 0, lineOutLength,  false, true, 0.5, 1.0); 
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        
        nbInternalTGComponent = 3;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        TGCOneLineText tgc = new TGCOneLineText(x+textX1-50, y+textY1, textX1-50, textX1+5, textY1, textY1 + 25, true, this, _tdp);
        tgc.setValue("[ ]");
        tgc.setName("guard 1");
        tgcomponent[0] = tgc;
        
        tgc = new TGCOneLineText(x+textX2, y+textY2, textX2, textX2+20, textY2, textY2+25, true, this, _tdp);
        tgc.setValue("[ ]");
        tgc.setName("guard 2");
        tgcomponent[1] = tgc;
        
        tgc = new TGCOneLineText(x+textX3, y+textY3, textX3, textX3+20, textY3, textY3+25, true, this, _tdp);
        tgc.setValue("[ ]");
        tgc.setName("guard 3");
        tgcomponent[2] = tgc;
        name = "choice";
       
        
        myImageIcon = IconManager.imgic2004;
    }
    
    
    public void internalDrawing(Graphics g) {
             
        // Lines to connecting points
      //  g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
      //  g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
		  
    	int p1x=x+width/2;
    	int p1y=y;
    	int p2x=x+width;
    	int p2y=y+height/2;
    	int p3x=x+width/2;
    	int p3y=y+height;
    	int p4x=x;
    	int p4y=y+height/2;
    	
    	// Drawing in clockwise fashion
        g.drawLine(p1x, p1y, p2x, p2y);
        g.drawLine(p2x, p2y, p3x, p3y);
        g.drawLine(p3x, p3y, p4x, p4y);
        g.drawLine(p4x, p4y, p1x, p1y); 
        
        //drawing the outgoing lines:
        g.drawLine(p2x, p2y, p2x+lineOutLength, p2y);
        g.drawLine(p3x, p3y, p3x, p3y+lineOutLength);
        g.drawLine(p4x, p4y, p4x-lineOutLength, p4y);
        
        
    }
   
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getGuard(int i) {
        if ((i>=0) && (i<nbInternalTGComponent)) {
            return tgcomponent[i].getValue();
        }
        return "";
    }

    public void setGuard(String guard, int i) {
        if ((i>=0) && (i<nbInternalTGComponent)) {
           tgcomponent[i].setValue(guard);
        }
   }
    
    
     public int getType() {
        return TGComponentManager.PROSMD_CHOICE;
    } 
}
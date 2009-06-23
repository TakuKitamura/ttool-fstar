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
 * Class ProSMDJonction
 * Action of getting a message
 * Creation: 25/07/2006
 * @version 1.1 25/07/2006
 * @author  Emil Salageanu, Ludovic APVRILLE
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

public class ProSMDJunction extends TGCWithoutInternalComponent {
    protected int lineLength = 10;
    private String label;
    //protected int textX =  5;
    //protected int textY =  15;
    /*protected int arc = 5;*/
    //protected int linebreak = 10;
   
    public ProSMDJunction(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 10;
        height = 10;
        
       // textX1 = -lineOutLength;
       // textY1 = height/2 - 5;
       // textX2 = width + 5;
       // textY2 = height/2 - 5;
       // textX3 = width /2 + 5;
       // textY3 = height + 15;
        
        minWidth = 5;
        
        nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
               
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        
        connectingPoint[1] = new TGConnectingPointProSMD(this, 0, 0, true, false, 0.0, 0.5);
        
        connectingPoint[2] = new TGConnectingPointProSMD(this, width/2,-width/2, true, false, 0, 0.5);
       
        connectingPoint[3] = new TGConnectingPointProSMD(this, width,0, true, false, 0, 0.5); 
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        
        nbInternalTGComponent = 0;
        
        name = "junction";
       
        
        myImageIcon = IconManager.imgic2006;
    }
    
    public void setLabel(String l)
    {
    	label=l;
    }

    public String getLabel()
    {
    	return label;
    }
    
    public void internalDrawing(Graphics g) {
       
    	
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
		  
    	g.fillOval(x, y, width, height);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.PROSMD_JUNCTION;
    }
    
}
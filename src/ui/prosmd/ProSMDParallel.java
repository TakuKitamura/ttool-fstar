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
 * Class TADParallel
 * Parallel operator. all activities start together and may synchronize on a list of gates
 * To be used in activity diagrams
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.prosmd;

import java.awt.*;

import myutil.*;

import ui.*;

public class ProSMDParallel extends TGCWithInternalComponent{
    private int lineLength = 0;
    private int textX, textY;
    
    public ProSMDParallel(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        
        width = 150;
        height = 5;
        
        textX = width - 10;
        textY = height - 8;
        
        nbConnectingPoint = 10;
        connectingPoint = new TGConnectingPoint[10];
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.167, 0.0);
        connectingPoint[1] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.333, 0.0);
        connectingPoint[2] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[3] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.667, 0.0);
        connectingPoint[4] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.833, 0.0);
        connectingPoint[5] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.167, 1.0);
        connectingPoint[6] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.333, 1.0);
        connectingPoint[7] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.5, 1.0);
        connectingPoint[8] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.667, 1.0);
        connectingPoint[9] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.833, 1.0);
        addTGConnectingPointsCommentCorner();
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        TGCOneLineText tgc = new TGCOneLineText(x+textX + 10, y+textY, textX - 25 , textX + 10 , textY, textY, true, this, _tdp);
        tgc.setValue("[ ]");
        tgc.setName("list of synchronization gates");
        tgcomponent[0] = tgc;
        
        moveable = true;
        editable = false;
        removable = true;
        
        name = "parallel / synchro";
        
        myImageIcon = IconManager.imgic206;
    }
    
    public void internalDrawing(Graphics g) {
       
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
    	
    	g.drawRect(x, y, width, height);
        g.fillRect(x, y, width, height);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getValueGate() {
        return tgcomponent[0].getValue();
    }
    
    public void setValueGate(String val) {
        tgcomponent[0].setValue(val);
    }
    
    public int getType() {
        return TGComponentManager.TAD_PARALLEL;
    }
}








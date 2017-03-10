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
 * Class SDActionState
 * Action state of a sequence diagram
 * Creation: 06/10/2004
 * @version 1.0 06/10/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

import java.awt.*;

import myutil.*;
import ui.*;

public class SDActionState extends TGCOneLineText implements SwallowedTGComponent {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int w; //w1;
    
    public SDActionState(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsCommentMiddle();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "action";
        name = "action state";
        
        myImageIcon = IconManager.imgic512;
    }
    
    public void internalDrawing(Graphics g) {
        w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) && (!tdp.isScaled())) {
            width = w1;
        }
        g.drawRoundRect(x - width/2, y, width, height, arc, arc);
        
        g.drawString(value, x - w / 2 , y + textY);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x - width/2, y, width, height)) {
            return this;
        }
        return null;
    }
    
 
    public String getAction() {
        return value;
    }
    
    public String getAction(int cpt) {
        if (cpt <0) {
            return value;
        }
        
        String ret;
        
        try {
            ret = value;
            while(cpt >0) {
                ret = ret.substring(ret.indexOf(';') + 1, ret.length());
                cpt --;
            }
            
            int index = ret.indexOf(';');
            
            if (index > 0) {
                ret = ret.substring(0, index+1);
            }
        } catch (Exception e) {
            return value;
        }
        return ret;
    }
    
    public int getType() {
        return TGComponentManager.SD_ACTION_STATE;
    }
  
}
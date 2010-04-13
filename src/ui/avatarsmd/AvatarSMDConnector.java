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
 * Class AvatarSMDConnector
 * Basic connector with a full arrow at the end. Used in state machine
 * Creation: 06/04/2010
 * @version 1.0 06/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarsmd;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public  class AvatarSMDConnector extends TGConnector {
    protected int arrowLength = 10;
	protected AvatarSMDTransitionInfo myTransitionInfo;
    
    public AvatarSMDConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
		
		//nbInternalTGComponent = 1;
        //tgcomponent = new TGComponent[nbInternalTGComponent];
        AvatarSMDTransitionInfo tgc = new AvatarSMDTransitionInfo(x, y+40, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, this, _tdp);
        tgc.setValue("");
        tgc.setName("List of all parameters of an Avatar SMD transition");
        tgc.setMoveWithFather(false);
        addInternalComponent(tgc, getNbInternalTGComponent());
		
		editable = true;
		
        myImageIcon = IconManager.imgic202;
    }
	
	/* public void internalDrawing(Graphics g) {
        
        TGComponent p3, p4;
		int previousx = 0, previousy = 0;
        
        if (nbInternalTGComponent>0) {
            p3 = tgcomponent[0];
            p4 = tgcomponent[0];
            //System.out.println("p3.x " + p3.getX() + " p3.y " + p3.getY());
			if (!(tgcomponent[0] instanceof AvatarSMDTransitionInfo)) {
				drawMiddleSegment(g, p1.getX(), p1.getY(), p3.getX(), p3.getY());
			}  else {
				previousx = p1.getX();
				previousy = p1.getY();
			}
            
            for(int i=0; i<nbInternalTGComponent-1; i++) {
				
				if (p4 instanceof AvatarSMDTransitionInfo) {
				} else {
					p4 = tgcomponent[i+1];
					drawMiddleSegment(g, previousx, previousy, p4.getX(), p4.getY());
					previousx = p4.getX();
					previousy = p4.getY();
				}
            }
            drawLastSegment(g, previousx, previousy, p2.getX(), p2.getY());
        } else {
            drawLastSegment(g, p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }*/
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }
    }
	
	public boolean editOndoubleClick(JFrame frame) {
		return ((AvatarSMDTransitionInfo)(tgcomponent[getFirstIndexOfOtherInternalComponents()])).editOndoubleClick(frame);
	}
    
    public int getType() {
        return TGComponentManager.AVATARSMD_CONNECTOR;
    }
}








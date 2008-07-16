/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TGConnectorProSMD
 * Basic connector with a full arrow at the end. Used in ProActive composite structure diagrams.
 * Creation: 20/07/2006
 * @version 1.0 20/07/2006
 * @author Emil Salageanu, Ludovic APVRILLE
 * @see
 */

/**
 * We have limited the possibilities of a Connector 
 * 
 */

package ui.procsd;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import myutil.*;
import ui.*;
import ui.cd.TGConnectorAssociation;

public  class TGConnectorDelegateProCSD extends TGConnectorProCSD {
    protected int arrowLength = 10;
        
    public TGConnectorDelegateProCSD(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, new Vector(0));
       // System.out.println("constructor"); 
       // System.out.println("list points "+_listPoint.toString());
        myImageIcon = IconManager.imgic202;
        automaticDrawing = false;
        
        if ((p1.getFather() instanceof ProCSDOutPort) || (p2.getFather() instanceof ProCSDInPort ))
        {  
        	TGConnectingPoint tmp=p1;
        	p1=p2;
        	p2=tmp;
     
        }
     //_tdp.finishAddingConnector(_p1);
    }
    
  
public void internalDrawing(Graphics g) {
        
        TGComponent p3, p4;
        
        if (nbInternalTGComponent>0) {
            p3 = tgcomponent[0];
            p4 = tgcomponent[0];
            //System.out.println("p3.x " + p3.getX() + " p3.y " + p3.getY());
            drawMiddleSegment(g, p1.getX(), p1.getY(), p3.getX(), p3.getY());
            
            for(int i=0; i<nbInternalTGComponent-1; i++) {
                p3 = tgcomponent[i];
                p4 = tgcomponent[i+1];
                drawMiddleSegment(g, p3.getX(), p3.getY(), p4.getX(), p4.getY());
            }
            drawLastSegment(g, p4.getX(), p4.getY(), p2.getX(), p2.getY());
        } else {
            drawLastSegment(g, p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }
        
    
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
		
    		g.setColor(Color.BLUE);
    		if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5)
    		{
    			//GraphicLib.dashedLine(g,x1, y1, x2, y2);
    			g.drawLine(x1, y1, x2, y2);
    		}
    		else
    		{
    			//Changed because the arrow was to the other side, by Solange
    			//GraphicLib.dashedArrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
    			//GraphicLib.dashedArrowWithLine(g, 1, 0, 10, x2, y2, x1, y1, true);
    			GraphicLib.arrowWithLine(g, 1, 0, 10, x2, y2, x1, y1, true);
    		}
    		g.setColor(Color.BLACK);
    }
    
  
    public int getType() {
        return TGComponentManager.CONNECTOR_DELEGATE_PROCSD;
    }
}







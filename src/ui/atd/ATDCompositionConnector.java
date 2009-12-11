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
 * Class ATDCompositionConnector
 * Connector used in Attack Tree Diagrams
 * Creation: 09/12/2009
 * @version 1.0 09/12/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.atd;



import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.cd.*;
import ui.window.*;

public  class ATDCompositionConnector extends TGConnector {
    protected int d = 20;
	protected int D = 26;
    //protected int widthValue, heightValue, maxWidthValue, h;
	
    
    public ATDCompositionConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "{info}";
        editable = true;
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
		Polygon p = new Polygon();
		Double alpha;
		
		
		if (x1 == x2) {
			if (y1 > y2) {
				p.addPoint(x2, y2+D);
				p.addPoint(x2+(d/2), y2+(D/2));
				p.addPoint(x2, y2);
				p.addPoint(x2-(d/2), y2+(D/2));
			} else {
				p.addPoint(x2, y2-D);
				p.addPoint(x2+(d/2), y2-(D/2));
				p.addPoint(x2, y2);
				p.addPoint(x2-(d/2), y2-(D/2));
			}
		} else {
			double xd[] = new double[4];
			double yd[] = new double[4];
			/*double a = ((double)y1-y2)/(x1-x2);
			
			alpha = Math.atan(a);
			if (x2 < x1) {
				x2 = (int)(x2 + (Math.cos(alpha)*D));
				y2 = (int)(y2 + (Math.sin(alpha)*D));
			} else {
				x2 = (int)(x2 - (Math.cos(alpha)*D));
				y2 = (int)(y2 - (Math.sin(alpha)*D));
			}
			
			int distance;
			for(int i=0; i<4; i++){
				if ((i%2) == 0) {
					distance = D;
				} else {
					distance = d;
				}
				xd[i] = x2 + (Math.cos(alpha)*distance);
				yd[i] = y2 + (Math.sin(alpha)*distance);
				p.addPoint((int)xd[i], (int)yd[i]);
				alpha = alpha + (Math.PI/2);
			}*/
			
			//P
			xd[0] = x2;
			yd[0] = y2;
			
			int x0 = x1 - x2;
			int y0 = y1 - y2;
			double k = 1/(Math.sqrt((x0*x0)+(y0*y0)));
			double u = x0*k;
			double v = y0*k;
			
			double Ex = D*u;
			double Ey = D*v;
			double Fx = d*v;
			double Fy = -d*u;
			
			//Q
			xd[1] = x2+((Ex+Fx)/2);
			yd[1] = y2+((Ey+Fy)/2);
			
			//R
			xd[2] = x2+Ex;
			yd[2] = y2+Ey;
			
			//S
			xd[3] = xd[1] - Fx;
			yd[3] = yd[1] - Fy;
			
			for(int i=0; i<4; i++) {
				p.addPoint((int)xd[i], (int)yd[i]);
			}
			
		}
		g.fillPolygon(p);
		g.drawLine(x1, y1, x2, y2);
    }
    
    
    public int getType() {
        return TGComponentManager.ATD_COMPOSITION_CONNECTOR;
    }
	

    
}

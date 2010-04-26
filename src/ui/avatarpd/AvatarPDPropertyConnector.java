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
* Class AvatarPDPropertyConnector
* Connector for Properties 
* Creation: 22/04/2010
* @version 1.0 22/04/2010
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarpd;



import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.cd.*;
import ui.window.*;

public  class AvatarPDPropertyConnector extends TGConnector implements ScalableTGComponent {
    //protected int arrowLength = 10;
    //protected int widthValue, heightValue, maxWidthValue, h;
	protected int c = 10; //square length 
	protected double oldScaleFactor;
	protected int fontSize = 12;
	protected int l = 4; // cross length;
	
	// value is set to "not" when the property is negated. 
	// Otherwise, it is set to "reg"
    
    public AvatarPDPropertyConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "reg";
        editable = true;
		oldScaleFactor = tdp.getZoom();
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
		g.drawLine(x1, y1, x2, y2);
        } else {
		GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
		
		//g.drawLine(x1, y1, x2, y2);
		int cz = (int)(tdp.getZoom() * c);
		//g.fillRect(x2-(cz/2), y2-(cz/2), cz, cz);
		//g.fillRect(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz);
		Color c = g.getColor();
		g.setColor(Color.white);
		g.fillOval(x2-(cz/2), y2 - (cz/2), cz, cz); 
		g.setColor(c);
		g.drawOval(x2-(cz/2), y2 - (cz/2), cz, cz); 
		
		//Point p = p2;//GraphicLib.intersectionRectangleSegment(x2-(cz/2), y2-(cz/2), cz, cz, x1, y1, x2, y2);
		
		cz = cz + 1;
		
		Point p = new Point();
		
		int x0 = x1 - x2;
		int y0 = y1 - y2;
		double k = 1/(Math.sqrt((x0*x0)+(y0*y0)));
		double u = x0*k;
		double v = y0*k;
		
		double Ex = cz/2*u;
		double Ey = cz/2*v;
		//double Fx = cz/2*v;
		//double Fy = -cz/2*u;
		
		p.x = (int)(x2+Ex);
		p.y = (int)(y2+Ey);
		
		if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
			//System.out.println("p.x=" + p.x + " x1=" + x1 + "p.y=" + p.y + " y1=" + y1);
			if ((x2 != x1) || (y2 != y1)) {
				g.drawLine(x1, y1, p.x, p.y);
				//System.out.println("drawn");
			}
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, p.x, p.y, true);
        }
		
		c = g.getColor();
		g.setColor(Color.white);
		g.fillOval(x1-(cz/2), y1 - (cz/2), cz, cz); 
		g.setColor(c);
		g.drawOval(x1-(cz/2), y1 - (cz/2), cz, cz);
		
		if (getValue().compareTo("not") == 0) {
			g.drawLine(x2-l, y2-l, x2+l, y2+l);
			g.drawLine(x2-l, y2+l, x2+l, y2-l);
		}
		
		
		/*if (value.length() > 0) {
		Font f = g.getFont();
		if (tdp.getZoom() < 1) {
		Font f0 =  f.deriveFont((float)(fontSize*tdp.getZoom()));
		g.setFont(f0);
		}
		g.drawString(value, x2-(cz/2), y2-(cz/2)-1);
		g.setFont(f);
		}*/
		
    }
	
	public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
		JDialogAvatarPropertyConnector jdapc = new JDialogAvatarPropertyConnector(frame, getValue().compareTo("not") == 0);
		jdapc.setSize(300, 200);
        GraphicLib.centerOnParent(jdapc);
        jdapc.setVisible(true); // blocked until dialog has been closed
		
		if (jdapc.hasBeenCancelled()) {
			return false;
		}
        
        if (jdapc.isNegated()) {
			value = "not";
		} else {
			value = "reg";
		}
		
		return true;
    }
    
    
    public int getType() {
        return TGComponentManager.APD_PROPERTY_CONNECTOR;
    }
	
	public void rescale(double scaleFactor){
		//System.out.println("Rescale connector");
		int xx, yy;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			xx = tgcomponent[i].getX();
			yy = tgcomponent[i].getY();
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
			tgcomponent[i].dx = (tgcomponent[i].dx + xx) / oldScaleFactor * scaleFactor;
			tgcomponent[i].dy = (tgcomponent[i].dy + yy) / oldScaleFactor * scaleFactor;
			xx = (int)(tgcomponent[i].dx);
			tgcomponent[i].dx = tgcomponent[i].dx - xx;
			yy = (int)(tgcomponent[i].dy);
			tgcomponent[i].dy = tgcomponent[i].dy - yy;
			
			tgcomponent[i].setCd(xx, yy);
			
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
        }
		
		oldScaleFactor = scaleFactor;
	}
	
	
    
}

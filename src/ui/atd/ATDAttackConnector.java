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
 * Class ATDAttackConnector
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

public  class ATDAttackConnector extends TGConnector implements ScalableTGComponent {
    //protected int arrowLength = 10;
    //protected int widthValue, heightValue, maxWidthValue, h;
	protected int c = 10; //square length 
	protected double oldScaleFactor;
	protected int fontSize = 12;
	
    
    public ATDAttackConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "";
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
		g.fillRect(x2-(cz/2), y2-(cz/2), cz, cz);
		g.fillRect(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz);
		
		Point p = GraphicLib.intersectionRectangleSegment(x2-(cz/2), y2-(cz/2), cz, cz, x1, y1, x2, y2);
		if (p == null) {
			//System.out.println("null point");
		} else {
		if (Point2D.distance(x1, y1, p.x, p.y) < GraphicLib.longueur * 1.5) {
			//System.out.println("p.x=" + p.x + " x1=" + x1 + "p.y=" + p.y + " y1=" + y1);
			if ((p.x != x1) || (p.y != y1)) {
				g.drawLine(x1, y1, p.x, p.y);
				//System.out.println("drawn");
			}
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, p.x, p.y, true);
        }
		}
		
		if (value.length() > 0) {
			Font f = g.getFont();
			if (tdp.getZoom() < 1) {
				Font f0 =  f.deriveFont((float)(fontSize*tdp.getZoom()));
				g.setFont(f0);
			}
			g.drawString(value, x2-(cz/2), y2-(cz/2)-1);
			g.setFont(f);
		}
	
    }
	
	public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + "Connector";
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "Setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if (s != null) {
            s = Conversion.removeFirstSpaces(s);
        }
		
		//System.out.println("emptytext=" + emptyText);
        
        if ((s != null) && (!s.equals(oldValue))) {
            setValue(s);
            return true;
        }
         
        return false;
    }
    
    
    public int getType() {
        return TGComponentManager.ATD_ATTACK_CONNECTOR;
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

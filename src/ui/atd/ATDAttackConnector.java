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

public  class ATDAttackConnector extends TGConnector {
    //protected int arrowLength = 10;
    //protected int widthValue, heightValue, maxWidthValue, h;
	protected int c = 10; //square length 
	
    
    public ATDAttackConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "";
        editable = true;
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
		
		//g.drawLine(x1, y1, x2, y2);
		g.fillRect(x2-(c/2), y2-(c/2), c, c);
		g.fillRect(p1.getX()-(c/2), p1.getY()-(c/2), c, c);
		
		Point p = GraphicLib.intersectionRectangleSegment(x2-(c/2), y2-(c/2), c, c, x1, y1, x2, y2);
		if (Point2D.distance(x1, y1, p.x, p.y) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, p.x, p.y);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, p.x, p.y, true);
        }
		
		if (value.length() > 0) {
			g.drawString(value, x2-(c/2), y2-(c/2)-2);
		}
	
    }
	
	    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if (s != null) {
            s = Conversion.removeFirstSpaces(s);
        }
		
		//System.out.println("emptytext=" + emptyText);
        
        if ((s != null) && ((s.length() > 0) && (!s.equals(oldValue)))) {
            setValue(s);
            //System.out.println("Value ok");
            return true;
        }
         
        return false;
    }
    
    
    public int getType() {
        return TGComponentManager.ATD_ATTACK_CONNECTOR;
    }
	

    
}

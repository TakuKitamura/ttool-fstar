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
 * Class ATDConstraint
 * Constraint of SysML Parametric diagrams, adapted to attack trees
 * Creation: 11/12/2009
 * @version 1.0 11/12/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.atd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class ATDConstraint extends TGCWithoutInternalComponent {
    private int textY1 = 20;
    //private int textY2 = 30;
	
	public static final String[] STEREOTYPES = {"<<OR>>", "<<AND>>", "<<SEQUENCE>>", "<<BEFORE>>", "<<AFTER>>"}; 
	
    protected String oldValue = "";
    
    public ATDConstraint(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 125;
        height = 40;
        minWidth = 100;
        
        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[12];
        
        connectingPoint[0] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[2] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[4] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[5] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[6] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[7] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[8] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[9] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[10] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[11] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        //addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "<<OR>>";
        
        myImageIcon = IconManager.imgic1078;
    }
    
    public void internalDrawing(Graphics g) {
        
        Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
		g.setColor(ColorManager.ATD_CONSTRAINT);
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
		Font f = g.getFont();
		g.setFont(f.deriveFont(Font.BOLD));
        int w  = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + (width - w)/2, y + textY1);
		g.setFont(f);
        
    }
    
   /* public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
		int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
		
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }*/
    

    
    
     public boolean editOndoubleClick(JFrame frame) {
		String tmp;
		boolean error = false;
		
		JDialogConstraint dialog = new JDialogConstraint(frame, "Setting constraint attributes", this);
		dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getStereotype() == null) {
			return false;
		}
		
		if (dialog.getStereotype().length() > 0) {
			value = dialog.getStereotype();
		}
			
		return true;
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.ATD_CONSTRAINT;
    }
  
    
}

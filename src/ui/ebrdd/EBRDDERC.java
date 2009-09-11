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
 * Class EBRDDERC
 * Event reception Container. To be used in EBRDDs
 * Creation: 02/05/2005
 * @version 1.1 21/05/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ebrdd;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class EBRDDERC extends TGCWithInternalComponent implements SwallowTGComponent {
    /*private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "CPU";*/
	
	 protected int lineLength = 5;
	
    
    public EBRDDERC(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 100;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointEBRDD(this, 0, - lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointEBRDD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findNodeName("ERC");
		value = "name";
        
        myImageIcon = IconManager.imgic1050;
    }
    
    public void internalDrawing(Graphics g) {
		Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
        
        // Top lines
        /*g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);
        
        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);*/
		
		// Filling color
		g.setColor(ColorManager.CPU_BOX_1);
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
		
		// Connecting lines
		g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        
        // String
        int w  = g.getFontMetrics().stringWidth(name);
        g.drawString(value, x + (width - w)/2, y+15);
		
		// Icon
		//g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
		//g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {  
      if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y + lineLength + height, x1, y1)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
    
    public boolean editOndoubleClick(JFrame frame) {
		//System.out.println("Double click");
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
        null,
        getValue());
        if ((s != null) && (s.length() > 0)) {
            setValue(s);
            return true;
        }
        return false;
    }
    
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof EBRDDESO) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((EBRDDESO)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
		
		if (tgc instanceof EBRDDERB) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((EBRDDERB)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    
    public Vector getElementList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if ((tgcomponent[i] instanceof EBRDDESO) || (tgcomponent[i] instanceof EBRDDERB)){
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof EBRDDESO) {
                ((EBRDDESO)tgcomponent[i]).resizeWithFather();
            }
			 if (tgcomponent[i] instanceof EBRDDERB) {
                ((EBRDDERB)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
	
	public int getType() {
        return TGComponentManager.EBRDD_ERC;
    }
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_EBRDD;
      }
	  
    
}

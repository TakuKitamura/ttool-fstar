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
 * Class ATDBlock
 * Node. To be used in Attack Tree Diagrams
 * Creation: 09/12/2009
 * @version 1.1 09/12/2009
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


public class ATDBlock extends TGCWithInternalComponent implements SwallowTGComponent {
    private int textY1 = 15;
    private int textY2 = 30;
    private String stereotype = "block";
	
    
    public ATDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 100;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new ATDCompositionConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findBlockName("Block");
		value = "name";
        
        myImageIcon = IconManager.imgic700;
    }
    
    public void internalDrawing(Graphics g) {
		Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
		g.setColor(ColorManager.ATD_BLOCK);
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
		Font f = g.getFont();
		g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
		g.setFont(f);
        w  = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (width - w)/2, y + textY2);
		
		// Icon
		//g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
		//g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getStereotype() {
        return stereotype;
        
    }
    
    public String getNodeName() {
        return name;
    }
    
   public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        
        //String text = getName() + ": ";
        String s = (String)JOptionPane.showInputDialog(frame, "Block name",
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Block: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isBlockNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Block: the new name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            setValue(s);
            recalculateSize();
            
            
            
            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the Block: this name is already in use",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                setValue(oldValue);
            }
        }
        return false;
    }
    
    
    public int getType() {
        return TGComponentManager.ATD_BLOCK;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof ATDAttack) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((ATDAttack)tgc).resizeWithFather();
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
    
    
    public Vector getAttackList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ATDAttack) {
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ATDAttack) {
                ((ATDAttack)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
    
   	public int getDefaultConnector() {
        return TGComponentManager.ATD_COMPOSITION_CONNECTOR;
      }
    
}

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
 * Class NCEqNode
 * Equipment node. To be used in NC diagrams.
 * Creation: 18/11/2008
 * @version 1.1 18/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ncdd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;


public class NCEqNode extends TGCWithInternalComponent implements SwallowTGComponent/*, WithAttributes*/ {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "Equipment";
	
	private int schedulingPolicy = 0; // 0: FCFS ; 1: SP
	private int type = 0; //0: Standard -> for future use, with client/server approach
    
    public NCEqNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 150;
        height = 120;
        minWidth = 150;
        minHeight = 100;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new NCNodeConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new NCNodeConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new NCNodeConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);
        
        connectingPoint[8] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new NCNodeConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new NCNodeConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new NCNodeConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findNodeName("eq");
		value = "name";
        
        myImageIcon = IconManager.imgic700;
    }
    
    public void internalDrawing(Graphics g) {
		Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
        
        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);
        
        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);
		
		// Filling color
		g.setColor(ColorManager.MEMORY_BOX);
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
		g.drawImage(IconManager.imgic1100.getImage(), x + width - 20, y + 4, null);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
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
	
	public int getSchedulingPolicy() {
		return schedulingPolicy;
	}
	
	public int getNodeType() {
		return type;
	}
	
	 public boolean editOndoubleClick(JFrame frame) {
	//System.out.println("Double click");
        String oldName = name;
		String tmp;
        
        JDialogNCEqNode jdncen = new JDialogNCEqNode(frame, "Setting equipment parameters", name, schedulingPolicy, type);
        jdncen.setSize(350, 250);
        GraphicLib.centerOnParent(jdncen);
        jdncen.show(); // Blocked until dialog has been closed
       
		if (jdncen.hasBeenCancelled()) {
			return false;
		}
		
        tmp = jdncen.getEqName().trim();
		
        if ((tmp == null) || (jdncen.hasBeenCancelled())) {
			return false;
        }
		
		 if ((tmp != null) && (tmp.length() > 0) && (!tmp.equals(oldName))) {
			 //boolean b;
            if (!TAttribute.isAValidId(tmp, false, false)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Switch: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isNCNameUnique(tmp)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Switch: the new name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
		 }
		
		name = tmp;
		schedulingPolicy = jdncen.getSchedulingPolicy();
		type = jdncen.getType();
		
        return true;
    }
    
    
    public int getType() {
        return TGComponentManager.NCDD_EQNODE;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof NCTrafficArtifact) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((NCTrafficArtifact)tgc).resizeWithFather();
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
    
    
    public Vector getArtifactList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof NCTrafficArtifact) {
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }
	
	public void addAllTraffics(ArrayList<String> list) {
		for(int i=0; i<nbInternalTGComponent; i++) {
           list.add(tgcomponent[i].getValue());
        }
	}
	
	public void addAllTrafficArtifacts(ArrayList<NCTrafficArtifact> list) {
		for(int i=0; i<nbInternalTGComponent; i++) {
           list.add((NCTrafficArtifact)(tgcomponent[i]));
        }
	}
	
	public boolean hasTraffic(NCTrafficArtifact arti) {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] == arti) {
				return true;
			}
		}
		return false;
	}
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof NCTrafficArtifact) {
                ((NCTrafficArtifact)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
    
 
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_NC;
      }
	 
	  public String getAttributes() {
		  String attr = "Type: ";
		if (type == 0) {
			attr += "Standard\n";
		} else {
			attr += "Unknown\n";
		}
		String pol;
		if (schedulingPolicy == 0) {
			pol = "FCFS";
		} else {
			pol = "SP";
		}
		attr += "Scheduling policy = " + pol + "\n";
		return attr;
		
	}
    
}

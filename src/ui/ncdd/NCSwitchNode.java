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
* Class NCSwitchNode
* Switch node. To be used in NC diagrams.
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


public class NCSwitchNode extends TGCWithInternalComponent implements SwallowTGComponent, WithAttributes {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "Switch";
	
	protected int switchingTechnique = 0; //0: SF (Store Forward) ; 1: CT (Cut Through)
	protected int schedulingPolicy = 0; // 0: FCFS ; 1: SP
	protected int capacity = 10;
	protected int technicalLatency = 60; // in microseconds 
	protected String capacityUnit = "Mbs";
    
    public NCSwitchNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 220;
        height = 180;
        minWidth = 150;
        minHeight = 100;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new NCNodeConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new NCNodeConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new NCNodeConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new NCNodeConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new NCNodeConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new NCNodeConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findNodeName("switch");
		value = "switch";
        
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
		g.setColor(ColorManager.CPU_BOX_1);
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
	
	public int getSwitchingTechnique() {
		return switchingTechnique;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public String getCapacityUnit() {
		return capacityUnit;
	}
	
	public int getTechnicalLatency() {
		return technicalLatency;
	}
    
   	public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click");
        String oldName = name;
		String tmp;
        
        JDialogNCSwitchNode jdncsn = new JDialogNCSwitchNode(frame, "Setting switch parameters", name, schedulingPolicy, switchingTechnique, capacity, capacityUnit, technicalLatency);
        jdncsn.setSize(350, 300);
        GraphicLib.centerOnParent(jdncsn);
        jdncsn.show(); // Blocked until dialog has been closed
       
		if (jdncsn.hasBeenCancelled()) {
			return false;
		}
		
        tmp = jdncsn.getSwitchName().trim();
		
        if ((tmp == null) || (jdncsn.hasBeenCancelled())) {
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
		schedulingPolicy = jdncsn.getSchedulingPolicy();
		switchingTechnique = jdncsn.getSwitchingTechnique();
		
		try {
			capacity = Integer.decode(jdncsn.getCapacity()).intValue();
		} catch (Exception e) {
			 JOptionPane.showMessageDialog(frame,
					"Wrong capacity value",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
        capacityUnit = jdncsn.getCapacityUnit();
		
		try {
			technicalLatency = Integer.decode(jdncsn.getTechnicalLatency()).intValue();
		} catch (Exception e) {
			 JOptionPane.showMessageDialog(frame,
					"Wrong technical latency value",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
		
        return true;
    }
    
    
    public int getType() {
        return TGComponentManager.NCDD_SWITCHNODE;
    }
	
	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		if (tgc instanceof NCRouteArtifact) {
			return true;
		}
		
		return false;
    }
    
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        if (tgc instanceof NCRouteArtifact) {
			 //Set its coordinates
			tgc.setFather(this);
			tgc.setDrawingZone(true);
            ((NCRouteArtifact)tgc).resizeWithFather();
			//add it
			addInternalComponent(tgc, 0);
			return true;
        }
        
        return false;
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    
    public ArrayList<NCRoute> getRoutesList() {
        ArrayList<NCRoute> list = new ArrayList<NCRoute>();
		NCRouteArtifact r;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof NCRouteArtifact) {
				r = (NCRouteArtifact)(tgcomponent[i]);
                list.addAll(r.getRoutes());
            }
        }
        return list;
    }
	
	public Vector<NCRouteArtifact> getArtifactList() {
       Vector<NCRouteArtifact> v = new Vector<NCRouteArtifact> ();
	   NCRouteArtifact r;
	   
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof NCRouteArtifact) {
				r = (NCRouteArtifact)(tgcomponent[i]);
                v.add(r);
            }
        }
        return v;
    }
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof NCRouteArtifact) {
                ((NCRouteArtifact)tgcomponent[i]).resizeWithFather();
            }
        }
        
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info schedulingPolicy=\"" + schedulingPolicy);
		sb.append("\" switchingTechnique=\"" + switchingTechnique);
		sb.append("\" technicalLatency=\"" + technicalLatency);
		sb.append("\" capacity=\"" + capacity);
		sb.append("\" capacityUnit=\"" + capacityUnit);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                schedulingPolicy = Integer.decode(elt.getAttribute("schedulingPolicy")).intValue();
								if (elt.getAttribute("switchingTechnique").length() > 0) {
									switchingTechnique = Integer.decode(elt.getAttribute("switchingTechnique")).intValue();
								}
								
								if (elt.getAttribute("capacity").length() > 0) {
									capacityUnit = elt.getAttribute("capacityUnit");
									capacity = Integer.decode(elt.getAttribute("capacity")).intValue();
								}
								if (elt.getAttribute("technicalLatency").length() > 0) {
									technicalLatency = Integer.decode(elt.getAttribute("technicalLatency")).intValue();
								}
							}
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
   	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_NC;
	}
	
	
	public String getAttributes() {
		String pol;
		if (schedulingPolicy == 0) {
			pol = "FCFS";
		} else {
			pol = "SP";
		}
		String attr = "Scheduling policy = " + pol + "\n";
		if (switchingTechnique == 0) {
			pol = "SF";
		} else {
			pol = "CT";
		}
		attr += "Switching tech. = " + pol + "\n";
		attr += "Capacity = " + capacity + " " + capacityUnit + "\n";
		attr += "Technical latency = " + technicalLatency;
		return attr;
		
	}
    
}

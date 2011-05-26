/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 * /**
 * Class AvatarSMDPanel
 * Panel used for drawing state machine diagrams of AVATAR blocks
 * Creation: 06/04/2010
 * @version 1.0 06/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarsmd;

//import java.awt.*;
import java.util.*;

//import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

import ui.*;

public class AvatarSMDPanel extends TDiagramPanel {
    
    public  AvatarSMDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        addComponent(400, 50, TGComponentManager.AVATARSMD_START_STATE, false);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        return false;
    }
    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }
    
    public  boolean actionOnRemove(TGComponent tgc) {
        return false;
    }
    
    public String getXMLHead() {
        return "<AVATARStateMachineDiagramPanel name=\"" + name + "\"" + sizeParam() + " >";
    }
    
    public String getXMLTail() {
        return "</AVATARStateMachineDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<AVATARStateMachineDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</AVATARStateMachineDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<AVATARStateMachineDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</AVATARStateMachineDiagramPanelCopy>";
    }
    
    public void makeGraphicalOptimizations() {
        // Segments of connector that mask components
        
        // Components over others
        
        // Position correctly guards of choice
    }
    
    public void enhance() {
        //System.out.println("enhance");
        Vector v = new Vector();
        Object o;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof AvatarSMDStartState){
                enhance(v, (AvatarSMDStartState)o);
            }
        }
        
        mgui.changeMade(this, MOVE_CONNECTOR);
        repaint();
    }
    
    public void enhance(Vector v, TGComponent tgc) {
        TGComponent tgc1;
        TGConnector tgcon;
        int i;
        
        //System.out.println("Enhancing: " + tgc);
        
        if (tgc == null) {
            return;
        }
        
        if (v.contains(tgc)) {
            return;
        }
        
        v.add(tgc);
        
        //System.out.println("Nb of nexts: " + tgc.getNbNext());
        if (!(tgc instanceof AvatarSMDStartState)) {
            for(i=0; i<tgc.getNbNext(); i++) {
                tgc1 = getNextTGComponent(tgc, i);
                tgcon = getNextTGConnector(tgc, i);
                if (tgcon.getAutomaticDrawing()) {
                    if ((tgc1 != null) && (tgcon != null)) {
                        tgcon.alignOrMakeSquareTGComponents();
                    }
                }
            }
        }
        
        // Explore next elements
        for(i=0; i<tgc.getNbNext(); i++) {
            tgc1 = getNextTGComponent(tgc, i);
            enhance(v, tgc1);
        }
    }
	
	public void setConnectorsToFront() {
		TGComponent tgc;
		
		//System.out.println("list size=" + componentList.size());
		
        Iterator iterator = componentList.listIterator();
        
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		
		//System.out.println("Putting to back ...");
		for(TGComponent tgc1: list) {
			//System.out.println("Putting to back: " + tgc1);
			componentList.remove(tgc1);
			componentList.add(tgc1);
		}
	}
	
    public boolean hasAutoConnect() {
		return true;
	}
	
	public void autoConnect(TGComponent added) {
		
		
		boolean cond = hasAutoConnect();
		
		if (!cond) {
			return;
		}
		
		int i, j;
		
		//TraceManager.addDev("Autoconnect");
		
		Vector listPoint = new Vector();
		
		Vector v = new Vector();
		
		int distance = 100;
        int dist1, dist2;
        int x1, y1;
		TGConnectingPoint found = null;
		int distanceTmp;
		
		boolean cd1, cd2;
		
		TGConnectingPoint tgcp, tgcp1;
		
		TGConnector tgco;
		
		TGComponent tgc;
        Iterator iterator;
        
        boolean inTaken = false;
        boolean outTaken = false;
        
        //Tries to locate the two closer connecting point both in and out
        // Connection can occur only from top to down
        
        
        int foundDistanceIn = 100;
        int foundDistanceOut = 100;
        TGConnectingPoint foundIn1 = null, foundIn2 = null;
        TGConnectingPoint foundOut1 = null, foundOut2 = null;
		
        for(i=0; i<added.getNbConnectingPoint(); i++) {
			tgcp = added.getTGConnectingPointAtIndex(i);
            // Only two at most : one up, one down!
			if (tgcp.isFree() && tgcp.isCompatibleWith(added.getDefaultConnector())) {
				
				// Try to connect that connecting point
				found = null;
				distance = 100;
				
				iterator = componentList.listIterator();
				while(iterator.hasNext()) {
					tgc = (TGComponent)(iterator.next());
					if (tgc != added) {
						for(j=0; j<tgc.getNbConnectingPoint(); j++) {
							tgcp1 = tgc.getTGConnectingPointAtIndex(j);
							if ((tgcp1 != null) && tgcp1.isFree()) {
                                if (tgcp1.isCompatibleWith(added.getDefaultConnector())) {
                                    if (tgcp1.getY() > tgcp.getY()) {
                                        // out connector
                                        if (tgcp.isOut() && tgcp1.isIn()) {
                                            distanceTmp = (int)(Math.sqrt(Math.pow(tgcp1.getX() - tgcp.getX(), 2) + Math.pow(tgcp1.getY() - tgcp.getY(), 2)));
                                            if (distanceTmp < foundDistanceOut) {
                                                foundDistanceOut = distanceTmp;
                                                foundOut1 = tgcp;
                                                foundOut2 = tgcp1;
                                            } else if ((distanceTmp == foundDistanceOut) && (foundOut1 != null)) {
                                                // Distance from the center
                                                x1 = added.getX() + added.getWidth() / 2;
                                                y1 = added.getY()  + added.getHeight() / 2;
                                                dist1 = (int)(Math.sqrt(Math.pow(foundOut1.getX() - x1, 2) + Math.pow(foundOut1.getY() - y1, 2)));
                                                dist2 = (int)(Math.sqrt(Math.pow(tgcp.getX() - x1, 2) + Math.pow(tgcp.getY() - y1, 2)));
                                                if (dist2 <= dist1) {
                                                    foundOut1 = tgcp;
                                                    foundOut2 = tgcp1;
                                                }
                                            }
                                        }
                                    } else {
                                        // In connector
                                        if (tgcp1.isOut() && tgcp.isIn()) {
                                            distanceTmp = (int)(Math.sqrt(Math.pow(tgcp1.getX() - tgcp.getX(), 2) + Math.pow(tgcp1.getY() - tgcp.getY(), 2)));
                                            if (distanceTmp < foundDistanceIn) {
                                                foundDistanceIn = distanceTmp;
                                                foundIn1 = tgcp1;
                                                foundIn2 = tgcp;
                                            } else if ((distanceTmp == foundDistanceIn) && (foundIn2 != null)) {
                                                x1 = added.getX() + added.getWidth() / 2;
                                                y1 = added.getY()  + added.getHeight() / 2;
                                                dist1 = (int)(Math.sqrt(Math.pow(foundIn2.getX() - x1, 2) + Math.pow(foundIn2.getY() - y1, 2)));
                                                dist2 = (int)(Math.sqrt(Math.pow(tgcp.getX() - x1, 2) + Math.pow(tgcp.getY() - y1, 2)));
                                                if (dist2 <= dist1) {
                                                    foundIn1 = tgcp1;
                                                    foundIn2 = tgcp;
                                                }
                                            }
                                        }
                                    }
                                }
							}
						}
					}
				}
				if (found != null) {
					//TraceManager.addDev("Adding connector");
					if (found.isIn()) {
						tgco = TGComponentManager.addConnector(tgcp.getX(), tgcp.getY(), added.getDefaultConnector(), this, tgcp, found, listPoint);
					} else {
						tgco = TGComponentManager.addConnector(found.getX(), found.getY(), added.getDefaultConnector(), this, found, tgcp, listPoint);
					}
					found.setFree(false);
					tgcp.setFree(false);
					componentList.add(tgco);
					//TraceManager.addDev("Connector added");
				}
			}
		}
        
        if (foundIn1 != null) {
            tgco = TGComponentManager.addConnector(foundIn1.getX(), foundIn1.getY(), added.getDefaultConnector(), this, foundIn1, foundIn2, listPoint);
            foundIn1.setFree(false);
            foundIn2.setFree(false);
            componentList.add(tgco);
        }
        
        if ((foundOut1 != null) && (foundOut1.isFree()) && (foundOut2.isFree())) {
            tgco = TGComponentManager.addConnector(foundOut1.getX(), foundOut1.getY(), added.getDefaultConnector(), this, foundOut1, foundOut2, listPoint);
            foundOut1.setFree(false);
            foundOut2.setFree(false);
            componentList.add(tgco);
        }
		//TraceManager.addDev("End Autoconnect");
	}
    
}

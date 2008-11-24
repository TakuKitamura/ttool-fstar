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
 * Class NCDiagramPanel
 * Panel for drawing an NC diagram
 * Creation: 18/11/2008
 * @version 1.0 18/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ncdd;


import org.w3c.dom.*;
import java.util.*;

import ui.*;

public class NCDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
	
    public  NCDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);
    }
    
    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        /*if (tgc instanceof TCDTClass) {
            TCDTClass t = (TCDTClass)tgc;
            return mgui.newTClassName(tp, t.oldValue, t.getValue());
        } else if (tgc instanceof TCDActivityDiagramBox) {
            if (tgc.getFather() instanceof TCDTClass) {
                mgui.selectTab(tp, tgc.getFather().getValue());
            } else if (tgc.getFather() instanceof TCDTObject) {
                TCDTObject to = (TCDTObject)(tgc.getFather());
                TCDTClass t = to.getMasterTClass();
                if (t != null) {
                    mgui.selectTab(tp, t.getValue());
                }
            }
            return false; // because no change made on any diagram
        }*/
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            //System.out.println(" *** add tclass *** name=" + tgcc.getClassName());
            mgui.addTClass(tp, tgcc.getClassName());
            return true;
        }*/
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.removeTClass(tp, tgcc.getClassName());
            resetAllInstancesOf(tgcc);
            return true;
        }*/
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            return actionOnDoubleClick(tgc);
        }*/
        return false;
    }
    
    public String getXMLHead() {
        return "<NCDiagramPanel name=\"" + name + "\"" + sizeParam()  + displayParam() + " >";
    }
    
    public String getXMLTail() {
        return "</NCDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<NCDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</NCDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<NCDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</NCDiagramPanelCopy>";
    }
	
	 public String displayParam() {
        String s = " attributes=\"";
		s += getAttributeState();
		s += "\"";
        return s;
    }
	
    
    public void loadExtraParameters(Element elt) {
        String s;
        //System.out.println("Extra parameter");
        try {
            s = elt.getAttribute("attributes");
            //System.out.println("S=" + s);
			int attr = Integer.decode(s).intValue();
			setAttributes(attr % 3);
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //System.out.println("older format");
			setAttributes(0);
        }
		

    }
    
    /*public boolean isFree(ArtifactTClassGate atg) {
        TGConnectorLinkNode tgco;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnectorLinkNode) {
                tgco = (TGConnectorLinkNode)tgc;
                if (tgco.hasArtifactTClassGate(atg)) {
                    return false;
                }   
            }
        }
        
        return true;
    }*/
    
    public LinkedList getListOfNodes() {
        LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCEqNode) {
                ll.add(tgc);
            }
			
			if (tgc instanceof NCSwitchNode) {
				 ll.add(tgc);
			}
			
        }
        
        return ll;
    }
	
	public LinkedList getListOfEqNode() {
        LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCEqNode) {
                ll.add(tgc);
            }
        }
        
        return ll;
    }
	
	public LinkedList getListOfSwitchNode() {
        LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCSwitchNode) {
                ll.add(tgc);
            }
        }
        
        return ll;
    }
    
     public LinkedList getListOfLinks() {
        LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCConnectorNode) {
                ll.add(tgc);
            }
        }
        
        return ll;
    }
	
	public ArrayList<String> getInterfaces(NCSwitchNode sw) {
		ListIterator iterator = getListOfLinks().listIterator();
		NCConnectorNode lk;
		TGConnectingPoint p;
		
		ArrayList<String> list = new ArrayList<String>();
		
		while(iterator.hasNext()) {
			lk = (NCConnectorNode)(iterator.next());
			p = lk.getTGConnectingPointP1();
			if (sw.belongsToMe(p)) {
				list.add(lk.getInterfaceName());
			} else {
				p = lk.getTGConnectingPointP2();
				if (sw.belongsToMe(p)) {
					list.add(lk.getInterfaceName());
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<NCRoute> getAllRoutesFor(NCSwitchNode sw, NCTrafficArtifact arti) {
		ArrayList<NCRoute> list = sw.getRoutesList();
		
		ArrayList<NCRoute> ret = new ArrayList<NCRoute>();
		
		for(NCRoute route :list) {
			if (route.traffic.equals(arti.getValue())) {
				ret.add(route);
			}
		}
		
		return ret;
	}
	
	public ArrayList<NCSwitchNode> getSwitchesOfEq(NCEqNode eq) {
		ListIterator iterator = getListOfLinks().listIterator();
		NCConnectorNode lk;
		TGConnectingPoint p;
		
		ArrayList<NCSwitchNode> list = new ArrayList<NCSwitchNode>();
		
		while(iterator.hasNext()) {
			lk = (NCConnectorNode)(iterator.next());
			p = lk.getTGConnectingPointP1();
			if (eq.belongsToMe(p)) {
				list.add((NCSwitchNode)(getComponentToWhichBelongs(lk.getTGConnectingPointP2())));
			} else {
				p = lk.getTGConnectingPointP2();
				if (eq.belongsToMe(p)) {
					list.add((NCSwitchNode)(getComponentToWhichBelongs(lk.getTGConnectingPointP1())));
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<NCConnectorNode> getConnectorOfEq(NCEqNode eq) {
		ListIterator iterator = getListOfLinks().listIterator();
		NCConnectorNode lk;
		TGConnectingPoint p;
		
		ArrayList<NCConnectorNode> list = new ArrayList<NCConnectorNode>();
		
		while(iterator.hasNext()) {
			lk = (NCConnectorNode)(iterator.next());
			p = lk.getTGConnectingPointP1();
			if (eq.belongsToMe(p)) {
				list.add(lk);
			} else {
				p = lk.getTGConnectingPointP2();
				if (eq.belongsToMe(p)) {
					list.add(lk);
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<NCTrafficArtifact> getTrafficArtifacts() {
		ListIterator iterator = getListOfEqNode().listIterator();
		NCEqNode eq;
		
		ArrayList<NCTrafficArtifact> list = new ArrayList<NCTrafficArtifact>();
		
		while(iterator.hasNext()) {
			eq = (NCEqNode)(iterator.next());
			eq.addAllTrafficArtifacts(list);
		}
		
		return list;
	}
	
	public NCEqNode getNCEqNodeOf(NCTrafficArtifact arti) {
		ListIterator iterator = getListOfEqNode().listIterator();
		NCEqNode eq;
		
		while(iterator.hasNext()) {
			eq = (NCEqNode)(iterator.next());
			if (eq.hasTraffic(arti)) {
				return eq;
			}
		}
		
		return null;

	}
	
	public ArrayList<String> getTraffics() {
		ListIterator iterator = getListOfEqNode().listIterator();
		NCEqNode eq;
		
		ArrayList<String> list = new ArrayList<String>();
		
		while(iterator.hasNext()) {
			eq = (NCEqNode)(iterator.next());
			eq.addAllTraffics(list);
		}
		
		return list;
	}
	
	public boolean isALinkBetweenEquipment(NCConnectorNode nccn) {
		TGComponent tgc1 = getComponentToWhichBelongs(nccn.getTGConnectingPointP1());
		TGComponent tgc2 = getComponentToWhichBelongs(nccn.getTGConnectingPointP2());
		if ((tgc1 instanceof NCEqNode) && (tgc2 instanceof NCEqNode)) {
			return true;
		}
		
		return false;
	}
	
	public NCEqNode getEquipmentByName(String name) {
		LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCEqNode) {
                if (tgc.getName().equals(name)) {
					return (NCEqNode)tgc;
				}
            }
        }
        
        return null;
	}
	
	public NCSwitchNode getSwitchByName(String name) {
		LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		NCSwitchNode node;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCSwitchNode) {
				node = (NCSwitchNode)tgc;
                if (node.getNodeName().equals(name)) {
					return node;
				}
            }
        }
        
        return null;
	}
	
	public NCConnectorNode getLinkByName(String name) {
		LinkedList ll = new LinkedList();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		NCConnectorNode link;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof NCConnectorNode) {
				link = (NCConnectorNode)tgc;
                if (link.getInterfaceName().equals(name)) {
					return link;
				}
            }
        }
        
        return null;
	}
	
	
	
	
	
	
    

}
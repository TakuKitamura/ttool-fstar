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
* Class GNCModeling
* Use to translate graphical NC modeling to nc structure
* Creation: 20/11/2008
* @version 1.0 20/11/2008
* @author Ludovic APVRILLE
* @see
*/


package ui;

import java.util.*;


import ui.ncdd.*;
import nc.*;
import myutil.*;

public class GNCModeling  {
	private NCStructure ncs;
    private NCPanel ncp;
	private NCDiagramPanel ncdp;
    private Vector checkingErrors, warnings;
	private CorrespondanceTGElement listE;
	private NCStructure nc;
	
	private static int PATH_INDEX = 0;
	
    
    public GNCModeling(NCPanel _ncp) {
        ncp = _ncp;
		ncdp = ncp.ncdp;
    }
	
    
	public NCStructure translateToNCStructure() {
		PATH_INDEX = 0;
		
        ncs = new NCStructure();
        checkingErrors = new Vector();
        warnings = new Vector();
		listE = new CorrespondanceTGElement();
        //boolean b;
        
		if (ncdp != null) {
			addEquipments();
			addSwitches();
			addTraffics();
			addLinks();
			addPaths();
		}
		
		System.out.println("NC XML:\n" + ncs.toXML());
		
        return ncs;
    }
	
	public CorrespondanceTGElement getCorrespondanceTable() {
		return listE;
	}
    
    
    public Vector getCheckingErrors() {
        return checkingErrors;
    }
    
    public Vector getCheckingWarnings() {
        return warnings;
    }
    
	private void addEquipments() {
		ListIterator iterator = ncdp.getListOfEqNode().listIterator();
		NCEqNode node;
		NCEquipment eq;
		
		while(iterator.hasNext()) {
			node = (NCEqNode)(iterator.next());
			
			eq = new NCEquipment();
			eq.setName(node.getName());
			ncs.equipments.add(eq);
		}
	}
	
	private void addSwitches() {
		ListIterator iterator = ncdp.getListOfSwitchNode().listIterator();
		NCSwitchNode node;
		NCSwitch sw;
		NCCapacityUnit unit = new NCCapacityUnit();
		
		while(iterator.hasNext()) {
			node = (NCSwitchNode)(iterator.next());
			
			sw= new NCSwitch();
			sw.setName(node.getNodeName());
			sw.setSchedulingPolicy(node.getSchedulingPolicy());
			sw.setCapacity(node.getCapacity());
			unit.setUnit(node.getCapacityUnit());
			sw.setCapacityUnit(unit);
			ncs.switches.add(sw);
		}
	}
	
	private void addTraffics() {
		ListIterator iterator = ncdp.getTrafficArtifacts().listIterator();
		NCTrafficArtifact arti;
		NCTraffic tr;
		NCTimeUnit unit;
		
		while(iterator.hasNext()) {
			arti = (NCTrafficArtifact)(iterator.next());
			tr = new NCTraffic();
			tr.setName(arti.getValue());
			tr.setPeriodicType(arti.getPeriodicType());
			tr.setDeadline(arti.getDeadline());
			unit = new NCTimeUnit();
			unit.setUnit(arti.getDeadlineUnit());
			tr.setDeadlineUnit(unit);
			tr.setMinPacketSize(arti.getMinPacketSize());
			tr.setMaxPacketSize(arti.getMaxPacketSize());
			tr.setPriority(arti.getPriority());
			ncs.traffics.add(tr);
		}
	}
	
	private void addLinks() {
		ListIterator iterator = ncdp.getListOfLinks().listIterator();
		NCConnectorNode nccn;
		NCLink lk;
		TGComponent tgc;
		TGConnectingPoint tp;
		NCLinkedElement ncle;
		String name;
		CheckingError ce;
		NCCapacityUnit nccu;
		NCSwitchNode switch1, switch2;
		
		while(iterator.hasNext()) {
			nccn = (NCConnectorNode)(iterator.next());
			
			if (ncdp.isALinkBetweenEquipment(nccn)) {
				ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Link connected between two equipments: " + nccn.getInterfaceName());
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(nccn);
				checkingErrors.add(ce);
				
			} else {
				switch1 = null;
				switch2 = null;
				
				lk = new NCLink();
				lk.setName(nccn.getInterfaceName());
				
				tp = nccn.getTGConnectingPointP1();
				tgc = ncdp.getComponentToWhichBelongs(tp);
				
				if (tgc == null) {
					addLinkError(nccn);
				} else {
					if ((tgc instanceof NCEqNode) || (tgc instanceof NCSwitchNode)) {
						if (tgc instanceof NCEqNode) {
							name =  tgc.getName();
						} else {
							switch1 = ((NCSwitchNode)tgc);
							name = switch1.getNodeName();
						}
						ncle = ncs.getNCLinkedElementByName(name);
						if (ncle == null) {
							addLinkError(nccn);
						} else {
							lk.setLinkedElement1(ncle);
						}
					} else {
						addLinkError(nccn);
					}
				}
				
				tp = nccn.getTGConnectingPointP2();
				tgc = ncdp.getComponentToWhichBelongs(tp);
				
				if (tgc == null) {
					addLinkError(nccn);
				} else {
					if ((tgc instanceof NCEqNode) || (tgc instanceof NCSwitchNode)) {
						if (tgc instanceof NCEqNode) {
							name =  tgc.getName();
						} else {
							switch2 = ((NCSwitchNode)tgc);
							name = switch2.getNodeName();
						}
						ncle = ncs.getNCLinkedElementByName(name);
						if (ncle == null) {
							addLinkError(nccn);
						} else {
							lk.setLinkedElement2(ncle);
						}
					} else {
						addLinkError(nccn);
					}
				}
				
				// Must also check that there is at most one link between two elements
				if (ncs.hasSimilarLink(lk)) {
					ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Duplicate link between elements: " + nccn.getInterfaceName());
					ce.setTDiagramPanel(ncdp);
					ce.setTGComponent(nccn);
					checkingErrors.add(ce);
				} else {
					ncs.links.add(lk);
				}
				
				if (!nccn.hasCapacity()) {
					// In that case, must set the capacity of switches
					if ((switch1 == null) && (switch2 == null)) {
						lk.setCapacity(nccn.getCapacity());
						nccu = new NCCapacityUnit();
						nccu.setUnit(nccn.getCapacityUnit());
						lk.setCapacityUnit(nccu);
					} else {
						if (switch1 == null) {
							lk.setCapacity(switch2.getCapacity());
							nccu = new NCCapacityUnit();
							nccu.setUnit(switch2.getCapacityUnit());
							lk.setCapacityUnit(nccu);
						}
						if (switch2 == null) {
							lk.setCapacity(switch1.getCapacity());
							nccu = new NCCapacityUnit();
							nccu.setUnit(switch2.getCapacityUnit());
							lk.setCapacityUnit(nccu);
						}
						if ((switch1 != null) && (switch2 != null)) {
							if (switch1.getCapacity() != switch2.getCapacity()) {
								ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Link with no capacity between two switches of different capacity: " + nccn.getInterfaceName());
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(nccn);
								checkingErrors.add(ce);
							} else {
								lk.setCapacity(switch1.getCapacity());
								nccu = new NCCapacityUnit();
								nccu.setUnit(switch2.getCapacityUnit());
								lk.setCapacityUnit(nccu);
							}
						}
					}
				} else {
					lk.setCapacity(nccn.getCapacity());
					nccu = new NCCapacityUnit();
					nccu.setUnit(nccn.getCapacityUnit());
					lk.setCapacityUnit(nccu);
				}
			}
		}
	}
	
	public void addLinkError(NCConnectorNode _nccn) {
		CheckingError ce;
		ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Badly connected link: " +_nccn.getInterfaceName());
		ce.setTDiagramPanel(ncdp);
		ce.setTGComponent(_nccn);
		checkingErrors.add(ce);
	}
	
	private void addPaths() {
		 System.out.println("Adding paths");
		// Consider each traffic
		// For each traffic, its builds a tree
		// Then, its generates the corresponding paths
		ListIterator iterator = ncdp.getTrafficArtifacts().listIterator();
		NCTrafficArtifact arti;
		NCTraffic tr;
		TreeCell tree;
		CheckingError ce;
		int ret;
		NCPath path;
		NCEqNode node;
		NCEquipment eq;
		ArrayList<String> list;
		
		while(iterator.hasNext()) {
			arti = (NCTrafficArtifact)(iterator.next());
			System.out.println("Considering traffic: " + arti.getValue()); 
			tr = ncs.getTrafficByName(arti.getValue());
			if (tr == null) {
				return;
			}
			
			tree = new TreeCell();
			ret = buildTreeFromTraffic(tree, arti);
			
			if (ret < 0) {
			} else {
				// May build the paths from the tree
				node = ncdp.getNCEqNodeOf(arti);
				if (node == null) {
					return;
				}
				
				eq = (NCEquipment)(ncs.getNCLinkedElementByName(node.getName()));
				if (eq == null) {
					return;
				}
				
				list = new ArrayList<String>();
				
				exploreTree(tree, list, eq, tr);
			}
			
		}
	}
	
	public void exploreTree(TreeCell tree, ArrayList<String> list, NCEquipment origin, NCTraffic traffic) {
		NCSwitchNode sw;
		NCLinkedElement ncle;
		CheckingError ce;
		
		if (tree.isLeaf()) {
			//System.out.println("Found path");
			NCPath path = new NCPath();
			path.traffic = traffic;
			path.origin = origin;
			ncle = ncs.getNCLinkedElementByName((((NCEqNode)(tree.getElement())).getName()));
			if (ncle == null) {
				ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown equipment named " + (((NCEqNode)(tree.getElement())).getName()) + " needed to generate path");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(null);
				checkingErrors.add(ce);
				return;
			}
			path.destination = (NCEquipment)ncle;
			
			for(String s: list) {
				ncle = ncs.getNCLinkedElementByName(s);
				if (ncle instanceof NCSwitch) {
					path.switches.add((NCSwitch)ncle);
				} else {
					ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Unknown switch named " + s + " needed to generate path");
					ce.setTDiagramPanel(ncdp);
					ce.setTGComponent(null);
					checkingErrors.add(ce);
					return;
				}
			}
			//System.out.println("Adding path");
			path.setName("path" + PATH_INDEX);
			PATH_INDEX++;
			ncs.paths.add(path);
		
		// not a leaf
		} else {
			sw = (NCSwitchNode)(tree.getElement());
			TreeCell next = new TreeCell();
			//System.out.println("Adding to path: " + sw.getName());
			list.add(sw.getName());
			for(int i=0; i<tree.getNbOfChildren(); i++) {
				next = tree.getChildrenByIndex(i);
				exploreTree(next, list, origin, traffic);
			}
			//System.out.println("Removing from path: " + sw.getName());
			list.remove(list.size()-1);
		}
	}
	
	public int buildTreeFromTraffic(TreeCell tree, NCTrafficArtifact arti) {
		NCEqNode node = ncdp.getNCEqNodeOf(arti);
		CheckingError ce;
		int ret;
		NCConnectorNode lk;
		
		if (node == null) {
			ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Badly mapped traffic: " + arti.getValue());
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(arti);
			checkingErrors.add(ce);
			return -1;
		}
		
		// Find the only interface of that Equipment
		// If more than one interface -> error
		ArrayList<NCSwitchNode> listsw = ncdp.getSwitchesOfEq(node);
		
		if (listsw.size() == 0) {
			ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Equipment " + node.getName() + " has non interface: traffic " + arti.getValue() + " cannot be sent over the network");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(node);
			warnings.add(ce);
			return -3;
		}
		
		if (listsw.size() > 1) {
			ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Equipment: " + node.getName() + " has more than one interface");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(node);
			checkingErrors.add(ce);
			return -4;
		}
		
		
		
		// List size = 1. Great.
		tree.setElement(listsw.get(0));
		lk = ncdp.getConnectorOfEq(node).get(0);
		
		ret = buildTreeFromSwitch(tree, tree, listsw.get(0), lk, arti);
		
		// print tree
		System.out.println(tree.toString());
		
		if (ret < 0) {
			return ret;
		}
		
		// Must check out that all leafs are Equipments
		ArrayList<TreeCell> list = tree.getAllLeafs();
		
		if (list.size() == 0) {
			return -1;
		}
		
		for(TreeCell cell: list) {
			if (!(cell.getElement() instanceof NCEqNode)) {
				ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Bad route for traffic " + arti.getValue() + " at switch " + cell.getElement().toString() + " (ignoring traffic)");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(node);
				checkingErrors.add(ce);
				return -1;
			}
	   }
	   
	   // Good tree!
		return 1;
	}
	
	public int buildTreeFromSwitch(TreeCell root, TreeCell tree, NCSwitchNode sw, NCConnectorNode arrivingLink, NCTrafficArtifact arti) {
		CheckingError ce;
		String lkname = arrivingLink.getInterfaceName();
		boolean error;
		NCLink link;
		NCLinkedElement ncle;
		NCEqNode ncen;
		NCSwitchNode ncsn;
		TreeCell cell;
		NCConnectorNode nextArriving;
		
		ArrayList<NCRoute> computed = new ArrayList<NCRoute>(); 
		
		// Get all routes concerning that traffic on that switch
		ArrayList<NCRoute> routes = ncdp.getAllRoutesFor(sw, arti);
		
		//System.out.println("toto0");
		
		// Get all next swithes, according to routes, and fill the tree
		// Verify that there is at least one possibile route
		boolean found = false;
		for(NCRoute route: routes) {
			if (route.inputInterface.equals(lkname)) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "No possible route for traffic " + arti.getValue() + " on switch " + sw.getNodeName() + " (ignoring trafic)");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(sw);
			checkingErrors.add(ce);
			return -1;
		}
		
		// Verify that no route on the same switch outputs on the same input interface
		found = false;
		for(NCRoute route: routes) {
			if (route.outputInterface.equals(lkname)) {
				found = true;
				break;
			}
		}
		
		if (found) {
			ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Cycle detected for traffic " + arti.getValue() + " on switch " + sw.getNodeName() + " (ignoring trafic)");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(sw);
			checkingErrors.add(ce);
			return -1;
		}
		
		for(NCRoute route: routes) {
			System.out.println("Considering route:" + route.toString()+  " vs input route=" + lkname);
			if (route.inputInterface.equals(lkname)) {
				// Must check that two routes don't have the same output interface
				//System.out.println("toto1");
				error = false;
				for(NCRoute route1: computed) {
					if ((route1 != route) && (route1.outputInterface.equals(route.outputInterface))) {
						ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Two similar routes " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring similar routes)");
						ce.setTDiagramPanel(ncdp);
						ce.setTGComponent(sw);
						warnings.add(ce);
						error = true;
					} 
				}
				computed.add(route);
				
				//System.out.println("toto2");
				// Is it an existing output interface?
				if (error == false) {
					link = ncs.hasLinkWith(sw.getNodeName(), route.outputInterface);
					if (link == null) {
						ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "No such output interface " + route.outputInterface + " in route " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring route)");
						ce.setTDiagramPanel(ncdp);
						ce.setTGComponent(sw);
						warnings.add(ce);	
					} else {
						//System.out.println("toto3");
						// Is the destination equipment a switch that is in the tree already?
						// If so, there is a cycle -> ignoring route
						if (link.getLinkedElement1().getName().equals(sw.getNodeName())) {
							ncle = link.getLinkedElement2();
						} else {
							ncle = link.getLinkedElement1();
						}
						
						//System.out.println("toto4");
						// Is the next of the route an equipment?
						if (ncle instanceof NCEquipment) {
							ncen = ncdp.getEquipmentByName(ncle.getName());
							if (ncen == null) {
								ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Internal error: could not find the equipment named " + ncle.getName() + " on diagram (ignoring route)");
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(sw);
								checkingErrors.add(ce);	
							} else {
								// Adding an equipment -> leaf of the tree
								//System.out.println("Adding a leaf: " + ncle.getName());
								cell = new TreeCell();
								cell.setElement(ncen);
								tree.addChildren(cell);
							}
						} else {
							// The next equipment is a switch
							ncsn = ncdp.getSwitchByName(ncle.getName());
							
							// Is this switch already in the tree?
							/*if (root.containsElement(ncsn)) {
								ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Cycle detected because of route " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring route)");
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(sw);
								warnings.add(ce);
							} else {*/
								// Good input and output -> must deal with the route to a switch
								cell = new TreeCell();
								cell.setElement(ncsn);
								tree.addChildren(cell);
								nextArriving = ncdp.getLinkByName(link.getName());
								if (nextArriving == null) {
									ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Internal error: could not find the equipment named " + ncle.getName() + " on diagram (ignoring route)");
									ce.setTDiagramPanel(ncdp);
									ce.setTGComponent(sw);
									checkingErrors.add(ce);	
								} else {
									// Recursive call
									//System.out.println("Adding a switch: " + ncsn.getNodeName());
									buildTreeFromSwitch(root, cell, ncsn, nextArriving, arti);
									//System.out.println("Ending adding a switch: " + ncsn.getNodeName());
								}
							//}
						}
					}
				}
				
			}
		}
		
		
		return 0;
	}
    
}

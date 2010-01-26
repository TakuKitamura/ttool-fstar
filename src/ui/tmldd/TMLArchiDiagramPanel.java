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
 * Class TMLArchiDiagramPanel
 * Panel for drawing an architecture diagram
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmldd;


import org.w3c.dom.*;
import java.util.*;

import ui.*;

public class TMLArchiDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
    private int masterClockFrequency = 200; // in MHz
	
    public  TMLArchiDiagramPanel(MainGUI mgui, TToolBar _ttb) {
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
	
	public int getMasterClockFrequency() {
		return masterClockFrequency;
	}
	
	public void setMasterClockFrequency(int _masterClockFrequency) {
		masterClockFrequency = _masterClockFrequency;
	}
    
    public String getXMLHead() {
        return "<TMLArchiDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + displayClock() + " >";
    }
    
    public String getXMLTail() {
        return "</TMLArchiDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<TMLArchiDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</TMLArchiDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<TMLArchiDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</TMLArchiDiagramPanelCopy>";
    }
	
	 public String displayParam() {
        String s = " attributes=\"";
		s += getAttributeState();
		s += "\"";
        return s;
    }
	
	 public String displayClock() {
        String s = " masterClockFrequency=\"";
		s += masterClockFrequency;
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
		
		try {
            s = elt.getAttribute("masterClockFrequency");
            //System.out.println("S=" + s);
			masterClockFrequency = Math.abs(Integer.decode(s).intValue());
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //System.out.println("older format");
			masterClockFrequency = 200;
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
            if (tgc instanceof TMLArchiCPUNode) {
                ll.add(tgc);
            }
			
			if (tgc instanceof TMLArchiHWANode) {
				 ll.add(tgc);
			}
			
			if (tgc instanceof TMLArchiCommunicationNode) {
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
            if (tgc instanceof TMLArchiConnectorNode) {
                ll.add(tgc);
            }
        }
        
        return ll;
    }
	
	public boolean isMapped(String _ref, String _name) {
		ListIterator iterator = getListOfNodes().listIterator();
		TMLArchiNode node;
		Vector v;
		TMLArchiArtifact artifact;
		int i;
		String name = _ref + "::" + _name;
		
		while(iterator.hasNext()) {
			node = (TMLArchiNode)(iterator.next());
			if (node instanceof TMLArchiCPUNode) {
				v =  ((TMLArchiCPUNode)(node)).getArtifactList();
				for(i=0; i<v.size(); i++) {
					artifact = (TMLArchiArtifact)(v.get(i));
					if (artifact.getValue().equals(name)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void renameMapping(String oldName, String newName) {
		ListIterator iterator = getListOfNodes().listIterator();
		TMLArchiNode node;
		Vector v;
		TMLArchiArtifact artifact;
		ArrayList<TMLArchiCommunicationArtifact> list;
		int i;
		
		while(iterator.hasNext()) {
			node = (TMLArchiNode)(iterator.next());
			
			// Task mapping
			
			if ((node instanceof TMLArchiCPUNode) || (node instanceof TMLArchiHWANode)) {
				if (node instanceof TMLArchiCPUNode) {
					v =  ((TMLArchiCPUNode)(node)).getArtifactList();
					//System.out.println("CPU:" + node.getName() +  " v:" + v.size());
				} else {
					v =  ((TMLArchiHWANode)(node)).getArtifactList();
					//System.out.println("HWA:" + node.getName() + " v:" + v.size());
				}
				
				for(i=0; i<v.size(); i++) {
					artifact = (TMLArchiArtifact)(v.get(i));
					if (artifact.getReferenceTaskName().compareTo(oldName) == 0) {
						artifact.setReferenceTaskName(newName);
					}
				}
			}
			
			// Channel, event, request mapping
			if (node instanceof TMLArchiCommunicationNode) {
				list = ((TMLArchiCommunicationNode)node).getArtifactList();
				
				for(TMLArchiCommunicationArtifact arti: list) {
					if (arti.getReferenceCommunicationName().compareTo(oldName) == 0) {
						arti.setReferenceCommunicationName(newName);
					}
				}
			}
		}
	}
	
	public void setPriority(String _name, int _priority) {
		ListIterator iterator = getListOfNodes().listIterator();
		TMLArchiNode node;
		Vector v;
		TMLArchiArtifact artifact;
		ArrayList<TMLArchiCommunicationArtifact> list;
		int i;
		
		while(iterator.hasNext()) {
			node = (TMLArchiNode)(iterator.next());
			
			
			// Channel, event, request mapping
			if (node instanceof TMLArchiCommunicationNode) {
				list = ((TMLArchiCommunicationNode)node).getArtifactList();
				
				for(TMLArchiCommunicationArtifact arti: list) {
					if (arti.getFullValue().compareTo(_name) == 0) {
						arti.setPriority(_priority);
					}
				}
			}
		}
		
	}
	
	public int getMaxPriority(String _name, TMLArchiCommunicationArtifact _arti) {
	ListIterator iterator = getListOfNodes().listIterator();
		TMLArchiNode node;
		Vector v;
		TMLArchiArtifact artifact;
		ArrayList<TMLArchiCommunicationArtifact> list;
		int i;
		int prio = _arti.getPriority();
		
		while(iterator.hasNext()) {
			node = (TMLArchiNode)(iterator.next());
			
			
			// Channel, event, request mapping
			if (node instanceof TMLArchiCommunicationNode) {
				list = ((TMLArchiCommunicationNode)node).getArtifactList();
				
				for(TMLArchiCommunicationArtifact arti: list) {
					if (arti.getFullValue().compareTo(_name) == 0) {
						if (arti != _arti) {
							priority = Math.max(priority, arti.getPriority);
						}
					}
				}
			}
		}	
		
		return priority;
	}
    

}
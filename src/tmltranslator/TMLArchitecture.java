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
* Class TMLArchitecture
* Creation: 05/09/2007
* @version 1.1 19/05/2008
* @author Ludovic APVRILLE
* @see
*/

package tmltranslator;

import java.util.*;


public class TMLArchitecture {
    private ArrayList<HwNode> hwnodes;
	private ArrayList<HwLink> hwlinks; // Between buses and other component
	
	private int masterClockFrequency = 200; // in MHz
	
	private int hashCode;
	private boolean hashCodeComputed = false;
	
    
    public TMLArchitecture() {
        init();
    }
	
    private void init() {
        hwnodes = new ArrayList<HwNode>();
		hwlinks = new ArrayList<HwLink>();
    }
	
	private void computeHashCode() {
		TMLArchiTextSpecification architxt = new TMLArchiTextSpecification("spec.tarchi");
		String s = architxt.toTextFormat(this);
		hashCode = s.hashCode();
		System.out.println("TARCHI hashcode = " + hashCode); 
	}
	
	public int getHashCode() {
		if (!hashCodeComputed) {
			computeHashCode();
			hashCodeComputed = true;
		}
		return hashCode;
	}
	
	public void setMasterClockFrequency(int value) {
		masterClockFrequency = value;
	}
	
	public int getMasterClockFrequency() {
		return masterClockFrequency;
	}
	
    
    public void addHwNode(HwNode _node) {
        hwnodes.add(_node);
    }
	
	public ArrayList<HwNode> getHwNodes() {
		return hwnodes;
	}
	
	public void addHwLink(HwLink _link) {
        hwlinks.add(_link);
    }
	
	public ArrayList<HwLink> getHwLinks() {
		return hwlinks;
	}
	
	public HwNode getHwNodeByName(String _name) {
		for(HwNode node: hwnodes) {
			if (node.getName().equals(_name)) {
				return node;
			}
		}
		return null;
	}
	
	public HwBus getHwBusByName(String _name) {
		for(HwNode node: hwnodes) {
			if (node.getName().equals(_name)) {
				if (node instanceof HwBus) {
					return (HwBus)node;
				}
			}
		}
		return null;
	}
	
	public HwLink getHwLinkByName(String _name) {
		for(HwLink link: hwlinks) {
			if (link.getName().equals(_name)) {
				return link;
			}
		}
		return null;
	}
	
	public ArrayList<HwLink> getLinkByHwNode(HwNode node){
		ArrayList<HwLink> tempList=new ArrayList<HwLink>();
		for(HwLink link: hwlinks) {
			if (link.hwnode==node) tempList.add(link);
		}
		return tempList;
	}
	
	public HwLink getHwLinkByHwNode(HwNode node){
		for(HwLink link: hwlinks) {
			if (link.hwnode==node) {
				return link;
			}
		}
		return null;
	}
	
	public boolean isNodeConnectedToBus(HwNode node, HwBus bus){
		for(HwLink link: hwlinks) {
			if (node==link.hwnode && bus==link.bus) return true;
		}
		return false;
	}
	
}
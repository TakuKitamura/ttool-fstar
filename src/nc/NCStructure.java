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
* Class NCStructure
* Creation: 14/11/2008
* @version 1.0 14/11/2008
* @author Ludovic APVRILLE
* @see
*/

package nc;

import java.util.*;

public class NCStructure extends NCElement {
	public ArrayList<NCEquipment> equipments;
	public ArrayList<NCSwitch> switches;
	public ArrayList<NCTraffic> traffics;
	public ArrayList<NCLink> links;
	public ArrayList<NCPath> paths;
	
	public NCStructure() {
		equipments = new ArrayList<NCEquipment>();
		switches = new ArrayList<NCSwitch>();
		traffics = new ArrayList<NCTraffic>();
		links = new ArrayList<NCLink>();
		paths = new ArrayList<NCPath>();
	}
	
	public NCLinkedElement getNCLinkedElementByName(String _name) {
		for(NCEquipment eq: equipments) {
			if (eq.getName().equals(_name)) {
				return eq;
			}
		}
		
		for(NCSwitch sw: switches) {
			if (sw.getName().equals(_name)) {
				return sw;
			}
		}
		
		return null;
	}
	
	public NCTraffic getTrafficByName(String name) {
		for(NCTraffic tr: traffics) {
			if (tr.getName().equals(name)) {
				return tr;
			}
		}
		return null;
	}
	
	public String toXML() {
		StringBuffer sb = new StringBuffer("");
		sb.append(getXMLHeader());
		sb.append("<NCStructure>\n");
		sb.append(getXMLEquipments());
		sb.append(getXMLSwitches());
		sb.append(getXMLTraffics());
		sb.append(getXMLLinks());	
		sb.append(getXMLPaths());	
		sb.append("</NCStructure>\n");
		return sb.toString();
	}
	
	private String getXMLHeader() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
	}
	
	private String getXMLEquipments() {
		String tmp = "";
		for(NCEquipment eq: equipments) {
			tmp += "<Equipment ";
			tmp += " name=\"";
			tmp += eq.getName();
			tmp += "\" />\n";
		}
		return tmp;
	}
	
	
	private String getXMLSwitches() {
		String tmp = "";
		for(NCSwitch sw: switches) {
			tmp += "<Switch ";
			tmp += " name=\"";
			tmp += sw.getName();
			tmp += "\" schedulingPolicy=\"";
			tmp += NCSwitch.getStringSchedulingPolicy(sw.getSchedulingPolicy());
			tmp += "\" capacity=\"" + sw.getCapacity();
			tmp += "\" capacityUnit=\"" + sw.getCapacityUnit().getStringUnit();
			tmp += "\" />\n";
		}
		return tmp;
	}
	
	private String getXMLTraffics() {
		String tmp = "";
		for(NCTraffic tr: traffics) {
			tmp += "<Traffic ";
			tmp += " name=\"";
			tmp += tr.getName();
			tmp += "\" Periodic=\"";
			if (tr.getPeriodicType() == 0) {
				tmp += "periodic";
			} else {
				tmp += "aperiodic";
			}
			tmp += "\" deadline=\"" + tr.getDeadline();
			tmp += "\" deadlineUnit=\"" + tr.getDeadlineUnit().getStringUnit();
			tmp += "\" minPacketSize=\"" + tr.getMinPacketSize();
			tmp += "\" maxPacketSize=\"" + tr.getMaxPacketSize();
			tmp += "\" priority=\"" + tr.getPriority();
			tmp += "\" />\n";
		}
		return tmp;
	}
	
	private String getXMLLinks() {
		String tmp = "";
		for(NCLink lk: links) {
			tmp += "<Link ";
			tmp += " name=\"";
			tmp += lk.getName();
			tmp += "\" capacity=\"";
			tmp += lk.getCapacity();
			tmp += "\" capacityUnit=\"";
			tmp += lk.getCapacityUnit().getStringUnit();
			tmp += "\" end1=\"";
			tmp += lk.getLinkedElement1().getName();
			tmp += "\" end2=\"";
			tmp += lk.getLinkedElement2().getName();
			tmp += "\" />\n";
		}
		return tmp;
	}
	
	private String getXMLPaths() {
		String tmp = "";
		for(NCPath pa: paths) {
			tmp += "<Path ";
			tmp += " name=\"";
			tmp += pa.getName();
			tmp += "\" traffic=\"";
			tmp += pa.traffic.getName();
			tmp += "\" origin=\"";
			tmp += pa.origin.getName();
			tmp += "\" destination=\"";
			tmp += pa.destination.getName();
			tmp += "\" />\n";
			for(NCSwitch sw: pa.switches) {
				tmp += "<PathIntermediateSwitch ";
				tmp += " name=\"";
				tmp += pa.getName();
				tmp += "\" switch=\"";
				tmp += sw.getName();
				tmp += "\" />\n";
			}
		}
		return tmp;
	}
	
	public boolean hasSimilarLink(NCLink _lk) {
		for(NCLink link: links) {
			if ((link.getLinkedElement1() == _lk.getLinkedElement1()) && (link.getLinkedElement2() == _lk.getLinkedElement2())) {
				return true;
			}
			if ((link.getLinkedElement2() == _lk.getLinkedElement1()) && (link.getLinkedElement1() == _lk.getLinkedElement2())) {
				return true;
			}
		}
		
		return false;
	}
	
	public NCLink hasLinkWith(String eqname, String linkname) {
		for(NCLink link: links) {
			if (link.getName().equals(linkname)) {
				if (link.getLinkedElement1().getName().equals(eqname)) {
					return link;
				}
				if (link.getLinkedElement2().getName().equals(eqname)) {
					return link;
				}
			}
		}
		return null;
	}
	
}
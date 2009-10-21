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
	
	public NCEquipment getNCEquipmentByName(String _name) {
		for(NCEquipment eq: equipments) {
			if (eq.getName().equals(_name)) {
				return eq;
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
	
	public String toISAENetworkXML() {
		StringBuffer sb = new StringBuffer("");
		sb.append(getXMLHeader());
		sb.append("<File Title=\"Network Definition\">\n");
		sb.append(getISAEXMLSwitches());
		sb.append(getISAEXMLEquipments());
		sb.append(getISAEXMLLinks());
		sb.append(getISAEXMLPorts());
		sb.append("</File>\n");
		return sb.toString();
	}
	
	public String toISAETrafficsXML() {
		StringBuffer sb = new StringBuffer("");
		sb.append(getXMLHeader());
		sb.append("<File Title=\"Traffic Definition\">\n");
		sb.append(getISAEXMLTraffics());
		sb.append("</File>\n");
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
	
	private String getISAEXMLSwitches() {
		String tmp = "";
		for(NCSwitch sw: switches) {
			tmp += "<Switch>\n";
			tmp += "<Name> " + sw.getName() + " </Name>\n";
			tmp += "<SWTechnique> " + NCSwitch.getStringSwitchingTechnique(sw.getSwitchingTechnique()) + "</SWTechnique>\n";
			tmp += "<Policy> " + NCSwitch.getStringSchedulingPolicy(sw.getSchedulingPolicy()) + "</Policy>\n";
			tmp += "<Capacity> " + sw.getCapacity() + " </Capacity>\n";
			tmp += "<TechLatency> " + sw.getTechnicalLatency() + " </TechLatency>\n";
			tmp += "</Switch>\n";
		}
		return tmp;
	}
	
	private String getISAEXMLEquipments() {
		String tmp = "";
		for(NCEquipment eq: equipments) {
			tmp += "<EndSystem>\n";
			tmp += "<Name> " + eq.getName() + " </Name>\n";
			tmp += "<Type> " + NCEquipment.getStringType(eq.getType()) + "</Type>\n";
			tmp += "<Policy> " + NCEquipment.getStringSchedulingPolicy(eq.getSchedulingPolicy()) + "</Policy>\n";
			tmp += "</EndSystem>\n";
		}
		return tmp;
	}
	
	private String getISAEXMLLinks() {
		String tmp = "";
		for(NCLink lk: links) {
			tmp += "<Link>\n";
			tmp += "<Name> " + lk.getName() + " </Name>\n";
			tmp += "<Capacity> " + lk.getCapacityInMbs() + "</Capacity>\n";
			tmp += "<StartPoint> " + lk.getName() + "_sp" + "</StartPoint>\n";
			tmp += "<ArrivalPoint> " + lk.getName() + "_sp"  + "</ArrivalPoint>\n";
			tmp += "</Link>\n";
		}
		return tmp;
	}
	
	private String getISAEXMLPorts() {
		String tmp = "";
		for(NCLink lk: links) {
			tmp += "<Port>\n";
			tmp += "<Name> " + lk.getName() + "_sp" + " </Name>\n";
			tmp += "<Owner> " + lk.getLinkedElement1().getName() + "</Owner>\n";
			tmp += "</Port>\n";
			tmp += "<Port>\n";
			tmp += "<Name> " + lk.getName() + "_ap" + " </Name>\n";
			tmp += "<Owner> " + lk.getLinkedElement2().getName() + "</Owner>\n";
			tmp += "</Port>\n";
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
	
	private String getISAEXMLTraffics() {
		String tmp = "";
		String info0, info1;
		for(NCTraffic tr: traffics) {
			tmp += "<Message>\n";
			tmp += "<Name> " + tr.getName() + " </Name>\n";
			tmp += "<Type> " + NCTraffic.getISAEStringPeriodicType(tr.getPeriodicType()) + "</Type>\n";
			tmp += "<Length> " + tr.getMaxLengthInBytes() + " </Length>\n";
			if (tr.getPeriodicType() == 0) {
				info0 = "" + tr.getPeriodMs();
				info1 = "";
			} else {
				info0 = "";
				info1 = "" + tr.getPeriodMs();
			}
			tmp += "<Period> " + info0 + " </Period>\n";
			tmp += "<InterArrivals> " + info1 + " </InterArrivals>\n";
			tmp += "<Deadline> " +  tr.getDeadlineMs() + " </Deadline>\n";
			tmp += "<Priority> " +  tr.getPriority() + " </Priority>\n";
			tmp += "<Source> " +  getTrafficSource(tr).getName() + " </Source>\n";
			tmp += "<Destination> " +  getTrafficDestinations(tr) + " </Destination>\n";
			for(NCPath path: paths) {
				if (path.traffic == tr) {
					tmp += "<Path> " + path.getLinksString() + " </Path>\n";
				}
			}
			tmp += "</Message>\n";
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
	
	public NCLink getLinkWith(NCLinkedElement le) {
		for(NCLink link: links) {
			if ((link.le1 == le) || (link.le2 == le)) {
				return link;
			}
		}
		
		return null;
	}
	
	public NCLink getLinkWith(NCLinkedElement _le1, NCLinkedElement _le2) {
		for(NCLink link: links) {
			if ((link.le1 == _le1) && (link.le2 == _le2)) {
				return link;
			}
		}
		
		return null;
	}
	
	public NCEquipment getTrafficSource(NCTraffic _traffic) {
		for(NCPath path: paths) {
			if (path.traffic == _traffic) {
				return path.origin;
			}
		}
		return null;
	}
	
	public String getTrafficDestinations(NCTraffic _traffic) {
		String tmp = "";
		for(NCPath path: paths) {
			if (path.traffic == _traffic) {
				tmp += path.destination.getName() + " ";
			}
		}
		return tmp;
	}
	
	
	
}
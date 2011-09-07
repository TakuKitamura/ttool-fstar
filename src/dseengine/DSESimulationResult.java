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
* Class DSESimulationResult
* Object for storing a simulation result
* Creation: 06/09/2011
* @version 1.0 06/09/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;


import myutil.*;


import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

//import uppaaldesc.*;

public class DSESimulationResult  {
	
	protected static final String SIMULATION_GLOBAL = "global";
	protected static final String SIMULATION_HEADER = "siminfo";
	protected static final String SIMULATION_CPU = "cpu";
	protected static final String SIMULATION_BUS = "bus";
	
	
	private Vector<CPUResult> cpus;
	private Vector<BusResult> busses;
	
	public DSESimulationResult() {
		reset();
	}
	
	public void reset() {
		cpus = new Vector<CPUResult>();
		busses = new Vector<BusResult>();
	}
	
	public int loadResultFromXMLFile(String pathToFile) {
		File f = new File(pathToFile);
		String data = FileUtils.loadFileData(f);
		
		if (data == null) {
			return -1;
		}
		
		analyzeServerAnswer(data);
		
		
		return 0;
	}
	
	protected void analyzeServerAnswer(String s) {
		//System.out.println("From server:" + s);
		int index0 = s.indexOf("<?xml");
		String ssxml = "";
		
		if (index0 != -1) {
			//System.out.println("toto1");
			ssxml = s.substring(index0, s.length()) + "\n";
		} else {
			//System.out.println("toto2");
			ssxml = ssxml + s + "\n";
		}
		
		index0 = ssxml.indexOf("</siminfo>");
		
		if (index0 != -1) {
			//System.out.println("toto3");
			ssxml = ssxml.substring(0, index0+10);
			loadXMLInfoFromServer(ssxml);
			ssxml = "";
		}
		//System.out.println("toto4");
		
	}
	
	protected boolean loadXMLInfoFromServer(String xmldata) {
		//jta.append("XML from server:" + xmldata + "\n\n");
		
		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			dbf = null;
			db = null;
		}
		
		if ((dbf == null) || (db == null)) {
			return false;
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(xmldata).getBytes());
		int i;
		
		try {
			// building nodes from xml String
			Document doc = db.parse(bais);
			NodeList nl;
			Node node;
			
			nl = doc.getElementsByTagName(SIMULATION_HEADER);
			
			if (nl == null) {
				return false;
			}
			
			for(i=0; i<nl.getLength(); i++) {
				node = nl.item(i);
				//System.out.println("Node = " + dnd);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					// create design, and get an index for it
					return loadConfiguration(node);
				}
			}
			
		} catch (IOException e) {
			TraceManager.addError("Error when parsing server info:" + e.getMessage());
			return false;
		} catch (SAXException saxe) {
			TraceManager.addError("Error when parsing server info:" + saxe.getMessage());
			TraceManager.addError("xml:" + xmldata);
			return false;
		}
		return true;
		
	}
	
	protected boolean loadConfiguration(Node node1) {
		NodeList diagramNl = node1.getChildNodes();
		if (diagramNl == null) {
			return false;
		}
		Element elt, elt0;
		Node node, node0, node00;
		NodeList nl, nl0;
		
		
		/*String tmp;
		int val;
		
		int[] colors;
		String msg = null;
		String error = null;
		String hash = null;
		
		String id, idvar;
		String name;
		String command;
		String startTime="", finishTime="";
		String progression="", nextCommand="";
		String transStartTime="", transFinishTime="";
		String util = null;
		String value;
		String extime;
		String contdel;
		String busname;
		String busid;
		String state;*/
		
		
		try {
			for(int j=0; j<diagramNl.getLength(); j++) {
				//System.out.println("Ndes: " + j);
				node = diagramNl.item(j);
				
				if (node == null) {
					TraceManager.addDev("null node");
					return false;
				}
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					
					TraceManager.addDev("Found tag:" + elt.getTagName());
					
					if (elt.getTagName().compareTo(SIMULATION_GLOBAL) ==0) {
						loadGlobalConfiguration(node);
					}
					
					
					/*nl = node.getChildNodes();
					if ((nl != null) && (nl.getLength() > 0)) {
						for(int i=0; i<nl.getLength(); i++) {                                               
							node0 = nl.item(i);
							TraceManager.addDev("i: " + i + " Node = " + node0);
							if (node0.getNodeType() == Node.ELEMENT_NODE) {
								// create design, and get an index for it
								return loadGlobalConfiguration(node0);
							}
						}
					}*/
					
				}
			}
		} catch (Exception e) {
			TraceManager.addError("Exception in xml parsing " + e.getMessage() + " node= " + node1);
			return false;
		}
		
		return true;
	}
			
	
	protected boolean loadGlobalConfiguration(Node node1) {
		
		TraceManager.addDev("Global configuration");
		
		NodeList diagramNl = node1.getChildNodes();
		if (diagramNl == null) {
			return false;
		}
		Element elt, elt0;
		Node node, node0, node00;
		NodeList nl, nl0;
		
		
		String tmp;
		int val;
		
		int[] colors;
		String msg = null;
		String error = null;
		String hash = null;
		
		String id, idvar;
		String name;
		String command;
		String startTime="", finishTime="";
		String progression="", nextCommand="";
		String transStartTime="", transFinishTime="";
		String util = null;
		String value;
		String extime;
		String contdel;
		String busname;
		String busid;
		String state;
		
		int k, l;
		
		try {
			for(int j=0; j<diagramNl.getLength(); j++) {
				//System.out.println("Ndes: " + j);
				node = diagramNl.item(j);
				
				if (node == null) {
					TraceManager.addDev("null node");
					return false;
				}
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elt = (Element)node;
					
					TraceManager.addDev("Found tag tag:" + elt.getTagName());
					
					// Status
					if (elt.getTagName().compareTo(SIMULATION_CPU) == 0) {
						id = null;
						name = null;
						command = null;
						contdel = null;
						busname = null;
						busid = null;
						util = null;
						
						id = elt.getAttribute("id");
						name = elt.getAttribute("name");
						
						if ((id != null) && (name != null)) {
							nl = elt.getElementsByTagName("util");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("toto12");
							nl = elt.getElementsByTagName("contdel");
							if ((nl != null) && (nl.getLength() > 0)) {
								nl = elt.getElementsByTagName("contdel");
								node0 = nl.item(0);
								elt0 = (Element)node0;
								busid = elt0.getAttribute("busID");
								busname = elt0.getAttribute("busName");
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								contdel = node0.getTextContent();
							}
							
							if ((util != null) || ((contdel != null) && (busid != null) && (busname != null))) {
								CPUResult cpur = new CPUResult();
								try {
									cpur.id = Integer.decode(id).intValue();
									cpur.name = name;
									
									if (util != null) {
										cpur.utilization = Double.valueOf(util).doubleValue();
									}
									
									if ((contdel != null) && (busid != null) && (busname != null)) {
										BusContentionResult bcr = new BusContentionResult();
										bcr.id = Integer.decode(busid).intValue();
										bcr.name = busname;
										bcr.contention = Long.decode(contdel).longValue();
										cpur.addContentionOnBus(bcr);
									}
									
									cpus.add(cpur);
								} catch (Exception e) {
								}
							}
						}
						
						
					}
				}
			}
		} catch (Exception e) {
			TraceManager.addError("Exception in xml parsing " + e.getMessage() + " node= " + node1);
			return false;
		}
		
		return true;
	}
						
					
						
						/*if (elt.getTagName().compareTo(SIMULATION_CPU) == 0) {
							id = null;
							name = null;
							command = null;
							contdel = null;
							busname = null;
							busid = null;
							
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("util");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("toto12");
							nl = elt.getElementsByTagName("contdel");
							if ((nl != null) && (nl.getLength() > 0)) {
								nl = elt.getElementsByTagName("contdel");
								node0 = nl.item(0);
								elt0 = (Element)node0;
								busid = elt0.getAttribute("busID");
								busname = elt0.getAttribute("busName");
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								contdel = node0.getTextContent();
							}
							
							//System.out.println("contdel: " + contdel + " busID:" + busid + " busName:" + busname);
							
							
							if ((id != null) && (util != null)) {
								updateCPUState(id, util, contdel, busname, busid);
							}
						}
						
						//System.out.println("toto2");
						
						if (elt.getTagName().compareTo(SIMULATION_BUS) == 0) {
							id = null;
							name = null;
							command = null;
							id = elt.getAttribute("id");
							name = elt.getAttribute("name");
							nl = elt.getElementsByTagName("util");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							//System.out.println("Got info on bus " + id + " util=" + util);
							
							if ((id != null) && (util != null)) {
								updateBusState(id, util);
							}
						}
						
						
					}
				}
			}
		} catch (Exception e) {
			TraceManager.addError("Exception in xml parsing " + e.getMessage() + " node= " + node1);
			return false;
		}
		
		return true;
	}*/
	
	public static String decodeString(String s)  {
		if (s == null)
			return s;
		byte b[] = null;
		try {
			b = s.getBytes("ISO-8859-1");
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	
} // Class DSEConfiguration


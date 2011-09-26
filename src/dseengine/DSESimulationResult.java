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
	protected static final String SIMULATION_DURATION = "simdur";
	protected static final String SIMULATION_CPU = "cpu";
	protected static final String SIMULATION_BUS = "bus";
	protected static final String SIMULATION_TASK = "task";
	
	private Vector<String> comments;
	
	private Vector<Long> simulationDurations;
	
	private Vector<CPUResult> cpus;
	private Vector<BusResult> busses;
	private Vector<TaskResult> tasks;
	
	private SimulationDurationWholeResult sdwr;
	private Vector<CPUWholeResult> wcpus;
	private Vector<BusWholeResult> wbusses;
	private Vector<TaskWholeResult> wtasks;
	

	
	public DSESimulationResult() {
		reset();
	}
	
	public void reset() {
		cpus = new Vector<CPUResult>();
		busses = new Vector<BusResult>();
		tasks = new Vector<TaskResult>();
		comments = new Vector<String>();
		simulationDurations = new Vector<Long>();
	}
	
	public void addComment(String _comment) {
		comments.add(_comment);
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
		
		//TraceManager.addDev("Computing results");
		//TraceManager.addDev("infos on cpus:" + cpus.size());
		//TraceManager.addDev("infos on busses:" + busses.size());
		//TraceManager.addDev("infos on tasks:" + tasks.size());
		
		
		// Compute results!
		
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
					
					//TraceManager.addDev("Found tag:" + elt.getTagName());
					
					if (elt.getTagName().compareTo(SIMULATION_GLOBAL) ==0) {
						loadGlobalConfiguration(node);
					}
					
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
		
		
		//int val;
	
		String id;
		String name;
		String util = null;
		String extime;
		String contdel;
		String busname;
		String busid;
		String state;
		String simdur;
		
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
					
					//TraceManager.addDev("Found tag tag:" + elt.getTagName());
					
					// Status
					if (elt.getTagName().compareTo(SIMULATION_DURATION) == 0) {
						simdur = elt.getTextContent();
						//System.out.println("Simulation duration=" + simdur);
						simulationDurations.add(new Long(simdur));
					}
					
					if (elt.getTagName().compareTo(SIMULATION_CPU) == 0) {
						id = null;
						name = null;
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
									
									AddingCPUResult(cpur);
								} catch (Exception e) {
								}
							}
						}
						
						
					}
					
					if (elt.getTagName().compareTo(SIMULATION_BUS) == 0) {
						name = null;
						id = null;
						extime = null;
						
						
						id = elt.getAttribute("id");
						name = elt.getAttribute("name");
						
						if ((id != null) && (name != null)) {
							nl = elt.getElementsByTagName("util");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								util = node0.getTextContent();
							}
							
							if (util != null) {
								BusResult busr = new BusResult();
								try {
									busr.id = Integer.decode(id).intValue();
									busr.name = name;
									busr.utilization = Double.valueOf(util).doubleValue();
									AddingBusResult(busr);
								} catch (Exception e) {
								}
							}
							
						}
					}
					
					if (elt.getTagName().compareTo(SIMULATION_TASK) == 0) {
						busname = null;
						busid = null;
						util = null;
						extime = null;
						state = null;
						
						id = elt.getAttribute("id");
						name = elt.getAttribute("name");
						
						if ((id != null) && (name != null)) {
							nl = elt.getElementsByTagName("extime");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								extime = node0.getTextContent();
							}
							
							nl = elt.getElementsByTagName("tskstate");
							if ((nl != null) && (nl.getLength() > 0)) {
								node0 = nl.item(0);
								//System.out.println("nl:" + nl + " value=" + node0.getNodeValue() + " content=" + node0.getTextContent());
								state = node0.getTextContent();
							}
							
							if (extime != null) {
								TaskResult tr = new TaskResult();
								try {
									tr.id = Integer.decode(id).intValue();
									tr.name = name;
									tr.nbOfExecutedCycles = Long.decode(extime).longValue();
									tr.state = state;
									AddingTaskResult(tr);
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
	
	public synchronized void AddingCPUResult(CPUResult cpur) {
		cpus.add(cpur);
	}
	
	public synchronized void AddingBusResult(BusResult br) {
		busses.add(br);
	}
	
	public synchronized void AddingTaskResult(TaskResult tr) {
		tasks.add(tr);
	}
						
	
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
	
	public void computeResults() {
		
		Hashtable resultsTable = new Hashtable();
		Object o;
		CPUWholeResult cpuwr;
		BusWholeResult buswr;
		TaskWholeResult taskwr;
		
		// Durations
		for(Long l: simulationDurations) {
			if (sdwr == null) {
				sdwr = new SimulationDurationWholeResult(l.longValue());
			} else {
				sdwr.updateResults(l.longValue());
			}
		}
		
		// CPUs
		wcpus = new Vector<CPUWholeResult>();
		for(CPUResult rescpu: cpus) {
			o = resultsTable.get(rescpu.id);
			//TraceManager.addDev("Got o=" + o);
			if (o == null) {
				cpuwr = new CPUWholeResult(rescpu);
				resultsTable.put(rescpu.id, cpuwr);
				wcpus.add(cpuwr);
				
			} else {
				cpuwr = (CPUWholeResult)o;
				cpuwr.updateResults(rescpu);
			}
		}
		
		wbusses = new Vector<BusWholeResult>();
		for(BusResult resbus: busses) {
			o = resultsTable.get(resbus.id);
			//TraceManager.addDev("Got o=" + o);
			if (o == null) {
				buswr = new BusWholeResult(resbus);
				resultsTable.put(resbus.id, buswr);
				wbusses.add(buswr);
				
			} else {
				buswr = (BusWholeResult)o;
				buswr.updateResults(resbus);
			}
		}
		
		wtasks = new Vector<TaskWholeResult>();
		for(TaskResult restask: tasks) {
			o = resultsTable.get(restask.id);
			//TraceManager.addDev("Got o=" + o);
			if (o == null) {
				taskwr = new TaskWholeResult(restask);
				resultsTable.put(restask.id, taskwr);
				wtasks.add(taskwr);
				
			} else {
				taskwr = (TaskWholeResult)o;
				taskwr.updateResults(restask);
			}
		}
		
		//TraceManager.addDev("Done compte results");
	}
	
	
	public String getWholeResults() {
		StringBuffer sb = new StringBuffer("");
		
		sb.append(sdwr.toStringResult() + "\n");
		
		for(CPUWholeResult reswcpu: wcpus) {
			sb.append(reswcpu.toStringResult() + "\n");
		}
		
		for(BusWholeResult reswbus: wbusses) {
			sb.append(reswbus.toStringResult() + "\n");
		}
		
		for(TaskWholeResult reswtask: wtasks) {
			sb.append(reswtask.toStringResult() + "\n");
		}
		
		return sb.toString();
	}
	
	public String getAllResults() {
		StringBuffer sb = new StringBuffer("");
		
		for(Long l: simulationDurations) {
			sb.append("DURATION " + l + "\n");
		}
		
		for(CPUResult rescpu: cpus) {
			sb.append(rescpu.toStringResult() + "\n");
		}
		
		for(BusResult resbus: busses) {
			sb.append(resbus.toStringResult() + "\n");
		}
		
		for(TaskResult restask: tasks) {
			sb.append(restask.toStringResult() + "\n");
		}
		
		return sb.toString();
	}
	
	public String getAllComments() {
		String s = "";
		
		for(String st: comments) {
			s+= "#" + st + "\n";
		}
		
		return s;
		
	}
	
	public static String getExplanationHeader() {
		String s;
		s = "# Simulation duration: DURATION  nbOfResults minDuration averageDuration maxDuration\n";
		s += "# CPUs: CPU ID Name nbOfResults minUtilization averageUtilization maxUtilization\n";
		s += "# Contention on busses: CPU_BUS_CONTENTION CPUID CPUName BusID BusName nbOfResults minContentionCycles averageContentionCycles maxContentionCycles\n";
		s += "# Busses: BUS ID Name nbOfResults minUtilization averageUtilization maxUtilization\n";
		s += "# Tasks: TASK ID Name nbOfResults minExecutedCycles averageExecutedCycles maxExecutedCycles nbOfRunnable nbOfRunning nbOfsuspended nbOfTerminated\n";
		
		s+= "\n";
		
		return s;
	}
	
	public static String getAllExplanationHeader() {
		String s;
		s = "# Simulation duration: DURATION value (in us)\n";
		s += "# CPUs: CPU ID Name utilization\n";
		s += "# Contention on busses: CPU_BUS_CONTENTION CPUID CPUName BusID BusName contentionCycle\n";
		s += "# Busses: BUS ID Name utilization\n";
		s += "# Tasks: TASK ID Name NbOfExecutedCycles state\n";
		return s;
	}
	
	public double getAverageCPUUsage() {
		double average = 0;
		
		for(CPUWholeResult wcpu: wcpus) {
			average += wcpu.averageUtilization;
		}
		
		return average / wcpus.size();
	}
	
	public double getMaxCPUUsage() {
		double max = 0;
		
		for(CPUWholeResult wcpu: wcpus) {
			max = Math.max(max, wcpu.maxUtilization);
		}
		
		return max;
	}
	
	public double getMinCPUUsage() {
		double min = 1.1;
		
		for(CPUWholeResult wcpu: wcpus) {
			min= Math.min(min, wcpu.minUtilization);
		}
		
		return min;
	}
	
	// Bus
	
	public double getAverageBusUsage() {
		double average = 0;
		
		for(BusWholeResult wbus: wbusses) {
			average += wbus.averageUtilization;
		}
		
		return average / busses.size();
	}
	
	public double getMaxBusUsage() {
		double max = 0;
		
		for(BusWholeResult wbus: wbusses) {
			max = Math.max(max, wbus.maxUtilization);
		}
		
		return max;
	}
	
	public double getMinBusUsage() {
		double min = 1.1;
		
		for(BusWholeResult wbus: wbusses) {
			min= Math.min(min, wbus.minUtilization);
		}
		
		return min;
	}
	
	// Bus contention
	
	
	public double getAverageBusContention() {
		double average = 0;
		
		for(CPUWholeResult wcpu: wcpus) {
			average += wcpu.getAverageBusContention();
		}
		
		return average / wcpus.size();
	}
	
	public long getMaxBusContention() {
		long max = 0;
		
		for(CPUWholeResult wcpu: wcpus) {
			max = Math.max(max, wcpu.getMaxBusContention());
		}
		
		return max;
	}
	
	public long getMinBusContention() {
		long min = 0;
		
		for(CPUWholeResult wcpu: wcpus) {
			min= Math.min(min, wcpu.getMinBusContention());
		}
		
		return min;
	}
	
	public double getAverageSimulationDuration() {
		return sdwr.averageDuration;
	}
	
	public long getMaxSimulationDuration() {
		return sdwr.maxDuration;
	}
	
	public long getMinSimulationDuration() {
		return sdwr.minDuration;
	}
	
	
	
	
	
	
} // Class DSEConfiguration


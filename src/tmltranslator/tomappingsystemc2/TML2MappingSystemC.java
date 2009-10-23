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
 * Class TML2MappingSystemC
 * Creation: 03/09/2007
 * @version 1.0 03/09/2007
 * @author Daniel Knorreck
 * @see
 */

package tmltranslator.tomappingsystemc2;

import java.util.*;

import tmltranslator.*;
import myutil.*;
import req.ebrdd.*;


public class TML2MappingSystemC {
    
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	private final static int MAX_EVENT = 1024;
	
	private TMLModeling tmlmodeling;
	private TMLMapping tmlmapping;
	
	private boolean debug;
	private String header, declaration, mainFile, src;
	private ArrayList<MappedSystemCTask> tasks;
	
	private ArrayList<EBRDD> ebrdds;
	private ArrayList<SystemCEBRDD> systemCebrdds = new ArrayList<SystemCEBRDD>();
    
	public TML2MappingSystemC(TMLModeling _tmlm) {
		tmlmodeling = _tmlm;
		tmlmapping = tmlmodeling.getDefaultMapping();
	}
	
	public TML2MappingSystemC(TMLMapping _tmlmapping) {
        tmlmapping = _tmlmapping;
		tmlmapping.makeMinimumMapping();
 	}

	public TML2MappingSystemC(TMLModeling _tmlm, ArrayList<EBRDD> _ebrdds) {
		tmlmodeling = _tmlm;
		ebrdds = _ebrdds;
		tmlmapping = tmlmodeling.getDefaultMapping();
	}
	
	public TML2MappingSystemC(TMLMapping _tmlmapping, ArrayList<EBRDD> _ebrdds) {
        tmlmapping = _tmlmapping;
		ebrdds = _ebrdds;
		tmlmapping.makeMinimumMapping();
 	}
    
	public void saveFile(String path, String filename) throws FileException {  
		generateTaskFiles(path);
        	FileUtils.saveFile(path + filename + ".cpp", getFullCode());
		src += filename + ".cpp";
		FileUtils.saveFile(path + "Makefile.src", src);
	}
	
	public String getFullCode() {
		return mainFile;
	}

    	public void generateSystemC(boolean _debug) {
        	debug = _debug;
 
		tmlmodeling = tmlmapping.getTMLModeling();
		tasks = new ArrayList<MappedSystemCTask>();

        	//generateSystemCTasks();
		generateEBRDDs();
		generateMainFile();
		generateMakefileSrc();
	}
	
	private void generateMainFile() {
		makeHeader();
		makeDeclarations();
		mainFile = header + declaration;
		mainFile = Conversion.indentString(mainFile, 4);
	}
	
	private void generateMakefileSrc() {
		src = "SRCS = ";
		for(TMLTask mst: tmlmapping.getMappedTasks()) {
			src += mst.getName() + ".cpp ";
		}
		for(EBRDD ebrdd: ebrdds){
			src += ebrdd.getName() + ".cpp ";
		}
	}
	
	private void makeHeader() {
		// System headers
		header = "#include <Simulator.h>" + CR;
		// Generate tasks header
		for(TMLTask mst: tmlmapping.getMappedTasks()) {
			//header += "#include <" + mst.getReference() + ".h>" + CR;
			header += "#include <" + mst.getName() + ".h>" + CR;
		}
		for(EBRDD ebrdd: ebrdds){
			header += "#include <" + ebrdd.getName() + ".h>" + CR;
		}
		header += CR;
	}
	
	private void makeDeclarations() {
		declaration = "class CurrentComponents: public SimComponents{\npublic:\nCurrentComponents():SimComponents(" + tmlmapping.getHashCode() + "){\n";
		
		// Declaration of HW nodes
		declaration += "//Declaration of CPUs" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwCPU) {
				if (tmlmapping.isAUsedHwNode(node)) {
					HwCPU exNode = (HwCPU)node;
					declaration += "CPU* " + exNode.getName() + " = new CPU(" + exNode.getID() + ", \"" + exNode.getName() + "\", ";
					if (exNode.getType().equals("CPURRPB"))
						declaration += "new PrioScheduler(\"" + exNode.getName() + "_PrioSched\",0), ";
					else
						 declaration += "new RRScheduler(\"" + exNode.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)exNode.execiTime)*(1+((float)exNode.branchingPredictionPenalty)/100)) + " ), ";
					declaration  += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", " + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", "  + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
					
					declaration += "addCPU("+ node.getName() +")"+ SCCR;
				}
			}
		}
		declaration += CR;
		
		// Declaration of Buses
		declaration += "//Declaration of Buses" + CR;
		//declaration+="Bus* defaultBus = new Bus(-1,\"defaultBus\",100,1,1)" + SCCR;
		//declaration += "addBus(defaultBus)"+ SCCR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwBus) {
				//if (tmlmapping.isAUsedHwNode(node)) {
				declaration += "Bus* " + node.getName() + " = new Bus("+ node.getID() + ",\"" + node.getName() + "\",0, 100, "+ ((HwBus)node).byteDataSize + ", " + node.clockRatio + ")" + SCCR;
				declaration += "addBus("+ node.getName() +")"+ SCCR;
				//}
			}
		}
		declaration += CR;

		// Declaration of Bridges
		declaration += "//Declaration of Bridges" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwBridge) {
				declaration+= "Bridge* " + node.getName() + " = new Bridge("+ node.getID() + ",\"" + node.getName() + "\", " + node.clockRatio + ", " + ((HwBridge)node).bufferByteSize + ")" +SCCR;
				declaration += "addBridge("+ node.getName() +")"+ SCCR;
			}
		}
		declaration += CR;

		// Declaration of Memories
		//declaration += "//Declaration of Memories\nMemory* defaultMemory = new Memory(-1,\"defaultMemory\",1,4)" + SCCR;
		//declaration += "addMem(defaultMemory)"+ SCCR;
		declaration += "//Declaration of Memories" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwMemory) {
				declaration+= "Memory* " + node.getName() + " = new Memory("+ node.getID() + ",\"" + node.getName() + "\", " + node.clockRatio + ", " + ((HwMemory)node).byteDataSize + ")" +SCCR;
				declaration += "addMem("+ node.getName() +")"+ SCCR;
			}
		}
		declaration += CR;

		//Declaration of Bus masters
		declaration += "//Declaration of Bus masters" + CR;
		/*for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node instanceof HwExecutionNode || node instanceof HwBridge){
				ArrayList<HwLink> nodeLinks= tmlmapping.getTMLArchitecture().getLinkByHwNode(node);
				if (nodeLinks.isEmpty())
					declaration+= node.getName() + "->addBusPriority(defaultBus,1)"+SCCR;
				else{
					for(HwLink link: nodeLinks){
						declaration+= node.getName() + "->addBusPriority(" + link.bus.getName() + "," + link.getPriority() + ")" + SCCR;
					}
				}
			} 
		}*/
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node instanceof HwExecutionNode || node instanceof HwBridge){
				ArrayList<HwLink> nodeLinks= tmlmapping.getTMLArchitecture().getLinkByHwNode(node);
				if (!nodeLinks.isEmpty()){
					//declaration+= "BusMaster* " + node.getName() + "2defaultBus = new BusMaster(\"" + node.getName() + "2defaultBus\", 0, defaultBus)" + SCCR;
				//else{
					for(HwLink link: nodeLinks){
						declaration+= "BusMaster* " + node.getName() + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + node.getName() + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", 1, array(1, (SchedulableCommDevice*)" +  link.bus.getName() + "))" + SCCR;
						//declaration+= "BusMaster* " + node.getName() + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + node.getName() + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", " + link.bus.getName() + ")" + SCCR;
						declaration+= node.getName() + "->addBusMaster(" + node.getName() + "_" + link.bus.getName() + "_Master)" + SCCR;
					}
				}
			} 
		}
		declaration += CR;
				
		// Declaration of channels
		TMLChannel channel;
		String tmp,param;
		declaration += "//Declaration of channels" + CR;
		for(TMLElement elem: tmlmodeling.getChannels()){
			if (elem instanceof TMLChannel) {
				channel = (TMLChannel)elem;
				switch(channel.getType()) {
				case TMLChannel.BRBW:
					tmp = "TMLbrbwChannel";
					param= "," + channel.getMax()*channel.getSize() + ",0";
					break;
				case TMLChannel.BRNBW:
					tmp = "TMLbrnbwChannel";
					param= ",0";
					break;
				case TMLChannel.NBRNBW:
				default:
					tmp = "TMLnbrnbwChannel";
					param= "";
				}
				declaration += tmp + "* " + channel.getExtendedName() + " = new " + tmp  +"(" + channel.getID() + ",\"" + channel.getName() + "\",";
				strwrap buses1=new strwrap(), buses2=new strwrap(), slaves1=new strwrap(), slaves2=new strwrap();
				int hopNum = addRoutingInfoForChannel(elem, ((TMLChannel)elem).getOriginTask(), tmlmapping.getHwNodeByTask(((TMLChannel)elem).getOriginTask()).getName(), buses1, slaves1, true);
				if (hopNum==-1){
					buses2.str=buses1.str;
					slaves2.str=slaves1.str;
					hopNum=2;
				}else{
					int tempHop= addRoutingInfoForChannel(elem, ((TMLChannel)elem).getDestinationTask(), tmlmapping.getHwNodeByTask(((TMLChannel)elem).getDestinationTask()).getName(), buses2, slaves2, false);
					if (tempHop==-1){
						buses1.str=buses2.str;
						slaves1.str=slaves2.str;
						hopNum=2;
					}else{
						hopNum+=tempHop;
					}	
				}
				declaration+= hopNum + ",array(" + hopNum + buses1.str + buses2.str + "),array(" + hopNum + slaves1.str + slaves2.str + ")" + param +")"+ SCCR;
				declaration += "addChannel("+ channel.getExtendedName() +")"+ SCCR;
			}
		}
		declaration += CR;
		
		// Declaration of events
		declaration += "//Declaration of events" + CR;
		for(TMLEvent evt: tmlmodeling.getEvents()) {		
			if (evt.isInfinite()) {
				tmp = "TMLEventBChannel";
				param= ",0";
			} else {
				if (evt.isBlocking()) {
					tmp = "TMLEventFBChannel";
					param= "," + evt.getMaxSize() + ",0";
				} else {
					tmp = "TMLEventFChannel";
					param= "," + evt.getMaxSize() + ",0";
				}
			}
			
			declaration += tmp + "* " + evt.getExtendedName() + " = new " + tmp + "(" + evt.getID() + ",\"" + evt.getName() + "\",0,0,0" + param +")" + SCCR;
			declaration += "addEvent("+ evt.getExtendedName() +")"+ SCCR;
		}
		declaration += CR;
		
		// Declaration of requests
		declaration += "//Declaration of requests" + CR;
		for(TMLTask task: tmlmodeling.getTasks()) {
			if (task.isRequested()){
				declaration += "TMLEventBChannel* reqChannel_"+ task.getName() + " = new TMLEventBChannel(" + task.getID() + ",\"reqChannel"+ task.getName() + "\",0,0,0,0,true)" + SCCR;
				declaration += "addRequest(reqChannel_"+ task.getName() +")"+ SCCR;
			}
		}
		declaration += CR;

		///Set bus schedulers
		declaration += "//Set bus schedulers" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwBus) {
				ArrayList<HwLink> busLinks= tmlmapping.getTMLArchitecture().getLinkByBus((HwBus)node);
				String devices="";
				int numDevices=0;
				if (!busLinks.isEmpty()){
					for(HwLink link: busLinks){
						if (link.hwnode instanceof HwExecutionNode || link.hwnode instanceof HwBridge){
							devices += ", (WorkloadSource*)" + link.hwnode.getName()+ "_" + node.getName() + "_Master";
							numDevices++;
						}
					}
					declaration += node.getName() + "->setScheduler((WorkloadSource*) new ";
					if (((HwBus)node).arbitration==0)
						declaration+="RRScheduler(\"" + node.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)node.clockRatio)/((float)((HwBus)node).byteDataSize)) + ", array(";
					else
						declaration+="PrioScheduler(\"" + node.getName() + "_PrioSched\", 0, array(";
					declaration+= numDevices + devices + "), " + numDevices + "))" + SCCR;
				}
				
			}
		}
		declaration += CR;
		
	
		//Declaration of Tasks
		ListIterator iterator;
		declaration += "//Declaration of tasks" + CR;
		HwExecutionNode node;
		iterator=tmlmapping.getNodes().listIterator();
		//for(TMLTask task: tmlmodeling.getTasks()) {
		ArrayList<TMLChannel> channels;
		ArrayList<TMLEvent> events;
		ArrayList<TMLRequest> requests;
		for(TMLTask task: tmlmapping.getMappedTasks()){
			node=(HwExecutionNode)iterator.next();
			declaration += task.getName() + "* task__" + task.getName() + " = new " + task.getName() + "("+ task.getID() +","+ task.getPriority() + ",\"" + task.getName() + "\"," + node.getName() + CR; 
			
			MappedSystemCTask mst;
			channels = (ArrayList<TMLChannel>) tmlmodeling.getChannels(task).clone();
			events = (ArrayList<TMLEvent>) tmlmodeling.getEvents(task).clone();
			requests = (ArrayList<TMLRequest>) tmlmodeling.getRequests(task).clone();
			mst = new MappedSystemCTask(task, channels, events, requests);
			mst.generateSystemC(debug);
			tasks.add(mst);

			for(TMLChannel channelb: channels) {
				declaration += "," + channelb.getExtendedName()+CR;
			}

			for(TMLEvent evt: events) {
				declaration += "," + evt.getExtendedName()+CR;
			}

			for(TMLRequest req: requests) {
				if (req.isAnOriginTask(task)) declaration+=",reqChannel_" + req.getDestinationTask().getName()+CR;
			}
			
			if (task.isRequested()) declaration += ",reqChannel_"+task.getName()+CR;
			declaration += ")" + SCCR;
			declaration += "addTask(task__"+ task.getName() +")"+ SCCR;
		}
		declaration += CR;

		//Declaration of EBRDDs
		declaration += "//Declaration of EBRDDs" + CR;
		for(EBRDD ebrdd: ebrdds){
			declaration += ebrdd.getName() + "* ebrdd__" + ebrdd.getName() + " = new " + ebrdd.getName() + "(0, \""+ ebrdd.getName() + "\");\n";
			declaration += "addEBRDD(ebrdd__"+ ebrdd.getName() +")"+ SCCR;
		}


		declaration += "}\n};\n\n";
		declaration +="#include <main.h>\n";
  	}

	private int addRoutingInfoForChannel(TMLElement _tmle, TMLTask _task, String CPUName, strwrap buses, strwrap slaves, boolean dir){
		LinkedList<HwCommunicationNode> commNodeList = tmlmapping.findNodesForElement(_tmle);
		if (debug){
			System.out.println("CommNodes for "+ _tmle.getName());
			for(HwCommunicationNode commNode: commNodeList) {
				System.out.println(commNode.getName());
			}
		}
		int hopNumber=1;
		int taskIndex = tmlmapping.getMappedTasks().indexOf(_task);
		if (debug) System.out.println("Starting from Task: " + _task.getName());
		if (taskIndex==-1){
			//buses.str=",dynamic_cast<SchedulableCommDevice*>(defaultBus)";
			buses.str="," + CPUName + "_defaultBus_Master";
			slaves.str= ",dynamic_cast<Slave*>(defaultMemory)";
			return -1;
		}
		HwBus bus= getBusConnectedToNode(commNodeList, tmlmapping.getNodes().get(taskIndex));
		if (bus==null){
			//buses.str=",dynamic_cast<SchedulableCommDevice*>(defaultBus)";
			buses.str="," + CPUName + "_defaultBus_Master";
			slaves.str= ",dynamic_cast<Slave*>(defaultMemory)";
			return -1;
		}
		//buses.str+= " ,dynamic_cast<SchedulableCommDevice*>(" + bus.getName() + ")";
		buses.str+= " ," + CPUName + "_" + bus.getName() + "_Master";
		if (debug) System.out.println("Chaining:\nFirst bus: " + bus.getName());
		HwMemory mem = getMemConnectedToBus(commNodeList, bus);
		commNodeList.remove(bus);
		HwBridge bridge;
		while(mem==null){
			bridge = getBridgeConnectedToBus(commNodeList, bus);
			if (bridge==null){
				//buses.str=",dynamic_cast<SchedulableCommDevice*>(defaultBus)";
				buses.str="," + CPUName + "_defaultBus_Master";
				slaves.str= ",dynamic_cast<Slave*>(defaultMemory)";
				return -1;
			}
			CPUName=bridge.getName();
			if (debug) System.out.println("Bridge: " + bridge.getName());
			commNodeList.remove(bridge);
			if (dir)
				slaves.str+= " ,dynamic_cast<Slave*>("+ bridge.getName() + ")";
			else
				slaves.str= " ,dynamic_cast<Slave*>("+ bridge.getName() + ")" + slaves.str;
			bus = getBusConnectedToNode(commNodeList, bridge);
			if (bus==null){
				//buses.str=",dynamic_cast<SchedulableCommDevice*>(defaultBus)";
				buses.str="," + CPUName + "_defaultBus_Master";
				slaves.str= ",dynamic_cast<Slave*>(defaultMemory)";
				return -1;
			}
			if (debug) System.out.println("Bus: " + bus.getName());
			commNodeList.remove(bus);
			if (dir) 
				//buses.str+= " ,dynamic_cast<SchedulableCommDevice*>("+ bus.getName() + ")";
				buses.str+= "," + CPUName + "_" + bus.getName() + "_Master";
			else
				//buses.str= " ,dynamic_cast<SchedulableCommDevice*>("+ bus.getName() + ")" + buses.str;
				buses.str= "," + CPUName + "_" + bus.getName() +"_Master" + buses.str;
			mem = getMemConnectedToBus(commNodeList, bus);
			hopNumber++;
		}
		if (dir) slaves.str+= " ,dynamic_cast<Slave*>("+ mem.getName() + ")"; else slaves.str= " ,dynamic_cast<Slave*>("+ mem.getName() + ")" + slaves.str;
		return hopNumber;
	}

	private HwMemory getMemConnectedToBus(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus){
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwMemory){
				if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus)) return (HwMemory)commNode;
			}
		}
		return null;
	}
	
	private HwBridge getBridgeConnectedToBus(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus){
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwBridge){
				if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus)) return (HwBridge)commNode;
			}
		}
		return null;
	}
	
	private HwBus getBusConnectedToNode(LinkedList<HwCommunicationNode> _commNodes, HwNode _node){
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwBus){
				if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(_node, (HwBus)commNode)) return (HwBus)commNode;
			}
		}
		return null;
	}



// *************** Internal structure manipulation ******************************* /
	/*private void generateSystemCTasks() {
		ListIterator iterator = tmlmodeling.getTasks().listIterator();
		TMLTask t;
		MappedSystemCTask mst;
		ArrayList<TMLChannel> channels;
		ArrayList<TMLEvent> events;
		ArrayList<TMLRequest> requests;
		while(iterator.hasNext()) {
			t = (TMLTask)(iterator.next());
			if (tmlmapping.isTaskMapped(t)) {
				channels = tmlmodeling.getChannels(t);
				events = tmlmodeling.getEvents(t);
				requests = tmlmodeling.getRequests(t);
				mst = new MappedSystemCTask(t, channels, events, requests);
				mst.generateSystemC(debug);
				//tasks.add(mst);
			}
		}
	}*/

	private void generateEBRDDs(){
		for(EBRDD ebrdd: ebrdds){
			SystemCEBRDD newEbrdd = new SystemCEBRDD(ebrdd, tmlmodeling, tmlmapping);
			newEbrdd.generateSystemC(debug);
			systemCebrdds.add(newEbrdd);
		}
	}
	
	private void generateTaskFiles(String path) throws FileException {
		for(MappedSystemCTask mst: tasks) {
			mst.saveInFiles(path);
		}
		for(SystemCEBRDD ebrdd: systemCebrdds) {
			ebrdd.saveInFiles(path);
		}
	}
}

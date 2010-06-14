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
	private boolean optimize;
	private String header, declaration, mainFile, src;
	private ArrayList<MappedSystemCTask> tasks;
	
	private ArrayList<EBRDD> ebrdds;
	private ArrayList<SystemCEBRDD> systemCebrdds = new ArrayList<SystemCEBRDD>();
	private HashMap<Integer,HashSet<Integer> > dependencies = new HashMap<Integer,HashSet<Integer> >();
	private HashSet<Integer> visitedVars = new HashSet<Integer>();
    
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

    	public void generateSystemC(boolean _debug, boolean _optimize) {
        	debug = _debug;
		optimize = _optimize;
		tmlmapping.removeAllRandomSequences();
		dependencies.clear();
		tmlmodeling = tmlmapping.getTMLModeling();
		tasks = new ArrayList<MappedSystemCTask>();
        	//generateSystemCTasks();
		generateEBRDDs();
		generateMainFile();
		generateMakefileSrc();
		System.out.println("********** All identified objects and their dependencies: **********");
		printDependencies(false);
		HashSet<Integer> keys = new HashSet<Integer>(dependencies.keySet());
		for(int elemID: keys){
			visitedVars.clear();
			eliminateStateVars(elemID, null);
		}
		System.out.println("********** System state variables and their dependency on indeterministic operators **********");
		printDependencies(true);
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
				HwCPU exNode = (HwCPU)node;
				if (exNode.getType().equals("CPURRPB"))
					declaration += "PrioScheduler* " + exNode.getName() + "_scheduler = new PrioScheduler(\"" + exNode.getName() + "_PrioSched\",0)" + SCCR;
				else
					 declaration += "RRScheduler* " + exNode.getName() + "_scheduler = new RRScheduler(\"" + exNode.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)exNode.execiTime)*(1+((float)exNode.branchingPredictionPenalty)/100)) + " ) " + SCCR;
				for(int cores=0; cores<exNode.nbOfCores; cores++){
				//if (tmlmapping.isAUsedHwNode(node)) {	
					declaration += "CPU* " + exNode.getName() + cores + " = new SingleCoreCPU(" + exNode.getID() + ", \"" + exNode.getName() + "_" + cores + "\", " + exNode.getName() + "_scheduler" + ", ";
					
					declaration  += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", " + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", "  + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
					if (cores!=0) declaration+= node.getName() + cores + "->setScheduler(" + exNode.getName() + "_scheduler,false)" + SCCR;
					declaration += "addCPU("+ node.getName() + cores +")"+ SCCR;
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
				HwBus thisBus = (HwBus)node;
				for(int i=0; i< thisBus.pipelineSize; i++){
					declaration += "Bus* " + node.getName() + "_" + i + " = new Bus("+ node.getID() + ",\"" + node.getName() + "_" + i + "\",0, 100, "+ thisBus.byteDataSize + ", " + node.clockRatio + ",";
					if(thisBus.arbitration==HwBus.CAN) declaration +="true"; else declaration +="false"; 
					declaration += ");\naddBus("+ node.getName() + "_" + i + ")"+ SCCR;
				}
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
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node instanceof HwExecutionNode || node instanceof HwBridge){
				ArrayList<HwLink> nodeLinks= tmlmapping.getTMLArchitecture().getLinkByHwNode(node);
				if (!nodeLinks.isEmpty()){
					//declaration+= "BusMaster* " + node.getName() + "2defaultBus = new BusMaster(\"" + node.getName() + "2defaultBus\", 0, defaultBus)" + SCCR;
				//else{
					for(HwLink link: nodeLinks){
						//declaration+= "BusMaster* " + node.getName() + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + node.getName() + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", 1, array(1, (SchedulableCommDevice*)" +  link.bus.getName() + "))" + SCCR;
						int noOfCores;
						if (node instanceof HwCPU) noOfCores= ((HwCPU)node).nbOfCores; else noOfCores=1;
						for (int cores=0; cores<noOfCores; cores++){
							String nodeName=node.getName();
							if (node instanceof HwCPU) nodeName+= cores;
							declaration+= "BusMaster* " + nodeName + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + nodeName + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", " + link.bus.pipelineSize + ", array(" + link.bus.pipelineSize;
							for(int i=0; i< link.bus.pipelineSize; i++)
								declaration+= ", (SchedulableCommDevice*)" +  link.bus.getName() + "_" + i; 
							declaration+= "))" + SCCR;
							declaration+= nodeName + "->addBusMaster(" + nodeName + "_" + link.bus.getName() + "_Master)" + SCCR;
						}
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
					param= "," + channel.getMax() + ",0";
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
				declaration += tmp + "* " + channel.getExtendedName() + " = new " + tmp  +"(" + channel.getID() + ",\"" + channel.getName() + "\"," + channel.getSize() + ",";
				declaration+= determineRouting(tmlmapping.getHwNodeOf(channel.getOriginTask()), tmlmapping.getHwNodeOf(channel.getDestinationTask()), elem) + param + "," + channel.getPriority() + ")"+ SCCR;
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
			if (tmlmapping.isCommNodeMappedOn(evt,null)){
				declaration += tmp + "* " + evt.getExtendedName() + " = new " + tmp + "(" + evt.getID() + ",\"" + evt.getName() + "\"," + determineRouting(tmlmapping.getHwNodeOf(evt.getOriginTask()), tmlmapping.getHwNodeOf(evt.getDestinationTask()), evt) + param +")" + SCCR;
				
			}else{
				declaration += tmp + "* " + evt.getExtendedName() + " = new " + tmp + "(" + evt.getID() + ",\"" + evt.getName() + "\",0,0,0" + param +")" + SCCR;   ///old command
			}

			declaration += "addEvent("+ evt.getExtendedName() +")"+ SCCR;
		}
		declaration += CR;
		
		// Declaration of requests
		declaration += "//Declaration of requests" + CR;
		for(TMLTask task: tmlmodeling.getTasks()) {
			if (task.isRequested()){
				if (tmlmapping.isCommNodeMappedOn(task.getRequest(),null)){
					declaration += "TMLEventBChannel* reqChannel_"+ task.getName() + " = new TMLEventBChannel(" +  task.getRequest().getID() + ",\"reqChannel"+ task.getName() + "\"," + determineRouting(tmlmapping.getHwNodeOf(task.getRequest().getOriginTasks().get(0)), tmlmapping.getHwNodeOf(task.getRequest().getDestinationTask()), task.getRequest()) + ",0,true)" + SCCR;
				}else{
					declaration += "TMLEventBChannel* reqChannel_"+ task.getName() + " = new TMLEventBChannel(" + task.getRequest().getID() + ",\"reqChannel"+ task.getName() + "\",0,0,0,0,true)" + SCCR;
				}
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
								if (link.hwnode instanceof HwCPU){
									for (int cores=0; cores< ((HwCPU)link.hwnode).nbOfCores; cores++){
										devices += ", (WorkloadSource*)" + link.hwnode.getName()+ cores + "_" + node.getName() + "_Master";
										numDevices++;
									}
								}else{
									devices += ", (WorkloadSource*)" + link.hwnode.getName()+ "_" + node.getName() + "_Master";
									numDevices++;
								}
						}
					}
					declaration += node.getName() + "_0->setScheduler((WorkloadSource*) new ";
					if (((HwBus)node).arbitration==HwBus.BASIC_ROUND_ROBIN)
						declaration+="RRScheduler(\"" + node.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)node.clockRatio)/((float)((HwBus)node).byteDataSize)) + ", array(";
					else
						declaration+="PrioScheduler(\"" + node.getName() + "_PrioSched\", 0, array(";
					declaration+= numDevices + devices + "), " + numDevices + "))" + SCCR;
				}
				for(int i=1; i< ((HwBus)node).pipelineSize; i++)
					declaration+=node.getName() + "_" + i + "->setScheduler(" + node.getName() + "_0->getScheduler(),false)" +SCCR;
			}
		}
		declaration += CR;
		
	
		//Declaration of Tasks
		ListIterator iterator = tmlmapping.getNodes().listIterator();
		declaration += "//Declaration of tasks" + CR;
		HwExecutionNode node;
		//for(TMLTask task: tmlmodeling.getTasks()) {
		ArrayList<TMLChannel> channels;
		ArrayList<TMLEvent> events;
		ArrayList<TMLRequest> requests;
		for(TMLTask task: tmlmapping.getMappedTasks()){
			node=(HwExecutionNode)iterator.next();
			int noOfCores;
			declaration += task.getName() + "* task__" + task.getName() + " = new " + task.getName() + "("+ task.getID() +","+ task.getPriority() + ",\"" + task.getName() + "\", array(";
			if (node instanceof HwCPU){
				declaration+= ((HwCPU)node).nbOfCores;
				for (int cores=0; cores< ((HwCPU)node).nbOfCores; cores++){
					declaration+= "," + node.getName()+cores;
				}
				declaration+= ")," + ((HwCPU)node).nbOfCores + CR;
			}else{ 
			 	declaration += "1," + node.getName() + "),1" + CR; 
			}
			MappedSystemCTask mst;
			//channels = (ArrayList<TMLChannel>) tmlmodeling.getChannels(task).clone();
			//events = (ArrayList<TMLEvent>) tmlmodeling.getEvents(task).clone();
			//requests = (ArrayList<TMLRequest>) tmlmodeling.getRequests(task).clone();
			channels = new ArrayList<TMLChannel>(tmlmodeling.getChannels(task));
			events = new ArrayList<TMLEvent>(tmlmodeling.getEvents(task));
			requests = new ArrayList<TMLRequest>(tmlmodeling.getRequests(task));

			mst = new MappedSystemCTask(task, channels, events, requests, tmlmapping);
			mst.generateSystemC(debug, optimize, dependencies);
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


	private int extractPath(LinkedList<HwCommunicationNode> path, strwrap masters, strwrap slaves, HwNode startNode, HwNode destNode, boolean reverseIn){
		String firstPart=""; //lastBus="";
		int masterCount=0;
		boolean reverse=reverseIn;
		if (reverseIn)
			slaves.str+=",static_cast<Slave*>(0)";
		else
			firstPart=startNode.getName() + "0";
		System.out.println("------------------------------------------------------");
		for(HwCommunicationNode commElem:path){
			System.out.println("CommELem to process: " + commElem.getName());
		}
		System.out.println("------------------------------------------------------");
		for(HwCommunicationNode commElem:path){
			System.out.println("CommELem to process: " + commElem.getName());
			//String commElemName = commElem.getName();
			//if (commElem instanceof HwCPU) commElemName += "0"; 
			if (commElem instanceof HwMemory){
				reverse=true;
				slaves.str+= ",static_cast<Slave*>(" + commElem.getName() + "),static_cast<Slave*>(" + commElem.getName() + ")";
				//firstPart=lastBus;
				firstPart="";
			}else{
				if (reverse){
					if (firstPart.length()==0){
						firstPart=commElem.getName();
						//firstPart=commElemName;
					}else{
						masters.str+= "," + commElem.getName() + "_" + firstPart + "_Master";
						//masters.str+= "," + commElemName + "_" + firstPart + "_Master";
						masterCount++;
						slaves.str+= ",static_cast<Slave*>(" + commElem.getName() + ")";
						firstPart="";
					}
				}else{
					if (firstPart.length()==0){
						firstPart=commElem.getName();
						slaves.str+= ",static_cast<Slave*>(" + firstPart + ")";
					}else{
						//lastBus=commElem.getName();
						masters.str+= "," + firstPart + "_" + commElem.getName() + "_Master";
						masterCount++;
						firstPart="";
					}
				}
			}
		}
		if (reverse){
			//masters.str+= "," + destNode.getName() + "_" + firstPart + "_Master";
			masters.str+= "," + destNode.getName() + "0_" + firstPart + "_Master";
			return masterCount+1;
		}else{
			slaves.str+=",static_cast<Slave*>(0)";
			return -masterCount;
		}
	}

	private String determineRouting(HwNode startNode, HwNode destNode, TMLElement commElemToRoute){
		strwrap masters=new strwrap(), slaves=new strwrap();
		LinkedList<HwCommunicationNode> path = new LinkedList<HwCommunicationNode>();
		LinkedList<HwCommunicationNode> commNodes = new LinkedList<HwCommunicationNode>();
		for (HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node instanceof HwCommunicationNode) commNodes.add((HwCommunicationNode) node);
		}
		HwMemory memory = getMemConnectedToBusChannelMapped(commNodes, null, commElemToRoute);
		if(memory==null){
			exploreBuses(0, commNodes, path, startNode, destNode, commElemToRoute);
		}else{
			LinkedList<HwCommunicationNode> commNodes2 = new LinkedList<HwCommunicationNode>(commNodes);
			exploreBuses(0, commNodes, path, startNode, memory, commElemToRoute);
			path.add(memory);
			exploreBuses(0, commNodes2, path, memory, destNode, commElemToRoute);

		}
		int hopNum;
		if ( (hopNum=extractPath(path, masters, slaves, startNode, destNode, false))<0){
			hopNum=extractPath(path, masters, slaves, destNode, destNode, true)-hopNum;
		}
		System.out.println(commElemToRoute.getName() + " is mapped on:");
		for(HwCommunicationNode commElem:path){
			System.out.println(commElem.getName());
		}
		System.out.println("number of elements: " + hopNum);
		System.out.println("masters: " + masters.str);
		System.out.println("slaves: " + slaves.str);
		return hopNum + ",array(" + hopNum + masters.str + "),array(" + hopNum + slaves.str + ")";
	}

	private boolean exploreBuses(int depth, LinkedList<HwCommunicationNode> commNodes, LinkedList<HwCommunicationNode> path, HwNode startNode, HwNode destNode, TMLElement commElemToRoute){
		//first called with Maping:getCommunicationNodes
		LinkedList<HwCommunicationNode> nodesToExplore;
		//System.out.println("No of comm nodes " + commNodes.size());
		boolean busExploreMode = ((depth & 1) == 0);
		//if (depth % 2 == 0){
		if(busExploreMode){
			//System.out.println("search for buses connected to " + startNode.getName());
			nodesToExplore=getBusesConnectedToNode(commNodes, startNode);
		}else{
			//System.out.println("search for bridges connected to: " + startNode.getName());
			nodesToExplore=getBridgesConnectedToBus(commNodes, (HwBus)startNode);
		}
		//HwMemory memory = null;
		//System.out.println("no of elements found: " + nodesToExplore.size());
		for(HwCommunicationNode currNode:nodesToExplore){
			//memory = null;
			if (busExploreMode){
				//memory = getMemConnectedToBusChannelMapped(commNodes, (HwBus)currNode, commElemToRoute);
				if(isBusConnectedToNode(currNode, destNode)){
					//System.out.println(currNode.getName() + " is last node");
					path.add(currNode);
					//if (memory!=null) path.add(memory);
					commNodes.remove(currNode);
					return true;
				}
			}
			if(tmlmapping.isCommNodeMappedOn(commElemToRoute, currNode)){
				//System.out.println(currNode.getName() + " mapping found for " + commElemToRoute.getName());
				path.add(currNode);
				//if (memory!=null) path.add(memory);
				commNodes.remove(currNode);
				if (exploreBuses(depth+1, commNodes, path, currNode, destNode, commElemToRoute)) return true;
				path.remove(currNode);
				//if (memory!=null) path.remove(memory);
				commNodes.add(currNode);
			}
		}
		for(HwCommunicationNode currNode:nodesToExplore){
			//if (busExploreMode) memory = getMemConnectedToBusChannelMapped(commNodes, (HwBus)currNode, commElemToRoute); else memory=null;
			path.add(currNode);
			//if (memory!=null) path.add(memory);
			commNodes.remove(currNode);
			//for (int i=0; i<path.size(); i++) System.out.print("  ");
			//System.out.println(currNode.getName());
			if (exploreBuses(depth+1, commNodes, path, currNode, destNode, commElemToRoute)) return true;
			path.remove(currNode);
			//if (memory!=null) path.remove(memory);
			commNodes.add(currNode);	
		}
		return false;
	}

	private HwMemory getMemConnectedToBusChannelMapped(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus, TMLElement _channel){
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwMemory){
				if ((_bus==null || tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus)) && tmlmapping.isCommNodeMappedOn(_channel,commNode)) return (HwMemory)commNode;
			}
		}
		return null;
	}

	private LinkedList<HwCommunicationNode> getBusesConnectedToNode(LinkedList<HwCommunicationNode> _commNodes, HwNode _node){
		LinkedList<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwBus){
				if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(_node, (HwBus)commNode)) resultList.add((HwBus)commNode);
			}
		}
		return resultList;
	}

	private LinkedList<HwCommunicationNode> getBridgesConnectedToBus(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus){
		LinkedList<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
		for(HwCommunicationNode commNode: _commNodes){
			if (commNode instanceof HwBridge){
				if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus)) resultList.add((HwBridge)commNode);
			}
		}
		return resultList;
	}
	
	private boolean isBusConnectedToNode(HwCommunicationNode commNode, HwNode node){
		for(HwLink link: tmlmapping.getTMLArchitecture().getHwLinks()) {
			if(link.bus==commNode &&  link.hwnode==node) return true;
		}
		return false;
	}



	private void eliminateStateVars(int id, HashSet<Integer>rndVars){
		visitedVars.add(id);
		HashSet<Integer> currSet = dependencies.get(id);
		if (currSet==null) return;
		HashSet<Integer> newSet = new HashSet<Integer>();
		for (int currID: currSet){
			if (!visitedVars.contains(currID)){
				if(currID>0){
					eliminateStateVars(currID, newSet);
				}else{
					newSet.add(currID);
				}
			}
		}
		dependencies.remove(id);
		if (!newSet.isEmpty()){
			dependencies.put(id, newSet);
			if (rndVars!=null) for(int currID: newSet) rndVars.add(currID);
		}
	}

	private String getIdentifierNameByID(int id){
		
		for(MappedSystemCTask task: tasks){
			String tmp = task.getIdentifierNameByID(id);
			if (tmp!=null) return tmp; 
		}
		return null;
	}

	private void printDependencies(boolean onlyState){	
		for(int elemID:dependencies.keySet()){
			if (!onlyState || elemID < Integer.MAX_VALUE/2){
				System.out.println(getIdentifierNameByID(elemID) + " depends on:");
				HashSet<Integer> deps = dependencies.get(elemID);
				for (int dep: deps){
					System.out.println("  " + getIdentifierNameByID(dep));
				}
			}
		}
		System.out.println("");
	}

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

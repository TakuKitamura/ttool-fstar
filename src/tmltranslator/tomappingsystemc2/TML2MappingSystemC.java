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
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.tomappingsystemc2;

import java.util.*;

import tmltranslator.*;
import myutil.*;


public class TML2MappingSystemC {
    
    //private static int gateId;
	
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
    
	public TML2MappingSystemC(TMLModeling _tmlm) {
		tmlmodeling = _tmlm;
		TMLArchitecture tmla = new TMLArchitecture();
		HwCPU cpu = new HwCPU("cpu0");
		cpu.byteDataSize = 4;
		cpu.pipelineSize = 1;
		cpu.goIdleTime = 0;
		cpu.taskSwitchingTime = 1;
		cpu.branchingPredictionPenalty = 0;
		cpu.execiTime = 1;
		tmla.addHwNode(cpu);
		
		tmlmapping = new TMLMapping(tmlmodeling, tmla);
		
		ListIterator iterator = _tmlm.getTasks().listIterator();
        TMLTask t;
		while(iterator.hasNext()) {
			t = (TMLTask)(iterator.next());
			tmlmapping.addTaskToHwExecutionNode(t, cpu);
		}
    }
	
    public TML2MappingSystemC(TMLMapping _tmlmapping) {
		//System.out.println("Coucou!");
        tmlmapping = _tmlmapping;
    }
    
    public void saveFile(String path, String filename) throws FileException {  
		//System.out.println("Content should be saved to path: "+path+CR);
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
        
        generateSystemCTasks();
		generateMainFile();
		generateMakefileSrc();
	}
	
	public void generateMainFile() {
		makeHeader();
		makeDeclarations();
		mainFile = header + declaration;
		mainFile = Conversion.indentString(mainFile, 4);
		//System.out.println(CR2+ "************MAINFILE************\n"+mainFile);
	}
	
	public void generateMakefileSrc() {
		src = "";
		src += "SRCS = ";
		for(MappedSystemCTask mst: tasks) {
			src += mst.getTMLTask().getName() + ".cpp ";
		}
	}
	
	public void makeHeader() {
		// System headers
		header = "#include <simkern.h>" + CR;
		// Generate tasks header
		for(MappedSystemCTask mst: tasks) {
			header += "#include <" + mst.getReference() + ".h>" + CR;
		}
		header += CR;
	}
	
	public void makeDeclarations() {
		declaration = "//************************* MAIN NEW SIMULATOR*********************\n\nint main(int len, char ** args) {\nstruct timeval begin, end;\ngettimeofday(&begin,NULL);\nCPUList cpulist;\nBusList buslist;\nTraceableDeviceList vcdlist;\n\n";
		
		// Hw nodes
		declaration += "//Declaration of hardware nodes" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwCPU) {
				if (tmlmapping.isAUsedHwNode(node)) {
					HwCPU exNode = (HwCPU)node;
					declaration += exNode.getType() + " " + exNode.getName() + "(\"" + exNode.getName() + "\", " + exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", " + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", "  + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
					declaration += "cpulist.push_back(&"+ node.getName() + ")" + SCCR;
					declaration += "vcdlist.push_back((TraceableDevice*)&"+ node.getName() + ")" + SCCR;
				}
			}
		}
		declaration += CR;
		
		//Buses
		declaration+="Bus defaultBus(\"defaultBus\",100)" + SCCR;
		declaration += "buslist.push_back(&defaultBus)" + SCCR;
		declaration += "vcdlist.push_back((TraceableDevice*)&defaultBus)" + SCCR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwBus) {
				//if (tmlmapping.isAUsedHwNode(node)) {
					declaration += "Bus " + node.getName() + "(\"" + node.getName() + "\",100)" + SCCR;
					declaration += "buslist.push_back(&"+ node.getName() + ")" + SCCR;
					declaration += "vcdlist.push_back((TraceableDevice*)&"+ node.getName() + ")" + SCCR;
				//}
			}
		}
		declaration += CR;

		// Channels, events, requests
		ListIterator iterator;
		TMLChannel channel;
		HwCommunicationNode commNode;
		int indexOfChannel;
		String tmp,param;
		
		declaration += "//Declaration of channels" + CR;
		//iterator=tmlmapping.getCommunicationNodes().listIterator();
		//for(TMLElement elem: tmlmapping.getMappedCommunicationElement()) {
		for(TMLElement elem: tmlmodeling.getChannels()){
			//commNode=(HwCommunicationNode)iterator.next();
			if (elem instanceof TMLChannel) {
				channel = (TMLChannel)elem;
				commNode=null;
				indexOfChannel=tmlmapping.getMappedCommunicationElement().indexOf(channel);
				if (indexOfChannel!=-1) commNode=tmlmapping.getCommunicationNodes().get(indexOfChannel);
				switch(channel.getType()) {
				case TMLChannel.BRBW:
					tmp = "TMLbrbwChannel ";
					param= "," + channel.getMax()*channel.getSize() + ",0";
					break;
				case TMLChannel.BRNBW:
					tmp = "TMLbrnbwChannel ";
					param= ",0";
					break;
				case TMLChannel.NBRNBW:
				default:
					tmp = "TMLnbrnbwChannel ";
					param= "";
				}
				if (commNode==null)
					declaration += tmp + " " + channel.getExtendedName() +"(\"" + channel.getName() + "\",&defaultBus" + param +")"+ SCCR;
				else
					declaration += tmp + " " + channel.getExtendedName() +"(\"" + channel.getName() + "\",&" + commNode.getName() + param +")"+ SCCR;
			}
		}
		declaration += CR;
		
		declaration += "//Declaration of events" + CR;
		for(TMLEvent evt: tmlmodeling.getEvents()) {		
			if (evt.isInfinite()) {
				tmp = "TMLEventBChannel ";
				param= ",0";
			} else {
				if (evt.isBlocking()) {
					tmp = "TMLEventFBChannel ";
					param= "," + evt.getMaxSize() + ",0";
				} else {
					tmp = "TMLEventFChannel ";
					param= "," + evt.getMaxSize() + ",0";
				}
			}
			indexOfChannel=tmlmapping.getMappedCommunicationElement().indexOf(evt);
			if (indexOfChannel!=-1) commNode=tmlmapping.getCommunicationNodes().get(indexOfChannel); else commNode=null; 
			if (commNode==null || !(commNode instanceof HwBus))
				declaration += tmp + evt.getExtendedName() + "(\"" + evt.getName() + "\",0" + param +")" + SCCR;
			else
				declaration += tmp + evt.getExtendedName() + "(\"" + evt.getName() + "\",&" + commNode.getName() + param +")" + SCCR;
		}
		declaration += CR;
		
		declaration += "//Declaration of requests" + CR;
		for(TMLTask task: tmlmodeling.getTasks()) {
			if (task.isRequested()) declaration += "TMLEventBChannel reqChannel_"+ task.getName() + "(\"reqChannel"+ task.getName() + "\",0,0,true)" + SCCR;
		}
		declaration += CR;
		
		//Registration of CPUs
		HwLink link;
		declaration += "//Registration of CPUs" + CR;
		//iterator=tmlmapping.getTMLArchitecture().getHwNodes().listIterator();
		//for(HwLink link: tmlmapping.getTMLArchitecture().getHwLinks()){
		//	declaration+=link.bus.getName() + ".registerMasterDevice(&" + ((HwNode)iterator.next()).getName() + ")" + SCCR; 
		//}
		PriorityQueue<HwLink> pq=new PriorityQueue<HwLink>(20);
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node instanceof HwExecutionNode){
				//getPriority
				link = tmlmapping.getTMLArchitecture().getLinkByHwNode(node);
				if (link==null){
					declaration+= "defaultBus.registerMasterDevice(&" + node.getName() + ")" + SCCR;
				}else{
					pq.add(link);
					//declaration+=link.bus.getName() + ".registerMasterDevice(&" + node.getName() + ")" + SCCR;
				}
			} 
		}
		while (!pq.isEmpty()){
			link = pq.remove();
			declaration+=link.bus.getName() + ".registerMasterDevice(&" + link.hwnode.getName() + ")" + SCCR;
		}
		declaration += CR;
	
		// Tasks
		declaration += "//Declaration of tasks" + CR;
		HwExecutionNode node;
		iterator=tmlmapping.getNodes().listIterator();
		//for(TMLTask task: tmlmodeling.getTasks()) {
		for(TMLTask task: tmlmapping.getMappedTasks()){
			node=(HwExecutionNode)iterator.next();
			declaration += task.getName() + " task__" + task.getName() + "(" + task.getPriority() + ",\"" + task.getName() + "\",&" + node.getName() + CR; 
			for(TMLChannel channelb: tmlmodeling.getChannels(task)) {
				declaration += ",&" + channelb.getExtendedName()+CR;
			}
			for(TMLEvent evt: tmlmodeling.getEvents(task)) {		
				declaration += ",&" + evt.getExtendedName()+CR;
			}
			//for(TMLRequest req: tmlmodeling.getRequests()) {
			for(TMLRequest req: tmlmodeling.getRequests(task)) {
				if (req.isAnOriginTask(task)) declaration+=",&reqChannel_" + req.getDestinationTask().getName()+CR;
			}
			if (task.isRequested()) declaration += ",&reqChannel_"+task.getName()+CR;
			declaration += ")" + SCCR;
			declaration += "vcdlist.push_back((TraceableDevice*)&task__"+ task.getName() + ");\n\n";
		}
		declaration += CR;
		
		//Task registration
		/*declaration += "//Tasks registration" + CR;
		HwExecutionNode node;
		iterator=tmlmapping.getNodes().listIterator();
		for(TMLTask task: tmlmapping.getMappedTasks()){
			node=(HwExecutionNode)iterator.next();
			declaration += node.getName() + ".registerTask(&task__" + task.getName() + ")" + SCCR;
		}
		declaration += CR;*/

		declaration+="gettimeofday(&end,NULL);\nstd::cout << \"The preparation took \" << getTimeDiff(begin,end) << \"usec.\\n\";\nsimulate(cpulist,buslist);\nschedule2HTML(cpulist,buslist,len,args);\nschedule2VCD(vcdlist,len,args);\nschedule2Graph(cpulist, len, args);\nreturn 0;\n}\n";
  }
  

  // *************** Internal structure manipulation ******************************* /
    
    public void generateSystemCTasks() {
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
				tasks.add(mst);
			}
        }
    }
	
	public void generateTaskFiles(String path) throws FileException {
		for(MappedSystemCTask mst: tasks) {
			mst.saveInFiles(path);
		}
	}
        

    
}
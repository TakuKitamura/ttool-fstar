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

package tmltranslator.tomappingsystemc;

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
    
    
    private String header, declaration, thread, simulation, mainFile, src;
	
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
		
		tmlmapping = new TMLMapping(tmlmodeling, tmla, false);
		
		ListIterator iterator = _tmlm.getTasks().listIterator();
        TMLTask t;
		while(iterator.hasNext()) {
			t = (TMLTask)(iterator.next());
			tmlmapping.addTaskToHwExecutionNode(t, cpu);
		}
    }
	
    public TML2MappingSystemC(TMLMapping _tmlmapping) {
        tmlmapping = _tmlmapping;
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
        
        generateSystemCTasks();
		generateMainFile();
		generateMakefileSrc();
	}
	
	public void generateMainFile() {
		makeHeader();
		makeDeclarations();
		makeThreadsCode();
		makeSimulationCode();
		mainFile = header + declaration + thread + simulation;
		mainFile = Conversion.indentString(mainFile, 4);
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
		header = "#include \"systemc.h\"" + CR;
		header += "#include \"node_labsoc.h\"" + CR;
		header += "#include \"cpu_labsoc.h\"" + CR;
		header += "#include \"cpurr_labsoc.h\"" + CR;
		header += "#include \"cpurrpb_labsoc.h\"" + CR;
		header += "#include \"task_labsoc.h\"" + CR;
		header += "#include \"channel_labsoc.h\"" + CR;
		
		// Generate tasks header
		for(MappedSystemCTask mst: tasks) {
			header += "#include \"" + mst.getReference() + ".h\"" + CR;
		}
		header += CR;
	}
	
	public void makeDeclarations() {
		declaration = "class testbench: public sc_module {" + CR2;
		declaration += "public:" + CR;
		declaration += "sc_in<bool> clk;" + CR2;
		
		// Hw nodes
		declaration += "//Declaration of hardware nodes" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (node instanceof HwExecutionNode) {
				if (tmlmapping.isAUsedHwNode(node)) {
					declaration += ((HwExecutionNode)node).getType() + " " + node.getName() + SCCR;
				}
			}
		}
		declaration += CR;
		
		// Tasks
		declaration += "//Declaration of tasks" + CR;
		for(MappedSystemCTask mst: tasks) {
			declaration += mst.getReference() + " task__" + mst.getReference() + SCCR;
		}
		declaration += CR;
		
		// Channels, events, requests
		ListIterator iterator;
		TMLChannel channel;
		TMLEvent evt;
		TMLRequest req;
		int type;
		String tmp;
		
		declaration += "//Declaration of channels" + CR;
		iterator = tmlmodeling.getListIteratorChannels();
		while(iterator.hasNext()) {
			channel = (TMLChannel)(iterator.next());
			type = channel.getType();
			switch(type) {
			case TMLChannel.BRBW:
				tmp = "BRBW_Channel";
				break;
			case TMLChannel.BRNBW:
				tmp = "BRNBW_Channel";
				break;
			case TMLChannel.NBRNBW:
			default:
				tmp = "NBRNBW_Channel";
			}
			declaration += tmp + " " + channel.getExtendedName() + SCCR;
		}
		declaration += CR;
		
		declaration += "//Declaration of events" + CR;
		iterator = tmlmodeling.getListIteratorEvents();
		while(iterator.hasNext()) {
			evt = (TMLEvent)(iterator.next());
			if (evt.isInfinite()) {
				tmp = "InfiniteFIFO_Event ";
			} else {
				if (evt.isBlocking()) {
					tmp = "FiniteBlockingFIFO_Event ";
				} else {
					tmp = "FiniteFIFO_Event ";
				}
			}
			declaration += tmp + evt.getExtendedName() + SCCR;
		}
		declaration += CR;
		
		declaration += "//Declaration of requests" + CR;
		iterator = tmlmodeling.getListIteratorRequests();
		while(iterator.hasNext()) {
			req = (TMLRequest)(iterator.next());
			declaration += "InfiniteFIFO_Event " + req.getExtendedName() + SCCR;
		}
		declaration += CR;
		
		// Starting threads
		declaration += CR +  "SC_HAS_PROCESS(testbench)" + SCCR;
		declaration += CR + "testbench(sc_module_name nm) {" + CR;
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
			if (tmlmapping.isAUsedHwNode(node)) {
				declaration += "SC_THREAD(main__" + node.getName() + ")" + SCCR;
			}
		}
		declaration += "sensitive<<clk.pos()" + SCCR;
		declaration += "dont_initialize()" + SCCR + CR + EFCR2;
  }
  public void makeThreadsCode() {
	  thread = "";
	  for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
		  if (node instanceof HwExecutionNode) {
			if (tmlmapping.isAUsedHwNode(node)) {
				makeThreadCode((HwExecutionNode)node);
			}
		  }
		}
		thread += "}" + SCCR + CR;
  }
  
  public void makeThreadCode(HwExecutionNode node) {
	  ListIterator iterator;
	  TMLChannel channel;
	  TMLEvent event;
	  TMLRequest request;
	  TMLTask task;
	  boolean first;
	  
	  thread += "// Thread of " + node.getType() + ": " + node.getName() + CR;
	  thread += "void main__" +  node.getName() + "(){" + CR;
		  if (debug) {
			  thread += "cout<<\"" + node.getName() + "is starting\\n\"" + SCCR;
		  }
		  
      // Setting internal channels
	  thread += CR + "// Setting channels" + CR;
	  iterator = tmlmodeling.getListIteratorChannels();
		while(iterator.hasNext()) {
			channel = (TMLChannel)(iterator.next());
			if ((tmlmapping.isTaskMappedOn(channel.getOriginTask(), node)) && (tmlmapping.isTaskMappedOn(channel.getDestinationTask(), node))) {
				thread += channel.getExtendedName() + ".initialize()" + SCCR;
				thread += channel.getExtendedName() + ".setNode(&" + node.getName() + ")" + SCCR;
				thread += channel.getExtendedName() + ".setWidth(" + channel.getSize() + ")" + SCCR;
				thread += channel.getExtendedName() + ".setCurrentNbOfSamples(0)" + SCCR;
				if (channel.getType() == TMLChannel.BRBW) {
					thread += channel.getExtendedName() + ".setMaxNbOfSamples(" + channel.getMax() + ")" + SCCR;
				}
			}
		}
		thread += CR;
		
		 // Setting internal events
		thread += "// Setting events" + CR;
		iterator = tmlmodeling.getListIteratorEvents();
		while(iterator.hasNext()) {
			event = (TMLEvent)(iterator.next());
			if ((tmlmapping.isTaskMappedOn(event.getOriginTask(), node)) && (tmlmapping.isTaskMappedOn(event.getDestinationTask(), node))) {
				thread += event.getExtendedName() + ".initialize()" + SCCR;
				thread += event.getExtendedName() + ".setNode(&" + node.getName() + ")" + SCCR;
				if (!event.isInfinite()) {
					thread += event.getExtendedName() + ".setMaxNbOfEvents(" + event.getMaxSize() + ")" + SCCR;
				} else {
					thread += event.getExtendedName() + ".setMaxNbOfEvents(" + MAX_EVENT + ")" + SCCR;
				}
			}
		}
		thread += CR;
		
		// Setting internal requests
		thread += "// Setting requests" + CR;
		iterator = tmlmodeling.getListIteratorRequests();
		while(iterator.hasNext()) {
			request = (TMLRequest)(iterator.next());
			if (tmlmapping.oneTaskMappedOn(request, node)) {
				thread += request.getExtendedName() + ".initialize()" + SCCR;
				thread += request.getExtendedName() + ".setNode(&" + node.getName() + ")" + SCCR;
				thread += request.getExtendedName() + ".setMaxNbOfEvents(" + MAX_EVENT + ")" + SCCR;
			}
		}
		thread += CR;
		
		// Setting tasks
		iterator = tmlmodeling.getListIteratorTasks();
		while(iterator.hasNext()) {
			task = (TMLTask)(iterator.next());
			if (tmlmapping.getHwNodeOf(task) == node) {
				thread += "task__" + task.getName() + ".initialize()" + SCCR;
				thread += "task__" + task.getName() + ".setNode(&" + node.getName() + ")" + SCCR;
				thread += "task__" + task.getName() + ".setPriority(" + task.getPriority() + ")" + SCCR;
				
				// Setting channels and events
				if (tmlmodeling.getChannels(task).size() > 0) {
					thread += "task__" + task.getName() + ".setChannels(";
					first = true;
					for(TMLChannel channelb: tmlmodeling.getChannels(task)) {
						if (!first) {
							thread += ", ";
						} else {
							first = false;
						}
						thread += "&" + channelb.getExtendedName();
					}
					thread += ")" + SCCR;
				}
				
				if (tmlmodeling.getEvents(task).size() > 0) {
					thread += "task__" + task.getName() + ".setEvents(";
						first = true;
					for(TMLEvent eventb: tmlmodeling.getEvents(task)) {
						if (!first) {
							thread += ", ";
						} else {
							first = false;
						}
						thread += "&" + eventb.getExtendedName();
					}
					thread += ")" + SCCR;
				}
				
				// Set receive events
				for(TMLEvent eventb: tmlmodeling.getEvents(task)) {
					if (eventb.getDestinationTask() == task) {
						thread += "task__" + task.getName() + ".addReceivedEvent(&" + eventb.getExtendedName();
						thread += ")" + SCCR;	
					}
				}
				
				if (tmlmodeling.getRequests(task).size() > 0) {
					thread += "task__" + task.getName() + ".setRequests(";
					first = true;
					for(TMLRequest requestb: tmlmodeling.getRequests(task)) {
						if (!first) {
							thread += ", ";
						} else {
							first = false;
						}
						thread += "&" + requestb.getExtendedName();
					}
					thread += ")" + SCCR;
				}
				thread += CR;
			}
		}


		// Setting node
		thread += "// Setting node" + CR;
		if (debug) {
			thread += "cout<<\"Setting node" + node.getName() + "\\n\"" + SCCR;
		}
		thread += node.getName() + ".initialize()" + SCCR;
		
		iterator = tmlmodeling.getListIteratorTasks();
		while(iterator.hasNext()){
			task = (TMLTask)(iterator.next());
			if (tmlmapping.isTaskMappedOn(task, node)) {
				thread += node.getName() + ".addTask(&task__" + task.getName() + ")" + SCCR;
			}
		}
		if (node instanceof HwCPU) {
			HwCPU cpu = (HwCPU)node;
			String tmp = node.getName();
			thread += tmp + ".setByteDataSize(" + cpu.byteDataSize + ")" + SCCR;
			thread += tmp + ".setPipelineSize(" + cpu.pipelineSize + ")" + SCCR;
			thread += tmp + ".setGoIdleTime(" + cpu.goIdleTime + ")" + SCCR;
			thread += tmp + ".setMaxConsecutiveIdleCycles(" + cpu.maxConsecutiveIdleCycles + ")" + SCCR;
			thread += tmp + ".setTaskSwitchingTime(" + cpu.taskSwitchingTime + ")" + SCCR;
			thread += tmp + ".setBranchingPredictionMissRate(" + cpu.branchingPredictionPenalty + ")" + SCCR;
			thread += tmp + ".setCyclePerEXECIOp(" + cpu.execiTime + ")" + SCCR;
		}
		
		if (debug) {
			thread += "cout<<\"Starting node " + node.getName() + "\\n\"" + SCCR;
		}
		
		thread += node.getName() + ".go()" + SCCR;
		
		if (debug) {
			thread += "cout<<\"Ending node " + node.getName() + "\\n\"" + SCCR;
		}
		
		thread += "exit(0)" + SCCR;
		thread += EFCR2;
  }
  
  public void makeSimulationCode() {
	  String tr = "sc_trace(tf, tb.";
	  
	  simulation = "";
	  simulation = "//************************* MAIN *********************\n\nint sc_main(int len, char ** args) {\n";
      simulation += "sc_clock clk(\"clk\", 1);\n\n";
      simulation += "testbench tb(\"tb\");\n";
      simulation += "tb.clk(clk);\n\n";
	  simulation += "sc_trace_file *tf;\n";
      simulation += "if (len>1) {\ntf = sc_create_vcd_trace_file(args[1]);\n} else {\n";     
	  simulation += "tf = sc_create_vcd_trace_file(\"vcddump\");\n}\n";
      simulation += "sc_trace(tf, tb.clk, \"CLOCK_TICKS\");\n\n";
	  
	  simulation += "// Tracing nodes" + CR;
	  for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()) {
		if (tmlmapping.isAUsedHwNode(node)) {
			if (node instanceof HwCPU) {
				traceCPU(tr + node.getName(), node.getName().toUpperCase());
			}
		}
	  }
	  simulation += CR;
	  
	  simulation += "// Tracing tasks" + CR;
	  TMLTask task;
	  ListIterator iterator = tmlmodeling.getListIteratorTasks();
		while(iterator.hasNext()) {
			task = (TMLTask)(iterator.next());
			if (tmlmapping.isTaskMapped(task)) {
				traceTask(task);
			}
		}
	  
	  simulation += "\nsc_start(-1);\n";
      simulation += "\nsc_close_vcd_trace_file(tf);\n\n";
      simulation += "return 1;\n}\n";
	  
	  
  }
  
  public void traceCPU(String tr, String upper) {
	  simulation += tr + ".runningTask, \"" + upper + "__RUNNING_TASK\")" + SCCR;
	  simulation += tr + ".taskSwitching, \"" + upper + "__TASK_SWITCHING\")" + SCCR;
	  simulation += tr + ".idle, \"" + upper + "__idle\")" + SCCR;
	  simulation += tr + ".goIdle, \"" + upper + "__goingIdle\")" + SCCR;
	  simulation += tr + ".pipelineLatency, \"" + upper + "__PIPELINE_LATENCY\")" + SCCR;
	  simulation += tr + ".branchingError, \"" + upper + "__BRANCHING_ERROR\")" + SCCR;
  }
  
  public void traceTask(TMLTask task) {
	String tr = "sc_trace(tf, tb." + task.getExtendedName();
	String TR = task.getExtendedName().toUpperCase() + '_';
	//String node = tmlmapping.getHwNodeOf(task).getName().toUpperCase() + "__" + TR + "_";
	simulation += tr + ".runnable, \"" + TR + "RUNNABLE\")" + SCCR;
	simulation += tr + ".running, \"" + TR + "RUNNING\")" + SCCR;
	simulation += tr + ".terminated, \"" + TR + "TERMINATED\")" + SCCR;
	simulation += tr + ".blocked, \"" + TR + "BLOCKED\")" + SCCR;
	simulation += tr + ".execi, \"" + TR + "EXECI\")" + SCCR;
	
	// Channels
	// Setting channels and events
	for(TMLChannel channelb: tmlmodeling.getChannels(task)) {
		if (channelb.getOriginTask() == task) {
			simulation += tr + ".wr__" + channelb.getName()+ ", \"" + TR + "WRITE_" + channelb.getName() + "\")" + SCCR;
		}
		if (channelb.getDestinationTask() == task) {
			simulation += tr + ".rd__" + channelb.getName()+ ", \"" + TR + "READ_" + channelb.getName() + "\")" + SCCR;
		}
	}
				
	for(TMLEvent eventb: tmlmodeling.getEvents(task)) {
		if (eventb.getOriginTask() == task) {
			simulation += tr + ".notify__" + eventb.getName()+ ", \"" + TR + "NOTIFY_" + eventb.getName() + "\")" + SCCR;
		}
		if (eventb.getDestinationTask() == task) {
			simulation += tr + ".wait__" + eventb.getName()+ ", \"" + TR + "WAIT_" + eventb.getName() + "\")" + SCCR;
		}
	}
				
	for(TMLRequest requestb: tmlmodeling.getRequests(task)) {
		if (requestb.isAnOriginTask(task)) {
			simulation += tr + ".sendrequest__" + requestb.getName()+ ", \"" + TR + "REQUEST_" + requestb.getName() + "\")" + SCCR;
		}
		if (requestb.getDestinationTask() == task) {
			simulation += tr + ".wait4request__" + requestb.getName()+ ", \"" + TR + "WAIT4REQUEST_" + requestb.getName() + "\")" + SCCR;
		}
	}

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
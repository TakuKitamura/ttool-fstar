/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT telecom-paristech.fr
   andrea.enrici AT telecom-paristech.fr

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
   * Class TMLMapping
   * Creation: 05/09/2007
   * @version 1.1 10/06/2014
   * @author Ludovic APVRILLE, Andrea ENRICI
   * @see
   */

package tmltranslator;

import java.util.*;
import myutil.*;


public class TMLMapping {

    private TMLModeling tmlm;
    private TMLArchitecture tmla;
    private TMLCP tmlcp;

    private ArrayList<HwExecutionNode> onnodes;
    private ArrayList<TMLTask> mappedtasks;
    private ArrayList<HwCommunicationNode> oncommnodes;
    private ArrayList<TMLElement> mappedcommelts;

    private ArrayList<TMLCP> mappedCPs;
    private ArrayList<TMLElement> commEltsMappedOnCPs;

    private ArrayList<TMLCPLib> mappedCPLibs;


    private boolean optimized = false;

    private int hashCode;
    private boolean hashCodeComputed = false;

    public TMLMapping(TMLModeling _tmlm, TMLArchitecture _tmla, boolean reset) {

        tmlm = _tmlm;
        tmla = _tmla;
        init();

        if( reset ) {
            DIPLOElement.resetID();
        }
    }

    public TMLMapping( TMLModeling _tmlm, TMLArchitecture _tmla, TMLCP _tmlcp, boolean reset ) {

        tmlm = _tmlm;
        tmla = _tmla;
        tmlcp = _tmlcp;
        init();

        if( reset ) {
            DIPLOElement.resetID();
        }
    }

    public void makeMinimumMapping() {
        HwCPU cpu;
        HwMemory mem;
        HwBus bus;
        HwLink link0, link1;
        TMLTask t;
        TMLChannel ch;
        ListIterator iterator;
        int cpt;

        if (tmla == null) {
            tmla = new TMLArchitecture();
        }

        if (!tmla.hasCPU()) {
            cpu = new HwCPU("defaultCPU");
            cpu.byteDataSize = 4;
            cpu.pipelineSize = 1;
            cpu.goIdleTime = 0;
            cpu.taskSwitchingTime = 1;
            cpu.branchingPredictionPenalty = 0;
            cpu.execiTime = 1;
            tmla.addHwNode(cpu);

            // tasks
            iterator = tmlm.getTasks().listIterator();
            while(iterator.hasNext()) {
                t = (TMLTask)(iterator.next());
                addTaskToHwExecutionNode(t, cpu);
            }
        }

        if (!tmla.hasBus()) {
            bus = new HwBus("defaultBus");
            tmla.addHwNode(bus);
            // Connect all possible nodes to that bus
            cpt = 0;
            for(HwNode node: tmla.getHwNodes()) {
                link0 = new HwLink("to_bus_" + cpt);
                cpt ++;
                link0.bus = bus;
                link0.hwnode = node;
                tmla.addHwLink(link0);
            }

            // Add all channels on that bus
            iterator = tmlm.getChannels().listIterator();
            while(iterator.hasNext()) {
                ch = (TMLChannel)(iterator.next());
                addCommToHwCommNode(ch, bus);
            }
        } else {
            mapAllChannelsOnBus();
        }

        // Is there a memory?
        /*if (!tmla.hasMemory()) {
          mem = new HwMemory("defaultMemory");
          tmla.addHwNode(mem);

          // Connect this memory to al buses


          iterator = tmlm.getChannels().listIterator();

          while(iterator.hasNext()) {
          ch = (TMLChannel)(iterator.next());
          addCommToHwCommNode(ch, mem);
          }
          } else {
          mapAllChannelsOnMemory();
          }*/
    }

    // If only one bus -> map all channels on it;
    private void mapAllChannelsOnBus() {
        // Check if only one bus
        if (getNbOfBusses() != 1) {
            return;
        }

        HwBus bus = tmla.getFirstBus();

        int index;
        boolean mapped;
        for(TMLChannel cha: tmlm.getChannels()) {
            index = 0;
            mapped = false;
            for(TMLElement el: mappedcommelts) {
                if (el == cha) {
                    if (oncommnodes.get(index) instanceof HwBus) {
                        mapped = true;
                        break;
                    }
                }
                index ++;
            }
            if (!mapped) {
                addCommToHwCommNode(cha, bus);
            }
        }
    }

    // If only one memory -> map all channels on it;
    private void mapAllChannelsOnMemory() {
        // Check if only one bus
        if (getNbOfMemories() != 1) {
            return;
        }

        HwMemory mem = tmla.getFirstMemory();

        int index;
        boolean mapped;
        for(TMLChannel cha: tmlm.getChannels()) {
            index = 0;
            mapped = false;
            for(TMLElement el: mappedcommelts) {
                if (el == cha) {
                    if (oncommnodes.get(index) instanceof HwMemory) {
                        mapped = true;
                        break;
                    }
                }
                index ++;
            }
            if (!mapped) {
                addCommToHwCommNode(cha, mem);
            }
        }
    }

    public int getNbOfBusses() {
        if (tmla == null) {
            return 0;
        }
        return tmla.getNbOfBusses();
    }

    public int getNbOfMemories() {
        if (tmla == null) {
            return 0;
        }
        return tmla.getNbOfMemories();
    }

    private void init() {
        mappedtasks = new ArrayList<TMLTask>();
        onnodes = new ArrayList<HwExecutionNode>();
        oncommnodes = new ArrayList<HwCommunicationNode>();
        mappedcommelts = new ArrayList<TMLElement>();
        mappedCPs = new ArrayList<TMLCP>();
        commEltsMappedOnCPs = new ArrayList<TMLElement>();
	mappedCPLibs = new ArrayList<TMLCPLib>();
    }

    public TMLTask getTMLTaskByCommandID(int id) {
        if (tmlm == null) {
            return null;
        }

        return tmlm.getTMLTaskByCommandID(id);
    }

    public String[] getTasksIDs() {
        if (tmlm == null) {
            return null;
        }

        return tmlm.getTasksIDs();
    }

    public String[] getChanIDs() {
        if (tmlm == null) {
            return null;
        }

        return tmlm.getChanIDs();
    }

    public String[] getCPUIDs() {
        if (tmla == null) {
            return null;
        }

        return tmla.getCPUIDs();
    }

    public String[] getCPUandHwAIDs() {
        if (tmla == null) {
            return null;
        }

        return tmla.getCPUandHwAIDs();
    }


    public String[] getBusIDs() {
        if (tmla == null) {
            return null;
        }

        return tmla.getBusIDs();
    }

    public String[] getMemIDs() {
        if (tmla == null) {
            return null;
        }

        return tmla.getMemIDs();
    }

    public String[] makeCommandIDs(int index) {
        if (tmlm == null) {
            return null;
        }

        return tmlm.makeCommandIDs(index);
    }

    public String[] makeVariableIDs(int index) {
        if (tmlm == null) {
            return null;
        }

        return tmlm.makeVariableIDs(index);
    }


    public void addTMLCPLib(TMLCPLib _tmlcplib) {
	mappedCPLibs.add(_tmlcplib);
    }

		public ArrayList<TMLCPLib> getMappedTMLCPLibs()	{
			return mappedCPLibs;
		}	

    public void addTaskToHwExecutionNode(TMLTask _task, HwExecutionNode _hwnode) {
        onnodes.add(_hwnode);
        mappedtasks.add(_task);
    }

    public void addCommToHwCommNode(TMLElement _elt, HwCommunicationNode _hwcommnode) {
        oncommnodes.add(_hwcommnode);
        mappedcommelts.add(_elt);
    }

    public TMLModeling getTMLModeling() {
        return tmlm;
    }
    public TMLArchitecture getTMLArchitecture() {
        return tmla;
    }

    private void computeHashCode() {
        hashCode = tmlm.getHashCode() + tmla.getHashCode();
        TMLMappingTextSpecification tmaptxt = new TMLMappingTextSpecification("spec.tmap");
        hashCode += tmaptxt.toTextFormat(this).hashCode();
    }

    public int getHashCode() {
        if (!hashCodeComputed) {
            computeHashCode();
            hashCodeComputed = true;
        }
        return hashCode;
    }

    public ArrayList<HwExecutionNode> getNodes(){
        return onnodes;
    }

    public ArrayList<TMLTask> getMappedTasks(){
        return mappedtasks;
    }

    public ArrayList<HwCommunicationNode> getCommunicationNodes(){
        return oncommnodes;
    }

    public ArrayList<TMLElement> getMappedCommunicationElement(){
        return mappedcommelts;
    }


    public TMLTask getTaskByName(String _name) {
        return tmlm.getTMLTaskByName(_name);
    }

    public TMLChannel getChannelByName(String _name) {
        TMLElement tmle = tmlm.getCommunicationElementByName(_name);
        if (tmle instanceof TMLChannel) {
            return (TMLChannel)tmle;
        }
        return null;
    }

    public HwExecutionNode getHwExecutionNodeByName(String _name) {
        HwNode node = tmla.getHwNodeByName(_name);
        if (node instanceof HwExecutionNode) {
            return (HwExecutionNode)(node);
        }
        return null;
    }

    public HwCommunicationNode getHwCommunicationNodeByName(String _name) {
        HwNode node = tmla.getHwNodeByName(_name);
        if (node instanceof HwCommunicationNode) {
            return (HwCommunicationNode)(node);
        }
        return null;
    }

    public TMLElement getCommunicationElementByName(String _name) {
        return tmlm.getCommunicationElementByName(_name);
    }

    public boolean isTaskMapped(TMLTask _task) {
        return (mappedtasks.contains(_task));
    }

    public HwNode getHwNodeOf(TMLTask _task) {
        int index = mappedtasks.indexOf(_task);
        if (index == -1) {
            return null;
        }

        return onnodes.get(index);
    }

    public boolean isAUsedHwNode(HwNode _node) {
        return (onnodes.contains(_node));
    }

    public boolean isTaskMappedOn(TMLTask _task, HwNode _node) {
        for(int i=0; i<onnodes.size(); i++) {
            if (onnodes.get(i) == _node) {
                if (mappedtasks.get(i) == _task) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCommNodeMappedOn(TMLElement _channel, HwCommunicationNode _node) {
        for(int i=0; i<oncommnodes.size(); i++) {
            if ((_node==null || oncommnodes.get(i) == _node) && mappedcommelts.get(i) == _channel) return true;
        }
        return false;
    }

    public boolean oneTaskMappedOn(TMLRequest _request, HwNode _node) {
        TMLTask task;
        ListIterator iterator = _request.getOriginTasks().listIterator();
        while(iterator.hasNext()) {
            task = (TMLTask)(iterator.next());
            if (isTaskMappedOn(task, _node)) {
                return true;
            }
        }
        return false;
    }

    public HwBus getFirstHwBusOf(TMLElement _tmle) {
        int index = mappedcommelts.indexOf(_tmle);
        if (index == -1) {
            return null;
        }

        index = 0;
        for(HwCommunicationNode node: oncommnodes) {
            if (node instanceof HwBus) {
                if (mappedcommelts.get(index) == _tmle) {
                    return (HwBus)node;
                }
            }
            index ++;
        }

        return null;

    }

    public int getMaxClockRatio() {
        int ret = 1;
        for(HwCommunicationNode node: oncommnodes) {
            ret = Math.max(node.clockRatio, ret);
        }

        for(HwExecutionNode node1: onnodes) {
            ret = Math.max(node1.clockRatio, ret);
        }

        return ret;
    }

    public ArrayList<TMLError> optimize() {
        ArrayList<TMLError> list = new ArrayList<TMLError>();
        if (!optimized) {
            optimized = true;
            list.addAll(tmlm.optimize());
        }
        return list;
    }

    public LinkedList<HwCommunicationNode> findNodesForElement(TMLElement _elementToFind){
        LinkedList<HwCommunicationNode> list = new LinkedList<HwCommunicationNode>();
        int index=0;
        for(TMLElement tmlelem: mappedcommelts) {
            if (tmlelem == _elementToFind) list.add(oncommnodes.get(index));
            index++;
        }
        return list;
    }

    public HwNode getHwNodeByTask(TMLTask cmpTask){
        int i=0;
        for(TMLTask task: mappedtasks) {
            if (task==cmpTask) break; else i++;
        }
        return onnodes.get(i);
    }

    public void removeAllRandomSequences() {
        if (tmlm != null) {
            tmlm.removeAllRandomSequences();
        }
    }

    public ArrayList<String> getSummaryTaskMapping() {
        StringBuffer sb = new StringBuffer("");
        ArrayList<String> list = new ArrayList<String>();
        int cpt = 0;
        int found = 0;

        for (HwNode node: tmla.getHwNodes()) {
            if (node instanceof HwCPU) {
                sb.append(node.getName() + "(");
                found = 0;
                cpt = 0;
                for(HwExecutionNode ex: onnodes) {
                    if (ex == node) {
                        if (found > 0) {
                            sb.append(", ");
                        }
                        found = 1;
                        sb.append(mappedtasks.get(cpt).getName());
                    }
                    cpt ++;
                }
                sb.append(") ");
                list.add( sb.toString() );
                sb = new StringBuffer("");
            }
        }

        return list;

    }

    public ArrayList<String> getSummaryCPMapping() {
        ArrayList<String> list = new ArrayList<String>();
        return list;
    }

    public ArrayList<String> getSummaryCommMapping() {
        StringBuffer sb = new StringBuffer("");
        ArrayList<String> list = new ArrayList<String>();
        int cpt = 0;
        int found = 0;

        for (HwNode node: tmla.getHwNodes()) {
            if (node instanceof HwCommunicationNode) {
                sb.append(node.getName() + "(");
                found = 0;
                cpt = 0;
                for(HwCommunicationNode ex: oncommnodes) {
                    if (ex == node) {
                        if (found > 0) {
                            sb.append(", ");
                        }
                        found = 1;
                        sb.append(mappedcommelts.get(cpt).getName());
                    }
                    cpt ++;
                }
                sb.append(") ");
                list.add( sb.toString() );
                sb = new StringBuffer("");
            }
        }

        return list;
    }

    public TMLMapping cloneMappingArchitecture() {
        return null;
    }

    public int getArchitectureComplexity() {
        if (tmla == null) {
            return 0;
        }

        return tmla.getArchitectureComplexity();
    }



    public void removeForksAndJoins() {
	if (tmlm != null) {
	    tmlm.removeForksAndJoins();
	}

	// We map the forked tasks to their origin node,and the join to their destination node
    }

    public void handleCPs() {
	// Remove the CPLib with new tasks, channels, HW components
	

	handleCPDMA();

	// Handle ports of forks / joins not mapped on local memories
	// 

	mappedCPLibs = new ArrayList<TMLCPLib>();
	
	
    }

    private void handleCPDMA() {
	TraceManager.addDev("\n\n**** HANDLING CPs:");

	for(TMLCPLib cp: mappedCPLibs) {
	    //TraceManager.addDev(" Found cp:" + cp.getName() + " ref=" + cp.getTypeName());
	    if (cp.isDMATransfer()) {
		TraceManager.addDev(" Found cp store:" + cp.getName() + "::" + cp.getTypeName());
		handleCPDMA(cp);
	    }
	}
    }

    private void handleCPDMA(TMLCPLib _cp) {
	for(TMLCPLibArtifact arti: _cp.getArtifacts()) {
	    handleCPDMAArtifact(_cp, arti);
	}
    }

    private void handleCPDMAArtifact(TMLCPLib _cp, TMLCPLibArtifact _arti) {
	// Find all the channel with the artifact
	TMLChannel chan = tmlm.getChannelByDestinationPortName(_arti.portName);
	if (chan == null) {
	    TraceManager.addDev("DMA_transfer/ Unknown channel with in port=" + _arti.portName);
	    return;
	}

	TraceManager.addDev("DMA_transfer/ Found channel=" + chan);

	if (chan.getNbOfDestinationPorts() > 1) {
	    TraceManager.addDev("DMA_transfer/ Channel has too many ports (must have only one)");
	}

	if (!(chan.isBasicChannel())) {
	    TraceManager.addDev("DMA_transfer/ Only basic channel is accepted");
	}

	String DMAController = _cp.getUnitByName("DMA_Controller_1");
	
	if (DMAController == null) {
	    TraceManager.addDev("DMA_transfer/ Unknown DMA controller in CP");
	}

	TraceManager.addDev("DMA controller=|" + DMAController + "|");
	HwExecutionNode node = getHwExecutionNodeByName(DMAController);
	if (node == null) {
	    TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + DMAController);
	}
	

	// At each origin: We write in a new local channel in a NBRNBW fashion
	// This new channel is mapped on Src_Storage_Instance_1
	// Then, we send an event to a new DMA task mapped
	// The DMa task read elements from the src mem and writes on the destination mem.
	
	// -> The old channel is thus transformed into two new channels


	// New DMATask
	TMLTask dmaTask = new TMLTask("DMATask__" + chan.getName(), chan, null);	
	tmlm.addTask(dmaTask);
	TMLChannel fromOriginToDMA = new TMLChannel("toDMATask__" + chan.getName(), chan);
	tmlm.addChannel(fromOriginToDMA);
	TMLPort portInDMA = new TMLPort("portToDMATask__" + chan.getName(), chan);
	TMLPort portOutDMA = new TMLPort("portfromDMATask__" + chan.getName(), chan);
	
	TMLTask origin = chan.getOriginTask();
	TMLTask destination = chan.getDestinationTask();
	fromOriginToDMA.setTasks(origin, dmaTask);
	fromOriginToDMA.setPorts(chan.getOriginPort(), portInDMA);

	chan.setPorts(portOutDMA, chan.getDestinationPort());

	// In the origin task, we change all writing to "chan" to "fromOriginToDMA"
	origin.replaceWriteChannelWith(chan, fromOriginToDMA);
	TMLEvent toDMA = new TMLEvent("toDMA" +  chan.getName(), chan, 1, false);
	tmlm.addEvent(toDMA);	
	toDMA.addParam(new TMLType(TMLType.NATURAL));
	toDMA.setTasks(origin, dmaTask);
	origin.addSendEventAfterWriteIn(fromOriginToDMA, toDMA, "size");
	

	// We need to create the activity diagram of DMATask
	// We wait for the wait event. Then, we read/write one by one until we have read size
	TMLActivity activity = dmaTask.getActivityDiagram();
	TMLStartState start = new TMLStartState("startOfDMA", null);
	activity.setFirst(start);
	TMLStopState mainStop = new TMLStopState("mainStopOfDMA", null);
	activity.addElement(mainStop);
	TMLStopState stop = new TMLStopState("stopOfDMA", null);
	activity.addElement(stop);
	TMLStopState stopWrite = new TMLStopState("stopOfWrite", null);
	activity.addElement(stopWrite);
	TMLWaitEvent wait = new TMLWaitEvent("waitEvtInDMA", null);
	wait.setEvent(toDMA);
	wait.addParam("size");
	TMLForLoop mainLoop = new TMLForLoop("mainLoopOfDMA", null);
	mainLoop.setInit("i=0");
	mainLoop.setCondition("i==1");
	mainLoop.setIncrement("i=i");
	activity.addElement(mainLoop);
	TMLForLoop loop = new TMLForLoop("loopOfDMA", null);
	loop.setInit("j=size");
	loop.setCondition("j==0");
	loop.setIncrement("j = j-1");
	activity.addElement(loop);
	TMLAttribute attri = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
	dmaTask.addAttribute(attri);
	TMLAttribute attrj = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
	dmaTask.addAttribute(attrj);
	TMLAttribute attrsize = new TMLAttribute("size", "size", new TMLType(TMLType.NATURAL), "0");
	dmaTask.addAttribute(attrsize);
	
        TMLWriteChannel write = new TMLWriteChannel("WriteOfDMA", null);
	activity.addElement(write);
        write.addChannel(chan);
        write.setNbOfSamples("1");
	TMLReadChannel read = new TMLReadChannel("ReadOfDMA", null);
	read.addChannel(fromOriginToDMA);
	read.setNbOfSamples("1");
	activity.addElement(read);

	activity.setFirst(start);
	start.addNext(mainLoop);
	mainLoop.addNext(wait);
	mainLoop.addNext(mainStop);
	wait.addNext(loop);
	loop.addNext(read);
	loop.addNext(stop);
	read.addNext(write);
	write.addNext(stopWrite);
	
 	// All mapping to be done
	// Map DMA task to the DMA nod eof the CPLib
	addTaskToHwExecutionNode(dmaTask, node);
       

	
				      

    }

    public String getMappedTasksString() {
	String tasks = "";
	for(TMLTask task: mappedtasks) {
	    tasks += task.getName() + " ";
	}
	return tasks;
    }

}

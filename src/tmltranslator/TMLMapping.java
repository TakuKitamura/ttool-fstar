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
import tmltranslator.toproverif.*;
import ui.*;

public class TMLMapping {

    private TMLModeling tmlm;
    private TMLArchitecture tmla;
    private TMLCP tmlcp;
public List<String> securityPatterns = new ArrayList<String>();

    private ArrayList<HwExecutionNode> onnodes;
    private ArrayList<TMLTask> mappedtasks;
    private ArrayList<HwCommunicationNode> oncommnodes;
    private ArrayList<TMLElement> mappedcommelts;

    private ArrayList<TMLCP> mappedCPs;
    private ArrayList<TMLElement> commEltsMappedOnCPs;

    private ArrayList<TMLCPLib> mappedCPLibs;

    private ArrayList<String[]> pragmas= new ArrayList<String[]>();
    private boolean optimized = false;

    private int hashCode;
    private boolean hashCodeComputed = false;
    private TMLComponentDesignPanel tmldp;
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

    public void translate2ProVerif(){
	TML2ProVerif spec = new TML2ProVerif(this);
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

    public void addPragma(String[] s){
	pragmas.add(s);
    }

    public ArrayList<String[]> getPragmas(){
	return pragmas;
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

    public ArrayList<TMLCPLib> getMappedTMLCPLibs()     {
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

    public void removeCommMapping(TMLElement _elt) {
        int index;
        while((index = mappedcommelts.indexOf(_elt)) > -1) {
            oncommnodes.remove(index);
            mappedcommelts.remove(index);
        }
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
	TraceManager.addDev("\n\nRemove fork and join in MAPPING");
        if (tmlm != null) {
            tmlm.removeForksAndJoins();
        }

        TMLChannel chan;

        // We map the forked tasks to their origin node, and the join ones to their destination node
        for(TMLTask task: tmlm.getTasks()) {
            if (task.getName().startsWith("FORKTASK__")) {
                if (!isTaskMapped(task)) {
		    TraceManager.addDev("\n\nFORKTASK is NOT mapped: " + task.getName());
                    // We need to map this fork task to the origin node
                    chan = tmlm.getChannelToMe(task);
                    if (chan != null) {
                        TMLTask origin = chan.getOriginTask();
                        if ((origin != null) && (isTaskMapped(origin))) {
                            HwExecutionNode node = (HwExecutionNode)(getHwNodeOf(origin));
                            if (node != null) {
                                TraceManager.addDev("\n\nMapping fork task " + task.getName() + " to " + node.getName());
                                addTaskToHwExecutionNode(task, node);
                            }
                        }
                    }
                } else {
		    TraceManager.addDev("\n\nFORKTASK is  mapped: " + task.getName());
		}
            } else {
		TraceManager.addDev("Non fork task found: " + task.getName());
	    }
            if (task.getName().startsWith("JOINTASK__")) {
                if (!isTaskMapped(task)) {
                    // We need to map this join task to the destination node
                    chan = tmlm.getChannelFromMe(task);
                    if (chan != null) {
                        TMLTask destination = chan.getDestinationTask();
                        if ((destination != null) && (isTaskMapped(destination))) {
                            HwExecutionNode node = (HwExecutionNode)(getHwNodeOf(destination));
                            if (node != null) {
                                TraceManager.addDev("\n\nMapping join task " + task.getName() + " to " + node.getName());
                                addTaskToHwExecutionNode(task, node);
                            }
                        }
                    }
                }
            }
        }


    }

    public void handleCPs() {
        // Remove the CPLib with new tasks, channels, HW components
        TraceManager.addDev("\n\n**** HANDLING CPs:");

        for(TMLCPLib cp: mappedCPLibs) {
            //TraceManager.addDev(" Found cp:" + cp.getName() + " ref=" + cp.getTypeName());
            if (cp.isDMATransfer()) {
                TraceManager.addDev(" Found cp DMA:" + cp.getName() + "::" + cp.getTypeName());
                handleCPDMA(cp);
            }
            if (cp.isDoubleDMATransfer()) {
                TraceManager.addDev(" Found cp Double DMA:" + cp.getName() + "::" + cp.getTypeName());
                handleCPDoubleDMA(cp);
            }
            if (cp.isMemoryCopy()) {
                TraceManager.addDev(" Found cp Memory Copy:" + cp.getName() + "::" + cp.getTypeName());
                handleCPMemoryCopy(cp);
            }
        }

        // Remove CPs
        mappedCPLibs = new ArrayList<TMLCPLib>();


    }


    private void handleCPDMA(TMLCPLib _cp) {
        for(TMLCPLibArtifact arti: _cp.getArtifacts()) {
            handleCPDMAArtifact(_cp, arti);
        }
    }

    private void handleCPDoubleDMA(TMLCPLib _cp) {
        TraceManager.addDev(" Found double DMA cp:" + _cp.getName() + " ref=" + _cp.getTypeName());
        for(TMLCPLibArtifact arti: _cp.getArtifacts()) {
            handleCPDoubleDMAArtifact(_cp, arti);
        }
    }

    private void handleCPMemoryCopy(TMLCPLib _cp) {
        for(TMLCPLibArtifact arti: _cp.getArtifacts()) {
            handleCPMemoryCopyArtifact(_cp, arti);
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
            return;
        }

        if (!(chan.isBasicChannel())) {
            TraceManager.addDev("DMA_transfer/ Only basic channel is accepted");
            return;
        }

        String DMAController = _cp.getUnitByName("DMA_Controller_1");

        if (DMAController == null) {
            TraceManager.addDev("DMA_transfer/ Unknown DMA controller in CP");
            return;
        }

        TraceManager.addDev("DMA controller=|" + DMAController + "|");
        HwExecutionNode node = getHwExecutionNodeByName(DMAController);
        if (node == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + DMAController);
            return;
        }

        // SRC MEM
        String SrcStorageInstance = _cp.getUnitByName("Src_Storage_Instance_1");
        if (SrcStorageInstance == null) {
            TraceManager.addDev("DMA_transfer/ Unknown SrcStorageInstance in CP");
            return;
        }
        HwMemory mem1 = tmla.getHwMemoryByName(SrcStorageInstance);
        if (mem1 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + SrcStorageInstance);
            return;
        }

        // DST MEM
        String DstStorageInstance = _cp.getUnitByName("Dst_Storage_Instance_1");
        if (DstStorageInstance == null) {
            TraceManager.addDev("DMA_transfer/ Unknown DstStorageInstance in CP");
            return;
        }
        HwMemory mem2 = tmla.getHwMemoryByName(DstStorageInstance);
        if (mem2 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + DstStorageInstance);
            return;
        }




        // At each origin: We write in a new local channel in a NBRNBW fashion
        // This new channel is mapped on Src_Storage_Instance_1
        // Then, we send an event to a new DMA task mapped
        // The DMA task reads elements from the src mem and writes on the destination mem.

        // -> The old channel is thus transformed into two new channels

        // The current chan is unmapped, and mapped to the destination memory
        removeCommMapping(chan);
        addCommToHwCommNode(chan, mem2);

        // New DMATask
        TMLTask dmaTask = new TMLTask("DMATask__" + chan.getName(), chan, null);
        tmlm.addTask(dmaTask);
        TMLChannel fromOriginToDMA = new TMLChannel("toDMATask__" + chan.getName(), chan);
        addCommToHwCommNode(fromOriginToDMA, mem1);
        fromOriginToDMA.setType(TMLChannel.NBRNBW);
        fromOriginToDMA.setSize(chan.getSize());
        tmlm.addChannel(fromOriginToDMA);
        TMLPort portInDMA = new TMLPort("portToDMATask__" + chan.getName(), chan);
        TMLPort portOutDMA = new TMLPort("portfromDMATask__" + chan.getName(), chan);

        TMLTask origin = chan.getOriginTask();
        TMLTask destination = chan.getDestinationTask();
        fromOriginToDMA.setTasks(origin, dmaTask);
        fromOriginToDMA.setPorts(chan.getOriginPort(), portInDMA);

        chan.setPorts(portOutDMA, chan.getDestinationPort());
        chan.setTasks(dmaTask, chan.getDestinationTask());

        // In the origin task, we change all writing to "chan" to "fromOriginToDMA"
        origin.replaceWriteChannelWith(chan, fromOriginToDMA);
        TMLEvent toDMA = new TMLEvent("toDMA" +  chan.getName(), chan, 1, false);
	TMLEvent fromDMA = new TMLEvent("fromDMA" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(toDMA);
	tmlm.addEvent(fromDMA);
        toDMA.addParam(new TMLType(TMLType.NATURAL));
        toDMA.setTasks(origin, dmaTask);
	fromDMA.setTasks(dmaTask, origin);
        //origin.addSendEventAfterWriteIn(fromOriginToDMA, toDMA, "size");
	origin.addSendAndReceiveEventAfterWriteIn(fromOriginToDMA, toDMA, fromDMA, "size", "");


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
        activity.addElement(wait);
	TMLSendEvent done = new TMLSendEvent("DMATransferComplete", null);
        done.setEvent(fromDMA);
        activity.addElement(done);
        TMLForLoop mainLoop = new TMLForLoop("mainLoopOfDMA", null);
        mainLoop.setInit("i=0");
        mainLoop.setCondition("i==0");
        mainLoop.setIncrement("i=i");
        activity.addElement(mainLoop);
        TMLForLoop loop = new TMLForLoop("loopOfDMA", null);
        loop.setInit("j=size");
        loop.setCondition("j>0");
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
        loop.addNext(done);
	done.addNext(stop);
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

    private void handleCPDoubleDMAArtifact(TMLCPLib _cp, TMLCPLibArtifact _arti) {
        // Find all the channel with the artifact
        TMLChannel chan = tmlm.getChannelByDestinationPortName(_arti.portName);
        if (chan == null) {
            TraceManager.addDev("Double_DMA_transfer/ Unknown channel with in port=" + _arti.portName);
            return;
        }

        TraceManager.addDev("Double_DMA_transfer/ Found channel=" + chan);

        if (chan.getNbOfDestinationPorts() > 1) {
            TraceManager.addDev("Double_DMA_transfer/ Channel has too many destination ports (must have only one)");
            return;
        }

        if (chan.isBasicChannel()) {
            TraceManager.addDev("Double_DMA_transfer/ Only join channel is accepted");
            return;
        }

        if (!(chan.isAJoinChannel(2))) {
            TraceManager.addDev("Double_DMA_transfer/ Only join channel with 2 origins is accepted");
            return;
        }

        String DMAController1 = _cp.getUnitByName("DMA_Controller_1");
        String DMAController2 = _cp.getUnitByName("DMA_Controller_2");

        if (DMAController1 == null) {
            TraceManager.addDev("Double_DMA_transfer/ Unknown DMA controller1 in CP");
            return;
        }

        if (DMAController2 == null) {
            TraceManager.addDev("Double_DMA_transfer/ Unknown DMA controller2 in CP");
            return;
        }

        HwExecutionNode node1 = getHwExecutionNodeByName(DMAController1);
        if (node1 == null) {
            TraceManager.addDev("Double_DMA_transfer/ Unknown Hw Execution Node1: " + DMAController1);
        }

        HwExecutionNode node2 = getHwExecutionNodeByName(DMAController2);
        if (node1 == null) {
            TraceManager.addDev("Double_DMA_transfer/ Unknown Hw Execution Node2: " + DMAController2);
        }

        // SRC MEM
        String SrcStorageInstance1 = _cp.getUnitByName("Src_Storage_Instance_1");
        if (SrcStorageInstance1 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown SrcStorageInstance1 in CP");
            return;
        }
        HwMemory mem11 = tmla.getHwMemoryByName(SrcStorageInstance1);
        if (mem11 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + SrcStorageInstance1);
            return;
        }

        String SrcStorageInstance2 = _cp.getUnitByName("Src_Storage_Instance_2");
        if (SrcStorageInstance2 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown SrcStorageInstance2 in CP");
            return;
        }
        HwMemory mem12 = tmla.getHwMemoryByName(SrcStorageInstance2);
        if (mem12 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + SrcStorageInstance2);
            return;
        }

        // DST MEM
        String DstStorageInstance1 = _cp.getUnitByName("Dst_Storage_Instance_1");
        if (DstStorageInstance1 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown DstStorageInstance1 in CP");
            return;
        }
        HwMemory mem21 = tmla.getHwMemoryByName(DstStorageInstance1);
        if (mem21 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + DstStorageInstance1);
            return;
        }

        String DstStorageInstance2 = _cp.getUnitByName("Dst_Storage_Instance_2");
        if (DstStorageInstance2 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown DstStorageInstance2 in CP");
            return;
        }
        HwMemory mem22 = tmla.getHwMemoryByName(DstStorageInstance2);
        if (mem22 == null) {
            TraceManager.addDev("DMA_transfer/ Unknown Hw Execution Node: " + DstStorageInstance2);
            return;
        }

        // The current chan is unmapped, and mapped to the destination memory
        removeCommMapping(chan);
        addCommToHwCommNode(chan, mem22);

        // For each DMA transfer: we make one task.
        // An event is sent from the origin task to the DMA task to inform about the fact to make the DMA transfer
        // Also, DMA tasks communicate by event to inform whether they should make a transfer or not

        // New DMATask1
        TMLTask dmaTask1 = new TMLTask("DMATask1__" + chan.getName(), chan, null);
        TMLTask dmaTask2 = new TMLTask("DMATask2__" + chan.getName(), chan, null);
        tmlm.addTask(dmaTask1);
        TMLChannel fromOriginToDMA1 = new TMLChannel("toDMATask1__" + chan.getName(), chan);
        addCommToHwCommNode(fromOriginToDMA1, mem11);
        fromOriginToDMA1.setType(TMLChannel.NBRNBW);
        fromOriginToDMA1.setSize(chan.getSize());
        tmlm.addChannel(fromOriginToDMA1);
        TMLChannel fromDMA1ToDestination = new TMLChannel("fromDMATask1__" + chan.getName(), chan);
        addCommToHwCommNode(fromDMA1ToDestination, mem21);
        fromDMA1ToDestination.setType(TMLChannel.NBRNBW);
        fromDMA1ToDestination.setSize(chan.getSize());
        tmlm.addChannel(fromDMA1ToDestination);
        TMLPort portInDMA1 = new TMLPort("portToDMATask1__" + chan.getName(), chan);
        TMLPort portOutDMA1 = new TMLPort("portfromDMATask1__" + chan.getName(), chan);
        TMLPort portIn1DestinationTask = new TMLPort("portfromDMATask1__" + chan.getName(), chan);

        TMLTask origin1 = chan.getOriginTask(0);
        TMLTask destination1 = chan.getDestinationTask(0);
        fromOriginToDMA1.setTasks(origin1, dmaTask1);
        fromOriginToDMA1.setPorts(chan.getOriginPort(0), portInDMA1);
        fromDMA1ToDestination.setTasks(dmaTask1, destination1);
        fromDMA1ToDestination.setPorts(portOutDMA1, portIn1DestinationTask);


        // In the origin task, we change all writing to "chan" to "fromOriginToDMA"
        origin1.replaceWriteChannelWith(chan, fromOriginToDMA1);
        TMLEvent toDMA1 = new TMLEvent("toDMA1" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(toDMA1);
        toDMA1.addParam(new TMLType(TMLType.NATURAL));
        toDMA1.setTasks(origin1, dmaTask1);
        origin1.addSendEventAfterWriteIn(fromOriginToDMA1, toDMA1, "size");

        // We need two events between DMATasks
        TMLEvent interdma1 = new TMLEvent("fromDMA1ToDMA2" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(interdma1);
        interdma1.setTasks(dmaTask1, dmaTask2);
        TMLEvent interdma2 = new TMLEvent("fromDMA2ToDMA1" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(interdma2);
        interdma2.setTasks(dmaTask1, dmaTask2);


        // We need to create the activity diagram of DMATask
        // We wait for the wait event. Then, we read/write one by one until we have read size
        TMLActivity activity1 = dmaTask1.getActivityDiagram();
        TMLStartState start1 = new TMLStartState("startOfDMA1", null);
        activity1.setFirst(start1);
        TMLStopState mainStop1 = new TMLStopState("mainStopOfDMA1", null);
        activity1.addElement(mainStop1);
        TMLStopState stop1 = new TMLStopState("stopOfDMA1", null);
        activity1.addElement(stop1);
        TMLStopState stopWrite1 = new TMLStopState("stopOfWrite1", null);
        activity1.addElement(stopWrite1);
        TMLWaitEvent wait1 = new TMLWaitEvent("waitEvtInDMA1", null);
        wait1.setEvent(toDMA1);
        wait1.addParam("size");
        activity1.addElement(wait1);
        TMLForLoop mainLoop1 = new TMLForLoop("mainLoopOfDMA1", null);
        mainLoop1.setInit("i=0");
        mainLoop1.setCondition("i==0");
        mainLoop1.setIncrement("i=i");
        activity1.addElement(mainLoop1);
        TMLForLoop loop1 = new TMLForLoop("loopOfDMA", null);
        loop1.setInit("j=size");
        loop1.setCondition("j>0");
        loop1.setIncrement("j = j-1");
        activity1.addElement(loop1);
        TMLAttribute attri1 = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
        dmaTask1.addAttribute(attri1);
        TMLAttribute attrj1 = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        dmaTask1.addAttribute(attrj1);
        TMLAttribute attrsize1 = new TMLAttribute("size", "size", new TMLType(TMLType.NATURAL), "0");
        dmaTask1.addAttribute(attrsize1);

        TMLWriteChannel write1 = new TMLWriteChannel("WriteOfDMA1", null);
        activity1.addElement(write1);
        write1.addChannel(fromDMA1ToDestination);
        write1.setNbOfSamples("1");
        TMLReadChannel read1 = new TMLReadChannel("ReadOfDMA", null);
        read1.addChannel(fromOriginToDMA1);
        read1.setNbOfSamples("1");
        activity1.addElement(read1);

        TMLWaitEvent waitFromOtherDMA1 = new TMLWaitEvent("waitEvtInDMA1_fromOtherDMA", null);
        waitFromOtherDMA1.setEvent(interdma2);
        activity1.addElement(waitFromOtherDMA1);

        TMLSendEvent notifyOtherDMA1 = new TMLSendEvent("notifyEvtInDMA1_toOtherDMA", null);
        notifyOtherDMA1.setEvent(interdma1);
        activity1.addElement(notifyOtherDMA1);

        activity1.setFirst(start1);
        start1.addNext(mainLoop1);
        mainLoop1.addNext(wait1);
        mainLoop1.addNext(mainStop1);
        wait1.addNext(loop1);
        loop1.addNext(read1);
        loop1.addNext(notifyOtherDMA1);
        notifyOtherDMA1.addNext(waitFromOtherDMA1);
        waitFromOtherDMA1.addNext(stop1);
        read1.addNext(write1);
        write1.addNext(stopWrite1);

        // New DMATask2

        tmlm.addTask(dmaTask2);
        TMLChannel fromOriginToDMA2 = new TMLChannel("toDMATask2__" + chan.getName(), chan);
        addCommToHwCommNode(fromOriginToDMA2, mem21);
        fromOriginToDMA2.setType(TMLChannel.NBRNBW);
        fromOriginToDMA2.setSize(chan.getSize());
        tmlm.addChannel(fromOriginToDMA2);
        TMLChannel fromDMA2ToDestination = chan;
        /*= new TMLChannel("fromDMATask2__" + chan.getName(), chan);
          tmlm.addChannel(fromDMA2ToDestination);*/

        TMLPort portInDMA2 = new TMLPort("portToDMATask2__" + chan.getName(), chan);
        TMLPort portOutDMA2 = new TMLPort("portfromDMATask2__" + chan.getName(), chan);
        TMLPort portIn2DestinationTask = new TMLPort("portfromDMATask2__" + chan.getName(), chan);

        TMLTask origin2 = chan.getOriginTask(1);
        fromOriginToDMA2.setTasks(origin2, dmaTask2);
        fromOriginToDMA2.setPorts(chan.getOriginPort(1), portInDMA2);
        fromDMA2ToDestination.setTasks(dmaTask2, destination1);
        fromDMA2ToDestination.setPorts(portOutDMA2, portIn2DestinationTask);


        // In the origin task, we change all writing to "chan" to "fromOriginToDMA"
        origin2.replaceWriteChannelWith(chan, fromOriginToDMA2);
        TMLEvent toDMA2 = new TMLEvent("toDMA2" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(toDMA2);
        toDMA2.addParam(new TMLType(TMLType.NATURAL));
        toDMA2.setTasks(origin2, dmaTask2);
        origin2.addSendEventAfterWriteIn(fromOriginToDMA2, toDMA2, "size");


        // We need to create the activity diagram of DMATask2
        // We wait for the wait event. Then, we read/write one by one until we have read size
        TMLActivity activity2 = dmaTask2.getActivityDiagram();
        TMLStartState start2 = new TMLStartState("startOfDMA2", null);
        activity2.setFirst(start2);
        TMLStopState mainStop2 = new TMLStopState("mainStopOfDMA2", null);
        activity2.addElement(mainStop2);
        TMLStopState stop2 = new TMLStopState("stopOfDMA2", null);
        activity2.addElement(stop2);
        TMLStopState stopWrite2 = new TMLStopState("stopOfWrite2", null);
        activity2.addElement(stopWrite2);
        TMLWaitEvent wait2 = new TMLWaitEvent("waitEvtInDMA2", null);
        wait2.setEvent(toDMA2);
        wait2.addParam("size");
        activity2.addElement(wait2);
        TMLSendEvent notifyOtherDMA2 = new TMLSendEvent("notifyEvtInDMA2_toOtherDMA", null);
        notifyOtherDMA2.setEvent(interdma2);
        activity2.addElement(notifyOtherDMA2);
        TMLWaitEvent waitFromOtherDMA2 = new TMLWaitEvent("waitEvtInDMA2_fromOtherDMA", null);
        waitFromOtherDMA2.setEvent(interdma1);
        activity2.addElement(waitFromOtherDMA2);
        TMLForLoop mainLoop2 = new TMLForLoop("mainLoopOfDMA2", null);
        mainLoop2.setInit("i=0");
        mainLoop2.setCondition("i==0");
        mainLoop2.setIncrement("i=i");
        activity2.addElement(mainLoop2);
        TMLForLoop loop2 = new TMLForLoop("loopOfDMA", null);
        loop2.setInit("j=size");
        loop2.setCondition("j>0");
        loop2.setIncrement("j = j-1");
        activity2.addElement(loop2);
        TMLAttribute attri2 = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
        dmaTask2.addAttribute(attri2);
        TMLAttribute attrj2 = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        dmaTask2.addAttribute(attrj2);
        TMLAttribute attrsize2 = new TMLAttribute("size", "size", new TMLType(TMLType.NATURAL), "0");
        dmaTask2.addAttribute(attrsize2);

        TMLWriteChannel write2 = new TMLWriteChannel("WriteOfDMA2", null);
        activity2.addElement(write2);
        write2.addChannel(fromDMA2ToDestination);
        write2.setNbOfSamples("1");
        TMLReadChannel read2 = new TMLReadChannel("ReadOfDMA2", null);
        read2.addChannel(fromOriginToDMA2);
        read2.setNbOfSamples("1");
        activity2.addElement(read2);

        activity2.setFirst(start2);
        start2.addNext(mainLoop2);
        mainLoop2.addNext(wait2);
        mainLoop2.addNext(mainStop2);
        wait2.addNext(waitFromOtherDMA2);
        waitFromOtherDMA2.addNext(loop2);
        loop2.addNext(read2);
        loop2.addNext(notifyOtherDMA2);
        notifyOtherDMA2.addNext(stop2);
        read2.addNext(write2);
        write2.addNext(stopWrite2);

        // All mapping to be done
        // Map DMA task to the DMA nod eof the CPLib
        addTaskToHwExecutionNode(dmaTask1, node1);
        addTaskToHwExecutionNode(dmaTask2, node2);

        // Remove olf channel from TMLModeling
        //tmlm.removeChannel(chan);
        chan.removeComplexInformation();
    }

    private void handleCPMemoryCopyArtifact(TMLCPLib _cp, TMLCPLibArtifact _arti) {
        // Find all the channel with the artifact
        TMLChannel chan = tmlm.getChannelByDestinationPortName(_arti.portName);
        if (chan == null) {
            TraceManager.addDev("MemCPY/ Unknown channel with in port=" + _arti.portName);
            return;
        }

        TraceManager.addDev("MemCPY/ Found channel=" + chan);

        if (chan.getNbOfDestinationPorts() > 1) {
            TraceManager.addDev("MemCPY/ Channel has too many ports (must have only one)");
            return;
        }

        if (!(chan.isBasicChannel())) {
            TraceManager.addDev("MemCPY/ Only basic channel is accepted");
            return;
        }

        // CPU
        String CPUController = _cp.getUnitByName("CPU_Controller");
        if (CPUController == null) {
            TraceManager.addDev("MemCPY/ Unknown CPU controller in CP");
            return;
        }
        TraceManager.addDev("CPU controller=|" + CPUController + "|");
        HwExecutionNode node = getHwExecutionNodeByName(CPUController);
        if (node == null) {
            TraceManager.addDev("MemCPY/ Unknown Hw Execution Node: " + CPUController);
            return;
        }

        // SRC MEM
        String SrcStorageInstance = _cp.getUnitByName("Src_Storage_Instance");
        if (SrcStorageInstance == null) {
            TraceManager.addDev("MemCPY/ Unknown SrcStorageInstance in CP");
            return;
        }
        HwMemory mem1 = tmla.getHwMemoryByName(SrcStorageInstance);
        if (mem1 == null) {
            TraceManager.addDev("MemCPY/ Unknown Hw Execution Node: " + SrcStorageInstance);
            return;
        }

        // DST MEM
        String DstStorageInstance = _cp.getUnitByName("Dst_Storage_Instance");
        if (DstStorageInstance == null) {
            TraceManager.addDev("MemCPY/ Unknown DstStorageInstance in CP");
            return;
        }
        HwMemory mem2 = tmla.getHwMemoryByName(DstStorageInstance);
        if (mem2 == null) {
            TraceManager.addDev("MemCPY/ Unknown Hw Execution Node: " + DstStorageInstance);
            return;
        }

        // The current chan is unmapped, and mapped to the destination memory
        removeCommMapping(chan);
        addCommToHwCommNode(chan, mem2);


        // We create a new Task mapped on CPUController, with a new channel
        TMLTask origin = chan.getOriginTask();
        TMLTask ctrl = new TMLTask("MemCpyController__" + chan.getName(), chan, null);
        tmlm.addTask(ctrl);
        addTaskToHwExecutionNode(ctrl, node);
        TMLChannel fromOriginToCTRL = new TMLChannel("toCTRL__" + chan.getName(), chan);
        addCommToHwCommNode(fromOriginToCTRL, mem1);
        fromOriginToCTRL.setType(TMLChannel.NBRNBW);
        fromOriginToCTRL.setSize(chan.getSize());
        fromOriginToCTRL.setTasks(chan.getOriginTask(), ctrl);
        tmlm.addChannel(fromOriginToCTRL);

        // Reworking chan
        chan.setTasks(ctrl, chan.getDestinationTask());

        // Reworking origin task
        origin.replaceWriteChannelWith(chan, fromOriginToCTRL);
        TMLEvent toCTRL = new TMLEvent("toCTRL__" +  chan.getName(), chan, 1, false);
        tmlm.addEvent(toCTRL);
        toCTRL.addParam(new TMLType(TMLType.NATURAL));
        toCTRL.setTasks(origin, ctrl);
        origin.addSendEventAfterWriteIn(fromOriginToCTRL, toCTRL, "size");

        // We need to create the CTRL task-> infinite loop, waiting for the origin signal, and then making the mem cpy
        TMLActivity activity = ctrl.getActivityDiagram();
        TMLStartState start = new TMLStartState("startOfCTRL", null);
        activity.setFirst(start);
        TMLStopState mainStop = new TMLStopState("mainStopOfCTRL", null);
        activity.addElement(mainStop);
        TMLStopState stop = new TMLStopState("stopOfCTRL", null);
        activity.addElement(stop);
        TMLStopState stopWrite = new TMLStopState("stopOfWrite", null);
        activity.addElement(stopWrite);
        TMLWaitEvent wait = new TMLWaitEvent("waitEvtInCTRL", null);
        wait.setEvent(toCTRL);
        wait.addParam("size");
        activity.addElement(wait);
        TMLForLoop mainLoop = new TMLForLoop("mainLoopOfCTRL", null);
        mainLoop.setInit("i=0");
        mainLoop.setCondition("i==0");
        mainLoop.setIncrement("i=i");
        activity.addElement(mainLoop);
        TMLForLoop loop = new TMLForLoop("loopOfCTRL", null);
        loop.setInit("j=size");
        loop.setCondition("j>0");
        loop.setIncrement("j = j-1");
        activity.addElement(loop);
        TMLAttribute attri = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
        ctrl.addAttribute(attri);
        TMLAttribute attrj = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        ctrl.addAttribute(attrj);
        TMLAttribute attrsize = new TMLAttribute("size", "size", new TMLType(TMLType.NATURAL), "0");
        ctrl.addAttribute(attrsize);

        TMLWriteChannel write = new TMLWriteChannel("WriteOfCTRL", null);
        activity.addElement(write);
        write.addChannel(chan);
        write.setNbOfSamples("1");
        TMLReadChannel read = new TMLReadChannel("ReadOfCTRL", null);
        read.addChannel(fromOriginToCTRL);
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






    }

    public void linkTasks2TMLChannels() {

        ListIterator iterator;
        if( tmlm != null )      {
            iterator = tmlm.getTasks().listIterator();
            while( iterator.hasNext() ) {
                TMLTask task = (TMLTask)( iterator.next() );
                for( TMLReadChannel readCh: task.getReadChannels() )    {
                    String readChName = readCh.toString().split(": ")[1];
                    for( TMLChannel ch: tmlm.getChannels() )    {
                        if( ch.getName().equals( readChName ) ) {
                            task.addTMLChannel( ch );
                            task.addReadTMLChannel( ch );
                        }
                    }
                }
                for( TMLWriteChannel writeCh: task.getWriteChannels() ) {
                    String writeChName = writeCh.toString().split(": ")[1];
                    for( TMLChannel ch: tmlm.getChannels() )    {
                        if( ch.getName().equals( writeChName ) )        {
                            task.addTMLChannel( ch );
                            task.addWriteTMLChannel( ch );
                        }
                    }
                }
            }
        }
    }

    public void linkTasks2TMLEvents()   {

        ListIterator iterator;
        if( tmlm != null )      {
            iterator = tmlm.getTasks().listIterator();
            while( iterator.hasNext() ) {
                TMLTask task = (TMLTask)( iterator.next() );
                for( TMLSendEvent sendEvt: task.getSendEvents() )       {
                    String sendEvtName = sendEvt.toString().split(":")[1].split("\\(")[0];
                    for( TMLEvent evt: tmlm.getEvents() )       {
                        if( evt.getName().equals( sendEvtName ) )       {
                            task.addTMLEvent( evt );
                        }
                    }
                }
                for( TMLWaitEvent waitEvt: task.getWaitEvents() )       {
                    String waitEvtName = waitEvt.toString().split(":")[1].split("\\(")[0];
                    for( TMLEvent evt: tmlm.getEvents() )       {
                        if( evt.getName().equals( waitEvtName ) )       {
                            task.addTMLEvent( evt );
                        }
                    }
                }
            }
        }
    }
    public void setTMLDesignPanel(TMLComponentDesignPanel _tmldp){
	tmldp = _tmldp;
    }
    public TMLComponentDesignPanel getTMLCDesignPanel(){
	return tmldp;
    }
}       //End of class

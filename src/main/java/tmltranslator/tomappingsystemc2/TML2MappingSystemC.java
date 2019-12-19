/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */


package tmltranslator.tomappingsystemc2;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.*;

import java.util.*;


/**
 * Class TML2MappingSystemC
 * Creation: 03/09/2007
 *
 * @author Daniel Knorreck
 * @version 1.0 03/09/2007
 */
public class TML2MappingSystemC implements IDiploSimulatorCodeGenerator {

    private final static String CR = "\n";
    //  private final static String CR2 = "\n\n";
    private final static String SCCR = ";\n";
    //  private final static String EFCR = "}\n";
    //  private final static String EFCR2 = "}\n\n";
    //  private final static String EF = "}";
    //private final static int MAX_EVENT = 1024;

    private TMLModeling<?> tmlmodeling;
    private TMLMapping<?> tmlmapping;

    private boolean debug;
    private boolean optimize;
    private String header, declaration, mainFile, src;
    private ArrayList<MappedSystemCTask> tasks;

    //private ArrayList<EBRDD> ebrdds;
    //private ArrayList<TEPE> tepes;
    SystemCTEPE tepeTranslator;
    //private ArrayList<SystemCEBRDD> systemCebrdds = new ArrayList<SystemCEBRDD>();

    public TML2MappingSystemC(TMLModeling<?> _tmlm) {
        tmlmodeling = _tmlm;
        tmlmodeling.removeForksAndJoins();
        tmlmapping = tmlmodeling.getDefaultMapping();
        tepeTranslator = new SystemCTEPE(new ArrayList<TEPE>(), this);
    }

    public TML2MappingSystemC(TMLMapping<?> _tmlmapping) {
        tmlmapping = _tmlmapping;
        tmlmapping.handleCPs();
        tmlmapping.removeForksAndJoins();
        tmlmapping.makeMinimumMapping();
        tmlmapping.removeAllRouters();
        tepeTranslator = new SystemCTEPE(new ArrayList<TEPE>(), this);
    }

    public TML2MappingSystemC(TMLModeling<?> _tmlm, List<EBRDD> _ebrdds, List<TEPE> _tepes) {
        tmlmodeling = _tmlm;
        //ebrdds = _ebrdds;
        tmlmapping = tmlmodeling.getDefaultMapping();
        tepeTranslator = new SystemCTEPE(_tepes, this);
        //tepeTranslator.generateTEPEs();
    }

    public TML2MappingSystemC(TMLMapping<?> _tmlmapping, List<EBRDD> _ebrdds, List<TEPE> _tepes) {
        tmlmapping = _tmlmapping;
        tmlmapping.handleCPs();
        tmlmapping.removeForksAndJoins();
        //ebrdds = _ebrdds;
        tmlmapping.makeMinimumMapping();
        tepeTranslator = new SystemCTEPE(_tepes, this);
        //tepeTranslator.generateTEPEs();
    }

    public void setModelName(String _modelName) {
    }

    public void saveFile(String path, String filename) throws FileException {
        generateTaskFiles(path);
        FileUtils.saveFile(path + filename + ".cpp", getFullCode());
        src += filename + ".cpp";
        FileUtils.saveFile(path + "Makefile.src", src);
        //tepeTranslator.saveFile(path + "src_simulator/TEPE/test.h");
    }

    public String getFullCode() {
        return mainFile;
    }

    public String generateSystemC(boolean _debug, boolean _optimize) {
        debug = _debug;
        optimize = _optimize;
        tmlmapping.removeAllRandomSequences();
        tmlmapping.handleCPs();
        tmlmodeling = tmlmapping.getTMLModeling();
        tasks = new ArrayList<MappedSystemCTask>();
        //generateSystemCTasks();
        //generateEBRDDs();
        generateMainFile();
        generateMakefileSrc();

        return null;
    }

    private void generateMainFile() {
        makeHeader();
        makeDeclarations();
        header += tepeTranslator.getEqFuncDeclaration() + "\n";
        mainFile = header + declaration;
        mainFile = Conversion.indentString(mainFile, 4);
    }

    private void generateMakefileSrc() {
        src = "SRCS = ";
        for (TMLTask mst : tmlmapping.getMappedTasks()) {
            src += mst.getName() + ".cpp ";
        }
        //for(EBRDD ebrdd: ebrdds){
        //      src += ebrdd.getName() + ".cpp ";
        //}
    }

    private void makeHeader() {
        // System headers
        header = "#include <Simulator.h>" + CR;
        header += "#include <AliasConstraint.h>\n#include <EqConstraint.h>\n#include <LogConstraint.h>\n#include <PropLabConstraint.h>\n";
        header += "#include <PropRelConstraint.h>\n#include <SeqConstraint.h>\n#include <SignalConstraint.h>\n#include <TimeMMConstraint.h>\n";
        header += "#include <TimeTConstraint.h>\n";
        header += "#include <CPU.h>\n#include <SingleCoreCPU.h>\n#include <MultiCoreCPU.h>\n#include <RRScheduler.h>\n#include <RRPrioScheduler.h>\n#include <PrioScheduler.h>\n#include <Bus.h>\n";
        header += "#include <Bridge.h>\n#include <Memory.h>\n#include <TMLbrbwChannel.h>\n#include <TMLnbrnbwChannel.h>\n";
        header += "#include <TMLbrnbwChannel.h>\n#include <TMLEventBChannel.h>\n#include <TMLEventFChannel.h>\n#include <TMLEventFBChannel.h>\n";
        header += "#include <TMLTransaction.h>\n#include <TMLCommand.h>\n#include <TMLTask.h>\n";
        header += "#include <SimComponents.h>\n#include <Server.h>\n#include <SimServSyncInfo.h>\n#include <ListenersSimCmd.h>\n";

        // Generate tasks header
        for (TMLTask mst : tmlmapping.getMappedTasks()) {
            //header += "#include <" + mst.getReference() + ".h>" + CR;
            header += "#include <" + mst.getName() + ".h>" + CR;
        }
        //for(EBRDD ebrdd: ebrdds){
        //      header += "#include <" + ebrdd.getName() + ".h>" + CR;
        //}
        header += CR;
    }

    private void makeDeclarations() {
        declaration = "class CurrentComponents: public SimComponents{\npublic:\nCurrentComponents():SimComponents(" + tmlmapping.getHashCode() + "){\n";

        // Declaration of HW nodes
        declaration += "//Declaration of CPUs" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwCPU) {
                HwCPU exNode = (HwCPU) node;
                if (exNode.getType().equals("CPURRPB"))
                    declaration += "RRPrioScheduler* " + exNode.getName() + "_scheduler = new RRPrioScheduler(\"" + exNode.getName() + "_PrioSched" + "\",0, " + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime) + ", " + (int) Math.ceil((float) (exNode.clockRatio * Math.max(exNode.execiTime, exNode.execcTime) * (exNode.branchingPredictionPenalty * exNode.pipelineSize + 100 - exNode.branchingPredictionPenalty)) / 100) + " ) " + SCCR;
                else
                    //tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime
                    //declaration += "RRScheduler* " + exNode.getName() + "_scheduler = new RRScheduler(\"" + exNode.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)exNode.execiTime)*(1+((float)exNode.branchingPredictionPenalty)/100)) + " ) " + SCCR;
                    declaration += "RRScheduler* " + exNode.getName() + "_scheduler = new RRScheduler(\"" + exNode.getName() + "_RRSched\", 0, " + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime) + ", " + (int) Math.ceil((float) (exNode.clockRatio * Math.max(exNode.execiTime, exNode.execcTime) * (exNode.branchingPredictionPenalty * exNode.pipelineSize + 100 - exNode.branchingPredictionPenalty)) / 100) + " ) " + SCCR;
                //TraceManager.addDev("cores " + exNode.nbOfCores);
		if (exNode.nbOfCores == 1) {
		    declaration += "CPU* " + exNode.getName() + exNode.nbOfCores + " = new SingleCoreCPU(" + exNode.getID() + ", \"" + exNode.getName() + "_" + exNode.nbOfCores + "\", " 
			+ exNode.getName() + "_scheduler" + ", ";
		    
                    declaration += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", " 
			+ exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", " + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
		} else {
		    declaration += "CPU* " + exNode.getName() + exNode.nbOfCores + " = new MultiCoreCPU(" + exNode.getID() + ", \"" + exNode.getName() + "_" + exNode.nbOfCores + "\", " + exNode.getName() + "_scheduler" + ", ";
		    
                    declaration += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", " + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", " + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
		}
		
		declaration += "addCPU(" + node.getName() + exNode.nbOfCores + ")" + SCCR;
		
            }
            if (node instanceof HwA) {
                HwA hwaNode = (HwA) node;
                declaration += "RRScheduler* " + hwaNode.getName() + "_scheduler = new RRScheduler(\"" + hwaNode.getName() + "_RRSched\", 0, " + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * HwA.DEFAULT_SLICE_TIME) + ", " + (int) Math.ceil((float) (hwaNode.clockRatio * Math.max(hwaNode.execiTime, hwaNode.execcTime) * (HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY * HwA.DEFAULT_PIPELINE_SIZE + 100 - HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY)) / 100) + " ) " + SCCR;
                for (int cores = 0; cores < 1; cores++) {
                    //if (tmlmapping.isAUsedHwNode(node)) {
                    declaration += "CPU* " + hwaNode.getName() + cores + " = new SingleCoreCPU(" + hwaNode.getID() + ", \"" + hwaNode.getName() + "_" + cores + "\", " + hwaNode.getName() + "_scheduler" + ", ";

                    declaration += hwaNode.clockRatio + ", " + hwaNode.execiTime + ", " + hwaNode.execcTime + ", " + HwA.DEFAULT_PIPELINE_SIZE + ", " + HwA.DEFAULT_TASK_SWITCHING_TIME + ", " + HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY + ", " + HwA.DEFAULT_GO_IDLE_TIME + ", " + HwA.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + ", " + hwaNode.byteDataSize + ")" + SCCR;
                    if (cores != 0)
                        declaration += node.getName() + cores + "->setScheduler(" + hwaNode.getName() + "_scheduler,false)" + SCCR;
                    declaration += "addCPU(" + node.getName() + cores + ")" + SCCR;
                }

		
            }
	    if (node instanceof HwCams) { //ajoute DG
                HwCams hwCamsNode = (HwCams) node;
                declaration += "RRScheduler* " + hwCamsNode.getName() + "_scheduler = new RRScheduler(\"" + hwCamsNode.getName() + "_RRSched\", 0, " + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * HwCams.DEFAULT_SLICE_TIME) + ", " + (int) Math.ceil((float) (hwCamsNode.clockRatio * Math.max(hwCamsNode.execiTime, hwCamsNode.execcTime) * (HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY * HwCams.DEFAULT_PIPELINE_SIZE + 100 - HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY)) / 100) + " ) " + SCCR;
                for (int cores = 0; cores < 1; cores++) {
                    //if (tmlmapping.isAUsedHwNode(node)) {
                    declaration += "CPU* " + hwCamsNode.getName() + cores + " = new SingleCoreCPU(" + hwCamsNode.getID() + ", \"" + hwCamsNode.getName() + "_" + cores + "\", " + hwCamsNode.getName() + "_scheduler" + ", ";

                    declaration += hwCamsNode.clockRatio + ", " + hwCamsNode.execiTime + ", " + hwCamsNode.execcTime + ", " + HwCams.DEFAULT_PIPELINE_SIZE + ", " + HwCams.DEFAULT_TASK_SWITCHING_TIME + ", " + HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY + ", " + HwCams.DEFAULT_GO_IDLE_TIME + ", " + HwCams.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + ", " + hwCamsNode.byteDataSize + ")" + SCCR;
                    if (cores != 0)
                        declaration += node.getName() + cores + "->setScheduler(" + hwCamsNode.getName() + "_scheduler,false)" + SCCR;
                    declaration += "addCPU(" + node.getName() + cores + ")" + SCCR;
                }

            }
        }
        declaration += CR;

        // Declaration of Buses
        declaration += "//Declaration of Buses" + CR;
        //declaration+="Bus* defaultBus = new Bus(-1,\"defaultBus\",100,1,1)" + SCCR;
        //declaration += "addBus(defaultBus)"+ SCCR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBus) {
                //if (tmlmapping.isAUsedHwNode(node)) {
                HwBus thisBus = (HwBus) node;
                for (int i = 0; i < thisBus.pipelineSize; i++) {
                    declaration += "Bus* " + node.getName() + "_" + i + " = new Bus(" + node.getID() + ",\"" + node.getName() + "_" + i + "\",0, 100, " + thisBus.byteDataSize + ", " + node.clockRatio + ",";
                    if (thisBus.arbitration == HwBus.CAN) declaration += "true";
                    else declaration += "false";
                    declaration += ");\naddBus(" + node.getName() + "_" + i + ")" + SCCR;
                }
                //}
            }
        }
        declaration += CR;

        // Declaration of Bridges
        declaration += "//Declaration of Bridges" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBridge) {
                declaration += "Bridge* " + node.getName() + " = new Bridge(" + node.getID() + ",\"" + node.getName() + "\", " + node.clockRatio + ", " + ((HwBridge) node).bufferByteSize + ")" + SCCR;
                declaration += "addBridge(" + node.getName() + ")" + SCCR;
            }
        }
        declaration += CR;

        // Declaration of Memories
        //declaration += "//Declaration of Memories\nMemory* defaultMemory = new Memory(-1,\"defaultMemory\",1,4)" + SCCR;
        //declaration += "addMem(defaultMemory)"+ SCCR;
        declaration += "//Declaration of Memories" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwMemory) {
                declaration += "Memory* " + node.getName() + " = new Memory(" + node.getID() + ",\"" + node.getName() + "\", " + node.clockRatio + ", " + ((HwMemory) node).byteDataSize + ")" + SCCR;
                declaration += "addMem(" + node.getName() + ")" + SCCR;
            }
        }
        declaration += CR;

        //Declaration of Bus masters
        declaration += "//Declaration of Bus masters" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwExecutionNode || node instanceof HwBridge) {
                List<HwLink> nodeLinks = tmlmapping.getTMLArchitecture().getLinkByHwNode(node);
                if (!nodeLinks.isEmpty()) {
                    //declaration+= "BusMaster* " + node.getName() + "2defaultBus = new BusMaster(\"" + node.getName() + "2defaultBus\", 0, defaultBus)" + SCCR;
                    //else{
                    for (HwLink link : nodeLinks) {
                        //declaration+= "BusMaster* " + node.getName() + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + node.getName() + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", 1, array(1, (SchedulableCommDevice*)" +  link.bus.getName() + "))" + SCCR;
                        int noOfCores;
                        if (node instanceof HwCPU) noOfCores = ((HwCPU) node).nbOfCores;
                        else noOfCores = 1;
                        //noOfCores=2;
                        //for (int cores = 0; cores < noOfCores; cores++) {
                            String nodeName = node.getName();
                            if (node instanceof HwCPU)
                                nodeName += ((HwCPU)node).nbOfCores;
                            declaration += "BusMaster* " + nodeName + "_" + link.bus.getName() + "_Master = new BusMaster(\"" + nodeName + "_" + link.bus.getName() + "_Master\", " + link.getPriority() + ", " + link.bus.pipelineSize + ", array(" + link.bus.pipelineSize;
                            for (int i = 0; i < link.bus.pipelineSize; i++)
                                declaration += ", (SchedulableCommDevice*)" + link.bus.getName() + "_" + i;
                            declaration += "))" + SCCR;
                            declaration += nodeName + "->addBusMaster(" + nodeName + "_" + link.bus.getName() + "_Master)" + SCCR;
			    //}
                    }
                }
            }
        }
        declaration += CR;

        // Declaration of channels
        TMLChannel channel;
        String tmp, param;
        declaration += "//Declaration of channels" + CR;
        for (TMLElement elem : tmlmodeling.getChannels()) {
            if (elem instanceof TMLChannel) {
                channel = (TMLChannel) elem;
                switch (channel.getType()) {
                    case TMLChannel.BRBW:
                        tmp = "TMLbrbwChannel";
                        param = "," + channel.getMax() + ",0";
                        break;
                    case TMLChannel.BRNBW:
                        tmp = "TMLbrnbwChannel";
                        param = ",0";
                        break;
                    case TMLChannel.NBRNBW:
                    default:
                        tmp = "TMLnbrnbwChannel";
                        param = "";
                }
                declaration += tmp + "* " + channel.getExtendedName() + " = new " + tmp + "(" + channel.getID() + ",\"" + channel.getName() + "\"," + channel.getSize() + ",";
                //TraceManager.addDev("Channel: " + channel.getName());
                //TraceManager.addDev("Channel origin node: " + channel.getOriginTask().getName() + " dest node: " + channel.getDestinationTask().getName());
                //TraceManager.addDev( "the list of mapped tasks: " + tmlmapping.getMappedTasksString());
                declaration += determineRouting(tmlmapping.getHwNodeOf(channel.getOriginTask()), tmlmapping.getHwNodeOf(channel.getDestinationTask()), elem) + param + "," + channel.getPriority();
                if (channel.isLossy() && channel.getType() != TMLChannel.NBRNBW)
                    declaration += "," + channel.getLossPercentage() + "," + channel.getMaxNbOfLoss();
                declaration += ")" + SCCR;
                declaration += "addChannel(" + channel.getExtendedName() + ")" + SCCR;
            }
        }
        declaration += CR;

        // Declaration of events
        declaration += "//Declaration of events" + CR;
        for (TMLEvent evt : tmlmodeling.getEvents()) {
            if (evt.isInfinite()) {
                tmp = "TMLEventBChannel<ParamType," + evt.getNbOfParams() + ">";
                param = ",0,false,false";
            } else {
                if (evt.isBlocking()) {
                    tmp = "TMLEventFBChannel<ParamType," + evt.getNbOfParams() + ">";
                    param = "," + evt.getMaxSize() + ",0";
                } else {
                    tmp = "TMLEventFChannel<ParamType," + evt.getNbOfParams() + ">";
                    param = "," + evt.getMaxSize() + ",0";
                }
            }
            //param += "," + evt.getNbOfParams();
            if (tmlmapping.isCommNodeMappedOn(evt, null)) {
                //TraceManager.addDev("Evt: " + evt.getName());
                declaration += tmp + "* " + evt.getExtendedName() + " = new " + tmp + "(" + evt.getID() + ",\"" + evt.getName() + "\"," + determineRouting(tmlmapping.getHwNodeOf(evt.getOriginTask()), tmlmapping.getHwNodeOf(evt.getDestinationTask()), evt) + param;

            } else {
                declaration += tmp + "* " + evt.getExtendedName() + " = new " + tmp + "(" + evt.getID() + ",\"" + evt.getName() + "\",0,0,0" + param;   ///old command
            }
            if (evt.isLossy()) declaration += "," + evt.getLossPercentage() + "," + evt.getMaxNbOfLoss();
            declaration += ")" + SCCR;
            declaration += "addEvent(" + evt.getExtendedName() + ")" + SCCR;
        }
        declaration += CR;

        // Declaration of requests
        declaration += "//Declaration of requests" + CR;
        for (TMLTask task : tmlmodeling.getTasks()) {
            if (task.isRequested()) {
                TMLRequest req = task.getRequest();
                if (tmlmapping.isCommNodeMappedOn(req, null)) {
                    //declaration += "TMLEventBChannel* reqChannel_"+ task.getName() + " = new TMLEventBChannel(" +
                    //TraceManager.addDev("Request: " + req.getName());
                    declaration += "TMLEventBChannel<ParamType," + req.getNbOfParams() + ">* reqChannel_" + task.getName() + " = new TMLEventBChannel<ParamType," + req.getNbOfParams() + ">(" +
                            req.getID() + ",\"reqChannel" + task.getName() + "\"," +
                            determineRouting(tmlmapping.getHwNodeOf(req.getOriginTasks().get(0)), //tmlmapping.getHwNodeOf(req.getDestinationTask()), req) + ",0," + req.getNbOfParams() + ",true)" + SCCR;
                                    tmlmapping.getHwNodeOf(req.getDestinationTask()), req) + ",0,true,false";
                } else {
                    declaration += "TMLEventBChannel<ParamType," + req.getNbOfParams() + ">* reqChannel_" + task.getName() + " = new TMLEventBChannel<ParamType," + req.getNbOfParams() + ">(" + //req.getID() + ",\"reqChannel"+ task.getName() + "\",0,0,0,0," + req.getNbOfParams() + ",true)" + SCCR;
                            req.getID() + ",\"reqChannel" + task.getName() + "\",0,0,0,0,true,false";
                }
                if (req.isLossy()) declaration += "," + req.getLossPercentage() + "," + req.getMaxNbOfLoss();
                declaration += ")" + SCCR;
                declaration += "addRequest(reqChannel_" + task.getName() + ")" + SCCR;
            }
        }
        declaration += CR;

        ///Set bus schedulers
        declaration += "//Set bus schedulers" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBus) {
                List<HwLink> busLinks = tmlmapping.getTMLArchitecture().getLinkByBus((HwBus) node);
                String devices = "";
                int numDevices = 0;
                if (!busLinks.isEmpty()) {
                    for (HwLink link : busLinks) {
                        if (link.hwnode instanceof HwExecutionNode || link.hwnode instanceof HwBridge) {
                            if ((link.hwnode instanceof HwCPU) || (link.hwnode instanceof HwA)) {
                                for (int cores = 0; cores < ((HwCPU) link.hwnode).nbOfCores; cores++) {
                                    //for (int cores=0; cores< 1; cores++){
                                    devices += ", (WorkloadSource*)" + link.hwnode.getName() + cores + "_" + node.getName() + "_Master";
                                    numDevices++;
                                }
                            } else {
                                devices += ", (WorkloadSource*)" + link.hwnode.getName() + "_" + node.getName() + "_Master";
                                numDevices++;
                            }
                        }
                    }
                    declaration += node.getName() + "_0->setScheduler((WorkloadSource*) new ";
                    if (((HwBus) node).arbitration == HwBus.BASIC_ROUND_ROBIN)
                        //declaration+="RRScheduler(\"" + node.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float)node.clockRatio)/((float)((HwBus)node).byteDataSize)) + ", array(";
                        declaration += "RRScheduler(\"" + node.getName() + "_RRSched\", 0, 5, " + (int) Math.ceil(((float) node.clockRatio) / ((float) ((HwBus) node).byteDataSize)) + ", array(";
                    else
                        declaration += "PrioScheduler(\"" + node.getName() + "_PrioSched\", 0, array(";
                    declaration += numDevices + devices + "), " + numDevices + "))" + SCCR;
                }
                for (int i = 1; i < ((HwBus) node).pipelineSize; i++)
                    declaration += node.getName() + "_" + i + "->setScheduler(" + node.getName() + "_0->getScheduler(),false)" + SCCR;
            }
        }
        declaration += CR;


        //Declaration of Tasks
        ListIterator<HwExecutionNode> iterator = tmlmapping.getNodes().listIterator();
        declaration += "//Declaration of tasks" + CR;
        HwExecutionNode node;
        //for(TMLTask task: tmlmodeling.getTasks()) {
        ArrayList<TMLChannel> channels;
        ArrayList<TMLEvent> events;
        ArrayList<TMLRequest> requests;
        int[] aStatistics = new int[8];
        Set<Integer> mappedChannels = new HashSet<Integer>();
        for (TMLTask task : tmlmapping.getMappedTasks()) {
            node = iterator.next();
            // int noOfCores;
            declaration += task.getName() + "* task__" + task.getName() + " = new " + task.getName() + "(" + task.getID() + "," + task.getPriority() + ",\"" + task.getName() + "\", array(";

            if (node instanceof HwCPU) {
                declaration += ((HwCPU) node).nbOfCores;
                //declaration+= 1;
                for (int cores = 0; cores < ((HwCPU) node).nbOfCores; cores++) {
                    //for (int cores=0; cores< 1; cores++){
                    declaration += "," + node.getName() + cores;
                }
                //declaration+= ")," + ((HwCPU)node).nbOfCores + CR;
                declaration += "),1" + CR;
            } else if (node instanceof HwA) {
                declaration += ((HwCPU) node).nbOfCores;
                //declaration+= 1;
                for (int cores = 0; cores < ((HwCPU) node).nbOfCores; cores++) {
                    //for (int cores=0; cores< 1; cores++){
                    declaration += "," + node.getName() + cores;
                }
                declaration += ")," + ((HwCPU) node).nbOfCores + CR;
                //declaration+= "),1" + CR;
            } else {
                declaration += "1," + node.getName() + "),1" + CR;
            }


            MappedSystemCTask mst;
            channels = new ArrayList<TMLChannel>(tmlmodeling.getChannels(task));
            events = new ArrayList<TMLEvent>(tmlmodeling.getEvents(task));
            requests = new ArrayList<TMLRequest>(tmlmodeling.getRequests(task));

            mst = new MappedSystemCTask(task, channels, events, requests, tmlmapping, mappedChannels, true);
            //mst.generateSystemC(debug, optimize, dependencies);
            //mst.generateSystemC(debug, optimize);
            tasks.add(mst);
            for (TMLChannel channelb : channels)
                declaration += "," + channelb.getExtendedName() + CR;

            for (TMLEvent evt : events)
                declaration += "," + evt.getExtendedName() + CR;

            for (TMLRequest req : requests)
                if (req.isAnOriginTask(task)) declaration += ",reqChannel_" + req.getDestinationTask().getName() + CR;

            if (task.isRequested()) declaration += ",reqChannel_" + task.getName() + CR;
            declaration += ")" + SCCR;
            declaration += "addTask(task__" + task.getName() + ")" + SCCR;
        }
        //int[] aStatistics = new int[8];

        declaration += "}\n\n";

        //Declaration of TEPEs
        declaration += "void generateTEPEs(){" + CR;
        declaration += "//Declaration of TEPEs" + CR;
        tepeTranslator.generateTEPEs();
        declaration += tepeTranslator.getCode();

        //Generation of tasks
        for (MappedSystemCTask task : tasks) {
            task.determineCheckpoints(aStatistics);
            task.generateSystemC(debug, optimize);
        }

        //Declaration of TEPEs continued
        declaration += CR;
        declaration += "}\n};\n\n" + tepeTranslator.getEqFuncs();
        declaration += "#include <main.h>\n";

        //if (aStatistics[0] != 0) TraceManager.addDev("Global gain variables " + 100 * aStatistics[1] / aStatistics[0]);
        //if (aStatistics[2] != 0) TraceManager.addDev("Global gain Channels " + 100 * aStatistics[3] / aStatistics[2]);
        //if (aStatistics[4] != 0) TraceManager.addDev("Global gain events " + 100 * aStatistics[5] / aStatistics[4]);
        /*if (aStatistics[6] != 0)
            TraceManager.addDev("Global gain checkpoints " + 100 * aStatistics[7] / aStatistics[6]);
        */
        //Declaration of EBRDDs
        /*declaration += "//Declaration of EBRDDs" + CR;
          for(EBRDD ebrdd: ebrdds){
          declaration += ebrdd.getName() + "* ebrdd__" + ebrdd.getName() + " = new " + ebrdd.getName() + "(0, \""+ ebrdd.getName() + "\");\n";
          declaration += "addEBRDD(ebrdd__"+ ebrdd.getName() +")"+ SCCR;
          }*/
    }


    private int extractPath(LinkedList<HwCommunicationNode> path, StrWrap masters, StrWrap slaves, HwNode startNode, HwNode destNode, boolean reverseIn) {
        String firstPart = ""; //lastBus="";
        int masterCount = 0;
        boolean reverse = reverseIn;
        if (reverseIn)
            slaves.str += ",static_cast<Slave*>(0)";
        else
            firstPart = startNode.getName() + "0";
        /*TraceManager.addDev("------------------------------------------------------");
          for(HwCommunicationNode commElem:path){
          TraceManager.addDev("CommELem to process: " + commElem.getName());
          }
          TraceManager.addDev("------------------------------------------------------");*/
        for (HwCommunicationNode commElem : path) {
            //TraceManager.addDev("CommELem to process: " + commElem.getName());
            //String commElemName = commElem.getName();
            //if (commElem instanceof HwCPU) commElemName += "0";
            //TraceManager.addDev("Next elem in path: " + commElem.getName());
            if (commElem instanceof HwMemory) {
                reverse = true;
                slaves.str += ",static_cast<Slave*>(" + commElem.getName() + "),static_cast<Slave*>(" + commElem.getName() + ")";
                //firstPart=lastBus;
                firstPart = "";
            } else {
                if (reverse) {
                    if (firstPart.length() == 0) {
                        firstPart = commElem.getName();
                        //firstPart=commElemName;
                    } else {
                        masters.str += "," + commElem.getName() + "_" + firstPart + "_Master";
                        //masters.str+= "," + commElemName + "_" + firstPart + "_Master";
                        masterCount++;
                        slaves.str += ",static_cast<Slave*>(" + commElem.getName() + ")";
                        firstPart = "";
                    }
                } else {
                    if (firstPart.length() == 0) {
                        firstPart = commElem.getName();
                        slaves.str += ",static_cast<Slave*>(" + firstPart + ")";
                    } else {
                        //lastBus=commElem.getName();
                        masters.str += "," + firstPart + "_" + commElem.getName() + "_Master";
                        masterCount++;
                        firstPart = "";
                    }
                }
            }
        }
        if (reverse) {
            //masters.str+= "," + destNode.getName() + "_" + firstPart + "_Master";
            masters.str += "," + destNode.getName() + "0_" + firstPart + "_Master";
            return masterCount + 1;
        } else {
            slaves.str += ",static_cast<Slave*>(0)";
            return -masterCount;
        }
    }

    private String determineRouting(HwNode startNode, HwNode destNode, TMLElement commElemToRoute) {

        //TraceManager.addDev( "Determine routing from " + startNode.getName() + " to " + destNode.getName() );
        StrWrap masters = new StrWrap(), slaves = new StrWrap();
        LinkedList<HwCommunicationNode> path = new LinkedList<HwCommunicationNode>();
        LinkedList<HwCommunicationNode> commNodes = new LinkedList<HwCommunicationNode>();

        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwCommunicationNode) {
                commNodes.add((HwCommunicationNode) node);
                //TraceManager.addDev( "Inserted: " + ((HwCommunicationNode)node).getName() );
            }
        }
        if (startNode == null) {
            TraceManager.addDev("NULL REFERENCE");
        } else {
            //TraceManager.addDev( "startNode: " + startNode.getName() );
        }
        HwMemory memory = getMemConnectedToBusChannelMapped(commNodes, null, commElemToRoute);
        if (memory == null) {
            TraceManager.addDev("no memories to map");
            exploreBuses(0, commNodes, path, startNode, destNode, commElemToRoute);
        } else {
            LinkedList<HwCommunicationNode> commNodes2 = new LinkedList<HwCommunicationNode>(commNodes);
            //exploreBuses(0, commNodes, path, startNode, memory, commElemToRoute);
            if (!exploreBuses(0, commNodes, path, startNode, memory, commElemToRoute)) {
                TraceManager.addDev("NO route to " + memory.getName() + "found!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            path.add(memory);
            exploreBuses(0, commNodes2, path, memory, destNode, commElemToRoute);
        }
        int hopNum;
        if ((hopNum = extractPath(path, masters, slaves, startNode, destNode, false)) < 0) {
            hopNum = extractPath(path, masters, slaves, destNode, destNode, true) - hopNum;
        }
        //TraceManager.addDev(commElemToRoute.getName() + " is mapped on:");
        for (HwCommunicationNode commElem : path) {
            TraceManager.addDev(commElem.getName());
        }
        //TraceManager.addDev("number of elements: " + hopNum);
        //TraceManager.addDev("masters: " + masters.str);
        //TraceManager.addDev("slaves: " + slaves.str);
        return hopNum + ",array(" + hopNum + masters.str + "),array(" + hopNum + slaves.str + ")";
    }

    private boolean exploreBuses(int depth, LinkedList<HwCommunicationNode> commNodes, LinkedList<HwCommunicationNode> path, HwNode startNode, HwNode destNode, TMLElement commElemToRoute) {
        //first called with Maping:getCommunicationNodes
        LinkedList<HwCommunicationNode> nodesToExplore;
        //TraceManager.addDev("No of comm nodes " + commNodes.size());
        //TraceManager.addDev("startNode=" + startNode);
        boolean busExploreMode = ((depth & 1) == 0);
        //if (depth % 2 == 0){
        if (busExploreMode) {
            //TraceManager.addDev("search for buses connected to " + startNode.getName());
            nodesToExplore = getBusesConnectedToNode(commNodes, startNode);
        } else {
            //TraceManager.addDev("search for bridges connected to: " + startNode.getName());
            nodesToExplore = getBridgesConnectedToBus(commNodes, (HwBus) startNode);
        }
        //HwMemory memory = null;
        //TraceManager.addDev("no of elements found: " + nodesToExplore.size());
        for (HwCommunicationNode currNode : nodesToExplore) {
            //memory = null;
            if (busExploreMode) {
                //memory = getMemConnectedToBusChannelMapped(commNodes, (HwBus)currNode, commElemToRoute);
                if (isBusConnectedToNode(currNode, destNode)) {
                    //TraceManager.addDev(currNode.getName() + " is last node");
                    path.add(currNode);
                    //if (memory!=null) path.add(memory);
                    commNodes.remove(currNode);
                    return true;
                }
            }
            if (tmlmapping.isCommNodeMappedOn(commElemToRoute, currNode)) {
                //TraceManager.addDev(currNode.getName() + " mapping found for " + commElemToRoute.getName());
                path.add(currNode);
                //if (memory!=null) path.add(memory);
                commNodes.remove(currNode);
                if (exploreBuses(depth + 1, commNodes, path, currNode, destNode, commElemToRoute)) return true;
                path.remove(currNode);
                //if (memory!=null) path.remove(memory);
                commNodes.add(currNode);
            }
        }
        for (HwCommunicationNode currNode : nodesToExplore) {
            //if (busExploreMode) memory = getMemConnectedToBusChannelMapped(commNodes, (HwBus)currNode, commElemToRoute); else memory=null;
            path.add(currNode);
            //if (memory!=null) path.add(memory);
            commNodes.remove(currNode);

            //TraceManager.addDev(currNode.getName());
            if (exploreBuses(depth + 1, commNodes, path, currNode, destNode, commElemToRoute)) return true;
            path.remove(currNode);
            //if (memory!=null) path.remove(memory);
            commNodes.add(currNode);
        }
        return false;
    }

    private HwMemory getMemConnectedToBusChannelMapped(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus, TMLElement _channel) {
        for (HwCommunicationNode commNode : _commNodes) {
            if (commNode instanceof HwMemory) {
                if (_bus != null) {
                    //TraceManager.addDev(commNode.getName() + " connected to bus " + _bus.getName() + ": " + tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus));
                }
                //TraceManager.addDev(_channel.getName() + " is mapped onto " + commNode.getName() + ": " + tmlmapping.isCommNodeMappedOn(_channel, commNode));
                if ((_bus == null || tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus))
                        && tmlmapping.isCommNodeMappedOn(_channel, commNode)) {
                    return (HwMemory) commNode;
                }
            }
        }
        return null;
    }

    private LinkedList<HwCommunicationNode> getBusesConnectedToNode(LinkedList<HwCommunicationNode> _commNodes, HwNode _node) {
        LinkedList<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
        for (HwCommunicationNode commNode : _commNodes) {
            if (commNode instanceof HwBus) {
                if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(_node, (HwBus) commNode))
                    resultList.add(commNode);
            }
        }
        return resultList;
    }

    private LinkedList<HwCommunicationNode> getBridgesConnectedToBus(LinkedList<HwCommunicationNode> _commNodes, HwBus _bus) {
        LinkedList<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
        for (HwCommunicationNode commNode : _commNodes) {
            if (commNode instanceof HwBridge) {
                if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus)) resultList.add(commNode);
            }
        }
        return resultList;
    }

    private boolean isBusConnectedToNode(HwCommunicationNode commNode, HwNode node) {
        for (HwLink link : tmlmapping.getTMLArchitecture().getHwLinks()) {
            if (link.bus == commNode && link.hwnode == node) return true;
        }
        return false;
    }

//    private String getIdentifierNameByID(int id){
//
//        for(MappedSystemCTask task: tasks){
//            String tmp = task.getIdentifierNameByID(id);
//            if (tmp!=null) return tmp;
//        }
//        return null;
//    }

    /*private void generateEBRDDs(){
      for(EBRDD ebrdd: ebrdds){
      SystemCEBRDD newEbrdd = new SystemCEBRDD(ebrdd, tmlmodeling, tmlmapping);
      newEbrdd.generateSystemC(debug);
      systemCebrdds.add(newEbrdd);
      }
      }*/

    private void generateTaskFiles(String path) throws FileException {
        for (MappedSystemCTask mst : tasks) {
            mst.saveInFiles(path);
        }
        //for(SystemCEBRDD ebrdd: systemCebrdds) {
        //      ebrdd.saveInFiles(path);
        //}
    }


    public MappedSystemCTask getMappedTaskByName(String iName) {
        for (MappedSystemCTask task : tasks) {
            if (task.getTMLTask().getName().equals(iName)) return task;
        }
        return null;
    }
}

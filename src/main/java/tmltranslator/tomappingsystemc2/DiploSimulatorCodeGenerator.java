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
 * Class TML2MappingSystemC Creation: 03/09/2007
 *
 * @author Daniel Knorreck, Dominique Blouin, Ludovic Apvrille
 * @version 1.1 03/09/2007
 */
public class DiploSimulatorCodeGenerator implements IDiploSimulatorCodeGenerator {

    private final static String CR = "\n";
    // private final static String CR2 = "\n\n";
    private final static String SCCR = ";\n";
    // private final static String EFCR = "}\n";
    // private final static String EFCR2 = "}\n\n";
    // private final static String EF = "}";
    // private final static int MAX_EVENT = 1024;

    private static final String SPACE = " ";

    private TMLModeling<?> tmlmodeling;
    private TMLMapping<?> tmlmapping;

    private boolean debug;
    private boolean optimize;
    private String header, declaration, mainFile, src;
    private ArrayList<MappedSystemCTask> tasks;

    private String modelName = "LovelyModel";

    // private ArrayList<EBRDD> ebrdds;
    // private ArrayList<TEPE> tepes;
    SystemCTEPE tepeTranslator;
    // private ArrayList<SystemCEBRDD> systemCebrdds = new
    // ArrayList<SystemCEBRDD>();

    private final NamesGenerationHelper namesGen;

    public DiploSimulatorCodeGenerator(TMLModeling<?> _tmlm) {
        this(_tmlm.getDefaultMapping());

        tmlmodeling = _tmlm;
        tmlmodeling.removeForksAndJoins();
        tmlmodeling.removePeriodicTasks();
        // tmlmapping = tmlmodeling.getDefaultMapping();
        // tepeTranslator = new SystemCTEPE(new ArrayList<TEPE>(), this);
    }

    public DiploSimulatorCodeGenerator(TMLMapping<?> _tmlmapping) {
        // tmlmapping = _tmlmapping;
        // tmlmapping.handleCPs();
        // tmlmapping.removeForksAndJoins();
        // tmlmapping.makeMinimumMapping();
        // tepeTranslator = new SystemCTEPE(new ArrayList<TEPE>(), this);
        this(_tmlmapping, null, new ArrayList<TEPE>());
    }

    DiploSimulatorCodeGenerator(TMLModeling<?> _tmlm, List<EBRDD> _ebrdds, List<TEPE> _tepes) {
        this(_tmlm.getDefaultMapping(), _ebrdds, _tepes);

        tmlmodeling = _tmlm;
        // tmlmapping = tmlmodeling.getDefaultMapping();
        // tepeTranslator = new SystemCTEPE(_tepes, this);
    }

    DiploSimulatorCodeGenerator(TMLMapping<?> _tmlmapping, List<EBRDD> _ebrdds, List<TEPE> _tepes) {
        tmlmapping = _tmlmapping;

        if (tmlmapping == null) {
            throw new IllegalArgumentException("TML Mapping is NULL.");
        }

        tmlmapping.handleCPs();
        tmlmapping.removeForksAndJoins();
        tmlmapping.removePeriodicTasks();
        tmlmapping.makeMinimumMapping();
        tepeTranslator = new SystemCTEPE(_tepes, this);
        namesGen = NamesGenerationHelper.INSTANCE;
    }

    public void setModelName(String _modelName) {
        modelName = _modelName;
    }

    public void saveFile(String path, String filename) throws FileException {
        generateTaskFiles(path);
        FileUtils.saveFile(path + filename + ".cpp", getFullCode());
        src += filename + ".cpp";
        FileUtils.saveFile(path + "Makefile.src", src);
        // tepeTranslator.saveFile(path + "src_simulator/TEPE/test.h");
    }

    public String getFullCode() {
        return mainFile;
    }

    // If String is non-null, returns an error
    public String generateSystemC(boolean _debug, boolean _optimize) {

        TraceManager.addDev("Generate SystemC code from DiploSimulatorCodeGenerator");

        debug = _debug;
        optimize = _optimize;
        tmlmapping.removeAllRandomSequences();
        tmlmapping.handleCPs();
        tmlmodeling = tmlmapping.getTMLModeling();
        tasks = new ArrayList<MappedSystemCTask>();
        // generateSystemCTasks();
        // generateEBRDDs();
        String error = generateMainFile();
        if (error != null) {
            return error;
        }

        generateMakefileSrc();

        return null;
    }

    // If String is non-null, returns an error
    private String generateMainFile() {
        makeHeader();

        String error = makeDeclarations();
        if (error != null) {
            return error;
        }

        header += tepeTranslator.getEqFuncDeclaration() + "\n";
        mainFile = header + declaration;
        mainFile = Conversion.indentString(mainFile, 4);

        return null;
    }

    private void generateMakefileSrc() {
        src = "SRCS = ";
        for (TMLTask mst : tmlmapping.getMappedTasks()) {
            src += namesGen.cppFileName(mst.getName()) + SPACE;
        }
        // for(EBRDD ebrdd: ebrdds){
        // src += ebrdd.getName() + ".cpp ";
        // }
    }

    private void makeHeader() {
        // System headers
        header = "#include <Simulator.h>" + CR;
        header += "#include <AliasConstraint.h>\n#include <EqConstraint.h>\n#include <LogConstraint.h>\n#include <PropLabConstraint.h>\n";
        header += "#include <PropRelConstraint.h>\n#include <SeqConstraint.h>\n#include <SignalConstraint.h>\n#include <TimeMMConstraint.h>\n";
        header += "#include <TimeTConstraint.h>\n";
        header += "#include <CPU.h>\n#include <SingleCoreCPU.h>\n#include <MultiCoreCPU.h>\n#include <FPGA.h>\n#include <RRScheduler.h>\n#include "
                + "<RRPrioScheduler.h>\n" + "#include <OrderScheduler.h>\n" + "#include <PrioScheduler.h>\n#include <Bus.h>\n";
        header += "#include <ReconfigScheduler.h>\n";
        header += "#include <Bridge.h>\n#include <Memory.h>\n#include <TMLbrbwChannel.h>\n#include <TMLnbrnbwChannel.h>\n";
        header += "#include <TMLbrnbwChannel.h>\n#include <TMLEventBChannel.h>\n#include <TMLEventFChannel.h>\n#include <TMLEventFBChannel.h>\n";
        header += "#include <TMLTransaction.h>\n#include <TMLCommand.h>\n#include <TMLTask.h>\n";
        header += "#include <SimComponents.h>\n#include <Server.h>\n#include <SimServSyncInfo.h>\n#include <ListenersSimCmd.h>\n";

        // Generate tasks header
        for (TMLTask mst : tmlmapping.getMappedTasks()) {
            // header += "#include <" + mst.getReference() + ".h>" + CR;
            header += "#include " + NamesGenerationHelper.LEFT_ANGLE_RACKET + namesGen.headerFileName(mst.getName())
                    + NamesGenerationHelper.RIGHT_ANGLE_RACKET + CR;
        }
        // for(EBRDD ebrdd: ebrdds){
        // header += "#include <" + ebrdd.getName() + ".h>" + CR;
        // }
        header += CR;
    }

    // If String is non-null, returns an error
    private String makeDeclarations() {
        declaration = "class CurrentComponents: public SimComponents{\npublic:\nCurrentComponents():SimComponents(" + tmlmapping.getHashCode()
                + "){\n";

        // Declaration of HW nodes
        declaration += "//Declaration of CPUs" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwCPU) {
                final HwCPU exNode = (HwCPU) node;
                final String schedulerInstName;

                if (exNode.getType().equals("CPURRPB")) {
                    schedulerInstName = namesGen.prioSchedulerInstanceName(exNode);
                    declaration += "RRPrioScheduler* " + schedulerInstName + " = new RRPrioScheduler(\"" + namesGen.prioSchedulerName(exNode)
                            + "\", 0," + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime) + ", "
                            + (int) Math.ceil((float) (exNode.clockRatio * Math.max(exNode.execiTime, exNode.execcTime)
                                    * (exNode.branchingPredictionPenalty * exNode.pipelineSize + 100 - exNode.branchingPredictionPenalty)) / 100)
                            + " ) " + SCCR;
                } else {
                    // tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime
                    // declaration += "RRScheduler* " + exNode.getName() + "_scheduler = new
                    // RRScheduler(\"" + exNode.getName() + "_RRSched\", 0, 5, " + (int)
                    // Math.ceil(((float)exNode.execiTime)*(1+((float)exNode.branchingPredictionPenalty)/100))
                    // + " ) " + SCCR;
                    schedulerInstName = namesGen.rrSchedulerInstanceName(exNode);
                    declaration += "RRScheduler* " + schedulerInstName + " = new RRScheduler(\"" + namesGen.rrSchedulerName(exNode) + "\", 0, "
                            + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * exNode.sliceTime) + ", "
                            + (int) Math.ceil((float) (exNode.clockRatio * Math.max(exNode.execiTime, exNode.execcTime)
                                    * (exNode.branchingPredictionPenalty * exNode.pipelineSize + 100 - exNode.branchingPredictionPenalty)) / 100)
                            + " ) " + SCCR;
                }

                // TraceManager.addDev("cores " + exNode.nbOfCores);

                /*
                 * for (int cores = 0; cores < exNode.nbOfCores; cores++) { final String
                 * cpuInstName = namesGen.cpuInstanceName(exNode, cores); declaration += "CPU* "
                 * + cpuInstName + " = new SingleCoreCPU(" + exNode.getID() + ", \"" +
                 * namesGen.cpuName(exNode, cores) + "\", " + schedulerInstName + ", ";
                 * declaration += exNode.clockRatio + ", " + exNode.execiTime + ", " +
                 * exNode.execcTime + ", " + exNode.pipelineSize + ", " +
                 * exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " +
                 * exNode.goIdleTime + ", " + exNode.maxConsecutiveIdleCycles + ", " +
                 * exNode.byteDataSize + ")" + SCCR;
                 * 
                 * if (cores != 0) { declaration += cpuInstName + "->setScheduler(" +
                 * schedulerInstName + ",false)" + SCCR; }
                 */
                final String cpuInstName = namesGen.cpuInstanceName(exNode, exNode.nbOfCores);
                if (exNode.nbOfCores == 1) {
                    declaration += "CPU* " + exNode.getName() + "_" + exNode.nbOfCores + " = new SingleCoreCPU(" + exNode.getID() + ", \""
                            + exNode.getName() + "_" + exNode.nbOfCores + "\", " + exNode.getName() + "_scheduler" + ", ";

                    declaration += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", "
                            + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", "
                            + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ")" + SCCR;
                } else {
                    declaration += "CPU* " + exNode.getName() + "_" + exNode.nbOfCores + " = new MultiCoreCPU(" + exNode.getID() + ", \""
                            + exNode.getName() + "_" + exNode.nbOfCores + "\", " + exNode.getName() + "_scheduler" + ", ";

                    declaration += exNode.clockRatio + ", " + exNode.execiTime + ", " + exNode.execcTime + ", " + exNode.pipelineSize + ", "
                            + exNode.taskSwitchingTime + ", " + exNode.branchingPredictionPenalty + ", " + exNode.goIdleTime + ", "
                            + exNode.maxConsecutiveIdleCycles + ", " + exNode.byteDataSize + ", " + exNode.nbOfCores + ")" + SCCR;
                }

                declaration += "addCPU(" + cpuInstName + ")" + SCCR;
                // }
            } else if (node instanceof HwA) {
                final HwA hwaNode = (HwA) node;
                final String schedulerInstName = namesGen.rrSchedulerInstanceName(hwaNode);
                final String schedulerName = namesGen.rrSchedulerName(hwaNode);
                declaration += "RRScheduler* " + schedulerInstName + " = new RRScheduler(\"" + schedulerName + "\", 0, "
                        + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * HwA.DEFAULT_SLICE_TIME) + ", "
                        + (int) Math.ceil((float) (hwaNode.clockRatio * Math.max(hwaNode.execiTime, hwaNode.execcTime)
                                * (HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY * HwA.DEFAULT_PIPELINE_SIZE + 100
                                        - HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY))
                                / 100)
                        + " ) " + SCCR;

                // DB: Issue #21 Why a for loop???
                // for (int cores=0; cores<1; cores++){
                final String hwaInstName = namesGen.hwAccInstanceName(hwaNode);
                declaration += "CPU* " + hwaInstName + " = new SingleCoreCPU(" + hwaNode.getID() + ", \"" + namesGen.hwAccName(hwaNode) + "\", "
                        + schedulerInstName + ", ";

                declaration += hwaNode.clockRatio + ", " + hwaNode.execiTime + ", " + hwaNode.execcTime + ", " + HwA.DEFAULT_PIPELINE_SIZE + ", "
                        + HwA.DEFAULT_TASK_SWITCHING_TIME + ", " + HwA.DEFAULT_BRANCHING_PREDICTION_PENALTY + ", " + HwA.DEFAULT_GO_IDLE_TIME + ", "
                        + HwA.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + ", " + hwaNode.byteDataSize + ")" + SCCR;

                // DB: Issue #21 TODO: Should there be a scheduler?? Given the for loop, cores
                // is always 0 so this code is never executed
                // if (cores!=0) {
                // declaration+= cpuInstName + "->setScheduler(" + schedulerInstName + ",false)"
                // + SCCR;
                // }

                declaration += "addCPU(" + hwaInstName + ")" + SCCR;
                // }

                // ajoute DG 24.09.2019
            } else if (node instanceof HwCams) {
                final HwCams hwCamsNode = (HwCams) node;
                final String schedulerInstName = namesGen.rrSchedulerInstanceName(hwCamsNode);
                final String schedulerName = namesGen.rrSchedulerName(hwCamsNode);
                declaration += "RRScheduler* " + schedulerInstName + " = new RRScheduler(\"" + schedulerName + "\", 0, "
                        + (tmlmapping.getTMLArchitecture().getMasterClockFrequency() * HwCams.DEFAULT_SLICE_TIME) + ", "
                        + (int) Math.ceil((float) (hwCamsNode.clockRatio * Math.max(hwCamsNode.execiTime, hwCamsNode.execcTime)
                                * (HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY * HwCams.DEFAULT_PIPELINE_SIZE + 100
                                        - HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY))
                                / 100)
                        + " ) " + SCCR;

                // DB: Issue #21 Why a for loop???
                // for (int cores=0; cores<1; cores++){
                final String hwCamsInstName = namesGen.hwCamsInstanceName(hwCamsNode);
                declaration += "CPU* " + hwCamsInstName + " = new SingleCoreCPU(" + hwCamsNode.getID() + ", \"" + namesGen.hwCamsName(hwCamsNode)
                        + "\", " + schedulerInstName + ", ";

                declaration += hwCamsNode.clockRatio + ", " + hwCamsNode.execiTime + ", " + hwCamsNode.execcTime + ", " + HwCams.DEFAULT_PIPELINE_SIZE
                        + ", " + HwCams.DEFAULT_TASK_SWITCHING_TIME + ", " + HwCams.DEFAULT_BRANCHING_PREDICTION_PENALTY + ", "
                        + HwCams.DEFAULT_GO_IDLE_TIME + ", " + HwCams.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES + ", " + hwCamsNode.byteDataSize + ")"
                        + SCCR;

                declaration += "addCPU(" + hwCamsInstName + ")" + SCCR;
                // }
                // fin ajoute DG

            } else if (node instanceof HwFPGA) {
                final HwFPGA hwFpgaNode = (HwFPGA) node;
                final String schedulerInstName = namesGen.rrSchedulerInstanceName(hwFpgaNode);
                final String schedulerName = namesGen.rrSchedulerName(hwFpgaNode);
                if (hwFpgaNode.getScheduling().trim().length() > 0) {
                    declaration += "ReconfigScheduler* " + schedulerInstName + " = new ReconfigScheduler(\"" + schedulerName + "\", 0, \""
                            + hwFpgaNode.getScheduling().trim() + "\") " + SCCR;
                } else {
                    declaration += "OrderScheduler* " + schedulerInstName + " = new OrderScheduler(\"" + schedulerName + "\", 0) " + SCCR;
                }

                final String hwFpgaInstName = namesGen.hwFpgaInstanceName(hwFpgaNode);
                declaration += "FPGA* " + hwFpgaInstName + " = new FPGA(" + hwFpgaNode.getID() + ", \"" + namesGen.hwFpgaName(hwFpgaNode) + "\", "
                        + schedulerInstName + ", ";

                declaration += hwFpgaNode.reconfigurationTime + ", " + hwFpgaNode.clockRatio + ", " + hwFpgaNode.goIdleTime + ", " + hwFpgaNode.maxConsecutiveIdleCycles + ", "
                        + hwFpgaNode.execiTime + ", " + hwFpgaNode.execcTime + ")" + SCCR;

                // DB: Issue #21 TODO: Should there be a scheduler?? Given the for loop, cores
                // is always 0 so this code is never executed
                // if (cores!=0) {
                // declaration+= cpuInstName + "->setScheduler(" + schedulerInstName + ",false)"
                // + SCCR;
                // }

                declaration += "addFPGA(" + hwFpgaInstName + ")" + SCCR;
                // }
            }
        }

        declaration += CR;

        // Declaration of Model Name
        declaration += "//Declaration of Model Name" + CR;
        declaration += "std::string msg=" + "\"" + modelName + "\"" + SCCR;
        declaration += "addModelName(" + "\"" + modelName + "\"" + ")" + SCCR;

        // Declaration of Buses
        declaration += "//Declaration of Buses" + CR;

        for (final HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBus) {
                final HwBus thisBus = (HwBus) node;

                for (int i = 0; i < thisBus.pipelineSize; i++) {
                    final String busInstName = namesGen.busInstanceName(thisBus, i);
                    declaration += "Bus* " + busInstName + " = new Bus(" + node.getID() + ",\"" + namesGen.busName(thisBus, i) + "\",0, 100, "
                            + thisBus.byteDataSize + ", " + node.clockRatio + ",";

                    if (thisBus.arbitration == HwBus.CAN) {
                        declaration += Boolean.TRUE.toString();
                    } else {
                        declaration += Boolean.FALSE.toString();
                    }

                    declaration += ");\naddBus(" + busInstName + ")" + SCCR;
                }
            }
        }

        declaration += CR;

        // Declaration of Bridges
        declaration += "//Declaration of Bridges" + CR;

        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBridge) {
                final HwBridge bridge = (HwBridge) node;
                final String bridgeInstName = namesGen.bridgeInstanceName(bridge);
                declaration += "Bridge* " + bridgeInstName + " = new Bridge(" + node.getID() + ",\"" + namesGen.bridgeName(bridge) + "\", "
                        + node.clockRatio + ", " + ((HwBridge) node).bufferByteSize + ")" + SCCR;
                declaration += "addBridge(" + bridgeInstName + ")" + SCCR;
            }
        }

        declaration += CR;

        // Declaration of Memories
        declaration += "//Declaration of Memories" + CR;

        for (final HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwMemory) {
                final HwMemory memory = (HwMemory) node;
                final String memInstName = namesGen.memoryInstanceName(memory);
                declaration += "Memory* " + memInstName + " = new Memory(" + node.getID() + ",\"" + namesGen.memoryName(memory) + "\", "
                        + node.clockRatio + ", " + ((HwMemory) node).byteDataSize + ")" + SCCR;
                declaration += "addMem(" + memInstName + ")" + SCCR;
            }
        }

        declaration += CR;

        // Declaration of Bus masters
        declaration += "//Declaration of Bus masters" + CR;
        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwExecutionNode || node instanceof HwBridge) {
                final List<HwLink> nodeLinks = tmlmapping.getTMLArchitecture().getLinkByHwNode(node);

                for (final HwLink link : nodeLinks) {
                    final int noOfCores;

                    if (node instanceof HwCPU) {
                        noOfCores = ((HwCPU) node).nbOfCores;
                    } else {
                        noOfCores = 1;
                    }

                    // for (int cores = 0; cores < noOfCores; cores++) {
                    final String nodeInstanceName;

                    // if (node instanceof HwCPU || node instanceof HwA || node instanceof HwFPGA) {
                    if (node instanceof HwCPU || node instanceof HwA || node instanceof HwFPGA || node instanceof HwCams) { // DG 23.09.2019
                        nodeInstanceName = namesGen.executionNodeInstanceName((HwExecutionNode) node, noOfCores);
                    } else {
                        nodeInstanceName = namesGen.bridgeInstanceName((HwBridge) node);
                    }

                    final String busMasterInstName = namesGen.busMasterInstanceName(node, 0, link.bus);

                    declaration += "BusMaster* " + busMasterInstName + " = new BusMaster(\"" + namesGen.busMasterName(node, 0, link.bus) + "\", "
                            + link.getPriority() + ", " + link.bus.pipelineSize + ", array(" + link.bus.pipelineSize;

                    for (int i = 0; i < link.bus.pipelineSize; i++) {
                        declaration += ", (SchedulableCommDevice*) " + namesGen.schedComDeviceInstanceName(link.bus, i);
                    }

                    declaration += "))" + SCCR;

                    declaration += nodeInstanceName + "->addBusMaster(" + busMasterInstName + ")" + SCCR;
                    // }
                }
            }
        }

        declaration += CR;

        // Declaration of channels
        declaration += "//Declaration of channels" + CR;

        for (final TMLElement elem : tmlmodeling.getChannels()) {
            if (elem instanceof TMLChannel) {
                final TMLChannel channel = (TMLChannel) elem;

                final String channelTypeName = namesGen.channelTypeName(channel);
                final String channelInstName = namesGen.channelInstanceName(channel);

                declaration += channelTypeName + "* " + channelInstName + " = new " + channelTypeName + "(" + channel.getID() + ",\""
                        + namesGen.channelName(channel) + "\"," + channel.getSize() + ",";

                final String param;

                switch (channel.getType()) {
                case TMLChannel.BRBW:
                    param = "," + channel.getMax() + ",0";
                    break;
                case TMLChannel.BRNBW:
                    param = ",0";
                    break;
                case TMLChannel.NBRNBW:
                default:
                    param = "";
                }

                // TraceManager.addDev("\nDetermining routing of " + channel.getName() + ":");
                // TraceManager.addDev(channel.toString());

                String ret = determineRouting(tmlmapping.getHwNodeOf(channel.getOriginTask()), tmlmapping.getHwNodeOf(channel.getDestinationTask()),
                        elem);
                // TraceManager.addDev("------> Routing = " + ret);
                if (ret == null) {
                    return "Could not determine  routing between " + channel.getOriginTask().getName() + " and "
                            + channel.getDestinationTask().getName() + " for channel " + channel.getName();
                }
                ret = ret + param + "," + channel.getPriority();
                declaration += ret;

                if (channel.isLossy() && channel.getType() != TMLChannel.NBRNBW) {
                    declaration += "," + channel.getLossPercentage() + "," + channel.getMaxNbOfLoss();
                }

                declaration += ")" + SCCR;
                declaration += "addChannel(" + channelInstName + ")" + SCCR;
            }
        }

        declaration += CR;

        // Declaration of events
        declaration += "//Declaration of events" + CR;

        for (final TMLEvent evt : tmlmodeling.getEvents()) {
            final String eventTypeName = namesGen.eventTypeName(evt);

            final String param;

            if (evt.isInfinite()) {
                param = ",0,false,false";
            } else {
                if (evt.isBlocking()) {
                    param = "," + evt.getMaxSize() + ",0";
                } else {
                    param = "," + evt.getMaxSize() + ",0";
                }
            }

            final String eventInstName = namesGen.eventInstanceName(evt);
            final String eventName = namesGen.eventName(evt);

            if (tmlmapping.isCommNodeMappedOn(evt, null)) {
                // TraceManager.addDev("Evt: " + evt.getName());
                String ret = determineRouting(tmlmapping.getHwNodeOf(evt.getOriginTask()), tmlmapping.getHwNodeOf(evt.getDestinationTask()), evt);
                if (ret == null) {
                    return "Could not determine routing between " + evt.getOriginTask().getName() + " and " + evt.getDestinationTask().getName()
                            + " for event " + evt.getName();
                }
                declaration += eventTypeName + "* " + eventInstName + " = new " + eventTypeName + "(" + evt.getID() + ",\"" + eventName + "\"," + ret
                        + param;

            } else {
                declaration += eventTypeName + "* " + eventInstName + " = new " + eventTypeName + "(" + evt.getID() + ",\"" + eventName + "\",0,0,0"
                        + param; /// old command
            }

            if (evt.isLossy()) {
                declaration += "," + evt.getLossPercentage() + "," + evt.getMaxNbOfLoss();
            }

            declaration += ")" + SCCR;
            declaration += "addEvent(" + eventInstName + ")" + SCCR;
        }

        declaration += CR;

        // Declaration of requests
        declaration += "//Declaration of requests" + CR;

        for (TMLTask task : tmlmodeling.getTasks()) {
            if (task.isRequested()) {
                final String reqChannelInstName = namesGen.requestChannelInstanceName(task);
                TMLRequest req = task.getRequest();

                if (tmlmapping.isCommNodeMappedOn(req, null)) {
                    // TraceManager.addDev("Request: " + req.getName());
                    String ret = determineRouting(tmlmapping.getHwNodeOf(req.getOriginTasks().get(0)), // tmlmapping.getHwNodeOf(req.getDestinationTask()),
                                                                                                       // req) + ",0," + req.getNbOfParams() +
                                                                                                       // ",true)" + SCCR;
                            tmlmapping.getHwNodeOf(req.getDestinationTask()), req) + ",0,true,false";
                    if (ret == null) {
                        return "Could not determine routing between " + req.getOriginTasks().get(0).getName() + " and "
                                + req.getDestinationTask().getName() + " for request " + req.getName();
                    }
                    declaration += "TMLEventBChannel<ParamType," + req.getNbOfParams() + ">* " + reqChannelInstName
                            + " = new TMLEventBChannel<ParamType," + req.getNbOfParams() + ">(" + req.getID() + ",\"" + reqChannelInstName + "\","
                            + ret;

                } else {
                    declaration += "TMLEventBChannel<ParamType," + req.getNbOfParams() + ">* " + reqChannelInstName
                            + " = new TMLEventBChannel<ParamType," + req.getNbOfParams() + ">(" + // req.getID() + ",\"reqChannel"+ task.getName() +
                                                                                                  // "\",0,0,0,0," + req.getNbOfParams() + ",true)" +
                                                                                                  // SCCR;
                            req.getID() + ",\"" + reqChannelInstName + "\",0,0,0,0,true,false";
                }

                if (req.isLossy()) {
                    declaration += "," + req.getLossPercentage() + "," + req.getMaxNbOfLoss();
                }

                declaration += ")" + SCCR;

                declaration += "addRequest( " + reqChannelInstName + ")" + SCCR;
            }
        }

        declaration += CR;

        // Set bus schedulers
        declaration += "//Set bus schedulers" + CR;

        for (final HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwBus) {
                final HwBus bus = (HwBus) node;
                final List<HwLink> busLinks = tmlmapping.getTMLArchitecture().getLinkByBus((HwBus) node);
                String devices = "";
                int numDevices = 0;

                if (!busLinks.isEmpty()) {
                    for (final HwLink link : busLinks) {
                        if (link.hwnode instanceof HwExecutionNode || link.hwnode instanceof HwBridge) {
                            // DB Issue #21: This is a bug according to the cast in the for loop line
                            if (link.hwnode instanceof HwCPU) { // || (link.hwnode instanceof HwA)){
                                final HwCPU cpu = (HwCPU) link.hwnode;

                                /*
                                 * for (int cores = 0; cores < cpu.nbOfCores; cores++) { devices +=
                                 * ", (WorkloadSource*) " + namesGen.workloadSourceInstanceName(cpu, cores,
                                 * bus); numDevices++; }
                                 */
                                devices += ", (WorkloadSource*) " + namesGen.workloadSourceInstanceName(cpu, 0, bus);
                                numDevices++;
                            } else {
                                devices += ", (WorkloadSource*) " + namesGen.workloadSourceInstanceName(link.hwnode, 0, bus);
                                numDevices++;
                            }
                        }
                    }

                    declaration += namesGen.busInstanceName(bus, 0) + "->setScheduler( (WorkloadSource*) new ";

                    if (bus.arbitration == HwBus.BASIC_ROUND_ROBIN) {
                        declaration += "RRScheduler(\"" + namesGen.rrSchedulerName(bus) + "\", 0, 5, "
                                + (int) Math.ceil(((float) node.clockRatio) / ((float) ((HwBus) node).byteDataSize)) + ", array(";
                    } else {
                        declaration += "PrioScheduler(\"" + namesGen.prioSchedulerName(bus) + "\", 0, array(";
                    }

                    declaration += numDevices + devices + "), " + numDevices + "))" + SCCR;
                }

                for (int i = 1; i < bus.pipelineSize; i++) {
                    declaration += namesGen.busInstanceName(bus, i) + "->setScheduler(" + namesGen.busInstanceName(bus, 0) + "->getScheduler(),false)"
                            + SCCR;
                }
            }
        }

        declaration += CR;

        // Declaration of Tasks
        ListIterator<HwExecutionNode> iterator = tmlmapping.getNodes().listIterator();
        declaration += "//Declaration of tasks" + CR;
        HwExecutionNode node;
        // for(TMLTask task: tmlmodeling.getTasks()) {
        // List<TMLChannel> channels;
        // List<TMLEvent> events;
        // List<TMLRequest> requests;
        int[] aStatistics = new int[8];
        Set<Integer> mappedChannels = new HashSet<Integer>();

        // DG 24.09. no mapping for tasks mapped on HwCAMS?
        for (final TMLTask task : tmlmapping.getMappedTasks()) {
            node = iterator.next();
            boolean mappedOnCPU = true;

            final String taskClassName = namesGen.taskTypeName(task);
            declaration += taskClassName + "* " + namesGen.taskInstanceName(task) + " = new " + taskClassName + "(" + task.getID() + ","
                    + task.getPriority() + ",\"" + namesGen.taskName(task) + "\", array(";

            if (node instanceof HwCPU) {
                final HwCPU hwCpu = (HwCPU) node;
                declaration += hwCpu.nbOfCores;

                /*
                 * for (int cores = 0; cores < hwCpu.nbOfCores; cores++) { declaration += "," +
                 * namesGen.cpuInstanceName(hwCpu, cores); }
                 */
                declaration += "," + namesGen.cpuInstanceName(hwCpu, hwCpu.nbOfCores);
                // declaration+= "),1" + CR;
            } else if (node instanceof HwA) {
                final HwA hwAcc = (HwA) node;
                declaration += "1 ," + namesGen.hwAccInstanceName(hwAcc);

                // DB Issue #22: copy paste error?? This causes class cast exception
                // declaration+= ((HwCPU)node).nbOfCores;
                //
                // for (int cores=0; cores< ((HwCPU)node).nbOfCores; cores++){
                // declaration+= "," + node.getName()+cores;
                // }
                //
                // declaration+= ")," + ((HwCPU)node).nbOfCores + CR;
            } else if (node instanceof HwFPGA) {
                final HwFPGA hwFpga = (HwFPGA) node;
                declaration += "1 ," + namesGen.hwFpgaInstanceName(hwFpga);
                mappedOnCPU = false;
            }
            // DB Issue #22: copy paste error?? This causes class cast exception
            // declaration+= ((HwCPU)node).nbOfCores;
            //
            // for (int cores=0; cores< ((HwCPU)node).nbOfCores; cores++){
            // declaration+= "," + node.getName()+cores;
            // }
            //
            // declaration+= ")," + ((HwCPU)node).nbOfCores + CR;

            else if (node instanceof HwCams) {
                final HwCams hwCams = (HwCams) node;
                declaration += "1 ," + namesGen.hwCamsInstanceName(hwCams);
                mappedOnCPU = true;// DG

            } else {
                throw new UnsupportedOperationException("Not implemented for " + node.getClass().getSimpleName() + "!");
            }

            declaration += "), 1" + CR;

            if (task.isDaemon()) {
                declaration += ", true";
            } else {
                declaration += ", false";
            }

            final List<TMLChannel> channels = new ArrayList<TMLChannel>(tmlmodeling.getChannels(task));
            final List<TMLEvent> events = new ArrayList<TMLEvent>(tmlmodeling.getEvents(task));
            final List<TMLRequest> requests = new ArrayList<TMLRequest>(tmlmodeling.getRequests(task));

            // TraceManager.addDev("Handling task=" + task.getTaskName());

            final MappedSystemCTask mst = new MappedSystemCTask(task, channels, events, requests, tmlmapping, mappedChannels, mappedOnCPU);
            tasks.add(mst);

            for (final TMLChannel channelb : channels) {
                declaration += "," + namesGen.channelInstanceName(channelb) + CR;
            }

            for (final TMLEvent evt : events) {
                declaration += "," + namesGen.eventInstanceName(evt) + CR;
            }

            for (final TMLRequest req : requests) {
                if (req.isAnOriginTask(task)) {
                    declaration += ", " + namesGen.requestChannelInstanceName(req.getDestinationTask()) + CR;
                }
            }

            if (task.isRequested()) {
                declaration += "," + namesGen.requestChannelInstanceName(task) + CR;
            }

            declaration += ")" + SCCR;
            declaration += "addTask(" + namesGen.taskInstanceName(task) + ")" + SCCR;
        }

        declaration += "}\n\n";

        // Declaration of TEPEs
        declaration += "void generateTEPEs(){" + CR;
        declaration += "//Declaration of TEPEs" + CR;
        tepeTranslator.generateTEPEs();
        declaration += tepeTranslator.getCode();

        // Generation of tasks
        for (MappedSystemCTask task : tasks) {
            task.determineCheckpoints(aStatistics);
            task.generateSystemC(debug, optimize);
        }

        // Declaration of TEPEs continued
        declaration += CR;
        declaration += "}\n};\n\n" + tepeTranslator.getEqFuncs();
        declaration += "#include <main.h>\n";

        // if (aStatistics[0] != 0) TraceManager.addDev("Global gain variables " + 100 *
        // aStatistics[1] / aStatistics[0]);
        // if (aStatistics[2] != 0) TraceManager.addDev("Global gain Channels " + 100 *
        // aStatistics[3] / aStatistics[2]);
        // if (aStatistics[4] != 0) TraceManager.addDev("Global gain events " + 100 *
        // aStatistics[5] / aStatistics[4]);
        // if (aStatistics[6] != 0)
        // TraceManager.addDev("Global gain checkpoints " + 100 * aStatistics[7] /
        // aStatistics[6]);

        // Declaration of EBRDDs
        /*
         * declaration += "//Declaration of EBRDDs" + CR; for(EBRDD ebrdd: ebrdds){
         * declaration += ebrdd.getName() + "* ebrdd__" + ebrdd.getName() + " = new " +
         * ebrdd.getName() + "(0, \""+ ebrdd.getName() + "\");\n"; declaration +=
         * "addEBRDD(ebrdd__"+ ebrdd.getName() +")"+ SCCR; }
         */
        return null;
    }

    private int extractPath(final List<HwCommunicationNode> path, final StrWrap masters, final StrWrap slaves, final HwNode startNode,
            final HwNode destNode, final boolean reverseIn) {
        // String firstPart="";
        HwNode firstNode = null;
        int masterCount = 0;
        boolean reverse = reverseIn;

        String pathS = "";
        for (HwCommunicationNode nodeS : path) {
            pathS += nodeS.getName() + " ";
        }
        // TraceManager.addDev("Path=" + pathS);

        if (reverseIn) {
            slaves.str += ",static_cast<Slave*>(0)";
        } else {
            // firstPart=startNode.getName() + "0";
            firstNode = startNode;
            // TraceManager.addDev("1. First node=" + firstNode);
        }

        for (final HwCommunicationNode commElem : path) {
            if (commElem instanceof HwMemory) {
                reverse = true;
                final String memoryInstName = namesGen.memoryInstanceName((HwMemory) commElem);
                slaves.str += ",static_cast<Slave*>(" + memoryInstName + "),static_cast<Slave*>(" + memoryInstName + ")";
                firstNode = null;
                // TraceManager.addDev("2. First node=" + firstNode);
                // firstPart = "";
            } else {
                if (reverse) {
                    if (firstNode == null) {
                        // if ( firstPart.length()==0 ){
                        firstNode = commElem;
                        // TraceManager.addDev("3. First node=" + firstNode);
                        // firstPart=commElem.getName();
                    } else {
                        masters.str += "," + namesGen.busMasterInstanceName(commElem, 0, (HwBus) firstNode);
                        // masters.str += "," + commElem.getName() + "_" + firstPart + "_Master";
                        masterCount++;
                        slaves.str += ",static_cast<Slave*>(" + namesGen.communicationNodeInstanceName(commElem, 0) + ")";
                        firstNode = null;
                        // TraceManager.addDev("4. First node=" + firstNode);
                        // firstPart="";
                    }
                } else {
                    if (firstNode == null) {
                        // if ( firstPart.length()==0 ){
                        firstNode = commElem;
                        // TraceManager.addDev("5. First node=" + firstNode);
                        slaves.str += ",static_cast<Slave*>(" + namesGen.communicationNodeInstanceName(commElem, 0) + ")";
                        // firstPart = commElem.getName();
                        // slaves.str+= ",static_cast<Slave*>(" + firstPart + ")";
                    } else {
                        masters.str += "," + namesGen.busMasterInstanceName(firstNode, 0, (HwBus) commElem);
                        // masters.str+= "," + firstPart + "_" + commElem.getName() + "_Master";
                        masterCount++;
                        firstNode = null;
                        // TraceManager.addDev("6. First node=" + firstNode);
                        // firstPart="";
                    }
                }
            }
        }

        if (reverse) {
            // TraceManager.addDev("REVERSE. First node=" + firstNode);
            masters.str += "," + namesGen.busMasterInstanceName(destNode, 0, (HwBus) firstNode);
            // masters.str+= "," + destNode.getName() + "0_" + firstPart + "_Master";

            return masterCount + 1;
        }

        slaves.str += ",static_cast<Slave*>(0)";

        return -masterCount;
    }

    public String determineRouting(HwNode startNode, HwNode destNode, TMLElement commElemToRoute) {
        /*
         * if (startNode == null) { TraceManager.addDev( "Null start node"); } else {
         * TraceManager.addDev( "Start node:" + startNode.getName()); }
         * 
         * if (destNode == null) { TraceManager.addDev( "Null destNode"); } else {
         * TraceManager.addDev( "destNode:" + destNode.getName()); }
         * 
         * TraceManager.addDev(
         * "******** -------> ROUTING ROUTING ROUTING\nDetermine routing from " +
         * startNode.getName() + " to " + destNode.getName () );
         */
        StrWrap masters = new StrWrap();
        StrWrap slaves = new StrWrap();

        List<HwCommunicationNode> path = new LinkedList<HwCommunicationNode>();
        List<HwCommunicationNode> commNodes = new LinkedList<HwCommunicationNode>();

        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwCommunicationNode) {
                commNodes.add((HwCommunicationNode) node);
            }
        }
        // if( startNode == null ) {
        // TraceManager.addDev( "NULL REFERENCE" );
        // }
        // else {
        // //TraceManager.addDev( "startNode: " + startNode.getName() );
        // }

        HwMemory memory = getMemConnectedToBusChannelMapped(commNodes, null, commElemToRoute);

        if (memory == null) {
            TraceManager.addDev("no memories to map");
            exploreBuses(0, commNodes, path, startNode, destNode, commElemToRoute);
        } else {
            final List<HwCommunicationNode> commNodes2 = new LinkedList<HwCommunicationNode>(commNodes);

            // TraceManager.addDev("Explore bus from " + startNode.getName() + " to memory "
            // + memory.getName());

            if (!exploreBuses(0, commNodes, path, startNode, memory, commElemToRoute)) {
                TraceManager.addDev("NO route to " + memory.getName() + " found!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            path.add(memory);

            exploreBuses(0, commNodes2, path, memory, destNode, commElemToRoute);
        }

        int hopNum = extractPath(path, masters, slaves, startNode, destNode, false);

        if (hopNum < 0) {
            hopNum = extractPath(path, masters, slaves, destNode, destNode, true) - hopNum;
        }

        /*
         * TraceManager.addDev(commElemToRoute.getName() + " is mapped on:");
         * 
         * for (HwCommunicationNode commElem : path) {
         * TraceManager.addDev(commElem.getName()); }
         */

        // TraceManager.addDev("number of elements: " + hopNum);
        // TraceManager.addDev("masters: " + masters.str);
        // TraceManager.addDev("slaves: " + slaves.str);

        if (masters.str.length() == 0) {
            return null;
        }

        // TraceManager.addDev("Going to return:" + hopNum + ",array(" + hopNum +
        // masters.str + "),array(" + hopNum + slaves.str + ")");

        return hopNum + ",array(" + hopNum + masters.str + "),array(" + hopNum + slaves.str + ")";
    }

    private boolean exploreBuses(final int depth, final List<HwCommunicationNode> commNodes, final List<HwCommunicationNode> path,
            final HwNode startNode, final HwNode destNode, final TMLElement commElemToRoute) {
        assert startNode != null : "Parameter 'startNode' should not be null.";

        // first called with Maping:getCommunicationNodes
        List<HwCommunicationNode> nodesToExplore;
        // TraceManager.addDev("No of comm nodes " + commNodes.size());
        // TraceManager.addDev("startNode=" + startNode);
        boolean busExploreMode = ((depth & 1) == 0);

        if (busExploreMode) {
            // TraceManager.addDev("search for buses connected to " + startNode.getName());
            nodesToExplore = getBusesConnectedToNode(commNodes, startNode);
        } else {
            // TraceManager.addDev("search for bridges connected to: " +
            // startNode.getName());
            nodesToExplore = getBridgesConnectedToBus(commNodes, (HwBus) startNode);
        }

        // TraceManager.addDev("no of elements found: " + nodesToExplore.size());

        for (HwCommunicationNode currNode : nodesToExplore) {
            // memory = null;
            if (busExploreMode) {
                // memory = getMemConnectedToBusChannelMapped(commNodes, (HwBus)currNode,
                // commElemToRoute);
                if (isBusConnectedToNode(currNode, destNode)) {
                    // TraceManager.addDev(currNode.getName() + " is last node");
                    path.add(currNode);
                    // if (memory!=null) path.add(memory);
                    commNodes.remove(currNode);
                    return true;
                }
            }
            if (tmlmapping.isCommNodeMappedOn(commElemToRoute, currNode)) {
                // TraceManager.addDev(currNode.getName() + " mapping found for " +
                // commElemToRoute.getName());
                path.add(currNode);
                // if (memory!=null) path.add(memory);
                commNodes.remove(currNode);
                if (exploreBuses(depth + 1, commNodes, path, currNode, destNode, commElemToRoute))
                    return true;
                path.remove(currNode);
                // if (memory!=null) path.remove(memory);
                commNodes.add(currNode);
            }
        }
        for (HwCommunicationNode currNode : nodesToExplore) {
            // if (busExploreMode) memory = getMemConnectedToBusChannelMapped(commNodes,
            // (HwBus)currNode, commElemToRoute); else memory=null;
            path.add(currNode);
            // if (memory!=null) path.add(memory);
            commNodes.remove(currNode);

            // TraceManager.addDev(currNode.getName());
            if (exploreBuses(depth + 1, commNodes, path, currNode, destNode, commElemToRoute))
                return true;
            path.remove(currNode);
            // if (memory!=null) path.remove(memory);
            commNodes.add(currNode);
        }
        return false;
    }

    private HwMemory getMemConnectedToBusChannelMapped(List<HwCommunicationNode> _commNodes, HwBus _bus, TMLElement _channel) {
        for (HwCommunicationNode commNode : _commNodes) {
            if (commNode instanceof HwMemory) {
                if (_bus != null) {
                    TraceManager.addDev(commNode.getName() + " connected to bus " + _bus.getName() + ": "
                            + tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus));
                }
                // TraceManager.addDev(_channel.getName() + " is mapped onto " +
                // commNode.getName() + ": " + tmlmapping.isCommNodeMappedOn(_channel,
                // commNode));
                if ((_bus == null || tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus))
                        && tmlmapping.isCommNodeMappedOn(_channel, commNode)) {
                    return (HwMemory) commNode;
                }
            }
        }
        return null;
    }

    private List<HwCommunicationNode> getBusesConnectedToNode(List<HwCommunicationNode> _commNodes, HwNode _node) {
        List<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
        for (HwCommunicationNode commNode : _commNodes) {
            if (commNode instanceof HwBus) {
                if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(_node, (HwBus) commNode))
                    resultList.add(commNode);
            }
        }
        return resultList;
    }

    private List<HwCommunicationNode> getBridgesConnectedToBus(List<HwCommunicationNode> _commNodes, HwBus _bus) {
        List<HwCommunicationNode> resultList = new LinkedList<HwCommunicationNode>();
        for (HwCommunicationNode commNode : _commNodes) {
            if ((commNode instanceof HwBridge) || (commNode instanceof HwNoC)) {
                if (tmlmapping.getTMLArchitecture().isNodeConnectedToBus(commNode, _bus))
                    resultList.add(commNode);
            }
        }
        return resultList;
    }

    private boolean isBusConnectedToNode(HwCommunicationNode commNode, HwNode node) {
        for (HwLink link : tmlmapping.getTMLArchitecture().getHwLinks()) {
            if (link.bus == commNode && link.hwnode == node)
                return true;
        }
        return false;
    }

    private void generateTaskFiles(String path) throws FileException {
        for (MappedSystemCTask mst : tasks) {
            mst.saveInFiles(path);
        }
    }

    public MappedSystemCTask getMappedTaskByName(String iName) {
        for (MappedSystemCTask task : tasks) {
            if (task.getTMLTask().getName().equals(iName))
                return task;
        }
        return null;
    }

    public List<HwCommunicationNode> determineRoutingPath(HwNode startNode, HwNode destNode, TMLElement commElemToRoute) {
        /*
         * if (startNode == null) { TraceManager.addDev( "Null start node"); } else {
         * TraceManager.addDev( "Start node:" + startNode.getName()); }
         * 
         * if (destNode == null) { TraceManager.addDev( "Null destNode"); } else {
         * TraceManager.addDev( "destNode:" + destNode.getName()); }
         * 
         * TraceManager.addDev(
         * "******** -------> ROUTING ROUTING ROUTING\nDetermine routing from " +
         * startNode.getName() + " to " + destNode.getName () );
         */
        StrWrap masters = new StrWrap();
        StrWrap slaves = new StrWrap();

        List<HwCommunicationNode> path = new LinkedList<HwCommunicationNode>();
        List<HwCommunicationNode> commNodes = new LinkedList<HwCommunicationNode>();

        for (HwNode node : tmlmapping.getTMLArchitecture().getHwNodes()) {
            if (node instanceof HwCommunicationNode) {
                commNodes.add((HwCommunicationNode) node);
            }
        }
        // if( startNode == null ) {
        // TraceManager.addDev( "NULL REFERENCE" );
        // }
        // else {
        // //TraceManager.addDev( "startNode: " + startNode.getName() );
        // }

        HwMemory memory = getMemConnectedToBusChannelMapped(commNodes, null, commElemToRoute);

        if (memory == null) {
            TraceManager.addDev("no memories to map");
            exploreBuses(0, commNodes, path, startNode, destNode, commElemToRoute);
        } else {
            final List<HwCommunicationNode> commNodes2 = new LinkedList<HwCommunicationNode>(commNodes);

            // TraceManager.addDev("Explore bus from " + startNode.getName() + " to memory "
            // + memory.getName());

            if (!exploreBuses(0, commNodes, path, startNode, memory, commElemToRoute)) {
                TraceManager.addDev("NO route to " + memory.getName() + " found!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            path.add(memory);

            exploreBuses(0, commNodes2, path, memory, destNode, commElemToRoute);
        }

        int hopNum = extractPath(path, masters, slaves, startNode, destNode, false);

        if (hopNum < 0) {
            hopNum = extractPath(path, masters, slaves, destNode, destNode, true) - hopNum;
        }

        /*
         * TraceManager.addDev(commElemToRoute.getName() + " is mapped on:");
         * 
         * for (HwCommunicationNode commElem : path) {
         * TraceManager.addDev(commElem.getName()); }
         */

        // TraceManager.addDev("number of elements: " + hopNum);
        // TraceManager.addDev("masters: " + masters.str);
        // TraceManager.addDev("slaves: " + slaves.str);

        if (masters.str.length() == 0) {
            return null;
        }

        // TraceManager.addDev("Going to return:" + hopNum + ",array(" + hopNum +
        // masters.str + "),array(" + hopNum + slaves.str + ")");

        return path;
    }
}
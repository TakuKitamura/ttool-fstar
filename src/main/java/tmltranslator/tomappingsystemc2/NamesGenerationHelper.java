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

import myutil.TraceManager;
import tmltranslator.*;

import java.util.Arrays;
import java.util.Collection;

public class NamesGenerationHelper {

    private static final String[] RESERVED_WORDS = {
            "IndeterminismSource",
            "Parameter",
            "TMLActionCommand",
            "TMLbrbwChannel",
            "TMLChannel",
            "TMLChoiceCommand",
            "TMLCommand",
            "TMLEventBChannel",
            "TMLEventFChannel",
            "TMLEventSizedChannel",
            "TMLExeciCommand",
            "TMLExeciRangeCommand",
            "TMLnbrnbwChannel",
            "TMLNotifiedCommand",
            "TMLRandomChoiceCommand",
            "TMLRandomCommand",
            "TMLReadCommand",
            "TMLRequestCommand",
            "TMLSelectCommand",
            "TMLSendCommand",
            "TMLStateChannel",
            "TMLStopCommand",
            "TMLTask",
            "TMLWaitCommand",
            "TMLWriteCommand",
            "TMLWriteMultCommand",

            "GeneralListener",
            "SimComponents",
            "RunXTransactions",
            "Breakpoint",
            "CondBreakpoint",
            "RunTillNextRandomChoice",
            "RunXCommands",
            "RunXTimeUnits",
            "RunTillTransOnDevice",
            "RunTillTransOnTask",
            "RunTillTransOnChannel",
            "TEPESigListener",
            "TEPEFloatingSigListener",
            "TEPEEquationListener",
            "TEPESettingListener",
            "ListenerSubject",

            "Server",
            "ServerExplore",
            "ServerIF",
            "ServerLocal",
            "SimComponents",
            "SimServSyncInfo",
            "Simulator",

            "AliasConstraint",
            "EqConstraint",
            "FSMConstraint",
            "LogConstraint",
            "PropertyConstraint",
            "PropertyStateConstraint",
            "PropLabConstraint",
            "PropRelConstraint",
            "SeqConstraint",
            "SignalConstraint",
            "ThreeSigConstraint",
            "TimeMMConstraint",
            "TimeTConstraint",
            "TwoSigConstraint",

            "Comment",
            "SignalChangeData",
            "MemPool",
            "MemPoolNoDel",
            "Serializable",
            "TMLTransaction",

            "WAIT_SEND_VLEN",
            "CLOCK_INC",
            "BLOCK_SIZE_TRANS",
            "BLOCK_SIZE_PARAM",
            "BLOCK_SIZE_COMMENT",
            "PARAMETER_BLOCK_SIZE",
            "NO_EVENTS_TO_LOAD",
            "PORT",
            "BACKLOG",
            "UNKNOWN",
            "TERMINATED",
            "RUNNING",
            "RUNNABLE",
            "SUSPENDED",
            "INT_MSB",
            "TAG_HEADER",
            "TAG_STARTo",
            "TAG_STARTc",
            "TAG_ERRNOo",
            "TAG_ERRNOc",
            "TAG_MSGo",
            "TAG_MSGc",
            "TAG_TIMEo",
            "TAG_TIMEc",
            "TAG_SIMDURo",
            "TAG_SIMDURc",
            "TAG_CYCLESo",
            "TAG_CYCLESc",
            "TAG_TASKo",
            "TAG_TASKc",
            "TAG_TSKSTATEo",
            "TAG_TSKSTATEc",
            "TAG_VARo",
            "TAG_VARc",
            "TAG_STATUSo",
            "TAG_STATUSc",
            "TAG_REASONo",
            "TAG_REASONc",
            "TAG_GLOBALo",
            "TAG_GLOBALc",
            "TAG_CURRCMDo",
            "TAG_CURRCMDc",
            "TAG_CMDo",
            "TAG_CMDc",
            "TAG_EXECTIMESo",
            "TAG_EXECTIMESc",
            "TAG_STARTTIMEo",
            "TAG_STARTTIMEc",
            "TAG_FINISHTIMEo",
            "TAG_FINISHTIMEc",
            "TAG_STARTTIMETRANSo",
            "TAG_STARTTIMETRANSc",
            "TAG_FINISHTIMETRANSo",
            "TAG_FINISHTIMETRANSc",
            "TAG_BREAKCMDo",
            "TAG_BREAKCMDc",
            "TAG_NEXTCMDo",
            "TAG_NEXTCMDc",
            "TAG_HASHo",
            "TAG_HASHc",
            "TAG_BRANCHo",
            "TAG_BRANCHc",
            "TAG_REPLYo",
            "TAG_REPLYc",
            "TAG_EXTIMEo",
            "TAG_EXTIMEc",
            "TAG_CONTDELo",
            "TAG_CONTDELc",
            "TAG_TRANSo",
            "TAG_TRANSc",
            "TAG_TRANSACTION_NBo",
            "TAG_TRANSACTION_NBc",
            "TAG_BUSo",
            "TAG_BUSc",
            "TAG_CHANNELo",
            "TAG_CHANNELc",
            "TAG_TOWRITEo",
            "TAG_TOWRITEc",
            "TAG_TOREADo",
            "TAG_TOREADc",
            "TAG_CONTENTo",
            "TAG_CONTENTc",
            "TAG_PARAMo",
            "TAG_PARAMc",
            "TAG_Pxo",
            "TAG_Pxc",
            "TAG_UTILo",
            "TAG_UTILc",
            "TAG_CPUo",
            "TAG_CPUc",
            "TAG_PROGRESSo",
            "TAG_PROGRESSc",
            "TAG_CURRTASKo",
            "TAG_CURRTASKc",
            "TAG_ENERGYo",
            "TAG_ENERGYc",

            "Bridge",
            "Bus",
            "CPU",
            "Memory",
            "PrioScheduler",
            "RRPrioScheduler",
            "RRScheduler",
            "SchedulableCommDevice",
            "SchedulableDevice",
            "SingleCoreCPU",
            "Slave",
            "TraceableDevice",
            "WorkloadSource",
    };

    private static final Collection<String> RESERVED_WORDS_LIST = Arrays.asList(RESERVED_WORDS);


    public static final String LEFT_ANGLE_RACKET = "<";
    public static final String RIGHT_ANGLE_RACKET = ">";

    public static final NamesGenerationHelper INSTANCE = new NamesGenerationHelper();

    private NamesGenerationHelper() {
    }

    String cppFileName(final String name) {
        return CPPCodeGenerationHelper.cppFileName(name);
    }

    String headerFileName(final String name) {
        return CPPCodeGenerationHelper.headerFileName(name);
    }

    String hardwareNodeInstanceName(final HwNode element,
                                    final int index) {
        if (element instanceof HwCommunicationNode) {
            return communicationNodeInstanceName((HwCommunicationNode) element, index);
        }

        if (element instanceof HwExecutionNode) {
            return executionNodeInstanceName((HwExecutionNode) element, index);
        }

        throw new UnsupportedOperationException();
    }

    String communicationNodeInstanceName(final HwCommunicationNode element,
                                         final int index) {
        if (element instanceof HwMemory) {
            return memoryInstanceName((HwMemory) element);
        }

        if (element instanceof HwBus) {
            return busInstanceName((HwBus) element, index);
        }

        if (element instanceof HwBridge) {
            return bridgeInstanceName((HwBridge) element);
        }

        if (element instanceof HwNoC) {
            return hwNoCInstanceName((HwNoC) element);
        }

        throw new UnsupportedOperationException();
    }

    String prioSchedulerInstanceName(final HwCPU element) {
        return normalize(element.getName() + "_scheduler");
    }

    String prioSchedulerName(final HwBus element) {
        return element.getName() + "_PrioSched";
    }

    String prioSchedulerName(final HwCPU element) {
        return element.getName() + "_PrioSched";
    }

    String rrSchedulerInstanceName(final HwExecutionNode element) {
        return normalize(element.getName() + "_scheduler");
    }

    String rrSchedulerName(final HwBus element) {
        return element.getName() + "_RRSched";
    }

    String rrSchedulerName(final HwExecutionNode element) {
        return element.getName() + "_RRSched";
    }

    String executionNodeInstanceName(final HwExecutionNode element,
                                     final int index) {
        if (element instanceof HwCPU) {
            return cpuInstanceName((HwCPU) element, index);
        }

        if (element instanceof HwA) {
            return hwAccInstanceName((HwA) element);
        }

        if (element instanceof HwFPGA) {
            return hwFpgaInstanceName((HwFPGA) element);
        }

        throw new UnsupportedOperationException("Unknown execution node type: " + String.valueOf(element));
    }

    String cpuInstanceName(final HwCPU element,
                           final int indexCore) {
        return normalize(cpuName(element, indexCore));
    }

    String cpuName(final HwCPU element,
                   final int indexCore) {
        return element.getName() + indexSuffix(indexCore);
    }

    String hwAccInstanceName(final HwA element) {
        return normalize(hwAccName(element));
    }

    String hwAccName(final HwA element) {
        return element.getName();
    }

    String hwFpgaInstanceName(final HwFPGA element) {
        return normalize(hwFpgaName(element));
    }

    String hwFpgaName(final HwFPGA element) {
        return element.getName();
    }

    String busInstanceName(final HwBus element,
                           final int pipelineIndex) {
        return normalize(busName(element, pipelineIndex));
    }

    String busName(final HwBus element,
                   final int pipelineIndex) {
        return element.getName() + indexSuffix(pipelineIndex);
    }

    String bridgeInstanceName(final HwBridge element) {
        return normalize(bridgeName(element));
    }

    String hwNoCInstanceName(final HwNoC element) {
        return normalize(hwNocName(element));
    }

    String bridgeName(final HwBridge element) {
        return element.getName();
    }

    String hwNocName(final HwNoC element) {
        return element.getName();
    }

    String memoryInstanceName(final HwMemory element) {
        return normalize(memoryName(element));
    }

    String memoryName(final HwMemory element) {
        return element.getName();
    }

    String busMasterInstanceName(final HwNode element,
                                 final int indexCore,
                                 final HwBus linkBus) {
        if (element == null) {
            TraceManager.addDev("NULL Hw element");
        } else {
            TraceManager.addDev("Hw element=" + element.getName());
        }

        if (linkBus == null) {
            TraceManager.addDev("NULL linkBus element");
        } else {
            TraceManager.addDev("linkBus element=" + linkBus.getName());
        }

        return normalize(busMasterName(element, indexCore, linkBus));
    }

    String busMasterName(final HwNode element,
                         final int indexCore,
                         final HwBus linkBus) {
        return element.getName() + indexSuffix(indexCore) + "_" + linkBus.getName() + "_Master";
    }

    String channelTypeName(final TMLChannel element) {
        switch (element.getType()) {
            case TMLChannel.BRBW:
                return "TMLbrbwChannel";
            case TMLChannel.BRNBW:
                return "TMLbrnbwChannel";
            case TMLChannel.NBRNBW:
            default:
                return "TMLnbrnbwChannel";
        }
    }

    String channelInstanceName(final TMLChannel element) {
        return CPPCodeGenerationHelper.normalize(element.getExtendedName());
    }

    String channelName(final TMLChannel element) {
        return element.getName();
    }

    String eventTypeName(final TMLEvent evt) {
        final String typeName;

        if (evt.isInfinite()) {
            typeName = "TMLEventBChannel";
        } else if (evt.isBlocking()) {
            typeName = "TMLEventFBChannel";
        } else {
            typeName = "TMLEventFChannel";
        }

        return typeName + LEFT_ANGLE_RACKET + "ParamType," + evt.getNbOfParams() + RIGHT_ANGLE_RACKET;
    }

    String eventInstanceName(final TMLEvent element) {
        return normalize(element.getExtendedName());
    }

    String eventName(final TMLEvent element) {
        return element.getName();
    }

    String requestChannelInstanceName(final TMLTask element) {
        return normalize("reqChannel_" + element.getName());
    }

    String schedComDeviceInstanceName(final HwBus element,
                                      final int index) {
        return normalize(element.getName() + indexSuffix(index));
    }

    String taskTypeName(final TMLTask element) {
        return normalize(element.getName());
    }

    String taskInstanceName(final TMLTask element) {
        return normalize("task__" + taskName(element));
    }

    String taskName(final TMLTask element) {
        return element.getName();
    }

    String workloadSourceInstanceName(final HwNode linkedHwNode,
                                      final int indexCore,
                                      final HwBus bus) {
        return busMasterInstanceName(linkedHwNode, indexCore, bus);
    }

    private String indexSuffix(final int index) {
        return index >= 0 ? "_" + index : "";
    }

    private static String normalize(final String name) {
        return CPPCodeGenerationHelper.normalize(removeReservedWords(name));
    }

    private static String removeReservedWords(final String name) {
        if (RESERVED_WORDS_LIST.contains(name)) {
            return normalize(name + CPPCodeGenerationHelper.NORMALIZATION_SUFFIX);
        }

        return name;
    }
}

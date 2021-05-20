/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT nokia.com
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

package tmltranslator.modelcompiler;

import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import tmltranslator.*;
import ui.GTMLModeling;
import ui.TMLCommunicationPatternPanel;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This class is the one of the model compiler that produces the control code of
 * a system design Class TMLModelCompiler Creation: 09/02/2014
 *
 * @author Andrea ENRICI
 * @version 1.0 09/02/2014
 */
public class TMLModelCompiler implements CCodeGenConstants {

    public String directory;

    // private String CR = "\n";
    // private String CR2 = "\n\n";
    // private String TAB = "\t";
    // private String TAB2 = "\t\t";
    // private String TAB3 = "\t\t\t";
    // private String TAB4 = "\t\t\t\t";
    // private String SP = " ";
    // private String SC = ";";
    // private String COLON = ",";

    private TMLMapping<?> tmap;
    // private TMLCP tmlcp;
    private TMLModeling<?> tmlm;
    private TMLArchitecture tmla;
    private String applicationName;
    private StringBuffer mainFileString;
    private StringBuffer headerString;
    private StringBuffer programString;
    private StringBuffer initFileString;
    // private ArrayList<TMLTask> mappedTasks;
    // private ArrayList<TMLElement> commElts;
    private List<Operation> operationsList;
    // private int SDRoperationsCounter;
    // private int signalsCounter;
    private List<Signal> signalsList;
    private List<TMLCPLib> mappedCPLibs;
    private List<TMLPort> postexList;
    private List<TMLPort> prexList;
    private List<Buffer> buffersList;
    private List<DataTransfer> dataTransfersList;
    private List<TMLCommunicationPatternPanel> tmlcpps;
    private List<TMLCP> tmlcpsList;

    // private ArrayList<TMLModelCompilerError> errors;
    // private ArrayList<TMLModelCompilerError> warnings;

    private String debugFileName;
    PrintWriter outputStream;

    public JFrame frame; // Main Frame

    public TMLModelCompiler(String _directory, String _applicationName, JFrame _frame,
            List<TMLCommunicationPatternPanel> _tmlcpps, TMLMapping<?> _tmap) {
        directory = _directory;
        applicationName = _applicationName;
        frame = _frame;
        tmap = _tmap;
        tmlcpps = _tmlcpps;
        tmap.linkTasks2TMLChannels();
        tmap.linkTasks2TMLEvents();
        tmlm = _tmap.getTMLModeling();
        tmla = _tmap.getTMLArchitecture();
        mappedCPLibs = _tmap.getMappedTMLCPLibs();
        init();
    }

    private void init() {
        // mappedTasks = new ArrayList<TMLTask>();
        // commElts = new ArrayList<TMLElement>();
        // errors = new ArrayList<TMLModelCompilerError>();
        mainFileString = new StringBuffer();
        headerString = new StringBuffer();
        programString = new StringBuffer();
        initFileString = new StringBuffer();
        operationsList = new ArrayList<Operation>();
        // SDRoperationsCounter = 0;
        // signalsCounter = 0;
        signalsList = new ArrayList<Signal>();
        postexList = new ArrayList<TMLPort>();
        prexList = new ArrayList<TMLPort>();
        buffersList = new ArrayList<Buffer>();
        dataTransfersList = new ArrayList<DataTransfer>();
        debugFileName = directory + "debugFile.txt";
        tmlcpsList = new ArrayList<TMLCP>();
    }

    public void toTextFormat() {

        List<TMLTask> mappedTasks = tmap.getMappedTasks();
        // List<TMLElement> commElts = tmap.getMappedCommunicationElement();

        // Create the data structures for signals, buffers, operations and data
        // transfers
        openDebugFile();
        makeCommunicationPatternsList();
        makeSignalsList(); // make the signals associated to operations, based on the tasks of operations

        for (Signal sig : signalsList) {
            // TraceManager.addDev( sig.toString() + CR );
            appendToDebugFile(sig.toString() + CR2);
        }
        makeOperationsList(mappedTasks); // make the list of operations based on the tasks in the app model
        setMappingParametersToBuffers();
        for (Buffer buff : buffersList) {
            // TraceManager.addDev( buff.toString() + CR );
            appendToDebugFile(buff.toString() + CR);
        }
        makeDataTransfersList();
        for (DataTransfer dt : dataTransfersList) {
            // TraceManager.addDev( dt.toString() );
            appendToDebugFile(dt.toString());
        }
        appendToDebugFile("\n");
        for (Operation op : operationsList) {
            // TraceManager.addDev( op.toString() );
            appendToDebugFile(op.toString() + CR);
        }
        closeDebugFile();

        // Generate the C code
        generateMainFile();
        generateHeaderFile(mappedTasks);
        generateProgramFile();
        generateInitFile(mappedTasks);
    }

    /**********************************************************************************
     * CREATION OF DATA STRUCTURE PART
     *********************************************************************************/
    // From the list of mapped tasks, built the list of operations. For SDR
    // operations, only F_ tasks are considered.
    private void makeOperationsList(List<TMLTask> mappedTasks) {

        List<TMLTask> SDRXtasks = new ArrayList<TMLTask>();
        List<TMLTask> SDRFtasks = new ArrayList<TMLTask>();
        Buffer inBuffer, outBuffer;
        Signal outSignal;
        List<Signal> inSignals;
        String[] s;
        boolean[] prexPostexList = new boolean[2];

        for (TMLTask task : mappedTasks) {
            String taskName = task.getName().split("__")[1];
            s = taskName.split("X_");
            if (s.length > 1) { // we are splitting an eXecution task
                SDRXtasks.add(task);
            } else {
                s = taskName.split("F_");
                if (s.length > 1) { // we are splitting a Firing task
                    SDRFtasks.add(task);
                }
            }
        }
        // Now couple the tasks to create SDRoperations
        for (TMLTask fTask : SDRFtasks) {
            String fTaskName = fTask.getName().split("__")[1].split("F_")[1];
            for (TMLTask xTask : SDRXtasks) {
                String xTaskName = xTask.getName().split("__")[1].split("X_")[1];
                if (xTaskName.equals(fTaskName)) {
                    // Mind that signals are based on channels NOT on events!
                    prexPostexList = getPrexPostex(xTask);
                    inSignals = getInSignals(xTask); // is null for Source operation
                    outSignal = getOutSignal(xTask); // is null for Sink operation
                    // Get the ports of channels and associated them to buffers
                    inBuffer = createInBuffer(xTask, tmap.getHwNodeOf(xTask)); // null for Source
                    outBuffer = createOutBuffer(xTask, tmap.getHwNodeOf(xTask)); // null for Sink
                    operationsList.add(new Operation(fTask, xTask, tmap.getHwNodeOf(xTask), tmap.getHwNodeOf(fTask),
                            inSignals, outSignal, inBuffer, outBuffer, prexPostexList[0], prexPostexList[1]));
                    // SDRoperationsCounter++;
                }
            }
        }
    }

    private boolean[] getPrexPostex(TMLTask xTask) {

        boolean[] list = new boolean[2];

        for (TMLChannel ch : xTask.getWriteTMLChannels()) { // test all channels
            if (ch.isBasicChannel()) {
                if (ch.getOriginPort().isPrex()) {
                    list[0] = true;
                    list[1] = false;
                    return list;
                }
            }
            if (ch.isAForkChannel()) {
                if (ch.getOriginPorts().get(0).isPrex()) {
                    list[0] = true;
                    list[1] = false;
                    return list;
                }
            }
        }
        for (TMLChannel ch : xTask.getReadTMLChannels()) { // test all channels
            if (ch.isBasicChannel()) {
                if (ch.getDestinationPort().isPostex()) {
                    list[0] = false;
                    list[1] = true;
                    return list;
                }
            }
            if (ch.isAJoinChannel()) {
                if (ch.getDestinationPorts().get(0).isPostex()) {
                    list[0] = false;
                    list[1] = true;
                    return list;
                }
            }
        }
        return list;
    }

    // Create the inBuffer from the port of the read channel associated to the xTask
    private Buffer createInBuffer(TMLTask xTask, HwNode node) {

        if (xTask.getReadTMLChannels().size() > 0) {
            TMLChannel readChannel = xTask.getReadTMLChannels().get(0);
            ArchUnitMEC mec = node.getArchUnitMEC();
            Buffer buff = new Buffer();
            int i;
            if (readChannel.isBasicChannel()) {
                if (mec instanceof FepMEC) {
                    buff = new FepBuffer("buff_" + readChannel.getDestinationPort().getName(), xTask);
                } else if (mec instanceof MapperMEC) {
                    buff = new MapperBuffer("buff_" + readChannel.getDestinationPort().getName(), xTask);
                } else if (mec instanceof InterleaverMEC) {
                    buff = new InterleaverBuffer("buff_" + readChannel.getDestinationPort().getName(), xTask);
                } else if (mec instanceof AdaifMEC) {
                    buff = new MMBuffer("buff_" + readChannel.getDestinationPort().getName(), xTask);
                } else if (mec instanceof CpuMEC) {
                    buff = new MMBuffer("buff_" + readChannel.getDestinationPort().getName(), xTask);
                }
                buffersList.add(buff); // this line has to be commented if ch_in buffers are to be excluded from the C
                                       // code
                return buff;
            } else {
                for (i = 0; i < readChannel.getDestinationTasks().size(); i++) {
                    if (readChannel.getDestinationTasks().get(i).getName().equals(xTask.getName())) {
                        break;
                    }
                }
                if (mec instanceof FepMEC) {
                    buff = new FepBuffer("buff_" + readChannel.getDestinationPorts().get(i).getName(), xTask);
                } else if (mec instanceof MapperMEC) {
                    buff = new MapperBuffer("buff_" + readChannel.getDestinationPorts().get(i).getName(), xTask);
                } else if (mec instanceof InterleaverMEC) {
                    buff = new InterleaverBuffer("buff_" + readChannel.getDestinationPorts().get(i).getName(), xTask);
                } else if (mec instanceof AdaifMEC) {
                    buff = new MMBuffer("buff_" + readChannel.getDestinationPorts().get(i).getName(), xTask);
                } else if (mec instanceof CpuMEC) {
                    buff = new MMBuffer("buff_" + readChannel.getDestinationPorts().get(i).getName(), xTask);
                }
                buffersList.add(buff); // this line has to be commented if ch_in buffers are to be excluded from the C
                                       // code
                return buff;
            }
        }
        return null;
    }

    // Create the inBuffer from the port of the write channel associated to the
    // xTask
    private Buffer createOutBuffer(TMLTask xTask, HwNode node) {

        if (xTask.getWriteTMLChannels().size() > 0) {
            TMLChannel writeChannel = xTask.getWriteTMLChannels().get(0);
            ArchUnitMEC mec = node.getArchUnitMEC();
            Buffer buff = new Buffer();
            int i;
            if (writeChannel.isBasicChannel()) {
                if (mec instanceof FepMEC) {
                    buff = new FepBuffer("buff_" + writeChannel.getOriginPort().getName(), xTask);
                } else if (mec instanceof MapperMEC) {
                    buff = new MapperBuffer("buff_" + writeChannel.getOriginPort().getName(), xTask);
                } else if (mec instanceof InterleaverMEC) {
                    buff = new InterleaverBuffer("buff_" + writeChannel.getOriginPort().getName(), xTask);
                } else if (mec instanceof AdaifMEC) {
                    buff = new MMBuffer("buff_" + writeChannel.getOriginPort().getName(), xTask);
                } else if (mec instanceof CpuMEC) {
                    buff = new MMBuffer("buff_" + writeChannel.getOriginPort().getName(), xTask);
                }
                buffersList.add(buff);
                return buff;
            } else {
                for (i = 0; i < writeChannel.getOriginTasks().size(); i++) {
                    if (writeChannel.getOriginTasks().get(i).getName().equals(xTask.getName())) {
                        break;
                    }
                }
                if (mec instanceof FepMEC) {
                    buff = new FepBuffer("buff_" + writeChannel.getOriginPorts().get(i).getName(), xTask);
                } else if (mec instanceof MapperMEC) {
                    buff = new MapperBuffer("buff_" + writeChannel.getOriginPorts().get(i).getName(), xTask);
                } else if (mec instanceof InterleaverMEC) {
                    buff = new InterleaverBuffer("buff_" + writeChannel.getOriginPorts().get(i).getName(), xTask);
                } else if (mec instanceof AdaifMEC) {
                    buff = new MMBuffer("buff_" + writeChannel.getOriginPorts().get(i).getName(), xTask);
                } else if (mec instanceof CpuMEC) {
                    buff = new MMBuffer("buff_" + writeChannel.getOriginPorts().get(i).getName(), xTask);
                }
                buffersList.add(buff);
                return buff;
            }
        }
        return null;
    }

    private ArrayList<Signal> getInSignals(TMLTask task) { // Find the signal associated to the write channel of task

        TMLPort originPort, destinationPortCh, /* originPortSigChannel, */ destinationPortSigChannel;
        TMLChannel sigChannel;
        ArrayList<Signal> sigsList = new ArrayList<Signal>();

        for (TMLChannel ch : task.getReadTMLChannels()) {
            if (ch.isBasicChannel()) {
                originPort = ch.getOriginPort();
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isBasicChannel()) {
                        if (sigChannel.getOriginPort().getName().equals(originPort.getName())) {
                            sigsList.add(sig);
                        }
                    }
                }
            } else if (ch.isAForkChannel()) {
                originPort = ch.getOriginPorts().get(0);
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isAForkChannel()) {
                        if (sigChannel.getOriginPorts().get(0).getName().equals(originPort.getName())) {
                            sigsList.add(sig);
                        }
                    }
                }
            } else if (ch.isAJoinChannel()) { // take all signals that match the destination port of the join channel
                destinationPortCh = ch.getDestinationPorts().get(0);
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isAForkChannel()) {
                        destinationPortSigChannel = sigChannel.getDestinationPorts().get(0);
                    } else { // there are no join channels associated to signals
                        destinationPortSigChannel = sigChannel.getDestinationPort();
                    }
                    if (destinationPortCh.getName().equals(destinationPortSigChannel.getName())) {
                        sigsList.add(sig);
                    }
                }
            }
        }
        return sigsList;
    }

    private Signal getOutSignal(TMLTask task) { // Find the signal associated to the write channel of task

        TMLPort originPort, /* destinationPort, */ originPortSigChannel;
        TMLChannel sigChannel;

        for (TMLChannel ch : task.getWriteTMLChannels()) {
            if (ch.isBasicChannel()) {
                originPort = ch.getOriginPort();
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isBasicChannel()) {
                        if (sigChannel.getOriginPort().getName().equals(originPort.getName())) {
                            return sig;
                        }
                    }
                }
            } else if (ch.isAForkChannel()) {
                originPort = ch.getOriginPorts().get(0);
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isAForkChannel()) {
                        if (sigChannel.getOriginPorts().get(0).getName().equals(originPort.getName())) {
                            return sig;
                        }
                    }
                }
            } else if (ch.isAJoinChannel()) { // join channels are tricky because of the multiple source tasks and
                                              // ports.
                ArrayList<TMLTask> tasksList = ch.getOriginTasks(); // get the index of the origin port
                int i;
                for (i = 0; i < tasksList.size(); i++) {
                    if (tasksList.get(i).getName().equals(task.getName())) {
                        break; // i is the index of the correct origin port
                    }
                }
                TMLPort sourcePortCh = ch.getOriginPorts().get(i);
                for (Signal sig : signalsList) {
                    sigChannel = sig.getTMLChannel();
                    if (sigChannel.isAForkChannel()) {
                        originPortSigChannel = sigChannel.getOriginPorts().get(0);
                    } else { // there are no join channels associated to signals
                        originPortSigChannel = sigChannel.getOriginPort();
                    }
                    if (sourcePortCh.getName().equals(originPortSigChannel.getName())) {
                        return sig;
                    }
                }
            }
        }
        return null;
    }

    private void setMappingParametersToBuffers() {

        for (TMLCPLib tmlcplib : mappedCPLibs) {
            List<String> bufferParameters = tmlcplib.getArtifacts().get(0).getBufferParameters();
            String portName = tmlcplib.getArtifacts().get(0).getPortName();
            for (Buffer buff : buffersList) {
                if (buff.getName().equals("buff_" + portName)) {
                    buff.setMappingParameters(bufferParameters);
                }
            }

        }
    }

    private void makeDataTransfersList() {
        List<Signal> inSignals;
        // Signal outSignal;

        for (TMLCPLib cplib : mappedCPLibs) {
            if (cplib.getArtifacts().size() == 1) {
                String portName = cplib.getArtifacts().get(0).getPortName(); // only one mapped port per CP
                // Object o = cplib.getArtifacts().get(0).getReferenceObject();
                inSignals = getDTInSignals(portName);
                String cpName = cplib.getName().split("::")[1];
                for (TMLCP tmlcp : tmlcpsList) {
                    if (tmlcp.getName().equals(cpName)) {
                        DataTransfer dt = new DataTransfer(cplib, tmlcp, inSignals, null); // outSignals are added later
                        dataTransfersList.add(dt);
                    }
                }
            }
        }

        Signal newSig;
        List<Signal> newInSignalsList = new ArrayList<Signal>();

        for (DataTransfer dt : dataTransfersList) {
            Operation op = getOperationWithSameInputSignals(dt.getInSignals()); // IMPORTANT: create a DT output signal
                                                                                // and
                                                                                // modifies operations input signals
            if (op != null) {
                for (Signal sig : op.getInSignals()) {
                    newSig = new Signal(sig.getTMLChannel());
                    newSig.setName(sig.getName() + "_CP");
                    dt.addOutSignal(newSig);
                    newInSignalsList.add(newSig); // to be substitued to the inSignals of op
                    signalsList.add(newSig);
                }
                Buffer inBuff = op.getInBuffer(); // the operation inBuffer is the dataTransfer outBuffer
                String portName = "buff_" + dt.getTMLCPLib().getArtifacts().get(0).getPortName();
                if (inBuff != null) { // the port mapped on the CP is associated to the CP output buffer
                    if (inBuff.getName().equals(portName)) {
                        dt.setOutBuffer(inBuff);
                    }
                }
            }
            op.setInSignals(newInSignalsList);
            newInSignalsList = new ArrayList<Signal>();
        }
        for (DataTransfer dt : dataTransfersList) {
            for (Signal inSignal : dt.getInSignals()) { // for each in signal corresponds an inBuffer
                String buffName = "buff_" + inSignal.getName();
                for (Buffer buff : buffersList) {
                    if (buff.getName().equals(buffName)) {
                        dt.addInBuffer(buff);
                    }
                }
            }
        }
    }

    private Operation getOperationWithSameInputSignals(List<Signal> inSignals) {

        int counter = 0;
        for (Operation op : operationsList) {
            for (Signal sig : op.getInSignals()) {
                if (inSignals.contains(sig)) {
                    counter++;
                }
            }
            if (counter == inSignals.size()) {
                return op;
            }
            counter = 0;
        }
        return null;
    }
    //
    // private Operation getOperationWithSameOutputSignals( ArrayList<Signal>
    // outSignals ) {
    //
    // int counter = 0;
    // for( Operation op: operationsList ) {
    // Signal sig = op.getOutSignal(); //operations have one and only one outSignal
    // if( sig != null) {
    // if( outSignals.contains( sig ) ) {
    // counter++;
    // }
    // if( counter == outSignals.size() ) {
    // return op;
    // }
    // counter = 0;
    // }
    // }
    // return null;
    // }

    // retrieve the signal whose channel has a destintation port equal to portName
    private List<Signal> getDTInSignals(String portName) {

        TMLChannel channel;
        List<Signal> sigsList = new ArrayList<Signal>();

        for (Signal sig : signalsList) {
            channel = sig.getTMLChannel();
            if (channel.isBasicChannel()) {
                if (channel.getDestinationPort().getName().equals(portName)) {
                    sigsList.add(sig);
                }
            } else { // fork or join channel with multiple destination ports
                for (TMLPort port : channel.getDestinationPorts()) {
                    if (port.getName().equals(portName)) {
                        sigsList.add(sig);
                    }
                }
            }
        }
        return sigsList;
    }

    private void makeCommunicationPatternsList() {

        for (TMLCommunicationPatternPanel panel : tmlcpps) {
            GTMLModeling gtmlm = new GTMLModeling(panel, true);
            TMLCP tmlcp = gtmlm.translateToTMLCPDataStructure(panel.getName());
            tmlcpsList.add(tmlcp);
        }
    }

    // Associate signals to operations and at the same time add signals to
    // signalsList. Only works for SDR operations (so far)
    // If user makes a typo in the event name associated to a channel, then the
    // signal is not created. Should raise an error!
    private void makeSignalsList() {

        for (TMLChannel ch : tmlm.getChannels()) {
            // for basic and for channels there is a one-to-one correspondence with signals.
            // Do not consider events, for simplicity, so
            // far.
            if (ch.isBasicChannel() || ch.isAForkChannel()) {
                signalsList.add(new Signal(ch));
            } else if (ch.isAJoinChannel()) { // basic signals must be created in order for the SDF scheduler to work
                for (TMLChannel tmlch : transformIntoBasicChannels(ch)) {
                    signalsList.add(new Signal(tmlch));
                }
            }
        }
        return;
    }

    /**********************************************************************************
     * CODE GENERATION PART
     *********************************************************************************/

    private void generateMainFile() {
        mainFileString.append("#include \"" + applicationName + ".h\"" + CR2);
        mainFileString.append("int main(void)\t{" + CR + TAB + USER_TO_DO + CR + TAB + "int status = " + applicationName
                + "_exec();" + CR + "}");
    }

    private void generateHeaderFile(List<TMLTask> mappedTasks) {

        getPrexAndPostexChannels();
        headerString.append(generateCodeForLibraries());
        headerString.append(generateCodeForCustomTypes());
        headerString.append(generateCodeForPrototypes());
        headerString.append(buffersAndInstructionsDeclaration(true));
        headerString.append(generateCodeForSignals());
    }

    private void getPrexAndPostexChannels() {

        // boolean foundPrex = false, foundPostex = false;
        TMLPort originPort, destinationPort;

        // Fill the the prex and postex lists
        for (TMLChannel ch : tmlm.getChannels()) {
            if (ch.isBasicChannel()) {
                originPort = ch.getOriginPort();
                if (originPort.isPrex()) {
                    prexList.add(originPort);
                }
                destinationPort = ch.getDestinationPort();
                if (destinationPort.isPostex()) {
                    postexList.add(destinationPort);
                }
            }
            if (ch.isAForkChannel()) {
                originPort = ch.getOriginPorts().get(0);
                if (originPort.isPrex()) {
                    prexList.add(originPort);
                }
            }
            if (ch.isAJoinChannel()) {
                destinationPort = ch.getDestinationPorts().get(0);
                if (destinationPort.isPostex()) {
                    postexList.add(destinationPort);
                }
            }
        }
    }

    private String generateCodeForLibraries() {
        String s = "#ifndef " + applicationName + "_H" + CR + "#define " + applicationName + "_H" + CR
                + "#include <stdio.h>" + CR + "#include <stdint.h>" + CR + "#include <embb/fep.h>" + CR
                + "#include <embb/intl.h>" + CR + "#include <embb/mapper.h>" + CR + "#include <embb/adaif.h>" + CR
                + "#include <embb/memory.h>" + CR2 + "extern char fep_mss[];" + CR + "extern char adaif_mss[];" + CR
                + "extern char intl_mss[];" + CR + "extern char mapper_mss[];" + CR2;
        return s;
    }

    private String generateCodeForPrototypes() {
        String s = "/**** prototypes *****/" + CR + "extern int " + applicationName + "_exec(void);" + CR
                + "extern void init_buffers(void);" + CR + "extern bool exit_rule(void);" + CR
                + "extern void register_operations(void);" + CR + "extern void register_dataTransfers(void);" + CR
                + "extern void register_fire_rules(void);" + CR + "extern void init_signals(void);" + CR
                + "extern void init_operations(void);" + CR + "extern void init_CPs(void);" + CR
                + "extern void cleanup_operations_context(void);" + CR + "extern void cleanup_CPs_context(void);" + CR2;
        return s;
    }

    private String buffersAndInstructionsDeclaration(boolean declaration) {

        TMLTask xTask;// , fTask;
        String ctxName;
        // ArchUnitMEC taskMEC;
        Buffer inBuff, outBuff;
        StringBuffer buffersString = new StringBuffer("/**** Buffers *****/" + CR);
        StringBuffer instructionsString = new StringBuffer("/**** Operations Data Structures *****/" + CR);

        for (Operation op : operationsList) {
            if (op.getType() == Operation.SDR) {
                xTask = op.getSDRTasks().get(Operation.X_TASK);
                // fTask = op.getSDRTasks().get( Operation.F_TASK );
                inBuff = op.getInBuffer();
                outBuff = op.getOutBuffer();
                ctxName = op.getContextName();
                int xTaskOperationType = xTask.getOperationType();
                TraceManager.addDev("TaskType for  task " + xTask.getTaskName() + " op: " + xTaskOperationType);
                if (declaration) {
                    if (inBuff == null) { // for source operation
                        buffersString.append("extern" + SP + outBuff.getType() + SP + outBuff.getName() + SC + CR);
                        instructionsString.append("extern" + SP + OperationMEC.CONTEXTS_LIST.get(xTaskOperationType)
                                + SP + ctxName + SC + CR);
                    } else {
                        if (outBuff == null) { // for sink operation
                            buffersString.append("extern" + SP + inBuff.getType() + SP + inBuff.getName() + SC + CR);
                            instructionsString.append("extern" + SP + OperationMEC.CONTEXTS_LIST.get(xTaskOperationType)
                                    + SP + ctxName + SC + CR);
                        } else { // for all the remaining operations
                            buffersString.append("extern" + SP + inBuff.getType() + SP + inBuff.getName() + SC + CR);
                            // if( outBuff != null ) {
                            buffersString.append("extern" + SP + outBuff.getType() + SP + outBuff.getName() + SC + CR);
                            instructionsString.append("extern" + SP + OperationMEC.CONTEXTS_LIST.get(xTaskOperationType)
                                    + SP + ctxName + SC + CR);
                            // }
                        }
                    }
                } else {
                    if (inBuff == null) { // for source operation
                        buffersString.append(outBuff.getType() + SP + outBuff.getName() + SC + CR);
                        instructionsString
                                .append(OperationMEC.CONTEXTS_LIST.get(xTaskOperationType) + SP + ctxName + SC + CR);
                    } else {
                        if (outBuff == null) { // for sink operation
                            buffersString.append(inBuff.getType() + SP + inBuff.getName() + SC + CR);
                            instructionsString.append(
                                    OperationMEC.CONTEXTS_LIST.get(xTaskOperationType) + SP + ctxName + SC + CR);
                        } else { // for all the remaining operations
                            buffersString.append(inBuff.getType() + SP + inBuff.getName() + SC + CR);
                            // if( outBuff != null ) {
                            buffersString.append(outBuff.getType() + SP + outBuff.getName() + SC + CR);
                            instructionsString.append(
                                    OperationMEC.CONTEXTS_LIST.get(xTaskOperationType) + SP + ctxName + SC + CR);
                            // }
                        }
                    }
                }
            }
        }
        instructionsString.append(CR2 + "/**** Data Transfers Instructions ****/" + CR);
        // ArchUnitMEC archMEC = new CpuMEC();
        for (DataTransfer dt : dataTransfersList) {
            TMLCPLib tmlcplib = dt.getTMLCPLib();
            String context = getContextOfDMAController(tmlcplib.getArtifacts().get(0).getMemoryName());
            int cpMECType = tmlcplib.getCPMECType();
            ctxName = dt.getContextName();
            if (cpMECType == CPMEC.CPU_MEMORY_COPY_MEC) {
                if (declaration) {
                    instructionsString.append("extern" + SP + context + SP + ctxName + SC + CR);
                } else {
                    instructionsString.append(context + SP + ctxName + SC + CR);
                }
            }
            if (cpMECType == CPMEC.SINGLE_DMA_MEC) {
                int transferType = tmlcplib.getTransferTypes().get(0);
                if (declaration) {
                    if (transferType == CPMEC.IP_2_IP) { // there are two contexts to declare
                        instructionsString.append("extern" + SP + context + SP + ctxName + "_1" + SC + CR);
                        instructionsString.append("extern" + SP + context + SP + ctxName + "_0" + SC + CR);
                    } else {
                        instructionsString.append("extern" + SP + context + SP + ctxName + SC + CR);
                    }
                } else {
                    if (transferType == CPMEC.IP_2_IP) { // there are two contexts to declare
                        instructionsString.append(context + SP + ctxName + "_0" + SC + CR);
                        instructionsString.append(context + SP + ctxName + "_1" + SC + CR);
                    } else {
                        instructionsString.append(context + SP + ctxName + SC + CR);
                    }
                }
            }
            if (cpMECType == CPMEC.DOUBLE_DMA_MEC) {
                int suffix = 0;
                for (String s : tmlcplib.getMappedUnits()) {
                    // ArrayList<Integer> transferTypes = tmlcplib.getTransferTypes();
                    if (s.contains(CPMEC.DMA_CONTROLLER)) {
                        int transferType = tmlcplib.getTransferTypes().get(suffix);
                        // String context = getContextOfDMAController(
                        // tmlcplib.getArtifacts().get(0).getMemoryName() );
                        if (declaration) {
                            if (transferType == CPMEC.IP_2_IP) { // there are two contexts to declare
                                instructionsString.append("extern" + SP + context + SP + ctxName + "_"
                                        + String.valueOf(suffix) + "_0" + SC + CR);
                                instructionsString.append("extern" + SP + context + SP + ctxName + "_"
                                        + String.valueOf(suffix) + "_1" + SC + CR);
                            } else {
                                instructionsString.append("extern" + SP + context + SP + ctxName + "_"
                                        + String.valueOf(suffix) + SC + CR);
                            }
                        } else {
                            if (transferType == CPMEC.IP_2_IP) { // there are two contexts to declare
                                instructionsString
                                        .append(context + SP + ctxName + "_" + String.valueOf(suffix) + "_0" + SC + CR);
                                instructionsString
                                        .append(context + SP + ctxName + "_" + String.valueOf(suffix) + "_1" + SC + CR);
                            } else {
                                instructionsString
                                        .append(context + SP + ctxName + "_" + String.valueOf(suffix) + SC + CR);
                            }
                        }
                        suffix++;
                    }
                }
            }
        }
        return buffersString.toString() + CR + instructionsString.toString();
    }

    // Issue #98
    private String getContextOfDMAController(String dmaUnit) {
        String context = "";
        if (dmaUnit.contains("MAPPER")) {
            context = MapperMEC.CONTEXT;
        } else if (dmaUnit.contains("FEP")) {
            context = FepMEC.CONTEXT;
        } else if (dmaUnit.contains("ADAIF")) {
            context = AdaifMEC.CONTEXT;
        } else if (dmaUnit.contains("INTERLEAVER") || dmaUnit.contains("INTL")) {
            context = InterleaverMEC.CONTEXT;
        }
        return context;
    }

    private String generateCodeForSignals() {
        StringBuffer s = new StringBuffer(CR2 + "enum sigs_enu {" + CR);

        for (Signal sig : signalsList) {
            s.append(TAB + sig.getName() + "," + CR);
        }

        if (postexList.size() > 0) {
            s.append(TAB + postexList.get(0).getName() + "," + CR);
        }

        s.append("NUM_SIGS };" + CR2 + "enum ops_enu   {" + CR);

        for (Operation op : operationsList) {
            s.append(TAB + op.getName() + ",\n");
        }
        for (DataTransfer dt : dataTransfersList) {
            s.append(TAB + dt.getName() + ",\n");
        }
        s.append("NUM_OPS };" + CR2 + "#endif");
        return s.toString();
    }

    private String generateCodeForCustomTypes() {
        StringBuffer s = new StringBuffer();
        s.append(Signal.DECLARATION + CR2);
        s.append("/**** Buffers ****/" + CR);
        s.append(FepBuffer.DECLARATION + CR2);
        s.append(MapperBuffer.DECLARATION + CR2);
        s.append(InterleaverBuffer.DECLARATION + CR2);
        s.append(MMBuffer.DECLARATION + CR2);
        s.append(AdaifBuffer.DECLARATION + CR);

        return s.toString();
    }

    private void generateProgramFile() {

        /*
         * JOptionPane.showMessageDialog( frame,
         * "The TURTLE Analysis contains several errors", "Syntax analysis failed",
         * JOptionPane.INFORMATION_MESSAGE );
         */
        Scheduler scheduler = new Scheduler(Scheduler.JAIR);
        programString.append("#include " + "\"" + applicationName + ".h\"" + CR2 + "int (*operation[NUM_OPS])();" + CR
                + "bool (*fire_rule[NUM_OPS])();" + CR + "SIG_TYPE sig[NUM_SIGS]={{0}};" + CR2 + "/******** "
                + applicationName + "_exec function *********/" + CR + "int " + applicationName + "_exec(void)    {"
                + CR + TAB + "bool valid_signal = false;" + CR + TAB + "bool blocked = true;" + CR + TAB
                + "int status = 0;" + CR + TAB + "register_operations();" + CR + TAB + "register_dataTransfers();" + CR
                + TAB + "register_fire_rules();" + CR + TAB + "init_buffers();" + CR + TAB + "init_signals();" + CR
                + TAB + "init_operations();" + CR + TAB + "init_CPs();" + CR2 + TAB
                + "/********* INIT PREX OPs signals ********/" + CR + generateCodeToInitPrexOperation() + CR + TAB
                + "/********* OPERATIONS scheduling ***************/" + CR + TAB + scheduler.getCode() + CR + TAB +
                // generateCodeForSinkOperation() + CR + TAB +
                "cleanup_operations_context();" + CR + TAB + "cleanup_CPs_context();" + CR + TAB + "return status;" + CR
                + "}" + CR2);
        generateCodeForOperations();
        generateCodeForCommunicationPatterns();
        generateCodeToRegisterOperations();
        generateCodeToRegisterDataTransfers();
        generateCodeForFireRules();
        generateCodeToRegisterFireRules();
        generateCodeForExitRule();
    }

    private String generateCodeToInitPrexOperation() {

        StringBuffer s = new StringBuffer();
        for (TMLPort port : prexList) {
            s.append(TAB + "sig[ " + port.getName() + " ].f = false;" + CR);
        }
        return s.toString();
    }

    // private String generateCodeForSinkOperation() {
    //
    // StringBuffer s = new StringBuffer();
    // for( Operation op: operationsList ) {
    // if( op.isPostex() ) {
    // s.append( "op_" + op.getName() + "()" + SC );
    // break;
    // }
    // }
    // return s.toString();
    // }

    private void generateCodeForOperations() { // generate the code for the execution operations

        // for each operations add the exec code + the info for all the signals and
        // stuff
        // String exec_code = "";

        for (Operation op : operationsList) {
            if (op.getType() == Operation.SDR) {
                programString.append(generateSDROperation(op));
            }
        }
    }

    private String generateSDROperation(Operation op) {

        // For SDR operations the xTask is used to retrieve the mapped unit
        String signalOutName = "";
        String signalInName = "";
        StringBuffer code = new StringBuffer();
        String ctxName = op.getContextName();
        TMLTask xTask = op.getSDRTasks().get(Operation.X_TASK);
        // TMLTask fTask = op.getSDRTasks().get( Operation.F_TASK );

        int xTaskOperationType = xTask.getOperationType();
        TraceManager.addDev("OperationType of " + xTask.getTaskName() + " = " + xTaskOperationType);

        if (op.getOutSignal() != null) {
            signalOutName = op.getOutSignal().getName();
        }
        for (Signal sig : op.getInSignals()) {
            signalInName += sig.getName();
        }

        code.append("int op_" + op.getName() + "()\t{" + CR /* + getTaskAttributes( fTask ) */ + CR);

        // Issue #98: Status is returned so we should always declare it
        // if( op.isPrex() || op.isPostex() ) {
        code.append(TAB + "int status = 0;" + CR);
        // }

        // Mark input signals as false
        for (Signal sig : op.getInSignals()) {
            code.append(TAB + "sig[" + sig.getName() + "].f = false;" + CR);
        }

        if (xTaskOperationType == FepOperationMEC.CWP_MEC) {
            CwpMEC cwp = new CwpMEC(ctxName, signalInName, signalOutName);
            code.append(cwp.getExecCode());
        } else if (xTaskOperationType == FepOperationMEC.CWM_MEC) {
            CwmMEC cwm = new CwmMEC(ctxName, signalInName, signalOutName);
            code.append(cwm.getExecCode());
        } else if (xTaskOperationType == FepOperationMEC.CWA_MEC) {
            CwaMEC cwa = new CwaMEC(ctxName, signalInName, "", signalOutName);
            code.append(cwa.getExecCode());
        } else if (xTaskOperationType == FepOperationMEC.CWL_MEC) {
            CwlMEC cwl = new CwlMEC(ctxName, signalInName, signalOutName);
            code.append(cwl.getExecCode());
        } else if (xTaskOperationType == FepOperationMEC.SUM_MEC) {
            SumMEC sum = new SumMEC(ctxName, signalInName, signalOutName);
            code.append(sum.getExecCode());
        } else if (xTaskOperationType == FepOperationMEC.FFT_MEC) {
            FftMEC fft = new FftMEC(ctxName, signalInName, signalOutName);
            code.append(fft.getExecCode());
        } else if (xTaskOperationType == OperationMEC.MAPP_OPERATION_MEC) {
            MappOperationMEC mapp = new MappOperationMEC(ctxName, signalInName, signalOutName);
            code.append(mapp.getExecCode());
        } else if (xTaskOperationType == OperationMEC.INTL_OPERATION_MEC) {
            IntlOperationMEC intl = new IntlOperationMEC(ctxName, signalInName, signalOutName);
            code.append(intl.getExecCode());
        } else if (xTaskOperationType == OperationMEC.ADAIF_OPERATION_MEC) {
            AdaifOperationMEC adaif = new AdaifOperationMEC(ctxName);
            code.append(adaif.getExecCode());
        }

        if (op.getOutSignal() != null) {
            code.append(TAB + "sig[" + op.getOutSignal().getName() + "].f = true;" + CR);
        } else { // the postex
            if (postexList.size() > 0) {
                code.append(TAB + "sig[" + postexList.get(0).getName() + "].f = true;" + CR);
            } else {
                code.append(CR);
            }
        }

        code.append(TAB + "return status;" + CR + "}" + CR2);

        return code.toString();
    }
    //
    // private String getTaskAttributes( TMLTask task ) {
    //
    // StringBuffer attributesList = new StringBuffer();
    // String type, value;
    //
    // String[] attributes = task.getAttributeString().split("/");
    // for( int i = 0; i < attributes.length; i++ ) {
    // if( attributes[i].length() > 1 ) {
    // String s = attributes[i].split("\\.")[1];
    // String name = s.split(":")[0];
    // if( !name.contains( "__req" ) ) { //filter out request parameters
    // type = s.split(":")[1].split("=")[0];
    // value = s.split(":")[1].split("=")[1];
    // if( value.equals(" " ) ) {
    // attributesList.append( TAB + type + " " + name + ";" + CR );
    // }
    // else {
    // attributesList.append( TAB + type + " " + name + " = " + value.substring( 0,
    // value.length() - 1 ) + ";" + CR );
    // }
    // }
    // }
    // }
    // return attributesList.toString().substring( 0, attributesList.length() - 1 );
    // //remove last CR
    // }

    // private String getOutSignalName( TMLTask task ) {
    //
    // String s = "";
    // if( task.getWriteChannels().size() > 0 ) {
    // TMLWriteChannel ch = task.getWriteChannels().get(0);
    // String signalName = ch.toString().split("__")[1];
    // if( signalsList.contains( signalName + "_CPin" ) ) {
    // s = signalName;
    // }
    // else {
    // s = signalName;
    // }
    // return s;
    // }
    // return "";
    // }

    // private String getInSignalName( TMLTask task ) {
    //
    // String s = "";
    // if( task.getReadChannels().size() > 0 ) {
    // TMLReadChannel ch = task.getReadChannels().get(0);
    // String signalName = ch.toString().split("__")[1];
    // if( signalsList.contains( signalName + "_CPin" ) ) {
    // s = signalName;
    // }
    // else {
    // s = signalName;
    // }
    // return s;
    // }
    // return "";
    // }

    private void generateCodeForCommunicationPatterns() {

        int srcMemoryType = 0, dstMemoryType = 0;
        TMLCPLib tmlcplib;
        String /* s, */ ctxName;// , counter;
        Vector<String> attributes;
        ArchUnitMEC dmaArchMEC = new CpuMEC();

        for (DataTransfer dt : dataTransfersList) {
            tmlcplib = dt.getTMLCPLib();
            ctxName = dt.getContextName();
            attributes = tmlcplib.getAssignedAttributes();

            TraceManager.addDev("Getting assigned attributes from tmlcplib:\n" + attributes.toString());

            String name = tmlcplib.getName().split("::")[0];
            programString.append("int op_" + name + "()\t{" + CR + TAB + "int status = 0;" + CR);

            for (Signal sig : dt.getInSignals()) {
                programString.append(TAB + "sig[ " + sig.getName() + " ].f = false;" + CR);
            }

            int cpMECType = tmlcplib.getCPMECType();

            if (cpMECType == CPMEC.CPU_MEMORY_COPY_MEC) {
                for (String s1 : tmlcplib.getMappedUnits()) {
                    if (s1.contains(CPMEC.SOURCE_STORAGE)) {
                        String memoryUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        srcMemoryType = tmla.getHwMemoryByName(memoryUnit).bufferType;

                        break;
                    }
                }

                Vector<String> mecAttributes = tmlcplib.getAssignedAttributes();
                CpuMemoryCopyMEC mec = new CpuMemoryCopyMEC(ctxName, mecAttributes, srcMemoryType); // mem2ip
                programString.append(mec.getExecCode());
            } else if (cpMECType == CPMEC.SINGLE_DMA_MEC) {
                for (String s1 : tmlcplib.getMappedUnits()) {
                    if (s1.contains(CPMEC.DMA_CONTROLLER)) {
                        String dmaUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        dmaArchMEC = tmla.getHwCPUByName(dmaUnit).MEC;
                    }
                    if (s1.contains(CPMEC.SOURCE_STORAGE)) {
                        String memoryUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        srcMemoryType = tmla.getHwMemoryByName(memoryUnit).bufferType;
                    }
                    if (s1.contains(CPMEC.DESTINATION_STORAGE)) {
                        String memoryUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        dstMemoryType = tmla.getHwMemoryByName(memoryUnit).bufferType;
                    }
                }

                SingleDmaMEC mec = new SingleDmaMEC(ctxName, dmaArchMEC, srcMemoryType, dstMemoryType,
                        tmlcplib.getTransferTypes().get(0), attributes);
                programString.append(mec.getExecCode());
            } else if (cpMECType == CPMEC.DOUBLE_DMA_MEC) {
                List<Integer> transferTypes = tmlcplib.getTransferTypes();
                // first I must get the mapped DMA controllers and the memories
                Vector<Integer> srcMemoryTypes = new Vector<Integer>();
                Vector<Integer> dstMemoryTypes = new Vector<Integer>();
                Vector<ArchUnitMEC> dmaArchMECs = new Vector<ArchUnitMEC>();
                int iDma = 1;
                int iSrc = 1;
                int iDst = 1;
                // int iteration = 1; // fixing compilation
                for (String s1 : tmlcplib.getMappedUnits()) { // there are two DMA_controllers
                    if (s1.contains(CPMEC.DMA_CONTROLLER + "_" + String.valueOf(iDma))) {
                        String dmaUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        dmaArchMECs.add(iDma - 1, tmla.getHwCPUByName(dmaUnit).MEC);
                        iDma++;
                    }
                    if (s1.contains(CPMEC.SOURCE_STORAGE + "_" + String.valueOf(iSrc))) {
                        String memoryUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        srcMemoryTypes.add(iSrc - 1, tmla.getHwMemoryByName(memoryUnit).bufferType);
                        iSrc++;
                    }
                    if (s1.contains(CPMEC.DESTINATION_STORAGE + "_" + String.valueOf(iDst))) {
                        String memoryUnit = s1.split(":")[1].replaceAll("\\s+", "");
                        dstMemoryTypes.add(iDst - 1, tmla.getHwMemoryByName(memoryUnit).bufferType);
                        iDst++;
                    }
                }
                /*
                 * TraceManager.addDev( "About to create the DoubleDmaMEC class" ); for( Integer
                 * i: srcMemoryTypes ) { TraceManager.addDev( "srcMemoryTypes = " + i.intValue()
                 * ); } for( Integer i: dstMemoryTypes ) { TraceManager.addDev(
                 * "dstMemoryTypes = " + i.intValue() ); } for( ArchUnitMEC i: dmaArchMECs ) {
                 * TraceManager.addDev( "dmaArchMECs = " + i.toString() ); }
                 */
                DoubleDmaMEC mec = new DoubleDmaMEC(ctxName, dmaArchMECs, srcMemoryTypes, dstMemoryTypes, transferTypes,
                        attributes);
                programString.append(mec.getExecCode());
            }

            for (Signal sig : dt.getOutSignals()) {
                programString.append(TAB + "sig[ " + sig.getName() + " ].f = true;" + CR);
            }

            programString.append(TAB + "return status;" + CR + "}" + CR2);
        }
    }

    /*
     * private DoubleDmaMEC getDoubleDmaMEC( TMLCPLib tmlcplib, int iteration,
     * String ctxName, Vector<String> attributes, ArrayList<Integer> transferTypes )
     * {
     * 
     * int srcMemoryType = 0, dstMemoryType = 0; ArchUnitMEC dmaArchMEC = new
     * CpuMEC(); ArrayList<String> sizes = new ArrayList<String>();
     */
    // sizes.add( (attributes.size() == 0 ) ? "/* USER TODO */" : attributes.get(
    // DoubleDmaMEC.counter1Index ) );
    // sizes.add( (attributes.size() == 0 ) ? "/* USER TODO */" : attributes.get(
    // DoubleDmaMEC.counter2Index ) );

    /*
     * for( String s1: tmlcplib.getMappedUnits() ) { //there are two DMA_controllers
     * if( s1.contains( CPMEC.dmaController + "_" + String.valueOf(iteration) ) ) {
     * String dmaUnit = s1.split(":")[1].replaceAll("\\s+",""); dmaArchMEC =
     * tmla.getHwCPUByName( dmaUnit ).MEC; } if( s1.contains( CPMEC.sourceStorage +
     * "_" + String.valueOf(iteration) ) ) { String memoryUnit =
     * s1.split(":")[1].replaceAll("\\s+",""); srcMemoryType =
     * tmla.getHwMemoryByName( memoryUnit ).BufferType; } if( s1.contains(
     * CPMEC.destinationStorage + "_" + String.valueOf(iteration) ) ) { String
     * memoryUnit = s1.split(":")[1].replaceAll("\\s+",""); dstMemoryType =
     * tmla.getHwMemoryByName( memoryUnit ).BufferType; } } DoubleDmaMEC mec = new
     * DoubleDmaMEC( ctxName + "_" + String.valueOf(iteration), dmaArchMEC,
     * srcMemoryType, dstMemoryType, transferTypes.get(iteration), attributes );
     * return mec; }
     */

    private void generateCodeToRegisterOperations() {

        programString.append("void register_operations( void )\t{" + CR);
        for (Operation op : operationsList) {
            programString.append(TAB + "operation[" + op.getName() + "] = " + "op_" + op.getName() + ";" + CR);
        }
        programString.append("}" + CR2);
    }

    private void generateCodeToRegisterDataTransfers() {

        programString.append("void register_dataTransfers( void )\t{" + CR);
        for (DataTransfer dt : dataTransfersList) {
            programString.append(TAB + "operation[" + dt.getName() + "] = " + "op_" + dt.getName() + ";" + CR);
        }
        programString.append("}" + CR2);
    }

    private void generateCodeForFireRules() {

        programString.append("/**** OPERATIONS FIRE RULES ****/" + CR);
        for (Operation op : operationsList) {
            programString.append("bool fr_" + op.getName() + "( void )\t{" + CR);
            programString.append(TAB + "return (" + op.getFireRuleCondition() + ");" + CR);
            programString.append("}" + CR2);
        }
        programString.append(CR);
        programString.append("/**** DATA TRANSFERS FIRE RULES ****/" + CR);
        for (DataTransfer dt : dataTransfersList) {
            programString.append("bool fr_" + dt.getName() + "( void )\t{" + CR);
            programString.append(TAB + "return (" + SP + dt.getFireRuleCondition() + SP + ");" + CR);
            programString.append("}" + CR2);
        }
        programString.append(CR);
    }

    private void generateCodeToRegisterFireRules() {

        programString.append("void register_fire_rules( void )\t{" + CR);
        for (Operation op : operationsList) {
            programString.append(TAB + "fire_rule[" + op.getName() + "] = " + "fr_" + op.getName() + ";" + CR);
        }
        for (DataTransfer dt : dataTransfersList) {
            programString.append(TAB + "fire_rule[" + dt.getName() + "] = " + "fr_" + dt.getName() + ";" + CR);
        }
        programString.append("}" + CR2);
    }

    private void generateCodeForExitRule() {

        StringBuffer s = new StringBuffer();
        for (TMLPort port : postexList) {
            s.append("( sig[ " + port.getName() + " ].f == true ) &&");
        }
        if (s.length() > 3) {
            programString.append("bool exit_rule(void)\t{" + CR + TAB + "return "
                    + s.toString().substring(0, s.length() - 3) + SC + CR + "}");
        } else {
            programString.append("bool exit_rule(void)\t{" + CR + TAB + "return " + s + SC + CR + "}");
        }
    }

    private void generateInitFile(List<TMLTask> mappedTasks) {

        String init_code = "";
        String ctxName;
        String inSignalName = Signal.USERTODO, outSignalName = Signal.USERTODO;
        initFileString.append("#include \"" + applicationName + ".h\"" + CR2);
        initFileString.append("/**** variables ****/" + CR2);
        initFileString.append("char fep_mss[0x10000];" + CR);
        initFileString.append("char adaif_mss[0x10000];" + CR);
        initFileString.append("char intl_mss[0x41000];" + CR);
        initFileString.append("char mapper_mss[0x8000];" + CR);
        initFileString.append(buffersAndInstructionsDeclaration(false) + CR2);
        generateCodeToInitializeBuffers();
        generateCodeToInitializeSignals();
        initFileString.append("/**** init code ****/" + CR);

        // Only for SDR operations
        for (Operation op : operationsList) {
            if (op.getType() == Operation.SDR) {
                TMLTask xTask = op.getSDRTasks().get(Operation.X_TASK);
                // TMLTask fTask = op.getSDRTasks().get( Operation.X_TASK );
                int xTaskOperationType = xTask.getOperationType();
                // int fTaskOperationType = fTask.getOperationType();
                ctxName = op.getContextName();
                if (op.getInSignals().size() > 0) {
                    inSignalName = op.getInSignals().get(0).getName();
                }
                if (op.getOutSignal() != null) {
                    outSignalName = op.getOutSignal().getName();
                }
                switch (xTaskOperationType) {
                    case OperationMEC.CWP_MEC:
                        CwpMEC cwp = new CwpMEC(ctxName, inSignalName, outSignalName);
                        init_code = cwp.getInitCode();
                        break;
                    case OperationMEC.CWM_MEC:
                        CwmMEC cwm = new CwmMEC(ctxName, inSignalName, outSignalName);
                        init_code = cwm.getInitCode();
                        break;
                    case OperationMEC.CWA_MEC:
                        CwaMEC cwa = new CwaMEC(ctxName, inSignalName, "", outSignalName);
                        init_code = cwa.getInitCode();
                        break;
                    case OperationMEC.CWL_MEC:
                        CwlMEC cwl = new CwlMEC(ctxName, inSignalName, outSignalName);
                        init_code = cwl.getInitCode();
                        break;
                    case OperationMEC.SUM_MEC:
                        SumMEC sum = new SumMEC(ctxName, inSignalName, outSignalName);
                        init_code = sum.getInitCode();
                        break;
                    case OperationMEC.FFT_MEC:
                        FftMEC fft = new FftMEC(ctxName, inSignalName, outSignalName);
                        init_code = fft.getInitCode();
                        break;
                    case OperationMEC.INTL_OPERATION_MEC:
                        IntlOperationMEC intl = new IntlOperationMEC(ctxName, inSignalName, outSignalName);
                        init_code = intl.getInitCode();
                        break;
                    case OperationMEC.MAPP_OPERATION_MEC:
                        MappOperationMEC mapp = new MappOperationMEC(ctxName, inSignalName, outSignalName);
                        init_code = mapp.getInitCode();
                        break;
                    case OperationMEC.ADAIF_OPERATION_MEC:
                        AdaifOperationMEC adaif = new AdaifOperationMEC(ctxName);
                        init_code = adaif.getInitCode();
                        break;
                    case OperationMEC.CPU_OPERATION_MEC:
                        CpuOperationMEC cpu = new CpuOperationMEC(ctxName, inSignalName, outSignalName);
                        init_code = cpu.getInitCode();
                        break;
                }
                initFileString.append(init_code + CR);
                init_code = "";
                inSignalName = Signal.USERTODO;
                outSignalName = Signal.USERTODO;
            }
        }

        generateInitRoutinesForCPs();

        initFileString.append("/**** init contexts ****/" + CR + "void init_operations(void)\t{" + CR);
        for (Operation op : operationsList) {
            if (op.getType() == Operation.SDR) {
                TMLTask xTask = op.getSDRTasks().get(Operation.X_TASK);
                ArchUnitMEC xTaskArchMEC = tmap.getHwNodeOf(xTask).getArchUnitMEC();
                if (xTaskArchMEC instanceof FepMEC) {
                    initFileString.append(TAB + "init_" + xTask.getTaskName() + "();" + CR);
                }
                if (xTaskArchMEC instanceof MapperMEC) {
                    initFileString.append(TAB + "init_" + xTask.getTaskName() + "();" + CR);
                }
                if (xTaskArchMEC instanceof InterleaverMEC) {
                    initFileString.append(TAB + "init_" + xTask.getTaskName() + "();" + CR);
                }
                if (xTaskArchMEC instanceof AdaifMEC) {
                    initFileString.append(TAB + "init_" + xTask.getTaskName() + "();" + CR);
                }
            }
        }
        initFileString.append("}" + CR2);

        // Init Communication Patterns. Only DMA transfers need init code
        initFileString.append("/**** init CPs ****/" + CR + "void init_CPs(void)\t{" + CR);
        for (DataTransfer dt : dataTransfersList) {
            TMLCPLib tmlcplib = dt.getTMLCPLib();
            String name = tmlcplib.getName().split("::")[0];
            if (!(tmlcplib.getCPMECType() == CPMEC.CPU_MEMORY_COPY_MEC)) { // No need to create init routine for memory
                                                                           // copy
                                                                           // transfers
                initFileString.append(TAB + "init_" + name + "();" + CR);
            }
        }
        initFileString.append("}" + CR2);

        // Clean-up context routines
        initFileString.append("/**** cleanup contexts ****/" + CR);
        initFileString.append("void cleanup_operations_context( void )\t{" + CR);
        for (Operation op : operationsList) {
            if (op.getType() == Operation.SDR) {
                TMLTask xTask = op.getSDRTasks().get(Operation.X_TASK);
                ctxName = op.getContextName();
                ArchUnitMEC xTaskArchMEC = tmap.getHwNodeOf(xTask).getArchUnitMEC();
                if (xTaskArchMEC instanceof FepMEC) {
                    initFileString.append(TAB + FepMEC.CONTEXT_CLEANUP + "( &" + ctxName + " );" + CR);
                }
                if (xTaskArchMEC instanceof MapperMEC) {
                    initFileString.append(TAB + MapperMEC.CONTEXT_CLEANUP + "( &" + ctxName + " );" + CR);
                }
                if (xTaskArchMEC instanceof InterleaverMEC) {
                    initFileString.append(TAB + InterleaverMEC.CONTEXT_CLEANUP + "( &" + ctxName + " );" + CR);
                }
                if (xTaskArchMEC instanceof AdaifMEC) {
                    initFileString.append(TAB + AdaifMEC.CTX_CLEANUP + "( &" + ctxName + " );" + CR);
                }
            }
        }
        initFileString.append("}" + CR2);
        initFileString.append("void cleanup_CPs_context( void )\t{" + CR);
        // ArchUnitMEC dmaArchMEC = new CpuMEC();

        for (DataTransfer dt : dataTransfersList) {
            TMLCPLib tmlcplib = dt.getTMLCPLib();
            int cpMECType = tmlcplib.getCPMECType();
            int transferType;
            ctxName = dt.getContextName();
            Vector<String> mappedUnits = tmlcplib.getMappedUnits();
            switch (cpMECType) {
                case CPMEC.CPU_MEMORY_COPY_MEC:
                    initFileString.append(getCtxCleanupRoutine(ctxName, mappedUnits));
                    break;
                case CPMEC.SINGLE_DMA_MEC:
                    transferType = tmlcplib.getTransferTypes().get(0);
                    /*
                     * for( String s: tmlcplib.getMappedUnits() ) { if( s.contains(
                     * CPMEC.dmaController ) ) { String dmaUnit =
                     * s.split(":")[1].replaceAll("\\s+",""); dmaArchMEC = tmla.getHwCPUByName(
                     * dmaUnit ).MEC; break; } }
                     */
                    if (transferType == CPMEC.IP_2_IP) {
                        initFileString.append(getCtxCleanupRoutine(ctxName + "_0", mappedUnits));
                        initFileString.append(getCtxCleanupRoutine(ctxName + "_1", mappedUnits));
                    } else {
                        initFileString.append(getCtxCleanupRoutine(ctxName, mappedUnits));
                    }
                    break;
                case CPMEC.DOUBLE_DMA_MEC:
                    int suffix = 0;
                    for (String s : tmlcplib.getMappedUnits()) { // there are two DMA_controllers
                        if (s.contains(CPMEC.DMA_CONTROLLER)) {
                            transferType = tmlcplib.getTransferTypes().get(suffix);
                            // String dmaUnit = s.split(":")[1].replaceAll("\\s+","");
                            // dmaArchMEC = tmla.getHwCPUByName( dmaUnit ).MEC;
                            if (transferType == CPMEC.IP_2_IP) {
                                initFileString.append(getCtxCleanupRoutine(
                                        ctxName + "_" + String.valueOf(suffix) + "_0", mappedUnits));
                                initFileString.append(getCtxCleanupRoutine(
                                        ctxName + "_" + String.valueOf(suffix) + "_1", mappedUnits));
                                // initFileString.append( TAB + dmaArchMEC.getCtxCleanupCode() + "(&" + ctxName
                                // + "_" + String.valueOf(suffix) + "_0);" + CR );
                                // initFileString.append( TAB + dmaArchMEC.getCtxCleanupCode() + "(&" + ctxName
                                // + "_" + String.valueOf(suffix) + "_1);" + CR );
                            } else {
                                initFileString.append(
                                        getCtxCleanupRoutine(ctxName + "_" + String.valueOf(suffix), mappedUnits));
                                // initFileString.append( TAB + dmaArchMEC.getCtxCleanupCode() + "(&" + ctxName
                                // + "_" + String.valueOf(suffix) + ");" + CR );
                            }
                            suffix++;
                        }
                    }
                    break;
            } // end of switch
        }
        initFileString.append("}" + CR);
    }

    private void generateInitRoutinesForCPs() {

        // ArchUnitMEC dmaArchMEC = new CpuMEC();
        // ArrayList<ArchUnitMEC> dmaArchMECList = new ArrayList<ArchUnitMEC>();
        int transferType, cpMECType;
        Vector<String> mappedUnits = new Vector<String>();

        for (DataTransfer dt : dataTransfersList) {
            TMLCPLib tmlcplib = dt.getTMLCPLib();
            cpMECType = tmlcplib.getCPMECType();
            mappedUnits = tmlcplib.getMappedUnits();
            String ctxName = dt.getContextName();
            String name = tmlcplib.getName().split("::")[0];
            switch (cpMECType) {
                case CPMEC.CPU_MEMORY_COPY_MEC:
                    initFileString.append("void init_" + name + "()\t{" + CR);
                    initFileString.append(getCtxInitRoutine(ctxName, mappedUnits)); // get the ctx_init routine,
                                                                                    // according to
                                                                                    // destination_storage in SD
                    break;
                case CPMEC.SINGLE_DMA_MEC:
                    transferType = tmlcplib.getTransferTypes().get(0);
                    for (String s : tmlcplib.getMappedUnits()) {
                        if (s.contains(CPMEC.DMA_CONTROLLER)) {
                            // String dmaUnit = s.split(":")[1].replaceAll("\\s+","");
                            // dmaArchMEC = tmla.getHwCPUByName( dmaUnit ).MEC;
                            break;
                        }
                    }
                    initFileString.append("void init_" + name + "()\t{" + CR);
                    if (transferType == CPMEC.IP_2_IP) {
                        // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                        // "_0, (uintptr_t) " + dmaArchMEC.getLocalMemoryPointer() + " );" + CR );
                        initFileString.append(getCtxInitRoutine(ctxName + "_0", mappedUnits));
                        // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                        // "_1, (uintptr_t) " + dmaArchMEC.getLocalMemoryPointer() + " );" + CR + "}" +
                        // CR2 );
                        initFileString.append(getCtxInitRoutine(ctxName + "_1", mappedUnits));
                    } else {
                        // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                        // ", (uintptr_t) " + dmaArchMEC.getLocalMemoryPointer() + " );" + CR + "}" +
                        // CR2 );
                        initFileString.append(getCtxInitRoutine(ctxName, mappedUnits));
                    }
                    break;
                case CPMEC.DOUBLE_DMA_MEC:
                    initFileString.append("void init_" + name + "()\t{" + CR);
                    int suffix = 0;
                    for (String s : tmlcplib.getMappedUnits()) { // there are two DMA_controllers
                        if (s.contains(CPMEC.DMA_CONTROLLER)) {
                            transferType = tmlcplib.getTransferTypes().get(suffix);
                            // String dmaUnit = s.split(":")[1].replaceAll("\\s+","");
                            // dmaArchMEC = tmla.getHwCPUByName( dmaUnit ).MEC ;
                            if (transferType == CPMEC.IP_2_IP) {
                                // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                                // "_" + String.valueOf(suffix) + "_0, (uintptr_t) " +
                                // dmaArchMEC.getLocalMemoryPointer() + " );" + CR );
                                initFileString.append(
                                        getCtxInitRoutine(ctxName + "_" + String.valueOf(suffix) + "_0", mappedUnits));
                                // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                                // "_" + String.valueOf(suffix) + "_1, (uintptr_t) " +
                                // dmaArchMEC.getLocalMemoryPointer() + " );" + CR );
                                initFileString.append(
                                        getCtxInitRoutine(ctxName + "_" + String.valueOf(suffix) + "_1", mappedUnits));
                            } else {
                                // initFileString.append( TAB + dmaArchMEC.getCtxInitCode() + "(&" + ctxName +
                                // "_" + String.valueOf(suffix) + ", (uintptr_t) " +
                                // dmaArchMEC.getLocalMemoryPointer() + " );" + CR );
                                initFileString
                                        .append(getCtxInitRoutine(ctxName + "_" + String.valueOf(suffix), mappedUnits));
                            }
                            suffix++;
                        }
                    }
                    break;
            } // end of switch statement
            initFileString.append("}" + CR2);
        } // end of for statement
    }

    private String getCtxInitRoutine(String ctxName, Vector<String> mappedUnits) {

        String filteredMemoryName = "";
        for (String s : mappedUnits) {
            if (s.contains("Dst_Storage_Instance")) {
                filteredMemoryName = s.split(":")[1].replaceAll("\\s+", "");
                if (filteredMemoryName.contains("MAPPER") || filteredMemoryName.contains("mapper")) {
                    return TAB + "mapper_ctx_init(&" + ctxName + ", (uintptr_t) mapper_mss );" + CR;
                }
                if (filteredMemoryName.contains("FEP") || filteredMemoryName.contains("fep")) {
                    return TAB + "fep_ctx_init(&" + ctxName + ", (uintptr_t) fep_mss );" + CR;
                }
                if (filteredMemoryName.contains("INTL") || filteredMemoryName.contains("intl")) {
                    return TAB + "intl_ctx_init(&" + ctxName + ", (uintptr_t) intl_mss );" + CR;
                }
                if (filteredMemoryName.contains("ADAIF") || filteredMemoryName.contains("adaif")) {
                    return TAB + "adaif_ctx_init(&" + ctxName + ", (uintptr_t) adaif_mss );" + CR;
                }
            }
        }
        return "/* USER TODO */";
    }

    private String getCtxCleanupRoutine(String ctxName, Vector<String> mappedUnits) {

        String filteredMemoryName = "";
        for (String s : mappedUnits) {
            if (s.contains("Dst_Storage_Instance")) {
                filteredMemoryName = s.split(":")[1].replaceAll("\\s+", "");
                if (filteredMemoryName.contains("MAPPER") || filteredMemoryName.contains("mapper")) {
                    return TAB + "mapper_ctx_cleanup(&" + ctxName + ");" + CR;
                }
                if (filteredMemoryName.contains("FEP") || filteredMemoryName.contains("fep")) {
                    return TAB + "fep_ctx_cleanup(&" + ctxName + ");" + CR;
                }
                if (filteredMemoryName.contains("INTL") || filteredMemoryName.contains("intl")) {
                    return TAB + "intl_ctx_cleanup(&" + ctxName + ");" + CR;
                }
                if (filteredMemoryName.contains("ADAIF") || filteredMemoryName.contains("adaif")) {
                    return TAB + "adaif_ctx_cleanup(&" + ctxName + ");" + CR;
                }
            }
        }
        return "/* USER TODO */";
    }

    private void generateCodeToInitializeBuffers() {

        initFileString.append("void init_buffers()\t{" + CR);

        for (Buffer buff : buffersList) {
            initFileString.append(buff.getInitCode() + CR);
        }

        initFileString.append("}" + CR2);
    }

    private void generateCodeToInitializeSignals() {

        initFileString.append("void init_signals()\t{" + CR);
        for (Signal sig : signalsList) {
            initFileString.append(TAB + "sig[" + sig.getName() + "].f = false;" + CR);
            Buffer buff = getBufferFromSignal(sig);
            if (buff != null) {
                initFileString.append(TAB + "sig[" + sig.getName() + "].pBuff = (" + buff.getType() + "*)" + SP + "&"
                        + buff.getName() + SC + CR2);
            } else {
                initFileString.append(TAB + "sig[" + sig.getName() + "].pBuff = " + USER_TO_DO + SC + CR2);
            }
        }
        initFileString.append("}" + CR2);
    }

    private Buffer getBufferFromSignal(Signal sig) {

        String sigName = sig.getName();
        if (sigName.contains("_CP")) { // filter out trailing _CP for CP's signals
            sigName = sigName.split("_CP")[0];
        }
        for (Buffer buff : buffersList) {
            if (buff.getName().equals("buff_" + sigName)) {
                return buff;
            }
        }
        return null;
    }

    // for code generation scheduling, transform a join channel into a set of basic
    // channels
    public ArrayList<TMLChannel> transformIntoBasicChannels(TMLChannel originalCh) {

        String chName, appName, dstPortName;
        int numSrcPorts;
        TMLChannel channel;
        ArrayList<TMLChannel> channelsList = new ArrayList<TMLChannel>();

        if (originalCh.isAJoinChannel()) {
            String[] s = originalCh.getName().split("__");
            numSrcPorts = s.length - 2;
            appName = s[0];
            dstPortName = s[s.length - 1];
            for (int i = 0; i < numSrcPorts; i++) {
                chName = appName + "__" + s[i + 1] + "__" + appName + "__" + dstPortName;
                channel = new TMLChannel(chName, null);
                channel.setPorts(originalCh.getOriginPorts().get(i), originalCh.getDestinationPorts().get(0));
                channel.setTasks(originalCh.getOriginTasks().get(i), originalCh.getDestinationTasks().get(0));
                channel.setType(originalCh.getType());
                channel.setSize(originalCh.getSize());
                channel.setMax(originalCh.getMax());
                channelsList.add(channel);
            }
        }
        return channelsList;
    }

    private static String prepareString(String s) {
        return s.replaceAll("\\s", "");
    }

    public static String modifyString(String s) {
        return prepareString(s);
    }

    public String toString() {
        return headerString.toString() + programString.toString();
    }
    //
    // private ArchUnitMEC getArchMEC( TMLCPLib tmlcplib, String sdInstanceName ) {
    //
    // ArchUnitMEC dmaArchMEC = new CpuMEC();
    // for( String s: tmlcplib.getMappedUnits() ) {
    // if( s.contains( sdInstanceName ) ) {
    // String dmaUnit = s.split(":")[1].replaceAll("\\s+","");
    // dmaArchMEC = tmla.getHwCPUByName( dmaUnit ).MEC;
    // break;
    // }
    // }
    // return dmaArchMEC;
    // }

    public void saveFile(String path, String filename) throws FileException {

        TMLModelCompilerMakefile make = new TMLModelCompilerMakefile(applicationName);
        path = path.substring(0, path.length() - 1);

        TraceManager.addUser("Saving C files in " + path);
        FileUtils.saveFile(path + "main.c", mainFileString.toString());
        FileUtils.saveFile(path + filename + ".h", headerString.toString());
        FileUtils.saveFile(path + filename + ".c", programString.toString());
        FileUtils.saveFile(path + filename + "_init.c", initFileString.toString());
        FileUtils.saveFile(path + "Makefile", make.getCode());
    }

    private void openDebugFile() {
        File fileObject = new File(debugFileName);

        if (fileObject.exists()) {
            fileObject.delete();
        }

        outputStream = null;

        try {
            outputStream = new PrintWriter(new FileOutputStream(debugFileName, true));
        } catch (FileNotFoundException e) {
            TraceManager.addDev("Error opening file " + debugFileName);
        }
    }

    private void appendToDebugFile(String s) {
        if ((s != null) && (s.isEmpty())) {
            outputStream.println(s);
        } else {
            outputStream.println(CR);
        }
    }

    private void closeDebugFile() {
        outputStream.close();
    }
} // End of class

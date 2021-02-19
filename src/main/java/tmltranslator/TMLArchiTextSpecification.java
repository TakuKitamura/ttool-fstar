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


package tmltranslator;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TMLArchiTextSpecification
 * Import and export of TML architecture textual specifications
 * Creation: 21/09/2007
 *
 * @author Ludovic APVRILLE, Matteo Bertolino
 * @version 1.1 07/02/2018
 */
public class TMLArchiTextSpecification {
    public final static String CR = "\n";
    public final static String SP = " ";
    public final static String CR2 = "\n\n";
    public final static String SC = ";";

    private String spec;
    //  private String title;

    private TMLArchitecture tmla;
    private ArrayList<TMLTXTError> errors;
    private ArrayList<TMLTXTError> warnings;

    private String keywords[] = {"NODE", "CPU", "FPGA", "SET", "BUS", "LINK", "BRIDGE", "NOC", "MEMORY", "MASTERCLOCKFREQUENCY", "DMA"};
    private String nodetypes[] = {"CPU", "FPGA", "BUS", "LINK", "BRIDGE", "NOC", "MEMORY", "HWA", "DMA"};
    private String cpuparameters[] = {"nbOfCores", "byteDataSize", "pipelineSize", "goIdleTime", "maxConsecutiveIdleCycles", "taskSwitchingTime",
            "branchingPredictionPenalty", "cacheMiss", "schedulingPolicy", "sliceTime", "execiTime", "execcTime", "operation", "clockDivider"};
    private String fpgaparameters[] = {"capacity", "byteDataSize", "mappingPenalty", "goIdleTime",
            "maxConsecutiveIdleCycles", "reconfigurationTime", "execiTime", "execcTime", "scheduling", "clockDivider"};
    private String linkparameters[] = {"bus", "node", "priority"};
    private String hwaparameters[] = {"byteDataSize", "execiTime", "execcTime", "clockDivider"};
    private String busparameters[] = {"byteDataSize", "pipelineSize", "arbitration", "clockDivider"};
    private String bridgeparameters[] = {"bufferByteSize", "clockDivider"};
    private String memoryparameters[] = {"byteDataSize", "clockDivider"};
    private String nocparameters[] = {"bufferbytesize", "nocSize", "clockdivider"};
    //  private String dmaparameters[] = {"byteDataSize", "nbOfChannels"};



    /*private String keywords[] = {"BOOL", "INT", "NAT", "CHANNEL", "EVENT", "REQUEST", "BRBW", "NBRNBW",
      "BRNBW", "INF", "NIB", "NINB", "TASK", "ENDTASK", "IF", "ELSE", "ELSEIF", "ENDIF", "FOR", "ENDFOR",
      "SELECTEVT", "CASE", "ENDSELECTEVT", "ENDCASE", "WRITE", "READ", "WAIT", "NOTIFY", "NOTIFIED", "RAND", "CASERAND", "ENDRAND", "ENDCASERAND", "EXECI"};

      private String channeltypes[] = {"BRBW", "NBRNBW", "BRNBW"};
      private String eventtypes[] = {"INF", "NIB", "NINB"};

      private String beginArray[] = {"TASK", "FOR", "IF", "ELSE", "ELSEIF", "SELECTEVT", "CASE", "RAND", "CASERAND"};
      private String endArray[] = {"ENDTASK", "ENDFOR", "ENDIF", "ELSE", "ELSEIF", "ENDSELECTEVT", "ENDCASE", "ENDRAND", "ENDCASERAND"};
    */

//    public TMLArchiTextSpecification(String _title) {
//        title = _title;
//    }

    public void saveFile(String path, String filename) throws FileException {
        TraceManager.addDev("Saving architecture spec file in " + path + filename);
        FileUtils.saveFile(path + filename, spec);
    }

    public TMLArchitecture getTMLArchitecture() {
        return tmla;
    }

    public String getSpec() {
        return spec;
    }

    public ArrayList<TMLTXTError> getErrors() {
        return errors;
    }

    public ArrayList<TMLTXTError> getWarnings() {
        return warnings;
    }

    
    @Override
    public String toString() {
        return spec;
    }

    public String toTextFormat(TMLArchitecture _tmla) {
        tmla = _tmla;

        spec = "// Master clock frequency - in MHz" + CR;
        spec += "MASTERCLOCKFREQUENCY " + tmla.getMasterClockFrequency() + CR + CR;

        spec += makeNodes(tmla);
        spec += makeLinks(tmla);
        return spec;
        //indent();
    }

    public String makeNodes(TMLArchitecture tmla) {
        String code = "";
        String name;
        String set;
        List<HwNode> hwnodes = tmla.getHwNodes();
        HwCPU cpu;
        HwFPGA fpga;
        HwA hwa;
        HwBus bus;
        HwBridge bridge;
        HwNoC noc;
        HwMemory memory;
        HwDMA dma;

        for (HwNode node : hwnodes) {

            // CPU
            if (node instanceof HwCPU) {
                cpu = (HwCPU) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE CPU " + name + CR;
                code += set + "nbOfCores " + cpu.nbOfCores + CR;
                code += set + "byteDataSize " + cpu.byteDataSize + CR;
                code += set + "pipelineSize " + cpu.pipelineSize + CR;
                code += set + "goIdleTime " + cpu.goIdleTime + CR;
                code += set + "maxConsecutiveIdleCycles " + cpu.maxConsecutiveIdleCycles + CR;
                code += set + "taskSwitchingTime " + cpu.taskSwitchingTime + CR;
                code += set + "branchingPredictionPenalty " + cpu.branchingPredictionPenalty + CR;
                code += set + "cacheMiss " + cpu.cacheMiss + CR;
                code += set + "schedulingPolicy " + cpu.schedulingPolicy + CR;
                code += set + "sliceTime " + cpu.sliceTime + CR;
                code += set + "execiTime " + cpu.execiTime + CR;
                code += set + "execcTime " + cpu.execcTime + CR;
                if (cpu.getOperation().length() > 0) {
                    code += set + "operation " + cpu.getOperation() + CR;
                }
            }

            // FPGA
            if (node instanceof HwFPGA) {
                fpga = (HwFPGA) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE FPGA " + name + CR;
                code += set + "capacity " + fpga.capacity + CR;
                code += set + "byteDataSize " + fpga.byteDataSize + CR;
                code += set + "mappingPenalty " + fpga.mappingPenalty + CR;
                code += set + "goIdleTime " + fpga.goIdleTime + CR;
                code += set + "maxConsecutiveIdleCycles " + fpga.maxConsecutiveIdleCycles + CR;
                code += set + "reconfigurationTime " + fpga.reconfigurationTime + CR;
                code += set + "execiTime " + fpga.execiTime + CR;
                code += set + "execcTime " + fpga.execcTime + CR;
                if (fpga.getOperation().length() > 0) {
                    code += set + "operation " + fpga.getOperation() + CR;
                }
                if (fpga.getScheduling().length() > 0) {
                    code += set + "scheduling " + fpga.getScheduling() + CR;
                }

            }

            //HWA
            if (node instanceof HwA) {
                hwa = (HwA) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE HWA " + name + CR;
                code += set + "byteDataSize " + hwa.byteDataSize + CR;
                code += set + "execiTime " + hwa.execiTime + CR;
                code += set + "execcTime " + hwa.execcTime + CR;
                if (hwa.getOperation().length() > 0) {
                    code += set + "operation " + hwa.getOperation() + CR;
                }
            }

            // BUS
            if (node instanceof HwBus) {
                bus = (HwBus) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE BUS " + name + CR;
                code += set + "byteDataSize " + bus.byteDataSize + CR;
                code += set + "pipelineSize " + bus.pipelineSize + CR;
                code += set + "arbitration " + bus.arbitration + CR;
            }


            // Bridge
            if (node instanceof HwBridge) {
                bridge = (HwBridge) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE BRIDGE " + name + CR;
                code += set + "bufferByteSize " + bridge.bufferByteSize + CR;
            }

            // NoC
            if (node instanceof HwNoC) {
                noc = (HwNoC) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE NOC " + name + CR;
                code += set + "bufferByteSize " + noc.bufferByteSize + CR;
                code += set + "NoCSize " + noc.size + CR;
            }

            // Memory
            if (node instanceof HwMemory) {
                memory = (HwMemory) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE MEMORY " + name + CR;
                code += set + "byteDataSize " + memory.byteDataSize + CR;
            }

            // DMA
            if (node instanceof HwDMA) {
                dma = (HwDMA) node;
                name = prepareString(node.getName());
                set = "SET " + name + " ";
                code += "NODE DMA " + name + CR;
                code += set + "byteDataSize " + dma.byteDataSize + CR;
                code += set + "nbOfChannels " + dma.nbOfChannels + CR;
            }

            code += "SET " + prepareString(node.getName()) + " " + "clockDivider " + node.clockRatio + "\n";

            code += CR;

        }
        return code;
    }

    public String makeLinks(TMLArchitecture tmla) {
        String code = "";
        String name;
        String set;
        List<HwLink> hwlinks = tmla.getHwLinks();

        //TraceManager.addDev("Making links");
        for (HwLink link : hwlinks) {
            //TraceManager.addDev("Link");
            if (link instanceof HwLink) {
                if ((link.hwnode != null) && (link.bus != null)) {
                    name = prepareString(link.getName());
                    set = "SET " + name + " ";
                    code += "NODE LINK " + name + CR;
                    code += set + "node " + prepareString(link.hwnode.getName()) + CR;
                    code += set + "bus " + prepareString(link.bus.getName()) + CR;
                    code += set + "priority " + link.getPriority() + CR;
                }
            }
        }
        //TraceManager.addDev("Links:done");

        return code;
    }


    // FROM Text file to TML ARCHITECTURE

    public boolean makeTMLArchitecture(String _spec) {
        spec = _spec;
        tmla = new TMLArchitecture();
        errors = new ArrayList<TMLTXTError>();
        warnings = new ArrayList<TMLTXTError>();

        spec = Conversion.removeComments(spec);
        //TraceManager.addDev(spec);
        browseCode();

        return (errors.size() == 0);
    }

    public String printErrors() {
        String ret = "";
        for (TMLTXTError error : errors) {
            ret += "ERROR at line " + error.lineNb + ": " + error.message + CR;
            try {
                ret += "->" + spec.split("\n")[error.lineNb] + CR2;
            } catch (Exception e) {
                ret += "(Code line not accessible)" + CR;
            }
        }
        return ret;
    }

    public String printWarnings() {
        String ret = "";
        for (TMLTXTError error : warnings) {
            ret += "WARNING at line " + error.lineNb + CR;
            ret += error.message + CR;
        }
        return ret;
    }

    public String printSummary() {
        String ret = "";
        if (errors.size() == 0) {
            ret += printWarnings();
            ret += "Compilation successful" + CR;
            ret += "No error, " + warnings.size() + " warning(s)" + CR;
        } else {
            ret += printErrors() + CR + printWarnings() + CR;
            ret += "Compilation failed" + CR;
            ret += errors.size() + " error(s), " + warnings.size() + " warning(s)" + CR;
        }

        return ret;
    }

    public void browseCode() {
        // Browse lines of code one after the other
        // Build accordingly the TMLModeling and updates errors and warnings
        // In case of fatal error, immediately quit code browsing

        StringReader sr = new StringReader(spec);
        BufferedReader br = new BufferedReader(sr);
        String s;
        String s1;
        String[] split;
        int lineNb = 0;

        //   String instruction;

        try {
            while ((s = br.readLine()) != null) {
                if (s != null) {
                    s = s.trim();
                    //TraceManager.addDev("s=" + s);
                    s = removeUndesiredWhiteSpaces(s, lineNb);
                    s1 = Conversion.replaceAllString(s, "\t", " ");
                    s1 = Conversion.replaceRecursiveAllString(s1, "  ", " ");
                    //TraceManager.addDev("s1=" + s1);
                    if (s1 != null) {
                        split = s1.split("\\s");
                        if (split.length > 0) {
                            //TraceManager.addDev("analyse");
                            analyseInstruction(s, lineNb, split);
                            //TraceManager.addDev("end analyse");
                        }
                    }

                    lineNb++;
                }
            }
        } catch (Exception e) {
            TraceManager.addDev("Exception when reading specification: " + e.getMessage());
            addError(0, lineNb, 0, "Exception when reading specification");
        }
    }

    public void addError(int _type, int _lineNb, int _charNb, String _msg) {
        TMLTXTError error = new TMLTXTError(_type);
        error.lineNb = _lineNb;
        error.charNb = _charNb;
        error.message = _msg;
        errors.add(error);
    }

    public int analyseInstruction(String _line, int _lineNb, String[] _split) {
        String error;
//        String params;
//        String id;
        int value;


        // Master clock frequency
        if (isInstruction("MASTERCLOCKFREQUENCY", _split[0])) {

            if (_split.length != 2) {
                error = "A master clock frequency must be declared with 1 parameter, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("NODE", _split, 1, 1, _lineNb)) {
                error = "A master clock frequency must be provided as a positive int number";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            try {
                value = Integer.decode(_split[1]).intValue();
            } catch (Exception e) {
                error = "A master clock frequency must be provided as a positive int number";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (value < 0) {
                error = "A master clock frequency must be provided as a positive int number";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            //TraceManager.addDev("Master clock frequency = " + value);
            tmla.setMasterClockFrequency(value);

            // NODE
        } else if (isInstruction("NODE", _split[0])) {

            if (_split.length != 3) {
                error = "A node must be declared with 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("NODE", _split, 1, 2, _lineNb)) {
                return -1;
            }

            if (!checkParameter("NODE", _split, 2, 0, _lineNb)) {
                return -1;
            }

            //TraceManager.addDev("NEW NODE =" + _split[1]);

            if (_split[1].equals("CPU")) {
                HwCPU cpu = new HwCPU(_split[2]);
                tmla.addHwNode(cpu);
                //TraceManager.addDev("Adding CPU:" + cpu.getName());
            } else if (_split[1].equals("FPGA")) {
                HwFPGA fpga = new HwFPGA(_split[2]);
                tmla.addHwNode(fpga);
            } else if (_split[1].equals("BUS")) {
                HwBus bus = new HwBus(_split[2]);
                tmla.addHwNode(bus);
            } else if (_split[1].equals("MEMORY")) {
                HwMemory memory = new HwMemory(_split[2]);
                tmla.addHwNode(memory);
            } else if (_split[1].equals("DMA")) {
                HwDMA dma = new HwDMA(_split[2]);
                tmla.addHwNode(dma);
            } else if (_split[1].equals("BRIDGE")) {
                HwBridge bridge = new HwBridge(_split[2]);
                tmla.addHwNode(bridge);
            } else if (_split[1].equals("NOC")) {
                HwNoC noc = new HwNoC(_split[2]);
                tmla.addHwNode(noc);
            } else if (_split[1].equals("HWA")) {
                HwA hwa = new HwA(_split[2]);
                tmla.addHwNode(hwa);
            } else if (_split[1].equals("LINK")) {
                HwLink link = new HwLink(_split[2]);
                tmla.addHwLink(link);
            }

        } // NODE

        // SET
        if (isInstruction("SET", _split[0])) {

            if (_split.length < 4) {
                error = "A set instruction must be used with 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("SET", _split, 1, 0, _lineNb)) {
                return -1;
            }


            HwLink link;
            HwNode node = tmla.getHwNodeByName(_split[1]);
            if (node != null) {
                //TraceManager.addDev("Handling node=" + node.getName());
            } else {
                //TraceManager.addDev("Null node=" + _split[1]);
            }

            if (node == null) {
                link = tmla.getHwLinkByName(_split[1]);
                if (link == null) {
                    error = "Unknown node: " + _split[1];
                    addError(0, _lineNb, 0, error);
                    return -1;
                } else {
                    // Link node

                    if (link instanceof HwLink) {
                        if (!checkParameter("SET", _split, 2, 8, _lineNb)) {
                            return -1;
                        }

                        if (!checkParameter("SET", _split, 3, 7, _lineNb)) {
                            return -1;
                        }

                        if (_split[2].toUpperCase().equals("NODE")) {
                            HwNode node0 = tmla.getHwNodeByName(_split[3]);
                            if (node0 == null) {
                                error = "Unknown node: " + _split[3];
                                addError(0, _lineNb, 0, error);
                                return -1;
                            } else {
                                link.hwnode = node0;
                            }
                        }

                        if (_split[2].toUpperCase().equals("BUS")) {
                            HwBus bus0 = tmla.getHwBusByName(_split[3]);
                            if (bus0 == null) {
                                error = "Unknown bus: " + _split[3];
                                addError(0, _lineNb, 0, error);
                                return -1;
                            } else {
                                link.bus = bus0;
                            }
                        }

                        if (_split[2].toUpperCase().equals("PRIORITY")) {
                            link.setPriority(Integer.decode(_split[3]).intValue());
                        }
                    }
                }
            } else {
                if (node instanceof HwCPU) {
                    HwCPU cpu = (HwCPU) node;

                    //TraceManager.addDev("Seeting 1" + _split[2] + " in " + cpu.getName());


                    if (!checkParameter("SET", _split, 2, 3, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().compareTo("OPERATION") != 0) {
                        if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                            return -1;
                        }
                    }

                    //TraceManager.addDev("Setting 2" + _split[2] + " in " + cpu.getName());


                    if (_split[2].toUpperCase().equals("NBOFCORES")) {
                        cpu.nbOfCores = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        cpu.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("PIPELINESIZE")) {
                        cpu.pipelineSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("GOIDLETIME")) {
                        cpu.goIdleTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("MAXCONSECUTIVEIDLECYCLES")) {
                        cpu.maxConsecutiveIdleCycles = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("TASKSWITCHINGTIME")) {
                        cpu.taskSwitchingTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("BRANCHINGPREDICTIONPENALTY")) {
                        cpu.branchingPredictionPenalty = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CACHEMISS")) {
                        cpu.cacheMiss = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("SCHEDULINGPOLICY")) {
                        cpu.schedulingPolicy = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("SLICETIME")) {
                        cpu.sliceTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("EXECITIME")) {
                        cpu.execiTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("EXECCTIME")) {
                        cpu.execcTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        cpu.clockRatio = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("OPERATION")) {
                        String tmpOp = "";
                        for (int i=3; i<_split.length; i++) {
                            tmpOp += _split[i] + " ";
                        }
                        TraceManager.addDev("Setting op in " + cpu.getName() + " to " + tmpOp);
                        cpu.setOperation(tmpOp.trim());
                    }
                }

                if (node instanceof HwFPGA) {
                    HwFPGA fpga = (HwFPGA) node;

                    if (!checkParameter("SET", _split, 2, 13, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("CAPACITY")) {
                        fpga.capacity = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        fpga.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("MAPPINGPENALTY")) {
                        fpga.mappingPenalty = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("GOIDLETIME")) {
                        fpga.goIdleTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("MAXCONSECUTIVEIDLECYCLES")) {
                        fpga.maxConsecutiveIdleCycles = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("RECONFIGURATIONTIME")) {
                        fpga.reconfigurationTime = Integer.decode(_split[3]).intValue();
                    }


                    if (_split[2].toUpperCase().equals("EXECITIME")) {
                        fpga.execiTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("EXECCTIME")) {
                        fpga.execcTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        fpga.clockRatio = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("OPERATION")) {
                        String tmpOp = "";
                        for (int i=3; i<_split.length; i++) {
                            tmpOp += _split[i] + " ";
                        }
                        fpga.setOperation(tmpOp.trim());

                    }

                    if (_split[2].toUpperCase().equals("SCHEDULING")) {
                        String tmpOp = "";
                        for (int i=3; i<_split.length; i++) {
                            tmpOp += _split[i] + " ";
                        }

                        fpga.setScheduling(tmpOp.trim());
                    }
                }

                if (node instanceof HwA) {
                    HwA hwa = (HwA) node;

                    TraceManager.addDev("HWA = " + _split[2]);

                    if (!checkParameter("SET", _split, 2, 10, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        hwa.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("EXECITIME")) {
                        hwa.execiTime = Integer.decode(_split[3]).intValue();
                    }

                    TraceManager.addDev("Testing HWA = " + _split[2]);

                    if (_split[2].toUpperCase().equals("EXECCTIME")) {
                        hwa.execcTime = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        hwa.clockRatio = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("OPERATION")) {
                        String tmpOp = "";
                        for (int i=3; i<_split.length; i++) {
                            tmpOp += _split[i] + " ";
                        }

                        hwa.setOperation(tmpOp.trim());
                    }
                }

                if (node instanceof HwBus) {
                    HwBus bus = (HwBus) node;

                    if (!checkParameter("SET", _split, 2, 9, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        bus.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("PIPELINESIZE")) {
                        bus.pipelineSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("ARBITRATION")) {
                        bus.arbitration = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        bus.clockRatio = Integer.decode(_split[3]).intValue();
                    }
                }

                if (node instanceof HwBridge) {
                    HwBridge bridge = (HwBridge) node;

                    if (!checkParameter("SET", _split, 2, 11, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BUFFERBYTESIZE")) {
                        bridge.bufferByteSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        bridge.clockRatio = Integer.decode(_split[3]).intValue();
                    }
                }

                if (node instanceof HwNoC) {
                    HwNoC noc = (HwNoC) node;

                    if (!checkParameter("SET", _split, 2, 14, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BUFFERBYTESIZE")) {
                        noc.bufferByteSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("NOCSIZE")) {
                        noc.size = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        noc.clockRatio = Integer.decode(_split[3]).intValue();
                    }

                }

                if (node instanceof HwMemory) {
                    HwMemory memory = (HwMemory) node;

                    if (!checkParameter("SET", _split, 2, 12, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        memory.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        memory.clockRatio = Integer.decode(_split[3]).intValue();
                    }
                }

                if (node instanceof HwDMA) {
                    HwDMA dma = (HwDMA) node;

                    if (!checkParameter("SET", _split, 2, 12, _lineNb)) {
                        return -1;
                    }

                    if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
                        return -1;
                    }

                    if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
                        dma.byteDataSize = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("NBOFCHANNELS")) {
                        dma.nbOfChannels = Integer.decode(_split[3]).intValue();
                    }

                    if (_split[2].toUpperCase().equals("CLOCKDIVIDER")) {
                        dma.clockRatio = Integer.decode(_split[3]).intValue();
                    }

                }
            }

        } // SET

        // Other command
        //TraceManager.addDev("ERROR hm hm");
        if ((_split[0].length() > 0) && (!(isInstruction(_split[0])))) {
            error = "Syntax error: unrecognized instruction: " + _split[0];
            addError(0, _lineNb, 0, error);
            return -1;

        } // Other command

        return 0;
    }

    // Type 0: id
    // Type 1: numeral
    // Type 2: Node type
    // Type 3: CPU parameter
    // Type 5: '='
    // Type 6: attribute value
    // Type 7: id or numeral
    // Type 8: LINK parameter
    // Type 9: BUS parameter
    // Type 10: HWA parameter
    // Type 11: BRIDGE parameter
    // Type 12: MEMORY parameter
    // Type 13: FPGA parameter

    public boolean checkParameter(String _inst, String[] _split, int _parameter, int _type, int _lineNb) {
        boolean err = false;
        String error;

        if (_parameter < _split.length) {
            switch (_type) {
                case 0:
                    if (!isAValidId(_split[_parameter])) {
                        err = true;
                    }
                    break;
                case 1:
                    if (!isANumeral(_split[_parameter])) {
                        err = true;
                    }
                    break;
                case 2:
                    if (!isIncluded(_split[_parameter], nodetypes)) {
                        err = true;
                    }
                    break;
                case 3:
                    if (!isIncluded(_split[_parameter], cpuparameters)) {
                        err = true;
                    }
                    break;
                case 4:
                    if (!isAValidId(getEvtId(_split[_parameter]))) {
                        err = true;
                        //TraceManager.addDev("Unvalid id");
                    } else if (!TMLEvent.isAValidListOfParams(getParams(_split[_parameter]))) {
                        //TraceManager.addDev("Unvalid param");
                        err = true;
                    }
                    break;
                case 5:
                    if (!(_split[_parameter].equals("="))) {
                        TraceManager.addDev("Error of =");
                        err = true;
                    }
                    break;
                case 6:
                    if (_inst.equals("BOOL")) {
                        String tmp = _split[_parameter].toUpperCase();
                        if (!(tmp.equals("TRUE") || tmp.equals("FALSE"))) {
                            err = true;
                        }
                    } else {
                        if (!isANumeral(_split[_parameter])) {
                            err = true;
                        }
                    }
                    break;
                case 7:
                    if (!isAValidId(_split[_parameter]) && !isANumeral(_split[_parameter])) {
                        err = true;
                    }
                    break;
                case 8:
                    if (!isIncluded(_split[_parameter], linkparameters)) {
                        err = true;
                    }
                    break;
                case 9:
                    if (!isIncluded(_split[_parameter], busparameters)) {
                        err = true;
                    }
                    break;
                case 10:
                    if (!isIncluded(_split[_parameter], hwaparameters)) {
                        err = true;
                    }
                    break;
                case 11:
                    if (!isIncluded(_split[_parameter], bridgeparameters)) {
                        err = true;
                    }
                    break;
                case 12:
                    if (!isIncluded(_split[_parameter], memoryparameters)) {
                        err = true;
                    }
                    break;
                case 13:
                    if (!isIncluded(_split[_parameter], fpgaparameters)) {
                        err = true;
                    }
                    break;
                case 14:
                    if (!isIncluded(_split[_parameter], nocparameters)) {
                        err = true;
                    }
                    break;

            }
        } else {
            err = true;
        }
        if (err) {
            error = "Unvalid parameter #" + _parameter + " ->" + _split[_parameter] + "<- in " + _inst + " instruction";
            addError(0, _lineNb, 0, error);
            return false;
        }
        return true;
    }

    public boolean isInstruction(String instcode, String inst) {
        return (inst.toUpperCase().compareTo(instcode) == 0);
    }

    public boolean isInstruction(String instcode) {
        return (!checkKeywords(instcode));
    }

    public boolean isAValidId(String _id) {
        if ((_id == null) || (_id.length() == 0)) {
            return false;
        }

        boolean b1 = (_id.substring(0, 1)).matches("[a-zA-Z]");
        boolean b2 = _id.matches("\\w*");
        boolean b3 = checkKeywords(_id);

        return (b1 && b2 && b3);
    }

    public boolean isANumeral(String _num) {
        return _num.matches("\\d*");
    }

    public boolean checkKeywords(String _id) {
        String id = _id.toUpperCase();
        for (int i = 0; i < keywords.length; i++) {
            if (id.compareTo(keywords[i]) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isIncluded(String _id, String[] _list) {
        String id = _id.toUpperCase();
        for (int i = 0; i < _list.length; i++) {
            if (id.compareTo(_list[i].toUpperCase()) == 0) {
                return true;
            }
        }
        return false;
    }

    public String removeUndesiredWhiteSpaces(String _input, int _lineNb) {
//        String error, tmp;
//        int index0, index1, index2;

        return _input;
    }

    private String getEvtId(String _input) {
        int index = _input.indexOf('(');
        if (index == -1) {
            return _input;
        }
        return _input.substring(0, index);
    }

    private String getParams(String _input) {
        //TraceManager.addDev("input=" + _input);
        int index0 = _input.indexOf('(');
        int index1 = _input.indexOf(')');
        if ((index0 == -1) || (index1 == -1)) {
            return _input;
        }
        return _input.substring(index0 + 1, index1);
    }

    private String prepareString(String s) {
        return s.replaceAll("\\s", "");
    }
}

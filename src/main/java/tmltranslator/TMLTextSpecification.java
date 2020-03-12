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

import common.SpecConfigTTool;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class TMLTextSpecification
 * Import and export of TML textual specifications
 * Creation: 12/09/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.0 12/09/2007
 */
public class TMLTextSpecification<E> {
    public final static String CR = "\n";
    public final static String SP = " ";
    public final static String CR2 = "\n\n";
    public final static String SC = ";";
    public final static String C = ",";


    public final static String AENCRYPT = "AE";
    public final static String SENCRYPT = "SE";
    public final static String MAC = "MAC";
    public final static String NONCE = "NONCE";
    public final static String HASH = "HASH";
    public final static String ADV = "ADV";


    private String spec;
    private String title;

    private TMLModeling<E> tmlm;
    private ArrayList<TMLTXTError> errors;
    private ArrayList<TMLTXTError> warnings;

    // For reading TMLTXT specifications
    private boolean inDec = true;
    private boolean inTask = false;
    private boolean inTaskDec = false;
    //  private boolean inTaskBehavior = false;
    private TMLTask task;
    private TMLActivityElement tmlae;
    private ArrayList<TMLParserSaveElt> parses;

    private Map<String, SecurityPattern> securityPatternMap = new HashMap<String, SecurityPattern>();

    private static String keywords[] = {"BOOL", "INT", "NAT", "CHANNEL", "EVENT", "REQUEST", "LOSSYCHANNEL", "VCCHANNEL",
            "LOSSYEVENT", "LOSSYREQUEST", "BRBW", "NBRNBW",
            "BRNBW", "INF", "NIB", "NINB", "TASK", "ENDTASK", "TASKOP", "IF", "ELSE", "ORIF", "ENDIF", "FOR", "ENDFOR",
            "SELECTEVT", "CASE", "ENDSELECTEVT", "ENDCASE", "WRITE", "READ", "WAIT", "NOTIFY", "NOTIFIED", "NOTIFYREQUEST", "RAND", "CASERAND",
            "ENDRAND",
            "ENDCASERAND", "EXECI", "EXECC", "DELAY", "RANDOM",
            "RANDOMSEQ", "ENDRANDOMSEQ", "SEQ", "ENDSEQ", "PRAGMA"};

    private String channeltypes[] = {"BRBW", "NBRNBW", "BRNBW"};
    private String eventtypes[] = {"INF", "NIB", "NINB"};

    private String beginArray[] = {"TASK", "FOR", "IF", "ELSE", "ORIF", "SELECTEVT", "CASE", "RAND", "CASERAND", "RANDOMSEQ", "SEQ"};
    private String endArray[] = {"ENDTASK", "ENDFOR", "ENDIF", "ELSE", "ORIF", "ENDSELECTEVT", "ENDCASE", "ENDRAND", "ENDCASERAND", "ENDRANDOMSEQ", "ENDSEQ"};

// New argument to be added on EXECC for security: CC_name Type Encrypt_complexity Decrypt_Complexity Overhead Size Nonce Key
// New argument on Read/Write Channels: CC_name


    public TMLTextSpecification(String _title) {
        title = _title;
    }

    public TMLTextSpecification(String _title, boolean reset) {
        title = _title;
        if (reset) {
            DIPLOElement.resetID();
        }
    }

    public void saveFile(String path, String filename) throws FileException {
        SpecConfigTTool.checkAndCreateTMLDir(path);
        TraceManager.addUser("Saving TML spec file in " + path + filename);
        FileUtils.saveFile(path + filename, spec);
    }

    public TMLModeling<E> getTMLModeling() {
        return tmlm;
    }

    public ArrayList<TMLTXTError> getErrors() {
        return errors;
    }

    public ArrayList<TMLTXTError> getWarnings() {
        return warnings;
    }


    public void indent() {
        indent(4);
    }

    public void indent(int _nbDec) {
        int dec = 0;
        int indexEnd;
        String output = "";
        String tmp;
        int nbOpen = 0;
        int nbClose = 0;

        while ((indexEnd = spec.indexOf('\n')) > -1) {
            tmp = spec.substring(0, indexEnd + 1);
            try {
                spec = spec.substring(indexEnd + 1, spec.length());
            } catch (Exception e) {
                spec = "";
            }
            nbOpen = nbOfOpen(tmp);
            nbClose = nbOfClose(tmp);
            dec -= nbClose * _nbDec;
            tmp = Conversion.addHead(tmp.trim(), ' ', dec);
            dec += nbOpen * _nbDec;
            //TraceManager.addDev("dec=" + dec);
            output += tmp + "\n";
        }
        spec = output;
    }

    private int nbOfOpen(String tmp) {
        return nbOf(tmp, beginArray);
    }

    private int nbOfClose(String tmp) {
        return nbOf(tmp, endArray);
    }

    private int nbOf(String _tmp, String[] array) {
        String tmp;
        //   int size;

        for (int i = 0; i < array.length; i++) {
            if (_tmp.startsWith(array[i])) {
                tmp = _tmp.substring(array[i].length(), _tmp.length());
                //TraceManager.addDev("tmp=" + tmp + " _tmp" + _tmp + " array=" + array[i]);
                if ((tmp.length() == 0) || (tmp.charAt(0) == ' ') || (tmp.charAt(0) == '(') || (tmp.charAt(0) == '\n')) {
                    //TraceManager.addDev("Returning 1!!");
                    return 1;
                }
            }
        }
        return 0;
    }

    public String toString() {
        return spec;
    }


    public String toTextFormat(TMLModeling<E> tmlm) {
        tmlm.removeForksAndJoins();
        tmlm.sortByName();

        // Print TMLTasks WCET
        tmlm.printWCETOfTasks();


        spec = makeDeclarations(tmlm);
        //Set up Cryptographic Configurations

        spec += makeTasks(tmlm);
        indent();
        return spec;
    }


    public String makeDeclarations(TMLModeling<E> tmlm) {
        int i;
        String sb = "";
        sb += "// TML Application - FORMAT 0.2" + CR;
        sb += "// Application: " + title + CR;
        sb += "// Generated: " + new Date().toString() + CR2;

        sb += "// PRAGMAS" + CR;
        for(String s: tmlm.getPragmas()) {
            sb += "PRAGMA " + s + CR;
        }
        sb += CR;

        sb += "// Channels" + CR;
        for (TMLChannel ch : tmlm.getChannels()) {
            if (ch.isBasicChannel()) {
                sb += "CHANNEL" + SP + ch.getName() + SP + TMLChannel.getStringType(ch.getType()) + SP + ch.getSize();
                if (!ch.isInfinite()) {
                    sb += SP + ch.getMax();
                }
                //TraceManager.addDev("Declaration. Handling channel " + ch.getName());
                if (ch.getOriginTask() == null) {
                    TraceManager.addDev("Missing origin Task in " + ch.getName());
                }
                if (ch.getDestinationTask() == null) {
                    TraceManager.addDev("Missing destination Task in " + ch.getName());
                }
                sb += SP + "OUT" + SP + ch.getOriginTask().getName() + SP + "IN" + SP + ch.getDestinationTask().getName() + CR;

                if (ch.isLossy()) {
                    sb += "LOSSYCHANNEL" + SP + ch.getName() + SP + ch.getLossPercentage() + SP + ch.getMaxNbOfLoss() + CR;
                }

                if (ch.getVC() >= 0) {
                    sb += "VCCHANNEL" + SP + ch.getName() + SP + ch.getVC() + CR;
                }
            } else {
                sb += "CHANNEL" + SP + ch.getName() + SP + TMLChannel.getStringType(ch.getType()) + SP + ch.getSize();
                if (!ch.isInfinite()) {
                    sb += SP + ch.getMax();
                }


                sb += SP + "OUT";
                for (TMLTask task : ch.getOriginTasks()) {
                    sb += SP + task.getName();
                }
                sb += SP + "IN";
                for (TMLTask task : ch.getDestinationTasks()) {
                    sb += SP + task.getName();
                }
                sb += CR;


                if (ch.isLossy()) {
                    sb += "LOSSYCHANNEL" + SP + ch.getName() + SP + ch.getLossPercentage() + SP + ch.getMaxNbOfLoss() + CR;
                }

                if (ch.getVC() >= 0) {
                    sb += "VCCHANNEL" + SP + ch.getName() + SP + ch.getVC() + CR;
                }
            }


        }
        sb += CR;

        sb += "// Events" + CR;
        for (TMLEvent evt : tmlm.getEvents()) {
            sb += "EVENT" + SP + evt.getName() + "(";
            for (i = 0; i < evt.getNbOfParams(); i++) {
                if (i != 0) {
                    sb += ", ";
                }
                sb += TMLType.getStringType(evt.getType(i).getType());
            }
            sb += ")";
            sb += SP + evt.getTypeTextFormat();
            if (!evt.isInfinite()) {
                sb += SP + evt.getMaxSize();
            }

            //TraceManager.addDev("Handing Event:" + evt.getName());
            if (evt.getOriginTask() == null) {
                TraceManager.addDev("Missing origin Task in " + evt.getName());
            }
            if (evt.getDestinationTask() == null) {
                TraceManager.addDev("Missing destination Task in " + evt.getName());
            }
            sb += SP + evt.getOriginTask().getName() + SP + evt.getDestinationTask().getName();

            sb += CR;

            if (evt.isLossy()) {
                sb += "LOSSYEVENT" + SP + evt.getName() + SP + evt.getLossPercentage() + SP + evt.getMaxNbOfLoss() + CR;
            }
        }
        sb += CR;

        sb += "// Requests" + CR;
        for (TMLRequest request : tmlm.getRequests()) {
            sb += "REQUEST" + SP + request.getName() + "(";
            for (i = 0; i < request.getNbOfParams(); i++) {
                if (i != 0) {
                    sb += ", ";
                }
                sb += TMLType.getStringType(request.getType(i).getType());
            }
            sb += ")";
            for (TMLTask t : request.getOriginTasks()) {
                sb += SP + t.getName();
            }
            sb += SP + request.getDestinationTask().getName();
            sb += CR;

            if (request.isLossy()) {
                sb += "LOSSYREQUEST" + SP + request.getName() + SP + request.getLossPercentage() + SP + request.getMaxNbOfLoss() + CR;
            }
        }
        sb += CR;

        return sb;

    }

    public String makeTasks(TMLModeling<E> tmlm) {
        String sb = "";
        for (TMLTask task : tmlm.getTasks()) {
            sb += "TASK" + SP + task.getName() + CR;
            if (task.isDaemon()) {
                sb += "DAEMON" + CR;
            }
            if (task.isPeriodic()) {
                sb += "PERIODIC " + task.getPeriodValue() + SP + task.getPeriodUnit() + CR;
            }
            sb += "TASKOP" + SP + task.getOperation() + CR;
            sb += makeActivity(task);
            sb += "ENDTASK" + CR2;
        }
        return sb;
    }

    public String makeActivity(TMLTask task) {
        String sb = "";
        sb += "//Local variables" + CR;

        //TraceManager.addDev("Making activity of task:" + task.getTaskName());


        for (TMLAttribute attr : task.getAttributes()) {
            sb += TMLType.getStringType(attr.getType().getType()) + SP + attr.getName();
            if ((attr.getInitialValue() != null) && (attr.getInitialValue().length() > 0)) {
                sb += " = " + attr.getInitialValue();
            }
            sb += CR;
        }

        sb += CR;
        sb += "//Behavior" + CR;
        sb += makeBehavior(task, task.getActivityDiagram().getFirst());

        return sb;
    }

    public String makeBehavior(TMLTask task, TMLActivityElement elt) {
        String code,/* code1,*/ code2;
        TMLForLoop tmlfl;
        TMLActivityElementChannel tmlch;
        TMLActivityElementEvent tmlevt;
        TMLSendRequest tmlreq;
        // TMLEvent evt;
        TMLRandom random;
        int i;
        String tmp1, tmp2;

        if (elt instanceof TMLStartState) {
            return makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLStopState) {
            return "";

        } else if (elt instanceof TMLExecI) {
            code = "EXECI" + SP + modifyString(((TMLExecI) elt).getAction()) + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecIInterval) {
            code = "EXECI" + SP + modifyString(((TMLExecIInterval) elt).getMinDelay()) + SP + modifyString(((TMLExecIInterval) elt).getMaxDelay()) + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecC) {
            if (elt.securityPattern == null) {
                code = "EXECC" + SP + modifyString(((TMLExecC) elt).getAction()) + CR;
            } else {
                String type = "";
                if (elt.securityPattern.type.equals("Asymmetric Encryption")) {
                    type = AENCRYPT;
                } else if (elt.securityPattern.type.equals("Symmetric Encryption")) {
                    type = SENCRYPT;
                } else if (elt.securityPattern.type.equals("MAC")) {
                    type = MAC;
                } else if (elt.securityPattern.type.equals("Nonce")) {
                    type = NONCE;
                } else if (elt.securityPattern.type.equals("Hash")) {
                    type = HASH;
                } else {
                    type = ADV;
                }
                code = "EXECC" + SP + modifyString(((TMLExecC) elt).getAction()) + SP + elt.securityPattern.name + SP + type + SP + elt.securityPattern.encTime + SP + elt.securityPattern.decTime + SP + elt.securityPattern.overhead + SP + elt.securityPattern.size + SP + elt.securityPattern.nonce + SP + elt.securityPattern.key + CR;
            }
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLExecCInterval) {
            code = "EXECC" + SP + modifyString(((TMLExecCInterval) elt).getMinDelay()) + SP + modifyString(((TMLExecCInterval) elt).getMaxDelay()) + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLDelay) {
            tmp1 = ((TMLDelay) elt).getMinDelay();
            tmp2 = ((TMLDelay) elt).getMaxDelay();
            if (tmp1.compareTo(tmp2) == 0) {
                code = "DELAY" + SP + modifyString(((TMLDelay) elt).getMinDelay()) + SP + modifyString(((TMLDelay) elt).getUnit()) + SP + "isActiveDelay" + SP + ((TMLDelay) elt).getActiveDelay() + CR;
            } else {
                code = "DELAY" + SP + modifyString(((TMLDelay) elt).getMinDelay()) + SP + modifyString(((TMLDelay) elt).getMaxDelay()) + SP + modifyString(((TMLDelay) elt).getUnit()) + SP + "isActiveDelay" + SP + ((TMLDelay) elt).getActiveDelay() + CR;
            }
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLForLoop) {
            tmlfl = (TMLForLoop) elt;
            if (tmlfl.isInfinite()) {
                code = "FOR( " + SC + " " + SC + " )" + CR;
            } else {
                code = "FOR(" + tmlfl.getInit() + SC + SP;
                code += tmlfl.getCondition() + SC + SP;
                code += tmlfl.getIncrement() + ")" + CR;
            }
            code += makeBehavior(task, elt.getNextElement(0));

            return code + "ENDFOR" + CR + makeBehavior(task, elt.getNextElement(1));

        } else if (elt instanceof TMLRandom) {
            random = (TMLRandom) elt;
            code = "RANDOM" + SP + modifyString("" + random.getFunctionId()) + SP;
            code += modifyString(random.getVariable()) + SP;
            code += modifyString(random.getMinValue()) + SP;
            code += modifyString(random.getMaxValue()) + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLActionState) {
            code = modifyString(((TMLActivityElementWithAction) elt).getAction()) + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLWriteChannel) {
            tmlch = (TMLActivityElementChannel) elt;
            code = "WRITE ";
            for (int k = 0; k < tmlch.getNbOfChannels(); k++) {
                code = code + tmlch.getChannel(k).getName() + SP;
            }
            //TraceManager.addDev("Nb of samples in task " + task.getName() + " = " + tmlch.getNbOfSamples());
            code = code + modifyString(tmlch.getNbOfSamples());
            if (elt.securityPattern != null) {
                code = code + SP + elt.securityPattern.name + CR;
            } else {
                code = code + CR;
            }
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLReadChannel) {
            tmlch = (TMLActivityElementChannel) elt;
            if (elt.securityPattern == null) {
                code = "READ " + tmlch.getChannel(0).getName() + SP + modifyString(tmlch.getNbOfSamples()) + CR;
            } else {
                code = "READ " + tmlch.getChannel(0).getName() + SP + modifyString(tmlch.getNbOfSamples()) + SP + elt.securityPattern.name + CR;
            }
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLSendEvent) {
            tmlevt = (TMLActivityElementEvent) elt;
            code = "NOTIFY " + tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLWaitEvent) {
            tmlevt = (TMLActivityElementEvent) elt;
            code = "WAIT " + tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLNotifiedEvent) {
            tmlevt = (TMLActivityElementEvent) elt;
            code = "NOTIFIED " + tmlevt.getEvent().getName() + " " + tmlevt.getVariable() + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLSendRequest) {
            tmlreq = (TMLSendRequest) elt;
            code = "NOTIFYREQUEST " + tmlreq.getRequest().getName() + " " + tmlreq.getAllParams(" ") + CR;
            return code + makeBehavior(task, elt.getNextElement(0));

        } else if (elt instanceof TMLSequence) {
            code = "";
            for (i = 0; i < elt.getNbNext(); i++) {
                code += makeBehavior(task, elt.getNextElement(i));
            }

            return code;

        } else if (elt instanceof TMLChoice) {
            TMLChoice choice = (TMLChoice) elt;
            code = "";
            if (choice.getNbGuard() != 0) {
                // code1 = "";
                int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
                int nb = Math.max(choice.nbOfNonDeterministicGuard(), choice.nbOfStochasticGuard());
                if (nb > 0) {
                    // Assumed to be a non deterministic choice
                    TraceManager.addDev("Non determinitic choice in task " + task.getTaskName()+ " nb=" + nb +
                                    " NonDeterministic:" + choice.nbOfNonDeterministicGuard()
                                    + " nbOfStochastic:" + choice.nbOfStochasticGuard()
                                    + " nbOfGuards:" + choice.getNbGuard() + " choice=" + choice.customExtraToXML());
                    code += "RAND" + CR;
                }
                nb = 0;
                for (i = 0; i < choice.getNbGuard(); i++) {
                    if (i != index2) {
                        if (choice.isNonDeterministicGuard(i)) {
                            code2 = "" + (int) (Math.floor(100 / choice.getNbGuard()));
                            nb++;
                        } else if (choice.isStochasticGuard(i)) {
                            code2 = prepareString(choice.getStochasticGuard(i));
                            nb++;
                        } else {
                            code2 = modifyString(choice.getGuard(i));
                            code2 = Conversion.replaceAllChar(code2, '[', "(");
                            code2 = Conversion.replaceAllChar(code2, ']', ")");
                        }
                        //TraceManager.addDev("guard = " + code1 + " i=" + i);
                        if (nb != 0) {
                            /*if (choice.isNonDeterministicGuard(i)) {
                              code = "CASERAND 50";
                              } else {
                              code = "CASERAND " + prepareString(choice.getStochasticGuard(i));

                              }*/
                            //nb ++;
                            if (i != index1) {
                                code += "CASERAND " + code2 + CR;
                                code += makeBehavior(task, elt.getNextElement(i));
                                code += "ENDCASERAND" + CR;
                            }
                        } else {
                            if (i == 0) {
                                code += "IF " + code2;
                            } else {
                                if (i != index1) {
                                    code += "ORIF " + code2;
                                } else {
                                    code += "ELSE";
                                }
                            }
                            code += CR + makeBehavior(task, elt.getNextElement(i));
                        }
                    }
                }
                if (nb > 0) {
                    // Assumed to be a non deterministic choice
                    code += "ENDRAND" + CR;
                } else {
                    code += "ENDIF" + CR;
                }
                if (index2 != -1) {
                    code += makeBehavior(task, elt.getNextElement(index2));
                }
            }
            return code;

        } else if (elt instanceof TMLSelectEvt) {
            code = "SELECTEVT" + CR;
            for (i = 0; i < elt.getNbNext(); i++) {
                try {
                    tmlevt = (TMLActivityElementEvent) (elt.getNextElement(i));
                    code += "CASE ";
                    code += tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
                    code += makeBehavior(task, elt.getNextElement(i).getNextElement(0));
                    code += "ENDCASE" + CR;
                } catch (Exception e) {
                    TraceManager.addError("Non-event receiving following a select event operator");
                }
            }
            code += "ENDSELECTEVT" + CR;
            return code;

        } else if (elt instanceof TMLRandomSequence) {
            code = "RANDOMSEQ" + CR;
            for (i = 0; i < elt.getNbNext(); i++) {
                code += "SEQ" + CR;
                code += makeBehavior(task, elt.getNextElement(i));
                code += "ENDSEQ" + CR;
            }
            code += "ENDRANDOMSEQ" + CR;
            return code;

        } else {
            if (elt == null) {
                return "";
            }
            TraceManager.addDev("Unrecognized element: " + elt);
            return makeBehavior(task, elt.getNextElement(0));
        }
    }

    public boolean makeTMLModeling(String _spec) {
        spec = _spec;
        tmlm = new TMLModeling<>();
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
        // Build accordinlgy the TMLModeling and updates errors and warnings
        // In case of fatal error, immedialty quit code bowsing

        StringReader sr = new StringReader(spec);
        BufferedReader br = new BufferedReader(sr);
        String s;
        String s1;
        String[] split;
        int lineNb = 0;

        inDec = true;
        inTask = false;
        inTaskDec = false;
        // inTaskBehavior = false;


        //String instruction;

        parses = new ArrayList<TMLParserSaveElt>();

        //Start by reading once and creating all Cryptographic Configuration
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
                            findSec(split);

                        }
                    }

                    lineNb++;
                }
            }

        } catch (Exception e) {
            TraceManager.addError("Exception when reading specification: " + e.getMessage());
            addError(0, lineNb, 0, "Exception when reading specification");
        }

        lineNb = 0;
        br = new BufferedReader(new StringReader(spec));
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
            TraceManager.addError("Exception when reading specification: " + e.getMessage());
            addError(0, lineNb, 0, "Exception when reading specification");
        }
    }

    public void findSec(String[] _split) {
        if (isInstruction(_split[0], "EXECC")) {
            if (_split.length > 4) {
                String ccName = _split[3];
                String type = _split[4];
                String stringType = "";
                if (type.equals(AENCRYPT)) {
                    stringType = "Symmetric Encryption";
                } else if (type.equals(SENCRYPT)) {
                    stringType = "Symmetric Encryption";
                } else if (type.equals(HASH)) {
                    stringType = "Hash";
                } else if (type.equals(MAC)) {
                    stringType = "MAC";
                } else if (type.equals(NONCE)) {
                    stringType = "Nonce";
                } else if (type.equals(ADV)) {
                    stringType = "Advanced";
                }
                if (!stringType.equals("")) {
                    SecurityPattern sp = new SecurityPattern(ccName, stringType, _split[6], _split[7], _split[4], _split[5], _split[8], "", _split[9]);
                    securityPatternMap.put(ccName, sp);
                }
            }
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
        String params;
        String id;
        TMLChannel ch;
        TMLEvent evt;
        TMLRequest request;
        TMLTask t1, t2;
        TMLAttribute attribute;
        //   TMLType type;
        TMLStopState stop;
        TMLRandom random;
        int tmp, tmp0, tmp1, i;
        int dec = 0;
        boolean blocking;
        TMLParserSaveElt parseElt;

        //TraceManager.addDev("Analyzing instruction:" + _line);

        if (parses.size() > 0) {
            parseElt = parses.get(0);
            if ((parseElt.type == TMLParserSaveElt.SELECTEVT) && ((!isInstruction("CASE", _split[0]) && (!isInstruction("ENDSELECTEVT", _split[0]))))) {
                error = "CASE or ENDSELECTEVT instruction expected";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            if ((parseElt.type == TMLParserSaveElt.RAND) && ((!isInstruction("CASERAND", _split[0]) && (!isInstruction("ENDRAND", _split[0]))))) {
                error = "CASERAND or ENDRAND instruction expected";
                addError(0, _lineNb, 0, error);
                return -1;
            }
        }

        // PRAGMA
        if (isInstruction("PRAGMA", _split[0])) {
            if (!inDec) {
                error = "A pragma must not be declared outside of the declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length < 2) {
                error = "A pragma instruction must contain a pragma";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            String pragma = "";
            for (int cpt=1; cpt<_split.length; cpt++) {
                pragma += _split[cpt] + " ";
            }
            tmlm.addPragma(pragma);
        }


        // CHANNEL
        if (isInstruction("CHANNEL", _split[0])) {
            if (!inDec) {
                error = "A channel may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!((_split.length > 7))) {
                error = "A channel must be declared with at least 7 parameters, and not: " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("CHANNEL", _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("CHANNEL", _split, 2, 2, _lineNb)) {
                return -1;
            }

            if (!checkParameter("CHANNEL", _split, 3, 1, _lineNb)) {
                return -1;
            }

            // Max nb of elements?
            try {
                tmp = Integer.decode(_split[4]).intValue();
                dec = 1;
            } catch (Exception e) {
                dec = 0;
                tmp = 8;
            }


            //TraceManager.addDev("Checking OUT");
            // "OUT" keyword?
            if (!checkParameter("CHANNEL", _split, 4 + dec, 10, _lineNb)) {
                return -1;
            }

            //TraceManager.addDev("Checking other params of channels");
            int indexOfIN = -1;
            for (i = 5 + dec; i < _split.length; i++) {
                if (!checkParameter("CHANNEL", _split, i, 0, _lineNb)) {
                    return -1;
                }
                if (_split[i].compareTo("IN") == 0) {
                    indexOfIN = i;
                }
            }

            if (indexOfIN == -1) {
                error = "\"IN\" keyword is missing";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (tmlm.getChannelByName(_split[1]) != null) {
                error = "Duplicate definition of channel " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split[2].toUpperCase().compareTo("NIB") == 0) {

            }

            ch = new TMLChannel(_split[1], null);
            ch.setTypeByName(_split[2]);
            try {
                tmp = Integer.decode(_split[3]).intValue();
            } catch (Exception e) {
                tmp = 4;
            }
            ch.setSize(tmp);
            ch.setMax(tmp);

            for (i = 5 + dec; i < _split.length; i++) {
                if (i != indexOfIN) {
                    t1 = tmlm.getTMLTaskByName(_split[i]);
                    if (t1 == null) {
                        t1 = new TMLTask(_split[i], null, null);
                        tmlm.addTask(t1);
                        //TraceManager.addDev("New task:" + _split[4+dec]);
                    }
                    ch.addTaskPort(t1, null, (i < indexOfIN));
                }

            }

            ch.toBasicIfPossible();

            if (!(ch.isBasicChannel())) {
                if (ch.isBadComplexChannel()) {
                    error = "A complex channel must be \"1 -> many\" of \"many -> 1\"";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }
            tmlm.addChannel(ch);
        } // CHANNEL


        // LOSSYCHANNEL
        if (isInstruction("LOSSYCHANNEL", _split[0])) {
            if (!inDec) {
                error = "A lossychannel may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!((_split.length > 3) && (_split.length < 5))) {
                error = "A lossychannel must be declared with exactly 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }


            if (!checkParameter("LOSSYCHANNEL", _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYCHANNEL", _split, 2, 1, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYCHANNEL", _split, 3, 9, _lineNb)) {
                return -1;
            }


            ch = tmlm.getChannelByName(_split[1]);
            if (ch == null) {
                error = "lossy channel not previously declared as a regular channel " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            try {
                tmp0 = Integer.decode(_split[2]).intValue();
            } catch (Exception e) {
                tmp0 = 5;
            }
            try {
                tmp1 = Integer.decode(_split[3]).intValue();
            } catch (Exception e) {
                tmp1 = -1;
            }

            ch.setLossy(true, tmp0, tmp1);
        } // LOSSYCHANNEL

        if (isInstruction("VCCHANNEL", _split[0])) {
            if (!inDec) {
                error = "A ycchannel may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length != 3) {
                error = "A vcchannel must be declared with exactly 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }


            if (!checkParameter("VCCHANNEL", _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("VCCHANNEL", _split, 2, 1, _lineNb)) {
                return -1;
            }


            ch = tmlm.getChannelByName(_split[1]);
            if (ch == null) {
                error = "vc channel not previously declared as a regular channel " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            try {
                tmp0 = Integer.decode(_split[2]);
            } catch (Exception e) {
                tmp0 = -1;
            }


            ch.setVC(tmp0);
        } // VCCHANNEL

        // EVENT
        if (isInstruction("EVENT", _split[0])) {
            if (!inDec) {
                error = "An event may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!((_split.length > 4) && (_split.length < 7))) {
                error = "An event must be declared with only 4 or 5 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length == 6) {
                dec = 1;
            } else {
                dec = 0;
            }

            id = getEvtId(_split[1]);
            params = getParams(_split[1]);

            //TraceManager.addDev("Evt id=" + id +  "params=" + params);

            if (!checkParameter("EVENT", _split, 1, 4, _lineNb)) {
                return -1;
            }

            if (!checkParameter("EVENT", _split, 2, 3, _lineNb)) {
                return -1;
            }

            if (_split.length == 6) {
                if (!checkParameter("EVENT", _split, 3, 1, _lineNb)) {
                    return -1;
                }
            }

            if (!checkParameter("EVENT", _split, 3 + dec, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("EVENT", _split, 4 + dec, 0, _lineNb)) {
                return -1;
            }

            if (tmlm.getEventByName(id) != null) {
                error = "Duplicate definition of event " + id;
                addError(0, _lineNb, 0, error);
                return -1;
            }

            blocking = _split[2].toUpperCase().compareTo("NIB") == 0;

            if (_split[2].toUpperCase().compareTo("INF") == 0) {
                tmp = -1;
            } else {
                try {
                    tmp = Integer.decode(_split[3]).intValue();
                } catch (Exception e) {
                    error = "Unvalid parameter #3: should be a numeric value";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            evt = new TMLEvent(id, null, tmp, blocking);
            evt.addParam(params);

            t1 = tmlm.getTMLTaskByName(_split[3 + dec]);
            if (t1 == null) {
                t1 = new TMLTask(_split[3 + dec], null, null);
                //TraceManager.addDev("New task:" + _split[3+dec]);
                tmlm.addTask(t1);
            }
            t2 = tmlm.getTMLTaskByName(_split[4 + dec]);
            if (t2 == null) {
                t2 = new TMLTask(_split[4 + dec], null, null);
                //TraceManager.addDev("New task:" + _split[4+dec]);
                tmlm.addTask(t2);
            }
            evt.setTasks(t1, t2);
            tmlm.addEvent(evt);


        } // EVENT

        // LOSSYEVENT
        if (isInstruction("LOSSYEVENT", _split[0])) {
            if (!inDec) {
                error = "A lossyevent may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!((_split.length > 3) && (_split.length < 5))) {
                error = "A lossyevent must be declared with exactly 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }


            if (!checkParameter("LOSSYEVENT", _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYEVENT", _split, 2, 1, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYEVENT", _split, 3, 9, _lineNb)) {
                return -1;
            }


            evt = tmlm.getEventByName(_split[1]);
            if (evt == null) {
                error = "lossyevent not previsouly declared as a regular event " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            try {
                tmp0 = Integer.decode(_split[2]).intValue();
            } catch (Exception e) {
                tmp0 = 5;
            }
            try {
                tmp1 = Integer.decode(_split[3]).intValue();
            } catch (Exception e) {
                tmp1 = -1;
            }

            evt.setLossy(true, tmp0, tmp1);
        } // LOSSYEVENT

        // REQUEST
        if ((isInstruction("REQUEST", _split[0])) && (inDec)) {
            if (!inDec) {
                error = "A request may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length < 4) {
                error = "A request must be declared with at least 4 paremeters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }


            id = getEvtId(_split[1]);
            params = getParams(_split[1]);

            //TraceManager.addDev("Evt id=" + id +  "params=" + params);

            if (!checkParameter("REQUEST", _split, 1, 4, _lineNb)) {
                return -1;
            }

            for (i = 2; i < _split.length; i++) {
                if (!checkParameter("REQUEST", _split, i, 0, _lineNb)) {
                    return -1;
                }
            }

            if (tmlm.getRequestByName(id) != null) {
                error = "Duplicate definition of request " + id;
                addError(0, _lineNb, 0, error);
                return -1;
            }

            request = new TMLRequest(id, null);
            request.addParam(params);

            for (i = 2; i < _split.length; i++) {
                t1 = tmlm.getTMLTaskByName(_split[i]);
                if (t1 == null) {
                    t1 = new TMLTask(_split[i], null, null);
                    //TraceManager.addDev("New task:" + _split[i]);
                    tmlm.addTask(t1);
                }
                if ((i + 1) == _split.length) {
                    request.setDestinationTask(t1);
                    t1.setRequested(true);
                    t1.setRequest(request);
                } else {
                    request.addOriginTask(t1);
                }
            }

            tmlm.addRequest(request);
        } // REQUEST

        // LOSSYREQUEST
        if (isInstruction("LOSSYREQUEST", _split[0])) {
            if (!inDec) {
                error = "A lossyrequest may not be declared in a non-declaration part of a TML specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!((_split.length > 3) && (_split.length < 5))) {
                error = "A lossyrequest must be declared with exactly 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }


            if (!checkParameter("LOSSYREQUEST", _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYREQUEST", _split, 2, 1, _lineNb)) {
                return -1;
            }

            if (!checkParameter("LOSSYREQUEST", _split, 3, 9, _lineNb)) {
                return -1;
            }


            request = tmlm.getRequestByName(_split[1]);
            if (request == null) {
                error = "lossyrequest not previsouly declared as a regular event " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            try {
                tmp0 = Integer.decode(_split[2]).intValue();
            } catch (Exception e) {
                tmp0 = 5;
            }
            try {
                tmp1 = Integer.decode(_split[3]).intValue();
            } catch (Exception e) {
                tmp1 = -1;
            }

            request.setLossy(true, tmp0, tmp1);
        } // LOSSYREQUEST

        // TASK
        if ((isInstruction("TASK", _split[0]))) {

            //TraceManager.addDev("In task");
            if (inTask) {
                error = "A task may not be declared in the body of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = true;
            //     inTaskBehavior = false;

            if (_split.length != 2) {
                error = "A task must be declared with exactly 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("TASK", _split, 1, 0, _lineNb)) {
                return -1;
            }

            //TraceManager.addDev("In task: 12");
            task = tmlm.getTMLTaskByName(_split[1]);
            if ((task != null) && (task.getActivityDiagram() != null)) {
                if (task.getActivityDiagram().getFirst() != null) {
                    error = "Duplicate definition for task " + (_split[1]);
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }
            //TraceManager.addDev("In task: 13");
            if (task == null) {
                task = new TMLTask(_split[1], null, null);
                tmlm.addTask(task);
                //TraceManager.addDev("New task:" + _split[1]);
            }

            TMLStartState start = new TMLStartState("start", null);
            task.getActivityDiagram().setFirst(start);
            tmlae = start;


        } // TASK


        // TASKOP
        if ((isInstruction("TASKOP", _split[0]))) {
            //An operation is really specified?

            if (!inTask) {
                error = "A task operation cannot be declared outside the body of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!inTaskDec) {
                error = "A task operation cannot be declared outside the declarative part of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = true;

            String tmpOp = "";

            for (int j = 1; j < _split.length; j++) {
                tmpOp += _split[j] +  " ";

            }
            task.addOperation(tmpOp);
        }

        // DAEMON
        if ((isInstruction("DAEMON", _split[0]))) {

            if (!inTask) {
                error = "A daemon task cannot be declared outside the body of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!inTaskDec) {
                error = "A daemon operation cannot be declared outside the declarative part of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = true;

            task.setDaemon(true);
        }

        // PERIODIC
        if ((isInstruction("PERIODIC", _split[0]))) {
           // periodic task?

            if (!inTask) {
                error = "A periodic task cannot be declared outside the body of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!inTaskDec) {
                error = "A periodic operation cannot be declared outside the declarative part of another task";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length < 3) {
                error = "A periodic task must be declared with a value and a unit";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            String periodValue = _split[1];
            String unit = _split[2];

            if (!periodValue.matches("-?\\d+")) {
                error = "A periodic task must be declared with a numerical period value (and not " + periodValue + ")";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!unit.matches("(?i)ns|us|ms|s")) {
                error = "A periodic task must be declared with a unit equal to ns or us or ms or s";
                addError(0, _lineNb, 0, error);
                return -1;
            }


            inDec = false;
            inTask = true;
            inTaskDec = true;

            task.setPeriodic(true, periodValue, unit);
        }


        // ENDTASK
        if ((isInstruction("ENDTASK", _split[0]))) {
            if (!inTask) {
                error = "A endtask may not be used outside the body of a task";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            inDec = true;
            inTask = false;
            inTaskDec = false;
            // inTaskBehavior = false;

            stop = new TMLStopState("stop", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);

            task = null;
        } // ENDTASK


        // Attribute declaration
        if ((isInstruction("INT", _split[0])) || (isInstruction("NAT", _split[0])) || (isInstruction("BOOL", _split[0]))) {
            if (!inTaskDec) {
                error = "An attribute declaration must be done in a task right after its declaration, and before its behavior";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            String inst = _split[0].toUpperCase();

            if (!((_split.length == 2) || (_split.length == 4))) {
                error = "An attribute declaration must be done with either 1 or 3 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter(inst, _split, 1, 0, _lineNb)) {
                return -1;
            }

            if (_split.length > 2) {
                if (!checkParameter(inst, _split, 2, 5, _lineNb)) {
                    return -1;
                }
                if (!checkParameter(inst, _split, 3, 6, _lineNb)) {
                    return -1;
                }
            }

            //TraceManager.addDev("Adding attribute " + _split[0] + " " + _split[1]);

            TMLAttribute ta = new TMLAttribute(_split[1], new TMLType(TMLType.getType(_split[0])));
            if (_split.length > 2) {
                ta.initialValue = _split[3];
            } else {
                ta.initialValue = ta.getDefaultInitialValue();
            }
            task.addAttribute(ta);
        } // Attribute declaration

        // RANDOM
        if ((isInstruction("RANDOM", _split[0]))) {

            if (!inTask) {
                error = "A RANDOM operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //    inTaskBehavior = true;

            if (_split.length != 5) {
                error = "A RANDOM operation must be declared with exactly 4 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("RANDOM", _split, 1, 1, _lineNb)) {
                return -1;
            }

            if (!checkParameter("RANDOM", _split, 2, 0, _lineNb)) {
                return -1;
            }

            random = new TMLRandom("random", null);
            try {
                random.setFunctionId(Integer.decode(_split[1]).intValue());
            } catch (Exception e) {
            }

            random.setVariable(_split[2]);
            random.setMinValue(_split[3]);
            random.setMaxValue(_split[4]);

            TraceManager.addDev("RANDOM min=" + random.getMinValue() + " max=" + random.getMaxValue());

            task.getActivityDiagram().addElement(random);
            tmlae.addNext(random);
            tmlae = random;

        } // RANDOM

        // READ
        if ((isInstruction("READ", _split[0]))) {

            if (!inTask) {
                error = "A READ operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            if (_split.length != 3 && _split.length != 4) {
                error = "A READ operation must be declared with exactly 3 or 4 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("READ", _split, 1, 0, _lineNb)) {
                return -1;
            }

            /*if (!checkParameter("READ", _split, 2, 7, _lineNb)) {
              return -1;
              }*/

            ch = tmlm.getChannelByName(_split[1]);
            if (ch == null) {
                error = "Undeclared channel: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }
            if (!(ch.hasDestinationTask(task))) {
                error = "READ operations must be done only in destination task(s). Should be in task(s): " + ch.getNameOfDestinationTasks();
                addError(0, _lineNb, 0, error);
                return -1;
            }


            TMLReadChannel tmlrch = new TMLReadChannel(_split[1], null);
            tmlrch.addChannel(ch);
            tmlrch.setNbOfSamples(_split[2]);
            task.getActivityDiagram().addElement(tmlrch);
            tmlae.addNext(tmlrch);

            if (_split.length == 4) {
                if (securityPatternMap.containsKey(_split[3])) {
                    tmlrch.securityPattern = securityPatternMap.get(_split[3]);
                }
            }

            tmlae = tmlrch;

        } // READ

        // WRITE
        if ((isInstruction("WRITE", _split[0]))) {

            if (!inTask) {
                error = "A WRITE operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;


            if (_split.length > 5 || _split.length < 2) {
                error = "A WRITE operation must be declared with at most 4 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!checkParameter("WRITE", _split, 1, 0, _lineNb)) {
                return -1;
            }

            //TraceManager.addDev("Handling write channel 1");
            TMLWriteChannel tmlwch = new TMLWriteChannel(_split[1], null);
            if (_split.length > 3) {
                if (securityPatternMap.containsKey(_split[_split.length - 1])) {
                    tmlwch.securityPattern = securityPatternMap.get(_split[_split.length - 1]);
                }
                for (int k = 0; k < _split.length - 3; k++) {
                    //TraceManager.addDev("Handling write channel 1.1");
                    ch = tmlm.getChannelByName(_split[1 + k]);
                    if (ch == null) {
                        error = "Undeclared channel: " + _split[1 + k];
                        addError(0, _lineNb, 0, error);
                        return -1;
                    }
                    //TraceManager.addDev("Handling write channel 1.2 for task: " + task.getName());
                    if (!(ch.hasOriginTask(task))) {
                        error = "WRITE operations must be done only in origin task(s). Should be in task(s): " + ch.getNameOfOriginTasks();
                        addError(0, _lineNb, 0, error);
                        return -1;
                    }
                    //TraceManager.addDev("Handling write channel 1.3");

                    tmlwch.addChannel(ch);
                }
            } else {

                for (int k = 0; k < _split.length - 2; k++) {
                    //TraceManager.addDev("Handling write channel 1.1");
                    ch = tmlm.getChannelByName(_split[1 + k]);
                    if (ch == null) {
                        error = "Undeclared channel: " + _split[1 + k];
                        addError(0, _lineNb, 0, error);
                        return -1;
                    }
                    //TraceManager.addDev("Handling write channel 1.2 for task: " + task.getName());
                    if (!(ch.hasOriginTask(task))) {
                        error = "WRITE operations must be done only in origin task(s). Should be in task(s): " + ch.getNameOfOriginTasks();
                        addError(0, _lineNb, 0, error);
                        return -1;
                    }
                    //TraceManager.addDev("Handling write channel 1.3");

                    tmlwch.addChannel(ch);
                }
            }
            //TraceManager.addDev("Handling write channel 2");
            tmlwch.setNbOfSamples(_split[2]);
            task.getActivityDiagram().addElement(tmlwch);
            tmlae.addNext(tmlwch);


            tmlae = tmlwch;

        } // WRITE

        // NOTIFY
        if ((isInstruction("NOTIFY", _split[0]))) {

            if (!inTask) {
                error = "A NOTIFY operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            // inTaskBehavior = true;

            if (_split.length < 2) {
                error = "A NOTIFY operation must be declared with at least 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            evt = tmlm.getEventByName(_split[1]);
            if (evt == null) {
                error = "Unknown event: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            dec = evt.getNbOfParams();

            if (_split.length != 2 + dec) {
                error = "A NOTIFY operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLSendEvent sevt = new TMLSendEvent(evt.getName(), null);
            sevt.setEvent(evt);
            for (i = 2; i < 2 + dec; i++) {
                sevt.addParam(_split[i]);
            }

            task.getActivityDiagram().addElement(sevt);
            tmlae.addNext(sevt);
            tmlae = sevt;
        } // NOTIFY

        // WAIT
        if ((isInstruction("WAIT", _split[0]))) {

            if (!inTask) {
                error = "A WAIT operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            if (_split.length < 2) {
                error = "A WAIT operation must be declared with at least 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            evt = tmlm.getEventByName(_split[1]);
            if (evt == null) {
                error = "Unknown event: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            dec = evt.getNbOfParams();

            if (_split.length != 2 + dec) {
                error = "A WAIT operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            // Each param must be a declared attribute of the right type
            for (i = 2; i < 2 + dec; i++) {
                attribute = task.getAttributeByName(_split[i]);
                if (attribute == null) {
                    error = "Attribute: " + _split[i] + " is undeclared";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
                if (attribute.type.getType() != evt.getType(i - 2).getType()) {
                    error = "Attribute: " + _split[i] + " is not of the right type";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }


            TMLWaitEvent wevt = new TMLWaitEvent(evt.getName(), null);
            wevt.setEvent(evt);
            for (i = 2; i < 2 + dec; i++) {
                wevt.addParam(_split[i]);
            }

            task.getActivityDiagram().addElement(wevt);
            tmlae.addNext(wevt);
            tmlae = wevt;
        } // WAIT

        // NOTIFIED
        if ((isInstruction("NOTIFIED", _split[0]))) {

            if (!inTask) {
                error = "A NOTIFIED operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //  inTaskBehavior = true;

            if (_split.length != 3) {
                error = "A NOTIFIED operation must be declared with exactly 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            evt = tmlm.getEventByName(_split[1]);
            if (evt == null) {
                error = "Unknown event: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            attribute = task.getAttributeByName(_split[2]);
            if (attribute == null) {
                error = "Attribute: " + _split[2] + " is undeclared";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            if (attribute.type.getType() != TMLType.NATURAL) {
                error = "Attribute: " + _split[2] + " should be of natural type";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLNotifiedEvent nevt = new TMLNotifiedEvent(evt.getName(), null);
            nevt.setEvent(evt);
            nevt.setVariable(_split[2]);

            task.getActivityDiagram().addElement(nevt);
            tmlae.addNext(nevt);
            tmlae = nevt;
        } // NOTIFIED

        // Send REQUEST a.k.a. NOTIFYREQUEST
        if ((isInstruction("NOTIFYREQUEST", _split[0])) && (inTask)) {

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            if (_split.length < 2) {
                error = "A REQUEST operation must be declared with at least 1 parameter (request name), and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            request = tmlm.getRequestByName(_split[1]);
            if (request == null) {
                error = "Unknown request: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            dec = request.getNbOfParams();

            if (_split.length != 2 + dec) {
                error = "A REQUEST operation on request " + request.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLSendRequest sreq = new TMLSendRequest(request.getName(), null);
            sreq.setRequest(request);
            for (i = 2; i < 2 + dec; i++) {
                sreq.addParam(_split[i]);
            }

            task.getActivityDiagram().addElement(sreq);
            tmlae.addNext(sreq);
            tmlae = sreq;
        } // Send REQUEST

        // FOR
        if ((isInstruction("FOR", _split[0])) && (inTask)) {
            //TraceManager.addDev("FOR encountered");
            if (_split.length < 2) {
                error = "FOR operation: missing parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            // Extract the three elements of FOR
            String forp = _split[1];
            //   String forps[];
            tmp0 = forp.indexOf('(');
            tmp1 = forp.lastIndexOf(')');
            if ((tmp0 == -1) || (tmp1 == -1)) {
                error = "FOR operation: badly formed parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            forp = forp.substring(tmp0 + 1, tmp1);
            int first = forp.indexOf(";");
            String init = "";
            if (first > -1) {
                init = forp.substring(0, first);
            } else {
                error = "FOR operation: badly formed parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            String condition = "";
            String increment = "";
            forp = forp.substring(first + 1, forp.length()).trim();

            int second = forp.indexOf(";");
            if (second == -1) {
                error = "FOR operation: badly formed parameters ";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            condition = forp.substring(0, second);
            increment = forp.substring(second + 1, forp.length());


            // All is ok: constructing the FOR
            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.FOR;
            parses.add(0, parseElt);
            TMLForLoop loop = new TMLForLoop("loop", null);
            loop.setInit(init);
            loop.setCondition(condition);
            loop.setIncrement(increment);
            task.getActivityDiagram().addElement(loop);
            parseElt.tmlae = loop;
            tmlae.addNext(loop);
            tmlae = loop;

        } // FOR

        // ENDFOR
        if (isInstruction("ENDFOR", _split[0])) {
            if (!inTask) {
                error = "ENDFOR: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //      inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDFOR: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.FOR) {
                error = "ENDFOR: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            // All is ok: constructing the FOR
            parses.remove(0);
            stop = new TMLStopState("stop loop", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);
            tmlae = parseElt.tmlae;
        } // ENDFOR

        // SELECTEVT
        if ((isInstruction("SELECTEVT", _split[0]))) {
            if (!inTask) {
                error = "SELECTEVT: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length > 1) {
                error = "A SELECTEVT cannot have any parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.SELECTEVT;
            parses.add(0, parseElt);
            TMLSequence seq = new TMLSequence("sequence", null);
            parseElt.top = seq;
            tmlae.addNext(seq);
            TMLSelectEvt sel = new TMLSelectEvt("select evt", null);
            parseElt.tmlae = sel;
            seq.addNext(sel);
            task.getActivityDiagram().addElement(seq);
            task.getActivityDiagram().addElement(sel);
            tmlae = sel;
        } // SELECTEVT

        // ENDSELECTEVT
        if ((isInstruction("ENDSELECTEVT", _split[0]))) {
            if (!inTask) {
                error = "ENDSELECTEVT: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //    inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDSELECTEVT: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.SELECTEVT) {
                error = "ENDSELECTEVT: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            parses.remove(0);
            tmlae = parseElt.top;
        } // ENDSELECTEVT

        // CASE
        if ((isInstruction("CASE", _split[0]))) {
            if (!inTask) {
                error = "CASE: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (parses.size() == 0) {
                error = "CASE: corresponding SELECTEVT not found";
                addError(0, _lineNb, 0, error);
                return -1;
            } else {
                parseElt = parses.get(0);
                if (parseElt.type != TMLParserSaveElt.SELECTEVT) {
                    error = "CASE: corresponding SELECTEVT not found";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            if (_split.length < 2) {
                error = "A CASE must have at least two parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            evt = tmlm.getEventByName(_split[1]);
            if (evt == null) {
                error = "Unknown event: " + _split[1];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            dec = evt.getNbOfParams();

            if (_split.length != 2 + dec) {
                error = "A CASE operation on evt " + evt.getName() + " must be declared with exactly " + (1 + dec) + " parameters and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLWaitEvent wevt = new TMLWaitEvent(evt.getName(), null);
            wevt.setEvent(evt);
            for (i = 2; i < 2 + dec; i++) {
                wevt.addParam(_split[i]);
            }

            task.getActivityDiagram().addElement(wevt);
            tmlae.addNext(wevt);

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.CASE;
            parseElt.tmlae = wevt;
            parseElt.top = tmlae;
            parses.add(0, parseElt);

            tmlae = wevt;
        } // CASE


        // ENDCASE
        if ((isInstruction("ENDCASE", _split[0]))) {
            if (!inTask) {
                error = "ENDCASE: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //        inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDCASE: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.CASE) {
                error = "ENDCASE: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parses.remove(0);
            stop = new TMLStopState("stop case", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);
            tmlae = parseElt.top;
        } // ENDCASE

        // RANDOMSEQ
        if ((isInstruction("RANDOMSEQ", _split[0]))) {
            if (!inTask) {
                error = "RANDOMSEQ: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length > 1) {
                error = "A RANDOMSEQ cannot have any parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //    inTaskBehavior = true;

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.RANDOMSEQ;
            parses.add(0, parseElt);
            TMLSequence seq = new TMLSequence("sequence", null);
            parseElt.top = seq;
            tmlae.addNext(seq);
            TMLRandomSequence rseq = new TMLRandomSequence("random sequence", null);
            parseElt.tmlae = rseq;
            seq.addNext(rseq);
            task.getActivityDiagram().addElement(seq);
            task.getActivityDiagram().addElement(rseq);
            tmlae = rseq;
        } // RANDOMSEQ

        // ENDRANDOMSEQ
        if ((isInstruction("ENDRANDOMSEQ", _split[0]))) {
            if (!inTask) {
                error = "ENDRANDOMSEQ: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDRANDOMSEQ: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.RANDOMSEQ) {
                error = "ENDRANDOMSEQ: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            parses.remove(0);
            tmlae = parseElt.top;
        } // ENDRANDOMSEQ


        // SEQ
        if ((isInstruction("SEQ", _split[0]))) {
            if (!inTask) {
                error = "SEQ: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (parses.size() == 0) {
                error = "SEQ: corresponding RANDOMSEQ not found";
                addError(0, _lineNb, 0, error);
                return -1;
            } else {
                parseElt = parses.get(0);
                if (parseElt.type != TMLParserSaveElt.RANDOMSEQ) {
                    error = "SEQ: corresponding RANDOMSEQ not found";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //   inTaskBehavior = true;

            if (_split.length > 1) {
                error = "A SEQ has no parameter";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!(parseElt.tmlae instanceof TMLRandomSequence)) {
                error = "Malformed specification: unexpected SEQ";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLRandomSequence rseq = (TMLRandomSequence) parseElt.tmlae;
            TMLSequence seq = new TMLSequence("sequence", null);
            rseq.addNext(seq);

            task.getActivityDiagram().addElement(seq);

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.SEQ;
            parseElt.tmlae = seq;
            parseElt.top = rseq;
            parses.add(0, parseElt);

            tmlae = seq;
        } // SEQ

        // ENDSEQ
        if ((isInstruction("ENDSEQ", _split[0]))) {
            if (!inTask) {
                error = "ENDSEQ: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //  inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDSEQ: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.SEQ) {
                error = "ENDSEQ: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parses.remove(0);
            stop = new TMLStopState("stop case", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);
            tmlae = parseElt.top;
        } // ENDSEQ

        // RAND
        if ((isInstruction("RAND", _split[0]))) {
            if (!inTask) {
                error = "RAND: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length > 1) {
                error = "A RAND cannot have any parameters";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.RAND;
            parses.add(0, parseElt);
            TMLSequence seq = new TMLSequence("sequence", null);
            parseElt.top = seq;
            tmlae.addNext(seq);
            TMLChoice choice = new TMLChoice("choice evt", null);
            parseElt.tmlae = choice;
            seq.addNext(choice);
            task.getActivityDiagram().addElement(seq);
            task.getActivityDiagram().addElement(choice);
            tmlae = choice;
        } // RAND

        // ENDRAND
        if ((isInstruction("ENDRAND", _split[0]))) {
            if (!inTask) {
                error = "ENDRAND: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDRAND: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.RAND) {
                error = "ENDRAND: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            parses.remove(0);
            tmlae = parseElt.top;
        } // ENDRAND

        // CASERAND
        if ((isInstruction("CASERAND", _split[0]))) {
            if (!inTask) {
                error = "CASERAND: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (parses.size() == 0) {
                error = "CASERAND: corresponding RAND not found";
                addError(0, _lineNb, 0, error);
                return -1;
            } else {
                parseElt = parses.get(0);
                if (parseElt.type != TMLParserSaveElt.RAND) {
                    error = "CASERAND: corresponding RAND not found";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //       inTaskBehavior = true;

            if (_split.length != 2) {
                error = "A CASERAND should have one parameter";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (!(parseElt.tmlae instanceof TMLChoice)) {
                error = "Malformed specification";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            TMLChoice choice = (TMLChoice) parseElt.tmlae;
            TMLSequence seq = new TMLSequence("sequence", null);
            choice.addGuard("[" + _split[1] + "%]");
            choice.addNext(seq);

            task.getActivityDiagram().addElement(seq);

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.CASERAND;
            parseElt.tmlae = seq;
            parseElt.top = choice;
            parses.add(0, parseElt);

            tmlae = seq;

        } // CASERAND


        // ENDCASERAND
        if ((isInstruction("ENDCASERAND", _split[0]))) {
            if (!inTask) {
                error = "ENDCASERAND: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDCASERAND: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.CASERAND) {
                error = "ENDCASERAND: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parses.remove(0);
            stop = new TMLStopState("stop case", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);
            tmlae = parseElt.top;
        } // ENDCASERAND

        // IF
        if ((isInstruction("IF", _split[0]))) {
            if (!inTask) {
                error = "IF: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length != 2) {
                error = "IF should be followed by one condition";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            String cond = _split[1].trim();
            tmp0 = cond.indexOf('(');
            tmp1 = cond.lastIndexOf(')');
            if ((tmp0 == -1) || (tmp1 == -1)) {
                error = "IF operation: badly formed condition";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            cond = cond.substring(tmp0 + 1, tmp1);

            parseElt = new TMLParserSaveElt();
            parseElt.type = TMLParserSaveElt.IF;
            parses.add(0, parseElt);
            TMLSequence seq = new TMLSequence("sequence", null);
            parseElt.top = seq;
            tmlae.addNext(seq);
            TMLChoice choice = new TMLChoice("if", null);
            parseElt.tmlae = choice;
            seq.addNext(choice);
            task.getActivityDiagram().addElement(seq);
            task.getActivityDiagram().addElement(choice);

            seq = new TMLSequence("sequence", null);
            task.getActivityDiagram().addElement(seq);
            choice.addNext(seq);
            choice.addGuard("[" + cond + "]");

            tmlae = seq;
        } // IF

        // ORIF
        if ((isInstruction("ORIF", _split[0]))) {
            if (!inTask) {
                error = "ORIF: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length != 2) {
                error = "ORIF should be followed by one condition";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;


            String cond = _split[1].trim();
            //TraceManager.addDev("cond1=" + cond);
            tmp0 = cond.indexOf('(');
            tmp1 = cond.lastIndexOf(')');
            if ((tmp0 == -1) || (tmp1 == -1)) {
                error = "ORIF operation: badly formed condition";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            cond = cond.substring(tmp0 + 1, tmp1);
            //TraceManager.addDev("cond2=" + cond);

            if (parses.size() == 0) {
                error = "ORIF: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.IF) {
                error = "ORIF: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (parseElt.nbElse > 0) {
                error = "ORIF: should not followed a else instruction";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            stop = new TMLStopState("stop", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);

            TMLSequence seq = new TMLSequence("sequence", null);
            TMLChoice choice = (TMLChoice) parseElt.tmlae;
            task.getActivityDiagram().addElement(seq);

            choice.addNext(seq);
            choice.addGuard("[" + cond + "]");

            tmlae = seq;
        } // ORIF

        // ELSE
        if ((isInstruction("ELSE", _split[0]))) {
            if (!inTask) {
                error = "ELSE: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length != 1) {
                error = "ELSE should have no parameter";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            if (parses.size() == 0) {
                error = "ELSE: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.IF) {
                error = "ELSE: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            stop = new TMLStopState("stop", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);

            parseElt.nbElse++;

            TMLSequence seq = new TMLSequence("sequence", null);
            TMLChoice choice = (TMLChoice) parseElt.tmlae;
            task.getActivityDiagram().addElement(seq);

            choice.addNext(seq);
            choice.addGuard("[else]");

            tmlae = seq;
        } // ELSE

        // ENDIF
        if ((isInstruction("ENDIF", _split[0]))) {
            if (!inTask) {
                error = "ENDIF: must be used in a Task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //    inTaskBehavior = true;

            // Extract the first element of the stack
            if (parses.size() == 0) {
                error = "ENDIF: badly placed instruction.";
                addError(0, _lineNb, 0, error);
                return -1;
            }
            parseElt = parses.get(0);
            if (parseElt.type != TMLParserSaveElt.IF) {
                error = "ENDIF: badly placed instruction. Was expecting: " + parseElt.getExpectedInstruction();
                addError(0, _lineNb, 0, error);
                return -1;
            }

            stop = new TMLStopState("stop", null);
            task.getActivityDiagram().addElement(stop);
            tmlae.addNext(stop);

            parses.remove(0);
            tmlae = parseElt.top;
        } // ENDIF

        // EXECI
        if ((isInstruction("EXECI", _split[0]))) {

            if (!inTask) {
                error = "An EXECI operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            if ((_split.length < 2) || (_split.length > 4)) {
                error = "An EXECI operation must be declared with 1 or 2 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length == 2) {
                TMLExecI execi = new TMLExecI("execi", null);
                execi.setAction(_split[1]);
                tmlae.addNext(execi);
                task.getActivityDiagram().addElement(execi);
                tmlae = execi;
            } else {
                TMLExecIInterval execi = new TMLExecIInterval("execi", null);
                execi.setMinDelay(_split[1]);
                execi.setMaxDelay(_split[2]);
                tmlae.addNext(execi);
                task.getActivityDiagram().addElement(execi);
                tmlae = execi;
            }
        } // EXECI

        // EXECC
        if ((isInstruction("EXECC", _split[0]))) {

            if (!inTask) {
                error = "An EXECC operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;
            if (_split.length > 4) {
                if (securityPatternMap.containsKey(_split[2])) {
                    //Security operation
                    TMLExecC execc = new TMLExecC("execc", null);
                    execc.setAction(_split[1]);
                    execc.securityPattern = securityPatternMap.get(_split[2]);
                    tmlae.addNext(execc);
                    task.getActivityDiagram().addElement(execc);
                    tmlae = execc;
                }
            } else {
                if ((_split.length < 2) || (_split.length > 4)) {
                    error = "An EXECC operation must be declared with 1, 2 parameters, and not " + (_split.length - 1);
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
                if (_split.length == 2) {
                    TMLExecC execc = new TMLExecC("execc", null);
                    execc.setAction(_split[1]);
                    tmlae.addNext(execc);
                    task.getActivityDiagram().addElement(execc);
                    tmlae = execc;
                } else {
                    TMLExecCInterval execci = new TMLExecCInterval("execci", null);
                    execci.setMinDelay(_split[1]);
                    execci.setMaxDelay(_split[2]);
                    tmlae.addNext(execci);
                    task.getActivityDiagram().addElement(execci);
                    tmlae = execci;
                }
            }
        } // EXECC

        // DELAY
        if ((isInstruction("DELAY", _split[0]))) {

            if (!inTask) {
                error = "A DELAY operation may only be performed in a task body";
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //       inTaskBehavior = true;

            if ((_split.length < 3) || (_split.length > 6)) {
                error = "A DELAY operation must be declared with 2, 3, 4 or 5 parameters, and not " + (_split.length - 1);
                addError(0, _lineNb, 0, error);
                return -1;
            }

            if (_split.length == 3 || _split.length == 5) {
                if (!checkParameter("DELAY", _split, 2, 0, _lineNb)) {
                    error = "A DELAY operation must be specified with a valid time unit (ns, us, ms, s))";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            if (_split.length == 4 || _split.length == 6) {
                if (!checkParameter("DELAY", _split, 3, 0, _lineNb)) {
                    error = "A DELAY operation must be specified with a valid time unit (ns, us, ms, s))";
                    addError(0, _lineNb, 0, error);
                    return -1;
                }
            }

            TMLDelay delay = new TMLDelay("delay", null);
            delay.setMinDelay(_split[1]);
            if (_split.length == 3) {
                delay.setMaxDelay(_split[1]);
                delay.setUnit(_split[2]); // DELAY min unit - this is for old format
            } else if (_split.length == 4) {
                delay.setMaxDelay(_split[2]);
                delay.setUnit(_split[3]); // DELAY min max unit - this is for old format
            } else if (_split.length == 5) {
                delay.setMaxDelay(_split[1]);
                delay.setUnit(_split[2]);
                delay.setActiveDelay(Boolean.valueOf(_split[4])); // DELAY min unit isActivedelay boolean
            } else {
                delay.setMaxDelay(_split[2]);
                delay.setUnit(_split[3]);
                delay.setActiveDelay(Boolean.valueOf(_split[5])); // DELAY min  max unit isActivedelay boolean
            }


            tmlae.addNext(delay);
            task.getActivityDiagram().addElement(delay);
            tmlae = delay;

        } // EXECC

        // Other command
        if ((_split[0].length() > 0) && (!(isInstruction(_split[0])))) {
            //TraceManager.addDev("Not an instruction:" + _split[0]);
            if (!inTask) {
                error = "Syntax error in TML modeling: unrecognized instruction:" + _split[0];
                addError(0, _lineNb, 0, error);
                return -1;
            }

            inDec = false;
            inTask = true;
            inTaskDec = false;
            //     inTaskBehavior = true;

            TMLActionState action = new TMLActionState(_split[0], null);
            action.setAction(_line);
            tmlae.addNext(action);
            task.getActivityDiagram().addElement(action);
            tmlae = action;

        } // Other command

        return 0;
    }

    // Type 0: id
    // Type 1: numeral
    // Type 2: channel type
    // Type 3: event type
    // Type 4: event name
    // Type 5: '='
    // Type 6: attribute value
    // Type 7: id or numeral
    // Type 8:unit

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
                    if (!isIncluded(_split[_parameter], channeltypes)) {
                        err = true;
                    }
                    break;
                case 3:
                    if (!isIncluded(_split[_parameter], eventtypes)) {
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
                    if (!isAValidUnit(_split[_parameter])) {
                        err = true;
                    }
                    break;
                case 9:
                    if (!isANegativeOrPositiveNumeral(_split[_parameter])) {
                        err = true;
                    }
                    break;
                case 10:
                    if (!(_split[_parameter].compareTo("OUT") == 0)) {
                        err = true;
                    }
                    break;
            }
        } else {
            err = true;
        }
        if (err) {
            error = "Unvalid parameter #" + _parameter + "-> $" + _split[_parameter] + "$ <- in " + _inst + " instruction";
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

    public static boolean isAValidId(String _id) {
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

    public boolean isANegativeOrPositiveNumeral(String _num) {
        if (_num.startsWith("-")) {
            return isANumeral(_num.substring(1, _num.length()));
        }
        return isANumeral(_num);
    }

    public boolean isAValidUnit(String s) {
        if (s.compareTo("ns") == 0) {
            return true;
        } else if (s.compareTo("us") == 0) {
            return true;
        } else if (s.compareTo("ms") == 0) {
            return true;
        } else if (s.compareTo("s") == 0) {
            return true;
        }

        return false;
    }

    public static boolean checkKeywords(String _id) {
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
            if (id.compareTo(_list[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    public String removeUndesiredWhiteSpaces(String _input, int _lineNb) {
        String error, tmp;
        int index0, index1;//, index2;

        if (_input.startsWith("EVENT ")) {
            index0 = _input.indexOf('(');
            index1 = _input.indexOf(')');
            if ((index0 == -1) || (index1 == -1)) {
                error = "Syntax Error: should be of the form EVENT evtname(<list of max three types>) + other parameters";
                addError(0, _lineNb, 0, error);
                return null;
            }
            return Conversion.replaceBetweenIndex(_input, index0, index1, " ", "");
        }

        if (_input.startsWith("REQUEST ") && (inDec)) {
            index0 = _input.indexOf('(');
            index1 = _input.indexOf(')');
            if ((index0 == -1) || (index1 == -1)) {
                error = "Syntax Error: should be of the form REQUEST requestname(<list of max three types>) + other parameters";
                addError(0, _lineNb, 0, error);
                return null;
            }
            return Conversion.replaceBetweenIndex(_input, index0, index1, " ", "");
        }

        if (_input.startsWith("FOR(")) {
            _input = "FOR (" + _input.substring(4, _input.length());
        }

        if (_input.startsWith("FOR (")) {
            tmp = _input.substring(5, _input.length());
            tmp = Conversion.replaceAllString(tmp, " ", "");
            return "FOR (" + tmp;
        }

        if (_input.startsWith("IF(")) {
            _input = "IF (" + _input.substring(3, _input.length());
        }

        if (_input.startsWith("IF (")) {
            tmp = _input.substring(4, _input.length());
            tmp = Conversion.replaceAllString(tmp, " ", "");
            return "IF (" + tmp;
        }

        if (_input.startsWith("ORIF(")) {
            _input = "ORIF (" + _input.substring(5, _input.length());
        }

        if (_input.startsWith("ORIF (")) {
            tmp = _input.substring(6, _input.length());
            tmp = Conversion.replaceAllString(tmp, " ", "");
            return "ORIF (" + tmp;
        }

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

    private static String prepareString(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("\\s", "");
    }

    public static String modifyString(String s) {
        return prepareString(s);
    }
}

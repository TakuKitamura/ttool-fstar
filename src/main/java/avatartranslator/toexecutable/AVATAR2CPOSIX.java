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

package avatartranslator.toexecutable;

import java.io.File;
import java.util.List;
import java.util.Vector;

import avatartranslator.*;
import common.SpecConfigTTool;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.Plugin;
import myutil.TraceManager;

import java.io.FileOutputStream;
import ui.GTURTLEModeling;
import ui.MainGUI;
import ui.MainGUI.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.*;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Class AVATAR2CPOSIX Creation: 29/03/2011
 *
 * @author Ludovic APVRILLE
 * @version 1.3 15/01/2021
 */
public class AVATAR2CPOSIX {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;

    private final static String UNUSED_ATTR = "__attribute__((unused))";
    private final static String GENERATED_PATH = "generated_src" + File.separator;
    // private final static String UNKNOWN = "UNKNOWN";
    private final static String CR = "\n";

    private AvatarSpecification avspec;

    private Vector warnings;

    private MainFile mainFile;
    private Vector<TaskFile> taskFiles;
    private String makefile_src;
    private String makefile_SocLib;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private boolean includeUserCode = true;

    private Plugin plugin;

    public AVATAR2CPOSIX(AvatarSpecification _avspec, Plugin _plugin) {
        avspec = _avspec;
        plugin = _plugin;
    }

    public void setTimeUnit(int _timeUnit) {
        timeUnit = _timeUnit;
    }

    public void includeUserCode(boolean _inc) {
        includeUserCode = _inc;
    }

    public static String getGeneratedPath() {
        return GENERATED_PATH;
    }

    public void saveInFiles(String path) throws FileException {

        TraceManager.addDev("save In Files AVATAR2CPOSIX");
        if (!SpecConfigTTool.checkAndCreateAVATARCodeDir(path)) {
            TraceManager.addDev("Directory cannot be created: " + path);
            throw new FileException("ERROR: Executable code directory cannot be created.");
        }
        TraceManager.addDev("Creating dir for saving generated code");
        File src_dir = new File(path + GENERATED_PATH);
        if (!src_dir.exists()) {
            TraceManager.addDev("Creating: " + src_dir.getAbsolutePath());
            src_dir.mkdir();
        }

        TraceManager.addDev("Generating main file");
        if (mainFile != null) {
            TraceManager.addDev("Generating main files in " + path + mainFile.getName() + ".h");
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".h",
                    Conversion.indentString(mainFile.getHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".c",
                    Conversion.indentString(mainFile.getMainCode(), 2));
        }

        TraceManager.addDev("Generating task files");
        for (TaskFile taskFile : taskFiles) {
            TraceManager.addDev("Generating task files: " + (path + GENERATED_PATH + taskFile.getName()));
            FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".h",
                    Conversion.indentString(taskFile.getFullHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".c",
                    Conversion.indentString(taskFile.getMainCode(), 2));
        }

        TraceManager.addDev("Making Makefiles");
        // Standard Makefile
        makeMakefileSrc(GENERATED_PATH);
        FileUtils.saveFile(path + "Makefile.src", makefile_src);

        // Makefile for SocLib
        makeMakefileSocLib();
        FileUtils.saveFile(path + "Makefile.soclib", makefile_SocLib);
    }

    public Vector getWarnings() {
        return warnings;
    }

    public void generateCPOSIX(boolean _debug, boolean _tracing) {
        debug = _debug;
        tracing = _tracing;

        mainFile = new MainFile("main", plugin);
        taskFiles = new Vector<TaskFile>();

        TraceManager.addDev("AVATAR2CPOSIX avspec=" + avspec);

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls();
        avspec.removeTimers();

        // TraceManager.addDev("AVATAR2CPOSIX avspec=" + avspec);

        if (avspec.hasApplicationCode() && includeUserCode) {
            mainFile.appendToBeforeMainCode("/* User code */\n");
            mainFile.appendToBeforeMainCode(avspec.getApplicationCode());
            mainFile.appendToBeforeMainCode("\n/* End of User code */\n\n");
        }

        makeMainMutex();

        makeSynchronousChannels();

        makeAsynchronousChannels();

        makeTasks();

        makeMainHeader();

        makeThreadsInMain(_debug);

    }

    public void makeMainMutex() {
        // Create a main mutex
        mainFile.appendToHCode("/* Main mutex */" + CR);
        mainFile.appendToBeforeMainCode("/* Main mutex */" + CR);
        mainFile.appendToHCode("extern pthread_mutex_t __mainMutex;" + CR + CR);
        mainFile.appendToBeforeMainCode("pthread_mutex_t __mainMutex;" + CR + CR);

    }

    public void makeSynchronousChannels() {

        // Create a synchronous channel per relation/signal
        mainFile.appendToHCode("/* Synchronous channels */" + CR);
        mainFile.appendToBeforeMainCode("/* Synchronous channels */" + CR);
        mainFile.appendToMainCode("/* Synchronous channels */" + CR);
        for (AvatarRelation ar : avspec.getRelations()) {
            if (!ar.isAsynchronous()) {
                for (int i = 0; i < ar.nbOfSignals(); i++) {
                    mainFile.appendToHCode("extern syncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFile.appendToBeforeMainCode("syncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFile.appendToMainCode(
                            "__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
                    mainFile.appendToMainCode(
                            "__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
                    if (ar.isBroadcast()) {
                        mainFile.appendToMainCode("setBroadcast(&__" + getChannelName(ar, i) + ", true);" + CR);
                    }
                }
            }
        }

        // mainFile.appendToHCode("pthread_mutex_t mainMutex;" + CR);

    }

    public void makeAsynchronousChannels() {

        // Create a synchronous channel per relation/signal
        mainFile.appendToHCode("/* Asynchronous channels */" + CR);
        mainFile.appendToBeforeMainCode("/* Asynchronous channels */" + CR);
        mainFile.appendToMainCode("/* Asynchronous channels */" + CR);
        for (AvatarRelation ar : avspec.getRelations()) {
            if (ar.isAsynchronous()) {
                for (int i = 0; i < ar.nbOfSignals(); i++) {
                    mainFile.appendToHCode("extern asyncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFile.appendToBeforeMainCode("asyncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFile.appendToMainCode(
                            "__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
                    mainFile.appendToMainCode(
                            "__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
                    if (ar.isBlocking()) {
                        mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 1;" + CR);
                    } else {
                        mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 0;" + CR);
                    }
                    mainFile.appendToMainCode(
                            "__" + getChannelName(ar, i) + ".maxNbOfMessages = " + ar.getSizeOfFIFO() + ";" + CR);
                }
            }
        }

        // mainFile.appendToHCode("pthread_mutex_t mainMutex;" + CR);

    }

    public void makeTasks() {
        for (AvatarBlock block : avspec.getListOfBlocks()) {
            makeTask(block);
        }
    }

    public void makeTask(AvatarBlock block) {
        TaskFile taskFile = new TaskFile(block.getName());

        // taskFile.addToHeaderCode("#include \"main.h\"" + CR);

        // taskFile.addToMainCode("#include \"" + block.getName() + ".h\"");

        if (includeUserCode) {
            String tmp = block.getGlobalCode();
            if (tmp != null) {
                taskFile.addToMainCode(CR + "// Header code defined in the model" + CR + tmp + CR
                        + "// End of header code defined in the model" + CR + CR);
            }
        }

        defineAllStates(block, taskFile);

        makeMainFunction(block, taskFile);

        taskFiles.add(taskFile);

        String writeFilePathStr = MainGUI.getFileName();
        String readFilePathStr = writeFilePathStr + "~";

        try {

            Path writeFilePath = Paths.get(writeFilePathStr);
            byte[] writeFileBytes = Files.readAllBytes(writeFilePath);

            Path readFilePath = Paths.get(readFilePathStr);
            byte[] readFileBytes = Files.readAllBytes(readFilePath);

            if (Arrays.equals(writeFileBytes, readFileBytes) == false) {

                File writeFile = new File(writeFilePathStr);

                FileOutputStream fos = new FileOutputStream(writeFile);
                fos.write(readFileBytes);
                fos.close();
                TraceManager.addDev("File Auto Saved!!");
            }
        } catch (Exception e) {
            TraceManager.addDev("Error during autosave: " + e.getMessage());
            return;
        }

        String blockName = block.getName();

        try {
            DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentbuilder = documentbuilderfactory.newDocumentBuilder();
            Document document = documentbuilder.parse(new FileInputStream(writeFilePathStr));
            Element element = document.getDocumentElement();
            NodeList components = element.getElementsByTagName("COMPONENT");
            for (int i = 0; i < components.getLength(); i++) {
                Node component = components.item(i);
                boolean findBlock = false;
                for (int j = 0; j < component.getChildNodes().getLength(); j++) {
                    Node maiItems = component.getChildNodes().item(j);

                    // String nodeName = maiItems.getNodeName();
                    if (findBlock == false) {
                        if (maiItems.getNodeName().equals("infoparam")) {
                            // String name = maiItems.getTextContent();
                            NamedNodeMap infoparamAttributes = maiItems.getAttributes();
                            // get the value of the attribute
                            String bolockNameInXML = infoparamAttributes.getNamedItem("value").getNodeValue();

                            if (blockName.equals(bolockNameInXML)) {
                                findBlock = true;
                            }

                        }
                    } else if (findBlock == true){
                        if (maiItems.getNodeName().equals("extraparam")) {
                            for (int k = 0; k < maiItems.getChildNodes().getLength(); k++) {
                                Node extraItems = maiItems.getChildNodes().item(k);
                                if (extraItems.getNodeName().equals("Method")) {
                                    NamedNodeMap methodAttributes = extraItems.getAttributes();
                                    String function = methodAttributes.getNamedItem("value").getNodeValue();
                                    String requireRefinementType = methodAttributes
                                            .getNamedItem("requireRefinementType").getNodeValue();
                                    String ensureRefinementType = methodAttributes.getNamedItem("ensureRefinementType")
                                            .getNodeValue();
                                    String logic = methodAttributes.getNamedItem("logic").getNodeValue();
                                    TraceManager.addDev(function + "," + requireRefinementType + ","
                                            + ensureRefinementType + "," + logic);

                                }

                            }

                        }
                    }

                }

            }
        } catch (Exception e) {
            TraceManager.addDev("Error during xml file: " + e.getMessage());
        }

    }

    public void defineAllStates(AvatarBlock _block, TaskFile _taskFile) {
        int id = 1;

        _taskFile.addToMainCode("#define STATE__START__STATE 0" + CR);

        for (AvatarStateMachineElement asme : _block.getStateMachine().getListOfElements()) {
            if (asme instanceof AvatarState) {
                _taskFile.addToMainCode("#define STATE__" + asme.getName() + " " + id + CR);
                id++;
            }
        }
        _taskFile.addToMainCode("#define STATE__STOP__STATE " + id + CR);
        _taskFile.addToMainCode(CR);
    }

    public void defineAllMethods(AvatarBlock _block, TaskFile _taskFile) {
        Vector<String> allNames = new Vector<String>();
        for (AvatarMethod am : _block.getMethods()) {
            makeMethod(_block, am, allNames, _taskFile);
        }

        // Make method of father
        makeFatherMethod(_block, _block, allNames, _taskFile);
    }

    private void makeFatherMethod(AvatarBlock _originBlock, AvatarBlock _currentBlock, Vector<String> _allNames,
            TaskFile _taskFile) {
        if (_currentBlock.getFather() == null) {
            return;
        }

        for (AvatarMethod am : _currentBlock.getFather().getMethods()) {
            makeMethod(_originBlock, am, _allNames, _taskFile);
        }

        makeFatherMethod(_originBlock, _currentBlock.getFather(), _allNames, _taskFile);

    }

    private void makeMethod(AvatarBlock _block, AvatarMethod _am, Vector<String> _allNames, TaskFile _taskFile) {
        String ret = "";
        List<AvatarAttribute> list;
        List<AvatarAttribute> listA;

        String nameMethod = _block.getName() + "__" + _am.getName();

        for (String s : _allNames) {
            if (s.compareTo(nameMethod) == 0) {
                return;
            }
        }

        list = _am.getListOfReturnAttributes();
        if (list.size() == 0) {
            ret += "void";
        } else {
            ret += getCTypeOf(list.get(0));
        }

        ret += " " + nameMethod + "(";
        list = _am.getListOfAttributes();
        int cpt = 0;
        for (AvatarAttribute aa : list) {
            if (cpt != 0) {
                ret += ", ";
            }
            ret += getCTypeOf(aa) + " " + aa.getName();
            cpt++;
        }

        ret += ") {" + CR;

        if (tracing) {
            String tr = "";
            cpt = 0;
            if (list.size() > 0) {
                ret += "char my__attr[CHAR_ALLOC_SIZE];" + CR;
                ret += "sprintf(my__attr, \"";
                for (AvatarAttribute aa : list) {
                    if (cpt != 0) {
                        tr += ",";
                        ret += ",";
                    }
                    tr += aa.getName();
                    ret += "%d";
                    cpt++;
                }
                ret += "\"," + tr + ");" + CR;
                ret += traceFunctionCall(_block.getName(), _am.getName(), "my__attr");
            } else {
                ret += traceFunctionCall(_block.getName(), _am.getName(), null);
            }
        }

        if (debug) {
            ret += "debugMsg(\"-> ....() Executing method " + _am.getName() + "\");" + CR;

            list = _am.getListOfAttributes();
            cpt = 0;
            for (AvatarAttribute aa : list) {
                ret += "debugInt(\"Attribute " + aa.getName() + " = \"," + aa.getName() + ");" + CR;
            }
        }

        listA = list;
        list = _am.getListOfReturnAttributes();
        if (list.size() != 0) {
            // Returns the first attribute. If not possible, return 0;
            // Implementation is provided by the user?
            // In that case, no need to generate the code!
            if (_am.isImplementationProvided()) {
                ret += "return __userImplemented__" + nameMethod + "(";
                cpt = 0;
                for (AvatarAttribute aaa : listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt++;
                }
                ret += ");" + CR;
                // TraceManager.addDev("Adding a call to the method");

            } else {

                if (listA.size() > 0) {
                    ret += "return " + listA.get(0).getName() + ";" + CR;
                } else {
                    ret += "return 0;" + CR;
                }
            }
        } else {
            if (_am.isImplementationProvided()) {
                ret += "__userImplemented__" + nameMethod + "(";
                cpt = 0;
                for (AvatarAttribute aaa : listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt++;
                }
                ret += ");" + CR;

            }
        }
        ret += "}" + CR + CR;
        _taskFile.addToMainCode(ret + CR);

    }

    public void makeMainHeader() {
        mainFile.appendToBeforeMainCode(CR);
        for (TaskFile taskFile : taskFiles) {
            mainFile.appendToBeforeMainCode("#include \"" + taskFile.getName() + ".h\"" + CR);
        }
        mainFile.appendToBeforeMainCode(CR);

    }

    public void makeMainFunction(AvatarBlock _block, TaskFile _taskFile) {
        int i;

        String s = "void *mainFunc__" + _block.getName() + "(void *arg)";
        String sh = "extern " + s + ";" + CR;
        s += "{" + CR;

        s += makeAttributesDeclaration(_block, _taskFile);

        s += CR + "int __currentState = STATE__START__STATE;" + CR;

        int nbOfMaxParams = _block.getMaxNbOfParams();
        // s+= "request *__req;" + CR;
        TraceManager.addDev("BLOCK NAME:" + _block.getName());
        for (i = 0; i < _block.getMaxNbOfMultipleBranches(); i++) {
            sh += UNUSED_ATTR + " request __req" + i + "__" + _block.getName() + ";" + CR;
            sh += UNUSED_ATTR + "int *__params" + i + "__" + _block.getName() + "[" + nbOfMaxParams + "];" + CR;
        }
        sh += UNUSED_ATTR + "setOfRequests __list__" + _block.getName() + ";" + CR;

        sh += UNUSED_ATTR + "pthread_cond_t __myCond__" + _block.getName() + ";" + CR;
        sh += UNUSED_ATTR + "request *__returnRequest__" + _block.getName() + ";" + CR;
        TraceManager.addDev("ATTRIBUTES OF BLOCK NAME:" + _block.getName() + " sh=" + sh);

        s += CR + "char * __myname = (char *)arg;" + CR;

        /*
         * if (tracing) { s+= CR + "char __value[CHAR_ALLOC_SIZE];" + CR; }
         */

        s += CR + "pthread_cond_init(&__myCond__" + _block.getName() + ", NULL);" + CR;

        s += CR + "fillListOfRequests(&__list__" + _block.getName() + ", __myname, &__myCond__" + _block.getName()
                + ", &__mainMutex);" + CR;

        s += "//printf(\"my name = %s\\n\", __myname);" + CR;

        s += CR + "/* Main loop on states */" + CR;
        s += "while(__currentState != STATE__STOP__STATE) {" + CR;

        s += "switch(__currentState) {" + CR;

        // Making start state
        AvatarStateMachine asm = _block.getStateMachine();
        s += "case STATE__START__STATE: " + CR;
        s += traceStateEntering("__myname", "__StartState");
        s += makeBehaviourFromElement(_block, asm.getStartState(), true);
        s += "break;" + CR + CR;

        String tmp;
        // Making other states
        for (AvatarStateMachineElement asme : asm.getListOfElements()) {
            if (asme instanceof AvatarState) {
                s += "case STATE__" + asme.getName() + ": " + CR;
                s += traceStateEntering("__myname", asme.getName());

                if (includeUserCode) {
                    tmp = ((AvatarState) asme).getEntryCode();
                    if (tmp != null) {
                        if (tmp.trim().length() > 0) {
                            s += "/* Entry code */\n" + tmp + "\n/* End of entry code */\n\n";
                        }
                    }
                }

                s += makeBehaviourFromElement(_block, asme, true);
                s += "break;" + CR + CR;
            }
        }

        s += "}" + CR;

        s += "}" + CR;

        s += "//printf(\"Exiting = %s\\n\", __myname);" + CR;
        s += "return NULL;" + CR;
        s += "}" + CR;
        _taskFile.addToMainCode(s + CR);
        _taskFile.addToHeaderCode(sh + CR);
    }

    public String makeBehaviourFromElement(AvatarBlock _block, AvatarStateMachineElement _asme, boolean firstCall) {
        // AvatarStateMachineElement asme0;

        if (_asme == null) {
            return "";
        }

        String ret = "";
        int i;

        if (_asme instanceof AvatarStartState) {
            return makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarTransition) {
            AvatarTransition at = (AvatarTransition) _asme;

            if (at.isGuarded()) {
                String g = modifyGuard(at.getGuard().toString());

                ret += "if (!" + g + ") {" + CR;
                if (debug) {
                    ret += "debug2Msg(__myname, \"Guard failed: " + g + "\");" + CR;
                }
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;
            }

            if (at.hasDelay()) {
                ret += "waitFor(" + reworkDelay(at.getMinDelay()) + ", " + reworkDelay(at.getMaxDelay()) + ");" + CR;
            }

            // String act;
            ret += makeActionsOfTransaction(_block, at);
            /*
             * for(i=0; i<at.getNbOfAction(); i++) { // Must know whether this is an action
             * or a method call act = at.getAction(i); if (at.isAMethodCall(act)) { ret +=
             * modifyMethodName(_block, act) + ";" + CR; } else { ret += act + ";" + CR; } }
             */

            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarState) {
            if (!firstCall) {
                if (debug) {
                    ret += "debug2Msg(__myname, \"-> (=====) Entering state + " + _asme.getName() + "\");" + CR;
                }
                return ret + "__currentState = STATE__" + _asme.getName() + ";" + CR;
            } else {
                if (_asme.nbOfNexts() == 0) {
                    return ret + "__currentState = STATE__STOP__STATE;" + CR;
                }

                if (_asme.nbOfNexts() == 1) {
                    TraceManager
                            .addDev("Only one next in state " + _asme.getNiceName() + " in block " + _block.getName());
                    return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
                }

                // Complex case of states -> several nexts
                // Put in list all

                // 1) Only immediately executable transitions
                for (i = 0; i < _asme.nbOfNexts(); i++) {
                    if (_asme.getNext(i) instanceof AvatarTransition) {
                        AvatarTransition at = (AvatarTransition) (_asme.getNext(i));

                        if (at.hasActions()) {
                            ret += makeImmediateAction(_block, at, i);
                        } else {
                            if (at.getNext(0) instanceof AvatarActionOnSignal) {
                                ret += makeSignalAction(_block, at, i);
                            } else {
                                // nothing special to do : immediate choice
                                TraceManager.addDev("Make immediate action in block" + _block.getName());
                                ret += makeImmediateAction(_block, at, i);
                            }
                        }
                    }
                }

                // Make all requests
                // Test if at least one request in the list!
                ret += "if (nbOfRequests(&__list__" + _block.getName() + ") == 0) {" + CR;
                ret += "debug2Msg(__myname, \"No possible request\");" + CR;
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;

                ret += "__returnRequest__" + _block.getName() + " = executeListOfRequests(&__list__" + _block.getName()
                        + ");" + CR;
                ret += "clearListOfRequests(&__list__" + _block.getName() + ");" + CR;
                ret += traceRequest(_block);

                // Resulting requests
                for (i = 0; i < _asme.nbOfNexts(); i++) {
                    if (i != 0) {
                        ret += "else ";
                    }
                    AvatarTransition at = (AvatarTransition) (_asme.getNext(i));
                    if (at.hasActions()) {
                        ret += " if (__returnRequest__" + _block.getName() + " == &__req" + i + "__" + _block.getName()
                                + ") {" + CR;
                        ret += makeActionsOfTransaction(_block, at);
                        /*
                         * for(int j=0; j<at.getNbOfAction(); j++) { if
                         * (at.isAMethodCall(at.getAction(j))) { ret += modifyMethodName(_block,
                         * at.getAction(j)) + ";" + CR; } else { ret += at.getAction(j) + ";" + CR;
                         * 
                         * }
                         * 
                         * }
                         */
                        ret += makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                    } else {
                        if (at.getNext(0) instanceof AvatarActionOnSignal) {
                            ret += " if (__returnRequest__" + _block.getName() + " == &__req" + i + "__"
                                    + _block.getName() + ") {" + CR
                                    + makeBehaviourFromElement(_block, at.getNext(0).getNext(0), false) + CR + "}";
                        } else {
                            // nothing special to do : immediate choice
                            ret += " if (__returnRequest__" + _block.getName() + " == &__req" + i + "__"
                                    + _block.getName() + ") {" + CR
                                    + makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                        }
                    }
                    ret += CR;

                }
                return ret;
            }
        }

        if (_asme instanceof AvatarStopState) {
            return ret + "__currentState = STATE__STOP__STATE;" + CR;
        }

        if (_asme instanceof AvatarRandom) {
            AvatarRandom ar = (AvatarRandom) _asme;
            ret += ar.getVariable() + " = computeRandom(" + ar.getMinValue() + ", " + ar.getMaxValue() + ");" + CR;
            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarQueryOnSignal) {
            AvatarQueryOnSignal aqos = (AvatarQueryOnSignal) _asme;
            AvatarSignal as = aqos.getSignal();
            AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
            ret += "__params0" + "__" + _block.getName() + "[0] = &" + aqos.getAttribute().getName() + ";" + CR;

            ret += "makeNewRequest(&__req0" + "__" + _block.getName() + ", " + aqos.getID() + ", QUERY_FIFO_SIZE, " + 0
                    + ", 0, 0, " + "1" + ", __params0__" + _block.getName() + ");" + CR;
            ret += "__req0" + "__" + _block.getName() + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
            ret += "addRequestToList(&__list__" + _block.getName() + ", &__req0__" + _block.getName() + ");" + CR;
            ret += executeOneRequest(_block, "__req0__" + _block.getName());
            ret += traceRequest(_block);

            // ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " +
            // _aaos.getID() + ", SEND_SYNC_REQUEST, " +
            /// delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + "__" +
            // _block.getName() + ");" + CR;

            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarActionOnSignal) {
            AvatarActionOnSignal aaos = (AvatarActionOnSignal) _asme;
            ret += makeSignalAction(_block, aaos, 0, false, "", "");
            AvatarSignal as = aaos.getSignal();
            // AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
            ret += executeOneRequest(_block, "__req0__" + _block.getName());
            ret += traceRequest(_block);
        }

        // Default
        return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
    }

    private String makeSignalAction(AvatarBlock _block, AvatarTransition _at, int _index) {
        String ret = "";
        AvatarActionOnSignal aaos;

        if (!(_at.getNext(0) instanceof AvatarActionOnSignal)) {
            return "";
        }

        aaos = (AvatarActionOnSignal) (_at.getNext(0));

        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString());
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += makeSignalAction(_block, aaos, _index, true, _at.getMinDelay(), _at.getMaxDelay());
        } else {
            ret += makeSignalAction(_block, aaos, _index, false, "", "");
        }
        ret += "addRequestToList(&__list__" + _block.getName() + ", &__req" + _index + "__" + _block.getName() + ");"
                + CR;

        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;
    }

    private String makeSignalAction(AvatarBlock _block, AvatarActionOnSignal _aaos, int _index, boolean hasDelay,
            String minDelay, String maxDelay) {
        String ret = "";
        int i;

        AvatarSignal as = _aaos.getSignal();
        AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);

        String delay;

        if (hasDelay) {
            delay = "1, " + reworkDelay(minDelay) + ", " + reworkDelay(maxDelay);
        } else {
            delay = "0, 0, 0";
        }

        if (ar != null) {

            // Sending
            if (_aaos.isSending()) {
                // Putting params
                for (i = 0; i < _aaos.getNbOfValues(); i++) {
                    ret += "__params" + _index + "__" + _block.getName() + "[" + i + "] = &" + _aaos.getValue(i) + ";"
                            + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                            + ", SEND_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index
                            + "__" + _block.getName() + ");" + CR;
                    ret += "__req" + _index + "__" + _block.getName() + ".asyncChannel = &__" + getChannelName(ar, as)
                            + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                                + ", SEND_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params"
                                + _index + "__" + _block.getName() + ");" + CR;
                        ret += "__req" + _index + "__" + _block.getName() + ".syncChannel = &__"
                                + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                                + ", SEND_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index
                                + "__" + _block.getName() + ");" + CR;
                        ret += "__req" + _index + "__" + _block.getName() + ".syncChannel = &__"
                                + getChannelName(ar, as) + ";" + CR;
                    }
                }

                // Receiving
            } else {
                for (i = 0; i < _aaos.getNbOfValues(); i++) {
                    ret += "__params" + _index + "__" + _block.getName() + "[" + i + "] = &" + _aaos.getValue(i) + ";"
                            + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                            + ", RECEIVE_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index
                            + "__" + _block.getName() + ");" + CR;
                    ret += "__req" + _index + "__" + _block.getName() + ".asyncChannel = &__" + getChannelName(ar, as)
                            + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                                + ", RECEIVE_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params"
                                + _index + "__" + _block.getName() + ";" + CR;
                        ret += "__req" + _index + "__" + _block.getName() + ".syncChannel = &__"
                                + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _aaos.getID()
                                + ", RECEIVE_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params"
                                + _index + "__" + _block.getName() + ");" + CR;
                        ret += "__req" + _index + "__" + _block.getName() + ".syncChannel = &__"
                                + getChannelName(ar, as) + ";" + CR;
                    }
                }
            }
        }

        return ret;
    }

    private String makeImmediateAction(AvatarBlock _block, AvatarTransition _at, int _index) {
        String ret = "";
        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString());
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _at.getID() + ", IMMEDIATE, 1, "
                    + reworkDelay(_at.getMinDelay()) + ", " + reworkDelay(_at.getMaxDelay()) + ", 0, __params" + _index
                    + "__" + _block.getName() + ");" + CR;
        } else {
            ret += "makeNewRequest(&__req" + _index + "__" + _block.getName() + ", " + _at.getID()
                    + ", IMMEDIATE, 0, 0, 0, 0, __params" + _index + "__" + _block.getName() + ");" + CR;
        }
        ret += "addRequestToList(&__list__" + _block.getName() + ", &__req" + _index + "__" + _block.getName() + ");"
                + CR;
        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;

    }

    private String executeOneRequest(AvatarBlock _block, String var) {
        String ret = "__returnRequest__" + _block.getName() + " = executeOneRequest(&__list__" + _block.getName()
                + ", &" + var + ");" + CR;
        ret += "clearListOfRequests(&__list__" + _block.getName() + ");" + CR;
        return ret;
    }

    public String makeAttributesDeclaration(AvatarBlock _block, TaskFile _taskFile) {
        String ret = "";
        for (AvatarAttribute aa : _block.getAttributes()) {
            ret += getCTypeOf(aa) + " " + aa.getName() + " = " + aa.getInitialValue() + ";" + CR;
        }
        return ret;
    }

    public void makeThreadsInMain(boolean _debug) {
        mainFile.appendToMainCode(CR + "/* Threads of tasks */" + CR);
        for (TaskFile taskFile : taskFiles) {
            mainFile.appendToMainCode("pthread_t thread__" + taskFile.getName() + ";" + CR);
        }

        makeArgumentsInMain(_debug);

        if (_debug) {
            mainFile.appendToMainCode("/* Activating debug messages */" + CR);
            mainFile.appendToMainCode("activeDebug();" + CR);
        }

        mainFile.appendToMainCode("/* Activating randomness */" + CR);
        mainFile.appendToMainCode("initRandom();" + CR);

        mainFile.appendToMainCode("/* Initializing the main mutex */" + CR);
        mainFile.appendToMainCode("if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}" + CR + CR);

        mainFile.appendToMainCode("/* Initializing mutex of messages */" + CR);
        mainFile.appendToMainCode("initMessages();" + CR);

        if (avspec.hasApplicationCode() && includeUserCode) {
            mainFile.appendToMainCode("/* User initialization */" + CR);
            mainFile.appendToMainCode("__user_init();" + CR);
        }

        mainFile.appendToMainCode(CR + CR + mainDebugMsg("Starting tasks"));
        for (TaskFile taskFile : taskFiles) {
            mainFile.appendToMainCode("pthread_create(&thread__" + taskFile.getName() + ", NULL, mainFunc__"
                    + taskFile.getName() + ", (void *)\"" + taskFile.getName() + "\");" + CR);
        }

        mainFile.appendToMainCode(CR + CR + mainDebugMsg("Joining tasks"));
        for (TaskFile taskFile : taskFiles) {
            mainFile.appendToMainCode("pthread_join(thread__" + taskFile.getName() + ", NULL);" + CR);
        }

        mainFile.appendToMainCode(CR + CR + mainDebugMsg("Application terminated"));
        mainFile.appendToMainCode("return 0;" + CR);
    }

    public void makeArgumentsInMain(boolean _debug) {
        mainFile.appendToMainCode("/* Activating tracing  */" + CR);

        if (tracing) {
            mainFile.appendToMainCode("if (argc>1){" + CR);
            mainFile.appendToMainCode("activeTracingInFile(argv[1]);" + CR + "} else {" + CR);
            mainFile.appendToMainCode("activeTracingInConsole();" + CR + "}" + CR);
        }
    }

    public void makeMakefileSrc(String _path) {
        makefile_src = "SRCS = ";
        makefile_src += _path + "main.c ";
        for (TaskFile taskFile : taskFiles) {
            makefile_src += _path + taskFile.getName() + ".c ";
        }

    }

    public void makeMakefileSocLib() {
        makefile_SocLib = "objs = ";
        makefile_SocLib += "main.o ";
        for (TaskFile taskFile : taskFiles) {
            makefile_SocLib += taskFile.getName() + ".o ";
        }

    }

    public String getCTypeOf(AvatarAttribute _aa) {
        String ret = "undefined_type";
        if (_aa.getType() == AvatarType.BOOLEAN) {
            ret = "bool";
        } else if (_aa.getType() == AvatarType.INTEGER) {
            ret = "int";
        } else if (_aa.getType() == AvatarType.INT8) {
            ret = "int8_t";
        } else if (_aa.getType() == AvatarType.INT16) {
            ret = "int16_t";
        } else if (_aa.getType() == AvatarType.INT32) {
            ret = "int32_t";
        } else if (_aa.getType() == AvatarType.INT64) {
            ret = "int64_t";
        } else if (_aa.getType() == AvatarType.UINT8) {
            ret = "uint8_t";
        } else if (_aa.getType() == AvatarType.UINT16) {
            ret = "uint16_t";
        } else if (_aa.getType() == AvatarType.UINT32) {
            ret = "uint32_t";
        } else if (_aa.getType() == AvatarType.UINT64) {
            ret = "uint64_t";
        } else {
            String variableName = _aa.getName();
            ret = _aa.getType().getTypeName(variableName);
        }

        return ret;
    }

    public String getChannelName(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_"
                + _ar.getSignal2(_index).getName();
    }

    public String getChannelName(AvatarRelation _ar, AvatarSignal _as) {
        int index = _ar.getIndexOfSignal(_as);
        return getChannelName(_ar, index);
    }

    public String modifyGuard(String _g) {
        String g = Conversion.replaceAllString(_g, "[", "(").trim();
        g = Conversion.replaceAllString(g, "]", ")").trim();
        g = Conversion.replaceOp(g, "and", "&&");
        g = Conversion.replaceOp(g, "or", "||");
        g = Conversion.replaceOp(g, "not", "!");
        TraceManager.addDev("Guard=" + g);
        return g;
    }

    public String reworkDelay(String _delay) {

        switch (timeUnit) {
            case USEC:
                return _delay;
            case MSEC:
                return "(" + _delay + ")*1000";
            case SEC:
                return "(" + _delay + ")*1000000";
        }

        return _delay;
    }

    private String modifyMethodName(AvatarBlock _ab, AvatarTerm term) {
        if (term instanceof AvatarAttribute)
            return term.getName();
        if (term instanceof AvatarConstant)
            return term.getName();
        if (term instanceof AvatarTermRaw)
            return term.getName();
        if (term instanceof AvatarArithmeticOp) {
            AvatarArithmeticOp aop = (AvatarArithmeticOp) term;
            return this.modifyMethodName(_ab, aop.getTerm1()) + aop.getOperator()
                    + this.modifyMethodName(_ab, aop.getTerm2());
        }
        if (term instanceof AvatarTuple) {
            boolean first = true;
            String res = "(";
            for (AvatarTerm tterm : ((AvatarTuple) term).getComponents()) {
                if (first)
                    first = false;
                else
                    res += ", ";
                res += this.modifyMethodName(_ab, tterm);
            }

            return res + ")";
        }
        if (term instanceof AvatarTermFunction)
            return _ab.getName() + "__" + ((AvatarTermFunction) term).getMethod().getName()
                    + this.modifyMethodName(_ab, ((AvatarTermFunction) term).getArgs());
        return "";
    }

    private String traceRequest(AvatarBlock _block) {
        if (!tracing) {
            return "";
        }
        return "traceRequest(__myname, __returnRequest__" + _block.getName() + ");" + CR;
    }

    private String traceVariableModification(String blockName, String varName, String type) {
        if (!tracing) {
            return "";
        }

        return "traceVariableModification(\"" + blockName + "\", \"" + varName + "\", " + varName + "," + type + ");"
                + CR;
    }

    private String traceFunctionCall(String blockName, String functionName, String params) {
        if (!tracing) {
            return "";
        }

        if (params == null) {
            params = "\"-\"";
        }
        return "traceFunctionCall(\"" + blockName + "\", \"" + functionName + "\", " + params + ");" + CR;
    }

    private String traceStateEntering(String name, String stateName) {
        if (!tracing) {
            return "";
        }
        return "traceStateEntering(" + name + ", \"" + stateName + "\");" + CR;
    }

    private String mainDebugMsg(String s) {
        if (!debug) {
            return "";
        }
        return "debugMsg(\"" + s + "\");" + CR;
    }

    private String taskDebugMsg(String s) {
        if (!debug) {
            return "";
        }

        return "debug2Msg(__myname, \"" + s + "\");" + CR;
    }

    public String makeActionsOfTransaction(AvatarBlock _block, AvatarTransition _at) {
        String ret = "";
        String type;
        for (int i = 0; i < _at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call

            AvatarAction act = _at.getAction(i);
            TraceManager.addDev("Action=" + act);
            if (act.isAMethodCall()) {
                TraceManager.addDev("Method call");
                String actModified = modifyMethodName(_block, (AvatarTermFunction) act);
                ret += actModified + ";" + CR;
            } else {
                TraceManager.addDev("Else");
                String actModified = modifyMethodName(_block, ((AvatarActionAssignment) act).getLeftHand()) + " = "
                        + modifyMethodName(_block, ((AvatarActionAssignment) act).getRightHand());
                AvatarLeftHand leftHand = ((AvatarActionAssignment) act).getLeftHand();
                ret += actModified + ";" + CR;
                if (leftHand instanceof AvatarAttribute) {
                    if (((AvatarAttribute) leftHand).isInt()) {
                        type = "0";
                    } else {
                        type = "1";
                    }
                    ret += traceVariableModification(_block.getName(), leftHand.getName(), type);
                }

            }
        }

        return ret;
    }

}

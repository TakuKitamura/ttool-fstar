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

import avatartranslator.*;
import myutil.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Class AVATAR2CPOSIXArduino
 * Creation: 05/11/2017 by Berkey Koksal
 *
 * Developed by : Dhiaeddine ALIOUI
 * @version 3 15/11/2018
 */
public class AVATAR2CPOSIXArduino {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;


    private final static String UNUSED_ATTR = "__attribute__((unused))";
    private final static String GENERATED_PATH = "generated_src" + File.separator;
    private final static String UNKNOWN = "UNKNOWN";
    private final static String CR = "\n";

    private AvatarSpecification avspec;

    private Vector warnings;

    private MainFile mainFile;
    private MainFile_Arduino mainFile_Arduino;

    private String makefile_src;
    private String makefile_SocLib;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private boolean includeUserCode = true;

    private Plugin plugin;


    public AVATAR2CPOSIXArduino(AvatarSpecification _avspec, Plugin _plugin) {
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

        TraceManager.addDev("Generating files");

        if (mainFile_Arduino != null) {
            TraceManager.addDev("Generating main files in " + path + mainFile_Arduino.getName() + ".ino");
            FileUtils.saveFile(path + GENERATED_PATH + mainFile_Arduino.getName() + ".ino",
                    Conversion.indentString(mainFile_Arduino.getAllCode(), 2));
        }


    }


    public Vector getWarnings() {
        return warnings;
    }


    public void generateArduinoCode(boolean _debug, boolean _tracing) {

        debug = _debug;
        tracing = _tracing;

        mainFile_Arduino = new MainFile_Arduino("mainArduino");

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls();
        avspec.removeTimers();


        if (avspec.hasApplicationCode() && includeUserCode) {
            mainFile_Arduino.appendToBeforeSetupCode("/* User code */\n");
            mainFile_Arduino.appendToBeforeSetupCode(avspec.getApplicationCode());
            mainFile_Arduino.appendToBeforeSetupCode("\n/* End of User code */\n");
        }


        makeTasks();


    }


    public void makeTasks() {

        mainFile_Arduino.appendToSetupCode("/* Activating randomness */" + CR);
        mainFile_Arduino.appendToSetupCode("initRandom(0);" + CR);
        mainFile_Arduino.appendToSetupCode("char __myname[] = \"\";"+CR);

        for (AvatarBlock block : avspec.getListOfBlocks()) {
            makeTask(block);
        }
    }

    public void makeTask(AvatarBlock block) {

        if (includeUserCode) {
            String tmp = block.getGlobalCode();
            if (tmp != null) {
                mainFile_Arduino.appendToBeforeSetupCode(CR + "// Header code defined in the model" + CR + tmp + CR + "// End of header code defined in the model" + CR + CR);
            }
        }

        defineAllMethods(block);

        defineAllStates(block);

        mainFile_Arduino.appendToSetupCode("fillListOfRequests(&"+ block.getName() +"__list, __myname, &mainConditionaVariable, &mutex);"+CR);

        mainFile_Arduino.appendToBeforeSetupCode("void Task_" + block.getName() + "( void *pvParameters );" + CR);

        mainFile_Arduino.appendToSetupCode("xTaskCreate (Task_" + block.getName() + ", (const portCHAR *)\"" + block.getName() + "\", 128, NULL, 1, NULL);" + CR);

        mainFile_Arduino.appendToAfterLoopCode("void Task_" + block.getName() + "( void *pvParameters )" + CR + "{" + CR + "(void) pvParameters;" + CR);

        makeMainFunction(block);

        mainFile_Arduino.appendToAfterLoopCode("}" + CR);


    }

    public String makeAttributesDeclaration(AvatarBlock _block) {
        String ret = "";
        for (AvatarAttribute aa : _block.getAttributes()) {
            ret += getCTypeOf(aa) + " " + aa.getName() + " = " + aa.getInitialValue() + ";" + CR;
        }
        return ret;
    }

    public void defineAllStates(AvatarBlock _block) {
        int id = 1;
        mainFile_Arduino.appendToBeforeSetupCode(CR);
        mainFile_Arduino.appendToBeforeSetupCode("#define " + _block.getName() + "_STATE__START__STATE 0" + CR);

        for (AvatarStateMachineElement asme : _block.getStateMachine().getListOfElements()) {
            if (asme instanceof AvatarState) {
                mainFile_Arduino.appendToBeforeSetupCode("#define " + _block.getName() + "_STATE__" + asme.getName() + " " + id + CR);
                id++;
            }
        }
        mainFile_Arduino.appendToBeforeSetupCode("#define " + _block.getName() + "_STATE__STOP__STATE " + id + CR);

    }

    public void defineAllMethods(AvatarBlock _block) {
        Vector<String> allNames = new Vector<String>();
        for (AvatarMethod am : _block.getMethods()) {
            makeMethod(_block, am, allNames);
        }

        // Make method of father
        makeFatherMethod(_block, _block, allNames);
    }

    private void makeFatherMethod(AvatarBlock _originBlock, AvatarBlock _currentBlock, Vector<String> _allNames) {
        if (_currentBlock.getFather() == null) {
            return;
        }

        for (AvatarMethod am : _currentBlock.getFather().getMethods()) {
            makeMethod(_originBlock, am, _allNames);
        }

        makeFatherMethod(_originBlock, _currentBlock.getFather(), _allNames);

    }

    private void makeMethod(AvatarBlock _block, AvatarMethod _am, Vector<String> _allNames) {
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


        if (debug) {
            ret += "debug2Msg(\"-> ....() Executing method " + _am.getName() + "\");" + CR;

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
                //TraceManager.addDev("Adding a call to the method");

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
        mainFile_Arduino.appendToBeforeSetupCode(CR + ret);

    }


    public void makeMainFunction(AvatarBlock _block) {
        int i;

        String s = "";
        String s2="";

        s += makeAttributesDeclaration(_block);

        s+=CR+"mutex.unlock();"+CR;

        s2 += CR + "int "+_block.getName()+"__currentState = " + _block.getName() + "_STATE__START__STATE;" + CR;

        int nbOfMaxParams = _block.getMaxNbOfParams();
        //s+= "request *__req;" + CR;
        for (i = 0; i < _block.getMaxNbOfMultipleBranches(); i++) {
            s2 += "request " + _block.getName() + "__req" + i + ";" + CR;
            s2 += "int *" + _block.getName() + "__params" + i + "[" + nbOfMaxParams + "];" + CR;
        }
        s2 += "setOfRequests " + _block.getName() + "__list;" + CR;
        s2 += "request *" + _block.getName() + "__returnRequest;" + CR;

        mainFile_Arduino.appendToBeforeSetupCode(s2);

        s += CR + "/* Main loop on states */" + CR;
        s += "while("+_block.getName()+"__currentState != " + _block.getName() + "_STATE__STOP__STATE) {" + CR;

        s += "switch("+_block.getName()+"__currentState) {" + CR;

        // Making start state
        AvatarStateMachine asm = _block.getStateMachine();
        s += "case " + _block.getName() + "_STATE__START__STATE: " + CR;
        s += makeBehaviourFromElement(_block, asm.getStartState(), true);
        s += "break;" + CR + CR;

        String tmp;
        // Making other states
        for (AvatarStateMachineElement asme : asm.getListOfElements()) {
            if (asme instanceof AvatarState) {
                s += "case " + _block.getName() + "_STATE__" + asme.getName() + ": " + CR;

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

        s += "while(1){};";

        mainFile_Arduino.appendToAfterLoopCode(s + CR);
    }

    public String makeBehaviourFromElement(AvatarBlock _block, AvatarStateMachineElement _asme, boolean firstCall) {
        //AvatarStateMachineElement asme0;

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
                ret += ""+_block.getName()+"__currentState = " + _block.getName() + "_STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;
            }

            if (at.hasDelay()) {
                ret += "waitFor(" + reworkDelay(at.getMinDelay()) + "," + reworkDelay(at.getMaxDelay()) + ");" + CR;
            }


            //+
            ret += makeActionsOfTransaction(_block, at);


            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarState) {
            if (!firstCall) {

                return ret + ""+_block.getName()+"__currentState = " + _block.getName() + "_STATE__" + _asme.getName() + ";" + CR;
            } else {
                if (_asme.nbOfNexts() == 0) {
                    return ret + ""+_block.getName()+"__currentState = " + _block.getName() + "_STATE__STOP__STATE;" + CR;
                }

                if (_asme.nbOfNexts() == 1) {
                    return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
                }

                // Complex case of states -> several nexts
                // Put in list all


                // 1) Only immediatly executable transitions
                for (i = 0; i < _asme.nbOfNexts(); i++) {
                    if (_asme.getNext(i) instanceof AvatarTransition) {
                        AvatarTransition at = (AvatarTransition) (_asme.getNext(i));

                        if (at.hasActions()) {
                            ret += makeImmediateAction(at, i,_block);
                        } else {
                            if (at.getNext(0) instanceof AvatarActionOnSignal) {
                                ret += makeSignalAction(at, i,_block);
                            } else {
                                // nothing special to do : immediate choice
                                ret += makeImmediateAction(at, i,_block);
                            }
                        }
                    }
                }

                // Make all requests
                // Test if at least one request in the list!
                ret += "if (nbOfRequests(&" + _block.getName() + "__list) == 0) {" + CR;
                ret += "debug2Msg(\"No possible request\");" + CR;
                ret += ""+_block.getName()+"__currentState = " + _block.getName() + "_STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;

                ret += _block.getName() + "__returnRequest = executeListOfRequests(&" + _block.getName() + "__list);" + CR;
                ret += "clearListOfRequests(&" + _block.getName() + "__list);" + CR;

                // Resulting requests
                for (i = 0; i < _asme.nbOfNexts(); i++) {
                    if (i != 0) {
                        ret += "else ";
                    }
                    AvatarTransition at = (AvatarTransition) (_asme.getNext(i));
                    if (at.hasActions()) {
                        ret += " if (" + _block.getName() + "__returnRequest == &" + _block.getName() + "__req" + i + ") {" + CR;
                        ret += makeActionsOfTransaction(_block, at);
                        /*for(int j=0; j<at.getNbOfAction(); j++) {
                          if (at.isAMethodCall(at.getAction(j))) {
                          ret +=  modifyMethodName(_block, at.getAction(j)) + ";" + CR;
                          } else {
                          ret +=  at.getAction(j) + ";" + CR;

                          }

                          }*/
                        ret += makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                    } else {
                        if (at.getNext(0) instanceof AvatarActionOnSignal) {
                            ret += " if (" + _block.getName() + "__returnRequest == &" + _block.getName() + "__req" + i + ") {" + CR + makeBehaviourFromElement(_block,
                                    at.getNext(0).getNext(0),
                                    false) + CR + "}";
                        } else {
                            // nothing special to do : immediate choice
                            ret += " if (" + _block.getName() + "__returnRequest == &" + _block.getName() + "__req" + i + ") {" + CR + makeBehaviourFromElement(_block,
                                    at.getNext(0), false) + CR + "}";
                        }
                    }
                    ret += CR;
                }
                return ret;
            }
        }

        if (_asme instanceof AvatarStopState) {
            return ret + ""+_block.getName()+"__currentState = " + _block.getName() + "_STATE__STOP__STATE;" + CR;
        }

        if (_asme instanceof AvatarRandom) {
            AvatarRandom ar = (AvatarRandom) _asme;
            ret += ar.getVariable() + " = computeRandom(" + ar.getMinValue() + ", " + ar.getMaxValue() + ");" + CR;
            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarActionOnSignal) {
            AvatarActionOnSignal aaos = (AvatarActionOnSignal) _asme;
            ret += makeSignalAction(aaos, 0, false, "", "",_block);
            AvatarSignal as = aaos.getSignal();
            //AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
            ret += executeOneRequest(_block.getName() + "__req0",_block);
        }

        // Default
        return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
    }

    private String makeSignalAction(AvatarTransition _at, int _index,AvatarBlock _block) {
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
            ret += makeSignalAction(aaos, _index, true, _at.getMinDelay(), _at.getMaxDelay(),_block);
        } else {
            ret += makeSignalAction(aaos, _index, false, "", "",_block);
        }
        ret += "addRequestToList(&"+_block.getName()+"__list, &"+_block.getName()+"__req" + _index + ");" + CR;

        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;
    }


    public String makeActionsOfTransaction(AvatarBlock _block, AvatarTransition _at) {
        String ret = "";
        String type;
        for (int i = 0; i < _at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call

            AvatarAction act = _at.getAction(i);
            if (act.isAMethodCall()) {
                String actModified = modifyMethodName(_block, (AvatarTermFunction) act);
                ret += actModified + ";" + CR;
            } else {
                String actModified = modifyMethodName(_block, ((AvatarActionAssignment) act).getLeftHand())
                        + " = " + modifyMethodName(_block, ((AvatarActionAssignment) act).getRightHand());
                AvatarLeftHand leftHand = ((AvatarActionAssignment) act).getLeftHand();
                ret += actModified + ";" + CR;
                if (leftHand instanceof AvatarAttribute) {
                    if (((AvatarAttribute) leftHand).isInt()) {
                        type = "0";
                    } else {
                        type = "1";
                    }
                }

            }
        }

        return ret;
    }

    private String makeSignalAction(AvatarActionOnSignal _aaos, int _index, boolean hasDelay, String minDelay, String maxDelay,AvatarBlock _block) {
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
                    ret += _block.getName()+"__params" + _index + "[" + i + "] = &" + _aaos.getValue(i) + ";" + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", SEND_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                    ret += _block.getName()+"__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", SEND_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                        ret += _block.getName()+"__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", SEND_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                        ret += _block.getName()+"__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    }
                }

                // Receiving
            } else {
                for (i = 0; i < _aaos.getNbOfValues(); i++) {
                    ret += _block.getName()+"__params" + _index + "[" + i + "] = &" + _aaos.getValue(i) + ";" + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", RECEIVE_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                    ret += _block.getName()+"__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", RECEIVE_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                        ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _aaos.getID() + ", RECEIVE_SYNC_REQUEST, " + delay +
                                ", " + _aaos.getNbOfValues() + ", "+_block.getName()+"__params" + _index + ");" + CR;
                        ret += _block.getName()+"__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    }
                }
            }
        }

        return ret;
    }

    private String makeImmediateAction(AvatarTransition _at, int _index,AvatarBlock _block) {
        String ret = "";
        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString());
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 1, " + reworkDelay(_at.getMinDelay()) + ", " + reworkDelay(_at.getMaxDelay()) + ", 0, "+_block.getName()+"__params" + _index + ");" + CR;
        } else {
            ret += "makeNewRequest(&"+_block.getName()+"__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 0, 0, 0, 0, "+_block.getName()+"__params" + _index +
                ");" + CR;
        }
        ret += "addRequestToList(&"+_block.getName()+"__list, &"+_block.getName()+"__req" + _index + ");" + CR;
        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;

    }

    private String executeOneRequest(String var,AvatarBlock _block) {
        String ret = _block.getName()+"__returnRequest = executeOneRequest(&"+_block.getName()+"__list, &" + var + ");" + CR;
        ret += "clearListOfRequests(&"+_block.getName()+"__list);" + CR;
        return ret;
    }

    public String getChannelName(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
    }

    public String getChannelName(AvatarRelation _ar, AvatarSignal _as) {
        int index = _ar.getIndexOfSignal(_as);
        return getChannelName(_ar, index);
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
            return this.modifyMethodName(_ab, aop.getTerm1())
                    + aop.getOperator()
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

    public String getCTypeOf(AvatarAttribute _aa) {
        String ret = "int";
        if (_aa.getType() == AvatarType.BOOLEAN) {
            ret = "bool";
        }
        return ret;
    }

    public String reworkDelay(String _delay) {

        switch (timeUnit) {
            case MSEC:
                return _delay;
            case USEC:
                return "(" + _delay + ")/1000";
            case SEC:
                return "(" + _delay + ")*1000";
        }

        return _delay;
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




}



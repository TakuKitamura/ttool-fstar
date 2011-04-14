/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class AVATAR2CPOSIX
* Creation: 29/03/2011
* @version 1.1 29/03/2011
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.toexecutable;

import java.awt.*;
import java.util.*;

import java.io.*;

import myutil.*;
import avatartranslator.*;

public class AVATAR2CPOSIX {

	private final static int USEC = 0;
	private final static int MSEC = 1;
	private final static int SEC = 2;
	
	
	private final static String GENERATED_PATH = "generated_src" + File.separator;
	private final static String UNKNOWN = "UNKNOWN";
	private final static String CR = "\n";
	
	private AvatarSpecification avspec;
	
	private Vector warnings;
	
	private MainFile mainFile;
	private Vector<TaskFile> taskFiles;
	private String makefile_src;
	
	private int timeUnit;
	private boolean debug;
	private boolean tracing;
	

	public AVATAR2CPOSIX(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	public void setTimeUnit(int _timeUnit) {
		timeUnit = _timeUnit;
	}
	
	public static String getGeneratedPath() {
		return GENERATED_PATH;
	}
	
	
	public void saveInFiles(String path) throws FileException {
		
		TraceManager.addDev("Generating files");
		
		if (mainFile != null) {
			TraceManager.addDev("Generating main files in " + path + mainFile.getName() + ".h");
			FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".h", Conversion.indentString(mainFile.getHeaderCode(), 2));
			FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".c", Conversion.indentString(mainFile.getMainCode(), 2));
		}
		
		for(TaskFile taskFile: taskFiles) {
			FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".h", Conversion.indentString(taskFile.getFullHeaderCode(), 2));
			FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".c", Conversion.indentString(taskFile.getMainCode(), 2));
		}
		
		makeMakefileSrc(GENERATED_PATH);
		FileUtils.saveFile(path + "Makefile.src", makefile_src);
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public void generateCPOSIX(boolean _debug, boolean _tracing) {
		debug = _debug;
		tracing = _tracing;
		
		mainFile = new MainFile("main");
		taskFiles = new Vector<TaskFile>();
		
		avspec.removeCompositeStates();
		avspec.removeTimers();
		
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
		for(AvatarRelation ar: avspec.getRelations()) {
				if (!ar.isAsynchronous()) {
					for(int i=0; i<ar.nbOfSignals(); i++) {
						mainFile.appendToHCode("extern syncchannel __" + getChannelName(ar, i)  + ";" + CR);
						mainFile.appendToBeforeMainCode("syncchannel __" + getChannelName(ar, i) + ";" + CR);
						mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
						mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
					}
				}
		}
			
		//mainFile.appendToHCode("pthread_mutex_t mainMutex;" + CR);
		
	}
	
	public void makeAsynchronousChannels() {
		
		// Create a synchronous channel per relation/signal
		mainFile.appendToHCode("/* Asynchronous channels */" + CR);
		mainFile.appendToBeforeMainCode("/* Asynchronous channels */" + CR);
		mainFile.appendToMainCode("/* Asynchronous channels */" + CR);
		for(AvatarRelation ar: avspec.getRelations()) {
				if (ar.isAsynchronous()) {
					for(int i=0; i<ar.nbOfSignals(); i++) {
						mainFile.appendToHCode("extern asyncchannel __" + getChannelName(ar, i)  + ";" + CR);
						mainFile.appendToBeforeMainCode("asyncchannel __" + getChannelName(ar, i) + ";" + CR);
						mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
						mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
						if (ar.isBlocking()) {
							mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 1;" + CR);
						} else {
							mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 0;" + CR);
						}
						mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".maxNbOfMessages = " + ar.getSizeOfFIFO() + ";" + CR);
					}
				}
		}
			
		//mainFile.appendToHCode("pthread_mutex_t mainMutex;" + CR);
		
	}
	
	public void makeTasks() {
		for(AvatarBlock block: avspec.getListOfBlocks()) {
			makeTask(block);
		}
	}
	
	public void makeTask(AvatarBlock block) {
		TaskFile taskFile = new TaskFile(block.getName());
		
		//taskFile.addToHeaderCode("#include \"main.h\"" + CR);
		
		//taskFile.addToMainCode("#include \"" + block.getName() + ".h\"");
		
		
		defineAllStates(block, taskFile);
		
		defineAllMethods(block, taskFile);
		
		makeMainFunction(block, taskFile);
		
		taskFiles.add(taskFile);
	}
	
	public void defineAllStates(AvatarBlock _block, TaskFile _taskFile) {
		int id = 1;
		
		_taskFile.addToMainCode("#define STATE__START__STATE 0" + CR);
		
		for (AvatarStateMachineElement asme: _block.getStateMachine().getListOfElements()) {
			if (asme instanceof AvatarState) {
				_taskFile.addToMainCode("#define STATE__" + asme.getName() + " " + id + CR);
				id ++;
			}
		}
		_taskFile.addToMainCode("#define STATE__STOP__STATE " + id + CR);
		_taskFile.addToMainCode(CR);
	}
	
	public void defineAllMethods(AvatarBlock _block, TaskFile _taskFile) {
		String ret = "";
		LinkedList<AvatarAttribute> list;
		
		for (AvatarMethod am: _block.getMethods()) {
			list = am.getListOfReturnAttributes();
			if (list.size() == 0) {
				ret += "void";
			} else {
				ret += getCTypeOf(list.get(0));
			}
			ret += " " + _block.getName() + "__" +am.getName() + "(";
			list = am.getListOfAttributes();
			int cpt = 0;
			for(AvatarAttribute aa: list) {
				if (cpt != 0) {
					ret += ", ";
				}
				ret += getCTypeOf(aa) + " " + aa.getName();
				cpt ++;
			}
			
			ret += ") {" + CR;
			
			if (tracing) {
				ret += traceFunctionCall(_block.getName(), am.getName());
			}
			
			if (debug) {
				ret += "debugMsg(\"-> ....() Executing method " + am.getName() + "\");" + CR;
				
				list = am.getListOfAttributes();
				cpt = 0;
				for(AvatarAttribute aa: list) {
					ret += "debugInt(\"Attribute " + aa.getName() + " = \"," + aa.getName() + ");" + CR;
				}
			}
			
			list = am.getListOfReturnAttributes();
			if (list.size() != 0) {
				ret += "return 0;" + CR;
			} 
			ret += "}" + CR + CR;
		}
		_taskFile.addToMainCode(ret + CR);
		
	}
	
	public void makeMainHeader() {
		mainFile.appendToBeforeMainCode(CR);
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToBeforeMainCode("#include \"" + taskFile.getName() + ".h\"" + CR);
		}
		mainFile.appendToBeforeMainCode(CR);
		
	}
	
	public void makeMainFunction(AvatarBlock _block, TaskFile _taskFile) {
		int i;
		
		String s = "void *mainFunc__" + _block.getName() + "(void *arg)";
		String sh = "extern " + s + ";" + CR;
		s+= "{" + CR;
			
		s += makeAttributesDeclaration(_block, _taskFile);	
		
		s+= CR + "int __currentState = STATE__START__STATE;" + CR;
			
		int nbOfMaxParams = _block.getMaxNbOfParams();
		//s+= "request *__req;" + CR;
		for(i=0; i<_block.getMaxNbOfMultipleBranches(); i++) {
			s+= "request __req" + i + ";" + CR;
			s+= "int *__params" + i + "[" + nbOfMaxParams + "];" + CR;
		}
		s+= "setOfRequests __list;" + CR;
		
		s+= "pthread_cond_t __myCond;" + CR;
		s+= "request *__returnRequest;" + CR;
		
		s+= CR + "char * __myname = (char *)arg;" + CR;
		
		s+= CR + "pthread_cond_init(&__myCond, NULL);" + CR;
		
		s+= CR + "fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);" + CR; 
		
		s+= "printf(\"my name = %s\\n\", __myname);" + CR;
		
		s+= CR + "/* Main loop on states */" + CR;
		s+= "while(__currentState != STATE__STOP__STATE) {" + CR;
		
		s += "switch(__currentState) {" + CR;
		
		// Making start state
		AvatarStateMachine asm = _block.getStateMachine();
		s += "case STATE__START__STATE: " + CR;
		s += traceStateEntering("__myname", "start state");
		s += makeBehaviourFromElement(_block, asm.getStartState(), true);
		s += "break;" + CR + CR;
		
		// Making other states
		for(AvatarStateMachineElement asme: asm.getListOfElements()) {
			if (asme instanceof AvatarState) {
				s += "case STATE__" + asme.getName() + ": " + CR;
				s += traceStateEntering("__myname", asme.getName());
				s += makeBehaviourFromElement(_block, asme, true);
				s += "break;" + CR + CR;
			}
		}
		
		s += "}" + CR;
		
		s += "}" + CR;
		
		s+= "printf(\"Exiting = %s\\n\", __myname);" + CR;
		s+= "return NULL;" + CR;
		s += "}" + CR;	
		_taskFile.addToMainCode(s + CR);
		_taskFile.addToHeaderCode(sh + CR);	
	}
	
	public String makeBehaviourFromElement(AvatarBlock _block, AvatarStateMachineElement _asme, boolean firstCall) {
		AvatarStateMachineElement asme0;
		
		
		if (_asme == null) {
			return "";
		}
		
		String ret = "";
		int i;
		
		if (_asme instanceof AvatarStartState) {
			return makeBehaviourFromElement(_block, _asme.getNext(0), false);
		}
		
		if (_asme instanceof AvatarTransition) {
			AvatarTransition at = (AvatarTransition)_asme;
			
			if (at.isGuarded()) {
				String g = modifyGuard(at.getGuard());
				
				ret += "if (!" + g + ") {" + CR;
				if (debug) {
					ret += "debug2Msg(__myname, \"Guard failed: " + g + "\");" + CR;
				}
				ret += "__currentState = STATE__STOP__STATE;" + CR; 
				ret += "break;" + CR;
				ret += "}" + CR;
			}
			
			if (at.hasDelay()) {
				ret+= "waitFor(" + reworkDelay(at.getMinDelay()) + ", " + reworkDelay(at.getMaxDelay()) + ");" + CR;
			}
			
			String act;
			for(i=0; i<at.getNbOfAction(); i++) {
				// Must know whether this is an action or a method call
				act = at.getAction(i);
				if (at.isAMethodCall(act)) {
					ret +=  modifyMethodName(_block, act) + ";" + CR;
				} else {
					ret +=  act + ";" + CR;
				}
			}
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
					return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
				}
				
				// Complex case of states -> several nexts
				// Put in list all 
				
				
				// 1) Only immediatly executable transitions
				for(i=0; i<_asme.nbOfNexts(); i++) {
					if (_asme.getNext(i) instanceof AvatarTransition) {
						AvatarTransition at = (AvatarTransition)(_asme.getNext(i));
						
						if (at.hasActions()) {
							ret += makeImmediateAction(at, i);
						} else {
							if (at.getNext(0) instanceof AvatarActionOnSignal) {
								ret += makeSignalAction(at, i);
							} else {
								// nothing special to do : immediate choice
								ret += makeImmediateAction(at, i);
							}
						}
					}
				}
				
				// Make all requests
				// Test if at least one request in the list!
				ret += "if (nbOfRequests(&__list) == 0) {" + CR;
				ret += "debug2Msg(__myname, \"No possible request\");" + CR;
				ret += "__currentState = STATE__STOP__STATE;" + CR; 
				ret += "break;" + CR;
				ret += "}" + CR;
				
				ret += "__returnRequest = executeListOfRequests(&__list);" + CR;
				ret += "clearListOfRequests(&__list);" + CR ;
				ret += traceRequest();
				
				// Resulting requests
				for(i=0; i<_asme.nbOfNexts(); i++) {
					if (i != 0) {
						ret += "else ";
					}
					AvatarTransition at = (AvatarTransition)(_asme.getNext(i));
					if (at.hasActions()) {
						ret += " if (__returnRequest == &__req" + i + ") {" + CR;
						for(int j=0; j<at.getNbOfAction(); j++) {
							ret += at.getAction(j) + ";" + CR;
						}
						ret += makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
					}  else {
						if (at.getNext(0) instanceof AvatarActionOnSignal) {
							ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0).getNext(0), false) + CR + "}";
						} else {
							// nothing special to do : immediate choice
							ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
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
			AvatarRandom ar = (AvatarRandom)_asme;
			ret += ar.getVariable() + " = computeRandom(" + ar.getMinValue() + ", " + ar.getMaxValue() + ");" + CR;
			return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
		}
		
		if (_asme instanceof AvatarActionOnSignal) {
			AvatarActionOnSignal aaos = (AvatarActionOnSignal)_asme;
			ret += makeSignalAction(aaos, 0, false, "", "");
			AvatarSignal as = aaos.getSignal();
			AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
			ret += executeOneRequest("__req0");
			ret += traceRequest();
		}
		
		// Default
		return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
	}
	
	private String makeSignalAction(AvatarTransition _at, int _index) {
		String ret = "";
		AvatarActionOnSignal aaos;
		
		if (!(_at.getNext(0) instanceof AvatarActionOnSignal)) {
			return "";
		}
		
		aaos = (AvatarActionOnSignal)(_at.getNext(0));
		
		if (_at.isGuarded()) {
			String g = modifyGuard(_at.getGuard());
			ret += "if (" + g + ") {" + CR;
		}
		
		if (_at.hasDelay()) {
			ret += makeSignalAction(aaos, _index, true, _at.getMinDelay(), _at.getMaxDelay());
		} else {
			ret += makeSignalAction(aaos, _index, false, "", "");
		}
		ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;
		
		if (_at.isGuarded()) {
			ret += "}" + CR;
		}
		
		return ret;
	}
	
	private String makeSignalAction(AvatarActionOnSignal _aaos, int _index, boolean hasDelay, String minDelay, String maxDelay) {
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
				for(i=0; i<_aaos.getNbOfValues() ;i++) {
					ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
				}
				if (ar.isAsynchronous()) {
					ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", SEND_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
					ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
				} else {
					ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID()+ ", SEND_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
					ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
				}
				
				// Receiving
			} else {
				for(i=0; i<_aaos.getNbOfValues() ;i++) {
					ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
				}
				if (ar.isAsynchronous()) {
					ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
					ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
				} else {
					ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
					ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
				}
			}
		}
		
		return ret;
	}
	
	private String makeImmediateAction(AvatarTransition _at, int _index) {
		String ret = "";
		if (_at.isGuarded()) {
			String g = modifyGuard(_at.getGuard());
			ret += "if (" + g + ") {" + CR;
		}
		
		if (_at.hasDelay()) {
			ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 1, " + reworkDelay(_at.getMinDelay()) + ", " + reworkDelay(_at.getMaxDelay()) + ", 0, __params" + _index + ");" + CR;
		} else {
			ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 0, 0, 0, 0, __params" + _index + ");" + CR;
		}
		ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;
		if (_at.isGuarded()) {
			ret += "}" + CR;
		}
		
		return ret;
		
	}
	
	private String executeOneRequest(String var) {
		String ret = "__returnRequest = executeOneRequest(&__list, &" + var + ");" + CR;
		ret += "clearListOfRequests(&__list);" + CR;
		return ret;
	}
	
	
	public String makeAttributesDeclaration(AvatarBlock _block, TaskFile _taskFile) {
		String ret = "";
		for(AvatarAttribute aa: _block.getAttributes()) {
			ret += getCTypeOf(aa) + " " + aa.getName() + " = " + aa.getInitialValue() + ";" + CR;
		}
		return ret;
	}
	
	public void makeThreadsInMain(boolean _debug) {
		mainFile.appendToMainCode(CR + "/* Threads of tasks */" + CR);  
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToMainCode("pthread_t thread__" + taskFile.getName() + ";" + CR);
		}
		
		if (_debug) {
			mainFile.appendToMainCode("/* Activating debug messages */" + CR); 
			mainFile.appendToMainCode("activeDebug();" + CR);  
		}
		
		if (tracing) {
			mainFile.appendToMainCode("/* Activating tracing  */" + CR); 
			mainFile.appendToMainCode("activeTracingInFile(\"trace.txt\");" + CR);  
		}
		
		mainFile.appendToMainCode("/* Activating randomness */" + CR); 
		mainFile.appendToMainCode("initRandom();" + CR); 
		
		mainFile.appendToMainCode("/* Initializing the main mutex */" + CR);
		mainFile.appendToMainCode("if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}" + CR + CR);
		
		
		mainFile.appendToMainCode(CR + CR + mainDebugMsg("Starting tasks"));
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToMainCode("pthread_create(&thread__" + taskFile.getName() + ", NULL, mainFunc__" + taskFile.getName() + ", (void *)\"" + taskFile.getName() + "\");" + CR);
		}
		
		mainFile.appendToMainCode(CR + CR + mainDebugMsg("Joining tasks"));
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToMainCode("pthread_join(thread__" + taskFile.getName() + ", NULL);" + CR);
		}
		
		mainFile.appendToMainCode(CR + CR + mainDebugMsg("Application terminated"));
		mainFile.appendToMainCode("return 0;" + CR);
	}
	
	public void makeMakefileSrc(String _path) {
		makefile_src = "SRCS = ";
		makefile_src += _path + "main.c ";
		for(TaskFile taskFile: taskFiles) {
			makefile_src += _path + taskFile.getName() + ".c ";
		}
		
	}
	
	
	public String getCTypeOf(AvatarAttribute _aa) {
		String ret = "int";
		if (_aa.getType() == AvatarType.BOOLEAN) {
			ret = "bool";
		}
		return ret;
	}
	
	public String getChannelName(AvatarRelation _ar, int _index) {
		return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
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
		return g;
	}
	
	public String reworkDelay(String _delay) {
		
		switch(timeUnit) {
		case USEC:
			return _delay;
		case MSEC:
			return "(" + _delay + ")*1000";
		case SEC:
			return "(" + _delay + ")*1000000";
		}
		
		return _delay;
	}
	
	private String modifyMethodName(AvatarBlock _ab, String _call) {
		int index;
		String ret0 = "";
		
		index = _call.indexOf("=");
		
		
		
		if (index > -1) {
			ret0 = _call.substring(0, index+1);
			_call = _call.substring(index+2, _call.length());
		}
		
		return ret0 + _ab.getName() + "__" + _call.trim();
	}
	
	private String traceRequest() {
		if (!tracing) {
			return "";
		}
		return "traceRequest(__myname, __returnRequest);" + CR;
	}
	
	private String traceFunctionCall(String blockName, String functionName) {
		if (!tracing) {
			return "";
		}
		return "traceFunctionCall(\"" + blockName + "\", \"" + functionName + "\");" + CR;
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
				
	
}
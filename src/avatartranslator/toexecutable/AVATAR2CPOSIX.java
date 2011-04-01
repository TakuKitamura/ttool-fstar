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

	private final static String GENERATED_PATH = "generated_src" + File.separator;
	private final static String UNKNOWN = "UNKNOWN";
	private final static String CR = "\n";
	
	private AvatarSpecification avspec;
	
	private Vector warnings;
	
	private MainFile mainFile;
	private Vector<TaskFile> taskFiles;
	private String makefile_src;
	

	public AVATAR2CPOSIX(AvatarSpecification _avspec) {
		avspec = _avspec;
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
	

	
	public void generateCPOSIX(boolean _debug) {
		mainFile = new MainFile("main");
		taskFiles = new Vector<TaskFile>();
		
		makeMainMutex();
	
		makeSynchronousChannels();
		
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
			ret += " " + am.getName() + "(";
			list = am.getListOfAttributes();
			int cpt = 0;
			for(AvatarAttribute aa: list) {
				if (cpt != 0) {
					ret += ", ";
				}
				ret += getCTypeOf(aa) + " " + aa.getName();
				cpt ++;
			}
			
			ret += ") {" + CR + "debugMsg(\"-> ....() Entering method " + am.getName() + "\");" + CR;
			
			list = am.getListOfAttributes();
			cpt = 0;
			for(AvatarAttribute aa: list) {
				ret += "debugInt(\"Attribute " + aa.getName() + " = \"," + aa.getName() + ");" + CR;
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
		for(i=0; i<_block.getMaxNbOfMultipleBranches(); i++) {
			s+= "request __req" + i + ";" + CR;
			s+= "int *__params0[" + nbOfMaxParams + "];" + CR;
		}
		
		s+= "pthread_cond_t __myCond;" + CR;
		s+= "request *__returnRequest;" + CR;
		
		s+= CR + "char * __myname = (char *)arg;" + CR;
		
		s+= "printf(\"my name = %s\\n\", __myname);" + CR;
		
		s+= CR + "/* Main loop on states */" + CR;
		s+= "while(__currentState != STATE__STOP__STATE) {" + CR;
		
		s += "switch(__currentState) {" + CR;
		
		// Making start state
		AvatarStateMachine asm = _block.getStateMachine();
		s += "case STATE__START__STATE: " + CR + makeBehaviourFromElement(_block, asm.getStartState(), true);
		s += "break;" + CR + CR;
		
		// Making other states
		for(AvatarStateMachineElement asme: asm.getListOfElements()) {
			if (asme instanceof AvatarState) {
				s += "case STATE__" + asme.getName() + ": " + CR + makeBehaviourFromElement(_block, asme, true);
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
				ret += "debugMsg(\"Guard failed: " + g + "\");" + CR;
				ret += "__currentState = STATE__STOP__STATE;" + CR; 
				ret += "break;" + CR;
				ret += "}" + CR;
			}
			
			for(i=0; i<at.getNbOfAction(); i++) {
				ret += at.getAction(i) + ";" + CR;
			}
			return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
		}
	
		if (_asme instanceof AvatarState) {
			if (!firstCall) {
				ret += "debugMsg(\"-> (=====) Entering state + " + _asme.getName() + "\");" + CR;
				return ret + "__currentState = STATE__" + _asme.getName() + ";" + CR; 
			} else {
				if (_asme.nbOfNexts() == 0) {
					return ret + "__currentState = STATE__STOP__STATE;" + CR; 
				}
				
				if (_asme.nbOfNexts() == 1) {
					return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
				}
				
				// Complex case of states -> several nexts
				// 1) Only immediatly executable transitions
				
				
				
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
			AvatarSignal as = aaos.getSignal();
			AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
			
			if (ar != null) {
				
				// Sending
				if (aaos.isSending()) {
					// Putting params
					for(i=0; i<aaos.getNbOfValues() ;i++) {
						ret += "__params0[" + i + "] = &" +  aaos.getValue(i) + ";" + CR;
					}
					if (ar.isAsynchronous()) {
						ret += "makeNewRequest(&__req0, SEND_ASYNC_REQUEST, 0, 0, 0, " + aaos.getNbOfValues() + ", __params0);" + CR;
						ret += "__req0.asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
					} else {
						ret += "makeNewRequest(&__req0, SEND_SYNC_REQUEST, 0, 0, 0, " + aaos.getNbOfValues() + ", __params0);" + CR;
						ret += "__req0.syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
					}
					ret += executeOneRequest("__req0");
					
				// Receiving
				} else {
					for(i=0; i<aaos.getNbOfValues() ;i++) {
						ret += "__params0[" + i + "] = &" +  aaos.getValue(i) + ";" + CR;
					}
					if (ar.isAsynchronous()) {
						ret += "makeNewRequest(&__req0, RECEIVE_ASYNC_REQUEST, 0, 0, 0, " + aaos.getNbOfValues() + ", __params0);" + CR;
						ret += "__req0.asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
					} else {
						ret += "makeNewRequest(&__req0, RECEIVE_SYNC_REQUEST, 0, 0, 0, " + aaos.getNbOfValues() + ", __params0);" + CR;
						ret += "__req0.syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
					}
					ret += executeOneRequest("__req0");
				}
			}
		}
		
		// Default
		return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
	}
	
	private String executeOneRequest(String var) {
		String ret = "__returnRequest = executeOneRequest(&" + var + ", &__myCond, &__mainMutex);" + CR; 
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
		
		mainFile.appendToMainCode(CR + CR + "debugMsg(\"Starting tasks\");" + CR);
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToMainCode("pthread_create(&thread__" + taskFile.getName() + ", NULL, mainFunc__" + taskFile.getName() + ", (void *)\"" + taskFile.getName() + "\");" + CR);
		}
		
		mainFile.appendToMainCode(CR + CR + "debugMsg(\"Joining tasks\");" + CR);
		for(TaskFile taskFile: taskFiles) {
			mainFile.appendToMainCode("pthread_join(thread__" + taskFile.getName() + ", NULL);" + CR);
		}
		
		mainFile.appendToMainCode(CR + CR + "debugMsg(\"Application terminated\");" + CR);
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
				
	
}
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
 * Class MappedSystemCTask
 * Creation: 24/11/2005
 * @version 1.0 24/11/2005
 * @author Daniel Knorreck
 * @see
 */

package tmltranslator.tomappingsystemc2;

import java.util.*;
import java.util.regex.*;

import tmltranslator.*;
import myutil.*;


public class MappedSystemCTask {
	//private TMLModeling tmlm;
	private TMLTask task;
 	private String reference, cppcode, hcode, initCommand, functions, functionSig, chaining, firstCommand, commentText, idsMergedCmds;
	private ArrayList<TMLChannel> channels;
	private ArrayList<TMLEvent> events;
	private ArrayList<TMLRequest> requests;
	private HashMap<Integer,HashSet<Integer> > dependencies;
	private ArrayList<LiveVariableNode> liveNodes;
	private final static Pattern varPattern = Pattern.compile("[\\w&&\\D]+[\\w]*");
	private TMLMapping tmlmapping;
	private int commentNum;
	private boolean debug;
	private boolean optimize;
	
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	
	
	public MappedSystemCTask(TMLTask _task, ArrayList<TMLChannel> _channels, ArrayList<TMLEvent> _events, ArrayList<TMLRequest> _requests, TMLMapping _tmlmapping) {
        	task = _task;
		channels = _channels;
		events = _events;
		requests = _requests;
		tmlmapping=_tmlmapping;
        	reference = task.getName();
		cppcode = "";
		hcode = "";
		initCommand="";
		functions="";
		chaining="";
		firstCommand="";
		functionSig="";
		commentText="";
		idsMergedCmds="";
		commentNum=0;
		optimize=false;
    	}
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
	public TMLTask getTMLTask() {
		return task;
	}
    
	public void generateSystemC(boolean _debug, boolean _optimize, HashMap<Integer,HashSet<Integer> > _dependencies) {
        	debug = _debug;
		optimize=_optimize;
		basicCPPCode();
		makeClassCode();
		dependencies=_dependencies;
		//analyzeDependencies(task.getActivityDiagram().getFirst(),false,false,new HashSet<Integer>());
		liveVariableAnalysis();
    	}
	
	public void print() {
		System.out.println("task: " + reference + DOTH + hcode);
		System.out.println("task: " + reference + DOTCPP + cppcode);
	}
	
	
	public String getCPPCode() {
		return cppcode;
	}
	
	public String getHCode() {
		return hcode;
	}
	
	public String getReference() {
		return reference;
	}
	
	// H-Code
	private String basicHCode() {
		String code="";
		code += "#ifndef " + reference.toUpperCase() + "__H" + CR;
		code += "#define " + reference.toUpperCase() + "__H" + CR2;
		code += "#include <TMLTask.h>\n#include <definitions.h>\n\n";
		code += "#include <TMLbrbwChannel.h>\n#include <TMLbrnbwChannel.h>\n#include <TMLnbrnbwChannel.h>\n\n";
		code += "#include <TMLEventBChannel.h>\n#include <TMLEventFChannel.h>\n#include <TMLEventFBChannel.h>\n\n";
		code += "#include <TMLActionCommand.h>\n#include <TMLChoiceCommand.h>\n#include <TMLExeciCommand.h>\n";
		code += "#include <TMLSelectCommand.h>\n#include <TMLReadCommand.h>\n#include <TMLNotifiedCommand.h>\n";
		code += "#include <TMLRequestCommand.h>\n#include <TMLSendCommand.h>\n#include <TMLWaitCommand.h>\n";
		code += "#include <TMLWriteCommand.h>\n#include <TMLStopCommand.h>\n#include<TMLWriteMultCommand.h>\n\n";
		code += "extern \"C\" bool condFunc(TMLTask* _ioTask_);\n";
		return code;
	}
	
	private void classHCode() {
	}
	
	// CPP Code
	private void basicCPPCode() {
		cppcode += "#include <" + reference + DOTH + ">" + CR2;
	}
	
	private void makeClassCode(){
		makeHeaderClassH();
		makeEndClassH();
		
		cppcode+=reference+ "::" + makeConstructorSignature()+":TMLTask(iID, iPriority,iName,iCPUs,iNumOfCPUs)"+ CR + makeAttributesCode();
		cppcode+=initCommand + CR + "{" + CR; 
		if (commentNum!=0) cppcode+= "_comment = new std::string[" + commentNum + "]" + SCCR + commentText + CR;
		cppcode+= "//generate task variable look-up table"+ CR;
		for(TMLAttribute att: task.getAttributes()) {
			//att = (TMLAttribute)(iterator.next());
			//code += TMLType.getStringType(att.type.getType()) + " " + att.name;
			cppcode += "_varLookUpName[\"" + att.name + "\"]=&" + att.name +SCCR;
			cppcode += "_varLookUpID[" + att.getID() + "]=&" + att.name +SCCR;
		}		
		cppcode += "_varLookUpName[\"rnd__0\"]=&rnd__0" + SCCR + CR;
		cppcode+= "//set blocked read task/set blocked write task"+ CR;
		for(TMLChannel ch: channels) {
			if (ch.getOriginTask()==task)
				cppcode+=ch.getExtendedName() + "->setBlockedWriteTask(this)"+SCCR;
			else
				cppcode+=ch.getExtendedName() + "->setBlockedReadTask(this)"+SCCR;
		}
		for(TMLEvent evt: events) {
			if (evt.getOriginTask()==task)
				cppcode+=evt.getExtendedName() + "->setBlockedWriteTask(this)"+SCCR;
			else
				cppcode+=evt.getExtendedName() + "->setBlockedReadTask(this)"+SCCR;
		}
		if (task.isRequested()) cppcode+="requestChannel->setBlockedReadTask(this)" +SCCR;
		for(TMLRequest req: requests) {
			if (req.isAnOriginTask(task)) cppcode+=req.getExtendedName() + "->setBlockedWriteTask(this)" +SCCR;
		}
		cppcode+=CR + "//command chaining"+ CR;
		cppcode+= chaining + "_currCommand=" + firstCommand + SCCR + "_firstCommand=" + firstCommand +SCCR + CR; 
		//if (!firstCommand.equals("0")) cppcode+= "_currCommand->prepare()"+SCCR;
		cppcode+="//IDs of merged commands\n" + idsMergedCmds + CR;
		cppcode+="}"+ CR2 + functions; // + makeDestructor();
		hcode = Conversion.indentString(hcode, 4);
		cppcode = Conversion.indentString(cppcode, 4);
	}
	
	private String makeDestructor(){
		String dest=reference + "::~" + reference + "(){" + CR;
		if (commentNum!=0) dest+="delete[] _comment" + SCCR;
		return dest+"}"+CR;
	}

	private String makeConstructorSignature(){
		String constSig=reference+ "(ID iID, Priority iPriority, std::string iName, CPU** iCPUs, unsigned int iNumOfCPUs"+CR;
		for(TMLChannel ch: channels) {
			constSig+=", TMLChannel* "+ ch.getExtendedName() + CR;
		}
		for(TMLEvent evt: events) {
			constSig+=", TMLEventChannel* "+ evt.getExtendedName() +CR;
		}
		for(TMLRequest req: requests) {
			if (req.isAnOriginTask(task)) constSig+=", TMLEventBChannel* " + req.getExtendedName() + CR;
		}
		if (task.isRequested()){
			constSig+=", TMLEventBChannel* requestChannel"+CR;
		}
		return constSig+")";
	}

	private void makeHeaderClassH() {
		String hcodeBegin="";
		// Common dec
		hcodeBegin = "class " + reference + ": public TMLTask {" + CR;
		hcodeBegin += "private:" + CR;
		
		// Attributes
		hcodeBegin += "// Attributes" + CR;
		//hcodeBegin += makeAttributesDeclaration() + CR;
		
		if (task.isRequested()) {
			int params = task.getRequest().getNbOfParams();
			firstCommand="_waitOnRequest";
			hcode+="TMLWaitCommand " + firstCommand + SCCR;
			initCommand+= "," + firstCommand + "(" + task.getActivityDiagram().getFirst().getID() + ",this,requestChannel,"; 
			if (params==0){
				initCommand+= "0)" + CR;
			}else{
				initCommand+= "(ParamFuncPointer)&" + reference + "::" + "waitOnRequest_func)" + CR;
				functionSig+="void waitOnRequest_func(Parameter<ParamType>& ioParam)" + SCCR;
				functions+="void " + reference + "::waitOnRequest_func(Parameter<ParamType>& ioParam){" + CR;
				functions+="arg1__req=ioParam.getP1()" + SCCR;
				if (params>1) functions+= "arg2__req=ioParam.getP2()" + SCCR;
				if (params>2) functions+= "arg3__req=ioParam.getP3()" + SCCR;
				functions+="}\n\n"; 
			}	
			String xx = firstCommand + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(task.getActivityDiagram().getFirst(),false,"&"+firstCommand,null,null) + "))"+ SCCR;
			firstCommand="&"+firstCommand;
			chaining+=xx;
		}else{
			firstCommand=makeCommands(task.getActivityDiagram().getFirst(),false,"0",null,null);
		}

		hcode = basicHCode() + hcodeBegin + makeAttributesDeclaration() + CR + hcode;
		// public dec
		hcode += CR + functionSig + CR + "public:" + CR;
		hcode += "friend bool condFunc(TMLTask* _ioTask_);\n";
		// Simulation
		hcode += makeConstructorSignature() + SCCR; // + "~" + reference + "()" + SCCR;
		makeSerializableFuncs();
	}

	private void makeSerializableFuncs(){
		//ListIterator iterator = task.getAttributes().listIterator();
		//TMLAttribute att;
		hcode += "std::istream& readObject(std::istream& i_stream_var)" + SCCR;
		hcode += "std::ostream& writeObject(std::ostream& i_stream_var)" + SCCR;
		//hcode += "unsigned long getStateHash() const" + SCCR;
		functions+= "std::istream& " + reference + "::readObject(std::istream& i_stream_var){\nTMLTask::readObject(i_stream_var);\n";
		//while(iterator.hasNext()) {
		for (TMLAttribute att:task.getAttributes()){
			//att = (TMLAttribute)(iterator.next());
			functions += "READ_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "#ifdef DEBUG_SERIALIZE\n";
			functions += "std::cout << \"Read: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
			functions += "#endif\n";
		}
		functions+= "return i_stream_var;\n}\n\n";
		functions+= "std::ostream& " + reference + "::writeObject(std::ostream& i_stream_var){\nTMLTask::writeObject(i_stream_var);\n";
		//iterator = task.getAttributes().listIterator();
		for (TMLAttribute att:task.getAttributes()){
		//while(iterator.hasNext()) {
			//att = (TMLAttribute)(iterator.next());
			functions += "WRITE_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "#ifdef DEBUG_SERIALIZE\n";
			functions += "std::cout << \"Write: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
			functions += "#endif\n";
		}
		functions+= "return i_stream_var;\n}\n\n";
		hcode += "void reset()" + SCCR;
		functions+= "void "+reference + "::reset(){\nTMLTask::reset();\n";
		//iterator = task.getAttributes().listIterator();
		//while(iterator.hasNext()) {
		//	att = (TMLAttribute)(iterator.next());
		for (TMLAttribute att:task.getAttributes()){
			functions += att.name + "=";
			if (att.hasInitialValue())
				functions += att.initialValue + SCCR;
			else
				functions += "0" + SCCR;
		}
		functions+= "}\n\n";
		//functions+= "unsigned long " + reference + "::getStateHash() const{\nunsigned long aHash=0;\n";
		//for (TMLAttribute att:task.getAttributes()){
			//if (!(att.name.startsWith("arg") && att.name.endsWith("__req"))) functions += "aHash+=" + att.name + ";\n";
		//}
		functions+=makeStateSavingRoutineLocal();
		/*for(TMLChannel ch: channels) {
			if (ch.getType()!=TMLChannel.NBRNBW &&  ch.getOriginTask()==task)
				functions+="aHash+=" + ch.getExtendedName() + "->getStateHash()"+SCCR;
		}
		for(TMLEvent evt: events) {
			if (evt.getOriginTask()==task)
				functions+="aHash+=" + evt.getExtendedName() + "->getStateHash()"+SCCR;
		}
		for(TMLRequest req: requests) {
			if (req.isAnOriginTask(task))
				functions+="aHash+=" +  req.getExtendedName() + "->getStateHash()"+SCCR;
		}
		functions+= "if (_currCommand!=0) aHash+= _currCommand->getStateHash();\nreturn aHash;\n}\n\n";*/
	}

	private String makeCommands(TMLActivityElement currElem, boolean skip, String retElement, MergedCmdStr nextCommandCont, String retElseElement){
		String nextCommand="",cmdName="";

		if (skip) return makeCommands(currElem.getNextElement(0), false,retElement,nextCommandCont,null);

		if (currElem==null){
			if (debug) System.out.println("Checking null\n");
			return retElement;
		}
		
		if (debug) System.out.println("Checking " + currElem.getName() + CR);

		if (currElem instanceof TMLStartState) {
			if (debug) System.out.println("Checking Start\n");
			return makeCommands(currElem.getNextElement(0), false,retElement,nextCommandCont,null);
		
		} else if (currElem instanceof TMLStopState){
			//add stop state if (retElement.equals("0"))
			if (debug) System.out.println("Checking Stop\n");
			if (retElement.equals("0")){
				cmdName= "_stop" + currElem.getID();
				hcode+="TMLStopCommand " + cmdName + SCCR;
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this)" + CR;
			}else
				return retElement;
		
		} else if (currElem instanceof TMLActionState || currElem instanceof TMLRandom || currElem instanceof TMLDelay){
			String action,comment;
			if (currElem instanceof TMLActionState){				
				if (debug) System.out.println("Checking Action\n");
				action = ((TMLActionState)currElem).getAction();
				comment=action;
				//if (action==null) System.out.println("No action!!!!\n");
			}else if(currElem instanceof TMLRandom){
				if (debug) System.out.println("Checking Random\n");
				TMLRandom random = (TMLRandom)currElem;
				action = random.getVariable() + "=myrand("+ random.getMinValue() + "," + random.getMaxValue() + ")";
				comment=action;	
			}else{
				if (debug) System.out.println("Checking Delay\n");
				int masterClockFreq = tmlmapping.getTMLArchitecture().getMasterClockFrequency();
				TMLDelay delay=(TMLDelay)currElem;
				action = "TMLTime tmpDelayxy = " + delay.getMaxDelay() + "*" + masterClockFreq + delay.getMasterClockFactor() + ";";
				comment=action;
				action+= "\nif (tmpDelayxy==0) tmpDelayxy=1;\n";
				if (delay.getMinDelay().equals(delay.getMaxDelay())){
					action += "_endLastTransaction+=tmpDelayxy";
				}else{
					action += "TMLTime tmpDelayxx = " + delay.getMinDelay() + "*" + masterClockFreq + delay.getMasterClockFactor() + ";\nif (tmpDelayxx==0) tmpDelayxx=1;\n";
					action += "_endLastTransaction+=myrand(tmpDelayxx,tmpDelayxy)";
				}
			}
			//cmdName= "_action" + currElem.getID();
			String elemName=currElem.getName(), idString;
			if (elemName.charAt(0)=='#'){
				int pos=elemName.indexOf('\\');
				idString=elemName.substring(1,pos);
				System.out.println(elemName + "***" + pos + "***" + idString + "***"+ elemName.length());
				cmdName="_" + elemName.substring(pos+1) + idString;
			}else{
				cmdName= "_action" + currElem.getID();
				idString=String.valueOf(currElem.getID());
			}
			if (nextCommandCont==null){
				//System.out.println("nextCommandCont==null in ActionState "+ action +CR);
				//hcode+="TMLActionCommand " + cmdName + SCCR;
				hcode+="TMLChoiceCommand " + cmdName + SCCR;
				MergedCmdStr nextCommandCollection = new MergedCmdStr("", cmdName);
				initCommand+= "," + cmdName + "("+ idString + ",this,(CondFuncPointer)&" + reference + "::" + cmdName + "_func, 1, false)"+CR;
				String MKResult;
				if (optimize)
					MKResult = makeCommands(currElem.getNextElement(0),false,retElement,nextCommandCollection,null);
				else
					MKResult = makeCommands(currElem.getNextElement(0),false,retElement,null,null);
				if(nextCommandCollection.num==0){
					nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + MKResult + "));\n";
				}else{	
					nextCommand= cmdName + ".setNextCommand(array(" + nextCommandCollection.num + nextCommandCollection.nextCmd + "));\n";
				}
				//functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + modifyString(addSemicolonIfNecessary(action)) + CR + nextCommandCollection.funcs + "}" + CR2;
				functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + modifyString(addSemicolonIfNecessary(action)) + CR + nextCommandCollection.funcs;
				if (nextCommandCollection.num==0) functions+="return 0"+ SCCR;
				functions+= "}" + CR2;
				//commentText+="_comment[" + commentNum + "]=std::string(\"Action " + action + "\");\n";
				commentText+="_comment[" + commentNum + "]=std::string(\"Action " + comment + "\");\n";
				commentNum++;
				functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
			}else{
				//System.out.println("nextCommandCont!=null in ActionState "+ action +CR);
				//idsMergedCmds += "_commandHash[" + currElem.getID() + "]=&" + nextCommandCont.srcCmd + SCCR;
				idsMergedCmds += "_commandHash[" + idString + "]=&" + nextCommandCont.srcCmd + SCCR;
				nextCommandCont.funcs += "#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + modifyString(addSemicolonIfNecessary(action)) + CR;
				commentText+="_comment[" + commentNum + "]=std::string(\"Action " + comment + "\");\n";
				commentNum++;
				return makeCommands(currElem.getNextElement(0),false,retElement,nextCommandCont,null);
			}
		
		} else if (currElem instanceof TMLExecI){
			if (debug) System.out.println("Checking Execi\n");
			cmdName= "_execi" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecI)currElem).getAction() + ",0,0)"+CR;
			if (isIntValue(((TMLExecI)currElem).getAction()))
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this,0,0," + ((TMLExecI)currElem).getAction() + ")" + CR;
			else
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this," + makeCommandLenFunc(cmdName, ((TMLExecI)currElem).getAction(), null) + ",0)" + CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

		} else if (currElem instanceof TMLExecC){
			if (debug) System.out.println("Checking ExecC\n");
			cmdName= "_execc" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			if (isIntValue(((TMLExecC)currElem).getAction()))
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this,0,1," + ((TMLExecC)currElem).getAction() + ")" + CR;
			else
				initCommand+= "," + cmdName + "("+ currElem.getID() + ",this,"+ makeCommandLenFunc(cmdName, ((TMLExecC)currElem).getAction(), null) + ",1)"+CR;
				
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLExecIInterval){
			if (debug) System.out.println("Checking ExeciInterv\n");
			cmdName= "_execi" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecIInterval)currElem).getMinDelay()+ "," + ((TMLExecIInterval)currElem).getMaxDelay() + ",0)"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,"+ makeCommandLenFunc(cmdName, ((TMLExecIInterval)currElem).getMinDelay(), ((TMLExecIInterval)currElem).getMaxDelay()) + ",0)"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

		} else if (currElem instanceof TMLExecCInterval){
			if (debug) System.out.println("Checking ExecCInterv\n");
			cmdName= "_execc" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecIInterval)currElem).getMinDelay()+ "," + ((TMLExecIInterval)currElem).getMaxDelay() + ",1)"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,"+ makeCommandLenFunc(cmdName, ((TMLExecCInterval)currElem).getMinDelay(), ((TMLExecCInterval)currElem).getMaxDelay()) + ",1)"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

					
		} else if (currElem instanceof TMLForLoop){
			//makeCommands(TMLActivityElement currElem, boolean skip, String retElement, MergedCmdStr nextCommandCont, String retElseElement)
			if (debug) System.out.println("Checking Loop\n");
			TMLForLoop fl = (TMLForLoop)currElem;
			if (fl.getCondition().isEmpty() || fl.getCondition().trim().toUpperCase().equals("TRUE")){
				//initAction.addNext(fl.getNextElement(0)); //inside loop
				TMLActionState incAction=new TMLActionState("#"+ fl.getID() + "\\lpIncAc",null);
				incAction.setAction(fl.getIncrement());
				String firstCmdInLoop= makeCommands(fl.getNextElement(0), false, "&_lpIncAc" + fl.getID(), null, null);
				makeCommands(incAction, false, firstCmdInLoop, null, null);
				if (fl.getInit().isEmpty()){
					return firstCmdInLoop;
				}else{
					TMLActionState initAction=new TMLActionState("lpInitAc",null);
					initAction.setAction(fl.getInit());
					return makeCommands(initAction, false, firstCmdInLoop, nextCommandCont, null);
				}
				
			}else{
				/*TMLActionState initAction=new TMLActionState("lpInitAc",null);
				initAction.setAction(fl.getInit());
				TMLActionState incAction=new TMLActionState("#"+ fl.getID() + "\\lpIncAc",null);
				incAction.setAction(fl.getIncrement());
				TMLChoice lpChoice=new TMLChoice("#"+ fl.getID() + "\\lpChoice",null);
				if (fl.getCondition().isEmpty())
					lpChoice.addGuard("[ true ]");
				else
					lpChoice.addGuard("[ " + fl.getCondition() + " ]");
				lpChoice.addGuard("[ else ]");
				lpChoice.addNext(fl.getNextElement(0));  //inside loop
				lpChoice.addNext(fl.getNextElement(1));  //after loop           cmdName= "_choice" + currElem.getID();
				makeCommands(incAction, false, "&_lpChoice" + fl.getID(), null, null);
				makeCommands(lpChoice, false, "&_lpIncAc" + fl.getID(), null, retElement);
				return makeCommands(initAction, false, "&_lpChoice" + fl.getID(), nextCommandCont, null);*/
				
				TMLChoice lpChoice=new TMLChoice("#"+ fl.getID() + "\\lpChoice",null);
				//if (fl.getCondition().isEmpty())
				//	lpChoice.addGuard("[ true ]");
				//else
				lpChoice.addGuard("[ " + fl.getCondition() + " ]");
				lpChoice.addGuard("[ else ]");
				lpChoice.addNext(fl.getNextElement(0));  //inside loop
				lpChoice.addNext(fl.getNextElement(1));  //after loop           cmdName= "_choice" + currElem.getID();
				if (fl.getIncrement().isEmpty()){
					makeCommands(lpChoice, false, "&_lpChoice" + fl.getID(), null, retElement);
				}else{
					TMLActionState incAction=new TMLActionState("#"+ fl.getID() + "\\lpIncAc",null);
					incAction.setAction(fl.getIncrement());
					makeCommands(incAction, false, "&_lpChoice" + fl.getID(), null, null);
					makeCommands(lpChoice, false, "&_lpIncAc" + fl.getID(), null, retElement);
				}
				if (fl.getInit().isEmpty()){
					return "&_lpChoice" + fl.getID();
				}else{
					TMLActionState initAction=new TMLActionState("lpInitAc",null);
					initAction.setAction(fl.getInit());
					return makeCommands(initAction, false, "&_lpChoice" + fl.getID(), nextCommandCont, null);
				}
			}
		
		} else if (currElem instanceof TMLReadChannel){
			if (debug) System.out.println("Checking Read\n");
			cmdName= "_read" + currElem.getID();
			hcode+="TMLReadCommand " + cmdName + SCCR;
			TMLReadChannel rCommand=(TMLReadChannel)currElem;
			if (isIntValue(rCommand.getNbOfSamples()))
				//initCommand+= "," + cmdName + "("+currElem.getID()+",this,0," + rCommand.getChannel(0).getExtendedName() + "," + rCommand.getChannel(0).getSize() + "*" + rCommand.getNbOfSamples() + ")"+CR;
				initCommand+= "," + cmdName + "("+currElem.getID()+",this,0," + rCommand.getChannel(0).getExtendedName() + "," +  rCommand.getNbOfSamples() + ")"+CR;

			else
				//initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, rCommand.getChannel(0).getSize() + "*(" + rCommand.getNbOfSamples()+")",null) + "," + rCommand.getChannel(0).getExtendedName() + ")"+CR;
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, rCommand.getNbOfSamples(),null) + "," + rCommand.getChannel(0).getExtendedName() + ")"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLWriteChannel){
			String channels;
			TMLWriteChannel wCommand=(TMLWriteChannel)currElem;
			if (wCommand.getNbOfChannels()>1){
				if (ChannelMappedOnSameHW(wCommand)){
					if (debug) System.out.println("Checking WriteMult with multicast\n");
					cmdName= "_mwrite" + currElem.getID();
					hcode+="TMLWriteMultCommand " + cmdName + SCCR;
					channels="array("+wCommand.getNbOfChannels();
					for(int i=0; i<wCommand.getNbOfChannels(); i++){
						channels+="," + wCommand.getChannel(i).getExtendedName();
					}
					channels+=")," + wCommand.getNbOfChannels();
				}else{
					if (debug) System.out.println("Checking WriteMult with unicast\n");
					TMLWriteChannel prevWrite = null, firstWrite=null;
					for(int i=0; i<wCommand.getNbOfChannels(); i++){
						TMLWriteChannel newWrite =  new TMLWriteChannel("WriteMult",null);
						if(i==0) firstWrite=newWrite;
						newWrite.addChannel(wCommand.getChannel(i));
						newWrite.setNbOfSamples(wCommand.getNbOfSamples());
						if (prevWrite!=null) prevWrite.addNext(newWrite);
						prevWrite=newWrite;
					}
					prevWrite.addNext(wCommand.getNextElement(0));
					return makeCommands(firstWrite, false, retElement, null, null);
				}
			}else{
				cmdName= "_write" + currElem.getID();
				hcode+="TMLWriteCommand " + cmdName + SCCR;
				channels=wCommand.getChannel(0).getExtendedName();
			}
			if (isIntValue(wCommand.getNbOfSamples()))
				initCommand+= "," + cmdName + "("+currElem.getID()+",this,0," + channels + "," +  wCommand.getNbOfSamples() + ")"+CR;
			else
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, wCommand.getNbOfSamples(), null) + "," + channels + ")"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLSendEvent){
			if (debug) System.out.println("Checking Send\n");
			TMLSendEvent sendEvt=(TMLSendEvent)currElem;
			cmdName= "_send" + currElem.getID();
			hcode+="TMLSendCommand " + cmdName + SCCR;
			handleParameters(sendEvt.getNbOfParams(), new String[]{sendEvt.getParam(0), sendEvt.getParam(1), sendEvt.getParam(2)}, cmdName, currElem.getID(), sendEvt.getEvent().getExtendedName(),false);
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
			
		} else if (currElem instanceof TMLSendRequest){
			if (debug) System.out.println("Checking Request\n");
			TMLSendRequest sendReq=(TMLSendRequest)currElem;
			cmdName= "_request" + currElem.getID();
			hcode+="TMLRequestCommand " + cmdName + SCCR;
			handleParameters(sendReq.getNbOfParams(), new String[]{sendReq.getParam(0), sendReq.getParam(1), sendReq.getParam(2)}, cmdName, currElem.getID(), sendReq.getRequest().getExtendedName(),false);
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;	
	
		} else if (currElem instanceof TMLWaitEvent){
			if (debug) System.out.println("Checking Wait\n");
			TMLWaitEvent waitEvt = (TMLWaitEvent)currElem;
			cmdName= "_wait" + currElem.getID();
			hcode+="TMLWaitCommand " + cmdName + SCCR;
			handleParameters(waitEvt.getNbOfParams(), new String[]{waitEvt.getParam(0), waitEvt.getParam(1), waitEvt.getParam(2)}, cmdName, currElem.getID(), waitEvt.getEvent().getExtendedName(),true);
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLNotifiedEvent){
			if (debug) System.out.println("Checking Notified\n");
			cmdName= "_notified" + currElem.getID();
			hcode+="TMLNotifiedCommand " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLNotifiedEvent)currElem).getEvent().getExtendedName() + ",(TMLLength*)&" + ((TMLNotifiedEvent)currElem).getVariable() +",\"" + ((TMLNotifiedEvent)currElem).getVariable() + "\")" + CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR; 
		
		} else if (currElem instanceof TMLSequence){
			TMLSequence tmlseq = (TMLSequence)currElem;
			if (debug) System.out.println("Checking Sequence with "+ tmlseq.getNbNext()+ " elements.");
			if (tmlseq.getNbNext() == 0) {
				//if (lastSequence!=null) return makeCommands(lastSequence, false,retElement,nextCommandCont,functionCont,null);
                		return retElement;
            		} else {
                		if (tmlseq.getNbNext() == 1) {
                    			return makeCommands(currElem.getNextElement(0), false,retElement,nextCommandCont,null);
                		} else {			
					String nextBranch;
					tmlseq.sortNexts();
					if (debug) System.out.println("Checking Sequence branch "+ (tmlseq.getNbNext()-1));
					nextBranch= makeCommands(currElem.getNextElement(currElem.getNbNext() - 1),false,retElement,null,null);
					for(int i=currElem.getNbNext() - 2; i>=0; i--) {
						if (debug) System.out.println("Checking Sequence branch "+ i);
						nextBranch=makeCommands(currElem.getNextElement(i),false,nextBranch,null,null);
					}
                    			return nextBranch;
                		}
            		}
		
		} else if (currElem instanceof TMLChoice){
			int returnIndex=0;
			String elemName=currElem.getName(), idString;
			if (elemName.charAt(0)=='#'){
				int pos=elemName.indexOf('\\');
				idString=elemName.substring(1,pos);
				System.out.println(elemName + "***" + pos + "***" + idString + "***"+ elemName.length());
				cmdName="_" + elemName.substring(pos+1) + idString;
			}else{
				cmdName= "_choice" + currElem.getID();
				idString=String.valueOf(currElem.getID());
			}
			TMLChoice choice = (TMLChoice)currElem;
			String code = "", nextCommandTemp="", MCResult="";
			boolean moreThanTwoGuards=false; 
			if (debug) System.out.println("Checking Choice\n");
			if (choice.getNbGuard() !=0 ) {
				String guardS = "",code2;
				int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
				int nb = choice.nbOfNonDeterministicGuard();
				int nbS = choice.nbOfStochasticGuard();
				if ((nb > 0) || (nbS > 0)){
					code += "rnd__0 = myrand(0, 99)" + SCCR;
				}else{
					if (choice.getNbGuard()>1 && !(choice.getNbGuard()==2 && choice.getElseGuard()!=-1)){
						moreThanTwoGuards=true;
						code+="rnd__0 = 0" + SCCR;
						for(int i=0; i<choice.getNbGuard(); i++) {
							if (i!=index1 && i!=index2)
								code+="if (" + choice.getGuard(i) + ") rnd__0++" + SCCR;
						}
						code = Conversion.replaceAllChar(code, '[', "(");
						code = Conversion.replaceAllChar(code, ']', ")");
					}
				}
				nb = 0;
				for(int i=0; i<choice.getNbGuard(); i++) {
					if (choice.isNonDeterministicGuard(i)) {
						if (i==choice.getNbGuard()-1)
							code2 = "(true)";
						else
							code2 = "(rnd__0 < " + Math.floor(100/choice.getNbGuard())*(nb+1) + ")";
						nb ++;
					} else if (choice.isStochasticGuard(i)) {
						if (guardS.isEmpty()) {
							guardS = choice.getStochasticGuard(i);
						} else {
							guardS = "(" + guardS + ")+(" + choice.getStochasticGuard(i) + ")";
						}
						code2 = "(rnd__0 < (" + guardS + "))";
								nbS ++;
					} else {
						code2 = choice.getGuard(i);
						if (!moreThanTwoGuards || i+1==choice.getNbGuard() || i==index1)
							code2 = choice.getGuard(i);
						else						
							code2 = "(" + choice.getGuard(i) + "&& myrand(1,rnd__0)==1)";
						code2 = Conversion.replaceAllChar(code2, '[', "(");
						code2 = Conversion.replaceAllChar(code2, ']', ")");
					}
					//System.out.println("guard = " + code1 + " i=" + i);
					if (i != index2) {
						if (i==0) {
							code += "if " + code2;
						} else {
							code += " else ";
							if (i != index1) {
								code += "if " + code2;
							}
						}
						if (nextCommandCont==null){
							MergedCmdStr nextCommandCollection=null;
							if (optimize) nextCommandCollection = new MergedCmdStr("",cmdName, returnIndex);
							//System.out.println("Call makeCommands, task: "+reference);
							//if (nextCommandCollection==null) System.out.println("Choice: nextCommandCollection==0");
							if (retElseElement!=null && i==index1)
								//else case
								MCResult = makeCommands(currElem.getNextElement(i), false, retElseElement,nextCommandCollection,null);
							else
								MCResult = makeCommands(currElem.getNextElement(i), false, retElement,nextCommandCollection,null);
							if (!optimize || nextCommandCollection.funcs.isEmpty()){
								//System.out.println("NO content has been added to "+ code2);
								code += "{\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\nreturn " + returnIndex + SCCR +"}" + CR;
								commentText+="_comment[" + commentNum + "]=std::string(\"Branch taken: " + code2 + "\");\n";
								commentNum++;
								//System.out.println("RETURN, aaINC NUM "+ returnIndex);
								returnIndex++;
								nextCommandTemp+= ",(TMLCommand*)" + MCResult;
							}else{
								//System.out.println("OLD RETURN INDEX "+ returnIndex);
								returnIndex = nextCommandCollection.num;
								//System.out.println("Returned index: "+ returnIndex);
								//System.out.println("Choice: Content has been added to "+ code2);
								code += "{\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + nextCommandCollection.funcs + "return " + returnIndex + ";\n}" + CR;
								commentText+="_comment[" + commentNum + "]=std::string(\"Branch taken: " + code2 + "\");\n";
								commentNum++;
								//returnIndex = nextCommandCollection.num;
								//System.out.println("RETURN, bbINC NUM "+ returnIndex);
								returnIndex++;
								nextCommandTemp+= nextCommandCollection.nextCmd+ ",(TMLCommand*)" + MCResult;
							}
						}else{
							idsMergedCmds += "_commandHash[" + currElem.getID() + "]=&" + nextCommandCont.srcCmd + SCCR;
							//System.out.println("Choice: Next command!=0 "+ code2);
							int oldReturnIndex=nextCommandCont.num;
							nextCommandCont.funcs += code + "{\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n";
							commentText+="_comment[" + commentNum + "]=std::string(\"Branch taken: " + code2 + "\");\n";
							commentNum++;
							if (retElseElement!=null && i==index1)
								MCResult = makeCommands(currElem.getNextElement(i), false, retElseElement, nextCommandCont,null);
							else
								MCResult = makeCommands(currElem.getNextElement(i), false, retElement, nextCommandCont,null);
							if (oldReturnIndex==nextCommandCont.num){
								//System.out.println("RETURN, ccINC NUM "+ nextCommandCont.num);
								nextCommandCont.funcs+= "return " + nextCommandCont.num + SCCR;
								nextCommandCont.num++;
								nextCommandCont.nextCmd += ",(TMLCommand*)" + MCResult;
							}
							nextCommandCont.funcs+= "}\n";
							code="";
						}
					} 
				}
				// If there was no else, do a terminate
				if (nextCommandCont==null){
					//System.out.println("Choice: finalization, add new command\n");
					if (index1 == -1){
						code += "#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\nreturn " + returnIndex + SCCR;
						commentText+="_comment[" + commentNum + "]=std::string(\"Exit branch taken\");\n";
						commentNum++;
						nextCommand= cmdName + ".setNextCommand(array(" + (returnIndex+1) + nextCommandTemp + ",(TMLCommand*)0))" + SCCR;
					}else{
						nextCommand= cmdName + ".setNextCommand(array(" + returnIndex + nextCommandTemp + "))" + SCCR;
					}
					hcode+="TMLChoiceCommand " + cmdName + SCCR;
					initCommand+= "," + cmdName + "("+ idString +",this,(CondFuncPointer)&" + reference + "::" + cmdName + "_func," + choice.getNbGuard();
					if (choice.nbOfNonDeterministicGuard()==0) initCommand+=",false)"+CR; else initCommand+=",true)"+CR;
					functions+="unsigned int "+ reference + "::" + cmdName + "_func(){" + CR + code +CR+ "}" + CR2;
					functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
				}else{
					//System.out.println("Choice: finalization, No new command\n");
					if (index1 == -1){
						nextCommandCont.funcs += "#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\nreturn " + nextCommandCont.num + SCCR;
						commentText+="_comment[" + commentNum + "]=std::string(\"Exit branch taken\");\n";
						commentNum++;
						nextCommandCont.nextCmd += ",(TMLCommand*)0";
						//System.out.println("RETURN, ddINC NUM "+ nextCommandCont.num);
						nextCommandCont.num++;
					}
					cmdName=MCResult;
				}
			}
					
		} else if (currElem instanceof TMLSelectEvt){
			TMLEvent evt;
			//Integer nbevt=0;
			int nbevt=0;
			String evtList="",paramList="";
			if (debug) System.out.println("Checking SelectEvt\n");
			cmdName= "_select" + currElem.getID();
			for(int i=0; i<currElem.getNbNext(); i++) {
				evt=((TMLSelectEvt)currElem).getEvent(i);
				if (evt!=null){
					nbevt++;	
					evtList += ",(TMLEventChannel*)"+ evt.getExtendedName();
					if (evt.getNbOfParams()==0) {
						paramList+=",(ParamFuncPointer)0";
					}else{

						functionSig+="void " + cmdName + "_func_" + i + "(Parameter<ParamType>& ioParam)" + SCCR;
						functions+="void " + reference + "::" + cmdName +  "_func_" + i + "(Parameter<ParamType>& ioParam){" + CR;
						paramList+=",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func_" + i + CR;
						functions += "ioParam.getP(";
						for(int j=0; j<3; j++) {
							if (j>0) functions += ",";
							//if (((TMLSelectEvt)currElem).getParam(i,j) != null) {
								//if (!((TMLSelectEvt)currElem).getParam(i,j).isEmpty()) {
									//paramList += ((TMLSelectEvt)currElem).getParam(i,j);
									//functions += ((TMLSelectEvt)currElem).getParam(i,j) + "=ioParam.getP" + (j+1) +"()"+ SCCR;
								//}
							if (((TMLSelectEvt)currElem).getParam(i,j)== null || ((TMLSelectEvt)currElem).getParam(i,j).isEmpty())
								functions+="rnd__0";
							else
								functions+=((TMLSelectEvt)currElem).getParam(i,j);
						}
						functions+=");\n}\n\n"; 
					}
					nextCommand+= ",(TMLCommand*)" + makeCommands(currElem.getNextElement(i), true, retElement,null,null); 
				}
			}
			hcode+="TMLSelectCommand " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,array("+ nbevt + evtList + "),"+ nbevt + ",array("+ nbevt + paramList + "))"+CR;
			nextCommand=cmdName + ".setNextCommand(array(" + nbevt + nextCommand + "))" + SCCR;
		
		} else {
			System.out.println("Operator: " + currElem + " of class " + currElem.getClass().getName() +  " is not managed in the current version of this C++ code generator.");
		}
		chaining+=nextCommand; 
		return (cmdName.equals("0") || cmdName.charAt(0)=='&')? cmdName : "&"+cmdName;
	}

	private boolean ChannelMappedOnSameHW(TMLWriteChannel writeCmd){
		LinkedList<HwCommunicationNode> commNodeRefList = tmlmapping.findNodesForElement(writeCmd.getChannel(0));
		for(int i=1; i<writeCmd.getNbOfChannels(); i++){
			LinkedList<HwCommunicationNode> commNodeCmpList = tmlmapping.findNodesForElement(writeCmd.getChannel(i));
			if (commNodeCmpList.size()!=commNodeRefList.size()) return false;
			Iterator it = commNodeCmpList.iterator(); 
			for(HwCommunicationNode cmnode: commNodeRefList) {
				if (it.next()!=cmnode) return false;
			}			
		}
		return true;
	}
	
	
	private String makeCommandLenFunc(String cmdName, String lowerLimit, String upperLimit){
		if (upperLimit==null)
			functions+="TMLLength "+ reference + "::" + cmdName + "_func(){\nreturn (TMLLength)(" + lowerLimit + ");\n}" + CR2;
		else
			functions+="TMLLength "+ reference + "::" + cmdName + "_func(){\nreturn (TMLLength)myrand(" + lowerLimit + "," + upperLimit + ");\n}" + CR2;
		functionSig+="TMLLength " + cmdName + "_func()" + SCCR;
		return "(LengthFuncPointer)&" + reference + "::" + cmdName + "_func";
	}

	private boolean isIntValue(String input){  
		try{  
			Integer.parseInt(input); 
			return true;  
		}catch(Exception e){  
			return false;
		}  
	}

	private void handleParameters(int nbOfParams, String[] param, String cmdName, int ID, String channelName, boolean wait){
		String params="";
		boolean areStatic=true;
		if (nbOfParams==0){
			initCommand+= "," + cmdName + "("+ ID +",this," + channelName + ",0)"+CR;
			return;
		}
		for(int i=0; i<3; i++){
			if(areStatic && !isIntValue(param[i])) areStatic=false;
			if (i>0) params+=",";
			if (param[i]==null || param[i].isEmpty()) params+="rnd__0"; else params+=param[i];
		}
		if (areStatic){
			initCommand+= "," + cmdName + "("+ ID +",this," + channelName + ",0,Parameter<ParamType>("+ params + "))"+CR;
		}else{
			initCommand+= "," + cmdName + "("+ ID +",this," + channelName + ",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func)" + CR;
			functionSig+="void " + cmdName + "_func(Parameter<ParamType>& ioParam)" + SCCR;
			functions+="void " + reference + "::" + cmdName +  "_func(Parameter<ParamType>& ioParam){" + CR;
			if (wait)
				functions += "ioParam.getP(" + params + ")" + SCCR;
			else
				functions += "ioParam.setP(" + params + ")" + SCCR;
			functions+="}\n\n"; 
		}
	}

	private void makeEndClassH() {
		hcode += "};" + CR + "#endif" + CR;
	}

	
	private String makeAttributesCode() {
		String code = "";
		//TMLAttribute att;
		int i;
		//istIterator iterator = task.getAttributes().listIterator();
		//while(iterator.hasNext()) {
		for(TMLAttribute att: task.getAttributes()) {
			//att = (TMLAttribute)(iterator.next());
			if (att.hasInitialValue())
				code += ","+ att.name + "(" + att.initialValue + ")"+CR;
			else
				code += ","+ att.name + "(0)"+CR;
		}	
		return code;
	}
	
	private String makeAttributesDeclaration() {
		String code = "";
		//TMLAttribute att;
		int i;
		//ListIterator iterator = task.getAttributes().listIterator();
		for(TMLAttribute att: task.getAttributes()) {
		//while(iterator.hasNext()) {
			//att = (TMLAttribute)(iterator.next());
			//code += TMLType.getStringType(att.type.getType()) + " " + att.name;
			code += "ParamType " + att.name;
			code += ";\n";
		}		
		code += "ParamType rnd__0" + SCCR;
		//code += "int arg1__req" + SCCR;
		//code += "int arg2__req" + SCCR;
		//code += "int arg3__req" + SCCR;
		return code;
	}
	

	private String addSemicolonIfNecessary(String _input) {
		String code1 = _input.trim();
		if (!(code1.endsWith(";"))) {
		code1 += ";";
		}
		return code1;
	}

	private String modifyString(String _input) {
		_input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
		_input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
        	return _input;
	}

	/*private void analyzeDependencies(TMLActivityElement currElem, boolean oneCmd, boolean skip, HashSet<Integer> depList){
		boolean done;
		do{
			if (skip){
				skip=false;
			}else{
				if (currElem instanceof TMLActionState) {
					String[] tokens=((TMLActionState)currElem).getAction().split("=",2);
					if (tokens.length==2){
						ArrayList<Integer> leftVars = getVariablesInString(tokens[0]);
						ArrayList<Integer> rightVars = getVariablesInString(tokens[1]);
						if (!(rightVars.isEmpty() && depList.isEmpty())){
							for (int lVar: leftVars){
								HashSet<Integer> currSet = getHashSetForID(lVar);
								for(int rVar: rightVars) currSet.add(rVar);
								for (int objs: depList) currSet.add(objs);
							}
						}
					}
					
				} else if (currElem instanceof TMLChoice){
					TMLChoice choice= (TMLChoice) currElem;
					if (choice.getNbGuard()>1){		
						if (choice.isNonDeterministicGuard(0) || choice.isStochasticGuard(0)) {
							//Random choice, add reference to choice command to nDepList
							depList.add(-currElem.getID());
						}else{
							//Add all variables that appear in any condition to NDepList
							for(int i=0; i<choice.getNbGuard(); i++) {
								ArrayList<Integer> guardVars = getVariablesInString(choice.getGuard(i));
								for (int gVar: guardVars) depList.add(gVar);
							}
						}
						//HashSet<Integer> tmpDepList = new HashSet<Integer>(nesDepList);
						for (int branch=0; branch<currElem.getNbNext(); branch++){
							//if(branch!=0) nesDepList=new HashSet<Integer>(tmpDepList);
							analyzeDependencies(currElem.getNextElement(branch),false,false,new HashSet<Integer>(depList));	
						}
					}
				} else if (currElem instanceof TMLWriteChannel){
					TMLWriteChannel write = (TMLWriteChannel) currElem;
					for (int i=0; i< write.getNbOfChannels(); i++){
						if (write.getChannel(i).getType()!=TMLChannel.NBRNBW){
							//Add to dependency list of event: NDepList + variable denoting the number of samples
							ArrayList<Integer> cmdVars = getVariablesInString(write.getNbOfSamples());
							if (!(depList.isEmpty() && cmdVars.isEmpty())){
								HashSet<Integer> currSet = getHashSetForID(write.getChannel(i).getID());
								for (int objs: depList) currSet.add(objs);
								for (int objs: cmdVars) currSet.add(objs);
							}
						}
					}
	
				} else if (currElem instanceof TMLSendEvent){
					TMLSendEvent send = (TMLSendEvent) currElem;
					//Add to dependency list of event: NDepList + variables appearing as parameters
					if (!depList.isEmpty()){
						HashSet<Integer> currSet = getHashSetForID(send.getEvent().getID());
						for (int objs: depList) currSet.add(objs);
					}
					for (int param=0; param<send.getNbOfParams(); param++){
						///?????????????????????
						ArrayList<Integer> paramVars = getVariablesInString(send.getParam(param));
						HashSet<Integer> paramSet = getHashSetForID(Integer.MAX_VALUE-3*send.getEvent().getID()-param); //NEW
						for (int objs: depList) paramSet.add(objs);  //NEW
						if (!paramVars.isEmpty()){
							//HashSet<Integer> paramSet = getHashSetForID(Integer.MAX_VALUE-3*send.getEvent().getID()-param); //NEW
							for (int objs: paramVars) paramSet.add(objs);
						}
					}
	
				} else if (currElem instanceof TMLSendRequest){
					//Add to dependency list of request: NDepList + variables appearing as parameters
					TMLSendRequest request = (TMLSendRequest) currElem;
					if (!depList.isEmpty()){
						HashSet<Integer> currSet = getHashSetForID(request.getRequest().getID());
						for (int objs: depList) currSet.add(objs);
					}
					for (int param=0; param<request.getNbOfParams(); param++){
						///?????????????????????
						ArrayList<Integer> paramVars = getVariablesInString(request.getParam(param));
						if (!paramVars.isEmpty()){
							HashSet<Integer> paramSet = getHashSetForID(Integer.MAX_VALUE-3*request.getRequest().getID()-param);
							for (int objs: paramVars) paramSet.add(objs);
						}
					}
	
				} else if (currElem instanceof TMLReadChannel){
					TMLReadChannel read = (TMLReadChannel) currElem;
					if (read.getChannel(0).getType()!=TMLChannel.NBRNBW){
						//Add to dependency list of channel: NDepList + variable denoting the number of samples
						ArrayList<Integer> cmdVars = getVariablesInString(read.getNbOfSamples());
						if (!(depList.isEmpty() && cmdVars.isEmpty())){
							HashSet<Integer> currSet = getHashSetForID(read.getChannel(0).getID());
							for (int objs: depList) currSet.add(objs);
							for (int objs: cmdVars) currSet.add(objs);
						}
					}
					
				} else if (currElem instanceof TMLWaitEvent){
					TMLWaitEvent wait= (TMLWaitEvent) currElem;
					//Add content of NDepList to dependency list of event, add channel to dependency list of variables appearing as parameters
					if (!depList.isEmpty()){
						HashSet<Integer> currSet = getHashSetForID(wait.getEvent().getID());
						for (int objs: depList) currSet.add(objs);
					}
					for (int param=0; param<wait.getNbOfParams(); param++){
						///?????????????????????
						ArrayList<Integer> paramVars = getVariablesInString(wait.getParam(param));
						for (int pvar: paramVars){
							HashSet<Integer> paramSet = getHashSetForID(pvar);
							//currSet.add(wait.getEvent().getID());
							paramSet.add(Integer.MAX_VALUE-3*wait.getEvent().getID()-param);
						}
					}
	
				} else if (currElem instanceof TMLNotifiedEvent){
					//Add channel/event/request to dependency of variable
					TMLNotifiedEvent notified = (TMLNotifiedEvent) currElem;
					ArrayList<Integer> vars = getVariablesInString(notified.getVariable());
					//int varID = getVarIDByName(notified.getVariable());
					//if (varID!=-1){
					for (int var: vars){
						HashSet<Integer> currSet = getHashSetForID(var);
						currSet.add(notified.getEvent().getID());
						for (int objs: depList) currSet.add(objs);
					}
					
				} else if (currElem instanceof TMLSelectEvt){
					//Execute WaitEvent actions for all connected events
					for (int i=0; i<3; i++){
						TMLEvent evt = ((TMLSelectEvt)currElem).getEvent(i);
						if (evt!=null) depList.add(evt.getID()); //for select event commands
					}
					for(int branch=0; branch<currElem.getNbNext(); branch++)
						analyzeDependencies(currElem.getNextElement(branch),true,false,depList);
					//HashSet<Integer> tmpDepList = new HashSet<Integer>(nesDepList);
					for(int branch=0; branch<currElem.getNbNext(); branch++){
						//if(branch!=0) nesDepList=new HashSet<Integer>(tmpDepList);
						analyzeDependencies(currElem.getNextElement(branch),false,true,new HashSet<Integer>(depList));
					}
						
				} else if (currElem instanceof TMLForLoop){
					TMLForLoop loop = (TMLForLoop) currElem;
					//Check_Branch(next_cmd outside loop)

					//Add all variables to NDepList which appear in loop expressions

					//Check_Branch(next_cmd within loop)
					//HashSet<Integer> tmpDepList = new HashSet<Integer>(nesDepList);
					analyzeDependencies(currElem.getNextElement(1),false,false,new HashSet<Integer>(depList));  //outside loop
					//nesDepList=tmpDepList;
					String[] cmds = new String[]{loop.getInit(), loop.getIncrement(), loop.getCondition()};
					for (int i=0; i<3; i++){
						ArrayList<Integer> cmdVars = getVariablesInString(cmds[i]);
						for (int cmdvar: cmdVars) depList.add(cmdvar);
					}
					analyzeDependencies(currElem.getNextElement(0),false,false,depList);  //inside loop

				} else if (currElem instanceof TMLSequence){
					//pay attention to correct nesDepList
					if (currElem.getNbNext()>1){
						//HashSet<Integer> tmpDepList = new HashSet<Integer>(nesDepList);
						for (int branch=0; branch<currElem.getNbNext(); branch++){
							//if(branch!=0) nesDepList=new HashSet<Integer>(tmpDepList);
							analyzeDependencies(currElem.getNextElement(branch),false,false,new HashSet<Integer>(depList));
						}
					}
					
				} else if (currElem instanceof TMLRandom){
					//Add to dependency list of variable: random + NDepList
					ArrayList<Integer> vars = getVariablesInString(((TMLRandom)currElem).getVariable());
					//int varID = getVarIDByName(((TMLRandom)currElem).getVariable());
					//if (varID!=-1){
					for (int var: vars){
						HashSet<Integer> currSet = getHashSetForID(var);
						for (int objs: depList) currSet.add(objs);
						currSet.add(-currElem.getID());
					}
				}
			}
			done = (oneCmd || currElem.getNbNext()!=1 || currElem instanceof TMLStopState);
			if (!done) currElem = currElem.getNextElement(0);
		}while (!done);
	}


	private ArrayList<Integer> getVariablesInString(String search){
		ArrayList<Integer> variables = new ArrayList<Integer>();
		if (search==null) return variables;
		Matcher matcher = varPattern.matcher(search);
		while (matcher.find()){
			int varID = getVarIDByName(search.substring(matcher.start(), matcher.end()));
			if (varID!=-1) variables.add(varID);
		}
		return variables;
	}

	private int getVarIDByName(String attName){
		if (task.isRequested()) {
			if (attName.equals("arg1__req")){
				return Integer.MAX_VALUE - 3*task.getRequest().getID();
			}else if (attName.equals("arg2__req")){
				return Integer.MAX_VALUE - 3*task.getRequest().getID()-1;
			}else if (attName.equals("arg3__req")){
				return Integer.MAX_VALUE - 3*task.getRequest().getID()-2;
			}
		}
		for(TMLAttribute att: task.getAttributes()) {
			if (att.name.equals(attName)) return att.getID();
		}
		return -1;
	}

	private HashSet<Integer> getHashSetForID(int id){
		HashSet<Integer> currSet = dependencies.get(id);
		if (currSet==null){
			currSet=new HashSet<Integer>();
			dependencies.put(id,currSet);
		}
		return currSet;
	}*/
	
	public String getIdentifierNameByID(int id){
		//Channels, Events, Requests, Variables, Choice, Random
		id=Math.abs(id);
		for (TMLChannel channel: channels){
			if (channel.getID()==id) return channel.getName();
		}
		for (TMLEvent event: events){
			if (event.getID()==id) return event.getName();
			int param = Integer.MAX_VALUE - 3 * event.getID() - id + 1;
			if (param>0 && param<4)  return event.getName() + "_param" + param;
		}
		for (TMLRequest request: requests){
			if (request.getID()==id) return request.getName();
			int param = Integer.MAX_VALUE - 3 * request.getID() - id +1;
			if (param>0 && param<4)  return request.getName() + "_param!" + param;
		}
		for(TMLAttribute att: task.getAttributes()){
			if (att.getID()==id) return reference + ":" + att.getName();
		}
		for(int i=0; i<task.getActivityDiagram().nElements();i++)
			if (task.getActivityDiagram().get(i).getID()==id) return reference + ":Command " + id;
		return null;
	}
	
	public String makeStateSavingRoutineLocal(){
		String aFunc="";
		//std::ostream& TMLTask_0::writeObject(std::ostream& i_stream_var){
		//TMLTask::writeObject(i_stream_var);
		//WRITE_STREAM(i_stream_var,a);
		//#ifdef DEBUG_SERIALIZE
    		/*int aSeqNo=0;
		aFunc += "std::ostream& " + reference + "::streamState(std::ostream& i_stream_var, int* iliveVars){\n";
		for(TMLAttribute att: task.getAttributes()) {
			aFunc += "if ((iLiveVars[" +  (aSeqNo >>> 5) + "]& (1<<" + (aSeqNo & 0x1F) + "))!=0) WRITE_STREAM(i_stream_var," + att.getName() + ");\n";
			aSeqNo++;
		}
		aFunc += "return }";*/
		return aFunc;
	}

	private LiveVariableNode getLiveVarNodeByCommand(TMLActivityElement iCmd){
		for(LiveVariableNode aLiveNode: liveNodes)
			if (aLiveNode.getLinkedElement()==iCmd) return aLiveNode;
		return null;
	}

	private int getVarSeqNoByName(String attName){
		int aSeq=0;
		for(TMLAttribute att: task.getAttributes()) {
			if (att.name.equals(attName)) return aSeq;
			aSeq++;
		}
		return -1;
	}

	private void liveVariableAnalysis(){
		System.out.println("*** Live Variable Analysis for task " + task.getName());
		int aNbOfLiveElements = task.getAttributes().size() + channels.size() + events.size();
		int aNumberOfBytes=0;
		LiveVariableNode.reset();
		liveNodes = new ArrayList<LiveVariableNode>();
		if (task.isRequested()) aNbOfLiveElements++;		
		aNumberOfBytes = aNbOfLiveElements >>> 5;
		if ((aNbOfLiveElements & 0x1F)!=0) aNumberOfBytes++;
		LiveVariableNode aStartNode=null, aLastNode=null;
		for(TMLAttribute att: task.getAttributes()) {
			if (att.hasInitialValue())
				aStartNode = new LiveVariableNode(new int[aNumberOfBytes], parseExprToVariableMap(att.name, aNumberOfBytes, null), null, null,
			false, att.name, att.initialValue);			
			else
				aStartNode = new LiveVariableNode(new int[aNumberOfBytes], parseExprToVariableMap(att.name, aNumberOfBytes, null), null, null,
			false, att.name, "0");
			liveNodes.add(aStartNode);			
			if (aLastNode!=null) aLastNode.setSuccessor(aStartNode);
			aLastNode=aStartNode;
		}
		if (task.isRequested()){
			int[] aReqVars = parseExprToVariableMap("arg1__req", aNumberOfBytes, null);
			parseExprToVariableMap("arg2__req", aNumberOfBytes, aReqVars);
			parseExprToVariableMap("arg3__req", aNumberOfBytes, aReqVars);
			int[] aReqChannelVar = new int[aNumberOfBytes];
			aReqChannelVar[aNumberOfBytes-1] = 1 << ((aNbOfLiveElements-1) & 0x1F);
			//LiveVariableNode aWaitReqNode = new LiveVariableNode(aReqChannelVar, aReqVars, null);
			aStartNode = new LiveVariableNode(aReqChannelVar, aReqVars, null, null, true);
			aStartNode.setInfected(true);
			//liveNodes.add(aWaitReqNode);
			liveNodes.add(aStartNode);
			//aWaitReqNode.setSuccessor(buildLiveAnalysisTree(task.getActivityDiagram().getFirst(), aWaitReqNode, aNumberOfBytes));
			aStartNode.setSuccessor(buildLiveAnalysisTree(task.getActivityDiagram().getFirst(), aStartNode, null, aNumberOfBytes));
		}else{
			aStartNode = buildLiveAnalysisTree(task.getActivityDiagram().getFirst(), null, null, aNumberOfBytes);
		}
		if (aLastNode!=null) aLastNode.setSuccessor(aStartNode);
		for(LiveVariableNode aLiveNode: liveNodes){
			aLiveNode.prepareReachingDefinitions();
		}
		boolean aChange;
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.determineKilledSets();
		}while(aChange);
		for(LiveVariableNode aLiveNode: liveNodes){
			//aLiveNode.printKillEntries();
			aLiveNode.reachingDefinitionsInit();
		}
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.reachingDefinitionAnalysis();
		}while(aChange);
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.determineIfConstant();
		}while(aChange);
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.infectionAnalysis();
		}while(aChange);
		do{
			aChange=false;
			for(LiveVariableNode aLiveNode: liveNodes)
				aChange |= aLiveNode.liveVariableAnalysis();
		}while(aChange);
		for(LiveVariableNode aLiveNode: liveNodes){
			aLiveNode.printReachingEntries();
			printLiveVarNode(aLiveNode);
		}
		System.out.println("*** End of Live Variable Analysis for task " + task.getName());
		
	}

	private int[] parseExprToVariableMap(String iExpr, int iBytesWidth, int[] iMap){
		//byte[] aResMap;
		int[] aResMap;
		if(iMap==null)
			//aResMap=new byte[iBytesWidth];
			aResMap=new int[iBytesWidth];
		else
			aResMap=iMap;
		if(!(iExpr==null || iExpr.isEmpty())){
			//System.out.println("Examine expression: " + iExpr);
			Matcher matcher = varPattern.matcher(iExpr);
			//System.out.print("Found tokens: ");
			while (matcher.find()){
				String token = iExpr.substring(matcher.start(), matcher.end());
				System.out.print(token + ", ");
				int aVarSeqNo = getVarSeqNoByName(token);
				if (aVarSeqNo>=0) aResMap[aVarSeqNo >>> 5] |= 1 << (aVarSeqNo & 0x1F);
			}
			//System.out.println();
			//System.out.println("Byte sequence: ");
		}
		/*for(int i=0; i<iBytesWidth; i++)
			System.out.print(((int)aResMap[i]) + ", ");
		System.out.println();*/
		return aResMap;		
	}

	private int[] parseChannelToVariableMap(TMLChannel iChannel, int iBytesWidth, int[] iMap){
		int[] aResMap;
		if(iMap==null)
			aResMap=new int[iBytesWidth];
		else
			aResMap=iMap;
		int anIndex = task.getAttributes().size() + channels.indexOf(iChannel);
		aResMap[anIndex >>> 5] |= 1 << (anIndex & 0x1F);
		return aResMap;	
	}

	private int[] parseEventToVariableMap(TMLEvent iEvent, int iBytesWidth){
		int[] aResMap = new int[iBytesWidth];
		int anIndex = task.getAttributes().size() + channels.size() + events.indexOf(iEvent);
		aResMap[anIndex >>> 5] |= 1 << (anIndex & 0x1F);
		return aResMap;
	}

	private void printLiveVarNode(LiveVariableNode iNode){
		int aSeq=0;
		//if (iNode.getLinkedElement()==null)
			//System.out.print("Cmd type: waitRequest id: none");
		//else
			//System.out.print("Cmd type:" + iNode.getLinkedElement().getName() + "  id: " + iNode.getLinkedElement().getID());
		//System.out.print(iNode.getStatementDescr());
		//if (iNode.canBeRemoved()) System.out.println(" to be removed"); else  System.out.println();
		for(TMLAttribute att: task.getAttributes()) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(att.getName() + ": significant");
			aSeq++;
		}
		for(TMLChannel ch: channels) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(ch.getName() + ": significant");
			aSeq++;
		}
		for(TMLEvent evt: events) {
			if ((iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
				System.out.println(evt.getName() + ": significant");
			aSeq++;
		}
		if (task.isRequested() && (iNode.getOutVars()[aSeq >>> 5] & (1 << (aSeq & 0x1F)))!=0)
			System.out.println("reqChannel: significant");
	}

	private LiveVariableNode buildLiveAnalysisTree(TMLActivityElement iCurrElem, LiveVariableNode iReturnNode, LiveVariableNode iSuperiorNode, int iBytesWidth){
		LiveVariableNode aResNode=null;
		
		if (iCurrElem instanceof TMLStartState) {
			iCurrElem.getNextElement(0);
		}

		if (iCurrElem==null || iCurrElem instanceof TMLStopState){
			return iReturnNode;

		} else if (iCurrElem instanceof TMLActionState){
			String[] aTokens=((TMLActionState)iCurrElem).getAction().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			aResNode = new LiveVariableNode(parseExprToVariableMap(aTokens[1], iBytesWidth, null), parseExprToVariableMap(aTokens[0], iBytesWidth, null), iCurrElem, iSuperiorNode, 
			false, aTokens[0], aTokens[1]);	

 		} else if (iCurrElem instanceof TMLRandom){
			TMLRandom aRndCmd=(TMLRandom)iCurrElem;
			int[] aRandomVars = parseExprToVariableMap(aRndCmd.getMinValue(), iBytesWidth ,null);
			parseExprToVariableMap(aRndCmd.getMaxValue(), iBytesWidth ,aRandomVars);
			aResNode = new LiveVariableNode(aRandomVars, parseExprToVariableMap(aRndCmd.getVariable(), iBytesWidth, null), iCurrElem, iSuperiorNode, true);
			aResNode.setInfected(true);

		} else if (iCurrElem instanceof TMLDelay){
			int[] aDelayVars = parseExprToVariableMap(((TMLDelay)iCurrElem).getMinDelay(), iBytesWidth, null);
			parseExprToVariableMap(((TMLDelay)iCurrElem).getMaxDelay(), iBytesWidth, aDelayVars);
			//aResNode = new LiveVariableNode(aDelayVars, new byte[iBytesWidth],iCurrElem);
			aResNode = new LiveVariableNode(aDelayVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecI){
			aResNode = new LiveVariableNode(parseExprToVariableMap(((TMLExecI)iCurrElem).getAction(), iBytesWidth, null), new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecC){
			aResNode = new LiveVariableNode(parseExprToVariableMap(((TMLExecC)iCurrElem).getAction(), iBytesWidth, null), new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecIInterval){
			int[] aExecVars = parseExprToVariableMap(((TMLExecIInterval)iCurrElem).getMinDelay(), iBytesWidth, null);
			parseExprToVariableMap(((TMLExecIInterval)iCurrElem).getMaxDelay(), iBytesWidth, aExecVars);
			aResNode = new LiveVariableNode(aExecVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLExecCInterval){
			int[] aExecVars = parseExprToVariableMap(((TMLExecCInterval)iCurrElem).getMinDelay(), iBytesWidth, null);
			parseExprToVariableMap(((TMLExecCInterval)iCurrElem).getMaxDelay(), iBytesWidth, aExecVars);
			aResNode = new LiveVariableNode(aExecVars, new int[iBytesWidth],iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLForLoop){
			TMLForLoop aLoop = (TMLForLoop) iCurrElem;
			String[] aTokens=aLoop.getInit().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			//LiveVariableNode aInit = new LiveVariableNode(parseExprToVariableMap(aTokens[1], iBytesWidth, null,), parseExprToVariableMap(aTokens[0], iBytesWidth, null),iCurrElem);
			LiveVariableNode aInit = new LiveVariableNode(parseExprToVariableMap(aTokens[1], iBytesWidth, null), parseExprToVariableMap(aTokens[0], iBytesWidth,
			null), iCurrElem, iSuperiorNode, false, aTokens[0], aTokens[1]);
			//LiveVariableNode aCondition = new LiveVariableNode(parseExprToVariableMap(aLoop.getCondition(), iBytesWidth, null), new int[iBytesWidth], iCurrElem);
			LiveVariableNode aCondition = new LiveVariableNode(parseExprToVariableMap(aLoop.getCondition(), iBytesWidth, null), new int[iBytesWidth], iCurrElem, iSuperiorNode);
			aTokens=aLoop.getIncrement().split("=",2);
			if (aTokens.length<2) aTokens=new String[2];
			//LiveVariableNode anIncrement = new LiveVariableNode(parseExprToVariableMap(aTokens[1], iBytesWidth, null), parseExprToVariableMap(aTokens[0], iBytesWidth, null),iCurrElem);
			LiveVariableNode anIncrement = new LiveVariableNode(parseExprToVariableMap(aTokens[1], iBytesWidth, null), parseExprToVariableMap(aTokens[0],
			iBytesWidth, null), iCurrElem, aCondition, false, aTokens[0], aTokens[1]);
			aInit.setSuccessor(aCondition);
			anIncrement.setSuccessor(aCondition);
			aCondition.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(0), anIncrement, aCondition, iBytesWidth));   //in loop
			aCondition.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(1), iReturnNode, aCondition, iBytesWidth));   //outside loop
			liveNodes.add(aInit);
			liveNodes.add(aCondition);
			liveNodes.add(anIncrement);
			return aInit;

		} else if (iCurrElem instanceof TMLReadChannel){
			TMLReadChannel aReadCmd = (TMLReadChannel)iCurrElem;
			int[] aReadVars=null;
			if (aReadCmd.getChannel(0).getType()==TMLChannel.BRBW || aReadCmd.getChannel(0).getType()==TMLChannel.BRNBW)
				aReadVars = parseChannelToVariableMap(aReadCmd.getChannel(0), iBytesWidth, null);
			else
				aReadVars = new int[iBytesWidth];
			parseExprToVariableMap(aReadCmd.getNbOfSamples(), iBytesWidth, aReadVars);
			aResNode = new LiveVariableNode(aReadVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLWriteChannel){
			TMLWriteChannel aWriteCmd=(TMLWriteChannel)iCurrElem;
			int[] aWriteVars = new int[iBytesWidth];
			for(int i=0; i< aWriteCmd.getNbOfChannels(); i++){
				if (aWriteCmd.getChannel(i).getType()==TMLChannel.BRBW) parseChannelToVariableMap(aWriteCmd.getChannel(i), iBytesWidth, aWriteVars);
			}
			parseExprToVariableMap(aWriteCmd.getNbOfSamples(), iBytesWidth, aWriteVars);
			aResNode = new LiveVariableNode(aWriteVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);
			
		} else if (iCurrElem instanceof TMLSendEvent){
			TMLSendEvent aSendCmd=(TMLSendEvent)iCurrElem;
			int[] aSendVars=null;
			if (aSendCmd.getEvent().isBlocking())
				aSendVars = parseEventToVariableMap(aSendCmd.getEvent(), iBytesWidth);
			else
				aSendVars = new int[iBytesWidth];
			parseExprToVariableMap(aSendCmd.getParam(0), iBytesWidth, aSendVars);
			parseExprToVariableMap(aSendCmd.getParam(1), iBytesWidth, aSendVars);
			parseExprToVariableMap(aSendCmd.getParam(2), iBytesWidth, aSendVars);
			aResNode = new LiveVariableNode(aSendVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);
		
		} else if (iCurrElem instanceof TMLSendRequest){
			TMLSendRequest aSendReqCmd=(TMLSendRequest)iCurrElem;
			int[] aSendReqVars = parseExprToVariableMap(aSendReqCmd.getParam(0), iBytesWidth, null);
			parseExprToVariableMap(aSendReqCmd.getParam(1), iBytesWidth, aSendReqVars);
			parseExprToVariableMap(aSendReqCmd.getParam(2), iBytesWidth, aSendReqVars);
			aResNode = new LiveVariableNode(aSendReqVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);

		} else if (iCurrElem instanceof TMLWaitEvent){
			TMLWaitEvent aWaitCmd=(TMLWaitEvent)iCurrElem;
			int[] aWaitVars = parseExprToVariableMap(aWaitCmd.getParam(0), iBytesWidth, null);
			parseExprToVariableMap(aWaitCmd.getParam(1), iBytesWidth, aWaitVars);
			parseExprToVariableMap(aWaitCmd.getParam(2), iBytesWidth, aWaitVars);
			aResNode = new LiveVariableNode(parseEventToVariableMap(aWaitCmd.getEvent(), iBytesWidth), aWaitVars, iCurrElem, iSuperiorNode, true);
			aResNode.setInfected(true);
			
		} else if (iCurrElem instanceof TMLNotifiedEvent){
			aResNode = new LiveVariableNode(parseEventToVariableMap(((TMLNotifiedEvent)iCurrElem).getEvent(), iBytesWidth), parseExprToVariableMap(((TMLNotifiedEvent)iCurrElem).getVariable(), iBytesWidth,
			null), iCurrElem, iSuperiorNode, true);
			aResNode.setInfected(true);

		} else if (iCurrElem instanceof TMLSequence){
			((TMLSequence)iCurrElem).sortNexts();
			for(int i=((TMLSequence)iCurrElem).getNbNext()-1; i>=0; i--){
				iReturnNode = buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, iSuperiorNode, iBytesWidth);
			}
			return iReturnNode;
			 
		} else if (iCurrElem instanceof TMLChoice){
			int[] aChoiceVars = new int[iBytesWidth];
			TMLChoice aChoiceCmd = (TMLChoice)iCurrElem;
			for(int i=0; i<aChoiceCmd.getNbGuard(); i++)
				if (!(aChoiceCmd.isNonDeterministicGuard(i) || aChoiceCmd.isStochasticGuard(i) || aChoiceCmd.getElseGuard()==i)) parseExprToVariableMap(aChoiceCmd.getGuard(i), iBytesWidth, aChoiceVars);
			aResNode = new LiveVariableNode(aChoiceVars, new int[iBytesWidth], iCurrElem, iSuperiorNode);
			aResNode.setInfected(aChoiceCmd.nbOfNonDeterministicGuard()>0 || aChoiceCmd.nbOfStochasticGuard()>0);
			System.out.println("checl:  " + aChoiceCmd.nbOfNonDeterministicGuard() + " ** "+ aChoiceCmd.nbOfStochasticGuard());
			for(int i=0; i<aChoiceCmd.getNbNext(); i++)
				aResNode.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, aResNode, iBytesWidth));
			liveNodes.add(aResNode);
			return aResNode;
		
		} else if (iCurrElem instanceof TMLSelectEvt){
			aResNode = new LiveVariableNode(new int[iBytesWidth], new int[iBytesWidth], iCurrElem, iSuperiorNode);
			aResNode.setInfected(true);
			for(int i=0; i<((TMLSelectEvt)iCurrElem).getNbNext(); i++){
				aResNode.setSuccessor(buildLiveAnalysisTree(iCurrElem.getNextElement(i), iReturnNode, aResNode, iBytesWidth));
			}
			liveNodes.add(aResNode);
			return aResNode;
		}

		LiveVariableNode aSucc = buildLiveAnalysisTree(iCurrElem.getNextElement(0), iReturnNode, iSuperiorNode, iBytesWidth);
		if (aResNode==null || aResNode.isEmptyNode()){
			aResNode=aSucc;
		}else{
			liveNodes.add(aResNode);
			aResNode.setSuccessor(aSucc);
		}
		return aResNode;
	}
}



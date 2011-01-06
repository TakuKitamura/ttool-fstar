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
import tmltranslator.*;
import myutil.*;


public class MappedSystemCTask {
	//private TMLModeling tmlm;
	private TMLTask task;
 	private String reference, cppcode, hcode, initCommand, functions, functionSig, chaining, firstCommand, commentText, idsMergedCmds;
	private ArrayList<TMLChannel> channels;
	private ArrayList<TMLEvent> events;
	private ArrayList<TMLRequest> requests;
	private TMLMapping tmlmapping;
	private int commentNum;
	private boolean debug;
	private boolean optimize;
	private StaticAnalysis _analysis;
	private LiveVariableNode _startAnaNode;
	
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
		_analysis = new StaticAnalysis(_task, _channels, _events, _requests);
    	}
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
	public TMLTask getTMLTask() {
		return task;
	}
    
	public void generateSystemC(boolean _debug, boolean _optimize, int[] aStatistics) {
		_startAnaNode = _analysis.startAnalysis();
		_analysis.determineCheckpoints(aStatistics); //NEW
        	debug = _debug;
		optimize=_optimize;
		basicCPPCode();
		makeClassCode();
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
		int aSeq=0;
		for(TMLChannel ch: channels) {
			cppcode+= "_channels[" + aSeq + "] = " + ch.getExtendedName() + SCCR;
			 aSeq++;
		}
		for(TMLEvent evt: events) {
			cppcode+= "_channels[" + aSeq + "] = " + evt.getExtendedName() + SCCR;
			aSeq++;
		}
		if (task.isRequested()){
			cppcode+= "_channels[" + aSeq + "] = requestChannel" + SCCR;
		}
		
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
		hcodeBegin = "class " + reference + ": public TMLTask {" + CR;
		hcodeBegin += "private:" + CR;
		
		hcodeBegin += "// Attributes" + CR;
		
		if (task.isRequested()) {
			int params = task.getRequest().getNbOfParams();
			firstCommand="_waitOnRequest";
			hcode+="TMLWaitCommand " + firstCommand + SCCR;
			initCommand+= "," + firstCommand + "(" + task.getActivityDiagram().getFirst().getID() + ",this,requestChannel,"; 
			if (params==0){
				initCommand+= "0,"+ getFormattedLiveVarStr(_startAnaNode) + ")" + CR;
			}else{
				initCommand+= "(ParamFuncPointer)&" + reference + "::" + "waitOnRequest_func," + getFormattedLiveVarStr(_startAnaNode)  + ")" + CR;
				functionSig+="Parameter<ParamType>* waitOnRequest_func(Parameter<ParamType>* ioParam)" + SCCR;
				functions+="Parameter<ParamType>* " + reference + "::waitOnRequest_func(Parameter<ParamType>* ioParam){" + CR;
				//functions+="arg1__req=ioParam.getP1()" + SCCR;
				//if (params>1) functions+= "arg2__req=ioParam.getP2()" + SCCR;
				//if (params>2) functions+= "arg3__req=ioParam.getP3()" + SCCR;
				functions+= "ioParam->getP(&arg1__req";
				for (int i=1; i<params; i++){
					functions+= ", &arg" + (i+1) + "__req";
				}
				functions+=")" + SCCR + "return 0" + SCCR;
				functions+="}\n\n"; 
			}	
			String xx = firstCommand + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(task.getActivityDiagram().getFirst(),false,"&"+firstCommand,null,null) + "))"+ SCCR;
			firstCommand="&"+firstCommand;
			chaining+=xx;
			/*chaining+= firstCommand + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(task.getActivityDiagram().getFirst(),false,"&"+firstCommand,null,null) + "))"+ SCCR;
			firstCommand = "&" + firstCommand;*/
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
		hcode += "std::istream& readObject(std::istream& i_stream_var)" + SCCR;
		hcode += "std::ostream& writeObject(std::ostream& i_stream_var)" + SCCR;
		//hcode += "unsigned long getStateHash() const" + SCCR;
		functions+= "std::istream& " + reference + "::readObject(std::istream& i_stream_var){\nTMLTask::readObject(i_stream_var);\n";
		for (TMLAttribute att:task.getAttributes()){
			//att = (TMLAttribute)(iterator.next());
			functions += "READ_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "#ifdef DEBUG_SERIALIZE\n";
			functions += "std::cout << \"Read: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
			functions += "#endif\n";
		}
		functions+= "return i_stream_var;\n}\n\n";
		functions+= "std::ostream& " + reference + "::writeObject(std::ostream& i_stream_var){\nTMLTask::writeObject(i_stream_var);\n";
		for (TMLAttribute att:task.getAttributes()){
			functions += "WRITE_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "#ifdef DEBUG_SERIALIZE\n";
			functions += "std::cout << \"Write: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
			functions += "#endif\n";
		}
		functions+= "return i_stream_var;\n}\n\n";
		hcode += "void reset()" + SCCR;
		functions+= "void "+reference + "::reset(){\nTMLTask::reset();\n";
		for (TMLAttribute att:task.getAttributes()){
			functions += att.name + "=";
			if (att.hasInitialValue())
				functions += att.initialValue + SCCR;
			else
				functions += "0" + SCCR;
		}
		functions+= "}\n\n";
		hcode += "void refreshStateHash(const char* iLiveVarList);\n";
		functions+= "void " + reference + "::refreshStateHash(const char* iLiveVarList){\n";
		int aSeq=0;
		functions += "_stateHash.init((HashValueType)this,30);\nif(iLiveVarList!=0){\n";
		for(TMLAttribute att: task.getAttributes()) {
			functions += "if ((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0) _stateHash.addValue(" + att.getName() + ");\n";
			aSeq++;
		}
		int i=0;
		//for channels: include hash only if performed action is blocking
		//for events: include filling level for senders (notified possible), include parameters for readers (if parameters set)
		for(TMLChannel ch: channels) {
			//if (ch.getType()==TMLChannel.BRBW || (ch.getType()==TMLChannel.BRNBW && ch.getDestinationTask()==task)) functions += "if ((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0) _channels[" + i +"]->getStateHash(&_stateHash);\n";
			if (ch.getType()==TMLChannel.BRBW || (ch.getType()==TMLChannel.BRNBW && ch.getDestinationTask()==task)) functions += "_channels[" + i +"]->setSignificance(this, " + "((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0));\n";
			aSeq++; i++;
		}
		for(TMLEvent evt: events) {
			//if (evt.isBlocking() || evt.getDestinationTask()==task) functions += "if ((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0) _channels[" + i +"]->getStateHash(&_stateHash);\n";
			if (evt.isBlocking() || evt.getDestinationTask()==task) functions += " _channels[" + i +"]->setSignificance(this, " + "((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0));\n";
			aSeq++; i++;
		}
		if (task.isRequested()){
			//functions += "if ((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0) _channels[" + i +"]->getStateHash(&_stateHash);\n";
			functions += " _channels[" + i +"]->setSignificance(this, " + "((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0));\n";
		}
		/*for(i=0; i< channels.size() + events.size() + (task.isRequested()? 1:0) ; i++){
			functions += "if ((iLiveVarList[" + (aSeq >>> 3) + "] & " + (1 << (aSeq & 0x7)) + ")!=0) _channels[" + i +"]->getStateHash(iHash);\n";
			aSeq++;
		}*/
		//functions += "_lastStateHash = iHash->getHash();\n";
		//functions += "_commonExecution = false;\n";
		functions += "}\n}\n\n";
		//return anEvalFunc;
		//functions+=_analysis.makeLiveVarEvalFunc()+ "}\n\n";
	}

	private String getFormattedLiveVarStr(TMLActivityElement currElem){
		return getFormattedLiveVarStr(_analysis.getLiveVarNodeByCommand(currElem));
	}
	
	private String getFormattedLiveVarStr(LiveVariableNode currNode){
		if (currNode==null){
			return "0, false";
		}else{
			String checkpoint = (currNode.isCheckpoint())? "true":"false";
			return currNode.getLiveVariableString() + "," + checkpoint;
		}
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
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this,0,0," + ((TMLExecI)currElem).getAction() + "," + getFormattedLiveVarStr(currElem) + ")" + CR;
			else
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this," + makeCommandLenFunc(cmdName, ((TMLExecI)currElem).getAction(), null) + ",0,1," + getFormattedLiveVarStr(currElem) + ")" + CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

		} else if (currElem instanceof TMLExecC){
			if (debug) System.out.println("Checking ExecC\n");
			cmdName= "_execc" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			if (isIntValue(((TMLExecC)currElem).getAction()))
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this,0,1," + ((TMLExecC)currElem).getAction() + "," + getFormattedLiveVarStr(currElem) + ")" + CR;
			else
				initCommand+= "," + cmdName + "("+ currElem.getID() + ",this,"+ makeCommandLenFunc(cmdName, ((TMLExecC)currElem).getAction(), null) + ",1,1," + getFormattedLiveVarStr(currElem) + ")"+CR;
				
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLExecIInterval){
			if (debug) System.out.println("Checking ExeciInterv\n");
			cmdName= "_execi" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecIInterval)currElem).getMinDelay()+ "," + ((TMLExecIInterval)currElem).getMaxDelay() + ",0)"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,"+ makeCommandLenFunc(cmdName, ((TMLExecIInterval)currElem).getMinDelay(), ((TMLExecIInterval)currElem).getMaxDelay()) + ",0,1," + getFormattedLiveVarStr(currElem) + ")" +CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

		} else if (currElem instanceof TMLExecCInterval){
			if (debug) System.out.println("Checking ExecCInterv\n");
			cmdName= "_execc" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecIInterval)currElem).getMinDelay()+ "," + ((TMLExecIInterval)currElem).getMaxDelay() + ",1)"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,"+ makeCommandLenFunc(cmdName, ((TMLExecCInterval)currElem).getMinDelay(), ((TMLExecCInterval)currElem).getMaxDelay()) + ",1,1," + getFormattedLiveVarStr(currElem) + ")"+CR;
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
				initCommand+= "," + cmdName + "("+currElem.getID()+",this,0," + rCommand.getChannel(0).getExtendedName() + "," + getFormattedLiveVarStr(currElem) + "," +  rCommand.getNbOfSamples() + ")"+CR;

			else
				//initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, rCommand.getChannel(0).getSize() + "*(" + rCommand.getNbOfSamples()+")",null) + "," + rCommand.getChannel(0).getExtendedName() + ")"+CR;
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, rCommand.getNbOfSamples(),null) + "," + rCommand.getChannel(0).getExtendedName() + "," + getFormattedLiveVarStr(currElem) + ")"+CR;
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
				initCommand+= "," + cmdName + "("+currElem.getID()+",this,0," + channels + "," +  getFormattedLiveVarStr(currElem) + "," + wCommand.getNbOfSamples() + ")" +CR;
			else
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, wCommand.getNbOfSamples(), null) + "," + channels + "," + getFormattedLiveVarStr(currElem) + ")"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLSendEvent){
			if (debug) System.out.println("Checking Send\n");
			TMLSendEvent sendEvt=(TMLSendEvent)currElem;
			cmdName= "_send" + currElem.getID();
			hcode+="TMLSendCommand " + cmdName + SCCR;
			//handleParameters(sendEvt.getNbOfParams(), new String[]{sendEvt.getParam(0), sendEvt.getParam(1), sendEvt.getParam(2)}, cmdName, currElem.getID(), sendEvt.getEvent().getExtendedName(), false,getFormattedLiveVarStr(currElem));
			handleParameters(currElem, cmdName, false, getFormattedLiveVarStr(currElem));
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
			
		} else if (currElem instanceof TMLSendRequest){
			if (debug) System.out.println("Checking Request\n");
			TMLSendRequest sendReq=(TMLSendRequest)currElem;
			cmdName= "_request" + currElem.getID();
			hcode+="TMLRequestCommand " + cmdName + SCCR;
			//handleParameters(sendReq.getNbOfParams(), new String[]{sendReq.getParam(0), sendReq.getParam(1), sendReq.getParam(2)}, cmdName, currElem.getID(), sendReq.getRequest().getExtendedName(),false, getFormattedLiveVarStr(currElem));
			handleParameters(currElem, cmdName, false, getFormattedLiveVarStr(currElem));
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;	
	
		} else if (currElem instanceof TMLWaitEvent){
			if (debug) System.out.println("Checking Wait\n");
			TMLWaitEvent waitEvt = (TMLWaitEvent)currElem;
			cmdName= "_wait" + currElem.getID();
			hcode+="TMLWaitCommand " + cmdName + SCCR;
			//handleParameters(waitEvt.getNbOfParams(), new String[]{waitEvt.getParam(0), waitEvt.getParam(1), waitEvt.getParam(2)}, cmdName, currElem.getID(), waitEvt.getEvent().getExtendedName(),true, getFormattedLiveVarStr(currElem));
			handleParameters(currElem, cmdName, true, getFormattedLiveVarStr(currElem));
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLNotifiedEvent){
			if (debug) System.out.println("Checking Notified\n");
			cmdName= "_notified" + currElem.getID();
			hcode+="TMLNotifiedCommand " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLNotifiedEvent)currElem).getEvent().getExtendedName() + ",(TMLLength*)&" + ((TMLNotifiedEvent)currElem).getVariable() +",\"" + ((TMLNotifiedEvent)currElem).getVariable() + "\"," + getFormattedLiveVarStr(currElem) + ")" + CR;
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

						functionSig+="Parameter<ParamType>* " + cmdName + "_func_" + i + "(Parameter<ParamType>* ioParam)" + SCCR;
						functions+="Parameter<ParamType>* " + reference + "::" + cmdName +  "_func_" + i + "(Parameter<ParamType>* ioParam){" + CR;
						paramList+=",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func_" + i + CR;
						functions += "ioParam->getP(&" + ((TMLSelectEvt)currElem).getParam(i,0);
						for(int j=1; j<evt.getNbOfParams(); j++) {
						//for(int j=0; j<3; j++) {
							//if (j>0) functions += ",";
							//if (((TMLSelectEvt)currElem).getParam(i,j)== null || ((TMLSelectEvt)currElem).getParam(i,j).isEmpty())
							//	functions+="rnd__0";
							//else
							//	functions+=((TMLSelectEvt)currElem).getParam(i,j);
							functions+= ", &" + ((TMLSelectEvt)currElem).getParam(i,j);
						}
						functions+=");\nreturn 0;\n}\n\n"; 
					}
					nextCommand+= ",(TMLCommand*)" + makeCommands(currElem.getNextElement(i), true, retElement,null,null); 
				}
			}
			hcode+="TMLSelectCommand " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,array("+ nbevt + evtList + "),"+ nbevt + "," + getFormattedLiveVarStr(currElem) + ",array("+ nbevt + paramList + "))"+CR;
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

	private void handleParameters(TMLActivityElement currElem, String cmdName, boolean wait, String liveVarString){
		String concatParams="", channelName="";
		String[] paramArray=null;
		boolean areStatic=true;
		int nbOfParams=0;
		String address=(wait)? "&":"";
		if (currElem instanceof TMLActivityElementEvent){
			nbOfParams=((TMLActivityElementEvent)currElem).getNbOfParams();
			paramArray = new String[nbOfParams];
			for (int i=0; i< nbOfParams; i++){
				paramArray[i]=((TMLActivityElementEvent)currElem).getParam(i);
			}
			channelName = ((TMLActivityElementEvent)currElem).getEvent().getExtendedName();
		}
		else if (currElem instanceof TMLSendRequest){
			nbOfParams=((TMLSendRequest)currElem).getNbOfParams();
			paramArray = new String[nbOfParams];
			for (int i=0; i< nbOfParams; i++){
				paramArray[i]=((TMLSendRequest)currElem).getParam(i);
			}
			channelName = ((TMLSendRequest)currElem).getRequest().getExtendedName();
		}
		if (nbOfParams==0){
			initCommand+= "," + cmdName + "("+ currElem.getID() +",this," + channelName + ",0," + liveVarString +")"+CR;
		}else{
			for(int i=0; i<nbOfParams; i++){
				if(areStatic && !isIntValue(paramArray[i])) areStatic=false;
				if (i>0) concatParams+=",";
				//concatParams+=",";
				if (paramArray[i]==null || paramArray[i].isEmpty()) concatParams+="0"; else concatParams+= address + paramArray[i];
			}
			if (areStatic){
				initCommand+= "," + cmdName + "("+ currElem.getID() +",this," + channelName + ",0," + liveVarString + ",Parameter<ParamType>(" + nbOfParams + "," + concatParams + "))"+CR;
			}else{
				initCommand+= "," + cmdName + "("+ currElem.getID() +",this," + channelName + ",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func,"+ liveVarString + ")" + CR;
				functionSig+="Parameter<ParamType>* " + cmdName + "_func(Parameter<ParamType>* ioParam)" + SCCR;
				functions+="Parameter<ParamType>* " + reference + "::" + cmdName +  "_func(Parameter<ParamType>* ioParam){" + CR;
				if (wait)
					functions += "ioParam->getP(" + concatParams + ")" + SCCR + "return 0" + SCCR;
				else
					//functions += "ioParam.setP(" + concatParams + ")" + SCCR;
					functions += "return new Parameter<ParamType>(" + nbOfParams + "," + concatParams + ")" + SCCR;
				functions+="}\n\n"; 
			}
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
		code += "TMLChannel* _channels[" + (channels.size() + events.size() + (task.isRequested()? 1:0)) + "]" + SCCR;
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
	
	public String getIdentifierNameByID(int id){
		//Channels, Events, Requests, Variables, Choice, Random
		id=Math.abs(id);
		for (TMLChannel channel: channels){
			if (channel.getID()==id) return channel.getName();
		}
		for (TMLEvent event: events){
			if (event.getID()==id) return event.getName();
			/*int param = Integer.MAX_VALUE - 3 * event.getID() - id + 1;
			if (param>0 && param<4)  return event.getName() + "_param" + param;*/
		}
		for (TMLRequest request: requests){
			if (request.getID()==id) return request.getName();
			/*int param = Integer.MAX_VALUE - 3 * request.getID() - id +1;
			if (param>0 && param<4)  return request.getName() + "_param!" + param;*/
		}
		for(TMLAttribute att: task.getAttributes()){
			if (att.getID()==id) return reference + ":" + att.getName();
		}
		for(int i=0; i<task.getActivityDiagram().nElements();i++)
			if (task.getActivityDiagram().get(i).getID()==id) return reference + ":Command " + id;
		return null;
	}
	
	//public void determineCheckpoints(int[] aStatistics){
	//	_analysis.determineCheckpoints(aStatistics);
	//}
}

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
 * @author Ludovic APVRILLE
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
	private int commentNum;
	private boolean debug;
	
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	
	
	public MappedSystemCTask(TMLTask _task, ArrayList<TMLChannel> _channels, ArrayList<TMLEvent> _events, ArrayList<TMLRequest> _requests) {
        	task = _task;
		channels = _channels;
		events = _events;
		requests = _requests;
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
    	}
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
	public TMLTask getTMLTask() {
		return task;
	}
    
	public void generateSystemC(boolean _debug) {
        	debug = _debug;
		//basicHCode();
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
		code += "#include <TMLWriteCommand.h>\n#include <TMLStopCommand.h>\n\n";
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
		
		cppcode+=reference+ "::" + makeConstructorSignature()+":TMLTask(iID, iPriority,iName,iCPU)"+ CR + makeAttributesCode();
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
		String constSig=reference+ "(unsigned int iID, unsigned int iPriority, std::string iName, CPU* iCPU"+CR;
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
		ListIterator iterator = task.getAttributes().listIterator();
		TMLAttribute att;
		hcode += "virtual std::istream& readObject(std::istream& i_stream_var)" + SCCR;
		hcode += "virtual std::ostream& writeObject(std::ostream& i_stream_var)" + SCCR;
		functions+= "std::istream& " + reference + "::readObject(std::istream& i_stream_var){\nTMLTask::readObject(i_stream_var);\n";
		while(iterator.hasNext()) {
			att = (TMLAttribute)(iterator.next());
			functions += "READ_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "std::cout << \"Read: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
		}
		functions+= "return i_stream_var;\n}\n\n";
		functions+= "std::ostream& " + reference + "::writeObject(std::ostream& i_stream_var){\nTMLTask::writeObject(i_stream_var);\n";
		iterator = task.getAttributes().listIterator();
		while(iterator.hasNext()) {
			att = (TMLAttribute)(iterator.next());
			functions += "WRITE_STREAM(i_stream_var," + att.name + ")" + SCCR;
			functions += "std::cout << \"Write: Variable " + att.name + " \" << " + att.name +  " << std::endl" + SCCR;
		}
		functions+= "return i_stream_var;\n}\n\n";
		hcode += "void reset();" + SCCR;
		functions+= "void "+reference + "::reset(){\nTMLTask::reset();\n";
		iterator = task.getAttributes().listIterator();
		while(iterator.hasNext()) {
			att = (TMLAttribute)(iterator.next());
			functions += att.name + "=";
			if (att.hasInitialValue())
				functions += att.initialValue + SCCR;
			else
				functions += "0" + SCCR;
		}
		functions+= "}\n\n";
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
			String action;
			if (currElem instanceof TMLActionState){				
				if (debug) System.out.println("Checking Action\n");
				action = ((TMLActionState)currElem).getAction();
				//if (action==null) System.out.println("No action!!!!\n");
			}else if(currElem instanceof TMLRandom){
				if (debug) System.out.println("Checking Random\n");
				TMLRandom random = (TMLRandom)currElem;
				action = random.getVariable() + "=myrand("+ random.getMinValue() + "," + random.getMaxValue() + ")";
			}else{
				if (debug) System.out.println("Checking Delay\n");
				TMLDelay delay=(TMLDelay)currElem;
				if (delay.getMinDelay()==delay.getMaxDelay())
					action = "_endLastTransaction+=" + delay.getMaxDelay();
				else
					action = "_endLastTransaction+=myrand(" + delay.getMinDelay() + "," + delay.getMaxDelay() + ")";
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
				hcode+="TMLActionCommand " + cmdName + SCCR;
				MergedCmdStr nextCommandCollection = new MergedCmdStr("", cmdName);
				//initCommand+= "," + cmdName + "("+ currElem.getID() + ",this,(ActionFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
				initCommand+= "," + cmdName + "("+ idString + ",this,(ActionFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
				String MKResult = makeCommands(currElem.getNextElement(0),false,retElement,nextCommandCollection,null);
				if(nextCommandCollection.num==0){
					nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + MKResult + "));\n";
				}else{	
					nextCommand= cmdName + ".setNextCommand(array(" + nextCommandCollection.num + nextCommandCollection.nextCmd + "));\n";
				}
				functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + modifyString(addSemicolonIfNecessary(action)) + CR + nextCommandCollection.funcs + "}" + CR2;
				commentText+="_comment[" + commentNum + "]=std::string(\"Action " + action + "\");\n";
				commentNum++;
				functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
			}else{
				//System.out.println("nextCommandCont!=null in ActionState "+ action +CR);
				//idsMergedCmds += "_commandHash[" + currElem.getID() + "]=&" + nextCommandCont.srcCmd + SCCR;
				idsMergedCmds += "_commandHash[" + idString + "]=&" + nextCommandCont.srcCmd + SCCR;
				nextCommandCont.funcs += "#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\n" + modifyString(addSemicolonIfNecessary(action)) + CR;
				commentText+="_comment[" + commentNum + "]=std::string(\"Action " + action + "\");\n";
				commentNum++;
				return makeCommands(currElem.getNextElement(0),false,retElement,nextCommandCont,null);
			}
		
		} else if (currElem instanceof TMLExecI){
			if (debug) System.out.println("Checking Execi\n");
			cmdName= "_execi" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecI)currElem).getAction() + ",0,0)"+CR;
			initCommand+= "," + cmdName + "(" + currElem.getID() + ",this," + makeCommandLenFunc(cmdName, ((TMLExecI)currElem).getAction(), null) + ",0)" + CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

		} else if (currElem instanceof TMLExecC){
			if (debug) System.out.println("Checking ExecC\n");
			cmdName= "_execc" + currElem.getID();
			hcode+="TMLExeciCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,"+ ((TMLExecI)currElem).getAction() + ",1)"+CR;
			initCommand+= "," + cmdName + "("+ currElem.getID() + ",this,"+ makeCommandLenFunc(cmdName, ((TMLExecI)currElem).getAction(), null) + ",1)"+CR;
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
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,"+ makeCommandLenFunc(cmdName, ((TMLExecIInterval)currElem).getMinDelay(), ((TMLExecIInterval)currElem).getMaxDelay()) + ",1)"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;

					
		} else if (currElem instanceof TMLForLoop){
			if (debug) System.out.println("Checking Loop\n");
			TMLForLoop fl = (TMLForLoop)currElem;
			TMLActionState initAction=new TMLActionState("lpInitAc",null);
			//if (fl.getInit()==null) System.out.println("No action in loop init!!!!\n"); else System.out.println(fl.getInit());
			//if (fl.getIncrement()==null) System.out.println("No action in loop inc!!!!\n"); else System.out.println(fl.getIncrement());
			initAction.setAction(fl.getInit());
			//cmdName= "_action" + initAction.getID();
			//TMLActionState incAction=new TMLActionState("lpIncAc",null);
			TMLActionState incAction=new TMLActionState("#"+ fl.getID() + "\\lpIncAc",null);
			incAction.setAction(fl.getIncrement());
			TMLChoice lpChoice=new TMLChoice("#"+ fl.getID() + "\\lpChoice",null);
			lpChoice.addGuard("[ " + fl.getCondition() + " ]");
			lpChoice.addGuard("[ else ]");
			//incAction.addNext(lpChoice);
			lpChoice.addNext(fl.getNextElement(0));  //inside loop
			lpChoice.addNext(fl.getNextElement(1));  //after loop           cmdName= "_choice" + currElem.getID();
			//makeCommands(initAction, false, "&_choice" + lpChoice.getID(), nextCommandCont, functionCont, null);
			//makeCommands(incAction, false, "&_choice" + lpChoice.getID(), null, null, null);
			//makeCommands(lpChoice, false, "&_action" + incAction.getID(), null, null, retElement);
			makeCommands(incAction, false, "&_lpChoice" + fl.getID(), null, null);
			//makeCommands(lpChoice, false, "&_action" + incAction.getID(), null, retElement);
			makeCommands(lpChoice, false, "&_lpIncAc" + fl.getID(), null, retElement);
			return makeCommands(initAction, false, "&_lpChoice" + fl.getID(), nextCommandCont, null);
			
			/*cmdName="_choice" + currElem.getID();
			hcode+="TMLChoiceCommand " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,(CondFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
			functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\nstatic bool firstTime=true;\nif(firstTime){\nfirstTime=false;\n" + addSemicolonIfNecessary(((TMLForLoop)currElem).getInit()) + "\n}else{\n" + addSemicolonIfNecessary(((TMLForLoop)currElem).getIncrement()) + "\n}\nif(" + ((TMLForLoop)currElem).getCondition() + "){\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + commentNum + "));\n#endif\nreturn 0;\n}else{\n#ifdef ADD_COMMENTS\naddComment(new Comment(_endLastTransaction,0," + (commentNum+1) + "));\n#endif\nfirstTime=true;\nreturn 1;\n}\n}\n\n";
			commentText+="_comment[" + commentNum + "]=std::string(\"" + ((TMLForLoop)currElem).getCondition() + "=true\");\n";
			commentNum++;
			commentText+="_comment[" + commentNum + "]=std::string(\"Exit loop: " + ((TMLForLoop)currElem).getCondition() + "=false\");\n";
			commentNum++;
			functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
			nextCommand= cmdName + ".setNextCommand(array(2,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,"&"+cmdName,null,null,null) + ",(TMLCommand*)" + makeCommands(currElem.getNextElement(1),false,retElement,null,null,lastSequence) + "))"+ SCCR;*/
		
		} else if (currElem instanceof TMLReadChannel){
			if (debug) System.out.println("Checking Read\n");
			cmdName= "_read" + currElem.getID();
			hcode+="TMLReadCommand " + cmdName + SCCR;
			TMLReadChannel rCommand=(TMLReadChannel)currElem;
			//initCommand+= "," + cmdName + "(this," + rCommand.getNbOfSamples() + "*" + rCommand.getChannel().getSize() + "," + rCommand.getChannel().getExtendedName() + ")"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, rCommand.getChannel().getSize() + "*(" + rCommand.getNbOfSamples()+")",null) + "," + rCommand.getChannel().getExtendedName() + ")"+CR;
			//initCommand+= "," + cmdName + "(this," + rCommand.getNbOfSamples() + "," + rCommand.getChannel().getExtendedName() + ")"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLWriteChannel){
			if (debug) System.out.println("Checking Write\n");
			cmdName= "_write" + currElem.getID();
			hcode+="TMLWriteCommand " + cmdName + SCCR;
			TMLWriteChannel wCommand=(TMLWriteChannel)currElem;
			//initCommand+= "," + cmdName + "(this," + wCommand.getNbOfSamples() + "*" + wCommand.getChannel().getSize() + "," + wCommand.getChannel().getExtendedName() + ")"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this," + makeCommandLenFunc(cmdName, wCommand.getChannel().getSize() + "*(" + wCommand.getNbOfSamples() + ")", null) + "," + wCommand.getChannel().getExtendedName() + ")"+CR;
			//initCommand+= "," + cmdName + "(this," + wCommand.getNbOfSamples() + "," + wCommand.getChannel().getExtendedName() + ")"+CR;
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
		
		} else if (currElem instanceof TMLSendEvent){
			if (debug) System.out.println("Checking Send\n");
			cmdName= "_send" + currElem.getID();
			hcode+="TMLSendCommand " + cmdName + SCCR;
			if (((TMLSendEvent)currElem).getNbOfParams()==0){
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLSendEvent)currElem).getEvent().getExtendedName() + ",0)"+CR;
			}else{ 
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLSendEvent)currElem).getEvent().getExtendedName() + ",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func)" + CR;
				functionSig+="void " + cmdName + "_func(Parameter<ParamType>& ioParam)" + SCCR;
				functions+="void " + reference + "::" + cmdName +  "_func(Parameter<ParamType>& ioParam){" + CR;
				for(int i=0; i<3; i++) {
					if (((TMLSendEvent)currElem).getParam(i) == null) {
						functions += "ioParam.setP" + (i+1) + "(0)" + SCCR;
					} else {
						if (((TMLSendEvent)currElem).getParam(i).length() > 0) {
							functions += "ioParam.setP" + (i+1) + "(" + ((TMLSendEvent)currElem).getParam(i) + ")" + SCCR;
						} else {
							functions += "ioParam.setP" + (i+1) + "(0)" + SCCR;
						}
					}
            			}
				functions+="}\n\n"; 
			}
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;
			
		} else if (currElem instanceof TMLSendRequest){
			if (debug) System.out.println("Checking Request\n");
			cmdName= "_request" + currElem.getID();
			hcode+="TMLRequestCommand " + cmdName + SCCR;
			if (((TMLSendRequest)currElem).getNbOfParams()==0){
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLSendRequest)currElem).getRequest().getExtendedName() + ",0)"+CR;
			}else{ 
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLSendRequest)currElem).getRequest().getExtendedName() + ",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func)" + CR;
				functionSig+="void " + cmdName + "_func(Parameter<ParamType>& ioParam)" + SCCR;
				functions+="void " + reference + "::" + cmdName +  "_func(Parameter<ParamType>& ioParam){" + CR;
				for(int i=0; i<3; i++) {
					if (((TMLSendRequest)currElem).getParam(i) == null) {
						functions += "ioParam.setP" + (i+1) + "(0)" + SCCR;
					} else {
						if (((TMLSendRequest)currElem).getParam(i).length() > 0) {
							functions += "ioParam.setP" + (i+1) + "(" + ((TMLSendRequest)currElem).getParam(i) + ")" + SCCR;
						} else {
							functions += "ioParam.setP" + (i+1) + "(0)" + SCCR;
						}
					}
				}
				functions+="}\n\n"; 
			}
			nextCommand= cmdName + ".setNextCommand(array(1,(TMLCommand*)" + makeCommands(currElem.getNextElement(0),false,retElement,null,null) + "))"+ SCCR;	
	
		} else if (currElem instanceof TMLWaitEvent){
			if (debug) System.out.println("Checking Wait\n");
			cmdName= "_wait" + currElem.getID();
			hcode+="TMLWaitCommand " + cmdName + SCCR;
			if (((TMLWaitEvent)currElem).getNbOfParams()==0){
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLWaitEvent)currElem).getEvent().getExtendedName() + ",0)"+CR;
			}else{ 
				initCommand+= "," + cmdName + "("+currElem.getID()+",this," + ((TMLWaitEvent)currElem).getEvent().getExtendedName() + ",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func)" + CR;
				functionSig+="void " + cmdName + "_func(Parameter<ParamType>& ioParam)" + SCCR;
				functions+="void " + reference + "::" + cmdName +  "_func(Parameter<ParamType>& ioParam){" + CR;
				for(int i=0; i<3; i++) {
					if (((TMLWaitEvent)currElem).getParam(i) != null) {
						if (((TMLWaitEvent)currElem).getParam(i).length() > 0) {
							functions += ((TMLWaitEvent)currElem).getParam(i) + "=ioParam.getP" + (i+1) +"()"+ SCCR;
						}
					}
				}
				functions+="}\n\n"; 
			}
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
					//nextBranch=makeCommands(currElem.getNextElement(0),false,nextBranch,nextCommandCont,functionCont,null);
                    			return nextBranch;
					//int index = tmlseq.getCurrIndex(lastSequence);
					//if (index==tmlseq.getNbNext()-1) return makeCommands(currElem.getNextElement(index), false,retElement,nextCommandCont,functionCont,tmlseq.getLastSequence());
					//return makeCommands(currElem.getNextElement(index), false,retElement,nextCommandCont,functionCont,currElem);
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
			if (debug) System.out.println("Checking Choice\n");
			if (choice.getNbGuard() !=0 ) {
				String guardS = "",code2;
				int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
				int nb = choice.nbOfNonDeterministicGuard();
				int nbS = choice.nbOfStochasticGuard();
				if ((nb > 0) || (nbS > 0)){
					code += "rnd__0 = myrand(0, 99)" + SCCR;
				}
				nb = 0;
				for(int i=0; i<choice.getNbGuard(); i++) {
					if (choice.isNonDeterministicGuard(i)) {
						code2 = "(rnd__0 < " + Math.floor(100/choice.getNbGuard())*(nb+1) + ")";
						nb ++;
					} else if (choice.isStochasticGuard(i)) {
						if (guardS.length() == 0) {
							guardS = choice.getStochasticGuard(i);
						} else {
							guardS = "(" + guardS + ")+(" + choice.getStochasticGuard(i) + ")";
						}
						code2 = "(rnd__0 < (" + guardS + "))";
								nbS ++;
					} else {
						code2 = choice.getGuard(i);
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
							MergedCmdStr nextCommandCollection = new MergedCmdStr("",cmdName, returnIndex);
							//System.out.println("Call makeCommands, task: "+reference);
							//if (nextCommandCollection==null) System.out.println("Choice: nextCommandCollection==0");
							if (retElseElement!=null && i==index1)
								//else case
								MCResult = makeCommands(currElem.getNextElement(i), false, retElseElement,nextCommandCollection,null);
							else
								MCResult = makeCommands(currElem.getNextElement(i), false, retElement,nextCommandCollection,null);
							if (nextCommandCollection.funcs.length() == 0){
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
					//initCommand+= "," + cmdName + "("+currElem.getID()+",this,(CondFuncPointer)&" + reference + "::" + cmdName + "_func," + choice.getNbGuard() + ")"+CR;
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
			Integer nbevt=0;
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

						functionSig+="void " + cmdName + "_func" + i + "(Parameter<ParamType>& ioParam)" + SCCR;
						functions+="void " + reference + "::" + cmdName +  "_func" + i + "(Parameter<ParamType>& ioParam){" + CR;
						paramList+=",(ParamFuncPointer)&" + reference + "::" + cmdName + "_func_" + i + CR;
						for(int j=0; j<3; j++) {
							if (((TMLSelectEvt)currElem).getParam(i,j) != null) {
								if (((TMLSelectEvt)currElem).getParam(i,j).length() > 0) {
									//paramList += ((TMLSelectEvt)currElem).getParam(i,j);
									functions += ((TMLSelectEvt)currElem).getParam(i,j) + "=ioParam.getP" + (j+1) +"()"+ SCCR;
								}
							}
						}
						functions+="}\n\n"; 
					}
					nextCommand+= ",(TMLCommand*)" + makeCommands(currElem.getNextElement(i), true, retElement,null,null); 
				}
			}
			hcode+="TMLSelectCommand " + cmdName + SCCR;
			//initCommand+= "," + cmdName + "(this,array("+ nbevt + evtList + "),"+ nbevt + ",0)"+CR;
			initCommand+= "," + cmdName + "("+currElem.getID()+",this,array("+ nbevt + evtList + "),"+ nbevt + ",array("+ nbevt + paramList + "))"+CR;
			nextCommand=cmdName + ".setNextCommand(array(" + nbevt + nextCommand + "))" + SCCR;
		
		} else {
			System.out.println("Operator: " + currElem + " is not managed in the current version of this C++ code generator." + "))" + SCCR);
		}
		chaining+=nextCommand; 
		return (cmdName.equals("0") || cmdName.charAt(0)=='&')? cmdName : "&"+cmdName;
	}
	
	
	private String makeCommandLenFunc(String cmdName, String lowerLimit, String upperLimit){
		if (upperLimit==null)
			functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\nreturn (unsigned int)(" + lowerLimit + ");\n}" + CR2;
		else
			functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\nreturn (unsigned int)myrand(" + lowerLimit + "," + upperLimit + ");\n}" + CR2;
		functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
		return "(LengthFuncPointer)&" + reference + "::" + cmdName + "_func";
	}

	private void makeEndClassH() {
		hcode += "};" + CR + "#endif" + CR;
	}

	
	private String makeAttributesCode() {
		String code = "";
		TMLAttribute att;
		int i;
		ListIterator iterator = task.getAttributes().listIterator();
		while(iterator.hasNext()) {
			att = (TMLAttribute)(iterator.next());
			if (att.hasInitialValue())
				code += ","+ att.name + "(" + att.initialValue + ")"+CR;
			else
				code += ","+ att.name + "(0)"+CR;
		}	
		return code;
	}
	
	private String makeAttributesDeclaration() {
		String code = "";
		TMLAttribute att;
		int i;
		ListIterator iterator = task.getAttributes().listIterator();
		while(iterator.hasNext()) {
			att = (TMLAttribute)(iterator.next());
			//code += TMLType.getStringType(att.type.getType()) + " " + att.name;
			code += "int " + att.name;
			code += ";\n";
		}		
		code += "int rnd__0" + SCCR;
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

}

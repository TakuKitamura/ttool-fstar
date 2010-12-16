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

package avatartranslator.tocppsim;

import java.util.*;
import avatartranslator.*;
import myutil.*;
import java.util.regex.*;


public class AvatarBlockCppSim {
 	private String reference, cppcode, hcode, initCommand, functions, functionSig, chaining, firstCommand;
	private AvatarBlock block;
	LinkedList<AvatarRelation> relations;
	private boolean debug;
	private boolean optimize;
	HashMap<Integer,String> visitedCmds = new HashMap<Integer,String> ();
	private final static Pattern _varPattern = Pattern.compile("[\\w&&\\D]+[\\w]*");
	
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	
	
	public AvatarBlockCppSim(AvatarBlock _block, LinkedList<AvatarRelation> _relations) {
        	reference = _block.getName();
		relations=_relations;
		block=_block;
		cppcode = "";
		hcode = "";
		initCommand="";
		functions="";
		chaining="";
		firstCommand="";
		functionSig="";
		optimize=false;
    	}
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
 
	public void generateCPPSIM(boolean _debug, boolean _optimize) {
        	debug = _debug;
		optimize=_optimize;
		basicCPPCode();
		makeClassCode();
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
		code += "#include <AvBlock.h>\n#include <definitions.h>\n\n";
		code += "#include <AvSyncSignal.h>\n#include <AvAsyncSignal.h>\n#include <AvAsyncSignalB.h>\n\n";
		code += "#include <AvActionCmd.h>\n#include <AvReceiveCmd.h>\n";
		code += "#include <AvSendCmd.h>\n#include <AvState.h>\n#include <AvTimerExpCmd.h>\n";
		code += "#include <AvTimer.h>\n#include <AvTransition.h>\n#include <AvSignal.h>\n";
		return code;
	}
	

	// CPP Code
	private void basicCPPCode() {
		cppcode += "#include <" + reference + DOTH + ">" + CR2;
	}
	
	private void makeClassCode(){
		makeHeaderClassH();
		makeEndClassH();
		
		cppcode+=reference+ "::" + makeConstructorSignature()+":AvBlock(iID, iName)"+ CR + makeAttributesCode();
		cppcode+=initCommand + CR + "{" + CR; 
		cppcode+= "//generate task variable look-up table"+ CR;
		for(AvatarAttribute att: block.getAttributes()) {
			if (att.getType()!=AvatarType.TIMER){
				cppcode += "_varLookUpName[\"" + att.getName() + "\"]=&" + att.getName() +SCCR;
				cppcode += "_varLookUpID[" + att.getID() + "]=&" + att.getName() +SCCR;
			}
		}		
		cppcode += "_varLookUpName[\"rnd__0\"]=&rnd__0" + SCCR + CR;
		cppcode+=CR + "//command chaining"+ CR;
		cppcode+= chaining + "_currCommand=" + firstCommand + SCCR + "_firstCommand=" + firstCommand +SCCR + CR; 
		cppcode+= "_firstCommand->prepare(true)"+SCCR;
		cppcode+="}"+ CR2 + functions; // + makeDestructor();
		hcode = Conversion.indentString(hcode, 4);
		cppcode = Conversion.indentString(cppcode, 4);
	}
	
	private String makeDestructor(){
		String dest=reference + "::~" + reference + "(){" + CR;
		return dest+"}"+CR;
	}

	private String makeConstructorSignature(){
		String constSig=reference+ "(ID iID, std::string iName " +CR;
		for(AvatarRelation rel: relations) {
			//if (rel.block1==block || rel.block2==block){
			int referred = isBlockHierarchyReferredToInRel(rel, block);
			if(referred!=0){
				for(int i=0; i<rel.nbOfSignals(); i++){
					String sigName;
					//if (rel.block1==block) sigName=rel.getSignal1(i).getName(); else sigName=rel.getSignal2(i).getName();
					if (referred==1) sigName=rel.getSignal1(i).getName(); else sigName=rel.getSignal2(i).getName();
					constSig+=", AvSignal* " + sigName + CR;
				}
			}
		}
		return constSig+")";
	}

	private void makeHeaderClassH() {
		String hcodeBegin="";
		hcodeBegin = "class " + reference + ": public AvBlock {" + CR;
		hcodeBegin += "private:" + CR;
		hcodeBegin += "// Attributes" + CR;
		if (block.getStateMachine()==null){
			System.out.println(block.getName() + " does not have a state machine!!!!!!!!!!!!!!!");
		}else{
			visitedCmds.clear();
			firstCommand=makeCommands(block.getStateMachine().getStartState());
			hcode = basicHCode() + hcodeBegin + makeAttributesDeclaration() + CR + hcode;
			// public dec
			hcode += CR + functionSig + CR + "public:" + CR;
			// Simulation
			hcode += makeConstructorSignature() + SCCR; // + "~" + reference + "()" + SCCR;
		}
	}

	private String makeCommands(AvatarStateMachineElement currElem){
		String nextCommand="",cmdName="", cmdInTable;
		
		if ((cmdInTable=visitedCmds.get(currElem.getID()))!=null) return "&" + cmdInTable;

		if (debug) System.out.println("Checking " + currElem.getName() + CR);
		
		if (currElem instanceof AvatarStartState || currElem instanceof AvatarState || currElem instanceof AvatarStopState) {
			if (debug) System.out.println("Checking State\n");
			cmdName= "_state" + currElem.getID();
			visitedCmds.put(currElem.getID(), cmdName);
			hcode+="AvState " + cmdName + SCCR;
			initCommand+= "," + cmdName + "(" + currElem.getID() + ", \"" + currElem.getName() + "\", this, " + currElem.nbOfNexts() +  ")" + CR;
			if (currElem.nbOfNexts()==0){
				nextCommand = cmdName + ".setOutgoingTrans(0)" + SCCR;
			}else{
				nextCommand = cmdName + ".setOutgoingTrans(array(" + currElem.nbOfNexts();
				for(int i=0; i<currElem.nbOfNexts(); i++)
					nextCommand += ", (AvTransition*)" + makeCommands(currElem.getNext(i));
				nextCommand += "))"+ SCCR;
			}
			
			//return makeCommands(currElem.getNextElement(0), false,retElement,nextCommandCont,null);
			
		} else if (currElem instanceof AvatarSetTimer || currElem instanceof AvatarResetTimer || currElem instanceof AvatarRandom){
			String action;
			if (currElem instanceof AvatarSetTimer){				
				if (debug) System.out.println("Checking SetTimer\n");
				action = ((AvatarSetTimer)currElem).getTimer().getName() + ".set(" + ((AvatarSetTimer)currElem).getTimerValue() + ");";
				cmdName= "_tmset" + currElem.getID();
				//if (action==null) System.out.println("No action!!!!\n");
			}else if(currElem instanceof AvatarRandom){
				if (debug) System.out.println("Checking Random\n");
				AvatarRandom random = (AvatarRandom)currElem;
				action = random.getVariable() + "=myrand("+ random.getMinValue() + "," + random.getMaxValue() + ");";
				cmdName= "_random" + currElem.getID();
			}else{
				if (debug) System.out.println("Checking AvatarResetTimer\n");
				action = ((AvatarResetTimer)currElem).getTimer().getName() + ".reset();";
				cmdName= "_tmrset" + currElem.getID();
			}
			//cmdName= "_action" + currElem.getID();
			visitedCmds.put(currElem.getID(), cmdName);
			hcode+="AvActionCmd " + cmdName + SCCR;
			initCommand+= "," + cmdName + "("+ currElem.getID() + ", this, (ActionFuncPointer)&" + reference + "::" + cmdName + "_func, \"" + action + "\")"+CR;
			nextCommand= cmdName + ".setOutgoingTrans(array(1,(AvTransition*)" + makeCommands(currElem.getNext(0)) + "));\n";
			functions+="void "+ reference + "::" + cmdName + "_func(){\n#ifdef COMMENTS\nstd::cout << \"execute action: " + action  + "\\n\";\n#endif\n" + action + "\n}" + CR2;
			functionSig+="void " + cmdName + "_func()" + SCCR;				
				
		} else if (currElem instanceof AvatarActionOnSignal){
			if (debug) System.out.println("Checking Send\n");
			AvatarActionOnSignal actionSig=(AvatarActionOnSignal)currElem;
			boolean completeFunc=true;
			if(actionSig.isSending()){
				cmdName= "_send" + currElem.getID();
				hcode+="AvSendCmd " + cmdName + SCCR;
				initCommand+= "," + cmdName + "("+ currElem.getID() + ", this, " + actionSig.getSignal().getName() + ",";
				//initCommand+= "," + cmdName + "("+ currElem.getID() + ", this, " + actionSig.getSignal().getName() + ",";
				if(actionSig.getNbOfValues()==0){
					initCommand+="(ParamGetFuncPointer)0)\n";
					completeFunc=false;
				}else{
					initCommand+= "(ParamGetFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
					functions+="Parameter* "+ reference + "::" + cmdName + "_func(){\nreturn new Parameter(PARAM_INIT(";
					functionSig+="Parameter* " + cmdName + "_func()" + SCCR;
				}
			}else{
				cmdName= "_rec" + currElem.getID();
				initCommand+= "," + cmdName + "("+ currElem.getID() + ", this, " + actionSig.getSignal().getName() + ",";
				hcode+="AvReceiveCmd " + cmdName + SCCR;
				//initCommand+= "," + cmdName + "("+ currElem.getID() + ", this, " + actionSig.getSignal().getName() + ",";
				if(actionSig.getNbOfValues()==0){
					initCommand+="(ParamSetFuncPointer)0)\n";
					completeFunc=false;
				}else{
					initCommand+= "(ParamSetFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
					functions+="void "+ reference + "::" + cmdName + "_func(Parameter* iParam){\niParam->copyTo(PARAM_CPY(";
					functionSig+="void " + cmdName + "_func(Parameter* iParam)" + SCCR;
				}
			}
			visitedCmds.put(currElem.getID(), cmdName);
			if (completeFunc){
				functions+=actionSig.getNbOfValues();
				for(int i=0; i<actionSig.getNbOfValues(); i++){
					functions+= ", " + actionSig.getValue(i);
				}
				functions+= "));\n}" + CR2;
			}
			nextCommand= cmdName + ".setOutgoingTrans(array(1,(AvTransition*)" + makeCommands(currElem.getNext(0)) + "));\n";
			
		} else if (currElem instanceof AvatarTransition){
			String condFunc, actionFunc;
			AvatarTransition trans = (AvatarTransition)currElem;
			//AvTransition(ID iID, AvBlock* iBlock, CondFuncPointer iCondFunc, AVTTime iAfterMin, AVTTime iAfterMax, AVTTime iComputeMin, AVTTime iComputeMax, ActionFuncPointer iActionFunc);
			cmdName= "_trans" + currElem.getID();
			visitedCmds.put(currElem.getID(), cmdName);
			hcode+="AvTransition " + cmdName + SCCR;
			if(trans.getNbOfAction()==0){
				actionFunc="(ActionFuncPointer)0";
			}else{
				actionFunc="(ActionFuncPointer)&" + reference + "::" + cmdName + "_afunc";
				functions+="void "+ reference + "::" + cmdName + "_afunc(){\n";
				for(int i=0; i< trans.getNbOfAction(); i++){
					functions+= commentSymbolicAction(addSemicolonIfNecessary(modifyString(trans.getAction(i)))) + "\n";
				}
				functions+= "}" + CR2;
				functionSig+="void " + cmdName + "_afunc()" + SCCR;
			}
			if(trans.isGuarded()){
				condFunc="(CondFuncPointer)&" + reference + "::" + cmdName + "_cfunc";
				functions+="unsigned int "+ reference + "::" + cmdName + "_cfunc(){\n#ifdef COMMENTS\nstd::cout << \"check condition: " + trans.getGuard()  + "\\n\";\n#endif\nreturn (" + Conversion.replaceAllChar(Conversion.replaceAllChar(trans.getGuard(), '[', "("),']',")") + ")? 1:0;\n";
				functions+= "}" + CR2;
				functionSig+="unsigned int " + cmdName + "_cfunc()" + SCCR;
			}else{
				condFunc="(CondFuncPointer)0";
			}
			initCommand+= "," + cmdName + "(" + currElem.getID() + ", this, " + condFunc + ", " + setEmtyStrToZero(trans.getMinDelay()) + ", " + setEmtyStrToZero(trans.getMaxDelay()) + ", " + setEmtyStrToZero(trans.getMinCompute()) + ", " + setEmtyStrToZero(trans.getMaxCompute()) + ", " + actionFunc + ")" + CR;
			nextCommand= cmdName + ".setOutgoingCmd(" + makeCommands(currElem.getNext(0)) + ");\n";
			
		} else if (currElem instanceof AvatarExpireTimer){
			cmdName= "_exp" + currElem.getID();
			visitedCmds.put(currElem.getID(), cmdName);
			hcode+="AvTimerExpCmd " + cmdName + SCCR;
			initCommand+= "," + cmdName + "(" + currElem.getID() + ", this, &" + ((AvatarExpireTimer)currElem).getTimer().getName() +  ")" + CR;
			nextCommand= cmdName + ".setOutgoingTrans(array(1,(AvTransition*)" + makeCommands(currElem.getNext(0)) + "));\n";
			
		} else {
			System.out.println("Operator: " + currElem + " of class " + currElem.getClass().getName() +  " is not managed in the current version of this C++ code generator.");
		}
		chaining+=nextCommand; 
		return (cmdName.equals("0") || cmdName.charAt(0)=='&')? cmdName : "&"+cmdName;
	}


	/*private boolean isIntValue(String input){  
		try{  
			Integer.parseInt(input); 
			return true;  
		}catch(Exception e){  
			return false;
		}  
	}*/

	private void makeEndClassH() {
		hcode += "};" + CR + "#endif" + CR;
	}

	
	private String makeAttributesCode() {
		String code = "";
		for(AvatarAttribute att: block.getAttributes()) {
			if (att.getType()==AvatarType.TIMER){
				code += ","+ att.getName() + "(" + att.getID() + ", \""+ att.getName() + "\")"+CR;
			}else{
				if (att.hasInitialValue())
					code += ","+ att.getName() + "(" + att.getInitialValue() + ")"+CR;
				else
					code += ","+ att.getName() + "(0)"+CR;
			}
		}	
		return code;
	}
	
	private String makeAttributesDeclaration() {
		String code = "";
		for(AvatarAttribute att: block.getAttributes()) {
			if (att.getType()==AvatarType.TIMER)
				code += "AvTimer " + att.getName() + ";\n";
			else
				code += "ParamType " + att.getName() + ";\n";
		}		
		code += "ParamType rnd__0" + SCCR;
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
	
	private String setEmtyStrToZero(String iStr){
		return (iStr.isEmpty())? "0":iStr;
	}
	
	private String commentSymbolicAction(String iAction){
		Matcher matcher = _varPattern.matcher(iAction);
		System.out.print("Found tokens: ");
		while (matcher.find()){
			String token = iAction.substring(matcher.start(), matcher.end());
			System.out.print(token + ", ");
			//if (!isAttribute(token)) return "//" + iAction;
			if (!isAttribute(token)) return "#ifdef COMMENTS\nstd::cout << \"execute action: " + iAction  + "\\n\";\n#endif\n";
			
		}
		System.out.println();
		return iAction + "\n#ifdef COMMENTS\nstd::cout << \"execute action: " + iAction  + "\\n\";\n#endif\n";
	}
	
	private boolean isAttribute(String iAttr){
		for(AvatarAttribute att: block.getAttributes()) {
			if (att.getName().equals(iAttr)) return true;
		}
		return false;
	}
	
	public static int isBlockHierarchyReferredToInRel(AvatarRelation rel, AvatarBlock blk){
		if (blk==null) return 0;
		if (rel.block1==blk) return 1;
		if (rel.block2==blk) return 2;
		return isBlockHierarchyReferredToInRel(rel, blk.getFather());
	}
}

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
 * Class SystemCEBRDD
 * Creation: 13/10/2009
 * @version 1.0 13/10/2009
 * @author Daniel Knorreck
 * @see
 */

package tmltranslator.tomappingsystemc2;

import java.util.*;

import tmltranslator.*;
import myutil.*;
import req.ebrdd.*;


public class SystemCEBRDD {
	private EBRDD ebrdd;
 	private String reference, cppcode, hcode, initCommand, functions, functionSig, chaining, firstCommand, idsMergedCmds;
	private String ETclasses;
	//, ETdeclare, ETInit;
	private boolean debug;
	TMLModeling tmlmodeling;
	TMLMapping tmlmapping;
	
	private final static String [] events = {"transexecuted", "cmdentered", "cmdstarted", "cmdexecuted", "cmdfinished", "taskstarted", "taskfinished", "readtrans", "writetrans", "simstarted", "simfinished"};
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	
	
	public SystemCEBRDD(EBRDD _ebrdd, TMLModeling _tmlmodeling, TMLMapping _tmlmapping) {
        	ebrdd = _ebrdd;
        	reference = ebrdd.getName();
		cppcode = "";
		hcode = "";
		initCommand="";
		functions="";
		chaining="";
		firstCommand="";
		functionSig="";
		idsMergedCmds="";
		ETclasses="";
		tmlmodeling=_tmlmodeling;
		tmlmapping=_tmlmapping;
    	}
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
	public EBRDD getEBRDD() {
		return ebrdd;
	}
    
	public void generateSystemC(boolean _debug) {
        	debug = _debug;
		//basicHCode();
		basicCPPCode();
		makeClassCode();
    	}
	
	public void print() {
		System.out.println("EBRDD: " + reference + DOTH + hcode);
		System.out.println("EBRDD: " + reference + DOTCPP + cppcode);
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
		code += "#include <definitions.h>\n#include <EBRDD.h>\n#include <ERC.h>\n#include <ESO.h>\n#include <ERB.h>\n#include <EBRDDChoiceCommand.h>\n#include <EBRDDActionCommand.h>\n#include <EBRDDStopCommand.h>\n\n";
		return code;
	}
	
	// CPP Code
	private void basicCPPCode() {
		cppcode += "#include <" + reference + DOTH + ">" + CR2;
	}
	
	private void makeClassCode(){
		makeHeaderClassH();
		makeEndClassH();
		cppcode+=reference+ "::" + makeConstructorSignature()+":EBRDD(iID, iName)"+ CR + makeAttributesCode();
		cppcode+=initCommand + CR + "{" + CR; 
		cppcode+= "//generate EBRDD variable look-up table"+ CR;
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++) {
			String attName = ebrdd.getAttributeByIndex(i).getName();
			cppcode += "_varLookUpName[\"" + attName + "\"]=&" + attName +SCCR;
			//cppcode += "_varLookUpID[" + ebrdd.getAttributeByIndex(i).getID() + "]=&" + attName +SCCR;
		}		
		cppcode+=CR + "//command chaining"+ CR;
		cppcode+= chaining + "_currCommand=" + firstCommand + SCCR + "_firstCommand=" + firstCommand + SCCR + CR; 
		cppcode+="//IDs of merged commands\n" + idsMergedCmds + CR;
		cppcode+="}"+ CR2 + functions; // + makeDestructor();
		hcode = Conversion.indentString(hcode, 4);
		cppcode = Conversion.indentString(cppcode, 4);
	}
	
	private String makeDestructor(){
		String dest=reference + "::~" + reference + "(){" + CR;
		return dest+"}"+CR;
	}

	private String makeConstructorSignature(){
		return reference + "(unsigned int iID, std::string iName)";
	}

	private void makeHeaderClassH() {
		String hcodeBegin="";
		// Common dec
		hcodeBegin = "class " + reference + ": public EBRDD {" + CR;
		hcodeBegin += "private:" + CR;
		// Attributes
		hcodeBegin += "// Attributes" + CR;
		firstCommand=makeCommands(ebrdd.getStartState(), "0", null, null);
		hcode = basicHCode() + ETclasses + "\n\n" + hcodeBegin + makeAttributesDeclaration() + CR + hcode;
		// public dec
		hcode += CR + functionSig + CR + "public:" + CR;
		// Simulation
		hcode += makeConstructorSignature() + SCCR;
		makeSerializableFuncs();
	}

	private void makeSerializableFuncs(){
		hcode += "virtual std::istream& readObject(std::istream& i_stream_var)" + SCCR;
		hcode += "virtual std::ostream& writeObject(std::ostream& i_stream_var)" + SCCR;
		functions+= "std::istream& " + reference + "::readObject(std::istream& i_stream_var){\nEBRDD::readObject(i_stream_var);\n"; 
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++){
			String attName = ebrdd.getAttributeByIndex(i).getName();
			functions += "READ_STREAM(i_stream_var," + attName + ")" + SCCR;
			functions += "std::cout << \"Read: Variable " + attName + " \" << " + attName +  " << std::endl" + SCCR;
		}
		functions+= "return i_stream_var;\n}\n\n";
		functions+= "std::ostream& " + reference + "::writeObject(std::ostream& i_stream_var){\nEBRDD::writeObject(i_stream_var);\n";
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++){
			String attName = ebrdd.getAttributeByIndex(i).getName();
			functions += "WRITE_STREAM(i_stream_var," + attName + ")" + SCCR;
			functions += "std::cout << \"Write: Variable " + attName + " \" << " + attName +  " << std::endl" + SCCR;
		}
		functions+= "return i_stream_var;\n}\n\n";
		hcode += "void reset()" + SCCR;
		functions+= "void "+reference + "::reset(){\nEBRDD::reset();\n";
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++){
			EBRDDAttribute att = ebrdd.getAttributeByIndex(i);
			functions += att.getName() + "=";
			if (att.hasInitialValue())
				functions += att.getInitialValue() + SCCR;
			else
				functions += "0" + SCCR;
		}
		functions+= "}\n\n";
	}

	private String makeCommands(EBRDDComponent currElem, String retElement, MergedCmdStr nextCommandCont, String retElseElement){
		String nextCommand="",cmdName="";

		if (currElem==null){
			if (debug) System.out.println("Checking null\n");
			return retElement;
		}
		
		if (debug) System.out.println("Checking " + currElem.getName() + CR);

		if (currElem instanceof EBRDDStart) {
			if (debug) System.out.println("Checking Start\n");
			return makeCommands(currElem.getNextElement(0), retElement,nextCommandCont,null);
		
		} else if (currElem instanceof EBRDDStop){
			if (debug) System.out.println("Checking Stop\n");
			if (retElement.equals("0")){
				cmdName= "_stop" + currElem.getID();
				hcode+="EBRDDStopCommand " + cmdName + SCCR;
				initCommand+= "," + cmdName + "(" + currElem.getID() + ",this)" + CR;
			}else
				return retElement;
		
		} else if (currElem instanceof EBRDDActionState){
			String action;
			if (debug) System.out.println("Checking Action\n");
			action = ((EBRDDActionState)currElem).getAction();
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
				hcode+="EBRDDChoiceCommand " + cmdName + SCCR;
				MergedCmdStr nextCommandCollection = new MergedCmdStr("", cmdName);
				initCommand+= "," + cmdName + "("+ idString + ",this,(EBRDDFuncPointer)&" + reference + "::" + cmdName + "_func)"+CR;
				String MKResult = makeCommands(currElem.getNextElement(0), retElement, nextCommandCollection, null);
				if(nextCommandCollection.num==0){
					nextCommand= cmdName + ".setNextCommand(array(1,(EBRDDCommand*)" + MKResult + "));\n";
				}else{	
					nextCommand= cmdName + ".setNextCommand(array(" + nextCommandCollection.num + nextCommandCollection.nextCmd + "));\n";
				}
				functions+="unsigned int "+ reference + "::" + cmdName + "_func(){\n" + modifyString(addSemicolonIfNecessary(action)) + CR + nextCommandCollection.funcs;
				if (nextCommandCollection.num==0) functions+="return 0"+ SCCR;
				functions+= "}" + CR2;
				functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
			}else{
				idsMergedCmds += "_commandHash[" + idString + "]=&" + nextCommandCont.srcCmd + SCCR;
				nextCommandCont.funcs += modifyString(addSemicolonIfNecessary(action)) + CR;
				return makeCommands(currElem.getNextElement(0), retElement, nextCommandCont, null);
			}
		
		} else if (currElem instanceof EBRDDLoop){
			if (debug) System.out.println("Checking Loop\n");
			EBRDDLoop fl = (EBRDDLoop)currElem;
			//EBRDDActionState initAction=new EBRDDActionState("lpInitAc",null);
			//initAction.setAction(fl.getInit());
			EBRDDActionState initAction=null;
			if (!fl.getInit().isEmpty()){
				initAction = new EBRDDActionState("lpInitAc",null);
				initAction.setAction(fl.getInit());
			}
			//EBRDDActionState incAction=new EBRDDActionState("#"+ fl.getID() + "\\lpIncAc",null);
			//incAction.setAction(fl.getIncrement());
			EBRDDActionState incAction=null;
			if (!fl.getIncrement().isEmpty()){
				incAction=new EBRDDActionState("#"+ fl.getID() + "\\lpIncAc",null);
				incAction.setAction(fl.getIncrement());
			}
			EBRDDChoice lpChoice=new EBRDDChoice("#"+ fl.getID() + "\\lpChoice",null);
			lpChoice.addGuard("[ " + fl.getCondition() + " ]");
			lpChoice.addGuard("[ else ]");
			//incAction.addNext(lpChoice);
			lpChoice.addNext(fl.getNextElement(0));  //inside loop
			lpChoice.addNext(fl.getNextElement(1));  //after loop           cmdName= "_choice" + currElem.getID();
			if (incAction==null){
				makeCommands(lpChoice, "&_lpChoice" + fl.getID(), null, retElement);
			}else{
				makeCommands(incAction, "&_lpChoice" + fl.getID(), null, null);
				makeCommands(lpChoice, "&_lpIncAc" + fl.getID(), null, retElement);
			}
			return makeCommands(initAction, "&_lpChoice" + fl.getID(), nextCommandCont, null);
	

		} else if (currElem instanceof EBRDDSequence){
			EBRDDSequence tmlseq = (EBRDDSequence)currElem;
			if (debug) System.out.println("Checking Sequence with "+ tmlseq.getNbNext()+ " elements.");
			if (tmlseq.getNbNext() == 0) {
				//if (lastSequence!=null) return makeCommands(lastSequence, retElement,nextCommandCont,functionCont,null);
                		return retElement;
            		} else {
                		if (tmlseq.getNbNext() == 1) {
                    			return makeCommands(currElem.getNextElement(0), retElement, nextCommandCont, null);
                		} else {			
					String nextBranch;
					tmlseq.sortNexts();
					if (debug) System.out.println("Checking Sequence branch "+ (tmlseq.getNbNext()-1));
					nextBranch= makeCommands(currElem.getNextElement(currElem.getNbNext() - 1), retElement, null, null);
					for(int i=currElem.getNbNext() - 2; i>=0; i--) {
						if (debug) System.out.println("Checking Sequence branch "+ i);
						nextBranch=makeCommands(currElem.getNextElement(i), nextBranch, null, null);
					}
                    			return nextBranch;
                		}
            		}
		} else if(currElem instanceof EBRDDERC){
			cmdName= "_erc" + currElem.getID();
			strwrap ETDeclare=new strwrap(), ETInit=new strwrap();
			LinkedList<String> erbFuncs = new LinkedList<String>();
			hcode+= cmdName + "_class " + cmdName + SCCR;
			//ERC declaration!!!
			buildET(((EBRDDERC)currElem).getRoot(), "this", 0, ETDeclare, ETInit, erbFuncs);
			initCommand+= ", " + cmdName + "("+ currElem.getID() + ", this";
			for (String erbFunc : erbFuncs){
				initCommand+= ", (EBRDDFuncPointer)&" + reference + "::" + erbFunc;
			}
			initCommand+=")\n";
			nextCommand= cmdName + ".setNextCommand(array(1,(EBRDDCommand*)" + makeCommands(currElem.getNextElement(0),retElement,null,null) + "))"+ SCCR;
			ETclasses+="class " + cmdName + "_class: public ERC{\nprivate:\n" + ETDeclare.str;
			ETclasses+="public:\n" + cmdName + "_class(unsigned int iID, EBRDD* iEBRDD";
			for (String erbFunc : erbFuncs){
			 	ETclasses+= ", EBRDDFuncPointer " + erbFunc;
			} 
			ETclasses+= "): ERC(iID, iEBRDD)\n";
			ETclasses+=ETInit.str + "{}\n};\n\n";
		
		} else if (currElem instanceof EBRDDChoice){
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
			EBRDDChoice choice = (EBRDDChoice)currElem;
			String code = "", nextCommandTemp="", MCResult="";
			if (debug) System.out.println("Checking Choice\n");
			if (choice.getNbGuard() !=0 ) {
				String guardS = "",code2;
				int index1 = choice.getElseGuard();
				for(int i=0; i<choice.getNbGuard(); i++) {
					code2 = choice.getGuard(i);
					code2 = Conversion.replaceAllChar(code2, '[', "(");
					code2 = Conversion.replaceAllChar(code2, ']', ")");
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
							MCResult = makeCommands(currElem.getNextElement(i), retElseElement,nextCommandCollection,null);
						else
							MCResult = makeCommands(currElem.getNextElement(i), retElement,nextCommandCollection,null);
						if (nextCommandCollection.funcs.length() == 0){
							code += "{\nreturn " + returnIndex + SCCR +"}" + CR;
							returnIndex++;
							nextCommandTemp+= ",(EBRDDCommand*)" + MCResult;
						}else{
							returnIndex = nextCommandCollection.num;
							code += "{\n" + nextCommandCollection.funcs + "return " + returnIndex + ";\n}" + CR;
							returnIndex++;
							nextCommandTemp+= nextCommandCollection.nextCmd+ ",(EBRDDCommand*)" + MCResult;
						}
					}else{
						idsMergedCmds += "_commandHash[" + currElem.getID() + "]=&" + nextCommandCont.srcCmd + SCCR;
						//System.out.println("Choice: Next command!=0 "+ code2);
						int oldReturnIndex=nextCommandCont.num;
						nextCommandCont.funcs += code + "{\n";
						if (retElseElement!=null && i==index1)
							MCResult = makeCommands(currElem.getNextElement(i), retElseElement, nextCommandCont,null);
						else
							MCResult = makeCommands(currElem.getNextElement(i), retElement, nextCommandCont,null);
						if (oldReturnIndex==nextCommandCont.num){
							//System.out.println("RETURN, ccINC NUM "+ nextCommandCont.num);
							nextCommandCont.funcs+= "return " + nextCommandCont.num + SCCR;
							nextCommandCont.num++;
							nextCommandCont.nextCmd += ",(EBRDDCommand*)" + MCResult;
						}
						nextCommandCont.funcs+= "}\n";
						code="";
					}
					 
				}
				// If there was no else, do a terminate
				if (nextCommandCont==null){
					//System.out.println("Choice: finalization, add new command\n");
					if (index1 == -1){
						code += "return " + returnIndex + SCCR;
						nextCommand= cmdName + ".setNextCommand(array(" + (returnIndex+1) + nextCommandTemp + ",(EBRDDCommand*)0))" + SCCR;
					}else{
						nextCommand= cmdName + ".setNextCommand(array(" + returnIndex + nextCommandTemp + "))" + SCCR;
					}
					hcode+="EBRDDChoiceCommand " + cmdName + SCCR;
					initCommand+= "," + cmdName + "("+ idString +",this,(EBRDDFuncPointer)&" + reference + "::" + cmdName + "_func)\n";
					functions+="unsigned int "+ reference + "::" + cmdName + "_func(){" + CR + code +CR+ "}" + CR2;
					functionSig+="unsigned int " + cmdName + "_func()" + SCCR;
				}else{
					//System.out.println("Choice: finalization, No new command\n");
					if (index1 == -1){
						nextCommandCont.funcs += "return " + nextCommandCont.num + SCCR;
						nextCommandCont.nextCmd += ",(EBRDDCommand*)0";
						//System.out.println("RETURN, ddINC NUM "+ nextCommandCont.num);
						nextCommandCont.num++;
					}
					cmdName=MCResult;
				}
			}
					
		} else {
			System.out.println("Operator: " + currElem + " is not managed in the current version of this C++ code generator." + "))" + SCCR);
		}
		chaining+=nextCommand; 
		return (cmdName.equals("0") || cmdName.charAt(0)=='&')? cmdName : "&"+cmdName;
	}
	
	
	private void makeEndClassH() {
		hcode += "};" + CR + "#endif" + CR;
	}

	private int buildET(ERCElement currElem, String ancestor, int newID, strwrap ETDeclare, strwrap ETInit, LinkedList<String> erbFuncs){
		if (currElem==null) return newID;
		String negated = (currElem.isNegated())? "true":"false";
		newID++;
		if (currElem instanceof ESO){
			ESO currESO = (ESO)currElem;
			String oncePerEvent = (currESO.getOncePerEvent())? "true":"false";
			String esoName="";
			switch (currESO.getID()){
			case 0://Conjunction
				//(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent)
				esoName= "_esoConj" + newID;
				ETDeclare.str+= "ESOConjunction " + esoName + ";\n";
				ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + "," + oncePerEvent + ")\n";
			break;
			case 1: //Disjunction
				//ESODisjunction(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut);
				esoName= "_esoDisj" + newID;
				ETDeclare.str+= "ESODisjunction " + esoName + ";\n";
				ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + ")\n";
			break;
			case 2: //Sequence
				//ESOSequence(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut);
				esoName= "_esoSeq" + newID;
				ETDeclare.str+= "ESOSequence " + esoName + ";\n";
				ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + ")\n";
			break;
			case 3: //Strict sequence
			case 4: //Simultaneous
				//ESOSSequence(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut);
				esoName= "_esoSSeq" + newID;
				ETDeclare.str+= "ESOSSequence " + esoName + ";\n";
				ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + ")\n";
			break;
			case 5: //At least/at most
				if (currESO.getN()==0){
					//ESOAtMost(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN);
					esoName= "_esoAtMost" + newID;
					ETDeclare.str+= "ESOAtMost " + esoName + ";\n";
					ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + "," + oncePerEvent + ","+ currESO.getM() + ")\n";
				}else{
					//ESOAtLeast(NotifyIF* iAncestorNode, bool iNegated, unsigned int iNbOfEvents, TMLTime iTimeOut, bool iOncePerEvent, unsigned int iN);
					esoName= "_esoAtLeast" + newID;
					ETDeclare.str+= "ESOAtLeast " + esoName + ";\n";
					ETInit.str+= "," + esoName + "(" + ancestor + ","+ negated + "," + currESO.getNbOfSons() + "," + currESO.getTimeout() + "," + oncePerEvent + ","+ currESO.getN() + ")\n";	
				}
				break;
			default:
				//can't handle element
			}
			for (int i=0; i<currESO.getNbOfSons(); i++){
				newID = buildET(currESO.getSon(i), "&" + esoName, newID, ETDeclare, ETInit, erbFuncs);
			} 
		}else if(currElem instanceof ERB){
			//getEvent, getCondition, getAction
			//ERB(NotifyIF* iAncestorNode, bool iNegated, const std::string& iName, unsigned int iSourceClass, unsigned int iSourceID, unsigned int iEvtID);
			//String message="myEvent(source1, source2, source3)";
			ERB currERB = (ERB)currElem;
			String[] tokens = currERB.getEvent().split("[ \\(\\),]+");
			System.out.print("Tokens: ");
			for (int i=0; i< tokens.length; i++){
				System.out.print(tokens[i]+ ", ");
			}
			System.out.println("");
			if (tokens.length>1){
				//eventID, sourceClass, arrayOfSources, numberOfSources
				String erbName= "_erb" + newID;
				ETDeclare.str+= "ERB " + erbName + ";\n";
				idtypewrap source=null;
				ETInit.str+= ", " + erbName + "(this, " + ancestor + ", "+ negated + ", \"" + erbName + "\", ";
				int paramIndex=1;
				String sourceIDs="";
				int nbOfIDs=0;
				while(paramIndex<tokens.length){
					source = getIDType(tokens[paramIndex]);
					sourceIDs += source.id;
					paramIndex++;
					nbOfIDs+=source.nbOfIDs;
				}
				ETInit.str += getEventCode(tokens[0]) + ", " + source.type + ", array(" + nbOfIDs;
				ETInit.str += sourceIDs + "), " + nbOfIDs + ", ";
				if ((currERB.getCondition().isEmpty() || currERB.getCondition().trim().toLowerCase().equals("true")) && currERB.getAction().isEmpty()){
					ETInit.str += "0, \"" + currERB.getCondition() + "\")\n";
				}else{
					if (ancestor.charAt(0)=='&') ancestor=ancestor.substring(1);
					 ETInit.str += ancestor + erbName + "_func, \"" + currERB.getCondition() + "\")\n";
					//ERBFunc.str += ", EBRDDFuncPointer " + ancestor + erbName + "_func";
					erbFuncs.add(ancestor + erbName + "_func");
					functionSig += "int " + ancestor + erbName + "_func()" + SCCR;
					if (currERB.getCondition().trim().toLowerCase().equals("true")){
						functions+= "int " + reference + "::" + ancestor + erbName + "_func(){\n" + currERB.getAction() + "\nreturn true;\n}\n\n";
					}else{
						functions+= "int " + reference + "::" + ancestor + erbName + "_func(){\nif(" + currERB.getCondition() + "){\n" + currERB.getAction();
						functions+= "\nreturn true;\n}else{\nreturn false;\n}\n}\n\n";
					}
				}
			} 
		}else{
			//can't handle element
		}
		return newID;
	}


	private idtypewrap getIDType(String nodeName){
		String lnodeName = nodeName.trim().toLowerCase();
		if (lnodeName.equals("kernel")) return new idtypewrap(7, ", (unsigned int)0", 1);
		idtypewrap idtype = new idtypewrap(1,"", 0);
		if (lnodeName.equals("allcpus")){
			idtype.type = 0;
			for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
				if (node instanceof HwCPU){
					idtype.id +=", (unsigned int)" + node.getID();
					idtype.nbOfIDs++;
				}
			}
			return idtype;
		}
		if (lnodeName.equals("allbuses")){
			idtype.type = 1;
			for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
				if (node instanceof HwBus){
					idtype.id +=", (unsigned int)" + node.getID();
					idtype.nbOfIDs++;
				}
			}
			return idtype;
		}
		if (lnodeName.equals("allbridges")){
			idtype.type = 3;
			for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
				if (node instanceof HwBridge){
					idtype.id +=", (unsigned int)" + node.getID();
					idtype.nbOfIDs++;
				}
			}
			return idtype;
		}
		if (lnodeName.equals("allmems")){
			idtype.type = 2;
			for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
				if (node instanceof HwMemory){
					idtype.id +=", (unsigned int)" + node.getID();
					idtype.nbOfIDs++;
				}
			}
			return idtype;
		}
		if (lnodeName.equals("allhwa")){
			idtype.type = 6;
			for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
				if (node instanceof HwA){
					idtype.id +=", (unsigned int)" + node.getID();
					idtype.nbOfIDs++;
				}
			}
			return idtype;
		}
		if (lnodeName.equals("allchannels")){
			idtype.type = 4;
			idtype.nbOfIDs = tmlmodeling.getChannels().size();
			for(TMLElement elem: tmlmodeling.getChannels()){
				idtype.id +=", (unsigned int)" + elem.getID();
			}
			return idtype;
		}
		if (lnodeName.equals("allevents")){
			idtype.type = 4;
			idtype.nbOfIDs = tmlmodeling.getEvents().size();
			for(TMLElement elem: tmlmodeling.getEvents()){
				idtype.id +=", (unsigned int)" + elem.getID();
			}
			return idtype;
		}
		if (lnodeName.equals("allrequests")){
			idtype.type = 4;
			idtype.nbOfIDs = tmlmodeling.getRequests().size();
			for(TMLElement elem: tmlmodeling.getRequests()){
				idtype.id +=", (unsigned int)" + elem.getID();
			}
			return idtype;
		}
		if (lnodeName.equals("alltasks")){
			idtype.type = 5;
			idtype.nbOfIDs = tmlmapping.getMappedTasks().size();
			for(TMLElement elem: tmlmapping.getMappedTasks()){
				idtype.id +=", (unsigned int)" + elem.getID();
			}
			return idtype;
		}
		//allCPUs, allBuses, allBridges, allMemories, allHWAs, allChannels, allEvents, allRequests, allTasks
		for(HwNode node: tmlmapping.getTMLArchitecture().getHwNodes()){
			if (node.getName().equals(nodeName)){
				if (node instanceof HwCPU){
					return new idtypewrap(0, ", (unsigned int)" + node.getID(), 1);
				}else if (node instanceof HwBus){
					return new idtypewrap(1, ", (unsigned int)" + node.getID(), 1);
				}else if (node instanceof HwBridge){
					return new idtypewrap(3, ", (unsigned int)" + node.getID(), 1);
				}else if (node instanceof HwMemory){
					return new idtypewrap(2, ", (unsigned int)" + node.getID(), 1);
				}else if (node instanceof HwA){
					return new idtypewrap(6, ", (unsigned int)" + node.getID(), 1);
				}
			}
		}
		String nodeName2 = nodeName;
		System.out.println("Name of Element: " + nodeName);
		for(TMLElement elem: tmlmodeling.getChannels()){
			if (elem.getName().equals(nodeName2)) return new idtypewrap(4, ", (unsigned int)" + elem.getID(), 1);
			System.out.println("Compare to: " + elem.getName());
		}
		for(TMLElement elem: tmlmodeling.getEvents()){
			if (elem.getName().equals(nodeName2)) return new idtypewrap(4, ", (unsigned int)" + elem.getID(), 1);
		}
		for(TMLElement elem: tmlmodeling.getRequests()){
			if (elem.getName().equals(nodeName2)) return new idtypewrap(4, ", (unsigned int)" + elem.getID(), 1);
		}
		for(TMLElement elem: tmlmapping.getMappedTasks()){
			if (elem.getName().equals(nodeName2)) return new idtypewrap(5, ", (unsigned int)" + elem.getID(), 1);
		}
		return new idtypewrap(-1,", (unsigned int)1", 1);
	}

	private int getEventCode(String eventText){
		eventText = eventText.trim().toLowerCase();
		for (int i= 0; i < events.length; i++){
			if (eventText.equals(events[i])) return i;
		}
		return -1;
	}

	private String makeAttributesCode() {
		String code = "";
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++){
			EBRDDAttribute att = ebrdd.getAttributeByIndex(i);
			if (att.hasInitialValue())
				code += ","+ att.getName() + "(" + att.getInitialValue() + ")"+CR;
			else
				code += ","+ att.getName() + "(0)"+CR;
		}	
		return code;
	}
	
	private String makeAttributesDeclaration() {
		String code = "";
		for(int i=0; i< ebrdd.getNbOfAttributes(); i++){
			code += "int " + ebrdd.getAttributeByIndex(i).getName();
			code += ";\n";
		}
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

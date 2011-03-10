/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class SystemCTEPE
 * Creation: 24/02/2011
 * @version 1.0 24/02/2011
 * @author Daniel Knorreck
 * @see
 */

package tmltranslator.tomappingsystemc2;

import java.util.*;
import tmltranslator.*;
import myutil.*;
import tepe.*;
import java.util.regex.*;


public class SystemCTEPE {
    
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	private final static Pattern _varPattern = Pattern.compile("[\\w&&\\D]+[\\w]*");
	private ArrayList<TEPE> _tepes;
	private int _nbOfFloatingSig=0,_nbOfStartNodes=0;
	private String _floatingSigProc="", _floatingSigComp="", _floatingEnaComp="", _connect="", _declare="", _evtRegister="", _code="", _listeners="", _eqFuncs="", _eqFuncDecl="";
	TML2MappingSystemC _tmltranslator;

	public SystemCTEPE(ArrayList<TEPE> tepes, TML2MappingSystemC tmltranslator){
		_tepes=tepes;
		_tmltranslator=tmltranslator;
	}
	
	public void generateTEPEs(){
		_nbOfFloatingSig=0; _nbOfStartNodes=0;
		_floatingSigProc=""; _floatingSigComp=""; _floatingEnaComp=""; _connect=""; _declare=""; _evtRegister=""; _listeners=""; _eqFuncs=""; _eqFuncDecl="";	
		if (!_tepes.isEmpty()){
			System.out.println("And the TEPEs are...............");
			for(TEPE tepe: _tepes) {
				for(TEPEComponent comp: tepe) {
					System.out.println("Component: " + comp.getName());
					generateTEPEProp(comp,null,null,null);
				}
			}
			_code = "bool aIsId;\n" + _declare;
			//TEPEFloatingSigListener(ListenerSubject<GeneralListener>* iSimulator, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc);
			_code += "TEPEFloatingSigListener* flListener = new TEPEFloatingSigListener(_simulator," + _nbOfFloatingSig + ",";
			if (_nbOfFloatingSig==0)
				_code += "0,0";
			else
				_code+= "array(" + _nbOfFloatingSig + _floatingSigComp + "),array(" + _nbOfFloatingSig + _floatingSigProc + ")";
			_code += "," + _nbOfStartNodes + ",array(" + _nbOfStartNodes + _floatingEnaComp + "))" + SCCR;
			_code += "setTEPEEntryPoint(flListener);\n";
			//_code += "SignalConstraint* opMapping[] = {0" + _floatingSigComp + "}"+SCCR;
			//_code += "NtfSigFuncPointer fnMapping[] = {0" + _floatingSigProc + "}" + SCCR;
			//_code += "PropertyConstraint* startNodes[] ={0" + _floatingEnaComp + "}" + SCCR;
			_code += _connect;
			_code += _listeners;
		}
	}
	
	public void saveFile(String filename) throws FileException{
		FileUtils.saveFile(filename, _code);
	}
	
	public String getCode(){
		return _code;
	}
	
	public String getEqFuncs(){
		return _eqFuncs;
	}
	
	public String getEqFuncDeclaration(){
		return _eqFuncDecl;
	}
	
	private String getTEPECompName(TEPEComponent currComp){
		return "_" + Conversion.replaceAllChar(currComp.getName(),' ',"") + currComp.getID();
	}
	
	private String[] getExprIDTskvarStrings(TEPEComponent currComp, String initExpr){
		String[] resultStr = new String[3];
		for(int i=0; i<3; i++)
			resultStr[i]="";
		LinkedList<String> addTokenList = new LinkedList<String>();
		ArrayList<String> addTskVarList = new ArrayList<String>();
		HashSet<Integer> addCmdIDList = new HashSet<Integer>();
		parseExprToTokenList(initExpr, addTokenList);
		if (currComp instanceof TEPEAttributeComponent){
			generateTEPEProp(currComp, addTokenList, addTskVarList, addCmdIDList);
		}else{
			for(TEPEComponent linkedComps: currComp.getInAttributes()){
				generateTEPEProp(linkedComps, addTokenList, addTskVarList, addCmdIDList);
			}
		}
		for(String anAddString: addTokenList){
			resultStr[0] += anAddString;
		}
		resultStr[1] += "array(" + addCmdIDList.size();
		for(Integer id: addCmdIDList){
			resultStr[1]+= ",(ID)" + id.toString();
		}
		resultStr[1] += ")," + addCmdIDList.size() ;
		resultStr[2] += "array(" + addTskVarList.size();
		for(String anAddTskVar: addTskVarList)
			resultStr[2] += "," + anAddTskVar;
		resultStr[2] += ")";
		return resultStr;
	}
	
	private String connectOutSignals(TEPEComponent currComp, int maxNoOfInSig, int maxNoOfNegInSig){
		String connect="";
		if (currComp.hasOutSignalComponents()){	
			for(TEPEComponent outCmp: currComp.getOutSignals()){
				int index = outCmp.getInSignals().indexOf(currComp);
				String suffix = (index==-1)? "f": ""+(index+1);
				connect += getTEPECompName(currComp) + "->connectSigOut(" + getTEPECompName(outCmp) + ",&SignalConstraint::notifyS" + suffix + ")" + SCCR;
			}
		}else
			System.out.println(getTEPECompName(currComp) + " has no out signal components");
			
		//connect floating in Signal Components
		int noOfSig= (currComp.hasInSignalComponents())? currComp.getInSignals().size():0;
		System.out.println(getTEPECompName(currComp) + " Number of sig: " + noOfSig);
		for(int i=noOfSig+1; i<=maxNoOfInSig; i++){
			_floatingSigProc += ",&SignalConstraint::notifyS" + i;
			_floatingSigComp += ",(SignalConstraint*)" + getTEPECompName(currComp);
			_nbOfFloatingSig++;
		}
		noOfSig= (currComp.hasInNegatedSignalComponents())? currComp.getInNegatedSignals().size():0;
		System.out.println(getTEPECompName(currComp) + " Number of neg sig: " + noOfSig);
		for(int i=noOfSig; i<maxNoOfNegInSig; i++){
			_floatingSigProc += ",&SignalConstraint::notifySf";
			_floatingSigComp += ",(SignalConstraint*)" + getTEPECompName(currComp);
			_nbOfFloatingSig++;
		}
		return connect;
	}
	
	private String connectOutProperties(TEPEComponent currComp){
		String connect="";
		if (currComp.hasInPropertyComponents()){
			connect += getTEPECompName(currComp) + "->connectEnaOut(array(" + currComp.getInProperties().size();
			for(TEPEComponent outCmp: currComp.getInProperties()){
				//connect += getTEPECompName(currComp) + "->connectEnaOut(array(1,(PropertyConstraint*)" + getTEPECompName(outCmp) + "),1)" + SCCR;
				connect += ",(PropertyConstraint*)" + getTEPECompName(outCmp);
			}
			connect += ")," + currComp.getInProperties().size() + ")" + SCCR;
		}else
			System.out.println(getTEPECompName(currComp) + " has no out properties.\n");
		if (!currComp.hasOutPropertyComponents()){
			_floatingEnaComp+= ",(PropertyConstraint*)" + getTEPECompName(currComp);
			_nbOfStartNodes++;
		}
		return connect;
	}
	
	private void parseExprToTokenList(String iAdd, LinkedList<String> iList){
		//LinkedList<String> resultList = new LinkedList<String>();
		Matcher matcher = _varPattern.matcher(iAdd);
		//System.out.print("Found tokens: ");
		int lastEnd=0;
		while (matcher.find()){
			String token = iAdd.substring(matcher.start(), matcher.end()).trim();
			if (matcher.start()>lastEnd) iList.add(iAdd.substring(lastEnd,matcher.start()).trim());
			iList.add(token);
			lastEnd=matcher.end();
		}
		if (lastEnd<iAdd.length()) iList.add(iAdd.substring(lastEnd,iAdd.length()).trim());
	}
	
	private void replaceTokenInList(LinkedList<String> iList, String iReplace, String iAdd){
		iReplace = iReplace.trim();
		ListIterator itr = iList.listIterator();
		LinkedList<String> addTokenList= new LinkedList<String>();
		parseExprToTokenList(iAdd,addTokenList);
		boolean aFound;
		System.out.println("Decomp in replaceTokenInList: ");
		for(String aToken: addTokenList)
			System.out.print(aToken + ", ");
		System.out.println("");
		do{
			aFound=false;
			while(!aFound && itr.hasNext()){
				aFound = ((String)itr.next()).equals(iReplace);
			}
			if (aFound){
				System.out.println("Pattern found\n");
				itr.remove();
				for(String anAddString: addTokenList){
					itr.add(anAddString);
				}
			}
		}while(itr.hasNext());
	}
	
	private String getSignalsForListener(TEPEComponent currComp){
		String result= currComp.getOutSignals().size() + ",array(" + currComp.getOutSignals().size();
		String aDstFuncs="";
		for (TEPEComponent aDstCmp: currComp.getOutSignals()){
			int index = aDstCmp.getInSignals().indexOf(currComp);
			String suffix = (index==-1)? "f": ""+(index+1);
			result += ",(SignalConstraint*)" + getTEPECompName(aDstCmp);
			aDstFuncs += ",(NtfSigFuncPointer)&SignalConstraint::notifyS" + suffix;
		}
		result += "),array(" + currComp.getOutSignals().size() + aDstFuncs + ")";
		return result;
	}
	
	private void generateTEPEProp(TEPEComponent currComp, LinkedList<String> iTokenList, ArrayList<String> iTskVarList, HashSet<Integer> iCmdIDList){
		
		if (!(currComp instanceof TEPEPropertyComponent || currComp.hasOutAttributeComponents() || currComp.hasOutSignalComponents() || currComp.hasOutPropertyComponents())) return;
		
		String cmpName =  getTEPECompName(currComp);
		
		if(currComp instanceof TEPEAttributeComponent){
			if (iTokenList==null){
				if (currComp.hasOutSignalComponents()){
					String[] decomp = getExprIDTskvarStrings(currComp, currComp.getValue());
					_eqFuncDecl+= "ParamType " + cmpName + "_func(ParamType** iVar);\n";
					_eqFuncs += "ParamType " + cmpName + "_func(ParamType** iVar){\n return " + decomp[0];
					_eqFuncs += ";\n}\n\n";
					_listeners+= "TEPESettingListener* " + cmpName + "_listener = new TEPESettingListener(" + decomp[1] + "," + decomp[2];
					_listeners+= ",&" + cmpName + "_func" + "," + getSignalsForListener(currComp);
					_listeners+= ",this,_simulator)" + SCCR;
					_listeners+= "addTEPEListener((GeneralListener*)" + cmpName + "_listener);\n";
				}
				
			}else{
				//replaceTokenInList(iTokenList, currComp.getValue(), ((TEPEAttributeComponent)currComp).getBlockName() + "x" + currComp.getValue());
				replaceTokenInList(iTokenList, currComp.getValue(), "*iVar[" + iTskVarList.size() + "]");
				//iTskVarList.add( "&task__" + ((TEPEAttributeComponent)currComp).getBlockName() + "->" + currComp.getValue());
				iTskVarList.add("getTaskByName(\""+ ((TEPEAttributeComponent)currComp).getBlockName() + "\")-> getVariableByName(\"" + currComp.getValue() + "\", aIsId)");
				//getCommandsImpactingVar(String iVarName, ArrayList<Integer> oList)
				MappedSystemCTask srcTsk = _tmltranslator.getMappedTaskByName(((TEPEAttributeComponent)currComp).getBlockName());
				if (srcTsk==null)
					System.out.println("Task not found: " + ((TEPEAttributeComponent)currComp).getBlockName());
				else{
					System.out.println("Search for Var " + currComp.getValue() +" in Task " + ((TEPEAttributeComponent)currComp).getBlockName());
					srcTsk.getAnalysis().getCommandsImpactingVar(currComp.getValue(), iCmdIDList);
				}
			}
		
		}else if (currComp instanceof TEPEAliasComponent){
			//no param
			_declare+= "AliasConstraint* " + cmpName + " = new AliasConstraint(" + currComp.getID() + ")" + SCCR;
			_connect += connectOutSignals(currComp,2,0);
		
		}else if (currComp instanceof TEPEEquationComponent){
			/*LinkedList<String> addTokenList = new LinkedList<String>();
			ArrayList<String> addTskVarList = new ArrayList<String>();
			HashSet<Integer> addCmdIDList = new HashSet<Integer>();
			parseExprToTokenList(currComp.getValue(), addTokenList);
			for(TEPEComponent linkedComps: currComp.getInAttributes()){
				System.out.println("%%%%%%%%%%% in Attribute");
				generateTEPEProp(linkedComps, addTokenList, addTskVarList, addCmdIDList);
			}*/
			String[] decomp = getExprIDTskvarStrings(currComp, currComp.getValue());
			_declare+= "EqConstraint* " + cmpName + " = new EqConstraint(" + currComp.getID() + ",GENERAL,true)" + SCCR;
			//System.out.print(cmpName + "transformed to: ");
			_eqFuncDecl+= "bool " + cmpName + "_func(ParamType** iVar);\n";
			_eqFuncs += "bool " + cmpName + "_func(ParamType** iVar){\n return " + decomp[0];
			/*for(String anAddString: addTokenList){
				System.out.print(anAddString);
				_eqFuncs += anAddString;
			}
			System.out.println();*/
			_eqFuncs += ";\n}\n\n";
			_listeners+= "TEPEEquationListener* " + cmpName + "_listener = new TEPEEquationListener(" + decomp[1] + "," + decomp[2];
			/*"array(" + addCmdIDList.size();
			for(Integer id: addCmdIDList){
				_listeners+= ",(ID)" + id.toString();
			} _listeners+= ")," + addCmdIDList.size() + "," + "array(" + addTskVarList.size();
			for(String anAddTskVar: addTskVarList)
				_listeners+= "," + anAddTskVar;*/
			_listeners+= ",&" + cmpName + "_func" + "," + cmpName + ",&SignalConstraint::notifyS1,this, _simulator)" + SCCR;
			_connect += connectOutSignals(currComp,0,0);
			_connect += connectOutProperties(currComp);
			_listeners+= "addTEPEListener((GeneralListener*)" + cmpName + "_listener);\n";
		
		}else if (currComp instanceof TEPELogicalConstraintComponent){
			//LogConstraint(PropType iType, bool iIncludeBounds)
			//SeqConstraint(PropType iType, bool iIncludeBounds)
			if (((TEPELogicalConstraintComponent)currComp).getType()==TEPELogicalConstraintComponent.SEQUENCE){
				_declare += "SeqConstraint* " + cmpName + " = new SeqConstraint("+ currComp.getID() + ",GENERAL,true)" + SCCR;
			}else{
				_declare += "LogConstraint* " + cmpName + " = new LogConstraint("+ currComp.getID() + ",GENERAL,true)" + SCCR;
			}
			//_declare += "addTEPEConstraint(" + cmpName + ");\n";
			_connect += connectOutSignals(currComp,2,1);
			_connect += connectOutProperties(currComp);
		
		}else if (currComp instanceof TEPEPropertyComponent){
			//PropLabConstraint(PropLabType iType)
			_declare += "PropLabConstraint* " + cmpName + " = new PropLabConstraint(";
			TEPEPropertyComponent propComp = (TEPEPropertyComponent)currComp;
			if (propComp.getType()==TEPEPropertyComponent.LIVENESS){
				_declare += "LIVENESS";
			}else if (propComp.getType()==TEPEPropertyComponent.NON_LIVENESS){
				_declare += "NLIVENESS";
			}else if (propComp.getType()==TEPEPropertyComponent.REACHABILITY){
				_declare += "REACHABILITY";
			}else{
				_declare += "NREACHABILITY";
			}
			_declare += ")" + SCCR;
			//_declare += "addTEPEConstraint(" + cmpName + ");\n";
			_connect += connectOutProperties(currComp);
		
		}else if (currComp instanceof TEPEPropertyOperatorComponent){
			//PropRelConstraint(PropRelType iType)
			_declare += "PropRelConstraint* " + cmpName + " = new PropRelConstraint(";
			TEPEPropertyOperatorComponent propOpComp = (TEPEPropertyOperatorComponent)currComp;
			if (propOpComp.getType()==TEPEPropertyOperatorComponent.OR){
				_declare += "OR";
			}else{
				_declare += "AND";
			}
			_declare += ")" + SCCR;
			//_connect += connectOutSignals(currComp);
			//_declare += "addTEPEConstraint(" + cmpName + ");\n";
			_connect += connectOutProperties(currComp);
		
		}else if (currComp instanceof TEPESettingComponent){
			//Variable Setting
			//TEPESettingListener::TEPESettingListener(ID* iSubjectIDs, unsigned int iNbOfSubjectIDs, ParamType** iVar, SettingFuncPointer iSetFunc, unsigned int inbOfSignals, SignalConstraint** iNotifConstr, NtfSigFuncPointer* iNotifFunc, SimComponents* iSimComp, ListenerSubject<GeneralListener>* iSimulator);
			if (iTokenList==null){
				if (currComp.hasOutSignalComponents()){
					String[] decomp = getExprIDTskvarStrings(currComp, currComp.getValue().split("=",2)[1]);
					_eqFuncDecl+= "ParamType " + cmpName + "_func(ParamType** iVar);\n";
					_eqFuncs += "ParamType " + cmpName + "_func(ParamType** iVar){\n return " + decomp[0];
					_eqFuncs += ";\n}\n\n";
					_listeners+= "TEPESettingListener* " + cmpName + "_listener = new TEPESettingListener(" + decomp[1] + "," + decomp[2];
					_listeners+= ",&" + cmpName + "_func" + "," + getSignalsForListener(currComp);
					_listeners+= ",this,_simulator)" + SCCR;
					_listeners+= "addTEPEListener((GeneralListener*)" + cmpName + "_listener);\n";
				}
			}else{
				String[] lhsrhs = currComp.getValue().split("=",2);
				//System.out.println("Replace " + lhsrhs[0] + " by " + lhsrhs[1] + " before: ");
				for(String aToken: iTokenList)
					System.out.print(aToken + ", ");
				//System.out.println("\nafter:");
				replaceTokenInList(iTokenList, lhsrhs[0], lhsrhs[1]);
				/*for(String aToken: iTokenList)
					System.out.print(aToken + ", ");
				System.out.println("");*/
				for(TEPEComponent linkedComps: currComp.getInAttributes())
					generateTEPEProp(linkedComps, iTokenList, iTskVarList, iCmdIDList);
			}
		
		}else if (currComp instanceof TEPESignalComponent){
			//Declaration of Signal?
			//TEPESigListener(ID* iSubjectIDs, unsigned int nbOfSubjectIDs, unsigned int iEvtBitmap, unsigned int iTransTypeBitmap, SignalConstraint* iNotifConstr, NtfSigFuncPointer iNotifFunc, SimComponents* iSimComp)
			if (currComp.hasOutSignalComponents()){
				String[] aTokens = currComp.getValue().split("__");
				System.out.println("name of block: "  + currComp.getValue());
				for(int i=0; i<aTokens.length; i++)
					System.out.println("A tokens [" + i + "]: " + aTokens[i]);
				System.out.println("A tokens lenght: " + aTokens.length);
				String[] aSrcIDs = aTokens[0].split("_");
				String[] aEvts = aTokens[1].split("_");
				String[] aTransTypes = aTokens[2].split("_");
				String[] aEvtDescriptor = {"SIMSTART", "SIMEND", "TIMEADV", "TASKSTART", "TASKEND", "CMDRUNNABLE", "CMDSTART", "CMDEND", "TRANSEXEC"};
				String[] aTransDescriptor = {"NONE", "EXE", "RD", "WR", "SEL", "SND", "REQ", "WAIT", "NOTIF", "ACT", "CHO", "RND", "STP"};
				_listeners+= "TEPESigListener* " + cmpName + "_listener= new TEPESigListener(array(" + (aSrcIDs.length-1);
				for(int i=1; i<aSrcIDs.length; i++)
					_listeners+= ",(ID)" + aSrcIDs[i];
				_listeners+= ")," + (aSrcIDs.length-1) + ",";
				int aEvtCode=0, aTransCode=0;
				for(int aEvtsInSigCmp=0; aEvtsInSigCmp<aEvts.length; aEvtsInSigCmp++){
					for(int aEvtDescr=0; aEvtDescr<aEvtDescriptor.length; aEvtDescr++ ){
						if (aEvts[aEvtsInSigCmp].toUpperCase().equals(aEvtDescriptor[aEvtDescr])) aEvtCode += (1<<aEvtDescr);
					}
				}
				for(int aTransInSigCmp=0; aTransInSigCmp<aTransTypes.length; aTransInSigCmp++){
					for(int aTransDescr=0; aTransDescr<aTransDescriptor.length; aTransDescr++ ){
						if (aTransTypes[aTransInSigCmp].toUpperCase().equals(aTransDescriptor[aTransDescr])) aTransCode += (1<<aTransDescr);
					}
				}
				
				_listeners+= aEvtCode + "," + aTransCode + "," + getSignalsForListener(currComp);
				/*_listeners+= currComp.getOutSignals().size() + ",array(" + currComp.getOutSignals().size();
				String aDstFuncs="";
				for (TEPEComponent aDstCmp: currComp.getOutSignals()){
					int index = aDstCmp.getInSignals().indexOf(currComp);
					String suffix = (index==-1)? "f": ""+(index+1);
					_listeners += ",(SignalConstraint*)" + getTEPECompName(aDstCmp);
					aDstFuncs += ",(NtfSigFuncPointer)&SignalConstraint::notifyS" + suffix;
				}
				_listeners += "),array(" + currComp.getOutSignals().size() + aDstFuncs + ")*/
				_listeners += ",this, _simulator)" + SCCR;
				_listeners+= "addTEPEListener((GeneralListener*)" + cmpName + "_listener);\n";
			}
			
		}else if (currComp instanceof TEPETimeConstraintComponent){
			//TimeMMConstraint(PropType iType, TMLTime iTmin, TMLTime iTmax, bool iRetrigger, bool iIncludeBounds)
			//TimeTConstraint(TMLTime iT, bool iRetrigger, bool iIncludeBounds)
			TEPETimeConstraintComponent timeConstr = (TEPETimeConstraintComponent)currComp;
			if (currComp.getInSignals().size()>1){
				String[] minMaxValues = timeConstr.getValue().split(",",2);
				_declare += "TimeMMConstraint* " + cmpName +  " = new TimeMMConstraint("+ currComp.getID() + ",GENERAL, " + minMaxValues[0] + "," + minMaxValues[1] + ",false,true)"  + SCCR;
				_connect += connectOutSignals(currComp,2,0);
			}else{
				_declare += "TimeTConstraint* " + cmpName + " =  new TimeTConstraint(" + currComp.getID() + "," + timeConstr.getValue() + ",false,true)"  + SCCR;
				_connect += connectOutSignals(currComp,1,0);
			}
			//_declare += "addTEPEConstraint(" + cmpName + ");\n";
			_connect += connectOutProperties(currComp);
		}
	}

}
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


public class SystemCTEPE {
    
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	private ArrayList<TEPE> _tepes;
	
	private String _floatingSigProc="", _floatingSigComp="", _floatingEnaComp="", _connect="", _declare="", _evtRegister="", _code="";

	public SystemCTEPE(ArrayList<TEPE> tepes){
		_tepes=tepes;
	}
	
	public void generateTEPEs(){
		_floatingSigProc=""; _floatingSigComp=""; _floatingEnaComp=""; _connect=""; _declare=""; _evtRegister="";
		System.out.println("And the TEPEs are...............");
		for(TEPE tepe: _tepes) {
			for(TEPEComponent comp: tepe) {
				System.out.println("Component: " + comp.getName());
				generateTEPEProp(comp);
			}
		}
		_code = _declare;
		_code += "SignalConstraint* opMapping[] = {0" + _floatingSigComp + "}"+SCCR;
		_code += "NtfSigFuncPointer fnMapping[] = {0" + _floatingSigProc + "}" + SCCR;
		_code += "PropertyConstraint* startNodes[] ={0" + _floatingEnaComp + "}" + SCCR;
		_code += _connect;
	}
	
	public void saveFile(String filename) throws FileException{
		FileUtils.saveFile(filename, _code);
	}
	
	private String getTEPECompName(TEPEComponent currComp){
		return "_" + Conversion.replaceAllChar(currComp.getName(),' ',"") + currComp.getID();
	}
	
	
	private String connectOutSignals(TEPEComponent currComp, int maxNoOfSig, int maxNoOfNegSig){
		String connect="";
		if (currComp.hasOutSignalComponents()){	
			for(TEPEComponent outCmp: currComp.getOutSignals()){
				int index = outCmp.getInSignals().indexOf(currComp)+1;
				connect += getTEPECompName(currComp) + ".connectSigOut(&" + getTEPECompName(outCmp) + ",&SignalConstraint::notifyS" + index + ")" + SCCR;
			}
		}
		int noOfSig= (currComp.hasInSignalComponents())? currComp.getInSignals().size():0;
		System.out.println(getTEPECompName(currComp) + " Number of sig: " + noOfSig);
		for(int i=noOfSig+1; i<=maxNoOfSig; i++){
			_floatingSigProc += ",&SignalConstraint::notifyS" + i;
			_floatingSigComp += ",&" + getTEPECompName(currComp);
		}
		noOfSig= (currComp.hasInNegatedSignalComponents())? currComp.getInNegatedSignals().size():0;
		System.out.println(getTEPECompName(currComp) + " Number of neg sig: " + noOfSig);
		for(int i=noOfSig; i<maxNoOfNegSig; i++){
			_floatingSigProc += ",&SignalConstraint::notifySf";
			_floatingSigComp += ",&" + getTEPECompName(currComp);
		}
		return connect;
	}
	
	private String connectOutProperties(TEPEComponent currComp){
		String connect="";
		if (currComp.hasOutPropertyComponents()){	
			for(TEPEComponent outCmp: currComp.getOutProperties()){
				connect += getTEPECompName(currComp) + ".connectEnaOut(array(1,(PropertyConstraint*)&" + getTEPECompName(outCmp) + ",1)" + SCCR;
			}
		}else
			System.out.println(getTEPECompName(currComp) + " has no out properties.\n");
		if (!currComp.hasInPropertyComponents()) _floatingEnaComp+= ",&" + getTEPECompName(currComp);
		return connect;
	}
	
	private void generateTEPEProp(TEPEComponent currComp){
		//labc.connectEnaOut(array(1, (PropertyConstraint*)&seqc1),1);
		//void connectSigOut(SignalConstraint* iRightConstr, NtfSigFuncPointer iNotFunc){
		String cmpName =  getTEPECompName(currComp);
		
		if(currComp instanceof TEPEAttributeComponent){
		}else if (currComp instanceof TEPEAliasComponent){
			//no param
			_declare+= "AliasConstraint " + cmpName + "()" + SCCR;
			_connect += connectOutSignals(currComp,2,0);
		}else if (currComp instanceof TEPEEquationComponent){
			//EqConstraint(PropType iType, bool iIncludeBounds)
			_declare+= "EqConstraint " + cmpName + "(GENERAL,true)" + SCCR;
			_connect += connectOutProperties(currComp);
		}else if (currComp instanceof TEPELogicalConstraintComponent){
			//LogConstraint(PropType iType, bool iIncludeBounds)
			//SeqConstraint(PropType iType, bool iIncludeBounds)
			if (((TEPELogicalConstraintComponent)currComp).getType()==TEPELogicalConstraintComponent.SEQUENCE){
				_declare += "SeqConstraint " + cmpName + "(GENERAL,true)" + SCCR;
			}else{
				_declare += "LogConstraint " + cmpName + "(GENERAL,true)" + SCCR;
			}
			_connect += connectOutSignals(currComp,2,1);
			_connect += connectOutProperties(currComp);
		}else if (currComp instanceof TEPEPropertyComponent){
			//PropLabConstraint(PropLabType iType)
			_declare += "PropLabConstraint " + cmpName + "(";
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
			_connect += connectOutProperties(currComp);
		}else if (currComp instanceof TEPEPropertyOperatorComponent){
			//PropRelConstraint(PropRelType iType)
			_declare += "PropRelConstraint " + cmpName + "(";
			TEPEPropertyOperatorComponent propOpComp = (TEPEPropertyOperatorComponent)currComp;
			if (propOpComp.getType()==TEPEPropertyOperatorComponent.OR){
				_declare += "OR";
			}else{
				_declare += "AND";
			}
			_declare += ")" + SCCR;
			//_connect += connectOutSignals(currComp);
			_connect += connectOutProperties(currComp);
		}else if (currComp instanceof TEPESettingComponent){
			//Variable Setting
		}else if (currComp instanceof TEPESignalComponent){
			//Declaration of Signal?
		}else if (currComp instanceof TEPETimeConstraintComponent){
			//TimeMMConstraint(PropType iType, TMLTime iTmin, TMLTime iTmax, bool iRetrigger, bool iIncludeBounds)
			//TimeTConstraint(TMLTime iT, bool iRetrigger, bool iIncludeBounds)
			TEPETimeConstraintComponent timeConstr = (TEPETimeConstraintComponent)currComp;
			if (currComp.getInSignals().size()>1){
				_declare += "TimeMMConstraint " + cmpName +  "(GENERAL, " + timeConstr.getMinTime() + "," + timeConstr.getMaxTime() + ",false,true)"  + SCCR;
				_connect += connectOutSignals(currComp,2,0);
			}else{
				_declare += "TimeTConstraint " + cmpName + "(" + timeConstr.getMinTime() + ",false,true)"  + SCCR;
				_connect += connectOutSignals(currComp,1,0);
			}
			_connect += connectOutProperties(currComp);
		}
	}

}
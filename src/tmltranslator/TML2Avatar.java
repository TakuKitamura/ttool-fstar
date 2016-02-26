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
 * Class AVATAR2ProVerif
 * Creation: 29/02/2016
 * @version 1.1 29/02/2016
 * @author Ludovic APVRILLE, Letitia LI
 * @see
 */

package tmltranslator;
import tmltranslator.*;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;

import javax.xml.parsers.*;
import ui.ConfigurationTTool;
import ui.CheckingError;
import ui.AvatarDesignPanel;
import ui.TGComponent;
import proverifspec.*;
import myutil.*;
import avatartranslator.*;

public class TML2Avatar {
    TMLMapping tmlmap;
    List<String> allStates;
    public TML2Avatar(TMLMapping tmlmap) {
        this.tmlmap = tmlmap;
	allStates = new ArrayList<String>();
    }
    
    public List<AvatarStateMachineElement> translateState(TMLActivityElement ae, AvatarBlock block){

        TMLActionState tmlaction;
        TMLChoice tmlchoice;
        TMLExecI tmlexeci;
        TMLExecIInterval tmlexecii;
        TMLExecC tmlexecc;
        TMLExecCInterval tmlexecci;
        TMLForLoop tmlforloop;
        TMLReadChannel tmlreadchannel;
        TMLSendEvent tmlsendevent;
        TMLSendRequest tmlsendrequest;
        TMLStopState tmlstopstate;
        TMLWaitEvent tmlwaitevent;
        TMLNotifiedEvent tmlnotifiedevent;
        TMLWriteChannel tmlwritechannel;
        TMLSequence tmlsequence;
        TMLRandomSequence tmlrsequence;
        TMLSelectEvt tmlselectevt;
        TMLDelay tmldelay;

	AvatarTransition tran= new AvatarTransition(block, "", null);
	List<AvatarStateMachineElement> elementList = new ArrayList<AvatarStateMachineElement>();

	if (ae==null){
	    return elementList;
	}

	if (ae instanceof TMLStopState){
	    AvatarStopState stops= new AvatarStopState(ae.getName(), ae.getReferenceObject());
	    elementList.add(stops);
	    return elementList;
	}
	else if (ae instanceof TMLStartState){
	    AvatarStartState ss= new AvatarStartState(ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_" + ae.getName(), null);
	    ss.addNext(tran);
	    elementList.add(ss);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLRandom){
	    AvatarRandom ar = new AvatarRandom(ae.getName(), ae.getReferenceObject());
	    TMLRandom tmlr = (TMLRandom) ae;
	    ar.setVariable(tmlr.getVariable());
	    ar.setValues(tmlr.getMinValue(), tmlr.getMaxValue());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), null);
	    ar.addNext(tran);
	    //Add to list
	    elementList.add(ar);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLSequence){
	    //Get all list of sequences and paste together
	    List<AvatarStateMachineElement> seq = translateState(ae.getNextElement(0), block);
	    elementList.addAll(seq);
	    for (int i=1; i< ae.getNbNext(); i++){
		List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block);
	 	seq.get(seq.size()-1).addNext(tmp.get(0));
		elementList.addAll(tmp);
		seq = tmp;
	    }
	    if (!(elementList.get(elementList.size()-1) instanceof AvatarStopState)){
		tran = new AvatarTransition(block, "end_seq", null);
		AvatarStopState ss = new AvatarStopState("stop", null);
		elementList.get(elementList.size()-1).addNext(tran);
	        tran.addNext(ss);
		elementList.add(tran);
		elementList.add(ss);
	    }
	    return elementList;
	    
	}
	else if (ae instanceof TMLRandomSequence){
	    HashMap<Integer, List<AvatarStateMachineElement>> seqs = new HashMap<Integer, List<AvatarStateMachineElement>>();
	    AvatarState choiceState = new AvatarState("seqchoice__"+ae.getName(), ae.getReferenceObject());
	    elementList.add(choiceState);
	    if (ae.getNbNext()==2){
		//Create 2 choices, set0 -> set1 and set1 -> set0
	      /*  for (int i=0; i< ae.getNbNext(); i++){
		    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block);
		    seqs.put(i, tmp);
	        }*/

		List<AvatarStateMachineElement> set0= translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> set1 = translateState(ae.getNextElement(1), block);
		elementList.addAll(set0);
		elementList.addAll(set1);

		//Build branch 0
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_0", null);
		choiceState.addNext(tran);
		elementList.add(tran);
		tran.addNext(set0.get(0));
		set0.get(set0.size()-1).addNext(set1.get(0));
		
	
		//Build branch 1
		List<AvatarStateMachineElement> set0_1= translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> set1_1 =translateState(ae.getNextElement(1), block);
		elementList.addAll(set0_1);
		elementList.addAll(set1_1);
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_1", null);
		elementList.add(tran);
		choiceState.addNext(tran);
		tran.addNext(set1_1.get(0));
		set1_1.get(set1_1.size()-1).addNext(set0_1.get(0));
	    }
	    else {
		for (int i=0; i< ae.getNbNext(); i++){
		    //For each of the possible state blocks, translate 1 and recurse on the remaining random sequence
		    tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, null);
		    choiceState.addNext(tran);
		    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block);
		    tran.addNext(tmp.get(0));
		    TMLRandomSequence newSeq = new TMLRandomSequence("seqchoice__"+i+"_"+ae.getNbNext()+"_"+ae.getName(), ae.getReferenceObject());
		    for (int j=0; j< ae.getNbNext(); j++){
			if (j!=i){
		            newSeq.addNext(ae.getNextElement(j));
			}
		    }
		    tran = new AvatarTransition(block, "__after_"+ae.getNextElement(i).getName(), null);
		    tmp.get(tmp.size()-1).addNext(tran);
		    elementList.addAll(tmp);
		    elementList.add(tran);
		    List<AvatarStateMachineElement> nexts = translateState(newSeq, block);
		    elementList.addAll(nexts);
		    tran.addNext(nexts.get(0));
		}
	    }
	    return elementList;
	}
	else if (ae instanceof TMLActivityElementEvent){
	    TMLActivityElementEvent aee = (TMLActivityElementEvent) ae;
	    TMLEvent ch = aee.getEvent();
	    if (ae instanceof TMLSendEvent){
		AvatarSignal sig = new AvatarSignal(ch.getName(), AvatarSignal.OUT, ch.getReferenceObject());
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), null);
	        elementList.add(as);
	        as.addNext(tran);
	        elementList.add(tran);
	    }
	    else if (ae instanceof TMLWaitEvent){
		AvatarSignal sig = new AvatarSignal(ch.getName(), AvatarSignal.IN, ch.getReferenceObject());
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), null);
	        elementList.add(as);
	        as.addNext(tran);
	        elementList.add(tran);
	    }
	    else {
		//Notify Event, I don't know how to translate this
		AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
		tran = new AvatarTransition(block, "__after_"+ae.getName(), null);
	   	as.addNext(tran);
	        elementList.add(as);
		elementList.add(tran);
	    }
	    
	}
	else if (ae instanceof TMLActivityElementWithAction){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), null);
	    tran.addAction(((TMLActivityElementWithAction) ae).getAction());
	    as.addNext(tran);
//	    AvatarActionAssignment aaa= new AvatarActionAssignment(
	    elementList.add(as);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLActivityElementWithIntervalAction){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), null);
	    as.addNext(tran);
	    elementList.add(as);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLActivityElementChannel){
	    TMLActivityElementChannel aec = (TMLActivityElementChannel) ae;
	    TMLChannel ch = aec.getChannel(0);
	    AvatarSignal sig;
	    if (ae instanceof TMLReadChannel){
		sig = new AvatarSignal(ch.getName(), AvatarSignal.IN, ch.getReferenceObject());
	    }
	    else {
		sig = new AvatarSignal(ch.getName(), AvatarSignal.OUT, ch.getReferenceObject());
	    }
	    AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	    tran= new AvatarTransition(block, "__after_"+ae.getName(), null);
	    as.addNext(tran);
	    elementList.add(as);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLForLoop){
	    TMLForLoop loop = (TMLForLoop)ae;
	    if (loop.isInfinite()){
		List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block);
	        System.out.println("looping... " + ae.getNextElement(0));
		AvatarTransition looptran = new AvatarTransition(block, "loop__"+ae.getName(), null);
		elementList.addAll(elements);
		elementList.add(looptran);
		elements.get(elements.size()-1).addNext(looptran);
		looptran.addNext(elements.get(0));
		return elementList;
	    }
	    else {
		System.out.println("why isn't my loop infinite ?");
	    }
	}
	else if (ae instanceof TMLChoice){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject(), true);
	    //Make many choices
	    elementList.add(as);
	    TMLChoice c = (TMLChoice) ae;
	    for (int i=0; i<c.getNbGuard(); i++){
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, null);
		tran.addGuard(c.getGuard(i));
		as.addNext(tran);
		List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block);
		tran.addNext(nexts.get(0));
		elementList.add(tran);
		elementList.addAll(nexts);
	    }
	    return elementList;

	}
	else if (ae instanceof TMLSelectEvt){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    //Make many choices
	    TMLSelectEvt c = (TMLSelectEvt) ae;
	    for (int i=0; i < ae.getNbNext(); i++){
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, null);
		as.addNext(tran);
		List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block);
		tran.addNext(nexts.get(0));
		elementList.addAll(nexts);
	    }
	    return elementList;
	}
	else {
	     System.out.println("undefined tml element " + ae);
	}
	List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(0), block);
	if (nexts.size()==0){
	    //in an infinite loop i hope
	    return elementList;
	}
	tran.addNext(nexts.get(0));
	elementList.addAll(nexts);
	return elementList;
    }

   public String processName(String name, int id){
	name = name.replaceAll("-","_").replaceAll(" ","");	
	if (allStates.contains(name)){
	    return name+id;

	}
	else {
	    allStates.add(name);
	    return name;
	}
   }

    public AvatarSpecification generateAvatarSpec(){
	AvatarSpecification avspec = new AvatarSpecification("spec", null);
	ArrayList<TMLTask> tasks = tmlmap.getTMLModeling().getTasks();
	for (TMLTask task:tasks){
	    AvatarBlock block = new AvatarBlock(task.getName(), avspec, task.getReferenceObject());
	    for (TMLAttribute attr: task.getAttributes()){
		AvatarType type;
		if (attr.getType().getType()==TMLType.NATURAL){
		    type = AvatarType.INTEGER;
		}
		else if (attr.getType().getType()==TMLType.BOOLEAN) {
		    type = AvatarType.BOOLEAN;
		}
		else {
		    type = AvatarType.UNDEFINED;
		}
		AvatarAttribute avattr = new AvatarAttribute(attr.getName(), type, block, null);
		block.addAttribute(avattr);
	    }	    
	    AvatarTransition last;
	    AvatarStateMachine asm = block.getStateMachine();
	    List<AvatarStateMachineElement> elementList= translateState(task.getActivityDiagram().get(0), block);
		int i=0;
	    for (AvatarStateMachineElement e: elementList){
		e.setName(processName(e.getName(), e.getID()));
		i++;
		asm.addElement(e);
	    }
	    asm.setStartState((AvatarStartState) elementList.get(0));
	 /*   for (int i=1; i<task.getActivityDiagram().nElements(); i++){
		TMLActivityElement ae= task.getActivityDiagram().get(i);
		List<AvatarStateMachineElement> elementList= translateState(ae);
		last.addNext(elementList.get(0));
		for (AvatarStateMachineElement el: elementList){
		    asm.addElement(el);
		}
		if (elementList.get(elementList.size()-1) instanceof AvatarTransition){
		    last = elementList.get(elementList.size()-1);		
		}
	    }*/


	    avspec.addBlock(block);
	   
	}
	System.out.println(avspec);
	return avspec;
    }

}

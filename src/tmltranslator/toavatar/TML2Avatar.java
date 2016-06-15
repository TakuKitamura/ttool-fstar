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

package tmltranslator.toavatar;
import tmltranslator.*;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;

import javax.xml.parsers.*;
import ui.ConfigurationTTool;
import ui.CheckingError;
import ui.AvatarDesignPanel;
import ui.tmlcompd.*;
import ui.tmlad.*;
import ui.TGComponent;
import proverifspec.*;
import myutil.*;
import avatartranslator.*;

public class TML2Avatar {
    TMLMapping tmlmap;
    TMLModeling tmlmodel;

    LinkedList<AvatarAttribute> keys = new LinkedList<AvatarAttribute>();
    public HashMap<TMLChannel, Integer> channelMap = new HashMap<TMLChannel,Integer>();
    public HashMap<TMLTask, AvatarBlock> taskBlockMap = new HashMap<TMLTask, AvatarBlock>();  
    public HashMap<String, Integer> originDestMap = new HashMap<String, Integer>();
    HashMap<String, AvatarSignal> signalMap = new HashMap<String, AvatarSignal>();
    public HashMap<String, Object> stateObjectMap = new HashMap<String, Object>();

    HashMap<String, String> secChannelMap = new HashMap<String, String>();

    HashMap<String, AvatarAttributeState> signalAuthOriginMap = new HashMap<String, AvatarAttributeState>();
    HashMap<String, AvatarAttributeState> signalAuthDestMap = new HashMap<String, AvatarAttributeState>();

    public ArrayList<SecurityPattern> secPatterns = new ArrayList<SecurityPattern>();

    List<AvatarSignal> signals = new ArrayList<AvatarSignal>();
    private final static Integer channelPublic = 0;
    private final static Integer channelPrivate = 1;
    private final static Integer channelUnreachable = 2;
    public int loopLimit = 1;
    AvatarSpecification avspec;
    ArrayList<String> attrsToCheck;
    List<String> allStates;
    public TML2Avatar(TMLMapping tmlmap) {
        this.tmlmap = tmlmap;
	this.tmlmodel = tmlmap.getTMLModeling();
	allStates = new ArrayList<String>();
	attrsToCheck=new ArrayList<String>();
    }
    
    public void checkConnections(){
	for (TMLTask t1:tmlmodel.getTasks()){
	    for (TMLTask t2:tmlmodel.getTasks()){
		HwExecutionNode node1 = (HwExecutionNode) tmlmap.getHwNodeOf(t1);
		HwExecutionNode node2 = (HwExecutionNode) tmlmap.getHwNodeOf(t2);
		if (node1==node2){
		    originDestMap.put(t1.getName()+"__"+t2.getName(), channelPrivate);
		}
		if (node1!=node2){
		    //Navigate architecture for node
		    List<HwLink> links = tmlmap.getTMLArchitecture().getHwLinks();
		    HwNode last = node1;
		    List<HwNode> found = new ArrayList<HwNode>();	
		    List<HwNode> done = new ArrayList<HwNode>();
		    List<HwNode> path = new ArrayList<HwNode>();
		    Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
		    for (HwLink link: links){
			if (link.hwnode == node1){
			    found.add(link.bus);
			    List<HwNode> tmp = new ArrayList<HwNode>();
			    tmp.add(link.bus);
			    pathMap.put(link.bus, tmp);
			}
		    }
		    outerloop:
		        while (found.size()>0){
			    HwNode curr = found.remove(0);
			    for (HwLink link: links){
			        if (curr == link.bus){
			    	    if (link.hwnode == node2){
			      		path = pathMap.get(curr);
			      		break outerloop;
			    	    }
			    	    if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
			      		found.add(link.hwnode);
			      		List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
			      		tmp.add(link.hwnode);
			      		pathMap.put(link.hwnode, tmp);
			    	    }
			  	}
			        else if (curr == link.hwnode){
			      	    if (!done.contains(link.bus) && !found.contains(link.bus)){
			        	found.add(link.bus);
			        	List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
			        	tmp.add(link.bus);
			        	pathMap.put(link.bus, tmp);
			      	    }
			  	}
			    }
			    done.add(curr);
		      }
		      if (path.size() ==0){
			  originDestMap.put(t1.getName()+"__"+t2.getName(), channelUnreachable);
		      }
		      else {
			  int priv=1;
			  HwBus bus;
			  //Check if all buses and bridges are private
			  for (HwNode n: path){
			      if (n instanceof HwBus){
			          bus = (HwBus) n;
			    	  if (bus.privacy ==0){
			      	      priv=0;
					break;
			    	  }
			      }
			  }
			  originDestMap.put(t1.getName()+"__"+t2.getName(), priv);
		     }
		}
	    }
	}
    }
    public void checkChannels(){
	ArrayList<TMLChannel> channels = tmlmodel.getChannels();
	List<TMLTask> destinations = new ArrayList<TMLTask>();
	TMLTask a; 
	for (TMLChannel channel: channels){	
	    destinations.clear();
	    if (channel.isBasicChannel()){
	        a = channel.getOriginTask();
		destinations.add(channel.getDestinationTask());
	    }
	    else {
		a=channel.getOriginTasks().get(0);
		destinations.addAll(channel.getDestinationTasks());
	    }  
	    HwExecutionNode node1 = (HwExecutionNode) tmlmap.getHwNodeOf(a);
	    for (TMLTask t: destinations){
	        List<HwBus> buses = new ArrayList<HwBus>();
		HwNode node2 = tmlmap.getHwNodeOf(t);
		if (node1==node2){
		    channelMap.put(channel, channelPrivate);
		}
		if (node1!=node2){
		    //Navigate architecture for node
		    List<HwLink> links = tmlmap.getTMLArchitecture().getHwLinks();
		    HwNode last = node1;
		    List<HwNode> found = new ArrayList<HwNode>();	
		    List<HwNode> done = new ArrayList<HwNode>();
		    List<HwNode> path = new ArrayList<HwNode>();
		    Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
		    for (HwLink link: links){
			if (link.hwnode == node1){
			    found.add(link.bus);
			    List<HwNode> tmp = new ArrayList<HwNode>();
			    tmp.add(link.bus);
			    pathMap.put(link.bus, tmp);
			}
		    }
		    outerloop:
		        while (found.size()>0){
			    HwNode curr = found.remove(0);
			    for (HwLink link: links){
			        if (curr == link.bus){
			    	    if (link.hwnode == node2){
			      		path = pathMap.get(curr);
			      		break outerloop;
			    	    }
			    	    if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
			      		found.add(link.hwnode);
			      		List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
			      		tmp.add(link.hwnode);
			      		pathMap.put(link.hwnode, tmp);
			    	    }
			  	}
			        else if (curr == link.hwnode){
			      	    if (!done.contains(link.bus) && !found.contains(link.bus)){
			        	found.add(link.bus);
			        	List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
			        	tmp.add(link.bus);
			        	pathMap.put(link.bus, tmp);
			      	    }
			  	}
			    }
			    done.add(curr);
		      }
		      if (path.size() ==0){
			//  System.out.println("Path does not exist for channel " + channel.getName() + " between Task " + a.getTaskName() + " and Task " + t.getTaskName());
			  channelMap.put(channel, channelUnreachable);
		      }
		      else {
			  int priv=1;
			  HwBus bus;
			  //Check if all buses and bridges are private
			  for (HwNode n: path){
			      if (n instanceof HwBus){
			          bus = (HwBus) n;
			    	  if (bus.privacy ==0){
			      	      priv=0;
					break;
			    	  }
			      }
			  }
			  channelMap.put(channel, priv);
			  //System.out.println("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is " + (priv==1 ? "confidential" : "not confidential"));
		      }
		}
	    }
	}  
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
	    tran = new AvatarTransition(block, "__after_" + ae.getName(), ss.getReferenceObject());
	    ss.addNext(tran);
	    elementList.add(ss);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLRandom){
	    AvatarRandom ar = new AvatarRandom(ae.getName(), ae.getReferenceObject());
	    TMLRandom tmlr = (TMLRandom) ae;
	    ar.setVariable(tmlr.getVariable());
	    ar.setValues(tmlr.getMinValue(), tmlr.getMaxValue());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    ar.addNext(tran);
	    //Add to list
	    elementList.add(ar);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLSequence){
	    //Get all list of sequences and paste together
	    List<AvatarStateMachineElement> seq = translateState(ae.getNextElement(0), block);
	    List<AvatarStateMachineElement> tmp;
	   // elementList.addAll(seq);
	    //get rid of any stops in the middle of the sequence and replace with the start of the next sequence
	    for (int i=1; i< ae.getNbNext(); i++){
		tmp = translateState(ae.getNextElement(i), block);
		for (AvatarStateMachineElement e: seq){
		    if (e instanceof AvatarStopState){
			//ignore
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(tmp.get(0));
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(tmp.get(0));
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}
		//elementList.addAll(tmp);
		seq = tmp;
	    }
	    //Put stop states on the end of the last in sequence
	    
	    for (AvatarStateMachineElement e: seq){
		if (e.getNexts().size()==0 && !(e instanceof AvatarStopState)){
		    AvatarStopState stop = new AvatarStopState("stop", null);
		    e.addNext(stop);
	    	    elementList.add(stop);
		}
		elementList.add(e);
	    }
	    return elementList;
	    
	}
	else if (ae instanceof TMLSendRequest){
	    TMLSendRequest sr= (TMLSendRequest) ae;
	    TMLRequest req = sr.getRequest();
	    AvatarSignal sig;
	    AvatarState signalState = new AvatarState("signalstate_"+ae.getName()+"_"+req.getName(),ae.getReferenceObject(), ((TGComponent)ae.getReferenceObject()).getCheckableAccessibility());
	    AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_"+ae.getName()+"_"+req.getName(), ae.getReferenceObject());
	    if (!signalMap.containsKey(block.getName()+"__OUT__"+req.getName())){
	        sig = new AvatarSignal(block.getName()+"__OUT__"+req.getName(), AvatarSignal.OUT, req.getReferenceObject());
	        signals.add(sig);
		signalMap.put(block.getName()+"__OUT__"+req.getName(), sig);
	        block.addSignal(sig);
	    }
	    else {
		sig=signalMap.get(block.getName()+"__OUT__"+req.getName());
	    }

	    AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	    for (int i=0; i<sr.getNbOfParams(); i++){
		if (block.getAvatarAttributeWithName(sr.getParam(i))==null){
		    //Throw Error
		    System.out.println("Missing Attribute " + sr.getParam(i));
		    as.addValue("tmp");
		}
		else {
		    as.addValue(sr.getParam(i));
		}
	    }
	    //Create new value to send....
	    AvatarAttribute requestData= new AvatarAttribute(req.getName()+"__reqData", AvatarType.INTEGER, block, null);
	    as.addValue(req.getName()+"__reqData");
	    if (block.getAvatarAttributeWithName(req.getName()+"__reqData")==null){
	    	block.addAttribute(requestData);	
	    }
	    if (req.checkAuth){
		AvatarAttributeState authOrig = new AvatarAttributeState(req.getName()+"__origin",ae.getReferenceObject(),requestData, signalState);
		signalAuthOriginMap.put(req.getName(), authOrig);
	    }
	    tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    elementList.add(signalState);
	    signalState.addNext(signalTran);
	    elementList.add(signalTran);	
	    signalTran.addNext(as);    
	    elementList.add(as);
	    as.addNext(tran);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLRandomSequence){
	    HashMap<Integer, List<AvatarStateMachineElement>> seqs = new HashMap<Integer, List<AvatarStateMachineElement>>();
	    AvatarState choiceState = new AvatarState("seqchoice__"+ae.getName(), ae.getReferenceObject());
	    elementList.add(choiceState);
	    if (ae.getNbNext()==2){
		List<AvatarStateMachineElement> set0= translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> set1 = translateState(ae.getNextElement(1), block);
//		elementList.addAll(set0);

		//Remove stop states of sets and route their transitions to the first element of the following sequence
		for (AvatarStateMachineElement e: set0){
		    if (e instanceof AvatarStopState){
			//ignore
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(set1.get(0));
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(set1.get(0));
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}


		//Build branch 0
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_0", ae.getReferenceObject());
		choiceState.addNext(tran);
		elementList.add(tran);
		tran.addNext(set0.get(0));
		//Put stop states at the end of set1 if they don't already exist
		AvatarStopState stop = new AvatarStopState("stop", null);
		for (AvatarStateMachineElement e: set1){
		    if (e.getNexts().size()==0 && (e instanceof AvatarTransition)){
			e.addNext(stop);		
		    }
		    elementList.add(e);
		}
		elementList.add(stop);
		
		//Build branch 1
		List<AvatarStateMachineElement> set0_1 = translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> set1_1 = translateState(ae.getNextElement(1), block);
		for (AvatarStateMachineElement e: set1_1){
		    if (e instanceof AvatarStopState){
			//ignore
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(set0_1.get(0));
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(set0_1.get(0));
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_1", ae.getReferenceObject());
		elementList.add(tran);
		choiceState.addNext(tran);
		tran.addNext(set1_1.get(0));
		stop = new AvatarStopState("stop", null);
		for (AvatarStateMachineElement e: set0_1){
		    if (e.getNexts().size()==0 && (e instanceof AvatarTransition)){
			e.addNext(stop);		
		    }
		    elementList.add(e);
		}
		elementList.add(stop);
		
	    }
	    else {
		//This gets really complicated in ProVerif
		for (int i=0; i< ae.getNbNext(); i++){
		    //For each of the possible state blocks, translate 1 and recurse on the remaining random sequence
		    tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, ae.getReferenceObject());
		    choiceState.addNext(tran);
		    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block);
		    tran.addNext(tmp.get(0));
		    TMLRandomSequence newSeq = new TMLRandomSequence("seqchoice__"+i+"_"+ae.getNbNext()+"_"+ae.getName(), ae.getReferenceObject());
		    for (int j=0; j< ae.getNbNext(); j++){
			if (j!=i){
		            newSeq.addNext(ae.getNextElement(j));
			}
		    }
		    tran = new AvatarTransition(block, "__after_"+ae.getNextElement(i).getName(), ae.getReferenceObject());
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
	    TMLEvent evt = aee.getEvent();
	    AvatarState signalState = new AvatarState("signalstate_"+ae.getName()+"_"+evt.getName(),ae.getReferenceObject(), ((TGComponent) ae.getReferenceObject()).getCheckableAccessibility());
	    AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_"+ae.getName()+"_"+evt.getName(), ae.getReferenceObject());
	    if (ae instanceof TMLSendEvent){
		AvatarSignal sig;
		if (!signalMap.containsKey(block.getName()+"__OUT__"+evt.getName())){
	            sig = new AvatarSignal(block.getName()+"__OUT__"+evt.getName(), AvatarSignal.OUT, evt.getReferenceObject());
	            signals.add(sig);
		    block.addSignal(sig);
		    signalMap.put(block.getName()+"__OUT__"+evt.getName(), sig);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__OUT__"+evt.getName());
	    	}
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
		for (int i=0; i< aee.getNbOfParams(); i++){
		    if (block.getAvatarAttributeWithName(aee.getParam(i))==null){
		    	//Throw Error
			as.addValue("tmp");
		    	System.out.println("Missing Attribute " + aee.getParam(i));
		    }
		    else {
		        as.addValue(aee.getParam(i));
		    }
		}
	    	
 		AvatarAttribute eventData= new AvatarAttribute(evt.getName()+"__eventData", AvatarType.INTEGER, block, null);
	        as.addValue(evt.getName()+"__eventData");
		if (block.getAvatarAttributeWithName(evt.getName()+"__eventData")==null){
	            block.addAttribute(eventData);
		}
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
		elementList.add(signalState);
		signalState.addNext(signalTran);
		elementList.add(signalTran);	
		signalTran.addNext(as);        
		elementList.add(as);
	        as.addNext(tran);
	        elementList.add(tran);
		
		
	    }
	    else if (ae instanceof TMLWaitEvent){
		AvatarSignal sig; 
		if (!signalMap.containsKey(block.getName()+"__IN__"+evt.getName())){
	            sig = new AvatarSignal(block.getName()+"__IN__"+evt.getName(), AvatarSignal.IN, evt.getReferenceObject());
	            signals.add(sig);
		    block.addSignal(sig);
		    signalMap.put(block.getName()+"__IN__"+evt.getName(), sig);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__IN__"+evt.getName());
	    	}
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
		for (int i=0; i< aee.getNbOfParams(); i++){
		    if (block.getAvatarAttributeWithName(aee.getParam(i))==null){
		        //Throw Error
			as.addValue("tmp");
		    	System.out.println("Missing Attribute " + aee.getParam(i));
		    }
		    else {
			as.addValue(aee.getParam(i));
		    }
		}
 		AvatarAttribute eventData= new AvatarAttribute(evt.getName()+"__eventData", AvatarType.INTEGER, block, null);
	    	as.addValue(evt.getName()+"__eventData");
		if (block.getAvatarAttributeWithName(evt.getName()+"__eventData")==null){
	    	    block.addAttribute(eventData);
		}
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
		elementList.add(signalState);
		signalState.addNext(signalTran);
		elementList.add(signalTran);	
		signalTran.addNext(as);    
	        elementList.add(as);
	        as.addNext(tran);
	        elementList.add(tran);
	    }
	    else {
		//Notify Event, I don't know how to translate this
		AvatarRandom as = new AvatarRandom(ae.getName(), ae.getReferenceObject());
		tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
		as.setVariable(aee.getVariable());
		as.setValues("0", "1");
	   	as.addNext(tran);
	        elementList.add(as);
		elementList.add(tran);
	    }
	    
	}
	else if (ae instanceof TMLActivityElementWithAction){
	    //Might be encrypt or decrypt
	    AvatarState as = new AvatarState(ae.getValue()+"_"+ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    //For now, get rid of the action. It won't translate anyway
	    //tran.addAction(((TMLActivityElementWithAction) ae).getAction());
	    if (ae.securityPattern!=null && ae.getName().contains("encrypt")){
		secPatterns.add(ae.securityPattern);

		if (ae.securityPattern.type.equals("Symmetric Encryption")){

		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null));

		    AvatarMethod sencrypt = new AvatarMethod("sencrypt", ae);
		    sencrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
		    sencrypt.addParameter(block.getAvatarAttributeWithName("key"));
		    block.addMethod(sencrypt);
		    tran.addAction(ae.securityPattern.name+"_encrypted = sencrypt("+ae.securityPattern.name+", key)");

		    ae.securityPattern.originTask=block.getName();
		    ae.securityPattern.state1=as;
		}
		else if (ae.securityPattern.type.equals("Asymmetric Encryption")){
		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null));

		    AvatarMethod sencrypt = new AvatarMethod("aencrypt", ae);
		    sencrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
		    sencrypt.addParameter(block.getAvatarAttributeWithName("publickey"));
		    block.addMethod(sencrypt);
		    tran.addAction(ae.securityPattern.name+"_encrypted = aencrypt("+ae.securityPattern.name+", key)");

		    ae.securityPattern.originTask=block.getName();
		    ae.securityPattern.state1=as;
		}
		AvatarAttributeState authOrigin = new AvatarAttributeState(ae.securityPattern.name+"1",ae.getReferenceObject(),block.getAvatarAttributeWithName(ae.securityPattern.name), as);
		signalAuthOriginMap.put(ae.securityPattern.name, authOrigin);
	    	as.addNext(tran);
	    	elementList.add(as);
	    	elementList.add(tran);
	    }	
	    else if (ae.securityPattern!=null && ae.getName().contains("decrypt")){
		if (ae.securityPattern.type.equals("Symmetric Encryption")){

		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
		    block.addAttribute(new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null));

		    AvatarMethod sdecrypt = new AvatarMethod("sdecrypt", ae);
		    sdecrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
		    sdecrypt.addParameter(block.getAvatarAttributeWithName("key"));
		    block.addMethod(sdecrypt);


		    tran.addAction(ae.securityPattern.name+" = sdecrypt("+ae.securityPattern.name+"_encrypted, key)");


		    ae.securityPattern.state2=as;
	

		    AvatarState dummy = new AvatarState(ae.getName()+"_dummy", ae.getReferenceObject());
		    tran.addNext(dummy);
	    	    tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
		    dummy.addNext(tran);
	    	    elementList.add(dummy);
	    	    elementList.add(tran);		
		    AvatarAttributeState authDest = new AvatarAttributeState(ae.securityPattern.name+"2",ae.getReferenceObject(),block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
		    signalAuthDestMap.put(ae.securityPattern.name, authDest);
		}
	    	elementList.add(as);
	    	elementList.add(tran);
		as.addNext(tran);
	    }
	    else {
	    	as.addNext(tran);
	    	elementList.add(as);
	    	elementList.add(tran);
	    }
	}
	else if (ae instanceof TMLActivityElementWithIntervalAction){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    as.addNext(tran);
	    elementList.add(as);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLActivityElementChannel){
	    TMLActivityElementChannel aec = (TMLActivityElementChannel) ae;
	    TMLChannel ch = aec.getChannel(0);
	    AvatarSignal sig;
	    AvatarState signalState = new AvatarState("signalstate_"+ae.getName()+"_"+ch.getName(),ae.getReferenceObject(), ((TGComponent)ae.getReferenceObject()).getCheckableAccessibility());
	    AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_"+ae.getName()+"_"+ch.getName(), ae.getReferenceObject());
	    if (ae instanceof TMLReadChannel){
		if (!signalMap.containsKey(block.getName()+"__IN__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__IN__"+ch.getName(), AvatarSignal.IN, ch.getReferenceObject());
	            signals.add(sig);
		    signalMap.put(block.getName()+"__IN__"+ch.getName(), sig);
		    block.addSignal(sig);
	    	    AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
		    if (block.getAvatarAttributeWithName(ch.getName()+"__chData")==null){
	    	        block.addAttribute(channelData);
		    }
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__IN__"+ch.getName());
		}
		AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
		
		if (ae.securityPattern!=null){
		    secChannelMap.put(ae.securityPattern.name,ch.getName());
		    as.addValue(ae.securityPattern.name+"_encrypted");
		    AvatarAttribute data= new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null);
		    block.addAttribute(data);
		}
		else {
	    	as.addValue(ch.getName()+"__chData");
		}

	    	tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    	elementList.add(signalState);
	    	signalState.addNext(signalTran);
	    	elementList.add(signalTran);	
	    	signalTran.addNext(as);    
	    	as.addNext(tran);
	    	elementList.add(as);
	    	elementList.add(tran);
	        if (ch.checkAuth){
		    //Add aftersignal state
		    AvatarState afterSignalState = new AvatarState("aftersignalstate_"+ae.getName()+"_"+ch.getName(),ae.getReferenceObject());
		    tran.addNext(afterSignalState);
		    tran = new AvatarTransition(block, "__aftersignalstate_"+ae.getName(), ae.getReferenceObject());
		    afterSignalState.addNext(tran);
		    elementList.add(afterSignalState);
		    elementList.add(tran);
		    if (block.getAvatarAttributeWithName(ch.getName()+"__chData")==null){
		        AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
	    	        block.addAttribute(channelData);
		    }
		    AvatarAttributeState authDest = new AvatarAttributeState(ch.getName()+"__destination",ae.getReferenceObject(),block.getAvatarAttributeWithName(ch.getName()+"__chData"), afterSignalState);
		    signalAuthDestMap.put(ch.getName(), authDest);
	    	}
	    }
	    else {
		//WriteChannel
		if (!signalMap.containsKey(block.getName()+"__OUT__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__OUT__"+ch.getName(), AvatarSignal.OUT, ch.getReferenceObject());
	            signals.add(sig);
	    	    block.addSignal(sig);
		    signalMap.put(block.getName()+"__OUT__"+ch.getName(), sig);
		    AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
		    if (block.getAvatarAttributeWithName(ch.getName()+"__chData")==null){
	    	        block.addAttribute(channelData);
		    }
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__OUT__"+ch.getName());
	    	}
	        if (ch.checkConf){
		    LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
		    if (!attrsToCheck.contains(ch.getName()+"__chData")){
		        attrs.add(new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null));
		    	attrsToCheck.add(ch.getName()+"__chData");
		    	avspec.addPragma(new AvatarPragmaSecret("#Confidentiality "+block.getName() + "."+ch.getName()+"__chData", ch.getReferenceObject(), attrs));
	            }
	    	}
	        if (ch.checkAuth){
		    if (block.getAvatarAttributeWithName(ch.getName()+"__chData")==null){
		        AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
	    	        block.addAttribute(channelData);
		    }
		    AvatarAttributeState authOrigin = new AvatarAttributeState(ch.getName()+"__destination",ae.getReferenceObject(),block.getAvatarAttributeWithName(ch.getName()+"__chData"), signalState);
		    signalAuthOriginMap.put(ch.getName(), authOrigin);
	    	}  
	    	AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());

		if (ae.securityPattern!=null){
		    as.addValue(ae.securityPattern.name+"_encrypted");
		    AvatarAttribute data= new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null);
		    block.addAttribute(data);
		    secChannelMap.put(ae.securityPattern.name,ch.getName());
		}
		else {
	  	   //No security pattern
	    	    as.addValue(ch.getName()+"__chData");
		}

	    	tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    	elementList.add(signalState);
	    	signalState.addNext(signalTran);
	    	elementList.add(signalTran);	
	    	signalTran.addNext(as);    
	    	as.addNext(tran);
	    	elementList.add(as);
	    	elementList.add(tran);
	    } 
	}
	else if (ae instanceof TMLForLoop){
	    TMLForLoop loop = (TMLForLoop)ae;
	    if (loop.isInfinite()){
		//Make initializaton, then choice state with transitions
		List<AvatarStateMachineElement> elements=translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> afterloop = translateState(ae.getNextElement(1), block);
		AvatarState initState = new AvatarState(ae.getName()+"__init", ae.getReferenceObject());
		elementList.add(initState);
		//Build transition to choice
		tran = new AvatarTransition(block, "loop_init__"+ae.getName(), ae.getReferenceObject());
		tran.addAction(AvatarTerm.createActionFromString(block, "loop_index=0"));
		elementList.add(tran);
		initState.addNext(tran);
		//Choice state
		AvatarState as = new AvatarState(ae.getName()+"__choice", ae.getReferenceObject());
		elementList.add(as);
		tran.addNext(as);
		//transition to first element of loop
		tran = new AvatarTransition(block, "loop_increment__"+ae.getName(), ae.getReferenceObject());
		//Set default loop limit guard
		tran.setGuard(AvatarGuard.createFromString(block, "loop_index != "+loopLimit));
		tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
		tran.addNext(elements.get(0));
		as.addNext(tran);
		elementList.add(tran);
		//Process elements in loop to remove stop states and empty transitions, and loop back to choice
		for (AvatarStateMachineElement e: elements){
		    if (e instanceof AvatarStopState){
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(as);
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(as);
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}
		
		//Transition if exiting loop
		tran=new AvatarTransition(block, "end_loop__"+ae.getName(), ae.getReferenceObject());
		tran.setGuard(new AvatarGuardElse());
		as.addNext(tran);
		AvatarStopState stop = new AvatarStopState("stop", null);
		tran.addNext(stop);
		elementList.add(tran);
		elementList.add(stop);
		return elementList;
	    }
	    else {
		//Make initializaton, then choice state with transitions
		List<AvatarStateMachineElement> elements=translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> afterloop = translateState(ae.getNextElement(1), block);
		AvatarState initState = new AvatarState(ae.getName()+"__init", ae.getReferenceObject());
		elementList.add(initState);
		//Build transition to choice
		tran = new AvatarTransition(block, "loop_init__"+ae.getName(), ae.getReferenceObject());
		tran.addAction(AvatarTerm.createActionFromString(block, loop.getInit()));
		tran.addAction(AvatarTerm.createActionFromString(block, "loop_index=0"));
		elementList.add(tran);
		initState.addNext(tran);
		//Choice state
		AvatarState as = new AvatarState(ae.getName()+"__choice", ae.getReferenceObject());
		elementList.add(as);
		tran.addNext(as);
		//transition to first element of loop
		tran = new AvatarTransition(block, "loop_increment__"+ae.getName(), ae.getReferenceObject());
		//Set default loop limit guard
		tran.setGuard(AvatarGuard.createFromString(block, "loop_index != "+loopLimit));
		AvatarGuard guard = AvatarGuard.createFromString (block, loop.getCondition().replaceAll("<", "!="));
                int error = AvatarSyntaxChecker.isAValidGuard(avspec, block, loop.getCondition().replaceAll("<","!="));
                if (error != 0) {
                    tran.addGuard(loop.getCondition().replaceAll("<", "!="));
                }
		tran.addAction(AvatarTerm.createActionFromString(block, loop.getIncrement()));
		tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
		tran.addNext(elements.get(0));
		as.addNext(tran);
		elementList.add(tran);
		//Process elements in loop to remove stop states and empty transitions, and loop back to choice
		for (AvatarStateMachineElement e: elements){
		    if (e instanceof AvatarStopState){
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(as);
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(as);
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}
		
		//Transition if exiting loop
		tran=new AvatarTransition(block, "end_loop__"+ae.getName(), ae.getReferenceObject());
		tran.setGuard(new AvatarGuardElse());
		as.addNext(tran);
		if (afterloop.size()==0){
		    afterloop.add(new AvatarStopState("stop", null));
		}
		tran.addNext(afterloop.get(0));
		elementList.add(tran);
		elementList.addAll(afterloop);
		return elementList;
	    }
	}
	else if (ae instanceof TMLChoice){
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    //Make many choices
	    elementList.add(as);
	    TMLChoice c = (TMLChoice) ae;
	    for (int i=0; i<c.getNbGuard(); i++){
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, ae.getReferenceObject());
		//tran.setGuard(c.getGuard(i));
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
	    elementList.add(as);
	    //Make many choices
	    TMLSelectEvt c = (TMLSelectEvt) ae;
	    for (int i=0; i < ae.getNbNext(); i++){
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, ae.getReferenceObject());
		as.addNext(tran);
		List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block);
		tran.addNext(nexts.get(0));
		elementList.add(tran);
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
/*    public AvatarPragma generatePragma(String[] s){
	
    }*/

    public AvatarSpecification generateAvatarSpec(String _loopLimit){
	//TODO: Add pragmas
	//TODO: Make state names readable
	//TODO: Put back numeric guards
	//TODO: Calcuate for temp variable
	//TODO: Cry
	this.avspec = new AvatarSpecification("spec", tmlmap.getTMLCDesignPanel());
	if (tmlmap.getTMLCDesignPanel()==null){
	    System.out.println("Failed to generate specification");
	    return avspec;
	}
	attrsToCheck.clear();
	
	//Only set the loop limit if it's a number
	String pattern = "^[0-9]{1,2}$";
	Pattern r = Pattern.compile(pattern);
	Matcher m = r.matcher(_loopLimit);
	if (m.find()){
	    loopLimit = Integer.valueOf(_loopLimit);
	}
	for (TMLChannel channel: tmlmodel.getChannels()){
	    for (TMLCPrimitivePort p: channel.ports){
	        channel.checkConf = channel.checkConf || p.checkConf;
		channel.checkAuth = channel.checkAuth || p.checkAuth;
	    }
	}
	for (TMLEvent event: tmlmodel.getEvents()){
	    event.checkConf = event.port.checkConf || event.port2.checkConf;
	    event.checkAuth = event.port.checkAuth || event.port2.checkAuth;
	}
	for (TMLRequest request: tmlmodel.getRequests()){
	    for (TMLCPrimitivePort p: request.ports){
		request.checkConf = p.checkConf || request.checkConf;
		request.checkAuth = p.checkAuth || request.checkAuth;
	    }
	}

	ArrayList<TMLTask> tasks = tmlmap.getTMLModeling().getTasks();
	for (TMLTask task:tasks){



	
	    AvatarBlock block = new AvatarBlock(task.getName(), avspec, task.getReferenceObject());
	    taskBlockMap.put(task, block);
	    //Add temp variable for unsendable signals
	    AvatarAttribute tmp = new AvatarAttribute("tmp", AvatarType.INTEGER, block, null);
	    block.addAttribute(tmp);
	    AvatarAttribute key = new AvatarAttribute("key", AvatarType.INTEGER, block, null);
	    keys.add(key);
	    block.addAttribute(key);
	 /*   tmp = new AvatarAttribute("aliceandbob", AvatarType.INTEGER, block, null);
	    block.addAttribute(tmp);
	    tmp = new AvatarAttribute("aliceandbob_encrypted", AvatarType.INTEGER, block, null);
	    block.addAttribute(tmp);*/
	    AvatarAttribute loop_index = new AvatarAttribute("loop_index", AvatarType.INTEGER, block, null);
	    block.addAttribute(loop_index);
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
	    
	    //TODO: Create a fork with many requests. This looks terrible
	    if (tmlmodel.getRequestToMe(task)!=null){
		//Create iteration attribute
		AvatarAttribute req_loop_index= new AvatarAttribute("req_loop_index", AvatarType.INTEGER, block, null);
		block.addAttribute(req_loop_index);

		//TMLRequest request= tmlmodel.getRequestToMe(task);
		//Oh this is fun...let's restructure the state machine
		//Create own start state, and ignore the returned one
		List<AvatarStateMachineElement> elementList= translateState(task.getActivityDiagram().get(0), block);
		AvatarStartState ss = new AvatarStartState("start", task.getActivityDiagram().get(0).getReferenceObject());
		asm.addElement(ss);
		AvatarTransition at= new AvatarTransition(block, "__after_start", task.getActivityDiagram().get(0).getReferenceObject());
		at.addAction(AvatarTerm.createActionFromString(block, "req_loop_index = 0"));
		ss.addNext(at);
		asm.addElement(at);
		
		AvatarState loopstart = new AvatarState("loopstart", task.getActivityDiagram().get(0).getReferenceObject());
		at.addNext(loopstart);	
		asm.addElement(loopstart);

		//Find the original start state, transition, and next element
		AvatarStateMachineElement start = elementList.get(0);
		AvatarStateMachineElement startTran= start.getNext(0);
		AvatarStateMachineElement newStart = startTran.getNext(0);
		elementList.remove(start);
		elementList.remove(startTran);
		//Find every stop state, remove them, reroute transitions to them
		//For now, route every transition to stop state to remove the loop on requests 

		for (AvatarStateMachineElement e: elementList){
		    e.setName(processName(e.getName(), e.getID()));
		    stateObjectMap.put(task.getName()+"__"+e.getName(), e.getReferenceObject());
		
		    if (e instanceof AvatarStopState){
			//ignore it
		    }
		    else {
			for (int i=0; i< e.getNexts().size(); i++){
			    if (e.getNext(i) instanceof AvatarStopState){
				e.removeNext(i);
				//Route it back to the loop start
				e.addNext(loopstart);
			    }
			}
		        asm.addElement(e);
		    }
		}

		//Create exit after # of loop iterations is maxed out
		AvatarStopState stop = new AvatarStopState("stop", task.getActivityDiagram().get(0).getReferenceObject());	
		AvatarTransition exitTran = new AvatarTransition(block, "to_stop", task.getActivityDiagram().get(0).getReferenceObject());
		
	
		//Add Requests, direct transition to start of state machine
		for (Object obj: tmlmodel.getRequestsToMe(task)){	
		    System.out.println("Building request ");	
		    TMLRequest req = (TMLRequest) obj;
		    AvatarTransition incrTran = new AvatarTransition(block, "__after_loopstart__"+req.getName(), task.getActivityDiagram().get(0).getReferenceObject());
		    incrTran.addAction(AvatarTerm.createActionFromString(block,"req_loop_index = req_loop_index + 1"));
		    incrTran.setGuard(AvatarGuard.createFromString(block, "req_loop_index != " + loopLimit));
		    asm.addElement(incrTran);
		    loopstart.addNext(incrTran);	
		    AvatarSignal sig; 
		    if (!signalMap.containsKey(block.getName()+"__IN__"+req.getName())){
		        sig = new AvatarSignal(block.getName()+"__IN__"+req.getName(), AvatarSignal.IN, req.getReferenceObject());
		        block.addSignal(sig);
		        signals.add(sig);
			signalMap.put(block.getName()+"__IN__"+req.getName(),sig);
		    }
		    else {
			sig = signalMap.get(block.getName()+"__IN__"+req.getName());
		    }
	            AvatarActionOnSignal as= new AvatarActionOnSignal("getRequest__"+req.getName(), sig, req.getReferenceObject());
		    incrTran.addNext(as);
		    asm.addElement(as);
	            as.addValue(req.getName()+"__reqData");
		    AvatarAttribute requestData= new AvatarAttribute(req.getName()+"__reqData", AvatarType.INTEGER, block, null);
		    block.addAttribute(requestData);
	 	    for (int i=0; i< req.getNbOfParams(); i++){
		        if (block.getAvatarAttributeWithName(req.getParam(i))==null){
		    	    //Throw Error
			    as.addValue("tmp");
		    	}
		     	else {
		      	    as.addValue(req.getParam(i));
		    	}
		    }
		    AvatarTransition tran = new AvatarTransition(block, "__after_" + req.getName(), task.getActivityDiagram().get(0).getReferenceObject());
		    as.addNext(tran);
		    asm.addElement(tran);
		    if (req.checkAuth){
 			AvatarState afterSignalState = new AvatarState("aftersignalstate_"+req.getName()+"_"+req.getName(),req.getReferenceObject());
		    	AvatarTransition afterSignalTran = new AvatarTransition(block, "__aftersignalstate_"+req.getName(), req.getReferenceObject());
		    	tran.addNext(afterSignalState);
		    	afterSignalState.addNext(afterSignalTran);
		    	asm.addElement(afterSignalState);
		    	asm.addElement(afterSignalTran);
			afterSignalTran.addNext(newStart);
		    	AvatarAttributeState authDest = new AvatarAttributeState(req.getName()+"__destination",obj,requestData, afterSignalState);
		    	signalAuthDestMap.put(req.getName(), authDest);
	    	    }  
		    else {
		        tran.addNext(newStart);
		    }

		}

		
		asm.setStartState((AvatarStartState) ss);
	
	    }
	    else {
		//Not requested
		List<AvatarStateMachineElement> elementList= translateState(task.getActivityDiagram().get(0), block);
	        for (AvatarStateMachineElement e: elementList){
		    e.setName(processName(e.getName(), e.getID()));
		    asm.addElement(e);
		    stateObjectMap.put(task.getName()+"__"+e.getName(), e.getReferenceObject());
	        }
	        asm.setStartState((AvatarStartState) elementList.get(0));
	    }
	    for (SecurityPattern secPattern: secPatterns){
		AvatarAttribute sec = new AvatarAttribute(secPattern.name, AvatarType.INTEGER, block, null);
		AvatarAttribute enc = new AvatarAttribute(secPattern.name+"_encrypted", AvatarType.INTEGER, block, null);
	        LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
		block.addAttribute(sec);
		block.addAttribute(enc);
	        attrs.add(sec);
	        avspec.addPragma(new AvatarPragmaSecret("#Confidentiality "+block.getName() + "."+ secPattern.name, null, attrs));
	    }
	    avspec.addBlock(block);
	}
	
	checkConnections();
	checkChannels();

	//Add authenticity pragmas
	for (String s: signalAuthOriginMap.keySet()){
	    if (signalAuthDestMap.containsKey(s)){
		AvatarPragmaAuthenticity pragma = new AvatarPragmaAuthenticity("#Authenticity ", signalAuthOriginMap.get(s).getReferenceObject(), signalAuthOriginMap.get(s), signalAuthDestMap.get(s));
		avspec.addPragma(pragma);
	    }
	}

	//Create relations
	//Channels are ?? to ??
	//Requests are n to 1
	//Events are ?? to ??
	for (TMLChannel channel:tmlmodel.getChannels()){
	    if (channel.isBasicChannel()){
		AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
		LinkedList<HwCommunicationNode> path =tmlmap.findNodesForElement(channel);
	        if (path.size()!=0){
		    ar.setPrivate(true);
		    for (HwCommunicationNode node:path){
			if (node instanceof HwBus){
			    if (((HwBus) node).privacy ==0){
				ar.setPrivate(false);
			    }
			}
		    }
		}
		else {
	            ar.setPrivate(originDestMap.get(channel.getOriginTask().getName()+"__"+channel.getDestinationTask().getName())==1);
		}
	        //Find in signal
	        List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	        List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	        for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.IN){
		        String name = sig.getName();
		        if (name.equals(channel.getDestinationTask().getName()+"__IN__"+channel.getName())){
			    sig1.add(sig);
		        }
		    }
	        }
	        //Find out signal
	        for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.OUT){
		        String name = sig.getName();
		        if (name.equals(channel.getOriginTask().getName()+"__OUT__"+channel.getName())){
			    sig2.add(sig);
		        }
		    }
	        }
	        if (sig1.size()==0){
		    sig1.add(new AvatarSignal(channel.getDestinationTask().getName()+"__IN__"+channel.getName(), AvatarSignal.IN, null));
	    	}
	    	if (sig2.size()==0){
		    sig2.add(new AvatarSignal(channel.getOriginTask().getName()+"__OUT__"+channel.getName(), AvatarSignal.OUT, null));
	    	}
	    	if (sig1.size()==1 && sig2.size()==1){
		    ar.addSignals(sig2.get(0), sig1.get(0));
	    	}
	    	else {
		    System.out.println("Failure to match signals for TMLChannel "+ channel.getName());
	    	}
	    	avspec.addRelation(ar);
	    }
	    else {
		for (TMLTask t1: channel.getOriginTasks()){
		    for (TMLTask t2: channel.getDestinationTasks()){
			AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(t1), taskBlockMap.get(t2), channel.getReferenceObject());
			ar.setPrivate(originDestMap.get(t1.getName()+"__"+t2.getName())==1);
	    		//Find in signal
		    	List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    		List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    		for (AvatarSignal sig: signals){
			    if (sig.getInOut()==AvatarSignal.IN){
		    	        String name = sig.getName();
		    		if (name.equals(t2.getName()+"__IN__"+channel.getName())){
				    sig1.add(sig);
		    		}
			    }
	    		}
	    		//Find out signal
	    		for (AvatarSignal sig: signals){
			    if (sig.getInOut()==AvatarSignal.OUT){
		    	        String name = sig.getName();
		    	    	if (name.equals(t1.getName()+"__OUT__"+channel.getName())){
				    sig2.add(sig);
		    	    	}
			    }
	    	    	}
	    	    	if (sig1.size()==0){
			    sig1.add(new AvatarSignal(t2.getName()+"__IN__"+channel.getName(), AvatarSignal.IN, null));
	    	    	}
	    	    	if (sig2.size()==0){
			    sig2.add(new AvatarSignal(t1.getName()+"__OUT__"+channel.getName(), AvatarSignal.OUT, null));
	    	    	}
	    	    	if (sig1.size()==1 && sig2.size()==1){
			    ar.addSignals(sig2.get(0), sig1.get(0));
	    	    	}
	    	    	else {
			    System.out.println("Failure to match signals for TMLChannel "+ channel.getName() + " between " + t1.getName() + " and "+ t2.getName());
	    	    	}
	    	        avspec.addRelation(ar);
		    }
		}
	    }
	}
	for (TMLRequest request: tmlmodel.getRequests()){
	    for (TMLTask t1: request.getOriginTasks()){
 	    	AvatarRelation ar = new AvatarRelation(request.getName(), taskBlockMap.get(t1), taskBlockMap.get(request.getDestinationTask()), request.getReferenceObject());
	    	ar.setPrivate(originDestMap.get(t1.getName()+"__"+request.getDestinationTask().getName())==1);	    
	    	List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    	List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    	for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.IN){
		        String name = sig.getName();
		    	if (name.equals(request.getDestinationTask().getName()+"__IN__"+request.getName())){
			    sig1.add(sig);
		    	}
		    }
	    	}
	    	//Find out signal
	    	for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.OUT){
		    	String name = sig.getName();
		    	if (name.equals(t1.getName()+"__OUT__"+request.getName())){
			    sig2.add(sig);
		        }
		    }
	    	}
	    	if (sig1.size()==0){
		    sig1.add(new AvatarSignal(request.getDestinationTask().getName()+"__IN__"+request.getName(), AvatarSignal.IN, null));
	        }
	    	if (sig2.size()==0){
		    sig2.add(new AvatarSignal(t1.getName()+"__OUT__"+request.getName(), AvatarSignal.OUT, null));
	    	}
	    	if (sig1.size()==1 && sig2.size()==1){
		    ar.addSignals(sig2.get(0), sig1.get(0));
	    	}
	    	else {
		    //Throw error
		    System.out.println("Could not match for " + request.getName());
	    	}
	    	avspec.addRelation(ar);
	    }
	}
	for (TMLEvent event: tmlmodel.getEvents()){
	    
	    AvatarRelation ar = new AvatarRelation(event.getName(), taskBlockMap.get(event.getOriginTask()), taskBlockMap.get(event.getDestinationTask()), event.getReferenceObject());
	    ar.setPrivate(originDestMap.get(event.getOriginTask().getName()+"__"+event.getDestinationTask().getName())==1);
	    List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.IN){
		    String name = sig.getName();
		    if (name.equals(event.getDestinationTask().getName()+"__IN__"+event.getName())){
			sig1.add(sig);
		    }
		}
	    }
	    //Find out signal
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.OUT){
		    String name = sig.getName();
		    if (name.equals(event.getOriginTask().getName()+"__OUT__"+event.getName())){
			sig2.add(sig);
		    }
		}
	    }
	    if (sig1.size()==0){
		sig1.add(new AvatarSignal(event.getDestinationTask().getName()+"__IN__"+event.getName(), AvatarSignal.IN, null));
	    }
	    if (sig2.size()==0){
		sig2.add(new AvatarSignal(event.getOriginTask().getName()+"__OUT__"+event.getName(), AvatarSignal.OUT, null));
	    }
	    if (sig1.size()==1 && sig2.size()==1){
		ar.addSignals(sig2.get(0), sig1.get(0));
	    }
	    else {
		//Throw error
		System.out.println("Could not match for " + event.getName());
	    }
	    avspec.addRelation(ar);
	}
	for (AvatarSignal sig: signals){
	    //check that all signals are put in relations
	    AvatarRelation ar = avspec.getAvatarRelationWithSignal(sig);
	    if (ar==null){
		System.out.println("missing relation for " + sig.getName());
	    }
	}
	//Check if we matched up all signals
	avspec.addPragma(new AvatarPragmaInitialKnowledge("#InitialSessionKnowledge", null, keys, true));
	System.out.println(avspec);
	
	tmlmap.getTMLModeling().secChannelMap = secChannelMap;
	return avspec;
    }

 //   public void backtracePatterns(List<Stri
   
    public void backtraceReachability(List<String> reachableStates, List<String> nonReachableStates){
	for (String s: reachableStates){
	    if (stateObjectMap.containsKey(s.replace("enteringState__",""))){
		Object obj = stateObjectMap.get(s.replace("enteringState__",""));
		if (obj instanceof TMLADWriteChannel){
		    TMLADWriteChannel wc =(TMLADWriteChannel) obj;
		    wc.reachabilityInformation=1;
		}
		if (obj instanceof TMLADReadChannel){
		    TMLADReadChannel wc =(TMLADReadChannel) obj;
		    wc.reachabilityInformation=1;
		}
		
		if (obj instanceof TMLADSendEvent){
		    TMLADSendEvent wc =(TMLADSendEvent) obj;
		    wc.reachabilityInformation=1;
		}
		
		if (obj instanceof TMLADSendRequest){
		    TMLADSendRequest wc =(TMLADSendRequest) obj;
		    wc.reachabilityInformation=1;
		}
		if (obj instanceof TMLADWaitEvent){
		    TMLADWaitEvent wc =(TMLADWaitEvent) obj;
		    wc.reachabilityInformation=1;
		}		
	    }
	}
	for (String s:nonReachableStates){
	    if (stateObjectMap.containsKey(s.replace("enteringState__",""))){
		Object obj = stateObjectMap.get(s.replace("enteringState__",""));
		if (obj instanceof TMLADWriteChannel){
		    TMLADWriteChannel wc =(TMLADWriteChannel) obj;
		    wc.reachabilityInformation=2;
		}
		if (obj instanceof TMLADReadChannel){
		    TMLADReadChannel wc =(TMLADReadChannel) obj;
		    wc.reachabilityInformation=2;
		}
		
		if (obj instanceof TMLADSendEvent){
		    TMLADSendEvent wc =(TMLADSendEvent) obj;
		    wc.reachabilityInformation=2;
		}
		
		if (obj instanceof TMLADSendRequest){
		    TMLADSendRequest wc =(TMLADSendRequest) obj;
		    wc.reachabilityInformation=2;
		}
		if (obj instanceof TMLADWaitEvent){
		    TMLADWaitEvent wc =(TMLADWaitEvent) obj;
		    wc.reachabilityInformation=2;
		}		
	    }
	}
    }
}

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
    TMLModeling tmlmodel;

    public HashMap<TMLChannel, Integer> channelMap = new HashMap<TMLChannel,Integer>();
    public HashMap<TMLTask, AvatarBlock> taskBlockMap = new HashMap<TMLTask, AvatarBlock>();  
    public HashMap<String, Integer> originDestMap = new HashMap<String, Integer>();
    HashMap<String, AvatarSignal> signalMap = new HashMap<String, AvatarSignal>();
    List<AvatarSignal> signals = new ArrayList<AvatarSignal>();
    private final static Integer channelPublic = 0;
    private final static Integer channelPrivate = 1;
    private final static Integer channelUnreachable = 2;


    List<String> allStates;
    public TML2Avatar(TMLMapping tmlmap) {
        this.tmlmap = tmlmap;
	this.tmlmodel = tmlmap.getTMLModeling();
	allStates = new ArrayList<String>();
    }
    
    public void checkConnections(){
	for (TMLTask t1:tmlmodel.getTasks()){
	    for (TMLTask t2:tmlmodel.getTasks()){
		HwCPU node1 = (HwCPU) tmlmap.getHwNodeOf(t1);
		HwCPU node2 = (HwCPU) tmlmap.getHwNodeOf(t2);
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
				  System.out.println("BUS PRIVACY "+bus.privacy);
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
	for (AvatarSignal sig:signals){
	    System.out.println("signal "+sig.getName());
	}
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
	    HwCPU node1 = (HwCPU) tmlmap.getHwNodeOf(a);
	    for (TMLTask t: destinations){
	        List<HwBus> buses = new ArrayList<HwBus>();
		HwNode node2 = tmlmap.getHwNodeOf(t);
		if (node1==node2){
		    System.out.println("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is confidential");
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
			  System.out.println("Path does not exist for channel " + channel.getName() + " between Task " + a.getTaskName() + " and Task " + t.getTaskName());
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
			  System.out.println("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is " + (priv==1 ? "confidential" : "not confidential"));
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
	    if (!signalMap.containsKey(block.getName()+"__"+req.getName())){
	        sig = new AvatarSignal(block.getName()+"__"+req.getName(), AvatarSignal.OUT, req.getReferenceObject());
	        signals.add(sig);
		signalMap.put(block.getName()+"__"+req.getName(), sig);
	        block.addSignal(sig);
	    }
	    else {
		sig=signalMap.get(block.getName()+"__"+req.getName());
	    }
	    AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	    for (int i=0; i<sr.getNbOfParams(); i++){
		as.addValue(sr.getParam(i));
	    }
	    //Create new value to send....
	    AvatarAttribute requestData= new AvatarAttribute(req.getName()+"__reqData", AvatarType.INTEGER, block, null);
	    as.addValue(req.getName()+"__reqData");
	    block.addAttribute(requestData);
	    for (int i=0; i< req.getNbOfParams(); i++){
		as.addValue(req.getParam(i));
	    }
	    tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
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
	    TMLEvent ch = aee.getEvent();
	    if (ae instanceof TMLSendEvent){
		AvatarSignal sig;
		if (!signalMap.containsKey(block.getName()+"__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__"+ch.getName(), AvatarSignal.OUT, ch.getReferenceObject());
	            signals.add(sig);
		    block.addSignal(sig);
		    signalMap.put(block.getName()+"__"+ch.getName(), sig);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__"+ch.getName());
	    	}
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
 		AvatarAttribute eventData= new AvatarAttribute(ch.getName()+"__eventData", AvatarType.INTEGER, block, null);
	        as.addValue(ch.getName()+"__eventData");
	        block.addAttribute(eventData);
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	        elementList.add(as);
	        as.addNext(tran);
	        elementList.add(tran);
	    }
	    else if (ae instanceof TMLWaitEvent){
		AvatarSignal sig; 
		if (!signalMap.containsKey(block.getName()+"__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__"+ch.getName(), AvatarSignal.IN, ch.getReferenceObject());
	            signals.add(sig);
		    block.addSignal(sig);
		    signalMap.put(block.getName()+"__"+ch.getName(), sig);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__"+ch.getName());
	    	}
	        AvatarActionOnSignal as= new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
 		AvatarAttribute eventData= new AvatarAttribute(ch.getName()+"__eventData", AvatarType.INTEGER, block, null);
	    	as.addValue(ch.getName()+"__eventData");
	    	block.addAttribute(eventData);
	        tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
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
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject());
	    tran = new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    tran.addAction(((TMLActivityElementWithAction) ae).getAction());
	    as.addNext(tran);
	    elementList.add(as);
	    elementList.add(tran);
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
	    if (ae instanceof TMLReadChannel){
		if (!signalMap.containsKey(block.getName()+"__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__"+ch.getName(), AvatarSignal.IN, ch.getReferenceObject());
	            signals.add(sig);
		    signalMap.put(block.getName()+"__"+ch.getName(), sig);
		    block.addSignal(sig);
	    	    AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
	    	    block.addAttribute(channelData);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__"+ch.getName());
	    	}
	    }
	    else {
		if (!signalMap.containsKey(block.getName()+"__"+ch.getName())){
	            sig = new AvatarSignal(block.getName()+"__"+ch.getName(), AvatarSignal.OUT, ch.getReferenceObject());
	            signals.add(sig);
	    	    block.addSignal(sig);
		    signalMap.put(block.getName()+"__"+ch.getName(), sig);
		    AvatarAttribute channelData= new AvatarAttribute(ch.getName()+"__chData", AvatarType.INTEGER, block, null);
	    	    block.addAttribute(channelData);
	    	}
	    	else {
		    sig=signalMap.get(block.getName()+"__"+ch.getName());
	    	}
	    }
	    AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject());
	    as.addValue(ch.getName()+"__chData");
	    tran= new AvatarTransition(block, "__after_"+ae.getName(), ae.getReferenceObject());
	    as.addNext(tran);
	    elementList.add(as);
	    elementList.add(tran);
	}
	else if (ae instanceof TMLForLoop){
	    TMLForLoop loop = (TMLForLoop)ae;
	    if (loop.isInfinite()){
		List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block);
		//AvatarTransition looptran = new AvatarTransition(block, "loop__"+ae.getName(), null);
		//elementList.addAll(elements);
		//elementList.add(looptran);
		//replace stop states and point empty transitions to start of loop
		for (AvatarStateMachineElement e: elements){
		    if (e instanceof AvatarStopState){
		    }
		    else if (e.getNexts().size()==0){
			e.addNext(elements.get(0));
			elementList.add(e);
		    }
		    else if (e.getNext(0) instanceof AvatarStopState){
			//Remove the transition to AvatarStopState
			e.removeNext(0);
			e.addNext(elements.get(0));
			elementList.add(e);
			}
		    else {
		        elementList.add(e);
		    }
		}
		return elementList;
	    }
	    else {
		//Make initializaton, then choice state with transitions
		List<AvatarStateMachineElement> elements=translateState(ae.getNextElement(0), block);
		List<AvatarStateMachineElement> afterloop = translateState(ae.getNextElement(1), block);
		AvatarState initState = new AvatarState(ae.getName()+"__init", ae.getReferenceObject(), true);
		elementList.add(initState);
		//Build transition to choice
		tran = new AvatarTransition(block, "loop_init__"+ae.getName(), ae.getReferenceObject());
		tran.addAction(AvatarTerm.createActionFromString(block, loop.getInit()));
		elementList.add(tran);
		initState.addNext(tran);
		//Choice state
		AvatarState as = new AvatarState(ae.getName()+"__choice", ae.getReferenceObject(), true);
		elementList.add(as);
		tran.addNext(as);
		//transition to first element of loop
		tran = new AvatarTransition(block, "loop_increment__"+ae.getName(), ae.getReferenceObject());
		tran.setGuard(loop.getCondition().replaceAll("<", "!="));
		tran.addAction(AvatarTerm.createActionFromString(block, loop.getIncrement()));
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
		tran.setGuard("else");
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
	    AvatarState as = new AvatarState(ae.getName(), ae.getReferenceObject(), true);
	    //Make many choices
	    elementList.add(as);
	    TMLChoice c = (TMLChoice) ae;
	    for (int i=0; i<c.getNbGuard(); i++){
		tran = new AvatarTransition(block, "__after_"+ae.getName()+"_"+i, ae.getReferenceObject());
		tran.setGuard(c.getGuard(i));
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

    public AvatarSpecification generateAvatarSpec(){
	//TODO: Broadcast channels
	//TODO: Request parameters
	//TODO: Add pragmas
	//TODO: Cry
	AvatarSpecification avspec = new AvatarSpecification("spec", null);
	ArrayList<TMLTask> tasks = tmlmap.getTMLModeling().getTasks();
	for (TMLTask task:tasks){
	    AvatarBlock block = new AvatarBlock(task.getName(), avspec, task.getReferenceObject());
	    taskBlockMap.put(task, block);
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
		TMLRequest request= tmlmodel.getRequestToMe(task);
		//Oh this is fun...let's restructure the state machine
		//Create own start state, and ignore the returned one
		List<AvatarStateMachineElement> elementList= translateState(task.getActivityDiagram().get(0), block);
		AvatarStartState ss = new AvatarStartState("start", task.getActivityDiagram().get(0).getReferenceObject());
		asm.addElement(ss);
		AvatarTransition at= new AvatarTransition(block, "__after_start", task.getActivityDiagram().get(0).getReferenceObject());
		ss.addNext(at);
		asm.addElement(at);
		AvatarSignal sig = new AvatarSignal(block.getName()+"__"+request.getName(), AvatarSignal.IN, request.getReferenceObject());
		block.addSignal(sig);
		signals.add(sig);
	        AvatarActionOnSignal as= new AvatarActionOnSignal("getRequest__"+request.getName(), sig, request.getReferenceObject());
		at.addNext(as);
		asm.addElement(as);
		for (int i=0; i< request.getNbOfParams(); i++){
		    as.addValue(request.getParam(i)+"__reqData");
		}
		AvatarTransition tran = new AvatarTransition(block, "__after_" + request.getName(), task.getActivityDiagram().get(0).getReferenceObject());
		as.addNext(tran);
		asm.addElement(tran);

		//Find the original start state, transition, and next element
		AvatarStateMachineElement start = elementList.get(0);
		AvatarStateMachineElement startTran= start.getNext(0);
		AvatarStateMachineElement newStart = startTran.getNext(0);
		tran.addNext(newStart);
		elementList.remove(start);
		elementList.remove(startTran);
		//Find every stop state, remove them, reroute transitions to them
		//For now, route every transition to stop state to remove the loop on requests 
		
		for (AvatarStateMachineElement e: elementList){
		    e.setName(processName(e.getName(), e.getID()));
		    asm.addElement(e);
		}
		  /*  if (e instanceof AvatarStopState){
			//ignore it
		    }
		    else {
			for (int i=0; i< e.getNexts().size(); i++){
			    if (e.getNext(i) instanceof AvatarStopState){
				e.removeNext(i);
				//Route it back to the state with request
				e.addNext(as);
			    }
			}
		        asm.addElement(e);
		    }
		}*/
		asm.setStartState((AvatarStartState) ss);
	    }
	    else {
		List<AvatarStateMachineElement> elementList= translateState(task.getActivityDiagram().get(0), block);
	        for (AvatarStateMachineElement e: elementList){
		    e.setName(processName(e.getName(), e.getID()));
		    asm.addElement(e);
	        }
	        asm.setStartState((AvatarStartState) elementList.get(0));
	    }
	    avspec.addBlock(block);
	}
	checkConnections();
	checkChannels();
	//Create relations
	//Channels are ?? to ??
	//Requests are n to 1
	//Events are ?? to ??
	for (TMLChannel channel:channelMap.keySet()){
	    if (channel.isBasicChannel()){
		System.out.println("Basic channel ");
		AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
	        ar.setPrivate(channelMap.get(channel)==1);
	        //Find in signal
	        List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	        List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	        for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.IN){
		        String name = sig.getName();
		        if (name.equals(channel.getDestinationTask().getName()+"__"+channel.getName())){
			    sig1.add(sig);
		        }
		    }
	        }
	        //Find out signal
	        for (AvatarSignal sig: signals){
		    if (sig.getInOut()==AvatarSignal.OUT){
		        String name = sig.getName();
		        if (name.equals(channel.getOriginTask().getName()+"__"+channel.getName())){
			    sig2.add(sig);
		        }
		    }
	        }
	        if (sig1.size()==0){
		    sig1.add(new AvatarSignal(channel.getDestinationTask().getName()+"__"+channel.getName(), AvatarSignal.IN, null));
	    	}
	    	if (sig2.size()==0){
		    sig2.add(new AvatarSignal(channel.getOriginTask().getName()+"__"+channel.getName(), AvatarSignal.OUT, null));
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
		System.out.println("Complex channel ");
		for (TMLTask t1: channel.getOriginTasks()){
		    for (TMLTask t2: channel.getDestinationTasks()){

			AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(t1), taskBlockMap.get(t2), channel.getReferenceObject());
			ar.setPrivate(channelMap.get(channel)==1);
	    		//Find in signal
		    	List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    		List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    		for (AvatarSignal sig: signals){
			    if (sig.getInOut()==AvatarSignal.IN){
		    	        String name = sig.getName();
		    		if (name.equals(t2.getName()+"__"+channel.getName())){
				    sig1.add(sig);
		    		}
			    }
	    		}
	    		//Find out signal
	    		for (AvatarSignal sig: signals){
			    if (sig.getInOut()==AvatarSignal.OUT){
		    	        String name = sig.getName();
		    	    	if (name.equals(t1.getName()+"__"+channel.getName())){
				    sig2.add(sig);
		    	    	}
			    }
	    	    	}
	    	    	if (sig1.size()==0){
			    sig1.add(new AvatarSignal(channel.getDestinationTask().getName()+"__"+channel.getName(), AvatarSignal.IN, null));
	    	    	}
	    	    	if (sig2.size()==0){
			    sig2.add(new AvatarSignal(channel.getOriginTask().getName()+"__"+channel.getName(), AvatarSignal.OUT, null));
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
	    AvatarRelation ar = new AvatarRelation(request.getName(), taskBlockMap.get(request.getOriginTasks().get(0)), taskBlockMap.get(request.getDestinationTask()), request.getReferenceObject());
	    ar.setPrivate(originDestMap.get(request.getOriginTasks().get(0).getName()+"__"+request.getDestinationTask().getName())==1);	    
	    List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.IN){
		    String name = sig.getName();
		    if (name.equals(request.getDestinationTask().getName()+"__"+request.getName())){
			sig1.add(sig);
		    }
		}
	    }
	    //Find out signal
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.OUT){
		    String name = sig.getName();
		    if (name.equals(request.getOriginTasks().get(0).getName()+"__"+request.getName())){
			sig2.add(sig);
		    }
		}
	    }
	    if (sig1.size()==0){
		sig1.add(new AvatarSignal(request.getDestinationTask().getName()+"__"+request.getName(), AvatarSignal.IN, null));
	    }
	    if (sig2.size()==0){
		sig2.add(new AvatarSignal(request.getOriginTasks().get(0).getName()+"__"+request.getName(), AvatarSignal.OUT, null));
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
	for (TMLEvent event: tmlmodel.getEvents()){
	    AvatarRelation ar = new AvatarRelation(event.getName(), taskBlockMap.get(event.getOriginTask()), taskBlockMap.get(event.getDestinationTask()), event.getReferenceObject());
	    ar.setPrivate(originDestMap.get(event.getOriginTask().getName()+"__"+event.getDestinationTask().getName())==1);
	    List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
	    List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.IN){
		    String name = sig.getName();
		    if (name.equals(event.getDestinationTask().getName()+"__"+event.getName())){
			sig1.add(sig);
		    }
		}
	    }
	    //Find out signal
	    for (AvatarSignal sig: signals){
		if (sig.getInOut()==AvatarSignal.OUT){
		    String name = sig.getName();
		    if (name.equals(event.getOriginTask().getName()+"__"+event.getName())){
			sig2.add(sig);
		    }
		}
	    }
	    if (sig1.size()==0){
		sig1.add(new AvatarSignal(event.getDestinationTask().getName()+"__"+event.getName(), AvatarSignal.IN, null));
	    }
	    if (sig2.size()==0){
		sig2.add(new AvatarSignal(event.getOriginTask().getName()+"__"+event.getName(), AvatarSignal.OUT, null));
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
	for (AvatarSignal s: signals){

	    System.out.println(s.getName());
	}
	for (AvatarRelation ar: avspec.getRelations()){
	    System.out.println(ar.getName());
	}
	//Check if we matched up all signals
	System.out.println(avspec);
	return avspec;
    }

}

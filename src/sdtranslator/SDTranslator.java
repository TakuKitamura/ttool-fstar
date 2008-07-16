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
 * Class SDTranslator
 * Creation: 16/08/2004
 * @version 1.0 16/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package sdtranslator;

import sddescription.*;
import translator.*;

import java.util.*;


public class SDTranslator {
    private HMSC hmsc;
    private LinkedList instances;
    private LinkedList evtstolink;
    private LinkedList tctolink;
    private LinkedList actionInstances; // for managing instances having no action in sds.
    private TURTLEModeling tm;
    
    
    public SDTranslator(HMSC _hmsc) {
        hmsc = _hmsc;
        instances = hmsc.getInstances();
    }
    
    public void addInstances(Instance instance) {
        instances.add(instance);
    }
    
    public TURTLEModeling toTURTLEModeling() throws SDTranslationException {
        evtstolink = new LinkedList();
        tctolink = new LinkedList();
        tm = new TURTLEModeling();
        actionInstances = new LinkedList();
        //renameMessages(); //should be unique -> a from I1 to I2 : a__I1__to__I2
        //System.out.println("\n\ntoTURTLEModeling:\nMaking instances");
        addClasses();
        //System.out.println("Making time constraints");
        makeTimeConstraints();
        //System.out.println("Making tclass behaviour");
        makeInstancesBehavior();
        //System.out.println("Making relations between tclasses");
        makeInstancesRelations();
        //System.out.println("Making channels");
        makeChannelsBehaviour();
        //makeChannelsRelations(); -> not needed anymore: channels are implementd within one classe
        //System.out.println("Translating timers");
        makeTimers();
        //System.out.println("Checking timers");
        checkForTimers();
        //System.out.println("Managing infinite loops");
        /*manageInfiniteLoops();*/
        //System.out.println("\n\n\n\n******DEBUG*****\n");
        tm.print("Sequence Data save");
		tm.makeSequenceWithDataSave();
		
        tm.removeInfiniteLoops();
        //tm.print("LcmgClient");
        //System.out.println("Remove leading to stops:");
        tm.removeChoicesLeadingToStop();
        //System.out.println("Leading to stops done");
        //System.out.println("\n*** Optimizing TURTLE modeling ***");
        //tm.print();
        //tm.optimize();
		tm.simplify(false, false);
        //System.out.println("\n*** Optimization done ***");
        //tm.print();
        return tm;
    }
    
    public String toRTLOTOS() {
        //System.out.println("\n*** Building TURTLE modeling ***");
        try {
            //TURTLEModeling tm = toTURTLEModeling();
        	tm = toTURTLEModeling();
            //tm.print();
        } catch (SDTranslationException e) {
            System.out.println("Exception during translation:" + e.getMessage());
            return null;
        }
        //System.out.println("\n*** Building RTLOTOS Specification ***");
        TURTLETranslator tt = new TURTLETranslator(tm);
        return tt.generateRTLOTOS();
    }
    
    
    // SD -> TURTLEModeling
    
    // we assume each instance has a unique name
    private void addClasses() throws SDTranslationException {
        Instance ins;
        TClass t;
        ListIterator iterator = instances.listIterator();
        while(iterator.hasNext()) {
            ins = (Instance)(iterator.next());
            t = new TClass(ins.getName(), true);
            //System.out.println("Adding TClass: " + t.getName());
            tm.addTClass(t);
        }
    }
    
    private void makeInstancesBehavior() throws SDTranslationException{
        Instance ins;
        ListIterator iterator = instances.listIterator();
		TClass t;
        while(iterator.hasNext()) {
            ins = (Instance)(iterator.next());
            t = tm.getTClassWithName(ins.getName());
            //System.out.println("Managing instance: " + ins.getName());
            makeInstanceBehavior(ins, t);
        }
    }
    
    // The hmsc is considered to be safe
    private void makeInstanceBehavior(Instance ins, TClass t) throws SDTranslationException {
        ListIterator iterator = instances.listIterator();
        ActivityDiagram ad;
        LinkedList nodes;
        LinkedList nodesUsed;
        LinkedList adcomponents;
        int i;
        int type;
        ADJunction adj;
        ADChoice adc;
        //ADStart adstart;
        ADStop adstop;
        ADParallel adp;
        ADPreempt adpr;
        ADSequence adseq;
        HMSCNode node;
        ADComponent adcomp;
        //HMSC myHmsc;
        
        // Build new Activity diagram
        ad = new ActivityDiagram();
        t.setActivityDiagram(ad);
        
        // Build corresponding hmsc;
        //myHmsc = buildCorrespondingHMSC(hmsc, ins);
        
        // Building correspondance between nodes and junction + choice
        //hmsc.print();
        nodes = hmsc.getListOfNodes();
        adcomponents = new LinkedList();
        iterator = nodes.listIterator();
        
        while(iterator.hasNext()) {
            node = (HMSCNode)(iterator.next());
            type = node.getType();
            switch(type) {
                case HMSCNode.STOP:
                    adstop = new ADStop();
                    adcomponents.add(adstop);
                    ad.add(adstop);
                    break;
                case HMSCNode.START:
                    //adstart= new ADStart();
                    adcomponents.add(ad.getStartState());
                    //ad.add(adstart);
                    break;
                case HMSCNode.CHOICE:
                    adj = new ADJunction();
                    adc = new ADChoice();
                    adj.addNext(adc);
                    adcomponents.add(adj);
                    ad.add(adj);
                    ad.add(adc);
                    break;
                case HMSCNode.PARALLEL:
                    adp = new ADParallel();
                    adp.setValueGate("[]");
                    adcomponents.add(adp);
                    ad.add(adp);
                    break;
                case HMSCNode.PREEMPT:
                    adpr = new ADPreempt();
                    adcomponents.add(adpr);
                    ad.add(adpr);
                    break;
                case HMSCNode.SEQUENCE:
                    adseq = new ADSequence();
                    adcomponents.add(adseq);
                    ad.add(adseq);
                    break;
                default:
                    throw new SDTranslationException();
            }
        }
        
        // Building AD -> Traversing the graph of nodes
        
        for(i=0; i<nodes.size(); i++) {
            node = (HMSCNode)(nodes.get(i));
            adcomp = (ADComponent)(adcomponents.get(i));
            while(adcomp.realNbOfNext() != 0) {
                adcomp = adcomp.getNext(0);
            }
            translateNode(ins, t, node, adcomp, ad, nodes, adcomponents);
        }
    }
    
    private void translateNode(Instance ins, TClass t, HMSCNode n, ADComponent adc, ActivityDiagram ad, LinkedList nodes, LinkedList adcomponents) throws SDTranslationException {
        translateNodetoNodes(ins, n, adc, nodes, adcomponents);
        translateNodetoMSCs(ins, t, n, adc, ad, nodes, adcomponents);
    }
    
    private void translateNodetoNodes(Instance ins, HMSCNode n, ADComponent adc,  LinkedList nodes, LinkedList adcomponents) {
        LinkedList list = n.getNextNodes();
        ListIterator iterator = list.listIterator();
        HMSCNode node;
        ADComponent adc1;
        int index;
        int i=0;
        String guard;
        
        while(iterator.hasNext()) {
            node = (HMSCNode)(iterator.next());
            //System.out.println("node (node to node)= " + node.getName());
            index = nodes.indexOf(node);
            adc1 = (ADComponent)(adcomponents.get(index));
            adc.addNext(adc1);
            if (adc instanceof ADChoice) {
                //System.out.println("Node guard with i=" + i + " and size=" + n.sizeNodeGuard() + " on " + n.getName());
                if (n.sizeNodeGuard() > i) {
                    guard = n.getNodeGuard(i);
                    guard = getGuard(ins, guard);
                    //System.out.println("Choice found with Node guard=" + n.getNodeGuard(i) + " and resultant guard=" + guard);
                    ((ADChoice)adc).addGuard(guard);
                }
                //System.out.println("End node guard");
            }
            i++;
        }
    }
    
    private void translateNodetoMSCs(Instance ins, TClass t, HMSCNode n, ADComponent adc, ActivityDiagram ad, LinkedList nodes, LinkedList adcomponents) throws SDTranslationException{
        MSC msc;
        LinkedList mscs = n.getNextMSCs();
        int id = 0;
        
        ListIterator iterator = mscs.listIterator();
        while(iterator.hasNext()) {
            msc = (MSC)(iterator.next());
            translateNodeToMSC(ins, t, msc, n, adc, id, ad,  nodes, adcomponents);
            id ++;
        }
    }
    
    // list of orders is assumed to be minimal
    // for example, if e1 < e2 and e2 <e3, the list does not contain e1 < e3
    // If an event is not ordered with regard to another one -> link to the last Parallel bar
    // id of the MSCs in the list of next MSCs ...
    private void translateNodeToMSC(Instance ins, TClass t, MSC msc, HMSCNode n, ADComponent adc, int id, ActivityDiagram ad, LinkedList nodes, LinkedList adcomponents) throws SDTranslationException{
        HMSCNode node;
        int index;
        ADParallel last, first;
        //ADParallel bar1, bar2;
        LinkedList evts, orders, actions;
        Evt evt1;
        ListIterator iterator, iterator1;
        Order order1;
        ActionEvt aevt, aevt1, aevt2;
        EvtToLink etl;
        String guard;
		ADChoice adch;
        
        // Settling the last bar and its link to next ADComponent
        last = new ADParallel();
        last.setValueGate("[]");
        ad.add(last);
        node = msc.getNextNode();
        index = nodes.indexOf(node);
        if (index == -1) {
            throw new SDTranslationException("Component has no next component (missing termination?)");
        }
        last.addNext((ADComponent)(adcomponents.get(index)));
        
        // Setting the first bar
        first = new ADParallel();
        first.setValueGate("[]");
        ad.add(first);
        adc.addNext(first);
        
        if (adc instanceof ADChoice) {
            //System.out.println("ADChoice node=" + n.getName() + " next Node = " + node.getName());   
            if (n.sizeMSCGuard() > id) {
                //System.out.println("Choice found with MSC guard=" + n.getMSCGuard(id) + " for id=" + id);
                guard = n.getMSCGuard(id);
                guard = getGuard(ins, guard);
                //System.out.println("Choice found with MSC guard=" + n.getMSCGuard(id) + " and resultant guard=" + guard);
                ((ADChoice)adc).addGuard(guard);
            }
        }
        
        // Building an action per event
        actions = new LinkedList();
        evts = msc.getEvts();
        
        iterator = evts.listIterator();
        while(iterator.hasNext()) {
            evt1 = (Evt)(iterator.next());
            if (evt1.getInstance() == ins){
                aevt = new ActionEvt(evt1, ad);
                
                actions.add(aevt);
                etl = aevt.translateEvt(t, ad);
                if (etl != null) {
                    if ((etl.type != EvtToLink.INTERNAL_ACTION) || (etl.type != EvtToLink.VARIABLE_SET) || (etl.type != EvtToLink.TIME_INTERVAL)) {
                        evtstolink.add(etl);
                    }
                    manageTimeConstraint(aevt, t);
                }
            }
        }
        
        // no action ?
        if (actions.size() ==0) {
            // link the first bar to the last one
            //System.out.println("Instance " + ins.getName() + " has no action in scenario " + msc.getName());
            first.addNext(last);
        }
        
        // Dealing with orders between events
        // first iteration -> link those leading ordered
        orders = msc.getOrders();
        iterator = orders.listIterator();
        
        //System.out.println("Dealing with order ");
        while(iterator.hasNext()) {
            order1 = (Order)(iterator.next());
            //System.out.println("order ");
            // if the two evts are involved -> used at activity diagram level
            iterator1 = actions.listIterator();
            aevt1 = null; aevt2 = null;
            while(iterator1.hasNext()) {
                aevt = (ActionEvt)(iterator1.next());
                if (aevt.evt == order1.evt1)
                    aevt1 = aevt;
                if (aevt.evt == order1.evt2)
                    aevt2 = aevt;
            }
            if ((aevt1 != null) && (aevt2 != null)) {
                //System.out.println("Order found: dealing with order ");
				// Is it a guard?
				if (!aevt2.evt.isAGuardEvt()) { 
					aevt1.getLast().addNext(aevt2.getFirst());
					if (aevt1.evt.isAGuardEvt()) {
						// Must add the right guard
						guard = aevt1.evt.getActionId();
						if (aevt1.evt.getType() == Evt.ELSE_GUARD) {
							guard = msc.getElseGuard(aevt1.evt);
						}
						if (guard == null) {
							guard = "";
						}
						if (!(aevt1.evt.getType() == Evt.END_GUARD)) { 
							guard = "[ " + guard + " ]"; 
							aevt1.getGuarded().addGuard(guard);
						}
					}
				} else {
					// guard management!
					if (aevt2.evt.getType() == Evt.END_GUARD) {
						aevt1.getLast().addNext(aevt2.getFirst());
					} else {
						Evt previous = msc.getPreviousGuardEvt(aevt2.evt);
						if ( previous == null) {
							// First guard -> easy case
							aevt1.getLast().addNext(aevt2.getFirst());
						} else {
							aevt1.getLast().addNext(getActionEvt(actions, msc.getEndGuard(aevt2.evt)).getMix());
							ActionEvt ae = getActionEvt(actions, previous);
							if (ae != null) {
								ae.getGuarded().addNext(aevt2.getFirst());
								ae.getGuarded().addGuard("[]");
							}
						}
					}
				}
            }
            
        }
        
        // second iteration -> when no order, executed at the beginning, and when no following order -> linked to last one
        iterator1 = actions.listIterator();
        while(iterator1.hasNext()) {
            aevt = (ActionEvt)(iterator1.next());
            
            //order before?
            if (ad.getNbComponentLeadingTo(aevt.getFirst()) == 0) {
                first.addNext(aevt.getFirst());
                //System.out.println("Adding first");
            }
            
            //order at the end?
            if (aevt.getLast().getNbNext() == 0) {
                aevt.getLast().addNext(last);
                //System.out.println("Adding last");
            } else {
                //System.out.println("No last: " + aevt.getLast().getNext(0).hashCode());
                
            }
        }
    }
    
    private String getGuard(Instance ins, String s) {
        String def = "[]";
        int index;
        String tmp;
        
        try {
            tmp = s.trim();
            
            index = tmp.indexOf("/");
            
            if (index < 0) {
                return def;
            }
            
            tmp = tmp.substring(tmp.indexOf('[') + 1, index);
            tmp = tmp.trim();
            
            //System.out.println("Found instance: " + tmp + "and intance= " + ins.getName());
            
            if (tmp.compareTo(ins.getName()) ==0) {
                return ("[" + s.substring(index +1, s.length())).trim();
            }
        } catch (Exception e) {
            return def;
        }
        
        return def;
        
    }
    
    // sync, inv between instances
    private void makeInstancesRelations() throws SDTranslationException {
        Iterator iterator1, iterator2, iterator3;
        LinkedList mscs, links;
        MSC msc;
        LinkEvts le;
        EvtToLink evtlink, evtlk1, evtlk2;
        Relation r;
        
        mscs = hmsc.getMSCs();
        iterator1 = mscs.listIterator();
        
        while(iterator1.hasNext()) {
            msc = (MSC)(iterator1.next());
            links = msc.getLinksEvts();
            iterator2 = links.listIterator();
            while(iterator2.hasNext()) {
                le = (LinkEvts)(iterator2.next());
                iterator3 = evtstolink.listIterator();
                evtlk1 = null; evtlk2 = null;
                while(iterator3.hasNext()) {
                    evtlink = (EvtToLink)(iterator3.next());
                    if (evtlink.evt == le.evt1) {
                        evtlk1 = evtlink;
                    }
                    if (evtlink.evt == le.evt2) {
                        evtlk2 = evtlink;
                    }
                }
                if ((evtlk1 != null) && (evtlk2 != null)) {
                    if ((evtlk1.type == EvtToLink.SYNC) && (evtlk2.type == EvtToLink.SYNC)) {
                        r = tm.syncRelationBetween(evtlk1.t, evtlk2.t);
                        if (r == null) {
                            r = new Relation(Relation.SYN, evtlk1.t, evtlk2.t, false);
                            tm.addRelation(r);
                        }
                        if (r.t1 == evtlk1.t) {
                            if (!r.gatesConnected(evtlk1.g, evtlk2.g)) {
                                r.addGatesIfApplicable(evtlk1.g, evtlk2.g);
                            }
                        } else {
                            if (!r.gatesConnected(evtlk2.g, evtlk1.g)) {
                                r.addGatesIfApplicable(evtlk2.g, evtlk1.g);
                            }
                        }
                        
                    }
                    if ((evtlk1.type == EvtToLink.SEND_MSG) && (evtlk2.type == EvtToLink.RECV_MSG)) {
						
						if (evtlk1.nbOfParams != evtlk2.nbOfParams) {
							throw new SDTranslationException("Parameters on asynchronous events should be the same on both sides");
						}
						
                        // create the buffer classes if necessary
                        TClass tin, tout;
                        String tname = evtlk1.t.getName() + "__" + evtlk2.t.getName();
                        //tin = tm.getTClassWithName("Channel__" + evtlk1.g.getName() + "__" + tname +"__IN");
                        //tout = tm.getTClassWithName("Channel__" + evtlk1.g.getName() + "__" + tname + "__OUT");
                        tin = tm.getTClassWithName("Channel__" + evtlk1.g.getName() + "__" + tname +"__INOUT");
                        tout = tin;
                        
                        /*if (tin == null) {
                            TClassBufferIn tinbuf = new TClassBufferIn("Channel__" + evtlk1.g.getName() + "__" + tname + "__IN");
                            tin = tinbuf;
                            tm.addTClass(tin);
                        }
                        
                        if (tout == null) {
                            TClassBufferOut toutbuf = new TClassBufferOut("Channel__" + evtlk2.g.getName() + "__" + tname + "__OUT");
                            tout = toutbuf;
                            tm.addTClass(tout);
                        }*/
						
						if (evtlk1.nbOfParams == 0) {
							if (tin == null) {
								TClassBasicFIFO tinbuf = new TClassBasicFIFO("Channel__" + evtlk1.g.getName() +  "__INOUT");
								tin = tinbuf;
								tout = tinbuf;
								tinbuf.setNbParam(0);
								tm.addTClass(tin);
							}
						} else {
							if (tin == null) {
								System.out.println("TClassInfiniteFIFO is used");
								TClassInfiniteFIFO tinbuf = new TClassInfiniteFIFO("Channel__" + evtlk1.g.getName() + "__INOUT");
								tin = tinbuf;
								tout = tinbuf;
								tinbuf.setNbParam(evtlk1.nbOfParams);
								tm.addTClass(tin);
							} 
							if (((TClassBuffer)tin).getNbParam() != evtlk1.nbOfParams) {
								throw new SDTranslationException("Parameters on asynchronous events should be the same on all exchanges");
							}
						}
                        
                        // t -> IN buffer
                        r = tm.syncRelationBetween(evtlk1.t, tin);
                        if (r == null) {
                            r = new Relation(Relation.SYN, evtlk1.t, tin, false);
                            tm.addRelation(r);
                        }
                        Gate g = tin.getGateByName(evtlk1.g.getName() + TClassBuffer.IN);
                        if (g == null) {
                            g = new Gate(evtlk1.g.getName() + TClassBuffer.IN, Gate.GATE, false);
                            tin.addGate(g);
                            ((TClassBuffer)tin).addParamInForExchange(evtlk1.g.getName() + TClassBuffer.IN);
                        }
                        
                        if (!r.gatesConnected(evtlk1.g, g)) {
                            r.addGatesIfApplicable(evtlk1.g, g);
                        }
                        
                        // OUT Buffer -> t
                        r = tm.syncRelationBetween(tout, evtlk2.t);
                        if (r == null) {
                            r = new Relation(Relation.SYN, tout, evtlk2.t, false);
                            tm.addRelation(r);
                        }
                        g = tout.getGateByName(evtlk2.g.getName() + TClassBuffer.OUT);
                        if (g == null) {
                            g = new Gate(evtlk2.g.getName() + TClassBuffer.OUT, Gate.GATE, false);
                            tout.addGate(g);
                            ((TClassBuffer)tout).addParamOutForExchange(evtlk2.g.getName() + TClassBuffer.OUT);
                        }
                        
                        if (!r.gatesConnected(g, evtlk2.g)) {
                            r.addGatesIfApplicable(g, evtlk2.g);
                        }
                        
                    }
                }
                
            }
        }
    }
    
    private void makeChannelsBehaviour() {
        TClass t;
        
        for(int i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            //System.out.println("Making Activity Diagram of " + t.getName());
            if (t instanceof TClassBuffer) {
                ((TClassBuffer)t).makeTClass();
            }
        }
    }
    
    private void makeChannelsRelations() {
        
        // Synchro between in channels and out channels (same name : Channel__msg__IN and Channel__msg__OUT)
        TClass tin, tout;
        int i, j, k;
        String sin, sout, param;
        Relation r;
        Gate gin, gout;
        
        for(i=0; i<tm.classNb(); i++) {
            tin = tm.getTClassAtIndex(i);
            if (tin instanceof TClassBufferIn) {
                for(j=0; j<tm.classNb(); j++) {
                    tout = tm.getTClassAtIndex(j);
                    if (tout instanceof TClassBufferOut) {
                        //System.out.println("Found corresponding tin and tout");
                        sin = tin.getName();
                        sin = sin.substring(0, sin.length() - 2);
                        sout = tout.getName();
                        sout = sout.substring(0, sout.length() - 3);
                        //System.out.println("name tin = " + sin + " name tout = " + sout);
                        if (sin.compareTo(sout) ==0) {
                            //System.out.println("Same name!");
                            r = tm.syncRelationBetween(tin, tout);
                            if (r == null) {
                                r = new Relation(Relation.SYN, tin, tout, false);
                                tm.addRelation(r);
                            }
                            // Obtain param
                            for(k=0; k<((TClassBuffer)tin).getParamInNb(); k++) {
                                param = ((TClassBuffer)tin).getParamInAt(k);
                                gin = tin.getGateByName(param);
                                gout = tout.getGateByName(param);
                                if ((gin != null) && (gout != null)) {
                                    r.addGates(gin, gout);
                                }
                            }
                            // Get gates
                            // Connect gates
                        }
                    }
                }
            }
        }
    }
    
    private void makeTimers() throws SDTranslationException {
        Iterator iterator1;
        
        //LinkedList mscs, links;
        //MSC msc;
        //LinkEvts le;
        EvtToLink evtlink; //evtlk1, evtlk2;
        Relation r;
        TClass t;
        Gate g;
        
        String timerName, tclassName;
        
        iterator1 = evtstolink.listIterator();
        while(iterator1.hasNext()) {
            evtlink = (EvtToLink)(iterator1.next());
            if (evtlink.evt.getType() == Evt.TIMER_SET) {
                //System.out.println("Timer set");
                timerName = evtlink.evt.getTimerName();
                tclassName = BasicTimer.makeTimerTClassName(timerName);
                //System.out.println("TClass name = " + tclassName);
                t = tm.getTClassWithName(tclassName);
                if (t == null) {
                    t = new BasicTimer(tclassName, true);
                    ((BasicTimer)t).setInstanceSet(evtlink.t.getName());
                    tm.addTClass(t);
                } else {
                    //System.out.println("Class found: " + t.getName());
                    // Does the set action of the timer corresponds to this instance?
                    if (!(t instanceof BasicTimer)) {
                        throw new SDTranslationException("Error on timers (error #1)");
                    }
                    
                    if (((BasicTimer)t).getInstanceSet() == null) {
                        ((BasicTimer)t).setInstanceSet(evtlink.t.getName());
                    }
                    
                    if (evtlink.t.getName().compareTo(((BasicTimer)t).getInstanceSet()) != 0) {
                        throw new SDTranslationException("Error on timers (error #2)");
                    }
                }
                
                // sync relation between timer and tclass on the set gate ?
                r = tm.syncRelationBetween(evtlink.t, t);
                if (r == null) {
                    r = new Relation(Relation.SYN, evtlink.t, t, false);
                    tm.addRelation(r);
                }
                
                // Right gates?
                g = ((BasicTimer)t).getGateSet();
                if (!(r.gatesConnected(evtlink.g, g))) {
                    r.addGates(evtlink.g, g);
                    //System.out.println("Adding set gates between " + evtlink.t.getName() + " and " + t.getName());
                }
            }
            
            if (evtlink.evt.getType() == Evt.TIMER_EXP) {
                //System.out.println("Timer exp");
                timerName = evtlink.evt.getTimerName();
                //System.out.println("Timer name (exp): " + timerName);
                tclassName = BasicTimer.makeTimerTClassName(timerName);
                //System.out.println("Timer name (exp): " + tclassName);
                t = tm.getTClassWithName(tclassName);
                if (t == null) {
                    t = new BasicTimer(tclassName, true);
                    ((BasicTimer)t).setInstanceExp(evtlink.t.getName());
                    tm.addTClass(t);
                } else {
                    // Does the set action of the timer corresponds to this instance?
                    if (!(t instanceof BasicTimer)) {
                        throw new SDTranslationException("Error on timers (error #1)");
                    }
                    
                    if (((BasicTimer)t).getInstanceExp() == null) {
                        ((BasicTimer)t).setInstanceExp(evtlink.t.getName());
                    }
                    
                    if ((((BasicTimer)t).getInstanceExp() != null) && (evtlink.t.getName().compareTo(((BasicTimer)t).getInstanceExp()) != 0)) {
                        throw new SDTranslationException("Error on timers (error #2)");
                    } else {
                        ((BasicTimer)t).setInstanceExp(evtlink.t.getName());
                    }
                }
                
                // sync relation between timer and tclass on the set gate ?
                r = tm.syncRelationBetween(evtlink.t, t);
                if (r == null) {
                    r = new Relation(Relation.SYN, evtlink.t, t, false);
                    tm.addRelation(r);
                }
                
                // Right gates?
                g = ((BasicTimer)t).getGateExp();
                if (!(r.gatesConnected(evtlink.g, g))) {
                    r.addGates(evtlink.g, g);
                    //System.out.println("Adding exp gates between " + evtlink.t.getName() + " and " + t.getName());
                }
            }
            
            if (evtlink.evt.getType() == Evt.TIMER_RESET) {
                timerName = evtlink.evt.getTimerName();
                //System.out.println("Timer name (exp): " + timerName);
                tclassName = BasicTimer.makeTimerTClassName(timerName);
                //System.out.println("Timer name (exp): " + tclassName);
                t = tm.getTClassWithName(tclassName);
                if (t == null) {
                    t = new BasicTimer(tclassName, true);
                    ((BasicTimer)t).setInstanceReset(evtlink.t.getName());
                    tm.addTClass(t);
                } else {
                    // Does the set action of the timer corresponds to this instance?
                    if (!(t instanceof BasicTimer)) {
                        throw new SDTranslationException("Error on timers (error #1)");
                    }
                    
                    if (((BasicTimer)t).getInstanceReset() == null) {
                        ((BasicTimer)t).setInstanceReset(evtlink.t.getName());
                    }
                    
                    if ((((BasicTimer)t).getInstanceReset() != null) && (evtlink.t.getName().compareTo(((BasicTimer)t).getInstanceReset()) != 0)) {
                        throw new SDTranslationException("Error on timers (error #2)");
                    } else {
                        ((BasicTimer)t).setInstanceReset(evtlink.t.getName());
                    }
                }
                
                // sync relation between timer and tclass on the set gate ?
                r = tm.syncRelationBetween(evtlink.t, t);
                if (r == null) {
                    r = new Relation(Relation.SYN, evtlink.t, t, false);
                    tm.addRelation(r);
                }
                
                // Right gates?
                g = ((BasicTimer)t).getGateReset();
                if (!(r.gatesConnected(evtlink.g, g))) {
                    r.addGates(evtlink.g, g);
                    //System.out.println("Adding reset gates between " + evtlink.t.getName() + " and " + t.getName());
                }
            }
            
        }
    }
    
    
    // if a reset gate is not used -> simplified timer
    // if an exp gate is not used -> error
    private void checkForTimers() throws SDTranslationException {
        BasicTimer bt;
        TClass t;
        Relation r;
        int i, j;
        boolean resetIsUsed = false;
        boolean expIsUsed = false;
        
        for(i=0; i<tm.classNb(); i++) {
            t = tm.getTClassAtIndex(i);
            if (t instanceof BasicTimer) {
                bt = (BasicTimer)t;
                resetIsUsed = false;
                expIsUsed = false;
                // search for relations involving bt
                for(j=0; j<tm.relationNb(); j++) {
                    r = tm.getRelationAtIndex(j);
                    if (r.gatesOfRelation(bt.getGateReset(), bt)) {
                        resetIsUsed = true;
                    }
                    if (r.gatesOfRelation(bt.getGateExp(), bt)) {
                        expIsUsed = true;
                    }
                }
                if (!expIsUsed) {
                    //bt.removeExp();
                    throw new SDTranslationException("Error on timers (error #3)");
                }
                if (!resetIsUsed) {
                    //System.out.println("\n\n\nReset is not used\n\n\n");
                    bt.removeReset();
                }
            }
        }
    }
    
    private void makeTimeConstraints() {
        LinkedList mscs, tcs;
        ListIterator iterator1, iterator2;
        TimeConstraint tc;
        TimeConstraintLink tcl;
        MSC msc;
        Evt evt1;
        
        TimeConstraintLink.reinitIndex();
        
        // For each time constraint, a TimeConstraintLink is built
        mscs = hmsc.getMSCs();
        iterator1 = mscs.listIterator();
        
        while(iterator1.hasNext()) {
            msc = (MSC)(iterator1.next());
            tcs = msc.getTimeConstraints();
            iterator2 = tcs.listIterator();
            while(iterator2.hasNext()) {
                tc = (TimeConstraint)(iterator2.next());
                //System.out.println("tc found");
                tcl = new TimeConstraintLink(tc, msc);
                tcl.build();
                tctolink.add(tcl);
                tm.addTClass(tcl.t);
            }
        }
        
        //says if tcl are basic i.e.:
        // * begin of relative constraint is just the previous evt of the tc
        // * the end only has one possible previous evt
        // * no other end of relative constraint on the end of the tc
        // * no absolute constraint on the end
        TimeConstraintLink tclink;
        
        iterator1 = tctolink.listIterator();
        while(iterator1.hasNext()) {
            tclink = (TimeConstraintLink)(iterator1.next());
            if (tclink.tc.type == TimeConstraint.RELATIVE) {
                evt1 = null;
                evt1 = tclink.msc.hasExactlyOnePreviousEvt(tclink.tc.evt2);
                if ((evt1 != null) && (evt1 ==tclink.tc.evt1)){
                    if (tclink.msc.isEndOfExactlyOneRelativeTC(tclink.tc.evt2)) {
                        //System.out.println("Basic tc found");
                        tclink.basicTC = true;
                        tm.removeTClass(tclink.t); // A TERMINER !!!!
                    }
                }
            }
        }    
    }
    
    private void manageTimeConstraint(ActionEvt aevt, TClass t) {
        // Is there a time constraint regarding this evt ?
        
        ListIterator iterator1;
        TimeConstraintLink tclink;
        Param p;
        
        iterator1 = tctolink.listIterator();
        while(iterator1.hasNext()) {
            tclink = (TimeConstraintLink)(iterator1.next());
            if (tclink.tc.type == TimeConstraint.RELATIVE) {
                //System.out.println("*** -> Relative time constraint");
                if (tclink.tc.evt1 == aevt.evt) {
                    // evt begin
                    if (!tclink.basicTC) {
                        //System.out.println("Managing begin with " + tclink.g1);
                        //p = t.addParameterGenerateName("cpt_begin_" + tclink.t.getName(), Param.NAT, "0");
                        //aevt.addBeginCallTo(tclink.g1, p);
						aevt.addBeginRTCCallTo(tclink.g1);
                        tm.addSynchroRelation(t, tclink.g1, tclink.t, tclink.t.getGateG1());
                    }
                } else if (tclink.tc.evt2 == aevt.evt) {                 
                    // evt end
                    //System.out.println("Managing end with " + tclink.g2);
                    if (!tclink.basicTC) {
                    // Param
                    //p = t.addParameterGenerateName("cpt_end_" + tclink.t.getName(), Param.NAT, "0");
                    aevt.addEndRTCCallTo(tclink.g2, tclink.g3, tclink.g4);
                    tm.addSynchroRelation(t, tclink.g2, tclink.t, tclink.t.getGateG2());
                    tm.addSynchroRelation(t, tclink.g3, tclink.t, tclink.t.getGateG3());
					tm.addSynchroRelation(t, tclink.g4, tclink.t, tclink.t.getGateG4());
                    //tm.addSynchroRelation(t, tclink.g4, tclink.t, tclink.t.getGateG4());
                    } else {
                        //System.out.println("Managing end with basic TC");
                        aevt.addBasicTCTo(tclink.tc.time1, tclink.tc.time2);
                    }
                }
            } else { // Absolute time constraint
                if (tclink.tc.evt1 == aevt.evt) {
                    //System.out.println("Managing end with " + tclink.g2);
                    //p = t.addParameterGenerateName("cpt_end_" + tclink.t.getName(), Param.NAT, "0");
					// Before : addEndCall(tclink.g2, tclink.g3, p);
                    aevt.addAbsoluteCallTo(tclink.g2, tclink.g3, tclink.g4);
					
                    tm.addSynchroRelation(t, tclink.g2, tclink.t, tclink.t.getGateG2());
                    tm.addSynchroRelation(t, tclink.g3, tclink.t, tclink.t.getGateG3());
                    tm.addSynchroRelation(t, tclink.g4, tclink.t, tclink.t.getGateG4());
                }
            }
        }
    }
    
    private void manageInfiniteLoops() {
        ActionInstance ai1, ai2;
        boolean found;
        Iterator iterator1, iterator2;
        ADActionStateWithGate adsg1, adsg2;
        ADStop adstop;
        Gate g1, g2;
        
        //looks for instances with no action in sd;
        iterator1 = actionInstances.listIterator();
        
        while (iterator1.hasNext()) {
            ai1 = (ActionInstance)(iterator1.next());
            if (ai1.nbAction == 0) {
                // look for another instance with actions and in the same msc
                iterator2 = actionInstances.listIterator();
                found = false;
                ai2 = null;
                while (iterator2.hasNext()) {
                    ai2 = (ActionInstance)(iterator2.next());
                    if ((ai2.msc == ai1.msc) && (ai2.nbAction >0)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    g1 = ai1.t.addNewGateIfApplicable("NoAction__" + ai1.ins.getName() + "__" + ai2.ins.getName() + "__" + ai1.msc.getName());
                    adsg1 = new ADActionStateWithGate(g1);
                    adsg1.setActionValue("");
                    ai1.first.addNext(adsg1);
                    adsg1.addNext(ai1.last);
                    ai1.t.getActivityDiagram().add(adsg1);
                    
                    g2 = ai2.t.addNewGateIfApplicable("NoAction__" + ai1.ins.getName() + "__" + ai2.ins.getName() + "__" + ai1.msc.getName());
                    adsg2 = new ADActionStateWithGate(g2);
                    adsg2.setActionValue("");
                    ai2.first.addNext(adsg2);
                    adstop = new ADStop();
                    adsg2.addNext(adstop);
                    ai2.t.getActivityDiagram().add(adsg2);
                    ai2.t.getActivityDiagram().add(adstop);
                    
                    // synchro link between the two tclasses
                    tm.addSynchroRelation(ai1.t, g1, ai2.t, g2);
                    //System.out.println("Adding synchro relation between " + ai1.t.getName() + " and " + ai2.t.getName());
                    
                } else {
                    // potential infinite loop
                    System.out.println("Warning: instance " + ai1.ins + " has no action in scenario " + ai1.msc.getName());
                    ai1.first.addNext(ai1.last);
                }
            }
        }
    }
	
	private ActionEvt getActionEvt(LinkedList actions, Evt evtToFind) {
		ListIterator iterator = actions.listIterator();
		ActionEvt ae;
		
		while(iterator.hasNext()) {
			ae = (ActionEvt)(iterator.next());
			if (ae.evt == evtToFind) {
				return ae;
			}
		}
		return null;
	}
    
    /*private HMSC buildCorrespondingHMSC(HMSC hmsc, Instance ins) {
     
        // Duplication of the HMSC
        HMSCNode startNode = new HMSCNode("startNode" + ins, HMSCNode.START);
        HMSC myHmsc = new HMSC(hmsc.getName(), startNode);
     
     
     
        // Remove unnecessary scenarios
     
     
     
        // Simplify the HMSC
     
     
        // Remove loops
     
        return myHmsc;
     
    }*/
}
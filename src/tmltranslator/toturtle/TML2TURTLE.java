/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
* Class TML2TURTLE
* Creation: 01/12/2005
* @version 1.0 01/12/2005
* @author Ludovic APVRILLE
* @see
*/

package tmltranslator.toturtle;

import java.util.*;

import tmltranslator.*;
import myutil.*;
import translator.*;
import ui.*;


public class TML2TURTLE {
    
    //private static int gateId;
    
    private static String nameChannelNBRNBW = "ChannelNBRNBW__";
    private static String nameChannelBRNBW = "ChannelBRNBW__";
    private static String nameChannelBRBW = "ChannelBRBW__";
    
    private static String nameEvent = "Event__";
    
    private static String nameRequest = "Request__";
    
    private TMLModeling tmlmodeling;
    private TURTLEModeling tm;
    private Vector checkingErrors;
	
    private int nbClass;
    
    public TML2TURTLE(TMLModeling _tmlmodeling) {
		//System.out.println("New TURTLE modeling");
        tmlmodeling = _tmlmodeling;
    }
    
    public Vector getCheckingErrors() {
        return checkingErrors;
    }
    
    
    public TURTLEModeling generateTURTLEModeling() {
		//System.out.println("generate TM");
		tmlmodeling.removeAllRandomSequences();
		
        tm = new TURTLEModeling();
        checkingErrors = new Vector();
        
        // Create TClasses -> same name as TML tasks
        nbClass = 0;
        //System.out.println("Tclasses");
        createTClasses();
        //System.out.println("Channels");
        createChannelTClasses();
        //System.out.println("Events");
        createEventTClasses();
        //System.out.println("Requests");
        createRequestTClasses();
        //System.out.println("AD of tclasses");
        createADOfTClasses();
        //System.out.println("All done");
        
        return tm;
    }
    
    private void createTClasses() {
        ListIterator iterator = tmlmodeling.getListIteratorTasks();
        TMLTask task;
        TClass tcl;
        
        while(iterator.hasNext()) {
            task = (TMLTask)(iterator.next());
            
            tcl = new TClass(task.getName(), true);
            tm.addTClass(tcl);
            tcl.setActivityDiagram(new ActivityDiagram());
            nbClass ++;
            makeAttributes(task, tcl);
        }
    }
    
    private void createChannelTClasses() {
        ListIterator iterator = tmlmodeling.getListIteratorChannels();
        TMLChannel channel;
        TClassChannelBRNBW tch1;
        TClassChannelNBRNBW tch2;
        TClassChannelBRBW tch3;
        String name;
        
        while(iterator.hasNext()) {
            channel = (TMLChannel)(iterator.next());
            name = getChannelString(channel);
            switch(channel.getType()) {
			case TMLChannel.BRNBW:
				tch1 = new TClassChannelBRNBW(name, channel.getName());
				tch1.makeTClass();
				tm.addTClass(tch1);
				break;
			case TMLChannel.BRBW:
				tch3 = new TClassChannelBRBW(name, channel.getName());
				tch3.makeTClass(channel.getMax());
				tm.addTClass(tch3);
				break;
			default:
				tch2 = new TClassChannelNBRNBW(name, channel.getName());
				tch2.makeTClass();
				tm.addTClass(tch2);
            }
        }
    }
    
    private String getChannelString(TMLChannel channel) {
        String name;
        switch(channel.getType()) {
		case TMLChannel.BRNBW:
			name = nameChannelBRNBW + channel.getName();
			break;
		default:
			name = nameChannelNBRNBW + channel.getName();
        }
        return name;
    }
    
    private void createEventTClasses() {
        ListIterator iterator = tmlmodeling.getListIteratorEvents();
        TMLEvent event;
        TClassEventInfinite tce;
        TClassEventFinite tcef;
        TClassEventFiniteBlocking tcefb;
        
        while(iterator.hasNext()) {
            event = (TMLEvent)(iterator.next());
            if (event.isInfinite()) {
				tce = new TClassEventInfinite(nameEvent + event.getName(), event.getName(), event.getNbOfParams());
				tce.addWriteGate();
				tce.addReadGate();
				//if (event.canBeNotified()) {
				tce.addSizeGate();
				//}
				tce.makeTClass();
				tm.addTClass(tce);
            } else {
				if (event.isBlocking()) {
					tcefb = new TClassEventFiniteBlocking(nameEvent + event.getName(), event.getName(), event.getNbOfParams(), event.getMaxSize());
					tcefb.addWriteGate();
					tcefb.addReadGate();
					//if (event.canBeNotified()) {
					tcefb.addSizeGate();
					//}
					tcefb.makeTClass();
					tm.addTClass(tcefb);
				} else {
					tcef = new TClassEventFinite(nameEvent + event.getName(), event.getName(), event.getNbOfParams(), event.getMaxSize());
					tcef.addWriteGate();
					tcef.addReadGate();
					//if (event.canBeNotified()) {
					tcef.addSizeGate();
					//}
					tcef.makeTClass();
					tm.addTClass(tcef);
				}
            }
        }
    }
    
    private void createRequestTClasses() {
        ListIterator iterator = tmlmodeling.getListIteratorRequests();
        TMLRequest request;
        TClassRequest tcr;
        ListIterator ite;
        TMLTask task;
		
        while(iterator.hasNext()) {
            request = (TMLRequest)(iterator.next());
            tcr = new TClassRequest(nameRequest + request.getName(), request.getName(), request.getNbOfParams());
            ite = request.getOriginTasks().listIterator();
            while(ite.hasNext()) {
				task = (TMLTask)(ite.next());
				tcr.addWriteGate(task.getName());
            }
            tcr.addReadGate(); // Assume that request is going to only one class
            tcr.makeTClass();
            tm.addTClass(tcr);
        }
    }
    
    private void createADOfTClasses() {
        TClass t;
        
        for(int i=0; i<nbClass; i++) {
            t = tm.getTClassAtIndex(i);
            //System.out.println("Create AD");
            createADOfTClass(t, (TMLTask)(tmlmodeling.getTasks().get(i)));
            //System.out.println("End create AD");
        }
    }
    
    private void createADOfTClass(TClass tclass, TMLTask task) {
        // For each element, make a translation
        Vector newElements = new Vector(); // elements of AD
        Vector baseElements = new Vector(); // elements of basic task
        
        //System.out.println("Making AD of " + tclass.getName());
        translateAD(newElements, baseElements, tclass, task, task.getActivityDiagram().getFirst(), null, null);
        
        // DANGER: if task may be requested, the AD must be modified!!!!
        //System.out.println("task requested?");
        if (task.isRequested()) {
            setADRequested(tclass, task);
        }
        //System.out.println("end task requested?");
        
        setGatesToTask(tclass, task);
    }
    
	/* ADJunction adjunc represents the junction to which the activity should be branched when it terminates */
    private ADComponent translateAD(Vector newElements, Vector baseElements, TClass tclass, TMLTask task, TMLActivityElement tmle, ADComponent previous, ADJunction adjunc) {
        //ADEmpty empty;
        ADActionStateWithParam adacparam, adacparam1, adacparam2, adacparam3, adacparam4;
        ADChoice adchoice;
        ADDelay addelay;
        ADTimeInterval adinterval;
        ADJunction adj, adj1, adj2;
        ADActionStateWithGate adag, adagtmp;
        //ADStop adstop;
        //ADSequence adseq;
        
        
        Gate g, g1;
        
        TMLChoice tmlchoice;
        TMLForLoop tmlforloop;
        TMLActivityElementChannel acch;
        TMLActivityElementEvent acevt;
        TMLSendRequest tmlreq;
        TMLSequence tmlseq;
        TMLSelectEvt tmlselectevt;
		TMLRandom tmlrandom;
        
        ADComponent adc, adc1, adc2;
        
        String action, tmp;
        //String param;
        Param parameter, parameter0, parameter1, parameter2;
        
        int i;
        //int j, k;
        
        // Translate AD components
        
        // START STATE
        
        //System.out.println("Call to TMLE=" + tmle.toString());
        try {
			
			if (tmle instanceof TMLStartState) {
				adc = tclass.getActivityDiagram().getStartState();
				baseElements.add(tmle);
				newElements.add(adc);
				adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adc, adjunc);
				adc.addNext(adc1);
				return adc;
				
				// STOP State
			} else if (tmle instanceof TMLStopState) {
				return endOfActivity(newElements, baseElements, tclass, adjunc);
				
				// TML Junction
			} else if (tmle instanceof TMLJunction) {
				return translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), previous, adjunc);
				
				// EXECIInterval
			} else if (tmle instanceof TMLActionState) {
				action = ((TMLActionState)tmle).getAction();
				// Eliminate cout <<
				if (printAnalyzer(action)) {
					adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), previous, adjunc);
					return adc1;
				} else {
					action = modifyString(action);
					action = removeLastSemicolon(action);
					parameter = null;
					if ((parameter = paramAnalyzer(action, tclass)) != null) {
						adacparam = new ADActionStateWithParam(parameter);
						adacparam.setActionValue(getActionValueParam(action, tclass));
						newElements.add(adacparam);
						baseElements.add(tmle);
						tclass.getActivityDiagram().add(adacparam);
						adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam, adjunc);
						adacparam.addNext(adc1);
						return adacparam;
					} else {
						adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), previous, adjunc);
						return adc1;
					}
				}
				
				// CHOICE
			} else if (tmle instanceof TMLChoice) {
				//System.out.println("TML Choice!");
				tmlchoice = (TMLChoice)tmle;
				adchoice = new ADChoice();
				newElements.add(adchoice);
				baseElements.add(tmle);
				tclass.getActivityDiagram().add(adchoice);
				
				//System.out.println("Get guards nb=" + tmlchoice.getNbGuard());
				//String guard = "";
				
				if (tmlchoice.getNbGuard() !=0 ) {
					int index1 = tmlchoice.getElseGuard(), index2 = tmlchoice.getAfterGuard();
					if (index2 != -1) {
						//System.out.println("Managing after");
						adj = new ADJunction();
						adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(index2), adchoice, adj);
						tclass.getActivityDiagram().add(adj);
					} else {
						adj = adjunc;
					}
					
					for(i=0; i<tmlchoice.getNbGuard(); i++) {
						//System.out.println("Get guards i=" + i);
						//System.out.println("ADjunc=" + adjunc);
						if (i==index1) {
							/* else guard */
							action = modifyString(tmlchoice.getValueOfElse());
						} else {
							if (tmlchoice.isStochasticGuard(i)) {
								action = "[ ]";
							} else {
								action = modifyString(tmlchoice.getGuard(i));
							}
							
						}
						adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(i), adchoice, adj);
						if (adc1 == null) {
							//System.out.println("Null adc1");
						} else {
							//System.out.println("adc1 = " +adc1);
						}
						g = tclass.addNewGateIfApplicable("branching");
						adag = new ADActionStateWithGate(g);
						adag.setActionValue("");
						adag.addNext(adc1);
						tclass.getActivityDiagram().add(adag);
						adchoice.addGuard(action);
						adchoice.addNext(adag);
					}
					//System.out.println("Return adchoice ...");
					return adchoice;
				} else {
					return endOfActivity(newElements, baseElements, tclass, adjunc);
				}
				
			} else if (tmle instanceof TMLSelectEvt) {
				tmlselectevt = (TMLSelectEvt)(tmle);
				adchoice = new ADChoice();
				newElements.add(adchoice);
				baseElements.add(tmle);
				tclass.getActivityDiagram().add(adchoice);
				for(i=0; i<tmlselectevt.getNbNext(); i++) {
					adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(i), adchoice, adjunc);
					adchoice.addNext(adc1);
					adchoice.addGuard("[]");
				}
				return adchoice;
				
                // EXECI
            } else if (tmle instanceof TMLExecI) {
                addelay = new ADDelay();
                newElements.add(addelay);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(addelay);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), addelay, adjunc);
                addelay.setValue(modifyString(((TMLExecI)tmle).getAction()));
                addelay.addNext(adc1);
                return addelay;
                
                // EXECIInterval
            } else if (tmle instanceof TMLExecIInterval) {
                adinterval = new ADTimeInterval();
                newElements.add(adinterval);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adinterval);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adinterval, adjunc);
                adinterval.setValue(modifyString(((TMLExecIInterval)tmle).getMinDelay()), modifyString(((TMLExecIInterval)tmle).getMaxDelay()));
                adinterval.addNext(adc1);
                return adinterval;
                
                // EXECC
            } else if (tmle instanceof TMLExecC) {
                addelay = new ADDelay();
                newElements.add(addelay);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(addelay);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), addelay, adjunc);
                addelay.setValue(modifyString(((TMLExecC)tmle).getAction()));
                addelay.addNext(adc1);
                return addelay;
                
                // EXECCInterval
            } else if (tmle instanceof TMLExecCInterval) {
                adinterval = new ADTimeInterval();
                newElements.add(adinterval);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adinterval);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adinterval, adjunc);
                adinterval.setValue(modifyString(((TMLExecCInterval)tmle).getMinDelay()), modifyString(((TMLExecCInterval)tmle).getMaxDelay()));
                adinterval.addNext(adc1);
                return adinterval;
				
				// DELAY
			} else if (tmle instanceof TMLDelay) {
                adinterval = new ADTimeInterval();
                newElements.add(adinterval);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adinterval);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adinterval, adjunc);
                adinterval.setValue(modifyString(((TMLDelay)tmle).getMinDelay()), modifyString(((TMLDelay)tmle).getMaxDelay()));
                adinterval.addNext(adc1);
                return adinterval;
				
				// TMLRandom
			} else if (tmle instanceof TMLRandom) {
				tmlrandom = (TMLRandom)tmle;
				
				parameter0 = tclass.addNewParamIfApplicable("min__random", Param.NAT, "0");
				parameter1 = tclass.addNewParamIfApplicable("max__random", Param.NAT, "0");
				parameter2 = tclass.addNewParamIfApplicable(tmlrandom.getVariable(), Param.NAT, "0");
				
				adacparam1 = new ADActionStateWithParam(parameter0);
				action = modifyString("min(" + tmlrandom.getMinValue() + ", " + tmlrandom.getMaxValue() + ")");
                adacparam1.setActionValue(action);
				newElements.add(adacparam1);
                baseElements.add(tmle);
				
				adacparam2 = new ADActionStateWithParam(parameter1);
				action = modifyString("max(" + tmlrandom.getMinValue() + ", " + tmlrandom.getMaxValue() + ")");
                adacparam2.setActionValue(action);
				
				adacparam3 = new ADActionStateWithParam(parameter2);
				action = modifyString("min__random");
                adacparam3.setActionValue(action);
				
				adacparam4 = new ADActionStateWithParam(parameter0);
				action = modifyString("min__random + 1");
                adacparam4.setActionValue(action);
				
				tclass.getActivityDiagram().add(adacparam1);
				
				adacparam1.addNext(adacparam2);
				
				adj1 = new ADJunction();
				adacparam2.addNext(adj1);
				
				adchoice = new ADChoice();
				adj1.addNext(adchoice);
				adchoice.addGuard("[min__random < (max__random + 1)]");
				adchoice.addNext(adacparam3);
				adchoice.addGuard("[min__random < max__random]");
				adchoice.addNext(adacparam4);
				adacparam4.addNext(adj1);
				
				tclass.getActivityDiagram().add(adacparam1);
				tclass.getActivityDiagram().add(adacparam2);
				tclass.getActivityDiagram().add(adacparam3);
				tclass.getActivityDiagram().add(adacparam4);
				tclass.getActivityDiagram().add(adj1);
				tclass.getActivityDiagram().add(adchoice);
				
				adc2 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam3, adjunc);
				adacparam3.addNext(adc2);
				
				return adacparam1;
				
                // TMLFORLOOP
            } else if (tmle instanceof TMLForLoop) {
                tmlforloop = (TMLForLoop)tmle;
                action = modifyString(tmlforloop.getInit());
                //System.out.println("FOR action = " + action);
                parameter = null;
                if ((action.length() == 0) || ((parameter = paramAnalyzer(action, tclass)) != null)) {
                    //System.out.println("parameter1 ok");
					if (action.length() != 0) {
						adacparam1 = new ADActionStateWithParam(parameter);
						adacparam1.setActionValue(getActionValueParam(action, tclass));
					} else {
						adacparam1 = null;
					}
                    
                    action = modifyString(tmlforloop.getIncrement());
                    parameter = null;
                    if ((action.length() == 0) || ((parameter = paramAnalyzer(action, tclass)) != null)) {
                        //System.out.println("New loop");
						if (action.length() != 0) {
							adacparam2 = new ADActionStateWithParam(parameter);
							adacparam2.setActionValue(getActionValueParam(action, tclass));
						} else {
							adacparam2 = null;
						}
                        
                        adchoice = new ADChoice();
                        adj1 = new ADJunction();
                        adj2 = new ADJunction();
                        
                        newElements.add(adacparam1);
                        baseElements.add(tmle);
                        
						if (adacparam1 != null) {
							tclass.getActivityDiagram().add(adacparam1);
							newElements.add(adacparam1);
						} else {
							newElements.add(adj1);
						}
						if (adacparam2 != null) {
							tclass.getActivityDiagram().add(adacparam2);
						}
                        tclass.getActivityDiagram().add(adchoice);
                        tclass.getActivityDiagram().add(adj1);
                        tclass.getActivityDiagram().add(adj2);
                        
                        if (adacparam1 != null) {
							adacparam1.addNext(adj1);
						}
                        adj1.addNext(adchoice);
						if (adacparam2 != null) {
							adacparam2.addNext(adj1);
							adj2.addNext(adacparam2);
						} else {
							adj2.addNext(adj1);
						}
                        
						action = (modifyString(tmlforloop.getCondition()));
						if (action.length() == 0) {
							action = "true";
						}
                        adchoice.addGuard("[" + action + "]");
						if (adacparam1 != null) {
							adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam1, adj2);
						} else {
							adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adj1, adj2);
						}
                        adchoice.addNext(adc1);
                        
                        action = Conversion.replaceAllChar(action, '[', "(");
                        action = Conversion.replaceAllChar(action, ']', ")");
                        adchoice.addGuard("[not(" + action + ")]");
						if (adacparam1 != null) {
							adc2 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(1), adacparam1, adjunc);
						} else {
							adc2 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(1), adj1, adjunc);
						}
                        adchoice.addNext(adc2);
                        
						if (adacparam1 != null) {
							return adacparam1;
						} else {
							return adj1;
						}
                    }
                }
                // Error! -> bad parameter
                //return  translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), previous, adjunc);
                CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Parameter undeclared in For operator:" + action);
                error.setTClass(tclass);
                checkingErrors.add(error);
                
                // TML Sequence
            } else if (tmle instanceof TMLSequence) {
                //System.out.println("TML sequence !");
                tmlseq = (TMLSequence)tmle;
                
                if (tmlseq.getNbNext() == 0) {
                    return endOfActivity(newElements, baseElements, tclass, adjunc);
                }
                
                
                if (tmlseq.getNbNext() == 1) {
                    return  translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), previous, adjunc);
                }
                
                tmlseq.sortNexts();
                // At least 2 next elements
                adj2 = null;
                adc2 = null;
                for(i=1; i<tmlseq.getNbNext(); i++) {
                    adj1 = new ADJunction();
                    if (adj2 == null) {
                        adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(i-1), previous, adj1);
                    } else {
                        adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(i-1), adj2, adj1);
                    }
                    if (adj2 == null) {
                        adc2 = adc1;
                        //newElements.add(adc1);
                        //baseElements.add(tmle);
                    } else {
                        adj2.addNext(adc1);
                    }
                    //tclass.getActivityDiagram().add(adc1);
                    tclass.getActivityDiagram().add(adj1);
                    adj2 = adj1;
                }
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(tmlseq.getNbNext()-1), previous, adjunc);
                //tclass.getActivityDiagram().add(adc1);
                adj2.addNext(adc1);
                return adc2;
                
                // TML Read Channel
            } else if (tmle instanceof TMLReadChannel) { // READ MUST BE MODIFIED
                acch = (TMLActivityElementChannel)tmle;
                
                if ((acch.getNbOfSamples().trim().compareTo("1")) == 0) {
                    g = addGateChannel("rd", acch, 0, tclass);
                    TClass tcl = tm.getTClassWithName(getChannelString(acch.getChannel(0)));
                    g1 = tcl.getGateByName("rd__" + acch.getChannel(0).getName());
                    tm.addSynchroRelation(tclass, g, tcl, g1);
                    adag = new ADActionStateWithGate(g);
                    adag.setActionValue("");
                    tclass.getActivityDiagram().add(adag);
                    adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                    adag.addNext(adc1);
                    newElements.add(adag);
                    baseElements.add(tmle);
                    return adag;
                    
                } else {
                    parameter = tclass.addNewParamIfApplicable("cpt__0", "nat", "0");
                    adacparam = new ADActionStateWithParam(parameter);
                    adacparam.setActionValue(acch.getNbOfSamples());
                    tclass.getActivityDiagram().add(adacparam);
                    
                    adj = new ADJunction();
                    tclass.getActivityDiagram().add(adj);
                    adacparam.addNext(adj);
                    
                    adchoice = new ADChoice();
                    tclass.getActivityDiagram().add(adchoice);
                    adj.addNext(adchoice);
                    
                    adacparam1 = new ADActionStateWithParam(parameter);
                    adacparam1.setActionValue("cpt__0 - 1");
                    tclass.getActivityDiagram().add(adacparam1);
                    adacparam1.addNext(adj);
                    
                    g = addGateChannel("rd", acch, 0, tclass);
                    TClass tcl = tm.getTClassWithName(getChannelString(acch.getChannel(0)));
                    g1 = tcl.getGateByName("rd__" + acch.getChannel(0).getName());
                    tm.addSynchroRelation(tclass, g, tcl, g1);
                    
                    adag = new ADActionStateWithGate(g);
                    adag.setActionValue("");
                    tclass.getActivityDiagram().add(adag);
                    adag.addNext(adacparam1);
                    
                    adchoice.addNext(adag);
                    adchoice.addGuard("[cpt__0 > 0]");
                    
                    newElements.add(adacparam);
                    baseElements.add(tmle);
                    
                    adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam, adjunc);
                    adchoice.addNext(adc1);
                    adchoice.addGuard("[cpt__0 == 0]");
                    return adacparam;
                }
                
                // TMLSendEvent
            } else if (tmle instanceof TMLSendEvent) {
                acevt = (TMLActivityElementEvent)tmle;
                g = tclass.addNewGateIfApplicable("notify__" + acevt.getEvent().getName());
                TClass tcl = tm.getTClassWithName(nameEvent + acevt.getEvent().getName());
                g1 = ((TClassEventCommon)(tcl)).getGateWrite();
                tm.addSynchroRelation(tclass, g, tcl, g1);
                
				adacparam2 = null;
				adacparam = null;
                adag = new ADActionStateWithGate(g);
                action = "";
                for (i=0; i<acevt.getNbOfParams(); i++) {
                    if (acevt.getParam(i).length() > 0) {
						if (!Conversion.isNumeralOrId(acevt.getParam(i))) {
							tmp = "ntmp__" + i;
							if (acevt.getEvent().getType(i).getType() == TMLType.NATURAL) {
								parameter = tclass.addNewParamIfApplicable(tmp, Param.NAT, "0");
							} else {
								parameter = tclass.addNewParamIfApplicable(tmp, Param.BOOL, "false");
							}
							
							adacparam1 = new ADActionStateWithParam(parameter);
							adacparam1.setActionValue(modifyString(acevt.getParam(i)));
							tclass.getActivityDiagram().add(adacparam1);
							if (adacparam == null) {
								adacparam2 = adacparam1;
								adacparam = adacparam1;
							} else {
								adacparam.addNext(adacparam1);
								adacparam = adacparam1;
							}
						} else {
							tmp = modifyString(acevt.getParam(i));
						}
                        action += "!" + modifyString(tmp);
                    }
                }
				if (adacparam != null) {
					adacparam.addNext(adag);
				}
                adag.setActionValue(action);
                
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adag);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                adag.addNext(adc1);
				
				if (adacparam2 != null) {
					newElements.add(adacparam2);
					return adacparam2;
				}
				newElements.add(adag);
                return adag;
                
                // TMLSendRequest
            } else if (tmle instanceof TMLSendRequest) {
                tmlreq = (TMLSendRequest)tmle;
                g = tclass.addNewGateIfApplicable("sendReq__" + tmlreq.getRequest().getName() + "__" + task.getName());
                TClass tcl = tm.getTClassWithName(nameRequest + tmlreq.getRequest().getName());
                //g1 = tcl.getGateByName("sendReq");
                //int index = tmlreq.getRequest().getOriginTasks().indexOf(task);
                //System.out.println("task=" + task.getName() + " index=" + index);
                //g1 = (Gate)(((TClassRequest)tcl).getGatesWrite().get(index));
				g1 = (Gate)(((TClassRequest)tcl).getGateWrite(task.getName()));
                //System.out.println("task=" + task.getName() + " index=" + index + "gate=" + g.getName());
                tm.addSynchroRelation(tclass, g, tcl, g1);
                
				adacparam2 = null;
				adacparam = null;
                adag = new ADActionStateWithGate(g);
                action = "";
                for (i=0; i<tmlreq.getNbOfParams(); i++) {
                    if (tmlreq.getParam(i).length() > 0) {
                        if (!Conversion.isNumeralOrId(tmlreq.getParam(i))) {
							tmp = "ntmp__" + i;
							if (tmlreq.getRequest().getType(i).getType() == TMLType.NATURAL) {
								parameter = tclass.addNewParamIfApplicable(tmp, Param.NAT, "0");
							} else {
								parameter = tclass.addNewParamIfApplicable(tmp, Param.BOOL, "false");
							}
							
							adacparam1 = new ADActionStateWithParam(parameter);
							adacparam1.setActionValue(modifyString(tmlreq.getParam(i)));
							tclass.getActivityDiagram().add(adacparam1);
							if (adacparam == null) {
								adacparam2 = adacparam1;
								adacparam = adacparam1;
							} else {
								adacparam.addNext(adacparam1);
								adacparam = adacparam1;
							}
						} else {
							tmp = modifyString(tmlreq.getParam(i));
						}
                        action += "!" + modifyString(tmp);
                    }
                }
				if (adacparam != null) {
					adacparam.addNext(adag);
				}
                adag.setActionValue(action);
                
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adag);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                adag.addNext(adc1);
				
				if (adacparam2 != null) {
					newElements.add(adacparam2);
					return adacparam2;
				}
				newElements.add(adag);
                return adag;
                
                // TMLWaitEvent
            } else if (tmle instanceof TMLWaitEvent) {
                acevt = (TMLActivityElementEvent)tmle;
                g = tclass.addNewGateIfApplicable("wait__" + acevt.getEvent().getName());
                TClass tcl = tm.getTClassWithName(nameEvent + acevt.getEvent().getName());
                g1 = ((TClassEventCommon)(tcl)).getGateRead();
                tm.addSynchroRelation(tclass, g, tcl, g1);
                
                adag = new ADActionStateWithGate(g);
                action = "";
                for (i=0; i<acevt.getNbOfParams(); i++) {
                    if (acevt.getParam(i).length() > 0) {
                        action += "?" + modifyString(acevt.getParam(i)) + ":" + TMLType.getLOTOSStringType(acevt.getEvent().getType(i).getType());
                    }
                }
                adag.setActionValue(action);
                
                newElements.add(adag);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adag);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                adag.addNext(adc1);
                return adag;
                
                // TMLNotifiedEvent
            } else if (tmle instanceof TMLNotifiedEvent) {
                acevt = (TMLActivityElementEvent)tmle;
                g = tclass.addNewGateIfApplicable("notified__" + acevt.getEvent().getName());
                TClass tcl = tm.getTClassWithName(nameEvent + acevt.getEvent().getName());
                g1 = ((TClassEventCommon)(tcl)).getGateSize();
                
                if (g1 == null) {
					return null;
                }
				
                tm.addSynchroRelation(tclass, g, tcl, g1);
                
                adag = new ADActionStateWithGate(g);
                action = "?" + acevt.getVariable() + ":nat";
                adag.setActionValue(action);
                
                newElements.add(adag);
                baseElements.add(tmle);
                tclass.getActivityDiagram().add(adag);
                adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                adag.addNext(adc1);
                return adag;
                
                // TMLWriteChannel
            } else if (tmle instanceof TMLWriteChannel) {
				acch = (TMLActivityElementChannel)tmle;
                if ((acch.getNbOfSamples().trim().compareTo("1")) == 0) {
					adag = null;
					adagtmp = null;
					for(int k=0; k<acch.getNbOfChannels(); k++) {
						g = addGateChannel("wr", acch, k, tclass);
						TClass tcl = tm.getTClassWithName(getChannelString(acch.getChannel(k)));
						g1 = tcl.getGateByName("wr__"+acch.getChannel(k).getName());
						tm.addSynchroRelation(tclass, g, tcl, g1);
						adag = new ADActionStateWithGate(g);
						adag.setActionValue("");
						tclass.getActivityDiagram().add(adag);
						if (adagtmp != null) {
							adagtmp.addNext(adag);
						}
						adagtmp = adag;
						
					}
					adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adag, adjunc);
                    adag.addNext(adc1);
					newElements.add(adag);
                    baseElements.add(tmle);
                    return adag;
                } else {
                    parameter = tclass.addNewParamIfApplicable("cpt__0", "nat", "0");
					adchoice = null;
					adacparam2 = null;
					
					for(int k=0; k<acch.getNbOfChannels(); k++) {
						
						adacparam = new ADActionStateWithParam(parameter);
						adacparam.setActionValue(acch.getNbOfSamples());
						tclass.getActivityDiagram().add(adacparam);
						
						if (k ==0) {
							newElements.add(adacparam);
							baseElements.add(tmle);
							adacparam2 = adacparam;
						} else {
							adchoice.addNext(adacparam);
						}
						
						adj = new ADJunction();
						tclass.getActivityDiagram().add(adj);
						adacparam.addNext(adj);
                    
						adchoice = new ADChoice();
						tclass.getActivityDiagram().add(adchoice);
						adj.addNext(adchoice);
						
						adacparam1 = new ADActionStateWithParam(parameter);
						adacparam1.setActionValue("cpt__0 - 1");
						tclass.getActivityDiagram().add(adacparam1);
						adacparam1.addNext(adj);
						
						g = addGateChannel("wr", acch, k, tclass);
						TClass tcl = tm.getTClassWithName(getChannelString(acch.getChannel(k)));
						g1 = tcl.getGateByName("wr__"+acch.getChannel(k).getName());
						tm.addSynchroRelation(tclass, g, tcl, g1);
						
						adag = new ADActionStateWithGate(g);
						adag.setActionValue("");
						tclass.getActivityDiagram().add(adag);
						adchoice.addNext(adag);
						adchoice.addGuard("[cpt__0 > 0]");
						
						adag.addNext(adacparam1);
						
						if (k == (acch.getNbOfChannels()-1)) {
							adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam1, adjunc);
							adchoice.addNext(adc1);
						}
						
						adchoice.addGuard("[cpt__0 == 0]");
					}
					
                    return adacparam2;
					
					
					
                    /*adacparam = new ADActionStateWithParam(parameter);
                    adacparam.setActionValue(acch.getNbOfSamples());
                    tclass.getActivityDiagram().add(adacparam);
                    
                    adj = new ADJunction();
                    tclass.getActivityDiagram().add(adj);
                    adacparam.addNext(adj);
                    
                    adchoice = new ADChoice();
                    tclass.getActivityDiagram().add(adchoice);
                    adj.addNext(adchoice);
                    
                    adacparam1 = new ADActionStateWithParam(parameter);
                    adacparam1.setActionValue("cpt__0 - 1");
                    tclass.getActivityDiagram().add(adacparam1);
                    adacparam1.addNext(adj);
					
					adagtmp = null;
					adag = null;
                    for(int k=0; k<acch.getNbOfChannels(); k++) {
						g = addGateChannel("wr", acch, k, tclass);
						TClass tcl = tm.getTClassWithName(getChannelString(acch.getChannel(k)));
						g1 = tcl.getGateByName("wr__"+acch.getChannel(k).getName());
						tm.addSynchroRelation(tclass, g, tcl, g1);
						
						adag = new ADActionStateWithGate(g);
						adag.setActionValue("");
						tclass.getActivityDiagram().add(adag);
						if (adagtmp == null) {
							adchoice.addNext(adag);
							adchoice.addGuard("[cpt__0 > 0]");
						} else {
							adagtmp.addNext(adag);
						}
						adagtmp = adag;
					}
                    adag.addNext(adacparam1);
                    
                    newElements.add(adacparam);
                    baseElements.add(tmle);
                    
                    adc1 = translateAD(newElements, baseElements, tclass, task, tmle.getNextElement(0), adacparam, adjunc);
                    adchoice.addNext(adc1);
                    adchoice.addGuard("[cpt__0 == 0]");
                    return adacparam;*/
                }
            }
		} catch (Exception e) {
			System.out.println("Exception in AD diagram analysis -> " + e.getMessage());
			return null;
		}
		
		return null;
		
	}
	
	private void  setADRequested(TClass tclass, TMLTask task) {
		// attributes
		int n = task.getRequest().getNbOfParams();
		int i;
		//String type;
		
		for(i=0; i<n; i++) {
			switch (task.getRequest().getType(i).getType()) {
			case TMLType.NATURAL:
				tclass.addNewParamIfApplicable("arg" + (i+1) + "__req", "nat", "0");
				break;
			default:
				tclass.addNewParamIfApplicable("arg" + (i+1) + "__req", "bool", "0");
			}
		}
		
		// Modifying AD
		ADStart start = tclass.getActivityDiagram().getStartState();
		ADComponent adc = start.getNext(0);
		ADJunction adj = new ADJunction();
		ADActionStateWithGate adag;
		//ADSequence adseq;
		Gate g, g1;
		String action;
		
		g = tclass.addNewGateIfApplicable("waitReq__" + task.getRequest().getName());
		TClass tcl = tm.getTClassWithName(nameRequest + task.getRequest().getName());
		g1 = ((TClassRequest)(tcl)).getGateRead();
		tm.addSynchroRelation(tclass, g, tcl, g1);
		
		adag = new ADActionStateWithGate(g);
		action = "";
		for (i=0; i<task.getRequest().getNbOfParams(); i++) {
			action += "?arg" + (i+1) + "__req:nat";
		}
		adag.setActionValue(action);
		
		// Search for all adcomponents which next is a stop ... Replace this next to a next to the first adjunction
		//System.out.println("Remove all elements ..");
		try {
			tm.removeAllElement(Class.forName("translator.ADStop"), adj, tclass.getActivityDiagram());
            } catch (ClassNotFoundException cnfe ) {}
            //System.out.println("All elements removed ...");
            tclass.getActivityDiagram().add(adag);
            tclass.getActivityDiagram().add(adj);
            
            // End of AD should be linked to the beginning!
            start.removeAllNext();
            start.addNext(adj);
            adj.addNext(adag);
            adag.addNext(adc);
            
            
	}
	
	private void  setGatesToTask(TClass tclass, TMLTask task) {
		setGatesEvt(tclass, task);
		setGatesRequest(tclass, task);
		setGatesChannel(tclass, task);
	}
	
	private void setGatesEvt(TClass tclass, TMLTask task) {
		ListIterator iterator = tmlmodeling.getListIteratorEvents();
		TMLEvent event;
		Gate g, g1;
		TClass tcl;
		
		while(iterator.hasNext()) {
            event = (TMLEvent)(iterator.next());
			
            if (task == event.getOriginTask()) {
				g = tclass.addNewGateIfApplicable("notify__" + event.getName());
				tcl = tm.getTClassWithName(nameEvent + event.getName());
				g1 = ((TClassEventCommon)(tcl)).getGateWrite();
				tm.addSynchroRelation(tclass, g, tcl, g1);
            }
            
            if (task == event.getDestinationTask()) {
				//Wait
                g = tclass.addNewGateIfApplicable("wait__" + event.getName());
                tcl = tm.getTClassWithName(nameEvent + event.getName());
                g1 = ((TClassEventCommon)(tcl)).getGateRead();
                tm.addSynchroRelation(tclass, g, tcl, g1);
				
                // Notified
                g = tclass.addNewGateIfApplicable("notified__" + event.getName());
                tcl = tm.getTClassWithName(nameEvent + event.getName());
                g1 = ((TClassEventCommon)(tcl)).getGateSize();
                tm.addSynchroRelation(tclass, g, tcl, g1);
            }
		}
	}
	
	private void setGatesRequest(TClass tclass, TMLTask task) {
		ListIterator iterator = tmlmodeling.getListIteratorRequests();
		TMLRequest request;
		Gate g, g1;
		TClass tcl;
		int index;
		
		while(iterator.hasNext()) {
            request = (TMLRequest)(iterator.next());
			g = tclass.addNewGateIfApplicable("sendReq__" + request.getName() + "__" + task.getName());
			tcl = tm.getTClassWithName(nameRequest + request.getName());
			//g1 = tcl.getGateByName("sendReq");
			index = request.getOriginTasks().indexOf(task);
			if (index != -1) {
                //System.out.println("task=" + task.getName() + " index=" + index);
                g1 = (Gate)(((TClassRequest)tcl).getGatesWrite().get(index));
                //System.out.println("task=" + task.getName() + " index=" + index + "gate=" + g.getName());
                tm.addSynchroRelation(tclass, g, tcl, g1);
			}
		}
	}
	
	private void setGatesChannel(TClass tclass, TMLTask task) {
		ListIterator iterator = tmlmodeling.getListIteratorChannels();
		TMLChannel channel;
		Gate g, g1;
		TClass tcl;
		//int index;
		//String name;
		
		while(iterator.hasNext()) {
            channel = (TMLChannel)(iterator.next());
			
            if (task == channel.getOriginTask()) {
				g = tclass.addNewGateIfApplicable("wr__" + channel.getName());
				tcl = tm.getTClassWithName(getChannelString(channel));
				g1 = tcl.getGateByName("wr__"+channel.getName());
				tm.addSynchroRelation(tclass, g, tcl, g1);
            }
			
            if (task == channel.getDestinationTask()) {
				g = tclass.addNewGateIfApplicable("rd__" + channel.getName());
				tcl = tm.getTClassWithName(getChannelString(channel));
				g1 = tcl.getGateByName("rd__"+channel.getName());
				tm.addSynchroRelation(tclass, g, tcl, g1);
				
			}
        }
	}
	
	
	
	
	private ADComponent endOfActivity(Vector newElements, Vector baseElements, TClass tclass, ADJunction adjunc) {
		if (adjunc == null) {
			ADStop adstop = new ADStop();
			newElements.add(adstop);
			baseElements.add(adstop);
			tclass.getActivityDiagram().add(adstop);
			return adstop;
		} else {
			return adjunc;
		}
	}
	
	private Gate addGateChannel(String name, TMLActivityElementChannel tmle, int _index, TClass tclass) {
		name = name + "__" + tmle.getChannel(_index).getName();
		return tclass.addNewGateIfApplicable(name);
	}
	
	private boolean printAnalyzer(String action) {
		action = action.trim();
		if (action.startsWith("cout") || action.startsWith("std::cout")) {
			return true;
		}
		return false;
		
	}
	
	private String modifyString(String _input) {
		_input = Conversion.replaceAllString(_input, "<<", "*");
		_input = Conversion.replaceAllString(_input, ">>", "/");
		
		// Replaces &&, || and !
		_input = Conversion.replaceAllString(_input,"&&", "and");
		_input = Conversion.replaceAllString(_input, "||", "or");
		_input = Conversion.replaceAllString(_input, "!", "not");
		_input = Conversion.replaceAllStringNonAlphanumerical(_input, "i", "i_0");
		
		return _input;
	}
	
	private void makeAttributes(TMLTask task, TClass tcl) {
		ListIterator iterator = task.getAttributes().listIterator();
		TMLAttribute tmla;
		//Param para;
		
		while(iterator.hasNext()) {
			tmla = (TMLAttribute)(iterator.next());
			switch (tmla.type.getType()) {
			case TMLType.NATURAL:
				//System.out.println("Adding nat attribute:" + modifyString(tmla.name));
				tcl.addNewParamIfApplicable(modifyString(tmla.name), "nat", modifyString(tmla.initialValue));
				break;
			default:
				tcl.addNewParamIfApplicable(modifyString(tmla.name), "bool", modifyString(tmla.initialValue));
			}
		}
	}
	
	// Returns Param if action starts with a Param ...
	private Param paramAnalyzer(String action, TClass tcl) {
		int index = action.indexOf("=");
		if (index < 0) {
			// ++ expression ?
			index = action.indexOf("++");
			if (index < 0) {
				// -- expression
				index = action.indexOf("--");
				if (index < 0) {
					return null;
				}
			}
		}
		
		action = action.substring(0, index);
		action = action.trim();
		
		return tcl.getParamByName(action);
	}
	
	private String getActionValueParam(String action, TClass tcl) {
		int index = action.indexOf("=");
		if (index < 0) {
			// ++ expression ?
			index = action.indexOf("++");
			if (index < 0) {
				// -- expression
				index = action.indexOf("--");
				if (index < 0) {
					return null;
				} else {
					action = action.substring(0, index);
					action = action.trim();
					return action + "-1";
				}
			} else {
				action = action.substring(0, index);
				action = action.trim();
				return action + "+1";
			}
		}
		
		return action = action.substring(index+1, action.length()).trim();
	}
	
	private String removeLastSemicolon(String action) {
		action = action.trim();
		if (action.charAt(action.length()-1) == ';') {
			return action.substring(0, action.length()-1);
		}
		return action;
	}
}

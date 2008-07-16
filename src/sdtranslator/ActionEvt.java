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
 * Class Evt
 * Creation: 17/08/2004
 * @version 1.1 17/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package sdtranslator;

import sddescription.*;
import translator.*;
import myutil.*;

import java.util.*;

public class ActionEvt {
    
    //private ADComponent paraTC;
    private ADActionStateWithGate firstActionAfterTC;
    private ADComponent first;
    private ADComponent last;
	private ADChoice guarded;
	private ADComponent mix;
    //private boolean limit_0Added = false;
    public Evt evt;
    public ActivityDiagram ad;
    public TClass t;
    
    
    
    public ActionEvt(Evt _evt, ActivityDiagram _ad) {
        evt = _evt;
        first = new ADParallel();
        last = new ADParallel();
        ad = _ad;
        ad.add(first);
        ad.add(last);
    }
    
    public Evt getEvt() {
        return evt;
    }
	
	private void setGuardedEvt(ADChoice adch) {
		guarded = adch;
	}
	
	public ADChoice getGuarded() {
		return guarded;
	}     
	
	private void setMix(ADJunction adj) {
		mix = adj;
	}
	
	public ADComponent getMix() {
		return mix;
	}
    
    private void makeAD(ADComponent nextfirst, ADComponent lastnext) {
        first.addNext(nextfirst);
        lastnext.addNext(last);
        ad.add(nextfirst);
        if (nextfirst != lastnext) {
            ad.add(lastnext);
        }
    }
    
	private int getNbParam(String action) {
		int index1 = action.indexOf('(');
			if (index1 == -1) {
				return 0;
			}
		return Conversion.nbOf(action, ',') + 1;
		
	}
	
	private String transformToTURTLEFormat(String action, boolean send) {
		int index1 = action.indexOf('(');
			if (index1 == -1) {
				return action;
			}
			action = action.trim();
			String ret = action.substring(0, index1);
			if (send) {
				ret += "!" + action.substring(index1 +1, action.length() -1);
			} else {
				ret += "?" + action.substring(index1 +1, action.length() -1);
			}
			if (send) {
				ret = Conversion.replaceAllChar(ret, ',', "!");
			} else {
				ret = Conversion.replaceAllChar(ret, ',', "?");
			}		
			ret = Conversion.replaceAllChar(ret, ' ', "");
			return ret;
	}
	
    // builds the actions corresponding to the node and adds various components to the activity diagram
    public EvtToLink translateEvt(TClass _t, ActivityDiagram ad) {
        t = _t;
        ADActionStateWithGate adsg = null;
        ADActionStateWithParam adsp;
        ADTimeInterval adti;
        //ADActionStateWithParam adsp;
        String action = evt.getActionId();
        int index;
        String gs, ps, actionValue;
        Gate g;
        Param p;
        EvtToLink etl = null;
        String nameGate, delay;
		int nbParam = getNbParam(action);
		ADChoice adch;
		ADJunction adj;
        
        
        switch(evt.getType()) {
            case Evt.SYNC:
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.SYNC);
                break;
            case Evt.SEND_SYNC:
				//System.out.println("Init action send = " + action);
				action = transformToTURTLEFormat(action, true);
				t.addParamFromAction(action);
				//System.out.println("Modified action send = " + action);
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.SYNC);
                break;
            case Evt.RECV_SYNC:
				//System.out.println("Init action recv = " + action);
				action = transformToTURTLEFormat(action, false);
				t.addParamFromAction(action);
				//System.out.println("Modified action recv = " + action);
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.SYNC);
                break;
            case Evt.TIMER_SET:
                nameGate = evt.getTimerName();
                delay = evt.getTimerValue();
                nameGate = BasicTimer.makeTimerSetGate(nameGate, t);
                g = t.addNewGateIfApplicable(nameGate);
                adsg = new ADActionStateWithGate(g);
                adsg.setActionValue("!" + delay);
                makeAD(adsg, adsg);
                etl = new EvtToLink(evt, t, g, EvtToLink.TIMER_SET);
                break;
            case Evt.TIMER_EXP:
                nameGate = evt.getTimerName();
                nameGate = BasicTimer.makeTimerExpGate(nameGate, t);
                g = t.addNewGateIfApplicable(nameGate);
                adsg = new ADActionStateWithGate(g);
                adsg.setActionValue("");
                makeAD(adsg, adsg);
                etl = new EvtToLink(evt, t, g, EvtToLink.TIMER_EXP);
                break;
            case Evt.TIMER_RESET:
                nameGate = evt.getTimerName();
                nameGate = BasicTimer.makeTimerResetGate(nameGate, t);
                g = t.addNewGateIfApplicable(nameGate);
                adsg = new ADActionStateWithGate(g);
                adsg.setActionValue("");
                makeAD(adsg, adsg);
                etl = new EvtToLink(evt, t, g, EvtToLink.TIMER_RESET);
                break;
            case Evt.SEND_MSG:
				action = transformToTURTLEFormat(action, true);
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.SEND_MSG, nbParam);
                break;
            case Evt.RECV_MSG:
				action = transformToTURTLEFormat(action, false);
				t.addParamFromAction(action);
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.RECV_MSG, nbParam);
                break;
            case Evt.INTERNAL_ACTION:
				action = transformToTURTLEFormat(action, true);
                gs = t.getGateNameFromActionState(action);
                g = t.addNewGateIfApplicable(gs);
                adsg = new ADActionStateWithGate(g);
                
                actionValue = t.getActionValueFromActionState(action);
                actionValue = TURTLEModeling.addTypeToDataReceiving(t, actionValue);
                adsg.setActionValue(actionValue);
                
                makeAD(adsg, adsg);
                
                etl = new EvtToLink(evt, t, g, EvtToLink.INTERNAL_ACTION);
                break;
            case Evt.VARIABLE_SET:
                gs = t.getParamNameFromActionState(action);
                actionValue = t.getExprValueFromActionState(action);
                p = t.addNewParamIfApplicable(gs, Param.NAT, "0");
                adsp = new ADActionStateWithParam(p);
                adsp.setActionValue(actionValue);
                
                makeAD(adsp, adsp);
                
                etl = new EvtToLink(evt, t, p, EvtToLink.VARIABLE_SET);
                break;
                
            case Evt.TIME_INTERVAL:
                adti = new ADTimeInterval();
                index = action.indexOf(",");
                adti.setValue(action.substring(0, index), action.substring(index+1, action.length())); 
                
                makeAD(adti, adti);
                
                etl = new EvtToLink(evt, t, EvtToLink.TIME_INTERVAL);
                break;
			case Evt.GUARD:
				adch = new ADChoice();
				//adch.addGuard("[" + evt.getActionId() + "]");
				setGuardedEvt(adch);
				makeAD(adch, adch);
				break;
			case Evt.ELSE_GUARD:
				adch = new ADChoice();
				setGuardedEvt(adch);
				//adch.addGuard("[" + evt.getActionId() + "]");
				makeAD(adch, adch);
				break;
			case Evt.END_GUARD:
				adj = new ADJunction();
				setMix(adj);
				makeAD(adj, adj);
				break;
            default:
                first.addNext(last);
        }
        firstActionAfterTC = adsg;
        return etl;
    }
    
    public ADComponent getFirst() {
        return first;
    }
    
    public ADComponent getLast() {
        return last;
    }
    
    public void addBasicTCTo(int time1, int time2) {
        ADDelay add = new ADDelay();
        add.setValue(""+time1);
        ADLatency adlat = new ADLatency();
        adlat.setValue(""+(time2-time1));
        
        /*ADTimeInterval adti = new ADTimeInterval();
        adti.setValue(""+time1, ""+time2);
        adti.setNewNext(first.getAllNext());*/
        adlat.setNewNext(first.getAllNext());
        add.addNext(adlat);
        first.setNewNext(new Vector());
        first.addNext(add);
        ad.add(add);
        ad.add(adlat);
        
        if (firstActionAfterTC != null) {
            //System.out.println("Limit added to first action gate: " + firstActionAfterTC.getGate().getName());
            firstActionAfterTC.setLimitOnGate("{" + (time2-time1) + "}");
        }
    }
    
    public void addBeginCallTo(Gate g, Param p) {
        System.out.println("Modifying begin with " + g);
        t.addGate(g);
        ADActionStateWithGate adsg = new ADActionStateWithGate(g);
        adsg.setActionValue("!"+p.getName());
        ADActionStateWithParam adincrement = new ADActionStateWithParam(p);
        adincrement.setActionValue(p.getName() + "+1");
        last.addNext(adsg);
        last = new ADParallel();
        adsg.addNext(adincrement);
        adincrement.addNext(last);
        ad.add(last);
        ad.add(adsg);
        ad.add(adincrement);
    }
	
	 public void addBeginRTCCallTo(Gate g) {
        System.out.println("Modifying begin with " + g);
        t.addGate(g);
        ADActionStateWithGate adsg = new ADActionStateWithGate(g);
        last.addNext(adsg);
        last = new ADParallel();
        adsg.addNext(last);
        ad.add(last);
        ad.add(adsg);
    }
    
	public void addAbsoluteCallTo(Gate g_go, Gate g_ok, Gate g_expire) {
		t.addGate(g_go);
        t.addGate(g_ok);
		t.addGate(g_expire);
		
		ADActionStateWithGate adsg1 = new ADActionStateWithGate(g_go);
		ADChoice adch = new ADChoice();
		adsg1.addNext(adch);
		adch.addNext(first);
		ADActionStateWithGate adsg2 = new ADActionStateWithGate(g_ok);
		last.addNext(adsg2);
		last = new ADParallel();
		adsg2.addNext(last);
		
		ADActionStateWithGate adsg3 = new ADActionStateWithGate(g_expire);
		adch.addNext(adsg3);
		ADStop adstop = new ADStop(); 
		adsg3.addNext(adstop);
		
		ad.add(adsg1);
		ad.add(adch);
		ad.add(adsg2);
		ad.add(adsg3);
		ad.add(adstop);
		ad.add(last);
		
		first = adsg1;
	}
	
    public void addEndCallTo(Gate g_go, Gate g_ok, Param p) {
        System.out.println("Modifying end with " + g_go);
        t.addGate(g_go);
        t.addGate(g_ok);
        
        ADActionStateWithGate adsg1 = new ADActionStateWithGate(g_go);
        adsg1.setActionValue("!" + p.getName());
        adsg1.setNewNext(first.getAllNext());
        first.setNewNext(new Vector());
        first.addNext(adsg1);
        
        ADActionStateWithGate adsg2 = new ADActionStateWithGate(g_ok);
        adsg2.setActionValue("{0}!"+p.getName());
        
        ADActionStateWithParam adincrement = new ADActionStateWithParam(p);
        adincrement.setActionValue(p.getName() + "+1");
        
        last.addNext(adsg2);
        last = new ADParallel();
        adsg2.addNext(adincrement);
        adincrement.addNext(last);
        ad.add(adincrement);
        ad.add(last);
        ad.add(adsg1);
        ad.add(adsg2);
        
        /*t.addGate(g_expire);
         
        ADPreempt adp = new ADPreempt();
         
        ADActionStateWithGate adsg1 = new ADActionStateWithGate(g_end);
        adsg1.setActionValue("!" + p.getName());
        adsg1.setNewNext(first.getAllNext());
         
        ADStop ads = new ADStop();
         
        ADActionStateWithGate adsg2 = new ADActionStateWithGate(g_expire);
        adsg2.setActionValue("!"+p.getName());
        adsg2.addNext(ads);
         
        adp.addNext(adsg1);
        adp.addNext(adsg2);
         
        first.setNewNext(new Vector());
        first.addNext(adp);
         
        ad.add(adp);
        ad.add(adsg1);
        ad.add(adsg2);
        ad.add(ads);
         
        ADActionStateWithGate adsg3 = new ADActionStateWithGate(g_cancel);
        adsg3.setActionValue("!" + p.getName());
         
        ADActionStateWithParam adincrement = new ADActionStateWithParam(p);
        adincrement.setActionValue(p.getName() + "+1");
         
        last.addNext(adsg3);
        last = new ADParallel();
        adsg3.addNext(adincrement);
        adincrement.addNext(last);
        ad.add(adincrement);
        ad.add(last);
        ad.add(adsg3);*/
    }
	
	 public void addEndRTCCallTo(Gate g_check, Gate g_end, Gate g_expire) {
		 addAbsoluteCallTo(g_check, g_end, g_expire);
	 }
}
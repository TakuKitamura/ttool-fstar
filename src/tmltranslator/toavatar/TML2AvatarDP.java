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
 * Class AvatarDesignPanelTranslator
 * Creation: 18/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.toavatar;

import java.util.*;
import java.awt.*;
import tmltranslator.*;
import ui.*;
import myutil.*;
import ui.avatarbd.*;
import ui.avatarsmd.*;

import avatartranslator.*;
//import translator.*;
import ui.window.*;


public class TML2AvatarDP {


    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected TMLMapping tmlmap;
    public AvatarDesignPanel adp;
    private List<String> signals;
    private Map<AvatarTransition, TGConnectingPoint> tranSourceMap = new HashMap<AvatarTransition, TGConnectingPoint>();
    private Map<AvatarTransition, AvatarStateMachineElement> tranDestMap = new HashMap<AvatarTransition, AvatarStateMachineElement>();
    private Map<AvatarStateMachineElement, TGConnectingPoint> locMap = new HashMap<AvatarStateMachineElement, TGConnectingPoint>();
    private Map<AvatarStateMachineElement, TGComponent> SMDMap = new HashMap<AvatarStateMachineElement, TGComponent>();
    public Map<String, Set<String>> originDestMap = new HashMap<String, Set<String>>();
    public Map<String, AvatarBDBlock> blockMap = new HashMap<String, AvatarBDBlock>();
    public TML2AvatarDP(TMLMapping tmlmapping) {
        tmlmap = tmlmapping;
    }

    public void commMap(){
	TMLModeling tmlmodel=tmlmap.getTMLModeling();
	for (TMLTask t: tmlmodel.getTasks()){
	    Set<String> hs = new HashSet<String>();
	 
	    LinkedList ll= tmlmodel.getChannelsToMe(t);
      	    ListIterator iterator = ll.listIterator();
            TMLChannel chanl;
            while(iterator.hasNext()) {
                chanl = (TMLChannel)(iterator.next()); 
		for (TMLTask dt: chanl.getDestinationTasks()){
		    hs.add(dt.getName());
		}
	    }
	    originDestMap.put(t.getName(), hs);
	    //check channels
	    //get connections
	//Do we care about requests/events
	}
    }
    public void addStates(AvatarStateMachineElement asme, int x, int y, AvatarSMDPanel smp){
	TGConnectingPoint tp = new TGConnectingPoint(null, x, y, false, false);
	if (asme instanceof AvatarStartState){
	    AvatarSMDStartState smdss = new AvatarSMDStartState(x, y, x, x*2, y, y*2, false, null, smp);
	    smp.addComponent(smdss, x, y, false, true);
	    SMDMap.put(asme, smdss);
	    tp = smdss.tgconnectingPointAtIndex(0);
	    locMap.put(asme, tp);
	}
	if (asme instanceof AvatarTransition){
	   //
	}
	if (asme instanceof AvatarActionOnSignal){
	    avatartranslator.AvatarSignal sig = ((AvatarActionOnSignal) asme).getSignal();
	    if (sig.isOut()){
		AvatarSMDReceiveSignal smdrs = new AvatarSMDReceiveSignal(x, y, x, x*2, y, y*2, false, null, smp);
		smp.addComponent(smdrs, x, y, false, true);
	        smdrs.setValue(sig.getName().split("__")[sig.getName().split("__").length-1]);
	        smdrs.recalculateSize();
		SMDMap.put(asme, smdrs);
	        tp = smdrs.getFreeTGConnectingPoint(x+smdrs.getWidth()/2,y+smdrs.getHeight());
		TGConnectingPoint tp2 = smdrs.getFreeTGConnectingPoint(x+smdrs.getWidth()/2,y);
	    	locMap.put(asme, tp2);

	    }
	    else {
		AvatarSMDSendSignal smdss = new AvatarSMDSendSignal(x, y, x, x*2, y, y*2, false, null, smp);
		smp.addComponent(smdss, x, y, false, true);
	        smdss.setValue(sig.getName().split("__")[sig.getName().split("__").length-1]);
	        smdss.recalculateSize();
		SMDMap.put(asme, smdss);
	    	tp = smdss.getFreeTGConnectingPoint(x+smdss.getWidth()/2,y+smdss.getHeight());
		TGConnectingPoint tp2 = smdss.getFreeTGConnectingPoint(x+smdss.getWidth()/2,y);
	    	locMap.put(asme, tp2);
	    }

	}
	if (asme instanceof AvatarStopState){
	    AvatarSMDStopState smdstop = new AvatarSMDStopState(x, y, x, x*2, y, y*2, false, null, smp);
	    SMDMap.put(asme, smdstop);
	    smp.addComponent(smdstop, x, y, false, true);
	    tp = smdstop.tgconnectingPointAtIndex(0);
	    locMap.put(asme, tp);
	}
	if (asme instanceof AvatarState){
	    //check if empty checker state
	    if (asme.getName().contains("signalstate_")){
		//don't add the state, ignore next transition, 
		if (asme.getNexts().size()==1){
		    AvatarStateMachineElement next = asme.getNext(0).getNext(0);
		    //Reroute transition
		    for (AvatarTransition at: tranDestMap.keySet()){
			if (tranDestMap.get(at) == asme){
			    tranDestMap.put(at, next);
			}
		    }
		    addStates(next, x, y, smp);
		    return;
		}
	    }
	    AvatarSMDState smdstate = new AvatarSMDState(x, y, x, x*2, y, y*2, false, null, smp);
	    smp.addComponent(smdstate, x, y, false, true);
	    smdstate.setValue(asme.getName());
	    smdstate.recalculateSize();
	    tp = smdstate.getFreeTGConnectingPoint(x+smdstate.getWidth()/2,y+smdstate.getHeight());
	    TGConnectingPoint tp2 = smdstate.getFreeTGConnectingPoint(x+smdstate.getWidth()/2,y);
	    locMap.put(asme, tp2);
	}
	int i=1;
	int diff=400;
	int ydiff=100;
	for (AvatarStateMachineElement el:asme.getNexts()){
	    if (el instanceof AvatarTransition){
		tranSourceMap.put((AvatarTransition) el, tp);
	    }
	    else {
		AvatarTransition t = (AvatarTransition) asme;
		tranDestMap.put(t, el);
	    }
	    addStates(el, diff*i, y+ydiff, smp);
	    i++;  
	}
	return;
    }
    public void translate() {
	TML2Avatar tml2av = new TML2Avatar(tmlmap);
	//Create AvatarDesignDiagram
	AvatarBDPanel abd = adp.abdp;
	AvatarSpecification avspec = tml2av.generateAvatarSpec("1");
	//Find all blocks, create blocks from left
	int xpos=10;
	int ypos=10;
	for (AvatarBlock ab:avspec.getListOfBlocks()){
	//Crypto blocks?
	    AvatarBDBlock bl = new AvatarBDBlock(xpos, ypos, xpos, xpos*2, ypos, ypos*2, false, null, abd);
	    tranSourceMap.clear();
	    bl.setValue(ab.getName().split("__")[1]);
	    abd.changeStateMachineTabName ("Block0", bl.getValue());
	    blockMap.put(bl.getValue(), abd);
	    abd.addComponent(bl, xpos, ypos, false, true);
	    xpos+=400;
	    //Build the state machine
	    int smx=400;
	    int smy=40;
	    AvatarSMDPanel smp = adp.getAvatarSMDPanel(bl.getValue());
	    if (smp==null){
		System.out.println("can't find");
		return;
	    }
	    smp.removeAll();
	    AvatarStateMachine asm = ab.getStateMachine();
	    //Remove the empty check states
	    
	    AvatarStartState start= asm.getStartState();
	    addStates(start, smx, smy, smp);
	    //Add transitions
	    for (AvatarTransition t: tranSourceMap.keySet()){
		TGConnectingPoint p1 = tranSourceMap.get(t);
		TGConnectingPoint p2 = locMap.get(tranDestMap.get(t));
		Vector points = new Vector();
		if (p1==null || p2 ==null){
		    System.out.println("Missing point");
		    return;
		}
		AvatarSMDConnector SMDcon = new AvatarSMDConnector((int) p1.getX(), (int) p1.getY(), (int) p1.getX(), (int) p1.getY(), (int) p1.getX(), (int) p1.getY(), true, null, smp, p1, p2, points);	
		smp.addComponent(SMDcon, (int) p1.getX(), (int) p1.getY(), false, true);
	    }
	}

	


	//Add Relations
	for (String s: originDestMap.keySet()){
	AvatarBDCompositionConnector conn = new AvatarBDCompositionConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint);
	
	ypos+=100;
	//Add Pragmas
	AvatarBDPragma pragma=new AvatarBDPragma(xpos, ypos, xpos, xpos*2, ypos, ypos*2, false, null,abd);
	String[] arr = new String[avspec.getPragmas().size()];
	String s="";
	int i=0;
	for (AvatarPragma p: avspec.getPragmas()){
	    System.out.println(p.getName());
	    arr[i] = p.getName();
	    s=s.concat(p.getName()+"\n");
	    i++;
	}
	pragma.setValue(s);
	pragma.makeValue();
	abd.addComponent(pragma, xpos, ypos, false,true);

	System.out.println("val "+pragma.getValues().length);

    }





}

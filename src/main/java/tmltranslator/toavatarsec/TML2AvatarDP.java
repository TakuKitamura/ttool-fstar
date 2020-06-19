/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */


package tmltranslator.toavatarsec;

import avatartranslator.*;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import ui.AvatarDesignPanel;
import ui.TAttribute;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.avatarbd.AvatarBDBlock;
import ui.avatarbd.AvatarBDPanel;
import ui.avatarbd.AvatarBDPortConnector;
import ui.avatarbd.AvatarBDPragma;
import ui.avatarsmd.*;

import java.awt.*;
import java.util.*;

//import translator.*;


/**
 * Class TML2AvatarDP
 * Creation: 05/10/2016
 *
 * @author Letitia LI, Ludovic APVRILLE
 */
public class TML2AvatarDP {

    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected TMLMapping tmlmap;
    public AvatarDesignPanel adp;

    private Map<AvatarTransition, TGConnectingPoint> tranSourceMap = new HashMap<AvatarTransition, TGConnectingPoint>();
    private Map<AvatarTransition, AvatarStateMachineElement> tranDestMap = new HashMap<AvatarTransition, AvatarStateMachineElement>();
    private Map<AvatarStateMachineElement, TGConnectingPoint> locMap = new HashMap<AvatarStateMachineElement, TGConnectingPoint>();
    private Map<AvatarStateMachineElement, TGComponent> SMDMap = new HashMap<AvatarStateMachineElement, TGComponent>();
    public Map<String, Set<String>> originDestMap = new HashMap<String, Set<String>>();
    public Map<String, AvatarBDBlock> blockMap = new HashMap<String, AvatarBDBlock>();
    boolean mc;
    boolean security;
    AvatarSpecification avspec;

    public TML2AvatarDP(TMLMapping tmlmapping, boolean modelcheck, boolean sec) {
        tmlmap = tmlmapping;
        mc = modelcheck;
        security = sec;
    }

    public TML2AvatarDP(AvatarSpecification av) {
        avspec = av;
    }

    public void commMap(AvatarSpecification avspec) {
        //Create a map of all connections
    /*TMLModeling tmlmodel=tmlmap.getTMLModeling();
	for (TMLTask t: tmlmodel.getTasks()){
	    Set<String> hs = new HashSet<String>();
	    //Iterate through channels
	    LinkedList ll= tmlmodel.getChannelsToMe(t);
      	    ListIterator iterator = ll.listIterator();
            TMLChannel chanl;
	    String name="";
            while(iterator.hasNext()) {
                chanl = (TMLChannel)(iterator.next()); 
		for (TMLTask dt: chanl.getOriginTasks()){
		    name= dt.getName();
		    hs.add(name.split("__")[name.split("__").length-1]);
		}
		name= chanl.getOriginTask().getName();
		hs.add(name.split("__")[name.split("__").length-1]);
	    }
	    name=t.getName();
	    originDestMap.put(name.split("__")[name.split("__").length-1], hs);
	    */
        for (AvatarRelation ar : avspec.getRelations()) {

            String bl1 = ar.block1.getName();
            String bl2 = ar.block2.getName();
            if (originDestMap.containsKey(bl1.split("__")[bl1.split("__").length - 1])) {
                originDestMap.get(bl1.split("__")[bl1.split("__").length - 1]).add(bl2.split("__")[bl2.split("__").length - 1]);
            } else {
                Set<String> hs = new HashSet<String>();
                hs.add(bl2.split("__")[bl2.split("__").length - 1]);
                originDestMap.put(bl1.split("__")[bl1.split("__").length - 1], hs);
            }
        }

    }

    public void addStates(AvatarStateMachineElement asme, int x, int y, AvatarSMDPanel smp, AvatarBDBlock bl) {
        TGConnectingPoint tp = new TGConnectingPoint(null, x, y, false, false);
        if (asme instanceof AvatarStartState) {
            AvatarSMDStartState smdss = new AvatarSMDStartState(x, y, x, x * 2, y, y * 2, false, null, smp);
            smp.addComponent(smdss, x, y, false, true);
            SMDMap.put(asme, smdss);
            tp = smdss.tgconnectingPointAtIndex(0);
            locMap.put(asme, tp);
        }
        if (asme instanceof AvatarTransition) {
            //
        }
        if (asme instanceof AvatarActionOnSignal) {
            avatartranslator.AvatarSignal sig = ((AvatarActionOnSignal) asme).getSignal();
            if (sig.isIn()) {
                AvatarSMDReceiveSignal smdrs = new AvatarSMDReceiveSignal(x, y, x, x * 2, y, y * 2, false, null, smp);
                smp.addComponent(smdrs, x, y, false, true);
                String name = sig.getName().split("__")[sig.getName().split("__").length - 1];
                smdrs.setValue(name + "()");
                sig.setName(name);
                smdrs.recalculateSize();
                SMDMap.put(asme, smdrs);
                tp = smdrs.getFreeTGConnectingPoint(x + smdrs.getWidth() / 2, y + smdrs.getHeight());
                TGConnectingPoint tp2 = smdrs.getFreeTGConnectingPoint(x + smdrs.getWidth() / 2, y);
                locMap.put(asme, tp2);
                if (bl.getAvatarSignalFromName(name) == null) {
                    bl.addSignal(new ui.AvatarSignal(0, name, new String[0], new String[0]));
                }

            } else {
                AvatarSMDSendSignal smdss = new AvatarSMDSendSignal(x, y, x, x * 2, y, y * 2, false, null, smp);
                smp.addComponent(smdss, x, y, false, true);
                String name = sig.getName().split("__")[sig.getName().split("__").length - 1];
                smdss.setValue(name + "()");
                sig.setName(name);
                smdss.recalculateSize();
                SMDMap.put(asme, smdss);
                tp = smdss.getFreeTGConnectingPoint(x + smdss.getWidth() / 2, y + smdss.getHeight());
                TGConnectingPoint tp2 = smdss.getFreeTGConnectingPoint(x + smdss.getWidth() / 2, y);
                locMap.put(asme, tp2);
                if (bl.getAvatarSignalFromName(name) == null) {
                    bl.addSignal(new ui.AvatarSignal(1, name, new String[0], new String[0]));
                }
            }

        }
        if (asme instanceof AvatarStopState) {
            AvatarSMDStopState smdstop = new AvatarSMDStopState(x, y, x, x * 2, y, y * 2, false, null, smp);
            SMDMap.put(asme, smdstop);
            smp.addComponent(smdstop, x, y, false, true);
            tp = smdstop.tgconnectingPointAtIndex(0);
            locMap.put(asme, tp);
        }
        if (asme instanceof AvatarState) {
            //check if empty checker state
            if (asme.getName().contains("signalstate_")) {
                //don't add the state, ignore next transition, 
                if (asme.getNexts().size() == 1) {
                    AvatarStateMachineElement next = asme.getNext(0).getNext(0);
                    //Reroute transition
                    for (AvatarTransition at : tranDestMap.keySet()) {
                        if (tranDestMap.get(at) == asme) {
                            tranDestMap.put(at, next);
                        }
                    }
                    addStates(next, x, y, smp, bl);
                    return;
                }
            }
            AvatarSMDState smdstate = new AvatarSMDState(x, y, x, x * 2, y, y * 2, false, null, smp);
            smp.addComponent(smdstate, x, y, false, true);
            smdstate.setValue(asme.getName());
            smdstate.recalculateSize();
            SMDMap.put(asme, smdstate);
            tp = smdstate.getFreeTGConnectingPoint(x + smdstate.getWidth() / 2, y + smdstate.getHeight());
            TGConnectingPoint tp2 = smdstate.getFreeTGConnectingPoint(x + smdstate.getWidth() / 2, y);
            locMap.put(asme, tp2);
        }
        int i = 1;
        int diff = 400;
        int ydiff = 50;
        for (AvatarStateMachineElement el : asme.getNexts()) {
            if (el instanceof AvatarTransition) {
                tranSourceMap.put((AvatarTransition) el, tp);
            } else {
                AvatarTransition t = (AvatarTransition) asme;
                tranDestMap.put(t, el);
            }
            if (!SMDMap.containsKey(el)) {
                addStates(el, diff * i, y + ydiff, smp, bl);
                i++;
            }
        }
        return;
    }

    public void translate() {
        TML2Avatar tml2av = new TML2Avatar(tmlmap, mc, security);
        avspec = tml2av.generateAvatarSpec("1");
        drawPanel();
        //Create AvatarDesignDiagram
    }

    public void drawPanel() {
        if (adp == null) {
            return;
        }
        if (avspec == null) {
            return;
        }
        AvatarBDPanel abd = adp.abdp;

        //Find all blocks, create blocks from left
        int xpos = 10;
        int ypos = 10;
        for (AvatarBlock ab : avspec.getListOfBlocks()) {
            //Crypto blocks?
            AvatarBDBlock father = null;
            if (ab.getFather() != null) {
                father = blockMap.get(ab.getFather().getName().split("__")[1]);

            }
            AvatarBDBlock bl = new AvatarBDBlock(xpos, ypos, xpos, xpos * 2, ypos, ypos * 2, false, father, abd);
            tranSourceMap.clear();
            bl.setValue(ab.getName().split("__")[1]);
            abd.changeStateMachineTabName("Block0", bl.getValue());
            blockMap.put(bl.getValue(), bl);
            abd.addComponent(bl, xpos, ypos, false, true);
            for (AvatarAttribute attr : ab.getAttributes()) {
                int type = 5;
                if (attr.getType() == AvatarType.BOOLEAN) {
                    type = 4;
                }
                if (attr.getType() == AvatarType.INTEGER) {
                    type = 0;
                }
                bl.addAttribute(new TAttribute(0, attr.getName(), attr.getType().getDefaultInitialValue(), type));
                if (attr.getName().equals("key")) {
                    bl.addCryptoElements();
                }
            }
            // xpos+=300;
            //Build the state machine
            int smx = 400;
            int smy = 40;
            AvatarSMDPanel smp = adp.getAvatarSMDPanel(bl.getValue());
            if (smp == null) {
                TraceManager.addDev("can't find SMP");
                return;
            }
            smp.removeAll();
            AvatarStateMachine asm = ab.getStateMachine();
            //Remove the empty check states

            AvatarStartState start = asm.getStartState();
            addStates(start, smx, smy, smp, bl);
            //Add transitions
            for (AvatarTransition t : tranSourceMap.keySet()) {
                TGConnectingPoint p1 = tranSourceMap.get(t);
                TGConnectingPoint p2 = locMap.get(tranDestMap.get(t));
                Vector<Point> points = new Vector<>();
                if (p1 == null || p2 == null) {
                    TraceManager.addDev("Missing point");
                    return;
                }
                AvatarSMDConnector SMDcon = new AvatarSMDConnector(p1.getX(), p1.getY(), p1.getX(), p1.getY(), p1.getX(), p1.getY(), true, null, smp, p1, p2, points);
                String action = "";
                if (t.getActions().size() == 0) {
                    action = "";
                } else {
                    action = t.getActions().get(0).toString();
                }
                SMDcon.setTransitionInfo(t.getGuard().toString(), action);
                smp.addComponent(SMDcon, p1.getX(), p1.getY(), false, true);
            }
        }


        commMap(avspec);
        //Add Relations

        for (String bl1 : originDestMap.keySet()) {
            for (String bl2 : originDestMap.get(bl1)) {
                Vector<Point> points = new Vector<>();

                //Add Relations to connector
                for (AvatarRelation ar : avspec.getRelations()) {
                    if (ar.block1.getName().contains(bl1) && ar.block2.getName().contains(bl2) || ar.block1.getName().contains(bl2) && ar.block2.getName().contains(bl1)) {
                        //TGConnectingPoint p1= blockMap.get(bl1).getFreeTGConnectingPoint(blockMap.get(bl1).getX(), blockMap.get(bl1).getY());
                        TGConnectingPoint p1 = blockMap.get(bl1).findFirstFreeTGConnectingPoint(true, true);
                        TGConnectingPoint p2 = blockMap.get(bl2).findFirstFreeTGConnectingPoint(true, true);
                        //	TGConnectingPoint p2=blockMap.get(bl2).getFreeTGConnectingPoint(blockMap.get(bl2).getX(),blockMap.get(bl2).getY());
                        AvatarBDPortConnector conn = new AvatarBDPortConnector(0, 0, 0, 0, 0, 0, true, null, abd, p1, p2, points);
                        conn.setAsynchronous(ar.isAsynchronous());
                        conn.setBlocking(ar.isBlocking());
                        conn.setPrivate(ar.isPrivate());
                        conn.setSizeOfFIFO(ar.getSizeOfFIFO());
                        TraceManager.addDev(bl1 + " " + ar.block1.getName() + " " + ar.block2.getName());
                        conn.addSignal("in " + ar.getSignal1(0).getName(), true, true);
                        conn.addSignal("out " + ar.getSignal2(0).getName(), false, false);
                        TraceManager.addDev("Added Signals");
                        conn.updateAllSignals();
                        p1.setFree(false);
                        p2.setFree(false);
                        abd.addComponent(conn, 0, 0, false, true);
                    }
                }
		/*for (ui.AvatarSignal sig:blockMap.get(bl1).getSignalList()){
		    for (ui.AvatarSignal sig2: blockMap.get(bl2).getSignalList()){
			if (sig.getId().equals(sig2.getId())){
			    conn.addSignal("in "+sig.getId(), true, true);
			    conn.addSignal("out "+sig.getId(), false, false);
			}
		    }
		}*/
            }
        }
        ypos += 100;
        //Add Pragmas
        AvatarBDPragma pragma = new AvatarBDPragma(xpos, ypos, xpos, xpos * 2, ypos, ypos * 2, false, null, abd);
        String[] arr = new String[avspec.getPragmas().size()];
        String s = "";
        int i = 0;
        for (AvatarPragma p : avspec.getPragmas()) {

//	    arr[i] = p.getName();
            String t = "";
            String[] split = p.getName().split(" ");
            if (p.getName().contains("#Confidentiality")) {
                for (String str : split) {
                    if (str.contains(".")) {
                        String tmp = str.split("\\.")[0];
                        String tmp2 = str.split("\\.")[1];
                        TraceManager.addDev("TMP " + tmp + " " + tmp2);
                        t = t.concat(tmp.split("__")[tmp.split("__").length - 1] + "." + tmp2.split("__")[Math.max(tmp2.split("__").length - 2, 0)] + " ");
                    } else {
                        t = t.concat(str + " ");
                    }
                }
            } else if (p.getName().contains("Authenticity")) {
            }
            s = s.concat(t + "\n");
            i++;
        }
        pragma.setValue(s);
        pragma.makeValue();
        abd.addComponent(pragma, xpos, ypos, false, true);

        TraceManager.addDev("val " + pragma.getValues().length);

    }


}

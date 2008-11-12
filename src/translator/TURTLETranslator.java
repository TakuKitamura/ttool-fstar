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
 * Class TURTLETranslator
 * Creation: 09/12/2003
 * @version 1.0 09/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

import ui.*;
import myutil.*;

public class TURTLETranslator {

	public final static int RT_LOTOS = 0;
	public final static int LOTOS = 1;

	private static int gateId;

	private TURTLEModeling tm;
	private Vector allProcesses;
	private Vector vectorProcess;
	private Vector vectorHLProcess;
	private int languageID;

	private Vector warning; /* Meant to contain String */

	private MasterGateManager mgm;

	public static final String HEAD = "specification essai : noexit\n\n";
	public static final String HEAD_FIRST_LOTOS = "specification essai ";
	public static final String HEAD_SECOND_LOTOS = ": exit\n\n";
	public static final String END = "\n\nendspec";

	public static final String BEHAVIOUR = "\nbehaviour\n\n";
	public static final String WHERE = "\n\nwhere\n\n";


	public static StringBuffer headerNat;
	public static StringBuffer headerBool;
	public static StringBuffer headerLOTOS;
	public static StringBuffer headerQueueNatLOTOS;

	//private static String[] operand = {"!", "?", "+", "-", "*", "min", "max", "<=", ">=", "==", "div", "mod", "divs", "=", " "};

	private Vector params, actions;

	private int index = 0;


	public TURTLETranslator(TURTLEModeling _tm) {
		tm = _tm;
		if (headerNat == null) {
			buildNatural();
		}
		if (headerBool == null) {
			buildBoolean();
		}
		if (headerLOTOS == null) {
			buildLOTOS(true);
		}

		if (headerQueueNatLOTOS == null) {
			buildheaderQueueNatLOTOS();
		}
	}

	/*public static int startsWithOperand(String s) {
		for(int i=0; i<operand.length; i++) {
			if (s.startsWith(operand[i])) {
				return operand[i].length();
			}
		}
		return -1;
	}

	public static int indexNextOperand(String s) {
		if (s.length() < 1) {
			return -1;
		}
		if (startsWithOperand(s) != -1) {
			return 0;
		} else {
			return 1 + startsWithOperand(s.substring(1));
		}
	}*/

	public Vector getWarnings() {
		return warning;
	}

	public String generateLOTOS(boolean xtendedNatural) {
		
		//System.out.println("Printing TM");
		//System.out.println("null? generate");
		//tm.print();
		//System.out.println("end null?");
		
		buildLOTOS(xtendedNatural);
		
		int i;

		languageID = LOTOS;
		warning = new Vector();

		MasterGateManager.reinitNameRestriction();

		tm.makeRTLOTOSName();
		tm.makeLOTOSName();
		//tm.renameParametersInActions();

		vectorProcess = new Vector();
		allProcesses = new Vector();
		//vectorHLProcess = new Vector();

		// Pre-processing: Advanced TURTLE -> BASIC TURTLE

		tm.translateWatchdogs();
		tm.translateInvocationIntoSynchronization();
		tm.translateActionStatesWithMultipleParams();
		
		//System.out.println("null? generate 1");
		//tm.print();
		//System.out.println("end null?");
		
		tm.removeUselessVariables();
		
		//System.out.println("null? generate 12");
		//tm.print();
		//System.out.println("end null?");
		
		//tm.removeUselessGates();
		
		//System.out.println("null? generate 2");
		//tm.print();
		//System.out.println("end null?");

		mgm = new MasterGateManager(tm, false);
		mgm.sort();
		//System.out.println(mgm.toString());

		// Activity Diagrams
		for(i=0; i<tm.classNb(); i++) {
			vectorProcess.addElement(translateActivityDiagram(tm.getTClassAtIndex(i)));
		}

		// make high level processes from class diagram
		vectorHLProcess = makeHighLevel();
		makeBodyHighLevel(vectorHLProcess);

		// Generate RT-LOTOS String
		StringBuffer translation = new StringBuffer();

		translation.append(HEAD_FIRST_LOTOS+ "[");
		if (mgm.nbVisibleMasterGate() > 0) {
			translation.append(mgm.allVisibleGatesToString());
		}

		translation.append("]" + HEAD_SECOND_LOTOS);
		translation.append(headerLOTOS);
		translation.append(headerQueueNatLOTOS);

		// spec dec
		translation.append(BEHAVIOUR);

		//System.out.println("translate main PSI");
		translation.append(translatePSI(tm, mgm, true));
		//System.out.println("main PSI translated");

		translation.append(WHERE);

		Process pr;
		Vector p;

		for(i=0; i<vectorHLProcess.size(); i++) {
			pr = (Process)(vectorHLProcess.elementAt(i));
			translation.append(pr.toString());
			translation.append("\n\n");
		}

		for(i=0; i<vectorProcess.size(); i++) {
			p = (Vector)(vectorProcess.elementAt(i));
			for(int j=0; j<p.size(); j++) {
				pr = (Process)(p.elementAt(j));
				translation.append(pr.toString());
				translation.append("\n\n");
			}
		}

		translation.append(END);
		
		//System.out.println("TRANSLATION=" + translation);

		return new String(translation);
	}

	public String generateRTLOTOS() {
		int i;

		languageID = RT_LOTOS;
		warning = new Vector();

		MasterGateManager.reinitNameRestriction();

		tm.makeRTLOTOSName();
		//tm.renameParametersInActions();

		vectorProcess = new Vector();
		allProcesses = new Vector();
		//vectorHLProcess = new Vector();

		// Pre-processing: Advanced TURTLE -> BASIC TURTLE

		tm.translateWatchdogs();
		tm.translateInvocationIntoSynchronization();
		tm.translateActionStatesWithMultipleParams();
		
		tm.removeUselessVariables();
		tm.removeUselessGates();

		mgm = new MasterGateManager(tm, false);
		mgm.sort();
		//System.out.println(mgm.toString());

		// Activity Diagrams
		for(i=0; i<tm.classNb(); i++) {
			vectorProcess.addElement(translateActivityDiagram(tm.getTClassAtIndex(i)));
		}

		// make high level processes from class diagram
		vectorHLProcess = makeHighLevel();
		makeBodyHighLevel(vectorHLProcess);

		// Generate RT-LOTOS String

		StringBuffer translation = new StringBuffer(HEAD);

		//beginning of RT-LOTOS spec : type declaration
		translation.append(headerNat);
		translation.append(headerBool);

		// spec dec
		translation.append(BEHAVIOUR);

		if (mgm.nbVisibleMasterGate() > 0) {
			translation.append("hide ");
			translation.append(mgm.allVisibleGatesToString());
			translation.append(" in\n\n");
		}

		//System.out.println("translate main PSI");
		translation.append(translatePSI(tm, mgm, true));
		//System.out.println("main PSI translated");

		translation.append(WHERE);

		//TClass t;
		Process pr;
		//Vector g1, pr1; // list of gates and of processes
		//Gate g;
		//Vector tmp;

		//g1 = new Vector();
		//pr1 = new Vector();
		Vector p;

		for(i=0; i<vectorHLProcess.size(); i++) {
			pr = (Process)(vectorHLProcess.elementAt(i));
			translation.append(pr.toString());
			translation.append("\n\n");
		}

		for(i=0; i<vectorProcess.size(); i++) {
			p = (Vector)(vectorProcess.elementAt(i));
			for(int j=0; j<p.size(); j++) {
				pr = (Process)(p.elementAt(j));
				translation.append(pr.toString());
				translation.append("\n\n");
			}
		}

		translation.append(END);

		return new String(translation);
	}

	private Vector makeHighLevel() {
		Vector v = new Vector();
		Vector gates;
		String s;
		TClass t;
		HLProcess p;
		Process pt;
		Gate g;
		int i, j;

		for(i=0; i<tm.classNb(); i++) {
			t = tm.getTClassAtIndex(i);
			s = generateProcessName("hl_" + t.getLotosName());
			// Gates
			// external : external gates of classes
			// param : param of tclasses.
			pt = t.getProcess();
			gates = (Vector)(pt.getGateList().clone());
			// remove internal gates
			for(j=0; j<gates.size(); j++) {
				g = (Gate)(gates.elementAt(j));
				if (g.isInternal()) {
					gates.removeElementAt(j);
					j --;
				}
			}
			p = new HLProcess(s, gates, pt.getParamList(), t, languageID);
			t.setHLProcess(p);
			v.add(p);
			allProcesses.add(p);

			// done later
			// internal  (= gates of launched processes)
			// param of processes launched: initial values ...no need of param
		}
		return v;

	}

	private void makeBodyHighLevel(Vector vhl) {
		TClass t;
		HLProcess p;

		for(int i=0; i<tm.classNb(); i++) {
			t = tm.getTClassAtIndex(i);
			p = (HLProcess)(vhl.elementAt(i));

			if (tm.hasPremptandSequence(t)) {
				makePremptAndSequence(t);
			} else if (tm.hasOnlyPreempt(t)) {
				makePrempt(t);
			} else if (tm.hasOnlySequence(t)) {
				makeSequence(t);
			} else {
				makeBasic(vhl, p);
			}
		}
	}

	private void makePremptAndSequence(TClass tc) {
		TURTLEModeling localtm1 = getSubProcessTURTLEModelingPre(tc, Relation.PRE);
		TURTLEModeling localtm2 = getSubProcessTURTLEModelingPre(tc, Relation.SEQ);
		MasterGateManager localmgm1 = new MasterGateManager(localtm1);
		MasterGateManager localmgm2 = new MasterGateManager(localtm2, localmgm1.getAllMasterGates());

		String  orPreemp = translatePreempt(localtm1, localmgm1);
		String psi = translatePSI(localtm2, localmgm2, false);

		makeHiddenGates(tc, localmgm1);
		makeHiddenGates(tc, localmgm2);

		String s = "(" + tc.getProcess().getCallToMe() + "[> (" + orPreemp;
		s = s + " >> exit)) >> " + psi;
		tc.getHLProcess().setBody(s);
	}

	private void makePrempt(TClass tc) {
		TURTLEModeling localtm = getSubProcessTURTLEModelingPre(tc, Relation.PRE);
		MasterGateManager localmgm = new MasterGateManager(localtm);
		//System.out.println("localmgm");
		localmgm.allGatesToString();
		String  orPreemp = translatePreempt(localtm, localmgm);
		makeHiddenGates(tc, localmgm);
		String s = tc.getProcess().getCallToMe() + "[>" + orPreemp;
		tc.getHLProcess().setBody(s);
	}

	private void makeSequence(TClass tc) {
		TURTLEModeling localtm = getSubProcessTURTLEModelingSeq(tc, Relation.SEQ);
		MasterGateManager localmgm = new MasterGateManager(localtm);
		String psi = translatePSI(localtm, localmgm, false);
		makeHiddenGates(tc, localmgm);
		String s = tc.getProcess().getCallToMe() + ">> " + psi;
		tc.getHLProcess().setBody(s);
	}

	private TURTLEModeling getSubProcessTURTLEModelingSeq(TClass tc, int relationType) {
		TURTLEModeling localtm = new TURTLEModeling();
		Relation r;
		int i;

		// we add all tclasses linked to tc by a seq relation
		for(i=0; i<tm.relationNb(); i++) {
			r = tm.getRelationAtIndex(i);
			if ((r.type == relationType) && (r.t1 == tc)) {
				localtm.addTClass(r.t2);
			}
		}

		// all relations of previously added Tclasses are added
		for(i=0; i<tm.relationNb(); i++) {
			r = tm.getRelationAtIndex(i);
			if ((localtm.belongsToMe(r.t1)) && (tm.belongsToMe(r.t2)) && (r.t1 != tc)) {
				localtm.addRelation(r);
			}
		}

		return localtm;
	}

	private TURTLEModeling getSubProcessTURTLEModelingPre(TClass tc, int relationType) {
		TURTLEModeling localtm = new TURTLEModeling();
		Relation r;
		int i;

		// we add all tclasses linked to tc by a relationType relation
		for(i=0; i<tm.relationNb(); i++) {
			r = tm.getRelationAtIndex(i);
			if ((r.type == relationType) && (r.t1 == tc)) {
				localtm.addTClass(r.t2);
			}
		}

		// no relation are added between tclasses

		return localtm;
	}


	private void makeHiddenGates(TClass tc, MasterGateManager localmgm) {
		Gate g1, g2;
		for(int i=0; i<localmgm.nbMasterGate(); i++) {
			g1 = localmgm.getMasterGateAtIndex(i);
			g2 = new Gate(g1.getLotosName(), Gate.GATE, true);
			g2.setLotosName(g1.getLotosName());
			tc.getHLProcess().addGate(g2);
		}
	}

	private void makeBasic(Vector v, HLProcess p) {
		p.setBody(p.getTClass().getProcess().getCallToMe());
	}

	private String translatePreempt(TURTLEModeling tumo, MasterGateManager magama) {
		StringBuffer sb = new StringBuffer("(");
		TClass t;
		for(int i=0; i<tumo.classNb(); i++) {
			t = tumo.getTClassAtIndex(i);
			if (i>0) {
				sb.append(" [] ");
			}
			sb.append(t.getHLProcess().getHighLevelCallToMe(magama, languageID));
		}
		sb.append(")");
		return new String(sb);
	}

	// translate PSI

	private String translatePSI(TURTLEModeling tumo, MasterGateManager magama, boolean active) {
		StringBuffer sb = new StringBuffer();

		recursiveTranslatePSI(0, sb, tumo, magama, active);

		return new String(sb);
	}

	private void recursiveTranslatePSI(int i, StringBuffer sb, TURTLEModeling tumo, MasterGateManager magama, boolean active) {
		//System.out.println("PSI " + i);
		TClass t =  tumo.getTClassAtIndex(i);
		
		if (t == null) {
			return;
		}

		if(active && !t.isActive()) {
			if (i<(tumo.classNb() - 1)) {
				recursiveTranslatePSI(i+1, sb, tumo, magama, active);
				//sb.append(")");
			}
			return;
		}

		Vector gates = new Vector();

		sb.append("(");
		sb.append(t.getHLProcess().getHighLevelCallToMe(magama, languageID));

		// look for relation with next ones
		TClass tmp;
		Relation r;
		Gate g;
		int k, l, index;
		Vector v;

		for(k=0; k<tumo.relationNb(); k++) {
			//System.out.println("Relation " + k);
			r = tumo.getRelationAtIndex(k);

			if ((r.type == r.SYN) && ((r.t1 == t) || (r.t2 == t))) {
				if (r.t1 == t) {
					tmp = r.t2;
				} else {
					tmp = r.t1;
				}

				// is tmp  next to t ?
				index = tumo.getIndexOf(tmp);

				// is tmp active
				if (active && !tmp.isActive()) {
					index = -1;
				}

				if (index > i) {
					//calculates master gates of the synchro relation
					v = r.gatesOfT1;
					for(l=0; l<v.size(); l++) {
						g = (Gate)(v.elementAt(l));
						//System.out.println("Looking for gate");
						gates.add(magama.getMasterGateOf(r.t1, g));
						//System.out.println("Got gate");
					}
				}

			}
		}

		// translate the relation of t with its next tclasses
		if (gates.size() == 0) {
			// if active equals true, must check if there is still active classes behind
			boolean b;
			if (!active) {
				b = true;
			} else {
				b = false;
				for(k=i+1; k<tumo.classNb(); k++) {
					tmp =  tumo.getTClassAtIndex(k);
					if (tmp.isActive()) {
						b = true;
					}
				}
			}

			if ((b) && (i < (tumo.classNb() - 1))) {
				sb.append(" ||| ");
			}
		} else {
			// translate gates
			sb.append(" |[");
			for(k=0; k<gates.size(); k++) {
				g = (Gate)(gates.elementAt(k));
				if (k != 0) {
					sb.append(", ");
				}
				sb.append(g.getLotosName());
			}
			sb.append("]| ");
		}


		if (i<(tumo.classNb() - 1)) {
			recursiveTranslatePSI(i+1, sb, tumo, magama, active);
		}
		sb.append(")");
	}



	private String generateProcessName(String s) {
		String name;
		int id = 0;

		if (!alreadyProcessName(s) && !forbiddenName(s)) {
			return s;
		}

		while(id >-1) {
			name = s + "_" + id;
			id ++;
			if (!alreadyProcessName(name) && !forbiddenName(s)) {
				return s;
			}
		}
		return "WRONG NAME";
	}

	private boolean forbiddenName(String s) {
		return RTLOTOSKeyword.isAKeyword(s);
	}

	private boolean alreadyProcessName(String s) {
		Process p;
		for(int i=0; i<allProcesses.size(); i++) {
			p = (Process)(allProcesses.elementAt(i));
			if (p.getName().equals(s)) {
				return true;
			}
		}
		return false;
	}



	/* note: the model MUST have been checked before*/
	private Vector translateActivityDiagram(TClass t) {
		//System.out.println("Translating activity diagram of " + t.getName());
		Vector v = new Vector();
		index = 0;
		int i, j;
		ActivityDiagram ad;
		Param p;
		LinkedList gfifos1=null, gfifos2=null, gfifos3=null;
		Gate gfifo1 = null, gfifo2 = null, gfifo3 = null;
		int nbPara = 0;
		Param nb = null, maxs = null;
		Param []params_nat = null;
		Param []params_fifo = null;
		Param param_fifo0 = null;
		Param [] params = null;
		String []exprs = null;


		Vector paramss = new Vector(t.getParamList());
		Vector gatess = new Vector(t.getGateList());

		if ((languageID == LOTOS) && (t instanceof FIFOTClass)) {
			// Attributes must be added
			gfifos1 = ((FIFOTClass)t).getGatesWrite();
			gfifos2 = ((FIFOTClass)t).getGatesRead();
			nbPara = ((FIFOTClass)t).getNbPara();
			params_nat = new Param[nbPara];
			params_fifo = new Param[nbPara];
			param_fifo0 = new Param("fifo_0", "Queue_nat", "nil");
			param_fifo0.setLotosName("fifo_0");
			paramss.add(param_fifo0);
			for(i=0; i<nbPara; i++) {
				params_fifo[i] = new Param("fifo_" + (i + 1), "Queue_nat", "nil");
				params_fifo[i].setLotosName("fifo_" + (i + 1));
				params_nat[i] = new Param("fifo_val_" + (i + 1), Param.NAT, "0");
				params_nat[i].setLotosName("fifo_val_" + (i + 1));
				//System.out.println("Adding param " + params_fifo[i]);
				paramss.add(params_fifo[i]);
				//System.out.println("Adding param " + params_nat[i]);
				paramss.add(params_nat[i]);
			}
		}

		if ((languageID == LOTOS) && (t instanceof FIFOInfiniteAndGetSizeTClass)) {
			// Attributes must be added
			paramss = new Vector();
			gatess = new Vector();
			gfifos1 = ((FIFOInfiniteAndGetSizeTClass)t).getGatesWrite();
			gfifos2 = ((FIFOInfiniteAndGetSizeTClass)t).getGatesRead();
			gfifos3 = ((FIFOInfiniteAndGetSizeTClass)t).getGatesSize();
			gatess.addAll(gfifos1);
			gatess.addAll(gfifos2);
			gatess.addAll(gfifos3);
			nbPara = ((FIFOInfiniteAndGetSizeTClass)t).getNbPara();
			params_nat = new Param[nbPara];
			params_fifo = new Param[nbPara];
			param_fifo0 = new Param("fifo_0", "Queue_nat", "nil");
			param_fifo0.setLotosName("fifo_0");
			paramss.add(param_fifo0);
			for(i=0; i<nbPara; i++) {
				params_fifo[i] = new Param("fifo_" + (i + 1), "Queue_nat", "nil");
				params_fifo[i].setLotosName("fifo_" + (i + 1));
				params_nat[i] = new Param("fifo_val_" + (i + 1), Param.NAT, "0");
				params_nat[i].setLotosName("fifo_val_" + (i + 1));
				//System.out.println("Adding param " + params_fifo[i]);
				paramss.add(params_fifo[i]);
				//System.out.println("Adding param " + params_nat[i]);
				paramss.add(params_nat[i]);
			}
		}

		if ((languageID == LOTOS) && (t instanceof FIFOFiniteAndGetSizeTClass)) {
			System.out.println("FIFO finite + " + t.getName());
			//t.removeAllGates();
			//t.removeAllAttributes();
			paramss = new Vector();
			gatess = new Vector();
			// Attributes must be added
			gfifos1 = ((FIFOFiniteAndGetSizeTClass)t).getGatesWrite();
			gfifos2 = ((FIFOFiniteAndGetSizeTClass)t).getGatesRead();
			gfifos3 = ((FIFOFiniteAndGetSizeTClass)t).getGatesSize();
			gatess.addAll(gfifos1);
			gatess.addAll(gfifos2);
			gatess.addAll(gfifos3);
			nbPara = ((FIFOFiniteAndGetSizeTClass)t).getNbPara();
			params_nat = new Param[nbPara];
			params_fifo = new Param[nbPara];
			param_fifo0 = new Param("fifo_0", "Queue_nat", "nil");
			param_fifo0.setLotosName("fifo_0");
			paramss.add(param_fifo0);
			for(i=0; i<nbPara; i++) {
				params_fifo[i] = new Param("fifo_" + (i + 1), "Queue_nat", "nil");
				params_fifo[i].setLotosName("fifo_" + (i + 1));
				params_nat[i] = new Param("fifo_val_" + (i + 1), Param.NAT, "0");
				params_nat[i].setLotosName("fifo_val_" + (i + 1));
				//System.out.println("Adding param " + params_fifo[i]);
				paramss.add(params_fifo[i]);
				//System.out.println("Adding param " + params_nat[i]);
				paramss.add(params_nat[i]);
			}
			nb = new Param("nb", "nat", "0");
			nb.setLotosName("nb");
			maxs = new Param("maxs", "nat", ""+((FIFOFiniteAndGetSizeTClass)t).getMaxSamples());
			maxs.setLotosName("maxs");
			paramss.add(nb);
			paramss.add(maxs);
		}



		Process main = new Process(generateProcessName("p_" + index + "_" + t.getLotosName()), gatess, paramss, languageID);
		//System.out.println("Parameters=" + main.listParameters());
		t.setProcess(main);
		v.add(main);
		allProcesses.add(main);
		index ++;

		if ((languageID == LOTOS) && (t instanceof FIFOTClass)) {
			// Translation of AD is calculated
			//main.setBody(...
			String bodyLotos="";
			ListIterator iterator = gfifos2.listIterator();
			// READ from FIFO

			while(iterator.hasNext()) {
				gfifo2 = (Gate)(iterator.next());
				bodyLotos += "[not (Empty (";
				bodyLotos += param_fifo0.getName() + "))] -> ";
				bodyLotos += gfifo2.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " !First(fifo_" + (i+1) + ")";
				}
				bodyLotos += ";";

				params = new Param[nbPara + 1];
				exprs = new String[nbPara + 1];
				params[0] = param_fifo0;
				exprs[0] = "Dequeue(" + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Dequeue(" + params_fifo[i].getName() + ")";
				}
				bodyLotos += main.getCallToMe(params, exprs);
				bodyLotos += "\n[]\n";
			}

			// WRITE to FIFO
			iterator = gfifos1.listIterator();

			while(iterator.hasNext()) {
				gfifo1 = (Gate)(iterator.next());
				bodyLotos += gfifo1.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " ?" + params_nat[i].getName() + ":" + params_nat[i].getType();
				}
				bodyLotos += ";";
				params = new Param[nbPara + 1];
				exprs = new String[nbPara + 1];
				params[0] = param_fifo0;
				exprs[0] = "Enqueue(0," + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Enqueue(" + params_nat[i].getName() + ", " + params_fifo[i].getName() + ")";
				}
				bodyLotos += main.getCallToMe(params, exprs);
				if (iterator.hasNext()) {
					bodyLotos += "\n[]\n";
				}
			}
			bodyLotos +="\n";

			//System.out.println("Body lotos=" + bodyLotos);

			main.setBody(bodyLotos);

			// Return process
			return v;
		}

		if ((languageID == LOTOS) && (t instanceof FIFOInfiniteAndGetSizeTClass)) {
			// Translation of AD is calculated
			//main.setBody(...
			String bodyLotos="";
			ListIterator iterator = gfifos2.listIterator();

			// READ from FIFO
			while(iterator.hasNext()) {
				gfifo2 = (Gate)(iterator.next());
				bodyLotos += "[not (Empty (";
				bodyLotos += param_fifo0.getName() + "))] -> ";
				bodyLotos += gfifo2.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " !First(fifo_" + (i+1) + ")";
				}
				bodyLotos += ";";

				params = new Param[nbPara + 1];
				exprs = new String[nbPara + 1];
				params[0] = param_fifo0;
				exprs[0] = "Dequeue(" + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Dequeue(" + params_fifo[i].getName() + ")";
				}
				bodyLotos += main.getCallToMe(params, exprs);
				bodyLotos += "\n[]\n";
			}

			// WRITE to FIFO
			iterator = gfifos1.listIterator();

			while(iterator.hasNext()) {
				gfifo1 = (Gate)(iterator.next());
				bodyLotos += gfifo1.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " ?" + params_nat[i].getName() + ":" + params_nat[i].getType();
				}
				bodyLotos += ";";
				params = new Param[nbPara + 1];
				exprs = new String[nbPara + 1];
				params[0] = param_fifo0;
				exprs[0] = "Enqueue(0," + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Enqueue(" + params_nat[i].getName() + ", " + params_fifo[i].getName() + ")";
				}
				bodyLotos += main.getCallToMe(params, exprs);
				if (iterator.hasNext()) {
					bodyLotos += "\n[]\n";
				}
			}


			// Empty or not?
			iterator = gfifos3.listIterator();
			if (iterator.hasNext()) {
				bodyLotos += "\n[]\n";
			}
			while(iterator.hasNext()) {
				gfifo3 = (Gate)(iterator.next());

				bodyLotos += "[not (Empty (";
				bodyLotos += param_fifo0.getName() + "))] -> ";
				bodyLotos += gfifo3.getName() + "!1;";
				bodyLotos += main.getCallToMe();
				bodyLotos += "\n[]\n";
				bodyLotos += "[Empty (";
				bodyLotos += param_fifo0.getName() + ")] -> ";
				bodyLotos += gfifo3.getName() + "!0;";
				bodyLotos += main.getCallToMe();
				if (iterator.hasNext()) {
					bodyLotos += "\n[]\n";
				}
			}

			bodyLotos +="\n";
			//System.out.println("Body lotos=" + bodyLotos);
			main.setBody(bodyLotos);

			// Return process
			return v;
		}

		if ((languageID == LOTOS) && (t instanceof FIFOFiniteAndGetSizeTClass)) {
			System.out.println("FIFO finite + " + t.getName());
			// Translation of AD is calculated
			//main.setBody(...
			String bodyLotos="";
			ListIterator iterator = gfifos2.listIterator();

			// READ from FIFO
			while(iterator.hasNext()) {
				gfifo2 = (Gate)(iterator.next());
				bodyLotos += "[not (Empty (";
				bodyLotos += param_fifo0.getName() + "))] -> ";
				bodyLotos += gfifo2.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " !First(fifo_" + (i+1) + ")";
				}
				bodyLotos += ";";

				params = new Param[nbPara + 3];
				exprs = new String[nbPara + 3];
				params[0] = param_fifo0;
				exprs[0] = "Dequeue(" + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Dequeue(" + params_fifo[i].getName() + ")";
				}
				params[i+1] = nb;
				exprs[i+1] = "nb-1";
				params[i+2] = maxs;
				exprs[i+2] = "maxs";
				bodyLotos += main.getCallToMe(params, exprs);
				bodyLotos += "\n[]\n";
			}

			// WRITE to FIFO
			iterator = gfifos1.listIterator();

			while(iterator.hasNext()) {
				gfifo1 = (Gate)(iterator.next());

				bodyLotos += "[nb<maxs] -> ";
				bodyLotos += gfifo1.getName();
				for(i=0; i<nbPara; i++) {
					bodyLotos += " ?" + params_nat[i].getName() + ":" + params_nat[i].getType();
				}
				bodyLotos += ";";
				params = new Param[nbPara + 3];
				exprs = new String[nbPara + 3];
				params[0] = param_fifo0;
				exprs[0] = "Enqueue(0," + param_fifo0.getName() + ")";
				for(i=0; i<nbPara; i++) {
					params[i+1] = params_fifo[i];
					exprs[i+1] = "Enqueue(" + params_nat[i].getName() + ", " + params_fifo[i].getName() + ")";
				}
				params[i+1] = nb;
				exprs[i+1] = "nb+1";
				params[i+2] = maxs;
				exprs[i+2] = "maxs";
				bodyLotos += main.getCallToMe(params, exprs);

				if (!((FIFOFiniteAndGetSizeTClass)t).isBlocking()) {
					bodyLotos += "\n[]\n";

					bodyLotos += "[nb == maxs] -> ";
					bodyLotos += gfifo1.getName();
					for(i=0; i<nbPara; i++) {
						bodyLotos += " ?" + params_nat[i].getName() + ":" + params_nat[i].getType();
					}
					bodyLotos += ";";
					params = new Param[nbPara + 3];
					exprs = new String[nbPara + 3];
					params[0] = param_fifo0;
					exprs[0] = "Enqueue(0, Dequeue(" + param_fifo0.getName() + "))";
					for(i=0; i<nbPara; i++) {
						params[i+1] = params_fifo[i];
						exprs[i+1] = "Enqueue(" + params_nat[i].getName() + ", Dequeue(" + params_fifo[i].getName() + "))";
					}
					params[i+1] = nb;
					exprs[i+1] = "nb";
					params[i+2] = maxs;
					exprs[i+2] = "maxs";
					bodyLotos += main.getCallToMe(params, exprs);
					/*bodyLotos += "\n[]\n";
              bodyLotos += main.getCallToMe(params, exprs);*/
					if (iterator.hasNext()) {
						bodyLotos += "\n[]\n";
					}
				}
			}



			// Empty or not?
			iterator = gfifos3.listIterator();
			if (iterator.hasNext()) {
				bodyLotos += "\n[]\n";
			}
			while(iterator.hasNext()) {
				gfifo3 = (Gate)(iterator.next());

				bodyLotos += "[not (Empty (";
				bodyLotos += param_fifo0.getName() + "))] -> ";
				bodyLotos += gfifo3.getName() + "!1;";
				bodyLotos += main.getCallToMe();
				bodyLotos += "\n[]\n";
				bodyLotos += "[Empty (";
				bodyLotos += param_fifo0.getName() + ")] -> ";
				bodyLotos += gfifo3.getName() + "!0;";
				bodyLotos += main.getCallToMe();
				if (iterator.hasNext()) {
					bodyLotos += "\n[]\n";
				}
			}

			bodyLotos +="\n";
			//System.out.println("Body lotos=" + bodyLotos);
			main.setBody(bodyLotos);

			// Return process
			return v;
		}


		// settle all processes
		// parallel processes
		// search of for parallel operator
		ad = t.getActivityDiagram();

		// Basic verification
		if ((ad == null) || (ad.getStartState() == null) || (ad.getStartState().getNext(0) == null)) {
			return v;
		}

		ad.setParallelMulti();
		int nbParallel = ad.getNbMultiParallel();
		if (nbParallel == 0) {
			// no parallel process
			makeJunctionProcess(v, ad, t, t.getGateList(), t.getParamList());
			makeActionStateWithParamProcess(v, ad, t, t.getGateList(), t.getParamList());
			translateJunctionProcess(ad);
			translateActionStateWithParamProcess(ad);
			main.setBody(translateActivity(ad.getStartState().getNext(0), ad.getStartState()));
			return v;
		} else {
			// At least one parallel component
			//System.out.println("Translation with Parallel");

			int cpt;
			ADParallel adp;
			ADComponent adc1, adc2;
			Gate g1, g2;

			Vector paraList = new Vector(); // list of parallel components in the ad diagram
			Vector listMainProcess = (Vector)(t.getGateList().clone()); // Gates of the main process
			Vector listOtherProcess = (Vector)(t.getGateList().clone()); // Gates of subprocesses
			Vector listAllInternalGates = new Vector(); //List of gates for synchro relation
			Vector processPar1List = new Vector(); // List of processes "Parm"
			Vector processPar2List = new Vector(); // List of processes "Parc"
			Process par1, par2;

			// List Other Processes
			// Gates which are hidden are tranform into public gates
			for(i=0; i<t.getGateList().size(); i++) {
				g1 = (Gate)(t.getGateList().elementAt(i));
				if (g1.isInternal()) {
					g2 = new Gate(g1.getLotosName(), g1.getType(), false);
					g2.setLotosName(g1.getLotosName());
					listOtherProcess.add(g2);
					listOtherProcess.remove(g1);
				}
			}

			//For each parallel, say which component leads to it and creates a gate for each of it
			for(i=0; i<ad.size(); i++) {
				adc1 = (ADComponent)(ad.elementAt(i));
				if (adc1 instanceof ADParallel) {
					adp = (ADParallel)adc1;
					paraList.add(adp);
					g1 = new Gate("par" + i +"_spe", Gate.GATE, false);
					g1.setLotosName(g1.getName());
					adp.setSpecialGate(g1);
					listAllInternalGates.add(g1);
					g2 = new Gate("par" + i +"_spe", Gate.GATE, true);
					g2.setLotosName(g2.getName());
					listMainProcess.add(g2);

					// search for all components leading to this parallel
					cpt = 0;
					for(j=0; j<ad.size(); j++) {
						adc2 = (ADComponent)(ad.elementAt(j));
						if (adc2.hasNextTo(adp)) {
							//System.out.println("Leading to par: " +adc2.toString());
							//creates a new Gate for it
							g1 = new Gate("par" + i +"_g" + cpt, Gate.GATE, false);
							g2 = new Gate("par" + i +"_g" + cpt, Gate.GATE, true);
							cpt ++;
							g1.setLotosName(g1.getName());
							g2.setLotosName(g2.getName());
							listMainProcess.add(g2);
							listOtherProcess.add(g1);
							listAllInternalGates.add(g1);
							adp.addCoupleGateComponent(g1, adc2);
						}
					}
				}
			}

			// make AD subprocesses
			//makeJunctionProcess(v, ad, t, t.getGateList(), t.getParamList());
			//makeActionStateWithParamProcess(v, ad, t, t.getGateList(), t.getParamList());
			makeJunctionProcess(v, ad, t, listOtherProcess, t.getParamList());
			makeActionStateWithParamProcess(v, ad, t, listOtherProcess, t.getParamList());
			translateJunctionProcess(ad);
			translateActionStateWithParamProcess(ad);

			// make parallel processes
			Vector proc1Gate, proc2Gate;
			Process p1, p2;
			for(i=0; i<paraList.size(); i++) {
				adp = (ADParallel)(paraList.elementAt(i));
				// 2 proceses to make
				// Higher process
				proc1Gate = (Vector)(adp.getNewAllGateList());
				proc1Gate.add(adp.getSpecialGate());
				p1 = new Process(generateProcessName("parm_" + index + "_" + t.getLotosName()), proc1Gate, null, languageID);
				processPar1List.add(p1);
				makeProcessParallelManager(p1);
				v.add(p1);
				allProcesses.add(p1);

				// Lower process
				proc2Gate = (Vector)(listOtherProcess.clone());
				proc2Gate.add(adp.getSpecialGate());
				p2 = new Process(generateProcessName("parc_" + index + "_" + t.getLotosName()), proc2Gate, t.getParamList(), languageID);
				index ++;
				processPar2List.add(p2);
				makeProcessParallelChild(p2, adp);
				v.add(p2);
				allProcesses.add(p2);
			}

			// make main body
			String s = "(\n";

			for(i=0; i<processPar1List.size(); i++) {
				par1 = (Process)(processPar1List.elementAt(i));
				if (i == 0) {
					s = s + par1.getCallToMe();
				} else {
					s = s + "\n|||\n" + par1.getCallToMe();
				}
			}

			s = s + "\n)\n|[";

			for(i=0; i<listAllInternalGates.size(); i++) {
				g1 = (Gate)(listAllInternalGates.elementAt(i));
				if (i ==0) {
					s = s + g1.getLotosName();
				}else {
					s = s + ", " + g1.getLotosName();
				}
			}

			s = s + "]|\n(\n";

			for(i=0; i<processPar2List.size(); i++) {
				par2 = (Process)(processPar2List.elementAt(i));
				s = s + par2.getCallToMe() + "\n|||\n";
			}


			s = s + "(" + translateActivity(ad.getStartState().getNext(0), ad.getStartState()) + ")\n)";
			main.setBody(s);
			main.setGateList(listMainProcess);
		}

		return v;
	}

	private String translateActivity(ADComponent ad, ADComponent previous) {

		String s = "";
		if (ad instanceof ADActionStateWithGate) {
			return translateADActionStateWithGate((ADActionStateWithGate)ad);
		}

		if (ad instanceof ADActionStateWithParam) {
			return translateADActionStateWithParam((ADActionStateWithParam)ad);
		}

		if (ad instanceof ADChoice) {
			return translateADChoice((ADChoice)ad);
		}

		if (ad instanceof ADDelay) {
			return translateADDelay((ADDelay)ad);
		}

		if (ad instanceof ADJunction) {
			return translateADJunction((ADJunction)ad);
		}

		if (ad instanceof ADLatency) {
			return translateADLatency((ADLatency)ad);
		}

		if (ad instanceof ADParallel) {
			return translateADParallel((ADParallel)ad, previous);
		}

		if (ad instanceof ADSequence) {
			return translateADSequence((ADSequence)ad);
		}

		if (ad instanceof ADPreempt) {
			return translateADPreempt((ADPreempt)ad);
		}

		if (ad instanceof ADStop) {
			return translateADStop((ADStop)ad);
		}

		if (ad instanceof ADTimeInterval) {
			return translateADTimeInterval((ADTimeInterval)ad);
		}

		if (ad instanceof ADTLO) {
			return translateADTLO((ADTLO)ad);
		}

		return s;
	}

	private String translateADActionState(ADActionState ad) {
		return modifyAction(ad.getActionValue(), languageID) + "; " + translateActivity(ad.getNext(0), ad);
	}

	private String translateADActionStateWithGate(ADActionStateWithGate ad) {
		String s = ad.getLimitOnGate();
		if ((s == null) || (s.length() == 0)){
			return ad.getGate().getLotosName() + modifyAction(ad.getActionValue(), languageID) + "; " + translateActivity(ad.getNext(0), ad);
		} else {
			//System.out.println("Limit on Gate: " + s);
			if (languageID == LOTOS) {
				CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time limited action with value " + ad.getActionValue() +  " and time limit of " + s + " has been modified with no time limitation");
				warning.add(error);
				return ad.getGate().getLotosName() + modifyAction(ad.getActionValue(), languageID) + "; " + translateActivity(ad.getNext(0), ad);
			}
			return ad.getGate().getLotosName() + s + modifyAction(ad.getActionValue(), languageID) + "; " + translateActivity(ad.getNext(0), ad);
		}
	}

	private String translateADActionStateWithParam(ADActionStateWithParam ad) {
		int i;
		String s;
		
		Process p = ad.getProcess();
		//System.out.println("ActionValue=*" + ad.getActionValue() + "*");
		if (p != null) {
			if ((params == null) || (actions == null)) {
				s = p.getCallToMe(ad.getParam(), modifyAction(ad.getActionValue(), languageID));
			} else {
				// Added May 2008
				Param[] paramss = new Param[params.size() + 1];
				String[] actionss = new String[actions.size() + 1];
				for (i=0; i<params.size(); i++) {
					paramss[i] = (Param)(params.get(i));
					actionss[i] = (String)(actions.get(i));
				}
				paramss[i] = ad.getParam();
				actionss[i] = modifyAction(ad.getActionValue(), languageID);
				s = p.getCallToMe(paramss, actionss);
				params = null;
				actions = null;
				// End added May 2008
			}
			/*if (p.getName().indexOf("67") > -1)
                System.out.println(p.getName() + " -> " + s);
            if (s == null) {
               System.out.println("*** NULL S (PROCESS) ***");
            }
            if (s.startsWith("null")) {
                System.out.println("*** NULL S1 (PROCESS) ***");
            }*/
			return s;
		} else {
			// added May 2008
			if (ad.getNext(0) instanceof ADActionStateWithParam) {
				if (params == null) {
					params = new Vector();
					actions = new Vector();
				}
				params.add(ad.getParam());
				actions.add(modifyAction(ad.getActionValue(), languageID));
				return translateADActionStateWithParam((ADActionStateWithParam)(ad.getNext(0)));
			// end added May 2008
			} else {
				System.out.println("*** NULL PROCESS ***");
				return "";
			}
		}
	}

	private String translateADChoice(ADChoice ad) {
		
		if (ad.realNbOfNext() < 1)
			return "";

		//System.out.println("GUARD=" + ad.getGuard(0));
		if (ad.realNbOfNext() == 1) {
			if (Conversion.replaceAllString(ad.getGuard(0), " ", "").compareTo("[]") == 0) {
				translateActivity(ad.getNext(0), ad);
			} else {
				String s = "(\n";
				s = s + modifyAction(ad.getGuard(0), languageID) + " -> (" + translateActivity(ad.getNext(0), ad) + ")\n";
				return s + ")\n";
			}
		}

		// more than one option
		String s = "(\n";
		for(int i=0; i<ad.realNbOfNext(); i++) {
			if (i != 0) {
				s = s + "[]\n";
			}
			if (ad.isGuarded(i)) {
				s = s + modifyAction(ad.getGuard(i), languageID) + " -> (" + translateActivity(ad.getNext(i), ad) + ")\n";
			} else {
				s = s + "(" + translateActivity(ad.getNext(i), ad) + ")\n";
			}
		}
		return s + ")\n";
	}

	private String translateADDelay(ADDelay ad) {
		if (languageID == LOTOS) {
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Determinitic delay with value " + ad.getValue() + " has been removed");
			warning.add(error);
			return translateActivity(ad.getNext(0), ad);
		}
		return "delay(" + ad.getValue() + ") " + translateActivity(ad.getNext(0), ad);
	}

	private String translateADJunction(ADJunction ad) {
		Process p = ad.getProcess();
		if (p != null) {
			return p.getCallToMe();
		}
		return "";
	}

	private String translateADLatency(ADLatency ad) {
		if (languageID == LOTOS) {
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Non determinitic delay with value " + ad.getValue() + " has been removed");
			warning.add(error);
			return translateActivity(ad.getNext(0), ad);
		}
		return "latency(" + ad.getValue() + ") " + translateActivity(ad.getNext(0), ad);
	}

	private String translateADParallel(ADParallel ad, ADComponent previous) {
		if (ad.isMulti()) {
			if (languageID == LOTOS) {
				return ad.getAction(previous) + " exit";
			}
			return ad.getAction(previous) + " exit";
		} else {
			StringBuffer sb = new StringBuffer();
			String motif;
			ADComponent adc;

			/*sb.append(" >> ");*/

			// motif management
			motif = ad.getLotosValueGate();
			if ((motif == null) || (motif.equals("[]")) || (motif.equals("[ ]"))) {
				motif = "|||";
			} else {
				motif = "|" + motif + "|";
			}

			if (ad.realNbOfNext() > 0) {
				sb.append("(");
			}
			for(int i=0; i<ad.realNbOfNext(); i++) {
				adc = ad.getNext(i);
				if (adc != null) {
					if (i != 0) {
						sb.append(motif);
					}
					sb.append("(");
					sb.append(translateActivity(adc, ad));
					sb.append(")");
				}
			}
			if (ad.realNbOfNext() > 0) {
				sb.append(")");
			}
			return new String(sb);
		}
	}

	private String translateADSequence(ADSequence ad) {
		if (ad.realNbOfNext() < 1)
			return "";

		if (ad.realNbOfNext() == 1) {
			return translateActivity(ad.getNext(0), ad);
		}

		// more than one option
		String s = "(\n";
		for(int i=0; i<ad.realNbOfNext(); i++) {
			if (i != 0) {
				s = s + ">>\n";
			}
			s = s + "(" + translateActivity(ad.getNext(i), ad) + ")\n";
		}
		return s + ")\n";
	}

	private String translateADPreempt(ADPreempt ad) {
		if (ad.realNbOfNext() < 1)
			return "";

		if (ad.realNbOfNext() == 1) {
			return translateActivity(ad.getNext(0), ad);
		}

		// more than one option
		String s = "(\n";
		for(int i=0; i<ad.realNbOfNext(); i++) {
			if (i != 0) {
				s = s + "[>\n";
			}
			s = s + "(" + translateActivity(ad.getNext(i), ad) + ")\n";
		}
		return s + ")\n";
	}

	private String translateADStop(ADStop ad) {
		if (languageID == LOTOS) {
			return "exit";
		}
		return "exit";
	}

	private String translateADTimeInterval(ADTimeInterval ad) {
		if (languageID == LOTOS) {
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time interval with value (" + ad.getMinValue() + ", " + ad.getMaxValue()+ ") has been removed");
			warning.add(error);
			return translateActivity(ad.getNext(0), ad);
		}
		return "delay(" + ad.getMinValue() + ", " + ad.getMaxValue() + ") " + translateActivity(ad.getNext(0), ad);
	}

	private String translateADTLO(ADTLO ad) {
		String s = "(\n(";
		if (languageID == LOTOS) {
			CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time limited offer on gate " + ad.getGate().getLotosName() + " with delay " + ad.getDelay() + " has been modified using a choice operator");
			warning.add(error);
			s = s + ad.getGate().getLotosName() + ad.getAction() + "; " + translateActivity(ad.getNext(0), ad) + ")\n[]\n(";
			s = s + translateActivity(ad.getNext(1), ad) + ")\n)";
			return s;
		}
		String l = ad.getLatency();
		if ((l != null) && (l.length() > 0) && (!l.equals("0"))) {
			s = s + "latency(" + l + ") ";
		}
		s = s + ad.getGate().getLotosName() + "{" + ad.getDelay() + "}" + ad.getAction() + "; " + translateActivity(ad.getNext(0), ad) + ")\n[]\n(";
		//s = s + ad.getAction() + "{" + ad.getDelay() + "}; " + translateActivity(ad.getNext(0), ad) + ")\n[]\n(";
		s = s + "delay(" + ad.getDelay() + ") " + translateActivity(ad.getNext(1), ad) + ")\n)";
		return s;
	}

	private void makeJunctionProcess(Vector v, ActivityDiagram ad, TClass t, Vector gateList, Vector paramList) {
		ADJunction adj;
		ADComponent adc;
		Process p;

		// creating processes
		for(int i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.elementAt(i));
			if (adc instanceof ADJunction) {
				adj = (ADJunction)adc;
				p = new Process("p_" + index + "_" + t.getLotosName(), gateList, paramList, languageID);
				//System.out.println("New process junction: " + p.getName());
				v.add(p);
				index ++;
				adj.setProcess(p);
			}
		}
	}

	private void makeActionStateWithParamProcess(Vector v, ActivityDiagram ad, TClass t, Vector gateList, Vector paramList) {
		ADActionStateWithParam ads, adsnext;
		ADComponent adc;
		Process p;
		Gate g1, g2;
		Param par;

		int i;
		Vector myGates = new Vector();
		boolean makeProcess;

		// Gates which are hidden are tranform into public gates
		for(i=0; i<gateList.size(); i++) {
			g1 = (Gate)(gateList.elementAt(i));
			if (g1.isInternal()) {
				g2 = new Gate(g1.getLotosName(), g1.getType(), false);
				g2.setLotosName(g1.getLotosName());
				myGates.add(g2);
			} else {
				myGates.add(g1);
			}
		}

		// Creating processes
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.elementAt(i));
			if (adc instanceof ADActionStateWithParam) {
				makeProcess = true;
				ads = (ADActionStateWithParam)adc;
				if (adc.getNext(0) instanceof ADActionStateWithParam) {
					adsnext = (ADActionStateWithParam)(adc.getNext(0));
					par = ads.getParam();
					if (adsnext.getParam() != par) {
						// One must verify that the second does not use the Param declared in the first one
						if (!paramIsUsedIn(par, adsnext.getActionValue())) {
								makeProcess = false;
						}
					}
				}
				if(makeProcess) {
					p = new Process("p_" + index + "_" + t.getLotosName(), myGates, paramList, languageID);
					//System.out.print("New process action state: " + p.getName() + "param=" + ads.getParam().getName() + " action=" + ads.getActionValue());
					//System.out.println("  Next: " + ads.getNext(0));
					v.add(p);
					index ++;
					ads.setProcess(p);
				}
			}
		}
	}

	private void translateJunctionProcess(ActivityDiagram ad) {
		// translating processes
		ADJunction adj;
		ADComponent adc;
		Process p;
		for(int i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.elementAt(i));
			if (adc instanceof ADJunction) {
				adj = (ADJunction)adc;
				p = adj.getProcess();

				if (p != null) {
					p.setBody(translateActivity(adj.getNext(0), adj));
				}
			}
		}
	}

	private void translateActionStateWithParamProcess(ActivityDiagram ad) {
		// translating processes
		ADActionStateWithParam ads;
		ADComponent adc;
		Process p;
		for(int i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.elementAt(i));
			if (adc instanceof ADActionStateWithParam) {
				ads = (ADActionStateWithParam)adc;
				p = ads.getProcess();
				//System.out.println("Translating " + p.getName());
				if (p != null) {

					String s = translateActivity(ads.getNext(0), ads);
					//System.out.println("Result = " + s);
					/*if (s == null) {
                        System.out.println("s is null!");
                    }
                    if (s.trim().compareTo("null") == 0) {
                         System.out.println("s is equal to null!");
                    }*/
					p.setBody(s);
				} else {
					//System.out.println("p is null!");
				}
			}
		}
	}

	private void makeProcessParallelManager(Process p) {
		String s = "";
		Vector l = p.getGateList();
		Gate g;
		for(int i=0; i<l.size(); i++) {
			g = (Gate)(l.elementAt(i));
			s = s + g.getLotosName() + "; ";
		}
		s = s + p.getCallToMe();
		p.setBody(s);
	}

	private void makeProcessParallelChild(Process p, ADParallel adp) {
		StringBuffer sb = new StringBuffer();
		String motif;
		ADComponent adc;

		sb.append(adp.getSpecialGate().getLotosName());
		sb.append("; ((");

		// motif management
		motif = adp.getLotosValueGate();
		if ((motif == null) || (motif.equals("[]")) || (motif.equals("[ ]"))) {
			motif = "|||";
		} else {
			motif = "|" + motif + "|";
		}

		for(int i=0; i<adp.realNbOfNext(); i++) {
			adc = adp.getNext(i);
			if (adc != null) {
				if (i != 0) {
					sb.append(motif);
				}
				sb.append("(");
				sb.append(translateActivity(adc, adp));
				sb.append(")");
			}
		}

		sb.append(")|||");
		sb.append(p.getCallToMe());
		sb.append(")");
		p.setBody(new String(sb));
	}

	public void buildNatural() {
		headerNat = new StringBuffer();
		headerNat.append("type natural is boolean\n\tsorts nat\n");
		headerNat.append("\topns\n\t\t+\t:nat,nat->nat\n");
		headerNat.append("\t\t-\t:nat,nat->nat\n\t\t*\t:nat,nat->nat\n");
		headerNat.append("\t\tmin\t:nat,nat->nat\n\t\tmax\t:nat,nat->nat\n");
		headerNat.append("\t\t<\t:nat,nat->bool\n\t\t>\t:nat,nat->bool\n");
		headerNat.append("\t\t<=\t:nat,nat->bool\n\t\t>=\t:nat,nat->bool\n");
		headerNat.append("\t\t==\t:nat,nat->bool\n\t\tdiv\t:nat,nat->nat\n");
		headerNat.append("\t\tmod\t:nat,nat->nat\n\t\tdivs\t:nat,nat->nat\n");
		headerNat.append("endtype\n\n");
	}

	public void buildBoolean() {
		headerBool = new StringBuffer();
		headerBool.append("type natural is \n\tsorts bool\n");
		headerBool.append("\topns\n\t\tnot\t:bool->bool\n");
		headerBool.append("\t\tand\t:bool,bool->bool\n\t\tor\t:bool,bool->bool\n");
		headerBool.append("endtype\n\n");
	}

	public void buildheaderQueueNatLOTOS() {
		headerQueueNatLOTOS = new StringBuffer();
		headerQueueNatLOTOS.append("type Queue_natural  is Boolean, Natural\n\tsorts Queue_nat\n");
		headerQueueNatLOTOS.append("\topns\n\t\tNil (*! implementedby NIL constructor *)\t: -> Queue_nat\n");
		headerQueueNatLOTOS.append("\t\tEnqueue (*! implementedby ENQUEUE constructor *)\t: Nat, Queue_nat -> Queue_nat\n");
		headerQueueNatLOTOS.append("\t\tDequeue (*! implementedby DEQUEUE *)\t: Queue_nat -> Queue_nat\n");
		headerQueueNatLOTOS.append("\t\tEmpty (*! implementedby EMPTY *)\t: Queue_nat -> Bool\n");
		headerQueueNatLOTOS.append("\t\tFirst (*! implementedby FIRST *)\t: Queue_nat -> Nat\n");
		headerQueueNatLOTOS.append("\t\tLast (*! implementedby LAST *)\t: Queue_nat -> Nat\n");
		headerQueueNatLOTOS.append("\t\tDestroy (*! implementedby DESTROY *)\t: Nat, Queue_nat -> Queue_nat\n");
		headerQueueNatLOTOS.append("\teqns\n");
		headerQueueNatLOTOS.append("\tforall E, E1, E2:Nat, Q:Queue_nat\n");
		headerQueueNatLOTOS.append("\t\tofsort Queue_nat\n\t\t\tDequeue (Enqueue (E, Nil)) = Nil;\n\t\t\tnot (Empty (Q)) => Dequeue (Enqueue (E, Q)) = Enqueue (E, Dequeue (Q))\n");
		headerQueueNatLOTOS.append("\t\tofsort Bool\n\t\t\tEmpty (Nil) = true;\n\t\t\tEmpty (Enqueue (E, Q)) = false;\n");
		headerQueueNatLOTOS.append("\t\tofsort Nat\n\t\t\tFirst (Enqueue (E, Nil)) = E;\n\t\t\tnot (Empty (Q)) => First (Enqueue (E, Q)) = First (Q);\n");
		headerQueueNatLOTOS.append("\t\tofsort Nat\n\t\t\tLast (Enqueue (E, Q)) = E;\n");
		headerQueueNatLOTOS.append("\t\tofsort Queue_nat\n\t\t\tDestroy (E1, Nil) = Nil;\n\t\t\tE1 eq E2 => Destroy (E1, Enqueue (E2, Q)) = Destroy (E1, Q);\n\t\t\tE1 ne E2 => Destroy (E1, Enqueue (E2, Q)) = Enqueue (E2, Destroy (E1, Q));\n");
		headerQueueNatLOTOS.append("endtype\n\n");
	}

	public void buildLOTOS(boolean xtendedNatural) {
		headerLOTOS = new StringBuffer();
		if (xtendedNatural) {
			headerLOTOS.append("Library BOOLEAN, X_NATURAL endlib\n\n");
		} else {
			headerLOTOS.append("Library BOOLEAN, NATURAL endlib\n\n");
		}
	}

	public static String modifyAction(String s, int languageID) {
		String ret;
		if (languageID == LOTOS) {
			ret = modifyNatural(s, languageID);
			
			ret = Conversion.replaceAllChar(ret, '%', " mod ");
			ret = Conversion.replaceAllChar(ret, '/', " div ");
			//ret = Conversion.changeBinaryOperatorWithUnary(ret, "div", "DIV");
			//ret = Conversion.changeBinaryOperatorWithUnary(ret, "mod", "MOD");
			
			// Must remove time limited offer information i.e. {..}
			int index0 = ret.indexOf('{');
			int index1 = ret.indexOf('}');
			if ((index0 > -1) && (index1 > index0)) {
				ret = ret.substring(0, index0) + ret.substring(index1+1, ret.length());
			}
			
			return ret;
		}
		
		if (languageID == RT_LOTOS) {
			ret = Conversion.replaceAllChar(s, '%', " mod ");
			ret = Conversion.replaceAllChar(ret, '/', " div ");
			return ret;
		}
		

		return s;
	}

	public static String modifyNatural(String s, int languageID) {
		boolean identifier = false;
		boolean foundNatural = false;
		int index0 = 0, index1, index2;
		String tmp0, tmp1, tmp2, tmp3, tmp4, tmp5;
		int i, j;


		while(index0 < s.length()) {
			tmp0 = s.substring(index0, index0+1);
			if ((tmp0.matches("\\d")) && (identifier == false)) {
				if (foundNatural == false) {
					foundNatural = true;
				} else {
					// Must replace natural!
					// Searches for the whole natural
					index1 = index0;
					while((index1 < s.length()) && (s.substring(index1, index1+1).matches("\\d"))) {
						index1 ++;
					}

					tmp1 = s.substring(index0-1, index1);
					//System.out.println("tmp1=" + tmp1);

					tmp2 = "";
					for(i=0; i<tmp1.length(); i++) {
						tmp3 = tmp1.substring(i, i+1);
						if (tmp3.compareTo("0") != 0) {
							tmp4 = "";
							for(j=i; j<tmp1.length()-1; j++) {
								tmp4 = tmp4 + "*(9+1)";
							}
							tmp4 = "(" + tmp3 + tmp4 + ")";
							if (i != 0) {
								tmp4 = "+" + tmp4;
							}
						} else {
							tmp4 = "";
						}
						tmp2 = tmp2 + tmp4;
					}
					if (tmp2.length() > 0) {
						tmp2 = "(" + tmp2 + ")";
					}
					//System.out.println("tmp2=" + tmp2);

					if (index0 == 1) {
						tmp5 = "";
					} else {
						tmp5 = s.substring(0, index0-1);
					}
					tmp5 = tmp5 + tmp2;
					if (index1 < s.length()) {
						tmp5 = tmp5 + s.substring(index1, s.length());
					}

					//System.out.println("tmp5=" + tmp5);

					// Then, recursive call
					return modifyNatural(tmp5, languageID);
					//return "9";
				}
			} else {
				foundNatural = false;
				if (tmp0.matches("\\w")) {
					identifier = true;
				} else {
					identifier = false;
				}
			}
			index0 ++;
		}
		return s;
	}
	
	public boolean paramIsUsedIn(Param p, String s) {
		if (p == null) {
			return false;
		}
		if (s  == null) {
			return false;
		}
		
		if (p.getName() == null) {
			return false;
		}
		
		s = Conversion.replaceAllChar(s, '!', "");
		s = Conversion.replaceAllChar(s, '?', "");
		s = Conversion.replaceAllChar(s, ' ', "");
		s = Conversion.replaceAllChar(s, '+', " ");
		s = Conversion.replaceAllChar(s, '-', " ");
		s = Conversion.replaceAllChar(s, '*', " ");
		s = Conversion.replaceAllChar(s, '/', " ");
		s = Conversion.replaceAllChar(s, '[', " ");
		s = Conversion.replaceAllChar(s, ']', " ");
		s = Conversion.replaceAllChar(s, '(', " ");
		s = Conversion.replaceAllChar(s, ')', " ");
		s = Conversion.replaceAllChar(s, ':', " ");
		s = Conversion.replaceAllChar(s, '=', " ");
		s = Conversion.replaceAllString(s, "==", " ");
		s = s + " ";
		return (s.indexOf(p.getName()) > -1);
	}

}

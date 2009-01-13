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
* Class TURTLE2UPPAAL
* Creation: 15/11/2006
* @version 1.1 10/05/2007
* @author Ludovic APVRILLE
* @see
*/

package translator.touppaal;

import java.awt.*;
import java.util.*;

import uppaaldesc.*;
import myutil.*;
import translator.*;

public class TURTLE2UPPAAL {
    
    private UPPAALSpec spec;
    private TURTLEModeling tm;
	private boolean isRegular;
	private boolean isRegularTClass;
	private boolean choicesDeterministic = false;
	private RelationTIFUPPAAL table;
	
    private Vector warnings;
    private LinkedList tmpComponents;
    private LinkedList tmpLocations;
	private ArrayList<UPPAALTemplate> templatesWithMultipleProcesses;
    private LinkedList locations;
    private LinkedList gates;
    private LinkedList relations; // null: not synchronize, Relation : synchronized
    private LinkedList parallels;
    
    private LinkedList gatesNotSynchronized; // String
	private ArrayList<Gate> gatesWithInternalSynchro;
    private int maxSentInt; // Max nb of int put on non synchronized gates
    private int maxSentBool;
    private LinkedList gatesSynchronized;
    private int idChoice;
    private int idTemplate;
	private int idPar;
	private int idParProcess;
	private ArrayList<ADParallel> paras;
	private ArrayList<Integer> parasint;
	//private int idTemplate;
	private boolean multiprocess;
	
    private int currentX, currentY;
    
    public final static int STEP_X = 0;
    public final static int STEP_Y = 120;
    public final static int STEP_LOOP_X = 150;
    public final static int NAME_X = 10;
    public final static int NAME_Y = 5;
    public final static int SYNCHRO_X = 5;
    public final static int SYNCHRO_Y = -10;
    public final static int ASSIGN_X = 10;
    public final static int ASSIGN_Y = 0;
    public final static int GUARD_X = 0;
    public final static int GUARD_Y = -20;
	
	public final static String SYNCID = "__sync__";
	public final static String GSYNCID = "__gsync__";
	
    private UPPAALTemplate templateNotSynchronized;
    
    public TURTLE2UPPAAL(TURTLEModeling _tm) {
        tm = _tm;
    }
	
	
    public void saveInFile(String path) throws FileException {
        FileUtils.saveFile(path + "spec.xml", spec.makeSpec());
        //System.out.println("spec.xml generated:\n" + spec.getFullSpec());
    }
	
	
    public Vector getWarnings() {
        return warnings;
    }
	
	public RelationTIFUPPAAL getRelationTIFUPPAAL () {
		return table;
	}
	
	public boolean isRegular() {
		return isRegular;
	}
	
	public void setChoiceDeterministic(boolean _b) {
		choicesDeterministic = _b;
		System.out.println("choices are assumed to be deterministic:" + choicesDeterministic);
	}
    
    public UPPAALSpec generateUPPAAL(boolean _debug, int _nb) {
        warnings = new Vector();
        spec = new UPPAALSpec();
		table = new RelationTIFUPPAAL();
		
        tmpComponents = new LinkedList();
        tmpLocations = new LinkedList();
        locations = new LinkedList();
        gatesNotSynchronized = new LinkedList();
		gatesWithInternalSynchro = new ArrayList<Gate>();
        gatesSynchronized = new LinkedList();
        parallels = new LinkedList();
		templatesWithMultipleProcesses = new ArrayList<UPPAALTemplate>();
        idChoice = 0;
        idTemplate = 0;
        
        int nb = _nb;
		multiprocess = false;
        
        UPPAALLocation.reinitID();
		
        // Name initialization -> we reuse the names used by LOTOS specification
        MasterGateManager.reinitNameRestriction();
        tm.makeRTLOTOSName();
        tm.makeLOTOSName();
		
		
        
        // Work with tm modeling
        // For example, compact latencies together, etc.
		tm.mergeChoices(true);
		tm.translateWatchdogs();
		tm.translateInvocationIntoSynchronization();
		tm.translateActionStatesWithMultipleParams();
		
		// Analyze the tm spcification
		isRegular = tm.isARegularTIFSpec(choicesDeterministic);
		idPar = 0;
		System.out.println("Regular spec:" + isRegular);
        
        // Deal with tclasses
        translateTClasses();
        
        // Gate with and without synchronization
        makeNotSynchronized();
        makeSynchronized();
        makeBoth();
		
		int effectiveNb;
		if (multiprocess) {
			effectiveNb = nb;
		} else {
			effectiveNb = 1;
		}
        // Generate system
        makeGlobal(effectiveNb);
        //makeParallel(nb);
        makeSystem(effectiveNb);
		
		spec.enhanceGraphics();
		
		//System.out.println("relations:" + table.toString());
		
        return spec;
    }
    
    public void initXY() {
		currentX = 0; currentY = -220;
    }
	
    public void makeGlobal(int nb) {
		String s;
		if (!isRegular) {
			spec.addGlobalDeclaration("\n// Global management\nint locid__ = 0;\nint taskid__ = 0;\nint startingid__ = 0;\nint preemptid__ = 0;\nint groupid__ = 0;\n");
			spec.addGlobalDeclaration("urgent chan gotask__, begintask__, endtask__, lockmutextask__, unlockmutextask__, lockmutexgroup__, unlockmutexgroup__;\n");
			spec.addGlobalDeclaration("const int totalTasks__ = " + nb + ";\n");
			spec.addGlobalDeclaration("const int preempt__ = -2;\n");
			spec.addGlobalDeclaration("const int maxGroups__ = " + (nb*tm.classNb()) + ";\n");
			spec.addGlobalDeclaration("const int maxTasks__ = " + (nb*tm.classNb()) + ";\n");
			spec.addGlobalDeclaration("int gotasks__ = 0;\n");
			spec.addGlobalDeclaration("int synchro__ = 1;\n");
			spec.addGlobalDeclaration("int tasks__[maxTasks__][maxTasks__];\n");
			spec.addGlobalDeclaration("//clock clocks__[maxTasks__];\n");
			spec.addGlobalDeclaration("//clock globalClock;\n");
			spec.addGlobalDeclaration("int groups__[maxGroups__];\n");
			spec.addGlobalDeclaration("urgent broadcast chan endGroup__;\n");
			spec.addGlobalDeclaration("urgent broadcast chan goAllTasks__;\n");
		
			// Has to manage
			s = "\nint hasToManage(int tab[maxTasks__]) {\nint i;\nfor(i=0; i<maxTasks__; i++) {\n";
			s += "if (tab[i] != -1) {\nreturn 1;\n}\n}\nreturn 0;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// First free
			s = "\nint firstFree(int id) {\nint i;\nint taskid = (id / totalTasks__)*totalTasks__;\n";
				s += "for(i=taskid; i<(taskid + totalTasks__); i++) {\nif (tasks__[i][i] == 0) {\n";
			s +="return i;\n}\n}\nreturn -1;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// Preempt task
			s = "int preempt(int id) {\nint i, j;\nint taskid;\nint ids[maxTasks__];\nint tmpids[maxTasks__];\n";
				s += "int cpt = 0;\nint currentid;\n\nfor(i=1; i<maxTasks__; i++) {\nids[i] = -1;\n}\nids[0] = id;\n\n";
				s += "while(hasToManage(ids) == 1) {\ncpt = 0;\nfor(i=0; i<maxTasks__; i++) {\ntmpids[i] = ids[i];\nids[i] = -1;\n";
					s += "}\nfor(j=0; j<maxTasks__; j++) {\nif (tmpids[j] != -1) {\ntaskid = (tmpids[j] / totalTasks__)*totalTasks__;\n";
						s += "currentid = tmpids[j];\nfor(i=taskid; i<(taskid + totalTasks__); i++) {\nif ((i!= id) && (tasks__[currentid][i] == preempt__)) {\n";
							s += "tasks__[currentid][i] = 0;\ntasks__[i][i] = -3;\nids[cpt] = i;\ncpt ++;\n}else if (tasks__[currentid][i] == preempt__) {\n";		
			s+= "tasks__[currentid][i] = 0;\n}\n}\n}\n}\n}\nreturn -1;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// End task
			s = "int endTask(int id) {\nint i, j;\nint taskid;\nint ids[maxTasks__];\nint tmpids[maxTasks__];\n";
			s += "int cpt = 0;\nint currentid;\n\nif (tasks__[id][id] == -3) {\n// I have been preempted\n";
			s += "tasks__[id][id] = 0;\nreturn -1;\n} else {\n// Normal termination\npreempt(id);\n}\n";
			s += "\nfor(i=0; i<maxTasks__; i++) {\nids[i] = -1;\n}\n";
			s += "ids[0] = id;\n\nwhile(hasToManage(ids) == 1) {\ncpt = 0;\nfor(i=0; i<maxTasks__; i++) {\n";
			s += "tmpids[i] = ids[i];\nids[i] = -1;\n}\nfor(j=0; j<maxTasks__; j++) {\nif (tmpids[j] != -1) {\n";
			s += "taskid = (tmpids[j] / totalTasks__)*totalTasks__;\ncurrentid = tmpids[j];\ntasks__[currentid][currentid] = 0;\n";
			s += "for(i=taskid; i<(taskid + totalTasks__); i++) {\nif ((i != id) && (tasks__[i][currentid] == preempt__)) {\ntasks__[i][currentid] = 0;\n";
			s += "ids[cpt] = i;\ncpt ++;\n}\n}\n}\n}\n}\nreturn -1;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// Can run
			s = "int canRun(int id) {\nreturn (tasks__[id][id] == 1);\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// Start task
			s = "void startTask(int myid) {\nint i;\nfor(i=0; i<maxTasks__; i++) {\ntasks__[myid][i] = 0;\n}\ntasks__[myid][myid] = 1;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// Make parallel
			s = "void makeParallel(int myid, int startingid) {\nint i;\n\nif (myid == startingid) {\nreturn;\n}\n\n";
				s += "for(i=0; i<maxTasks__; i++){\nif ((tasks__[startingid][i] == preempt__) && (i != startingid)){\ntasks__[myid][i] = preempt__;\n";
				s += "}\n}\n\nfor(i=0; i<maxTasks__; i++){\nif ((tasks__[i][startingid] == preempt__) && (i != myid)) {\n";
			s += "tasks__[i][myid] = preempt__;\n}\n}\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// Make preempt operator
			s = "void makePreempt(int myid, int preemptid, int startingid) {\ntasks__[myid][preemptid] = preempt__;\n";
			s += "//makeParallel(myid, startingid);\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			// First group id...
			s = "int firstGroupId() {\nint i;\nfor(i=0; i<maxGroups__; i++){\nif (groups__[i] == 0) {\n";
			s += "groups__[i] = 1;\nreturn i;\n}\n}\nreturn 0;\n}\n\n";
			spec.addGlobalDeclaration(Conversion.indentString(s, 2));
			
			makeTaskManager(nb);
			
		} else {
			ListIterator iterator = spec.getTemplates().listIterator();
			UPPAALTemplate template1;
			TClass t;
			
			while(iterator.hasNext()) {
				template1 = (UPPAALTemplate)(iterator.next());
				t = tm.getTClassWithName(template1.getName());
				if (t!= null) {
					spec.addGlobalDeclaration(makeGlobalParamDeclaration(t));
				}
			}
		}
		
		s = "\nint min(int x, int y) {\nif(x<y) {\nreturn x;\n}\nreturn y;\n}\n\n";
		s += "int max(int x, int y) {\nif(x<y) {\nreturn y;\n}\nreturn x;\n}\n";
		spec.addGlobalDeclaration(Conversion.indentString(s, 2));
    }
    
    public void makeTaskManager(int nb) {
		TClass t;
		UPPAALLocation loc1, loc2, loc3;
		UPPAALTransition tr;
		
		initXY();
		
		UPPAALTemplate template = new UPPAALTemplate();
		UPPAALTemplate template1;
		spec.addTemplate(template);
		template.setName("TaskManager");
		loc1 = addLocation(template);
		//loc1.setUrgent();
		loc1.setCommitted();
		template.setInitLocation(loc1);
		
		int cpt = 0;
		
		ListIterator iterator = spec.getTemplates().listIterator();
		
		while(iterator.hasNext()) {
			template1 = (UPPAALTemplate)(iterator.next());
			t = tm.getTClassWithName(template1.getName());
			if (t!= null) {
				spec.addGlobalDeclaration(makeGlobalParamDeclaration(t));
				
				loc2 = addLocation(template);
				//loc2.setUrgent();
				loc2.setCommitted();
				tr = addTransition(template, loc1, loc2);
				setAssignment(tr, "locid__ =" + template1.getInitLocation().int_id + ",\ntaskid__ =" + cpt + ",\ngroupid__ = firstGroupId()");
				loc3 = addLocation(template);
				//loc3.setUrgent();
				loc3.setCommitted();
				tr = addTransition(template, loc2, loc3);
				loc1 = loc3;
				setSynchronization(tr, "begintask__!");
			}
			cpt += nb;
		}
		
		//loc1.unsetUrgent();
		loc1.unsetCommitted();
		loc2 = addLocation(template);
		//loc2.setCommitted();
		tr = addTransition(template, loc1, loc2);
		setSynchronization(tr, "lockmutextask__?");
		setAssignment(tr, "synchro__ = 0");
		tr = addTransition(template, loc2, loc1);
		setSynchronization(tr, "unlockmutextask__?");
		setAssignment(tr, "synchro__ = 1");
		
		loc3 = addLocation(template);
		//loc3.setCommitted();
		tr = addTransition(template, loc1, loc3);
		setSynchronization(tr, "lockmutexgroup__?");
		setAssignment(tr, "synchro__ = 0");
		tr = addTransition(template, loc3, loc1);
		setSynchronization(tr, "unlockmutexgroup__?");
		setAssignment(tr, "synchro__ = 1");
		
		loc1 = addLocation(template);
		loc1.setUrgent();
		tr = addTransition(template, loc2, loc1);
		setSynchronization(tr, "begintask__?");
		setGuard(tr, "locid__ == -1");
    }
    
    /*public void makeParallel() {
		if (parallels.size() == 0) {
			return;
		}
		
		ListIterator iterator = parallels.listIterator();
		TClass t;
		UPPAALLocation loc1, loc2;
		UPPAALTransition tr;
		
		initXY();
		
		UPPAALTemplate template = new UPPAALTemplate();
		spec.addTemplate(template);
		template.setName("ParallelManager");
		spec.addGlobalDeclaration("\n// Parallel operators\nint paralocid = 0;\nchan begintask, endtask;\n");
		loc1 = addLocation(template);
		template.setInitLocation(loc1);
		
		while(iterator.hasNext()) {
			t = (TClass)(iterator.next());
			spec.addGlobalDeclaration("int para__" + t.getName() + " = 0;\n");
			spec.addGlobalDeclaration("chan gopara__" + t.getName() + ";\n");
			spec.addGlobalDeclaration(makeGlobalParamDeclaration(t));
			loc2 = addLocation(template);
			tr = addTransition(template, loc1, loc2);
			loc1 = loc2;
			setSynchronization(tr, "gopara__" + t.getName() + "!");
			
		}
		
		loc2 = addLocation(template);
		tr = addTransition(template, loc1, loc2);
		setSynchronization(tr, "begintask?");
		tr = addTransition(template, loc2, loc1);
		setSynchronization(tr, "endtask?");
		
    }*/
    
    public void addNotSync(String s) {
		ListIterator iterator = gatesNotSynchronized.listIterator();
		String action;
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			if (action.compareTo(s) ==0) {
				return;
			}
		}
		gatesNotSynchronized.add(s);
    }
	
    public void makeNotSynchronized() {
		if (gatesNotSynchronized.size() == 0) {
			return;
		}
		
		initXY();
		
		templateNotSynchronized = new UPPAALTemplate();
		templateNotSynchronized.setName("Actions__not__synchronized");
		spec.addTemplate(templateNotSynchronized);
		UPPAALLocation loc = addLocation(templateNotSynchronized);
		templateNotSynchronized.setInitLocation(loc);
		UPPAALTransition tr;
		
		spec.addGlobalDeclaration("\n//Declarations used for non synchronized gates\n");
		
		String action;
		ListIterator iterator = gatesNotSynchronized.listIterator();
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			tr = addTransition(templateNotSynchronized, loc, loc);
			setSynchronization(tr, action+"?");
			//addGuard(tr, action + TURTLE2UPPAAL.SYNCID + " == 0");
			spec.addGlobalDeclaration("urgent chan " + action + ";\n");
			//spec.addGlobalDeclaration("int " + action + TURTLE2UPPAAL.SYNCID + " = 0;\n");
		}
    }
    
    public void makeSynchronized() {
		if (gatesSynchronized.size() == 0) {
			return;
		}
		
		spec.addGlobalDeclaration("\n//Declarations used for synchronized gates\n");
		
		String action;
		ListIterator iterator = gatesSynchronized.listIterator();
		while(iterator.hasNext()) {
			action = (String)(iterator.next());
			spec.addGlobalDeclaration("urgent chan " + action + ";\n");
		}
    }
    
    public void makeBoth() {
		int i;
		spec.addGlobalDeclaration("\n//Declarations used for (non) synchronized gates\n");
		
		for(i=0; i<maxSentInt; i++) {
			spec.addGlobalDeclaration("int action_int__" + i + ";\n");
		}
		
		for(i=0; i<maxSentBool; i++) {
			spec.addGlobalDeclaration("int action_bool__" + i + ";\n");
		}
    }
	
    public void translateTClasses() {
		TClass t;
        for(int i=0; i<tm.classNb(); i++) {
			t = tm.getTClassAtIndex(i);
			if (t.isActive()) {
				translateTClass(t);
			}
        }
    }
    
    public void translateTClass(TClass t) {
		isRegularTClass = tm.isRegularTClass(t.getActivityDiagram(), choicesDeterministic);
		tmpComponents = new LinkedList();
		tmpLocations = new LinkedList();
		locations = new LinkedList();
		
		UPPAALTemplate template = newTClassTemplate(t, 0);
		
		// Gate analysis
		makeGates(t, template);
		
		// Attributes
		makeAttributes(t, template);
		
		// Behaviour
		if (isRegularTClass) {
			idTemplate = 0;
		}
		makeBehaviour(t, template, null);
    }
	
	public UPPAALTemplate newTClassTemplate(TClass t, int id) {
		UPPAALTemplate template = new UPPAALTemplate();
		if (id != 0) {
			template.setName(t.getName() + "__" + id);
		} else {
			template.setName(t.getName());
		}
		spec.addTemplate(template);
		table.addTClassTemplate(t, template, id);
		return template;
	}
    
	
    public void makeAttributes(TClass t, UPPAALTemplate template) {
		Param p;
		Vector params = t.getParamList();
		for(int i=0; i<params.size(); i++) {
			p = (Param)(params.get(i));
			if (p.getType() == Param.NAT) {
				template.addDeclaration("int ");
			} else {
				template.addDeclaration("bool ");
			}
			template.addDeclaration(p.getName() + " = " + p.getValue() + ";\n");
		}
		
		template.addDeclaration("int waitgroupid__;\n");
		template.addDeclaration("clock h__;\n");
		
		for(Gate g:gatesWithInternalSynchro) {
			template.addDeclaration("int " + t.getName() + "__" + g.getName() + TURTLE2UPPAAL.SYNCID + " = 0;\n");
		}
    }
    
    public void makeAttributeChoice(TClass t, int id1, int id2) {
		Param p;
		Vector params = t.getParamList();
		for(int i=0; i<params.size(); i++) {
			p = (Param)(params.get(i));
			if (p.getType() == Param.NAT) {
				spec.addGlobalDeclaration("int ");
			} else {
				spec.addGlobalDeclaration("bool ");
			}
			spec.addGlobalDeclaration(t.getName() + "__" + p.getName() + "__" + id1 + "__" + id2 + " = " + p.getValue() + ";\n");
		}
    }
    
	public void makeGates(TClass t, UPPAALTemplate template) {
		// Classify gates: basic synchro, complex synchro, and not synchronized
		
		/*Gate g;
		Vector tgates = t.getGateList();
        for(int i=0; i<tgates.size(); i++) {
			g = (Gate)(tgates.get(i));
			classifyGate(t, template ,g);
        }*/
		
		fillInternalSynchros(t);
	}
	
	public void classifyGate(TClass t, UPPAALTemplate template, Gate g) {
        Relation r = tm.syncRelationWith(t, g);
        //gates.add(g);
        //relations.add(r);
        if (r == null) {
			// Assume it is a "basic" synchro
			// Look for internal synchros 
			//fillInternalSynchros(t.getActivityDiagram());
        } else {
			// No synchro
			
        }
	}
	
	private void fillInternalSynchros(TClass t) {
		ActivityDiagram ad = t.getActivityDiagram();
		ADComponent adc;
		ADParallel adp;
		Gate g;
		int i,j;
		
		spec.addGlobalDeclaration("// Internal synchronizations of class " + t.getName() + "\n");
		
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADParallel) {
				adp = (ADParallel)adc;
				if (adp.nbGate() > 0) {
					for(j=0; j<adp.nbGate(); j++) {
						g = adp.getGate(j);
						gatesWithInternalSynchro.add(g);
						spec.addGlobalDeclaration("urgent chan " + t.getName() + "__" + g.getName() + TURTLE2UPPAAL.GSYNCID + ";\n");
					}
				}
			}
		}
		spec.addGlobalDeclaration("\n");
	}
	
	public boolean isSynchronized(TClass t, Gate g) {
		return (tm.syncRelationWith(t, g) != null);
	}
	
	
	
	public void makeBehaviour(TClass t, UPPAALTemplate template, ADComponent adc) {
		// Request is not yet taken into account
		//System.out.println("Making special choices for " + t.getName());
		//t.getActivityDiagram().makeSpecialChoices();
		//System.out.println("Done special choices");
		
		initXY();
		paras = new ArrayList<ADParallel>();
		parasint = new ArrayList<Integer>();
		UPPAALLocation loc = makeTaskInit(t, template);
		if (adc == null) {
			ADComponent start = t.getActivityDiagram().getStartState();
			makeElementBehavior(t, template, start, loc, null, null);
		} else {
			makeElementBehavior(t, template, adc, loc, null, null);
		}
    }
	
    public void makeElementBehavior(TClass t, UPPAALTemplate template, ADComponent elt, UPPAALLocation previous, UPPAALLocation end, String guard) {
		UPPAALLocation loc, loc1, loc2, loc3, loc4, loc5;
		UPPAALTransition tr, tr1, tr2, tr3;
		int i, index;
		int preemptid;
		ADActionStateWithParam adap;
		ADActionStateWithGate adag;
		ADTLO adtlo;
		ADChoice adch;
		ADParallel adp;
		String action;
		Gate g;
		LinkedList ll;
		boolean stopJunc;
		String name;
		
		//System.out.println("Making behavior of " + t.getName() + " elt = " + elt);
		
		if (elt == null) {
			return;
		}
		
		// Start state
		if (elt instanceof ADStart) {
			if (previous == null) {
				loc = addLocation(template);
				template.setInitLocation(loc);
				table.addADComponentLocation(elt, loc, loc);
			} else {
				loc = previous;
			}
			makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			return;
			
			
		} else if (elt instanceof ADStop) {
			table.addADComponentLocation(elt, previous, previous);
			if (end == null) {
				//tr = addEndTransition(template, previous);
				makeEnd(template, previous);
				return;
			}
			tr = addRTransition(template, previous, end);
			return;
			
			// Junction
		} else if (elt instanceof ADJunction) {
			stopJunc = false;
			if ((index = tmpComponents.indexOf(elt)) != -1) {
				ll = (LinkedList)(tmpLocations.get(index));
				if (ll.indexOf(end) != -1) {
					loc = (UPPAALLocation)(locations.get(index));
					tr = addRTransition(template, previous, loc);
					previous.setCommitted();
					table.addADComponentLocation(elt, previous, loc);
				} else {
					ll.add(end);
					loc = addRLocation(template);
					previous.setCommitted();
					locations.add(loc);
					tr = addRTransition(template, previous, loc);
					currentX += STEP_LOOP_X;
					table.addADComponentLocation(elt, previous, loc);
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
				}
			} else {
				tmpComponents.add(elt);
				ll = new LinkedList();
				ll.add(end);
				tmpLocations.add(ll);
				loc = addRLocation(template);
				locations.add(loc);
				previous.setCommitted();
				tr = addRTransition(template, previous, loc);
				currentX += STEP_LOOP_X;
				table.addADComponentLocation(elt, previous, loc);
				makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			}
			
			return;
			
			// ADActionStateWithGate
		} else if (elt instanceof ADActionStateWithGate) {
			adag = (ADActionStateWithGate)(elt);
			action = adag.getActionValue();
			g = adag.getGate();
			if (isSynchronized(t, g)) {
				String [] s = manageSynchro(t, g, action);
				loc = addRLocation(template);
				tr = addActionTransition(template, previous, loc);
				if (!isRegular) {
					addGuard(tr, "synchro__ == 1");
				}
				if (guard != null) {
					addGuard(tr, guard);
				}
				setSynchronization(tr, s[0]);
				addAssignment(tr, s[1]);
				table.addADComponentLocation(elt, previous, loc);
				makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			} else {
				name = t.getName() + "__" + g.getName();
				if (gatesWithInternalSynchro.indexOf(g) != -1) {
					// Gate with possible internal synchro
					addNotSync(name);
					loc = addRLocation(template);
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 0");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + "!");
					buildAssignment(tr, action);
					
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 1");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + TURTLE2UPPAAL.GSYNCID + "!");
					buildAssignment(tr, action);
					
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 2");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + TURTLE2UPPAAL.GSYNCID + "?");
					buildAssignment(tr, action);
					
					table.addADComponentLocation(elt, previous, loc);
				
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
					
				} else {
					//loc1 = addUrgentRLocation(template);
					//tr1 = addRTransition(template, previous, loc1);
					//setAssignment(tr1, "clocks__[myid__] = globalClock");
					//addGuard(tr1, "clocks__[myid__] == globalClock");
					addNotSync(name);
					loc = addRLocation(template);
					tr = addActionTransition(template, previous, loc);
					//addGuard(tr, "clocks__[myid__] == globalClock");
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + "!");
					buildAssignment(tr, action);
					
					table.addADComponentLocation(elt, previous, loc);
					
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
				}
			}
			return;
			
			// Time limited offer ->   assumes no latency
		} else if (elt instanceof ADTLO) {
			// init of h
			adtlo = (ADTLO)(elt);
			
			loc1 = addLocation(template);
			loc1.setInvariant("h__<="+adtlo.getDelay());
			tr1 = addTransition(template, previous, loc1);
			setAssignment(tr1, "h__ = 0");
			previous.setUrgent();
			
			action = adtlo.getAction();
			g = adtlo.getGate();
			
			if (isSynchronized(t, g)) {
				String [] s = manageSynchro(t, g, action);
				loc = addLocation(template);
				tr = addTransition(template, loc1, loc);
				setSynchronization(tr, s[0]);
				addAssignment(tr, s[1]);
				//setGuard(tr, "h__<=" + adtlo.getDelay());
				if (!isRegular) {
					addGuard(tr, "synchro__ == 1");
				}
				if (guard != null) {
					addGuard(tr, guard);
				}
				
				table.addADComponentLocation(elt, previous, loc);
				
				makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
				
				currentX += STEP_LOOP_X;
				loc2 = addLocation(template);
				tr2 = addTransition(template, loc1, loc2);
				setGuard(tr2, "h__==" + adtlo.getDelay());
				//setSynchronization(tr2, "timeLimitedOfferExpired!");
				//addNotSync("timeLimitedOfferExpired");
				

				
				makeElementBehavior(t, template, elt.getNext(1), loc2, end, null);
				currentX -= STEP_LOOP_X;
				
			} else {
				name = t.getName() + "__" + g.getName();
				if (gatesWithInternalSynchro.indexOf(g) != -1) {
					// Gate with possible internal synchro
					addNotSync(name);
					loc = addRLocation(template);
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 0");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + "!");
					buildAssignment(tr, action);
					
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 1");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + TURTLE2UPPAAL.GSYNCID + "!");
					buildAssignment(tr, action);
					
					tr = addActionTransition(template, previous, loc);
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					addGuard(tr, name + TURTLE2UPPAAL.SYNCID + " == 2");
					if (guard != null) {
						addGuard(tr, guard);
					}
					setSynchronization(tr, name + TURTLE2UPPAAL.GSYNCID + "?");
					buildAssignment(tr, action);
					
					table.addADComponentLocation(elt, previous, loc);
					
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
					
					currentX += STEP_LOOP_X;
					loc2 = addLocation(template);
					tr2 = addTransition(template, loc1, loc2);
					setGuard(tr2, "h__==" + adtlo.getDelay());
					//setSynchronization(tr2, "timeLimitedOfferExpired!");
					//addNotSync("timeLimitedOfferExpired");
					
					
					
					makeElementBehavior(t, template, elt.getNext(1), loc2, end, null);
					currentX -= STEP_LOOP_X;
					
				} else {
					addNotSync(t.getName() + "__" + g.getName());
					loc = addLocation(template);
					tr = addTransition(template, previous, loc);
					setSynchronization(tr, t.getName() + "__" + g.getName() + "!");
					//setGuard(tr, "(h__<=" + adtlo.getDelay() + ")");
					if (!isRegular) {
						addGuard(tr, "synchro__ == 1");
					}
					buildAssignment(tr, action);
					
					table.addADComponentLocation(elt, previous, loc);
					
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
					
					currentX += STEP_LOOP_X;
					loc2 = addLocation(template);
					tr2 = addTransition(template, loc1, loc2);
					setGuard(tr2, "h__==" + adtlo.getDelay());
					//setSynchronization(tr2, "timeLimitedOfferExpired!");
					//addNotSync("timeLimitedOfferExpired");
					makeElementBehavior(t, template, elt.getNext(1), loc2, end, null);
					currentX -= STEP_LOOP_X;
				}
			}
			return;
			
			// ADActionStateWithParam
		} else if (elt instanceof ADActionStateWithParam) {
			adap = (ADActionStateWithParam)(elt);
			loc = addRLocation(template);
			previous.setCommitted();
			tr = addRTransition(template, previous, loc);
			if (adap.getParam() == null) {
				System.out.println("Unknown param");
				name = "unknown";
			} else {
				name = adap.getParam().getName();
			}
			setAssignment(tr, name + "=" + adap.getActionValue());
			
			table.addADComponentLocation(elt, previous, loc);
			
			makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			return;
			
			// Choice
		} else if (elt instanceof ADChoice) {
			//System.out.println("ADChoice");
			adch = (ADChoice)elt;
			
			if (adch.getNbGuard() == 1) {
				if (adch.isGuarded(0)){
					loc = addLocation(template);
					tr = addTransition(template, previous, loc);
					action = convertGuard(adch.getGuard(0));
					setGuard(tr, action);
					
					table.addADComponentLocation(elt, previous, loc);
					
					makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
				} else {
					table.addADComponentLocation(elt, previous, previous);
					makeElementBehavior(t, template, elt.getNext(0), previous, end, null);
				}
			} else {
				//System.out.println("ADChoice: testing");
				if (choicesDeterministic) {
					//System.out.println("Choice is deterministic");
					int tmpX = currentX;
					for(i=0; i<elt.getNbNext(); i++){
						//System.out.println("Choice is deterministic i=" + i);
						String gua = null;
						if (adch.isGuarded(i)) {
							gua = convertGuard(adch.getGuard(i));
						}
						
						if (gua == null) {
							gua = "";
						}
						
						loc = addLocation(template);
						tr = addTransition(template, previous, loc);
						setGuard(tr, gua);
					
						table.addADComponentLocation(elt, previous, loc);
						
						makeElementBehavior(t, template, elt.getNext(i), loc, end, null);
						currentX += 2 * STEP_LOOP_X;
					}
				} else {
					/*if (adch.choiceFollowedWithADActionStateWithGates()) {
						int tmpX = currentX;
						for(i=0; i<elt.getNbNext(); i++){
							String gua = null;
							if (adch.isGuarded(i)) {
								gua = convertGuard(adch.getGuard(i));
							}
							table.addADComponentLocation(elt, previous, previous);
							makeElementBehavior(t, template, elt.getNext(i), previous, end, gua);
							currentX += 2 * STEP_LOOP_X;
						}*/
					if (adch.isSpecialChoiceDelay()) {
						int tmpX = currentX;
						for(i=0; i<elt.getNbNext(); i++){
							String gua = null;
							String guadelay = null;
							if (adch.isGuarded(i)) {
								gua = convertGuard(adch.getGuard(i));
							}
							
							if (elt.getNext(i) instanceof ADDelay) {
								guadelay = ((ADDelay)(elt.getNext(i))).getValue();
								guadelay = 	"(h__ >= (" + guadelay + "))";
							}
							
							if (elt.getNext(i) instanceof ADLatency) {
								guadelay = ((ADLatency)(elt.getNext(i))).getValue();
								guadelay = 	"(h__ >= (" + guadelay + "))";
							}
							
							if (elt.getNext(i) instanceof ADTimeInterval) {
								guadelay = ((ADTimeInterval)(elt.getNext(i))).getMinValue();
								guadelay = 	"(h__ >= (" + guadelay + "))";
							}
							
							if (gua == null) {
								gua = guadelay;
							} else {
								if (guadelay != null) {
									gua = "((" + gua + " && " + guadelay + ")";
								}
							}
							
							table.addADComponentLocation(elt, previous, previous);
							
							makeElementBehavior(t, template, elt.getNext(i), previous, end, gua);
							currentX += 2 * STEP_LOOP_X;
						}
					} /*else if (adch.isElseChoice()) {
						System.out.println("Else choice detected");
						int tmpX = currentX;
						previous.setUrgent();
						for(i=0; i<elt.getNbNext(); i++){
							loc = addLocation(template);
							tr = addTransition(template, previous, loc);
							action = convertGuard(adch.getGuard(i));
							setGuard(tr, action);
							
							table.addADComponentLocation(elt, previous, loc);
							
							makeElementBehavior(t, template, elt.getNext(i), loc, end, null);
							currentX += 2 * STEP_LOOP_X;
						}
						
					}*/ else {
						System.out.println("Irregular choice");
						setMultiProcess(template);
						addParallel(t, template);
						
						//start other tasks
						loc = previous;
						
						loc.setUrgent();
						loc3 = addLocation(template);
						loc3.setCommitted();
						tr1 = addRTransition(template, loc, loc3);
						setSynchronization(tr1, "lockmutextask__!");
						//addAssignment(tr1, "gotasks__ = 0, \nstartingid__ = myid__,\npreemptid__ = myid__,\ngroupid__= firstGroupId(),\n waitgroupid__ = groupid__");
						addAssignment(tr1, "gotasks__ = 0, \nstartingid__ = myid__,\npreemptid__ = myid__,\ngroupid__= mygroupid__");
						loc = loc3;
						
						//System.out.println("Managing preempt");
						index = currentX;
						for(i=1; i<elt.getNbNext(); i++){
							loc2 = makePreemptFromInit(t, template);
							loc1 = addLocation(template);
							loc1.setUrgent();
							tr1 = addTransition(template, loc, loc1);
							if (i!=0) {
								setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__),\nmygroupid__ = groupid__");
							} else {
								setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__)");
							}
							loc3 = addLocation(template);
							loc3.setCommitted();
							tr1 = addTransition(template, loc1, loc3);
							setSynchronization(tr1, "begintask__!");
							setAssignment(tr1, makeSetParam(t));
							loc = loc3;
							currentX += 2 * STEP_LOOP_X;
							// Manage guard if applicable
							//System.out.println("Guard #" + i);
							if (adch.isGuarded(i)) {
								loc4 = addRLocation(template);
								tr1 = addTransition(template, loc2, loc4);
								action = convertGuard(adch.getGuard(i));
								//System.out.println("action=" + action);
								setGuard(tr1, action);
								loc2.setUrgent();
								//System.out.println("guard set");
								loc5 = addRLocation(template);
								tr1 = addTransition(template, loc2, loc5);
								action = convertGuard("!(" + adch.getGuard(i) + ")");
								setGuard(tr1, action);
								
								
								loc2 = loc4;
							}
							
							table.addADComponentLocation(elt, previous, loc2);
							
							makeElementBehavior(t, template, elt.getNext(i), loc2, end, null);
						}
						
						/*loc3 = addLocation(template);
						tr1 = addTransition(template, loc, loc3);
						setAssignment(tr1, "gotasks__ = " + (elt.getNbNext() - 1));
						loc2 = addLocation(template);
						tr1 = addTransition(template, loc3, loc2);
						setSynchronization(tr1, "unlockmutextask__!");
						setGuard(tr1, "gotasks__ == 0");
						//setAssignment(tr1, "makeParallel(myid__, startingid__)");
						
						loc3 = addLocation(template);
						tr1 = addTransition(template, loc2, loc3);
						setSynchronization(tr1, "endGroup__[waitgroupid__]?");
						
						makeEnd(template, loc3);*/
						
						loc3 = addRLocation(template);
						//loc3.setUrgent();
						loc3.setCommitted();
						tr1 = addTransition(template, loc, loc3);
						//setAssignment(tr1, "gotasks__ = " + (elt.getNbNext() - 1));
						setSynchronization(tr1, "goAllTasks__!");
						
						loc2 = addRLocation(template);
						tr1 = addTransition(template, loc3, loc2);
						setSynchronization(tr1, "unlockmutextask__!");
						setGuard(tr1, "gotasks__ == 0");
						setAssignment(tr1, "makeParallel(myid__, startingid__),\nmakePreempt(myid__, preemptid__, startingid__)");
						
						if (adch.isGuarded(0)) {
							loc4 = addRLocation(template);
							tr1 = addTransition(template, loc2, loc4);
							loc2.setUrgent();
							action = convertGuard(adch.getGuard(0));
							setGuard(tr1, action);
							
							loc5 = addRLocation(template);
							tr1 = addTransition(template, loc2, loc5);
							action = convertGuard("!(" + adch.getGuard(0) + ")");
							setGuard(tr1, action);
							loc2 = loc4;
						}
						
						loc = loc2;
						currentX += 2 * STEP_LOOP_X;
						
						table.addADComponentLocation(elt, previous, loc);
						
						makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
						currentX = index;
					}
				}
			}
			return;
			// Start activities in parallel -> all of them may preempt the others
			
			
			
			// Quite complex -> use other functions to continue
			/*if (adch.isSpecialChoice()) {
				makeSpecialChoice(t, template, elt, previous, end, adch);
				System.out.println("Special choice encountered");
				return;
			} else {
				// Choice is considered as a "regular" choice and not a LOTOS choice
				System.out.println("Warning: elt = " + elt + " is not a special choice");
				makeChoice(t, template, elt, previous, end, adch);
			}*/
			
			// Sequence
		} else if (elt instanceof ADSequence) {
			if (elt.getNbNext() == 0) {
				table.addADComponentLocation(elt, previous, previous);
				makeElementBehavior(t, template, elt.getNext(0), previous, end, null);
				return;
			} else {
				int y = currentY;
				index = elt.getNbNext() -1;
				currentX += 2 * ((elt.getNbNext() - 1) * STEP_LOOP_X);
				loc = addLocation(template);
				table.addADComponentLocation(elt, previous, loc);
				makeElementBehavior(t, template, elt.getNext(index), loc, end, null);
				
				for(i=index-1; i>-1; i--) {
					currentX -= 2 * STEP_LOOP_X;
					currentY = y;
					if (i!=0) {
						loc1 = addLocation(template);
					} else {
						loc1 = previous;
					}
					table.addADComponentLocation(elt, previous, loc1);
					makeElementBehavior(t, template, elt.getNext(i), loc1, loc, null);
					loc = loc1;
				}
			}
			return;
			
			// Parallel
		} else if (elt instanceof ADParallel) {
			adp = (ADParallel)elt;
			// Also, activities leading to the same parallel are ignored for full translation
			int nb = t.getActivityDiagram().getNbComponentLeadingTo(adp);
			if (nb > 1){
				String sync;
				if (paras.contains(adp)) {
					// I am not the first one!
					// Must find my index
					index = paras.indexOf(adp);
					loc = addLocation(template);
					tr = addTransition(template, previous, loc);
					sync = "exitpar__" + parasint.get(index).intValue();
					setSynchronization(tr, sync + "!");
					parasint.set(index, new Integer(parasint.get(index).intValue() + 1));
					return;
				} else {
					// I am the first one
					paras.add(adp);
					parasint.add(new Integer(idPar));
					loc1 = previous;
					for(i=1; i<nb; i++) {
						loc = addLocation(template);
						tr = addTransition(template, loc1, loc);
						sync = "exitpar__" + idPar;
						idPar ++;
						setSynchronization(tr, sync + "?");
						addSynchro(sync);
						loc1 = loc;
					}
					previous = loc1;
				}
			}
			
			// After Parallel
			if (elt.getNbNext() == 1) {
				table.addADComponentLocation(elt, previous, previous);
				makeElementBehavior(t, template, elt.getNext(0), previous, end, null);
				return;
			} else {
				if(isRegularTClass) {
					UPPAALTemplate template1;
					loc1 = previous;
					int tmpint;
					int tmpX;
					int tmpY;
					int size = elt.getNbNext();
					String sync;
					
					for(i=1; i<size; i++){
						tmpX = currentX;
						tmpY = currentY;
						initXY();
						
						idTemplate ++;
						template1 = newTClassTemplate(t, idTemplate);
						// Gate analysis
						makeGates(t, template1);
						// Attributes
						makeAttributes(t, template1);
						tmpint = idPar;
						idPar ++;
						loc2 = makeTaskInit(t, template1);
						loc3 = addLocation(template1);
						tr1 = addTransition(template1, loc2, loc3);
						sync =  "gopar__" + tmpint;
						setSynchronization(tr1, sync + "?");
						setAssignment(tr1, makeGetParam(t));
						
						makeElementBehavior(t, template1, elt.getNext(i), loc3, null, null);
						
						currentX = tmpX;
						currentY = tmpY;
						loc = addLocation(template);
						if (i!= (size-1)) {
							loc.setCommitted();
						}
						tr = addTransition(template, loc1, loc);
						setSynchronization(tr, sync + "!");
						addSynchro(sync);
						setAssignment(tr, makeSetParam(t));
						//addAssignment(tr, s[1]);
						loc1 = loc;
					}
					
					makeElementBehavior(t, template, elt.getNext(0), loc1, end, null);
					
				} else {
					// To be done: Search for a recursion path: if found -> stay in this one.
					setMultiProcess(template);
					
					addParallel(t, template);
					
					//start other tasks
					loc = previous;
					loc.setUrgent();
					
					loc3 = addRUnlockTaskLocation(template);
					loc3.setUrgent();
					tr1 = addRTransition(template, loc, loc3);
					setSynchronization(tr1, "lockmutextask__!");
					addAssignment(tr1, "gotasks__ = 0, \nstartingid__ = myid__,\ngroupid__= firstGroupId(),\n waitgroupid__ = groupid__");
					loc = loc3;
					
					int tmpx = currentX;
					//System.out.println("Managing parallel");
					for(i=0; i<elt.getNbNext(); i++){
						
						loc2 = makeParaFromInit(t, template, adp, i);
						loc1 = addRUnlockTaskLocation(template);
						loc1.setUrgent();
						tr1 = addTransition(template, loc, loc1);
						if (i!=0) {
							setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__),\ngroups__[groupid__] ++");
						} else {
							setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__)");
						}
						
						loc3 = addRUnlockTaskLocation(template);
						loc3.setUrgent();
						tr1 = addTransition(template, loc1, loc3);
						setSynchronization(tr1, "begintask__!");
						setAssignment(tr1, makeSetParam(t));
						loc = loc3;
						currentX += ((i + 1) * STEP_LOOP_X);
						table.addADComponentLocation(elt, previous, loc2);
						makeElementBehavior(t, template, elt.getNext(i), loc2, end, null);
						currentX = tmpx;
					}
					
					// old one
					loc3 = addRUnlockTaskLocation(template);
					loc3.setCommitted();
					tr1 = addTransition(template, loc, loc3);
					//setAssignment(tr1, "gotasks__ = " + (elt.getNbNext()));
					setSynchronization(tr1, "goAllTasks__!");
					loc2 = addRLocation(template);
					tr1 = addTransition(template, loc3, loc2);
					setSynchronization(tr1, "unlockmutextask__!");
					setGuard(tr1, "gotasks__ == 0");
					//setAssignment(tr1, "makeParallel(myid__, startingid__)");
					
					loc3 = addRLocation(template);
					tr1 = addTransition(template, loc2, loc3);
					setGuard(tr1, "groupid__ == waitgroupid__");
					setSynchronization(tr1, "endGroup__?");
					
					//makeElementBehavior(t, template, elt.getNext(0), loc3, end);
					table.addADComponentLocation(elt, previous, loc3);
					makeEnd(template, loc3);
				}
			}
			return;
			
        } else if (elt instanceof ADPreempt) {
			if (elt.getNbNext() == 0) {
				table.addADComponentLocation(elt, previous, previous);
				makeElementBehavior(t, template, elt.getNext(0), previous, end, null);
				return;
			} else {
				// To be done: Search for a recursion path: if found -> stay in this one.
				setMultiProcess(template);
				addParallel(t, template);
				
				//start other tasks
				loc = previous;
				
				loc3 = addLocation(template);
				tr1 = addRTransition(template, loc, loc3);
				setSynchronization(tr1, "lockmutextask__!");
				//addAssignment(tr1, "gotasks__ = 0, \nstartingid__ = myid__,\npreemptid__ = myid__,\ngroupid__= firstGroupId(),\n waitgroupid__ = groupid__");
				addAssignment(tr1, "gotasks__ = 0, \nstartingid__ = myid__,\npreemptid__ = myid__,\ngroupid__= mygroupid__");
				loc = loc3;
				
				System.out.println("Managing preempt");
				index = currentX;
				for(i=1; i<elt.getNbNext(); i++){
					loc2 = makePreemptFromInit(t, template);
					loc1 = addLocation(template);
					tr1 = addTransition(template, loc, loc1);
					if (i!=0) {
						setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__),\nmygroupid__ = groupid__");
					} else {
						setAssignment(tr1, "locid__ = " + loc2.int_id + ",\ntaskid__ = firstFree(myid__)");
					}
					loc3 = addLocation(template);
					tr1 = addTransition(template, loc1, loc3);
					setSynchronization(tr1, "begintask__!");
					setAssignment(tr1, makeSetParam(t));
					loc = loc3;
					currentX += 2 * STEP_LOOP_X;
					table.addADComponentLocation(elt, previous, loc2);
					makeElementBehavior(t, template, elt.getNext(i), loc2, end, null);
				}
				
				/*loc3 = addLocation(template);
				tr1 = addTransition(template, loc, loc3);
				setAssignment(tr1, "gotasks__ = " + (elt.getNbNext() - 1));
				loc2 = addLocation(template);
				tr1 = addTransition(template, loc3, loc2);
				setSynchronization(tr1, "unlockmutextask__!");
				setGuard(tr1, "gotasks__ == 0");
				//setAssignment(tr1, "makeParallel(myid__, startingid__)");
				
				loc3 = addLocation(template);
				tr1 = addTransition(template, loc2, loc3);
				setSynchronization(tr1, "endGroup__[waitgroupid__]?");
				
				makeEnd(template, loc3);*/
				
				loc3 = addRLocation(template);
				loc3.setCommitted();
				tr1 = addTransition(template, loc, loc3);
				//setAssignment(tr1, "gotasks__ = " + (elt.getNbNext() - 1));
				setSynchronization(tr1, "goAllTasks__!");
				loc2 = addRLocation(template);
				tr1 = addTransition(template, loc3, loc2);
				setSynchronization(tr1, "unlockmutextask__!");
				setGuard(tr1, "gotasks__ == 0");
				setAssignment(tr1, "makeParallel(myid__, startingid__)");
				
				loc = loc2;
				currentX += 2 * STEP_LOOP_X;
				table.addADComponentLocation(elt, previous, loc);
				makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
				currentX = index;
			}
			return;
			
			// Deterministic delay
		} else if (elt instanceof ADDelay) {
			loc = makeTimeInterval(template, previous, ((ADDelay)(elt)).getValue(), ((ADDelay)(elt)).getValue());
			table.addADComponentLocation(elt, previous, loc);
			makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			return;
			
			// Non deterministic delay
		} else if (elt instanceof ADLatency) {
			loc = makeTimeInterval(template, previous, "0", ((ADLatency)(elt)).getValue());
			table.addADComponentLocation(elt, previous, loc);
			makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			return;
			
			// Non deterministic delay
		} else if (elt instanceof ADTimeInterval) {
			loc = makeTimeInterval(template, previous, ((ADTimeInterval)(elt)).getMinValue(), ((ADTimeInterval)(elt)).getMaxValue());
			table.addADComponentLocation(elt, previous, loc);
			makeElementBehavior(t, template, elt.getNext(0), loc, end, null);
			return;
			
		} else {
			System.out.println("Warning: elt = " + elt + " is not yet taken into account -> skipping");
			table.addADComponentLocation(elt, previous, previous);
			makeElementBehavior(t, template, elt.getNext(0), previous, end, null);
			return;
		}
	}
	
	public UPPAALLocation makeTimeInterval(UPPAALTemplate template, UPPAALLocation previous, String minint, String maxint) {
		UPPAALLocation loc, loc1;
		UPPAALTransition tr, tr1;
		loc1 = addRLocation(template);
		previous.setUrgent();
		tr1 = addRTransition(template, previous, loc1);
		setAssignment(tr1, "h__ = 0");
		loc = addRLocation(template);
		tr = addRTransition(template, loc1, loc);
		loc1.setInvariant("(h__ <= (" + maxint + "))");
		addGuard(tr, "(h__ >= (" + minint + "))");
		return loc;
	}
	
	public void addParallel(TClass t, UPPAALTemplate template) {
		/*if (parallels.contains(t)) {
			//Nothing to do!
		}
		parallels.add(t);
		UPPAALLocation loc1 = template.getInitLocation();
		UPPAALLocation loc = addLocation(template);
		template.setInitLocation(loc);
		template.addParameter("int " + t.getName() + "__id");
		UPPAALTransition tr = addTransition(template, loc, loc1);
		setGuard(tr, "(para__" + t.getName() + " == " + t.getName() + "__id)&&(paralocid == 0)");
		setSynchronization(tr, "gopara__" + t.getName() + "?");
		//setAssignment(tr, "para__" + t.getName() + " ++");*/
	}
	
	public UPPAALLocation makeTaskInit(TClass t, UPPAALTemplate template) {
		if (!isRegular()) {
			template.addParameter("int myid__, int mygroupid__");
		}
		currentX = currentX - 100;
		UPPAALLocation loc1 = addLocation(template);
		currentX = currentX + 100;
		template.setInitLocation(loc1);
		if (!isRegularTClass) {
			UPPAALLocation loc2 = addRLocation(template);
			UPPAALTransition tr = addTransition(template, loc1, loc2);
			setGuard(tr, "(locid__ ==" + loc1.int_id + ") && (myid__ == taskid__)");
			setSynchronization(tr, "begintask__?");
			String s = "";
			/*s = makeGetParam(t);
			if (s.length() > 1) {
				s +=",\n";
			}*/
			s += "startTask(myid__),\nmygroupid__ = groupid__";
			setAssignment(tr, s);
			loc1 = loc2;
		}
		return loc1;
	}
	
	public UPPAALLocation makeParaFromInit(TClass t, UPPAALTemplate template, ADParallel adp, int index) {
		Gate g;
		
		UPPAALLocation loc1 = template.getInitLocation();
		UPPAALLocation loc = addLocation(template);
		UPPAALTransition tr = addTransition(template, loc1, loc);
		setGuard(tr, "(locid__ == " + (loc.int_id +1)+ ")&&(myid__ == taskid__)");
		setSynchronization(tr, "begintask__?");
		String s = "";
		s = makeGetParam(t);
		if (s.length() > 1) {
			s +=",\n";
		}
		s += "startTask(myid__),\n makeParallel(myid__, startingid__),\n mygroupid__ = groupid__";
		
		// Synchronized gates?
		if (adp.nbGate() > 0) {
			for(int i=0; i<adp.nbGate(); i++) {
				g = adp.getGate(i);
				if (index==0 ) {
					s+= ",\n" + t.getName() + "__" + g.getName() + TURTLE2UPPAAL.SYNCID + "= 1";
				} else {
					s+= ",\n" + t.getName() + "__" + g.getName() + TURTLE2UPPAAL.SYNCID + "= 2";
				}
			}
		}
		
		
		//s += "startTask(myid__),\n mygroupid__ = groupid__\n";
		setAssignment(tr, s);
		UPPAALLocation loc2 = addRLocation(template);
		UPPAALTransition tr1 = addRTransition(template, loc, loc2);
		//addGuard(tr1, "gotasks__ > 0");
		//addAssignment(tr1, "gotasks__ --");
		setSynchronization(tr1, "goAllTasks__?");
		
		return loc2;
	}
	
	public UPPAALLocation makePreemptFromInit(TClass t, UPPAALTemplate template) {
		UPPAALLocation loc1 = template.getInitLocation();
		UPPAALLocation loc = addLocation(template);
		//loc.setCommitted();
		UPPAALTransition tr = addTransition(template, loc1, loc);
		setGuard(tr, "(locid__ == " + (loc.int_id +1)+ ")&&(myid__ == taskid__)");
		setSynchronization(tr, "begintask__?");
		String s = "";
		s = makeGetParam(t);
		if (s.length() > 1) {
			s +=",\n";
		}
		s += "startTask(myid__),\n makePreempt(myid__, preemptid__, startingid__),\npreemptid__ = myid__,\n mygroupid__ = groupid__\n";
		setAssignment(tr, s);
		UPPAALLocation loc2 = addRLocation(template);
		UPPAALTransition tr1 = addRTransition(template, loc, loc2);
		//addGuard(tr1, "gotasks__ > 0");
		//addAssignment(tr1, "gotasks__ --");
		setSynchronization(tr1, "goAllTasks__?");
		
		
		return loc2;
	}
	
    // Assumes adch is a not special choice
	public void makeChoice(TClass t, UPPAALTemplate template, ADComponent elt, UPPAALLocation previous, UPPAALLocation end, ADChoice adch) {
		int nbG = adch.getNbGuard();
		UPPAALLocation loc1, loc2;
		UPPAALTransition tr;
		
		for(int i=0; i<nbG; i++) {
			loc1 = addLocation(template);
			makeElementBehavior(t, template, elt.getNext(i), loc1, end, null);
			tr = addTransition(template, previous, loc1);
			setGuard(tr, convertGuard(adch.getGuard(i)));
		}
	}
	
	// Assumes adch is a special choice
	public void makeSpecialChoice(TClass t, UPPAALTemplate template, ADComponent elt, UPPAALLocation previous, UPPAALLocation end, ADChoice adch) {
		ADActionStateWithGate adag;
		int nbG = adch.getNbGuard();
		UPPAALLocation loc1 = previous, loc2;
		UPPAALTransition tr;
		int id = idChoice;
		String param;
		String sync;
		int i;
		
		idChoice ++;
		
		spec.addGlobalDeclaration("\n//Choice #" + id + " of " + template.getName() + "\n");
		//spec.addGlobalDeclaration("int idchoice__" + id + " = 0;\n");
		
		
		for(i=0; i<nbG; i++) {
			adag = adch.getADActionStateWithGate(i);
			loc2 = addLocation(template);
			tr = addTransition(template, loc1, loc2);
			sync = "start__" + id + "__" + i ;
			setSynchronization(tr, sync + "!");
			spec.addGlobalDeclaration("urgent chan " + sync + ";\n");
			makeAttributeChoice(t, id, i);
			param = makeSetParam(t, id, i);
			setAssignment(tr, param);
			loc1 = loc2;
		}
		
		for(i=0; i<nbG; i++) {
			adag = adch.getADActionStateWithGate(i);
			loc2 = addLocation(template);
			tr = addTransition(template, loc1, loc2);
			sync = "go__" + id + "__" + i;
			setSynchronization(tr, sync + "?");
			spec.addGlobalDeclaration("urgent chan " + sync + ";\n");
			param = makeGetParam(t, id, i);
			setAssignment(tr, param);
			currentX += STEP_LOOP_X;
			makeElementBehavior(t, template, adag.getNext(0), loc2, end, null);
		}
		
		currentX -= (nbG * STEP_LOOP_X);
		
		for(i=0; i<nbG; i++) {
			makeChoiceTemplate(t, adch, id, i);
		}
	}
	
	public String makeSetParam(TClass t, int id1, int id2) {
		Vector v = t.getParamList();
		Param p;
		String s = "";
		
		for(int i=0; i<v.size(); i++) {
			p = (Param)(v.get(i));
			if (i!=0) {
				s += ",\n";
			}
			s += t.getName() + "__" + p.getName() + "__" + id1 + "__" + id2 + " = " + p.getName();
		}
		return s;
	}
	
	// Choice has to be a special choice
	public void makeChoiceTemplate(TClass t, ADChoice adch, int id1, int id2) {
		UPPAALLocation loc, loc1, loc2, loc3, loc4, loc5, loc6, loc22;
		UPPAALTransition tr;
		String sync, param, guard, delaymin, delaymax, delays, action;
		ADActionStateWithGate adag;
		Gate g;
		String choicedone = "choice_done__" + id1;
		
		int tmpx = currentX;
		int tmpy = currentY;
		
		initXY();
		
		if (id2 == 0) {
			spec.addGlobalDeclaration("int " + choicedone + " = 0;\n");
		}
		
		
		UPPAALTemplate template = new UPPAALTemplate();
		template.setName(t.getName() + "__choice__" + id1 + "__" + id2);
		spec.addTemplate(template);
		makeAttributes(t, template);
		
		
		
		loc = addLocation(template);
		template.setInitLocation(loc);
		loc1 = addLocation(template);
		tr = addTransition(template, loc, loc1);
		setGuard(tr, choicedone + " == 0");
		
		currentX += (2 * STEP_LOOP_X);
		
		loc2 = addLocation(template);
		tr = addTransition(template, loc1, loc2);
		sync = "start__" + id1 + "__" + id2 ;
		setSynchronization(tr, sync + "?");
		param = makeGetParam(t, id1, id2);
		setAssignment(tr, param);
		
		loc3 = addLocation(template);
		
		guard = convertGuard(adch.getGuard(id2));
		if (guard.length() != 0) {
			loc22 = addLocation(template);
			tr = addTransition(template, loc2, loc22);
			setGuard(tr, "!(" + guard + ")");
			tr = addTransition(template, loc22, loc);
			setGuard(tr, choicedone + " > 0");
			setAssignment(tr, choicedone + "--");
		}
		
		tr = addTransition(template, loc3, loc);
		setGuard(tr, choicedone + " > 0");
		setAssignment(tr, choicedone + "--");
		
		tr = addTransition(template, loc2, loc3);
		setGuard(tr, guard);
		
		delaymin = adch.getMinDelay(id2);
		delaymax = adch.getMaxDelay(id2);
		if ((delaymin.compareTo("-1") == 0) && (delaymax.compareTo("-1") ==0)) {
			delays = "";
		} else {
			delays = "h__ >=" + delaymin; // What to do with latency???
		}
		
		currentX += (2 * STEP_LOOP_X);
		
		loc4 = addLocation(template);
		tr = addTransition(template, loc3, loc4);
		setGuard(tr, delays);
		
		tr = addTransition(template, loc4, loc);
		setGuard(tr, choicedone + " > 0");
		setAssignment(tr, choicedone + "--");
		
		loc5 = addLocation(template);
		tr = addTransition(template, loc4, loc5);
		
		adag = adch.getADActionStateWithGate(id2);
		action = adag.getActionValue();
		g = adag.getGate();
		if (isSynchronized(t, g)) {
			String [] s = manageSynchro(t, g, action);
			setSynchronization(tr, s[0]);
			setAssignment(tr, s[1]);
		} else {
			addNotSync(t.getName() + "__" + g.getName());
			setSynchronization(tr, t.getName() + "__" + g.getName() + "!");
			buildAssignment(tr, action);
		}
		setGuard(tr, choicedone + " ==0");
		
		if (tr.assignment.length() > 0) {
			tr.assignment += ",\n";
		}
		tr.assignment += (choicedone + " = " + adch.getNbGuard());
		
		loc6 = addLocation(template);
		tr = addTransition(template, loc5, loc6);
		sync = "go__" + id1 + "__" + id2;
		param = makeSetParam(t, id1, id2);
		setAssignment(tr, param);
		setSynchronization(tr, sync + "!");
		
		tr = addTransition(template, loc6, loc);
		setAssignment(tr, choicedone + "--");
		
		currentX = tmpx;
		currentY = tmpy;
		
	}
	
	public String makeGlobalParamDeclaration(TClass t) {
		Vector v = t.getParamList();
		Param p;
		String s = "";
		
		for(int i=0; i<v.size(); i++) {
			p = (Param)(v.get(i));
			/*if (i!=0) {
				s += ",\n";
			}*/
			if (p.getType() == Param.NAT) {
				s += "int ";
			} else {
				s +="bool ";
			}
			s += t.getName() + "__" + p.getName() + ";\n";
		}
		return s;
	}
	
	public String makeGetParam(TClass t, int id1, int id2) {
		Vector v = t.getParamList();
		Param p;
		String s = "";
		
		for(int i=0; i<v.size(); i++) {
			p = (Param)(v.get(i));
			if (i!=0) {
				s += ",\n";
			}
			s += p.getName() + " = " +  t.getName() + "__" + p.getName() + "__" + id1 + "__" + id2;
		}
		return s;
	}
	
	public String makeGetParam(TClass t) {
		Vector v = t.getParamList();
		Param p;
		String s = "";
		
		for(int i=0; i<v.size(); i++) {
			p = (Param)(v.get(i));
			if (i!=0) {
				s += ",\n";
			}
			s += p.getName() + " = " + t.getName() + "__" + p.getName();
		}
		return s;
	}
	
	public String makeSetParam(TClass t) {
		Vector v = t.getParamList();
		Param p;
		String s = "";
		
		for(int i=0; i<v.size(); i++) {
			p = (Param)(v.get(i));
			if (i!=0) {
				s += ",\n";
			}
			s += t.getName() + "__" + p.getName()+ " = " + p.getName();
		}
		return s;
	}
	
	public void buildAssignment(UPPAALTransition tr, String s) {
		String []tmp;
		int i;
		int nb = Conversion.nbOf(s, '!');
		String ass = "";
		
		maxSentInt = Math.max(nb, maxSentInt);
		tmp = Conversion.cutIntoSectionsBeginningWith(s, '!');
		//System.out.println("s=" + s);
		
		for(i=0; i<nb; i++) {
			if (i!=0) {
				ass += ",\n";
				
			}
			ass += "action_int__" + i + " = " + tmp[i];
		}
		addAssignment(tr, ass);
	}
	
	public String [] manageSynchro(TClass t, Gate g, String action) {
		int index1, index2, i, nb_nat = 0, nb_bool = 0;
		String []result = new String[2];
		String[] tmp;
		String gate;
		char c;
		Param p;
		Relation r = tm.syncRelationWith(t, g);
		
		
		result[0] = "";
		result[1] = "";
		
		if (r == null) {
			return result;
		}
		
		index1 = action.indexOf('!');
		index2 = action.indexOf('?');
		
		// No data sending / receiving
		if ((index1 == -1) && (index2 == -1)) {
			if (r.t1 == t) {
				result[0] = addSynchronized(t, r, g) + "!";
			} else {
				result[0] = addSynchronized(t, r, g) + "?";
			}
			result[1] = "";
			return result;
			
			// Data receiving only
		} else if ((index1 == -1) && (index2 != -1)) {
			System.out.println("Receiving = " + action);
			
			tmp = Conversion.cutIntoSectionsBeginningWith(action, '?');
			gate = getGlobalGateName(t, r, g);
			if (t == r.t1) {
				c = 'r';
			} else {
				c = 's';
			}
			
			gate += "_";
			
			for(i=0;i<tmp.length; i++) {
				if (i!=0) {
					result[1] += ",\n";
				}
				if (tmp[i].indexOf(":nat") == -1) {
					nb_bool ++;
					result[1] +=  tmp[i].substring(0, tmp[i].indexOf(':')) + " = action_bool__" + i;
					gate += "_" + c + "bool";
				} else {
					nb_nat ++;
					result[1] += tmp[i].substring(0, tmp[i].indexOf(':')) + " = action_int__" + i;
					gate += "_" + c + "int";
				}
			}
			
			maxSentInt = Math.max(maxSentInt, nb_nat);
			maxSentBool = Math.max(maxSentBool, nb_bool);
			addSynchro(gate);
			result[0] = gate + "?";
			return result;
			
			// Data sending only
		} else if ((index1 != -1) && (index2 == -1)) {
			//Must check whether the other class has only sending on the corresponding gate, or not.
			TClass tc;
			if (r.t1 == t) {
				tc = r.t2;
			} else {
				tc = r.t1;
			}
			if (!tc.hasReceivingGate(g)) {
				tmp = Conversion.cutIntoSectionsBeginningWith(action, '!');
				gate = getGlobalGateName(t, r, g);
				if (t == r.t1) {
					c = 's';
				} else {
					c = 'r';
				}
				
				gate += "_";
				
				for(i=0;i<tmp.length; i++) {
					if (i!=0) {
						result[1] += ",\n";
					}
					p = t.getParamByName(tmp[i]);
					if (p == null) {
						// p is directly a value
						if ((tmp[i].compareTo("true") ==0) || (tmp[i].compareTo("false") ==0)) {
							nb_bool ++;
							result[1] +=  "action_bool__" + i +  " = " + tmp[i];
							gate += "_" + c + "bool";
						} else {
							nb_nat ++;
							result[1] += "action_int__" + i +  " = " + tmp[i];
							gate += "_" + c + "int";
						}
					} else {
						if (!p.isNat()) {
							nb_bool ++;
							result[1] +=  "action_bool__" + i +  " = " + tmp[i];
							gate += "_" + c + "bool";
						} else {
							nb_nat ++;
							result[1] += "action_int__" + i +  " = " + tmp[i];
							gate += "_" + c + "int";
						}
					}
				}
				
				maxSentInt = Math.max(maxSentInt, nb_nat);
				maxSentBool = Math.max(maxSentBool, nb_bool);
				addSynchro(gate);
				result[0] = gate + "!";
				return result;
			} else {
				// Complex case ...
			}
			
			// Both sending and receiving -> complex case
		} else {
			// complex case
		}
		
		
		return result;
	}
	
	public String getGlobalGateName(TClass t, Relation r, Gate g) {
		Gate g1, g2;
		
		if (t == r.t1) {
			g1 = g;
			g2 = r.correspondingGate(g, t);
		} else {
			g2 = g;
			g1 = r.correspondingGate(g, t);
		}
		
		String s = r.t1.getName() + "_" + g1.getName() + "__" + r.t2.getName() + "_" + g2.getName();
		return s;
	}
	
	public String addSynchronized(TClass t, Relation r, Gate g) {
		String s = getGlobalGateName(t, r, g);
		addSynchro(s);
		return s;
	}
	
	public void addSynchro(String s) {
		ListIterator iterator = gatesSynchronized.listIterator();
		String tmp;
		
		while(iterator.hasNext()) {
			tmp = (String)(iterator.next());
			if (tmp.compareTo(s) == 0) {
				return;
			}
		}
		
		gatesSynchronized.add(s);
	}
	
	public void makeEnd(UPPAALTemplate template, UPPAALLocation previous) {
		if (!isRegularTClass) { 
			UPPAALLocation loc1 = addRLocation(template);
			UPPAALLocation loc2 = addRUnlockLocation(template);
			UPPAALLocation loc3 = addRLocation(template);
			
			UPPAALTransition tr1 = addRTransition(template, previous, loc1);
			UPPAALTransition tr2 = addEndTransition(template, loc1);
			UPPAALTransition tr3 = addRTransition(template, previous, loc2);
			UPPAALTransition tr4 = addRTransition(template, loc2, loc3);
			UPPAALTransition tr5 = addEndTransition(template, loc3);
			
			addGuard(tr1, "groups__[mygroupid__] > 1");
			setAssignment(tr1, "groups__[mygroupid__] --");
			
			addGuard(tr2, "mygroupid__ == groupid__");
			setSynchronization(tr2, "endGroup__?");
			
			addGuard(tr3, "groups__[mygroupid__] == 1");
			setSynchronization(tr3, "lockmutexgroup__!");
			addAssignment(tr3, "groupid__ = mygroupid__");
			
			setSynchronization(tr4, "endGroup__!");
			
			addGuard(tr5, "groups__[mygroupid__] == 1");
			setSynchronization(tr5, "unlockmutexgroup__!");
			addAssignment(tr5, "groups__[mygroupid__] = 0");
		}
		
	}
    
    public UPPAALLocation addLocation(UPPAALTemplate template) {
		UPPAALLocation loc = new UPPAALLocation();
		loc.idPoint.x = currentX;
		loc.idPoint.y = currentY;
		loc.namePoint.x = currentX + NAME_X;
		loc.namePoint.y = currentY + NAME_Y;
		template.addLocation(loc);
		currentX += STEP_X;
		currentY += STEP_Y;
		return loc;
    }
    
    public UPPAALLocation addRLocation(UPPAALTemplate template) {
		UPPAALLocation loc = new UPPAALLocation();
		loc.idPoint.x = currentX;
		loc.idPoint.y = currentY;
		loc.namePoint.x = currentX + NAME_X;
		loc.namePoint.y = currentY + NAME_Y;
		template.addLocation(loc);
		currentX += STEP_X;
		currentY += STEP_Y;
		
		if (!isRegularTClass) {
			UPPAALTransition tr = addEndTransition(template, loc);
			setGuard(tr, "!canRun(myid__)");
		}
		
		return loc;
    }
	
	public UPPAALLocation addRUnlockLocation(UPPAALTemplate template) {
		UPPAALLocation loc = new UPPAALLocation();
		loc.idPoint.x = currentX;
		loc.idPoint.y = currentY;
		loc.namePoint.x = currentX + NAME_X;
		loc.namePoint.y = currentY + NAME_Y;
		template.addLocation(loc);
		currentX += STEP_X;
		currentY += STEP_Y;
		
		if (!isRegularTClass) {
			UPPAALTransition tr = addEndTransition(template, loc);
			setGuard(tr, "!canRun(myid__)");
			setSynchronization(tr, "unlockmutexgroup__!");
		}
		
		return loc;
    }
    
    public UPPAALLocation addRUnlockTaskLocation(UPPAALTemplate template) {
		UPPAALLocation loc = new UPPAALLocation();
		loc.idPoint.x = currentX;
		loc.idPoint.y = currentY;
		loc.namePoint.x = currentX + NAME_X;
		loc.namePoint.y = currentY + NAME_Y;
		template.addLocation(loc);
		currentX += STEP_X;
		currentY += STEP_Y;
		
		if (!isRegularTClass) {
			UPPAALTransition tr = addEndTransition(template, loc);
			setGuard(tr, "!canRun(myid__)");
			setSynchronization(tr, "unlockmutextask__!");
		}
		
		return loc;
    }
	
	public void addRandomNailPoint(UPPAALTransition tr) {
		int x = 0, y = 0;
		if (tr.sourceLoc != tr.destinationLoc) {
			x = ((tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2) - 25 + (int)(50.0 * Math.random());
			y = ((tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2) - 25 + (int)(50.0 * Math.random());
			tr.points.add(new Point(x, y));
		}
	}
    
    public UPPAALTransition addTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
		UPPAALTransition tr = new UPPAALTransition();
		tr.sourceLoc = loc1;
		tr.destinationLoc = loc2;
		template.addTransition(tr);
		// Nails?
		// Adding random intermediate nail
		addRandomNailPoint(tr);
		/*int x = 0, y = 0;
		if (loc1 != loc2) {
			x = ((loc1.idPoint.x + loc2.idPoint.x)/2) - 25 + (int)(50.0 * Math.random());
			y = ((loc1.idPoint.y + loc2.idPoint.y)/2) - 25 + (int)(50.0 * Math.random());
			tr.points.add(new Point(x, y));
		}*/
		return tr;
    }
	
    public UPPAALTransition addEndTransition(UPPAALTemplate template, UPPAALLocation loc1) {
		UPPAALTransition tr = addTransition(template, loc1, template.getInitLocation());
		//template.addTransition(tr);
		setEndAssignment(tr);
		addRandomNailPoint(tr);
		return tr;
    }
    
    public UPPAALTransition addRTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
		UPPAALTransition tr = new UPPAALTransition();
		tr.sourceLoc = loc1;
		tr.destinationLoc = loc2;
		template.addTransition(tr);
		
		if (!isRegularTClass) {
			addGuard(tr, "canRun(myid__)");
		}
		
		addRandomNailPoint(tr);
		// Nails?
		return tr;
    }
    
    public UPPAALTransition addActionTransition(UPPAALTemplate template, UPPAALLocation loc1, UPPAALLocation loc2) {
		UPPAALTransition tr = addRTransition(template, loc1, loc2);
		if (!isRegularTClass) {
			addAssignment(tr, "preempt(myid__),\nh__ = 0");
		}
		addRandomNailPoint(tr);
		return tr;
    }
    
    public void setSynchronization(UPPAALTransition tr, String s) {
		tr.synchronization = modifyString(s);
		tr.synchronizationPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + SYNCHRO_X;
		tr.synchronizationPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + SYNCHRO_Y;
    }
    
    public void addGuard(UPPAALTransition tr, String s) {
		if ((tr.guard == null) || (tr.guard.length() < 2)){
			tr.guard = modifyString(s);
		} else {
			tr.guard = "(" + tr.guard + ")&&(" + modifyString(s) + ")";
		}
		tr.guardPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + GUARD_X;
		tr.guardPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + GUARD_Y;
    }
    
    public void setInvariant(UPPAALLocation loc, String s) {
	    loc.setInvariant(modifyString(s));
    }
    
    public void setGuard(UPPAALTransition tr, String s) {
		tr.guard = modifyString(s);
		tr.guardPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + GUARD_X;
		tr.guardPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + GUARD_Y;
    }
    
    public void setAssignment(UPPAALTransition tr, String s) {
		tr.assignment = modifyString(s);
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
    }
    
    public void addAssignment(UPPAALTransition tr, String s) {
		if (s.length() <1) {
			return;
		}
		if ((tr.assignment == null) || (tr.assignment.length() < 2)){
			tr.assignment = modifyString(s);
		} else {
			tr.assignment = tr.assignment + ",\n " + modifyString(s);
		}
		
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
    }
    
    public void setEndAssignment(UPPAALTransition tr) {
		if (!isRegularTClass) {
			tr.assignment = "endTask(myid__)";
		}
		tr.assignmentPoint.x = (tr.sourceLoc.idPoint.x + tr.destinationLoc.idPoint.x)/2 + ASSIGN_X;
		tr.assignmentPoint.y = (tr.sourceLoc.idPoint.y + tr.destinationLoc.idPoint.y)/2 + ASSIGN_Y;
    }
    
    public void makeSystem(int nb) {
		ListIterator iterator = spec.getTemplates().listIterator();
		UPPAALTemplate template;
		String system = "system ";
		String dec = "";
		int id = 0;
		int i;
		TClass t;
		
		while(iterator.hasNext()) {
			template = (UPPAALTemplate)(iterator.next());
			t = tm.getTClassWithName(template.getName());
			System.out.println("temp=" + template.getName());
			if (t != null) {
				if (templatesWithMultipleProcesses.contains(template)) {
					table.setIds(template, id, id+nb-1);
					for(i=0; i<nb; i++) {
						dec += template.getName() + "__" + id + " = " + template.getName() + "(" + id + ", 0);\n";
						if (i != 0) {
							system += ",";
						}
						system += template.getName() + "__" + id;
						id ++;
					}
				} else {
					if (!isRegular()) {
						dec += template.getName() + "__" + id + " = " + template.getName() + "(" + id + ", 0);\n";
						system += template.getName() + "__" + id;
						table.setIds(template, id, id);
						id += nb;
					} else {
						//System.out.println("temp=" + template.getName());
						dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
						system += template.getName() + "__" + id;
						table.setIds(template, id, id);
						id += nb;
					}
				}
			} else {
				System.out.println("temp=" + template.getName());
				template.removeParameter();
				dec += template.getName() + "__" + id + " = " + template.getName() + "();\n";
				system += template.getName() + "__" + id;
			}
			if (iterator.hasNext()) {
				system += ",";
			} else {
				system += ";";
			}
		}
		
		spec.addInstanciation(dec+system);
    }
	
	public void setMultiProcess(UPPAALTemplate t) {
		System.out.println("Setting multiprocess: " + t.getName());
		if (!(templatesWithMultipleProcesses.contains(t))) {
			templatesWithMultipleProcesses.add(t);
		}
		multiprocess = true;
	}
    
    public String modifyString(String _input) {
        try {
			//_input = Conversion.replaceAllString(_input, "&&", "&amp;&amp;");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
			//_input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
			//_input = Conversion.replaceAllChar(_input, '<', "&lt;");
			//_input = Conversion.replaceAllChar(_input, '>', "&gt;");
			_input = Conversion.replaceAllStringNonAlphanumerical(_input, "mod", "%");
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception when changing binary operator in " + _input);
        }
		//System.out.println("Modified string=" + _input);
        return _input;
    }
    
    public String convertGuard(String g) {
		if (g == null) {
			return "";
		}
		
		if (g.compareTo("null") == 0) {
			return "";
		}
		String action = Conversion.replaceAllChar(g, '[', "");
		action = Conversion.replaceAllChar(action, ']', "");
		return modifyString(action.trim());
    }
	
    
}
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




package jttool;

import java.util.*;


/**
 * Class JTToolThread
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 16/03/2005
 * @version 1.1 16/03/2005
 * @author Ludovic APVRILLE
 */
public abstract class JTToolThread extends Thread {

    private boolean monitor = false;
    private boolean hasToBePreempted = false;
    private boolean startingSequence = false;
    protected LinkedList<JTToolThread> internalThreads;
    protected LinkedList<JTToolThread> threadsThatCanPreemptMe;
    private LinkedList<JTToolThread> toPreempt;

    protected int t__state = 0;
    protected boolean t__go = true;



    public JTToolThread() {
	internalThreads = new LinkedList<>();
	threadsThatCanPreemptMe = new LinkedList<>();
	toPreempt = new LinkedList<>();
    }


    public void setToPreempt(JTToolThread jttt) {
	toPreempt.add(jttt);
    }

    public void addInternalThread(JTToolThread jttt) {
	internalThreads.add(jttt);
    }

    public void addThreadsThatCanPreemptMe(JTToolThread jttt) {
	threadsThatCanPreemptMe.add(jttt);
    }

    public boolean hasToBePreempted() {
	return hasToBePreempted;
    }

    public boolean hasMonitor() {
	return monitor;
    }

    public void setMonitor(boolean b) throws PreemptionException {
	monitor = b;
	if (hasToBePreempted) {
	    //System.out.println("Preempted!");
	    if (toPreempt.size() > 0) {
		makeAllPreempt();
	    }
	    throw new PreemptionException();
	}

	if ((b == false) && (toPreempt.size() > 0)) {
	    //System.out.println("Going to preempt");
	    makeAllPreempt();    
	}
    }

    public void makeAllPreempt() {
        for (JTToolThread t: this.toPreempt) {
	    t.preemptAll(this);
	}
    }
	

    public void preemptAll(JTToolThread jttt) {
        for (JTToolThread t: this.internalThreads) {
	    if (t != jttt) {
		t.preemptAll(this);
	    }
	}

	/*if (toPreempt.size() > 0) {
	    makeAllPreempt();
	    }*/

	preemptMe();
	
    }

    public void preemptMe() {
	hasToBePreempted = true;
	if (hasMonitor()) {
	    interrupt();
	}
    }

    public void ending() {
        for (JTToolThread t: this.internalThreads) {
            try {
                t.join();
            } catch (InterruptedException ie) {

            }
        }

        startingSequence();
    }

    public final void run() {
        try {
            if (t__state == 0) {
                startPreemptionTasks();
            }
            runMe();
            startingSequence();
        } catch (PreemptionException pe) {}

    }

    public void startingSequence() {
        //System.out.println("ending!");
        if (startingSequence) {
            // kill all preemption tasks
            preemptAllPreemptionTasks();
            startSequence();
        }
    }

    public void startPreemptionTasks() {}

    public void setStartingSequence(boolean b) {
        startingSequence = b;
    }

    public void preemptAllPreemptionTasks() {
        for (JTToolThread t: this.threadsThatCanPreemptMe) {
            t.preemptAll(this);
        }
    }

    public void setState(int state) {
        t__state = state;
    }

    public void startSequence() {}

    public abstract void runMe() throws PreemptionException;

}

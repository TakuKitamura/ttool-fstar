/**
 * Class JTToolThread
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 16/03/2005
 * @version 1.1 16/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

public abstract class JTToolThread extends Thread {

    private boolean monitor = false;
    private boolean hasToBePreempted = false;
    private boolean startingSequence = false;
    protected LinkedList internalThreads;
    protected LinkedList threadsThatCanPreemptMe;
    private LinkedList toPreempt;

    protected int t__state = 0;
    protected boolean t__go = true;



    public JTToolThread() {
	internalThreads = new LinkedList();
	threadsThatCanPreemptMe = new LinkedList();
	toPreempt = new LinkedList();
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
	ListIterator iterator = toPreempt.listIterator();
	while(iterator.hasNext()) {
	    ((JTToolThread)(iterator.next())).preemptAll(this);
	}
    }
	

    public void preemptAll(JTToolThread jttt) {
	//System.out.println("Preempt all");
	JTToolThread t;
	ListIterator iterator = internalThreads.listIterator();

	while(iterator.hasNext()) {
	    t = (JTToolThread)(iterator.next());
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
	 ListIterator iterator = internalThreads.listIterator();
	 while(iterator.hasNext()) {
	     try {
		 ((JTToolThread)(iterator.next())).join();
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
	 ListIterator iterator = threadsThatCanPreemptMe.listIterator();
	 while(iterator.hasNext()) {   
	     ((JTToolThread)(iterator.next())).preemptAll(this);
	 }
    }

    public void setState(int state) {
	t__state = state;
    }

    public void startSequence() {}

    public abstract void runMe() throws PreemptionException;

}

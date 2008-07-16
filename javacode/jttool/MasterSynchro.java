/**Copyright or © or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 * /**
 * Class MasterSynchro
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://labsoc.comelec.enst.fr/turtle
 * Creation: 01/02/2006
 * @version 1.3 06/03/2006
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

public class MasterSynchro {
    
    public static MasterSynchro master = new MasterSynchro();
    private LinkedList requested;
    private long time;
    
    
    public MasterSynchro() {
        requested = new LinkedList();
    }
    
    public  SynchroSchemes synchro(SynchroSchemes synchros[], JTToolThread jttt) throws PreemptionException {
	SynchroSchemes sss =  synchro2(synchros, jttt);
	if (sss.jgate.protocol != Network.NO_PROTOCOL) {
	    Network.net.action(sss, sss.jgate.protocol, sss.jgate.localPort, sss.jgate.destPort, sss.jgate.localHost, sss.jgate.destHost);
	}
	return sss;
    }
    
    public synchronized SynchroSchemes synchro2(SynchroSchemes synchros[], JTToolThread jttt) throws PreemptionException {
        long maxTimeToWait;

        //SynchroSchemes ssss[];
        
	if (synchros.length < 1) {
            return null;
        }

	time = System.currentTimeMillis();

	        
        jttt.setMonitor(true);
        
        setStartTime(synchros);
        setGroup(synchros);
        
        
        // Add requested synchros
        addSynchros(synchros);
        
        updateSynchros(synchros);

        
        while(!hasSynchroDone(synchros)) {
            maxTimeToWait = getMaxTimeToWait(synchros);
            try {
                if (maxTimeToWait < 1) {
		    //System.out.println("Waiting no duration: " + synchros[0].jgate.getName());
		    //printRequested();
                    wait();
		    //System.out.println("Waiting up (forced): " + synchros[0].jgate.getName());
                } else {
		    //System.out.println("Waiting with max duration: " + maxTimeToWait + " gate=" + synchros[0].jgate.getName());
		    //printRequested();
                    wait(maxTimeToWait);
                    //updatesynchro?
		    //System.out.println("Waiting up (max elapsed or forced): " + synchros[0].jgate.getName());
                }

		time = System.currentTimeMillis();
                
                //System.out.println("Waking up");
                
                if (!hasSynchroDone(synchros)) {
		    
		    if (allAreInMaxDelays(synchros, System.currentTimeMillis())) {
			removeAllGroup(synchros[0].group);
			System.out.println("Max delay");
			return null;
		    }
                    updateSynchros(synchros);
                }
            } catch (InterruptedException ie) {
            }
            
        }
        
        // Remove all synchros from list
        
        return synchroDone(synchros);
    }

    public boolean allAreInMaxDelays(SynchroSchemes synchros[], long currentTime) {
	for(int i=0; i<synchros.length; i++) {
	    if (synchros[i].maxDelay != -1) {
		if (currentTime < (synchros[i].maxDelay + synchros[i].startSynchroTime)) {
		    return false;
		}
	    } else {
		return false;
	    }
	}
	return true;
    }
    
    public void addSynchros(SynchroSchemes[] synchros) {
        for(int i=0; i<synchros.length; i++) {
            requested.add(synchros[i]);
        }
	//printRequested();
    }
    
    public SynchroSchemes synchroDone(SynchroSchemes[] synchros) {
        for(int i=0; i<synchros.length; i++) {
            if (synchros[i].synchroDone) {
                return synchros[i];
            }
        }
        
        return null;
    }
    
    public boolean hasSynchroDone(SynchroSchemes[] synchros) {
        for(int i=0; i<synchros.length; i++) {
            if (synchros[i].synchroDone) {
                return true;
            }
        }
        
        return false;
    }
    
    public void updateSynchros(SynchroSchemes[] synchros) {
        ListIterator iterator;
        SynchroSchemes sss0, sss1;
        JMasterGate jmg;
	JGate jgate;
        
        //long time = System.currentTimeMillis();
        
        // Randomly start at a given index
        int index = (int)(Math.random() * synchros.length);
        
        for(int cpt=0; cpt<synchros.length; cpt++) {
            sss0 = synchros[index];
            //System.out.println("index = " + index + " Analyzed gate=" + sss0.jgate.getName());
            if ((sss0.minDelay == -1) || (time >= sss0.minDelay +  sss0.startSynchroTime)) {
                //System.out.println("Examining gate");
                jmg = sss0.jgate.getMasterGate();
                // non synchronized gate?
                if (jmg == null) {
                    //System.out.println("Solo gate");
                    //System.out.println("Call on " + name + " " + sss);
		    jgate = sss0.jgate;
                    if (jgate.protocol != Network.NO_PROTOCOL) {
			//System.out.println("Networked gate");
			// Must exit synchro first ...
                        //Network.net.action(sss0, jgate.protocol, jgate.localPort, jgate.destPort, jgate.localHost, jgate.destHost);
                        //System.out.println("protocol = " + protocol);
                    } else {
                        if (sss0 != null) {
                            sss0.fillValue();
                        }
                    }
                    sss0.synchroDone = true;
                    removeAllGroup(sss0.group);
                    return;
                } else {
                    //System.out.println("Synchronized gate");
                    iterator = requested.listIterator();
                    while(iterator.hasNext()) {
                        sss1 = (SynchroSchemes)(iterator.next());
                        if (synchroPossible(sss0, sss1)) {
                            makeSynchro(sss0, sss1);
                            return;
                        }
                    }
                }
                
            }
            index = (index + 1) % synchros.length;
        }
    }
    
    public void makeSynchro(SynchroSchemes sss0, SynchroSchemes sss1) {
        // Update data
	//System.out.println("Making synchro between sss0=" + sss0.jgate.getName() + " and sss1=" + sss1.jgate.getName());
        sss0.completeSynchro(sss1);
	sss1.completeSynchro(sss0);

	// Mark synchro as done
        //setSynchroDoneForGroup(sss0, sss1);
	sss0.synchroDone = true;
	sss1.synchroDone = true;
        
        // Remove elements of the same group
	//printRequested();
        removeAllGroup(sss0.group);
        removeAllGroup(sss1.group);
	//System.out.println("Requested size=" + requested.size());
        //printRequested();

        // Mark synchro as done
        //setSynchroDoneForGroup(sss0, sss1);
        
        // notify others
	//System.out.println("Notifying ...");
        notifyAll();
    }
    
    public void removeAllGroup(JGroupSynchro group) {
        SynchroSchemes sss;
        ListIterator iterator = requested.listIterator();
        LinkedList ll = new LinkedList();
        
        
        while(iterator.hasNext()) {
            sss = (SynchroSchemes)(iterator.next());
            if (sss.group != group) {
                ll.add(sss);
            }
        }

	requested = ll;
    }

    public void printRequested() {
	SynchroSchemes sss;
        ListIterator iterator = requested.listIterator();
        LinkedList ll = new LinkedList();
        
        System.out.print("Requested = ");
        while(iterator.hasNext()) {
            sss = (SynchroSchemes)(iterator.next());
            System.out.print(sss.jgate.getName() + " ");
        }
	System.out.println(" ");
    }
    
    public void setSynchroDoneForGroup(SynchroSchemes sss0, SynchroSchemes sss1) {
	SynchroSchemes sss;
	ListIterator iterator = requested.listIterator();
	LinkedList ll = new LinkedList();
	
	
	while(iterator.hasNext()) {
	    sss = (SynchroSchemes)(iterator.next());
	    if ((sss.group == sss0.group) ||(sss.group == sss1.group)) {
		sss.synchroDone = true;
	    }
	}
	
    }  
    
    
    public boolean synchroPossible(SynchroSchemes sss0, SynchroSchemes sss1) {
        // min delay elapsed for both, and not over maxDelay 


	//System.out.println("Evaluating synchro between " + sss0.jgate.getName() + " and " + sss1.jgate.getName());
	if (!delaysOk(sss0)) {
	    //System.out.println("Delay not ok 0");
	    return false;
	}
	
	//System.out.println("Step1");

	if (!delaysOk(sss1)) {
	    //System.out.println("Delay not ok 1");
	    return false;
	}

	//System.out.println("Step2");

        //System.out.println("Comparing gates " + sss0.jgate.getName() + " and " + sss1.jgate.getName());
        
        //System.out.println("Step 0");
        if (sss0 == sss1) {
            return false;
        }

	//System.out.println("Step3");

	// Synchro not yet done
	if (sss0.synchroDone || sss1.synchroDone) {
	    return false;
	}

	//System.out.println("Step4");
        
        //System.out.println("Step 1");
        if (sss0.jgate.getMasterGate() != sss1.jgate.getMasterGate()) {
            return false;
        }

	//System.out.println("Step5");
        
	if (sss0.jgate.getLeft() == sss1.jgate.getLeft()) {
            return false;
        }

	//System.out.println("Step6");

        if (sss0.group == sss1.group) {
            return false;
        }
        
        //System.out.println("Step7");
        return sss0.isCompatibleWith(sss1);
    }
    

    public void setStartTime(SynchroSchemes synchros[]) {
        //long startTime = System.currentTimeMillis();
        
        for(int i=0; i<synchros.length; i++) {
            synchros[i].startSynchroTime = time;
        }
    }  
    
    public void setGroup(SynchroSchemes synchros[]) {
        JGroupSynchro group = new JGroupSynchro();
        
        for(int i=0; i<synchros.length; i++) {
            synchros[i].group = group;
        }
    }
    
    


    public boolean delaysOk(SynchroSchemes sss) {
	//System.out.println("Min delay = " +sss.minDelay + " Max delay = " + sss.maxDelay);
	if ((sss.minDelay == -1) && (sss.maxDelay == -1)) {
	    return true;
	}

	boolean minok, maxok;
	//long time = System.currentTimeMillis();

	if (sss.minDelay == -1) {
	    minok = true;
	} else {
	    if ((sss.startSynchroTime + sss.minDelay) <= time) {
		//System.out.println("Min ok pour " + sss.jgate.getName());
		minok = true;
	    } else {
		//System.out.println("Min pas ok pour " + sss.jgate.getName());
		minok = false;
	    }
	}

	if (sss.maxDelay == -1) {
	    maxok = true;
	} else {
	    if (time >= (sss.startSynchroTime + sss.maxDelay)) {
		maxok = false;
		//System.out.println("Max pas ok");
	    } else {
		maxok = true;
	    }
	}

	return minok && maxok;
    }


    
    public long getMaxTimeToWait(SynchroSchemes synchros[]) {
        long maxTimeToWait = 999999999;
        SynchroSchemes sss;

        //long time = System.currentTimeMillis();
        
        for(int i=0; i<synchros.length; i++) {
            sss = synchros[i];
            if (sss.minDelay != -1) {
		//System.out.println("Min=" + sss.minDelay + "starttime=" + sss.startSynchroTime + " time=" + time);
                if ((sss.startSynchroTime + sss.minDelay) > time) {
		    //System.out.println("Min not obtained");
                    maxTimeToWait = Math.min(maxTimeToWait, (sss.startSynchroTime + sss.minDelay) -time);
                }
            }
            
            if (sss.maxDelay != -1) {
                if (time < sss.startSynchroTime + sss.maxDelay) {
                    maxTimeToWait = Math.min(maxTimeToWait, (sss.startSynchroTime + sss.maxDelay)- time);
                }
            }
        }
        
	if (maxTimeToWait == 999999999) {
	    return -1;
	}
        return maxTimeToWait;
    }
   
}

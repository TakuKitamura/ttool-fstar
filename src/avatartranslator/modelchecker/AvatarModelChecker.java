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
   * Class AvatarModelChecker
   * Avatar Model Checker
   * Creation: 31/05/2016
   * @version 1.0 31/05/2016
   * @author Ludovic APVRILLE
   * @see
   */


package avatartranslator.modelchecker;

import java.util.*;

import myutil.*;

public class AvatarModelChecker extends Runnable {
    private final static int DEFAULT_NB_OF_THREADS = 4;
    private final static int SLEEP_DURATION = 500;

    private AvatarSpecification spec;
    private int nbOfThreads = DEFAULT_NB_OF_THREAD;
    private int nbOfCurrentComputations;


    // ReachabilityGraph
    private Map<Integer, SpecificationState> states;
    private List<SpecificationState> pendingStates;
    private List<SpecificationLink> links;

    public AvatarModelChecker(AvatarSpecification _spec) {
        spec = _spec;
    }

    public void startModelChecking() {
	// Remove timers, composite states, randoms
	spec.removeTimers();
	spec.removeCompositeStates();
	spec.removeRandoms();
	
        startModelChecking(DEFAULT_NB_OF_THREADS);
    }

    public void startModelChecking(int _nbOfThreads) {
        nbOfThreads = _nbOfThreads;

        // Init data stuctures
        states = Collections.synchronizedMap(new HashMap<Integer, SpecificationState>());
        pendingStates = Collections.synchronizedList(new LinkedList<SpecificationState>());
        links = Collections.synchronizedList(new ArrayList<SpecificationLink>());

        // Compute initial state
        SpecificationState initialState = new SpecificationState();
        initialState.setInit(spec);

        states.add(initialState);
        pendingStates.add(initialState);

        computeAllStates();

        // All done
    }

    private void computeAllStates() {
        int i;
        Thread []ts = new Thread[nbOfThreads];

        for(i=0; i<nbOfThreads; i++) {
            ts[i] = new Thread(this);
            ts[i].start();
        }

        for(i=0; i<nbOfThreads; i++) {
            ts[i].join();
        }
    }

    public void run() {
        SpecificationState s;

        boolean go = true;
        while(go) {
            // Pickup a state
            s = pickupState();

            if (s == null) {
                // Terminate
                go = false;
            } else {
                // Handle one given state
                computeAllStatesFrom(s);
                // Release the computation
                releasePickupState();
            }
        }
    }

    private synchronized SpecificationState pickupState() {
        int size = pendingStates.size();
        while (size == 0) {
            if (nbOfComputations == 0) {
                return null;
            } else {
                try {
                    wait(SLEEP_DURATION);
                } catch (Exception e) {}
            }
        }

        SpecificationState s = pendingStates.get(0);
        pendingStates.remove(0);
        nbOfComputations ++;
        return s;
    }

    private synchronized void releasePickupState() {
        nbOfCurrentComputations --;
        notifyAll();
    }

    private synchronized int getNbOfComputations() {
        return nbOfComputations;
    }


    private void computeAllStatesFrom(SpecificationState _ss) {
        // For each block, get the list of possible transactions

        // For each realizable transition
        //   Make it, reset clock of the involved blocks to 0, increase hmin/hmax of each block
        //   compute new state, and compare with existing ones
        //   If not a new state, create the link rom the previsou state to the new one
        //   Otherwise create the new state and its link, and add it to the pending list of states



    }

}

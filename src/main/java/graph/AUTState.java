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




package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
   * Class AUTState
   * Creation : 05/03/2008
   ** @version 1.0 05/03/2008
   * @author Ludovic APVRILLE
 */
public class AUTState implements Comparable<AUTState> {

	public String info;
    public int id;
    public ArrayList<AUTTransition> inTransitions; // Arriving to that state
    public ArrayList<AUTTransition> outTransitions; // Departing from that state
    public boolean met = false;
    public Object referenceObject;
    public boolean isOrigin = false;

    public AUTState(int _id) {
        id = _id;
        inTransitions = new ArrayList<AUTTransition>();
        outTransitions = new ArrayList<AUTTransition>();
    }

    public int compareTo( AUTState _s ) {
	return id - _s.id;
    }

    public void addInTransition(AUTTransition tr) {
        inTransitions.add(tr);
    }

    public void addOutTransition(AUTTransition tr) {
        outTransitions.add(tr);
    }

    public int getNbInTransitions() {
        return inTransitions.size();
    }

    public int getNbOutTransitions() {
        return outTransitions.size();
    }

    public boolean isTerminationState() {
	return (outTransitions.size() == 0);
    }

    public AUTTransition getTransitionTo(int destination) {
	for(AUTTransition tr: outTransitions) {
            if (tr.destination == destination) {
                return tr;
            }
        }
        return null;
    }

    public boolean hasTransitionTo(int destination) {
        for(AUTTransition aut1 : outTransitions) {
            if (aut1.destination == destination) {
                return true;
            }
        }
        return false;
    }

    public AUTTransition returnRandomTransition() {
        int size = outTransitions.size();
        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return outTransitions.get(0);
        }

        Random generator = new Random();
        int choice = generator.nextInt(size);
        return outTransitions.get(choice);

    }

    public void updateID(int _newID) {
	id = _newID;
	for(AUTTransition inT: inTransitions) {
	    inT.destination = id;
	}
	
	for(AUTTransition outT: outTransitions) {
	    outT.origin = id;
	}
    }

    public AUTTransition[] getAtLeastTwoOutTauTransitions() {
	if (outTransitions == null) {
	    return null;
	}

	int cpt = 0;
	AUTTransition[] trans = new AUTTransition[2];
	for(AUTTransition tr: outTransitions) {
	    if (tr.isTau) {
		trans[cpt] = tr;
		cpt ++;
		if (cpt == 2) {
		    return trans;
		}
	    }
	}
	return null;
    }

    public boolean hasOneIncomingTauAndOneFollower() {
	if (outTransitions.size() != 1) {
	    return false;
	}

	if (inTransitions.size() != 1) {
	    return false;
	}

	return inTransitions.get(0).isTau;
    }

    public boolean hasOutputTauTransition() {
	if (outTransitions.size() < 1) {
	    return false;
	}

	for(AUTTransition outT: outTransitions) {
	    if (outT.isTau) {
		return true;
	    }
	}

	return false;
    }

    public boolean hasSimilarTransition(AUTTransition _tr) {
	if (outTransitions.size() == 0) {
	    return false;
	}
	for(AUTTransition tr: outTransitions) {
	    if ((tr.origin == _tr.origin) && (tr.destination == _tr.destination) && (tr.transition.compareTo(_tr.transition) == 0)) {
		return true;
	    }
	}
	
	return false;
    }

    public void removeAllOutTauTransitions(ArrayList<AUTTransition> _transitions, ArrayList<AUTState> _states) {
	ArrayList<AUTTransition> outTransitions2 = new ArrayList<AUTTransition>();
	for(AUTTransition tr: outTransitions) {
	    if (!(tr.isTau)) {
		outTransitions2.add(tr);
	    } else {
		_transitions.remove(tr);
		_states.get(tr.destination).removeInTransition(tr);
	    }
	}
	outTransitions = outTransitions2;
    }

    public void removeInTransition(AUTTransition _tr) {
	inTransitions.remove(_tr);
    }


    public void createTransitionsButNotDuplicate(LinkedList<AUTTransition> _newTransitions, ArrayList<AUTState> _states, ArrayList<AUTTransition> _transitions) {
	for(AUTTransition tr: _newTransitions) {
	    if (!(hasSimilarTransition(tr))) {
		AUTTransition ntr = tr.basicClone();
		ntr.origin = id;
		outTransitions.add(ntr);
		_states.get(ntr.destination).addInTransition(ntr);
		_transitions.add(ntr);
	    }
	}
    }

    public void removeAllTransitionsWithId(int id) {
	ArrayList<AUTTransition> toBeRemoved = new 	ArrayList<AUTTransition>();
	for(AUTTransition tr: outTransitions) {
	    if (tr.destination == id) {
		toBeRemoved.add(tr);
	    }
	}
	for(AUTTransition tr: toBeRemoved) {
	    outTransitions.remove(tr);
	}
	toBeRemoved.clear();
	
	for(AUTTransition tr: inTransitions) {
	    if (tr.origin == id) {
		toBeRemoved.add(tr);
	    }
	}
	for(AUTTransition tr: toBeRemoved) {
	    inTransitions.remove(tr);
	}
    }

    public String toString() {
	String s = "" + id + "\n";
	for(AUTTransition tr: inTransitions) {
	    s += "\t in: " + tr.toString() + "\n";
	}
	for(AUTTransition tr: outTransitions) {
	    s += "\tout: " + tr.toString() + "\n";
	}
	return s;
    }


}

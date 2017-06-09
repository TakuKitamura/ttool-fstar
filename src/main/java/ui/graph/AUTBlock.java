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
   * Class AUTBlock
   * Creation : 06/01/2017
   ** @version 1.0 06/01/2017
   * @author Ludovic APVRILLE
   * @see
   */

package ui.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AUTBlock  implements Comparable<AUTBlock> {


    public ArrayList<AUTState> states; // Arriving to that state
    public int hashValue;
    public boolean hashComputed;

    public AUTBlock() {
        states = new ArrayList<AUTState>();
    }

    public void addState(AUTState _st) {
        states.add(_st);
    }


    public String toString() {
        boolean first = true;
        StringBuffer sb = new StringBuffer("");
        for(AUTState state: states) {
            if (!first) {
                sb.append("," + state.id);
            } else {
                sb.append(state.id);
                first = false;
            }
        }
        return sb.toString();
    }

    public boolean hasInTransitionWith(AUTElement _elt) {
        for(AUTState st: states) {
            for(AUTTransition tr: st.inTransitions) {
                if (tr.elt == _elt) {
                    return true;
                }
            }
        }
        return false;
    }


    public AUTBlock getMinus1(AUTElement _elt, ArrayList<AUTState> _states) {
        AUTBlock b = new AUTBlock();
        for(AUTState st: states) {
            //TraceManager.addDev("Considering state" + st);
            for(AUTTransition tr: st.inTransitions) {
                //TraceManager.addDev("Considering transition:" + tr + " with elt " + tr.elt +  ". Is it equal to " + _elt + "?");
                if (tr.elt == _elt) {
                    AUTState tmp = _states.get(tr.origin);
                    //TraceManager.addDev("Yes! Found state for minus-1=" + tmp);
                    if (!(b.states.contains(tmp))) {
                        b.states.add(tmp);
                    }
                } else {
                    //TraceManager.addDev("No...");
                }
            }
        }
        return b;
    }

    public AUTBlock getStateIntersectWith(AUTBlock _b) {
        AUTBlock b = new AUTBlock();
        for(AUTState st: states) {
            if (_b.states.contains(st)) {
                b.addState(st);
            }
        }
        return b;
    }

    public AUTBlock getStateDifferenceWith(AUTBlock _b) {
        AUTBlock b = new AUTBlock();
        for(AUTState st: states) {
            if (!(_b.states.contains(st))) {
                b.addState(st);
            }
        }
        return b;
    }


    public boolean hasStateOf(AUTBlock _b) {
        for(AUTState st: states) {
            if (_b.states.contains(st)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return states.size();
    }

    public boolean isEmpty() {
        return (states.size() == 0);
    }

    public void computeHash() {
        Collections.sort(states);
        int[] hash = new int[states.size()];
        int cpt = 0;
        for(int i=0; i<hash.length; i++) {
            hash[i] = states.get(i).id;
        }
        hashValue = Arrays.hashCode(hash);
        hashComputed = true;
    }

    public int compareTo( AUTBlock _b ) {
        if (!hashComputed) {
            computeHash();
        }
        if (!_b.hashComputed) {
            _b.computeHash();
        }
        return (hashValue - _b.hashValue);
    }

    public boolean hasState(int _id) {
        for(AUTState st: states) {
            if (st.id == _id) {
                return true;
            }
        }
        return false;
    }


    public boolean leadsTo(AUTBlock _b, AUTElement _elt) {
        for(AUTState st: states) {
            for(AUTTransition tr: st.outTransitions) {
                if (tr.elt == _elt) {
                    if (_b.hasState(tr.destination)) {
                        //TraceManager.addDev("Transition from block " + _b + " from state " + st + " to state " + tr.destination +  " with elt=" + _elt);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static AUTBlock concat(AUTBlock b1, AUTBlock b2) {
	AUTBlock ret = new AUTBlock();
	for(AUTState st1: b1.states) {
	    ret.addState(st1);
	}
	for(AUTState st2: b2.states) {
	    ret.addState(st2);
	}
	return ret;
    }

}

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
* Class AUTMappingGraph
* Creation : 05/03/2008
** @version 1.0 05/03/2008
* @author Ludovic APVRILLE
* @see
*/

package ui.graph;

import java.awt.*;
import java.util.*;
import java.io.*;

import myutil.*;

public class AUTMappingGraph  extends AUTGraph {
	
	public AUTMappingGraph() {
	}
    
	public void renameTransitions() {
		String tmp, tmp1;
		int index, index1;
		
		for(AUTTransition aut1 : transitions) {
			tmp = aut1.transition;
			// we remove three "__" (name of HwNode, name of Design, name of task)
			index = tmp.indexOf("__");
			
			if (index != -1) {
				tmp = tmp.substring(index+2, tmp.length());
				index = tmp.indexOf("__");
				if (index != -1) {
					tmp = tmp.substring(index+2, tmp.length());
					tmp1 = tmp;
					index = tmp.indexOf("__");
					if (index != -1) {
						tmp = tmp.substring(index+2, tmp.length());
						if (tmp.startsWith("wr__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "wr" + tmp.substring(index, tmp.length());
							}
						}
						
						if (tmp.startsWith("rd__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "rd" + tmp.substring(index, tmp.length());
							}
						}
						
						if (tmp.startsWith("wait__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "wait" + tmp.substring(index, tmp.length());
							}
						}
						
						if (tmp.startsWith("notify__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "notify" + tmp.substring(index, tmp.length());
							}
						}
						
						if (tmp.startsWith("notified__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "notified" + tmp.substring(index, tmp.length());
							}
						}
						
						if (tmp.startsWith("sendReq__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								// Must add the name of the task
								tmp = tmp.substring(index, tmp.length());
								index1 = tmp1.indexOf("__");
								tmp1 = tmp1.substring(0, index1);
								if ((index = tmp.indexOf('<')) > -1) {
									tmp = tmp.substring(0, index) + "__" + tmp1 + tmp.substring(index, tmp.length());
								} else {
									tmp = tmp + "__" + tmp1;
								}
								aut1.transition = "sendReq" + tmp;
							}
						}
						
						if (tmp.startsWith("waitReq__")) {
							index = tmp.lastIndexOf("__");
							if ((index != -1) && (index > 3)) {
								aut1.transition = "waitReq" + tmp.substring(index, tmp.length());
							}
						}
					}
					
				}
			}
			
        }
	}
	
	public void mergeReadTransitions() {
		mergeTransitions("rd__");
	}
	
	public void mergeWriteTransitions() {
		mergeTransitions("wr__");
	}
	
	// Transitions to be removed are named "i"
	public void mergeTransitions(String keyword) {
		if (states == null) {
			computeStates();
		}
		
		reinitMet();
		
		// ExploringPaths, starting from state 0
		mergeTransitionsInPath(states.get(0), keyword);
	}
	
	public void mergeTransitionsInPath(AUTState state, String keyword) {
		//System.out.println("in state: " + state.id);
		AUTState state1;
		AUTTransition autnext;
		
		if (state.met) {
			return;
		}
		
		state.met = true;
		for(AUTTransition aut1: state.outTransitions) {
			state1 = states.get(aut1.destination);
			if((state1.getNbOutTransitions() == 1) && (state1.getNbInTransitions() ==1)) {
				autnext = state1.outTransitions.get(0);
				mergeTheTwoTransitions(aut1, autnext, keyword);
			}
			mergeTransitionsInPathIterative(state1, keyword);
		}
	}
	
	public void mergeTransitionsInPathIterative(AUTState state, String keyword) {
		ArrayList<AUTState> list = new ArrayList<AUTState>();
		list.add(state);
		AUTState mystate;
		AUTState state1;
		AUTTransition autnext;
		
		while (list.size()>0) {
			mystate = list.get(0);
			list.remove(0);
			mystate.met = true;
			for(AUTTransition aut1: mystate.outTransitions) {
				state1 = states.get(aut1.destination);
				if((state1.getNbOutTransitions() == 1) && (state1.getNbInTransitions() ==1)) {
					autnext = state1.outTransitions.get(0);
					mergeTheTwoTransitions(aut1, autnext, keyword);
				}
				if (!state1.met) {
					list.add(state1);
				}
			}
		}
	}
	
	public void mergeTheTwoTransitions(AUTTransition aut1, AUTTransition aut2, String keyword) {
		int index1 = aut1.transition.indexOf(keyword);
		if (index1 == -1) {
			return;
		}
		
		int index2 = aut2.transition.indexOf(keyword);
		if (index2 == -1) {
			return;
		}
		
		System.out.println("Found two transitions to merge: " + aut1.transition + " and " + aut2.transition);
		
		// Must check that this is the same channel
		String tmp1 = aut1.transition.substring(index1 + keyword.length(), aut1.transition.length());
		index1 = tmp1.indexOf('<');
		if (index1 == -1) {
			return;
		}
		tmp1 = tmp1.substring(0, index1);
		
		String tmp2 = aut2.transition.substring(index2 + keyword.length(), aut2.transition.length());
		index2 = tmp2.indexOf('<');
		if (index2 == -1) {
			return;
		}
		tmp2 = tmp2.substring(0, index2);
		
		//System.out.println("The two channel names are:" + tmp1 + " and " + tmp2);
		
		if (!tmp1.equals(tmp2)) {
			return;
		}
		
		// So, we have the same channels
		// Get the values
		
		Point p1 = getValues(aut1.transition);
		if ((p1.x == -1) || (p1.y == -1)) {
			return;
		}
		
		Point p2 = getValues(aut2.transition);
		if ((p2.x == -1) || (p2.y == -1)) {
			return;
		}
		
		if (p2.y != p1.y) {
			return;
		}
		
		aut1.transition = "i";
		tmp2 = aut2.transition.substring(0, aut2.transition.indexOf('<'));
		tmp2 += "<" + (p1.x + p2.x) + ", " + p2.y + ">";
		aut2.transition = tmp2;
		
		//System.out.println("The second transition is now:" + aut2.transition);
		
	}
	
	public Point getValues(String values) {
		Point p = new Point();
		
		int index1 = values.indexOf('<');
		int index2 = values.indexOf(',');
		int index3 = values.indexOf('>');
		int value1 = -1;
		int value2 = -1;
		
		if ((index1 > -1) && (index2 > index1) && (index3 > index2)) {
			try {
				value1 = Integer.decode(values.substring(index1+1, index2).trim()).intValue();
				value2 = Integer.decode(values.substring(index2+1, index3).trim()).intValue();
			} catch (NumberFormatException nfe) {
				value1 = -1;
				value2 = -1;
			}
			
		}
		
		p.x = value1;
		p.y = value2;
		
		return p;
		
	}
	
	
	public void splitTransitions() {
		String tmp;
		int index1, index2;
		ArrayList<AUTTransition> tmptransitions = new ArrayList<AUTTransition>();
		
		for(AUTTransition aut1 : transitions) {
			tmp = aut1.transition;
			index1 = tmp.indexOf('<');
			if (index1 > -1) {
				index2 = tmp.indexOf('>');
				if (index2 > index1) {
					splitTransition(tmptransitions, aut1, index1, index2);
				}
			}
		}
		
		for(AUTTransition aut2 : tmptransitions) {
			transitions.add(aut2);
		}
	}
	
	public boolean splitTransition(ArrayList<AUTTransition> tmptransitions, AUTTransition transition, int index1, int index2) {
		Point p;
		int value;
		int destination = transition.destination;
		//String tmp = transition.transition.substring(index1+1, index2);
		String newTransition;
		
		if (!transition.transition.startsWith("wr__") && !transition.transition.startsWith("rd__"))  {
			return false;
		}
		
		p = getValues(transition.transition);
		
		//System.out.println("p.x=" + p.x +  " p.y=" + p.y);
		
		if ((p.x == -1) || (p.y == -1)) {
			return false;
		}
		
		if (p.x == p.y) {
			// Just need to transform the transition i.e. remove the <x, y>
			transition.transition = transition.transition.substring(0, index1);
			return true;
		}
		
		if (p.x < p.y) {
			// Nothing to be done...
			return true;
		}
	
		// And so, p.x > p.y
		newTransition = transition.transition.substring(0, index1);
		transition.transition = newTransition;
		
		//System.out.println("new Transition =" + newTransition);
		
		transition.destination = nbState;
		AUTTransition tr = null;
		
		value = (p.x / p.y) - 1;
		if ((p.x % p.y) != 0) {
			value ++;
		}
		
		//System.out.println("value" + value);
		
		for(int i=0; i<value; i++) {
			if ((i == (value-1)) && ((p.x % p.y) != 0)) {
				tr = new AUTTransition(nbState + i, newTransition + "<" + (p.x % p.y) + ", " + p.y + ">", nbState + i + 1);
			} else {
				tr = new AUTTransition(nbState + i, newTransition, nbState + i + 1);
			}
			tmptransitions.add(tr);
		}
		tr.destination = destination;
		nbState = nbState + value;
		nbTransition = nbTransition + value;
		
		return true;
	}
	
	public void removeInternalTransitions() {
		AUTTransition transition;
		ArrayList<AUTState> tmpStates = new ArrayList<AUTState>();
		AUTState deststate;
		
		if (states == null) {
			computeStates();
		}
		
		for(AUTState state: states) {
			if (state.getNbOutTransitions() == 1) {
				transition = state.outTransitions.get(0);
				deststate = states.get(transition.destination);
				if (transition.transition.equals("i")) {
					for(AUTTransition aut1: state.inTransitions) {
						aut1.destination = transition.destination;
						deststate.inTransitions.add(aut1);
					}
					transitions.remove(transition);
					tmpStates.add(state);
					nbTransition = nbTransition -1;
					nbState = nbState - 1;
				}
			}
		}
		
		for(AUTState state: tmpStates) {
			states.remove(state);
		}
		
		if (tmpStates.size() > 0) {
			updateStateIds();
		}
	}
	
	public void updateStateIds() {
		AUTState state;
		
		for(int i=0; i<states.size(); i++) {
			state = states.get(i);
			state.id = i;
			for(AUTTransition aut1: state.inTransitions) {
				aut1.destination = i;
			}
			for(AUTTransition aut1: state.outTransitions) {
				aut1.origin = i;
			}
		}
	}
	
    
}
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
* Class AUTGraph
* Creation : 16/09/2004
** @version 1.0 16/09/2004
* @author Ludovic APVRILLE
* @see
*/

package ui.graph;

import java.util.*;
import java.io.*;

import myutil.*;

public class AUTGraph  implements myutil.Graph {
    
    protected ArrayList<AUTTransition> transitions;
	protected ArrayList<AUTState> states;
    protected int nbState;
    protected BufferedReader br;
    protected long nbTransition;
    protected int percentage;
    protected boolean[] hasExitTransition;
    protected boolean[] hasEntryTransition;
	protected boolean statesComputed;
    
    public AUTGraph() {
        transitions = new ArrayList<AUTTransition>();
        //buildGraph(dataAUT);
    }
    
    public void stopBuildGraph() {
    	br = null;
    }
    
    public int getPercentage() {
    	return percentage;
    }
    
    public void buildGraph(String data) {
        
        StringReader sr = new StringReader(data);
        br = new BufferedReader(sr);
        String s, s1, s2;
        int index1;
        //int origin, destination;
        AUTTransition at;
        
        percentage = 0;
        int cpt, mod;
        
        /* read header */
        //System.out.println("Building graph");
        try {
            while((s = br.readLine()) != null) {
                index1 = s.indexOf("des");
                //System.out.println("Searching for des");
                if (index1 == 0) {
                    //System.out.println("des found");
                    s1 = s.substring(s.indexOf(',') + 1, s.length());
                    s1 = s1.substring(0, s1.indexOf(','));
                    s1 = Conversion.removeFirstSpaces(s1);
                    nbTransition = new Integer(s1).intValue();
                    s2 = s.substring(s.indexOf(",") + 1, s.indexOf(')'));
                    s2 = s2.substring(s2.indexOf(",") + 1, s2.length());
                    s2 = Conversion.removeFirstSpaces(s2);
                    nbState = new Integer(s2).intValue();
                    break;
                }
            }
        } catch (Exception e) {
			System.out.println("Exception when reading graph information: " + e.getMessage());
        	return;
        }
        
        String[] array;
        hasExitTransition = new boolean[nbState];
        hasEntryTransition = new boolean[nbState];
		
		System.out.println("NbState=" + nbState + "  NbTransition=" + nbTransition);
        
        /*for(cpt=0; cpt<nbState; cpt ++) {
        	hasExitTransition[cpt] = false;
        	hasEntryTransition[cpt] = false;
        }*/
        
        /* read transitions */
        try {
        	cpt = 0;
        	mod = Math.max(1, (int)(nbTransition / 100));
			
        	
            while((s = br.readLine()) != null) {
				//System.out.println("realine:" + s);
                array = AUTGraph.decodeLine(s);
                at = new AUTTransition(array[0], array[1], array[2]);
                transitions.add(at);
                hasExitTransition[at.origin] = true;
                hasEntryTransition[at.destination] = true;
                cpt ++;
                if ((cpt % mod) == 0) {
                	percentage = (int)((cpt *100) / nbTransition);
                	//System.out.println("percentage=" + percentage + "cpt=" + cpt + "nbTransition=" + nbTransition);
                }
            }
        } catch (Exception e) {
        	System.out.println("Cancelled: " + e.getMessage());
        	return;
        }
    }
    
    public static String[] decodeLine(String s) {
        int index1, index2;
        String s1, s2, s3;
        
        index1 = s.indexOf("(");
			index2 = s.indexOf(",");
			s1 = s.substring(index1+1, index2);
			s = s.substring(index2 +1, s.length());
			s = Conversion.removeFirstSpaces(s);
			
			// for of the action
			// , action,
			// "i(action<1,2,4>)",
			// "action<1,2,4>",
			
			// guillemets ?
			index1 = s.indexOf("\"");
			if (index1 > -1) {
				//System.out.println("Guillemets on " + s);
				s2 = s.substring(index1+1, s.length());
				s2 = s2.substring(0, s2.indexOf("\""));
				//System.out.println("Guillemets on " + s2);
				index2 = s2.indexOf("(");
					if (index2 > -1) {
						s2 = s2.substring(index2+1, s2.indexOf(")"));
					}
					//System.out.println("Guillemets on " + s2);
					
			} else {
				//System.out.println("No Guillemets on " + s);
				index1 = s.indexOf(",");
				if ((index2 = s.indexOf("(")) >= 0) {
						s2 = s.substring(index2+1, index1-2);
				} else {
					if ((index2 = s.indexOf("\"t\"")) >= 0) {
						s2 = "t";
					} else {
						s2 = s.substring(0, index1);
					}
				}
			}
			
			s = s.substring(s.indexOf(s2) + s2.length(), s.length());
			//System.out.println("s=" + s);
			index1 = s.indexOf(",");
			//index2 = s.indexOf(")");
			//s2 = s.substring(0, index1-1);
			s3 = s.substring(index1+1, s.length()-1);
			s3 = Conversion.removeFirstSpaces(s3);
			//System.out.println("s1=" + s1 + " s2=" + s2 + " s3=" + s3);
			
			String []array = new String[3];
			array[0] = s1;
			array[1] = s2;
			array[2] = s3;
			return array;
    }
    
    public int getNbState() {
        return nbState;
    }
    
    public int getNbTransition() {
        //return nbTransition;
        return transitions.size();
    }
    
    public AUTTransition getAUTTransition(int index) {
        return transitions.get(index);
    }
    
    public int getNbPotentialDeadlocks(){
        int nb = 0;
        
        for(int i=0; i<nbState; i++) {
            if (hasEntryTransition(i)) {
                if (!hasExitTransition(i)) {
                    nb ++;
                }
            }
        }
        
        return nb;
    }
    
    public String getActionTransition(int origin, int destination) {
        
		for(AUTTransition aut1 : transitions) {
            if ((aut1.origin == origin) &&  (aut1.destination == destination)){
                return aut1.transition;
            }
        }
        return "";
    }
    
    public boolean hasEntryTransition(int state) {
    	return hasEntryTransition[state];
    }
    
    public boolean hasExitTransition(int state) {
    	return hasExitTransition[state];
    }
    
    public boolean hasExitTransitionTo(int state, int destination) {
		if (!hasExitTransition(state)) {
			return false;
		}
		
        
		for(AUTTransition aut1 : transitions) {
            if ((aut1.origin == state) && (aut1.destination == destination)){
                return true;
            }
        }
        return false;
        
    }
    
    /* State numbers are return under the form of int */
    /* Should be rewritten: not of high performance at all */
	
    public int[] getVectorPotentialDeadlocks() {
        int nbPotentialDeadlock = getNbPotentialDeadlocks();
        //System.out.println("nb of deadlocks: " + nbPotentialDeadlock);
        int[] states = new int[nbPotentialDeadlock];
        int index = 0;
        
        for(int i=0; i<nbState; i++) {
            if (hasEntryTransition(i)) {
                if (!hasExitTransition(i)) {
                    states[index] = i;
                    index ++;
                }
            }
        }
        
        return states;
    }
    
    public int [] shortestPathTo(int fromState, int targetState) {
        return GraphAlgorithms.ShortestPathFrom(this, fromState)[targetState].path;
    }
    
    
    public boolean hasTransitionWithAction(String action) {
        
       for(AUTTransition aut1 : transitions) {
            if (aut1.transition.compareTo(action) == 0){
                return true;
            }
        }
        return false;
    }
    
    // For Graph interface
    
    public int getWeightOfTransition(int originState, int destinationState) {
		if (statesComputed) {
			if (states.get(originState).hasTransitionTo(destinationState)) {
				return 1;
			}
		} else {
			if (hasExitTransitionTo(originState, destinationState)) {
				return 1;
			}
		}
        return 0;
    }
	
	public String toAUTStringFormat() {
		StringBuffer graph = new StringBuffer("");
		graph.append("des(0," +  nbTransition + "," + nbState + ")\n");
		for(AUTTransition aut1 : transitions) {
            graph.append("(" + aut1.origin + ",\"" + aut1.transition + "\"," + aut1.destination + ")\n");
        }
        return graph.toString();
	}
	
	public void computeStates() {
		states = new ArrayList<AUTState>(nbState);
		AUTState state;
		for(int i=0; i<nbState; i++) {
			state = new AUTState(i);
			states.add(state);
		}
		
		for(AUTTransition aut1 : transitions) {
            states.get(aut1.origin).addOutTransition(aut1);
			states.get(aut1.destination).addInTransition(aut1);
        }	
		statesComputed = true;
	}
	
	public void reinitMet() {
		for(AUTState state: states) {
			state.met = false;
		}
	}
	
	public AUTState findFirstOriginState() {
		AUTState state;
		
		for(int i=0; i<states.size(); i++) {
			state = states.get(i);
			//System.out.println("id=" + state.id + " transitions to me = " +state.inTransitions.size()); 
			if (state.inTransitions.size() == 0) {
				return state;
			}
		}
		
		return null;
	}
	
	public void putTransitionsFromInitFirst() {
		ArrayList<AUTTransition> tmp = new ArrayList<AUTTransition>();
		
		for(AUTTransition aut1 : transitions) {
			if (aut1.origin == 0) {
				tmp.add(aut1);
			}
		}
		
		for(AUTTransition aut2 : tmp) {
			transitions.remove(aut2);
			transitions.add(0, aut2);
		}
	}
    
}
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
 * Class GraphAlgorithms
 *
 * Creation: 16/09/2004
 * @version 1.0 16/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package myutil;

import java.util.*;


public class GraphAlgorithms {
	public static boolean go = true;
	
    
    // Assume that states are numbered from 0 to nbState - 1
	
	public static boolean hasCycle(Graph g){
		int nbState = g.getNbState();
		int i,j;
		
        if (nbState == 0) {
            return false;
        }
		
        System.out.println("cycle0? Nb state = " + nbState);
        DijkstraState[] states = new DijkstraState[nbState];
		
		for(i=0; i<nbState; i++) {
            states[i] = new DijkstraState(i);
            states[i].previous = -1;
            states[i].used = false;
            states[i].weight = 0;
        }
		
		if (go == false) {
			return false;
		}
		
		System.out.println("cycle1? Nb state = " + nbState);
		// Succ
		int succ;
		for(i=0; i<nbState; i++) {
			/*if ((i % 100) == 0) {
				System.out.println("i=" + i);
			}*/
            for(j=0; j<nbState; j++) {
				succ = g.getWeightOfTransition(i, j);
				if (succ > 0) {
					 states[j].weight ++;
				}
			}
        }
		
		if (go == false) {
			return false;
		}
		
		int nb = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		System.out.println("cycle2? Nb state = " + nbState);
		for(i=0; i<nbState; i++) {
            if (states[i].weight == 0) {
				list.add(new Integer(i));
				nb ++;
			}
        }
		
		System.out.println("cycle3? Nb state = " + nbState +  " nb=" + nb);
		int index;
		while((list.size() > 0) && (go == true)){
			index = list.get(0).intValue();
			list.remove(0);
			for(j=0; j<nbState; j++) {
				succ = g.getWeightOfTransition(index, j);
				if (succ > 0) {
					 states[j].weight --;
					 if (states[j].weight == 0) {
						 list.add(new Integer(j));
						 nb ++;
					 }
				}
			}
		}
		
		System.out.println("cycle4? Nb state = " + nbState +  " nb=" + nb);
		
		return !(nb == nbState);
		
	}
	
	
	
    public static DijkstraState[] ShortestPathFrom(Graph g, int startState){
		
        int nbState = g.getNbState();
        if (nbState == 0) {
            return null;
        }
        //System.out.println("Nb state = " + nbState + " startState = " + startState);
        DijkstraState[] states = new DijkstraState[nbState];
        int i;
        
        /* init of states */
        for(i=0; i<nbState; i++) {
            states[i] = new DijkstraState(i);
            states[i].previous = -1;
            states[i].used = false;
            states[i].weight = Integer.MAX_VALUE;
        }
        
        states[startState].used = false;
        states[startState].weight = 0;
        states[startState].previous = -1;
        
		if (go == true) {
			iterativeShortestPathFrom(g, states, startState);
		}
		

		
		if (go == true) {
			computePaths(states, startState);
		}
        
		
		if (go == false) {
			//System.out.println("Algorithm stopped");
			return null;
		}
        return states;
    }
    
    public static void iterativeShortestPathFrom(Graph g, DijkstraState[] states, int startState) {
        //DijkstraState ds;
        int weight;
        //int[] path;
        int currentState;
        
        while (((currentState = getNextStateToCompute(states, startState)) != -1) && (go == true)) {
            //System.out.println("State managed:" + currentState);
            states[currentState].used = true;
            for(int i=0; (i<states.length) && (go == true); i++) {
                weight = g.getWeightOfTransition(currentState, i);
                // adjacent state
                if (weight > 0) {
                    if (!states[i].used) {
                        if((states[currentState].weight + weight) < states[i].weight) {
                            states[i].weight = states[currentState].weight + weight;
                            states[i].previous = currentState;
                        }
                    }
                }
            }
			if (go == false) {
				return;
			}
        }
    }
    
    private static int getNextStateToCompute(DijkstraState[] states, int startState) {
        int minWeight = Integer.MAX_VALUE;
        int state = -1;
        for(int i=0; i<states.length; i++) {
			if (go == false) {
				return 0;
			}
            if ((!states[i].used) && (states[i].weight <= minWeight)) {
                state = i;
                minWeight = states[i].weight;
                //System.out.println("MinWeight = " + minWeight + " state=" + i);
            }
        }
        return state;
    }
	
	// Assumes no cycle!
	 public static DijkstraState[] LongestPathFrom(Graph g, int startState){
		
		//System.out.println("Cycle detection");
		/*boolean result = hasCycle(g);
		if (result) {
			System.out.println("The graph contains cycles");
			return null;
		} else {
			System.out.println("The graph has no cycle");
		}*/
		
		 
        int nbState = g.getNbState();
        if (nbState == 0) {
            return null;
        }
        //System.out.println("Longest path Nb state = " + nbState + " startState = " + startState);
        DijkstraState[] states = new DijkstraState[nbState];
        int i;
        
        /* init of states */
        for(i=0; i<nbState; i++) {
            states[i] = new DijkstraState(i);
            states[i].previous = -1;
            states[i].used = false;
            states[i].weight = -1;
        }
        
        states[startState].used = false;
        states[startState].weight = 0;
        states[startState].previous = -1;
        
		if (go == true) {
			iterativeLongestPathFrom(g, states, startState);
		}
		

		
		if (go == true) {
			computePaths(states, startState);
		}
        
		
		if (go == false) {
			//System.out.println("Algorithm stopped");
			return null;
		}
        return states;
    }
    
    public static void iterativeLongestPathFrom(Graph g, DijkstraState[] states, int startState) {
        //DijkstraState ds;
        int weight;
        //int[] path;
        int currentState;
		int tmp;
        
        while (((currentState = getNextLongestStateToCompute(states, startState)) != -1) && (go == true)) {
            //System.out.println("State managed:" + currentState + " weight=" + states[currentState].weight);
            states[currentState].used = true;
            for(int i=0; (i<states.length) && (go == true); i++) {
                weight = g.getWeightOfTransition(currentState, i);
                // adjacent state
                if (weight > 0) {
                    //if (!states[i].used) {
                        if(states[currentState].weight + weight > states[i].weight) {
							tmp =  states[i].weight;
							//if (!states[i].used) {
								states[i].weight = states[currentState].weight + weight;
								states[i].previous = currentState;
								states[i].used = false;
								//System.out.println("Changing weight of state " + i + " former=" +  tmp + " new=" +states[i].weight);
							//}
                        }
                    //}
                }
            }
			if (go == false) {
				return;
			}
        }
    }
    
    private static int getNextLongestStateToCompute(DijkstraState[] states, int startState) {
        int maxWeight = -2;
        int state = -1;
        for(int i=0; i<states.length; i++) {
			if (go == false) {
				return 0;
			}
            if ((!states[i].used) && (states[i].weight > maxWeight)) {
                state = i;
                maxWeight = states[i].weight;
                //System.out.println("MaxWeight = " + maxWeight + " state=" + i);
            }
        }
        return state;
    }
    
    private static void computePaths(DijkstraState[] states, int startState) {
        int size;
        int [] path;
        
        for(int i=0; (i<states.length) && (go == true); i++) {
            size = computePathLength(states, i, startState);
            //System.out.println("i = " + i + " size=" + size);
            if (size ==0) {
                states[i].path = new int[0];
            } else {
                path = new int[size+1];
                states[i].path = path;
                makePath(states, i, path, path.length - 1);
            }
            
        }
    }
    
    private static int computePathLength(DijkstraState[] states, int state, int startState) {
        //System.out.println("Compute path, state = " + state);
		if (go == false) {
			return 0;
		}
		
        if (state == startState) {
            return 0;
        }
        if (states[state].previous == -1) {
            return -1;
        } else {
            int ret = computePathLength(states, states[state].previous, startState);
            if (ret == -1)
                return -1;
            else
                return 1 + ret;
        }
    }
    
    private static void makePath(DijkstraState[] states, int state, int [] path, int index) {
        while(index>=0) {
            path[index] = state;
            index --;
            state = states[state].previous;
        }
    }
    
}

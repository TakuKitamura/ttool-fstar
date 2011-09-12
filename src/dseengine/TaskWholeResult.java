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
* Class TaskWholeResult
* Object for storing a whole task result after a simulation
* Creation: 08/09/2011
* @version 1.0 08/09/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;


import myutil.*;


//import uppaaldesc.*;

public class TaskWholeResult  {
	public int id;
	public String name;
	
	public double minNbOfExecutedCycles;
	public double maxNbOfExecutedCycles;
	public double averageNbOfExecutedCycles;
	public int nbOfResults;
	
	public int nbOfRunningStates;
	public int nbOfRunnableStates;
	public int nbOfSuspendedStates;
	public int nbOfTerminatedStates;
	
	public TaskWholeResult(TaskResult taskres) {
		
		id = taskres.id;
		name = taskres.name;
		minNbOfExecutedCycles = taskres.nbOfExecutedCycles;
		maxNbOfExecutedCycles = taskres.nbOfExecutedCycles;
		averageNbOfExecutedCycles = taskres.nbOfExecutedCycles;
		nbOfResults = 1;
		
		if (taskres.state.toLowerCase().compareTo("runnable") == 0) {
			nbOfRunnableStates = 1 ;
		} else {
			nbOfRunnableStates = 0 ;
		}
		
		if (taskres.state.toLowerCase().compareTo("running") == 0) {
			nbOfRunningStates = 1 ;
		} else {
			nbOfRunningStates = 0 ;
		}
		
		if (taskres.state.toLowerCase().compareTo("suspended") == 0) {
			nbOfSuspendedStates = 1 ;
		} else {
			nbOfSuspendedStates = 0 ;
		}
		
		if (taskres.state.toLowerCase().compareTo("terminated") == 0) {
			nbOfTerminatedStates = 1 ;
		} else {
			nbOfTerminatedStates = 0 ;
		}
	}
	
	public void updateResults(TaskResult restask) {
		minNbOfExecutedCycles = Math.min(minNbOfExecutedCycles, restask.nbOfExecutedCycles);
		maxNbOfExecutedCycles = Math.max(maxNbOfExecutedCycles, restask.nbOfExecutedCycles);
		averageNbOfExecutedCycles = ((averageNbOfExecutedCycles *  nbOfResults)+restask.nbOfExecutedCycles)/(nbOfResults + 1);
		
		if (restask.state.toLowerCase().compareTo("running") == 0) {
			nbOfRunningStates ++ ;
		}
		if (restask.state.toLowerCase().compareTo("runnable") == 0) {
			nbOfRunnableStates ++ ;
		}
		if (restask.state.toLowerCase().compareTo("suspended") == 0) {
			nbOfSuspendedStates ++ ;
		}
		if (restask.state.toLowerCase().compareTo("terminated") == 0) {
			nbOfRunnableStates ++ ;
		}
		nbOfResults ++;
	}


	public String toStringResult() {
		StringBuffer sb = new StringBuffer("");
		sb.append("TASK " + id + " " + name + " " + nbOfResults + " " + minNbOfExecutedCycles + " " + averageNbOfExecutedCycles + " " + maxNbOfExecutedCycles + " " + nbOfRunnableStates + " " + nbOfRunningStates +" " + nbOfSuspendedStates + " " + nbOfTerminatedStates);
		
		return sb.toString();
	}	
	
	
	
} // Class BusResult


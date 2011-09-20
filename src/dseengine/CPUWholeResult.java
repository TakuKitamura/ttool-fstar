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
* Class CPUWholeResult
* Object for storing all CPU simulation results
* Creation: 07/09/2011
* @version 1.0 07/09/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;


import myutil.*;


//import uppaaldesc.*;

public class CPUWholeResult  {
	public int id;
	public String name;
	
	public double minUtilization;
	public double maxUtilization;
	public double averageUtilization;
	public int nbOfResults;
	
	public Vector<BusContentionWholeResult> contentions;
	Hashtable contentionTable = new Hashtable();
	
	public CPUWholeResult(CPUResult rescpu) {
		contentionTable = new Hashtable();
		
		id = rescpu.id;
		name = rescpu.name;
		minUtilization = rescpu.utilization;
		maxUtilization = rescpu.utilization;
		averageUtilization = rescpu.utilization;
		nbOfResults = 1;
		
		workOnContentions(rescpu);
	}
	
	public void updateResults(CPUResult rescpu) {
		minUtilization = Math.min(minUtilization, rescpu.utilization);
		maxUtilization = Math.max(maxUtilization, rescpu.utilization);
		averageUtilization = ((averageUtilization *  nbOfResults)+rescpu.utilization)/(nbOfResults + 1);
		nbOfResults ++;
		workOnContentions(rescpu);
	}
	
	public void workOnContentions(CPUResult rescpu) {
		Object o;
		BusContentionWholeResult bcwr;
		
		if (rescpu.contentions != null) {
			TraceManager.addDev("Working on contentions");
			for(BusContentionResult ct: rescpu.contentions) {
				TraceManager.addDev("One contention");
				o = contentionTable.get(ct.id);
				if (o == null) {
					bcwr = new BusContentionWholeResult(ct);
					contentionTable.put(ct.id, bcwr);
					addContentionOnBus(bcwr);
					TraceManager.addDev("adding contention");
				} else {
					bcwr = (BusContentionWholeResult)o;
					bcwr.updateResults(ct);
					TraceManager.addDev("updating contention");
				}
			}
		} else {
			TraceManager.addDev("null contention");
		}
	}
	
	public void addContentionOnBus(BusContentionWholeResult ct) {
		if (contentions == null) {
			contentions = new Vector<BusContentionWholeResult>();
		}
		
		contentions.add(ct);
	}
	
	public String toStringResult() {
		StringBuffer sb = new StringBuffer("");
		sb.append("CPU " + id + " " + name + " " + nbOfResults + " " + minUtilization + " " + averageUtilization + " " + maxUtilization);
		if (contentions != null) {
			for(BusContentionWholeResult bcwr: contentions) {
				sb.append("\n" + bcwr.toStringResult(id, name));
			}
		}
		
		return sb.toString();
	}
	
	public double getAverageBusContention() {
		double average = 0;
		
		if (contentions == null) {
			TraceManager.addDev("No contention");
			return 0;
		}
		
		for(BusContentionWholeResult wbc: contentions) {
			average += wbc.averageContention;
		}
		
		return average / contentions.size();
	}    
	
	public long getMaxBusContention() {
		long max = 0;
		
		if (contentions == null) {
			TraceManager.addDev("No contention");
			return 0;
		}
		
		for(BusContentionWholeResult wbc: contentions) {
			max = Math.max(max, wbc.maxContention);
		}
		
		return max;
	}
	
	public long getMinBusContention() {
		long min = 10000000;
		
		if (contentions == null) {
			TraceManager.addDev("No contention");
			return 0;
		}
		
		for(BusContentionWholeResult wbc: contentions) {
			min = Math.min(min, wbc.minContention);
		}
		
		return min;
	}
	
} // Class CPUWholeResult


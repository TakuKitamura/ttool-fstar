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
* Class DSEMappingSimulationResults
* Object for storing simulation results of several mappings
* Creation: 20/09/2011
* @version 1.0 20/09/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;

import tmltranslator.*;

import myutil.*;


import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

//import uppaaldesc.*;

public class DSEMappingSimulationResults  {
	
	
	private Vector<String> comments;
	private Vector<DSESimulationResult> results;
	private Vector<TMLMapping> maps;
	
	
	public DSEMappingSimulationResults() {
		reset();
	}
	
	public void reset() {
		comments = new Vector<String>();
		results = new Vector<DSESimulationResult>();
		maps = new Vector<TMLMapping>();
	}
	
	public void addElement(String _comment, DSESimulationResult _result, TMLMapping _tmla) {
		comments.add(_comment);
		results.add(_result);
		maps.add(_tmla);
	}
	
	public int nbOfElements() {
		return comments.size();
	}
	
	public String getComment(int index) {
		return comments.get(index);
	}
	
	public DSESimulationResult getResults(int index) {
		return results.get(index);
	}
	
	public TMLMapping getMapping(int index) {
		return maps.get(index);
	}
	
	public void computeSummaryResult() {
		for(DSESimulationResult res: results) {
			res.computeResults();
		}
	}
	
	public String getDescriptionOfAllMappings() {
		StringBuffer sb = new StringBuffer("");
		int cpt = 0;
		
		for(TMLMapping map: maps) {
			sb.append("#" + cpt + ": " + map.getSummaryTaskMapping() + "\n");
			cpt ++;
		}
		
		return sb.toString();
	}
	
	
	public int getMappingWithLowestAverageCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageCPUUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestAverageCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageCPUUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMaxCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxCPUUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMaxCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxCPUUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMinCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinCPUUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMinCPUUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinCPUUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	// Bus usage
	
	public int getMappingWithLowestAverageBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageBusUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestAverageBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageBusUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMaxBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxBusUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMaxBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxBusUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMinBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinBusUsage();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMinBusUsage() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinBusUsage();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	// Bus contention
	
	public int getMappingWithLowestAverageBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 1000000000;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageBusContention();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestAverageBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = -0.1;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageBusContention();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMaxBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 1000000000;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxBusContention();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMaxBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 0;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxBusContention();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMinBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 100000000;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinBusContention();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMinBusContention() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 0;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinBusContention();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	
	
	
} // Class DSEMappingSimulationResults


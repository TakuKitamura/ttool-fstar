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
	
	public final static String HTML_TOP = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n";
	public final static String HTML_HEADER = "<head>\n<title>DSE summary</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"results.css\" />\n<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" /\n</head>\n<body>\n";
	public final static String HTML_FOOTER = "</body>\n</html>\n";
	private Vector<String> comments;
	private Vector<DSESimulationResult> results;
	private Vector<TMLMapping> maps;
	private int [] cumulativeGrades;
	
	public DSEMappingSimulationResults() {
		reset();
	}
	
	public int getNbOfMappings() {
		return maps.size();
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
	
	public int getMappingWithHighestMinSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 0;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinSimulationDuration();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	


	public int getMappingWithLowestMinSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = Long.MAX_VALUE;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMinSimulationDuration();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}

	
	public int getMappingWithHighestAverageSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = 0;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageSimulationDuration();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	

	public int getMappingWithLowestAverageSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		double value = Long.MAX_VALUE;
		double valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getAverageSimulationDuration();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithHighestMaxSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = 0;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxSimulationDuration();
			if (valuetmp > value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	public int getMappingWithLowestMaxSimulationDuration() {
		if (results.size() == 0) {
			return -1;
		}
		
		int currentIndex = 0;
		int index = 0;
		long value = Long.MAX_VALUE;
		long valuetmp;
		
		for(DSESimulationResult dserr: results) {
			valuetmp = dserr.getMaxSimulationDuration();
			if (valuetmp < value) {
				value = valuetmp;
				index = currentIndex;
			}
			currentIndex ++;
		}
		
		return index;
	}
	
	// For ranking
	public long getMinSimulationDuration(int index) {
		return results.get(index).getMinSimulationDuration();
	}
	
	public double getAverageSimulationDuration(int index) {
		return results.get(index).getAverageSimulationDuration();
	}
	
	public long getMaxSimulationDuration(int index) {
		return results.get(index).getMaxSimulationDuration();
	}
	
	public void computeGrades(int tapLowestMinSimulationDuration, 
		int tapLowestMaxSimulationDuration, 
		int tapLowestAverageSimulationDuration,
		int tapLowestArchitectureComplexity) {
	
		// Give a grade to each mapping
		int nb = getNbOfMappings();
		cumulativeGrades = new int[nb];
		
		
		long min = Long.MAX_VALUE;
		int i;
		long max = 0;
		long value;
		double a;
		double b;
		int y;
		double valued;
		
		double mind;
		double maxd = 0;
		
		for(i=0; i<nb; i++) {
			cumulativeGrades[i] = 0;
		}
		
		// min get a grade of 100
		// max get a grade of 0
		
		if (tapLowestMinSimulationDuration != 0) {
			min = Long.MAX_VALUE;
			max = 0;
			for(i=0; i<nb; i++) {
				value = getMinSimulationDuration(i);
				min = Math.min(min, value);
				max = Math.max(max, value);
				
				/*if (max == value) {
					System.out.println("Max is for:" + i + " val=" + value);
				}
				
				if (min== value) {
					System.out.println("Min is for:" + i + " val=" + value);
				}*/
			}
			
			// If min = max, no difference between mappings -> no grade to give
			if (min != max) {
				
				a = 100 / ((double)min - (double)max);
				b = - a * max;
				
				//System.out.println("a=" + a + " b=" + b);
			
				for(i=0; i<nb; i++) {
					value = getMinSimulationDuration(i);
					y = (int)(a * value + b);
					cumulativeGrades[i] += tapLowestMinSimulationDuration * y; 
				}
			}
		}
		
		if (tapLowestAverageSimulationDuration != 0) {
			mind = Double.MAX_VALUE;
			maxd = 0;
			for(i=0; i<nb; i++) {
				valued = getAverageSimulationDuration(i);
				
				mind = Math.min(mind, valued);
				maxd = Math.max(maxd, valued);
				
				/*if (maxd == valued) {
					System.out.println("Max is for:" + i + " val=" + valued);
				}
				
				if (mind == valued) {
					System.out.println("Min is for:" + i + " val=" + valued);
				}*/
			}
			
			// If min = max, no difference between mappings -> no grade to give
			if (mind != maxd) {
				
				a = 100 / (mind - maxd);
				b = - a * maxd;
				
				//System.out.println("a=" + a + " b=" + b);
			
				for(i=0; i<nb; i++) {
					valued = getAverageSimulationDuration(i);
					y = (int)(a * valued + b);
					cumulativeGrades[i] += tapLowestAverageSimulationDuration * y; 
				}
			}
		}
		
		if (tapLowestMaxSimulationDuration != 0) {
			min = Long.MAX_VALUE;
			max = 0;
			for(i=0; i<nb; i++) {
				value = getMaxSimulationDuration(i);
				min = Math.min(min, value);
				max = Math.max(max, value);
				
				/*if (max == value) {
					System.out.println("Max is for:" + i + " val=" + value);
				}
				
				if (min== value) {
					System.out.println("Min is for:" + i + " val=" + value);
				}*/
			}
			
			// If min = max, no difference between mappings -> no grade to give
			if (min != max) {
				
				a = 100 / ((double)min - (double)max);
				b = - a * max;
				
				//System.out.println("a=" + a + " b=" + b);
			
				for(i=0; i<nb; i++) {
					value = getMaxSimulationDuration(i);
					y = (int)(a * value + b);
					cumulativeGrades[i] += tapLowestMaxSimulationDuration * y; 
				}
			}
		}
		
		if (tapLowestArchitectureComplexity != 0) {
			min = Long.MAX_VALUE;
			max = 0;
			for(i=0; i<nb; i++) {
				value = maps.get(i).getArchitectureComplexity();
				min = Math.min(min, value);
				max = Math.max(max, value);
				
				if (max == value) {
					System.out.println("Max is for:" + i + " val=" + value);
				}
				
				if (min== value) {
					System.out.println("Min is for:" + i + " val=" + value);
				}
			}
			
			// If min = max, no difference between mappings -> no grade to give
			if (min != max) {
				
				a = 100 / ((double)min - (double)max);
				b = - a * max;
				
				System.out.println("a=" + a + " b=" + b);
			
				for(i=0; i<nb; i++) {
					value = maps.get(i).getArchitectureComplexity();
					y = (int)(a * value + b);
					cumulativeGrades[i] += tapLowestArchitectureComplexity * y; 
				}
			}
		}
		
		
		// Printing grades
		for(i=0; i<nb; i++) {
			System.out.println("grade #" + i + ": " + cumulativeGrades[i]);
		}
		
	}
	
	public int[] getGrades() {
		return cumulativeGrades;
	}
	
	
	public String makeHTMLTableOfResults(int tapLowestMinSimulationDuration, 
		int tapLowestMaxSimulationDuration, 
		int tapLowestAverageSimulationDuration,
		int tapLowestArchitectureComplexity) {
	
	
		int nb = getNbOfMappings();
		int i;
		int cpt = 0;
		
		StringBuffer sb = new StringBuffer(HTML_TOP);
		sb.append(HTML_HEADER);
		sb.append("<h1> DSE: List of mappings</h1>\n\n");
		sb.append("<ul>\n");
		cpt = 0;
		for(TMLMapping map: maps) {
			sb.append("<li>#" +   cpt + ": " + map.getSummaryTaskMapping() + "</li>\n<br>\n");
			cpt ++;
		}
		sb.append("</ul>\n");
		sb.append("<h1> DSE: Results</h1>\n\n");
		sb.append("<table border=\"1\" id=\"bluetable\" >\n");
		sb.append("<tr>");
		sb.append("<th> </th>\n");
		for(i=0; i<nb; i++) {
			sb.append("<th>");
			sb.append("#" + i);
			sb.append("</th>\n");
		}
		sb.append("<th> tap </th>\n");
		
		sb.append("<th> </th>\n");
		sb.append("</tr>");
		
		// MinSimulationDuration
		sb.append("<tr>");
		sb.append("<th> Min Simulation Duration </th>\n");
		for(i=0; i<nb; i++) {
			sb.append("<td>");
			sb.append("" + getMinSimulationDuration(i));
			sb.append("</td>\n");
		}
		appendEndOfRow(sb, tapLowestMinSimulationDuration);
		
		
		
		// AverageSimulationDuration
		sb.append("<tr>");
		sb.append("<th> Average Simulation Duration </th>\n");
		for(i=0; i<nb; i++) {
			sb.append("<td>");
			sb.append("" + getAverageSimulationDuration(i));
			sb.append("</td>\n");
		}
		
		appendEndOfRow(sb, tapLowestAverageSimulationDuration);
		
		
		// MaxSimulationDuration
		sb.append("<tr>");
		sb.append("<th> Max Simulation Duration </th>\n");
		for(i=0; i<nb; i++) {
			sb.append("<td>");
			sb.append("" + getMaxSimulationDuration(i));
			sb.append("</td>\n");
		}
		
		appendEndOfRow(sb, tapLowestMaxSimulationDuration);
		
		// Complexity
		sb.append("<tr>");
		sb.append("<th>  Mapping complexity </th>\n");
		for(TMLMapping map: maps) {
			sb.append("<td>" + map.getArchitectureComplexity() + "</td>\n");
			cpt ++;
		}
		sb.append("<td>" +  tapLowestArchitectureComplexity + "</td>\n");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th> </th>\n");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<th> </th>\n");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th> Grade </th>\n");
		for(i=0; i<cumulativeGrades.length; i++) {
			sb.append("<td> " + cumulativeGrades[i] + "</td>\n");
		}
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th> Rank </th>\n");
		
		int[] index = new int[cumulativeGrades.length];
		for(i=0; i<index.length; i++) {
			index[i] = i;
		}
		int[] grades = cumulativeGrades.clone();
		
		TraceManager.addDev("Ranking 0");
		
		Conversion.quickSort(grades, 0, grades.length-1, index);
		
		int myGrade = 0;
		int myrank;
		for(i=0; i<index.length; i++) {
			myGrade = cumulativeGrades[i];
			myrank = -1;
			for(int j=index.length; j>=0; j--) {
				if (myGrade == grades[j]) {
					sb.append("<td>" + j + "</td>\n");
					break;
				}
			}
		}
		
		sb.append("</tr>");
		
		sb.append("</table>");
		
		sb.append(HTML_FOOTER);
		
		return sb.toString();
	}
	
	public void appendEndOfRow(StringBuffer sb, int value) {
		sb.append("<td> </td>");
		sb.append("<td>" +  value + "</td>\n");
		sb.append("</tr>");
	}
	
	

} // Class DSEMappingSimulationResults


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
	
	public final static String [] colors = {"99FF00", "99CC00", "999900", "996600", "993300", "990000"};
	
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
	
	private long getLongResultValueByID(int ID, int index) {
		switch(ID) {
		case 0:
			return getMinSimulationDuration(index);
		case 2: 
			return getMaxSimulationDuration(index);
		case 3: 
			return maps.get(index).getArchitectureComplexity();
		case 10:
			return results.get(index).getMinBusContention();
		case 12:
			return results.get(index).getMaxBusContention();
		}
		return 0;
	}
	
	private double getDoubleResultValueByID(int ID, int index) {
		switch(ID) {
		case 1:
			return results.get(index).getAverageSimulationDuration();
		case 4: 
			return results.get(index).getMinCPUUsage();
		case 5: 
			return results.get(index).getAverageCPUUsage();
		case 6: 
			return results.get(index).getAverageCPUUsage();
		case 7: 
			return results.get(index).getMinBusUsage();
		case 8: 
			return results.get(index).getAverageBusUsage();
		case 9: 
			return results.get(index).getAverageBusUsage();
		case 11:
			return results.get(index).getAverageBusContention();
		}
		return 0.0;
	}
	
	private void computeGradesDouble(int []cumulativeGrades, int ID, int tap) {
		double mind, maxd;
		int i;
		double valued;
		double a, b;
		int y;
		
		if (tap == 0) {
			return;
		}
		
		System.out.println("Computing grades of ID=" + ID + " tap=" + tap + " length=" + cumulativeGrades.length);
		
		mind = Long.MAX_VALUE; maxd = 0;
		for(i=0; i<cumulativeGrades.length; i++) {
			valued = getDoubleResultValueByID(ID, i);
			mind = Math.min(mind, valued); maxd = Math.max(maxd, valued);
		}
		
		// If min = max, no difference between mappings -> no grade to give
		System.out.println("mind= " + mind + " maxd= " + maxd);
		if (mind != maxd) {
			a = 100 / ((double)mind - (double)maxd);b = - a * maxd;
			for(i=0; i<cumulativeGrades.length; i++) {
				valued = getDoubleResultValueByID(ID, i);
				y = (int)(a * valued + b);
				System.out.println("Giving grade at " + i + " = " + tap * y);
				cumulativeGrades[i] += tap * y; 
			}
		}
	}
	
	private void computeGradesLong(int []cumulativeGrades, int ID, int tap) {
		long min, max;
		int i;
		long value;
		double a, b;
		int y;
		
		if (tap == 0) {
			return;
		}
		
		min = Long.MAX_VALUE; max = 0;
		for(i=0; i<cumulativeGrades.length; i++) {
			value = getLongResultValueByID(ID, i);
			min = Math.min(min, value); max = Math.max(max, value);
		}
		
		// If min = max, no difference between mappings -> no grade to give
		if (min != max) {
			a = 100 / ((double)min - (double)max);b = - a * max;
			for(i=0; i<cumulativeGrades.length; i++) {
				value = getLongResultValueByID(ID, i);
				y = (int)(a * value + b);
				cumulativeGrades[i] += tap * y; 
			}
		}
	}
	
	public void computeGrades(int []tapValues) {
	
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
		for(i=0; i<tapValues.length; i++) {
			if (DSEConfiguration.tapType[i] == DSEConfiguration.LONG_TYPE) {
				computeGradesLong(cumulativeGrades, i, tapValues[i]);
			}
			
			if (DSEConfiguration.tapType[i] == DSEConfiguration.DOUBLE_TYPE) {
				computeGradesDouble(cumulativeGrades, i, tapValues[i]);
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
	
	
	public String makeHTMLTableOfResults(int[] tapValues) {
	
	
		int nb = getNbOfMappings();
		int i, j;
		int cpt = 0;
		String[] values = new String[nb];
		long []valuesl;
		double []valuesd;
		int min, max;
		int indexMin, indexMax;
		int val;
		long vall;
		long minl, maxl;
		double vald;
		double mind, maxd;
		
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
		sb.append("<th> </th>\n");
		sb.append("<th> tap </th>\n");
		sb.append("</tr>");
		makeAllSetOfValuesLong(sb, "Min Simulation Duration", 0, tapValues[0]);
		makeAllSetOfValuesDouble(sb, "Average Simulation Duration", 1, tapValues[1]);
		makeAllSetOfValuesLong(sb, "Max Simulation Duration", 2, tapValues[2]);
		makeEmptyLine(sb);
		makeAllSetOfValuesDouble(sb, "Min CPU Usage", 4, tapValues[4]);
		makeAllSetOfValuesDouble(sb, "Average CPU Usage", 5, tapValues[5]);
		makeAllSetOfValuesDouble(sb, "Max CPU Usage", 6, tapValues[6]);
		makeEmptyLine(sb);
		makeAllSetOfValuesDouble(sb, "Min Bus Usage", 7, tapValues[7]);
		makeAllSetOfValuesDouble(sb, "Average Bus Usage", 8, tapValues[8]);
		makeAllSetOfValuesDouble(sb, "Max Bus Usage", 9, tapValues[9]);
		makeEmptyLine(sb);
		makeAllSetOfValuesLong(sb, "Min Bus Contention", 10, tapValues[10]);
		makeAllSetOfValuesDouble(sb, "Average Bus Contention", 11, tapValues[11]);
		makeAllSetOfValuesLong(sb, "Max Bus Contention", 12, tapValues[12]);
		makeEmptyLine(sb);
		makeAllSetOfValuesLong(sb, "Architecture complexity", 3, tapValues[3]);
		makeEmptyLine(sb);
		makeEmptyLine(sb);
		
		
		// Grades and ranking
		int[] index = new int[cumulativeGrades.length];
		for(i=0; i<index.length; i++) {
			index[i] = i;
		}
		int[] grades = cumulativeGrades.clone();
		Conversion.quickSort(grades, 0, grades.length-1, index);
		
		sb.append("<tr>");
		sb.append("<th> Grade </th>\n");
		for(i=0; i<cumulativeGrades.length; i++) {
			sb.append("<td bgcolor=\"" + getColorLong(grades[grades.length-1], grades[0], cumulativeGrades[i]) + "\">" + cumulativeGrades[i] + "</td>\n");
			//sb.append("<td> " + cumulativeGrades[i] + "</td>\n");
		}
		sb.append("<th> </th>\n");sb.append("<th> </th>\n");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th> Rank </th>\n");
		
		
		
		int myGrade = 0;
		int myrank;
		for(i=0; i<nb; i++) {
			for(j=0; j<index.length; j++) {
				if (index[j] == i) {
					myrank = nb - j;
					sb.append("<td bgcolor=\"" + getColorLong(1, nb, myrank) + "\">" + myrank + "</td>\n");
				}
			}
		}
		sb.append("<th> </th>\n");sb.append("<th> </th>\n");
		sb.append("</tr>");
		
		sb.append("</table>");
		
		sb.append(HTML_FOOTER);
		
		return sb.toString();
	}
	
	public void makeAllSetOfValuesLong(StringBuffer sb, String title, int tapID, int tapValue) {
		int index, i;
		long vall;
		
		int nb = getNbOfMappings();
		
		long[] values = new long[nb];
		long minl = Long.MAX_VALUE;
		long maxl = 0;
		for(i=0; i<nb; i++) {
			vall = getLongResultValueByID(tapID, i);
			values[i] =  vall;
			if (vall < minl) {
				minl = vall;
			}
			
			if (vall > maxl) {
				maxl = vall;
			}
		}
		
		sb.append("<tr>\n<th> " + title + " </th>\n");
		for(i=0; i<values.length; i++) {
			sb.append("<td bgcolor=\"" + getColorLongTapValue(minl, maxl, values[i], tapValue) + "\"> " + values[i] + "</td>\n");
		}
		appendEndOfRow(sb, tapValue);
	}
	
	public void makeAllSetOfValuesDouble(StringBuffer sb, String title, int tapID, int tapValue) {
		int index, i;
		double vall;
		
		int nb = getNbOfMappings();
		
		double[] values = new double[nb];
		double minl = Double.MAX_VALUE;
		double maxl = 0;
		for(i=0; i<nb; i++) {
			vall = getDoubleResultValueByID(tapID, i);
			values[i] =  vall;
			if (vall < minl) {
				minl = vall;
			}
			
			if (vall > maxl) {
				maxl = vall;
			}
		}
		
		sb.append("<tr>\n<th> " + title + " </th>\n");
		for(i=0; i<values.length; i++) {
			sb.append("<td bgcolor=\"" + getColorDoubleTapValue(minl, maxl, values[i], tapValue) + "\"> " +(((int)(100 * values[i]))/100.0) + "</td>\n");
		}
		appendEndOfRow(sb, tapValue);
	}
	
	public void makeSetOfValuesLong(StringBuffer sb, String title, long[] values, int minIndex, int maxIndex, long minl, long maxl, int tapValue) {
		int index;
		double vald;
		
		sb.append("<tr>\n<th> " + title + " </th>\n");
		for(int i=0; i<values.length; i++) {
			sb.append("<td bgcolor=\"" + getColorLong(minl, maxl, values[i]) + "\"> " + values[i] + "</td>\n");
		}
		appendEndOfRow(sb, tapValue);
	}
	
	public void makeSetOfValuesDouble(StringBuffer sb, String title, double[] values, int minIndex, int maxIndex, double mind, double maxd, int tapValue) {
		int index;
		double vald;
		
		if (tapValue < 0) {
			double tmp = maxd;
			maxd = mind;
			mind = tmp;
		}
		
		sb.append("<tr>\n<th> " + title + " </th>\n");
		for(int i=0; i<values.length; i++) {
			sb.append("<td bgcolor=\"" + getColorDouble(mind, maxd, values[i]) + "\"> " + (((int)(100 * values[i]))/100.0) + "</td>\n");
		}
		appendEndOfRow(sb, tapValue);
	}
	
	public void makeSetOfValues(StringBuffer sb, String title, String[] values, int minIndex, int maxIndex, int tapValue) {
		sb.append("<tr>\n<th> " + title + " </th>\n");
		for(int i=0; i<values.length; i++) {
			if (i == minIndex) {
				sb.append("<td bgcolor=\"#00ff00\"> " + values[i] + "</td>\n");
			} else if (i == maxIndex) {
				sb.append("<td bgcolor=\"red\"> " + values[i] + "</td>\n");
			} else {
				sb.append("<td> " + values[i] + "</td>\n");
			}
		}
		appendEndOfRow(sb, tapValue);
	}
	
	public void makeEmptyLine(StringBuffer sb) {
		sb.append("<tr>");
		for(int i=0; i<cumulativeGrades.length+3; i++) {
			sb.append("<th> </th>\n");
		}
		sb.append("</tr>");
	}
	
	public void appendEndOfRow(StringBuffer sb, int value) {
		sb.append("<th> </th>");
		sb.append("<td>" +  value + "</td>\n");
		sb.append("</tr>\n");
	}
	
	public String getColorLongTapValue(long min, long max, long value, int tapValue) {
		if (tapValue < 0) {
			return getColorLong(max, min, value);
		}
		return getColorLong(min, max, value);
	}          
	
	public String getColorLong(long min, long max, long value) {
		if (value == min) {
			return 	"#00ff00";
		} else if (value == max) {
			return "red";
		} else {
			int index = (int)((4 * (value - max) / ((double)(max) - (double)(min))) + 5);
			return getColor(index);
		}
	}          
	
	public String getColorDoubleTapValue(double mind, double maxd, double value, int tapValue) {
		if (tapValue < 0) {
			return getColorDouble(maxd, mind, value);
		}
		return getColorDouble(mind, maxd, value);
	}          
	
	public String getColorDouble(double mind, double maxd, double value) {
		if (value == mind) {
			return 	"#00ff00";
		} else if (value == maxd) {
			return "red";
		} else {
			int index = (int)((4 * (value - maxd) / ((double)(maxd) - (double)(mind))) + 5);
			return getColor(index);
		}
	}
	
	public String getColor(int index) {
		return colors[index%6];
	}
	
	

} // Class DSEMappingSimulationResults


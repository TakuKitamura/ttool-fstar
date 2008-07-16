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
 * Class StatisticsTableModel
 * Data of an action on a simulation trace
 * Creation: 13/08/2004
 * @version 1.0 13/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.util.*;
import java.io.*;
import javax.swing.table.*;

import ui.graph.*;
import myutil.*;

public class StatisticsTableModel extends AbstractTableModel implements SteppedAlgorithm {
	Vector statisticData;
	private int percentage;
	private boolean go;

	
	public StatisticsTableModel() {
		statisticData = new Vector();
		
	}
	
	public void analyzeData(String data) {
		makeStatisticData(data);
	}
	
	public void analyzeData(AUTGraph graph) {
		makeStatisticData(graph);
	}
	
	public int getPercentage() {
		return percentage;
	}
	
	public void  stopBuildElement() {
		go = false;
	}
	
	public void  setGo() {
		go = true;
	}
	
	

	// From AbstractTableModel
	public int getRowCount() {
		return statisticData.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int row, int column) {
		StatisticsItem si;
		si = (StatisticsItem)(statisticData.elementAt(Math.min(row, statisticData.size())));
		if (column == 0) {
			return si.getName();
		} else if (column == 1) {
			return si.getOccurence();
		} else {
			return si.getOriginDestination();
		}
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Transition";
		case 1:
			return "Nb";
		case 2:
		default:
			return "(origin, destination)";
		}
	}


	// to build internal data structure -> graph in AUT format
	private void makeStatisticData(String data) {
		StringReader sr = new StringReader(data);
		BufferedReader br = new BufferedReader(sr);
		String s;
		//String s1="", s2="", s3="";
		//String actionName, actionName1;
		//int index, index1, index2;
		StatisticsItem si1;
		String array[];
		
		setGo();

		try {
			while(((s = br.readLine()) != null) && (go)) {
				if (s.startsWith("(")) {
					array = AUTGraph.decodeLine(s);
					si1 = foundStatisticsItem(array[1]);
					//System.out.println("Toto1");
					if (si1 == null) {
						si1 = new StatisticsItem(array[1]);
						statisticData.add(si1);
					}
					//System.out.println("Toto2");
					si1.increaseOccurence();
					si1.addOriginDestination(Integer.decode(array[0]).intValue(), Integer.decode(array[2]).intValue());

				}

			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}
		Collections.sort(statisticData);
	}

	private void makeStatisticData(AUTGraph graph) {
		String s;
		//String s1="", s2="", s3="";
		//String actionName, actionName1;
		//int index, index1, index2;
		StatisticsItem si1 = null;
		String array[];
		int i;
		int nb = graph.getNbTransition();
		AUTTransition tr;
		
		percentage = 0;
		
		setGo();

		try {
			for(i=0; i<graph.getNbTransition(); i++) {
				
				if (!go) {
					return;
				}
				
				percentage = (int)(((i+1) * 100) / nb);
				//System.out.println("percentage = " + percentage);
				tr = graph.getAUTTransition(i);

				si1 = foundStatisticsItem(tr.transition);
				//System.out.println("Toto1");
				if (si1 == null) {
					si1 = new StatisticsItem(tr.transition);
					statisticData.add(si1);
				}
				//System.out.println("Toto2");
				si1.increaseOccurence();
				si1.addOriginDestination(tr.origin, tr.destination);

			}
		} catch (Exception e) {
			System.out.println("Exception " + e.getMessage());
		}
		Collections.sort(statisticData);
	}

	private StatisticsItem foundStatisticsItem(String name) {
		StatisticsItem si;

		for(int i=0; i<statisticData.size(); i++) {
			//System.out.println("i=" + i);
			si = (StatisticsItem)(statisticData.elementAt(i));
			if (si.getName().compareTo(name) == 0) {
				return si;
			}
		}
		return null;
	}
}

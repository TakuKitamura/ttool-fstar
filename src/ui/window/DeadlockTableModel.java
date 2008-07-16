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
 * Class DeadlockTableModel
 * Data of an action on a simulation trace
 * Creation: 15/09/2004
 * @version 1.0 15/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.util.*;
import javax.swing.table.*;

import myutil.*;
import ui.graph.*;

public class DeadlockTableModel extends AbstractTableModel {
	Vector deadlockData;
	int maxTransitions;

	public DeadlockTableModel(AUTGraph _graph, int _maxTransitions) {
		deadlockData = new Vector();
		maxTransitions = _maxTransitions;
		makeDeadlockData(_graph);
	}

	// From AbstractTableModel
	public int getRowCount() {
		return deadlockData.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int row, int column) {
		DeadlockItem di;
		di = (DeadlockItem)(deadlockData.elementAt(Math.min(row, deadlockData.size())));
		if (column == 0) {
			return di.getName();
		} else if (column == 1) {
			return di.getOriginAction();
		} else {
			return di.getPath();
		}
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "States";
		case 1:
			return "(origin, action)";
		case 2:
		default:
			return "Shortest path to state";
		}
	}


	// to build internal data structure -> graph in AUT format
	private void makeDeadlockData(AUTGraph graph) {
		DeadlockItem di;
		AUTTransition aut1;
		DijkstraState[] dss = null;
		if (graph.getNbTransition() < maxTransitions) {
			dss = GraphAlgorithms.ShortestPathFrom(graph, 0);
		}
		//System.out.println(dss.toString());

		/*for(int k=0; k<dss.length; k++) {
            System.out.println(dss[k]);
        }*/

		//System.out.println("Getting vector potential deadlocks");
		int [] states = graph.getVectorPotentialDeadlocks();
		//System.out.println("Got vector potential deadlocks");
		int i, j, size, state;
		String path;

		for(i=0; i<states.length; i++) {
			state = states[i];
			di = new DeadlockItem(""+ state);
			deadlockData.add(di);
			if (dss != null) {
				for(j=0; j<graph.getNbTransition(); j++) {
					aut1 = graph.getAUTTransition(j);
					if (aut1.destination == state) {
						di.addOriginAction(""+aut1.origin, aut1.transition);
					}
				} 
			}else {
				di.addOriginAction("", "");
			}
			//path = "0 --" + graph.getActionTransition(0, dss[state].path[0]) + "--> ";;
			if (dss != null) {
				size = dss[state].path.length;
				path = "";
				for(j=0; j<dss[state].path.length; j++) {
					path = path + "[" + dss[state].path[j] + "]";
					if (j < size - 1) {
						path = path + " -- " + graph.getActionTransition(dss[state].path[j], dss[state].path[j+1]) + " --> ";
					}
				}
				di.setPath(path);
			} else {
				di.setPath("not calculated");
			}
		}
		Collections.sort(deadlockData);
	}
}
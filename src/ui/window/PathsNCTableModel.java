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
 * Class TrafficsNCTableModel
 * Main data of links in NC structures
 * Creation: 27/11/2008
 * @version 1.0 27/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.util.*;
import javax.swing.table.*;

import myutil.*;
import nc.*;

public class PathsNCTableModel extends AbstractTableModel {
	private NCStructure ncs;
	private String[] paths;
	
	//private String [] names;
	public PathsNCTableModel(NCStructure _ncs) {
		ncs = _ncs;
		computeData();
	}

	// From AbstractTableModel
	public int getRowCount() {
		return ncs.paths.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return ncs.paths.get(row).getName();
		} else if (column == 1) {
			return ncs.paths.get(row).traffic.getName();
		} 
		
		return paths[row];
		
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Path";
		case 1:
			return "Traffic";
		case 2:
			return "Eq -> ... -> Eq ";
		}
		return "none";
	}
	
	private void computeData() {
		paths = new String[ncs.paths.size()];
		String tmp;
		int cpt = 0;
		
		for(NCPath path: ncs.paths) {
			tmp = path.origin.getName() + " -> ";
			for(NCSwitch sw: path.switches) {
				tmp += sw.getName() + " -> ";
			}
			tmp += path.destination.getName();
			paths[cpt] = tmp;
			cpt ++;
		}
	}

}
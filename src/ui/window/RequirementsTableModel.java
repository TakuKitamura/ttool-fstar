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
 * Class RequirementsTableModel
 * Main data of requirements
 * Creation: 17/02/2009
 * @version 1.0 17/02/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.util.*;
import javax.swing.table.*;

import myutil.*;
import ui.req.*;

public class RequirementsTableModel extends AbstractTableModel {
	private LinkedList<Requirement> list;
	
	//private String [] names;
	public RequirementsTableModel(LinkedList<Requirement> _list) {
		list = _list;
		//computeData(_ncs);
	}

	// From AbstractTableModel
	public int getRowCount() {
		return list.size();
	}

	public int getColumnCount() {
		return 9;
	}

	public Object getValueAt(int row, int column) {
		Requirement r = list.get(row);
		int type;
		
		switch(column) {
		case 0:
			return r.getID();
		case 1:
			return r.getValue();
		case 2:
			type = r.getRequirementType();
			if (type == 0) {
				return "Regular req.";
			} else if (type == 1) {
				return "Formal req.";
			} else {
				return "Security req.";
			}
		case 3:
			return r.getText();
		case 4:
			return r.getKind();
		case 5:
			return r.getCriticality();
		case 6:
			if (r.getRequirementType() == 1) {
				return r.getViolatedAction();
			} else {
				return " - ";
			}
		case 7:
			if (r.getRequirementType() == 2) {
				return r.getAttackTreeNode();
			} else {
				return " - ";
			}
		case 8:
			if (r.isSatisfied()) {
				return "yes";
			} else {
				return "no";
			}
		}
		
		return "Invalid column";
		
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "ID";
		case 1:
			return "Name";
		case 2:
			return "Type";
		case 3:
			return "Description";
		case 4:
			return "Kind";
		case 5:
			return "Criticality";
		case 6:
			return "Violated action";
		case 7:
			return "Attack Tree Nodes";
		case 8:
			return "Satisfied";
		}
		return "none";
	}

}
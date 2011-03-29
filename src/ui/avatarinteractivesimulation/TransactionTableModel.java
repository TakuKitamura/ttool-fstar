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
* Class TransactionTableModel
* Information on transactions
* Creation: 28/03/2011
* @version 1.0 28/03/2011
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarinteractivesimulation;

import java.util.*;
import javax.swing.table.*;

import ui.*;
import myutil.*;
import avatartranslator.*;
import avatartranslator.directsimulation.*;

public class TransactionTableModel extends AbstractTableModel {
	private static String ERROR_STRING = "-"; 
	private static int MAX_TRANSACTIONS = 1000;
	
	private AvatarSpecificationSimulation ass;
	
	//private String [] names;
	public TransactionTableModel(AvatarSpecificationSimulation _ass) {
		ass = _ass;
	}
	
	// From AbstractTableModel
	public int getRowCount() {
		return Math.min(ass.getAllTransactions().size(), MAX_TRANSACTIONS);
	}
	
	public int getColumnCount() {
		return 8;
	}
	
	public Object getValueAt(int row, int column) {
		try {
			if (ass == null) {
				return ERROR_STRING;
			}
			
			AvatarSimulationTransaction ast;
			if (ass.getAllTransactions().size() < MAX_TRANSACTIONS) {
				ast = ass.getAllTransactions().get(row);
			} else {
				ast = ass.getAllTransactions().get(ass.getAllTransactions().size()-MAX_TRANSACTIONS + row);
			}
			
			if (ast == null) {
				return ERROR_STRING;
			}
			
			if (column == 0) {
				return ""+ast.id;
			} else if (column == 1) {
				return ast.block.getName();
			} else if (column == 2) {
				return ast.executedElement.getNiceName() + "/" + ast.executedElement.getID();
			} else if (column == 3) {
				if (ast.concernedElement != null) {
					return ast.concernedElement.getNiceName() + "/" + ast.concernedElement.getID();
				}
			} else if (column == 4) {
				if (ast.linkedTransaction != null) {
					return ""+ast.linkedTransaction.id;
				}
			} else if (column == 5) {
				return ""+ast.initialClockValue;
			} else if (column == 6) {
				return ""+ast.duration;
			} else if (column == 7) {
				return ""+ast.clockValueWhenFinished;
			}
		} catch (Exception e) {
		}
		return ERROR_STRING;
	}
	
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "#";
		case 1:
			return "Block";
		case 2:
			return "ASM element";
		case 3:
			return "Related ASM element";
		case 4:
			return "Linked transaction";
		case 5:
			return "Initial clock value";
		case 6:
			return "Duration";
		case 7:
			return "Final clock value";
		}
		return "unknown";
	}
	
}
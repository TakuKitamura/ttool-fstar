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
 * Class blockTableModel
 * Information on blocks
 * Creation: 21/01/2011
 * @version 1.0 21/01/2011
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarinteractivesimulation;

import java.util.*;
import javax.swing.table.*;

import myutil.*;
import avatartranslator.directsimulation.*;

public class BlockTableModel extends AbstractTableModel {
	private AvatarSpecificationSimulation ass;
	
	private int nbOfRows;
	
	//private String [] names;
	public BlockTableModel(AvatarSpecificationSimulation _ass) {
		ass = _ass;
		computeData();
	}

	// From AbstractTableModel
	public int getRowCount() {
		
		if (nbOfRows == -1) {
			computeData();
		}
		return nbOfRows;
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int row, int column) {
		if (ass == null) {
			return "-";
		}
		
		if (column == 0) {
			return getBlockName(row);
		} else if (column == 1) {
			return getBlockID(row);
		} else if (column == 2) {
			return getBlockStatus(row);
		} else if (column == 3) {
			return getBlockNbOfTransactions(row);
		}
		return "";
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Block Name";
		case 1:
			return "Block ID";
		case 2:
			return "State";
		case 3:
			return "Nb of transactions";
		}
		return "unknown";
	}
	
	// Assumes tmlm != null
	private String getBlockName(int row) {
		return ass.getSimulationBlocks().get(row).getName();
	}
	
	// Assumes tmlm != null
	private String getBlockID(int row) {
		return ""+ass.getSimulationBlocks().get(row).getID();
	}
	
	public String getBlockStatus(int row) {
		int status = ass.getSimulationBlocks().get(row).getStatus();
		
		switch(status) {
		case AvatarSimulationBlock.NOT_STARTED:
			return "not started";
		case AvatarSimulationBlock.STARTED:
			return "running";
		case AvatarSimulationBlock.COMPLETED:
			return "terminated";
		}
		return "unknown";
		
	}
	
	public String getBlockNbOfTransactions(int row) {
		return  ""+ ass.getSimulationBlocks().get(row).getTransactions().size();
	}
	
	private void computeData() {
		if (ass == null) {
			nbOfRows = 0;
			return ;
		}
		
		if (ass.getSimulationBlocks() != null) {
			nbOfRows = ass.getSimulationBlocks().size();
		}
		
		nbOfRows = -1;
		
		return;
	}

}
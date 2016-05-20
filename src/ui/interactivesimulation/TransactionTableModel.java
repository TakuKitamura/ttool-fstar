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
   * Recent transactions
   * Creation: 20/05/2016
   * @version 1.0 20/05/2016
   * @author Ludovic APVRILLE
   * @see
   */

package ui.interactivesimulation;

import java.util.*;
import javax.swing.table.*;

import myutil.*;
import tmltranslator.*;

public class TransactionTableModel extends AbstractTableModel {
    private JFrameInteractiveSimulation jfis;
    private int nbOfRows;

    //private String [] names;
    public TransactionTableModel(JFrameInteractiveSimulation _jfis) {
	jfis = _jfis;
    }

    // From AbstractTableModel
    public int getRowCount() {
	Vector<SimulationTransaction> tr = jfis.getListOfRecentTransactions();
	if (tr == null) {
	    return 0;
	}
        return tr.size();
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int row, int column) {
        Vector<SimulationTransaction> tr = jfis.getListOfRecentTransactions();
	if (tr == null) {
	    return "";
	}

	if (row >= tr.size()) {
	    return "";
	}

	SimulationTransaction st = tr.get(row);

	switch(column) {
        case 0:
            return st.deviceName;
        case 1:
            return st.taskName;
        case 2:
            return st.command;
        case 3:
            return st.startTime;
        case 4:
            return st.channelName;
        }
        return "unknown";		
    }

    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return "Node";
        case 1:
            return "Task";
        case 2:
            return "Command";
        case 3:
            return "Start";
        case 4:
            return "Channel";
        }
        return "unknown";
    }

    public void fireTableRowUpdated(int index) {
	TraceManager.addDev("Firing on row=" + index);
	for (int i=0; i<getColumnCount(); i++) {
	    fireTableCellUpdated(index, i);
	}
    }

}

/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui.interactivesimulation;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Class TransactionTableModel Recent transactions Creation: 20/05/2016
 * 
 * @version 1.0 20/05/2016
 * @author Ludovic APVRILLE
 */
public class LatencyTableModel extends AbstractTableModel {
  // private JFrameInteractiveSimulation jfis;
  private int nbOfRows;
  private SimulationLatency data[];

  // private String [] names;
  public LatencyTableModel() {
    // jfis = jfis;
    SimulationLatency sl = new SimulationLatency();
    data = new SimulationLatency[] { sl };

  }

  // From AbstractTableModel
  public synchronized int getRowCount() {
    if (data == null) {
      return 0;
    }
    return data.length;
  }

  public int getColumnCount() {
    return 6;
  }

  public synchronized Object getValueAt(int row, int column) {
    if (data == null) {
      return "";
    }

    if (row >= data.length) {
      return "";
    }
    SimulationLatency st = data[row];

    switch (column) {
      case 0:
        return st.getTransaction1();
      case 1:
        return st.getTransaction2();
      case 2:
        return st.getMinTime();
      case 3:
        return st.getMaxTime();
      case 4:
        return st.getAverageTime();
      case 5:
        return st.getStDev();
    }
    return "unknown";
  }

  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return "Transaction 1";
      case 1:
        return "Transaction 2";
      case 2:
        return "Min";
      case 3:
        return "Max";
      case 4:
        return "Average";
      case 5:
        return "St Dev";
    }

    return "unknown";
  }

  public synchronized void setData(Vector<SimulationLatency> _trans) {
    data = new SimulationLatency[_trans.size()];
    for (int i = 0; i < _trans.size(); i++) {
      data[i] = _trans.get(i);
    }
    fireTableStructureChanged();
  }

}

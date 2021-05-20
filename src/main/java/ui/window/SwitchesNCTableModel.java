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

package ui.window;

import nc.NCStructure;
import nc.NCSwitch;

import javax.swing.table.AbstractTableModel;

/**
 * Class SwitchesNCTableModel Main data of switches in NC structures Creation:
 * 26/11/2008
 * 
 * @version 1.0 26/11/2008
 * @author Ludovic APVRILLE
 */
public class SwitchesNCTableModel extends AbstractTableModel {
  private NCStructure ncs;

  // private String [] names;
  public SwitchesNCTableModel(NCStructure _ncs) {
    ncs = _ncs;
    // computeData(_ncs);
  }

  // From AbstractTableModel
  public int getRowCount() {
    return ncs.switches.size();
  }

  public int getColumnCount() {
    return 5;
  }

  public Object getValueAt(int row, int column) {
    if (column == 0) {
      return ncs.switches.get(row).getName();
    } else if (column == 1) {
      return NCSwitch.getStringSwitchingTechnique(ncs.switches.get(row).getSwitchingTechnique());
    } else if (column == 2) {
      return NCSwitch.getStringSchedulingPolicy(ncs.switches.get(row).getSchedulingPolicy());
    } else if (column == 3) {
      return ncs.switches.get(row).getCapacity() + " " + ncs.switches.get(row).getCapacityUnit().getStringUnit();
    } else {
      return ncs.switches.get(row).getTechnicalLatency() + " us";
    }
  }

  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return "Switch";
      case 1:
        return "Switching tech.";
      case 2:
        return "Scheduling policy";
      case 3:
        return "Capacity";
    }
    return "Tech. latency";
  }

}
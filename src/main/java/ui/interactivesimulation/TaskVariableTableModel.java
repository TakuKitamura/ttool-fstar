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
   * Class TaskVariableTableModel
   * Variables of TML tasks
   * Creation: 29/05/2009
   * @version 1.0 29/05/2009
   * @author Ludovic APVRILLE
   * @see
   */

package ui.interactivesimulation;

import tmltranslator.TMLModeling;
import tmltranslator.TMLTask;

import javax.swing.table.AbstractTableModel;
import java.util.Hashtable;

public class TaskVariableTableModel extends AbstractTableModel {
    private TMLModeling tmlm;
    private Hashtable <Integer, String> valueTable;
    private Hashtable <Integer, Integer> rowTable;

    private int nbOfRows;

    //private String [] names;
    public TaskVariableTableModel(TMLModeling _tmlm, Hashtable<Integer, String> _valueTable, Hashtable <Integer, Integer> _rowTable) {
        tmlm = _tmlm;
        valueTable = _valueTable;
        rowTable = _rowTable;
        computeData();
    }

    // From AbstractTableModel
    public int getRowCount() {
        return nbOfRows;
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int row, int column) {
        if (tmlm == null) {
            return "-";
        }

        if (column == 0) {
            return getTaskName(row);
        } else if (column == 1) {
            return getTaskID(row);
        } else if (column == 2) {
            return getVariableName(row);
        } else if (column == 3) {
            return getStringVariableID(row);
        } else if (column == 4) {
            return getVariableValue(row);
        }
        return "";
    }

    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return "Task Name";
        case 1:
            return "Task ID";
        case 2:
            return "Variable name";
        case 3:
            return "Variable ID";
        case 4:
            return "Value";
        }
        return "unknown";
    }

    // Assumes tmlm != null
    private String getTaskName(int row) {
        int cpt = 0;
        for(TMLTask task: tmlm.getTasks()) {
            cpt += task.getAttributes().size();
            if (row < cpt) {
                return task.getName();
            }
        }

        return "unknown task";
    }

    // Assumes tmlm != null
    private String getTaskID(int row) {
        int cpt = 0;
        for(TMLTask task: tmlm.getTasks()) {
            cpt += task.getAttributes().size();
            if (row < cpt) {
                return "" + task.getID();
            }
        }

        return "unknown ID";
    }

    private String getVariableName(int row) {
        int cpt = 0;
        int size;
        for(TMLTask task: tmlm.getTasks()) {
            size = task.getAttributes().size();
            cpt += size;
            if (row < cpt) {
                return "" + task.getAttributes().get(row+size-cpt).getName();
            }
        }

        return "unknown name";
    }

    private String getStringVariableID(int row) {
        int id = getVariableID(row);
        if (id < 0) {
            return "unknown id";
        }
        return "" + id;
    }

    private int getVariableID(int row) {
        int cpt = 0;
        int size;
        for(TMLTask task: tmlm.getTasks()) {
            size = task.getAttributes().size();
            cpt += size;
            if (row < cpt) {
                return task.getAttributes().get(row+size-cpt).getID();
            }
        }

        return 0;
    }

    private String getVariableInitialValue(int row) {
        int cpt = 0;
        int size;
        for(TMLTask task: tmlm.getTasks()) {
            size = task.getAttributes().size();
            cpt += size;
            if (row < cpt) {
                String val = null ;
                try {
                    val =  task.getAttributes().get(row+size-cpt).getInitialValue();
                } catch (Exception e) {}
                if ((val == null) || (val.length() == 0)) {
                    return " - ";
                } else {
                    return val;
                }
            }
        }

        return "unknown ID";
    }

    private String getVariableValue(int row) {
        int ID = getVariableID(row);
        String s = valueTable.get(new Integer(ID));
        if (s != null) {
            return s.toString();
        }

        // Must set the ID;
        String val = getVariableInitialValue(row);
        valueTable.put(new Integer(ID), val);
        rowTable.put(new Integer(ID), row);
        return val;

    }

    private void computeData() {
        if (tmlm == null) {
            nbOfRows = 0;
            return ;
        }

        int cpt = 0;
        for(TMLTask task: tmlm.getTasks()) {
            cpt += task.getAttributes().size();
        }

        nbOfRows = cpt;

        for(int i=0; i<nbOfRows; i++) {
            getVariableValue(i);
        }
        return;
    }

}

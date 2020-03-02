package ui.interactivesimulation;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Vector;

public class TaskTransactionTableModel extends AbstractTableModel  {
    private JFrameInteractiveSimulation jfis;
    private int nbOfRows;
    private SimulationTransaction data[];


    //private String [] names;
    public TaskTransactionTableModel(JFrameInteractiveSimulation _jfis) {
        jfis = jfis;
        data = null;
    }

    // From AbstractTableModel
    public synchronized int getRowCount() {
        //Vector<SimulationTransaction> tr = jfis.getListOfRecentTransactions();
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    public int getColumnCount() {
        return 7;
    }

    public synchronized Object getValueAt(int row, int column) {
        if (data == null) {
            return "";
        }

        if (row >= data.length) {
            return "";
        }

        SimulationTransaction st = data[row];

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
                return st.endTime;
            case 5:
                return st.length;
            case 6:
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
                return "End";
            case 5:
                return "Length";
            case 6:
                return "Channel";
        }
        return "unknown";
    }

    public synchronized void setData(Vector<SimulationTransaction> _trans, String taskName) {
        data = new SimulationTransaction[_trans.size()];
        int t = 0;
        for(int i=0; i<_trans.size(); i++) {
            if (_trans.get(i).taskName.equals(taskName)){
                data[t] = _trans.get(i);
                t++;
            }

        }
        fireTableStructureChanged();
    }

}

package ui.interactivesimulation;

import myutil.TableSorter;
import tmltranslator.TMLModeling;
import tmltranslator.simulation.SimulationTransaction;
import ui.TGComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class JPanelTaskTransactions extends JPanel {
    private JFrameInteractiveSimulation jfis;
    private TaskTransactionTableModel ttm;
    private JButton updateTransactionInformationButton, clearAllOldTransactions;
    private JScrollPane jspTransactionInfo;
    private JComboBox<String> tasks;
    private Vector<SimulationTransaction> _trans;
    private TMLModeling<TGComponent> tmlm;

    private JTextField nbOfTransactions;
    private int defaultNbOfTransactions;




    public JPanelTaskTransactions(TMLModeling<TGComponent> _tmlm,JFrameInteractiveSimulation _jfis, int _defaultNbOfTransactions) {
        super();
        tmlm = _tmlm;
        jfis = _jfis;
        defaultNbOfTransactions = _defaultNbOfTransactions;

        makeComponents();
        setComponents();
    }

    public void makeComponents() {
        TableSorter sorterPI;
        JTable jtablePI;

        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        setLayout(gridbag2);
        setBorder(new javax.swing.border.TitledBorder("Managing transactions of Tasks"));

        ttm = new TaskTransactionTableModel(jfis);
        sorterPI = new TableSorter(ttm);
        jtablePI = new JTable(sorterPI);
        sorterPI.setTableHeader(jtablePI.getTableHeader());
        ((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(200);
        ((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(150);
        ((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(5)).setPreferredWidth(100);
        ((jtablePI.getColumnModel()).getColumn(6)).setPreferredWidth(100);
        jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jspTransactionInfo = new JScrollPane(jtablePI);
        jspTransactionInfo.setWheelScrollingEnabled(true);
        jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
        jspTransactionInfo.setPreferredSize(new Dimension(500, 300));


        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        add(jspTransactionInfo, c2);

        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        add(new JLabel(""), c2);
        add(new JLabel("# of transactions per node:"), c2);

        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;

        nbOfTransactions = new JTextField(Integer.toString(defaultNbOfTransactions), defaultNbOfTransactions);
        add(nbOfTransactions, c2);

        add(new JLabel("Task Name: "), c2);
        tasks = new JComboBox<>();
        tasks.addItem("-----");
        if(tmlm != null){
            for (int k = 0; k < tmlm.getTasks().size(); k++){
                tasks.addItem(tmlm.getTasks().get(k).getName());
            }
        }

        tasks.setSelectedIndex(0);
        add(tasks, c2);
        updateTransactionInformationButton = new JButton(jfis.actions[InteractiveSimulationActions.ACT_UPDATE_TRANSACTIONS]);
        add(updateTransactionInformationButton, c2);
//        clearAllOldTransactions = new JButton();
//        clearAllOldTransactions.setText("Clear all old transactions");
//        clearAllOldTransactions.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                jfis.sendCommand("rmat 1");
//            }
//        });
//        add(clearAllOldTransactions, c2);
        tasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (_trans == null){
                    System.out.println("TRANSACTION LIST IS NULL \nYOU NEED TO UPDATE TRANSACTIONS FIRST");
                    final JPopupMenu menu = new JPopupMenu("Alert");
                    menu.add("TRANSACTION LIST IS NULL. \nYOU NEED TO UPDATE TRANSACTIONS FIRST.");
                    menu.show(updateTransactionInformationButton, updateTransactionInformationButton.getWidth()/2, updateTransactionInformationButton.getHeight()/2);
                }else {
                    System.out.println(tasks.getSelectedItem().toString());
                    if (ttm != null) {
                        ttm.setData(_trans,tasks.getSelectedItem().toString());
                    }
                }
            }
        });
    }

    public void setComponents() {

    }

    public TaskTransactionTableModel getTableModel() {
        return ttm;
    }

    public void resetTable() {
        if (ttm != null) {
            ttm.setData(_trans,"----");
        }
    }

    public int getNbOfTransactions() {

        try {
            defaultNbOfTransactions = Integer.decode(nbOfTransactions.getText()).intValue();
        } catch (Exception e) {
            //TraceManager.addDev(nbOfTransactions.getText());
            //TraceManager.addDev("Invalid default transaction");
            defaultNbOfTransactions = 0;
        }
        return defaultNbOfTransactions;
    }


    public void setData(Vector<SimulationTransaction> _trans) {
        this._trans = _trans;
        ttm.setData(_trans,tasks.getSelectedItem().toString());

    }

}

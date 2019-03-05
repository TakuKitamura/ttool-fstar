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

import myutil.GraphicLib;
import tmltranslator.modelcompiler.ArchUnitMEC;
import ui.ColorManager;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmldd.TMLArchiFPGANode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Class JDialogFPGA
 * Dialog for managing attributes of cpu nodes
 * Creation: 07/02/2018
 *
 * @author Ludovic APVRILLE
 * @version 2.0 05/03/2019
 */
public class JDialogFPGANode extends JDialogBase implements ActionListener {

    private boolean regularClose;

    private JPanel panel2, panel4, panel5;
    //   private Frame frame;
    private TMLArchiFPGANode node;


    protected JTextField nodeName;

    // Panel2
    protected JTextField byteDataSize, goIdleTime, maxConsecutiveIdleCycles, clockRatio, execiTime, execcTime,
            capacity, mappingPenalty, reconfigurationTime, operation;

    // Tabbed pane for panel1 and panel2
    //private JTabbedPane tabbedPane;


    /*
     * Creates new form
     */
    public JDialogFPGANode(Frame _frame, String _title, TMLArchiFPGANode _node) {
        super(_frame, _title, true);
        //  frame = _frame;
        node = _node;
        initComponents();
        //     myInitComponents();
        pack();
    }
//
//    private void myInitComponents() {
//    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Attributes"));
        panel2.setPreferredSize(new Dimension(400, 300));

        // Issue #41 Ordering of tabbed panes 
        //tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("CPU name:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
        panel2.add(nodeName, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Data size (in byte):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        byteDataSize = new JTextField("" + node.getByteDataSize(), 15);
        panel2.add(byteDataSize, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Overall mapping capacity:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        capacity = new JTextField("" + node.getCapacity(), 15);
        panel2.add(capacity, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Mapping penalty:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        mappingPenalty = new JTextField("" + node.getMappingPenalty(), 15);
        panel2.add(mappingPenalty, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Reconfiguration time:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        reconfigurationTime = new JTextField("" + node.getReconfigurationTime(), 15);
        panel2.add(reconfigurationTime, c2);


        c2.gridwidth = 1;
        panel2.add(new JLabel("Go idle time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        goIdleTime = new JTextField("" + node.getGoIdleTime(), 15);
        panel2.add(goIdleTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Max consecutive cycles before idle (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxConsecutiveIdleCycles = new JTextField("" + node.getMaxConsecutiveIdleCycles(), 15);
        panel2.add(maxConsecutiveIdleCycles, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECI execution time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execiTime = new JTextField("" + node.getExeciTime(), 15);
        panel2.add(execiTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("EXECC execution time (in cycle):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        execcTime = new JTextField("" + node.getExeccTime(), 15);
        panel2.add(execcTime, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Operation:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        operation = new JTextField(""+node.getOperation(), 15);
        panel2.add(operation, c2);

        c2.gridwidth = 1;
        panel2.add(new JLabel("Clock divider:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        clockRatio = new JTextField("" + node.getClockRatio(), 15);
        panel2.add(clockRatio, c2);


        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt) {
        /* if (evt.getSource() == typeBox) {
           boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
           initialValue.setEnabled(b);
           return;
           }*/

//        if (evt.getSource() == tracemode) {
//            selectedTracemode = tracemode.getSelectedIndex();
//        }

        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }

    public void closeDialog() {
        regularClose = true;
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public boolean isRegularClose() {
        return regularClose;
    }

    public String getNodeName() {
        return nodeName.getText();
    }

    public String getCapacity() {
        return capacity.getText();
    }

    public String getByteDataSize() {
        return byteDataSize.getText();
    }

    public String getReconfigurationTime() {
        return reconfigurationTime.getText();
    }

    public String getMappingPenalty() {
        return mappingPenalty.getText();
    }

    public String getGoIdleTime() {
        return goIdleTime.getText();
    }

    public String getMaxConsecutiveIdleCycles() {
        return maxConsecutiveIdleCycles.getText();
    }

    public String getExeciTime() {
        return execiTime.getText();
    }

    public String getExeccTime() {
        return execcTime.getText();
    }

    public String getOperation() {
        return operation.getText();
    }


    public String getClockRatio() {
        return clockRatio.getText();
    }


}

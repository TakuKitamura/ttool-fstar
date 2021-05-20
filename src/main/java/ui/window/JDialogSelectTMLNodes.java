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

import ui.TGComponent;
import ui.TGComponentPlugin;
import ui.tmldd.TMLArchiNode;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Class JDialogSelectTMLNodes Dialog for managing nodes to be validated
 * Creation: 19/09/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.0 19/09/2007
 */
public class JDialogSelectTMLNodes extends JDialogBase implements ActionListener, ListSelectionListener {
    public static Vector<TGComponent> validated, ignored;
    private static boolean optimized = true;

    private int clock;

    private Vector<TGComponent> val, ign, back;

    // subpanels
    private JPanel panel1, panel2, panel3, panel4, panel5, panel6;
    private JList<TGComponent> listIgnored;
    private JList<TGComponent> listValidated;
    private JButton allValidated;
    private JButton addOneValidated;
    private JButton addOneIgnored;
    private JButton allIgnored;
    protected JCheckBox optimize;
    protected JTextField clockField;

    /*
     * Creates new form
     */
    public JDialogSelectTMLNodes(Frame f, Vector<TGComponent> _back, java.util.List<TGComponent> componentList,
            String title, int _clock) {
        super(f, title, true);

        back = _back;

        clock = _clock;

        if ((validated == null) || (ignored == null)) {
            val = makeNewVal(componentList);
            ign = new Vector<TGComponent>();
        } else {
            val = validated;
            ign = ignored;
            checkNode(val, componentList);
            checkNode(ign, componentList);
            addNewNode(val, componentList, ign);
        }

        initComponents();
        myInitComponents();
        pack();
    }

    private Vector<TGComponent> makeNewVal(java.util.List<TGComponent> list) {
        Vector<TGComponent> v = new Vector<TGComponent>();
        TGComponent tgc;

        for (int i = 0; i < list.size(); i++) {
            tgc = list.get(i);
            //
            if (tgc instanceof TMLArchiNode) {
                v.addElement(tgc);
            }
            if (tgc instanceof TGComponentPlugin) {
                v.addElement(tgc);
            }
        }

        return v;
    }

    private void checkNode(Vector<TGComponent> tobeChecked, java.util.List<TGComponent> source) {
        TGComponent t;

        for (int i = 0; i < tobeChecked.size(); i++) {
            t = tobeChecked.elementAt(i);

            if (!source.contains(t)) {
                tobeChecked.removeElementAt(i);
                i--;
            }
        }
    }

    public void addNewNode(Vector<TGComponent> added, java.util.List<TGComponent> source,
            Vector<TGComponent> notSource) {
        TGComponent tgc;

        for (int i = 0; i < source.size(); i++) {
            tgc = source.get(i);

            if (((tgc instanceof TMLArchiNode) || (tgc instanceof TGComponentPlugin)) && (!added.contains(tgc))
                    && (!notSource.contains(tgc))) {
                added.addElement(tgc);
                //
            }
        }
    }

    private void myInitComponents() {
        setButtons();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag2);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;

        // ignored list
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new javax.swing.border.TitledBorder("Nodes ignored"));
        listIgnored = new JList<TGComponent>(ign);
        // listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(200, 250));
        c.add(panel1, c2);

        // central buttons
        panel3 = new JPanel();
        panel3.setLayout(gridbag1);

        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridheight = 1;

        allValidated = new JButton(IconManager.imgic50);
        allValidated.setPreferredSize(new Dimension(50, 25));
        allValidated.addActionListener(this);
        allValidated.setActionCommand("allValidated");
        panel3.add(allValidated, c1);

        addOneValidated = new JButton(IconManager.imgic48);
        addOneValidated.setPreferredSize(new Dimension(50, 25));
        addOneValidated.addActionListener(this);
        addOneValidated.setActionCommand("addOneValidated");
        panel3.add(addOneValidated, c1);

        panel3.add(new JLabel(" "), c1);

        addOneIgnored = new JButton(IconManager.imgic46);
        addOneIgnored.addActionListener(this);
        addOneIgnored.setPreferredSize(new Dimension(50, 25));
        addOneIgnored.setActionCommand("addOneIgnored");
        panel3.add(addOneIgnored, c1);

        allIgnored = new JButton(IconManager.imgic44);
        allIgnored.addActionListener(this);
        allIgnored.setPreferredSize(new Dimension(50, 25));
        allIgnored.setActionCommand("allIgnored");
        panel3.add(allIgnored, c1);

        c.add(panel3, c2);

        // validated list
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Nodes taken into account"));
        listValidated = new JList<TGComponent>(val);
        // listValidated.setPreferredSize(new Dimension(200, 250));
        listValidated.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValidated.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listValidated);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(200, 250));
        c2.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.add(panel2, c2);

        optimize = new JCheckBox("Optimize TML specification");
        optimize.setSelected(optimized);
        c.add(optimize, c2);
        c2.gridwidth = 1; // end row
        c.add(new JLabel("Master clock (in MHz)"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; // end row
        clockField = new JTextField("" + clock);
        c.add(clockField, c2);

        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridwidth = 1; // end row
        initMainButtons(c2, c, this, false, "Check syntax", "Cancel");

        // main panel;
        /*
         * panel6 = new JPanel(); panel6.setLayout(new BorderLayout());
         * 
         * panel5 = new JPanel(); //GridBagLayout gridbag3 = new GridBagLayout();
         * GridBagConstraints c3 = new GridBagConstraints(); panel5.setLayout(gridbag2);
         * 
         * c3.weighty = 1.0; c3.weightx = 1.0; c3.fill = GridBagConstraints.HORIZONTAL;
         * c3.gridheight = 1;
         * 
         * c3.gridwidth = GridBagConstraints.REMAINDER; //end row optimize = new
         * JCheckBox("Optimize TML specification"); optimize.setSelected(optimized);
         * panel5.add(optimize, c3);
         * 
         * c3.gridwidth = 1; //end row panel5.add(new JLabel("Master clock (in MHz)"),
         * c3); c3.gridwidth = GridBagConstraints.REMAINDER; //end row clockField = new
         * JTextField("" + clock); panel5.add(clockField, c3);
         * 
         * panel4 = new JPanel(); panel4.setLayout(new FlowLayout());
         * 
         * closeButton = new JButton("Start Syntax Analysis", IconManager.imgic37);
         * //closeButton.setPreferredSize(new Dimension(600, 50));
         * closeButton.addActionListener(this); closeButton.setPreferredSize(new
         * Dimension(200, 30));
         * 
         * cancelButton = new JButton("Cancel", IconManager.imgic27);
         * cancelButton.addActionListener(this); cancelButton.setPreferredSize(new
         * Dimension(200, 30)); panel4.add(cancelButton); panel4.add(closeButton);
         * 
         * panel6.add(panel5, BorderLayout.NORTH); panel6.add(panel4,
         * BorderLayout.SOUTH);
         * 
         * c.add(panel6, c2);
         */

    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (evt.getSource() == closeButton) {
            closeDialog();
            return;
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
            return;
        }

        // Compare the action command to the known actions.
        if (command.equals("addOneIgnored")) {
            addOneIgnored();
        } else if (command.equals("addOneValidated")) {
            addOneValidated();
        } else if (command.equals("allValidated")) {
            allValidated();
        } else if (command.equals("allIgnored")) {
            allIgnored();
        }
    }

    private void addOneIgnored() {
        int[] list = listValidated.getSelectedIndices();
        Vector<TGComponent> v = new Vector<TGComponent>();
        TGComponent o;
        for (int i = 0; i < list.length; i++) {
            o = val.elementAt(list[i]);
            ign.addElement(o);
            v.addElement(o);
        }

        val.removeAll(v);
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void addOneValidated() {
        int[] list = listIgnored.getSelectedIndices();
        Vector<TGComponent> v = new Vector<TGComponent>();
        TGComponent o;

        for (int i = 0; i < list.length; i++) {
            o = ign.elementAt(list[i]);
            val.addElement(o);
            v.addElement(o);
        }

        ign.removeAll(v);
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void allValidated() {
        val.addAll(ign);
        ign.removeAllElements();
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    private void allIgnored() {
        ign.addAll(val);
        val.removeAllElements();
        listIgnored.setListData(ign);
        listValidated.setListData(val);
        setButtons();
    }

    public void closeDialog() {
        back.removeAllElements();
        for (int i = 0; i < val.size(); i++) {
            back.addElement(val.elementAt(i));
        }
        validated = val;
        ignored = ign;
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    private void setButtons() {
        int i1 = listIgnored.getSelectedIndex();
        int i2 = listValidated.getSelectedIndex();

        if (i1 == -1) {
            addOneValidated.setEnabled(false);
        } else {
            addOneValidated.setEnabled(true);
            // listValidated.clearSelection();
        }

        if (i2 == -1) {
            addOneIgnored.setEnabled(false);
        } else {
            addOneIgnored.setEnabled(true);
            // listIgnored.clearSelection();
        }

        if (ign.size() == 0) {
            allValidated.setEnabled(false);
        } else {
            allValidated.setEnabled(true);
        }

        if (val.size() == 0) {
            allIgnored.setEnabled(false);
            closeButton.setEnabled(false);
        } else {
            allIgnored.setEnabled(true);
            closeButton.setEnabled(true);
        }
    }

    public int getClock() {
        try {
            int c = Integer.decode(clockField.getText()).intValue();
            if (c > 0) {
                return c;
            }
        } catch (Exception e) {
        }
        return clock;
    }

    public void valueChanged(ListSelectionEvent e) {
        setButtons();
    }

    public boolean getOptimize() {
        return optimized;
    }
}

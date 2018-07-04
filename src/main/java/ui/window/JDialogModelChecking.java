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

import ui.TClassInterface;
import ui.TGComponent;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class JDialogModelChecking
 * Dialog for managing Tclasses to be validated
 * Creation: 13/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 13/12/2003
 */
public class JDialogModelChecking extends JDialogBase implements ActionListener, ListSelectionListener {
    public static java.util.List<TClassInterface> validated, ignored;
    private static boolean overideSyntaxChecking = false;

    private java.util.List<TClassInterface> val, ign, back;

    //subpanels
    private JPanel panel1, panel2, panel3, panel4, panel5, panel6;
    private JList<TClassInterface> listIgnored;
    private JList<TClassInterface> listValidated;
    private JButton allValidated;
    private JButton addOneValidated;
    private JButton addOneIgnored;
    private JButton allIgnored;
    protected JCheckBox syntax;

    /**
     * Creates new form
     */
    public JDialogModelChecking(Frame f, LinkedList<TClassInterface> _back, java.util.List<TGComponent> componentList, String title) {
        super(f, title, true);

        back = _back;

        if ((validated == null) || (ignored == null)) {
            val = makeNewVal(componentList);
            //
            ign = new LinkedList<TClassInterface>();
        } else {
            val = validated;
            ign = ignored;
            this.checkTClasses(val, componentList);
            this.checkTClasses(ign, componentList);
            addNewTclasses(val, componentList, ign);
        }

        initComponents();
        myInitComponents();
        pack();
    }

    private java.util.List<TClassInterface> makeNewVal(java.util.List<TGComponent> list) {
        java.util.List<TClassInterface> v = new LinkedList<TClassInterface>();

        for (TGComponent tgc : list)
            if (tgc instanceof TClassInterface)
                v.add((TClassInterface) tgc);

        return v;
    }

    private void checkTClasses(java.util.List<TClassInterface> tobeChecked, java.util.List<TGComponent> source) {
        Iterator<TClassInterface> iter = tobeChecked.iterator();
        while (iter.hasNext()) {
            TClassInterface t = iter.next();
            if (!source.contains(t))
                iter.remove();
        }
    }

    public void addNewTclasses(java.util.List<TClassInterface> added, java.util.List<TGComponent> source, java.util.List<TClassInterface> notSource) {
        for (TGComponent tgc : source)
            if ((tgc instanceof TClassInterface) && (!added.contains(tgc)) && (!notSource.contains(tgc)))
                added.add((TClassInterface) tgc);
    }

    private void myInitComponents() {
        setButtons();
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        //c.setLayout(new BorderLayout());
        c.setLayout(gridbag0);
        JPanel mainPanel = new JPanel(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ignored list
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new javax.swing.border.TitledBorder("Ignored"));
        listIgnored = new JList<TClassInterface>(ign.toArray(new TClassInterface[0]));
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(200, 250));
        mainPanel.add(panel1, BorderLayout.WEST);

        // validated list
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Taken into account"));
        listValidated = new JList<TClassInterface>(val.toArray(new TClassInterface[0]));
        //listValidated.setPreferredSize(new Dimension(200, 250));
        listValidated.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValidated.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listValidated);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(200, 250));
        mainPanel.add(panel2, BorderLayout.EAST);

        // central buttons
        panel3 = new JPanel();
        panel3.setLayout(gridbag1);

        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
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

        mainPanel.add(panel3, BorderLayout.CENTER);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        panel6 = new JPanel();
        panel6.setLayout(new BorderLayout());

        panel5 = new JPanel();
        panel5.setLayout(new FlowLayout());

        syntax = new JCheckBox("Overide TIF syntax checking");
        syntax.setSelected(overideSyntaxChecking);
        panel5.add(syntax);

        panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());

        closeButton = new JButton("Start Syntax Analysis", IconManager.imgic37);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        closeButton.setPreferredSize(new Dimension(200, 30));

        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(200, 30));
        panel4.add(cancelButton);
        panel4.add(closeButton);

        panel6.add(panel5, BorderLayout.NORTH);
        panel6.add(panel4, BorderLayout.SOUTH);

        c.add(mainPanel, c0);
        c.add(panel6, c0);

    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Start Syntax Analysis")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("addOneIgnored")) {
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
        for (TClassInterface c : this.listValidated.getSelectedValuesList()) {
            ign.add(c);
            val.remove(c);
        }

        listIgnored.setListData(ign.toArray(new TClassInterface[0]));
        listValidated.setListData(val.toArray(new TClassInterface[0]));
        this.setButtons();
    }

    private void addOneValidated() {
        for (TClassInterface c : this.listIgnored.getSelectedValuesList()) {
            ign.remove(c);
            val.add(c);
        }

        listIgnored.setListData(ign.toArray(new TClassInterface[0]));
        listValidated.setListData(val.toArray(new TClassInterface[0]));
        setButtons();
    }

    private void allValidated() {
        val.addAll(ign);
        ign.clear();
        listIgnored.setListData(ign.toArray(new TClassInterface[0]));
        listValidated.setListData(val.toArray(new TClassInterface[0]));
        this.setButtons();
    }

    private void allIgnored() {
        ign.addAll(val);
        val.clear();
        listIgnored.setListData(ign.toArray(new TClassInterface[0]));
        listValidated.setListData(val.toArray(new TClassInterface[0]));
        setButtons();
    }


    public void closeDialog() {
        back.clear();
        for (int i = 0; i < val.size(); i++) {
            back.add(val.get(i));
        }
        validated = val;
        ignored = ign;
        overideSyntaxChecking = syntax.isSelected();
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
            //listValidated.clearSelection();
        }

        if (i2 == -1) {
            addOneIgnored.setEnabled(false);
        } else {
            addOneIgnored.setEnabled(true);
            //listIgnored.clearSelection();
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
            if (nbStart() > 0) {
                closeButton.setEnabled(true);
            } else {
                closeButton.setEnabled(false);
            }
        }
    }

    public int nbStart() {
        int cpt = 0;
        for (TClassInterface t : val)
            if (t.isStart())
                cpt++;

        return cpt;
    }

    public void valueChanged(ListSelectionEvent e) {
        setButtons();
    }

    public boolean getOverideSyntaxChecking() {
        return overideSyntaxChecking;
    }
}

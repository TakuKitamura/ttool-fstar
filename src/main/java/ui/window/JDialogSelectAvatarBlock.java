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

import ui.avatarbd.AvatarBDStateMachineOwner;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Class JDialogSelectAvatarBlock
 * Dialog for managing blocks to be validated
 * Creation: 18/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 18/05/2010
 */
public class JDialogSelectAvatarBlock extends JDialogBase implements ActionListener, ListSelectionListener {
    
	public List<AvatarBDStateMachineOwner> validated, ignored;
    private boolean optimized = true;
    private boolean considerTimingOperators = false;

    private List<AvatarBDStateMachineOwner> val, ign, back;

    private JList<AvatarBDStateMachineOwner> listIgnored;
    private JList<AvatarBDStateMachineOwner> listValidated;
    private JButton allValidated;
    private JButton addOneValidated;
    private JButton addOneIgnored;
    private JButton allIgnored;
    protected JCheckBox optimize;
    protected JCheckBox considerTimingOperatorsBox;

    private boolean hasBeenCancelled = true;

    /*
     * Creates new form
     */
    public JDialogSelectAvatarBlock(Frame f, List<AvatarBDStateMachineOwner> _back, List<AvatarBDStateMachineOwner> componentList, String title, List<AvatarBDStateMachineOwner> _validated, List<AvatarBDStateMachineOwner> _ignored,
                                    boolean _optimized, boolean _considerTimingOperators) {
        super(f, title, true);

        back = _back;
        validated = _validated;
        ignored = _ignored;
        considerTimingOperators = _considerTimingOperators;
        optimized = _optimized;

        if ((validated == null) || (ignored == null)) {
            val = new LinkedList<>(componentList);
            ign = new LinkedList<>();
        } else {
            val = validated;
            ign = ignored;
            checkTask(val, componentList);
            checkTask(ign, componentList);
            addNewTask(val, componentList, ign);
        }

        initComponents();
        myInitComponents();
        pack();
    }

    private void checkTask( List<AvatarBDStateMachineOwner> tobeChecked, List<AvatarBDStateMachineOwner> source) {
        Iterator<AvatarBDStateMachineOwner> iterator = tobeChecked.iterator();

        while (iterator.hasNext()) {
            AvatarBDStateMachineOwner t = iterator.next();
            if (!source.contains(t))
                iterator.remove();
        }
    }

    private void addNewTask( List<AvatarBDStateMachineOwner> added, List<AvatarBDStateMachineOwner> source, List<AvatarBDStateMachineOwner> notSource) {
        for (AvatarBDStateMachineOwner tgc : source)
            if (!added.contains(tgc) && !notSource.contains(tgc))
                added.add(tgc);
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
        //c.setLayout(new BorderLayout());
        c.setLayout(gridbag2);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;

        // ignored list
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new javax.swing.border.TitledBorder("Blocks ignored"));
        listIgnored = new JList<>(ign.toArray(new AvatarBDStateMachineOwner[0]));
        //listIgnored.setPreferredSize(new Dimension(200, 250));
        listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listIgnored.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listIgnored);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(200, 250));
        c.add(panel1, c2);


        // central buttons
        JPanel panel3 = new JPanel();
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

        c.add(panel3, c2);


        // validated list
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new javax.swing.border.TitledBorder("Blocks taken into account"));
        listValidated = new JList<>(val.toArray(new AvatarBDStateMachineOwner[0]));
        //listValidated.setPreferredSize(new Dimension(200, 250));
        listValidated.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listValidated.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listValidated);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.setPreferredSize(new Dimension(200, 250));
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c2);


        optimize = new JCheckBox("Optimize specification");
        optimize.setSelected(optimized);
        c.add(optimize, c2);
        considerTimingOperatorsBox = new JCheckBox("Take into account time operators");
        considerTimingOperatorsBox.setSelected(considerTimingOperators);
        c.add(considerTimingOperatorsBox, c2);
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridwidth = 1; //end row
        initMainButtons(c2, c, this, false, "Check syntax", "Cancel");

        /*closeButton = new JButton("Start Syntax Analysis", IconManager.imgic37);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        closeButton.setPreferredSize(new Dimension(200, 30));

        JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());
        JButton cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(200, 30));
        panel4.add(cancelButton);
        panel4.add(closeButton);
        c.add(panel4, c2);*/


        // main panel;
        /*JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout());

        JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout());

        optimize = new JCheckBox("Optimize specification");
        optimize.setSelected(optimized);
        panel5.add(optimize);

        JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout());

        closeButton = new JButton("Start Syntax Analysis", IconManager.imgic37);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        closeButton.setPreferredSize(new Dimension(200, 30));

        JButton cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(200, 30));
        panel4.add(cancelButton);
        panel4.add(closeButton);

        panel6.add(panel5, BorderLayout.NORTH);
        panel6.add(panel4, BorderLayout.SOUTH);

        c.add(panel6, c2);*/

    }

    @Override
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
        switch (command) {
            case "addOneIgnored":
                addOneIgnored();
                break;
            case "addOneValidated":
                addOneValidated();
                break;
            case "allValidated":
                allValidated();
                break;
            case "allIgnored":
                allIgnored();
                break;
        }
    }

    private void addOneIgnored() {
        for (AvatarBDStateMachineOwner o : this.listValidated.getSelectedValuesList()) {
            ign.add(o);
            val.remove(o);
        }

        listIgnored.setListData(ign.toArray(new AvatarBDStateMachineOwner[0]));
        listValidated.setListData(val.toArray(new AvatarBDStateMachineOwner[0]));
        setButtons();
    }

    private void addOneValidated() {
        for (AvatarBDStateMachineOwner o : this.listIgnored.getSelectedValuesList()) {
            val.add(o);
            ign.remove(o);
        }

        listIgnored.setListData(ign.toArray(new AvatarBDStateMachineOwner[0]));
        listValidated.setListData(val.toArray(new AvatarBDStateMachineOwner[0]));
        setButtons();
    }

    private void allValidated() {
        val.addAll(ign);
        ign.clear();
        listIgnored.setListData(ign.toArray(new AvatarBDStateMachineOwner[0]));
        listValidated.setListData(val.toArray(new AvatarBDStateMachineOwner[0]));
        setButtons();
    }

    private void allIgnored() {
        ign.addAll(val);
        val.clear();
        listIgnored.setListData(ign.toArray(new AvatarBDStateMachineOwner[0]));
        listValidated.setListData(val.toArray(new AvatarBDStateMachineOwner[0]));
        setButtons();
    }

    public void closeDialog() {
        back.clear();
        back.addAll(val);

        validated = val;
        ignored = ign;
        optimized = optimize.isSelected();
        considerTimingOperators = considerTimingOperatorsBox.isSelected();

        hasBeenCancelled = false;
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
            closeButton.setEnabled(true);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        setButtons();
    }

    public boolean getOptimized() {
        return optimized;
    }

    public boolean getConsiderTimingOperators() {
        return considerTimingOperators;
    }

    public boolean hasBeenCancelled() {
        return hasBeenCancelled;
    }

    public List<AvatarBDStateMachineOwner> getValidated() {
        return validated;
    }

    public List<AvatarBDStateMachineOwner> getIgnored() {
        return ignored;
    }
}

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

import ui.util.IconManager;
import ui.ftd.FTDFault;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;

/**
 * Class JDialogFault Dialog for managing fault attributes Creation: 14/12/2009
 * 
 * @version 1.0 14/12/2009
 * @author Ludovic APVRILLE
 */
public class JDialogFault extends JDialogBase implements ActionListener {

    private boolean regularClose;

    private JPanel panel2;
    private Frame frame;

    // protected JTextField taskName;
    protected JTextField name, description, probability;
    protected JCheckBox isRootFault;

    private FTDFault fault;

    /* Creates new form */
    public JDialogFault(Frame _frame, String _title, FTDFault _fault) {
        super(_frame, _title, true);
        frame = _frame;
        fault = _fault;

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Fault attributes"));
        panel2.setPreferredSize(new Dimension(350, 250));

        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        isRootFault = new JCheckBox("root fault");
        isRootFault.setSelected(fault.isRootFault());
        panel2.add(isRootFault, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Probability (<1):"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        probability = new JTextField("" + fault.getProbability());
        panel2.add(probability, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Name:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        name = new JTextField(fault.getFaultName());
        panel2.add(name, c1);

        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Description:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        description = new JTextField(fault.getDescription());
        panel2.add(description, c1);

        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        c0.fill = GridBagConstraints.BOTH;
        c.add(panel2, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt) {
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

    public String getDescription() {
        return description.getText();
    }

    public String getProbability() {
        return probability.getText();
    }

    public String getName() {
        return name.getText();
    }

    public boolean isRootFault() {
        return isRootFault.isSelected();
    }

}

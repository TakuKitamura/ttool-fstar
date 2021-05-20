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

import ui.eln.sca_eln.ELNComponentInductor;
import ui.util.IconManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Class JDialogELNComponentInductor Dialog for managing of ELN inductor
 * Creation: 12/06/2018
 * 
 * @version 1.0 12/06/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogELNComponentInductor extends JDialog implements ActionListener {

    /** Access to ActionPerformed **/
    private JTextField nameTextField;
    private JTextField valueTextField;
    private JTextField fluxTextField;
    private String valueListString[];
    private JComboBox<String> valueComboBoxString;
    private String fluxListString[];
    private JComboBox<String> fluxComboBoxString;

    /** Parameters **/
    private ELNComponentInductor l;

    /* Constructor **/
    public JDialogELNComponentInductor(ELNComponentInductor _l) {
        /** Set JDialog **/
        setTitle("Setting the inductor");
        setLocationRelativeTo(null);
        setVisible(true);
        setAlwaysOnTop(true);
        setResizable(false);

        /** Parameters **/
        l = _l;

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        dialog();
    }

    public void dialog() {
        /** JPanel **/
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.add(mainPanel);

        JPanel attributesMainPanel = new JPanel(new GridLayout());
        mainPanel.add(attributesMainPanel, BorderLayout.NORTH);

        // Left Side
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createTitledBorder("Setting inductor attributes"));

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel boxPanel = new JPanel();
        boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        boxPanel.setLayout(gridBag);

        JLabel labelName = new JLabel("nm : ");
        constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(labelName, constraints);
        boxPanel.add(labelName);

        nameTextField = new JTextField(l.getValue().toString(), 10); // name not empty
        constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(nameTextField, constraints);
        boxPanel.add(nameTextField);

        JLabel valueLabel = new JLabel("value : ");
        constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(valueLabel, constraints);
        boxPanel.add(valueLabel);

        valueTextField = new JTextField("" + l.getVal(), 10); // name not empty
        constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(valueTextField, constraints);
        boxPanel.add(valueTextField);

        valueListString = new String[9];
        valueListString[0] = "GH";
        valueListString[1] = "MH";
        valueListString[2] = "kH";
        valueListString[3] = "H";
        valueListString[4] = "mH";
        valueListString[5] = "\u03BCH";
        valueListString[6] = "nH";
        valueListString[7] = "pH";
        valueListString[8] = "fH";
        valueComboBoxString = new JComboBox<String>(valueListString);
        if (l.getUnit0().equals("GH")) {
            valueComboBoxString.setSelectedIndex(0);
        } else if (l.getUnit0().equals("MH")) {
            valueComboBoxString.setSelectedIndex(1);
        } else if (l.getUnit0().equals("kH")) {
            valueComboBoxString.setSelectedIndex(2);
        } else if (l.getUnit0().equals("H")) {
            valueComboBoxString.setSelectedIndex(3);
        } else if (l.getUnit0().equals("mH")) {
            valueComboBoxString.setSelectedIndex(4);
        } else if (l.getUnit0().equals("\u03BCH")) {
            valueComboBoxString.setSelectedIndex(5);
        } else if (l.getUnit0().equals("nH")) {
            valueComboBoxString.setSelectedIndex(6);
        } else if (l.getUnit0().equals("pH")) {
            valueComboBoxString.setSelectedIndex(7);
        } else if (l.getUnit0().equals("fH")) {
            valueComboBoxString.setSelectedIndex(8);
        }
        valueComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(valueComboBoxString, constraints);
        boxPanel.add(valueComboBoxString);

        JLabel fluxLabel = new JLabel("phi0 : ");
        constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(fluxLabel, constraints);
        boxPanel.add(fluxLabel);

        fluxTextField = new JTextField("" + l.getPhi0(), 10); // name not empty
        constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(fluxTextField, constraints);
        boxPanel.add(fluxTextField);

        fluxListString = new String[9];
        fluxListString[0] = "GWb";
        fluxListString[1] = "MWb";
        fluxListString[2] = "kWb";
        fluxListString[3] = "Wb";
        fluxListString[4] = "mWb";
        fluxListString[5] = "\u03BCWb";
        fluxListString[6] = "nWb";
        fluxListString[7] = "pWb";
        fluxListString[8] = "fWb";
        fluxComboBoxString = new JComboBox<String>(fluxListString);
        if (l.getUnit1().equals("GWb")) {
            fluxComboBoxString.setSelectedIndex(0);
        } else if (l.getUnit1().equals("MWb")) {
            fluxComboBoxString.setSelectedIndex(1);
        } else if (l.getUnit1().equals("kWb")) {
            fluxComboBoxString.setSelectedIndex(2);
        } else if (l.getUnit1().equals("Wb")) {
            fluxComboBoxString.setSelectedIndex(3);
        } else if (l.getUnit1().equals("mWb")) {
            fluxComboBoxString.setSelectedIndex(4);
        } else if (l.getUnit1().equals("\u03BCWb")) {
            fluxComboBoxString.setSelectedIndex(5);
        } else if (l.getUnit1().equals("nWb")) {
            fluxComboBoxString.setSelectedIndex(6);
        } else if (l.getUnit1().equals("pWb")) {
            fluxComboBoxString.setSelectedIndex(7);
        } else if (l.getUnit1().equals("fWb")) {
            fluxComboBoxString.setSelectedIndex(8);
        }
        fluxComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(fluxComboBoxString, constraints);
        boxPanel.add(fluxComboBoxString);

        box.add(boxPanel); // add border to box
        attributesMainPanel.add(box); // add grid to grid

        // Down Side
        JPanel downPanel = new JPanel(new FlowLayout());

        JButton saveCloseButton = new JButton("Save and close");
        saveCloseButton.setIcon(IconManager.imgic25);
        saveCloseButton.setActionCommand("Save_Close");
        saveCloseButton.addActionListener(this);
        saveCloseButton.setPreferredSize(new Dimension(200, 30));
        downPanel.add(saveCloseButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setIcon(IconManager.imgic27);
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(new Dimension(200, 30));
        downPanel.add(cancelButton);

        mainPanel.add(downPanel, BorderLayout.CENTER);
        pack();
        this.getRootPane().setDefaultButton(saveCloseButton);
    }

    public void actionPerformed(ActionEvent e) {
        if ("Save_Close".equals(e.getActionCommand())) {
            l.setValue(new String(nameTextField.getText()));

            if (!(valueTextField.getText().isEmpty())) {
                Boolean valValueDouble = false;
                try {
                    Double.parseDouble(valueTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The inductance is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    valValueDouble = true;
                }
                if (valValueDouble == false && Double.parseDouble(valueTextField.getText()) != 0.0) {
                    l.setVal(Double.parseDouble(valueTextField.getText()));
                } else if (valValueDouble == false && Double.parseDouble(valueTextField.getText()) == 0.0) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The inductance shall not be numerically zero", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    valValueDouble = true;
                    l.setVal(1.0);
                }
            } else {
                l.setVal(1.0);
            }
            l.setUnit0((String) valueComboBoxString.getSelectedItem());

            if (!(fluxTextField.getText().isEmpty())) {
                Boolean fluxValueDouble = false;
                try {
                    Double.parseDouble(fluxTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The initial magnetic flux is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    fluxValueDouble = true;
                }
                if (fluxValueDouble == false) {
                    l.setPhi0(Double.parseDouble(fluxTextField.getText()));
                }
            } else {
                l.setPhi0(0.0);
            }
            l.setUnit1((String) fluxComboBoxString.getSelectedItem());

            this.dispose();
        }

        if ("Cancel".equals(e.getActionCommand())) {
            this.dispose();
        }
    }
}

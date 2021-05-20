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

import ui.eln.sca_eln.ELNComponentIndependentVoltageSource;
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
import java.util.regex.Pattern;

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
 * Class JDialogELNComponentIndependentVoltageSource Dialog for managing of ELN
 * independent voltage source Creation: 15/06/2018
 * 
 * @version 1.0 15/06/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogELNComponentIndependentVoltageSource extends JDialog implements ActionListener {

    /** Access to ActionPerformed **/
    private JTextField nameTextField;
    private JTextField initValueTextField;
    private JTextField offsetTextField;
    private JTextField amplitudeTextField;
    private JTextField frequencyTextField;
    private String frequencyListString[];
    private JComboBox<String> frequencyComboBoxString;
    private JTextField phaseTextField;
    private JTextField delayTextField;
    private JTextField acAmplitudeTextField;
    private JTextField acPhaseTextField;
    private JTextField acNoiseAmplitudeTextField;

    /** Parameters **/
    private ELNComponentIndependentVoltageSource vsource;

    /* Constructor **/
    public JDialogELNComponentIndependentVoltageSource(ELNComponentIndependentVoltageSource _vsource) {
        /** Set JDialog **/
        setTitle("Setting the independent voltage source");
        setLocationRelativeTo(null);
        setVisible(true);
        setAlwaysOnTop(true);
        setResizable(false);

        /** Parameters **/
        vsource = _vsource;

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
        box.setBorder(BorderFactory.createTitledBorder("Setting independent voltage source attributes"));

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel boxPanel = new JPanel();
        boxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        boxPanel.setLayout(gridBag);

        JLabel nameLabel = new JLabel("nm : ");
        constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(nameLabel, constraints);
        boxPanel.add(nameLabel);

        nameTextField = new JTextField(vsource.getValue().toString(), 10); // name not empty
        constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(nameTextField, constraints);
        boxPanel.add(nameTextField);

        JLabel initValLabel = new JLabel("init_value : ");
        constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(initValLabel, constraints);
        boxPanel.add(initValLabel);

        initValueTextField = new JTextField("" + vsource.getInitValue(), 10);
        constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(initValueTextField, constraints);
        boxPanel.add(initValueTextField);

        JLabel offsetLabel = new JLabel("offset : ");
        constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(offsetLabel, constraints);
        boxPanel.add(offsetLabel);

        offsetTextField = new JTextField("" + vsource.getOffset(), 10);
        constraints = new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(offsetTextField, constraints);
        boxPanel.add(offsetTextField);

        JLabel amplitudeLabel = new JLabel("amplitude : ");
        constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(amplitudeLabel, constraints);
        boxPanel.add(amplitudeLabel);

        amplitudeTextField = new JTextField("" + vsource.getAmplitude(), 10);
        constraints = new GridBagConstraints(1, 3, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(amplitudeTextField, constraints);
        boxPanel.add(amplitudeTextField);

        JLabel frequencyLabel = new JLabel("frequency : ");
        constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(frequencyLabel, constraints);
        boxPanel.add(frequencyLabel);

        frequencyTextField = new JTextField("" + vsource.getFrequency(), 10);
        constraints = new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(frequencyTextField, constraints);
        boxPanel.add(frequencyTextField);

        frequencyListString = new String[9];
        frequencyListString[0] = "GHz";
        frequencyListString[1] = "MHz";
        frequencyListString[2] = "kHz";
        frequencyListString[3] = "Hz";
        frequencyListString[4] = "mHz";
        frequencyListString[5] = "\u03BCHz";
        frequencyListString[6] = "nHz";
        frequencyListString[7] = "pHz";
        frequencyListString[8] = "fHz";
        frequencyComboBoxString = new JComboBox<String>(frequencyListString);
        if (vsource.getUnit0().equals("GHz")) {
            frequencyComboBoxString.setSelectedIndex(0);
        } else if (vsource.getUnit0().equals("MHz")) {
            frequencyComboBoxString.setSelectedIndex(1);
        } else if (vsource.getUnit0().equals("kHz")) {
            frequencyComboBoxString.setSelectedIndex(2);
        } else if (vsource.getUnit0().equals("Hz")) {
            frequencyComboBoxString.setSelectedIndex(3);
        } else if (vsource.getUnit0().equals("mHz")) {
            frequencyComboBoxString.setSelectedIndex(4);
        } else if (vsource.getUnit0().equals("\u03BCHz")) {
            frequencyComboBoxString.setSelectedIndex(5);
        } else if (vsource.getUnit0().equals("nHz")) {
            frequencyComboBoxString.setSelectedIndex(6);
        } else if (vsource.getUnit0().equals("pHz")) {
            frequencyComboBoxString.setSelectedIndex(7);
        } else if (vsource.getUnit0().equals("fHz")) {
            frequencyComboBoxString.setSelectedIndex(8);
        }
        frequencyComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(frequencyComboBoxString, constraints);
        boxPanel.add(frequencyComboBoxString);

        JLabel phaseLabel = new JLabel("phase : ");
        constraints = new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(phaseLabel, constraints);
        boxPanel.add(phaseLabel);

        phaseTextField = new JTextField("" + vsource.getPhase(), 10); // name not empty
        constraints = new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(phaseTextField, constraints);
        boxPanel.add(phaseTextField);

        JLabel phaseRadLabel = new JLabel("rad ");
        constraints = new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(phaseRadLabel, constraints);
        boxPanel.add(phaseRadLabel);

        JLabel delayLabel = new JLabel("delay : ");
        constraints = new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(delayLabel, constraints);
        boxPanel.add(delayLabel);

        delayTextField = new JTextField("" + vsource.getDelay(), 10);
        constraints = new GridBagConstraints(1, 6, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(delayTextField, constraints);
        boxPanel.add(delayTextField);

        JLabel acAmplitudeLabel = new JLabel("ac_amplitude : ");
        constraints = new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acAmplitudeLabel, constraints);
        boxPanel.add(acAmplitudeLabel);

        acAmplitudeTextField = new JTextField("" + vsource.getAcAmplitude(), 10);
        constraints = new GridBagConstraints(1, 7, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acAmplitudeTextField, constraints);
        boxPanel.add(acAmplitudeTextField);

        JLabel acPhaseLabel = new JLabel("ac_phase : ");
        constraints = new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acPhaseLabel, constraints);
        boxPanel.add(acPhaseLabel);

        acPhaseTextField = new JTextField("" + vsource.getAcPhase(), 10); // name not empty
        constraints = new GridBagConstraints(1, 8, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acPhaseTextField, constraints);
        boxPanel.add(acPhaseTextField);

        JLabel acPhaseRadLabel = new JLabel("rad ");
        constraints = new GridBagConstraints(2, 8, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acPhaseRadLabel, constraints);
        boxPanel.add(acPhaseRadLabel);

        JLabel acNoiseAmplitudeLabel = new JLabel("ac_noise_amplitude : ");
        constraints = new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acNoiseAmplitudeLabel, constraints);
        boxPanel.add(acNoiseAmplitudeLabel);

        acNoiseAmplitudeTextField = new JTextField("" + vsource.getAcNoiseAmplitude(), 10);
        constraints = new GridBagConstraints(1, 9, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 5, 10), 0, 0);
        gridBag.setConstraints(acNoiseAmplitudeTextField, constraints);
        boxPanel.add(acNoiseAmplitudeTextField);

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
            vsource.setValue(new String(nameTextField.getText()));

            if (!(initValueTextField.getText().isEmpty())) {
                Boolean initValValueDouble = false;
                try {
                    Double.parseDouble(initValueTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The initial value is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    initValValueDouble = true;
                }
                if (initValValueDouble == false) {
                    vsource.setInitValue(Double.parseDouble(initValueTextField.getText()));
                }
            } else {
                vsource.setInitValue(0.0);
            }

            if (!(offsetTextField.getText().isEmpty())) {
                Boolean offsetValueDouble = false;
                try {
                    Double.parseDouble(offsetTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The offset value is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    offsetValueDouble = true;
                }
                if (offsetValueDouble == false) {
                    vsource.setOffset(Double.parseDouble(offsetTextField.getText()));
                }
            } else {
                vsource.setOffset(0.0);
            }

            if (!(amplitudeTextField.getText().isEmpty())) {
                Boolean amplitudeValueDouble = false;
                try {
                    Double.parseDouble(amplitudeTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The source amplitude is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    amplitudeValueDouble = true;
                }
                if (amplitudeValueDouble == false) {
                    vsource.setAmplitude(Double.parseDouble(amplitudeTextField.getText()));
                }
            } else {
                vsource.setAmplitude(0.0);
            }

            if (!(frequencyTextField.getText().isEmpty())) {
                Boolean frequencyValueDouble = false;
                try {
                    Double.parseDouble(frequencyTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The source frequency is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    frequencyValueDouble = true;
                }
                if (frequencyValueDouble == false) {
                    vsource.setFrequency(Double.parseDouble(frequencyTextField.getText()));
                }
            } else {
                vsource.setFrequency(0.0);
            }

            vsource.setUnit0((String) frequencyComboBoxString.getSelectedItem());

            if (!(phaseTextField.getText().isEmpty())) {
                Boolean phaseValueDouble = false;
                try {
                    Double.parseDouble(phaseTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The source phase is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    phaseValueDouble = true;
                }
                if (phaseValueDouble == false) {
                    vsource.setPhase(Double.parseDouble(phaseTextField.getText()));
                }
            } else {
                vsource.setPhase(0.0);
            }

            String a = delayTextField.getText().split(Pattern.quote("("))[1].split(",")[0];
            String b = delayTextField.getText().split(Pattern.quote("("))[1].split(",")[1].split(Pattern.quote(")"))[0]
                    .split(" ")[1];

            if (delayTextField.getText() == "sc_core::SC_ZERO_TIME"
                    || (Double.parseDouble(a) >= 1.0) && b.equals("sc_core::SC_SEC")) {
                vsource.setDelay(delayTextField.getText());
            }

            if (!(acAmplitudeTextField.getText().isEmpty())) {
                Boolean acAmplitudeValueDouble = false;
                try {
                    Double.parseDouble(acAmplitudeTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The small-signal amplitude is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    acAmplitudeValueDouble = true;
                }
                if (acAmplitudeValueDouble == false) {
                    vsource.setAcAmplitude(Double.parseDouble(acAmplitudeTextField.getText()));
                }
            } else {
                vsource.setAcAmplitude(0.0);
            }

            if (!(acPhaseTextField.getText().isEmpty())) {
                Boolean acPhaseValueDouble = false;
                try {
                    Double.parseDouble(acPhaseTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The small-signal phase is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    acPhaseValueDouble = true;
                }
                if (acPhaseValueDouble == false) {
                    vsource.setAcPhase(Double.parseDouble(acPhaseTextField.getText()));
                }
            } else {
                vsource.setAcPhase(0.0);
            }

            if (!(acNoiseAmplitudeTextField.getText().isEmpty())) {
                Boolean acNoiseAmplitudeValueDouble = false;
                try {
                    Double.parseDouble(acNoiseAmplitudeTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "The small-signal noise amplitude is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    acNoiseAmplitudeValueDouble = true;
                }
                if (acNoiseAmplitudeValueDouble == false) {
                    vsource.setAcNoiseAmplitude(Double.parseDouble(acNoiseAmplitudeTextField.getText()));
                }
            } else {
                vsource.setAcNoiseAmplitude(0.0);
            }

            this.dispose();
        }

        if ("Cancel".equals(e.getActionCommand())) {
            this.dispose();
        }
    }
}

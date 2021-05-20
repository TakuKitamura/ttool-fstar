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

import ui.syscams.SysCAMSClock;
import ui.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Class JDialogSysCAMSClock Dialog for managing of SystemC-AMS Clock Creation:
 * 04/06/2019
 * 
 * @version 1.0 04/06/2016
 * @author Daniela GENIUS
 */

@SuppressWarnings("serial")

public class JDialogSysCAMSClock extends JDialog implements ActionListener {

    private JTextField nameTextField;

    private JComboBox<String> unitComboBoxString;

    private JTextField posFirstTextField;
    private String listPosFirstString[];
    private JComboBox<String> posFirstComboBoxString;

    private JTextField frequencyTextField;
    private String listFrequencyString[];
    private JComboBox<String> frequencyComboBoxString;

    private JTextField dutyCycleTextField;
    private String listDutyCycleString[];
    private JComboBox<String> dutyCycleComboBoxString;

    private JTextField startTimeTextField;
    private String listStartTimeString[];
    private JComboBox<String> startTimeComboBoxString;

    private JTextField nameStructTextField;
    private JTextField valueStructTextField;
    private JRadioButton constantStructRadioButton;
    private String listTypeStructString[];
    private JComboBox<String> typeStructComboBoxString;
    private ArrayList<String> listTmpStruct;
    private JList<String> structList;
    private DefaultListModel<String> structListModel;
    private boolean structBool = false;
    private JTextField nameTemplateTextField;
    private JTextField valueTemplateTextField;
    private String listTypeTemplateString[];
    private JComboBox<String> typeTemplateComboBoxString;
    private JTextField nameTypedefTextField;
    private String listTypeTypedefString[];
    private JComboBox<String> typeTypedefComboBoxString;
    private JButton addModifyTypedefButton;
    private ArrayList<String> listTmpTypedef;
    private JList<String> typedefList;
    private DefaultListModel<String> typedefListModel;
    private boolean typedefBool = false;

    private JButton upButton, downButton, removeButton;

    private SysCAMSClock clock;

    public JDialogSysCAMSClock(SysCAMSClock clock) {
        this.setTitle("Setting Clock Attributes");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.setResizable(false);

        this.clock = clock;

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        dialog();
    }

    public StringBuffer encode(String data) {
        StringBuffer databuf = new StringBuffer(data);
        StringBuffer buffer = new StringBuffer("");
        int endline = 0;
        int nb_arobase = 0;
        int condition = 0;

        for (int pos = 0; pos != data.length(); pos++) {
            char c = databuf.charAt(pos);
            switch (c) {
                case '\n':
                    break;
                case '\t':
                    break;
                case '{':
                    buffer.append("{\n");
                    endline = 1;
                    nb_arobase++;
                    break;
                case '}':
                    if (nb_arobase == 1) {
                        buffer.append("}\n");
                        endline = 0;
                    } else {
                        int i = nb_arobase;
                        while (i >= 1) {
                            buffer.append("\t");
                            i--;
                        }
                        buffer.append("}\n");
                        endline = 1;
                    }
                    nb_arobase--;
                    break;
                case ';':
                    if (condition == 1) {
                        buffer.append(";");
                    } else {
                        buffer.append(";\n");
                        endline = 1;
                    }
                    break;
                case ' ':
                    if (endline == 0) {
                        buffer.append(databuf.charAt(pos));
                    }
                    break;
                case '(':
                    buffer.append("(");
                    condition = 1;
                    break;
                case ')':
                    buffer.append(")");
                    condition = 0;
                    break;
                default:
                    if (endline == 1) {
                        endline = 0;
                        int i = nb_arobase;
                        while (i >= 1) {
                            buffer.append("\t");
                            i--;
                        }
                    }
                    buffer.append(databuf.charAt(pos));
                    break;
            }
        }
        return buffer;
    }

    public void dialog() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.add(mainPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel attributesMainPanel = new JPanel();
        JPanel parametersMainPanel = new JPanel();
        JPanel processMainPanel = new JPanel();
        JPanel contructorMainPanel = new JPanel();
        tabbedPane.add("Attributes", attributesMainPanel);

        mainPanel.add(tabbedPane, BorderLayout.NORTH);

        // --- Attributes ---//
        attributesMainPanel.setLayout(new BorderLayout());

        Box attributesBox = Box.createVerticalBox();
        attributesBox.setBorder(BorderFactory.createTitledBorder("Setting Clock attributes"));

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel attributesBoxPanel = new JPanel();
        attributesBoxPanel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        attributesBoxPanel.setLayout(gridBag);

        JLabel labelName = new JLabel("Name : ");
        constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(15, 10, 5, 10), 0, 0);
        gridBag.setConstraints(labelName, constraints);
        attributesBoxPanel.add(labelName);

        if (clock.getValue().toString().equals("")) {
            nameTextField = new JTextField(10);
        } else {
            nameTextField = new JTextField(clock.getValue().toString(), 10);
        }
        constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(15, 10, 5, 10), 0, 0);
        gridBag.setConstraints(nameTextField, constraints);
        attributesBoxPanel.add(nameTextField);

        JLabel frequencyLabel = new JLabel("Frequency Tm : ");
        constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(frequencyLabel, constraints);
        attributesBoxPanel.add(frequencyLabel);

        if (clock.getFrequency() == -1) {
            frequencyTextField = new JTextField(10);
        } else {
            frequencyTextField = new JTextField("" + clock.getFrequency(), 10);
        }
        constraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(frequencyTextField, constraints);
        attributesBoxPanel.add(frequencyTextField);

        listFrequencyString = new String[4];
        listFrequencyString[0] = "s";
        listFrequencyString[1] = "ms";
        listFrequencyString[2] = "\u03BCs";
        listFrequencyString[3] = "ns";
        frequencyComboBoxString = new JComboBox<String>(listFrequencyString);
        // clock.setUnit("");
        if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
            frequencyComboBoxString.setSelectedIndex(0);
        } else if (clock.getUnit().equals("ms")) {
            frequencyComboBoxString.setSelectedIndex(1);
        } else if (clock.getUnit().equals("\u03BCs")) {
            frequencyComboBoxString.setSelectedIndex(2);
        } else if (clock.getUnit().equals("ns")) {
            frequencyComboBoxString.setSelectedIndex(3);
        }

        frequencyComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(frequencyComboBoxString, constraints);
        attributesBoxPanel.add(frequencyComboBoxString);

        JLabel dutyCycleLabel = new JLabel("DutyCycle Tm : ");
        constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(dutyCycleLabel, constraints);
        attributesBoxPanel.add(dutyCycleLabel);

        if (clock.getDutyCycle() == -1) {
            dutyCycleTextField = new JTextField(10);
        } else {
            dutyCycleTextField = new JTextField("" + clock.getDutyCycle(), 10);
        }
        constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(dutyCycleTextField, constraints);
        attributesBoxPanel.add(dutyCycleTextField);

        JLabel startTimeLabel = new JLabel("StartTime Tm : ");
        constraints = new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(startTimeLabel, constraints);
        attributesBoxPanel.add(startTimeLabel);

        if (clock.getStartTime() == -1) {
            startTimeTextField = new JTextField(10);
        } else {
            startTimeTextField = new JTextField("" + clock.getStartTime(), 10);
        }
        constraints = new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(startTimeTextField, constraints);
        attributesBoxPanel.add(startTimeTextField);

        listStartTimeString = new String[4];
        listStartTimeString[0] = "s";
        listStartTimeString[1] = "ms";
        listStartTimeString[2] = "\u03BCs";
        listStartTimeString[3] = "ns";
        startTimeComboBoxString = new JComboBox<String>(listStartTimeString);
        if (clock.getUnit().equals("") || clock.getUnit().equals("s")) {
            startTimeComboBoxString.setSelectedIndex(0);
        } else if (clock.getUnit().equals("ms")) {
            startTimeComboBoxString.setSelectedIndex(1);
        } else if (clock.getUnit().equals("\u03BCs")) {
            startTimeComboBoxString.setSelectedIndex(2);
        } else if (clock.getUnit().equals("ns")) {
            startTimeComboBoxString.setSelectedIndex(3);
        }
        startTimeComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(startTimeComboBoxString, constraints);
        attributesBoxPanel.add(startTimeComboBoxString);

        //
        JLabel posFirstLabel = new JLabel("PosFirst Tm : ");
        constraints = new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(posFirstLabel, constraints);
        attributesBoxPanel.add(posFirstLabel);

        if (clock.getPosFirst() == false) {
            posFirstTextField = new JTextField(10);
        } else {
            posFirstTextField = new JTextField("" + clock.getPosFirst(), 10);
        }
        constraints = new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        // gridBag.setConstraints(posFirstTextField, constraints);
        // attributesBoxPanel.add(posFirstTextField);

        listPosFirstString = new String[2];
        listPosFirstString[0] = "false";
        listPosFirstString[1] = "true";

        posFirstComboBoxString = new JComboBox<String>(listPosFirstString);
        if (clock.getUnit().equals("") || clock.getUnit().equals("no")) {
            posFirstComboBoxString.setSelectedIndex(0);
        } else if (clock.getUnit().equals("yes")) {
            posFirstComboBoxString.setSelectedIndex(1);
        }
        posFirstComboBoxString.addActionListener(this);
        constraints = new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 10, 15, 10), 0, 0);
        gridBag.setConstraints(posFirstComboBoxString, constraints);
        attributesBoxPanel.add(posFirstComboBoxString);
        //

        attributesBox.add(attributesBoxPanel);
        attributesMainPanel.add(attributesBox, BorderLayout.NORTH);

        // --- Button --- //
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
        clock.setName(nameTextField.getText());

        if (posFirstComboBoxString.getSelectedIndex() == 0) {
            clock.setPosFirst(false);
        } else {
            clock.setPosFirst(true);
        }

        clock.setUnit((String) frequencyComboBoxString.getSelectedItem());

        clock.setUnitStartTime((String) startTimeComboBoxString.getSelectedItem());

        if ("Save_Close".equals(e.getActionCommand())) {
            clock.setValue(new String(nameTextField.getText()));

            if (!(frequencyTextField.getText().isEmpty())) {
                Boolean frequencyValueInteger = false;
                try {
                    Double.parseDouble(frequencyTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "Frequency is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    frequencyValueInteger = true;
                }
                if (frequencyValueInteger == false) {

                    // if(frequencyComboBoxString.getSelectedIndex()==0)frequencyComboBoxString.setSelectedItem("SC_SEC");
                    // if(frequencyComboBoxString.getSelectedIndex()==1)frequencyComboBoxString.setSelectedItem("SC_MS");
                    // if(frequencyComboBoxString.getSelectedIndex()==2)frequencyComboBoxString.setSelectedItem("SC_US");
                    // if(frequencyComboBoxString.getSelectedIndex()==3)frequencyComboBoxString.setSelectedItem("SC_NS");

                    if (frequencyComboBoxString.getSelectedIndex() == 0)
                        frequencyComboBoxString.setSelectedItem("s");
                    if (frequencyComboBoxString.getSelectedIndex() == 1)
                        frequencyComboBoxString.setSelectedItem("ms");
                    if (frequencyComboBoxString.getSelectedIndex() == 2)
                        frequencyComboBoxString.setSelectedItem("\u03BCs");
                    if (frequencyComboBoxString.getSelectedIndex() == 3)
                        frequencyComboBoxString.setSelectedItem("ns");

                    clock.setFrequency(Double.parseDouble(frequencyTextField.getText()));
                    clock.setUnit((String) frequencyComboBoxString.getSelectedItem());

                }
            } else {
                clock.setFrequency(-1);
                clock.setUnit("");
            }

            if (!(dutyCycleTextField.getText().isEmpty())) {
                Boolean dutyCycleValueInteger = false;
                try {
                    Double.parseDouble(dutyCycleTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "DutyCycle is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    dutyCycleValueInteger = true;
                }
                if (dutyCycleValueInteger == false) {
                    clock.setDutyCycle(Double.parseDouble(dutyCycleTextField.getText()));
                    // clock.setUnit((String) dutyCycleComboBoxString.getSelectedItem());
                }
            } else {
                clock.setDutyCycle(-1);
                // clock.setUnit("");
            }

            if (!(startTimeTextField.getText().isEmpty())) {
                Boolean startTimeValueInteger = false;
                try {
                    Double.parseDouble(startTimeTextField.getText());
                } catch (NumberFormatException e1) {
                    JDialog msg = new JDialog(this);
                    msg.setLocationRelativeTo(null);
                    JOptionPane.showMessageDialog(msg, "StartTime is not a Double", "Warning !",
                            JOptionPane.WARNING_MESSAGE);
                    startTimeValueInteger = true;
                }
                if (startTimeValueInteger == false) {

                    if (startTimeComboBoxString.getSelectedIndex() == 0)
                        startTimeComboBoxString.setSelectedItem("s");
                    if (startTimeComboBoxString.getSelectedIndex() == 1)
                        startTimeComboBoxString.setSelectedItem("ms");
                    if (startTimeComboBoxString.getSelectedIndex() == 2)
                        startTimeComboBoxString.setSelectedItem("\u03BCs");
                    if (startTimeComboBoxString.getSelectedIndex() == 3)
                        startTimeComboBoxString.setSelectedItem("ns");

                    clock.setStartTime(Double.parseDouble(startTimeTextField.getText()));
                    clock.setUnitStartTime((String) startTimeComboBoxString.getSelectedItem());

                    // System.out.println("@@@@@@@@@ item start time:
                    // "+(String)startTimeComboBoxString.getSelectedItem());

                }
            } else {
                clock.setStartTime(-1);
                clock.setUnitStartTime("");
                // System.out.println("@@@@@@@@@ units :
                // "+(String)startTimeComboBoxString.getSelectedItem());
            }

            clock.setListStruct(structListModel);
            /*
             * clock.setNameTemplate(nameTemplateTextField.getText());
             * clock.setTypeTemplate((String) typeTemplateComboBoxString.getSelectedItem());
             * clock.setValueTemplate(valueTemplateTextField.getText());
             * clock.setListTypedef(typedefListModel);
             */

            this.dispose();
        }

        if ("Save_Close".equals(e.getActionCommand())) {
            clock.setValue(new String(nameTextField.getText()));

            if (clock.getFather() != null) {
                clock.setListStruct(structListModel);
                // clock.setNameTemplate(nameTemplateTextField.getText());
                // clock.setTypeTemplate((String) typeTemplateComboBoxString.getSelectedItem());
                // clock.setValueTemplate(valueTemplateTextField.getText());
                // clock.setListTypedef(typedefListModel);
                // clock.setNameFn(nameFnTextField.getText());
                // clock.setCode(codeTextArea.getText());
            }

            this.dispose();
        }

        if ("Cancel".equals(e.getActionCommand())) {
            this.dispose();
        }
    }
}

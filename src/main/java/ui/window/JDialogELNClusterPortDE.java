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

import ui.eln.ELNClusterPortDE;
import ui.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Class JDialogELNClusterPortDE Dialog for managing of ELN cluster port DE
 * Creation: 03/08/2018
 * 
 * @version 1.0 03/08/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class JDialogELNClusterPortDE extends JDialog implements ActionListener {

  private JTextField nameTextField;
  private ArrayList<String> listArrayTypeString;
  private JComboBox<String> typeComboBoxString;
  private ArrayList<String> listOriginString;
  private JComboBox<String> originComboBoxString;

  private ELNClusterPortDE term;

  public JDialogELNClusterPortDE(ELNClusterPortDE term) {
    this.setTitle("Setting Cluster Port DE Attributes");
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    this.setAlwaysOnTop(true);
    this.setResizable(false);

    this.term = term;

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
    getRootPane().getActionMap().put("close", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    dialog();
  }

  public void dialog() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    this.add(mainPanel);

    JPanel attributesMainPanel = new JPanel();
    mainPanel.add(attributesMainPanel, BorderLayout.NORTH);

    attributesMainPanel.setLayout(new BorderLayout());

    Box attributesBox = Box.createVerticalBox();
    attributesBox.setBorder(BorderFactory.createTitledBorder("Setting cluster port DE attributes"));

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

    nameTextField = new JTextField(term.getValue().toString(), 10);
    nameTextField.setEditable(false);
    constraints = new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(15, 10, 5, 10), 0, 0);
    gridBag.setConstraints(nameTextField, constraints);
    attributesBoxPanel.add(nameTextField);

    JLabel typeLabel = new JLabel("Type : ");
    constraints = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(typeLabel, constraints);
    attributesBoxPanel.add(typeLabel);

    listArrayTypeString = new ArrayList<String>();
    listArrayTypeString.add("bool");
    listArrayTypeString.add("double");
    listArrayTypeString.add("int");
    typeComboBoxString = new JComboBox<String>();
    for (int i = 0; i < listArrayTypeString.size(); i++) {
      typeComboBoxString.addItem(listArrayTypeString.get(i));
    }
    for (int i = 0; i < listArrayTypeString.size(); i++) {
      if (term.getPortType().equals(listArrayTypeString.get(i))) {
        typeComboBoxString.setSelectedIndex(i);
      }
    }
    typeComboBoxString.addActionListener(this);
    typeComboBoxString.setEnabled(false);
    constraints = new GridBagConstraints(1, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 5, 10), 0, 0);
    gridBag.setConstraints(typeComboBoxString, constraints);
    attributesBoxPanel.add(typeComboBoxString);

    JLabel orginLabel = new JLabel("In / Out : ");
    constraints = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 15, 10), 0, 0);
    gridBag.setConstraints(orginLabel, constraints);
    attributesBoxPanel.add(orginLabel);

    listOriginString = new ArrayList<String>();
    listOriginString.add("in");
    listOriginString.add("out");
    originComboBoxString = new JComboBox<String>();
    for (int i = 0; i < listOriginString.size(); i++) {
      originComboBoxString.addItem(listOriginString.get(i));
    }
    for (int i = 0; i < listOriginString.size(); i++) {
      if (term.getOrigin().equals(listOriginString.get(i))) {
        originComboBoxString.setSelectedIndex(i);
      }
    }
    originComboBoxString.addActionListener(this);
    originComboBoxString.setEnabled(false);
    constraints = new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(5, 10, 15, 10), 0, 0);
    gridBag.setConstraints(originComboBoxString, constraints);
    attributesBoxPanel.add(originComboBoxString);

    attributesBox.add(attributesBoxPanel);

    attributesMainPanel.add(attributesBox, BorderLayout.NORTH);

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
      term.setValue(new String(nameTextField.getText()));

      this.dispose();
    }

    if ("Cancel".equals(e.getActionCommand())) {
      this.dispose();
    }
  }
}
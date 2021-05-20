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

import attacktrees.*;
import myutil.Conversion;
import myutil.TraceManager;
import ui.MainGUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class JDialogAttackerPopulation Dialog for managing an attacker population
 * Creation: 04/02/2021
 *
 * @author Ludovic APVRILLE
 * @version 2.0 04/02/2021
 */
public class JDialogAttackerPopulation extends JDialogBase implements ActionListener, ListSelectionListener, Runnable {
  protected AttackerPopulation population;
  protected ArrayList<AttackerGroup> groups;

  protected JPanel panel1, panel2;

  protected MainGUI mgui;
  protected Frame frame;

  protected String attrib = "Attacker group";

  // Name of population
  protected String name;
  protected JPanel panelName;
  protected JTextField nameField;

  // Panel1
  protected JComboBox<String> expertise;
  protected JTextField identifierText;
  protected JTextField moneyText;
  protected JTextField nbText;
  protected JButton addButton;

  // Panel2
  protected JList<AttackerGroup> listAttackers;
  protected JButton upButton;
  protected JButton downButton;
  protected JButton removeButton;
  protected JButton analyzeButton;
  protected JTextArea resultArea;

  private boolean hasBeenCancelled = true;

  /* Creates new form */
  public JDialogAttackerPopulation(MainGUI _mgui, Frame f, String title, AttackerPopulation _population) {
    super(f, title, true);
    mgui = _mgui;
    frame = f;
    population = _population;
    name = population.getName();
    groups = new ArrayList<>();
    groups.addAll(population.getAttackerGroups());

    initComponents();
    myInitComponents();
    pack();
  }

  protected void myInitComponents() {
    removeButton.setEnabled(false);
    upButton.setEnabled(false);
    downButton.setEnabled(false);
  }

  protected void initComponents() {
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

    panel1 = new JPanel();
    panel1.setLayout(gridbag1);
    panel1.setBorder(new javax.swing.border.TitledBorder("Adding " + attrib + "s"));
    panel1.setPreferredSize(new Dimension(300, 250));

    panel2 = new JPanel();
    panel2.setLayout(gridbag2);
    panel2.setBorder(new javax.swing.border.TitledBorder("Managing " + attrib + "s"));
    panel2.setPreferredSize(new Dimension(300, 250));

    // first line panel1
    c1.gridwidth = 1;
    c1.gridheight = 1;
    c1.weighty = 1.0;
    c1.weightx = 1.0;
    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    c1.fill = GridBagConstraints.BOTH;
    c1.gridheight = 3;
    panel1.add(new JLabel(" "), c1);

    c1.gridwidth = 1;
    c1.gridheight = 1;
    c1.weighty = 1.0;
    c1.weightx = 1.0;
    c1.anchor = GridBagConstraints.CENTER;
    panel1.add(new JLabel("Identifier"), c1);
    panel1.add(new JLabel("Resource ($ / €)"), c1);
    panel1.add(new JLabel("Expertise"), c1);
    panel1.add(new JLabel(" "), c1);
    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    panel1.add(new JLabel("How many of them?"), c1);

    // Second line panel1
    c1.gridwidth = 1;
    c1.fill = GridBagConstraints.HORIZONTAL;
    c1.anchor = GridBagConstraints.CENTER;

    identifierText = new JTextField();
    identifierText.setColumns(15);
    identifierText.setEditable(true);
    panel1.add(identifierText, c1);

    moneyText = new JTextField();
    moneyText.setColumns(15);
    moneyText.setEditable(true);
    panel1.add(moneyText, c1);

    expertise = new JComboBox<>(Attack.EXPERIENCES);
    panel1.add(expertise, c1);

    panel1.add(new JLabel(" "), c1);

    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    nbText = new JTextField();
    nbText.setColumns(10);
    nbText.setEditable(true);
    panel1.add(nbText, c1);

    c1.anchor = GridBagConstraints.CENTER;

    // third line panel1
    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    c1.fill = GridBagConstraints.BOTH;
    c1.gridheight = 3;
    panel1.add(new JLabel(" "), c1);

    // fourth line panel2
    c1.gridheight = 1;
    c1.fill = GridBagConstraints.HORIZONTAL;
    addButton = new JButton("Add / Modify " + attrib);
    addButton.addActionListener(this);
    panel1.add(addButton, c1);

    // 1st line panel2
    listAttackers = new JList<AttackerGroup>(groups.toArray(new AttackerGroup[0]));
    // listAttribute.setFixedCellWidth(150);
    // listAttribute.setFixedCellHeight(20);
    listAttackers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listAttackers.addListSelectionListener(this);
    JScrollPane scrollPane = new JScrollPane(listAttackers);
    scrollPane.setSize(300, 250);
    c2.gridwidth = GridBagConstraints.REMAINDER; // end row
    c2.fill = GridBagConstraints.BOTH;
    c2.gridheight = 5;
    c2.weighty = 10.0;
    c2.weightx = 10.0;
    panel2.add(scrollPane, c2);

    // 2nd line panel2
    c2.weighty = 1.0;
    c2.weightx = 1.0;
    c2.fill = GridBagConstraints.BOTH;
    c2.gridheight = 1;
    panel2.add(new JLabel(""), c2);

    // third line panel2
    c2.gridwidth = GridBagConstraints.REMAINDER; // end row
    c2.fill = GridBagConstraints.HORIZONTAL;
    upButton = new JButton("Up");
    upButton.addActionListener(this);
    panel2.add(upButton, c2);

    downButton = new JButton("Down");
    downButton.addActionListener(this);
    panel2.add(downButton, c2);

    removeButton = new JButton("Remove " + attrib);
    removeButton.addActionListener(this);
    panel2.add(removeButton, c2);

    // Name panel
    if (name != null) {
      GridBagLayout gbOp = new GridBagLayout();
      GridBagConstraints cOp = new GridBagConstraints();
      panelName = new JPanel();
      panelName.setLayout(gbOp);
      panelName.setBorder(new javax.swing.border.TitledBorder("Name"));
      // panelOperation.setPreferredSize(new Dimension(500, 70));

      cOp.weighty = 1.0;
      cOp.weightx = 2.0;
      cOp.gridwidth = GridBagConstraints.REMAINDER;
      cOp.fill = GridBagConstraints.BOTH;
      cOp.gridheight = 3;
      nameField = new JTextField(name);
      panelName.add(nameField, cOp);

      c0.weighty = 1.0;
      c0.weightx = 1.0;
      c0.fill = GridBagConstraints.BOTH;
      c0.gridwidth = GridBagConstraints.REMAINDER;
      c.add(panelName, c0);
    }

    // main panel;
    c0.gridwidth = 1;
    c0.gridheight = 15;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
    c0.fill = GridBagConstraints.BOTH;

    c.add(panel1, c0);
    c0.gridwidth = GridBagConstraints.REMAINDER; // end row
    c.add(panel2, c0);

    // Analysis panel
    c0.gridheight = 1;
    analyzeButton = new JButton("Analyze");
    analyzeButton.addActionListener(this);
    c.add(analyzeButton, c0);

    JPanel jta = new JPanel();
    jta.setBorder(new javax.swing.border.TitledBorder("Results"));
    Font f = new Font("Courrier", Font.BOLD, 12);
    jta.setFont(f);
    resultArea = new JTextArea();
    JScrollPane jsp = new JScrollPane(resultArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jsp.setPreferredSize(new Dimension(600, 150));
    jta.add(jsp);
    resultArea.append("Click on \"Analyse\" to analyse the current population");

    c0.gridheight = 25;
    c.add(jta, c0);

    c0.gridheight = 1;
    c0.gridwidth = GridBagConstraints.REMAINDER;
    c0.fill = GridBagConstraints.HORIZONTAL;

    initButtons(c0, c, this);
  }

  public void actionPerformed(ActionEvent evt) {

    String command = evt.getActionCommand();

    // Compare the action command to the known actions.
    if (command.equals("Save and Close")) {
      closeDialog();
    } else if (command.equals("Add / Modify " + attrib)) {
      addAttackerGroup();
    } else if (command.equals("Cancel")) {
      cancelDialog();
    } else if (command.equals("Remove " + attrib)) {
      removeAttacker();
    } else if (command.equals("Down")) {
      downAttribute();
    } else if (command.equals("Up")) {
      upAttribute();
    } else if (evt.getSource() == analyzeButton) {
      Thread t = new Thread(this);
      t.start();
    }
  }

  public void addAttackerGroup() {
    String s = identifierText.getText();

    if (s.length() > 0) {
      if (Attacker.isValidID(s)) {
        int index = -1;
        int cpt = 0;
        for (AttackerGroup ag : groups) {
          if (ag.getName().compareTo(s) == 0) {
            index = cpt;
            break;
          }
          cpt++;
        }

        // Found
        if (index > -1) {
          TraceManager.addDev("Found attacker group");
          AttackerGroup ag = groups.get(index);

          String tmp = moneyText.getText();
          if (Conversion.isInteger(tmp)) {
            ag.attacker.money = Integer.decode(tmp);
          }

          ag.attacker.expertise = expertise.getSelectedIndex();

          tmp = nbText.getText();
          if (Conversion.isInteger(tmp)) {
            ag.occurrence = Integer.decode(tmp);
          }

          // not found
        } else {
          TraceManager.addDev("NOT Found attacker group");
          AttackerGroup ag = new AttackerGroup(s, this);
          groups.add(ag);

          String tmp = moneyText.getText();
          if (Attacker.isValidMoney(tmp)) {
            ag.attacker.money = Integer.decode(tmp);
          } else {
            ag.attacker.money = 0;
          }

          ag.attacker.expertise = expertise.getSelectedIndex();

          tmp = nbText.getText();
          if (AttackerGroup.isValidOccurrence(tmp)) {
            ag.occurrence = Integer.decode(tmp);
          } else {
            ag.occurrence = 1;
          }
        }

        listAttackers.setListData(groups.toArray(new AttackerGroup[0]));
        identifierText.setText("");
      } else {
        JOptionPane.showMessageDialog(frame, "Bad identifier", "Error", JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    } else {
      JOptionPane.showMessageDialog(frame, "Bad identifier: identifier already in use, or invalid identifier", "Error",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }
  }

  public void removeAttacker() {
    int i = listAttackers.getSelectedIndex();
    if (i != -1) {
      AttackerGroup ag = groups.get(i);
      groups.remove(i);
      listAttackers.setListData(groups.toArray(new AttackerGroup[0]));
    }
  }

  public void downAttribute() {
    int i = listAttackers.getSelectedIndex();
    if ((i != -1) && (i != groups.size() - 1)) {
      AttackerGroup o = groups.get(i);
      groups.remove(i);
      groups.add(i + 1, o);
      listAttackers.setListData(groups.toArray(new AttackerGroup[0]));
      listAttackers.setSelectedIndex(i + 1);
    }
  }

  public void upAttribute() {
    int i = listAttackers.getSelectedIndex();
    if (i > 0) {
      AttackerGroup o = groups.get(i);
      groups.remove(i);
      groups.add(i - 1, o);
      listAttackers.setListData(groups.toArray(new AttackerGroup[0]));
      listAttackers.setSelectedIndex(i - 1);
    }
  }

  public void closeDialog() {
    population.setGroup(groups);
    if (Attacker.isValidID(nameField.getText())) {
      population.setName(nameField.getText());
    }
    hasBeenCancelled = false;
    dispose();
  }

  public void cancelDialog() {
    dispose();
  }

  public void valueChanged(ListSelectionEvent e) {
    int i = listAttackers.getSelectedIndex();
    if (i == -1) {
      removeButton.setEnabled(false);
      upButton.setEnabled(false);
      downButton.setEnabled(false);
      identifierText.setText("");
      // initialValue.setText("");
    } else {
      AttackerGroup a = groups.get(i);
      identifierText.setText(a.getName());
      moneyText.setText("" + a.getMoney());
      expertise.setSelectedIndex(a.getExpertise());
      nbText.setText("" + a.getOccurrence());
      removeButton.setEnabled(true);
      if (i > 0) {
        upButton.setEnabled(true);
      } else {
        upButton.setEnabled(false);
      }
      if (i != groups.size() - 1) {
        downButton.setEnabled(true);
      } else {
        downButton.setEnabled(false);
      }
    }
  }

  public void select(JComboBox<String> jcb, String text) {
    String s;
    for (int i = 0; i < jcb.getItemCount(); i++) {
      s = jcb.getItemAt(i);
      //
      if (s.equals(text)) {
        jcb.setSelectedIndex(i);
        return;
      }
    }
  }

  public String getName() {
    if (nameField != null) {
      return nameField.getText().trim();
    }
    return "";
  }

  public boolean hasBeenCancelled() {
    return hasBeenCancelled;
  }

  // Analysis
  public void run() {
    // Must check the syntax of the system
    resultArea.append("\n\nRunning analysis\n");
    resultArea.append("\tSyntax checking\n");
    if (!mgui.checkModelingSyntax(true)) {
      resultArea.append("\t\t-> KO\n");
      return;
    }
    resultArea.append("\t\t-> OK\n");
    resultArea.append("\tTree analysis\n");
    AttackTree at = mgui.runAttackTreeAnalysis();
    if (at == null) {
      resultArea.append("\t\t-> KO\n");
      return;
    }
    resultArea.append("\t\t-> OK\n");

    AttackerPopulation pop = new AttackerPopulation(population.getName(), population.getReferenceObject());
    pop.setGroup(groups);
    int size = pop.getTotalPopulation();
    int success = pop.getTotalAttackers(at);
    resultArea.append("\tPopulation analysis\n");
    resultArea.append("\t\tTotal population: " + size + "\n");
    resultArea.append("\t\tSuccessful attackers: " + success + "\n");
    resultArea.append("\t\t% of successful attackers: " + (int) (100.0 * success / size) + "%\n");
    resultArea.append("\t\tProbability of success: " + ((double) success / size) + "\n");
  }

}

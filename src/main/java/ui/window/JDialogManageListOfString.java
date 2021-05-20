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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Class JDialogManageListOfString Dialog for managing two lists of String
 * Creation: 28/03/2014
 * 
 * @version 1.0 28/03/2014
 * @author Ludovic APVRILLE
 */
public class JDialogManageListOfString extends JDialogBase implements ActionListener, ListSelectionListener {

  // private static boolean overideSyntaxChecking = false;

  private Vector<String> ignored, selected;

  // subpanels
  private JPanel panel1, panel2, panel3, panel6;
  private JList<String> listIgnored;
  private JList<String> listSelected;
  private JButton allSelected;
  private JButton addOneSelected;
  private JButton addOneIgnored;
  private JButton allIgnored;

  // Main Panel

  private boolean hasBeenCancelled = true;

  /* Creates new form */
  public JDialogManageListOfString(Frame f, Vector<String> _ignored, Vector<String> _selected, String title) {
    super(f, title, true);

    ignored = _ignored;
    selected = _selected;

    initComponents();
    myInitComponents();
    pack();
  }

  private void myInitComponents() {
    setButtons();
  }

  private void initComponents() {
    Container c = getContentPane();
    GridBagLayout gridbag1 = new GridBagLayout();
    GridBagConstraints c1 = new GridBagConstraints();
    setFont(new Font("Helvetica", Font.PLAIN, 14));
    c.setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // ignored list
    panel1 = new JPanel();
    panel1.setLayout(new BorderLayout());
    panel1.setBorder(new javax.swing.border.TitledBorder("Non selected diagrams"));
    listIgnored = new JList<>(ignored);
    // listIgnored.setPreferredSize(new Dimension(200, 250));
    listIgnored.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listIgnored.addListSelectionListener(this);
    JScrollPane scrollPane1 = new JScrollPane(listIgnored);
    panel1.add(scrollPane1, BorderLayout.CENTER);
    panel1.setPreferredSize(new Dimension(200, 250));
    c.add(panel1, BorderLayout.WEST);

    // validated list
    panel2 = new JPanel();
    panel2.setLayout(new BorderLayout());
    panel2.setBorder(new javax.swing.border.TitledBorder("Selected diagrams"));
    listSelected = new JList<>(selected);
    // listValidated.setPreferredSize(new Dimension(200, 250));
    listSelected.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listSelected.addListSelectionListener(this);
    JScrollPane scrollPane2 = new JScrollPane(listSelected);
    panel2.add(scrollPane2, BorderLayout.CENTER);
    panel2.setPreferredSize(new Dimension(200, 250));
    c.add(panel2, BorderLayout.EAST);

    // central buttons
    panel3 = new JPanel();
    panel3.setLayout(gridbag1);

    c1.weighty = 1.0;
    c1.weightx = 1.0;
    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    c1.fill = GridBagConstraints.HORIZONTAL;
    c1.gridheight = 1;

    allSelected = new JButton(IconManager.imgic50);
    allSelected.setPreferredSize(new Dimension(50, 25));
    allSelected.addActionListener(this);
    allSelected.setActionCommand("allSelected");
    panel3.add(allSelected, c1);

    addOneSelected = new JButton(IconManager.imgic48);
    addOneSelected.setPreferredSize(new Dimension(50, 25));
    addOneSelected.addActionListener(this);
    addOneSelected.setActionCommand("addOneSelected");
    panel3.add(addOneSelected, c1);

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

    c.add(panel3, BorderLayout.CENTER);

    // main panel;
    panel6 = new JPanel();
    panel6.setLayout(new FlowLayout());

    closeButton = new JButton("OK", IconManager.imgic37);
    // closeButton.setPreferredSize(new Dimension(600, 50));
    closeButton.addActionListener(this);
    closeButton.setPreferredSize(new Dimension(200, 30));

    cancelButton = new JButton("Cancel", IconManager.imgic27);
    cancelButton.addActionListener(this);
    cancelButton.setPreferredSize(new Dimension(200, 30));
    panel6.add(cancelButton);
    panel6.add(closeButton);

    c.add(panel6, BorderLayout.SOUTH);

  }

  public void actionPerformed(ActionEvent evt) {
    String command = evt.getActionCommand();

    // Compare the action command to the known actions.
    if (evt.getSource() == closeButton) {
      closeDialog();
    } else if (command.equals("Cancel")) {
      cancelDialog();
    } else if (evt.getSource() == addOneIgnored) {
      addOneIgnored();
    } else if (evt.getSource() == addOneSelected) {
      addOneSelected();
    } else if (evt.getSource() == allSelected) {
      allSelected();
    } else if (evt.getSource() == allIgnored) {
      allIgnored();
    }
  }

  private void addOneIgnored() {
    int[] list = listSelected.getSelectedIndices();
    Vector<String> v = new Vector<>();
    String o;
    for (int i = 0; i < list.length; i++) {
      o = selected.elementAt(list[i]);
      ignored.addElement(o);
      v.addElement(o);
    }

    selected.removeAll(v);
    listIgnored.setListData(ignored);
    listSelected.setListData(selected);
    setButtons();
  }

  private void addOneSelected() {
    int[] list = listIgnored.getSelectedIndices();
    Vector<String> v = new Vector<>();
    String o;
    for (int i = 0; i < list.length; i++) {
      o = ignored.elementAt(list[i]);
      selected.addElement(o);
      v.addElement(o);
    }

    ignored.removeAll(v);
    listIgnored.setListData(ignored);
    listSelected.setListData(selected);
    setButtons();
  }

  private void allSelected() {
    selected.addAll(ignored);
    ignored.removeAllElements();
    listIgnored.setListData(ignored);
    listSelected.setListData(selected);
    setButtons();
  }

  private void allIgnored() {
    ignored.addAll(selected);
    selected.removeAllElements();
    listIgnored.setListData(ignored);
    listSelected.setListData(selected);
    setButtons();
  }

  public void closeDialog() {
    hasBeenCancelled = false;
    dispose();
  }

  public void cancelDialog() {
    dispose();
  }

  public boolean hasBeenCancelled() {
    return hasBeenCancelled;
  }

  private void setButtons() {
    int i1 = listIgnored.getSelectedIndex();
    int i2 = listSelected.getSelectedIndex();

    // closeButton.setEnabled(true);

    if (i1 == -1) {
      addOneSelected.setEnabled(false);
    } else {
      addOneSelected.setEnabled(true);
      // listValidated.clearSelection();
    }

    if (i2 == -1) {
      addOneIgnored.setEnabled(false);
    } else {
      addOneIgnored.setEnabled(true);
      // listIgnored.clearSelection();
    }

    if (ignored.size() == 0) {
      allSelected.setEnabled(false);
    } else {
      allSelected.setEnabled(true);
    }

    if (selected.size() == 0) {
      allIgnored.setEnabled(false);

    } else {
      allIgnored.setEnabled(true);

    }
  }

  public void valueChanged(ListSelectionEvent e) {
    setButtons();
  }

  public Vector<String> getSelected() {
    return selected;
  }

  public Vector<String> getIgnored() {
    return ignored;
  }

}

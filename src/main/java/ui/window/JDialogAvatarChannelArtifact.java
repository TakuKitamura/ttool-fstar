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
import ui.avatardd.ADDChannelArtifact;
import ui.avatardd.ADDDiagramPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

//import javax.swing.event.*;

/**
 * Class JDialogAvatarChannelArtifact Dialog for managing channel artifacts on
 * memory nodes Creation: 25/08/2014
 * 
 * @version 1.0 25/08/2014
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarChannelArtifact extends JDialogBase implements ActionListener {

  private boolean regularClose;
  private boolean emptyList = false;

  private JPanel panel2;
  private Frame frame;
  private ADDChannelArtifact artifact;

  // protected JTextField taskName;
  protected JComboBox<String> referenceDiagram;

  /* Creates new form */
  public JDialogAvatarChannelArtifact(Frame _frame, String _title, ADDChannelArtifact _artifact) {
    super(_frame, _title, true);
    frame = _frame;
    artifact = _artifact;

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
    panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
    panel2.setPreferredSize(new Dimension(550, 250));

    c1.gridwidth = 1;
    c1.gridheight = 1;
    c1.weighty = 1.0;
    c1.weightx = 1.0;
    c1.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(new JLabel("channel: "), c2);
    c1.gridwidth = GridBagConstraints.REMAINDER; // end row
    Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllNonMappedAvatarChannelNames(
        (ADDDiagramPanel) (artifact.getTDiagramPanel()), artifact.getReferenceDiagram(), artifact.getChannelName());
    int index = 0;
    if (list.size() == 0) {
      list.add("No more channel to map");
      emptyList = true;
    } else {
      index = indexOf(list, artifact.getValue());
    }
    referenceDiagram = new JComboBox<>(list);
    referenceDiagram.setSelectedIndex(index);
    // referenceTaskName.setEditable(true);
    // referenceTaskName.setFont(new Font("times", Font.PLAIN, 12));
    panel2.add(referenceDiagram, c1);

    /*
     * c1.gridwidth = 1; c1.gridheight = 1; c1.weighty = 1.0; c1.weightx = 1.0;
     * c1.fill = GridBagConstraints.HORIZONTAL; panel2.add(new JLabel("Name:"), c2);
     * c1.gridwidth = GridBagConstraints.REMAINDER; //end row taskName = new
     * JTextField(artifact.getTaskName(), 30); taskName.setEditable(true);
     * taskName.setFont(new Font("times", Font.PLAIN, 12)); panel2.add(taskName,
     * c1);
     */

    // main panel;
    c0.gridheight = 10;
    c0.weighty = 1.0;
    c0.weightx = 1.0;
    c0.gridwidth = GridBagConstraints.REMAINDER; // end row
    c.add(panel2, c0);

    c0.gridwidth = 1;
    c0.gridheight = 1;
    c0.fill = GridBagConstraints.HORIZONTAL;

    initButtons(c0, c, this);
  }

  public void actionPerformed(ActionEvent evt) {
    /*
     * if (evt.getSource() == typeBox) { boolean b =
     * ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
     * initialValue.setEnabled(b); return; }
     */

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

  public String getReferenceDiagram() {
    if (emptyList) {
      return null;
    }
    String tmp = (String) (referenceDiagram.getSelectedItem());
    int index = tmp.indexOf("::");
    if (index == -1) {
      return tmp;
    }
    return tmp.substring(0, index);
  }

  public String getChannelName() {
    String tmp = (String) (referenceDiagram.getSelectedItem());
    int index = tmp.indexOf("::");
    if (index == -1) {
      return tmp;
    }
    return tmp.substring(index + 2, tmp.length());
  }

  public String getFullChannelName() {
    return (String) (referenceDiagram.getSelectedItem());
  }

  public int indexOf(Vector<String> _list, String name) {
    int i = 0;
    for (String s : _list) {
      if (s.equals(name)) {
        return i;
      }
      i++;
    }
    return 0;
  }

}

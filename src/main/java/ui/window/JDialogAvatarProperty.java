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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class JDialogAvatarProperty Dialog for managing property attributes Creation:
 * 26/04/2010
 * 
 * @version 1.0 26/04/2010
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarProperty extends JDialogBase implements ActionListener {
    private JPanel panel1;

    private String name;
    private int kind;
    boolean notSelected;
    private boolean hasBeenCancelled = true;
    private JTextField myName;
    private JRadioButton safety, notSafety, reachability, liveness, notReachability, notLiveness;

    // private String id1, id2;

    /* Creates new form */
    public JDialogAvatarProperty(Frame f, String _name, int _kind, boolean _notSelected) {

        super(f, "Setting property attributes", true);

        name = _name;
        kind = _kind;
        notSelected = _notSelected;

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
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);

        panel1.setBorder(new javax.swing.border.TitledBorder("Property"));

        // panel1.setPreferredSize(new Dimension(200, 150));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel1.add(new JLabel(" "), c1);

        // second line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        myName = new JTextField(name, 30);
        myName.setEditable(true);
        panel1.add(myName, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        reachability = new JRadioButton("Reachable");
        reachability.setSelected((kind == 1) && !notSelected);
        panel1.add(reachability, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        notReachability = new JRadioButton("Not reachable");
        notReachability.setSelected((kind == 1) && notSelected);
        panel1.add(notReachability, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        liveness = new JRadioButton("Liveness");
        liveness.setSelected((kind == 0) && !notSelected);
        panel1.add(liveness, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        notLiveness = new JRadioButton("Not Liveness");
        notLiveness.setSelected((kind == 0) && notSelected);
        panel1.add(notLiveness, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        safety = new JRadioButton("Safety");
        safety.setSelected((kind == 2) && !notSelected);
        panel1.add(safety, c1);

        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        notSafety = new JRadioButton("Not Safety");
        notSafety.setSelected((kind == 2) && notSelected);
        panel1.add(notSafety, c1);

        ButtonGroup bg = new ButtonGroup();
        bg.add(reachability);
        bg.add(liveness);
        bg.add(notReachability);
        bg.add(notLiveness);
        bg.add(safety);
        bg.add(notSafety);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        c0.fill = GridBagConstraints.BOTH;

        c.add(panel1, c0);

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

    public boolean hasBeenCancelled() {
        return hasBeenCancelled;
    }

    public void closeDialog() {
        hasBeenCancelled = false;
        dispose();
    }

    public boolean isReachabilitySelected() {
        return reachability.isSelected();
    }

    public boolean isNotReachabilitySelected() {
        return notReachability.isSelected();
    }

    public boolean isLivenessSelected() {
        return liveness.isSelected();
    }

    public boolean isNotLivenessSelected() {
        return notLiveness.isSelected();
    }

    public boolean isSafetySelected() {
        return safety.isSelected();
    }

    public boolean isNotSafetySelected() {
        return notSafety.isSelected();
    }

    public String getName() {
        return myName.getText();
    }

    public void cancelDialog() {
        dispose();
    }
}

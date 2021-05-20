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
 * Class JDialogVersioning Dialog for managing information on versioning
 * connectors in MAD Creation: 09/09/2013
 * 
 * @version 1.0 09/09/2013
 * @author Ludovic APVRILLE
 */
public class JDialogVersioningConnector extends JDialogBase implements ActionListener {

    private JPanel panel1;// , panel2, panel3, panel4, panel5;

    // Panel1
    int oldVersion, newVersion;
    private JTextField version1, version2;

    boolean cancel = false;

    // private String id1, id2;

    /* Creates new form */
    public JDialogVersioningConnector(Frame f, int _oldVersion, int _newVersion) {

        super(f, "Setting versions", true);

        oldVersion = _oldVersion;
        newVersion = _newVersion;

        initComponents();
        // myInitComponents();
        pack();
    }
    //
    //
    // private void myInitComponents() {
    // }

    private void initComponents() {
        // JTabbedPane tabbedPane = new JTabbedPane();
        // tabbedPane.setPreferredSize(new Dimension(550, 400));

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

        panel1.setBorder(new javax.swing.border.TitledBorder("Value of versions"));

        // panel1.setPreferredSize(new Dimension(600, 350));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel1.add(new JLabel(" "), c1);

        // second line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;

        // name
        panel1.add(new JLabel("origin version = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        version1 = new JTextField("" + oldVersion, 15);
        panel1.add(version1, c1);

        // loss rate
        c1.gridwidth = 1;
        panel1.add(new JLabel("Destination version = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        version2 = new JTextField("" + newVersion, 15);
        panel1.add(version2, c1);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row

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

    public void closeDialog() {
        /*
         * delay = jdelay.getText(); lossRate = jlossRate.getText(); implementation =
         * jimp.getSelectedIndex(); try { oport =
         * Integer.decode(joport.getText()).intValue(); } catch (Exception e) { oport =
         * -1; }
         * 
         * try { dport = Integer.decode(jdport.getText()).intValue(); } catch (Exception
         * e) { dport = -1; }
         */

        dispose();
    }

    public String getOldVersion() {
        return version1.getText();
    }

    public String getNewVersion() {
        return version2.getText();
    }

    public void cancelDialog() {
        cancel = true;
        dispose();
    }

    public boolean hasBeenCancelled() {
        return cancel;
    }

}

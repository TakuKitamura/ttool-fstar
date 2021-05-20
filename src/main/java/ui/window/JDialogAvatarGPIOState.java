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

import myutil.Conversion;
import myutil.GraphicLib;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class JDialogAvatarGPIOState Dialog for managing AVATAR states information
 * Creation: 08/03/2013
 * 
 * @version 1.0 08/03/2013
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarGPIOState extends JDialogBase implements ActionListener {

    // protected String [] globalCode;
    protected String[] entryCode;

    private boolean cancelled = true;

    private JPanel panel1;
    private JPanel panel2;

    // Panel1
    private String stateName;
    private JTextField stateNameText;

    // Panel of code and files
    protected JTextArea jtaEntryCode;
    // jtaGlobalCode;

    /* Creates new form */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogAvatarGPIOState(Frame _f, String _title, String _name, String[] _entryCode) {

        super(_f, _title, true);

        stateName = _name;

        // globalCode = _globalCode;
        entryCode = _entryCode;

        initComponents();
        myInitComponents();
        pack();
    }

    private void myInitComponents() {
    }

    private void initComponents() {
        int i;

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

        panel1.setBorder(new javax.swing.border.TitledBorder("GPIOState parameters"));

        JPanel panel11 = new JPanel(new BorderLayout());
        // Name of state
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridheight = 1;
        panel1.add(new JLabel("ID of state = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        stateNameText = new JTextField(stateName);
        panel1.add(stateNameText, c1);
        panel11.add(panel1, BorderLayout.CENTER);

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);

        panel2.setBorder(new javax.swing.border.TitledBorder("Entry code"));
        // guard
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.gridheight = 1;

        /*
         * panel2.add(new JLabel("Global code:"), c2); jtaGlobalCode = new JTextArea();
         * jtaGlobalCode.setEditable(true); jtaGlobalCode.setMargin(new Insets(10, 10,
         * 10, 10)); jtaGlobalCode.setTabSize(3); String files = ""; if (globalCode !=
         * null) { for(i=0; i<globalCode.length; i++) { files += globalCode[i] + "\n"; }
         * } jtaGlobalCode.append(files); jtaGlobalCode.setFont(new Font("times",
         * Font.PLAIN, 12)); JScrollPane jsp = new JScrollPane(jtaGlobalCode,
         * JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
         * JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); jsp.setPreferredSize(new
         * Dimension(300, 200)); panel2.add(jsp, c2);
         */

        // panel2.add(new JLabel("Entry code"), c2);
        jtaEntryCode = new JTextArea();
        jtaEntryCode.setEditable(true);
        jtaEntryCode.setMargin(new Insets(10, 10, 10, 10));
        jtaEntryCode.setTabSize(3);
        String code = "";
        if (entryCode != null) {
            for (i = 0; i < entryCode.length; i++) {
                code += entryCode[i] + "\n";
            }
        }
        jtaEntryCode.append(code);
        jtaEntryCode.setFont(new Font("times", Font.PLAIN, 12));
        JScrollPane jsp = new JScrollPane(jtaEntryCode, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setPreferredSize(new Dimension(300, 200));
        // jsp.setPreferredSize(new Dimension(300, 300));
        panel2.add(jsp, c2);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row

        // Issue #41 Ordering of tabbed panes
        JTabbedPane jtp = GraphicLib.createTabbedPane();// new JTabbedPane();
        jtp.setPreferredSize(new Dimension(400, 450));
        jtp.add("General", panel11);
        jtp.add("Prototyping", panel2);
        c0.fill = GridBagConstraints.BOTH;
        c.add(jtp, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }

    public void actionPerformed(ActionEvent evt) {
        // String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton) {
            closeDialog();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        }
    }

    public void closeDialog() {

        // globalCode = Conversion.wrapText(jtaGlobalCode.getText());
        entryCode = Conversion.wrapText(jtaEntryCode.getText());
        cancelled = false;
        dispose();
    }

    /*
     * public String getActions() { return signal.getText(); }
     */

    public String getGPIOStateName() {
        return stateNameText.getText();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }

    /*
     * public String[] getGlobalCode() { return globalCode; }
     */

    public String[] getEntryCode() {
        return entryCode;
    }

}

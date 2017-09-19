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
import java.util.LinkedList;

/**
 * Class JDialogAvatarTimer
 * Dialog for managing timer set, reset and expire
 * Creation: 15/07/2010
 * @version 1.0 15/07/2010
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarTimer extends JDialogBase implements ActionListener  {

    private LinkedList<String> timers;

    private boolean cancelled = true;
    private JPanel panel1, panel2;

    // Panel1
    private JComboBox<String> listTimers;
    private JButton selectTimer;
    private JTextField ttimer;

    // Panel2
    private JTextField tvalue;

    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    private String timer, value;
    private boolean setValue;


    /** Creates new form  */
    public JDialogAvatarTimer(Frame _f, String _title, String _timer, String _value, LinkedList<String> _timers, boolean _setValue) {

        super(_f, _title, true);

        timers = _timers;
        timer = _timer;
        value = _value;
        setValue = _setValue;

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

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);

        panel1.setBorder(new javax.swing.border.TitledBorder("Timer"));

        panel1.setPreferredSize(new Dimension(300, 150));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel1.add(new JLabel(" "), c1);

        // Combo box
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        listTimers = new JComboBox<String> (timers.toArray (new String[0]));
        panel1.add(listTimers, c1);


        // Selection of the timer
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectTimer = new JButton("Select timer");
        panel1.add(selectTimer, c1);
        selectTimer.setEnabled(timers.size() > 0);
        selectTimer.addActionListener(this);

        // Text
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        ttimer = new JTextField(timer, 30);
        panel1.add(ttimer, c1);
        //panel1.setEditable(true);

        // Panel2
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Timer value"));
        panel2.setPreferredSize(new Dimension(300, 75));
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel2.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        tvalue = new JTextField(value, 30);
        panel2.add(tvalue, c1);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row

        c.add(panel1, c0);
        if (setValue) {
            c.add(panel2, c0);
        }

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(closeButton, cancelButton, c0, c, this);
    }

    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == selectTimer)  {
            selectTimer();
        }
    }

    public void selectTimer() {
        int index = listTimers.getSelectedIndex();
        ttimer.setText(timers.get(index).toString());
    }

    public void closeDialog() {
        cancelled = false;
        dispose();
    }

    public String getTimer() {
        return ttimer.getText();
    }

    public String getValue() {
        return tvalue.getText();
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }
}

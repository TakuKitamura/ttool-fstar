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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarType;
import myutil.TraceManager;
import ui.AvatarSignal;
import ui.TAttribute;
import ui.TGComponent;

/**
 * Class JDialogAvatarQuerySignal
 * Dialog for managing query signals
 * Creation: 07/04/2021
 * @version 1.0 07/04/2021
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarQuerySignal extends JDialogBase implements ActionListener  {

    private List<AvatarSignal> signals, realSignals;
    private List<String> showSignals, showAttributes;

    private String currentValue;

    private List<TAttribute> attributes, realAttributes;



    private boolean cancelled = true;

	private JComboBox<TGComponent> refChecks;

    private JPanel panel1;

    // Panel1
    private JComboBox<String> listSignals, listAttributes;
    private JButton selectSignalAndAttribute;
    private JTextField resultingValue;

    /* Creates new form  */
    public JDialogAvatarQuerySignal(Frame _f, String _title, String _currentValue, List<AvatarSignal> _signals, List<TAttribute> _attributes) {
        super(_f, _title, true);

        attributes = _attributes;
        signals = _signals;
        currentValue = _currentValue;

        makeSignals();

        makeAttributes();

        initComponents();
//        myInitComponents();

        pack();
    }

    private void makeSignals() {
        showSignals = new LinkedList<String> ();
        realSignals = new LinkedList<AvatarSignal> ();

        for (AvatarSignal as: signals)
            if (as.getInOut() == AvatarSignal.IN){
                showSignals.add(as.toString());
                realSignals.add(as);
            }
    }

    private void makeAttributes() {
        showAttributes = new LinkedList<String> ();
        realAttributes = new LinkedList<TAttribute> ();

        for (TAttribute aa: attributes)
            if (aa.getType() == TAttribute.INTEGER){
                showAttributes.add(aa.getId());
                realAttributes.add(aa);
            }
    }



    private void initComponents() {
        Container c = getContentPane();
        //GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        //GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);

        panel1.setBorder(new javax.swing.border.TitledBorder("Signals and attributes"));

        //panel1.setPreferredSize(new Dimension(500, 250));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(" "), c1);

        // Combo box
        c1.fill = GridBagConstraints.HORIZONTAL;
        listSignals = new JComboBox<String> (showSignals.toArray (new String[showSignals.size()]));
        panel1.add(listSignals, c1);

        c1.fill = GridBagConstraints.HORIZONTAL;
        listAttributes = new JComboBox<String> (showAttributes.toArray (new String[showAttributes.size()]));
        panel1.add(listAttributes, c1);


        // Signal
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectSignalAndAttribute = new JButton("Select signal and attribute");
        panel1.add(selectSignalAndAttribute, c1);
        selectSignalAndAttribute.setEnabled((showSignals.size() > 0) && (showAttributes.size() > 0));
        selectSignalAndAttribute.addActionListener(this);

        // Text
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        resultingValue = new JTextField(currentValue, 30);
        panel1.add(resultingValue, c1);
        //panel1.setEditable(true);



        c.add(panel1, BorderLayout.CENTER);

        JPanel buttons = initBasicButtons(this);
        c.add(buttons, BorderLayout.SOUTH);
    }

    @Override
    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == selectSignalAndAttribute)  {
            selectSignal();
        }
    }

    public void selectSignal() {
        int indexS = listSignals.getSelectedIndex();
        int indexA = listAttributes.getSelectedIndex();
        resultingValue.setText(realAttributes.get(indexA) + "=?" + realSignals.get(indexS).getUseDescription());
    }

    public void closeDialog() {
        cancelled = false;
        dispose();
    }

    public String getSignalAndAttribute() {
        return resultingValue.getText();
    }


    public boolean hasValidString() {
        return resultingValue.getText().length() > 0;
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }
}

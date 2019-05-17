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

import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class JDialogMultiString
 * Dialog for managing several string components
 * Creation: 18/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 18/11/2005
 */
public class JDialogMultiString extends JDialogBase implements ActionListener {

    private String[] labels;
    private String[] values;

    private int nbString;

    private boolean set = false;

    private JPanel panel1;

    // Panel1
    private JTextField[] texts;
    private JButton inserts[];
    private Map<Integer, JComboBox<String>> helps;

    private List<String[]> possibleValues = null;


    /*
     * Creates new form
     */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogMultiString(Frame f, String title, int _nbString, String[] _labels, String[] _values) {

        super(f, title, true);

        nbString = _nbString;
        labels = _labels;
        values = _values;


        texts = new JTextField[nbString];

        initComponents();
     //   myInitComponents();
        pack();
    }

    public JDialogMultiString(Frame f, String title, int _nbString, String[] _labels, String[] _values, List<String[]> _possibleValues) {

        super(f, title, true);

        nbString = _nbString;
        labels = _labels;
        values = _values;
        possibleValues = _possibleValues;

        texts = new JTextField[nbString];

        initComponents();
    //    myInitComponents();
        pack();
    }

//    private void myInitComponents() {
//    }

    private void initComponents() {
        inserts = new JButton[labels.length];
        helps = new HashMap<>();

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

        panel1.setBorder(new javax.swing.border.TitledBorder("Properties"));

        //panel1.setPreferredSize(new Dimension(600, 300));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
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

        // String1
        for (int i = 0; i < nbString; i++) {
            c1.gridwidth = 1;
            c1.fill = GridBagConstraints.BOTH;
            panel1.add(new JLabel(labels[i] + " = "), c1);

            if (possibleValues != null) {
                if (i < possibleValues.size()) {
                    String[] tmp = possibleValues.get(i);
                    if (tmp != null) {
                        helps.put(i, new JComboBox<>(tmp));
                        panel1.add(helps.get(i), c1);
                        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
                        inserts[i] = new JButton("Use");
                        inserts[i].addActionListener(this);
                        panel1.add(inserts[i], c1);
                    }
                }
            }
            c1.gridwidth = GridBagConstraints.REMAINDER; //end row
            texts[i] = new JTextField(values[i], 15);
            panel1.add(texts[i], c1);
        }


        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c0.fill = GridBagConstraints.BOTH;
        c.add(panel1, c0);

        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c0, c, this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (command.equals("Save and Close")) {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (inserts != null) {
            for (int i = 0; i < inserts.length; i++) {
                if (evt.getSource() == inserts[i]) {
                    texts[i].setText(helps.get(i).getSelectedItem().toString());
                }
            }
        }
    }

    public void closeDialog() {
        set = true;
        dispose();
    }

    public String getString(int i) {
        return texts[i].getText();
    }

    public boolean hasValidString(int i) {
        return texts[i].getText().length() > 0;
    }


    public boolean hasBeenSet() {
        return set;
    }

    public void cancelDialog() {
        dispose();
    }
}

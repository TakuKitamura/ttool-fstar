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

import ui.GeneralAttribute;
import ui.util.IconManager;
import ui.TAttribute;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Class JDialogGeneralAttribute Dialog for managing general attributes and
 * names Creation: 03/07/2019
 *
 * @author Ludovic APVRILLE
 * @version 2.0 03/07/2019
 */
public class JDialogGeneralAttribute extends JDialogBase implements ActionListener, ListSelectionListener {
    protected java.util.List<GeneralAttribute> attributes, attributesPar, forbidden;
    protected java.util.List<Boolean> initValues;

    protected JPanel panel1, panel2;

    protected Frame frame;

    protected String attrib; // "Attributes", "Gates", etc.

    // Daemon task?
    protected boolean isDaemon;
    protected JCheckBox daemonBox;

    // Operation type
    protected String value;
    protected JPanel panelValue;
    protected JTextField valueField;

    // Panel1
    protected JTextField identifierText;
    protected JTextField initialValue;
    protected JTextField type;
    protected JButton addButton;

    // Panel2
    protected JList<GeneralAttribute> listAttribute;
    protected JButton upButton;
    protected JButton downButton;
    protected JButton removeButton;

    protected String finalValue;

    /* Creates new form */
    public JDialogGeneralAttribute(java.util.List<GeneralAttribute> _attributes,
            java.util.List<GeneralAttribute> _forbidden, Frame f, String title, String attrib, String _value) {
        super(f, title, true);
        frame = f;
        attributesPar = _attributes;
        forbidden = _forbidden;
        initValues = new LinkedList<Boolean>();
        this.attrib = attrib;
        this.value = _value;

        attributes = new LinkedList<GeneralAttribute>();

        for (GeneralAttribute attr : attributesPar)
            attributes.add(attr.makeClone());

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
        panel1.setBorder(new TitledBorder("Adding " + attrib + "s"));
        panel1.setPreferredSize(new Dimension(300, 250));

        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new TitledBorder("Managing " + attrib + "s"));
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
        panel1.add(new JLabel("identifier"), c1);

        panel1.add(new JLabel(" "), c1);
        panel1.add(new JLabel("initial value"), c1);

        panel1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        panel1.add(new JLabel("type"), c1);

        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        identifierText = new JTextField();
        identifierText.setColumns(15);
        identifierText.setEditable(true);
        panel1.add(identifierText, c1);

        initialValue = new JTextField();
        initialValue.setColumns(5);
        initialValue.setEditable(true);

        panel1.add(new JLabel(" = "), c1);
        panel1.add(initialValue, c1);

        panel1.add(new JLabel(" : "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; // end row
        type = new JTextField();
        type.setColumns(15);
        type.setEditable(true);
        panel1.add(type, c1);

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
        listAttribute = new JList<GeneralAttribute>(attributes.toArray(new GeneralAttribute[0]));
        // listAttribute.setFixedCellWidth(150);
        // listAttribute.setFixedCellHeight(20);
        listAttribute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAttribute.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listAttribute);
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

        // Operation panel
        if (value == null) {
            value = "";
        }

        GridBagLayout gbOp = new GridBagLayout();
        GridBagConstraints cOp = new GridBagConstraints();
        panelValue = new JPanel();
        panelValue.setLayout(gbOp);
        panelValue.setBorder(new TitledBorder("Value: stereotype/blockname"));
        // panelOperation.setPreferredSize(new Dimension(500, 70));

        cOp.weighty = 1.0;
        cOp.weightx = 2.0;
        cOp.gridwidth = 4;
        cOp.fill = GridBagConstraints.BOTH;
        cOp.gridheight = 3;
        valueField = new JTextField(value);
        panelValue.add(valueField, cOp);

        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.add(panelValue, c0);

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.fill = GridBagConstraints.BOTH;

        c.add(panel1, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.add(panel2, c0);

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
        } else if (command.equals("Add / Modify " + attrib)) {
            addAttribute();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove " + attrib)) {
            removeAttribute();
        } else if (command.equals("Down")) {
            downAttribute();
        } else if (command.equals("Up")) {
            upAttribute();
        }
    }

    public void addAttribute() {

        String id = identifierText.getText();
        String value = initialValue.getText();
        String typeS = type.getText();
        GeneralAttribute a = new GeneralAttribute(id, value, typeS);

        // checks whether the same attribute already belongs to the list
        int index = attributes.size();

        int indexIn = -1;
        int cpt = 0;
        for (GeneralAttribute ga : attributes) {
            if (ga.getId().equals(id)) {
                indexIn = cpt;
                break;
            }
            cpt++;
        }

        if (indexIn > -1) {
            a = attributes.get(indexIn);
            a.setType(type.getText());
            a.setInitialValue(initialValue.getText());
            a.setId(identifierText.getText());
        } else {
            attributes.add(index, a);
        }
        listAttribute.setListData(attributes.toArray(new GeneralAttribute[0]));
        identifierText.setText("");
    }

    public void removeAttribute() {
        int i = listAttribute.getSelectedIndex();
        if (i != -1) {
            GeneralAttribute a = attributes.get(i);
            attributes.remove(i);
            listAttribute.setListData(attributes.toArray(new GeneralAttribute[0]));
        }
    }

    public void downAttribute() {
        int i = listAttribute.getSelectedIndex();
        if ((i != -1) && (i != attributes.size() - 1)) {
            GeneralAttribute o = attributes.get(i);
            attributes.remove(i);
            attributes.add(i + 1, o);
            listAttribute.setListData(attributes.toArray(new GeneralAttribute[0]));
            listAttribute.setSelectedIndex(i + 1);
        }
    }

    public void upAttribute() {
        int i = listAttribute.getSelectedIndex();
        if (i > 0) {
            GeneralAttribute o = attributes.get(i);
            attributes.remove(i);
            attributes.add(i - 1, o);
            listAttribute.setListData(attributes.toArray(new GeneralAttribute[0]));
            listAttribute.setSelectedIndex(i - 1);
        }
    }

    public void closeDialog() {
        attributesPar.clear();
        for (GeneralAttribute attr : this.attributes)
            attributesPar.add(attr);
        finalValue = valueField.getText();
        dispose();
    }

    public void cancelDialog() {
        dispose();
    }

    public void valueChanged(ListSelectionEvent e) {
        int i = listAttribute.getSelectedIndex();
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            identifierText.setText("");
            // initialValue.setText("");
        } else {
            GeneralAttribute a = attributes.get(i);
            identifierText.setText(a.getId());
            initialValue.setText(a.getInitialValue());
            type.setText(a.getType());
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != attributes.size() - 1) {
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

    public boolean isDaemon() {
        return daemonBox.isSelected();
    }

    public String getValue() {
        return finalValue;
    }

}

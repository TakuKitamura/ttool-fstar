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

import ui.AvatarSignal;
import ui.ColorManager;
import ui.util.IconManager;
import ui.TGComponent;
import myutil.TraceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


/**
 * Class JDialogIDAndStereotype
 * Dialog for managing several names and stereotype
 * Creation: 04/07/2018
 * @version 1.0 04/07/2018
 * @author Ludovic APVRILLE
 */
public class JDialogIDAndStereotype extends JDialogBase implements ActionListener  {

    private String[] availableStereotypes;
    private Color[] colors;
    private String currentName;
    private int currentStereotype;


    private JPanel panel1;

    // Panel1
    private JComboBox<String> listStereotypes;
    private JButton selectStereotype;
    private JTextField stereotype, name;
    private JButton colorButton;
    private JButton useDefaultColor;


    private boolean cancelled;

    /** Creates new form  */
    public JDialogIDAndStereotype(Frame _f, String _title,
                                  String[] _availableStereotypes, String _currentName,
                                  int _currentStereotype, Color[] _colors) {

        super(_f, _title, true);

        availableStereotypes = _availableStereotypes;
        colors = _colors;
        currentName = _currentName;
        currentStereotype = _currentStereotype;

        initComponents();
        myInitComponents();

        pack();
    }



    private void myInitComponents() {
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

        panel1.setBorder(new javax.swing.border.TitledBorder("Requirement"));

        //panel1.setPreferredSize(new Dimension(500, 250));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(" "), c1);

        // Combo box
        listStereotypes = new JComboBox<String>(availableStereotypes);
        listStereotypes.setSelectedIndex(currentStereotype);
        panel1.add(listStereotypes, c1);


        // List of stereotypes
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectStereotype = new JButton("Select stereotype");
        panel1.add(selectStereotype, c1);
        selectStereotype.setEnabled(availableStereotypes.length > 0);
        selectStereotype.addActionListener(this);

        // Text of stereotype
        stereotype = new JTextField(availableStereotypes[currentStereotype], 30);
        c1.gridwidth = 1;
        panel1.add(stereotype, c1);
        colorButton = new JButton();
        colorButton.setBackground(colors[currentStereotype]);
        colorButton.addActionListener(this);
        c1.gridwidth = GridBagConstraints.REMAINDER;
        c1.fill = GridBagConstraints.BOTH;
        panel1.add(colorButton, c1);

        useDefaultColor = new JButton("Use default color");
        useDefaultColor.setBackground(ColorManager.AVATAR_REQUIREMENT_TOP);
        useDefaultColor.addActionListener(this);
        panel1.add(useDefaultColor, c1);
        //panel1.setEditable(true);

        // ID
        c1.fill = GridBagConstraints.HORIZONTAL;
        name = new JTextField(currentName, 30);
        panel1.add(name, c1);
        //panel1.setEditable(true);


        c.add(panel1, BorderLayout.CENTER);

        JPanel buttons = initBasicButtons(this);
        c.add(buttons, BorderLayout.SOUTH);
    }

    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == selectStereotype)  {
            selectStereotype();
        } else if (evt.getSource() == colorButton)  {
            selectColor();
        } else if (evt.getSource() == useDefaultColor)  {
            selectDefaultColor();
        }
    }

    public void selectColor() {
        Color newColor = JColorChooser.showDialog
                (null, "Background color of top box", colorButton.getBackground());
        colorButton.setBackground(newColor);
    }

    public void selectDefaultColor() {
        colorButton.setBackground(ColorManager.AVATAR_REQUIREMENT_TOP);
    }

    public void selectStereotype() {
        int index = listStereotypes.getSelectedIndex();
        stereotype.setText(availableStereotypes[index]);
        colorButton.setBackground(colors[index]);
    }

    public void closeDialog() {
        cancelled = false;
        dispose();
    }

    public String getStereotype() {
        return stereotype.getText();
    }

    public int getColor() {
        return colorButton.getBackground().getRGB();
    }

    public String getName() {
        return name.getText();
    }


    public boolean hasValidString() {
        return stereotype.getText().length() > 0;
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        cancelled = true;
        dispose();
    }
}

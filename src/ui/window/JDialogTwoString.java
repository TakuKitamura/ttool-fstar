/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class JDialogTwoString
 * Dialog for managing two string components
 * Creation: 17/11/2005
 * @version 1.0 17/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ui.*;

public class JDialogTwoString extends javax.swing.JDialog implements ActionListener  {
    
    private String string1, string2;
    private String id1, id2;
    
    private boolean set = false;
    
    private JPanel panel1;
    
    // Panel1
    private JTextField text1, text2;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    
    /** Creates new form  */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogTwoString(Frame f, String title, String _id1, String _string1, String _id2, String _string2) {
        
        super(f, title, true);
       
        id1 = _id1; string1 = _string1;
        id2 = _id2; string2 = _string2;
        
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
           
        panel1.setBorder(new javax.swing.border.TitledBorder("Properties"));
    
        panel1.setPreferredSize(new Dimension(300, 150));
        
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
        panel1.add(new JLabel(id1 + " = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        text1 = new JTextField(string1, 15);
        panel1.add(text1, c1);
        
        // String2
        c1.gridwidth = 1;    
        panel1.add(new JLabel(id2 + " = "),  c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        text2 = new JTextField(string2, 15);
        panel1.add(text2, c1);
        

        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(panel1, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        //closeButton.setPreferredSize(new Dimension(600, 50));
        closeButton.addActionListener(this);
        c.add(closeButton, c0);
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(this);
        c.add(cancelButton, c0);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }
    
    
    public void closeDialog() {
        set = true;
        dispose();
    }
    
    public String getString1() {
        return text1.getText();
    }
    
    public String getString2() {
        return text2.getText();
    }
    
    public boolean hasBeenSet() {
        return set;
    }
    
    public boolean hasValidString() {
        if (text1.getText().length() < 1) {
            return false;
        }
        if (text2.getText().length() < 1) {
            return false;
        }
        return true;
    }
    
    public void cancelDialog() {
        dispose();
    }
}

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
 * Class JDialogTOSClass
 * Dialog for managing remote processes call for simulation
 * Creation: 04/10/2006
 * @version 1.0 04/10/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ui.*;

import ui.oscd.*;

public class JDialogTOSClass extends javax.swing.JDialog implements ActionListener {

    protected MainGUI mgui;

    
    //components
    protected JTextField name, period, deadline;
    protected JComboBox stereotypes;

    protected JButton ok;
    protected JButton cancel;
    
    protected TOSClass tos;
    
    protected boolean changeMade = false;


    /** Creates new form  */
    public JDialogTOSClass(Frame f, String title, TOSClass _tos) {
        super(f, title, true);

        tos = _tos;

        initComponents();
        myInitComponents();
        pack();

    }
    
    
    protected void myInitComponents() {
        setProperties();
    }
    
    protected void initComponents() {
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel jp1 = new JPanel();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        
        
        
        jp1.setLayout(gridbag1);
        jp1.setBorder(new javax.swing.border.TitledBorder("Simulation options"));
        //jp1.setPreferredSize(new Dimension(300, 150));
        
        // first line panel1
        //c1.gridwidth = 3;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridheight = 1;
        
        name = new JTextField(tos.getClassName());
        period = new JTextField(""+tos.getPeriod());
        deadline = new JTextField(""+tos.getDeadline());
        stereotypes = new JComboBox(TOSClass.stereotypes);
        stereotypes.addActionListener(this);
        stereotypes.setSelectedIndex(tos.getStereotype());

        c1.gridwidth = 1;
        jp1.add(new JLabel("name="));
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(name, c1);
        
        c1.gridwidth = 1;
        jp1.add(new JLabel("type="));
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(stereotypes, c1);
        
        c1.gridwidth = 1;
        jp1.add(new JLabel("period="));
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(period, c1);

        c1.gridwidth = 1;
        jp1.add(new JLabel("deadline="));
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(deadline, c1);


        c.add(jp1, BorderLayout.CENTER);

        ok = new JButton("Save and close", IconManager.imgic25);
        cancel = new JButton("Cancel", IconManager.imgic27);

        ok.setPreferredSize(new Dimension(100, 30));
        cancel.setPreferredSize(new Dimension(100, 30));

        ok.addActionListener(this);
        cancel.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(ok);
        jp2.add(cancel);

        c.add(jp2, BorderLayout.SOUTH);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();
        
        
        if (evt.getSource() == stereotypes) {
           setProperties();
           return;
        }

        // Compare the action command to the known actions.
        if (evt.getSource() == ok) {
            changeMade = true;
            dispose();
            return;
        } else if (evt.getSource() == cancel) {
          changeMade = false;
          dispose();
        }
    }
    
    public boolean changeMade() {
           return changeMade;
    }
    
    public String getName() {
           return name.getText();
    }

    public String getPeriod() {
           return period.getText();
    }
    
    public String getDeadline() {
           return deadline.getText();
    }
    
    public int getIndexStereotype() {
           return stereotypes.getSelectedIndex();
    }
    
    
    protected void setProperties() {
              if (stereotypes.getSelectedIndex() == TOSClass.PERIODIC) {
                 period.setEnabled(true);
                 deadline.setEnabled(true);
              } else {
                 period.setEnabled(false);
                 deadline.setEnabled(false);
              }
    }
}

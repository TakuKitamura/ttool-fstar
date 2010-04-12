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
 * Class JDialogAvatarTransition
 * Dialog for managing transitions between states
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;

public class JDialogAvatarTransition extends javax.swing.JDialog implements ActionListener  {
    
    private Vector<String> actions;
	private String guard, afterMin, afterMax, computeMin, computeMax; 
    
    private boolean cancelled = false;
    
    private JPanel panel1;
    
    // Panel1
	private JTextField guardT, afterMinT, afterMaxT, computeMinT, computeMaxT;
	private JTextArea actionsT;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    
    /** Creates new form  */
    // arrayDelay: [0] -> minDelay ; [1] -> maxDelay
    public JDialogAvatarTransition(Frame _f, String _title, String _guard, String _afterMin, String _afterMax, String _computeMin, String _computeMax, Vector<String> _actions) {
        
        super(_f, _title, true);
       
        guard = _guard;
		afterMin = _afterMin;
		afterMax = _afterMax;
		computeMin = _computeMin;
		computeMax = _computeMax;
		actions = _actions;
		
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
           
        panel1.setBorder(new javax.swing.border.TitledBorder("Signals"));
    
        panel1.setPreferredSize(new Dimension(300, 150));
        
        // guard
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = 1;
		c1.gridheight = 1;
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
		panel1.add(new JLabel("guard = "), c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		guardT = new JTextField(guard);
        panel1.add(guardT, c1);
        
        // After
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        panel1.add(new JLabel("after ("), c1);
		afterMinT = new JTextField(afterMin);
        panel1.add(afterMinT, c1);
		panel1.add(new JLabel(","), c1);
		afterMaxT = new JTextField(afterMax);
        panel1.add(afterMaxT, c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		panel1.add(new JLabel(")"), c1);
		
		// Compute
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        panel1.add(new JLabel("compute for ("), c1);
		computeMinT = new JTextField(computeMin);
        panel1.add(computeMinT, c1);
		panel1.add(new JLabel(","), c1);
		computeMaxT = new JTextField(computeMax);
        panel1.add(computeMaxT, c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		panel1.add(new JLabel(")"), c1);
		
        
        // actions
		 c1.gridheight = 10;
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		actionsT = new JTextArea();
        actionsT.setEditable(true);
        actionsT.setMargin(new Insets(10, 10, 10, 10));
        actionsT.setTabSize(3);
        actionsT.setFont(new Font("times", Font.PLAIN, 12));
        actionsT.setPreferredSize(new Dimension(300, 300));
        JScrollPane jsp = new JScrollPane(actionsT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		for(int i=0; i<actions.size(); i++) {
			actionsT.append(actions.get(i) + "\n");
		}
		panel1.add(jsp, c1);
        
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
        //String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } 
    }
    
    public void closeDialog() {
		actions.removeAllElements();
		String[] act = actionsT.getText().split("\n");
		for(int i=0; i<act.length; i++) {
			if (act[0].length() > 0) {
				actions.add(act[i]);
			}
		}
        dispose();
    }
    
    /*public String getActions() {
        return signal.getText();
    }*/
    
    public String getGuard() {
        return guardT.getText();
    }
	
	public String getAfterMin() {
        return afterMinT.getText();
    }
	
	public String getAfterMax() {
        return afterMaxT.getText();
    }
	
	public String getComputeMin() {
        return computeMinT.getText();
    }
	
	public String getComputeMax() {
        return computeMaxT.getText();
    }
	
	public boolean hasBeenCancelled() {
		return cancelled;
	}
    
    public void cancelDialog() {
		cancelled = true;
        dispose();
    }
}

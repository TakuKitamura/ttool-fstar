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
import ui.avatardd.ADDBusNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogADDBusNode
 * Dialog for managing attributes of bus nodes
 * Creation: 02/07/2014
 * @version 1.0 02/07/2014
 * @author Ludovic APVRILLE
 */
public class JDialogADDBusNode extends javax.swing.JDialog implements ActionListener  {
    
    private boolean regularClose;
    
    private JPanel panel2;
    private Frame frame;
    private ADDBusNode node;
    
	
    // Panel1
    protected JTextField nodeName;
	
	// Panel2
    protected JTextField index, nbOfAttachedInitiators, nbOfAttachedTargets, fifoDepth, minLatency;
	
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogADDBusNode(Frame _frame, String _title, ADDBusNode _node) {
        super(_frame, _title, true);
        frame = _frame;
        node = _node;
        
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
        
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("BUS attributes"));
        panel2.setPreferredSize(new Dimension(400, 200));
        
		c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Bus name:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
		panel2.add(nodeName, c1);
 
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
      
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Index:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        index = new JTextField(""+node.getIndex(), 15);
        panel2.add(index, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Nb of attached initators:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfAttachedInitiators = new JTextField(""+node.getNbOfAttachedInitiators(), 15);
        panel2.add(nbOfAttachedInitiators, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Nb of attached target:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfAttachedTargets = new JTextField(""+node.getNbOfAttachedTargets(), 15);
        panel2.add(nbOfAttachedTargets, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Fifo depth:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        fifoDepth = new JTextField(""+node.getFifoDepth(), 15);
        panel2.add(fifoDepth, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Min latency:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        minLatency = new JTextField(""+node.getMinLatency(), 15);
        panel2.add(minLatency, c2);
        
        
        
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        
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
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/
        
        
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        }
    }
    
    public void closeDialog() {
        regularClose = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public boolean isRegularClose() {
        return regularClose;
    }
	
	public String getNodeName() {
        return nodeName.getText();
    }
    
    public String getIndex() {
        return index.getText();
    }
    
    public String getNbOfAttachedInitiators() {
        return nbOfAttachedInitiators.getText();
    }
	
	public String getNbOfAttachedTargets(){
		  return nbOfAttachedTargets.getText();
	  }
	  
	public String getFifoDepth(){
		  return fifoDepth.getText();
	  }
	  
	  public String getMinLatency(){
		  return minLatency.getText();
	  }
 
    
}

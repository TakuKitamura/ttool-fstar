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
import ui.avatarbd.AvatarBDBlock;
import ui.avatarbd.AvatarBDFirewall;
import ui.avatarbd.AvatarBDPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

//import javax.swing.event.*;
//import java.util.*;


/**
 * Class JDialogFirewallNode
 * Dialog for managing attributes of Firewall nodes
 * Creation: 17/10/2007
 * @version 1.0 17/10/2007
 * @author Letitia Li
 */
public class JDialogAvatarFirewall extends JDialogBase implements ActionListener,ListSelectionListener  {
    
    private boolean regularClose;
    
    private JPanel panel2;
    private Frame frame;
    private AvatarBDFirewall node;
    
	
    // Panel1
    protected JTextField nodeName;
    private Vector<String> rules= new Vector<>();
	// Panel2
    protected JTextField latency;
    private JList<String> listRules;
    private JButton addButton,removeButton;
    
    // Main Panel
    private JComboBox<String> task1;
    private JComboBox<String> task2;
    /* Creates new form  */
    public JDialogAvatarFirewall(Frame _frame, String _title, AvatarBDFirewall _node) {
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
        panel2.setBorder(new javax.swing.border.TitledBorder("Firewall attributes"));
        panel2.setPreferredSize(new Dimension(500, 500));
        
	c2.gridwidth = 2;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Firewall name:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        nodeName = new JTextField(node.getNodeName(), 30);
        nodeName.setEditable(true);
        nodeName.setFont(new Font("times", Font.PLAIN, 12));
	panel2.add(nodeName, c2);
	c2.gridwidth=2;
        panel2.add(new JLabel("Latency (CC):"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        latency = new JTextField(""+node.getLatency(), 15);
        panel2.add(latency, c2);

        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        rules.addAll(node.getRules());
	JLabel rulesLabel = new JLabel("Blocking Rules");
	panel2.add(rulesLabel, c2);

	listRules = new JList<>(rules);
        listRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRules.addListSelectionListener(this);
	JScrollPane scrollPane = new JScrollPane(listRules);
        scrollPane.setSize(500, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 10;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        panel2.add(scrollPane, c2);
	c2.gridheight=1;
	c2.gridwidth=1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
	task1 = new JComboBox<>();
	if (node.getTDiagramPanel() instanceof AvatarBDPanel){
		AvatarBDPanel abdp = (AvatarBDPanel) node.getTDiagramPanel();
		for (AvatarBDBlock block: abdp.getFullBlockList()){
		    task1.addItem(block.getName());
		}
		task1.addItem("*");
	
		task2= new JComboBox<>();
		for (AvatarBDBlock block: abdp.getFullBlockList()){
		    task2.addItem(block.getName());
		}
		task2.addItem("*");
	}
	panel2.add(task1, c2);
	panel2.add(new JLabel("--->"),c2);
	c2.gridwidth=GridBagConstraints.REMAINDER;
	panel2.add(task2, c2);

        addButton = new JButton("Add Rule");
        addButton.addActionListener(this);
	panel2.add(addButton,c2);
	removeButton= new JButton("Remove Rule");
	removeButton.addActionListener(this);
	panel2.add(removeButton,c2);
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.add(panel2, c0);
        
        c0.gridwidth = 1;
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
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
        } else if (evt.getSource() == addButton) {
            addRule();
        } else if (evt.getSource() == removeButton) {
            removeRule();
        }
    }
 public void valueChanged(ListSelectionEvent e) {
        int i = listRules.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
        }
    }

     public void removeRule() {
        int i = listRules.getSelectedIndex() ;
        if (i!= -1) {
            rules.removeElementAt(i);
            listRules.setListData(rules);
        }
    }
    public void addRule(){
	String s = task1.getSelectedItem().toString();
	s+= "->";
	s+= task2.getSelectedItem().toString();
	rules.add(s);
	listRules.setListData(rules);
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
    
    public String getLatency() {
        return latency.getText();
    }
    public ArrayList<String> getRules(){
	ArrayList<String> r = new ArrayList<String>();
	for (int i=0; i<rules.size(); i++){
	    r.add(rules.get(i));
	}
	return r;
    }
}

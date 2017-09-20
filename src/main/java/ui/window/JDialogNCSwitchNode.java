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


/**
 * Class JDialogNCSwitchNode
 * Dialog for managing NC switch properties
 * Creation: 19/11/2008
 * @version 1.0 19/11/2008
 * @author Ludovic APVRILLE
 */
public class JDialogNCSwitchNode extends JDialogBase implements ActionListener {

    private String switchName;
    private int schedulingPolicy;
	private int switchingTechnique;
	private int capacity;
	private String capacityUnit;
	private int technicalLatency;
	
	private boolean data;
    
    
    // Panel1
    private JTextField switchNameText;
	private JComboBox<String> schedulingPolicyBox;
	private JComboBox<String> switchingTechniqueBox;
	private JTextField capacityText;
	private JComboBox<String> capacityUnitBox;
	private JTextField technicalLatencyText;

    /** Creates new form  */
    public JDialogNCSwitchNode(Frame _f, String _title, String _switchName, int _schedulingPolicy, int _switchingTechnique, int _capacity, String _capacityUnit, int _technicalLatency) {
        super(_f, _title, true);

        switchName = _switchName;
        schedulingPolicy = _schedulingPolicy;
		switchingTechnique = _switchingTechnique;
		capacity = _capacity;
		capacityUnit = _capacityUnit;
		technicalLatency = _technicalLatency;
        
        myInitComponents();
        initComponents();
        pack();
    }
    
    private void myInitComponents() {
		data = false;
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

        JPanel panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Setting idenfier and capacity "));
        panel1.setPreferredSize(new Dimension(300, 200));
        
        // first line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Switch name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        switchNameText = new JTextField(switchName);
        panel1.add(switchNameText, c1);
        
        c1.gridwidth = 1;
        panel1.add(new JLabel("Scheduling policy:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        schedulingPolicyBox = new JComboBox<>();
		schedulingPolicyBox.addItem("First Come First Served");
		schedulingPolicyBox.addItem("Static Priority");
		schedulingPolicyBox.setSelectedIndex(schedulingPolicy);
        panel1.add(schedulingPolicyBox, c1);
		
		c1.gridwidth = 1;
        panel1.add(new JLabel("Switching technique:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        switchingTechniqueBox = new JComboBox<>();
		switchingTechniqueBox.addItem("Store and Forward");
		switchingTechniqueBox.addItem("Cut Through");
		switchingTechniqueBox.setSelectedIndex(switchingTechnique);
        panel1.add(switchingTechniqueBox, c1);
		
		c1.gridwidth = 1;
        panel1.add(new JLabel("Capacity:"), c1);
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        capacityText = new JTextField(""+capacity);
        panel1.add(capacityText, c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; 
		capacityUnitBox = new JComboBox<>();
		capacityUnitBox.addItem("Mbs");
		capacityUnitBox.addItem("Kbs");
		if (capacityUnit.equals("Mbs")) {
			capacityUnitBox.setSelectedItem(0);
		} else {
			capacityUnitBox.setSelectedItem(1);
		}
		panel1.add(capacityUnitBox, c1);
		
		c1.gridwidth = 1;
        panel1.add(new JLabel("Technical latency:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        technicalLatencyText = new JTextField(""+technicalLatency);
        panel1.add(technicalLatencyText, c1);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(panel1, c0);
        
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        
        initButtons(c0, c, this);
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
        data = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
	public boolean hasBeenCancelled() {
		return !data;
	}
	
    public boolean hasNewData() {
        return data;
    }
    
    public String getSwitchName() {
        return switchNameText.getText();
    }
    
    public int getSchedulingPolicy() {
        return schedulingPolicyBox.getSelectedIndex();
    }
	
	public int getSwitchingTechnique() {
        return switchingTechniqueBox.getSelectedIndex();
    }
	
	public String getCapacity() {
		return capacityText.getText();
	}
	
	public String getCapacityUnit() {
		return capacityUnitBox.getSelectedItem().toString();
	}
	
	public String getTechnicalLatency() {
		return technicalLatencyText.getText();
	}
    
  
}

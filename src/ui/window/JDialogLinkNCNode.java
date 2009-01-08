/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogLinkNCNode
 * Dialog for managing NC links properties
 * Creation: 18/11/2008
 * @version 1.0 18/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;
import ui.tmlcd.*;

public class JDialogLinkNCNode extends javax.swing.JDialog implements ActionListener {
    
    private JPanel panel1;
    private Frame frame;
    
    private String interfaceName;
    private int capacity;
	private String capacityUnit;
	private boolean hasCapacity;
	private int parameter;
	private boolean hasParameter;
	
	private boolean data;
    
    
    
    // Panel1
    private JTextField interfaceNameText, capacityText, parameterText;
	private JCheckBox hasCapacityBox, hasParameterBox;
	private JComboBox capacityUnitBox;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogLinkNCNode(Frame _f, String _title, boolean _hasCapacity, int _capacity, String _capacityUnit, boolean _hasParameter, int _parameter, String _interfaceName) {
        super(_f, _title, true);
        frame = _f;
        
        interfaceName = _interfaceName;
		hasCapacity = _hasCapacity;
        capacity = _capacity;
		capacityUnit = _capacityUnit;
		hasParameter = _hasParameter;
        parameter = _parameter;
        
        myInitComponents();
        initComponents();
        //pack();
		updateComponents();
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
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Id, capacity, parameter"));
        panel1.setPreferredSize(new Dimension(450, 450));
        
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
        panel1.add(new JLabel("Interface name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        interfaceNameText = new JTextField(interfaceName);
        panel1.add(interfaceNameText, c1);
		
		hasCapacityBox = new JCheckBox("has its own capacity", hasCapacity);
		hasCapacityBox.addActionListener(this);
		panel1.add(hasCapacityBox, c1);
        
        c1.gridwidth = 1;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Capacity:"), c1);
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        capacityText = new JTextField(""+capacity);
        panel1.add(capacityText, c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		capacityUnitBox = new JComboBox();
		capacityUnitBox.addItem("Mbs");
		capacityUnitBox.addItem("kbs");
		if (capacityUnit.equals("Mbs")) {
			capacityUnitBox.setSelectedItem(0);
		} else {
			capacityUnitBox.setSelectedItem(1);
		}
		panel1.add(capacityUnitBox, c1);
		
		
		hasParameterBox = new JCheckBox("has its own parameter", hasParameter);
		hasParameterBox.addActionListener(this);
		panel1.add(hasParameterBox, c1);
        
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Parameter:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        parameterText = new JTextField(""+parameter);
        panel1.add(parameterText, c1);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(panel1, c0);
        
        c0.gridheight = 1;
        c0.fill = GridBagConstraints.HORIZONTAL;
        closeButton = new JButton("Save and Close", IconManager.imgic25);
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
		} if (evt.getSource() == hasCapacityBox) {
			updateComponents();
		}
    }
	
	public void updateComponents() {
		boolean b = hasCapacityBox.isSelected();
		capacityText.setEnabled(b);
		capacityUnitBox.setEnabled(b);
	}
    
    
    public void closeDialog() {
        data = true;
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
	public boolean hasBeenCancelled() {
		return (data == false);
	}
	
    public boolean hasNewData() {
        return data;
    }
    
    public String getInterfaceName() {
        return interfaceNameText.getText();
    }
    
    public String getCapacity() {
        return capacityText.getText();
    }
	
	public String getParameter() {
        return parameterText.getText();
    }
	
	public String getCapacityUnit() {
		return capacityUnitBox.getSelectedItem().toString();
	}
	
	public boolean hasCapacity() {
		return hasCapacityBox.isSelected();
	}
	
	public boolean hasParameter() {
		return hasParameterBox.isSelected();
	}
    
  
}

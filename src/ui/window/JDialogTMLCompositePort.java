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
 * Class JDialogTMLCompositePort
 * Dialog for managing port properties
 * Creation: 26/03/2008
 * @version 1.0 26/03/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;

public class JDialogTMLCompositePort extends javax.swing.JDialog implements ActionListener {
	
    private JPanel panel1, panel2;
    private Frame frame;
    
    private String name;
    private int type1, type2, type3;
    private boolean isFinite, isBlocking, isOrigin;
    private String maxInFIFO, widthSamples;

    public boolean data;

    
    
    // Panel1
    private JTextField nameText, maxText, widthText;
    private JComboBox typePort, typeList1, typeList2, typeList3;
    private JComboBox origin, finite, blocking;
	private int portIndex;
    private Vector origins, finites, blockings, portTypes, types1, types2, types3;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    public JDialogTMLCompositePort(String _name, int _portIndex, int _type1, int _type2, int _type3, boolean _isOrigin, boolean _isFinite, boolean _isBlocking, String _maxInFIFO, String _widthSamples, Frame f, String title) {
        super(f, title, true);
        frame = f;
        
        name = _name;
		portIndex = _portIndex;
        type1 = _type1; type2 = _type2; type3 = _type3;
        
        data = false;
        
        maxInFIFO = _maxInFIFO;
		widthSamples = _widthSamples;
		isOrigin = _isOrigin;
        isFinite = _isFinite;
        isBlocking = _isBlocking;
        
        myInitComponents();
        initComponents();
        checkMode();
        pack();
    }
    
    private void myInitComponents() {
		
		portTypes = new Vector();
		portTypes.add("Channel");
		portTypes.add("Event");
		portTypes.add("Request");
		
        types1 = new Vector(); types2 = new Vector(); types3 = new Vector();
        types1.add(TType.getStringType(0));
        types1.add(TType.getStringType(1));
        types1.add(TType.getStringType(2));
        types2.add(TType.getStringType(0));
        types2.add(TType.getStringType(1));
        types2.add(TType.getStringType(2));
        types3.add(TType.getStringType(0));
        types3.add(TType.getStringType(1));
        types3.add(TType.getStringType(2));
		
		origins = new Vector();
		origins.add("Origin");
		origins.add("Destination");
		
		finites = new Vector();
		finites.add("Finite FIFO");
		finites.add("Infinite FIFO");
		
		blockings = new Vector();
		blockings.add("Blocking");
		blockings.add("Non-blocking FIFO");
		
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Name and type "));
        panel1.setPreferredSize(new Dimension(300, 150));
        
        // First line panel1
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
        panel1.add(new JLabel("Name:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        nameText = new JTextField(name);
        panel1.add(nameText, c1);
		
		// Type of port
		c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Type:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        typePort = new JComboBox(portTypes);
        typePort.setSelectedIndex(portIndex);
		typePort.addActionListener(this);
        panel1.add(typePort, c1);
		
		c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel("Origin:"), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        origin = new JComboBox(origins);
		if (isOrigin) {
			origin.setSelectedIndex(0);
		} else {
			origin.setSelectedIndex(1);
		}
		origin.addActionListener(this);
        panel1.add(origin, c1);
		
		
		// parameters
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Parameters "));
        panel2.setPreferredSize(new Dimension(300, 300));
		c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 3;
        panel2.add(new JLabel(" "), c2);
		
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Type #1"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList1 = new JComboBox(types1);
        typeList1.setSelectedIndex(type1);
        panel2.add(typeList1, c2);
        
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Type #2"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList2 = new JComboBox(types2);
        typeList2.setSelectedIndex(type2);
        panel2.add(typeList2, c2);
        
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Type: #3"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        typeList3 = new JComboBox(types3);
        typeList3.setSelectedIndex(type3);
        panel2.add(typeList3, c2);
        
        c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Blocking?"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        blocking = new JComboBox(blockings);
		if (isBlocking) {
			blocking.setSelectedIndex(0);
		} else {
			blocking.setSelectedIndex(1);
		}
		blocking.addActionListener(this);
        panel2.add(blocking, c2);
		
		c2.gridwidth = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        panel2.add(new JLabel("Finite?"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        finite = new JComboBox(finites);
		if (isFinite) {
			finite.setSelectedIndex(0);
		} else {
			finite.setSelectedIndex(1);
		}
		finite.addActionListener(this);
        panel2.add(finite, c2);
        
		c2.gridwidth = 1;
        panel2.add(new JLabel("Width (in Byte)="), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        widthText = new JTextField(widthSamples);
        panel2.add(widthText, c2);
		
        c2.gridwidth = 1;
        panel2.add(new JLabel("Capacity="), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        maxText = new JTextField(maxInFIFO);
        panel2.add(maxText, c2);
        
        /*c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.anchor = GridBagConstraints.CENTER;
        blocking = new JCheckBox("Blocking FIFO");
        blocking.setSelected(isBlocking);
        blocking.addActionListener(this);
        panel2.add(blocking, c1);*/

        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(panel1, c0);
        c.add(panel2, c0);
        
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
        
		checkMode();
		
        /*if (evt.getSource() == finite) {
			if (finite.getSelectedIndex() == 1) {
				blocking.setSelectedIndex(1);
			}
           checkMode();
        }*/

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
    
    public void checkMode() {
		int index = typePort.getSelectedIndex();
		if (index == 0) {
			// channel
			origin.setEnabled(true);
			typeList1.setEnabled(false);  
			typeList2.setEnabled(false);
			typeList3.setEnabled(false);
			blocking.setEnabled(true);
			finite.setEnabled(false);
			widthText.setEnabled(true);
			maxText.setEnabled((origin.getSelectedIndex() == 0) && (blocking.getSelectedIndex() == 0));
			
		} else if (index == 1) {
			// Event
			origin.setEnabled(true);
			typeList1.setEnabled(true);  
			typeList2.setEnabled(true);
			typeList3.setEnabled(true);
			
			if (origin.getSelectedIndex() == 0) {
				blocking.setEnabled(true);
				finite.setEnabled(true);
				if (blocking.getSelectedIndex() == 0) {
					finite.setSelectedIndex(0);
				}
			} else {
				blocking.setEnabled(false);
				blocking.setSelectedIndex(0);
				finite.setEnabled(false);
			}
			
			widthText.setEnabled(false);
			maxText.setEnabled(finite.getSelectedIndex() == 0);
			
		} else {
			// Request
			origin.setEnabled(true);
			typeList1.setEnabled(true);  
			typeList2.setEnabled(true);
			typeList3.setEnabled(true);
			blocking.setEnabled(false);
			if (origin.getSelectedIndex() == 0) {
				blocking.setSelectedIndex(1);
			} else {
				blocking.setSelectedIndex(0);
			}
			finite.setEnabled(false);
			finite.setSelectedIndex(1);
			widthText.setEnabled(false);
			maxText.setEnabled(false);
			
		}
           /*maxText.setEnabled(finite.isSelected());
           blocking.setEnabled(finite.isSelected());*/
    }
    
    public boolean hasNewData() {
        return data;
    }
    
    public String getParamName() {
        return nameText.getText();
    }
    
    public String getMaxSamples() {
           return maxText.getText();
    }
	
	public String getWidthSamples() {
           return widthText.getText();
    }
	
	public boolean isOrigin() {
           return (origin.getSelectedIndex() == 0);
    }
    
    public boolean isFinite() {
           return (finite.getSelectedIndex() == 0);
    }
    
    public boolean isBlocking() {
           return (blocking.getSelectedIndex() == 0);
    }
	
	 public int getPortType() {
		 return typePort.getSelectedIndex();
	 }

    public int getType(int i) {
        switch(i) {
            case 0:
                return typeList1.getSelectedIndex();
            case 1:
                return typeList2.getSelectedIndex();
            case 2:
                return typeList3.getSelectedIndex();
            default:
                return typeList1.getSelectedIndex();
        }
        
    }
}
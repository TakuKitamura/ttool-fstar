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
 * Class JDialogNCRoute
 * Dialog for managing route attributes 
 * Creation: 19/11/2008
 * @version 1.0 19/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import ui.*;
import ui.ncdd.*;


public class JDialogNCRoute extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    private Vector<NCRoute> routes;
	private ArrayList<String> inputInterfaces, traffics, outputInterfaces;
    
    private JPanel panel1, panel2, panel3;
    
    private Frame frame;
    
	protected String value;
	
	protected boolean hasBeenCancelled;
    
    // Panel1
    private JComboBox inputInterfaceBox, trafficBox, outputInterfaceBox;
    private JButton addButton;
    
    //Panel2
    private JList listRoute;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
	
	// Panel3
	private JTextField valueText;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogNCRoute(Frame _f, String _title, String _value, Vector<NCRoute> _routes, ArrayList<String> _inputInterfaces, ArrayList<String> _traffics, ArrayList<String> _outputInterfaces) {
        super(_f, _title, true);
        frame = _f;
		
		value = _value;
		routes = _routes;
		inputInterfaces = _inputInterfaces;
		outputInterfaces = _outputInterfaces;
		traffics = _traffics;
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
		GridBagLayout gridbag3 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
		GridBagConstraints c3 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Adding a route"));
        panel1.setPreferredSize(new Dimension(500, 350));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Managing routes"));
        panel2.setPreferredSize(new Dimension(300, 250));
		
		panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Setting route name"));
        panel3.setPreferredSize(new Dimension(300, 50));
		
		// first line panel3
		c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel("Name: "), c3);
		c3.gridwidth = GridBagConstraints.REMAINDER; //end row
		valueText = new JTextField(value);
		valueText.setColumns(15);
		panel3.add(valueText);
        
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
        panel1.add(new JLabel("input interface"), c1);
		panel1.add(new JLabel(" "), c1);
        panel1.add(new JLabel("traffic"), c1);
        panel1.add(new JLabel(" "), c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel1.add(new JLabel("output interface"), c1);
        
        // second line panel1
        c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        inputInterfaceBox = new JComboBox();
		for(String s0: inputInterfaces) {
			inputInterfaceBox.addItem(s0);
		}
		if (inputInterfaces.size() < 2) {
			inputInterfaceBox.setEnabled(false);
		}
		inputInterfaceBox.addActionListener(this);
        panel1.add(inputInterfaceBox, c1);
		
		panel1.add(new JLabel(" / "), c1);
		
		trafficBox = new JComboBox();
		for(String s1: traffics) {
			trafficBox.addItem(s1);
		}
        panel1.add(trafficBox, c1);
		
		panel1.add(new JLabel(" / "), c1);
		
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		
		outputInterfaceBox = new JComboBox();
		for(String s2: outputInterfaces) {
			outputInterfaceBox.addItem(s2);
		}
		if (outputInterfaces.size() < 2) {
			outputInterfaceBox.setEnabled(false);
		} else {
			outputInterfaceBox.setSelectedIndex(1);
		}
		outputInterfaceBox.addActionListener(this);
        panel1.add(outputInterfaceBox, c1);
        
        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        // fourth line panel2
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add route");
        addButton.addActionListener(this);
        panel1.add(addButton, c1);
        
        // 1st line panel2
        listRoute = new JList(routes);
        //listAttribute.setFixedCellWidth(150);
        //listAttribute.setFixedCellHeight(20);
        listRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listRoute.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listRoute);
        scrollPane.setSize(300, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
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
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel2.add(upButton, c2);
        
        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel2.add(downButton, c2);
        
        removeButton = new JButton("Remove route");
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.add(panel3, c0);
		c0.gridwidth = 1;
        c.add(panel1, c0);
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
        
        String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (command.equals("Save and Close"))  {
            closeDialog();
        } else if (command.equals("Add route")) {
            addRoute();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove route")) {
            removeRoute();
        } else if (command.equals("Down")) {
            downRoute();
        } else if (command.equals("Up")) {
            upRoute();
        } else if (evt.getSource() == inputInterfaceBox) {
			manageInputBox();
		} else if (evt.getSource() == outputInterfaceBox) {
			manageOutputBox();
		}
    }
	
	public void manageInputBox() {
		int index = inputInterfaceBox.getSelectedIndex();
		int indexOther = outputInterfaceBox.getSelectedIndex();
		
		if (indexOther == index) {
			indexOther = (indexOther + 1) % outputInterfaceBox.getItemCount();
			outputInterfaceBox.setSelectedIndex(indexOther);
		}
	}
	
	public void manageOutputBox() {
		int index = inputInterfaceBox.getSelectedIndex();
		int indexOther = outputInterfaceBox.getSelectedIndex();
		
		if (indexOther == index) {
			index = (index + 1) % inputInterfaceBox.getItemCount();
			inputInterfaceBox.setSelectedIndex(index);
		}
	}
    
    
    public void addRoute() {
        String s0 = (String)(inputInterfaceBox.getSelectedItem());
		String s1 = (String)(trafficBox.getSelectedItem());
		String s2 = (String)(outputInterfaceBox.getSelectedItem());
		
		NCRoute route = new NCRoute(s0, s1, s2);
		
		for(NCRoute r: routes) {
			if (r.equals(route)) {
				return;
			}
		}
		routes.add(route);
		listRoute.setListData(routes);
		
    }
    
    public void removeRoute() {
        int i = listRoute.getSelectedIndex() ;
        if (i!= -1) {
            NCRoute r = (NCRoute)(routes.elementAt(i));
            routes.removeElementAt(i);
            listRoute.setListData(routes);
        }
    }
    
    public void downRoute() {
        int i = listRoute.getSelectedIndex();
        if ((i!= -1) && (i != routes.size() - 1)) {
            NCRoute o = routes.elementAt(i);
            routes.removeElementAt(i);
            routes.insertElementAt(o, i+1);
            listRoute.setListData(routes);
            listRoute.setSelectedIndex(i+1);
        }
    }
    
    public void upRoute() {
        int i = listRoute.getSelectedIndex();
        if (i > 0) {
            NCRoute o = routes.elementAt(i);
            routes.removeElementAt(i);
            routes.insertElementAt(o, i-1);
            listRoute.setListData(routes);
            listRoute.setSelectedIndex(i-1);
        }
    }
    
    
    public void closeDialog() {
        //attributesPar.removeAllElements();
        //for(int i=0; i<attributes.size(); i++) {
        //    attributesPar.addElement(attributes.elementAt(i));
        //}
        dispose();
    }
    
    public void cancelDialog() {
		hasBeenCancelled = true;
        dispose();
    }
	
	public boolean hasBeenCancelled() {
		return hasBeenCancelled;
	}
    
    public void valueChanged(ListSelectionEvent e) {
        int i = listRoute.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            NCRoute route = routes.get(i);
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != routes.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }
	
	public String getValue() {
		return valueText.getText();
	}
    
    
}

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
* Class JPanelSetVariables
* Creation: 11/06/2009
* version 1.0 11/06/2009
* @author Ludovic APVRILLE
* @see
*/

package ui.interactivesimulation;

//import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


import myutil.*;
import ui.*;
import ui.file.*;

import tmltranslator.*; 

import launcher.*;
import remotesimulation.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


public	class JPanelSetVariables extends JPanel implements ActionListener  {
	
	
	private JFrameInteractiveSimulation jfis;
	
 
	
	private JComboBox tasks;
	private JComboBox variables;
	private JTextField currentValue, newValue;
	private JButton setButton;
	
	private String[] taskIDs, variableIDs;
	
	private Hashtable <Integer, String> valueTable;
	
	
	public JPanelSetVariables(JFrameInteractiveSimulation _jfis, Hashtable <Integer, String> _valueTable) {
		super();
		
		jfis = _jfis;
		
		taskIDs = jfis.makeTasksIDs();
		valueTable = _valueTable;
		
		makeComponents();
		setComponents();
	}
	
	public void makeComponents() {
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
		setLayout(gridbag2);
		setBorder(new javax.swing.border.TitledBorder("Variables setting"));
		
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        add(new JLabel(""), c2);
		
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
		
		add(new JLabel(" "), c2);
		
		if (taskIDs == null) {
			tasks = new JComboBox();
		} else {
			tasks = new JComboBox(taskIDs);
			tasks.addActionListener(this);
		}
		add(tasks, c2);
		
		if ((taskIDs == null) || (taskIDs.length == 0)) {
			variables = new JComboBox();
		} else {
			variableIDs = jfis.makeVariableIDs(0);
			variables = new JComboBox(variableIDs);
			variables.addActionListener(this);
		}
		add(variables, c2);
        
		c2.gridwidth = 1;
		add(new JLabel("Current value: "), c2);
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        currentValue = new JTextField(getCurrentVariableValue(), 30);
		currentValue.setEditable(false);
		add(currentValue, c2);
		
		add(new JLabel(""), c2);
		
		c2.gridwidth = 1;
		add(new JLabel("New value: "), c2);
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        newValue = new JTextField("", 30);
		newValue.setEditable(true);
		add(newValue, c2);
		
		setButton = new JButton("Set variable");
        setButton.addActionListener(this);
        add(setButton, c2);
	}
	
	public void setComponents() {
		if ((variableIDs != null) && (variableIDs.length > 0)){
			setButton.setEnabled(true);
			newValue.setEditable(true);
		} else {
			setButton.setEnabled(false);
			newValue.setEditable(false);
		}
	}
	
    public void	actionPerformed(ActionEvent evt)  {
        
        // Compare the action command to the known actions.
        if (evt.getSource() == setButton)  {
			setValue();
        } else if (evt.getSource() == tasks) {
			variableIDs = jfis.makeVariableIDs(tasks.getSelectedIndex());
			variables.removeAllItems();
			for(int i=0; i<variableIDs.length; i++) {
				variables.addItem(variableIDs[i]);
			}
			setComponents();
		} else if (evt.getSource() == variables) {
			currentValue.setText(getCurrentVariableValue());
		} 
    }
	
	
	public void setValue() {
		int idTask = getID((String)(tasks.getSelectedItem()));
		int idVariable = getID((String)(variables.getSelectedItem()));
		String val = newValue.getText().trim();
		
		if ((idTask == -1) || (idVariable == -1) || (val.length() == 0)) {
			return;
		}
		
		jfis.setVariables(idTask, idVariable, val);
	}
	
	public int getID(String s) {
		if (s == null) {
			return -1;
		}
		int index0 = s.indexOf("(");
		int index1 = s.indexOf(")");
		if ((index0 < 0) || (index1 <0) || (index1 < index0)) {
			return -1;
		}
		
		String in = s.substring(index0+1, index1);
		
		try {
			return Integer.decode(in).intValue();
		} catch (Exception e) {
			System.out.println("Wrong string: "+ in);
		}
		
		return -1;
	}
	
	protected String getCurrentVariableValue() {
		int id = getID((String)(variables.getSelectedItem()));
		if (id == -1) {
			return " - ";
		}
		String val = valueTable.get(id);
		if (val == null) {
			val = " - ";
		}
		return val;
		
	}
	
	protected void updateOnVariableValue(String _idVar) {
		int id = getID((String)(variables.getSelectedItem()));
		if (_idVar.equals(""+id)) {
			currentValue.setText(getCurrentVariableValue());
		}
	}
	
	protected void unsetElements() {
		setButton.setEnabled(false);
		newValue.setEditable(false);
		variables.setEnabled(false);
		variables.removeAllItems();
		variables.removeActionListener(this);
		tasks.setEnabled(false);
		tasks.removeActionListener(this);
		tasks.removeAllItems();
		currentValue.setText(" ");
		currentValue.setEnabled(false);
	}
	

	
	
} // Class
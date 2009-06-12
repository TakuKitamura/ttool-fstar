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
* Class JPanelBreakPoints
* Creation: 04/06/2009
* version 1.0 04/06/2009
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


public	class JPanelBreakPoints extends JPanel implements ActionListener, ListSelectionListener  {
	
	
	private JFrameInteractiveSimulation jfis;
	private ArrayList<Point> points;
	private Vector breakpoints;
	
	
	private JList listBreakpoints;
    private JButton removeButton;
	private JButton addButton;
	private JComboBox tasks;
	private JComboBox commands;
	private JCheckBox activate;
	
	private String[] taskIDs, commandIDs;
	
	
	public JPanelBreakPoints(JFrameInteractiveSimulation _jfis, ArrayList<Point> _points) {
		super();
		
		jfis = _jfis;
		
		points = _points;
		
		//setBackground(new Color(50, 40, 40, 200));
		
		breakpoints = new Vector();
        
        for(int i=0; i<points.size(); i++) {
            breakpoints.addElement("Task=" + points.get(i).x +  " Command=" + points.get(i).y);
        }
		
		taskIDs = jfis.makeTasksIDs();
		
		makeComponents();
		setComponents();
	}
	
	public void makeComponents() {
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
		setLayout(gridbag2);
		setBorder(new javax.swing.border.TitledBorder("Managing breakpoints"));
		
		listBreakpoints = new JList(breakpoints);
        //listAttribute.setFixedCellWidth(150);
        //listAttribute.setFixedCellHeight(20);
        listBreakpoints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listBreakpoints.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listBreakpoints);
        scrollPane.setSize(300, 250);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 5;
        c2.weighty = 10.0;
        c2.weightx = 10.0;
        add(scrollPane, c2);
        
        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        add(new JLabel(""), c2);
		
		c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
		
		
        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        add(removeButton, c2);
		
		add(new JLabel(" "), c2);
		
		if (taskIDs == null) {
			tasks = new JComboBox();
		} else {
			tasks = new JComboBox(taskIDs);
			tasks.addActionListener(this);
		}
		add(tasks, c2);
		
		if ((taskIDs == null) || (taskIDs.length == 0)) {
			commands = new JComboBox();
		} else {
			commandIDs = jfis.makeCommandIDs(0);
			commands = new JComboBox(commandIDs);
		}
		add(commands, c2);
        
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        add(addButton, c2);
		
		activate = new JCheckBox("Breakpoints activated");
        activate.addActionListener(this);
        add(activate, c2);
		activate.setSelected(true);
		
        
	}
	
	public void setComponents() {
		removeButton.setEnabled(false);
		if (commandIDs != null) {
			addButton.setEnabled(true);
		} else {
			addButton.setEnabled(false);
		}
	}
	
    public void	actionPerformed(ActionEvent evt)  {
        
        // Compare the action command to the known actions.
        if (evt.getSource() == removeButton)  {
			removeBreakpoint();
        } else if (evt.getSource() == addButton)  {
			addBreakpoint();
        } else if (evt.getSource() == tasks) {
			commandIDs = jfis.makeCommandIDs(tasks.getSelectedIndex());
			commands.removeAllItems();
			for(int i=0; i<commandIDs.length; i++) {
				commands.addItem(commandIDs[i]);
			}
		} else if (evt.getSource() == activate) {
			jfis.activeBreakPoint(activate.isSelected());
		}
    }
	
	public void valueChanged(ListSelectionEvent e) {
		int i = listBreakpoints.getSelectedIndex() ;
		if (i == -1) {
            removeButton.setEnabled(false);
        } else {
			removeButton.setEnabled(true);
		}
		
	}
	
	public void removeBreakpoint() {
		int i = listBreakpoints.getSelectedIndex() ;
		if (i != -1) {
			Point p = points.get(i);
			points.remove(i);
			breakpoints.removeElementAt(i);
            listBreakpoints.setListData(breakpoints);
			jfis.removeBreakpoint(p);
			jfis.printMessage("Breakpoint removed");
		}
	}
	
	public void addBreakpoint() {
		String t = (String)(tasks.getSelectedItem());
		String c = (String)(commands.getSelectedItem());
		
		if ((t != null) && (c!= null)) {
			int tid = getID(t);
			int cid = getID(c);
			if ((cid > -1) && (tid > -1)) {
				// Must be sure than that breakpoint is not already set
				for(Point p: points) {
					if (p.y == cid) {
						return;
					}
				}
				
				Point p0 = new Point(tid, cid);
				points.add(p0);
				breakpoints.addElement("Task=" + p0.x +  " Command=" + p0.y);
				listBreakpoints.setListData(breakpoints);
				jfis.addBreakpoint(p0);
				jfis.printMessage("Breakpoint added");
			}
		}
	}
	
	public int getID(String s) {
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
	
	public void removeExternalBreakpoint(int _index) {
		points.remove(_index);
		breakpoints.removeElementAt(_index);
		listBreakpoints.setListData(breakpoints);
	}
	
	public void addExternalBreakpoint(int _taskID, int _commandID) {
		points.add(new Point(_taskID, _commandID));
		breakpoints.addElement("Task=" + _taskID +  " Command=" + _commandID);
		listBreakpoints.setListData(breakpoints);
	}
	
	public void unsetElements() {
		Vector v = new Vector();
		listBreakpoints.setListData(v);
		removeButton.setEnabled(false);
		addButton.setEnabled(false);
		tasks.removeActionListener(this);
		tasks.setEnabled(false);
		tasks.removeAllItems();
		tasks.setEnabled(false);
		commands.setEnabled(false);
		commands.removeAllItems();
	}
	
	
	
	
	
	
} // Class
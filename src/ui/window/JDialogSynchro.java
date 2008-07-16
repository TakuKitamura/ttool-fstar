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
 * Class JDialogSynchro
 * Dialog for managing OCL formulas of Synchro relations
 * Creation: 13/12/2003
 * @version 1.0 13/12/2003
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
import ui.cd.*;


public class JDialogSynchro extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    private Vector gatesOfT1, gatesOfT2, synchro, newSynchro;
    private TClassSynchroInterface t1, t2;
    //private TDiagramPanel _tdp;
    
    private JPanel panel1, panel2;
    
    // Panel1
    private JComboBox gatesBox1, gatesBox2;
    private JButton addButton;
    
    //Panel2
    private JList listGates;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    /** Creates new form  */
    public JDialogSynchro(Frame f, TClassSynchroInterface _t1, TClassSynchroInterface _t2, Vector _synchro, TCDSynchroGateList _tcdsgl, String title) {
        super(f, title, true);
        t1 = _t1;
        t2 = _t2;
        synchro = _synchro;
        
        // Getting the gates of both tclasses
        gatesOfT1 = t1.gatesNotSynchronizedOn(_tcdsgl);
        gatesOfT2 = t2.gatesNotSynchronizedOn(_tcdsgl);
        
        // check that all gates of synchro are also gates of t1 and t2
        checkGates(synchro);
        
        // clone synchronization gates -> for cancel
        newSynchro = (Vector)(synchro.clone());
        
        // remove gates involved in synchro
        adjustGatesOfTClasses();
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void checkGates(Vector sync) {
        TTwoAttributes tt;
        int i;
        
        for(i=0; i<sync.size(); i++) {
            tt = (TTwoAttributes)(sync.elementAt(i));
            if ((tt.t1 != t1) || (tt.t2 != t2) || (!gatesOfT1.contains(tt.ta1)) || (!gatesOfT2.contains(tt.ta2))) {
                sync.remove(tt);
                i --;
            } else if  ((tt.ta1.getAccess() != TAttribute.PUBLIC) || (tt.ta2.getAccess() != TAttribute.PUBLIC)) {
                sync.remove(tt);
                i --;
            }
        }
    }
    
    private void adjustGatesOfTClasses() {
        int i;
        TTwoAttributes tt;
        
        for(i=0; i<newSynchro.size(); i++) {
            tt = (TTwoAttributes)(newSynchro.elementAt(i));
            if (gatesOfT1.contains(tt.ta1)) {
                gatesOfT1.remove(tt.ta1);
            }
            if (gatesOfT2.contains(tt.ta2)) {
                gatesOfT2.remove(tt.ta2);
            }
        }
        
        //checking access : only pubic gates can be connected
        int access;
        TAttribute a;
        
        for(i=0; i<gatesOfT1.size(); i++) {
            a = (TAttribute)(gatesOfT1.elementAt(i));
            access = a.getAccess();
            if (access != TAttribute.PUBLIC) {
                gatesOfT1.removeElementAt(i);
                i--;
            }
        }
        
        for(i=0; i<gatesOfT2.size(); i++) {
            a = (TAttribute)(gatesOfT2.elementAt(i));
            access = a.getAccess();
            if (access != TAttribute.PUBLIC) {
                gatesOfT2.removeElementAt(i);
                i--;
            }
        }
    }
    
    private void myInitComponents() {
        removeButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        makeComboBoxes();
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
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("adding gates"));
        panel1.setPreferredSize(new Dimension(400, 250));
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("removing gates"));
        panel2.setPreferredSize(new Dimension(300, 250));
        
        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        // second line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        
        gatesBox1 = new JComboBox();
        panel1.add(gatesBox1, c1);
        c1.gridwidth = 1;
        panel1.add(new JLabel(" = "), c1);
        
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        gatesBox2 = new JComboBox();
        panel1.add(gatesBox2, c1);
        
        // third line panel1
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        panel1.add(new JLabel(" "), c1);
        
        // fourth line panel1
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add Gates");
        addButton.addActionListener(this);
        panel1.add(addButton, c1);
        
        // 1st line panel2
        listGates = new JList(newSynchro);
        listGates.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listGates.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listGates);
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
        
        removeButton = new JButton("Remove Gates");
        removeButton.addActionListener(this);
        panel2.add(removeButton, c2);
        
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        
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
        } else if (command.equals("Add Gates")) {
            addSynchro();
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (command.equals("Remove Gates")) {
            removeSynchro();
        } else if (command.equals("Down")) {
            downSynchro();
        } else if (command.equals("Up")) {
            upSynchro();
        }
    }
    
    private void makeComboBoxes() {
        gatesBox1.removeAllItems();
        gatesBox2.removeAllItems();
        
        int i;
        TAttribute g;
        
        for(i=0; i<gatesOfT1.size(); i++) {
            g = (TAttribute)(gatesOfT1.elementAt(i));
            gatesBox1.addItem(t1.getValue() + "." + g.getId());
        }
        
        for(i=0; i<gatesOfT2.size(); i++) {
            g = (TAttribute)(gatesOfT2.elementAt(i));
            gatesBox2.addItem(t2.getValue() + "." + g.getId());
        }
    }
    
    public void addSynchro() {
        int i1 = gatesBox1.getSelectedIndex();
        int i2 = gatesBox2.getSelectedIndex();
        TTwoAttributes tt;
        
        if ((i1 > -1) && (i2 > -1)) {
            TAttribute g1 = (TAttribute)(gatesOfT1.elementAt(i1));
            TAttribute g2 = (TAttribute)(gatesOfT2.elementAt(i2));
            
            tt = new TTwoAttributes(t1, t2, g1, g2);
            newSynchro.add(tt);
            gatesOfT1.removeElementAt(i1);
            gatesOfT2.removeElementAt(i2);
            makeComboBoxes();
            listGates.setListData(newSynchro);
        }
    }
    
    public void removeSynchro() {
        int i = listGates.getSelectedIndex() ;
        if (i!= -1) {
            TTwoAttributes tt = (TTwoAttributes)(newSynchro.elementAt(i));
            gatesOfT1.add(tt.ta1);
            gatesOfT2.add(tt.ta2);
            makeComboBoxes();
            newSynchro.removeElementAt(i);
            listGates.setListData(newSynchro);
        }
    }
    
    public void downSynchro() {
        int i = listGates.getSelectedIndex();
        if ((i!= -1) && (i != newSynchro.size() - 1)) {
            Object o = newSynchro.elementAt(i);
            newSynchro.removeElementAt(i);
            newSynchro.insertElementAt(o, i+1);
            listGates.setListData(newSynchro);
            listGates.setSelectedIndex(i+1);
        }
    }
    
    public void upSynchro() {
        int i = listGates.getSelectedIndex();
        if (i > 0) {
            Object o = newSynchro.elementAt(i);
            newSynchro.removeElementAt(i);
            newSynchro.insertElementAt(o, i-1);
            listGates.setListData(newSynchro);
            listGates.setSelectedIndex(i-1);
        }
    }
    
    
    public void closeDialog() {
        synchro.removeAllElements();
        for(int i=0; i<newSynchro.size(); i++) {
            synchro.addElement(newSynchro.elementAt(i));
        }
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int i = listGates.getSelectedIndex() ;
        if (i == -1) {
            removeButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
            if (i > 0) {
                upButton.setEnabled(true);
            } else {
                upButton.setEnabled(false);
            }
            if (i != newSynchro.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }
    
}

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
 * Class JDialogLinkNode
 * Dialog for managing information on links between nodes
 * Creation: 09/05/2005
 * @version 1.0 09/05/2005
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

public class JDialogLinkNode extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
    
    private String delay, lossRate;
    private int implementation, oport, dport;
    private Vector lothers, rothers, associations;
    private boolean cancel = false;
    
    private JPanel panel1, panel2, panel3, panel4, panel5;
    
    // Panel1
    private JTextField jdelay, jlossRate;
    
    // Panels2
    String[] impStrings = { "None", "UDP", "TCP", "RMI" };
    JComboBox jimp;
    JTextField joport, jdport;
    
    // Panels 3, 4 and 5
    JComboBox gatesBox1, gatesBox2;
    JButton addButton, upButton, downButton, removeButton;
    JList listGates;
    
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
    
    //private String id1, id2;
    
    /** Creates new form  */
    public JDialogLinkNode(Frame f, String _delay, String _lossRate, int _implementation, int _oport, int _dport, Vector _lothers, Vector _rothers, Vector _associations) {
        
        super(f, "Setting link's properties", true);
       
        delay = _delay;
        lossRate = _lossRate;
        implementation = _implementation;
        oport = _oport;
        dport = _dport;
        
        // Danger -> vectors should be duplicated
        lothers = new Vector(_lothers);
        rothers = new Vector(_rothers);
        associations = new Vector(_associations);
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    
    private void myInitComponents() {
        selectActions();
        makeComboBoxes();
    }
    
    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        //tabbedPane.setPreferredSize(new Dimension(550, 400));
        
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagLayout gridbag4 = new GridBagLayout();
        //GridBagLayout gridbag5 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        GridBagConstraints c3 = new GridBagConstraints();
        GridBagConstraints c4 = new GridBagConstraints();
        //GridBagConstraints c5 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel1 = new JPanel();
        panel1.setLayout(gridbag1);
           
        panel1.setBorder(new javax.swing.border.TitledBorder("Properties"));
    
        //panel1.setPreferredSize(new Dimension(600, 350));
        
        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        panel1.add(new JLabel(" "), c1);
        
        // second line panel1
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.anchor = GridBagConstraints.CENTER;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        
        // name
        panel1.add(new JLabel("Delay = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jdelay = new JTextField(delay, 15);
        panel1.add(jdelay, c1);
        
        // loss rate
        c1.gridwidth = 1;     
        panel1.add(new JLabel("Loss rate = "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jlossRate = new JTextField(lossRate, 15);
        panel1.add(jlossRate, c1);
        
        tabbedPane.addTab("Verification", panel1);
        
        // Panel2
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
           
        panel2.setBorder(new javax.swing.border.TitledBorder("Properties"));
    
        //panel2.setPreferredSize(new Dimension(500, 350));
        
        // first line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panel2.add(new JLabel(" "), c2);
        
        // second line panel1
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.anchor = GridBagConstraints.CENTER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Implementation = "), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        jimp = new JComboBox(impStrings);
        jimp.setSelectedIndex(implementation);
        jimp.addActionListener(this);
        panel2.add(jimp, c1);
        
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.anchor = GridBagConstraints.CENTER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Origin port = "), c1);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        joport = new JTextField(""+oport, 15);
        panel2.add(joport, c1);
        
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.anchor = GridBagConstraints.CENTER;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Destination port = "), c1);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        jdport = new JTextField(""+dport, 15);
        panel2.add(jdport, c1);
        
        tabbedPane.addTab("Implementation", panel2);
        
        
        
        
        panel3 = new JPanel();
        panel3.setLayout(gridbag3);
        panel3.setBorder(new javax.swing.border.TitledBorder("Adding gates"));
        //panel3.setPreferredSize(new Dimension(300, 350));
        
        panel4 = new JPanel();
        panel4.setLayout(gridbag4);
        panel4.setBorder(new javax.swing.border.TitledBorder("Removing gates"));
        //panel4.setPreferredSize(new Dimension(275, 350));
        
        // first line panel3
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);
        
        // second line panel3
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.anchor = GridBagConstraints.CENTER;
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.anchor = GridBagConstraints.CENTER;
        
        gatesBox1 = new JComboBox();
        panel3.add(gatesBox1, c3);
        c3.gridwidth = 1;
        panel3.add(new JLabel(" = "), c3);
        
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        gatesBox2 = new JComboBox();
        panel3.add(gatesBox2, c3);
        
        // third line panel3
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 3;
        panel3.add(new JLabel(" "), c3);
        
        // fourth line panel3
        c3.gridheight = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        addButton = new JButton("Add Gate association");
        addButton.addActionListener(this);
        panel3.add(addButton, c3);
        
        // 1st line panel4
        listGates = new JList(associations);
        listGates.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listGates.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(listGates);
        //scrollPane.setSize(300, 350);
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 5;
        c4.weighty = 10.0;
        c4.weightx = 10.0;
        panel4.add(scrollPane, c4);
        
        // 2nd line panel4
        c4.weighty = 1.0;
        c4.weightx = 1.0;
        c4.fill = GridBagConstraints.BOTH;
        c4.gridheight = 1;
        panel4.add(new JLabel(""), c4);
        
        // third line panel4
        c4.gridwidth = GridBagConstraints.REMAINDER; //end row
        c4.fill = GridBagConstraints.HORIZONTAL;
        upButton = new JButton("Up");
        upButton.addActionListener(this);
        panel4.add(upButton, c4);
        
        downButton = new JButton("Down");
        downButton.addActionListener(this);
        panel4.add(downButton, c4);
        
        removeButton = new JButton("Remove Gates");
        removeButton.addActionListener(this);
        panel4.add(removeButton, c4);
        
        
        // main panel;
        panel5 = new JPanel();
        //panel5.setPreferredSize(new Dimension(600, 350));
        panel5.setLayout(new BorderLayout());
        //panel5.setLayout(gridbag5);
        //panel5.setBorder(new javax.swing.border.TitledBorder("Adding gates"));
        //panel5.setPreferredSize(new Dimension(400, 250));
        /*c5.gridwidth = 1;
        c5.gridheight = 10;
        c5.weighty = 1.0;
        c5.weightx = 1.0;*/
        
        panel5.add(panel3, BorderLayout.WEST);
        //c5.gridwidth = GridBagConstraints.REMAINDER; //end row
        panel5.add(panel4, BorderLayout.EAST);
        
        tabbedPane.addTab("Gates", panel5);
        
   
        // main panel;
        c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        c.add(tabbedPane, c0);
        
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
        } else if (command.equals("Cancel")) {
            cancelDialog();
        } else if (evt.getSource() == jimp) {
           selectActions();
        } else if (command.equals("Add Gate association")) {
            addSynchro();
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
        
        for(i=0; i<lothers.size(); i++) {
            gatesBox1.addItem(lothers.elementAt(i));
            //System.out.println("lothers +" + lothers.elementAt(i));
        }
        
        for(i=0; i<rothers.size(); i++) {
            gatesBox2.addItem(rothers.elementAt(i));
            //System.out.println("lothers +" + rothers.elementAt(i));
        }
    }
    
    public void addSynchro() {
        int i1 = gatesBox1.getSelectedIndex();
        int i2 = gatesBox2.getSelectedIndex();
        LRArtifactTClassGate lratg;
        
        if ((i1 > -1) && (i2 > -1)) {
            ArtifactTClassGate atg1 = (ArtifactTClassGate)(lothers.elementAt(i1));
            ArtifactTClassGate atg2 = (ArtifactTClassGate)(rothers.elementAt(i2));
            
            lratg = new LRArtifactTClassGate(atg1, atg2);
            associations.add(lratg);
            lothers.removeElementAt(i1);
            rothers.removeElementAt(i2);
            makeComboBoxes();
            listGates.setListData(associations);
        }
    }
    
    public void removeSynchro() {
        int i = listGates.getSelectedIndex() ;
        if (i!= -1) {
            LRArtifactTClassGate lratg = (LRArtifactTClassGate)(associations.elementAt(i));
            lothers.add(lratg.left);
            rothers.add(lratg.right);
            makeComboBoxes();
            associations.removeElementAt(i);
            listGates.setListData(associations);
        }
    }
    
    public void downSynchro() {
        int i = listGates.getSelectedIndex();
        if ((i!= -1) && (i != associations.size() - 1)) {
            Object o = associations.elementAt(i);
            associations.removeElementAt(i);
            associations.insertElementAt(o, i+1);
            listGates.setListData(associations);
            listGates.setSelectedIndex(i+1);
        }
    }
    
    public void upSynchro() {
        int i = listGates.getSelectedIndex();
        if (i > 0) {
            Object o = associations.elementAt(i);
            associations.removeElementAt(i);
            associations.insertElementAt(o, i-1);
            listGates.setListData(associations);
            listGates.setSelectedIndex(i-1);
        }
    }
    
    public void selectActions() {
         boolean b = (jimp.getSelectedIndex() == 1) || (jimp.getSelectedIndex() == 2);
         joport.setEnabled(b);
         jdport.setEnabled(b);
         valueChanged(null);
    }
    
    
    public void closeDialog() {
        delay = jdelay.getText();
        lossRate = jlossRate.getText();
        implementation = jimp.getSelectedIndex();
        try {
           oport = Integer.decode(joport.getText()).intValue();
        } catch (Exception e) {
            oport = -1;
        }
        
        try {
           dport = Integer.decode(jdport.getText()).intValue();
        } catch (Exception e) {
           dport = -1;
        }
        
        dispose();
    }
    
    public String getDelay() {
        return delay;
    }
    
    public String getLossRate() {
        return lossRate;
    }
    
    public int getImplementation() {
        return implementation;
    }
    
    public int getOport() {
        return oport;
    }
    
    public int getDport() {
        return dport;
    }
    
    public Vector getAssociations() {
        return associations;
    }
    
    public void cancelDialog() {
        delay = null;
        lossRate = null;
        implementation = -1;
        oport = -1;
        dport = -1;
        associations = null;
        cancel = true;
        dispose();
    }
    
    public boolean hasCancelled() {
        return cancel;
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
            if (i != associations.size() - 1) {
                downButton.setEnabled(true);
            } else {
                downButton.setEnabled(false);
            }
        }
    }
}

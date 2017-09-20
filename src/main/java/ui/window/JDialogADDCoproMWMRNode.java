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
import ui.avatardd.ADDCoproMWMRNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import javax.swing.event.*;
//import java.util.*;


/**
* Class JDialogADDCoproMWMRNode
* Dialog for managing attributes of Copro nodes
* Creation: 22/08/2014
* @version 1.0 22/08/2014
* @author Ludovic APVRILLE
 */
public class JDialogADDCoproMWMRNode extends JDialogBase implements ActionListener  {
		
		private boolean regularClose;
		
		private JPanel panel2;
		private Frame frame;
		private ADDCoproMWMRNode node;
		
		
		// Panel1
		protected JTextField nodeName;
		
		// Panel2
		private JTextField srcid; // initiator id 
		private JTextField tgtid; // target id
		private JTextField plaps; // configuration of integrated timer
		private JTextField fifoToCoprocDepth;
		private JTextField fifoFromCoprocDepth;    
		private JTextField nToCopro; // Nb of channels going to copro
		private JTextField nFromCopro; // Nb of channels coming from copro
		private JTextField nConfig; // Nb of configuration registers
		private JTextField nStatus; // nb of status registers
		
		private String[] choices = { "false", "true"};
		private JComboBox<String> useLLSC;
		
		/** Creates new form  */
		public JDialogADDCoproMWMRNode(Frame _frame, String _title, ADDCoproMWMRNode _node) {
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
				panel2.setBorder(new javax.swing.border.TitledBorder("Copro attributes"));
				//panel2.setPreferredSize(new Dimension(400, 200));
				
				c1.gridwidth = 1;
				c1.gridheight = 1;
				c1.weighty = 1.0;
				c1.weightx = 1.0;
				c1.fill = GridBagConstraints.HORIZONTAL;
				panel2.add(new JLabel("Timer node name:"), c2);
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
				
				
				c2.gridwidth = 1; panel2.add(new JLabel("srcid:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; srcid = new JTextField(""+node.getSrcid(), 15); panel2.add(srcid, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("tgtid:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; tgtid = new JTextField(""+node.getTgtid(), 15); panel2.add(tgtid, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("plaps:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; plaps = new JTextField(""+node.getPlaps(), 15); panel2.add(plaps, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("fifoToCoprocDepth:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; fifoToCoprocDepth = new JTextField(""+node.getFifoToCoprocDepth(), 15); panel2.add(fifoToCoprocDepth, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("fifoFromCoprocDepth:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; fifoFromCoprocDepth = new JTextField(""+node.getFifoFromCoprocDepth(), 15); panel2.add(fifoFromCoprocDepth, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("nToCopro:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; nToCopro = new JTextField(""+node.getNToCopro(), 15); panel2.add(nToCopro, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("nFromCopro:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; nFromCopro = new JTextField(""+node.getNFromCopro(), 15); panel2.add(nFromCopro, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("nConfig:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; nConfig = new JTextField(""+node.getNConfig(), 15); panel2.add(nConfig, c2);
				
				c2.gridwidth = 1; panel2.add(new JLabel("nStatus:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER; nStatus = new JTextField(""+node.getNStatus(), 15); panel2.add(nStatus, c2);
				
				
				//Create the combo box, select item at index 4.
				//Indices start at 0, so 4 specifies the pig.
				
				useLLSC = new JComboBox<>(choices);
				useLLSC.setSelectedIndex((node.getUseLLSC()) ? 1 : 0);
				c2.gridwidth = 1; panel2.add(new JLabel("use LLSC:"), c2);
				c2.gridwidth = GridBagConstraints.REMAINDER;
				panel2.add(useLLSC, c2);
				
				
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
		
		public String getSrcid() {
				return srcid.getText();
		}
		
		public String getTgtid() {
				return tgtid.getText();
		}
		
		public String getPlaps() {
				return plaps.getText();
		}
		
		public String getFifoToCoprocDepth() {
				return fifoToCoprocDepth.getText();
		}
		
		public String getFifoFromCoprocDepth() {
				return fifoFromCoprocDepth.getText();
		}
		
		public String getNToCopro() {
				return nToCopro.getText();
		}
		
		public String getNFromCopro() {
				return nFromCopro.getText();
		}
		
		public String getNConfig() {
				return nConfig.getText();
		}
		
		public String getNStatus() {
				return nStatus.getText();
		}
		
		public boolean getUseLLSC() {
				return (useLLSC.getSelectedIndex() == 1);
		}
		
		
}

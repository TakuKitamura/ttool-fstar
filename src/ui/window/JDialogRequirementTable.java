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
 * Class JDialogRequirementTable
 * Dialog for setting requirement tables
 * Creation: 19/02/2009
 * @version 1.0 19/02/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;

public class JDialogRequirementTable extends javax.swing.JDialog implements ActionListener {
    
    private JPanel panel1;
    private Frame frame;
    
    private String name;
    public static String[] items = {"none", "ID", "Name", "Type", "Description", "Kind", "Criticality", "Violated action", "Attack Tree Nodes", "Satisfied"};
    public static String[] sizes = {"0", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500"};
   
	private int nbColumn = 9;
    private static int[] selectedItems = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static int[] selectedSizes = {1, 3, 2, 6, 2, 2, 2, 2, 1};
	
    // Panel1
    private JTextField nameText;
    private JComboBox[] itemBoxes, sizeBoxes;
    private Vector types1, types2, types3;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;
	
	private boolean data = false;
    
    /** Creates new form  */
    public JDialogRequirementTable(JFrame f, String title) {
        super(f, title, true);
        frame = f;
        
        myInitComponents();
        initComponents();
        pack();
    }
    
    private void myInitComponents() {
        itemBoxes = new JComboBox[nbColumn];
		sizeBoxes = new JComboBox[nbColumn];
    }
    
    private void initComponents() {
		JLabel label;
		
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
        panel1.setBorder(new javax.swing.border.TitledBorder("Setting columns parameters "));
        panel1.setPreferredSize(new Dimension(500, 300));
        
        // first line panel1
        //c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 3;
        
		for (int i=0; i<nbColumn; i++) {
			c1.gridwidth = 1;
			panel1.add(new JLabel("Column #" + i), c1);
			label = new JLabel("element:");
			label.setHorizontalTextPosition(JLabel.RIGHT);
			panel1.add(label, c1);
			itemBoxes[i] = new JComboBox(items);
			itemBoxes[i].setSelectedIndex(selectedItems[i]);
			panel1.add(itemBoxes[i], c1);
			label = new JLabel("  size:");
			label.setHorizontalTextPosition(JLabel.RIGHT);
			panel1.add(label, c1);
			c1.gridwidth = GridBagConstraints.REMAINDER;
			sizeBoxes[i] = new JComboBox(sizes);
			sizeBoxes[i].setSelectedIndex(selectedSizes[i]);
			panel1.add(sizeBoxes[i], c1);
		}
        
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
        }
    }
    
    
    public void closeDialog() {
        data = true;
		updateStaticValues();
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
	
	private void  updateStaticValues() {
		for(int i=0; i<nbColumn; i++) {
			selectedItems[i] = itemBoxes[i].getSelectedIndex();
			selectedSizes[i] = sizeBoxes[i].getSelectedIndex();
		}
	}
	
	public boolean hasBeenCancelled() {
		return !data;
	}
	
	
	// x = columnType
	// y = columnSize
	public Point[] getColumnsInfo() {
		int nbRealColumn = 0;
		int i, cpt;
		Point p;
		
		for(i=0; i<nbColumn; i++) {
			if (itemBoxes[i].getSelectedIndex() > 0) {
				nbRealColumn ++;
			}
		}
		
		if (nbRealColumn < 1) {
			return null;
		}
		
		Point [] pts = new Point[nbRealColumn];
		
		cpt = 0;
		for(i=0; i<nbColumn; i++) {
			if (itemBoxes[i].getSelectedIndex() > 0) {
				p = new Point();
				p.x = itemBoxes[i].getSelectedIndex();
				p.y = sizeBoxes[i].getSelectedIndex();
				pts[cpt] = p;
				cpt ++;
			}
		}
		
		return pts;
	}
    
}
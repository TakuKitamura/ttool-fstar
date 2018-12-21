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
import java.util.ArrayList;


/**
 * Class JDialogRequirementTable
 * Dialog for setting requirement tables
 * Creation: 19/02/2009
 * @version 1.0 19/02/2009
 * @author Ludovic APVRILLE
 */
public class JDialogRequirementTable extends JDialogBase implements ActionListener {

    // Direct sons / fathers
    // All sons / fathers
    // Property verifiying the req
    // Elements satisfying the req.

    static String[] items = {"none", "ID", "Stereotype", "Name",
            "Description", "Kind", "Criticality", "Violated action", "Targeted " +
            "attacks", "Satisfied", "Reference elements", "Custom attributes",
            "Verification Properties", "Satisfied by", "Immediate sons",
            "All sons", "Immediate fathers", "All fathers", "Me -> refine -> Other",
            "Other -> refine -> Me", "Me -> derive -> Other", "Other -> derive -> Me"};

    private static String[] sizes = {"0", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500"};
   
	private int nbColumn = 21;
    private static int[] selectedItems = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
	private static int[] selectedSizes = {1, 3, 2, 6, 2, 2, 2, 2, 1, 5,  5,  5,  5,  6,  6,   6, 6,   6,  6,  6,  6};
	
    // Panel1
    private ArrayList<JComboBox<String>> itemBoxes, sizeBoxes;

    private boolean data = false;
    
    /* Creates new form  */
    public JDialogRequirementTable(JFrame f, String title) {
        super(f, title, true);

        myInitComponents();
        initComponents();
        pack();
    }
    
    private void myInitComponents() {
        itemBoxes = new ArrayList<>();
		sizeBoxes = new ArrayList<>();
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

        JPanel panel1 = new JPanel();
        panel1.setLayout(gridbag1);
        panel1.setBorder(new javax.swing.border.TitledBorder("Setting columns parameters "));
        panel1.setPreferredSize(new Dimension(500, 500));
        
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
			itemBoxes.add(new JComboBox<>(items));
			itemBoxes.get(i).setSelectedIndex(selectedItems[i]);
			panel1.add(itemBoxes.get(i), c1);
			label = new JLabel("  size:");
			label.setHorizontalTextPosition(JLabel.RIGHT);
			panel1.add(label, c1);
			c1.gridwidth = GridBagConstraints.REMAINDER;
			sizeBoxes.add(new JComboBox<>(sizes));
			sizeBoxes.get(i).setSelectedIndex(selectedSizes[i]);
			panel1.add(sizeBoxes.get(i), c1);
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


        initButtons(c0, c, this);
        renameSaveButton("Show table");
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        
        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton) {
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
			selectedItems[i] = itemBoxes.get(i).getSelectedIndex();
			selectedSizes[i] = sizeBoxes.get(i).getSelectedIndex();
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
			if (itemBoxes.get(i).getSelectedIndex() > 0) {
				nbRealColumn ++;
			}
		}
		
		if (nbRealColumn < 1) {
			return null;
		}
		
		Point [] pts = new Point[nbRealColumn];
		
		cpt = 0;
		for(i=0; i<nbColumn; i++) {
			if (itemBoxes.get(i).getSelectedIndex() > 0) {
				p = new Point();
				p.x = itemBoxes.get(i).getSelectedIndex();
				p.y = sizeBoxes.get(i).getSelectedIndex();
				pts[cpt] = p;
				cpt ++;
			}
		}
		
		return pts;
	}
    
}
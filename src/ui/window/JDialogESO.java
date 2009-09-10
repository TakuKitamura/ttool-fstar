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
 * Class JDialogESO
 * Dialog for managing attributes of Event Sequence Operators (ESO)
 * Creation: 10/09/2009
 * @version 1.0 10/09/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;
//import java.util.*;

import ui.*;
import ui.ebrdd.*;

import ui.tmldd.*;


public class JDialogESO extends javax.swing.JDialog implements ActionListener  {
    
    private boolean regularClose;
    
    private JPanel panel2;
    private Frame frame;
	private EBRDDESO eso;
 
	// Panel
    protected JTextField timeout, n ,m;
	protected JComboBox ids, oncePerEvent;
    
    // Main Panel
    private JButton closeButton;
    private JButton cancelButton;

    
    /** Creates new form  */
    public JDialogESO(Frame _frame, EBRDDESO _eso) {
        super(_frame, "Event Sequence Operator", true);
        frame = _frame;
        eso = _eso;
        
        initComponents();
        myInitComponents();
        pack();
    }
    
    private void myInitComponents() {
		checkMode();
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        //GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("ESO attributes"));
        panel2.setPreferredSize(new Dimension(400, 300));
        
		c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Operator:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        ids = new JComboBox(EBRDDESO.ESOS);
		/*for(int i=0; i<EBRDDESO.ESOS.length; i++) {
			 ids.addItem(EBRDDESO.ESOS[i]);
		}*/
		ids.addActionListener(this);
		ids.setSelectedIndex(eso.getID());
        panel2.add(ids, c2);
        
        c2.gridwidth = 1;
        panel2.add(new JLabel("Timeout:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        timeout = new JTextField(""+eso.getTimeout(), 15);
        panel2.add(timeout, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("Once Per Event:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
		oncePerEvent = new JComboBox();
		oncePerEvent.addItem("false");
		oncePerEvent.addItem("true");
		if (eso.getOncePerEvent()) {
			oncePerEvent.setSelectedIndex(1);
		} else {
			oncePerEvent.setSelectedIndex(0);
		}
        panel2.add(oncePerEvent, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("At least:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        n = new JTextField(""+eso.getN(), 15);
        panel2.add(n, c2);
		
		c2.gridwidth = 1;
        panel2.add(new JLabel("At most:"), c2);
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        m = new JTextField(""+eso.getM(), 15);
        panel2.add(m, c2);
		
        
        // main panel;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
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
	
	private void checkMode() {
		if (timeout != null) {
			int index = ids.getSelectedIndex();
			oncePerEvent.setEnabled(eso.hasOncePerEvent(index));
			timeout.setEnabled(eso.hasTimeout(index));
			n.setEnabled(eso.hasNM(index));
			m.setEnabled(eso.hasNM(index));
		}
	}
	
	
    public void	actionPerformed(ActionEvent evt)  {
       /* if (evt.getSource() == typeBox) {
            boolean b = ((Boolean)(initValues.elementAt(typeBox.getSelectedIndex()))).booleanValue();
            initialValue.setEnabled(b);
            return;
        }*/
        
		checkMode();
        
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
	
	public int getID() {
		return ids.getSelectedIndex();
	}
	
	public String getTimeout() {
		return timeout.getText();
	}
	
	public boolean getOncePerEvent() {
		return (oncePerEvent.getSelectedIndex() == 1);
	}
	
	public String getN() {
        return n.getText();
    }
	
	public String getM() {
        return m.getText();
    }
    
}

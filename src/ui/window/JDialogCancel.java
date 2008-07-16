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
 * Class JDialogCancel
 * Dialog for managing cancel over other frames
 * Creation: 27/04/2007
 * @version 1.0 27/04/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.*;

public class JDialogCancel extends javax.swing.JDialog implements ActionListener, Runnable  {
	
	private static String DEFAULT_WAIT = "Please wait";

	private StoppableGUIElement sge;
	private Thread t;
	

	private JButton cancelButton;
	private String info;
	private JLabel percentage;
	private JLabel message;
	private String wait = DEFAULT_WAIT;
	private int cpt = 0;

	/** Creates new form  */
	// arrayDelay: [0] -> minDelay ; [1] -> maxDelay
	public JDialogCancel(Frame f, String _title, String _info, StoppableGUIElement _sge) {

		super(f, _title, true);

		sge = _sge;
		info = _info;

		initComponents();
		myInitComponents();
		//pack();
	}


	private void myInitComponents() {
		t = new Thread(this);
		t.start();
	}

	private void initComponents() {
		Container c = getContentPane();
		GridBagLayout gridbag0 = new GridBagLayout();
		GridBagConstraints c0 = new GridBagConstraints();

		setFont(new Font("Helvetica", Font.PLAIN, 14));
		c.setLayout(gridbag0);

		c0.gridwidth = GridBagConstraints.REMAINDER; //end row


		c.add(new JLabel("          "), c0);
		message = new JLabel(info);
		c.add(message, c0);
		c.add(new JLabel("          "), c0);
		percentage = new JLabel("0%");
		c.add(percentage, c0);
		c.add(new JLabel("          "), c0);
		cancelButton = new JButton("Cancel", IconManager.imgic27);
		cancelButton.addActionListener(this);

		c.add(cancelButton, c0);
	}

	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();

		// Compare the action command to the known actions.
		if (command.equals("Cancel")) {
			cancelDialog();
		}
	}

	public void run() {
		int percentage;
		try {
			while(sge !=null) {
				Thread.sleep(100);
				if (sge != null) {
					percentage = sge.getPercentage();
					setPercentage(percentage);
					setCurrentMessage(sge.getCurrentActivity());
				}
			}
		} catch (InterruptedException ie) {}
	}
	
	public void setPercentage(int p) {
		if (p <0) {
			cpt ++;
			if (cpt%10 == 0) {
				wait += ".";
				if (cpt%100 == 0) {
					wait = DEFAULT_WAIT;
					cpt = 0;
				}
				percentage.setText(wait);
				repaint();
			}
			
		} else {
			percentage.setText(p+"%");
			repaint();
		}
	}
	
	public void setCurrentMessage(String _msg) {
		message.setText(_msg);
		repaint();
	}



	public void cancelDialog() {
		if (sge != null) {
			sge.stopElement();
		}
		sge = null;
		dispose();
	}

	public void stopAll() {
		sge = null;
		dispose();
	}
}

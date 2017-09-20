package ui.window;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import ui.util.IconManager;

/**
 * Class JDialogBase
 * Superclass for dialog inheritance 
 * Creation: 19/09/2017
 * @version 1.0 19/09/2017
 * @author Fabien TESSIER
*/

public class JDialogBase extends JDialog {
	
	protected JButton cancelButton, closeButton;
	
	protected JDialogBase(Frame _frame, String _title, boolean b) {
		super(_frame, _title, b);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
                dispose();
        	}
        });
	}
	
	protected JDialogBase(Frame _frame, String _title, Dialog.ModalityType m) {
		super(_frame, _title, m);
		
		//Add closeButton's behaviour on Escape key
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
                dispose();
        	}
        });
	}
	
	protected void initButtons(GridBagConstraints c0, Container c,
			                  ActionListener al) {
		//Close Button
        closeButton = new JButton("Save and Close", IconManager.imgic25);
        closeButton.addActionListener(al);
        c.add(closeButton, c0);
        
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        //Cancel Button
        cancelButton = new JButton("Cancel", IconManager.imgic27);
        cancelButton.addActionListener(al);
        c.add(cancelButton, c0);
        
        //Add closeButton's behaviour on Enter key
        this.getRootPane().setDefaultButton(closeButton);
	}
}

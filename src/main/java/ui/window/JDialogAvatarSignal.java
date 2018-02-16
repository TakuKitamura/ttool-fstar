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

import ui.AvatarSignal;
import ui.util.IconManager;
import ui.TGComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
/**
 * Class JDialogAvatarSignal
 * Dialog for managing several string components
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarSignal extends JDialogBase implements ActionListener  {

    private LinkedList<AvatarSignal> signals, realSignals;
    private LinkedList<String> showSignals;
    private String currentSignal;
    private boolean isOut;
	
	private TGComponent reference;
    private Vector<TGComponent> refs;

    private boolean cancelled = true;

	private JComboBox<TGComponent> refChecks;

    private JPanel panel1;

    // Panel1
    private JComboBox<String> listSignals;
    private JButton selectSignal;
    private JTextField signal;

    /** Creates new form  */
    public JDialogAvatarSignal(Frame _f, String _title, String _currentSignal, LinkedList<AvatarSignal> _signals, boolean _isOut, TGComponent _reference, Vector<TGComponent> _refs) {

        super(_f, _title, true);

        signals = _signals;
        currentSignal = _currentSignal;
        isOut = _isOut;
		reference=_reference;
		refs=_refs;


        makeSignals();

        initComponents();
        myInitComponents();

        pack();
    }

    private void makeSignals() {
        showSignals = new LinkedList<String> ();
        realSignals = new LinkedList<AvatarSignal> ();

        for (AvatarSignal as: signals)
            if (((as.getInOut() == AvatarSignal.OUT) && (isOut)) ||  ((as.getInOut() == AvatarSignal.IN) && (!isOut))){
                showSignals.add(as.toString());
                realSignals.add(as);
            }
    }


    private void myInitComponents() {
    }

    private void initComponents() {
        Container c = getContentPane();
        //GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        //GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel1 = new JPanel();
        panel1.setLayout(gridbag1);

        panel1.setBorder(new javax.swing.border.TitledBorder("Signals"));

        //panel1.setPreferredSize(new Dimension(500, 250));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        listSignals = new JComboBox<String>(showSignals.toArray (new String[0]));
        panel1.add(new JLabel(" "), c1);

        // Combo box
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        listSignals = new JComboBox<String> (showSignals.toArray (new String[0]));
        panel1.add(listSignals, c1);


        // Signal
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectSignal = new JButton("Select signal");
        panel1.add(selectSignal, c1);
        selectSignal.setEnabled(showSignals.size() > 0);
        selectSignal.addActionListener(this);

        // Text
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        signal = new JTextField(currentSignal, 30);
        panel1.add(signal, c1);
        //panel1.setEditable(true);

		//Reference to DIPLODOCUS signal or Requirement
		c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
		panel1.add(new JLabel("Reference Requirement"),c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		refChecks = new JComboBox<TGComponent>(refs);
		refChecks.insertItemAt(null, 0);
		if (reference!=null){
			refChecks.setSelectedItem(reference);
		}
		panel1.add(refChecks,c1);


        // main panel;
        /*c0.gridwidth = 1;
        c0.gridheight = 10;
        c0.weighty = 1.0;
        c0.weightx = 1.0;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row*/

        c.add(panel1, BorderLayout.CENTER);




        //c0.gridwidth = 1;
        //c0.gridheight = 1;
        //c0.fill = GridBagConstraints.HORIZONTAL;

        initButtons(c, this);
    }

    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == selectSignal)  {
            selectSignal();
        }
    }

    public void selectSignal() {
        int index = listSignals.getSelectedIndex();
        signal.setText(realSignals.get(index).getUseDescription());
    }

    public void closeDialog() {
        cancelled = false;
        dispose();
    }

    public String getSignal() {
        return signal.getText();
    }
	
	public TGComponent getReference(){
		return (TGComponent) refChecks.getSelectedItem();
	}


    public boolean hasValidString() {
        return signal.getText().length() > 0;
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }
}

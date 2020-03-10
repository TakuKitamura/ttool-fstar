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

    //vrac
/*for(AvatarAmsCluster amsCluster : TopCellGenerator.avatardd.getAllAmsCluster ()) {
            if(amsCluster.getNo_amsCluster() == 0) {
	    code += "if(strcmp(name, \""+amsCluster.getAmsClusterName()+"\") == 0) {\n";*/
		//end vrac


package ui.window;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import myutil.TraceManager;
import ui.AvatarSignal;
import ui.TGComponent;

import ddtranslatorSoclib.AvatarAmsCluster;

/**
 * Class JDialogAvatarGPIOState
 * Dialog for managing several string components
 * Creation: 12/04/2010
 * @version 1.0 12/04/2010
 * @author Ludovic APVRILLE
 */
public class JDialogAvatarGPIOState extends JDialogBase implements ActionListener  {

    //private List<AvatarGPIO> gpios, realGpios;
    private List<AvatarAmsCluster> gpios, realGpios;
    private List<String> showGPIO;
    private String currentGPIO;
    private boolean isOut;
	
	private TGComponent reference;
    private Vector<TGComponent> refs;

    private boolean cancelled = true;

	private JComboBox<TGComponent> refChecks;

    private JPanel panel1;

    // Panel1
    private JComboBox<String> listGPIO;
    private JButton selectGPIO;
    private JTextField GPIO;

    /* Creates new form  */
    public JDialogAvatarGPIOState(Frame _f, String _title, String _currentGPIO, List<AvatarAmsCluster> _GPIOs, boolean _isOut, TGComponent _reference, Vector<TGComponent> _refs) {
        super(_f, _title, true);

        GPIOs = _GPIOs;
        currentGPIO = _currentGPIO;
        isOut = _isOut;
		reference=_reference;
		refs=_refs;

        makeGPIOs();

        initComponents();
//        myInitComponents();

        pack();
    }

    private void makeGPIOs() {
        showGPIOs = new LinkedList<String> ();
        realGPIOs = new LinkedList<AvatarGPIO> ();


	for(AvatarAmsCluster amsCluster : TopCellGenerator.avatardd.getAllAmsCluster ()) {
            if(amsCluster.getNo_amsCluster() == 0) {
	        showGPIOs.add(amsCluster.getAmsClusterName());
		realGPIOs.add(amsCluster);
		//DG toDo distinguish GPIO's in and out signals
	
		/*    for (AvatarGPIO as: GPIOs)
            if (((as.getInOut() == AvatarGPIO.OUT) && (isOut)) ||  ((as.getInOut() == AvatarGPIO.IN) && (!isOut))){
                showGPIOs.add(as.toString());
                realGPIOs.add(as);
		}*/
		
	    }
	}
    }

//    private void myInitComponents() {
//    }

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

        panel1.setBorder(new javax.swing.border.TitledBorder("GPIOs"));

        //panel1.setPreferredSize(new Dimension(500, 250));

        // first line panel1
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        c1.anchor = GridBagConstraints.CENTER;
        panel1.add(new JLabel(" "), c1);

        // Combo box
        c1.fill = GridBagConstraints.HORIZONTAL;
        listGPIOs = new JComboBox<String> (showGPIOs.toArray (new String[showGPIOs.size()]));
        panel1.add(listGPIOs, c1);


        // GPIO
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        selectGPIO = new JButton("Select GPIO by name");
        panel1.add(selectGPIO, c1);
        selectGPIO.setEnabled(showGPIOs.size() > 0);
        selectGPIO.addActionListener(this);

        // Text
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        GPIO = new JTextField(currentGPIO, 30);
        panel1.add(GPIO, c1);
        //panel1.setEditable(true);

		//Reference to DIPLODOCUS GPIO or Requirement
		c1.gridwidth = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.anchor = GridBagConstraints.CENTER;
		panel1.add(new JLabel("Reference Requirement"),c1);
		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		refChecks = new JComboBox<TGComponent>(refs);
		refChecks.insertItemAt(null, 0);
		TraceManager.addDev("Reference=" + reference);
		if (reference != null){
			refChecks.setSelectedItem(reference);
		} else {
		    refChecks.setSelectedIndex(0);
        }
		panel1.add(refChecks,c1);

        c.add(panel1, BorderLayout.CENTER);

        JPanel buttons = initBasicButtons(this);
        c.add(buttons, BorderLayout.SOUTH);
    }

    @Override
    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton)  {
            closeDialog();
        } else if (evt.getSource() == cancelButton)  {
            cancelDialog();
        } else if (evt.getSource() == selectGPIO)  {
            selectGPIO();
        }
    }

    public void selectGPIO() {
        int index = listGPIOs.getSelectedIndex();
        GPIO.setText(realGPIOs.get(index).getUseDescription());
    }

    public void closeDialog() {
        cancelled = false;
        dispose();
    }

    /*  public String getGPIO() {
        return GPIO.getText();
	}*/

    public String getGPIOName() {
        return GPIO.getText();
	}
    
	public TGComponent getReference(){
		return (TGComponent) refChecks.getSelectedItem();
	}

    public boolean hasValidString() {
        return GPIO.getText().length() > 0;
    }

    public boolean hasBeenCancelled() {
        return cancelled;
    }

    public void cancelDialog() {
        dispose();
    }
}

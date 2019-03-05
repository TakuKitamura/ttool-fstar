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

import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.modelcompiler.*;
import ui.util.IconManager;
import ui.tmldd.TMLArchiArtifact;
import ui.tmldd.TMLArchiDiagramPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

//import javax.swing.event.*;

/**
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifacts on hw nodes
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 */
public class JDialogTMLTaskArtifact extends JDialogBase implements ActionListener {

	private boolean regularClose;
	private boolean emptyList = false;
    
	private JPanel panel2, panel3;
 // private Frame frame;
	private TMLArchiArtifact artifact;
	private String operation = "VOID";
	private ArchUnitMEC MECType;
    
  //protected JTextField taskName;
	protected JComboBox<String> referenceTaskName, priority, operationsListCB;
	
	private JTabbedPane tabbedPane;
    
    /* Creates new form  */
  public JDialogTMLTaskArtifact(Frame _frame, String _title, TMLArchiArtifact _artifact, String _operation, ArchUnitMEC _MECType) {
        super(_frame, _title, true);
        //frame = _frame;
        artifact = _artifact;
		operation = _operation;
		MECType = _MECType;
        
        initComponents();
        pack();
    }

    private void initComponents() {

    	Container c = getContentPane();
    	GridBagLayout gridbag0 = new GridBagLayout();
    //	GridBagLayout gridbag1 = new GridBagLayout();
    	GridBagLayout gridbag2 = new GridBagLayout();
    	GridBagLayout gridbag3 = new GridBagLayout();
    	GridBagConstraints c0 = new GridBagConstraints();
    	GridBagConstraints c1 = new GridBagConstraints();
    	//GridBagConstraints c2 = new GridBagConstraints();
    	GridBagConstraints c3 = new GridBagConstraints();

    	setFont(new Font("Helvetica", Font.PLAIN, 14));
    	c.setLayout(gridbag0);

    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


    	panel2 = new JPanel();
    	panel2.setLayout(gridbag2);
    	panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
    	panel2.setPreferredSize(new Dimension(350, 250));

    	// Issue #41 Ordering of tabbed panes 
    	tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

		c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	c1.gridheight = 1;
    	c1.weighty = 1.0;
    	c1.weightx = 1.0;
    	c1.fill = GridBagConstraints.HORIZONTAL;
    	panel2.add(new JLabel("Task:"), c1);
    	//c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllNonMappedTMLTaskNames((TMLArchiDiagramPanel)(artifact.getTDiagramPanel()), artifact.getReferenceTaskName(), artifact.getTaskName());
    	int index = 0;
    	if (list.size() == 0) {
    		list.add("No more task to map");
    		emptyList = true;
    	} else {
    		index = indexOf(list, artifact.getValue());
    	}
    	referenceTaskName = new JComboBox<String>(list);
    	referenceTaskName.setSelectedIndex(index);
    	//referenceTaskName.setEditable(true);
    	//referenceTaskName.setFont(new Font("times", Font.PLAIN, 12));
    	panel2.add(referenceTaskName, c1);

    	list = new Vector<String>();
    	for(int i=0; i<11; i++) {
    		list.add(""+i);
    	}
    	c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	panel2.add(new JLabel("Priority:"), c1);
    	//c1.gridwidth = GridBagConstraints.REMAINDER; //end row
    	priority = new JComboBox<String>(list);
    	priority.setSelectedIndex(artifact.getPriority());
    	panel2.add(priority, c1);

    	panel3 = new JPanel();
    	panel3.setLayout(gridbag3);
    	panel3.setBorder(new javax.swing.border.TitledBorder("Code generation"));
    	panel3.setPreferredSize(new Dimension(350, 250));
    	c3.gridwidth = 1;
    	c3.gridheight = 1;
    	c3.weighty = 1.0;
    	c3.weightx = 1.0;
    	c3.fill = GridBagConstraints.HORIZONTAL;
    	c3.gridwidth = GridBagConstraints.REMAINDER; //end row
    	panel3.add(new JLabel("<html>Task Extension<br>Construct:</html>"), c3);
    	c3.gridwidth = GridBagConstraints.REMAINDER; //end row
    	Vector<String> operationsListS = new Vector<>();
    	int indexOp = 0;
    	TraceManager.addDev( "Inside JDialogTMLTaskArtifact: " + MECType );
    	if( MECType instanceof FepMEC )	{
    		operationsListS = FepMEC.OPERATIONS_LIST;
    		indexOp = operationsListS.indexOf( operation );
    	}
    	else if( MECType instanceof MapperMEC )	{
    		operationsListS.add( MapperMEC.OPERATION );
    		indexOp = operationsListS.indexOf( operation );
    	}
    	else if( MECType instanceof InterleaverMEC )	{
    		operationsListS.add( InterleaverMEC.OPERATION );
    		indexOp = operationsListS.indexOf( operation );
    	}
    	else if( MECType instanceof AdaifMEC )	{
    		operationsListS.add( AdaifMEC.OPERATION );
    		indexOp = operationsListS.indexOf( operation );
    	}
    	else if( MECType instanceof CpuMEC )	{
    		String tmp = (String)(referenceTaskName.getSelectedItem());
    		operationsListS.add( tmp.split("::")[1] );
    		indexOp = operationsListS.indexOf( operation );
    	}
    	else	{
    		operationsListS.add("No MEC selected");
    	}
    	operationsListCB = new JComboBox<String>( operationsListS );
    	if( operation.equals( "VOID" ) || operation.equals( "" ) )	{
    		operationsListCB.setSelectedIndex( 0 );
    	}
    	else	{
    		if( indexOp == -1 )	{ indexOp = 0; }
    		operationsListCB.setSelectedIndex( indexOp  );
    	}
    	panel3.add( operationsListCB, c3 );


    	// main panel;
    	c0.gridheight = 10;
    	c0.weighty = 1.0;
    	c0.weightx = 1.0;
    	c0.gridwidth = GridBagConstraints.REMAINDER; //end row
    	c0.fill = GridBagConstraints.BOTH;
    	tabbedPane.addTab( "Simulation", panel2 );
    	tabbedPane.addTab( "Code generation", panel3 );
    	tabbedPane.setSelectedIndex(0);
    	/*c.add(panel2, c0);
	    c.add(panel3, c0);*/
    	c.add( tabbedPane, c0 );

    	c0.gridwidth = 1;
    	c0.gridheight = 1;
    	c0.fill = GridBagConstraints.HORIZONTAL;
    	
    	initButtons(c0, c, this);
    }
    
    @Override
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
		operation = operationsListCB.getItemAt( operationsListCB.getSelectedIndex() );
        dispose();
    }
    
    public void cancelDialog() {
        dispose();
    }
    
    public boolean isRegularClose() {
        return regularClose;
    }
	
	public String getReferenceTaskName() {
		if (emptyList) {
			return null;
		}
		String tmp = (String)(referenceTaskName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        return tmp.substring(0, index);
    }
    
    public String getTaskName() {
        String tmp = (String)(referenceTaskName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        return tmp.substring(index+2, tmp.length());
    }
	
	public int getPriority() {
		return priority.getSelectedIndex();
	}
	
	public String getOperation() {
		return operation;
	}

	public int indexOf(Vector<String> _list, String name) {
		int i = 0;
		for(String s : _list) {
			if (s.equals(name)) {
				return i;
			}
			i++;
		}
		return 0;
	}
    
}

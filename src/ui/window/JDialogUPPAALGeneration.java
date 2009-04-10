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
* Class JDialogUPPAALGeneration
* Dialog for managing the generation of UPPAAL code
* Creation: 11/05/2007
* @version 1.0 11/05/2007
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import myutil.*;
import translator.tojava.*;
import ui.*;


public class JDialogUPPAALGeneration extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {
    
    protected MainGUI mgui;
    
    private String textJava1 = "Generate UPPAAL code in";
    
    
    protected static String pathCode;
	protected static String nbProcesses = "10";
	protected static String sizeInfiniteFIFO = "1024";
	protected static boolean debugGen = false;
	protected static boolean choicesDeterministicStatic = false;
	
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    int mode;
    
    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    protected JLabel genJava;
    protected JTextField nbOfProcesses;
	protected JTextField sizeOfInfiniteFIFO;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox debugmode, choicesDeterministic;
    
    private Thread t;
    private boolean go = false;
    private ProcessThread pt;
    private boolean hasError = false;
    
    
    /** Creates new form  */
    public JDialogUPPAALGeneration(Frame f, MainGUI _mgui, String title, String _pathCode) {
        super(f, title, true);
        
        mgui = _mgui;
        
        if (pathCode == null) {
            pathCode = _pathCode;
        }
        
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    
    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
		
		if (mgui.getCurrentTURTLEPanel() instanceof TMLDesignPanel) {
			nbOfProcesses.setEnabled(false);
		} else {
			sizeOfInfiniteFIFO.setEnabled(false);
		}
    }
    
    protected void initComponents() {
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        jp1 = new JTabbedPane();
        
        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Code generation"));
        
        
        // first line panel01
        //c1.gridwidth = 3;
        
        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;
		
		c01.gridwidth = 1;
        
        jp01.add(new JLabel("Nb of processes = "), c01);
		
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        
        nbOfProcesses = new JTextField(nbProcesses, 10);
        jp01.add(nbOfProcesses, c01);
		
		c01.gridwidth = 1;
		jp01.add(new JLabel("Size of infinite FIFO = "), c01);
		
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		
		sizeOfInfiniteFIFO = new JTextField(sizeInfiniteFIFO, 10);        
		jp01.add(sizeOfInfiniteFIFO, c01);
        
        debugmode = new JCheckBox("Print debug information");
        debugmode.setSelected(debugGen);
        jp01.add(debugmode, c01);
		
		choicesDeterministic = new JCheckBox("Assume all choices as deterministic");
        choicesDeterministic.setSelected(choicesDeterministicStatic);
        jp01.add(choicesDeterministic, c01);
        
        jp01.add(new JLabel(" "), c01);
        jp1.add("Generate code", jp01);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch UPPAAL code generation \n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        c.add(jsp, BorderLayout.CENTER);
        
        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);
        
        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(100, 30));
        
        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        
        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        
        c.add(jp2, BorderLayout.SOUTH);
        
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //System.out.println("Actions");
        
        // Compare the action command to the known actions.
        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
		nbProcesses = nbOfProcesses.getText();
		sizeInfiniteFIFO = sizeOfInfiniteFIFO.getText();
		debugGen = debugmode.isSelected();
		choicesDeterministicStatic = choicesDeterministic.isSelected();
    }
    
    public void stopProcess() {
        go = false;
        if (pt != null) {
            pt.stopProcess();
        }
        mode = 	STOPPED;
        setButtons();
    }
    
    public void startProcess() {
        t = new Thread(this);
        mode = STARTED;
        setButtons();
        go = true;
        t.start();
    }
    
    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }
    
    public void run() {
        String cmd;
        String list;
		
		boolean debug, choices;
		int nb = 0;
		int nb1;
		
		int size=0, size1;
		
		
		// Code generation
		if (jp1.getSelectedIndex() == 0) {
			jta.append("Generating UPPAAL code\n");
			
			//Manage debug
			debug = debugmode.isSelected();
			choices = choicesDeterministic.isSelected();
			
			// Manage nb of processes
			try {
				nb = Integer.decode(nbOfProcesses.getText()).intValue();
			} catch (Exception e) {
				jta.append("Non valid number of processes");
				jta.append("Nb of processes is assumed to be: " + 10);
				nb = 10;
			}
			
			nb1 = Math.max(nb, 1);
			
			if (nb1 != nb) {
				jta.append("Nb of processes is assumed to be: " + nb1);
			}
			
			// Manage size of infinite FIFO
			try {
				size = Integer.decode(sizeOfInfiniteFIFO.getText()).intValue();
			} catch (Exception e) {
				jta.append("Non valid size for infinite FIFO");
				jta.append("Size is assumed to be: " + 1024);
				size = 1024;
			}
			
			size1 = Math.max(size, 1);
			
			if (size1 != size) {
				jta.append("Size of infinite FIFO is assumed to be: " + size1);
			}
			
			TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
			boolean result;
			if (tp instanceof TMLDesignPanel) {
				result = mgui.gtm.generateUPPAALFromTML(pathCode, debug, size1, choices);
			} else {
				result = mgui.gtm.generateUPPAALFromTIF(pathCode, debug, nb1, choices);
				jta.append("UPPAAL specification generated\n");
				jta.append("Checking the regularity of the TIF specification\n");
				System.out.println("Regularity?");
				boolean b = mgui.gtm.isRegularTM();
				if (b) {
					jta.append("UPPAAL code was optimized since the TIF specification is regular\n");
				} else {
					jta.append("UPPAAL code was NOT optimized since the TIF specification is NOT regular\n");
				}
				System.out.println("Regularity done");
			}
			if (result) {
				jta.append("UPPAAL code generated in " + pathCode);
			} else {
				jta.append("Error during UPPAAL code generation");
			}
		}
		
		
		jta.append("\n\nReady to process next command\n");
		
		checkMode();
		setButtons();
	}
	
	protected void checkMode() {
		mode = NOT_STARTED;
	}
	
	protected void setButtons() {
		switch(mode) {
		case NOT_STARTED:
			start.setEnabled(true);
			stop.setEnabled(false);
			close.setEnabled(true);
			//setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getGlassPane().setVisible(false);
			break;
		case STARTED:
			start.setEnabled(false);
			stop.setEnabled(true);
			close.setEnabled(false);
			getGlassPane().setVisible(true);
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			break;
		case STOPPED:
		default:
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		}
	}
	
	public boolean hasToContinue() {
		return (go == true);
	}
	
	public void appendOut(String s) {
		jta.append(s);
	}
	
	public void setError() {
		hasError = true;
	}
}

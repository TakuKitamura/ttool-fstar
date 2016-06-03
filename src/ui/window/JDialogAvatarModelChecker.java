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
 * Class JDialogAvatarModelChecker
 * Dialog for managing the model checking of avatar specifications
 * Creation: 1/06/2016
 * @version 1.1 1/06/2016
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import myutil.*;
import avatartranslator.*;
import avatartranslator.modelchecker.*;
import ui.*;



public class JDialogAvatarModelChecker extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface  {

    protected static String graphDir;
    protected static boolean graphSelected = false;
    
    protected MainGUI mgui;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    public final static int REACHABILITY_ALL        = 1;
    public final static int REACHABILITY_SELECTED   = 2;
    public final static int REACHABILITY_NONE       = 3;

    private int mode;

    private AvatarSpecification spec;

    private avatartranslator.modelchecker.AvatarModelChecker amc;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    //protected JRadioButton exe, exeint;
    //protected ButtonGroup exegroup;
    //protected JLabel gen, comp;
    //protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, loopLimit;

    protected JCheckBox saveGraphAUT;
    protected JTextField graphPath;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;


    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    //protected boolean startProcess = false;

 


    /** Creates new form  */
    public JDialogAvatarModelChecker(Frame f, MainGUI _mgui, String title, AvatarSpecification _spec, String _graphDir)  {
        super(f, title, true);

        mgui = _mgui;
	spec = _spec;

	if (graphDir == null) {
	    graphDir = _graphDir + File.separator + "avatar.aut";
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
        jp01.setBorder(new javax.swing.border.TitledBorder("Options"));

	c01.gridwidth = 1;
	c01.gridheight = 1;
	c01.weighty = 1.0;
	c01.weightx = 1.0;
	c01.fill = GridBagConstraints.HORIZONTAL;
	c01.gridwidth = GridBagConstraints.REMAINDER; //end row
	saveGraphAUT = new JCheckBox("Save RG (AUT format) in:", graphSelected);
	saveGraphAUT.addActionListener(this);
	jp01.add(saveGraphAUT, c01);
	graphPath = new JTextField(graphDir);
	jp01.add(graphPath, c01);
	c.add(jp01, BorderLayout.NORTH);
 


        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to start the model checker\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);
        close = new JButton("Close", IconManager.imgic27);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();

        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (evt.getSource() == saveGraphAUT) {
	    setButtons();
	}
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    public void stopProcess() {
	if (amc != null) {
	    amc.stopModelChecking();
	}
        mode =  STOPPED;
        setButtons();
        go = false;
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
	    TraceManager.addDev("Interrupted by user");
            throw new InterruptedException("Stopped by user");
        }
    }

    public void run() {
        String cmd;
        String list, data;
        int cycle = 0;

        hasError = false;

        TraceManager.addDev("Thread started");
        File testFile;
        try {  
	    jta.append("Starting the model checker\n");
	    amc = new AvatarModelChecker(spec);
	    
	    
	    testGo();
	    amc.startModelChecking();
            
	    jta.append("Nb of states:" + amc.getNbOfStates() + "\n");
	    jta.append("Nb of links:" + amc.getNbOfLinks() + "\n");
	    //TraceManager.addDev(amc.toString());
	    TraceManager.addDev(amc.toString());
	    if (saveGraphAUT.isSelected()) {
		try {
		    String graph = amc.toAUT();
		    //TraceManager.addDev("graph AUT=\n" + graph);
		    FileUtils.saveFile(graphPath.getText(), graph);
		    jta.append("Graph saved in " + graphPath.getText());
		} catch (Exception e) {
		    jta.append("Graph could not be saved in " + graphPath.getText());
		}
	    }
	    

        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        jta.append("\n\nReady to process next command\n");

        checkMode();
        setButtons();

        //System.out.println("Selected item=" + selectedItem);
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
	graphSelected = saveGraphAUT.isSelected();
	graphPath.setEnabled(saveGraphAUT.isSelected());
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

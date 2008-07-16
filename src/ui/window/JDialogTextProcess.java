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
 * Class JDialogTextProcess
 * Dialog for managing remote processes call
 * Creation: 16/12/2003
 * @version 1.0 16/12/2003
 * @author Ludovic APVRILLE
 * @see 
 */
 
package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import launcher.*;
import myutil.*;
import ui.*;


public class JDialogTextProcess extends javax.swing.JDialog implements ActionListener, Runnable  {
	
	protected String cmd;
	protected String fileName;
	protected String spec;
	protected String host;
	protected int mode;
	protected RshClient rshc;
	protected Thread t;
	
	protected final static int NOT_STARTED = 0;
	protected final static int STARTED = 1;
	protected final static int STOPPED = 2;
	
	//components
	protected JTextArea jta; 
	protected JButton start;
	protected JButton stop;
	protected JButton close;
	
	
    /** Creates new form  */
    public JDialogTextProcess(Frame f, String title, String _cmd, String _fileName, String _spec, String _host) {
	    super(f, title, true);
	    
	    cmd = _cmd;
	    fileName = _fileName;
	    spec = _spec;
	    host = _host;
	    
        initComponents ();
        myInitComponents();
        pack();
    }
    
    
    protected void myInitComponents() {
	    mode = NOT_STARTED;
	    setButtons();
    }  
   
    protected void initComponents () {
	    
	    Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    jta = new JTextArea();
		jta.setEditable(false);
		jta.setMargin(new Insets(10, 10, 10, 10));
		jta.setTabSize(3);
		jta.append("Click on 'start' to launch process\n");
		Font f = new Font("Courrier", Font.BOLD, 12); 
		jta.setFont(f);
		JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
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
		
		JPanel jp = new JPanel();
		jp.add(start);
		jp.add(stop);
		jp.add(close);
		
		c.add(jp, BorderLayout.SOUTH);
	    
    }
 
    public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
	
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
		dispose();
	}
	
	public void stopProcess() {
                try {
                    rshc.stopFillJTA();
                } catch (LauncherException le) {
                    
                }
		rshc = null;
		mode = 	STOPPED;
		setButtons();
	}
	
	public void startProcess() {
		t = new Thread(this);
		mode = STARTED;
		setButtons();
		t.start();
	}
	
	public void run() {
		
		rshc = new RshClient(cmd, host);
		RshClient rshctmp = rshc;
		int id = 0;

		try {
          id = rshc.getId();
          fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);
          jta.append("Sending file data\n");
			rshc.sendFileData(fileName, spec);
			jta.append("Sending process request\n");
			rshc.setCmd(Conversion.replaceAllString(cmd, "__FILENAME", fileName));
			rshc.sendProcessRequest();

		} catch (LauncherException le) {
			jta.append(le.getMessage() + "\n");
			mode = 	STOPPED;
			setButtons();
			return;
		} catch (Exception e) {
			mode = 	STOPPED;
			setButtons();
			return;
		}
			
		try {
			jta.append("\nRTL Process:\n------------------\n");
			rshc.fillJTA(jta);

			rshc.deleteFile(fileName);
			rshc.deleteFile(fileName+".sim");
			rshc.freeId(id);

		} catch (LauncherException le) {
			jta.append(le.getMessage()+ "\n");
			mode = 	STOPPED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
			return;
		} catch (Exception e) {
			mode = 	STOPPED;
			setButtons();
			try{
				if (rshctmp != null) {
					rshctmp.freeId(id);
				}
			} catch (LauncherException leb) {}
			return;
		}
		
		mode = STOPPED;
		setButtons();
		jta.append("\n------------------\nRTL process stopped\n");
	}
	
	protected void setButtons() {
		switch(mode) {
			case NOT_STARTED:
				start.setEnabled(true);
				stop.setEnabled(false);
				close.setEnabled(true);
				break;
			case STARTED:
				start.setEnabled(false);
				stop.setEnabled(true);
				close.setEnabled(true);
				break;
			case STOPPED:
			default:
				start.setEnabled(false);
				stop.setEnabled(false);
				close.setEnabled(true);
				break;
		}	
	}	
}

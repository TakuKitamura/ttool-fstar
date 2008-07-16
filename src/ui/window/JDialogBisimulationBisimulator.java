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
* Class JDialogBisimulationBisimulator
* Dialog for managing remote bisimulations with BISIMULATOR (CADP)
* Creation: 28/02/2008
* @version 1.0 28/02/2008
* @author Ludovic APVRILLE
* @see
*/

package ui.window;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import launcher.*;
import myutil.*;
import ui.*;
import ui.file.*;


public class JDialogBisimulationBisimulator extends javax.swing.JDialog implements ActionListener, Runnable  {
    
    
    private String [] textAlgo = { "Branching equivalence", 
		"Observational equivalence", "Safety equivalence",
		"Strong equivalence", "tau* equivalence",
	"Trace equivalence", "Weaktrace equivalence"};
    
    private String [] cmdAlgo = {"-branching", "-observational", "-safety", "-strong", "-taustar", "-trace", "-weaktrace"};
    
	private String [] textRelation = { "LTS1 smaller than LTS2", "LTS1 equal to LTS2",
	"LTS1 greater than LTS2"};
    
    private String [] cmdRelation = {"-smaller", "-equal", "-greater"};
    
	
    private String cadpHost;
    private String bisimulatorPath;
	private String bcgioPath;
    protected String remoteFile1;
    protected String remoteFile2;
    
    protected int mode;
    protected RshClient rshc;
    protected Thread t;
    
    protected final static int NO_ALGO = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    //components
    protected JTextField file1, file2;
    protected JButton openFile1, openFile2;
    
    protected int nbAlgo = 7;
    protected int lastAlgoSelected = 0;
	protected boolean algoSelected = false;
    protected JRadioButton [] algoButton = new JRadioButton[nbAlgo];
	
	protected int nbRelation = 3;
    protected int lastRelationSelected = 0;
	protected boolean relationSelected = false;
    protected JRadioButton [] relationButton = new JRadioButton[nbRelation];
	
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    /** Creates new form  */
    public JDialogBisimulationBisimulator(Frame f, String _cadpHost, String _bisimulatorPath,  String _bcgioPath, String _remoteFile1, String _remoteFile2, String title) {
        super(f, title, true);
        
        cadpHost = _cadpHost;
        bisimulatorPath = _bisimulatorPath;
		bcgioPath = _bcgioPath;
        
        remoteFile1 = _remoteFile1;
        remoteFile2 = _remoteFile2;
        
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    
    protected void myInitComponents() {
        mode = NO_ALGO;
        setButtons();
    }
    
    protected void initComponents() {
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        
        //File section
        JPanel jp1 = new JPanel();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        jp1.setLayout(gridbag1);
        jp1.setBorder(new javax.swing.border.TitledBorder("LTS"));
        
        c1.weighty = 1.0;
        c1.weightx = 0.0;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.BOTH;
        jp1.add(new JLabel("LTS #1:"), c1);
        
        c1.weightx = 5.0;
        c1.gridwidth = 10;
        file1 = new JTextField(200);
        file1.setEditable(true);
        jp1.add(file1, c1);
        
        c1.weightx = 0.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        //c1.gridwidth = 1;
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        openFile1 = new JButton(IconManager.imgic22);
        openFile1.addActionListener(this);
        jp1.add(openFile1, c1);
        //c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        //jp1.add(new JLabel(""), c1);
        
        c1.weightx = 0.0;
        c1.gridwidth = 1;
        jp1.add(new JLabel("LTS #2:"), c1);
        
        c1.weightx = 5.0;
        c1.gridwidth = 10;
        file2 = new JTextField(200);
        file2.setEditable(true);
        jp1.add(file2, c1);
        
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.weightx = 0.0;
        openFile2 = new JButton(IconManager.imgic22);
        openFile2.addActionListener(this);
        jp1.add(openFile2, c1);
        
        panelTop.add(jp1, BorderLayout.NORTH);
        
        // Algorithm selection
        JPanel jp2 = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        jp2.setLayout(gridbag2);
        jp2.setBorder(new javax.swing.border.TitledBorder("Bisimulation algorithm"));
        
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridheight = 1;
        ButtonGroup bg = new ButtonGroup();
        for(int i=0; i<nbAlgo; i++) {
            algoButton[i] = new JRadioButton(textAlgo[i]);
            algoButton[i].addActionListener(this);
            bg.add(algoButton[i]);
            jp2.add(algoButton[i], c2);
        }
        panelTop.add(jp2, BorderLayout.CENTER);
		
		// Relation selection
		JPanel jp3 = new JPanel();
        GridBagLayout gridbag3 = new GridBagLayout();
        GridBagConstraints c3 = new GridBagConstraints();
        jp3.setLayout(gridbag3);
        jp3.setBorder(new javax.swing.border.TitledBorder("Bisimulation relation"));
        
        c3.weighty = 1.0;
        c3.weightx = 1.0;
        c3.gridwidth = GridBagConstraints.REMAINDER; //end row
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.gridheight = 1;
        bg = new ButtonGroup();
        for(int i=0; i<nbRelation; i++) {
            relationButton[i] = new JRadioButton(textRelation[i]);
            relationButton[i].addActionListener(this);
            bg.add(relationButton[i]);
            jp3.add(relationButton[i], c3);
        }
		panelTop.add(jp3, BorderLayout.SOUTH);
		
        
        c.add(panelTop, BorderLayout.NORTH);
        
        // Text area
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select files, algorithms, relations and then, click on 'start' to launch bisimulation\n");
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
        
        JPanel jp4 = new JPanel();
        jp4.add(start);
        jp4.add(stop);
        jp4.add(close);
        
        c.add(jp4, BorderLayout.SOUTH);
    }
    
    public void	actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();
        
        // Compare the action command to the known actions.
        if (evt.getSource() == openFile1) {
            selectGraph1();
        } else if (evt.getSource() == openFile2) {
            selectGraph2();
        } else if (evt.getSource() == start) {
            startProcess();
        } else if (evt.getSource() == stop) {
            stopProcess();
        } else if (evt.getSource() == close) {
            closeDialog();
        } else {
			int i;
            for(i=0; i<nbAlgo; i++) {
                if (evt.getSource() == algoButton[i]) {
                    lastAlgoSelected = i;
					algoSelected = true;
					break;
                }
            }
			for(i=0; i<nbRelation; i++) {
                if (evt.getSource() == relationButton[i]) {
                    lastRelationSelected = i;
					relationSelected = true;
					break;
                }
            }
			if (algoSelected && relationSelected) {
				mode = NOT_STARTED;
				setButtons();
			}
        }
    }
    
    public void selectGraph1() {
        String path = selectFile();
        if (path != null) {
            file1.setText(path);
        }
    }
    
    public void selectGraph2() {
        String path = selectFile();
        if (path != null) {
            file2.setText(path);
        }
    }
    
    private String selectFile() {
        JFileChooser jfc;
        if (ConfigurationTTool.TGraphPath.length() > 0) {
            jfc = new JFileChooser(ConfigurationTTool.TGraphPath);
        } else {
            jfc = new JFileChooser();
        }
        
		BCGFileFilter filter1 = new BCGFileFilter();
        AUTFileFilter filter = new AUTFileFilter();
        jfc.setFileFilter(filter1);
		jfc.addChoosableFileFilter(filter);
        
        int returnVal = jfc.showDialog(this, "Select graph");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        
        return jfc.getSelectedFile().getAbsolutePath();
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }
    
    public void stopProcess() {
        if (rshc != null) {
            try {
                rshc.stopFillJTA();
            } catch (LauncherException le) {
                
            }
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
        String data1, data2;
        String result;
        String cmd;
        int id = 0;
		String remote1, remote2;
		String data, cmd1;
		String tmp1 = remoteFile1, tmp2=remoteFile2;
        
        rshc = new RshClient(cadpHost);
		RshClient rshctmp = rshc;
        //Point p;
        
        try {
            jta.append("Reading input data\n");
            id = rshc.getId();
			if (file1.getText().length() == 0) {
				jta.append("Error: LTS1 not selected ...\n");
			} else {
				if (file1.getText().length() == 0) {
					jta.append("Error: LTS2 not selected ...\n");
				} else {
					data1 = FileUtils.loadFileData(new File(file1.getText()));
					data2 = FileUtils.loadFileData(new File(file2.getText()));
					
					if ((data1 ==null) ||(data2 == null)) {
						jta.append("\nBad files specified\n");
						mode = NOT_STARTED;
						setButtons();
					} else {
						jta.append("Sending input data\n");
						
						// Need to convert the file to bcg?
						remote1 = remoteFile1 + ".bcg";
						remote1 = FileUtils.addBeforeFileExtension(remoteFile1, "_" + id);
						jta.append("Sending LTS1 data\n");
						if (file1.getText().endsWith(".aut")) {
							remoteFile1 = remoteFile1 + ".aut";
							remoteFile1 = FileUtils.addBeforeFileExtension(remoteFile1, "_" + id);
							rshc.sendFileData(remoteFile1, data1);
							jta.append("Converting LTS1 to BCG format\n");
							cmd1 = bcgioPath + " -aldebaran " + remoteFile1 + " -bcg " + remote1;
							data = processCmd(cmd1);
							rshc.deleteFile(remoteFile1);
						} else {
							rshc.sendFileData(remote1, data1);
						}
						
						jta.append("Sending LTS2 data\n");
						remote2 = remoteFile2 + ".bcg";
						remote2 = FileUtils.addBeforeFileExtension(remoteFile2, "_" + id);
						if (file2.getText().endsWith(".aut")) {
							remoteFile2 = remoteFile2 + ".aut";
							remoteFile2 = FileUtils.addBeforeFileExtension(remoteFile2, "_" + id);
							rshc.sendFileData(remoteFile2, data2);
							jta.append("Converting LTS2 to BCG format\n");
							cmd1 = bcgioPath + " -aldebaran " + remoteFile2 + " -bcg " + remote2;
							data = processCmd(cmd1);
							rshc.deleteFile(remoteFile2);
						} else {
							rshc.sendFileData(remote2, data2);
						}
						
						jta.append("Starting bisimulation\n");
						cmd = bisimulatorPath + " " + remote1 + " bisimulator " + cmdAlgo[lastAlgoSelected] + " " + cmdRelation[lastRelationSelected] +  " " + remote2;
						result = processCmd(cmd);
						rshc.deleteFile(remote1);
						rshc.deleteFile(remote2);
						rshc.freeId(id);
						
						jta.append("Bisimulation done:\n");
						jta.append(result);
					}
				}
			}
		} catch (LauncherException le) {
			jta.append("Error: " + le.getMessage() + "\n");
			mode = NOT_STARTED;
			setButtons();
			try{
				rshctmp.freeId(id);
            } catch (LauncherException leb) {}
            return;
		} catch (Exception e) {
			mode = NOT_STARTED;
			setButtons();
			try{
				rshctmp.freeId(id);
            } catch (LauncherException leb) {}
            return;
		}
        
        mode = NOT_STARTED;
        setButtons();
        jta.append("\n\nFor a new bisimulation, select new files / algorithms and press start\n");
		remoteFile1 = tmp1;
		remoteFile2 = tmp2;
    }
    
    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected void setButtons() {
        int i;
        switch(mode) {
		case NO_ALGO:
			openFile1.setEnabled(true);
			openFile2.setEnabled(true);
			file1.setEnabled(true);
			file2.setEnabled(true);
			for (i=0; i<nbAlgo; i++) {
				algoButton[i].setEnabled(true);
			}
			for (i=0; i<nbRelation; i++) {
				relationButton[i].setEnabled(true);
			}
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		case NOT_STARTED:
			openFile1.setEnabled(true);
			openFile2.setEnabled(true);
			file1.setEnabled(true);
			file2.setEnabled(true);
			for (i=0; i<nbAlgo; i++) {
				algoButton[i].setEnabled(true);
			}
			for (i=0; i<nbRelation; i++) {
				relationButton[i].setEnabled(true);
			}
			start.setEnabled(true);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
		case STARTED:
			openFile1.setEnabled(false);
			openFile2.setEnabled(false);
			file1.setEnabled(false);
			file2.setEnabled(false);
			for (i=0; i<nbAlgo; i++) {
				algoButton[i].setEnabled(false);
			}
			for (i=0; i<nbRelation; i++) {
				relationButton[i].setEnabled(false);
			}
			start.setEnabled(false);
			stop.setEnabled(true);
			close.setEnabled(false);
			getGlassPane().setVisible(true);
			break;
		case STOPPED:
		default:
			openFile1.setEnabled(false);
			openFile2.setEnabled(false);
			file1.setEnabled(false);
			file2.setEnabled(false);
			for (i=0; i<nbAlgo; i++) {
				algoButton[i].setEnabled(false);
			}
			for (i=0; i<nbRelation; i++) {
				relationButton[i].setEnabled(false);
			}
			start.setEnabled(false);
			stop.setEnabled(false);
			close.setEnabled(true);
			getGlassPane().setVisible(false);
			break;
        }
    }
}

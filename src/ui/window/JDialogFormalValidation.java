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
 * Class JDialogFormalValidation
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

public class JDialogFormalValidation extends javax.swing.JDialog implements ActionListener, Runnable  {
    private static boolean makeDTAChecked, makeRGChecked, makeRGAutChecked, makeTLSAChecked = false;
	private static boolean fromDTASelected, onTheFlySelected, autFromDTASelected, autOnTheFlySelected = false;
	
    protected MainGUI mgui;
    
    private String textRG1 = "Make Reachability Graph (default format)";
    private String textRG2 = "Make Reachability Graph (AUT format)";
    
    protected String cmdRTL;
    protected String cmdDTA2DOT;
    protected String cmdRGSTRAP;
    protected String cmdRG2TLSA;
    protected String fileName;
    protected String spec;
    protected String host;
    protected String hostAldebaran;
    protected String bcgioPath;
    protected int mode;
    protected RshClient rshc;
    protected Thread t;
    
    protected final static int NO_OPTIONS = 0;
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
    protected JCheckBox makeDTA, makeRG, makeRGAut, makeTLSA;
    protected JRadioButton fromDTA, onTheFly;
    protected JRadioButton autFromDTA, autOnTheFly;
    
    /** Creates new form  */
    public JDialogFormalValidation(Frame f, MainGUI _mgui, String title, String _cmdRTL, String _cmdDTA2DOT, String _cmdRGSTRAP, String _cmdRG2TLSA, String _fileName, String _spec, String _host, String _aldebaranHost, String _bcgioPath) {
        super(f, title, true);
        
        mgui = _mgui;
        
        cmdRTL = _cmdRTL;
        cmdDTA2DOT = _cmdDTA2DOT;
        cmdRGSTRAP = _cmdRGSTRAP;
        cmdRG2TLSA = _cmdRG2TLSA;
        fileName = _fileName;
        spec = _spec;
        host = _host;
        
        hostAldebaran = _aldebaranHost;
        bcgioPath = _bcgioPath;
        
        initComponents();
        myInitComponents();
        pack();
        
        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    
    protected void myInitComponents() {
        mode = NO_OPTIONS;
        setButtons();
		makeDTA();
		makeRG();
		makeRGAut();
    }
    
    protected void initComponents() {
        
        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel jp1 = new JPanel();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        
        jp1.setLayout(gridbag1);
        jp1.setBorder(new javax.swing.border.TitledBorder("Validation options"));
        //jp1.setPreferredSize(new Dimension(300, 150));
        
        // first line panel1
        //c1.gridwidth = 3;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        c1.fill = GridBagConstraints.BOTH;
        c1.gridheight = 1;
        
        makeDTA = new JCheckBox("Make Dynamic Timed Automaton");
        makeDTA.addActionListener(this);
		makeDTA.setSelected(makeDTAChecked);
        
        makeRG = new JCheckBox(textRG1);
        makeRG.addActionListener(this);
		makeRG.setSelected(makeRGChecked);
        
        fromDTA = new JRadioButton("from DTA");
        fromDTA.addActionListener(this);
        fromDTA.setEnabled(false);
		fromDTA.setSelected(fromDTASelected);
        onTheFly = new JRadioButton("on the fly");
        onTheFly.addActionListener(this);
        onTheFly.setEnabled(false);
		onTheFly.setSelected(onTheFlySelected);
		
		
        jp1.add(makeDTA, c1);
        jp1.add(makeRG, c1);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(fromDTA);
        bg.add(onTheFly);
        
        makeTLSA = new JCheckBox("generate TLSA");
        makeTLSA.addActionListener(this);
        makeTLSA.setEnabled(false);
		makeTLSA.setSelected(makeTLSAChecked);
        
        c1.gridwidth = 1;
        c1.anchor = GridBagConstraints.EAST;
        jp1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(fromDTA, c1);
        c1.gridwidth = 1;
        jp1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(onTheFly, c1);
        c1.gridwidth = 1;
        jp1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(makeTLSA, c1);
        
        makeRGAut = new JCheckBox(textRG2);
        makeRGAut.addActionListener(this);
		makeRGAut.setSelected(makeRGAutChecked);
        jp1.add(makeRGAut, c1);
        
        autFromDTA = new JRadioButton("from DTA");
        autFromDTA.addActionListener(this);
        autFromDTA.setEnabled(false);
		autFromDTA.setSelected(autFromDTASelected);
        autOnTheFly = new JRadioButton("on the fly");
        autOnTheFly.addActionListener(this);
        autOnTheFly.setEnabled(false);
		autOnTheFly.setSelected(autOnTheFlySelected);
        
        bg = new ButtonGroup();
        bg.add(autFromDTA);
        bg.add(autOnTheFly);
        
        c1.gridwidth = 1;
        c1.anchor = GridBagConstraints.EAST;
        jp1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(autFromDTA, c1);
        c1.gridwidth = 1;
        jp1.add(new JLabel(" "), c1);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
        jp1.add(autOnTheFly, c1);
        
        c.add(jp1, BorderLayout.NORTH);
        
        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch validation\n");
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
        } else if (command.equals("Make Dynamic Timed Automaton")) {
            makeDTA();
        } else if (command.equals(textRG1)) {
            makeRG();
        } else if (command.equals(textRG2)) {
            makeRGAut();
        } else if (command.equals("from DTA")) {
            fromDTA();
        } else if (command.equals("on the fly")) {
            onTheFly();
        } else if (command.equals("generate TLSA")) {
            generateTLSA();
        }
    }
	
	public void setAutomatic(boolean automatic) {
		if (automatic) {
			if (mode == NO_OPTIONS) {
				makeRGAut.setSelected(true);
				autOnTheFly.setSelected(true);
				makeRGAut();
			}
			startProcess();
		}
	}
    
    public void makeDTA() {
        checkMode();
        setButtons();
    }
    
    
    public void makeRG() {
        checkMode();
        if (makeRG.isSelected()) {
            fromDTA.setEnabled(true);
            onTheFly.setEnabled(true);
            makeTLSA.setEnabled(true);
        } else {
            fromDTA.setEnabled(false);
            onTheFly.setEnabled(false);
            makeTLSA.setEnabled(false);
        }
        setButtons();
    }
    
    public void makeRGAut() {
        checkMode();
        if (makeRGAut.isSelected()) {
            autFromDTA.setEnabled(true);
            autOnTheFly.setEnabled(true);
        } else {
            autFromDTA.setEnabled(false);
            autOnTheFly.setEnabled(false);
        }
        setButtons();
    }
    
    public void fromDTA() {
        checkMode();
        setButtons();
    }
    
    public void onTheFly() {
        checkMode();
        setButtons();
    }
    
    public void generateTLSA() {
        checkMode();
        setButtons();
    }
    
    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
		makeDTAChecked = makeDTA.isSelected();
		makeRGChecked = makeRG.isSelected();
		makeRGAutChecked = makeRGAut.isSelected();
		makeTLSAChecked = makeTLSA.isSelected();
		fromDTASelected = fromDTA.isSelected();
		onTheFlySelected = onTheFly.isSelected();
		autFromDTASelected = autFromDTA.isSelected(); 
		autOnTheFlySelected = autOnTheFly.isSelected();
	
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
        String cmd1, cmd2, cmd3, cmd4;
        String baseFileName;
        String fileDTA ;
        //String fileDTADOT ;
        String fileRG ;
        String fileRGDOT;
        String fileTLSA;
        String fileTLSADOT;
        String data;
        int id=0;
        
        rshc = new RshClient(host);
        RshClient rshctmp =  rshc;
        Point p;
        
        try {
            jta.append("Sending data\n");
            id = rshc.getId();
            fileName = FileUtils.addBeforeFileExtension(fileName, "_" + id);
            jta.append("Session id on launcher="+id + " ; working on " + fileName + "\n");
            
            baseFileName = fileName.substring(0, fileName.length() - 4);
            fileDTA = baseFileName + ".dta";
            //fileDTADOT = fileDTA + ".dot";
            fileRG = baseFileName + ".dta";
            fileRGDOT = fileRG + ".dot";
            fileTLSA = baseFileName + ".tlsa";
            fileTLSADOT = fileTLSA + ".dot";

            rshc.sendFileData(fileName, spec);
            jta.append("Data sent");
            
            if (makeDTA.isSelected()) {
                mgui.gtm.reinitDTA();
                cmd1 = cmdRTL + " -TG1 -p1 " + fileName;
                cmd2 = "cat " + fileDTA;
                cmd3 = cmdDTA2DOT;
                
                //.DTA
                jta.append("\nMaking DTA\n");
                data = processCmd(cmd1);
                
                p = FormatManager.nbStateTransitionDTA(data);
                jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");
                
                jta.append(data);
                mgui.gtm.setDTA(data);
                mgui.saveDTA();
                
                //.DOT
                jta.append("Sending file dta\n");
                
                rshc.sendFileData(fileDTA, data);
                
                //System.out.println("Starting piped processes");
                
                //data = processCmd(cmd2);
                data = processPipedCmd(cmd2, cmd3);
                
                jta.append(data);
                mgui.gtm.setDTADOT(data);
                mgui.saveDTADOT();
                jta.append("\nDTA done\n");
            }
            
            if (makeRG.isSelected() && ((onTheFly.isSelected()) || (fromDTA.isSelected()))) {
                mgui.gtm.reinitRG();
                cmd1 = cmdRTL;
                if (fromDTA.isSelected()) {
                    cmd1 += " -TG3 " + fileName;
                } else {
                    cmd1 += " -TG2 " + fileName;
                }
                cmd2 = cmdRGSTRAP;
                cmd3 = "cat " + fileRG;
                cmd4 = cmdDTA2DOT;
                
                //.RG
                jta.append("\nMaking RG\n");
                data = processPipedCmd(cmd1, cmd2);
                
                p = FormatManager.nbStateTransitionRGDefault(data);
                jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");
                
                jta.append(data);
                mgui.gtm.setRG(data);
                mgui.saveRG();
                
                //.DOT
                jta.append("Sending data RG\n");
                //rshc.deleteFile(fileName);
                rshc.deleteFile(fileRG);
                rshc.deleteFile(fileRGDOT);
                
                rshc.sendFileData(fileRG, data);
                
                //data = processCmd(cmd2);
                data = processPipedCmd(cmd3, cmd4);
                jta.append(data);
                mgui.gtm.setRGDOT(data);
                mgui.saveRGDOT();
                
                if (makeTLSA.isSelected()) {
                    cmd1 = cmdRG2TLSA;
                    cmd2 = "cat " + fileRG;
                    jta.append("\nMaking TLSA\n");
                    data = processPipedCmd(cmd2, cmd1);
                    jta.append(data);
                    mgui.gtm.setTLSA(data);
                    mgui.saveTLSA();
                    
                    rshc.deleteFile(fileTLSA);
                    rshc.deleteFile(fileTLSADOT);
                    
                    rshc.sendFileData(fileTLSA, data);
                    
                    cmd3 = "cat " + fileTLSA;
                    cmd4 = cmdDTA2DOT;
                    data = processPipedCmd(cmd3, cmd4);
                    mgui.gtm.setTLSADOT(data);
                    mgui.saveTLSADOT();
                }
                
                
                jta.append("\nRG done\n");
            }
            
            if (makeRGAut.isSelected() && ((autOnTheFly.isSelected()) || (autFromDTA.isSelected()))) {
                mgui.gtm.reinitRGAUT();
                mgui.gtm.reinitRGAUTPROJDOT();
                //rshc.deleteFile(fileName + ".rg0.aut");
                //rshc.deleteFile(fileName + ".rg0.aut.dot");
                cmd1 = cmdRTL + " -ATG -AUT";
                if (autFromDTA.isSelected()) {
                    cmd1 += " -TG3 " + fileName;
                } else {
                    cmd1 += " -TG2 " + fileName;
                }
                cmd3 = "cat " + fileRG;
                cmd4 = cmdDTA2DOT;
                
                //.RG
                jta.append("\nMaking RG format AUT\n");
                processCmd(cmd1);
                //jta.append(data);
                //mgui.gtm.setRGAut(data);
                //mgui.saveRGAut();
                jta.append("\nRG Done\n");

                //jta.append("\nTemporary files deleted\n");
                
                jta.append("\nGetting data from " + fileName + ".rg0.aut" + "\n");
                // Getting data
                data = rshc.getFileData(fileName + ".rg0.aut");
                //jta.append("\ndata done\n");
                p = FormatManager.nbStateTransitionRGAldebaran(data);
                jta.append("\n" + p.x + " state(s), " + p.y + " transition(s)\n\n");
                jta.append(data);
                mgui.gtm.setRGAut(data);
                mgui.saveRGAut();
                
                // AUT  dot
                jta.append("\nConverting to dotty format\n");
                rshc = new RshClient(hostAldebaran);
                // Sending data > if rtlhost != aldebaranhost
                if (host.compareTo(hostAldebaran) != 0) {
                    // Need to send data generated by rtl
                    rshc.sendFileData(fileName + ".rg0.aut", data);
                    jta.append("\nSending data to aldebaran host\n");
                }

                // Bcgio command
                cmd1 = bcgioPath + " -aldebaran " + fileName + ".rg0.aut" + " -graphviz " + fileName + ".rg0.aut.dot";
                data = processCmd(cmd1);
                data = rshc.getFileData(fileName + ".rg0.aut.dot");
                mgui.gtm.setRGAutDOT(data);
                mgui.saveRGAutDOT();
                
                // removing useless files
                rshc.deleteFile(fileName + ".tg0.aut");
                rshc.deleteFile(fileName + ".tg0.fc2");
                rshc.deleteFile(fileName + ".rg0.fc2");
                rshc.deleteFile(fileName + ".rg0.ren");
                rshc.deleteFile(fileName + ".rg0.aut");
                rshc.deleteFile(fileName + ".rg0.aut.dot");

            }
            rshc.deleteFile(fileName);
            rshc.deleteFile(fileRG);
            rshc.deleteFile(fileRGDOT);
            rshc.deleteFile(fileDTA);
            rshc.deleteFile(fileTLSA);
            rshc.deleteFile(fileTLSADOT);

            rshc.freeId(id);

            jta.append("\nAll Done\n");
            
        } catch (LauncherException le) {
            jta.append("Error: " + le.getMessage() + "\n");
            mode = 	STOPPED;
            setButtons();
            try{
            	rshctmp.freeId(id);
            } catch (LauncherException leb) {}
            return;
        } catch (Exception e) {
            jta.append("Error: " + e.getMessage() + "\n");
            mode = 	STOPPED;
            setButtons();
            try{
                rshctmp.freeId(id);
            } catch (LauncherException leb) {}
            return;
        }
        
        mode = STOPPED;
        setButtons();
    }
    
    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
        rshc.sendProcessRequest();
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected String processPipedCmd(String cmd1, String cmd2) throws LauncherException {
        String s = null;
        rshc.sendProcessRequest(cmd1, cmd2);
        s = rshc.getDataFromProcess();
        return s;
    }
    
    protected void checkMode() {
        if (makeDTA.isSelected()) {
            mode = NOT_STARTED;
            return;
        }
        
        if (makeRG.isSelected() && ((onTheFly.isSelected()) || (fromDTA.isSelected()))) {
            mode = NOT_STARTED;
            return;
        }
        
        if (makeRGAut.isSelected() && ((autOnTheFly.isSelected()) || (autFromDTA.isSelected()))) {
            mode = NOT_STARTED;
            return;
        }
        
        mode = NO_OPTIONS;
    }
    
    protected void setButtons() {
        switch(mode) {
            case NO_OPTIONS:
                makeDTA.setEnabled(true);
                makeRG.setEnabled(true);
                makeRGAut.setEnabled(true);
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case NOT_STARTED:
                makeDTA.setEnabled(true);
                makeRG.setEnabled(true);
                makeRGAut.setEnabled(true);
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                makeDTA.setEnabled(false);
                makeRG.setEnabled(false);
                makeRGAut.setEnabled(false);
                fromDTA.setEnabled(false);
                onTheFly.setEnabled(false);
                makeTLSA.setEnabled(false);
                autFromDTA.setEnabled(false);
                autOnTheFly.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            case STOPPED:
            default:
                makeDTA.setEnabled(false);
                makeRG.setEnabled(false);
                makeRGAut.setEnabled(false);
                fromDTA.setEnabled(false);
                onTheFly.setEnabled(false);
                makeTLSA.setEnabled(false);
                autFromDTA.setEnabled(false);
                autOnTheFly.setEnabled(false);
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
        }
    }
}

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import myutil.*;
import ui.*;


import avatartranslator.*;
import avatartranslator.toexecutable.*;
import launcher.*;

import ui.interactivesimulation.*;

import ddtranslatorSoclib.toSoclib.*;
import ddtranslatorSoclib.toTopCell.*;
import ddtranslatorSoclib.*;
import ui.avatardd.*;
import ui.avatarbd.*;

// -----------------------------------------------------


public class JDialogAvatarddExecutableCodeGeneration extends javax.swing.JFrame implements ActionListener, Runnable, MasterProcessInterface  {
    
	private static String[] unitTab = {"usec", "msec", "sec"}; 
	
    protected Frame f;
    protected MainGUI mgui;
    
    private String textSysC1 = "Base directory of code generation code Soclib :";
    private String textSysC2 = "Compile executable code in";
	private String textSysC10 = "Base directory of code generation code TopCell";
    private String textSysC3 = "Base directory of code generation code TopCell :";
    private String textSysC4 = "Run code:";
	private String textSysC5 = "Run code and trace events (if enabled at code generation):";
	private String textSysC6 = "Run code in soclib / mutekh:";
	private String textSysC8 = "Show trace from file:";
	private String textSysC9 = "Show trace from soclib file:";
    
    private static String unitCycle = "1";
	
  //modif DG
	private static String[] codes = {"AVATAR TOPCELL", "AVATAR TOPCELL, TASKS AND MAIN"};
	private static int selectedItem = 0;
	private static int selectedRun = 1;
	private static int selectedCompile = 0;
	private static int selectedViewTrace = 0;
	private static boolean static_putUserCode = true;
	
    protected static String pathCode;
    protected static String pathCompiler;
    protected static String pathExecute;
	protected static String pathExecuteWithTracing;
	protected static String pathCompileSoclib;
	protected static String pathExecuteSoclib;
	protected static String pathSoclibTraceFile;
    protected static String pathCodeTopCell;
    protected static String pathCodeSocLib;
	
    protected static boolean optimizeModeSelected = true;
    
    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    
    private static 
    
      int mode;
    
  //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    
	protected JRadioButton exe, exeint, exetrace, exesoclib, compile, compilesoclib, viewtrace, viewtracesoclib;
	protected ButtonGroup compilegroup, exegroup, viewgroup;
    protected JLabel gen;
    protected JTextField code1, code2, compiler1, compiler2, exe1, exe2, exe3, exe4, exe2int, simulationTraceFile, simulationsoclibTraceFile;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
  
	protected JComboBox versionCodeGenerator, units;
    protected JButton showSimulationTrace;
	
	private static int selectedUnit = 2;
	private static boolean removeCFilesValue = true;
	private static boolean removeXFilesValue = true;
    private static boolean removeCCFilesValue = true;
	private static boolean debugValue = false;
	private static boolean tracingValue = true;
	private static boolean optimizeValue = true;
    
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
	protected boolean startProcess = false;

    
    private String hostExecute;
    
    private RshClient rshc;
    
    
  /** Creates new form  */
    public JDialogAvatarddExecutableCodeGeneration(Frame _f, MainGUI _mgui, String title, String _hostExecute, String _pathCodeSocLib , String _pathCodeTopCell, String _pathCompilerSoclib, String _pathSoclibTraceFile) {
      super(title);
        
      f = _f;
      mgui = _mgui;
        
      if (pathCodeSocLib == null) {
        pathCodeSocLib = _pathCodeSocLib;
      }
		
      if (pathSoclibTraceFile == null){
        pathSoclibTraceFile = _pathSoclibTraceFile;
      }
		
      if (pathCodeTopCell == null){
        pathCodeTopCell =  _pathCodeTopCell;
      }

      hostExecute = _hostExecute;
        
		
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
      jp01.setBorder(new javax.swing.border.TitledBorder("Code generation"));

      c01.gridheight = 1;
      c01.weighty = 1.0;
      c01.weightx = 1.0;
      c01.gridwidth = GridBagConstraints.REMAINDER; //end row
      c01.fill = GridBagConstraints.BOTH;
      c01.gridheight = 1;
        
      gen = new JLabel(textSysC1);
    //genJava.addActionListener(this);
      jp01.add(gen, c01);
        
      code1 = new JTextField(pathCodeSocLib, 100);
      jp01.add(code1, c01);

    // -----------------------------------------------
    // path code for TopCell

      gen = new JLabel(textSysC10);
    //genJava.addActionListener(this);
      jp01.add(gen, c01);

      code2 = new JTextField(pathCodeTopCell, 100);
      jp01.add(code2, c01);
        
    // ---------------------------------------------
          
      jp01.add(new JLabel(" "), c01);
      c01.gridwidth = GridBagConstraints.REMAINDER; //end row       
		
      jp01.add(new JLabel("Code generator used:"), c01);
		
      versionCodeGenerator = new JComboBox(codes);
      versionCodeGenerator.setSelectedIndex(selectedItem);
      versionCodeGenerator.addActionListener(this);
      jp01.add(versionCodeGenerator, c01);
        
      jp01.add(new JLabel(" "), c01);
      jp1.add("Generate code and topcell", jp01);

      c.add(jp1, BorderLayout.NORTH);
        
      jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Select options and then, click on 'start' to launch code generation\n");

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
        
    // Compare the action command to the known actions.
      if (command.equals("Start"))  {
        startProcess();
      } else if (command.equals("Stop")) {
        stopProcess();
      } else if (command.equals("Close")) {
        closeDialog();
      } else if (evt.getSource() == versionCodeGenerator) {
        selectedItem = versionCodeGenerator.getSelectedIndex();
      } else if (evt.getSource() == units) {
        selectedUnit = units.getSelectedIndex();
      }else if ((evt.getSource() == exe) || (evt.getSource() == exetrace)|| (evt.getSource() == exesoclib)) {
			makeSelectionExecute();
      }
    }

    public void makeSelectionExecute() {
      if (exe.isSelected()) {
        selectedRun = 0;
      } else {
        if (exetrace.isSelected()) {
          selectedRun = 1;
        } /*else {
          selectedRun = 2;
	  }*/
      }
			
      exe2.setEnabled(selectedRun == 0);
      exe3.setEnabled(selectedRun == 1);
      //exe4.setEnabled(selectedRun == 2);
			
	}
    
    public void closeDialog() {
      if (mode == STARTED) {
        stopProcess();
      }
      dispose();
    }
	
	  
    public void stopProcess() {
      System.err.println("stop process ! ");
      try {
        rshc.stopFillJTA();
      } catch (LauncherException le) {
      }
      rshc = null;
      mode = STOPPED;
      setButtons();
      go = false;
    }
    
    public void startProcess() {
      startProcess = false;
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
      String list, data;
      int cycle = 0;
        
      hasError = false;
        
      try {
        selectedItem = versionCodeGenerator.getSelectedIndex();
        if (selectedItem == 0) {
          System.err.println("AVATAR ");
        // Code generation
          if (jp1.getSelectedIndex() == 0) {
            jta.append("Generating TopCell \n");       	  
 
          System.err.println("AVATAR TOPCELL");
					
          testGo();
	  selectedUnit = 1;//DG 29.10.			
	  //selectedUnit = units.getSelectedIndex();
					
          ADDDiagramPanel deploymentDiagramPanel = mgui.getFirstAvatarDeploymentPanelFound();
          AvatarDeploymentPanelTranslator avdeploymenttranslator = new AvatarDeploymentPanelTranslator(deploymentDiagramPanel);
          AvatarddSpecification avddspec = avdeploymenttranslator.getAvatarddSpecification();

        // Generating code
          if ( avddspec == null) {
            jta.append("Error: No AVATAR Deployemnt specification\n");
          } else {
            System.err.println("AVATAR TOPCELL found");

            TopCellGenerator topCellGenerator = new TopCellGenerator(avddspec);
            testGo();
            jta.append("Generation of TopCell executable code: done\n");

            try {
              jta.append("Saving code in files\n");
              pathCode = code2.getText();
              topCellGenerator.saveFile(pathCode);

              jta.append("Code saved\n");
            } catch (Exception e) {
              jta.append("Could not generate files\n");
            }
          }
        }				     
        }
        testGo();
      // generation code Soclib ---------------------------------------
		
   			
      // generation Soclib / TopCell
	
      if (selectedItem == 1) {
        jta.append("Generating executable code (SOCLIB version)\n");
					
      //testGo();
	selectedUnit = 1;//DG 29.10.					
        //selectedUnit = units.getSelectedIndex();
      //System.out.println("Selected item=" + selectedItem);
  
      // get Design Panel
          AvatarDesignPanel designDiagramPanel = mgui.getFirstAvatarDesignPanelFound();
          AvatarDesignPanelTranslator avdesigntranslator = new AvatarDesignPanelTranslator( designDiagramPanel);
          LinkedList<AvatarBDBlock> adp =  designDiagramPanel.getAvatarBDPanel().getFullBlockList();
          Vector blocks = new Vector(adp);
          AvatarSpecification avaspec = avdesigntranslator.generateAvatarSpecification(blocks);

        // get Deployment Panel
          ADDDiagramPanel deploymentDiagramPanel = mgui.getFirstAvatarDeploymentPanelFound();
          AvatarDeploymentPanelTranslator avdeploymenttranslator = new AvatarDeploymentPanelTranslator(deploymentDiagramPanel);
          AvatarddSpecification avddspec = avdeploymenttranslator.getAvatarddSpecification();
						
				
      // Generating code
        if (avaspec == null) {
          jta.append("Error: No AVATAR Design specification\n");
        }else if( avddspec == null){
          jta.append("Error: No AVATAR Deployment specification\n");
        } else {

        // julien -----------------------------------------
      
          TopCellGenerator topCellGenerator = new TopCellGenerator(avddspec);
                    			  
          TasksAndMainGenerator gene = new TasksAndMainGenerator(avddspec,avaspec);
	  //          gene.includeUserCode(putUserCode.isSelected());
	  // gene.setTimeUnit(selectedUnit);
	  // gene. generateSoclib(debugmode.isSelected(), tracemode.isSelected());
        // --------------------------------------------------
        //testGo();
          jta.append("Generation of Soclib executable code: done\n");

          try {
            jta.append("Saving code in files\n");
            pathCode = code2.getText();
            gene.saveInFiles(pathCode);
            pathCode = code2.getText();
            topCellGenerator.saveFile(pathCode);
          //tml2systc.saveFile(pathCode, "appmodel");
            jta.append("Code saved\n");
          } catch (Exception e) {
            jta.append("Could not generate files\n");
          }
        }
      }
      }catch (InterruptedException ie) {
            jta.append("Interrupted\n");
      }

        jta.append("\n\nReady to process next command\n");    
        checkMode();
        setButtons();
    }

    protected void processCmd(String cmd, JTextArea _jta) throws LauncherException {
        rshc.setCmd(cmd);
        rshc.sendProcessRequest();
        rshc.fillJTA(_jta);
        return;
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
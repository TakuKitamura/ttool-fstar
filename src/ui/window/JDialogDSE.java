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
 * Class JDialogProVerifGeneration
 * Dialog for managing the generation of ProVerif code and execution of
 * ProVerif
 * Creation: 10/09/2010
 * @version 1.1 10/09/2010
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

import ui.*;
import ui.tmldd.*;

import myutil.*;
import avatartranslator.*;
import tmltranslator.*;
import ui.*;
import dseengine.*;
import launcher.*;


public class JDialogDSE extends javax.swing.JDialog implements ActionListener, Runnable  {

    protected MainGUI mgui;


    protected static String pathCode;
    protected static String pathExecute;


    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;
    int mode;
  
    JRadioButton dseButton;
    JRadioButton simButton;
    ButtonGroup group;
    //components
    
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    String simulator;
   
    protected JCheckBox autoConf, autoAuth, autoMapKeys, custom;
	
    protected JTextField encTime, decTime, secOverhead;

    protected JTextField tmlDirectory, mappingFile, modelFile, simulationThreads, resultsDirectory, simulationCycles, minCPU, maxCPU, simulationsPerMapping;
    protected JTextArea outputText;
    protected String output = "";
    protected JCheckBox secAnalysis;
    protected JTextField encTime2, decTime2, secOverhead2;


    String tmlDir;
    String mapFile = "spec.tmap";
    String modFile = "spec.tml";
    String resDirect;
    String simThreads="1000";
    String simCycles="1000";
    String NbMinCPU ="1";
    String NbMaxCPU ="1";
    String Nbsim ="100";
    String encCC="100";
    String decCC="100";
    String secOv = "100";

    protected JTabbedPane jp1;
    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    //protected boolean startProcess = false;

    private String hostProVerif;

    protected RshClient rshc;


    /** Creates new form  */
    public JDialogDSE(Frame f, MainGUI _mgui, String title, String _simulator, String dir) {
        super(f, title, true);

        mgui = _mgui;
	simulator=_simulator;
 	tmlDir = dir+"/";
	resDirect = _simulator + "results/";
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
        jp01.setBorder(new javax.swing.border.TitledBorder("Automated Security"));

    
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        //genJava.addActionListener(this);
	autoConf= new JCheckBox("Add security (Confidentiality)");
        jp01.add(autoConf, c01);
	autoAuth= new JCheckBox("Add security (Authenticity)");
        jp01.add(autoAuth, c01);
	autoMapKeys= new JCheckBox("Add Keys");
	jp01.add(autoMapKeys, c01);
	
	custom = new JCheckBox("Custom performance attributes");
	jp01.add(custom,c01);
	
	jp01.add(new JLabel("Encryption Computational Complexity"),c01);
	encTime = new JTextField(encCC);
	jp01.add(encTime,c01);

	jp01.add(new JLabel("Decryption Computational Complexity"),c01);
	decTime = new JTextField(decCC);
	jp01.add(decTime,c01);

	jp01.add(new JLabel("Data Overhead (bits)"),c01);
	secOverhead = new JTextField(secOv);
	jp01.add(secOverhead,c01);
	
	jp1.add("Automated Security", jp01);

	JPanel jp03 = new JPanel();
 	GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Mapping Exploration"));



	c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;
		
	jp03.add(new JLabel("Directory of TML specification files"),c03);
	tmlDirectory = new JTextField(tmlDir);
	jp03.add(tmlDirectory, c03);
	
	jp03.add(new JLabel("Mapping File name (.tmap)"),c03);
	mappingFile = new JTextField(mapFile);
	jp03.add(mappingFile,c03);
	
	jp03.add(new JLabel("Modeling File name (.tml)"),c03);
	modelFile = new JTextField(modFile);
	jp03.add(modelFile,c03);

	
	jp03.add(new JLabel("Number of Simulation Threads"),c03);
	simulationThreads = new JTextField(simThreads);
	jp03.add(simulationThreads, c03);

	
	jp03.add(new JLabel("Results Directory"),c03);
	resultsDirectory = new JTextField(resDirect);
	jp03.add(resultsDirectory, c03);

	
	jp03.add(new JLabel("Number of Simulation Cycles"),c03);
	simulationCycles = new JTextField(simCycles);
	jp03.add(simulationCycles, c03);

	
	jp03.add(new JLabel("Minimum Number of CPUs"),c03);
	minCPU = new JTextField(NbMinCPU);
	jp03.add(minCPU, c03);

	jp03.add(new JLabel("Maximum Number of CPUs"),c03);
	maxCPU = new JTextField(NbMaxCPU);
	jp03.add(maxCPU, c03);

	jp03.add(new JLabel("Number of Simulations Per Mapping"),c03);
	simulationsPerMapping = new JTextField(Nbsim);
	jp03.add(simulationsPerMapping, c03);


	secAnalysis = new JCheckBox("Security Analysis");
	jp03.add(secAnalysis,c03);
	
	jp03.add(new JLabel("Encryption Computational Complexity"),c03);
	encTime2 = new JTextField(encCC);
	jp03.add(encTime2,c03);

	jp03.add(new JLabel("Decryption Computational Complexity"),c03);
	decTime2 = new JTextField(decCC);
	jp03.add(decTime2,c03);

	jp03.add(new JLabel("Data Overhead (bits)"),c03);
	secOverhead2 = new JTextField(secOv);

	jp03.add(secOverhead2,c03);

	group = new ButtonGroup();
	dseButton = new JRadioButton("Run Design Space Exploration");
	jp03.add(dseButton,c03);
	simButton = new JRadioButton("Run Lots of Simulations");
	jp03.add(simButton,c03);
	group.add(dseButton);
	group.add(simButton);

	jp1.add("Mapping Exploration", jp03);

	JPanel jp04 = new JPanel();

 	GridBagLayout gridbag04 = new GridBagLayout();
        GridBagConstraints c04 = new GridBagConstraints();
        jp04.setLayout(gridbag04);

	c04.weighty = 1.0;
        c04.weightx = 1.0;
        c04.gridwidth = GridBagConstraints.REMAINDER; //end row
        c04.fill = GridBagConstraints.BOTH;
        c04.gridheight = 1;



        jp04.setBorder(new javax.swing.border.TitledBorder("DSE Output"));
	jp04.add(new JLabel("Design Space Exploration Output"), c04);



	outputText = new ScrolledJTextArea();
        outputText.setEditable(false);
        outputText.setMargin(new Insets(10, 10, 10, 10));
        outputText.setTabSize(3);
	outputText.append("Text here");
	JScrollPane jsp = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	jsp.setPreferredSize(new Dimension(300,300));
        Font f = new Font("Courrier", Font.BOLD, 12);
        outputText.setFont(f);
	jp04.add(jsp, c04);
	jp1.add("DSE Output", jp04);

	

	c.add(jp1, BorderLayout.NORTH);


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
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
    }

    public void stopProcess() {
        if (rshc != null ){
            try {
                rshc.stopFillJTA();
            } catch (LauncherException le) {
            }
        }
        rshc = null;
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
            throw new InterruptedException("Stopped by user");
        }
    }

    public void run() {
        String cmd;
        String list, data;
        int cycle = 0;

        hasError = false;
	//try {
	    mapFile = mappingFile.getText();
	    modFile = modelFile.getText();
	    tmlDir = tmlDirectory.getText();
	    resDirect = resultsDirectory.getText();
	    simThreads = simulationThreads.getText();
	    simCycles = simulationCycles.getText();
	    NbMinCPU = minCPU.getText();
	    NbMaxCPU = maxCPU.getText();
	    Nbsim = simulationsPerMapping.getText();
        TraceManager.addDev("Thread started");
        File testFile;
	if (jp1.getSelectedIndex() == 0){
	    encCC=encTime.getText();
	    decCC=decTime.getText();
	    secOv = secOverhead.getText();
	    TMLMapping map;
	    if (autoConf.isSelected() || autoAuth.isSelected()){
		if (custom.isSelected()){
		    map = mgui.gtm.autoSecure(mgui, encCC,secOv,decCC,autoConf.isSelected(), autoAuth.isSelected());
		}
		else {
	    	    map = mgui.gtm.autoSecure(mgui,autoConf.isSelected(), autoAuth.isSelected());
		}
	    }
	    if (autoMapKeys.isSelected()){
	    	mgui.gtm.autoMapKeys();
	    }
	}
	else if (jp1.getSelectedIndex()==1){
	    encCC=encTime2.getText();
	    decCC=decTime2.getText();
	    secOv = secOverhead2.getText();

	    DSEConfiguration config = new DSEConfiguration();
	    config.addSecurity = secAnalysis.isSelected();
	    config.encComp = encCC;
	    config.overhead = secOv;
	    config.decComp = decCC;

	    config.mainGUI = mgui;
	    TMLMapping map = mgui.gtm.getTMLMapping();
	    config.tmlcdp = map.getTMLCDesignPanel();
	    config.tmlap = map.tmlap;
	    if (config.setModelPath(tmlDir) != 0) {
		TraceManager.addDev("TML Directory file at " + tmlDir + " error");
		checkMode();
		return;
	    }
	    else {
		TraceManager.addDev("Set directory to " + tmlDir);
	    }
	    if (config.setMappingFile(mapFile) <0) {
		TraceManager.addDev("Mapping at " + mapFile + " error");
		checkMode();
		return;
	    }
	    else {
		TraceManager.addDev("Set mapping file to " + mapFile);
	    }
	    if (config.setTaskModelFile(modFile)!=0){
		TraceManager.addDev("Model File " + modFile +" error");
		checkMode();
		return;
	    }
	    else {
		TraceManager.addDev("Set model file to " + modFile);
	    }
	    if (config.setPathToSimulator(simulator) != 0) {
		TraceManager.addDev("Simulator at " + mapFile + " error");
		checkMode();
		return;
	    }
	    else {
		TraceManager.addDev("Simulator set");
	    }

	    if (config.setPathToResults(resDirect) != 0) {
		TraceManager.addDev("Results Directory at " + resDirect + " error");
		return;
	    }
	    else {
		TraceManager.addDev("Results Directory set");
	    }

	    if (config.setNbOfSimulationThreads(simThreads) != 0) {
		TraceManager.addDev("Simulation threads error: "+simThreads);
		return;
	    }
	    if (config.setSimulationCompilationCommand("make -j9 -C") !=0){
		TraceManager.addDev("Simulation compilation error");
		return;
	    }
	    if (config.setSimulationExecutionCommand("run.x") !=0){
		TraceManager.addDev("Simulation execution error");
		return;
	    }
	    if (config.setMinNbOfCPUs(NbMinCPU) != 0) {
		TraceManager.addDev("Can't set Min # CPUS to " + NbMinCPU);
	    }
	    if (config.setMaxNbOfCPUs(NbMaxCPU) != 0) {
		TraceManager.addDev("Can't set Max # CPUS to " + NbMaxCPU);
	    }
	    config.setOutputTXT("true");
	   // config.setOutputHTML("true");
	   // config.setOutputVCD("true");
	   // config.setOutputXML("true");
	    config.setRecordResults("true");
	    if (simButton.isSelected()){
		if (config.runParallelSimulation(Nbsim, true, true) != 0) {
		    output+="Simulation Failed";
		    outputText.setText(output);
		    checkMode();
		    return;
	    	}
	    	else {
		    output+="Simulation Succeeded";
		    outputText.setText(output);
		}
	    }
	    else if (dseButton.isSelected()){
	    	if (config.runDSE("", false, false)!=0){
		    TraceManager.addDev("Can't run DSE");
	   	}
	        System.out.println("DSE run");
	    }
	    if (config.printAllResults("", true, true)!=0){
		TraceManager.addDev("Can't print all results");
	    }
	    System.out.println("Results printed");
	    if (config.printResultsSummary("", true, true)!=0){
		TraceManager.addDev("Can't print result summary");
	    }
	    System.out.println("Results summary printed");
	    jp1.setSelectedIndex(2);
	    outputText.setText(output + "\n" + config.overallResults);
	}
	//} catch (Exception e){
	//    System.out.println(e);
	//}
	checkMode();
        setButtons();
	
        //System.out.println("Selected item=" + selectedItem);
    }

    protected String processCmd(String cmd) throws LauncherException {
        rshc.setCmd(cmd);
        String s = null;
	checkMode();
        return s;
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


    public void setError() {
        hasError = true;
    }

}

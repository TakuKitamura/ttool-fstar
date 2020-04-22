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

import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelchecker.SafetyProperty;
import avatartranslator.modelchecker.SpecificationLiveness;
import avatartranslator.modelchecker.SpecificationReachability;
import avatartranslator.modelchecker.SpecificationReachabilityType;
import myutil.*;
import ui.util.IconManager;
import ui.MainGUI;
import ui.TGComponent;
import graph.RG;
import graph.AUTGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Class JDialogAvatarModelChecker
 * Dialog for managing the model checking of avatar specifications
 * Creation: 1/06/2016
 *
 * @author Ludovic APVRILLE
 * @version 1.1 1/06/2016
 */
public class JDialogAvatarModelChecker extends javax.swing.JFrame implements ActionListener, Runnable, MasterProcessInterface {
    private final static String[] INFOS = {"Not started", "Running", "Stopped by user", "Finished"};
    private final static Color[] COLORS = {Color.darkGray, Color.magenta, Color.red, Color.blue};


    public final static int REACHABILITY_ALL = 1;
    public final static int REACHABILITY_SELECTED = 2;
    public final static int REACHABILITY_NONE = 3;

    public final static int LIVENESS_ALL = 4;
    public final static int LIVENESS_SELECTED = 5;
    public final static int LIVENESS_NONE = 6;


    protected static String graphDir;
    protected static boolean graphSelected = false;
    protected static String graphDirDot;
    protected static boolean graphSelectedDot = false;
    protected static boolean ignoreEmptyTransitionsSelected = true;
    protected static boolean ignoreConcurrenceBetweenInternalActionsSelected = true;
    protected static boolean ignoreInternalStatesSelected = true;
    protected static boolean generateDesignSelected = false;
    protected static int reachabilitySelected = REACHABILITY_NONE;
    protected static int livenessSelected = LIVENESS_NONE;
    protected static boolean safetySelected = false;
    protected static boolean checkNoDeadSelected = false;
    protected static boolean limitStatesSelected = false;
    protected static String stateLimitValue;
    protected static boolean limitTimeSelected = false;
    protected static String timeLimitValue;

    protected MainGUI mgui;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    private int mode;

    protected final static int NO_GRAPH = 1;
    protected final static int GRAPH_OK = 2;

    private int graphMode;
    private String graphAUT;

    private AvatarSpecification spec;

    private avatartranslator.modelchecker.AvatarModelChecker amc;
    private ModelCheckerMonitor mcm;
    private Date startDate, endDate;
    private Date previousDate;
    private int previousNbOfStates;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;
    protected JButton show;
    protected JButton display;

    //protected JRadioButton exe, exeint;
    //protected ButtonGroup exegroup;
    //protected JLabel gen, comp;
    //protected JTextField code1, code2, unitcycle, compiler1, exe1, exe2, exe3, exe2int, loopLimit;

    protected JRadioButton noReachability, reachabilityCheckable, reachabilityAllStates;
    protected ButtonGroup reachabilities;
    protected JRadioButton noLiveness, livenessCheckable, livenessAllStates;
    protected ButtonGroup liveness;
    protected boolean showLiveness;
    protected JCheckBox stateLimit;
    protected JTextField stateLimitField;
    protected JCheckBox timeLimit;
    protected JTextField timeLimitField;
    protected JCheckBox noDeadlocks;
    protected JCheckBox safety;

    protected JCheckBox saveGraphAUT, saveGraphDot, ignoreEmptyTransitions, ignoreInternalStates,
            ignoreConcurrenceBetweenInternalActions, generateDesign;
    protected JTextField graphPath, graphPathDot;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;

    // Information
    protected JLabel nbOfStates, nbOfLinks, nbOfPendingStates, elapsedTime, nbOfStatesPerSecond, nbOfDeadlocks, nbOfReachabilities, nbOfReachabilitiesNotFound, info;


    private Thread t;
    private boolean go = false;
    //  private boolean hasError = false;
    private java.util.Timer timer;
    //protected boolean startProcess = false;
    protected Map<String, Integer> verifMap;

    /*
     * Creates new form
     */
    public JDialogAvatarModelChecker(Frame f, MainGUI _mgui, String title, AvatarSpecification _spec, String _graphDir, boolean _showLiveness) {
        super(title);

        mgui = _mgui;
        spec = _spec;

        if (graphDir == null) {
            graphDir = _graphDir + File.separator + "rgavatar$.aut";
        }
        if (graphDirDot == null) {
            graphDirDot = _graphDir + File.separator + "rgavatar$.dot";
        }

        //showLiveness = _showLiveness;
        showLiveness = true;
        
        initComponents();
        myInitComponents();
        pack();
        
        verifMap = new HashMap<String, Integer>();

	/*if ((mgui != null) && (spec != null)) {
        mgui.drawAvatarSpecification(spec);
	    }*/

        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        graphMode = NO_GRAPH;
        setButtons();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Issue #41 Ordering of tabbed panes 
        jp1 = GraphicLib.createTabbedPane();//new JTabbedPane();

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

        if (TraceManager.devPolicy == TraceManager.TO_CONSOLE) {
            generateDesign = new JCheckBox("[For testing purpose only] Generate Design", generateDesignSelected);
            generateDesign.addActionListener(this);
            jp01.add(generateDesign, c01);
        }

        ignoreEmptyTransitions = new JCheckBox("Do not display empty transitions as internal actions", ignoreEmptyTransitionsSelected);
        ignoreEmptyTransitions.addActionListener(this);
        jp01.add(ignoreEmptyTransitions, c01);
        ignoreConcurrenceBetweenInternalActions = new JCheckBox("Ignore concurrency between internal actions", ignoreConcurrenceBetweenInternalActionsSelected);
        ignoreConcurrenceBetweenInternalActions.addActionListener(this);
        jp01.add(ignoreConcurrenceBetweenInternalActions, c01);
        ignoreInternalStates = new JCheckBox("Ignore states between internal actions",
                ignoreInternalStatesSelected);
        ignoreInternalStates.addActionListener(this);
        jp01.add(ignoreInternalStates, c01);
        
        noDeadlocks = new JCheckBox("No deadlocks?", checkNoDeadSelected);
        noDeadlocks.addActionListener(this);
        jp01.add(noDeadlocks, c01);


        // Reachability
        reachabilities = new ButtonGroup();

        noReachability = new JRadioButton("No reachability");
        noReachability.addActionListener(this);
        jp01.add(noReachability, c01);
        reachabilities.add(noReachability);

        reachabilityCheckable = new JRadioButton("Reachability of selected states");
        reachabilityCheckable.addActionListener(this);
        jp01.add(reachabilityCheckable, c01);
        reachabilities.add(reachabilityCheckable);

        reachabilityAllStates = new JRadioButton("Reachability of all states");
        reachabilityAllStates.addActionListener(this);
        jp01.add(reachabilityAllStates, c01);
        reachabilities.add(reachabilityAllStates);

        noReachability.setSelected(reachabilitySelected == REACHABILITY_NONE);
        reachabilityCheckable.setSelected(reachabilitySelected == REACHABILITY_SELECTED);
        reachabilityAllStates.setSelected(reachabilitySelected == REACHABILITY_ALL);


        // Liveness
        liveness = new ButtonGroup();

        noLiveness = new JRadioButton("No liveness");
        noLiveness.addActionListener(this);
        if (showLiveness) {
            jp01.add(noLiveness, c01);
        }
        liveness.add(noLiveness);

        livenessCheckable = new JRadioButton("Liveness of selected states");
        livenessCheckable.addActionListener(this);
        if (showLiveness) {
            jp01.add(livenessCheckable, c01);
        }
        liveness.add(livenessCheckable);

        livenessAllStates = new JRadioButton("Liveness of all states");
        livenessAllStates.addActionListener(this);
        if (showLiveness) {
            jp01.add(livenessAllStates, c01);
        }
        liveness.add(livenessAllStates);

        noLiveness.setSelected(livenessSelected == LIVENESS_NONE);
        livenessCheckable.setSelected(livenessSelected == LIVENESS_SELECTED);
        livenessAllStates.setSelected(livenessSelected == LIVENESS_ALL);
        
        
        //Safety pragmas
        safety = new JCheckBox("Safety Pragmas", safetySelected);
        safety.addActionListener(this);
        jp01.add(safety, c01);

        //Limitations
        stateLimit = new JCheckBox("Limit number of states in GF", limitStatesSelected);
        stateLimit.addActionListener(this);
        jp01.add(stateLimit, c01);
        stateLimitField = new JTextField(stateLimitValue);
        jp01.add(stateLimitField, c01);
        timeLimit = new JCheckBox("Limit time for GF generation (ms)", limitTimeSelected);
        timeLimit.addActionListener(this);
        jp01.add(timeLimit, c01);
        timeLimitField = new JTextField(timeLimitValue);
        jp01.add(timeLimitField, c01);


        // RG
        saveGraphAUT = new JCheckBox("Reachability Graph Generation", graphSelected);
        saveGraphAUT.addActionListener(this);
        //saveGraphAUT.addSelectionListener(this);
        jp01.add(saveGraphAUT, c01);
        graphPath = new JTextField(graphDir);
        jp01.add(graphPath, c01);
        saveGraphDot = new JCheckBox("Save RG in dotty:", graphSelectedDot);
        saveGraphDot.addActionListener(this);
        //saveGraphDot.setEnebaled(false);
        jp01.add(saveGraphDot, c01);
        graphPathDot = new JTextField(graphDirDot);
        jp01.add(graphPathDot, c01);
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
        show = new JButton("RG analysis", IconManager.imgic28);
        display = new JButton("Show", IconManager.imgic28);

        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));
        close.setPreferredSize(new Dimension(120, 30));
        show.setPreferredSize(new Dimension(150, 30));
        display.setPreferredSize(new Dimension(100, 30));

        start.addActionListener(this);
        stop.addActionListener(this);
        close.addActionListener(this);
        show.addActionListener(this);
        display.addActionListener(this);

        // Information
        JPanel jplow = new JPanel(new BorderLayout());

        JPanel jpinfo = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jpinfo.setLayout(gridbag02);
        jpinfo.setBorder(new javax.swing.border.TitledBorder("Graph information"));
        jplow.add(jpinfo, BorderLayout.NORTH);
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.fill = GridBagConstraints.HORIZONTAL;
        //c02.gridwidth = 1;
        //jpinfo.add(new JLabel(""), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        info = new JLabel();
        info.setFont(new Font("Serif", Font.BOLD, 16));
        updateInfo();
        jpinfo.add(info, c02);
        jpinfo.add(new JLabel(" "), c02);
        // nb of states, nb of links, nb of pending states, elapsed time, nbOfStatesPerSeconds


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of states:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfStates = new JLabel("-");
        jpinfo.add(nbOfStates, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of transitions:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfLinks = new JLabel("-");
        jpinfo.add(nbOfLinks, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Reachability found:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfReachabilities = new JLabel("-");
        jpinfo.add(nbOfReachabilities, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Reachability not found:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfReachabilitiesNotFound = new JLabel("-");
        jpinfo.add(nbOfReachabilitiesNotFound, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of deadlock states:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfDeadlocks = new JLabel("-");
        jpinfo.add(nbOfDeadlocks, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of pending states:"), c02);
        //c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfPendingStates = new JLabel("-");
        jpinfo.add(nbOfPendingStates, c02);

        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Nb of states/seconds:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        nbOfStatesPerSecond = new JLabel("-");
        jpinfo.add(nbOfStatesPerSecond, c02);


        c02.gridwidth = 1;
        jpinfo.add(new JLabel("Elapsed timed:"), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        elapsedTime = new JLabel("-");
        jpinfo.add(elapsedTime, c02);


        JPanel jp2 = new JPanel();
        jp2.add(start);
        jp2.add(stop);
        jp2.add(close);
        jp2.add(show);
        jp2.add(display);
        jplow.add(jp2, BorderLayout.SOUTH);

        c.add(jplow, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (command.equals("Start")) {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        } else if (evt.getSource() == show) {
            showGraph();
        } else if (evt.getSource() == display) {
            displayGraph();
        } else if (evt.getSource() == saveGraphAUT) {
            setButtons();
        } else if (evt.getSource() == saveGraphDot) {
            setButtons();
        } else if (evt.getSource() == ignoreEmptyTransitions) {
            setButtons();
        } else if (evt.getSource() == ignoreConcurrenceBetweenInternalActions) {
            setButtons();
        } else if (evt.getSource() == ignoreInternalStates) {
            setButtons();
        } else {
            setButtons();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        dispose();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void showGraph() {
        if (graphAUT != null) {
            mgui.showAUTFromString("Last RG", graphAUT);
        }
    }

    public void displayGraph() {
        // Make this in a different thread
        // And authorize the show only for a small nb of states ...

        if (graphAUT != null) {
            AUTGraph rg = new AUTGraph();
            rg.buildGraph(graphAUT);
            rg.display();
        }
    }

    public synchronized void stopProcess() {
        if (amc != null) {
            amc.stopModelChecking();
        }
        mode = STOPPED;
        setButtons();
        go = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        updateValues();

    }

    public synchronized void startProcess() {
        if (mode == STARTED) {
            return;
        }
        t = new Thread(this);
        mode = STARTED;
        graphMode = NO_GRAPH;
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
        //       String cmd;
        //     String list, data;
        //   int cycle = 0;

        //   hasError = false;

        TraceManager.addDev("Thread started");
        //   File testFile;
        try {
            reinitValues();
            jta.append("Starting the model checker\n");




            amc = new AvatarModelChecker(spec);

            if (generateDesignSelected) {
                TraceManager.addDev("Drawing non modified avatar spec");
                if ((mgui != null) && (spec != null)) {
                    mgui.drawAvatarSpecification(amc.getInitialSpec());
                }
            }

            endDate = null;
            previousNbOfStates = 0;
            startDate = new Date();
            mcm = new ModelCheckerMonitor(this);
            timer = new java.util.Timer(true);
            timer.scheduleAtFixedRate(mcm, 0, 500);

            // Setting options
            amc.setIgnoreEmptyTransitions(ignoreEmptyTransitionsSelected);
            amc.setIgnoreConcurrenceBetweenInternalActions(ignoreConcurrenceBetweenInternalActionsSelected);
            amc.setIgnoreInternalStates(ignoreInternalStatesSelected);
            amc.setCheckNoDeadlocks(checkNoDeadSelected);

            // Reachability
            int res;
            if (reachabilitySelected == REACHABILITY_SELECTED) {
                mgui.resetReachability();
                res = amc.setReachabilityOfSelected();
                jta.append("Reachability of " + res + " selected elements activated\n");

                for (SpecificationReachability sr : amc.getReachabilities()) {
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }

            if (reachabilitySelected == REACHABILITY_ALL) {
                mgui.resetReachability();
                res = amc.setReachabilityOfAllStates();
                jta.append("Reachability of " + res + " states activated\n");
                for (SpecificationReachability sr : amc.getReachabilities()) {
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }

            // RG?
            if (graphSelected || graphSelectedDot) {
                amc.setComputeRG(true);
                jta.append("Computation of Reachability Graph activated\n");
            }
            
            if (livenessSelected == LIVENESS_SELECTED) {
                mgui.resetLiveness();
                res = amc.setLivenessOfSelected();
                jta.append("Liveness of " + res + " states activated\n");
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.state, false);
                }
            }
            
            if (livenessSelected == LIVENESS_ALL) {
                mgui.resetLiveness();
                res = amc.setLivenessOfAllStates();
                jta.append("Liveness of " + res + " selected elements activated\n");
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.state, false);
                }
            }
            
            if (safetySelected) {
                res = amc.setSafetyAnalysis();
            }
            
            // Limitations
            if (stateLimit.isSelected()) {
            	amc.setStateLimit(true);
				try{
					Long stateLimitLong = Long.parseLong(stateLimitField.getText());
					if (stateLimitLong <= 0) {
						jta.append("State Limit field is not valid, insert a positive number\n");
						go = false;
					}
					amc.setStateLimitValue(stateLimitLong.longValue());
				} catch (NumberFormatException e) {
					jta.append("State Limit field is not valid\n");
					go = false;
				}
            }
            if (timeLimit.isSelected()) {
                amc.setTimeLimit(true);
                try{
                    Long timeLimitLong = Long.parseLong(timeLimitField.getText());
                    if (timeLimitLong <= 0) {
                        jta.append("State Limit field is not valid, insert a positive number\n");
                        go = false;
                    }
                    amc.setTimeLimitValue(timeLimitLong.longValue());
                } catch (NumberFormatException e) {
                    jta.append("Time Limit field is not valid\n");
                    go = false;
                }
            }

            // Starting model checking
            testGo();

            if (livenessSelected == LIVENESS_NONE && safetySelected == false) {
                amc.startModelChecking();
            } else {
                amc.startModelCheckingProperties();
            }
            
            TraceManager.addDev("Model checking done");
            //TraceManager.addDev("RG:" + amc.statesToString() + "\n\n");

            /*if (generateDesignSelected) {
                TraceManager.addDev("Drawing modified avatar spec");
                AvatarSpecification reworkedSpec = amc.getReworkedAvatarSpecification();
                if ((mgui != null) && (reworkedSpec != null)) {
                    mgui.drawAvatarSpecification(reworkedSpec);
                }
            }*/

            timer.cancel();
            endDate = new Date();
            updateValues();
            jta.append("\n\nModel checking done\n");
            jta.append("Nb of states:" + amc.getNbOfStates() + "\n");
            jta.append("Nb of links:" + amc.getNbOfLinks() + "\n");
            if (checkNoDeadSelected) {
                jta.append("\nNo deadlocks?\n" + "-> " + amc.deadlockToString() + "\n");
            }

            if ((reachabilitySelected == REACHABILITY_SELECTED) || (reachabilitySelected == REACHABILITY_ALL)) {
                jta.append("\nReachabilities found:\n");
                jta.append(amc.reachabilityToString());

                // Back annotation on diagrams
                for (SpecificationReachability sr : amc.getReachabilities()) {
                    //TraceManager.addDev("Handing reachability of " + sr);
                    handleReachability(sr.ref1, sr.result);
                    if (sr.ref2 != sr.ref1) {
                        handleReachability(sr.ref2, sr.result);
                    }
                }
            }
            
            if (livenessSelected != LIVENESS_NONE) {
                jta.append("\nLiveness Analysis:\n");
                jta.append(amc.livenessToString());
                
                for (SafetyProperty sp : amc.getLivenesses()) {
                    handleLiveness(sp.state, sp.result);
                }
            }
            
            if (safetySelected) {
                jta.append("\nSafety Analysis:\n");
                jta.append(amc.safetyToString());
                handleSafety(amc.getSafeties());
            }

            //TraceManager.addDev(amc.toString());
            //TraceManager.addDev(amc.toString());
            DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
            Date date = new Date();
            String dateAndTime = dateFormat.format(date);

            if (saveGraphAUT.isSelected()) {
                graphAUT = amc.toAUT();
                graphMode = GRAPH_OK;
                //TraceManager.addDev("graph AUT=\n" + graph);

                String autfile;
                if (graphPath.getText().indexOf("$") != -1) {
                    autfile = Conversion.replaceAllChar(graphPath.getText(), '$', dateAndTime);
                } else {
                    autfile = graphPath.getText();
                }

                try {
                    RG rg = new RG(autfile);
                    rg.data = graphAUT;
                    rg.fileName = autfile;
                    rg.nbOfStates = amc.getNbOfStates();
                    rg.nbOfTransitions = amc.getNbOfLinks();
                    File f = new File(autfile);
                    rg.name = f.getName();
                    mgui.addRG(rg);
                    FileUtils.saveFile(autfile, graphAUT);
                    jta.append("Graph saved in " + autfile + "\n");
                } catch (Exception e) {
                    jta.append("Graph could not be saved in " + autfile + "\n");
                }
            }
            if (saveGraphDot.isSelected()) {
                String dotfile;
                if (graphPathDot.getText().indexOf("$") != -1) {
                    dotfile = Conversion.replaceAllChar(graphPathDot.getText(), '$', dateAndTime);
                } else {
                    dotfile = graphPathDot.getText();
                }
                try {
                    String graph = amc.toDOT();
                    //TraceManager.addDev("graph AUT=\n" + graph);
                    FileUtils.saveFile(dotfile, graph);
                    jta.append("Graph saved in " + dotfile + "\n");
                } catch (Exception e) {
                    jta.append("Graph could not be saved in " + dotfile + "\n");
                }
            }

        } catch (InterruptedException ie) {
            jta.append("Interrupted\n");
        }

        jta.append("\n\nReady to process next command\n");

        checkMode();
        setButtons();

        //
    }

    protected void handleReachability(Object _o, SpecificationReachabilityType _res) {
        if (_o instanceof AvatarStateMachineElement) {
            Object o = ((AvatarStateMachineElement) _o).getReferenceObject();
            if (o instanceof TGComponent) {
                TGComponent tgc = (TGComponent) (o);
                //TraceManager.addDev("Reachability of tgc=" + tgc + " value=" + tgc.getValue() + " class=" + tgc.getClass());
                switch (_res) {
                    case NOTCOMPUTED:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_UNKNOWN);
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_UNKNOWN);
                        break;
                    case REACHABLE:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_OK);
                        break;
                    case NONREACHABLE:
                        tgc.setReachability(TGComponent.ACCESSIBILITY_KO);
                        tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                        break;
                }
                tgc.getTDiagramPanel().repaint();
            }
        }
    }
    
    protected void handleLiveness(Object _o, boolean _res) {
        if (_o instanceof AvatarStateMachineElement) {
            Object o = ((AvatarStateMachineElement) _o).getReferenceObject();
            if (o instanceof TGComponent) {
                TGComponent tgc = (TGComponent) (o);
                //TraceManager.addDev("Reachability of tgc=" + tgc + " value=" + tgc.getValue() + " class=" + tgc.getClass());
                if (_res) {
                    tgc.setReachability(TGComponent.ACCESSIBILITY_OK);
                    tgc.setLiveness(TGComponent.ACCESSIBILITY_OK);
                } else {
                    tgc.setLiveness(TGComponent.ACCESSIBILITY_KO);
                }
                tgc.getTDiagramPanel().repaint();
            }
        }
    }
    
    protected void handleSafety(ArrayList<SafetyProperty> safeties) {
        int status;
        for (SafetyProperty sp : safeties) {
            if (sp.result) {
                status = 1;
            } else {
                status = 0;
            }
            verifMap.put(sp.getRawProperty(), status);
        }
        mgui.modelBacktracingUPPAAL(verifMap);
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
        graphSelected = saveGraphAUT.isSelected();
        graphPath.setEnabled(saveGraphAUT.isSelected());
        graphSelectedDot = saveGraphDot.isSelected();
        saveGraphDot.setEnabled(saveGraphAUT.isSelected());
        graphPathDot.setEnabled(saveGraphDot.isSelected() && saveGraphAUT.isSelected());
        if (generateDesign != null) {
            generateDesignSelected = generateDesign.isSelected();
        }
        ignoreEmptyTransitionsSelected = ignoreEmptyTransitions.isSelected();
        ignoreConcurrenceBetweenInternalActionsSelected = ignoreConcurrenceBetweenInternalActions.isSelected();
        ignoreInternalStatesSelected = ignoreInternalStates.isSelected();
        checkNoDeadSelected = noDeadlocks.isSelected();

        if (noReachability.isSelected()) {
            reachabilitySelected = REACHABILITY_NONE;
        } else if (reachabilityCheckable.isSelected()) {
            reachabilitySelected = REACHABILITY_SELECTED;
        } else {
            reachabilitySelected = REACHABILITY_ALL;
        }
        
        if (noLiveness.isSelected()) {
            livenessSelected = LIVENESS_NONE;
        } else if (livenessCheckable.isSelected()) {
            livenessSelected = LIVENESS_SELECTED;
        } else {
            livenessSelected = LIVENESS_ALL;
        }
        
        safetySelected = safety.isSelected();
        stateLimitField.setEnabled(stateLimit.isSelected());
        limitStatesSelected = stateLimit.isSelected();
        timeLimitField.setEnabled(timeLimit.isSelected());
        limitTimeSelected = timeLimit.isSelected();

        switch (mode) {
            case NOT_STARTED:
                if ((reachabilitySelected == REACHABILITY_SELECTED) || (reachabilitySelected == REACHABILITY_ALL) || (livenessSelected == LIVENESS_SELECTED) || (livenessSelected == LIVENESS_ALL) || safetySelected|| graphSelected || graphSelectedDot) {
                    start.setEnabled(true);
                } else {
                    start.setEnabled(false);
                }
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

        show.setEnabled(graphMode == GRAPH_OK);
        display.setEnabled(graphMode == GRAPH_OK);
    }

    public boolean hasToContinue() {
        return (go == true);
    }

    public void appendOut(String s) {
        jta.append(s);
    }

    @Override
    public void setError() {
//        hasError = true;
    }

    public void updateValues() {
        //TraceManager.addDev("Updating values...");
        try {
            if (amc != null) {
                int nbOfStatess = amc.getNbOfStates();
                nbOfStates.setText("" + nbOfStatess);
                nbOfLinks.setText("" + amc.getNbOfLinks());

                // Reachability and deadlocks
                int nb = amc.getNbOfReachabilities();
                if (nb == -1) {
                    //nbOfReachabilities.setText("-");
                } else {
                    nbOfReachabilities.setText("" + nb);
                }
                nb = amc.getNbOfRemainingReachabilities();
                nbOfReachabilitiesNotFound.setText("" + nb);


                nbOfDeadlocks.setText("" + amc.getNbOfDeadlocks());


                nbOfPendingStates.setText("" + amc.getNbOfPendingStates());
                Date d;
                previousDate = new Date();
                if (endDate != null) {
                    d = endDate;
                } else {
                    d = previousDate;
                }
                long duration = d.getTime() - startDate.getTime();
                long durationMn = TimeUnit.MILLISECONDS.toMinutes(duration);
                long durationSec = TimeUnit.MILLISECONDS.toSeconds(duration) - durationMn * 60;
                long durationMs = duration - 1000 * (durationSec - durationMn * 60);

                //TraceManager.addDev("mn=" + durationMn + " sec=" + durationSec + " ms=" + durationMs + " raw=" + duration);

                String t = String.format("%02d min %02d sec %03d msec", durationMn, durationSec, durationMs);

//                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                //long diffInMs = TimeUnit.MILLISECONDS.toMilliseconds(duration);
                elapsedTime.setText(t);
                long diff = 0;
                if (endDate != null) {
                    diff = nbOfStatess;
                } else {
                    diff = nbOfStatess - previousNbOfStates;
                }
                previousNbOfStates = nbOfStatess;
                //if (diff == 0) {


                nbOfStatesPerSecond.setText("" + (int)(1000.0 * diff / duration));

                updateInfo();
                //}
            }
        } catch (Exception e) {
        }
    }

    public void reinitValues() {
        nbOfStates.setText("-");
        nbOfLinks.setText("-");
        nbOfReachabilities.setText("-");
        nbOfReachabilitiesNotFound.setText("-");
        nbOfDeadlocks.setText("-");
        nbOfPendingStates.setText("-");
        nbOfStatesPerSecond.setText("-");
        elapsedTime.setText("-");
    }

    public int getStateIndex() {
        if (amc == null) {
            return 0;
        }


        if ((endDate == null) && (go == true)) {
            return 1;
        }

        return amc.hasBeenStoppedBeforeCompletion() ? 2 : 3;

    }

    public void updateInfo() {
        info.setForeground(COLORS[getStateIndex()]);
        info.setText(INFOS[getStateIndex()]);
    }

    private class ModelCheckerMonitor extends TimerTask {
        private JDialogAvatarModelChecker jdamc;

        public ModelCheckerMonitor(JDialogAvatarModelChecker _jdamc) {
            jdamc = _jdamc;
        }

        public void run() {
            jdamc.updateValues();
        }

    }
}

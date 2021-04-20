
/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
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

package ui;

import attacktrees.AttackTree;
import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarSpecification;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import graph.RG;
import help.HelpEntry;
import help.HelpManager;
import launcher.RemoteExecutionThread;
import launcher.RshClient;
import myutil.*;
import proverifspec.ProVerifOutputAnalyzer;
import tmltranslator.TMLMapping;
import tmltranslator.simulation.SimulationTransaction;
import translator.CheckingError;
import translator.MasterGateManager;
import ui.ad.TActivityDiagramPanel;
import ui.atd.AttackTreeDiagramPanel;
import ui.avatarad.AvatarADPanel;
import ui.avatarbd.AvatarBDLibraryFunction;
import ui.avatarbd.AvatarBDPortConnector;
import ui.avatarbd.AvatarBDStateMachineOwner;
import ui.avatarcd.AvatarCDPanel;
import ui.avatardd.ADDDiagramPanel;
import ui.avatarinteractivesimulation.JFrameAvatarInteractiveSimulation;
import ui.avatarmad.AvatarMADPanel;
import ui.avatarpd.AvatarPDPanel;
import ui.avatarrd.AvatarRDPanel;
import ui.avatarsmd.AvatarSMDPanel;
import ui.cd.TClassDiagramPanel;
import ui.diplodocusmethodology.DiplodocusMethodologyDiagramPanel;
import ui.ebrdd.EBRDDPanel;
import ui.eln.ELNDiagramPanel;
import ui.file.*;
import ui.ftd.FaultTreeDiagramPanel;
import ui.interactivesimulation.JFrameInteractiveSimulation;
import ui.iod.InteractionOverviewDiagramPanel;
import ui.networkmodelloader.JDialogLoadingNetworkModel;
import ui.osad.TURTLEOSActivityDiagramPanel;
import ui.prosmd.ProactiveSMDPanel;
import ui.req.Requirement;
import ui.req.RequirementDiagramPanel;
import ui.simulationtraceanalysis.JFrameCompareSimulationTraces;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;
import ui.syscams.SysCAMSComponentTaskDiagramPanel;
import ui.syscams.SysCAMSCompositeComponent;
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcompd.TMLCCompositeComponent;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;
import ui.tmlcp.TMLCPPanel;
import ui.tmldd.TMLArchiArtifact;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiNode;
import ui.tmlsd.TMLSDPanel;
import ui.tree.DiagramTreeModel;
import ui.tree.DiagramTreeRenderer;
import ui.tree.JDiagramTree;
import ui.ucd.UseCaseDiagramPanel;
import ui.util.DefaultText;
import ui.util.IconManager;
import ui.window.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// AVATAR

/**
 * Class MainGUI Main window / Actions management / Mode management Creation:
 * 15/12/2003 Version: 1.1 21/12/2003 Version: 1.2 29/09/2004 26/11/2015 D.
 * Genius added DDD
 *
 * @author Ludovic APVRILLE
 */
public class MainGUI implements ActionListener, WindowListener, KeyListener, PeriodicBehavior, DraggableTabbedPaneCallbackInterface {

    public static boolean systemcOn;
    public static boolean lotosOn;
    public static boolean proactiveOn;
    public static boolean tpnOn;
    public static boolean osOn;
    public static boolean uppaalOn;
    public static boolean ncOn;
    public static boolean avatarOn;
    public static boolean proverifOn;
    public static boolean experimentalOn;
    public static boolean avatarOnly;
    public static boolean turtleOn;
    public static boolean openLast;

    public boolean isxml = false;

    public final static int LOTOS = 0;
    public final static int RT_LOTOS = 1;
    public final static int UPPAAL = 2;
    public final static int JAVA = 3;
    public final static int DESIGN = 4;

    public JFrame frame; // Main Frame
    public Container framePanel; // Main pane
    public Container panelForTab, panelForTree; // panelForAnalysisTab; //panelForDesignTab;
    public JSplitPane split, split1;

    // Multi analysis / design / deployment
    public Vector<TURTLEPanel> tabs;
    /*
     * This dummySelectedTab is used when loading a model from XML. It enables to
     * use standard getCurrentTURTLEPanel even though the mainTabbedPane has not yet
     * been created.
     */
    private TURTLEPanel dummySelectedTab;

    // JTree
    public JDiagramTree dtree;
    public DiagramTreeModel dtm;

    // Actions
    public TGUIAction[] actions;
    public TGUIAction[] actionsLast;
    public MouseHandler mouseHandler;
    public KeyListener keyHandler;

    // Validation
    public List<TClassInterface> tclassesToValidate = new LinkedList<TClassInterface>();

    // Status bar
    private JLabel status;

    // Menubar
    private JMenuBarTurtle jmenubarturtle;

    // Communication key
    private String sk;

    // Annex windows
    JFrameCode javaframe;
    JFrameBird birdframe;
    private boolean hasChanged = false;

    // @author: Huy TRUONG
    public JDialogSearchBox searchBox;

    public final static byte NOT_OPENED = 0;
    public final static byte OPENED = 1;
    public final static byte MODEL_OK = 2;
    public final static byte MODEL_CHANGED = 3; 
    public final static byte MODEL_SAVED = 4;
    public final static byte RTLOTOS_OK = 5;
    public final static byte BACKWARD = 6;
    public final static byte NO_BACKWARD = 7;
    public final static byte FORWARD = 8;
    public final static byte NO_FORWARD = 9;
    public final static byte FORWARD_DIAG = 10;
    public final static byte BACKWARD_DIAG = 11;
    public final static byte NO_FORWARD_DIAG = 12;
    public final static byte NO_BACKWARD_DIAG = 13;
    public final static byte DTADOT_OK = 14;
    public final static byte DTADOT_KO = 15;
    public final static byte RGDOT_OK = 16;
    public final static byte RGDOT_KO = 17;
    public final static byte TLSADOT_OK = 31;
    public final static byte TLSADOT_KO = 32;
    public final static byte SIM_OK = 18;
    public final static byte SIM_KO = 19;
    public final static byte CUTCOPY_OK = 20;
    public final static byte CUTCOPY_KO = 21;
    public final static byte PASTE_OK = 22;
    public final static byte RGAUTDOT_OK = 23;
    public final static byte RGAUTDOT_KO = 24;
    public final static byte RGAUT_OK = 25;
    public final static byte RGAUT_KO = 26;
    public final static byte RGAUTPROJDOT_OK = 27;
    public final static byte RGAUTPROJDOT_KO = 28;
    public final static byte EXPORT_LIB_OK = 29;
    public final static byte EXPORT_LIB_KO = 30;
    public final static byte METHO_CHANGED = 33;
    public final static byte VIEW_SUGG_DESIGN_OK = 34;
    public final static byte VIEW_SUGG_DESIGN_KO = 35;
    public final static byte GEN_DESIGN_OK = 36;
    public final static byte GEN_DESIGN_KO = 37;
    public final static byte GEN_SYSTEMC_OK = 38;
    public final static byte GEN_SYSTEMC_KO = 39;
    public final static byte VIEW_WAVE_OK = 40;
    public final static byte REQ_OK = 41;
    public final static byte UPPAAL_OK = 42;
    public final static byte NC_OK = 43;
    public final static byte MODEL_UPPAAL_OK = 44;
    public final static byte EDIT_PROVERIF_OK = 46;
    public final static byte AVATAR_SYNTAXCHECKING_OK = 47;
    public final static byte PANEL_CHANGED = 48;
    public final static byte ATTACKTREE_SYNTAXCHECKING_OK = 49;
    public final static byte FAULTTREE_SYNTAXCHECKING_OK = 51;
    public final static byte COMPONENT_SELECTED = 50;
    public final static byte CREATE_NEW_PANEL = 52;

    public final static int INCREMENT = 10;

    public static Object BACK_COLOR;
    // public static Object BACK_COLOR;

    public final static String REMOTE_RTL_LOTOS_FILE = "spec.lot";
    public final static String REMOTE_UPPAAL_FILE = "spec.xml";
    public final static String REMOTE_ALDEBARAN_AUT_FILE = "spec.aut";
    public final static String REMOTE_ALDEBARAN_BISIMU_FILE1 = "file1.aut";
    public final static String REMOTE_ALDEBARAN_BISIMU_FILE2 = "file2.aut";
    public final static String REMOTE_BISIMULATOR_FILE1 = "lts1bis";
    public final static String REMOTE_BISIMULATOR_FILE2 = "lts2bis";

    public final static byte METHO_ANALYSIS = 0;
    public final static byte METHO_DESIGN = 1;
    public final static byte METHO_DEPLOY = 2;

    public byte mode;
    public byte methoMode;

    // TURTLE Modeling
    public GTURTLEModeling gtm;

    // TURTLE modeling graphic components
    // private JTabbedPane mainTabbedPane;
    private myutil.DraggableEnhancedTabbedPane mainTabbedPane;
    private JToolBarMainTurtle mainBar;
    // private JPopupMenu menuTabbedPane;

    private TDiagramPanel activetdp;

    // Modified graphs
    private String modifiedaut;
    private String modifiedautdot;

    private RG lastDiploRG;

    // JBirdPanel
    private JBirdPanel jbp;

    private int typeButtonSelected;
    private int idButtonSelected;
    private Plugin pluginSelected;

    private File file;
    private File dir;
    private File config;
    private File lotosfile;
    private File simfile;
    private File dtafile;
    private File dtadotfile;
    private File rgfile;
    private File rgdotfile;
    private File tlsafile;
    private File tlsadotfile;
    private File rgautfile;
    private File fc2file;
    private File bcgfile;
    private File rgautdotfile;
    private File rgautprojfile;
    private File rgautprojdotfile;
    private JFileChooser jfc;
    private JFileChooser jfclib;
    private JFileChooser jfcimg;
    private JFileChooser jfcimgsvg;
    private JFileChooser jfcggraph;
    private JFileChooser jfctgraph;
    private JFileChooser jfclot;
    private JFileChooser jfctif;
    private JFileChooser jfcmsc;

    // private int selectedAction = -1;

    // Interaction with simulators
    private ArrayList<RunningInfo> runningIDs;
    private ArrayList<LoadInfo> loadIDs;
    private ConcurrentHashMap<Integer, ArrayList<SimulationTransaction>> transactionMap = new ConcurrentHashMap<Integer, ArrayList<SimulationTransaction>>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> latencyMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>>();
    private ConcurrentHashMap<String, String> statusMap = new ConcurrentHashMap<String, String>();
    private JFrameInteractiveSimulation jfis;
    private JFrameAvatarInteractiveSimulation jfais;
    private JFrameCompareSimulationTraces cSimTrace;
    
    
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    


    // Help
    private HelpManager helpManager;
    private JFrameHelp helpFrame;

    // Invariants
    private Invariant currentInvariant;

    // Thread for autosave
    private PeriodicBehaviorThread pbt;   


    private boolean hidden = false;

    public MainGUI(boolean _openLast, boolean _turtleOn, boolean _systemcOn, boolean _lotosOn, boolean _proactiveOn, boolean _tpnOn, boolean _osOn,
            boolean _uppaalOn, boolean _ncOn, boolean _avatarOn, boolean _proverifOn, boolean _avatarOnly, boolean _experimental) {
        openLast = _openLast;
        TraceManager.addDev("openLast=" + openLast);
        turtleOn = _turtleOn;
        systemcOn = _systemcOn;
        lotosOn = _lotosOn;
        proactiveOn = _proactiveOn;
        tpnOn = _tpnOn;
        osOn = _osOn;
        uppaalOn = _uppaalOn;
        ncOn = _ncOn;
        avatarOn = _avatarOn;
        proverifOn = _proverifOn;
        avatarOnly = _avatarOnly;
        experimentalOn = _experimental;

        currentInvariant = null;

        pbt = new PeriodicBehaviorThread(this, 120000); // save every two minutes

    }

    public void setKey(String _sk) {
        sk = _sk;
        RshClient.sk = sk;
    }

    public String getKey() {
        return sk;
    }

    public File getDir() {
        return dir;
    }

    public boolean isAvatarOn() {
        return avatarOn;
    }

    public boolean isSystemcOn() {
        return systemcOn;
    }

    public void build() {
        // Swing look and feel

        // try {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException e) {
            ErrorGUI.exit(ErrorGUI.GUI);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ErrorGUI.exit(ErrorGUI.GUI);
            e.printStackTrace();
        }
        // } catch (Exception e) { ErrorGUI.exit(ErrorGUI.GUI);}

        // Creating main container

        frame = new JFrame("TTool");

        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setIconImage(IconManager.img8);

        framePanel = frame.getContentPane();
        framePanel.setLayout(new BorderLayout());

        // file chooser
        if (ConfigurationTTool.FILEPath.length() > 0) {
            jfc = new JFileChooser(ConfigurationTTool.FILEPath);
        } else {
            jfc = new JFileChooser();
        }

        if (ConfigurationTTool.FILEPath.length() > 0) {
            jfctif = new JFileChooser(ConfigurationTTool.FILEPath);
        } else {
            jfctif = new JFileChooser();
        }

        if (ConfigurationTTool.FILEPath.length() > 0) {
            jfcmsc = new JFileChooser(ConfigurationTTool.FILEPath);
        } else {
            jfcmsc = new JFileChooser();
        }

        if (ConfigurationTTool.LIBPath.length() > 0) {
            jfclib = new JFileChooser(ConfigurationTTool.LIBPath);
        } else {
            jfclib = new JFileChooser();
        }

        if (SpecConfigTTool.IMGPath.length() > 0) {
            jfcimg = new JFileChooser(SpecConfigTTool.IMGPath);
        } else {
            jfcimg = new JFileChooser();
        }

        if (SpecConfigTTool.IMGPath.length() > 0) {
            jfcimgsvg = new JFileChooser(SpecConfigTTool.IMGPath);
        } else {
            jfcimgsvg = new JFileChooser();
        }

        if (ConfigurationTTool.LOTOSPath.length() > 0) {
            jfclot = new JFileChooser(ConfigurationTTool.LOTOSPath);
        } else {
            jfclot = new JFileChooser();
        }

        if (SpecConfigTTool.GGraphPath.length() > 0) {
            jfcggraph = new JFileChooser(SpecConfigTTool.GGraphPath);
        } else {
            jfcggraph = new JFileChooser();
        }

        if (SpecConfigTTool.TGraphPath.length() > 0) {
            jfctgraph = new JFileChooser(SpecConfigTTool.TGraphPath);
        } else {
            jfctgraph = new JFileChooser();
        }

        TFileFilter filter = new TFileFilter();
        jfc.setFileFilter(filter);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        TTIFFilter filtertif = new TTIFFilter();
        jfctif.setFileFilter(filtertif);

        MSCFilter filtermsc = new MSCFilter();
        jfcmsc.setFileFilter(filtermsc);

        TLibFilter filterLib = new TLibFilter();
        jfclib.setFileFilter(filterLib);

        TImgFilter filterImg = new TImgFilter();
        jfcimg.setFileFilter(filterImg);

        TSVGFilter filterSVG = new TSVGFilter();
        jfcimgsvg.setFileFilter(filterSVG);

        RTLFileFilter filterRTL = new RTLFileFilter();
        jfclot.setFileFilter(filterRTL);

        TDotFilter filterDot = new TDotFilter();
        jfcggraph.setFileFilter(filterDot);

        // Actions
        initActions();

        // mode
        setMode(NOT_OPENED);

        // statusBar
        status = createStatusBar();

        // Mouse handler
        mouseHandler = new MouseHandler(status);

        framePanel.add(status, BorderLayout.SOUTH);

        // toolbar
        mainBar = new JToolBarMainTurtle(this);
        framePanel.add(mainBar, BorderLayout.NORTH);

        // Panels
        panelForTab = new JPanel();
        panelForTab.setLayout(new BorderLayout());

        // panelForTree = new JPanel(); panelForTree.setLayout(new BorderLayout());
        // Tree
        dtree = new JDiagramTree(this);
        dtree.setCellRenderer(new DiagramTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(dtree);
        JScrollPane scrollPane = new JScrollPane(dtree);
        scrollPane.setPreferredSize(new Dimension(200, 600));
        scrollPane.setMinimumSize(new Dimension(25, 200));
        jbp = new JBirdPanel(this);
        jbp.setPreferredSize(new Dimension(200, 200));
        split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, jbp);

        // split1.setDividerLocation(700);
        split1.setResizeWeight(0.1);
        // split1.setOneTouchExpandable(true);
        // panelForTree.add(scrollPane, BorderLayout.CENTER);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, split1, panelForTab);
        framePanel.add(split, BorderLayout.CENTER);
        split.setDividerLocation(250);
        split1.setResizeWeight(0.9);
        split.setOneTouchExpandable(true);
        // split1.resetToPreferredSizes();

        // Creating menus
        jmenubarturtle = new JMenuBarTurtle(this);
        frame.setJMenuBar(jmenubarturtle);

        // if openLast, must open the latest specification (if it exists)
        if ((ConfigurationTTool.LastOpenFileDefined) && (openLast)) {
            openLastProject();
            dtree.update();
            // split1.setDividerLocation(600);
            // split.setDividerLocation(220);
        }

        // Help
        helpManager = new HelpManager();
        // TraceManager.addDev("Resources:");
        // helpManager.listFiles("/help");
        if (!helpManager.loadEntries()) {
            TraceManager.addDev("Failed to load help");
        } else {
            TraceManager.addDev(helpManager.printHierarchy());
        }

        //

        // ToolBar
        // toolbarDesign = new Vector();
        // toolbarAnalysis = new Vector();

        // Panels
        // analysisPanels = new Vector();
        // designPanels = new Vector();

    }

    public void initActions() {
        actions = new TGUIAction[TGUIAction.NB_ACTION];
        for (int i = 0; i < TGUIAction.NB_ACTION; i++) {
            actions[i] = new TGUIAction(i);
            actions[i].addActionListener(this);
            // actions[i].addKeyListener(this);
        }
        actionsLast = new TGUIAction[ConfigurationTTool.NB_LAST_OPEN_FILE];
        for (int j = 0; j < actionsLast.length; j++) {
            actionsLast[j] = new TGUIAction(TGUIAction.ACT_OPEN_LAST, "Open recent: " + ConfigurationTTool.LastOpenFiles[j]);
            actionsLast[j].addActionListener(this);
            // actions[i].addKeyListener(this);
        }
        if (jmenubarturtle != null) {
            jmenubarturtle.makeFileMenu(this);
        }

    }

    public String getTitle() {
        return frame.getTitle();
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void resetChange() {
        hasChanged = false;
    }

    public void changeMade(TDiagramPanel tdp, int type) {
        hasChanged = true;
        if (tdp != null) {
            switch (type) {
            case -1:
                // Structural change
                break;
            case TDiagramPanel.NEW_COMPONENT:
                // TraceManager.addDev("New Component");
                tdp.structureChanged();
                break;
            case TDiagramPanel.NEW_CONNECTOR:
                // TraceManager.addDev("New Connector");
                tdp.structureChanged();
                break;
            case TDiagramPanel.REMOVE_COMPONENT:
                // TraceManager.addDev("Remove Component");
                tdp.structureChanged();
                break;
            case TDiagramPanel.MOVE_CONNECTOR:
                // TraceManager.addDev("Move Connector");
                tdp.structureChanged();
                break;
            case TDiagramPanel.CHANGE_VALUE_COMPONENT:
                // TraceManager.addDev("Value of component changed");
                tdp.valueChanged();
                break;
            case TDiagramPanel.MOVE_COMPONENT:
                // TraceManager.addDev("Component moved");
                break;
            default:

            }
        }
        setMode(MODEL_CHANGED);
        Point p;
        if (tdp == null) {
            p = getCurrentSelectedPoint();
        } else {
            p = getPoint(tdp);
        }
        // TraceManager.addDev("Change made!");

        // Issue #81: For tests when gtm could be null
        if (gtm != null && type != TDiagramPanel.SELECT_COMPONENT) { // Issue #105
            gtm.saveOperation(p);
        }

        dtree.toBeUpdated();
    }

    public void setMethodologicalMode(byte m) {
        methoMode = m;
        switch (methoMode) {
        case METHO_ANALYSIS:
            break;
        case METHO_DESIGN:
            break;
        default:
        }
    }

    public void setMode(byte m) {
        mode = m;
        ModeManager.setMode(mode, actions, mainBar, this);
    }

    // @author: Huy TRUONG
    public JToolBarMainTurtle getMainBar() {
        return this.mainBar;
    }
    // --

    public void activeActions(boolean b) {
        // TraceManager.addDev("Action actions:" + b);
        for (int i = 0; i < TGUIAction.NB_ACTION; i++) {
            actions[i].setEnabled(b);
        }
    }

    public String getModelFileFullPath() {
        if (file == null) {
            return "./";
        }
        return file.getAbsolutePath();
    }

    public boolean isExperimentalOn() {
        return experimentalOn;
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    public void setHelpManager(HelpManager hm) {
        helpManager = hm;
    }

    public void showIntegratedHelp() {
        if (helpManager != null) {
            openHelpFrame(helpManager);
        }
    }

    public void openHelpFrame(HelpEntry he) {
        if (helpFrame == null) {
            helpFrame = new JFrameHelp("Help", helpManager, he);
            helpFrame.setVisible(true);
            GraphicLib.centerOnParent(helpFrame, 800, 700);
            return;
        }
        helpFrame.setHelpEntry(he);
    }

    public void periodicAction() {
        // TraceManager.addDev("Autosaving ");
        if (file == null) {
            return;
        }

        File fileSave = new File(file.getAbsolutePath() + "~");
        TraceManager.addDev("Autosaving in " + fileSave.getAbsolutePath());
        status.setText("Autosaving in " + fileSave.getAbsolutePath());

        if (checkFileForSave(fileSave)) {
            try {
                String s = gtm.makeXMLFromTurtleModeling(-1);
                FileOutputStream fos = new FileOutputStream(fileSave);
                fos.write(s.getBytes());
                fos.close();
            } catch (Exception e) {
                TraceManager.addDev("Error during autosave: " + e.getMessage());
                status.setText("Error during autosave: " + e.getMessage());
                return;
            }
        }

        status.setText("Autosave done in " + fileSave.getAbsolutePath());
    }

    public void search(String text) {

        Vector<Object> elements = new Vector<Object>();

        TURTLEPanel panel;
        for (int i = 0; i < tabs.size(); i++) {
            panel = tabs.get(i);
            panel.searchForText(text.toLowerCase(), elements);
        }

        elements.addAll(helpManager.getEntriesWithKeyword(text.split(" ")));

        gtm.setElementsOfSearchTree(elements);
        // TraceManager.addDev("Found " + elements.size() + " elements");
        dtree.forceUpdate();
    }

    public List<Invariant> getInvariants() {
        return gtm.getInvariants();
    }

    public List<RG> getRGs() {
        return gtm.getRGs();
    }

    public void addRG(RG _newGraph) {
        gtm.addRG(_newGraph);
        expandToGraphs();
        dtree.toBeUpdated();
    }

    public void removeRG(RG _toBeRemoved) {
        gtm.removeRG(_toBeRemoved);
        if (_toBeRemoved.fileName != null) {
            TraceManager.addDev("Filename=" + _toBeRemoved.fileName);
            File toBeDeleted = new File(_toBeRemoved.fileName);
            try {
                toBeDeleted.delete();
                TraceManager.addDev("File of RG was deleted on disk");
            } catch (Exception e) {

            }
        }
        dtree.toBeUpdated();
    }

    public void minimizeRG(RG toBeMinimized) {
        JFrameMinimize jfm = new JFrameMinimize(frame, this, "Graph minimization", toBeMinimized, SpecConfigTTool.TGraphPath);
        // jfm.setSize(900, 700);
        GraphicLib.centerOnParent(jfm, 900, 700);
        jfm.setVisible(true);
    }

    public void makeRefusalGraph(RG inputGraph) {
        JFrameRefusalGraph jfm = new JFrameRefusalGraph(frame, this, "Test Sequences Construction", inputGraph, SpecConfigTTool.TGraphPath);
        // jfm.setSize(900, 700);
        GraphicLib.centerOnParent(jfm, 900, 700);
        jfm.setVisible(true);
    }

    // Simulation traces
    public List<SimulationTrace> getSimulationTraces() {
        return gtm.getSimulationTraces();
    }

    // Getting the avatar simulation window
    public JFrameAvatarInteractiveSimulation getJFrameAvatarInteractiveSimulation() {
        return jfais;
    }

    public void addSimulationTrace(SimulationTrace _newSimulationTrace) {
        gtm.addSimulationTrace(_newSimulationTrace);
        expandToSimulationTraces();
        dtree.toBeUpdated();
    }

    public void removeSimulationTrace(SimulationTrace _toBeRemoved) {
        gtm.removeSimulationTrace(_toBeRemoved);
        /*
         * if (_toBeRemoved.fileName != null) { TraceManager.addDev("Filename=" +
         * _toBeRemoved.fileName); File toBeDeleted = new File(_toBeRemoved.fileName);
         * try { toBeDeleted.delete();
         * TraceManager.addDev("File of RG was deleted on disk"); } catch (Exception e)
         * {
         * 
         * } }
         */
        dtree.toBeUpdated();
    }

    public void showInFinder(RG inputGraph) {
        TraceManager.addDev("in show in finder");
        if (inputGraph.fileName == null) {
            return;
        }
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        File file = new File(SpecConfigTTool.TGraphPath);
        TraceManager.addDev("Getting desktop");
        Desktop desktop = Desktop.getDesktop();
        try {
            TraceManager.addDev("opening in desktop");
            desktop.open(file);
        } catch (Exception e) {
            TraceManager.addDev("Exception in opening explorer: " + e.getMessage());
        }
    }

    // Can open directory or file
    public void showInFinder(SimulationTrace trace, boolean openDirectory) {
        TraceManager.addDev("in show in finder");
        if (!trace.hasFile()) {
            return;
        }
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        File file = new File(trace.getFullPath());
        if (openDirectory) {
            if (!file.isDirectory()) {
                file = file.getParentFile();
            }
        }
        TraceManager.addDev("Getting desktop");
        Desktop desktop = Desktop.getDesktop();
        try {
            TraceManager.addDev("Opening in desktop");
            desktop.open(file);
        } catch (Exception e) {
            TraceManager.addDev("Exception in opening explorer: " + e.getMessage());
        }
    }

    public void setCurrentInvariant(Invariant inv) {
        currentInvariant = inv;
    }

    public Invariant getCurrentInvariant() {
        return currentInvariant;
    }

    public TGComponent hasCheckableMasterMutex() {
        TURTLEPanel tp = getCurrentTURTLEPanel();

        if (tp instanceof AvatarDesignPanel) {
            return ((AvatarDesignPanel) tp).hasCheckableMasterMutex();
        }

        return null;
    }

    public void removeAllMutualExclusionWithMasterMutex() {
        TURTLEPanel tp = getCurrentTURTLEPanel();

        if (tp instanceof AvatarDesignPanel) {
            ((AvatarDesignPanel) tp).removeAllMutualExclusionWithMasterMutex();
        }

    }

    private int addAnalysisPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AnalysisPanel ap = new AnalysisPanel(this);
        tabs.add(index, ap); // should look for the first
        // mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens
        // analysis diagrams");
        mainTabbedPane.add(ap.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open analysis diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        ap.init();
        // TraceManager.addDev("Main analysis added");
        return index;
    }

    private int addTMLCPPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        TMLCommunicationPatternPanel tmlcpp = new TMLCommunicationPatternPanel(this);
        tabs.add(index, tmlcpp); // should look for the first
        // mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens
        // analysis diagrams");
        mainTabbedPane.add(tmlcpp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open CP diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        tmlcpp.init();
        // TraceManager.addDev("Main analysis added");
        return index;
    }

    private int addAvatarAnalysisPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarAnalysisPanel aap = new AvatarAnalysisPanel(this);
        tabs.add(index, aap); // should look for the first
        // mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens
        // analysis diagrams");
        mainTabbedPane.add(aap.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open analysis diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        aap.init();
        // TraceManager.addDev("Main analysis added");
        return index;
    }

    private int addAttackTreePanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AttackTreePanel atp = new AttackTreePanel(this);
        tabs.add(index, atp); // should look for the first
        mainTabbedPane.add(atp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open attack tree diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1074);
        atp.init();
        return index;
    }

    private int addFaultTreePanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        FaultTreePanel ftp = new FaultTreePanel(this);
        tabs.add(index, ftp); // should look for the first
        mainTabbedPane.add(ftp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open fault tree diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1074);
        ftp.init();
        return index;
    }

    private int addRequirementPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        RequirementPanel rp = new RequirementPanel(this);
        tabs.add(index, rp); // should look for the first
        mainTabbedPane.add(rp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open requirement diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1000);
        rp.init();
        return index;
    }

    public int addAvatarDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarDesignPanel adp = new AvatarDesignPanel(this);
        tabs.add(index, adp);
        mainTabbedPane.add(adp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open AVATAR design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic80);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        adp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addAvatarRequirementPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarRequirementPanel arp = new AvatarRequirementPanel(this);
        tabs.add(index, arp);
        mainTabbedPane.add(arp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open AVATAR requirement diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic82);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        arp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addAvatarMADPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarMADsPanel amadsp = new AvatarMADsPanel(this);
        tabs.add(index, amadsp);
        mainTabbedPane.add(amadsp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open AVATAR Modeling Assumptions diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic82);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        amadsp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        DesignPanel dp = new DesignPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic14);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addTMLDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        TMLDesignPanel dp = new TMLDesignPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open DIPLODOCUS design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic62);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addDiplodocusMethodologyPanel(String name, int index, boolean addDefaultElements) {
        if (index == -1) {
            index = tabs.size();
        }
        DiplodocusMethodologyPanel dp = new DiplodocusMethodologyPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open DIPLODOCUS methodology");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic98);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init(name);
        if (addDefaultElements) {
            dp.initElements();
        }
        // ystem.out.println("Design added");
        return index;
    }

    private int addAvatarMethodologyPanel(String name, int index, boolean addDefaultElements) {
        if (index == -1) {
            index = tabs.size();
        }

        TraceManager.addDev("New avatar methodo panel");
        AvatarMethodologyPanel dp = new AvatarMethodologyPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open AVATAR methodology");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic99);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init(name);
        if (addDefaultElements) {
            dp.initElements();
        }
        // ystem.out.println("Design added");
        return index;
    }

    private int addSysmlsecMethodologyPanel(String name, int index, boolean addDefaultElements) {
        if (index == -1) {
            index = tabs.size();
        }
        TraceManager.addDev("New SysMLSec Methodopanel");
        SysmlsecMethodologyPanel dp = new SysmlsecMethodologyPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open SysML-Sec methodology");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic99);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init(name);
        if (addDefaultElements) {
            dp.initElements();
        }
        // ystem.out.println("Design added");
        return index;
    }

    private int addVerificationPropertyPanel(String name, int index, boolean addDefaultElements) {
        if (index == -1) {
            index = tabs.size();
        }
        // TraceManager.addDev("New SysMLSec Methodopanel");
        VerificationPanel dp = new VerificationPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open Verification Tracking");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic99);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init(name);
        if (addDefaultElements) {
            dp.initElements();
        }
        // ystem.out.println("Design added");
        return index;
    }

    private int addTMLComponentDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        TMLComponentDesignPanel dp = new TMLComponentDesignPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open DIPLODOCUS component design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1208);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addADDPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        ADDPanel dp = new ADDPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open deployment diagram");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic60);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addTMLArchiPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        TMLArchiPanel dp = new TMLArchiPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open DIPLODOCUS architecture diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic60);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    public int addSysCAMSComponentDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        SysCAMSComponentDesignPanel sccdp = new SysCAMSComponentDesignPanel(this);
        tabs.add(index, sccdp);
        mainTabbedPane.add(sccdp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open SystemC-AMS design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1208);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        sccdp.init();
        // ystem.out.println("Design added");
        return index;
    }

    public int addELNDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        ELNDesignPanel elndp = new ELNDesignPanel(this);
        tabs.add(index, elndp);
        mainTabbedPane.add(elndp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open ELN design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic1208);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        elndp.init();
        // ystem.out.println("Design added");
        return index;
    }

    // Return the list of all the TMLArchiDiagramPanels
    public Vector<TMLArchiPanel> getTMLArchiDiagramPanels() {

        Vector<TMLArchiPanel> panelsList = new Vector<TMLArchiPanel>();
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLArchiPanel) {
                panelsList.add((TMLArchiPanel) (tp));
            }
        }
        return panelsList;
    }

    public Vector<String> getAllTMLTaskNames() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel) tp).getAllTMLTaskNames(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

    public void expandToErrors() {
        if (gtm != null) {
            gtm.expandToErrors();
        }
    }

    public void expandToWarnings() {
        if (gtm != null) {
            gtm.expandToWarnings();
        }
    }

    public void expandToGraphs() {
        if (gtm != null) {
            gtm.expandToGraphs();
        }
    }

    public void expandToSimulationTraces() {
        if (gtm != null) {
            gtm.expandToSimulationTraces();
        }
    }

    public Vector<String> getTMLTasks() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel) tp).getAllTMLTaskNames(mainTabbedPane.getTitleAt(i)));
            }
            if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel) tp).getAllTMLTaskNames(mainTabbedPane.getTitleAt(i)));
            }
        }

        return list;
    }

    public Vector<String> getAllApplicationTMLTasksAttributes() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel) tp).getAllTMLTasksAttributes());
            }
        }

        return list;
    }

    public ArrayList<TMLCommunicationPatternPanel> getAllTMLCP() {
        TURTLEPanel tp;
        ArrayList<TMLCommunicationPatternPanel> list = new ArrayList<TMLCommunicationPatternPanel>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLCommunicationPatternPanel) {
                list.add((TMLCommunicationPatternPanel) tp);
            }
        }
        return list;
    }

    public void updateAllReferences() {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel) tp).tmlctdp.delayedLoad();
                // ((TMLComponentDesignPanel)tp).tmlctdp.updatePorts();
            }
        }
    }

    public void updateAllPorts() {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                // ((TMLComponentDesignPanel)tp).tmlctdp.delayedLoad();
                ((TMLComponentDesignPanel) tp).tmlctdp.updatePorts();
            }
        }
    }

    public Vector<String> getAllTMLCommunicationNames() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel) tp).getAllTMLCommunicationNames(mainTabbedPane.getTitleAt(i)));
            } else if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel) tp).getAllTMLCommunicationNames(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

    public Vector<String> getAllTMLInputPorts() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            /*
             * if (tp instanceof TMLDesignPanel) {
             * list.addAll(((TMLDesignPanel)tp).getAllTMLChannelNames(
             * mainTabbedPane.getTitleAt(i)) ); } else
             */
            if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel) tp).getAllTMLInputPorts(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

    public Vector<String> getAllTMLEventNames() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel) tp).getAllTMLEventNames(mainTabbedPane.getTitleAt(i)));
            } else if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel) tp).getAllTMLEventNames(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

    public Vector<String> getAllNonMappedTMLTaskNames(TMLArchiDiagramPanel tadp, String ref, String name) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        boolean b;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((TMLDesignPanel) tp).getAllNonMappedTMLTaskNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
            if (tp instanceof TMLComponentDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((TMLComponentDesignPanel) tp).getAllNonMappedTMLPrimitiveComponentNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
        }

        return list;
    }

    public Vector<String> getAllNonMappedAvatarBlockNames(ADDDiagramPanel tadp, String ref, String name) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        boolean b;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AvatarDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((AvatarDesignPanel) tp).getAllNonMappedAvatarBlockNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
        }

        return list;
    }

    public Vector<String> getAllNonMappedAvatarChannelNames(ADDDiagramPanel tadp, String ref, String name) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        boolean b;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AvatarDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((AvatarDesignPanel) tp).getAllNonMappedAvatarChannelNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
        }

        return list;
    }

    public Vector<String> getAllCompositeComponent(TMLComponentTaskDiagramPanel tcdp) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        // boolean b;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                if (((TMLComponentDesignPanel) tp).tmlctdp != tcdp) {
                    list.addAll(((TMLComponentDesignPanel) tp).getAllCompositeComponent(mainTabbedPane.getTitleAt(i)));
                }
            }
        }

        return list;
    }

    private int addTURTLEOSDesignPanel(String name, int index) {

        if (index == -1) {
            index = tabs.size();
        }
        TURTLEOSDesignPanel dp = new TURTLEOSDesignPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open TURTLE-OS design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic14);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        TraceManager.addDev("TURTLE OS Design added index=" + index);
        return index;
    }

    private int addNCDesignPanel(String name, int index) {

        if (index == -1) {
            index = tabs.size();
        }
        NCPanel ncp = new NCPanel(this);
        tabs.add(index, ncp);
        mainTabbedPane.add(ncp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open network calculus diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic60);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        ncp.init();
        // TraceManager.addDev("TURTLE OS Design added index=" + index);
        return index;
    }

    /*
     * private int addAvatarDesignPanel(String name, int index) {
     * 
     * if (index == -1) { index = tabs.size(); } AvatarDesignPanel avdp = new
     * AvatarDesignPanel(this); tabs.add(index, avdp);
     * mainTabbedPane.add(avdp.tabbedPane, index);
     * mainTabbedPane.setToolTipTextAt(index, "Open AVATAR Design");
     * mainTabbedPane.setTitleAt(index, name); mainTabbedPane.setIconAt(index,
     * IconManager.imgic60); //mainTabbedPane.addTab(name, IconManager.imgic14,
     * dp.tabbedPane, "Opens design diagrams"); avdp.init();
     * //TraceManager.addDev("TURTLE OS Design added index=" + index); return index;
     * }
     */

    /*
     * private int addAvatarRequirementPanel(String name, int index) {
     * 
     * if (index == -1) { index = tabs.size(); } AvatarRequirementPanel arp = new
     * AvatarRequirementPanel(this); tabs.add(index, arp);
     * mainTabbedPane.add(arp.tabbedPane, index);
     * mainTabbedPane.setToolTipTextAt(index, "Open AVATAR Requirement");
     * mainTabbedPane.setTitleAt(index, name); mainTabbedPane.setIconAt(index,
     * IconManager.imgic60); //mainTabbedPane.addTab(name, IconManager.imgic14,
     * dp.tabbedPane, "Opens design diagrams"); arp.init();
     * //TraceManager.addDev("TURTLE OS Design added index=" + index); return index;
     * }
     */

    private int addProActiveDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        ProactiveDesignPanel dp = new ProactiveDesignPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open ProActive design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic14);
        // mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design
        // diagrams");
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    private int addDeploymentPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        DeploymentPanel dp = new DeploymentPanel(this);
        tabs.add(index, dp);
        mainTabbedPane.add(dp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open deployment diagram");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic60);
        dp.init();
        // ystem.out.println("Design added");
        return index;
    }

    public int createAvatarDesign(String name) {
        int index = addAvatarDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAvatarRequirement(String name) {
        int index = addAvatarRequirementPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAvatarMADs(String name) {
        int index = addAvatarMADPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createDesign(String name) {
        int index = addDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createDiplodocusMethodology(String name) {
        int index = addDiplodocusMethodologyPanel(name, -1, false);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAvatarMethodology(String name) {
        int index = addAvatarMethodologyPanel(name, -1, false);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createSysmlsecMethodology(String name) {
        int index = addSysmlsecMethodologyPanel(name, -1, false);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createVerificationProperty(String name) {
        int index = addVerificationPropertyPanel(name, -1, false);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createTMLDesign(String name) {
        int index = addTMLDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createTMLComponentDesign(String name) {
        int index = addTMLComponentDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createTMLCP(String name) {
        int index = addTMLCPPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createTMLArchitecture(String name) {
        int index = addTMLArchiPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createSysCAMSComponentDesign(String name) {
        int index = addSysCAMSComponentDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createELN(String name) {
        int index = addELNDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createADD(String name) {
        int index = addADDPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createTURTLEOSDesign(String name) {
        int index = addTURTLEOSDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createProActiveDesign(String name) {
        int index = addProActiveDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAnalysis(String name) {
        int index = addAnalysisPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAvatarAnalysis(String name) {
        int index = addAvatarAnalysisPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createDeployment(String name) {
        int index = addDeploymentPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createNC(String name) {
        int index = addNCDesignPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createRequirement(String name) {
        int index = addRequirementPanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createAttackTree(String name) {
        int index = addAttackTreePanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public int createFaultTree(String name) {
        int index = addFaultTreePanel(name, -1);
        mainTabbedPane.setSelectedIndex(index);
        return index;
    }

    public void setIODName(int analysisIndex, String name) {
        AnalysisPanel ap = (AnalysisPanel) (tabs.elementAt(analysisIndex));
        ap.tabbedPane.setTitleAt(0, name);
    }

    public void setDeploymentName(int deploymentIndex, String name) {
        DeploymentPanel dp = (DeploymentPanel) (tabs.elementAt(deploymentIndex));
        dp.tabbedPane.setTitleAt(0, name);
    }

    public void setNCName(int ncIndex, String name) {
        NCPanel ncp = (NCPanel) (tabs.elementAt(ncIndex));
        ncp.tabbedPane.setTitleAt(0, name);
    }

    // add main panel for editing TURTLE diagrams
    private void addTURTLEPanel() {

        // TraceManager.addDev("New TURTLE Panels");
        // Issue #41 Ordering of tabbed panes
        mainTabbedPane = GraphicLib.createDraggableEnhancedTabbedPane(this);// new JTabbedPane();
        mainTabbedPane.setBackground(ColorManager.MainTabbedPane);
        mainTabbedPane.setForeground(Color.black);

        BACK_COLOR = UIManager.get("TabbedPane.selected");
        // UIManager.put("TabbedPane.tabAreaBackground",
        // ColorManager.MainTabbedPaneBack);
        UIManager.put("TabbedPane.selected", ColorManager.MainTabbedPaneSelect);
        // UIManager.put("TabbedPane.darkShadow", Color.black);
        UIManager.put("TabbedPane.focus", Color.blue);
        /*
         * UIManager.put("TabbedPane.highlight", Color.blue);
         * UIManager.put("TabbedPane.lightHighlight", Color.red);
         * UIManager.put("TabbedPane.shadow", Color.black);
         * UIManager.put("TabbedPane.darkShadow", Color.magenta);
         * UIManager.put("TabbedPane.focus", Color.green);
         */
        SwingUtilities.updateComponentTreeUI(mainTabbedPane);
        mainTabbedPane.setOpaque(true);

        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // TraceManager.addDev("State Changed in main tabbed pane");
                ModelParameters.setValueForID("LAST_SELECTED_MAIN_TAB", "" + mainTabbedPane.getSelectedIndex());
                paneAction(e);
            }
        };

        mainTabbedPane.addChangeListener(cl);
        panelForTab.add(mainTabbedPane, BorderLayout.CENTER);
        mainTabbedPane.addMouseListener(new PopupListener(this));

        tabs = new Vector<TURTLEPanel>();

        frame.setVisible(true);
    }

    public Vector<TURTLEPanel> getTabs() {
        return tabs;
    }

    public void firstDiag() {

        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return;
        }
        tp.tabbedPane.setSelectedIndex(0);
    }

    public String getTitleOf(TDiagramPanel _tdp) {
        TURTLEPanel panel;
        for (int i = 0; i < tabs.size(); i++) {
            panel = tabs.get(i);
            if (panel.hasTDiagramPanel(_tdp)) {
                return getTitleAt(panel);
            }
        }

        return "Unknown";

    }

    public String getTitleAt(TURTLEPanel tp) {
        int index = tabs.indexOf(tp);
        if (index == -1) {
            return "Unknown";
        }
        return mainTabbedPane.getTitleAt(index);

    }

    public TURTLEPanel getTURTLEPanel(String s) {
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            if (mainTabbedPane.getTitleAt(i).compareTo(s) == 0) {
                return tabs.elementAt(i);
            }
        }
        return null;
    }

    public TURTLEPanel getTURTLEPanel(int index) {
        if (index >= tabs.size()) {
            return null;
        }
        return tabs.get(index);
    }

    public void drawAvatarSpecification(AvatarSpecification av) {
        DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
        Date date = new Date();
        String dateAndTime = dateFormat.format(date);
        String tabName = "GeneratedDesign_" + dateAndTime;
        TraceManager.addDev("Draw Spec 1");
        int index = createAvatarDesign(tabName);
        TraceManager.addDev("Draw Spec 2");
        AvatarDesignPanel adp = (AvatarDesignPanel) (tabs.elementAt(index));
        TraceManager.addDev("Draw Spec 3");
        gtm.drawPanel(av, adp);
        TraceManager.addDev("Draw Spec 4");
    }

    // Creates the status bar.
    private JLabel createStatusBar() {
        status = new JLabel("Ready...");
        status.setBorder(BorderFactory.createEtchedBorder());
        return status;
    }

    public void activate(JMenu jm, boolean b) {
        JMenuItem im;
        for (int i = 0; i < jm.getItemCount(); i++) {
            im = jm.getItem(i);
            if (im != null)
                im.setEnabled(b);
        }
    }

    public void start() {
        start(true);
    }

    public void start(boolean show) {
        // Main window is ready to be drawn on screen
        if (frame == null) {
            ErrorGUI.exit(ErrorGUI.GUI);
        }

        boolean positioned = false;

        if (ConfigurationTTool.configSizeAvailable()) {
            try {
                int x = Integer.decode(ConfigurationTTool.LastWindowAttributesX).intValue();
                int y = Integer.decode(ConfigurationTTool.LastWindowAttributesY).intValue();
                int width = Integer.decode(ConfigurationTTool.LastWindowAttributesWidth).intValue();
                int height = Integer.decode(ConfigurationTTool.LastWindowAttributesHeight).intValue();
                String max = ConfigurationTTool.LastWindowAttributesMax;
                if (max.compareTo("true") != 0) {
                    frame.setBounds(x, y, width, height);
                    positioned = true;
                }
                // TraceManager.addDev("Setting window attributes");
            } catch (Exception e) {
                // TraceManager.addDev("Window positioning has failed: " + e.getMessage());
            }
        }

        if (!positioned) {
            frame.setBounds(100, 100, 800, 600);
            // jdk 1.4 or more
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            // TraceManager.addDev("No default window attributes");
        }

        frame.setVisible(show);

        // split1.setDividerLocation(0.90);
        // split.setDividerLocation(0.2);

    }

    public void newTurtleModeling() {
        setMode(OPENED);
        addTURTLEPanel();
        gtm = new GTURTLEModeling(this, tabs);
        dtm = new DiagramTreeModel(this);
        dtree.setModel(dtm);
    }

    public void closeTurtleModeling() {
        if (mode != NOT_OPENED) {
            setMode(NOT_OPENED);

            // tabbed pane
            for (int i = 0; i < tabs.size(); i++) {
                tabs.elementAt(i).tabbedPane.removeAll();
            }

            tabs = null;
            mainTabbedPane = null;
            panelForTab.removeAll();
            activetdp = null;

            gtm = null;
            tclassesToValidate = new LinkedList<TClassInterface>();
            MasterGateManager.reinitNameRestriction();

            typeButtonSelected = -1;
            idButtonSelected = -1;
            pluginSelected = null;

            // activeDiagramToolBar = null;

            dtree.reinit();
            dtree.forceUpdate();
            frame.setTitle("TTool");
            frame.repaint();
        }
    }

    public void setStatusBarText(String s) {
        // captitalizeFirstLetter
        if (s == null) {
            return;
        }

        if (s.length() == 0) {
            return;
        }

        if (s.length() > 1) {
            status.setText(s.substring(0, 1).toUpperCase() + s.substring(1, s.length()));
        } else {
            status.setText(s);
        }
    }

    public void reinitMainTabbedPane() {
        mainTabbedPane.removeAll();
        tabs.removeAllElements();
    }

    public void newDesign() {
        // TraceManager.addDev("NEW DESIGN");
        addDesignPanel("Design", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newDeployment() {
        // TraceManager.addDev("NEW DESIGN");
        addDeploymentPanel("Deployment", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newAnalysis() {
        // TraceManager.addDev("NEW ANALYSIS");
        addAnalysisPanel("Analysis", -1);
        tabs.elementAt(0).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        // paneAction(null);
        // frame.repaint();
    }

    public void newAvatarAnalysis() {
        // TraceManager.addDev("NEW ANALYSIS");
        addAvatarAnalysisPanel("Analysis", -1);
        // ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newCommunicationPattern() {
        // TraceManager.addDev("NEW ANALYSIS");
        // addCommunicationPatternPanel("CP", 0);
        tabs.elementAt(0).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newTMLDesign() {
        // TraceManager.addDev("NEW DESIGN");
        addTMLDesignPanel("DIPLODOCUS_Design", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newDiplodocusMethodology() {
        // TraceManager.addDev("NEW DESIGN");
        addDiplodocusMethodologyPanel("DIPLODOCUS_Methodology", -1, true);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newAvatarMethodology() {
        // TraceManager.addDev("NEW DESIGN");
        addAvatarMethodologyPanel("AVATAR_Methodology", -1, true);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newSysmlsecMethodology() {
        // TraceManager.addDev("NEW DESIGN");
        addSysmlsecMethodologyPanel("SysMLSec_Methodology", -1, true);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newVerificationPropertyPanel() {
        // TraceManager.addDev("NEW DESIGN");
        addVerificationPropertyPanel("Verification Tracking", -1, true);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newTMLComponentDesign() {
        // TraceManager.addDev("NEW DESIGN");
        addTMLComponentDesignPanel("Application", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newTMLCP() {
        // TraceManager.addDev("NEW ANALYSIS");
        addTMLCPPanel("CP", -1);
        tabs.elementAt(0).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newTMLArchi() {
        // TraceManager.addDev("NEW DIPLO Architecture");
        addTMLArchiPanel("Architecture", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newSysCAMS() {
        // TraceManager.addDev("NEW DESIGN");
        addSysCAMSComponentDesignPanel("SystemC_AMS", -1);
        // tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newELN() {
        // TraceManager.addDev("NEW DESIGN");
        addELNDesignPanel("ELN", -1);
//    	tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newADD() {
        // TraceManager.addDev("NEW Avatar deployment");
        addADDPanel("Deployment", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newTURTLEOSDesign() {
        // TraceManager.addDev("NEW DESIGN");
        addTURTLEOSDesignPanel("TURTLE-OS Design", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
    }

    public void newNCDesign() {
        // TraceManager.addDev("NEW NC DESIGN");
        addNCDesignPanel("NC Design", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
    }

    public void newAvatarBD() {
        // TraceManager.addDev("NEW AVATAR BD");
        addAvatarDesignPanel("Design", -1);
        tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
    }

    public void newAvatarRequirement() {
        // TraceManager.addDev("NEW AVATAR Requirement");
        addAvatarRequirementPanel("Requirements", -1);
        // ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        // mainTabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
    }

    public void newAvatarMADs() {
        // TraceManager.addDev("NEW AVATAR MAD");
        addAvatarMADPanel("Assumptions", -1);
        // ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        // mainTabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
    }

    public void newProactiveDesign() {
        // TraceManager.addDev("NEW DESIGN");
        /* int index = */
        addProActiveDesignPanel("ProActive Design", -1);
        // tabs.elementAt(tabs.size() - 1).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();*/
    }

    public void newAttackTree() {
        // TraceManager.addDev("NEW ANALYSIS");
        addAttackTreePanel("Attack Trees", -1);
        // ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newFaultTree() {
        // TraceManager.addDev("NEW ANALYSIS");
        addFaultTreePanel("Fault Trees", -1);
        // ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    public void newRequirement() {
        // TraceManager.addDev("NEW ANALYSIS");
        addRequirementPanel("Requirements", -1);
        // ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        // mainTabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size() - 1);
        // paneAction(null);
        // frame.repaint();
    }

    // Action listeners
    public void newProject() {
        if (mode == NOT_OPENED) {
            // mode = ProjectManager.TM_OPENED;
            newTurtleModeling();
            // gtm.saveOperation(tcdp);
            file = null;
            dir = null;
            config = null;
            frame.setTitle("TTool: unsaved project");
        } else {
            // check if previous modeling is saved
            boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
            if (b) {
                if (!saveBeforeAction("Save and Start New Modeling", "Start New modeling")) {
                    return;
                }
                /*
                 * int back = JOptionPane.showConfirmDialog(frame,
                 * "Modeling has not been saved\nDo you really want to open a new one ?",
                 * "Attention: current modeling not saved ?", JOptionPane.OK_CANCEL_OPTION); if
                 * (back == JOptionPane.CANCEL_OPTION) { return;
                 */
                /* } */
            }
            // close current modeling
            closeTurtleModeling();

            // opens a new one
            newTurtleModeling();

            // gtm.saveOperation(tcdp);

            file = null;
            lotosfile = null;
            simfile = null;
            dtafile = null;
            dtadotfile = null;
            rgfile = null;
            tlsafile = null;
            rgdotfile = null;
            tlsadotfile = null;
            rgautfile = null;
            fc2file = null;
            bcgfile = null;
            rgautdotfile = null;
            rgautprojfile = null;
            rgautprojdotfile = null;

            frame.setTitle("TTool: unsaved project");
        }
    }

    public void newProjectDir() {
        if (mode == NOT_OPENED) {
            int val = createFileDialog();
            if (val == JFileChooser.APPROVE_OPTION)
                createFile();
        } else {
            // check if previous modeling is saved
            boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
            if (b) {
                if (!saveBeforeAction("Save and Start New Modeling", "Start New modeling")) {
                    return;
                }
                /*
                 * int back = JOptionPane.showConfirmDialog(frame,
                 * "Modeling has not been saved\nDo you really want to open a new one ?",
                 * "Attention: current modeling not saved ?", JOptionPane.OK_CANCEL_OPTION); if
                 * (back == JOptionPane.CANCEL_OPTION) { return;
                 */
                /* } */
            }
            int val = createFileDialog();
            if (val == JFileChooser.APPROVE_OPTION) {
                // close current modeling
                closeTurtleModeling();
                createFile();
            }
        }
    }

    public void saveConfig() {
        int i = 0;
        for (; i < tabs.size(); i++) {
            if (tabs.get(i) == activetdp.tp)
                break;
        }
        int j = tabs.get(i).getIndexOfChild(activetdp);
        SpecConfigTTool.lastTab = i;
        SpecConfigTTool.lastPanel = j;
        try {
            SpecConfigTTool.saveConfiguration(config);
            SpecConfigTTool.lastPanel = -1;
            SpecConfigTTool.lastTab = -1;
        } catch (MalformedConfigurationException e) {
            System.err.println(e.getMessage() + " : Can't save config file.");
        }
    }

    public String loadFile(File f) {
        String s = null;

        if (checkFileForOpen(f)) {
            try {
                FileInputStream fis = new FileInputStream(f);
                int nb = fis.available();

                byte[] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (OutOfMemoryError er) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + er.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return s;
    }

    public void saveFile(File f, String data, String msg) {
        if (checkFileForSave(f)) {
            try {
                if (data == null) {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data.getBytes());
                fos.close();
                JOptionPane.showMessageDialog(frame, msg + " " + f.getAbsolutePath(), "Saving", JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public String loadLibrary() {
        File libfile;

        int returnVal = jfclib.showDialog(frame, "Import library");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        libfile = jfclib.getSelectedFile();
        libfile = FileUtils.addFileExtensionIfMissing(libfile, TLibFilter.getExtension());

        return loadFile(libfile);
    }

    public String[] loadLotosSpec() {
        File lotfile;

        int returnVal = jfclot.showDialog(frame, "Load RT-LOTOS specification");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        lotfile = jfclot.getSelectedFile();
        lotfile = FileUtils.addFileExtensionIfMissing(lotfile, RTLFileFilter.getExtension());

        String spec = loadFile(lotfile);
        if (spec == null) {
            return null;
        }

        String[] ret = new String[2];
        ret[0] = lotfile.getName();
        ret[1] = spec;
        return ret;
    }

    public void saveLotosSpec(String data) {
        File lotfile;

        int returnVal = jfclot.showDialog(frame, "Save RT-LOTOS specification");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        lotfile = jfclot.getSelectedFile();
        lotfile = FileUtils.addFileExtensionIfMissing(lotfile, RTLFileFilter.getExtension());

        saveFile(lotfile, data, "LOTOS specification saved under");
    }

    public void saveDTA(String tdata, String gdata) {
        File dtafile;

        /* textual form */
        DTAFileFilter filter = new DTAFileFilter();
        jfctgraph.setFileFilter(filter);
        jfctgraph.setCurrentDirectory(new File(SpecConfigTTool.TGraphPath));

        int returnVal = jfctgraph.showDialog(frame, "Save last DTA (textual form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        dtafile = jfctgraph.getSelectedFile();
        dtafile = FileUtils.addFileExtensionIfMissing(dtafile, DTAFileFilter.getExtension());

        saveFile(dtafile, tdata, "Textual DTA saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last DTA (graphical form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        dtafile = jfcggraph.getSelectedFile();
        dtafile = FileUtils.addFileExtensionIfMissing(dtafile, TDotFilter.getExtension());

        saveFile(dtafile, gdata, "Graphical DTA saved under");
    }

    public String saveTPNNDRFormat(String tpn) {
        String s = file.getAbsolutePath();
        int l = s.length();
        String myFile = s.substring(0, l - 4) + ".ndr";
        try {
            FileUtils.saveFile(myFile, tpn);
        } catch (Exception e) {
            return "TPN could not be saved in myFile: " + e.getMessage();
        }
        return "TPN saved in " + myFile;
    }

    public void saveRG(String tdata, String gdata) {
        File rgfile;

        /* textual form */
        RGFileFilter filter = new RGFileFilter();
        jfctgraph.setFileFilter(filter);
        jfctgraph.setCurrentDirectory(new File(SpecConfigTTool.TGraphPath));

        int returnVal = jfctgraph.showDialog(frame, "Save last RG (textual form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfctgraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, RGFileFilter.getExtension());

        saveFile(rgfile, tdata, "Textual RG saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last RG (graphical form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfcggraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, TDotFilter.getExtension());

        saveFile(rgfile, gdata, "Graphical RG saved under");
    }

    public void saveTLSA(String tdata, String gdata) {
        File tlsafile;

        /* textual form */
        TLSAFileFilter filter = new TLSAFileFilter();
        jfctgraph.setFileFilter(filter);

        int returnVal = jfctgraph.showDialog(frame, "Save last TLSA (textual form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        tlsafile = jfctgraph.getSelectedFile();
        tlsafile = FileUtils.addFileExtensionIfMissing(tlsafile, TLSAFileFilter.getExtension());

        saveFile(tlsafile, tdata, "Textual TLSA saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last TLSA (graphical form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        tlsafile = jfcggraph.getSelectedFile();
        tlsafile = FileUtils.addFileExtensionIfMissing(tlsafile, TDotFilter.getExtension());

        saveFile(tlsafile, gdata, "Graphical TLSA saved under");
    }

    public void saveRGAUT(String tdata, String gdata) {
        File rgfile;

        /* textual form */
        AUTFileFilter filter = new AUTFileFilter();
        jfctgraph.setFileFilter(filter);

        int returnVal = jfctgraph.showDialog(frame, "Save last RG/AUT (textual form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfctgraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, AUTFileFilter.getExtension());

        saveFile(rgfile, tdata, "Textual RG/AUTsaved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last RG/AUT (graphical form)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfcggraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, TDotFilter.getExtension());

        saveFile(rgfile, gdata, "Graphical RG/AUT saved under");
    }

    public String[] loadGGraph() {
        File gfile;

        int returnVal = jfcggraph.showDialog(frame, "Load Graph (DOT format)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        gfile = jfcggraph.getSelectedFile();
        gfile = FileUtils.addFileExtensionIfMissing(gfile, TDotFilter.getExtension());

        String spec = loadFile(gfile);
        if (spec == null) {
            return null;
        }

        String[] ret = new String[2];
        ret[0] = gfile.getName();
        ret[1] = spec;
        return ret;
    }

    public String[] loadGGraph(String name) {
        try {
            String spec = FileUtils.loadFile(name);
            if (spec == null) {
                return null;
            }

            String[] ret = new String[2];
            ret[0] = name;
            ret[1] = spec;
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    public String[] loadAUTGraph() {
        File autfile;

        /* textual form */
        AUTFileFilter filter = new AUTFileFilter();
        jfctgraph.setFileFilter(filter);
        jfctgraph.setCurrentDirectory(new File(SpecConfigTTool.TGraphPath));

        int returnVal = jfctgraph.showDialog(frame, "Load AUT graph");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        autfile = jfctgraph.getSelectedFile();
        autfile = FileUtils.addFileExtensionIfMissing(autfile, AUTFileFilter.getExtension());

        String spec = loadFile(autfile);
        if (spec == null) {
            return null;
        }

        String[] ret = new String[2];
        ret[0] = autfile.getName();
        ret[1] = spec;
        return ret;
    }

    public String[] loadSimulationTraceCSV() {
        File file;

        /* textual form */
        CSVFileFilter filter = new CSVFileFilter();
        jfctgraph.setFileFilter(filter);
        jfctgraph.setCurrentDirectory(new File(SpecConfigTTool.TGraphPath));

        int returnVal = jfctgraph.showDialog(frame, "Load simulation trace (CSV format)");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        file = jfctgraph.getSelectedFile();
        return loadSimulationTraceCSVFile(file);
    }

    public String[] loadSimulationTraceCSVFile(File file) {
        file = FileUtils.addFileExtensionIfMissing(file, CSVFileFilter.getExtension());

        String spec = loadFile(file);
        if (spec == null) {
            return null;
        }

        String[] ret = new String[3];
        ret[0] = file.getName();
        ret[1] = file.getAbsolutePath();
        ret[2] = spec;
        return ret;
    }

    public void loadAUTGraphsDir() {
        File dir = new File(SpecConfigTTool.TGraphPath);
        if (!dir.exists()) {
            System.err.println("Graphs directory doesn't exists !");
            return;
        }

        for (File autfile : dir.listFiles()) {
            if (!FileUtils.getExtension(autfile).equals("aut"))
                continue;

            String spec = loadFile(autfile);
            if (spec == null) {
                continue;
            }

            RG rg = new RG(autfile.getName());
            rg.fileName = autfile.getAbsolutePath();
            rg.data = spec;
            addRG(rg);
        }

    }

    public void updateLastOpenFile(File file) {
        if (ConfigurationTTool.LastOpenFileDefined) {

            ConfigurationTTool.LastOpenFile = file.getPath();
            if (ConfigurationTTool.LastOpenFile.contains(".ttool" + File.separator)) {
                int last = 0;
                for (int i = 0; i < ConfigurationTTool.LastOpenFile.length(); i++) {
                    if (ConfigurationTTool.LastOpenFile.charAt(i) == '/')
                        last = i;
                }
                ConfigurationTTool.LastOpenFile = ConfigurationTTool.LastOpenFile.substring(0, last);
            }
            // Change name of action
            actions[TGUIAction.ACT_OPEN_LAST].setName(TGUIAction.ACT_OPEN_LAST, ConfigurationTTool.LastOpenFile);

            // is the new name already in the list of opened files?
            for (int i = 0; i < ConfigurationTTool.LastOpenFiles.length; i++) {
                if (ConfigurationTTool.LastOpenFiles[i].compareTo(ConfigurationTTool.LastOpenFile) == 0) {
                    ConfigurationTTool.LastOpenFiles[i] = "";
                }
            }

            // Dec all files
            ConfigurationTTool.decLastFiles();
            ConfigurationTTool.LastOpenFiles[0] = ConfigurationTTool.LastOpenFile;

            // We need to update the actions
            for (int j = 0; j < actionsLast.length; j++) {
                actionsLast[j] = new TGUIAction(TGUIAction.ACT_OPEN_LAST, "Open recent: " + ConfigurationTTool.LastOpenFiles[j]);
                actionsLast[j].addActionListener(this);
                // actions[i].addKeyListener(this);
            }

            if (jmenubarturtle != null) {
                jmenubarturtle.makeFileMenu(this);
            }

        }
    }

    // Only if a project is already opened
    public void mergeProject() {
        // TraceManager.addDev("Merge");
        File filetmp = file;
        int returnVal = jfc.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filetmp = jfc.getSelectedFile();
        }

        String s = null;
        String oldmodeling = gtm.makeXMLFromTurtleModeling(-1);
        if (checkFileForOpen(filetmp)) {
            try {
                FileInputStream fis = new FileInputStream(filetmp);
                int nb = fis.available();

                byte[] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba, "UTF-8");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // close current modeling
            closeTurtleModeling();

            // open the new TURTLE modeling
            newTurtleModeling();

            loadModels(gtm.mergeTURTLEGModeling(oldmodeling, s), "merged");

        }
    }

    public void openNetworkProject() {
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b) {
            if (!saveBeforeAction("Save and Open", "Open")) {
                return;
            }
            /*
             * int back = JOptionPane.showConfirmDialog(frame,
             * "Current modeling has not been saved\nDo you really want to open a new one ?"
             * , "To quit, or not to quit ?", JOptionPane.OK_CANCEL_OPTION); if (back ==
             * JOptionPane.CANCEL_OPTION) { return; }
             */
        }

        JDialogLoadingNetworkModel jdlnm = new JDialogLoadingNetworkModel(frame, this, "Opening a network model", ConfigurationTTool.URL_MODEL);
        GraphicLib.centerOnParent(jdlnm, 700, 800);
        jdlnm.setVisible(true); // blocked until dialog has been closed
    }

    public void openProject(boolean isProject) {
        if (config != null && activetdp != null)
            saveConfig();
        // check if a current modeling is opened
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();

        if (b) {
            if (!saveBeforeAction("Save and Open", "Open")) {
                return;
            }
            /*
             * int back = JOptionPane.showConfirmDialog(frame,
             * "Current modeling has not been saved\nDo you really want to open a new one ?"
             * , "To quit, or not to quit ?", JOptionPane.OK_CANCEL_OPTION); if (back ==
             * JOptionPane.CANCEL_OPTION) { return; }
             */
        }

        // jfc.setApproveButtonText("Open");
        if (isProject) {
            jfc.resetChoosableFileFilters();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TTool project", "ttool");
            jfc.setFileFilter(filter);
        } else {
            jfc.resetChoosableFileFilters();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
            jfc.setFileFilter(filter);
        }
        int returnVal = jfc.showOpenDialog(frame);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            return;
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File tmpFile = jfc.getSelectedFile();
            openProjectFromFile(tmpFile);
        }

    }

    public void openProjectFromFile(File _f) {
        String data = null;
        File tmpFile;

        if (FileUtils.getExtension(_f).equals("ttool")) {
            File tmpDir = _f;
            // SpecConfigTTool.setDirConfig(dir);
            String filename = tmpDir.getAbsolutePath() + "/" + tmpDir.getName().replaceAll(".ttool", ".xml");
            tmpFile = new File(filename);
        } else {
            tmpFile = _f;
        }

        if (!checkFileForOpen(tmpFile)) {
            JOptionPane.showMessageDialog(frame, "File " + tmpFile.getAbsolutePath() + " could not be opened ", "File Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(tmpFile);
            int nb = fis.available();

            byte[] ba = new byte[nb];
            fis.read(ba);
            fis.close();
            data = new String(ba, "UTF-8");
            // TraceManager.addDev("Mode:" + s);
        } catch (Exception e) {
            // TraceManager.addDev("Open file error");
            JOptionPane.showMessageDialog(frame, "File " + tmpFile.getAbsolutePath() + " could not be opened", "File Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (FileUtils.getExtension(_f).equals("ttool")) {
            File tmpDir = _f;
            config = new File(tmpDir.getAbsolutePath() + "/project_config.xml");
            try {
                SpecConfigTTool.loadConfigFile(config);
            } catch (MalformedConfigurationException e) {
                System.err.println(e.getMessage() + " : Can't load config file.");
            }
            SpecConfigTTool.setDirConfig(tmpDir);
        } else {
            SpecConfigTTool.setBasicConfig(systemcOn);
        }

        file = tmpFile;

        // close current modeling
        closeTurtleModeling();

        // open the new TURTLE modeling
        newTurtleModeling();

        // gtm.enableUndo(false);

        // Update configuration
        updateLastOpenFile(file);

        // Issue #41: Moved to common method
        loadModels(data, "loaded");
        // // load the new TURTLE modeling
        // try {
        // gtm.loadModelingFromXML(s);
        // //gtm.saveOperation(tcdp);
        // frame.setTitle("TTool: " + file.getAbsolutePath());
        // makeLotosFile();
        //
        // if (gtm.getCheckingErrors().size() > 0) {
        // JOptionPane.showMessageDialog(frame, "Modeling could not be correctly
        // loaded", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
        //
        // }
        // } catch (MalformedModelingException mme) {
        // JOptionPane.showMessageDialog(frame, "Modeling could not be correctly
        // loaded", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
        // frame.setTitle("TToolt: unamed project");
        // }
        //
        // gtm.enableUndo(true);
        // gtm.saveOperation(getCurrentSelectedPoint());
        // dtree.forceUpdate();
        if (getCurrentTDiagramPanel() != null)
            getCurrentTDiagramPanel().repaint();

    }

    public void openLastProject() {
        openLastProject(0);
    }

    public void openLastProject(int id) {

        // Check if a current modeling is opened
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b) {
            if (!saveBeforeAction("Save and Open", "Open")) {
                return;
            }
        }

        file = new File(ConfigurationTTool.LastOpenFiles[id]);
        // TraceManager.addDev("Opening project #" + id + " for file=" +
        // file.getAbsolutePath());

        if (checkFileForOpen(file)) {
            String s = null;

            if (FileUtils.getExtension(file).equals("ttool")) {
                openProjectFromFile(file);
                return;
                /*
                 * TraceManager.addDev("this is a ttool project"); int last = 0; for (int i = 0;
                 * i < ConfigurationTTool.LastOpenFile.length(); i++) { if
                 * (ConfigurationTTool.LastOpenFile.charAt(i) == '/') last = i; } dir = file;
                 * String xml = ConfigurationTTool.LastOpenFile.substring(last,
                 * ConfigurationTTool.LastOpenFile.length()).replaceAll(".ttool", ".xml"); file
                 * = new File(dir.getAbsolutePath() + File.separator + xml);
                 * SpecConfigTTool.setDirConfig(dir); config = new File(dir.getAbsolutePath() +
                 * "/project_config.xml"); try { SpecConfigTTool.loadConfigFile(config); } catch
                 * (MalformedConfigurationException e) { System.err.println(e.getMessage() +
                 * " : Can't load config file."); }
                 */
            } else {
                dir = null;
                config = null;
                SpecConfigTTool.setBasicConfig(systemcOn);
                try {
                    FileInputStream fis = new FileInputStream(file);
                    int nb = fis.available();

                    byte[] ba = new byte[nb];
                    fis.read(ba);
                    fis.close();
                    s = new String(ba, "UTF-8");
                    // TraceManager.addDev("Model:" + s);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // close current modeling
            closeTurtleModeling();

            // open the new TURTLE modeling
            newTurtleModeling();

            gtm.enableUndo(false);

            // Issue #41: Moved to common method
            updateLastOpenFile(file);
            loadModels(s, "loaded");

        }

    }

    private void loadModels(final String xmlModel, final String actionMessage) {
        gtm.enableUndo(false);

        TraceManager.addDev("Loading model");

        // load the new TTool modeling
        try {
            gtm.loadModelingFromXML(xmlModel);
            frame.setTitle("TTool: " + file.getAbsolutePath());
            makeLotosFile();

            // Issue #41: Reselect the last tab
            // mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);

            if (gtm.getCheckingErrors().size() > 0) {
                JOptionPane.showMessageDialog(frame, "Model " + file.getAbsolutePath() + " could not be correctly " + actionMessage,
                        "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);

            }
        } catch (Exception mme) {
            JOptionPane.showMessageDialog(frame, "Model " + file.getAbsolutePath() + " could not be correctly " + actionMessage,
                    "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
            frame.setTitle("TTool: unnamed project");
        }

        gtm.enableUndo(true);
        gtm.saveOperation(getCurrentSelectedPoint());
        dtree.forceUpdate();
        if (SpecConfigTTool.lastTab > -1 && SpecConfigTTool.lastPanel > -1 && mainTabbedPane.getTabCount() > 0) {
            if (SpecConfigTTool.lastTab < mainTabbedPane.getTabCount()) {
                mainTabbedPane.setSelectedIndex(SpecConfigTTool.lastTab);
                activetdp = tabs.get(SpecConfigTTool.lastTab).getPanels().elementAt(SpecConfigTTool.lastPanel);
                activetdp.selectTab(activetdp.name);
                basicActivateDrawing();
            }
        }

        if (dir != null)
            loadAUTGraphsDir();
    }

    public void saveAsLibrary(String data) {
        File libFile;

        int returnVal = jfclib.showDialog(frame, "Export library");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        libFile = jfclib.getSelectedFile();
        libFile = FileUtils.addFileExtensionIfMissing(libFile, TLibFilter.getExtension());

        if (checkFileForSave(libFile)) {
            try {
                if (data == null) {
                    throw new Exception("Selected data corrupted");
                }
                FileOutputStream fos = new FileOutputStream(libFile);
                fos.write(data.getBytes());
                fos.close();
                JOptionPane.showMessageDialog(frame, "Modeling was correctly saved under a TTool library named " + libFile.getName(), "Saving",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public void saveAsProject() {
        File old = file;
        if (file == null) {
            // project never saved
            saveProject();
        } else {
            file = null;
            if (!saveProject()) {
                file = old;
            }
        }
        if (file != old) {
            // frame.setTitle("TURTLE Toolkit: " + file.getAbsolutePath());
            makeLotosFile();
        }
    }

    public boolean saveTIF() {
        int returnVal = jfctif.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfctif.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TTIFFilter.getExtension());
        }

        if (checkFileForSave(file)) {
            String s = gtm.saveTIF();
            try {

                if (s == null) {
                    throw new Exception("TIF specification is void");

                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(s.getBytes());
                fos.close();

                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return false;
    }

    public boolean openTIF() {
        // jfc.setApproveButtonText("Open");
        int returnVal = jfctif.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfctif.getSelectedFile();
        }

        String s = null;
        if (checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte[] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        if (s == null) {
            return false;
        }
        TraceManager.addDev("Open TIF =" + s);
        if (gtm == null) {
            newTurtleModeling();
        }
        return gtm.openTIF(s);

    }

    public boolean openSD() {
        // jfc.setApproveButtonText("Open");
        int returnVal = jfcmsc.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcmsc.getSelectedFile();
        }

        String s = null;
        if (checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte[] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        if (s == null) {
            return false;
        }
        TraceManager.addDev("Open SD =" + s);
        if (gtm == null) {
            newTurtleModeling();
        }
        return gtm.openSD(s);

    }

    private int createFileDialog() {
        int returnVal = jfc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dir = jfc.getSelectedFile();
            dir = FileUtils.addFileExtensionIfMissing(dir, "ttool");
            dir.mkdir();
            SpecConfigTTool.setDirConfig(dir);
            config = SpecConfigTTool.createProjectConfig(dir);
            try {
                SpecConfigTTool.loadConfigFile(config);
            } catch (MalformedConfigurationException e) {
                System.err.println(e.getMessage() + " : Can't load config file.");
            }
            String newname = FileUtils.removeFileExtension(dir.getName());
            file = new File(dir, newname);
            file = FileUtils.addFileExtensionIfMissing(file, TFileFilter.getExtension());
        }

        return returnVal;
    }

    private void createFile() {
        newTurtleModeling();
        frame.setTitle(file.getName());
        try {
            if (gtm == null) {
                throw new Exception("Internal model Error 1");
            }
            String s = gtm.makeXMLFromTurtleModeling(-1);
            if (s == null) {
                throw new Exception("Internal model Error 2");
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(s.getBytes());
            fos.close();
            updateLastOpenFile(file);
            setMode(MODEL_SAVED);
            String title = "TTool: " + file.getAbsolutePath();
            if (!frame.getTitle().equals(title)) {
                frame.setTitle(title);
            }
            if (lotosfile == null) {
                makeLotosFile();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected boolean saveAsNewProject() {
        jfc.resetChoosableFileFilters();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TTool project", "ttool");
        jfc.setFileFilter(filter);

        createFileDialog();

        boolean b = mainSave();

        if (b) {
            updateLastOpenFile(file);
        }

        return b;

    }

    protected boolean saveAsNewModel() {
        jfc.resetChoosableFileFilters();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
        jfc.setFileFilter(filter);
        int returnVal = jfc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TFileFilter.getExtension());
        }

        boolean b = mainSave();
        if (b) {
            dir = null;
            config = null;
            SpecConfigTTool.setBasicConfig(systemcOn);
            updateLastOpenFile(file);
        }
        return b;
    }

    public String setFileName(String fileName) {
        File f = new File(fileName);
        file = FileUtils.addFileExtensionIfMissing(f, TFileFilter.getExtension());
        return file.getName();
    }

    public String getFileName() {
        return file.getAbsolutePath();
    }

    public boolean saveProject() {
        if (file == null) {
            if (dir != null)
                createFileDialog();
            else {
                int returnVal = jfc.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = jfc.getSelectedFile();
                    file = FileUtils.addFileExtensionIfMissing(file, TFileFilter.getExtension());
                }
            }
        }

        return mainSave();
    }

    public boolean mainSave() {
        if (checkFileForSave(file)) {
            try {
                if (gtm == null) {
                    throw new Exception("Internal model Error 1");
                }
                String s = gtm.makeXMLFromTurtleModeling(-1);
                if (s == null) {
                    throw new Exception("Internal model Error 2");
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(s.getBytes());
                fos.close();
                updateLastOpenFile(file);
                setMode(MODEL_SAVED);
                String title = "TTool: " + file.getAbsolutePath();
                if (!frame.getTitle().equals(title)) {
                    frame.setTitle(title);
                }
                if (lotosfile == null) {
                    makeLotosFile();
                }
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return false;
    }

    public boolean checkFileForOpen(File file) {
        boolean ok = true;
        String pb = "";

        // TraceManager.addDev("File path=" + file.getPath() + " name=" +
        // file.getName());

        if (file == null) {
            return false;
        }

        try {
            if (file != null) {
                if (!file.exists()) {
                    pb = "File " + file + " doesn't exist";
                    ok = false;
                }
                if ((ok == true) && (!file.canRead())) {
                    pb = "File is read protected";
                    ok = false;
                }
            }
        } catch (Exception e) {
            ok = false;
            pb = e.getMessage();
        }
        if (ok == false) {
            file = null;
            JOptionPane.showMessageDialog(frame, pb, "File Error", JOptionPane.INFORMATION_MESSAGE);
        }
        return ok;
    }

    public boolean checkFileForSave(File file) {
        boolean ok = true;
        String pb = "";

        if (file == null) {
            return false;
        }

        try {
            if (file != null) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        pb = "File could not be created";
                        ok = false;
                    }
                    if (!file.canWrite()) {
                        pb = "File is write protected";
                        ok = false;
                    }
                }
            }
        } catch (Exception e) {
            ok = false;
            pb = e.getMessage();
        }
        if (ok == false) {
            file = null;
            JOptionPane.showMessageDialog(frame, pb, "File Error", JOptionPane.INFORMATION_MESSAGE);
        }
        return ok;
    }

    public void saveLastLotos() {
        saveLotosSpec(gtm.getLastRTLOTOSSpecification());
    }

    public void saveLastDTA() {
        saveDTA(gtm.getLastTextualDTA(), gtm.getLastGraphicalDTA());
    }

    public void saveLastRG() {
        saveRG(gtm.getLastTextualRG(), gtm.getLastGraphicalRG());
    }

    public void saveLastTLSA() {
        saveTLSA(gtm.getLastTextualTLSA(), gtm.getLastGraphicalTLSA());
    }

    public void saveLastRGAUT() {
        saveRGAUT(gtm.getLastTextualRGAUT(), gtm.getLastGraphicalRGAUT());
    }

    public void saveLastRGAUTProj() {
        saveRGAUT(gtm.getLastTextualRGAUTProj(), gtm.getLastGraphicalRGAUTProj());
    }

    public void saveLastModifiedRG() {
        saveRGAUT(modifiedaut, modifiedautdot);
    }

    public void importLibrary() {
        // TDiagramPanel tdp1 =
        // ((TDiagramPanel)(designPanels.elementAt(mainDesignTabbedPane.getSelectedIndex()));
        // tdp1.insertLibrary(tdp1.getMinX(), tdp1.getMinY());
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            tdp1.insertLibrary(tdp1.getMinX(), tdp1.getMinY());
        }
    }

    public void exportLibrary() {
        // TDiagramPanel tdp1 =
        // (TDiagramPanel)(designPanels.elementAt(mainDesignTabbedPane.getSelectedIndex()));
        // tdp1.saveAsLibrary();
        getCurrentTDiagramPanel().saveAsLibrary();
    }

    public void makeLotosFile() {
        String s = file.getAbsolutePath();
        int l = s.length();
        String myFile = s.substring(0, l - 4);
        lotosfile = new File(myFile + ".lot");
        simfile = new File(myFile + ".sim");
        dtafile = new File(myFile + ".dta");
        dtadotfile = new File(myFile + ".dta.dot");
        rgfile = new File(myFile + ".rg");
        rgdotfile = new File(myFile + ".rg.dot");
        tlsafile = new File(myFile + ".tlsa");
        tlsadotfile = new File(myFile + ".dot.tlsa");
        rgautfile = new File(myFile + ".aut");
        fc2file = new File(myFile + ".fc2");
        bcgfile = new File(myFile + ".bcg");
        rgautdotfile = new File(myFile + ".aut.dot");
        rgautprojfile = new File(myFile + "_proj.aut");
        rgautprojdotfile = new File(myFile + "_proj.aut.dot");
    }

    /**
     * Display "Modeling has not been saved" window
     *
     * @param str1 (text for the left button)
     * @param str2 (text for the right button)
     * @return boolean (false = cancel, true = perform action)
     * @author Fabien Tessier
     */
    public boolean saveBeforeAction(String str1, String str2) {
        Object[] options = { str1, str2, "CANCEL" }; // Texts for buttons
        JOptionPane optionPane = new JOptionPane("Modeling has not been saved", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null,
                options, options[0]);
        JDialog dialog = optionPane.createDialog(activetdp, "Warning"); // Use JDialog to enable navigation with arrow keys
        dialog.setLocation((frame.getSize().width) / 2 - dialog.getWidth() / 2, (frame.getSize().height) / 2 - dialog.getHeight() / 2);
        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

        Set<AWTKeyStroke> forwardTraversalKeys = new HashSet<AWTKeyStroke>(dialog.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_UNDEFINED));
        dialog.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardTraversalKeys); // Navigation with right arrow

        Set<AWTKeyStroke> backwardTraversalKeys = new HashSet<AWTKeyStroke>(
                dialog.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_UNDEFINED));
        dialog.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardTraversalKeys); // Navigation with left arrow

        dialog.setVisible(true);
        dialog.dispose();
        String ret = (String) optionPane.getValue(); // Get value of the pressed button
        if (ret == null || ret.equals("CANCEL")) {
            return false;
        }

        if (ret.equals(str1)) {
            saveProject();
        }
        return true;
    }

    public void quitApplication() {
        quitApplication(true, true);
    }

    public void quitApplication(boolean askUser, boolean mustExit) {
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b && askUser) {
            if (saveBeforeAction("SAVE and QUIT", "QUIT now") == false) {
                return;
            }

        }

        ConfigurationTTool.LastWindowAttributesX = "" + frame.getLocation().x;
        ConfigurationTTool.LastWindowAttributesY = "" + frame.getLocation().y;
        ConfigurationTTool.LastWindowAttributesWidth = "" + frame.getSize().width;
        ConfigurationTTool.LastWindowAttributesHeight = "" + frame.getSize().height;

        if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
            ConfigurationTTool.LastWindowAttributesMax = "true";
        } else {
            ConfigurationTTool.LastWindowAttributesMax = "false";
        }

        try {
            if (ConfigurationTTool.LastOpenFileDefined) {
                if (dir != null)
                    saveConfig();
                ConfigurationTTool.saveConfiguration();
                // TraceManager.addDev("Configuration written to file");
            }
        } catch (Exception e) {
        }

        if (mustExit) {
            System.exit(0);
        }
    }

    public void cut() {
        getCurrentTDiagramPanel().makeCut();
    }

    public void copy() {
        getCurrentTDiagramPanel().makeCopy();
    }

    public void paste() {
        int x = Math.min(Math.max(getCurrentTDiagramPanel().getMinX(), getCurrentTDiagramPanel().currentX), getCurrentTDiagramPanel().getMaxX());
        int y = Math.min(Math.max(getCurrentTDiagramPanel().getMinY(), getCurrentTDiagramPanel().currentY), getCurrentTDiagramPanel().getMaxY());
        getCurrentTDiagramPanel().makePaste(x, y);
    }

    public void delete() {
        getCurrentTDiagramPanel().makeDelete();
    }

    public void backward() {
        // TraceManager.addDev("backward");
        gtm.backward();
        setMode(MODEL_CHANGED);
        dtree.toBeUpdated();
    }

    public void forward() {
        // TraceManager.addDev("forward");
        gtm.forward();
        setMode(MODEL_CHANGED);
        dtree.toBeUpdated();
    }

    public void zoomMore() {
        zoom(1.25);
    }

    public void zoomLess() {
        zoom(0.8);
    }

    public void zoom(double multFactor) {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        tdp.setZoom(tdp.getZoom() * multFactor);
        // tdp.updateComponentsAfterZoom();
        updateZoomInfo();
    }

    public void updateZoomInfo() {
        String s = "";
        int zoom = (int) (getCurrentTDiagramPanel().getZoom() * 100);
        if (zoom < 100) {
            s = "0" + zoom + "%";
        } else {
            s += zoom + "%";
        }
        // TraceManager.addDev("Seeting zoom in " + getCurrentTDiagramPanel());
        actions[TGUIAction.ACT_SHOW_ZOOM].setName(TGUIAction.ACT_SHOW_ZOOM, s);
    }

    public void oldFirstDiag() {
        getCurrentJTabbedPane().setSelectedIndex(0);
    }

    public void backDiag() {
        getCurrentJTabbedPane().setSelectedIndex(Math.max(0, getCurrentJTabbedPane().getSelectedIndex() - 1));
    }

    public void nextDiag() {
        getCurrentJTabbedPane().setSelectedIndex(Math.min(getCurrentJTabbedPane().getTabCount(), getCurrentJTabbedPane().getSelectedIndex() + 1));
    }

    public void lastDiag() {
        getCurrentJTabbedPane().setSelectedIndex(getCurrentJTabbedPane().getTabCount() - 1);
    }

    // @author: Huy TRUONG
    // open a new External Search Dialog
    public void showExternalSearch() {
        String textSearchField = mainBar.getSearchText();
        List<String> listSearch = new ArrayList<String>();

        if (null == this.searchBox) {
            if (getCurrentTDiagramPanel() != null) {
                if (getCurrentTDiagramPanel().tdmm.getSelectComponents().size() == 0) {
                    listSearch.add(textSearchField);
                } else {
                    listSearch = getCurrentTDiagramPanel().tdmm.getSelectComponents();
                    listSearch.add(0, textSearchField);

                }
                this.searchBox = new JDialogSearchBox(frame, "External Search", listSearch, getCurrentTDiagramPanel().tdmm);
            } else
                this.searchBox = new JDialogSearchBox(frame, "External Search", new ArrayList<String>());

        } else {
            if (this.searchBox.isShowing()) {
                this.searchBox.setVisible(true);
            } else {
                this.searchBox = null;
                showExternalSearch();
            }
        }
    }

    public void doInternalSearch() {
        search(mainBar.getSearchText());
    }

    public void aboutVersion() {
        JFrameBasicText jft = new JFrameBasicText("About TTool ...", DefaultText.getAboutText(), IconManager.imgic324);
        jft.setIconImage(IconManager.img8);
        GraphicLib.centerOnParent(jft, 740, 800);
        jft.setVisible(true);

    }


    public void aboutPlugins() {
        JFramePluginText jft = new JFramePluginText("Plugins", IconManager.imgic324);
        jft.setIconImage(IconManager.img8);
        GraphicLib.centerOnParent(jft, 740, 800);
        jft.setVisible(true);
    }

    public void showTToolConfiguration() {
        JFrameBasicText jft = new JFrameBasicText("Your configuration of TTool ...",
                "Default configuration:\n-----------------------\n" + ConfigurationTTool.getConfiguration(systemcOn)
                        + "\nProject configuration:\n-----------------------\n" + SpecConfigTTool.getConfiguration(systemcOn),
                IconManager.imgic76);
        jft.setIconImage(IconManager.img8);
        // jft.setSize(700, 800);
        GraphicLib.centerOnParent(jft, 700, 800);
        jft.setVisible(true);

    }

    public void aboutTURTLE() {
        BrowserControl.startBrowerToURL("http://ttool.telecom-paristech.fr/");
    }

    public void helpTURTLE() {
        BrowserControl.startBrowerToURL("http://ttool.telecom-paristech.fr/avatar.html");
    }

    public void helpSysMLSec() {
        BrowserControl.startBrowerToURL("http://sysml-sec.telecom-paristech.fr/");
    }

    public void helpDIPLODOCUS() {
        BrowserControl.startBrowerToURL("http://ttool.telecom-paristech.fr/diplodocus.html");
    }

    public void oneClickLOTOSRG() {
        // boolean ret;
        if (!checkModelingSyntax(true)) {
            TraceManager.addDev("Syntax error");
            return;
        }

        if (!generateLOTOS(true)) {
            TraceManager.addDev("Generate LOTOS: error");
            return;
        }

        formalValidation(true);
    }

    public void oneClickRTLOTOSRG() {
        // boolean ret;
        if (!checkModelingSyntax(true)) {
            TraceManager.addDev("Syntax error");
            return;
        }

        if (!generateRTLOTOS(true)) {
            TraceManager.addDev("Generate RT-LOTOS: error");
            return;
        }

        formalValidation(true);
    }

    public void modelChecking() {
        checkModelingSyntax(false);
    }

    public boolean checkModelingSyntax(String panelName, boolean automatic) {
        TURTLEPanel tp = getTURTLEPanel(panelName);
        if (tp != null) {
            return checkModelingSyntax(tp, automatic);
        }

        return false;
    }

    public boolean checkModelingSyntax(boolean automatic) {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp != null) {
            return checkModelingSyntax(tp, automatic);
        }

        return false;
    }

    public void issueError(String error, String title) {
        JOptionPane.showMessageDialog(frame, error, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean checkModelingSyntax(TURTLEPanel tp, boolean automatic) {
        // String msg = "";
        boolean b = false;
        boolean ret = false;

        if (file == null) {
            JOptionPane.showMessageDialog(frame, "The project must be saved before any simulation or formal verification can be performed",
                    "Syntax analysis failed", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            try {
                b = gtm.buildTURTLEModelingFromAnalysis((AnalysisPanel) tp);
            } catch (AnalysisSyntaxException ae) {
                // TraceManager.addDev("Exception AnalysisSyntaxException");
                // msg = ae.getMessage();
                b = false;
            }
            expandToWarnings();
            expandToErrors();
            if (b) {
                setMode(MainGUI.MODEL_OK);
                setMode(MainGUI.GEN_DESIGN_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                            "0 error, " + getCheckingWarnings().size()
                                    + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                            "Syntax analysis successful on analysis diagrams", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The TURTLE Analysis contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                setMode(MainGUI.GEN_DESIGN_OK);
                // setMode(MainGUI.MODEL_OK);
            }

        } else if (tp instanceof DesignPanel) {
            // Design
            DesignPanel dp = (DesignPanel) tp;
            JDialogModelChecking.validated = dp.validated;
            JDialogModelChecking.ignored = dp.ignored;
            List<TClassInterface> tclassesToValidate = new LinkedList<TClassInterface>();
            JDialogModelChecking jdmc = new JDialogModelChecking(frame, tclassesToValidate, dp.tcdp.getComponentList(),
                    "Choosing Tclasses to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed
            } else {
                jdmc.closeDialog();
            }
            boolean overideTifChecking = jdmc.getOverideSyntaxChecking();
            if (tclassesToValidate.size() > 0) {
                dp.validated = JDialogModelChecking.validated;
                dp.ignored = JDialogModelChecking.ignored;
                b = gtm.checkTURTLEModeling(tclassesToValidate, dp, overideTifChecking);
                expandToWarnings();
                expandToErrors();
                if (b) {
                    ret = true;
                    setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_DESIGN_OK);
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                                "Syntax analysis successful on design diagrams", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The TURTLE Modeling contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } else if (tp instanceof DeploymentPanel) {
            DeploymentPanel dp = (DeploymentPanel) tp;
            b = gtm.translateDeployment(dp);
            expandToWarnings();
            expandToErrors();
            if (b) {
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                            "0 error, " + getCheckingWarnings().size()
                                    + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                            "Syntax analysis successful on deployment diagrams", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The TURTLE deployment contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // FaultTree
        } else if (tp instanceof FaultTreePanel) {
            FaultTreePanel atp = (FaultTreePanel) tp;
            b = gtm.translateFaultTreePanel(atp);
            expandToWarnings();
            expandToErrors();
            if (b) {
                setMode(MainGUI.FAULTTREE_SYNTAXCHECKING_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size() + " warning(s)",
                            "Syntax analysis successful on fault tree", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The fault tree contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } else if (tp instanceof AttackTreePanel) {
            AttackTreePanel atp = (AttackTreePanel) tp;
            b = gtm.translateAttackTreePanel(atp, getTDiagramPanelIndex());
            expandToWarnings();
            expandToErrors();
            if (b) {
                setMode(MainGUI.ATTACKTREE_SYNTAXCHECKING_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size() + " warning(s)",
                            "Syntax analysis successful on attack tree", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The Attack tree contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // AVATAR
        } else if (tp instanceof AvatarDesignPanel) {
            // Design
            AvatarDesignPanel adp = (AvatarDesignPanel) tp;
            // JDialogModelChecking.validated = adp.validated;
            // JDialogModelChecking.ignored = adp.ignored;
            List<AvatarBDStateMachineOwner> blocksToValidate = new LinkedList<AvatarBDStateMachineOwner>();
            JDialogSelectAvatarBlock jdmc = new JDialogSelectAvatarBlock(frame, blocksToValidate,
                    adp.getAvatarBDPanel().getFullStateMachineOwnerList(), "Choosing blocks to validate", adp.getValidated(), adp.getIgnored(),
                    adp.getOptimized(), adp.getConsiderTimingOperators());
            if (!automatic) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed
            } else {
                jdmc.closeDialog();
            }

            if (jdmc.hasBeenCancelled()) {
                return false;
            }

            adp.resetModelBacktracingProVerif();

            adp.setValidated(jdmc.getValidated());
            adp.setIgnored(jdmc.getIgnored());
            adp.setOptimized(jdmc.getOptimized());
            adp.setConsiderTimingOperators(jdmc.getConsiderTimingOperators());

            boolean optimize = jdmc.getOptimized();
            boolean considerTimingOperators = jdmc.getConsiderTimingOperators();
            if (blocksToValidate.size() > 0) {
                /*
                 * adp.validated = JDialogModelChecking.validated; adp.ignored =
                 * JDialogModelChecking.ignored;
                 */
                b = gtm.checkAvatarDesign(blocksToValidate, adp, optimize, considerTimingOperators);
                expandToWarnings();
                expandToErrors();
                if (b) {
                    ret = true;
                    setMode(MainGUI.AVATAR_SYNTAXCHECKING_OK);
                    // setMode(MainGUI.GEN_DESIGN_OK);
                    /*
                     * if (!automatic) { JOptionPane.showMessageDialog(frame, "0 error, " +
                     * getCheckingWarnings().size() +
                     * " warning(s). You can now perform simulations or formal proofs (UPPAAL)",
                     * "Syntax analysis successful on avatar design diagrams",
                     * JOptionPane.INFORMATION_MESSAGE); }
                     */
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The Avatar modeling contains several errors", "Syntax analysis failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // NC
        }
        // DG 6.2. 2017

        else if (tp instanceof ADDPanel) {
            // Design
            AvatarDesignPanel adp = getFirstAvatarDesignPanelFound();

            // JDialogModelChecking.validated = adp.validated;
            // JDialogModelChecking.ignored = adp.ignored;
            List<AvatarBDStateMachineOwner> blocksToValidate = new LinkedList<AvatarBDStateMachineOwner>();
            JDialogSelectAvatarBlock jdmc = new JDialogSelectAvatarBlock(frame, blocksToValidate,
                    adp.getAvatarBDPanel().getFullStateMachineOwnerList(), "Choosing blocks to validate", adp.getValidated(), adp.getIgnored(),
                    adp.getOptimized(), adp.getConsiderTimingOperators());
            if (!automatic) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed
            } else {
                jdmc.closeDialog();
            }

            if (jdmc.hasBeenCancelled()) {
                return false;
            }

            adp.resetModelBacktracingProVerif();

            adp.setValidated(jdmc.getValidated());
            adp.setIgnored(jdmc.getIgnored());
            adp.setOptimized(jdmc.getOptimized());
            adp.setConsiderTimingOperators(jdmc.getConsiderTimingOperators());

            boolean optimize = jdmc.getOptimized();
            boolean considerTimingOperators = jdmc.getConsiderTimingOperators();


            if (blocksToValidate.size() > 0) {
                /*
                 * adp.validated = JDialogModelChecking.validated; adp.ignored =
                 * JDialogModelChecking.ignored;
                 */
                b = gtm.checkAvatarDesign(blocksToValidate, adp, optimize, considerTimingOperators);
                expandToWarnings();
                expandToErrors();
                if (b) {
                    ret = true;
                    setMode(MainGUI.AVATAR_SYNTAXCHECKING_OK);
                    // setMode(MainGUI.GEN_DESIGN_OK);
                    /*
                     * if (!automatic) { JOptionPane.showMessageDialog(frame, "0 error, " +
                     * getCheckingWarnings().size() +
                     * " warning(s). You can now perform simulations or formal proofs (UPPAAL)",
                     * "Syntax analysis successful on avatar design diagrams",
                     * JOptionPane.INFORMATION_MESSAGE); }
                     */
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The Avatar modeling contains several errors", "Syntax analysis failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // NC
        }

        // fin DG
        else if (tp instanceof NCPanel) {
            NCPanel ncp = (NCPanel) tp;
            b = gtm.translateNC(ncp);
            if (b) {
                // setMode(MainGUI.MODEL_OK_NC);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size() + " warning(s)",
                            "Syntax analysis successful on NC diagram", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The NC diagram contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } else if (tp instanceof TMLDesignPanel) {
            TMLDesignPanel tmldp = (TMLDesignPanel) tp;
            JDialogSelectTMLTask.validated = tmldp.validated;
            JDialogSelectTMLTask.ignored = tmldp.ignored;
            Vector<TGComponent> tmlTasksToValidate = new Vector<TGComponent>();
            JDialogSelectTMLTask jdstmlt = new JDialogSelectTMLTask(frame, tmlTasksToValidate, tmldp.tmltdp.getComponentList(),
                    "Choosing TML tasks to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdstmlt);
                jdstmlt.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdstmlt.closeDialog();
            }
            if (tmlTasksToValidate.size() > 0) {
                tmldp.validated = JDialogSelectTMLTask.validated;
                tmldp.ignored = JDialogSelectTMLTask.ignored;
                b = gtm.translateTMLDesign(tmlTasksToValidate, tmldp, jdstmlt.getOptimize());
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if ((!automatic) && (getCheckingWarnings().size() > 0)) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now generate make proofs (safety, security and performance) or generate executable code",
                                "Syntax analysis successful on TML designs", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The TML design contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof TMLComponentDesignPanel) {
            TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) tp;
            JDialogSelectTMLComponent.validated = tmlcdp.validated;
            JDialogSelectTMLComponent.ignored = tmlcdp.ignored;
            Vector<TGComponent> tmlComponentsToValidate = new Vector<>();
            JDialogSelectTMLComponent jdstmlc = new JDialogSelectTMLComponent(frame, tmlComponentsToValidate, tmlcdp.tmlctdp.getComponentList(),
                    "Choosing TML components to validate", true, true);
            if (!automatic) {
                GraphicLib.centerOnParent(jdstmlc);
                jdstmlc.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdstmlc.closeDialog();
            }
            if (tmlComponentsToValidate.size() > 0) {
                tmlcdp.validated = JDialogSelectTMLComponent.validated;
                tmlcdp.ignored = JDialogSelectTMLComponent.ignored;
                b = gtm.translateTMLComponentDesign(tmlComponentsToValidate, tmlcdp, jdstmlc.getOptimize(),
                        jdstmlc.getConsiderTimingOperators());
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if ((!automatic) && (getCheckingWarnings().size() > 0)) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now generate make proofs (safety, security and performance) or generate executable code",
                                "Syntax analysis successful on TML designs", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The TML design contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof SysCAMSComponentDesignPanel) {
            SysCAMSComponentDesignPanel syscamscdp = (SysCAMSComponentDesignPanel) tp;
            JDialogSelectSysCAMSComponent.validated = syscamscdp.validated;
            JDialogSelectSysCAMSComponent.ignored = syscamscdp.ignored;
            Vector<TGComponent> syscamsComponentsToValidate = new Vector<TGComponent>();
            JDialogSelectSysCAMSComponent jdssyscamsc = new JDialogSelectSysCAMSComponent(frame, syscamsComponentsToValidate,
                    syscamscdp.syscamsctdp.getComponentList(), "Choosing SystemC-AMS components to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdssyscamsc);
                jdssyscamsc.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdssyscamsc.closeDialog();
            }
            if (syscamsComponentsToValidate.size() > 0) {
                syscamscdp.validated = JDialogSelectSysCAMSComponent.validated;
                syscamscdp.ignored = JDialogSelectSysCAMSComponent.ignored;
//        		b = gtm.translateSysCAMSComponentDesign(syscamsComponentsToValidate, syscamscdp, jdssyscamsc.getOptimize());
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now generate make proofs (safety, security and performance) or generate executable code",
                                "Syntax analysis successful on SystemC-AMS designs", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                // "The SystemC-AMS design contains several errors",
                                // "Syntax analysis failed",
                                "Syntax analysis not executed", "Not available for SystemC-AMS panel", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof ELNDesignPanel) {
            ELNDesignPanel elndp = (ELNDesignPanel) tp;
            JDialogSelectELNComponent.validated = elndp.validated;
            JDialogSelectELNComponent.ignored = elndp.ignored;
            Vector<TGComponent> ELNComponentsToValidate = new Vector<TGComponent>();
            JDialogSelectELNComponent jdselnc = new JDialogSelectELNComponent(frame, ELNComponentsToValidate, elndp.elndp.getComponentList(),
                    "Choosing ELN components to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdselnc);
                jdselnc.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdselnc.closeDialog();
            }
            if (ELNComponentsToValidate.size() > 0) {
                elndp.validated = JDialogSelectELNComponent.validated;
                elndp.ignored = JDialogSelectELNComponent.ignored;
//        		b = gtm.translateSysCAMSComponentDesign(syscamsComponentsToValidate, syscamscdp, jdssyscamsc.getOptimize());
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now generate make proofs (safety, security and performance) or generate executable code",
                                "Syntax analysis successful on ELN designs", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The ELN design contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof TMLArchiPanel) {
            TMLArchiPanel  tmlap = (TMLArchiPanel) tp;
            JDialogSelectTMLNodes.validated = tmlap.validated;
            JDialogSelectTMLNodes.ignored = tmlap.ignored;
            Vector<TGComponent> tmlNodesToValidate = new Vector<TGComponent>();
            JDialogSelectTMLNodes jdstmln = new JDialogSelectTMLNodes(frame, tmlNodesToValidate, tmlap.tmlap.getComponentList(),
                    "Choosing Nodes to validate", tmlap.tmlap.getMasterClockFrequency());
            if (!automatic) {
                GraphicLib.centerOnParent(jdstmln);
                jdstmln.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdstmln.closeDialog();
            }
            tmlap.tmlap.setMasterClockFrequency(jdstmln.getClock());

            if (tmlNodesToValidate.size() > 0) {
                tmlap.validated = JDialogSelectTMLNodes.validated;
                tmlap.ignored = JDialogSelectTMLNodes.ignored;
                // TraceManager.addDev("Ready to generate TML mapping!");
                b = gtm.checkSyntaxTMLMapping(tmlNodesToValidate, tmlap, jdstmln.getOptimize());
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if ((!automatic) && (getCheckingWarnings().size() > 0)) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now perform verifications (safety, security, performance) or generate executable code",
                                "Syntax analysis successful on TML mapping", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The TML mapping contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof TMLCommunicationPatternPanel) {
            TMLCommunicationPatternPanel tmlcpp = (TMLCommunicationPatternPanel) tp;
            JDialogSelectCPDiagrams.validated = tmlcpp.validated;
            JDialogSelectCPDiagrams.ignored = tmlcpp.ignored;
            Vector<TGComponent> tmlDiagramsToValidate = new Vector<TGComponent>();
            JDialogSelectCPDiagrams jdscpd = new JDialogSelectCPDiagrams(frame, tmlDiagramsToValidate, tmlcpp.tmlcpp.getComponentList(),
                    "Choosing Diagrams to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdscpd);
                jdscpd.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdscpd.closeDialog();
            }
            if (tmlDiagramsToValidate.size() > 0) {
                tmlcpp.validated = JDialogSelectCPDiagrams.validated;
                tmlcpp.ignored = JDialogSelectCPDiagrams.ignored;
                TraceManager.addDev("Ready to generate TML code for Communication Patterns!");
                b = gtm.checkSyntaxTMLCP(tmlDiagramsToValidate, tmlcpp, jdscpd.getOptimize()); // Fills a data structure
                // translateTMLComponentDesign
                // and should say if it is correct or contains error in the return variable b
                expandToWarnings();
                expandToErrors();
                if (b) {
                    // setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if ((!automatic) && (getCheckingWarnings().size() > 0)) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size()
                                + " warning(s). You can now perform verifications (safety, security, performance) or generate executable code",
                                "Syntax analysis successful on TML mapping", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame, "The Communication Patterns design contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof RequirementPanel) {
            TDiagramPanel tdp = getCurrentTDiagramPanel();
            if (!(tdp instanceof RequirementDiagramPanel)) {
                if (tdp instanceof EBRDDPanel) {

                    b = gtm.makeEBRDD((EBRDDPanel) tdp);
                    if (b) {
                        JOptionPane.showMessageDialog(frame, "0 error, " + getCheckingWarnings().size() + " warning(s).",
                                "Syntax analysis successful on EBRDD", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "The EBRDD contains several errors", "Syntax analysis failed",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    return ret;
                }
                // TraceManager.addDev("No syntax checking for EBRDD: not yet implemented");
            } else {
                RequirementDiagramPanel rdp = (RequirementDiagramPanel) tdp;
                JDialogSelectRequirements.validated = rdp.validated;
                JDialogSelectRequirements.ignored = rdp.ignored;
                Vector<Requirement> reqsToValidate = new Vector<Requirement>();
                JDialogSelectRequirements jdsreq = new JDialogSelectRequirements(frame, reqsToValidate, rdp.getComponentList(),
                        "Choosing requirements to verify");

                if (!automatic) {
                    GraphicLib.centerOnParent(jdsreq);
                    jdsreq.setVisible(true); // Blocked until dialog has been closed
                }

                if (reqsToValidate.size() > 0) {
                    rdp.validated = JDialogSelectRequirements.validated;
                    rdp.ignored = JDialogSelectRequirements.ignored;
                    b = gtm.generateTMsForRequirementAnalysis(reqsToValidate, rdp);
                    expandToWarnings();
                    expandToErrors();
                    if (b) {
                        // setMode(MainGUI.GEN_SYSTEMC_OK);
                        setMode(MainGUI.REQ_OK);
                        ret = true;
                        if (!automatic) {
                            JOptionPane.showMessageDialog(frame,
                                    "0 error, " + getCheckingWarnings().size() + " warning(s). You can now verify requirements' satisfiability",
                                    "Syntax analysis successful on requirements", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        if (!automatic) {
                            JOptionPane.showMessageDialog(frame, "The requirement diagram contains several errors", "Syntax analysis failed",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        } else if (tp instanceof ProactiveDesignPanel) {
            // TraceManager.addDev("!!!!!!!!!!!!1");
            // newTurtleModeling();
            b = gtm.translateProactiveDesign((ProactiveDesignPanel) tp);
            expandToWarnings();
            expandToErrors();
            if (b) {
                // setMode(MainGUI.MODEL_OK);
                // setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                            "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a LOTOS,specification",
                            "Syntax analysis successful on Proactive design", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The Proactive design contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } else if (tp instanceof TURTLEOSDesignPanel) {
            TraceManager.addDev("TURTLEOS Design Panel");
            // TURTLEOSDesignPanel tosdp = (TURTLEOSDesignPanel) tp;
            b = gtm.translateTURTLEOSDesign((TURTLEOSDesignPanel) tp);
            expandToWarnings();
            expandToErrors();
            if (b) {
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                            "0 error, " + getCheckingWarnings().size()
                                    + " warning(s). You can now generate a corresponding formal specification or executable code (Java)",
                            "Syntax analysis successful on deployment diagrams", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame, "The TURTLE deployment contains several errors", "Syntax analysis failed",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        // dtree.toBeUpdated();
        dtree.forceUpdate();
        return ret;
    }

    public AttackTree runAttackTreeAnalysis() {
        AttackTreePanel atp = (AttackTreePanel) getCurrentTURTLEPanel();
        return gtm.getAttackTree(atp, getTDiagramPanelIndex());
    }

    public List<TAttribute> getAllAttributes() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        String name = getCurrentTDiagramPanel().getName();

        return this.getAllAttributes(tp, name);
    }

    public List<TAttribute> getAllAttributes(TURTLEPanel tp, String name) {
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel) tp;

        return adp.getAllAttributes(name);
    }

    public List<AvatarMethod> getAllMethods() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel) tp;

        String name = getCurrentTDiagramPanel().getName();

        return adp.getAllMethods(name);
    }

    public List<AvatarSignal> getAllSignals() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        String name = getCurrentTDiagramPanel().getName();
        return this.getAllSignals(tp, name);
    }


    public List<AvatarSignal> getAllSignals(TURTLEPanel tp, String name) {
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel) tp;

        return adp.getAllSignals(name);
    }



    public List<AvatarBDLibraryFunction> getAllLibraryFunctions() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        return this.getAllLibraryFunctions(tp);
    }

    public List<AvatarBDLibraryFunction> getAllLibraryFunctions(TURTLEPanel tp) {
        String name = getCurrentTDiagramPanel().getName();
        return this.getAllLibraryFunctions(tp, name);
    }

    /*
     * Note that this is here for historical purpose : Now, any block can access
     * library functions of any other block.
     */
    public List<AvatarBDLibraryFunction> getAllLibraryFunctions(TURTLEPanel tp, String name) {
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel) tp;

        return adp.getAllLibraryFunctions(name);
    }

    public String[] getAllOutEvents() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;

        String name = getCurrentTDiagramPanel().getName();

        return tmlcomp.getAllOutEvents(name);
    }

    public ArrayList<TGComponent> getAllLatencyChecks() {
        TURTLEPanel tp;
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            tp.getAllLatencyChecks(list);
        }
        return list;
    }

    public String[] getAllInEvents() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        String name = getCurrentTDiagramPanel().getName();
        return tmlcomp.getAllInEvents(name);
    }

    public String[] getAllOutChannels() { // this routine can be called only from a TMLComponentDesignPanel
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        String name = getCurrentTDiagramPanel().getName();

        return tmlcomp.getAllOutChannels(name);
    }

    public String[] getAllCompOutChannels() { // this routine can be called only from a TMLComponentDesignPanel
        // List<String> chans = new ArrayList<String>();
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        return tmlcomp.getCompOutChannels();
    }

    public String[] getAllCompInChannels() { // this routine can be called only from a TMLComponentDesignPanel
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        return tmlcomp.getCompInChannels();
    }

    public String[] getAllInChannels() { // this routine can be called only from a TMLComponentDesignPanel
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        String name = getCurrentTDiagramPanel().getName();
        return tmlcomp.getAllInChannels(name);
    }

    public Vector<String> getAllCryptoConfig() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                for (String s : ((TMLComponentDesignPanel) tp).getAllCryptoConfig()) {
                    list.add(mainTabbedPane.getTitleAt(i) + "::" + s);
                }
            }
        }
        return list;
    }

    public String[] getCurrentCryptoConfig() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        List<String> strlist = tmlcomp.getAllCryptoConfig();
        String[] strarray = new String[strlist.size()];
        strlist.toArray(strarray);
        return strarray;
    }

    public String[] getAllNonce() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        List<String> strlist = tmlcomp.getAllNonce();
        String[] strarray = new String[strlist.size()];
        strlist.toArray(strarray);
        return strarray;
    }

    public ArrayList<String> getAllKeys() {
        TURTLEPanel tp;
        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                for (String s : ((TMLComponentDesignPanel) tp).getAllKeys()) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    public ArrayList<TGComponent> getAllAttacks() {
        TURTLEPanel tp;
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AttackTreePanel) {
                for (TGComponent s : ((AttackTreePanel) tp).getAllAttacks()) {
                    list.add(s);
                }
            }
        }
        return list;

    }

    public ArrayList<TGComponent> getAllFaults() {
        TURTLEPanel tp;
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof FaultTreePanel) {
                for (TGComponent s : ((FaultTreePanel) tp).getAllFaults()) {
                    list.add(s);
                }
            }
        }
        return list;

    }

    public ArrayList<TGComponent> getAllRequirements() {
        TURTLEPanel tp;
        ArrayList<TGComponent> list = new ArrayList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AvatarRequirementPanel) {
                for (TGComponent s : ((AvatarRequirementPanel) tp).getAllRequirements()) {
                    list.add(s);
                }
            }
        }
        return list;

    }

    public String[] getAllOutRequests() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return null;
        }
        TMLComponentDesignPanel tmlcomp = (TMLComponentDesignPanel) tp;
        String name = getCurrentTDiagramPanel().getName();
        return tmlcomp.getAllOutRequests(name);
    }

    public List<String> getAllTimers() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel) tp;

        String name = getCurrentTDiagramPanel().getName();

        return adp.getAllTimers(name);
    }

    public List<CheckingError> getCheckingErrors() {
        return gtm.getCheckingErrors();
    }

    public List<CheckingError> getCheckingWarnings() {
        return gtm.getCheckingWarnings();
    }

    public void modelBacktracingProVerif(ProVerifOutputAnalyzer pvoa) {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return;
        }

        if (tp instanceof AvatarDesignPanel) {
            AvatarDesignPanel adp = (AvatarDesignPanel) tp;
            adp.modelBacktracingProVerif(pvoa);
            getCurrentTDiagramPanel().repaint();
        } else if (tp instanceof TMLArchiPanel) {
            /*
             * for (int i=0; i<tabs.size(); i++){ tp = (TURTLEPanel)(tabs.elementAt(i)); if
             * (tp instanceof TMLComponentDesignPanel) {
             * ((TMLComponentDesignPanel)tp).modelBacktracingProVerif(pvoa); } }
             */
            gtm.getTMLMapping().getTMLModeling().clearBacktracing();
            gtm.getTMLMapping().getTMLModeling().backtrace(pvoa, getTabName(tp));
            gtm.getTML2Avatar().backtraceReachability(pvoa.getReachabilityResults());
            gtm.getTMLMapping().getTMLModeling().backtraceAuthenticity(pvoa, getTabName(tp));
        } else if (tp instanceof TMLComponentDesignPanel) {
            gtm.getTMLMapping().getTMLModeling().clearBacktracing();
            gtm.getTMLMapping().getTMLModeling().backtrace(pvoa, "Default Mapping");
            gtm.getTML2Avatar().backtraceReachability(pvoa.getReachabilityResults());
            gtm.getTMLMapping().getTMLModeling().backtraceAuthenticity(pvoa, "Default Mapping");
        }
        return;
    }

    public void clearBacktracing() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return;
        }

        tp.resetReachability();

        if (tp instanceof AvatarDesignPanel) {
            AvatarDesignPanel adp = (AvatarDesignPanel) tp;
            adp.resetModelBacktracingProVerif();
            adp.resetMetElements();
            getCurrentTDiagramPanel().repaint();
        } else if (tp instanceof TMLArchiPanel) {
            gtm.getTMLMapping().getTMLModeling().clearBacktracing();
        } else if (tp instanceof TMLComponentDesignPanel) {
            gtm.getTMLMapping().getTMLModeling().clearBacktracing();
        }
    }

    public void modelBacktracingUPPAAL(Map<String, Integer> verifMap) {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return;
        }

        AvatarDesignPanel adp = (AvatarDesignPanel) tp;
        adp.modelBacktracingUppaal(verifMap);
        getCurrentTDiagramPanel().repaint();
    }

    public void generateRTLOTOS() {
        generateRTLOTOS(false);
    }

    public boolean generateRTLOTOS(boolean automatic) {
        int ret = 0;
        if (gtm.getTURTLEModelingState() > 0) {
            if ((ret = generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), automatic, RT_LOTOS)) == -1) {
                return false;
            }
        }

        gtm.reinitSIM();
        gtm.reinitDTA();
        gtm.reinitRG();
        gtm.reinitRGAUT();
        gtm.reinitRGAUTPROJDOT();
        if (ret == 0) {
            gtm.generateRTLOTOS(lotosfile);
        }
        if (!automatic) {
            JOptionPane.showMessageDialog(frame, "RT-LOTOS specification generated", "RT-LOTOS specification", JOptionPane.INFORMATION_MESSAGE);
        }
        dtree.toBeUpdated();
        return true;
    }

    public void generateLOTOS() {
        generateLOTOS(false);
    }

    public boolean generateLOTOS(boolean automatic) {
        int ret = 0;
        if (gtm.getTURTLEModelingState() > 0) {
            ret = generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), automatic, LOTOS);
            if (ret == -1) {
                dtree.toBeUpdated();
                TraceManager.addDev("Generate from state failed");
                return false;
            }
            /*
             * if (!automatic && (gtm.getTURTLEModelingState() == 1)) { return true; }
             */
        }

        // TraceManager.addDev("generate LOTOS");
        if (ret == 0) {
            gtm.generateFullLOTOS(lotosfile);
            // TraceManager.addDev("LOTOS generated");
            if (!automatic) {
                JOptionPane.showMessageDialog(frame, "LOTOS specification generated (" + getCheckingWarnings().size() + " warning(s))",
                        "LOTOS specification", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        setMode(MainGUI.RTLOTOS_OK);
        dtree.toBeUpdated();
        return true;
    }

    public void generateFullLOTOS() {
        gtm.generateFullLOTOS(lotosfile);
        dtree.toBeUpdated();
    }

    // -1 : error
    // 0: ok
    // 1: ok, code already generated
    public int generateTURTLEModelingFromState(int state, boolean automatic, int generator) {
        if (state == 1) {
            TraceManager.addDev("Generating from state 1");
            if (generateTIFFromMapping(automatic, generator)) {
                return 1;
            }
            return -1;
        }
        if (state == 2) {
            TraceManager.addDev("Generating from state 2");
            if (generateTIFFromTMLModeling(automatic, generator)) {
                return 0;
            }
            return -1;
        }
        if (state == 3) {
            TraceManager.addDev("Generating from state 3 (Avatar)");
            if (generateTIFFromAvatarSpecification(automatic, generator)) {
                return 0;
            }
            return -1;
        }
        return -1;
    }

    public boolean generateTIFFromAvatarSpecification(boolean automatic, int generator) {
        boolean b = gtm.translateAvatarSpecificationToTIF();
        if (b) {
            setMode(MainGUI.MODEL_OK);
            return true;
        }
        return false;
    }

    public boolean generateTIFFromMapping(boolean automatic, int generator) {
        boolean b;

        // Scheduling options
        JDialogScheduling jds = new JDialogScheduling(frame, this, "Mapping options", generator);
        if (!automatic) {
            GraphicLib.centerOnParent(jds);
            jds.setVisible(true); // Blocked until dialog has been closed
        } else {
            jds.closeDialog();
            b = gtm.translateTMLMapping(jds.getSample(), jds.getChannel(), jds.getEvent(), jds.getRequest(), jds.getExec(), jds.getBusTransfer(),
                    jds.getScheduling(), jds.getTaskState(), jds.getChannelState(), jds.getBranching(), jds.getTerminateCPU(), jds.getTerminateCPUs(),
                    jds.getClocked(), jds.getTickIntervalValue(), jds.getEndClocked(), jds.getCountTick(), jds.getMaxCountTick(),
                    jds.getMaxCountTickValue(), jds.getRandomTask());
            if (b) {
                setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                return true;
            }
            return false;
        }

        if (!jds.isCancelled()) {
            b = jds.hasError();
            // gtm.translateTMLMapping(jds.getChannel(), jds.getEvent(), jds.getRequest(),
            // jds.getExec(), jds.getBusTransfer(), jds.getScheduling(), jds.getTaskState(),
            // jds.getChannelState(), jds.getBranching(), jds.getTerminateCPU(),
            // jds.getTerminateCPUs(), jds.getClocked(), jds.getTickIntervalValue(),
            // jds.getEndClocked(), jds.getCountTick(), jds.getMaxCountTick(),
            // jds.getMaxCountTickValue(), jds.getRandomTask());
            if (!b) {
                // setMode(MainGUI.MODEL_OK);
                setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                return true;
                /*
                 * if (!automatic) { JOptionPane.showMessageDialog(frame, "0 error, " +
                 * getCheckingWarnings().size() +
                 * " warning(s). Formal specification can be generated",
                 * "Successful translation from the TML mapping to a formal specification",
                 * JOptionPane.INFORMATION_MESSAGE); }
                 */
            } else {
                /*
                 * if (!automatic) { JOptionPane.showMessageDialog(frame,
                 * "Formal specification generation failed: the TML mapping contains several errors"
                 * , "Syntax analysis failed", JOptionPane.INFORMATION_MESSAGE); }
                 */
                return false;
            }
        }

        return false;
    }

    public boolean generateTIFFromTMLModeling(boolean automatic, int generator) {
        return gtm.translateTMLModeling();
    }

    public void generateAUT() {
        JDialogGenAUT jdgaut = new JDialogGenAUT(frame, this, "Generation of automata", ConfigurationTTool.BcgioPath,
                ConfigurationTTool.AldebaranHost, SpecConfigTTool.TGraphPath);
        // jdgaut.setSize(450, 600);
        GraphicLib.centerOnParent(jdgaut, 450, 600);
        jdgaut.setVisible(true);

        // Update menu
        Vector<String> v = jdgaut.getFiles();
        JMenu menu = jmenubarturtle.getJMenuGraph();
        menu.removeAll();
        String s;
        for (int i = 0; i < v.size(); i++) {
            s = v.elementAt(i);
            jmenubarturtle.addMenuItem(menu, s, this);
        }

    }

    public void generateAUTS() {
        JDialogGenAUTS jdgauts = new JDialogGenAUTS(frame, this, "Generation of automata via LOTOS", gtm.getPathCaesar(),
                GTURTLEModeling.getPathBcgio(), REMOTE_RTL_LOTOS_FILE, GTURTLEModeling.getCaesarHost(), SpecConfigTTool.TGraphPath);
        // jdgauts.setSize(450, 600);
        GraphicLib.centerOnParent(jdgauts, 450, 600);
        jdgauts.setVisible(true);

        // Update menu
        /*
         * Vector v = jdgauts.getFiles(); JMenu menu = jmenubarturtle.getJMenuGraph();
         * menu.removeAll(); String s; for(int i=0; i<v.size(); i++) { s =
         * (String)(v.elementAt(i)); jmenubarturtle.addMenuItem(menu, s, this); }
         */

    }
    
    
    
    
    
    
    

    public void avatarSimulation() {
        TraceManager.addDev("Avatar simulation");

        TURTLEPanel tdp = getCurrentTURTLEPanel();
        if (tdp instanceof TMLComponentDesignPanel) {
            boolean ret = gtm.generateFullAvatarFromTML();
            gtm.getAvatarSpecification().removeElseGuards();

            if (!ret) {
                return;
            }

        }

        if (gtm.getAvatarSpecification() == null) {
            TraceManager.addDev("Null avatar spec");
            return;
        }

        jfais = new JFrameAvatarInteractiveSimulation( /* frame, */ this, "Interactive simulation", gtm.getAvatarSpecification());
        jfais.setIconImage(IconManager.img9);
        // jfais.setSize(900, 600);
        GraphicLib.centerOnParent(jfais, 900, 600);
        jfais.setVisible(true);
        //jfais.repaint();
    }

    public void openCloseAvatarSimulationWindow() {
        if (jfais == null) {
            avatarSimulation();
        } else {
            jfais.setVisible(false);
            jfais = null;
        }
    }

    public void setSimulationTraceSelected(SimulationTrace _st) {
        JFrameAvatarInteractiveSimulation.SELECTED_SIMULATION_TRACE = _st;
        if (jfais != null) {
            jfais.updateSimulationTrace();
        }
    }

    public void avatarUPPAALVerification() {
        // TraceManager.addDev("Avatar uppaal fv");
        boolean result = gtm.generateUPPAALFromAVATAR(SpecConfigTTool.UPPAALCodeDirectory);
        if (result) {
            formalValidation(true);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "" + getCheckingErrors().size() + " errors, " + getCheckingWarnings().size()
                            + " warning(s). UPPAAL specification could NOT be generated",
                    "Translation to UPPAAL failed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    public void avatarProVerifVerification() {
        boolean limit = true;
        TraceManager.addDev("AVATAR PROVERIF FV");
        TURTLEPanel tp = this.getCurrentTURTLEPanel();
        AvatarDesignPanel adp = null;
        if (tp instanceof AvatarDesignPanel) {
            adp = (AvatarDesignPanel) tp;
            limit = false;
        }
        JDialogProverifVerification jgen = new JDialogProverifVerification(frame, this, "Security verification with ProVerif",
                ConfigurationTTool.ProVerifVerifierHost, SpecConfigTTool.ProVerifCodeDirectory, ConfigurationTTool.ProVerifVerifierPath, adp, limit,
                gtm.getCPUTaskMap());
        // jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen, 600, 800);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void dse() {
        // TraceManager.addDev("Design space exploration");
        JDialogDSE jdse = new JDialogDSE(frame, this, "Design Space Exploration", SpecConfigTTool.SystemCCodeDirectory,
                SpecConfigTTool.TMLCodeDirectory);
        // jdse.setSize(600,800);
        GraphicLib.centerOnParent(jdse, 700, 800);
        jdse.setVisible(true);
        dtree.toBeUpdated();
    }

    public void dseZ3() {
        // TraceManager.addDev("Design space exploration with Z3");
        if (gtm == null) {
            return;
        }

        TMLMapping<TGComponent> map = gtm.getTMLMapping();

        if (map == null) {
            return;
        }

        JDialogDSEZ3 jdsez3 = new JDialogDSEZ3(frame, this, "Design Space Exploration with Z3", map, SpecConfigTTool.TMLCodeDirectory);
        // jdsez3.setSize(600,800);
        GraphicLib.centerOnParent(jdsez3, 700, 800);
        jdsez3.setVisible(true);
        dtree.toBeUpdated();
    }

    public void removeNoC() {
        removeNoC(false);
    }

    public void removeNoC(boolean automatic) {
        // TraceManager.addDev("Design space exploration with Z3");
        if (gtm == null) {
            return;
        }

        TMLMapping map = gtm.getTMLMapping();

        if (map == null) {
            return;
        }

        JDialogNoCManagement jdsenm = new JDialogNoCManagement(frame, this, "Removing NoC", map);
        // jdsez3.setSize(600,800);
        GraphicLib.centerOnParent(jdsenm, 700, 800);
        if (!automatic) {
            jdsenm.setVisible(true);
        } else {
            jdsenm.startProcess();
        }
        dtree.toBeUpdated();
    }

    public void avatarStaticAnalysis() {
        TraceManager.addDev("Avatar static analysis invariants");
        JDialogInvariantAnalysis jgen = new JDialogInvariantAnalysis(frame, this, "Static analysis: invariants computation");
        // jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen, 500, 450);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void avatarExecutableCodeGeneration() {
        TraceManager.addDev("Avatar code generation");
        JDialogAvatarExecutableCodeGeneration jgen = new JDialogAvatarExecutableCodeGeneration(frame, this,
                "Executable Code generation, compilation and execution", ConfigurationTTool.AVATARExecutableCodeHost,
                SpecConfigTTool.AVATARExecutableCodeDirectory, SpecConfigTTool.AVATARExecutableCodeCompileCommand,
                SpecConfigTTool.AVATARExecutableCodeExecuteCommand, ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand,
                ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand, ConfigurationTTool.AVATARExecutableSoclibTraceFile);
        // jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen, 800, 800);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    // DG
    public void avatarddExecutableCodeGeneration() {
        TraceManager.addDev("Avatar code generation");
        JDialogAvatarddExecutableCodeGeneration jgen = new JDialogAvatarddExecutableCodeGeneration(frame, this,
                "Executable Code generation, compilation and execution", ConfigurationTTool.AVATARExecutableCodeHost,
                ConfigurationTTool.AVATARMPSoCCodeDirectory, ConfigurationTTool.AVATARMPSoCCompileCommand,
                ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand);
        // jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen, 500, 450);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    // ---------------------------------------------------------------------

    public void generateUPPAAL() {
        generateUPPAAL(true);
    }

    public void generateUPPAAL(boolean showWindow) {
        TraceManager.addDev("Generate UPPAAL! showwindow=" + showWindow);
        // gtm.mergeChoices(true);
        if (gtm.getTURTLEModelingState() > 0) {
            // TraceManager.addDev("4173");
            if (gtm.getTURTLEModelingState() == 3) {
                // AVATAR
                boolean result = gtm.generateUPPAALFromAVATAR(SpecConfigTTool.UPPAALCodeDirectory);
                TraceManager.addDev("4177");
                if (showWindow) {
                    TraceManager.addDev("4178");
                    if (result) {
                        JOptionPane.showMessageDialog(frame,
                                "0 error, " + getCheckingWarnings().size() + " warning(s). UPPAAL specification generated",
                                "Successful translation to UPPAAL", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "" + getCheckingErrors().size() + " errors, " + getCheckingWarnings().size()
                                        + " warning(s). UPPAAL specification could NOT be generated",
                                "Translation to UPPAAL failed", JOptionPane.INFORMATION_MESSAGE);

                    }
                }
                TraceManager.addDev("4196");
                if (!result) {
                    return;
                }
            } else {
                /*
                 * if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false,
                 * UPPAAL) == -1) { TraceManager.addDev("4202 - UPPAAL generation failed");
                 * return; }
                 */
                TraceManager.addDev("About to open the window at line 4198");
                if (showWindow) {
                    TURTLEPanel tp = getCurrentTURTLEPanel();
                    boolean result = false;

                    if ((tp instanceof TMLDesignPanel) || (tp instanceof TMLComponentDesignPanel)) {
                        result = gtm.generateUPPAALFromTML(SpecConfigTTool.UPPAALCodeDirectory, false, 1024, true);
                    }
                    if (result != false) {
                        formalValidation();
                    }
                    /*
                     * JDialogUPPAALGeneration jgen = new JDialogUPPAALGeneration(frame, this,
                     * "UPPAAL code generation", ConfigurationTTool.UPPAALCodeDirectory,
                     * JDialogUPPAALGeneration.DIPLODOCUS_MODE); // jgen.setSize(450, 500);
                     * GraphicLib.centerOnParent(jgen, 450, 500); jgen.setVisible(true);
                     */

                }
                return;
            }
        }

        TraceManager.addDev("gtm.getTURTLEModelingState() <= 0)");
        // TraceManager.addDev("After UPPAAL");
        if (showWindow) {
            JDialogUPPAALGeneration jgen = new JDialogUPPAALGeneration(frame, this, "UPPAAL code generation", SpecConfigTTool.UPPAALCodeDirectory,
                    JDialogUPPAALGeneration.TURTLE_MODE);
            // jgen.setSize(450, 600);
            GraphicLib.centerOnParent(jgen, 450, 600);
            jgen.setVisible(true);
            // dtree.toBeUpdated();
        }
    }


    public void avatarModelChecker() {
        TraceManager.addDev("Execute avatar model checker");
        TURTLEPanel tdp = getCurrentTURTLEPanel();
        if (tdp instanceof TMLComponentDesignPanel) {
            boolean ret = gtm.generateFullAvatarFromTML();

            if (!ret) {
                return;
            }

        }

        if (gtm.getAvatarSpecification() == null) {
            TraceManager.addDev("Null avatar spec");
            return;
        }
        JDialogAvatarModelChecker jmc = new JDialogAvatarModelChecker(frame, this, "Avatar: Model Checking", gtm.getAvatarSpecification(),
                SpecConfigTTool.TGraphPath, experimentalOn);
        // jmc.setSize(550, 600);
        GraphicLib.centerOnParent(jmc, 800, 850);
        jmc.setVisible(true);
    }

    public List<String> generateAllAUT(String path) {
        return gtm.generateAUT(path);
    }

    public List<String> generateAllLOTOS(String path) {
        return gtm.generateLOTOSAUT(path);
    }

    public void generateJava() {

        if (gtm.getTURTLEModelingState() == 1) {
            if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false, JAVA) == -1) {
                return;
            }
        }

        JDialogJavaGeneration jgen = new JDialogJavaGeneration(frame, this, "Java code generation and compilation",
                ConfigurationTTool.JavaCodeDirectory, ConfigurationTTool.JavaCompilerPath, ConfigurationTTool.TToolClassPath,
                ConfigurationTTool.JavaExecutePath, ConfigurationTTool.JavaHeader);
        // jgen.setSize(450, 600);
        GraphicLib.centerOnParent(jgen, 450, 600);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void simuJava() {
        /*
         * TraceManager.addDev("Generate Java"); gtm.generateJava("");
         * JOptionPane.showMessageDialog(frame, "Java code generated", "Java generator",
         * JOptionPane.INFORMATION_MESSAGE); dtree.toBeUpdated();
         */

        JDialogJavaSimulation jgen = new JDialogJavaSimulation(frame, this, "Java simulation", ConfigurationTTool.SimuJavaCodeDirectory,
                ConfigurationTTool.JavaCompilerPath, ConfigurationTTool.TToolSimuClassPath, ConfigurationTTool.JavaExecutePath);
        // jgen.setSize(450, 600);
        GraphicLib.centerOnParent(jgen, 450, 600);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void generateSystemC() {
        generateSystemC(0);
    }

    // Modes are defined in JDialogSystemCGeneration "automatic modes"
    public void generateSystemC(int _mode) {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp instanceof AvatarDesignPanel) {
            avatarSimulation();
        } else if ((tp instanceof TMLDesignPanel) || (tp instanceof TMLComponentDesignPanel) || (tp instanceof TMLArchiPanel)) {
            JDialogSystemCGeneration jgen = new JDialogSystemCGeneration(frame, this, "Simulation Code Generation and Compilation",
                    ConfigurationTTool.SystemCHost, SpecConfigTTool.SystemCCodeDirectory, SpecConfigTTool.SystemCCodeCompileCommand,
                    SpecConfigTTool.SystemCCodeExecuteCommand, SpecConfigTTool.SystemCCodeExecuteXCycle,
                    SpecConfigTTool.SystemCCodeInteractiveExecuteCommand, SpecConfigTTool.GGraphPath, _mode);
            // jgen.setSize(500, 750);
            GraphicLib.centerOnParent(jgen, 700, 750);
            jgen.setVisible(true);
            dtree.toBeUpdated();

            /*
             * if (jgen.isInteractiveSimulationSelected() && (mode == 0)) {
             * interactiveSimulationSystemC(jgen.getPathInteractiveExecute()); }
             */
        }
    }

    public void interactiveSimulationSystemC() {
        interactiveSimulationSystemC(SpecConfigTTool.SystemCCodeInteractiveExecuteCommand + " -gpath " + SpecConfigTTool.GGraphPath);
    }

    public JFrameInteractiveSimulation getJfis() {
        if (ConfigurationTTool.SystemCHost == null || ConfigurationTTool.SystemCHost.equals("")) {
            ConfigurationTTool.SystemCHost = "localhost";
        }
        interactiveSimulationSystemC();
        return this.jfis;
    }

    public void interactiveSimulationSystemC(String executePath) {
        TraceManager.addDev("MainGUI / Execute path=" + executePath);
        List<Point> points = getListOfBreakPoints();

        if (gtm == null) {
            jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, null, points);
        } else {
            // TraceManager.addDev("toto1");
            if (gtm.getTMLMapping() != null) {
                jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath,
                        gtm.getTMLMapping(), points);
            } else {
                // TraceManager.addDev("toto2");
                if (gtm.getArtificialTMLMapping() != null) {
                    jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath,
                            gtm.getArtificialTMLMapping(), points);
                } else {
                    // TraceManager.addDev("toto3");
                    jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, null,
                            points);
                }
            }
        }

        jfis.setIconImage(IconManager.img9);
        // jfis.setSize(1024, 900);
        GraphicLib.centerOnParent(jfis, 1024, 900);
        jfis.setVisible(true);
    }

    public void addBreakPoint(int commandId) {
        if (jfis != null) {
            jfis.addBreakPoint(commandId);
        }
    }

    public void removeBreakPoint(int commandId) {
        if (jfis != null) {
            jfis.removeBreakPoint(commandId);
        }
    }

    // Sent by simulation interface
    public void removeBreakpoint(Point p) {
        if (gtm != null) {
            gtm.removeBreakpoint(p);
            getCurrentTDiagramPanel().repaint();
        }
    }

    // Sent by simulation interface
    public void addBreakpoint(Point p) {
        if (gtm != null) {
            gtm.addBreakpoint(p);
            getCurrentTDiagramPanel().repaint();
        }
    }

    public ArrayList<Point> getListOfBreakPoints() {
        ArrayList<Point> points = new ArrayList<Point>();
        TURTLEPanel tp;

        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                tp = tabs.elementAt(i);
                if (tp instanceof TMLDesignPanel) {
                    ((TMLDesignPanel) tp).getListOfBreakPoints(points);
                }
                if (tp instanceof TMLComponentDesignPanel) {
                    ((TMLComponentDesignPanel) tp).getListOfBreakPoints(points);
                }
            }
        }
        return points;
    }

    // Returns null if failed
    // Otherwise the path of the generated file
    public String generateTMLTxt() {
        String path = ConfigurationTTool.FILEPath;
        if (file != null) {
            path = file.getAbsolutePath();
        }
        TraceManager.addDev("Generating TML code: " + file.getAbsolutePath());
        if (gtm.generateTMLTxt(path)) {
            TraceManager.addDev("Done TML generation");
            return ConfigurationTTool.TMLCodeDirectory;
        }

        return null;
    }

    public String generateCCode() {

        // String path = ConfigurationTTool.FILEPath;
        // if( file != null ) {
        // path = file.getAbsolutePath();
        // }
        JDialogCCodeGeneration jgen = new JDialogCCodeGeneration(frame, this, "Application code generation and compilation",
                SpecConfigTTool.CCodeDirectory, "make -C " + SpecConfigTTool.CCodeDirectory, gtm);
        // jgen.setSize(500, 750);
        GraphicLib.centerOnParent(jgen, 500, 750);
        jgen.setVisible(true);
        // dtree.toBeUpdated();
        // gtm.generateCCode( path );
        return null;
    }

    public void generateDesign() {
        if (gtm.getTURTLEModelingState() == 1) {
            if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false, DESIGN) == -1) {
                return;
            }
        }

        // TraceManager.addDev("Generate design");
        gtm.generateDesign();
    }

    public void saveSIM() {
        gtm.saveSIM(simfile);
    }

    public void saveDTA() {
        gtm.saveDTA(dtafile);
    }

    public void saveDTADOT() {
        gtm.saveDTADOT(dtadotfile);
    }

    public void saveRG() {
        gtm.saveRG(rgfile);
    }

    public void saveTLSA() {
        gtm.saveTLSA(tlsafile);
    }

    public String saveRGAut() {
        gtm.saveRGAut(rgautfile);
        return rgautfile.getAbsolutePath();
    }

    public String saveFC2(String data) {
        gtm.saveInFile(fc2file, data);
        return fc2file.getAbsolutePath();
    }

    public String saveBCG(String data) {
        gtm.saveInFile(bcgfile, data);
        return bcgfile.getAbsolutePath();
    }

    public void saveRGDOT() {
        gtm.saveRGDOT(rgdotfile);
    }

    public void saveTLSADOT() {
        gtm.saveTLSADOT(tlsadotfile);
    }

    public String saveRGAutDOT() {
        gtm.saveRGAutDOT(rgautdotfile);
        return rgautdotfile.getAbsolutePath();
    }

    public void saveRGAutProj() {
        gtm.saveRGAutProj(rgautprojfile);
    }

    public void saveRGAutProjDOT() {
        gtm.saveRGAutProjDOT(rgautprojdotfile);
    }

    public void checkCode() {
        if (gtm.getLanguageID() == GTURTLEModeling.RT_LOTOS) {
            JDialogTextProcess jdtp = new JDialogTextProcess(frame, "Checking RT-LOTOS specification with RTL",
                    gtm.getPathRTL() + " __FILENAME -max-spec-t-1", REMOTE_RTL_LOTOS_FILE, gtm.getLastRTLOTOSSpecification(), gtm.getHost());
            // jdtp.setSize(450, 600);
            GraphicLib.centerOnParent(jdtp, 450, 600);
            jdtp.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {
            JDialogLOTOSAnalysis jdla = new JDialogLOTOSAnalysis(frame, this, "Checking LOTOS specification with CAESAR", gtm.getPathCaesar(),
                    REMOTE_RTL_LOTOS_FILE, gtm.getLastRTLOTOSSpecification(), GTURTLEModeling.getCaesarHost());
            // jdla.setSize(450, 600);
            GraphicLib.centerOnParent(jdla, 450, 600);
            jdla.setVisible(true);
            dtree.toBeUpdated();
        }
    }

    public void simulation() {
        if (gtm.getLanguageID() == GTURTLEModeling.RT_LOTOS) {
            JDialogSimulation jds = new JDialogSimulation(frame, this, "Intensive simulation with RTL", gtm.getPathRTL(), REMOTE_RTL_LOTOS_FILE,
                    gtm.getLastRTLOTOSSpecification(), gtm.getHost());
            // jds.setSize(450, 600);
            GraphicLib.centerOnParent(jds, 450, 600);
            jds.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {

        }
    }

    public void formalValidation() {
        formalValidation(false);
    }

    public void formalValidation(boolean automatic) {
        formalValidation(automatic, getCurrentTURTLEPanel());
    }

    public boolean formalValidation(boolean automatic, String diagramName) {
        TURTLEPanel tp = getTURTLEPanel(diagramName);
        if (tp != null) {
            formalValidation(automatic, tp);
            return true;
        }
        return false;
    }

    public void formalValidation(boolean automatic, TURTLEPanel _tp) {
        if (gtm.getLanguageID() == GTURTLEModeling.RT_LOTOS) {
            JDialogFormalValidation jdfv = new JDialogFormalValidation(frame, this, "Formal Validation with RTL", gtm.getPathRTL(),
                    gtm.getPathDTA2DOT(), gtm.getPathRGSTRAP(), gtm.getPathRG2TLSA(), REMOTE_RTL_LOTOS_FILE, gtm.getLastRTLOTOSSpecification(),
                    gtm.getHost(), GTURTLEModeling.getHostAldebaran(), GTURTLEModeling.getPathBcgio());
            jdfv.setAutomatic(automatic);
            // jdfv.setSize(450, 600);
            GraphicLib.centerOnParent(jdfv, 450, 600);
            jdfv.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {
            JDialogLOTOSValidation jdla = new JDialogLOTOSValidation(frame, this, "Generating RG with CAESAR", gtm.getPathCaesar(),
                    gtm.getPathCaesarOpen(), GTURTLEModeling.getPathBcgio(), gtm.getPathBcgmerge(), REMOTE_RTL_LOTOS_FILE,
                    gtm.getLastRTLOTOSSpecification(), GTURTLEModeling.getCaesarHost());
            jdla.setAutomatic(automatic);
            // jdla.setSize(450, 600);
            GraphicLib.centerOnParent(jdla, 450, 600);
            jdla.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.UPPAAL) {
            JDialogUPPAALValidation jduv = new JDialogUPPAALValidation(frame, this, "Formal Verification with UPPAAL", gtm.getPathUPPAALVerifier(),
                    gtm.getPathUPPAALFile(), REMOTE_UPPAAL_FILE, gtm.getLastUPPAALSpecification().getStringSpec(), gtm.getUPPAALVerifierHost(), _tp);
            // jduv.setSize(450, 600);
            GraphicLib.centerOnParent(jduv, 800, 600);
            jduv.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.MATRIX) {
            JDialogTMatrixManagement jdfv = new JDialogTMatrixManagement(frame, this, "Observers's Based Formal Verification",
                    gtm.getRequirementModeling(), gtm.getPathRTL(), gtm.getPathDTA2DOT(), gtm.getPathRGSTRAP(), gtm.getPathRG2TLSA(),
                    REMOTE_RTL_LOTOS_FILE, gtm.getHost(), GTURTLEModeling.getHostAldebaran(), GTURTLEModeling.getPathBcgio());
            // jdfv.setSize(550, 600);
            jdfv.setIconImage(IconManager.img8);
            GraphicLib.centerOnParent(jdfv, 550, 600);
            jdfv.setVisible(true);
            dtree.toBeUpdated();
        }
    }

    public void projection() {
        TClassDiagramPanel tcdp = null;
        if (getCurrentTURTLEPanel() instanceof DesignPanel) {
            tcdp = ((DesignPanel) (getCurrentTURTLEPanel())).tcdp;
        }
        JDialogProjection jdfv = new JDialogProjection(frame, this, tcdp, gtm.getTURTLEModeling(), GTURTLEModeling.getHostAldebaran(),
                GTURTLEModeling.getPathAldebaran(), gtm.getPathBcgmin(), GTURTLEModeling.getPathBcgio(), gtm.getLastRGAUT(),
                REMOTE_ALDEBARAN_AUT_FILE, "Minimization using Aldebaran");
        // jdfv.setSize(900, 700);
        GraphicLib.centerOnParent(jdfv, 900, 700);
        jdfv.setVisible(true);
    }

    public void modifyGraph() {
        JDialogGraphModification jdgm;
        if (gtm == null) {
            jdgm = new JDialogGraphModification(frame, GTURTLEModeling.getHostAldebaran(), GTURTLEModeling.getPathBcgio(), "graph",
                    "Minimization using Aldebaran", null, null);
        } else {
            jdgm = new JDialogGraphModification(frame, GTURTLEModeling.getHostAldebaran(), GTURTLEModeling.getPathBcgio(), "graph",
                    "Minimization using Aldebaran", gtm.getLastRGAUT(), gtm.getLastTextualRGAUTProj());
        }
        // jdgm.setSize(600, 500);
        GraphicLib.centerOnParent(jdgm, 600, 500);
        jdgm.setVisible(true);
        modifiedaut = jdgm.getGraphAUT();
        modifiedautdot = jdgm.getGraphDOT();
        if (modifiedautdot != null) {
            actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUTMODIFIED].setEnabled(true);
        }
        // gtm.modifyMinimizedGraph();
    }

    public void bisimulation() {
        JDialogBisimulation jdb = new JDialogBisimulation(frame, GTURTLEModeling.getHostAldebaran(), GTURTLEModeling.getPathAldebaran(),
                REMOTE_ALDEBARAN_BISIMU_FILE1, REMOTE_ALDEBARAN_BISIMU_FILE2, "Bisimulation using Aldebaran");
        // jdb.setSize(650, 800);
        GraphicLib.centerOnParent(jdb, 650, 800);
        jdb.setVisible(true);
        // TraceManager.addDev("Bisimulation");
    }

    public void bisimulationCADP() {
        JDialogBisimulationBisimulator jdb = new JDialogBisimulationBisimulator(frame, GTURTLEModeling.getCaesarHost(),
                GTURTLEModeling.getPathBisimulator(), GTURTLEModeling.getPathBcgio(), REMOTE_BISIMULATOR_FILE1, REMOTE_BISIMULATOR_FILE2,
                "Bisimulation using BISIMULATOR");
        // jdb.setSize(650, 800);
        GraphicLib.centerOnParent(jdb, 650, 800);
        jdb.setVisible(true);
        // TraceManager.addDev("Bisimulation");
    }

    public void seekDeadlockAUT() {
        String dataAUT = gtm.getLastTextualRGAUT();

        JFrameDeadlock jfd = new JFrameDeadlock("Potential deadlocks", dataAUT);
        jfd.setIconImage(IconManager.img8);
        // jfd.setSize(600, 600);
        GraphicLib.centerOnParent(jfd, 600, 600);
        jfd.setVisible(true);
    }

    public void seekDeadlockSavedAUT() {
        String graph[] = loadAUTGraph();
        if (graph != null) {
            JFrameDeadlock jfd = new JFrameDeadlock("Potential deadlocks on " + graph[0], graph[1]);
            jfd.setIconImage(IconManager.img8);
            // jfd.setSize(600, 600);
            GraphicLib.centerOnParent(jfd, 600, 600);
            jfd.setVisible(true);
        }
    }

    public void showAUTFromString(String title, String data) {
        /*
         * JFrameStatistics jfs = new JFrameStatistics(title, data);
         * jfs.setIconImage(IconManager.img8); jfs.setSize(600, 600);
         * GraphicLib.centerOnParent(jfs); jfs.setVisible(true);
         */
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, data, "Analyzing graph... Please wait", null, null, true);
        t.go();
    }

    public void showAUTFromGraph(String title, AUTGraph graph) {
        /*
         * JFrameStatistics jfs = new JFrameStatistics(title, data);
         * jfs.setIconImage(IconManager.img8); jfs.setSize(600, 600);
         * GraphicLib.centerOnParent(jfs); jfs.setVisible(true);
         */
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, null, "Analyzing graph... Please wait", graph, null, true);
        t.go();
    }

    public void showAUT(String title, String data, AUTGraph graph) {
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, data, "Analyzing graph... Please wait", graph, null, true);
        t.go();
    }

    public void showAUTFromRG(String title, RG rg) {
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, rg.data, "Analyzing graph... Please wait", rg.graph, rg, true);
        t.go();
    }

    public void displayAUTFromRG(String title, RG rg) {
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, rg.data, "Analyzing graph... Please wait", rg.graph, rg, false);
        t.go();
    }

    public void showPMAUT(String title, String data) {
        TraceManager.addDev("Power management analysis");
        JFramePowerManagementAnalysis jfpma = new JFramePowerManagementAnalysis(title, data);
        jfpma.setIconImage(IconManager.img8);
        // jfpma.setSize(600, 600);
        GraphicLib.centerOnParent(jfpma, 600, 600);
        jfpma.setVisible(true);
    }

    public void NC() {
        TraceManager.addDev("NC");
        JFrameNC jfnc = new JFrameNC("Network Calculus", gtm.getNCS());
        jfnc.setIconImage(IconManager.img8);
        // jfnc.setSize(600, 600);
        GraphicLib.centerOnParent(jfnc, 600, 600);
        jfnc.setVisible(true);
        TraceManager.addDev("Done");

        /*
         * JFrameStatistics jfs = new JFrameStatistics(title, data);
         * jfs.setIconImage(IconManager.img8); jfs.setSize(600, 600);
         * GraphicLib.centerOnParent(jfs); jfs.setVisible(true);
         */
    }

    public void statAUT() {
        showAUTFromString("Analysis on the last RG (AUT format)", gtm.getLastTextualRGAUT());
    }

    public RG setLastRGDiplodocus(String graphName) {
        TraceManager.addDev("setting last RG diplodocus");
        // lastDiploRG = graphName;
        // Loadng the graph
        // Adding RG to the tree on the left
        try {
            String fileName = SpecConfigTTool.TGraphPath + "/" + graphName + ".aut";
            File f = new File(fileName);
            String spec = loadFile(f);
            RG rg = new RG(graphName);
            rg.data = spec;
            // rg.nbOfStates = amc.getNbOfStates();
            // rg.nbOfTransitions = amc.getNbOfLinks();
            addRG(rg);
            lastDiploRG = rg;
            return rg;
        } catch (Exception e) {
            TraceManager.addDev("RG creation in the left tree failed");
            return null;
        }
    }

    public void statAUTDiplodocus() {
        if (lastDiploRG == null) {
            JOptionPane.showMessageDialog(frame, "The file could not be loaded:", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showAUTFromRG(lastDiploRG.name, lastDiploRG);
        // String spec = loadFile(new File(ConfigurationTTool.TGraphPath + "/" +
        // lastDiploRG + ".aut"));

        // showAUTFromString("Analysis on the last DIPLODOCUS RG", spec);
    }

    public void statAUTProj() {
        showAUTFromString("Analysis on the last minimized RG (A>UT format)", gtm.getLastTextualRGAUTProj());
    }

    public void statSavedAUT() {
        // TraceManager.addDev("toto");
        String graph[] = loadAUTGraph();
        if (graph != null) {
            showAUTFromString("Analysis on " + graph[0], graph[1]);
        }
    }

    public void pmAUT() {
        showPMAUT("Power Management Analysis on the last RG (AUT format)", gtm.getLastTextualRGAUT());
    }

    public void pmAUTProj() {
        showPMAUT("Power Management Analysis on the last minimized RG (AUT format)", gtm.getLastTextualRGAUTProj());
    }

    public void pmSavedAUT() {
        // TraceManager.addDev("toto");
        String graph[] = loadAUTGraph();
        if (graph != null) {
            showPMAUT("Power Management Analysis on " + graph[0], graph[1]);
        }
    }

    public void showFormalSpecification() {
        if (gtm.getLanguageID() == GTURTLEModeling.RT_LOTOS) {
            showFormalSpecification("RT-LOTOS Specification #" + gtm.getNbRTLOTOS(), gtm.getLastRTLOTOSSpecification());
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {
            showFormalSpecification("LOTOS Specification #" + gtm.getNbRTLOTOS(), gtm.getLastRTLOTOSSpecification());
        } else if (gtm.getLanguageID() == GTURTLEModeling.PROVERIF) {
            showFormalSpecification("Last ProVerif Specification", gtm.getLastProVerifSpecification());
        }
    }

    public void showFormalSpecification(String title, String data) {
        JFrameText jft = new JFrameText(title, data);
        jft.setIconImage(IconManager.img8);
        // jft.setSize(600, 600);
        GraphicLib.centerOnParent(jft, 600, 600);
        jft.setVisible(true);
    }

    public void showJavaCode() {
        if (javaframe == null) {
            javaframe = new JFrameCode("JavaCode", "", "");
            javaframe.setIconImage(IconManager.img8);
            // javaframe.setSize(350, 350);
            GraphicLib.centerOnParent(javaframe, 350, 350);
            javaframe.setVisible(true);
        } else {
            javaframe.setVisible(true);
        }
    }

    public void showBirdEyesView() {
        if (birdframe == null) {
            birdframe = new JFrameBird(this);
            birdframe.setIconImage(IconManager.img8);
            // birdframe.setSize(150, 100);
            GraphicLib.centerOnParent(birdframe, 150, 100);
            birdframe.setVisible(true);
        } else {
            birdframe.setVisible(true);
        }
    }

    public void showEmbeddedBirdEyesView() {
        // TraceManager.addDev("Embedded!");
        if (jbp.getGo()) {
            jbp.setGo(false);
        } else {
            jbp.startProcess();
        }
    }

    public void drawBird() {
        if (jbp.getGo()) {
            jbp.repaint();
        }

        if (birdframe != null) {
            birdframe.updatePanel();
        }
    }

    public void unsetBirdFrame() {
        birdframe = null;
    }

    public void setJavaPreCode(TGComponent tgc) {
        if (javaframe != null) {
            if (tgc == null) {
                javaframe.setPreCode("");
                return;
            }
            if (tgc.hasPreJavaCode()) {
                javaframe.setPreCode(tgc.getPreJavaCode());
            } else {
                javaframe.setPreCode("");
            }
        }
    }

    public void setJavaPostCode(TGComponent tgc) {
        if (javaframe != null) {
            if (tgc == null) {
                javaframe.setPostCode("");
                return;
            }
            if (tgc.hasPostJavaCode()) {
                javaframe.setPostCode(tgc.getPostJavaCode());
            } else {
                javaframe.setPostCode("");
            }
        }
    }

    public void showSuggestedDesign() {
        JFrameText jft = new JFrameText("Suggested Design #" + gtm.getNbSuggestedDesign(), gtm.getLastTextualDesign());
        jft.setIconImage(IconManager.img8);
        // jft.setSize(600, 600);
        GraphicLib.centerOnParent(jft, 600, 600);
        jft.setVisible(true);
    }

    public void showSavedRTLOTOS() {
        String spec[] = loadLotosSpec();
        if ((spec != null) && (spec[0] != null) && (spec[1] != null)) {
            JFrameText jft = new JFrameText("RT-LOTOS Specification: " + spec[0], spec[1]);
            jft.setIconImage(IconManager.img8);
            // jft.setSize(600, 600);
            GraphicLib.centerOnParent(jft, 600, 600);
            jft.setVisible(true);
        }
    }

    public void showSimulationTrace() {
        gtm.showSIM(1);
    }

    public void showSimulationTraceChrono() {
        gtm.showSIM(2);
    }

    public void showDTA() {
        String s = gtm.showDTA();
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The DTA could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showWave() {
        RemoteExecutionThread ret = new RemoteExecutionThread(ConfigurationTTool.SystemCHost, null, null,
                ConfigurationTTool.GTKWavePath + " vcddump.vcd");
        ret.start();
    }

    public void showRG() {
        String s = gtm.showRG();
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The RG could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showTLSA() {
        String s = gtm.showTLSA();
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The TLSA could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGAut() {
        String s = gtm.showRGAut();
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The RG could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGAutProj() {
        String s = gtm.showRGAutProj();
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The RG could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGDiplodocus() {
        if (lastDiploRG == null) {
            JOptionPane.showMessageDialog(frame, "The RG was not yet generated", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TraceManager.addDev("Showing diplo RG");
        displayAUTFromRG(lastDiploRG.name, lastDiploRG);

    }

    public void runGTKWave(String pathToFile) {
        executeUserCommand("localhost", ConfigurationTTool.GTKWavePath + " " + pathToFile);
    }

    public void showModifiedAUTDOT() {
        String s = GTURTLEModeling.runDOTTY(modifiedautdot);
        if (s != null) {
            JOptionPane.showMessageDialog(frame, "The RG could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showGGraph() {
        String graph[] = loadGGraph();
        if (graph != null) {
            String s = GTURTLEModeling.showGGraph(graph[1]);
            if (s != null) {
                JOptionPane.showMessageDialog(frame, "The graph could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void viewAutomata(String file) {
        // TraceManager.addDev("viewing: " + file);
        String graph[] = loadGGraph(file);
        if (graph != null) {
            String s = GTURTLEModeling.showGGraph(graph[1]);
            if (s != null) {
                JOptionPane.showMessageDialog(frame, "The graph could not be displayed: " + s, "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void executeUserCommand(String host, String command) {
        RemoteExecutionThread ret = new RemoteExecutionThread(host, null, null, command);
        ret.start();
        TraceManager.addDev("User command ->" + command + "<- started on host " + host);
    }

    public void screenCapture() {
        // Select file
        File file = selectFileForCapture();
        if (file == null)
            return;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        performScreenCapture(screenRect, file);
    }

    public void windowCapture() {
        // Select file
        File file = selectFileForCapture();
        if (file == null)
            return;

        Rectangle screenRect = new Rectangle(frame.getLocation().x, frame.getLocation().y, frame.getWidth(), frame.getHeight());
        performScreenCapture(screenRect, file);
    }

    public void diagramCapture() {
        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame, "No diagram is under edition", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectFileForCapture();
        if (file == null)
            return;

        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            BufferedImage image = tdp1.performMinimalCapture();
            writeImageCapture(image, file, true);
        }
    }

    public void svgDiagramCapture() {

        TraceManager.addDev("SVG capture ");

        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame, "No diagram is under edition", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectSVGFileForCapture(true);

        if (file == null)
            return;

        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            String s = tdp1.svgCapture();
            try {
                FileUtils.saveFile(file.getAbsolutePath(), s);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(frame, "The capture was correctly performed and saved in " + file.getAbsolutePath(),
                    "Save in svg format ok", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void allDiagramCapture() {
        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame, "No diagram is under edition", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectFileForCapture();
        if (file == null)
            return;

        TURTLEPanel tp;// = getCurrentTURTLEPanel();
        TDiagramPanel tdp1;
        BufferedImage image;
        File file1;
        String name = file.getAbsolutePath();
        name = name.substring(0, name.length() - 4);

        // boolean actions;
        for (int j = 0; j < tabs.size(); j++) {
            tp = tabs.get(j);
            for (int i = 0; i < tp.panels.size(); i++) {
                tdp1 = tp.panels.elementAt(i);
                tdp1.repaint();
                image = tdp1.performMinimalCapture();
                if (i < 10) {
                    file1 = new File(name + "0" + i + "__" + tdp1.getName());
                } else {
                    file1 = new File(name + i + "__" + tdp1.getName());
                }
                file1 = FileUtils.addFileExtensionIfMissing(file1, TImgFilter.getExtension());
                if (!writeImageCapture(image, file1, false)) {
                    JOptionPane.showMessageDialog(frame, "Diagrams could NOT be captured in png format", "Capture failed",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (i == 0) {
                    if (!writeImageCapture(image, file, false)) {
                        JOptionPane.showMessageDialog(frame, "Diagrams could NOT be captured in png format", "Capture failed",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(frame, "All diagrams were sucessfully captured in png format", "Capture ok", JOptionPane.INFORMATION_MESSAGE);
    }

    public void allDiagramCaptureSvg() {
        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame, "No diagram is under edition", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectSVGFileForCapture(false);
        if (file == null)
            return;

        TURTLEPanel tp;// = getCurrentTURTLEPanel();
        TDiagramPanel tdp1;
        // BufferedImage image;
        File file1;
        String name = file.getAbsolutePath();
        name = name.substring(0, name.length() - 4);

        // boolean actions;
        for (int j = 0; j < tabs.size(); j++) {
            tp = tabs.get(j);
            for (int i = 0; i < tp.panels.size(); i++) {
                tdp1 = tp.panels.elementAt(i);
                tdp1.repaint();

                // tdp1.performMinimalCapture();
                String svgImg = tdp1.svgCapture();

                if (i < 10) {
                    file1 = new File(name + j + "_" + "0" + i + "__" + tdp1.getName());
                } else {
                    file1 = new File(name + j + "_" + i + "__" + tdp1.getName());
                }
                file1 = FileUtils.addFileExtensionIfMissing(file1, TSVGFilter.getExtension());
                try {
                    TraceManager.addDev("Saving in file:" + file1.getAbsolutePath());
                    FileUtils.saveFile(file1, svgImg);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Diagrams could NOT be captured in svg format", "Capture failed",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

            }
        }

        JOptionPane.showMessageDialog(frame, "All diagrams were sucessfully captured in svg format", "Capture ok", JOptionPane.INFORMATION_MESSAGE);
    }

    public void selectedCapture() {
        File file = selectFileForCapture();
        if (file == null)
            return;
        BufferedImage image = getCurrentTDiagramPanel().performSelectedCapture();
        writeImageCapture(image, file, true);
    }

    public File selectFileForCapture() {
        File file = null;
        jfcimg.setCurrentDirectory(new File(SpecConfigTTool.IMGPath));
        int returnVal = jfcimg.showSaveDialog(frame);
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcimg.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TImgFilter.getExtension());

        }
        if (!checkFileForSave(file)) {
            JOptionPane.showMessageDialog(frame, "The capture could not be performed: invalid file", "Error", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return file;
    }

    public File selectSVGFileForCapture(boolean checkForSave) {
        File file = null;
        jfcimgsvg.setCurrentDirectory(new File(SpecConfigTTool.IMGPath));
        int returnVal = jfcimgsvg.showSaveDialog(frame);
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return null;

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcimgsvg.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TSVGFilter.getExtension());

        }
        if (checkForSave) {
            if (!checkFileForSave(file)) {
                JOptionPane.showMessageDialog(frame, "The capture could not be performed: invalid file", "Error", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
        }
        return file;
    }

    public void performScreenCapture(Rectangle rect, File file) {
        frame.paint(frame.getGraphics());
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(rect);
            // save captured image to PNG file
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "The capture could not be performed:" + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(frame, "The capture was correctly performed", "Screen capture ok", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    public boolean writeImageCapture(BufferedImage image, File file, boolean info) {
        frame.paint(frame.getGraphics());
        try {
            // save captured image to PNG file
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "The capture could not be performed:" + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if (info) {
            JOptionPane.showMessageDialog(frame, "The capture was correctly performed and saved in " + file.getAbsolutePath(), "Screen capture ok",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    public void generateDocumentation() {
        // TraceManager.addDev("Documentation");
        ThreadGUIElement t = new ThreadGUIElement(frame, 1, tabs, mainTabbedPane, SpecConfigTTool.DocGenPath, file.getName(), "Documentation",
                "Generating documentation ... Please wait");
        t.go();
        /*
         * DocumentationGenerator docgen = new DocumentationGenerator(tabs,
         * mainTabbedPane, ConfigurationTTool.IMGPath, file.getName());
         * docgen.setFirstHeadingNumber(2); if (docgen.generateDocumentation()) {
         * JOptionPane.showMessageDialog(frame, "All done!", "Documentation generation",
         * JOptionPane.INFORMATION_MESSAGE); } else {
         * JOptionPane.showMessageDialog(frame,
         * "The documentation generation could not be performed", "Error",
         * JOptionPane.INFORMATION_MESSAGE); }
         */
        // TraceManager.addDev("Documentation=" + docgen.getDocumentation());
    }

    public void generateDocumentationReq() {
        TraceManager.addDev("Frame Req");

        // Repaint all requirement diags
        TURTLEPanel tp;
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AvatarRequirementPanel) {
                ((AvatarRequirementPanel) (tp)).updateReferences();
            }
        }

        JDialogRequirementTable jdrt = new JDialogRequirementTable(frame, "Selecting table columns");
        GraphicLib.centerOnParent(jdrt);
        jdrt.setVisible(true);
        if (!jdrt.hasBeenCancelled()) {
            Point[] pts = jdrt.getColumnsInfo();
            if (pts != null) {
                for (int i = 0; i < pts.length; i++) {
                    TraceManager.addDev("" + i + ": (" + pts[i].x + ", " + pts[i].y + ")");
                }

                JFrameRequirementTable jfrt = new JFrameRequirementTable("Requirement table", tabs, mainTabbedPane, pts);
                jfrt.setIconImage(IconManager.img8);
                // jfrt.setSize(1024, 768);
                GraphicLib.centerOnParent(jfrt, 1024, 768);
                jfrt.setVisible(true);
            } else {
                TraceManager.addDev("No column to print");
            }
        }
        TraceManager.addDev("Done");
    }

    public int getTypeButtonSelected() {
        return typeButtonSelected;
    }

    public Plugin getPluginSelected() {
        return pluginSelected;
    }

    public void actionOnButton(int type, int id) {
        typeButtonSelected = type;
        idButtonSelected = id;
        pluginSelected = null;
        // TDiagramPanel tdp1 =
        // ((TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()))).tdp;
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        // TraceManager.addDev("Selected TDiagramPanel=" + tdp1.getName());
        tdp1.repaint();
    }

    public void actionOnButton(int type, Plugin _p) {
        typeButtonSelected = type;
        idButtonSelected = TGComponentManager.COMPONENT_PLUGIN;
        pluginSelected = _p;
        // TDiagramPanel tdp1 =
        // ((TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()))).tdp;
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        // TraceManager.addDev("Selected TDiagramPanel=" + tdp1.getName());
        tdp1.repaint();
    }

    public int getIdButtonSelected() {
        return idButtonSelected;
    }

    public void addTClass(TURTLEPanel tp, String s) {
        if (!(tp instanceof DesignPanel)) {
            return;
        }

        ((DesignPanel) tp).addTActivityDiagram(s);
        setPanelMode();
    }

    public void addTOSClass(TURTLEPanel tp, String s) {
        if (!(tp instanceof TURTLEOSDesignPanel)) {
            return;
        }

        ((TURTLEOSDesignPanel) tp).addTURTLEOSActivityDiagram(s);
        setPanelMode();
    }

    public void addTMLTask(TURTLEPanel tp, String s) {
        // TraceManager.addDev("ADD TML Task=" + s);
        if (!(tp instanceof TMLDesignPanel)) {
            return;
        }

        ((TMLDesignPanel) tp).addTMLActivityDiagram(s);
        setPanelMode();
    }

    public void addTMLCPrimitiveComponent(TURTLEPanel tp, String s) {
        // TraceManager.addDev("ADD C Primitive Component=" + s);
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return;
        }

        ((TMLComponentDesignPanel) tp).addTMLActivityDiagram(s);
        setPanelMode();
    }

    public TMLActivityDiagramPanel getReferencedTMLActivityDiagramPanel(TDiagramPanel _tdp, String name) {
        TURTLEPanel tp;
        TMLActivityDiagramPanel tmladp;
        // TraceManager.addDev("global search for: " + name);
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                if (tp.hasTDiagramPanel(_tdp)) {
                    tmladp = ((TMLComponentDesignPanel) tp).getTMLActivityDiagramPanel(name);
                    if (tmladp != null) {
                        // TraceManager.addDev("Found");
                        return tmladp;
                    }
                }
            }
        }

        TraceManager.addDev("Not found");
        return null;
    }

    public TURTLEPanel getTURTLEPanelOfTDiagramPanel(TDiagramPanel _tdp) {
        TURTLEPanel tp;
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                if (tp.hasTDiagramPanel(_tdp)) {
                    return tp;

                }
            }
        }
        return null;
    }

    public ArrayList<EBRDDPanel> getAllEBRDDPanels() {
        TURTLEPanel tp;
        ArrayList<EBRDDPanel> al = new ArrayList<EBRDDPanel>();
        // TraceManager.addDev("global search for: " + name);
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof RequirementPanel) {
                ((RequirementPanel) tp).addAllEBRDDPanels(al);
            }
        }

        return al;
    }

    public ArrayList<AvatarPDPanel> getAllAvatarPDPanels() {
        TURTLEPanel tp;
        ArrayList<AvatarPDPanel> al = new ArrayList<AvatarPDPanel>();
        // TraceManager.addDev("global search for: " + name);
        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof AvatarRequirementPanel) {
                ((AvatarRequirementPanel) tp).addAllAvatarPDPanels(al);
            }
        }

        return al;
    }

    public List<TGComponent> getAllTMLComponents() {
        TURTLEPanel tp;
        List<TGComponent> ll = new LinkedList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);

            if (tp instanceof TMLComponentDesignPanel) {
                ll.addAll(((TMLComponentDesignPanel) tp).tmlctdp.getComponentList());
            }
        }

        return ll;
    }

    public List<TGComponent> getAllSysCAMSComponents() {
        TURTLEPanel tp;
        List<TGComponent> ll = new LinkedList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);

            if (tp instanceof SysCAMSComponentDesignPanel) {
                ll.addAll(((SysCAMSComponentDesignPanel) tp).syscamsctdp.getComponentList());
            }
        }

        return ll;
    }

    public List<TGComponent> getAllELNComponents() {
        TURTLEPanel tp;
        List<TGComponent> ll = new LinkedList<TGComponent>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);

            if (tp instanceof ELNDesignPanel) {
                ll.addAll(((ELNDesignPanel) tp).elndp.getComponentList());
            }
        }

        return ll;
    }

    public ArrayList<SysCAMSComponentTaskDiagramPanel> getAllPanelsReferencingSysCAMSCompositeComponent(SysCAMSCompositeComponent syscamscc) {
        TURTLEPanel tp;
        ArrayList<SysCAMSComponentTaskDiagramPanel> foundPanels = new ArrayList<SysCAMSComponentTaskDiagramPanel>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof SysCAMSComponentDesignPanel) {
                ((SysCAMSComponentDesignPanel) tp).syscamsctdp.getPanelsUsingAComponent(syscamscc, foundPanels);
            }
        }
        return foundPanels;
    }

    public void removeTClass(TURTLEPanel tp, String s) {
        if (!(tp instanceof DesignPanel)) {
            return;
        }

        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeTOSClass(TURTLEPanel tp, String s) {
        if (!(tp instanceof TURTLEOSDesignPanel)) {
            return;
        }

        TraceManager.addDev("Removing tab ...");
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeTMLTask(TURTLEPanel tp, String s) {
        if (!(tp instanceof TMLDesignPanel)) {
            return;
        }

        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeAvatarBlock(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarDesignPanel))
            return;

        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++)
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                this.setPanelMode();
                return;
            }
    }

    public void removeTMLCPrimitiveComponent(TURTLEPanel tp, String s) {
        // TraceManager.addDev("Removing panel 0:" + s);
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return;
        }

        // TraceManager.addDev("Removing panel 1:" + s);
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    // for diagrams
    public void setEditMode() {
        typeButtonSelected = TGComponentManager.EDIT;
        idButtonSelected = -1;
    }

    public String getTabName(TURTLEPanel p) {
        int index = tabs.indexOf(p);
        if (index < 0) {
            return "";
        }
        return mainTabbedPane.getTitleAt(index);
    }


    public int getIndexOfPanel(TURTLEPanel p) {
        return tabs.indexOf(p);
    }

    public int getMajorIndexOf(TDiagramPanel tdp) {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp.panels.contains(tdp)) {
                return i;
            }
        }

        return -1;
    }

    public String getMajorTitle(TDiagramPanel tdp) {
        int index = getMajorIndexOf(tdp);

        if (index == -1) {
            return "unknown";
        }

        return mainTabbedPane.getTitleAt(index);
    }

    public ArrayList<TMLComponentTaskDiagramPanel> getAllPanelsReferencingTMLCCompositeComponent(TMLCCompositeComponent tmlcc) {
        TURTLEPanel tp;
        ArrayList<TMLComponentTaskDiagramPanel> foundPanels = new ArrayList<TMLComponentTaskDiagramPanel>();

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel) tp).tmlctdp.getPanelsUsingAComponent(tmlcc, foundPanels);
            }
        }

        return foundPanels;
    }

    public void updateReferenceToTMLCCompositeComponent(TMLCCompositeComponent tmlcc) {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel) tp).tmlctdp.updateReferenceToTMLCCompositeComponent(tmlcc);
            }
        }
    }

    public void updateReferenceToSysCAMSCompositeComponent(SysCAMSCompositeComponent tmlcc) {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof SysCAMSComponentDesignPanel) {
                ((SysCAMSComponentDesignPanel) tp).syscamsctdp.updateReferenceToSysCAMSCompositeComponent(tmlcc);
            }
        }
    }

    public TMLCCompositeComponent getCompositeComponent(String name) {
        int index = name.indexOf("::");
        if (index == -1) {
            return null;
        }

        String panelName = name.substring(0, index);
        String componentName = name.substring(index + 2, name.length());

        TURTLEPanel tp = getTURTLEPanel(panelName);

        if ((tp == null) || (!(tp instanceof TMLComponentDesignPanel))) {
            return null;
        }

        return ((TMLComponentDesignPanel) (tp)).tmlctdp.getCompositeComponentByName(componentName);
    }

    public AvatarSMDPanel getAvatarSMDPanel(int indexDesign, String name) {

        TURTLEPanel tp = tabs.elementAt(indexDesign);
        if (tp == null) {
            TraceManager.addDev("null TP");
            return null;
        }
        if (tp instanceof AvatarDesignPanel) {
            return ((AvatarDesignPanel) tp).getAvatarSMDPanel(name);
        }
        TraceManager.addDev("null ADP :" + name);
        return null;
    }

    public TActivityDiagramPanel getActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        if (tp == null) {
            return null;
        }
        if (tp instanceof DesignPanel) {
            return ((DesignPanel) tp).getActivityDiagramPanel(name);
        }
        return null;
    }

    public TURTLEOSActivityDiagramPanel getTURTLEOSActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        if (tp == null) {
            return null;
        }
        if (tp instanceof TURTLEOSDesignPanel) {
            return ((TURTLEOSDesignPanel) tp).getTURTLEOSActivityDiagramPanel(name);
        }
        return null;
    }

    public TMLActivityDiagramPanel getTMLActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        if (tp == null) {
            return null;
        }

        if (tp instanceof TMLDesignPanel) {
            return ((TMLDesignPanel) tp).getTMLActivityDiagramPanel(name);
        }

        if (tp instanceof TMLComponentDesignPanel) {
            // TraceManager.addDev("Returning TMLComponentDesignPanel");
            return ((TMLComponentDesignPanel) tp).getTMLActivityDiagramPanel(name);
        }
        return null;
    }

    public ProactiveSMDPanel getSMDPanel(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        if (tp == null) {
            return null;
        }
        if (tp instanceof ProactiveDesignPanel) {
            return ((ProactiveDesignPanel) tp).getSMDPanel(name);
        }
        return null;
    }

    public int getNbActivityDiagram() {
        TDiagramPanel tdp;
        int cpt = 0;

        for (int i = 0; i < getCurrentJTabbedPane().getTabCount(); i++) {
            tdp = getCurrentTURTLEPanel().panels.elementAt(i);
            if (tdp instanceof TActivityDiagramPanel) {
                cpt++;
            }
        }
        return cpt;
    }

    public void setAvatarBDName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setClassDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setDiplodocusMethodologyDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setAvatarMethodologyDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setSystemCAMSDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setSysmlsecMethodologyDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setVerificationPropertyDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLTaskDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLComponentTaskDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setSysCAMSComponentTaskDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setELNDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLArchitectureDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setAADName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setProacticeCSDName(int indexDesign, String name) {
        TURTLEPanel tp = tabs.elementAt(indexDesign);
        tp.tabbedPane.setTitleAt(0, name);
    }

    public TDiagramPanel getMainTDiagramPanel(int indexPanel) {
        if (tabs.size() > indexPanel) {
            TURTLEPanel tp = tabs.elementAt(indexPanel);
            return tp.tdp;
        } else {
            return null;
        }

    }

    // TMLCP
    public boolean isTMLCPSDCreated(int index, String s) {
        return isTMLCPSDCreated(tabs.elementAt(index), s);
    }

    public boolean isTMLCPSDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof TMLSDPanel);
    }

    public boolean isTMLCPCreated(int index, String s) {
        return isTMLCPCreated(tabs.elementAt(index), s);
    }

    public boolean isTMLCPCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof TMLCPPanel);
    }

    // IOD, SD

    public boolean isSDCreated(int index, String s) {
        return isSDCreated(tabs.elementAt(index), s);
    }

    public boolean isIODCreated(int index, String s) {
        return isIODCreated(tabs.elementAt(index), s);
    }

    public boolean isProActiveSMDCreated(int index, String s) {
        return isProActiveSMDCreated(tabs.elementAt(index), s);
    }

    public boolean isSDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof ui.sd.SequenceDiagramPanel);
    }

    public boolean isSDCreatedZV(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof ui.sd2.SequenceDiagramPanel);
    }

    public boolean isSDZVCreated(int index, String s) {
        return isSDZVCreated(tabs.elementAt(index), s);
    }

    public boolean isSDZVCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof ui.sd2.SequenceDiagramPanel);
    }

    public boolean isUseCaseDiagramCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof UseCaseDiagramPanel);
    }

    public boolean isAvatarCDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof AvatarCDPanel);
    }

    public boolean isAvatarADCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof AvatarADPanel);
    }

    public boolean isIODCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof InteractionOverviewDiagramPanel);
    }

    public boolean isProActiveSMDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof ProactiveSMDPanel);
    }

    public boolean openSequenceDiagram(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

    public boolean openSequenceDiagramZV(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

    public boolean openIODiagram(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

    // TMLCP
    public boolean openTMLCPSequenceDiagram(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

    public boolean openTMLCPDiagram(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

    public boolean createTMLCPSequenceDiagram(int index, String s) {
        return createTMLCPSequenceDiagram(tabs.elementAt(index), s);
    }

    public boolean createTMLCPSequenceDiagram(TURTLEPanel tp, String s) {
        if (isSDCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof TMLCommunicationPatternPanel)) {
            return false;
        }

        ((TMLCommunicationPatternPanel) tp).addCPSequenceDiagram(s);

        setPanelMode();
        return true;
    }

    public boolean createUniqueTMLCPSequenceDiagram(TURTLEPanel tp, String s) {
        int i;
        for (i = 0; i < 1000; i++) {
            if (!isTMLCPSDCreated(tp, s + i)) {
                break;
            }
        }

        ((TMLCommunicationPatternPanel) tp).addCPSequenceDiagram(s + i);

        setPanelMode();
        return true;
    }

    public boolean createTMLCPDiagram(int index, String s) {
        return createTMLCPDiagram(tabs.elementAt(index), s);
    }

    public boolean createTMLCPDiagram(TURTLEPanel tp, String s) {
        if (isTMLCPCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof TMLCommunicationPatternPanel)) {
            return false;
        }

        ((TMLCommunicationPatternPanel) tp).addCPDiagram(s);
        setPanelMode();
        return true;
    }

    // End of TMLCP

    public ui.sd.SequenceDiagramPanel getSequenceDiagramPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getSequenceDiagramPanel(tp, s);
    }

    public ui.sd2.SequenceDiagramPanel getSequenceDiagramPanelZV(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getSequenceDiagramPanelZV(tp, s);
    }

    public AttackTreeDiagramPanel getAttackTreeDiagramPanel(int index, int indexTab, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getAttackTreeDiagramPanel(tp, indexTab, s);
    }

    public AttackTreeDiagramPanel getAttackTreeDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AttackTreeDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
        /*
         * for(int i=0; i<tp.tabbedPane.getTabCount(); i++) { if
         * (tp.tabbedPane.getTitleAt(indexTab).equals(s)) { if (tp.panelAt(i) instanceof
         * AttackTreeDiagramPanel) return (AttackTreeDiagramPanel)(tp.panelAt(i)); } }
         * return null;
         */
    }

    public FaultTreeDiagramPanel getFaultTreeDiagramPanel(int index, int indexTab, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getFaultTreeDiagramPanel(tp, indexTab, s);
    }

    public FaultTreeDiagramPanel getFaultTreeDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (FaultTreeDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
        /*
         * for(int i=0; i<tp.tabbedPane.getTabCount(); i++) { if
         * (tp.tabbedPane.getTitleAt(indexTab).equals(s)) { if (tp.panelAt(i) instanceof
         * AttackTreeDiagramPanel) return (AttackTreeDiagramPanel)(tp.panelAt(i)); } }
         * return null;
         */
    }

    public TMLCPPanel getTMLCPDiagramPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getTMLCPDiagramPanel(tp, s);
    }

    public TMLCPPanel getTMLCPDiagramPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof TMLCPPanel)
                    return (TMLCPPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public TMLSDPanel getTMLCPSDDiagramPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getTMLCPSDDiagramPanel(tp, s);
    }

    public TMLSDPanel getTMLCPSDDiagramPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof TMLSDPanel)
                    return (TMLSDPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public InteractionOverviewDiagramPanel getIODiagramPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getIODiagramPanel(tp, s);
    }

    public ui.sd.SequenceDiagramPanel getSequenceDiagramPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof ui.sd.SequenceDiagramPanel)
                    return (ui.sd.SequenceDiagramPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public ui.sd2.SequenceDiagramPanel getSequenceDiagramPanelZV(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof ui.sd2.SequenceDiagramPanel)
                    return (ui.sd2.SequenceDiagramPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public InteractionOverviewDiagramPanel getIODiagramPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof InteractionOverviewDiagramPanel)
                    return (InteractionOverviewDiagramPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public UseCaseDiagramPanel getUseCaseDiagramPanel(int index, int indexTab, String s) {
        // TraceManager.addDev("Searching for " + s + " at index =" + index);
        TURTLEPanel tp = tabs.elementAt(index);
        return getUseCaseDiagramPanel(tp, indexTab, s);
    }

    public UseCaseDiagramPanel getUseCaseDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (UseCaseDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public boolean createSequenceDiagram(int index, String s) {
        return createSequenceDiagram(tabs.elementAt(index), s);
    }

    public boolean createSequenceDiagram(TURTLEPanel tp, String s) {
        if (isSDCreated(tp, s)) {
            return false;
        }

        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addSequenceDiagram(s);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel) tp).addSequenceDiagram(s);
        }
        setPanelMode();
        return true;
    }

    public boolean createSequenceDiagramZV(int index, String s) {
        return createSequenceDiagramZV(tabs.elementAt(index), s);
    }

    public boolean createSequenceDiagramZV(TURTLEPanel tp, String s) {
        if (isSDCreated(tp, s)) {
            return false;
        }

        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addSequenceDiagramZV(s);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel) tp).addSequenceDiagramZV(s);
        }
        setPanelMode();
        return true;
    }

    public boolean createUniqueSequenceDiagram(TURTLEPanel tp, String s) {
        int i;
        for (i = 0; i < 1000; i++) {
            if (!isSDCreated(tp, s + i)) {
                break;
            }
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addSequenceDiagram(s + i);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel) tp).addSequenceDiagram(s + i);
        }

        setPanelMode();
        return true;
    }

    public boolean createUniqueSequenceDiagramZV(TURTLEPanel tp, String s) {
        int i;
        for (i = 0; i < 1000; i++) {
            if (!isSDCreatedZV(tp, s + i)) {
                break;
            }
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addSequenceDiagramZV(s + i);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel) tp).addSequenceDiagramZV(s + i);
        }

        setPanelMode();
        return true;
    }

    public boolean createSequenceDiagramFromUCD(TURTLEPanel tp, String s, UseCaseDiagramPanel _ucdp) {
        if (!createUniqueSequenceDiagramZV(tp, s)) {
            return false;
        }

        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addInstancesToLastSD(_ucdp);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel) tp).addInstancesToLastSD(_ucdp);
        }

        return true;
    }

    public boolean createIODiagram(int index, String s) {
        return createIODiagram(tabs.elementAt(index), s);
    }

    public boolean createIODiagram(TURTLEPanel tp, String s) {
        if (isIODCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof AnalysisPanel)) {
            return false;
        }

        ((AnalysisPanel) tp).addIODiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createUseCaseDiagram(int index, String s) {
        return createUseCaseDiagram(tabs.elementAt(index), s);
    }

    public boolean createUniqueUseCaseDiagram(TURTLEPanel tp, String s) {
        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        int i;
        for (i = 0; i < 1000; i++) {
            if (!isUseCaseDiagramCreated(tp, s + " " + i)) {
                break;
            }
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addUseCaseDiagram(s + " " + i);
        } else {
            ((AvatarAnalysisPanel) tp).addUseCaseDiagram(s + " " + i);
        }
        setPanelMode();
        return true;
    }

    public boolean createUseCaseDiagram(TURTLEPanel tp, String s) {
        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel) tp).addUseCaseDiagram(s);
        } else {
            ((AvatarAnalysisPanel) tp).addUseCaseDiagram(s);
        }
        setPanelMode();
        return true;
    }

    public boolean createAvatarCD(int index, String s) {
        return createAvatarCD(tabs.elementAt(index), s);
    }

    public boolean createUniqueAvatarCD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }

        int i;
        for (i = 0; i < 1000; i++) {
            if (!isAvatarCDCreated(tp, s + " " + i)) {
                break;
            }
        }

        ((AvatarAnalysisPanel) tp).addAvatarContextDiagram(s + " " + i);

        setPanelMode();
        return true;
    }

    public boolean createAvatarCD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }

        ((AvatarAnalysisPanel) tp).addAvatarContextDiagram(s);

        setPanelMode();
        return true;
    }

    public boolean createAvatarAD(int index, String s) {
        return createAvatarAD(tabs.elementAt(index), s);
    }

    public boolean createUniqueAvatarAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }

        int i;
        for (i = 0; i < 1000; i++) {
            if (!isAvatarADCreated(tp, s + " " + i)) {
                break;
            }
        }

        ((AvatarAnalysisPanel) tp).addAvatarActivityDiagram(s + " " + i);
        setPanelMode();
        return true;
    }

    public boolean createAvatarAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }

        ((AvatarAnalysisPanel) tp).addAvatarActivityDiagram(s);

        setPanelMode();
        return true;
    }

    // Changed by Solange from public boolean...
    public String createProActiveSMD(int index, String s) {
        // Adde by Solange String name at the beginning
        String name = createProActiveSMD(tabs.elementAt(index), s);
        return (name); // changed from return true
    }

    // Return changed by Solange from boolean to String
    public String createProActiveSMD(TURTLEPanel tp, String s) {

        // Added by Solange. It fills the lists of components, interfaces, etc
        TURTLEPanel tp2 = getCurrentTURTLEPanel();
        gtm.generateLists((ProactiveDesignPanel) tp2);
        //

        if (!(tp instanceof ProactiveDesignPanel)) {
            return null; // Changed by Solange from return false
        }

        s = ((ProactiveDesignPanel) tp).addSMD(s);
        // Added by Solange
        // ProactiveSMDPanel temp=((ProactiveDesignPanel)tp).getSMDPanel(s);
        // Added by Solange
        // And removed by Emil

        /*
         * LinkedList cmps=gtm.gpdtemp.getProCSDComponentsList(); for (int
         * i=0;i<cmps.size();i++) { ProCSDComponent c = (ProCSDComponent)cmps.get(i);
         * 
         * if (c.getType()== TGComponentManager.PROCSD_COMPONENT) {
         * if(c.getComponentID().equals(temp.getName())) { c.mySMD=temp; i=cmps.size();
         * } } }
         */
        //
        setPanelMode();
        return (s); // Changes by Solange from return true
    }

    public boolean createAvatarRD(int index, String s) {
        return createAvatarRD(tabs.elementAt(index), s);
    }

    public boolean createAvatarRD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarRequirementPanel)) {
            return false;
        }

        ((AvatarRequirementPanel) tp).addAvatarRD(s);
        setPanelMode();
        return true;
    }

    public boolean createAvatarMAD(int index, String s) {
        return createAvatarMAD(tabs.elementAt(index), s);
    }

    public boolean createAvatarMAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarMADsPanel)) {
            return false;
        }

        ((AvatarMADsPanel) tp).addAvatarMADPanel(s);
        setPanelMode();
        return true;
    }

    public boolean createAvatarPD(int index, String s) {
        return createAvatarPD(tabs.elementAt(index), s);
    }

    public boolean createAvatarPD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarRequirementPanel)) {
            return false;
        }

        ((AvatarRequirementPanel) tp).addAvatarPD(s);
        setPanelMode();
        return true;
    }

    public boolean createADDDiagram(int index, String s) {
        return createADDDiagram(tabs.elementAt(index), s);
    }

    public boolean createADDDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof ADDPanel)) {
            return false;
        }

        ((ADDPanel) tp).addDeploymentPanelDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createSysCAMS(int index, String s) {
        return createSysCAMS(tabs.elementAt(index), s);
    }

    public boolean createSysCAMS(TURTLEPanel tp, String s) {
        if (!(tp instanceof SysCAMSComponentDesignPanel)) {
            return false;
        }

        ((SysCAMSComponentDesignPanel) tp).addSysCAMS(s);
        setPanelMode();
        return true;
    }

    public boolean createELN(int index, String s) {
        return createELN(tabs.elementAt(index), s);
    }

    public boolean createELN(TURTLEPanel tp, String s) {
        if (!(tp instanceof ELNDesignPanel)) {
            return false;
        }

        ((ELNDesignPanel) tp).addELN(s);
        setPanelMode();
        return true;
    }

    public boolean isRequirementCreated(int index, String s) {
        return isRequirementCreated(tabs.elementAt(index), s);
    }

    public boolean isRequirementCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof RequirementDiagramPanel);
    }

    public boolean createRequirementDiagram(int index, String s) {
        return createRequirementDiagram(tabs.elementAt(index), s);
    }

    public boolean createRequirementDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof RequirementPanel)) {
            return false;
        }

        ((RequirementPanel) tp).addRequirementDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createAttackTreeDiagram(int index, String s) {
        return createAttackTreeDiagram(tabs.elementAt(index), s);
    }

    public boolean createAttackTreeDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof AttackTreePanel)) {
            return false;
        }

        ((AttackTreePanel) tp).addAttackTreeDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createFaultTreeDiagram(int index, String s) {
        return createFaultTreeDiagram(tabs.elementAt(index), s);
    }

    public boolean createFaultTreeDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof FaultTreePanel)) {
            return false;
        }

        ((FaultTreePanel) tp).addFaultTreeDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createEBRDD(int index, String s) {
        return createEBRDD(tabs.elementAt(index), s);
    }

    public boolean createEBRDD(TURTLEPanel tp, String s) {
        if (!(tp instanceof RequirementPanel)) {
            return false;
        }

        ((RequirementPanel) tp).addEBRDD(s);
        setPanelMode();
        return true;
    }

    public void generateOntologyForCurrentDiagram() {
        TraceManager.addDev("Ontology for current diagram");
        try {
            TURTLEPanel tp = getCurrentTURTLEPanel();
            String modeling = gtm.makeOneDiagramXMLFromGraphicalModel(tp, tp.tabbedPane.getSelectedIndex());
            TraceManager.addDev("Model made: " + modeling);
        } catch (Exception e) {
        }

    }

    public void generateOntologyForCurrentSetOfDiagrams() {
        TraceManager.addDev("Ontology for current set of diagrams");
        try {
            String modeling = gtm.makeXMLFromTurtleModeling(mainTabbedPane.getSelectedIndex());
            TraceManager.addDev("Model made: " + modeling);
        } catch (Exception e) {
        }

    }

    public void generateOntologyForAllDiagrams() {
        TraceManager.addDev("Ontology for all diagrams");
        try {
            /* String modeling = */
            gtm.makeXMLFromTurtleModeling(-1);
            TraceManager.addDev("Model made");
        } catch (Exception e) {
        }

    }

    public SysCAMSComponentTaskDiagramPanel getSysCAMSPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getSysCAMSPanel(tp, indexTab, s);
    }

    public SysCAMSComponentTaskDiagramPanel getSysCAMSPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (SysCAMSComponentTaskDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public ELNDiagramPanel getELNPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getELNPanel(tp, indexTab, s);
    }

    public ELNDiagramPanel getELNPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (ELNDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarRDPanel getAvatarRDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarRDPanel(tp, indexTab, s);
    }

    public AvatarRDPanel getAvatarRDPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarRDPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarMADPanel getAvatarMADPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarMADPanel(tp, indexTab, s);
    }

    public AvatarMADPanel getAvatarMADPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarMADPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarPDPanel getAvatarPDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarPDPanel(tp, indexTab, s);
    }

    public ADDDiagramPanel getAvatarADDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarADDPanel(tp, indexTab, s);
    }

    public ADDDiagramPanel getAvatarADDPanelByIndex(int index, int indexTab) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarADDPanelByIndex(tp, indexTab);
    }

    public AvatarPDPanel getAvatarPDPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarPDPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public ADDDiagramPanel getAvatarADDPanel(TURTLEPanel tp, int indexTab, String s) {
        TraceManager.addDev("index=" + indexTab + " s=" + s + "title=" + tp.tabbedPane.getTitleAt(indexTab));

        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (ADDDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public ADDDiagramPanel getAvatarADDPanelByIndex(TURTLEPanel tp, int indexTab) {
        // TraceManager.addDev("index=" + indexTab + " s=" + s + "title="
        // +tp.tabbedPane.getTitleAt(indexTab));

        // if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
        return (ADDDiagramPanel) (tp.panelAt(indexTab));
        // }

    }

    public AvatarCDPanel getAvatarCDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarCDPanel(tp, indexTab, s);
    }

    public AvatarCDPanel getAvatarCDPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarCDPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarADPanel getAvatarADPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getAvatarADPanel(tp, indexTab, s);
    }

    public AvatarADPanel getAvatarADPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarADPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getRequirementDiagramPanel(tp, s);
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof RequirementDiagramPanel)
                    return (RequirementDiagramPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getRequirementDiagramPanel(tp, indexTab, s);
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (RequirementDiagramPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public EBRDDPanel getEBRDDPanel(int index, String s) {
        // TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = tabs.elementAt(index);
        return getEBRDDPanel(tp, s);
    }

    public EBRDDPanel getEBRDDPanel(TURTLEPanel tp, String s) {
        for (int i = 0; i < tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof EBRDDPanel)
                    return (EBRDDPanel) (tp.panelAt(i));
            }
        }
        return null;
    }

    public EBRDDPanel getEBRDDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = tabs.elementAt(index);
        return getEBRDDPanel(tp, indexTab, s);
    }

    public EBRDDPanel getEBRDDPanel(TURTLEPanel tp, int indexTab, String s) {
        if (tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (EBRDDPanel) (tp.panelAt(indexTab));
        }
        return null;
    }

    public void alignInstances() {
        // TraceManager.addDev("Align instances");
        if (getCurrentTDiagramPanel() instanceof ui.sd.SequenceDiagramPanel) {
            ((ui.sd.SequenceDiagramPanel) (getCurrentTDiagramPanel())).alignInstances();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }

        if (getCurrentTDiagramPanel() instanceof ui.sd2.SequenceDiagramPanel) {
            ((ui.sd2.SequenceDiagramPanel) (getCurrentTDiagramPanel())).alignInstances();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }

        if (getCurrentTDiagramPanel() instanceof TMLSDPanel) {
            ((TMLSDPanel) (getCurrentTDiagramPanel())).alignInstances();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }
    }

    public void alignPartitions() {
        // TraceManager.addDev("Align instances");
        if (getCurrentTDiagramPanel() instanceof AvatarADPanel) {
            ((AvatarADPanel) (getCurrentTDiagramPanel())).alignPartitions();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }
    }

    public void enhanceDiagram() {
        getCurrentTDiagramPanel().enhance();
    }

    public JTabbedPane getCurrentJTabbedPane() {
        if (tabs == null) {
            return null;
        }
        if (mainTabbedPane == null) {
            return null;
        }

        if (mainTabbedPane.getSelectedIndex() < 0) {
            return null;
        }

        return tabs.elementAt(mainTabbedPane.getSelectedIndex()).tabbedPane;
    }

    public int getCurrentSelectedIndex() {
        return mainTabbedPane.getSelectedIndex();
    }

    public TDiagramPanel getCurrentTDiagramPanel() {
        try {
            TURTLEPanel tp = tabs.elementAt(mainTabbedPane.getSelectedIndex());
            return tp.panels.elementAt(tp.tabbedPane.getSelectedIndex());
        } catch (Exception e) {
            return null;
        }
    }

    public int getTDiagramPanelIndex() {
        try {
            TURTLEPanel tp = tabs.elementAt(mainTabbedPane.getSelectedIndex());
            return tp.tabbedPane.getSelectedIndex();
        } catch (Exception e) {
            return -1;
        }
    }

    public TDiagramPanel getCurrentMainTDiagramPanel() {
        return tabs.elementAt(mainTabbedPane.getSelectedIndex()).tdp;
    }

    public void selectDummyTab(int index) {
        if (this.tabs.size() < index + 1)
            return;
        this.dummySelectedTab = this.tabs.elementAt(index);
    }

    public void forgetDummyTab() {
        this.dummySelectedTab = null;
    }

    public TURTLEPanel getCurrentTURTLEPanel() {
        if (this.dummySelectedTab != null)
            return this.dummySelectedTab;

        if (tabs.size() == 0) {
            return null;
        } else {
            return tabs.elementAt(mainTabbedPane.getSelectedIndex());
        }
    }

    public void reinitCountOfPanels() {
        int i, j;
        TURTLEPanel tp;
        for (i = 0; i < mainTabbedPane.getTabCount(); i++) {
            tp = tabs.elementAt(i);
            for (j = 0; j < tp.tabbedPane.getTabCount(); j++) {
                tp.panels.elementAt(j).count = 0;
            }
        }
    }

    public void setPanelMode() {
        int index;
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            TraceManager.addDev("null TP Panel");
            return;
        }
        index = tp.tabbedPane.getSelectedIndex();

        // TraceManager.addDev("TP Panel: " + tp + " index=" + index);

        if (index < tp.panels.size() - 1) {
            setMode(FORWARD_DIAG);
        } else {
            setMode(NO_FORWARD_DIAG);
        }

        if (index > 0) {
            setMode(BACKWARD_DIAG);
        } else {
            setMode(NO_BACKWARD_DIAG);
        }

        // If no edition panel, we need to deactivate previous diagram and model
        // checking

        if (index == -1) {
            setMode(MainGUI.CREATE_NEW_PANEL);
        }

        setMode(METHO_CHANGED);

    }

    public void paneMADAction(ChangeEvent e) {
        // TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane design action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneDesignAction(ChangeEvent e) {
        // TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane design action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneDiplodocusMethodologyAction(ChangeEvent e) {
        // TraceManager.addDev("Pane design action size=" + tabs.size());
        try {
            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {

                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane design action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneAvatarMethodologyAction(ChangeEvent e) {
        // TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {

                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane design action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneAnalysisAction(ChangeEvent e) {
        // TraceManager.addDev("Pane analysis action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane analysis action 1 on " + tdp1.getName());
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane analysis action 2");
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane analysis action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane analysis action");
        }
    }

    public void paneDeployAction(ChangeEvent e) {
        // TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            // TraceManager.addDev("Pane design action 3");

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneRequirementAction(ChangeEvent e) {
        try {

            TDiagramPanel tdp1 = getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex());
            // TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            // TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();

            // activate the drawing of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane design action");
        }
    }

    // nameTab : array of strings not to be removed
    public void removeAnalysisDiagrams(String[] nameTab) {
        int i, j;
        String value;
        boolean found;

        for (i = 1; i < getCurrentJTabbedPane().getTabCount(); i++) {
            value = getCurrentJTabbedPane().getTitleAt(i);
            found = false;
            for (j = 0; j < nameTab.length; j++) {
                if (nameTab[j] != null) {
                    if (nameTab[j].compareTo(value) == 0) {
                        found = true;
                        break;
                    }

                }
            }
            if (!found) {
                TraceManager.addDev("Removing" + getCurrentJTabbedPane().getTitleAt(i));
                getCurrentJTabbedPane().remove(i);
                getCurrentTURTLEPanel().removeElementAt(i);
                i--;
            }
        }
    }

    public void renameTab(TDiagramPanel tdp) {
        String value;
        int index, index1;

        index = getCurrentJTabbedPane().getSelectedIndex();
        value = getCurrentJTabbedPane().getTitleAt(index);

        // String s = (String)JOptionPane.showInputDialog(this, "Name of the diagram:",
        // JOptionPane.QUESTION_MESSAGE);
        String s = (String) JOptionPane.showInputDialog(frame, "Name of the diagram:", "setting value", JOptionPane.PLAIN_MESSAGE,
                IconManager.imgic101, null, value);

        if ((s != null) && (s.length() > 0)) {
            // name already in use?
            index1 = getCurrentJTabbedPane().indexOfTab(s);
            /*
             * if (methoMode == METHO_ANALYSIS) { index1 =
             * mainAnalysisTabbedPane.indexOfTab(s);
             * 
             * } else { index1 = getCurrentJTabbedPane.indexOfTab(s);
             * 
             * }
             */
            if (index1 > -1) {
                JOptionPane.showMessageDialog(frame, "Name is already in use", "Error", JOptionPane.INFORMATION_MESSAGE);
            } else {
                /*
                 * if (methoMode == METHO_ANALYSIS) { mainAnalysisTabbedPane.setTitleAt(index,
                 * s); } else { mainDesignTabbedPane.setTitleAt(index, s); }
                 */
                if (isAValidTabName(s)) {
                    JOptionPane.showMessageDialog(frame, "Invalid name", "Error", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    getCurrentJTabbedPane().setTitleAt(index, s);
                    tdp.setName(s);
                    changeMade(tdp, TDiagramPanel.NEW_COMPONENT);
                }
            }
        }
    }

    public void deleteTab(TDiagramPanel tdp) {
        // TraceManager.addDev("Delete");

        // We know the selected tab -> remove this tab

        int index;
        JTabbedPane jtp = getCurrentJTabbedPane();
        index = jtp.getSelectedIndex();
        getCurrentTURTLEPanel().panels.removeElementAt(index);
        jtp.remove(index);

        // Signal the change
        changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);

    }

    private void basicActivateDrawing() {
        activetdp.setDraw(true);
        if (activetdp.mode == TDiagramPanel.SELECTED_COMPONENTS) {
            setMode(MainGUI.CUTCOPY_OK);
            setMode(MainGUI.EXPORT_LIB_OK);
        } else {
            setMode(MainGUI.CUTCOPY_KO);
            setMode(MainGUI.EXPORT_LIB_KO);
        }
    }

    private void unactivateDrawing() {
        activetdp.setDraw(false);
    }

    public void paneAction(ChangeEvent e) {
        // TraceManager.addDev("Pane action");
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            // TraceManager.addDev("Stop Adding connector in maingui");
            tdp1.stopAddingConnector();
        }

        try {
            if (getCurrentTURTLEPanel() instanceof DesignPanel) {
                setMethodologicalMode(METHO_DESIGN);
                paneDesignAction(e);
            } else if (getCurrentTURTLEPanel() instanceof AnalysisPanel) {
                setMethodologicalMode(METHO_ANALYSIS);
                paneAnalysisAction(e);
            } else {
                setMethodologicalMode(METHO_DEPLOY);
                paneDeployAction(e);
            }

            if ((getCurrentTURTLEPanel() instanceof AvatarDesignPanel) || (getCurrentTURTLEPanel() instanceof AvatarRequirementPanel)
                    || (getCurrentTURTLEPanel() instanceof AttackTreePanel) || (getCurrentTURTLEPanel() instanceof FaultTreePanel)
                    || (getCurrentTURTLEPanel() instanceof ADDPanel)) {
                mainBar.showAvatarActions(true);
            } else if ((getCurrentTURTLEPanel() instanceof TMLDesignPanel) || (getCurrentTURTLEPanel() instanceof TMLComponentDesignPanel)
                    || (getCurrentTURTLEPanel() instanceof TMLArchiPanel)) {
                mainBar.showDiplodocusActions(true);
            } else {
                mainBar.showAvatarActions(false);
            }

            setMode(PANEL_CHANGED);

            setEditMode();
            setPanelMode();

        } catch (Exception ex) {
            // TraceManager.addDev("Exception pane action: " + ex.getMessage());
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public boolean newTClassName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        // TraceManager.addDev("old " + old + " niou " + niou);
        for (int i = 0; i < jtp.getTabCount(); i++) {
            // TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the activity diagram of " + niou);
                TDiagramPanel tdp;
                // change panel name
                for (int j = 0; j < tp.panels.size(); j++) {
                    tdp = tp.panels.elementAt(j);
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                    }
                }

                return true;
            }
        }
        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public boolean newTOSClassName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        // TraceManager.addDev("old " + old + " niou " + niou);
        for (int i = 0; i < jtp.getTabCount(); i++) {
            // TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TURTLE-OS activity diagram of " + niou);
                TDiagramPanel tdp;
                // change panel name
                for (int j = 0; j < tp.panels.size(); j++) {
                    tdp = tp.panels.elementAt(j);
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                    }
                }

                return true;
            }
        }
        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public boolean newTMLTaskName(TURTLEPanel tp, String old, String niou) {
        // TraceManager.addDev("Panel=" + tp + " Old task name = " + old + " New task
        // name=" + niou);
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            // TraceManager.addDev("jtp = " + jtp.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        // TraceManager.addDev("old " + old + " niou " + niou);
        for (int i = 0; i < jtp.getTabCount(); i++) {
            // TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            // TraceManager.addDev("jtp = " + jtp.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TML activity diagram of " + niou);
                TDiagramPanel tdp;
                // change panel name
                for (int j = 0; j < tp.panels.size(); j++) {
                    tdp = tp.panels.elementAt(j);
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                        // TraceManager.addDev("Renamed to " + niou);
                    }
                }

                return true;
            }
        }

        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public boolean nameComponentInUse(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return true;
            }
        }
        return false;
    }

    public boolean newTMLComponentTaskName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        TraceManager.addDev("old " + old + " niou " + niou);
        for (int i = 0; i < jtp.getTabCount(); i++) {
            TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TML activity diagram of " + niou);
                TDiagramPanel tdp;
                // change panel name
                for (int j = 0; j < tp.panels.size(); j++) {
                    tdp = tp.panels.elementAt(j);
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                    }
                }

                return true;
            }
        }
        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public boolean newSysCAMSComponentTaskName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for (int i = 0; i < jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        TraceManager.addDev("old " + old + " niou " + niou);
        for (int i = 0; i < jtp.getTabCount(); i++) {
            TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the SystemC-AMS diagram of " + niou);
                TDiagramPanel tdp;
                // change panel name
                for (int j = 0; j < tp.panels.size(); j++) {
                    tdp = tp.panels.elementAt(j);
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                    }
                }

                return true;
            }
        }
        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public List<TMLCPrimitiveComponent> getAllTMLCPrimitiveComponents() {
        List<TMLCPrimitiveComponent> allComponents = new ArrayList<TMLCPrimitiveComponent>();

        for (TURTLEPanel panel: tabs) {
            if (panel instanceof TMLComponentDesignPanel) {
                List<TMLCPrimitiveComponent> allComponentsTmp = new ArrayList<TMLCPrimitiveComponent>();
                ((TMLComponentDesignPanel)panel).fillAllTMLCPrimitiveComponent(allComponentsTmp);

                for(TMLCPrimitiveComponent pc: allComponentsTmp) {
                    pc.setNameOfArchi(panel.getNameOfTab());
                }
                allComponents.addAll(allComponentsTmp);
            }
        }


        return allComponents;
    }


    public void generateRandomMapping() {
        // Open dialog window to select tasks
        TraceManager.addDev("Generate random mapping");
        List<TMLCPrimitiveComponent> tasksToMap = new LinkedList<TMLCPrimitiveComponent>();
        List<TMLCPrimitiveComponent> allComponents = getAllTMLCPrimitiveComponents();

        JDialogMappingGeneration jdmg = new JDialogMappingGeneration(frame, tasksToMap, allComponents,
                "Selection of tasks to be mapped");
        jdmg.setSize(900, 500);
        GraphicLib.centerOnParent(jdmg);
        jdmg.setVisible(true);

        if (jdmg.hasBeenCancelled()) {
            return;
        }


        String nameOfArchi = getCurrentTURTLEPanel().getNameOfTab();
        TraceManager.addDev("Name of archi:" + nameOfArchi);

        String []nameOfTasks = new String[tasksToMap.size()];
        for(int i=0; i<nameOfTasks.length; i++) {
            nameOfTasks[i] = tasksToMap.get(i).toString();
        }

        int nbOfMapings = jdmg.getNbOfMappings();

        for(int i=0; i<nbOfMapings; i++) {
            gtm.generateRandomMapping(nameOfTasks, nameOfArchi, "RandomMapping_" + i);
        }
    }


    public void cloneTab(int index) {
        String s = gtm.makeXMLFromTurtleModeling(index, "_cloned");
        try {
            gtm.loadModelingFromXML(s);
            changeMade(null, -1);
        } catch (MalformedModelingException mme) {
            JOptionPane.showMessageDialog(frame, "Modeling could not be loaded (unsupported xml format) ", "Error when loading modeling",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.setTitle("TTool: unamed project");
        } catch (UnsupportedEncodingException mme) {
            JOptionPane.showMessageDialog(frame, "Modeling could not be loaded (unsupported encoding format) ", "Error when loading modeling",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.setTitle("TTool: unamed project");
        }

    }

    public void cloneRenameTab(int index, String s) {
        cloneTab(index);
        mainTabbedPane.setTitleAt(tabs.size() - 1, mainTabbedPane.getTitleAt(index) + "_" + s);
    }

    public void removeCurrentTab() {
        requestRemoveTab(mainTabbedPane.getSelectedIndex());
    }

    public void requestRemoveTab(int index) {
        if (index >= tabs.size()) {
            return;
        }

        tabs.removeElementAt(index);
        mainTabbedPane.remove(index);
        changeMade(null, -1);
    }


    public boolean selectPanelByName(String name) {
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            if (mainTabbedPane.getTitleAt(i).equals(name)) {
                mainTabbedPane.setSelectedIndex(i);
                return true;
            }

        }
        return false;
    }


    public void requestMoveRightTab(int index) {
        // TraceManager.addDev("Move right");
        if (index > tabs.size() - 2) {
            return;
        }
        requestMoveTabFromTo(index, index + 1);
        changeMade(null, -1);
    }

    public void selectPanelOnTheLeft() {
        if (mainTabbedPane != null) {
            mainTabbedPane.setSelectedIndex(Math.max(0, mainTabbedPane.getSelectedIndex() - 1));
        }
    }

    public void requestMoveLeftTab(int index) {
        // TraceManager.addDev("Move left");
        if (index < 1) {
            return;
        }
        requestMoveTabFromTo(index, index - 1);
        changeMade(null, -1);
    }

    public void requestMoveTabFromTo(int src, int dst) {

        // Get all the properties
        Component comp = mainTabbedPane.getComponentAt(src);
        String label = mainTabbedPane.getTitleAt(src);
        Icon icon = mainTabbedPane.getIconAt(src);
        Icon iconDis = mainTabbedPane.getDisabledIconAt(src);
        String tooltip = mainTabbedPane.getToolTipTextAt(src);
        boolean enabled = mainTabbedPane.isEnabledAt(src);
        int keycode = mainTabbedPane.getMnemonicAt(src);
        int mnemonicLoc = mainTabbedPane.getDisplayedMnemonicIndexAt(src);
        Color fg = mainTabbedPane.getForegroundAt(src);
        Color bg = mainTabbedPane.getBackgroundAt(src);

        // Remove the tab
        mainTabbedPane.remove(src);

        // Add a new tab
        mainTabbedPane.insertTab(label, icon, comp, tooltip, dst);

        // Restore all properties
        mainTabbedPane.setDisabledIconAt(dst, iconDis);
        mainTabbedPane.setEnabledAt(dst, enabled);
        mainTabbedPane.setMnemonicAt(dst, keycode);
        mainTabbedPane.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
        mainTabbedPane.setForegroundAt(dst, fg);
        mainTabbedPane.setBackgroundAt(dst, bg);

        TURTLEPanel o = tabs.elementAt(src);
        tabs.removeElementAt(src);
        tabs.insertElementAt(o, dst);

        mainTabbedPane.setSelectedIndex(dst);
    }

    public void requestRenameTab(int index) {
        String oldName = mainTabbedPane.getTitleAt(index);
        String s = (String) JOptionPane.showInputDialog(frame, "Name: ", "Renaming a tab", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null,
                mainTabbedPane.getTitleAt(index));
        if ((s != null) && (s.length() > 0)) {
            // name already in use?
            if (s.compareTo(oldName) != 0) {
                if (isAValidTabName(s) && (!isTabNameUsed(s))) {
                    renameInMethodo(oldName, s);
                    if (index < mainTabbedPane.getTabCount()) {
                        mainTabbedPane.setTitleAt(index, s);
                        changeMade(getCurrentTDiagramPanel(), /* ((TURTLEPanel)(tabs.elementAt(index))).tdp */TDiagramPanel.MOVE_COMPONENT);

                        if (index < tabs.size() ) {
                            TURTLEPanel tp = tabs.elementAt(index);
                            if ((tp instanceof TMLDesignPanel) || (tp instanceof TMLComponentDesignPanel)) {
                                renameMapping(oldName, s);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid name", "Error", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
        changeMade(null, -1);
    }

    /**
     * Check if a name is already used by another TURTLEPanel tab
     *
     * @param name the name to be checked
     * @return true if the name matches another tab name
     * @author Fabien Tessier
     */
    public boolean isTabNameUsed(String name) {
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            if (mainTabbedPane.getTitleAt(i).equals(name))
                return true;
        }
        return false;
    }

    /**
     * Rename reference to the tab in Diplodocus Methodology
     *
     * @param oldname : old name
     * @param newname : new name
     * @author Fabien Tessier
     */
    public void renameInMethodo(String oldname, String newname) {
        if (tabs != null) {
            for (TURTLEPanel tp : tabs) {
                if (tp instanceof DiplodocusMethodologyPanel) {
                    DiplodocusMethodologyDiagramPanel dmdp = (DiplodocusMethodologyDiagramPanel) tp.tdp;
                    if (dmdp != null) {
                        for (TGComponent tgc : dmdp.componentList)
                            for (TGComponent tgc2 : tgc.tgcomponent) {
                                if (tgc2.getValue().equals(oldname))
                                    tgc2.setValue(newname);
                            }
                    }
                }
            }
        }
    }

    public void renameMapping(String oldName, String newName) {
        TURTLEPanel tp;

        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof TMLArchiPanel) {
                ((TMLArchiPanel) tp).renameMapping(oldName, newName);
            }
        }

    }

    public void renameDeployment(String oldName, String newName) {
        TURTLEPanel tp;

        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            tp = tabs.elementAt(i);
            if (tp instanceof ADDPanel) {
                ((ADDPanel) tp).renameDeployment(oldName, newName);
            }
        }

    }

    public boolean selectTDiagramPanel(TDiagramPanel tdp) {
        return (selectTab(getPoint(tdp)) == tdp);
    }

    public boolean selectTab(TURTLEPanel tp, String s) {
        int j;
        int index1 = tabs.indexOf(tp);

        if (mainTabbedPane.getSelectedIndex() != index1) {
            mainTabbedPane.setSelectedIndex(index1);
        }

        for (j = 0; j < tp.tabbedPane.getTabCount(); j++) {
            if (tp.tabbedPane.getTitleAt(j).equals(s)) {
                tp.tabbedPane.setSelectedIndex(j);
                return true;
            }
        }

        return false;
    }

    public boolean selectTab(String s) {
        return selectTab(getCurrentTURTLEPanel(), s);
    }

    public void openTMLTaskActivityDiagram(String panel, String tab) {
        int index = mainTabbedPane.indexOfTab(panel);
        if (index != -1) {
            mainTabbedPane.setSelectedIndex(index);
        }
        openTMLTaskActivityDiagram(tab);
    }

    public void openTMLTaskActivityDiagram(String tab) {
        selectTab(getCurrentTURTLEPanel(), tab);
    }

    public void refreshCurrentPanel() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void openAVATARSMD(String tab) {
        TDiagramPanel cur = getCurrentTDiagramPanel();
        selectTab(getCurrentTURTLEPanel(), tab);
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp == cur) {
            tdp.repaint();
        }
    }

    public boolean selectHighLevelTab(String s) {
        TURTLEPanel tp = getTURTLEPanel(s);
        if (s != null) {
            selectTab(tp);
            return true;
        }
        return false;
    }

    public TDiagramPanel selectTab(Point p) {
        if (p.x < 0) {
            return null;
        }
        if (p.y < 0) {
            return null;
        }

        if (mainTabbedPane.getTabCount() > 0) {
            mainTabbedPane.setSelectedIndex(p.x);
            TURTLEPanel tp = tabs.elementAt(p.x);
            TraceManager.addDev("Got TP");
            if (tp != null) {
                if (tp.tabbedPane.getTabCount() > 0) {
                    tp.tabbedPane.setSelectedIndex(p.y);
                    return tp.panels.elementAt(p.y);
                }
            }
        }
        return null;

    }

    public Point getCurrentSelectedPoint() {
        Point p = new Point();

        p.x = mainTabbedPane.getSelectedIndex();
        if (getCurrentTURTLEPanel() == null) {
            p.y = -1;
        } else {
            p.y = getCurrentTURTLEPanel().tabbedPane.getSelectedIndex();
        }

        return p;
    }

    public Point getPoint(TDiagramPanel tdp) {
        Point p = new Point();

        int index;
        TURTLEPanel tp;

        // Issue #81: For tests
        if (tabs != null) {
            for (int i = 0; i < tabs.size(); i++) {
                tp = tabs.elementAt(i);
                index = tp.panels.indexOf(tdp);
                if (index > -1) {
                    p.x = i;
                    p.y = index;
                    return p;
                }
            }
        }

        p.x = 0;
        p.y = 0;

        return p;
    }

    public DesignPanel getDesignPanel(String name) {
        int index = mainTabbedPane.indexOfTab(name);
        try {
            TURTLEPanel tp = tabs.elementAt(index);

            if (tp instanceof DesignPanel) {
                return (DesignPanel) tp;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    // ---------------------------------- DDD ------------------------------------
    // return current deployment panel

    public ADDDiagramPanel getDeploymentPanel() {
        ADDDiagramPanel deploymentDiagram = null; // (ADDDiagramPanel)activetdp;
        TURTLEPanel tp = getCurrentTURTLEPanel();
        Vector<TDiagramPanel> ps = tp.panels;
        for (TDiagramPanel panel : ps) {
            if (panel instanceof ADDDiagramPanel) {
                deploymentDiagram = (ADDDiagramPanel) panel;
            }
        }
        if (deploymentDiagram == null)
            System.err.println("No ADDDiagramPanel found : MainGUI.getDeploymentPanel()");
        return deploymentDiagram;
    }

    public ADDDiagramPanel getFirstAvatarDeploymentPanelFound() {
        ADDDiagramPanel adp = null;
        for (int i = 0; i < tabs.size(); i++)
            if (tabs.get(i) instanceof ADDPanel) {
                adp = ((ADDPanel) tabs.get(i)).tmladd;
            }
        if (adp == null)
            System.err.println("No AvatarDeployment Panel Found : MainGUI.getFirstAvatarDeploymentPanelFound()");
        return adp;
    }

    // find the first Design Panel in MainGUI.tabs

    public AvatarDesignPanel getFirstAvatarDesignPanelFound() {
        AvatarDesignPanel adp = null;
        for (int i = 0; i < tabs.size(); i++)
            if (tabs.get(i) instanceof AvatarDesignPanel) {
                adp = (AvatarDesignPanel) tabs.get(i);
            }
        if (adp == null)
            System.err.println("No AvatarDesign Panel Found : MainGUI.getFirstAvatarDesignPanel()");
        return adp;
    }

    public void extracDeploymentDiagramToFile() {

        ADDDiagramPanel deploymentDiagramPanel = getDeploymentPanel();
        AvatarDesignPanel designDiagramPanel = getFirstAvatarDesignPanelFound();

        AvatarDeploymentPanelTranslator avdeploymenttranslator = new AvatarDeploymentPanelTranslator(deploymentDiagramPanel);
        /* AvatarddSpecification avddspec = */
        avdeploymenttranslator.getAvatarddSpecification();

        AvatarDesignPanelTranslator avdesigntranslator = new AvatarDesignPanelTranslator(designDiagramPanel);
        List<AvatarBDStateMachineOwner> adp = designDiagramPanel.getAvatarBDPanel().getFullStateMachineOwnerList();
        /* AvatarSpecification avaspec = */
        avdesigntranslator.generateAvatarSpecification(adp);

        // DG
        // LinkedList<AvatarComponent> components =
        // AvatarddSpecification.getComponents();
        // AvatarToFile tofile;
        // tofile = new AvatarToFile(components,avaspec);
        // tofile.extracParamToFile();
    }

//    public void avatarToSoclib() {
//        //DG 6.2. appelee nulle part?
//
//        ADDDiagramPanel deploymentDiagramPanel = getDeploymentPanel();
//        AvatarDesignPanel designDiagramPanel = getFirstAvatarDesignPanelFound();
//
//        AvatarDeploymentPanelTranslator avdeploymenttranslator = new AvatarDeploymentPanelTranslator(deploymentDiagramPanel);
//        AvatarddSpecification avddspec = avdeploymenttranslator.getAvatarddSpecification();
//
//
//        AvatarDesignPanelTranslator avdesigntranslator = new AvatarDesignPanelTranslator(designDiagramPanel);
//
//        List<AvatarBDStateMachineOwner> adp = designDiagramPanel.getAvatarBDPanel().getFullStateMachineOwnerList();
//        AvatarSpecification avaspec = avdesigntranslator.generateAvatarSpecification(adp);
//
//        // Generator for block tasks and application main file
//
//        TasksAndMainGenerator gene = new TasksAndMainGenerator(avddspec, avaspec);
//        gene.generateSoclib(false, false);
//        try {
//            //System.err.println("ok");
//            gene.saveInFiles(TasksAndMainGenerator.getGeneratedPath());
//        } catch (FileException e) {
//            System.err.println("FileException : MainGUI.avatarToSoclib()");
//        }
//
//    }
    // --------------------end DDD------------------------------------------------

    public Vector<SysCAMSComponentTaskDiagramPanel> getListSysCAMSPanel() {
        Vector<SysCAMSComponentTaskDiagramPanel> syscamsDiagram = new Vector<SysCAMSComponentTaskDiagramPanel>();
        TURTLEPanel tp = getTURTLEPanel("SystemC_AMS");
        if (tp != null) {
            Vector<TDiagramPanel> ps = tp.panels;
            for (TDiagramPanel panel : ps) {
                if (panel instanceof SysCAMSComponentTaskDiagramPanel) {
                    syscamsDiagram.add((SysCAMSComponentTaskDiagramPanel) panel);
                }
            }
            if (syscamsDiagram.size() == 0)
                System.err.println("No SysCAMS Panel found : MainGUI.getListSysCAMSPanel()");
            return syscamsDiagram;
        } else {
            JDialog msg = new JDialog();
            msg.setLocationRelativeTo(null);
            //JOptionPane.showMessageDialog(msg, "There is no SystemC-AMS panel. Please add one.", "Warning !", JOptionPane.WARNING_MESSAGE);//DG 19.12. obsolete
            return null;
        }
    }

    public void syscamsExecutableCodeGeneration() {
        JDialogSysCAMSExecutableCodeGeneration jgen = new JDialogSysCAMSExecutableCodeGeneration(frame, this,
                "Executable Code generation, compilation and execution", "../SysCAMSGenerationCode/");

        GraphicLib.centerOnParent(jgen, 500, 450);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public Vector<ELNDiagramPanel> getListELNPanel() {
        Vector<ELNDiagramPanel> elnDiagram = new Vector<ELNDiagramPanel>();
        TURTLEPanel tp = getTURTLEPanel("ELN");
        if (tp != null) {
            Vector<TDiagramPanel> ps = tp.panels;
            for (TDiagramPanel panel : ps) {
                if (panel instanceof ELNDiagramPanel) {
                    elnDiagram.add((ELNDiagramPanel) panel);
                }
            }
            if (elnDiagram.size() == 0)
                System.err.println("No ELN Panel found : MainGUI.getListELNPanel()");
            return elnDiagram;
        } else {
            JDialog msg = new JDialog();
            msg.setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(msg, "There is no ELN panel. Please add one.", "Warning !", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void elnExecutableCodeGeneration() {
        JDialogELNExecutableCodeGeneration jgen = new JDialogELNExecutableCodeGeneration(frame, this,
                "Executable Code generation, compilation and execution", "../ELNGenerationCode/");

        GraphicLib.centerOnParent(jgen, 500, 450);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public boolean selectMainTab(String id) {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            if (getTabName(tp).compareTo(id) == 0) {
                selectTab(tp);
                return true;
            }
        }

        return false;
    }

    public void selectTab(TURTLEPanel tp) {
        int index1 = tabs.indexOf(tp);
        mainTabbedPane.setSelectedIndex(index1);
    }

    public void selectTab(TDiagramPanel tdp) {
        int index;
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            index = tp.panels.indexOf(tdp);
            if (index > -1) {
                selectTab(tp);
                tp.tabbedPane.setSelectedIndex(index);
            }
        }
    }

    public void toggleAttributes() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)) {
            // TraceManager.addDev("Toggle attributes");
            TClassDiagramPanel tdcp = (TClassDiagramPanel) tdp;
            tdcp.setAttributesVisible(!tdcp.areAttributesVisible());
            tdcp.checkAllMySize();
            tdcp.repaint();
            changeMade(tdcp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    // AVATAR Simulation
    public void setAVATARIDs(boolean b) {
        TDiagramPanel.AVATAR_ID_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void setAVATARActions(int val) {
        TDiagramPanel.AVATAR_ID_ACTIONS = val;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void toggleAVATARIDs() {
        setAVATARIDs(!TDiagramPanel.AVATAR_ID_ON);
        //TraceManager.addDev("AVATAR id: " + TDiagramPanel.AVATAR_ID_ON);
    }

    public void toggleAVATARActions() {
        setAVATARActions((TDiagramPanel.AVATAR_ID_ACTIONS + 1)%3);
        //TraceManager.addDev("AVATAR actions: " + TDiagramPanel.AVATAR_ID_ACTIONS);
    }

    // For simulation purpose
    public void resetAllDIPLOIDs() {
        TURTLEPanel tp;

        for (int i = 0; i < tabs.size(); i++) {
            tp = tabs.elementAt(i);
            tp.resetAllDIPLOIDs();
        }
    }

    public void toggleDiploIDs() {
        setDiploIDs(!TDiagramPanel.DIPLO_ID_ON);
    }

    public void toggleTEPEIDs() {
        setTEPEIDs(!TDiagramPanel.TEPE_ID_ON);
    }

    public void toggleDiploAnimate() {
        setDiploAnimate(!TDiagramPanel.DIPLO_ANIMATE_ON);
    }

    public int isRunningAvatarComponent(TGComponent _tgc) {
        if (jfais.isVisible()) {
            if (jfais.isRunningComponent(_tgc)) {
                return 1;
            }
            if (jfais.isSelectedComponentFromTransaction(_tgc)) {
                return 2;
            }
        }
        return 0;
    }

    public String[] hasMessageInformationForAvatarConnector(AvatarBDPortConnector _conn) {
        if (jfais.isVisible()) {
            return jfais.getFirstMessagesOnEachConnectorSide(_conn);
        }
        return null;
    }

    public void setDiploAnimate(boolean b) {
        TDiagramPanel.DIPLO_ANIMATE_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void setAvatarAnimate(boolean b) {
        TDiagramPanel.AVATAR_ANIMATE_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void setDiploIDs(boolean b) {
        TDiagramPanel.DIPLO_ID_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void setTEPEIDs(boolean b) {
        TDiagramPanel.TEPE_ID_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public void setTransationProgression(boolean b) {
        TDiagramPanel.DIPLO_TRANSACTION_PROGRESSION_ON = b;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized RunningInfo isRunningID(int id) {
        if (runningIDs == null) {
            return null;
        }

        for (RunningInfo ri : runningIDs) {
            if (ri.id == id) {
                return ri;
            }
        }

        return null;
    }

    public synchronized ArrayList<SimulationTransaction> getTransactions(int id) {
        if (transactionMap == null) {
            TraceManager.addDev("Null map");
            return null;
        }

        return transactionMap.get(id);
    }

    public synchronized void addLatencyVals(int id, String[] latency) {
        if (latencyMap != null) {
            //
            if (!latencyMap.containsKey(id)) {
                ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
                latencyMap.put(id, map);
            }
            latencyMap.get(id).put(latency[0], latency[1]);
            //
        }
        //
    }

    public synchronized ConcurrentHashMap<String, String> getLatencyVals(int id) {
        //
        if (latencyMap != null) {
            return latencyMap.get(id);
        }
        return null;
    }

    public synchronized ConcurrentHashMap<String, String> getStatus(int id) {
        if (statusMap == null) {
            return null;
        }

        return statusMap;
    }

    public void popupMainCommand() {
        mainBar.popupMainCommand();
    }


    public String getStatus(String s) {
        if (statusMap == null) {
            return null;
        }
        return statusMap.get(s);
    }

    public synchronized LoadInfo isLoadID(int id) {
        if (loadIDs == null) {
            return null;
        }

        for (LoadInfo li : loadIDs) {
            if (li.id == id) {
                return li;
            }
        }

        return null;
    }

    public void resetReachability() {
        // TraceManager.addDev("Reset reachability");
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp != null) {
            tp.resetReachability();
        }
    }

    public void resetLiveness() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp != null) {
            tp.resetLiveness();
        }
    }

    public synchronized void resetRunningID() {
        if (runningIDs != null) {
            runningIDs.clear();
        }
        runningIDs = null;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void resetLoadID() {
        if (loadIDs != null) {
            loadIDs.clear();
        }
        loadIDs = null;
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void resetTransactions() {
        transactionMap.clear();
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void resetStatus() {
        statusMap.clear();
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void addRunningIDTaskState(int _id, String _state) {
        if (runningIDs == null) {
            return;
        }

        if (_state == null) {
            _state = "unknown";
        }

        for (RunningInfo ri : runningIDs) {
            if (ri.id == _id) {
                ri.state = _state.toLowerCase();
                // TraceManager.addDev("Updated state on UML diagram");
                TDiagramPanel tdp = getCurrentTDiagramPanel();
                if (tdp != null) {
                    tdp.repaint();
                }
                return;
            }
        }

    }

    public synchronized void addRunningID(int _id, int _nextCommand, String _progression, String _startTime, String _finishTime,
            String _transStartTime, String _transFinishTime, String _state) {
        if (runningIDs == null) {
            runningIDs = new ArrayList<RunningInfo>();
        }
        RunningInfo ri = new RunningInfo();
        if (_state == null) {
            _state = "unknown";
        }
        ri.id = _id;
        ri.nextCommand = _nextCommand;
        ri.progression = _progression;
        ri.startTime = _startTime;
        ri.finishTime = _finishTime;
        ri.transStartTime = _transStartTime;
        ri.transFinishTime = _transFinishTime;
        ri.state = _state.toLowerCase();
        runningIDs.add(ri);
        // TraceManager.addDev("Running id " + id + " added");
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void addTransaction(int _id, SimulationTransaction st) {
        if (transactionMap.containsKey(_id)) {
            if (!transactionMap.get(_id).contains(st)) {
                transactionMap.get(_id).add(st);
            }
        } else {
            ArrayList<SimulationTransaction> ts = new ArrayList<SimulationTransaction>();
            ts.add(st);
            transactionMap.put(_id, ts);
        }
        /*
         * TDiagramPanel tdp = getCurrentTDiagramPanel(); if (tdp != null) {
         * tdp.repaint(); }
         */
    }

    public synchronized void addStatus(String task, String stat) {
        statusMap.put(task, stat);
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void addLoadInfo(int _id, double _load, long _energy) {
        if (loadIDs == null) {
            loadIDs = new ArrayList<LoadInfo>();
        }

        removeLoadId(_id);
        LoadInfo li = new LoadInfo();
        li.id = _id;
        li.load = _load;
        li.energy = _energy;
        loadIDs.add(li);
        // TraceManager.addDev("Running id " + _id + " added load=" + _load);
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void removeRunningId(Integer id) {
        if (runningIDs == null) {
            return;
        }

        for (RunningInfo ri : runningIDs) {
            if (ri.id == id.intValue()) {
                runningIDs.remove(ri);
                // TraceManager.addDev("Running id " + i + " removed");
                return;
            }
        }
        getCurrentTDiagramPanel().repaint();
    }

    public synchronized void removeLoadId(int _id) {
        if (loadIDs == null) {
            return;
        }

        for (LoadInfo li : loadIDs) {
            if (li.id == _id) {
                loadIDs.remove(li);
                // TraceManager.addDev("Running id " + i + " removed");
                return;
            }
        }
        getCurrentTDiagramPanel().repaint();
    }

    public void toggleGates() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)) {
            // TraceManager.addDev("Toggle gates");
            TClassDiagramPanel tdcp = (TClassDiagramPanel) tdp;
            tdcp.setGatesVisible(!tdcp.areGatesVisible());
            tdcp.checkAllMySize();
            tdcp.repaint();
            changeMade(tdcp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleSynchro() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)) {
            // TraceManager.addDev("Toggle synchro");
            TClassDiagramPanel tdcp = (TClassDiagramPanel) tdp;
            tdcp.setSynchroVisible(!tdcp.areSynchroVisible());
            tdcp.checkAllMySize();
            tdcp.repaint();
            changeMade(tdcp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleJava() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TActivityDiagramPanel)) {
            // TraceManager.addDev("Toggle synchro");
            TActivityDiagramPanel tadp = (TActivityDiagramPanel) tdp;
            tadp.setJavaVisible(!tadp.isJavaVisible());
            tadp.checkAllMySize();
            tadp.repaint();
            changeMade(tadp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleInternalComment() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.setInternalCommentVisible((tdp.getInternalCommentVisible() + 1) % 3);
            tdp.checkAllMySize();
            tdp.repaint();
            changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleAttr() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            // TraceManager.addDev("Toggle attributes");
            tdp.setAttributes((tdp.getAttributeState() + 1) % 3);
            tdp.checkAllMySize();
            tdp.repaint();
            changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleChannels() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)) {
            // TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel) tdp;
            tmltdp.setChannelsVisible(!tmltdp.areChannelsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleEvents() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)) {
            // TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel) tdp;
            tmltdp.setEventsVisible(!tmltdp.areEventsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleRequests() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)) {
            // TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel) tdp;
            tmltdp.setRequestsVisible(!tmltdp.areRequestsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleELN() {
        if (hidden == false) {
            hidden = true;
        } else {
            hidden = false;
        }
    }

    public boolean getHidden() {
        return hidden;
    }

    public boolean isAValidTabName(String name) {
        boolean b1, b2;// , b3, b4, b5, b6, b7;
        b1 = (name.substring(0, 1)).matches("[a-zA-Z]");
        b2 = name.matches("\\w*");
        return b1 && b2;
        // return name.matches("((\\w)*(\\s)*)*");
    }

    public void windowClosing(WindowEvent e) {
        // frame.setVisible(false);
        quitApplication();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    // TGUIACtions Event listener

    public void keyTyped(KeyEvent e) {
        TraceManager.addDev("KEY TYPED");
    }

    public void keyPressed(KeyEvent e) {
        TraceManager.addDev("KEY PRESSED: ");
    }

    public void keyReleased(KeyEvent e) {
        TraceManager.addDev("KEY RELEASED: ");
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        // TraceManager.addDev("Command:" + command);
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            // TraceManager.addDev("Stop Adding connector in maingui");
            tdp1.stopAddingConnector();
        }

        ActionPerformer.actionPerformed(this, evt, command, tdp1);

    }

    private class PopupListener extends MouseAdapter /* popup menus onto tabs */ {
        private MainGUI mgui;
        private JPopupMenu menu;

        private JMenuItem rename, remove, moveRight, moveLeft, newDesign, newAnalysis, newDeployment, newRequirement/* , newTMLDesign */,
                newTMLComponentDesign, newTMLArchi, newProactiveDesign, newTURTLEOSDesign, newNCDesign, sort, clone, newAttackTree, newFaultTree,
                newAVATARBD, newAVATARRequirement, newMAD, newTMLCP, newTMLMethodo, newAvatarMethodo, newAVATARDD, newSysmlsecMethodo, newSysCAMS,
                newELN, newVerificationProperty, clearVerificationInformation;
        private JMenuItem newAVATARAnalysis;

        public PopupListener(MainGUI _mgui) {
            mgui = _mgui;
            createMenu();
        }

        public void mousePressed(MouseEvent e) {
            if (activetdp != null)
                activetdp.tdmm.setSelection(-1, -1);
            checkForPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (activetdp != null)
                activetdp.tdmm.setSelection(-1, -1);
            checkForPopup(e);
        }

        public void mouseClicked(MouseEvent e) {
            if (activetdp != null)
                activetdp.tdmm.setSelection(-1, -1);
            checkForPopup(e);
        }

        private void checkForPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                Component c = e.getComponent();
                // TraceManager.addDev("e =" + e + " Component=" + c);
                updateMenu(mgui.getCurrentSelectedIndex());
                menu.show(c, e.getX(), e.getY());
            }
        }

        private void createMenu() {
            rename = createMenuItem("Rename");
            remove = createMenuItem("Remove");
            moveLeft = createMenuItem("Move to the left");
            moveRight = createMenuItem("Move to the right");
            sort = createMenuItem("Sort");
            clone = createMenuItem("Clone");
            newAnalysis = createMenuItem("New TURTLE Analysis");
            newDesign = createMenuItem("New TURTLE Design");
            newDeployment = createMenuItem("New TURTLE Deployment");

            newAttackTree = createMenuItem("New Attack Tree");
            newFaultTree = createMenuItem("New Fault Tree");

            newRequirement = createMenuItem("New TURTLE Requirement Diagram");

            newTMLMethodo = createMenuItem("New DIPLODOCUS Methodology");

            /* newTMLDesign = */
            createMenuItem("New Partitioning - Design");
            newTMLComponentDesign = createMenuItem("New Partitioning - Functional view");
            newTMLArchi = createMenuItem("New Partitioning - Architecture and Mapping");
            newTMLCP = createMenuItem("New Partitioning - Communication Pattern");
            newSysCAMS = createMenuItem("New SystemC-AMS Block Diagram");
            newELN = createMenuItem("New ELN Diagram");
            newProactiveDesign = createMenuItem("New Proactive Design");
            newTURTLEOSDesign = createMenuItem("New TURTLE-OS Design");
            newNCDesign = createMenuItem("New Network Calculus Design");
            newMAD = createMenuItem("New Modeling Assumptions Diagram");
            newAVATARRequirement = createMenuItem("New Requirement Diagrams");
            newAVATARAnalysis = createMenuItem("New Analysis");
            newAVATARBD = createMenuItem("New Design");
            newAVATARDD = createMenuItem("New Deployment Diagram");
            newAvatarMethodo = createMenuItem("New AVATAR Methodology");
            newSysmlsecMethodo = createMenuItem("New SysML-Sec Methodology");
            newVerificationProperty = createMenuItem("New Verification Tracking");
            clearVerificationInformation = createMenuItem("Clear Verification Backtracing");

            menu = new JPopupMenu("Views");
            menu.add(moveLeft);
            menu.add(moveRight);

            menu.addSeparator();

            menu.add(rename);
            menu.add(remove);

            menu.addSeparator();

            menu.add(sort);

            menu.addSeparator();

            menu.add(clone);

            menu.addSeparator();

            // TURTLE first and other old profiles. Old way
            if (!avatarOnly) {
                if (turtleOn) {
                    menu.add(newRequirement);
                    menu.add(newAnalysis);
                    menu.add(newDesign);
                    menu.add(newDeployment);
                    menu.addSeparator();
                }

                if (osOn) {
                    menu.add(newTURTLEOSDesign);
                    menu.addSeparator();
                    // TraceManager.addDev("OS is on");
                } else {
                    // TraceManager.addDev("OS is off");
                }

                if (proactiveOn) {
                    menu.add(newProactiveDesign);
                    menu.addSeparator();
                }

                if (ncOn) {
                    menu.add(newNCDesign);
                    menu.addSeparator();
                }

            }

            // Methodologies
            if (!avatarOnly) {
                if (systemcOn) {
                    menu.add(newTMLMethodo);
                }

            }

            if (avatarOn) {
                menu.add(newAvatarMethodo);
                menu.add(newSysmlsecMethodo);
            }
            menu.addSeparator();

            // diagrams
            if (!avatarOnly) {
                if (systemcOn) {

                    // menu.add(newTMLMethodo);
                    // menu.add(newTMLDesign);
                    menu.add(newTMLComponentDesign);
                    menu.add(newTMLCP);
                    menu.add(newTMLArchi);
                    menu.addSeparator();
                    menu.add(newSysCAMS);
                    menu.add(newELN);
                    menu.addSeparator();
                }
            }

            if (avatarOn) {
                // menu.addSeparator();
                // menu.add(newAvatarMethodo);

                menu.add(newMAD);
                menu.add(newAVATARRequirement);
                menu.add(newAttackTree);
                menu.add(newFaultTree);
                menu.add(newAVATARAnalysis);
                menu.add(newAVATARBD);
                if (experimentalOn) {
                    menu.add(newAVATARDD);
                }
            }

            if (experimentalOn) {
                menu.addSeparator();
                menu.add(newVerificationProperty);
            }

            menu.addSeparator();
            menu.add(clearVerificationInformation);

        }

        private JMenuItem createMenuItem(String s) {
            JMenuItem item = new JMenuItem(s);
            item.setActionCommand(s);
            item.addActionListener(listener);
            return item;
        }

        private void updateMenu(int index) {
            // TraceManager.addDev("UpdateMenu index=" + index);
            if (index < 1) {
                moveLeft.setEnabled(false);
            } else {
                moveLeft.setEnabled(true);
            }

            if (index + 1 < mgui.tabs.size()) {
                moveRight.setEnabled(true);
            } else {
                moveRight.setEnabled(false);
            }

            if (index < 0) {
                rename.setEnabled(false);
                remove.setEnabled(false);
                clone.setEnabled(false);
            } else {
                rename.setEnabled(true);
                remove.setEnabled(true);
                clone.setEnabled(true);
            }

            if (mgui.mainTabbedPane.getTabCount() < 2) {
                sort.setEnabled(false);
            } else {
                sort.setEnabled(true);
            }
        }

        private Action listener = new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = -3632935027104753332L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();
                String ac = item.getActionCommand();
                if (ac.equals("Rename")) {
                    mgui.requestRenameTab(mainTabbedPane.getSelectedIndex());
                } else if (ac.equals("Remove")) {
                    mgui.removeCurrentTab();
                } else if (ac.equals("Move to the left")) {
                    mgui.requestMoveLeftTab(mainTabbedPane.getSelectedIndex());
                } else if (ac.equals("Move to the right")) {
                    mgui.requestMoveRightTab(mainTabbedPane.getSelectedIndex());
                } else if (ac.equals("Sort")) {
                    GraphicLib.sortJTabbedPane(mgui.mainTabbedPane, mgui.tabs, 0, mgui.mainTabbedPane.getTabCount());
                    mgui.changeMade(null, -1);
                } else if (ac.equals("Clone")) {
                    mgui.cloneTab(mainTabbedPane.getSelectedIndex());
                } else if (ac.equals("New TURTLE Analysis")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAnalysis();
                } else if (ac.equals("New TURTLE Design")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newDesign();
                } else if (ac.equals("New TURTLE Deployment")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newDeployment();
                } else if (e.getSource() == newAttackTree) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAttackTree();
                } else if (e.getSource() == newFaultTree) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newFaultTree();
                } else if (ac.equals("New TURTLE Requirement Diagram")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newRequirement();
                } else if (e.getSource() == newTMLMethodo) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newDiplodocusMethodology();
                } else if (e.getSource() == newAvatarMethodo) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAvatarMethodology();
                } else if (e.getSource() == newSysmlsecMethodo) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newSysmlsecMethodology();
                } else if (ac.equals("New DIPLODOCUS Design")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newTMLDesign();
                } else if (e.getSource() == newTMLComponentDesign) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newTMLComponentDesign();
                } else if (e.getSource() == newTMLCP) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newTMLCP();
                } else if (e.getSource() == newTMLArchi) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newTMLArchi();
                } else if (ac.equals("New Proactive Design")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newProactiveDesign();
                } else if (ac.equals("New TURTLE-OS Design")) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newTURTLEOSDesign();
                } else if (e.getSource() == newNCDesign) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newNCDesign();
                } else if (e.getSource() == newAVATARBD) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAvatarBD();
                } else if (e.getSource() == newAVATARDD) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newADD();
                } else if (e.getSource() == newAVATARRequirement) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAvatarRequirement();
                } else if (e.getSource() == newMAD) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAvatarMADs();
                } else if (e.getSource() == newAVATARAnalysis) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newAvatarAnalysis();
                } else if (e.getSource() == newSysCAMS) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newSysCAMS();
                } else if (e.getSource() == newELN) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newELN();
                } else if (e.getSource() == newVerificationProperty) {
                    ModeManager.setMode(CREATE_NEW_PANEL, actions, mainBar, mgui);
                    mgui.newVerificationPropertyPanel();
                } else if (e.getSource() == clearVerificationInformation) {
                    mgui.clearBacktracing();
                }
            }
        };
    }

    /**
     * This adapter is constructed to handle mouse over component events.
     */
    private class MouseHandler extends MouseAdapter {

        private JLabel label;

        /**
         * ctor for the adapter.
         *
         * @param label the JLabel which will recieve value of the
         *              Action.LONG_DESCRIPTION key.
         */
        public MouseHandler(JLabel label) {
            setLabel(label);
        }

        public void setLabel(JLabel label) {
            this.label = label;
        }

        @Override
        public void mouseEntered(MouseEvent evt) {
            if (evt.getSource() instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) evt.getSource();
                Action action = button.getAction();
                if (action != null) {
                    String message = (String) action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }

    }

   

    // DraggableTabbedPaneCallbackInterface
    public void hasBeenDragged(int initialPosition, int destinationPosition) {

        TURTLEPanel p = tabs.get(initialPosition);
        TraceManager.addDev("Has been dragged: " + mainTabbedPane.getTitleAt(initialPosition));
        tabs.removeElementAt(initialPosition);
        tabs.insertElementAt(p, destinationPosition);
        mainTabbedPane.setSelectedIndex(destinationPosition);

        changeMade(null, -1);
        // frame.repaint();

    }

    public void compareSimulationTraces(SimulationTrace selectedST, boolean b) {
        cSimTrace = new JFrameCompareSimulationTraces(this, "Compare Simulation simulation", selectedST, true);
        cSimTrace.setIconImage(IconManager.img9);
        GraphicLib.centerOnParent(cSimTrace, 900, 600);
        cSimTrace.setVisible(true);
    }

    public void compareLatencyForXML(SimulationTrace selectedST, boolean b) {

        try {
            LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(1, this, selectedST, b, false, 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void latencyDetailedAnalysisForXML(SimulationTrace selectedST, boolean b, boolean compare, int j)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

        try {
            LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(2, this, selectedST, b, compare, j);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

  

} // Class MainGUI

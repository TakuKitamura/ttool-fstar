/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
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
 * Class MainGUI
 * Main window / Actions management / Mode management
 * Creation: 15/12/2003
 * Version: 1.1 21/12/2003
 * Version: 1.2 29/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.awt.image.*;
import javax.imageio.*;

import launcher.*;
import translator.*;
import myutil.*;

import ui.ad.*;
import ui.atd.*;
import ui.cd.*;
import ui.file.*;
import ui.avatarinteractivesimulation.*;
import ui.interactivesimulation.*;
import ui.iod.*;
import ui.req.*;
import ui.ebrdd.*;
import ui.sd.*;
import ui.ucd.*;
import ui.tree.*;
import ui.window.*;

import ui.osad.*;

import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmldd.*;
import ui.tmlcp.*;
import ui.tmlsd.*;

import ui.procsd.*;
import ui.prosmd.*;

// AVATAR
import ui.avatarbd.*;
import ui.avatarsmd.*;
import ui.avatarrd.*;
import ui.avatarmad.*;
import ui.avatarpd.*;
import ui.avatarcd.*;
import ui.avatarad.*;

import proverifspec.*;

public  class MainGUI implements ActionListener, WindowListener, KeyListener, PeriodicBehavior {

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

    public final static int LOTOS = 0;
    public final static int RT_LOTOS = 1;
    public final static int UPPAAL = 2;
    public final static int JAVA = 3;
    public final static int DESIGN = 4;

    public JFrame frame; //Main Frame
    public Container framePanel; //Main pane
    public Container panelForTab, panelForTree; //panelForAnalysisTab; //panelForDesignTab;
    public JSplitPane split;



    // Multi analysis / design / deployment
    public Vector tabs;

    // JTree
    public JDiagramTree dtree;
    public DiagramTreeModel dtm;

    // Actions
    public      TGUIAction [] actions;
    public      MouseHandler mouseHandler;
    public  KeyListener keyHandler;

    // Validation
    public Vector tclassesToValidate = new Vector();

    // Status bar
    private     JLabel status;

    //Menubar
    private JMenuBarTurtle jmenubarturtle;

    // Annex windows
    JFrameCode javaframe;
    JFrameBird birdframe;
    private boolean hasChanged = false;

    //public final static boolean analysis = false;

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
    public final static byte MODEL_PROVERIF_OK = 45;
    public final static byte EDIT_PROVERIF_OK = 46;
    public final static byte AVATAR_SYNTAXCHECKING_OK = 47;
    public final static byte PANEL_CHANGED = 48;

    public final static int INCREMENT = 10;

    public static Object BACK_COLOR;
    //public static Object BACK_COLOR;

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


    //TURTLE modeling graphic components
    private     JTabbedPane mainTabbedPane;
    private JToolBarMainTurtle mainBar;
    //private JPopupMenu menuTabbedPane;

    private TDiagramPanel activetdp;

    // Modified graphs
    private String modifiedaut;
    private String modifiedautdot;


    // JBirdPanel
    private JBirdPanel jbp;

    private int typeButtonSelected;
    private int idButtonSelected;

    private File file;
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

    //private int selectedAction = -1;

    // Interaction with simulators
    private ArrayList<RunningInfo> runningIDs;
    private ArrayList<LoadInfo> loadIDs;
    private JFrameInteractiveSimulation jfis;
    private JFrameAvatarInteractiveSimulation jfais;

    // Invariants
    Invariant currentInvariant;

    // Thread fof autosave
    PeriodicBehaviorThread pbt;

    public MainGUI(boolean _systemcOn, boolean _lotosOn, boolean _proactiveOn, boolean _tpnOn, boolean _osOn, boolean _uppaalOn, boolean _ncOn, boolean _avatarOn, boolean _proverifOn, boolean _experimental) {
        systemcOn = _systemcOn;
        lotosOn = _lotosOn;
        proactiveOn = _proactiveOn;
        tpnOn = _tpnOn;
        osOn = _osOn;
        uppaalOn = _uppaalOn;
        ncOn = _ncOn;
        avatarOn = _avatarOn;
        proverifOn = _proverifOn;
        experimentalOn = _experimental;

        currentInvariant = null;

        pbt = new PeriodicBehaviorThread(this, 120000); // save every two minutes

    }



    public boolean isAvatarOn() {
        return avatarOn;
    }


    public void build() {

        // Swing look and feel

        try {
            UIManager.setLookAndFeel(
                                     UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { ErrorGUI.exit(ErrorGUI.GUI);}

        // Creating main container
        frame = new JFrame("TTool");
        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
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

        if (ConfigurationTTool.IMGPath.length() > 0) {
            jfcimg = new JFileChooser(ConfigurationTTool.IMGPath);
        } else {
            jfcimg = new JFileChooser();
        }

        if (ConfigurationTTool.IMGPath.length() > 0) {
            jfcimgsvg = new JFileChooser(ConfigurationTTool.IMGPath);
        } else {
            jfcimgsvg = new JFileChooser();
        }

        if (ConfigurationTTool.LOTOSPath.length() > 0) {
            jfclot = new JFileChooser(ConfigurationTTool.LOTOSPath);
        } else {
            jfclot = new JFileChooser();
        }

        if (ConfigurationTTool.GGraphPath.length() > 0) {
            jfcggraph = new JFileChooser(ConfigurationTTool.GGraphPath);
        } else {
            jfcggraph = new JFileChooser();
        }

        if (ConfigurationTTool.TGraphPath.length() > 0) {
            jfctgraph = new JFileChooser(ConfigurationTTool.TGraphPath);
        } else {
            jfctgraph = new JFileChooser();
        }


        TFileFilter filter = new TFileFilter();
        jfc.setFileFilter(filter);

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
        panelForTab = new JPanel(); panelForTab.setLayout(new BorderLayout());
        //panelForTree = new JPanel(); panelForTree.setLayout(new BorderLayout());
        // Tree
        dtree = new JDiagramTree(this);
        dtree.setCellRenderer(new DiagramTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(dtree);
        JScrollPane scrollPane = new JScrollPane(dtree);
        scrollPane.setPreferredSize(new Dimension(200, 600));
        scrollPane.setMinimumSize(new Dimension(25, 200));
        jbp = new JBirdPanel(this);
        jbp.setPreferredSize(new Dimension(200, 200));
        JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, jbp);

        //split1.setLastDividerLocation(500);
        //panelForTree.add(scrollPane, BorderLayout.CENTER);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, split1, panelForTab);
        framePanel.add(split, BorderLayout.CENTER);
        //split1.resetToPreferredSizes();

        // Creating menus
        jmenubarturtle = new JMenuBarTurtle(this);
        frame.setJMenuBar(jmenubarturtle);

        //split1.setLastDividerLocation(split1.getHeight() * 4 / 5);
        //split1.setLastDividerLocation(900);

        // ToolBar
        //toolbarDesign = new Vector();
        //toolbarAnalysis       = new Vector();

        // Panels
        //analysisPanels = new Vector();
        //designPanels = new Vector();

    }

    private     void initActions() {
        actions = new TGUIAction[TGUIAction.NB_ACTION];
        for(int i=0; i<TGUIAction.NB_ACTION; i++) {
            actions[i] = new TGUIAction(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
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
        switch (type) {
        case -1:
            // Structural change
            break;
        case TDiagramPanel.NEW_COMPONENT:
            //TraceManager.addDev("New Component");
            tdp.structureChanged();
            break;
        case TDiagramPanel.NEW_CONNECTOR:
            //TraceManager.addDev("New Connector");
            tdp.structureChanged();
            break;
        case TDiagramPanel.REMOVE_COMPONENT:
            //TraceManager.addDev("Remove Component");
            tdp.structureChanged();
            break;
        case TDiagramPanel.MOVE_CONNECTOR:
            //TraceManager.addDev("Move Connector");
            tdp.structureChanged();
            break;
        case TDiagramPanel.CHANGE_VALUE_COMPONENT:
            //TraceManager.addDev("Value of component changed");
            tdp.valueChanged();
            break;
        case TDiagramPanel.MOVE_COMPONENT:
            //TraceManager.addDev("Component moved");
            break;
        default:

        }
        setMode(MODEL_CHANGED);
        Point p;
        if (tdp == null) {
            p = getCurrentSelectedPoint();
        } else {
            p = getPoint(tdp);
        }
        //TraceManager.addDev("Change made!");
        gtm.saveOperation(p);
        dtree.toBeUpdated();
    }

    public void setMethodologicalMode(byte m) {
        methoMode = m;
        switch(methoMode) {
        case METHO_ANALYSIS:
            break;
        case METHO_DESIGN:
            break;
        default:
        }
    }

    public void setMode(byte m) {
        mode = m;
        switch(mode) {
        case MainGUI.NOT_OPENED:
            activeActions(false);
            actions[TGUIAction.ACT_NEW].setEnabled(true);
            actions[TGUIAction.ACT_OPEN].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_TIF].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_SD].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_LAST].setEnabled(true);
            actions[TGUIAction.ACT_QUIT].setEnabled(true);
            actions[TGUIAction.ACT_ABOUT].setEnabled(true);
            actions[TGUIAction.ACT_TTOOL_CONFIGURATION].setEnabled(true);
            actions[TGUIAction.ACT_TURTLE_WEBSITE].setEnabled(true);
            actions[TGUIAction.ACT_TURTLE_DOCUMENTATION].setEnabled(true);
            actions[TGUIAction.ACT_DIPLODOCUS_DOCUMENTATION].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_SAVED_LOT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_SAVED_DOT].setEnabled(true);
            actions[TGUIAction.ACT_BISIMULATION].setEnabled(true);
            actions[TGUIAction.ACT_BISIMULATION_CADP].setEnabled(true);
            actions[TGUIAction.ACT_GRAPH_MODIFICATION].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_STAT_SAVED_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_SAVED_AUT].setEnabled(true);
            actions[TGUIAction.ACT_SCREEN_CAPTURE].setEnabled(true);
            actions[TGUIAction.ACT_TTOOL_WINDOW_CAPTURE].setEnabled(true);
            actions[TGUIAction.ACT_DEADLOCK_SEEKER_SAVED_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_BIRDEYES_EMB].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_WAVE].setEnabled(true);
            actions[TGUIAction.EXTERNAL_ACTION_1].setEnabled(true);
            actions[TGUIAction.EXTERNAL_ACTION_2].setEnabled(true);
            //actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RG_DIPLODOCUS].setEnabled(ConfigurationTTool.GGraphPath != null);
            actions[TGUIAction.ACT_VIEW_STAT_AUTDIPLODOCUS].setEnabled(ConfigurationTTool.GGraphPath != null);
            if (mainBar != null) {
                mainBar.activateSearch(false);
            }
            break;
        case OPENED:
            actions[TGUIAction.ACT_MERGE].setEnabled(true);
            actions[TGUIAction.ACT_NEW_DESIGN].setEnabled(true);
            actions[TGUIAction.ACT_NEW_ANALYSIS].setEnabled(true);
            //actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(true);
            //actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG].setEnabled(true);
            //actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AS].setEnabled(true);
            actions[TGUIAction.ACT_IMPORT_LIB].setEnabled(true);
            actions[TGUIAction.ACT_SAVE].setEnabled(false);
            if (TDiagramPanel.copyData != null) {
                actions[TGUIAction.ACT_PASTE].setEnabled(true);
            } else {
                actions[TGUIAction.ACT_PASTE].setEnabled(false);
            }
            actions[TGUIAction.ACT_DIAGRAM_CAPTURE].setEnabled(true);
            actions[TGUIAction.ACT_SVG_DIAGRAM_CAPTURE].setEnabled(true);
            actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE].setEnabled(true);
            actions[TGUIAction.ACT_GEN_DOC].setEnabled(true);
            actions[TGUIAction.ACT_GEN_DOC_REQ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_BIRDEYES].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS].setEnabled(true);
            actions[TGUIAction.ACT_DELETE].setEnabled(true);
            if (mainBar != null) {
                mainBar.activateSearch(true);
            }

            break;
        case MODEL_OK:
            actions[TGUIAction.ACT_SAVE_TIF].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            break;

        case MODEL_UPPAAL_OK:
            //actions[TGUIAction.ACT_SAVE_TIF].setEnabled(true);
            //actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            //actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);
            //actions[TGUIAction.ACT_GEN_JAVA].setEnabled(true);
            //actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(true);
            //actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            //actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            break;
        case MODEL_PROVERIF_OK:
            actions[TGUIAction.ACT_GEN_PROVERIF].setEnabled(true);
            break;
        case EDIT_PROVERIF_OK:
            actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            break;
        case GEN_DESIGN_OK:
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(true);
            break;
        case GEN_SYSTEMC_OK:
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(true);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(true);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(true);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);
            break;
        case AVATAR_SYNTAXCHECKING_OK:
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(true);
            break;
        case REQ_OK:
            //actions[TGUIAction.ACT_VIEW_MATRIX].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case RTLOTOS_OK:
            actions[TGUIAction.ACT_SAVE_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(true);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case UPPAAL_OK:
            //actions[TGUIAction.ACT_SAVE_LOTOS].setEnabled(true);
            //actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            //actions[TGUIAction.ACT_CHECKCODE].setEnabled(true);
            //actions[TGUIAction.ACT_SIMULATION].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case MODEL_CHANGED:
            actions[TGUIAction.ACT_SAVE].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_GEN_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(false);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(false);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(false);
            break;
        case METHO_CHANGED:
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(false);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(false);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_GEN_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            break;

        case PANEL_CHANGED:
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_GEN_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(false);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(false);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(false);

        case MODEL_SAVED:
            actions[TGUIAction.ACT_SAVE].setEnabled(false);
            break;
        case BACKWARD:
            actions[TGUIAction.ACT_BACKWARD].setEnabled(true);
            break;
        case NO_BACKWARD:
            actions[TGUIAction.ACT_BACKWARD].setEnabled(false);
            break;
        case FORWARD:
            actions[TGUIAction.ACT_FORWARD].setEnabled(true);
            break;
        case NO_FORWARD:
            actions[TGUIAction.ACT_FORWARD].setEnabled(false);
            break;
        case FORWARD_DIAG:
            actions[TGUIAction.ACT_NEXT_DIAG].setEnabled(true);
            actions[TGUIAction.ACT_LAST_DIAG].setEnabled(true);
            break;
        case BACKWARD_DIAG:
            actions[TGUIAction.ACT_FIRST_DIAG].setEnabled(true);
            actions[TGUIAction.ACT_BACK_DIAG].setEnabled(true);
            break;
        case NO_FORWARD_DIAG:
            actions[TGUIAction.ACT_NEXT_DIAG].setEnabled(false);
            actions[TGUIAction.ACT_LAST_DIAG].setEnabled(false);
            break;
        case NO_BACKWARD_DIAG:
            actions[TGUIAction.ACT_FIRST_DIAG].setEnabled(false);
            actions[TGUIAction.ACT_BACK_DIAG].setEnabled(false);
            break;
        case SIM_OK:
            actions[TGUIAction.ACT_VIEW_SIM].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_SIM_CHRONO].setEnabled(true);
            break;
        case SIM_KO:
            actions[TGUIAction.ACT_VIEW_SIM].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_SIM_CHRONO].setEnabled(false);
            break;
        case DTADOT_OK:
            actions[TGUIAction.ACT_SAVE_DTA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_DTADOT].setEnabled(true);
            break;
        case DTADOT_KO:
            actions[TGUIAction.ACT_SAVE_DTA].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_DTADOT].setEnabled(false);
            break;
        case RGDOT_OK:
            actions[TGUIAction.ACT_SAVE_RG].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGDOT].setEnabled(true);
            break;
        case RGDOT_KO:
            actions[TGUIAction.ACT_SAVE_RG].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGDOT].setEnabled(false);
            break;
        case TLSADOT_OK:
            actions[TGUIAction.ACT_SAVE_TLSA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_TLSADOT].setEnabled(true);
            break;
        case TLSADOT_KO:
            actions[TGUIAction.ACT_SAVE_TLSA].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_TLSADOT].setEnabled(false);
            break;
        case CUTCOPY_OK:
            actions[TGUIAction.ACT_CUT].setEnabled(true);
            actions[TGUIAction.ACT_COPY].setEnabled(true);
            //actions[TGUIAction.ACT_DELETE].setEnabled(true);
            actions[TGUIAction.ACT_SELECTED_CAPTURE].setEnabled(true);
            break;
        case CUTCOPY_KO:
            actions[TGUIAction.ACT_CUT].setEnabled(false);
            actions[TGUIAction.ACT_COPY].setEnabled(false);
            //actions[TGUIAction.ACT_DELETE].setEnabled(false);
            actions[TGUIAction.ACT_SELECTED_CAPTURE].setEnabled(false);
            break;
        case PASTE_OK:
            actions[TGUIAction.ACT_PASTE].setEnabled(true);
            break;
        case RGAUTDOT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGAUTDOT].setEnabled(true);
            break;
        case RGAUTDOT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(false);
            actions[TGUIAction.ACT_SAVE_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGAUTDOT].setEnabled(false);
            break;
        case RGAUT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(true);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(true);
            actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].setEnabled(true);
            break;
        case RGAUT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].setEnabled(false);
            break;
        case RGAUTPROJDOT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].setEnabled(true);
            break;
        case RGAUTPROJDOT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_SAVE_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].setEnabled(false);
            break;
        case EXPORT_LIB_OK:
            actions[TGUIAction.ACT_EXPORT_LIB].setEnabled(true);
            break;
        case EXPORT_LIB_KO:
            actions[TGUIAction.ACT_EXPORT_LIB].setEnabled(false);
            break;
        case VIEW_SUGG_DESIGN_OK:
            actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].setEnabled(true);
            break;
        case VIEW_SUGG_DESIGN_KO:
            actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].setEnabled(false);
            break;
        case NC_OK:
            actions[TGUIAction.ACT_NC].setEnabled(true);
            break;
        default:
            TraceManager.addDev("DEFAULT");
            activeActions(false);
        }
    }



    private void activeActions(boolean b) {
        for(int i=0; i<TGUIAction.NB_ACTION; i++) {
            actions[i].setEnabled(b);
        }
    }

    public String getModelFileFullPath() {
        if (file == null) {
            return "./";
        }
        return file.getAbsolutePath();
    }

    public void periodicAction() {
        //TraceManager.addDev("Autosaving ");
        if (file == null) {
            return;
        }

        File fileSave = new File(file.getAbsolutePath() + "~");
        TraceManager.addDev("Autosaving in " + fileSave.getAbsolutePath());
        status.setText("Autosaving in " + fileSave.getAbsolutePath());

        if(checkFileForSave(fileSave)) {
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
        for(int i=0; i<tabs.size(); i++) {
            panel = (TURTLEPanel)(tabs.get(i));
            panel.searchForText(text.toLowerCase(), elements);
        }

        gtm.setElementsOfSearchTree(elements);
        //TraceManager.addDev("Found " + elements.size() + " elements");
        dtree.forceUpdate();
    }


    public LinkedList<Invariant> getInvariants() {
        return gtm.getInvariants();
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
            return ((AvatarDesignPanel)tp).hasCheckableMasterMutex();
        }

        return null;
    }

    public void removeAllMutualExclusionWithMasterMutex() {
        TURTLEPanel tp = getCurrentTURTLEPanel();

        if (tp instanceof AvatarDesignPanel) {
            ((AvatarDesignPanel)tp).removeAllMutualExclusionWithMasterMutex();
        }

    }




    private int addAnalysisPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AnalysisPanel ap = new AnalysisPanel(this);
        tabs.add(index, ap); // should look for the first
        //mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens analysis diagrams");
        mainTabbedPane.add(ap.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open analysis diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        ap.init();
        //TraceManager.addDev("Main analysis added");
        return index;
    }

    private int addTMLCPPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        TMLCommunicationPatternPanel tmlcpp = new TMLCommunicationPatternPanel(this);
        tabs.add( index, tmlcpp ); // should look for the first
        //mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens analysis diagrams");
        mainTabbedPane.add(tmlcpp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open CP diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        tmlcpp.init();
        //TraceManager.addDev("Main analysis added");
        return index;
    }

    private int addAvatarAnalysisPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarAnalysisPanel aap = new AvatarAnalysisPanel(this);
        tabs.add(index, aap); // should look for the first
        //mainTabbedPane.addTab(name, IconManager.imgic17, ap.tabbedPane, "Opens analysis diagrams");
        mainTabbedPane.add(aap.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open analysis diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic17);
        aap.init();
        //TraceManager.addDev("Main analysis added");
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

    private int addAvatarDesignPanel(String name, int index) {
        if (index == -1) {
            index = tabs.size();
        }
        AvatarDesignPanel adp = new AvatarDesignPanel(this);
        tabs.add(index, adp);
        mainTabbedPane.add(adp.tabbedPane, index);
        mainTabbedPane.setToolTipTextAt(index, "Open AVATAR design diagrams");
        mainTabbedPane.setTitleAt(index, name);
        mainTabbedPane.setIconAt(index, IconManager.imgic80);
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        adp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        arp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        amadsp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        if (addDefaultElements) {
            dp.initElements();
        }
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        //ystem.out.println("Design added");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        //ystem.out.println("Design added");
        return index;
    }

    public Vector<String> getAllTMLTaskNames() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel)tp).getAllTMLTaskNames(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

    public Vector<String> getAllTMLCP() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLCommunicationPatternPanel) {
                list.add(mainTabbedPane.getTitleAt(i));
            }
        }
        return list;
    }

    public void updateAllReferences() {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel)tp).tmlctdp.delayedLoad();
                //((TMLComponentDesignPanel)tp).tmlctdp.updatePorts();
            }
        }
    }

    public void updateAllPorts() {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                //((TMLComponentDesignPanel)tp).tmlctdp.delayedLoad();
                ((TMLComponentDesignPanel)tp).tmlctdp.updatePorts();
            }
        }
    }

    public Vector<String> getAllTMLCommunicationNames() {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLDesignPanel) {
                list.addAll(((TMLDesignPanel)tp).getAllTMLCommunicationNames(mainTabbedPane.getTitleAt(i)));
            } else if (tp instanceof TMLComponentDesignPanel) {
                list.addAll(((TMLComponentDesignPanel)tp).getAllTMLCommunicationNames(mainTabbedPane.getTitleAt(i)));
            }
        }
        return list;
    }

   public Vector<String> getAllTMLEventNames() {
     TURTLEPanel tp;
    Vector<String> list = new Vector<String>();
 
     for( int i = 0; i < tabs.size(); i++ ) {
       tp = ( TURTLEPanel )( tabs.elementAt(i) );
       if ( tp instanceof TMLDesignPanel ) {
         list.addAll( ( (TMLDesignPanel)tp ).getAllTMLEventNames( mainTabbedPane.getTitleAt(i) ) );
       } else if ( tp instanceof TMLComponentDesignPanel ) {
         list.addAll( ( (TMLComponentDesignPanel)tp ).getAllTMLEventNames( mainTabbedPane.getTitleAt(i) ) );
       }
     }
     return list;
   }

    public Vector<String> getAllNonMappedTMLTaskNames(TMLArchiDiagramPanel tadp, String ref, String name) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        boolean b;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((TMLDesignPanel)tp).getAllNonMappedTMLTaskNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
            if (tp instanceof TMLComponentDesignPanel) {
                b = (mainTabbedPane.getTitleAt(i).compareTo(ref) == 0);
                list.addAll(((TMLComponentDesignPanel)tp).getAllNonMappedTMLPrimitiveComponentNames(mainTabbedPane.getTitleAt(i), tadp, b, name));
            }
        }

        return list;
    }

    public Vector<String> getAllCompositeComponent(TMLComponentTaskDiagramPanel tcdp) {
        TURTLEPanel tp;
        Vector<String> list = new Vector<String>();
        boolean b;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel)  {
                if (((TMLComponentDesignPanel)tp).tmlctdp != tcdp) {
                    list.addAll(((TMLComponentDesignPanel)tp).getAllCompositeComponent(mainTabbedPane.getTitleAt(i)));
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        ncp.init();
        //TraceManager.addDev("TURTLE OS Design added index=" + index);
        return index;
    }

    /*private int addAvatarDesignPanel(String name, int index) {

      if (index == -1) {
      index = tabs.size();
      }
      AvatarDesignPanel avdp = new AvatarDesignPanel(this);
      tabs.add(index, avdp);
      mainTabbedPane.add(avdp.tabbedPane, index);
      mainTabbedPane.setToolTipTextAt(index, "Open AVATAR Design");
      mainTabbedPane.setTitleAt(index, name);
      mainTabbedPane.setIconAt(index, IconManager.imgic60);
      //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
      avdp.init();
      //TraceManager.addDev("TURTLE OS Design added index=" + index);
      return index;
      }*/

    /*private int addAvatarRequirementPanel(String name, int index) {

      if (index == -1) {
      index = tabs.size();
      }
      AvatarRequirementPanel arp = new AvatarRequirementPanel(this);
      tabs.add(index, arp);
      mainTabbedPane.add(arp.tabbedPane, index);
      mainTabbedPane.setToolTipTextAt(index, "Open AVATAR Requirement");
      mainTabbedPane.setTitleAt(index, name);
      mainTabbedPane.setIconAt(index, IconManager.imgic60);
      //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
      arp.init();
      //TraceManager.addDev("TURTLE OS Design added index=" + index);
      return index;
      }*/

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
        //mainTabbedPane.addTab(name, IconManager.imgic14, dp.tabbedPane, "Opens design diagrams");
        dp.init();
        //ystem.out.println("Design added");
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
        //ystem.out.println("Design added");
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

    public void setIODName(int analysisIndex, String name) {
        AnalysisPanel ap = (AnalysisPanel)(tabs.elementAt(analysisIndex));
        ap.tabbedPane.setTitleAt(0, name);
    }

    public void setDeploymentName(int deploymentIndex, String name) {
        DeploymentPanel dp = (DeploymentPanel)(tabs.elementAt(deploymentIndex));
        dp.tabbedPane.setTitleAt(0, name);
    }

    public void setNCName(int ncIndex, String name) {
        NCPanel ncp = (NCPanel)(tabs.elementAt(ncIndex));
        ncp.tabbedPane.setTitleAt(0, name);
    }



    // add main panel for editing TURTLE diagrams
    private void addTURTLEPanel() {

        //TraceManager.addDev("New TURTLE Panels");
        // tabbed pane
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setBackground(ColorManager.MainTabbedPane);
        mainTabbedPane.setForeground(Color.black);

        BACK_COLOR = UIManager.get("TabbedPane.selected");
        //UIManager.put("TabbedPane.tabAreaBackground", ColorManager.MainTabbedPaneBack);
        UIManager.put("TabbedPane.selected", ColorManager.MainTabbedPaneSelect);
        //UIManager.put("TabbedPane.darkShadow", Color.black);
        UIManager.put("TabbedPane.focus", Color.blue);
        /*UIManager.put("TabbedPane.highlight", Color.blue);
          UIManager.put("TabbedPane.lightHighlight", Color.red);
          UIManager.put("TabbedPane.shadow", Color.black);
          UIManager.put("TabbedPane.darkShadow", Color.magenta);
          UIManager.put("TabbedPane.focus", Color.green);*/
        SwingUtilities.updateComponentTreeUI(mainTabbedPane);
        mainTabbedPane.setOpaque(true);


        ChangeListener cl = new ChangeListener() {
                public void stateChanged(ChangeEvent e){
                    paneAction(e);
                }
            };
        mainTabbedPane.addChangeListener(cl);
        panelForTab.add(mainTabbedPane, BorderLayout.CENTER);
        mainTabbedPane.addMouseListener(new PopupListener(this));

        tabs = new Vector();

        frame.setVisible(true);
    }

    public Vector getTabs() {
        return tabs;
    }

    public String getTitleOf(TDiagramPanel _tdp) {
        TURTLEPanel panel;
        for(int i=0; i<tabs.size(); i++) {
            panel = (TURTLEPanel)(tabs.get(i));
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
        for(int i=0; i<mainTabbedPane.getTabCount(); i++) {
            if (mainTabbedPane.getTitleAt(i).compareTo(s) == 0) {
                return (TURTLEPanel)(tabs.elementAt(i));
            }
        }
        return null;
    }


    // Creates the status bar.
    private     JLabel createStatusBar()  {
        status = new JLabel("Ready...");
        status.setBorder(BorderFactory.createEtchedBorder());
        return status;
    }

    public void activate(JMenu jm, boolean b) {
        JMenuItem im;
        for(int i=0; i<jm.getItemCount(); i++) {
            im = jm.getItem(i);
            if (im != null)
                im.setEnabled(b);
        }
    }

    public void start() {
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
                //TraceManager.addDev("Setting window attributes");
            } catch (Exception e) {
                //TraceManager.addDev("Window positioning has failed: " + e.getMessage());
            }
        }

        if (!positioned) {
            frame.setBounds(100, 100, 800, 600);
            // jdk 1.4 or more
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            //TraceManager.addDev("No default window attributes");
        }

        frame.setVisible(true);
    }

    public void newTurtleModeling()     {
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
            for(int i=0; i<tabs.size(); i++) {
                ((TURTLEPanel)(tabs.elementAt(i))).tabbedPane.removeAll();
            }

            tabs = null;
            mainTabbedPane = null;
            panelForTab.removeAll();
            activetdp = null;

            gtm = null;
            tclassesToValidate = new Vector();
            MasterGateManager.reinitNameRestriction();

            typeButtonSelected = - 1;
            idButtonSelected = -1;

            //activeDiagramToolBar = null;

            dtree.reinit();
            dtree.forceUpdate();
            frame.setTitle("TTool");
            frame.repaint();
        }
    }

    public void setStatusBarText(String s) {
        // captitalizeFirstLetter
        status.setText(s.substring(0, 1).toUpperCase() + s.substring(1, s.length()));
    }

    public void reinitMainTabbedPane() {
        mainTabbedPane.removeAll();
        tabs.removeAllElements();
    }

    public void newDesign() {
        //TraceManager.addDev("NEW DESIGN");
        addDesignPanel("Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newDeployment() {
        //TraceManager.addDev("NEW DESIGN");
        addDeploymentPanel("Deployment", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newAnalysis() {
        //TraceManager.addDev("NEW ANALYSIS");
        addAnalysisPanel("Analysis", 0);
        ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }

    public void newAvatarAnalysis() {
        //TraceManager.addDev("NEW ANALYSIS");
        addAvatarAnalysisPanel("Analysis", 0);
        //((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }


    public void newCommunicationPattern() {
        //TraceManager.addDev("NEW ANALYSIS");
        //addCommunicationPatternPanel("CP", 0);
        ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }

    public void newTMLDesign() {
        //TraceManager.addDev("NEW DESIGN");
        addTMLDesignPanel("DIPLODOCUS_Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newDiplodocusMethodology() {
        //TraceManager.addDev("NEW DESIGN");
        addDiplodocusMethodologyPanel("DIPLODOCUS_Methodology", -1, true);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newTMLComponentDesign() {
        //TraceManager.addDev("NEW DESIGN");
        addTMLComponentDesignPanel("DIPLODOCUS_C_Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newTMLCP() {
        //TraceManager.addDev("NEW ANALYSIS");
        addTMLCPPanel("CP", 0);
        ((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }

    public void newTMLArchi() {
        TraceManager.addDev("NEW DIPLO Architecture");
        addTMLArchiPanel("DIPLODOCUS Architecture", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();
    }

    public void newTURTLEOSDesign() {
        //TraceManager.addDev("NEW DESIGN");
        addTURTLEOSDesignPanel("TURTLE-OS Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
    }

    public void newNCDesign() {
        //TraceManager.addDev("NEW NC DESIGN");
        addNCDesignPanel("NC Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
    }

    public void newAvatarBD() {
        TraceManager.addDev("NEW AVATAR BD");
        addAvatarDesignPanel("AVATAR Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
    }

    public void newAvatarRequirement() {
        TraceManager.addDev("NEW AVATAR Requirement");
        addAvatarRequirementPanel("AVATAR Requirements", 0);
        //((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
    }

    public void newAvatarMADs() {
        TraceManager.addDev("NEW AVATAR MAD");
        addAvatarMADPanel("MADs", 0);
        //((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
    }

    public void newProactiveDesign() {
        //TraceManager.addDev("NEW DESIGN");
        int index = addProActiveDesignPanel("ProActive Design", -1);
        ((TURTLEPanel)tabs.elementAt(tabs.size()-1)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(tabs.size()-1);
        //paneAction(null);
        //frame.repaint();*/
    }

    public void newAttackTree() {
        //TraceManager.addDev("NEW ANALYSIS");
        addAttackTreePanel("Attack Trees", 0);
        //((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }

    public void newRequirement() {
        //TraceManager.addDev("NEW ANALYSIS");
        addRequirementPanel("Requirements", 0);
        //((TURTLEPanel)tabs.elementAt(0)).tabbedPane.setSelectedIndex(0);
        mainTabbedPane.setSelectedIndex(0);
        //paneAction(null);
        //frame.repaint();
    }


    // Action listeners
    public void newProject() {
        if (mode == NOT_OPENED) {
            //mode = ProjectManager.TM_OPENED;
            newTurtleModeling();
            //gtm.saveOperation(tcdp);
            file = null;
            frame.setTitle("TTool: unsaved project");
        } else {
            //  check if previous modeling is saved
            boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
            if (b) {
                if (!saveBeforeAction("Save and Start New Modeling", "Start New modeling")) {
                    return;
                }
                /*int back = JOptionPane.showConfirmDialog(frame, "Modeling has not been saved\nDo you really want to open a new one ?", "Attention: current modeling not saved ?", JOptionPane.OK_CANCEL_OPTION);
                  if (back == JOptionPane.CANCEL_OPTION) {
                  return;       */
                /*}*/
            }
            // close current modeling
            closeTurtleModeling();

            // opens a new one
            newTurtleModeling();

            //gtm.saveOperation(tcdp);

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

    public String loadFile(File f) {
        String s = null;

        if(checkFileForOpen(f)) {
            try {
                FileInputStream fis = new FileInputStream(f);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
            } catch (OutOfMemoryError er) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + er.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return s;
    }

    public void saveFile(File f, String data, String msg) {
        if(checkFileForSave(f)) {
            try {
                if (data == null) {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data.getBytes());
                fos.close();
                JOptionPane.showMessageDialog(frame, msg + " " + f.getAbsolutePath(), "Saving", JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public String loadLibrary() {
        File libfile;

        int returnVal = jfclib.showDialog(frame, "Import library");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        libfile = jfclib.getSelectedFile();
        libfile = FileUtils.addFileExtensionIfMissing(libfile, TLibFilter.getExtension());

        return loadFile(libfile);
    }

    public String[] loadLotosSpec() {
        File lotfile;

        int returnVal = jfclot.showDialog(frame, "Load RT-LOTOS specification");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        lotfile = jfclot.getSelectedFile();
        lotfile = FileUtils.addFileExtensionIfMissing(lotfile, RTLFileFilter.getExtension());

        String spec = loadFile(lotfile);
        if (spec == null) {
            return null;
        }

        String [] ret = new String[2];
        ret[0] = lotfile.getName();
        ret[1] = spec;
        return ret;
    }

    public void saveLotosSpec(String data) {
        File lotfile;

        int returnVal = jfclot.showDialog(frame, "Save RT-LOTOS specification");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
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

        int returnVal = jfctgraph.showDialog(frame, "Save last DTA (textual form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        dtafile = jfctgraph.getSelectedFile();
        dtafile = FileUtils.addFileExtensionIfMissing(dtafile, DTAFileFilter.getExtension());

        saveFile(dtafile, tdata, "Textual DTA saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last DTA (graphical form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        dtafile = jfcggraph.getSelectedFile();
        dtafile = FileUtils.addFileExtensionIfMissing(dtafile, TDotFilter.getExtension());

        saveFile(dtafile, gdata, "Graphical DTA saved under");
    }

    public String saveTPNNDRFormat(String tpn) {
        String s = file.getAbsolutePath();
        int l = s.length();
        String myFile = s.substring(0, l-4) + ".ndr";
        try {
            FileUtils.saveFile(myFile, tpn);
        } catch (Exception e) {
            return "TPN could not be saved in myFile: " +e.getMessage();
        }
        return "TPN saved in " +myFile;
    }

    public void saveRG(String tdata, String gdata) {
        File rgfile;

        /* textual form */
        RGFileFilter filter = new RGFileFilter();
        jfctgraph.setFileFilter(filter);

        int returnVal = jfctgraph.showDialog(frame, "Save last RG (textual form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfctgraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, RGFileFilter.getExtension());

        saveFile(rgfile, tdata, "Textual RG saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last RG (graphical form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
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
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        tlsafile = jfctgraph.getSelectedFile();
        tlsafile = FileUtils.addFileExtensionIfMissing(tlsafile, TLSAFileFilter.getExtension());

        saveFile(tlsafile, tdata, "Textual TLSA saved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last TLSA (graphical form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
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
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfctgraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, AUTFileFilter.getExtension());

        saveFile(rgfile, tdata, "Textual RG/AUTsaved under");

        /* graphical form */
        returnVal = jfcggraph.showDialog(frame, "Save last RG/AUT (graphical form)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        rgfile = jfcggraph.getSelectedFile();
        rgfile = FileUtils.addFileExtensionIfMissing(rgfile, TDotFilter.getExtension());

        saveFile(rgfile, gdata, "Graphical RG/AUT saved under");
    }



    public String[] loadGGraph() {
        File gfile;

        int returnVal = jfcggraph.showDialog(frame, "Load Graph (DOT format)");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        gfile = jfcggraph.getSelectedFile();
        gfile = FileUtils.addFileExtensionIfMissing(gfile, TDotFilter.getExtension());

        String spec = loadFile(gfile);
        if (spec == null) {
            return null;
        }

        String [] ret = new String[2];
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

            String [] ret = new String[2];
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

        int returnVal = jfctgraph.showDialog(frame, "Load AUT graph");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        autfile = jfctgraph.getSelectedFile();
        autfile = FileUtils.addFileExtensionIfMissing(autfile, AUTFileFilter.getExtension());

        String spec = loadFile(autfile);
        if (spec == null) {
            return null;
        }

        String [] ret = new String[2];
        ret[0] = autfile.getName();
        ret[1] = spec;
        return ret;
    }

    public void updateLastOpenFile(File file) {
        if (ConfigurationTTool.LastOpenFileDefined) {
            ConfigurationTTool.LastOpenFile = file.getPath();
            // Change name of action
            actions[TGUIAction.ACT_OPEN_LAST].setName(TGUIAction.ACT_OPEN_LAST, ConfigurationTTool.LastOpenFile);
        }
    }

    // Only if a project is already opened
    public void mergeProject() {
        //TraceManager.addDev("Merge");
        File filetmp = file;
        int returnVal = jfc.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filetmp = jfc.getSelectedFile();
        }

        String s = null;
        String oldmodeling = gtm.makeXMLFromTurtleModeling(-1);
        if(checkFileForOpen(filetmp)) {
            try {
                FileInputStream fis = new FileInputStream(filetmp);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // close current modeling
            closeTurtleModeling();

            // open the new TURTLE modeling
            newTurtleModeling();

            //TraceManager.addDev("Loading");
            // load the new TURTLE modeling
            try {
                //TraceManager.addDev("Merging");
                gtm.loadModelingFromXML(gtm.mergeTURTLEGModeling(oldmodeling, s));
                //gtm.saveOperation(tcdp);
                frame.setTitle("TTool: " + file.getAbsolutePath());
                makeLotosFile();

            } catch (MalformedModelingException mme) {
                JOptionPane.showMessageDialog(frame, "Modeling could not be merged (unsupported selected file) ", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
            }
            dtree.forceUpdate();
        }

    }


    public void openProject() {
        // check if a current modeling is opened
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b) {
            if (!saveBeforeAction("Save and Open", "Open")) {
                return;
            }
            /*  int back = JOptionPane.showConfirmDialog(frame, "Current modeling has not been saved\nDo you really want to open a new one ?", "To quit, or not to quit ?", JOptionPane.OK_CANCEL_OPTION);
                if (back == JOptionPane.CANCEL_OPTION) {
                return;
                }*/
        }

        //jfc.setApproveButtonText("Open");
        int returnVal = jfc.showOpenDialog(frame);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            return;
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
        }

        String s = null;
        if(checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // close current modeling
            closeTurtleModeling();

            // open the new TURTLE modeling
            newTurtleModeling();

            // Update configuration
            updateLastOpenFile(file);

            //TraceManager.addDev("Loading");
            // load the new TURTLE modeling
            try {
                gtm.loadModelingFromXML(s);
                //gtm.saveOperation(tcdp);
                frame.setTitle("TTool: " + file.getAbsolutePath());
                makeLotosFile();

            } catch (MalformedModelingException mme) {
                JOptionPane.showMessageDialog(frame, "Modeling could not be loaded (unsupported file) ", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
                frame.setTitle("TToolt: unamed project");
            }
            dtree.forceUpdate();
        }
    }

    public void openLastProject() {
        // Check if a current modeling is opened
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b) {
            if (!saveBeforeAction("Save and Open", "Open")) {
                return;
            }
        }


        file = new File(ConfigurationTTool.LastOpenFile);

        String s = null;
        if(checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // close current modeling
            closeTurtleModeling();

            // open the new TURTLE modeling
            newTurtleModeling();

            //TraceManager.addDev("Loading");
            // load the new TURTLE modeling
            try {
                gtm.loadModelingFromXML(s);
                //gtm.saveOperation(tcdp);
                frame.setTitle("TTool: " + file.getAbsolutePath());
                makeLotosFile();

            } catch (MalformedModelingException mme) {
                JOptionPane.showMessageDialog(frame, "Modeling could not be loaded (unsupported file) ", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
                frame.setTitle("TTool: unamed project");
            }
            dtree.forceUpdate();

        }

        //Added by Solange
        //TURTLEPanel tp = getCurrentTURTLEPanel();
        //gtm.generateLists((ProactiveDesignPanel)tp);
        //
    }

    public void saveAsLibrary(String data) {
        File libfile;

        int returnVal = jfclib.showDialog(frame, "Export library");
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        libfile = jfclib.getSelectedFile();
        libfile = FileUtils.addFileExtensionIfMissing(libfile, TLibFilter.getExtension());

        if(checkFileForSave(libfile)) {
            try {
                if (data == null) {
                    throw new Exception("Selected data corrupted");
                }
                FileOutputStream fos = new FileOutputStream(libfile);
                fos.write(data.getBytes());
                fos.close();
                JOptionPane.showMessageDialog(frame, "Modeling was correctly saved under a TURTLE library named " + libfile.getName(), "Saving", JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
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
            //frame.setTitle("TURTLE Toolkit: " + file.getAbsolutePath());
            makeLotosFile();
        }
    }

    public boolean saveTIF() {
        int returnVal = jfctif.showSaveDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfctif.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TTIFFilter.getExtension());
        }

        if(checkFileForSave(file)) {
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
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return false;
    }

    public boolean openTIF() {
        //jfc.setApproveButtonText("Open");
        int returnVal = jfctif.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfctif.getSelectedFile();
        }

        String s = null;
        if(checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
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
        //jfc.setApproveButtonText("Open");
        int returnVal = jfcmsc.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcmsc.getSelectedFile();
        }

        String s = null;
        if(checkFileForOpen(file)) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int nb = fis.available();

                byte [] ba = new byte[nb];
                fis.read(ba);
                fis.close();
                s = new String(ba);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be opened because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
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

    public boolean saveProject() {
        if (file == null) {
            //jfc.setApproveButtonText("Save");
            int returnVal = jfc.showSaveDialog(frame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                file = jfc.getSelectedFile();
                file = FileUtils.addFileExtensionIfMissing(file, TFileFilter.getExtension());
            }
        }

        if(checkFileForSave(file)) {
            String s = gtm.makeXMLFromTurtleModeling(-1);
            try {
                if (gtm == null) {
                    throw new Exception("Internal model Error 1");
                }
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
            } catch(Exception e) {
                JOptionPane.showMessageDialog(frame, "File could not be saved because " + e.getMessage(), "File Error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return false;
    }

    public boolean checkFileForOpen(File file) {
        boolean ok = true;
        String pb = "";

        //TraceManager.addDev("File path=" + file.getPath() + " name=" + file.getName());

        if (file == null) {
            return false;
        }

        try {
            if (file != null) {
                if (!file.exists()) {
                    pb  = "File doesn't exit";
                    ok = false;
                }
                if ((ok == true) && (!file.canRead())) {
                    pb  = "File is read protected";
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
                        pb  = "File could not be created";
                        ok = false;
                    }
                    if (!file.canWrite()) {
                        pb  = "File is write protected";
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
        //TDiagramPanel tdp1 = ((TDiagramPanel)(designPanels.elementAt(mainDesignTabbedPane.getSelectedIndex()));
        //tdp1.insertLibrary(tdp1.getMinX(), tdp1.getMinY());
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            tdp1.insertLibrary(tdp1.getMinX(), tdp1.getMinY());
        }
    }

    public void exportLibrary() {
        //TDiagramPanel tdp1 = (TDiagramPanel)(designPanels.elementAt(mainDesignTabbedPane.getSelectedIndex()));
        //tdp1.saveAsLibrary();
        getCurrentTDiagramPanel().saveAsLibrary();
    }

    public void makeLotosFile() {
        String s = file.getAbsolutePath();
        int l = s.length();
        String myFile = s.substring(0, l-4);
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

    public boolean saveBeforeAction(String str1, String str2) {
        Object[] options = { str1, str2, "CANCEL" };
        int back = JOptionPane.showOptionDialog(frame, "Modeling has not been saved", "Warning",
                                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                                null, options, options[0]);
        //TraceManager.addDev("back= " + back);
        if (back == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        if (back == JOptionPane.YES_OPTION) {
            saveProject();
        }
        return true;
    }

    public void quitApplication() {
        boolean b = actions[TGUIAction.ACT_SAVE].isEnabled();
        if (b) {
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
                ConfigurationTTool.saveConfiguration();
                //TraceManager.addDev("Configuration written to file");
            }
        } catch (Exception e) {}


        System.exit(0);
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
        //TraceManager.addDev("backward");
        gtm.backward();
        setMode(MODEL_CHANGED);
        dtree.toBeUpdated();
    }

    public void forward() {
        //TraceManager.addDev("forward");
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
        tdp.updateComponentsAfterZoom();
        updateZoomInfo();
    }

    public void updateZoomInfo() {
        String s = "";
        int zoom = (int)(getCurrentTDiagramPanel().getZoom()*100);
        if (zoom < 100) {
            s = "0" + zoom + "%";
        } else {
            s += zoom + "%";
        }
        actions[TGUIAction.ACT_SHOW_ZOOM].setName(TGUIAction.ACT_SHOW_ZOOM, s);
    }

    public void firstDiag() {
        getCurrentJTabbedPane().setSelectedIndex(0);
        /*if (methoMode == METHO_ANALYSIS) {
          mainAnalysisTabbedPane.setSelectedIndex(0);
          } else {
          mainDesignTabbedPane.setSelectedIndex(0);
          }*/
    }

    public void backDiag() {
        getCurrentJTabbedPane().setSelectedIndex(Math.max(0, getCurrentJTabbedPane().getSelectedIndex() - 1));
        /*if (methoMode == METHO_ANALYSIS) {
          mainAnalysisTabbedPane.setSelectedIndex(Math.max(0, mainAnalysisTabbedPane.getSelectedIndex() - 1));
          } else {
          mainDesignTabbedPane.setSelectedIndex(Math.max(0, mainDesignTabbedPane.getSelectedIndex() - 1));
          }*/

    }

    public void nextDiag() {
        getCurrentJTabbedPane().setSelectedIndex(Math.min(getCurrentJTabbedPane().getTabCount(), getCurrentJTabbedPane().getSelectedIndex() + 1));
        /*if (methoMode == METHO_ANALYSIS) {
          mainAnalysisTabbedPane.setSelectedIndex(Math.min(mainAnalysisTabbedPane.getTabCount(), mainAnalysisTabbedPane.getSelectedIndex() + 1));
          } else {
          mainDesignTabbedPane.setSelectedIndex(Math.min(mainDesignTabbedPane.getTabCount(), mainDesignTabbedPane.getSelectedIndex() + 1));
          }*/
    }

    public void lastDiag() {
        getCurrentJTabbedPane().setSelectedIndex(getCurrentJTabbedPane().getTabCount() - 1);
        /*if (methoMode == METHO_ANALYSIS) {
          mainAnalysisTabbedPane.setSelectedIndex(mainAnalysisTabbedPane.getTabCount() - 1);
          } else {
          mainDesignTabbedPane.setSelectedIndex(mainDesignTabbedPane.getTabCount() - 1);
          }*/
    }

    public void aboutVersion() {
        /*JOptionPane.showMessageDialog(frame,
          "TTool version 0.2 - Ludovic Apvrille",
          "About Ttool",
          JOptionPane.INFORMATION_MESSAGE);*/
        JFrameBasicText jft = new JFrameBasicText("About TTool ...", DefaultText.getAboutText(), IconManager.imgic324);
        jft.setIconImage(IconManager.img8);
        jft.setSize(700, 800);
        GraphicLib.centerOnParent(jft);
        jft.setVisible(true);

    }

    public void showTToolConfiguration() {
        /*JOptionPane.showMessageDialog(frame,
          "TTool version 0.2 - Ludovic Apvrille",
          "About Ttool",
          JOptionPane.INFORMATION_MESSAGE);*/
        JFrameBasicText jft = new JFrameBasicText("Your configuration of TTool ...", ConfigurationTTool.getConfiguration(systemcOn), IconManager.imgic76);
        jft.setIconImage(IconManager.img8);
        jft.setSize(700, 800);
        GraphicLib.centerOnParent(jft);
        jft.setVisible(true);

    }

    public void aboutTURTLE() {
        BrowserControl.startBrowerToURL("http://labsoc.comelec.enst.fr/ttool/");
    }

    public void helpTURTLE() {
        BrowserControl.startBrowerToURL("http://labsoc.comelec.enst.fr/tool/support.html");
    }

    public void helpDIPLODOCUS() {
        BrowserControl.startBrowerToURL("http://labsoc.comelec.enst.fr/tool/diplodocus.html");
    }

    public void oneClickLOTOSRG() {
        boolean ret;
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
        boolean ret;
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

    public boolean checkModelingSyntax(TURTLEPanel tp, boolean automatic) {
        String msg = "";
        boolean b = false;
        boolean ret = false;

        if (file == null) {
            JOptionPane.showMessageDialog(frame,
                                          "The project must be saved before any simulation or formal verification can be performed",
                                          "Syntax analysis failed",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }


        if (tp instanceof AnalysisPanel) {
            try {
                b = gtm.buildTURTLEModelingFromAnalysis((AnalysisPanel)tp);
            } catch (AnalysisSyntaxException ae) {
                //TraceManager.addDev("Exception AnalysisSyntaxException");
                msg = ae.getMessage();
                b = false;
            }
            if (b) {
                setMode(MainGUI.MODEL_OK);
                setMode(MainGUI.GEN_DESIGN_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                                                  "Syntax analysis successful on analysis diagrams",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "The TURTLE Analysis contains several errors",
                                                  "Syntax analysis failed",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
                setMode(MainGUI.GEN_DESIGN_OK);
                //setMode(MainGUI.MODEL_OK);
            }



        } else if (tp instanceof DesignPanel) {
            //Design
            DesignPanel dp = (DesignPanel)tp;
            JDialogModelChecking.validated = dp.validated;
            JDialogModelChecking.ignored = dp.ignored;
            tclassesToValidate = new Vector();
            JDialogModelChecking jdmc = new JDialogModelChecking(frame, tclassesToValidate,dp.tcdp.getComponentList(), "Choosing Tclasses to validate");
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
                if (b) {
                    ret = true;
                    setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_DESIGN_OK);
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                                                      "Syntax analysis successful on design diagrams",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "The TURTLE Modeling contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        } else if (tp instanceof DeploymentPanel) {
            DeploymentPanel dp = (DeploymentPanel) tp;
            b = gtm.translateDeployment(dp);
            if (b) {
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a corresponding formal (RT-LOTOS) specification or executable code (Java)",
                                                  "Syntax analysis successful on deployment diagrams",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "The TURTLE deployment contains several errors",
                                                  "Syntax analysis failed",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // AVATAR
        } else if (tp instanceof AvatarDesignPanel) {
            //Design
            AvatarDesignPanel adp = (AvatarDesignPanel)tp;
            //JDialogModelChecking.validated = adp.validated;
            //JDialogModelChecking.ignored = adp.ignored;
            tclassesToValidate = new Vector();
            JDialogSelectAvatarBlock jdmc = new JDialogSelectAvatarBlock(frame, tclassesToValidate, adp.getAvatarBDPanel().getFullBlockList(), "Choosing blocks to validate", adp.getValidated(), adp.getIgnored(), adp.getOptimized());
            if (!automatic) {
                GraphicLib.centerOnParent(jdmc);
                jdmc.setVisible(true); // blocked until dialog has been closed
            } else {
                jdmc.closeDialog();
            }

            if (jdmc.hasBeenCancelled()) {
                return false;
            }

            adp.setValidated(jdmc.getValidated());
            adp.setIgnored(jdmc.getIgnored());
            adp.setOptimized(jdmc.getOptimized());


            boolean optimize = jdmc.getOptimized();
            if (tclassesToValidate.size() > 0) {
                /*adp.validated = JDialogModelChecking.validated;
                  adp.ignored = JDialogModelChecking.ignored;*/
                b = gtm.checkAvatarDesign(tclassesToValidate, adp, optimize);
                if (b) {
                    ret = true;
                    setMode(MainGUI.AVATAR_SYNTAXCHECKING_OK);
                    //setMode(MainGUI.MODEL_PROVERIF_OK);
                    //setMode(MainGUI.GEN_DESIGN_OK);
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). You can now perform simulations or formal proofs (UPPAAL)",
                                                      "Syntax analysis successful on avatar design diagrams",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "The Avatar modeling contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }


            // NC
        } else if (tp instanceof NCPanel) {
            NCPanel ncp = (NCPanel) tp;
            b = gtm.translateNC(ncp);
            if (b) {
                //setMode(MainGUI.MODEL_OK_NC);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "0 error, " + getCheckingWarnings().size() + " warning(s)",
                                                  "Syntax analysis successful on NC diagram",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "The NC diagram contains several errors",
                                                  "Syntax analysis failed",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            }


        } else if (tp instanceof TMLDesignPanel) {
            TMLDesignPanel tmldp = (TMLDesignPanel)tp;
            JDialogSelectTMLTask.validated = tmldp.validated;
            JDialogSelectTMLTask.ignored = tmldp.ignored;
            Vector tmlTasksToValidate = new Vector();
            JDialogSelectTMLTask jdstmlt = new JDialogSelectTMLTask(frame, tmlTasksToValidate, tmldp.tmltdp.getComponentList(), "Choosing TML tasks to validate");
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
                if (b) {
                    //setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a formal (LOTOS, UPPAAL) specification or executable code (C++ / SystemC)",
                                                      "Syntax analysis successful on TML designs",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "The TML design contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof TMLComponentDesignPanel) {
            TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel)tp;
            JDialogSelectTMLComponent.validated = tmlcdp.validated;
            JDialogSelectTMLComponent.ignored = tmlcdp.ignored;
            Vector tmlComponentsToValidate = new Vector();
            JDialogSelectTMLComponent jdstmlc = new JDialogSelectTMLComponent(frame, tmlComponentsToValidate, tmlcdp.tmlctdp.getComponentList(), "Choosing TML components to validate");
            if (!automatic) {
                GraphicLib.centerOnParent(jdstmlc);
                jdstmlc.setVisible(true); // Blocked until dialog has been closed
            } else {
                jdstmlc.closeDialog();
            }
            if (tmlComponentsToValidate.size() > 0) {
                tmlcdp.validated = JDialogSelectTMLComponent.validated;
                tmlcdp.ignored = JDialogSelectTMLComponent.ignored;
                b = gtm.translateTMLComponentDesign(tmlComponentsToValidate, tmlcdp, jdstmlc.getOptimize());
                if (b) {
                    //setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a formal (LOTOS, UPPAAL) specification or executable code (C++ / SystemC)",
                                                      "Syntax analysis successful on TML designs",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "The TML design contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } else if (tp instanceof TMLArchiPanel) {
            TMLArchiPanel tmlap = (TMLArchiPanel)tp;
            JDialogSelectTMLNodes.validated = tmlap.validated;
            JDialogSelectTMLNodes.ignored = tmlap.ignored;
            Vector tmlNodesToValidate = new Vector();
            JDialogSelectTMLNodes jdstmln = new JDialogSelectTMLNodes(frame, tmlNodesToValidate, tmlap.tmlap.getComponentList(), "Choosing Nodes to validate", tmlap.tmlap.getMasterClockFrequency());
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
                //TraceManager.addDev("Ready to generate TML mapping!");
                b = gtm.checkSyntaxTMLMapping(tmlNodesToValidate, tmlap, jdstmln.getOptimize());
                if (b) {
                    //setMode(MainGUI.MODEL_OK);
                    setMode(MainGUI.GEN_SYSTEMC_OK);
                    setMode(MainGUI.MODEL_OK);
                    ret = true;
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a formal (LOTOS, RT-LOTOS) specification or executable code (systemC)",
                                                      "Syntax analysis successful on TML mapping",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    if (!automatic) {
                        JOptionPane.showMessageDialog(frame,
                                                      "The TML mapping contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
				else if( tp instanceof TMLCommunicationPatternPanel ) {
					TMLCommunicationPatternPanel tmlcpp = (TMLCommunicationPatternPanel) tp;
          JDialogSelectCPDiagrams.validated =  tmlcpp.validated;
          JDialogSelectCPDiagrams.ignored =  tmlcpp.ignored;
          Vector tmlDiagramsToValidate = new Vector();
          JDialogSelectCPDiagrams jdscpd = new JDialogSelectCPDiagrams( frame, tmlDiagramsToValidate, tmlcpp.tmlcpp.getComponentList(),
																														"Choosing Diagrams to validate" );
					if( !automatic ) {
						GraphicLib.centerOnParent( jdscpd );
						jdscpd.setVisible( true ); // Blocked until dialog has been closed
					}
					else {
						jdscpd.closeDialog();
					}
			    if( tmlDiagramsToValidate.size() > 0 ) {
						tmlcpp.validated = JDialogSelectCPDiagrams.validated;
						tmlcpp.ignored = JDialogSelectCPDiagrams.ignored;
						TraceManager.addDev("Ready to generate TML mapping for Communication Patterns!");
						//b = gtm.checkSyntaxTMLCP( tmlDiagramsToValidate, tmlcpp, jdscpd.getOptimize() );
						if( b ) {
							//setMode(MainGUI.MODEL_OK);
							setMode( MainGUI.GEN_SYSTEMC_OK );
							setMode( MainGUI.MODEL_OK );
							ret = true;
							if( !automatic ) {
								JOptionPane.showMessageDialog( frame, "0 error, " + getCheckingWarnings().size() +
								" warning(s). You can now generate a formal (LOTOS, RT-LOTOS) specification or executable code (systemC)",
								"Syntax analysis successful on TML mapping",
								JOptionPane.INFORMATION_MESSAGE );
							}
						}
						else {
							if( !automatic ) {
								JOptionPane.showMessageDialog( frame, "The TML mapping contains several errors", "Syntax analysis failed",
																								JOptionPane.INFORMATION_MESSAGE );
							}
						}
          }
        }
				else if( tp instanceof RequirementPanel ) {
            TDiagramPanel tdp = getCurrentTDiagramPanel();
            if (!(tdp instanceof RequirementDiagramPanel)) {
                if (tdp instanceof EBRDDPanel) {

                    b = gtm.makeEBRDD((EBRDDPanel)tdp);
                    if (b) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s).",
                                                      "Syntax analysis successful on EBRDD",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                                      "The EBRDD contains several errors",
                                                      "Syntax analysis failed",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    return ret;
                }
                //TraceManager.addDev("No syntax checking for EBRDD: not yet implemented");
            } else {
                RequirementDiagramPanel rdp= (RequirementDiagramPanel)tdp;
                JDialogSelectRequirements.validated = rdp.validated;
                JDialogSelectRequirements.ignored = rdp.ignored;
                Vector reqsToValidate = new Vector();
                JDialogSelectRequirements jdsreq = new JDialogSelectRequirements(frame, reqsToValidate, rdp.getComponentList(), "Choosing requirements to verify");
                if (!automatic) {
                    GraphicLib.centerOnParent(jdsreq);
                    jdsreq.setVisible(true); // Blocked until dialog has been closed
                }
                if (reqsToValidate.size() > 0) {
                    rdp.validated = JDialogSelectRequirements.validated;
                    rdp.ignored = JDialogSelectRequirements.ignored;
                    b = gtm.generateTMsForRequirementAnalysis(reqsToValidate, rdp);
                    if (b) {
                        //setMode(MainGUI.GEN_SYSTEMC_OK);
                        setMode(MainGUI.REQ_OK);
                        ret = true;
                        if (!automatic) {
                            JOptionPane.showMessageDialog(frame,
                                                          "0 error, " + getCheckingWarnings().size() + " warning(s). You can now verify requirements' satisfiability",
                                                          "Syntax analysis successful on requirements",
                                                          JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        if (!automatic) {
                            JOptionPane.showMessageDialog(frame,
                                                          "The requirement diagram contains several errors",
                                                          "Syntax analysis failed",
                                                          JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        } else if (tp instanceof ProactiveDesignPanel) {
            // TraceManager.addDev("!!!!!!!!!!!!1");
            //newTurtleModeling();
            b = gtm.translateProactiveDesign((ProactiveDesignPanel)tp);
            if (b) {
                //setMode(MainGUI.MODEL_OK);
                //setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a LOTOS,specification",
                                                  "Syntax analysis successful on Proactive design",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "The Proactive design contains several errors",
                                                  "Syntax analysis failed",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } else if (tp instanceof TURTLEOSDesignPanel) {
            TraceManager.addDev("TURTLEOS Design Panel");
            //TURTLEOSDesignPanel tosdp = (TURTLEOSDesignPanel) tp;
            b = gtm.translateTURTLEOSDesign((TURTLEOSDesignPanel)tp);
            if (b) {
                setMode(MainGUI.MODEL_OK);
                ret = true;
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "0 error, " + getCheckingWarnings().size() + " warning(s). You can now generate a corresponding formal specification or executable code (Java)",
                                                  "Syntax analysis successful on deployment diagrams",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (!automatic) {
                    JOptionPane.showMessageDialog(frame,
                                                  "The TURTLE deployment contains several errors",
                                                  "Syntax analysis failed",
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        //dtree.toBeUpdated();
        dtree.forceUpdate();
        return ret;
    }

    public Vector getAllAttributes() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel)tp;

        String name =  getCurrentTDiagramPanel().getName();

        return adp.getAllAttributes(name);
    }

    public Vector getAllMethods() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel)tp;

        String name =  getCurrentTDiagramPanel().getName();

        return adp.getAllMethods(name);
    }

    public Vector getAllSignals() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel)tp;

        String name =  getCurrentTDiagramPanel().getName();

        return adp.getAllSignals(name);
    }

    public Vector getAllTimers() {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return null;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return null;
        }
        AvatarDesignPanel adp = (AvatarDesignPanel)tp;

        String name =  getCurrentTDiagramPanel().getName();

        return adp.getAllTimers(name);
    }

    public Vector getCheckingErrors() {
        return gtm.getCheckingErrors();
    }

    public Vector getCheckingWarnings() {
        return gtm.getCheckingWarnings();
    }

    public void modelBacktracingProVerif(ProVerifOutputAnalyzer pvoa) {
        TURTLEPanel tp = getCurrentTURTLEPanel();
        if (tp == null) {
            return;
        }

        if (!(tp instanceof AvatarDesignPanel)) {
            return;
        }

        AvatarDesignPanel adp = (AvatarDesignPanel)tp;
        adp.modelBacktracingProVerif(pvoa);
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
            JOptionPane.showMessageDialog(frame,
                                          "RT-LOTOS specification generated",
                                          "RT-LOTOS specification",
                                          JOptionPane.INFORMATION_MESSAGE);
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
            /*if (!automatic && (gtm.getTURTLEModelingState() == 1)) {
              return true;
              }*/
        }

        //TraceManager.addDev("generate LOTOS");
        if (ret == 0) {
            gtm.generateFullLOTOS(lotosfile);
            //TraceManager.addDev("LOTOS generated");
            if (!automatic) {
                JOptionPane.showMessageDialog(frame,
                                              "LOTOS specification generated (" + getCheckingWarnings().size() + " warning(s))",
                                              "LOTOS specification",
                                              JOptionPane.INFORMATION_MESSAGE);
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
            b = gtm.translateTMLMapping(jds.getSample(), jds.getChannel(), jds.getEvent(), jds.getRequest(), jds.getExec(), jds.getBusTransfer(), jds.getScheduling(), jds.getTaskState(), jds.getChannelState(), jds.getBranching(), jds.getTerminateCPU(), jds.getTerminateCPUs(), jds.getClocked(), jds.getTickIntervalValue(), jds.getEndClocked(), jds.getCountTick(), jds.getMaxCountTick(), jds.getMaxCountTickValue(), jds.getRandomTask());
            if (b) {
                setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                return true;
            }
            return false;
        }

        if (!jds.isCancelled()) {
            b = jds.hasError();
            //gtm.translateTMLMapping(jds.getChannel(), jds.getEvent(), jds.getRequest(), jds.getExec(), jds.getBusTransfer(), jds.getScheduling(), jds.getTaskState(), jds.getChannelState(), jds.getBranching(), jds.getTerminateCPU(), jds.getTerminateCPUs(), jds.getClocked(), jds.getTickIntervalValue(), jds.getEndClocked(), jds.getCountTick(), jds.getMaxCountTick(), jds.getMaxCountTickValue(), jds.getRandomTask());
            if (!b) {
                //setMode(MainGUI.MODEL_OK);
                setMode(MainGUI.GEN_SYSTEMC_OK);
                setMode(MainGUI.MODEL_OK);
                return true;
                /*if (!automatic) {
                  JOptionPane.showMessageDialog(frame,
                  "0 error, " + getCheckingWarnings().size() + " warning(s). Formal specification can be generated",
                  "Successful translation from the TML mapping to a formal specification",
                  JOptionPane.INFORMATION_MESSAGE);
                  }*/
            } else {
                /*if (!automatic) {
                  JOptionPane.showMessageDialog(frame,
                  "Formal specification generation failed: the TML mapping contains several errors",
                  "Syntax analysis failed",
                  JOptionPane.INFORMATION_MESSAGE);
                  }*/
                return false;
            }
        }

        return false;
    }

    public boolean generateTIFFromTMLModeling(boolean automatic, int generator) {
        return gtm.translateTMLModeling();
    }

    public void generateAUT() {
        JDialogGenAUT jdgaut = new JDialogGenAUT(frame, this, "Generation of automata", ConfigurationTTool.BcgioPath, ConfigurationTTool.AldebaranHost, ConfigurationTTool.TGraphPath);
        jdgaut.setSize(450, 600);
        GraphicLib.centerOnParent(jdgaut);
        jdgaut.setVisible(true);

        //Update menu
        Vector v = jdgaut.getFiles();
        JMenu menu = jmenubarturtle.getJMenuGraph();
        menu.removeAll();
        String s;
        for(int i=0; i<v.size(); i++) {
            s = (String)(v.elementAt(i));
            jmenubarturtle.addMenuItem(menu, s, this);
        }

    }

    public void generateAUTS() {
        JDialogGenAUTS jdgauts = new JDialogGenAUTS(frame, this, "Generation of automata via LOTOS", gtm.getPathCaesar(),
                                                    gtm.getPathBcgio(),
                                                    REMOTE_RTL_LOTOS_FILE,
                                                    gtm.getCaesarHost(), ConfigurationTTool.TGraphPath);
        jdgauts.setSize(450, 600);
        GraphicLib.centerOnParent(jdgauts);
        jdgauts.setVisible(true);

        //Update menu
        /*Vector v = jdgauts.getFiles();
          JMenu menu = jmenubarturtle.getJMenuGraph();
          menu.removeAll();
          String s;
          for(int i=0; i<v.size(); i++) {
          s = (String)(v.elementAt(i));
          jmenubarturtle.addMenuItem(menu, s, this);
          }*/

    }

    public void avatarSimulation() {
        TraceManager.addDev("Avatar simulation");
        jfais = new JFrameAvatarInteractiveSimulation(frame, this, "Interactive simulation", gtm.getAvatarSpecification());
        jfais.setIconImage(IconManager.img9);
        jfais.setSize(800, 600);
        GraphicLib.centerOnParent(jfais);
        jfais.setVisible(true);
    }

    public void avatarUPPAALVerification() {
        TraceManager.addDev("Avatar uppaal fv");
        boolean result = gtm.generateUPPAALFromAVATAR(ConfigurationTTool.UPPAALCodeDirectory);
        if (result) {
            formalValidation(true);
        } else {
            JOptionPane.showMessageDialog(frame,
                                          "" + getCheckingErrors().size() + " errors, " +getCheckingWarnings().size() + " warning(s). UPPAAL specification could NOT be generated",
                                          "Translation to UPPAAL failed",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    public void avatarProVerifVerification() {
        TraceManager.addDev("Avatar proverif fv");
        JDialogProVerifGeneration jgen = new JDialogProVerifGeneration(frame, this, "ProVerif: code generation and verification", ConfigurationTTool.ProVerifVerifierHost, ConfigurationTTool.ProVerifCodeDirectory, ConfigurationTTool.ProVerifVerifierPath);
        jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void avatarStaticAnalysis() {
        TraceManager.addDev("Avatar static analysis invariants");
        JDialogInvariantAnalysis jgen = new JDialogInvariantAnalysis(frame, this, "Static analysis: invariants computation");
        jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void avatarExecutableCodeGeneration() {
        TraceManager.addDev("Avatar code generation");
        JDialogAvatarExecutableCodeGeneration jgen = new JDialogAvatarExecutableCodeGeneration(frame, this, "Executable Code generation, compilation and execution", ConfigurationTTool.AVATARExecutableCodeHost, ConfigurationTTool.AVATARExecutableCodeDirectory,  ConfigurationTTool.AVATARExecutableCodeCompileCommand, ConfigurationTTool.AVATARExecutableCodeExecuteCommand, ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand, ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand,  ConfigurationTTool.AVATARExecutableSoclibTraceFile);
        jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void generateUPPAAL() {
        generateUPPAAL(true);
    }

    public void generateUPPAAL(boolean showWindow) {
        //TraceManager.addDev("Generate UPPAAL!");
        //gtm.mergeChoices(true);
        if (gtm.getTURTLEModelingState() > 0) {
            if (gtm.getTURTLEModelingState() == 3) {
                //AVATAR
                boolean result = gtm.generateUPPAALFromAVATAR(ConfigurationTTool.UPPAALCodeDirectory);
                if (showWindow) {
                    if (result) {
                        JOptionPane.showMessageDialog(frame,
                                                      "0 error, " + getCheckingWarnings().size() + " warning(s). UPPAAL specification generated",
                                                      "Successful translation to UPPAAL",
                                                      JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                                      "" + getCheckingErrors().size() + " errors, " +getCheckingWarnings().size() + " warning(s). UPPAAL specification could NOT be generated",
                                                      "Translation to UPPAAL failed",
                                                      JOptionPane.INFORMATION_MESSAGE);


                    }
                }
                if (!result) {
                    return;
                }
            } else {
                if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false, UPPAAL) == -1) {
                    return;
                }
                if (showWindow) {
                    JDialogUPPAALGeneration jgen = new JDialogUPPAALGeneration(frame, this, "UPPAAL code generation", ConfigurationTTool.UPPAALCodeDirectory, JDialogUPPAALGeneration.DIPLODOCUS_MODE);
                    jgen.setSize(450, 500);
                    GraphicLib.centerOnParent(jgen);
                    jgen.setVisible(true);

                }
                return;
            }
        }

        //TraceManager.addDev("After UPPAAL");
        if (showWindow) {
            JDialogUPPAALGeneration jgen = new JDialogUPPAALGeneration(frame, this, "UPPAAL code generation", ConfigurationTTool.UPPAALCodeDirectory, JDialogUPPAALGeneration.TURTLE_MODE);
            jgen.setSize(450, 600);
            GraphicLib.centerOnParent(jgen);
            jgen.setVisible(true);
            //dtree.toBeUpdated();
        }
    }

    public void generateProVerif() {
        TraceManager.addDev("Generate ProVerif!");

        JDialogProVerifGeneration jgen = new JDialogProVerifGeneration(frame, this, "ProVerif: code generation and verification", ConfigurationTTool.ProVerifVerifierHost, ConfigurationTTool.ProVerifCodeDirectory, ConfigurationTTool.ProVerifVerifierPath);
        jgen.setSize(500, 450);
        GraphicLib.centerOnParent(jgen);
        jgen.setVisible(true);
        dtree.toBeUpdated();

        // Generate from AVATAR
        /*if (gtm.getTURTLEModelingState() == 3) {
          boolean result = gtm.generateProVerifFromAVATAR(ConfigurationTTool.ProVerifCodeDirectory);
          if (result) {
          JOptionPane.showMessageDialog(frame,
          "0 error, " + getCheckingWarnings().size() + " warning(s). ProVerif specification generated",
          "Successful translation from the Avatar to a ProVerif specification",
          JOptionPane.INFORMATION_MESSAGE);
          } else {
          JOptionPane.showMessageDialog(frame,
          "" + getCheckingErrors().size() + " errors, " +getCheckingWarnings().size() + " warning(s). ProVerif specification could NOT be generated",
          "ERROR during translation from AVATAR to UPPAAL",
          JOptionPane.INFORMATION_MESSAGE);
          }
          }*/
    }

    public LinkedList generateAllAUT(String path) {
        return gtm.generateAUT(path);
    }

    public LinkedList generateAllLOTOS(String path) {
        return gtm.generateLOTOSAUT(path);
    }

    public void generateJava() {

        if (gtm.getTURTLEModelingState() == 1) {
            if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false, JAVA) == -1) {
                return;
            }
        }

        JDialogJavaGeneration jgen = new JDialogJavaGeneration(frame, this, "Java code generation and compilation", ConfigurationTTool.JavaCodeDirectory, ConfigurationTTool.JavaCompilerPath, ConfigurationTTool.TToolClassPath, ConfigurationTTool.JavaExecutePath, ConfigurationTTool.JavaHeader);
        jgen.setSize(450, 600);
        GraphicLib.centerOnParent(jgen);
        jgen.setVisible(true);
        dtree.toBeUpdated();
    }

    public void simuJava() {
        /*TraceManager.addDev("Generate Java");
          gtm.generateJava("");
          JOptionPane.showMessageDialog(frame,
          "Java code generated",
          "Java generator",
          JOptionPane.INFORMATION_MESSAGE);
          dtree.toBeUpdated();*/

        JDialogJavaSimulation jgen = new JDialogJavaSimulation(frame, this, "Java simulation", ConfigurationTTool.SimuJavaCodeDirectory, ConfigurationTTool.JavaCompilerPath, ConfigurationTTool.TToolSimuClassPath, ConfigurationTTool.JavaExecutePath);
        jgen.setSize(450, 600);
        GraphicLib.centerOnParent(jgen);
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
        } else if ((tp instanceof TMLDesignPanel) || (tp instanceof TMLComponentDesignPanel) || (tp instanceof TMLArchiPanel))  {
            JDialogSystemCGeneration jgen = new JDialogSystemCGeneration(frame, this, "Simulation code generation and compilation",
                                                                         ConfigurationTTool.SystemCHost, ConfigurationTTool.SystemCCodeDirectory, ConfigurationTTool.SystemCCodeCompileCommand,
                                                                         ConfigurationTTool.SystemCCodeExecuteCommand, ConfigurationTTool.SystemCCodeInteractiveExecuteCommand, ConfigurationTTool.GGraphPath, _mode);
            jgen.setSize(500, 750);
            GraphicLib.centerOnParent(jgen);
            jgen.setVisible(true);
            dtree.toBeUpdated();

            /*if (jgen.isInteractiveSimulationSelected() && (mode == 0)) {
              interactiveSimulationSystemC(jgen.getPathInteractiveExecute());
              }*/
        }
    }

    public void interactiveSimulationSystemC() {
        interactiveSimulationSystemC(ConfigurationTTool.SystemCCodeInteractiveExecuteCommand);
    }

    public void interactiveSimulationSystemC(String executePath) {
        //TraceManager.addDev("toto0");
        ArrayList<Point> points = getListOfBreakPoints();
        if (gtm == null) {
            jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, null, points);
        } else {
            //TraceManager.addDev("toto1");
            if (gtm.getTMLMapping() != null) {
                jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, gtm.getTMLMapping(), points);
            } else {
                //TraceManager.addDev("toto2");
                if (gtm.getArtificialTMLMapping() != null) {
                    jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, gtm.getArtificialTMLMapping(), points);
                } else {
                    //TraceManager.addDev("toto3");
                    jfis = new JFrameInteractiveSimulation(frame, this, "Interactive simulation", ConfigurationTTool.SystemCHost, executePath, null, points);
                }
            }
        }
        jfis.setIconImage(IconManager.img9);
        jfis.setSize(1024, 900);
        GraphicLib.centerOnParent(jfis);
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
            for(int i=0; i<tabs.size(); i++) {
                tp = (TURTLEPanel)(tabs.elementAt(i));
                if (tp instanceof TMLDesignPanel) {
                    ((TMLDesignPanel)tp).getListOfBreakPoints(points);
                }
                if (tp instanceof TMLComponentDesignPanel) {
                    ((TMLComponentDesignPanel)tp).getListOfBreakPoints(points);
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
        //TraceManager.addDev("Generating TML code: "+file.getAbsolutePath());
        if (gtm.generateTMLTxt(path)) {
            return ConfigurationTTool.TMLCodeDirectory;
        }

        return null;
        //TraceManager.addDev("Done");
    }

    public void generateDesign() {
        if (gtm.getTURTLEModelingState() == 1) {
            if (generateTURTLEModelingFromState(gtm.getTURTLEModelingState(), false, DESIGN) == -1) {
                return;
            }
        }

        //TraceManager.addDev("Generate design");
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
            JDialogTextProcess jdtp = new JDialogTextProcess(frame,
                                                             "Checking RT-LOTOS specification with RTL",
                                                             gtm.getPathRTL() + " __FILENAME -max-spec-t-1",
                                                             REMOTE_RTL_LOTOS_FILE,
                                                             gtm.getLastRTLOTOSSpecification(),
                                                             gtm.getHost());
            jdtp.setSize(450, 600);
            GraphicLib.centerOnParent(jdtp);
            jdtp.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {
            JDialogLOTOSAnalysis jdla = new JDialogLOTOSAnalysis(frame,
                                                                 this, "Checking LOTOS specification with CAESAR",
                                                                 gtm.getPathCaesar(),
                                                                 REMOTE_RTL_LOTOS_FILE,
                                                                 gtm.getLastRTLOTOSSpecification(),
                                                                 gtm.getCaesarHost());
            jdla.setSize(450, 600);
            GraphicLib.centerOnParent(jdla);
            jdla.setVisible(true);
            dtree.toBeUpdated();
        }
    }


    public void simulation() {
        if (gtm.getLanguageID() == GTURTLEModeling.RT_LOTOS) {
            JDialogSimulation jds = new JDialogSimulation(frame,
                                                          this,
                                                          "Intensive simulation with RTL",
                                                          gtm.getPathRTL(),
                                                          REMOTE_RTL_LOTOS_FILE,
                                                          gtm.getLastRTLOTOSSpecification(),
                                                          gtm.getHost());
            jds.setSize(450, 600);
            GraphicLib.centerOnParent(jds);
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
            JDialogFormalValidation jdfv = new JDialogFormalValidation(frame,
                                                                       this,
                                                                       "Formal Validation with RTL",
                                                                       gtm.getPathRTL(),
                                                                       gtm.getPathDTA2DOT(),
                                                                       gtm.getPathRGSTRAP(),
                                                                       gtm.getPathRG2TLSA(),
                                                                       REMOTE_RTL_LOTOS_FILE,
                                                                       gtm.getLastRTLOTOSSpecification(),
                                                                       gtm.getHost(),
                                                                       gtm.getHostAldebaran(),
                                                                       gtm.getPathBcgio());
            jdfv.setAutomatic(automatic);
            jdfv.setSize(450, 600);
            GraphicLib.centerOnParent(jdfv);
            jdfv.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.LOTOS) {
            JDialogLOTOSValidation jdla = new JDialogLOTOSValidation(frame,
                                                                     this, "Generating RG with CAESAR",
                                                                     gtm.getPathCaesar(),
                                                                     gtm.getPathCaesarOpen(),
                                                                     gtm.getPathBcgio(),
                                                                     gtm.getPathBcgmerge(),
                                                                     REMOTE_RTL_LOTOS_FILE,
                                                                     gtm.getLastRTLOTOSSpecification(),
                                                                     gtm.getCaesarHost());
            jdla.setAutomatic(automatic);
            jdla.setSize(450, 600);
            GraphicLib.centerOnParent(jdla);
            jdla.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.UPPAAL) {
            JDialogUPPAALValidation jduv = new JDialogUPPAALValidation(frame,
                                                                       this, "Formal verification with UPPAAL",
                                                                       gtm.getPathUPPAALVerifier(),
                                                                       gtm.getPathUPPAALFile(),
                                                                       REMOTE_UPPAAL_FILE,
                                                                       gtm.getLastUPPAALSpecification().getStringSpec(),
                                                                       gtm.getUPPAALVerifierHost(),
                                                                       _tp);
            jduv.setSize(450, 600);
            GraphicLib.centerOnParent(jduv);
            jduv.setVisible(true);
            dtree.toBeUpdated();
        } else if (gtm.getLanguageID() == GTURTLEModeling.MATRIX) {
            JDialogTMatrixManagement jdfv = new JDialogTMatrixManagement(frame,
                                                                         this,
                                                                         "Observers's Based Formal Verification",
                                                                         gtm.getRequirementModeling(),
                                                                         gtm.getPathRTL(),
                                                                         gtm.getPathDTA2DOT(),
                                                                         gtm.getPathRGSTRAP(),
                                                                         gtm.getPathRG2TLSA(),
                                                                         REMOTE_RTL_LOTOS_FILE,
                                                                         gtm.getHost(),
                                                                         GTURTLEModeling.getHostAldebaran(),
                                                                         gtm.getPathBcgio());
            jdfv.setSize(550, 600);
            jdfv.setIconImage(IconManager.img8);
            GraphicLib.centerOnParent(jdfv);
            jdfv.setVisible(true);
            dtree.toBeUpdated();
        }
    }

    public void projection() {
        TClassDiagramPanel tcdp = null;
        if (getCurrentTURTLEPanel() instanceof DesignPanel) {
            tcdp = ((DesignPanel)(getCurrentTURTLEPanel())).tcdp;
        }
        JDialogProjection jdfv = new JDialogProjection(frame,
                                                       this,
                                                       tcdp,
                                                       gtm.getTURTLEModeling(),
                                                       GTURTLEModeling.getHostAldebaran(),
                                                       GTURTLEModeling.getPathAldebaran(),
                                                       gtm.getPathBcgmin(),
                                                       gtm.getPathBcgio(),
                                                       gtm.getLastRGAUT(),
                                                       REMOTE_ALDEBARAN_AUT_FILE,
                                                       "Minimization using Aldebaran");
        jdfv.setSize(900, 700);
        GraphicLib.centerOnParent(jdfv);
        jdfv.setVisible(true);
    }

    public void modifyGraph() {
        JDialogGraphModification jdgm;
        if (gtm == null ){
            jdgm = new JDialogGraphModification(frame,
                                                GTURTLEModeling.getHostAldebaran(),
                                                GTURTLEModeling.getPathBcgio(),
                                                "graph",
                                                "Minimization using Aldebaran",
                                                null, null);
        } else {
            jdgm = new JDialogGraphModification(frame,
                                                GTURTLEModeling.getHostAldebaran(),
                                                gtm.getPathBcgio(),
                                                "graph",
                                                "Minimization using Aldebaran",
                                                gtm.getLastRGAUT(),
                                                gtm.getLastTextualRGAUTProj());
        }
        jdgm.setSize(600, 500);
        GraphicLib.centerOnParent(jdgm);
        jdgm.setVisible(true);
        modifiedaut = jdgm.getGraphAUT();
        modifiedautdot = jdgm.getGraphDOT();
        if (modifiedautdot != null) {
            actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUTMODIFIED].setEnabled(true);
        }
        //gtm.modifyMinimizedGraph();
    }

    public void bisimulation() {
        JDialogBisimulation jdb = new JDialogBisimulation(frame,
                                                          GTURTLEModeling.getHostAldebaran(),
                                                          GTURTLEModeling.getPathAldebaran(),
                                                          REMOTE_ALDEBARAN_BISIMU_FILE1,
                                                          REMOTE_ALDEBARAN_BISIMU_FILE2,
                                                          "Bisimulation using Aldebaran");
        jdb.setSize(650, 800);
        GraphicLib.centerOnParent(jdb);
        jdb.setVisible(true);
        //TraceManager.addDev("Bisimulation");
    }

    public void bisimulationCADP() {
        JDialogBisimulationBisimulator jdb = new JDialogBisimulationBisimulator(frame,
                                                                                GTURTLEModeling.getCaesarHost(),
                                                                                GTURTLEModeling.getPathBisimulator(),
                                                                                GTURTLEModeling.getPathBcgio(),
                                                                                REMOTE_BISIMULATOR_FILE1,
                                                                                REMOTE_BISIMULATOR_FILE2,
                                                                                "Bisimulation using BISIMULATOR");
        jdb.setSize(650, 800);
        GraphicLib.centerOnParent(jdb);
        jdb.setVisible(true);
        //TraceManager.addDev("Bisimulation");
    }

    public void seekDeadlockAUT() {
        String dataAUT = gtm.getLastTextualRGAUT();

        JFrameDeadlock jfd = new JFrameDeadlock("Potential deadlocks", dataAUT);
        jfd.setIconImage(IconManager.img8);
        jfd.setSize(600, 600);
        GraphicLib.centerOnParent(jfd);
        jfd.setVisible(true);
    }

    public void seekDeadlockSavedAUT() {
        String graph[] = loadAUTGraph();
        if (graph != null) {
            JFrameDeadlock jfd = new JFrameDeadlock("Potential deadlocks on " + graph[0], graph[1]);
            jfd.setIconImage(IconManager.img8);
            jfd.setSize(600, 600);
            GraphicLib.centerOnParent(jfd);
            jfd.setVisible(true);
        }
    }

    public void showAUT(String title, String data) {
        /*JFrameStatistics jfs = new JFrameStatistics(title, data);
          jfs.setIconImage(IconManager.img8);
          jfs.setSize(600, 600);
          GraphicLib.centerOnParent(jfs);
          jfs.setVisible(true);*/
        ThreadGUIElement t = new ThreadGUIElement(frame, 0, title, data, "Analyzing graph... Please wait");
        t.go();
    }

    public void showPMAUT(String title, String data) {
        TraceManager.addDev("Power management analysis");
        JFramePowerManagementAnalysis jfpma = new JFramePowerManagementAnalysis(title, data);
        jfpma.setIconImage(IconManager.img8);
        jfpma.setSize(600, 600);
        GraphicLib.centerOnParent(jfpma);
        jfpma.setVisible(true);
    }

    public void NC() {
        TraceManager.addDev("NC");
        JFrameNC jfnc = new JFrameNC("Network calculus", gtm.getNCS());
        jfnc.setIconImage(IconManager.img8);
        jfnc.setSize(600, 600);
        GraphicLib.centerOnParent(jfnc);
        jfnc.setVisible(true);
        TraceManager.addDev("Done");

        /*JFrameStatistics jfs = new JFrameStatistics(title, data);
          jfs.setIconImage(IconManager.img8);
          jfs.setSize(600, 600);
          GraphicLib.centerOnParent(jfs);
          jfs.setVisible(true);*/
    }

    public void statAUT() {
        showAUT("Analysis on the last RG (AUT format)", gtm.getLastTextualRGAUT());
    }

    public void statAUTDiplodocus() {
        String spec = loadFile(new File(ConfigurationTTool.TGraphPath + "/tree.aut"));
        if (spec == null) {
            JOptionPane.showMessageDialog(frame,
                                          "The fill could not be loaded:",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showAUT("Analysis on the last DIPLODOCUS RG", spec);
    }

    public void statAUTProj() {
        showAUT("Analysis on the last minimized RG (AUT format)", gtm.getLastTextualRGAUTProj());
    }

    public void statSavedAUT() {
        //TraceManager.addDev("toto");
        String graph[] = loadAUTGraph();
        if (graph != null) {
            showAUT("Analysis on " + graph[0], graph[1]);
        }
    }

    public void pmAUT() {
        showPMAUT("Power Management Analysis on the last RG (AUT format)", gtm.getLastTextualRGAUT());
    }

    public void pmAUTProj() {
        showPMAUT("Power Management Analysis on the last minimized RG (AUT format)", gtm.getLastTextualRGAUTProj());
    }

    public void pmSavedAUT() {
        //TraceManager.addDev("toto");
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
        jft.setSize(600, 600);
        GraphicLib.centerOnParent(jft);
        jft.setVisible(true);
    }

    public void showJavaCode() {
        if (javaframe == null) {
            javaframe = new JFrameCode("JavaCode", "", "");
            javaframe.setIconImage(IconManager.img8);
            javaframe.setSize(350, 350);
            GraphicLib.centerOnParent(javaframe);
            javaframe.setVisible(true);
        } else {
            javaframe.setVisible(true);
        }
    }

    public void showBirdEyesView() {
        if (birdframe == null) {
            birdframe = new JFrameBird(this);
            birdframe.setIconImage(IconManager.img8);
            birdframe.setSize(150, 100);
            GraphicLib.centerOnParent(birdframe);
            birdframe.setVisible(true);
        } else {
            birdframe.setVisible(true);
        }
    }

    public void showEmbeddedBirdEyesView() {
        //TraceManager.addDev("Embedded!");
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
        jft.setSize(600, 600);
        GraphicLib.centerOnParent(jft);
        jft.setVisible(true);
    }

    public void showSavedRTLOTOS() {
        String spec[] = loadLotosSpec();
        if ((spec != null) && (spec[0] != null) && (spec[1] != null)) {
            JFrameText jft = new JFrameText("RT-LOTOS Specification: " + spec[0], spec[1]);
            jft.setIconImage(IconManager.img8);
            jft.setSize(600, 600);
            GraphicLib.centerOnParent(jft);
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
            JOptionPane.showMessageDialog(frame,
                                          "The DTA could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showWave() {
        RemoteExecutionThread ret = new RemoteExecutionThread(ConfigurationTTool.SystemCHost, null, null, ConfigurationTTool.GTKWavePath  + " vcddump.vcd");
        ret.start();
    }

    public void showRG() {
        String s = gtm.showRG();
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The RG could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showTLSA() {
        String s = gtm.showTLSA();
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The TLSA could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGAut() {
        String s = gtm.showRGAut();
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The RG could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGAutProj() {
        String s = gtm.showRGAutProj();
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The RG could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showRGDiplodocus() {
        String s = gtm.showRGDiplodocus();
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The RG could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showModifiedAUTDOT() {
        String s = GTURTLEModeling.runDOTTY(modifiedautdot);
        if (s != null) {
            JOptionPane.showMessageDialog(frame,
                                          "The RG could not be displayed: " + s,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showGGraph() {
        String graph[] = loadGGraph();
        if (graph != null) {
            String s = GTURTLEModeling.showGGraph(graph[1]);
            if (s != null) {
                JOptionPane.showMessageDialog(frame,
                                              "The graph could not be displayed: " + s,
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void viewAutomata(String file) {
        //TraceManager.addDev("viewing: " + file);
        String graph[] = loadGGraph(file);
        if (graph != null) {
            String s = GTURTLEModeling.showGGraph(graph[1]);
            if (s != null) {
                JOptionPane.showMessageDialog(frame,
                                              "The graph could not be displayed: " + s,
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void executeUserCommand(String host, String command) {
        RemoteExecutionThread ret = new RemoteExecutionThread(host, null, null, command);
        ret.start();
        TraceManager.addDev("User command ->" + command + "<- started on host " + host);
    }


    public void screenCapture() {
        //Select file
        File file = selectFileForCapture();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        performScreenCapture(screenRect, file);
    }

    public void windowCapture() {
        //Select file
        File file = selectFileForCapture();
        if (file == null)
            return;

        Rectangle screenRect = new Rectangle(frame.getLocation().x, frame.getLocation().y, frame.getWidth(), frame.getHeight());
        performScreenCapture(screenRect, file);
    }

    public void diagramCapture() {
        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame,
                                          "No diagram is under edition",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(frame,
                                          "No diagram is under edition",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectSVGFileForCapture();

        if (file == null)
            return;

        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            String s = tdp1.svgCapture();
            try {
                FileUtils.saveFile(file.getAbsolutePath(), s);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame,
                                              "File could not be saved: " + e.getMessage(),
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(frame,
                                          "The capture was correctly performed and saved in " + file.getAbsolutePath(),
                                          "Save in svg format ok",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void allDiagramCapture() {
        if (tabs.size() < 1) {
            JOptionPane.showMessageDialog(frame,
                                          "No diagram is under edition",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File file = selectFileForCapture();
        if (file == null)
            return;

        TURTLEPanel tp = getCurrentTURTLEPanel();
        TDiagramPanel tdp1;
        BufferedImage image;
        File file1;
        String name = file.getAbsolutePath();
        name = name.substring(0, name.length() - 4);

        //boolean actions;
        for(int i=0; i<tp.panels.size(); i++) {
            tdp1 = (TDiagramPanel)(tp.panels.elementAt(i));
            tdp1.repaint();
            image = tdp1.performMinimalCapture();
            if (i < 10) {
                file1 = new File(name + "0" + i);
            } else {
                file1 = new File(name + i);
            }
            file1 = FileUtils.addFileExtensionIfMissing(file1, TImgFilter.getExtension());
            if (!writeImageCapture(image, file1, false)) {
                return;
            }
            if (i ==0) {
                if (!writeImageCapture(image, file, false)) {
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(frame,
                                      "All diagrams were sucessfully captured",
                                      "Capture ok",
                                      JOptionPane.INFORMATION_MESSAGE);
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
        int returnVal = jfcimg.showSaveDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcimg.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TImgFilter.getExtension());

        }
        if(!checkFileForSave(file)) {
            JOptionPane.showMessageDialog(frame,
                                          "The capture could not be performed: invalid file",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return file;
    }

    public File selectSVGFileForCapture() {
        File file = null;
        int returnVal = jfcimgsvg.showSaveDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = jfcimgsvg.getSelectedFile();
            file = FileUtils.addFileExtensionIfMissing(file, TSVGFilter.getExtension());

        }
        if(!checkFileForSave(file)) {
            JOptionPane.showMessageDialog(frame,
                                          "The capture could not be performed: invalid file",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return null;
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
            JOptionPane.showMessageDialog(frame,
                                          "The capture could not be performed:" + e.getMessage(),
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(frame,
                                      "The capture was correctly performed",
                                      "Screen capture ok",
                                      JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    public boolean writeImageCapture(BufferedImage image, File file, boolean info) {
        frame.paint(frame.getGraphics());
        try {
            // save captured image to PNG file
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                                          "The capture could not be performed:" + e.getMessage(),
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if (info) {
            JOptionPane.showMessageDialog(frame,
                                          "The capture was correctly performed and saved in " + file.getAbsolutePath(),
                                          "Screen capture ok",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    public void generateDocumentation() {
        //TraceManager.addDev("Documentation");
        ThreadGUIElement t = new ThreadGUIElement(frame, 1, tabs, mainTabbedPane, ConfigurationTTool.IMGPath, file.getName(),"Documentation", "Generating documentation ... Please wait");
        t.go();
        /*DocumentationGenerator docgen = new DocumentationGenerator(tabs, mainTabbedPane, ConfigurationTTool.IMGPath, file.getName());
          docgen.setFirstHeadingNumber(2);
          if (docgen.generateDocumentation()) {
          JOptionPane.showMessageDialog(frame,
          "All done!",
          "Documentation generation",
          JOptionPane.INFORMATION_MESSAGE);
          } else {
          JOptionPane.showMessageDialog(frame,
          "The documentation generation could not be performed",
          "Error",
          JOptionPane.INFORMATION_MESSAGE);
          }*/
        //TraceManager.addDev("Documentation=" + docgen.getDocumentation());
    }

    public void generateDocumentationReq() {
        TraceManager.addDev("Frame Req");
        JDialogRequirementTable jdrt = new JDialogRequirementTable(frame, "Selecting table columns");
        GraphicLib.centerOnParent(jdrt);
        jdrt.setVisible(true);
        if (!jdrt.hasBeenCancelled()) {
            Point [] pts = jdrt.getColumnsInfo();
            if (pts != null) {
                for(int i=0; i<pts.length; i++) {
                    TraceManager.addDev("" + i + ": (" + pts[i].x + ", " + pts[i].y + ")");
                }

                JFrameRequirementTable jfrt = new JFrameRequirementTable("Requirement table", tabs, mainTabbedPane, pts);
                jfrt.setIconImage(IconManager.img8);
                jfrt.setSize(1024, 768);
                GraphicLib.centerOnParent(jfrt);
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

    public void actionOnButton(int type, int id) {
        typeButtonSelected = type;
        idButtonSelected = id;
        //TDiagramPanel tdp1 = ((TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()))).tdp;
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        //TraceManager.addDev("Selected TDiagramPanel=" + tdp1.getName());
        tdp1.repaint();
    }

    public int getIdButtonSelected() {
        return idButtonSelected;
    }

    public void addAvatarBlock(TURTLEPanel tp, String s)        {
        //TraceManager.addDev("ADD TML Task=" + s);
        if (!(tp instanceof AvatarDesignPanel)) {
            return;
        }

        ((AvatarDesignPanel)tp).addAvatarStateMachineDiagramPanel(s);
        setPanelMode();
    }

    public void addTClass(TURTLEPanel tp, String s)     {
        if (!(tp instanceof DesignPanel)) {
            return;
        }

        ((DesignPanel)tp).addTActivityDiagram(s);
        setPanelMode();
    }

    public void addTOSClass(TURTLEPanel tp, String s)   {
        if (!(tp instanceof TURTLEOSDesignPanel)) {
            return;
        }

        ((TURTLEOSDesignPanel)tp).addTURTLEOSActivityDiagram(s);
        setPanelMode();
    }

    public void addTMLTask(TURTLEPanel tp, String s)    {
        //TraceManager.addDev("ADD TML Task=" + s);
        if (!(tp instanceof TMLDesignPanel)) {
            return;
        }

        ((TMLDesignPanel)tp).addTMLActivityDiagram(s);
        setPanelMode();
    }

    public void addTMLCPrimitiveComponent(TURTLEPanel tp, String s)     {
        //TraceManager.addDev("ADD C Primitive Component=" + s);
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return;
        }

        ((TMLComponentDesignPanel)tp).addTMLActivityDiagram(s);
        setPanelMode();
    }

    public TMLActivityDiagramPanel getReferencedTMLActivityDiagramPanel(TDiagramPanel _tdp, String name) {
        TURTLEPanel tp;
        TMLActivityDiagramPanel tmladp;
        //TraceManager.addDev("global search for: " + name);
        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                if (tp.hasTDiagramPanel(_tdp)) {
                    tmladp = ((TMLComponentDesignPanel)tp).getTMLActivityDiagramPanel(name);
                    if (tmladp != null) {
                        //TraceManager.addDev("Found");
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
        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
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
        //TraceManager.addDev("global search for: " + name);
        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof RequirementPanel) {
                ((RequirementPanel)tp).addAllEBRDDPanels(al);
            }
        }

        return al;
    }

    public ArrayList<AvatarPDPanel> getAllAvatarPDPanels() {
        TURTLEPanel tp;
        ArrayList<AvatarPDPanel> al = new ArrayList<AvatarPDPanel>();
        //TraceManager.addDev("global search for: " + name);
        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof AvatarRequirementPanel) {
                ((AvatarRequirementPanel)tp).addAllAvatarPDPanels(al);
            }
        }

        return al;
    }

    public LinkedList getAllTMLComponents() {
        TURTLEPanel tp;
        LinkedList ll = new LinkedList();
        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                ll.addAll(((TMLComponentDesignPanel)tp).tmlctdp.getComponentList());
            }
        }
        return ll;
    }

    public void removeTClass(TURTLEPanel tp, String s)  {
        if (!(tp instanceof DesignPanel)) {
            return;
        }

        for(int i = 0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeTOSClass(TURTLEPanel tp, String s)        {
        if (!(tp instanceof TURTLEOSDesignPanel)) {
            return;
        }

        TraceManager.addDev("Removing tab ...");
        for(int i = 0; i<tp.tabbedPane.getTabCount(); i++) {
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

        for(int i = 0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeAvatarBlock(TURTLEPanel tp, String s)     {
        if (!(tp instanceof AvatarDesignPanel)) {
            return;
        }

        for(int i = 0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                tp.tabbedPane.removeTabAt(i);
                tp.panels.removeElementAt(i);
                setPanelMode();
                return;
            }
        }
    }

    public void removeTMLCPrimitiveComponent(TURTLEPanel tp, String s)  {
        //TraceManager.addDev("Removing panel 0:" + s);
        if (!(tp instanceof TMLComponentDesignPanel)) {
            return;
        }

        //TraceManager.addDev("Removing panel 1:" + s);
        for(int i = 0; i<tp.tabbedPane.getTabCount(); i++) {
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
        if (index<0) {
            return "";
        }
        return mainTabbedPane.getTitleAt(index);
    }

    public int getMajorIndexOf(TDiagramPanel tdp) {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size();i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
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

        for(int i=0; i<tabs.size();i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel)tp).tmlctdp.getPanelsUsingAComponent(tmlcc, foundPanels);
            }
        }

        return foundPanels;
    }

    public void updateReferenceToTMLCCompositeComponent(TMLCCompositeComponent tmlcc) {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size();i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLComponentDesignPanel) {
                ((TMLComponentDesignPanel)tp).tmlctdp.updateReferenceToTMLCCompositeComponent(tmlcc);
            }
        }
    }

    public TMLCCompositeComponent getCompositeComponent(String name) {
        int index = name.indexOf("::");
        if (index == -1) {
            return null;
        }

        String panelName = name.substring(0, index);
        String componentName = name.substring(index+2, name.length());

        TURTLEPanel tp = getTURTLEPanel(panelName);

        if ((tp == null) || (!(tp instanceof TMLComponentDesignPanel))) {
            return null;
        }


        return ((TMLComponentDesignPanel)(tp)).tmlctdp.getCompositeComponentByName(componentName);
    }

    public AvatarSMDPanel getAvatarSMDPanel(int indexDesign, String name) {

        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        if (tp == null) {
            TraceManager.addDev("null TP");
            return null;
        }
        if (tp instanceof AvatarDesignPanel) {
            return ((AvatarDesignPanel)tp).getAvatarSMDPanel(name);
        }
        TraceManager.addDev("null ADP :" + name);
        return null;
    }

    public TActivityDiagramPanel getActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        if (tp == null) {
            return null;
        }
        if (tp instanceof DesignPanel) {
            return ((DesignPanel)tp).getActivityDiagramPanel(name);
        }
        return null;
    }

    public TURTLEOSActivityDiagramPanel getTURTLEOSActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        if (tp == null) {
            return null;
        }
        if (tp instanceof TURTLEOSDesignPanel) {
            return ((TURTLEOSDesignPanel)tp).getTURTLEOSActivityDiagramPanel(name);
        }
        return null;
    }

    public TMLActivityDiagramPanel getTMLActivityDiagramPanel(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        if (tp == null) {
            return null;
        }
        if (tp instanceof TMLDesignPanel) {
            return ((TMLDesignPanel)tp).getTMLActivityDiagramPanel(name);
        }
        if (tp instanceof TMLComponentDesignPanel) {
            return ((TMLComponentDesignPanel)tp).getTMLActivityDiagramPanel(name);
        }
        return null;
    }

    public ProactiveSMDPanel getSMDPanel(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        if (tp == null) {
            return null;
        }
        if (tp instanceof ProactiveDesignPanel) {
            return ((ProactiveDesignPanel)tp).getSMDPanel(name);
        }
        return null;
    }

    public int getNbActivityDiagram() {
        TDiagramPanel tdp;
        int cpt = 0;

        for(int i = 0; i<getCurrentJTabbedPane().getTabCount(); i++) {
            tdp = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(i));
            if (tdp instanceof TActivityDiagramPanel) {
                cpt ++;
            }
        }
        return cpt;
    }

    public void setAvatarBDName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setClassDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setDiplodocusMethodologyDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLTaskDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLComponentTaskDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setTMLArchitectureDiagramName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public void setProacticeCSDName(int indexDesign, String name) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexDesign));
        tp.tabbedPane.setTitleAt(0, name);
    }

    public TDiagramPanel getMainTDiagramPanel(int indexPanel) {
        if (tabs.size() > indexPanel) {
            TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(indexPanel));
            return tp.tdp;
        } else {
            return null;
        }

    }

    // TMLCP
    public boolean isTMLCPSDCreated(int index, String s) {
        return isTMLCPSDCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
    }

    public boolean isTMLCPSDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof TMLSDPanel);
    }

    public boolean isTMLCPCreated(int index, String s) {
        return isTMLCPCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
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
        return isSDCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
    }

    public boolean isIODCreated(int index, String s) {
        return isIODCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
    }

    public boolean isProActiveSMDCreated(int index, String s) {
        return isProActiveSMDCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
    }

    public boolean isSDCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof SequenceDiagramPanel);
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

    public boolean openIODiagram(String s) {
        int index = getCurrentJTabbedPane().indexOfTab(s);
        if (index > -1) {
            getCurrentJTabbedPane().setSelectedIndex(index);
            return true;
        }
        return false;
    }

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



    // TMLCP

    public boolean createTMLCPSequenceDiagram(int index, String s) {
        return createTMLCPSequenceDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createTMLCPSequenceDiagram(TURTLEPanel tp, String s) {
        if(isSDCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof TMLCommunicationPatternPanel) ) {
            return false;
        }


        ((TMLCommunicationPatternPanel)tp).addCPSequenceDiagram(s);

        setPanelMode();
        return true;
    }


    public boolean createUniqueTMLCPSequenceDiagram(TURTLEPanel tp, String s) {
        int i;
        for(i=0; i<1000; i++) {
            if(!isTMLCPSDCreated(tp, s+i)) {
                break;
            }
        }

        ((TMLCommunicationPatternPanel)tp).addCPSequenceDiagram(s+i);

        setPanelMode();
        return true;
    }




    public boolean createTMLCPDiagram(int index, String s) {
        return createTMLCPDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createTMLCPDiagram(TURTLEPanel tp, String s) {
        if(isTMLCPCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof TMLCommunicationPatternPanel)) {
            return false;
        }

        ((TMLCommunicationPatternPanel)tp).addCPDiagram(s);
        setPanelMode();
        return true;
    }

    // End of TMLCP


    public SequenceDiagramPanel getSequenceDiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getSequenceDiagramPanel(tp, s);
    }

    public AttackTreeDiagramPanel getAttackTreeDiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAttackTreeDiagramPanel(tp, s);
    }

    public AttackTreeDiagramPanel getAttackTreeDiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof AttackTreeDiagramPanel)
                    return  (AttackTreeDiagramPanel)(tp.panelAt(i));
            }
        }
        return null;
    }


    public TMLCPPanel getTMLCPDiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getTMLCPDiagramPanel(tp, s);
    }

    public TMLCPPanel getTMLCPDiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof TMLCPPanel)
                    return  (TMLCPPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public TMLSDPanel getTMLCPSDDiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getTMLCPSDDiagramPanel(tp, s);
    }

    public TMLSDPanel getTMLCPSDDiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof TMLSDPanel)
                    return  (TMLSDPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public InteractionOverviewDiagramPanel getIODiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getIODiagramPanel(tp, s);
    }

    public SequenceDiagramPanel getSequenceDiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof SequenceDiagramPanel)
                    return  (SequenceDiagramPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public InteractionOverviewDiagramPanel getIODiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof InteractionOverviewDiagramPanel)
                    return  (InteractionOverviewDiagramPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public UseCaseDiagramPanel getUseCaseDiagramPanel(int index, int indexTab, String s) {
        //TraceManager.addDev("Searching for " + s + " at index =" + index);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getUseCaseDiagramPanel(tp, indexTab, s);
    }

    public UseCaseDiagramPanel getUseCaseDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (UseCaseDiagramPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public boolean createSequenceDiagram(int index, String s) {
        return createSequenceDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createSequenceDiagram(TURTLEPanel tp, String s) {
        if(isSDCreated(tp, s)) {
            return false;
        }

        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel)tp).addSequenceDiagram(s);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel)tp).addSequenceDiagram(s);
        }
        setPanelMode();
        return true;
    }


    public boolean createUniqueSequenceDiagram(TURTLEPanel tp, String s) {
        int i;
        for(i=0; i<1000; i++) {
            if(!isSDCreated(tp, s+i)) {
                break;
            }
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel)tp).addSequenceDiagram(s+i);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel)tp).addSequenceDiagram(s+i);
        }

        setPanelMode();
        return true;
    }

    public boolean createSequenceDiagramFromUCD(TURTLEPanel tp, String s, UseCaseDiagramPanel _ucdp) {
        if (!createUniqueSequenceDiagram(tp, s)) {
            return false;
        }

        if (! ((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel))) {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel)tp).addInstancesToLastSD(_ucdp);
        } else if (tp instanceof AvatarAnalysisPanel) {
            ((AvatarAnalysisPanel)tp).addInstancesToLastSD(_ucdp);
        }

        return true;
    }



    public boolean createIODiagram(int index, String s) {
        return createIODiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createIODiagram(TURTLEPanel tp, String s) {
        if(isIODCreated(tp, s)) {
            return false;
        }

        if (!(tp instanceof AnalysisPanel)) {
            return false;
        }

        ((AnalysisPanel)tp).addIODiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createUseCaseDiagram(int index, String s) {
        return createUseCaseDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createUniqueUseCaseDiagram(TURTLEPanel tp, String s) {
        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel)))  {
            return false;
        }

        int i;
        for(i=0; i<1000; i++) {
            if(!isUseCaseDiagramCreated(tp, s + " " + i)) {
                break;
            }
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel)tp).addUseCaseDiagram(s + " " + i);
        } else {
            ((AvatarAnalysisPanel)tp).addUseCaseDiagram(s + " " + i);
        }
        setPanelMode();
        return true;
    }

    public boolean createUseCaseDiagram(TURTLEPanel tp, String s) {
        if (!((tp instanceof AnalysisPanel) || (tp instanceof AvatarAnalysisPanel)))  {
            return false;
        }

        if (tp instanceof AnalysisPanel) {
            ((AnalysisPanel)tp).addUseCaseDiagram(s);
        } else {
            ((AvatarAnalysisPanel)tp).addUseCaseDiagram(s);
        }
        setPanelMode();
        return true;
    }

    public boolean createAvatarCD(int index, String s) {
        return createAvatarCD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createUniqueAvatarCD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel))  {
            return false;
        }

        int i;
        for(i=0; i<1000; i++) {
            if(!isAvatarCDCreated(tp, s + " " + i)) {
                break;
            }
        }

        ((AvatarAnalysisPanel)tp).addAvatarContextDiagram(s + " " + i);


        setPanelMode();
        return true;
    }

    public boolean createAvatarCD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel))  {
            return false;
        }


        ((AvatarAnalysisPanel)tp).addAvatarContextDiagram(s);


        setPanelMode();
        return true;
    }

    public boolean createAvatarAD(int index, String s) {
        return createAvatarAD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createUniqueAvatarAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }

        int i;
        for(i=0; i<1000; i++) {
            if(!isAvatarADCreated(tp, s + " " + i)) {
                break;
            }
        }

        ((AvatarAnalysisPanel)tp).addAvatarActivityDiagram(s + " " + i);
        setPanelMode();
        return true;
    }

    public boolean createAvatarAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarAnalysisPanel)) {
            return false;
        }


        ((AvatarAnalysisPanel)tp).addAvatarActivityDiagram(s);

        setPanelMode();
        return true;
    }

    //Changed by Solange from public boolean...
    public String createProActiveSMD(int index, String s) {
        //Adde by Solange String name at  the beginning
        String name=createProActiveSMD((TURTLEPanel)(tabs.elementAt(index)), s);
        return(name); //changed from return true
    }

    //Return changed by Solange from boolean to String
    public String createProActiveSMD(TURTLEPanel tp, String s) {

        //Added by Solange. It fills the lists of components, interfaces, etc
        TURTLEPanel tp2 = getCurrentTURTLEPanel();
        gtm.generateLists((ProactiveDesignPanel)tp2);
        //

        if (!(tp instanceof ProactiveDesignPanel)) {
            return null; //Changed by Solange from return false
        }

        s=((ProactiveDesignPanel)tp).addSMD(s);
        //Added by Solange
        //  ProactiveSMDPanel temp=((ProactiveDesignPanel)tp).getSMDPanel(s);
        //Added by Solange
        //And removed by Emil

        /*
          LinkedList cmps=gtm.gpdtemp.getProCSDComponentsList();
          for (int i=0;i<cmps.size();i++)
          {
          ProCSDComponent c = (ProCSDComponent)cmps.get(i);

          if (c.getType()== TGComponentManager.PROCSD_COMPONENT)
          {
          if(c.getComponentID().equals(temp.getName()))
          {
          c.mySMD=temp;
          i=cmps.size();
          }
          }
          }
        */
        //
        setPanelMode();
        return(s); //Changes by Solange from return true
    }

    public boolean createAvatarRD(int index, String s) {
        return createAvatarRD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createAvatarRD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarRequirementPanel)) {
            return false;
        }

        ((AvatarRequirementPanel)tp).addAvatarRD(s);
        setPanelMode();
        return true;
    }

    public boolean createAvatarMAD(int index, String s) {
        return createAvatarMAD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createAvatarMAD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarMADsPanel)) {
            return false;
        }

        ((AvatarMADsPanel)tp).addAvatarMADPanel(s);
        setPanelMode();
        return true;
    }

    public boolean createAvatarPD(int index, String s) {
        return createAvatarPD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createAvatarPD(TURTLEPanel tp, String s) {
        if (!(tp instanceof AvatarRequirementPanel)) {
            return false;
        }

        ((AvatarRequirementPanel)tp).addAvatarPD(s);
        setPanelMode();
        return true;
    }

    public boolean isRequirementCreated(int index, String s) {
        return isRequirementCreated(((TURTLEPanel)(tabs.elementAt(index))), s);
    }

    public boolean isRequirementCreated(TURTLEPanel tp, String s) {
        int index = tp.tabbedPane.indexOfTab(s);
        if (index == -1) {
            return false;
        }
        return (tp.panelAt(index) instanceof RequirementDiagramPanel);
    }

    public boolean createRequirementDiagram(int index, String s) {
        return createRequirementDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createRequirementDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof RequirementPanel)) {
            return false;
        }

        ((RequirementPanel)tp).addRequirementDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createAttackTreeDiagram(int index, String s) {
        return createAttackTreeDiagram((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createAttackTreeDiagram(TURTLEPanel tp, String s) {
        if (!(tp instanceof AttackTreePanel)) {
            return false;
        }

        ((AttackTreePanel)tp).addAttackTreeDiagram(s);
        setPanelMode();
        return true;
    }

    public boolean createEBRDD(int index, String s) {
        return createEBRDD((TURTLEPanel)(tabs.elementAt(index)), s);
    }

    public boolean createEBRDD(TURTLEPanel tp, String s) {
        if (!(tp instanceof RequirementPanel)) {
            return false;
        }

        ((RequirementPanel)tp).addEBRDD(s);
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
            String modeling = gtm.makeXMLFromTurtleModeling(-1);
            TraceManager.addDev("Model made");
        } catch (Exception e) {
        }

    }



    public AvatarRDPanel getAvatarRDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAvatarRDPanel(tp, indexTab, s);
    }

    public AvatarRDPanel getAvatarRDPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarRDPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarMADPanel getAvatarMADPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAvatarMADPanel(tp, indexTab, s);
    }

    public AvatarMADPanel getAvatarMADPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarMADPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarPDPanel getAvatarPDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAvatarPDPanel(tp, indexTab, s);
    }


    public AvatarPDPanel getAvatarPDPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarPDPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarCDPanel getAvatarCDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAvatarCDPanel(tp, indexTab, s);
    }


    public AvatarCDPanel getAvatarCDPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarCDPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public AvatarADPanel getAvatarADPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getAvatarADPanel(tp, indexTab, s);
    }


    public AvatarADPanel getAvatarADPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (AvatarADPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getRequirementDiagramPanel(tp, s);
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof RequirementDiagramPanel)
                    return  (RequirementDiagramPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public RequirementDiagramPanel getRequirementDiagramPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getRequirementDiagramPanel(tp, indexTab, s);
    }


    public RequirementDiagramPanel getRequirementDiagramPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (RequirementDiagramPanel)(tp.panelAt(indexTab));
        }
        return null;
    }

    public EBRDDPanel getEBRDDPanel(int index, String s) {
        //TraceManager.addDev("Searching for " + s);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getEBRDDPanel(tp, s);
    }

    public EBRDDPanel getEBRDDPanel(TURTLEPanel tp, String s) {
        for(int i=0; i<tp.tabbedPane.getTabCount(); i++) {
            if (tp.tabbedPane.getTitleAt(i).equals(s)) {
                if (tp.panelAt(i) instanceof EBRDDPanel)
                    return  (EBRDDPanel)(tp.panelAt(i));
            }
        }
        return null;
    }

    public EBRDDPanel getEBRDDPanel(int index, int indexTab, String s) {
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
        return getEBRDDPanel(tp, indexTab, s);
    }


    public EBRDDPanel getEBRDDPanel(TURTLEPanel tp, int indexTab, String s) {
        if(tp.tabbedPane.getTitleAt(indexTab).equals(s)) {
            return (EBRDDPanel)(tp.panelAt(indexTab));
        }
        return null;
    }


    public void alignInstances() {
        //TraceManager.addDev("Align instances");
        if (getCurrentTDiagramPanel() instanceof SequenceDiagramPanel) {
            ((SequenceDiagramPanel)(getCurrentTDiagramPanel())).alignInstances();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }

        if (getCurrentTDiagramPanel() instanceof TMLSDPanel) {
            ((TMLSDPanel)(getCurrentTDiagramPanel())).alignInstances();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }
    }

    public void alignPartitions() {
        //TraceManager.addDev("Align instances");
        if (getCurrentTDiagramPanel() instanceof AvatarADPanel) {
            ((AvatarADPanel)(getCurrentTDiagramPanel())).alignPartitions();
            changeMade(getCurrentTDiagramPanel(), TDiagramPanel.MOVE_COMPONENT);
            getCurrentTDiagramPanel().repaint();
        }
    }

    public void enhanceDiagram() {
        getCurrentTDiagramPanel().enhance();
    }

    public JTabbedPane getCurrentJTabbedPane() {
        return ((TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()))).tabbedPane;
    }

    public int getCurrentSelectedIndex() {
        return mainTabbedPane.getSelectedIndex();
    }


    public TDiagramPanel getCurrentTDiagramPanel() {
        try {
            TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()));
            return (TDiagramPanel)(tp.panels.elementAt(tp.tabbedPane.getSelectedIndex()));
        } catch (Exception e) {
            return null;
        }
    }

    public TDiagramPanel getCurrentMainTDiagramPanel() {
        return ((TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()))).tdp;
    }

    public TURTLEPanel getCurrentTURTLEPanel() {
        if (tabs.size() == 0) {
            return null;
        } else {
            return (TURTLEPanel)(tabs.elementAt(mainTabbedPane.getSelectedIndex()));
        }
    }


    public void reinitCountOfPanels() {
        int i, j;
        TURTLEPanel tp;
        for(i = 0; i<mainTabbedPane.getTabCount(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            for(j=0; j<tp.tabbedPane.getTabCount(); j++) {
                ((TDiagramPanel)(tp.panels.elementAt(j))).count = 0;
            }
        }
    }

    public void setPanelMode() {
        int index;
        TURTLEPanel tp = getCurrentTURTLEPanel();
        index = tp.tabbedPane.getSelectedIndex();

        if (index < tp.panels.size() -1) {
            setMode(FORWARD_DIAG);
        } else {
            setMode(NO_FORWARD_DIAG);
        }

        if (index > 0) {
            setMode(BACKWARD_DIAG);
        } else {
            setMode(NO_BACKWARD_DIAG);
        }

        setMode(METHO_CHANGED);

    }

    public void paneMADAction(ChangeEvent e) {
        //TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            //TraceManager.addDev("Pane design action 3");

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneDesignAction(ChangeEvent e) {
        //TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            //TraceManager.addDev("Pane design action 3");

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneDiplodocusMethodologyAction(ChangeEvent e) {
        //TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {

                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            //TraceManager.addDev("Pane design action 3");

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneAnalysisAction(ChangeEvent e) {
        //TraceManager.addDev("Pane analysis action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane analysis action 1 on " + tdp1.getName());
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane analysis action 2");
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            //TraceManager.addDev("Pane analysis action 3");

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane analysis action");
        }
    }

    public void paneDeployAction(ChangeEvent e) {
        //TraceManager.addDev("Pane design action size=" + tabs.size());
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();
            //TraceManager.addDev("Pane design action 3");

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane design action");
        }
    }

    public void paneRequirementAction(ChangeEvent e) {
        try {

            TDiagramPanel tdp1 = (TDiagramPanel)(getCurrentTURTLEPanel().panels.elementAt(getCurrentJTabbedPane().getSelectedIndex()));
            //TraceManager.addDev("Pane design action 1");
            if (activetdp != null) {
                activetdp.activateActions(false);
                unactivateDrawing();
                activetdp.stopAddingConnector();
            }
            //TraceManager.addDev("Pane design action 1 on "+ tdp1.getName());
            tdp1.activateActions(true);
            activetdp = tdp1;

            setEditMode();
            setPanelMode();

            // activate the   drawing   of the right pane
            basicActivateDrawing();

        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane design action");
        }
    }

    // nameTab : array of strings not to be removed
    public void removeAnalysisDiagrams(String [] nameTab) {
        int i, j;
        String value;
        boolean found;

        for(i=1; i<getCurrentJTabbedPane().getTabCount(); i++) {
            value = getCurrentJTabbedPane().getTitleAt(i);
            found = false;
            for(j=0; j<nameTab.length; j++) {
                if (nameTab[j] != null) {
                    if (nameTab[j].compareTo(value) ==0) {
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

        //String s = (String)JOptionPane.showInputDialog(this, "Name of the diagram:", JOptionPane.QUESTION_MESSAGE);
        String s = (String)JOptionPane.showInputDialog(frame, "Name of the diagram:", "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null, value);

        if ((s != null) && (s.length() > 0)){
            // name already in use?
            index1 = getCurrentJTabbedPane().indexOfTab(s);
            /*if (methoMode == METHO_ANALYSIS) {
              index1 = mainAnalysisTabbedPane.indexOfTab(s);

              } else {
              index1 = getCurrentJTabbedPane.indexOfTab(s);

              }*/
            if (index1 > -1) {
                JOptionPane.showMessageDialog(frame, "Name is already in use", "Error", JOptionPane.INFORMATION_MESSAGE);
            } else {
                /*if (methoMode == METHO_ANALYSIS) {
                  mainAnalysisTabbedPane.setTitleAt(index, s);
                  } else {
                  mainDesignTabbedPane.setTitleAt(index, s);
                  }*/
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
        //TraceManager.addDev("Delete");

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

    private void activateDrawing(TDiagramPanel tdp, JTabbedPane good, Vector goodV, JTabbedPane wrong, Vector wrongV) {
        int i;
        TDiagramPanel tdp2;

        for(i=0; i<good.getTabCount(); i++) {
            tdp2 = (TDiagramPanel)(goodV.elementAt(i));
            if (tdp2 == tdp) {
                tdp2.setDraw(true);
                if (tdp2.mode == TDiagramPanel.SELECTED_COMPONENTS) {
                    setMode(MainGUI.CUTCOPY_OK);
                    setMode(MainGUI.EXPORT_LIB_OK);
                } else {
                    setMode(MainGUI.CUTCOPY_KO);
                    setMode(MainGUI.EXPORT_LIB_KO);
                }
            } else {
                tdp2.setDraw(false);
            }
        }

        for(i=0; i<wrong.getTabCount(); i++) {
            tdp2 = (TDiagramPanel)(wrongV.elementAt(i));
            tdp2.setDraw(false);
        }
    }

    public void paneAction(ChangeEvent e) {
        //TraceManager.addDev("Pane action");
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            //TraceManager.addDev("Stop Adding connector in maingui");
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

            if ((getCurrentTURTLEPanel() instanceof AvatarDesignPanel) || (getCurrentTURTLEPanel() instanceof AvatarRequirementPanel)) {
                mainBar.showAvatarActions(true);
            } else if ((getCurrentTURTLEPanel() instanceof TMLDesignPanel) || (getCurrentTURTLEPanel() instanceof TMLComponentDesignPanel) || (getCurrentTURTLEPanel() instanceof TMLArchiPanel)){
                mainBar.showDiplodocusActions(true);
            }  else {
                mainBar.showAvatarActions(false);
            }

            setMode(PANEL_CHANGED);

            setEditMode();
            setPanelMode();


        } catch (Exception ex) {
            //TraceManager.addDev("Exception pane action: " + ex.getMessage());
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public boolean newTClassName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for(int i = 0; i<jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        //TraceManager.addDev("old " + old + " niou " + niou);
        for(int i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the activity diagram of " + niou);
                TDiagramPanel tdp;
                //change panel name
                for(int j=0; j<tp.panels.size(); j++) {
                    tdp = (TDiagramPanel)(tp.panels.elementAt(j));
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
        for(int i = 0; i<jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        //TraceManager.addDev("old " + old + " niou " + niou);
        for(int i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TURTLE-OS activity diagram of " + niou);
                TDiagramPanel tdp;
                //change panel name
                for(int j=0; j<tp.panels.size(); j++) {
                    tdp = (TDiagramPanel)(tp.panels.elementAt(j));
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
        //TraceManager.addDev("Panel=" + tp + " Old  task name = " + old + " New task name=" + niou);
        JTabbedPane jtp = tp.tabbedPane;
        for(int i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("jtp  = " + jtp.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        //TraceManager.addDev("old " + old + " niou " + niou);
        for(int i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            //TraceManager.addDev("jtp  = " + jtp.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TML activity diagram of " + niou);
                TDiagramPanel tdp;
                //change panel name
                for(int j=0; j<tp.panels.size(); j++) {
                    tdp = (TDiagramPanel)(tp.panels.elementAt(j));
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                        //TraceManager.addDev("Renamed to " + niou);
                    }
                }

                return true;
            }
        }
        // internal error
        ErrorGUI.exit(ErrorGUI.ERROR_TAB);
        return false;
    }

    public boolean newAvatarBDBlockName(TURTLEPanel tp, String old, String niou) {
        //TraceManager.addDev("Panel=" + tp + " Old  task name = " + old + " New task name=" + niou);
        JTabbedPane jtp = tp.tabbedPane;
        int i;
        for(i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("jtp  = " + jtp.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        //TraceManager.addDev("old " + old + " niou " + niou);
        //TraceManager.addDev("nb Of panels:"+ jtp.getTabCount());
        for(i = 0; i<jtp.getTabCount(); i++) {
            //TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            //TraceManager.addDev("jtp  = >" + jtp.getTitleAt(i) + "<");
            //TraceManager.addDev("old  = >" + old + "<");
            if (jtp.getTitleAt(i).compareTo(old) == 0) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the state machine of " + niou);
                TDiagramPanel tdp;
                //change panel name
                for(int j=0; j<tp.panels.size(); j++) {
                    tdp = (TDiagramPanel)(tp.panels.elementAt(j));
                    if (tdp.getName().equals(old)) {
                        tdp.setName(niou);
                        //TraceManager.addDev("Renamed to " + niou);
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
        for(int i = 0; i<jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return true;
            }
        }
        return false;
    }

    public boolean newTMLComponentTaskName(TURTLEPanel tp, String old, String niou) {
        JTabbedPane jtp = tp.tabbedPane;
        for(int i = 0; i<jtp.getTabCount(); i++) {
            if (jtp.getTitleAt(i).equals(niou)) {
                return false;
            }
        }
        TraceManager.addDev("old " + old + " niou " + niou);
        for(int i = 0; i<jtp.getTabCount(); i++) {
            TraceManager.addDev("Tab " + i + " = " + mainTabbedPane.getTitleAt(i));
            if (jtp.getTitleAt(i).equals(old)) {
                jtp.setTitleAt(i, niou);
                jtp.setToolTipTextAt(i, "Opens the TML activity diagram of " + niou);
                TDiagramPanel tdp;
                //change panel name
                for(int j=0; j<tp.panels.size(); j++) {
                    tdp = (TDiagramPanel)(tp.panels.elementAt(j));
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

    public void cloneTab(int index) {
        String s = gtm.makeXMLFromTurtleModeling(index);
        try {
            gtm.loadModelingFromXML(s);
            changeMade(null, -1);
        } catch (MalformedModelingException mme) {
            JOptionPane.showMessageDialog(frame, "Modeling could not be loaded (unsupported file) ", "Error when loading modeling", JOptionPane.INFORMATION_MESSAGE);
            frame.setTitle("TTool: unamed project");
        }

    }

    public void requestRemoveTab(int index) {
        if (index >= tabs.size()) {
            return;
        }

        tabs.removeElementAt(index);
        mainTabbedPane.remove(index);
        changeMade(null, -1);
    }

    public void requestMoveRightTab(int index) {
        //TraceManager.addDev("Move right");
        if (index > tabs.size()-2) {
            return;
        }
        requestMoveTabFromTo(index, index+1);
        changeMade(null, -1);
    }

    public void requestMoveLeftTab(int index) {
        //TraceManager.addDev("Move left");
        if (index < 1) {
            return;
        }
        requestMoveTabFromTo(index, index-1);
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

        Object o = tabs.elementAt(src);
        tabs.removeElementAt(src);
        tabs.insertElementAt(o, dst);

        mainTabbedPane.setSelectedIndex(dst);
    }



    public void requestRenameTab(int index) {
        String oldName = mainTabbedPane.getTitleAt(index);
        String s = (String)JOptionPane.showInputDialog(frame, "Name: ", "Renaming a tab=", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null, mainTabbedPane.getTitleAt(index));
        if ((s != null) && (s.length() > 0)){
            // name already in use?
            if (s.compareTo(oldName) != 0) {
                if (isAValidTabName(s)) {
                    mainTabbedPane.setTitleAt(index, s);
                    changeMade(getCurrentTDiagramPanel(), ((TURTLEPanel)(tabs.elementAt(index))).tdp.MOVE_COMPONENT);

                    TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));
                    if ((tp instanceof TMLDesignPanel) || (tp instanceof TMLComponentDesignPanel)) {
                        renameMapping(oldName, s);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid name", "Error", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
        changeMade(null, -1);
    }

    public void renameMapping(String oldName, String newName) {
        TURTLEPanel tp;

        for(int i = 0; i<mainTabbedPane.getTabCount(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            if (tp instanceof TMLArchiPanel) {
                ((TMLArchiPanel)tp).renameMapping(oldName, newName);
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

        for(j=0; j<tp.tabbedPane.getTabCount(); j++) {
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
        mainTabbedPane.setSelectedIndex(p.x);
        TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(p.x));
        tp.tabbedPane.setSelectedIndex(p.y);
        return (TDiagramPanel)(tp.panels.elementAt(p.y));

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

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            index = tp.panels.indexOf(tdp);
            if (index > -1) {
                p.x = i;
                p.y = index;
                return p;
            }
        }
        p.x = 0;
        p.y = 0;

        return p;
    }

    public DesignPanel getDesignPanel(String name) {
        int index = mainTabbedPane.indexOfTab(name);
        try {
            TURTLEPanel tp = (TURTLEPanel)(tabs.elementAt(index));

            if (tp instanceof DesignPanel) {
                return (DesignPanel)tp;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean selectMainTab(String id) {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
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

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
            index = tp.panels.indexOf(tdp);
            if (index > -1) {
                selectTab(tp);
                tp.tabbedPane.setSelectedIndex(index);
            }
        }
    }

    public void toggleAttributes() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)){
            //TraceManager.addDev("Toggle attributes");
            TClassDiagramPanel tdcp = (TClassDiagramPanel)tdp;
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

    public void toggleAVATARIDs() {
        setAVATARIDs(!TDiagramPanel.AVATAR_ID_ON);
        TraceManager.addDev("AVATAR id: " + TDiagramPanel.AVATAR_ID_ON);
    }

    // For simulation purpose
    public void resetAllDIPLOIDs() {
        TURTLEPanel tp;

        for(int i=0; i<tabs.size(); i++) {
            tp = (TURTLEPanel)(tabs.elementAt(i));
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
        if(jfais.isVisible()) {
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
        if(jfais.isVisible()) {
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

        for(RunningInfo ri: runningIDs) {
            if (ri.id == id) {
                return ri;
            }
        }

        return null;
    }

    public synchronized LoadInfo isLoadID(int id) {
        if (loadIDs == null) {
            return null;
        }

        for(LoadInfo li: loadIDs) {
            if (li.id == id) {
                return li;
            }
        }

        return null;
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

    public synchronized void addRunningIDTaskState(int _id, String _state) {
        if (runningIDs == null) {
            return;
        }

        if (_state == null) {
            _state = "unknown";
        }

        for(RunningInfo ri: runningIDs) {
            if (ri.id == _id) {
                ri.state = _state.toLowerCase();
                //TraceManager.addDev("Updated state on UML diagram");
                TDiagramPanel tdp = getCurrentTDiagramPanel();
                if (tdp != null) {
                    tdp.repaint();
                }
                return;
            }
        }

    }

    public synchronized void addRunningID(int _id, int _nextCommand, String _progression, String _startTime, String _finishTime, String _transStartTime, String _transFinishTime, String _state) {
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
        //TraceManager.addDev("Running id " + id +  " added");
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
        //TraceManager.addDev("Running id " + _id +  " added load=" + _load);
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.repaint();
        }
    }

    public synchronized void removeRunningId(Integer id) {
        if (runningIDs == null) {
            return ;
        }

        for(RunningInfo ri: runningIDs) {
            if (ri.id == id.intValue()) {
                runningIDs.remove(ri);
                //TraceManager.addDev("Running id " + i +  " removed");
                return;
            }
        }
        getCurrentTDiagramPanel().repaint();
    }

    public synchronized void removeLoadId(int _id) {
        if (loadIDs == null) {
            return ;
        }

        for(LoadInfo li: loadIDs) {
            if (li.id == _id) {
                loadIDs.remove(li);
                //TraceManager.addDev("Running id " + i +  " removed");
                return;
            }
        }
        getCurrentTDiagramPanel().repaint();
    }

    public void toggleGates() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)){
            //TraceManager.addDev("Toggle gates");
            TClassDiagramPanel tdcp = (TClassDiagramPanel)tdp;
            tdcp.setGatesVisible(!tdcp.areGatesVisible());
            tdcp.checkAllMySize();
            tdcp.repaint();
            changeMade(tdcp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleSynchro() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TClassDiagramPanel)){
            //TraceManager.addDev("Toggle synchro");
            TClassDiagramPanel tdcp = (TClassDiagramPanel)tdp;
            tdcp.setSynchroVisible(!tdcp.areSynchroVisible());
            tdcp.checkAllMySize();
            tdcp.repaint();
            changeMade(tdcp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleJava() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TActivityDiagramPanel)){
            //TraceManager.addDev("Toggle synchro");
            TActivityDiagramPanel tadp = (TActivityDiagramPanel)tdp;
            tadp.setJavaVisible(!tadp.isJavaVisible());
            tadp.checkAllMySize();
            tadp.repaint();
            changeMade(tadp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleInternalComment() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null) {
            tdp.setInternalCommentVisible((tdp.getInternalCommentVisible() +1 )% 3);
            tdp.checkAllMySize();
            tdp.repaint();
            changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleAttr() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if (tdp != null){
            //TraceManager.addDev("Toggle attributes");
            tdp.setAttributes((tdp.getAttributeState() +1 )% 3);
            tdp.checkAllMySize();
            tdp.repaint();
            changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleChannels() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)){
            //TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel)tdp;
            tmltdp.setChannelsVisible(!tmltdp.areChannelsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleEvents() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)){
            //TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel)tdp;
            tmltdp.setEventsVisible(!tmltdp.areEventsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public void toggleRequests() {
        TDiagramPanel tdp = getCurrentTDiagramPanel();
        if ((tdp != null) && (tdp instanceof TMLTaskDiagramPanel)){
            //TraceManager.addDev("Toggle attributes");
            TMLTaskDiagramPanel tmltdp = (TMLTaskDiagramPanel)tdp;
            tmltdp.setRequestsVisible(!tmltdp.areRequestsVisible());
            tmltdp.checkAllMySize();
            tmltdp.repaint();
            changeMade(tmltdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
        }
    }

    public boolean isAValidTabName(String name) {
        return name.matches("((\\w)*(\\s)*)*");
    }



    public void windowClosing(WindowEvent e) {
        //frame.setVisible(false);
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

    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        //TraceManager.addDev("Command:" + command);
        TDiagramPanel tdp1 = getCurrentTDiagramPanel();
        if (tdp1 != null) {
            //TraceManager.addDev("Stop Adding connector in maingui");
            tdp1.stopAddingConnector();
        }


        // Compare the action command to the known actions.
        if (command.equals(actions[TGUIAction.ACT_NEW].getActionCommand()))  {
            newProject();
        } else if (command.equals(actions[TGUIAction.ACT_NEW_DESIGN].getActionCommand())) {
            newDesign();
        } else if (command.equals(actions[TGUIAction.ACT_NEW_ANALYSIS].getActionCommand())) {
            newAnalysis();
        } else if (command.equals(actions[TGUIAction.ACT_OPEN].getActionCommand())) {
            openProject();
        } else if (command.equals(actions[TGUIAction.ACT_MERGE].getActionCommand())) {
            mergeProject();
        } else if (command.equals(actions[TGUIAction.ACT_OPEN_LAST].getActionCommand())) {
            openLastProject();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE].getActionCommand())) {
            saveProject();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_AS].getActionCommand())) {
            saveAsProject();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_TIF].getActionCommand())) {
            saveTIF();
        } else if (command.equals(actions[TGUIAction.ACT_OPEN_TIF].getActionCommand())) {
            openTIF();
        } else if (command.equals(actions[TGUIAction.ACT_OPEN_SD].getActionCommand())) {
            openSD();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_LOTOS].getActionCommand())) {
            saveLastLotos();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_DTA].getActionCommand())) {
            saveLastDTA();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_RG].getActionCommand())) {
            saveLastRG();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_TLSA].getActionCommand())) {
            saveLastTLSA();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_AUT].getActionCommand())) {
            saveLastRGAUT();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_AUTPROJ].getActionCommand())) {
            saveLastRGAUTProj();
        } else if (command.equals(actions[TGUIAction.ACT_SAVE_AUTMODIFIED].getActionCommand())) {
            saveLastModifiedRG();
        } else if (command.equals(actions[TGUIAction.ACT_EXPORT_LIB].getActionCommand())) {
            exportLibrary();
        } else if (command.equals(actions[TGUIAction.ACT_IMPORT_LIB].getActionCommand())) {
            importLibrary();
        } else if (command.equals(actions[TGUIAction.ACT_QUIT].getActionCommand())) {
            quitApplication();
        } else if (command.equals(actions[TGUIAction.ACT_CUT].getActionCommand())) {
            cut();
        } else if (command.equals(actions[TGUIAction.ACT_COPY].getActionCommand())) {
            copy();
        } else if (command.equals(actions[TGUIAction.ACT_PASTE].getActionCommand())) {
            paste();
        } else if (command.equals(actions[TGUIAction.ACT_DELETE].getActionCommand())) {
            delete();
        } else if (command.equals(actions[TGUIAction.ACT_ZOOM_MORE].getActionCommand())) {
            zoomMore();
        } else if (command.equals(actions[TGUIAction.ACT_ZOOM_LESS].getActionCommand())) {
            zoomLess();
        } else if (command.equals(actions[TGUIAction.ACT_BACKWARD].getActionCommand())) {
            backward();
        } else if (command.equals(actions[TGUIAction.ACT_FORWARD].getActionCommand())) {
            forward();
        } else if (command.equals(actions[TGUIAction.ACT_FIRST_DIAG].getActionCommand())) {
            firstDiag();
        } else if (command.equals(actions[TGUIAction.ACT_BACK_DIAG].getActionCommand())) {
            backDiag();
        } else if (command.equals(actions[TGUIAction.ACT_NEXT_DIAG].getActionCommand())) {
            nextDiag();
        }  else if (command.equals(actions[TGUIAction.ACT_LAST_DIAG].getActionCommand())) {
            lastDiag();
        } else if (command.equals(actions[TGUIAction.ACT_ABOUT].getActionCommand())) {
            aboutVersion();
        } else if (command.equals(actions[TGUIAction.ACT_TTOOL_CONFIGURATION].getActionCommand())) {
            showTToolConfiguration();
        } else if (command.equals(actions[TGUIAction.ACT_TURTLE_WEBSITE].getActionCommand())) {
            aboutTURTLE();
        } else if (command.equals(actions[TGUIAction.ACT_TURTLE_DOCUMENTATION].getActionCommand())) {
            helpTURTLE();
        } else if (command.equals(actions[TGUIAction.ACT_DIPLODOCUS_DOCUMENTATION].getActionCommand())) {
            helpDIPLODOCUS();
        } else if (command.equals(actions[TGUIAction.ACT_MODEL_CHECKING].getActionCommand())) {
            modelChecking();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_RTLOTOS].getActionCommand())) {
            generateRTLOTOS();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_LOTOS].getActionCommand())) {
            generateLOTOS();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_AUT].getActionCommand())) {
            generateAUT();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_AUTS].getActionCommand())) {
            generateAUTS();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_UPPAAL].getActionCommand())) {
            generateUPPAAL();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_PROVERIF].getActionCommand())) {
            generateProVerif();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_JAVA].getActionCommand())) {
            generateJava();
        } else if (command.equals(actions[TGUIAction.ACT_SIMU_JAVA].getActionCommand())) {
            simuJava();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_SYSTEMC].getActionCommand())) {
            generateSystemC();
        } else if (command.equals(actions[TGUIAction.ACT_SIMU_SYSTEMC].getActionCommand())) {
            interactiveSimulationSystemC();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_TMLTXT].getActionCommand())) {
            generateTMLTxt();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_DESIGN].getActionCommand())) {
            generateDesign();
        } else if (command.equals(actions[TGUIAction.ACT_CHECKCODE].getActionCommand())) {
            checkCode();
        } else if (command.equals(actions[TGUIAction.ACT_SIMULATION].getActionCommand())) {
            simulation();
        } else if (command.equals(actions[TGUIAction.ACT_VALIDATION].getActionCommand())) {
            formalValidation();
        } else if (command.equals(actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].getActionCommand())) {
            oneClickLOTOSRG();
        } else if (command.equals(actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG].getActionCommand())) {
            oneClickRTLOTOSRG();
        } else if (command.equals(actions[TGUIAction.ACT_PROJECTION].getActionCommand())) {
            projection();
        }  else if (command.equals(actions[TGUIAction.ACT_GRAPH_MODIFICATION].getActionCommand())) {
            modifyGraph();
        } else if (command.equals(actions[TGUIAction.ACT_BISIMULATION].getActionCommand())) {
            bisimulation();
        } else if (command.equals(actions[TGUIAction.ACT_BISIMULATION_CADP].getActionCommand())) {
            bisimulationCADP();
        } else if (command.equals(actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].getActionCommand())) {
            seekDeadlockAUT();
        } else if (command.equals(actions[TGUIAction.ACT_DEADLOCK_SEEKER_SAVED_AUT].getActionCommand())) {
            seekDeadlockSavedAUT();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_STAT_AUT].getActionCommand())) {
            statAUT();
        }  else if (command.equals(actions[TGUIAction.ACT_VIEW_STAT_AUTDIPLODOCUS].getActionCommand())) {
            statAUTDiplodocus();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].getActionCommand())) {
            statAUTProj();
        }  else if (command.equals(actions[TGUIAction.ACT_VIEW_STAT_SAVED_AUT].getActionCommand())) {
            statSavedAUT();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_PM_AUT].getActionCommand())) {
            pmAUT();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].getActionCommand())) {
            pmAUTProj();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_PM_SAVED_AUT].getActionCommand())) {
            pmSavedAUT();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_RTLOTOS].getActionCommand())) {
            showFormalSpecification();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_JAVA].getActionCommand())) {
            showJavaCode();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_BIRDEYES].getActionCommand())) {
            showBirdEyesView();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_BIRDEYES_EMB].getActionCommand())) {
            showEmbeddedBirdEyesView();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_WAVE].getActionCommand())) {
            showWave();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].getActionCommand())) {
            showSuggestedDesign();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_SIM].getActionCommand())) {
            showSimulationTrace();
        }  else if (command.equals(actions[TGUIAction.ACT_VIEW_SIM_CHRONO].getActionCommand())) {
            showSimulationTraceChrono();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_DTADOT].getActionCommand())) {
            showDTA();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_RGDOT].getActionCommand())) {
            showRG();
        }  else if (command.equals(actions[TGUIAction.ACT_VIEW_TLSADOT].getActionCommand())) {
            showTLSA();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_RGAUTDOT].getActionCommand())) {
            showRGAut();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].getActionCommand())) {
            showRGAutProj();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT].getActionCommand())) {
            showModifiedAUTDOT();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_RG_DIPLODOCUS].getActionCommand())) {
            showRGDiplodocus();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_SAVED_LOT].getActionCommand())) {
            showSavedRTLOTOS();
        } else if (command.equals(actions[TGUIAction.ACT_VIEW_SAVED_DOT].getActionCommand())) {
            showGGraph();
        } else if (command.equals(actions[TGUIAction.ACT_SCREEN_CAPTURE].getActionCommand())) {
            screenCapture();
        } else if (command.equals(actions[TGUIAction.ACT_TTOOL_WINDOW_CAPTURE].getActionCommand())) {
            windowCapture();
        } else if (command.equals(actions[TGUIAction.ACT_DIAGRAM_CAPTURE].getActionCommand())) {
            diagramCapture();
        } else if (command.equals(actions[TGUIAction.ACT_SVG_DIAGRAM_CAPTURE].getActionCommand())) {
            svgDiagramCapture();
        } else if (command.equals(actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE].getActionCommand())) {
            allDiagramCapture();
        } else if (command.equals(actions[TGUIAction.ACT_SELECTED_CAPTURE].getActionCommand())) {
            selectedCapture();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_DOC].getActionCommand())) {
            generateDocumentation();
        } else if (command.equals(actions[TGUIAction.ACT_GEN_DOC_REQ].getActionCommand())) {
            generateDocumentationReq();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_ATTRIBUTES].getActionCommand())) {
            toggleAttributes();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_DIPLO_ID].getActionCommand())) {
            toggleDiploIDs();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_TEPE_ID].getActionCommand())) {
            toggleTEPEIDs();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_AVATAR_ID].getActionCommand())) {
            toggleAVATARIDs();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_GATES].getActionCommand())) {
            toggleGates();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_SYNCHRO].getActionCommand())) {
            toggleSynchro();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_CHANNELS].getActionCommand())) {
            toggleChannels();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_EVENTS].getActionCommand())) {
            toggleEvents();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_REQUESTS].getActionCommand())) {
            toggleRequests();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_JAVA].getActionCommand())) {
            toggleJava();
        }  else if (command.equals(actions[TGUIAction.ACT_TOGGLE_INTERNAL_COMMENT].getActionCommand())) {
            toggleInternalComment();
        } else if (command.equals(actions[TGUIAction.ACT_TOGGLE_ATTR].getActionCommand())) {
            toggleAttr();
        } else if (command.equals(actions[TGUIAction.ACT_ENHANCE].getActionCommand())) {
            enhanceDiagram();
        } else if (command.equals(actions[TGUIAction.ACT_NC].getActionCommand())) {
            NC();
        } else if (command.equals(actions[TGUIAction.EXTERNAL_ACTION_1].getActionCommand())) {
            executeUserCommand(ConfigurationTTool.ExternalCommand1Host, ConfigurationTTool.ExternalCommand1);
        } else if (command.equals(actions[TGUIAction.EXTERNAL_ACTION_2].getActionCommand())) {
            executeUserCommand(ConfigurationTTool.ExternalCommand2Host, ConfigurationTTool.ExternalCommand2);
        } else if (command.equals(actions[TGUIAction.CONNECTOR_COMMENT].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COMMENT);
        } else if (command.equals(actions[TGUIAction.TCD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.UML_NOTE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UML_NOTE);

            // AVATAR actions
        } else if (command.equals(actions[TGUIAction.ACT_AVATAR_SIM].getActionCommand())) {
            avatarSimulation();
        } else if (command.equals(actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].getActionCommand())) {
            avatarUPPAALVerification();
        } else if (command.equals(actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].getActionCommand())) {
            avatarProVerifVerification();
        } else if (command.equals(actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].getActionCommand())) {
            avatarStaticAnalysis();
        } else if (command.equals(actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].getActionCommand())) {
            avatarExecutableCodeGeneration();

            // AVATAR BD
        } else if (command.equals(actions[TGUIAction.ABD_BLOCK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_BLOCK);
        } else if (command.equals(actions[TGUIAction.ABD_CRYPTOBLOCK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_CRYPTOBLOCK);
        } else if (command.equals(actions[TGUIAction.ABD_DATATYPE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_DATATYPE);
        } else if (command.equals(actions[TGUIAction.ABD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARBD_COMPOSITION_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ABD_PORT_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARBD_PORT_CONNECTOR);

            // AVATAR SMD
        } else if (command.equals(actions[TGUIAction.ASMD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.ASMD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_START_STATE);
        } else if (command.equals(actions[TGUIAction.ASMD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.ASMD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARSMD_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ASMD_SEND_SIGNAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_SEND_SIGNAL);
        } else if (command.equals(actions[TGUIAction.ASMD_RECEIVE_SIGNAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RECEIVE_SIGNAL);
        } else if (command.equals(actions[TGUIAction.ASMD_PARALLEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_PARALLEL);
        } else if (command.equals(actions[TGUIAction.ASMD_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_STATE);
        } else if (command.equals(actions[TGUIAction.ASMD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_CHOICE);
        } else if (command.equals(actions[TGUIAction.ASMD_RANDOM].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RANDOM);
        } else if (command.equals(actions[TGUIAction.ASMD_SET_TIMER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_SET_TIMER);
        } else if (command.equals(actions[TGUIAction.ASMD_RESET_TIMER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RESET_TIMER);
        } else if (command.equals(actions[TGUIAction.ASMD_EXPIRE_TIMER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_EXPIRE_TIMER);

            // AVATAR MAD
        } else if (command.equals(actions[TGUIAction.AMAD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.AMAD_ASSUMPTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_ASSUMPTION);
        } else if (command.equals(actions[TGUIAction.AMAD_DIAGRAM_REFERENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_DIAGRAM_REFERENCE);
        } else if (command.equals(actions[TGUIAction.AMAD_ELEMENT_REFERENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_ELEMENT_REFERENCE);
        } else if (command.equals(actions[TGUIAction.AMAD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_COMPOSITION_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.AMAD_VERSIONING_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_VERSIONING_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.AMAD_IMPACT_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_IMPACT_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.AMAD_MEET_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_MEET_CONNECTOR);

            // AVATAR RD
        } else if (command.equals(actions[TGUIAction.ARD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.ARD_REQUIREMENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARRD_REQUIREMENT);
        } else if (command.equals(actions[TGUIAction.ARD_PROPERTY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARRD_PROPERTY);
        } else if (command.equals(actions[TGUIAction.ARD_DERIVE_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_DERIVE_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ARD_REFINE_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_REFINE_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ARD_VERIFY_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_VERIFY_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ARD_COPY_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_COPY_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ARD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_COMPOSITION_CONNECTOR);

            // AVATAR PD
        } else if (command.equals(actions[TGUIAction.APD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.APD_BLOCK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_BLOCK);
        } else if (command.equals(actions[TGUIAction.APD_LOGICAL_CONSTRAINT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_LOGICAL_CONSTRAINT);
        } else if (command.equals(actions[TGUIAction.APD_TEMPORAL_CONSTRAINT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_TEMPORAL_CONSTRAINT);
        } else if (command.equals(actions[TGUIAction.APD_ATTRIBUTE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ATTRIBUTE);
        } else if (command.equals(actions[TGUIAction.APD_SIGNAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_SIGNAL);
        } else if (command.equals(actions[TGUIAction.APD_ALIAS].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ALIAS);
        } else if (command.equals(actions[TGUIAction.APD_BOOLEQ].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_BOOLEQ);
        } else if (command.equals(actions[TGUIAction.APD_ATTRIBUTE_SETTING].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ATTRIBUTE_SETTING);
        } else if (command.equals(actions[TGUIAction.APD_PROPERTY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_PROPERTY);
        } else if (command.equals(actions[TGUIAction.APD_PROPERTY_RELATION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_PROPERTY_RELATION);
        }

        else if (command.equals(actions[TGUIAction.APD_ATTRIBUTE_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_ATTRIBUTE_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.APD_SIGNAL_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_SIGNAL_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.APD_PROPERTY_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_PROPERTY_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.APD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_COMPOSITION_CONNECTOR);

            // AVATAR CD
        } else if (command.equals(actions[TGUIAction.ACD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.ACD_BLOCK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_BLOCK);
        } else if (command.equals(actions[TGUIAction.ACD_ACTOR_STICKMAN].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_ACTOR_STICKMAN);
        } else if (command.equals(actions[TGUIAction.ACD_ACTOR_BOX].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_ACTOR_BOX);

        } else if (command.equals(actions[TGUIAction.ACD_ASSOCIATION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ACD_ASSOCIATION_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ACD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ACD_COMPOSITION_CONNECTOR);

            // AVATAR AD
        } else if (command.equals(actions[TGUIAction.AAD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.AAD_ASSOCIATION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AAD_ASSOCIATION_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.AAD_START_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_START_STATE);
        } else if (command.equals(actions[TGUIAction.AAD_STOP_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.AAD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_CHOICE);
        } else if (command.equals(actions[TGUIAction.AAD_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_JUNCTION);
        } else if (command.equals(actions[TGUIAction.AAD_PARALLEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_PARALLEL);
        } else if (command.equals(actions[TGUIAction.AAD_ACTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACTION);
        } else if (command.equals(actions[TGUIAction.AAD_ACTIVITY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACTIVITY);
        } else if (command.equals(actions[TGUIAction.AAD_STOP_FLOW].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_STOP_FLOW);
        } else if (command.equals(actions[TGUIAction.AAD_SEND_SIGNAL_ACTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_SEND_SIGNAL_ACTION);
        } else if (command.equals(actions[TGUIAction.AAD_ACCEPT_EVENT_ACTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACCEPT_EVENT_ACTION);
        } else if (command.equals(actions[TGUIAction.AAD_PARTITION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_PARTITION);
        } else if (command.equals(actions[TGUIAction.AAD_ALIGN_PARTITION].getActionCommand())) {
            alignPartitions();

        } else if (command.equals(actions[TGUIAction.TCD_ASSOCIATION].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ASSOCIATION);
        } else if (command.equals(actions[TGUIAction.TCD_CONNECTOR_ATTRIBUTE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ATTRIBUTE);
        } else if (command.equals(actions[TGUIAction.TCD_ASSOCIATION_NAVIGATION].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ASSOCIATION_NAVIGATION);
        } else if (command.equals(actions[TGUIAction.TCD_NEW_TCLASS].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TCLASS);
        } else if (command.equals(actions[TGUIAction.TCD_NEW_TOBJECT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TOBJECT);
        } else if (command.equals(actions[TGUIAction.TCD_NEW_TDATA].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TDATA);
        } else if (command.equals(actions[TGUIAction.TCD_PARALLEL_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_PARALLEL_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TCD_SYNCHRO_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_SYNCHRO_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TCD_INVOCATION_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_INVOCATION_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TCD_SEQUENCE_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_SEQUENCE_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TCD_PREEMPTION_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_PREEMPTION_OPERATOR);
        } else if (command.equals(actions[TGUIAction.AD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.AD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_AD_DIAGRAM);
        } else if (command.equals(actions[TGUIAction.AD_ACTION_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ACTION_STATE);
        } else if (command.equals(actions[TGUIAction.AD_ARRAY_GET].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ARRAY_GET);
        } else if (command.equals(actions[TGUIAction.AD_ARRAY_SET].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ARRAY_SET);
        } else if (command.equals(actions[TGUIAction.AD_PARALLEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_PARALLEL);
        } else if (command.equals(actions[TGUIAction.AD_SEQUENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_SEQUENCE);
        } else if (command.equals(actions[TGUIAction.AD_PREEMPTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_PREEMPTION);
        } else if (command.equals(actions[TGUIAction.AD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_CHOICE);
        } else if (command.equals(actions[TGUIAction.AD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_START_STATE);
        } else if (command.equals(actions[TGUIAction.AD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.AD_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_JUNCTION);
        } else if (command.equals(actions[TGUIAction.AD_DETERMINISTIC_DELAY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_DETERMINISTIC_DELAY);
        } else if (command.equals(actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_NON_DETERMINISTIC_DELAY);
        } else if (command.equals(actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_DELAY_NON_DETERMINISTIC_DELAY);
        } else if (command.equals(actions[TGUIAction.AD_TIME_LIMITED_OFFER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_LIMITED_OFFER);
        } else if (command.equals(actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_LIMITED_OFFER_WITH_LATENCY);
        } else if (command.equals(actions[TGUIAction.AD_TIME_CAPTURE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_CAPTURE);
        } else if (command.equals(actions[TGUIAction.IOD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.IOD_CONNECTOR].getActionCommand())) {
            //TraceManager.addDev("Connector interaction");
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_INTERACTION);
        } else if (command.equals(actions[TGUIAction.IOD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_START_STATE);
        } else if (command.equals(actions[TGUIAction.IOD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.IOD_PARALLEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_PARALLEL);
        } else if (command.equals(actions[TGUIAction.IOD_PREEMPTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_PREEMPTION);
        } else if (command.equals(actions[TGUIAction.IOD_SEQUENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_SEQUENCE);
        } else if (command.equals(actions[TGUIAction.IOD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_CHOICE);
        } else if (command.equals(actions[TGUIAction.IOD_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_JUNCTION);
        } else if (command.equals(actions[TGUIAction.IOD_REF_SD].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_REF_SD);
        } else if (command.equals(actions[TGUIAction.IOD_REF_IOD].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_REF_IOD);
        } else if (command.equals(actions[TGUIAction.SD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.SD_INSTANCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_INSTANCE);
        } else if (command.equals(actions[TGUIAction.SD_CONNECTOR_MESSAGE_SYNC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_SYNC_SD);
        } else if (command.equals(actions[TGUIAction.SD_CONNECTOR_MESSAGE_ASYNC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_ASYNC_SD);
        } else if (command.equals(actions[TGUIAction.SD_ABSOLUTE_TIME_CONSTRAINT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_ABSOLUTE_TIME_CONSTRAINT);
        } else if (command.equals(actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_RELATIVE_TIME_CONSTRAINT);
        } else if (command.equals(actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_RELATIVE_TIME_SD);
        } else if (command.equals(actions[TGUIAction.SD_ACTION_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_ACTION_STATE);
        } else if (command.equals(actions[TGUIAction.SD_GUARD].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_GUARD);
        } else if (command.equals(actions[TGUIAction.SD_TIME_INTERVAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIME_INTERVAL);
        } else if (command.equals(actions[TGUIAction.SD_TIMER_SETTING].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_SETTING);
        } else if (command.equals(actions[TGUIAction.SD_TIMER_EXPIRATION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_EXPIRATION);
        } else if (command.equals(actions[TGUIAction.SD_TIMER_CANCELLATION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_CANCELLATION);
        } else if (command.equals(actions[TGUIAction.SD_COREGION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_COREGION);
        } else if (command.equals(actions[TGUIAction.SD_ALIGN_INSTANCES].getActionCommand())) {
            alignInstances();
        } else if (command.equals(actions[TGUIAction.UCD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.UCD_ACTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_ACTOR);
        } else if (command.equals(actions[TGUIAction.UCD_ACTORBOX].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_ACTORBOX);
        } else if (command.equals(actions[TGUIAction.UCD_USECASE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_USECASE);
        } else if (command.equals(actions[TGUIAction.UCD_BORDER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_BORDER);
        } else if (command.equals(actions[TGUIAction.UCD_CONNECTOR_ACTOR_UC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ACTOR_UCD);
        } else if (command.equals(actions[TGUIAction.UCD_CONNECTOR_INCLUDE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_INCLUDE_UCD);
        } else if (command.equals(actions[TGUIAction.UCD_CONNECTOR_EXTEND].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EXTEND_UCD);
        } else if (command.equals(actions[TGUIAction.UCD_CONNECTOR_SPECIA].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_SPECIA_UCD);
        } else if (command.equals(actions[TGUIAction.TDD_LINK].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_DD);
        } else if (command.equals(actions[TGUIAction.TDD_NODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TDD_NODE);
        } else if (command.equals(actions[TGUIAction.TDD_ARTIFACT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TDD_ARTIFACT);
        } else if (command.equals(actions[TGUIAction.NCDD_LINK].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_NC);
        } else if (command.equals(actions[TGUIAction.NCDD_EQNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_EQNODE);
        } else if (command.equals(actions[TGUIAction.NCDD_SWITCHNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_SWITCHNODE);
        } else if (command.equals(actions[TGUIAction.NCDD_TRAFFIC_ARTIFACT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_TRAFFIC_ARTIFACT);
        } else if (command.equals(actions[TGUIAction.NCDD_ROUTE_ARTIFACT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_ROUTE_ARTIFACT);
        } else if (command.equals(actions[TGUIAction.TMLTD_TASK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_TASK);
        } else if (command.equals(actions[TGUIAction.EBRDD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_START_STATE);
        } else if (command.equals(actions[TGUIAction.EBRDD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.EBRDD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EBRDD);
        } else if (command.equals(actions[TGUIAction.EBRDD_CONNECTOR_ERC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EBRDD_ERC);
        } else if (command.equals(actions[TGUIAction.EBRDD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_CHOICE);
        } else if (command.equals(actions[TGUIAction.EBRDD_ERC].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ERC);
        } else if (command.equals(actions[TGUIAction.EBRDD_ESO].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ESO);
        } else if (command.equals(actions[TGUIAction.EBRDD_ERB].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ERB);
        } else if (command.equals(actions[TGUIAction.EBRDD_SEQUENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_SEQUENCE);
        } else if (command.equals(actions[TGUIAction.EBRDD_ACTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ACTION);
        } else if (command.equals(actions[TGUIAction.EBRDD_FOR_LOOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_FOR_LOOP);
        } else if (command.equals(actions[TGUIAction.EBRDD_VARIABLE_DECLARATION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_VARIABLE_DECLARATION);
        } else if (command.equals(actions[TGUIAction.TMLAD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_START_STATE);
        } else if (command.equals(actions[TGUIAction.TMLAD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.TMLAD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TMLAD);
        } else if (command.equals(actions[TGUIAction.TMLTD_ASSOC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TML_ASSOCIATION_NAV);
        } else if (command.equals(actions[TGUIAction.TMLTD_CHANNEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_CHANNEL_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TMLTD_REQ].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_REQUEST_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TMLTD_EVENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_EVENT_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TMLTD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TML_COMPOSITION_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TMLTD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.TMLCTD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.TMLAD_WRITE_CHANNEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_WRITE_CHANNEL);
        } else if (command.equals(actions[TGUIAction.TMLAD_SEND_REQUEST].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEND_REQUEST);
        } else if (command.equals(actions[TGUIAction.TMLAD_READ_REQUEST_ARG].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_READ_REQUEST_ARG);
        } else if (command.equals(actions[TGUIAction.TMLAD_SEND_EVENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEND_EVENT);
        } else if (command.equals(actions[TGUIAction.TMLAD_WAIT_EVENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_WAIT_EVENT);
        } else if (command.equals(actions[TGUIAction.TMLAD_NOTIFIED_EVENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_NOTIFIED_EVENT);
        } else if (command.equals(actions[TGUIAction.TMLAD_READ_CHANNEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_READ_CHANNEL);
        }  else if (command.equals(actions[TGUIAction.TMLAD_ACTION_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_ACTION_STATE);
        }  else if (command.equals(actions[TGUIAction.TMLAD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_CHOICE);
        } else if (command.equals(actions[TGUIAction.TMLAD_EXECI].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECI);
        } else if (command.equals(actions[TGUIAction.TMLAD_EXECI_INTERVAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECI_INTERVAL);
        } else if (command.equals(actions[TGUIAction.TMLAD_EXECC].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECC);
        } else if (command.equals(actions[TGUIAction.TMLAD_EXECC_INTERVAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECC_INTERVAL);
        } else if (command.equals(actions[TGUIAction.TMLAD_DELAY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_DELAY);
        } else if (command.equals(actions[TGUIAction.TMLAD_INTERVAL_DELAY].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_INTERVAL_DELAY);
        } else if (command.equals(actions[TGUIAction.TMLAD_FOR_LOOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_LOOP);
        } else if (command.equals(actions[TGUIAction.TMLAD_FOR_STATIC_LOOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_STATIC_LOOP);
        } else if (command.equals(actions[TGUIAction.TMLAD_FOR_EVER_LOOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_EVER_LOOP);
        } else if (command.equals(actions[TGUIAction.TMLAD_SEQUENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEQUENCE);
        } else if (command.equals(actions[TGUIAction.TMLAD_UNORDERED_SEQUENCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_UNORDERED_SEQUENCE);
        } else if (command.equals(actions[TGUIAction.TMLAD_SELECT_EVT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SELECT_EVT);
        } else if (command.equals(actions[TGUIAction.TMLAD_RANDOM].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_RANDOM);
        } else if (command.equals(actions[TGUIAction.TMLCTD_CCOMPONENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CCOMPONENT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_CREMOTECOMPONENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CREMOTECOMPONENT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_PCOMPONENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_PCOMPONENT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_RCOMPONENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_RCOMPONENT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_CPORT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CPORT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_FORK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_FORK);
        } else if (command.equals(actions[TGUIAction.TMLCTD_JOIN].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_JOIN);
        } else if (command.equals(actions[TGUIAction.TMLCTD_COPORT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_COPORT);
        } else if (command.equals(actions[TGUIAction.TMLCTD_PORT_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PORT_TMLC);

        } else if (command.equals(actions[TGUIAction.TMLARCHI_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_LINK].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_TMLARCHI);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_CPUNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_CPUNODE);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_BUSNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_BUSNODE);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_CPNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_CPNODE);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_BRIDGENODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_BRIDGENODE);
        }  else if (command.equals(actions[TGUIAction.TMLARCHI_HWANODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_HWANODE);
        }  else if (command.equals(actions[TGUIAction.TMLARCHI_MEMORYNODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_MEMORYNODE);
        }  else if (command.equals(actions[TGUIAction.TMLARCHI_DMANODE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_DMANODE);
        }  else if (command.equals(actions[TGUIAction.TMLARCHI_ARTIFACT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_ARTIFACT);
        } else if (command.equals(actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_COMMUNICATION_ARTIFACT);



            // Communication patterns
        } else if (command.equals(actions[TGUIAction.TMLCP_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.TMLCP_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TMLCP);
        } else if (command.equals(actions[TGUIAction.TMLCP_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_CHOICE);
        } else if (command.equals(actions[TGUIAction.TMLCP_FORK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_FORK);
        } else if (command.equals(actions[TGUIAction.TMLCP_JOIN].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_JOIN);
        } else if (command.equals(actions[TGUIAction.TMLCP_REF_CP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_REF_CP);
        } else if (command.equals(actions[TGUIAction.TMLCP_REF_SD].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_REF_SD);
        } else if (command.equals(actions[TGUIAction.TMLCP_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_START_STATE);
        } else if (command.equals(actions[TGUIAction.TMLCP_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.TMLCP_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_JUNCTION);
        } else if (command.equals(actions[TGUIAction.TMLSD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(actions[TGUIAction.TMLSD_MESSAGE_ASYNC].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_ASYNC_TMLSD);
        } else if (command.equals(actions[TGUIAction.TMLSD_STORAGE_INSTANCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_STORAGE_INSTANCE);
        } else if (command.equals(actions[TGUIAction.TMLSD_CONTROLLER_INSTANCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_CONTROLLER_INSTANCE);
        } else if (command.equals(actions[TGUIAction.TMLSD_TRANSFER_INSTANCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_TRANSFER_INSTANCE);
        } else if (command.equals(actions[TGUIAction.TMLSD_ACTION_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_ACTION_STATE);


            // Attack Tree Diagrams
        } else if (command.equals(actions[TGUIAction.ATD_BLOCK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_BLOCK);
        } else if (command.equals(actions[TGUIAction.ATD_ATTACK].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_ATTACK);
        } else if (command.equals(actions[TGUIAction.ATD_CONSTRAINT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_CONSTRAINT);
        } else if (command.equals(actions[TGUIAction.ATD_COMPOSITION_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ATD_COMPOSITION_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.ATD_ATTACK_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ATD_ATTACK_CONNECTOR);

            // TURTLE-OS
        } else if (command.equals(actions[TGUIAction.TOS_TCLASS].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_TCLASS);
        } else if (command.equals(actions[TGUIAction.TOS_CONNECTOR_ATTRIBUTE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ATTRIBUTE);
        } else if (command.equals(actions[TGUIAction.TOS_ASSOCIATION].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ASSOCIATION);
        } else if (command.equals(actions[TGUIAction.TOS_ASSOCIATION_NAVIGATION].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ASSOCIATION_NAVIGATION);
        } else if (command.equals(actions[TGUIAction.TOS_CALL_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_CALL_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TOS_EVT_OPERATOR].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_EVT_OPERATOR);
        } else if (command.equals(actions[TGUIAction.TOSAD_ACTION_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_ACTION_STATE);
        } else if (command.equals(actions[TGUIAction.TOSAD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_CHOICE);
        } else if (command.equals(actions[TGUIAction.TOSAD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOSAD_CONNECTOR);
        } else if (command.equals(actions[TGUIAction.TOSAD_INT_TIME_INTERVAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_INT_TIME_INTERVAL);
        } else if (command.equals(actions[TGUIAction.TOSAD_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_JUNCTION);
        } else if (command.equals(actions[TGUIAction.TOSAD_START_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_START_STATE);
        } else if (command.equals(actions[TGUIAction.TOSAD_STOP_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.TOSAD_TIME_INTERVAL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_TIME_INTERVAL);


            // Ontologies
        } else if (command.equals(actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM].getActionCommand())) {
            generateOntologyForCurrentDiagram();
        } else if (command.equals(actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS].getActionCommand())) {
            generateOntologyForCurrentSetOfDiagrams();
        } else if (command.equals(actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS].getActionCommand())) {
            generateOntologyForAllDiagrams();

            // Requirement diagrams
        } else if (command.equals(actions[TGUIAction.TREQ_REQUIREMENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_REQUIREMENT);
        } else if (command.equals(actions[TGUIAction.TREQ_OBSERVER].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_OBSERVER);
        } else if (command.equals(actions[TGUIAction.TREQ_EBRDD].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_EBRDD);
        } else if (command.equals(actions[TGUIAction.TREQ_DERIVE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_DERIVE_REQ);
        } else if (command.equals(actions[TGUIAction.TREQ_COPY].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COPY_REQ);
        } else if (command.equals(actions[TGUIAction.TREQ_COMPOSITION].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COMPOSITION_REQ);
        } else if (command.equals(actions[TGUIAction.TREQ_VERIFY].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_VERIFY_REQ);
        } else if (command.equals(actions[TGUIAction.PROSMD_START].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_START_STATE);
        } else if (command.equals(actions[TGUIAction.PROSMD_SENDMSG].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_SENDMSG);
        } else if (command.equals(actions[TGUIAction.PROSMD_GETMSG].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_GETMSG);
        } else if (command.equals(actions[TGUIAction.PROSMD_CHOICE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_CHOICE);
        } else if (command.equals(actions[TGUIAction.PROSMD_STOP].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_STOP_STATE);
        } else if (command.equals(actions[TGUIAction.PROSMD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROSMD);
        } else if (command.equals(actions[TGUIAction.PROSMD_JUNCTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_JUNCTION);
        } else if (command.equals(actions[TGUIAction.PROSMD_SUBMACHINE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_SUBMACHINE);
        } else if (command.equals(actions[TGUIAction.PROSMD_ACTION].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_ACTION);
        } else if (command.equals(actions[TGUIAction.PROSMD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        }  else if (command.equals(actions[TGUIAction.PROSMD_PARALLEL].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_PARALLEL);
        }  else if (command.equals(actions[TGUIAction.PROSMD_STATE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_STATE);
        }  else if (command.equals(actions[TGUIAction.PROCSD_COMPONENT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_COMPONENT);
            //Delegate ports removed, by Solange
            /*
              } else if (command.equals(actions[TGUIAction.PROCSD_DELEGATE_PORT].getActionCommand())) {
              actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_DELEGATE_PORT);
            */
        } else if (command.equals(actions[TGUIAction.PROCSD_IN_PORT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_IN_PORT);
        } else if (command.equals(actions[TGUIAction.PROCSD_OUT_PORT].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_OUT_PORT);
        } else if (command.equals(actions[TGUIAction.PROCSD_EDIT].getActionCommand())) {
            actionOnButton(TGComponentManager.EDIT, -1);
        }  else if (command.equals(actions[TGUIAction.PROCSD_CONNECTOR].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROCSD);
        }
        else if (command.equals(actions[TGUIAction.PROCSD_CONNECTOR_PORT_INTERFACE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE);}
        else if (command.equals(actions[TGUIAction.PROCSD_CONNECTOR_DELEGATE].getActionCommand())) {
            actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_DELEGATE_PROCSD);
        }  else if (command.equals(actions[TGUIAction.PROCSD_INTERFCE].getActionCommand())) {
            actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_INTERFACE);
            // Command for the action created by Solange. Window appears.
        }   else if (command.equals(actions[TGUIAction.PRUEBA_1].getActionCommand())) {
            JOptionPane.showMessageDialog(frame, "In Port: Color CYAN\nOut Port: Color LIGHT GRAY", "Help color of the ports", JOptionPane.INFORMATION_MESSAGE);
        }   else if (command.endsWith(".dot")) {
            viewAutomata(command);
        }
    }

    private  class PopupListener extends MouseAdapter /* popup menus onto tabs */ {
        private MainGUI mgui;
        private JPopupMenu menu;

        private JMenuItem rename, remove, moveRight, moveLeft, newDesign, newAnalysis, newDeployment, newRequirement, newTMLDesign, newTMLComponentDesign, newTMLArchi, newProactiveDesign, newTURTLEOSDesign,
            newNCDesign, sort, clone, newAttackTree, newAVATARBD, newAVATARRequirement, newMAD, newTMLCP, newTMLMethodo;
        private JMenuItem newAVATARAnalysis;

        public PopupListener(MainGUI _mgui) {
            mgui = _mgui;
            createMenu();
        }

        public void mousePressed(MouseEvent e) {
            checkForPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            checkForPopup(e);
        }
        public void mouseClicked(MouseEvent e) {
            checkForPopup(e);
        }

        private void checkForPopup(MouseEvent e) {
            if(e.isPopupTrigger()) {
                Component c = e.getComponent();
                //TraceManager.addDev("e =" + e + " Component=" + c);
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
            newAttackTree = createMenuItem("New AVATAR Attack Tree");
            newRequirement = createMenuItem("New TURTLE Requirement Diagram");
            newTMLMethodo = createMenuItem("New DIPLODOCUS Methodology");
            newTMLDesign = createMenuItem("New DIPLODOCUS Design");
            newTMLComponentDesign = createMenuItem("New Component-based DIPLODOCUS Design");
            newTMLArchi = createMenuItem("New DIPLODOCUS Architecture");
            newTMLCP = createMenuItem("New DIPLODOCUS Communication Pattern");
            newProactiveDesign = createMenuItem("New Proactive Design");
            newTURTLEOSDesign = createMenuItem("New TURTLE-OS Design");
            newNCDesign = createMenuItem("New Network Calculus Design");
            newMAD = createMenuItem("New AVATAR Modeling Assumptions Diagram");
            newAVATARRequirement = createMenuItem("New AVATAR Requirement Diagrams");
            newAVATARAnalysis = createMenuItem("New AVATAR Analysis");
            newAVATARBD = createMenuItem("New AVATAR Design");

            menu = new JPopupMenu("TURTLE analysis, design and deployment / DIPLODOCUS design / Proactive design");
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


            menu.add(newRequirement);

            menu.add(newAnalysis);
            menu.add(newDesign);
            menu.add(newDeployment);

            if (osOn) {
                //TraceManager.addDev("OS is on");
                menu.addSeparator();
                menu.add(newTURTLEOSDesign);
            } else {
                //TraceManager.addDev("OS is off");
            }

            if (proactiveOn) {
                menu.addSeparator();
                menu.add(newProactiveDesign);
            }

            if (systemcOn) {
                menu.addSeparator();
                menu.add(newTMLMethodo);
                menu.add(newTMLDesign);
                menu.add(newTMLComponentDesign);
                menu.add(newTMLCP);
                menu.add(newTMLArchi);
            }

            if (ncOn) {
                menu.addSeparator();
                menu.add(newNCDesign);
            }

            if (avatarOn) {
                menu.addSeparator();
                menu.add(newAttackTree);
                menu.add(newMAD);
                menu.add(newAVATARRequirement);
                menu.add(newAVATARAnalysis);
                menu.add(newAVATARBD);
            }

        }

        private JMenuItem createMenuItem(String s) {
            JMenuItem item = new JMenuItem(s);
            item.setActionCommand(s);
            item.addActionListener(listener);
            return item;
        }

        private void updateMenu(int index) {
            //TraceManager.addDev("UpdateMenu index=" + index);
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

                public void actionPerformed(ActionEvent e) {
                    JMenuItem item = (JMenuItem)e.getSource();
                    String ac = item.getActionCommand();
                    if(ac.equals("Rename")) {
                        mgui.requestRenameTab(mainTabbedPane.getSelectedIndex());
                    } else if (ac.equals("Remove")) {
                        mgui.requestRemoveTab(mainTabbedPane.getSelectedIndex());
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
                        mgui.newAnalysis();
                    } else if (ac.equals("New TURTLE Design")) {
                        mgui.newDesign();
                    } else if (ac.equals("New TURTLE Deployment")) {
                        mgui.newDeployment();
                    } else if (e.getSource() == newAttackTree) {
                        mgui.newAttackTree();
                    } else if (ac.equals("New TURTLE Requirement Diagram")) {
                        mgui.newRequirement();
                    }    else if (e.getSource() == newTMLMethodo) {
                        mgui.newDiplodocusMethodology();
                    } else if (ac.equals("New DIPLODOCUS Design")) {
                        mgui.newTMLDesign();
                    } else if (ac.equals("New Component-based DIPLODOCUS Design")) {
                        mgui.newTMLComponentDesign();
                    } else if (e.getSource() == newTMLCP) {
                        mgui.newTMLCP();
                    } else if (ac.equals("New DIPLODOCUS Architecture")) {
                        mgui.newTMLArchi();
                    } else if (ac.equals("New Proactive Design")) {
                        mgui.newProactiveDesign();
                    } else if (ac.equals("New TURTLE-OS Design")) {
                        mgui.newTURTLEOSDesign();
                    } else if (e.getSource() == newNCDesign) {
                        mgui.newNCDesign();
                    } else if (e.getSource() == newAVATARBD) {
                        mgui.newAvatarBD();
                    } else if (e.getSource() == newAVATARRequirement) {
                        mgui.newAvatarRequirement();
                    } else if (e.getSource() == newMAD) {
                        mgui.newAvatarMADs();
                    } else if (e.getSource() == newAVATARAnalysis) {
                        mgui.newAvatarAnalysis();
                    }
                }
            };
    }


    /**
     * This adapter is constructed to handle mouse over component events.
     */
    private class MouseHandler extends MouseAdapter  {

        private JLabel label;

        /**
         * ctor for the adapter.
         * @param label the JLabel which will recieve value of the
         *              Action.LONG_DESCRIPTION key.
         */
        public MouseHandler(JLabel label)  {
            setLabel(label);
        }

        public void setLabel(JLabel label)  {
            this.label = label;
        }

        public void mouseEntered(MouseEvent evt)  {
            if (evt.getSource() instanceof AbstractButton)  {
                AbstractButton button = (AbstractButton)evt.getSource();
                Action action = button.getAction();
                if (action != null)  {
                    String message = (String)action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }
} // Class MainGUI

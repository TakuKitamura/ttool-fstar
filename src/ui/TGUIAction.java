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
 * Class TGUIAction
 *
 * Creation: 21/12/2003
 * @version 1.1 11/07/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see TGComponent
 */

package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


import javax.swing.*;

import javax.swing.event.EventListenerList;

public class TGUIAction extends AbstractAction {
    // Actions
    public static final int ACT_NEW = 0;
    public static final int ACT_NEW_DESIGN = 100;
    public static final int ACT_NEW_ANALYSIS = 101;
    public static final int ACT_OPEN = 1;
    public static final int ACT_OPEN_LAST = 154;
	public static final int ACT_MERGE = 228;
    public static final int ACT_SAVE = 2;
	
    public static final int ACT_SAVE_AS = 31;
	public static final int ACT_SAVE_TIF = 213;
	public static final int ACT_OPEN_TIF = 214;
    public static final int ACT_QUIT = 3;
    
    public static final int ACT_SAVE_LOTOS = 64;
    public static final int ACT_SAVE_DTA = 65;    
    public static final int ACT_SAVE_RG = 66; 
    public static final int ACT_SAVE_TLSA = 74; 
    public static final int ACT_SAVE_AUT = 67;
    public static final int ACT_SAVE_AUTPROJ = 68;
	public static final int ACT_SAVE_AUTMODIFIED = 232;
    
    public static final int ACT_TURTLE_WEBSITE = 117;
    public static final int ACT_TURTLE_DOCUMENTATION = 118;
	public static final int ACT_DIPLODOCUS_DOCUMENTATION = 242;
    public static final int ACT_ABOUT = 4;
    public static final int ACT_ENHANCE = 160;
    
    
    public static final int TCD_EDIT = 5;
    public static final int TCD_NEW_TCLASS = 6;
    public static final int TCD_NEW_TOBJECT = 50;
    public static final int TCD_NEW_TDATA = 111;
    public static final int TCD_ASSOCIATION = 7;
    public static final int AD_EDIT = 8;
    public static final int AD_CONNECTOR = 9;
    public static final int AD_ACTION_STATE = 10;
    public static final int AD_PARALLEL = 11;
    public static final int AD_SEQUENCE = 51;
    public static final int AD_PREEMPTION = 52;
    public static final int AD_CHOICE = 12;
    public static final int AD_STOP = 13;
    public static final int AD_JUNCTION = 14;
    public static final int AD_DETERMINISTIC_DELAY = 15;
    public static final int AD_NON_DETERMINISTIC_DELAY = 16;
    public static final int AD_DELAY_NON_DETERMINISTIC_DELAY = 49;
    public static final int AD_TIME_LIMITED_OFFER = 17;
    public static final int AD_TIME_LIMITED_OFFER_WITH_LATENCY = 18;
    public static final int TCD_PARALLEL_OPERATOR = 19;
    public static final int TCD_CONNECTOR_ATTRIBUTE = 20;
    public static final int TCD_SEQUENCE_OPERATOR = 21;
    public static final int TCD_PREEMPTION_OPERATOR = 22;
    public static final int TCD_SYNCHRO_OPERATOR = 23;
    public static final int TCD_INVOCATION_OPERATOR = 48;
    public static final int TCD_ASSOCIATION_NAVIGATION = 24;
    public static final int AD_START = 43;
    
    public static final int IOD_EDIT = 77;
    public static final int IOD_CHOICE = 78;
    public static final int IOD_START = 79;
    public static final int IOD_STOP = 80;
    public static final int IOD_PARALLEL = 81;
    public static final int IOD_PREEMPTION = 98;
    public static final int IOD_SEQUENCE = 159;
    public static final int IOD_JUNCTION = 82;
    public static final int IOD_CONNECTOR = 83;
    public static final int IOD_REF_SD = 84;
    public static final int IOD_REF_IOD = 158;
    
    public static final int SD_EDIT = 85;
    public static final int SD_INSTANCE = 86;
    public static final int SD_CONNECTOR_MESSAGE_ASYNC = 87;
    public static final int SD_CONNECTOR_MESSAGE_SYNC = 88;
    public static final int SD_ABSOLUTE_TIME_CONSTRAINT = 89;
    public static final int SD_TIME_INTERVAL = 188;
    public static final int SD_RELATIVE_TIME_CONSTRAINT = 90;
    public static final int SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR = 91;
    public static final int SD_ACTION_STATE = 92;
	public static final int SD_GUARD = 212;
    public static final int SD_TIMER_SETTING = 93;
    public static final int SD_TIMER_EXPIRATION = 94;
    public static final int SD_TIMER_CANCELLATION = 95;
    public static final int SD_COREGION = 96;
    public static final int SD_ALIGN_INSTANCES = 97;
    
    public static final int UCD_EDIT = 104;
    public static final int UCD_ACTOR = 105;
    public static final int UCD_USECASE = 106;
    public static final int UCD_CONNECTOR_ACTOR_UC = 107;
    public static final int UCD_CONNECTOR_INCLUDE = 108;
    public static final int UCD_CONNECTOR_EXTEND = 109;
    public static final int UCD_CONNECTOR_SPECIA = 110;
    public static final int UCD_BORDER = 161;
    
    public static final int TMLTD_EDIT = 127;
    public static final int TMLTD_TASK = 128;
    public static final int TMLTD_CHANNEL = 129;
    public static final int TMLTD_REQ = 130;
    public static final int TMLTD_EVENT = 131;
    public static final int TMLTD_CONNECTOR = 132;
    public static final int TMLTD_ASSOC = 133;
	
	public static final int TMLCTD_EDIT = 233;
    public static final int TMLCTD_CCOMPONENT = 234;
	public static final int TMLCTD_CREMOTECOMPONENT = 246;
	public static final int TMLCTD_CPORT = 237;
	public static final int TMLCTD_PORT_CONNECTOR = 238;
	public static final int TMLCTD_PCOMPONENT = 239;
	public static final int TMLCTD_COPORT = 240;
	
    
    public static final int TMLAD_EDIT = 134;
    public static final int TMLAD_CONNECTOR = 135;
    public static final int TMLAD_ACTION_STATE = 136;
    public static final int TMLAD_CHOICE = 137;
    public static final int TMLAD_STOP = 138;
    public static final int TMLAD_START = 139;
    public static final int TMLAD_JUNCTION = 140;
    public static final int TMLAD_EXECI = 141;
    public static final int TMLAD_EXECI_INTERVAL = 152;
	public static final int TMLAD_EXECC = 243;
    public static final int TMLAD_EXECC_INTERVAL = 244;
	public static final int TMLAD_DELAY = 248;
    public static final int TMLAD_INTERVAL_DELAY = 249;
    public static final int TMLAD_WRITE_CHANNEL = 142;
    public static final int TMLAD_SEND_REQUEST = 143; 
    public static final int TMLAD_SEND_EVENT = 144; 
    public static final int TMLAD_WAIT_EVENT = 145; 
    public static final int TMLAD_NOTIFIED_EVENT = 203;
    public static final int TMLAD_READ_CHANNEL = 146;
    public static final int TMLAD_FOR_LOOP = 147;
	public static final int TMLAD_FOR_STATIC_LOOP = 226;
	public static final int TMLAD_FOR_EVER_LOOP = 255;
    public static final int TMLAD_SEQUENCE = 156;
    public static final int TMLAD_SELECT_EVT = 206;
	public static final int TMLAD_RANDOM = 245;
	
	public static final int TMLARCHI_EDIT = 216;
    public static final int TMLARCHI_LINK = 217;
    public static final int TMLARCHI_CPUNODE = 218;
	public static final int TMLARCHI_BUSNODE = 221;
    public static final int TMLARCHI_ARTIFACT = 219;
	public static final int TMLARCHI_COMMUNICATION_ARTIFACT = 222;
	public static final int TMLARCHI_HWANODE = 223;
	public static final int TMLARCHI_MEMORYNODE = 224;
	public static final int TMLARCHI_BRIDGENODE = 225;
	
	
    // TURTLE-OS
    public static final int TOS_TCLASS = 189;
    public static final int TOS_ASSOCIATION = 190;
    public static final int TOS_ASSOCIATION_NAVIGATION = 191;
    public static final int TOS_CONNECTOR_ATTRIBUTE = 192;
    public static final int TOS_CALL_OPERATOR = 193;
    public static final int TOS_EVT_OPERATOR = 205;
    
    public static final int TOSAD_ACTION_STATE = 194;
    public static final int TOSAD_CONNECTOR = 195;
    public static final int TOSAD_CHOICE = 196;
    public static final int TOSAD_START_STATE = 197;
    public static final int TOSAD_STOP_STATE = 198;
    public static final int TOSAD_JUNCTION = 199;
    public static final int TOSAD_TIME_INTERVAL = 200;
    public static final int TOSAD_INT_TIME_INTERVAL = 201;

    //ProActive State Machine Diagram
    public static final int PROSMD_EDIT = 168;
    public static final int PROSMD_STOP = 169;
    public static final int PROSMD_START = 170;
    public static final int PROSMD_CONNECTOR = 172;
    public static final int PROSMD_SENDMSG = 173;
    public static final int PROSMD_GETMSG = 175;
    public static final int PROSMD_CHOICE = 176;
    public static final int PROSMD_JUNCTION = 183;
    public static final int PROSMD_SUBMACHINE = 184;
    public static final int PROSMD_ACTION = 185;
    public static final int PROSMD_PARALLEL = 186;
    public static final int PROSMD_STATE = 187;    
    
    public static final int PROCSD_EDIT = 171;
    public static final int PROCSD_COMPONENT = 174;
    //Delegate port action removed, by Solange
    //public static final int PROCSD_DELEGATE_PORT = 177;
    public static final int PROCSD_CONNECTOR = 178;
    public static final int PROCSD_CONNECTOR_DELEGATE = 182;
    public static final int PROCSD_INTERFCE=179;
    public static final int PROCSD_IN_PORT = 180;
    public static final int PROCSD_OUT_PORT = 181;
    public static final int PROCSD_CONNECTOR_PORT_INTERFACE = 209;
    
    public static final int TDD_EDIT = 119;
    public static final int TDD_LINK = 120;
    public static final int TDD_NODE = 121;
    public static final int TDD_ARTIFACT = 122;
	
	// NC
	public static final int NCDD_EDIT = 250;
    public static final int NCDD_LINK = 251;
    public static final int NCDD_EQNODE = 252;
	public static final int NCDD_SWITCHNODE = 254;
    public static final int NCDD_TRAFFIC_ARTIFACT = 253;
	public static final int NCDD_ROUTE_ARTIFACT = 256;
	public static final int ACT_NC = 257;
	
	// SysML Requirements
    
    public static final int TREQ_REQUIREMENT = 163;
    public static final int TREQ_OBSERVER = 166;
    public static final int TREQ_DERIVE = 164;
    public static final int TREQ_VERIFY = 165;
	public static final int TREQ_COPY = 258;
	public static final int TREQ_COMPOSITION = 259;
    
    
    public static final int ACT_MODEL_CHECKING = 25;
    public static final int ACT_GEN_RTLOTOS = 27;
    public static final int ACT_GEN_LOTOS = 155;
    public static final int ACT_GEN_UPPAAL = 204;
    public static final int ACT_GEN_JAVA = 112;
    public static final int ACT_SIMU_JAVA = 167;
    public static final int ACT_GEN_SYSTEMC = 148;
	public static final int ACT_GEN_TMLTXT = 215;
    public static final int ACT_GEN_AUT = 157;
    public static final int ACT_GEN_AUTS = 202;
    public static final int ACT_GEN_DESIGN = 103;
    public static final int ACT_CHECKCODE = 28;
    public static final int ACT_SIMULATION = 29;
    public static final int ACT_VALIDATION = 30;
    public static final int ACT_PROJECTION = 54;
	public static final int ACT_GRAPH_MODIFICATION = 230;
    public static final int ACT_BISIMULATION = 69;
	public static final int ACT_BISIMULATION_CADP = 229;
    public static final int ACT_DEADLOCK_SEEKER_AUT = 75;
    public static final int ACT_DEADLOCK_SEEKER_SAVED_AUT = 76;
	
	public static final int ACT_ONECLICK_LOTOS_RG = 210;
	public static final int ACT_ONECLICK_RTLOTOS_RG = 211;
    
    public static final int ACT_VIEW_RTLOTOS = 32;
    public static final int ACT_VIEW_SUGGESTED_DESIGN = 99;
    
    public static final int ACT_BACKWARD = 33;
    public static final int ACT_FORWARD = 34;
    
    public static final int ACT_FIRST_DIAG = 35;
    public static final int ACT_BACK_DIAG = 36;
    public static final int ACT_NEXT_DIAG = 37;
    public static final int ACT_LAST_DIAG = 38;
    
    public static final int ACT_VIEW_JAVA = 113;
    public static final int ACT_VIEW_DTADOT = 39;
    public static final int ACT_VIEW_RGDOT = 40;
    public static final int ACT_VIEW_TLSADOT = 73;
    public static final int ACT_VIEW_RGAUTDOT = 53;
    public static final int ACT_VIEW_STAT_AUT = 70;
    public static final int ACT_VIEW_RGAUTPROJDOT = 55;
	public static final int ACT_VIEW_MODIFIEDAUTDOT = 231;
    public static final int ACT_VIEW_STAT_AUTPROJ = 71;
    public static final int ACT_VIEW_SIM = 41;
    public static final int ACT_VIEW_SIM_CHRONO = 102;
    public static final int ACT_VIEW_SAVED_LOT = 62;
    public static final int ACT_VIEW_SAVED_DOT = 63;
    public static final int ACT_VIEW_STAT_SAVED_AUT = 72;
    public static final int ACT_VIEW_BIRDEYES = 115;
    public static final int ACT_VIEW_BIRDEYES_EMB = 116;
    public static final int ACT_VIEW_WAVE = 153;
    
    public static final int UML_NOTE = 42;
    
    public static final int ACT_CUT = 44;
    public static final int ACT_COPY = 45;
    public static final int ACT_PASTE = 46;
    public static final int ACT_DELETE = 47;
	
	public static final int ACT_ZOOM_MORE = 235;
	public static final int ACT_ZOOM_LESS = 236;
	public static final int ACT_SHOW_ZOOM = 241;
    
    public static final int ACT_IMPORT_LIB = 56;
    public static final int ACT_EXPORT_LIB = 57;
    
    public static final int ACT_SCREEN_CAPTURE = 58;
    public static final int ACT_TTOOL_WINDOW_CAPTURE = 59;
    public static final int ACT_DIAGRAM_CAPTURE = 60;
    public static final int ACT_ALL_DIAGRAM_CAPTURE = 114;
    public static final int ACT_SELECTED_CAPTURE = 61;
	
	public static final int ACT_GEN_DOC = 247;
	public static final int ACT_GEN_DOC_REQ = 260;
    
    public static final int ACT_TOGGLE_ATTRIBUTES = 123;
    public static final int ACT_TOGGLE_GATES = 124;
    public static final int ACT_TOGGLE_SYNCHRO = 125;
    
    public static final int ACT_TOGGLE_CHANNELS = 149;
    public static final int ACT_TOGGLE_EVENTS = 150;
    public static final int ACT_TOGGLE_REQUESTS = 151;
    
    public static final int ACT_TOGGLE_JAVA = 126;
	public static final int ACT_TOGGLE_ATTR = 220;
	
	public static final int ACT_TOGGLE_INTERNAL_COMMENT = 227;
   
    public static final int CONNECTOR_COMMENT = 162;
    
    public static final int EXTERNAL_ACTION_1 = 207;
    public static final int EXTERNAL_ACTION_2 = 208;

    
    //Action for the help button created by Solange
    public static final int PRUEBA_1 = 205;

    
    public static final int NB_ACTION = 261;


    private  static final TAction [] actions = new TAction[NB_ACTION];
    
    private EventListenerList listeners;
    
    public static final String JLF_IMAGE_DIR = "";
    
    public static final String LARGE_ICON = "LargeIcon";
    

    
    public TGUIAction(int id) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return ;
        }
        
        putValue(Action.NAME, actions[id].NAME);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, actions[id].SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, actions[id].LONG_DESCRIPTION);
        //putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
        if (actions[id].MNEMONIC_KEY != 0) {
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].MNEMONIC_KEY, java.awt.event.InputEvent.CTRL_MASK));
        }
        putValue(Action.ACTION_COMMAND_KEY, actions[id].ACTION_COMMAND_KEY);
        
    }
    
    public void setName(int index, String name) {
        actions[index].NAME = name;
        putValue(Action.NAME, actions[index].NAME);
    }
    
    public void init() {
        actions[ACT_NEW] = new TAction("new-command", "New", IconManager.imgic20, IconManager.imgic21, "New", "New TURTLE modeling", 'N');
        actions[ACT_NEW_DESIGN] = new TAction("new-command-design", "New design", IconManager.imgic14, IconManager.imgic14, "New design", "New TURTLE design", 0);
        actions[ACT_NEW_ANALYSIS] = new TAction("new-command-analysis", "New analysis", IconManager.imgic17, IconManager.imgic17, "New analysis", "New TURTLE analysis", 0);
        actions[ACT_OPEN] = new TAction("open-command","Open", IconManager.imgic22, IconManager.imgic23, "Open", "Open an existing TURTLE modeling",'O');
        actions[ACT_OPEN_LAST] = new TAction("openlast-command","Open file: " + ConfigurationTTool.LastOpenFile, IconManager.imgic22, IconManager.imgic23, "Open: " + ConfigurationTTool.LastOpenFile, "Open the lastly saved TURTLE modeling", 0);
        actions[ACT_MERGE] = new TAction("merge-command","Merge", IconManager.imgic22, IconManager.imgic23, "Merge", "Merge the current TURTLE modeling with another one saved in a file ", 0);
		actions[ACT_SAVE] = new TAction("save-command", "Save",IconManager.imgic24, IconManager.imgic25, "Save", "Save an opened or a new TURTLE modeling", 'S');
        actions[ACT_SAVE_TIF] = new TAction("save-tif-command", "Save TIF specification",IconManager.imgic24, IconManager.imgic25, "Save TIF Specification", "Save in TIF a TURTLE modeling", 0);
        actions[ACT_OPEN_TIF] = new TAction("open-tif-command", "Open TIF specification",IconManager.imgic24, IconManager.imgic25, "Open TIF Specification", "Opena TURTLE modeling given in TIF", 0);
        actions[ACT_SAVE_AS] = new TAction("saveas-command", "Save as",IconManager.imgic24, IconManager.imgic25, "Save as", "Save an opened or a new TURTLE modeling under a new name", 0);
        actions[ACT_QUIT] = new TAction("quit-command", "Quit", IconManager.imgic26, IconManager.imgic27, "Quit",  "Quit the TURTLE editor", 'Q');
        
        actions[ACT_SAVE_LOTOS] = new TAction("save-last-lotos", "Save last RT-LOTOS specification",IconManager.imgic24, IconManager.imgic25, "Save last RT-LOTOS specification", "Save the lastest automatically generated RT-LOTOS specification", 0);
        actions[ACT_SAVE_DTA] = new TAction("save-last-dta", "Save last DTA",IconManager.imgic24, IconManager.imgic25, "Save last DTA", "Save the lastest built DTA", 0);
        actions[ACT_SAVE_RG] = new TAction("save-last-rg", "Save last RG",IconManager.imgic24, IconManager.imgic25, "Save last RG", "Save the lastest built RG", 0);
        actions[ACT_SAVE_TLSA] = new TAction("save-last-tlsa", "Save last TLSA",IconManager.imgic24, IconManager.imgic25, "Save last TLSA", "Save the lastest generated TLSA", 0);
        actions[ACT_SAVE_AUT] = new TAction("save-last-rg-aut", "Save last RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last RG (AUT format)", "Save the lastest built RG (AUT Format)", 0);
        actions[ACT_SAVE_AUTPROJ] = new TAction("save-last-rg-autproj", "Save last minimized RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last minimized RG (AUT format)", "Save the lastest minimized RG (AUT Format)", 0);
        actions[ACT_SAVE_AUTMODIFIED] = new TAction("save-last-rg-modified", "Save last modified RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last modified RG (AUT format)", "Save the lastest modified RG (AUT Format)", 0);
              
        actions[ACT_IMPORT_LIB] = new TAction("import-lib-command", "Import library", IconManager.imgic338, IconManager.imgic339, "Import library",  "Import a TURTLE library", 'E');
        actions[ACT_EXPORT_LIB] = new TAction("export-lib-command", "Export library", IconManager.imgic340, IconManager.imgic341, "Export library",  "Export a TURTLE library", 'I');
        
        actions[ACT_CUT] = new TAction("cut-command", "Cut",IconManager.imgic330, IconManager.imgic331, "Cut", "Cut selected components", 'X');
        actions[ACT_COPY] = new TAction("copy-command", "Copy", IconManager.imgic332, IconManager.imgic333, "Copy", "Copy selected components", 'C');
        actions[ACT_PASTE] = new TAction("paste-command", "Paste",IconManager.imgic334, IconManager.imgic335, "Paste", "Paste - if possible - previously cut / copied components at the minimal position of the currently opened diagram", 'V');
        actions[ACT_DELETE] = new TAction("delete-command", "Undo", IconManager.imgic336, IconManager.imgic337, "Delete", "Delete selected components", 'D');
        
		actions[ACT_ZOOM_MORE] = new TAction("zoommore-command", "Zoom +", IconManager.imgic316, IconManager.imgic317, "Zoom +", "Zoom +", '0');
        actions[ACT_ZOOM_LESS] = new TAction("zoomless-command", "Zoom -", IconManager.imgic314, IconManager.imgic315, "Zoom -", "Zoom -", '0');
        actions[ACT_SHOW_ZOOM] = new TAction("showzoom-command", "100%", null, null, "Zoom value", "Zoom value", '0');
        
        actions[ACT_BACKWARD] = new TAction("backward-command", "Undo",IconManager.imgic40, IconManager.imgic41, "Undo", "One operation before", 'Z');
        actions[ACT_FORWARD] = new TAction("forward-command", "Redo", IconManager.imgic42, IconManager.imgic43, "Redo", "One operation ahead", 'Y');
        
        actions[ACT_FIRST_DIAG] = new TAction("firstdiag-command", "First diagram",IconManager.imgic44, IconManager.imgic45, "First diagram", "Open the first diagram", '1');
        actions[ACT_BACK_DIAG] = new TAction("backdiag-command", "Previous diagram", IconManager.imgic46, IconManager.imgic47, "Previous diagram", "Open the previous diagram", '2');
        actions[ACT_NEXT_DIAG] = new TAction("nextdiag-command", "Next diagram",IconManager.imgic48, IconManager.imgic49, "Next diagram", "Open the next diagram", '3');
        actions[ACT_LAST_DIAG] = new TAction("lastdiag-command", "Last diagram", IconManager.imgic50, IconManager.imgic51, "Last diagram", "Open the last diagram", '4');
        
        actions[ACT_TOGGLE_ATTRIBUTES] = new TAction("toggle-att-command", "Show / hide Attributes",IconManager.imgic132, IconManager.imgic132, "Show / hide Attributes", "Show / hide Attributes", '0');
        actions[ACT_TOGGLE_GATES] = new TAction("toggle-gate-command", "Show / hide Gates",IconManager.imgic134, IconManager.imgic134, "Show / hide Gates", "Show / hide Gates", '0');
        actions[ACT_TOGGLE_SYNCHRO] = new TAction("toggle-synchro-command", "Show / hide Synchronization gates",IconManager.imgic136, IconManager.imgic136, "Show / hide Synchronization gates", "Show / hide Synchronization gates", '0');
        
        actions[ACT_TOGGLE_JAVA] = new TAction("toggle-java-command", "Show / hide Java code", IconManager.imgic138, IconManager.imgic138, "Show / hide Java code", "Show / hide Java code", '0');
        actions[ACT_TOGGLE_ATTR] = new TAction("toggle-attr-command", "Show / hide attributes (OFF -> partial -> Full)", IconManager.imgic138, IconManager.imgic138, "Show / hide element attributes (OFF -> Partial -> Full)", "Show / hide attributes (OFF -> Partial -> Full)", '0');
        
		actions[ACT_TOGGLE_INTERNAL_COMMENT] = new TAction("toggle-internal-comment-command", "Show / hide (OFF -> partial -> Full)", IconManager.imgic138, IconManager.imgic138, "Show / hide internal comments (OFF -> partial -> Full)", "Show / hide internal comments (OFF -> partial -> Full)", '0');
        
        actions[ACT_MODEL_CHECKING] = new TAction("checking-command", "Syntax analysis", IconManager.imgic36, IconManager.imgic37, "Syntax analysis",  "Checks that all diagrams follows the TURTLE's syntax", KeyEvent.VK_F5);
        actions[ACT_GEN_RTLOTOS] = new TAction("gen_rtlotos-command", "Generate RT-LOTOS", IconManager.imgic34, IconManager.imgic35, "Generate RT-LOTOS specification",  "Generates a RT-LOTOS specification from TURTLE diagrams", KeyEvent.VK_F6);
        actions[ACT_GEN_LOTOS] = new TAction("gen_lotos-command", "Generate LOTOS", IconManager.imgic34, IconManager.imgic35, "Generate LOTOS specification",  "Generates a LOTOS specification from TURTLE diagrams", '0');
        actions[ACT_ONECLICK_LOTOS_RG] = new TAction("gen_rglotos-command", "Generate LOTOS-based RG", IconManager.imgic342, IconManager.imgic342, "Generate LOTOS-based RG ",  "Generates a LOTOS-based RG  from TURTLE diagrams", '0');
        actions[ACT_ONECLICK_RTLOTOS_RG] = new TAction("gen_rgrtlotos-command", "Generate RT-LOTOS-based RG", IconManager.imgic342, IconManager.imgic342, "Generate RT-LOTOS-based RG ",  "Generates a RT-LOTOS-based RG  from TURTLE diagrams", '0');
        actions[ACT_GEN_UPPAAL] = new TAction("gen_uppaal-command", "Generate UPPAAL", IconManager.imgic34, IconManager.imgic35, "Generate UPPAAL specification",  "Generates an UPPAAL specification from DIPLODOCUS diagrams", '0');
        actions[ACT_GEN_JAVA] = new TAction("gen_java-command", "Generate JAVA", IconManager.imgic38, IconManager.imgic39, "Generate JAVA",  "Generates Java code from TURTLE diagrams", 0);
        actions[ACT_SIMU_JAVA] = new TAction("gen_simujava-command", "Java-based simulation", IconManager.imgic38, IconManager.imgic39, "JAVA-based simualtion",  "Simulate diagrams using Java language", 0);
        actions[ACT_GEN_SYSTEMC] = new TAction("gen_systemc-command", "Generate SystemC", IconManager.imgic61, IconManager.imgic61, "Generate SystemC",  "Generates SystemC code from TML Design diagrams", 0);
        actions[ACT_GEN_TMLTXT] = new TAction("gen_tmltxt-command", "Generate TML / TMAP in text format", IconManager.imgic66, IconManager.imgic66, "Generate TML / TMAP in text format",  "Generates TML / TMAP code in text format from TML Design diagrams", 0);
        actions[ACT_GEN_AUT] = new TAction("gen_aut-command", "Generate automata", IconManager.imgic64, IconManager.imgic64, "Generate automata",  "Generates automata from TML Design diagrams", 0);
        actions[ACT_GEN_AUTS] = new TAction("gen_auts-command", "Generate automata via LOTOS", IconManager.imgic64, IconManager.imgic64, "Generate automata via LOTOS",  "Generates automata from TML Design diagrams, using LOTOS", 0);
        actions[ACT_GEN_DESIGN] = new TAction("gen_design-command", "Generate Design", IconManager.imgic58, IconManager.imgic59, "Generate Design from analysis",  "Generates a TURTLE design from a TURTLE analysis", 0);
        actions[ACT_CHECKCODE] = new TAction("gen_checkcode-command", "Check syntax of formal code", IconManager.imgic312, IconManager.imgic312, "Check syntax of formal code",  "Gives as input to the corresponding tool the lastly generated formal specification", 0);
        actions[ACT_SIMULATION] = new TAction("gen_sim-command", "Run intensive simulation", IconManager.imgic312, IconManager.imgic312, "Run simulation",  "Generate a simulation trace for the lastly generated formal specification", KeyEvent.VK_F7);
        actions[ACT_VALIDATION] = new TAction("gen_val-command", "Run formal validation", IconManager.imgic310, IconManager.imgic310, "Run validation",  "Generate an automata (DTA, RG) from the lastly generated formal specification", KeyEvent.VK_F8);
        actions[ACT_PROJECTION] = new TAction("proj_val-command", "Make minimization", IconManager.imgic310, IconManager.imgic310, "Make minimization",  "Minimize a RG using Aldebaran", KeyEvent.VK_F9);
        actions[ACT_GRAPH_MODIFICATION] = new TAction("graph_modification-command", "Modify minimized graph", IconManager.imgic310, IconManager.imgic310, "Modify minimized graph",  "Modify minimized graph according to a selected function", 0);
        actions[ACT_BISIMULATION] = new TAction("bisimulation-command", "Make bisimulation (Aldebaran)", IconManager.imgic310, IconManager.imgic310, "Make bisimulation (Aldebaran)",  "Perform bisimulations using Aldebaran", KeyEvent.VK_F10);
        actions[ACT_BISIMULATION_CADP] = new TAction("bisimulation-cadp-command", "Make bisimulation (BISIMULATOR)", IconManager.imgic310, IconManager.imgic310, "Make bisimulation (BISIMULATOR)",  "Perform bisimulations using BISIMULATOR", KeyEvent.VK_F10);
        actions[ACT_DEADLOCK_SEEKER_AUT] = new TAction("deadlockseeker-command", "Search for Deadlocks on last AUT graph", IconManager.imgic310, IconManager.imgic310, "Search for deadlocks (last AUT graph)",  "Display all states with no exit transitions (potential deadlocks on lastly generated AUT graph)", 0);
        actions[ACT_DEADLOCK_SEEKER_SAVED_AUT] = new TAction("deadlockseekersavedaut-command", "Search for Deadlocks on saved AUT graph", IconManager.imgic310, IconManager.imgic310, "Search for deadlocks (saved AUT graph)",  "Display all states with no exit transitions (potential deadlocks on previously saved AUT graph)", 0);
        actions[ACT_VIEW_STAT_AUT] = new TAction("viewstataut-command", "Analysis (last AUT graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (last AUT graph)",  "Analysis on the last generated reachability graph generated in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_STAT_AUTPROJ] = new TAction("viewstatautproj-command", "Analysis (last minimized AUT graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (last minimized AUT graph)",  "Analysis on the last minimized reachability graph  in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_STAT_SAVED_AUT] = new TAction("viewstatsavedautproj-command", "Analysis (saved AUT graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (saved AUT graph)",  "Analysis on a graph saved in AUT (Ald�baran) format", 0);
        
        
        actions[ACT_VIEW_JAVA] = new TAction("view-java", "Display Java code", IconManager.imgic38, IconManager.imgic39, "Display Java code",  "Display the java code of the pointed component", 0);
        actions[ACT_VIEW_RTLOTOS] = new TAction("view-rtlotos", "Show last formal specification", IconManager.imgic302, IconManager.imgic302, "Show formal specification",  "Display the lastest generated formal specification", 0);
        actions[ACT_VIEW_SUGGESTED_DESIGN] = new TAction("view-suggested-design", "Show last suggested design", IconManager.imgic302, IconManager.imgic302, "Show suggested design",  "Display the lastest generated suggested design", 0);
        actions[ACT_VIEW_SIM] = new TAction("view-sim", "Show last simulation trace (timing order)", IconManager.imgic318, IconManager.imgic319, "Show last simulation trace (timing order)",  "Display the lastest generated simulation trace in function of action time", 0);
        actions[ACT_VIEW_SIM_CHRONO] = new TAction("view-sim-chrono", "Show last simulation trace (chronological order)", IconManager.imgic318, IconManager.imgic319, "Show last simulation trace (chronological order)",  "Display the lastest generated simulation trace in function of action chronological order", 0);
        actions[ACT_VIEW_DTADOT] = new TAction("view-dtadot", "Show last DTA", IconManager.imgic306, IconManager.imgic306, "Show DTA",  "Display the lastest generated DTA", 0);
        actions[ACT_VIEW_RGDOT] = new TAction("view-rgdot", "Show last RG", IconManager.imgic308, IconManager.imgic308, "Show RG",  "Display the lastest generated RG", 0);
        actions[ACT_VIEW_TLSADOT] = new TAction("view-tlsadot", "Show last TLSA", IconManager.imgic308, IconManager.imgic308, "Show TLSA",  "Display the lastest generated TLSA", 0);
        actions[ACT_VIEW_RGAUTDOT] = new TAction("view-rgdot-aut", "Show last RG (AUT format)", IconManager.imgic308, IconManager.imgic308, "Show RG (aldebaran)",  "Display the lastest generated RG (Aldebaran format)", 0);
        actions[ACT_VIEW_RGAUTPROJDOT] = new TAction("view-rgdot-aut-proj", "View minimized RG", IconManager.imgic308, IconManager.imgic308, "View minimized RG",  "Display the lastest minimized RG", 0);
        actions[ACT_VIEW_MODIFIEDAUTDOT] = new TAction("view-modified-aut-dot", "View modified RG", IconManager.imgic308, IconManager.imgic308, "View modified RG",  "Display the lastest modified RG", 0);
        actions[ACT_VIEW_SAVED_LOT] = new TAction("view-saved-lot", "View RT-LOTOS specification", IconManager.imgic308, IconManager.imgic308, "View RT-LOTOS specification",  "Display a RT-LOTOS specification previously saved", 0);
        actions[ACT_VIEW_SAVED_DOT] = new TAction("view-saved-dot", "View saved graph", IconManager.imgic308, IconManager.imgic308, "View saved graph",  "Display a previously saved graph (DTA, RG, AUT)", 0);
        actions[ACT_VIEW_BIRDEYES] = new TAction("view-birdeyes", "Show external bird eyes view", IconManager.imgic56, IconManager.imgic57, "Show external bird eyes view",  "Bird eyes view of the diagram under edition", 0);
        actions[ACT_VIEW_BIRDEYES_EMB] = new TAction("view-birdeyes-emb", "Hide / Show embedded bird eyes view", IconManager.imgic56, IconManager.imgic57, "Hide / Show embedded bird eyes view",  "Embedded bird eyes view of the diagram under edition", 0);
        actions[ACT_VIEW_WAVE] = new TAction("view-wave", "View wave", IconManager.imgic56, IconManager.imgic57, "View wave",  "Use gtkwave to display various signals of the lastly performed simulation", 0);
        
        
        actions[ACT_SCREEN_CAPTURE] = new TAction("screen-capture", "Screen capture", IconManager.imgic338, IconManager.imgic339, "Screen capture",  "Capture the screen", 0);
        actions[ACT_TTOOL_WINDOW_CAPTURE] = new TAction("tool-window-capture", "Main window capture", IconManager.imgic338, IconManager.imgic339, "Main window capture",  "Capture the main window", 0);
        actions[ACT_DIAGRAM_CAPTURE] = new TAction("diagram-capture", "Diagram capture", IconManager.imgic338, IconManager.imgic339, "Diagram capture",  "Capture the currenlty opened diagram", 0);
        actions[ACT_ALL_DIAGRAM_CAPTURE] = new TAction("all-diagram-capture", "All diagram capture", IconManager.imgic338, IconManager.imgic339, "All diagram capture",  "Capture the currenlty opened set of diagram (analysis, design, etc.)", 0);
        actions[ACT_SELECTED_CAPTURE] = new TAction("selected-capture", "Capture of selected components", IconManager.imgic338, IconManager.imgic339, "Capture of selected components",  "Capture the selected components of the currently opened diagram", 0);
        actions[ACT_GEN_DOC] = new TAction("gen-doc", "Documentation generation", IconManager.imgic28, IconManager.imgic28, "Documentation generation",  "Documentation may be automatically generated, in html format, from edited diagrams", 0);
        actions[ACT_GEN_DOC_REQ] = new TAction("gen-doc-req", "SysML requirements documentation", IconManager.imgic28, IconManager.imgic28, "SysML requirements documentation",  "SysML requirements documentation is displayed in an array, and may be further automatically generated in html format", 0);
        
        actions[ACT_ABOUT] = new TAction("about-command", "About", IconManager.imgic30, IconManager.imgic31, "About", "TURTLE Toolkit information", 0);
        actions[ACT_TURTLE_WEBSITE] = new TAction("turtle-website-command", "TURTLE's website", IconManager.imgic30, IconManager.imgic31, "TURTLE's website", "Various information on the TURTLE profile", 0);
        actions[ACT_TURTLE_DOCUMENTATION] = new TAction("turtle-docu-command", "TURTLE online help", IconManager.imgic30, IconManager.imgic31, "TURTLE online help", "TURTLE online help", 0);
        actions[ACT_DIPLODOCUS_DOCUMENTATION] = new TAction("diplo-docu-command", "DIPLODOCUS online help", IconManager.imgic30, IconManager.imgic31, "DIPLODOCUS online help", "DIPLODOCUS online help", 0);
        
        actions[ACT_ENHANCE] = new TAction("enhance-command", "Enhance", IconManager.imgic28, IconManager.imgic29, "Enhance", "Automatically enhance diagram", 0);
        
        actions[CONNECTOR_COMMENT] = new TAction("add-comment-connector", "Add Comment connector", IconManager.imgic65, IconManager.imgic65, "Comment connector", "Add a Connector between a UML note and a component in the currently opened diagram", 0);
        
        //for the new action created by Solange
        actions[PRUEBA_1] = new TAction("Help for color of the ports", "Help for color of the ports", IconManager.imgic2111, IconManager.imgic2111, "Help for color of the ports", "Help for color of the ports",0);
        
        actions[TCD_EDIT] = new TAction("edit-class-diagram", "Edit class diagram", IconManager.imgic100, IconManager.imgic101, "Edit Class Diagram", "Make it possible to edit the currently opened class diagram", 0);
        actions[TCD_NEW_TCLASS] = new TAction("add-tclass", "Add Tclass", IconManager.imgic104, IconManager.imgic104, "Tclass", "Add a TClass to the currently opened class diagram", 0);
        actions[TCD_NEW_TOBJECT] = new TAction("add-tobject", "Add Tobject", IconManager.imgic128, IconManager.imgic128, "Tobject", "Add a TObject (= instance of a TClass) to the currently opened class diagram", 0);
        actions[TCD_NEW_TDATA] = new TAction("add-tdata", "Add Tdata", IconManager.imgic130, IconManager.imgic130, "Tdata", "Add a Tdata (= TURTLE data) to the currently opened class diagram", 0);
        actions[TCD_ASSOCIATION] = new TAction("add-association", "Add association", IconManager.imgic102, IconManager.imgic102, "Association", "Add an association between two tclasses of the currently opened class diagram", 0);
        actions[AD_EDIT] = new TAction("edit-activity-diagram", "Edit activity diagram", IconManager.imgic200, IconManager.imgic201, "Edit Activity Diagram", "Make it possible to edit the currently opened activity diagram", 0);
        actions[AD_CONNECTOR] = new TAction("add-ad-connector", "Add AD Connector", IconManager.imgic202, IconManager.imgic202, "ADConnector", "Add a Connector between two components of the currently opnened activity diagram", 0);
        actions[AD_ACTION_STATE] = new TAction("add-action-state", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. synchronization on gate, variable modification, process spawning) to the currently opened activity diagram", 0);
        actions[AD_PARALLEL] = new TAction("add-parallel", "Add Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel or synchro operator to the currently opened activity diagram", 0);
        actions[AD_SEQUENCE] = new TAction("add-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened activity diagram", 0);
        actions[AD_PREEMPTION] = new TAction("add-preemption", "Add Preemption", IconManager.imgic228, IconManager.imgic228, "Preemption", "Add a preemption operator to the currently opened activity diagram", 0);
        actions[AD_CHOICE] = new TAction("add-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened activity diagram", 0);
        actions[AD_START] = new TAction("add-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened activity diagram", 0);
        actions[AD_STOP] = new TAction("add-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened activity diagram", 0);
        actions[AD_JUNCTION] = new TAction("add-juunction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened activity diagram", 0);
        actions[AD_DETERMINISTIC_DELAY] = new TAction("add-deterministic-delay", "Add Deterministic Delay", IconManager.imgic214, IconManager.imgic214, "Deterministic Delay", "Add a deterministic delay to the currently opened activity diagram", 0);
        actions[AD_NON_DETERMINISTIC_DELAY] = new TAction("add-non-deterministic-delay", "Add Non-Deterministic Delay", IconManager.imgic216, IconManager.imgic216, "Non-Deterministic Delay", "Add a non-deterministic delay to the currently opened activity diagram", 0);
        actions[AD_DELAY_NON_DETERMINISTIC_DELAY] = new TAction("add-time-interval", "Add Time Interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened activity diagram", 0);
        actions[AD_TIME_LIMITED_OFFER] = new TAction("add-time-limited-offer", "Add Time-Limited offer", IconManager.imgic218, IconManager.imgic218, "Time-Limited Offer", "Add a time-limited offer to the currently opened activity diagram", 0);
        actions[AD_TIME_LIMITED_OFFER_WITH_LATENCY] = new TAction("add-time-limited-offer-with-latency", "Add Time-Limited offer with a non-deterministic delay", IconManager.imgic220, IconManager.imgic220, "Time-Limited Offer with non-deterministic delay", "Adds a time-limited offer, beginning with a non-deterministic delay, to the currently opened activity diagram", 0);
        actions[TCD_PARALLEL_OPERATOR] = new TAction("add-parallel-operator", "Add Parallel composition operator", IconManager.imgic112, IconManager.imgic112, "Parallel composition operator", "Add a Parallel composition operator to the currently opened class diagram", 0);
        actions[TCD_CONNECTOR_ATTRIBUTE] = new TAction("add-attribute-connector", "Connects an association to a composition operator", IconManager.imgic108, IconManager.imgic108, "Attributes an association with a composition operator", "Adds a connector between an association and a composition operator to the currently opened class diagram", 0);
        actions[TCD_SEQUENCE_OPERATOR] = new TAction("add-sequence-operator", "Add Sequence composition operator", IconManager.imgic114, IconManager.imgic114, "Sequence composition operator", "Add a Sequence composition operator to the currently opened class diagram", 0);
        actions[TCD_PREEMPTION_OPERATOR] = new TAction("add-preemption-operator", "Add Preemption composition operator", IconManager.imgic116, IconManager.imgic116, "Preemption composition operator", "Add a Preemption composition operator to the currently opened class diagram", 0);
        actions[TCD_SYNCHRO_OPERATOR] = new TAction("add-synchro-operator", "Add Synchro composition operator", IconManager.imgic110, IconManager.imgic110, "Synchro composition operator", "Adds a Synchro composition operator to the currently opened class diagram", 0);
        actions[TCD_INVOCATION_OPERATOR] = new TAction("add-invocation-operator", "Add Invocation composition operator", IconManager.imgic126, IconManager.imgic126, "Invocation composition operator", "Adds an invocation composition operator to the currently opened class diagram", 0);
        actions[TCD_ASSOCIATION_NAVIGATION] = new TAction("add-association-navigation", "Add association with navigation", IconManager.imgic106, IconManager.imgic108, "Association with navigation", "Adds an association with a navigation indication between two tclasses of the currently opened class diagram", 0);
        actions[UML_NOTE] = new TAction("edit-note", "Add a note to a diagram", IconManager.imgic320, IconManager.imgic321, "Add a comment", "Add a comment to the currently opened TURTLE diagram", 0);
        
        
        actions[IOD_EDIT] = new TAction("edit-io-diagram", "Edit interaction overview diagram", IconManager.imgic100, IconManager.imgic101, "Edit interaction overview diagram", "Make it possible to edit the currently opened interaction overview diagram", 0);
        actions[IOD_CONNECTOR] = new TAction("add-iod-connector", "Add IOD Connector", IconManager.imgic202, IconManager.imgic202, "Connector", "Add a Connector between two components of the currently opened interaction overview diagram", 0);
        actions[IOD_REF_SD] = new TAction("add-ref-sd", "Add reference to a SD", IconManager.imgic400, IconManager.imgic400, "Reference to a SD", "Add a reference to a sequence diagram", 0);
        actions[IOD_REF_IOD] = new TAction("add-ref-iod", "Add reference to an IOD", IconManager.imgic400, IconManager.imgic400, "Reference to an IOD", "Add a reference to an interaction overview diagram", 0);
        actions[IOD_PARALLEL] = new TAction("add-iod-parallel", "Add Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel or synchro operator to the currently opened interaction overview diagram", 0);
        //actions[AD_SEQUENCE] = new TAction("add-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened activity diagram", 0);
        actions[IOD_PREEMPTION] = new TAction("add-iod-preemption", "Add Preemption", IconManager.imgic402, IconManager.imgic402, "Preemption", "Add a preemption operator to the currently opened interaction overview diagram", 0);
        actions[IOD_SEQUENCE] = new TAction("add-iod-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened interaction overview diagram", 0);
        actions[IOD_CHOICE] = new TAction("add-iod-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened interaction overview diagram", 0);
        actions[IOD_START] = new TAction("add-iod-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened interaction overview diagram", 0);
        actions[IOD_STOP] = new TAction("add-iod-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened interaction overview diagram", 0);
        actions[IOD_JUNCTION] = new TAction("add-iod-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened interaction overview diagram", 0);
        
        actions[SD_EDIT] = new TAction("edit-sd-diagram", "Edit sequence diagram", IconManager.imgic100, IconManager.imgic101, "Edit sequence diagram", "Make it possible to edit the currently opened sequence diagram", 0);
        actions[SD_INSTANCE] = new TAction("add-sd-instance", "Add an instance", IconManager.imgic500, IconManager.imgic500, "Instance", "Add a new instance  to the currently opened sequence diagram", 0);
        actions[SD_CONNECTOR_MESSAGE_ASYNC] = new TAction("add-sd-connector-message-async", "Add asynchronous message", IconManager.imgic504, IconManager.imgic504, "Asynchronous message", "Add an asynchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SD_CONNECTOR_MESSAGE_SYNC] = new TAction("add-sd-connector-message-sync", "Add synchronous message", IconManager.imgic502, IconManager.imgic502, "Synchronous message", "Add an synchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SD_ABSOLUTE_TIME_CONSTRAINT] = new TAction("add-sd-atc", "Absolute time constraint", IconManager.imgic506, IconManager.imgic506, "Absolute time constraint", "Add an absolute time constraint to the currently opened sequence diagram", 0);
        actions[SD_RELATIVE_TIME_CONSTRAINT] = new TAction("add-sd-rtc", "Relative time constraint", IconManager.imgic508, IconManager.imgic508, "Relative time constraint", "Add a relative time constraint to the currently opened sequence diagram", 0);
        actions[SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR] = new TAction("add-sd-rtc-c", "Connect two relative time constraint lines", IconManager.imgic510, IconManager.imgic510, "Relative time constraint connector", "Add a connection between two relative time constraint lines of the currently opened sequence diagram", 0);
        actions[SD_ACTION_STATE] = new TAction("add-sd-action-state", "Action state", IconManager.imgic512, IconManager.imgic512, "Action state", "Add an action state to the currently opened sequence diagram", 0);
        actions[SD_GUARD] = new TAction("add-sd-guard", "Guard", IconManager.imgic524, IconManager.imgic524, "Guard", "Add a guard (non-deterministic, else, end) to the currently opened sequence diagram", 0);
        actions[SD_TIMER_SETTING] = new TAction("add-sd-timer-set-state", "Timer setting", IconManager.imgic514, IconManager.imgic514, "Timer setting", "Add a timer setting to the currently opened sequence diagram", 0);
        actions[SD_TIME_INTERVAL] = new TAction("add-sd-time-interval", "Time interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened sequence diagram", 0);
        actions[SD_TIMER_EXPIRATION] = new TAction("add-sd-timer-expiration-state", "Timer expiration", IconManager.imgic516, IconManager.imgic516, "Timer expiration", "Add a timer expiration to the currently opened sequence diagram", 0);
        actions[SD_TIMER_CANCELLATION] = new TAction("add-sd-timer-cancellation-state", "Timer cancellation", IconManager.imgic518, IconManager.imgic518, "Timer cancellation", "Add a timer cancellation to the currently opened sequence diagram", 0);
        actions[SD_COREGION] = new TAction("add-sd-coregion", "Coregion", IconManager.imgic520, IconManager.imgic520, "Coregion", "Add a coregion to the currently opened sequence diagram", 0);
        actions[SD_ALIGN_INSTANCES] = new TAction("add-sd-align_instances", "Align instances", IconManager.imgic522, IconManager.imgic522, "Align instances", "align instances of the currently opened sequence diagram", 0);
        
        actions[UCD_EDIT] = new TAction("edit-ucd-diagram", "Edit use case diagram", IconManager.imgic100, IconManager.imgic101, "Edit use case diagram", "Make it possible to edit the currently opened use case diagram", 0);
        actions[UCD_ACTOR] = new TAction("add-actor", "Add an actor", IconManager.imgic600, IconManager.imgic600, "Actor", "Add an actor to the currently opened use case diagram", 0);
        actions[UCD_USECASE] = new TAction("add-usecase", "Add a use case", IconManager.imgic602, IconManager.imgic602, "Add a use case", "Add a use case to the currently opened use case diagram", 0);
        actions[UCD_BORDER] = new TAction("add-border", "Add a border", IconManager.imgic612, IconManager.imgic612, "Add a border", "Add a border to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_ACTOR_UC] = new TAction("add-connector-actor-uc", "actor <-> use case", IconManager.imgic604, IconManager.imgic604, "Actor <-> use case", "Add a connector between an actor and a use case of the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_INCLUDE] = new TAction("add-connector-include", "<<include>> relationship", IconManager.imgic606, IconManager.imgic606, "<<include>> relationship", "Add a <<include>> relationship to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_EXTEND] = new TAction("add-connector-extend", "<<extend>> relationship", IconManager.imgic608, IconManager.imgic608, "<<extend>> relationship", "Add a <<extend>> relationship to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_SPECIA] = new TAction("add-connector-specia", "Specialization", IconManager.imgic610, IconManager.imgic610, "specialization", "Add a specialization relationship to the currently opened use case diagram", 0);
        
        actions[TDD_EDIT] = new TAction("edit-tdd-diagram", "Edit deployment diagram", IconManager.imgic100, IconManager.imgic101, "Edit deployment diagram", "Make it possible to edit the currently opened deployment diagram", 0);
        actions[TDD_LINK] = new TAction("add-tdd-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened deployment diagram", 0);
        actions[TDD_NODE] = new TAction("add-tdd-node", "Add a node", IconManager.imgic700, IconManager.imgic700, "Node", "Add a node to the currently opened deployment diagram", 0);
        actions[TDD_ARTIFACT] = new TAction("add-tdd-artifact", "Add an artifact", IconManager.imgic702, IconManager.imgic702, "Artifact", "Add an artifact to the currently opened deployment diagram", 0);
        
	    actions[NCDD_EDIT] = new TAction("edit-ncdd-diagram", "Edit network calculus diagram", IconManager.imgic100, IconManager.imgic101, "Edit network calculs diagram", "Make it possible to edit the currently opened network calculus diagram", 0);
        actions[NCDD_LINK] = new TAction("add-ncdd-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened network calculus diagram", 0);
        actions[NCDD_EQNODE] = new TAction("add-ncdd-eqnode", "Add an equipment node", IconManager.imgic700, IconManager.imgic700, "Add an equipment node", "Add an equipment node to the currently opened network calculus diagram", 0);
        actions[NCDD_SWITCHNODE] = new TAction("add-ncdd-switchnode", "Add a switch node", IconManager.imgic700, IconManager.imgic700, "Add a switch node", "Add a switch node to the currently opened network calculus diagram", 0);
        actions[NCDD_TRAFFIC_ARTIFACT] = new TAction("add-ncdd-traffic", "Add a traffic", IconManager.imgic702, IconManager.imgic702, "Traffic", "Add a traffic to the currently opened network calculus diagram", 0);
		actions[NCDD_ROUTE_ARTIFACT] = new TAction("add-ncdd-route", "Add a route", IconManager.imgic702, IconManager.imgic702, "Route", "Add a route to the currently opened network calculus diagram", 0);
		actions[ACT_NC] = new TAction("gen_nc-command", "Network calculus", IconManager.imgic3000, IconManager.imgic3000, "Network calculus",  "Perform network calculus from  UML diagrams", '0');
     
   
		
        actions[TMLTD_EDIT] = new TAction("edit-tmlcd-diagram", "Edit TML Task diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML task diagram", "Make it possible to edit the currently opened TML task diagram", 0);
        actions[TMLTD_TASK] = new TAction("add-tmlcd-task", "Add a TML Task", IconManager.imgic806, IconManager.imgic806, "Add a TML task", "Add a TML task to the currently opened TML task diagram", 0);
        actions[TMLTD_CHANNEL] = new TAction("add-tmlcd-channel", "Add a channel operator", IconManager.imgic802, IconManager.imgic802, "Add a channel operator", "Add a channel operator to the currently opened TML task diagram", 0);
        actions[TMLTD_REQ] = new TAction("add-tmlcd-req", "Add a request operator", IconManager.imgic804, IconManager.imgic804, "Add a request operator", "Add a request operator to the currently opened TML task diagram", 0);
        actions[TMLTD_EVENT] = new TAction("add-tmlcd-event", "Add an event operator", IconManager.imgic800, IconManager.imgic800, "Add an event operator", "Add an event operator to the currently opened TML task diagram", 0);
        actions[TMLTD_ASSOC] = new TAction("add-tmlcd-assoc", "Add an association between two TML tasks", IconManager.imgic202, IconManager.imgic202, "Add an association between two TML tasks", "Add an association between two TML tasks to the currently opened TML task diagram", 0);
        actions[TMLTD_CONNECTOR] = new TAction("add-tmlcd-connector", "Connect an operator to an association", IconManager.imgic108, IconManager.imgic108, "Connect an operator to an association", "Connect an operator to an asociation onto the currently opened TML task diagram", 0);

		actions[TMLCTD_EDIT] = new TAction("edit-tmlccd-diagram", "Edit TML Component Task diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML Component Task diagram", "Make it possible to edit the currently opened TML Component Task diagram", 0);
        actions[TMLCTD_CCOMPONENT] = new TAction("add-tmlccd-ccomponent", "Add a composite component", IconManager.imgic1200, IconManager.imgic1200, "Add a composite component", "Add a composite component to the currently opened TML component Task diagram", 0);
		actions[TMLCTD_CREMOTECOMPONENT] = new TAction("add-tmlcremotecd-ccomponent", "Add a reference to a composite component", IconManager.imgic1200, IconManager.imgic1200, "Add a reference to a composite component", "Add a reference to a composite component to the currently opened TML component Task diagram", 0);
		actions[TMLCTD_PCOMPONENT] = new TAction("add-tmlccd-pcomponent", "Add a primitive component", IconManager.imgic1202, IconManager.imgic1202, "Add a primitive component", "Add a primitive component to the currently opened TML component Task diagram", 0);
		actions[TMLCTD_CPORT] = new TAction("add-tmlccd-cport", "Add a composite port", IconManager.imgic1204, IconManager.imgic1204, "Add a composite port", "Add a composite port to the currently opened TML component Task diagram", 0);
		actions[TMLCTD_COPORT] = new TAction("add-tmlccd-coport", "Add a primitive port", IconManager.imgic1206, IconManager.imgic1206, "Add a primitive port", "Add a primitive port to the currently opened TML component Task diagram", 0);
		actions[TMLCTD_PORT_CONNECTOR] = new TAction("add-tmlccd-port-connector", "Add a connector between two ports", IconManager.imgic202, IconManager.imgic202, "Add a connector between two ports", "Add a connector between two ports of the currently opened TML component Task diagram", 0);
		
        actions[TMLAD_EDIT] = new TAction("edit-tmlad-diagram", "Edit TML Task activity diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML task activity diagram", "Make it possible to edit the currently opened TML task activity diagram", 0);
        actions[TMLAD_CONNECTOR] = new TAction("add-tmlad-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened TML task activity diagram", 0);
        actions[TMLAD_ACTION_STATE] = new TAction("add-tmlad-action-state", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. variable modification, operation call) to the currently opened TML activity diagram", 0);
        actions[TMLAD_CHOICE] = new TAction("add-tmlad-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened TML Task activity diagram", 0);
        actions[TMLAD_START] = new TAction("add-tmladd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened TML activity diagram", 0);
        actions[TMLAD_STOP] = new TAction("add-tmlad-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened TML task activity diagram", 0);
        actions[TMLAD_JUNCTION] = new TAction("add-tmlad-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECI] = new TAction("add-tmlad-execi", "EXECI", IconManager.imgic910, IconManager.imgic910, "EXECI", "Add an EXECI delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECI_INTERVAL] = new TAction("add-tmlad-execi-int", "EXECI (time interval)", IconManager.imgic914, IconManager.imgic914, "EXECI (time interval)", "Add an EXECI time interval to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECC] = new TAction("add-tmlad-execc", "EXECC", IconManager.imgic920, IconManager.imgic920, "EXECC", "Add an EXECC delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECC_INTERVAL] = new TAction("add-tmlad-execc-int", "EXECC (time interval)", IconManager.imgic922, IconManager.imgic922, "EXECC (time interval)", "Add an EXECC time interval to the currently opened TML task activity diagram", 0);
        actions[TMLAD_DELAY] = new TAction("add-tmlad-delay", "DELAY[]", IconManager.imgic214, IconManager.imgic214, "DELAY[]", "Add a physical delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_INTERVAL_DELAY] = new TAction("add-tmlad-interval-delay", "DELAY[,]", IconManager.imgic224, IconManager.imgic224, "DELAY[,]", "Add a physical interval delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_WRITE_CHANNEL] = new TAction("add-tmladd-write-channel", "Write in channel", IconManager.imgic900, IconManager.imgic900, "Write in channel", "Add a write to channel operator opened TML activity diagram", 0);
        actions[TMLAD_SEND_REQUEST] = new TAction("add-tmladd-send-request", "Send request", IconManager.imgic902, IconManager.imgic902, "Send request", "Add a send request operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_SEND_EVENT] = new TAction("add-tmladd-send-event", "Send event", IconManager.imgic904, IconManager.imgic904, "Send event", "Add a send event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_WAIT_EVENT] = new TAction("add-tmladd-wait-event", "Wait event", IconManager.imgic908, IconManager.imgic908, "Wait event", "Add a wait event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_NOTIFIED_EVENT] = new TAction("add-tmladd-notified-event", "Notified event", IconManager.imgic918, IconManager.imgic918, "Notified event", "Add a notified event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_READ_CHANNEL] = new TAction("add-tmladd-read-channel", "Read in channel", IconManager.imgic906, IconManager.imgic906, "Read in channel", "Add a read to channel operator opened TML activity diagram", 0);
        actions[TMLAD_FOR_LOOP] = new TAction("add-tmlad-for-loop", "Loop (for)", IconManager.imgic912, IconManager.imgic912, "Loop (for)", "Add a for loop to the currently opened TML activity diagram", 0);
        actions[TMLAD_FOR_STATIC_LOOP] = new TAction("add-tmlad-for-static-loop", "Static loop (for)", IconManager.imgic912, IconManager.imgic912, "Static loop (for)", "Add a static for loop to the currently opened TML activity diagram", 0);
        actions[TMLAD_FOR_EVER_LOOP] = new TAction("add-tmlad-for-ever-loop", "Loop for ever", IconManager.imgic926, IconManager.imgic926, "Loop for ever", "Add a loop-for-ever operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_SEQUENCE] = new TAction("add-tmlad-sequence", "Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence to the currently opened TML activity diagram", 0);
        actions[TMLAD_SELECT_EVT] = new TAction("add-tmlad-select-evt", "Add select event", IconManager.imgic916, IconManager.imgic916, "Select event", "Add a selection between events to the currently opened TML Task activity diagram", 0);
		actions[TMLAD_RANDOM] = new TAction("add-tmlad-random", "Add random", IconManager.imgic924, IconManager.imgic924, "Select random", "Add a random operator to the currently opened TML Task activity diagram", 0);
		
		actions[TMLARCHI_EDIT] = new TAction("edit-tmlarchi-diagram", "Edit DIPLODOCUS architecture diagram", IconManager.imgic100, IconManager.imgic101, "Edit DIPLODOCUS architecture diagram", "Make it possible to edit the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_LINK] = new TAction("add-tmlarchi-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_CPUNODE] = new TAction("add-tmlarchi-cpunode", "Add a CPU node", IconManager.imgic1100, IconManager.imgic1100, "CPU node", "Add a cpu node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_BUSNODE] = new TAction("add-tmlarchi-busnode", "Add a BUS node", IconManager.imgic1102, IconManager.imgic1102, "BUS node", "Add a bus node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_ARTIFACT] = new TAction("add-tmlarchi-artifact", "Map a task", IconManager.imgic702, IconManager.imgic702, "Map a task", "Map a task onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_COMMUNICATION_ARTIFACT] = new TAction("add-tmlarchi-communication-artifact", "Map a channel / event / request", IconManager.imgic702, IconManager.imgic702, "Map a channel / event / request", "Map a channel, event or request onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_BRIDGENODE] = new TAction("add-tmlarchi-bridgenode", "Add a Bridge node", IconManager.imgic1104, IconManager.imgic1104, "Bridge node", "Add a bridge node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_HWANODE] = new TAction("add-tmlarchi-hwanode", "Add a hardware accelerator node", IconManager.imgic1106, IconManager.imgic1106, "Hardware accelerator node", "Add a hardware accelerator node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_MEMORYNODE] = new TAction("add-tmlarchi-memorynode", "Add a memory node", IconManager.imgic1108, IconManager.imgic1108, "Memory node", "Add a memory node to the currently opened DIPLODOCUS architecture diagram", 0);
        

        //ProActive State Machine Diagram  
        actions[PROSMD_EDIT] = new TAction("edit-prosmd-diagram", "Edit ProActive state machine diagram", IconManager.imgic100, IconManager.imgic101, "Edit ProActive state machine diagram", "Make it possible to edit the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_START] = new TAction("add-prosmd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_STOP] = new TAction("add-prosmd-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_CONNECTOR] = new TAction("add-prosmd-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_SENDMSG] = new TAction("add-prosmd-sendmsg", "Send msg", IconManager.imgic2000, IconManager.imgic2000, "Send msg", "Add a send message to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_GETMSG] = new TAction("add-prosmd-getmsg", "Get msg", IconManager.imgic2002, IconManager.imgic2002, "Get msg", "Add a get message to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_CHOICE] = new TAction("add-prosmd-choice", "Choice", IconManager.imgic2004,IconManager.imgic2004,"Choice operator","Add a choice operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_JUNCTION] = new TAction("add-prosmd-junction", "Junction", IconManager.imgic2006,IconManager.imgic2006,"Junction operator","Add a junction operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_SUBMACHINE] = new TAction("add-prosmd-submachine", "Submachine", IconManager.imgic2008,IconManager.imgic2008,"Submachine","Add a submachine to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_ACTION] = new TAction("add-prosmd-action", "Action", IconManager.imgic2010,IconManager.imgic2010,"Action","Add an action to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_PARALLEL] = new TAction("add-prosmd-nondeterministic-choice", "Nondeterministic choice operator", IconManager.imgic206,IconManager.imgic206,"Parallel operator","Add a parallel operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_STATE] = new TAction("add-prosmd-state", "State ", IconManager.imgic2012,IconManager.imgic2012,"State ","Add state to the currently opened ProActive state machine diagram",0);

        //ProcActive Composite Structure Diagram
        actions[PROCSD_EDIT] = new TAction("edit-procsd-diagram", "Edit ProActive composite stucture diagram", IconManager.imgic100, IconManager.imgic101, "Edit ProActive composite structure diagram", "Make it possible to edit the currently opened ProActive composite structure diagram", 0);
        actions[PROCSD_COMPONENT] = new TAction("add-procsd-component", "Add component", IconManager.imgic2100, IconManager.imgic2100, "Add component", "Add a component to the currently opened ProActive composite structure diagram", 0);
        //Delegate port action removed, by Solange
        //actions[PROCSD_DELEGATE_PORT] = new TAction("add-procsd-delegate-port", "Add a delegate port", IconManager.imgic2102, IconManager.imgic2102, "Add a delegate port", "Add a delegate port to a component", 0); 
        actions[PROCSD_CONNECTOR] = new TAction("add-procsd-connector", "Connect two ports together", IconManager.imgic2110, IconManager.imgic2110, "Connect two ports together", "An interface coresponds to this conector", 0);
        actions[PROCSD_CONNECTOR_DELEGATE] = new TAction("add-procsd-connector-delegate", "Delegates the message", IconManager.imgic202, IconManager.imgic202, "Delegates the message ", "It transmits the message between the border and an inside component of a composite.", 0);
        actions[PROCSD_INTERFCE] = new TAction("add-procsd-interface", "Add an interface", IconManager.imgic2104, IconManager.imgic2104,"Add an interface", "an interface describes the messages wich pass through a connection", 0); 
        actions[PROCSD_IN_PORT] = new TAction("add-procsd-in-port", "Add in port", IconManager.imgic2106, IconManager.imgic2106, "Add in port", "Add an in port to a component. An in port coresponds to a offered/server interface", 0);
        actions[PROCSD_OUT_PORT] = new TAction("add-procsd-out-port", "Add out port", IconManager.imgic2108, IconManager.imgic2108, "Add out port", "Add an out port to a component. An out port coresponds to an requiered/client  interface", 0);        
        actions[PROCSD_CONNECTOR_PORT_INTERFACE] = new TAction("add-port-to-interface-connector", "Connect a port to an interface definition", IconManager.imgic108, IconManager.imgic108, "Connects a port to an interface definition", "Port to interface connector", 0);
        
        actions[ACT_TOGGLE_CHANNELS] = new TAction("toggle-ch-command", "Show / hide Channels",IconManager.imgic808, IconManager.imgic808, "Show / hide channels", "Show / hide channels", '0');
        actions[ACT_TOGGLE_EVENTS] = new TAction("toggle-evt-command", "Show / hide Events",IconManager.imgic810, IconManager.imgic810, "Show / hide events", "Show / hide events", '0');
        actions[ACT_TOGGLE_REQUESTS] = new TAction("toggle-req-command", "Show / hide Synchronization Requests",IconManager.imgic812, IconManager.imgic812, "Show / hide requests", "Show / hide requests", '0');
        
        // Requirement diagrams
        actions[TREQ_REQUIREMENT] = new TAction("add-treq-requirement", "Add a requirement", IconManager.imgic1002, IconManager.imgic1002, "Requirement", "Add a requirement to the currently opened requirement diagram", 0);
        actions[TREQ_OBSERVER] = new TAction("add-treq-observer", "Add an observer", IconManager.imgic1004, IconManager.imgic1004, "Observer", "Add an observer to the currently opened requirement diagram", 0);
        actions[TREQ_DERIVE] = new TAction("add-treq-derive", "Add a <<deriveReqt>> link", IconManager.imgic1008, IconManager.imgic1008, "DeriveReqt", "Add a <<deriveReqt>> link between two requirements of the currently opened requirement diagram", 0);
        actions[TREQ_VERIFY] = new TAction("add-treq-verify", "Add a <<verify>> link", IconManager.imgic1006, IconManager.imgic1006, "Verify", "Add a <<verify>> link between an observer and a requirement of the currently opened requirement diagram", 0);
		actions[TREQ_COPY] = new TAction("add-treq-copy", "Add a <<copy>> link", IconManager.imgic1010, IconManager.imgic1010, "Copy", "Add a <<copy>> link between two requirements of the currently opened requirement diagram", 0);
        actions[TREQ_COMPOSITION] = new TAction("add-treq-composition", "Add a <<derive>> link", IconManager.imgic1012, IconManager.imgic1012, "Composition", "Add a <<copy>> link between two requirements of the currently opened requirement diagram", 0);
        
        // TURTLE-OS
        actions[TOS_TCLASS] = new TAction("add-tclass-os", "Add Tclass", IconManager.imgic104, IconManager.imgic104, "Tclass", "Add a TClass to the currently opened class diagram", 0);
        actions[TOS_ASSOCIATION] = new TAction("add-association-tos", "Add association", IconManager.imgic102, IconManager.imgic102, "Association", "Add an association between two tclasses of the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_ASSOCIATION_NAVIGATION] = new TAction("add-association-nav-tos", "Add association with navigation", IconManager.imgic106, IconManager.imgic106, "Association with navigation", "Add an association with navigation between two tclasses of the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_CONNECTOR_ATTRIBUTE] = new TAction("add-attribute-connector-tos", "Connects an association to a composition operator", IconManager.imgic108, IconManager.imgic108, "Attributes an association with a composition operator", "Adds a connector between an association and a composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_CALL_OPERATOR] = new TAction("add-call-operator", "Add Call composition operator", IconManager.imgic126, IconManager.imgic126, "Call composition operator", "Adds a call composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_EVT_OPERATOR] = new TAction("add-evt-operator", "Add Evt composition operator", IconManager.imgic126, IconManager.imgic126, "Evt composition operator", "Adds an evt composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOSAD_ACTION_STATE] = new TAction("add-action-state-osad", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. synchronization on gate, variable modification, call on protected object) to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_CONNECTOR] = new TAction("add-tosad-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_CHOICE] = new TAction("add-tosad-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_START_STATE] = new TAction("add-tosad-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened TURTLKE-OS activity diagram", 0);
        actions[TOSAD_STOP_STATE] = new TAction("add-tosad-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_JUNCTION] = new TAction("add-tosad-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_TIME_INTERVAL] = new TAction("add-tosad-time-interval", "Add Time Interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_INT_TIME_INTERVAL] = new TAction("add-tosad-int-time-interval", "Add Interruptible Time Interval", IconManager.imgic224, IconManager.imgic224, "Interruptible time interval", "Add an interruptible time interval to the currently opened TURTLE-OS activity diagram", 0);

        actions[EXTERNAL_ACTION_1] = new TAction("user-command-1", "User command 1", IconManager.imgic338, IconManager.imgic338, "User command 1", "Execute the user command #1", '0');
        actions[EXTERNAL_ACTION_2] = new TAction("user-command-2", "User command 2", IconManager.imgic338, IconManager.imgic338, "User command 2", "Execute the user command #2", '0');


    }
    
    
    public String getActionCommand()  {
        return (String)getValue(Action.ACTION_COMMAND_KEY);
    }

    public String getShortDescription()  {
        return (String)getValue(Action.SHORT_DESCRIPTION);
    }
    
    public String getLongDescription()  {
        return (String)getValue(Action.LONG_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt)  {
        //System.out.println("Action performed");
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();
            
            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
            (String)getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length-2; i += 2) {
                ((ActionListener)listenerList[i+1]).actionPerformed(e);
            }
        }
    }
    
    public void addActionListener(ActionListener l)  {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, l);
    }
    
    public void removeActionListener(ActionListener l)  {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, l);
    }    
}

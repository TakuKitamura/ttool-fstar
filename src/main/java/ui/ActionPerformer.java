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





package ui;

import common.ConfigurationTTool;
import common.SpecConfigTTool;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
   * Class ActionPerformer
   * Method to be called when actions are performed
   * Created for refactoring of MainGUI
   * Creation: 19/02/2017
   * @version 1.0 19/02/2017
   * @author Ludovic APVRILLE
 */
public class ActionPerformer {

    public static void actionPerformed(MainGUI mgui, ActionEvent evt, String command, TDiagramPanel tdp1) {
	
        // Compare the action command to the known actions.
        if (command.equals(mgui.actions[TGUIAction.ACT_NEW].getActionCommand()))  {
        	mgui.newProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_NEW_PROJECT].getActionCommand()))  {
        	mgui.newProjectDir();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_NEW_DESIGN].getActionCommand())) {
            mgui.newDesign();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_NEW_ANALYSIS].getActionCommand())) {
            mgui.newAnalysis();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN_FROM_NETWORK].getActionCommand())) {
            mgui.openNetworkProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN].getActionCommand())) {
            mgui.openProject(false);
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN_PROJECT].getActionCommand())) {
            mgui.openProject(true);
        } else if (command.equals(mgui.actions[TGUIAction.ACT_MERGE].getActionCommand())) {
            mgui.mergeProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN_LAST].getActionCommand())) {
            mgui.openLastProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE].getActionCommand())) {
            mgui.saveProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_AS_MODEL].getActionCommand())) {
            mgui.saveAsNewModel();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_AS_PROJECT].getActionCommand())) {
            mgui.saveAsNewProject();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_TIF].getActionCommand())) {
            mgui.saveTIF();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN_TIF].getActionCommand())) {
            mgui.openTIF();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_OPEN_SD].getActionCommand())) {
            mgui.openSD();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_LOTOS].getActionCommand())) {
            mgui.saveLastLotos();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_DTA].getActionCommand())) {
            mgui.saveLastDTA();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_RG].getActionCommand())) {
            mgui.saveLastRG();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_TLSA].getActionCommand())) {
            mgui.saveLastTLSA();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_AUT].getActionCommand())) {
            mgui.saveLastRGAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_AUTPROJ].getActionCommand())) {
            mgui.saveLastRGAUTProj();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SAVE_AUTMODIFIED].getActionCommand())) {
            mgui.saveLastModifiedRG();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_EXPORT_LIB].getActionCommand())) {
            mgui.exportLibrary();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_IMPORT_LIB].getActionCommand())) {
            mgui.importLibrary();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_QUIT].getActionCommand())) {
            mgui.quitApplication();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_CUT].getActionCommand())) {
            mgui.cut();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_COPY].getActionCommand())) {
            mgui.copy();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_PASTE].getActionCommand())) {
            mgui.paste();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DELETE].getActionCommand())) {
            mgui.delete();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SUPPR].getActionCommand())) {
            mgui.delete();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ZOOM_MORE].getActionCommand())) {
            mgui.zoomMore();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ZOOM_LESS].getActionCommand())) {
            mgui.zoomLess();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_BACKWARD].getActionCommand())) {
            mgui.backward();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_FORWARD].getActionCommand())) {
            mgui.forward();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_FIRST_DIAG].getActionCommand())) {
            mgui.firstDiag();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_BACK_DIAG].getActionCommand())) {
            mgui.backDiag();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_NEXT_DIAG].getActionCommand())) {
            mgui.nextDiag();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_LAST_DIAG].getActionCommand())) {
            mgui.lastDiag();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ABOUT].getActionCommand())) {
            mgui.aboutVersion();
        } else if (command.equals(mgui.actions[TGUIAction.FIRST_DIAGRAM].getActionCommand())) {
            mgui.firstDiag();
        //@author: Huy TRUONG.
        //open a external search box for ACT_EXTERNAL_SEARCH
	}  else if (command.equals(mgui.actions[TGUIAction.ACT_EXTERNAL_SEARCH].getActionCommand())) {
            mgui.showExternalSearch();}
        else if (command.equals(mgui.actions[TGUIAction.ACT_INTERNAL_SEARCH].getActionCommand())) {
            mgui.doInternalSearch();
            //--

        } else if (command.equals(mgui.actions[TGUIAction.ACT_TTOOL_CONFIGURATION].getActionCommand())) {
            mgui.showTToolConfiguration();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TURTLE_WEBSITE].getActionCommand())) {
            mgui.aboutTURTLE();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TURTLE_DOCUMENTATION].getActionCommand())) {
            mgui.helpTURTLE();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SYSMLSEC_DOCUMENTATION].getActionCommand())) {
            mgui.helpSysMLSec();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DIPLODOCUS_DOCUMENTATION].getActionCommand())) {
            mgui.helpDIPLODOCUS();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_MODEL_CHECKING].getActionCommand())) {
            mgui.modelChecking();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS].getActionCommand())) {
            mgui.generateRTLOTOS();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_LOTOS].getActionCommand())) {
            mgui.generateLOTOS();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_AUT].getActionCommand())) {
            mgui.generateAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_AUTS].getActionCommand())) {
            mgui.generateAUTS();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_UPPAAL].getActionCommand())) {
            mgui.generateUPPAAL();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DSE].getActionCommand())) {
            mgui.dse();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].getActionCommand())) {
            mgui.avatarModelChecker();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_JAVA].getActionCommand())) {
            mgui.generateJava();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SIMU_JAVA].getActionCommand())) {
            mgui.simuJava();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC].getActionCommand())) {
            mgui.generateSystemC();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC].getActionCommand())) {
            mgui.interactiveSimulationSystemC();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_TMLTXT].getActionCommand())) {
            mgui.generateTMLTxt();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_CCODE].getActionCommand())) {
            mgui.generateCCode();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_DESIGN].getActionCommand())) {
            mgui.generateDesign();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_CHECKCODE].getActionCommand())) {
            mgui.checkCode();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SIMULATION].getActionCommand())) {
            mgui.simulation();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VALIDATION].getActionCommand())) {
            mgui.formalValidation();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].getActionCommand())) {
            mgui.oneClickLOTOSRG();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG].getActionCommand())) {
            mgui.oneClickRTLOTOSRG();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_PROJECTION].getActionCommand())) {
            mgui.projection();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_GRAPH_MODIFICATION].getActionCommand())) {
            mgui.modifyGraph();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_BISIMULATION].getActionCommand())) {
            mgui.bisimulation();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_BISIMULATION_CADP].getActionCommand())) {
            mgui.bisimulationCADP();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].getActionCommand())) {
            mgui.seekDeadlockAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_SAVED_AUT].getActionCommand())) {
            mgui.seekDeadlockSavedAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUT].getActionCommand())) {
            mgui.statAUT();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUTDIPLODOCUS].getActionCommand())) {
            mgui.statAUTDiplodocus();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].getActionCommand())) {
            mgui.statAUTProj();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_STAT_SAVED_AUT].getActionCommand())) {
            mgui.statSavedAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_PM_AUT].getActionCommand())) {
            mgui.pmAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].getActionCommand())) {
            mgui.pmAUTProj();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_PM_SAVED_AUT].getActionCommand())) {
            mgui.pmSavedAUT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_RTLOTOS].getActionCommand())) {
            mgui.showFormalSpecification();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_JAVA].getActionCommand())) {
            mgui.showJavaCode();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES].getActionCommand())) {
            mgui.showBirdEyesView();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES_EMB].getActionCommand())) {
            mgui.showEmbeddedBirdEyesView();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_WAVE].getActionCommand())) {
            mgui.showWave();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].getActionCommand())) {
            mgui.showSuggestedDesign();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_SIM].getActionCommand())) {
            mgui.showSimulationTrace();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_SIM_CHRONO].getActionCommand())) {
            mgui.showSimulationTraceChrono();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_DTADOT].getActionCommand())) {
            mgui.showDTA();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_RGDOT].getActionCommand())) {
            mgui.showRG();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_TLSADOT].getActionCommand())) {
            mgui.showTLSA();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_RGAUTDOT].getActionCommand())) {
            mgui.showRGAut();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].getActionCommand())) {
            mgui.showRGAutProj();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT].getActionCommand())) {
            mgui.showModifiedAUTDOT();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_RG_DIPLODOCUS].getActionCommand())) {
            mgui.showRGDiplodocus();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_SAVED_LOT].getActionCommand())) {
            mgui.showSavedRTLOTOS();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_VIEW_SAVED_DOT].getActionCommand())) {
            mgui.showGGraph();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SCREEN_CAPTURE].getActionCommand())) {
            mgui.screenCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TTOOL_WINDOW_CAPTURE].getActionCommand())) {
            mgui.windowCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_DIAGRAM_CAPTURE].getActionCommand())) {
            mgui.diagramCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SVG_DIAGRAM_CAPTURE].getActionCommand())) {
            mgui.svgDiagramCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE].getActionCommand())) {
            mgui.allDiagramCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE_SVG].getActionCommand())) {
            mgui.allDiagramCaptureSvg();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_SELECTED_CAPTURE].getActionCommand())) {
            mgui.selectedCapture();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_DOC].getActionCommand())) {
            mgui.generateDocumentation();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GEN_DOC_REQ].getActionCommand())) {
            mgui.generateDocumentationReq();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_ATTRIBUTES].getActionCommand())) {
            mgui.toggleAttributes();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID].getActionCommand())) {
            mgui.toggleDiploIDs();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_TEPE_ID].getActionCommand())) {
            mgui.toggleTEPEIDs();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_AVATAR_ID].getActionCommand())) {
            mgui.toggleAVATARIDs();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_GATES].getActionCommand())) {
            mgui.toggleGates();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_SYNCHRO].getActionCommand())) {
            mgui.toggleSynchro();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_CHANNELS].getActionCommand())) {
            mgui.toggleChannels();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_EVENTS].getActionCommand())) {
            mgui.toggleEvents();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_REQUESTS].getActionCommand())) {
            mgui.toggleRequests();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_JAVA].getActionCommand())) {
            mgui.toggleJava();
        }  else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_INTERNAL_COMMENT].getActionCommand())) {
            mgui.toggleInternalComment();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR].getActionCommand())) {
            mgui.toggleAttr();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_ENHANCE].getActionCommand())) {
            mgui.enhanceDiagram();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_NC].getActionCommand())) {
            mgui.NC();
        } else if (command.equals(mgui.actions[TGUIAction.EXTERNAL_ACTION_1].getActionCommand())) {
            mgui.executeUserCommand(ConfigurationTTool.ExternalCommand1Host, SpecConfigTTool.ExternalCommand1);
        } else if (command.equals(mgui.actions[TGUIAction.EXTERNAL_ACTION_2].getActionCommand())) {
            mgui.executeUserCommand(ConfigurationTTool.ExternalCommand2Host, ConfigurationTTool.ExternalCommand2);
        } else if (command.equals(mgui.actions[TGUIAction.CONNECTOR_COMMENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COMMENT);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.UML_NOTE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UML_NOTE);
        } else if (command.equals(mgui.actions[TGUIAction.PRAGMA].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PRAGMA);
        } else if (command.equals(mgui.actions[TGUIAction.SAFETY_PRAGMA].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SAFETY_PRAGMA);
        } else if (command.equals(mgui.actions[TGUIAction.PERFORMANCE_PRAGMA].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PERFORMANCE_PRAGMA);

            // AVATAR actions
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_SIM].getActionCommand())) {
            mgui.avatarSimulation();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].getActionCommand())) {
            mgui.avatarUPPAALVerification();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].getActionCommand())) {
            mgui.avatarProVerifVerification();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].getActionCommand())) {
            mgui.avatarStaticAnalysis();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].getActionCommand())) {
            mgui.avatarExecutableCodeGeneration();

            // AVATAR BD
        } else if (command.equals(mgui.actions[TGUIAction.ABD_BLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_BLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_CRYPTOBLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_CRYPTOBLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_DATATYPE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_DATATYPE);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARBD_COMPOSITION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_PORT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARBD_PORT_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_LIBRARYFUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_LIBRARYFUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.ABD_CRYPTOLIBRARYFUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARBD_CRYPTOLIBRARYFUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.AVATAR_FIREWALL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATAR_FIREWALL);

            // AVATAR SMD
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARSMD_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_SEND_SIGNAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_SEND_SIGNAL);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_RECEIVE_SIGNAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RECEIVE_SIGNAL);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_LIBRARY_FUNCTION_CALL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_LIBRARY_FUNCTION_CALL);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_PARALLEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_PARALLEL);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_RANDOM].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RANDOM);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_SET_TIMER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_SET_TIMER);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_RESET_TIMER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_RESET_TIMER);
        } else if (command.equals(mgui.actions[TGUIAction.ASMD_EXPIRE_TIMER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARSMD_EXPIRE_TIMER);

            // AVATAR MAD
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_ASSUMPTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_ASSUMPTION);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_DIAGRAM_REFERENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_DIAGRAM_REFERENCE);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_ELEMENT_REFERENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARMAD_ELEMENT_REFERENCE);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_COMPOSITION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_VERSIONING_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_VERSIONING_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_IMPACT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_IMPACT_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_MEET_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_MEET_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.AMAD_BELONGSTOCOMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARMAD_BELONGSTOCOMPOSITION_CONNECTOR);

            // AVATAR RD
        } else if (command.equals(mgui.actions[TGUIAction.ARD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_REQUIREMENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARRD_REQUIREMENT);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_PROPERTY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARRD_PROPERTY);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_ELEMENT_REFERENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AVATARRD_ELEMENT_REFERENCE);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_DERIVE_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_DERIVE_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_REFINE_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_REFINE_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_VERIFY_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_VERIFY_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_SATISFY_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_SATISFY_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_COPY_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_COPY_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ARD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AVATARRD_COMPOSITION_CONNECTOR);

            // AVATAR PD
        } else if (command.equals(mgui.actions[TGUIAction.APD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.APD_BLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_BLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.APD_LOGICAL_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_LOGICAL_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.APD_TEMPORAL_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_TEMPORAL_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.APD_ATTRIBUTE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ATTRIBUTE);
        } else if (command.equals(mgui.actions[TGUIAction.APD_SIGNAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_SIGNAL);
        } else if (command.equals(mgui.actions[TGUIAction.APD_ALIAS].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ALIAS);
        } else if (command.equals(mgui.actions[TGUIAction.APD_BOOLEQ].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_BOOLEQ);
        } else if (command.equals(mgui.actions[TGUIAction.APD_ATTRIBUTE_SETTING].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_ATTRIBUTE_SETTING);
        } else if (command.equals(mgui.actions[TGUIAction.APD_PROPERTY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_PROPERTY);
        } else if (command.equals(mgui.actions[TGUIAction.APD_PROPERTY_RELATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.APD_PROPERTY_RELATION);
        }

        else if (command.equals(mgui.actions[TGUIAction.APD_ATTRIBUTE_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_ATTRIBUTE_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.APD_SIGNAL_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_SIGNAL_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.APD_PROPERTY_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_PROPERTY_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.APD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.APD_COMPOSITION_CONNECTOR);

            // AVATAR CD
        } else if (command.equals(mgui.actions[TGUIAction.ACD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.ACD_BLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_BLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.ACD_ACTOR_STICKMAN].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_ACTOR_STICKMAN);
        } else if (command.equals(mgui.actions[TGUIAction.ACD_ACTOR_BOX].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ACD_ACTOR_BOX);

        } else if (command.equals(mgui.actions[TGUIAction.ACD_ASSOCIATION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ACD_ASSOCIATION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ACD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ACD_COMPOSITION_CONNECTOR);

            // AVATAR AD
        } else if (command.equals(mgui.actions[TGUIAction.AAD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_ASSOCIATION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.AAD_ASSOCIATION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_START_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_STOP_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_PARALLEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_PARALLEL);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_ACTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACTION);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_ACTIVITY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACTIVITY);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_STOP_FLOW].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_STOP_FLOW);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_SEND_SIGNAL_ACTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_SEND_SIGNAL_ACTION);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_ACCEPT_EVENT_ACTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_ACCEPT_EVENT_ACTION);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_PARTITION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.AAD_PARTITION);
        } else if (command.equals(mgui.actions[TGUIAction.AAD_ALIGN_PARTITION].getActionCommand())) {
            mgui.alignPartitions();

            // Avatar DD
        } else if (command.equals(mgui.actions[TGUIAction.ADD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);

            // julien  --------------------------------------------------------------
        } else if (command.equals(mgui.actions[TGUIAction.DEPLOY_AVATAR_DIAGRAM].getActionCommand())){
            mgui.avatarddExecutableCodeGeneration();
        } else if  (command.equals(mgui.actions[TGUIAction.EXTRAC_DEPLOY_PARAM_TO_FILE].getActionCommand())){
            mgui.extracDeploymentDiagramToFile();
            // -------------------------------------------------------------------------


        } else if (command.equals(mgui.actions[TGUIAction.ADD_LINK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ADD_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ADD_CPUNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_CPUNODE);
        } else if (command.equals(mgui.actions[TGUIAction.ADD_BUSNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_BUSNODE);
        }
        else if (command.equals(mgui.actions[TGUIAction.ADD_VGMNNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_VGMNNODE);
        }
        else if (command.equals(mgui.actions[TGUIAction.ADD_CROSSBARNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_CROSSBARNODE);
        }
        else if (command.equals(mgui.actions[TGUIAction.ADD_BRIDGENODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_BRIDGENODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_TTYNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_TTYNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_RAMNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_RAMNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_ROMNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_ROMNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_DMANODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_DMANODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_ICUNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_ICUNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_COPROMWMRNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_COPROMWMRNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_TIMERNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_TIMERNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_BLOCKARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_ARTIFACT);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_CHANNELARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_CHANNELARTIFACT);
        }  else if (command.equals(mgui.actions[TGUIAction.ADD_CLUSTERNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ADD_CLUSTERNODE);

        } else if (command.equals(mgui.actions[TGUIAction.TCD_ASSOCIATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ASSOCIATION);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_CONNECTOR_ATTRIBUTE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ATTRIBUTE);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_ASSOCIATION_NAVIGATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ASSOCIATION_NAVIGATION);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_NEW_TCLASS].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TCLASS);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_NEW_TOBJECT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TOBJECT);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_NEW_TDATA].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_TDATA);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_PARALLEL_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_PARALLEL_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_SYNCHRO_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_SYNCHRO_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_INVOCATION_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_INVOCATION_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_SEQUENCE_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_SEQUENCE_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TCD_PREEMPTION_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TCD_PREEMPTION_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.AD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.AD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_AD_DIAGRAM);
        } else if (command.equals(mgui.actions[TGUIAction.AD_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ACTION_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.AD_ARRAY_GET].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ARRAY_GET);
        } else if (command.equals(mgui.actions[TGUIAction.AD_ARRAY_SET].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_ARRAY_SET);
        } else if (command.equals(mgui.actions[TGUIAction.AD_PARALLEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_PARALLEL);
        } else if (command.equals(mgui.actions[TGUIAction.AD_SEQUENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_SEQUENCE);
        } else if (command.equals(mgui.actions[TGUIAction.AD_PREEMPTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_PREEMPTION);
        } else if (command.equals(mgui.actions[TGUIAction.AD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.AD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.AD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.AD_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_DETERMINISTIC_DELAY);
        } else if (command.equals(mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_NON_DETERMINISTIC_DELAY);
        } else if (command.equals(mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_DELAY_NON_DETERMINISTIC_DELAY);
        } else if (command.equals(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_LIMITED_OFFER);
        } else if (command.equals(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_LIMITED_OFFER_WITH_LATENCY);
        } else if (command.equals(mgui.actions[TGUIAction.AD_TIME_CAPTURE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TAD_TIME_CAPTURE);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_CONNECTOR].getActionCommand())) {
            //TraceManager.addDev("Connector interaction");
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_INTERACTION);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_PARALLEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_PARALLEL);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_PREEMPTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_PREEMPTION);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_SEQUENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_SEQUENCE);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_REF_SD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_REF_SD);
        } else if (command.equals(mgui.actions[TGUIAction.IOD_REF_IOD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.IOD_REF_IOD);
        } else if (command.equals(mgui.actions[TGUIAction.SD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.SD_INSTANCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_INSTANCE);
        } else if (command.equals(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_SYNC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_SYNC_SD);
        } else if (command.equals(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_ASYNC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_ASYNC_SD);
        } else if (command.equals(mgui.actions[TGUIAction.SD_ABSOLUTE_TIME_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_ABSOLUTE_TIME_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_RELATIVE_TIME_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_RELATIVE_TIME_SD);
        } else if (command.equals(mgui.actions[TGUIAction.SD_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_ACTION_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.SD_GUARD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_GUARD);
        } else if (command.equals(mgui.actions[TGUIAction.SD_TIME_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIME_INTERVAL);
        } else if (command.equals(mgui.actions[TGUIAction.SD_TIMER_SETTING].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_SETTING);
        } else if (command.equals(mgui.actions[TGUIAction.SD_TIMER_EXPIRATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_EXPIRATION);
        } else if (command.equals(mgui.actions[TGUIAction.SD_TIMER_CANCELLATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_TIMER_CANCELLATION);
        } else if (command.equals(mgui.actions[TGUIAction.SD_COREGION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SD_COREGION);
        } else if (command.equals(mgui.actions[TGUIAction.SD_ALIGN_INSTANCES].getActionCommand())) {
            mgui.alignInstances();
	} else if (command.equals(mgui.actions[TGUIAction.SDZV_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_INSTANCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_INSTANCE);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_CONNECTOR_MESSAGE_SYNC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_SYNC_SDZV);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_CONNECTOR_MESSAGE_ASYNC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_ASYNC_SDZV);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_ABSOLUTE_TIME_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_ABSOLUTE_TIME_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_RELATIVE_TIME_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_RELATIVE_TIME_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_RELATIVE_TIME_CONSTRAINT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_RELATIVE_TIME_SDZV);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_ACTION_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_GUARD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_GUARD);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_TIME_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_TIME_INTERVAL);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_TIMER_SETTING].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_TIMER_SETTING);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_TIMER_EXPIRATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_TIMER_EXPIRATION);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_TIMER_CANCELLATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_TIMER_CANCELLATION);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_COREGION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.SDZV_COREGION);
        } else if (command.equals(mgui.actions[TGUIAction.SDZV_ALIGN_INSTANCES].getActionCommand())) {
            mgui.alignInstances();
        } else if (command.equals(mgui.actions[TGUIAction.UCD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_ACTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_ACTOR);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_ACTORBOX].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_ACTORBOX);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_USECASE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_USECASE);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_BORDER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.UCD_BORDER);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_CONNECTOR_ACTOR_UC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_ACTOR_UCD);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_CONNECTOR_INCLUDE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_INCLUDE_UCD);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_CONNECTOR_EXTEND].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EXTEND_UCD);
        } else if (command.equals(mgui.actions[TGUIAction.UCD_CONNECTOR_SPECIA].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_SPECIA_UCD);
        } else if (command.equals(mgui.actions[TGUIAction.TDD_LINK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_DD);
        } else if (command.equals(mgui.actions[TGUIAction.TDD_NODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TDD_NODE);
        } else if (command.equals(mgui.actions[TGUIAction.TDD_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TDD_ARTIFACT);
        } else if (command.equals(mgui.actions[TGUIAction.NCDD_LINK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_NC);
        } else if (command.equals(mgui.actions[TGUIAction.NCDD_EQNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_EQNODE);
        } else if (command.equals(mgui.actions[TGUIAction.NCDD_SWITCHNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_SWITCHNODE);
        } else if (command.equals(mgui.actions[TGUIAction.NCDD_TRAFFIC_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_TRAFFIC_ARTIFACT);
        } else if (command.equals(mgui.actions[TGUIAction.NCDD_ROUTE_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.NCDD_ROUTE_ARTIFACT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_TASK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_TASK);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EBRDD);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_CONNECTOR_ERC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_EBRDD_ERC);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_ERC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ERC);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_ESO].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ESO);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_ERB].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ERB);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_SEQUENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_SEQUENCE);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_ACTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_ACTION);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_FOR_LOOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_FOR_LOOP);
        } else if (command.equals(mgui.actions[TGUIAction.EBRDD_VARIABLE_DECLARATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.EBRDD_VARIABLE_DECLARATION);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TMLAD);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_ASSOC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TML_ASSOCIATION_NAV);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_CHANNEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_CHANNEL_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_REQ].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_REQUEST_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_EVENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLTD_EVENT_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TML_COMPOSITION_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TMLTD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_WRITE_CHANNEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_WRITE_CHANNEL);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_SEND_REQUEST].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEND_REQUEST);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_READ_REQUEST_ARG].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_READ_REQUEST_ARG);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_SEND_EVENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEND_EVENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_WAIT_EVENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_WAIT_EVENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_NOTIFIED_EVENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_NOTIFIED_EVENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_READ_CHANNEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_READ_CHANNEL);
        }  else if (command.equals(mgui.actions[TGUIAction.TMLAD_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_ACTION_STATE);
        }  else if (command.equals(mgui.actions[TGUIAction.TMLAD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_EXECI].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECI);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_EXECI_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECI_INTERVAL);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_EXECC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECC);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_EXECC_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_EXECC_INTERVAL);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_DELAY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_DELAY);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_INTERVAL_DELAY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_INTERVAL_DELAY);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_FOR_LOOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_LOOP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_FOR_STATIC_LOOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_STATIC_LOOP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_FOR_EVER_LOOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_FOR_EVER_LOOP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_SEQUENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SEQUENCE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_UNORDERED_SEQUENCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_UNORDERED_SEQUENCE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_SELECT_EVT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_SELECT_EVT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_RANDOM].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_RANDOM);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_ENCRYPT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_ENCRYPT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLAD_DECRYPT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLAD_DECRYPT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_CCOMPONENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CCOMPONENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_CREMOTECOMPONENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CREMOTECOMPONENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_PCOMPONENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_PCOMPONENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_RCOMPONENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_RCOMPONENT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_CPORT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_CPORT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_FORK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_FORK);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_JOIN].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_JOIN);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_COPORT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCTD_COPORT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCTD_PORT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PORT_TMLC);

        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_LINK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_NODE_TMLARCHI);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_CPUNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_CPUNODE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_FPGANODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_FPGANODE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_BUSNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_BUSNODE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_CPNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_CPNODE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_BRIDGENODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_BRIDGENODE);
        }  else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_HWANODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_HWANODE);
	}  else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_CAMSNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_CAMSNODE); 
        }  else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_MEMORYNODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_MEMORYNODE);
        }  else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_DMANODE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_DMANODE);
        }  else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_ARTIFACT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_COMMUNICATION_ARTIFACT);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_KEY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_KEY);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_FIREWALL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_FIREWALL);
        } else if (command.equals(mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLARCHI_PORT_ARTIFACT);



            // Communication patterns
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_TMLCP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_FORK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_FORK);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_JOIN].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_JOIN);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_REF_CP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_REF_CP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_REF_SD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_REF_SD);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.TMLCP_FOR_LOOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLCP_FOR_LOOP);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_MESSAGE_ASYNC].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_MESSAGE_ASYNC_TMLSD);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_STORAGE_INSTANCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_STORAGE_INSTANCE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_CONTROLLER_INSTANCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_CONTROLLER_INSTANCE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_TRANSFER_INSTANCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_TRANSFER_INSTANCE);
        } else if (command.equals(mgui.actions[TGUIAction.TMLSD_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TMLSD_ACTION_STATE);

	    	// SystemC-AMS
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_BLOCK_TDF].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_BLOCK_TDF);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_BLOCK_DE].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_BLOCK_DE);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_CONNECTOR].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_PORT_TDF].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_PORT_TDF);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_PORT_DE].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_PORT_DE);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_PORT_CONVERTER].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_PORT_CONVERTER);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_CLUSTER].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_CLUSTER);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_BLOCK_GPIO2VCI].getActionCommand())) {
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.CAMS_BLOCK_GPIO2VCI);
        } else if (command.equals(mgui.actions[TGUIAction.CAMS_GENCODE].getActionCommand())){
            mgui.syscamsExecutableCodeGeneration();

		// ELN
        } else if (command.equals(mgui.actions[TGUIAction.ELN_EDIT].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.EDIT, -1);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_CONNECTOR].getActionCommand())){
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_RESISTOR].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_RESISTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_CAPACITOR].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_CAPACITOR);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_INDUCTOR].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_INDUCTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_IDEAL_TRANSFORMER].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_IDEAL_TRANSFORMER);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_TRANSMISSION_LINE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_TRANSMISSION_LINE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_INDEPENDENT_VOLTAGE_SOURCE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_INDEPENDENT_VOLTAGE_SOURCE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_INDEPENDENT_CURRENT_SOURCE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_INDEPENDENT_CURRENT_SOURCE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_NODE_REF].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_NODE_REF);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_TDF_VOLTAGE_SINK].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_TDF_VOLTAGE_SINK);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_TDF_CURRENT_SINK].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_TDF_CURRENT_SINK);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_MODULE].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_MODULE);
        } else if (command.equals(mgui.actions[TGUIAction.ELN_MODULE_TERMINAL].getActionCommand())){
        	mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ELN_MODULE_TERMINAL);

            // Attack Tree Diagrams
        } else if (command.equals(mgui.actions[TGUIAction.ATD_BLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_BLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.ATD_ATTACK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_ATTACK);
	} else if (command.equals(mgui.actions[TGUIAction.ATD_COUNTERMEASURE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_COUNTERMEASURE);
        } else if (command.equals(mgui.actions[TGUIAction.ATD_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.ATD_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.ATD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ATD_COMPOSITION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.ATD_ATTACK_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ATD_ATTACK_CONNECTOR);
	} else if (command.equals(mgui.actions[TGUIAction.ATD_COUNTERMEASURE_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.ATD_COUNTERMEASURE_CONNECTOR);

	    // Fault Tree Diagrams
        } else if (command.equals(mgui.actions[TGUIAction.FTD_BLOCK].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.FTD_BLOCK);
        } else if (command.equals(mgui.actions[TGUIAction.FTD_FAULT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.FTD_FAULT);
	} else if (command.equals(mgui.actions[TGUIAction.FTD_COUNTERMEASURE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.FTD_COUNTERMEASURE);
        } else if (command.equals(mgui.actions[TGUIAction.FTD_CONSTRAINT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.FTD_CONSTRAINT);
        } else if (command.equals(mgui.actions[TGUIAction.FTD_COMPOSITION_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.FTD_COMPOSITION_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.FTD_FAULT_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.FTD_FAULT_CONNECTOR);
	} else if (command.equals(mgui.actions[TGUIAction.FTD_COUNTERMEASURE_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.FTD_COUNTERMEASURE_CONNECTOR);

            // TURTLE-OS
        } else if (command.equals(mgui.actions[TGUIAction.TOS_TCLASS].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_TCLASS);
        } else if (command.equals(mgui.actions[TGUIAction.TOS_CONNECTOR_ATTRIBUTE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ATTRIBUTE);
        } else if (command.equals(mgui.actions[TGUIAction.TOS_ASSOCIATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ASSOCIATION);
        } else if (command.equals(mgui.actions[TGUIAction.TOS_ASSOCIATION_NAVIGATION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOS_CONNECTOR_ASSOCIATION_NAVIGATION);
        } else if (command.equals(mgui.actions[TGUIAction.TOS_CALL_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_CALL_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TOS_EVT_OPERATOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSCD_EVT_OPERATOR);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_ACTION_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_ACTION_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.TOSAD_CONNECTOR);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_INT_TIME_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_INT_TIME_INTERVAL);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_START_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_STOP_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.TOSAD_TIME_INTERVAL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TOSAD_TIME_INTERVAL);


            // Ontologies
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM].getActionCommand())) {
            mgui.generateOntologyForCurrentDiagram();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS].getActionCommand())) {
            mgui.generateOntologyForCurrentSetOfDiagrams();
        } else if (command.equals(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS].getActionCommand())) {
            mgui.generateOntologyForAllDiagrams();

            // Requirement diagrams
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_REQUIREMENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_REQUIREMENT);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_OBSERVER].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_OBSERVER);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_EBRDD].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.TREQ_EBRDD);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_DERIVE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_DERIVE_REQ);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_COPY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COPY_REQ);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_COMPOSITION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_COMPOSITION_REQ);
        } else if (command.equals(mgui.actions[TGUIAction.TREQ_VERIFY].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_VERIFY_REQ);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_START].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_START_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_SENDMSG].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_SENDMSG);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_GETMSG].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_GETMSG);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_CHOICE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_CHOICE);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_STOP].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_STOP_STATE);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROSMD);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_JUNCTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_JUNCTION);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_SUBMACHINE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_SUBMACHINE);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_ACTION].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_ACTION);
        } else if (command.equals(mgui.actions[TGUIAction.PROSMD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        }  else if (command.equals(mgui.actions[TGUIAction.PROSMD_PARALLEL].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_PARALLEL);
        }  else if (command.equals(mgui.actions[TGUIAction.PROSMD_STATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROSMD_STATE);
        }  else if (command.equals(mgui.actions[TGUIAction.PROCSD_COMPONENT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_COMPONENT);
            //Delegate ports removed, by Solange
            /*
              } else if (command.equals(mgui.actions[TGUIAction.PROCSD_DELEGATE_PORT].getActionCommand())) {
              mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_DELEGATE_PORT);
            */
        } else if (command.equals(mgui.actions[TGUIAction.PROCSD_IN_PORT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_IN_PORT);
        } else if (command.equals(mgui.actions[TGUIAction.PROCSD_OUT_PORT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_OUT_PORT);
        } else if (command.equals(mgui.actions[TGUIAction.PROCSD_EDIT].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.EDIT, -1);
        }  else if (command.equals(mgui.actions[TGUIAction.PROCSD_CONNECTOR].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROCSD);
        }
        else if (command.equals(mgui.actions[TGUIAction.PROCSD_CONNECTOR_PORT_INTERFACE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE);}
        else if (command.equals(mgui.actions[TGUIAction.PROCSD_CONNECTOR_DELEGATE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.CONNECTOR, TGComponentManager.CONNECTOR_DELEGATE_PROCSD);
        }  else if (command.equals(mgui.actions[TGUIAction.PROCSD_INTERFCE].getActionCommand())) {
            mgui.actionOnButton(TGComponentManager.COMPONENT, TGComponentManager.PROCSD_INTERFACE);
            // Command for the action created by Solange. Window appears.
        }   else if (command.equals(mgui.actions[TGUIAction.PRUEBA_1].getActionCommand())) {
            JOptionPane.showMessageDialog(mgui.getFrame(), "In Port: Color CYAN\nOut Port: Color LIGHT GRAY", "Help color of the ports", JOptionPane.INFORMATION_MESSAGE);
        }   else if (command.endsWith(".dot")) {
            mgui.viewAutomata(command);

            // Last open
        } else {
            for(int i=0; i<mgui.actionsLast.length; i++) {
                if (command.equals(mgui.actionsLast[i].getActionCommand())) {
                    mgui.openLastProject(i);
                    break;
                }
            }
        }
    }
}

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

import common.SpecConfigTTool;
import myutil.TraceManager;

/**
 * Class ModeManager Managing modes in TTool (icon/action activation) Created
 * for refactoring of MainGUI Creation: 19/02/2017
 *
 * @author Ludovic APVRILLE
 * @version 1.0 19/02/2017
 */
public class ModeManager {

    public static void setMode(byte mode, TGUIAction[] actions, JToolBarMainTurtle mainBar, MainGUI mgui) {
        // TraceManager.addDev("Setting mode=" + mode);
        switch (mode) {
        case MainGUI.CREATE_NEW_PANEL:
            actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(false);
            TraceManager.addDev("Deactivating syntax checking");
            break;
        case MainGUI.NOT_OPENED:
            mgui.activeActions(false);
            actions[TGUIAction.ACT_NEW].setEnabled(true);
            actions[TGUIAction.ACT_NEW_PROJECT].setEnabled(true);
            actions[TGUIAction.ACT_OPEN].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_PROJECT].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_FROM_NETWORK].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_TIF].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_SD].setEnabled(true);
            actions[TGUIAction.ACT_OPEN_LAST].setEnabled(true);
            actions[TGUIAction.ACT_QUIT].setEnabled(true);
            actions[TGUIAction.ACT_ABOUT].setEnabled(true);
            actions[TGUIAction.ACT_INTEGRATED_HELP].setEnabled(true);
            actions[TGUIAction.ACT_TTOOL_CONFIGURATION].setEnabled(true);
            actions[TGUIAction.ACT_TURTLE_WEBSITE].setEnabled(true);
            actions[TGUIAction.ACT_TURTLE_DOCUMENTATION].setEnabled(true);
            actions[TGUIAction.ACT_SYSMLSEC_DOCUMENTATION].setEnabled(true);
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
            // actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RG_DIPLODOCUS].setEnabled(SpecConfigTTool.GGraphPath != null);
            actions[TGUIAction.ACT_VIEW_STAT_AUTDIPLODOCUS].setEnabled(SpecConfigTTool.GGraphPath != null);
            if (mainBar != null) {
                mainBar.activateSearch(false);
            }
            break;
        case MainGUI.OPENED:
            actions[TGUIAction.ACT_MERGE].setEnabled(true);
            actions[TGUIAction.ACT_NEW_DESIGN].setEnabled(true);
            actions[TGUIAction.ACT_NEW_ANALYSIS].setEnabled(true);
            // actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(true);//DG 06.02.
            // actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG].setEnabled(true);
            // actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AS_PROJECT].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AS_MODEL].setEnabled(true);
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
            actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE_SVG].setEnabled(true);
            actions[TGUIAction.ACT_GEN_DOC].setEnabled(true);
            actions[TGUIAction.ACT_GEN_DOC_REQ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_BIRDEYES].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS].setEnabled(true);
            actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS].setEnabled(true);
            actions[TGUIAction.ACT_DELETE].setEnabled(false);
            actions[TGUIAction.ACT_SUPPR].setEnabled(false);
            actions[TGUIAction.MOVE_ENABLED].setEnabled(false);
            actions[TGUIAction.FIRST_DIAGRAM].setEnabled(true);

            if (mainBar != null) {
                mainBar.activateSearch(true);
            }
            // @author: Huy TRUONG
            actions[TGUIAction.ACT_EXTERNAL_SEARCH].setEnabled(true);
            // disable when there is no text in search textfield
            actions[TGUIAction.ACT_INTERNAL_SEARCH].setEnabled(false);
            // --
            break;
        case MainGUI.MODEL_OK:
            actions[TGUIAction.ACT_SAVE_TIF].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_DSE].setEnabled(true);
            actions[TGUIAction.ACT_DSE_Z3].setEnabled(true);
            actions[TGUIAction.ACT_REMOVENOC].setEnabled(true);
            if (mgui.getCurrentTURTLEPanel() instanceof TMLComponentDesignPanel) {
                actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);
            } else {
                actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            }
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(false);
            break;

        case MainGUI.MODEL_UPPAAL_OK:
            // actions[TGUIAction.ACT_SAVE_TIF].setEnabled(true);
            // actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            // actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);
            // actions[TGUIAction.ACT_GEN_JAVA].setEnabled(true);
            // actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(true);
            // actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(true);
            // actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            break;
        case MainGUI.EDIT_PROVERIF_OK:
            actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            break;
        case MainGUI.GEN_DESIGN_OK:
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(true);
            break;
        case MainGUI.GEN_SYSTEMC_OK:
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(true);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(true);
            actions[TGUIAction.ACT_GEN_CCODE].setEnabled(true);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(true);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(true);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(true);            
            break;
        case MainGUI.AVATAR_SYNTAXCHECKING_OK:
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(true);
            break;
        case MainGUI.ATTACKTREE_SYNTAXCHECKING_OK:
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(true);
            break;
        case MainGUI.FAULTTREE_SYNTAXCHECKING_OK:
            actions[TGUIAction.ACT_AVATAR_SIM].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_UPPAAL].setEnabled(true);
            actions[TGUIAction.ACT_AVATAR_FV_PROVERIF].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(true);
            break;
        case MainGUI.REQ_OK:
            // actions[TGUIAction.ACT_VIEW_MATRIX].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case MainGUI.RTLOTOS_OK:
            actions[TGUIAction.ACT_SAVE_LOTOS].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(true);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case MainGUI.UPPAAL_OK:
            // actions[TGUIAction.ACT_SAVE_LOTOS].setEnabled(true);
            // actions[TGUIAction.ACT_VIEW_RTLOTOS].setEnabled(true);
            // actions[TGUIAction.ACT_CHECKCODE].setEnabled(true);
            // actions[TGUIAction.ACT_SIMULATION].setEnabled(true);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(true);
            break;
        case MainGUI.MODEL_CHANGED:
            actions[TGUIAction.ACT_SAVE].setEnabled(true);
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_DSE].setEnabled(false);
            actions[TGUIAction.ACT_DSE_Z3].setEnabled(false);
            actions[TGUIAction.ACT_REMOVENOC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_CCODE].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(false);
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
        case MainGUI.METHO_CHANGED:
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_DSE].setEnabled(false);
            actions[TGUIAction.ACT_DSE_Z3].setEnabled(false);
            actions[TGUIAction.ACT_REMOVENOC].setEnabled(false);
            actions[TGUIAction.ACT_CHECKCODE].setEnabled(false);
            actions[TGUIAction.ACT_SIMULATION].setEnabled(false);
            actions[TGUIAction.ACT_VALIDATION].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_CCODE].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            // actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            break;

        case MainGUI.PANEL_CHANGED:
            actions[TGUIAction.ACT_GEN_RTLOTOS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_LOTOS].setEnabled(false);
            actions[TGUIAction.ACT_DSE].setEnabled(false);
            actions[TGUIAction.ACT_DSE_Z3].setEnabled(false);
            actions[TGUIAction.ACT_REMOVENOC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_SIMU_JAVA].setEnabled(false);
            actions[TGUIAction.ACT_GEN_DESIGN].setEnabled(false);
            actions[TGUIAction.ACT_GEN_SYSTEMC].setEnabled(false);
            actions[TGUIAction.ACT_GEN_TMLTXT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_CCODE].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUT].setEnabled(false);
            actions[TGUIAction.ACT_GEN_AUTS].setEnabled(false);
            actions[TGUIAction.ACT_GEN_UPPAAL].setEnabled(false);
            actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER].setEnabled(false);
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

        case MainGUI.MODEL_SAVED:
            TraceManager.addDev("Disable save");
            actions[TGUIAction.ACT_SAVE].setEnabled(false);
            break;
        case MainGUI.BACKWARD:
            actions[TGUIAction.ACT_BACKWARD].setEnabled(true);
            break;
        case MainGUI.NO_BACKWARD:
            actions[TGUIAction.ACT_BACKWARD].setEnabled(false);
            break;
        case MainGUI.FORWARD:
            actions[TGUIAction.ACT_FORWARD].setEnabled(true);
            break;
        case MainGUI.NO_FORWARD:
            actions[TGUIAction.ACT_FORWARD].setEnabled(false);
            break;
        case MainGUI.FORWARD_DIAG:
            actions[TGUIAction.ACT_NEXT_DIAG].setEnabled(true);
            actions[TGUIAction.ACT_LAST_DIAG].setEnabled(true);
            break;
        case MainGUI.BACKWARD_DIAG:
            actions[TGUIAction.ACT_FIRST_DIAG].setEnabled(true);
            actions[TGUIAction.ACT_BACK_DIAG].setEnabled(true);
            break;
        case MainGUI.NO_FORWARD_DIAG:
            actions[TGUIAction.ACT_NEXT_DIAG].setEnabled(false);
            actions[TGUIAction.ACT_LAST_DIAG].setEnabled(false);
            break;
        case MainGUI.NO_BACKWARD_DIAG:
            actions[TGUIAction.ACT_FIRST_DIAG].setEnabled(false);
            actions[TGUIAction.ACT_BACK_DIAG].setEnabled(false);
            break;
        case MainGUI.SIM_OK:
            actions[TGUIAction.ACT_VIEW_SIM].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_SIM_CHRONO].setEnabled(true);
            break;
        case MainGUI.SIM_KO:
            actions[TGUIAction.ACT_VIEW_SIM].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_SIM_CHRONO].setEnabled(false);
            break;
        case MainGUI.DTADOT_OK:
            actions[TGUIAction.ACT_SAVE_DTA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_DTADOT].setEnabled(true);
            break;
        case MainGUI.DTADOT_KO:
            actions[TGUIAction.ACT_SAVE_DTA].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_DTADOT].setEnabled(false);
            break;
        case MainGUI.RGDOT_OK:
            actions[TGUIAction.ACT_SAVE_RG].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGDOT].setEnabled(true);
            break;
        case MainGUI.RGDOT_KO:
            actions[TGUIAction.ACT_SAVE_RG].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGDOT].setEnabled(false);
            break;
        case MainGUI.TLSADOT_OK:
            actions[TGUIAction.ACT_SAVE_TLSA].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_TLSADOT].setEnabled(true);
            break;
        case MainGUI.TLSADOT_KO:
            actions[TGUIAction.ACT_SAVE_TLSA].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_TLSADOT].setEnabled(false);
            break;
        case MainGUI.CUTCOPY_OK:
            actions[TGUIAction.ACT_CUT].setEnabled(true);
            actions[TGUIAction.ACT_COPY].setEnabled(true);
            // actions[TGUIAction.ACT_DELETE].setEnabled(true);
            actions[TGUIAction.ACT_SELECTED_CAPTURE].setEnabled(true);
            break;
        case MainGUI.CUTCOPY_KO:
            actions[TGUIAction.ACT_CUT].setEnabled(false);
            actions[TGUIAction.ACT_COPY].setEnabled(false);
            // actions[TGUIAction.ACT_DELETE].setEnabled(false);
            actions[TGUIAction.ACT_SELECTED_CAPTURE].setEnabled(false);
            break;
        case MainGUI.PASTE_OK:
            actions[TGUIAction.ACT_PASTE].setEnabled(true);
            break;
        case MainGUI.RGAUTDOT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGAUTDOT].setEnabled(true);
            break;
        case MainGUI.RGAUTDOT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(false);
            actions[TGUIAction.ACT_SAVE_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGAUTDOT].setEnabled(false);
            break;
        case MainGUI.RGAUT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(true);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(true);
            actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].setEnabled(true);
            break;
        case MainGUI.RGAUT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUT].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUT].setEnabled(false);
            actions[TGUIAction.ACT_PROJECTION].setEnabled(false);
            actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT].setEnabled(false);
            break;
        case MainGUI.RGAUTPROJDOT_OK:
            actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_SAVE_AUTPROJ].setEnabled(true);
            actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].setEnabled(true);
            break;
        case MainGUI.RGAUTPROJDOT_KO:
            actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_PM_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_SAVE_AUTPROJ].setEnabled(false);
            actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT].setEnabled(false);
            break;
        case MainGUI.EXPORT_LIB_OK:
            actions[TGUIAction.ACT_EXPORT_LIB].setEnabled(true);
            break;
        case MainGUI.EXPORT_LIB_KO:
            actions[TGUIAction.ACT_EXPORT_LIB].setEnabled(false);
            break;
        case MainGUI.VIEW_SUGG_DESIGN_OK:
            actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].setEnabled(true);
            break;
        case MainGUI.VIEW_SUGG_DESIGN_KO:
            actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN].setEnabled(false);
            break;
        case MainGUI.NC_OK:
            actions[TGUIAction.ACT_NC].setEnabled(true);
            break;
        case MainGUI.COMPONENT_SELECTED:
            actions[TGUIAction.MOVE_ENABLED].setEnabled(true);
            actions[TGUIAction.ACT_DELETE].setEnabled(true);
            actions[TGUIAction.ACT_SUPPR].setEnabled(true);
            break;
        default:
            TraceManager.addDev("DEFAULT");
            mgui.activeActions(false);
        }

    }
}
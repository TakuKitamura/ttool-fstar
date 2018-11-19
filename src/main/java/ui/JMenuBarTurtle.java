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
import myutil.TraceManager;

import javax.swing.*;
//import javax.swing.event.*;
//import java.awt.*;
//import java.awt.event.*;


/**
 * Class JMenuBarTurtle
 * Creation: 09/12/2003
 * Version 1.0 09/12/2003
 * Version 1.5 11/10/2018
 * 
 * @author Ludovic APVRILLE, Arthur VUAGNIAUX
 */
public class JMenuBarTurtle extends JMenuBar {
    private JMenu menugraph;

    //Menu
    private JMenu file, saveLastGraph, diagram, cd, ad, iod, ucd, sd, edit, vAndV, codeG, view, tool, capture, ontologies, help;

    public JMenuBarTurtle(MainGUI mgui) {
        super();
        buildMenus(mgui);
    }

    public JMenu getJMenuGraph() {
        return menugraph;
    }

    public void addMenuItem(JMenu menu, String text, MainGUI mgui) {
        JMenuItem menuItem;

        menuItem = menu.add(text);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem.addActionListener(mgui);
    }


    public void makeFileMenu(MainGUI mgui) {
        JMenuItem menuItem;

        file.removeAll();

        // FILE
        menuItem = file.add(mgui.actions[TGUIAction.ACT_NEW]);
        menuItem.setName("File New");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_NEW_PROJECT]);
        menuItem.setName("File New Project");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN]);
        menuItem.setName("File Model Project");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_PROJECT]);
        menuItem.setName("File Open Project");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_FROM_NETWORK]);
        menuItem.setName("File Open From Network");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE]);
        menuItem.setName("File Save");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_AS_MODEL]);
        menuItem.setName("File Save As Model");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_AS_PROJECT]);
        menuItem.setName("File Save As Project");
        menuItem.addMouseListener(mgui.mouseHandler);

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_MERGE]);
        menuItem.setName("File Merge");
        menuItem.addMouseListener(mgui.mouseHandler);

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_TIF]);
        menuItem.setName("File Open Tif");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_TIF]);
        menuItem.setName("File Save Tif");
        menuItem.addMouseListener(mgui.mouseHandler);

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_SD]);
        menuItem.setName("File Open MSC");
        menuItem.addMouseListener(mgui.mouseHandler);

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_LOTOS]);
        menuItem.setName("File Save RT-LOTOS");
        menuItem.addMouseListener(mgui.mouseHandler);

        saveLastGraph = new JMenu("Save Last Graphs");
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_DTA]);
        menuItem.setName("File Save DTA");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_RG]);
        menuItem.setName("File Save RG");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_TLSA]);
        menuItem.setName("File Save TLSA");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUT]);
        menuItem.setName("File Save AUT");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUTPROJ]);
        menuItem.setName("File Save AUTPROJ");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUTMODIFIED]);
        menuItem.setName("File Save AUTMODIFIED");
        menuItem.addMouseListener(mgui.mouseHandler);
        file.add(saveLastGraph);

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_IMPORT_LIB]);
        menuItem.setName("File Import");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_EXPORT_LIB]);
        menuItem.setName("File Export");
        menuItem.addMouseListener(mgui.mouseHandler);

        if (ConfigurationTTool.LastOpenFileDefined) {
            file.addSeparator();
            for(int i=0; i<ConfigurationTTool.LastOpenFiles.length;i++){
                //TraceManager.addDev("Considering last open file: " + ConfigurationTTool.LastOpenFiles[i]);
                if ((ConfigurationTTool.LastOpenFiles[i] != null) && (ConfigurationTTool.LastOpenFiles[i].length() > 0)) {
                    menuItem = file.add(mgui.actionsLast[i]);
                    menuItem.addMouseListener(mgui.mouseHandler);
                }
            }
        }

        file.addSeparator();

        menuItem = file.add(mgui.actions[TGUIAction.ACT_QUIT]);
        menuItem.setName("File Quit");
        menuItem.addMouseListener(mgui.mouseHandler);
    }

    // Menus
    private void buildMenus(MainGUI mgui) {
        // Main menu
        file = new JMenu("File");
        edit = new JMenu("Edit");
        diagram = new JMenu("Diagram");
        vAndV = new JMenu("V&V");
        codeG = new JMenu("Code Generation");
        view = new JMenu("View");
        tool = new JMenu("Tool");
        help = new JMenu("Help");

        JMenuItem menuItem;
        makeFileMenu(mgui);

        //Edit
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_CUT]);
        menuItem.setName("Edit Cut");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_COPY]);
        menuItem.setName("Edit Copy");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_PASTE]);
        menuItem.setName("Edit Paste");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_DELETE]);
        menuItem.setName("Edit Delete");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_SUPPR]);
        menuItem.setName("Edit Suppr");
        menuItem.addMouseListener(mgui.mouseHandler);

        edit.addSeparator();

        menuItem = edit.add(mgui.actions[TGUIAction.ACT_BACKWARD]);
        menuItem.setName("Edit Undo");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_FORWARD]);
        menuItem.setName("Edit Redo");
        menuItem.addMouseListener(mgui.mouseHandler);

        edit.addSeparator();

        menuItem = edit.add(mgui.actions[TGUIAction.ACT_FIRST_DIAG]);
        menuItem.setName("Edit First Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_BACK_DIAG]);
        menuItem.setName("Edit Previous Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_NEXT_DIAG]);
        menuItem.setName("Edit Next Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_LAST_DIAG]);
        menuItem.setName("Edit Last Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);

        // DIAGRAM

        menuItem = diagram.add(mgui.actions[TGUIAction.ACT_NEW_ANALYSIS]);
        menuItem.setName("Diagram New Analysis");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = diagram.add(mgui.actions[TGUIAction.ACT_NEW_DESIGN]);
        menuItem.setName("Diagram New Design");
        menuItem.addMouseListener(mgui.mouseHandler);

        diagram.addSeparator();

        menuItem = diagram.add(mgui.actions[TGUIAction.UML_NOTE]);
        menuItem.setName("Diagram UML Note");
        menuItem.addMouseListener(mgui.mouseHandler);

        // Interaction Overview Diagram
        iod = new JMenu("Interaction Overview Diagram");
        diagram.add(iod);

        menuItem = iod.add(mgui.actions[TGUIAction.IOD_EDIT]);
        menuItem.setName("Iod Edit");
        menuItem.addMouseListener(mgui.mouseHandler);

        iod.addSeparator();

        menuItem = iod.add(mgui.actions[TGUIAction.IOD_CONNECTOR]);
        menuItem.setName("Iod Connector");
        menuItem.addMouseListener(mgui.mouseHandler);

        iod.addSeparator();

        menuItem = iod.add(mgui.actions[TGUIAction.IOD_START]);
        menuItem.setName("Iod Start");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_STOP]);
        menuItem.setName("Iod Stop");
        menuItem.addMouseListener(mgui.mouseHandler);

        iod.addSeparator();

        menuItem = iod.add(mgui.actions[TGUIAction.IOD_REF_SD]);
        menuItem.setName("Iod Ref");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_PARALLEL]);
        menuItem.setName("Iod Parallel");
        menuItem.addMouseListener(mgui.mouseHandler);
        /*menuItem = iod.add(mgui.actions[TGUIAction.IOD_SEQUENCE]);
          menuItem.addMouseListener(mgui.mouseHandler);
          menuItem = iod.add(mgui.actions[TGUIAction.IOD_PREEMPTION]);
          menuItem.addMouseListener(mgui.mouseHandler);*/
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_CHOICE]);
        menuItem.setName("Iod Choice");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_JUNCTION]);
        menuItem.setName("Iod Junction");
        menuItem.addMouseListener(mgui.mouseHandler);

        // Sequence Diagram
        sd = new JMenu("Sequence Diagram");
        diagram.add(sd);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_EDIT]);
        menuItem.setName("Sd Edit");
        menuItem.addMouseListener(mgui.mouseHandler);

        sd.addSeparator();

        menuItem = sd.add(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_SYNC]);
        menuItem.setName("Sd Connector Message Sync");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_ASYNC]);
        menuItem.setName("Sd Connector Message Async");
        menuItem.addMouseListener(mgui.mouseHandler);

        sd.addSeparator();

        menuItem = sd.add(mgui.actions[TGUIAction.SD_INSTANCE]);
        menuItem.setName("Sd Instance");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_ACTION_STATE]);
        menuItem.setName("Sd Action State");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_COREGION]);
        menuItem.setName("Sd Coregion");
        menuItem.addMouseListener(mgui.mouseHandler);

        sd.addSeparator();

        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_SETTING]);
        menuItem.setName("Sd Timer Setting");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_EXPIRATION]);
        menuItem.setName("Sd Timer Expiration");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_CANCELLATION]);
        menuItem.setName("Sd Timer Cancellation");
        menuItem.addMouseListener(mgui.mouseHandler);

        sd.addSeparator();

        menuItem = sd.add(mgui.actions[TGUIAction.SD_ABSOLUTE_TIME_CONSTRAINT]);
        menuItem.setName("Sd Absolute Time");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT]);
        menuItem.setName("Sd Relative Time");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = sd.add(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR]);
        menuItem.setName("Sd Relative Time Connector");
        menuItem.addMouseListener(mgui.mouseHandler);

        sd.addSeparator();

        menuItem = sd.add(mgui.actions[TGUIAction.SD_ALIGN_INSTANCES]);
        menuItem.setName("Sd Align Instances");
        menuItem.addMouseListener(mgui.mouseHandler);

        // Use case diagram
        ucd = new JMenu("Use Case Diagram");
        diagram.add(ucd);

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_EDIT]);
        menuItem.setName("Ucd Edit");
        menuItem.addMouseListener(mgui.mouseHandler);

        ucd.addSeparator();

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_ACTOR]);
        menuItem.setName("Ucd Actor");
        menuItem.addMouseListener(mgui.mouseHandler);

        ucd.addSeparator();

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_USECASE]);
        menuItem.setName("Ucd Usecase");
        menuItem.addMouseListener(mgui.mouseHandler);

        ucd.addSeparator();

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_ACTOR_UC]);
        menuItem.setName("Ucd Connector Actor");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_INCLUDE]);
        menuItem.setName("Ucd Connector Include");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_EXTEND]);
        menuItem.setName("Ucd Connector Extend");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_SPECIA]);
        menuItem.setName("Ucd Connector Special");
        menuItem.addMouseListener(mgui.mouseHandler);

        // CLASS DIAGRAM
        cd = new JMenu("Class Diagram");
        diagram.add(cd);

        menuItem = cd.add(mgui.actions[TGUIAction.TCD_EDIT]);
        menuItem.setName("Cd Edit");
        menuItem.addMouseListener(mgui.mouseHandler);

        cd.addSeparator();

        menuItem = cd.add(mgui.actions[TGUIAction.TCD_ASSOCIATION]);
        menuItem.setName("Cd Association");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_ASSOCIATION_NAVIGATION]);
        menuItem.setName("Cd Navigation");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_CONNECTOR_ATTRIBUTE]);
        menuItem.setName("Cd Connector Attribute");
        menuItem.addMouseListener(mgui.mouseHandler);

        cd.addSeparator();

        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TCLASS]);
        menuItem.setName("Cd New Class");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TOBJECT]);
        menuItem.setName("Cd New Object");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TDATA]);
        menuItem.setName("Cd New Data");
        menuItem.addMouseListener(mgui.mouseHandler);

        cd.addSeparator();

        menuItem = cd.add(mgui.actions[TGUIAction.TCD_PARALLEL_OPERATOR]);
        menuItem.setName("Cd Parallel Operator");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_SYNCHRO_OPERATOR]);
        menuItem.setName("Cd Syncho Operator");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_INVOCATION_OPERATOR]);
        menuItem.setName("Cd Invocation Operator");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_SEQUENCE_OPERATOR]);
        menuItem.setName("Cd Sequence Operator");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_PREEMPTION_OPERATOR]);
        menuItem.setName("Cd Preemption Operator");
        menuItem.addMouseListener(mgui.mouseHandler);

        // ACTIVITY DIAGRAM
        ad = new JMenu("Activity Diagram");
        diagram.add(ad);

        menuItem = ad.add(mgui.actions[TGUIAction.AD_EDIT]);
        menuItem.setName("Ad Edit");
        menuItem.addMouseListener(mgui.mouseHandler);

        ad.addSeparator();

        menuItem = ad.add(mgui.actions[TGUIAction.AD_CONNECTOR]);
        menuItem.setName("Ad Connector");
        menuItem.addMouseListener(mgui.mouseHandler);

        ad.addSeparator();

        menuItem = ad.add(mgui.actions[TGUIAction.AD_START]);
        menuItem.setName("Ad Start");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_STOP]);
        menuItem.setName("Ad Stop");
        menuItem.addMouseListener(mgui.mouseHandler);

        ad.addSeparator();

        menuItem = ad.add(mgui.actions[TGUIAction.AD_ACTION_STATE]);
        menuItem.setName("Ad Action State");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_PARALLEL]);
        menuItem.setName("Ad Parallel");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_SEQUENCE]);
        menuItem.setName("Ad Sequence");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_PREEMPTION]);
        menuItem.setName("Ad Preemption");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_CHOICE]);
        menuItem.setName("Ad Choice");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_JUNCTION]);
        menuItem.setName("Ad Junction");
        menuItem.addMouseListener(mgui.mouseHandler);

        ad.addSeparator();

        menuItem = ad.add(mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY]);
        menuItem.setName("Ad DDelay");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY]);
        menuItem.setName("Ad NDDelay");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY]);
        menuItem.setName("Ad DNDDelay");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER]);
        menuItem.setName("Ad Time Limited");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY]);
        menuItem.setName("Ad Time Limited Latency");
        menuItem.addMouseListener(mgui.mouseHandler);

        // V&V 
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_MODEL_CHECKING]);
        menuItem.setName("V&V Syntax Analysis");
        menuItem.addMouseListener(mgui.mouseHandler);

        vAndV.addSeparator();

        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);
        menuItem.setName("V&V Gen RTLOTOS");
        menuItem.addMouseListener(mgui.mouseHandler);


        if (MainGUI.lotosOn) {
            menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
            menuItem.setName("V&V Gen LOTOS");
            menuItem.addMouseListener(mgui.mouseHandler);
        }

        if (MainGUI.uppaalOn) {
            menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
            menuItem.setName("V&V Gen UPPAL");
            menuItem.addMouseListener(mgui.mouseHandler);
        }

        vAndV.addSeparator();

        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
        menuItem.setName("V&V Gen Design");
        menuItem.addMouseListener(mgui.mouseHandler);

        /*vAndV.addSeparator();

          menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
          menuItem.addMouseListener(mgui.mouseHandler);*/

        vAndV.addSeparator();

        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_CHECKCODE]);
        menuItem.setName("V&V Check");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_SIMULATION]);
        menuItem.setName("V&V Simulation");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VALIDATION]);
        menuItem.setName("V&V Validation");
        menuItem.addMouseListener(mgui.mouseHandler);

        vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_PROJECTION]);
        menuItem.setName("V&V Projection");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GRAPH_MODIFICATION]);
        menuItem.setName("V&V Graph Modification");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_BISIMULATION]);
        menuItem.setName("V&V Bisimulation Aldebaran");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_BISIMULATION_CADP]);
        menuItem.setName("V&V Bisimulation Bisimulator");
        menuItem.addMouseListener(mgui.mouseHandler);

        vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUT]);
        menuItem.setName("V&V Aut");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ]);
        menuItem.setName("V&V Autproj");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUTDIPLODOCUS]);
        menuItem.setName("V&V Autdiplodocus");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_SAVED_AUT]);
        menuItem.setName("V&V Saved Aut");
        menuItem.addMouseListener(mgui.mouseHandler);

        /*vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_AUTPROJ]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_SAVED_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);*/

        vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_AVATAR_SIM]);
        menuItem.setName("V&V Avatar Simulation");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_AVATAR_FV_UPPAAL]);
        menuItem.setName("V&V Avatar UPPAAL");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_AVATAR_FV_PROVERIF]);
        menuItem.setName("V&V ProVerif");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS]);
        menuItem.setName("V&V Avatar Static Analysis");
        menuItem.addMouseListener(mgui.mouseHandler);



        /*vAndV.addSeparator();
          menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT]);
          menuItem.addMouseListener(mgui.mouseHandler);
          menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_SAVED_AUT]);
          menuItem.addMouseListener(mgui.mouseHandler);*/

        // Code generation

        menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
        menuItem.setName("CodeG Java");
        menuItem.addMouseListener(mgui.mouseHandler);

        if (MainGUI.systemcOn) {
            menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);
            menuItem.setName("CodeG SystemC");
            menuItem.addMouseListener(mgui.mouseHandler);

            menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
            menuItem.setName("CodeG TMLTXT");
            menuItem.addMouseListener(mgui.mouseHandler);

            menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_CCODE]);
            menuItem.setName("CodeG C");
            menuItem.addMouseListener(mgui.mouseHandler);
        }

        menuItem = codeG.add(mgui.actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION]);
        menuItem.setName("CodeG Executable");
        menuItem.addMouseListener(mgui.mouseHandler);

        // View
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES_EMB]);
        menuItem.setName("View Hide Birdeye");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES]);
        menuItem.setName("View Show Birdeye");
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_JAVA]);
        menuItem.setName("View Java");
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RTLOTOS]);
        menuItem.setName("View RT-LOTOS");
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN]);
        menuItem.setName("View Suggested Design");
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SIM]);
        menuItem.setName("View Sim");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SIM_CHRONO]);
        menuItem.setName("View Sim Chrono");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_DTADOT]);
        menuItem.setName("View DTadot");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGDOT]);
        menuItem.setName("View RGdot");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_TLSADOT]);
        menuItem.setName("View TLSdot");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGAUTDOT]);
        menuItem.setName("View RGAutdot");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT]);
        menuItem.setName("View RGAutProjdot");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT]);
        menuItem.setName("View Modified Autdot");
        menuItem.addMouseListener(mgui.mouseHandler);

        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RG_DIPLODOCUS]);
        menuItem.setName("View Show Diplodocus");
        menuItem.addMouseListener(mgui.mouseHandler);

        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SAVED_LOT]);
        menuItem.setName("View Saved");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SAVED_DOT]);
        menuItem.setName("View Saved Graph");
        menuItem.addMouseListener(mgui.mouseHandler);

        /*if (MainGUI.systemcOn) {
          view.addSeparator();
          menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_WAVE]);
          menuItem.addMouseListener(mgui.mouseHandler);
          view.addSeparator();
          menugraph = new JMenu("View generated automata");
          view.add(menugraph);
          }*/

        // Tool
        capture = new JMenu("Capture");
        tool.add(capture);

        menuItem = capture.add(mgui.actions[TGUIAction.ACT_SCREEN_CAPTURE]);
        menuItem.setName("Capture Screen");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_TTOOL_WINDOW_CAPTURE]);
        menuItem.setName("Capture Window");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_DIAGRAM_CAPTURE]);
        menuItem.setName("Capture Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_SVG_DIAGRAM_CAPTURE]);
        menuItem.setName("Capture SVG Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE]);
        menuItem.setName("Capture All Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE_SVG]);
        menuItem.setName("Capture All Diagram SVG");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_SELECTED_CAPTURE]);
        menuItem.setName("Capture Selected");
        menuItem.addMouseListener(mgui.mouseHandler);


        ontologies = new JMenu("Ontologies");
        tool.add(ontologies);
        menuItem = ontologies.add(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM]);
        menuItem.setName("Ontologie Current Diagram");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ontologies.add(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS]);
        menuItem.setName("Ontologie Set of Diagrams");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ontologies.add(mgui.actions[TGUIAction.ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS]);
        menuItem.setName("Ontologie All Diagrams");
        menuItem.addMouseListener(mgui.mouseHandler);

        menuItem = tool.add(mgui.actions[TGUIAction.ACT_GEN_DOC]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem.setName("Tool Doc");
        menuItem = tool.add(mgui.actions[TGUIAction.ACT_GEN_DOC_REQ]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem.setName("Tool SysML Doc");

        if ((SpecConfigTTool.ExternalCommand1.length() > 0) && (ConfigurationTTool.ExternalCommand1Host.length() > 0)) {
            menuItem = tool.add(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
            menuItem.setName("Tool External Action");
            menuItem.addMouseListener(mgui.mouseHandler);
            menuItem.setToolTipText("Launch gtkwave with last vcd file");
        }

        if ((ConfigurationTTool.ExternalCommand2.length() > 0) && (ConfigurationTTool.ExternalCommand2Host.length() > 0)) {
            menuItem = tool.add(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
            menuItem.setName("Tool External Action2");
            menuItem.addMouseListener(mgui.mouseHandler);
            menuItem.setToolTipText(ConfigurationTTool.ExternalCommand2);
        }

        menuItem = tool.add(mgui.actions[TGUIAction.ACT_EXTERNAL_SEARCH]);
        menuItem.setName("Tool Search");
        menuItem.addMouseListener(mgui.mouseHandler);


        // HELP
        menuItem = help.add(mgui.actions[TGUIAction.ACT_TTOOL_CONFIGURATION]);
        menuItem.setName("Help Configuration");
        menuItem.addMouseListener(mgui.mouseHandler);
        help.addSeparator();
        menuItem = help.add(mgui.actions[TGUIAction.ACT_TURTLE_DOCUMENTATION]);
        menuItem.setName("Help Turtle Doc");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = help.add(mgui.actions[TGUIAction.ACT_DIPLODOCUS_DOCUMENTATION]);
        menuItem.setName("Help Diplodocus Doc");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = help.add(mgui.actions[TGUIAction.ACT_SYSMLSEC_DOCUMENTATION]);
        menuItem.setName("Help Sysmlsec Doc");
        menuItem.addMouseListener(mgui.mouseHandler);
        help.addSeparator();
        menuItem = help.add(mgui.actions[TGUIAction.ACT_TURTLE_WEBSITE]);
        menuItem.setName("Help Turtle");
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = help.add(mgui.actions[TGUIAction.ACT_ABOUT]);
        menuItem.setName("Help About");
        menuItem.addMouseListener(mgui.mouseHandler);

        // MenuBar
        add(file);
        add(edit);
        //add(diagram);
        add(vAndV);
        add(codeG);
        add(view);
        add(tool);
        add(help);
        
        //menuItem.setName("Menu Item");
        
    }
} // Class

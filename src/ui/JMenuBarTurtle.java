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
 * Class JMenuBarTurtle
 * Creation: 09/12/2003
 * Version 1.0 09/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import javax.swing.*;
//import javax.swing.event.*;
//import java.awt.*;
//import java.awt.event.*;


public	class JMenuBarTurtle extends JMenuBar	{
    private JMenu menugraph;
    
    //Menu
    private JMenu file, saveLastGraph, diagram, cd, ad, iod, ucd, sd, edit, vAndV, codeG, view, tool, capture, help;
    
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
    
    
    // Menus
    private	void buildMenus(MainGUI mgui) {
        // Main	menu
        file = new JMenu("File");
        edit = new JMenu("Edit");
        diagram = new JMenu("Diagram");
        vAndV =	new JMenu("V&V");
        codeG =	new JMenu("Code Generation");
        view = new JMenu("View");
        tool = new JMenu("Tool");
        help = new JMenu("Help");
        
        JMenuItem menuItem;
        
        // FILE
        menuItem = file.add(mgui.actions[TGUIAction.ACT_NEW]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_AS]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		file.addSeparator();
		
		menuItem = file.add(mgui.actions[TGUIAction.ACT_MERGE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        file.addSeparator();
		
		menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_TIF]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_TIF]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		file.addSeparator();
		
		menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_SD]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		file.addSeparator();
        
        menuItem = file.add(mgui.actions[TGUIAction.ACT_SAVE_LOTOS]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        saveLastGraph = new JMenu("Save Last Graphs");
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_DTA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_RG]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_TLSA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUTPROJ]);
        menuItem.addMouseListener(mgui.mouseHandler);
		menuItem = saveLastGraph.add(mgui.actions[TGUIAction.ACT_SAVE_AUTMODIFIED]);
        menuItem.addMouseListener(mgui.mouseHandler);
        file.add(saveLastGraph);
        
        file.addSeparator();
        
        menuItem = file.add(mgui.actions[TGUIAction.ACT_IMPORT_LIB]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = file.add(mgui.actions[TGUIAction.ACT_EXPORT_LIB]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        if (ConfigurationTTool.LastOpenFileDefined) {
            file.addSeparator();
        
            menuItem = file.add(mgui.actions[TGUIAction.ACT_OPEN_LAST]);
            menuItem.addMouseListener(mgui.mouseHandler);
        }
        
        file.addSeparator();
        
        menuItem = file.add(mgui.actions[TGUIAction.ACT_QUIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        //Edit
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_CUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_COPY]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_PASTE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_DELETE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        edit.addSeparator();
        
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_BACKWARD]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_FORWARD]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        edit.addSeparator();
        
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_FIRST_DIAG]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_BACK_DIAG]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_NEXT_DIAG]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = edit.add(mgui.actions[TGUIAction.ACT_LAST_DIAG]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // DIAGRAM
        
        menuItem = diagram.add(mgui.actions[TGUIAction.ACT_NEW_ANALYSIS]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = diagram.add(mgui.actions[TGUIAction.ACT_NEW_DESIGN]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        diagram.addSeparator();
        
        menuItem = diagram.add(mgui.actions[TGUIAction.UML_NOTE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // Interaction Overview Diagram
        iod = new JMenu("Interaction Overview Diagram");
        diagram.add(iod);
        
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_EDIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        iod.addSeparator();
        
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_CONNECTOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        iod.addSeparator();
        
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_START]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_STOP]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        iod.addSeparator();
        
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_REF_SD]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_PARALLEL]);
        menuItem.addMouseListener(mgui.mouseHandler);
        /*menuItem = iod.add(mgui.actions[TGUIAction.IOD_SEQUENCE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_PREEMPTION]);
        menuItem.addMouseListener(mgui.mouseHandler);*/
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_CHOICE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = iod.add(mgui.actions[TGUIAction.IOD_JUNCTION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // Sequence Diagram
        sd = new JMenu("Sequence Diagram");
        diagram.add(sd);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_EDIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        sd.addSeparator();
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_SYNC]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_CONNECTOR_MESSAGE_ASYNC]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        sd.addSeparator();
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_INSTANCE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_ACTION_STATE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_COREGION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        sd.addSeparator();
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_SETTING]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_EXPIRATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_TIMER_CANCELLATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        sd.addSeparator();   
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_ABSOLUTE_TIME_CONSTRAINT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = sd.add(mgui.actions[TGUIAction.SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        sd.addSeparator();
        
         menuItem = sd.add(mgui.actions[TGUIAction.SD_ALIGN_INSTANCES]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // Use case diagram
        ucd = new JMenu("Use Case Diagram");
        diagram.add(ucd);
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_EDIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ucd.addSeparator();
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_ACTOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ucd.addSeparator();
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_USECASE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ucd.addSeparator();
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_ACTOR_UC]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_INCLUDE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_EXTEND]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        menuItem = ucd.add(mgui.actions[TGUIAction.UCD_CONNECTOR_SPECIA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // CLASS DIAGRAM
        cd = new JMenu("Class Diagram");
        diagram.add(cd);
        
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_EDIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        cd.addSeparator();
        
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_ASSOCIATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_ASSOCIATION_NAVIGATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_CONNECTOR_ATTRIBUTE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        cd.addSeparator();
        
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TCLASS]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TOBJECT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_NEW_TDATA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        cd.addSeparator();
        
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_PARALLEL_OPERATOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_SYNCHRO_OPERATOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_INVOCATION_OPERATOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_SEQUENCE_OPERATOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = cd.add(mgui.actions[TGUIAction.TCD_PREEMPTION_OPERATOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // ACTIVITY DIAGRAM
        ad = new JMenu("Activity Diagram");
        diagram.add(ad);
        
        menuItem = ad.add(mgui.actions[TGUIAction.AD_EDIT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ad.addSeparator();
        
        menuItem = ad.add(mgui.actions[TGUIAction.AD_CONNECTOR]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ad.addSeparator();
        
        menuItem = ad.add(mgui.actions[TGUIAction.AD_START]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_STOP]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ad.addSeparator();
        
        menuItem = ad.add(mgui.actions[TGUIAction.AD_ACTION_STATE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_PARALLEL]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_SEQUENCE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_PREEMPTION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_CHOICE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_JUNCTION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        ad.addSeparator();
        
        menuItem = ad.add(mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = ad.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // V&V
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_MODEL_CHECKING]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        vAndV.addSeparator();
        
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		if (MainGUI.lotosOn) {
			menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
			menuItem.addMouseListener(mgui.mouseHandler);
		}
		
		if (MainGUI.uppaalOn) {
			menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
			menuItem.addMouseListener(mgui.mouseHandler);
		}
        
        vAndV.addSeparator();
        
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        /*vAndV.addSeparator();
        
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
        menuItem.addMouseListener(mgui.mouseHandler);*/
        
        vAndV.addSeparator();
        
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_CHECKCODE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_SIMULATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VALIDATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_PROJECTION]);
        menuItem.addMouseListener(mgui.mouseHandler);
		menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_GRAPH_MODIFICATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_BISIMULATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
		menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_BISIMULATION_CADP]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_AUTPROJ]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_STAT_SAVED_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_AUTPROJ]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_VIEW_PM_SAVED_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);

        
        /*vAndV.addSeparator();
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = vAndV.add(mgui.actions[TGUIAction.ACT_DEADLOCK_SEEKER_SAVED_AUT]);
        menuItem.addMouseListener(mgui.mouseHandler);*/
        
        // Code generation
       
        menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        if (MainGUI.systemcOn) {
            menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);
            menuItem.addMouseListener(mgui.mouseHandler);
			
			menuItem = codeG.add(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
            menuItem.addMouseListener(mgui.mouseHandler);
        }
        
        // View
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES_EMB]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_BIRDEYES]);
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_JAVA]);
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RTLOTOS]);
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SUGGESTED_DESIGN]);
        menuItem.addMouseListener(mgui.mouseHandler);
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SIM]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SIM_CHRONO]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_DTADOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGDOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_TLSADOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGAUTDOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_RGAUTPROJDOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
		menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_MODIFIEDAUTDOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        view.addSeparator();
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SAVED_LOT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = view.add(mgui.actions[TGUIAction.ACT_VIEW_SAVED_DOT]);
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
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_TTOOL_WINDOW_CAPTURE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_DIAGRAM_CAPTURE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_ALL_DIAGRAM_CAPTURE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = capture.add(mgui.actions[TGUIAction.ACT_SELECTED_CAPTURE]);
        menuItem.addMouseListener(mgui.mouseHandler);
		
		menuItem = tool.add(mgui.actions[TGUIAction.ACT_GEN_DOC]);
		menuItem.addMouseListener(mgui.mouseHandler);
		
		menuItem = tool.add(mgui.actions[TGUIAction.ACT_GEN_DOC_REQ]);
		menuItem.addMouseListener(mgui.mouseHandler);
		
		if ((ConfigurationTTool.ExternalCommand1.length() > 0) && (ConfigurationTTool.ExternalCommand1Host.length() > 0)) {
			menuItem = tool.add(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
			menuItem.addMouseListener(mgui.mouseHandler);
			menuItem.setToolTipText(ConfigurationTTool.ExternalCommand1);
        }
        
        if ((ConfigurationTTool.ExternalCommand2.length() > 0) && (ConfigurationTTool.ExternalCommand2Host.length() > 0)) {
			menuItem = tool.add(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
			menuItem.addMouseListener(mgui.mouseHandler);
			menuItem.setToolTipText(ConfigurationTTool.ExternalCommand2);
        }
        
        // HELP
        menuItem = help.add(mgui.actions[TGUIAction.ACT_TURTLE_DOCUMENTATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
		menuItem = help.add(mgui.actions[TGUIAction.ACT_DIPLODOCUS_DOCUMENTATION]);
        menuItem.addMouseListener(mgui.mouseHandler);
        help.addSeparator();
        menuItem = help.add(mgui.actions[TGUIAction.ACT_TURTLE_WEBSITE]);
        menuItem.addMouseListener(mgui.mouseHandler);
        menuItem = help.add(mgui.actions[TGUIAction.ACT_ABOUT]);
        menuItem.addMouseListener(mgui.mouseHandler);
        
        // MenuBar
        add(file);
        add(edit);
        add(diagram);
        add(vAndV);
        add(codeG);
        add(view);
        add(tool);
        add(help);
    }
} // Class


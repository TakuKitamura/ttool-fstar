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
* Class JToolBarMainTurtle
* Main toolbar of the ttool main window
* Creation: 09/12/2003
* @author Ludovic APVRILLE
* @see
*/

package ui;

import javax.swing.*;
//import javax.swing.event.*;
//import java.awt.*;
//import java.awt.event.*;

/**
* Class	*
* @author Ludovic APVRILLE
* @see	*/

public	class JToolBarMainTurtle extends JToolBar	{
    
    public JToolBarMainTurtle(MainGUI mgui) {
        super();
        buildToolBar(mgui);
    }
    
    // Menus
    private	void buildToolBar(MainGUI mgui) {
        JButton button;
        
        button = add(mgui.actions[TGUIAction.ACT_NEW]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_OPEN]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_SAVE]);
        button.addMouseListener(mgui.mouseHandler);
        //button = add(mgui.actions[TGUIAction.ACT_SAVE_AS]);
        //button.addMouseListener(mgui.mouseHandler);
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_IMPORT_LIB]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_EXPORT_LIB]);
        button.addMouseListener(mgui.mouseHandler);
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_CUT]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_COPY]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_PASTE]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_DELETE]);
        button.addMouseListener(mgui.mouseHandler);
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_BACKWARD]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_FORWARD]);
        button.addMouseListener(mgui.mouseHandler);
		
		addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_ZOOM_LESS]);
        button.addMouseListener(mgui.mouseHandler);
		button = add(mgui.actions[TGUIAction.ACT_SHOW_ZOOM]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_ZOOM_MORE]);
        button.addMouseListener(mgui.mouseHandler);
		//button.setMinimumSize(button.getSize());
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_FIRST_DIAG]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_BACK_DIAG]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_NEXT_DIAG]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_LAST_DIAG]);
        button.addMouseListener(mgui.mouseHandler);
        
        addSeparator();
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_MODEL_CHECKING]);
        button.addMouseListener(mgui.mouseHandler);
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
        button.addMouseListener(mgui.mouseHandler);
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);
        button.addMouseListener(mgui.mouseHandler);
        if (MainGUI.systemcOn) {
			button = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
			button.addMouseListener(mgui.mouseHandler);
			/*addSeparator();
			button = add(mgui.actions[TGUIAction.ACT_GEN_AUTS]);
			button.addMouseListener(mgui.mouseHandler);
			button = add(mgui.actions[TGUIAction.ACT_GEN_AUT]);
			button.addMouseListener(mgui.mouseHandler);*/
        } else if (MainGUI.lotosOn) {
            button = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
            button.addMouseListener(mgui.mouseHandler);
        }
        
        if (MainGUI.uppaalOn) {
			button = add(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
			button.addMouseListener(mgui.mouseHandler);
        }
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_CHECKCODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_SIMULATION]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_VALIDATION]);
        button.addMouseListener(mgui.mouseHandler);
		
		addSeparator();
		
		button = add(mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG]);
        button.addMouseListener(mgui.mouseHandler);
		if (MainGUI.lotosOn) {
			button = add(mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG]);
			button.addMouseListener(mgui.mouseHandler);
		}
        
        addSeparator();
        
        button = add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
        button.addMouseListener(mgui.mouseHandler);
		//button = add(mgui.actions[TGUIAction.ACT_SIMU_JAVA]);
        //button.addMouseListener(mgui.mouseHandler);
		
		addSeparator();
		
		if (MainGUI.ncOn) {
			button = add(mgui.actions[TGUIAction.ACT_NC]);
			button.addMouseListener(mgui.mouseHandler);
		}
        
        addSeparator();
		
        if (MainGUI.systemcOn) {
            button = add(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);
            button.addMouseListener(mgui.mouseHandler);
            addSeparator();
			
			button = add(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
            button.addMouseListener(mgui.mouseHandler);
            addSeparator();
        }
		
        
        if ((ConfigurationTTool.ExternalCommand1.length() > 0) && (ConfigurationTTool.ExternalCommand1Host.length() > 0)) {
			button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
			button.addMouseListener(mgui.mouseHandler);
			button.setToolTipText(ConfigurationTTool.ExternalCommand1);
			addSeparator();
        }
        
        if ((ConfigurationTTool.ExternalCommand2.length() > 0) && (ConfigurationTTool.ExternalCommand2Host.length() > 0)) {
			button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
			button.addMouseListener(mgui.mouseHandler);
			button.setToolTipText(ConfigurationTTool.ExternalCommand2);
			addSeparator();
        }
        
		
        
        
    }
} // Class

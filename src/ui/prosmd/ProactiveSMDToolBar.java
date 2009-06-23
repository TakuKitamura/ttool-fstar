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
 * Class ProactiveSMDToolBar
 * Toolbar associated with ProActive state machine diagrams
 * Creation: 05/07/2006
 * @version 1.1 11/07/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see
 */

package ui.prosmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import ui.*;

public class ProactiveSMDToolBar extends TToolBar {
    
    
    public ProactiveSMDToolBar(MainGUI _mgui) {
        super(_mgui);
    }
    
    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.PROSMD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_SENDMSG].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_GETMSG].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_CHOICE].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_SUBMACHINE].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_ACTION].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_PARALLEL].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_STATE].setEnabled(b);  
        /*mgui.actions[TGUIAction.AD_SEQUENCE].setEnabled(b);
        mgui.actions[TGUIAction.AD_PREEMPTION].setEnabled(b);
        mgui.actions[TGUIAction.AD_CHOICE].setEnabled(b);*/
        mgui.actions[TGUIAction.PROSMD_START].setEnabled(b);
        mgui.actions[TGUIAction.PROSMD_STOP].setEnabled(b);
        /*mgui.actions[TGUIAction.AD_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY].setEnabled(b);
        mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER].setEnabled(b);
        mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_JAVA].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ENHANCE].setEnabled(b);*/
		
		mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(false);
		mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(false);
		mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);
		mgui.updateZoomInfo();
    }
    
    protected void setButtons() {
        JButton button;
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_START]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_STOP]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_SENDMSG]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_GETMSG]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_JUNCTION]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_SUBMACHINE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_ACTION]);
        button.addMouseListener(mgui.mouseHandler);
        					
        button = this.add(mgui.actions[TGUIAction.PROSMD_PARALLEL]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.PROSMD_STATE]);
        button.addMouseListener(mgui.mouseHandler);
        
        /*button = this.add(mgui.actions[TGUIAction.AD_PARALLEL]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_SEQUENCE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_PREEMPTION]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_JUNCTION]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.AD_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_NON_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_DELAY_NON_DETERMINISTIC_DELAY]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.AD_TIME_LIMITED_OFFER_WITH_LATENCY]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_JAVA]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.ACT_ENHANCE]);
        button.addMouseListener(mgui.mouseHandler);*/
    }
} // Class






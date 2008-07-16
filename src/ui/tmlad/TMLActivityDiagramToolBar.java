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
 * Class TMLActivityDiagramToolBar
 * Toolbar associated with TML activity diagrams
 * Creation: 28/10/2005
 * @version 1.0 28/10/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlad;

import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;

import ui.*;

public class TMLActivityDiagramToolBar extends TToolBar {
    
    
    public TMLActivityDiagramToolBar(MainGUI _mgui) {
        super(_mgui);
    }
    
    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.TMLAD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_START].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_STOP].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_ACTION_STATE].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_JUNCTION].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_CHOICE].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_SELECT_EVT].setEnabled(b);
		mgui.actions[TGUIAction.TMLAD_RANDOM].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_EXECI].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_EXECI_INTERVAL].setEnabled(b);
		mgui.actions[TGUIAction.TMLAD_EXECC].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_EXECC_INTERVAL].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_WRITE_CHANNEL].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_READ_CHANNEL].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_SEND_REQUEST].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_SEND_EVENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_WAIT_EVENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_NOTIFIED_EVENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_FOR_LOOP].setEnabled(b);
		mgui.actions[TGUIAction.TMLAD_FOR_STATIC_LOOP].setEnabled(b);
        mgui.actions[TGUIAction.TMLAD_SEQUENCE].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ENHANCE].setEnabled(b);
		mgui.actions[TGUIAction.ACT_TOGGLE_INTERNAL_COMMENT].setEnabled(b);
    }
    
    protected void setButtons() {
        JButton button;
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_START]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_STOP]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_WRITE_CHANNEL]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_SEND_EVENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_SEND_REQUEST]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_READ_CHANNEL]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_WAIT_EVENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_NOTIFIED_EVENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_ACTION_STATE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_CHOICE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_SELECT_EVT]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_FOR_LOOP]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.TMLAD_FOR_STATIC_LOOP]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_SEQUENCE]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.TMLAD_RANDOM]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.TMLAD_EXECI]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLAD_EXECI_INTERVAL]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.TMLAD_EXECC]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLAD_EXECC_INTERVAL]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.ACT_ENHANCE]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_INTERNAL_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);
       
    }
} // Class






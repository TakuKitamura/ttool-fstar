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
 * Class AvatarRDToolBar
 * Implements the toolbar to be used in conjunction with the Avatar Requirement Diagram
* Creation: 20/04/2010
* @version 1.0 20/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarrd;

import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;

import ui.*;

public class AvatarRDToolBar extends TToolBar {
    
    public AvatarRDToolBar(MainGUI _mgui) {
        super(_mgui);
        
    }
    
    protected void setActive(boolean b) {
		mgui.actions[TGUIAction.ACT_ZOOM_MORE].setEnabled(b);
		mgui.actions[TGUIAction.ACT_ZOOM_LESS].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(b);
		mgui.updateZoomInfo();
		
        mgui.actions[TGUIAction.ARD_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.ARD_REQUIREMENT].setEnabled(b);
        mgui.actions[TGUIAction.ARD_PROPERTY].setEnabled(b);
        mgui.actions[TGUIAction.ARD_VERIFY_CONNECTOR].setEnabled(b);
        mgui.actions[TGUIAction.ARD_DERIVE_CONNECTOR].setEnabled(b);
		mgui.actions[TGUIAction.ARD_REFINE_CONNECTOR].setEnabled(b);
		mgui.actions[TGUIAction.ARD_COPY_CONNECTOR].setEnabled(b);
		mgui.actions[TGUIAction.ARD_COMPOSITION_CONNECTOR].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_TOGGLE_ATTR].setEnabled(b);
		
		mgui.actions[TGUIAction.ACT_ENHANCE].setEnabled(b);
        
    }
    
    protected void setButtons() {
        JButton button;
        
        button = this.add(mgui.actions[TGUIAction.ARD_EDIT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.ARD_REQUIREMENT]);
        button.addMouseListener(mgui.mouseHandler);
        
        button = this.add(mgui.actions[TGUIAction.ARD_PROPERTY]);
        button.addMouseListener(mgui.mouseHandler);
        
        this.addSeparator();
        
		button = this.add(mgui.actions[TGUIAction.ARD_COMPOSITION_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
		
        button = this.add(mgui.actions[TGUIAction.ARD_DERIVE_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
		
		button = this.add(mgui.actions[TGUIAction.ARD_COPY_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ARD_REFINE_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
        
        button = this.add(mgui.actions[TGUIAction.ARD_VERIFY_CONNECTOR]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
		
		button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
        button.addMouseListener(mgui.mouseHandler);
		
		this.addSeparator();
         
        button = this.add(mgui.actions[TGUIAction.ACT_ENHANCE]);
        button.addMouseListener(mgui.mouseHandler);
        
    }
    
} // Class






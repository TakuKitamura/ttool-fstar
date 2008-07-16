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
 * Class TMLComponentDesignPanel
 * Managenemt of TML component-based design panels
 * Creation: 10/03/2008
 * @version 1.0 10/03/2008
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
 
package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
//import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmlad.*;
import ui.tmldd.*;

public class TMLComponentDesignPanel extends TURTLEPanel {
    public TMLComponentTaskDiagramPanel tmlctdp; 
    public Vector validated, ignored;
    
    public TMLComponentDesignPanel(MainGUI _mgui) {
        super(_mgui);
        tabbedPane = new JTabbedPane();
        cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e){
                mgui.paneDesignAction(e);
            }
        };
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }
    
   public TMLActivityDiagramPanel getTMLActivityDiagramPanel(String name) {
        TMLActivityDiagramPanel tmladp;
        for(int i=1; i<panels.size(); i++) {
            tmladp = (TMLActivityDiagramPanel)(panels.elementAt(i));
            if (tmladp.getName().compareTo(name) ==0) {
                return tmladp;
            }
        }
        return null;
    }
	
	public TMLActivityDiagramPanel getReferencedTMLActivityDiagramPanel(String name) {
		System.out.println("Searching for activity diagram of:" + name);
        TMLActivityDiagramPanel tmladp = getTMLActivityDiagramPanel(name);
		if (tmladp != null) {
			System.out.println("Locally found");
			return tmladp;
		}
        
		// Search on other tabs
		return mgui.getReferencedTMLActivityDiagramPanel(name);
    }
    
    public void addTMLActivityDiagram(String s) {
		//System.out.println("Adding TML diagram panel = " + s);
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        TMLActivityDiagramToolBar toolBarActivity	= new TMLActivityDiagramToolBar(mgui);
        toolbars.add(toolBarActivity);
        
        TMLActivityDiagramPanel tmladp = new TMLActivityDiagramPanel(mgui, toolBarActivity);
        tmladp.tp = this;
        tmladp.setName(s);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(tmladp);
        tmladp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(tmladp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens the activity diagram of " + s);
   
        return;
    }
    
    public void init() {
         
        // Toolbar
        TMLComponentTaskDiagramToolBar toolBarTML = new TMLComponentTaskDiagramToolBar(mgui);
        toolbars.add(toolBarTML);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        // Diagram
        tmlctdp = new TMLComponentTaskDiagramPanel(mgui, toolBarTML);
        tmlctdp.setName("TML Component Task Diagram");
        tmlctdp.tp = this;
        tdp = tmlctdp;
        panels.add(tmlctdp); // Always first in list
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(tmlctdp);
        tmlctdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarTML, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("TML Component Task Diagram", IconManager.imgic1208, toolBarPanel, "Opens TML component task diagram");
        tabbedPane.setSelectedIndex(0);
        
        //jsp.setVisible(true);
 
    }
    
    public String saveHeaderInXml() {
        return "<Modeling type=\"TML Component Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n"; 
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return "TML Component Design: " + mgui.getTitleAt(this);
    }
	
	public ArrayList<String> getAllNonMappedTMLPrimitiveComponentNames(String _name, TMLArchiDiagramPanel _tadp, boolean ref, String name) {
		return tmlctdp.getAllNonMappedTMLPrimitiveComponentNames(_name, _tadp, ref, name);
	}
	
	public TMLCPrimitiveComponent getPrimitiveComponentByName(String _name) {
		return tmlctdp.getPrimitiveComponentByName(_name);
	}
	
	public ArrayList<String> getAllCompositeComponent(String _name) {
		return tmlctdp.getAllCompositeComponent(_name);
	}
	
}
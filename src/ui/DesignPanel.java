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
 * Class DesignPanel
 * Managenemt of design panels
 * Creation: 14/01/2005
 * @version 1.0 14/01/2005
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
 
package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import ui.cd.*;
import ui.ad.*;

public class DesignPanel extends TURTLEPanel implements TURTLEDesignPanelInterface {
    public TClassDiagramPanel tcdp; 
    public Vector validated, ignored;


    public DesignPanel(MainGUI _mgui) {
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

    public TActivityDiagramPanel getActivityDiagramPanel(String name) {
        TActivityDiagramPanel tadp;
        for(int i=1; i<panels.size(); i++) {
            tadp = (TActivityDiagramPanel)(panels.elementAt(i));
            if (tadp.getName().compareTo(name) ==0) {
                return tadp;
            }
        }
        return null;
    }

    public ActivityDiagramPanelInterface getBehaviourPanel(String name) {
           return (ActivityDiagramPanelInterface)(getActivityDiagramPanel(name));
    }

    public ClassDiagramPanelInterface getStructurePanel() {
           return (ClassDiagramPanelInterface)tcdp;
    }
    
    public void addTActivityDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        TActivityDiagramToolBar toolBarActivity	= new TActivityDiagramToolBar(mgui);
        toolbars.add(toolBarActivity);
        
        TActivityDiagramPanel tadp = new TActivityDiagramPanel(mgui, toolBarActivity);
        tadp.tp = this;
        tadp.setName(s);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(tadp);
        tadp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(tadp);
        tabbedPane.addTab(s, IconManager.imgic15, toolBarPanel, "Opens the activity diagram of " + s);
        //tabbedPane.setVisible(true);
        //tadp.setVisible(true);
        //jsp.setVisible(true);
        //tabbedPane.setSelectedIndex(panels.size()-1);
        
        return;
    }
    
    public void init() {
         
        //  Class Diagram toolbar
        TClassDiagramToolBar toolBarClass = new TClassDiagramToolBar(mgui);
        toolbars.add(toolBarClass);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        //Class	diagram
        tcdp = new TClassDiagramPanel(mgui, toolBarClass);
        tcdp.setName("Class Diagram");
        tcdp.tp = this;
        tdp = tcdp;
        panels.add(tcdp); // Always first in list
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(tcdp);
        tcdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarClass, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("Class Diagram", IconManager.imgic14, toolBarPanel, "Opens class diagram");
        tabbedPane.setSelectedIndex(0);
        
        //jsp.setVisible(true);
 
    }
    
    public String saveHeaderInXml() {
        return "<Modeling type=\"Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n"; 
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return "TURTLE Design: " + mgui.getTitleAt(this);
    }
    
    public TCDTClass getTCDTClass(String name) {
        return tcdp.getTCDTClass(name);
    }
}
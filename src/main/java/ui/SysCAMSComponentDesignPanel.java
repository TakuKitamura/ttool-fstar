/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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

import myutil.GraphicLib;
import ui.syscams.*;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * Class SysCAMSComponentDesignPanel
 * Managenemt of SystemC-AMS component-based design panels
 * Creation: 22/04/2018
 * @version 1.0 22/04/2018
 * @author Irina Kit Yan LEE
 * @see MainGUI
 */

public class SysCAMSComponentDesignPanel extends TURTLEPanel {
    
	public SysCAMSComponentTaskDiagramPanel syscamsctdp;
    
	public Vector<TGComponent> validated, ignored;

    public SysCAMSComponentDesignPanel(MainGUI _mgui) {
        super(_mgui);
        
    	// Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        
        cl = new ChangeListener() {
        	
        	@Override
        	public void stateChanged(ChangeEvent e){
        		mgui.paneDesignAction(e);
        	}
        };
        
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
        
        // Issue #41: Ordering of tabbed panes
        tabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
    }

    public void init() {

        // Toolbar
        SysCAMSComponentTaskDiagramToolBar toolBarSysCAMS = new SysCAMSComponentTaskDiagramToolBar(mgui);
        toolbars.add(toolBarSysCAMS);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        // Diagram
        syscamsctdp = new SysCAMSComponentTaskDiagramPanel(mgui, toolBarSysCAMS);
        syscamsctdp.setName("SystemC-AMS Component Diagram");
        syscamsctdp.tp = this;
        tdp = syscamsctdp;
        panels.add(syscamsctdp); // Always first in list
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(syscamsctdp);
        syscamsctdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarSysCAMS, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("SystemC-AMS Component Diagram", IconManager.imgic1208, toolBarPanel, "SysCAMS Component Diagram");
        tabbedPane.setSelectedIndex(0);

        mgui.changeMade(syscamsctdp, TDiagramPanel.NEW_COMPONENT);
    }

     public String saveHeaderInXml(String extensionToName) {
	 if (extensionToName == null) {
	     return "<Modeling type=\"SystemC-AMS\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	 }
	 return "<Modeling type=\"SystemC-AMS\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
	 
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) +  "(SystemC-AMS Component Application diagram)";
    }

    public SysCAMSPrimitiveComponent getPrimitiveComponentByName(String _name) {
        return syscamsctdp.getPrimitiveComponentByName(_name);
    }
//
//	public String[] getCompOutChannels(){
//		return syscamsctdp.getCompOutChannels();
//	}
//
//	public String[] getCompInChannels(){
//		return syscamsctdp.getCompInChannels();
//	}
//
//    public java.util.List<String> getAllSysCAMSCommunicationNames(String _name) {
//        return syscamsctdp.getAllSysCAMSCommunicationNames(_name);
//    }
//
//    public java.util.List<String> getAllSysCAMSInputPorts( String _name ) {
//        return syscamsctdp.getAllSysCAMSInputPorts( _name );
//    }

    public java.util.List<String> getAllCompositeComponent(String _name) {
        return syscamsctdp.getAllCompositeComponent(_name);
    }
		
	public Vector<String> getAllSysCAMSTasksAttributes() {
		return syscamsctdp.getAllSysCAMSTasksAttributes();
	}
		
    public java.util.List<String> getAllSysCAMSTaskNames(String _name) {
    	return syscamsctdp.getAllSysCAMSTaskNames(_name);
    }

    public String[] getAllOutTDF(String nameOfComponent) {
	return syscamsctdp.getAllOutTDF(nameOfComponent);
    }

    public String[] getAllInTDF(String nameOfComponent) {
	return syscamsctdp.getAllInTDF(nameOfComponent);
    }

    public String[] getAllOutDE(String nameOfComponent) {
	return syscamsctdp.getAllOutDE(nameOfComponent);
    }

    public String[] getAllInDE(String nameOfComponent) {
	return syscamsctdp.getAllInDE(nameOfComponent);
    }
}

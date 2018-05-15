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
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlad.TMLActivityDiagramToolBar;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;
import ui.tmlcompd.TMLComponentTaskDiagramToolBar;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
   * Class TMLComponentDesignPanel
   * Managenemt of TML component-based design panels
   * Creation: 10/03/2008
   * @version 1.0 10/03/2008
   * @author Ludovic APVRILLE, Andrea ENRICI
   * @see MainGUI
 */
public class TMLComponentDesignPanel extends TURTLEPanel {
    
	public TMLComponentTaskDiagramPanel tmlctdp;
    
	public Vector<TGComponent> validated, ignored;

    public TMLComponentDesignPanel(MainGUI _mgui) {
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

	

    public TMLActivityDiagramPanel getTMLActivityDiagramPanel(String _name) {
        TMLActivityDiagramPanel tmladp;
        for(int i=1; i<panels.size(); i++) {
            tmladp = (TMLActivityDiagramPanel)(panels.elementAt(i));
            if (tmladp.getName().compareTo(_name) ==0 || _name.endsWith(tmladp.getName())) {
                return tmladp;
            }
        }
        return null;
    }

    public TMLActivityDiagramPanel getReferencedTMLActivityDiagramPanel(TDiagramPanel _tdp, String _name) {
        //System.out.println("Searching for activity diagram of:" + _name);
        TMLActivityDiagramPanel tmladp = getTMLActivityDiagramPanel(_name);
        if (tmladp != null) {
            //System.out.println("Locally found");
            return tmladp;
        }

        // Search on other tabs
        return mgui.getReferencedTMLActivityDiagramPanel(_tdp, _name);
    }

    public void addTMLActivityDiagram(String s) {
        //System.out.println("Adding TML diagram panel = " + s);
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        TMLActivityDiagramToolBar toolBarActivity       = new TMLActivityDiagramToolBar(mgui);
        toolbars.add(toolBarActivity);

        TMLActivityDiagramPanel tmladp = new TMLActivityDiagramPanel(mgui, toolBarActivity);
        tmladp.tp = this;
        tmladp.setName(s);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(tmladp);
        tmladp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
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
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(tmlctdp);
        tmlctdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarTML, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("TML Component Task Diagram", IconManager.imgic1208, toolBarPanel, "Opens TML component task diagram");
        tabbedPane.setSelectedIndex(0);

        mgui.changeMade(tmlctdp, TDiagramPanel.NEW_COMPONENT);

        //jsp.setVisible(true);
    }

     public String saveHeaderInXml(String extensionToName) {
	 if (extensionToName == null) {
	     return "<Modeling type=\"TML Component Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	 }
	 return "<Modeling type=\"TML Component Design\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
	 
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) +  "(DIPLODOCUS Component Application diagram)";
    }

    public java.util.List<String> getAllNonMappedTMLPrimitiveComponentNames(String _name, TMLArchiDiagramPanel _tadp, boolean ref, String name) {
        return tmlctdp.getAllNonMappedTMLPrimitiveComponentNames(_name, _tadp, ref, name);
    }

    public TMLCPrimitiveComponent getPrimitiveComponentByName(String _name) {
        return tmlctdp.getPrimitiveComponentByName(_name);
    }

	public String[] getCompOutChannels(){
		return tmlctdp.getCompOutChannels();
	}

	public String[] getCompInChannels(){
		return tmlctdp.getCompInChannels();
	}

    public java.util.List<String> getAllTMLCommunicationNames(String _name) {
        return tmlctdp.getAllTMLCommunicationNames(_name);
    }

    public java.util.List<String> getAllTMLInputPorts( String _name ) {
        return tmlctdp.getAllTMLInputPorts( _name );
    }

    public java.util.List<String> getAllTMLEventNames( String _name ) {
        return tmlctdp.getAllTMLEventNames( _name );
    }

    public java.util.List<String> getAllCompositeComponent(String _name) {
        return tmlctdp.getAllCompositeComponent(_name);
    }
		
		public Vector<String> getAllTMLTasksAttributes() {
			return tmlctdp.getAllTMLTasksAttributes();
		}
    public java.util.List<String> getAllTMLTaskNames(String _name) {
	return tmlctdp.getAllTMLTaskNames(_name);
    }

    public void getListOfBreakPoints( java.util.List<Point> points ) {
       // TGComponent tgc;
        Iterator<TMLCPrimitiveComponent> iterator = tmlctdp.getPrimitiveComponentList().listIterator();
        TMLCPrimitiveComponent tmlcpc;
        TMLActivityDiagramPanel tmladp;

        while(iterator.hasNext()) {
           // tgc = (TGComponent)(iterator.next());
            //if (tgc instanceof TMLCPrimitiveComponent) {
            tmlcpc = iterator.next();
            if (tmlcpc.getDIPLOID() != -1) {
                //System.out.println("Searching for ad of name: " + tmlcpc.getValue());
                tmladp = mgui.getReferencedTMLActivityDiagramPanel(tmlcpc.getTDiagramPanel(), tmlcpc.getValue());
                if (tmladp != null) {
                    tmladp.getListOfBreakPoints(points, tmlcpc.getDIPLOID());
                } else {
                    System.out.println("Unknown panel:" + tmlcpc.getValue());
                }
            }
//            }
        }
    }
    
    public java.util.List<String> getAllCryptoConfig(){
    	java.util.List<String> cryptoConfigs=new ArrayList<String>();
    	TMLActivityDiagramPanel tmladp;
        
    	for(int i=1; i<panels.size(); i++) {
            tmladp = (TMLActivityDiagramPanel)(panels.elementAt(i));
            cryptoConfigs.addAll(tmladp.getAllCryptoConfig());
        }
	
    	return cryptoConfigs;
    }
    
    public java.util.List<String> getAllNonce(){
    	java.util.List<String> ns=new ArrayList<String>();
    	TMLActivityDiagramPanel tmladp;
        
    	for(int i=1; i<panels.size(); i++) {
            tmladp = (TMLActivityDiagramPanel)(panels.elementAt(i));
            ns.addAll(tmladp.getAllNonce());
        }
	return ns;
    }

    public ArrayList<String> getAllKeys(){
	ArrayList<String> ns=new ArrayList<String>();
	TMLActivityDiagramPanel tmladp;
        for(int i=1; i<panels.size(); i++) {
            tmladp = (TMLActivityDiagramPanel)(panels.elementAt(i));
            ns.addAll(tmladp.getAllKeys());
        }
	return ns;
    }

    public String[] getAllOutEvents(String nameOfComponent) {
	return tmlctdp.getAllOutEvents(nameOfComponent);
    }

    public String[] getAllInEvents(String nameOfComponent) {
	return tmlctdp.getAllInEvents(nameOfComponent);
    }

    public String[] getAllOutChannels(String nameOfComponent) {
	return tmlctdp.getAllOutChannels(nameOfComponent);
    }

    public String[] getAllInChannels(String nameOfComponent) {
	return tmlctdp.getAllInChannels(nameOfComponent);
    }

    public String[] getAllOutRequests(String nameOfComponent) {
	return tmlctdp.getAllOutRequests(nameOfComponent);
    }
  
}

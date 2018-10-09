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

import myutil.GraphicLib;
import ui.avatarmad.AvatarMADPanel;
import ui.avatarmad.AvatarMADToolBar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Class AvatarMADsPanel
 * Management of Avatar modeling hypothesis panels
 * Creation: 27/08/2013
 * @version 1.0 27/08/2013
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class AvatarMADsPanel extends TURTLEPanel {
    
    public AvatarMADsPanel(MainGUI _mgui) {
        super(_mgui);

        // Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        cl = new ChangeListener() {
        	
        	@Override
            public void stateChanged(ChangeEvent e){
                mgui.paneMADAction(e);
            }
        };
        
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }
	
    
   public AvatarMADPanel getMADPanel(String name) {
        AvatarMADPanel amadp;
        for(int i=1; i<panels.size(); i++) {
            amadp = (AvatarMADPanel)(panels.elementAt(i));
            if (amadp.getName().compareTo(name) ==0) {
                return amadp;
            }
        }
        return null;
    }
    
    public void addAvatarMADPanel(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        AvatarMADToolBar toolBarActivity = new AvatarMADToolBar(mgui);
        toolbars.add(toolBarActivity);
        
        AvatarMADPanel amadp = new AvatarMADPanel(mgui, toolBarActivity);
        amadp.tp = this;
        amadp.setName(s);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(amadp);
        amadp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(amadp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens Modeling Assumption Panel of " + s);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        mgui.changeMade(amadp, TDiagramPanel.NEW_COMPONENT);
        return;
    }
    
    public void init() {
         mgui.changeMade(null, TDiagramPanel.NEW_COMPONENT);
        //  Class Diagram toolbar
        /*AvatarMADToolBar toolBarAvatarMAD = new AvatarMADToolBar(mgui);
        toolbars.add(toolBarAvatarMAD);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        //Class	diagram
        AvatarMADPanel amadp = new AvatarMADPanel(mgui, toolBarAvatarMAD);
        amadp.setName("Modeling Assumption Diagram");
        amadp.tp = this;
        tdp = amadp;
        panels.add(amadp); // Always first in list
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(amadp);
        amadp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarAvatarMAD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("Model Assumption Diagram", IconManager.imgic80, toolBarPanel, "Opens the MAD");
        tabbedPane.setSelectedIndex(0);
        //tabbedPane.setMnemonicAt(tabbedPane.getTabCount()-1, '^');
        
        //jsp.setVisible(true);*/
 
    }
	

    public String saveHeaderInXml(String extensionToName) {
	if (extensionToName == null) {
	    return "<Modeling type=\"Avatar MAD\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	}
	return "<Modeling type=\"Avatar MAD\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return mgui.getTitleAt(this) + " (Avatar MAD)";
    }
	
	
    public boolean isAvatarMADEnabled() {
        return true;
    }

    public boolean canFirstDiagramBeMoved() {
	return true;
    }
    
    public boolean removeEnabled(int index) {
        return true;
    }
    
    public boolean renameEnabled(int index) {
        return true;
    }
}

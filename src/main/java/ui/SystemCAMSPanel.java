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
import ui.het.CAMSBlockDiagramToolBar;
import ui.het.CAMSBlockDiagramPanel;
import ui.util.IconManager;
import ui.TDiagramPanel;
import ui.window.JDialogCAMSBlocks;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * Class SystemCAMSPanel
 * Managenemt of TML architecture panels
 * Creation: 23/06/2017
 * @version 0.1 23/06/2107
 * @author Côme DEMARIGNY
 * @see MainGUI
 */
public class SystemCAMSPanel extends TURTLEPanel {
    public SystemCAMSPanel scp;
    public CAMSBlockDiagramPanel camsbdp; 
    public TURTLEPanel tp;
    public TDiagramPanel tdp;
    public Vector<TGComponent> validated, ignored;
    
    public SystemCAMSPanel(MainGUI _mgui) {
        super(_mgui);
         
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        
        cl = new ChangeListener() {	
        	@Override
		    public void stateChanged(ChangeEvent e){
		    mgui.paneDesignAction(e);
		}
	    };        
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }

    public void init() {
	
        // Class Diagram toolbar
        CAMSBlockDiagramToolBar camstoolBar = new CAMSBlockDiagramToolBar(mgui);
        toolbars.add(camstoolBar);
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
	//Class Diagram Panel
        camsbdp = new CAMSBlockDiagramPanel(mgui, camstoolBar);
        camsbdp.setName("SystemC-AMS Diagram");

	// Diagram toolbar          
	camsbdp.tp = this;
	tp = scp;
        panels.add(camsbdp); // Always first in list
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(camsbdp);
        camsbdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(camstoolBar, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("SystemC-AMS Diagram", IconManager.imgic60, toolBarPanel, "opens SystemC-AMS diagram");
        tabbedPane.setSelectedIndex(0);
        mgui.changeMade(camsbdp, TDiagramPanel.NEW_COMPONENT);
        
 
    }
    
    public String saveHeaderInXml(String extensionToName) {
	if (extensionToName == null) {
	    return "<Modeling type=\"SystemC-AMS\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	}
	return "<Modeling type=\"SystemC-AMS\" nameTab=\"" + mgui.getTabName(this) + extensionToName +"\" >\n";
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return mgui.getTitleAt(this) + " (SystemC-AMS Diagram)";
    }
	
    public void renameMapping(String oldName, String newName) {
	if (scp != null) {
	    scp.renameMapping(oldName, newName);
	}
    }

    public CAMSBlockDiagramPanel getCAMSBlockDiagramPanel(){
	return camsbdp;
    }
}

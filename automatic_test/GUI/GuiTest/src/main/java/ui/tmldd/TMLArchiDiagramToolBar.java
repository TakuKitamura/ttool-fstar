/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package ui.tmldd;

import myutil.TraceManager;
import ui.MainGUI;
import ui.TGUIAction;
import ui.TToolBar;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
   * Class TMLArchiDiagramToolBar
   * Implements the toolbar to be used in conjunction with the panel of a TML Architecture diagram
   * Creation: 18/09/2007
   * @version 1.1 18/09/2017
   * @author Ludovic APVRILLE
   * @see ui.cd.TClassDiagramPanel
 */
public class TMLArchiDiagramToolBar extends TToolBar  implements ActionListener {

    protected JMenuBar box;
    protected JMenu menu;
    protected JCheckBoxMenuItem m1, m2, m3, m4, m5;
    protected static String[] viewInfos = {"View task mapping", "View channel mapping", "View Comm. Pattern mapping", "View Port Interface", "View security mapping"};
    protected TMLArchiDiagramPanel panel;

    public TMLArchiDiagramToolBar(MainGUI _mgui) {
        super(_mgui);
    }

    @Override
    protected void setActive(boolean b) {
        mgui.actions[TGUIAction.TMLARCHI_EDIT].setEnabled(b);
        mgui.actions[TGUIAction.UML_NOTE].setEnabled(b);
        mgui.actions[TGUIAction.CONNECTOR_COMMENT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_LINK].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_CPUNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_FPGANODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_HWANODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_BUSNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_CPNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_BRIDGENODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_KEY].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_FIREWALL].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_EVENT_ARTIFACT].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_MEMORYNODE].setEnabled(b);
        mgui.actions[TGUIAction.TMLARCHI_DMANODE].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_ATTR].setEnabled(b);
        mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID].setEnabled(b);

        mgui.actions[TGUIAction.ACT_SHOW_ZOOM].setEnabled(false);

        mgui.actions[TGUIAction.ACT_MODEL_CHECKING].setEnabled(b);
        mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG].setEnabled(b);
        mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC].setEnabled(b);

        mgui.updateZoomInfo();
    }

    @Override
    protected void setButtons() {
        JButton button;

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_EDIT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.UML_NOTE]);
        button.addMouseListener(mgui.mouseHandler);

        button = this.add(mgui.actions[TGUIAction.CONNECTOR_COMMENT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_LINK]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_CPUNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_HWANODE]);
        button.addMouseListener(mgui.mouseHandler);

        if (mgui.isExperimentalOn()) {
            button = this.add(mgui.actions[TGUIAction.TMLARCHI_FPGANODE]);
            button.addMouseListener(mgui.mouseHandler);
        }


        button = this.add(mgui.actions[TGUIAction.TMLARCHI_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_DMANODE]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.TMLARCHI_BUSNODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_BRIDGENODE]);
        button.addMouseListener(mgui.mouseHandler);
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_MEMORYNODE]);
        button.addMouseListener(mgui.mouseHandler);
        this.addSeparator();

		button = this.add(mgui.actions[TGUIAction.TMLARCHI_COMMUNICATION_ARTIFACT]);
        button.addMouseListener(mgui.mouseHandler);
	
	/*button = this.add(mgui.actions[TGUIAction.TMLARCHI_EVENT_ARTIFACT]);
	  button.addMouseListener(mgui.mouseHandler);*/
	    
        if (MainGUI.experimentalOn) {
        	this.addSeparator();
            button = this.add(mgui.actions[TGUIAction.TMLARCHI_CPNODE]);
            button.addMouseListener(mgui.mouseHandler);
            button = this.add(mgui.actions[TGUIAction.TMLARCHI_PORT_ARTIFACT]);
            button.addMouseListener(mgui.mouseHandler);                    
	        button = this.add(mgui.actions[TGUIAction.TMLARCHI_FIREWALL]);
	        button.addMouseListener(mgui.mouseHandler);
        }

        this.addSeparator();
	
        button = this.add(mgui.actions[TGUIAction.TMLARCHI_KEY]);
        button.addMouseListener(mgui.mouseHandler);


	
        this.addSeparator();
        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_ATTR]);
        button.addMouseListener(mgui.mouseHandler);

        this.addSeparator();

        button = this.add(mgui.actions[TGUIAction.ACT_TOGGLE_DIPLO_ID]);
        button.addMouseListener(mgui.mouseHandler);

        setPluginButtons("TMLArchiDiagramPanel");
	
        this.addSeparator();
        if (viewInfos == null) {
            TraceManager.addDev("null viewInfos");
        }
        
        //Issue #68: Review Filters of Diagram Elements
        box = new JMenuBar();
        menu = new JMenu("View (options)");
        
       	m1 = new JCheckBoxMenuItem(viewInfos[0], true);
       	m1.addActionListener(this);
       	m2 = new JCheckBoxMenuItem(viewInfos[1], true);
       	m2.addActionListener(this);
       	m3 = new JCheckBoxMenuItem(viewInfos[2], true);
       	m3.addActionListener(this);
       	m4 = new JCheckBoxMenuItem(viewInfos[3], true);
       	m4.addActionListener(this);
       	m5 = new JCheckBoxMenuItem(viewInfos[4], true);
       	m5.addActionListener(this);
       	
       	
        menu.add(m1);
        menu.add(m2); 
        menu.add(m3); 
        menu.add(m4); 
        menu.add(m5);

        box.add(menu);    
        
        this.add(box);
    }

    public void setPanel(TMLArchiDiagramPanel _panel) {
        panel = _panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	super.actionPerformed(e);
        
    	//if (e.getSource() == box) {
            if (panel != null) {
            	int res = 0;
            	
            	//Check if "View Comm. Pattern mapping" is check to enable "Port Interface" option
            	if (!((JCheckBoxMenuItem) box.getMenu(0).getItem(2)).getState())
            		((JCheckBoxMenuItem)box.getMenu(0).getItem(3)).setEnabled(false);
            	else
            		((JCheckBoxMenuItem)box.getMenu(0).getItem(3)).setEnabled(true);
            	
            	//Verify checked options
            	for (int i = 0; i < box.getMenu(0).getItemCount(); i++) {
            		JMenuItem j = box.getMenu(0).getItem(i);
            		JCheckBoxMenuItem ci = (JCheckBoxMenuItem) j;
            		res *= 2;
            		if (ci.isEnabled() && ci.getState())
            			res++;
            	}
            	
            	panel.setCurrentView(res);
            }
        //}
    }
} // Class

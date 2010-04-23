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
 * Class AvatarRequirementPanel
 * Managenemt of avatar requirement panels
 * Creation: 15/05/2006
 * @version 1.1 08/09/2009
 * @author Ludovic APVRILLE
 * @see MainGUI
 */

package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import ui.avatarrd.*;
import ui.avatarpd.*;

import java.util.*;

public class AvatarRequirementPanel extends TURTLEPanel {
    public AvatarRDPanel ardp;
	public AvatarPDPanel apdp;
    
    public AvatarRequirementPanel(MainGUI _mgui) {
        super(_mgui);
		
        tabbedPane = new JTabbedPane();
		UIManager.put("TabbedPane.tabAreaBackground", _mgui.BACK_COLOR);
		UIManager.put("TabbedPane.selected", _mgui.BACK_COLOR);
		SwingUtilities.updateComponentTreeUI(tabbedPane);
		//tabbedPane.setOpaque(true);
		
        cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e){
                mgui.paneRequirementAction(e);
            }
        };
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
		
    }
    
    public void init() {
        
        // Requirement Diagram toolbar
        //addRequirementDiagram("Requirement Diagram");
        
        //jsp.setVisible(true);
    }
    
    public boolean addAvatarRD(String s) {
        AvatarRDToolBar ardtb = new AvatarRDToolBar(mgui);
        toolbars.add(ardtb);
        
        toolBarPanel = new JPanel();
		//toolBarPanel.setBackground(Color.red);
        toolBarPanel.setLayout(new BorderLayout());
		//toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);
        
        //Class	diagram
        ardp = new AvatarRDPanel(mgui, ardtb);
        ardp.setName(s);
        ardp.tp = this;
        tdp = ardp;
        panels.add(ardp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(ardp);
        ardp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(ardtb, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic1000, toolBarPanel, "Opens requirement diagram");
        tabbedPane.setSelectedIndex(0); 
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
       
        return true;
    }
	
	public boolean addAvatarPD(String s) {
        AvatarPDToolBar apdtb = new AvatarPDToolBar(mgui);
        toolbars.add(apdtb);
        
        toolBarPanel = new JPanel();
		//toolBarPanel.setBackground(Color.red);
        toolBarPanel.setLayout(new BorderLayout());
		//toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);
        
        //Class	diagram
        apdp = new AvatarPDPanel(mgui, apdtb);
        apdp.setName(s);
        apdp.tp = this;
        tdp = apdp;
        panels.add(apdp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(apdp);
        apdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(apdtb, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic1000, toolBarPanel, "Opens Parametric Diagram");
        tabbedPane.setSelectedIndex(0); 
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
       
        return true;
    }
	

    public String saveHeaderInXml() {
        return "<Modeling type=\"Avatar Requirement\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return mgui.getTitleAt(this) + " (SysML Requirement and Parametric Diagrams)";
    }
    
    public boolean removeEnabled(int index) {
        if (panels.size() > 1) {
            return true;
        }
        return false;
    }
    
    public boolean renameEnabled(int index) {
        if (panels.size() == 0) {
            return false;
        }
        if ((panels.elementAt(index) instanceof AvatarRDPanel)){
            return true;
        }
		
		/*if ((panels.elementAt(index) instanceof EBRDDPanel)){
            return true;
        }*/
		
        return false;
    }
	
	public boolean isAvatarRDEnabled() {
		return true;
	}
	
	public boolean isAvatarPDEnabled() {
		return true;
	}
    
     /*public boolean isReqEnabled() {
        return true;
    }*/
	
	/*public void addAllEBRDDPanels(ArrayList<EBRDDPanel> _al) {
		for(int i=0; i<panels.size(); i++) {
			if (panelAt(i) instanceof EBRDDPanel) {
				_al.add(((EBRDDPanel)panelAt(i)));
			}
		}
	}*/
    

}
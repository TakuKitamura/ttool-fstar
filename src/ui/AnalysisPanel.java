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
* Class AnalysisPanel
* Managenemt of analysis panels
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

import ui.iod.*;
import ui.sd.*;
import ui.ucd.*;
import ui.avatarcd.*;
import ui.avatarad.*;
import myutil.*;

public class AnalysisPanel extends TURTLEPanel {
    public InteractionOverviewDiagramPanel iodp;
    
    public AnalysisPanel(MainGUI _mgui) {
        super(_mgui);
        tabbedPane = new JTabbedPane();
        cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e){
                mgui.paneAnalysisAction(e);
            }
        };
        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }
    
    public void init() {
        
        //  Class Diagram toolbar
        InteractionOverviewDiagramToolBar toolBarIOD = new InteractionOverviewDiagramToolBar(mgui);
        toolbars.add(toolBarIOD);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        //Class	diagram
        iodp = new InteractionOverviewDiagramPanel(mgui, toolBarIOD);
        iodp.setName("Interaction Overview Diagram");
        iodp.tp = this;
        tdp = iodp;
        panels.add(iodp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(iodp);
        iodp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarIOD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("Interaction Overview Diagram", IconManager.imgic17, toolBarPanel, "Opens interaction overview diagram");
        tabbedPane.setSelectedIndex(0); 
        //jsp.setVisible(true);
    }
    
    public boolean addSequenceDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        SequenceDiagramToolBar toolBarSequence	= new SequenceDiagramToolBar(mgui);
        toolbars.add(toolBarSequence);
        
        SequenceDiagramPanel sdp = new SequenceDiagramPanel(mgui, toolBarSequence);
        sdp.setName(s);
        sdp.tp = this;
        panels.add(sdp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(sdp);
        sdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarSequence, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic18, toolBarPanel, "Open the sequence diagram of " + s);
        //tabbedPane.setVisible(true);
        //sdp.setVisible(true);
        //jsp.setVisible(true);
        //tabbedPane.setSelectedIndex(panels.size()-1);
        
        return true;
        
    }
    
	public boolean addIODiagram(String s) {
        InteractionOverviewDiagramToolBar toolBarIOD = new InteractionOverviewDiagramToolBar(mgui);
        toolbars.add(toolBarIOD);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        iodp = new InteractionOverviewDiagramPanel(mgui, toolBarIOD);
        iodp.setName(s);
        iodp.tp = this;
        tdp = iodp;
        panels.add(iodp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(iodp);
        iodp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarIOD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic17, toolBarPanel, "Opens interaction overview diagram");
        
        return true;
		
    }
    
    public boolean addUseCaseDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        UseCaseDiagramToolBar toolBarUC	= new UseCaseDiagramToolBar(mgui);
        toolbars.add(toolBarUC);
        
        UseCaseDiagramPanel ucdp = new UseCaseDiagramPanel(mgui, toolBarUC);
        ucdp.setName(s);
        ucdp.tp = this;
        panels.add(ucdp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(ucdp);
        ucdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarUC, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic19, toolBarPanel, "Open the use case diagram of " + s);
        
        return true;
    }
	
	public boolean addAvatarContextDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        AvatarCDToolBar toolBarACD	= new AvatarCDToolBar(mgui);
        toolbars.add(toolBarACD);
        
        AvatarCDPanel acdp = new AvatarCDPanel(mgui, toolBarACD);
        acdp.setName(s);
        acdp.tp = this;
        panels.add(acdp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(acdp);
        acdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarACD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic80, toolBarPanel, "Open the context diagram of " + s);
        
        return true;
    }
    
    public boolean addAvatarActivityDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        AvatarADToolBar toolBarAAD = new AvatarADToolBar(mgui);
        toolbars.add(toolBarAAD);
        
        AvatarADPanel aadp = new AvatarADPanel(mgui, toolBarAAD);
        aadp.setName(s);
        aadp.tp = this;
        panels.add(aadp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(aadp);
        aadp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarAAD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic80, toolBarPanel, "Open the activity diagram of " + s);
        
        return true;
    }
    
	
    public String saveHeaderInXml() {
        return "<Modeling type=\"Analysis\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return "TURTLE Analysis: " + mgui.getTitleAt(this);
    }
    
    public boolean removeEnabled(int index) {
        if ((panels.elementAt(index) instanceof SequenceDiagramPanel) ||(panels.elementAt(index) instanceof UseCaseDiagramPanel) ||(panels.elementAt(index) instanceof AvatarCDPanel)||(panels.elementAt(index) instanceof AvatarADPanel) || ((panels.elementAt(index) instanceof InteractionOverviewDiagramPanel) & index != 0)){
            return true;
        }
        return false;
    }
    
    public boolean renameEnabled(int index) {
        if ((panels.elementAt(index) instanceof SequenceDiagramPanel) ||(panels.elementAt(index) instanceof UseCaseDiagramPanel) ||(panels.elementAt(index) instanceof AvatarCDPanel)||(panels.elementAt(index) instanceof AvatarADPanel)|| ((panels.elementAt(index) instanceof InteractionOverviewDiagramPanel) & index != 0)){
            return true;
        }
        return false;
    }
    
    public boolean isUCDEnabled() {
        return true;
    }  
	
	public boolean isSDEnabled() {
        return true;
    }  
    
    public boolean isAvatarCDEnabled() {
        return true;
    }  
    
     public boolean isAvatarADEnabled() {
        return true;
    }  
	
	public void addInstancesToLastSD(UseCaseDiagramPanel _ucdp) {
		TraceManager.addDev("Adding instances to last SD");
		
		TDiagramPanel panel = (TDiagramPanel)(panels.get(panels.size()-1));
		if (!(panel instanceof SequenceDiagramPanel)) {
			return;
		}
		
		//TraceManager.addDev("Adding instances to last SD Step 2");
		
		ListIterator iterator = _ucdp.getComponentList().listIterator();
		TGComponent tgc;
		
		// To determine whether an actor is on the left, or on the right
		int middleX = 0;
		int cptTotal = 0;
		String systemName;
		UCDBorder border = _ucdp.getFirstUCDBorder();
		if (border != null) {
			middleX = border.getX() + border.getWidth()/2;
			systemName = border.getValue();
		} else {
			systemName = "System";
			while(iterator.hasNext()) {
				tgc = (TGComponent)(iterator.next());
				if ((tgc instanceof UCDActor) || (tgc instanceof UCDActorBox)) {
					middleX = middleX + tgc.getX();
					cptTotal ++;
				}
			}
			middleX = middleX / cptTotal;
		}
		
		//TraceManager.addDev("Adding instances to last SD Step 3");
		
		// Classify actors
		LinkedList <TGComponent> actors = new LinkedList();
		iterator = _ucdp.getComponentList().listIterator();
		int i;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if ((tgc instanceof UCDActor) || (tgc instanceof UCDActorBox)) {
				for(i=0; i<actors.size(); i++) {
					if (actors.get(i).getX() > tgc.getX()) {
						break;
					}
				}
				// added at index i
				actors.add(i, tgc);
			}
		}
		
		//TraceManager.addDev("Adding instances to last SD Step 3 nb of actors = " + actors.size());
		
		int initX = 100;
		int initY = 100;
		int stepX = 150;
		SDInstance sdi;
		boolean systemAdded = false;
		// Add actors (and the system)
		for(TGComponent elt: actors) {
			if (elt.getX() > middleX && !systemAdded) {
				sdi = (SDInstance)(TGComponentManager.addComponent(initX, initY, TGComponentManager.SD_INSTANCE, panel));
				sdi.setValue(systemName);
				sdi.setActor(false);
				panel.addComponent(sdi, initX, initY, false, true);
				initX += stepX;
				systemAdded = true;
			}
			sdi = (SDInstance)(TGComponentManager.addComponent(initX, initY, TGComponentManager.SD_INSTANCE, panel));
			sdi.setValue(elt.getValue());
			sdi.setActor(true);
			panel.addComponent(sdi, initX, initY, false, true);
			initX += stepX;
		}
		
		if (!systemAdded) {
			sdi = (SDInstance)(TGComponentManager.addComponent(initX, initY, TGComponentManager.SD_INSTANCE, panel));
			sdi.setValue(systemName);
			sdi.setActor(false);
			panel.addComponent(sdi, initX, initY, false, true);
			initX += stepX;
			systemAdded = true;
		}
		
		while(initX > panel.getMaxX()) {
			panel.setMaxX(panel.getMaxX() + panel.getIncrement());
			panel.updateSize();
		}
		
		panel.repaint();
		
		TraceManager.addDev("initX = " + initX + " nb of components:" + panel.getComponentList().size());
	}
}
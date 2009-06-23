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
 * Class ProactiveDesignPanel
 * Managenemt of proactive panels
 * Creation: 03/07/2006
 * @version 1.0 03/07/2006
 * @author Ludovic APVRILLE
 * @see MainGUI
 */

package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import ui.procsd.*;
import ui.prosmd.*;

public class ProactiveDesignPanel extends TURTLEPanel {
   
   public ProactiveCSDPanel procsdp;
   
   
  
    
    
    


	public ProactiveDesignPanel(MainGUI _mgui) {
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
        
        //  Add class diagram and composite structure diagram
        //addProClassDiagram();
        addCompositeStructureDiagram("Main Composite Structure Diagram");
  //      tdp = procsdp;
        tabbedPane.setSelectedIndex(0); 
    }
    

    public ProActiveCompSpecificationCSDPanel addProActiveCompSpecificationPanel(String name)
    {
    	 ProactiveCSDToolBar toolBarCSD = new ProactiveCSDToolBar(mgui);
         toolbars.add(toolBarCSD);
         
         toolBarPanel = new JPanel();
         toolBarPanel.setLayout(new BorderLayout());
         
         // Class diagram
         
         
         ProActiveCompSpecificationCSDPanel newCSDPanel = new ProActiveCompSpecificationCSDPanel(mgui, toolBarCSD,name);
         newCSDPanel.tp = this;
         
         if (procsdp==null)
         {
             procsdp=newCSDPanel;
         	tdp = procsdp;
         }
         
         panels.add(newCSDPanel);
         JScrollDiagramPanel jsp	= new JScrollDiagramPanel(newCSDPanel);
         newCSDPanel.jsp = jsp;
         jsp.setWheelScrollingEnabled(true);
         jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
         toolBarPanel.add(toolBarCSD, BorderLayout.NORTH);
         toolBarPanel.add(jsp, BorderLayout.CENTER);
         tabbedPane.addTab(name, IconManager.imgic17, toolBarPanel, "Opens composite structure diagram");
         return newCSDPanel;
    }
    
    public ProactiveCSDPanel addCompositeStructureDiagram(String name) {
        ProactiveCSDToolBar toolBarCSD = new ProactiveCSDToolBar(mgui);
        toolbars.add(toolBarCSD);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        // Class diagram
        
        
        ProactiveCSDPanel newCSDPanel = new ProactiveCSDPanel(mgui, toolBarCSD);
        newCSDPanel.setName(name);
        newCSDPanel.tp = this;
        
        if (procsdp==null)
        {
            procsdp=newCSDPanel;
        	tdp = procsdp;
        }
        
        panels.add(newCSDPanel);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(newCSDPanel);
        newCSDPanel.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarCSD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(name, IconManager.imgic17, toolBarPanel, "Opens composite structure diagram");
        return newCSDPanel;
    }
    
    public String addSMD(String s) {
    	
    	for (int k=0;k<panels.size();k++)
    	{
    		TDiagramPanel tg=(TDiagramPanel)panels.get(k);
    		//System.out.println(tg.toString()+"\n");
    	}
    	
        // Ensure that s is unique
        // Otherwise -> add an index
        s = generateNameIfInUse(s);
        addSMD2(s);
        return(s);
    }      
        public void addSMD2(String s)
  {
        
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        ProactiveSMDToolBar toolBar = new ProactiveSMDToolBar(mgui);
        toolbars.add(toolBar);
        ProactiveSMDPanel psmdp = new ProactiveSMDPanel(mgui, toolBar);
        psmdp.setName(s);
        psmdp.tp = this;
        panels.add(psmdp);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(psmdp);
        psmdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBar, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic18, toolBarPanel, "Open the state machine diagram diagram of " + s);
        //tabbedPane.setVisible(true);
        //sdp.setVisible(true);
        //jsp.setVisible(true);
        //tabbedPane.setSelectedIndex(panels.size()-1);
        
        //Commented by Solange
        //return true;
        
    } 

    public String saveHeaderInXml() {
        return "<Modeling type=\"ProActive Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return "TURTLE Proactive: " + mgui.getTitleAt(this);
    }
    
    public boolean removeEnabled(int index) {
        if ((panels.elementAt(index) instanceof ProactiveSMDPanel)) {
            return true;
        }
        if (index!=0)
        if ((panels.elementAt(index) instanceof ProactiveCSDPanel)) {
            return true;
        }
        return false;
    }
    
    public boolean renameEnabled(int index) {
       if ((panels.elementAt(index) instanceof ProactiveSMDPanel)) {
            return true;
        }
      if (index!=0)
       if ((panels.elementAt(index) instanceof ProactiveCSDPanel)) {
           return true;
       } 
       return false;
    }
    
    public boolean isProSMDEnabled() {
        return true;
    }
    

    public boolean removeSMDPanel(String name) {
        ProactiveSMDPanel psmdp;
        for(int i=1; i<panels.size(); i++) {
            psmdp = (ProactiveSMDPanel)(panels.elementAt(i));
            if (psmdp.getName().compareTo(name) ==0) {
                //tabbedPane.remove((ProactiveSMDPanel)panels.elementAt(i));
            	//panels.remove(i);
                toolbars.remove(i);
            	return true;
            }
        }
        return false;
    }

    
    
    public ProactiveSMDPanel getSMDPanel(String name) {
        TDiagramPanel psmdp;
        for(int i=1; i<panels.size(); i++) {
            psmdp = (TDiagramPanel)(panels.elementAt(i));
            if ((psmdp instanceof ProactiveSMDPanel)&&(psmdp.getName().compareTo(name) ==0)) {
                return (ProactiveSMDPanel)psmdp;
            }
        }
        return null;
    }

    public void updateAllMembranes()
    {
    	 for(int i=1; i<panels.size(); i++) {
          TDiagramPanel  psmdp = (TDiagramPanel)(panels.elementAt(i));
             if ((psmdp instanceof ProactiveCSDPanel)) {
                 
            	 ProactiveCSDPanel pcsdp=(ProactiveCSDPanel)psmdp;
            	 pcsdp.updateAllMembranes();
            	 
            	 
             }
         }
    	
    }

    public ProActiveCompSpecificationCSDPanel getCompSpecPanel(String name) {
        TDiagramPanel psmdp;
        for(int i=1; i<panels.size(); i++) {
            psmdp = (TDiagramPanel)(panels.elementAt(i));
            if ((psmdp instanceof ProActiveCompSpecificationCSDPanel)&&(psmdp.getName().compareTo(name) ==0)) {
                return (ProActiveCompSpecificationCSDPanel)psmdp;
            }
        }
        return null;
    }

    
    
    
 public void renamePanel(TDiagramPanel tdp,String newName)
 {	
	 //int index=this.tabbedPane.indexOfComponent((ProactiveSMDPanel)tdp);
	   int index=tabbedPane.indexOfTab(tdp.name); 
	 
	   if (index!=-1)
	   {
	    tabbedPane.setTitleAt(index, newName);
         ((TDiagramPanel)(panels.elementAt(index))).setName(newName);
          mgui.changeMade(null, -1);
	   } 
 }




}
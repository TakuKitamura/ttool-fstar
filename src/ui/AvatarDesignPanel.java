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
 * Class AvatarDesignPanel
 * Management of Avatar block panels
 * Creation: 06/04/2010
 * @version 1.0 06/04/2010
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
 
package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import ui.avatarbd.*;
import ui.avatarsmd.*;

public class AvatarDesignPanel extends TURTLEPanel {
    public AvatarBDPanel abdp; 
    public Vector validated, ignored;
    
    public AvatarDesignPanel(MainGUI _mgui) {
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
    
   public AvatarSMDPanel getAvatarSMDPanel(String name) {
        AvatarSMDPanel asmdp;
        for(int i=1; i<panels.size(); i++) {
            asmdp = (AvatarSMDPanel)(panels.elementAt(i));
            if (asmdp.getName().compareTo(name) ==0) {
                return asmdp;
            }
        }
        return null;
    }
    
    public void addAvatarStateMachineDiagramPanel(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        AvatarSMDToolBar toolBarActivity = new AvatarSMDToolBar(mgui);
        toolbars.add(toolBarActivity);
        
        AvatarSMDPanel asmdp = new AvatarSMDPanel(mgui, toolBarActivity);
        asmdp.tp = this;
        asmdp.setName(s);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(asmdp);
        asmdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(asmdp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens the state machine of " + s);
   
        return;
    }
    
    public void init() {
         
        //  Class Diagram toolbar
        AvatarBDToolBar toolBarAvatarBD = new AvatarBDToolBar(mgui);
        toolbars.add(toolBarAvatarBD);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        
        //Class	diagram
        abdp = new AvatarBDPanel(mgui, toolBarAvatarBD);
        abdp.setName("AVATAR Block Diagram");
        abdp.tp = this;
        tdp = abdp;
        panels.add(abdp); // Always first in list
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(abdp);
        abdp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarAvatarBD, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("AVATAR Design", IconManager.imgic62, toolBarPanel, "Opens the AVATAR Design");
        tabbedPane.setSelectedIndex(0);
        
        //jsp.setVisible(true);
 
    }
	
	public Vector getAllAttributes(String _name) {
		return abdp.getAllAttributesOfBlock(_name);
	}
	
	public Vector getAllMethods(String _name) {
		return abdp.getAllMethodsOfBlock(_name);
	}
	
	public Vector getAllSignals(String _name) {
		return abdp.getAllSignalsOfBlock(_name);
	}
    
    public String saveHeaderInXml() {
        return "<Modeling type=\"AVATAR Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n"; 
    }
    
    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }
    
    public String toString() {
        return mgui.getTitleAt(this) + " (AVATAR Design)";
    }
	
	/*public ArrayList<String> getAllTMLTaskNames(String _name) {
		return tmltdp.getAllTMLTaskNames(_name);
	}
	
	public ArrayList<String> getAllTMLCommunicationNames(String _name) {
		return tmltdp.getAllTMLCommunicationNames(_name);
	}
	
	public ArrayList<String> getAllNonMappedTMLTaskNames(String _name, TMLArchiDiagramPanel _tadp, boolean ref, String name) {
		return tmltdp.getAllNonMappedTMLTaskNames(_name, _tadp, ref, name);
	}
	
	public TMLTaskOperator getTaskByName(String _name) {
		return tmltdp.getTaskByName(_name);
	}
	
	public void getListOfBreakPoints(ArrayList<Point> points) {
		TGComponent tgc;
		ListIterator iterator = tmltdp.getComponentList().listIterator();
		TMLTaskOperator tmlto;
		TMLActivityDiagramPanel tmladp;
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TMLTaskOperator) {
                tmlto = (TMLTaskOperator)tgc;
				if (tmlto.getDIPLOID() != -1) {
					tmladp = getTMLActivityDiagramPanel(tmlto.getValue());
					tmladp.getListOfBreakPoints(points, tmlto.getDIPLOID());
				}
			}
		}
	}*/
    
}
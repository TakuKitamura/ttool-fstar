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
 * Class TURTLEPanelPopupListener
 * Management of TURTLE panels
 * Creation: 14/01/2005
 * @version 1.0 14/01/2005
 * @author Ludovic APVRILLE
 * @see MainGUI
 */

package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import myutil.*;


public class TURTLEPanelPopupListener extends MouseAdapter /* popup menus onto tabs */ {
    private TURTLEPanel tp;
    private JPopupMenu menu;
    protected MainGUI mgui;
    
    private JMenuItem rename, remove, moveRight, moveLeft, sort, newucd, newreq, newebrdd, newprosmd;
    
    public TURTLEPanelPopupListener(TURTLEPanel _tp, MainGUI _mgui) {
        tp = _tp;
        mgui = _mgui;
        createMenu();
    }
    
    public void mousePressed(MouseEvent e) {
        checkForPopup(e);
    }
    public void mouseReleased(MouseEvent e) {
        checkForPopup(e);
    }
    public void mouseClicked(MouseEvent e) {
        checkForPopup(e);
    }
    
    private void checkForPopup(MouseEvent e) {
        if(e.isPopupTrigger()) {
            Component c = e.getComponent();
            updateMenu(tp.tabbedPane.getSelectedIndex());
            menu.show(c, e.getX(), e.getY());
        }
    }
    
    private void createMenu() {
        rename = createMenuItem("Rename");
        remove = createMenuItem("Remove");
        moveLeft = createMenuItem("Move to the left");
        moveRight = createMenuItem("Move to the right");
        sort = createMenuItem("Sort");
        newucd = createMenuItem("New use case diagram");
        newreq = createMenuItem("New requirement diagram");
		newebrdd = createMenuItem("New Event-Based Requirement Description Diagram");
        newprosmd = createMenuItem("New ProActive state machine diagram");
        
        menu = new JPopupMenu("TURTLE panel");
        menu.add(moveLeft);
        menu.add(moveRight);
        menu.addSeparator();
        menu.add(rename);
        menu.add(remove);
        menu.addSeparator();
        menu.add(sort);
        menu.addSeparator();
        menu.add(newucd);
        menu.addSeparator();
        menu.add(newreq);
		menu.add(newebrdd);
        menu.addSeparator();
        menu.add(newprosmd);
    }
    
    private JMenuItem createMenuItem(String s) {
        JMenuItem item = new JMenuItem(s);
        item.setActionCommand(s);
        item.addActionListener(listener);
        return item;
    }
    
    private void updateMenu(int index) {
        //System.out.println("UpdateMenu index=" + index);
        if (index < 2) {
            moveLeft.setEnabled(false);
        } else {
            moveLeft.setEnabled(true);
        }
        
        if ((index + 1 < tp.panels.size()) && (index > 0)) {
            moveRight.setEnabled(true);
        } else {
            moveRight.setEnabled(false);
        }
        
        // remove!
     
        remove.setEnabled(tp.removeEnabled(index));
        rename.setEnabled(tp.renameEnabled(index));
        
        if (tp.tabbedPane.getTabCount() < 3) {
            sort.setEnabled(false);
        } else {
             sort.setEnabled(true);
        }
        
        newucd.setEnabled(tp.isUCDEnabled());
        newreq.setEnabled(tp.isReqEnabled());
		newebrdd.setEnabled(tp.isReqEnabled());
        newprosmd.setEnabled(tp.isProSMDEnabled());
        
    }
    
    private Action listener = new AbstractAction() {
        
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            String ac = item.getActionCommand();
            if(ac.equals("Rename")) {
                tp.requestRenameTab(tp.tabbedPane.getSelectedIndex());
            } else if (ac.equals("Remove")) {
                tp.requestRemoveTab(tp.tabbedPane.getSelectedIndex());
            } else if (ac.equals("Move to the left")) {
                tp.requestMoveLeftTab(tp.tabbedPane.getSelectedIndex());
            } else if (ac.equals("Move to the right")) {
                tp.requestMoveRightTab(tp.tabbedPane.getSelectedIndex());
            } else if (ac.equals("Sort")) {
                GraphicLib.sortJTabbedPane(tp.tabbedPane, tp.panels, 1, tp.tabbedPane.getTabCount());
                mgui.changeMade(null, -1);
            } else if (ac.equals("New use case diagram")) {
                mgui.createUseCaseDiagram(tp, "Use Case diagram");
                mgui.changeMade(null, -1);
            } else if (ac.equals("New requirement diagram")) {
                mgui.createRequirementDiagram(tp, "Requirement diagram");
                mgui.changeMade(null, -1);
            } else if (ac.equals("New Event-Based Requirement Description Diagram")) {
                mgui.createEBRDD(tp, "EBRDD");
                mgui.changeMade(null, -1);
            } else if (ac.equals("New ProActive state machine diagram")) {
                mgui.createProActiveSMD(tp, "ProActive SMD");
                mgui.changeMade(null, -1);
            }
        }
    };
}









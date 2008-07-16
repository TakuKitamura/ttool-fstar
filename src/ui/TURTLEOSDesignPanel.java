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
 * Class TURTLEOSDesignPanel
 * Managenemt of TURTLEOS design panels
 * Creation: 29/09/2006
 * @version 1.0 29/09/2006
 * @author Ludovic APVRILLE
 * @see MainGUI
 */

package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import ui.oscd.*;
import ui.osad.*;
//import ui.tmlcd.*;

public class TURTLEOSDesignPanel extends TURTLEPanel implements TURTLEDesignPanelInterface {
    public Vector validated, ignored;
    public TURTLEOSClassDiagramPanel toscdp;

    public TURTLEOSDesignPanel(MainGUI _mgui) {
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
    
    public ActivityDiagramPanelInterface getBehaviourPanel(String name) {
           return getTURTLEOSActivityDiagramPanel(name);
    }

    public ClassDiagramPanelInterface getStructurePanel() {
           return toscdp;
    }

   public TURTLEOSActivityDiagramPanel getTURTLEOSActivityDiagramPanel(String name) {
        TURTLEOSActivityDiagramPanel tosadp;
        for(int i=1; i<panels.size(); i++) {
            tosadp = (TURTLEOSActivityDiagramPanel)(panels.elementAt(i));
            if (tosadp.getName().compareTo(name) ==0) {
                return tosadp;
            }
        }
        return null;
    }

    public void addTURTLEOSActivityDiagram(String s) {
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        TURTLEOSActivityDiagramToolBar toolBarActivity	= new TURTLEOSActivityDiagramToolBar(mgui);
        toolbars.add(toolBarActivity);

        TURTLEOSActivityDiagramPanel tosadp = new TURTLEOSActivityDiagramPanel(mgui, toolBarActivity);
        tosadp.tp = this;
        tosadp.setName(s);
        JScrollDiagramPanel jsp	= new JScrollDiagramPanel(tosadp);
        tosadp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
        toolBarPanel.add(toolBarActivity, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        panels.add(tosadp);
        tabbedPane.addTab(s, IconManager.imgic63, toolBarPanel, "Opens the TURTLE-OS activity diagram of " + s);

        return;
    }

    public void init() {
          //  Class Diagram toolbar
          TURTLEOSClassDiagramToolBar toolBarTOS = new TURTLEOSClassDiagramToolBar(mgui);
          toolbars.add(toolBarTOS);

          toolBarPanel = new JPanel();
          toolBarPanel.setLayout(new BorderLayout());

          //Class	diagram
          toscdp = new TURTLEOSClassDiagramPanel(mgui, toolBarTOS);
          toscdp.setName("TURTLE-OS Class Diagram");
          toscdp.tp = this;
          tdp = toscdp;
          panels.add(toscdp); // Always first in list
          JScrollDiagramPanel jsp	= new JScrollDiagramPanel(toscdp);
          toscdp.jsp = jsp;
          jsp.setWheelScrollingEnabled(true);
          jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
          toolBarPanel.add(toolBarTOS, BorderLayout.NORTH);
          toolBarPanel.add(jsp, BorderLayout.CENTER);
          tabbedPane.addTab("TURTLE-OS Class diagram", IconManager.imgic62, toolBarPanel, "Opens TURTLE-OS class diagram");
          tabbedPane.setSelectedIndex(0);
    }

    public String saveHeaderInXml() {
        return "<Modeling type=\"TURTLE-OS Design\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return "TURTLE-OS: " + mgui.getTitleAt(this);
    }

}
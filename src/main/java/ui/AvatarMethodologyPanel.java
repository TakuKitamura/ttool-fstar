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
import ui.avatarmethodology.AvatarMethodologyDiagramPanel;
import ui.avatarmethodology.AvatarMethodologyDiagramToolbar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * Class AvatarMethodologyPanel Managenemt of the avatar methodology panels
 * Creation: 27/08/2014
 * 
 * @version 1.1 27/08/2014
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class AvatarMethodologyPanel extends TURTLEPanel {

    public AvatarMethodologyDiagramPanel dmd;

    public AvatarMethodologyPanel(MainGUI _mgui) {
        super(_mgui);

        // Issue #41 Ordering of tabbed panes
        tabbedPane = GraphicLib.createTabbedPane();// new JTabbedPane();
        UIManager.put("TabbedPane.tabAreaBackground", MainGUI.BACK_COLOR);
        UIManager.put("TabbedPane.selected", MainGUI.BACK_COLOR);
        SwingUtilities.updateComponentTreeUI(tabbedPane);
        // tabbedPane.setOpaque(true);

        cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                mgui.paneDiplodocusMethodologyAction(e);
            }
        };

        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));

    }

    // Put the methodology
    public void initElements() {
        TGComponent tgc1 = dmd.addComponent(150, 100, TGComponentManager.AVATARMETHODOLOGY_REF_ASSUMPTIONS, false);
        TGComponent tgc2 = dmd.addComponent(250, 200, TGComponentManager.AVATARMETHODOLOGY_REF_REQUIREMENT, false);
        TGComponent tgc3 = dmd.addComponent(350, 300, TGComponentManager.AVATARMETHODOLOGY_REF_ANALYSIS, false);
        TGComponent tgc4 = dmd.addComponent(450, 400, TGComponentManager.AVATARMETHODOLOGY_REF_DESIGN, false);
        TGComponent tgc5 = dmd.addComponent(550, 500, TGComponentManager.AVATARMETHODOLOGY_REF_PROTOTYPE, false);
        TGComponent tgc6 = dmd.addComponent(200, 400, TGComponentManager.AVATARMETHODOLOGY_REF_PROPERTIES, false);

        // Connectors

        // Assumptions -> reqs
        TGConnectingPoint p1, p2;
        p1 = tgc1.getTGConnectingPointAtIndex(0);
        p2 = tgc2.getTGConnectingPointAtIndex(0);
        Vector<Point> listPoint = new Vector<Point>();
        Point p = new Point(210, 235);
        listPoint.add(p);
        TGConnector tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(),
                TGComponentManager.AVATARMETHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Reqs -> Analysis
        p1 = tgc2.getTGConnectingPointAtIndex(1);
        p2 = tgc3.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        p = new Point(310, 335);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.AVATARMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Analysis -> Design
        p1 = tgc3.getTGConnectingPointAtIndex(1);
        p2 = tgc4.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        p = new Point(410, 435);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.AVATARMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Design -> Prototyping
        p1 = tgc4.getTGConnectingPointAtIndex(1);
        p2 = tgc5.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        p = new Point(510, 535);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.AVATARMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Reqs -> Prop
        p1 = tgc2.getTGConnectingPointAtIndex(2);
        p2 = tgc6.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.AVATARMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);
    }

    public void init() {
        init("Avatar methodology");
    }

    public void init(String name) {
        addAvatarMethodologyDiagram(name);

        // Requirement Diagram toolbar
        // addRequirementDiagram("Requirement Diagram");

        // jsp.setVisible(true);
    }

    public boolean addAvatarMethodologyDiagram(String s) {
        AvatarMethodologyDiagramToolbar dmdt = new AvatarMethodologyDiagramToolbar(mgui);
        toolbars.add(dmdt);

        toolBarPanel = new JPanel();
        // toolBarPanel.setBackground(Color.red);
        toolBarPanel.setLayout(new BorderLayout());
        // toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);

        // Class diagram
        dmd = new AvatarMethodologyDiagramPanel(mgui, dmdt);
        dmd.setName(s);
        dmd.tp = this;
        tdp = dmd;
        panels.add(dmd);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(dmd);
        dmd.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
        toolBarPanel.add(dmdt, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic99, toolBarPanel, "Opens avatar methodology");
        tabbedPane.setSelectedIndex(0);
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        // TGComponent tgc = TGComponentManager.addComponent(100, 100,
        // TGComponentManager.DIPLODODUSMETHODOLOGY_REF_APPLICATION, dmd);
        mgui.changeMade(dmd, TDiagramPanel.NEW_COMPONENT);

        return true;
    }

    public String saveHeaderInXml(String extensionToName) {
        if (extensionToName == null) {
            return "<Modeling type=\"Avatar Methodology\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
        }
        return "<Modeling type=\"Avatar Methodology\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (Avatar Methodology)";
    }

    public boolean removeEnabled(int index) {
        return panels.size() > 1;
    }

    public boolean renameEnabled(int index) {
        return panels.size() != 0;
    }

    public boolean isAvatarMethodologyEnabled() {
        return true;
    }

}

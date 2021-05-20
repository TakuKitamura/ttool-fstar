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
import ui.diplodocusmethodology.DiplodocusMethodologyDiagramPanel;
import ui.diplodocusmethodology.DiplodocusMethodologyDiagramToolbar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * Class DiplodocusMethodologyPanel Managenemt of the diplodocus methodology
 * panels Creation: 26/03/2014
 * 
 * @version 1.1 26/03/2014
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class DiplodocusMethodologyPanel extends TURTLEPanel {
    public DiplodocusMethodologyDiagramPanel dmd;

    public DiplodocusMethodologyPanel(MainGUI _mgui) {
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
        TGComponent tgc1 = dmd.addComponent(350, 100, TGComponentManager.DIPLODODUSMETHODOLOGY_REF_APPLICATION, false);
        TGComponent tgc2 = dmd.addComponent(850, 100, TGComponentManager.DIPLODODUSMETHODOLOGY_REF_ARCHITECTURE, false);
        TGComponent tgc3 = dmd.addComponent(600, 300, TGComponentManager.DIPLODODUSMETHODOLOGY_REF_MAPPING, false);
        TGComponent tgc4 = dmd.addComponent(600, 100, TGComponentManager.DIPLODODUSMETHODOLOGY_REF_CP, false);

        // Connectors

        // App -> mapping
        TGConnectingPoint p1, p2;
        p1 = tgc1.getTGConnectingPointAtIndex(0);
        p2 = tgc3.getTGConnectingPointAtIndex(0);
        Vector<Point> listPoint = new Vector<Point>();
        TGConnector tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(),
                TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // cp -> mapping
        p1 = tgc4.getTGConnectingPointAtIndex(0);
        p2 = tgc3.getTGConnectingPointAtIndex(2);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Archi -> mapping
        p1 = tgc2.getTGConnectingPointAtIndex(0);
        p2 = tgc3.getTGConnectingPointAtIndex(1);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR,
                dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        dmd.addComponent(50, 150, TGComponentManager.DIPLODODUSMETHODOLOGY_REF_REQUIREMENT, false);
    }

    public void init() {
        init("Diplodocus methodology");
    }

    public void init(String name) {
        addDiplodocusMethodologyDiagram(name);

        // Requirement Diagram toolbar
        // addRequirementDiagram("Requirement Diagram");

        // jsp.setVisible(true);
    }

    public boolean addDiplodocusMethodologyDiagram(String s) {
        DiplodocusMethodologyDiagramToolbar dmdt = new DiplodocusMethodologyDiagramToolbar(mgui);
        toolbars.add(dmdt);

        toolBarPanel = new JPanel();
        // toolBarPanel.setBackground(Color.red);
        toolBarPanel.setLayout(new BorderLayout());
        // toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);

        // Class diagram
        dmd = new DiplodocusMethodologyDiagramPanel(mgui, dmdt);
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
        tabbedPane.addTab(s, IconManager.imgic98, toolBarPanel, "Opens diplodocus methodology");
        tabbedPane.setSelectedIndex(0);
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());
        mgui.changeMade(dmd, TDiagramPanel.NEW_COMPONENT);
        // TGComponent tgc = TGComponentManager.addComponent(100, 100,
        // TGComponentManager.DIPLODODUSMETHODOLOGY_REF_APPLICATION, dmd);

        return true;
    }

    public String saveHeaderInXml(String extensionToName) {
        if (extensionToName == null) {
            return "<Modeling type=\"Diplodocus Methodology\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
        }
        return "<Modeling type=\"Diplodocus Methodology\" nameTab=\"" + mgui.getTabName(this) + extensionToName
                + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (Diplodocus Methodology)";
    }

    public boolean removeEnabled(int index) {
        return panels.size() > 1;
    }

    public boolean renameEnabled(int index) {
        return panels.size() != 0;
    }

    public boolean isDiplodocusMethodologyEnabled() {
        return true;
    }

}

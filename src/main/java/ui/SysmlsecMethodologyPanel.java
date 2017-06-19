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
import ui.sysmlsecmethodology.SysmlsecMethodologyDiagramPanel;
import ui.sysmlsecmethodology.SysmlsecMethodologyDiagramToolbar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
   * Class SysmlsecMethodologyPanel
   * Managenemt of the sysmlsec methodology panels
   * Creation: 26/01/2016
   * @version 1.1 26/01/2016
   * @author Ludovic APVRILLE
   * @see MainGUI
 */
public class SysmlsecMethodologyPanel extends TURTLEPanel {
    public SysmlsecMethodologyDiagramPanel dmd;

    public SysmlsecMethodologyPanel(MainGUI _mgui) {
        super(_mgui);

    	// Issue #41 Ordering of tabbed panes 
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        UIManager.put("TabbedPane.tabAreaBackground", MainGUI.BACK_COLOR);
        UIManager.put("TabbedPane.selected", MainGUI.BACK_COLOR);
        SwingUtilities.updateComponentTreeUI(tabbedPane);
        //tabbedPane.setOpaque(true);

        cl = new ChangeListener() {
        	
        	@Override
            public void stateChanged(ChangeEvent e){
                mgui.paneDiplodocusMethodologyAction(e);
            }
        };

        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));

    }

    // Put the methodology
    public void initElements() {
        TGComponent tgc1 = dmd.addComponent(50, 300, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_ASSUMPTIONS, false);
        TGComponent tgc2 = dmd.addComponent(350, 200, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_REQUIREMENT, false);

	TGComponent tgc10 = dmd.addComponent(350, 400, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_ATTACK, false);

	// Partitioning
	TGComponent tgc7 = dmd.addComponent(650, 100, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_FUNCTIONAL_VIEW, false);
        TGComponent tgc8 = dmd.addComponent(1150, 100, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_ARCHITECTURE_VIEW, false);
        TGComponent tgc9 = dmd.addComponent(900, 225, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_MAPPING_VIEW, false);
	TGComponent tgc11 = dmd.addComponent(887, 100, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_CP_VIEW, false);
        

	int xa = 900;
	int ya = 400;
	// Software dev.
	TGComponent tgc3 = dmd.addComponent(xa, ya, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_ANALYSIS, false);
        TGComponent tgc4 = dmd.addComponent(xa+100, ya+100, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_DESIGN, false);
        TGComponent tgc5 = dmd.addComponent(xa+200, ya+200, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_PROTOTYPE, false);
        TGComponent tgc6 = dmd.addComponent(xa-250, ya+100, TGComponentManager.SYSMLSEC_METHODOLOGY_REF_PROPERTIES, false);
	

	TGCPanelInfo infoParti = (TGCPanelInfo)(dmd.addComponent(630, 65, TGComponentManager.INFO_PANEL, false));
	infoParti.resize(750, 250);
	infoParti.setValue("SW/HW Partitioning");
	infoParti.setStringPos(TGCPanelInfo.UPPER_MIDDLE);
	infoParti.setFillColor(ColorManager.SYSMLSEC_PARTITIONING);
	infoParti.setTextColor(Color.white);

	TGCPanelInfo infoSw = (TGCPanelInfo)(dmd.addComponent(xa-270, ya-25, TGComponentManager.INFO_PANEL, false));
	infoSw.resize(750, 325);
	infoSw.setValue("SW Design");
	infoSw.setStringPos(TGCPanelInfo.UPPER_MIDDLE);
	infoSw.setFillColor(ColorManager.SYSMLSEC_SWDESIGN);
	infoSw.setTextColor(Color.white);
       
	
	TGCPanelInfo infoReq = (TGCPanelInfo)(dmd.addComponent(330, 169, TGComponentManager.INFO_PANEL, false));
	infoReq.resize(241, 340);
	infoReq.setValue("Req and attacks");
	infoReq.setStringPos(TGCPanelInfo.UPPER_MIDDLE);
	infoReq.setTextColor(Color.white);
	infoReq.setFillColor(ColorManager.SYSMLSEC_REQ);
	
	

        //Connectors

        // Assumptions -> reqs
        TGConnectingPoint p1, p2;
        p1 = tgc1.getTGConnectingPointAtIndex(0);
        p2 = tgc2.getTGConnectingPointAtIndex(0);
        Vector<Point> listPoint = new Vector<Point>();
        Point p = new Point(210, 235);
        listPoint.add(p);
        TGConnector tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        //dmd.getComponentList().add(0, tgco);

        // Reqs -> Attacks
        p1 = tgc2.getTGConnectingPointAtIndex(1);
        p2 = tgc10.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        //p = new Point(375, 370);
        //listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Attacks -> Reqs
        p1 = tgc10.getTGConnectingPointAtIndex(1);
        p2 = tgc2.getTGConnectingPointAtIndex(2);
        listPoint = new Vector<Point>();
        //p = new Point(375, 370);
        //listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Analysis -> Design
        p1 = tgc3.getTGConnectingPointAtIndex(1);
        p2 = tgc4.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        p = new Point(xa+60, ya+125);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Design -> Prototyping
        p1 = tgc4.getTGConnectingPointAtIndex(1);
        p2 = tgc5.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        p = new Point(xa+160, ya+225);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Reqs -> Prop
        p1 = tgc2.getTGConnectingPointAtIndex(0);
        p2 = tgc6.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
	p = new Point(xa-305, 235);
	listPoint.add(p);
	p = new Point(xa-305, ya+50);
        listPoint.add(p);
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

	// Partitioning
	// App -> mapping
        p1 = tgc7.getTGConnectingPointAtIndex(0);
        p2 = tgc9.getTGConnectingPointAtIndex(0);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

	// cp -> mapping
        p1 = tgc11.getTGConnectingPointAtIndex(0);
        p2 = tgc9.getTGConnectingPointAtIndex(2);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);

        // Archi -> mapping
        p1 = tgc8.getTGConnectingPointAtIndex(0);
        p2 = tgc9.getTGConnectingPointAtIndex(1);
        listPoint = new Vector<Point>();
        tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR, dmd, p1, p2, listPoint);
        p1.setFree(false);
        p2.setFree(false);
        dmd.getComponentList().add(0, tgco);
	
		dmd.bringToBack(infoParti);
		dmd.bringToBack(infoSw);
		dmd.bringToBack(infoReq);
    }

    public void init() {
        init("SysML-Sec methodology");
    }

    public void init(String name) {
        addSysmlsecMethodologyDiagram(name);

        // Requirement Diagram toolbar
        //addRequirementDiagram("Requirement Diagram");

        //jsp.setVisible(true);
    }

    public boolean addSysmlsecMethodologyDiagram(String s) {
        SysmlsecMethodologyDiagramToolbar dmdt = new SysmlsecMethodologyDiagramToolbar(mgui);
        toolbars.add(dmdt);

        toolBarPanel = new JPanel();
        //toolBarPanel.setBackground(Color.red);
        toolBarPanel.setLayout(new BorderLayout());
        //toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);

        //Class diagram
        dmd = new SysmlsecMethodologyDiagramPanel(mgui, dmdt);
        dmd.setName(s);
        dmd.tp = this;
        tdp = dmd;
        panels.add(dmd);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(dmd);
        dmd.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT);
        toolBarPanel.add(dmdt, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic99, toolBarPanel, "Opens SysMLSec methodology");
        tabbedPane.setSelectedIndex(0);
        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        mgui.changeMade(dmd, TDiagramPanel.NEW_COMPONENT);

        return true;
    }



    public String saveHeaderInXml(String extensionToName) {
	if (extensionToName == null) {
	    return "<Modeling type=\"Sysmlsec Methodology\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	}
	return "<Modeling type=\"Sysmlsec Methodology\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (SysMLSec Methodology)";
    }

    public boolean removeEnabled(int index) {
        return panels.size() > 1;
    }

    public boolean renameEnabled(int index) {
        return panels.size() != 0;
    }

    public boolean isSysmlsecMethodologyEnabled() {
        return true;
    }


}

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
import ui.tmlcp.TMLCPPanel;
import ui.tmlcp.TMLCPToolBar;
import ui.tmlsd.TMLSDPanel;
import ui.tmlsd.TMLSDToolBar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
   * Class TMLCommunicationPatternPanel
   * Managenemt of CP panels
   * Creation: 17/02/2014
   * @version 1.0 17/02/2014
   * @author Ludovic APVRILLE
   * @see MainGUI
 */
public class TMLCommunicationPatternPanel extends TURTLEPanel {
    public TMLCPPanel tmlcpp;
    public Vector<TGComponent> validated, ignored;

    public TMLCommunicationPatternPanel(MainGUI _mgui) {
        super(_mgui);
        
    	// Issue #41 Ordering of tabbed panes 
        //tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();
        tabbedPane = GraphicLib.createDraggableEnhancedTabbedPaneFixedAt0(this);//new JTabbedPane();
        
        cl = new ChangeListener() {
        	
        	@Override
            public void stateChanged(ChangeEvent e){
                mgui.paneAnalysisAction(e);
            }
        };

        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }

    public void init() {

        //  Main CP toolbar
        TMLCPToolBar toolBarMainCP = new TMLCPToolBar(mgui);
        toolbars.add(toolBarMainCP);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        // TMLCPPanel
        tmlcpp = new TMLCPPanel(mgui, toolBarMainCP);
        tmlcpp.setName("MainCP");
        tmlcpp.tp = this;
        tdp = tmlcpp;
        panels.add(tmlcpp);
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(tmlcpp);
        tmlcpp.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarMainCP, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("MainCP", IconManager.imgic17, toolBarPanel, "Opens the main communication pattern");
        tabbedPane.setSelectedIndex(0);
        //jsp.setVisible(true);
        mgui.changeMade(tmlcpp, TDiagramPanel.NEW_COMPONENT);
    }

    public boolean addCPSequenceDiagram( String s ) {

        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout( new BorderLayout() );

        TMLSDToolBar toolBarSequence    = new TMLSDToolBar( mgui );
        toolbars.add( toolBarSequence );

        TMLSDPanel sdp = new TMLSDPanel( mgui, toolBarSequence );
        sdp.setName( s );
        sdp.tp = this;
        panels.add( sdp );
        JScrollDiagramPanel jsp = new JScrollDiagramPanel( sdp );
        sdp.jsp = jsp;
        jsp.setWheelScrollingEnabled( true );
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarSequence, BorderLayout.NORTH );
        toolBarPanel.add( jsp, BorderLayout.CENTER );
        tabbedPane.addTab( s, IconManager.imgic18, toolBarPanel, "Open the communication pattern sequence diagram of " + s );

        //Vector<TDiagramPanel> panelList = tmlcpp.getPanels();
        /*TDiagramPanel mainCP = panels.get( 0 );       //get the Main CP
          TraceManager.addDev( mainCP.getName() );
          LinkedList mainCPelems = mainCP.getComponentList();
          for( int i = 0; i < mainCPelems.size(); i++ ) {
          TGComponent elem = (TGComponent) mainCPelems.get(i);
          TraceManager.addDev( elem.getName() );
          if( elem instanceof TMLCPRefSD && s.equals(elem.getName()) )  {
          TMLCPRefSD cpRefSD = (TMLCPRefSD) mainCPelems.get(i);
          cpRefSD.setReferenceToSD( sdp );
          //cpRefSD.setIndex( i );
          TraceManager.addDev( "Found and added the reference" );
          }
          }*/
        //tabbedPane.setVisible(true);
        //sdp.setVisible(true);
        //jsp.setVisible(true);
        //tabbedPane.setSelectedIndex(panels.size()-1);

        return true;
    }

    public boolean addCPDiagram(String s) {
        TMLCPToolBar toolBarMainCP = new TMLCPToolBar(mgui);
        toolbars.add(toolBarMainCP);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        TMLCPPanel tmlcppNew = new TMLCPPanel(mgui, toolBarMainCP);
        tmlcppNew.setName(s);
        tmlcppNew.tp = this;
        tdp = tmlcppNew;
        panels.add( tmlcppNew );
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(tmlcppNew);
        tmlcppNew.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarMainCP, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab(s, IconManager.imgic17, toolBarPanel, "Opens communication pattern diagram");

        return true;

    }


    public String saveHeaderInXml(String extensionToName) {
	if (extensionToName == null) {
	    return "<Modeling type=\"TML CP\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
	}
	return "<Modeling type=\"TML CP\" nameTab=\"" + mgui.getTabName(this) + extensionToName + "\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return "DIPLODOCUS Communication Pattern: " + mgui.getTitleAt(this);
    }

    public String getName()     {
        return mgui.getTitleAt(this);
    }

    public boolean removeEnabled(int index) {
        if (index ==0) {
            return false;
        }
        return (panels.elementAt(index) instanceof TMLCPPanel) || (panels.elementAt(index) instanceof TMLSDPanel) || ((panels.elementAt(index) instanceof TMLCPPanel) & index != 0);
    }

    public boolean renameEnabled(int index) {
        if (index ==0) {
            return false;
        }
        return (panels.elementAt(index) instanceof TMLCPPanel) || (panels.elementAt(index) instanceof TMLSDPanel) || ((panels.elementAt(index) instanceof TMLCPPanel) & index != 0);
    }


    public boolean isTMLSDEnabled() {
        return true;
    }

}

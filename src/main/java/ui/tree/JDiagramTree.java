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


package ui.tree;

//import java.awt.*;

import common.*;
import help.HelpEntry;
import help.HelpManager;
import translator.CheckingError;
import tmltranslator.TMLCheckingError;
import ui.*;
import graph.RG;
import myutil.*;
import ui.avatarinteractivesimulation.JFrameAvatarInteractiveSimulation;
import ui.interactivesimulation.JFrameInteractiveSimulation;
import ui.window.JFrameHelp;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//import translator.*;


/**
 * Class JDiagramTree
 * Dialog for managing attributes
 * Creation: 14/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 14/12/2003
 */
public class JDiagramTree extends javax.swing.JTree implements ActionListener, MouseListener, TreeExpansionListener, TreeSelectionListener, Runnable {
    private boolean toUpdate = false;
    private MainGUI mgui;
    private DiagramTreeModel dtm;

    //for update
    private Set<TreePath> m_expandedTreePaths = new HashSet<>();
    private TreePath[] m_selectedTreePaths = new TreePath[0];
    //private boolean m_nodeWasSelected = false;

    protected JMenuItem jmiAnalyze;
    protected JMenuItem jmiShow;
    protected JMenuItem jmiMinimize;
    protected JMenuItem jmiRefusalGraph;
    protected JMenuItem jmiRemove;
    protected JMenuItem jmiShowInFinder;
    protected JMenuItem jmiSelectInSimulator;
    protected JMenuItem jmiShowST;
    protected JMenuItem jmiShowInFinderST;
    protected JMenuItem jmiCompareST;
    protected JMenuItem jmiLatencyAnalysisST;
    protected JMenuItem jmiCompareLatencyAnalysisST;
    protected JPopupMenu popupTree;
    protected JPopupMenu popupTreeST;
    protected RG selectedRG;
    protected SimulationTrace selectedST;

    protected JPopupMenu popupGraphTree;
    protected JPopupMenu popupSimulationTraceTree;
    protected JMenuItem jmiAddSimTraceFromFile;
    protected JMenuItem jmiAddFromFile;
    protected GraphTree selectedGT;
    protected SimulationTraceTree selectedSTT;


    /*
     * Creates new form
     */
    public JDiagramTree(MainGUI _mgui) {
        super(new DiagramTreeModel(_mgui));

        //TraceManager.addDev("TREE CREATED");

        mgui = _mgui;
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setEditable(false);
        addMouseListener(this);
        addTreeExpansionListener(this);
        addTreeSelectionListener(this);
    }

    public void reinit() {
        m_expandedTreePaths.clear();
        m_selectedTreePaths = new TreePath[0];
    }

    public void toBeUpdated() {
        toUpdate = true;
    }

    public void updateNow() {
        if (toUpdate) {
            forceUpdate();
        }
    }

    public void forceUpdate() {
        toUpdate = false;
        dtm = new DiagramTreeModel(mgui);
        setModel(dtm);
        update();
    }

    public void mousePressed(MouseEvent e) {
        //TraceManager.addDev("Mouse event");
        if (SwingUtilities.isRightMouseButton(e)) {
            //TraceManager.addDev("right mouse event. popup trigger? " + e.isPopupTrigger());
            if (e.isPopupTrigger()) myPopupEvent(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        //TraceManager.addDev("Mouse event");
        if (SwingUtilities.isRightMouseButton(e)) {
            //TraceManager.addDev("right mouse event. popup trigger? " + e.isPopupTrigger());
            if (e.isPopupTrigger()) myPopupEvent(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
        updateNow();
    }

    public void mouseExited(MouseEvent e) {
        setSelectionPath(null);
        m_selectedTreePaths = new TreePath[0];
    }

    public void mouseClicked(MouseEvent e) {

    }


    private void myPopupEvent(MouseEvent e) {

         //TraceManager.addDev("myPopupEvent");

        int x = e.getX();
        int y = e.getY();
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(x, y);

        //TraceManager.addDev("Path=" + path);

        if (path == null) {
            //TraceManager.addDev("Null path");
            return;
        }

        tree.setSelectionPath(path);

        Object obj = path.getLastPathComponent();

        //TraceManager.addDev("Adding popup menu to " + obj.getClass() + "/" + obj);

        if (obj instanceof SimulationTraceTree) {
            selectedSTT = (SimulationTraceTree) obj;
            if (popupSimulationTraceTree == null) {
                popupSimulationTraceTree = new JPopupMenu();
                jmiAddSimTraceFromFile = new JMenuItem("Add simulation trace from file (.csv)");
                jmiAddSimTraceFromFile.addActionListener(this);
                popupSimulationTraceTree.add(jmiAddSimTraceFromFile);
            }
            popupSimulationTraceTree.show(tree, x, y);
        }

        if (obj instanceof GraphTree) {
            selectedGT = (GraphTree) obj;
            if (popupGraphTree == null) {
                popupGraphTree = new JPopupMenu();
                jmiAddFromFile = new JMenuItem("Add graph from file (.aut)");
                jmiAddFromFile.addActionListener(this);
                popupGraphTree.add(jmiAddFromFile);
            }
            popupGraphTree.show(tree, x, y);
        }


        if (obj instanceof RG) {
            //TraceManager.addDev("RG object");
            selectedRG = (RG) obj;
            selectedST = null;
            if (popupTree == null) {
                popupTree = new JPopupMenu();
                jmiAnalyze = new JMenuItem("Analyze");
                jmiAnalyze.addActionListener(this);
                jmiShow = new JMenuItem("Show");
                jmiShow.addActionListener(this);
                jmiMinimize = new JMenuItem("Minimize");
                jmiMinimize.addActionListener(this);
                jmiRefusalGraph = new JMenuItem("Make Test Sequences");
                jmiRefusalGraph.addActionListener(this);
                jmiRemove = new JMenuItem("Delete");
                jmiRemove.addActionListener(this);
                jmiShowInFinder = new JMenuItem("Show in File Explorer");
                jmiShowInFinder.addActionListener(this);
                popupTree.add(jmiAnalyze);
                popupTree.add(jmiShow);
                popupTree.add(jmiMinimize);
                popupTree.add(jmiRefusalGraph);
                popupTree.addSeparator();
                popupTree.add(jmiRemove);
                popupTree.add(jmiShowInFinder);
            }
            popupTree.show(tree, x, y);
        }

        if (obj instanceof SimulationTrace) {
            //TraceManager.addDev("RG object");
            selectedST = (SimulationTrace) obj;
            selectedRG = null;
            //if (popupTree == null) {
                popupTreeST = new JPopupMenu();
                if (selectedST.getType() == SimulationTrace.VCD_DIPLO) {
                    jmiShowST = new JMenuItem("Show with gtkwave");
                } else {
                    jmiShowST = new JMenuItem("Show (default app)");
                }
                jmiShowST.addActionListener(this);
                popupTreeST.add(jmiShowST);
                if (selectedST.hasFile()) {
                    jmiShowInFinderST = new JMenuItem("Show in File Explorer");
                    jmiShowInFinderST.addActionListener(this);
                }
                if (selectedST.hasContent() && selectedST.getType() == SimulationTrace.CSV_AVATAR) {
                    jmiSelectInSimulator = new JMenuItem("Select in simulator");
                    jmiSelectInSimulator.addActionListener(this);
                }

                popupTreeST.add(jmiShowInFinderST);
                if (jmiSelectInSimulator != null) {
                    popupTreeST.add(jmiSelectInSimulator);
                }
                
                if (selectedST.getType() == SimulationTrace.XML_DIPLO) {
                    
                    jmiCompareST = new JMenuItem("Compare Simulation Traces");
                    jmiCompareST.addActionListener(this);
                    popupTreeST.add(jmiCompareST);
                    
                    jmiLatencyAnalysisST  = new JMenuItem("Latency Analysis");
                    jmiLatencyAnalysisST.addActionListener(this);
                    popupTreeST.add(jmiLatencyAnalysisST);
                    
                    jmiCompareLatencyAnalysisST  = new JMenuItem("Compare Latency Analysis");
                    jmiCompareLatencyAnalysisST.addActionListener(this);
                    popupTreeST.add(jmiCompareLatencyAnalysisST);

                }



            //}
            popupTreeST.show(tree, x, y);
        }
    }

    public synchronized void run() {
        checkPaths();
        Iterator<TreePath> l_keys = m_expandedTreePaths.iterator();
        TreePath l_path = null;
        while (l_keys.hasNext()) {
            try {
                l_path = l_keys.next();
                TreePath parent = l_path.getParentPath();
                //
                //
                if ((l_path.getPathCount() == 2) || (m_expandedTreePaths.contains(parent))) {
                    //TraceManager.addDev("Path=" + l_path);
                    expandPath(l_path);
                }
            } catch (Exception e) {
                //
                if (l_path != null) {
                    //
                    m_expandedTreePaths.remove(l_path);
                }
            }
        }
        getSelectionModel().setSelectionPaths(m_selectedTreePaths);
    }

    private void checkPaths() {
        TreePath l_path = null;
        Iterator<TreePath> l_keys = m_expandedTreePaths.iterator();
        while (l_keys.hasNext()) {
            l_path = l_keys.next();
            if (!isAPathOf(l_path)) {
                m_expandedTreePaths.remove(l_path);
            }
        }

    }

    private boolean isAPathOf(TreePath tp) {
        if ((dtm == null) || (tp == null)) {
            return false;
        }

        Object[] objs = tp.getPath();

        if (objs.length == 0) {
            return false;
        }

        if (objs[0] != dtm.getRoot()) {
            return false;
        }

        int index;

        for (int i = 0; i < objs.length - 2; i++) {
            index = dtm.getIndexOfChild(objs[i], objs[i + 1]);
            if (index == -1) {
                return false;
            }
        }

        return true;

    }

    public synchronized void update() {
        SwingUtilities.invokeLater(this);
    }

    public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
        TreePath tp = treeExpansionEvent.getPath();
        m_expandedTreePaths.add(tp);
        for (TreePath m_expandedTreePath : m_expandedTreePaths) {
            TreePath l_path = null;
            try {
                l_path = m_expandedTreePath;
                TreePath parent = l_path.getParentPath();
                if ((l_path.getPathCount() == 1) || (m_expandedTreePaths.contains(parent))) {
                    expandPath(l_path);
                }
            } catch (Exception e) {
                if (l_path != null) {
                    //
                    m_expandedTreePaths.remove(l_path);
                }
            }
        }

    }

    public void expandMyPath(TreePath tp) {
        //TraceManager.addDev("Path=" + tp);
        expandPath(tp);
    }

    public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
        m_expandedTreePaths.remove(treeExpansionEvent.getPath());
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        //TraceManager.addDev("Value changed");

        if (getSelectionPaths() != null && getSelectionPaths().length > 0) {
            m_selectedTreePaths = getSelectionModel().getSelectionPaths();
        }

        TreePath tp = treeSelectionEvent.getNewLeadSelectionPath();

        //TraceManager.addDev("Expanded path=" + tp);

        if (tp == null) {
            return;
        }



        Object nodeInfo = tp.getLastPathComponent();
        //TraceManager.addDev("NodeInfo:" + nodeInfo);
        Object o;

        if (nodeInfo instanceof TDiagramPanel) {
            mgui.selectTab((TDiagramPanel) nodeInfo);
        } else if (nodeInfo instanceof TURTLEPanel) {
            mgui.selectTab((TURTLEPanel) nodeInfo);
        } else if (nodeInfo instanceof TGComponent) {
            TGComponent tgc = (TGComponent) nodeInfo;
            mgui.selectTab(tgc.getTDiagramPanel());
            tgc.getTDiagramPanel().highlightTGComponent(tgc);
        } else if (nodeInfo instanceof Invariant) {
            //TraceManager.addDev("Click on invariant");
            Invariant inv = (Invariant) nodeInfo;
            mgui.setCurrentInvariant(inv);
            for (int i = 2; i < inv.getChildCount(); i++) {
                o = inv.getChild(i);
                if (o instanceof TGComponent) {
                    TGComponent tgc1 = (TGComponent) (o);
                    tgc1.getTDiagramPanel().repaint();
                }

                if (o instanceof InvariantSynchro) {
                    InvariantSynchro is = (InvariantSynchro) o;
                    is.getFrom().getTDiagramPanel().repaint();
                    is.getTo().getTDiagramPanel().repaint();
                }

            }
        } else if (nodeInfo instanceof CheckingError) {
            CheckingError ce = (CheckingError) nodeInfo;
            TDiagramPanel tdp = null;
            if (ce instanceof UICheckingError) {
                tdp = ((UICheckingError) ce).getTDiagramPanel();
                TGComponent tgc = ((UICheckingError) ce).getTGComponent();
                if (tgc != null) {
                    tgc.getTDiagramPanel().highlightTGComponent(tgc);
                }
            }

            if (tdp != null) {
                mgui.selectTDiagramPanel(tdp);
            } else if (ce.getTClass() != null) {
                mgui.selectTab(ce.getTClass().getName());
            } else if (ce.getRelation() != null) {
                mgui.selectTab("Class diagram");
            } else if (ce instanceof TMLCheckingError && ((TMLCheckingError) ce).getTMLActivityElement() != null) {
                TGComponent tgc = (TGComponent) ((TMLCheckingError) ce).getTMLActivityElement().getReferenceObject();
                tgc.getTDiagramPanel().highlightTGComponent(tgc);
                mgui.selectTDiagramPanel(tgc.getTDiagramPanel());
            } else if (ce instanceof TMLCheckingError && ((TMLCheckingError) ce).getTMLTask() != null) {
                mgui.selectTab(((TMLCheckingError) ce).getTMLTask().getName());
            }
        } else if (nodeInfo instanceof RG) {
            /*RG rg = (RG)nodeInfo;
              if (rg.data != null) {
              mgui.showAUT("Last RG", rg.data);
              }*/

        } else if (nodeInfo instanceof HelpEntry) {
            mgui.openHelpFrame((HelpEntry)nodeInfo);
        } else if (nodeInfo instanceof HelpTree) {
            mgui.openHelpFrame(((HelpTree)nodeInfo).getHelpManager());
        }
    }

    public void showSimulationTrace() {
        TraceManager.addDev("Showing simulation trace");

        if (selectedST.getType() == SimulationTrace.VCD_DIPLO) {
            String gtkwavePath = ConfigurationTTool.GTKWavePath;
            if ((gtkwavePath != null) && (gtkwavePath.length() > 0)) {
                mgui.runGTKWave(selectedST.getFullPath());
                return ;
            }
        }

        if (selectedST.hasFile()) {
            mgui.showInFinder(selectedST, false);
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (selectedRG != null) {
            if (ae.getSource() == jmiAnalyze) {
                mgui.showAUTFromRG(selectedRG.name, selectedRG);

            } else if (ae.getSource() == jmiShow) {

                    if (selectedRG.graph != null) {
                        selectedRG.graph.display();
                    } else {
                        mgui.displayAUTFromRG(selectedRG.name, selectedRG);
                    }

            } else if (ae.getSource() == jmiRemove) {
                if (selectedRG != null) {
                    mgui.removeRG(selectedRG);
                    selectedRG = null;
                }

            } else if (ae.getSource() == jmiMinimize) {
                if (selectedRG != null) {
                    mgui.minimizeRG(selectedRG);
                }
            } else if (ae.getSource() == jmiRefusalGraph) {
                if (selectedRG != null) {
                    mgui.makeRefusalGraph(selectedRG);
                }
            } else if (ae.getSource() == jmiShowInFinder) {
                if (selectedRG != null) {
                    mgui.showInFinder(selectedRG);
                }

            }

        }

        if (selectedST != null) {
           if (ae.getSource() == jmiShowST) {
               showSimulationTrace();

            } else if (ae.getSource() == jmiShowInFinderST) {
               mgui.showInFinder(selectedST, true);

            } else if (ae.getSource() == jmiSelectInSimulator) {
               mgui.setSimulationTraceSelected(selectedST);

           } else if (ae.getSource() == jmiCompareST) {
                mgui.compareSimulationTraces(selectedST, true);
               
           } else if (ae.getSource() == jmiLatencyAnalysisST) {
                try {
                    mgui.latencyDetailedAnalysisForXML(selectedST, true , false,1);
                } catch (Exception e) {
                    TraceManager.addDev("Error in latency analysis: " + e.getMessage());
                }
               
           } else if (ae.getSource() == jmiCompareLatencyAnalysisST) {
                     mgui.compareLatencyForXML(selectedST, false);
              }
         }


        if (selectedSTT != null) {
            if (ae.getSource() == jmiAddSimTraceFromFile) {

                //TraceManager.addDev("Adding simulation trace from file");
                String[] st = mgui.loadSimulationTraceCSV();
                if (st != null) {
                    SimulationTrace sim = new SimulationTrace(st[0], SimulationTrace.CSV_AVATAR, st[1]);
                    //TraceManager.addDev("Content=" + st[2]);
                    sim.setContent(st[2]);
                    mgui.addSimulationTrace(sim);
                }

            }
        }

        if (selectedGT != null) {
            if (ae.getSource() == jmiAddFromFile) {

                //TraceManager.addDev("Adding graph from file");
                String[] graph = mgui.loadAUTGraph();
                if (graph != null) {
                    RG rg = new RG(graph[0]);
                    rg.fileName = graph[0];
                    rg.data = graph[1];
                    mgui.addRG(rg);
                }

            }
        }
    }
}

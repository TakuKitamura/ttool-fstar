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


package myutil;

//import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//import translator.*;


/**
 * Class JTreeStats
 * Tree for managing stats
 * Creation: 14/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 11/01/2021
 */
public class JTreeStats extends javax.swing.JTree implements ActionListener, MouseListener, TreeExpansionListener, TreeSelectionListener, Runnable {
    private boolean toUpdate = false;
    private JFrameStatistics jFStats;
    private TreeModelStats dtm;

    //for update
    private Set<TreePath> m_expandedTreePaths = new HashSet<>();
    private TreePath[] m_selectedTreePaths = new TreePath[0];
    //private boolean m_nodeWasSelected = false;

    protected JMenuItem showAllSelectedCharts;
    protected JMenuItem showHistogram, showPieChart, showTimeValueChart, showValueEvolutionChart;
    protected JMenuItem saveAsCSVMI;
    protected JPopupMenu popupTree;
    protected DataElement selectedDataElement;


    /*
     * Creates new form
     */
    public JTreeStats(JFrameStatistics _jFStats) {
        super(new TreeModelStats(_jFStats));

        //TraceManager.addDev("TREE CREATED");

        jFStats = _jFStats;
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
        dtm = new TreeModelStats(jFStats);
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
        selectedDataElement = null;

        if (obj instanceof DataElement) {
            selectedDataElement = (DataElement)obj;
            if (popupTree == null) {
                popupTree = new JPopupMenu();

                showAllSelectedCharts = new JMenuItem("Show all selected Charts");
                showAllSelectedCharts.addActionListener(this);

                showHistogram = new JMenuItem("Show Histogram");
                showHistogram.addActionListener(this);

                showPieChart = new JMenuItem("Show Pie Chart");
                showPieChart.addActionListener(this);

                showTimeValueChart = new JMenuItem("Show Value = f(t) Chart");
                showTimeValueChart.addActionListener(this);

                showValueEvolutionChart = new JMenuItem("Show Value = f(t) per Simulation Chart");
                showValueEvolutionChart.addActionListener(this);


                saveAsCSVMI = new JMenuItem("Save data in CSV format");
                saveAsCSVMI.addActionListener(this);

                popupTree.add(showAllSelectedCharts);
                popupTree.addSeparator();
                popupTree.add(showHistogram);
                popupTree.add(showPieChart);
                popupTree.add(showTimeValueChart);
                popupTree.add(showValueEvolutionChart);
                popupTree.addSeparator();
                popupTree.add(saveAsCSVMI);
            }
            popupTree.show(tree, x, y);
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

        if (nodeInfo instanceof DataElement) {
            //if ( ((DataElement)(nodeInfo)).isLeaf()) {
                //jFStats.showStats((DataElement) nodeInfo);
            //}
        }
    }


    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == saveAsCSVMI) {
            if (selectedDataElement != null) {
                TraceManager.addDev("Save in CSV format");

                String csvData = selectedDataElement.getCSVData();
                if ((csvData == null) || (csvData.length() == 0)) {
                    JOptionPane.showMessageDialog(jFStats,
                            "Empty data",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JFileChooser jfc = new JFileChooser();
                int returnVal = jfc.showDialog(this, "Select file");
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File selectedFile = jfc.getSelectedFile();

                if (selectedFile != null) {
                    try {
                        FileUtils.saveFile(selectedFile, csvData);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(jFStats,
                                "Could not save the file: " + e.getMessage(),
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

            // Find the related DataElement
        } else if (ae.getSource() == showAllSelectedCharts) {
            if (selectedDataElement != null) {
                jFStats.showStats(selectedDataElement);
            }
        } else if (ae.getSource() == showHistogram) {
            if (selectedDataElement != null) {
                jFStats.showHistogram(selectedDataElement);
            }
        } else if (ae.getSource() == showPieChart) {
            if (selectedDataElement != null) {
                jFStats.showPieChart(selectedDataElement);
            }
        } else if (ae.getSource() == showTimeValueChart) {
            if (selectedDataElement != null) {
                jFStats.showTimeValueChart(selectedDataElement);
            }
        } else if (ae.getSource() == showValueEvolutionChart) {
            if (selectedDataElement != null) {
                jFStats.showValueEvolutionChart(selectedDataElement);
            }
        }

    }


}

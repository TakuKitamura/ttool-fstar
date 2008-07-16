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
 * Class JDiagramTree
 * Dialog for managing attributes
 * Creation: 14/12/2003
 * @version 1.0 14/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tree;

//import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;

//import translator.*;
import ui.*;


public class JDiagramTree extends javax.swing.JTree implements MouseListener, TreeExpansionListener, TreeSelectionListener, Runnable   {
    private boolean toUpdate = false;
    private MainGUI mgui;
    private DiagramTreeModel dtm;
    
    //for update
    private Set m_expandedTreePaths = new HashSet();
    private TreePath[] m_selectedTreePaths = new TreePath[0];
    //private boolean m_nodeWasSelected = false;
    
    
    /** Creates new form  */
    public JDiagramTree(MainGUI _mgui) {
        super(new DiagramTreeModel(_mgui));
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
        if(toUpdate) {
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
        
    }
    
    public void mouseReleased(MouseEvent e) {
        
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
    
    public synchronized void run(){
        checkPaths();
        Iterator l_keys = m_expandedTreePaths.iterator();
        TreePath l_path = null;
        while(l_keys.hasNext()){
            try {
                l_path = (TreePath) l_keys.next();
                TreePath parent = l_path.getParentPath();
                //System.out.println("Path: " + l_path);
                //System.out.println("Parent path: " + parent);
                if ((l_path.getPathCount() == 2) || (m_expandedTreePaths.contains(parent))) {
                    expandPath(l_path);
                }
            } catch (Exception e) {
                //System.out.println("Exception " + e.getMessage());
                if (l_path != null) {
                    //System.out.println("Removing path " + l_path);
                    m_expandedTreePaths.remove(l_path);
                }
            }
        }
        getSelectionModel().setSelectionPaths(m_selectedTreePaths);
    }
    
    private void checkPaths() {
        TreePath l_path = null;
        Iterator l_keys = m_expandedTreePaths.iterator();
        while(l_keys.hasNext()){
            l_path = (TreePath) l_keys.next();
            if (!isAPathOf(l_path)) {
                m_expandedTreePaths.remove(l_path);
            }
        }
        
    }
    
    private boolean isAPathOf(TreePath tp) {
        if ((dtm ==null) || (tp == null)) {
            return false;
        }
        
        Object [] objs = tp.getPath();
        
        if (objs.length == 0) {
            return false;
        }
        
        if (objs[0] != dtm.getRoot()) {
            return false;
        }
        
        int index;
        
        for(int i=0; i<objs.length - 2; i++) {
            index = dtm.getIndexOfChild(objs[i], objs[i+1]);
            if (index == -1) {
                return false;
            }
        }
        
        return true;
        
    }
    
    public synchronized void update(){
        SwingUtilities.invokeLater(this);
    }
    
    public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
        TreePath tp = treeExpansionEvent.getPath();
        m_expandedTreePaths.add(tp);
        Iterator l_keys = m_expandedTreePaths.iterator();
        while(l_keys.hasNext()){
            TreePath l_path = null;
            try {
                l_path = (TreePath) l_keys.next();
                TreePath parent = l_path.getParentPath();
                if ((l_path.getPathCount() == 1) || (m_expandedTreePaths.contains(parent))) {
                    expandPath(l_path);
                }
            } catch (Exception e) {
                if (l_path != null) {
                    //System.out.println("Removing path " + l_path);
                    m_expandedTreePaths.remove(l_path);
                }
            }
        }
        
    }
    
    public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
        m_expandedTreePaths.remove(treeExpansionEvent.getPath());
    }
    
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        if(getSelectionPaths() != null && getSelectionPaths().length >0 ){
            m_selectedTreePaths = getSelectionModel().getSelectionPaths();
        }
        
        TreePath tp = treeSelectionEvent.getNewLeadSelectionPath();
        if (tp == null) {
            return;
        }
        
        Object nodeInfo = tp.getLastPathComponent();
        
        if (nodeInfo instanceof TDiagramPanel) {
            mgui.selectTab((TDiagramPanel)nodeInfo);
        } else if (nodeInfo instanceof TURTLEPanel) {
            mgui.selectTab((TURTLEPanel)nodeInfo);
        } else if (nodeInfo instanceof TGComponent) {
            TGComponent tgc = (TGComponent) nodeInfo;
            mgui.selectTab(tgc.getTDiagramPanel());
            tgc.getTDiagramPanel().highlightTGComponent(tgc);
        } else if (nodeInfo instanceof CheckingError) {
            CheckingError ce = (CheckingError)nodeInfo;
            TDiagramPanel tdp; TGComponent tgc;
            tdp = ce.getTDiagramPanel();
            tgc = ce.getTGComponent();
            if (tdp != null) {
                mgui.selectTDiagramPanel(tdp);
            }
            if (tgc != null) {
                tgc.getTDiagramPanel().highlightTGComponent(tgc);
            }
            if (ce.getTClass() != null) {
                mgui.selectTab(ce.getTClass().getName());
            } else if(ce.getRelation() != null) {
                mgui.selectTab("Class diagram");
            } else if (ce.getTMLTask() != null) {
                mgui.selectTab(ce.getTMLTask().getName());
            }
        }
    }
    
}

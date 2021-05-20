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

package ui.window;

import myutil.GenericTree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

/**
 * Class TreeModelStats Model for the tree to know its data Creation: 14/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 14/12/2003
 */
public class TreeModelStats implements TreeModel {
  private JFrameDataElementStatistics jFStats;
  private Vector<TreeModelListener> treeModelListeners = new Vector<>();

  public TreeModelStats(JFrameDataElementStatistics _jFStats) {
    // super(_mgui.gtm);
    jFStats = _jFStats;
  }

  /**
   * Adds a listener for the TreeModelEvent posted after the tree changes.
   */
  public void addTreeModelListener(TreeModelListener l) {
    treeModelListeners.addElement(l);
  }

  /**
   * Returns the child of parent at index index in the parent's child array.
   */
  public Object getChild(Object parent, int index) {
    //
    if (parent instanceof GenericTree) {
      return ((GenericTree) parent).getChild(index);
    }
    return null;
  }

  /**
   * Returns the number of children of parent.
   */
  public int getChildCount(Object parent) {
    //
    if (parent instanceof GenericTree) {
      return ((GenericTree) parent).getChildCount();
    }

    return 0;
  }

  /**
   * Returns the index of child in parent.
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (parent instanceof GenericTree) {
      return ((GenericTree) parent).getIndexOfChild(child);
    }

    return -1;
  }

  /**
   * Returns the root of the tree.
   */
  public Object getRoot() {
    return jFStats;
  }

  /**
   * Returns true if node is a leaf.
   */
  public boolean isLeaf(Object node) {
    if (node instanceof GenericTree) {
      return (((GenericTree) node).getChildCount() == 0);
    }

    return true;

  }

  /**
   * Removes a listener previously added with addTreeModelListener().
   */
  public void removeTreeModelListener(TreeModelListener l) {
    treeModelListeners.removeElement(l);
  }

  /**
   * Messaged when the user has altered the value for the item identified by path
   * to newValue. Not used by this model.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
    //
  }
}

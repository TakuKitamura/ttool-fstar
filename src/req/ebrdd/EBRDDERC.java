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
 * Class EBRDDERC
 * Creation: 22/09/2009
 * @version 1.0 22/09/2009
 * @author Ludovic APVRILLE
 * @see
 */

package req.ebrdd;

import java.util.*;


public class EBRDDERC extends EBRDDComponent {
	protected ArrayList<ERCElement> treeElements;
	protected ESO root;
	
    public EBRDDERC() {
		treeElements = new ArrayList<ERCElement>();
    }
    
    public String toString() {
        return "EBRDERC: " + treeElements.size();
    }
	
	
	public void setRoot(ESO _root) {
		root = _root;
	}
	
	public ESO getRoot() {
		return root;
	}
	
	public ArrayList<ERCElement> getTreeElements() {
		return treeElements;
	}
	
	public void addTreeElement(ERCElement elt) {
		treeElements.add(elt);
	}
	
	//From its list of tree elements, it tries to find the root
	// of the tree. If it returns false, no root could be found,
	// either because the tree contains no element or because
	// there are several roots. Otherwise, it returns true
	// Additionally, there shall be no loop in the system
	
	public boolean makeRoot() {
		if (treeElements.size() ==0) {
			return false;
		}
		
		boolean b;
		int cpt;
		ESO root = null;
		
		for (ERCElement elt: treeElements) {
			if (elt instanceof ERB) {
				cpt = nbOfESOLeadingTo((ERB)elt);
				if (cpt != 1) {
					// The ERB is the son of several ESOs or
					// The ERB is not attached to an ESO
					return false;
				}
			}
			
			if (elt instanceof ESO) {
				// Must check that all ESO have at least one son
				if (((ESO)elt).getNbOfSons() == 0) {
					return false;
				}
				
				
				
				// Check whether it is a root of a tree, or not
				if (isRoot((ESO)elt)) {
					if (root != null) {
						// Second root!!
						return false;
					} else {
						root = (ESO)elt;
					}
				}
			}
		}
		
		// no root found!
		if (root == null) {
			return false;
		}
		
		// Must check that there is no cycle in the tree
		ArrayList<ERCElement> mets = new ArrayList<ERCElement>();
		if (!hasCycle(root, mets)) {
			return false;
		}
		
		return true;
	}
	
	private boolean hasCycle(ESO _eso, ArrayList<ERCElement> _mets) {
		if (_mets.contains(_eso)) {
			return true;
		}
		
		_mets.add(_eso);
		ArrayList<ERCElement> list = _eso.getAllSons();
			
		for(ERCElement elt: list) {
			if (elt instanceof ESO) {
				if  (hasCycle((ESO)elt, _mets)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Check whether this is a son, or not.
	private boolean isRoot(ESO eso) {
		for (ERCElement elt: treeElements) {
			if (elt instanceof ESO) {
				if (((ESO)elt).isOneOfMySon(eso)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	private int nbOfESOLeadingTo(ERCElement son) {
		int cpt = 0;
		
		for (ERCElement elt: treeElements) {
			if (elt instanceof ESO) {
				cpt += ((ESO)elt).nbOfSonsEqualTo(son);
			}
		}
		
		return cpt;
	}
}
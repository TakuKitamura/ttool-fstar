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
* Class ESO
* Creation: 22/09/2009
* @version 1.0 22/09/2009
* @author Ludovic APVRILLE
* @see
*/

package req.ebrdd;

import java.util.*;

public class ESO extends ERCElement {
	//public final static String [] ESOS = {"Conjunction", "Disjunction", "Sequence", "Strict sequence", "Simultaneous", "At least/At most"};
	
    protected ArrayList<ERCElement> sons;
	protected int id;
	protected int timeout;
	protected boolean oncePerEvent;
	protected int n, m;
	
	private Vector indexes;
	
    
    public ESO() {
        sons = new ArrayList<ERCElement>();
		indexes = new Vector();
    }
	
	public void addSon(ERCElement ercelt) {
		sons.add(ercelt);
	}
    
	public void addIndex(int index) {
		indexes.add(new Integer(index));
    }
	
	public void sortNexts() {
		if (indexes.size() == 0) {
			return;
		}
		
		//System.out.println("Nb of indexes" + indexes.size());
		//System.out.println("Nb of nexts" + nexts.size());
		ArrayList<ERCElement> nextsbis = new ArrayList<ERCElement>();
		
		// Sort according to index stored in indexes
		// The smaller is removed at each step
		Integer i0;
		int index;
		int i;
		
		while(indexes.size() > 0) {
			i0 = new Integer(1000);
			index = -1;
			for(i=0; i<indexes.size(); i++) {
				if ((((Integer)indexes.elementAt(i)).compareTo(i0))<0) {
					index = i;
					i0 = ((Integer)indexes.elementAt(i));
				}
			}
			nextsbis.add(sons.get(index));
			sons.remove(index);
			indexes.remove(index);
		}
		
		sons = nextsbis;
		
		//for(i=0; i<nexts.size(); i++){
		// System.out.println("sequence #" + i + " = " + nexts.elementAt(i));
		//}
		
    }
    
    public ERCElement getSon(int index) {
        if (index < sons.size()) {
            return sons.get(index);
        } else {
            return null;
        }
    }
    
    public int getNbOfSons() {
		return  sons.size();
    }
    
    public ArrayList<ERCElement> getAllSons() {
        return sons;
    }
	
	public int getID() {
		return id;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public boolean getOncePerEvent() {
		return oncePerEvent;
	}
	
	public int getN() {
		return n;
	}
	
	public int getM() {
		return m;
	}
	
	public void setID(int _id) {
		id = _id;
	}
	
	public void setTimeout(int _timeout) {
		timeout = _timeout;
	}
	
	public void setOncePerEvent(boolean _oncePerEvent) {
		oncePerEvent = _oncePerEvent;
	}
	
	public void setN(int _n) {
		n = _n;
	}
	
	public void setM(int _m) {
		m = _m;
	}
	
	public boolean isOneOfMySon(ERCElement son) {
		return sons.contains(son);
	}
	
	public int nbOfSonsEqualTo(ERCElement son) {
		int cpt = 0;
		
		for(ERCElement elt: sons) {
			if (elt == son) {
				cpt ++;
			}
		}
		
		return cpt;
	}
    
}
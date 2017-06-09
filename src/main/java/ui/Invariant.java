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
 * Class Invariant implements GenericTree
 * Invariant computed from Avatar diagrams
 * Creation: 15/02/2012
 * @version 1.0 15/02/2012
 * @author Ludovic APVRILLE
 * @see TGComponent
 */
 
package ui;

import myutil.GenericTree;
import myutil.TraceManager;

import java.util.LinkedList;


public class Invariant implements GenericTree {

	private String name;
	private int tokenValue; // Invariant on a given nb of tokens;
	private int value; // value on the incidence matrix after computation of the invariant
	private LinkedList<TGComponent> components;
	private LinkedList<InvariantSynchro> synchros;
	
	public Invariant(String _name) {
		name = _name;
		components = new LinkedList<TGComponent>();
		synchros = new LinkedList<InvariantSynchro>();
	}
	
	public void setTokenValue(int _value) {
		tokenValue = _value;
	}
	
	
	public void setValue(int _value) {
		value = _value;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTokenValue() {
		return tokenValue;
	}
	
	public int getValue() {
		return value;
	}
	
	public LinkedList<TGComponent> getComponents() {
		return components;
	}
	
	public void addSynchro(InvariantSynchro _synchro) {
		// Look for similar synchro
		for(InvariantSynchro is: synchros) {
			if ((is.getFrom() == _synchro.getFrom()) &&  (is.getTo() == _synchro.getTo())) {
				return;
			}
		}
		
		synchros.add(_synchro);
	}
	
	public void addComponent(TGComponent _tgc) {
		if (_tgc == null) {
			TraceManager.addDev("NULL Component added to invariant -> IGNORING");
			return;
		}
		
		//Component already belongs to invariant?
		
		if (components.contains(_tgc)) {
			//TraceManager.addDev("Duplicated component:" + _tgc);
			return;
			
		}
		
		components.add(_tgc);
	}
	
	public void computeValue() {
		value = components.size() + synchros.size();
	}
	
    
	public String toString() {
        return "(" + value + ") " + name;
    }
    
    public int getChildCount() {
        return 2 + synchros.size() + components.size();
    }
    
    public Object getChild(int index) {
    	if (index == 0) {
    		return "Nb of elements: " + value;
    	}
    	
    	if (index == 1) {
    		return "Token value: " + tokenValue;
    	}
    	
    	
    	if (index-2 < synchros.size()) {
    		return synchros.get(index-2);
    	}
    	
    	index -= synchros.size();
    	
    	TGComponent tgc  = components.get(index-2);
    	//TraceManager.addDev("Getting at index #" + (index-2) + " = " + tgc);
    	
    	
    	return components.get(index-2);
    	
    }
    
    public int getIndexOfChild(Object child) {
    	if (child instanceof String) {
    		String s = (String)child;
    		if (s.startsWith("value")) {
    			return 0;
    		}
    		return 1;
    	}
    	
    	if (child instanceof InvariantSynchro) {
    		return synchros.indexOf(child)+2;
    	}
    	
    	return components.indexOf(child)+2+synchros.size();
    }
    
    public boolean containsComponent(TGComponent tgc) {
    	for(InvariantSynchro is: synchros) {
    		if (is.containsComponent(tgc)) {
    			return true;
    		}
    	}
    	return components.contains(tgc);
    }
    
}




    



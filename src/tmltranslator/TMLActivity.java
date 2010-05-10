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
 * Class TMLActivity
 * Creation: 23/11/2005
 * @version 1.0 23/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;

import java.util.*;


public class TMLActivity extends TMLElement {
    private TMLActivityElement first;
    private Vector elements;
 
    
    public TMLActivity(String name, Object reference) {
        super(name, reference);
        elements = new Vector();
    }
	
	public boolean contains(TMLActivityElement _elt) {
		return elements.contains(_elt);
	}
    
    public void setFirst(TMLActivityElement _tmlae) {
        first = _tmlae;
        addElement(_tmlae);
    }
    
    public TMLActivityElement getFirst() {
        return first;
    }
    
    public TMLActivityElement get(int index) {
        return (TMLActivityElement)(elements.elementAt(index));
    }
	
	public void removeElementAt(int index) {
        elements.removeElementAt(index);
    }
	
	public void removeElement(TMLActivityElement _element) {
        elements.remove(_element);
    }
    
    public int nElements() {
        return elements.size();
    }
    
    public void addElement(TMLActivityElement _tmlae) {
        elements.add(_tmlae);
    }
    
    
    public TMLActivityElement findReferenceElement(Object reference) {
        TMLActivityElement ae;
        for(int i=0; i<elements.size(); i++) {
            ae = (TMLActivityElement)(elements.elementAt(i));
            if (ae.getReferenceObject() == reference) {
                return ae;
            }
        }
        return null;
    }
	
	public int getMaximumSelectEvtSize() {
		int found = -1;
		int next;
		TMLActivityElement ae;
        for(int i=0; i<elements.size(); i++) {
            ae = (TMLActivityElement)(elements.elementAt(i));
            if (ae instanceof TMLSelectEvt) {
				next = ae.getNbNext();
                if (next>found) {
					found = next;
				}
            }
        }
        return found;
	}
	
	private void replaceAllNext(TMLActivityElement _oldE, TMLActivityElement _newE) {
		TMLActivityElement tmlae;
		for(int i=0; i<elements.size(); i++) {
			tmlae = (TMLActivityElement)(elements.elementAt(i));
			tmlae.setNewNext(_oldE, _newE);	
		}
	}
	
	private TMLRandomSequence findTMLRandomSequence() {
		TMLActivityElement tmlae;
		for(int i=0; i<elements.size(); i++) {
			tmlae = (TMLActivityElement)(elements.elementAt(i));
			if (tmlae instanceof TMLRandomSequence) {
				return (TMLRandomSequence)tmlae;
			}
		}
		
		return null;
	}
	
	public void removeAllRandomSequences(TMLTask _task) {
		int idRandomSequence = 0;
		TMLRandomSequence tmlrs = findTMLRandomSequence();
		
		while(tmlrs != null) {
			replaceRandomSequence(_task, tmlrs, idRandomSequence);
			idRandomSequence ++;
			tmlrs = findTMLRandomSequence();
		}
	}
	
	private void replaceRandomSequence(TMLTask _task, TMLRandomSequence _tmlrs, int _idRandomSequence) {
		int nnext = _tmlrs.getNbNext();
		int i;
		
		if (nnext == 0) {
			TMLStopState adstop = new TMLStopState("stop", _tmlrs.getReferenceObject());
			addElement(adstop);
			removeElement(_tmlrs);
			replaceAllNext(_tmlrs, adstop);
			return;
		} 
		
		// At least one next!
		if (nnext == 1) {
			TMLActivityElement tmlae = _tmlrs.getNextElement(0);
			removeElement(_tmlrs);
			replaceAllNext(_tmlrs, tmlae);
			return;
		}
		
		// At least two nexts -> use of a loop combined with a choice
		String name;
		TMLChoice choice = new TMLChoice("choice for random sequence", _tmlrs.getReferenceObject());
		elements.addElement(choice);
		
		TMLForLoop loop = new TMLForLoop("loop for random sequence", _tmlrs.getReferenceObject());
		elements.addElement(loop);
		name = "looprd__" + _idRandomSequence;
		TMLAttribute loopAttribute = new TMLAttribute(name, new TMLType(TMLType.NATURAL));
		_task.addAttribute(loopAttribute);
		loop.setInit(name + "=0");
		loop.setCondition(name + " < " + nnext);
		loop.setIncrement(name + " = " + name + " + 1");
		
		TMLStopState tmlstop = new TMLStopState("stop", _tmlrs.getReferenceObject());
		addElement(tmlstop);
		
		TMLActionState [] tmlactions = new TMLActionState[nnext];
		TMLActionState tmlaction;
		TMLAttribute[] attributes = new TMLAttribute[nnext];
		
		
		for(i=0; i<nnext; i++) {
			name = "rd__" + _idRandomSequence + "__" + i;
			attributes[i] = new TMLAttribute(name, new TMLType(TMLType.BOOLEAN));
			_task.addAttribute(attributes[i]);
			
			tmlactions[i] = new TMLActionState("Setting random sequence", _tmlrs.getReferenceObject());
			elements.add(tmlactions[i]);
			tmlactions[i].setAction(name + " = false");
			
			tmlaction = new TMLActionState("Setting random sequence", _tmlrs.getReferenceObject());
			elements.add(tmlaction);
			tmlaction.setAction(name + " = true");
			tmlaction.addNext(_tmlrs.getNextElement(i));
			
			choice.addNext(tmlaction);
			choice.addGuard("[not(" + name + ")]");
			
			if (i!=0) {
				tmlactions[i-1].addNext(tmlactions[i]);
			}
		}
		
		replaceAllNext(_tmlrs, tmlactions[0]);
		tmlactions[nnext-1].addNext(loop);
		loop.addNext(choice);
		loop.addNext(tmlstop);
		removeElement(_tmlrs);
	}
    
 
}
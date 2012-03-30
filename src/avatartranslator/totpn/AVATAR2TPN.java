/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class AVATAR2TPN
* Creation: 08/02/2012
* @version 1.1 08/02/2012
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.totpn;

import java.awt.*;
import java.util.*;

import tpndescription.*;
import myutil.*;
import avatartranslator.*;

public class AVATAR2TPN {
	
	private static int GENERAL_ID = 0;  


	
	private TPN tpn;
	private AvatarSpecification avspec;
	
	private Hashtable<AvatarStateMachineElement, Place> entryPlaces;
	private Hashtable<AvatarStateMachineElement, Place> exitPlaces;
	private LinkedList<AvatarActionOnSignal> sendActions;
	private LinkedList<AvatarActionOnSignal> receiveActions;
	
	
	private Vector warnings;
	
	

	public AVATAR2TPN(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public TPN generateTPN(boolean _debug, boolean _optimize) {
		GENERAL_ID = 0;
		
		entryPlaces = new Hashtable<AvatarStateMachineElement, Place>();
		exitPlaces = new Hashtable<AvatarStateMachineElement, Place>();
		
		sendActions = new LinkedList<AvatarActionOnSignal>();
		receiveActions = new LinkedList<AvatarActionOnSignal>();
		
		warnings = new Vector();
		tpn = new TPN();
		
		avspec.removeCompositeStates();
		avspec.removeTimers();
		
		makeBlocks();
		
		//TraceManager.addDev("->   tpn:" + tpn.toString());
		
		
		/*if (_optimize) {
			spec.optimize();
		}*/
		
		
		return tpn;
	}
	
	public void makeBlocks() {
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			makeBlock(block);
		}
	}
	
	public void makeBlock(AvatarBlock ab) {
		AvatarStateMachine asm = ab.getStateMachine();
		AvatarStartState ass = asm.getStartState();
		
		makeBlockTPN(ab, asm, ass,null);
		
		interconnectSynchro();
	}
	
	public void makeBlockTPN(AvatarBlock _block, AvatarStateMachine _asm, AvatarStateMachineElement _asme, AvatarStateMachineElement _previous) {
		
		Place p0, p1, pentry=null, pexit=null;
		Transition t0;
		AvatarActionOnSignal aaos;
		
		p0 = entryPlaces.get(_asme);
		if (_previous== null) {
			p1 = null;
		} else {
			p1 = exitPlaces.get(_previous);
		}
		
		boolean link=false;
		
		// Element already taken into account?
		if (p0 != null) {
			// Link the exit place of the previous element to the one of the current element
			if (p1 != null){
				t0 = new Transition(getTPNName(_block, _previous) + " to " + getTPNName(_block, _asme));
				t0.addDestinationPlace(p0);
				t0.addOriginPlace(p1);
				tpn.addTransition(t0);
			}
			return;
		}
		
		// New element!
		
		//Start state
		if ((_asme instanceof AvatarStartState)|| (_asme instanceof AvatarStopState) || (_asme instanceof AvatarState)) {
			pentry = new Place(getTPNName(_block, _asme));
			pexit = pentry;
			entryPlaces.put(_asme, pentry);    
			exitPlaces.put(_asme, pexit);
			//TraceManager.addDev("Adding place : " + pentry);
			tpn.addPlace(pentry);
			link = true;
			
		} else if  ((_asme instanceof AvatarTransition) || (_asme instanceof AvatarRandom)) {
			if (p1 != null){
				entryPlaces.put(_asme, p1);    
				exitPlaces.put(_asme, p1);
			} else {
				TraceManager.addDev("Previous element without pexit!!");
			}
			
		} else if (_asme instanceof AvatarActionOnSignal){
			aaos = (AvatarActionOnSignal)_asme;
			
			if (aaos.getSignal().isOut()) {
				sendActions.add(aaos);
			} else {
				receiveActions.add(aaos);
			}
			
			pentry = p1;
			pexit = new Place(getTPNName(_block, _asme));
			entryPlaces.put(_asme, pentry);    
			exitPlaces.put(_asme, pexit);
			
			tpn.addPlace(pexit);
			//TraceManager.addDev("Adding place : " + pentry);
			
		} else {
			TraceManager.addDev("UNMANAGED ELEMENTS: " +_asme);
		}
		
		
		// Must link the new element to the previous one
		if ((p1 != null) && (link)){
			t0 = new Transition(getTPNName(_block, _previous) + " to " + getTPNName(_block, _asme));
			t0.addDestinationPlace(pentry);
			t0.addOriginPlace(p1);
			tpn.addTransition(t0);
		}
		
		// Work with next elements
		for(int i=0; i<_asme.nbOfNexts(); i++) {
			makeBlockTPN(_block, _asm, _asme.getNext(i), _asme);
		}
		
		
		
	}
	
	public void interconnectSynchro() {
		int index;
		AvatarSignal sig;
		Transition t0, t1;
		Place pSynchro;
		
		//TraceManager.addDev("Interconnecting synchro");
		
		// Interconnect sender and receivers together!
		for(AvatarActionOnSignal destination: receiveActions) {
			// Find the related relation
			for(AvatarRelation ar: avspec.getRelations()) {
				if (ar.containsSignal(destination.getSignal()) && !ar.isAsynchronous()) {
					index = ar.getIndexOfSignal(destination.getSignal());
					sig = ar.getOutSignal(index);
					for(AvatarActionOnSignal origin:sendActions) {
						if (origin.getSignal() == sig) {
							// combination found!
							//TraceManager.addDev("Combination found");
							t0 = new Transition("beginning Synchro from " + getTPNName(ar.getOutBlock(index), origin) + " to " + getTPNName(ar.getInBlock(index), destination));
							pSynchro = new Place("Synchro from " + getTPNName(ar.getOutBlock(index), origin) + " to " + getTPNName(ar.getInBlock(index), destination));
							tpn.addPlace(pSynchro);
							t1 = new Transition("end Synchro from " + getTPNName(ar.getOutBlock(index), origin) + " to " + getTPNName(ar.getInBlock(index), destination));
							
							t0.addOriginPlace(entryPlaces.get(destination));
							t0.addOriginPlace(entryPlaces.get(origin));
							t0.addDestinationPlace(pSynchro);
							
							t1.addOriginPlace(pSynchro);
							t1.addDestinationPlace(exitPlaces.get(origin));
							t1.addDestinationPlace(exitPlaces.get(destination));
							
							tpn.addTransition(t0);
							tpn.addTransition(t1);
							
						}
					}
				}
			}
		}
		
	}
	/* Old version
	public void interconnectSynchro() {
		int index;
		AvatarSignal sig;
		Transition t0;
		
		//TraceManager.addDev("Interconnecting synchro");
		
		// Interconnect sender and receivers together!
		for(AvatarActionOnSignal destination: receiveActions) {
			// Find the related relation
			for(AvatarRelation ar: avspec.getRelations()) {
				if (ar.containsSignal(destination.getSignal()) && !ar.isAsynchronous()) {
					index = ar.getIndexOfSignal(destination.getSignal());
					sig = ar.getOutSignal(index);
					for(AvatarActionOnSignal origin:sendActions) {
						if (origin.getSignal() == sig) {
							// combination found!
							//TraceManager.addDev("Combination found");
							t0 = new Transition("Synchro from " + getShortTPNName(origin) + " to " + getShortTPNName(destination));
							t0.addOriginPlace(entryPlaces.get(origin));
							t0.addDestinationPlace(exitPlaces.get(origin));
							t0.addOriginPlace(entryPlaces.get(destination));
							t0.addDestinationPlace(exitPlaces.get(destination));
							tpn.addTransition(t0);
						}
					}
				}
			}
		}
		
	}*/
	
	public String getTPNName(AvatarBlock _block, AvatarStateMachineElement _asme) {
		return _block.getName() + "__" + _asme.getName() + "__" + _asme.getID();
	}
	
	public String getShortTPNName(AvatarStateMachineElement _asme) {
		return _asme.getName() + "__" + _asme.getID();
	}
	
	
	
}
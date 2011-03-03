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
 * Class AvatarBroadcast
 * List all signals that are related to the broadcast channel
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.*;

import myutil.*;

public class AvatarBroadcast extends AvatarElement {
    
    
    //public AvatarBlock block1, block2;
	
	// This two lists contain send and receive signals
	// Signals with no correspondance are sent to the environment
	private LinkedList<AvatarSignal> sendSignals, receiveSignals;
	private boolean [] hasSendCorrespondance; // For send signals only
  
	
    public AvatarBroadcast(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
		sendSignals = new LinkedList<AvatarSignal>();
		receiveSignals = new LinkedList<AvatarSignal>();
    }
	
	public boolean containsSignal(AvatarSignal _as) {
		if (_as.isOut()) {
			return sendSignals.contains(_as);
		}
		return receiveSignals.contains(_as);
	}
	
	
	public void addSignal(AvatarSignal _sig)   {
		if (_sig.isOut()) {
			sendSignals.add(_sig);
		} else {
			receiveSignals.add(_sig);
		}
		computeCorrespondance();
	}
	
	public int nbOfSendSignals() {
		return sendSignals.size();
	}
	
	public AvatarSignal getSendSignal(int _index) {
		return sendSignals.get(_index);
	}
	
	public int nbOfReceiveSignals() {
		return receiveSignals.size();
	}
	
	public AvatarSignal getReceiveSignal(int _index) {
		return receiveSignals.get(_index);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("Send signals: ");
		for(int i=0; i<sendSignals.size(); i++) {
			if (i>0) {
				sb.append(" ; ");
			}
			sb.append(sendSignals.get(i).getName()); 
		}
		sb.append("  ;  receive signals: ");
		for(int j=0; j<receiveSignals.size(); j++) {
			if (j>0) {
				sb.append(" ; ");
			}
			sb.append(receiveSignals.get(j).getName()); 
		}
		return sb.toString();
	}
	
	// Return index of signal. If not found, return -1
	public boolean hasSignal(AvatarSignal _sig) {
		if (_sig.isOut()) {
			return sendSignals.contains(_sig);
		}
		return receiveSignals.contains(_sig);
	}
	
	public int getIndexOfSignal(AvatarSignal _sig) {
		if (_sig.isOut()) {
			return sendSignals.indexOf(_sig);
		}
		return receiveSignals.indexOf(_sig);
	}
	
	private void computeCorrespondance() {
		hasSendCorrespondance = new boolean[sendSignals.size()];
		
		int cpt = 0;
		for(AvatarSignal ss: sendSignals) {
			computeCorrespondance(ss, cpt);
			cpt ++;
		}
	}
	
	private void computeCorrespondance(AvatarSignal _ss, int _index) {
		for(AvatarSignal rs: receiveSignals) {
			if (rs.isCompatibleWith(_ss)) {
				hasSendCorrespondance[_index] = true;
				return;
			}
		}
		hasSendCorrespondance[_index] = false;
	}
	

}
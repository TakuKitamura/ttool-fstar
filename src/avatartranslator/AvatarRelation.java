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
 * Class AvatarRelation
 * synchronizatio in Avatar ...
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.*;

import myutil.*;

public class AvatarRelation extends AvatarElement {
    
    
    public AvatarBlock block1, block2;
	private LinkedList<AvatarSignal> signals1, signals2;
	private boolean blocking, asynchronous, isPrivate, isBroadcast, isLossy;
	private int sizeOfFIFO; // -1 means infinite
  
	
    public AvatarRelation(String _name, AvatarBlock _block1, AvatarBlock _block2, Object _referenceObject) {
        super(_name, _referenceObject);
		signals1 = new LinkedList<AvatarSignal>();
		signals2 = new LinkedList<AvatarSignal>();
		block1 = _block1;
		block2 = _block2;
		blocking = false;
		sizeOfFIFO = 1024;
		asynchronous = false;
		isBroadcast = false;
		
    }
	
	public boolean containsSignal(AvatarSignal _as) {
		return (signals1.contains(_as) || signals2.contains(_as));
	}
	
	public void setAsynchronous(boolean _b) {
		asynchronous = _b;
	}
	
	public void setBlocking(boolean _b) {
		blocking = _b;
	}
	
	public void setPrivate(boolean _b) {
		isPrivate = _b;
	}
	
	public void setBroadcast(boolean _b) {
		isBroadcast = _b;
	}
	
	public void setLossy(boolean _b) {
		isLossy = _b;
	}
	
	public void setSizeOfFIFO(int _sizeOfFIFO) {
		sizeOfFIFO = _sizeOfFIFO;
	}
	
	public boolean isAsynchronous() {
		return asynchronous;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean isBroadcast() {
		return isBroadcast;
	}
	
	public boolean isLossy() {
		return isLossy;
	}
	
	public int getSizeOfFIFO() {
		return sizeOfFIFO;
	}
	
	public boolean isBlocking() {
		return blocking;
	}
	

	
	public void addSignals(AvatarSignal _sig1, AvatarSignal _sig2)   {
		signals1.add(_sig1);
		signals2.add(_sig2);
	}
	
	public int nbOfSignals() {
		return signals1.size();
	}
	
	public AvatarSignal getSignal1(int _index) {
		return signals1.get(_index);
	}
	
	public AvatarSignal getSignal2(int _index) {
		return signals2.get(_index);
	}
	
	public AvatarSignal getInSignal(int _index) {
		AvatarSignal sig1 = signals1.get(_index);
		if (sig1.isIn()) {
			return sig1;
		}
		
		return getSignal2(_index);
	}
	
	public AvatarBlock getInBlock(int _index) {
		AvatarSignal sig1 = signals1.get(_index);
		if (sig1.isIn()) {
			return block1;
		}
		
		return block2;
	}
	
	public AvatarSignal getOutSignal(int _index) {
		AvatarSignal sig1 = signals1.get(_index);
		if (sig1.isOut()) {
			return sig1;
		}
		
		return getSignal2(_index);
	}
	
	public AvatarBlock getOutBlock(int _index) {
		AvatarSignal sig1 = signals1.get(_index);
		if (sig1.isOut()) {
			return block1;
		}
		
		return block2;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<signals1.size(); i++) {
			if (i>0) {
				sb.append(" ; ");
			}
			sb.append(block1.getName() + "." + signals1.get(i).getName() + "=" + block2.getName() + "." + signals2.get(i).getName()); 
		}
		return sb.toString();
	}
	
	// Return index of signal. If not found, return -1
	public int hasSignal(AvatarSignal sig) {
		int index1 = signals1.indexOf(sig);
		int index2 = signals2.indexOf(sig);
		return Math.max(index1, index2);
	}
	
	public int getIndexOfSignal(AvatarSignal sig) {
		int index1 = signals1.indexOf(sig);
		if (index1 > -1) {
			return index1;
		}
		return signals2.indexOf(sig);
	}
	
	/*public void makeRobustness() {
		LinkedList<AvatarSignal> signals1_tmp = new LinkedList<AvatarSignal>();
		LinkedList<AvatarSignal> signals2_tmp =  new LinkedList<AvatarSignal>();
		AvatarSignal as1, as2, astmp;
		for(int i=0; i<signals1.size(); i++) {
			as1 = signals1.get(i);
			as2 = signals2.get(i);
			
			if (as1.isOut()) {
				astmp = as2;
				as2 = as1;
				as1 = astmp;
			}
			
			signals1_tmp.add(as1);
			astmp = new AvatarSignal(as1.getName() + "__in", AvatarSignal.IN, as1.getReferenceObject());
			astmp.setInOut(AvatarSignal.IN);
			signals2_tmp.add(astmp);
			
			
			astmp = new AvatarSignal(as2.getName() + "__out", AvatarSignal.OUT, as2.getReferenceObject());
			astmp.setInOut(AvatarSignal.OUT);
			signals1_tmp.add(astmp);
			signals2_tmp.add(as2);
		}
		
		signals1 = signals1_tmp;
		signals2 = signals2_tmp;
	}*/
	

}
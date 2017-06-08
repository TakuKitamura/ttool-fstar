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
 * Class SynchroSchemes
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 07/03/2005
 * @version 1.1 07/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

public class SynchroSchemes extends Vector{
    private boolean forceSynchro = false;
    public JGate jgate;
    public long minDelay = -1;
    public long maxDelay = -1;
    public long startSynchroTime = 0;
    public boolean synchroDone = false;
    public JGroupSynchro group;
    public int protocol, localPort, destPort;
    public String localHost, destHost;
    
    public SynchroSchemes() {}

    public SynchroSchemes(String s) {
	buildFromString(s);
    }

    public JGate getjMultiGates() {
	return jgate;
    }

    public void setJGate(JGate _jgate) {
	jgate = _jgate;
    }

    public long  getMinDelay() {
	return minDelay;
    }

    public long getMaxDelay() {
	return maxDelay;
    }

    public void setMinDelay(long _minDelay) {
	minDelay = _minDelay;
    }

    public void setMaxDelay(long _maxDelay) {
	maxDelay = _maxDelay;
    }
    
    public boolean isCompatibleWith(SynchroSchemes sss) {
	if (size() != sss.size()) {
	    return false;
	}
	for(int i=0; i<size(); i++) {
	    if (!(sss.synchroSchemeAt(i).isCompatibleWith(synchroSchemeAt(i)))) {
		return false;
	    }
	}
	return true;
    }
    
    public SynchroScheme synchroSchemeAt(int i) {
	return ((SynchroScheme)(elementAt(i)));
    }
    
    public void completeSynchro(SynchroSchemes sss) {
        if (!(isCompatibleWith(sss))) {
            return;
        }
        for(int i=0; i<size(); i++) {
	    synchroSchemeAt(i).completeSynchro(sss.synchroSchemeAt(i));
	}
    }   
    
    public String toString() {
        String s = "";
        for(int i=0; i<size(); i++) {
	    s += synchroSchemeAt(i).toString();
	}
        return s;
    }

     public void fillValue() {
        for(int i=0; i<size(); i++) {
	    synchroSchemeAt(i).fillValue();
	}
    }   
    
    public void forceSynchro() {
        forceSynchro = true;
    }
    
    public boolean isSynchroForced() {
        return forceSynchro;
    }

    public boolean isOnlySending() {
	for(int i=0; i<size(); i++) {
	    if (synchroSchemeAt(i).sending == false) {
		return false;
	    }
	}
	return true;
    }
     
    public boolean isOnlyReceiving() {
	for(int i=0; i<size(); i++) {
	    if (synchroSchemeAt(i).sending == true) {
		return false;
	    }
	}
	return true;
    }

    public String getStringPacket() {
	String s = "";
        for(int i=0; i<size(); i++) {
	    s += synchroSchemeAt(i).getStringPacket();
	}
        return s;
    }

    public void buildFromString(String s) {
	String tmp;
	int index1, index2;

	s = s.trim();
	//System.out.println("build s=" + s);
	char c = s.charAt(0);
	while( (c == '!') || (c == '?')) {
	    tmp = s.substring(1, s.length());
	    index1 = tmp.indexOf('!');
	    index2 =  tmp.indexOf('?');
	    if ((index1 == -1) && (index2== -1)) {
		index1 = s.length();
	    } else {
		if (index1 == -1) {
		    index1 = index2 + 1;
		} else {
		    if (index2 == -1) {
			index1 = index1 + 1;
		    } else {
			index1 = Math.min(index1, index2) + 1;
		    }
		}
	    }
	    add(new SynchroScheme(s.substring(0, index1)));
	    if (index1 == s.length()) {
		break;
	    }
	    s = s.substring(index1, s.length());
	    //System.out.println("build s=" + s);
	}
    }
}

/**Copyright or © or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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

public class SynchroScheme {
    public boolean sending;
    public int valueNat;
    public boolean valueBool;
    public int type; /* 0 : natural, 1: boolean */
    
    public SynchroScheme() {}

    public SynchroScheme(String s) {
	buildFromString(s);
    }

    public SynchroScheme(boolean _sending) {
        sending = _sending;
    }
    
    public boolean isCompatibleWith(SynchroScheme ss) {
        if ((ss.sending) && (sending)) {
            // g!1   <-> g!2
            if (ss.type == type) {
                return sameValue(ss);
            } else {
                return false;
            }
        } else if ((ss.sending) && (!sending)) {
            // g!1 <-> g?x:nat
            return (ss.type == type);
        } else if ((!ss.sending) && (sending)) {
            // g?x:nat <-> g!1
            return (ss.type == type);
        } else {
            // two receiving -> cannot synchronize
            return false;
        }
    }
    
    public boolean sameValue(SynchroScheme ss) {
        if (ss.type == 0) {
            return (ss.valueNat == valueNat);
        } else {
            return (ss.valueBool == valueBool);
        }
    }
    
    public void completeSynchro(SynchroScheme ss) {
	//System.out.println("Complete synchro");
        if ((ss.sending) && (!sending)) {
            valueNat = ss.valueNat;
            valueBool = ss.valueBool;
	    //System.out.println("Setting value nat to: " + valueNat);
        }
    }
    
    public void fillValue() {
        Random rand = new Random();
        switch(type) {
            case 0: /* natural */
                valueNat = rand.nextInt(1000);
                break;
            case 1:
                valueBool = rand.nextBoolean();
                break;
        }   
    }
    
    public String toString() {
        String s = "";
        if (sending) {
            s += "!";
            if (type ==0) {
                s+= valueNat;
            } else {
                s+= valueBool;
            }
        } else {
            s += "?";
            if (type == 0) {
                s+= "nat";
            } else {
                s+= "bool";
            }
        }
        return s;
    }

    public String getStringPacket() {
	String s = "";
	 if (sending) {
            s += "!";
            if (type ==0) {
                s+= "i" + valueNat;
            } else {
                s+= "b" + valueBool;
            }
        } else {
            s += "?";
            if (type == 0) {
                s+= "nat";
            } else {
                s+= "bool";
            }
        }
        return s;
    }

    public void buildFromString(String s) {
	System.out.println("Building SynchroScheme from: " + s);
	
	if(s.charAt(0) == '?') {
	    sending = false;
	} else {
	    sending = true;
	}

	if (s.charAt(1) == 'i') {
	    type = 0;
	    valueNat = Integer.decode(s.substring(2, s.length())).intValue();
	} else {
	    type = 1;
	    if (s.substring(2, s.length()).compareTo("true") ==0) {
		valueBool = true;
	    } else {
		valueBool = false;
	    }
	}
    }
}

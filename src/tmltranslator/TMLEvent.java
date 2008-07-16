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
 * Class TMLEvent
 * Creation: 22/11/2005
 * @version 1.0 22/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;

import java.util.*;


public class TMLEvent extends TMLCommunicationElement {
    protected Vector params; // List of various types of parameters
    protected int maxEvt = -1; // maxEvt = -1 -> infinite nb of evts: default behaviour
    protected boolean isBlocking = false; // By default, latest events is removed when the FIFO is full
    protected boolean canBeNotified = false;
    protected TMLTask origin, destination;
    
    /*public TMLEvent(String name, Object reference) {
        super(name, reference);
        params = new Vector();
    }*/
    
    public TMLEvent(String name, Object reference, int _maxEvt, boolean _isBlocking) {
        super(name, reference);
        params = new Vector();
        maxEvt = _maxEvt;
        isBlocking = _isBlocking;
        checkMaxEvt();
		
		//System.out.println("New event: " + name + " max=" + _maxEvt + " blocking=" + isBlocking);
    }
    
    public int getNbOfParams() {
      return params.size();
    }
    
    public void setSizeFIFO(int _max) {
      maxEvt = _max;
      checkMaxEvt();
    }
    
    public void setBlocking(boolean _isBlocking) {
	    isBlocking = _isBlocking;
    }
    
    public void setTasks(TMLTask _origin, TMLTask _destination) {
      origin = _origin;
      destination = _destination;
    }
    
    public TMLTask getOriginTask() {
      return origin;
    }
    
    public TMLTask getDestinationTask() {
      return destination;
    }

    public void checkMaxEvt() {
      if (maxEvt < -1) {
         maxEvt = -1;
      }
      
      if (maxEvt == 0) {
         maxEvt = -1;
      }

    }
    
    public void setNotified(boolean b) {
           canBeNotified = b;
    }
    
    public boolean canBeNotified() {
           return canBeNotified;
    }
    
    public boolean isInfinite() {
           return (maxEvt == -1);
    }
    
    public boolean isBlocking() {
	    return isBlocking;
    }
    
    public int getMaxSize() {
           return maxEvt;
    }
    
    public void addParam(TMLType _type) {
      params.add(_type);
    }
	
	public boolean isBlockingAtOrigin() {
		if (isInfinite()) {
			return false;
		}
		
		if (isBlocking()) {
			return true;
		}
		
		return false;
	}
	
	public boolean isBlockingAtDestination() {
		return true;
	}
    
    public TMLType getType(int i) {
        if (i<getNbOfParams()) {
            return (TMLType)(params.elementAt(i));
        } else {
            return null;
        }
    }
	
	public String getNameExtension() {
		return "event__";
	}
	
	public String getTypeTextFormat() {
		if (isInfinite()) {
			return "INF";
		} else {
			if (isBlocking()) {
				return "NIB";
			} else {
				return "NINB";
			}
		}
	}
	
	public static boolean isAValidListOfParams(String _list) {
		if (_list.length() == 0) {
			return true;
		}
		String []split = _list.split(","); 
		for(int i=0; i<split.length; i++) {
			if (!TMLType.isAValidType(split[i])) {
				return false;
			}
		}
		return true;
	}
	
	 public void addParam(String _list) {
		String []split = _list.split(",");
		TMLType type;
		for(int i=0; i<split.length; i++) {
			if (TMLType.isAValidType(split[i])) {
				type = new TMLType(TMLType.getType(split[i]));
				addParam(type);
			}
		}
    }
	
}
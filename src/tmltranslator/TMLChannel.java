/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TMLChannel
 * Creation: 22/11/2005
 * @version 1.0 22/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;



public class TMLChannel extends TMLCommunicationElement {
    
    public static final int BRBW = 0;
    public static final int BRNBW = 1;
    public static final int NBRNBW = 2; 
    
    private int size;
    private int type;
    private int max;
  
    protected TMLTask origin, destination;


    public TMLChannel(String name, Object reference) {
        super(name, reference);
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
    
    public void setSize(int _size) {
        size = _size;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setMax(int _max) {
        max = _max;
    }

    public int getMax() {
        return max;
    }
    
    public void setType(int _type) {
        type = _type;
    }
	
	 public void setTypeByName(String _name) {
		 if (_name.compareTo("BRBW") == 0) {
			 type = BRBW;
		 }
		 if (_name.compareTo("BRNBW") == 0) {
			 type = BRNBW;
		 }
		 if (_name.compareTo("NBRNBW") == 0) {
			 type = NBRNBW;
		 }
		 
    }

    public int getType() {
        return type;
    }
	
	public boolean isInfinite() {
		return (type != 0);
	}
	
	public String getNameExtension() {
		return "channel__";
	}
	
	public static String getStringType(int type) {
		switch(type) {
		case BRBW:
			return "BRBW";
		case BRNBW:
			return "BRNBW";
		case NBRNBW:
			return "NBRNBW";
		}
		return "unknown type";
	}
	
	public boolean isBlockingAtOrigin() {
		switch(type) {
			case BRBW:
				return true;
			case BRNBW:
				return false;
			case NBRNBW:
				return false;
			}
		return false;
	}
	
	public boolean isBlockingAtDestination() {
		switch(type) {
			case BRBW:
				return true;
			case BRNBW:
				return true;
			case NBRNBW:
				return false;
			}
		return false;
	}
	
}
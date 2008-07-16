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
 * Class Gate
 * Creation: 2003
 * @version 1.1 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

public class Gate implements Comparable {
    private String name;
    private String lotosName;
    public String tmp = null;
	public int translation; // May be used by code translators
    private int type;
    private boolean internal = false;
    
    private int protocolJava = 0;
    private int localPortJava = 0;
    private int destPortJava = 0;
    private String destHostJava = "";
    private String localHostJava = "";
    
    public static final int GATE = 0;
    public static final int OUTGATE = 1;
    public static final int INGATE = 2;
    
    public Gate(String _name, int _type, boolean _internal) {
        name = _name;
        type = _type;
        internal = _internal;
    }
    
    public String getName() {
        return name;
    }
    
    public int getType() {
        return type;
    }
    
    public String getTranslation() {
        return name;
    }
    
    public String getLotosTranslation() {
        return name;
    }
    
    public boolean isInternal() {
        return internal;
    }
    
    public String getLotosName() {
        return lotosName;
    }
    
    public void setLotosName(String _lotosName) {
        lotosName = _lotosName;
    }
    
    public String toString() {
        return name;
    }
    
    public int compareTo(Object o) {
        if (!(o instanceof Gate)) {
            return 0;
        } else {
            return toString().compareTo(o.toString());
        }
        
    }
    
    public void setProtocolJava(int proto) {
        protocolJava = proto;
    }
    
    public int getProtocolJava() {
        return protocolJava;
    }   
    
    public void setLocalPortJava(int port) {
        localPortJava = port;
    }
    
    public int getLocalPortJava() {
        return localPortJava;
    }   
    
    public void setDestPortJava(int port) {
        destPortJava = port;
    }
    
    public int getDestPortJava() {
        return destPortJava;
    }   
    
    public void setDestHostJava(String _host) {
        destHostJava = _host;
    }
    
    public String getDestHostJava() {
        return destHostJava;
    }   
    
    public void setLocalHostJava(String _host) {
        localHostJava = _host;
    }
    
    public String getLocalHostJava() {
        return localHostJava;
    }   
}

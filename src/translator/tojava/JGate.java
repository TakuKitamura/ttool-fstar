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
 * Class JGate
 * Creation: 04/03/2005
 * @version 1.1 04/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package translator.tojava;

public class JGate implements Comparable {
    private String name;
    private String jName;
    private String access;
    private int protocol;
    private int localPort;
    private int destPort;
    private String destHost;
    private String localHost;
    
    public JGate(String _name, boolean _internal, int _protocol, int _localPort, int _destPort, String _destHost, String _localHost) {
        name = _name;
        jName = name;
        if (_internal == true) {
            access = JKeyword.PRIVATE;
        } else {
            access = JKeyword.PUBLIC;
        }
        protocol = _protocol;
        localPort = _localPort;
        destPort = _destPort;
        destHost = _destHost;
        localHost = _localHost;
    }
    
    public String getName() {
        return name;
    }
    
    public void setJName(String _name) {
        jName = _name;
    }
    
    public String getJName() {
        return jName;
    }
    
    public String getAccess() {
        return access;
    }
    
    public String getJavaDeclaration() {
        return access + " " + TURTLE2Java.JGATE + " " + jName + JKeyword.END_OP;
    }
    
    public String getJavaCreation() {
        return TURTLE2Java.JGATE + " " + jName + " = new " + TURTLE2Java.JGATE + "(" + jName + ")" + JKeyword.END_OP;
    }
    
    public boolean hasAProtocol() {
        return (protocol != TURTLE2Java.NO_PROTOCOL);
    }
    
    public int getProtocol() {
        return protocol;
    }
    
    public int getLocalPort() {
        return localPort;
    }
    
     public int getDestPort() {
        return destPort;
    }
    
    public String getDestHost() {
        return destHost;
    }
    
    public String getLocalHost() {
        return localHost;
    }
    
    public int compareTo(Object o) {
        if (!(o instanceof JGate)) {
            return 0;
        } else {
            return toString().compareTo(o.toString());
        }
        
    }
}

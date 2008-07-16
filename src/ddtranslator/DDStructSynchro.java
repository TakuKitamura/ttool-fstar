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
 * Class DDStructSynchro
 * Creation: 03/06/2005
 * version 1.0 03/06/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ddtranslator;

import java.util.*;

import translator.*;

public class DDStructSynchro  {
    private LinkedList list;
    
    public DDStructSynchro() {
        list = new LinkedList();
    }
    
    public DDStructSynchro(String actionValue, TClass t) {
        list = new LinkedList();
        constructList(actionValue, t);
    }
    
    // assumes that it contains no "!" operator
    public void constructList(String actionValue, TClass t) {
        String stmp, paramName;
        Param p;
        char c = '?';
        
        int index, index1, index2, index3, index4, index5;
        String s = actionValue;
        
        if ((s == null) || (s.length() == 0))
            return;
        
        while ( (index = s.indexOf(c)) != -1) {
            stmp = s.substring(index+1, s.length());
            index1 = stmp.indexOf('!');
            index2 = stmp.indexOf('?');
            index3 = stmp.length();
            if (index1 == -1) {
                index1 = index3;
            }
            if (index2 == -1) {
                index2 = index3;
            }
            index4 = Math.min(index1, index2);
            index4 = Math.min(index4, index3);
            
            if (index4 > 0) {
                paramName = s.substring(index+1, index4+index+1);
                paramName = paramName.trim();
                index5 = paramName.indexOf(":");
                if (index5>0) {
                    paramName = paramName.substring(0, index5);
                }
                p = t.getParamByName(paramName);
                //System.out.println("Param=" + paramName);
                if (p != null) {
                    list.add(p.getType());
                } else {
                    return;
                }         
            }
            s = s.substring(index4+index+1, s.length());
        }
    }
    
    public String getRegularCall() {
        
        //System.out.println("synchro size=" + list.size());
        
        String call = "";
        int x = 0;
        int b = 0;
        String type;
        
        ListIterator iterator = list.listIterator();
        
        while(iterator.hasNext()) {
            type = (String)(iterator.next());
            if (type.compareTo(Param.NAT) ==0) {
                call += "?x" + x + ":nat";
                x ++;
            } else {
                call += "?b" + b + ":nat";
                b ++;
            }
        }
        
        return call;
    }
    
    public int nbNat() {
        int nb = 0;
        ListIterator iterator = list.listIterator();
        String type;
        
        while(iterator.hasNext()) {
            type = (String)(iterator.next());
            if (type.compareTo(Param.NAT) ==0) {
                nb ++;
            }
        }
        
        return nb;
    }
    
    public int nbBool() {
        int nb = 0;
        ListIterator iterator = list.listIterator();
        String type;
        while(iterator.hasNext()) {
            type = (String)(iterator.next());
            if (type.compareTo(Param.BOOL) ==0) {
                nb ++;
            }
        }
        
        return nb;
    }
    
    public int compareTo(Object o) {
        if (o instanceof DDStructSynchro) {
            DDStructSynchro ddss = (DDStructSynchro)o;
            LinkedList listd = ddss.getList();
            if (list.size() == listd.size()) {
                ListIterator li1 = list.listIterator();
                ListIterator li2 = listd.listIterator();
                String s1, s2;
                while(li1.hasNext()) {
                    s1 = (String)(li1.next());
                    s2 = (String)(li2.next());
                    if (s1.compareTo(s2) != 0) {
                        return -1;
                    }
                }
                return 0;
            }
            return -1;
        }
        return -1;
    }
    
    public LinkedList getList() {
        return list;
    }
    
    public int size() {
        return list.size();
    }
    
    public boolean isInList(LinkedList _list) {
        ListIterator iterator = _list.listIterator();
        DDStructSynchro ddss;
        while(iterator.hasNext()) {
            ddss = (DDStructSynchro)(iterator.next());
            if (compareTo(ddss) == 0) {
                return true;
            }
        }
        return false;
    }
    
    
    
} // Class
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
 * Class ADComponent
 * Creation: 10/12/2003
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

public abstract class ADComponent implements Cloneable {
    protected boolean selected = false;
    protected int nbNext;
    protected int minNbNext;
    protected Vector next;
    public ADComponent substitute; // For modification of AD
    protected String pre, post;
    
    public ADComponent() {
        next = new Vector();
        minNbNext = 1;
    }
    
    public abstract ADComponent makeSame();

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }   
    
    public ADComponent getNext(int index) {
        if (index < next.size()) {
            return (ADComponent)(next.elementAt(index));
        } else {
            return null;
        }
    }
    
    public int getNbNext() {
        return  next.size();
    }
    
    public int getMinNext() {
        return  minNbNext;
    }
    
    public int realNbOfNext() {
        return next.size();
    }
    
    public Vector getAllNext() {
        return next;
    }
    
    public void setNewNext(Vector newNext) {
        next = newNext;
    }
    
    public void removeNext(int i) {
		if(i<next.size()) { 
           next.removeElementAt(i);
		}
    }
	
	public void removeAllNextAfter(int index) {
		System.out.println("Removing all nexts on " + toString()); 
		for(int i=index+1; i<getNbNext(); i++){
			removeNext(i);
			i--;
		}
	}

    public void removeNext(Object o) {
        next.removeElement(o);
    }
    
    public void removeAllNext() {
        next.removeAllElements();
    }
    
    public void addNext(ADComponent adc) {
        if (next.size() < nbNext) {
            next.add(adc);
        }
    }
    
    public void addNextAtIndex(ADComponent adc, int index) {
        if (index > next.size()) {
            index = next.size();
        }
        next.insertElementAt(adc, index);
    }
    
    public void setNextAtIndex(ADComponent adc, int index) {
        if (index > next.size()) {
            index = next.size();
        }
        next.insertElementAt(adc, index);
        next.removeElementAt(index+1);
    }
    
    public boolean hasNextTo(ADComponent adc) {
        ADComponent adc1;
        for(int i=0; i<next.size(); i++) {
            adc1 = (ADComponent)(next.elementAt(i));
            if (adc == adc1) {
                return true;
            }
        }
        return false;
    }
    
    public void updateNext(ADComponent oldOne, ADComponent newOne) {
        //System.out.println("Checking nexts in " + toString());
        ADComponent adc1;
        for(int i=0; i<next.size(); i++) {
            adc1 = (ADComponent)(next.elementAt(i));
            //System.out.println("Next = " + adc1.toString());
            if (adc1 == oldOne) {
                //System.out.println("found relink in " + this);
                next.insertElementAt(newOne, i);
                next.removeElement(oldOne);
            }
        }
        return;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean b) {
        selected = b;
    }
    
    public void setPreJavaCode(String _pre) {
        pre = _pre;
    }
    
    public void setPostJavaCode(String _post) {
        post = _post;
    }
    
     public String getPreJavaCode() {
        return pre;
    }
    
    public String getPostJavaCode() {
        return post;
    }
    
    public Vector getLastBeforeStop(Vector v, ADComponent previous) {
        if (this instanceof ADStop) {
            v.add(previous);
            return v;
        } else {
            ADComponent adc1;
            for(int i=0; i<next.size(); i++) {
                adc1 = (ADComponent)(next.elementAt(i));
                if (adc1 != null) {
                    v = adc1.getLastBeforeStop(v, this);
                }
            }
            return v;
        }
    }
}
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
* Class TClassBuffer
* Creation: 18/08/2004
* @version 1.0 18/08/2004
* @author Ludovic APVRILLE
* @see 
*/

package translator;

import java.util.*;

public abstract class TClassBuffer extends TClass {
	
	public final static String OUT = "__out";
	public final static String IN = "__in";
	
	// Array storing gate names
	protected ArrayList<String> paramInForExchange;
	protected ArrayList<String> paramOutForExchange;
	protected ArrayList<String> paramSizeForExchange;
	
	protected int nbParam; // Params must be of Natural type
    
    public TClassBuffer(String name, boolean isActive) {
		super(name, isActive);
        paramInForExchange = new ArrayList<String>();
		paramOutForExchange = new ArrayList<String>();
		paramSizeForExchange = new ArrayList<String>();
    }
	
    public abstract void makeTClass();
    
    public void addParamInForExchange(String m) {
		for(String tmp:paramInForExchange) {
			if (tmp.compareTo(m) == 0) {
                return;
            }
		}
        paramInForExchange.add(m);
    }
	
	public void addParamOutForExchange(String m) {
		for(String tmp:paramOutForExchange) {
			if (tmp.compareTo(m) == 0) {
                return;
            }
		}
        paramOutForExchange.add(m);
    }
	
	public void addParamSizeForExchange(String m) {
		for(String tmp:paramSizeForExchange) {
			if (tmp.compareTo(m) == 0) {
                return;
            }
		}
        paramOutForExchange.add(m);
    }
    
    public String getParamInAt(int index) {
        return paramInForExchange.get(index);
    }
	
	public String getParamOutAt(int index) {
        return paramOutForExchange.get(index);
    }
	
	public String getParamSizeAt(int index) {
        return paramSizeForExchange.get(index);
    }
    
    public int getParamInNb() {
        return paramInForExchange.size();
    }
	
	public int getParamOutNb() {
        return paramOutForExchange.size();
    }
	
	public int getParamSizeNb() {
        return paramSizeForExchange.size();
    }
	
	public int getNbParam() {
		return nbParam;
	}
	
	public void setNbParam(int _nbParam) {
		nbParam = _nbParam;
	}
}  
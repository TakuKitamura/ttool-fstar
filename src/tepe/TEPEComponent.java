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
* Class TEPEComponent
* Creation: 15/02/2011
* @version 1.0 15/02/2011
* @author Ludovic APVRILLE
* @see
*/

package tepe;

import java.util.*;

import myutil.*;

public abstract class TEPEComponent  {
	private static int ID = 0;
	
    protected String name;
	protected Object referenceObject;
	private int id;
	
	protected String value;
	
	protected Vector <TEPEComponent> inAttributeComponents;
	protected Vector <TEPEComponent> outAttributeComponents;
	
	protected Vector <TEPEComponent> inSignalComponents;
	protected Vector <TEPEComponent> inNegatedSignalComponents;
	protected Vector <TEPEComponent> outSignalComponents;
	
	protected Vector <TEPEComponent> inPropertyComponents;
	protected Vector <TEPEComponent> inNegatedPropertyComponents;
	protected Vector <TEPEComponent> outPropertyComponents;
    
    public TEPEComponent(String _name, Object _referenceObject) {
		name = _name;
		referenceObject = _referenceObject;
		id = ID;
		ID ++;
    }
    
	
	public String getName() {
		return name;
	}
	
	public void setValue(String _value) {
		value = _value;
	}
	
	public String getValue() {
		return value;
	}
	
	public Object getReferenceObject() {
		return referenceObject;
	}
	
	public String getExtraString() {
		return "";
	}
	
	public String toString() {
		String ret = "Component: " + name + " id: " + id + " value: " + value;
		ret += getExtraString();
		if (hasInAttributeComponents()) {
			ret += "\n    in Attributes:";
			for(TEPEComponent comp: inAttributeComponents) {
				ret += " " + comp.getName() + "/ID=" + id;
			}
		}
		if (hasOutAttributeComponents()) {
			ret += "\n    out Attributes:";
			for(TEPEComponent comp: outAttributeComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasInSignalComponents()) {
			ret += "\n    in Signals:";
			for(TEPEComponent comp: inSignalComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasInNegatedSignalComponents()) {
			ret += "\n    in negated Signals:";
			for(TEPEComponent comp: inNegatedSignalComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasOutSignalComponents()) {
			ret += "\n    out Signals:";
			for(TEPEComponent comp: outSignalComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasInPropertyComponents()) {
			ret += "\n    in Properties:";
			for(TEPEComponent comp: inPropertyComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasInNegatedPropertyComponents()) {
			ret += "\n    in negated Properties:";
			for(TEPEComponent comp: inNegatedPropertyComponents) {
				ret += " " + comp.getName();
			}
		}
		if (hasOutPropertyComponents()) {
			ret += "\n    out Properties:";
			for(TEPEComponent comp: outPropertyComponents) {
				ret += " " + comp.getName();
			}
		}
		
		return ret;
	}
	
	public static void reinitID() {
		ID = 0;
	}
	
	public boolean hasInAttributeComponents() {
		return ((inAttributeComponents != null) && (inAttributeComponents.size()>0));
	}
	
	public boolean hasOutAttributeComponents() {
		return ((outAttributeComponents != null) && (outAttributeComponents.size()>0));
	}
	
	public boolean hasInSignalComponents() {
		return ((inSignalComponents != null) && (inSignalComponents.size()>0));
	}
	
	public boolean hasInNegatedSignalComponents() {
		return ((inNegatedSignalComponents != null) && (inNegatedSignalComponents.size()>0));
	}
	
	public boolean hasOutSignalComponents() {
		return ((outSignalComponents != null) && (outSignalComponents.size()>0));
	}
	
	public boolean hasInPropertyComponents() {
		return ((inPropertyComponents != null) && (inPropertyComponents.size()>0));
	}
	
	public boolean hasInNegatedPropertyComponents() {
		return ((inNegatedPropertyComponents != null) && (inNegatedPropertyComponents.size()>0));
	}
	
	public boolean hasOutPropertyComponents() {
		return ((outPropertyComponents != null) && (outPropertyComponents.size()>0));
	}
	
	public void addInAttributeComponent(TEPEComponent _tepec) {
		if (inAttributeComponents != null) {
			inAttributeComponents.add(_tepec);
		}
	}
	
	public void addOutAttributeComponent(TEPEComponent _tepec) {
		if (outAttributeComponents != null) {
			outAttributeComponents.add(_tepec);
		}
	}
	
    
}

/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package tmltranslator;

import java.util.Vector;


/**
   * Class TMLActivityElement
   * Creation: 23/11/2005
   * @version 1.0 23/11/2005
   * @author Ludovic APVRILLE
 */
public abstract class TMLActivityElement extends TMLElement{
    protected Vector<TMLActivityElement> nexts;
    public SecurityPattern securityPattern;
    private String value="";
    
    public TMLActivityElement(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        
        nexts = new Vector<TMLActivityElement>();
    }

    public int getNbNext() {
        return nexts.size();
    }
    
    public void setValue(String val){
    	value=val;
    }
    
    public String getValue(){
    	return value;
    }
    
    public TMLActivityElement getNextElement(int _i) {
        if (_i < getNbNext() ) {
            return nexts.elementAt(_i);
        } else {
            return null;
        }
    }

    public void addNext(TMLActivityElement _tmlae) {
        nexts.add(_tmlae);
    }

    public void addNext(int _index, TMLActivityElement _tmlae) {
        nexts.add(_index, _tmlae);
    }

    public void removeNext(int index) {
        nexts.removeElementAt(index);
    }

    public Vector<TMLActivityElement> getNexts() {
        return nexts;
    }

    public void setNexts(Vector<TMLActivityElement> _nexts) {
        nexts = _nexts;
    }

    public void clearNexts() {
        nexts.clear();
    }

    public void setNewNext(TMLActivityElement oldE, TMLActivityElement newE) {
        TMLActivityElement elt;
        for(int i=0; i<getNbNext(); i++) {
            elt = getNextElement(i);
            if (elt == oldE) {
                nexts.setElementAt(newE, i);
            }
        }
    }

    public String toXML(Vector<TMLActivityElement> elements) {
	String s = "<ACTIVITYELEMENT type=\"" + getClass().getName() + "\" value=\"" + value + "\" id=\"" + elements.indexOf(this) + "\" name=\"" + name + "\">\n";
	if (securityPattern != null) {
	    s += securityPattern.toXML();
	}
	s += extraToXML();
	for(TMLActivityElement tmlae: nexts) {
	    s += "<NEXTACTIVITYELEMENT id=\"" + elements.indexOf(tmlae) + "\" />\n";
	}
	s += "</ACTIVITYELEMENT>\n";
	return s;
    }


    public String extraToXML() {
	String s = "<CUSTOM " + customExtraToXML() + " />\n";
	return s;
    }
    
    public abstract String customExtraToXML();
}

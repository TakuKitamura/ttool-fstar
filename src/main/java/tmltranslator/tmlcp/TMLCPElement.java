/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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

package tmltranslator.tmlcp;

import tmltranslator.TMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class TMLCPElement Creation: 18/02/2014
 * 
 * @version 1.0 22/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public abstract class TMLCPElement extends TMLElement {
    protected List<TMLCPElement> nexts;

    public TMLCPElement(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        nexts = new ArrayList<TMLCPElement>();
    }

    public String getShortName() {
        int index = name.indexOf("_#");
        if (index == 0) {
            return getName();
        }

        return name.substring(0, index);

    }

    public void addNextElement(TMLCPElement _elt) {
        nexts.add(_elt);
    }

    public void setNextElement(TMLCPElement _elt) {
        nexts.clear();
        addNextElement(_elt);
    }

    public void setNextElementAtIndex(TMLCPElement _elt, int _index) {
        nexts.set(_index, _elt);
    }

    public void clearNexts() {
        nexts.clear();
    }

    public List<TMLCPElement> getNextElements() {
        return nexts;
    }

    public void setNexts(List<TMLCPElement> _nexts) {
        nexts = _nexts;
    }

    public void replaceNext(TMLCPElement oldOne, TMLCPElement newOne) {
        int index;
        while ((index = nexts.indexOf(oldOne)) != -1) {
            nexts.set(index, newOne);
        }

    }

    public String toShortString() {
        return getClass().getCanonicalName() + " " + getName() + "\n";
    }

    public String toString() {
        String s = "\t+" + toShortString();
        for (TMLCPElement elt : nexts) {
            s += "\t\t->" + elt.toShortString();
        }
        return s;
    }

} // End of class

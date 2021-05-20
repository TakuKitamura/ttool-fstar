/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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

import tmltranslator.TMLCP;
import tmltranslator.TMLElement;

import java.util.*;

/**
 * Class TMLCPActivityDiagram Creation: 18/02/2014
 * 
 * @version 1.0 21/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLCPActivityDiagram extends TMLElement {

    private TMLCPStart start;
    private List<TMLCPElement> elements; // Including the start element
    // private ArrayList<TMLAttribute> globalVariables;

    private List<String> ads; // a list of the activity diagrams declared in a section (for parsing of text)
    private List<String> sds; // a list of the sequence diagrams declated in a section (for parsing of text)

    // private int hashCode;
    // private boolean hashCodeComputed = false;

    /*
     * private boolean definedVariable( TMLAttribute _var ) {
     * 
     * TMLAttribute var; int i;
     * 
     * for(i = 0; i < globalVariables.size(); i++ ) { var = globalVariables.get(i);
     * //Attention, control is done when variables have not been initialized yet: do
     * not use TMLAttribute.equals! String tempName = var.getName(); TMLType
     * tempType = var.getType(); if( tempName.equals( _var.getName()) &&
     * tempType.equals( _var.getType() ) ) { return true; } } return false; }
     */

    private void init() {
        // globalVariables = new ArrayList<TMLAttribute>();
        elements = new ArrayList<TMLCPElement>();
        ads = new ArrayList<String>();
        sds = new ArrayList<String>();
    }

    public TMLCPActivityDiagram(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        init();
    }

    /*
     * public void addVariable( TMLAttribute _var ) { globalVariables.add( _var ); }
     */

    public void addADname(String _name) {
        ads.add(_name);
    }

    public void addSDname(String _name) {
        sds.add(_name);
    }

    public int getSize() {
        return elements.size();
    }

    public TMLCPElement getElementByReference(Object referenceObject) {
        for (TMLCPElement elt : elements) {
            if (elt.getReferenceObject() == referenceObject) {
                return elt;
            }
        }
        return null;
    }

    /*
     * public boolean checkVariableNoType( TMLAttribute _attr ) {
     * 
     * int i = 0; String str; TMLAttribute tempAttr; TMLType tempType, _attrType;
     * 
     * for( i = 0; i < globalVariables.size(); i++ ) { tempAttr =
     * globalVariables.get(i); str = tempAttr.getName(); if(
     * str.equals(_attr.getName()) ) { tempType = tempAttr.getType(); _attrType =
     * _attr.getType(); if( tempType.getType() == _attrType.getType() ) { return
     * true; } } } return false; }
     */

    public boolean declaredDiagram(String _name) {
        if (containsADDiagram(_name)) {
            return true;
        } else {
            if (containsSDDiagram(_name)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSDDiagram(String _name) {
        return sds.contains(_name);
    }

    public boolean containsADDiagram(String _name) {
        return ads.contains(_name);
    }

    /*
     * public void insertInitialValue( TMLAttribute _attr, String value ) {
     * 
     * int i = 0; String str; TMLAttribute tempAttr; TMLType tempType;
     * 
     * for( i = 0; i < globalVariables.size(); i++ ) { tempAttr =
     * globalVariables.get(i); str = tempAttr.getName(); if( str.equals(
     * _attr.getName() ) ) { tempType = tempAttr.getType(); if( tempType.equals(
     * _attr.getType() ) ) { _attr.initialValue = value; globalVariables.set( i,
     * _attr ); return; } } } //The variable trying to be initialized was not
     * declared }
     */

    public List<TMLCPElement> getElements() {
        return elements;
    }

    public void addElements(List<TMLCPElement> _nexts) {
        elements = new ArrayList<TMLCPElement>(_nexts);
    }

    /*
     * public void addElement( TMLCPElement _elem ) { elements.add( _elem ); }
     */

    public boolean contains(TMLCPElement elt) {
        return elements.contains(elt);
    }

    public TMLCPElement getElementByName(String name) {
        for (TMLCPElement elt : elements) {
            if (elt.getName().equals(name)) {
                return elt;
            }
        }
        return null;
    }

    /*
     * public ArrayList<TMLAttribute> getAttributes() { return globalVariables; }
     */

    public List<String> getADlist() {
        return ads;
    }

    public List<String> getSDlist() {
        return sds;
    }

    public void setStartElement(TMLCPStart _elt) {
        start = _elt;
    }

    public TMLCPElement getStartElement() {
        return start;
    }

    public void addTMLCPElement(TMLCPElement _elt) {
        elements.add(_elt);
    }

    /*
     * public boolean definedBoolVariable( String _name ) {
     * 
     * TMLAttribute var; int i;
     * 
     * for(i = 0; i < globalVariables.size(); i++ ) { var = globalVariables.get(i);
     * if( var.isBool() ) { if( _name.equals( var.getName() ) ) { return true; } } }
     * return false; }
     */

    public void correctReferences(TMLCP _refTopCP) {

        String tempString;

        List<TMLCPActivityDiagram> activityList = _refTopCP.getCPActivityDiagrams();
        List<TMLCPSequenceDiagram> sequenceList = _refTopCP.getCPSequenceDiagrams();

        for (TMLCPElement tempElem : elements) {

            if (tempElem instanceof TMLCPRefAD) {
                tempString = tempElem.getShortName();
                for (TMLCPActivityDiagram tempCP : activityList) {
                    if (tempString.equals(tempCP.getName())) {
                        ((TMLCPRefAD) tempElem).setReference(tempCP);

                        /*
                         * TMLCPRefAD CPRef = new TMLCPRefAD( tempCP, tempElem.getName(), tempCP );
                         * elements.set( i, CPRef );
                         */
                        break; // We must ensure that AD names are unique
                    }
                }
            } else if (tempElem instanceof TMLCPRefSD) { // A reference to a sequence diagram must be inserted instead
                tempString = tempElem.getName();
                for (TMLCPSequenceDiagram tempSD : sequenceList) {
                    if (tempString.equals(tempSD.getName())) {
                        ((TMLCPRefSD) tempElem).setReference(tempSD);
                        /*
                         * TMLCPRefSD SDRef = new TMLCPRefSD( tempSD, tempElem.getName(), tempSD );
                         * elements.set( i, SDRef );
                         */
                        break; // We must ensure that SD names are unique
                    }
                }
            }
        }
    }

    public void generateNexts() {
        String startName, endName;
        TMLCPElement src, dest;
        for (TMLCPElement tempElem : elements) {
            if (tempElem instanceof TMLCPConnector) {
                startName = ((TMLCPConnector) tempElem).getStartName();
                endName = ((TMLCPConnector) tempElem).getEndName();
                src = getElementByName(startName);
                dest = getElementByName(endName);

                if ((src != null) && (dest != null)) {
                    src.addNextElement(dest);
                    if (src instanceof TMLCPChoice) {
                        ((TMLCPChoice) src).addGuard(((TMLCPConnector) tempElem).getGuard());
                    }
                }

            }
        }
    }

    public void removeADConnectors() {
        LinkedList<TMLCPElement> toBeRemoved = new LinkedList<TMLCPElement>();
        for (TMLCPElement tempElem : elements) {
            if (tempElem instanceof TMLCPConnector) {
                toBeRemoved.add(tempElem);
            }
        }
        for (TMLCPElement tempElem : toBeRemoved) {
            elements.remove(tempElem);
        }
    }

    // Issue #69; Unused TMLCPJunction
    // The splitting works only if there is no other operations than sequences and
    // references to ADs/SDs
    // between forks and joins
    // The function removes junctions, and creates one new AD per junction
    // private Collection<TMLCPActivityDiagram> splitADs() {
    // int id = 0;
    // TMLCPActivityDiagram diag;
    //
    // //TraceManager.addDev("Splitting AD: " + getName());
    //
    // // For each junction, we create a new AD
    //// List<TMLCPJunction> junctions = new ArrayList<TMLCPJunction>();
    // List<TMLCPRefAD> refsAD = new ArrayList<TMLCPRefAD>();
    // List<TMLCPElement> toBeRemoved = new ArrayList<TMLCPElement>();
    //// Map<TMLCPJunction, TMLCPActivityDiagram> refs = new HashMap<TMLCPJunction,
    // TMLCPActivityDiagram>();
    // for(TMLCPElement elt: elements) {
    // if (elt instanceof TMLCPJunction) {
    // junctions.add((TMLCPJunction)elt);
    // diag = new TMLCPActivityDiagram(elt.getName() + "_" + id, referenceObject);
    // TMLCPStart start = new TMLCPStart("StartFrom_" + elt.getName(),
    // elt.getReferenceObject());
    // diag.setStartElement(start);
    // diag.addTMLCPElement(start);
    // refs.put((TMLCPJunction)elt, diag);
    // //TraceManager.addDev("Adding a new diag named: " + diag.getName());
    // }
    // }
    //
    // // We replace all elements leading to a junction by a call to the
    // corresponding ref AD, keeping the same nexts
    // for(TMLCPJunction junction: junctions) {
    // TMLCPActivityDiagram toAD = refs.get(junction);
    // //TMLCPRefAD ref = new TMLCPRefAD(toAD, toAD.getName(),
    // junction.getReferenceObject());
    // //refsAD.add(ref);
    // elements.remove(junction);
    // //elements.add(ref);
    //
    // // Replacing next to junction by new refs
    // int index;
    // // int ID = 0;
    // for(TMLCPElement elt: elements) {
    // while(((index = elt.getNextElements().indexOf(junction)) != -1)) {
    // //TMLCPRefAD ref = new TMLCPRefAD(toAD, toAD.getName() + "_" + ID,
    // junction.getReferenceObject());
    // //ID ++;
    // TMLCPRefAD ref = new TMLCPRefAD(toAD, toAD.getName(),
    // junction.getReferenceObject());
    // //ID ++;
    // refsAD.add(ref);
    // //elements.add(ref);
    // elt.setNextElementAtIndex(ref, index);
    // /*TMLCPStop stop = new TMLCPStop(ref.getName() + "__stop_after_ref_" + ID,
    // ref.getReferenceObject());
    // elements.add(stop);
    // ref.addNextElement(stop);*/
    // }
    // //ref.setNexts(junction.getNextElements());
    // }
    // }
    //
    // // Removing nexts in new refs, and putting a stop
    // for(TMLCPRefAD ref: refsAD) {
    // ref.clearNexts();
    // elements.add(ref);
    // TMLCPStop stop = new TMLCPStop(ref.getName() + "__stop_after_ref",
    // ref.getReferenceObject());
    // elements.add(stop);
    // ref.addNextElement(stop);
    // }
    //
    // // Moving elements from old AD to split ADs
    // //int cpt = 0;
    // for(TMLCPJunction junction: junctions) {
    // diag = refs.get(junction);
    // //TMLCPRefAD refAD = refsAD.get(cpt);
    // //cpt++;
    //
    // // To be modified-> add elements from RefADs
    // // Also, avoid to add already met elements
    // addElementsFromJunction(junction, diag.getStartElement(), diag, refs,
    // toBeRemoved);
    // }
    //
    // // Removing elements from main diagram
    // for(TMLCPElement elt: toBeRemoved) {
    // elements.remove(elt);
    // }
    //
    //
    //
    // // Returns new elements
    // return refs.values();
    // }

    // private void addElementsFromJunction(TMLCPElement originInOld, TMLCPElement
    // originInNew, TMLCPActivityDiagram newDiag, Map<TMLCPJunction,
    // TMLCPActivityDiagram> refs, List<TMLCPElement> toBeRemoved) {
    // if (originInOld.getNextElements() == null) {
    // return;
    // }
    //
    // for(TMLCPElement elt: originInOld.getNextElements()) {
    // //TraceManager.addDev("Exploring elt (0):" + elt.getName());
    // if (elt instanceof TMLCPJunction) {
    // // Must replace the junction by a ref to an AD
    // TMLCPActivityDiagram toAD = refs.get(elt);
    // TMLCPRefAD ref = new TMLCPRefAD(toAD, toAD.getName(),
    // elt.getReferenceObject());
    // newDiag.addTMLCPElement(ref);
    // originInNew.setNextElement(ref);
    // } else {
    // //TraceManager.addDev("Exploring elt (1):" + elt.getName());
    // if (originInOld != originInNew) {
    // originInNew.addNextElement(elt);
    // }
    // if (!newDiag.contains(elt)) {
    // newDiag.addTMLCPElement(elt);
    // toBeRemoved.add(elt);
    // addElementsFromJunction(elt, elt, newDiag, refs, toBeRemoved);
    // }
    // }
    // }
    // }

    public TMLCPElement getNonConnectedElement() {
        // Starting from Start state ... reaching all elements
        // Then, see elements which are not in the reachable ones
        if (start == null) {
            return null;
        }

        List<TMLCPElement> reached = new ArrayList<TMLCPElement>();

        computeReachableElements(start, reached);

        // Find elements which were not reached
        for (TMLCPElement elt : elements) {
            if (!(reached.contains(elt))) {
                return elt;
            }
        }
        return null;
    }

    public List<TMLCPElement> getAllNonConnectedElements() {
        // Starting from Start state ... reaching all elements
        // Then, see elements which are not in the reachable ones
        if (start == null) {
            return null;
        }

        LinkedList<TMLCPElement> list = new LinkedList<TMLCPElement>();
        ArrayList<TMLCPElement> reached = new ArrayList<TMLCPElement>();

        computeReachableElements(start, reached);

        // Find elements which were not reached
        for (TMLCPElement elt : elements) {
            if (!(reached.contains(elt))) {
                list.add(elt);
            }
        }
        return list;
    }

    public List<TMLCPElement> removeAllNonConnectedElements() {
        // Starting from Start state ... reaching all elements
        // Then, see elements which are not in the reachable ones
        if (start == null) {
            return null;
        }

        List<TMLCPElement> list = new LinkedList<TMLCPElement>();
        List<TMLCPElement> reached = new ArrayList<TMLCPElement>();

        computeReachableElements(start, reached);

        // Find elements which were not reached
        for (TMLCPElement elt : elements) {
            if (!(reached.contains(elt))) {
                list.add(elt);
            }
        }

        for (TMLCPElement elt : list) {
            elements.remove(elt);
        }

        return list;
    }

    private void computeReachableElements(TMLCPElement _elt, List<TMLCPElement> _reached) {
        if (_reached.contains(_elt)) {
            return;
        }

        _reached.add(_elt);

        // Issue #38 found while cleaning code
        for (TMLCPElement elt : _elt.getNextElements()) {
            computeReachableElements(elt, _reached);
            // computeReachableElements(_elt, _reached);
        }
    }

    @Override
    public String toString() {
        String s = "*** Activity diagram " + getName() + "\n";
        for (TMLCPElement elt : elements) {
            s += elt.toString();
        }
        return s + "\n";
    }
} // End of class

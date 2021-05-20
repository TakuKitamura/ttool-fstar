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

package tmltranslator;

/**
 * Class TMLSDAttribute Notion of attribute for a message parameters of a TML
 * Scenario Diagram Creation: 15/05/2014
 * 
 * @version 1.0 15/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDAttribute extends DIPLOElement {

    public TMLSDType type;
    public String name;
    public String initialValue;

    public TMLSDAttribute(String _name) {
        this.name = _name;
        type = new TMLSDType("unknown");
        initialValue = "unknown";
    }

    public TMLSDAttribute(String _name, TMLSDType _type) {
        name = _name;
        type = _type;
    }

    public TMLSDAttribute(String _name, TMLSDType _type, String _initialValue) {
        name = _name;
        type = _type;
        initialValue = _initialValue;
    }

    public String getName() {
        return name;
    }

    public TMLSDType getType() {
        return type;
    }

    public boolean isNat() {
        return (TMLSDType.getType("NATURAL") == TMLSDType.NATURAL);
    }

    public boolean isArch() {
        return (TMLSDType.getType("ARCHITECTURE") == TMLSDType.ARCHITECTURE);
    }

    public String getInitialValue() {
        return initialValue;
    }

    public String toString() {
        return name + ":" + type.toString();
    }

    public boolean hasInitialValue() {
        return ((initialValue != null) && (initialValue.length() > 0));
    }

    public String getDefaultInitialValue() {
        if (isNat()) {
            return "0";
        } else {
            if (isArch()) {
                return "false";
            }
        }
        return "unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof TMLSDAttribute)) {
            return false;
        }
        TMLSDAttribute mt = (TMLSDAttribute) o;
        if (!this.initialValue.equals(mt.initialValue)) {
            return false;
        }
        if (!this.name.equals(mt.name)) {
            return false;
        }
        return this.type.equals(mt.type);
    }
} // End of class

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
 * Class TMLSDType Correspondance between data of a TML Scenario Diagram
 * modeling and graphical elements Creation: 15/05/2014
 * 
 * @version 1.0 23/11/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDType {

    // type
    public final static int NATURAL = 1;
    public final static int ARCHITECTURE = 2;
    public final static int OTHER = 3;

    private int type;
    private String typeOther;

    public TMLSDType(int _type) {
        // TraceManager.addDev("New TYPE:" + _type);
        type = _type;
        typeOther = "";
    }

    public TMLSDType(String _typeOther) {
        type = OTHER;
        typeOther = _typeOther;
    }

    public TMLSDType(int _type, String _typeOther) {
        type = _type;
        typeOther = _typeOther;

    }

    public int getSDType() {
        return type;
    }

    public String getSDTypeOther() {
        return typeOther;
    }

    public void setType(int _type) {
        type = _type;
    }

    public static int getType(String s) {
        s = s.toUpperCase();
        if (s.equals("NATURAL")) {
            return NATURAL;
        } else if (s.equals("ARCHITECTURE")) {
            return ARCHITECTURE;
        } else if (!s.equals("")) {
            return OTHER;
        }

        return -1;
    }

    public static String getLOTOSStringType(int type) {
        switch (type) {
            case NATURAL:
                return "nat";
            case ARCHITECTURE:
                return "arch";
            case OTHER:
                return "Other";
            default:
                return "" + type;
        }
    }

    public static boolean isAValidType(String type) {
        type = type.toUpperCase();

        if (type.compareTo("NAT") == 0) {
            return true;
        }
        if (type.compareTo("INT") == 0) {
            return true;
        }
        return type.compareTo("ARCH") == 0;
    }

    public static String getStringType(int type) {
        switch (type) {
            case NATURAL:
                return "int";
            case ARCHITECTURE:
                return "arch";
            case OTHER:
                return "Other";
            default:
                return "" + type;
        }
    }

    public String toString() {
        return getStringType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof TMLSDType)) {
            return false;
        }
        TMLSDType mt = (TMLSDType) o;
        if (this.type != mt.type) {
            return false;
        }
        return this.typeOther.equals(mt.typeOther);
    }
} // End of class

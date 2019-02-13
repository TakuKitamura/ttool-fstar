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


package ui;

import tmltranslator.TMLTextSpecification;
import translator.JKeyword;
import translator.RTLOTOSKeyword;
import translator.UPPAALKeyword;

import java.util.List;

/**
 * Class TAttribute
 * Correspondance between data of a Turtle modeling and graphical elements
 * Creation: 18/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 18/12/2003
 */
public class TAttribute {

    //access
    public final static int PRIVATE = 0;
    public final static int PROTECTED = 1;
    public final static int PUBLIC = 2;

    // type
    public final static int NATURAL = 0;
    public final static int GATE = 1;
    public final static int OUTGATE = 2;
    public final static int INGATE = 3;
    public final static int BOOLEAN = 4;
    public final static int OTHER = 5;
    public final static int QUEUE_NAT = 6;
    public final static int ARRAY_NAT = 7;
    public final static int INTEGER = 8;
    public final static int TIMER = 9;
    public final static int ADDRESS = 10;
    public final static int DOUBLE = 11;

    // Confidentiality verififcation
    public final static int NOT_VERIFIED = 0;
    public final static int CONFIDENTIALITY_OK = 1;
    public final static int CONFIDENTIALITY_KO = 2;
    public final static int COULD_NOT_VERIFY_CONFIDENTIALITY = 3;


    private int access;
    private String id;
    private String initialValue;
    private int type;
    private String typeOther;

    private int confidentialityVerification = NOT_VERIFIED;

    public boolean isAvatar;
    public boolean isCAMS;

    private boolean set = false;

    public TAttribute(int _access, String _id, String _initialValue, int _type) {
        access = _access;
        id = new String(_id);
        initialValue = new String(_initialValue);
        type = _type;
        typeOther = "";
    }

    public TAttribute(int _access, String _id, String _initialValue, String _typeOther) {
        access = _access;
        id = new String(_id);
        initialValue = new String(_initialValue);
        type = OTHER;
        typeOther = _typeOther;
    }

    public TAttribute(int _access, String _id, String _initialValue, int _type, String _typeOther) {
        access = _access;
        id = new String(_id);
        initialValue = new String(_initialValue);
        type = _type;
        typeOther = new String(_typeOther);
    }


    public int getAccess() {
        return access;
    }

    public String getAccessString() {
        switch (access) {
            case PRIVATE:
                return "private";
            case PROTECTED:
                return "protected";
            case PUBLIC:
            default:
                return "public";
        }

    }

    public String getId() {
        return id;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public int getType() {
        return type;
    }

    public String getTypeOther() {
        return typeOther;
    }

    public boolean hasSameType(TAttribute ta) {
        int otherType = ta.getType();
        return otherType == this.type &&
                (otherType != OTHER || this.typeOther.equals(ta.getTypeOther()));
    }

    public void setAccess(int _access) {
        access = _access;
    }

    public void setType(int _type) {
        type = _type;
    }

    public void setInitialValue(String _initialValue) {
        initialValue = _initialValue;
    }

    public void setTypeOther(String _typeOther) {
        typeOther = _typeOther;
    }

    public boolean isSet() {
        return set;
    }

    public void set(boolean b) {
        set = b;
    }

    public static boolean isAValidId(String id, boolean checkKeyword, boolean checkUPPAALKeywords, boolean checkJavaKeyword) {
        return isAValidId(id, checkKeyword, checkUPPAALKeywords, checkJavaKeyword, false);
    }

    public static boolean isAValidId(String id, boolean checkKeyword, boolean checkUPPAALKeyword, boolean checkJavaKeyword, boolean checkTMLKeyword) {
        // test whether _id is a word

        if ((id == null) || (id.length() < 1)) {
            return false;
        }

        String lowerid = id.toLowerCase();
        boolean b1, b2, b3, b4, b5, b6, b7;
        b1 = (id.substring(0, 1)).matches("[a-zA-Z]");
        b2 = id.matches("\\w*");
        if (checkKeyword) {
            b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        } else {
            b3 = true;
        }
        if (checkUPPAALKeyword) {
            b7 = !UPPAALKeyword.isAKeyword(lowerid);
        } else {
            b7 = true;
        }

        if (checkJavaKeyword) {
            b5 = !JKeyword.isAKeyword(lowerid);
        } else {
            b5 = true;
        }

        b4 = !((lowerid.equals(getStringType(0).toLowerCase())) || (lowerid.equals(getStringType(1).toLowerCase())) || (lowerid.equals(getStringType(2).toLowerCase())) || (lowerid.equals(getStringType(3).toLowerCase())) || (lowerid.equals(getStringType(4).toLowerCase())));

        if (checkTMLKeyword) {
            b6 = TMLTextSpecification.checkKeywords(lowerid);
        } else {
            b6 = true;
        }

        return (b1 && b2 && b3 && b4 && b5 && b6 && b7);
    }

    //checking port name
    //author : minh hiep
    public static boolean isAValidPortName(String id, boolean checkKeyword, boolean checkUPPAALKeyword, boolean checkJavaKeyword,
                                           boolean checkTMLKeyword) {
        // test whether _id is a word
        if (id.contains(",")) {
            String [] splitName = id.split("\\s*,\\s*");
            for (int i = 0; i < splitName.length; i ++) {
                if(!isAValidId(splitName[i],checkKeyword,checkUPPAALKeyword, checkJavaKeyword,checkTMLKeyword)) {
                    return false;
                }
            }
        }else {
            return isAValidId(id,checkKeyword,checkUPPAALKeyword, checkJavaKeyword,checkTMLKeyword);
        }
        return  true;
    }
    public static boolean isAValidInitialValue(int type, String value) {
        //boolean b;
        int val;

        //TraceManager.addDev("Is A Valid Inital Value type=" + type + " value=" + value);

        switch (type) {
            case NATURAL:
                return value.matches("\\d*");
            case ADDRESS:
                return value.matches("\\d*");
            case BOOLEAN:
                //
                return ((value.equals("true")) || (value.equals("false")) || (value.equals("")));
            case GATE:
            case OUTGATE:
            case INGATE:
                return ((value == null) || (value.equals("")));
            case OTHER:
                return ((value == null) || (value.equals("")));
            case QUEUE_NAT:
                return ((value == null) || (value.equals("")) || (value.equals("nil")));
            case ARRAY_NAT:
                if (value == null) {
                    return false;
                }

                try {
                    val = Integer.decode(value).intValue();
                } catch (Exception e) {
                    return false;
                }
                return val > 0;
            case INTEGER:
                return value.matches("\\d*");
            case TIMER:
                return ((value == null) || (value.equals("")));
            default:
                return false;
        }
    }

    public static boolean notIn(String s, List<TAttribute> forbidden) {
        if (forbidden == null)
            return true;

        for (TAttribute t : forbidden)
            if (s.compareTo(t.getId()) == 0)
                return false;

        return true;
    }

    public static int getAccess(String s) {
        if (s.equals("+")) {
            return PUBLIC;
        } else if (s.equals("#")) {
            return PROTECTED;
        } else if (s.equals("-")) {
            return PRIVATE;
        }
        return -1;
    }

    public static int getType(String s) {
        if (s.equals("Natural")) {
            return NATURAL;
        } else if (s.equals("Address")) {
            return ADDRESS;
        } else if (s.equals("Boolean")) {
            return BOOLEAN;
        } else if (s.equals("Gate")) {
            return GATE;
        } else if (s.equals("OutGate")) {
            return OUTGATE;
        } else if (s.equals("InGate")) {
            return INGATE;
        } else if (s.equals("Queue_nat")) {
            return QUEUE_NAT;
        } else if (s.equals("Array_nat")) {
            return ARRAY_NAT;
        } else if (s.equals("Integer")) {
            return INTEGER;
        } else if (s.equals("Timer")) {
            return TIMER;
        } else if (!s.equals("")) {
            return OTHER;
        }
        return -1;
    }

    public static int getAvatarType(String s) {
        if (s.equals("int")) {
            return INTEGER;
        } else if (s.equals("bool")) {
            return BOOLEAN;
        } else if (s.equals("Timer")) {
            return TIMER;
        } else if (s.equals("Integer")) {
            return INTEGER;
        } else if (!s.equals("")) {
            return OTHER;
        }
        return -1;
    }

    public static int getCAMSType(String s) {
        if (s.equals("bool")) {
            return BOOLEAN;
        } else if (s.equals("double")) {
            return DOUBLE;
        } else if (!s.equals("")) {
            return OTHER;
        }
        return -1;
    }


    public static String getStringAccess(int access) {
        switch (access) {
            case PRIVATE:
                return "-";
            case PROTECTED:
                return "#";
            case PUBLIC:
                return "+";
            default:
                return "";
        }
    }

    public static String getStringType(int type) {
        switch (type) {
            case NATURAL:
                return "Natural";
            case ADDRESS:
                return "Address";
            case BOOLEAN:
                return "Boolean";
            case GATE:
                return "Gate";
            case OUTGATE:
                return "OutGate";
            case INGATE:
                return "InGate";
            case OTHER:
                return "Other";
            case QUEUE_NAT:
                return "Queue_nat";
            case ARRAY_NAT:
                return "Array_nat";
            case INTEGER:
                return "Integer";
            case TIMER:
                return "Timer";
            default:
                return "";
        }
    }

    public static String getStringAvatarType(int type) {
        switch (type) {
            case INTEGER:
                return "int";
            case BOOLEAN:
                return "bool";
            case TIMER:
                return "Timer";
            case NATURAL:
                return "int";
            case ADDRESS:
                return "addr";
            default:
                return "unknown";
        }
    }

    public static String getStringCAMSType(int type) {
        switch (type) {
            case DOUBLE:
                return "double";
            case BOOLEAN:
                return "bool";
            default:
                return "unknown";
        }
    }

    public String toString() {
        if (isAvatar) {
            return toAvatarString();
        }
        String myType;
        if (type == OTHER) {
            myType = typeOther;
        } else {
            myType = getStringType(type);
        }

        if ((initialValue == null) || (initialValue.equals(""))) {
            return getStringAccess(access) + " " + id + " : " + myType + ";";
        } else {
            if (type == ARRAY_NAT) {
                return getStringAccess(access) + " " + id + " [" + getInitialValue() + "] : " + myType + ";";
            } else {
                return getStringAccess(access) + " " + id + " = " + getInitialValue() + " : " + myType + ";";
            }
        }
    }

    public String toAvatarString() {
        String myType;
        if (type == OTHER) {
            myType = typeOther;
        } else {
            myType = getStringAvatarType(type);
        }

        if ((initialValue == null) || (initialValue.equals(""))) {
            return getStringAccess(access) + " " + id + " : " + myType + ";";
        } else {
            if (type == ARRAY_NAT) {
                return getStringAccess(access) + " " + id + " [" + getInitialValue() + "] : " + myType + ";";
            } else {
                return getStringAccess(access) + " " + id + " = " + getInitialValue() + " : " + myType + ";";
            }
        }
    }

    public String toCAMSString() {
        String myType;
        if (type == OTHER) {
            myType = typeOther;
        } else {
            myType = getStringCAMSType(type);
        }

        if ((initialValue == null) || (initialValue.equals(""))) {
            return getStringAccess(access) + " " + id + " : " + myType + ";";
        } else {
            if (type == ARRAY_NAT) {
                return getStringAccess(access) + " " + id + " [" + getInitialValue() + "] : " + myType + ";";
            } else {
                return getStringAccess(access) + " " + id + " = " + getInitialValue() + " : " + myType + ";";
            }
        }
    }

    public String toNameAndValue() {
        if (type == ARRAY_NAT) {
            return id + "[" + getInitialValue() + "]";
        } else {
            if ((initialValue == null) || (initialValue.equals(""))) {
                return id + ";";
            } else {
                return id + " = " + getInitialValue() + ";";

            }
        }
    }

    // comparison on id only
    public boolean equals(Object o) {
        if (!(o instanceof TAttribute)) {
            return false;
        }

        TAttribute a = (TAttribute) o;
        return getId().equals(a.getId());

    }

    // comparison on all fields
    public int compareTo(Object o) {
        if (!(o instanceof TAttribute)) {
            return 1;
        }

        TAttribute a = (TAttribute) o;
        if (!(getId().equals(a.getId()))) {
            return 1;
        }

        if (getAccess() != a.getAccess()) {
            return 1;
        }

        if (getType() != a.getType()) {
            return 1;
        }

        if (getType() == OTHER) {
            if (!getTypeOther().equals(a.getTypeOther())) {
                return 1;
            }
        }

        if (!(getInitialValue().equals(a.getInitialValue()))) {
            return 1;
        }

        return 0;

    }

    public TAttribute makeClone() {
        TAttribute ta = new TAttribute(access, id, initialValue, type, typeOther);
        ta.isAvatar = isAvatar;
        return ta;
    }

    public int getConfidentialityVerification() {
        return confidentialityVerification;
    }

    public void setConfidentialityVerification(int _confidentialityVerification) {
        confidentialityVerification = _confidentialityVerification;
    }
}

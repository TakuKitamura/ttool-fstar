/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TType
 * Correspondance between data of a Turtle modeling and graphical elements
 * Creation: 18/12/2003
 * @version 1.0 18/12/2003
 * @author Ludovic APVRILLE
 * @see
 */


package ui;



public class TType {
    
    // type
    public final static int NONE = 0;
    public final static int NATURAL = 1;
    public final static int BOOLEAN = 2;
    public final static int OTHER = 3;
    
    private int type;
    private String typeOther;
    
     public TType() {
        type = NONE;
        typeOther = "";
    }
    
    public TType(int _type) {
        type = _type;
        typeOther = "";
    }
    
    public TType(String _typeOther) {
        type = OTHER;
        typeOther = _typeOther;
    }
    
    public TType(int _type, String _typeOther) {
        type = _type;
        typeOther = _typeOther;
        
    }
    
    
    public int getType() { return type;}
    public String getTypeOther() { return typeOther;}
    
    public void setType(int _type) { type = _type;};
    
    
    
    public static int getType(String s) {
        if (s.equals("Natural")) {
            return 	NATURAL;
        } else if (s.equals("Boolean")) {
            return 	BOOLEAN;
        } else if (!s.equals("")) {
            return OTHER;
        }
        return -1;
    }
    
    
    
    public static String getStringType(int type) {
        switch(type) {
            case NONE:
                return "<unset>";
            case NATURAL:
                return "Natural";
            case BOOLEAN:
                return "Boolean";
            case OTHER:
                return "Other";
            default:
                return "";
        }
    }
    
    public String toString() {
        return getStringType(type);
    }
    
    
    public TType makeClone() {
        return new TType(type, typeOther);
    }
}
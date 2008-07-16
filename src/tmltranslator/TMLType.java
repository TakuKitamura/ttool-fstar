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
 * Class TMLType
 * Correspondance between data of a TML modeling and graphical elements
 * Creation: 23/11/2005
 * @version 1.0 23/11/2005
 * @author Ludovic APVRILLE
 * @see
 */


package tmltranslator;



public class TMLType {
    
    // type
    public final static int NATURAL = 1;
    public final static int BOOLEAN = 2;
    public final static int OTHER = 3;
    
    private int type;
    private String typeOther;
    
    public TMLType(int _type) {
        type = _type;
        typeOther = "";
    }
    
    public TMLType(String _typeOther) {
        type = OTHER;
        typeOther = _typeOther;
    }
    
    public TMLType(int _type, String _typeOther) {
        type = _type;
        typeOther = _typeOther;
        
    }
    
    
    public int getType() { return type;}
    public String getTypeOther() { return typeOther;}
    
    public void setType(int _type) { type = _type;};
    

    public static int getType(String s) {
		s = s.toUpperCase();
        if (s.equals("NATURAL")) {
            return 	NATURAL;
        } else if (s.equals("BOOLEAN")) {
            return 	BOOLEAN;
        } else if (s.equals("NAT")) {
            return 	NATURAL;
        } else if (s.equals("INT")) {
            return 	NATURAL;
        } else if (s.equals("BOOL")) {
            return 	BOOLEAN;
        } else if (!s.equals("")) {
            return OTHER;
        }
		
        return -1;
    }
    
    public static String getLOTOSStringType(int type) {
        switch(type) {
            case NATURAL:
                return "nat";
            case BOOLEAN:
                return "bool";
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
		
		if (type.compareTo("BOOL") == 0) {
			return true;
		}
		
		return false;
		
	}
    
    
    
    public static String getStringType(int type) {
        switch(type) {
            case NATURAL:
                return "int";
            case BOOLEAN:
                return "bool";
            case OTHER:
                return "Other";
            default:
                return "" + type;
        }
    }
    
    public String toString() {
        return getStringType(type);
    }
    
}
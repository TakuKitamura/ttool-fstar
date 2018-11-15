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

package tmltranslator.modelcompiler;

/**
 * Class CVariable
 * Abstract Data Type that implements a C variable composed of type and identifier
 * Creation: 11/10/2016
 * @version 1.0 11/10/2016
 * @author Andrea ENRICI
 */
public class CVariable implements CCodeGenConstants {
    
    // type
    //private final static String SPACE = " ";

    public final static int NATURAL = 1;
    public final static int BOOLEAN = 2;
    public final static int ADDRESS = 3;
    public final static int OTHER = 4;

    public final static String ADDRESS_STRING = "addr";

    //public final static HashSet<String> typesDataBase = new HashSet<String>();
        /*typesDataBase.add( "void" );
        typesDataBase.add( "float" );
        typesDataBase.add( "double" );
        typesDataBase.add( "char" );
        typesDataBase.add( "unsigned char" );
        typesDataBase.add( "int" );
        typesDataBase.add( "unsigned int" );
        typesDataBase.add( "short" );
        typesDataBase.add( "unsigned short" );
        typesDataBase.add( "long" );
        typesDataBase.add( "unsigned long" );
        typesDataBase.add( "uint32_t" );
        typesDataBase.add( "int32_t" );
        typesDataBase.add( "uint64_t" );
        typesDataBase.add( "int64_t" );
        typesDataBase.add( "uintptr_t" );
        typesDataBase.add( "intprt_t" );*/
    
    private String type;
    private String name;
    
    public CVariable( String _type, String _name )    {
        type = _type;
        name = _name;
    }
    
    public String getType()	{
        return type;
    }

    public String getName()    {
        return name;
    }
    
    public void setType( String _type )    {
        type = _type;
    }
    
    public void setName( String _name )    {
        name = _name;
    }

    public static int getIntegerType( String s ) {
		s = s.toUpperCase();
        if (s.equals("NATURAL")) {
            return 	NATURAL;
        } else if (s.equals("BOOLEAN")) {
            return 	BOOLEAN;
        } else if (s.equals("ADDRESS")) {
            return ADDRESS;
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
    
    public static String getStringType( String type )  {
        switch(type) {
            case NATURAL_TYPE:
                return "nat";
            case BOOLEAN_TYPE:
                return BOOLEAN_TYPE;
            case ADDRESS_STRING:
                return "addr";
            default:
                return "" + type;
        }
    }
	
	public static boolean isAValidType( String type )   {
		type = type.toUpperCase();
		
		if (type.compareTo("NAT") == 0) {
			return true;
		}  
		
		if (type.compareTo("INT") == 0) {
			return true;
		}

        return type.compareTo("BOOL") == 0;

    }
    
    @Override public String toString() {
        return type + SP + name;
    }

	@Override public boolean equals( Object o )	{
		if( !( o instanceof CVariable ) )	{
			return false;
		}
		else    {
			CVariable var = (CVariable)o;
			return ( (getType().equals(var.getType())) && (getName().equals(var.getName())) );
		}
	}

	@Override public int hashCode()	{
		int result = 17;
		result = 31 * result + type.hashCode();
		return result;
	}
    
}	//End of class

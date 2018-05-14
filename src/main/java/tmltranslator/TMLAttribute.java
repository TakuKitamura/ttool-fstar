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
 * Class TMLAttribute
 * Notion of attribute for a TML Task
 * Creation: 24/11/2005
 * @version 1.0 21/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLAttribute extends DIPLOElement {

    public TMLType type;
    public String name = "";
    public String initialValue = "";
    private String instanceName = "";

    public TMLAttribute( String _name, String _instanceName, TMLType _type, String _initialValue ) {
        this.name = _name;
        this.instanceName = _instanceName;
        this.type = _type;
        this.initialValue = _initialValue;
    }

    public TMLAttribute( String _name, String _instanceName, TMLType _type ) {
        this.name = _name;
        this.instanceName = _instanceName;
        this.type = _type;
        this.initialValue = "";
    }

    public TMLAttribute( String _name, TMLType _type ) {
        this.name = _name;
        this.instanceName = "NO_NAME";
        this.type = _type;
        this.initialValue = "";
    }

    public TMLAttribute( String _name, TMLType _type, String _initialValue ) {
        this.name = _name;
        this.instanceName = "NO_NAME";
        this.type = _type;
        this.initialValue = _initialValue;
    }

    public TMLAttribute( String _name ) {
        this.name = _name;
        this.instanceName = "NO_NAME";
        this.type = new TMLType( TMLType.OTHER );
        this.initialValue = "";
    }

    public String getInstanceName()     {
        return instanceName;
    }

    public String getName() {
        return name;
    }

    public TMLType getType() {
        return type;
    }

    public boolean isNat() {
        return (type.getType() == TMLType.NATURAL);
    }

    public boolean isBool() {
        return (type.getType() ==  TMLType.BOOLEAN);
    }

    public boolean isAddress() {
        return (type.getType() ==  TMLType.ADDRESS);
    }

    public String getInitialValue() {
        return initialValue;
    }

    public String toString() {
        return instanceName + "." + name + ":" + type.toString() + "=" + initialValue;
    }

    public boolean hasInitialValue() {
        return ((initialValue != null) && (initialValue.length() > 0));
    }

    public String getDefaultInitialValue() {
        if (isNat()) {
            return "0";
        } else {
            if (isBool()) {
                return "false";
            }
        }
        return "unknown";
    }

    @Override public boolean equals( Object o )        {
        if( !(o instanceof TMLAttribute ) )     {
            return false;
        }
        else    {
            TMLAttribute attr = (TMLAttribute)o;
            return ( name.equals( attr.getName() ) && ( type.equals( attr.getType()) ) );
        }
        //return ( (name.equals( _other.getName() )) && ( initialValue.equals( _other.getInitialValue() )) && (type.equals( _other.getType() )) );
    }

    @Override public int hashCode()     {
        int result = 17;
        if( name == null )      {
            result = 31 * result;
        }
        else    {
            result = 31 * result + name.hashCode();
        }
        if( type == null )      {
            result = 31 * result;
        }
        else    {
            result = 31 * result + type.hashCode();
        }
        return result;
    }

    public String toXML() {
	String s = "<ATTRIBUTE type=\"" + type.getType() + "\" name=\"" + name + "\" initialValue=\"" + initialValue + "\"/>\n";
	return s;
    }
}//End of class

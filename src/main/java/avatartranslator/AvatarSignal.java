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

package avatartranslator;

/**
   * Class AvatarSignal
   * Signals in Avatar ...
   * Creation: 20/05/2010
   * @version 1.0 20/05/2010
   * @author Ludovic APVRILLE
 */
public class AvatarSignal extends AvatarMethod {

    // Signal type
    public final static int IN = 0;
    public final static int OUT = 1;

    private int inout;

    public AvatarSignal(String _name, int _inout, Object _referenceObject) {
        super(_name, _referenceObject);
        
        inout = _inout;
        name = _name;
    }

    public int getInOut() {
        return inout;
    }

    public String getSignalName() {
        return name;
    }

    public void setInOut(int _inout) {
        inout = _inout;
    }

    public boolean isOut() {
        return (inout == OUT);
    }

    public boolean isIn() {
        return (inout == IN);
    }

    public static boolean isAValidSignal(String _signal) {
        return AvatarTerm.isValidName (_signal);
    }

    @Override
    public String toString() {
        String ret = super.toString();
        if (isOut()) {
            return "out " + ret;
        }
        return "in " + ret;
    }

    @Override
    public String toBasicString() {
        String ret = super.toBasicString();
        if (isOut()) {
            return "out " + ret;
        }
        return "in " + ret;
    }

	public String minString(){
        int cpt = 0;
		String ret = getName() + "(";
        for(AvatarAttribute attribute: parameters) {
            if (cpt != 0) {
                ret += ",";
            }
            cpt ++;
            ret += attribute.getName();
        }

        ret += ")";
        return ret;
	}

	public int getNbParams(){
        int cpt = 0;
	
        for(AvatarAttribute attribute: parameters) {           
           cpt ++;            
        }
        return cpt;
	}

    //DG 13.06.
    /* public int getCumulSizeParams(){
        int cumul = 0;
	
        for(AvatarAttribute attribute: parameters) { 
	    AvatarType type = attribute.getType();
	    cumul += 4;  //hack          
        }
        return cumul;
	}*/
    //fin DG 
	public AvatarSignal advancedClone(AvatarStateMachineOwner _block) {
		AvatarSignal as = new AvatarSignal(getName(), getInOut(), getReferenceObject());
		setAdvancedClone(as, _block);
		return as;
	}


	public boolean isCompatibleWith(AvatarSignal _as) {
		if (getInOut() == _as.getInOut()) {
			return false;
		}

		return super.isCompatibleWith(_as);
	}
}

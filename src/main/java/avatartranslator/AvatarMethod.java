/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

  ludovic.apvrille AT enst.fr

  This software is a computer program whose purpose is to allow the 
  edition of TURTLE analysis, design and deployment diagrams, to 
  allow the generation of RT-LOTOS or Java code from this diagram, 
  and at last to allow the analysis of formal validation traces 
  obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
  from INRIA Rhone-Alpes.

  This software is governed by the CeCILL  license under French law and
  abiding by the rules of distribution of free software.  You can  use, 
  modify and/ or redistribute the software under the terms of the CeCILL
  license as circulated by CEA, CNRS and INRIA at the following URL
  "http://www.cecill.info". 

  As a counterpart to the access to the source code and  rights to copy,
  modify and redistribute granted by the license, users are provided only
  with a limited warranty  and the software's author,  the holder of the
  economic rights,  and the successive licensors  have only  limited
  liability. 

  In this respect, the user's attention is drawn to the risks associated
  with loading,  using,  modifying and/or developing or reproducing the
  software by the user in light of its specific status of free software,
  that may mean  that it is complicated to manipulate,  and  that  also
  therefore means  that it is reserved for developers  and  experienced
  professionals having in-depth computer knowledge. Users are therefore
  encouraged to load and test the software's suitability as regards their
  requirements in conditions enabling the security of their systems and/or 
  data to be ensured and,  more generally, to use and operate it in the 
  same conditions as regards security. 

  The fact that you are presently reading this means that you have had
  knowledge of the CeCILL license and that you accept its terms.

/**
 * Class AvatarMethod
 * Method in Avatar ...
 * Creation: 08/04/2010
 * @version 1.0 08/04/2010
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator;

import java.util.LinkedList;

public class AvatarMethod extends AvatarElement {

    protected boolean implementationProvided;

    protected LinkedList<AvatarAttribute> parameters;
    protected LinkedList<AvatarAttribute> returnParameters;


    public AvatarMethod(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        parameters = new LinkedList<AvatarAttribute>();
        returnParameters = new LinkedList<AvatarAttribute>();
    }

    public void setImplementationProvided(boolean _imp) {
        implementationProvided = _imp;
    }

    public boolean isImplementationProvided() {
        return implementationProvided;
    }

    public void addParameter(AvatarAttribute _attribute) {
        parameters.add(_attribute);
    }

    public void addReturnParameter(AvatarAttribute _attribute) {
        returnParameters.add(_attribute);
    }

    public LinkedList<AvatarAttribute> getListOfAttributes() {
        return parameters;
    }

    public LinkedList<AvatarAttribute> getListOfReturnAttributes() {
        return returnParameters;
    }

    public static boolean isAValidMethodName(String _method) {
        return AvatarTerm.isValidName (_method);
    }

    public String toString() {
        int cpt = 0;
        String ret = "";

        if (returnParameters.size() != 0) {
            if (returnParameters.size() == 1) {
                ret += returnParameters.get(0).getType().getStringType() + " ";
            } else {
                int index = 0;
                for(AvatarAttribute aa: returnParameters) {
                    if (index == 0) {
                        index ++;
                    } else {
                        ret = ret + ",";
                    }
                    ret += aa.getType().getStringType();
                }
                ret = "(" + ret + ") ";
            }

        }

        ret += getName() + "(";
        for(AvatarAttribute attribute: parameters) {
            if (cpt != 0) {
                ret += ",";
            }
            cpt ++;
            ret += attribute.toString();
        }

        ret += ")";
        return ret;
    }

    public String toBasicString() {
        int cpt = 0;
        String ret = "";

        if (returnParameters.size() != 0) {
            if (returnParameters.size() == 1) {
                ret += returnParameters.get(0).getType().getStringType() + " ";
            } else {
                int index = 0;
                for(AvatarAttribute aa: returnParameters) {
                    if (index == 0) {
                        index ++;
                    } else {
                        ret = ret + ",";
                    }
                    ret += aa.getType().getStringType();
                }
                ret = "(" + ret + ") ";
            }

        }

        ret += getName() + "(";

        for(AvatarAttribute attribute: parameters) {
            if (cpt != 0) {
                ret += ",";
            }
            cpt ++;
            ret += attribute.toBasicString();
        }

        ret += ")";
        return ret;
    }

    public boolean isCompatibleWith(AvatarMethod _am) {
        if (parameters.size() != _am.getListOfAttributes().size()) {
            return false;
        }

        AvatarAttribute _ama;
        int cpt = 0;
        for(AvatarAttribute aa: parameters) {
            _ama = _am.getListOfAttributes().get(cpt);
            if (_ama.getType() != aa.getType()) {
                return false;
            }
            cpt ++;
        }

        if (returnParameters.size() != _am.getListOfReturnAttributes().size()) {
            return false;
        }

        cpt = 0;
        for(AvatarAttribute aa: returnParameters) {
            _ama = _am.getListOfReturnAttributes().get(cpt);
            if (_ama.getType() != aa.getType()) {
                return false;
            }
            cpt ++;
        }

        return true;
    }

    protected void setAdvancedClone(AvatarMethod am, AvatarStateMachineOwner _block) {
	am.setImplementationProvided(isImplementationProvided());
	for(AvatarAttribute param: parameters) {
	    am.addParameter(param.advancedClone(_block));
	}
	for(AvatarAttribute ret: returnParameters) {
	    am.addReturnParameter(ret.advancedClone(_block));
	}
    }

    public AvatarMethod advancedClone(AvatarStateMachineOwner _block) {
	AvatarMethod am = new AvatarMethod(getName(), getReferenceObject());
	setAdvancedClone(am, _block);	
	return am;
    }


}

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

import java.util.Map;

import myutil.TraceManager;

import java.lang.reflect.Field;

/**
 * Class AvatarAttribute Avatar attributes, either of blocks, or manipulated by
 * signals / methods Creation: 20/05/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/05/2010
 */
public class AvatarAttribute extends AvatarLeftHand {

    // Types of parameters
    private AvatarType type;
    private String initialValue;

    private AvatarStateMachineOwner block;

    public AvatarAttribute(String _name, AvatarType _type, AvatarStateMachineOwner _block, Object _referenceObject) {
        super(_name, _referenceObject);
        type = _type;
        this.block = _block;
    }

    public AvatarStateMachineOwner getBlock() {
        return this.block;
    }

    public void setInitialValue(String _initialValue) {
        initialValue = _initialValue;
    }

    public boolean hasInitialValue() {
        if (getInitialValue() == null) {
            return false;
        }
        return (!(getInitialValue().trim().length() == 0));
    }

    public String getInitialValue() {
        if ((initialValue == null) || (initialValue.length() == 0)) {
            return getDefaultInitialValue();
        }
        return initialValue;
    }

    public int getInitialValueInInt() {
        if ((initialValue == null) || (initialValue.length() == 0)) {
            return getDefaultInitialValueInInt();
        }

        if (isInt()) {
            try {
                return Integer.decode(initialValue).intValue();
            } catch (Exception e) {
                return -1;
            }
        }

        if (isBool()) {
            if (initialValue.charAt(0) == 'f') {
                return 0;
            } else {
                return 1;
            }
        }

        return 0;
    }

    public String getDefaultInitialValue() {
        return this.type.getDefaultInitialValue();
    }

    public String getDefaultInitialValueTF() {
        return this.type.getDefaultInitialValueTF();
    }

    public int getDefaultInitialValueInInt() {
        return this.type.getDefaultInitialValueInInt();
    }

    public AvatarType getType() {
        return this.type;
    }

    public boolean isInt() {
        return (type == AvatarType.INTEGER);
    }

    public boolean isBool() {
        return (type == AvatarType.BOOLEAN);
    }

    public boolean isTimer() {
        return (type == AvatarType.TIMER);
    }

    public String toString() {
        String ret = this.type.getStringType() + " " + getName();
        if (initialValue == null) {
            return ret;
        }

        return ret + " = " + initialValue;
    }

    public String toBasicString() {
        String ret = this.type.getStringType() + " " + getName();
        return ret;
    }

    public String toStringType() {
        String ret = type.getStringType() + " " + getName() + " typeid= " + getType();
        if (initialValue == null) {
            return ret;
        }

        return ret + " = " + initialValue;
    }

    public boolean isLeftHand() {
        return true;
    }

    @Override
    public boolean containsAMethodCall() {
        return false;
    }

    @Override
    public AvatarAttribute clone() {
        return this;
    }

    @Override
    public void replaceAttributes(Map<AvatarAttribute, AvatarAttribute> attributesMapping) {
        TraceManager.addDev("!!! ERROR !!! (replaceAttributes in AvatarAttribute)");
        /* !!! We should never arrive here !!! */
    }

    public AvatarAttribute advancedClone(AvatarStateMachineOwner _block) {
        AvatarAttribute aa = new AvatarAttribute(getName(), getType(), _block, getReferenceObject());
        if (hasInitialValue()) {
            aa.setInitialValue(getInitialValue());
        }

        return aa;
    }

}

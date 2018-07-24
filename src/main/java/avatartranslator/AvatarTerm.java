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
import translator.JKeyword;
import translator.RTLOTOSKeyword;

/**
   * Class AvatarTerm
   * Creation: 16/09/2015
   * @version 1.0 16/09/2015
   * @author Florian LUGOU
 */
public abstract class AvatarTerm extends AvatarElement {

    public abstract boolean isLeftHand ();

    public AvatarTerm (String _name, Object _referenceObject) {
        super (_name, _referenceObject);
    }

    public static AvatarTerm createFromString (AvatarStateMachineOwner block, String toParse) {
        return AvatarTerm.createFromString(block, toParse, false);
    }

    public static AvatarTerm createFromString (AvatarStateMachineOwner block, String toParse, boolean allowRaw) {
        if (toParse == null || toParse.isEmpty ())
            return null;

        AvatarTerm result = AvatarTermFunction.createFromString (block, toParse);
        if (result != null)
            return result;

        result = AvatarTuple.createFromString (block, toParse);
        if (result != null)
            return result;

        result = AvatarArithmeticOp.createFromString (block, toParse);
        if (result != null)
            return result;

        toParse = toParse.trim ();
        result = block.getAvatarAttributeWithName (toParse);
        if (result != null)
            return result;
        // TraceManager.addDev ("AvatarAttribute '" + toParse + "' couldn't be parsed");

        result = block.getAvatarSpecification ().getAvatarConstantWithName (toParse);
        if (result != null)
            return result;

        try {
            // TODO: replace that by a true AvatarNumeric
            //int i = Integer.parseInt (toParse);
            result = new AvatarConstant (toParse, block);
            block.getAvatarSpecification ().addConstant ((AvatarConstant) result);
            return result;
        } catch (NumberFormatException e) { }

        // Consider that new names are constants
        if (AvatarTerm.isValidName (toParse)) {
            result = new AvatarConstant (toParse, block);
            block.getAvatarSpecification ().addConstant ((AvatarConstant) result);
            return result;
        }
        //TraceManager.addDev ("AvatarConstant '" + toParse + "' couldn't be parsed");

        //TraceManager.addDev ("AvatarTerm '" + toParse + "' couldn't be parsed");
        //if (allowRaw)
            return new AvatarTermRaw (toParse, block);
	    //else
            //return null;
    }

    public static AvatarAction createActionFromString (AvatarStateMachineOwner block, String toParse) {
        AvatarAction result = null;

        int indexEq = toParse.indexOf("=");
        if (indexEq == -1)
            // No equal sign: this must be a function call
            result = AvatarTermFunction.createFromString (block, toParse);

        else {
            // This should be an assignment
            AvatarTerm leftHand = AvatarTerm.createFromString (block, toParse.substring (0, indexEq));
            AvatarTerm rightHand = AvatarTerm.createFromString (block, toParse.substring (indexEq + 1));
            if (leftHand != null && rightHand != null && leftHand.isLeftHand ())

                result = new AvatarActionAssignment ((AvatarLeftHand) leftHand, rightHand);
        }

        if (result == null)
            TraceManager.addDev ("Action '" + toParse + "' couldn't be parsed");

        return result;
    }

    public static boolean isValidName (String _name) {
        String toParse = _name.trim ();
        String lowerid = toParse.toLowerCase();
        boolean b1, b2, b3, b4, b5;
        b1 = (toParse.substring(0,1)).matches("[a-zA-Z]");
        b2 = toParse.matches("\\w*");
        b3 = !RTLOTOSKeyword.isAKeyword(lowerid);
        b4 = true;
        for (AvatarType type: AvatarType.values ())
            if (lowerid.equals(type.getStringType ().toLowerCase ()))
                b4 = false;
        b5 = !JKeyword.isAKeyword(lowerid);

        return (b1 && b2 && b3 && b4 && b5);
    }

    /**
     * Returns True if the whole action contains a method call.
     *
     * @return True if the whole action contains a method call. False otherwise.
     */
    public abstract boolean containsAMethodCall ();

    /**
     * Returns a full clone of the term.
     *
     * <p>Note that this is a full clone except for {@link AvatarAttribute} and {@link AvatarConstant} that shouldn't be cloned</p>
     *
     * @return A clone of the term.
     */
    public abstract AvatarTerm clone ();

    /**
     * Replaces attributes in this term according to the provided mapping.
     *
     * @param attributesMapping
     *      The mapping used to replace the attributes of the term. All the attributes of the block should be present as keys.
     */
    public abstract void replaceAttributes( Map<AvatarAttribute, AvatarAttribute> attributesMapping);
}

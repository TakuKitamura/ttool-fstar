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

import myutil.TraceManager;
import ui.TAttribute;
import ui.avatarbd.AvatarBDPragma;

import java.util.*;

/**
 * Class AvatarPragma Creation: 20/05/2010
 * 
 * @version 1.1 01/07/2014
 * @author Ludovic APVRILLE, Raja GATGOUT
 */
public abstract class AvatarPragma extends AvatarElement implements Comparable<AvatarPragma> {
    public static final String[] PRAGMAS = { "Confidentiality", "Secret", "SecrecyAssumption", "InitialSystemKnowledge",
            "InitialSessionKnowledge", "Authenticity", "PrivatePublicKeys", "Public", "PublicConstant",
            "PrivateConstant" };
    public static final String[] PRAGMAS_TRANSLATION = { "Secret", "Secret", "SecrecyAssumption",
            "InitialSystemKnowledge", "InitialSessionKnowledge", "Authenticity", "PrivatePublicKeys", "Public",
            "PublicConstant", "PrivateConstant" };

    private int proofStatus = 0;

    public AvatarPragma(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
    }

    public int getProofStatus() {
        return proofStatus;
    }

    public void setProofStatus(int status) {
        proofStatus = status;
    }

    public static List<AvatarPragma> createFromString(String str, Object obj, List<AvatarBlock> blocks,
            Map<String, List<TAttribute>> typeAttributesMap, Map<String, String> nameTypeMap,
            ErrorAccumulator errorAcc) {
        // createFromString takes in a pragma string (with # removed), the containing
        // object, and the list of AvatarBlocks, and returns the corresponding
        // AvatarPragma or null if an error occurred
        // The attributes referenced must exist

        List<AvatarPragma> pragmas = new LinkedList<AvatarPragma>();
        AvatarBDPragma pragmaObj = (AvatarBDPragma) obj;
        // Remove leading spaces
        str = str.trim();

        String[] split = str.split("\\s+");
        if (split.length < 2) {
            // One word is not enough
            return pragmas;
        }

        String header = split[0];
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        if (header.equals("Authenticity")) {
            if (args.length != 2) {
                TraceManager.addDev("Wrong number of attributes for Authenticity Pragma " + str);
                errorAcc.addWarning("Wrong number of attributes for Authenticity Pragma " + str);
                pragmaObj.syntaxErrors.add("#" + str);
                return pragmas;
            }
            String[] split1 = args[0].split("\\.");
            String[] split2 = args[1].split("\\.");
            // Must be blockName.stateName.attributeName
            if (split1.length != 3 || split2.length != 3) {
                TraceManager.addDev("Badly Formatted Pragma Attribute " + str);
                errorAcc.addWarning("Badly Formatted Pragma Attribute " + str);
                pragmaObj.syntaxErrors.add("#" + str);
                return pragmas;
            }
            String blockName1 = split1[0];
            String blockName2 = split2[0];
            String attrName1 = split1[2];
            String attrName2 = split2[2];
            if (!nameTypeMap.containsKey(blockName1 + "." + attrName1)
                    && !nameTypeMap.containsKey(blockName2 + "." + attrName2)) {
                // Not composed types
                LinkedList<AvatarAttributeState> attrStates = new LinkedList<AvatarAttributeState>();
                for (String arg : args) {
                    AvatarAttributeState res = parseAuthAttr(arg, blocks);
                    if (res == null) {
                        TraceManager.addDev("Can't find Pragma Attribute " + arg);
                        errorAcc.addWarning("Can't find Pragma Attribute " + arg);
                        pragmaObj.syntaxErrors.add("#" + str);
                        return pragmas;
                    }
                    attrStates.add(res);
                }
                // Check if same type
                if (attrStates.get(0).getAttribute().getType() != attrStates.get(1).getAttribute().getType()) {
                    TraceManager.addDev("Incompatible types " + str);
                    errorAcc.addWarning("Incompatible types " + str);
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                pragmas.add(new AvatarPragmaAuthenticity(str, obj, attrStates));
                return pragmas;
            } else if (!nameTypeMap.containsKey(blockName1 + "." + attrName1)
                    && nameTypeMap.containsKey(blockName2 + "." + attrName2)
                    || nameTypeMap.containsKey(blockName1 + "." + attrName1)
                            && !nameTypeMap.containsKey(blockName2 + "." + attrName2)) {
                // 1 composed type, 1 not
                TraceManager.addDev("Incompatible types " + str);
                errorAcc.addWarning("Incompatible types " + str);
                pragmaObj.syntaxErrors.add("#" + str);
                return pragmas;
            } else {
                // Yay composed types
                if (!nameTypeMap.get(blockName1 + "." + attrName1)
                        .equals(nameTypeMap.get(blockName2 + "." + attrName2))) {
                    // Different types
                    TraceManager.addDev("Incompatible types " + str);
                    errorAcc.addWarning("Incompatible types " + str);
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                // Generate a fun lot of pragmas...
                // For each attribute, generate an authenticity pragma
                List<TAttribute> typeAttrs = typeAttributesMap.get(nameTypeMap.get(blockName1 + "." + attrName1));

                for (TAttribute ta : typeAttrs) {
                    List<AvatarAttributeState> attrStates = new LinkedList<AvatarAttributeState>();
                    String suffix = ta.getId();
                    for (String arg : args) {
                        AvatarAttributeState res = parseAuthAttr(arg + "__" + suffix, blocks);
                        if (res == null) {
                            TraceManager.addDev("Can't find Pragma Attribute " + arg + "__" + suffix);
                            errorAcc.addWarning("Can't find Pragma Attribute " + arg + "__" + suffix);
                            pragmaObj.syntaxErrors.add("#" + str);
                            return pragmas;
                        }
                        attrStates.add(res);
                    }
                    pragmas.add(new AvatarPragmaAuthenticity(str, obj, attrStates));
                }
                return pragmas;
            }
        } else if (header.equals("PrivateConstant")) {
            LinkedList<AvatarConstant> constants = new LinkedList<AvatarConstant>();
            for (String arg : args) {
                if (!AvatarTerm.isValidName(arg)) {
                    errorAcc.addWarning("Constant name '" + arg + "' is not valid");
                    continue;
                }
                constants.add(new AvatarConstant(arg, obj));
            }
            pragmas.add(new AvatarPragmaConstant(str, obj, constants, false));
        } else if (header.equals("PublicConstant")) {
            LinkedList<AvatarConstant> constants = new LinkedList<AvatarConstant>();
            for (String arg : args) {
                if (!AvatarTerm.isValidName(arg)) {
                    errorAcc.addWarning("Constant name '" + arg + "' is not valid");
                    continue;
                }
                constants.add(new AvatarConstant(arg, obj));
            }
            pragmas.add(new AvatarPragmaConstant(str, obj, constants, true));
        } else if (header.equals("PrivatePublicKeys")) {
            LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
            if (args.length != 3) {
                TraceManager.addDev("Wrong number of attributes for PrivatePublicKeys Pragma " + str);
                errorAcc.addWarning("Wrong number of attributes for PrivatePublicKeys Pragma " + str);
                pragmaObj.syntaxErrors.add("#" + str);
                return pragmas;
            }
            String blockName = args[0];
            String attr1 = args[1];
            String attr2 = args[2];
            // Check if simple type or has only one field
            if (nameTypeMap.containsKey(blockName + "." + attr1)) {
                // Find # of fields
                String type = nameTypeMap.get(blockName + "." + attr1);
                if (typeAttributesMap.get(type).size() != 1) {
                    TraceManager.addDev("PrivatePublicKey cannot have more than 1 attribute " + attr1);
                    errorAcc.addWarning("PrivatePublicKey cannot have more than 1 attribute " + attr1);
                    return pragmas;
                }
                TAttribute ta = typeAttributesMap.get(type).get(0);
                attr1 = attr1 + "__" + ta.getId();
            }
            if (nameTypeMap.containsKey(blockName + "." + attr2)) {
                // Find # of fields
                String type = nameTypeMap.get(blockName + "." + attr2);
                if (typeAttributesMap.get(type).size() != 1) {
                    TraceManager.addDev("PrivatePublicKey cannot have more than 1 attribute " + attr2);
                    errorAcc.addWarning("PrivatePublicKey cannot have more than 1 attribute " + attr2);
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                TAttribute ta = typeAttributesMap.get(type).get(0);
                attr2 = attr2 + "__" + ta.getId();
            }
            for (String attr : new String[] { attr1, attr2 }) {
                AvatarAttribute res = parseAttr(blockName, attr, blocks);
                if (res == null) {
                    TraceManager.addDev("Can't find Pragma Attribute " + attr);
                    errorAcc.addWarning("Can't find Pragma Attribute " + attr);
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                attrs.add(res);
            }
            pragmas.add(new AvatarPragmaPrivatePublicKey(str, obj, attrs));
        } else if (header.equals("InitialSystemKnowledge") || header.equals("InitialSessionKnowledge")) {
            // Check if all types are the same
            TreeSet<String> types = new TreeSet<String>();
            for (String arg : args) {
                String[] sp = arg.split("\\.");
                // Must be blockName.attributeName
                if (sp.length != 2) {
                    TraceManager.addDev("Badly Formatted Pragma Attribute " + str);
                    errorAcc.addWarning("Badly Formatted Pragma Attribute " + str);
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                String blockName = sp[0];
                String attrName = sp[1];
                if (nameTypeMap.containsKey(blockName + "." + attrName)) {
                    types.add(nameTypeMap.get(blockName + "." + attrName));

                } else {
                    types.add("base");
                }
            }
            if (types.size() != 1) {
                TraceManager.addDev("Initial Knowledge Pragma attributes must be same type " + str);
                errorAcc.addWarning("Initial Knowledge Pragma attributes must be same type " + str);
                pragmaObj.syntaxErrors.add("#" + str);
                return pragmas;
            }
            if (types.first().equals("base")) {
                // Simple type
                LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
                for (String arg : args) {
                    String[] sp = arg.split("\\.");
                    String blockName = sp[0];
                    String attrName = sp[1];
                    AvatarAttribute res = parseAttr(blockName, attrName, blocks);
                    if (res == null) {
                        TraceManager.addDev("Can't find Pragma Attribute " + arg);
                        errorAcc.addWarning("Can't find Pragma Attribute " + arg);
                        pragmaObj.syntaxErrors.add("#" + str);
                        return pragmas;
                    }
                    attrs.add(res);
                }
                // Check if same type
                AvatarType type = attrs.get(0).getType();
                for (int i = 1; i < attrs.size(); i++) {
                    if (type != attrs.get(i).getType()) {
                        TraceManager.addDev("Incompatible types " + str);
                        errorAcc.addWarning("Incompatible types " + str);
                        pragmaObj.syntaxErrors.add("#" + str);
                        return pragmas;
                    }
                }
                pragmas.add(new AvatarPragmaInitialKnowledge(str, obj, attrs, header.equals("InitialSystemKnowledge")));
            } else {
                List<TAttribute> typeAttrs = typeAttributesMap.get(types.first());

                for (TAttribute ta : typeAttrs) {
                    List<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
                    String suffix = ta.getId();

                    for (String arg : args) {
                        String[] sp = arg.split("\\.");
                        String blockName = sp[0];
                        String attrName = sp[1];
                        AvatarAttribute res = parseAttr(blockName, attrName + "__" + suffix, blocks);
                        if (res == null) {
                            TraceManager.addDev("Can't find Pragma Attribute " + attrName + "__" + suffix);
                            errorAcc.addWarning("Can't find Pragma Attribute " + attrName + "__" + suffix);
                            pragmaObj.syntaxErrors.add("#" + str);
                            return pragmas;
                        }
                        attrs.add(res);
                    }
                    pragmas.add(
                            new AvatarPragmaInitialKnowledge(str, obj, attrs, header.equals("InitialSystemKnowledge")));
                }
            }
        } else {
            LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
            for (String arg : args) {
                String[] sp = arg.split("\\.");
                // Must be blockName.attributeName
                if (sp.length != 2) {
                    TraceManager.addDev("Badly Formatted Pragma Attribute " + str);
                    errorAcc.addWarning("Badly Formatted Pragma Attribute in '" + str + "'");
                    pragmaObj.syntaxErrors.add("#" + str);
                    return pragmas;
                }
                String blockName = sp[0];
                String attrName = sp[1];
                if (nameTypeMap.containsKey(blockName + "." + attrName)) {
                    // Composed type, YAY#$%^&*
                    List<TAttribute> typeAttrs = typeAttributesMap.get(nameTypeMap.get(blockName + "." + attrName));

                    for (TAttribute ta : typeAttrs) {
                        String suffix = ta.getId();
                        AvatarAttribute res = parseAttr(blockName, attrName + "__" + suffix, blocks);
                        if (res == null) {
                            TraceManager.addDev("Can't find Pragma Attribute " + attrName + "__" + suffix);
                            errorAcc.addWarning("Can't find Pragma Attribute " + attrName + "__" + suffix);
                            pragmaObj.syntaxErrors.add("#" + str);
                            return pragmas;
                        }
                        attrs.add(res);
                    }
                } else {
                    AvatarAttribute res = parseAttr(blockName, attrName, blocks);
                    if (res == null) {
                        TraceManager.addDev("Can't find Pragma Attribute " + arg);
                        errorAcc.addWarning("Can't find Pragma Attribute " + arg);
                        pragmaObj.syntaxErrors.add("#" + str);
                        return pragmas;
                    }
                    attrs.add(res);
                }
            }
            switch (header) {
                case "Confidentiality":
                    for (AvatarAttribute attr : attrs)
                        pragmas.add(new AvatarPragmaSecret(str, obj, attr));
                    break;
                case "Secret":
                    for (AvatarAttribute attr : attrs)
                        pragmas.add(new AvatarPragmaSecret(str, obj, attr));
                    break;
                case "SecrecyAssumption":
                    pragmas.add(new AvatarPragmaSecrecyAssumption(str, obj, attrs));
                    break;
                case "Public":
                    pragmas.add(new AvatarPragmaPublic(str, obj, attrs));
                    break;
                default:
                    TraceManager.addDev("Invalid Pragma Name " + header);
                    errorAcc.addWarning("Invalid Pragma Name " + header);
                    // Invalid pragma
                    return pragmas;
            }
        }
        return pragmas;
    }

    public static AvatarAttribute parseAttr(String blockName, String attrName, List<AvatarBlock> blocks) {
        // Iterate through blocks
        for (AvatarBlock block : blocks) {
            if (block.getName().equals(blockName)) {
                // If the state is found, find either 'attrName' or 'attrName__data'
                return block.getAvatarAttributeWithName(attrName);
            }
        }
        TraceManager.addDev("Pragma Attribute Block not found " + blockName);
        return null;
    }

    public static AvatarAttributeState parseAuthAttr(String arg, List<AvatarBlock> blocks) {
        // Iterate through the list of blocks
        String[] split = arg.split("\\.");
        String blockName = split[0];
        String stateName = split[1];
        String attrName = split[2];
        for (AvatarBlock block : blocks) {
            if (block.getName().equals(blockName)) {
                // Check if the state exists
                AvatarStateMachine asm = block.getStateMachine();
                if (asm.getStateWithName(stateName) != null) {
                    // If the state is found, find either 'attrName' or 'attrName__data'
                    AvatarAttribute attr = block.getAvatarAttributeWithName(attrName);
                    if (attr == null) {
                        return null;
                    }
                    return new AvatarAttributeState(stateName + "." + attrName, attr, attr,
                            asm.getStateWithName(stateName));
                } else {
                    TraceManager.addDev("Pragma Attribute State not found " + stateName);
                    return null;
                }
            }
        }
        TraceManager.addDev("Pragma Attribute Block not found");
        return null;
    }

    /**
     * Returns a full clone of the pragma.
     *
     * @param avspec The AvatarSpecification that will contain the new pragma. Note
     *               that the specification should have already been populated with
     *               blocks and attributes.
     *
     * @return A full clone of the pragma.
     */
    public abstract AvatarPragma advancedClone(AvatarSpecification avspec);

    @Override
    public int compareTo(AvatarPragma b) {
        return this.toString().compareTo(b.toString());
    }
}

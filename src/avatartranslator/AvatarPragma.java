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
 * Class AvatarPragma
 * Creation: 20/05/2010
 * @version 1.1 01/07/2014
 * @author Ludovic APVRILLE, Raja GATGOUT
 * @see
 */

package avatartranslator;

import java.util.*;
import ui.avatarbd.*;
import myutil.*;
import ui.*;

public abstract class AvatarPragma extends AvatarElement {
     private final String[] PRAGMAS = {"Confidentiality", "Secret", "SecrecyAssumption", "InitialSystemKnowledge", "InitialSessionKnowledge", "Authenticity", "PrivatePublicKeys", "Public", "Constant"};
    private final String[] PRAGMAS_TRANSLATION = {"Secret", "Secret", "SecrecyAssumption", "InitialSystemKnowledge", "InitialSessionKnowledge", "Authenticity", "PrivatePublicKeys", "Public", "Constant"};
    private LinkedList<AvatarAttribute> arguments;

    
    private int proofStatus = 0;


    public AvatarPragma(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
    }
    public LinkedList<AvatarAttribute> getArgs(){
	return arguments;
    }
    public int getProofStatus(){
	return proofStatus;
    }
    public void setProofStatus(int status){
	proofStatus = status;
    }
    public static AvatarPragma createFromString(String str, Object obj, LinkedList<AvatarBlock> blocks){
	//createFromString takes in a pragma string (with # removed), the containing object, and the list of AvatarBlocks, and returns the corresponding AvatarPragma or null if an error occurred
	//The attributes referenced must exist 
	//Remove leading spaces
    	str = str.trim();
	String[] split = str.split("\\s+");
	if (split.length < 2){
	  return null;
	}
        String header = split[0];
	String[] args = Arrays.copyOfRange(split, 1, split.length);
	LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute>();
	if (header.equals("Authenticity")){
	    //uses AttributeStates
	    if (args.length != 2){
		return null;
	    }
	    LinkedList<AvatarAttributeState> attrStates = new LinkedList<AvatarAttributeState>();
	    for (String arg: args){
		AvatarAttributeState res = parseAuthAttr(arg, blocks);
		if (res ==null){
		    TraceManager.addDev("Can't find Pragma Attribute " + arg);
		    return null;
		}
		attrStates.add(res);
	    }
	    return new AvatarPragmaAuthenticity(str, obj, attrStates);
	}
	else if (header.equals("Constant")){
	    LinkedList<AvatarConstant> constants = new LinkedList<AvatarConstant>();
	    for (String arg: args){
		constants.add(new AvatarConstant(arg, obj));
	    }
	    return new AvatarPragmaConstant(str, obj, constants);
	}
	else {
	    for (String arg: args){
		AvatarAttribute res = parseAttr(arg, blocks);
		if (res ==null){
		    TraceManager.addDev("Can't find Pragma Attribute "+ arg);
		    return null;
		}
		attrs.add(res);
	    }
	    switch(header){
	        case "Confidentiality":
		    return new AvatarPragmaSecret(str, obj, attrs);
	        case "Secret":
		    return new AvatarPragmaSecret(str, obj, attrs);
	        case "SecrecyAssumption":
		    return new AvatarPragmaSecrecyAssumption(str, obj, attrs);
	        case "InitialSystemKnowledge":
		    return new AvatarPragmaInitialKnowledge(str, obj, attrs, true);
	        case "InitialSessionKnowledge":
		    return new AvatarPragmaInitialKnowledge(str, obj, attrs, false);
	        case "PrivatePublicKeys":
		    if (args.length != 2){
			TraceManager.addDev("Wrong Number of attributes for Private public key");
		        return null;
		    }
		    return new AvatarPragmaPrivatePublicKey(str, obj, attrs);
	        case "Public":
		    return new AvatarPragmaPublic(str, obj, attrs);
	        default:
		    TraceManager.addDev("Invalid Pragma Name " + header);
		    //Invalid pragma
		    return null;
	    }
	}
    }
    public static AvatarAttribute parseAttr(String arg, LinkedList<AvatarBlock> blocks){
	String[] split = arg.split("\\.");
	// Must be blockName.attributeName
	if (split.length != 2){
	    TraceManager.addDev("Badly Formatted Pragma Attribute");
	    return null;
	}
	String blockName = split[0];
	String attrName = split[1];
	//Iterate through blocks
	for (AvatarBlock block:blocks){
	    if (block.getName().equals(blockName)){
		//If the state is found, find either 'attrName' or 'attrName__data'
		AvatarAttribute attr= block.getAvatarAttributeWithName(attrName);
		if (attr ==null){
		    return block.getAvatarAttributeWithName(attrName+"__data");
		}
		return attr;   
	    }
	}
	TraceManager.addDev("Pragma Attribute Block not found "+ blockName);
	return null;
    }
    public static AvatarAttributeState parseAuthAttr(String arg, LinkedList<AvatarBlock> blocks){
	//For Finding Authenticity Attributes
	String[] split = arg.split("\\.");
	// Must be blockName.stateName.attributeName
	if (split.length != 3){
	    TraceManager.addDev("Badly Formatted Pragma Attribute");
	    return null;
	}
	String blockName = split[0];
	String stateName = split[1];
	String attrName = split[2];
	//Iterate through the list of blocks
	for (AvatarBlock block:blocks){
	    if (block.getName().equals(blockName)){
		//Check if the state exists
		AvatarStateMachine asm = block.getStateMachine();
		if (asm.getStateWithName(stateName) !=null){
		    //If the state is found, find either 'attrName' or 'attrName__data'
		    AvatarAttribute attr= block.getAvatarAttributeWithName(attrName);
		    if (attr ==null){
			attr= block.getAvatarAttributeWithName(attrName+"__data");
			if (attr==null){
			    return null;
			}
			return new AvatarAttributeState(stateName+"."+attrName+"__data", attr, attr, asm.getStateWithName(stateName));   
		    }
		    return new AvatarAttributeState(stateName+"."+attrName, attr, attr, asm.getStateWithName(stateName));   
		}
		else {
		    TraceManager.addDev("Pragma Attribute State not found "+ stateName);
  		    return null;
		}
	    }
	}
	TraceManager.addDev("Pragma Attribute Block not found");
	return null;
    }
}

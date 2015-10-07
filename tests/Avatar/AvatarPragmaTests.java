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


import java.util.LinkedList;
import java.util.HashMap;
import java.util.Vector;

import ui.TAttribute;
import avatartranslator.AvatarStateMachine;
import avatartranslator.AvatarState;
import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarType;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarPragma;
import avatartranslator.AvatarPragmaSecret;
import avatartranslator.AvatarPragmaSecrecyAssumption;
import avatartranslator.AvatarPragmaInitialKnowledge;
import avatartranslator.AvatarPragmaPrivatePublicKey;
import avatartranslator.AvatarPragmaPublic;
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaConstant;

import avatartranslator.*;

public class AvatarPragmaTests extends TToolTest {	

	public AvatarPragmaTests () {
        super ("AvatarPragmas", false);
    }

    protected void test () {
	AvatarBlock A = new AvatarBlock("A", null, null);
	AvatarStateMachine Aasm = A.getStateMachine();
	Aasm.addElement(new AvatarState("a1", null));
	A.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, A, null));
	A.addAttribute(new AvatarAttribute("key2", AvatarType.INTEGER, A, null));

	AvatarBlock B = new AvatarBlock("B", null, null);
	AvatarStateMachine Basm = B.getStateMachine();
	Basm.addElement(new AvatarState("b1", null));
	B.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, B, null));
	B.addAttribute(new AvatarAttribute("key2", AvatarType.BOOLEAN, B, null));
	B.addAttribute(new AvatarAttribute("m__a", AvatarType.UNDEFINED, B, null));
	B.addAttribute(new AvatarAttribute("m__b", AvatarType.UNDEFINED, B, null));

	AvatarBlock C = new AvatarBlock("C", null, null);
	AvatarStateMachine Casm = C.getStateMachine();
	Casm.addElement(new AvatarState("c1", null));
	C.addAttribute(new AvatarAttribute("attr", AvatarType.INTEGER, C, null));
	C.addAttribute(new AvatarAttribute("m__a", AvatarType.UNDEFINED, C, null));	
	C.addAttribute(new AvatarAttribute("m__b", AvatarType.UNDEFINED, C, null));
	C.addAttribute(new AvatarAttribute("d__c", AvatarType.UNDEFINED, C, null));	

	LinkedList<AvatarBlock> blocks = new LinkedList<AvatarBlock>();
	blocks.add(A);
	blocks.add(B);
        blocks.add(C);

	HashMap<String, Vector> typeAttributesMap = new HashMap<String, Vector>();  
        HashMap<String, String> nameTypeMap = new HashMap<String, String>();
	LinkedList<AvatarPragma> res;

	//Type T1: a,b
	//Type T2: c
	TAttribute attr_a = new TAttribute(2, "a", "0", 2);
	TAttribute attr_b = new TAttribute(2, "b", "1", 1);
	TAttribute attr_c = new TAttribute(2, "c", "true", 0);
	nameTypeMap.put("C.m", "T1");
	nameTypeMap.put("B.m", "T1");
	nameTypeMap.put("C.d", "T2");
	Vector t1s= new Vector();
	Vector t2s= new Vector();
	t1s.add(attr_a);
	t1s.add(attr_b);
	t2s.add(attr_c);
	typeAttributesMap.put("T1", t1s);
	typeAttributesMap.put("T2", t2s);	

	//Test Bad keyword
	res = AvatarPragma.createFromString("FakePragma A.key1", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Bad keyword: " + (res.size()==0));

	//Handle whitespace
	res = AvatarPragma.createFromString("Public      A.key1", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Whitespace parsing: " + (res.size()!=0));
	res = AvatarPragma.createFromString("PrivatePublicKeys   A.key1     B.key2", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Whitespace parsing: " + (res.size()!=0));

	//Test missing block
	res = AvatarPragma.createFromString("Confidentiality non.arrrrg", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Missing Block: " + (res.size()==0));
	res = AvatarPragma.createFromString("Confidentiality A.key1 B.key2 C.attr non.arrrrg", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Missing Block: " + (res.size()==0));

	//Test badly formed attribute
	res = AvatarPragma.createFromString("Confidentiality attr", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Attribute formatting: " + (res.size()==0));
	res = AvatarPragma.createFromString("Public A.a1.attr", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Attribute formatting: " + (res.size()==0));

	//Test missing attribute
	res = AvatarPragma.createFromString(" Confidentiality            ", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Missing Attribute: " + (res.size()==0));
	res = AvatarPragma.createFromString("Confidentiality A.arrrrg", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Missing Attribute: " + (res.size()==0));
	res = AvatarPragma.createFromString("Confidentiality C.attr C.attr B.key1 A.arrrrg", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Missing Attribute: " + (res.size()==0));
	this.updateDigest("-------------------------------------");

	//Test Confidentiality
	this.updateDigest("Confidentiality Tests");
	res = AvatarPragma.createFromString("Confidentiality A.key1", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()==1));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaSecret));
	//1 Attribute
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 1));
	this.updateDigest("Attr Name " + res.get(0).getArgs().get(0));
	this.updateDigest("-------------------------------------");

	//Test Secret
	this.updateDigest("Secret Tests");
	res = AvatarPragma.createFromString("Secret A.key1 A.key2", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()==1));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaSecret));
	//Attributes
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 2));
	this.updateDigest(res.get(0).getArgs().get(0));
	this.updateDigest("Attr Name "+ (res.get(0).getArgs().get(0) +";"));
	this.updateDigest("Attr Name "+ (res.get(0).getArgs().get(1)+";"));

	//Composed Types
	res = AvatarPragma.createFromString("Secret B.m C.d", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("No error: "+ (res.size()==1));
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 3));
	this.updateDigest("-------------------------------------");
	
	//Test Secrecy Assumption
	this.updateDigest("SecrecyAssumption Tests");
	res = AvatarPragma.createFromString("SecrecyAssumption A.key1 A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()==1));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaSecrecyAssumption));
	//Attributes
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 4));
	this.updateDigest("-------------------------------------");
	
	//Test System Knowledge
	this.updateDigest("Initial System Knowledge Tests");

	//Check composed types
	res = AvatarPragma.createFromString("InitialSystemKnowledge B.key1 C.m", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Fail if different types: "+ (res.size()==0));
	res = AvatarPragma.createFromString("InitialSystemKnowledge B.d C.m", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Fail if different types: "+ (res.size()==0));

	res = AvatarPragma.createFromString("InitialSystemKnowledge A.key1 A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()!=0));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaInitialKnowledge));
	//Attributes
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 4));
	//Is system
	AvatarPragmaInitialKnowledge res2 = (AvatarPragmaInitialKnowledge) res.get(0);
	this.updateDigest("Is System: " + res2.isSystem());

	//Check multiple creation
	res = AvatarPragma.createFromString("InitialSystemKnowledge B.m C.m", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Multiple creation: "+ (res.size()==2));
	this.updateDigest("Attr Name " + res.get(0).getArgs().get(0));
	this.updateDigest("Attr Name " + res.get(0).getArgs().get(1));
	this.updateDigest("Attr Name " + res.get(1).getArgs().get(0));
	this.updateDigest("Attr Name " + res.get(1).getArgs().get(1));
	this.updateDigest("-------------------------------------");

	//Test System Knowledge
	this.updateDigest("Initial Session Knowledge Tests");
	res = AvatarPragma.createFromString("InitialSessionKnowledge A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()==0));
	
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaInitialKnowledge));
	//1 Attribute
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 3));
	//Is session
	res2 = (AvatarPragmaInitialKnowledge) res.get(0);
	this.updateDigest("Is Session: " + !res2.isSystem());
	this.updateDigest("-------------------------------------");

	//Test PrivatePublicKey
	this.updateDigest("PrivatePublicKeys Tests");
	//Fail if wrong # of args
	res = AvatarPragma.createFromString("PrivatePublicKeys C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("PrivatePublicKeys args count " + (res.size()==0));
	res = AvatarPragma.createFromString("PrivatePublicKeys A.key1 A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("PrivatePublicKeys args count " + (res.size()==0));
	//Check no error
	res = AvatarPragma.createFromString("PrivatePublicKeys A key2 key1", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("No error: "+ (res.size()!=0));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaPrivatePublicKey));
	//Attributes
	AvatarPragmaPrivatePublicKey res4 = (AvatarPragmaPrivatePublicKey) res.get(0);
	this.updateDigest("# of Attributes: " + (res4.getArgs().size() == 2));
	this.updateDigest("Attr Name "+ res4.getPublicKey());
	this.updateDigest("Attr Name "+ res4.getPrivateKey());

	//Handle composed types
	res = AvatarPragma.createFromString("PrivatePublicKeys C attr d", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("PrivatePublicKeys composed types " + (res.size()!=0));
	//Fail if more than 1 field
	res = AvatarPragma.createFromString("PrivatePublicKeys C attr m", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("PrivatePublicKeys multiple fields " + (res.size()==0));

	
	this.updateDigest("-------------------------------------");

	//Test Public
	this.updateDigest("Public Tests");
	res = AvatarPragma.createFromString("Public A.key1 B.key2", null,blocks, typeAttributesMap, nameTypeMap);	
	//Check no error
	this.updateDigest("No error: "+ (res.size()!=0));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaPublic));
	//1 Attribute
	this.updateDigest("# of Attributes: " + (res.get(0).getArgs().size() == 2));
	this.updateDigest("Attr Name " + res.get(0).getArgs().get(0));
	this.updateDigest("Attr Name " + res.get(0).getArgs().get(1));
	this.updateDigest("-------------------------------------");
	
	//Test Authenticity
	this.updateDigest("Authenticity Tests");
	//Fail if wrong # of args
	res = AvatarPragma.createFromString("Authenticity A.key1 A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Authenticity args count " + (res.size()==0));
	res = AvatarPragma.createFromString("Authenticity C.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Authenticity args count " + (res.size()==0));
	//Fail if lack of state
	res = AvatarPragma.createFromString("Authenticity A.state.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Missing State " + (res.size()==0));
	//Fail if attributes are not same type
	res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.m", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Incompatible types " + (res.size()==0));
	res = AvatarPragma.createFromString("Authenticity B.a1.m C.c1.d", null,blocks, typeAttributesMap, nameTypeMap);
	this.updateDigest("Incompatible types " + (res.size()==0));
	//Check no error
	res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.attr", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("No error: "+ (res.size()!=0));
	//Check Type
	this.updateDigest("Right Type: " + (res.get(0) instanceof AvatarPragmaAuthenticity));
	//Attribute
	AvatarPragmaAuthenticity res3 = (AvatarPragmaAuthenticity) res.get(0);
	this.updateDigest("# of Attributes: " + (res3.getArgs().size() == 2));
	this.updateDigest("Attr "+ res3.getAttrA());
	this.updateDigest("Attr "+ res3.getAttrB());
	this.updateDigest("Attr Name "+ res3.getAttrA().getName());
	this.updateDigest("Attr Name "+ res3.getAttrB().getName());
	this.updateDigest("Attr State "+ res3.getAttrA().getState());
	this.updateDigest("Attr State "+ res3.getAttrB().getState());
	//Check multi-creation
	res = AvatarPragma.createFromString("Authenticity B.b1.m C.c1.m", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Multiple creation pass: "+ (res.size()==2));
	res3 = (AvatarPragmaAuthenticity) res.get(1);
	this.updateDigest("# of Attributes: " + (res3.getArgs().size() == 2));
	this.updateDigest("Attr "+ res3.getAttrA());
	this.updateDigest("Attr "+ res3.getAttrB());
	this.updateDigest("Attr Name "+ res3.getAttrA().getName());
	this.updateDigest("Attr Name "+ res3.getAttrB().getName());
	this.updateDigest("Attr State "+ res3.getAttrA().getState());
	this.updateDigest("Attr State "+ res3.getAttrB().getState());
	res3 = (AvatarPragmaAuthenticity) res.get(0);
	this.updateDigest("# of Attributes: " + (res3.getArgs().size() == 2));
	this.updateDigest("Attr Name "+ res3.getAttrA().getName());
	this.updateDigest("Attr Name "+ res3.getAttrB().getName());
	this.updateDigest("Attr State "+ res3.getAttrA().getState());
	this.updateDigest("Attr State "+ res3.getAttrB().getState());
	this.updateDigest("-------------------------------------");
	

	//Test Constants
	this.updateDigest("Constant Tests");
	res = AvatarPragma.createFromString("Constant 1 0 a b", null,blocks, typeAttributesMap, nameTypeMap);	
	this.updateDigest("Right type :" + (res.get(0) instanceof AvatarPragmaConstant));
	AvatarPragmaConstant res5 = (AvatarPragmaConstant) res.get(0);
        this.updateDigest("Right number of constants " + (res5.getConstants().size() == 4));
	for (int i=0; i< res5.getConstants().size(); i++){
            this.updateDigest("Constant " + res5.getConstants().get(i).getName());
	}
	this.updateDigest("-------------------------------------");


	//Avatar Specification Tests
	
	this.updateDigest("Tests finished");
    }
    public static void main(String[] args){
        AvatarPragmaTests apt = new AvatarPragmaTests ();
        apt.runTest ();
    }
}

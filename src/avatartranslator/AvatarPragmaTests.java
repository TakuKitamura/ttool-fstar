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

import myutil.*;


public class AvatarPragmaTests {	

    public static void main(String[] args){
	
	//AvatarStateMachine Aasm = new AvatarStateMachine("Aasm", null);
	//Aasm.addElement(new AvatarState("a1", null));
	AvatarBlock A = new AvatarBlock("A", null);
	AvatarStateMachine Aasm = A.getStateMachine();
	Aasm.addElement(new AvatarState("a1", null));
	A.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, null));
	A.addAttribute(new AvatarAttribute("key2", AvatarType.INTEGER, null));

	AvatarBlock B = new AvatarBlock("B", null);
	B.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, null));
	B.addAttribute(new AvatarAttribute("key2", AvatarType.BOOLEAN, null));

	AvatarBlock C = new AvatarBlock("C", null);
	AvatarStateMachine Casm = C.getStateMachine();
	Casm.addElement(new AvatarState("c1", null));
	C.addAttribute(new AvatarAttribute("attr", AvatarType.INTEGER, null));


	//System.out.println(A.getStateMachine().getStateWithName("a1"));

	LinkedList<AvatarBlock> blocks = new LinkedList<AvatarBlock>();
	blocks.add(A);
	blocks.add(B);
        blocks.add(C);
	AvatarPragma res;

	//Test Bad keyword
	res = AvatarPragma.createFromString("FakePragma A.key1", null,blocks);
	System.out.println("Bad keyword: " + (res==null));

	//Handle whitespace
	res = AvatarPragma.createFromString("Public      A.key1", null,blocks);
	System.out.println("Whitespace parsing: " + (res!=null));
	res = AvatarPragma.createFromString("PrivatePublicKeys   A.key1     B.key2", null,blocks);
	System.out.println("Whitespace parsing: " + (res!=null));

	//Test missing block
	res = AvatarPragma.createFromString("Confidentiality non.arrrrg", null,blocks);
	System.out.println("Missing Block: " + (res==null));
	res = AvatarPragma.createFromString("Confidentiality A.key1 B.key2 C.attr non.arrrrg", null,blocks);
	System.out.println("Missing Block: " + (res==null));

	//Test badly formed attribute
	res = AvatarPragma.createFromString("Confidentiality attr", null,blocks);
	System.out.println("Attribute formatting: " + (res==null));
	res = AvatarPragma.createFromString("Public A.a1.attr", null,blocks);
	System.out.println("Attribute formatting: " + (res==null));

	//Test missing attribute
	res = AvatarPragma.createFromString(" Confidentiality            ", null,blocks);
	System.out.println("Missing Attribute: " + (res==null));
	res = AvatarPragma.createFromString("Confidentiality A.arrrrg", null,blocks);
	System.out.println("Missing Attribute: " + (res==null));
	res = AvatarPragma.createFromString("Confidentiality C.attr C.attr B.key1 A.arrrrg", null,blocks);
	System.out.println("Missing Attribute: " + (res==null));
	System.out.println("-------------------------------------");

	//Test Confidentiality
	System.out.println("Confidentiality Tests");
	res = AvatarPragma.createFromString("Confidentiality A.key1", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaSecret));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 1));
	System.out.println("Attr Name " + res.getArgs().get(0));
	System.out.println("-------------------------------------");

	//Test Secret
	System.out.println("Secret Tests");
	res = AvatarPragma.createFromString("Secret A.key1 A.key2", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaSecret));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 2));
	System.out.println(res.getArgs().get(0));
	System.out.println("Attr Name "+ (res.getArgs().get(0).equals("int key1")));
	System.out.println("Attr Name "+(res.getArgs().get(1).equals("int key2")));
	System.out.println("-------------------------------------");
	
	//Test Secrecy Assumption
	System.out.println("SecrecyAssumption Tests");
	res = AvatarPragma.createFromString("SecrecyAssumption A.key1 A.key2 B.key1 C.attr", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaSecrecyAssumption));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 4));
	System.out.println("-------------------------------------");
	
	//Test System Knowledge
	System.out.println("Initial System Knowledge Tests");
	res = AvatarPragma.createFromString("InitialSystemKnowledge A.key1 A.key2 B.key1 C.attr", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaInitialKnowledge));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 4));
	//Is system
	AvatarPragmaInitialKnowledge res2 = (AvatarPragmaInitialKnowledge) res;
	System.out.println("Is System: " + res2.isSystem());
	System.out.println("-------------------------------------");

	//Test System Knowledge
	System.out.println("Initial Session Knowledge Tests");
	res = AvatarPragma.createFromString("InitialSessionKnowledge A.key2 B.key1 C.attr", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaInitialKnowledge));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 3));
	//Is session
	res2 = (AvatarPragmaInitialKnowledge) res;
	System.out.println("Is Session: " + !res2.isSystem());
	System.out.println("-------------------------------------");

	//Test PrivatePublicKey
	System.out.println("PrivatePublicKeys Tests");
	//Fail if wrong # of args
	res = AvatarPragma.createFromString("PrivatePublicKeys C.attr", null,blocks);	
	System.out.println("PrivatePublicKeys args count " + (res==null));
	res = AvatarPragma.createFromString("PrivatePublicKeys A.key1 A.key2 B.key1 C.attr", null,blocks);	
	System.out.println("PrivatePublicKeys args count " + (res==null));
	//Check no error
	res = AvatarPragma.createFromString("PrivatePublicKeys A.key2 B.key1", null,blocks);	
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaPrivatePublicKey));
	//1 Attribute
	AvatarPragmaPrivatePublicKey res4 = (AvatarPragmaPrivatePublicKey) res;
	System.out.println("# of Attributes: " + (res4.getArgs().size() == 2));
	System.out.println("Attr Name "+ res4.getPublicKey());
	System.out.println("Attr Name "+ res4.getPrivateKey());
	System.out.println("-------------------------------------");

	//Test Public
	System.out.println("Public Tests");
	res = AvatarPragma.createFromString("Public A.key1 B.key2", null,blocks);	
	//Check no error
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaPublic));
	//1 Attribute
	System.out.println("# of Attributes: " + (res.getArgs().size() == 2));
	System.out.println("Attr Name " + res.getArgs().get(0));
	System.out.println("Attr Name " + res.getArgs().get(1));
	System.out.println("-------------------------------------");
	
	//Test Authenticity
	System.out.println("Authenticity Tests");
	//Fail if wrong # of args
	res = AvatarPragma.createFromString("Authenticity A.key1 A.key2 B.key1 C.attr", null,blocks);	
	System.out.println("Authenticity args count " + (res==null));
	res = AvatarPragma.createFromString("Authenticity C.attr", null,blocks);	
	System.out.println("Authenticity args count " + (res==null));
	//Fail if lack of state
	res = AvatarPragma.createFromString("Authenticity A.state.attr", null,blocks);	
	System.out.println("Missing State " + (res==null));
	//Check no error
	res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.attr", null,blocks);	
	System.out.println("No error: "+ (res !=null));
	//Check Type
	System.out.println("Right Type: " + (res instanceof AvatarPragmaAuthenticity));
	//1 Attribute
	AvatarPragmaAuthenticity res3 = (AvatarPragmaAuthenticity) res;
	System.out.println("# of Attributes: " + (res.getArgs().size() == 2));
	System.out.println("Attr "+ res3.getAttrA());
	System.out.println("Attr "+ res3.getAttrB());
	System.out.println("Attr Name "+ res3.getAttrA().getName());
	System.out.println("Attr Name "+ res3.getAttrB().getName());
	System.out.println("Attr State "+ res3.getAttrA().getState());
	System.out.println("Attr State "+ res3.getAttrB().getState());
	System.out.println("-------------------------------------");


	//Test Constants
	System.out.println("Constant Tests");
	res = AvatarPragma.createFromString("Constant 1 0 a b", null,blocks);	
	System.out.println("Right type :" + (res instanceof AvatarPragmaConstant));
	AvatarPragmaConstant res5 = (AvatarPragmaConstant) res;
        System.out.println("Right number of constants " + (res5.getConstants().size() == 4));
	for (int i=0; i< res5.getConstants().size(); i++){
            System.out.println("Constant " + res5.getConstants().get(i).getName());
	}
	System.out.println("-------------------------------------");


	//Avatar Specification Tests
	
	System.out.println("Tests finished");
    }
    public static void test(String[] args){
	System.out.println("!!!");
	}
}

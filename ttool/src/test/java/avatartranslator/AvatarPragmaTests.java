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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ui.TAttribute;
import ui.avatarbd.AvatarBDPanel;
import ui.avatarbd.AvatarBDPragma;

public class AvatarPragmaTests {	
	List<AvatarPragma> res;
	ErrorAccumulator errorAcc;
	Map<String, List<TAttribute>> typeAttributesMap = new HashMap<>();  
	Map<String, String> nameTypeMap = new HashMap<String, String>();
	List<AvatarBlock> blocks; 
	AvatarBDPragma bdpragma;
	
	public AvatarPragmaTests() {
       //
    }
	
	@Before
	public void setupBlocks(){
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

		blocks = new LinkedList<AvatarBlock>();
		blocks.add(A);
		blocks.add(B);
        blocks.add(C);


		//Type T1: a,b
		//Type T2: c
		TAttribute attr_a = new TAttribute(2, "a", "0", 2);
		TAttribute attr_b = new TAttribute(2, "b", "1", 1);
		TAttribute attr_c = new TAttribute(2, "c", "true", 0);
		this.nameTypeMap.put("C.m", "T1");
		this.nameTypeMap.put("B.m", "T1");
		this.nameTypeMap.put("C.d", "T2");
		List<TAttribute> t1s = new LinkedList<TAttribute>();
		List<TAttribute> t2s = new LinkedList<TAttribute>();
		t1s.add(attr_a);
		t1s.add(attr_b);
		t2s.add(attr_c);
		this.typeAttributesMap.put("T1", t1s);
		this.typeAttributesMap.put("T2", t2s);	
   		this.errorAcc = new ErrorAccumulator () {
            public void addWarning (String s) { }
            public void addError (String s) { }
    	};
		bdpragma = new AvatarBDPragma(0, 0, 0, 0, 0, 0, false, null, new AvatarBDPanel(null,null));
	}
    //protected void test () {
	
	@Test
	public void testBadKeywordNoPragmaCreated(){
		//Test Bad keyword
		this.res = AvatarPragma.createFromString("FakePragma A.key1", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(res.size(),0);
	}
	
	@Test
	public void checkHandleWhitespaceParsing(){
		//Handle whitespace
		this.res = AvatarPragma.createFromString("Public      A.key1", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertTrue(this.res.size()!=0);
		this.res = AvatarPragma.createFromString("Confidentiality   A.key1     B.key2", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertTrue(this.res.size()!=0);
	}
	
	@Test
	public void testDetectionMissingBlockNoPragmaCreated(){
		//Test missing block
		this.res = AvatarPragma.createFromString("Confidentiality non.arrrrg", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
		this.res = AvatarPragma.createFromString("Confidentiality A.key1 B.key2 C.attr non.arrrrg", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
	}
	
	@Test
	public void testDetectionAttributeFormatErrorNoPragmaCreated(){
		//Test badly formed attribute
		this.res = AvatarPragma.createFromString("Confidentiality attr", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
		this.res = AvatarPragma.createFromString("Public A.a1.attr", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
	}

	@Test
	public void testDetectionMissingAttributeNoPragmaCreated(){
		//Test missing attribute
		this.res = AvatarPragma.createFromString("Confidentiality            ", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
		this.res = AvatarPragma.createFromString("Confidentiality A.arrrrg", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
		this.res = AvatarPragma.createFromString("Confidentiality C.attr C.attr B.key1 A.arrrrg", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(this.res.size(),0);
	}


	@Test
	public void testCreateConfidentialityPragma(){
		//Test Confidentiality
		this.res = AvatarPragma.createFromString("Confidentiality A.key1", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);	
		//Check no error
		assertEquals(this.res.size(),1);
		//Check Type
		assertTrue(this.res.get(0) instanceof AvatarPragmaSecret);
		//1 Attribute
		assertTrue(((AvatarPragmaSecret) this.res.get(0)).getArg() instanceof AvatarAttribute);
	}

	@Test
	public void testCreateSecretPragma(){
		//Test Secret
		this.res = AvatarPragma.createFromString("Secret A.key1 A.key2", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);	
		//Check no error
		assertEquals(this.res.size(),2);
		//Check Type
		assertTrue(this.res.get(0) instanceof AvatarPragmaSecret);
		//Attributes
		assertEquals(((AvatarPragmaSecret) res.get(0)).getArg().getName(),"key1");
		assertEquals(((AvatarPragmaSecret) res.get(1)).getArg().getName(),"key2");
	// this.updateDigest("Attr Name "+ (((AvatarPragmaSecret) res.get(0)).getArgs().get(1)+";"));
	}


	@Test
	public void testCreateComplexAttributePragma(){
		res = AvatarPragma.createFromString("Secret B.m C.d", bdpragma, blocks, this.typeAttributesMap, this.nameTypeMap, errorAcc);
		assertEquals(res.size(),3);
	}

	@Test
	public void testCreateSecrecyAssumption(){
		res = AvatarPragma.createFromString("SecrecyAssumption A.key1 A.key2 B.key1 C.attr", bdpragma, blocks, typeAttributesMap, this.nameTypeMap, errorAcc);	
		assertEquals(res.size(),1);
		assertTrue((res.get(0) instanceof AvatarPragmaSecrecyAssumption));
		assertEquals(((AvatarPragmaSecrecyAssumption) res.get(0)).getArgs().size(),4);
	}
	
	@Test
	public void testInitialKnowledgeWrongType(){
		//Check composed type errors
		res = AvatarPragma.createFromString("InitialSystemKnowledge B.key1 C.m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
		res = AvatarPragma.createFromString("InitialSystemKnowledge B.d C.m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
	}

	@Test
	public void testInitalSystemKnowledgeCreation(){
		res = AvatarPragma.createFromString("InitialSystemKnowledge A.key1 A.key2 B.key1 C.attr", null,blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),1);
		assertTrue((res.get(0) instanceof AvatarPragmaInitialKnowledge));
		assertEquals(((AvatarPragmaInitialKnowledge) res.get(0)).getArgs().size(),4);
		assertTrue(((AvatarPragmaInitialKnowledge) res.get(0)).isSystem());
	}
	
	@Test
	public void testInitialSystemKnowledgeComplexAttributeMultipleCreation(){
		res = AvatarPragma.createFromString("InitialSystemKnowledge B.m C.m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),2);
	}


	@Test
	public void testInitialSystemKnowledgeDifferentTypeFail(){
		//Test for error on different base types
		res = AvatarPragma.createFromString("InitialSessionKnowledge A.key2 B.key2", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),0);
	}

	@Test
	public void testPublicPrivateKeyFailWrongArgument(){
		//Test PrivatePublicKey
		//Fail if wrong # of args
		res = AvatarPragma.createFromString("PrivatePublicKeys C.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
		res = AvatarPragma.createFromString("PrivatePublicKeys A.key1 A.key2 B.key1 C.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
	}

	@Test
	public void testSuccessfulPublicPrivateKeyCreation(){
		//Check no error
		res = AvatarPragma.createFromString("PrivatePublicKeys A key2 key1", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),1);
		//Check Type
		assertTrue(res.get(0) instanceof AvatarPragmaPrivatePublicKey);
		//Attributes
		AvatarPragmaPrivatePublicKey res4 = (AvatarPragmaPrivatePublicKey) res.get(0);
		assertEquals(res4.getPublicKey().getName(),"key1");
		assertEquals(res4.getPrivateKey().getName(),"key2");
		//Handle composed types
		res = AvatarPragma.createFromString("PrivatePublicKeys C attr d", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),1);
		//Fail if more than 1 field
		res = AvatarPragma.createFromString("PrivatePublicKeys C attr m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),0);
	}
	
	@Test
	public void testPublicPragmaCreation(){
		res = AvatarPragma.createFromString("Public A.key1 B.key2", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		//Check no error
		assertEquals(res.size(),1);
		//Check Type
		assertTrue(res.get(0) instanceof AvatarPragmaPublic);
		//1 Attribute
		assertEquals(((AvatarPragmaPublic) res.get(0)).getArgs().size(),2);
		assertEquals(((AvatarPragmaPublic) res.get(0)).getArgs().get(0).getName(),"key1");
		assertEquals(((AvatarPragmaPublic) res.get(0)).getArgs().get(1).getName(),"key2");
	}

	@Test
	public void testPragmaAuthenticityFailWrongArgs(){
		//Fail if wrong # of arguments
		res = AvatarPragma.createFromString("Authenticity A.key1 A.key2 B.key1 C.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
		res = AvatarPragma.createFromString("Authenticity C.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
	}

	@Test
	public void testPragmaAuthenticityFailMissingState(){
		//Fail if lack of state
		res = AvatarPragma.createFromString("Authenticity A.state.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),0);
	}
	
	@Test	
	public void testPragmaAuthenticityFailAttributeWrongType(){
		//Fail if attributes are not same type
		res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),0);
		res = AvatarPragma.createFromString("Authenticity B.b1.m C.c1.d", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),0);
		res = AvatarPragma.createFromString("Authenticity B.b1.key2 A.a1.key1", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);
		assertEquals(res.size(),0);	
	}
	
	@Test
	public void testPragmaAuthenticityCreation(){
		//Check no error
		res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.attr", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),1);
		//Check Type
		assertTrue(res.get(0) instanceof AvatarPragmaAuthenticity);
		//Check Attributes
		AvatarPragmaAuthenticity res3 = (AvatarPragmaAuthenticity) res.get(0);
		assertEquals(res3.getAttrA().getAttribute().getName(),"key1");
		assertEquals(res3.getAttrB().getAttribute().getName(),"attr");
		assertEquals(res3.getAttrA().getState().getName(),"a1");
		assertEquals(res3.getAttrB().getState().getName(),"c1");
	}
	
	@Test
	public void testPragmaAuthenticityMultipleCreation(){
		res = AvatarPragma.createFromString("Authenticity B.b1.m C.c1.m", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertEquals(res.size(),2);
		AvatarPragmaAuthenticity res3 = (AvatarPragmaAuthenticity) res.get(1);
		assertEquals(res3.getAttrA().getAttribute().getName(),"m__b");
		assertEquals(res3.getAttrB().getAttribute().getName(),"m__b");
		assertEquals(res3.getAttrA().getState().getName(),"b1");
		assertEquals(res3.getAttrB().getState().getName(),"c1");

		res3 = (AvatarPragmaAuthenticity) res.get(0);
		assertEquals(res3.getAttrA().getAttribute().getName(),"m__a");
		assertEquals(res3.getAttrB().getAttribute().getName(),"m__a");
		assertEquals(res3.getAttrA().getState().getName(),"b1");
		assertEquals(res3.getAttrB().getState().getName(),"c1");
	}

	@Test
	public void testPragmaConstant(){
		res = AvatarPragma.createFromString("PublicConstant 1 0 a b", bdpragma, blocks, typeAttributesMap, nameTypeMap, errorAcc);	
		assertTrue(res.get(0) instanceof AvatarPragmaConstant);
		AvatarPragmaConstant res5 = (AvatarPragmaConstant) res.get(0);
        assertEquals(res5.getConstants().size(),2);
		String[] attrs = new String[]{"a","b"};
		for (int i=0; i< res5.getConstants().size(); i++){
            assertEquals(res5.getConstants().get(i).getName(),attrs[i]);
		}
	}
	
	public void	test(){
	
    }

    public static void main(String[] args){
        AvatarPragmaTests apt = new AvatarPragmaTests ();
        //apt.runTest ();
    }
}

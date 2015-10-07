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
        B.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, B, null));
        B.addAttribute(new AvatarAttribute("key2", AvatarType.BOOLEAN, B, null));

        AvatarBlock C = new AvatarBlock("C", null, null);
        AvatarStateMachine Casm = C.getStateMachine();
        Casm.addElement(new AvatarState("c1", null));
        C.addAttribute(new AvatarAttribute("attr", AvatarType.INTEGER, C, null));


        //this.updateDigest(A.getStateMachine().getStateWithName("a1"));

        LinkedList<AvatarBlock> blocks = new LinkedList<AvatarBlock>();
        blocks.add(A);
        blocks.add(B);
        blocks.add(C);
        AvatarPragma res;

        //Test Bad keyword
        res = AvatarPragma.createFromString("FakePragma A.key1", null,blocks);
        this.updateDigest("Bad keyword: " + (res==null));

        //Handle whitespace
        res = AvatarPragma.createFromString("Public      A.key1", null,blocks);
        this.updateDigest("Whitespace parsing: " + (res!=null));
        res = AvatarPragma.createFromString("PrivatePublicKeys   A.key1     B.key2", null,blocks);
        this.updateDigest("Whitespace parsing: " + (res!=null));

        //Test missing block
        res = AvatarPragma.createFromString("Confidentiality non.arrrrg", null,blocks);
        this.updateDigest("Missing Block: " + (res==null));
        res = AvatarPragma.createFromString("Confidentiality A.key1 B.key2 C.attr non.arrrrg", null,blocks);
        this.updateDigest("Missing Block: " + (res==null));

        //Test badly formed attribute
        res = AvatarPragma.createFromString("Confidentiality attr", null,blocks);
        this.updateDigest("Attribute formatting: " + (res==null));
        res = AvatarPragma.createFromString("Public A.a1.attr", null,blocks);
        this.updateDigest("Attribute formatting: " + (res==null));

        //Test missing attribute
        res = AvatarPragma.createFromString(" Confidentiality            ", null,blocks);
        this.updateDigest("Missing Attribute: " + (res==null));
        res = AvatarPragma.createFromString("Confidentiality A.arrrrg", null,blocks);
        this.updateDigest("Missing Attribute: " + (res==null));
        res = AvatarPragma.createFromString("Confidentiality C.attr C.attr B.key1 A.arrrrg", null,blocks);
        this.updateDigest("Missing Attribute: " + (res==null));
        this.updateDigest("-------------------------------------");

        //Test Confidentiality
        this.updateDigest("Confidentiality Tests");
        res = AvatarPragma.createFromString("Confidentiality A.key1", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaSecret));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 1));
        this.updateDigest("Attr Name " + res.getArgs().get(0));
        this.updateDigest("-------------------------------------");

        //Test Secret
        this.updateDigest("Secret Tests");
        res = AvatarPragma.createFromString("Secret A.key1 A.key2", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaSecret));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 2));
        this.updateDigest(res.getArgs().get(0).toString ());
        this.updateDigest("Attr Name "+ (res.getArgs().get(0).equals("int key1")));
        this.updateDigest("Attr Name "+(res.getArgs().get(1).equals("int key2")));
        this.updateDigest("-------------------------------------");

        //Test Secrecy Assumption
        this.updateDigest("SecrecyAssumption Tests");
        res = AvatarPragma.createFromString("SecrecyAssumption A.key1 A.key2 B.key1 C.attr", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaSecrecyAssumption));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 4));
        this.updateDigest("-------------------------------------");

        //Test System Knowledge
        this.updateDigest("Initial System Knowledge Tests");
        res = AvatarPragma.createFromString("InitialSystemKnowledge A.key1 A.key2 B.key1 C.attr", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaInitialKnowledge));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 4));
        //Is system
        AvatarPragmaInitialKnowledge res2 = (AvatarPragmaInitialKnowledge) res;
        this.updateDigest("Is System: " + res2.isSystem());
        this.updateDigest("-------------------------------------");

        //Test System Knowledge
        this.updateDigest("Initial Session Knowledge Tests");
        res = AvatarPragma.createFromString("InitialSessionKnowledge A.key2 B.key1 C.attr", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaInitialKnowledge));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 3));
        //Is session
        res2 = (AvatarPragmaInitialKnowledge) res;
        this.updateDigest("Is Session: " + !res2.isSystem());
        this.updateDigest("-------------------------------------");

        //Test PrivatePublicKey
        this.updateDigest("PrivatePublicKeys Tests");
        //Fail if wrong # of args
        res = AvatarPragma.createFromString("PrivatePublicKeys C.attr", null,blocks);	
        this.updateDigest("PrivatePublicKeys args count " + (res==null));
        res = AvatarPragma.createFromString("PrivatePublicKeys A.key1 A.key2 B.key1 C.attr", null,blocks);	
        this.updateDigest("PrivatePublicKeys args count " + (res==null));
        //Check no error
        res = AvatarPragma.createFromString("PrivatePublicKeys A.key2 B.key1", null,blocks);	
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaPrivatePublicKey));
        //1 Attribute
        AvatarPragmaPrivatePublicKey res4 = (AvatarPragmaPrivatePublicKey) res;
        this.updateDigest("# of Attributes: " + (res4.getArgs().size() == 2));
        this.updateDigest("Attr Name "+ res4.getPublicKey());
        this.updateDigest("Attr Name "+ res4.getPrivateKey());
        this.updateDigest("-------------------------------------");

        //Test Public
        this.updateDigest("Public Tests");
        res = AvatarPragma.createFromString("Public A.key1 B.key2", null,blocks);	
        //Check no error
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaPublic));
        //1 Attribute
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 2));
        this.updateDigest("Attr Name " + res.getArgs().get(0));
        this.updateDigest("Attr Name " + res.getArgs().get(1));
        this.updateDigest("-------------------------------------");

        //Test Authenticity
        this.updateDigest("Authenticity Tests");
        //Fail if wrong # of args
        res = AvatarPragma.createFromString("Authenticity A.key1 A.key2 B.key1 C.attr", null,blocks);	
        this.updateDigest("Authenticity args count " + (res==null));
        res = AvatarPragma.createFromString("Authenticity C.attr", null,blocks);	
        this.updateDigest("Authenticity args count " + (res==null));
        //Fail if lack of state
        res = AvatarPragma.createFromString("Authenticity A.state.attr", null,blocks);	
        this.updateDigest("Missing State " + (res==null));
        //Check no error
        res = AvatarPragma.createFromString("Authenticity A.a1.key1 C.c1.attr", null,blocks);	
        this.updateDigest("No error: "+ (res !=null));
        //Check Type
        this.updateDigest("Right Type: " + (res instanceof AvatarPragmaAuthenticity));
        //1 Attribute
        AvatarPragmaAuthenticity res3 = (AvatarPragmaAuthenticity) res;
        this.updateDigest("# of Attributes: " + (res.getArgs().size() == 2));
        this.updateDigest("Attr "+ res3.getAttrA());
        this.updateDigest("Attr "+ res3.getAttrB());
        this.updateDigest("Attr Name "+ res3.getAttrA().getName());
        this.updateDigest("Attr Name "+ res3.getAttrB().getName());
        this.updateDigest("Attr State "+ res3.getAttrA().getState());
        this.updateDigest("Attr State "+ res3.getAttrB().getState());
        this.updateDigest("-------------------------------------");


        //Test Constants
        this.updateDigest("Constant Tests");
        res = AvatarPragma.createFromString("Constant 1 0 a b", null,blocks);	
        this.updateDigest("Right type :" + (res instanceof AvatarPragmaConstant));
        AvatarPragmaConstant res5 = (AvatarPragmaConstant) res;
        this.updateDigest("Right number of constants " + (res5.getConstants().size() == 4));
        for (int i=0; i< res5.getConstants().size(); i++){
            this.updateDigest("Constant " + res5.getConstants().get(i).getName());
        }
        this.updateDigest("-------------------------------------");


        //Avatar Specification Tests
        this.updateDigest("Tests finished");

        if (!this.testDigest (new byte[] {84, 40, -70, -41, -32, 102, 18, 125, -30, -120, -87, -7, 112, -25, 119, 106, 96, 18, -97, 41}))
            this.error ("Unexpected result when testing AvatarPragmas...");
    }

    public static void main(String[] args){
        AvatarPragmaTests apt = new AvatarPragmaTests ();
        apt.runTest ();
    }
}

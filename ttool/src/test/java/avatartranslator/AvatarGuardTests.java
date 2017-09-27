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
 * Class AvatarGuardTests
 * Creation: 20/05/2015
 * @version 1.1 01/07/2015
 * @author Ludovic APVRILLE, Letitia LI
 * @see
 */


import java.util.LinkedList;
import java.util.HashMap;
import java.util.Vector;

import ui.TAttribute;
import avatartranslator.*;

public class AvatarGuardTests {

    public AvatarGuardTests () {
      //  super ("AvatarGuards", false);
    }

    protected void test () {
        AvatarSpecification as = new AvatarSpecification("avatarspecification", null);
        AvatarBlock A = new AvatarBlock("A", as, null);
        AvatarStateMachine Aasm = A.getStateMachine();
        Aasm.addElement(new AvatarState("a1", null));
        A.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, A, null));
        A.addAttribute(new AvatarAttribute("key2", AvatarType.INTEGER, A, null));
        A.addAttribute(new AvatarAttribute("test", AvatarType.BOOLEAN, A, null));
        A.addAttribute(new AvatarAttribute("test2", AvatarType.BOOLEAN, A, null));

        AvatarBlock B = new AvatarBlock("B", as, null);
        AvatarStateMachine Basm = B.getStateMachine();
        Basm.addElement(new AvatarState("b1", null));
        B.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, B, null));
        B.addAttribute(new AvatarAttribute("key2", AvatarType.BOOLEAN, B, null));
        B.addAttribute(new AvatarAttribute("m__a", AvatarType.UNDEFINED, B, null));
        B.addAttribute(new AvatarAttribute("m__b", AvatarType.UNDEFINED, B, null));

        AvatarBlock C = new AvatarBlock("C", as, null);
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
        AvatarGuard res;

		/*
        //Else Guard
        res= AvatarGuard.createFromString(A, "else");
        this.updateDigest("Created else guard " +(res instanceof AvatarGuardElse));
        //Empty Guard
        res= AvatarGuard.createFromString(A, "");
        this.updateDigest("Created empty guard " +(res instanceof AvatarGuardEmpty));
        //Fail if not valid expression
        res= AvatarGuard.createFromString(A, "arg(key1==key2))");
        this.updateDigest("Fail if nonvalid expression " +(res instanceof AvatarGuardEmpty));
        res= AvatarGuard.createFromString(A, "key1=key2");
        this.updateDigest("Fail if nonvalid expression " +(res instanceof AvatarGuardEmpty));
        res= AvatarGuard.createFromString(A, "key1==)");
        this.updateDigest("Fail if nonvalid expression " +(res instanceof AvatarGuardEmpty));
        res= AvatarGuard.createFromString(A, "(key1==)))");
        this.updateDigest("Fail if nonvalid expression " +(res instanceof AvatarGuardEmpty));

        //Mono Guards
        this.updateDigest("Mono Guard Tests");
        res= AvatarGuard.createFromString(A, "test");
        this.updateDigest("Created mono guard " +(res instanceof AvatarSimpleGuardMono));

        //Duo Guards
        this.updateDigest("Duo Guard Tests");
        res= AvatarGuard.createFromString(A, "test==test2");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1==key2");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1 != key2");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1 != true");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1 != false");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1 != 1");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "key1 != a1234");
        this.updateDigest("Created duo guard " +(res instanceof AvatarSimpleGuardDuo));
        res= AvatarGuard.createFromString(A, "(a,b)==(c,d)");
        this.updateDigest("Created duo guard with tuple " +(res instanceof AvatarSimpleGuardDuo));

        //Unary Guards
        this.updateDigest("Unary Guard Tests");
        res= AvatarGuard.createFromString(A, "not(test)");
        this.updateDigest("Created unary guard " +(res instanceof AvatarUnaryGuard));
        res= AvatarGuard.createFromString(A, "not(test==test2)");
        this.updateDigest("Created unary guard " +(res instanceof AvatarUnaryGuard));
        res= AvatarGuard.createFromString(A, "not(key1==key2)");
        this.updateDigest("Created unary guard " +(res instanceof AvatarUnaryGuard));
        res= AvatarGuard.createFromString(B, "not(m__a==key2)");
        this.updateDigest("Created unary guard " +(res instanceof AvatarUnaryGuard));
        res= AvatarGuard.createFromString(B, "not(m__a==m__b)");
        this.updateDigest("Created unary guard " +(res instanceof AvatarUnaryGuard));

        //Binary Guards
        this.updateDigest("Binary Guard Tests");
        res= AvatarGuard.createFromString(A, "(key1==true) and (key2==false)");
        this.updateDigest("Binary guard with and " +(res instanceof AvatarBinaryGuard));
        res= AvatarGuard.createFromString(A, "(a) and (b)");
        this.updateDigest("Binary guard with tuple " +(res instanceof AvatarBinaryGuard));
        res= AvatarGuard.createFromString(A, "(key1==key1) or (key2==key1)");
        this.updateDigest("Binary guard with or " +(res instanceof AvatarBinaryGuard));
        res= AvatarGuard.createFromString(A, "((key1==key1) or (key2==key1)) and (m__a==m__b)");
        this.updateDigest("Binary guard with and/or " +(res instanceof AvatarBinaryGuard));
        this.updateDigest("-------------------------------------");

		*/
        //Avatar Specification Tests

 
    }
    public static void main(String[] args){
        AvatarGuardTests apt = new AvatarGuardTests ();
        //apt.runTest ();
    }
}

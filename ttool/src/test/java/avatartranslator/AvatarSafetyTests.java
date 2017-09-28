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
 * Class AvatarSafetyTests
 * Creation: 29/09/2017
 * @version 1.0 29/09/2017
 * @author Letitia LI
 */

//I am adding safety tests because Ludovic said to

import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import ui.TAttribute;
import avatartranslator.*;
import ui.*;
import ui.avatarbd.*;

import myutil.*;

public class AvatarSafetyTests {

	List<AvatarBDBlock> blocks;
	AvatarSpecification as;
	AvatarDesignPanelTranslator adpt;
	AvatarPragmaLatency pragma;

    public AvatarSafetyTests () {
       // super ("AvatarSafety", true);
    }
	@Before
    public void setupForTest () {

		//Test creation of Latency Pragma
		adpt = new AvatarDesignPanelTranslator(null);
		blocks = new ArrayList<AvatarBDBlock>();
		as = new AvatarSpecification("avspec",null);
		AvatarBlock A = new AvatarBlock("A", null, null);
		avatartranslator.AvatarSignal sig = new avatartranslator.AvatarSignal("sig", 0, null);
		A.addSignal(sig);
		AvatarStateMachine Aasm = A.getStateMachine();
		AvatarActionOnSignal aaos = new AvatarActionOnSignal("action_on_signal", sig, null);
		aaos.setCheckLatency(true);
		Aasm.addElement(aaos);

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

		as.addBlock(A);
		as.addBlock(B);
		as.addBlock(C);
	}

	@Test
	public void testFailIfLatencyPragmaEmpty(){
		pragma = adpt.checkLatencyPragma("", blocks, as, null);
		assertNull(pragma);
	}
	
	@Test
	public void testFailIfBadFormat(){
		//Fail if does not contain 'Latency()'
		pragma = adpt.checkLatencyPragma("Lat(b,s)<1", blocks, as, null);
		assertNull(pragma);
		//Fail if unmatched ')'
		pragma = adpt.checkLatencyPragma("Latency(b,s<1", blocks, as, null);
		assertNull(pragma);
	}

	@Test
	public void testFormLessThanPragma(){
		//Test : Form pragma with less than expression
		pragma = adpt.checkLatencyPragma("Latency(A.sig,A.sig)<1", blocks, as, null);
		assertTrue(pragma !=null);
	}


	@Test
	public void testFormGreaterThanPragma(){
		//Test : Form pragma with greater than expression
		pragma = adpt.checkLatencyPragma("Latency(A.sig,A.sig)>1", blocks, as, null);
		assertTrue(pragma !=null);
	}

    
    public static void main(String[] args){
        AvatarSafetyTests ast = new AvatarSafetyTests ();
       // ast.runTest ();
    }
}

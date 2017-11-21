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

package avatartranslator;
//I am adding safety tests because Ludovic said to

import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import ui.*;
import ui.avatarbd.*;

public class AvatarSafetyTests {

	List<AvatarBDBlock> blocks;
	AvatarSpecification as;
	AvatarDesignPanelTranslator adpt;
	AvatarPragmaLatency pragma;

	avatartranslator.AvatarSignal sig2;
	avatartranslator.AvatarSignal sig;
	

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

		sig = new avatartranslator.AvatarSignal("sig", 0, null);
		A.addSignal(sig);
		AvatarStateMachine Aasm = A.getStateMachine();
		AvatarActionOnSignal aaos = new AvatarActionOnSignal("action_on_signal", sig, null);
		aaos.setCheckLatency(true);
		Aasm.addElement(aaos);

		A.addAttribute(new AvatarAttribute("key1", AvatarType.INTEGER, A, null));
		A.addAttribute(new AvatarAttribute("key2", AvatarType.INTEGER, A, null));

		AvatarBlock B = new AvatarBlock("B", null, null);
		AvatarStateMachine Basm = B.getStateMachine();

		sig2 = new avatartranslator.AvatarSignal("sig2", 0, null);
		AvatarActionOnSignal aaos2= new AvatarActionOnSignal("action_on_signal", sig2, null);
		aaos2.setCheckLatency(true);
		Basm.addElement(aaos2);

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
		pragma = adpt.checkPerformancePragma("", blocks, as, null);
		assertNull(pragma);
	}
	
	@Test
	public void testFailIfBadFormat(){
		//Fail if does not contain 'Latency()'
		pragma = adpt.checkPerformancePragma("Lat(b,s)<1", blocks, as, null);
		assertNull(pragma);
		//Fail if unmatched ')'
		pragma = adpt.checkPerformancePragma("Latency(b,s<1", blocks, as, null);
		assertNull(pragma);
	}

	@Test
	public void testFormLessThanPragma(){
		//Test : Form pragma with less than expression
		pragma = adpt.checkPerformancePragma("Latency(A.sig,B.sig2)<1", blocks, as, null);
		assertTrue(pragma !=null);
		//Check Block names
		assertEquals(pragma.getBlock1().getName(),"A");
		assertEquals(pragma.getBlock2().getName(),"B");
		//Check State names
		assertEquals(pragma.getState1().getSignal().getName(),"sig");
		assertEquals(pragma.getState2().getSignal().getName(),"sig2");
		//Check ids not empty
		assertEquals(pragma.getId1().size(),1);
		assertEquals(pragma.getId2().size(),1);	
		//Check symbol
		assertEquals(pragma.getSymbolType(),AvatarPragmaLatency.lessThan);
		//Check time
		assertEquals(pragma.getTime(),1);
	}


	@Test
	public void testFormGreaterThanPragma(){
		//Test : Form pragma with greater than expression
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,A.sig)>231", blocks, as, null);
		assertTrue(pragma !=null);
		//Check symbol
		assertEquals(pragma.getSymbolType(),AvatarPragmaLatency.greaterThan);
		//Check time
		assertEquals(pragma.getTime(),231);
	}

	@Test
	public void testFailWrongNumberFormat(){
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,A.sig)>a231", blocks, as, null);
		assertNull(pragma);
	}

	@Test
	public void testFailMissingBlock(){
		pragma = adpt.checkPerformancePragma("Latency(Bob.sig2,A.sig)>231", blocks, as, null);
		assertNull(pragma);
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,Alice.sig)>231", blocks, as, null);
		assertNull(pragma);
	}

	@Test
	public void testFailMissingState(){
		pragma = adpt.checkPerformancePragma("Latency(B.state,A.sig)>231", blocks, as, null);
		assertNull(pragma);
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,A.sig2)>231", blocks, as, null);
		assertNull(pragma);
	}

	@Test
	public void testFormQueryPragma(){
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,A.sig)?", blocks, as, null);
		assertTrue(pragma!=null);
	}


	@Test
	public void testMultipleIdsPerSignal(){
		AvatarBlock A = as.getBlockWithName("A");
		AvatarStateMachine Aasm = A.getStateMachine();
		AvatarActionOnSignal aaos = new AvatarActionOnSignal("action_on_signal", sig, null);
		aaos.setCheckLatency(true);
		Aasm.addElement(aaos);
		Aasm.getListOfElements().get(0).addNext(aaos);

		//Form pragma
		pragma = adpt.checkPerformancePragma("Latency(B.sig2,A.sig)>231", blocks, as, null);

		assertEquals(pragma.getId1().size(),1);
		assertEquals(pragma.getId2().size(),2);	
	
	}
    
    public static void main(String[] args){
        AvatarSafetyTests ast = new AvatarSafetyTests ();
       // ast.runTest ();
    }
}

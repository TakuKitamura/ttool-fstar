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
 * Class AvatarSecurityTranslationTests
 * Creation: 1/10/2017
 * @version 1.1 01/10/2017
 * @author Letitia LI
 * @see
 */

package avatartranslator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import org.junit.Before;
import org.junit.Test;

import tmltranslator.SecurityPattern;
import tmltranslator.TMLArchitecture;
import tmltranslator.TMLExecC;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import tmltranslator.TMLStartState;
import tmltranslator.TMLTask;
import tmltranslator.toavatar.TML2Avatar;
import ui.MainGUI;
import ui.TGComponent;
import ui.TMLComponentDesignPanel;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;
import ui.tree.DiagramTreeRenderer;
import ui.tree.JDiagramTree;

public class AvatarSecurityTranslationTests {	
	TMLMapping<TGComponent> map;
	TMLTask task1;
	TMLStartState start;
	TMLModeling<TGComponent> tmlm;
	public AvatarSecurityTranslationTests () {
       //
    }
	
	@Before
	public void setupDiploMapping (){
		MainGUI mgui = new MainGUI(false, false, false, false, false, false, false, false, false, false, true, false, false);
		mgui.initActions();
        mgui.panelForTab = new JPanel();
		mgui.frame = new JFrame();
 		mgui.dtree = new JDiagramTree(mgui);
        mgui.dtree.setCellRenderer(new DiagramTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(mgui.dtree);
        /*JScrollPane scrollPane =*/ new JScrollPane(mgui.dtree);
		mgui.newTurtleModeling();
//		mgui.tabs = new Vector<TURTLEPanel>();
		TMLComponentDesignPanel tmlcdp = new TMLComponentDesignPanel(mgui);
		tmlcdp.tmlctdp =  new TMLComponentTaskDiagramPanel(mgui, null);
		tmlcdp.tmlctdp.tp = tmlcdp;

		tmlm = new TMLModeling<TGComponent>();

		task1= new TMLTask("DESIGN__task1", null, new TMLCPrimitiveComponent(0, 0, 0, 0, 0, 0, false, null, tmlcdp.tmlctdp));


		start = new TMLStartState("start",null);
		task1.getActivityDiagram().addElement(start);

		tmlm.addTask(task1);
		TMLArchitecture arch = new TMLArchitecture();
		map = new TMLMapping<TGComponent>(tmlm, arch, false);

		
		
	}
    //protected void test () {
	
	@Test
	public void testBlockTranslation(){
		TML2Avatar tml2avatar = new TML2Avatar(map,false,true);

		AvatarSpecification avspec =tml2avatar.generateAvatarSpec("1");
		assertTrue(avspec!=null);
		assertEquals(avspec.getListOfBlocks().size(),1);
	
	}


	@Test
	public void testKeyDistribution(){
		
	}	


	@Test
	public void testTranslateStatesSymmetricEncryption(){
		//setup security pattern
		SecurityPattern sec = new SecurityPattern("sym", "Symmetric Encryption", "100", "100", "100", "100", "", "", "");
		tmlm.addSecurityPattern(sec);

		//Setup states
		TMLExecC tmlexecc = new TMLExecC("encrypt_sym", null);
		tmlexecc.securityPattern = sec;
		start.addNext(tmlexecc);
        task1.getActivityDiagram().addElement(tmlexecc);


		TML2Avatar tml2avatar = new TML2Avatar(map,false,true);
		AvatarSpecification avspec =tml2avatar.generateAvatarSpec("1");
		assertTrue(avspec!=null);
		AvatarBlock block1 = avspec.getBlockWithName("task1");
		AvatarStateMachine sm =	block1.getStateMachine();
		List<AvatarStateMachineElement> elems=  sm.getListOfElements();
		//First state is start state
		assertTrue(elems.get(0) instanceof AvatarStartState);
		//state is avatartransition
		assertTrue(elems.get(1) instanceof AvatarTransition);
		//state is avatarstate
		assertTrue(elems.get(2) instanceof AvatarState);
		assertEquals(elems.get(2).getName(),"_encrypt_sym");
		//Next state is avatartransition
		assertTrue(elems.get(3) instanceof AvatarTransition);
		assertEquals(elems.get(3).getName(),"__after_encrypt_sym");
		AvatarTransition at = (AvatarTransition) elems.get(3);
		//Check that action is encryption
		assertEquals(at.getActions().get(0).getName().replaceAll(" ",""),"sym_encrypted=sencrypt(sym,key_sym)");
	}


	@Test
	public void testTranslateStatesAsymmetricEncryption(){
		//setup security pattern
		SecurityPattern sec = new SecurityPattern("asym", "Asymmetric Encryption", "100", "100", "100", "100", "", "", "");
		tmlm.addSecurityPattern(sec);

		//Setup states
		TMLExecC tmlexecc = new TMLExecC("encrypt_asym", null);
		tmlexecc.securityPattern = sec;
		start.addNext(tmlexecc);
        task1.getActivityDiagram().addElement(tmlexecc);


		TML2Avatar tml2avatar = new TML2Avatar(map,false,true);
		AvatarSpecification avspec =tml2avatar.generateAvatarSpec("1");
		assertTrue(avspec!=null);
		AvatarBlock block1 = avspec.getBlockWithName("task1");
		AvatarStateMachine sm =	block1.getStateMachine();
		List<AvatarStateMachineElement> elems=  sm.getListOfElements();
		
		AvatarTransition at = (AvatarTransition) elems.get(3);
		//Check that action is encryption
		assertEquals(at.getActions().get(0).getName().replaceAll(" ",""),"asym_encrypted=aencrypt(asym,pubKey_asym)");
	}


	@Test
	public void testTranslateNonce(){
		//setup security pattern
		SecurityPattern sec = new SecurityPattern("asym", "Nonce", "100", "100", "100", "100", "", "", "");
		tmlm.addSecurityPattern(sec);

		//Setup states
		TMLExecC tmlexecc = new TMLExecC("encrypt_asym", null);
		tmlexecc.securityPattern = sec;
		start.addNext(tmlexecc);
        task1.getActivityDiagram().addElement(tmlexecc);


		TML2Avatar tml2avatar = new TML2Avatar(map,false,true);
		AvatarSpecification avspec =tml2avatar.generateAvatarSpec("1");
		assertTrue(avspec!=null);
		AvatarBlock block1 = avspec.getBlockWithName("task1");
		AvatarStateMachine sm =	block1.getStateMachine();
		List<AvatarStateMachineElement> elems=  sm.getListOfElements();
		
		AvatarTransition at = (AvatarTransition) elems.get(3);
		//Check that action is empty
		assertEquals(at.getActions().size(),0);
		//Check that next state is random
		//assertTrue(elems.get(4) instanceof AvatarRandom);

	}



	@Test
	public void testTranslateMAC(){
		//setup security pattern
		SecurityPattern sec = new SecurityPattern("mac", "MAC", "100", "100", "100", "100", "", "", "");
		tmlm.addSecurityPattern(sec);

		//Setup states
		TMLExecC tmlexecc = new TMLExecC("encrypt_mac", null);
		tmlexecc.securityPattern = sec;
		start.addNext(tmlexecc);
        task1.getActivityDiagram().addElement(tmlexecc);


		TML2Avatar tml2avatar = new TML2Avatar(map,false,true);
		AvatarSpecification avspec =tml2avatar.generateAvatarSpec("1");
		assertTrue(avspec!=null);
		AvatarBlock block1 = avspec.getBlockWithName("task1");
		AvatarStateMachine sm =	block1.getStateMachine();
		List<AvatarStateMachineElement> elems=  sm.getListOfElements();
		
		AvatarTransition at = (AvatarTransition) elems.get(3);
		//Check that action is encryption
		assertEquals(at.getActions().get(0).getName().replaceAll(" ",""),"mac_mac=MAC(mac,key_mac)");
	}

    public static void main(String[] args){
        AvatarSecurityTranslationTests apt = new AvatarSecurityTranslationTests ();
        //apt.runTest ();
    }
}

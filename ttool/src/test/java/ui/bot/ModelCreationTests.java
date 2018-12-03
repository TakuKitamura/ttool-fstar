/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file gather all the tests on the creation of one and single model.
 * All the tests check the creation of the model, it development to it suppression 
 */

package ui.bot;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JMenuItemFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import myutil.TraceManager;

/*
 * Class ModelCreationTests
 * Creation: 19/11/2018
 * @version 1.0 19/11/2018
 * @author Arthur VUAGNIAUX
*/

public class ModelCreationTests extends AssertJSwingJUnitTestCase {
	private FrameFixture window;
	
	private UsefulTools ut;
    private boolean debug = true;
    
	@Test
	public void creationOfAFile() {
		/*
    	 * Description : Verify if TTool can create a new file, by clicking on the File menu then on
    	 * the tab New. Then save it.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() +
							"MainFrameTest: createANewFile: Started");
		JMenuItemFixture jmf = window.menuItem("File New");
		TraceManager.addDev("MainFrameTest: createANewFile: Creating a new file by clicking on New");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: createANewFile: ");
		TraceManager.addDev("MainFrameTest: createANewFile: File created");
	}
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main(false, false, false, false, false, false, false, false, false, false, false, false, false));
		ut = new UsefulTools();
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

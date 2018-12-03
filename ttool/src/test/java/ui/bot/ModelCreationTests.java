/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file gather all the tests on the creation of one and single model.
 * All the tests check the creation of the model, it development to it suppression 
 */

package ui.bot;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JFileChooserFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
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
							"ModelCreationTests: creationOfAFile: Started");
		JMenuItemFixture jmf = window.menuItem("File New");
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Creating a new file by clicking on New");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "ModelCreationTests: ccreationOfAFile: ");
		TraceManager.addDev("ModelCreationTests: creationOfAFile: File created");
		
		jmf = window.menuItem("File Save As Model");
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Saving the new model by clicking on Save As Model");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "ModelCreationTests: creationOfAFile: ");
		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Writting the path ");
		jfc.fileNameTextBox().pressAndReleaseKeys(ut.stringToKeyEvent("git/TTool/modeling/test"));
		TraceManager.addDev("ModelCreationTests: creationOfAFile: End writting");
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Clicking on the Approval Button");
		jfc.approveButton().click();
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Approve");
		
		TraceManager.addDev("ModelCreationTests: creationOfAFile: File saved");
		TraceManager.addDev("ModelCreationTests: creationOfAFile: Finished" + 
							System.lineSeparator() + "==============");
	}
	
	@Test
	public void openAFile() {
		/*
    	 * Description : Open the file that had been created.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() +
							"ModelCreationTests: openAFile: Started");
		
		JMenuItemFixture jmf = window.menuItem("File Model Project");
		TraceManager.addDev("ModelCreationTests: openAFile: Clicking on Open Model, in order to open test.xml");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "ModelCreationTests: openAFile: ");
		TraceManager.addDev("ModelCreationTests: openAFile: End Clicking");
		
		TraceManager.addDev("ModelCreationTests: openAFile: Try to open the file test.xml");
		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
		TraceManager.addDev("ModelCreationTests: openAFile: Writting the path ");
		jfc.fileNameTextBox().pressAndReleaseKeys(ut.stringToKeyEvent("git/TTool/modeling/test.xml"));
		TraceManager.addDev("ModelCreationTests: openAFile: End writting");
		TraceManager.addDev("ModelCreationTests: openAFile: Clicking on the Approval Button");
		jfc.approveButton().click();
		if (debug)
			ut.debugThread(3600, "ModelCreationTests: openAFile: ");
		TraceManager.addDev("ModelCreationTests: openAFile: Approve");
		TraceManager.addDev("ModelCreationTests: openAFile: End loading the file");
		
		TraceManager.addDev("ModelCreationTests: openAFile: Finished" + 
							System.lineSeparator() + "==============");
	}
	
	@Test
	public void diplodocusModel() {
		/*
    	 * Description : Open the file that had been created, and then use it
    	 * in order to create a diplodocus model and to do some things.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() +
							"ModelCreationTests: diplodocusModel: Started");
		TraceManager.addDev("ModelCreationTests: diplodocusModel: Openning the file");
		this.openAFile();
		TraceManager.addDev("ModelCreationTests: diplodocusModel: File open");
		
		TraceManager.addDev("ModelCreationTests: diplodocusModel: Richt clicking");
		window.rightClick();
		if (debug)
			ut.debugThread(3600, "ModelCreationTests: diplodocusModel: ");
		TraceManager.addDev("ModelCreationTests: diplodocusModel: End Clicking");
		
		TraceManager.addDev("ModelCreationTests: diplodocusModel: Clicking on New DIPLODOCUS Methodology");
		JMenuItemFixture jmf = window.menuItem("RC New TMLMethodology");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: diplodocusModel: ");
		TraceManager.addDev("ModelCreationTests: diplodocusModel: End Clicking");
		
		TraceManager.addDev("ModelCreationTests: diplodocusModel: Finished" + 
							System.lineSeparator() + "==============");
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

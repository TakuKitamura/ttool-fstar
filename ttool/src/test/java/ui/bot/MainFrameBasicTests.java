/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file is for some basic tests on the main frame
 */

package ui.bot;

import static org.assertj.swing.finder.WindowFinder.findFrame;

import java.awt.event.KeyEvent;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JFileChooserFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.fixture.JMenuItemFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import myutil.TraceManager;

/*
 * Class MainFrameBasicTests
 * Creation: 09/10/2018
 * @version 1.0 09/10/2018
 * @author Arthur VUAGNIAUX
*/

public class MainFrameBasicTests extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    private FrameFixture window2;
    
    private UsefulTools ut;
    private boolean debug = true;
    
    // Path : /git/TTool/modeling/
    private int[] project = {KeyEvent.VK_SLASH, KeyEvent.VK_G, KeyEvent.VK_I, KeyEvent.VK_T, KeyEvent.VK_SLASH, 
    		               KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_T, KeyEvent.VK_T, KeyEvent.VK_CAPS_LOCK, 
    		               KeyEvent.VK_O, KeyEvent.VK_O, KeyEvent.VK_L, KeyEvent.VK_SLASH, KeyEvent.VK_M, 
    		               KeyEvent.VK_O, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_L, KeyEvent.VK_I ,
    		               KeyEvent.VK_N, KeyEvent.VK_G, KeyEvent.VK_SLASH};
    
    // Path : git/TTool/modeling/my-elt-filter.xml
    private int[] model = {KeyEvent.VK_G, KeyEvent.VK_I, KeyEvent.VK_T, KeyEvent.VK_SLASH, 
            KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_T, KeyEvent.VK_T, KeyEvent.VK_CAPS_LOCK, 
            KeyEvent.VK_O, KeyEvent.VK_O, KeyEvent.VK_L, KeyEvent.VK_SLASH, KeyEvent.VK_M, 
            KeyEvent.VK_O, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_L, KeyEvent.VK_I ,
            KeyEvent.VK_N, KeyEvent.VK_G, KeyEvent.VK_SLASH, KeyEvent.VK_M, KeyEvent.VK_Y, 
            KeyEvent.VK_UNDERSCORE, KeyEvent.VK_E, KeyEvent.VK_L, KeyEvent.VK_N, KeyEvent.VK_UNDERSCORE, KeyEvent.VK_F, 
            KeyEvent.VK_I, KeyEvent.VK_L, KeyEvent.VK_T, KeyEvent.VK_E, KeyEvent.VK_R, 
            KeyEvent.VK_PERIOD, KeyEvent.VK_X, KeyEvent.VK_M, KeyEvent.VK_L};
    
//  @Test
//  public void capture() {
//  	JMenuItemFixture jmif = window.menuItem("Capture Screen");
//		jmif.click();
//  }

  
//  @Test
//	public void quit() {
//		JMenuItemFixture jmif = window.menuItem("File Quit");
//		jmif.click();
//	}
//	    
    
	@Test
	public void openProject() {
		/*
    	 * Description : Verify that TTool open the right project.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() + 
							"MainFrameTest: openProject: Started");
		JMenuItemFixture jmif = window.menuItem("File Open Project");	
		TraceManager.addDev("MainFrameTest: openProject: Clicking on the tab for opening a project");
		jmif.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openProject: ");
		TraceManager.addDev("MainFrameTest: openProject: Done clicking, the file chooser is now open");
		
		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
		TraceManager.addDev("MainFrameTest: openProject: Clicking on the file chooser, in order to write on it");
		jfc.fileNameTextBox().click();
		TraceManager.addDev("MainFrameTest: openProject: Done clicking");
		
		TraceManager.addDev("MainFrameTest: openProject: Writting the testing path");
		jfc.fileNameTextBox().pressAndReleaseKeys(project);	
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openProject: ");
		TraceManager.addDev("MainFrameTest: openProject: Done writting");
		
		TraceManager.addDev("MainFrameTest: openProject: Clicking on the approval button of the file chooser");
		jfc.approveButton().click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openProject: ");
		TraceManager.addDev("MainFrameTest: openProject: Approve");
		
//		TraceManager.addDev("MainFrameTest: openProject: Clicking on the tab for opening a project");
//		jmif.click();
//		try {
//			Thread.sleep(3600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}	
//		TraceManager.addDev("MainFrameTest: openProject: Done clicking, the file chooser is now open");
//		
//		TraceManager.addDev("MainFrameTest: openProject: Clicking on the file chooser, in order to write on it");
//		jfc.fileNameTextBox().click();
//		TraceManager.addDev("MainFrameTest: openProject: Done clicking");
//		try {
//			Thread.sleep(3600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}	
		TraceManager.addDev("MainFrameTest: openProject: Finished" + 
							System.lineSeparator() + "==============");
	}
	
    @Test
    public void help() {
    	/*
    	 * Description : Check the help from the main frame.
    	 */
    	TraceManager.addDev("==============" + System.lineSeparator() + 
    						"MainFrameTest: help: Started");
    	JMenuItemFixture jmif = window.menuItem("Help Configuration");
    	TraceManager.addDev("MainFrameTest: help: Opening the help by clicking on it");
    	jmif.click();
    	if (debug)
			ut.debugThread(3600, "MainFrameTest: help: ");
    	TraceManager.addDev("MainFrameTest: help: Done clicking");
    	
    	window2 = findFrame("TestTest").using(robot());
    	JButtonFixture jb = window2.button("Close Configuration");
    	TraceManager.addDev("MainFrameTest: help: Closing the help, by clicking on the button");
    	jb.click();
    	if (debug)
    		ut.debugThread(3600, "MainFrameTest: help: ");
    	TraceManager.addDev("MainFrameTest: help: Done closing");
    	TraceManager.addDev("MainFrameTest: help: Finished"+ 
							System.lineSeparator() + "==============");
    }
	
    @Test
    public void vandV() {
    	/*
    	 * Description : Check the V&V part
    	 */
    	TraceManager.addDev("==============" + System.lineSeparator() + 
    						"MainFrameTest: vandV: Started");
    	JMenuItemFixture jmf = window.menuItem("V&V Graph Modification");
    	TraceManager.addDev("MainFrameTest: vandV: Clicking on the tab specific of graph modification in V&V");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: vandV: ");
		TraceManager.addDev("MainFrameTest: vandV: Done clicking");
		TraceManager.addDev("MainFrameTest: vandV: Finished " + 
							System.lineSeparator() + "==============");
    }
    
    @Test
    public void openModel( ) {
    	/*
    	 * Description : Verify that TTool open the right model.
    	 * Note : KeyEvent.VK_UNDERSCORE make a KeyEvent.VK_MINUS, and that's a real problem
    	 */
    	TraceManager.addDev("==============" + System.lineSeparator() + 
    						"MainFrameTest: openModel: Started");
    	JMenuItemFixture jmif = window.menuItem("File Model Project");	
		TraceManager.addDev("MainFrameTest: openModel: Clicking on the tab for opening a model");
		jmif.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openModel: ");
		TraceManager.addDev("MainFrameTest: openModel: Done clicking, the file chooser is now open");
		
		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
		TraceManager.addDev("MainFrameTest: openModel: Writting the testing path");
		jfc.fileNameTextBox().pressAndReleaseKeys(model);	
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openModel: ");	
		TraceManager.addDev("MainFrameTest: openModel: Done writting");
		
		TraceManager.addDev("MainFrameTest: openModel: Clicking on the approval button of the file chooser");
		jfc.approveButton().click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openModel: ");
		TraceManager.addDev("MainFrameTest: openModel: Approve");		
		if (debug)
			ut.debugThread(3600, "MainFrameTest: openModel: ");
		TraceManager.addDev("MainFrameTest: openModel: Finished" + 
							System.lineSeparator() + "==============");
    }
    
	@Test
	public void createANewFile() {
		/*
    	 * Description : Verify if TTool can create a new file, by clicking on the File menu then on
    	 * the tab New. Then right click on it.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() +
							"MainFrameTest: createANewFile: Started");
		JMenuItemFixture jmf = window.menuItem("File New");
		TraceManager.addDev("MainFrameTest: createANewFile: Creating a new file by clicking on New");
		jmf.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: createANewFile: ");
		TraceManager.addDev("MainFrameTest: createANewFile: File created");
		
		TraceManager.addDev("MainFrameTest: createANewFile: Right clicking on the file");
		window.rightClick();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: createANewFile: ");
		TraceManager.addDev("MainFrameTest: createANewFile: Done clicking");
		TraceManager.addDev("MainFrameTest: createANewFile: Finished" + 
							System.lineSeparator() + "==============");
	}
	
	@Test
	public void saveDTA() {
		/*
    	 * Description : Check the save DTA part, by clicking on it.
    	 */
		TraceManager.addDev("==============" + System.lineSeparator() +
							"MainFrameTest: saveDTA: Started");
		JMenuItemFixture jmif = window.menuItem("File Save DTA");
		TraceManager.addDev("MainFrameTest: saveDTA: Clicking on the tab DTA");
		jmif.click();
		if (debug)
			ut.debugThread(3600, "MainFrameTest: saveDTA: ");
		TraceManager.addDev("MainFrameTest: saveDTA: Done clicking");
		TraceManager.addDev("MainFrameTest: saveDTA: Finished" + 
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

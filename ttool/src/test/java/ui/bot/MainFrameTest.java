/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 */

package ui.bot;

import static org.junit.Assert.*;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.FrameMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.JFileChooserFinder;
import org.assertj.swing.fixture.*;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.finder.JFileChooserFinder.findFileChooser;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import ui.util.IconManager;
import ui.window.JFrameBasicText;

/*
 * Class Main
 * Creation: 09/10/2018
 * @version 1.0 09/10/2018
 * @author Arthur VUAGNIAUX
*/

public class MainFrameTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    private FrameFixture window2;
    private int[] enter = {KeyEvent.VK_SLASH, KeyEvent.VK_G, KeyEvent.VK_I, KeyEvent.VK_T, KeyEvent.VK_SLASH, 
    		               KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_T, KeyEvent.VK_T, KeyEvent.VK_CAPS_LOCK, 
    		               KeyEvent.VK_O, KeyEvent.VK_O, KeyEvent.VK_L, KeyEvent.VK_SLASH, KeyEvent.VK_M, 
    		               KeyEvent.VK_O, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_L, KeyEvent.VK_I ,
    		               KeyEvent.VK_N, KeyEvent.VK_G, KeyEvent.VK_SLASH};
    
//	@Test
//	public void openProject() {
//		/*
//    	 * Description : Verify that TTool open right a project.
//    	 */
//		
//		JMenuItemFixture jmif = window.menuItem("File Open Project");	
//		TraceManager.addDev("MainFrameTest: openProject: Clicking on the tab for opening a project");
//		jmif.click();
//		try {
//			Thread.sleep(3600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		TraceManager.addDev("MainFrameTest: openProject: Done clicking, the file chooser is now open");
//		
//		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
//		TraceManager.addDev("MainFrameTest: openProject: Clicking on the file chooser, in order to write on it");
//		jfc.fileNameTextBox().click();
//		TraceManager.addDev("MainFrameTest: openProject: Done clicking");
//		
//		TraceManager.addDev("MainFrameTest: openProject: Writting the testing path");
//		jfc.fileNameTextBox().pressAndReleaseKeys(enter);	
//		try {
//			Thread.sleep(3600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}	
//		TraceManager.addDev("MainFrameTest: openProject: Done writting");
//		
//		TraceManager.addDev("MainFrameTest: openProject: Clicking on the approval button of the file chooser");
//		jfc.approveButton().click();
//		try {
//			Thread.sleep(3600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		TraceManager.addDev("MainFrameTest: openProject: Approve");
//		
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
//	}
	
    @Test
    public void help() {
    	/*
    	 * Description : Check the help from the main frame.
    	 */
    	
    	JMenuItemFixture jmif = window.menuItem("Help Configuration");
    	TraceManager.addDev("MainFrameTest: help: Opening the help by clicking on it");
    	jmif.click();
    	try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	TraceManager.addDev("MainFrameTest: help: Done clicking");
    	
    	window2 = findFrame("TestTest").using(robot());
    	JButtonFixture jb = window2.button("Close Configuration");
    	TraceManager.addDev("MainFrameTest: help: Closing the help, by clicking on the button");
    	jb.click();
    	try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	TraceManager.addDev("MainFrameTest: help: Done closing");
    }
	
//    @Test
//    public void capture() {
//    	JMenuItemFixture jmif = window.menuItem("Capture Screen");
//		jmif.click();
//    }
 
    
//    @Test
//	public void quit() {
//		JMenuItemFixture jmif = window.menuItem("File Quit");
//		jmif.click();
//	}
//	 
    @Test
    public void vandV() {
    	/*
    	 * Description : Check the V&V part
    	 */
    	
    	JMenuItemFixture jmf = window.menuItem("V&V Graph Modification");
    	TraceManager.addDev("MainFrameTest: vandV: Clicking on the tab specific of graph modification in V&V");
		jmf.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TraceManager.addDev("MainFrameTest: vandV: Done clicking");
    }
    
	@Test
	public void createANewFile() {
		/*
    	 * Description : Verify if TTool can create a new file, by clicking on the File menu then on
    	 * the tab New. Then right click on it.
    	 */
		
		JMenuItemFixture jmf = window.menuItem("File New");
		TraceManager.addDev("MainFrameTest: createANewFile: Creating a new file by clicking on New");
		jmf.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TraceManager.addDev("MainFrameTest: createANewFile: File created");
		
		TraceManager.addDev("MainFrameTest: createANewFile: Right clicking on the file");
		window.rightClick();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TraceManager.addDev("MainFrameTest: createANewFile: Done clicking");
	}
	
	@Test
	public void saveDTA() {
		/*
    	 * Description : Check the save DTA part, by clicking on it.
    	 */
		
		JMenuItemFixture jmif = window.menuItem("File Save DTA");
		TraceManager.addDev("MainFrameTest: saveDTA: Clicking on the tab DTA");
		jmif.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TraceManager.addDev("MainFrameTest: saveDTA: Done clicking");
	}
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main(false, false, false, false, false, false, false, false, false, false, false, false, false));
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

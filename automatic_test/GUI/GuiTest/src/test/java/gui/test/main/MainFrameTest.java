/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 */

package gui.test.main;

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
    
    @Test
    public void checkFrame() {
    	/*
    	 * Description : Check the title of the main frame, and verify that the frame is visible.
    	 */
    	
    	TraceManager.addDev("checkFrame: Checking title");
    	window.requireTitle("TTool");
    	TraceManager.addDev("checkFrame: Done checking title");
    	
    	TraceManager.addDev("checkFrame: Checking visibility");
    	window.requireVisible();
    	TraceManager.addDev("checkFrame: checking visibility");
    }
    
	@Test
	public void openProject() {
		/*
    	 * Description : Verify that TTool open right a project.
    	 */
		
		JMenuItemFixture jmif = window.menuItem("File Open Project");	
		jmif.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JFileChooserFixture jfc = JFileChooserFinder.findFileChooser().using(robot());
		jfc.fileNameTextBox().click();
		jfc.fileNameTextBox().pressAndReleaseKeys(enter);	
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		jfc.approveButton().click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		jmif.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		jfc.fileNameTextBox().click();
	}
	
    @Test
    public void help() {
    	/*
    	 * Description : Check the help from the main frame.
    	 */
    	
    	JMenuItemFixture jmif = window.menuItem("Help Configuration");
    	jmif.click();
    	try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	window2 = findFrame("TestTest").using(robot());
    	JButtonFixture jb = window2.button("Close Configuration");
    	jb.click();
    	try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	 
    @Test
    public void vandV() {
    	/*
    	 * Description : Check the V&V part
    	 */
    	
    	JMenuItemFixture jmf = window.menuItem("V&V Graph Modification");
		jmf.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
	@Test
	public void createANewFile() {
		/*
    	 * Description : Verify if TTool can create a new file, by clicking on the File menu then on
    	 * the tab New. Then right click on it.
    	 */
		
		JMenuItemFixture jmf = window.menuItem("File New");
		jmf.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		window.rightClick();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void saveDTA() {
		/*
    	 * Description : Check the save DTA part, by clicking on it.
    	 */
		
		JMenuItemFixture jmif = window.menuItem("File Save DTA");
		jmif.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main(true, true, true, true, true, true, true, true, true, true, true, true, true));
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

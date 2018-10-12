package gui.test.main;

import static org.junit.Assert.*;

import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
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
    
	@Test
	public void openProject() {
		JMenuItemFixture jmif = window.menuItem("File Open Project");
		//JFileChooserFixture jfc = window.fileChooser("Test JFC");
		jmif.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//jfc.approveButton().click();
	}
	
    @Test
    public void help() {
    	JMenuItemFixture jmif = window.menuItem("Help Configuration");
    	jmif.click();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	JButtonFixture jb = window2.button("Close Configuration");
    	jb.click();
    	try {
			Thread.sleep(1000);
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
	public void createANewFile() {
		JMenuItemFixture jmf = window.menuItem("File New");
		jmf.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		window.rightClick();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void saveDTA() {
		JMenuItemFixture jmif = window.menuItem("File Save DTA");
		jmif.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main(true, true, true, true, true, true, true, true, true, true, true, true, true));
		JFrameBasicText frame2 = GuiActionRunner.execute(()-> new JFrameBasicText("Your configuration of TTool ...",
	               "Default configuration:\n-----------------------\n" + ConfigurationTTool.getConfiguration(true)
                + "\nProject configuration:\n-----------------------\n" + SpecConfigTTool.getConfiguration(true),
        IconManager.imgic76));
		window2 = new FrameFixture(robot(), frame2);
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
		//window2.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

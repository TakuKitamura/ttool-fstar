package gui.test.main;

import static org.junit.Assert.*;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

/*
 * Class Main
 * Creation: 09/10/2018
 * @version 1.0 09/10/2018
 * @author Arthur VUAGNIAUX
*/

public class MainFrameTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
	
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
    }
	
    @Test
    public void capture() {
    	JMenuItemFixture jmif = window.menuItem("Capture Screen");
		jmif.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    @Test
	public void quit() {
		JMenuItemFixture jmif = window.menuItem("File Quit");
		jmif.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 
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
		window = new FrameFixture(robot(), frame.getFrame());
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

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

public class MainTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
	
	@Test
	public void test() {
		assertTrue(1 == 1);
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
	public void openProject() {
		JMenuItemFixture jmf = window.menuItem("File Open Project");
		//JFileChooserFixture jfc = window.fileChooser("jfc");
		jmf.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//jfc.approveButton().click();
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

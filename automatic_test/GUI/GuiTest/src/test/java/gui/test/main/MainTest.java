package gui.test.main;

import static org.junit.Assert.*;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

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
	public void testMainBar() {
		//JToolBarFixture jtb = window.toolBar("Main Bar");
		//JMenuItemFixture jmi = window.menuItem("TestItem");
		window.click();
		try {
			Thread.sleep(3600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		System.out.println(mouse.x + "," + mouse.y);
		//jtb.click();
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

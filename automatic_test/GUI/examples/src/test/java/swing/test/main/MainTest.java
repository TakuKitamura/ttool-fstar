/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 *
 * Test part of Main with AssertJ framework
 */

package swing.test.main;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

/*
   * Class MainTest
   * Creation: 05/10/2018
   * @version 1.0 05/10/2018
   * @author Arthur VUAGNIAUX
*/

public class MainTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
	private int[] keyboard = { KeyEvent.VK_S, KeyEvent.VK_O, KeyEvent.VK_G, KeyEvent.VK_E };
	private int[] keyboardother = { KeyEvent.VK_0, KeyEvent.VK_SEMICOLON, 
			                        KeyEvent.VK_BACK_SLASH, KeyEvent.VK_BACK_QUOTE };
	
	@Test
	public void test() {
		assertTrue(1 == 1);
	}
	
	@Test
	public void testArea() {
		JTextComponentFixture testBox = window.textBox("TestArea");
		testBox.pressAndReleaseKeys(keyboard);
	}
	
	@Test
	public void testAreaBis() {
		JTextComponentFixture testBox = window.textBox("TestArea");
		testBox.pressAndReleaseKeys(keyboardother);
	}
	
	@Test
	public void testButton1() {
		JButtonFixture testButton = window.button("Button1");
		testButton.click();
		testButton.doubleClick();
		testButton.click();
	}
	
	@Test
	public void testButtonRed() {
		JButtonFixture testButton = window.button("ButtonRed");
		testButton.background().requireEqualTo(Color.red);
		testButton.foreground().requireEqualTo(Color.black);
		testButton.requireVisible();
	}
	
	@Override
	protected void onSetUp() {
		Main frame = GuiActionRunner.execute(()-> new Main());
		window = new FrameFixture(robot(), frame);
		window.show(); // shows the frame to test
	}

	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

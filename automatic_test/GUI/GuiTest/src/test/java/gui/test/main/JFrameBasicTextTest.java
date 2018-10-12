package gui.test.main;

import static org.junit.Assert.*;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JInternalFrameFixture;
import org.assertj.swing.fixture.JMenuItemFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import ui.util.IconManager;
import ui.window.JFrameBasicText;

public class JFrameBasicTextTest extends AssertJSwingJUnitTestCase {
   private FrameFixture window;
   
	@Test
	public void test() {
		assertTrue(1 == 1); 
	}
	
	@Test
    public void help() {
    	JButtonFixture jb = window.button("Close Configuration");
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

	@Override
	protected void onSetUp() {
		JFrameBasicText frame = GuiActionRunner.execute(()-> new JFrameBasicText("Your configuration of TTool ...",
	               "Default configuration:\n-----------------------\n" + ConfigurationTTool.getConfiguration(true)
                   + "\nProject configuration:\n-----------------------\n" + SpecConfigTTool.getConfiguration(true),
           IconManager.imgic76));
		window = new FrameFixture(robot(), frame);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		super.onTearDown();
		window.cleanUp();
	}
}

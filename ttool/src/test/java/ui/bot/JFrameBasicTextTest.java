/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 */

package ui.bot;

import static org.junit.Assert.*;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.*;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import ui.util.IconManager;
import ui.window.JFrameBasicText;

/*
 * Class Main
 * Creation: 12/10/2018
 * @version 1.0 12/10/2018
 * @author Arthur VUAGNIAUX
*/

public class JFrameBasicTextTest extends AssertJSwingJUnitTestCase {
   private FrameFixture window;
   
	@Test
	public void checkFrame() {
		TraceManager.addDev("==============" + System.lineSeparator() + 
							"JFrameBasicTextTest: checkFrame: Started");
		TraceManager.addDev("JFrameBasicTextTest: checkFrame: Checking Frame");
    	window.requireVisible();
    	TraceManager.addDev("JFrameBasicTextTest: checkFrame: Check");
    	TraceManager.addDev("JFrameBasicTextTest: checkFrame: Finished" + 
							System.lineSeparator() + "==============");
	}
	
	@Test
    public void closeHelp() {
		TraceManager.addDev("==============" + System.lineSeparator() + 
							"JFrameBasicTextTest: closeHelp: Started");
		TraceManager.addDev("JFrameBasicTextTest: closeHelp: Opening Frame");
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	TraceManager.addDev("JFrameBasicTextTest: closeHelp: Clicking on the button Close");
    	JButtonFixture jb = window.button("Close Configuration");
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	TraceManager.addDev("JFrameBasicTextTest: closeHelp: Closing the Help");
    	TraceManager.addDev("JFrameBasicTextTest: closeHelp: Finished" + 
							System.lineSeparator() + "==============");
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

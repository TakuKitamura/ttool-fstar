/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 *
 * The purpose of this file is only to test all the project (all the xml files),
 * in order to open each, and to do simple tests 
 */

package ui.bot;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

/*
 * Class ProjectBasicTests
 * Creation: 19/11/2018
 * @version 1.0 19/11/2018
 * @author Arthur VUAGNIAUX
*/

public class ProjectBasicTests extends AssertJSwingJUnitTestCase {
	private FrameFixture window;
	
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

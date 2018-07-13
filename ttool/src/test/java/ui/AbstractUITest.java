package ui;

import static org.junit.Assert.fail;

import myutil.PluginManager;
import test.AbstractTest;
import ui.util.IconManager;

public abstract class AbstractUITest extends AbstractTest {
	
	protected final MainGUI mainGui;
	
	protected AbstractUITest() {
		IconManager.loadImg();
		mainGui = new MainGUI(false,false, false, false, false, false, false, false, false, false, true, false, false);
		mainGui.build();
		PluginManager.pluginManager = new PluginManager();
	}

	protected void handleException( final Throwable th ) {
        th.printStackTrace();
        fail( th.getLocalizedMessage() );
    }
}

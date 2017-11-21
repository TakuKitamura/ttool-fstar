package ui;

import static org.junit.Assert.fail;

import myutil.PluginManager;

public abstract class AbstractUITest {
	
	protected final MainGUI mainGui;
	
	protected AbstractUITest() {
		mainGui = new MainGUI(false, false, false, false, false, false, false, false, false, true, false, false);
		mainGui.build();
		PluginManager.pluginManager = new PluginManager();
	}

	protected void handleException( final Throwable th ) {
        th.printStackTrace();
        fail( th.getLocalizedMessage() );
    }
}

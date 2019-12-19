package ui;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import myutil.PluginManager;
import test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {




	
	protected MainGUI mainGUI = null;
	
	protected AbstractUITest() {
		
		// DB: Not needed for tests (causes NPE)
		//IconManager.loadImg();
        System.out.println("Creating main Window");
        mainGUI = new MainGUI(false, false, false, false, false, false, false, false, false, false, true, false, false);
        System.out.println("Main Window new done");
        mainGUI.build();
        System.out.println("Main Window build done");
        PluginManager.pluginManager = new PluginManager();
        System.out.println("Main Window created");
	}

	protected void openModel( final String fileName ) {
		final String fullFileName = fileName + XML_EXT;
		mainGUI.openProjectFromFile( new File( INPUT_DIR + fullFileName ) );
	}
    
	protected TMLArchiPanel findArchiPanel( final String name ) {
    	for ( final TMLArchiPanel panel : mainGUI.getTMLArchiDiagramPanels() ) {
    		if ( name.equals( mainGUI.getTitleAt( panel ) ) ) {
    			return panel;
    		}
    	}
    	
    	return null;
    }
	
	protected TDiagramPanel findDiagramPanel( 	final TDiagramPanel parentPanel,
												final String diagramName ) {
		for ( final TDiagramPanel subPanel : parentPanel.tp.getPanels() ) {
			if ( diagramName.equals( subPanel.getName() ) ) {
				return subPanel;
			}
		}
	
		return null;
	}
	
	protected TGComponent findDiagComponent( 	final int compoId,
												final TDiagramPanel diagPanel ) {
        return findDiagComponent( compoId, diagPanel.componentList );
	}
	
	protected TGComponent findDiagComponent( 	final int compoId,
												final Collection<TGComponent> components ) {
        for ( final TGComponent compo : components ) {
        	if ( compoId == compo.getId() ) {
        		return compo;
        	}
        	
        	final TGComponent subCompo = findDiagComponent( compoId, Arrays.asList( compo.tgcomponent ) );
        	
        	if ( subCompo != null ) {
        		return subCompo;
        	}
        }
        
        return null;
	}

	protected void monitorError(Process proc) {
        BufferedReader proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        new Thread() {
            @Override public void run() {
                String line;
                try {
                    while ((line = proc_err.readLine()) != null) {
                        System.out.println("NOC executing err: " + line);
                    }
                } catch (Exception e) {
                    //System.out.println("FAILED reading errors");
                    return;
                }

            }
        }.start();
	}
}

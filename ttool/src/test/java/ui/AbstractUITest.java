package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import myutil.PluginManager;
import test.AbstractTest;

public abstract class AbstractUITest extends AbstractTest {
	
	protected final MainGUI mainGUI;
	
	protected AbstractUITest() {
		
		// DB: Not needed for tests (causes NPE)
		//IconManager.loadImg();
		mainGUI = new MainGUI(false,false, false, false, false, false, false, false, false, false, true, false, false);
		mainGUI.build();
		PluginManager.pluginManager = new PluginManager();
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
}

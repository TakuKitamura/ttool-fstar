package ui;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import org.junit.Test;

import fr.tpt.mem4csd.utils.compare.IComparator;
import fr.tpt.mem4csd.utils.compare.IComparisonReport;
import fr.tpt.mem4csd.utils.compare.text.TextComparator;
import myutil.FileUtils;

public class TestGTURTLEModeling extends AbstractUITest {
	
	private static final String RESOURCES_DIR = "resources/ui/generateccode/";
	
	private final IComparator textComparator;
	
	public TestGTURTLEModeling() {
		super();
		
		mainGui.openProjectFromFile( new File( RESOURCES_DIR + "models/ZigBeeTutorial.xml") );
		
		// FIXME: Temporarily ignoring the debug text file due to bug in comparator tool
		textComparator = new TextComparator( Collections.singletonList( "txt" ) );
	}
	
	private void testGenerateCCode( final String mappingDiagName ) {
    	final TMLArchiPanel panel = findPanel( mappingDiagName );
    	final Vector<TGComponent> compos = new Vector<TGComponent>( panel.tmlap.getComponentList() );
    	
    	assertTrue( mainGui.gtm.checkSyntaxTMLMapping( compos, panel, true ) );
    	
    	final String codeDir = RESOURCES_DIR + mappingDiagName + File.separator + "actual" + File.separator;
    	FileUtils.deleteFiles( codeDir );
    	
    	final String codeDirExpected = RESOURCES_DIR + mappingDiagName + File.separator + "expected" + File.separator;
    	
    	mainGui.gtm.generateCCode( codeDir );
    	
    	try {
			final IComparisonReport difference = textComparator.compare( new File( codeDir ), new File( codeDirExpected ) );
			
			if ( difference.containsDiff() ) {
				difference.print();
				
				fail( "Generated code files are not the same!!!" );
			}
			else {
		    	FileUtils.deleteFiles( codeDir );
			}
		} 
    	catch ( final IOException ex ) {
			handleException( ex );
		}
	}
    
    private TMLArchiPanel findPanel( final String name ) {
    	for ( final TMLArchiPanel panel : mainGui.getTMLArchiDiagramPanels() ) {
    		if ( name.equals( mainGui.getTitleAt( panel ) ) ) {
    			return panel;
    		}
    	}
    	
    	return null;
    }

    @Test
    public void testGenerateCCodeMapping0() {
    	testGenerateCCode( "Mapping_0" );
	}

    @Test
    public void testGenerateCCodeMapping1() {
    	testGenerateCCode( "Mapping_1" );
	}

    @Test
    public void testGenerateCCodeMapping2() {
    	testGenerateCCode( "Mapping_2" );
	}
}

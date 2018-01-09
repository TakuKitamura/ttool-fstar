package test;

public abstract class AbstractTest {
	
	protected static String RESOURCES_DIR = "";

	protected static String getBaseResourcesDir() {
    	final String systemPropResDir = System.getProperty( "resources_dir" );
    	
    	if ( systemPropResDir == null ) {
    		return "resources/test/";
    	}

   		return systemPropResDir;
	}
}

package tmltranslator.tomappingsystemc2;

import myutil.FileException;

public interface IDiploSimulatorCodeGenerator {

	MappedSystemCTask getMappedTaskByName( String iName );
	
    void generateSystemC(	boolean _debug, 
    						boolean _optimize );

    void saveFile(	String path,
    				String filename )
    throws FileException;
}

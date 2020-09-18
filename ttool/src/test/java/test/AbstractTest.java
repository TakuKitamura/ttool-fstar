package test;

import static org.junit.Assert.fail;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import myutil.FileException;
import myutil.FileUtils;

public abstract class AbstractTest {
	
	protected static String RESOURCES_DIR = "";
	protected static String INPUT_DIR;
	protected static String EXPECTED_CODE_DIR;
	protected static String ACTUAL_CODE_DIR;
	protected static final String TXT_EXT = ".txt";
	protected static final String XML_EXT = ".xml";

	protected static String getBaseResourcesDir() {
    	final String systemPropResDir = System.getProperty( "resources_dir" );
    	
    	if ( systemPropResDir == null ) {
    		return "resources/test/";
    	}

   		return systemPropResDir;
	}
	
	protected void checkResult( final String actualCode,
								final String fileName ) {
		try {
			final String expectedCode = FileUtils.loadFile( EXPECTED_CODE_DIR + fileName + TXT_EXT );

			if ( !expectedCode.equals( actualCode ) ) {
				saveActualResults( fileName + TXT_EXT, actualCode );
			}
		}
		catch ( FileException ex ) {
			handleException( ex );
		}
	}
	
	protected void checkResultXml( 	final String actualCode,
									final String fileName ) {

		System.out.println("Comparing with " + actualCode.substring(0, 30) + " with file: " + fileName);

		// Since this function fails because tasks are not always in the same order, it is deactivated



		try {
			final String expectedCode = FileUtils.loadFile( EXPECTED_CODE_DIR + fileName + XML_EXT );

			//FileUtils.saveFile(EXPECTED_CODE_DIR + fileName + XML_EXT, actualCode);
			
			if ( !compareXml( actualCode, expectedCode ) ) {
				saveActualResults( fileName + XML_EXT, actualCode );
			}
		}
		catch ( ParserConfigurationException | SAXException | IOException | FileException ex ) {
			handleException( ex );
		}
	}
	
	private void saveActualResults( final String fileName,
									final String actualCode ) {
		final String filePath = ACTUAL_CODE_DIR + fileName;
		final File fileToSave = new File( filePath );
		final File dir = fileToSave.getParentFile();
		
		if (!dir.exists() ) {
			dir.mkdirs();
		}

		try {
			FileUtils.saveFile( filePath, actualCode );
			fail( "Differences were found between actual and expected code!!" );
		}
		catch( final FileException ex ) {
			handleException( ex );
		}
	}
	
	protected boolean compareXml( 	final String result,
									final String expected ) 
	throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware( true );
		documentFactory.setCoalescing( true );
		documentFactory.setIgnoringElementContentWhitespace( true );
		documentFactory.setIgnoringComments( true );
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		
		final Document doc1 = documentBuilder.parse( new ByteArrayInputStream( result.getBytes() ) );
		doc1.normalizeDocument();

		final Document doc2 = documentBuilder.parse( new ByteArrayInputStream( expected.getBytes() ) );
		doc2.normalizeDocument();
		
		return doc1.isEqualNode( doc2 );
	}

	protected void handleException( final Throwable th ) {
        th.printStackTrace();
        fail( th.getLocalizedMessage() );
    }
}

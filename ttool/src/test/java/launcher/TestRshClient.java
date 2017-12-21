package launcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import launcher.LauncherException;
import launcher.RshClient;
import launcher.RshServer;
import myutil.FileException;
import myutil.FileUtils;

public class TestRshClient {

    private static final String EXPECTED_COMMAND_OUTPUT = "!!!Hello World!!!" + System.lineSeparator();
    private static final String TEST_PROGRAM_NAME = "helloWorld";
    private static final String TEST_COMMAND = "./resources/launcher/" + TEST_PROGRAM_NAME;
    private static final String TEST_COMMAND_NON_STOP = "./resources/launcher/helloWorldNonStop";
    private static final String TEST_FILE_NAME = "./resources/launcher/test.txt";
    private static final String TEST_FILE_DATA = "testDatafhkenomrcg ,jgh o";


    private RshClient client = null;
    private static Thread serverThread = null;

    @BeforeClass
    public static void setUpBeforeClass()
        throws Exception {
        RshClient.PORT_NUMBER = 8080;
        RshServer.PORT_NUMBER = RshClient.PORT_NUMBER;

        final Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    new RshServer( null ).startServer();
                }
            };

        serverThread = new Thread( runnable );
        serverThread.start();
        Thread.sleep( 500 );
    }

    @Before
    public void setUp()
        throws Exception {
        client = new RshClient( "localhost" );
    }

    @After
    public void tearDown()
        throws Exception {
        client = null;
    }

    @AfterClass
    public static void tearDownAfterClass()
        throws Exception {
        serverThread.interrupt();
    }

    private void handleException( final Throwable th ) {
        th.printStackTrace();
        fail( th.getLocalizedMessage() );
    }

    @Test
    public void testStopCommand() {
        final Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        client.setCmd( TEST_COMMAND_NON_STOP );
                        client.sendExecuteCommandRequest();
                        final Writer writer = new StringWriter();
                        client.writeCommandMessages( writer );
                    }
                    catch (LauncherException e) {
                        handleException( e );
                    }
                }
            };

        final Thread thread = new Thread( runnable );
        thread.start();

        try {

            // Ensure the remote process has enough time to start
            Thread.sleep( 200 );
            client.stopCommand();

            // Ensure the stop command has been processed on the server
            Thread.sleep( 200 );
            assertTrue( killUnexistentProcess() );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
        catch ( InterruptedException ex ) {
            handleException( ex );
        }
    }

    private boolean killUnexistentProcess() {
        try {
            client.sendKillProcessRequest();

            return false;
        }
        catch ( LauncherException ex ) {
            return RshClient.FAILED.equals( ex.getMessage() );
        }
    }

    @Test
    public void testGetId() {
        try {
            final int id = client.getId();
            assertTrue( id == 1 );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testFreeId() {
        try {
            client.freeId( 1 );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testSendExecuteCommandRequest() {
        client.setCmd( TEST_COMMAND );

        try {
            client.sendExecuteCommandRequest();
            final Writer writer = new StringWriter();
            client.writeCommandMessages( writer );
            assertTrue( ( EXPECTED_COMMAND_OUTPUT + System.lineSeparator() ).equals( writer.toString() ) );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testSendExecuteCommandRequestBoolean() {
        client.setCmd( TEST_COMMAND );

        try {
            client.sendExecuteCommandRequest( true );
            final Writer writer = new StringWriter();
            client.writeCommandMessages( writer );
            assertTrue( writer.toString().startsWith( EXPECTED_COMMAND_OUTPUT ) );

            final Integer retCode = client.getProcessReturnCode();
            assertTrue( retCode != null && retCode == 0  );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testSendExecutePipedCommandsRequest() {
        final String testFileName = "./resources/test_piped_commands.txt";
        final String expectedData = "Test Passed!" + System.lineSeparator();

        try {
            FileUtils.saveFile( testFileName, expectedData );
            client.sendExecutePipedCommandsRequest( "echo " + testFileName, "xargs cat" );
            final String data = client.getDataFromProcess();

            assertTrue( "Piped commands returned " + data, expectedData.equals( data ) );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
        catch ( FileException ex ) {
            handleException( ex );
        }
        finally {
            new File( testFileName ).delete();
        }
    }

    private boolean deleteTestFile() {
        final File testFile = new File( TEST_FILE_NAME );

        if ( testFile.exists() ) {
            assertTrue(  "Test file could not be deleted!", testFile.delete() );
        }

        return true;
    }

    @Test
    public void testSendFileData() {
        deleteTestFile();

        try {
            client.sendFileData( TEST_FILE_NAME, TEST_FILE_DATA );

            try {
                final String readData = FileUtils.loadFile( TEST_FILE_NAME );

                assertTrue( ( TEST_FILE_DATA + System.lineSeparator() ).equals( readData ) );
            }
            catch ( FileException ex ) {
                handleException( ex );
            }
        }
        catch( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testGetFileData() {
        deleteTestFile();

        try {
            FileUtils.saveFile( TEST_FILE_NAME, TEST_FILE_DATA );

            final String readData = client.getFileData( TEST_FILE_NAME );

            assertTrue( TEST_FILE_DATA.equals( readData ) );
        }
        catch ( FileException ex ) {
            handleException( ex );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testDeleteFile() {
        deleteTestFile();

        try {
            FileUtils.saveFile( TEST_FILE_NAME, TEST_FILE_DATA );

            client.deleteFile( TEST_FILE_NAME );

            assertFalse( new File( TEST_FILE_NAME ).exists() );
        }
        catch ( FileException ex ) {
            handleException( ex );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testSendKillProcessRequest() {
        client.setCmd( TEST_COMMAND_NON_STOP );

        try {
            client.sendExecuteCommandRequest();
            Thread.sleep( 200 );
            client.sendKillProcessRequest();

            Thread.sleep( 200 );
            assertTrue( killUnexistentProcess() );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
        catch ( InterruptedException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testSendKillAllProcessRequest() {
        client.setCmd( TEST_COMMAND_NON_STOP );

        try {
            for ( int index = 0; index < 4; index++ ) {
                client.sendExecuteCommandRequest();
                Thread.sleep( 200 );
            }

            client.sendKillAllProcessRequest();

            Thread.sleep( 200 );
            assertTrue( killUnexistentProcess() );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
        catch ( InterruptedException ex ) {
            handleException( ex );
        }
    }

    @Test
    public void testGetDataFromProcess() {
        client.setCmd( TEST_COMMAND );

        try {
            client.sendExecuteCommandRequest();
            final String messageFromProcess = client.getDataFromProcess();

            assertTrue( ( EXPECTED_COMMAND_OUTPUT ).equals( messageFromProcess ) );
        }
        catch ( LauncherException ex ) {
            handleException( ex );
        }
    }
}

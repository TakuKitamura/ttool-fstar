/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

        ludovic.apvrille AT enst.fr

        This software is a computer program whose purpose is to allow the
        edition of TURTLE analysis, design and deployment diagrams, to
        allow the generation of RT-LOTOS or Java code from this diagram,
        and at last to allow the analysis of formal validation traces
        obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
        from INRIA Rhone-Alpes.

        This software is governed by the CeCILL  license under French law and
        abiding by the rules of distribution of free software.  You can  use,
        modify and/ or redistribute the software under the terms of the CeCILL
        license as circulated by CEA, CNRS and INRIA at the following URL
        "http://www.cecill.info".

        As a counterpart to the access to the source code and  rights to copy,
        modify and redistribute granted by the license, users are provided only
        with a limited warranty  and the software's author,  the holder of the
        economic rights,  and the successive licensors  have only  limited
        liability.

        In this respect, the user's attention is drawn to the risks associated
        with loading,  using,  modifying and/or developing or reproducing the
        software by the user in light of its specific status of free software,
        that may mean  that it is complicated to manipulate,  and  that  also
        therefore means  that it is reserved for developers  and  experienced
        professionals having in-depth computer knowledge. Users are therefore
        encouraged to load and test the software's suitability as regards their
        requirements in conditions enabling the security of their systems and/or
        data to be ensured and,  more generally, to use and operate it in the
        same conditions as regards security.

        The fact that you are presently reading this means that you have had
        knowledge of the CeCILL license and that you accept its terms.
        */

package launcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import myutil.FileException;
import myutil.FileUtils;
import test.AbstractTest;

/**
 * Class TestRshclient For testing remote client Creation: 01/08/2018
 *
 * @author Dominique BLOUIN, Ludovic APVRILLE
 * @version 1.0 01/08/2018
 */
public class TestRshClient extends AbstractTest {

    private static final String EXPECTED_COMMAND_OUTPUT = "!!!Hello World!!!" + System.lineSeparator();
    private static final String TEST_FILE_DATA = "testDatafhkenomrcg ,jgh o";
    private static String TEST_COMMAND;
    private static String TEST_COMMAND_NON_STOP;
    private static String TEST_FILE_NAME;

    private static Thread SERVER_THREAD;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "launcher/";
        TEST_COMMAND = "./" + RESOURCES_DIR + "helloWorld";
        TEST_COMMAND_NON_STOP = "./" + RESOURCES_DIR + "helloWorldNonStop";
        TEST_FILE_NAME = RESOURCES_DIR + "test.txt";

        RshClient.PORT_NUMBER = 8080;
        RshServer.PORT_NUMBER = RshClient.PORT_NUMBER;

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                new RshServer(null).startServer();
            }
        };

        SERVER_THREAD = new Thread(runnable);
        SERVER_THREAD.start();
        Thread.sleep(500);
    }

    private RshClient client = null;

    @Before
    public void setUp() throws Exception {
        client = new RshClient("localhost");
    }

    @After
    public void tearDown() throws Exception {
        client = null;
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        SERVER_THREAD.interrupt();
    }
    //
    // private void handleException( final Throwable th ) {
    // th.printStackTrace();
    // fail( th.getLocalizedMessage() );
    // }

    /*
     * @Test public void testStopCommand() { final Runnable runnable = new
     * Runnable() {
     * 
     * @Override public void run() { try {
     * System.out.println("starting thread for LauncherException:"); client.setCmd(
     * TEST_COMMAND_NON_STOP ); client.sendExecuteCommandRequest(); final Writer
     * writer = new StringWriter(); client.writeCommandMessages( writer ); } catch
     * (LauncherException e) { System.out.println("LauncherException:" +
     * e.getMessage()); handleException( e ); } } };
     * 
     * final Thread thread = new Thread( runnable );
     * System.out.println("Going to start thread for LauncherException:");
     * thread.start();
     * 
     * try {
     * 
     * // Ensure the remote process has enough time to start Thread.sleep( 2000 );
     * client.stopCommand();
     * 
     * // Ensure the stop command has been processed on the server Thread.sleep(
     * 2000 ); assertTrue( killUnexistentProcess() ); } catch ( LauncherException ex
     * ) { System.out.println("LauncherException:" + ex.getMessage());
     * handleException( ex ); } catch ( InterruptedException ex ) {
     * System.out.println("InterruptedException:" + ex.getMessage());
     * handleException( ex ); } }
     */

    /*
     * public boolean killUnexistentProcess() { try {
     * client.sendKillProcessRequest(); return false; } catch ( LauncherException ex
     * ) { return RshClient.FAILED.equals( ex.getMessage() ); } }
     */

    /*
     * @Test public void testGetId() { try { final int id = client.getId();
     * assertTrue( id == 1 ); } catch ( LauncherException ex ) { handleException( ex
     * ); } }
     * 
     * @Test public void testFreeId() { try { client.freeId( 1 ); } catch (
     * LauncherException ex ) { handleException( ex ); } }
     * 
     * @Test public void testSendExecuteCommandRequest() { client.setCmd(
     * TEST_COMMAND );
     * 
     * try { client.sendExecuteCommandRequest(); final Writer writer = new
     * StringWriter(); client.writeCommandMessages( writer ); assertTrue( (
     * EXPECTED_COMMAND_OUTPUT + System.lineSeparator() ).equals( writer.toString()
     * ) ); } catch ( LauncherException ex ) { handleException( ex ); } }
     * 
     * @Test public void testSendExecuteCommandRequestBoolean() { client.setCmd(
     * TEST_COMMAND );
     * 
     * try { client.sendExecuteCommandRequest( true ); final Writer writer = new
     * StringWriter(); client.writeCommandMessages( writer ); assertTrue(
     * writer.toString().startsWith( EXPECTED_COMMAND_OUTPUT ) );
     * 
     * final Integer retCode = client.getProcessReturnCode(); assertTrue( retCode !=
     * null && retCode == 0 ); } catch ( LauncherException ex ) { handleException(
     * ex ); } }
     * 
     * @Test public void testSendExecutePipedCommandsRequest() { final String
     * testFileName = "./" + RESOURCES_DIR + "test_piped_commands.txt"; final String
     * expectedData = "Test Passed!" + System.lineSeparator();
     * 
     * try { FileUtils.saveFile( testFileName, expectedData );
     * client.sendExecutePipedCommandsRequest( "echo " + testFileName, "xargs cat"
     * ); final String data = client.getDataFromProcess();
     * 
     * assertTrue( "Piped commands returned " + data, expectedData.equals( data ) );
     * } catch ( LauncherException ex ) { handleException( ex ); } catch (
     * FileException ex ) { handleException( ex ); } finally { new File(
     * testFileName ).delete(); } }
     */

    private boolean deleteTestFile() {
        final File testFile = new File(TEST_FILE_NAME);

        if (testFile.exists()) {
            assertTrue("Test file could not be deleted!", testFile.delete());
        }

        return true;
    }

    @Test
    public void testSendFileData() {
        deleteTestFile();

        try {
            client.sendFileData(TEST_FILE_NAME, TEST_FILE_DATA);

            try {
                final String readData = FileUtils.loadFile(TEST_FILE_NAME);

                assertTrue((TEST_FILE_DATA + System.lineSeparator()).equals(readData));
            } catch (FileException ex) {
                handleException(ex);
            }
        } catch (LauncherException ex) {
            handleException(ex);
        }
    }

    @Test
    public void testGetFileData() {
        deleteTestFile();

        try {
            FileUtils.saveFile(TEST_FILE_NAME, TEST_FILE_DATA);

            final String readData = client.getFileData(TEST_FILE_NAME);

            assertTrue(TEST_FILE_DATA.equals(readData));
        } catch (FileException ex) {
            handleException(ex);
        } catch (LauncherException ex) {
            handleException(ex);
        }
    }

    @Test
    public void testDeleteFile() {
        deleteTestFile();

        try {
            FileUtils.saveFile(TEST_FILE_NAME, TEST_FILE_DATA);

            client.deleteFile(TEST_FILE_NAME);

            assertFalse(new File(TEST_FILE_NAME).exists());
        } catch (FileException ex) {
            handleException(ex);
        } catch (LauncherException ex) {
            handleException(ex);
        }
    }

    /*
     * @Test public void testSendKillProcessRequest() { client.setCmd(
     * TEST_COMMAND_NON_STOP );
     * 
     * try { client.sendExecuteCommandRequest(); Thread.sleep( 200 );
     * client.sendKillProcessRequest();
     * 
     * Thread.sleep( 200 ); assertTrue( killUnexistentProcess() ); } catch (
     * LauncherException ex ) { handleException( ex ); } catch (
     * InterruptedException ex ) { handleException( ex ); } }
     */

    /*
     * @Test public void testSendKillAllProcessRequest() { client.setCmd(
     * TEST_COMMAND_NON_STOP );
     * 
     * try { for ( int index = 0; index < 4; index++ ) {
     * client.sendExecuteCommandRequest(); Thread.sleep( 200 ); }
     * 
     * client.sendKillAllProcessRequest();
     * 
     * Thread.sleep( 200 ); assertTrue( killUnexistentProcess() ); } catch (
     * LauncherException ex ) { handleException( ex ); } catch (
     * InterruptedException ex ) { handleException( ex ); } }
     */

    /*
     * @Test public void testGetDataFromProcess() { client.setCmd( TEST_COMMAND );
     * 
     * try { client.sendExecuteCommandRequest(); final String messageFromProcess =
     * client.getDataFromProcess();
     * 
     * assertTrue( ( EXPECTED_COMMAND_OUTPUT ).equals( messageFromProcess ) ); }
     * catch ( LauncherException ex ) { handleException( ex ); } }
     */
}

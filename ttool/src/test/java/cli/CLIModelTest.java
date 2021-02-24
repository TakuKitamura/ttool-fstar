/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 * /**
 * Class AvatarPragma
 * Creation: 20/05/2010
 * @version 1.1 01/07/2014
 * @author Ludovic APVRILLE, Raja GATGOUT
 * @see
 */

package cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.nio.file.Files;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractTest;


public class CLIModelTest extends AbstractTest implements InterpreterOutputInterface {

    final static String PATH_TO_TEST_FILE = "cli/input/";
    final static String PATH_TO_OUTPUT_FILE = "cli/output/";
    final static String NEW_MODEL_FILE = PATH_TO_OUTPUT_FILE + "mynewmodel.xml";
    final static String EMPTY_MODEL_FILE = PATH_TO_OUTPUT_FILE + "myemptymodel.xml";
    private StringBuilder outputResult;



	public CLIModelTest() {
	    //
    }
	

    public void exit(int reason) {
	    System.out.println("Exit reason=" + reason);
	    assertTrue(reason == 0);
    }

    public void printError(String error) {
        System.out.println("Error=" + error);
    }

    public void print(String s) {
	    System.out.println("info from interpreter:" + s);
	    outputResult.append(s);
    }
	
	@Test
	public void testNewModel() {
	    String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodel";
	    String script;
	    
	    outputResult = new StringBuilder();

	    File f = new File(filePath);
	    assertTrue(myutil.FileUtils.checkFileForOpen(f));

	    script = myutil.FileUtils.loadFileData(f);

	    assertTrue(script.length() > 0);


        // Create directory
        File removeIfExists = new File(getBaseResourcesDir() + NEW_MODEL_FILE);
        if (removeIfExists.exists()) {
            removeIfExists.delete();
        }
        File makeDir = new File(getBaseResourcesDir() + PATH_TO_OUTPUT_FILE);
        makeDir.mkdir();
        System.out.println("Created: " + makeDir.getAbsolutePath());


	    boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must check if the model has really been saved
        f = new File(getBaseResourcesDir() + NEW_MODEL_FILE);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        assertTrue (f.length() > 100);
        assertTrue (f.length() < 500);




	}

    @Test
    public void testRemoveTabEmptyModel() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptemptymodel";
        String script;

        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);
        assertTrue(script.length() > 0);

        // Create directory
        File removeIfExists = new File(getBaseResourcesDir() + EMPTY_MODEL_FILE);
        if (removeIfExists.exists()) {
            removeIfExists.delete();
        }
        File makeDir = new File(getBaseResourcesDir() + PATH_TO_OUTPUT_FILE);
        makeDir.mkdir();
        System.out.println("Created: " + makeDir.getAbsolutePath());


        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must check if the model has really been saved
        f = new File(getBaseResourcesDir() + EMPTY_MODEL_FILE);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        assertTrue (f.length() > 100);
        assertTrue (f.length() < 500);




    }
	


}

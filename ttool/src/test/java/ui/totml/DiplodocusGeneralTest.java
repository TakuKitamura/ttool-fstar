package ui.totml;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.*;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusGeneralTest extends AbstractUITest {

    //final String [] MODELS = {"scp"};
    final String [] MODELS = {"spec_sdf_simple.tml"};
    final String [] TASKS = {"ApplicationSimple_FixedSize__T1", "ApplicationSimple_FixedSize__T2"};

    // Complexity
    final int [] WorstCaseExecIValueByTask = {100, 170};

    // Write in channels
    final String [] WRITE_CHANNELS = {"ApplicationSimple_FixedSize__chToT2"};
    final String [] WRITE_TASKS = {"ApplicationSimple_FixedSize__T1"};
    final int [] WRITE_RESULTS = {60}; // in bytes

    // Read in channels
    final String [] READ_CHANNELS = {"ApplicationSimple_FixedSize__chToT1", "ApplicationSimple_FixedSize__chToT2"};
    final String [] READ_TASKS = {"ApplicationSimple_FixedSize__T1", "ApplicationSimple_FixedSize__T2"};
    final int [] READ_RESULTS = {200, 600}; // in bytes






    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/general/";
    }

    public DiplodocusGeneralTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void testWorstCase() throws Exception {
        for(int i=0; i<MODELS.length; i++) {
            String s = MODELS[i];
            // Load the TML
            System.out.println("Executing: loading " + s);
            TMLTextSpecification tmt = new TMLTextSpecification(s);
            File f = new File(RESOURCES_DIR + s);
            System.out.println("Executing: new file loaded " + s);
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                System.out.println("Exception executing: loading " + s);
                assertTrue(false);
            }
            System.out.println("executing: testing spec " + s);
            assertTrue(spec != null);
            System.out.println("executing: testing parsed " + s);
            boolean parsed = tmt.makeTMLModeling(spec);
            assertTrue(parsed);


            System.out.println("executing: checking syntax " + s);
            // Checking syntax
            TMLModeling tmlm = tmt.getTMLModeling();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlm);
            syntax.checkSyntax();

            assertTrue(syntax.hasErrors() == 0);

            // Checking Worst Case ExecI

            System.out.println("TMLModeling:" + tmlm.toString());

            int cpt = 0;
            for(String task: TASKS) {
                TMLTask t = tmlm.getTMLTaskByName(task);
                assertNotNull(t);

                int worstCaseExecI = t.getWorstCaseIComplexity();
                assertEquals(worstCaseExecI, WorstCaseExecIValueByTask[cpt]);

                cpt++;
            }

            for (cpt=0; cpt<WRITE_CHANNELS.length; cpt++) {
                TMLChannel ch = tmlm.getChannelByName(WRITE_CHANNELS[cpt]);
                assertNotNull(ch);

                TMLTask t = tmlm.getTMLTaskByName(WRITE_TASKS[cpt]);
                assertNotNull(t);

                int worstCaseWriteChannel = t.getWorstCaseDataSending(ch);
                assertEquals(worstCaseWriteChannel, WRITE_RESULTS[cpt]);
            }

            for (cpt=0; cpt<READ_CHANNELS.length; cpt++) {
                TMLChannel ch = tmlm.getChannelByName(READ_CHANNELS[cpt]);
                assertNotNull(ch);

                TMLTask t = tmlm.getTMLTaskByName(READ_TASKS[cpt]);
                assertNotNull(t);


                int worstCaseReadChannel = t.getWorstCaseDataReceiving(ch);
                System.out.println("Task=" + t.getTaskName() + " channel=" + ch.getName() + " read WC=" + worstCaseReadChannel);
                assertEquals(worstCaseReadChannel, READ_RESULTS[cpt]);
            }


        }

    }

}
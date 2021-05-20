package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.model.TestTimedOutException;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import tmltranslator.tonetwork.TMAP2Network;
import ui.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusNoCTest extends AbstractUITest {

  final String MODEL = "spec";
  final String DIR_GEN = "test_spec_noc/";
  final int SIZE_OF_NOCS = 2;
  final int NB_Of_SIM_CYCLES = 300;
  final String[] SIM_ACTIONS = { "INVC_CPU01___p3_vc0: Read 4,ch_pktin3_vc0_0_0",
      "NI_OUT_CPU01: Read 4,channelBetweenOUTToIN__P_x0_y0_N_x0_y0_OUTRouter",
      "NI_OUT_CPU11: Read 4,channelBetweenOUTToIN__P_x1_y0_N_x1_y0_OUTRouter" };

  private String SIM_DIR;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/noc/";

  }

  public DiplodocusNoCTest() {
    super();
  }

  @Before
  public void setUp() throws Exception {
    // SIM_DIR = getBaseResourcesDir() + "/" + DIR_GEN;
    // SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/" + DIR_GEN;
    SIM_DIR = DIR_GEN;
  }

  @Test(timeout = 600000) // in milliseconds
  public void testSimulationGraph() throws Exception {

    String s = MODEL;
    // Load the TML
    System.out.println("executing: loading " + s);
    TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(s);
    File f = new File(RESOURCES_DIR + s + ".tmap");
    System.out.println("executing: new file loaded " + s);
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
    boolean parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
    assertTrue(parsed);

    System.out.println("executing: checking syntax " + s);
    // Checking syntax
    TMLMapping tmap = tmts.getTMLMapping();

    TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
    syntax.checkSyntax();

    assertEquals(syntax.hasErrors(), 0);

    // Check if models contain the expected nb of NoCs
    int size = tmap.getTMLArchitecture().getSizeOfNoC();

    // System.out.println("SIZE=" + size);

    assertEquals(size, SIZE_OF_NOCS);

    // Remove Noc
    TMAP2Network t2n = new TMAP2Network<>(tmap, size);
    String error = t2n.removeAllRouterNodes();

    // System.out.println("NOC error=" + error);

    assertNull(error);

    // Check syntax of the new mapping
    syntax = new TMLSyntaxChecking(tmap);
    tmap.forceMakeAutomata();
    syntax.checkSyntax();

    if (syntax.hasErrors() > 0) {
      for (TMLError er : syntax.getErrors()) {
        System.out.println("NOC error:" + er.toString());
      }

    }

    assertEquals(syntax.hasErrors(), 0);

    // Generate SystemC code
    System.out.println("NOC executing: sim code generation for " + s);
    final IDiploSimulatorCodeGenerator tml2systc;
    List<EBRDD> al = new ArrayList<EBRDD>();
    List<TEPE> alTepe = new ArrayList<TEPE>();
    tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
    tml2systc.setModelName(s);
    error = tml2systc.generateSystemC(false, true);
    assertNull(error);

    File directory = new File(SIM_DIR);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    // Putting sim files
    System.out.println("NOC executing: sim lib code copying for " + s);
    ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + "../../../../simulators/c++2/";
    boolean simFiles = SpecConfigTTool.checkAndCreateSystemCDir(SIM_DIR);

    System.out.println("NOC executing: sim lib code copying done with result " + simFiles);
    assertTrue(simFiles);

    System.out.println("NOC Saving file in: " + SIM_DIR);
    tml2systc.saveFile(SIM_DIR, "appmodel");

    // Compile it
    System.out.println("NOC executing: compile");
    Process proc;
    BufferedReader proc_in;
    BufferedReader proc_err;
    String str;
    boolean mustRecompileAll;
    Penalties penalty = new Penalties(SIM_DIR + File.separator + "src_simulator");
    int changed = penalty.handlePenalties(false);

    if (changed == 1) {
      mustRecompileAll = true;
    } else {
      mustRecompileAll = false;
    }

    if (mustRecompileAll) {
      System.out.println("executing: " + "make -C " + SIM_DIR + " clean");
      try {
        proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + " clean");
        proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while ((str = proc_in.readLine()) != null) {
          // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
          System.out.println("executing: " + str);
        }
      } catch (Exception e) {
        // probably make is not installed
        System.out.println("FAILED: executing: " + "make -C " + SIM_DIR + " clean");
        return;
      }
    }

    System.out.println("NOC executing: " + "make -C " + SIM_DIR);
    try {
      String[] params = new String[3];

      params[0] = "make";
      params[1] = "-C";
      params[2] = SIM_DIR;

      proc = Runtime.getRuntime().exec(params);
      proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      monitorError(proc);

      while ((str = proc_in.readLine()) != null) {
        System.out.println("NOC executing: " + str);
      }
    } catch (Exception e) {
      // Probably make is not installed
      System.out.println("NOC FAILED: executing: " + "make -C " + SIM_DIR);
      return;
    }
    System.out.println("NOC SUCCESS: executing: " + "make -C " + SIM_DIR);

    // Run the simulator
    try {
      System.out.println("NOC executing simulation in " + SIM_DIR + " and gen file in " + s + ".txt");
      String[] params = new String[3];

      params[0] = "./" + SIM_DIR + "run.x";
      params[1] = "-cmd";
      params[2] = "1 6 " + NB_Of_SIM_CYCLES + "; 7 2 " + DIR_GEN + s + ".txt";
      proc = Runtime.getRuntime().exec(params);
      proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      monitorError(proc);

      while ((str = proc_in.readLine()) != null) {
        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
        System.out.println("NOC executing: " + str);
      }
    } catch (Exception e) {
      // Probably make is not installed
      System.out.println("FAILED: executing simulation");
      assertTrue(false);
      return;
    }

    System.out.println("NOC Simulation executed");

    // Compare results with expected ones
    // Must load the file data
    File simFile = new File(DIR_GEN + s + ".txt");
    String simData = "";
    try {
      simData = FileUtils.loadFileData(simFile);
    } catch (Exception e) {
      assertTrue(false);
    }

    for (String act : SIM_ACTIONS) {
      assertTrue(simData.indexOf(act) > -1);
    }

    assertTrue(true);
    System.out.println("NOC Done");
  }

}
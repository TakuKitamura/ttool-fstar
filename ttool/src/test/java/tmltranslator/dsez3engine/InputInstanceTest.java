package tmltranslator.dsez3engine;

import com.microsoft.z3.Context;
import com.microsoft.z3.Log;
import com.microsoft.z3.Z3Exception;
import org.junit.Before;
import org.junit.Test;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class InputInstanceTest {

    public TMLArchitecture tmla;
    public TMLModeling tmlm;

    public InputInstance inputInstance;

    public OptimizationModel optimizationModel;

    @Before
    public void setUpTest() {

        tmla = setUpTMLArchitecture();
        tmlm = setUpTMLModeling();

        inputInstance = new InputInstance(tmla, tmlm);

        optimizationModel = new OptimizationModel(inputInstance);
    }

    private TMLModeling setUpTMLModeling() {

        tmlm = new TMLModeling();


        TMLTask taskA = new TMLTask("task__A", null, null);
        TMLTask taskB = new TMLTask("task__B", null, null);
        TMLTask taskD = new TMLTask("task__D", null, null);
        TMLTask taskE = new TMLTask("task__E", null, null);

        //filling activity diagrams of tasks

        //taskA
        TMLActivityElementWithAction firstA = new TMLExecI("execiA", null);
        firstA.setAction("150");
        taskA.getActivityDiagram().setFirst(firstA);

        TMLActivityElement AwB = new TMLWriteChannel("AwB", null);
        taskA.getActivityDiagram().addLinkElement(firstA, AwB);
        taskA.getActivityDiagram().addElement(AwB);

        TMLActivityElement AwD = new TMLWriteChannel("AwD", null);
        taskA.getActivityDiagram().addLinkElement(AwB, AwD);
        taskA.getActivityDiagram().addElement(AwD);

        //taskB
        TMLActivityElement firstB = new TMLReadChannel("BrA", null);
        taskB.getActivityDiagram().setFirst(firstB);

        TMLActivityElementWithAction execiB = new TMLExecI("execiB", null);
        execiB.setAction("100");
        taskB.getActivityDiagram().addLinkElement(firstB, execiB);
        taskB.getActivityDiagram().addElement(execiB);

        TMLActivityElement BwE = new TMLWriteChannel("BwE", null);
        taskB.getActivityDiagram().addLinkElement(execiB, BwE);
        taskB.getActivityDiagram().addElement(BwE);

        //taskD
        TMLActivityElement firstD = new TMLReadChannel("DrA", null);
        taskD.getActivityDiagram().setFirst(firstD);

        TMLActivityElementWithAction execiD = new TMLExecI("execiD", null);
        execiD.setAction("100");
        taskD.getActivityDiagram().addLinkElement(firstD, execiD);
        taskD.getActivityDiagram().addElement(execiD);

        TMLActivityElement DwE = new TMLWriteChannel("DwE", null);
        taskD.getActivityDiagram().addLinkElement(execiD, DwE);
        taskD.getActivityDiagram().addElement(DwE);

        //taskE
        TMLActivityElement firstE = new TMLReadChannel("ErB", null);
        taskE.getActivityDiagram().setFirst(firstE);

        TMLActivityElement ErD = new TMLReadChannel("ErD", null);
        taskE.getActivityDiagram().addLinkElement(firstE, ErD);
        taskE.getActivityDiagram().addElement(ErD);

        TMLActivityElementWithAction execiE = new TMLExecI("execiE", null);
        execiE.setAction("50");
        taskE.getActivityDiagram().addLinkElement(ErD, execiE);
        taskE.getActivityDiagram().addElement(execiE);


        taskA.addOperation("generic");
        taskB.addOperation("fft");
        taskD.addOperation("fft");
        taskE.addOperation("generic");

        //creating channels
        TMLChannel ab = new TMLChannel("ab", null);
        TMLChannel ad = new TMLChannel("ad", null);
        TMLChannel be = new TMLChannel("be", null);
        TMLChannel de = new TMLChannel("de", null);

        taskA.addWriteTMLChannel(ab);
        taskA.addWriteTMLChannel(ad);

        taskB.addReadTMLChannel(ab);
        taskB.addWriteTMLChannel(be);

        taskD.addReadTMLChannel(ad);
        taskD.addWriteTMLChannel(de);

        taskE.addReadTMLChannel(be);
        taskE.addReadTMLChannel(de);

        ab.setTasks(taskA, taskB);
        ad.setTasks(taskA, taskD);
        be.setTasks(taskB, taskE);
        de.setTasks(taskD, taskE);

        ab.setNumberOfSamples(2);
        ad.setNumberOfSamples(2);
        be.setNumberOfSamples(5);
        de.setNumberOfSamples(5);

        tmlm.addTask(taskA);
        tmlm.addTask(taskB);
        tmlm.addTask(taskD);
        tmlm.addTask(taskE);

        tmlm.addChannel(ab);
        tmlm.addChannel(ad);
        tmlm.addChannel(be);
        tmlm.addChannel(de);


        return tmlm;
    }


    private TMLArchitecture setUpTMLArchitecture() {

        HwExecutionNode mainCPU = new HwCPU("MainCPU");
        HwMemory mainMem = new HwMemory("mainMem");

        HwExecutionNode dsp = new HwCPU("dsp");
        HwMemory dspMem = new HwMemory("dspMem");

        HwBus bus0 = new HwBus("bus0");
        HwBus bus1 = new HwBus("bus1");


        HwLink maincpu_bus0 = new HwLink("maincpu_bus0");
        HwLink bus0_cpumem = new HwLink("bus0_cpumem");

        HwLink dsp_bus1 = new HwLink("dsp_bus1");
        HwLink bus1_dspmem = new HwLink("bus1_dspmem");

        mainCPU.execiTime = 2;
        dsp.execiTime = 1;


        mainCPU.addOperationType("generic");
        mainCPU.addOperationType("fft");

        mainMem.memorySize = 200;

        dsp.addOperationType("fft");
        dspMem.memorySize = 100;

        maincpu_bus0.hwnode = mainCPU;
        maincpu_bus0.bus = bus0;

        bus0_cpumem.bus = bus0;
        bus0_cpumem.hwnode = mainMem;

        dsp_bus1.hwnode = dsp;
        dsp_bus1.bus = bus1;

        bus1_dspmem.bus = bus1;
        bus1_dspmem.hwnode = dspMem;


        tmla = new TMLArchitecture();

        tmla.addHwNode(mainCPU);
        tmla.addHwNode(dsp);
        tmla.addHwNode(mainMem);
        tmla.addHwNode(dspMem);
        tmla.addHwNode(bus0);
        tmla.addHwNode(bus1);
        tmla.addHwLink(maincpu_bus0);
        tmla.addHwLink(bus0_cpumem);
        tmla.addHwLink(dsp_bus1);
        tmla.addHwLink(bus1_dspmem);

        return tmla;
    }

    @Test
    public void findOptimizedMapping() {

        try {

            // These examples need model generation turned on.
            HashMap<String, String> cfg = new HashMap<String, String>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);


            optimizationModel.findOptimizedMapping(ctx);

            Log.close();
            if (Log.isOpen())
                System.out.println("Log is still open!");
        } catch (Z3Exception ex) {
            System.out.println("Z3 Managed Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(
                    System.out);
        } catch (OptimizationModel.TestFailedException ex) {
            System.out.println("TEST CASE FAILED: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            System.out.println("Unknown Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        }


    }


    @Test
    public void findFeasibleMapping() {

        try {

            // These examples need model generation turned on.
            HashMap<String, String> cfg = new HashMap<String, String>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);


            optimizationModel.findFeasibleMapping(ctx);

            Log.close();
            if (Log.isOpen())
                System.out.println("Log is still open!");
        } catch (Z3Exception ex) {
            System.out.println("Z3 Managed Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(
                    System.out);
        } catch (OptimizationModel.TestFailedException ex) {
            System.out.println("TEST CASE FAILED: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            System.out.println("Unknown Exception: " + ex.getMessage());
            System.out.println("Stack trace: ");
            ex.printStackTrace(System.out);
        }


    }


    @Test
    public void getFeasibleCPUs() {

        Boolean test = false;
        List <HwExecutionNode> expectedList = new ArrayList<>();
        expectedList.add(inputInstance.getArchitecture().getHwCPUByName("MainCPU"));
        expectedList.add(inputInstance.getArchitecture().getHwCPUByName("dsp"));

        TMLTask tempTask = (TMLTask) inputInstance.getModeling().getTasks().get(2);


        List<HwExecutionNode> actualList = new ArrayList<>();

        for (int i = 0; i < inputInstance.getFeasibleCPUs(tempTask).size(); i++) {
            actualList.add(inputInstance.getFeasibleCPUs(tempTask).get(i));
        }

        assertEquals(actualList.size(), expectedList.size());

        for (HwExecutionNode hwExecutionNode : actualList){
            assertTrue(expectedList.contains(hwExecutionNode));
        }

    }

    @Test
    public void getBufferIn() {

        List<TMLTask> tempTasks = new ArrayList<>();
        for (int i = 0; i < inputInstance.getModeling().getTasks().size(); i++) {
            tempTasks.add((TMLTask) inputInstance.getModeling().getTasks().get(i));
        }

        assertEquals(inputInstance.getBufferIn((TMLTask) tempTasks.get(0)), 0);
        assertEquals(inputInstance.getBufferIn((TMLTask) tempTasks.get(1)), 2);
        assertEquals(inputInstance.getBufferIn((TMLTask) tempTasks.get(2)), 2);
        assertEquals(inputInstance.getBufferIn((TMLTask) tempTasks.get(3)), 10);


    }

    @Test
    public void getBufferOut() {
        List<TMLTask> tempTasks = new ArrayList<>();
        for (int i = 0; i < inputInstance.getModeling().getTasks().size(); i++) {
            tempTasks.add((TMLTask) inputInstance.getModeling().getTasks().get(i));
        }

        assertEquals(inputInstance.getBufferOut((TMLTask) tempTasks.get(0)), 4);
        assertEquals(inputInstance.getBufferOut((TMLTask) tempTasks.get(1)), 5);
        assertEquals(inputInstance.getBufferOut((TMLTask) tempTasks.get(2)), 5);
        assertEquals(inputInstance.getBufferOut((TMLTask) tempTasks.get(3)), 0);

    }


    @Test
    public void getLocalMemoryOfHwExecutionNode() {

        HwNode output1 = inputInstance.getArchitecture().getHwMemoryByName("mainMem");
        HwNode output2 = inputInstance.getArchitecture().getHwMemoryByName("dspMem");

        assertTrue("comparing between the expected local memory for main_CPU and the memory found", inputInstance.getLocalMemoryOfHwExecutionNode(inputInstance.getArchitecture().getHwNodeByName("MainCPU")) == output1);
        assertTrue("comparing between the expected local memory for DSP and the memory found", inputInstance.getLocalMemoryOfHwExecutionNode(inputInstance.getArchitecture().getHwNodeByName("dsp")) == output2);

    }


    @Test
    public void getWCET() {
        //a temporary list of the tasks
        List<TMLTask> tempTasks = new ArrayList<>();
        for (int i = 0; i < inputInstance.getModeling().getTasks().size(); i++) {
            tempTasks.add((TMLTask) inputInstance.getModeling().getTasks().get(i));
        }
        int expectedWcetA_CPU = 300;
        int expectedWcetA_dsp = 150;

        assertEquals("comparing between the expected WCET of task A on main_CPU and the computed value", expectedWcetA_CPU, inputInstance.getWCET(tempTasks.get(0), (HwExecutionNode) inputInstance.getArchitecture().getHwNodeByName("MainCPU")));
        assertEquals("comparing between the expected WCET of task A on DSP and the computed value", expectedWcetA_dsp, inputInstance.getWCET(tempTasks.get(0), (HwExecutionNode) inputInstance.getArchitecture().getHwNodeByName("dsp")));

    }


    @Test
    public void getFinalTask() {
        assertEquals("checking the last task", inputInstance.getModeling().getTMLTaskByName("task__E"), inputInstance.getFinalTask(inputInstance.getModeling()));

    }
}
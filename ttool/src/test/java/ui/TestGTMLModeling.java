package ui;

import myutil.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import tmltranslator.*;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestGTMLModeling extends AbstractUITest {

    private static final String MODEL_ZIGBEE_TUTORIAL = "ZigBeeTutorial";
    private static final String DIAG_ZIGBEE_TX = "Zigbee_TX";
    private static final String DIAG_F_SOURCE = "F_Source";
    private static final String DIAG_X_SOURCE = "X_Source";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "ui/diagram2tml/";
        INPUT_DIR = RESOURCES_DIR + "input/";
        EXPECTED_CODE_DIR = RESOURCES_DIR + "expected/";
        ACTUAL_CODE_DIR = RESOURCES_DIR + "actual/";
    }

    public TestGTMLModeling() {
        super();
    }

    private TMLModeling<TGComponent> translateToTMLModeling(final String tabName)
            throws MalformedTMLDesignException {
        final TMLComponentDesignPanel panel = (TMLComponentDesignPanel) mainGUI.getTURTLEPanel(tabName);
        final GTMLModeling gtmlm = new GTMLModeling(panel, true);
        gtmlm.putPrefixName(true);

        final TMLModeling<TGComponent> model = gtmlm.translateToTMLModeling(false);

        if (!gtmlm.getCheckingErrors().isEmpty()) {
            throw new MalformedTMLDesignException("Errors were found when generating TML model.", gtmlm.getCheckingErrors());
        }

        return model;
    }

    private String translateToTMLModelingXMLZigBeeTutorial(final String diagramName)
            throws MalformedTMLDesignException {
        openModel(MODEL_ZIGBEE_TUTORIAL);

        return translateToTMLModeling(diagramName).toXML();
    }

    private TMLMapping<TGComponent> translateToTMLMapping(final String diagramName)
            throws MalformedTMLDesignException {
        final TMLArchiPanel panel = findArchiPanel(diagramName);
        final GTMLModeling gtmlm = new GTMLModeling(panel, true);
        gtmlm.putPrefixName(true);

        final TMLMapping<TGComponent> model = gtmlm.translateToTMLMapping();

        if (!gtmlm.getCheckingErrors().isEmpty()) {
            throw new MalformedTMLDesignException("Errors were found when generating TML model.", gtmlm.getCheckingErrors());
        }

        return model;
    }

    private String translateToTMLMappingXMLZigBeeTutorial(final String diagramName)
            throws MalformedTMLDesignException {
        openModel(MODEL_ZIGBEE_TUTORIAL);

        return translateToTMLMapping(diagramName).toXML();
    }

    @Test
    public void testTranslateToTMLModelingZigbeeTX()
            throws MalformedTMLDesignException {
        final String actualCode = translateToTMLModelingXMLZigBeeTutorial(DIAG_ZIGBEE_TX);

        assertTrue(actualCode != null);

        File f = new File("actual.xml");
        try {
            FileUtils.saveFile(f, actualCode);
        } catch (Exception e) {

        }

        //checkResultXml( actualCode, DIAG_ZIGBEE_TX );
    }

    @Test
    public void testTranslateToTMLMappingMapping0()
            throws MalformedTMLDesignException {
        final String diagramName = "Mapping_0";
        final String actualCode = translateToTMLMappingXMLZigBeeTutorial(diagramName);

        assertTrue(actualCode != null);


        File f = new File("mapping0.xml");
        try {
            FileUtils.saveFile(f, actualCode);
        } catch (Exception e) {

        }

        //checkResultXml( actualCode, diagramName );
    }

    protected void testTranslateToTMLModelingDisable(final String modelName,
                                                     final String tabName,
                                                     final String taskName,
                                                     final String testName,
                                                     final int... compoIds)
            throws MalformedTMLDesignException {
        openModel(modelName);

        final TURTLEPanel panel = mainGUI.getTURTLEPanel(tabName);
        final TDiagramPanel parentPanel = panel.tdp;

        final TDiagramPanel activityDiagramPanel = findDiagramPanel(parentPanel, taskName);
        assertNotNull(activityDiagramPanel);

        for (final int compoId : compoIds) {
            final TGComponent compoToDisable = findDiagComponent(compoId, activityDiagramPanel);
            if (compoToDisable == null) {
                System.out.println("\n****FAILED ID: " + compoId + " for test " + testName + " taskName " + taskName + " ****\n");
                for(TGComponent tgc: activityDiagramPanel.getComponentList()) {
                    System.out.println("\t\tFAILED comp:" + tgc.getName() + " id=" + tgc.getId() + " UUID=" + tgc.getUUID().toString());
                }
            }
            assertNotNull(compoToDisable);
            compoToDisable.setEnabled(false);
        }

        final TMLModeling<TGComponent> tmlModel = translateToTMLModeling(tabName);

        assertNotNull(tmlModel);

        // Check no error
        TMLSyntaxChecking tmlsc = new TMLSyntaxChecking(tmlModel);
        tmlsc.checkSyntax();
        ArrayList<TMLError> errors = tmlsc.getErrors();
        assertEquals(errors.size(), 0);
        ArrayList<TMLError> warnings = tmlsc.getWarnings();
        assertEquals(warnings.size(), 0);


        //final String prefixedTaskName = tabName + "__" + taskName;
        //final TMLTask task = tmlModel.getTMLTaskByName(prefixedTaskName);

        //checkResultXml(task.getActivityDiagram().toXML(), "components_disabling/" + taskName + "_Disable_" + testName);
    }

    protected void testTranslateToTMLModelingDisableZigBeeTutorial(final String taskName,
                                                                   final String testName,
                                                                   final int... compoIds)
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisable(MODEL_ZIGBEE_TUTORIAL, DIAG_ZIGBEE_TX, taskName, testName, compoIds);
    }

    protected void testTranslateToTMLModelingDisableSmartCardProtocol(final String taskName,
                                                                      final String testName,
                                                                      final int... compoIds)
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisable("SmartCardProtocol", "AppC", taskName, testName, compoIds);
    }

    // Tests for disabling every element type

    @Test
    public void testTranslateToTMLModelingDisableActionState()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "ActionState", 925);
    }

    @Test
    public void testTranslateToTMLModelingDisableSendRequest()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "SendRequest", 922);
    }

    @Test
    public void testTranslateToTMLModelingSendEvent()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "SendEvent", 919);
    }

    @Test
    public void testTranslateToTMLModelingDisableExecC()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial("X_Chip2Octet", "ExecC", 1009);
    }

    @Test
    public void testTranslateToTMLModelingDisableExecI()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_X_SOURCE, "ExecI", 935);
    }

    @Test
    public void testTranslateToTMLModelingDisableReadRequestArg()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_X_SOURCE, "ReadRequestArg", 943);
    }

    @Test
    public void testTranslateToTMLModelingDisableWriteChannel()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_X_SOURCE, "WriteChannel", 940);
    }

    @Test
    public void testTranslateToTMLModelingDisableWaitEvent()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial("F_Symbol2ChipSeq", "WaitEvent", 952);
    }

    @Test
    public void testTranslateToTMLModelingDisableReadChannel()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial("X_Symbol2ChipSeq", "ReadChannel", 973);
    }

    @Test
    public void testTranslateToTMLModelingDisableActionStateSendRequest()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "ActionStateSendRequest", new int[]{925, 922});
    }

    @Test
    public void testTranslateToTMLModelingDisableForLoop()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableSmartCardProtocol("InterfaceDevice", "ForLoop", 1285);
    }

    @Test
    public void testTranslateToTMLModelingDisableForLoopAfterStart()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableSmartCardProtocol("TCPIP", "ForLoopAfterStart", 510);
    }

    @Test
    public void testTranslateToTMLModelingDisableChoiceLeft() {
        try {
            testTranslateToTMLModelingDisableSmartCardProtocol("InterfaceDevice", "ChoiceLeft", 1218);
        } catch (final MalformedTMLDesignException ex) {
            assertTrue("TML modeling translation did not generate the expected '" + UICheckingError.MESSAGE_CHOICE_BOTH_STOCHASTIC_DETERMINISTIC + "' error!", ex.getErrors().size() == 1);

            assertEquals(UICheckingError.MESSAGE_CHOICE_BOTH_STOCHASTIC_DETERMINISTIC, ex.getErrors().get(0).toString());
        }
    }

    @Test
    public void testTranslateToTMLModelingDisableChoiceLeftBottom()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableSmartCardProtocol("TCPIP", "ChoiceLeftBottom", 1060, 1063);
    }

    @Test
    public void testTranslateToTMLModelingDisableChoiceLeftBottomRight()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableSmartCardProtocol("TCPIP", "ChoiceLeftBottomRight", 1043, 1044, 1045);
    }

    // Tests for disabling connectors

    @Test
    public void testTranslateToTMLModelingDisableConnector()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableSmartCardProtocol("InterfaceDevice", "ConnectorChoice", 1305);
    }

    // Tests for disabling several elements

    @Test
    public void testTranslateToTMLModelingDisableActionStateSendEvent()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "ActionStateSendEvent", new int[]{925, 919});
    }

    @Test
    public void testTranslateToTMLModelingDisableSendRequestSendEvent()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "SendRequestSendEvent", new int[]{922, 919});
    }

    @Test
    public void testTranslateToTMLModelingDisableActionStateSendRequestSendEvent()
            throws MalformedTMLDesignException {
        testTranslateToTMLModelingDisableZigBeeTutorial(DIAG_F_SOURCE, "ActionStateSendRequestSendEvent", new int[]{925, 922, 919});
    }
}

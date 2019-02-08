package ui.tmlcompd;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.*;
import java.io.File;

import static org.junit.Assert.*;

public class TMLCPrimitivePortTest extends AbstractUITest {
    static TDiagramPanel diagramPanel;
    static TMLCChannelOutPort eventPortIn;
    static TMLCChannelOutPort channelPortIn;
    static TMLCChannelOutPort requestPortIn;

    static TMLCChannelOutPort eventPortOut;
    static TMLCChannelOutPort channelPortOut;
    static TMLCChannelOutPort requestPortOut;

    static TMLCPrimitiveComponent primitiveComponent1;
    static TMLCPrimitiveComponent primitiveComponent2;
    final String VALID_NAME_1    = "valid_name";
    final String VALID_NAME_2   = "valid_name_1, valid_name_2, valid_name_3";
    final String INVALID_NAME_1   = "1_name"; //name begins by number
    final String INVALID_NAME_2   = "name 2"; // name with whitespace
    final String INVALID_NAME_3  = "clock"; //one of UPPAAL keywords
    final String INVALID_NAME_4  = "name?"; //name with specialized characters
    final String INVALID_NAME_5 = "";
    //final String INVALID_NAME_6   = "protected"; //one of java keywords
    //final String INVALID_NAME_7  = "exit"; //one of RTLOTOS keywords
    final String INVALID_NAME_6   = "Natural"; //one of String type
    final String INVALID_NAME_7   = "ENDTASK"; // one of TMLkeywords
    final String INVALID_NAME_8   = "name1, 2name, name3"; //fault at "2name"
    final String INVALID_NAME_9   = "name1, name2, name3,"; //have not name after the comma

    final String CHANNEL_IN = "channel_in";
    final String CHANNEL_OUT = "channel_out";
    final String EVENT_IN = "event_in";
    final String EVENT_OUT = "event_out";
    final String REQUEST_IN = "request_in";
    final String REQUEST_OUT = "request_out";




    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/tmlcompd/input/TestPortName.xml";
        //RESOURCES_DIR = "/home/pham/Documents/TToolDev/TTool/ttool/src/test/resources/ui/tmlcompd/input/TestPortName.xml";

    }

    public TMLCPrimitivePortTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() throws Exception {
        diagramPanel = null;
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLComponentDesignPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof  TMLComponentTaskDiagramPanel) {
                        diagramPanel = tdp;
                        mainGUI.selectTab(tdp);
                        break;
                    }
                }
                break;
            }
        }


        for (TGComponent tgc : diagramPanel.getComponentList()) {
            if (tgc.getValue().equals("CompositeComponent")) {
                for (TMLCPrimitiveComponent tmlcpp : ((TMLCCompositeComponent) tgc).getAllPrimitiveComponents()) {
                    if (tmlcpp.getValue().equals("PrimitiveComp1")) {
                        primitiveComponent1 = tmlcpp;
                    }
                    if (tmlcpp.getValue().equals("PrimitiveComp2")) {
                        primitiveComponent2 = tmlcpp;
                    }
                }
            }
        }


        if (diagramPanel != null && primitiveComponent1 != null && primitiveComponent2 != null) {
            eventPortOut = new TMLCChannelOutPort(509, 672, -13, 187, -13, 583,true, primitiveComponent1, diagramPanel);
            eventPortOut.isOrigin = true;
            eventPortOut.typep = 1;
            eventPortIn = new TMLCChannelOutPort(708, 694, -13, 187, -13, 578,true, primitiveComponent2, diagramPanel);
            eventPortIn.isOrigin = false;
            eventPortIn.typep = 1;

            channelPortOut = new TMLCChannelOutPort(509, 625, -13, 187, -13, 583,true, primitiveComponent1, diagramPanel);
            channelPortOut.isOrigin = true;
            channelPortOut.typep = 0;
            channelPortIn = new TMLCChannelOutPort(708, 630, -13, 187, -13, 578,true, primitiveComponent2, diagramPanel);
            channelPortIn.isOrigin = false;
            channelPortIn.typep = 0;

            requestPortOut = new TMLCChannelOutPort(509, 730, -13, 187, -13, 583,true, primitiveComponent1, diagramPanel);
            requestPortOut.isOrigin = true;
            requestPortOut.typep = 2;
            requestPortIn = new TMLCChannelOutPort(708, 747, -13, 187, -13, 578,true, primitiveComponent2, diagramPanel);
            requestPortIn.isOrigin = false;
            requestPortIn.typep = 2;
        }
    }


    @Test
    public void testEventPortName() throws Exception {
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(eventPortIn,EVENT_IN));
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(eventPortOut,EVENT_OUT));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(eventPortOut,EVENT_IN));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(eventPortIn,EVENT_OUT));
    }

    @Test
    public void testChannelPortName() throws Exception {
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(channelPortIn,CHANNEL_IN));
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(channelPortOut,CHANNEL_OUT));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(channelPortOut,CHANNEL_IN));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(channelPortIn,CHANNEL_OUT));
    }

    @Test
    public void tesRequestPortName() throws Exception {
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(requestPortIn,EVENT_IN));
        assertTrue("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(requestPortOut,EVENT_OUT));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(requestPortOut,REQUEST_IN));
        assertFalse("Test : Port name in use",((TMLComponentTaskDiagramPanel) diagramPanel).namePrimitivePortInUse(requestPortIn,REQUEST_OUT));
    }

    @Test
    public void testInAValidName()throws  Exception {
        assertTrue(TAttribute.isAValidPortName(VALID_NAME_1,false,true, false,false));
        assertTrue(TAttribute.isAValidPortName(VALID_NAME_2,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_1,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_2,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_3,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_4,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_5,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_6,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_7,false,true, false,true));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_8,false,true, false,false));
        assertFalse(TAttribute.isAValidPortName(INVALID_NAME_9,false,true, false,false));

    }

}
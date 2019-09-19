/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package tmltranslator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testing equalSpec function for comparing hardware nodes
 * author : Minh Hiep
 * update test : 19/10/2019
 */

public class Test_CompareHardwareNode extends AbstractUITest {

    TDiagramPanel architecture;
    TMLArchitecture tmlArchitecture;
    TMLMapping tmlMapping;

    HwLink hwLink1, hwLink2, hwLink3, linkTest1, linkTest2, linkTest3;

    HwCPU hwCPU, cpuTest, cpu0;
    HwA hwa, hwaTest;
    HwDMA hwDMA, dmaTest;
    HwMemory hwMemory, memoryTest;
    HwFPGA hwFPGA, fpgaTest;
    HwBus hwBus, busTest, bus0;
    HwBridge hwBridge, bridgeTest, bridge0;

    List<HwNode> hwNodeList;
    List<HwLink> hwLinkList;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/diagram2tml/input/Test_equalsFunction.xml";

    }

    public Test_CompareHardwareNode() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() {
        hwCPU = new HwCPU("CPU1");
        hwa = new HwA("HWA1");
        hwDMA = new HwDMA("DMA1");
        hwMemory =  new HwMemory("Memory1");
        hwFPGA = new HwFPGA("FPGA1");
        hwBus = new HwBus("Bus1");
        hwBridge = new HwBridge("Bridge1");

        hwLink1 = new HwLink("link_Bridge0_to_Bus0");
        hwLink2 = new HwLink("link_CPU0_to_Bus0");
        hwLink3 = new HwLink("link_CPU1_to_Bus0");

        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLArchiPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof TMLArchiDiagramPanel) {
                        architecture = tdp;
                        mainGUI.selectTab(architecture);
                        break;
                    }
                }
                break;
            }
        }

        assertNotNull(architecture);

        mainGUI.checkModelingSyntax(true);
        tmlMapping = mainGUI.gtm.getTMLMapping();
        tmlArchitecture = tmlMapping.getTMLArchitecture();

        cpuTest = tmlArchitecture.getHwCPUByName("CPU1");
        assertNotNull(cpuTest);

        cpu0 = tmlArchitecture.getHwCPUByName("CPU0");
        assertNotNull(cpu0);

        hwaTest = (HwA) tmlArchitecture.getHwNodeByName("HWA1");
        assertNotNull(hwaTest);

        dmaTest = (HwDMA) tmlArchitecture.getHwNodeByName("DMA1");
        assertNotNull(dmaTest);

        memoryTest = tmlArchitecture.getHwMemoryByName("Memory1");
        assertNotNull(memoryTest);

        fpgaTest = (HwFPGA) tmlArchitecture.getHwNodeByName("FPGA1");
        assertNotNull(fpgaTest);

        busTest = tmlArchitecture.getHwBusByName("Bus1");
        assertNotNull(busTest);

        bus0 = tmlArchitecture.getHwBusByName("Bus0");
        assertNotNull(bus0);

        bridgeTest = (HwBridge) tmlArchitecture.getHwNodeByName("Bridge1");
        assertNotNull(bridgeTest);

        bridge0 = (HwBridge) tmlArchitecture.getHwNodeByName("Bridge0");
        assertNotNull(bridge0);

        linkTest1 = tmlArchitecture.getHwLinkByName("link_Bridge0_to_Bus0");
        assertNotNull(linkTest1);

        linkTest2 = tmlArchitecture.getHwLinkByName("link_CPU0_to_Bus0");
        assertNotNull(linkTest2);

        linkTest3 = tmlArchitecture.getHwLinkByName("link_CPU1_to_Bus0");
        assertNotNull(linkTest3);

        hwLink1.setNodes(bus0,bridge0);
        hwLink2.setNodes(bus0,cpu0);
        hwLink3.setNodes(bus0,cpuTest);

        hwNodeList = new ArrayList<>();

        hwNodeList.add(busTest);
        hwNodeList.add(cpuTest);
        hwNodeList.add(bridgeTest);
        hwNodeList.add(memoryTest);
        hwNodeList.add(dmaTest);
        hwNodeList.add(fpgaTest);
        hwNodeList.add(hwaTest);
        hwNodeList.add(cpu0);
        hwNodeList.add(bridge0);
        hwNodeList.add(bus0);


        hwLinkList = new ArrayList<>();
        hwLinkList.add(hwLink2);
        hwLinkList.add(hwLink1);
        hwLinkList.add(hwLink3);

    }

    @Test
    public void test_CompareTwoHwNodes() {
        assertTrue(hwDMA.equalSpec(dmaTest));
        assertTrue(hwCPU.equalSpec(cpuTest));
        assertTrue(hwa.equalSpec(hwaTest));
        assertTrue(hwMemory.equalSpec(memoryTest));
        assertTrue(hwFPGA.equalSpec(fpgaTest));
        assertTrue(hwBus.equalSpec(busTest));
        assertTrue(hwBridge.equalSpec(bridgeTest));
    }

    @Test
    public void test_compareTwoListOfHwNodes() {
        assertTrue(tmlArchitecture.isHwNodeListEquals(hwNodeList,tmlArchitecture.getHwNodes()));
    }

    @Test
    public void test_CompareTwoHwLink() {
        assertTrue(hwLink1.equalSpec(linkTest1));
        assertTrue(hwLink2.equalSpec(linkTest2));
        assertTrue(hwLink3.equalSpec(linkTest3));
    }

    @Test
    public void test_CompareTwoListOfHwLinks() {
        assertTrue(tmlArchitecture.isHwlinkListEquals(hwLinkList,tmlArchitecture.getHwLinks()));
    }
}
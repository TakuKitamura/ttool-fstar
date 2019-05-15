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

package ui.tmldd;

import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.AbstractUITest;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TMLArchiPanel;

import java.io.File;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 * Class CPNodeUpdateConfigurationTest
 * Test for reconfiguration of CP node after renaming HW nodes
 * Creation: 15/05/2019
 * @version 1.0 15/05/2019
 * @author Minh Hiep PHAM
 */

public class CPNodeUpdateConfigurationTest extends AbstractUITest {

    private static final String ARCHITECTURE = "Architecture";
    private static final String RENAMED_FPGA = "renamedFPGA";
    private static final String RENAMED_CPU = "renamedCPU";
    private static final String RENAMED_HWA = "renamedHWA";
    private static final String RENAMED_DMA = "renamedDMA";
    private static final String RENAMED_MEMORY = "renamedMemory";
    private static final String RENAMED_BUS = "renamedBUS";
    private static final String RENAMED_BRIDGE = "renamedBRIDGE";

    private static final String ORIGINAL_FPGA = "FPGA";
    private static final String ORIGINAL_CPU = "CPU";
    private static final String ORIGINAL_HWA = "HWA";
    private static final String ORIGINAL_DMA = "DMA";
    private static final String ORIGINAL_MEMORY = "Memory";
    private static final String ORIGINAL_BUS = "Bus1";
    private static final String ORIGINAL_BRIDGE = "Bridge";
    private static final String ORIGINAL_CP = "CP";

    private static final String INPUT_MODEL = "/ui/tmldd/input/testCPNodeMapping_RenameHwNode.xml";

    private Vector<String> oldMappedUnits;
    private TMLArchiPanel archiPanel;
    private TMLArchiDiagramPanel archiDiagramPanel;
    private TMLArchiCPNode cpNode;
    private TMLArchiCPUNode cpuNode;
    private TMLArchiDMANode dmaNode;
    private TMLArchiFPGANode fpgaNode;
    private TMLArchiHWANode hwaNode;
    private TMLArchiBridgeNode bridgeNode;
    private TMLArchiMemoryNode memoryNode;
    private TMLArchiBUSNode busNode;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + INPUT_MODEL;
    }

    public CPNodeUpdateConfigurationTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() throws Exception {

        // Created the original mapped units (before renaming the HW nodes)
        oldMappedUnits = new Vector<>();
        oldMappedUnits.add("Double_DMA_Transfer.CPU_Controller_1 : FPGA");
        oldMappedUnits.add("Double_DMA_Transfer.CPU_Controller_2 : HWA");
        oldMappedUnits.add("Double_DMA_Transfer.DMA_Controller_1 : DMA");
        oldMappedUnits.add("Double_DMA_Transfer.DMA_Controller_2 : CPU");
        oldMappedUnits.add("Double_DMA_Transfer.Src_Storage_Instance_1 : Memory");
        oldMappedUnits.add("Double_DMA_Transfer.Dst_Storage_Instance_2 : Memory");
        oldMappedUnits.add("Double_DMA_Transfer.Src_Storage_Instance_2 : Memory");
        oldMappedUnits.add("Double_DMA_Transfer.Dst_Storage_Instance_1 : Memory");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_1 : Bridge, Bus1, Bus3");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_3 : Bus1");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_2 : Bus1");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_4 : Bridge, Bus1, Bus3");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_5 : Bus2, Bridge, Bus1");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_6 : Bus1");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_7 : Bus1");
        oldMappedUnits.add("Double_DMA_Transfer.Transfer_Instance_8 : Bus2, Bridge, Bus1");


        archiPanel = findArchiPanel(ARCHITECTURE);
        for (TDiagramPanel _tdp: archiPanel.getPanels()) {
            if (_tdp instanceof TMLArchiDiagramPanel) {
                archiDiagramPanel = (TMLArchiDiagramPanel) _tdp;
                break;
            }
        }

        if (archiDiagramPanel != null) {
            for (TGComponent tgc: archiDiagramPanel.getComponentList()) {
                if (tgc.getName().equals(ORIGINAL_CPU)) {
                    cpuNode = (TMLArchiCPUNode) tgc;
                    //TraceManager.addDev("cpu name : " + cpuNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_CP)) {
                    cpNode = (TMLArchiCPNode) tgc;
                    //TraceManager.addDev("cp name : " + cpNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_DMA)) {
                    dmaNode = (TMLArchiDMANode) tgc;
                    //TraceManager.addDev("dma name : " + dmaNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_MEMORY)) {
                    memoryNode = (TMLArchiMemoryNode) tgc;
                    //TraceManager.addDev("memory name : " + memoryNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_HWA)) {
                    hwaNode = (TMLArchiHWANode) tgc;
                    //TraceManager.addDev("hwa name : " + hwaNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_FPGA)) {
                    fpgaNode = (TMLArchiFPGANode) tgc;
                    //TraceManager.addDev("fpga name : " + fpgaNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_BUS)) {
                    busNode = (TMLArchiBUSNode) tgc;
                    //TraceManager.addDev("bus name : " + busNode.getNodeName());
                }

                if (tgc.getName().equals(ORIGINAL_BRIDGE)) {
                    bridgeNode = (TMLArchiBridgeNode) tgc;
                    //TraceManager.addDev("bridge name : " + bridgeNode.getNodeName());
                }
            }
        }
    }

    @Test
    public void FPGATest() {
        fpgaNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_FPGA);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_FPGA," " + RENAMED_FPGA)));
        }
    }

    @Test
    public void HWATest() {
        hwaNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_HWA);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_HWA," " + RENAMED_HWA)));
        }
    }

    @Test
    public void DMATest() {
        dmaNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_DMA);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            TraceManager.addDev("oldstr : " + oldStr + "    newstr : " +  newStr + "  oldString.replace : " + oldStr.replaceAll(ORIGINAL_DMA,
                    RENAMED_DMA));
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_DMA," " + RENAMED_DMA)));
        }

    }

    @Test
    public void CPUTest() {
        cpuNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_CPU);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_CPU," " + RENAMED_CPU)));
        }
    }

    @Test
    public void MemoryTest() {
        memoryNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_MEMORY);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_MEMORY," " + RENAMED_MEMORY)));
        }

    }

    @Test
    public void BusTest() {
       busNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_BUS);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_BUS," " + RENAMED_BUS)));
        }
    }

    @Test
    public void BridgeTest() {
       bridgeNode.updateCPNodeConfigAfterRenamingHWNode(RENAMED_BRIDGE);
        assertTrue(oldMappedUnits.size() == cpNode.getMappedUnits().size());
        for (int i = 0; i < oldMappedUnits.size(); i++) {
            String oldStr = oldMappedUnits.get(i);
            String newStr = cpNode.getMappedUnits().get(i);
            assertTrue(newStr.equals(oldStr.replaceAll(" " + ORIGINAL_BRIDGE," " + RENAMED_BRIDGE)));
        }
    }

}
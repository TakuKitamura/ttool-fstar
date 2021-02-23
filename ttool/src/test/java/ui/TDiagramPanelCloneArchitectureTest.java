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


package ui;

import myutil.TraceManager;
import org.junit.*;
import tmltranslator.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.*;


import static org.junit.Assert.*;


/**
 * Testing the clone function on architecture
 * author : Minh Hiep
 * update test : 18/10/2019
 */
public class TDiagramPanelCloneArchitectureTest extends AbstractUITest {

    private static final String PATH_TO_EXPECTED_MODEL = "/ui/diagram2tml/expected/Expected_CloneArchitecture.xml";
    private static final String PATH_TO_SOURCE_MODEL = "/ui/diagram2tml/input/CloneCompositeComponentTest.xml";
    private static String EXPECTED_FILE_MODEL;

    private final TMLMapping tmlMapping_exp;
    private TDiagramPanel architecture_clone;
    private TGComponent CPU_Cl;
    private TGComponent DMA_Cl;
    private TGComponent FPGA_Cl;
    private TGComponent HWA_Cl;
    private TGComponent Bus_Cl;
    private TGComponent Bridge_Cl;
    private TGComponent Memory_Cl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + PATH_TO_SOURCE_MODEL;
        EXPECTED_FILE_MODEL = getBaseResourcesDir() + PATH_TO_EXPECTED_MODEL;
    }

    public TDiagramPanelCloneArchitectureTest() {
        super();

        // Open expected model
        mainGUI.openProjectFromFile(new File(EXPECTED_FILE_MODEL));

        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLArchiPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof TMLArchiDiagramPanel) {
                        mainGUI.selectTab(tdp);
                        break;
                    }
                }
                break;
            }
        }


        mainGUI.checkModelingSyntax(true);
        tmlMapping_exp = mainGUI.gtm.getTMLMapping();

        // Open testing model
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() {
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLArchiPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof TMLArchiDiagramPanel) {
                        architecture_clone = tdp;
                        mainGUI.selectTab(architecture_clone);
                        break;
                    }
                }
                break;
            }
        }

        assertNotNull(architecture_clone);

        for (TGComponent tgc : architecture_clone.getAllComponentList()) {

            if (tgc.getName().equals("CPU1")) {
                CPU_Cl = tgc;
            }

            if (tgc.getName().equals("FPGA0")) {
                FPGA_Cl = tgc;
            }

            if (tgc.getName().equals("Bridge0")) {
                Bridge_Cl = tgc;
            }

            if (tgc.getName().equals("HWA0")) {
                HWA_Cl = tgc;
            }

            if (tgc.getName().equals("Bus0")) {
                Bus_Cl = tgc;
            }

            if (tgc.getName().equals("DMA0")) {
                DMA_Cl = tgc;
            }

            if (tgc.getName().equals("Memory0")) {
                Memory_Cl = tgc;
            }
        }

    }

    public void cloneHwNodesOfTestingModel(){
        architecture_clone.cloneComponent(CPU_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("CPU1")) {
                tgc.setName("CPU2");
                break;
            }
        }

        architecture_clone.cloneComponent(FPGA_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("FPGA0")) {
                tgc.setName("FPGA1");
                break;
            }
        }

        architecture_clone.cloneComponent(Bridge_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("Bridge0")) {
                tgc.setName("Bridge1");
                break;
            }
        }

        architecture_clone.cloneComponent(Bus_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("Bus0")) {
                tgc.setName("Bus1");
                break;
            }
        }

        architecture_clone.cloneComponent(DMA_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("DMA0")) {
                tgc.setName("DMA1");
                break;
            }
        }

        architecture_clone.cloneComponent(Memory_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("Memory0")) {
                tgc.setName("Memory1");
                break;
            }
        }

        architecture_clone.cloneComponent(HWA_Cl);
        for (TGComponent tgc : architecture_clone.getComponentList()) {
            if (tgc.getName().equals("HWA0")) {
                tgc.setName("HWA1");
                break;
            }
        }


    }


    @Test
    public void testTMLMapping() {
        cloneHwNodesOfTestingModel();
        mainGUI.checkModelingSyntax(true);
        TMLMapping tmlMapping_clone = mainGUI.gtm.getTMLMapping();
        assertTrue(tmlMapping_clone.equalSpec(tmlMapping_exp));
    }

}
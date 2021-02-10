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


package ui.atd;

import attacktrees.AttackTree;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.AbstractUITest;
import ui.AttackTreePanel;
import ui.TDiagramPanel;
import ui.TURTLEPanel;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * Testing the clone function on architecture
 * author : Ludovic Apvrille
 * update test : 08/02/2021
 */
public class AttackTreeAnalysisTest extends AbstractUITest {

    private static final String PATH_TO_SOURCE_MODEL = "/ui/atd/input/testAttackTreeAnalysis.xml";

    private static final double RESULTS[] = {0.8, 0.6, 0.8, 0.2};


    public AttackTreeAnalysisTest() {
        super();

        // Open expected model
        System.out.println("File: " + RESOURCES_DIR);
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));


    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + PATH_TO_SOURCE_MODEL;
    }



    @Test
    public void testAttackTreeAnalysis() {
        int cpt = 0;

        assertFalse(mainGUI == null);
        assertFalse(mainGUI.getTabs() == null);

        for (TURTLEPanel _tab : mainGUI.getTabs()) {
            if (_tab instanceof AttackTreePanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof AttackTreeDiagramPanel) {
                        mainGUI.selectTab(tdp);
                        System.out.println("Tab:" + tdp.getName());
                        analyse(cpt);
                        cpt ++;
                    }
                }
                break;
            }
        }
    }

    public void analyse(int cpt) {
        assertTrue(mainGUI.checkModelingSyntax(true));
        AttackTree at  = mainGUI.runAttackTreeAnalysis();
        assertFalse(at == null);
        ArrayList<Double> res = at.analyse();
        assertTrue(res.size() == 1);
        Double d0 = res.get(0);
        System.out.println("d0=" + d0 + " results:" + RESULTS[cpt]);
        assertTrue(d0.doubleValue() == RESULTS[cpt]);
    }

}
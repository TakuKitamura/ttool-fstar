/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;

import ddtranslatorSoclib.AvatarCoproMWMR;//DG 30.04.2018

import java.util.LinkedList;

import syscamstranslator.*;
import ui.MainGUI;
import ui.SysCAMSPanelTranslator;
import ui.syscams.SysCAMSComponentTaskDiagramPanel;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Arrays;

public class Simulation {

    protected MainGUI mgui;// DG 28.11.2019

    private static String simulation;

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    public Simulation() {
    }

    public static String getSimulation(Vector<SysCAMSComponentTaskDiagramPanel> listsyscamspanel) {
        int network_io = 0;
        simulation = CR;

        /* Clocks are to be declared in the sc_main in the topcell */

        for (AvatarCoproMWMR copro : TopCellGenerator.avatardd.getAllCoproMWMR()) {
            // a coprocessor with its FIFO interface built from HWA
            /*
             * netlist = netlist +"hwa"+i+".p_clk(signal_clk);" + CR; netlist = netlist
             * +"hwa"+i+".p_resetn(signal_resetn);" + CR; netlist = netlist
             * +"hwa"+i+".p_from_ctrl["+i+"](signal_fifo_"+i+"_from_ctrl);" + CR; netlist =
             * netlist +"hwa"+i+".p_to_ctrl["+i+"](signal_fifo_"+i+"_to_ctrl);" + CR2;
             */

            // IE and OE are special cases as they have VCI an fifo initiator interface!!!
            // In that case, another main (for Network_IO) is substituted

            if ((copro.getCoprocType() == 0) || (copro.getCoprocType() == 1)) {
                network_io = 1;
            }
        }

        if ((network_io == 0)) {
            simulation = CR2 + CR2 + "/***************************************************************************" + CR
                    + "----------------------------simulation-------------------------" + CR
                    + "***************************************************************************/" + CR2;
            simulation = simulation + "int sc_main (int argc, char *argv[])" + CR + "{" + CR;

            /* loop over all panels and all clocks */

            // Vector<SysCAMSComponentTaskDiagramPanel> syscamsDiagramPanels =
            // mgui.getListSysCAMSPanel();

            if (listsyscamspanel != null) {

                for (SysCAMSComponentTaskDiagramPanel syscamsDiagramPanel : listsyscamspanel) {
                    SysCAMSPanelTranslator syscamspaneltranslator = new SysCAMSPanelTranslator(syscamsDiagramPanel);
                    SysCAMSSpecification syscalsspec = syscamspaneltranslator.getSysCAMSSpecification();
                    LinkedList<SysCAMSTClock> clocks = syscalsspec.getAllClock();

                    for (SysCAMSTClock t : clocks) {

                        String unitString = "SC_SEC";
                        String unitStartTimeString = "SC_SEC";

                        if (t.getUnit().equals("s"))
                            unitString = "SC_SEC";
                        if (t.getUnitStartTime().equals("s"))
                            unitStartTimeString = "SC_SEC";
                        if (t.getUnit().equals("ms"))
                            unitString = "SC_MS";
                        if (t.getUnitStartTime().equals("ms"))
                            unitStartTimeString = "SC_MS";
                        if (t.getUnit().equals("\u03BCs"))
                            unitString = "SC_US";
                        if (t.getUnitStartTime().equals("\u03BCs"))
                            unitStartTimeString = "SC_US";
                        if (t.getUnit().equals("ns"))
                            unitString = "SC_NS";
                        if (t.getUnitStartTime().equals("ns"))
                            unitStartTimeString = "SC_NS";
                        simulation = simulation + "\t  sc_clock " + t.getName() + " (\"" + t.getName() + "\","
                                + t.getFrequency() + "," + unitString + "," + t.getDutyCycle() + "," + t.getStartTime()
                                + "," + unitStartTimeString + "," + t.getPosFirst() + ");" + CR;
                    }
                }
            }

            simulation = simulation + "       try {" + CR + "         return _main(argc, argv);" + CR + "    }" + CR2;
            simulation = simulation + "       catch (std::exception &e) {" + CR
                    + "            std::cout << e.what() << std::endl;" + CR + "            throw;" + CR + "    }";
            simulation = simulation + " catch (...) {" + CR;
            simulation = simulation + "std::cout << \"Unknown exception occured\" << std::endl;" + CR;
            simulation = simulation + "throw;" + CR;
            simulation = simulation + "}" + CR;
            simulation = simulation + CR + "       return 1;" + CR + "}";
            // return simulation;
        }

        return simulation;
    }
}

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

/* This class generates the lines of the topcell where the signals are declared*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package syscamstranslator.toSysCAMS;

import ddtranslatorSoclib.AvatarCoproMWMR;
import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTTY;

public class Signal {

	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String NAME_CLK = "signal_clk";
	private static final String NAME_RST = "signal_resetn";

	public static String getSignal() {
		int nb_clusters = TopCellGenerator.syscams.getAllCrossbar().size();
		// nb_clusters=2;
		String signal = CR2 + "//-------------------------------signaux------------------------------------" + CR2;

		signal = signal + "caba::VciSignals<vci_param> signal_vci_m[cpus.size() + 1];" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_xicu(\"signal_vci_xicu\");" + CR;

		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcifdtrom(\"signal_vci_vcifdtrom\");" + CR;
		signal = signal + " caba::VciSignals<vci_param> signal_vci_vcihetrom(\"signal_vci_vcihetrom\");" + CR;
		signal = signal + " caba::VciSignals<vci_param> signal_vci_vcirom(\"signal_vci_vcirom\");" + CR;
		signal = signal + " caba::VciSignals<vci_param> signal_vci_vcisimhelper(\"signal_vci_vcisimhelper\");" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcirttimer(\"signal_vci_vcirttimer\");" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcilocks(\"signal_vci_vcilocks\");" + CR;
		// signal = signal +"caba::VciSignals<vci_param>
		// signal_vci_mwmr_ram(\"signal_vci_mwmr_ram\");"+ CR;
		// signal = signal +"caba::VciSignals<vci_param>
		// signal_vci_mwmrd_ram(\"signal_vci_mwmrd_ram\");"+ CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcifdaccessi;" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcifdaccesst;" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_bdi;" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_bdt;" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_etherneti;" + CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_ethernett;" + CR;
		signal = signal + "" + CR;
		signal = signal + "sc_clock signal_clk(\"signal_clk\");" + CR;
		signal = signal + "sc_signal<bool>  signal_resetn(\"" + NAME_RST + "\");" + CR2;
		int i = 0;
		for (AvatarCoproMWMR copro : TopCellGenerator.syscams.getAllCoproMWMR()) {
			signal = signal + "caba::VciSignals<vci_param> signal_mwmr_" + i + "_initiator;" + CR;
			signal = signal + "caba::VciSignals<vci_param> signal_mwmr_" + i + "_target;" + CR;
			signal = signal + " soclib::caba::FifoSignals<uint32_t> signal_fifo_" + i + "_from_ctrl;" + CR;
			signal = signal + " soclib::caba::FifoSignals<uint32_t> signal_fifo_" + i + "_to_ctrl;" + CR;
			i++;
		}

		if (TopCellGenerator.syscams.getAllCrossbar().size() == 0) {
			for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM())
				signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_vciram" + ram.getIndex()
						+ "(\"signal_vci_vciram" + ram.getIndex() + "\");" + CR2;
			i = 0;

			for (AvatarTTY tty : TopCellGenerator.syscams.getAllTTY()) {
				// signal = signal + "soclib::caba::VciSignals<vci_param>
				// signal_vci_tty"+tty.getNo_tty()+"(\"signal_vci_tty"+tty.getNo_tty()+"\");" +
				// CR2;
				signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_tty" + i + "(\"signal_vci_tty" + i
						+ "\");" + CR2;
				i++;
			}

			signal = signal + " sc_core::sc_signal<bool> signal_xicu_irq[xicu_n_irq];" + CR2;
			System.out.print("number of processors : " + TopCellGenerator.syscams.getNbCPU() + "\n");
		}

		else {
			for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM())
				signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_vciram" + ram.getIndex()
						+ "(\"signal_vci_vciram" + ram.getIndex() + "\");" + CR2;
			i = 0;
			for (AvatarTTY tty : TopCellGenerator.syscams.getAllTTY()) {
				// signal = signal + "soclib::caba::VciSignals<vci_param>
				// signal_vci_tty"+tty.getNo_tty()+"(\"signal_vci_tty"+tty.getNo_tty()+"\");" +
				// CR2;
				signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_tty" + i + "(\"signal_vci_tty" + i
						+ "\");" + CR2;
				i++;
			}
			int p = 0;
			// if (with_hw_accellerator>0){ //DG 23.08.
			for (AvatarCoproMWMR HWAccelerator : TopCellGenerator.syscams.getAllCoproMWMR()) {
				// les accellerateurs sont caches car apparaissent uniquement au niveau
				// DIPLODOCUS
				// signal = signal + " soclib::caba::VciSignals<vci_param>
				// signal_mwmr"+HWAccelerator.getNo()+"_target(\"signal_mwmr"+HWAccelerator.getNo()+"_target\""+CR;
				// signal = signal + " soclib::caba::VciSignals<vci_param>
				// signal_mwmr"+HWAccelerator.getNo()+"_initiator(\"signal_mwmr"+HWAccelerator.getNo()+"_initiator\""
				// +CR;

				// signal = signal + " soclib::caba::FifoSignals<uint32_t>
				// signal_fifo_to_ctrl"+HWAccelerator.getNo()+"(\"signal_fifo_to_ctrl"+HWAccelerator.getNo()+"\");"+CR;
				// signal = signal + " soclib::caba::FifoSignals<uint32_t>
				// signal_fifo_from_ctrl"+HWAccelerator.getNo()+"(\"signal_fifo_from_ctrl"+HWAccelerator.getNo()+"\");"+CR;

				signal = signal + "	soclib::caba::VciSignals<vci_param> signal_mwmr" + p + "_target(\"signal_mwmr" + p
						+ "_target\"" + CR;
				signal = signal + "	soclib::caba::VciSignals<vci_param> signal_mwmr" + p + "_initiator(\"signal_mwmr"
						+ p + "_initiator\"" + CR;

				signal = signal + "	soclib::caba::FifoSignals<uint32_t> signal_fifo_to_ctrl" + p
						+ "(\"signal_fifo_to_ctrl" + p + "\");" + CR;
				signal = signal + "       soclib::caba::FifoSignals<uint32_t> signal_fifo_from_ctrl" + p
						+ "(\"signal_fifo_from_ctrl" + p + "\");" + CR;
				p++;
			}

			signal = signal + " sc_core::sc_signal<bool> signal_xicu_irq[xicu_n_irq];" + CR2;
			// System.out.print("number of processors : " +
			// TopCellGenerator.avatardd.getNbCPU()+"\n");
			System.out.print("number of clusters : " + TopCellGenerator.syscams.getNbClusters() + "\n");

		}
		return signal;
	}
}

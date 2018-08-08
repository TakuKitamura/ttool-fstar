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

/* this class produces the lines containing essentially the initial #includes; we include all potential components event if they are not used in the deployment diagram*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package elntranslator.toELN;

import java.util.LinkedList;
import elntranslator.*;

/**
 * Class ClusterCode 
 * Principal code of a cluster component 
 * Creation: 31/07/2018
 * @version 1.0 31/07/2018
 * @author Irina Kit Yan LEE
 */

public class ClusterCode {
	static private String corpsCluster;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	ClusterCode() {
	}

	public static String getClusterCode(ELNTCluster cluster, LinkedList<ELNTConnector> connectors) {
		LinkedList<String> ELNnames = new LinkedList<String>();

		if (cluster != null) {
			LinkedList<ELNTModule> modules = cluster.getModule();

			corpsCluster = "#include <systemc-ams>" + CR;

			for (int i = 0; i < modules.size(); i++) {
				corpsCluster = corpsCluster + "#include \"" + modules.get(i).getName() + ".h\"" + CR;
				if (i == modules.size()-1) {
					corpsCluster = corpsCluster + CR;
				}
			}
			corpsCluster = corpsCluster + "int sc_main(int argc, char *argv[])" + CR + "{" + CR;

			for (int i = 0; i < connectors.size(); i++) {
				if (connectors.get(i).getName().equals("")) {
					if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + ((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("n_" + ((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + ((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterTerminal) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("n_" + ((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterTerminal) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + ((ELNTClusterTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("n_" + ((ELNTClusterTerminal) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortDE && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterPortDE) {
						corpsCluster = corpsCluster + "\tsc_core::sc_signal " + "s_" + ((ELNTModulePortDE) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterPortDE) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("s_" + ((ELNTModulePortDE) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterPortDE) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterPortDE && connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortDE) {
						corpsCluster = corpsCluster + "\tsc_core::sc_signal " + "s_" + ((ELNTClusterPortDE) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModulePortDE) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("s_" + ((ELNTClusterPortDE) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModulePortDE) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterPortTDF) {
						corpsCluster = corpsCluster + "\tsca_tdf::sca_signal " + "s_" + ((ELNTModulePortTDF) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterPortTDF) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("s_" + ((ELNTModulePortTDF) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTClusterPortTDF) connectors.get(i).get_p2().getComponent()).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterPortTDF && connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF) {
						corpsCluster = corpsCluster + "\tsca_tdf::sca_signal " + "s_" + ((ELNTClusterPortTDF) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModulePortTDF) connectors.get(i).get_p2().getComponent()).getName() + ";" + CR;
						ELNnames.add("s_" + ((ELNTClusterPortTDF) connectors.get(i).get_p1().getComponent()).getName() + "_" + ((ELNTModulePortTDF) connectors.get(i).get_p2().getComponent()).getName());
					}
				} else {
					if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("n_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("n_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterTerminal && connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
						corpsCluster = corpsCluster + "\tsca_eln::sca_node " + "n_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("n_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortDE && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterPortDE) {
						corpsCluster = corpsCluster + "\tsc_core::sc_signal " + "s_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("s_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterPortDE && connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortDE) {
						corpsCluster = corpsCluster + "\tsc_core::sc_signal " + "s_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("s_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF && connectors.get(i).get_p2().getComponent() instanceof ELNTClusterPortTDF) {
						corpsCluster = corpsCluster + "\tsca_tdf::sca_signal " + "s_" +connectors.get(i).getName() + ";" + CR;
						ELNnames.add("s_" + connectors.get(i).getName());
					} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTClusterPortTDF && connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF) {
						corpsCluster = corpsCluster + "\tsca_tdf::sca_signal " + "s_" + connectors.get(i).getName() + ";" + CR;
						ELNnames.add("s_" + connectors.get(i).getName());
					}
				}
				if (i == connectors.size()-1) {
					corpsCluster = corpsCluster + CR;
				}
			}

			for (ELNTModule t : modules) {
				corpsCluster = corpsCluster + "\t" + t.getName() + " i_" + t.getName() + "(\"i_" + t.getName() + "\");" + CR;

				LinkedList<ELNTModuleTerminal> term = t.getModuleTerminal();
				LinkedList<ELNTModulePortTDF> portTDF = t.getModulePortTDF();
				LinkedList<ELNTModulePortDE> portDE = t.getModulePortDE();

				for (ELNTModuleTerminal p : term) {
					for (int i = 0; i < connectors.size(); i++) {
						if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal) {
							if (((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName()) && ((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
						if (connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
							if (((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName()) && ((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
					}
				}

				for (ELNTModulePortTDF p : portTDF) {
					for (int i = 0; i < connectors.size(); i++) {
						if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF) {
							if (((ELNTModulePortTDF) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName()) && ((ELNTModulePortTDF) connectors.get(i).get_p1().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
						if (connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF) {
							if (((ELNTModulePortTDF) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName()) && ((ELNTModulePortTDF) connectors.get(i).get_p2().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
					}
				}
				
				for (ELNTModulePortDE p : portDE) {
					for (int i = 0; i < connectors.size(); i++) {
						if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortDE) {
							if (((ELNTModulePortDE) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName()) && ((ELNTModulePortDE) connectors.get(i).get_p1().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
						if (connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortDE) {
							if (((ELNTModulePortDE) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName()) && ((ELNTModulePortDE) connectors.get(i).get_p2().getComponent()).getModule().getName().equals(t.getName())) {
								corpsCluster = corpsCluster + "\ti_" + t.getName() + "." + p.getName() + "(" + ELNnames.get(i) + ");" + CR;
							}
						}
					}
				}
				corpsCluster = corpsCluster + CR;
			}

			corpsCluster = corpsCluster + "\tsca_util::sca_trace_file* tfp = sca_util::sca_create_tabular_trace_file(\"" + cluster.getName() + "_tb\");" + CR;

			for (int i = 0; i < connectors.size(); i++) {
				corpsCluster = corpsCluster + "\tsca_util::sca_trace(tfp, "+ ELNnames.get(i) + ", \"" + ELNnames.get(i) + "\");" + CR;
			}
			
			corpsCluster = corpsCluster + CR + "\tsc_start(100.0, sc_core::SC_MS);" + CR2
					+ "\tsca_util::sca_close_tabular_trace_file(tfp);" + CR
					+ "\tsc_core::sc_stop();" + CR + "\treturn 0;" + CR + "}" + CR2;
		} else {
			corpsCluster = "";
		}
		return corpsCluster;
	}
}
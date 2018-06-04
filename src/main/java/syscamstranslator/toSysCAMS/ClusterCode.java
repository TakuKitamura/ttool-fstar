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

package syscamstranslator.toSysCAMS;

import java.util.LinkedList;

import syscamstranslator.*;

/**
 * Class ClusterCode
 * Principal code of a cluster component
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class ClusterCode {
	static private String corpsCluster;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	ClusterCode() {}

	public static String getClusterCode(SysCAMSTCluster cluster, LinkedList<SysCAMSTConnector> connectors) {
		int nb_con = 1;
		int nb_block = 1;
		
		if (cluster != null) {
			LinkedList<SysCAMSTBlockTDF> tdf = cluster.getBlockTDF();
			
			corpsCluster = "// Simulation entry point." + CR + "int sc_main(int argc, char *argv[]) {" + CR2 
					+ "\tusing namespace sc_core;" + CR + "\tusing namespace sca_util;" + CR2;
			
			corpsCluster = corpsCluster + "\t// Declare signal to interconnect." + CR;
			
			for (SysCAMSTConnector c : connectors) {
				corpsCluster = corpsCluster + "\tsca_tdf::sca_signal<" + ((SysCAMSTPortTDF) c.get_p1().getComponent()).getTDFType() + "> " 
						+ "sig_" + nb_con + "(\"" 
						+ "sig_" + nb_con + "\");" + CR;
				nb_con++;
			}
			 
			corpsCluster = corpsCluster + CR + "\t// Instantiate headers files as well as bind their ports to the signal." + CR;
			
			for (SysCAMSTBlockTDF t : tdf) {
				corpsCluster = corpsCluster + "\t" + t.getName() + " " + t.getName() + "_" + nb_block + "(\"" + t.getName() + "_" + nb_block + "\");" + CR;
				
				LinkedList<SysCAMSTPortTDF> port = t.getPortTDF();
				for (SysCAMSTPortTDF p : port) {
					corpsCluster = corpsCluster + "\t" + t.getName() + "_" + nb_block + "." + p.getName() + "(";
					for (int i = 0; i < connectors.size(); i++) {
						nb_con = i+1;
						if (((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())) {
							corpsCluster = corpsCluster + "sig_" + nb_con + ");" + CR;
						} else if (((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())) {
							corpsCluster = corpsCluster + "sig_" + nb_con + ");" + CR;
						}
					}
				}
				corpsCluster = corpsCluster + CR;
				nb_block++;
			}
			
			corpsCluster = corpsCluster + "\t// Configure signal tracing." + CR 
					+ "\tsca_trace_file* tfp = sca_create_tabular_trace_file(\"" + cluster.getClusterName() + "_tb\");" + CR;
			
			nb_con = 1;
			for (int i = 0; i < connectors.size(); i++) {
				corpsCluster = corpsCluster + "\tsca_trace(tfp, "+ "sig_" + nb_con + ", \"" + "sig_" + nb_con + "\");";
				nb_con++;
			}
			corpsCluster = corpsCluster + CR2 + "\t// Start simulation." + CR + "\tsc_start(100.0, SC_MS);" + CR2
					+ "\t// Close trace file and stop simulation to enable clean-up by" + CR
					+ "\t// asking SystemC to execute all end_of_simulation() callbacks." + CR
					+ "\tsca_close_tabular_trace_file(tfp);" + CR
					+ "\tsc_stop();" + CR + "\treturn 0;" + CR + "}" + CR2;
		} else {
			corpsCluster = "";
		}
		return corpsCluster;
	}
}

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

import java.util.List;

import syscamstranslator.*;
import ui.syscams.SysCAMSBlockTDF;
import ui.syscams.SysCAMSPortConverter;
import ui.syscams.SysCAMSPortTDF;

/**
 * Class Body
 * Body of .h et .cpp files
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class Corps {
	static private String bodyPrimitive;
	static private String bodyCluster;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	Corps() {}

	public static String getPrimitiveCorps(SysCAMSTBlockTDF tdf) {
		if (tdf != null) {
			List<SysCAMSPortTDF> tdfports = tdf.getTdfports();
			List<SysCAMSPortConverter> convports = tdf.getConvports();

			bodyPrimitive = "//-------------------------------Corps------------------------------------" + CR2
					+ "SCA_TDF_MODULE(" + tdf.getBlockTDFName() + ") {" + CR2;

			if (!tdfports.isEmpty()) {
				bodyPrimitive = bodyPrimitive + "\t// TDF port declarations" + CR;
				for (SysCAMSPortTDF t : tdfports) {
					if (t.getOrigin() == 0) {
						bodyPrimitive = bodyPrimitive + "\tsca_tdf::sca_in<" + t.getTDFType() + "> " + t.getPortName() + CR;
					} else if (t.getOrigin() == 1) {
						bodyPrimitive = bodyPrimitive + "\tsca_tdf::sca_out<" + t.getTDFType() + "> " + t.getPortName() + CR;
					}
				}
			}
			if (!convports.isEmpty()) {
				bodyPrimitive = bodyPrimitive + "\t// Converter port declarations" + CR;
				for (SysCAMSPortConverter conv : convports) {
					if (conv.getOrigin() == 0) {
						bodyPrimitive = bodyPrimitive + "\tsca_tdf::sca_de::sca_in<" + conv.getConvType() + "> " + conv.getPortName() + CR;
					} else if (conv.getOrigin() == 1) {
						bodyPrimitive = bodyPrimitive + "\tsca_tdf::sca_de::out<" + conv.getConvType() + "> " + conv.getPortName() + CR;
					}
				}
			}

			bodyPrimitive = bodyPrimitive + CR + "\t// Constructor" + CR + "\tSCA_CTOR(" + tdf.getBlockTDFName() + ")" + CR;

			if (!tdfports.isEmpty() || !convports.isEmpty()) {
				bodyPrimitive = bodyPrimitive + ": ";
				if (!tdfports.isEmpty()) {
					for (SysCAMSPortTDF t : tdfports) {
						bodyPrimitive = bodyPrimitive + "\t" + t.getPortName() + "(\"" + t.getPortName() + "\")"+ CR;
					}
				}
				if (!convports.isEmpty()) {
					for (SysCAMSPortConverter conv : convports) {
						bodyPrimitive = bodyPrimitive + "\t" + conv.getPortName() + "(\"" + conv.getPortName() + "\")"+ CR;
					}
				}
				bodyPrimitive = bodyPrimitive + "\t{}" + CR2;
			}

			bodyPrimitive = bodyPrimitive + "\tvoid set_attributes() {" + CR;

			// Block period
			if (tdf.getPeriod() != 0) {
				bodyPrimitive = bodyPrimitive + "\t\t" + "set_timestep(" + tdf.getPeriod() + ", sc_core::SC_MS);" + CR;
			}
			for (SysCAMSPortTDF t : tdfports) {
				if (t.getPeriod() != 0) {
					bodyPrimitive = bodyPrimitive + "\t\t" + t.getPortName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_US);" + CR;
				}
				if (t.getRate() != 0) {
					bodyPrimitive = bodyPrimitive + "\t\t" + t.getPortName() + ".set_rate(" + t.getRate() + ");" + CR;
				}
				if (t.getDelay() != 0) {
					bodyPrimitive = bodyPrimitive + "\t\t" + t.getPortName() + ".set_delay(" + t.getDelay() + ");" + CR;
				}
			}
			// Block processCode
			bodyPrimitive = bodyPrimitive + "\t}" + CR2 + "\t" + tdf.getProcessCode() + CR2 + "};" + CR2 + "# endif"
					+ " // " + tdf.getBlockTDFName().toUpperCase() + "_H";
		} else {
			bodyPrimitive = "";
		}
		return bodyPrimitive;
	}

	public static String getClusterCorps(SysCAMSTCluster cluster) {
		 if (cluster != null) {
			 List<SysCAMSBlockTDF> blocks = cluster.getBlocks();

			 bodyCluster = "//-------------------------------Header------------------------------------" + CR2
						+ "#include <systemc-ams>" + CR2;

			 for (SysCAMSBlockTDF b : blocks) {
				 bodyCluster = bodyCluster + "#include \"" + b.getValue() + ".h\"" + CR;
			 }
			 bodyCluster = bodyCluster + CR;
		 } else {
			 bodyCluster = "";
		 }
		 return bodyCluster;
	}
}

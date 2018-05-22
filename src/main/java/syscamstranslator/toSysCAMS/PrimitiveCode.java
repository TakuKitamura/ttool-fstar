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
 * Class PrimitiveCode
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class PrimitiveCode {
	static private String codePrimitive;
	static private String corpsCluster;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	PrimitiveCode() {}

	public static String getPrimitiveCode(SysCAMSBlockTDF tdf) {
		if (tdf != null) {
			codePrimitive = Header.getPrimitiveHeader(tdf) + Body.getPrimitiveCorps(tdf);
		} else {
			codePrimitive = "";
		}
		return codePrimitive;
	}
	
	public static String getClusterBody(SysCAMSTCluster cluster) {
		 if (cluster != null) {
			 List<SysCAMSBlockTDF> blocks = cluster.getBlocks();
			 
			 corpsCluster = "//-------------------------------Header------------------------------------" + CR2
						+ "#include <systemc-ams>" + CR2;
			 
			 for (SysCAMSBlockTDF b : blocks) {
				 corpsCluster = corpsCluster + "#include \"" + b.getValue() + ".h\"" + CR;
			 }
			 corpsCluster = corpsCluster + CR;
		 } else {
			 corpsCluster = "";
		 }
		 return corpsCluster;
	} 
}
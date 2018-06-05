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
 * Class PrimitiveCode
 * Principal code of a primive component
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
*/

public class PrimitiveCode {
	static private String corpsPrimitive;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	PrimitiveCode() {}

	public static String getPrimitiveCode(SysCAMSTBlockTDF tdf) {
		if (tdf != null) {
			LinkedList<SysCAMSTPortTDF> tdfports = tdf.getPortTDF();
			LinkedList<SysCAMSTPortConverter> convports = tdf.getPortConverter();
			int cpt = 0;
			int cpt2 = 0;
			
			corpsPrimitive = "SCA_TDF_MODULE(" + tdf.getName() + ") {" + CR2;
			
			if (!tdfports.isEmpty()) {
				corpsPrimitive = corpsPrimitive + "\t// TDF port declarations" + CR;
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getOrigin() == 0) {
						corpsPrimitive = corpsPrimitive + "\tsca_tdf::sca_in<" + t.getTDFType() + "> " + t.getName() + ";" + CR;
					} else if (t.getOrigin() == 1) {
						corpsPrimitive = corpsPrimitive + "\tsca_tdf::sca_out<" + t.getTDFType() + "> " + t.getName() + ";" + CR;
					}
				}
			}
			if (!convports.isEmpty()) {
				corpsPrimitive = corpsPrimitive + "\t// Converter port declarations" + CR;
				for (SysCAMSTPortConverter conv : convports) {
					if (conv.getOrigin() == 0) {
						corpsPrimitive = corpsPrimitive + "\tsca_tdf::sca_de::sca_in<" + conv.getConvType() + "> " + conv.getName() + ";" + CR;
					} else if (conv.getOrigin() == 1) {
						corpsPrimitive = corpsPrimitive + "\tsca_tdf::sca_de::sca_out<" + conv.getConvType() + "> " + conv.getName() + ";" + CR;
					}
				}
			}
			
			corpsPrimitive = corpsPrimitive + CR + "\t// Constructor" + CR + "\tSCA_CTOR(" + tdf.getName() + ")" + CR;
		
			if (!tdfports.isEmpty() || !convports.isEmpty()) {
				corpsPrimitive = corpsPrimitive + "\t: ";
				if (!tdfports.isEmpty()) {
					for (int i = 0; i < tdfports.size(); i++) {
						if (tdfports.size() > 1) {
							if (cpt == 0) {
								corpsPrimitive = corpsPrimitive + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
								cpt++;
							} else {
								corpsPrimitive = corpsPrimitive + "\t, " + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
							}
						} else {
							corpsPrimitive = corpsPrimitive + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
							cpt++;
						}
					}
				}
				if (!convports.isEmpty()) {
					for (int i = 0; i < convports.size(); i++) {
						if (convports.size() > 1) {
							if (cpt == 0) {
								corpsPrimitive = corpsPrimitive + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
								cpt++;
							} else {
								corpsPrimitive = corpsPrimitive + "\t, " + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
							}
						} else {
							corpsPrimitive = corpsPrimitive + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
							cpt++;
						}
					}
				}
				corpsPrimitive = corpsPrimitive + "\t{}" + CR2;
			}
			
			// Block period 
			if (tdf.getPeriod() != -1) {
				corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + "set_timestep(" + tdf.getPeriod() + ", sc_core::SC_MS);" + CR;
				cpt2++;
			}	
			if (cpt2 > 0) {
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getPeriod() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
					} 
					if (t.getRate() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
					} 
					if (t.getDelay() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
					} 
				}
			} else {
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getPeriod() != -1) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
						}
					} 
					if (t.getRate() != -1) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
						}
					} 
					if (t.getDelay() != -1) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
						}
					} 
				}
			}
			if (cpt2 > 0) {
				for (SysCAMSTPortConverter t : convports) {
					if (t.getPeriod() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
					} 
					if (t.getRate() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
					} 
					if (t.getDelay() != -1) {
						corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
					} 
				}
			} else {
				for (SysCAMSTPortConverter t : convports) {
					if (t.getPeriod() != -1) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
						}
					} 
					if (t.getRate() != -1 && cpt2 == 0) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
						}
					} 
					if (t.getDelay() != -1 && cpt2 == 0) {
						if (cpt2 == 0) {
							corpsPrimitive = corpsPrimitive + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitive = corpsPrimitive + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
						}
					} 
				}
			}
			
			// Block processCode
			if (cpt2 > 0) {
				corpsPrimitive = corpsPrimitive + "\t}" + CR2;
			}
					
			StringBuffer pcbuf = new StringBuffer(tdf.getProcessCode());
			StringBuffer buffer = new StringBuffer("");
			int tab = 0;
			int begin = 0;
			
	        for(int pos = 0; pos != tdf.getProcessCode().length(); pos++) {
	        	char c = pcbuf.charAt(pos);
	            switch(c) {
	                case '\t':  
	                	begin = 1;
	                	tab++;
	                	break;
	                default:  
	                	if (begin == 1) {
	                		int i = tab;
	                		while (i >= 0) {
	                			buffer.append("\t"); 
	                			i--;
	                		}
	                		buffer.append(pcbuf.charAt(pos)); 	
	                		begin = 0;
	                		tab = 0;
	                	} else {
	                		if (c == '}') {
	                			buffer.append("\t"); 
	                		}
	                		buffer.append(pcbuf.charAt(pos)); 	
	                	}
	                	break;
	            }
	        }
			
	        String pc = buffer.toString();
	        
			corpsPrimitive = corpsPrimitive + "\t" + pc + CR + "};" + CR2 + "#endif"
					+ " // " + tdf.getName().toUpperCase() + "_H";
		} else {
			corpsPrimitive = "";
		}
		return corpsPrimitive;
	}
}

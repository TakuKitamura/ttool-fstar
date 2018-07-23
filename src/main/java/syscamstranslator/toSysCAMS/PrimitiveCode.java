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
	static private String corpsPrimitiveTDF;
	static private String corpsPrimitiveDE;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	PrimitiveCode() {}

	public static String getPrimitiveCodeTDF(SysCAMSTBlockTDF tdf) {
		corpsPrimitiveTDF = "";
		
		if (tdf != null) {
			LinkedList<SysCAMSTPortTDF> tdfports = tdf.getPortTDF();
			LinkedList<SysCAMSTPortConverter> convports = tdf.getPortConverter();
			int cpt = 0;
			int cpt2 = 0;

			if ((!tdf.getTypeTemplate().equals("")) && (!tdf.getNameTemplate().equals("")))  {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "template<" + tdf.getTypeTemplate() + " " + tdf.getNameTemplate() + ">" + CR;
			}
			//corpsPrimitive = "SCA_TDF_MODULE(" + tdf.getName() + ") {" + CR2;
			corpsPrimitiveTDF = corpsPrimitiveTDF + "class " + tdf.getName() + " : public sca_tdf::sca_module {" + CR2 + "public:" + CR;

			if (!tdf.getListTypedef().isEmpty()) {
				for (int i = 0; i < tdf.getListTypedef().getSize(); i++) {
					String select = tdf.getListTypedef().get(i);
					String[] split = select.split(" : ");
					corpsPrimitiveTDF = corpsPrimitiveTDF + "\ttypedef " + split[1] + "<" + tdf.getNameTemplate() + "> " + split[0] + ";" + CR;
					if (i == tdf.getListTypedef().getSize()-1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + CR;
					}
				}
			}
			
			if (tdf.getListStruct().getSize() != 0) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\tstruct parameters {" + CR;

				String identifier, value, type;
				for (int i = 0; i < tdf.getListStruct().size(); i++) {
					String select = tdf.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					value = splitb[0];
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						type = splitc[1];
					} else {
						type = splitc[0];
					}
					corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + type + " " + identifier + ";" + CR;
				}

				corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\tparameters()" + CR;

				for (int i = 0; i < tdf.getListStruct().size(); i++) {
					String select = tdf.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					value = splitb[0];
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						type = splitc[1];
					} else {
						type = splitc[0];
					}
					if (i == 0) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t: " + identifier + "(" + value + ")" + CR;
					} 
					if ((i > 0) && (i < tdf.getListStruct().getSize()-1)) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t, " + identifier + "(" + value + ")" + CR;
					} 
					if (i == tdf.getListStruct().getSize()-1 && i != 0) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t, " + identifier + "(" + value + ")" + CR + "\t\t{}" + CR;
					} else {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t{}" + CR;
					}
				}
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\t};" + CR;
			}

			if (!tdfports.isEmpty()) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + CR;
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getOrigin() == 0) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\tsca_tdf::sca_in<" + t.getTDFType() + "> " + t.getName() + ";" + CR;
					} else if (t.getOrigin() == 1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\tsca_tdf::sca_out<" + t.getTDFType() + "> " + t.getName() + ";" + CR;
					}
				}
			}
			if (!convports.isEmpty()) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + CR;
				for (SysCAMSTPortConverter conv : convports) {
					if (conv.getOrigin() == 0) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\tsca_tdf::sca_de::sca_in<" + conv.getConvType() + "> " + conv.getName() + ";" + CR;
					} else if (conv.getOrigin() == 1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\tsca_tdf::sca_de::sca_out<" + conv.getConvType() + "> " + conv.getName() + ";" + CR;
					}
				}
			}

			//corpsPrimitive = corpsPrimitive + CR + "\t// Constructor" + CR + "\tSCA_CTOR(" + tdf.getName() + ")" + CR;
			corpsPrimitiveTDF = corpsPrimitiveTDF + CR + "\texplicit " + tdf.getName() + "(sc_core::sc_module_name nm";

			if (tdf.getListStruct().getSize() != 0) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + ", const parameters& p = parameters())" + CR;
			} else {
				corpsPrimitiveTDF = corpsPrimitiveTDF + ")" + CR;
			}

			if (!tdfports.isEmpty() || !convports.isEmpty() || !tdf.getListStruct().isEmpty()) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\t: ";
				if (!tdfports.isEmpty()) {
					for (int i = 0; i < tdfports.size(); i++) {
						if (tdfports.size() >= 1) {
							if (cpt == 0) {
								corpsPrimitiveTDF = corpsPrimitiveTDF + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
								cpt++;
							} else {
								corpsPrimitiveTDF = corpsPrimitiveTDF + "\t, " + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
							}
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + tdfports.get(i).getName() + "(\"" + tdfports.get(i).getName() + "\")" + CR;
							cpt++;
						}
					}
				}
				if (!convports.isEmpty()) {
					for (int i = 0; i < convports.size(); i++) {
						if (convports.size() >= 1) {
							if (cpt == 0) {
								corpsPrimitiveTDF = corpsPrimitiveTDF + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
								cpt++;
							} else {
								corpsPrimitiveTDF = corpsPrimitiveTDF + "\t, " + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
							}
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + convports.get(i).getName() + "(\"" + convports.get(i).getName() + "\")" + CR;
							cpt++;
						}
					}
				}
				String identifier;
				if (!tdf.getListStruct().isEmpty()) {
					for (int i = 0; i < tdf.getListStruct().size(); i++) {
						String select = tdf.getListStruct().get(i);
						String[] splita = select.split(" = ");
						identifier = splita[0];
						if (tdf.getListStruct().getSize() > 1) {
							if (cpt == 0) {
								corpsPrimitiveTDF = corpsPrimitiveTDF + identifier + "(p." + identifier + ")" + CR;
								cpt++;
							} else {
								corpsPrimitiveTDF = corpsPrimitiveTDF + "\t, " + identifier + "(p." + identifier + ")" + CR;
							}
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + identifier + "(p." + identifier + ")" + CR;
							cpt++;
						}
					}
				}
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\t{}" + CR2 + "protected:" + CR;
			}

			if (tdf.getPeriod() != -1) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + "set_timestep(" + tdf.getPeriod() + ", sc_core::SC_" + tdf.getTime().toUpperCase() + CR;
				cpt2++;
			}	
			if (cpt2 > 0) {
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getPeriod() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
					} 
					if (t.getRate() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
					} 
					if (t.getDelay() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
					} 
				}
			} else {
				for (SysCAMSTPortTDF t : tdfports) {
					if (t.getPeriod() != -1) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
						}
					} 
					if (t.getRate() != -1) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
						}
					} 
					if (t.getDelay() != -1) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
						}
					} 
				}
			}
			if (cpt2 > 0) {
				for (SysCAMSTPortConverter t : convports) {
					if (t.getPeriod() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
					} 
					if (t.getRate() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
					} 
					if (t.getDelay() != -1) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
					} 
				}
			} else {
				for (SysCAMSTPortConverter t : convports) {
					if (t.getPeriod() != -1) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_timestep(" + t.getPeriod() + ", sc_core::SC_" + t.getTime().toUpperCase() + ");" + CR;
						}
					} 
					if (t.getRate() != -1 && cpt2 == 0) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_rate(" + t.getRate() + ");" + CR;
						}
					} 
					if (t.getDelay() != -1 && cpt2 == 0) {
						if (cpt2 == 0) {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\tvoid set_attributes() {" + CR + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
							cpt2++;
						} else {
							corpsPrimitiveTDF = corpsPrimitiveTDF + "\t\t" + t.getName() + ".set_delay(" + t.getDelay() + ");" + CR;
						}
					} 
				}
			}

			if (cpt2 > 0) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "\t}" + CR2;
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

			corpsPrimitiveTDF = corpsPrimitiveTDF + "\t" + pc + CR;

			if (tdf.getListStruct().getSize() != 0) {
				corpsPrimitiveTDF = corpsPrimitiveTDF + "private:" + CR;

				String identifier, type, constant;
				for (int i = 0; i < tdf.getListStruct().size(); i++) {
					String select = tdf.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						constant = splitc[0];
						type = splitc[1];
					} else {
						constant = "";
						type = splitc[0];
					}
					if (constant.equals("")) {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t" + type + " " + identifier + ";" + CR;
					} else {
						corpsPrimitiveTDF = corpsPrimitiveTDF + "\t" + constant + " " + type + " " + identifier + ";" + CR;
					}
				}
			}
			corpsPrimitiveTDF = corpsPrimitiveTDF + "};" + CR2 + "#endif" + " // " + tdf.getName().toUpperCase() + "_H";
		} else {
			corpsPrimitiveTDF = "";
		}
		return corpsPrimitiveTDF;
	}
	
	public static String getPrimitiveCodeDE(SysCAMSTBlockDE de) {
		corpsPrimitiveDE = "";
		
		if (de != null) {
			LinkedList<SysCAMSTPortDE> deports = de.getPortDE();
			int cpt = 0;
			int cpt2 = 0;

			if ((!de.getTypeTemplate().equals("")) && (!de.getNameTemplate().equals("")))  {
				corpsPrimitiveDE = corpsPrimitiveDE + "template<" + de.getTypeTemplate() + " " + de.getNameTemplate() + ">" + CR;
			}
			//corpsPrimitive = "SCA_TDF_MODULE(" + de.getName() + ") {" + CR2;
			corpsPrimitiveDE = corpsPrimitiveDE + "class " + de.getName() + " : public sca_core::sca_module {" + CR2 + "public:" + CR;

			if (!de.getListTypedef().isEmpty()) {
				for (int i = 0; i < de.getListTypedef().getSize(); i++) {
					String select = de.getListTypedef().get(i);
					String[] split = select.split(" : ");
					corpsPrimitiveDE = corpsPrimitiveDE + "\ttypedef " + split[1] + "<" + de.getNameTemplate() + "> " + split[0] + ";" + CR;
					if (i == de.getListTypedef().getSize()-1) {
						corpsPrimitiveDE = corpsPrimitiveDE + CR;
					}
				}
			}
			
			if (de.getListStruct().getSize() != 0) {
				corpsPrimitiveDE = corpsPrimitiveDE + "\tstruct parameters {" + CR;

				String identifier, value, type;
				for (int i = 0; i < de.getListStruct().size(); i++) {
					String select = de.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					value = splitb[0];
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						type = splitc[1];
					} else {
						type = splitc[0];
					}
					corpsPrimitiveDE = corpsPrimitiveDE + "\t\t" + type + " " + identifier + ";" + CR;
				}

				corpsPrimitiveDE = corpsPrimitiveDE + "\t\tparameters()" + CR;

				for (int i = 0; i < de.getListStruct().size(); i++) {
					String select = de.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					value = splitb[0];
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						type = splitc[1];
					} else {
						type = splitc[0];
					}
					if (i == 0) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t\t: " + identifier + "(" + value + ")" + CR;
					} 
					if ((i > 0) && (i < de.getListStruct().getSize()-1)) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t\t, " + identifier + "(" + value + ")" + CR;
					} 
					if (i == de.getListStruct().getSize()-1 && i != 0) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t\t, " + identifier + "(" + value + ")" + CR + "\t\t{}" + CR;
					} else {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t\t{}" + CR;
					}
				}
				corpsPrimitiveDE = corpsPrimitiveDE + "\t};" + CR;
			}

			if (!deports.isEmpty()) {
				corpsPrimitiveDE = corpsPrimitiveDE + CR;
				for (SysCAMSTPortDE t : deports) {
					if (t.getOrigin() == 0) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\tsca_core::sca_in<" + t.getDEType() + "> " + t.getName() + ";" + CR;
					} else if (t.getOrigin() == 1) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\tsca_core::sca_out<" + t.getDEType() + "> " + t.getName() + ";" + CR;
					}
				}
			}

			corpsPrimitiveDE = corpsPrimitiveDE + CR + "\tSC_HAS_PROCESS(" + de.getName() + ");" + CR + 
			"\texplicit " + de.getName() + "(sc_core::sc_module_name nm";

			if (de.getListStruct().getSize() != 0) {
				corpsPrimitiveDE = corpsPrimitiveDE + ", const parameters& p = parameters())" + CR;
			} else {
				corpsPrimitiveDE = corpsPrimitiveDE + ")" + CR;
			}

			if (!deports.isEmpty() || !de.getListStruct().isEmpty()) {
				corpsPrimitiveDE = corpsPrimitiveDE + "\t: ";
				if (!deports.isEmpty()) {
					for (int i = 0; i < deports.size(); i++) {
						if (deports.size() >= 1) {
							if (cpt == 0) {
								corpsPrimitiveDE = corpsPrimitiveDE + deports.get(i).getName() + "(\"" + deports.get(i).getName() + "\")" + CR;
								cpt++;
							} else {
								corpsPrimitiveDE = corpsPrimitiveDE + "\t, " + deports.get(i).getName() + "(\"" + deports.get(i).getName() + "\")" + CR;
							}
						} else {
							corpsPrimitiveDE = corpsPrimitiveDE + deports.get(i).getName() + "(\"" + deports.get(i).getName() + "\")" + CR;
							cpt++;
						}
					}
				}
				String identifier;
				if (!de.getListStruct().isEmpty()) {
					for (int i = 0; i < de.getListStruct().size(); i++) {
						String select = de.getListStruct().get(i);
						String[] splita = select.split(" = ");
						identifier = splita[0];
						if (de.getListStruct().getSize() >= 1) {
							if (cpt == 0) {
								corpsPrimitiveDE = corpsPrimitiveDE + identifier + "(p." + identifier + ")" + CR;
								cpt++;
							} else {
								corpsPrimitiveDE = corpsPrimitiveDE + "\t, " + identifier + "(p." + identifier + ")" + CR;
							}
						} else {
							corpsPrimitiveDE = corpsPrimitiveDE + identifier + "(p." + identifier + ")" + CR;
							cpt++;
						}
					}
				}
			}

			boolean sensitive = false, method = false;
			if (!de.getCode().equals("")) {
				corpsPrimitiveDE = corpsPrimitiveDE + "\t{" + CR + "\t\tSC_METHOD(" + de.getNameFn() + ");" + CR;
				method = true;
			} 
			
			for (SysCAMSTPortDE t : deports) {
				if (t.getSensitive() == true) {
					if (method == false) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t{" + CR;
					} 
					corpsPrimitiveDE = corpsPrimitiveDE + "\t\tsensitive << " + t.getName() + ".";
					if (t.getSensitiveMethod().equals("positive")) {
						corpsPrimitiveDE = corpsPrimitiveDE + "pos();" + CR;
					} else if (t.getSensitiveMethod().equals("negative")) {
						corpsPrimitiveDE = corpsPrimitiveDE + "neg();" + CR;						
					}
					sensitive = true;
				}
			}
			if (sensitive == true || method == true) {
				corpsPrimitiveDE = corpsPrimitiveDE + "\t}" + CR2;
			} else {
				corpsPrimitiveDE = corpsPrimitiveDE + "\t{}" + CR2;
			}
			
			if (de.getListStruct().getSize() != 0) {
				corpsPrimitiveDE = corpsPrimitiveDE + "private:" + CR;

				String identifier, type, constant;
				for (int i = 0; i < de.getListStruct().size(); i++) {
					String select = de.getListStruct().get(i);
					String[] splita = select.split(" = ");
					identifier = splita[0];
					String[] splitb = splita[1].split(" : ");
					String[] splitc = splitb[1].split(" ");
					if (splitc[0].equals("const")) {
						constant = splitc[0];
						type = splitc[1];
					} else {
						constant = "";
						type = splitc[0];
					}
					if (constant.equals("")) {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t" + type + " " + identifier + ";" + CR;
					} else {
						corpsPrimitiveDE = corpsPrimitiveDE + "\t" + constant + " " + type + " " + identifier + ";" + CR;
					}
				}
			}
			
			StringBuffer pcbuf = new StringBuffer(de.getCode());
			StringBuffer buffer = new StringBuffer("");
			int tab = 0;
			int begin = 0;

			for(int pos = 0; pos != de.getCode().length(); pos++) {
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
			corpsPrimitiveDE = corpsPrimitiveDE + CR + "\t" + pc;
			
			corpsPrimitiveDE = corpsPrimitiveDE + CR + "};" + CR2 + "#endif" + " // " + de.getName().toUpperCase() + "_H";
		} else {
			corpsPrimitiveDE = "";
		}
		return corpsPrimitiveDE;
	}
}
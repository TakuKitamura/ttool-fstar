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

import java.util.*;
import elntranslator.*;

/**
 * Class ModuleCode
 * Principal code of a module
 * Creation: 25/07/2018
 * @version 1.0 25/07/2018
 * @author Irina Kit Yan LEE
 */

public class ModuleCode {
	static private String corpsModule;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	ModuleCode() {}

	public static String getModuleCode(ELNTModule module, List<ELNTConnector> connectors) {
		if (module != null) {
			corpsModule = "#ifndef " + module.getName().toUpperCase() + "_H"+ CR 
					+ "#define " + module.getName().toUpperCase() + "_H" + CR2
					+ "#include <cmath>" + CR + "#include <iostream>" + CR + "#include <systemc-ams>" + CR2;
			
			corpsModule = corpsModule + "SC_MODULE(" + module.getName() + ")" + CR + "{" + CR;
			
			LinkedList<ELNTModuleTerminal> terms = module.getModuleTerminal();
			for (int i = 0; i < terms.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_terminal " + terms.get(i).getName() + ";" + CR;
				if (i == terms.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentCapacitor> capacitors = module.getCapacitor();
			for (int i = 0; i < capacitors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_c " + capacitors.get(i).getName() + ";" + CR;
				if (i == capacitors.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentIdealTransformer> idealTransformers = module.getIdealTransformer();
			for (int i = 0; i < idealTransformers.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_ideal_transformer " + idealTransformers.get(i).getName() + ";" + CR;
				if (i == idealTransformers.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentIndependentCurrentSource> isources = module.getIsource();
			for (int i = 0; i < isources.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_isource " + isources.get(i).getName() + ";" + CR;
				if (i == isources.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentIndependentVoltageSource> vsources = module.getVsource();
			for (int i = 0; i < vsources.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vsource " + vsources.get(i).getName() + ";" + CR;
				if (i == vsources.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentInductor> inductors = module.getInductor();
			for (int i = 0; i < inductors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_l " + inductors.get(i).getName() + ";" + CR;
				if (i == inductors.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}

			LinkedList<ELNTComponentResistor> resistors = module.getResistor();
			for (int i = 0; i < resistors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_r " + resistors.get(i).getName() + ";" + CR;
				if (i == resistors.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentTransmissionLine> transmissionLines = module.getTransmissionLine();
			for (int i = 0; i < transmissionLines.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_transmission_line " + transmissionLines.get(i).getName() + ";" + CR;
				if (i == transmissionLines.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentVoltageControlledCurrentSource> vccss = module.getVccs();
			for (int i = 0; i < vccss.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vccs " + vccss.get(i).getName() + ";" + CR;
				if (i == vccss.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentVoltageControlledVoltageSource> vcvss = module.getVcvs();
			for (int i = 0; i < vcvss.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vcvs " + vcvss.get(i).getName() + ";" + CR;
				if (i == vcvss.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentCurrentSinkTDF> TDF_isinks = module.getTDF_isink();
			for (int i = 0; i < TDF_isinks.size(); i++) {
				//corps = corps + "\tsca_eln::sca_tdf::sca_isink" + TDF_isinks.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_isink " + TDF_isinks.get(i).getName() + ";" + CR;
				if (i == TDF_isinks.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentCurrentSourceTDF> TDF_isources = module.getTDF_isource();
			for (int i = 0; i < TDF_isources.size(); i++) {
				//corps = corps + "\tsca_eln::sca_tdf::sca_isource " + TDF_isources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_isource " + TDF_isources.get(i).getName() + ";" + CR;
				if (i == TDF_isources.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentVoltageSinkTDF> TDF_vsinks = module.getTDF_vsink();
			for (int i = 0; i < TDF_vsinks.size(); i++) {
				//corps = corps + "\tsca_eln::sca_tdf::sca_vsink " + TDF_vsinks.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_vsink " + TDF_vsinks.get(i).getName() + ";" + CR;
				if (i == TDF_vsinks.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			LinkedList<ELNTComponentVoltageSourceTDF> TDF_vsources = module.getTDF_vsource();
			for (int i = 0; i < TDF_vsources.size(); i++) {
				//corps = corps + "\tsca_eln::sca_tdf::sca_vsource " + TDF_vsources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_vsource " + TDF_vsources.get(i).getName() + ";" + CR;
				if (i == TDF_vsources.size()-1) {
					corpsModule = corpsModule + CR;
				}
			}
			
			corpsModule = corpsModule + "\tSC_CTOR(" + module.getName() + ")" + CR;
			
			int cpt = 0;
			for (int i = 0; i < terms.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + terms.get(i).getName() + "(\"" + terms.get(i).getName() + "\")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + terms.get(i).getName() + "(\"" + terms.get(i).getName() + "\")" + CR;
				}
			}
			for (int i = 0; i < capacitors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + capacitors.get(i).getName() + "(\"" + capacitors.get(i).getName() + "\", " + capacitors.get(i).getVal() + ", " + capacitors.get(i).getQ0() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + capacitors.get(i).getName() + "(\"" + capacitors.get(i).getName() + "\", " + capacitors.get(i).getVal() + ", " + capacitors.get(i).getQ0() + ")" + CR;
				}
			}
			for (int i = 0; i < idealTransformers.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + idealTransformers.get(i).getName() + "(\"" + idealTransformers.get(i).getName() + "\", " + idealTransformers.get(i).getRatio() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + idealTransformers.get(i).getName() + "(\"" + idealTransformers.get(i).getName() + "\", " + idealTransformers.get(i).getRatio() + ")" + CR;
				}
			}
			for (int i = 0; i < isources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + isources.get(i).getName() + "(\"" + isources.get(i).getName() + "\", " + isources.get(i).getInitValue() + ", " + isources.get(i).getOffset() 
					  + ", " + isources.get(i).getAmplitude() + ", " + isources.get(i).getFrequency() + ", " + isources.get(i).getPhase() + ", " + isources.get(i).getDelay() 
					  + ", " + isources.get(i).getAcAmplitude() + ", " + isources.get(i).getAcPhase() + ", " + isources.get(i).getAcNoiseAmplitude() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + isources.get(i).getName() + "(\"" + isources.get(i).getName() + "\", " + isources.get(i).getInitValue() + ", " + isources.get(i).getOffset() 
					  + ", " + isources.get(i).getAmplitude() + ", " + isources.get(i).getFrequency() + ", " + isources.get(i).getPhase() + ", " + isources.get(i).getDelay() 
					  + ", " + isources.get(i).getAcAmplitude() + ", " + isources.get(i).getAcPhase() + ", " + isources.get(i).getAcNoiseAmplitude() + ")" + CR;
				}
			}
			for (int i = 0; i < vsources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vsources.get(i).getName() + "(\"" + vsources.get(i).getName() + "\", " + vsources.get(i).getInitValue() + ", " + vsources.get(i).getOffset() 
					  + ", " + vsources.get(i).getAmplitude() + ", " + vsources.get(i).getFrequency() + ", " + vsources.get(i).getPhase() + ", " + vsources.get(i).getDelay() 
					  + ", " + vsources.get(i).getAcAmplitude() + ", " + vsources.get(i).getAcPhase() + ", " + vsources.get(i).getAcNoiseAmplitude() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + vsources.get(i).getName() + "(\"" + vsources.get(i).getName() + "\", " + vsources.get(i).getInitValue() + ", " + vsources.get(i).getOffset() 
					  + ", " + vsources.get(i).getAmplitude() + ", " + vsources.get(i).getFrequency() + ", " + vsources.get(i).getPhase() + ", " + vsources.get(i).getDelay() 
					  + ", " + vsources.get(i).getAcAmplitude() + ", " + vsources.get(i).getAcPhase() + ", " + vsources.get(i).getAcNoiseAmplitude() + ")" + CR;
				}
			}
			for (int i = 0; i < inductors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + inductors.get(i).getName() + "(\"" + inductors.get(i).getName() + "\", " + inductors.get(i).getVal() + ", " + inductors.get(i).getPhi0() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + inductors.get(i).getName() + "(\"" + inductors.get(i).getName() + "\", " + inductors.get(i).getVal() + ", " + inductors.get(i).getPhi0() + ")" + CR;
				}
			}
			for (int i = 0; i < resistors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + resistors.get(i).getName() + "(\"" + resistors.get(i).getName() + "\", " + resistors.get(i).getVal() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + resistors.get(i).getName() + "(\"" + resistors.get(i).getName() + "\", " + resistors.get(i).getVal() + ")" + CR;
				}
			}
			for (int i = 0; i < transmissionLines.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + transmissionLines.get(i).getName() + "(\"" + transmissionLines.get(i).getName() + "\", " + transmissionLines.get(i).getZ0() + ", " + transmissionLines.get(i).getDelay() + ", " + transmissionLines.get(i).getDelta0() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + transmissionLines.get(i).getName() + "(\"" + transmissionLines.get(i).getName() + "\")" + CR;
				}
			}
			for (int i = 0; i < vccss.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vccss.get(i).getName() + "(\"" + vccss.get(i).getName() + "\", " + vccss.get(i).getVal() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + vccss.get(i).getName() + "(\"" + vccss.get(i).getName() + "\", " + vccss.get(i).getVal() + ")" + CR;
				}
			}
			for (int i = 0; i < vcvss.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vcvss.get(i).getName() + "(\"" + vcvss.get(i).getName() + "\", " + vcvss.get(i).getVal() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + vcvss.get(i).getName() + "(\"" + vcvss.get(i).getName() + "\", " + vcvss.get(i).getVal() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_isinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_isinks.get(i).getName() + "(\"" + TDF_isinks.get(i).getName() + "\", " + TDF_isinks.get(i).getScale() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_isinks.get(i).getName() + "(\"" + TDF_isinks.get(i).getName() + "\", " + TDF_isinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_isources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_isources.get(i).getName() + "(\"" + TDF_isources.get(i).getName() + "\", " + TDF_isources.get(i).getScale() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_isources.get(i).getName() + "(\"" + TDF_isources.get(i).getName() + "\", " + TDF_isources.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_vsinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_vsinks.get(i).getName() + "(\"" + TDF_vsinks.get(i).getName() + "\", " + TDF_vsinks.get(i).getScale() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_vsinks.get(i).getName() + "(\"" + TDF_vsinks.get(i).getName() + "\", " + TDF_vsinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_vsources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_vsources.get(i).getName() + "(\"" + TDF_vsources.get(i).getName() + "\", " + TDF_vsources.get(i).getScale() + ")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_vsources.get(i).getName() + "(\"" + TDF_vsources.get(i).getName() + "\", " + TDF_vsources.get(i).getScale() + ")" + CR;
				}
			}
			
			// List of nodes
			LinkedList<ELNTConnector> nodes = new LinkedList<ELNTConnector>();
			for (int i = 0; i < connectors.size(); i++) {
				if (!connectors.get(i).getName().equals("")) {
					nodes.add(connectors.get(i));
				}
			}
			
			for (int i = 0; i < nodes.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + nodes.get(i).getName() + "(\"" + nodes.get(i).getName() + "\")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + nodes.get(i).getName() + "(\"" + nodes.get(i).getName() + "\")" + CR;
				}
			}
			
			LinkedList<ELNTComponentNodeRef> nodeRefs = module.getNodeRef();
			for (int i = 0; i < nodeRefs.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + nodeRefs.get(i).getName() + "(\"" + nodeRefs.get(i).getName() + "\")" + CR;
					cpt ++;
				} else {
					corpsModule = corpsModule + "\t, " + nodeRefs.get(i).getName() + "(\"" + nodeRefs.get(i).getName() + "\")" + CR;
				}
			}
			
			corpsModule = corpsModule + "\t{" + CR;
			
			for (int i = 0; i < capacitors.size(); i++) {
				for (int j = 0; j < capacitors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + capacitors.get(i).getName() + "." + capacitors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(capacitors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(capacitors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(capacitors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(capacitors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < idealTransformers.size(); i++) {
				for (int j = 0; j < idealTransformers.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + idealTransformers.get(i).getName() + "." + idealTransformers.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(idealTransformers.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(idealTransformers.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(idealTransformers.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(idealTransformers.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < isources.size(); i++) {
				for (int j = 0; j < isources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + isources.get(i).getName() + "." + isources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(isources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(isources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(isources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(isources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < vsources.size(); i++) {
				for (int j = 0; j < vsources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vsources.get(i).getName() + "." + vsources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(vsources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(vsources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(vsources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(vsources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < inductors.size(); i++) {
				for (int j = 0; j < inductors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + inductors.get(i).getName() + "." + inductors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(inductors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(inductors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(inductors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(inductors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < resistors.size(); i++) {
				for (int j = 0; j < resistors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + resistors.get(i).getName() + "." + resistors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(resistors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(resistors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(resistors.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(resistors.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < transmissionLines.size(); i++) {
				for (int j = 0; j < transmissionLines.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + transmissionLines.get(i).getName() + "." + transmissionLines.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(transmissionLines.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(transmissionLines.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(transmissionLines.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(transmissionLines.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < vccss.size(); i++) {
				for (int j = 0; j < vccss.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vccss.get(i).getName() + "." + vccss.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(vccss.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(vccss.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(vccss.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(vccss.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < vcvss.size(); i++) {
				for (int j = 0; j < vcvss.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vcvss.get(i).getName() + "." + vcvss.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(vcvss.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(vcvss.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(vcvss.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(vcvss.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < TDF_isinks.size(); i++) {
				for (int j = 0; j < TDF_isinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_isinks.get(i).getName() + "." + TDF_isinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(TDF_isinks.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(TDF_isinks.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(TDF_isinks.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(TDF_isinks.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < TDF_isources.size(); i++) {
				for (int j = 0; j < TDF_isources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_isources.get(i).getName() + "." + TDF_isources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(TDF_isources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(TDF_isources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(TDF_isources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(TDF_isources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < TDF_vsinks.size(); i++) {
				for (int j = 0; j < TDF_vsinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_vsinks.get(i).getName() + "." + TDF_vsinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(TDF_vsinks.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(TDF_vsinks.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(TDF_vsinks.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(TDF_vsinks.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < TDF_vsources.size(); i++) {
				for (int j = 0; j < TDF_vsources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_vsources.get(i).getName() + "." + TDF_vsources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectors.size(); k++) {
						if (connectors.get(k).get_p1().getName().equals(TDF_vsources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p1().getComponent().getName().equals(TDF_vsources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p2().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectors.get(k).get_p2().getName().equals(TDF_vsources.get(i).getConnectingPoint().get(j).getName()) && connectors.get(k).get_p2().getComponent().getName().equals(TDF_vsources.get(i).getName())) {
							if (!connectors.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectors.get(k).getName() + ");" + CR;
							} else {
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTComponentNodeRef) {
									corpsModule = corpsModule + connectors.get(k).get_p1().getComponent().getName() + ");" + CR;
								}
								if (connectors.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectors.get(k).get_p2().getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			corpsModule = corpsModule + "\t}" + CR2 + "private:" + CR;
			
			for (int i = 0; i < nodes.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_node " + nodes.get(i).getName() + CR;
			}
			
			for (int i = 0; i < nodeRefs.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_node_ref " + nodeRefs.get(i).getName() + CR;
			}
			
			corpsModule = corpsModule + "};" + CR2 + "#endif" + " // " + module.getName().toUpperCase() + "_H";
		} else {
			corpsModule = "";
		}
		return corpsModule;
	}
	
	private static String searchName(ELNTConnector connector) {
		if (connector.get_p1().getComponent() instanceof ELNTComponent) {
			if (connector.get_p2().getComponent() instanceof ELNTModuleTerminal) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTComponentNodeRef) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTMidPortTerminal) {
				ELNTConnector c = ((ELNTMidPortTerminal) connector.get_p2().getComponent()).getConnector();
				return searchName(c);
			}
		}
		if (connector.get_p1().getComponent() instanceof ELNTModuleTerminal) { 
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTComponentNodeRef) { 
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTMidPortTerminal) {
			if (connector.get_p2().getComponent() instanceof ELNTModuleTerminal) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTComponentNodeRef) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTComponent) {
				ELNTConnector c = ((ELNTMidPortTerminal) connector.get_p1().getComponent()).getConnector();
				return searchName(c);
			}
		}
		return "";
	}
}
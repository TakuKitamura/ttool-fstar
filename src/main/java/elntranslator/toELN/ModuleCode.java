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
 * Class ModuleCode Principal code of a module Creation: 25/07/2018
 * 
 * @version 1.0 25/07/2018
 * @author Irina Kit Yan LEE
 */

public class ModuleCode {
	static private String corpsModule;
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	ModuleCode() {
	}

	public static String getModuleCode(ELNTModule module, List<ELNTConnector> connectors) {
		LinkedList<ELNTConnector> connectorsModule = new LinkedList<ELNTConnector>();
		
		for (int i = 0; i < connectors.size(); i++) {
			if (connectors.get(i).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
				if (((ELNTPrimitiveComponent) connectors.get(i).get_p1().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortDE) {
				if (((ELNTModulePortDE) connectors.get(i).get_p1().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF) {
				if (((ELNTModulePortTDF) connectors.get(i).get_p1().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTModuleTerminal) {
				if (((ELNTModuleTerminal) connectors.get(i).get_p1().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p1().getComponent() instanceof ELNTNodeRef) {
				if (((ELNTNodeRef) connectors.get(i).get_p1().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
				if (((ELNTPrimitiveComponent) connectors.get(i).get_p2().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortDE) {
				if (((ELNTModulePortDE) connectors.get(i).get_p2().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF) {
				if (((ELNTModulePortTDF) connectors.get(i).get_p2().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p2().getComponent() instanceof ELNTModuleTerminal) {
				if (((ELNTModuleTerminal) connectors.get(i).get_p2().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			} else if (connectors.get(i).get_p2().getComponent() instanceof ELNTNodeRef) {
				if (((ELNTNodeRef) connectors.get(i).get_p2().getComponent()).getModule().equals(module)) {
					connectorsModule.add(connectors.get(i));
				}
			}
		}
		
		if (module != null) {
			corpsModule = "#ifndef " + module.getName().toUpperCase() + "_H" + CR + "#define "
					+ module.getName().toUpperCase() + "_H" + CR2 + "#include <cmath>" + CR + "#include <iostream>" + CR
					+ "#include <systemc-ams>" + CR2;

			corpsModule = corpsModule + "SC_MODULE(" + module.getName() + ")" + CR + "{" + CR;

			LinkedList<ELNTModuleTerminal> terms = module.getModuleTerminal();
			for (int i = 0; i < terms.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_terminal " + terms.get(i).getName() + ";" + CR;
				if (i == terms.size() - 1) {
					corpsModule = corpsModule + CR;
				}
			}

			LinkedList<ELNTModulePortDE> portsDE = module.getModulePortDE();
			for (int i = 0; i < portsDE.size(); i++) {
				corpsModule = corpsModule + "\tsc_core::sc_" + portsDE.get(i).getOrigin() + "<"
						+ portsDE.get(i).getType() + "> " + portsDE.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTModulePortTDF> portsTDF = module.getModulePortTDF();
			for (int i = 0; i < portsTDF.size(); i++) {
				corpsModule = corpsModule + "\tsca_tdf::sca_" + portsTDF.get(i).getOrigin() + "<"
						+ portsTDF.get(i).getType() + "> " + portsTDF.get(i).getName() + ";" + CR;
				if (i == portsTDF.size() - 1) {
					corpsModule = corpsModule + CR;
				}
			}

			LinkedList<ELNTComponentCapacitor> capacitors = module.getCapacitor();
			for (int i = 0; i < capacitors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_c " + capacitors.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentIdealTransformer> idealTransformers = module.getIdealTransformer();
			for (int i = 0; i < idealTransformers.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_ideal_transformer " + idealTransformers.get(i).getName()
						+ ";" + CR;
			}

			LinkedList<ELNTComponentIndependentCurrentSource> isources = module.getIsource();
			for (int i = 0; i < isources.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_isource " + isources.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentIndependentVoltageSource> vsources = module.getVsource();
			for (int i = 0; i < vsources.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vsource " + vsources.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentInductor> inductors = module.getInductor();
			for (int i = 0; i < inductors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_l " + inductors.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentResistor> resistors = module.getResistor();
			for (int i = 0; i < resistors.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_r " + resistors.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentTransmissionLine> transmissionLines = module.getTransmissionLine();
			for (int i = 0; i < transmissionLines.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_transmission_line " + transmissionLines.get(i).getName()
						+ ";" + CR;
			}

			LinkedList<ELNTComponentVoltageControlledCurrentSource> vccss = module.getVccs();
			for (int i = 0; i < vccss.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vccs " + vccss.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentVoltageControlledVoltageSource> vcvss = module.getVcvs();
			for (int i = 0; i < vcvss.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_vcvs " + vcvss.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentCurrentSinkDE> DE_isinks = module.getDE_isink();
			for (int i = 0; i < DE_isinks.size(); i++) {
				// corps = corps + "\tsca_eln::sca_de::sca_isink" + DE_isinks.get(i).getName()
				// + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_de_isink " + DE_isinks.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentCurrentSourceDE> DE_isources = module.getDE_isource();
			for (int i = 0; i < DE_isources.size(); i++) {
				// corps = corps + "\tsca_eln::sca_de::sca_isource " +
				// DE_isources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_de_isource " + DE_isources.get(i).getName() + ";" + CR;
			}
			
			LinkedList<ELNTComponentCurrentSinkTDF> TDF_isinks = module.getTDF_isink();
			for (int i = 0; i < TDF_isinks.size(); i++) {
				// corps = corps + "\tsca_eln::sca_tdf::sca_isink" + TDF_isinks.get(i).getName()
				// + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_isink " + TDF_isinks.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentCurrentSourceTDF> TDF_isources = module.getTDF_isource();
			for (int i = 0; i < TDF_isources.size(); i++) {
				// corps = corps + "\tsca_eln::sca_tdf::sca_isource " +
				// TDF_isources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_isource " + TDF_isources.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentVoltageSinkDE> DE_vsinks = module.getDE_vsink();
			for (int i = 0; i < DE_vsinks.size(); i++) {
				// corps = corps + "\tsca_eln::sca_de::sca_vsink " +
				// DE_vsinks.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_de_vsink " + DE_vsinks.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentVoltageSourceDE> DE_vsources = module.getDE_vsource();
			for (int i = 0; i < DE_vsources.size(); i++) {
				// corps = corps + "\tsca_eln::sca_de::sca_vsource " +
				// DE_vsources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_de_vsource " + DE_vsources.get(i).getName() + ";" + CR;
			}
			
			LinkedList<ELNTComponentVoltageSinkTDF> TDF_vsinks = module.getTDF_vsink();
			for (int i = 0; i < TDF_vsinks.size(); i++) {
				// corps = corps + "\tsca_eln::sca_tdf::sca_vsink " +
				// TDF_vsinks.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_vsink " + TDF_vsinks.get(i).getName() + ";" + CR;
			}

			LinkedList<ELNTComponentVoltageSourceTDF> TDF_vsources = module.getTDF_vsource();
			for (int i = 0; i < TDF_vsources.size(); i++) {
				// corps = corps + "\tsca_eln::sca_tdf::sca_vsource " +
				// TDF_vsources.get(i).getName() + ";" + CR;
				corpsModule = corpsModule + "\tsca_eln::sca_tdf_vsource " + TDF_vsources.get(i).getName() + ";" + CR;
			}

			corpsModule = corpsModule + CR + "\tSC_CTOR(" + module.getName() + ")" + CR;

			int cpt = 0;
			for (int i = 0; i < terms.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + terms.get(i).getName() + "(\"" + terms.get(i).getName() + "\")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + terms.get(i).getName() + "(\"" + terms.get(i).getName() + "\")"
							+ CR;
				}
			}
			for (int i = 0; i < portsDE.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + portsDE.get(i).getName() + "(\"" + portsDE.get(i).getName() + "\")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + portsDE.get(i).getName() + "(\"" + portsDE.get(i).getName() + "\")"
							+ CR;
				}
			}
			for (int i = 0; i < portsTDF.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + portsTDF.get(i).getName() + "(\"" + portsTDF.get(i).getName() + "\")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + portsTDF.get(i).getName() + "(\"" + portsTDF.get(i).getName() + "\")"
							+ CR;
				}
			}
			for (int i = 0; i < capacitors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + capacitors.get(i).getName() + "(\""
							+ capacitors.get(i).getName() + "\", "
							+ encode(capacitors.get(i).getVal(), capacitors.get(i).getUnit0()) + ", "
							+ encode(capacitors.get(i).getQ0(), capacitors.get(i).getUnit1()) + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + capacitors.get(i).getName() + "(\""
							+ capacitors.get(i).getName() + "\", "
							+ encode(capacitors.get(i).getVal(), capacitors.get(i).getUnit0()) + ", "
							+ encode(capacitors.get(i).getQ0(), capacitors.get(i).getUnit1()) + ")" + CR;
				}
			}
			for (int i = 0; i < idealTransformers.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + idealTransformers.get(i).getName() + "(\""
							+ idealTransformers.get(i).getName() + "\", " + idealTransformers.get(i).getRatio() + ")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + idealTransformers.get(i).getName() + "(\""
							+ idealTransformers.get(i).getName() + "\", " + idealTransformers.get(i).getRatio() + ")"
							+ CR;
				}
			}
			for (int i = 0; i < isources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + isources.get(i).getName() + "(\"" + isources.get(i).getName()
							+ "\", " + isources.get(i).getInitValue() + ", " + isources.get(i).getOffset() + ", "
							+ isources.get(i).getAmplitude() + ", "
							+ encode(isources.get(i).getFrequency(), isources.get(i).getUnit0()) + ", "
							+ isources.get(i).getPhase() + ", " + isources.get(i).getDelay() + ", "
							+ isources.get(i).getAcAmplitude() + ", " + isources.get(i).getAcPhase() + ", "
							+ isources.get(i).getAcNoiseAmplitude() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + isources.get(i).getName() + "(\"" + isources.get(i).getName()
							+ "\", " + isources.get(i).getInitValue() + ", " + isources.get(i).getOffset() + ", "
							+ isources.get(i).getAmplitude() + ", "
							+ encode(isources.get(i).getFrequency(), isources.get(i).getUnit0()) + ", "
							+ isources.get(i).getPhase() + ", " + isources.get(i).getDelay() + ", "
							+ isources.get(i).getAcAmplitude() + ", " + isources.get(i).getAcPhase() + ", "
							+ isources.get(i).getAcNoiseAmplitude() + ")" + CR;
				}
			}
			for (int i = 0; i < vsources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vsources.get(i).getName() + "(\"" + vsources.get(i).getName()
							+ "\", " + vsources.get(i).getInitValue() + ", " + vsources.get(i).getOffset() + ", "
							+ vsources.get(i).getAmplitude() + ", "
							+ encode(vsources.get(i).getFrequency(), vsources.get(i).getUnit0()) + ", "
							+ vsources.get(i).getPhase() + ", " + vsources.get(i).getDelay() + ", "
							+ vsources.get(i).getAcAmplitude() + ", " + vsources.get(i).getAcPhase() + ", "
							+ vsources.get(i).getAcNoiseAmplitude() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + vsources.get(i).getName() + "(\"" + vsources.get(i).getName()
							+ "\", " + vsources.get(i).getInitValue() + ", " + vsources.get(i).getOffset() + ", "
							+ vsources.get(i).getAmplitude() + ", "
							+ encode(vsources.get(i).getFrequency(), vsources.get(i).getUnit0()) + ", "
							+ vsources.get(i).getPhase() + ", " + vsources.get(i).getDelay() + ", "
							+ vsources.get(i).getAcAmplitude() + ", " + vsources.get(i).getAcPhase() + ", "
							+ vsources.get(i).getAcNoiseAmplitude() + ")" + CR;
				}
			}
			for (int i = 0; i < inductors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + inductors.get(i).getName() + "(\"" + inductors.get(i).getName()
							+ "\", " + encode(inductors.get(i).getVal(), inductors.get(i).getUnit0()) + ", "
							+ encode(inductors.get(i).getPhi0(), inductors.get(i).getUnit1()) + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + inductors.get(i).getName() + "(\"" + inductors.get(i).getName()
							+ "\", " + encode(inductors.get(i).getVal(), inductors.get(i).getUnit0()) + ", "
							+ encode(inductors.get(i).getPhi0(), inductors.get(i).getUnit1()) + ")" + CR;
				}
			}
			for (int i = 0; i < resistors.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + resistors.get(i).getName() + "(\"" + resistors.get(i).getName()
							+ "\", " + encode(resistors.get(i).getVal(), resistors.get(i).getUnit()) + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + resistors.get(i).getName() + "(\"" + resistors.get(i).getName()
							+ "\", " + encode(resistors.get(i).getVal(), resistors.get(i).getUnit()) + ")" + CR;
				}
			}
			for (int i = 0; i < transmissionLines.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + transmissionLines.get(i).getName() + "(\""
							+ transmissionLines.get(i).getName() + "\", "
							+ encode(transmissionLines.get(i).getZ0(), transmissionLines.get(i).getUnit0()) + ", "
							+ transmissionLines.get(i).getDelay() + ", "
							+ encode(transmissionLines.get(i).getDelta0(), transmissionLines.get(i).getUnit2()) + ")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + transmissionLines.get(i).getName() + "(\""
							+ transmissionLines.get(i).getName() + "\", "
							+ encode(transmissionLines.get(i).getZ0(), transmissionLines.get(i).getUnit0()) + ", "
							+ transmissionLines.get(i).getDelay() + ", "
							+ encode(transmissionLines.get(i).getDelta0(), transmissionLines.get(i).getUnit2()) + ")"
							+ CR;
				}
			}
			for (int i = 0; i < vccss.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vccss.get(i).getName() + "(\"" + vccss.get(i).getName()
							+ "\", " + encode(vccss.get(i).getVal(), vccss.get(i).getUnit()) + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + vccss.get(i).getName() + "(\"" + vccss.get(i).getName()
							+ "\", " + encode(vccss.get(i).getVal(), vccss.get(i).getUnit()) + ")" + CR;
				}
			}
			for (int i = 0; i < vcvss.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + vcvss.get(i).getName() + "(\"" + vcvss.get(i).getName()
							+ "\", " + vcvss.get(i).getVal() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + vcvss.get(i).getName() + "(\"" + vcvss.get(i).getName()
							+ "\", " + vcvss.get(i).getVal() + ")" + CR;
				}
			}
			for (int i = 0; i < DE_isinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + DE_isinks.get(i).getName() + "(\""
							+ DE_isinks.get(i).getName() + "\", " + DE_isinks.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + DE_isinks.get(i).getName() + "(\""
							+ DE_isinks.get(i).getName() + "\", " + DE_isinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < DE_isources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + DE_isources.get(i).getName() + "(\""
							+ DE_isources.get(i).getName() + "\", " + DE_isources.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + DE_isources.get(i).getName() + "(\""
							+ DE_isources.get(i).getName() + "\", " + DE_isources.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_isinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_isinks.get(i).getName() + "(\""
							+ TDF_isinks.get(i).getName() + "\", " + TDF_isinks.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_isinks.get(i).getName() + "(\""
							+ TDF_isinks.get(i).getName() + "\", " + TDF_isinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_isources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_isources.get(i).getName() + "(\""
							+ TDF_isources.get(i).getName() + "\", " + TDF_isources.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_isources.get(i).getName() + "(\""
							+ TDF_isources.get(i).getName() + "\", " + TDF_isources.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < DE_vsinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + DE_vsinks.get(i).getName() + "(\""
							+ DE_vsinks.get(i).getName() + "\", " + DE_vsinks.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + DE_vsinks.get(i).getName() + "(\""
							+ DE_vsinks.get(i).getName() + "\", " + DE_vsinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < DE_vsources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + DE_vsources.get(i).getName() + "(\""
							+ DE_vsources.get(i).getName() + "\", " + DE_vsources.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + DE_vsources.get(i).getName() + "(\""
							+ DE_vsources.get(i).getName() + "\", " + DE_vsources.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_vsinks.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_vsinks.get(i).getName() + "(\""
							+ TDF_vsinks.get(i).getName() + "\", " + TDF_vsinks.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_vsinks.get(i).getName() + "(\""
							+ TDF_vsinks.get(i).getName() + "\", " + TDF_vsinks.get(i).getScale() + ")" + CR;
				}
			}
			for (int i = 0; i < TDF_vsources.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + TDF_vsources.get(i).getName() + "(\""
							+ TDF_vsources.get(i).getName() + "\", " + TDF_vsources.get(i).getScale() + ")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + TDF_vsources.get(i).getName() + "(\""
							+ TDF_vsources.get(i).getName() + "\", " + TDF_vsources.get(i).getScale() + ")" + CR;
				}
			}

			// List of nodes
			LinkedList<ELNTConnector> nodes = new LinkedList<ELNTConnector>();
			for (int i = 0; i < connectorsModule.size(); i++) {
				if (!connectorsModule.get(i).getName().equals("")) {
					if (!((connectorsModule.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF 
							&& (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSourceTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSinkTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSourceTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSinkTDF))
							|| (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF 
							&& (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSourceTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSinkTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSourceTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSinkTDF))
							|| (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTModulePortTDF 
							&& (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSourceTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSinkTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSourceTDF
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSinkTDF))
							|| (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTModulePortTDF 
							&& (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSourceTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSinkTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSourceTDF
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSinkTDF))
							|| (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTModulePortDE
							&& (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSourceDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSinkDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSourceDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSinkDE))
							|| (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTModulePortDE
							&& (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSourceDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSinkDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSourceDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSinkDE))
							|| (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTModulePortDE 
							&& (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSourceDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentVoltageSinkDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSourceDE
							|| connectorsModule.get(i).get_p2().getComponent() instanceof ELNTComponentCurrentSinkDE))
							|| (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTModulePortDE 
							&& (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSourceDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentVoltageSinkDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSourceDE
							|| connectorsModule.get(i).get_p1().getComponent() instanceof ELNTComponentCurrentSinkDE))
							|| (connectorsModule.get(i).get_p1().getComponent() instanceof ELNTMidPortTerminal
							&& connectorsModule.get(i).get_p2().getComponent() instanceof ELNTPrimitiveComponent)
							|| (connectorsModule.get(i).get_p2().getComponent() instanceof ELNTMidPortTerminal
							&& connectorsModule.get(i).get_p1().getComponent() instanceof ELNTPrimitiveComponent))) {
						nodes.add(connectorsModule.get(i));
					}
				}
			}

			for (int i = 0; i < nodes.size(); i++) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + nodes.get(i).getName() + "(\"" + nodes.get(i).getName() + "\")"
							+ CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + nodes.get(i).getName() + "(\"" + nodes.get(i).getName() + "\")"
							+ CR;
				}
			}

			LinkedList<ELNTNodeRef> nodeRefs = module.getNodeRef();
			if (nodeRefs.size() >= 1) {
				if (cpt == 0) {
					corpsModule = corpsModule + "\t: " + nodeRefs.get(0).getName() + "(\"" + nodeRefs.get(0).getName()
							+ "\")" + CR;
					cpt++;
				} else {
					corpsModule = corpsModule + "\t, " + nodeRefs.get(0).getName() + "(\"" + nodeRefs.get(0).getName()
							+ "\")" + CR;
				}
			}

			corpsModule = corpsModule + "\t{" + CR;

			for (int i = 0; i < capacitors.size(); i++) {
				for (int j = 0; j < capacitors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + capacitors.get(i).getName() + "."
							+ capacitors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(capacitors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(capacitors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}	
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(capacitors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(capacitors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < idealTransformers.size(); i++) {
				for (int j = 0; j < idealTransformers.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + idealTransformers.get(i).getName() + "."
							+ idealTransformers.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(idealTransformers.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(idealTransformers.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(idealTransformers.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(idealTransformers.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < isources.size(); i++) {
				for (int j = 0; j < isources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + isources.get(i).getName() + "."
							+ isources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < vsources.size(); i++) {
				for (int j = 0; j < vsources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vsources.get(i).getName() + "."
							+ vsources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < inductors.size(); i++) {
				for (int j = 0; j < inductors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + inductors.get(i).getName() + "."
							+ inductors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(inductors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(inductors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(inductors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(inductors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < resistors.size(); i++) {
				for (int j = 0; j < resistors.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + resistors.get(i).getName() + "."
							+ resistors.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(resistors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(resistors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(resistors.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(resistors.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < transmissionLines.size(); i++) {
				for (int j = 0; j < transmissionLines.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + transmissionLines.get(i).getName() + "."
							+ transmissionLines.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(transmissionLines.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(transmissionLines.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(transmissionLines.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(transmissionLines.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < vccss.size(); i++) {
				for (int j = 0; j < vccss.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vccss.get(i).getName() + "."
							+ vccss.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(vccss.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(vccss.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(vccss.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(vccss.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < vcvss.size(); i++) {
				for (int j = 0; j < vcvss.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + vcvss.get(i).getName() + "."
							+ vcvss.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(vcvss.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(vcvss.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(vcvss.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(vcvss.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < DE_isinks.size(); i++) {
				for (int j = 0; j < DE_isinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + DE_isinks.get(i).getName() + "."
							+ DE_isinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(DE_isinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(DE_isinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(DE_isinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(DE_isinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < DE_isources.size(); i++) {
				for (int j = 0; j < DE_isources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + DE_isources.get(i).getName() + "."
							+ DE_isources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(DE_isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(DE_isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(DE_isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(DE_isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < TDF_isinks.size(); i++) {
				for (int j = 0; j < TDF_isinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_isinks.get(i).getName() + "."
							+ TDF_isinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(TDF_isinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(TDF_isinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(TDF_isinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(TDF_isinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < TDF_isources.size(); i++) {
				for (int j = 0; j < TDF_isources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_isources.get(i).getName() + "."
							+ TDF_isources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(TDF_isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(TDF_isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(TDF_isources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(TDF_isources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}
			
			for (int i = 0; i < DE_vsinks.size(); i++) {
				for (int j = 0; j < DE_vsinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + DE_vsinks.get(i).getName() + "."
							+ DE_vsinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(DE_vsinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(DE_vsinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(DE_vsinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(DE_vsinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < DE_vsources.size(); i++) {
				for (int j = 0; j < DE_vsources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + DE_vsources.get(i).getName() + "."
							+ DE_vsources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(DE_vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(DE_vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(DE_vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(DE_vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < TDF_vsinks.size(); i++) {
				for (int j = 0; j < TDF_vsinks.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_vsinks.get(i).getName() + "."
							+ TDF_vsinks.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(TDF_vsinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(TDF_vsinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(TDF_vsinks.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(TDF_vsinks.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			for (int i = 0; i < TDF_vsources.size(); i++) {
				for (int j = 0; j < TDF_vsources.get(i).getConnectingPoint().size(); j++) {
					corpsModule = corpsModule + "\t\t" + TDF_vsources.get(i).getName() + "."
							+ TDF_vsources.get(i).getConnectingPoint().get(j).getName() + "(";
					for (int k = 0; k < connectorsModule.size(); k++) {
						if (connectorsModule.get(k).get_p1().getName()
								.equals(TDF_vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p1().getComponent().equals(TDF_vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p2().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p2().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
						if (connectorsModule.get(k).get_p2().getName()
								.equals(TDF_vsources.get(i).getConnectingPoint().get(j).getName())
								&& connectorsModule.get(k).get_p2().getComponent().equals(TDF_vsources.get(i))) {
							if (!connectorsModule.get(k).getName().equals("")) {
								corpsModule = corpsModule + connectorsModule.get(k).getName() + ");" + CR;
							} else {
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
									LinkedList<ELNTMidPortTerminal> mids = connectorsModule.get(k).getMidPortTerminal();
									boolean stop = false;
									for (ELNTMidPortTerminal mid : mids) {
										for (int l = 0; l < connectorsModule.size(); l++) {
											if (connectorsModule.get(l).get_p1().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											} else if (connectorsModule.get(l).get_p2().getComponent().equals(mid)) {
												if (!searchName(connectorsModule.get(l)).equals("")) {
													corpsModule = corpsModule + searchName(connectorsModule.get(l)) + ");" + CR;
													stop = true;
													break;
												}
											}
										}
										if (stop == true) {
											break;
										}
									}
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModuleTerminal) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTNodeRef) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortDE) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTModulePortTDF) {
									corpsModule = corpsModule + connectorsModule.get(k).get_p1().getComponent().getName()
											+ ");" + CR;
								}
								if (connectorsModule.get(k).get_p1().getComponent() instanceof ELNTMidPortTerminal) {
									ELNTConnector connector = ((ELNTMidPortTerminal) connectorsModule.get(k).get_p2()
											.getComponent()).getConnector();
									corpsModule = corpsModule + searchName(connector) + ");" + CR;
								}
							}
						}
					}
				}
			}

			corpsModule = corpsModule + "\t}" + CR2 + "private:" + CR;

			for (int i = 0; i < nodes.size(); i++) {
				corpsModule = corpsModule + "\tsca_eln::sca_node " + nodes.get(i).getName() + ";" + CR;
			}

			if (nodeRefs.size() >= 1) {
				corpsModule = corpsModule + "\tsca_eln::sca_node_ref " + nodeRefs.get(0).getName() + ";" + CR;
			}

			corpsModule = corpsModule + "};" + CR2 + "#endif" + " // " + module.getName().toUpperCase() + "_H";
		} else {
			corpsModule = "";
		}
		return corpsModule;
	}

	private static String encode(double value, String unit) {
		StringBuffer unit_buf = new StringBuffer(unit);
		if (unit_buf.length() == 1) {
			return "" + value;
		} else if (unit_buf.length() == 2) {
			char c = unit_buf.charAt(0);
			switch (c) {
			case 'G':
				return value + "e9";
			case 'M':
				return value + "e6";
			case 'k':
				return value + "e3";
			case 'm':
				return value + "e-3";
			case '\u03BC':
				return value + "e-6";
			case 'n':
				return value + "e-9";
			case 'p':
				return value + "e-12";
			case 'f':
				return value + "e-15";
			default:
				return "" + value;
			}
		}
		return "";
	}

	private static String searchName(ELNTConnector connector) {
		if (connector.get_p1().getComponent() instanceof ELNTPrimitiveComponent) {
			if (connector.get_p2().getComponent() instanceof ELNTModulePortTDF) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTModulePortDE) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTModuleTerminal) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTNodeRef) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTMidPortTerminal) {
				ELNTConnector c = ((ELNTMidPortTerminal) connector.get_p2().getComponent()).getConnector();
				return searchName(c);
			}
		}
		if (connector.get_p1().getComponent() instanceof ELNTModulePortTDF) {
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTModulePortDE) {
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTModuleTerminal) {
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTNodeRef) {
			return connector.get_p1().getComponent().getName();
		}
		if (connector.get_p1().getComponent() instanceof ELNTMidPortTerminal) {
			if (connector.get_p2().getComponent() instanceof ELNTModulePortTDF) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTModulePortDE) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTModuleTerminal) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTNodeRef) {
				return connector.get_p2().getComponent().getName();
			}
			if (connector.get_p2().getComponent() instanceof ELNTPrimitiveComponent) {
				ELNTConnector c = ((ELNTMidPortTerminal) connector.get_p1().getComponent()).getConnector();
				return searchName(c);
			}
		}
		return "";
	}
}
/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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

package ui;

import ui.eln.*;
import ui.eln.sca_eln.*;
import ui.eln.sca_eln_sca_tdf.*;
import java.util.*;
import elntranslator.*;

/**
 * Class ELNPanelTranslator
 * Translation of semantics of ELN Diagrams
 * Creation: 24/07/2018
 * @version 1.0 24/07/2018
 * @author Irina Kit Yan LEE
 */

public class ELNPanelTranslator {
	private List<TGComponent> tgcComponents;
	private List<ELNTComponent> elnComponents;
	private List<ELNTConnector> elnConnectors;

	public ELNPanelTranslator(ELNDiagramPanel _elnDiagramPanel) {
		tgcComponents = _elnDiagramPanel.getComponentList();

		elnComponents = new LinkedList<ELNTComponent>();
		elnConnectors = new LinkedList<ELNTConnector>();

		MakeListOfComponent(_elnDiagramPanel);
	}

	private void MakeListOfComponent(ELNDiagramPanel elnDiagramPanel) {

		Map<TGComponent, ELNTComponent> elnMap = new HashMap<TGComponent, ELNTComponent>();

		TGComponent tgc;
		Iterator<TGComponent> iterator1 = tgcComponents.listIterator();
		Iterator<TGComponent> iterator2 = tgcComponents.listIterator();
		List<TGComponent> list = new ArrayList<TGComponent>();

		while (iterator1.hasNext()) {
			tgc = iterator1.next();
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		
		while (iterator2.hasNext()) {
			tgc = iterator2.next();
			if (tgc instanceof TGConnector) {
				list.add(tgc);
			}
		}	

		for (TGComponent dp : list) {
			if (dp instanceof ELNModule) {
				ELNModule module = (ELNModule) dp;

				String moduleName = module.getValue();
				
				ELNTModule elnModule = new ELNTModule(moduleName);

				List<ELNComponentCapacitor> capacitors = module.getAllComponentCapacitor();
				for (int i = 0; i < capacitors.size(); i++) {
					ELNComponentCapacitor capacitor = capacitors.get(i);

					String name = capacitor.getValue();
					double val = capacitor.getVal();
					double q0 = capacitor.getQ0();
					String unit0 = capacitor.getUnit0();
					String unit1 = capacitor.getUnit1();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) capacitor.connectingPoint;

					ELNTComponentCapacitor elnCapacitor = new ELNTComponentCapacitor(name, val, q0, unit0, unit1, cp, elnModule);

					elnMap.put(capacitor, elnCapacitor);
					elnModule.addCapacitor(elnCapacitor);
					elnComponents.add(elnCapacitor);
				}	
				List<ELNComponentCurrentSinkTDF> TDF_isinks = module.getAllComponentCurrentSinkTDF();
				for (int i = 0; i < TDF_isinks.size(); i++) {
					ELNComponentCurrentSinkTDF TDF_isink = TDF_isinks.get(i);

					String name = TDF_isink.getValue();
					double scale = TDF_isink.getScale();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) TDF_isink.connectingPoint;

					ELNTComponentCurrentSinkTDF elnTDF_isink = new ELNTComponentCurrentSinkTDF(name, scale, cp, elnModule);

					elnMap.put(TDF_isink, elnTDF_isink);
					elnModule.addTDF_isink(elnTDF_isink);
					elnComponents.add(elnTDF_isink);
				}	
				List<ELNComponentCurrentSourceTDF> TDF_isources = module.getAllComponentCurrentSourceTDF();
				for (int i = 0; i < TDF_isources.size(); i++) {
					ELNComponentCurrentSourceTDF TDF_isource = TDF_isources.get(i);

					String name = TDF_isource.getValue();
					double scale = TDF_isource.getScale();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) TDF_isource.connectingPoint;

					ELNTComponentCurrentSourceTDF elnTDF_isource = new ELNTComponentCurrentSourceTDF(name, scale, cp, elnModule);

					elnMap.put(TDF_isource, elnTDF_isource);
					elnModule.addTDF_isource(elnTDF_isource);
					elnComponents.add(elnTDF_isource);
				}	
				List<ELNComponentIdealTransformer> idealTransformers = module.getAllComponentIdealTransformer();
				for (int i = 0; i < idealTransformers.size(); i++) {
					ELNComponentIdealTransformer idealTransformer = idealTransformers.get(i);

					String name = idealTransformer.getValue();
					double ratio = idealTransformer.getRatio();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) idealTransformer.connectingPoint;

					ELNTComponentIdealTransformer elnIdealTransformer = new ELNTComponentIdealTransformer(name, ratio, cp, elnModule);

					elnMap.put(idealTransformer, elnIdealTransformer);
					elnModule.addIdealTransformer(elnIdealTransformer);
					elnComponents.add(elnIdealTransformer);
				}	
				List<ELNComponentIndependentCurrentSource> isources = module.getAllComponentIndependentCurrentSource();
				for (int i = 0; i < isources.size(); i++) {
					ELNComponentIndependentCurrentSource isource = isources.get(i);

					String name = isource.getValue();
					double initValue = isource.getInitValue();
					double offset = isource.getOffset();
					double amplitude = isource.getAmplitude();
					double frequency = isource.getFrequency();
					double phase = isource.getPhase();
					double acAmplitude = isource.getAcAmplitude();
					double acPhase = isource.getAcPhase();
					double acNoiseAmpliture = isource.getAcNoiseAmplitude();
					String delay = isource.getDelay();
					String unit0 = isource.getUnit0();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) isource.connectingPoint;

					ELNTComponentIndependentCurrentSource elnISource = new ELNTComponentIndependentCurrentSource(name, initValue, offset, amplitude, frequency, phase, acAmplitude, acPhase, acNoiseAmpliture, delay, unit0, cp, elnModule);

					elnMap.put(isource, elnISource);
					elnModule.addIsource(elnISource);
					elnComponents.add(elnISource);
				}	
				List<ELNComponentIndependentVoltageSource> vsources = module.getAllComponentIndependentVoltageSource();
				for (int i = 0; i < vsources.size(); i++) {
					ELNComponentIndependentVoltageSource vsource = vsources.get(i);

					String name = vsource.getValue();
					double initValue = vsource.getInitValue();
					double offset = vsource.getOffset();
					double amplitude = vsource.getAmplitude();
					double frequency = vsource.getFrequency();
					double phase = vsource.getPhase();
					double acAmplitude = vsource.getAcAmplitude();
					double acPhase = vsource.getAcPhase();
					double acNoiseAmpliture = vsource.getAcNoiseAmplitude();
					String delay = vsource.getDelay();
					String unit0 = vsource.getUnit0();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) vsource.connectingPoint;

					ELNTComponentIndependentVoltageSource elnVSource = new ELNTComponentIndependentVoltageSource(name, initValue, offset, amplitude, frequency, phase, acAmplitude, acPhase, acNoiseAmpliture, delay, unit0, cp, elnModule);

					elnMap.put(vsource, elnVSource);
					elnModule.addVsource(elnVSource);
					elnComponents.add(elnVSource);
				}
				List<ELNComponentInductor> inductors = module.getAllComponentInductor();
				for (int i = 0; i < inductors.size(); i++) {
					ELNComponentInductor inductor = inductors.get(i);

					String name = inductor.getValue();
					double val = inductor.getVal();
					double phi0 = inductor.getPhi0();
					String unit0 = inductor.getUnit0();
					String unit1 = inductor.getUnit1();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) inductor.connectingPoint;

					ELNTComponentInductor elnInductor = new ELNTComponentInductor(name, val, phi0, unit0, unit1, cp, elnModule);

					elnMap.put(inductor, elnInductor);
					elnModule.addInductor(elnInductor);
					elnComponents.add(elnInductor);
				}
				List<ELNComponentNodeRef> nodeRefs = module.getAllComponentNodeRef();
				for (int i = 0; i < nodeRefs.size(); i++) {
					ELNComponentNodeRef nodeRef = nodeRefs.get(i);

					String name = nodeRef.getValue();

					ELNTComponentNodeRef elnNodeRef = new ELNTComponentNodeRef(name, elnModule);

					elnMap.put(nodeRef, elnNodeRef);
					elnModule.addNodeRef(elnNodeRef);
					elnComponents.add(elnNodeRef);
				}	
				List<ELNComponentResistor> resistors = module.getAllComponentResistor();
				for (int i = 0; i < resistors.size(); i++) {
					ELNComponentResistor resistor = resistors.get(i);

					String name = resistor.getValue();
					double val = resistor.getVal();
					String unit = resistor.getUnit();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) resistor.connectingPoint;

					ELNTComponentResistor elnResistor = new ELNTComponentResistor(name, val, unit, cp, elnModule);

					elnMap.put(resistor, elnResistor);
					elnModule.addResistor(elnResistor);
					elnComponents.add(elnResistor);
				}
				List<ELNComponentTransmissionLine> transmissionLines = module.getAllComponentTransmissionLine();
				for (int i = 0; i < transmissionLines.size(); i++) {
					ELNComponentTransmissionLine transmissionLine = transmissionLines.get(i);

					String name = transmissionLine.getValue();
					double z0 = transmissionLine.getZ0();
					double delta0 = transmissionLine.getDelta0();
					String delay = transmissionLine.getDelay();
					String unit0 = transmissionLine.getUnit0();
					String unit2 = transmissionLine.getUnit2();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) transmissionLine.connectingPoint;

					ELNTComponentTransmissionLine elnTransmissionLine = new ELNTComponentTransmissionLine(name, z0, delta0, delay, unit0, unit2, cp, elnModule);

					elnMap.put(transmissionLine, elnTransmissionLine);
					elnModule.addTransmissionLine(elnTransmissionLine);
					elnComponents.add(elnTransmissionLine);
				}
				List<ELNComponentVoltageControlledCurrentSource> vccss = module.getAllComponentVoltageControlledCurrentSource();
				for (int i = 0; i < vccss.size(); i++) {
					ELNComponentVoltageControlledCurrentSource vccs = vccss.get(i);

					String name = vccs.getValue();
					double val = vccs.getVal();
					String unit = vccs.getUnit();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) vccs.connectingPoint;

					ELNTComponentVoltageControlledCurrentSource elnVCCS = new ELNTComponentVoltageControlledCurrentSource(name, val, unit, cp, elnModule);

					elnMap.put(vccs, elnVCCS);
					elnModule.addVccs(elnVCCS);
					elnComponents.add(elnVCCS);
				}
				List<ELNComponentVoltageControlledVoltageSource> vcvss = module.getAllComponentVoltageControlledVoltageSource();
				for (int i = 0; i < vcvss.size(); i++) {
					ELNComponentVoltageControlledVoltageSource vcvs = vcvss.get(i);

					String name = vcvs.getValue();
					double val = vcvs.getVal();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) vcvs.connectingPoint;
					
					ELNTComponentVoltageControlledVoltageSource elnVCVS = new ELNTComponentVoltageControlledVoltageSource(name, val, cp, elnModule);

					elnMap.put(vcvs, elnVCVS);
					elnModule.addVcvs(elnVCVS);
					elnComponents.add(elnVCVS);
				}
				List<ELNComponentVoltageSinkTDF> TDF_vsinks = module.getAllComponentVoltageSinkTDF();
				for (int i = 0; i < TDF_vsinks.size(); i++) {
					ELNComponentVoltageSinkTDF TDF_vsink = TDF_vsinks.get(i);

					String name = TDF_vsink.getValue();
					double scale = TDF_vsink.getScale();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) TDF_vsink.connectingPoint;

					ELNTComponentVoltageSinkTDF elnTDF_vsink = new ELNTComponentVoltageSinkTDF(name, scale, cp, elnModule);

					elnMap.put(TDF_vsink, elnTDF_vsink);
					elnModule.addTDF_vsink(elnTDF_vsink);
					elnComponents.add(elnTDF_vsink);
				}	
				List<ELNComponentVoltageSourceTDF> TDF_vsources = module.getAllComponentVoltageSourceTDF();
				for (int i = 0; i < TDF_vsources.size(); i++) {
					ELNComponentVoltageSourceTDF TDF_vsource = TDF_vsources.get(i);

					String name = TDF_vsource.getValue();
					double scale = TDF_vsource.getScale();
					ELNConnectingPoint[] cp = (ELNConnectingPoint[]) TDF_vsource.connectingPoint;

					ELNTComponentVoltageSourceTDF elnTDF_vsource = new ELNTComponentVoltageSourceTDF(name, scale, cp, elnModule);

					elnMap.put(TDF_vsource, elnTDF_vsource);
					elnModule.addTDF_vsource(elnTDF_vsource);
					elnComponents.add(elnTDF_vsource);
				}	
				List<ELNModuleTerminal> moduleTerminals = module.getAllModuleTerminal();
				for (int i = 0; i < moduleTerminals.size(); i++) {
					ELNModuleTerminal moduleTerminal = moduleTerminals.get(i);

					String name = moduleTerminal.getValue();

					ELNTModuleTerminal elnModuleTerminal = new ELNTModuleTerminal(name, elnModule);

					elnMap.put(moduleTerminal, elnModuleTerminal);
					elnModule.addModuleTerminal(elnModuleTerminal);
					elnComponents.add(elnModuleTerminal);
				}	
				elnMap.put(module, elnModule);
				elnComponents.add(elnModule);
			} else if (dp instanceof ELNConnector) {
				ELNConnector connector = (ELNConnector) dp;

				String name = connector.getValue();
				
				ELNConnectingPoint connectingPoint1 = (ELNConnectingPoint) connector.get_p1();
				ELNConnectingPoint connectingPoint2 = (ELNConnectingPoint) connector.get_p2();
				
				String p1Name = connectingPoint1.getName();
				String p2Name = connectingPoint2.getName();
				
				TGComponent owner_p1 = elnDiagramPanel.getComponentToWhichBelongs(connectingPoint1);
				TGComponent owner_p2 = elnDiagramPanel.getComponentToWhichBelongs(connectingPoint2);

				ELNTComponent avowner_p1 = elnMap.get(owner_p1);	
				ELNTComponent avowner_p2 = elnMap.get(owner_p2);

				ELNTConnectingPoint avConnectingPoint1 = new ELNTConnectingPoint(avowner_p1, p1Name);
				ELNTConnectingPoint avConnectingPoint2 = new ELNTConnectingPoint(avowner_p2, p2Name);

				ELNTConnector avconnector = new ELNTConnector(avConnectingPoint1, avConnectingPoint2, name);	
				
				List<ELNMidPortTerminal> midPortTerminals = connector.getAllMidPortTerminal();
				for (int i = 0; i < midPortTerminals.size(); i++) {
					ELNMidPortTerminal midPortTerminal = midPortTerminals.get(i);

					ELNTMidPortTerminal elnMidPortTerminal = new ELNTMidPortTerminal(avconnector);

					elnMap.put(midPortTerminal, elnMidPortTerminal);
					avconnector.addMidPortTerminal(elnMidPortTerminal);
					elnComponents.add(elnMidPortTerminal);
				}	

				elnConnectors.add(avconnector);
			}
		}
	}

	public ELNSpecification getELNSpecification() {
		return new ELNSpecification(elnComponents, elnConnectors);
	}
}
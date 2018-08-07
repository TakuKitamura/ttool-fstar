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
import ui.eln.sca_eln_sca_de.*;
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
		Iterator<TGComponent> iterator3 = tgcComponents.listIterator();
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
				if (!(((TGConnector) tgc).getTGConnectingPointP1().getFather() instanceof ELNMidPortTerminal || ((TGConnector) tgc).getTGConnectingPointP2().getFather() instanceof ELNMidPortTerminal)) {
					list.add(tgc);
				}
			}
		}	

		while (iterator3.hasNext()) {
			tgc = iterator3.next();
			if (tgc instanceof TGConnector) {
				if (((TGConnector) tgc).getTGConnectingPointP1().getFather() instanceof ELNMidPortTerminal || ((TGConnector) tgc).getTGConnectingPointP2().getFather() instanceof ELNMidPortTerminal) {
					list.add(tgc);
				}
			}
		}
		
		for (TGComponent dp : list) {
			if (dp instanceof ELNCluster) {
				ELNCluster cluster = (ELNCluster) dp;
				
				String clusterName = cluster.getValue();
				
				ELNTCluster elnCluster = new ELNTCluster(clusterName);
				
				List<ELNClusterTerminal> clusterTerminals = cluster.getAllClusterTerminal();
				for (int i = 0; i < clusterTerminals.size(); i++) {
					ELNClusterTerminal clusterTerminal = clusterTerminals.get(i);

					String termName = clusterTerminal.getValue();

					ELNTClusterTerminal elnClusterTerminal = new ELNTClusterTerminal(termName, elnCluster);

					elnMap.put(clusterTerminal, elnClusterTerminal);
					elnCluster.addClusterTerminal(elnClusterTerminal);
					elnComponents.add(elnClusterTerminal);
				}
				List<ELNClusterPortDE> clusterPortsDE = cluster.getAllClusterPortDE();
				for (int i = 0; i < clusterPortsDE.size(); i++) {
					ELNClusterPortDE clusterPortDE = clusterPortsDE.get(i);

					String portName = clusterPortDE.getValue();
					String type = clusterPortDE.getPortType();
					String origin = clusterPortDE.getOrigin();

					ELNTClusterPortDE elnClusterPortDE = new ELNTClusterPortDE(portName, type, origin, elnCluster);

					elnMap.put(clusterPortDE, elnClusterPortDE);
					elnCluster.addClusterPortDE(elnClusterPortDE);
					elnComponents.add(elnClusterPortDE);
				}
				List<ELNClusterPortTDF> clusterPortsTDF = cluster.getAllClusterPortTDF();
				for (int i = 0; i < clusterPortsTDF.size(); i++) {
					ELNClusterPortTDF clusterPortTDF = clusterPortsTDF.get(i);

					String portName = clusterPortTDF.getValue();
					String type = clusterPortTDF.getPortType();
					String origin = clusterPortTDF.getOrigin();

					ELNTClusterPortTDF elnClusterPortTDF = new ELNTClusterPortTDF(portName, type, origin, elnCluster);

					elnMap.put(clusterPortTDF, elnClusterPortTDF);
					elnCluster.addClusterPortTDF(elnClusterPortTDF);
					elnComponents.add(elnClusterPortTDF);
				}
				List<ELNModule> modules = cluster.getAllModule();
				for (int i = 0; i < modules.size(); i++) {
					ELNModule module = modules.get(i);

					String moduleName = module.getValue();
				
					ELNTModule elnModule = new ELNTModule(moduleName, elnCluster);

					List<ELNComponentCapacitor> capacitors = module.getAllComponentCapacitor();
					for (int j = 0; j < capacitors.size(); j++) {
						ELNComponentCapacitor capacitor = capacitors.get(j);
	
						String name = capacitor.getValue();
						double val = capacitor.getVal();
						double q0 = capacitor.getQ0();
						String unit0 = capacitor.getUnit0();
						String unit1 = capacitor.getUnit1();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) capacitor.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) capacitor.getTGConnectingPointAtIndex(1); 
	
						ELNTComponentCapacitor elnCapacitor = new ELNTComponentCapacitor(name, val, q0, unit0, unit1, elnModule);
						
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnCapacitor);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnCapacitor);
						
						elnCapacitor.addConnectingPoint(elncp0);
						elnCapacitor.addConnectingPoint(elncp1);
						
						elnMap.put(capacitor, elnCapacitor);
						elnModule.addCapacitor(elnCapacitor);
						elnComponents.add(elnCapacitor);
					}	
					List<ELNComponentCurrentSinkDE> DE_isinks = module.getAllComponentCurrentSinkDE();
					for (int j = 0; j < DE_isinks.size(); j++) {
						ELNComponentCurrentSinkDE DE_isink = DE_isinks.get(j);
	
						String name = DE_isink.getValue();
						double scale = DE_isink.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) DE_isink.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) DE_isink.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) DE_isink.getTGConnectingPointAtIndex(2);
	
						ELNTComponentCurrentSinkDE elnDE_isink = new ELNTComponentCurrentSinkDE(name, scale,elnModule);
						
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnDE_isink);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnDE_isink);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnDE_isink);
						
						elnDE_isink.addConnectingPoint(elncp0);
						elnDE_isink.addConnectingPoint(elncp1);
						elnDE_isink.addConnectingPoint(elncp2);
	
						elnMap.put(DE_isink, elnDE_isink);
						elnModule.addDE_isink(elnDE_isink);
						elnComponents.add(elnDE_isink);
					}	
					List<ELNComponentCurrentSourceDE> DE_isources = module.getAllComponentCurrentSourceDE();
					for (int j = 0; j < DE_isources.size(); j++) {
						ELNComponentCurrentSourceDE DE_isource = DE_isources.get(j);
	
						String name = DE_isource.getValue();
						double scale = DE_isource.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) DE_isource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) DE_isource.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) DE_isource.getTGConnectingPointAtIndex(2);
						
						ELNTComponentCurrentSourceDE elnDE_isource = new ELNTComponentCurrentSourceDE(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnDE_isource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnDE_isource);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnDE_isource);
						
						elnDE_isource.addConnectingPoint(elncp0);
						elnDE_isource.addConnectingPoint(elncp1);
						elnDE_isource.addConnectingPoint(elncp2);
						
						elnMap.put(DE_isource, elnDE_isource);
						elnModule.addDE_isource(elnDE_isource);
						elnComponents.add(elnDE_isource);
					}	
					List<ELNComponentCurrentSinkTDF> TDF_isinks = module.getAllComponentCurrentSinkTDF();
					for (int j = 0; j < TDF_isinks.size(); j++) {
						ELNComponentCurrentSinkTDF TDF_isink = TDF_isinks.get(j);
	
						String name = TDF_isink.getValue();
						double scale = TDF_isink.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) TDF_isink.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) TDF_isink.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) TDF_isink.getTGConnectingPointAtIndex(2);
	
						ELNTComponentCurrentSinkTDF elnTDF_isink = new ELNTComponentCurrentSinkTDF(name, scale,elnModule);
						
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnTDF_isink);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnTDF_isink);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnTDF_isink);
						
						elnTDF_isink.addConnectingPoint(elncp0);
						elnTDF_isink.addConnectingPoint(elncp1);
						elnTDF_isink.addConnectingPoint(elncp2);
	
						elnMap.put(TDF_isink, elnTDF_isink);
						elnModule.addTDF_isink(elnTDF_isink);
						elnComponents.add(elnTDF_isink);
					}	
					List<ELNComponentCurrentSourceTDF> TDF_isources = module.getAllComponentCurrentSourceTDF();
					for (int j = 0; j < TDF_isources.size(); j++) {
						ELNComponentCurrentSourceTDF TDF_isource = TDF_isources.get(j);
	
						String name = TDF_isource.getValue();
						double scale = TDF_isource.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) TDF_isource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) TDF_isource.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) TDF_isource.getTGConnectingPointAtIndex(2);
						
						ELNTComponentCurrentSourceTDF elnTDF_isource = new ELNTComponentCurrentSourceTDF(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnTDF_isource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnTDF_isource);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnTDF_isource);
						
						elnTDF_isource.addConnectingPoint(elncp0);
						elnTDF_isource.addConnectingPoint(elncp1);
						elnTDF_isource.addConnectingPoint(elncp2);
						
						elnMap.put(TDF_isource, elnTDF_isource);
						elnModule.addTDF_isource(elnTDF_isource);
						elnComponents.add(elnTDF_isource);
					}	
					List<ELNComponentIdealTransformer> idealTransformers = module.getAllComponentIdealTransformer();
					for (int j = 0; j < idealTransformers.size(); j++) {
						ELNComponentIdealTransformer idealTransformer = idealTransformers.get(j);
	
						String name = idealTransformer.getValue();
						double ratio = idealTransformer.getRatio();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) idealTransformer.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) idealTransformer.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) idealTransformer.getTGConnectingPointAtIndex(2);
						ELNConnectingPoint cp3 = (ELNConnectingPoint) idealTransformer.getTGConnectingPointAtIndex(3); 
	
						ELNTComponentIdealTransformer elnIdealTransformer = new ELNTComponentIdealTransformer(name, ratio, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnIdealTransformer);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnIdealTransformer);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnIdealTransformer);
						ELNTConnectingPoint elncp3 = new ELNTConnectingPoint(cp3.getName(), elnIdealTransformer);
						
						elnIdealTransformer.addConnectingPoint(elncp0);
						elnIdealTransformer.addConnectingPoint(elncp1);
						elnIdealTransformer.addConnectingPoint(elncp2);
						elnIdealTransformer.addConnectingPoint(elncp3);
						
						elnMap.put(idealTransformer, elnIdealTransformer);
						elnModule.addIdealTransformer(elnIdealTransformer);
						elnComponents.add(elnIdealTransformer);
					}	
					List<ELNComponentIndependentCurrentSource> isources = module.getAllComponentIndependentCurrentSource();
					for (int j = 0; j < isources.size(); j++) {
						ELNComponentIndependentCurrentSource isource = isources.get(j);
	
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
						ELNConnectingPoint cp0 = (ELNConnectingPoint) isource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) isource.getTGConnectingPointAtIndex(1); 
	
						ELNTComponentIndependentCurrentSource elnISource = new ELNTComponentIndependentCurrentSource(name, initValue, offset, amplitude, frequency, phase, acAmplitude, acPhase, acNoiseAmpliture, delay, unit0, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnISource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnISource);
						
						elnISource.addConnectingPoint(elncp0);
						elnISource.addConnectingPoint(elncp1);
						
						elnMap.put(isource, elnISource);
						elnModule.addIsource(elnISource);
						elnComponents.add(elnISource);
					}	
					List<ELNComponentIndependentVoltageSource> vsources = module.getAllComponentIndependentVoltageSource();
					for (int j = 0; j < vsources.size(); j++) {
						ELNComponentIndependentVoltageSource vsource = vsources.get(j);
	
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
						ELNConnectingPoint cp0 = (ELNConnectingPoint) vsource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) vsource.getTGConnectingPointAtIndex(1); 
						
						ELNTComponentIndependentVoltageSource elnVSource = new ELNTComponentIndependentVoltageSource(name, initValue, offset, amplitude, frequency, phase, acAmplitude, acPhase, acNoiseAmpliture, delay, unit0, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnVSource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnVSource);
						
						elnVSource.addConnectingPoint(elncp0);
						elnVSource.addConnectingPoint(elncp1);
						
						elnMap.put(vsource, elnVSource);
						elnModule.addVsource(elnVSource);
						elnComponents.add(elnVSource);
					}
					List<ELNComponentInductor> inductors = module.getAllComponentInductor();
					for (int j = 0; j < inductors.size(); j++) {
						ELNComponentInductor inductor = inductors.get(j);
	
						String name = inductor.getValue();
						double val = inductor.getVal();
						double phi0 = inductor.getPhi0();
						String unit0 = inductor.getUnit0();
						String unit1 = inductor.getUnit1();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) inductor.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) inductor.getTGConnectingPointAtIndex(1); 
						
						ELNTComponentInductor elnInductor = new ELNTComponentInductor(name, val, phi0, unit0, unit1, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnInductor);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnInductor);
						
						elnInductor.addConnectingPoint(elncp0);
						elnInductor.addConnectingPoint(elncp1);
						
						elnMap.put(inductor, elnInductor);
						elnModule.addInductor(elnInductor);
						elnComponents.add(elnInductor);
					}
					List<ELNNodeRef> nodeRefs = module.getAllComponentNodeRef();
					for (int j = 0; j < nodeRefs.size(); j++) {
						ELNNodeRef nodeRef = nodeRefs.get(j);
	
						String name = nodeRef.getValue();
	
						ELNTComponentNodeRef elnNodeRef = new ELNTComponentNodeRef(name, elnModule);
	
						elnMap.put(nodeRef, elnNodeRef);
						elnModule.addNodeRef(elnNodeRef);
						elnComponents.add(elnNodeRef);
					}	
					List<ELNComponentResistor> resistors = module.getAllComponentResistor();
					for (int j = 0; j < resistors.size(); j++) {
						ELNComponentResistor resistor = resistors.get(j);
	
						String name = resistor.getValue();
						double val = resistor.getVal();
						String unit = resistor.getUnit();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) resistor.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) resistor.getTGConnectingPointAtIndex(1);
	
						ELNTComponentResistor elnResistor = new ELNTComponentResistor(name, val, unit, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnResistor);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnResistor);
						
						elnResistor.addConnectingPoint(elncp0);
						elnResistor.addConnectingPoint(elncp1);
						
						elnMap.put(resistor, elnResistor);
						elnModule.addResistor(elnResistor);
						elnComponents.add(elnResistor);
					}
					List<ELNComponentTransmissionLine> transmissionLines = module.getAllComponentTransmissionLine();
					for (int j = 0; j < transmissionLines.size(); j++) {
						ELNComponentTransmissionLine transmissionLine = transmissionLines.get(j);
	
						String name = transmissionLine.getValue();
						double z0 = transmissionLine.getZ0();
						double delta0 = transmissionLine.getDelta0();
						String delay = transmissionLine.getDelay();
						String unit0 = transmissionLine.getUnit0();
						String unit2 = transmissionLine.getUnit2();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) transmissionLine.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) transmissionLine.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) transmissionLine.getTGConnectingPointAtIndex(2);
						ELNConnectingPoint cp3 = (ELNConnectingPoint) transmissionLine.getTGConnectingPointAtIndex(3); 
	
						ELNTComponentTransmissionLine elnTransmissionLine = new ELNTComponentTransmissionLine(name, z0, delta0, delay, unit0, unit2, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnTransmissionLine);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnTransmissionLine);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnTransmissionLine);
						ELNTConnectingPoint elncp3 = new ELNTConnectingPoint(cp3.getName(), elnTransmissionLine);
						
						elnTransmissionLine.addConnectingPoint(elncp0);
						elnTransmissionLine.addConnectingPoint(elncp1);
						elnTransmissionLine.addConnectingPoint(elncp2);
						elnTransmissionLine.addConnectingPoint(elncp3);
						
						elnMap.put(transmissionLine, elnTransmissionLine);
						elnModule.addTransmissionLine(elnTransmissionLine);
						elnComponents.add(elnTransmissionLine);
					}
					List<ELNComponentVoltageControlledCurrentSource> vccss = module.getAllComponentVoltageControlledCurrentSource();
					for (int j = 0; j < vccss.size(); j++) {
						ELNComponentVoltageControlledCurrentSource vccs = vccss.get(j);
	
						String name = vccs.getValue();
						double val = vccs.getVal();
						String unit = vccs.getUnit();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) vccs.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) vccs.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) vccs.getTGConnectingPointAtIndex(2);
						ELNConnectingPoint cp3 = (ELNConnectingPoint) vccs.getTGConnectingPointAtIndex(3); 
	
						ELNTComponentVoltageControlledCurrentSource elnVCCS = new ELNTComponentVoltageControlledCurrentSource(name, val, unit, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnVCCS);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnVCCS);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnVCCS);
						ELNTConnectingPoint elncp3 = new ELNTConnectingPoint(cp3.getName(), elnVCCS);
						
						elnVCCS.addConnectingPoint(elncp0);
						elnVCCS.addConnectingPoint(elncp1);
						elnVCCS.addConnectingPoint(elncp2);
						elnVCCS.addConnectingPoint(elncp3);
						
						elnMap.put(vccs, elnVCCS);
						elnModule.addVccs(elnVCCS);
						elnComponents.add(elnVCCS);
					}
					List<ELNComponentVoltageControlledVoltageSource> vcvss = module.getAllComponentVoltageControlledVoltageSource();
					for (int j = 0; j < vcvss.size(); j++) {
						ELNComponentVoltageControlledVoltageSource vcvs = vcvss.get(j);
	
						String name = vcvs.getValue();
						double val = vcvs.getVal();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) vcvs.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) vcvs.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) vcvs.getTGConnectingPointAtIndex(2);
						ELNConnectingPoint cp3 = (ELNConnectingPoint) vcvs.getTGConnectingPointAtIndex(3); 
						
						ELNTComponentVoltageControlledVoltageSource elnVCVS = new ELNTComponentVoltageControlledVoltageSource(name, val, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnVCVS);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnVCVS);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnVCVS);
						ELNTConnectingPoint elncp3 = new ELNTConnectingPoint(cp3.getName(), elnVCVS);
						
						elnVCVS.addConnectingPoint(elncp0);
						elnVCVS.addConnectingPoint(elncp1);
						elnVCVS.addConnectingPoint(elncp2);
						elnVCVS.addConnectingPoint(elncp3);
						
						elnMap.put(vcvs, elnVCVS);
						elnModule.addVcvs(elnVCVS);
						elnComponents.add(elnVCVS);
					}
					List<ELNComponentVoltageSinkDE> DE_vsinks = module.getAllComponentVoltageSinkDE();
					for (int j = 0; j < DE_vsinks.size(); j++) {
						ELNComponentVoltageSinkDE DE_vsink = DE_vsinks.get(j);
	
						String name = DE_vsink.getValue();
						double scale = DE_vsink.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) DE_vsink.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) DE_vsink.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) DE_vsink.getTGConnectingPointAtIndex(2);
						
						ELNTComponentVoltageSinkDE elnDE_vsink = new ELNTComponentVoltageSinkDE(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnDE_vsink);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnDE_vsink);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnDE_vsink);
						
						elnDE_vsink.addConnectingPoint(elncp0);
						elnDE_vsink.addConnectingPoint(elncp1);
						elnDE_vsink.addConnectingPoint(elncp2);
						
						elnMap.put(DE_vsink, elnDE_vsink);
						elnModule.addDE_vsink(elnDE_vsink);
						elnComponents.add(elnDE_vsink);
					}	
					List<ELNComponentVoltageSourceDE> DE_vsources = module.getAllComponentVoltageSourceDE();
					for (int j = 0; j < DE_vsources.size(); j++) {
						ELNComponentVoltageSourceDE DE_vsource = DE_vsources.get(j);
	
						String name = DE_vsource.getValue();
						double scale = DE_vsource.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) DE_vsource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) DE_vsource.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) DE_vsource.getTGConnectingPointAtIndex(2);
						
						ELNTComponentVoltageSourceDE elnDE_vsource = new ELNTComponentVoltageSourceDE(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnDE_vsource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnDE_vsource);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnDE_vsource);
						
						elnDE_vsource.addConnectingPoint(elncp0);
						elnDE_vsource.addConnectingPoint(elncp1);
						elnDE_vsource.addConnectingPoint(elncp2);
						
						elnMap.put(DE_vsource, elnDE_vsource);
						elnModule.addDE_vsource(elnDE_vsource);
						elnComponents.add(elnDE_vsource);
					}	
					List<ELNComponentVoltageSinkTDF> TDF_vsinks = module.getAllComponentVoltageSinkTDF();
					for (int j = 0; j < TDF_vsinks.size(); j++) {
						ELNComponentVoltageSinkTDF TDF_vsink = TDF_vsinks.get(j);
	
						String name = TDF_vsink.getValue();
						double scale = TDF_vsink.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) TDF_vsink.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) TDF_vsink.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) TDF_vsink.getTGConnectingPointAtIndex(2);
						
						ELNTComponentVoltageSinkTDF elnTDF_vsink = new ELNTComponentVoltageSinkTDF(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnTDF_vsink);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnTDF_vsink);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnTDF_vsink);
						
						elnTDF_vsink.addConnectingPoint(elncp0);
						elnTDF_vsink.addConnectingPoint(elncp1);
						elnTDF_vsink.addConnectingPoint(elncp2);
						
						elnMap.put(TDF_vsink, elnTDF_vsink);
						elnModule.addTDF_vsink(elnTDF_vsink);
						elnComponents.add(elnTDF_vsink);
					}	
					List<ELNComponentVoltageSourceTDF> TDF_vsources = module.getAllComponentVoltageSourceTDF();
					for (int j = 0; j < TDF_vsources.size(); j++) {
						ELNComponentVoltageSourceTDF TDF_vsource = TDF_vsources.get(j);
	
						String name = TDF_vsource.getValue();
						double scale = TDF_vsource.getScale();
						ELNConnectingPoint cp0 = (ELNConnectingPoint) TDF_vsource.getTGConnectingPointAtIndex(0);
						ELNConnectingPoint cp1 = (ELNConnectingPoint) TDF_vsource.getTGConnectingPointAtIndex(1); 
						ELNConnectingPoint cp2 = (ELNConnectingPoint) TDF_vsource.getTGConnectingPointAtIndex(2);
						
						ELNTComponentVoltageSourceTDF elnTDF_vsource = new ELNTComponentVoltageSourceTDF(name, scale, elnModule);
	
						ELNTConnectingPoint elncp0 = new ELNTConnectingPoint(cp0.getName(), elnTDF_vsource);
						ELNTConnectingPoint elncp1 = new ELNTConnectingPoint(cp1.getName(), elnTDF_vsource);
						ELNTConnectingPoint elncp2 = new ELNTConnectingPoint(cp2.getName(), elnTDF_vsource);
						
						elnTDF_vsource.addConnectingPoint(elncp0);
						elnTDF_vsource.addConnectingPoint(elncp1);
						elnTDF_vsource.addConnectingPoint(elncp2);
						
						elnMap.put(TDF_vsource, elnTDF_vsource);
						elnModule.addTDF_vsource(elnTDF_vsource);
						elnComponents.add(elnTDF_vsource);
					}	
					List<ELNModuleTerminal> moduleTerminals = module.getAllModuleTerminal();
					for (int j = 0; j < moduleTerminals.size(); j++) {
						ELNModuleTerminal moduleTerminal = moduleTerminals.get(j);
	
						String name = moduleTerminal.getValue();
	
						ELNTModuleTerminal elnModuleTerminal = new ELNTModuleTerminal(name, elnModule);
	
						elnMap.put(moduleTerminal, elnModuleTerminal);
						elnModule.addModuleTerminal(elnModuleTerminal);
						elnComponents.add(elnModuleTerminal);
					}
					List<ELNModulePortDE> modulePortsDE = module.getAllModulePortDE();
					for (int j = 0; j < modulePortsDE.size(); j++) {
						ELNModulePortDE modulePortDE = modulePortsDE.get(j);

						String portName = modulePortDE.getValue();
						String type = modulePortDE.getPortType();
						String origin = modulePortDE.getOrigin();

						ELNTModulePortDE elnModulePortDE = new ELNTModulePortDE(portName, type, origin, elnModule);

						elnMap.put(modulePortDE, elnModulePortDE);
						elnModule.addModulePortDE(elnModulePortDE);
						elnComponents.add(elnModulePortDE);
					}
					List<ELNModulePortTDF> modulePortsTDF = module.getAllModulePortTDF();
					for (int j = 0; j < modulePortsTDF.size(); j++) {
						ELNModulePortTDF modulePortTDF = modulePortsTDF.get(j);

						String portName = modulePortTDF.getValue();
						String type = modulePortTDF.getPortType();
						String origin = modulePortTDF.getOrigin();

						ELNTModulePortTDF elnModulePortTDF = new ELNTModulePortTDF(portName, type, origin, elnModule);

						elnMap.put(modulePortTDF, elnModulePortTDF);
						elnModule.addModulePortTDF(elnModulePortTDF);
						elnComponents.add(elnModulePortTDF);
					}
					elnMap.put(module, elnModule);
					elnCluster.addModule(elnModule);
					elnComponents.add(elnModule);
				}
				elnMap.put(cluster, elnCluster);
				elnComponents.add(elnCluster);
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

				ELNTConnectingPoint avConnectingPoint1 = new ELNTConnectingPoint(p1Name, avowner_p1);
				ELNTConnectingPoint avConnectingPoint2 = new ELNTConnectingPoint(p2Name, avowner_p2);

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
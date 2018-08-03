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

package elntranslator;

import java.util.LinkedList;
import java.util.List;

/**
 * Class ELNSpecification
 * List of all the elements in a ELN diagram
 * Creation: 23/07/2018
 * @version 1.0 23/07/2018
 * @author Irina Kit Yan LEE
 */

public class ELNSpecification{
	private List<ELNTComponent> elnComponents;
	private List<ELNTConnector> elnConnectors;

	public ELNSpecification(List<ELNTComponent> _elnComponents, List<ELNTConnector> _elnConnectors){
		elnComponents = _elnComponents ;
		elnConnectors = _elnConnectors ;
	}

	public List<ELNTComponent> getELNComponents(){
		return elnComponents;
	}

	public List<ELNTConnector> getELNConnectors(){
		return elnConnectors;
	}

	public LinkedList<ELNTConnector> getAllConnectorsInCluster(){
		LinkedList<ELNTConnector> cons = new LinkedList<ELNTConnector>();
		for (ELNTConnector con : elnConnectors) {
			if ((con.get_p1().getComponent() instanceof ELNTModuleTerminal && con.get_p2().getComponent() instanceof ELNTModuleTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTModuleTerminal && con.get_p2().getComponent() instanceof ELNTClusterTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTClusterTerminal && con.get_p2().getComponent() instanceof ELNTModuleTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTModulePortDE && con.get_p2().getComponent() instanceof ELNTClusterPortDE)
					|| (con.get_p1().getComponent() instanceof ELNTClusterPortDE && con.get_p2().getComponent() instanceof ELNTModulePortDE)
					|| (con.get_p1().getComponent() instanceof ELNTModulePortTDF && con.get_p2().getComponent() instanceof ELNTClusterPortTDF)
					|| (con.get_p1().getComponent() instanceof ELNTClusterPortTDF && con.get_p2().getComponent() instanceof ELNTModulePortTDF)) {
				cons.add(con);
			}
		}
		return cons;
	}

	public LinkedList<ELNTConnector> getAllConnectorsInModule(){
		LinkedList<ELNTConnector> cons = new LinkedList<ELNTConnector>();
		for (ELNTConnector con : elnConnectors) {
			if (!((con.get_p1().getComponent() instanceof ELNTModuleTerminal && con.get_p2().getComponent() instanceof ELNTModuleTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTModuleTerminal && con.get_p2().getComponent() instanceof ELNTClusterTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTClusterTerminal && con.get_p2().getComponent() instanceof ELNTModuleTerminal)
					|| (con.get_p1().getComponent() instanceof ELNTModulePortDE && con.get_p2().getComponent() instanceof ELNTClusterPortDE)
					|| (con.get_p1().getComponent() instanceof ELNTClusterPortDE && con.get_p2().getComponent() instanceof ELNTModulePortDE)
					|| (con.get_p1().getComponent() instanceof ELNTModulePortTDF && con.get_p2().getComponent() instanceof ELNTClusterPortTDF)
					|| (con.get_p1().getComponent() instanceof ELNTClusterPortTDF && con.get_p2().getComponent() instanceof ELNTModulePortTDF))) {
				cons.add(con);
			}
		}
		return cons;
	}

	public ELNTCluster getCluster(){
		for (ELNTComponent comp : elnComponents) {
			if (comp instanceof ELNTCluster) {
				return (ELNTCluster) comp;
			}
		}
		return null;
	}

	public LinkedList<ELNTClusterTerminal> getAllClusterTerminal(){
		LinkedList<ELNTClusterTerminal> clusterTerminals = new LinkedList<ELNTClusterTerminal>();
		for (ELNTComponent clusterTerminal : elnComponents) {
			if (clusterTerminal instanceof ELNTClusterTerminal) {
				clusterTerminals.add((ELNTClusterTerminal) clusterTerminal);
			}
		}
		return clusterTerminals;
	}

	public LinkedList<ELNTClusterPortDE> getAllClusterPortDE(){
		LinkedList<ELNTClusterPortDE> clusterPortsDE = new LinkedList<ELNTClusterPortDE>();
		for (ELNTComponent clusterPortDE : elnComponents) {
			if (clusterPortDE instanceof ELNTClusterPortDE) {
				clusterPortsDE.add((ELNTClusterPortDE) clusterPortDE);
			}
		}
		return clusterPortsDE;
	}

	public LinkedList<ELNTClusterPortTDF> getAllClusterPortTDF(){
		LinkedList<ELNTClusterPortTDF> clusterPortsTDF = new LinkedList<ELNTClusterPortTDF>();
		for (ELNTComponent clusterPortTDF : elnComponents) {
			if (clusterPortTDF instanceof ELNTClusterPortTDF) {
				clusterPortsTDF.add((ELNTClusterPortTDF) clusterPortTDF);
			}
		}
		return clusterPortsTDF;
	}

	public LinkedList<ELNTComponentCapacitor> getAllComponentCapacitor(){
		LinkedList<ELNTComponentCapacitor> capacitors = new LinkedList<ELNTComponentCapacitor>();
		for (ELNTComponent capacitor : elnComponents) {
			if (capacitor instanceof ELNTComponentCapacitor) {
				capacitors.add((ELNTComponentCapacitor) capacitor);
			}
		}
		return capacitors;
	}

	public LinkedList<ELNTComponentCurrentSinkTDF> getAllComponentCurrentSinkTDF(){
		LinkedList<ELNTComponentCurrentSinkTDF> TDF_isinks = new LinkedList<ELNTComponentCurrentSinkTDF>();
		for (ELNTComponent TDF_isink : elnComponents) {
			if (TDF_isink instanceof ELNTComponentCurrentSinkTDF) {
				TDF_isinks.add((ELNTComponentCurrentSinkTDF) TDF_isink);
			}
		}
		return TDF_isinks;
	}

	public LinkedList<ELNTComponentCurrentSourceTDF> getAllComponentCurrentSourceTDF(){
		LinkedList<ELNTComponentCurrentSourceTDF> TDF_isources = new LinkedList<ELNTComponentCurrentSourceTDF>();
		for (ELNTComponent TDF_isource : elnComponents) {
			if (TDF_isource instanceof ELNTComponentCurrentSourceTDF) {
				TDF_isources.add((ELNTComponentCurrentSourceTDF) TDF_isource);
			}
		}
		return TDF_isources;
	}

	public LinkedList<ELNTComponentIdealTransformer> getAllComponentIdealTransformer(){
		LinkedList<ELNTComponentIdealTransformer> idealTransformers = new LinkedList<ELNTComponentIdealTransformer>();
		for (ELNTComponent idealTransformer : elnComponents) {
			if (idealTransformer instanceof ELNTComponentIdealTransformer) {
				idealTransformers.add((ELNTComponentIdealTransformer) idealTransformer);
			}
		}
		return idealTransformers;
	}

	public LinkedList<ELNTComponentIndependentCurrentSource> getAllComponentIndependentCurrentSource(){
		LinkedList<ELNTComponentIndependentCurrentSource> isources = new LinkedList<ELNTComponentIndependentCurrentSource>();
		for (ELNTComponent isource : elnComponents) {
			if (isource instanceof ELNTComponentIndependentCurrentSource) {
				isources.add((ELNTComponentIndependentCurrentSource) isource);
			}
		}
		return isources;
	}

	public LinkedList<ELNTComponentIndependentVoltageSource> getAllComponentIndependentVoltageSource(){
		LinkedList<ELNTComponentIndependentVoltageSource> vsources = new LinkedList<ELNTComponentIndependentVoltageSource>();
		for (ELNTComponent vsource : elnComponents) {
			if (vsource instanceof ELNTComponentIndependentVoltageSource) {
				vsources.add((ELNTComponentIndependentVoltageSource) vsource);
			}
		}
		return vsources;
	}

	public LinkedList<ELNTComponentInductor> getAllComponentInductor(){
		LinkedList<ELNTComponentInductor> inductors = new LinkedList<ELNTComponentInductor>();
		for (ELNTComponent inductor : elnComponents) {
			if (inductor instanceof ELNTComponentInductor) {
				inductors.add((ELNTComponentInductor) inductor);
			}
		}
		return inductors;
	}

	public LinkedList<ELNTComponentNodeRef> getAllComponentNodeRef(){
		LinkedList<ELNTComponentNodeRef> nodeRefs = new LinkedList<ELNTComponentNodeRef>();
		for (ELNTComponent nodeRef : elnComponents) {
			if (nodeRef instanceof ELNTComponentNodeRef) {
				nodeRefs.add((ELNTComponentNodeRef) nodeRef);
			}
		}
		return nodeRefs;
	}

	public LinkedList<ELNTComponentResistor> getAllComponentResistor(){
		LinkedList<ELNTComponentResistor> resistors = new LinkedList<ELNTComponentResistor>();
		for (ELNTComponent resistor : elnComponents) {
			if (resistor instanceof ELNTComponentResistor) {
				resistors.add((ELNTComponentResistor) resistor);
			}
		}
		return resistors;
	}

	public LinkedList<ELNTComponentTransmissionLine> getAllComponentTransmissionLine(){
		LinkedList<ELNTComponentTransmissionLine> transmissionLines = new LinkedList<ELNTComponentTransmissionLine>();
		for (ELNTComponent transmissionLine : elnComponents) {
			if (transmissionLine instanceof ELNTComponentTransmissionLine) {
				transmissionLines.add((ELNTComponentTransmissionLine) transmissionLine);
			}
		}
		return transmissionLines;
	}

	public LinkedList<ELNTComponentVoltageControlledCurrentSource> getAllComponentVoltageControlledCurrentSource(){
		LinkedList<ELNTComponentVoltageControlledCurrentSource> vccss = new LinkedList<ELNTComponentVoltageControlledCurrentSource>();
		for (ELNTComponent vccs : elnComponents) {
			if (vccs instanceof ELNTComponentVoltageControlledCurrentSource) {
				vccss.add((ELNTComponentVoltageControlledCurrentSource) vccs);
			}
		}
		return vccss;
	}

	public LinkedList<ELNTComponentVoltageControlledVoltageSource> getAllComponentVoltageControlledVoltageSource(){
		LinkedList<ELNTComponentVoltageControlledVoltageSource> vcvss = new LinkedList<ELNTComponentVoltageControlledVoltageSource>();
		for (ELNTComponent vcvs : elnComponents) {
			if (vcvs instanceof ELNTComponentVoltageControlledVoltageSource) {
				vcvss.add((ELNTComponentVoltageControlledVoltageSource) vcvs);
			}
		}
		return vcvss;
	}

	public LinkedList<ELNTComponentVoltageSinkTDF> getAllComponentVoltageSinkTDF(){
		LinkedList<ELNTComponentVoltageSinkTDF> TDF_vsinks = new LinkedList<ELNTComponentVoltageSinkTDF>();
		for (ELNTComponent TDF_vsink : elnComponents) {
			if (TDF_vsink instanceof ELNTComponentVoltageSinkTDF) {
				TDF_vsinks.add((ELNTComponentVoltageSinkTDF) TDF_vsink);
			}
		}
		return TDF_vsinks;
	}

	public LinkedList<ELNTComponentVoltageSourceTDF> getAllComponentVoltageSourceTDF(){
		LinkedList<ELNTComponentVoltageSourceTDF> TDF_vsources = new LinkedList<ELNTComponentVoltageSourceTDF>();
		for (ELNTComponent TDF_vsource : elnComponents) {
			if (TDF_vsource instanceof ELNTComponentVoltageSourceTDF) {
				TDF_vsources.add((ELNTComponentVoltageSourceTDF) TDF_vsource);
			}
		}
		return TDF_vsources;
	}

	public LinkedList<ELNTMidPortTerminal> getAllMidPortTerminal(){
		LinkedList<ELNTMidPortTerminal> midPortTerminals = new LinkedList<ELNTMidPortTerminal>();
		for (ELNTComponent midPortTerminal : elnComponents) {
			if (midPortTerminal instanceof ELNTMidPortTerminal) {
				midPortTerminals.add((ELNTMidPortTerminal) midPortTerminal);
			}
		}
		return midPortTerminals;
	}

	public LinkedList<ELNTModule> getAllModule(){
		LinkedList<ELNTModule> modules = new LinkedList<ELNTModule>();
		for (ELNTComponent module : elnComponents) {
			if (module instanceof ELNTModule) {
				modules.add((ELNTModule) module);
			}
		}
		return modules;
	}

	public LinkedList<ELNTModuleTerminal> getAllModuleTerminal(){
		LinkedList<ELNTModuleTerminal> moduleTerminals = new LinkedList<ELNTModuleTerminal>();
		for (ELNTComponent moduleTerminal : elnComponents) {
			if (moduleTerminal instanceof ELNTModuleTerminal) {
				moduleTerminals.add((ELNTModuleTerminal) moduleTerminal);
			}
		}
		return moduleTerminals;
	}

	public LinkedList<ELNTModulePortDE> getAllModulePortDE(){
		LinkedList<ELNTModulePortDE> modulePortsDE = new LinkedList<ELNTModulePortDE>();
		for (ELNTComponent modulePortDE : elnComponents) {
			if (modulePortDE instanceof ELNTModulePortDE) {
				modulePortsDE.add((ELNTModulePortDE) modulePortDE);
			}
		}
		return modulePortsDE;
	}

	public LinkedList<ELNTModulePortTDF> getAllModulePortTDF(){
		LinkedList<ELNTModulePortTDF> modulePortsTDF = new LinkedList<ELNTModulePortTDF>();
		for (ELNTComponent modulePortTDF : elnComponents) {
			if (modulePortTDF instanceof ELNTModulePortTDF) {
				modulePortsTDF.add((ELNTModulePortTDF) modulePortTDF);
			}
		}
		return modulePortsTDF;
	}

	public int getNbClusterTerminal(){
		return (getAllClusterTerminal()).size();
	}

	public int getNbClusterPortDE(){
		return (getAllClusterPortDE()).size();
	}

	public int getNbClusterPortTDF(){
		return (getAllClusterPortTDF()).size();
	}

	public int getNbComponentCapacitor(){
		return (getAllComponentCapacitor()).size();
	}

	public int getNbComponentCurrentSinkTDF(){
		return (getAllComponentCurrentSinkTDF()).size();
	}

	public int getNbComponentCurrentSourceTDF(){
		return (getAllComponentCurrentSourceTDF()).size();
	}

	public int getNbComponentIdealTransformer(){
		return (getAllComponentIdealTransformer()).size();
	}

	public int getNbComponentIndependentCurrentSource(){
		return (getAllComponentIndependentCurrentSource()).size();
	}

	public int getNbComponentIndependentVoltageSource(){
		return (getAllComponentIndependentVoltageSource()).size();
	}

	public int getNbComponentInductor(){
		return (getAllComponentInductor()).size();
	}

	public int getNbComponentNodeRef(){
		return (getAllComponentNodeRef()).size();
	}

	public int getNbComponentResistor(){
		return (getAllComponentResistor()).size();
	}

	public int getNbComponentTransmissionLine(){
		return (getAllComponentTransmissionLine()).size();
	}

	public int getNbComponentVoltageControlledCurrentSource(){
		return (getAllComponentVoltageControlledCurrentSource()).size();
	}

	public int getNbComponentVoltageControlledVoltageSource(){
		return (getAllComponentVoltageControlledVoltageSource()).size();
	}

	public int getNbComponentVoltageSinkTDF(){
		return (getAllComponentVoltageSinkTDF()).size();
	}

	public int getNbComponentVoltageSourceTDF(){
		return (getAllComponentVoltageSourceTDF()).size();
	}

	public int getNbMidPortTerminal(){
		return (getAllMidPortTerminal()).size();
	}

	public int getNbModule(){
		return (getAllModule()).size();
	}

	public int getNbModuleTerminal(){
		return (getAllModuleTerminal()).size();
	}

	public int getNbModulePortDE(){
		return (getAllModulePortDE()).size();
	}

	public int getNbModulePortTDF(){
		return (getAllModulePortTDF()).size();
	}
}
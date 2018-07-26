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

import syscamstranslator.SysCAMSTBlockTDF;

/**
 * Class ELNTModule
 * Parameters of a ELN component : module
 * Creation: 23/07/2018
 * @version 1.0 23/07/2018
 * @author Irina Kit Yan LEE
*/

public class ELNTModule extends ELNTComponent {
	private String name;
	
	private LinkedList<ELNTComponentCapacitor> capacitor;
	private LinkedList<ELNTComponentIdealTransformer> idealTransformer;
	private LinkedList<ELNTComponentIndependentCurrentSource> isource;
	private LinkedList<ELNTComponentIndependentVoltageSource> vsource;
	private LinkedList<ELNTComponentInductor> inductor;
	private LinkedList<ELNTComponentNodeRef> nodeRef;
	private LinkedList<ELNTComponentResistor> resistor;
	private LinkedList<ELNTComponentTransmissionLine> transmissionLine;
	private LinkedList<ELNTComponentVoltageControlledCurrentSource> vccs;
	private LinkedList<ELNTComponentVoltageControlledVoltageSource> vcvs;
	private LinkedList<ELNTComponentCurrentSinkTDF> TDF_isink;
	private LinkedList<ELNTComponentCurrentSourceTDF> TDF_isource;
	private LinkedList<ELNTComponentVoltageSinkTDF> TDF_vsink;
	private LinkedList<ELNTComponentVoltageSourceTDF> TDF_vsource;
	private LinkedList<ELNTComponentModuleTerminal> moduleTerminal;
	
	public ELNTModule(String _name) {
		name = _name;
		capacitor = new LinkedList<ELNTComponentCapacitor>();
		idealTransformer = new LinkedList<ELNTComponentIdealTransformer>();
		isource = new LinkedList<ELNTComponentIndependentCurrentSource>();
		vsource = new LinkedList<ELNTComponentIndependentVoltageSource>();
		inductor = new LinkedList<ELNTComponentInductor>();
		nodeRef = new LinkedList<ELNTComponentNodeRef>();
		resistor = new LinkedList<ELNTComponentResistor>();
		transmissionLine = new LinkedList<ELNTComponentTransmissionLine>();
		vccs = new LinkedList<ELNTComponentVoltageControlledCurrentSource>();
		vcvs = new LinkedList<ELNTComponentVoltageControlledVoltageSource>();
		TDF_isink = new LinkedList<ELNTComponentCurrentSinkTDF>();
		TDF_isource = new LinkedList<ELNTComponentCurrentSourceTDF>();
		TDF_vsink = new LinkedList<ELNTComponentVoltageSinkTDF>();
		TDF_vsource = new LinkedList<ELNTComponentVoltageSourceTDF>();
		moduleTerminal = new LinkedList<ELNTComponentModuleTerminal>();
	}

	public String getName() {
		return name;
	}

	public LinkedList<ELNTComponentCapacitor> getCapacitor() {
		return capacitor;
	}
	
	public void addCapacitor(ELNTComponentCapacitor _capacitor){
		capacitor.add(_capacitor);
	}

	public LinkedList<ELNTComponentIdealTransformer> getIdealTransformer() {
		return idealTransformer;
	}

	public void addIdealTransformer(ELNTComponentIdealTransformer _idealTransformer){
		idealTransformer.add(_idealTransformer);
	}
	
	public LinkedList<ELNTComponentIndependentCurrentSource> getIsource() {
		return isource;
	}

	public void addIsource(ELNTComponentIndependentCurrentSource _isource){
		isource.add(_isource);
	}
	
	public LinkedList<ELNTComponentIndependentVoltageSource> getVsource() {
		return vsource;
	}
	
	public void addVsource(ELNTComponentIndependentVoltageSource _vsource){
		vsource.add(_vsource);
	}

	public LinkedList<ELNTComponentInductor> getInductor() {
		return inductor;
	}

	public void addInductor(ELNTComponentInductor _inductor){
		inductor.add(_inductor);
	}
	
	public LinkedList<ELNTComponentNodeRef> getNodeRef() {
		return nodeRef;
	}
	
	public void addNodeRef(ELNTComponentNodeRef _nodeRef){
		nodeRef.add(_nodeRef);
	}

	public LinkedList<ELNTComponentResistor> getResistor() {
		return resistor;
	}

	public void addResistor(ELNTComponentResistor _resistor){
		resistor.add(_resistor);
	}
	
	public LinkedList<ELNTComponentTransmissionLine> getTransmissionLine() {
		return transmissionLine;
	}
	
	public void addTransmissionLine(ELNTComponentTransmissionLine _transmissionLine){
		transmissionLine.add(_transmissionLine);
	}

	public LinkedList<ELNTComponentVoltageControlledCurrentSource> getVccs() {
		return vccs;
	}

	public void addVccs(ELNTComponentVoltageControlledCurrentSource _vccs){
		vccs.add(_vccs);
	}
	
	public LinkedList<ELNTComponentVoltageControlledVoltageSource> getVcvs() {
		return vcvs;
	}
	
	public void addVcvs(ELNTComponentVoltageControlledVoltageSource _vcvs){
		vcvs.add(_vcvs);
	}

	public LinkedList<ELNTComponentCurrentSinkTDF> getTDF_isink() {
		return TDF_isink;
	}
	
	public void addTDF_isink(ELNTComponentCurrentSinkTDF _TDF_isink){
		TDF_isink.add(_TDF_isink);
	}

	public LinkedList<ELNTComponentCurrentSourceTDF> getTDF_isource() {
		return TDF_isource;
	}
	
	public void addTDF_isource(ELNTComponentCurrentSourceTDF _TDF_isource){
		TDF_isource.add(_TDF_isource);
	}

	public LinkedList<ELNTComponentVoltageSinkTDF> getTDF_vsink() {
		return TDF_vsink;
	}
	
	public void addTDF_vsink(ELNTComponentVoltageSinkTDF _TDF_vsink){
		TDF_vsink.add(_TDF_vsink);
	}

	public LinkedList<ELNTComponentVoltageSourceTDF> getTDF_vsource() {
		return TDF_vsource;
	}

	public void addTDF_vsource(ELNTComponentVoltageSourceTDF _TDF_vsource){
		TDF_vsource.add(_TDF_vsource);
	}
	
	public LinkedList<ELNTComponentModuleTerminal> getModuleTerminal() {
		return moduleTerminal;
	}

	public void addModuleTerminal(ELNTComponentModuleTerminal _moduleTerminal){
		moduleTerminal.add(_moduleTerminal);
	}
}
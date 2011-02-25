/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
*
* /**
* Class AvatarRequirementPanelTranslator
* Creation: 17/02/2011
* @author Ludovic APVRILLE
* @see
*/

package ui;

import java.util.*;



import myutil.*;
import ui.avatarpd.*;
import tepe.*;


public class AvatarRequirementPanelTranslator {
	protected Vector checkingErrors, warnings;
	protected CorrespondanceTGElement listE; // usual list
	
	public AvatarRequirementPanelTranslator() {
	}
	
	public void reinit() {
		checkingErrors = new Vector();
		warnings = new Vector();
		listE = new CorrespondanceTGElement();
	}
	
	public Vector getErrors() {
		return checkingErrors;
	}
	
	public Vector getWarnings() {
		return warnings;
	}
	
	public CorrespondanceTGElement getCorrespondanceTGElement() {
		return listE;
	}
	
	public TEPE generateTEPESpecification(AvatarPDPanel _apdp) {
		TEPE tepe = new TEPE(_apdp.getName(), _apdp);
		TraceManager.addDev("Creating new TEPE named " + tepe.getName());
		
		reinit();
		makeComponents(tepe, _apdp);
		makeLinksBetweenComponents(tepe, _apdp);
		
		TraceManager.addDev(tepe.toString());
		
		return tepe;
	}
	
	public void makeComponents(TEPE _tepe, AvatarPDPanel _apdp) {
		TGComponent tgc;
		
		ListIterator iterator =  _apdp.getComponentList().listIterator();
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			// Alias
			if (tgc instanceof AvatarPDAlias) {
				TEPEAliasComponent tepealiasc = new TEPEAliasComponent("Alias", tgc);
				tepealiasc.setValue(tgc.getValue());
				_tepe.add(tepealiasc);
				listE.addCor(tepealiasc, tgc);
			
			// Attributes
			} else if (tgc instanceof AvatarPDAttribute) {
				TEPEAttributeComponent tepeattributec = new TEPEAttributeComponent("Attribute", tgc, "No__Block");
				tepeattributec.setValue(tgc.getValue());
				_tepe.add(tepeattributec);
				listE.addCor(tepeattributec, tgc);
				
			// Block
			} else if (tgc instanceof AvatarPDBlock) {
				Vector<AvatarPDAttribute> va = ((AvatarPDBlock)tgc).getAllAvatarPDAttribute();
				for(AvatarPDAttribute attr: va) {
					TEPEAttributeComponent tepeattributec = new TEPEAttributeComponent("Attribute", attr, attr.getFather().getValue());
					tepeattributec.setValue(attr.getValue());
					_tepe.add(tepeattributec);
					listE.addCor(tepeattributec, attr);
				}
				Vector<AvatarPDSignal> vs = ((AvatarPDBlock)tgc).getAllAvatarPDSignal();
				for(AvatarPDSignal sig: vs) {
					TEPESignalComponent tepesignalc = new TEPESignalComponent("Signal", sig, sig.getFather().getValue());
					tepesignalc.setValue(sig.getValue());
					_tepe.add(tepesignalc);
					listE.addCor(tepesignalc, sig);
				}
				
			// Equations
			} else if (tgc instanceof AvatarPDBoolEq) {
				TEPEEquationComponent tepeequationc = new TEPEEquationComponent("Equation", tgc);
				tepeequationc.setValue(tgc.getValue());
				_tepe.add(tepeequationc);
				listE.addCor(tepeequationc, tgc);
				
			// Logical constraint
			} else if (tgc instanceof AvatarPDLogicalConstraint) {
				AvatarPDLogicalConstraint apdlc = (AvatarPDLogicalConstraint)tgc;
				TEPELogicalConstraintComponent tepelogicalc;
				if (apdlc.getValue().compareTo(AvatarPDLogicalConstraint.STEREOTYPES[0]) ==0) {
					// no sequence
					tepelogicalc = new TEPELogicalConstraintComponent("Logical constraint", tgc, TEPELogicalConstraintComponent.NO_SEQUENCE);
				} else {
					// Sequence
					tepelogicalc = new TEPELogicalConstraintComponent("Logical sequence", tgc, TEPELogicalConstraintComponent.SEQUENCE);
				}
				
				tepelogicalc.setValue(tgc.getValue());
				_tepe.add(tepelogicalc);
				listE.addCor(tepelogicalc, tgc);
				
			// Property
			} else if (tgc instanceof AvatarPDProperty) {
				AvatarPDProperty apdp = (AvatarPDProperty)tgc;
				
				int type = 0;
				if (apdp.isLiveness()) {
					type = TEPEPropertyComponent.LIVENESS;
				} else if (apdp.isNotLiveness()) {
					type = TEPEPropertyComponent.NON_LIVENESS;
				} else if (apdp.isRechability()) {
					type = TEPEPropertyComponent.REACHABILITY;
				} else if (apdp.isNotRechability()) {
					type = TEPEPropertyComponent.NON_REACHABILITY;
				}
				
				TEPEPropertyComponent tepepropertyc = new TEPEPropertyComponent("Property", tgc, type);
				tepepropertyc.setValue(tgc.getValue());
				_tepe.add(tepepropertyc);
				listE.addCor(tepepropertyc, tgc);
			
			} else if (tgc instanceof AvatarPDPropertyRelation) {
				AvatarPDPropertyRelation apdpr = (AvatarPDPropertyRelation)tgc;
				
				int type = 0;
				if (apdpr.isOr()) {
					type = TEPEPropertyOperatorComponent.OR;
				} else if (apdpr.isAnd()) {
					type = TEPEPropertyOperatorComponent.AND;
				} else if (apdpr.isImply()) {
					type = TEPEPropertyOperatorComponent.IMPLY;
				} else if (apdpr.isEquivalent()) {
					type = TEPEPropertyOperatorComponent.EQUIVALENT;
				}
				
				TEPEPropertyOperatorComponent tepepropertyoperatorc = new TEPEPropertyOperatorComponent("Property operator", tgc, type);
				tepepropertyoperatorc.setValue(tgc.getValue());
				_tepe.add(tepepropertyoperatorc);
				listE.addCor(tepepropertyoperatorc, tgc);
				
			// Signal 
			} else if (tgc instanceof AvatarPDSignal) {
				TEPESignalComponent tepesignalc = new TEPESignalComponent("Signal", tgc, tgc.getFather().getValue());
				tepesignalc.setValue(tgc.getValue());
				_tepe.add(tepesignalc);
				listE.addCor(tepesignalc, tgc);
				
			// Temporal constraint
			} else if (tgc instanceof AvatarPDTemporalConstraint) {
				TEPETimeConstraintComponent tepetimeconstraintc = new TEPETimeConstraintComponent("Time Constraint", tgc);
				tepetimeconstraintc.setValue(tgc.getValue());
				_tepe.add(tepetimeconstraintc);
				listE.addCor(tepetimeconstraintc, tgc);
			}
		}
	}
	
	public void makeLinksBetweenComponents(TEPE _tepe, AvatarPDPanel _apdp) {
		TGComponent tgc, tgc1, tgc2;
		ListIterator iterator =  _apdp.getComponentList().listIterator();
		TEPEComponent element1, element2;
		
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			if (tgc instanceof AvatarPDAttributeConnector) {
				AvatarPDAttributeConnector apdaco = (AvatarPDAttributeConnector)tgc;
				tgc1 = _apdp.getComponentToWhichBelongs(apdaco.getTGConnectingPointP1());
				tgc2 = _apdp.getComponentToWhichBelongs(apdaco.getTGConnectingPointP2());
				if ((tgc1 == null) || (tgc2 == null)) {
					TraceManager.addDev("Tgcs null in Avatar translation");
				} else {
					element1 = (TEPEComponent)(listE.getObject(tgc1));
					element2 = (TEPEComponent)(listE.getObject(tgc2));
					if ((element1 != null) && (element2 != null)) {
						//TraceManager.addDev("Adding output / input");
						element1.addOutAttributeComponent(element2);
						element2.addInAttributeComponent(element1);
					}
				}
			} else if (tgc instanceof AvatarPDPropertyConnector) {
				AvatarPDPropertyConnector apdpco = (AvatarPDPropertyConnector)tgc;
				tgc1 = _apdp.getComponentToWhichBelongs(apdpco.getTGConnectingPointP1());
				tgc2 = _apdp.getComponentToWhichBelongs(apdpco.getTGConnectingPointP2());
				if ((tgc1 == null) || (tgc2 == null)) {
					TraceManager.addDev("Tgcs null in Avatar translation");
				} else {
					element1 = (TEPEComponent)(listE.getObject(tgc1));
					element2 = (TEPEComponent)(listE.getObject(tgc2));
					if ((element1 != null) && (element2 != null)) {
						//TraceManager.addDev("Adding output / input");
						element1.addOutPropertyComponent(element2);
						
						// Must know whether it is negated, or not
						if (!(tgc2 instanceof AvatarPDPropertyRelation)) {
							element2.addInPropertyComponent(element1);
							if (apdpco.isNegated()) {
								element2.addInNegatedProperty(new Boolean(true));
							} else {
								element2.addInNegatedProperty(new Boolean(false));
							}
						}
					}
				}
			} else if (tgc instanceof AvatarPDSignalConnector) {
				AvatarPDSignalConnector apdsco = (AvatarPDSignalConnector)tgc;
				tgc1 = _apdp.getComponentToWhichBelongs(apdsco.getTGConnectingPointP1());
				tgc2 = _apdp.getComponentToWhichBelongs(apdsco.getTGConnectingPointP2());
				if ((tgc1 == null) || (tgc2 == null)) {
					TraceManager.addDev("Tgcs null in Avatar translation");
				} else {
					element1 = (TEPEComponent)(listE.getObject(tgc1));
					element2 = (TEPEComponent)(listE.getObject(tgc2));
					if ((element1 != null) && (element2 != null)) {
						element1.addOutAttributeComponent(element2);
						
						// Must know whether it is negated, or not
						if (apdsco.getTGConnectingPointP2() instanceof AvatarPDForbiddenSignalConnectingPoint) {
							element2.addInNegatedSignalComponent(element1);
						} else {
							// Must enforce order of connectors
							if ((tgc2 instanceof AvatarPDLogicalConstraint) || (tgc2 instanceof AvatarPDTemporalConstraint)) {
								// Must enforce order of connectors
								// Done afterwards
							} else {
								element2.addInSignalComponent(element1);
							}
						}
						
					}
				}
			}
		}
		
		// Enforcing order in AvatarPDLogicalConstraint
		iterator =  _apdp.getComponentList().listIterator();
		TGConnector tgco;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			if (tgc instanceof AvatarPDLogicalConstraint) {
				for(int i=2; i<8; i++) {
					tgco = _apdp.getConnectorConnectedTo(tgc.getTGConnectingPointAtIndex(i));
					if (tgco != null) {
						AvatarPDSignalConnector apdsco = (AvatarPDSignalConnector)tgco;
						tgc1 = _apdp.getComponentToWhichBelongs(apdsco.getTGConnectingPointP1());
						element1 = (TEPEComponent)(listE.getObject(tgc1));
						element2 = (TEPEComponent)(listE.getObject(tgc));
						if ((element1 != null) && (element2 != null)) {
							element2.addInSignalComponent(element1);
						}
					}
				}
			} else if (tgc instanceof AvatarPDTemporalConstraint) {
				for(int i=2; i<4; i++) {
					tgco = _apdp.getConnectorConnectedTo(tgc.getTGConnectingPointAtIndex(i));
					if (tgco != null) {
						AvatarPDSignalConnector apdsco = (AvatarPDSignalConnector)tgco;
						tgc1 = _apdp.getComponentToWhichBelongs(apdsco.getTGConnectingPointP1());
						element1 = (TEPEComponent)(listE.getObject(tgc1));
						element2 = (TEPEComponent)(listE.getObject(tgc));
						if ((element1 != null) && (element2 != null)) {
							element2.addInSignalComponent(element1);
						}
					}
				}
			} else if (tgc instanceof AvatarPDPropertyRelation) {
				for(int i=0; i<8; i++) {
					tgco = _apdp.getConnectorConnectedTo(tgc.getTGConnectingPointAtIndex(i));
					if (tgco != null) {
						AvatarPDPropertyConnector apdpco = (AvatarPDPropertyConnector)tgco;
						tgc1 = _apdp.getComponentToWhichBelongs(apdpco.getTGConnectingPointP1());
						element1 = (TEPEComponent)(listE.getObject(tgc1));
						element2 = (TEPEComponent)(listE.getObject(tgc));
						if ((element1 != null) && (element2 != null)) {
							element2.addInPropertyComponent(element1);
							if (apdpco.isNegated()) {
								element2.addInNegatedProperty(new Boolean(true));
							} else {
								element2.addInNegatedProperty(new Boolean(false));
							}
						}
					}
				}
			}
		}
		
	}
	
}

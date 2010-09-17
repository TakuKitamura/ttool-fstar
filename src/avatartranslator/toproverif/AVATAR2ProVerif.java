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
* Class AVATAR2ProVerif
* Creation: 03/09/2010
* @version 1.1 03/09/2010
* @author Ludovic APVRILLE
* @see
*/

package avatartranslator.toproverif;

import java.awt.*;
import java.util.*;

import proverifspec.*;
import myutil.*;
import avatartranslator.*;

public class AVATAR2ProVerif {

	private final static String UNKNOWN = "UNKNOWN";

	
	private final static String BOOLEAN_DATA_HEADER = "(* Boolean return types *)\ndata true/0.\ndata false/0.\n";
	private final static String FUNC_DATA_HEADER = "(* Functions data *)\ndata " + UNKNOWN + "/0.\n";
	
	private final static String PK_HEADER = "(* Public key cryptography *)\nfun pk/1.\nfun encrypt/2.\nreduc decrypt(encrypt(x,pk(y)),y) = x.\n";
	private final static String SK_HEADER = "(* Symmetric key cryptography *)\nfun sencrypt/2.\nreduc sdecrypt(sencrypt(x,k),k) = x.\n";
	private final static String MAC_HEADER = "(* MAC *)\nfun MAC/2.\nreduc verifyMAC(m, k, MAC(m, k)) = true.\n";
	private final static String CONCAT_HEADER = "(* CONCAT *)\nfun concat/5.\nreduc get1(concat(m1, m2, m3, m4, m5))= m1.\nreduc get2(concat(m1, m2, m3, m4, m5))= m2.\nreduc get3(concat(m1, m2, m3, m4, m5))= m3.\nreduc get4(concat(m1, m2, m3, m4, m5))= m4.\nreduc get5(concat(m1, m2, m3, m4, m5))= m5.\n";
	
	private ProVerifSpec spec;
	private AvatarSpecification avspec;
	
	private Vector warnings;
	

	public AVATAR2ProVerif(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	public void saveInFile(String path) throws FileException {
		FileUtils.saveFile(path + "pvspec", spec.makeSpec());
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize, boolean _stateReachability) {
		warnings = new Vector();
		spec = new ProVerifSpec();
		
		avspec.removeCompositeStates();
		avspec.removeTimers();
		
		makeHeader(_stateReachability);
		
		makeBlocks();
		
		makeStartingProcess();
		
		//TraceManager.addDev("->   Spec:" + avspec.toString());
		
		
		// Deal with blocks
		//translateBlocks();
		
		//translationRelations();
		
		//makeGlobal();
		
		
		// Generate system
		//makeGlobal(effectiveNb);
		//makeParallel(nb);
		
		
		//makeSystem();
		
		/*if (_optimize) {
			spec.optimize();
		}*/
		
		
		return spec;
	}
	
	public void makeHeader(boolean _stateReachability) {
		spec.addToGlobalSpecification(BOOLEAN_DATA_HEADER + "\n");
		spec.addToGlobalSpecification(FUNC_DATA_HEADER + "\n");
		
		spec.addToGlobalSpecification(PK_HEADER + "\n");
		spec.addToGlobalSpecification(SK_HEADER + "\n");
		spec.addToGlobalSpecification(MAC_HEADER + "\n");
		spec.addToGlobalSpecification(CONCAT_HEADER + "\n");
		
		spec.addToGlobalSpecification("\n(* Channel *)\nfree ch.\n");
		spec.addToGlobalSpecification("\n(* Channel *)\nprivate free chprivate.\n");
		
		/* Parse all attributes declared by blocks and declare them as "private free" */
		/*LinkedList<AvatarBlock> blocks = avatarspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			spec.addToGlobalSpecification("\n(* Block " + block.getName() + " : variables *)\n");
			for(AvatarAttribute attribute: block.getAttributes()) {
				spec.addToGlobalSpecification("private free " + attribute.getName() + ".\n");
			}
		}*/
		
		/* Queries */
		/* Parse all attributes starting with "secret" and declare them as non accesible to attacker" */
		spec.addToGlobalSpecification("\n(* Queries *)\n");
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			for(AvatarAttribute attribute: block.getAttributes()) {
				// Attribute is preinitialized if it is in a secret pragma
				//TraceManager.addDev("Testing secret of " + block.getName() + "." + attribute.getName() + " ?");
				if (hasSecretPragmaWithAttribute(block.getName(), attribute.getName())) {
					//TraceManager.addDev("Secret!");
					spec.addToGlobalSpecification("private free " + attribute.getName() + ".\n");
					spec.addToGlobalSpecification("query attacker:" + attribute.getName() + ".\n\n");
				}
			}
			// Queries for states
			if (_stateReachability) {
				for(AvatarStateMachineElement asme: block.getStateMachine().getListOfElements()) {
					if (asme instanceof AvatarState) {
						spec.addToGlobalSpecification("query ev:" + "enteringState__" + block.getName() + "__" + asme.getName() + "().\n");
					}
				}
			}
		}
		

	}
	
	public boolean hasSecretPragmaWithAttribute(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		
		for(String pragma: pragmas) {
			if (isSecretPragma(pragma)) {
				tmp = pragma.substring(7, pragma.length()).trim();
				
				//TraceManager.addDev("Testing prama: " + tmp);
				
				if (tmp.length() == 0) {
					return false;
				}
				
				tmps = tmp.split(" ");
				for(int i=0; i<tmps.length; i++) {
					tmp = tmps[i];
					//TraceManager.addDev("Testing with: " + tmp);
					if (tmp.length() > 0) {
						index = tmp.indexOf('.');
						if (index != -1) {
							try {
								if (tmp.substring(0, index).compareTo(_blockName) == 0) {
									if (tmp.substring(index+1, tmp.length()).compareTo(attributeName) == 0) {
										return true;
									}
								}
							} catch (Exception e) {
								TraceManager.addDev("Error on testing pragma");
							}
						}
					}
				}
				
			}
		}
		
		return false;
	}
	
	public boolean hasInitialCommonKnowledgePragmaWithAttribute(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		
		for(String pragma: pragmas) {
			if (isInitialCommonKnowledgePragma(pragma)) {
				tmp = pragma.substring(23, pragma.length()).trim();
				
				//TraceManager.addDev("Testing pragma: " + tmp);
				
				if (tmp.length() == 0) {
					return false;
				}
				
				tmps = tmp.split(" ");
				for(int i=0; i<tmps.length; i++) {
					tmp = tmps[i];
					//TraceManager.addDev("Testing with: " + tmp);
					if (tmp.length() > 0) {
						index = tmp.indexOf('.');
						if (index != -1) {
							try {
								if (tmp.substring(0, index).compareTo(_blockName) == 0) {
									if (tmp.substring(index+1, tmp.length()).compareTo(attributeName) == 0) {
										return true;
									}
								}
							} catch (Exception e) {
								TraceManager.addDev("Error on testing pragma");
							}
						}
					}
				}
				
			}
		}
		
		return false;
	}
	
	public boolean isSecretPragma(String _pragma) {
		return _pragma.startsWith("Secret ");
	}
	
	public boolean isInitialCommonKnowledgePragma(String _pragma) {
		return _pragma.startsWith("InitialCommonKnowledge ");
	}
	
	public String[] getListOfBlockParams(String _pragma) {
		String s = _pragma;
		
		if (isSecretPragma(s)) {
			s = s.substring(7, s.length()).trim();
		} else if (isInitialCommonKnowledgePragma(s)) {
			s = s.substring(23, s.length()).trim();
		} else {
			return null;
		}
		
		return s.split(" ");
	}
	
	public void makeStartingProcess() {
		String action = "";
		String tmp;
		int index;
		boolean found;
		
		ProVerifProcess p = new ProVerifProcess();
		p.processName = "starting__";
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		
		LinkedList<String> createdVariables = new LinkedList<String>();
		String[] list;
		String blockName, paramName;
		
		for(String pragma: avspec.getPragmas()) {
			if (isInitialCommonKnowledgePragma(pragma)) {
				// Identify each blockName / paramName
				list = getListOfBlockParams(pragma);
				
				for(int i=0; i<list.length; i++) {
					tmp = list[i];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						
						// Verify whether they are secret or not
						if (!hasSecretPragmaWithAttribute(blockName, paramName)) {
							found = false;
							// If not, add them if not already added
							for(String st: createdVariables) {
								if (st.compareTo(paramName) == 0) {
									found = true;
									break;
								}
							}
							if (!found) {
								action += "new " + paramName + ";\n";
								createdVariables.add(paramName);
							}
						}
					}
				}
			}
		}
		
		action += "(";
		index = 0;
		for(AvatarBlock block: blocks) {
			if (index != 0) {
				action += " | ";
			} 
			index ++;
			action += "(!" + block.getName() + "__0)";
		}
		action += ")";
		p.processLines.add(action);
		spec.addProcess(p);
		spec.setStartingProcess(p);
	}
	
	public void makeBlocks() {
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		for(AvatarBlock block: blocks) {
			makeBlock(block);
		}
	}
	
	public void makeBlock(AvatarBlock ab) {
		LinkedList<ProVerifProcess> tmpprocesses = new LinkedList<ProVerifProcess>();
		LinkedList<AvatarState> states = new LinkedList<AvatarState>();
		
		// First process : variable declaration
		ProVerifProcess p = new ProVerifProcess(ab.getName() + "__0");
		spec.addProcess(p);
		
		for(AvatarAttribute aa: ab.getAttributes()) {
			//TraceManager.addDev("Testing: " + ab.getName() + "." + aa.getName());
			if ((!hasInitialCommonKnowledgePragmaWithAttribute(ab.getName(), aa.getName())) && (!(hasSecretPragmaWithAttribute(ab.getName(), aa.getName())))) {
					TraceManager.addDev("  Adding: " + aa.getName());
					addLine(p, "new " + aa.getName());
			}
		}
		
		AvatarStateMachine asm = ab.getStateMachine();
		AvatarStartState ass = asm.getStartState();
		
		makeBlockProcesses(ab, asm, ass.getNext(0), p, tmpprocesses, states, null);
	}
	
	public void makeBlockProcesses(AvatarBlock _block, AvatarStateMachine _asm, AvatarStateMachineElement _asme, ProVerifProcess _p, LinkedList<ProVerifProcess> _processes, LinkedList<AvatarState> _states, String _choiceInfo) {
		AvatarSignal as;
		AvatarActionOnSignal aaos;
		AvatarTransition at;
		ProVerifProcess p, ptmp, ptmp1, ptmp2;
		String tmp, name, value, term;
		int i, j;
		int index0, index1;
		AvatarMethod am;
		boolean found;
		
		// Null element
		if (_asme == null) {
			terminate0Process(_p);
			return;
		}
		
		// Stop state
		if (_asme instanceof AvatarStopState) {
			terminate0Process(_p);
			return;
			
		// Action on signal
		} else if (_asme instanceof AvatarActionOnSignal){
			aaos = (AvatarActionOnSignal)_asme;
			as = aaos.getSignal();
			if (as.isOut()) {
				tmp ="out";
			} else {
				tmp = "in";
			}
			
			tmp+="(ch, ";
			
			for(i=0; i<aaos.getNbOfValues(); i++) {
				if (i>0) {
					tmp += ", ";
				}
				tmp += aaos.getValue(i);
			}
			tmp += ")";
			addLine(_p, tmp);
			makeBlockProcesses(_block, _asm, _asme.getNext(0), _p, _processes, _states, null);
			
			
		// State
		} else if (_asme instanceof AvatarState){
			i = _states.indexOf(_asme);
			if (i != -1) {
				// State has already been met
				// Must branch to the corresponding process
				p = _processes.get(i);
				addLine(_p, p.processName + "()");
				return;
				
			} else {
				// New state
				// We create a new process for each state
				p = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
				spec.addProcess(p);
				_processes.add(p);
				_states.add((AvatarState)_asme);
				addLine(p, "event enteringState__" + _block.getName() + "__" + _asme.getName() + "()");
				
				// Calling the new process from the old one
				addLine(_p, p.processName);
				terminateProcess(_p);
				
				// Making the new process (the old one is finished);
				if (_asme.nbOfNexts() ==0) {
					terminateProcess(p);
					return;
				}
				
				if (_asme.nbOfNexts() ==1) {
					makeBlockProcesses(_block, _asm, _asme.getNext(0), p, _processes, _states, null);
					return ;
				} 
							
				
				if (_asme.hasElseChoiceType1()) {
					TraceManager.addDev("Found a else choice");
					ProVerifProcess pvp[] = new ProVerifProcess[_asme.nbOfNexts()];
					tmp = "(";
					for(i=0; i<_asme.nbOfNexts(); i++) {
						if (i>0) {
							tmp += " | ";
						}
						// Creating a new process
						pvp[i] = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
						spec.addProcess(pvp[i]);
						_processes.add(pvp[i]);
						_states.add((AvatarState)_asme);
						tmp += "(" + _block.getName() + "__" + (_processes.size()) + ")";
					}
					tmp += ")";
					addLine(p, tmp);
					for(i=0; i<_asme.nbOfNexts(); i++) {
						makeBlockProcesses(_block, _asm, _asme.getNext(i), pvp[i], _processes, _states, null);
					}
					terminateProcess(p);
					
				} else {
					
					// Must handle the choice between several transitions
					// Must select the first transition to analyse non-deterministically
					// Make a process for all new following
					addLine(p, "new choice__" + _asme.getName());
					addLine(p, "out(chprivate, choice__" + _asme.getName() + ")");
					
					
					ProVerifProcess pvp[] = new ProVerifProcess[_asme.nbOfNexts()];
					tmp = "(";
					for(i=0; i<_asme.nbOfNexts(); i++) {
						if (i>0) {
							tmp += " | ";
						}
						// Creating a new process
						pvp[i] = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
						spec.addProcess(pvp[i]);
						_processes.add(pvp[i]);
						_states.add((AvatarState)_asme);
						tmp += "(" + _block.getName() + "__" + (_processes.size()) + ")";
					}
					tmp += ")";
					addLine(p, tmp);
					for(i=0; i<_asme.nbOfNexts(); i++) {
						makeBlockProcesses(_block, _asm, _asme.getNext(i), pvp[i], _processes, _states, _asme.getName());
					}
					terminateProcess(p);
				}
			}
			
		// Transition
		} else if (_asme instanceof AvatarTransition) {
			at = (AvatarTransition)_asme;
			// Guard
			if (at.isGuarded()) {
				tmp = modifyGuard(at.getGuard());
				if (tmp != null) {
					TraceManager.addDev("   Adding guard: " + tmp);
					addLineNoEnd(_p, "if " + tmp + " then");
				}
			}
			
			
			// Transition from a state -> this transition must be the one selected
			if (_choiceInfo != null) {
				addLine(_p, "in(chprivate, m__)");
				tmp = "if choice__" + _choiceInfo + " = m__ then";
				addLineNoEnd(_p, tmp);
			} 
				
			// Temporal operators are ignored
			// Only functions are taken into account
			p = _p;
			for(i=0; i<at.getNbOfAction(); i++) {
				tmp = at.getAction(i);
				TraceManager.addDev("Found action: " + tmp);
				if (!AvatarSpecification.isAVariableSettingString(tmp)) {
					TraceManager.addDev("Found function: " + tmp);
					index0 = tmp.indexOf('=');
					index1 = tmp.indexOf('(');
					term = tmp.substring(0, index0).trim();
					if ((index0 == -1) || (index1 == -1) || (index0 > index1) || (term.length() == 0)) {
						addLineNoEnd(p, "let " + tmp + " in ");
					} else {
						found = false;
						name = tmp.substring(index0+1, index1).trim();
						am = _block.getAvatarMethodWithName(name);
						if (am != null) {
							LinkedList<AvatarAttribute> list = am.getListOfReturnAttributes();
							if (list.size() == 1) {
								if (list.get(0).getType() == AvatarType.BOOLEAN) {
									found = true;
								}
							}
						}
						
						
						if ((found) && (name.compareTo("verifyMAC") == 0)){
							// Verify MAC!
							index0 = tmp.indexOf(')');
							if (index0 == -1) {
								index0 = tmp.length();
							}
							value = tmp.substring(index1+1, index0).trim();
							String[] values = value.split(",");
							if (values.length < 3) {
								addLineNoEnd(p, "let " + tmp + " in");
							} else {
								addLineNoEnd(p, "let MAC__tmp = MAC(" + values[0].trim() + " , " + values[1].trim() + ") in");
								//addLine(p, "new choice__mac");
								//addLine(p, "out(chprivate, choice__mac)");
								
								ptmp1 = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
								spec.addProcess(ptmp1);
								_processes.add(ptmp1);
								_states.add(null);
								
								ptmp2 = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
								spec.addProcess(ptmp2);
								_processes.add(ptmp2);
								_states.add(null);
								
								addLineNoEnd(p, "((" + ptmp1.processName + ")|(" + ptmp2.processName + "))."); 
								
								ptmp = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
								spec.addProcess(ptmp);
								_processes.add(ptmp);
								_states.add(null);
								
								addLineNoEnd(ptmp1, "if MAC__tmp = " + values[2].trim() + " then");
								//addLine(ptmp1, "in(chprivate, m__)");
								//addLineNoEnd(ptmp1, "if m__ = choice__mac then");
								addLineNoEnd(ptmp1, "let " + term + "= true in");
								addLineNoEnd(ptmp1, ptmp.processName + ".");
								
								addLineNoEnd(ptmp2, "if MAC__tmp <> " + values[2].trim() + " then");
								//addLine(ptmp2, "in(chprivate, m__)");
								//addLineNoEnd(ptmp2, "if m__ = choice__mac then");
								addLineNoEnd(ptmp2, "let " + term + "= false in");
								addLineNoEnd(ptmp2, ptmp.processName + ".");
								
								/*addLineNoEnd(p, "let MAC__tmp = MAC(" + values[0].trim() + " , " + values[1].trim() + ") in");
								addLineNoEnd(p, "if MAC__tmp =  " + values[2].trim() + " then");
								addLineNoEnd(p, "let " + term + "= true in");
								addLineNoEnd(p, ptmp.processName);
								addLineNoEnd(p, "else");
								addLineNoEnd(p, "let " + term + "= false in");
								addLineNoEnd(p, ptmp.processName + ".");*/
								p = ptmp;
							}
						} else {
							addLineNoEnd(p, "let " + tmp + " in");
						}
						
						
					}
					
				} else if (AvatarSpecification.isABasicVariableSettingString(tmp)) {
					TraceManager.addDev("Found function: " + tmp);
					addLineNoEnd(p, "let " + tmp + " in ");
				}
			}
			
			makeBlockProcesses(_block, _asm, _asme.getNext(0), p, _processes, _states, null);
			
		// Ignored elements
		} else {
			// Go to the next element
			makeBlockProcesses(_block, _asm, _asme.getNext(0), _p, _processes, _states, null);
		}
	}
	

	// Supported guards: a == b, not(a == b)
	// -> transformed into a = b, a <> b
	// Returns nulls otherwise
	public String modifyGuard(String _guard) {
		String[] ab;
		
		TraceManager.addDev(" -> Analyzing guard: " + _guard);
		
		String s = Conversion.replaceAllString(_guard, "[", "");
		s = Conversion.replaceAllString(s, "]", "").trim();
		s = Conversion.replaceAllString(s, " ", "");
		
		if (s.startsWith("not(")) {
			if (s.endsWith(")")) {
					s = s.substring(4, s.length()-1);
					// Should have a "a == b";
					ab = getEqualGuard(s);
					if (ab == null) {
						return null;
					}
					return ab[0] + " <> " + ab[1];
			}
			return null;
		} else {
			ab = getEqualGuard(s);
			if (ab == null) {
				return null;
			}
			return ab[0] + " = " + ab[1];
		}
	}
	
	// Input string must be of the form "a==b" or "b"
	// With a and b ids.
	// Returns a and b
	// Otherwise, returns null;
	public String[] getEqualGuard(String _guard) {
		TraceManager.addDev(" -> Analyzing equal guard: " + _guard);
		int index = _guard.indexOf("==");
		if (index == -1) {
			if (AvatarAttribute.isAValidAttributeName(_guard.trim())) {
				String[] ab = new String[2];
				ab[0] = _guard;
				ab[1] = "true";
				return ab;
			}
		}
		
		String a = _guard.substring(0, index).trim();
		String b = _guard.substring(index+2, _guard.length()).trim();
		
		if (AvatarAttribute.isAValidAttributeName(a) && AvatarAttribute.isAValidAttributeName(b)) {
			String[] ab = new String[2];
			ab[0] = a;
			ab[1] = b;
			return ab;
		}
		
		return null;
	}
	
	public void terminateProcess(ProVerifProcess _p) {
		if ((_p == null) || (_p.processLines == null)) {
			return;
		}
		
		if (_p.processLines.size() == 0) {
			return;
		}
		
		String s = _p.processLines.get(_p.processLines.size() - 1);
		s = s .trim();
		s = s.substring(0, s.length()-1) + ".";
		_p.processLines.remove(_p.processLines.size() - 1);
		_p.processLines.add(s);
	}
	
	public void terminate0Process(ProVerifProcess _p) {
		if ((_p == null) || (_p.processLines == null)) {
			return;
		}
		
		_p.processLines.add("0.");
	}
	
	
	
	
	public void addLine(ProVerifProcess _p, String _line) {
		_p.processLines.add(_line + ";\n");
	}
	
	public void addLineNoEnd(ProVerifProcess _p, String _line) {
		_p.processLines.add(_line + "\n");
	}
	
}
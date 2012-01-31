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
	
	private static int GENERAL_ID = 0;  

	private final static String UNKNOWN = "UNKNOWN";

	
	private final static String BOOLEAN_DATA_HEADER = "(* Boolean return types *)\ndata true/0.\ndata false/0.\n";
	private final static String FUNC_DATA_HEADER = "(* Functions data *)\ndata " + UNKNOWN + "/0.\n";
	
	private final static String PK_HEADER = "(* Public key cryptography *)\nfun pk/1.\nfun aencryptPK/2.\nreduc adecryptPK(aencryptPK(x,pk(y)),y) = x.\n";
	private final static String SIG_HEADER = "fun sign/2.\nfun verifySign/3.\nequation verifySign(m, sign(m,sk), pk(sk))=true.\n";
	private final static String CERT_HEADER = "(* Certificates  *)\nfun cert/2.\nfun verifyCert/2.\nequation verifyCert(cert(epk, sign(epk, sk)), pk(sk))=true.\nreduc getpk(cert(epk, sign(epk,sk))) = epk.\n";

	
	private final static String SK_HEADER = "(* Symmetric key cryptography *)\nfun sencrypt/2.\nreduc sdecrypt(sencrypt(x,k),k) = x.\n";
	private final static String MAC_HEADER = "(* MAC *)\nfun MAC/2.\nreduc verifyMAC(m, k, MAC(m, k)) = true.\n";
	private final static String HASH_HEADER = "(* HASH *)\nfun hash/1.\n";
	private final static String CONCAT_HEADER = "(* CONCAT *)\nfun concat/5.\nreduc get1(concat(m1, m2, m3, m4, m5))= m1.\nreduc get2(concat(m1, m2, m3, m4, m5))= m2.\nreduc get3(concat(m1, m2, m3, m4, m5))= m3.\nreduc get4(concat(m1, m2, m3, m4, m5))= m4.\nreduc get5(concat(m1, m2, m3, m4, m5))= m5.\n";
	
	private ProVerifSpec spec;
	private AvatarSpecification avspec;
	private Hashtable<String, Integer> macs;
	
	protected Hashtable<String, String> declarations;
	
	private Vector warnings;
	
	private boolean advancedTranslation;
	

	public AVATAR2ProVerif(AvatarSpecification _avspec) {
		avspec = _avspec;
	}
	
	
	public void saveInFile(String path) throws FileException {
		FileUtils.saveFile(path + "pvspec", spec.makeSpec());
	}
	
	
	public Vector getWarnings() {
		return warnings;
	}
	

	
	public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize, boolean _stateReachability, boolean _advancedTranslation) {
		advancedTranslation = _advancedTranslation;
		GENERAL_ID = 0;
		
		macs = new Hashtable<String, Integer>();
		declarations = new Hashtable<String, String>();
		
		warnings = new Vector();
		spec = new ProVerifSpec();
		
		avspec.removeCompositeStates();
		avspec.removeTimers();
		
		makeHeader(_stateReachability);
		
		makeStartingProcess();
		
		makeBlocks();
		
		
		//TraceManager.addDev("->   Spec:" + avspec.toString());
		
		
		/*if (_optimize) {
			spec.optimize();
		}*/
		
		
		return spec;
	}
	
	private String makeAttrName(String _block, String _attribute) {
		return _block + "__" + _attribute;
	}
	
	
	private String makeActionFromBlockParam(String _block, String _param) {
		String tmp = makeAttrName(_block, _param);
		String tmpH = declarations.get(tmp);
		if (tmpH == null) {
			declarations.put(tmp, tmp); 
			tmp = "new " + tmp + ";\n";
			return tmp;
		}
		
		return "";
	}
	
	private void addDeclarationsFromList(int startIndex, String[] list, String result) {
		String tmp, blockName, paramName;
		String tmp1;
		int index;
		
		TraceManager.addDev("Add declaration list length=" + list.length);
		
		for(int i=startIndex; i<list.length; i++) {
			
			tmp = list[i];
			TraceManager.addDev("tmp=" + tmp);
			index = tmp.indexOf('.');
			if (index != -1) {
				blockName = tmp.substring(0, index).trim();
				paramName = tmp.substring(index+1, tmp.length());
				tmp1 = makeAttrName(blockName, paramName);
				if (tmp1 != null) {
					declarations.put(tmp1, result);
					TraceManager.addDev("Adding declaration: " + tmp1 + " result=" + result);
				}
			}
		}
		
	}
	
	public void makeHeader(boolean _stateReachability) {
		spec.addToGlobalSpecification(BOOLEAN_DATA_HEADER + "\n");
		spec.addToGlobalSpecification(FUNC_DATA_HEADER + "\n");
		
		spec.addToGlobalSpecification(PK_HEADER + "\n");
		spec.addToGlobalSpecification(SIG_HEADER + "\n");
		spec.addToGlobalSpecification(CERT_HEADER + "\n");
		spec.addToGlobalSpecification(SK_HEADER + "\n");
		spec.addToGlobalSpecification(MAC_HEADER + "\n");
		spec.addToGlobalSpecification(HASH_HEADER + "\n");
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
		
		
		spec.addToGlobalSpecification("\n(* Data *)\n");
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		String[] list;
		String pragma;
		
		
		/* Data */
		for(AvatarBlock block: blocks) {
			for(AvatarAttribute attribute: block.getAttributes()) {
				pragma = hasConstantPragmaStartingWithAttribute(block.getName(), attribute.getName());
				if (pragma != null) {
					spec.addToGlobalSpecification("data " + makeAttrName(block.getName(), attribute.getName()) + "/0.\n");
					declarations.put(makeAttrName(block.getName(), attribute.getName()), makeAttrName(block.getName(), attribute.getName()));
					list = getListOfBlockParams(pragma);
					addDeclarationsFromList(1, list, makeAttrName(block.getName(), attribute.getName()));
				}
			}
		}
		
		
		spec.addToGlobalSpecification("\n(* Secrecy Assumptions *)\n");
		/* Secrecy Assumptions */
		int index;
		String tmp, blockName, paramName, tmp1;
		for(String pr: avspec.getPragmas()) {
			if (isSecrecyAssumptionPragma(pr)) {
				list = getListOfBlockParams(pr);
				for(int i=0; i<list.length; i++) {
					tmp = list[i];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						tmp1 = makeAttrName(blockName, paramName);
						if (tmp1 != null) {
							spec.addToGlobalSpecification("not " + tmp1 +".\n");
						}
					}
				}
			}
		}
		
		/* Queries */
		/* Parse all attributes starting with "secret" and declare them as non accesible to attacker" */
		spec.addToGlobalSpecification("\n(* Queries *)\n");
		for(AvatarBlock block: blocks) {
			for(AvatarAttribute attribute: block.getAttributes()) {
				// Attribute is preinitialized if it is in a secret pragma
				//TraceManager.addDev("Testing secret of " + block.getName() + "." + attribute.getName() + " ?");
				if (hasSecretPragmaWithAttribute(block.getName(), attribute.getName())) {
					//TraceManager.addDev("Secret!");
					spec.addToGlobalSpecification("private free " + makeAttrName(block.getName(), attribute.getName()) + ".\n");
					declarations.put(makeAttrName(block.getName(), attribute.getName()), makeAttrName(block.getName(), attribute.getName()));
					spec.addToGlobalSpecification("query attacker:" + makeAttrName(block.getName(), attribute.getName()) + ".\n\n");
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
				
		
		/* Autenticity */
		makeAuthenticityPragmas();
	}
	
	public void makeAuthenticityPragmas() {
		spec.addToGlobalSpecification("\n(* Authenticity *)\n");
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		String name1;
		String name0;
		String p0, p1;
		int cpt = -1;
		int index;
		
		for(String pragma: pragmas) {
			cpt ++;
			if (isAuthenticityPragma(pragma)) {
				tmp = pragma.substring(13, pragma.length()).trim();
				//tmp = Conversion.replaceAllChar(tmp, '.', "__");
				
				//TraceManager.addDev("Testing pragma: " + tmp);
				
				if (tmp.length() != 0) {
					tmps = tmp.split(" ");
					if (tmps.length > 1) {
						TraceManager.addDev("0");
						index = tmps[0].indexOf(".");
						name0 = tmps[0].substring(index+1, tmps[0].length());
						index = name0.indexOf(".");
						name0 = name0.substring(index+1, name0.length());
						
						TraceManager.addDev("1");
						
						index = tmps[1].indexOf(".");
						name1 = tmps[1].substring(index+1, tmps[1].length());
						index = name1.indexOf(".");
						name1 = name1.substring(index+1, name1.length());
						
						TraceManager.addDev("2");
						p1 = Conversion.replaceAllChar(tmps[1], '.', "__");
						TraceManager.addDev("3");
						p0 = Conversion.replaceAllChar(tmps[0], '.', "__");
						TraceManager.addDev("name0" + tmps[0]);
						TraceManager.addDev("name1" + tmps[1]);
						
						try {
							spec.addToGlobalSpecification("query evinj:authenticity__" + p1 + "__" + cpt + "(" + name1 + ") ==> evinj:authenticity__" + p0 + "__" + cpt + "(" + name0 + ").\n");
						} catch (Exception e) {
							TraceManager.addDev("\n\n*** Error on pragma:" + pragma + ": " + e.getMessage()); 
						}
					}
				}
			}
		}
		
	}
	
	public boolean hasAuthenticityPragma(boolean isOut, String _blockName, String attributeName) {
		TraceManager.addDev("************* Searching for authenticity pragma for " +  _blockName + "." + attributeName + " " + isOut);
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		String name;
		
		for(String pragma: pragmas) {
			if (isAuthenticityPragma(pragma)) {
				tmp = pragma.substring(13, pragma.length()).trim();
				
				TraceManager.addDev("Testing prama: " + tmp);
				
				if (tmp.length() == 0) {
					return false;
				}
				
				tmps = tmp.split(" ");
				
				if (tmps.length >1) {
					if (isOut) {
						tmp = tmps[0];
					} else {
						tmp = tmps[1];
					}
					//TraceManager.addDev("Testing with: " + tmp);
					if (tmp.length() > 0) {
						index = tmp.indexOf('.');
						if (index != -1) {
							try {
								if (tmp.substring(0, index).compareTo(_blockName) == 0) {
									
									TraceManager.addDev("Testing with: " + _blockName + "." + attributeName);
									name = attributeName;
									/*index = name.indexOf("__");
									if (index != -1) {
										name = name.substring(0, index);
									}*/
									if (tmp.substring(index+1, tmp.length()).compareTo(name) == 0) {
										TraceManager.addDev("Found authenticity");
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
		
		TraceManager.addDev("Authenticity failed");
		
		return false;
	}
	
	public LinkedList<String> getAuthenticityPragmas(String _blockName, String _stateName) {
		TraceManager.addDev("************* Searching for authenticity pragma for " +  _blockName + "." + _stateName);
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		String name;
		int cpt = -1;
		LinkedList<String> ret = new LinkedList<String>();
		
		for(String pragma: pragmas) {
			cpt ++;
			if (isAuthenticityPragma(pragma)) {
				tmp = pragma.substring(13, pragma.length()).trim();
				
				TraceManager.addDev("Testing prama: " + tmp);
				
				if (tmp.length() == 0) {
					return ret;
				}
				
				tmps = tmp.split(" ");
				
				if (tmps.length >1) {
					for(int i=0; i<2; i++) {
					if (i == 0) {
						tmp = tmps[0];
					} else {
						tmp = tmps[1];
					}
					//TraceManager.addDev("Testing with: " + tmp);
					if (tmp.length() > 0) {
						index = tmp.indexOf('.');
						if (index != -1) {
							try {
								TraceManager.addDev("Testing with: " + _blockName + "." + _stateName);
								if (tmp.substring(0, index).compareTo(_blockName) == 0) {
									tmp = tmp.substring(index+1, tmp.length());
									index = tmp.indexOf('.');
									if (index != -1) {
										if (tmp.substring(0, index).compareTo(_stateName) == 0) {
											ret.add(new String("authenticity__" + _blockName + "__" + _stateName + "__" + tmp.substring(index+1, tmp.length()) + "__" + cpt + "(" + tmp.substring(index+1, tmp.length()) + ")"));
											TraceManager.addDev("Pragma added:" + ret.get(ret.size()-1));
										}
									}
									
									//name = attributeName;
									/*index = name.indexOf("__");
									if (index != -1) {
										name = name.substring(0, index);
									}*/
									/*if (tmp.substring(index+1, tmp.length()).compareTo(name) == 0) {
										TraceManager.addDev("Found authenticity");
										return true;
										
									}*/
								}
							} catch (Exception e) {
								TraceManager.addDev("Error on testing pragma");
							}
						}
					}
					}
				}
				
			}
		}
		
		TraceManager.addDev("Authenticity done found:" + ret.size());
		
		return ret;
	}
	
	public boolean isPublicPrivateKeyPragma(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		
		for(String pragma: pragmas) {
			if (isPrivatePublicKeyPragma(pragma)) {
				tmp = pragma.substring(18, pragma.length()).trim();
				
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
						} else {
							if (tmp.compareTo(attributeName) == 0) {
								return true;
							}
						}
					}
				}
				
			}
		}
		
		return false;
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
	
	public String hasConstantPragmaStartingWithAttribute(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		
		for(String pragma: pragmas) {
			if (isConstantPragma(pragma)) {
				tmp = pragma.substring(8, pragma.length()).trim();
				
				TraceManager.addDev("Testing CONSTANT pragma: " + tmp);
				
				if (tmp.length() == 0) {
					return null;
				}
				
				tmps = tmp.split(" ");
				tmp = tmps[0];
				
				index = tmp.indexOf('.');
				if (index != -1) {
					try {
						if (tmp.substring(0, index).compareTo(_blockName) == 0) {
							if (tmp.substring(index+1, tmp.length()).compareTo(attributeName) == 0) {
								return pragma;
							}
						}
					} catch (Exception e) {
						TraceManager.addDev("Error on testing pragma");
					}
				}
			}
		}
		

		return null;
	}
	
	public boolean hasSecretPragmaWithAttribute(String attributeName) {
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
								
									if (tmp.substring(index+1, tmp.length()).compareTo(attributeName) == 0) {
										return true;
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
	
	public boolean hasPrivatePublicKeysPragmaWithAttribute(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		boolean foundBlock = false;
		
		for(String pragma: pragmas) {
			if (isPrivatePublicKeyPragma(pragma)) {
				tmp = pragma.substring(17, pragma.length()).trim();
				
				//TraceManager.addDev("Testing prama: " + tmp);
				
				if (tmp.length() == 0) {
					return false;
				}
				
				tmps = tmp.split(" ");
				for(int i=0; i<tmps.length; i++) {
					tmp = tmps[i];
					if (i == 0) {
						if (tmp.compareTo(_blockName) == 0) {
							foundBlock = true;
						}
					} else {
						if ((tmp.compareTo(attributeName) == 0) && (foundBlock)) {
							return true;
						}
					}
				}
				
			}
		}
		
		return false;
	}
	
	public boolean hasInitialSystemKnowledgePragmaWithAttribute(String _blockName, String attributeName) {
		LinkedList<String> pragmas = avspec.getPragmas();
		String tmp;
		String tmps [];
		int index;
		
		for(String pragma: pragmas) {
			if (isInitialSystemKnowledgePragma(pragma)) {
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
	
	public boolean isAuthenticityPragma(String _pragma) {
		return _pragma.startsWith("Authenticity ");
	}
	
	public boolean isInitialSystemKnowledgePragma(String _pragma) {
		return _pragma.startsWith("InitialSystemKnowledge ");
	}
	
	public boolean isInitialSessionKnowledgePragma(String _pragma) {
		return _pragma.startsWith("InitialSessionKnowledge ");
	}
	
	public boolean isConstantPragma(String _pragma) {
		return _pragma.startsWith("Constant ");
	}
	
	public boolean isSecrecyAssumptionPragma(String _pragma) {
		return _pragma.startsWith("SecrecyAssumption ");
	}
	
	public boolean isPrivatePublicKeyPragma(String _pragma) {
		return _pragma.startsWith("PrivatePublicKeys ");
	}
	
	public String[] getListOfBlockParams(String _pragma) {
		String s = _pragma;
		
		if (isSecretPragma(s)) {
			s = s.substring(7, s.length()).trim();
		} else if (isInitialSystemKnowledgePragma(s)) {
			s = s.substring(23, s.length()).trim();
		} else if (isInitialSessionKnowledgePragma(s)) {
			s = s.substring(24, s.length()).trim();
		} else if (isConstantPragma(s)) {
			s = s.substring(8, s.length()).trim();
		} else if (isSecrecyAssumptionPragma(s)) {
			s = s.substring(17, s.length()).trim();
		} else {
			return null;
		}
		
		return s.split(" ");
	}
	
	public void makeStartingProcess() {
		String action = "";
		String tmp, tmp1, tmp2, attributeName;
		int index;
		boolean found;
		
		ProVerifProcess p = new ProVerifProcess();
		p.processName = "starting__";
		LinkedList<AvatarBlock> blocks = avspec.getListOfBlocks();
		
		//LinkedList<String> createdVariables = new LinkedList<String>();
		String[] list;
		String blockName, paramName;
		
		for(String pragma: avspec.getPragmas()) {
			TraceManager.addDev("Working on pragma: " + pragma);
			if (isInitialSystemKnowledgePragma(pragma)) {
				// Identify each blockName / paramName
				list = getListOfBlockParams(pragma);
				
				
				// Declare only the first one of the list
				if (list.length > 0) {
					tmp = list[0];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						
						action += makeActionFromBlockParam(blockName, paramName);
						addDeclarationsFromList(1, list, makeAttrName(blockName, paramName));
					}
				}
				/*for(int i=0; i<list.length; i++) {
					tmp = list[i];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						
						// Verify whether they are secret or not
						if ((!hasSecretPragmaWithAttribute(paramName)) &&  (!hasPrivatePublicKeysPragmaWithAttribute(blockName, paramName))){
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
				}*/
			} else if (isPrivatePublicKeyPragma(pragma)) {
				TraceManager.addDev("Pragma Public: " + pragma);
				String privK, pubK;
				index = pragma.indexOf(" ");
				if (index != -1) {
					tmp = pragma.substring(index+1, pragma.length()).trim();
					index = tmp.indexOf(" ");
					if (index != -1) {
						blockName = tmp.substring(0, index);
						tmp2 = tmp.substring(index+1, tmp.length()).trim();
						index = tmp2.indexOf(" ");
						if (index != -1) {
							privK = tmp2.substring(0, index).trim();
							pubK = tmp2.substring(index+1, tmp2.length()).trim();
							
							action += makeActionFromBlockParam(blockName, privK);
							
							action += "let " + makeAttrName(blockName, pubK) + " = pk(" + makeAttrName(blockName, privK) + ") in \n";
							action += "out(ch, " + makeAttrName(blockName, pubK) + ");\n";
							TraceManager.addDev("Putting :" + makeAttrName(blockName, pubK + " -> " + makeAttrName(blockName, pubK)));
							declarations.put(makeAttrName(blockName, pubK), makeAttrName(blockName, pubK));
						}
					}
				}
			} else if (isSecrecyAssumptionPragma(pragma)) {
				// Identify each blockName / paramName
				list = getListOfBlockParams(pragma);
				
				
				// Declare only the first one of the list
				if (list.length > 0) {
					tmp = list[0];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						
						action += makeActionFromBlockParam(blockName, paramName);
					}
				}
			}
		}
		
		action += "(!\n(";
		
		// Must add Session Knowledge
		for(String pragma: avspec.getPragmas()) {
			TraceManager.addDev("Working on pragma: " + pragma);
			if (isInitialSessionKnowledgePragma(pragma)) {
				list = getListOfBlockParams(pragma);
				
				
				// Declare only the first one of the list
				if (list.length > 0) {
					tmp = list[0];
					index = tmp.indexOf('.');
					if (index != -1) {
						blockName = tmp.substring(0, index).trim();
						paramName = tmp.substring(index+1, tmp.length());
						
						action += makeActionFromBlockParam(blockName, paramName);
						addDeclarationsFromList(1, list, makeAttrName(blockName, paramName));
					}
				}
			}
		}
		index = 0;
		for(AvatarBlock block: blocks) {
			if (index != 0) {
				action += " | ";
			} 
			index ++;
			action += "(" + block.getName() + "__0)";
		}
		action += "))";
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
		String dec;
		
		LinkedList<ProVerifProcess> tmpprocesses = new LinkedList<ProVerifProcess>();
		LinkedList<AvatarState> states = new LinkedList<AvatarState>();
		
		// First process : variable declaration
		ProVerifProcess p = new ProVerifProcess(ab.getName() + "__0");
		spec.addProcess(p);
		
		for(AvatarAttribute aa: ab.getAttributes()) {
			//TraceManager.addDev("Testing: " + ab.getName() + "." + aa.getName());
			/*if ((!hasInitialSystemKnowledgePragmaWithAttribute(ab.getName(), aa.getName())) && (!(hasSecretPragmaWithAttribute(ab.getName(), aa.getName())))) {
				if (!isPublicPrivateKeyPragma(ab.getName(), aa.getName())) {
						TraceManager.addDev("  Adding: " + aa.getName());
						addLine(p, "new " + aa.getName());
					}
			}*/
			TraceManager.addDev("Getting:" + makeAttrName(ab.getName(), aa.getName()));
			dec = declarations.get(makeAttrName(ab.getName(), aa.getName()));
			if (dec == null) {
				addLine(p, "new " + aa.getName());
			} else {
				if (dec.compareTo(aa.getName()) != 0) {
					addLineNoEnd(p, "let " + aa.getName() + " = " + dec + " in");
				}
			}
			
		}
		
		AvatarStateMachine asm = ab.getStateMachine();
		AvatarStartState ass = asm.getStartState();
		
		macs.clear();
		
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
		LinkedList<String> pos;
		
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
			
			boolean isPrivate = false;
			AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
			if (ar != null) {
				isPrivate = ar.isPrivate();
			}
			
			if (as.isOut()) {
				if (aaos.getNbOfValues() == 0) {
					addLine(_p, "new data__");
				}
				tmp ="out";
				
			} else {
				tmp = "in";
			}
			
			if (!isPrivate) {
				tmp+="(ch, ";
			} else {
				tmp +="(chprivate, ";
			}
			
			if (aaos.getNbOfValues() == 0) {
				tmp += "data__";
			} else {
				for(i=0; i<aaos.getNbOfValues(); i++) {
					if (i>0) {
						tmp += ", ";
					}
					// Work on authenticity
					/*if (hasAuthenticityPragma(as.isOut(), _block.getName(), aaos.getValue(i))) {
						if (as.isOut()) {
							addLine(_p, "event authenticity__" + _block.getName() + "__" + aaos.getValue(i) + "__out()");
						}
					}*/
					tmp += aaos.getValue(i);
				}
			}
			tmp += ")";
			addLine(_p, tmp);
			/*for(i=0; i<aaos.getNbOfValues(); i++) {
				if (hasAuthenticityPragma(as.isOut(), _block.getName(), aaos.getValue(i))) {
					if (!as.isOut()) {
						addLine(_p, "event authenticity__" + _block.getName() + "__" + aaos.getValue(i) + "__in()");
					}
				}
			}*/
			makeBlockProcesses(_block, _asm, _asme.getNext(0), _p, _processes, _states, null);
			
			
		// State
		} else if (_asme instanceof AvatarState){
			i = _states.indexOf(_asme);
			if (i != -1) {
				// State has already been met
				// Must branch to the corresponding process
				p = _processes.get(i);
				addLineNoEnd(_p, p.processName + ".");
				return;
				
			} else {
				// New state
				// We create a new process for each state
				p = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
				spec.addProcess(p);
				_processes.add(p);
				_states.add((AvatarState)_asme);
				addLine(p, "event enteringState__" + _block.getName() + "__" + _asme.getName() + "()");
				
				// Adding an event if authenticity is concerned with that state
				pos = getAuthenticityPragmas( _block.getName(), _asme.getName());
				for(String sp: pos) {
					addLine(p, "event " + sp);
				}
				
				
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
					term = "";
					if (index0 != -1) {
						term = tmp.substring(0, index0).trim();
					}
					if ((index0 == -1) || (index1 == -1) || (index0 > index1) || (term.length() == 0)) {
						name = tmp.substring(0, index1).trim();
						index0 = tmp.indexOf(')');
						
						// get functions
						if ((name.compareTo("get2") == 0) || (name.compareTo("get3") == 0)  || (name.compareTo("get4") == 0)) {
							int index2 = tmp.indexOf(',');
								if ((index2 != -1) && (index2 > index1)) {
								addLineNoEnd(p, "let (" + tmp.substring(index2+1, index0+1).trim() + " = " + tmp.substring(index1+1, index2).trim() + " in");
							} else {
									addLineNoEnd(p, "let " + tmp + " in");
							}
						} else {
							addLineNoEnd(p, "let " + tmp + " in");
						}
						
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
						
						index0 = tmp.indexOf(')');
						if ((found) && (name.compareTo("verifyMAC") == 0) && (advancedTranslation)){
							// Verify MAC!
							if (index0 == -1) {
								index0 = tmp.length();
							}
							value = tmp.substring(index1+1, index0).trim();
							String[] values = value.split(",");
							if (values.length < 3) {
								addLineNoEnd(p, "let " + tmp + " in");
							} else {
								addLineNoEnd(p, "let MAC__tmp0__" + GENERAL_ID + " = MAC(" + values[0].trim() + " , " + values[1].trim() + ") in");
								addLineNoEnd(p, "let MAC__tmp1__" + GENERAL_ID + " = " + values[2].trim() + " in");
								
								macs.remove(term);
								macs.put(term, new Integer(GENERAL_ID));
								GENERAL_ID++;
								//addLine(p, "new choice__mac");
								//addLine(p, "out(chprivate, choice__mac)");
								
								// We don't need anymore the two parralel process
								/*ptmp1 = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
								spec.addProcess(ptmp1);
								_processes.add(ptmp1);
								_states.add(null);
								
								ptmp2 = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
								spec.addProcess(ptmp2);
								_processes.add(ptmp2);
								_states.add(null);
								
								addLineNoEnd(p, "((" + ptmp1.processName + ")|(" + ptmp2.processName + "))."); */
								
								/*ptmp = new ProVerifProcess(_block.getName() + "__" + (_processes.size() + 1));
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
								addLineNoEnd(ptmp2, ptmp.processName + ".");*/
								
								/*addLineNoEnd(p, "let MAC__tmp = MAC(" + values[0].trim() + " , " + values[1].trim() + ") in");
								addLineNoEnd(p, "if MAC__tmp =  " + values[2].trim() + " then");
								addLineNoEnd(p, "let " + term + "= true in");
								addLineNoEnd(p, ptmp.processName);
								addLineNoEnd(p, "else");
								addLineNoEnd(p, "let " + term + "= false in");
								addLineNoEnd(p, ptmp.processName + ".");
								p = ptmp;*/
							}
						} else if ((name.compareTo("concat2") == 0) || (name.compareTo("concat3") == 0) || (name.compareTo("concat4") == 0)){
							addLineNoEnd(p, "let " + term + " = " + tmp.substring(index1, tmp.length()) + " in");
						} else if ((name.compareTo("get2") == 0) || (name.compareTo("get3") == 0)  || (name.compareTo("get4") == 0)) {
							int index2 = tmp.indexOf(',');
							if ((index2 != -1) && (index2 > index1)) {
							addLineNoEnd(p, "let (" + tmp.substring(index2+1, index0) + " = " + tmp.substring(index1, index2) + " in");
						} else {
								addLineNoEnd(p, "let " + tmp + " in");
							}
						} else	{
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
		Integer myInt;
		TraceManager.addDev(" -> Analyzing equal guard: " + _guard);
		int index = _guard.indexOf("==");
		if (index == -1) {
			if (AvatarAttribute.isAValidAttributeName(_guard.trim())) {
				myInt = macs.get(_guard.trim());
				String[] ab = new String[2];
				if (myInt != null) {
					ab[0] = "MAC__tmp0__" + myInt.intValue();
					ab[1] = "MAC__tmp1__" + myInt.intValue();
				} else {
					ab[0] = _guard;
					ab[1] = "true";
				}
				return ab;
			} else {
				return null;
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
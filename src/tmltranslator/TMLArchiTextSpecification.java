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
* Class TMLArchiTextSpecification
* Import and export of TML architecture textual specifications
* Creation: 21/09/2007
* @version 1.0 21/09/2007
* @author Ludovic APVRILLE
* @see
*/


package tmltranslator;

import java.util.*;
import java.io.*;
import myutil.*;

public class TMLArchiTextSpecification {
	public final static String CR = "\n";
	public final static String SP = " ";
	public final static String CR2 = "\n\n";
	public final static String SC = ";";
    
    private String spec;
	private String title;
	
	private TMLArchitecture tmla;
	private ArrayList<TMLTXTError> errors;
	private ArrayList<TMLTXTError> warnings;
	
	private String keywords[] = {"NODE", "CPU", "SET", "BUS", "LINK", "BRIDGE", "MEMORY"};
	private String nodetypes[] = {"CPU", "BUS", "LINK", "BRIDGE", "MEMORY", "HWA"};
	private String cpuparameters[] = {"byteDataSize", "pipelineSize", "goIdleTime", "taskSwitchingTime", "branchingPredictionPenalty", "schedulingPolicy", "execiTime"};
	private String linkparameters[] = {"bus", "node", "priority"};
	private String hwaparameters[] = {"byteDataSize", "execiTime"};
	private String busparameters[] = {"byteDataSize", "pipelineSize", "arbitration"};
	private String bridgeparameters[] = {"bufferByteSize"};
	private String memoryparameters[] = {"byteDataSize"};
	
	
	
	/*private String keywords[] = {"BOOL", "INT", "NAT", "CHANNEL", "EVENT", "REQUEST", "BRBW", "NBRNBW", 
		"BRNBW", "INF", "NIB", "NINB", "TASK", "ENDTASK", "IF", "ELSE", "ELSEIF", "ENDIF", "FOR", "ENDFOR",
	"SELECTEVT", "CASE", "ENDSELECTEVT", "ENDCASE", "WRITE", "READ", "WAIT", "NOTIFY", "NOTIFIED", "RAND", "CASERAND", "ENDRAND", "ENDCASERAND", "EXECI"};
	
	private String channeltypes[] = {"BRBW", "NBRNBW", "BRNBW"};
	private String eventtypes[] = {"INF", "NIB", "NINB"};
	
	private String beginArray[] = {"TASK", "FOR", "IF", "ELSE", "ELSEIF", "SELECTEVT", "CASE", "RAND", "CASERAND"};
	private String endArray[] = {"ENDTASK", "ENDFOR", "ENDIF", "ELSE", "ELSEIF", "ENDSELECTEVT", "ENDCASE", "ENDRAND", "ENDCASERAND"};
	*/	
	
	public TMLArchiTextSpecification(String _title) {
		title = _title;
    }
	
	public void saveFile(String path, String filename) throws FileException {
		System.out.println("Saving architecture spec file in " + path + filename);
        FileUtils.saveFile(path + filename, spec);
    }
	
	public TMLArchitecture getTMLArchitecture() {
		return tmla;
	}
	
	public String getSpec() {
		return spec;
	}
	
	public ArrayList<TMLTXTError> getErrors() {
		return errors;
	}  
	
	public ArrayList<TMLTXTError> getWarnings() {
		return warnings;
	}
	
	/*public void indent() {
		indent(4);
	}
	
	public void indent(int _nbDec) {
		int dec = 0;
        int indexEnd;
        String output = "";
        String tmp;
        int nbOpen = 0;
        int nbClose = 0;
		
		while ( (indexEnd = spec.indexOf('\n')) > -1) {
			tmp = spec.substring(0, indexEnd+1);
			try {
                spec = spec.substring(indexEnd+1, spec.length());
            } catch (Exception e) {
                spec = "";
            }
			nbOpen = nbOfOpen(tmp);
            nbClose = nbOfClose(tmp);
			dec -= nbClose * _nbDec;
            tmp = Conversion.addHead(tmp.trim(), ' ', dec);
            dec += nbOpen * _nbDec;
			//System.out.println("dec=" + dec);
            output += tmp + "\n";
		}
		spec = output;
	}*/
	
	/*private int nbOfOpen(String tmp) {
		return nbOf(tmp, beginArray);
	}
	
	private int nbOfClose(String tmp) {
		return nbOf(tmp, endArray);
	}*/
	
	/*private int nbOf(String _tmp, String[] array) {
		String tmp;
		int size;
		
		for(int i=0; i<array.length; i++) {
			if (_tmp.startsWith(array[i])) {
				tmp = _tmp.substring(array[i].length(), _tmp.length());
				//System.out.println("tmp=" + tmp + " _tmp" + _tmp + " array=" + array[i]);
				if ((tmp.length() == 0) || (tmp.charAt(0) == ' ') || (tmp.charAt(0) == '(') || (tmp.charAt(0) == '\n')) {
						//System.out.println("Returning 1!!");
						return 1;
				}
			}
		}
		return 0;
	}*/
	
	public String toString() {
		return spec;
	}
	
	public String toTextFormat(TMLArchitecture _tmla) {
		tmla = _tmla;
		spec = makeNodes(tmla);
		spec += makeLinks(tmla);
		return spec;
		//indent();
	}
	
	public String makeNodes(TMLArchitecture tmla) {
		String code = "";
		String name;
		String set;
		ArrayList<HwNode> hwnodes = tmla.getHwNodes();
		HwCPU cpu;
		HwA hwa;
		HwBus bus;
		HwBridge bridge;
		HwMemory memory;
		
		for(HwNode node: hwnodes) {
			
			// CPU
			if (node instanceof HwCPU) {
				cpu = (HwCPU)node;
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE CPU " +  name + CR;
				code += set + "byteDataSize " + cpu.byteDataSize + CR;  
				code += set + "pipelineSize " + cpu.pipelineSize + CR;
				code += set + "goIdleTime " + cpu.goIdleTime + CR;  
				code += set + "taskSwitchingTime " + cpu.taskSwitchingTime + CR;
				code += set + "branchingPredictionPenalty " + cpu.branchingPredictionPenalty + CR;  
				code += set + "schedulingPolicy " + cpu.schedulingPolicy + CR;
				code += set + "execiTime " + cpu.execiTime + CR;
			}
			
			//HWA
			if (node instanceof HwA) {
				hwa = (HwA)node;
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE HWA " +  name + CR;
				code += set + "byteDataSize " + hwa.byteDataSize + CR;  
				code += set + "execiTime " + hwa.execiTime + CR;
			}
			
			// BUS
			if (node instanceof HwBus) {
				bus = (HwBus)node;
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE BUS " +  name + CR;
				code += set + "byteDataSize " + bus.byteDataSize + CR;  
				code += set + "pipelineSize " + bus.pipelineSize + CR;
				code += set + "arbitration " + bus.arbitration + CR;  
			}
			
			
			// Bridge
			if (node instanceof HwBridge) {
				bridge = (HwBridge)node;
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE BRIDGE " +  name + CR;
				code += set + "bufferByteSize " + bridge.bufferByteSize + CR;  
			}
			
			// Memory
			if (node instanceof HwMemory) {
				memory = (HwMemory)node;
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE MEMORY " +  name + CR;
				code += set + "byteDataSize " + memory.byteDataSize + CR;  
			}
			
		}
		return code;
	}
	
	public String makeLinks(TMLArchitecture tmla) {
		String code = "";
		String name;
		String set;
		ArrayList<HwLink> hwlinks = tmla.getHwLinks();
		
		System.out.println("Making links");
		for(HwLink link: hwlinks) {
			System.out.println("Link");
			if (link instanceof HwLink) {
				if ((link.hwnode != null) && (link.bus != null)) {
					name = prepareString(link.getName());
					set = "SET " + name + " ";
					code += "NODE LINK " +  name + CR;
					code += set + "node " + prepareString(link.hwnode.getName()) + CR;  
					code += set + "bus " + prepareString(link.bus.getName()) + CR; 
					code += set + "priority " + link.getPriority() + CR;  
				}
			}
		}
		System.out.println("Links:done");
		
		return code;
	}
	
	
	
	
	// FROM Text file to TML ARCHITECTURE
	
	public boolean makeTMLArchitecture(String _spec) {
		spec = _spec;
		tmla = new TMLArchitecture();
		errors = new ArrayList<TMLTXTError>();  
		warnings = new ArrayList<TMLTXTError>();
		
		spec = Conversion.removeComments(spec);
		//System.out.println(spec);
		browseCode();
		
		return (errors.size() == 0);
	}
	
	public String printErrors() {
		String ret = "";
		for(TMLTXTError error: errors) {
			ret += "ERROR at line " + error.lineNb + ": " + error.message + CR;
			try {
				ret += "->" + spec.split("\n")[error.lineNb] + CR2;
			} catch (Exception e) {
				ret += "(Code line not accessible)" + CR;
			}
		}
		return ret;
	}
	
	public String printWarnings() {
		String ret = "";
		for(TMLTXTError error: warnings) {
			ret += "WARNING at line " + error.lineNb + CR;
			ret += error.message + CR; 
		}
		return ret;
	}
	
	public String printSummary() {
		String ret = "";
		if (errors.size() == 0) {
			ret += printWarnings();
			ret += "Compilation successful" + CR;
			ret += "No error, " + warnings.size() + " warning(s)" + CR;
		} else {
			ret += printErrors() + CR + printWarnings() + CR;
			ret += "Compilation failed" + CR;
			ret += errors.size() + " error(s), "+ warnings.size() + " warning(s)" + CR;	
		}
		
		return ret;
	}
	
	public void browseCode() {
		// Browse lines of code one after the other
		// Build accordinlgy the TMLModeling and updates errors and warnings
		// In case of fatal error, immedialty quit code bowsing
		
		StringReader sr = new StringReader(spec);
        BufferedReader br = new BufferedReader(sr);
        String s;
		String s1;
		String [] split;
		int lineNb = 0;
		
		String instruction;
		
        try {
            while((s = br.readLine()) != null) {
				if (s != null) {
					s = s.trim();
					//System.out.println("s=" + s);
					s = removeUndesiredWhiteSpaces(s, lineNb);
					s1 = Conversion.replaceAllString(s, "\t", " ");
					s1 = Conversion.replaceRecursiveAllString(s1, "  ", " ");
					//System.out.println("s1=" + s1);
					if (s1 != null) {
						split = s1.split("\\s");
						if (split.length > 0) {
							//System.out.println("analyse");
							analyseInstruction(s, lineNb, split);
							//System.out.println("end analyse");
						}
					}
					
					lineNb++;
				}
            }
        } catch (Exception e) {
            System.out.println("Exception when reading specification: " + e.getMessage());
			addError(0, lineNb, 0, "Exception when reading specification");
        }
	}
	
	public void addError(int _type, int _lineNb, int _charNb, String _msg) {
		TMLTXTError error = new TMLTXTError(_type);
		error.lineNb = _lineNb;
		error.charNb = _charNb;
		error.message = _msg;
		errors.add(error);
	}
	
	public int analyseInstruction(String _line, int _lineNb, String[] _split) {
		String error;
		String params;
		String id;
		
		// NODE
		if(isInstruction("NODE", _split[0])) {
			
			if (_split.length != 3) {
				error = "A node must be declared with 3 parameters, and not " + (_split.length - 1) ;
				addError(0, _lineNb, 0, error);
				return -1;
			}
			
			if (!checkParameter("NODE", _split, 1, 2, _lineNb)) {
				return -1;
			}
			
			if (!checkParameter("NODE", _split, 2, 0, _lineNb)) {
				return -1;
			}
			
			if (_split[1].equals("CPU")) {
				HwCPU cpu = new HwCPU(_split[2]); 
				tmla.addHwNode(cpu);
			} else if (_split[1].equals("BUS")) {
				HwBus bus = new HwBus(_split[2]); 
				tmla.addHwNode(bus);
			} else if (_split[1].equals("MEMORY")) {
				HwMemory memory = new HwMemory(_split[2]); 
				tmla.addHwNode(memory);
			} else if (_split[1].equals("BRIDGE")) {
				HwBridge bridge = new HwBridge(_split[2]); 
				tmla.addHwNode(bridge);
			} else if (_split[1].equals("HWA")) {
				HwA hwa = new HwA(_split[2]); 
				tmla.addHwNode(hwa);
			} else if (_split[1].equals("LINK")) {
				HwLink link = new HwLink(_split[2]); 
				tmla.addHwLink(link);
			}
			
		} // NODE
		
		// SET
		if(isInstruction("SET", _split[0])) {
			
			if (_split.length != 4) {
				error = "A set instruction must be used with 3 parameters, and not " + (_split.length - 1) ;
				addError(0, _lineNb, 0, error);
				return -1;
			}
			
			if (!checkParameter("SET", _split, 1, 0, _lineNb)) {
				return -1;
			}
			
			HwNode node = tmla.getHwNodeByName(_split[1]);
			HwLink link;
			
			if (node == null) {
				link = tmla.getHwLinkByName(_split[1]);
				if (link == null) {
					error = "Unknown node: " + _split[1] ;
					addError(0, _lineNb, 0, error);
					return -1;
				} else {
					// Link node
					if (link instanceof HwLink) {
						if (!checkParameter("SET", _split, 2, 8, _lineNb)) {
							return -1;
						}
					
						if (!checkParameter("SET", _split, 3, 7, _lineNb)) {
							return -1;
						}
						
						if (_split[2].toUpperCase().equals("NODE")) {
							HwNode node0 = tmla.getHwNodeByName(_split[3]);
							if (node0 == null) {
								error = "Unknown node: " + _split[3] ;
								addError(0, _lineNb, 0, error);
								return -1;
							} else {
								link.hwnode = node0;
							}
						}
						
						if (_split[2].toUpperCase().equals("BUS")) {
							HwBus bus0 = tmla.getHwBusByName(_split[3]);
							if (bus0 == null) {
								error = "Unknown bus: " + _split[3] ;
								addError(0, _lineNb, 0, error);
								return -1;
							} else {
								link.bus = bus0;
							}
						}
						
						if (_split[2].toUpperCase().equals("PRIORITY")) {
							link.setPriority(Integer.decode(_split[3]).intValue());
						}
					}
				}
			} else {
				if (node instanceof HwCPU) {
					HwCPU cpu = (HwCPU)node;
					
					if (!checkParameter("SET", _split, 2, 3, _lineNb)) {
						return -1;
					}
					
					if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
						return -1;
					}
					
					if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
						cpu.byteDataSize = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("PIPELINESIZE")) {
						cpu.pipelineSize = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("GOIDLETIME")) {
						cpu.goIdleTime = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("TASKSWITCHINGTIME")) {
						cpu.taskSwitchingTime = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("SCHEDULINGPOLICY")) {
						cpu.schedulingPolicy = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("BRANCHINGPREDICTIONPENALTY")) {
						cpu.branchingPredictionPenalty = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("EXECITIME")) {
						cpu.execiTime = Integer.decode(_split[3]).intValue();
					}
				}
				
				if (node instanceof HwA) {
					HwA hwa = (HwA)node;
					
					if (!checkParameter("SET", _split, 2, 10, _lineNb)) {
						return -1;
					}
					
					if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
						return -1;
					}
					
					if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
						hwa.byteDataSize = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("EXECITIME")) {
						hwa.execiTime = Integer.decode(_split[3]).intValue();
					}
				}
				
				if (node instanceof HwBus) {
					HwBus bus = (HwBus)node;
					
					if (!checkParameter("SET", _split, 2, 9, _lineNb)) {
						return -1;
					}
					
					if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
						return -1;
					}
					
					if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
						bus.byteDataSize = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("PIPELINESIZE")) {
						bus.pipelineSize = Integer.decode(_split[3]).intValue();
					}
					
					if (_split[2].toUpperCase().equals("ARBITRATION")) {
						bus.arbitration = Integer.decode(_split[3]).intValue();
					}
				}
				
				if (node instanceof HwBridge) {
					HwBridge bridge = (HwBridge)node;
					
					if (!checkParameter("SET", _split, 2, 11, _lineNb)) {
						return -1;
					}
					
					if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
						return -1;
					}
					
					if (_split[2].toUpperCase().equals("BUFFERBYTESIZE")) {
						bridge.bufferByteSize = Integer.decode(_split[3]).intValue();
					}
				}
				
				if (node instanceof HwMemory) {
					HwMemory memory = (HwMemory)node;
					
					if (!checkParameter("SET", _split, 2, 12, _lineNb)) {
						return -1;
					}
					
					if (!checkParameter("SET", _split, 3, 1, _lineNb)) {
						return -1;
					}
					
					if (_split[2].toUpperCase().equals("BYTEDATASIZE")) {
						memory.byteDataSize = Integer.decode(_split[3]).intValue();
					}
				}
			}
			
		} // SET
		
		// Other command
		if((_split[0].length() > 0) && (!(isInstruction(_split[0])))) {
			error = "Syntax error: unrecognized instruction.";
			addError(0, _lineNb, 0, error);
			return -1;
			
		} // Other command
		
		return 0;
	}
	
	// Type 0: id
	// Type 1: numeral
	// Type 2: Node type
	// Type 3: CPU parameter  
	// Type 5: '='
	// Type 6: attribute value
	// Type 7: id or numeral
	// Type 8: LINK parameter  
	// Type 9: BUS parameter  
	// Type 10: HWA parameter  
	// Type 11: BRIDGE parameter 
	// Type 12: MEMORY parameter
	
	public boolean checkParameter(String _inst, String[] _split, int _parameter, int _type, int _lineNb) {
		boolean err = false;
		String error;
		
		if(_parameter < _split.length) {
			switch(_type) {
			case 0:
				if (!isAValidId(_split[_parameter])) {
					err = true;
				}
				break;
			case 1:
				if (!isANumeral(_split[_parameter])) {
					err = true;
				}
				break;
			case 2:
				if (!isIncluded(_split[_parameter], nodetypes)) {
					err = true;
				}
				break;	
			case 3:
				if (!isIncluded(_split[_parameter], cpuparameters)) {
					err = true;
				}
				break;	
			case 4:
				if (!isAValidId(getEvtId(_split[_parameter]))) {
					err = true;
					//System.out.println("Unvalid id");
				} else if (!TMLEvent.isAValidListOfParams(getParams(_split[_parameter]))) {
					//System.out.println("Unvalid param");
					err = true;
				}
				break;
			case 5:
				if (!(_split[_parameter].equals("="))) {
					System.out.println("Error of =");
					err = true;
				}
				break;
			case 6:
				if (_inst.equals("BOOL")) {
					String tmp = _split[_parameter].toUpperCase();
					if (!(tmp.equals("TRUE") || tmp.equals("FALSE"))) {
						err = true;
					}
				} else {
					if (!isANumeral(_split[_parameter])) {
						err = true;
					}
				}
				break;	 
			case 7:
				if (!isAValidId(_split[_parameter]) && !isANumeral(_split[_parameter])) {
					err = true;
				}
				break;
			case 8:
				if (!isIncluded(_split[_parameter], linkparameters)) {
					err = true;
				}
				break;	
			case 9:
				if (!isIncluded(_split[_parameter], busparameters)) {
					err = true;
				}
				break;	
			case 10:
				if (!isIncluded(_split[_parameter], hwaparameters)) {
					err = true;
				}
				break;	
			case 11:
				if (!isIncluded(_split[_parameter], bridgeparameters)) {
					err = true;
				}
				break;	
			case 12:
				if (!isIncluded(_split[_parameter], memoryparameters)) {
					err = true;
				}
				break;	
			}
		} else {
			err = true;
		}
		if (err) {
			error = "Unvalid parameter #" + _parameter + " ->" + _split[_parameter] + "<- in " + _inst + " instruction";
			addError(0, _lineNb, 0, error);
			return false;
		}
		return true;
	}
	
	public boolean isInstruction(String instcode, String inst) {
		return (inst.toUpperCase().compareTo(instcode) == 0);
	}
	
	public boolean isInstruction(String instcode) {
		return (!checkKeywords(instcode));
	}
	
	public boolean isAValidId(String _id) {
		if ((_id == null) || (_id.length() == 0)) {
			return false;
		}
		
		boolean b1 = (_id.substring(0,1)).matches("[a-zA-Z]");
        boolean b2 = _id.matches("\\w*");
		boolean b3 = checkKeywords(_id);
		
		return (b1 && b2 && b3);
	}
	
	public boolean isANumeral(String _num) {
		return _num.matches("\\d*");
	}
	
	public boolean checkKeywords(String _id) {
		String id = _id.toUpperCase();
		for(int i=0; i<keywords.length; i++) {
			if (id.compareTo(keywords[i]) == 0) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isIncluded(String _id, String[] _list) {
		String id = _id.toUpperCase();
		for(int i=0; i<_list.length; i++) {
			if (id.compareTo(_list[i].toUpperCase()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public String removeUndesiredWhiteSpaces(String _input, int _lineNb) {
		String error, tmp;
		int index0, index1, index2;
		
		return _input;
	}
	
	private String getEvtId(String _input) {
		int index = _input.indexOf('(');
			if (index == -1) {
				return _input;
			}
			return _input.substring(0, index);
	}
	
	private String getParams(String _input) {
		//System.out.println("input=" + _input);
		int index0 = _input.indexOf('(');
			int index1 = _input.indexOf(')');
			if ((index0 == -1) || (index1 == -1)) {
				return _input;
			}
			return _input.substring(index0 + 1, index1);
	}
	
	private String prepareString(String s) {
		return s.replaceAll("\\s", "");
	}
}
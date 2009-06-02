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

public class TMLMappingTextSpecification {
	public final static String CR = "\n";
	public final static String SP = " ";
	public final static String CR2 = "\n\n";
	public final static String SC = ";";
    
    private TMLTextSpecification tmlmtxt;
	private TMLArchiTextSpecification tmlatxt;
	private String spec;
	private String title;
	
	private TMLMapping tmlmap;
	private ArrayList<TMLTXTError> errors;
	private ArrayList<TMLTXTError> warnings;
	
	private String keywords[] = {"MAP", "SET", "TMLMAPPING", "ENDTMLMAPPING", "TMLSPEC", "TMLARCHI", "ENDTMLSPEC", "ENDTMLARCHI"};
	private String beginArray[] = {"TMLSPEC", "TMLARCHI", "TMLMAPPING"};
	private String endArray[] = {"ENDTMLSPEC", "ENDTMLARCHI", "ENDTMLMAPPING"};

	private String taskparameters[] = {"PRIORITY"};		
	
	
	public TMLMappingTextSpecification(String _title) {
		title = _title;
    }
	
	public void saveFile(String path, String filename) throws FileException {
		
		if (tmlmtxt != null) {
			tmlmtxt.saveFile(path, filename + ".tml");
		}
		if (tmlatxt != null) {
			tmlatxt.saveFile(path, filename + ".tarchi");
		}
		
		System.out.println("Saving architecture spec file in " + path + filename + ".tmap");
		String header = makeHeader(filename);
        FileUtils.saveFile(path + filename + ".tmap", indent(header + spec));
    }
	
	public TMLMapping getTMLMapping() {
		return tmlmap;
	}
	
	public String getSpec() {
		return spec;
	}
	
	public String indent(String _toIndent) {
		return indent(4, _toIndent);
	}
	
	public String indent(int _nbDec, String _toIndent) {
		int dec = 0;
        int indexEnd;
        String output = "";
        String tmp;
        int nbOpen = 0;
        int nbClose = 0;
		
		while ( (indexEnd = _toIndent.indexOf('\n')) > -1) {
			tmp = _toIndent.substring(0, indexEnd+1);
			try {
                _toIndent = _toIndent.substring(indexEnd+1, _toIndent.length());
            } catch (Exception e) {
                _toIndent = "";
            }
			nbOpen = nbOfOpen(tmp);
            nbClose = nbOfClose(tmp);
			dec -= nbClose * _nbDec;
            tmp = Conversion.addHead(tmp.trim(), ' ', dec);
            dec += nbOpen * _nbDec;
			//System.out.println("dec=" + dec);
            output += tmp + "\n";
		}
		_toIndent = output;
		return _toIndent;
	}
	
	private int nbOfOpen(String tmp) {
		return nbOf(tmp, beginArray);
	}
	
	private int nbOfClose(String tmp) {
		return nbOf(tmp, endArray);
	}
	
	private int nbOf(String _tmp, String[] array) {
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
	}
	
	public String toString() {
		return spec;
	}
	
	public String toTextFormat(TMLMapping _tmlmap) {
		tmlmap = _tmlmap;
		makeTML(tmlmap);
		makeArchi(tmlmap);
		makeMapping(tmlmap);
		return spec;
		//indent();
	}
	
	public void makeTML(TMLMapping tmlmap) {
		tmlmtxt = new TMLTextSpecification(title);
		tmlmtxt.toTextFormat(tmlmap.getTMLModeling());
	}
	
	public void makeArchi(TMLMapping tmlmap) {
		tmlatxt = new TMLArchiTextSpecification(title);
		tmlatxt.toTextFormat(tmlmap.getTMLArchitecture());
	}
	
	public void makeMapping(TMLMapping tmlmap) {
		spec = CR;
		spec +="TMLMAPPING" + CR;
		spec += makeMappingNodes(tmlmap);
		spec += makeMappingCommunicationNodes(tmlmap);
		spec += "ENDTMLMAPPING" + CR;
	}
	
	public String makeMappingNodes(TMLMapping tmlmap) {
		String tmp = "";
		ArrayList<HwExecutionNode> nodes = tmlmap.getNodes();
		ArrayList<TMLTask> tasks = tmlmap.getMappedTasks();
		HwNode node;
		TMLTask task;
		
		for(int i=0; i<nodes.size(); i++) {
			node = nodes.get(i);
			task = tasks.get(i);
			
			if ((node != null) && (task != null)) {
				tmp += "MAP " + prepareString(node.getName()) + " " + prepareString(task.getName()) + CR;
				tmp += "SET " + prepareString(task.getName()) +  " priority " + task.getPriority() + CR;
			}
		}
		
		return tmp;
	}
	
	public String makeMappingCommunicationNodes(TMLMapping tmlmap) {
		String tmp = "";
		ArrayList<HwCommunicationNode> nodes = tmlmap.getCommunicationNodes();
		ArrayList<TMLElement> elts = tmlmap.getMappedCommunicationElement();
		HwNode node;
		TMLElement elt;
		
		for(int i=0; i<nodes.size(); i++) {
			node = nodes.get(i);
			elt = elts.get(i);
			
			if ((node != null) && (elt != null)) {
				tmp += "MAP " + prepareString(node.getName()) + " " + prepareString(elt.getName()) + CR;
				//tmp += "SET " + prepareString(task.getName()) +  " priority " + task.getPriority() + CR;
			}
		}
		
		return tmp;
	}
	
	public String makeHeader(String _filename) {
		String tmp = "";
		tmp += "TMLSPEC" + CR;
		tmp += "#include \"" + _filename + ".tml\"" + CR;
		tmp += "ENDTMLSPEC" + CR2;
		tmp += "TMLARCHI" + CR;
		tmp += "#include \"" + _filename + ".tarchi\"" + CR;
		tmp += "ENDTMLARCHI" + CR;
		return tmp;
	}
	
	/*public String makeNodes(TMLArchitecture tmla) {
		String code = "";
		String name;
		String set;
		ArrayList<HwNode> hwnodes = tmla.getHwNodes();
		
		for(HwNode node: hwnodes) {
			if (node instanceof HwCPU) {
				name = prepareString(node.getName());
				set = "SET " + name + " ";
				code += "NODE CPU " +  name;
				code += set + "byteDataSize " + node.byteDataSize;  
				code += set + "pipelineSize " + node.pipelineSize;
				code += set + "goIdleTime " + node.goIdleTime;  
				code += set + "taskSwitchingTime " + node.taskSwitchingTime;
				code += set + "branchingPredictionPenalty " + node.branchingPredictionPenalty;  
				code += set + "schedulingPolicy " + node.schedulingPolicy;
			}
		}
		return code;
	}
	
	public String makeLinks(TMLArchitecture tmla) {
		String code = "";
		String name;
		String set;
		ArrayList<HwLink> hwlinks = tmla.getHwLinks();
		
		for(hwlinks link: hwlinks) {
			if (link instanceof HwLink) {
				name = prepareString(link.getName());
				set = "SET " + name + " ";
				code += "NODE LINK " +  name;
				code += set + "node " + prepareString(link.hwnode.getName());  
				code += set + "bus " + prepareString(link.bus.getName()); 
				code += set + "priority " + link.getPriority;  
			}
		}
	}*/
	
	/*public String makeDeclarations(TMLModeling tmlm) {
		int i;
		String sb = "";
		sb += "// TML Application - FORMAT 0.1" + CR;
		sb += "// Application: " + title + CR;
		sb += "// Generated: " + new Date().toString() + CR2; 
		
		sb += "// Channels" + CR;
		for(TMLChannel ch:tmlm.getChannels()) {
			sb += "CHANNEL" + SP + ch.getName() + SP + TMLChannel.getStringType(ch.getType()) + SP + ch.getSize();
			if (!ch.isInfinite()) {
				sb += SP + ch.getMax();
			}
			sb += SP + ch.getOriginTask().getName() + SP + ch.getDestinationTask().getName() + CR;
		}
		sb+= CR;
		
		sb += "// Events" + CR;
		for(TMLEvent evt:tmlm.getEvents()) {
			sb += "EVENT" + SP + evt.getName() + "(";
				for(i=0; i<evt.getNbOfParams(); i++) {
					if (i != 0) {
						sb+= ", ";
					}
					sb += TMLType.getStringType(evt.getType(i).getType());
				}
				sb += ")";
			sb += SP + evt.getTypeTextFormat();
			if (!evt.isInfinite()) {
				sb += SP + evt.getMaxSize();
			}
			sb +=  SP + evt.getOriginTask().getName() + SP + evt.getDestinationTask().getName();
			
			sb+= CR;
		}
		sb+= CR;
		
		sb += "// Requests" + CR;
		for(TMLRequest request:tmlm.getRequests()) {
			sb += "REQUEST" + SP + request.getName() + "(";
				for(i=0; i<request.getNbOfParams(); i++) {
					if (i != 0) {
						sb+= ", ";
					}
					sb += TMLType.getStringType(request.getType(i).getType());
				}
				sb += ")";
			for(TMLTask t: request.getOriginTasks()) {
				sb+= SP + t.getName();
			}
			sb += SP + request.getDestinationTask().getName();
			sb+= CR;
		}
		sb+= CR;
		
		return sb;
		
	}
	
	public String makeTasks(TMLModeling tmlm) {
		String sb = "";
		for(TMLTask task: tmlm.getTasks()) {
			sb += "TASK" + SP + task.getName() + CR;
			sb += makeActivity(task);
			sb += "ENDTASK" + CR2;
		}
		return sb;
	}
	
	public String makeActivity(TMLTask task) {
		String sb = "";
		sb += "//Local variables" + CR;
		
		for(TMLAttribute attr: task.getAttributes()) {
			sb += TMLType.getStringType(attr.getType().getType()) + SP + attr.getName();
			if ((attr.getInitialValue() != null) && (attr.getInitialValue().length() > 0)){
				sb += " = " + attr.getInitialValue();
			}
			sb += CR;
		}
		
		sb += CR;
		sb += "//Behavior" + CR;
		sb += makeBehavior(task, task.getActivityDiagram().getFirst());
		
		return sb;
	}*/
	
	public boolean makeTMLMapping(String _spec, String path) {
		DIPLOElement.resetID();
		
		spec = _spec;
		//tmlmap = new TMLMappingodeling();
		errors = new ArrayList<TMLTXTError>();  
		warnings = new ArrayList<TMLTXTError>();
		
		spec = Conversion.removeComments(spec);
		spec = applyInclude(spec, path);
		
		//System.out.println(spec);
		
		TMLModeling tmlm = makeTMLModeling();
		//System.out.println("TML modeling:" + tmlm);
		
		TMLArchitecture tarchi = makeArchitectureModeling();
		
		if ((errors.size() != 0) || (tmlm == null) || (tarchi == null)) {
			return false;
		}
		
		tmlmap = new TMLMapping(tmlm, tarchi, false);
		
		System.out.println("Compiling mapping...");
		
		browseCode();
		
		return (errors.size() == 0);
	}
	
	
	public TMLModeling makeTMLModeling() {
		TMLTextSpecification t = new TMLTextSpecification("from file");
		
		// Import errors and warnings
		
		String spectml;
		int index0 = spec.indexOf("TMLSPEC");
		int index1 = spec.indexOf("ENDTMLSPEC");
		
		if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
			addError(0, 0, 0, "No TMLSPEC / ENDTMLSPEC directives", null);
			return null;
		}
		
		spectml = spec.substring(index0 + 7, index1);
		
		System.out.println("Compiling TML...");
		
		boolean ret = t.makeTMLModeling(spectml);
		//errors.addAll(t.getErrors());
		//warnings.addAll(t.getWarnings());
		System.out.println(t.printSummary());
		
		if (!ret) {
			return null;
		}
		
		return t.getTMLModeling();
	}
	
	public TMLArchitecture makeArchitectureModeling() {
		TMLArchiTextSpecification t = new TMLArchiTextSpecification("from file");
		
		// Import errors and warnings
		
		String spectml;
		int index0 = spec.indexOf("TMLARCHI");
		int index1 = spec.indexOf("ENDTMLARCHI");
		
		if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
			addError(0, 0, 0, "No TMLARCHI / ENDTMLARCHI directives", null);
			return null;
		}
		
		spectml = spec.substring(index0 + 8, index1);
		
		System.out.println("Compiling architecture...");
		
		boolean ret = t.makeTMLArchitecture(spectml);
		//errors.addAll(t.getErrors());
		//warnings.addAll(t.getWarnings());
		System.out.println(t.printSummary());
		
		if (!ret) {
			return null;
		}
		
		return t.getTMLArchitecture();
	}
	
	public String printErrors() {
		String ret = "";
		for(TMLTXTError error: errors) {
			ret += "ERROR at line " + error.lineNb + ": " + error.message + CR;
			try {
				if (error.lineString == null) {
					ret += "->" + spec.split("\n")[error.lineNb] + CR2;
				} else {
					ret += "->" + error.lineString + CR2;
				}
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
		
		
        String s;
		String s1;
		String [] split;
		int lineNb = 0;
		
		String instruction;
		
		String specarchi;
		int index0 = spec.indexOf("TMLMAPPING");
		int index1 = spec.indexOf("ENDTMLMAPPING");
		
		if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
			addError(0, 0, 0, "No TMLMAPPING / ENDTMLMAPPING directives", null);
			return;
		}
		
		specarchi = spec.substring(index0 + 10, index1);
		
		StringReader sr = new StringReader(specarchi);
        BufferedReader br = new BufferedReader(sr);
		
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
			addError(0, lineNb, 0, "Exception when reading specification", null);
        }
	}
	
	public void addError(int _type, int _lineNb, int _charNb, String _msg, String _lineString) {
		TMLTXTError error = new TMLTXTError(_type);
		error.lineNb = _lineNb;
		error.charNb = _charNb;
		error.message = _msg;
		error.lineString = _lineString;
		errors.add(error);
	}
	
	public int analyseInstruction(String _line, int _lineNb, String[] _split) {
		String error;
		String params;
		String id;
		int tmp, tmp0, tmp1, i;
		
		HwExecutionNode hwnode;
		TMLTask task;
		HwCommunicationNode hwcommnode;
		TMLElement elt;
		
		// MAP
		if(isInstruction("MAP", _split[0])) {
			
			if (_split.length != 3) {
				error = "A MAP instruction must be used with 2 parameters, and not " + (_split.length - 1) ;
				addError(0, _lineNb, 0, error, _line);
				return -1;
			}
			
			if (!checkParameter("MAP", _split, 1, 0, _lineNb, _line)) {
				return -1;
			}
			
			if (!checkParameter("MAP", _split, 2, 0, _lineNb, _line)) {
				return -1;
			}
			
			hwnode = tmlmap.getHwExecutionNodeByName(_split[1]);
			
			if (hwnode == null) {
				hwcommnode = tmlmap.getHwCommunicationNodeByName(_split[1]);
				if (hwcommnode == null) {
					error = "No node named " + _split[1];
					addError(0, _lineNb, 0, error, _line);
					return -1;
				} else {
					elt = tmlmap.getCommunicationElementByName(_split[2]);
					if (elt == null) {
						error = "No communication element named " + _split[1];
						addError(0, _lineNb, 0, error, _line);
						return -1;
					}
					tmlmap.addCommToHwCommNode(elt, hwcommnode);
				}
			} else {
				task = tmlmap.getTaskByName(_split[2]);
				if (task == null) {
					error = "Unknown task: " + _split[2];
					addError(0, _lineNb, 0, error, _line);
					return -1;
				}
			
				tmlmap.addTaskToHwExecutionNode(task, hwnode);
			}
		} // MAP
		
		
		// SET
		if(isInstruction("SET", _split[0])) {
			
			if (_split.length != 4) {
				error = "A set instruction must be used with 3 parameters, and not " + (_split.length - 1) ;
				addError(0, _lineNb, 0, error, _line);
				return -1;
			}
			
			if (!checkParameter("SET", _split, 1, 0, _lineNb, _line)) {
				return -1;
			}
			
			if (!checkParameter("SET", _split, 2, 3, _lineNb, _line)) {
				return -1;
			}
			
			if (!checkParameter("SET", _split, 3, 1, _lineNb, _line)) {
				return -1;
			}
			
			task = tmlmap.getTaskByName(_split[1]);
			
			if (task == null) {
				error = "Unknown task: " + _split[1] ;
				addError(0, _lineNb, 0, error, _line);
				return -1;
			}
			
			if (_split[2].toUpperCase().equals("PRIORITY")) {
				task.setPriority(Integer.decode(_split[3]).intValue());
			}
			
		} // SET
		
		// Other command
		if((_split[0].length() > 0) && (!(isInstruction(_split[0])))) {
			error = "Syntax error in mapping information: unrecognized instruction: " + _split[0];
			addError(0, _lineNb, 0, error, _line);
			return -1;
			
		} // Other command
		
		return 0;
	}
	
	// Type 0: id
	// Type 1: numeral
	// Type 3: Task parameter  
	// Type 5: '='
	// Type 6: attribute value
	// Type 7: id or numeral
	
	public boolean checkParameter(String _inst, String[] _split, int _parameter, int _type, int _lineNb, String _line) {
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
			case 3:
				if (!isIncluded(_split[_parameter], taskparameters)) {
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
			}
		} else {
			err = true;
		}
		if (err) {
			error = "Unvalid parameter #" + _parameter + " ->" + _split[_parameter] + "<- in " + _inst + " instruction";
			addError(0, _lineNb, 0, error, _line);
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
	
	private String applyInclude(String _s, String path) {
		StringReader sr = new StringReader(spec);
        BufferedReader br = new BufferedReader(sr);
		String split[];
        String s;
		String s1;
		String output = "";
		String content;
		boolean found;
		int lineNb = 0;
		
        try {
            while((s = br.readLine()) != null) {
				lineNb ++;
				if (s != null) {
					found = false;
					s = s.trim();
					s1 = Conversion.replaceAllString(s, "\t", " ");
					s1 = Conversion.replaceRecursiveAllString(s1, "  ", " ");
					if (s1 != null) {
						split = s1.split("\\s");
						if (split.length > 0) {
							if ((split[0].equals("#include")) && (split.length == 2)) {
								s1 = split[1];
								if ((s1.charAt(0) == '\"') && (s1.charAt(s1.length()-1) == '\"')) {
									s1 = s1.substring(1, s1.length()-1).trim();
									//System.out.println("Loading file:" + path + s1);
									try {
										content = FileUtils.loadFile(path + s1);
										output += content + CR;
										found = true;
									} catch (FileException fe) {
										addError(0, lineNb, 0, "Could not include file:" + path + s1, null);
									}
								}
								
							}
						}
					}
					
					if (!found) {
						output += s + CR;
					}
					
				}
            }
        } catch (Exception e) {
            System.out.println("Exception when reading specification: " + e.getMessage());
			addError(0, lineNb, 0, "Exception when reading specification", null);
        }
		
		return output;
	}
}
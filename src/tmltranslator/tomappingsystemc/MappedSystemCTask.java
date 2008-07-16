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
 * Class MappedSystemCTask
 * Creation: 24/11/2005
 * @version 1.0 24/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.tomappingsystemc;

import java.util.*;

import tmltranslator.*;
import myutil.*;


public class MappedSystemCTask {
	//private TMLModeling tmlm;
    private TMLTask task;
    private String reference, cppcode, hcode;
	private ArrayList<TMLChannel> channels;
	private ArrayList<TMLEvent> events;
	private ArrayList<TMLRequest> requests;
	
	private int nbOfFunc = 0;
    
    private boolean debug;
	
	private final static String DOTH = ".h";
	private final static String DOTCPP = ".cpp";
	private final static String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";
	private final static String SCCR = ";\n";
	private final static String EFCR = "}\n";
	private final static String EFCR2 = "}\n\n";
	private final static String EF = "}";
	
	
    public MappedSystemCTask(TMLTask _task, ArrayList<TMLChannel> _channels, ArrayList<TMLEvent> _events, ArrayList<TMLRequest> _requests) {
        task = _task;
		channels = _channels;
		events = _events;
		requests = _requests;
        reference = task.getName();
		cppcode = "";
		hcode = "";
		nbOfFunc = 0;
    }
	
	public void saveInFiles(String path) throws FileException {	
		FileUtils.saveFile(path + reference + DOTH, getHCode());
		FileUtils.saveFile(path + reference + DOTCPP, getCPPCode());
	}
	
	public TMLTask getTMLTask() {
		return task;
	}
    
    
    public void generateSystemC(boolean _debug) {
        debug = _debug;
		basicHCode();
		basicCPPCode();
		makeClassCode();
    }
    
    public void print() {
		System.out.println("task: " + reference + DOTH + hcode);
		System.out.println("task: " + reference + DOTCPP + cppcode);
    }
	
	
	public String getCPPCode() {
		return cppcode;
	}
	
	public String getHCode() {
		return hcode;
	}
	
	public String getReference() {
		return reference;
	}
	
	
	// H Code
	//
	public void basicHCode() {
		hcode = SYSTEM_INCLUDE + CR2;
		
		hcode += "#ifndef " + reference.toUpperCase() + "__H" + CR;
		hcode += "#define " + reference.toUpperCase() + "__H" + CR2;
		
		hcode += "#include \"task_labsoc.h\"" + CR;
		hcode += "#include \"channel_labsoc.h\"" + CR;
		hcode += "#include \"event_labsoc.h\"" + CR2;
		hcode += "#include \"utils_labsoc.h\"" + CR2;
		
		hcode += "class " + reference + ";" + CR2;
		hcode += "typedef int (" + reference + "::*pt2func" + reference +")();" + CR2;
	}
	

	public void classHCode() {
	}
	
	
	
	// CPP Code
	//
	public void basicCPPCode() {
		cppcode = SYSTEM_INCLUDE + CR2;
		cppcode += "#include \"" + reference + DOTH + "\"" + CR2;
		
		cppcode += "int " + reference + "::run() {" + CR;
			
		if (debug) {
			cppcode += "cout<<\"" + reference + " runs\\n\";" + CR; 
		}
		cppcode += "return (this->*funcs[currentFunction])();" + CR + "}" + CR2;
	}
	
	public void classCPPCode() {
	}
	
	
	public void makeClassCode(){
		makeHeaderClassH();
		makeHeaderClassCPP();
		
		cppcode += makeNewFunction();
		if (task.isRequested()) {
			cppcode += makeFunctions(task.getActivityDiagram().getFirst(), 0, "");
		} else {
			cppcode += makeFunctions(task.getActivityDiagram().getFirst(), -1, "");
		}
		
		makeEndClassH();
		makeEndClassCPP();
		
		hcode = Conversion.indentString(hcode, 4);
		cppcode = Conversion.indentString(cppcode, 4);
	}
	
	public void makeHeaderClassH() {
		String tmp;
		
		// Common dec
		hcode += "class " + reference + ": public Task {" + CR;
		hcode += "private:" + CR;
		hcode += "int nbOfFunc;" + CR + "pt2func" + reference + " funcs[MAX_FUNC_TASK];" + CR2;
		
		// Attributes
		hcode += "// Attributes" + CR;
		hcode += makeAttributesDeclaration() + CR;
		
		// Channels
		hcode += "//Channels" + CR;
		for(TMLChannel ch: channels) {
			hcode += "TMLChannel *" + ch.getName() + ";" + CR;
		}
		hcode += CR;
		
		// Events
		hcode += "//Events" + CR;
		for(TMLEvent evt: events) {
			hcode += "TMLEvent *" + evt.getName() + ";" + CR;
		}
		hcode += CR;
		
		// Requests
		hcode += "//Requests" + CR;
		for(TMLRequest request: requests) {
			hcode += "TMLEvent *" + request.getName() + ";" + CR;
		}
		hcode += CR;
		
		
		
		// public dec
		hcode += "public:" + CR;
		// Simulation
		for(TMLChannel ch: channels) {
			if (ch.getOriginTask() == task) {
				hcode += "sc_signal<bool> wr__" + ch.getName() + ";" + CR;
			}
			if (ch.getDestinationTask() == task) {
				hcode += "sc_signal<bool> rd__" + ch.getName() + ";" + CR;
			}
		}
		for(TMLEvent evt: events) {
			if (evt.getOriginTask() == task) {
				hcode += "sc_signal<bool> notify__" + evt.getName() + ";" + CR;
			}
			if (evt.getDestinationTask() == task) {
				hcode += "sc_signal<bool> wait__" + evt.getName() + ";" + CR;
				hcode += "sc_signal<bool> notified__" + evt.getName() + ";" + CR;
			}
		}
		
		for(TMLRequest request: requests) {
			if (request.isAnOriginTask(task)) {
				hcode += "sc_signal<bool> sendrequest__" + request.getName() + ";" + CR;
			}
			if (request.getDestinationTask() == task) {
				hcode += "sc_signal<bool> wait4request__" + request.getName() + ";" + CR;
			}
		}
		hcode += CR;
		
		// Operations to set channels, events and requests
		if (channels.size() > 0) {
			tmp = "";
			hcode += "void setChannels(";
			for(TMLChannel ch: channels) {
				if (tmp.length() > 0) {
					hcode +=", ";
				}
				hcode += "TMLChannel *__" + ch.getName();
				tmp += ch.getName() + " = __" +  ch.getName() + ";" + CR;
			}
			hcode += "){" + CR + tmp + "}" + CR;
		}
		
		if (events.size() > 0) {
			tmp = "";
			hcode += "void setEvents(";
			for(TMLEvent evt: events) {
				if (tmp.length() > 0) {
					hcode +=", ";
				}
				hcode += "TMLEvent *__" + evt.getName();
				tmp += evt.getName() + " = __" +  evt.getName() + ";" + CR;
			}
			hcode += "){" + CR + tmp + "}" + CR;
		}
		
		if (requests.size() > 0) {
			tmp = "";
			hcode += "void setRequests(";
			for(TMLRequest req: requests) {
				if (tmp.length() > 0) {
					hcode +=", ";
				}
				hcode += "TMLEvent *__" + req.getName();
				tmp += req.getName() + " = __" +  req.getName() + ";" + CR;
			}
			hcode += "){" + CR + tmp + "}" + CR;
		}

		// Other operations
		hcode += "virtual int run()" + SCCR;
		hcode += "virtual ~" + reference + "() {}" + CR;
		hcode += "void initFuncs()" + SCCR + CR;
		
	}
	
	public void makeHeaderClassCPP() {
	}
	
	public void makeEndClassH() {
		hcode += /*reference + "::" +*/ reference + "() {" + CR;
		hcode += "nbOfFunc = " + nbOfFunc + SCCR;
		hcode += makeAttributesCode();
		hcode += "initFuncs()" + SCCR + EFCR;
		
		for(int i=0; i<nbOfFunc; i++) {
			hcode += "int func" + i + "()" + SCCR;
		}
		
		hcode += "};" + CR + "#endif" + CR;
	}
	
	public void	makeEndClassCPP() {
		cppcode += "void " + reference + "::initFuncs() {" + CR;
		for(int i=0; i<nbOfFunc; i++) {
			cppcode += "funcs[" + i + "] = &" + reference + "::func" + i + SCCR;
		}
		cppcode += EFCR;
		
	}
	
	private String makeAttributesCode() {
        String code = "";
        TMLAttribute att;
        int i;
        
        ListIterator iterator = task.getAttributes().listIterator();
        
        while(iterator.hasNext()) {
            att = (TMLAttribute)(iterator.next());
            if (att.hasInitialValue()) {
                code += att.name + " = " + att.initialValue;
				code += ";\n";
            }
        }
		
        
        return code;
    }
	
	private String makeAttributesDeclaration() {
        String code = "";
        TMLAttribute att;
        int i;
        
        ListIterator iterator = task.getAttributes().listIterator();
        
        while(iterator.hasNext()) {
            att = (TMLAttribute)(iterator.next());
            code += TMLType.getStringType(att.type.getType()) + " " + att.name;
            code += ";\n";
        }
        
		// Attributes for events
		code += "int arg__evt0" + SCCR;
		code += "int arg__evt1" + SCCR;
		code += "int arg__evt2" + SCCR;	
		
		//Adding request arguments
        if (task.isRequested()) {
            /*for(i=0; i<task.getRequest().getNbOfParams(); i++) {
                code += TMLType.getStringType(task.getRequest().getType(i).getType()) + " arg" + (i+1) + "__req;\n";
            }*/
			code += "int arg1__req" + SCCR;
			code += "int arg2__req" + SCCR;
			code += "int arg3__req" + SCCR;	
        }
		
		code += "int rnd__0" + SCCR;	
		
		// Array for storing select evts
		int select = task.getMaximumSelectEvtSize();
		//System.out.println("select=" + select);
		if (select > 0) {
			code += "// For storing events in SELECT_EVENT operations" + CR;
			code += "TMLEvent *select[" + select + "]" + SCCR;
			code += "sc_signal<bool> *sigs[" + select + "]" + SCCR;
			code += "int endfuncs[" +select + "]" + SCCR;
		}
		
        return code;
    }
	
	public String makeNewFunction() {
		String code = "int " + reference + "::func" + nbOfFunc + "() {" + CR;
		if (debug) {
			code += "cout<<\"" + reference + " func" +  nbOfFunc + "\\n\";" + CR;
		}
		nbOfFunc ++;
		return code;
	}
	
	public String makeTowardsNextFunction() {
		String code = nextCurrentFunc();
		code += "return func" + nbOfFunc + "();" + CR;
		return code;
	}
	
	public String makeTowardsFunction() {
		String code = "currentFunction = " + nbOfFunc + SCCR;
		code += "return func" + nbOfFunc + "()" + SCCR;
		return code;
	}
	
	public String nextCurrentFunc() {
		return "currentFunction++;" + CR;
	}
	


	public String makeFunctions(TMLActivityElement _tmlae, int nextFunctionToExecute, String codeToInsert) {
		String code="", code1, code2;
        int i, j;
		int current;
		TMLRandom tmlrandom;
        
		//System.out.println("Operator= " + _tmlae);
		
        if (_tmlae instanceof TMLStartState) {
			code = "";
			if (task.isRequested()) {
				//code += makeTowardsNextFunction() + EFCR;
				//code += makeNewFunction();
				TMLRequest req = task.getRequest();
				code1 = req.getName();
				code += "return WAIT_EVENT(" + code1;
				current = 0;
				
				for(i=0; i<3; i++) {
					code +=", &arg" + (i+1) + "__req";
				}
				code += ", &wait4request__" + code1;
				code += ", " + nbOfFunc + ")" + SCCR + EFCR2;
				code += makeNewFunction();
				code += "END_WAIT(&wait4request__" + code1 + ")" + SCCR;
				code += makeFunctions(_tmlae.getNextElement(0), 0, codeToInsert);
			} else {
				code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			}
			
        } else if (_tmlae instanceof TMLStopState) {
			if (nextFunctionToExecute == -1) {
				code += "TERMINATE()" + SCCR;
				code += "return 0" + SCCR;
				code += EFCR + CR;
			} else {
				code += codeToInsert;
				code += "currentFunction = " + nextFunctionToExecute + SCCR;
				code += "return func" + nextFunctionToExecute + "()" + SCCR;
				code += EFCR + CR;
			}
			
        } else if (_tmlae instanceof TMLActionState) {
            code1 = ((TMLActionState)_tmlae).getAction();
            code1 = addSemicolonIfNecessary(code1);
            code1 = modifyString(code1);
            code = code1 + CR; 
			code += makeFunctions( _tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
	
		} else if (_tmlae instanceof TMLRandom) {
			tmlrandom = ((TMLRandom)_tmlae);
            code1 = tmlrandom.getVariable() + " = Utils::myrand(" + tmlrandom.getMinValue() + ", " + tmlrandom.getMinValue() + ")";
            code1 = addSemicolonIfNecessary(code1);
            code1 = modifyString(code1);
            code = code1 + CR; 
			code += makeFunctions( _tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
        } else if (_tmlae instanceof TMLExecI) {
			code1 = nextCurrentFunc();
			code1 += "return EXECI(" + ((TMLExecI)_tmlae).getAction() + ")" + SCCR;
			code1 += EFCR + CR;
			code1 += makeNewFunction();
			code1 += "END_EXECI()" + SCCR;
            code = code1 + makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
        } else if (_tmlae instanceof TMLExecIInterval) {
			code1 = nextCurrentFunc();
            code1 += "rnd__0 = Utils::myrand(" + ((TMLExecIInterval)_tmlae).getMinDelay() + ", " + ((TMLExecIInterval)_tmlae).getMaxDelay() + ")" + SCCR;
            code1 += "return EXECI(rnd__0)" + SCCR;
			code1 += EFCR + CR;
			code1 += makeNewFunction();
			code1 += "END_EXECI()" + SCCR;
            code = code1 + makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
        } else if (_tmlae instanceof TMLForLoop) {
            TMLForLoop tmlfl = (TMLForLoop)_tmlae;
			code += tmlfl.getInit() +SCCR;
			code += makeTowardsNextFunction() + EFCR + CR;
			code += makeNewFunction();
			current = nbOfFunc;
			code += "if (" + tmlfl.getCondition() + ") {" + CR;
			code += makeTowardsNextFunction();
			code += "} else {" + CR;

			code1 =  makeNewFunction();
			code1 += makeFunctions(_tmlae.getNextElement(0), current - 1, tmlfl.getIncrement() + SCCR);
			code1 +=  makeNewFunction();
			
			code += "currentFunction = " + (nbOfFunc - 1) + SCCR;
			code += "return func" + (nbOfFunc -1) + "();" + CR;
			code += EF + CR + EF + CR2;
			
			code += code1;
			code += makeFunctions(_tmlae.getNextElement(1), nextFunctionToExecute, codeToInsert);
			
        } else if (_tmlae instanceof TMLReadChannel) {
			
			code += makeTowardsNextFunction() + EFCR2;
			code += makeNewFunction();
			code1 = ((TMLReadChannel)_tmlae).getChannel().getName();
			code += "return READ(" + code1 + ", ";
			code += ((TMLReadChannel)_tmlae).getNbOfSamples() + ", &rd__" + code1;
			code += ", " + nbOfFunc + ")" + SCCR + EFCR2;
			code += makeNewFunction();
			code += "END_READ(&rd__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
		} else if (_tmlae instanceof TMLWriteChannel) {
			
			code += makeTowardsNextFunction() + EFCR2;
			code += makeNewFunction();
			code1 = ((TMLWriteChannel)_tmlae).getChannel().getName();
			code += "return WRITE(" + code1 + ", ";
			code += ((TMLWriteChannel)_tmlae).getNbOfSamples() + ", &wr__" + code1;
			code += ", " + nbOfFunc + ")" + SCCR + EFCR;
			code += makeNewFunction();
			code += "END_WRITE(&wr__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
        } else if (_tmlae instanceof TMLSendEvent) {
			
            code += makeTowardsNextFunction() + EFCR2;
			code += makeNewFunction();
			code1 = ((TMLSendEvent)_tmlae).getEvent().getName();
			//code += nextCurrentFunc();
			code += "return NOTIFY_EVENT(" + code1;
			for(i=0; i<3; i++) {
				if (((TMLSendEvent)_tmlae).getParam(i) == null) {
					code += ", 0";
				} else {
					if (((TMLSendEvent)_tmlae).getParam(i).length() > 0) {
						code += ", " + ((TMLSendEvent)_tmlae).getParam(i);
					} else {
						code += ", 0";
					}
				}
            }
			code += ", &notify__" + code1 + ", ";
			code += nbOfFunc + ")" + SCCR + EFCR + CR;
			code += makeNewFunction();
			code += "END_NOTIFY(&notify__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute,codeToInsert);
			
        } else if (_tmlae instanceof TMLSendRequest) {
			
            code += makeTowardsNextFunction() + EFCR2;
			code += makeNewFunction();
			code1 = ((TMLSendRequest)_tmlae).getRequest().getName();
			code += nextCurrentFunc();
			code += "return NOTIFY_EVENT(" + code1;
			for(i=0; i<3; i++) {
				if (((TMLSendRequest)_tmlae).getParam(i) == null) {
					code += ", 0";
				} else {
					if (((TMLSendRequest)_tmlae).getParam(i).length() > 0) {
						code += ", " + ((TMLSendRequest)_tmlae).getParam(i);
					} else {
						code += ", 0";
					}
				}
            }
			code += ", &sendrequest__" + code1 + ", ";
			code += nbOfFunc + ")" + SCCR + EFCR + CR;
			code += makeNewFunction();
			code += "END_NOTIFY(&sendrequest__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute,codeToInsert);
			
        } else if (_tmlae instanceof TMLWaitEvent) {
            code += makeTowardsNextFunction() + EFCR2;
			code += makeNewFunction();
			code1 = ((TMLWaitEvent)_tmlae).getEvent().getName();
			code += "return WAIT_EVENT(" + code1;
			current = 0;
			for(i=0; i<3; i++) {
				if (((TMLWaitEvent)_tmlae).getParam(i) == null) {
					code += ", &arg__evt" + current;
					current ++;
				} else {
					if (((TMLWaitEvent)_tmlae).getParam(i).length() > 0) {
						code += ", &" + ((TMLWaitEvent)_tmlae).getParam(i);
					} else {
						code += ", &arg__evt" + current;
						current ++;
					}
				}
            }
			code += ", &wait__" + code1;
			code += ", " + nbOfFunc + ")" + SCCR + EFCR + CR;
			code += makeNewFunction();
			code += "END_WAIT(&wait__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
		} else if (_tmlae instanceof TMLNotifiedEvent) {
            //code += makeTowardsNextFunction() + EFCR2;
			//code += makeNewFunction();
			code1 =  ((TMLNotifiedEvent)_tmlae).getEvent().getName();
			code += nextCurrentFunc() + "return NOTIFIED_EVENT(" + code1;
			code += ", &" + ((TMLNotifiedEvent)_tmlae).getVariable();
			code += ", &notified__" + code1 + ")" + SCCR + EFCR2;
			code += makeNewFunction();
			if (debug) {
				code += "cout<<\"notified=\"<<" + ((TMLNotifiedEvent)_tmlae).getVariable() + "<<\"\\n\"" + SCCR;
			}
			code += "END_NOTIFIED(&notified__" + code1 + ")" + SCCR;
            code += makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
			
		} else if (_tmlae instanceof TMLSequence) {
            TMLSequence tmlseq = (TMLSequence)_tmlae;
			//System.out.println("TML sequence with " + tmlseq.getNbNext() + " nexts");
            
            if (tmlseq.getNbNext() == 0) {
                return code;
            } else {
                if (tmlseq.getNbNext() == 1) {
                    return makeFunctions(_tmlae.getNextElement(0), nextFunctionToExecute, codeToInsert);
                } else {
                    tmlseq.sortNexts();
                    // At least 2 next elements
					int end = nbOfFunc;
					int endFunc = 0;
					code1 = "";
					code1 += makeNewFunction() + makeFunctions(_tmlae.getNextElement(tmlseq.getNbNext() - 1), nextFunctionToExecute, codeToInsert);
					
					for(i=tmlseq.getNbNext() - 2; i>=0; i--) {
						endFunc = nbOfFunc;
						code1 += makeNewFunction() + makeFunctions(_tmlae.getNextElement(i), end, "");
						end = endFunc;
					}
					if (debug) {
						code += "cout<<\"Branching for sequence\"<<endl" + SCCR;
					}
					code = "currentFunction = " + endFunc + SCCR;
					code += "return func" + endFunc + "()" + SCCR + EFCR;
					code += code1;
					
                    /*for(i=0; i<tmlseq.getNbNext(); i++) {
						if (i < (tmlseq.getNbNext() - 1)) {
							code += makeFunctions(_tmlae.getNextElement(i), nbOfFunc + 1, codeToInsert);
							code += makeNewFunction();
						} else {
							code += makeFunctions(_tmlae.getNextElement(i), nextFunctionToExecute, codeToInsert);
						}
						//System.out.println("Code at next=" + i + " = " + code);
                    }*/
                }
            }
			
		} else if (_tmlae instanceof TMLChoice) {
            TMLChoice choice = (TMLChoice)_tmlae;
            //System.out.println("nb of guards = " + choice.getNbGuard() + " nb of nexts =" + choice.getNbNext());
            if (choice.getNbGuard() !=0 ) {
                code = "";
				code1 = "";
				int func = nbOfFunc;
                int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
				if (index2 != -1) {
					code1 += makeNewFunction();
					code1 += makeFunctions(_tmlae.getNextElement(index2), nextFunctionToExecute, codeToInsert);
					nextFunctionToExecute = func;
					codeToInsert = "";
				}
				int nb = choice.nbOfNonDeterministicGuard();
				int nbS = choice.nbOfStochasticGuard();
				String guardS = "";
				if ((nb > 0) || (nbS > 0)){
					code += "rnd__0 = Utils::myrand(0, 99)" + SCCR;
				}
				nb = 0;
                for(i=0; i<choice.getNbGuard(); i++) {
					if (choice.isNonDeterministicGuard(i)) {
						code2 = "(rnd__0 < " + Math.floor(100/choice.getNbGuard())*(nb+1) + ")";
						nb ++;
					} else if (choice.isStochasticGuard(i)) {
						if (guardS.length() == 0) {
							guardS = choice.getStochasticGuard(i);
						} else {
							guardS = "(" + guardS + ")+(" + choice.getStochasticGuard(i) + ")";
						}
						code2 = "(rnd__0 < (" + guardS + "))";
						nbS ++;
					} else {
						code2 = choice.getGuard(i);
						code2 = Conversion.replaceAllChar(code2, '[', "(");
						code2 = Conversion.replaceAllChar(code2, ']', ")");
					}
                    //System.out.println("guard = " + code1 + " i=" + i);
                    if (i != index2) {
                        if (i==0) {
                            code += "if " + code2;
                        } else {
                            code += " else ";
                            if (i != index1) {
                                code += "if " + code2;
                            }
                        }
                        code += " {" + CR;
						code += makeTowardsFunction();
						code += "}" + CR;
                        code1 += makeNewFunction() + makeFunctions(_tmlae.getNextElement(i), nextFunctionToExecute, codeToInsert);
                    } 
                }
				// If there was no else, do a terminate
				if (index1 == -1) {
					code += "TERMINATE()" + SCCR;
					code += "return 0" + SCCR;
				}
                code += EFCR + CR2;
				code += code1;
            }
			
		} else if (_tmlae instanceof TMLSelectEvt) {
			TMLSelectEvt sel = (TMLSelectEvt)(_tmlae);
			TMLEvent evt;
			code1 = "";
			
			// First, it shall put right information in the selectevt array
			for(i=0; i<_tmlae.getNbNext(); i++) {
				evt = sel.getEvent(i);
				if (evt != null) {
					code += "select[" + i + "] = " + evt.getName() + SCCR;
					code += "sigs[" + i + "] = &wait__" + evt.getName() + SCCR;
					code += "endfuncs[" + i + "] = " + nbOfFunc + SCCR;
					
					code1 +=  makeNewFunction();
					code1 += "END_SELECT(&wait__" + evt.getName() + ")" + SCCR;
					for(j=0; j<evt.getNbOfParams(); j++) {
						code1 += sel.getParam(i, j) + " = arg__evt" +j + SCCR; 
					}
					code1 += makeFunctions(_tmlae.getNextElement(i).getNextElement(0), nextFunctionToExecute, codeToInsert);
				} else {
					System.out.println("Select evt with " + _tmlae.getNbNext() + " nexts but id=" + i + " has no evt. Instead:" + _tmlae.getNextElement(i));
				}
			}
			code += "return SELECT_EVENT(select, " + _tmlae.getNbNext() + ", &arg__evt0, &arg__evt1, &arg__evt2, sigs, endfuncs)" + SCCR;
			code += EFCR;
			code += code1;
				
        } else {
            System.out.println("Operator: " + _tmlae + " is not managed in SystemC");
			//System.out.println("Code=" + code);
        }
        
        return code;
    }
	
    
    public String addSemicolonIfNecessary(String _input) {
        String code1 = _input.trim();
        if (!(code1.endsWith(";"))) {
            code1 += ";";
        }
        return code1;
    }
    
    public String modifyString(String _input) {
        //System.out.println("Modify string=" + _input);
        _input = Conversion.changeBinaryOperatorWithUnary(_input, "div", "/");
        _input = Conversion.changeBinaryOperatorWithUnary(_input, "mod", "%");
        //System.out.println("Modified string=" + _input);
        return _input;
    }
      
}

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
 * Class SystemCTask
 * Creation: 24/11/2005
 * @version 1.0 24/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator.tosystemc;

import java.util.*;

import tmltranslator.*;
import myutil.*;


public class SystemCTask {
    public TMLTask task;
    public String reference;
    
    private boolean debug;
    
    
    public SystemCTask(TMLTask _task) {
        task = _task;
        reference = "task__" + task.getName();
    }
    
    
    public void generateSystemC(boolean _debug) {
        debug = _debug;
        
    }
    
    public void print() {
        System.out.println(getFullCode());
    }
    
    public String getFullCode() {
        int i ;
        
        String output = "// ******** TASK: " + task.getName() + " **********\n// task reference = " + reference + "\n";
        output += "void " + task.getName() + "() {\n";
        
        String endTask = reference + ".STOP();\n";
        
        if (task.exits()) {
            endTask += "exit(0);\n";
        }
        
        
        String outputend = "}\n";
        
        String translatecode = calculateCode(task.getActivityDiagram().getFirst()) + endTask;
        
        // add start + attributes
        //...
        String attributecode = calculateAttributesCode();
        
        //Task is requested ?
        
        if (task.isRequested()) {
            String requestcode = "while(1) {\n";

            requestcode += reference + ".START();\n";
            requestcode += "wait(SC_ZERO_TIME);\n";
            // reading requests parameters
            for(i=0; i<task.getRequest().getNbOfParams(); i++) {
                requestcode += "arg" + (i+1) + "_req = task__" + ((TMLTask)(task.getRequest().getOriginTasks()).get(0)).getName() + ".rq" + (i+1) + ".read();\n";
            }
            requestcode += "\n";
            
            //adding regular code;
            requestcode += translatecode;
            
            requestcode += "\n";
            //requestcode += reference + ".STOP();\n";
            requestcode += "wait(" + task.getRequest().getName() + ");\n";

            requestcode += "}\n"; // End of while(1)
            translatecode = requestcode;
        } else {
            translatecode = reference + ".START();\n" + translatecode;
        }
        
        translatecode = output + "// attributes\n" + attributecode + "\n// code \n" + translatecode + outputend ;
        
        return translatecode;
    }
    
    private String calculateAttributesCode() {
        String code = "";
        TMLAttribute att;
        int i;
        
        ListIterator iterator = task.getAttributes().listIterator();
        
        while(iterator.hasNext()) {
            att = (TMLAttribute)(iterator.next());
            code += TMLType.getStringType(att.type.getType()) + " " + att.name;
            if (att.hasInitialValue()) {
                code += " = " + att.initialValue;
            }
            code += ";\n";
        }
        
        //adding request arguments
        if (task.isRequested()) {
            for(i=0; i<task.getRequest().getNbOfParams(); i++) {
                code += TMLType.getStringType(task.getRequest().getType(i).getType()) + " arg" + (i+1) + "_req;\n";
            }
        }
        
        
        return code;
    }
    
    public String calculateCode(TMLActivityElement _tmlae) {
        String code="", code1;
        int i;
        
        if (_tmlae instanceof TMLStartState) {
            code = calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLStopState) {
            
        } else if (_tmlae instanceof TMLActionState) {
            code1 = ((TMLActionState)_tmlae).getAction();
            code1 = addSemicolonIfNecessary(code1);
            code1 = modifyString(code1);
            code = code1 + "\n" + calculateCode( _tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLExecI) {
            code1 = reference + ".EXECI(" + ((TMLExecI)_tmlae).getAction() + ");\n";
            code = code1 + calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLExecIInterval) {
            code1 = "rnd__0 = TML_tasks::myrand(" + ((TMLExecIInterval)_tmlae).getMinDelay() + ", " + ((TMLExecIInterval)_tmlae).getMaxDelay() + ");\n";
            code1 = code1 + reference + ".EXECI(rnd__0);\n";
            code = code1 + calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLForLoop) {
            TMLForLoop tmlfl = (TMLForLoop)_tmlae;
            code = "for (" + tmlfl.getInit() + ";" + tmlfl.getCondition() + ";" + tmlfl.getIncrement() + ") {\n";
            code = code + calculateCode(_tmlae.getNextElement(0));
            code += "}\n";
            code += calculateCode(_tmlae.getNextElement(1));
        } else if (_tmlae instanceof TMLReadChannel) {
            code = reference + ".RD(" + ((TMLReadChannel)_tmlae).getNbOfSamples() + ", " + ((TMLReadChannel)_tmlae).getChannel().getName()+ ");\n";
            code += calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLSendEvent) {
            code = reference + ".NOTIFY(" + ((TMLSendEvent)_tmlae).getEvent().getName()+ ");\n";
            // parameters ?
            code += calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLSendRequest) {
            code = reference + ".REQ(" + ((TMLSendRequest)_tmlae).getRequest().getName();
            for(i=0; i<((TMLSendRequest)_tmlae).getNbOfParams(); i++) {
                if (((TMLSendRequest)_tmlae).getParam(i).length() > 0) {
                    code += ", " + ((TMLSendRequest)_tmlae).getParam(i);
                }
            }
            code += ");\n";
            code += calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLWaitEvent) {
            code = reference + "._WAIT(" + ((TMLWaitEvent)_tmlae).getEvent().getName()+ ");\n";
            //parameters ?
            code += calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLWriteChannel) {
            code = reference + ".WR(" + ((TMLWriteChannel)_tmlae).getNbOfSamples() + ", " + ((TMLWriteChannel)_tmlae).getChannel().getName()+ ");\n";
            code += calculateCode(_tmlae.getNextElement(0));
        } else if (_tmlae instanceof TMLChoice) {
            TMLChoice choice = (TMLChoice)_tmlae;
            //System.out.println("nb of guards = " + choice.getNbGuard() + " nb of nexts =" + choice.getNbNext());
            if (choice.getNbGuard() !=0 ) {
                code = "";
                int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
                for(i=0; i<choice.getNbGuard(); i++) {
                    code1 = choice.getGuard(i);
                    code1 = Conversion.replaceAllChar(code1, '[', "(");
                    code1 = Conversion.replaceAllChar(code1, ']', ")");
                    //System.out.println("guard = " + code1 + " i=" + i);
                    if (i != index2) {
                        if (i==0) {
                            code += "if " + code1;
                        } else {
                            code += " else ";
                            if (i != index1) {
                                code += "if " + code1;
                            }
                        }
                        code += " {\n";
                        code += calculateCode(_tmlae.getNextElement(i));
                        code += "}";
                    } 
                }
                code +="\n";
                if (index2 > -1) {
                    code += calculateCode(_tmlae.getNextElement(index2));
                }
            }
        } else if (_tmlae instanceof TMLSequence) {
            TMLSequence tmlseq = (TMLSequence)_tmlae;
            
            if (tmlseq.getNbNext() == 0) {
                return code;
            } else {
                
                
                if (tmlseq.getNbNext() == 1) {
                    return calculateCode(_tmlae.getNextElement(0));
                } else {
                    tmlseq.sortNexts();
                    // At least 2 next elements
                    for(i=1; i<tmlseq.getNbNext(); i++) {
                        code += calculateCode(_tmlae.getNextElement(i));
                    }
                }
            }
        } else {
            System.out.println("Operator: " + _tmlae + " is not managed in SystemC");
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

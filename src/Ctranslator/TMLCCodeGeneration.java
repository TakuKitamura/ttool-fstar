/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT enst.fr
   andrea.enrici AT enstr.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class TMLCCodeGeneration
   * Creation: 09/02/2014
   * @version 1.0 09/02/2014
   * @author Andrea ENRICI
   * @see
   */

package Ctranslator;

import java.util.*;
import java.nio.*;
import javax.swing.*;
import javax.swing.event.*;
import myutil.*;

import tmltranslator.*;
import ui.tmlcompd.*;

public class TMLCCodeGeneration	{

	public String title;

	private String CR = "\n";
	private String CR2 = "\n\n";
	private String TAB = "\t";
	private String TAB2 = "\t\t";
	private String TAB3 = "\t\t\t";
	private String TAB4 = "\t\t\t\t";
	private String SP = " ";
	private String SC = ";";
	private String COLON = ",";

	private TMLMapping tmap;
	private TMLModeling tmlm;
	private String applicationName;
	private String mainFile;
	private String headerString;
	private String programString;
	private String initString;
	private ArrayList<TMLTask> mappedTasks;
	private ArrayList<TMLElement> commElts;
	private ArrayList<Operation> operationsList = new ArrayList<Operation>();
	private int nonSDRoperationsCounter = 0;
	private int SDRoperationsCounter = 0;
	private int signalsCounter = 0;
	private ArrayList<String> signalsList = new ArrayList<String>();

	public JFrame frame; //Main Frame

	public TMLCCodeGeneration( String _title, String _applicationName, JFrame _frame )	{
		title = _title;
		applicationName = _applicationName;
		frame = _frame;
		init();
	}

	private void init()	{
		headerString = "";
		programString = "";
		initString = "";
		mappedTasks = new ArrayList<TMLTask>();
		commElts = new ArrayList<TMLElement>();
	}

	public void toTextFormat( TMLMapping _tmap , TMLModeling _tmlm )	{

		tmap = _tmap;
		tmlm = _tmlm;

		ArrayList<TMLTask> mappedTasks = tmap.getMappedTasks();
		ArrayList<TMLElement> commElts = tmap.getMappedCommunicationElement();

		//Generate the C code
		makeOperationsList( mappedTasks );
		generateMainFile();
		generateHeaderFile( mappedTasks );
		generateCProgram();
		generateInitProgram( mappedTasks );
	}

	private void generateMainFile()	{
		mainFile = "#include \"" + applicationName + "\".h" + CR2;
		mainFile += "int main(void)\t{" + CR +
								TAB + "int status=0;" + CR +
								TAB + "char *src_out_dat;" + CR +
								TAB + "char *dma1_out_dat;" + CR +
								TAB + "int g_r_size = 10240;" + CR +
								TAB + "int g_Ns = 1024;" + CR +
								TAB + "int g_Fi = 593;" + CR +
								TAB + "int g_Li = 116;" + CR2 +
								TAB + "src_out_dat = (char*) calloc(g_r_size*4, 1);" + CR +
								TAB + "if( src_out_dat == NULL ) exit(1);" + CR +
								TAB2 + "dma1_out_dat = (char*) calloc(4, 1);" + CR +
								TAB + "if( dma1_out_dat == NULL ) exit(1);" + CR +
								TAB2 + "FILE *source = fopen(\"date_demo.dat\", \"r\");" + CR +
								TAB + "if( source != NULL ){ " + CR +
								TAB2 + "fread(src_out_dat, 1, g_r_size*4, source);" + CR +
								TAB2 + "fclose(source);" + CR +
 								TAB + "} else printf(\"ERROR input file does not exist!\\n\");" + CR +
								TAB + applicationName + "_init( (char*)src_out_dat, (char*)dma1_out_dat, g_r_size, g_Ns , g_Fi, g_Li );" + CR +
								TAB + "status = " + applicationName + "();" + CR +
								TAB + "printf(\"score %d \", *(uint32_t*)dma1_out_dat );" + CR +
								TAB + "free(src_out_dat);" + CR +
								"}";
	}

	private void generateHeaderFile( ArrayList<TMLTask> mappedTasks )	{

		headerString += libraries();
		headerString += prototypes();
		headerString += buffers();
		headerString += instructions( mappedTasks );
		headerString += signals();
		headerString += variables();
	}

	private String libraries()	{
		String s = "#ifndef " + applicationName + "_H" + CR +
							"#define " + applicationName + "_H" + CR +
							"#include <stdio.h>" + CR +
							"#include <stdint.h>" + CR +
							"#include <embb/fep.h>" + CR +
							"#include <embb/memory.h>" + CR +
							"#include <embb/intl.h>" + CR2;
		return s;
	}

	private String prototypes()	{
		String s = 	"/**** prototypes *****/" + CR +
								"extern int " + applicationName + "_final(void);" + CR +
								"extern void " + applicationName + "_final_init();" + CR +
								"extern bool exit_rule(void);" + CR +
								"extern void register_operations(void);" + CR +
								"extern void register_fire_rules(void);" + CR +
								"extern void signal_to_buffer_init();" + CR +
								"extern void init_operations_context(void);" + CR +
								"extern void cleanup_operations_context(void);" + CR2;
		return s;
	}

	private String buffers()	{
		String s = "/**** Buffers *****/" + CR2;
		return s;
	}

	private String instructions( ArrayList<TMLTask> mappedTasks )	{
		String s = 	"/**** Instructions *****/" + CR;
		for( String s1: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			s += "extern embb_fep_context " + s1 + ";" + CR;
		}
		s += CR;
		for( String s1: getTaskNamePerMappedUnit( "MAPPER", mappedTasks ) )	{
			s += "extern embb_mapper_context " + s1 + ";" + CR;
		}
		s += CR;
		for( String s1: getTaskNamePerMappedUnit( "INTL", mappedTasks ) )	{
			s += "extern embb_intl_context " + s1 + ";" + CR;
		}
		s += CR;
		return s;
	}

	private String signals()	{

		String opsList = "";
		String s = "";
		Signal sig = new Signal();

		for( Operation op: operationsList )	{
			opsList += op.getName() + ",\n";
		}
		String temp = opsList.substring( 0, opsList.length() - 1 );
		opsList = temp;

		s =	"/********* SIGNAL TYPE ***************/" + CR +
				sig.toString() + CR2 +							
				"enum sigs_enu	{" + CR +
				getListOfWriteChannelPorts() + CR + /* list of comma separated output ports*/
				"NUM_SIGS };" + CR2 +
				"enum ops_enu	{" + CR +
				opsList + CR + /* list of comma separated tasks*/
				"NUM_OPS };" + CR2;
		
		return s;
	}

	//Returns a string containing the list of all output ports name for channels
	private String getListOfWriteChannelPorts()	{

		String s = "";
		TMLTask task;
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.NONSDR )	{
				task = op.getNONSDRTask();
			}
			else	{
				task = op.getSDRTasks().get( Operation.X_TASK );
			}
			if( task.getWriteChannels().size() > 0 )	{
				signalsList.add( task.getWriteChannels().toString().split("__")[1] );
				s += task.getWriteChannels().toString().split("__")[1] + ",\n";
				signalsCounter++;
			}
		}
		return s.substring( 0, s.length()-1 );
	}

	private String variables()	{
		String s = 	"/**** variables *****/" + CR +
								"extern SIG_TYPE sig[];" + CR +
								"#endif";
		return s;
	}

	private void generateCProgram()	{

		/*JOptionPane.showMessageDialog( frame,
																	 "The TURTLE Analysis contains several errors",
																	 "Syntax analysis failed",
																	 JOptionPane.INFORMATION_MESSAGE );*/
		Scheduler scheduler = new Scheduler( Scheduler.JAIR );
		programString += "#include " + "\"" + applicationName + ".h\"" + CR +
							"int (*operation[NUM_OPS])();" + CR +
							"bool (*fire_rule[NUM_OPS])();" + CR +
							"SIG_TYPE sig[NUM_SIGS]={{0}};" + CR2 +
							"/******** " + applicationName + "_final function *********/" + CR +
							"int " + applicationName + "_final(void)	{" + CR +
							"register_operations();" + CR +
							"register_fire_rules();" + CR +
							"signal_to_buffer_init();" + CR +
							"init_operations_context();" + CR2 +
							"/********* INIT PREX OPs signals ********/" + CR +
							initPrexOperations() + CR +
							"/********* OPs scheduler ***************/" + CR +
							scheduler.getCode() + CR +
							"cleanup_operations_context();" + CR + "}" + CR2;
		generateOperations();
		registerOperations();
		fireRules();
		registerFireRules();
		exitRule();
	}

	private String initPrexOperations()	{
		
		String s = "";
		ArrayList<TMLPort> prexList = new ArrayList<TMLPort>();
		for( TMLChannel ch: tmlm.getChannels() )	{
			TMLPort originPort = ch.getOriginPort();
			if( originPort.isPrex() )	{
				prexList.add( originPort );
			}
		}
		for( TMLPort port: prexList )	{
			s += "sig[ " + port.getName() +" ].f = true;" + CR;
		}
		return s;
	}

	//From the list of mapped tasks, built the list of operations. For SDR operations, only F_ tasks are considered.
	private void makeOperationsList( ArrayList<TMLTask> mappedTasks )	{

		ArrayList<TMLTask> SDRXtasks = new ArrayList<TMLTask>();
		ArrayList<TMLTask> SDRFtasks = new ArrayList<TMLTask>();

		String[] s;

		for( TMLTask task: mappedTasks )	{
			String taskName = task.getName().split( "__" )[1];
			s = taskName.split( "X_" );
			if( s.length > 1 )	{	//we are splitting an eXecution task
				SDRXtasks.add( task );
			}
			else	{	
				s = taskName.split( "F_" );
				if( s.length > 1 )	{	//we are splitting a Firing task
					SDRFtasks.add( task );
				}
				else	{	//it is a non-SDR operation
					operationsList.add( new Operation( task ) );
					nonSDRoperationsCounter++;
				}
			}
		}
		//Now I need to couple the tasks for SDRtasks
		for( TMLTask fTask: SDRFtasks )	{
			String fTaskName = fTask.getName().split( "__" )[1].split( "F_" )[1];
			for( TMLTask xTask: SDRXtasks )	{
				String xTaskName = xTask.getName().split( "__" )[1].split( "X_" )[1];
				if( xTaskName.equals( fTaskName ) )	{
					operationsList.add( new Operation( fTask, xTask ) );
					SDRoperationsCounter++;
				}
			}
		}
		TraceManager.addDev( "OperationsList: " + operationsList.toString() );
	}

	private void generateOperations()	{ //generate the code for the execution operations
		
		//for each operations add the exec code + the info for all the signals and stuff
		String exec_code = "";

		TMLTask fTask, xTask;
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.NONSDR )	{
				fTask = op.getNONSDRTask();
				xTask = fTask;
				programString += generateNONSDROperation( op, xTask, fTask );
			}
			else	{
				xTask = op.getSDRTasks().get( Operation.X_TASK );
				fTask = op.getSDRTasks().get( Operation.F_TASK );
				programString += generateSDROperation( op, xTask, fTask );
			}
		}
		/*DmaMEC myDMA = new DmaMEC( "dma", "ctx_TAB_to_FEP_RX1", "0x123", "0x456", "256", "NULL" );
		TraceManager.addDev( myDMA.getExecCode() );
		TraceManager.addDev( myDMA.getInitCode() );
		TraceManager.addDev( myDMA.getCleanupCode() );
		System.exit(0);*/
	}

	private String generateNONSDROperation( Operation op, TMLTask xTask, TMLTask fTask )	{
		
		String XOD = op.getName();
		String functionName = "int op_" + XOD + "()\t{" + CR +
													getTaskAttributes( fTask ) + CR +
													"static int size;" + CR +
													updateInSignals( xTask ) + CR;
		//no need to re-invent the wheel, re-use the code from TMLTextSpecification
		String exec_code = makeBehavior( fTask, fTask.getActivityDiagram().getFirst() );
		String endCode =	updateOutSignals( xTask ) + CR +
											"return status;" + CR +
											"}" + CR2;
		return functionName + exec_code + endCode;
	}

	private String makeBehavior( TMLTask task, TMLActivityElement elt ) {

		String code, code1, code2;
		TMLForLoop tmlfl;
		TMLActivityElementChannel tmlch;
		TMLActivityElementEvent tmlevt;
		TMLSendRequest tmlreq;
		TMLEvent evt;
		TMLRandom random;
		int i;
		String tmp1, tmp2;
		
		if( elt instanceof TMLStartState )	{
			return makeBehavior( task, elt.getNextElement(0) );
		}
		else if( elt instanceof TMLStopState )	{
			return "";			
		} else if( elt instanceof TMLExecI ) {	//ignored
			//code = "EXECI" + SP + modifyString(((TMLExecI)elt).getAction()) + CR;
			return /*code +*/ makeBehavior( task, elt.getNextElement(0) );
			
		} else if( elt instanceof TMLExecIInterval )	{	//ignored
			//code = "EXECI" + SP + modifyString(((TMLExecIInterval)elt).getMinDelay()) + SP + modifyString(((TMLExecIInterval)elt).getMaxDelay()) + CR;
			return /*code +*/ makeBehavior( task, elt.getNextElement(0) );
			
		} else if( elt instanceof TMLExecC )	{	//ignored
			//code = "EXECC" + SP + modifyString(((TMLExecC)elt).getAction()) + CR;
			return /*code +*/ makeBehavior( task, elt.getNextElement(0) );
			
		} else if( elt instanceof TMLExecCInterval ) {	//ignored
			//code = "EXECC" + SP + modifyString(((TMLExecCInterval)elt).getMinDelay()) + SP + modifyString(((TMLExecCInterval)elt).getMaxDelay()) + CR;
			return /*code +*/ makeBehavior( task, elt.getNextElement(0) );
			
		} else if( elt instanceof TMLDelay )	{	//ignored
			/*tmp1 = ((TMLDelay)elt).getMinDelay();
			tmp2 = ((TMLDelay)elt).getMaxDelay();
			if (tmp1.compareTo(tmp2) == 0) {
				code = "DELAY" + SP + modifyString(((TMLDelay)elt).getMinDelay()) + SP + modifyString(((TMLDelay)elt).getUnit()) + CR;
			} else {
				code = "DELAY" + SP + modifyString(((TMLDelay)elt).getMinDelay()) + SP + modifyString(((TMLDelay)elt).getMaxDelay()) + SP + modifyString(((TMLDelay)elt).getUnit()) + CR;
			}*/
			return /*code +*/ makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLForLoop )	{
			tmlfl = (TMLForLoop)elt;
			code = "for(" + tmlfl.getInit() + SC + SP;
			code += tmlfl.getCondition() + SC + SP;
			code += tmlfl.getIncrement() + ")" + TAB + "{" + CR;
			code += makeBehavior(task, elt.getNextElement(0));
			return code + "}" + CR + makeBehavior(task, elt.getNextElement(1));
		
		} else if( elt instanceof TMLRandom )	{	//ignored
			/*random = (TMLRandom)elt;
			code = "RANDOM" + SP + modifyString(""+random.getFunctionId()) + SP;
			code += modifyString(random.getVariable()) + SP;
			code += modifyString(random.getMinValue()) + SP;
			code += modifyString(random.getMaxValue()) + CR;*/
			return /*code +*/ makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLActionState )	{
			code = modifyString( ((TMLActivityElementWithAction)elt).getAction() ) + SC + CR;
			return code + makeBehavior( task, elt.getNextElement(0) );
			
		} else if( elt instanceof TMLWriteChannel )	{
			tmlch = (TMLActivityElementChannel)elt;
			code = "WRITE ";
			for(int k=0; k<tmlch.getNbOfChannels(); k++) {
				code = code + tmlch.getChannel(k).getName() + SP;
			}
			code = code + modifyString(tmlch.getNbOfSamples()) + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLReadChannel ) {
			tmlch = (TMLActivityElementChannel)elt;
			code = "READ " + tmlch.getChannel(0).getName() + SP + modifyString(tmlch.getNbOfSamples()) + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLSendEvent ) {
			tmlevt = (TMLActivityElementEvent)elt;
			code = "sig[ " + tmlevt.getEvent().getName().split("__")[1] + " ] = sig.[ " + tmlevt.getEvent().getName().split("__")[3] + " ]" + SC + CR;
			code += "sig[ " + tmlevt.getEvent().getName().split("__")[1] + " ].f = true" + SC + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLWaitEvent ) {
			tmlevt = (TMLActivityElementEvent)elt;
			code = "sig[ " + tmlevt.getEvent().getName().split("__")[1] + " ].f = false" + SC + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLNotifiedEvent ) {
			tmlevt = (TMLActivityElementEvent)elt;
			code = "NOTIFIED " + tmlevt.getEvent().getName() + " " + tmlevt.getVariable() + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		} else if( elt instanceof TMLSendRequest ) {
			tmlreq = (TMLSendRequest)elt;
			code = "REQUEST " + tmlreq.getRequest().getName() + " " + tmlreq.getAllParams(" ") + CR;
			return code + makeBehavior(task, elt.getNextElement(0));
			
		}  else if( elt instanceof TMLSequence ) {
			code = "";
			for(i=0; i<elt.getNbNext(); i++) {
				code += makeBehavior(task, elt.getNextElement(i));
			}
			return code;
		} else if( elt instanceof TMLChoice ) {
			TMLChoice choice = (TMLChoice)elt;
			code = "";
			if (choice.getNbGuard() !=0 ) {
				code1 = "";
				int index1 = choice.getElseGuard(), index2 = choice.getAfterGuard();
				int nb = Math.max(choice.nbOfNonDeterministicGuard(), choice.nbOfStochasticGuard());
				if (nb > 0) {
					// Assumed to be a non deterministic choice
					code += "RAND" + CR;
				}
				nb = 0;
        for(i=0; i<choice.getNbGuard(); i++) {
					if (i != index2) {
						if (choice.isNonDeterministicGuard(i)) {
							code2 = "" + (int)(Math.floor(100/choice.getNbGuard()));
							nb ++;
						} else if (choice.isStochasticGuard(i)){
							code2 = prepareString(choice.getStochasticGuard(i));
							nb ++;
						} else {
							code2 = modifyString(choice.getGuard(i));
							code2 = Conversion.replaceAllChar(code2, '[', "(");
							code2 = Conversion.replaceAllChar(code2, ']', ")");
						}
						//TraceManager.addDev("guard = " + code1 + " i=" + i);
						if (nb != 0) {
							/*if (choice.isNonDeterministicGuard(i)) {
								code = "CASERAND 50";
							} else {
								code = "CASERAND " + prepareString(choice.getStochasticGuard(i));
								
							}*/
							//nb ++;
							if (i != index1) {
								code += "CASERAND " + code2 + CR;
								code += makeBehavior(task, elt.getNextElement(i));
								code += "ENDCASERAND" + CR;
							}
						} else {
							if (i==0) {
								code += "IF " + code2;
							} else {
								if (i != index1) {
									code += "ORIF " + code2;
								} else {
									code += "ELSE";
								}
							}
							code += CR + makeBehavior(task, elt.getNextElement(i));
						}
					}
                }
				if (nb > 0) {
					// Assumed to be a non deterministic choice
					code += "ENDRAND" + CR;
				} else {
					code += "ENDIF" + CR;
				}
				if (index2 != -1) {
					code += makeBehavior(task, elt.getNextElement(index2));
				}
            }
			return code;
			
		} else if( elt instanceof TMLSelectEvt ) {
			code = "SELECTEVT" + CR;
			for( i = 0; i < elt.getNbNext(); i++ ) {
				try {
				tmlevt = (TMLActivityElementEvent)(elt.getNextElement(i));
				code += "CASE ";
				code += tmlevt.getEvent().getName() + " " + tmlevt.getAllParams(" ") + CR;
				code += makeBehavior(task, elt.getNextElement(i).getNextElement(0));
				code += "ENDCASE" + CR;
				} catch (Exception e) {
					TraceManager.addError("Non-event receiving following a select event operator");
				}
			}
			code += "ENDSELECTEVT" + CR;
			return code;
			
		} else if( elt instanceof TMLRandomSequence ) {
			code = "RANDOMSEQ" + CR;
			for(i=0; i<elt.getNbNext(); i++) {
				code += "SEQ" + CR;
				code += makeBehavior(task, elt.getNextElement(i));
				code += "ENDSEQ" + CR;
			}
			code += "ENDRANDOMSEQ" + CR;
			return code;
		} else {
			if( elt == null ) {
				return "";
			}
			TraceManager.addDev("Unrecognized element: " + elt);
			return makeBehavior(task, elt.getNextElement(0));
		}
	}

	private String generateSDROperation( Operation op, TMLTask xTask, TMLTask fTask )	{
		
		//For SDR operations the xTask is used to retrieve the mapped unit
		String exec_code = "";
		String XOD = op.getName();
		String functionName = "int op_" + XOD + "()\t{" + CR +
													getTaskAttributes( fTask ) + CR +
													"static int size;" + CR +
													updateInSignals( xTask ) + CR2;
		
		String mappedHwUnit = tmap.getHwNodeOf( xTask ).getName();
		if( mappedHwUnit.contains( "FEP" ) )	{
			if( XOD.contains( "CWP" ) || XOD.contains( "cwp" ) )	{
				CwpMEC cwp = new CwpMEC( XOD, "", "", "" );
				exec_code = cwp.getExecCode();
			}
			if( XOD.contains( "CWM" ) || XOD.contains( "cwm" ) )	{
				CwmMEC cwm = new CwmMEC( XOD, "", "", "" );
				exec_code = cwm.getExecCode();
			}
			if( XOD.contains( "CWA" ) || XOD.contains( "cwa" ) )	{
				CwaMEC cwa = new CwaMEC( XOD, "", "", "" );
				exec_code = cwa.getExecCode();
			}
			if( XOD.contains( "CWL" ) || XOD.contains( "cwl" ) )	{
				CwlMEC cwl = new CwlMEC( XOD, "", "", "" );
				exec_code = cwl.getExecCode();
			}
			if( XOD.contains( "SUM" ) || XOD.contains( "sum" ) )	{
				SumMEC sum = new SumMEC( XOD, "", "", "" );
				exec_code = sum.getExecCode();
			}
			if( XOD.contains( "FFT" ) || XOD.contains( "fft" ) )	{
				FftMEC fft = new FftMEC( XOD, "", "", "" );
				exec_code = fft.getExecCode();
			}
		}
		else if( mappedHwUnit.contains( "MAPPER" ) )	{
			MapperMEC mapp = new MapperMEC( XOD, "", "", "" );
			exec_code = mapp.getExecCode();
		}
		else if( mappedHwUnit.contains( "INTL" ) )	{
			InterleaverMEC intl = new InterleaverMEC( XOD, "", "", "" );
			exec_code = intl.getExecCode();
		}
		String endCode =	updateOutSignals( xTask ) + CR +
											"return status;" + CR +
											"}" + CR2;
		return functionName + exec_code + endCode;
	}

	private String getTaskAttributes( TMLTask task )	{

		String attributesList = "";
		String[] attributes = task.getAttributeString().split("/");
		for( int i = 0; i < attributes.length; i++ )	{
			if( attributes[i].length() > 1 )	{
				String s = attributes[i].split("\\.")[1];
				String name = s.split(":")[0];
				if( !name.contains( "__req" ) )	{	//filter out request parameters
					String type = s.split(":")[1].split("=")[0];
					String value = s.split(":")[1].split("=")[1];
					if( value.equals(" " ) )	{
						attributesList += type + " " + name + ";" + CR;
					}
					else	{
						attributesList += type + " " + name + " = " + value.substring( 0, value.length() - 1 ) + ";" + CR;
					}
				}
			}
		}
		return attributesList.substring( 0, attributesList.length() - 1 );	//remove last CR
	}

	private String updateOutSignals( TMLTask task )	{
		
		String s = "";
		for( TMLWriteChannel ch: task.getWriteChannels() )	{
			s += "sig.[" + ch.toString().split("__")[1] + "].f = true;" + CR;
		}
		return s;
	}

	private String updateInSignals( TMLTask task )	{
		
		String s = "";
		for( TMLReadChannel ch: task.getReadChannels() )	{
			s += "sig.[" + ch.toString().split("__")[1] + "].f = false;" + CR;
		}
		return s;
	}

	private void registerOperations()	{

		programString += "void register_operations( void )\t{" + CR;
		for( Operation op: operationsList )	{
			String XOD = op.getName();
			programString += TAB + "operation[" + XOD + "] = " + "op_" + XOD + ";" + CR;
		}
		programString += "}" + CR2;
	}

	private void fireRules()	{

		programString += "/**** OPs FIRE RULES ****/" + CR;
		for( Operation op: operationsList )	{
			programString += "bool fr_" + op.getName() + "( void )\t{" + CR;
			programString += "return (" + generateFireRuleCondition( op ) + ");" + CR;
			programString += "}" + CR2;
		}
		programString += CR;
	}

	private String generateFireRuleCondition( Operation op )	{
		
		TMLTask task;
		if( op.getType() == Operation.NONSDR )	{
			task = op.getNONSDRTask();
		}
		else	{
			task = op.getSDRTasks().get( Operation.X_TASK );
		}
		ArrayList<TMLWriteChannel> writeChannels = task.getWriteChannels();
		ArrayList<TMLReadChannel> readChannels = task.getReadChannels();
		ArrayList<String> writeList = new ArrayList<String>();
		ArrayList<String> readList = new ArrayList<String>();
		String s2 = "";
		String s1 = "";

		if( readChannels.size() > 0 )	{
			for( TMLReadChannel ch: readChannels )	{
				s1 += "( sig.[" + ch.toString().split("__")[1] + "].f ) && ";
			}
		}

		if( writeChannels.size() > 0 )	{
			for( TMLWriteChannel ch: writeChannels )	{
				s2 += "( !sig.[" + ch.toString().split("__")[1] + "].f ) && ";
			}
			String temp = s2.substring( 0, s2.length()-4 );
			s2 = temp;
		}

		return s1 + s2;
	}

	private void registerFireRules()	{

		programString += "void register_fire_rules( void )\t{" + CR;
		for( Operation op: operationsList )	{
			String XOD = op.getName();
			programString += TAB + "fire_rule[" + XOD + "] = " + "fr_" + XOD + ";" + CR;
		}
		programString += "}" + CR2;
	}

	private void exitRule()	{
		
		String s = "";
		ArrayList<TMLPort> postexList = new ArrayList<TMLPort>();
		for( TMLChannel ch: tmlm.getChannels() )	{
			TMLPort destinationPort = ch.getDestinationPort();
			if( destinationPort.isPostex() )	{
				postexList.add( destinationPort );
			}
		}
		for( TMLPort port: postexList )	{
			s += "( sig[ " + port.getName() +" ].f == true ) &&";
		}
		programString += 	"bool exit_rule(void)\t{" + CR +
											"return " + s.substring( 0, s.length() - 3 ) + SC + CR + "}";
	}

	private void generateInitProgram( ArrayList<TMLTask> mappedTasks )	{
		
		String init_code = "";
		String XOD = "";

		initString += "#include \"" + applicationName + "_final.h\"" + CR2;

		initString += "/**** variables ****/" + CR2;
		
		initString += "/**** buffers ****/" + CR2;

		initString += "/**** instructions ****/" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += "embb_fep_context " + s + ";" + CR;
		}
		initString += CR;
		for( String s: getTaskNamePerMappedUnit( "MAPPER", mappedTasks ) )	{
			initString += "embb_mapper_context " + s + ";" + CR;
		}
		initString += CR;
		for( String s: getTaskNamePerMappedUnit( "INTL", mappedTasks ) )	{
			initString += "embb_intl_context " + s + ";" + CR;
		}
		initString += CR;

		initString += "/**** init buffers ****/" + CR +
									"void " + applicationName + "_final_init()\t{" + CR + "}" + CR2;
		initString += initializeSignals() + CR;

		initString += "/**** init code ****/" + CR;

		ArrayList<Operation> SDRoperations = new ArrayList<Operation>();
		//Only for SDR operations
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				SDRoperations.add( op );
			}
		}

		//TMLTask task;
		for( TMLTask task: getTasksPerMappedUnit( "FEP", mappedTasks ) )	{
			XOD = task.getName().split("__")[1];
			if( XOD.contains( "CWP" ) || XOD.contains( "cwp" ) )	{
				CwpMEC cwp = new CwpMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwp.getInitCode();
			}
			else if( XOD.contains( "CWM" ) || XOD.contains( "cwm" ) )	{
				CwmMEC cwm = new CwmMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwm.getInitCode();
			}
			else if( XOD.contains( "CWA" ) || XOD.contains( "cwa" ) )	{
				CwaMEC cwa = new CwaMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwa.getInitCode();
			}
			else if( XOD.contains( "CWL" ) || XOD.contains( "cwl" ) )	{
				CwlMEC cwl = new CwlMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwl.getInitCode();
			}
			else if( XOD.contains( "SUM" ) || XOD.contains( "sum" ) )	{
				SumMEC sum = new SumMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = sum.getInitCode();
			}
			else if( XOD.contains( "FFT" ) || XOD.contains( "fft" ) )	{
				FftMEC fft = new FftMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = fft.getInitCode();
			}
			initString += init_code + CR;
			init_code = "";
		}
		for( TMLTask task: getTasksPerMappedUnit( "INTL", mappedTasks ) )	{
			XOD = task.getName().split("__")[1];
			InterleaverMEC intl = new InterleaverMEC( XOD, task.getID0(), task.getOD0(), "" );
			initString += intl.getInitCode() + CR;
		}
		for( TMLTask task: getTasksPerMappedUnit( "MAPPER", mappedTasks ) )	{
			XOD = task.getName().split("__")[1];
			MapperMEC mapp = new MapperMEC( XOD, task.getID0(), task.getOD0(), "" );
			initString += mapp.getInitCode() + CR;
		}

		initString += "/**** init contexts ****/" + CR +
									"void init_operations_context(void)\t{" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += TAB + "init_" + s + "();" + CR;
		}
		for( String s: getTaskNamePerMappedUnit( "MAPPER", mappedTasks ) )	{
			initString += TAB + "init_" + s + "();" + CR;
		}
		for( String s: getTaskNamePerMappedUnit( "INTL", mappedTasks ) )	{
			initString += TAB + "init_" + s + "();" + CR;
		}
		initString += "}" + CR2;

		initString += "/**** cleanup contexts ****/" + CR;
		initString += "void cleanup_operations_context( void )\t{" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += TAB + "fep_ctx_cleanup( &" + s + " );" + CR;
		}
		for( String s: getTaskNamePerMappedUnit( "MAPPER", mappedTasks ) )	{
			initString += TAB + "mapper_ctx_cleanup( &" + s + " );" + CR;
		}
		for( String s: getTaskNamePerMappedUnit( "INTL", mappedTasks ) )	{
			initString += TAB + "intl_ctx_cleanup( &" + s + " );" + CR;
		}
		initString += "}";
	}

	private String initializeSignals()	{
		String s = "void signal_to_buffer_init()\t{" + CR;
		for( int i = 0; i < signalsCounter; i++ )	{
			s += TAB + "sig[" + String.valueOf(i) + "].f = false;" + CR;
			s += TAB + "sig[" + String.valueOf(i) + "].roff = false;" + CR;
			s += TAB + "sig[" + String.valueOf(i) + "].woff = false;" + CR;
			s += TAB + "sig[" + String.valueOf(i) + "].pBuff = false;" + CR2;
		}
		return s + "}" + SC + CR;
	}
	
	private ArrayList<String> getTaskNamePerMappedUnit( String mappedUnit, ArrayList<TMLTask> mappedTasks )	{

		ArrayList<String> list = new ArrayList<String>();

		for( TMLTask task: mappedTasks )	{
			HwNode hwNode = tmap.getHwNodeOf( task );
			if( hwNode.getName().contains( mappedUnit.toUpperCase() ) || hwNode.getName().contains( mappedUnit.toLowerCase() ) )	{
				String XOP = task.getName().split( "__" )[1];
				list.add( XOP );
			}
		}
		return list;
	}

	private ArrayList<TMLTask> getTasksPerMappedUnit( String mappedUnit, ArrayList<TMLTask> mappedTasks )	{

		ArrayList<TMLTask> list = new ArrayList<TMLTask>();

		for( TMLTask task: mappedTasks )	{
			HwNode hwNode = tmap.getHwNodeOf( task );
			if( hwNode.getName().contains( mappedUnit.toUpperCase() ) || hwNode.getName().contains( mappedUnit.toLowerCase() ) )	{
				list.add( task );
			}
		}
		return list;
	}

	private static String prepareString(String s) {
		return s.replaceAll("\\s", "");
	}
	
	public static String modifyString(String s) {
		return prepareString(s);
	}

	public String toString()	{
		return headerString + programString;
	}

	public void saveFile( String path, String filename ) throws FileException {
		
		TraceManager.addUser( "Saving C files in " + path + filename );
		FileUtils.saveFile( path + "main.c", mainFile );
		FileUtils.saveFile( path + filename + ".h", headerString );
		FileUtils.saveFile( path + filename + ".c", programString );
		FileUtils.saveFile( path + filename + "_init.c", initString );
	}
}	//End of class

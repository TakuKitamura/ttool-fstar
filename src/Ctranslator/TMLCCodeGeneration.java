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

public class TMLCCodeGeneration	{

	public String title;

	private String applicationName;
	private String CR = "\n";
	private String CR2 = "\n\n";
	private String TAB = "\t";
	private String TAB2 = "\t\t";
	private String TAB3 = "\t\t\t";
	private String TAB4 = "\t\t\t\t";
	private TMLMapping tmap;
	private String headerString;
	private String programString;
	private String initString;
	private ArrayList<TMLTask> mappedTasks;
	private ArrayList<TMLElement> commElts;
	private ArrayList<Operation> operationsList = new ArrayList<Operation>();
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

	public void toTextFormat( TMLMapping _tmap )	{

		tmap = _tmap;
		ArrayList<TMLTask> mappedTasks = tmap.getMappedTasks();
		ArrayList<TMLElement> commElts = tmap.getMappedCommunicationElement();

		//Generate the C code
		makeOperationsList( mappedTasks );
		generateHeaderFile( mappedTasks );
		generateCProgram();
		generateInitProgram( mappedTasks );
	}

	public void generateHeaderFile( ArrayList<TMLTask> mappedTasks )	{

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
							"#include <embb/intl.h>" + CR +
							"#include \"eMIMO.h\"" + CR2;
		return s;
	}

	private String prototypes()	{
		String s = 	"/**** prototypes *****/" + CR +
								"extern int " + applicationName + "_final(void);" + CR +
								"extern void " + applicationName + "_final_init();" + CR +
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
			s += "extern FEP_CONTEXT " + s1 + ";" + CR;
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
				"NUM_SIGS };" + CR2;
		
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
				s += task.getWriteChannels().toString().split("__")[1] + ",\n";
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
		programString += "#include " + "\"" + applicationName + ".h\"" + CR +
							"int (*operation[NUM_OPS])();" + CR +
							"bool (*fire_rule[NUM_OPS])();" + CR +
							"SIG_TYPE sig[NUM_SIGS]={{0}};" + CR2 +
							"/******** " + applicationName + "_final function *********/" + CR +
							"int " + applicationName + "_final(void)	{" + CR +
							"bool valid_signal = false;" + CR +
							"bool blocked = true;" + CR +
							"int status = 0;" + CR2 +
							"register_operations();" + CR +
							"register_fire_rules();" + CR +
							"signal_to_buffer_init();" + CR +
							"init_operations_context();" + CR +
							"/********* INIT PREX OPs signals ********/" + CR +
							"sig[feed_out].f=true;" + CR +
							"sig[src_out].f=true;" + CR +
							"/********* OPs scheduler ***************/" + CR +
							TAB + "while( !exit_rule() )	{" + CR +
							TAB2 + "for( int n_op = 0; n_op < NUM_OPS; ++n_op )	{" + CR +
							TAB3 + "valid_signal = (*fire_rule[n_op])();" + CR +
							TAB3 + "if( valid_signal )	{" + CR +
							TAB4 + "status = (*operation[n_op])();" + CR + 
							TAB4 + "blocked = false;" + CR +
   						TAB3 + "}" + CR +
							TAB2 + "}" + CR +
							TAB2 + "if( blocked )	{" + CR +
							TAB3 + "printf(\"ERROR: the system got blocked, no new signals\\n\");" + CR +
							TAB3 + "return 1;" + CR +
							TAB2 + "}" + CR +
							TAB2 + "blocked = true;" + CR +
							TAB + "}" + CR +
							"cleanup_operations_context();" + CR + "}" + CR2;
		generateOperations();
		registerOperations();
		fireRules();
		registerFireRules();
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
	}

	private String generateNONSDROperation( Operation op, TMLTask xTask, TMLTask fTask )	{
		
		String XOD = op.getName();
		String functionName = "int op_" + XOD + "()\t{" + CR +
													getTaskAttributes( fTask ) + CR +
													"static int size;" + CR +
													updateInSignals( xTask ) + CR2;
		String exec_code = generateCodeFromActivity( fTask.getActivityDiagram() );
		String endCode =	updateOutSignals( xTask ) + CR +
											"return status;" + CR +
											"}" + CR2;
		return functionName + exec_code + endCode;
	}

	private String generateCodeFromActivity( TMLActivity ad )	{

		for( int i = 0; i < ad.nElements(); i++ )	{
			TraceManager.addDev( "Element: " + ad.get(i).toString() );
		}

		return "";
	}

	private String generateSDROperation( Operation op, TMLTask xTask, TMLTask fTask )	{
		
		String exec_code = "";
		String XOD = op.getName();
		String functionName = "int op_" + XOD + "()\t{" + CR +
													getTaskAttributes( fTask ) + CR +
													"static int size;" + CR +
													updateInSignals( xTask ) + CR2;
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
		return attributesList;
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
		programString += "}";
	}

	private void generateInitProgram( ArrayList<TMLTask> mappedTasks )	{
		
		String init_code = "";
		String XOD = "";

		initString += "#include \"" + applicationName + "_final.h\"" + CR2;

		initString += "/**** variables ****/" + CR2;
		
		initString += "/**** buffers ****/" + CR2;

		initString += "/**** instructions ****/" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += "FEP_CONTEXT " + s + ";" + CR;
		}
		initString += CR;

		initString += "/**** init buffers ****/" + CR +
									"void " + applicationName + "_final_init()\t{" + CR + "}" + CR +
									"void signal_to_buffer_init()\t{" + CR + "}" + CR2;

		initString += "/**** init code ****/" + CR;

		ArrayList<Operation> SDRoperations = new ArrayList<Operation>();
		//Only for SDR operations
		for( Operation op: operationsList )	{
			if( op.getType() == Operation.SDR )	{
				SDRoperations.add( op );
			}
		}

		TMLTask task;
		for( Operation op: SDRoperations )	{
			XOD = op.getName();
			task = op.getSDRTasks().get( Operation.X_TASK );
			if( XOD.contains( "CWP" ) || XOD.contains( "cwp" ) )	{
				CwpMEC cwp = new CwpMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwp.getInitCode();
			}
			if( XOD.contains( "CWM" ) || XOD.contains( "cwm" ) )	{
				CwmMEC cwm = new CwmMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwm.getInitCode();
			}
			if( XOD.contains( "CWA" ) || XOD.contains( "cwa" ) )	{
				CwaMEC cwa = new CwaMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwa.getInitCode();
			}
			if( XOD.contains( "CWL" ) || XOD.contains( "cwl" ) )	{
				CwlMEC cwl = new CwlMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = cwl.getInitCode();
			}
			if( XOD.contains( "SUM" ) || XOD.contains( "sum" ) )	{
				SumMEC sum = new SumMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = sum.getInitCode();
			}
			if( XOD.contains( "FFT" ) || XOD.contains( "fft" ) )	{
				FftMEC fft = new FftMEC( XOD, task.getID0(), task.getOD0(), "" );
				init_code = fft.getInitCode();
			}
			initString += init_code + CR;
			init_code = "";
		}

		initString += "/**** init contexts ****/" + CR +
									"void init_operations_context(void)\t{" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += TAB + "init_" + s + "();" + CR;
		}
		initString += "}" + CR2;

		initString += "/**** cleanup contexts ****/" + CR;
		initString += "void cleanup_operations_context( void )\t{" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += TAB + "fep_ctx_cleanup( &" + s + " );" + CR;
		}
		initString += "}";
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

	public String toString()	{
		return headerString + programString;
	}

	public void saveFile( String path, String filename ) throws FileException {
		
		TraceManager.addUser( "Saving C files in " + path + filename );
		FileUtils.saveFile( path + filename + ".h", headerString );
		FileUtils.saveFile( path + filename + ".c", programString );
		FileUtils.saveFile( path + filename + "_init.c", initString );
	}
}	//End of class

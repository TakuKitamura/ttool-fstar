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

	public TMLCCodeGeneration( String _title, String _applicationName )	{
		title = _title;
		applicationName = _applicationName;
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

		//Fill the data structure to get the needed information
		for( TMLTask task: mappedTasks )	{
			TraceManager.addDev( "Task " + task.getName() );
			TraceManager.addDev( "Write channel: " + task.getWriteChannels().toString() );
			TraceManager.addDev( "Read channel: " + task.getReadChannels().toString() );
		}
		
		//Generate the C code
		generateHeaderFile( mappedTasks );
		generateCProgram( mappedTasks );
		generateInitProgram( mappedTasks );
	}

	public void generateHeaderFile( ArrayList<TMLTask> mappedTasks )	{

		headerString += libraries();
		headerString += prototypes();
		headerString += buffers();
		headerString += instructions();
		headerString += signals( mappedTasks );
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

	private String instructions()	{
		String s = 	"/**** Instructions *****/" + CR2;
		return s;
	}

	private String signals( ArrayList<TMLTask> mappedTasks )	{

		String tasksList = "";
		String s = "";
		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			tasksList += XOD + ",\n";
		}
		String temp = tasksList.substring(0, tasksList.length() - 1 );
		tasksList = temp;

		s =	"/********* SIGNAL TYPE ***************/" + CR +
				"struct SIG_TYPE	{" + CR +
				"bool f; // new signal flag=1" + CR +
				"int  woff; // write offset" + CR +
				"int  roff; // read offset" + CR +
				"void *pBuff; // pointer to buffer" + CR +
				"};	typedef struct SIG_TYPE SIG_TYPE;" + CR2 +
							
				"enum sigs_enu	{" + CR +
				getListOfWriteChannelPorts( mappedTasks ) + CR + /* list of comma separated output ports*/
				"NUM_SIGS };" + CR2 +
				"enum ops_enu	{" + CR +
				tasksList + CR + /* list of comma separated tasks*/
				"NUM_SIGS };" + CR2;
		return s;
	}

	//Returns a string containing the list of all output ports name for channels
	private String getListOfWriteChannelPorts( ArrayList<TMLTask> mappedTasks )	{

		String s = "";
		for( TMLTask task: mappedTasks )	{
			if( task.getWriteChannels().size() > 0 )	{
				s += task.getWriteChannels().toString().split("__")[1] + ",\n";
			}
		}
		return s.substring( 0, s.length()-1 );
	}

	private String variables()	{
		String s = 	"/**** variables *****/" + CR +
								"#endif";
		return s;
	}

	private void generateCProgram( ArrayList<TMLTask> mappedTasks )	{
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
							"/********* OPs scheduller ***************/" + CR +
							TAB + "while( ERROR: there are not exit signals )	{" + CR +
							TAB2 + "for( int n_op = 0; n_op < NUM_OPS; ++n_op )	{" + CR +
							TAB3 + "valid_signal = (*fire_rule[n_op])();" + CR +
							TAB3 + "if( valid_signal )	{" + CR +
							TAB4 + "status = (*operation[n_op])();" + CR + 
							TAB4 + "blocked = false;" + CR +
   						TAB3 + "}" + CR +
							TAB2 + "}" + CR +
							TAB2 + "if( blocked )	{" + CR +
							TAB3 + "printf(\"ERROR:the system got blocked, no new signals\\n\");" + CR +
							TAB3 + "return 1;" + CR +
							TAB2 + "}" + CR +
							TAB2 + "blocked = true;" + CR +
							TAB + "}" + CR +
							"cleanup_operations_context();" + CR + "}" + CR2;
		generateOperations( mappedTasks );
		registerOperations( mappedTasks );
		fireRules( mappedTasks );
		registerFireRules( mappedTasks );
	}

	private void generateOperations( ArrayList<TMLTask> mappedTasks )	{ //generate the code for the execution operations
		
		//for each operations add the exec code + the info for all the signals and stuff
		String exec_code = "";

		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			String functionName = "int op_" + XOD + "()\t{" + CR +
														"int status=0;" + CR +
														"static int size;" + CR +
														"sig[ovlp_out].f = false;" + CR2;
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
			String endCode =	"sig[fft_out].f=true;" + CR +
												"return status;" + CR +
												"}" + CR2;
			programString += functionName + exec_code + endCode;
			exec_code = "";
		}
	}

	private void registerOperations( ArrayList<TMLTask> mappedTasks )	{

		programString += "void register_operations( void )\t{" + CR;
		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			programString += TAB + "operation[" + XOD + "] = " + "op_" + XOD + ";" + CR;
		}
		programString += "}" + CR2;
	}

	private void fireRules( ArrayList<TMLTask> mappedTasks )	{

		programString += "/**** OPs FIRE RULES ****/";
		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			programString += "bool fr_" + XOD + "( void )\t" + CR;
			programString += "return (" + generateFireRuleCondition( task ) + " );" + CR;
			programString += "}" + CR;
		}
		programString += CR;
	}

	private String generateFireRuleCondition( TMLTask task )	{
		return "";
	}

	private void registerFireRules( ArrayList<TMLTask> mappedTasks )	{

		programString += "void register_fire_rules( void )\t{" + CR;
		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			programString += TAB + "fire_rule[" + XOD + "] = " + "fr_" + XOD + ";" + CR;
		}
		programString += "}";
	}

	private void generateInitProgram( ArrayList<TMLTask> mappedTasks )	{
		
		String init_code = "";

		initString += "#include \"" + applicationName + "_final.h\"" + CR2;

		initString += "/**** variables ****/" + CR2;
		
		initString += "/**** buffers ****/" + CR2;

		initString += "/**** instructions ****/" + CR;
		for( String s: getTaskNamePerMappedUnit( "FEP", mappedTasks ) )	{
			initString += "FEP_CONTEXT " + s + ";" + CR;
		}
		initString += CR;

		initString += "/**** init buffers ****/" + CR +
									"void signal_to_buffer_init()\t{" + CR + "}" + CR2;

		initString += "/**** init code ****/" + CR;

		for( TMLTask task: mappedTasks )	{
			String XOD = task.getName().split( "__" )[1];
			if( XOD.contains( "CWP" ) || XOD.contains( "cwp" ) )	{
				CwpMEC cwp = new CwpMEC( XOD, "", "", "" );
				init_code = cwp.getInitCode();
			}
			if( XOD.contains( "CWM" ) || XOD.contains( "cwm" ) )	{
				CwmMEC cwm = new CwmMEC( XOD, "", "", "" );
				init_code = cwm.getInitCode();
			}
			if( XOD.contains( "CWA" ) || XOD.contains( "cwa" ) )	{
				CwaMEC cwa = new CwaMEC( XOD, "", "", "" );
				init_code = cwa.getInitCode();
			}
			if( XOD.contains( "CWL" ) || XOD.contains( "cwl" ) )	{
				CwlMEC cwl = new CwlMEC( XOD, "", "", "" );
				init_code = cwl.getInitCode();
			}
			if( XOD.contains( "SUM" ) || XOD.contains( "sum" ) )	{
				SumMEC sum = new SumMEC( XOD, "", "", "" );
				init_code = sum.getInitCode();
			}
			if( XOD.contains( "FFT" ) || XOD.contains( "fft" ) )	{
				FftMEC fft = new FftMEC( XOD, "", "", "" );
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

/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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
 */

package tmltranslator.modelcompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
   * Class OperationMEC, Model Extension Construct (MEC) class for operations
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
 */
public abstract class OperationMEC implements CCodeGenConstants {

	public static final int MAPP_OPERATION_MEC = 0;
	public static final int INTL_OPERATION_MEC = 1;
	public static final int ADAIF_OPERATION_MEC = 2;
	public static final int CPU_OPERATION_MEC = 3;
	public static final int FEP_OPERATION_MEC = 4;
	public static final int CWM_MEC = FEP_OPERATION_MEC + 1;
	public static final int CWA_MEC = FEP_OPERATION_MEC + 2;
	public static final int CWL_MEC = FEP_OPERATION_MEC + 3;
	public static final int CWP_MEC = FEP_OPERATION_MEC + 4;
	public static final int SUM_MEC = FEP_OPERATION_MEC + 5;
	public static final int FFT_MEC = FEP_OPERATION_MEC + 6;

	private static final String[] CONTEXTS = { "MAPPER_CONTEXT", "INTL_CONTEXT", "ADAIF_CONTEXT", "EMBB_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT", "FEP_CONTEXT" };
	public static final List<String> CONTEXTS_LIST = new ArrayList<String>( Arrays.asList( CONTEXTS ) );

//	protected String CR = "\n";
//	protected String TAB = "\t";
	protected String name = new String();
	protected String init_code = new String();
	protected String exec_code = new String();
	protected String cleanup_code = new String();
	protected String context = new String();
	
	protected String ID0 = new String();
	protected String OD0 = new String();
	protected String XOP = new String();

	public ArchUnitMEC archUnitMEC;
	
	public OperationMEC() {
		name = "OperationMEC";
	}

	public String getExecCode()	{
		return exec_code;
	}

	public String getInitCode()	{
		return init_code;
	}
//
//	public String getContext()	{
//		return context;
//	}

	@Override
	public String toString()	{
		return name;
	}
}	//End of class

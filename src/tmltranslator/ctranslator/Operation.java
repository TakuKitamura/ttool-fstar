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
   * Class Operation for code generation
   * Creation: 11/02/2014
   * @version 1.0 11/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;
import java.nio.*;
import myutil.*;

import tmltranslator.*;

public class Operation	{

	public static final int NONSDR = 0;
	public static final int SDR = 1;
	public static final int F_TASK = 1;
	public static final int X_TASK = 1;
	private int type;
	private String name = "";
	private TMLTask fTask;
	private TMLTask xTask;
	private boolean prex;
	private boolean postex;
	private Signal inSignal;
	private Signal outSignal;
	private Buffer inBuffer;
	private Buffer outBuffer;
	private HwNode xHwNode;
	private HwNode fHwNode;


	//Constructor for non-SDR operations
	public Operation( TMLTask _task )	{
		name = _task.getName().split( "__" )[1];
		fTask = _task;
		xTask = null;
		type = 0;	//NONSDR
	}
	
	//Constructor for SDR operations, before the introduction of signals
	public Operation( TMLTask _task1, TMLTask _task2 )	{	//First pass the F task
		name = _task1.getName().split( "__" )[1].split( "F_" )[1];
		fTask = _task1;
		xTask = _task2;
		type = 1;	//SDR
	}

	//Constructor for SDR operations with input (READ channels and events) and output (WRITE channels and events) signals
	public Operation( TMLTask _xTask, TMLTask _fTask, HwNode _xHwNode, HwNode _fHwNode, Signal _inSignal, Signal _outSignal, Buffer _inBuffer, Buffer _outBuffer )	{	//First pass the F task
		name = _xTask.getName().split( "__" )[1].split( "F_" )[1];
		fTask = _xTask;
		xTask = _fTask;
		xHwNode = _xHwNode;
		fHwNode = _fHwNode;
		inSignal = _inSignal;
		outSignal = _outSignal;
		inBuffer = _inBuffer;
		outBuffer = _outBuffer;
		type = 1;	//SDR
	}

	public TMLTask getNONSDRTask()	{
		return fTask;
	}

	public TMLTask getXTask()	{
		return xTask;
	}

	public TMLTask getFTask()	{
		return fTask;
	}

	public ArrayList<TMLTask> getSDRTasks()	{
		ArrayList<TMLTask> tasks = new ArrayList<TMLTask>();
		tasks.add( fTask );
		tasks.add( xTask );
		return tasks;
	}

	public boolean isSDRoperation()	{
		return ( type == Operation.SDR );
	}

	public String getName()	{
		return "F_" + name;
	}

	public String getContextName()	{
		return "X_" + name;
	}

	public int getType()	{
		return type;
	}

	public Signal getInSignal()	{
		return inSignal;
	}

	public Signal getOutSignal()	{
		return outSignal;
	}

	public void setInBuffer( Buffer _inBuffer)	{
		inBuffer = _inBuffer;
	}

	public void setOutBuffer( Buffer _outBuffer)	{
		outBuffer = _outBuffer;
	}

	public Buffer getInBuffer()	{
		return inBuffer;
	}

	public Buffer getOutBuffer()	{
		return outBuffer;
	}

	public String toString()	{
		if( ( inSignal != null ) && ( outSignal != null ) )	{
			return 	"OPERATION " + name + "\n\t" +
							"inSignal: " + inSignal.getName() + "\n\t" +
							"outSignal: " + outSignal.getName() + "\n\t" +
							"X task HwExecutionNode: " + xHwNode.getName() + "\n\t" +
							"X task MEC: " + xHwNode.getTaskMEC().toString() + "\n\t" +
							"F task HwExecutionNode: " + fHwNode.getName() + "\n\t" +
							"F task MEC: " + fHwNode.getTaskMEC().toString() + "\n\t" +
							"inBuffer: " + inBuffer.toString() + "\n\t" + 
							"outBuffer: " + outBuffer.toString();
		}
		else if( inSignal == null )	{
			return 	"OPERATION " + name + "\n\t" +
							"outSignal: " + outSignal.getName() + "\n\t" +
							"X task HwExecutionNode: " + xHwNode.getName() + "\n\t" +
							"X task MEC: " + xHwNode.getTaskMEC().toString() + "\n\t" +
							"F task HwExecutionNode: " + fHwNode.getName() + "\n\t" +
							"F task MEC: " + fHwNode.getTaskMEC().toString() + "\n\t" +
							"outBuffer: " + outBuffer.toString();
		}
		else if( outSignal == null )	{
			return 	"OPERATION " + name + "\n\t" +
							"inSignal: " + inSignal.getName() + "\n\t" +
							"X task HwExecutionNode: " + xHwNode.getName() + "\n\t" +
							"X task MEC: " + xHwNode.getTaskMEC().toString() + "\n\t" +
							"F task HwExecutionNode: " + fHwNode.getName() + "\n\t" +
							"F task MEC: " + fHwNode.getTaskMEC().toString() + "\n\t" +
							"inBuffer: " + inBuffer.toString();
		}
		return "void OPERATION";
	}

}	//End of class

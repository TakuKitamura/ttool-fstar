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

import tmltranslator.HwNode;
import tmltranslator.TMLTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Operation for code generation Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class Operation {

  public static final int NONSDR = 0;
  public static final int SDR = 1;
  public static final int F_TASK = 1;
  public static final int X_TASK = 1;
  private int type;
  private String name = "";
  private TMLTask fTask;
  private TMLTask xTask;
  // private boolean prex;
  // private boolean postex;
  private List<Signal> inSignals = new ArrayList<Signal>();
  private Signal outSignal;
  private Buffer inBuffer;
  private Buffer outBuffer;
  private HwNode xHwNode;
  private HwNode fHwNode;
  private boolean isPrex;
  private boolean isPostex;

  // Constructor for SDR operations with input (READ channels and events) and
  // output (WRITE channels and events) signals
  public Operation(TMLTask _xTask, TMLTask _fTask, HwNode _xHwNode, HwNode _fHwNode, List<Signal> _inSignals,
      Signal _outSignal, Buffer _inBuffer, Buffer _outBuffer, boolean _isPrex, boolean _isPostex) { // First pass the F
                                                                                                    // task
    name = _xTask.getName().split("__")[1].split("F_")[1];
    fTask = _xTask;
    xTask = _fTask;
    xHwNode = _xHwNode;
    fHwNode = _fHwNode;
    inSignals = _inSignals;
    outSignal = _outSignal;
    inBuffer = _inBuffer;
    outBuffer = _outBuffer;
    isPrex = _isPrex;
    isPostex = _isPostex;
    type = 1; // SDR
  }

  public TMLTask getNONSDRTask() {
    return fTask;
  }

  public TMLTask getXTask() {
    return xTask;
  }

  public TMLTask getFTask() {
    return fTask;
  }

  public List<TMLTask> getSDRTasks() {
    List<TMLTask> tasks = new ArrayList<TMLTask>();
    tasks.add(fTask);
    tasks.add(xTask);

    return tasks;
  }

  public boolean isSDRoperation() {
    return (type == Operation.SDR);
  }

  public String getName() {
    return "F_" + name;
  }

  public String getContextName() {
    return "X_" + name + "_ctx";
  }

  public int getType() {
    return type;
  }

  public List<Signal> getInSignals() {
    return inSignals;
  }

  public void setInSignals(List<Signal> _list) {
    inSignals = _list;
  }

  public Signal getOutSignal() {
    return outSignal;
  }

  public void setInBuffer(Buffer _inBuffer) {
    inBuffer = _inBuffer;
  }

  public void setOutBuffer(Buffer _outBuffer) {
    outBuffer = _outBuffer;
  }

  public Buffer getInBuffer() {
    return inBuffer;
  }

  public Buffer getOutBuffer() {
    return outBuffer;
  }

  public boolean isPrex() {
    return isPrex;
  }

  public boolean isPostex() {
    return isPostex;
  }

  public String getFireRuleCondition() {

    StringBuffer frCondition = new StringBuffer();

    if ((inSignals.size() != 0) && (outSignal != null)) {
      for (Signal sig : inSignals) {
        frCondition.append("( sig[ " + sig.getName() + " ].f ) &&");
      }
      if (outSignal.isAJoinSignal()) { // get the port name associated to the current xTask
        ArrayList<TMLTask> tasksList = outSignal.getTMLChannel().getOriginTasks();
        for (int i = 0; i < tasksList.size(); i++) {
          if (tasksList.get(i).getName().equals(xTask.getName())) {
            frCondition.append(" ( !sig[ " + outSignal.getTMLChannel().getOriginPorts().get(i).getName() + " ].f )");
          }
        }
      } else {
        frCondition.append(" ( !sig[ " + outSignal.getName() + " ].f )");
      }
    } else if (inSignals.size() == 0) { // prex Operation
      frCondition.append("( !sig[ " + outSignal.getName() + " ].f )");
    } else if (outSignal == null) { // postex Operation
      if (inSignals.size() > 1) { // the fire rule condition is given by the logical AND of all input ports of the
                                  // join signal
        for (Signal sig : inSignals) {
          frCondition.append(" ( sig[ " + sig.getName() + " ].f ) &&");
        }
        frCondition = new StringBuffer(frCondition.toString().substring(0, frCondition.length() - 3));
      } else {
        frCondition.append("( sig[ " + inSignals.get(0).getName() + " ].f )");
      }
    }
    return frCondition.toString();
  }

  public String toString() {
    String s = "OPERATION " + name + "\n\t" + "isPrex: " + isPrex + "\n\t" + "isPostex: " + isPostex + "\n\t";
    if ((inSignals.size() != 0) && (outSignal != null)) {
      for (Signal sig : inSignals) {
        s += "inSignal: " + sig.getName() + "\n\t";
      }
      s += "outSignal: " + outSignal.getName() + "\n\t" + "X task HwExecutionNode: " + xHwNode.getName() + "\n\t"
          + "X task MEC: " + xHwNode.getArchUnitMEC().toString() + "\n\t" + "F task HwExecutionNode: "
          + fHwNode.getName() + "\n\t" + "F task MEC: " + fHwNode.getArchUnitMEC().toString() + "\n\t" + "inBuffer: "
          + inBuffer.toString() + "\n\t" + "outBuffer: " + outBuffer.toString();
    } else if (inSignals.size() == 0) {
      s += "outSignal: " + outSignal.getName() + "\n\t" + "X task HwExecutionNode: " + xHwNode.getName() + "\n\t"
          + "X task MEC: " + xHwNode.getArchUnitMEC().toString() + "\n\t" + "F task HwExecutionNode: "
          + fHwNode.getName() + "\n\t" + "F task MEC: " + fHwNode.getArchUnitMEC().toString() + "\n\t" + "outBuffer: "
          + outBuffer.toString();
    } else if (outSignal == null) {
      for (Signal sig : inSignals) {
        s += "inSignal: " + sig.getName() + "\n\t";
      }
      s += "X task HwExecutionNode: " + xHwNode.getName() + "\n\t" + "X task MEC: "
          + xHwNode.getArchUnitMEC().toString() + "\n\t" + "F task HwExecutionNode: " + fHwNode.getName() + "\n\t"
          + "F task MEC: " + fHwNode.getArchUnitMEC().toString() + "\n\t" + "inBuffer: " + inBuffer.toString();
    }
    return s;
  }
} // End of class

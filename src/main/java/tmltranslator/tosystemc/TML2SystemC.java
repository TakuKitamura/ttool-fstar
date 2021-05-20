/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */

package tmltranslator.tosystemc;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import tmltranslator.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class TML2SystemC Creation: 24/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 24/11/2005
 */
public class TML2SystemC {

  // private static int gateId;

  private TMLModeling<?> tmlmodeling;
  private List<SystemCTask> tasks;

  // private boolean debug;

  public static String SYSTEMC_EXTENSION = "cpp";

  public static String DECL_CODE_01 = "public class ";

  private String header, classBegin, referencesToTMLTasks, channelsDec, requestsDec, eventsDec;
  private String hasProcess, testBench;
  private String endClass, mainFunction;

  public TML2SystemC(TMLModeling<?> _tmlmodeling) {
    tmlmodeling = _tmlmodeling;
  }

  public void saveFile(String path, String filename) throws FileException {
    FileUtils.saveFile(path + filename + "." + SYSTEMC_EXTENSION, getFullCode());
  }

  public void generateSystemC(boolean _debug) {
    // debug = _debug;

    tasks = new LinkedList<SystemCTask>();

    generateSystemCTasks();

    generateHeader();
    generateClassBegin();
    generateReferencesToTMLTasks();

    generateChannelsDec();
    generateEventsDec();
    generateRequestsDec();

    generateHasProcess();
    generateTestBench();

    generateEndClass();
    generateMainFunction();

  }

  public void print() {
    TraceManager.addDev(getFullCode());
  }

  public String getFullCode() {
    String s = header + "\n\n" + classBegin + "\n" + referencesToTMLTasks + "\n" + channelsDec + "\n" + requestsDec
        + "\n" + eventsDec + "\n";
    s += hasProcess + "\n" + testBench;
    s += getFullCodeTasks();
    s += endClass;
    s += "\n" + mainFunction;
    s = Conversion.indentString(s, 4);
    return s;
  }

  public String getFullCodeTasks() {
    Iterator<SystemCTask> iterator = tasks.listIterator();
    SystemCTask st;

    String output = "\n\n";

    while (iterator.hasNext()) {
      st = iterator.next();
      output += st.getFullCode() + "\n\n";
    }

    // TraceManager.addDev("code class:\n" + output + "\nend code class");

    return output;
  }

  // *************** String generation ******************************* /

  public void generateHeader() {
    header = "#include \"systemc.h\"\n#include \"tml_library.h\"\n";
  }

  public void generateClassBegin() {
    classBegin = "class testbench: public sc_module {\npublic:\nsc_in<bool> clk;\n";
  }

  public void generateReferencesToTMLTasks() {
    referencesToTMLTasks = "// references to each TML tasks (Task_ID)\n";
    referencesToTMLTasks += "TML_tasks ";

    Iterator<SystemCTask> iterator = tasks.listIterator();
    SystemCTask st;
    boolean first = true;

    while (iterator.hasNext()) {
      st = iterator.next();
      if (!first) {
        referencesToTMLTasks += ", ";
      }
      referencesToTMLTasks += st.reference;
      first = false;
    }
    referencesToTMLTasks += ";\n";
  }

  public void generateChannelsDec() {
    channelsDec = "// Communication channels declaration (see channels.h)\n";

    Iterator<TMLChannel> iterator = tmlmodeling.getListIteratorChannels();
    TMLChannel channel;

    while (iterator.hasNext()) {
      channel = iterator.next();
      channelsDec += "channel" + channel.getType() + "<s" + channel.getSize() + "> " + channel.getName() + ";\n";
    }
  }

  public void generateEventsDec() {
    eventsDec = "// Events declaration (for NOTIFY)\n";

    Iterator<TMLEvent> iterator = tmlmodeling.getListIteratorEvents();
    TMLEvent event;

    while (iterator.hasNext()) {
      event = iterator.next();
      eventsDec += "sc_event " + event.getName() + ";\n";
    }
  }

  public void generateRequestsDec() {
    requestsDec = "// Events declaration (for requests)\n";

    Iterator<TMLRequest> iterator = tmlmodeling.getListIteratorRequests();
    TMLRequest request;

    while (iterator.hasNext()) {
      request = iterator.next();
      requestsDec += "sc_event " + request.getName() + ";\n";
    }
  }

  public void generateHasProcess() {
    hasProcess = "SC_HAS_PROCESS(testbench);";
  }

  public void generateTestBench() {
    testBench = "testbench(sc_module_name nm) { \n";

    Iterator<TMLTask> iterator = tmlmodeling.getListIteratorTasks();
    TMLTask task;
    List<TMLRequest> ll;
    Iterator<TMLRequest> itereq;
    TMLRequest req;

    while (iterator.hasNext()) {
      task = iterator.next();
      testBench += "SC_THREAD(" + task.getName() + ");\n";
      ll = tmlmodeling.getRequestsToMe(task);
      if (ll.size() == 0) {
        testBench += "sensitive<<clk;\n";
      } else {
        itereq = ll.listIterator();
        while (itereq.hasNext()) {
          req = itereq.next();
          testBench += "sensitive<<" + req.getName() + ";\n";
        }
      }

      testBench += "dont_initialize();\n\n";
    }

    testBench += "}\n";
  }

  public void generateEndClass() {
    endClass = "};\n";
  }

  public void generateMainFunction() {
    Iterator<SystemCTask> iterator;
    SystemCTask st;
    TMLChannel channel;

    mainFunction = "//************************* MAIN *********************\n\nint sc_main(int, char **) {\n";
    mainFunction += "sc_clock clk(\"clk\", 1);\n\n";
    mainFunction += "testbench tb(\"tb\");\n";
    mainFunction += "tb.clk(clk);\n\n";
    mainFunction += "sc_trace_file *tf = sc_create_vcd_trace_file(\"vcddump\");\n\n";
    mainFunction += "sc_trace(tf, tb.clk, \"CLOCK_TICKS\");\n\n";

    mainFunction += "// Task req tracings\n";
    iterator = tasks.listIterator();
    while (iterator.hasNext()) {
      st = iterator.next();
      mainFunction += "sc_trace(tf, tb." + st.reference + ".active, \"TASK_" + st.reference + "\");\n";
    }

    mainFunction += "\n// Channel RD/WR tracings\n";
    final Iterator<TMLChannel> chanIterator = tmlmodeling.getListIteratorChannels();

    while (chanIterator.hasNext()) {
      channel = chanIterator.next();
      mainFunction += "sc_trace(tf, tb." + channel.getName() + ".wr, \"" + channel.getName() + "_WR\");\n";
      mainFunction += "sc_trace(tf, tb." + channel.getName() + ".rd, \"" + channel.getName() + "_RD\");\n\n";
    }

    mainFunction += "\n// Exec tracings\n";
    iterator = tasks.listIterator();
    while (iterator.hasNext()) {
      st = iterator.next();
      mainFunction += "sc_trace(tf, tb." + st.reference + ".exi, \"EX_" + st.reference + "\");\n";
    }

    mainFunction += "\nsc_start(-1);\n";
    mainFunction += "\nsc_close_vcd_trace_file(tf);\n\n";

    mainFunction += "return 1;\n}\n";

  }

  // *************** Internal structure manipulation
  // ******************************* /

  public void generateSystemCTasks() {
    Iterator<TMLTask> iterator = tmlmodeling.getTasks().listIterator();
    TMLTask t;
    SystemCTask st;

    while (iterator.hasNext()) {
      t = iterator.next();
      st = new SystemCTask(t);
      tasks.add(st);
    }
  }

}
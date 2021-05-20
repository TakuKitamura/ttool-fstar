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

import tmltranslator.TMLCP;
import tmltranslator.TMLCPLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Class DataTransfer for code generation Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class DataTransfer {

  public static final int DMA = 0;
  public static final int DOUBLE_DMA = 1;
  public static final int MEMORY_COPY = 2;
  public static final String TAB = "\t";
  public static final String CR = "\n";

  private int type;
  private String name = "";
  private List<Buffer> inBuffers;
  private Buffer outBuffer;
  private TMLCPLib tmlcplib;
  private TMLCP tmlcp;
  private List<Signal> outSignals;
  private List<Signal> inSignals;

  public DataTransfer(TMLCPLib _tmlcplib, TMLCP _tmlcp, List<Signal> _inSignals, List<Signal> _outSignals) {
    name = _tmlcplib.getName();
    tmlcplib = _tmlcplib;
    tmlcp = _tmlcp;
    inSignals = _inSignals;
    if (_outSignals == null) {
      outSignals = new ArrayList<Signal>();
    } else {
      outSignals = _outSignals;
    }
    init();
  }

  private void init() {
    inBuffers = new ArrayList<Buffer>();
  }

  public TMLCPLib getTMLCPLib() {
    return tmlcplib;
  }

  public String getName() {
    return name.split("::")[0];
  }

  public int getType() {
    return type;
  }

  public List<Signal> getInSignals() {
    return inSignals;
  }

  public List<Signal> getOutSignals() {
    return outSignals;
  }

  /*
   * public void setOutSignal( Signal _outSignal ) { outSignal = _outSignal; }
   */

  public void addOutSignal(Signal _outSignal) {
    outSignals.add(_outSignal);
  }

  public void addInSignal(Signal _inSignal) {
    inSignals.add(_inSignal);
  }

  /*
   * public void setInBuffer( Buffer _inBuffer) { inBuffer = _inBuffer; }
   */

  public void addInBuffer(Buffer _inBuffer) {
    inBuffers.add(_inBuffer);
  }

  public List<Buffer> getInBuffers() {
    return inBuffers;
  }

  public void setOutBuffer(Buffer _outBuffer) {
    outBuffer = _outBuffer;
  }

  public Buffer getOutBuffer() {
    return outBuffer;
  }

  public String getFireRuleCondition() {

    StringBuffer s = new StringBuffer();
    for (Signal sig : inSignals) {
      s.append("( sig[ " + sig.getName() + " ].f ) &&");
    }
    for (Signal sig : outSignals) {
      s.append("( !sig[ " + sig.getName() + " ].f ) &&");
    }
    return s.toString().substring(0, s.length() - 3);
  }

  public String getContextName() {
    return getTMLCPLib().getName().split("::")[0] + "_ctx";
  }

  public String toString() {

    StringBuffer s = new StringBuffer("DATA TRANSFER " + name + "\n\t");
    for (Signal sig : inSignals) {
      s.append("inSignal: " + sig.getName() + "\n\t");
    }
    for (Signal sig : outSignals) {
      s.append("outSignal: " + sig.getName() + "\n\t");
    }
    for (Buffer buff : inBuffers) {
      s.append("inBuffer: " + buff.toString() + "\n\t");
    }
    if (outBuffer != null) {
      s.append("outBuffer: " + outBuffer.toString() + "\n\t");
    }
    if (tmlcp != null) {
      s.append("TMLCP: " + tmlcp.toString());
    }

    return s.toString();
  }

} // End of class

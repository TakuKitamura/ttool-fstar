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

package tmltranslator;

import myutil.TraceManager;
import ui.tmlcompd.TMLCPrimitivePort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class TMLChannel Creation: 22/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 22/11/2005
 */
public class TMLChannel extends TMLCommunicationElement {

  public static final int BRBW = 0;
  public static final int BRNBW = 1;
  public static final int NBRNBW = 2;

  public boolean checkConf;
  public boolean checkAuth;
  private int size; // width of the channel i.e. nb of bytes of each sample
  private int type;
  private int max; // Maximum number of samples

  public TMLPortWithSecurityInformation port;
  // public TMLCPrimitivePort port2;
  public ArrayList<TMLPortWithSecurityInformation> ports;

  // Used on for 1 -> 1 channel
  protected TMLTask originTask, destinationTask;
  protected TMLPort originPort, destinationPort; // Not used by the simulator

  // Used for 1 -> many channel, or for many -> 1 channel
  protected ArrayList<TMLTask> originTasks, destinationTasks;
  protected ArrayList<TMLPort> originPorts, destinationPorts;

  protected int nbOfSamples = 1; // Represent how many samples are read (join) or written (fork) at once

  private String TAB = "\t";
  private String TAB2 = "\t\t";
  private String CR = "\n";
  private String SP = " ";
  private int priority;

  private int vc = 0;

  public ArrayList<TMLTask> originalOriginTasks = new ArrayList<TMLTask>();
  public ArrayList<TMLTask> originalDestinationTasks = new ArrayList<TMLTask>();
  // A reference to the original origin and destination tasks

  public TMLChannel(String name, Object reference) {
    super(name, reference);
    originTasks = new ArrayList<TMLTask>();
    destinationTasks = new ArrayList<TMLTask>();
    originPorts = new ArrayList<TMLPort>();
    destinationPorts = new ArrayList<TMLPort>();
    ports = new ArrayList<TMLPortWithSecurityInformation>();
    checkConf = false;
  }

  public boolean hasDestinationTask(TMLTask t) {
    if (destinationTask == t) {
      return true;
    }

    for (TMLTask task : destinationTasks) {
      if (task == t) {
        return true;
      }
    }

    return false;
  }

  public boolean hasOriginTask(TMLTask t) {
    /*
     * TraceManager.addDev("t=" + t + " origin task=" + originTask); if (originTask
     * != null) { TraceManager.addDev("t=" + t.getName() + "| origin task=" +
     * originTask.getName() + "|"); }
     */
    if (originTask == t) {
      return true;
    }

    for (TMLTask task : originTasks) {
      // TraceManager.addDev("t=" + t + " origins task=" + task);
      if (task == t) {
        return true;
      }
    }

    TraceManager.addDev("Returning false");

    return false;
  }

  public String getNameOfDestinationTasks() {
    if (destinationTask != null) {
      return destinationTask.getName();
    }

    String ret = "";
    for (TMLTask task : destinationTasks) {
      ret += " " + task.getName();
    }
    return ret.trim();
  }

  public TMLTask getOriginTask(int index) {
    return originTasks.get(index);
  }

  public List<String> getTaskNames() {
    List<String> taskNames = new ArrayList<String>();
    if (isBasicChannel()) {
      taskNames.add(destinationTask.getTaskName());
      return taskNames;
    }
    for (TMLTask destTask : destinationTasks) {
      taskNames.add(destTask.getTaskName());
    }
    return taskNames;
  }

  public ArrayList<TMLTask> getAllTasks() {
    ArrayList<TMLTask> allTasks = new ArrayList<>();
    if (originTask != null) {
      allTasks.add(originTask);
    }
    if (destinationTask != null) {
      allTasks.add(destinationTask);
    }
    for (TMLTask task : originTasks) {
      allTasks.add(task);
    }
    for (TMLTask task : destinationTasks) {
      allTasks.add(task);
    }

    return allTasks;

  }

  public void setNumberOfSamples(int nbOfSamples) {
    this.nbOfSamples = nbOfSamples;
  }

  public int getNumberOfSamples() {
    return nbOfSamples;
  }

  public void setVC(int vc) {
    TraceManager.addDev("Setting VC to " + vc + " for channel " + getName());
    this.vc = vc;
  }

  public int getVC() {
    return vc;
  }

  public TMLTask getDestinationTask(int index) {
    return destinationTasks.get(index);
  }

  public TMLPort getOriginPort(int index) {
    return originPorts.get(index);
  }

  public TMLPort getDestinationPort(int index) {
    return destinationPorts.get(index);
  }

  public String getNameOfOriginTasks() {
    if (originTask != null) {
      return originTask.getName();
    }

    String ret = "";
    for (TMLTask task : originTasks) {
      ret += " " + task.getName();
    }
    return ret.trim();
  }

  public int getNbOfDestinationPorts() {
    if (isBasicChannel()) {
      if (destinationPort != null) {
        return 1;
      } else {
        return 0;
      }
    }

    if (destinationPorts == null) {
      return 0;
    }

    return destinationPorts.size();
  }

  public TMLPort hasDestinationPort(String name) {
    // TraceManager.addDev("Searching for dest port=" + name);
    if (destinationPort != null) {
      // TraceManager.addDev("Dest port1=" + destinationPort.getName());
      if (destinationPort.getName().compareTo(name) == 0) {
        // TraceManager.addDev("Found1");
        return destinationPort;
      }
    }

    if (destinationPorts == null) {
      return null;
    }

    for (TMLPort port : destinationPorts) {
      // TraceManager.addDev("Dest portm=" + port.getName());
      if (port.getName().compareTo(name) == 0) {
        // TraceManager.addDev("Foundm");
        return port;
      }
    }
    // TraceManager.addDev("Not found");
    return null;

  }

  public TMLPort hasOriginPort(String name) {
    // TraceManager.addDev("Searching for dest port=" + name);
    if (originPort != null) {
      // TraceManager.addDev("Dest port1=" + destinationPort.getName());
      if (originPort.getName().compareTo(name) == 0) {
        // TraceManager.addDev("Found1");
        return originPort;
      }
    }

    if (originPorts == null) {
      return null;
    }

    for (TMLPort port : originPorts) {
      // TraceManager.addDev("Dest portm=" + port.getName());
      if (port.getName().compareTo(name) == 0) {
        // TraceManager.addDev("Foundm");
        return port;
      }
    }
    // TraceManager.addDev("Not found");
    return null;

  }

  // Complex channels
  public boolean isBasicChannel() {
    return (originTasks.size() == 0);
  }

  /*
   * public void removeComplexInformation() { originTasks = new
   * ArrayList<TMLTask>(); destinationTasks = new ArrayList<TMLTask>();
   * originPorts = new ArrayList<TMLPort>(); destinationPorts = new
   * ArrayList<TMLPort>(); }
   */

  public boolean isBadComplexChannel() {
    if ((originTasks.size() == 1) && (destinationTasks.size() >= 1)) {
      return false;
    }

    return !((destinationTasks.size() == 1) && (originTasks.size() >= 1));
  }

  public boolean isAForkChannel() {
    return ((originTasks.size() == 1) && (destinationTasks.size() >= 1));
  }

  public boolean isAJoinChannel() {
    return ((destinationTasks.size() == 1) && (originTasks.size() >= 1));
  }

  public boolean isAJoinChannel(int nbOfOrigins) {
    return ((destinationTasks.size() == 1) && (originTasks.size() == nbOfOrigins));
  }

  public void toBasicIfPossible() {
    if ((originTasks.size() == 1) && (destinationTasks.size() == 1)) {
      originTask = originTasks.get(0);
      destinationTask = destinationTasks.get(0);
      originPort = originPorts.get(0);
      destinationPort = destinationPorts.get(0);
      originTasks = new ArrayList<TMLTask>();
      destinationTasks = new ArrayList<TMLTask>();
      originPorts = new ArrayList<TMLPort>();
      destinationPorts = new ArrayList<TMLPort>();
    }
  }

  public void addTaskPort(TMLTask _task, TMLPort _port, boolean isOrigin) {
    if (isOrigin) {
      originTasks.add(_task);
      originPorts.add(_port);
    } else {
      destinationTasks.add(_task);
      destinationPorts.add(_port);
    }
  }

  public ArrayList<TMLTask> getOriginTasks() {
    return originTasks;
  }

  public ArrayList<TMLTask> getDestinationTasks() {
    return destinationTasks;
  }

  public ArrayList<TMLPort> getOriginPorts() {
    return originPorts;
  }

  public ArrayList<TMLPort> getDestinationPorts() {
    return destinationPorts;
  }

  public void removeComplexInformations() {
    originTasks = new ArrayList<TMLTask>();
    destinationTasks = new ArrayList<TMLTask>();
    originPorts = new ArrayList<TMLPort>();
    destinationPorts = new ArrayList<TMLPort>();
  }

  // Basic channels
  public void setTasks(TMLTask _origin, TMLTask _destination) {
    originTask = _origin;
    destinationTask = _destination;
  }

  public void setOriginTask(TMLTask t) {
    originTask = t;
  }

  public void setDestinationTask(TMLTask t) {
    destinationTask = t;
  }

  public void setPorts(TMLPort _origin, TMLPort _destination) {
    originPort = _origin;
    destinationPort = _destination;
  }

  public TMLTask getOriginTask() {
    return originTask;
  }

  public TMLTask getDestinationTask() {
    return destinationTask;
  }

  public TMLTask getSystemOriginTask() {
    for (TMLTask task : originTasks) {
      if (!task.isAttacker()) {
        return task;
      }
    }
    return originTask;
  }

  public TMLTask getSystemDestinationTask() {
    for (TMLTask task : destinationTasks) {
      if (!task.isAttacker()) {
        return task;
      }
    }
    return destinationTask;
  }

  public TMLPort getOriginPort() {
    return originPort;
  }

  public TMLPort getDestinationPort() {
    return destinationPort;
  }

  public void setPriority(int _priority) {
    priority = _priority;
  }

  public int getPriority() {
    return priority;
  }

  public void setSize(int _size) {
    size = _size;
  }

  public int getSize() {
    return size;
  }

  public void setMax(int _max) {
    max = _max;
  }

  public int getMax() {
    return max;
  }

  public void setType(int _type) {
    type = _type;
  }

  public void setTypeByName(String _name) {
    if (_name.compareTo("BRBW") == 0) {
      type = BRBW;
    }
    if (_name.compareTo("BRNBW") == 0) {
      type = BRNBW;
    }
    if (_name.compareTo("NBRNBW") == 0) {
      type = NBRNBW;
    }

  }

  public int getType() {
    return type;
  }

  public boolean isInfinite() {
    return (type != 0);
  }

  public String getNameExtension() {
    return "channel__";
  }

  public static String getStringType(int type) {
    switch (type) {
      case BRBW:
        return "BRBW";
      case BRNBW:
        return "BRNBW";
      case NBRNBW:
        return "NBRNBW";
    }
    return "unknown type";
  }

  public boolean isBlockingAtOrigin() {
    switch (type) {
      case BRBW:
        return true;
      case BRNBW:
        return false;
      case NBRNBW:
        return false;
    }
    return false;
  }

  public boolean isBlockingAtDestination() {
    switch (type) {
      case BRBW:
        return true;
      case BRNBW:
        return true;
      case NBRNBW:
        return false;
    }
    return false;
  }

  public String toString() {

    String s = TAB + "CHANNEL" + SP + name + CR;
    if (isBasicChannel()) {
      s += TAB2 + "Origin task: " + originTask.getName() + CR;
      if (originPort != null) {
        s += TAB2 + "Destination port: " + originPort.getName() + CR;
      }
      s += TAB2 + "Destination task: " + destinationTask.getName() + CR;
      if (destinationPort != null) {
        s += TAB2 + "Destination port: " + destinationPort.getName() + CR;
      }
    }
    if (isAForkChannel()) {
      s += TAB2 + "Origin task: " + originTasks.get(0).getName() + CR;
      s += TAB2 + "Origin port: " + originPorts.get(0).getName() + CR;
      for (int i = 0; i < destinationTasks.size(); i++) {
        s += TAB2 + "Destination task: " + destinationTasks.get(i).getName() + CR;
        s += TAB2 + "Destination port: " + destinationPorts.get(i).getName() + CR;
      }
    }
    if (isAJoinChannel()) {
      for (int i = 0; i < originTasks.size(); i++) {
        s += TAB2 + "Origin task: " + originTasks.get(i).getName() + CR;
        s += TAB2 + "Origin port: " + originPorts.get(i).getName() + CR;
      }
      s += TAB2 + "Destination task: " + destinationTasks.get(0).getName() + CR;
      s += TAB2 + "Destination port: " + destinationPorts.get(0).getName() + CR;
    }
    return s;
  }

  public String toXML() {
    // TraceManager.addDev("Channel:" + this.toString());
    String s = "<TMLCHANNEL ";
    s += "name=\"" + name + "\" ";
    if (isBasicChannel()) {
      s += "origintask=\"" + originTask.getName() + "\" ";
      if (originPort != null) {
        s += "originport=\"" + originPort.getName() + "\" ";
      }
      s += "destinationtask=\"" + destinationTask.getName() + "\" ";
      if (destinationPort != null) {
        s += "destinationport=\"" + destinationPort.getName() + "\" ";
      }
      if (originPort != null) {
        s += "dataFlowType=\"" + originPort.getDataFlowType() + "\" ";
      }
    }
    if (isAForkChannel()) {
      s += "origintask=\"" + originTasks.get(0).getName() + "\" ";
      s += "originport=\"" + originPorts.get(0).getName() + "\" ";
      String destTask = "";
      String destPort = "";
      for (int i = 0; i < destinationTasks.size(); i++) {
        destTask += destinationTasks.get(i).getName() + ";";
        destPort += destinationPorts.get(i).getName() + ";";
      }
      s += "destinationtask=\"" + destTask + "\" ";
      s += "destinationport=\"" + destPort + "\" ";
      if (originPort != null) {
        s += "dataFlowType=\"" + originPort.getDataFlowType() + "\" ";
      } else if ((originPorts != null) && (originPorts.size() > 0)) {
        TMLPort tmpP = originPorts.get(0);
        s += "dataFlowType=\"" + tmpP.getDataFlowType() + "\" ";
      }
    }
    if (isAJoinChannel()) {
      s += "destinationtask=\"" + destinationTasks.get(0).getName() + "\" ";
      s += "destinationport=\"" + destinationPorts.get(0).getName() + "\" ";

      // Origin
      String oriTask = "";
      String oriPort = "";
      for (int i = 0; i < originTasks.size(); i++) {
        oriTask += originTasks.get(i).getName() + ";";
        oriPort += originPorts.get(i).getName() + ";";
      }
      s += "origintask=\"" + oriTask + "\" ";
      s += "originport=\"" + oriPort + "\" ";
      s += "dataFlowType=\"" + originPorts.get(0).getDataFlowType() + "\" ";
    }

    s += "isLossy=\"" + isLossy + "\" ";
    s += "lossPercentage=\"" + lossPercentage + "\" ";
    s += "maxNbOfLoss=\"" + maxNbOfLoss + "\" ";
    switch (type) {
      case BRBW:
        s += "type=\"BRBW\" ";
        break;
      case BRNBW:
        s += "type=\"BRNBW\" ";
        break;
      case NBRNBW:
      default:
        s += "type=\"NBRNBW\" ";
    }
    s += "size=\"" + size + "\" ";
    s += "max=\"" + max + "\" ";
    s += "vc=\"" + vc + "\" ";
    s += " />\n";
    return s;
  }

  public boolean equalSpec(Object o) {
    if (!(o instanceof TMLChannel))
      return false;
    if (!super.equalSpec(o))
      return false;
    TMLChannel channel = (TMLChannel) o;
    TMLComparingMethod comp = new TMLComparingMethod();

    if (originPort != null) {
      if (!originPort.equalSpec(channel.getOriginPort()))
        return false;
    }

    if (destinationPort != null) {
      if (!destinationPort.equalSpec(channel.getDestinationPort()))
        return false;
    }

    if (originTask != null) {
      if (!originTask.equalSpec(channel.getOriginTask()))
        return false;
    }

    if (destinationTask != null) {
      if (!destinationTask.equalSpec(channel.getDestinationTask()))
        return false;
    }

    return checkConf == channel.checkConf && checkAuth == channel.checkAuth && size == channel.size
        && type == channel.type && max == channel.max && vc == channel.vc && nbOfSamples == channel.getNumberOfSamples()
        && priority == channel.priority && comp.isTasksListEquals(originTasks, channel.getOriginTasks())
        && comp.isTasksListEquals(destinationTasks, channel.getDestinationTasks())
        && comp.isPortListEquals(originPorts, channel.getOriginPorts())
        && comp.isPortListEquals(destinationPorts, channel.getDestinationPorts());
  }

}

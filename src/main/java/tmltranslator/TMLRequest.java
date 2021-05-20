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

import cli.TML;
import ui.tmlcompd.TMLCPrimitivePort;

import java.util.*;

/**
 * Class TMLRequest Creation: 22/11/2005
 * 
 * @version 1.0 22/11/2005
 * @author Ludovic APVRILLE
 */
public class TMLRequest extends TMLCommunicationElement {

  protected Vector<TMLType> params; // List of various types of parameters
  private List<TMLTask> originTasks; // list of tasks from which request starts
  protected TMLTask destinationTask;

  protected List<String> paramNames;

  // For security verification
  public int confStatus;
  public boolean checkConf;
  public boolean checkAuth;

  public List<TMLCPrimitivePort> ports;

  public TMLRequest(final String name, final Object reference) {
    super(name, reference);

    params = new Vector<TMLType>();
    originTasks = new ArrayList<TMLTask>();
    paramNames = new ArrayList<String>();
    ports = new ArrayList<TMLCPrimitivePort>();
    checkConf = false;
  }

  public int getNbOfParams() {
    return params.size();
  }

  public void addParam(TMLType _type) {
    params.add(_type);
  }

  public void addParamName(String name) {
    paramNames.add(name);
  }

  public TMLType getType(int i) {
    if (i < getNbOfParams()) {
      return params.elementAt(i);
    }

    return null;
  }

  public Vector<TMLType> getParams() {
    return params;
  }

  public String getParam(int i) {
    if (i < paramNames.size()) {
      return paramNames.get(i);
    }

    return "";
  }

  public void setDestinationTask(TMLTask _task) {
    destinationTask = _task;
  }

  public TMLTask getDestinationTask() {
    return destinationTask;
  }

  public void addOriginTask(TMLTask _task) {
    originTasks.add(_task);
  }

  public boolean isAnOriginTask(TMLTask _task) {
    return (originTasks.contains(_task));
  }

  public List<TMLTask> getOriginTasks() {
    return originTasks;
  }

  public String getNameExtension() {
    return "request__";
  }

  public void addParam(String _list) {
    String[] split = _list.split(",");
    TMLType type;

    for (int i = 0; i < split.length; i++) {
      if (TMLType.isAValidType(split[i])) {
        type = new TMLType(TMLType.getType(split[i]));
        addParam(type);
      }
    }
  }

  public boolean isBlockingAtOrigin() {
    return false;
  }

  public boolean isBlockingAtDestination() {
    return true;
  }

  public String toXML() {
    String s = "<TMLREQUEST ";
    s += "name=\"" + name + "\" ";
    s += "destinationtask=\"" + destinationTask.getName() + "\" ";
    s += "isLossy=\"" + isLossy + "\" ";
    s += "lossPercentage=\"" + lossPercentage + "\" ";
    s += "maxNbOfLoss=\"" + maxNbOfLoss + "\" ";
    s += " >\n";

    for (TMLTask ta : originTasks) {
      s += "<ORIGINTASK name=\"" + ta.getName() + "\" /> ";
    }

    for (TMLType t : params) {
      s += "<PARAM type=\"" + t.toString() + "\" />";
    }
    s += "</TMLREQUEST>\n";
    return s;
  }

  public boolean equalSpec(Object o) {
    if (!(o instanceof TMLRequest))
      return false;
    if (!super.equalSpec(o))
      return false;
    TMLRequest request = (TMLRequest) o;
    TMLComparingMethod comp = new TMLComparingMethod();

    if (destinationTask != null) {
      if (!destinationTask.equalSpec(request.getDestinationTask()))
        return false;
    }

    if (!(new HashSet<>(params).equals(new HashSet<>(request.params))))
      return false;

    if (!(new HashSet<>(paramNames).equals(new HashSet<>(request.paramNames))))
      return false;

    return confStatus == request.confStatus && checkConf == request.checkConf && checkAuth == request.checkAuth
        && comp.isTasksListEquals(originTasks, request.getOriginTasks());
  }
}

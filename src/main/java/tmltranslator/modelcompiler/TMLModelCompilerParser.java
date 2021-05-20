/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea ENRICI
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT nokia.com
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

import myutil.TraceManager;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class TMLModelCompilerParser Parse models before compiling them into control
 * code Creation: 20/03/2015
 * 
 * @version 1.0 20/03/2015
 * @author Andrea ENRICI
 */
public class TMLModelCompilerParser {

  public TMLActivityElement element;
  private List<TMLModelCompilerError> errors;
  // private ArrayList<TMLTask> mappedTasks;
  private TMLMapping<?> tmap;
  private TMLModeling<?> tmlm;
  // private TMLArchitecture tmla;
  private List<TMLCPLib> mappedCPLibs;

  public final static int ERROR_STRUCTURE = 0;
  public final static int WARNING_STRUCTURE = 1;
  public final static int ERROR_BEHAVIOR = 2;
  public final static int WARNING_BEHAVIOR = 3;
  public int type; // ERROR, WARNING
  public String message;
  public TMLTask task;

  public TMLModelCompilerParser(TMLMapping<?> _tmap, TMLModeling<?> _tmlm, TMLArchitecture _tmla) {
    // mappedTasks = _mappedTasks;
    errors = new ArrayList<TMLModelCompilerError>();
    tmap = _tmap;
    tmlm = _tmlm;
    // tmla = _tmla;
    mappedCPLibs = _tmap.getMappedTMLCPLibs();
  }

  public void addError(String message, int type) {
    TMLModelCompilerError error = new TMLModelCompilerError(type);
    error.message = message;
    errors.add(error);
  }

  public List<TMLModelCompilerError> getErrors() {
    return errors;
  }

  public boolean hasErrors() {
    return errors.size() > 0;
  }

  public void check() {
    checkForPrexAndPostexChannels();
    checkForCPsAssociatedToForkChannels(); // so far we do not handle CPs associated to ports that are part of a fork
                                           // channel
    checkForXFTasks(); // check that the Operations have been correctly modeled with X and F tasks
    checkMappingOfTasks(); // check that all tasks in the application model are mapped.
  }

  // valid prex ports are:
  // origin port of a basic channel
  // origin port of a fork channel
  // valid postex ports are:
  // destination port of a basic channel
  // destination port of a join channel
  // anything else raises an error
  private void checkForPrexAndPostexChannels() {

    boolean foundPrex = false, foundPostex = false;
    TMLPort originPort = new TMLPort("noName", null);
    TMLPort destinationPort = new TMLPort("noName", null);

    // Fill the the prex and postex lists
    for (TMLChannel ch : tmlm.getChannels()) {
      if (ch.isBasicChannel()) {
        originPort = ch.getOriginPort();
        destinationPort = ch.getDestinationPort();
        if (originPort.isPrex()) {
          if (ch.getOriginTask().getReadChannels().size() > 0) {
            addError("Port " + originPort.getName() + " cannot be marked as prex. Task " + ch.getOriginTask().getName()
                + " has input channels", TMLModelCompilerError.ERROR_STRUCTURE);
          }
          foundPrex = true;
        }
        if (destinationPort.isPostex()) {
          if (ch.getDestinationTask().getWriteChannels().size() > 0) {
            addError(
                "Port " + destinationPort.getName() + " cannot be marked as postex. Task "
                    + ch.getDestinationTask().getName() + " has output channels",
                TMLModelCompilerError.ERROR_STRUCTURE);
          }
          foundPostex = true;
        }
      }
      if (ch.isAForkChannel()) {
        originPort = ch.getOriginPorts().get(0);
        if (originPort.isPrex()) {
          if (ch.getOriginTasks().get(0).getReadChannels().size() > 0) {
            addError("Port " + originPort.getName() + " cannot be marked as prex. Task " + ch.getOriginTask().getName()
                + " has input channels", TMLModelCompilerError.ERROR_STRUCTURE);
          }
          foundPrex = true;
        }
        for (TMLPort port : ch.getDestinationPorts()) { // check all destination ports: they cannot be marked as postex
          if (port.isPostex()) {
            addError("Port " + port.getName() + " belongs to a fork channel: it cannot be marked as postex",
                TMLModelCompilerError.ERROR_STRUCTURE);
          }
        }
      }
      if (ch.isAJoinChannel()) {
        originPort = ch.getOriginPorts().get(0);
        destinationPort = ch.getDestinationPorts().get(0);
        if (destinationPort.isPostex()) {
          if (ch.getDestinationTasks().get(0).getWriteChannels().size() > 0) {
            addError(
                "Port " + destinationPort.getName() + " cannot be marked as postex. Task "
                    + ch.getDestinationTask().getName() + " has output channels",
                TMLModelCompilerError.ERROR_STRUCTURE);
          }
          foundPostex = true;
        }
        for (TMLPort port : ch.getOriginPorts()) { // check all origin ports: they cannot be marked as prex
          if (port.isPrex()) {
            addError("Port " + port.getName() + " belongs to a join channel: it cannot be marked as prex",
                TMLModelCompilerError.ERROR_STRUCTURE);
          }
        }
      }
      if (originPort.isPostex()) {
        addError("Port " + originPort.getName() + " cannot be marked as postex", TMLModelCompilerError.ERROR_STRUCTURE);
      }
      if (destinationPort.isPrex()) {
        addError("Port " + destinationPort.getName() + " cannot be marked as postex",
            TMLModelCompilerError.ERROR_STRUCTURE);
      }
    }
    if (!foundPrex) {
      addError("No suitable channel in the application diagram has been marked as prex",
          TMLModelCompilerError.ERROR_STRUCTURE);
    }
    if (!foundPostex) {
      addError("No suitable channel in the application diagram has been marked as postex",
          TMLModelCompilerError.ERROR_STRUCTURE);
    }
  }

  private void checkForCPsAssociatedToForkChannels() {

    for (TMLCPLib cplib : mappedCPLibs) {
      if (cplib.getArtifacts().size() == 1) {
        String portName = cplib.getArtifacts().get(0).getPortName();
        for (TMLChannel channel : tmlm.getChannels()) {
          if (channel.isAForkChannel()) {
            for (TMLPort port : channel.getDestinationPorts()) {
              if (port.getName().equals(portName)) {
                addError("Port " + portName + " is part of a fork channel. It cannot be mapped to a CP",
                    TMLModelCompilerError.ERROR_STRUCTURE);
              }
            }
          }
        }
      }
    }
  }

  // Check that a Composite component contains only one data-processing Primitive
  // component...TO DO
  private void checkForXFTasks() {

    HashSet<String> xTasksList = new HashSet<String>();
    HashSet<String> fTasksList = new HashSet<String>();

    for (TMLTask task : tmlm.getTasks()) {
      TraceManager.addDev("Task " + task.toString() + " is of " + task.getClass());
      if (task.getTaskName().length() > 2) {
        String name = task.getTaskName();
        String prefix = task.getTaskName().substring(0, 2);
        if (prefix.equals("X_")) { // This is erroneous, there cannot be such a check. The data-processing task is
                                   // defined as the one that is connected to data channels
          xTasksList.add(name.substring(2, name.length()));
        } else if (prefix.equals("F_")) {
          fTasksList.add(name.substring(2, name.length()));
        } else {
          addError("Task " + task.getTaskName() + " has a not a valid name: no X_ or F_ prefix has been detected",
              TMLModelCompilerError.ERROR_STRUCTURE);
        }
      } else {
        addError("Task " + task.getTaskName() + " has a not a valid name (too short)",
            TMLModelCompilerError.ERROR_STRUCTURE);
      }
    }
    if ((xTasksList.size() > 0) && (fTasksList.size() > 0)) {
      for (String name : xTasksList) {
        if (!fTasksList.contains(name)) {
          addError("F task for operation " + name + " has not been instantiated",
              TMLModelCompilerError.ERROR_STRUCTURE);
        }
      }
    }
  }

  private void checkMappingOfTasks() { // check that all tasks in the application have been mapped

    Set<String> mappedTasksList = new HashSet<String>();

    for (TMLTask task : tmap.getMappedTasks()) {
      mappedTasksList.add(task.getTaskName());
    }
    for (TMLTask task : tmlm.getTasks()) {
      if (!mappedTasksList.contains(task.getTaskName())) {
        addError("Task " + task.getTaskName() + " has not been mapped", TMLModelCompilerError.ERROR_STRUCTURE);
      }
    }
  }

} // End of class

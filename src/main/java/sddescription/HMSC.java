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

package sddescription;

import myutil.TraceManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class HMSC Creation: 16/08/2004
 * 
 * @version 1.1 16/08/2004
 * @author Ludovic APVRILLE
 */
public class HMSC {
  private String name;
  private HMSCNode startNode;
  private List<Instance> instances;

  public HMSC(String _name, HMSCNode _startNode) {
    startNode = _startNode;
    name = _name;
    instances = new LinkedList<Instance>();
  }

  public void addInstance(Instance instance) {
    instances.add(instance);
  }

  public Instance getCreateInstanceIfNecessary(String name) {
    Instance ins = getInstance(name);
    if (ins == null) {
      ins = new Instance(name);
      addInstance(ins);
    }
    return ins;
  }

  public Instance getInstance(String name) {
    Iterator<Instance> iterator = instances.listIterator();
    Instance ins;
    while (iterator.hasNext()) {
      ins = iterator.next();
      if (ins.getName().compareTo(name) == 0) {
        return ins;
      }
    }
    return null;
  }

  public List<Instance> getInstances() {
    return instances;
  }

  public String getName() {
    return name;
  }

  public List<HMSCNode> getListOfNodesExceptStartStop() {
    List<HMSCNode> nodes = new LinkedList<HMSCNode>();
    addNodesExceptStartStop(startNode, nodes);
    return nodes;
  }

  public List<HMSCNode> getListOfNodes() {
    List<HMSCNode> nodes = new LinkedList<HMSCNode>();
    addNodes(startNode, nodes);
    return nodes;
  }

  public void addNodesExceptStartStop(HMSCNode n, List<HMSCNode> list) {
    if (n == null) {
      return;
    }
    if (n.getType() == HMSCNode.STOP) {
      return;
    }

    if (n.getType() == HMSCNode.CHOICE) {
      if (list.contains(n)) {
        return;
      }
      list.add(n);
    }

    // recursive call;
    MSC msc;
    HMSCNode n1;
    List<HMSCNode> nodes = n.getNextNodes();
    Iterator<HMSCNode> nodesIterator = nodes.listIterator();

    // direct nodes
    while (nodesIterator.hasNext()) {
      n1 = nodesIterator.next();
      addNodesExceptStartStop(n1, list);
    }

    // nodes after MSCs
    List<MSC> mscs = n.getNextMSCs();
    Iterator<MSC> mscIerator = mscs.listIterator();
    while (mscIerator.hasNext()) {
      msc = mscIerator.next();
      addNodesExceptStartStop(msc.getNextNode(), list);
    }
  }

  public void addNodes(HMSCNode n, List<HMSCNode> list) {
    if (n == null) {
      return;
    }

    if (list.contains(n)) {
      return;
    }

    list.add(n);

    // recursive call;
    MSC msc;
    HMSCNode n1;
    List<HMSCNode> ll = n.getNextNodes();
    Iterator<HMSCNode> iterator1 = ll.listIterator();

    // direct nodes
    while (iterator1.hasNext()) {
      n1 = iterator1.next();
      addNodes(n1, list);
    }

    // nodes after MSCs
    Iterator<MSC> mscsIt = n.getNextMSCs().iterator();
    // iterator1 = ll.listIterator();
    while (mscsIt.hasNext()) {
      msc = mscsIt.next();
      addNodes(msc.getNextNode(), list);
    }
  }

  public List<MSC> getMSCs() {
    List<HMSCNode> ll = getListOfNodes();
    HMSCNode n;
    List<MSC> mscs = new LinkedList<MSC>();
    Iterator<HMSCNode> iterator = ll.listIterator();

    while (iterator.hasNext()) {
      n = iterator.next();
      mscs.addAll(n.getNextMSCs());
    }

    return mscs;
  }

  public void print() {
    // LinkedList ll = getListOfNodes();
    Iterator<HMSCNode> iterator = getListOfNodes().listIterator();
    HMSCNode n;

    while (iterator.hasNext()) {
      n = iterator.next();
      TraceManager.addDev(n.toString());
    }

  }

}

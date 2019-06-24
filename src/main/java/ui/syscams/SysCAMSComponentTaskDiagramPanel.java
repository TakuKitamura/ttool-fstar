/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enst.fr
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

package ui.syscams;

import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;

import java.util.*;

/**
 * Class SysCAMSComponentTaskDiagramPanel 
 * Panel for drawing SystemC-AMS elements
 * Creation: 27/04/2018
 * @version 1.0 27/04/2018
 * @author Irina Kit Yan LEE
 */

@SuppressWarnings("serial")

public class SysCAMSComponentTaskDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

	public SysCAMSComponentTaskDiagramPanel(MainGUI mgui, TToolBar _ttb) {
		super(mgui, _ttb);
	}

	public boolean actionOnDoubleClick(TGComponent tgc) {
		return false;
	}

	public boolean actionOnAdd(TGComponent tgc) {
		return true;
	}

	public boolean actionOnRemove(TGComponent tgc) {
		if (tgc instanceof SysCAMSPortConnector) {
			updatePorts();
		}
		if (tgc instanceof SysCAMSPrimitivePort) {
			updatePorts();
		}
		return true;
	}

	public List<SysCAMSPrimitivePort> getPortsByName(String name) {
		List<SysCAMSPrimitivePort> ports = new ArrayList<SysCAMSPrimitivePort>();
		for (TGComponent tgc : componentList) {

			if (tgc instanceof SysCAMSBlockTDF) {
				SysCAMSBlockTDF comp = (SysCAMSBlockTDF) tgc;
				List<SysCAMSPortTDF> cps = comp.getAllTDFOriginPorts();
				for (SysCAMSPortTDF port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
				cps = comp.getAllTDFDestinationPorts();
				for (SysCAMSPortTDF port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
			}
			if (tgc instanceof SysCAMSBlockDE) {
				SysCAMSBlockDE comp = (SysCAMSBlockDE) tgc;
				List<SysCAMSPortDE> cps = comp.getAllDEOriginPorts();
				for (SysCAMSPortDE port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
				cps = comp.getAllDEDestinationPorts();
				for (SysCAMSPortDE port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
			}
			if (tgc instanceof SysCAMSClock) {
				SysCAMSClock comp = (SysCAMSClock) tgc;
				List<SysCAMSPortDE> cps = comp.getAllDEOriginPorts();
				for (SysCAMSPortDE port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
				cps = comp.getAllDEDestinationPorts();
				for (SysCAMSPortDE port : cps) {
					if (port.commName.equals(name)) {
						ports.add(port);
					}
				}
			}
		}
		return ports;
	}

	public boolean actionOnValueChanged(TGComponent tgc) {
		if (tgc instanceof SysCAMSCompositeComponent) {
			SysCAMSCompositeComponent syscamscc = (SysCAMSCompositeComponent) tgc;
			getMGUI().updateReferenceToSysCAMSCompositeComponent(syscamscc);
		}
		return true;
	}

	public boolean renameBlockTDFComponent(String oldValue, String newValue) {
		return mgui.newSysCAMSComponentTaskName(tp, oldValue, newValue);
	}

	public boolean nameBlockTDFComponentInUse(String oldValue, String newValue) {
		boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
		return ko;
	}

	public boolean nameRecordComponentInUse(String oldValue, String newValue) {
		boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
		return ko;
	}

	public boolean isCompositeNameUsed(String newvalue) {
		for (TGComponent tgc : this.componentList) {
			if (tgc.getValue().equals(newvalue))
				return true;
		}
		return false;
	}

	public SysCAMSCompositeComponent getCompositeComponent() {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				return (SysCAMSCompositeComponent) tgc;
			}
		}
		return null;
	}

	public List<SysCAMSBlockTDF> getBlockTDFComponentList() {
		List<SysCAMSBlockTDF> ll = new LinkedList<SysCAMSBlockTDF>();
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSBlockTDF) {
				ll.add((SysCAMSBlockTDF) tgc);
			}
			if (tgc instanceof SysCAMSCompositeComponent) {
				ll.addAll(((SysCAMSCompositeComponent) tgc).getAllBlockTDFComponents());
			}
		}
		return ll;
	}

	public List<SysCAMSBlockDE> getBlockDEComponentList() {
		List<SysCAMSBlockDE> ll = new LinkedList<SysCAMSBlockDE>();
		TGComponent tgc;

		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSBlockDE) {
				ll.add((SysCAMSBlockDE) tgc);
			}
			// if (tgc instanceof SysCAMSCompositeComponent) {
			// ll.addAll(((SysCAMSCompositeComponent)tgc).getAllBlockDEComponents());
			// }
			// if (tgc instanceof SysCAMSRemoteCompositeComponent) {
			// ll.addAll(((SysCAMSRemoteCompositeComponent)tgc).getAllBlockDEComponents());
			// }
		}
		return ll;
	}

	public List<SysCAMSPrimitivePort> getPortsConnectedTo(SysCAMSPrimitivePort _port,
			List<? extends TGComponent> componentsToTakeIntoAccount) {
		List<TGComponent> ll;
		List<SysCAMSPrimitivePort> ret = new LinkedList<SysCAMSPrimitivePort>();
		Object o;
		SysCAMSPrimitivePort p;

		ll = getAllPortsConnectedTo(_port);
		Iterator<TGComponent> li = ll.listIterator();

		while (li.hasNext()) {
			o = li.next();

			if (o instanceof SysCAMSPrimitivePort) {
				p = (SysCAMSPrimitivePort) o;

				if (p.getFather() instanceof SysCAMSBlockTDF) {
					if (componentsToTakeIntoAccount.contains(p.getFather())) {
						ret.add(p);
					}
				}
				if (p.getFather() instanceof SysCAMSBlockDE) {
					if (componentsToTakeIntoAccount.contains(p.getFather())) {
						ret.add(p);
					}
				}
			}
		}
		return ret;
	}

	public List<TGComponent> getAllPortsConnectedTo(SysCAMSPrimitivePort _port) {
		List<TGComponent> ll = new LinkedList<TGComponent>();
		getAllPortsConnectedTo(ll, _port);
		return ll;
	}

	public void getAllPortsConnectedTo(List<TGComponent> ll, SysCAMSPrimitivePort _port) {
		List<TGComponent> components = getMGUI().getAllSysCAMSComponents();
		Iterator<TGComponent> iterator = components.listIterator();
		TGComponent tgc, tgc1, tgc2;
		SysCAMSPortConnector portco;

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSPortConnector) {
				portco = (SysCAMSPortConnector) tgc;
				tgc1 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP1());
				tgc2 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP2());
				if ((tgc1 != null) && (tgc2 != null)) {
					if ((!ll.contains(tgc2) && (tgc2 != _port) && ((tgc1 == _port) || (ll.contains(tgc1))))) {
						ll.add(tgc2);
						iterator = components.listIterator();
					} else {
						if ((!ll.contains(tgc1) && (tgc1 != _port) && ((tgc2 == _port) || (ll.contains(tgc2))))) {
							ll.add(tgc1);
							iterator = components.listIterator();
						}
					}
				}
			}
		}
	}

	public String getXMLHead() {
		return "<SysCAMSComponentTaskDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + zoomParam()
				+ " >";
	}

	public String getXMLTail() {
		return "</SysCAMSComponentTaskDiagramPanel>";
	}

	public String getXMLSelectedHead() {
		return "<SysCAMSComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel
				+ "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
	}

	public String getXMLSelectedTail() {
		return "</SysCAMSComponentTaskDiagramPanelCopy>";
	}

	public String getXMLCloneHead() {
		return "<SysCAMSComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0
				+ "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
	}

	public String getXMLCloneTail() {
		return "</SysCAMSComponentTaskDiagramPanelCopy>";
	}

	public boolean areAttributesVisible() {
		return attributesVisible;
	}

	public boolean areChannelVisible() {
		return synchroVisible;
	}

	public void setAttributesVisible(boolean b) {
		attributesVisible = b;
	}

	public void setChannelVisible(boolean b) {
		channelVisible = b;
	}

	public String displayParam() {
		String s = "";
		if (channelsVisible) {
			s += " TDF=\"true\"";
		} else {
			s += " TDF=\"false\"";
		}
		if (eventsVisible) {
			s += " DE=\"true\"";
		} else {
			s += " DE=\"false\"";
		}
		return s;
	}

	public ArrayList<String> getAllCompositeComponent(String _name) {
		ArrayList<String> list = new ArrayList<String>();
		TGComponent tgc1;
		String s;
		SysCAMSCompositeComponent syscamscc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc1 = iterator.next();

			if (tgc1 instanceof SysCAMSCompositeComponent) {
				syscamscc = (SysCAMSCompositeComponent) tgc1;
				s = _name + "::" + syscamscc.getValue();
				list.add(s);
				syscamscc.getAllCompositeComponents(list, _name);
			}
		}
		return list;
	}

	public String[] getCompOutTDF() {
		List<String> chls = new ArrayList<String>();
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockTDF) {
				SysCAMSBlockTDF comp = (SysCAMSBlockTDF) tgc;
				List<SysCAMSPortTDF> ll = comp.getAllTDFOriginPorts();
				Iterator<SysCAMSPortTDF> ite = ll.listIterator();
				while (ite.hasNext()) {
					SysCAMSPortTDF port = ite.next();
					chls.add(port.getPortName());
				}
			}
		}
		String[] chlArray = new String[chls.size()];
		chlArray = chls.toArray(chlArray);
		return chlArray;
	}

	public String[] getCompInTDF() {
		List<String> chls = new ArrayList<String>();
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockTDF) {
				SysCAMSBlockTDF comp = (SysCAMSBlockTDF) tgc;
				List<SysCAMSPortTDF> ll = comp.getAllTDFDestinationPorts();
				Iterator<SysCAMSPortTDF> ite = ll.listIterator();
				while (ite.hasNext()) {
					SysCAMSPortTDF port = ite.next();
					chls.add(port.getPortName());
				}
			}
		}
		String[] chlArray = new String[chls.size()];
		chlArray = chls.toArray(chlArray);
		return chlArray;
	}

	public String[] getCompOutDE() {
		List<String> chls = new ArrayList<String>();
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockDE) {
				SysCAMSBlockDE comp = (SysCAMSBlockDE) tgc;
				List<SysCAMSPortDE> ll = comp.getAllDEOriginPorts();
				Iterator<SysCAMSPortDE> ite = ll.listIterator();
				while (ite.hasNext()) {
					SysCAMSPortDE port = ite.next();
					chls.add(port.getPortName());
				}
			}
		}
		String[] chlArray = new String[chls.size()];
		chlArray = chls.toArray(chlArray);
		return chlArray;
	}

	public String[] getCompInDE() {
		List<String> chls = new ArrayList<String>();
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockDE) {
				SysCAMSBlockDE comp = (SysCAMSBlockDE) tgc;
				List<SysCAMSPortDE> ll = comp.getAllDEDestinationPorts();
				Iterator<SysCAMSPortDE> ite = ll.listIterator();
				while (ite.hasNext()) {
					SysCAMSPortDE port = ite.next();
					chls.add(port.getPortName());
				}
			}
		}
		String[] chlArray = new String[chls.size()];
		chlArray = chls.toArray(chlArray);
		return chlArray;
	}

	public SysCAMSBlockTDF getBlockTDFComponentByName(String _name) {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();
		SysCAMSBlockTDF tmp;

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockTDF) {
				if (tgc.getValue().equals(_name)) {
					return ((SysCAMSBlockTDF) tgc);
				}
			}
			if (tgc instanceof SysCAMSCompositeComponent) {
				tmp = ((SysCAMSCompositeComponent) tgc).getBlockTDFComponentByName(_name);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		return null;
	}

	public SysCAMSBlockDE getBlockDEComponentByName(String _name) {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSBlockDE) {
				if (tgc.getValue().equals(_name)) {
					return ((SysCAMSBlockDE) tgc);
				}
			}
		}
		return null;
	}

    public SysCAMSClock getClockComponentByName(String _name) {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSClock) {
				if (tgc.getValue().equals(_name)) {
					return ((SysCAMSClock) tgc);
				}
			}
		}
		return null;
	}
    
	public void updateReferenceToSysCAMSCompositeComponent(SysCAMSCompositeComponent syscamscc) {
		Iterator<TGComponent> iterator = componentList.listIterator();
		TGComponent tgc;

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				((SysCAMSCompositeComponent) tgc).updateReferenceToSysCAMSCompositeComponent(syscamscc);
			}
		}
	}

	public SysCAMSCompositeComponent getCompositeComponentByName(String _name) {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();
		SysCAMSCompositeComponent tmp;

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				tmp = (SysCAMSCompositeComponent) tgc;
				if (tmp.getValue().equals(_name)) {
					return tmp;
				}

				if ((tmp = tmp.getCompositeComponentByName(name)) != null) {
					return tmp;
				}
			}
		}
		return null;
	}

	public void loadExtraParameters(Element elt) {
	}

	public void setConnectorsToFront() {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();
		List<TGComponent> list = new ArrayList<TGComponent>();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		for (TGComponent tgc1 : list) {
			componentList.remove(tgc1);
			componentList.add(tgc1);
		}
	}

	public void delayedLoad() {
		Iterator<TGComponent> iterator;
		TGComponent tgc;

		iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				((SysCAMSCompositeComponent) (tgc)).delayedLoad();
			}
		}
	}

	// Returns the faulty paths
	public ArrayList<SysCAMSPath> updatePorts() {
		List<SysCAMSPath> paths = makePaths();
		ArrayList<SysCAMSPath> faultyPaths = new ArrayList<SysCAMSPath>();

		// Checking rules of paths, and setting colors accordingly
		for (SysCAMSPath path : paths) {
			path.checkRules();
			if (path.hasError()) {
				TraceManager.addDev("Path error:" + path.getErrorMessage());
				faultyPaths.add(path);
			}
		}
		return faultyPaths;
	}

	public void updatePorts_oldVersion() {
		Iterator<TGComponent> iterator;
		TGComponent tgc;

		// Get all SysCAMSPrimitivePort
		List<SysCAMSPortTDF> tdfports = new ArrayList<SysCAMSPortTDF>();
		List<SysCAMSPortDE> deports = new ArrayList<SysCAMSPortDE>();

		iterator = componentList.listIterator();

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSBlockTDF) {
				tdfports.addAll(((SysCAMSBlockTDF) tgc).getAllInternalPortsTDF());
			}
			if (tgc instanceof SysCAMSBlockDE) {
				deports.addAll(((SysCAMSBlockDE) tgc).getAllInternalPortsDE());
			}
			if (tgc instanceof SysCAMSClock) {
				deports.addAll(((SysCAMSClock) tgc).getAllInternalPortsDE());
			}
			if (tgc instanceof SysCAMSPortTDF) {
				tdfports.add((SysCAMSPortTDF) tgc);
			}
			if (tgc instanceof SysCAMSPortDE) {
				deports.add((SysCAMSPortDE) tgc);
			}			
		}

		// We take each primitive ports individually and we go thru the graph
		TGConnector connector;
		TGConnectingPoint tp;
		String conflictMessage;

		for (SysCAMSPortTDF pport : tdfports) {
			for (int i = 0; i < pport.getNbConnectingPoint(); i++) {
				tp = pport.getTGConnectingPointAtIndex(i);
				connector = findTGConnectorUsing(tp);
				if (connector != null) {
					conflictMessage = propagate(pport, tp, connector);
					TraceManager.addDev("Conflict=" + conflictMessage);
				}
			}
		}
		for (SysCAMSPortDE pport : deports) {
			for (int i = 0; i < pport.getNbConnectingPoint(); i++) {
				tp = pport.getTGConnectingPointAtIndex(i);
				connector = findTGConnectorUsing(tp);
				if (connector != null) {
					conflictMessage = propagate(pport, tp, connector);
					TraceManager.addDev("Conflict=" + conflictMessage);
				}
			}
		}
	}

	public String propagate(SysCAMSPrimitivePort pport, TGConnectingPoint tp, TGConnector connector) {
		TGConnectingPoint tp2;
		String conflictMessage = null;

		if (tp == connector.getTGConnectingPointP1()) {
			tp2 = connector.getTGConnectingPointP2();
		} else {
			tp2 = connector.getTGConnectingPointP1();
		}

		TGComponent tgc = (TGComponent) (tp2.getFather());

		if (tgc instanceof SysCAMSPrimitivePort) {
			return conflictMessage;
		}

		return conflictMessage;
	}

	public List<String> getAllSysCAMSTaskNames(String _topname) {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();
		List<String> list = new ArrayList<String>();
		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSBlockTDF) {
				list.add(_topname + "::" + tgc.getValue());
			}
			if (tgc instanceof SysCAMSBlockDE) {
				list.add(_topname + "::" + tgc.getValue());
			}
		}
		return list;
	}

	public Vector<String> getAllSysCAMSTasksAttributes() {
		TGComponent tgc;
		Iterator<TGComponent> iterator = componentList.listIterator();
		Vector<String> list = new Vector<String>();

		while (iterator.hasNext()) {
			tgc = iterator.next();
			if (tgc instanceof SysCAMSCompositeComponent) {
				for (SysCAMSBlockTDF primComp : ((SysCAMSCompositeComponent) tgc).getAllBlockTDFComponents()) {
					Object o = primComp.getPeriod();
					String s = o.toString();
					list.add(primComp.getValue() + "." + s.substring(2, s.length() - 1));
				}
			}
		}
		return list;
	}

	public void findAllReferencedPanels(List<SysCAMSComponentTaskDiagramPanel> panels) {
		if (panels.contains(this)) {
			return;
		}
		panels.add(this);

		Iterator<TGComponent> iterator = componentList.listIterator();
		TGComponent tgc;

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				// We must find all panels referencing this component
				panels.addAll(mgui.getAllPanelsReferencingSysCAMSCompositeComponent((SysCAMSCompositeComponent) tgc));
			}
		}
	}

	public List<SysCAMSPath> makePaths() {
		List<SysCAMSComponentTaskDiagramPanel> panels = new ArrayList<SysCAMSComponentTaskDiagramPanel>();

		// We first find all the implicated panels
		findAllReferencedPanels(panels);

		List<SysCAMSPath> paths = new ArrayList<SysCAMSPath>();
		Iterator<TGComponent> iterator;
		TGComponent tgc;

		// Go through the component list of all panels, and make paths. Then, go thru
		// connectors,
		// and merge paths until nomore merging is possible
		for (TDiagramPanel panel : panels) {
			iterator = panel.getComponentList().listIterator();
			List<SysCAMSPortTDF> listtdf;
			List<SysCAMSPortDE> listde;

			while (iterator.hasNext()) {
				tgc = iterator.next();

				if (tgc instanceof SysCAMSCompositeComponent) {
					listtdf = ((SysCAMSCompositeComponent) tgc).getAllInternalPortsTDF();
					for (SysCAMSPortTDF pp : listtdf) {
						addToPaths(paths, pp);
					}
				}
				if (tgc instanceof SysCAMSBlockTDF) {
					listtdf = ((SysCAMSBlockTDF) tgc).getAllInternalPortsTDF();
					for (SysCAMSPrimitivePort pp : listtdf) {
						addToPaths(paths, pp);
					}
				}
				if (tgc instanceof SysCAMSBlockDE) {
					listde = ((SysCAMSBlockDE) tgc).getAllInternalPortsDE();
					for (SysCAMSPrimitivePort pp : listde) {
						addToPaths(paths, pp);
					}
				}
				if (tgc instanceof SysCAMSPrimitivePort) {
					addToPaths(paths, tgc);
				}
			}
		}

		// Use connectors to merge paths with one another
		for (TDiagramPanel panel : panels) {
			iterator = panel.getComponentList().listIterator();
			SysCAMSPortConnector connector;
			TGComponent tgc1, tgc2;
			SysCAMSPath path1, path2;

			while (iterator.hasNext()) {
				tgc = iterator.next();
				if (tgc instanceof SysCAMSPortConnector) {
					connector = (SysCAMSPortConnector) tgc;
					if (connector.getTGConnectingPointP1().getFather() instanceof TGComponent) {
						tgc1 = (TGComponent) (connector.getTGConnectingPointP1().getFather());
					} else {
						tgc1 = null;
					}
					if (connector.getTGConnectingPointP2().getFather() instanceof TGComponent) {
						tgc2 = (TGComponent) (connector.getTGConnectingPointP2().getFather());
					} else {
						tgc2 = null;
					}
					if ((tgc1 != null) && (tgc2 != null) && (tgc1 != tgc2)) {
						path1 = getPathOf(paths, tgc1);
						path2 = getPathOf(paths, tgc2);
						if ((path1 != null) && (path2 != null)) {
							// Not in the same path -> we must do a merging
							// and then we remove path2 from path
							if (path1 != path2) {
								path1.mergeWith(path2);
								paths.remove(path2);
							}
						}
					} else {
						// If there is a null component in the path, then, we must set an error in the
						// path
						if ((tgc1 == null) && (tgc2 != null)) {
							path2 = getPathOf(paths, tgc2);
							path2.setErrorOfConnection(true);

						}
						if ((tgc2 == null) && (tgc1 != null)) {
							path1 = getPathOf(paths, tgc1);
							path1.setErrorOfConnection(true);
						}
					}
				}
			}
		}
		return paths;
	}

	public SysCAMSPath getPathOf(List<SysCAMSPath> paths, TGComponent tgc) {
		for (SysCAMSPath path : paths) {
			if (path.contains(tgc)) {
				return path;
			}
		}
		return null;
	}

	public void addToPaths(List<SysCAMSPath> paths, TGComponent tgc) {
		for (SysCAMSPath path : paths) {
			if (path.contains(tgc)) {
				return;
			}
		}
		// Create a new path
		SysCAMSPath ph = new SysCAMSPath();
		ph.addComponent(tgc);
		paths.add(ph);
	}

	public void getPanelsUsingAComponent(SysCAMSCompositeComponent syscamscc,
			ArrayList<SysCAMSComponentTaskDiagramPanel> panels) {
		Iterator<TGComponent> iterator = componentList.listIterator();
		TGComponent tgc;

		while (iterator.hasNext()) {
			tgc = iterator.next();

			if (tgc instanceof SysCAMSCompositeComponent) {
				if (((SysCAMSCompositeComponent) tgc).hasRefencesTo(syscamscc)) {
					panels.add(this);
					return;
				}
			}
		}
	}

	public String[] getAllOutDE(String nameOfComponent) {
		SysCAMSBlockDE comp = getBlockDEComponentByName(nameOfComponent);
		if (comp == null) {
			return null;
		}
		List<SysCAMSPortDE> ll = comp.getAllDEOriginPorts();
		String[] terms = new String[ll.size()];
		Iterator<SysCAMSPortDE> ite = ll.listIterator();
		int i = 0;

		while (ite.hasNext()) {
			SysCAMSPortDE port = ite.next();
			terms[i] = port.getPortName();
			i++;
		}
		return terms;
	}

	public String[] getAllInDE(String nameOfComponent) {
		SysCAMSBlockDE comp = getBlockDEComponentByName(nameOfComponent);
		if (comp == null) {
			return null;
		}
		List<SysCAMSPortDE> ll = comp.getAllDEDestinationPorts();
		String[] terms = new String[ll.size()];
		ListIterator<SysCAMSPortDE> ite = ll.listIterator();
		int i = 0;
		while (ite.hasNext()) {
			SysCAMSPortDE port = ite.next();
			terms[i] = port.getPortName();
			i++;
		}
		return terms;
	}

	public String[] getAllOutTDF(String nameOfComponent) {
		SysCAMSBlockTDF comp = getBlockTDFComponentByName(nameOfComponent);
		if (comp == null) {
			return null;
		}
		List<SysCAMSPortTDF> ll = comp.getAllTDFOriginPorts();
		String[] terms = new String[ll.size()];
		Iterator<SysCAMSPortTDF> ite = ll.listIterator();
		int i = 0;
		while (ite.hasNext()) {
			SysCAMSPortTDF port = ite.next();
			terms[i] = port.getPortName();
			i++;
		}
		return terms;
	}

	public String[] getAllInTDF(String nameOfComponent) {
		SysCAMSBlockTDF comp = getBlockTDFComponentByName(nameOfComponent);
		if (comp == null) {
			return null;
		}

		List<SysCAMSPortTDF> ll = comp.getAllTDFDestinationPorts();
		String[] terms = new String[ll.size()];
		Iterator<SysCAMSPortTDF> ite = ll.listIterator();
		int i = 0;

		while (ite.hasNext()) {
			SysCAMSPortTDF port = ite.next();
			terms[i] = port.getPortName();
			i++;
		}
		return terms;
	}
}

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

package ui.tmlcompd;

import myutil.TraceManager;
import org.w3c.dom.Element;
import ui.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.util.*;

/**
 * Class TMLComponentTaskDiagramPanel
 * Panel for drawing TML component-tasks
 * Creation: 10/03/2008
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.0 10/03/2008
 */
public class TMLComponentTaskDiagramPanel extends TDiagramPanel implements TDPWithAttributes {

    public TMLComponentTaskDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
          addMouseListener(tdmm);
          addMouseMotionListener(tdmm);*/
        //setAttributesVisible(true);
    }

    @Override
    public boolean actionOnDoubleClick(TGComponent tgc) {
        /*TraceManager.addDev("Action on double click!");
          if (tgc instanceof TMLCPrimitiveComponent) {
          TraceManager.addDev("Action on double click primitive");
          TMLCPrimitiveComponent t = (TMLCPrimitiveComponent)tgc;
          return (t.oldValue.compareTo(t.getValue()) == 0);
          }*/

        return false;
    }

    @Override
    public boolean actionOnAdd(TGComponent tgc) {
        //TraceManager.addDev("Action on add! value=" + tgc.getValue());
        if (tgc instanceof TMLCPrimitiveComponent) {
            TMLCPrimitiveComponent tmcpc = (TMLCPrimitiveComponent) (tgc);
            //TraceManager.addDev(" *** Add component *** name=" + tmcpc.getValue());
            mgui.addTMLCPrimitiveComponent(tp, tmcpc.getValue());
            return true;
        } /*else if (tgc instanceof TMLCPortConnector) {
            TraceManager.addDev("Bringing to front");
            bringToFront(tgc);
            }*/
        //updatePorts();
        if (tgc instanceof TMLCCompositePort) {
            if (tgc.getFather() instanceof TMLCCompositeComponent) {
                getMGUI().updateReferenceToTMLCCompositeComponent((TMLCCompositeComponent) (tgc.getFather()));
            }
        }

        return true;
    }

    @Override
    public boolean actionOnRemove(TGComponent tgc) {
        //TraceManager.addDev("Action on remove tgc=" + tgc + " value=" + tgc.getValue());
        if (tgc instanceof TMLCPrimitiveComponent) {
            TMLCPrimitiveComponent tmcpc = (TMLCPrimitiveComponent) (tgc);
            mgui.removeTMLCPrimitiveComponent(tp, tmcpc.getValue());
            //resetAllInstancesOf(tgcc);
            return true;
        }

        if (tgc instanceof TMLCCompositeComponent) {
            TMLCCompositeComponent tmcc = (TMLCCompositeComponent) (tgc);
            Iterator<TMLCPrimitiveComponent> iterator = tmcc.getAllPrimitiveComponents().listIterator();
            TMLCPrimitiveComponent tmcpc;

            while (iterator.hasNext()) {
                tmcpc = iterator.next();
                mgui.removeTMLCPrimitiveComponent(tp, tmcpc.getValue());
            }
            //resetAllInstancesOf(tgcc);
            return true;
        }

        if (tgc instanceof TMLCPortConnector) {
            updatePorts();
        }
        if (tgc instanceof TMLCPrimitivePort) {
            updatePorts();
        }

        if (tgc instanceof TMLCChannelFacility) {
            updatePorts();
        }

        if (tgc instanceof TMLCCompositePort) {
            //TraceManager.addDev("tgc.getFather() = " + tgc.getFather());
            updatePorts();
            //TraceManager.addDev("fatherOfRemoved = " + fatherOfRemoved);
            if (fatherOfRemoved instanceof TMLCCompositeComponent) {
                getMGUI().updateReferenceToTMLCCompositeComponent((TMLCCompositeComponent) (fatherOfRemoved));
                //TraceManager.addDev("Shall do the update");
            }
        }

        return true;
    }

    public List<TMLCPrimitivePort> getPortsByName(String name) {
        List<TMLCPrimitivePort> ports = new ArrayList<TMLCPrimitivePort>();
        for (TGComponent tgc : componentList) {

            if (tgc instanceof TMLCPrimitiveComponent) {
                TMLCPrimitiveComponent comp = (TMLCPrimitiveComponent) tgc;
                List<TMLCPrimitivePort> cps = comp.getAllChannelsOriginPorts();
                for (TMLCPrimitivePort port : cps) {
                    if (port.commName.equals(name)) {
                        ports.add(port);
                    }
                }
                cps = comp.getAllChannelsDestinationPorts();
                for (TMLCPrimitivePort port : cps) {
                    if (port.commName.equals(name)) {
                        ports.add(port);
                    }
                }
            }
        }
        return ports;
    }

    @Override
    public boolean actionOnValueChanged(TGComponent tgc) {
        //TraceManager.addDev("Action on value changed on component:" + tgc);
        if (tgc instanceof TMLCPrimitiveComponent) {
            TMLCPrimitiveComponent t = (TMLCPrimitiveComponent) tgc;
            //TraceManager.addDev("Adding new name in mgui oldvalue=" + t.oldValue + " new value=" + t.getValue());
            mgui.newTMLTaskName(tp, t.oldValue, t.getValue());
            //TraceManager.addDev("Done adding new name in mgui");
            return true;
        }
        if (tgc instanceof TMLCCompositeComponent) {
            TMLCCompositeComponent tmlcc = (TMLCCompositeComponent) tgc;
            getMGUI().updateReferenceToTMLCCompositeComponent(tmlcc);
        }
        return true;
    }

    public boolean renamePrimitiveComponent(String oldValue, String newValue) {
        return mgui.newTMLComponentTaskName(tp, oldValue, newValue);
    }

    public boolean namePrimitiveComponentInUse(String oldValue, String newValue) {
        boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
        return ko ? ko : nameAllRecordComponentInUse(oldValue, newValue);
    }

    public boolean nameRecordComponentInUse(String oldValue, String newValue) {
        boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
        return ko ? ko : nameAllRecordComponentInUse(oldValue, newValue);
    }

    public boolean nameAllRecordComponentInUse(String oldValue, String newValue) {
        //   LinkedList ll = getRecordComponentList();
        Iterator<TMLCRecordComponent> iterator = getRecordComponentList().listIterator();
        TMLCRecordComponent record;

        while (iterator.hasNext()) {
            record = iterator.next();

            if (record.getName().compareTo(newValue) == 0) {
                return true;
            }
        }

        return false;
    }

    /* method created for checking valid port name
     * author : minh hiep
     * */
    public boolean namePrimitivePortInUse(TMLCPrimitivePort tmlcpp, String newName) {
        List<TMLCPrimitiveComponent> pcompList = getPrimitiveComponentList();
        List<TMLCPrimitivePort> originPPorts = new ArrayList<>();
        List<TMLCPrimitivePort> destinationPPorts = new ArrayList<>();

        for (TMLCPrimitiveComponent tmlcpc : pcompList) {
            originPPorts.addAll(tmlcpc.getAllChannelsOriginPorts());
            originPPorts.addAll(tmlcpc.getAllEventsOriginPorts());
            originPPorts.addAll(tmlcpc.getAllRequestsOriginPorts());
        }

        for (TMLCPrimitiveComponent tmlcpc : pcompList) {
            destinationPPorts.addAll(tmlcpc.getAllChannelsDestinationPorts());
            destinationPPorts.addAll(tmlcpc.getAllEventsDestinationPorts());
            destinationPPorts.addAll(tmlcpc.getAllRequestsDestinationPorts());
        }

        if (tmlcpp.isOrigin) {
            for (TMLCPrimitivePort op : originPPorts) {
                if (op.getPortName().equalsIgnoreCase(newName)) {
                    return true;
                }
            }
        } else {
            if (tmlcpp.getPortType() != 2) {
                for (TMLCPrimitivePort dp : destinationPPorts) {
                    if (dp.getPortName().equalsIgnoreCase(newName)) {
                        return true;
                    }
                }
            } else {
                for (TMLCPrimitivePort dp : destinationPPorts) {
                    if ((dp.getPortName().equalsIgnoreCase(newName)) && (dp.getFather() != tmlcpp.getFather()))  {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public List<TMLCRecordComponent> getRecordComponentList() {
        List<TMLCRecordComponent> ll = new LinkedList<TMLCRecordComponent>();
        TGComponent tgc;

        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCRecordComponent) {
                ll.add((TMLCRecordComponent) tgc);
            }

            if (tgc instanceof TMLCCompositeComponent) {
                ll.addAll(((TMLCCompositeComponent) tgc).getAllRecordComponents());
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                ll.addAll(((TMLCRemoteCompositeComponent) tgc).getAllRecordComponents());
            }
        }

        return ll;
    }

    public List<TMLPragma> getPragmaList() {
        List<TMLPragma> ll = new LinkedList<TMLPragma>();
        TGComponent tgc;

        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLPragma) {
                ll.add((TMLPragma) tgc);
            }
        }

        return ll;
    }

    public List<TMLCPrimitiveComponent> getPrimitiveComponentList() {
        List<TMLCPrimitiveComponent> ll = new LinkedList<TMLCPrimitiveComponent>();
        TGComponent tgc;

        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPrimitiveComponent) {
                ll.add((TMLCPrimitiveComponent) tgc);
            }

            if (tgc instanceof TMLCCompositeComponent) {
                ll.addAll(((TMLCCompositeComponent) tgc).getAllPrimitiveComponents());
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                ll.addAll(((TMLCRemoteCompositeComponent) tgc).getAllPrimitiveComponents());
            }
        }

        return ll;
    }

    public List<TMLCPrimitivePort> getPortsConnectedTo(TMLCPrimitivePort _port, List<? extends TGComponent> componentsToTakeIntoAccount) {
        List<TGComponent> ll;
        List<TMLCPrimitivePort> ret = new LinkedList<TMLCPrimitivePort>();
        Object o;
        TMLCPrimitivePort p;

        ll = getAllPortsConnectedTo(_port);
        Iterator<TGComponent> li = ll.listIterator();

        while (li.hasNext()) {
            o = li.next();

            if (o instanceof TMLCPrimitivePort) {
                p = (TMLCPrimitivePort) o;

                //   if (p.getFather() != null) {
                if (p.getFather() instanceof TMLCPrimitiveComponent) {
                    if (componentsToTakeIntoAccount.contains(p.getFather())) {
                        //TraceManager.addDev("-> port " + _port + " is connected to " + o);
                        ret.add(p);
                    }
                }
                //    }
            }
        }

        return ret;
    }

    public List<TGComponent> getAllPortsConnectedTo(TMLCPrimitivePort _port) {
        List<TGComponent> ll = new LinkedList<TGComponent>();
        getAllPortsConnectedTo(ll, _port);

        return ll;
    }

    public List<String> getAllTMLCommunicationNames(String _topname) {
        List<String> al = new ArrayList<String>();

        // TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        List<TMLCPrimitiveComponent> components = getPrimitiveComponentList();
        Iterator<TMLCPrimitiveComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name, name1, name2;
        TMLCPrimitivePort port1, port2;

        int j;

        //TraceManager.addDev("*** Adding channels ***");

        while (iterator.hasNext()) {
            //            tgc = iterator.next();
            //            if( tgc instanceof TMLCPrimitiveComponent ) {
            tmlc = iterator.next();
            //TraceManager.addDev("Component:" + tmlc.getValue());
            ports = tmlc.getAllChannelsOriginPorts();
            //TraceManager.addDev("Ports size:" + ports.size());
            li = ports.listIterator();
            while (li.hasNext()) {
                port1 = li.next();
                portstome = getPortsConnectedTo(port1, components);
                //TraceManager.addDev("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());

                Iterator<TMLCPrimitivePort> ite = portstome.listIterator();

                while (ite.hasNext()) {
                    //if ( portstome.size() == 1 ) {
                    //TraceManager.addDev("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
                    port2 = ite.next();
                    if (!port2.isOrigin()) {
                        String[] text1 = port1.getPortName().split(",");
                        String[] text2 = port2.getPortName().split(",");
                        for (j = 0; j < Math.min(text1.length, text2.length); j++) {
                            name1 = text1[j].trim();
                            name2 = text2[j].trim();
                            if (name1.equals(name2)) {
                                name = name1;
                            } else {
                                name = name1 + "__" + name2;
                            }
                            //TraceManager.addDev( "About to add " + _topname + " " + name + " " );
                            al.add(_topname + "::" + name);
                        }
                    }
                }
            }
            //            }
        }

        return al;
    }

    public List<String> getAllTMLInputPorts(String _topname) {   //the destination ports

        //Use HashSet to avoid returning multiple identical ports due to the presence of join nodes
        Set<String> al = new HashSet<String>();

        //  TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        List<TMLCPrimitiveComponent> components = getPrimitiveComponentList();
        Iterator<TMLCPrimitiveComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name2;
        TMLCPrimitivePort port1, port2;
        int j;

        while (iterator.hasNext()) {
            //tgc = iterator.next();

            //if( tgc instanceof TMLCPrimitiveComponent ) {
            tmlc = iterator.next();
            //TraceManager.addDev("Component:" + tmlc.getValue());
            ports = tmlc.getAllChannelsOriginPorts();
            //TraceManager.addDev("Ports size:" + ports.size());
            li = ports.listIterator();
            while (li.hasNext()) {
                port1 = li.next();
                portstome = getPortsConnectedTo(port1, components);   //this prints the ports via TraceManager
                //TraceManager.addDev("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
                Iterator<TMLCPrimitivePort> ite = portstome.listIterator();
                while (ite.hasNext()) {
                    //if ( portstome.size() == 1 ) {
                    //TraceManager.addDev("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
                    port2 = ite.next();
                    if (!port2.isOrigin()) {
                        String[] text1 = port1.getPortName().split(",");
                        String[] text2 = port2.getPortName().split(",");
                        for (j = 0; j < Math.min(text1.length, text2.length); j++) {
                            //name1 = text1[j].trim();
                            name2 = text2[j].trim();
                            //TraceManager.addDev( "The input port is: " + name2 );
                            al.add(_topname + "::" + name2);
                        }
                    }
                }
            }
            //}
        }
        return new ArrayList<String>(al);
        //return ArrayList<String>( Arrays.asListal );
    }

    public List<String> getAllTMLEventNames(String _topname) {

        List<String> al = new ArrayList<String>();
        //     TGComponent tgc;
        TMLCPrimitiveComponent tmlc;
        List<TMLCPrimitiveComponent> components = getPrimitiveComponentList();
        Iterator<TMLCPrimitiveComponent> iterator = components.listIterator();
        Iterator<TMLCPrimitivePort> li;//, li2;
        List<TMLCPrimitivePort> ports, portstome;
        String name, name1, name2;
        TMLCPrimitivePort port1, port2;
        int j;

        while (iterator.hasNext()) {
            tmlc = iterator.next();
            // if( tgc instanceof TMLCPrimitiveComponent ) {
            //   tmlc = ( TMLCPrimitiveComponent )tgc;
            //TraceManager.addDev("Component:" + tmlc.getValue());
            ports = tmlc.getAllEventsOriginPorts();
            //TraceManager.addDev("Ports size:" + ports.size());
            li = ports.listIterator();
            while (li.hasNext()) {
                port1 = li.next();
                portstome = getPortsConnectedTo(port1, components);
                //TraceManager.addDev("Considering port1 = " +port1.getPortName() + " size of connecting ports:" + portstome.size());
                //Iterator<TMLCPrimitivePort> ite = portstome.listIterator();
                //while(ite.hasNext()) {
                //TraceManager.addDev("port=" + ((TMLCPrimitivePort)(ite.next())).getPortName());
                //}
                if (portstome.size() == 1) {
                    port2 = portstome.get(0);
                    String[] text1 = port1.getPortName().split(",");
                    String[] text2 = port2.getPortName().split(",");
                    for (j = 0; j < Math.min(text1.length, text2.length); j++) {
                        name1 = text1[j].trim();
                        name2 = text2[j].trim();
                        if (name1.equals(name2)) {
                            name = name1;
                        } else {
                            name = name1 + "__" + name2;
                        }
                        al.add(_topname + "::" + name);
                    }
                }
            }
            //}
        }

        return al;
    }

    public void getAllPortsConnectedTo(List<TGComponent> ll, TMLCPrimitivePort _port) {
        List<TGComponent> components = getMGUI().getAllTMLComponents();
        Iterator<TGComponent> iterator = components.listIterator();
        TGComponent tgc, tgc1, tgc2;
        TMLCPortConnector portco;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPortConnector) {
                portco = (TMLCPortConnector) tgc;
                //TraceManager.addDev("portco");
                tgc1 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP1());
                tgc2 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP2());
                if ((tgc1 != null) && (tgc2 != null)) {
                    //TraceManager.addDev("tgc1=" + tgc1 + " tgc2=" + tgc2);

                    if (tgc1 instanceof TMLCRemoteCompositeComponent) {
                        tgc1 = ((TMLCRemoteCompositeComponent) tgc1).getPortOf(portco.getTGConnectingPointP1());
                    }

                    if (tgc2 instanceof TMLCRemoteCompositeComponent) {
                        tgc2 = ((TMLCRemoteCompositeComponent) tgc2).getPortOf(portco.getTGConnectingPointP2());
                    }

                    //TraceManager.addDev("tgc1=" + tgc1 + " tgc2=" + tgc2);

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

    @Override
    public String getXMLHead() {
        return "<TMLComponentTaskDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + zoomParam() + " >";
    }

    @Override
    public String getXMLTail() {
        return "</TMLComponentTaskDiagramPanel>";
    }

    @Override
    public String getXMLSelectedHead() {
        return "<TMLComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    @Override
    public String getXMLSelectedTail() {
        return "</TMLComponentTaskDiagramPanelCopy>";
    }

    @Override
    public String getXMLCloneHead() {
        return "<TMLComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }

    @Override
    public String getXMLCloneTail() {
        return "</TMLComponentTaskDiagramPanelCopy>";
    }
    @Override
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
            s += " channels=\"true\"";
        } else {
            s += " channels=\"false\"";
        }
        if (eventsVisible) {
            s += " events=\"true\"";
        } else {
            s += " events=\"false\"";
        }
        if (requestsVisible) {
            s += " requests=\"true\"";
        } else {
            s += " requests=\"false\"";
        }

        return s;
    }

    public List<String> getAllNonMappedTMLPrimitiveComponentNames(String _topName, TMLArchiDiagramPanel _tadp, boolean ref, String _name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        List<String> list = new ArrayList<String>();
        //String name;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPrimitiveComponent) {
                addNonMappedTMLPritimiveComponentsNames((TMLCPrimitiveComponent) tgc, list, _topName, _tadp, ref, _name);
            }
            if (tgc instanceof TMLCCompositeComponent) {
                getAllNonMappedTMLPrimitiveComponentNamesByComponent(tgc, list, _topName, _tadp, ref, _name);
            }
        }

        return list;
    }

    public void getAllNonMappedTMLPrimitiveComponentNamesByComponent(TGComponent tgc, List<String> list, String _topName, TMLArchiDiagramPanel _tadp, boolean ref, String _name) {
        TGComponent tgc1;

        for (int i = 0; i < tgc.getNbInternalTGComponent(); i++) {
            tgc1 = tgc.getInternalTGComponent(i);
            if (tgc1 instanceof TMLCPrimitiveComponent) {
                addNonMappedTMLPritimiveComponentsNames((TMLCPrimitiveComponent) tgc1, list, _topName, _tadp, ref, _name);
            }
            if (tgc1 instanceof TMLCCompositeComponent) {
                getAllNonMappedTMLPrimitiveComponentNamesByComponent(tgc1, list, _topName, _tadp, ref, _name);
            }
        }
    }

    public List<String> getAllCompositeComponent(String _name) {
        List<String> list = new ArrayList<String>();
        TGComponent tgc1;
        String s;
        TMLCCompositeComponent tmlcc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc1 = iterator.next();

            if (tgc1 instanceof TMLCCompositeComponent) {
                tmlcc = (TMLCCompositeComponent) tgc1;
                s = _name + "::" + tmlcc.getValue();
                list.add(s);
                tmlcc.getAllCompositeComponents(list, _name);
            }
        }

        return list;
    }

    public void addNonMappedTMLPritimiveComponentsNames(TMLCPrimitiveComponent tgc, List<String> list, String _topName, TMLArchiDiagramPanel _tadp, boolean ref, String _name) {
        String name1 = tgc.getValue();

        if (ref && name1.equals(_name)) {
            list.add(_topName + "::" + name1);
        } else {
            if (!_tadp.isMapped(_topName, name1)) {
                list.add(_topName + "::" + name1);
            }
        }
    }

    public String[] getCompOutChannels() {
        List<String> chls = new ArrayList<String>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        // List<String> list = new ArrayList<String>();
        //TMLCPrimitiveComponent tmp;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                TMLCPrimitiveComponent comp = (TMLCPrimitiveComponent) tgc;
                List<TMLCPrimitivePort> ll = comp.getAllChannelsOriginPorts();
                Iterator<TMLCPrimitivePort> ite = ll.listIterator();
                while (ite.hasNext()) {
                    TMLCPrimitivePort port = ite.next();
                    chls.add(port.getPortName());
                }
            }
        }
        String[] chlArray = new String[chls.size()];
        chlArray = chls.toArray(chlArray);
        return chlArray;
    }

    public String[] getCompInChannels() {
        List<String> chls = new ArrayList<String>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        // List<String> list = new ArrayList<String>();
        //TMLCPrimitiveComponent tmp;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                TMLCPrimitiveComponent comp = (TMLCPrimitiveComponent) tgc;
                List<TMLCPrimitivePort> ll = comp.getAllChannelsDestinationPorts();
                Iterator<TMLCPrimitivePort> ite = ll.listIterator();
                while (ite.hasNext()) {
                    TMLCPrimitivePort port = ite.next();
                    chls.add(port.getPortName());
                }
            }
        }
        String[] chlArray = new String[chls.size()];
        chlArray = chls.toArray(chlArray);
        return chlArray;
    }

    public TMLCPrimitiveComponent getPrimitiveComponentByName(String _name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        // List<String> list = new ArrayList<String>();
        TMLCPrimitiveComponent tmp;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCPrimitiveComponent) {
                if (tgc.getValue().equals(_name)) {
                    return ((TMLCPrimitiveComponent) tgc);
                }
            }

            if (tgc instanceof TMLCCompositeComponent) {
                tmp = ((TMLCCompositeComponent) tgc).getPrimitiveComponentByName(_name);
                if (tmp != null) {
                    return tmp;
                }
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                tmp = ((TMLCRemoteCompositeComponent) tgc).getPrimitiveComponentByName(_name);
                if (tmp != null) {
                    return tmp;
                }
            }
        }

        return null;
    }

    public void updateReferenceToTMLCCompositeComponent(TMLCCompositeComponent tmlcc) {
        //TraceManager.addDev("Update from " + tmlcc.getValue());
        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCCompositeComponent) {
                ((TMLCCompositeComponent) tgc).updateReferenceToTMLCCompositeComponent(tmlcc);
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                ((TMLCRemoteCompositeComponent) tgc).updateReference(tmlcc);
            }
        }
    }

    public TMLCCompositeComponent getCompositeComponentByName(String _name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        TMLCCompositeComponent tmp;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCCompositeComponent) {
                tmp = (TMLCCompositeComponent) tgc;
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

    public void hideConnectors() {
        Iterator<TGComponent> iterator = componentList.listIterator();
        TMLCPortConnector connector;
        TGComponent tgc;
        TGComponent tgc1;
        TGComponent tgc2;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPortConnector) {
                connector = (TMLCPortConnector) tgc;
                tgc1 = getComponentToWhichBelongs(connector.getTGConnectingPointP1());
                tgc2 = getComponentToWhichBelongs(connector.getTGConnectingPointP2());
                if ((tgc1 != null) && (tgc2 != null)) {
                    if (tgc1.hasAnHiddenAncestor()) {
                        tgc.setHidden(true);
                    } else {
                        if (tgc2.hasAnHiddenAncestor()) {
                            tgc.setHidden(true);
                        } else {
                            tgc.setHidden(false);
                        }
                    }
                }
            }
        }
    }

    /*public boolean isConnectedToTasks(TMLCompositionOperator co) {
      if ((getTask1ToWhichIamConnected(co) != null) && (getTask2ToWhichIamConnected(co) != null)) {
      return true;
      }
      return false;
      }*/

    /*public TMLTaskInterface getTask1ToWhichIamConnected(TMLCompositionOperator co) {
      TGConnectorTMLAssociationNav tgctmlan = getTGConnectorAssociationOf(co);
      TGComponent tgc;
      //TraceManager.addDev("tmlan t1?");
      if (tgctmlan != null) {
      //TraceManager.addDev("tmlan found t1");
      tgc = getTopComponentToWhichBelongs(tgctmlan.getTGConnectingPointP1());
      if ((tgc != null) && (tgc instanceof TMLTaskInterface)) {
      return (TMLTaskInterface) tgc;
      }
      }
      return null;
      }

      public TMLTaskInterface getTask2ToWhichIamConnected(TMLCompositionOperator co) {
      TGConnectorTMLAssociationNav tgctmlan = getTGConnectorAssociationOf(co);
      TGComponent tgc;
      //TraceManager.addDev("tmlan t2?");
      if (tgctmlan != null) {
      //TraceManager.addDev("tmlan found t2");
      tgc = getTopComponentToWhichBelongs(tgctmlan.getTGConnectingPointP2());
      if ((tgc != null) && (tgc instanceof TMLTaskInterface)) {
      return (TMLTaskInterface) tgc;
      }
      }
      return null;
      }*/

    /*public TGConnectorTMLAssociationNav getTGConnectorAssociationOf(TMLCompositionOperator tcd) {
      int i;
      TGConnectingPoint p1, p2;
      TGConnector tgco;
      TGConnectorTMLCompositionOperator tgcoco;
      TGComponent tgc;

      for(i=0; i<tcd.getNbConnectingPoint(); i++) {
      //TraceManager.addDev("titi");
      p1 = tcd.tgconnectingPointAtIndex(i);
      tgco = getConnectorConnectedTo(p1);
      if (tgco != null) {
      //TraceManager.addDev("Found tgco");
      }
      if ((tgco != null) && (tgco instanceof TGConnectorTMLCompositionOperator)){
      //TraceManager.addDev("toto");
      tgcoco = (TGConnectorTMLCompositionOperator)tgco;
      if (p1 == tgcoco.getTGConnectingPointP1()) {
      p2 = tgcoco.getTGConnectingPointP2();
      } else {
      p2 = tgcoco.getTGConnectingPointP1();
      }

      // p2 now contains the connecting point of a association
      tgc = getComponentToWhichBelongs(p2);
      if ((tgc != null) && (!p2.isFree()) && (tgc instanceof TGConnectorTMLAssociationNav)) {
      //TraceManager.addDev("tutu");
      return (TGConnectorTMLAssociationNav)tgc;
      }
      }
      }
      return null;
      }*/

    /*public boolean connectedToVisible(TGConnectorTMLAssociationNav tgconav) {
      TGConnectorTMLCompositionOperator tgcoco = tgconav.getTGConnectorTMLCompositionOperator();
      if (tgcoco == null) {
      return true;
      }
      return connectedToVisible(tgcoco);
      }

      public boolean connectedToVisible(TGConnectorTMLCompositionOperator tgcoco) {
      TGConnectingPoint p2 = tgcoco.getTGConnectingPointP2();
      TGComponent tgc = getComponentToWhichBelongs(p2);
      if (tgc instanceof TMLCompositionOperator) {
      return ((TMLCompositionOperator)tgc).isToggledVisible();
      }
      return false;
      }*/


    public boolean areAllVisible() {
        return channelsVisible && eventsVisible && requestsVisible;
    }

    public boolean areChannelsVisible() {
        return channelsVisible;
    }

    public boolean areEventsVisible() {
        return eventsVisible;
    }

    public boolean areRequestsVisible() {
        return requestsVisible;
    }

    public void setChannelsVisible(boolean b) {
        channelsVisible = b;
    }

    public void setEventsVisible(boolean b) {
        eventsVisible = b;
    }

    public void setRequestsVisible(boolean b) {
        requestsVisible = b;
    }

    public void loadExtraParameters(Element elt) {
        String s;
        //TraceManager.addDev("Extra parameter");
        try {
            s = elt.getAttribute("channels");
            //TraceManager.addDev("S=" + s);
            if (s.compareTo("false") == 0) {
                setChannelsVisible(false);
            } else {
                setChannelsVisible(true);
            }
            s = elt.getAttribute("events");
            if (s.compareTo("false") == 0) {
                setEventsVisible(false);
            } else {
                setEventsVisible(true);
            }
            s = elt.getAttribute("requests");
            if (s.compareTo("false") == 0) {
                setRequestsVisible(false);
            } else {
                setRequestsVisible(true);
            }

        } catch (Exception e) {
            // Model was saved in an older version of TTool
            //TraceManager.addDev("older format");
            setChannelsVisible(true);
            setEventsVisible(true);
            setRequestsVisible(true);
        }
    }

    public void setConnectorsToFront() {
        TGComponent tgc;

        //TraceManager.addDev("list size=" + componentList.size());

        Iterator<TGComponent> iterator = componentList.listIterator();

        List<TGComponent> list = new ArrayList<TGComponent>();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (!(tgc instanceof TGConnector)) {
                list.add(tgc);
            }
        }


        //TraceManager.addDev("Putting to back ...");
        for (TGComponent tgc1 : list) {
            //TraceManager.addDev("Putting to back: " + tgc1);
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

            if (tgc instanceof TMLCCompositeComponent) {
                ((TMLCCompositeComponent) (tgc)).delayedLoad();
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                try {
                    ((TMLCRemoteCompositeComponent) (tgc)).delayedLoad();
                } catch (Exception e) {
                }
            }
        }
    }

    // Returns the faulty paths
    public List<TMLCPath> updatePorts() {
        //TraceManager.addDev("Making paths");
        List<TMLCPath> paths = makePaths();
        List<TMLCPath> faultyPaths = new ArrayList<TMLCPath>();
        //String error = "";

        // Checking rules of paths, and setting colors accordingly
        for (TMLCPath path : paths) {
            path.checkRules();
            if (path.hasError()) {
                //TraceManager.addDev("Path error:" + path.getErrorMessage());
                faultyPaths.add(path);
                //error = path.getErrorMessage();
            }
            path.setColor();
        }



        return faultyPaths;

    }

    public TMLCPath findPathWith(TGComponent tgc) {
        List<TMLCPath> paths = makePaths();
        for (TMLCPath path : paths) {
            if (path.contains(tgc)) {
                return path;
            }
        }
        return null;
    }

    public void updatePorts_oldVersion() {

        //TraceManager.addDev("Update ports / nb of components = " + componentList.size());
        Iterator<TGComponent> iterator;
        TGComponent tgc;

        // Get all TMLCPrimitivePort
        List<TMLCCompositePort> ports = new ArrayList<TMLCCompositePort>();
        List<TMLCCompositePort> referencedports = new ArrayList<TMLCCompositePort>();
        List<TMLCPrimitivePort> pports = new ArrayList<TMLCPrimitivePort>();
        List<TMLCChannelFacility> facilities = new ArrayList<TMLCChannelFacility>();

        iterator = componentList.listIterator();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCCompositeComponent) {
                ports.addAll(((TMLCCompositeComponent) tgc).getAllInternalCompositePorts());
                pports.addAll(((TMLCCompositeComponent) tgc).getAllInternalPrimitivePorts());
                referencedports.addAll(((TMLCCompositeComponent) tgc).getAllReferencedCompositePorts());
            }

            /*if (tgc instanceof TMLCRemoteCompositeComponent) {
              ports.addAll(((TMLCRemoteCompositeComponent)tgc).getAllInternalCompositePorts());
              pports.addAll(((TMLCRemoteCompositeComponent)tgc).getAllInternalPrimitivePorts());
              }*/

            if (tgc instanceof TMLCPrimitiveComponent) {
                pports.addAll(((TMLCPrimitiveComponent) tgc).getAllInternalPrimitivePorts());
            }

            if (tgc instanceof TMLCCompositePort) {
                ports.add((TMLCCompositePort) tgc);
            }

            if (tgc instanceof TMLCPrimitivePort) {
                pports.add((TMLCPrimitivePort) tgc);
            }

            if (tgc instanceof TMLCChannelFacility) {
                facilities.add((TMLCChannelFacility) tgc);
            }
        }

        // Remove All Current Links To Ports
        for (TMLCCompositePort port : ports) {
            if (!referencedports.contains(port)) {
                port.purge();
            }
        }

        // We take each primitive ports individually and we go thru the graph
        List<TMLCChannelFacility> mets = new ArrayList<TMLCChannelFacility>();
        TGConnector connector;
        TGConnectingPoint tp;
        String conflictMessage;

        //TraceManager.addDev("pports size=" + pports.size() + " ports size=" + ports.size());

        for (TMLCPrimitivePort pport : pports) {
            //TraceManager.addDev("port id=" + pport.getId());
            for (int i = 0; i < pport.getNbConnectingPoint(); i++) {
                tp = pport.getTGConnectingPointAtIndex(i);
                connector = findTGConnectorUsing(tp);
                if (connector != null) {
                    //TraceManager.addDev("Connector");
                    mets.clear();
                    conflictMessage = propagate(pport, tp, connector, mets);
                    TraceManager.addDev("Conflict=" + conflictMessage);
                    analysePorts(pport, mets, (conflictMessage != null), conflictMessage);
                } else {
                    //TraceManager.addDev("no connector");
                }
            }
        }
    }

    public String propagate(TMLCPrimitivePort pport, TGConnectingPoint tp, TGConnector connector, List<TMLCChannelFacility> mets) {
        TGConnectingPoint tp2;
        TMLCChannelFacility cp = null;
        //boolean conflict = false;
        String conflictMessage = null;
        String conflictMessageTmp;
        //    boolean ret;
        int outindex, inindex;

        if (tp == connector.getTGConnectingPointP1()) {
            tp2 = connector.getTGConnectingPointP2();
        } else {
            tp2 = connector.getTGConnectingPointP1();
        }

        TGComponent tgc = (TGComponent) (tp2.getFather());
        int index = tgc.getIndexOfTGConnectingPoint(tp2);

        if (tgc instanceof TMLCPrimitivePort) {
            return conflictMessage;
        }

        // Cycle?
        //TraceManager.addDev("cycle?");
        if (mets.contains(tgc)) {
            //TraceManager.addDev("Conflict issue -1");
            return "Connection contains a cycle";
        }

        //TraceManager.addDev("Composite port? tgc=" + tgc);
        if (tgc instanceof TMLCCompositePort) {
            //TraceManager.addDev("Composite port!");
            cp = (TMLCChannelFacility) tgc;
            mets.add(cp);

            inindex = cp.getInpIndex();
            outindex = cp.getOutpIndex();
            // Already positionned port?
            if (pport.isOrigin()) {
                //TraceManager.addDev("Origin port");
                if (cp.getOutPort() != null) {
                    //TraceManager.addDev("Two ports: pport.getType() = " +  pport.getType());
                    if (pport.getPortType() != 2) {
                        //conflict = true;
                        conflictMessage = "Conflicting ports types";
                        //TraceManager.addDev("Conflict issue 0");
                    } else {
                        if (cp.getOutPort().getPortType() != 2) {
                            conflictMessage = "More than two sending non-request ports ";
                            //TraceManager.addDev("Conflict issue 1");
                        } else {
                            if ((outindex < 5 && index > 4) || (outindex > 4 && index < 5)) {
                                conflictMessage = "Sending ports on both side of a composite port";
                                //TraceManager.addDev("Conflict issue 2");
                            }
                        }
                    }
                } else {
                    if (inindex > -1) {
                        if ((inindex < 5 && index < 5) || (inindex > 4 && index > 4)) {
                            conflictMessage = "Sending and receiving ports on the same side of a composite port";
                            //TraceManager.addDev("Conflict issue 3");
                        }
                    }
                    //TraceManager.addDev("Setting out port");
                    cp.setOutPort(pport);
                    cp.setOutpIndex(index);
                }
                //TraceManager.addDev("Explore next");
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                //TraceManager.addDev("Explore done");
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                if (cp.getInPort() != null) {
                    conflictMessage = "More than two receiving ports ";
                    //TraceManager.addDev("Conflict issue 4");
                } else {
                    if (outindex > -1) {
                        if ((index < 5 && outindex < 5) || (index > 4 && outindex > 4)) {
                            conflictMessage = "Sending and receiving ports on the same side of a composite port";
                            //TraceManager.addDev("Conflict issue 5");
                        }
                    }
                    cp.setInPort(pport);
                    cp.setInpIndex(index);
                }
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            }
        } else if (tgc instanceof TMLCFork) {
            // Only one out, more than one in is ok
            // No TMLCJoin
            cp = (TMLCChannelFacility) tgc;
            mets.add(cp);

            // Checks that "mets" contains no TMLJoin
            for (TMLCChannelFacility met : mets) {
                if (met instanceof TMLCJoin) {
                    conflictMessage = "Join and Fork operators are mixed in the same channel";
                    conflictMessageTmp = explore(pport, tp2, cp, mets);
                    if (conflictMessageTmp != null) {
                        conflictMessage = conflictMessageTmp;
                    }
                    return conflictMessage;
                }
            }

            if (pport.isOrigin()) {

                //TraceManager.addDev("Origin port");
                if ((cp.getInPort() != null) && (cp.getInPort() != pport)) {
                    conflictMessage = "More than two sending ports  in a fork architecture";
                }

                cp.setInPort(pport);

                //TraceManager.addDev("Explore next");
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                //TraceManager.addDev("Explore done");
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                conflictMessage = explore(pport, tp2, cp, mets);
            }
        } else if (tgc instanceof TMLCJoin) {
            // Only one out, more than one in is ok
            // No TMLCFork
            cp = (TMLCChannelFacility) tgc;
            mets.add(cp);

            // Checks that "mets" contains no TMLJoin
            for (TMLCChannelFacility met : mets) {
                if (met instanceof TMLCFork) {
                    conflictMessage = "Fork and Join operators are mixed in the same channel";
                    conflictMessageTmp = explore(pport, tp2, cp, mets);
                    if (conflictMessageTmp != null) {
                        conflictMessage = conflictMessageTmp;
                    }
                    return conflictMessage;
                }
            }

            if (!pport.isOrigin()) {

                //TraceManager.addDev("Origin port");
                if ((cp.getOutPort() != null) && (cp.getOutPort() != pport)) {
                    conflictMessage = "More than two receiving ports in a join architecture";
                }
                cp.setOutPort(pport);


                //TraceManager.addDev("Explore next");
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                //TraceManager.addDev("Explore done");
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                conflictMessage = explore(pport, tp2, cp, mets);
            }
        }
        if (cp != null) {
            if ((cp.getInPort() != null) && (cp.getOutPort() != null)) {
                if (cp.getInPort().getType() != cp.getOutPort().getType()) {
                    conflictMessage = "Ports are not compatible";
                } else {
                    TraceManager.addDev("ports of " + cp + " are compatible out=" + cp.getOutPort().getType() + " in=" + cp.getInPort().getType());
                }
            }
        }

        return conflictMessage;
    }

    public String explore(TMLCPrimitivePort pport, TGConnectingPoint _tp, TMLCChannelFacility cp, List<TMLCChannelFacility> mets) {
        String conflictMessage = null;
        String conflictMessageTmp;
        TGConnectingPoint tp;
        TGConnector connector;

        for (int i = 0; i < cp.getNbConnectingPoint(); i++) {
            tp = cp.getTGConnectingPointAtIndex(i);
            if (tp != _tp) {
                connector = findTGConnectorUsing(tp);
                if (connector != null) {
                    conflictMessageTmp = propagate(pport, tp, connector, mets);
                    if (conflictMessageTmp != null) {
                        conflictMessage = conflictMessageTmp;
                    }
                }
            }
        }

        return conflictMessage;
    }

    public void analysePorts(TMLCPrimitivePort pport, List<TMLCChannelFacility> mets, boolean conflict, String message) {
        if (mets.size() == 0) {
            return;
        }

        for (TMLCChannelFacility port : mets) {
            port.setConflict(conflict, message);
        }
    }

    public List<String> getAllTMLTaskNames(String _topname) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        List<String> list = new ArrayList<String>();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCPrimitiveComponent) {
                list.add(_topname + "::" + tgc.getValue());
            }
        }

        return list;
    }

    /*public ArrayList<String> getAllTMLCommunicationNames(String _topname) {
      TGComponent tgc;
      Iterator iterator = componentList.listIterator();
      ArrayList<String> list = new ArrayList<String>();
      String name = "";
      String type = "";

      while(iterator.hasNext()) {
      tgc = (TGComponent)(iterator.next());
      if (tgc instanceof TMLCompositionOperator) {
      if (tgc instanceof TMLEventOperator) {
      name = ((TMLEventOperator)tgc).getEventName();
      type = "Event";
      }
      if (tgc instanceof TMLChannelOperator) {
      name = ((TMLChannelOperator)tgc).getChannelName();
      type = "Channel";
      }
      if (tgc instanceof TMLRequestOperator) {
      name = ((TMLRequestOperator)tgc).getRequestName();
      type = "Request";
      }

      list.add(_topname + "::" + name + " (" + type + ")");
      }
      }

      return list;
      }*/

    /*public ArrayList<String> getAllNonMappedTMLTaskNames(String _topName, TMLArchiDiagramPanel _tadp, boolean ref, String _name) {
      TGComponent tgc;
      Iterator iterator = componentList.listIterator();
      ArrayList<String> list = new ArrayList<String>();
      String name;

      while(iterator.hasNext()) {
      tgc = (TGComponent)(iterator.next());
      if (tgc instanceof TMLTaskOperator) {
      name = ((TMLTaskOperator)tgc).getTaskName();
      if (ref && name.equals(_name)) {
      list.add(_topName + "::" + name);
      } else {
      if (!_tadp.isMapped(_topName,  name)) {
      list.add(_topName + "::" + name);
      }
      }
      }
      }

      return list;
      }*/

    /*public TMLTaskOperator getTaskByName(String _name) {
      TGComponent tgc;
      Iterator iterator = componentList.listIterator();
      ArrayList<String> list = new ArrayList<String>();

      while(iterator.hasNext()) {
      tgc = (TGComponent)(iterator.next());
      if (tgc instanceof TMLTaskOperator) {
      if (((TMLTaskOperator)tgc).getTaskName().equals(_name)) {
      return ((TMLTaskOperator)tgc);
      }
      }
      }

      return null;
      }*/
    public Vector<String> getAllTMLTasksAttributes() {

        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        Vector<String> list = new Vector<String>();

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCCompositeComponent) {
                for (TMLCPrimitiveComponent primComp : ((TMLCCompositeComponent) tgc).getAllPrimitiveComponents()) {
                    for (Object o : primComp.getAttributeList()) {
                        String s = o.toString();
                        list.add(primComp.getValue() + "." + s.substring(2, s.length() - 1));
                    }
                }
            }
        }

        return list;
    }

    public Vector<String> getAllRecords(TMLCPrimitiveComponent tgc) {
        Vector<String> list = new Vector<String>();
        getAllRecords((TMLCCompositeComponent) (tgc.getFather()), list);
        return list;
    }

    public void getAllRecords(TMLCCompositeComponent comp, Vector<String> list) {
        TGComponent tgc;
        if (comp == null) {
            Iterator<TGComponent> iterator = componentList.listIterator();

            while (iterator.hasNext()) {
                tgc = iterator.next();

                if (tgc instanceof TMLCRecordComponent) {
                    list.add(tgc.getValue());
                }
            }
            return;
        }

        for (int i = 0; i < comp.getNbInternalTGComponent(); i++) {
            tgc = comp.getInternalTGComponent(i);
            if (tgc instanceof TMLCRecordComponent) {
                list.add(tgc.getValue());
            }
        }

        getAllRecords((TMLCCompositeComponent) (comp.getFather()), list);
    }

    public TMLCRecordComponent getRecordNamed(TMLCPrimitiveComponent tgc, String _nameOfRecord) {
        return getRecordNamed((TMLCCompositeComponent) (tgc.getFather()), _nameOfRecord);
    }

    public TMLCRecordComponent getRecordNamed(TMLCCompositeComponent comp, String _nameOfRecord) {
        TGComponent tgc;
        if (comp == null) {
            Iterator<TGComponent> iterator = componentList.listIterator();

            while (iterator.hasNext()) {
                tgc = iterator.next();
                if (tgc instanceof TMLCRecordComponent) {
                    if (tgc.getValue().compareTo(_nameOfRecord) == 0) {
                        return (TMLCRecordComponent) tgc;
                    }
                }
            }
            return null;
        }

        for (int i = 0; i < comp.getNbInternalTGComponent(); i++) {
            tgc = comp.getInternalTGComponent(i);
            if (tgc instanceof TMLCRecordComponent) {
                if (tgc.getValue().compareTo(_nameOfRecord) == 0) {
                    return (TMLCRecordComponent) tgc;
                }
            }
        }

        return getRecordNamed((TMLCCompositeComponent) (comp.getFather()), _nameOfRecord);
    }


    public void findAllReferencedPanels(List<TMLComponentTaskDiagramPanel> panels) {
        if (panels.contains(this)) {
            return;
        }


        panels.add(this);

        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while (iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                TMLCRemoteCompositeComponent remote = (TMLCRemoteCompositeComponent) tgc;
                TDiagramPanel panel = remote.getReferencedDiagram();
                if ((panel != null) && (panel instanceof TMLComponentTaskDiagramPanel)) {
                    ((TMLComponentTaskDiagramPanel) panel).findAllReferencedPanels(panels);
                }
            }

            if (tgc instanceof TMLCCompositeComponent) {
                //We must find all panels referencing this component
                panels.addAll(mgui.getAllPanelsReferencingTMLCCompositeComponent((TMLCCompositeComponent) tgc));
            }
        }
    }

    public List<TMLCPath> makePaths() {
        List<TMLComponentTaskDiagramPanel> panels = new ArrayList<TMLComponentTaskDiagramPanel>();

        // We first find all the implicated panels
        findAllReferencedPanels(panels);
        //TraceManager.addDev("Nb of handled panels:" + panels.size());

        List<TMLCPath> paths = new ArrayList<TMLCPath>();
        Iterator<TGComponent> iterator;
        TGComponent tgc;

        // Go through the component list of all panels, and make paths. Then, go thru connectors,
        // and merge paths until nomore merging is possible

        for (TDiagramPanel panel : panels) {
            iterator = panel.getComponentList().listIterator();

            List<TMLCCompositePort> listcp;
            List<TMLCPrimitivePort> listpp;

            while (iterator.hasNext()) {
                tgc = iterator.next();

                if (tgc instanceof TMLCCompositeComponent) {
                    listcp = ((TMLCCompositeComponent) tgc).getAllInternalCompositePorts();
                    for (TMLCCompositePort cp : listcp) {
                        addToPaths(paths, cp);
                    }

                    listpp = ((TMLCCompositeComponent) tgc).getAllInternalPrimitivePorts();
                    for (TMLCPrimitivePort pp : listpp) {
                        addToPaths(paths, pp);
                    }
                }

                if (tgc instanceof TMLCPrimitiveComponent) {
                    listpp = ((TMLCPrimitiveComponent) tgc).getAllInternalPrimitivePorts();
                    for (TMLCPrimitivePort pp : listpp) {
                        addToPaths(paths, pp);
                    }
                }

                if (tgc instanceof TMLCPrimitivePort) {
                    addToPaths(paths, tgc);
                }

                if (tgc instanceof TMLCChannelFacility) {
                    addToPaths(paths, tgc);
                }

                /* We also include the paths of diagrams referenced via referenced components */
                //                if (tgc instanceof TMLCRemoteCompositeComponent) {
                //                    TMLCRemoteCompositeComponent remote = (TMLCRemoteCompositeComponent)tgc;
                //
                //                    // Nothing to do: referenced ports are added thanks to the analyzes of the referenced panels
                //                    // and the handling of that issue in connector analysis
                //                }
            }
        }

        // Use connectors to merge paths with one another

        for (TDiagramPanel panel : panels) {
            iterator = panel.getComponentList().listIterator();
            TMLCPortConnector connector;
            TGComponent tgc1, tgc2;
            TMLCPath path1, path2;

            while (iterator.hasNext()) {
                tgc = iterator.next();

                if (tgc instanceof TMLCPortConnector) {
                    connector = (TMLCPortConnector) tgc;
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


                    if (tgc1 instanceof TMLCRemoteCompositeComponent) {
                        tgc1 = ((TMLCRemoteCompositeComponent) tgc1).getPortOf(connector.getTGConnectingPointP1());
                    }
                    if (tgc2 instanceof TMLCRemoteCompositeComponent) {
                        tgc2 = ((TMLCRemoteCompositeComponent) tgc2).getPortOf(connector.getTGConnectingPointP2());
                    }

                    //TraceManager.addDev("tgc1=" + tgc1 +  " tgc2=" + tgc2);


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
                        // If there is a null component in the path, then, we must set an error in the path
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

        //TraceManager.addDev("----------- Nb of paths: " + paths.size());


        return paths;
    }

    public TMLCPath getPathOf(List<TMLCPath> paths, TGComponent tgc) {
        for (TMLCPath path : paths) {
            if (path.contains(tgc)) {
                return path;
            }
        }

        return null;
    }

    public void addToPaths(List<TMLCPath> paths, TGComponent tgc) {
        // Boolean found = false;

        for (TMLCPath path : paths) {
            if (path.contains(tgc)) {
                // found = true;
                return;
            }
        }

        // Create a new path
        TMLCPath ph = new TMLCPath();
        ph.addComponent(tgc);
        paths.add(ph);
    }

    public void getPanelsUsingAComponent(TMLCCompositeComponent tmlcc, List<TMLComponentTaskDiagramPanel> panels) {
        //TraceManager.addDev("Update from " + tmlcc.getValue());
        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLCCompositeComponent) {
                if (((TMLCCompositeComponent) tgc).hasRefencesTo(tmlcc)) {
                    panels.add(this);
                    return;
                }
            }

            if (tgc instanceof TMLCRemoteCompositeComponent) {
                if (((TMLCRemoteCompositeComponent) tgc).getReference() == tmlcc) {
                    panels.add(this);
                    return;
                }
            }
        }
    }

    public String[] getAllOutEvents(String nameOfComponent) {
        TMLCPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<TMLCPrimitivePort> ll = comp.getAllEventsOriginPorts();
        List<String> lString = new LinkedList<>();


        //String[] terms = new String[ll.size()];
        Iterator<TMLCPrimitivePort> ite = ll.listIterator();

        while (ite.hasNext()) {
            TMLCPrimitivePort port = ite.next();

            String name = port.getPortName();
            String [] allNames = name.split(",");
            for(int i=0; i<allNames.length; i++) {
                lString.add(allNames[i]);
            }

        }

        return  lString.toArray(new String[lString.size()]);
    }

    public String[] getAllInEvents(String nameOfComponent) {
        TMLCPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<TMLCPrimitivePort> ll = comp.getAllEventsDestinationPorts();
        String[] terms = new String[ll.size()];
        ListIterator<TMLCPrimitivePort> ite = ll.listIterator();
        int i = 0;
        while (ite.hasNext()) {
            TMLCPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }

        return terms;
    }

    public String[] getAllOutChannels(String nameOfComponent) {
        TMLCPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<TMLCPrimitivePort> ll = comp.getAllChannelsOriginPorts();
        String[] terms = new String[ll.size()];
        Iterator<TMLCPrimitivePort> ite = ll.listIterator();
        int i = 0;
        while (ite.hasNext()) {
            TMLCPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }

        return terms;
    }

    public String[] getAllOutRequests(String nameOfComponent) {
        TMLCPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<TMLCPrimitivePort> ll = comp.getAllRequestsOriginPorts();
        String[] terms = new String[ll.size()];
        Iterator<TMLCPrimitivePort> ite = ll.listIterator();
        int i = 0;
        while (ite.hasNext()) {
            TMLCPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }

        return terms;
    }

    public String[] getAllInChannels(String nameOfComponent) {
        TMLCPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<TMLCPrimitivePort> ll = comp.getAllChannelsDestinationPorts();
        String[] terms = new String[ll.size()];
        Iterator<TMLCPrimitivePort> ite = ll.listIterator();
        int i = 0;

        while (ite.hasNext()) {
            TMLCPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }

        return terms;
    }
}

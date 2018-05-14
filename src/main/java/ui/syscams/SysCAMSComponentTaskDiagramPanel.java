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
        if (tgc instanceof SysCAMSPrimitiveComponent) {
            return true;
        } 
        if (tgc instanceof SysCAMSCompositePort) {
            if (tgc.getFather() instanceof SysCAMSCompositeComponent) {
                getMGUI().updateReferenceToSysCAMSCompositeComponent((SysCAMSCompositeComponent)(tgc.getFather()));
            }
        }

        return true;
    }

    public boolean actionOnRemove(TGComponent tgc) {
        if (tgc instanceof SysCAMSPortConnector) {
            updatePorts();
        }
        if (tgc instanceof SysCAMSPrimitivePort) {
            updatePorts();
        }

        if (tgc instanceof SysCAMSChannelFacility) {
            updatePorts();
        }

        if (tgc instanceof SysCAMSCompositePort) {
            updatePorts();
            if (fatherOfRemoved instanceof SysCAMSCompositeComponent) {
                getMGUI().updateReferenceToSysCAMSCompositeComponent((SysCAMSCompositeComponent)(fatherOfRemoved));
            }
        }

        return true;
    }

    public List<SysCAMSPrimitivePort> getPortsByName(String name){
        List<SysCAMSPrimitivePort> ports = new ArrayList<SysCAMSPrimitivePort>();
        for (TGComponent tgc : componentList){

            if (tgc instanceof SysCAMSPrimitiveComponent){
                SysCAMSPrimitiveComponent comp = (SysCAMSPrimitiveComponent) tgc;
                List<SysCAMSPrimitivePort> cps = comp.getAllTDFOriginPorts();
                for (SysCAMSPrimitivePort port : cps){
                    if (port.commName.equals(name)){
                        ports.add(port);
                    }
                }
                cps = comp.getAllTDFDestinationPorts();
                for (SysCAMSPrimitivePort port : cps){
                    if (port.commName.equals(name)){
                        ports.add(port);
                    }
                }
            }
            if (tgc instanceof SysCAMSPrimitiveComponent){
            	SysCAMSPrimitiveComponent comp = (SysCAMSPrimitiveComponent) tgc;
            	List<SysCAMSPrimitivePort> cps = comp.getAllDEOriginPorts();
            	for (SysCAMSPrimitivePort port : cps){
            		if (port.commName.equals(name)){
            			ports.add(port);
            		}
            	}
            	cps = comp.getAllDEDestinationPorts();
            	for (SysCAMSPrimitivePort port : cps){
            		if (port.commName.equals(name)){
            			ports.add(port);
            		}
            	}
            }
        }
        return ports;
    }

    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof SysCAMSPrimitiveComponent) {
            SysCAMSPrimitiveComponent t = (SysCAMSPrimitiveComponent)tgc;
            mgui.newSysCAMSTaskName(tp, t.oldValue, t.getValue());
            return true;
        }
        if (tgc instanceof SysCAMSCompositeComponent) {
            SysCAMSCompositeComponent syscamscc = (SysCAMSCompositeComponent)tgc;
            getMGUI().updateReferenceToSysCAMSCompositeComponent(syscamscc);
        }
        return true;
    }

    public boolean renamePrimitiveComponent(String oldValue, String newValue) {
        return mgui.newSysCAMSComponentTaskName(tp, oldValue, newValue);
    }

    public boolean namePrimitiveComponentInUse(String oldValue, String newValue) {
        boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
        return ko ? ko : nameAllRecordComponentInUse(oldValue, newValue);
    }

    public boolean nameRecordComponentInUse(String oldValue, String newValue) {
        boolean ko = mgui.nameComponentInUse(tp, oldValue, newValue);
        return ko? ko : nameAllRecordComponentInUse(oldValue, newValue);
    }

    public boolean isCompositeNameUsed(String newvalue) {
        for (TGComponent tgc: this.componentList) {
            if (tgc.getValue().equals(newvalue))
                return true;
        }
        return false;
    }

    public boolean nameAllRecordComponentInUse(String oldValue, String newValue) {
        Iterator<SysCAMSRecordComponent> iterator = getRecordComponentList().listIterator();
        SysCAMSRecordComponent record;

        while(iterator.hasNext()) {
            record = iterator.next();
            if (record.getName().compareTo(newValue) == 0) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<SysCAMSRecordComponent> getRecordComponentList() {
        LinkedList<SysCAMSRecordComponent> ll = new LinkedList<SysCAMSRecordComponent>();
        TGComponent tgc;

        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSRecordComponent) {
                ll.add((SysCAMSRecordComponent) tgc);
            }
            if (tgc instanceof SysCAMSCompositeComponent) {
                ll.addAll(((SysCAMSCompositeComponent)tgc).getAllRecordComponents());
            }
            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                ll.addAll(((SysCAMSRemoteCompositeComponent)tgc).getAllRecordComponents());
            }
        }
        return ll;
    }

    public List<SysCAMSPrimitiveComponent> getPrimitiveComponentList() {
        List<SysCAMSPrimitiveComponent> ll = new LinkedList<SysCAMSPrimitiveComponent>();
        TGComponent tgc;

        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSPrimitiveComponent) {
                ll.add( (SysCAMSPrimitiveComponent) tgc );
            }
            if (tgc instanceof SysCAMSCompositeComponent) {
                ll.addAll(((SysCAMSCompositeComponent)tgc).getAllPrimitiveComponents());
            }
            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                ll.addAll(((SysCAMSRemoteCompositeComponent)tgc).getAllPrimitiveComponents());
            }
        }
        return ll;
    }

    public List<SysCAMSPrimitivePort> getPortsConnectedTo(SysCAMSPrimitivePort _port, List<? extends TGComponent> componentsToTakeIntoAccount) {
        List<TGComponent> ll;
        List<SysCAMSPrimitivePort> ret = new LinkedList<SysCAMSPrimitivePort>();
        Object o;
        SysCAMSPrimitivePort p;

        ll = getAllPortsConnectedTo(_port);
        Iterator<TGComponent> li = ll.listIterator();

        while(li.hasNext()) {
            o = li.next();

            if (o instanceof SysCAMSPrimitivePort) {
                p = (SysCAMSPrimitivePort)o;

                if (p.getFather() instanceof SysCAMSPrimitiveComponent) {
                    if (componentsToTakeIntoAccount.contains(p.getFather())) {
                        ret.add( p );
                    }
                }
            }
        }
        return ret;
    }

    public List<TGComponent> getAllPortsConnectedTo(SysCAMSPrimitivePort _port) {
        List<TGComponent> ll = new LinkedList<TGComponent>();
        getAllPortsConnectedTo( ll, _port );
        return ll;
    }

//    public List<String> getAllSysCAMSCommunicationNames(String _topname) {
//        List<String> al = new ArrayList<String>();
//
//        SysCAMSPrimitiveComponent syscamsc;
//        List<SysCAMSPrimitiveComponent> components = getPrimitiveComponentList();
//        Iterator<SysCAMSPrimitiveComponent> iterator = components.listIterator();
//        Iterator<SysCAMSPrimitivePort> li;//, li2;
//        List<SysCAMSPrimitivePort> ports, portstome;
//        String name, name1, name2;
//        SysCAMSPrimitivePort port1, port2;
//
//        int j;
//
//        while( iterator.hasNext() ) {
//            syscamsc = iterator.next();
//            ports = syscamsc.getAllChannelsOriginPorts();
//            li = ports.listIterator();
//            while( li.hasNext() ) {
//                port1 = li.next();
//                portstome = getPortsConnectedTo( port1, components );
//                Iterator<SysCAMSPrimitivePort> ite = portstome.listIterator();
//
//                while( ite.hasNext()) {
//                    port2 = ite.next();
//                    if (!port2.isOrigin()) {
//                        String []text1 = port1.getPortName().split( "," );
//                        String []text2 = port2.getPortName().split( "," );
//                        for ( j = 0; j < Math.min( text1.length, text2.length ); j++ ) {
//                            name1 = text1[j].trim();
//                            name2 = text2[j].trim();
//                            if( name1.equals( name2 ) ) {
//                                name = name1;
//                            }
//                            else {
//                                name = name1 + "__" + name2;
//                            }
//                            al.add( _topname + "::" + name );
//                        }
//                    }
//                }
//            }
//        }
//        return al;
//    }
//
//    public List<String> getAllSysCAMSInputPorts( String _topname ) {   //the destination ports
//        //Use HashSet to avoid returning multiple identical ports due to the presence of join nodes
//        Set<String> al = new HashSet<String>();
//        SysCAMSPrimitiveComponent syscamsc;
//        List<SysCAMSPrimitiveComponent> components = getPrimitiveComponentList();
//        Iterator<SysCAMSPrimitiveComponent> iterator = components.listIterator();
//        Iterator<SysCAMSPrimitivePort> li;//, li2;
//        List<SysCAMSPrimitivePort> ports, portstome;
//        String name2;
//        SysCAMSPrimitivePort port1, port2;
//        int j;
//
//        while( iterator.hasNext() ) {
//            syscamsc = iterator.next();
//            ports = syscamsc.getAllChannelsOriginPorts();
//            li = ports.listIterator();
//            while( li.hasNext() ) {
//                port1 = li.next();
//                portstome = getPortsConnectedTo( port1, components );   //this prints the ports via TraceManager
//                Iterator<SysCAMSPrimitivePort> ite = portstome.listIterator();
//                while( ite.hasNext()) {
//                    port2 = ite.next();
//                    if( !port2.isOrigin() ) {
//                        String []text1 = port1.getPortName().split( "," );
//                        String []text2 = port2.getPortName().split( "," );
//                        for( j = 0; j < Math.min( text1.length, text2.length ); j++ ) {
//                            name2 = text2[j].trim();
//                            al.add( _topname + "::" + name2 );
//                        }
//                    }
//                }
//            }
//        }
//        return new ArrayList<String>(al);
//    }
//
//    public List<String> getAllSysCAMSEventNames( String _topname ) {
//
//        List<String> al = new ArrayList<String>();
//        SysCAMSPrimitiveComponent syscamsc;
//        List<SysCAMSPrimitiveComponent> components = getPrimitiveComponentList();
//        Iterator<SysCAMSPrimitiveComponent> iterator = components.listIterator();
//        Iterator<SysCAMSPrimitivePort> li;//, li2;
//        List<SysCAMSPrimitivePort> ports, portstome;
//        String name, name1, name2;
//        SysCAMSPrimitivePort port1, port2;
//        int j;
//
//        while( iterator.hasNext() ) {
//            syscamsc = iterator.next() ;
//            ports = syscamsc.getAllEventsOriginPorts();
//            li = ports.listIterator();
//            while( li.hasNext() ) {
//                port1 = li.next();
//                portstome = getPortsConnectedTo( port1, components );
//                if ( portstome.size() == 1 ) {
//                    port2 = portstome.get(0);
//                    String []text1 = port1.getPortName().split( "," );
//                    String []text2 = port2.getPortName().split( "," );
//                    for ( j = 0; j < Math.min( text1.length, text2.length ); j++ ) {
//                        name1 = text1[j].trim();
//                        name2 = text2[j].trim();
//                        if( name1.equals( name2 ) ) {
//                            name = name1;
//                        }
//                        else {
//                            name = name1 + "__" + name2;
//                        }
//                        al.add( _topname + "::" + name );
//                    }
//                }
//            }
//        }
//        return al;
//    }

    public void getAllPortsConnectedTo( List<TGComponent> ll, SysCAMSPrimitivePort _port) {
        List<TGComponent> components = getMGUI().getAllSysCAMSComponents();
        Iterator<TGComponent> iterator = components.listIterator();
        TGComponent tgc, tgc1, tgc2;
        SysCAMSPortConnector portco;

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSPortConnector) {
                portco = (SysCAMSPortConnector)tgc;
                tgc1 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP1());
                tgc2 = getComponentToWhichBelongs(components, portco.getTGConnectingPointP2());
                if ((tgc1 != null) && (tgc2 != null)) {
                    if (tgc1 instanceof SysCAMSRemoteCompositeComponent) {
                        tgc1 = ((SysCAMSRemoteCompositeComponent)tgc1).getPortOf(portco.getTGConnectingPointP1());
                    }

                    if (tgc2 instanceof SysCAMSRemoteCompositeComponent) {
                        tgc2 = ((SysCAMSRemoteCompositeComponent)tgc2).getPortOf(portco.getTGConnectingPointP2());
                    }
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
        return "<SysCAMSComponentTaskDiagramPanel name=\"" + name + "\"" + sizeParam() + displayParam() + zoomParam() +" >";
    }

    public String getXMLTail() {
        return "</SysCAMSComponentTaskDiagramPanel>";
    }

    public String getXMLSelectedHead() {
        return "<SysCAMSComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }

    public String getXMLSelectedTail() {
        return "</SysCAMSComponentTaskDiagramPanelCopy>";
    }

    public String getXMLCloneHead() {
        return "<SysCAMSComponentTaskDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
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

        while(iterator.hasNext()) {
            tgc1 = iterator.next();

            if (tgc1 instanceof SysCAMSCompositeComponent) {
                syscamscc = (SysCAMSCompositeComponent)tgc1;
                s = _name + "::" + syscamscc.getValue();
                list.add(s);
                syscamscc.getAllCompositeComponents(list, _name);
            }
        }
        return list;
    }

    public String[] getCompOutTDF(){
        List<String> chls = new ArrayList<String>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSPrimitiveComponent) {
                SysCAMSPrimitiveComponent comp = (SysCAMSPrimitiveComponent) tgc;
                List<SysCAMSPrimitivePort> ll = comp.getAllTDFOriginPorts();
                Iterator<SysCAMSPrimitivePort> ite = ll.listIterator();
                while(ite.hasNext()) {
                    SysCAMSPrimitivePort port = ite.next();
                    chls.add(port.getPortName());
                }
            }
        }
        String[] chlArray = new String[chls.size()];
        chlArray = chls.toArray(chlArray);
        return chlArray;
    }

    public String[] getCompInTDF(){
        List<String> chls = new ArrayList<String>();
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSPrimitiveComponent) {
                SysCAMSPrimitiveComponent comp = (SysCAMSPrimitiveComponent) tgc;
                List<SysCAMSPrimitivePort> ll = comp.getAllTDFDestinationPorts();
                Iterator<SysCAMSPrimitivePort> ite = ll.listIterator();
                while(ite.hasNext()) {
                    SysCAMSPrimitivePort port = ite.next();
                    chls.add(port.getPortName());
                }
            }
        }
        String[] chlArray = new String[chls.size()];
        chlArray = chls.toArray(chlArray);
        return chlArray;
    }

    public SysCAMSPrimitiveComponent getPrimitiveComponentByName(String _name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        SysCAMSPrimitiveComponent tmp;

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSPrimitiveComponent) {
                if (tgc.getValue().equals(_name)) {
                    return ((SysCAMSPrimitiveComponent)tgc);
                }
            }
            if (tgc instanceof SysCAMSCompositeComponent) {
                tmp = ((SysCAMSCompositeComponent)tgc).getPrimitiveComponentByName(_name);
                if (tmp != null) {
                    return tmp;
                }
            }
            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                tmp = ((SysCAMSRemoteCompositeComponent)tgc).getPrimitiveComponentByName(_name);
                if (tmp != null) {
                    return tmp;
                }
            }
        }
        return null;
    }

    public void updateReferenceToSysCAMSCompositeComponent(SysCAMSCompositeComponent syscamscc) {
        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSCompositeComponent) {
                ((SysCAMSCompositeComponent)tgc).updateReferenceToSysCAMSCompositeComponent(syscamscc);
            }

            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                ((SysCAMSRemoteCompositeComponent)tgc).updateReference(syscamscc);
            }
        }
    }

    public SysCAMSCompositeComponent getCompositeComponentByName(String _name) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        SysCAMSCompositeComponent tmp;

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSCompositeComponent) {
                tmp = (SysCAMSCompositeComponent)tgc;
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
        SysCAMSPortConnector connector;
        TGComponent tgc;
        TGComponent tgc1;
        TGComponent tgc2;

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSPortConnector) {
                connector = (SysCAMSPortConnector) tgc;
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

    public void loadExtraParameters(Element elt) {
    }

    public void setConnectorsToFront() {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        List<TGComponent> list = new ArrayList<TGComponent>();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (!(tgc instanceof TGConnector)) {
                list.add(tgc);
            }
        }
        for(TGComponent tgc1: list) {
            componentList.remove(tgc1);
            componentList.add(tgc1);
        }
    }

    public void delayedLoad() {
        Iterator<TGComponent> iterator;
        TGComponent tgc;

        iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSCompositeComponent) {
                ((SysCAMSCompositeComponent)(tgc)).delayedLoad();
            }

            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                try {
                    ((SysCAMSRemoteCompositeComponent)(tgc)).delayedLoad();
                } catch (Exception e) {
                }
            }
        }
    }

    // Returns the faulty paths
    public ArrayList<SysCAMSPath> updatePorts() {
        List<SysCAMSPath> paths = makePaths();
        ArrayList<SysCAMSPath> faultyPaths = new ArrayList<SysCAMSPath>();

        // Checking rules of paths, and setting colors accordingly
        for(SysCAMSPath path: paths) {
            path.checkRules();
            if (path.hasError()) {
                TraceManager.addDev("Path error:" + path.getErrorMessage());
                faultyPaths.add(path);
            }
            path.setColor();
        }
        return faultyPaths;
    }

    public void updatePorts_oldVersion() {
        Iterator<TGComponent> iterator;
        TGComponent tgc;

        // Get all SysCAMSPrimitivePort
        List<SysCAMSCompositePort> ports = new ArrayList<SysCAMSCompositePort>();
        List<SysCAMSCompositePort> referencedports = new ArrayList<SysCAMSCompositePort>();
        List<SysCAMSPrimitivePort> pports = new ArrayList<SysCAMSPrimitivePort>();
        List<SysCAMSChannelFacility> facilities = new ArrayList<SysCAMSChannelFacility>();

        iterator = componentList.listIterator();

        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSCompositeComponent) {
                ports.addAll(((SysCAMSCompositeComponent)tgc).getAllInternalCompositePorts());
                pports.addAll(((SysCAMSCompositeComponent)tgc).getAllInternalPrimitivePorts());
                referencedports.addAll(((SysCAMSCompositeComponent)tgc).getAllReferencedCompositePorts());
            }
            if (tgc instanceof SysCAMSPrimitiveComponent) {
                pports.addAll(((SysCAMSPrimitiveComponent)tgc).getAllInternalPrimitivePorts());
            }
            if (tgc instanceof SysCAMSCompositePort) {
                ports.add((SysCAMSCompositePort)tgc);
            }
            if (tgc instanceof SysCAMSPrimitivePort) {
                pports.add((SysCAMSPrimitivePort)tgc);
            }
            if (tgc instanceof SysCAMSChannelFacility) {
                facilities.add((SysCAMSChannelFacility)tgc);
            }
        }

        // Remove All Current Links To Ports
        for(SysCAMSCompositePort port:ports) {
            if (!referencedports.contains(port)) {
                port.purge();
            }
        }

        // We take each primitive ports individually and we go thru the graph
        ArrayList<SysCAMSChannelFacility> mets = new ArrayList<SysCAMSChannelFacility>();
        TGConnector connector;
        TGConnectingPoint tp;
        String conflictMessage;

        for(SysCAMSPrimitivePort pport:pports) {
            for(int i=0; i<pport.getNbConnectingPoint(); i++) {
                tp = pport.getTGConnectingPointAtIndex(i);
                connector = findTGConnectorUsing(tp);
                if (connector != null) {
                    mets.clear();
                    conflictMessage = propagate(pport, tp, connector, mets);
                    TraceManager.addDev("Conflict=" + conflictMessage);
                    analysePorts(pport, mets, (conflictMessage != null), conflictMessage);
                } 
            }
        }
    }

    public String propagate(SysCAMSPrimitivePort pport, TGConnectingPoint tp, TGConnector connector, ArrayList<SysCAMSChannelFacility> mets) {
        TGConnectingPoint tp2;
        SysCAMSChannelFacility cp = null;
        String conflictMessage = null;
        String conflictMessageTmp;
        int outindex, inindex;

        if (tp == connector.getTGConnectingPointP1()) {
            tp2 = connector.getTGConnectingPointP2();
        } else {
            tp2 = connector.getTGConnectingPointP1();
        }

        TGComponent tgc = (TGComponent)(tp2.getFather());
        int index = tgc.getIndexOfTGConnectingPoint(tp2);

        if (tgc instanceof SysCAMSPrimitivePort) {
            return conflictMessage;
        }

        // Cycle?
        if (mets.contains(tgc)) {
            return "Connection contains a cycle";
        }
        if(tgc instanceof SysCAMSCompositePort) {
            cp = (SysCAMSChannelFacility)tgc;
            mets.add(cp);

            inindex = cp.getInpIndex();
            outindex = cp.getOutpIndex();
            // Already positionned port?
            if (pport.isOrigin()) {
                if (cp.getOutPort() != null) {
                    if (pport.getPortType() != 2) {
                        conflictMessage = "Conflicting ports types";
                    } else {
                        if (cp.getOutPort().getPortType() != 2) {
                            conflictMessage = "More than two sending non-request ports ";
                        } else {
                            if ((outindex<5 && index>4) || (outindex>4 && index<5)) {
                                conflictMessage = "Sending ports on both side of a composite port";
                            }
                        }
                    }
                } else {
                    if (inindex > -1) {
                        if ((inindex<5 && index<5) || (inindex>4 && index>4)) {
                            conflictMessage = "Sending and receiving ports on the same side of a composite port";
                        }
                    }
                    cp.setOutPort(pport);
                    cp.setOutpIndex(index);
                }
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                if (cp.getInPort() != null) {
                    conflictMessage = "More than two receiving ports ";
                } else {
                    if (outindex > -1) {
                        if ((index<5 && outindex<5) || (index>4 && outindex>4)) {
                            conflictMessage = "Sending and receiving ports on the same side of a composite port";
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
        } else if(tgc instanceof SysCAMSFork) {
            // Only one out, more than one in is ok
            // No SysCAMSJoin
            cp = (SysCAMSChannelFacility)tgc;
            mets.add(cp);

            // Checks that "mets" contains no SysCAMSJoin
            for(SysCAMSChannelFacility met: mets) {
                if (met instanceof SysCAMSJoin) {
                    conflictMessage = "Join and Fork operators are mixed in the same channel";
                    conflictMessageTmp = explore(pport, tp2, cp, mets);
                    if (conflictMessageTmp != null) {
                        conflictMessage = conflictMessageTmp;
                    }
                    return conflictMessage;
                }
            }

            if (pport.isOrigin()) {
                if ((cp.getInPort() != null) && (cp.getInPort() != pport)) {
                    conflictMessage = "More than two sending ports  in a fork architecture";
                }
                cp.setInPort(pport);
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                conflictMessage = explore(pport, tp2, cp, mets);
            }
        } else if(tgc instanceof SysCAMSJoin) {
            // Only one out, more than one in is ok
            // No SysCAMSFork
            cp = (SysCAMSChannelFacility)tgc;
            mets.add(cp);

            // Checks that "mets" contains no SysCAMSJoin
            for(SysCAMSChannelFacility met: mets) {
                if (met instanceof SysCAMSFork) {
                    conflictMessage = "Fork and Join operators are mixed in the same channel";
                    conflictMessageTmp = explore(pport, tp2, cp, mets);
                    if (conflictMessageTmp != null) {
                        conflictMessage = conflictMessageTmp;
                    }
                    return conflictMessage;
                }
            }

            if (!pport.isOrigin()) {
                if ((cp.getOutPort() != null) && (cp.getOutPort() != pport)) {
                    conflictMessage = "More than two receiving ports in a join architecture";
                }
                cp.setOutPort(pport);
                conflictMessageTmp = explore(pport, tp2, cp, mets);
                if (conflictMessageTmp != null) {
                    conflictMessage = conflictMessageTmp;
                }
            } else {
                conflictMessage = explore(pport, tp2, cp, mets);
            }
        }
        if (cp != null) {
            if ((cp.getInPort() != null) && (cp.getOutPort() != null)){
                if (cp.getInPort().getType() != cp.getOutPort().getType()) {
                    conflictMessage = "Ports are not compatible";
                } else {
                    TraceManager.addDev("ports of " + cp + " are compatible out=" + cp.getOutPort().getType() + " in=" + cp.getInPort().getType());
                }
            }
        }
        return conflictMessage;
    }

    public String explore(SysCAMSPrimitivePort pport, TGConnectingPoint _tp, SysCAMSChannelFacility cp, ArrayList<SysCAMSChannelFacility> mets) {
        String conflictMessage = null;
        String conflictMessageTmp;
        TGConnectingPoint tp;
        TGConnector connector;

        for(int i=0; i<cp.getNbConnectingPoint(); i++) {
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

    public void analysePorts(SysCAMSPrimitivePort pport, ArrayList<SysCAMSChannelFacility> mets, boolean conflict, String message) {
        if (mets.size() == 0) {
            return;
        }
        for(SysCAMSChannelFacility port: mets) {
            port.setConflict(conflict, message);
        }
    }

    public List<String> getAllSysCAMSTaskNames(String _topname) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        List<String> list = new ArrayList<String>();
        while(iterator.hasNext()) {
            tgc = iterator.next();

            if (tgc instanceof SysCAMSPrimitiveComponent) {
                list.add(_topname + "::" + tgc.getValue());
            }
        }
        return list;
    }

    public Vector<String> getAllSysCAMSTasksAttributes() {
        TGComponent tgc;
        Iterator<TGComponent> iterator = componentList.listIterator();
        Vector<String> list = new Vector<String>();

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if( tgc instanceof SysCAMSCompositeComponent ) {
                for( SysCAMSPrimitiveComponent primComp: ((SysCAMSCompositeComponent)tgc).getAllPrimitiveComponents() ) {
                    for( Object o: primComp.getAttributeList() )   {
                        String s = o.toString();
                        list.add( primComp.getValue() + "." + s.substring( 2, s.length()-1 ) );
                    }
                }
            }
        }
        return list;
    }

    public Vector<String> getAllRecords(SysCAMSPrimitiveComponent tgc) {
        Vector<String> list = new Vector<String>();
        getAllRecords((SysCAMSCompositeComponent)(tgc.getFather()), list);
        return list;
    }

    public void getAllRecords(SysCAMSCompositeComponent comp,  Vector<String> list) {
        TGComponent tgc;
        if (comp == null) {
            Iterator<TGComponent> iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = iterator.next();
                if (tgc instanceof SysCAMSRecordComponent) {
                    list.add(tgc.getValue());
                }
            }
            return;
        }

        for(int i=0; i<comp.getNbInternalTGComponent(); i++) {
            tgc = comp.getInternalTGComponent(i);
            if (tgc instanceof SysCAMSRecordComponent) {
                list.add(tgc.getValue());
            }
        }
        getAllRecords((SysCAMSCompositeComponent)(comp.getFather()), list);
    }

    public SysCAMSRecordComponent getRecordNamed(SysCAMSPrimitiveComponent tgc, String _nameOfRecord) {
        return getRecordNamed((SysCAMSCompositeComponent)(tgc.getFather()), _nameOfRecord);
    }

    public SysCAMSRecordComponent getRecordNamed(SysCAMSCompositeComponent comp,  String _nameOfRecord) {
        TGComponent tgc;
        if (comp == null) {
            Iterator<TGComponent> iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = iterator.next();
                if (tgc instanceof SysCAMSRecordComponent) {
                    if (tgc.getValue().compareTo(_nameOfRecord) == 0) {
                        return (SysCAMSRecordComponent)tgc;
                    }
                }
            }
            return null;
        }
        for(int i=0; i<comp.getNbInternalTGComponent(); i++) {
            tgc = comp.getInternalTGComponent(i);
            if (tgc instanceof SysCAMSRecordComponent) {
                if (tgc.getValue().compareTo(_nameOfRecord) == 0) {
                    return (SysCAMSRecordComponent)tgc;
                }
            }
        }
        return getRecordNamed((SysCAMSCompositeComponent)(comp.getFather()), _nameOfRecord);
    }

    public void findAllReferencedPanels( List<SysCAMSComponentTaskDiagramPanel> panels) {
        if (panels.contains(this)) {
            return;
        }
        panels.add(this);

        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                SysCAMSRemoteCompositeComponent remote = (SysCAMSRemoteCompositeComponent)tgc;
                TDiagramPanel panel = remote.getReferencedDiagram();
                if ((panel != null) && (panel instanceof SysCAMSComponentTaskDiagramPanel)){
                    ((SysCAMSComponentTaskDiagramPanel)panel).findAllReferencedPanels(panels);
                }
            }
            if (tgc instanceof SysCAMSCompositeComponent) {
                //We must find all panels referencing this component
                panels.addAll(mgui.getAllPanelsReferencingSysCAMSCompositeComponent((SysCAMSCompositeComponent)tgc));
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

        // Go through the component list of all panels, and make paths. Then, go thru connectors,
        // and merge paths until nomore merging is possible
        for (TDiagramPanel panel: panels) {
            iterator = panel.getComponentList().listIterator();
            List<SysCAMSCompositePort> listcp;
            List<SysCAMSPrimitivePort> listpp;

            while(iterator.hasNext()) {
                tgc = iterator.next();

                if (tgc instanceof SysCAMSCompositeComponent) {
                    listcp = ((SysCAMSCompositeComponent)tgc).getAllInternalCompositePorts();
                    for(SysCAMSCompositePort cp: listcp) {
                        addToPaths(paths, cp);
                    }
                    listpp = ((SysCAMSCompositeComponent)tgc).getAllInternalPrimitivePorts();
                    for(SysCAMSPrimitivePort pp: listpp) {
                        addToPaths(paths, pp);
                    }
                }
                if (tgc instanceof SysCAMSPrimitiveComponent) {
                    listpp = ((SysCAMSPrimitiveComponent)tgc).getAllInternalPrimitivePorts();
                    for(SysCAMSPrimitivePort pp: listpp) {
                        addToPaths(paths, pp);
                    }
                }
                if (tgc instanceof SysCAMSPrimitivePort) {
                    addToPaths(paths, tgc);
                }
                if (tgc instanceof SysCAMSChannelFacility) {
                    addToPaths(paths, tgc);
                }
            }
        }

        // Use connectors to merge paths with one another
        for (TDiagramPanel panel: panels) {
            iterator = panel.getComponentList().listIterator();
            SysCAMSPortConnector connector;
            TGComponent tgc1, tgc2;
            SysCAMSPath path1, path2;

            while(iterator.hasNext()) {
                tgc = iterator.next();
                if (tgc instanceof SysCAMSPortConnector) {
                    connector = (SysCAMSPortConnector)tgc;
                    if (connector.getTGConnectingPointP1().getFather() instanceof TGComponent) {
                        tgc1 = (TGComponent)(connector.getTGConnectingPointP1().getFather());
                    } else {
                        tgc1 = null;
                    }
                    if (connector.getTGConnectingPointP2().getFather() instanceof TGComponent) {
                        tgc2 = (TGComponent)(connector.getTGConnectingPointP2().getFather());
                    } else {
                        tgc2 = null;
                    }
                    if (tgc1 instanceof SysCAMSRemoteCompositeComponent) {
                        tgc1 = ((SysCAMSRemoteCompositeComponent)tgc1).getPortOf(connector.getTGConnectingPointP1());
                    }
                    if (tgc2 instanceof SysCAMSRemoteCompositeComponent) {
                        tgc2 = ((SysCAMSRemoteCompositeComponent)tgc2).getPortOf(connector.getTGConnectingPointP2());
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
        return paths;
    }

    public SysCAMSPath getPathOf( List<SysCAMSPath> paths, TGComponent tgc) {
        for(SysCAMSPath path: paths) {
            if (path.contains(tgc)) {
                return path;
            }
        }
        return null;
    }

    public void addToPaths( List<SysCAMSPath> paths, TGComponent tgc) {
        for(SysCAMSPath path: paths) {
            if (path.contains(tgc)) {
                return;
            }
        }
        // Create a new path
        SysCAMSPath ph = new SysCAMSPath();
        ph.addComponent(tgc);
        paths.add(ph);
    }


    public void getPanelsUsingAComponent(SysCAMSCompositeComponent syscamscc, ArrayList<SysCAMSComponentTaskDiagramPanel> panels) {
        Iterator<TGComponent> iterator = componentList.listIterator();
        TGComponent tgc;

        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof SysCAMSCompositeComponent) {
                if (((SysCAMSCompositeComponent)tgc).hasReferencesTo(syscamscc)) {
                    panels.add(this);
                    return;
                }
            }
            if (tgc instanceof SysCAMSRemoteCompositeComponent) {
                if (((SysCAMSRemoteCompositeComponent)tgc).getReference() == syscamscc) {
                    panels.add(this);
                    return;
                }
            }
        }
    }

    public String[] getAllOutDE(String nameOfComponent) {
        SysCAMSPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }
        List<SysCAMSPrimitivePort> ll = comp.getAllDEOriginPorts();
        String[]terms = new String[ll.size()];
        Iterator<SysCAMSPrimitivePort> ite = ll.listIterator();
        int i = 0;

        while(ite.hasNext()) {
            SysCAMSPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i ++;
        }
        return terms;
    }

    public String[] getAllInDE(String nameOfComponent) {
        SysCAMSPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }
        List<SysCAMSPrimitivePort> ll = comp.getAllDEDestinationPorts();
        String[]terms = new String[ll.size()];
        ListIterator<SysCAMSPrimitivePort> ite = ll.listIterator();
        int i = 0;
        while(ite.hasNext()) {
            SysCAMSPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i ++;
        }
        return terms;
    }

    public String[] getAllOutTDF(String nameOfComponent) {
        SysCAMSPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }
        List<SysCAMSPrimitivePort> ll = comp.getAllTDFOriginPorts();
        String[]terms = new String[ll.size()];
        Iterator<SysCAMSPrimitivePort> ite = ll.listIterator();
        int i = 0;
        while(ite.hasNext()) {
            SysCAMSPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }
        return terms;
    }

    public String[] getAllInTDF(String nameOfComponent) {
        SysCAMSPrimitiveComponent comp = getPrimitiveComponentByName(nameOfComponent);
        if (comp == null) {
            return null;
        }

        List<SysCAMSPrimitivePort> ll = comp.getAllTDFDestinationPorts();
        String[]terms = new String[ll.size()];
        Iterator<SysCAMSPrimitivePort> ite = ll.listIterator();
        int i = 0;

        while(ite.hasNext()) {
            SysCAMSPrimitivePort port = ite.next();
            terms[i] = port.getPortName();
            i++;
        }
        return terms;
    }
}
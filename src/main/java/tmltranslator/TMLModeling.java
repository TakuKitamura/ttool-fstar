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

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarPragmaAuthenticity;
import avatartranslator.AvatarPragmaSecret;
import avatartranslator.AvatarPragmaReachability;
import myutil.Conversion;
import myutil.TraceManager;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import proverifspec.ProVerifResultTrace;
//import ui.tmlcompd.TMLCPrimitivePort;
import ui.TGComponent;

import java.util.*;

/**
 * Class TMLModeling Creation: 21/11/2005
 * 
 * @version 1.0 21/11/2005
 * @author Ludovic APVRILLE
 */
public class TMLModeling<E> {
    public final String SEP1 = "_S_";

    private List<TMLTask> tasks;
    private List<TMLChannel> channels;
    private List<TMLRequest> requests;
    private List<TMLEvent> events;
    private List<String> pragmas;

    private TMLElement correspondance[]; // Link to graphical components

    // Security
    public List<String> securityPatterns;
    public List<SecurityPattern> secPatterns;
    private boolean optimized = false;
    public Map<String, List<String>> secChannelMap;
    public Map<SecurityPattern, List<TMLTask>> securityTaskMap;

    private String[] ops = { ">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", "," };

    private Map<E, String> checkedActivities;

    private int hashCode;
    private boolean hashCodeComputed = false;

    public TMLModeling() {
        init();
    }

    public TMLModeling(boolean reset) {
        init();
        if (reset) {
            DIPLOElement.resetID();
        }
    }

    public void addSecurityPattern(SecurityPattern sp) {
        if (!secPatterns.contains(sp)) {
            secPatterns.add(sp);
        }
    }

    public void addCheckedActivity(E tgc, String s) {
        checkedActivities.put(tgc, s);
    }

    public Map<E, String> getCheckedComps() {
        return checkedActivities;
    }

    public SecurityPattern getSecurityPattern(String s) {
        for (SecurityPattern sp : secPatterns) {
            if (sp.name.equals(s)) {
                return sp;
            }
        }
        return null;
    }

    public TMLMapping<E> getDefaultMapping() {
        TMLMapping<E> tmlmapping;
        tmlmapping = new TMLMapping<>(this, new TMLArchitecture(), false);
        tmlmapping.makeMinimumMapping();
        return tmlmapping;
    }

    private void init() {

        tasks = new ArrayList<TMLTask>();
        channels = new ArrayList<TMLChannel>();
        events = new ArrayList<TMLEvent>();
        requests = new ArrayList<TMLRequest>();
        pragmas = new ArrayList<String>();

        securityPatterns = new ArrayList<String>();
        secPatterns = new ArrayList<SecurityPattern>();

        secChannelMap = new HashMap<String, List<String>>();
        securityTaskMap = new HashMap<SecurityPattern, List<TMLTask>>();

        checkedActivities = new HashMap<>();
    }

    public void addTask(TMLTask task) {
        tasks.add(task);
    }

    public void addPragma(String s) {
        pragmas.add(s);
    }

    public void addChannel(TMLChannel channel) {
        channels.add(channel);
    }

    public void addRequest(TMLRequest request) {
        requests.add(request);
    }

    public void addEvent(TMLEvent event) {
        events.add(event);
    }

    public boolean checkConsistency() {
        return true;
    }

    public void removeAllChannels() {
        channels.clear();
    }

    public TMLTask findTMLTask(TMLActivityElement _elt) {
        TMLTask tmp;
        for (int i = 0; i < tasks.size(); i++) {
            tmp = tasks.get(i);
            if (tmp.has(_elt)) {
                return tmp;
            }
        }
        return null;
    }

    private void computeHashCode() {
        TMLTextSpecification<E> tmltxt = new TMLTextSpecification<>("spec.tml");
        String s = tmltxt.toTextFormat(this);

        int index = s.indexOf("// Channels");
        if (index != -1) {
            s = s.substring(index, s.length());
        }
        hashCode = s.hashCode();
        // TraceManager.addDev("TML hashcode = " + hashCode);
    }

    public int getHashCode() {
        if (!hashCodeComputed) {
            computeHashCode();
            hashCodeComputed = true;
        }
        return hashCode;
    }

    public String toString() {
        String s = tasksToString();
        s += channelsToString();
        s += eventsToString();
        s += requestsToString();
        return s;
    }

    public String getStringListCommunicationElements() {
        String s = channelsToString();
        s += eventsToString();
        s += requestsToString();
        return s;
    }

    public String tasksToString() {
        String s = "Tasks: ";
        return s + listToString(tasks) + "\n";
    }

    public String channelsToString() {
        String s = "Channels: ";
        return s + listToString(channels) + "\n";
    }

    public String eventsToString() {
        String s = "Events: ";
        return s + listToString(events) + "\n";
    }

    public String requestsToString() {
        String s = "Requests: ";
        return s + listToString(requests) + "\n";
    }

    public String listToString(List<? extends TMLElement> ll) {
        String s = "";
        Iterator<? extends TMLElement> iterator = ll.listIterator();
        TMLElement tmle;

        while (iterator.hasNext()) {
            tmle = iterator.next();
            s += tmle.getName() + " ";
        }

        return s;
    }

    public boolean hasSameChannelName(TMLChannel _channel) {
        TMLChannel channel;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            channel = iterator.next();

            if (channel.getName().compareTo(_channel.getName()) == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAlmostSimilarChannel(TMLChannel _channel) {
        TMLChannel channel;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            channel = iterator.next();

            if (channel.getName().compareTo(_channel.getName()) == 0) {
                if (channel.getSize() != _channel.getSize()) {
                    return true;
                }
                if (channel.getType() != _channel.getType()) {
                    return true;
                }

                if (channel.getOriginTask() != _channel.getOriginTask()) {
                    return true;
                }

                if (channel.getDestinationTask() != _channel.getDestinationTask()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasSameRequestName(TMLRequest _request) {
        TMLRequest request;
        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            request = iterator.next();
            if (request != _request) {
                if (request.getName().compareTo(_request.getName()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public TMLRequest getRequestNamed(String name) {
        TMLRequest request;
        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            request = iterator.next();
            // TraceManager.addDev("Request=" +request.getName() + " name=" + name);
            if (request.getName().compareTo(name) == 0) {
                return request;
            }
        }
        return null;
    }

    // Returns a similar request if found
    public TMLRequest hasSimilarRequest(TMLRequest _request) {
        TMLRequest request;
        int i;

        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            request = iterator.next();

            if (request.getName().compareTo(_request.getName()) == 0) {
                // must verify whether a param is different or not.
                if (request.getNbOfParams() != _request.getNbOfParams()) {
                    TraceManager.addDev(" *** -> nb of params is different");
                    return null;
                }

                for (i = 0; i < request.getNbOfParams(); i++) {
                    if (request.getType(i).getType() != _request.getType(i).getType()) {
                        TraceManager.addDev("**** -> Params #" + i + " type");
                        return null;
                    }
                }

                if (request.getDestinationTask() != _request.getDestinationTask()) {
                    TraceManager.addDev("**** ->Destination task: found=" + request.getDestinationTask().getName()
                            + " provided=" + _request.getDestinationTask().getName());
                    return null;
                }

                return request;
            }
        }
        return null;
    }

    public boolean hasSameEventName(TMLEvent _event) {
        TMLEvent event;
        Iterator<TMLEvent> iterator = events.listIterator();

        while (iterator.hasNext()) {
            event = iterator.next();

            if (event.getName().compareTo(_event.getName()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAlmostSimilarEvent(TMLEvent _event) {
        TMLEvent event;
        int i;

        Iterator<TMLEvent> iterator = events.listIterator();

        while (iterator.hasNext()) {
            event = iterator.next();
            if (event.getName().compareTo(_event.getName()) == 0) {
                // must verify whether a param is different or not.
                if (event.getNbOfParams() != _event.getNbOfParams()) {
                    return true;
                }

                for (i = 0; i < event.getNbOfParams(); i++) {
                    if (event.getType(i).getType() != _event.getType(i).getType()) {
                        return true;
                    }
                }

                // Must check for origin and destination task
                if (event.getOriginTask() != _event.getOriginTask()) {
                    return true;
                }
                if (event.getDestinationTask() != _event.getDestinationTask()) {
                    return true;
                }
            }
        }
        return false;
    }

    public TMLTask getTMLTaskByName(String _name) {
        TMLTask task;
        Iterator<TMLTask> iterator = tasks.listIterator();

        while (iterator.hasNext()) {
            task = iterator.next();

            if (task.getName().compareTo(_name) == 0) {
                return task;
            }
        }
        return null;
    }

    public String[] getTasksIDs() {
        if (tasks == null) {
            return null;
        }

        String[] list = new String[tasks.size()];
        TMLTask task;
        Iterator<TMLTask> iterator = tasks.listIterator();
        int cpt = 0;

        while (iterator.hasNext()) {
            task = iterator.next();
            list[cpt] = task.getName() + " (" + task.getID() + ")";
            cpt++;
        }
        return list;
    }

    public String[] getChanIDs() {
        if (channels == null) {
            return null;
        }

        String[] list = new String[channels.size()];
        TMLChannel ch;
        Iterator<TMLChannel> iterator = channels.listIterator();
        int cpt = 0;

        while (iterator.hasNext()) {
            ch = iterator.next();
            list[cpt] = ch.getName() + " (" + ch.getID() + ")";
            cpt++;
        }

        return list;
    }

    public String[] makeCommandIDs(int index) {
        if (tasks == null) {
            return null;
        }

        TMLTask task = tasks.get(index);
        if (task != null) {
            return task.makeCommandIDs();
        }

        return null;
    }

    public String[] makeVariableIDs(int index) {
        if (tasks == null) {
            return null;
        }

        TMLTask task = tasks.get(index);
        if (task != null) {
            return task.makeVariableIDs();
        }

        return null;
    }

    public TMLTask getTMLTaskByCommandID(int id) {
        TMLTask task;
        Iterator<TMLTask> iterator = tasks.listIterator();

        while (iterator.hasNext()) {
            task = iterator.next();

            if (task.hasCommand(id)) {
                return task;
            }
        }

        return null;
    }

    public TMLElement getCommunicationElementByName(String _name) {
        TMLChannel ch = getChannelByName(_name);
        if (ch != null) {
            return ch;
        }

        TMLEvent evt = getEventByName(_name);
        if (evt != null) {
            return evt;
        }

        return getRequestByName(_name);
    }

    public TMLChannel getChannelByName(String _name) {
        TMLChannel ch;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            ch = iterator.next();
            if (ch.getName().compareTo(_name) == 0) {
                return ch;
            }
        }

        return null;
    }

    public TMLChannel getChannelByShortName(String _name) {
        TMLChannel ch;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            ch = iterator.next();
            if (ch.getName().endsWith(_name)) {
                return ch;
            }
        }

        return null;
    }

    public TMLChannel getChannelByDestinationPortName(String _portName) {
        TMLChannel ch;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            ch = iterator.next();

            if (ch.hasDestinationPort(_portName) != null) {
                return ch;
            }
        }

        return null;
    }

    public TMLChannel getChannelByOriginPortName(String _portName) {
        TMLChannel ch;
        Iterator<TMLChannel> iterator = channels.listIterator();

        while (iterator.hasNext()) {
            ch = iterator.next();

            if (ch.hasOriginPort(_portName) != null) {
                return ch;
            }
        }

        return null;
    }

    public TMLEvent getEventByName(String _name) {
        TMLEvent evt;
        Iterator<TMLEvent> iterator = events.listIterator();

        while (iterator.hasNext()) {
            evt = iterator.next();

            if (evt.getName().compareTo(_name) == 0) {
                return evt;
            }
        }

        return null;
    }

    public TMLEvent getEventByShortName(String _name) {
        TMLEvent evt;
        Iterator<TMLEvent> iterator = events.listIterator();

        while (iterator.hasNext()) {
            evt = iterator.next();
            if (evt.getName().endsWith(_name)) {
                return evt;
            }
        }

        return null;
    }

    public TMLRequest getRequestByShortName(String _name) {
        TMLRequest req;
        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            req = iterator.next();

            if (req.getName().endsWith(_name)) {
                return req;
            }
        }

        return null;
    }

    public TMLRequest getRequestByName(String _name) {
        TMLRequest req;
        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            req = iterator.next();

            if (req.getName().compareTo(_name) == 0) {
                return req;
            }
        }

        return null;
    }

    public TMLRequest getRequestByDestinationTask(TMLTask tmlt) {
        if (tmlt == null) {
            return null;
        }

        TMLRequest req;
        Iterator<TMLRequest> iterator = requests.listIterator();

        while (iterator.hasNext()) {
            req = iterator.next();

            if (req.getDestinationTask() == tmlt) {
                return req;
            }
        }

        return null;
    }

    public List<TMLElement> getAllElementsWithName(String name) {
        Vector<TMLElement> elts = new Vector<>();

        for (TMLTask task : tasks) {
            if (task.getName().compareTo(name) == 0) {
                elts.add(task);
            }
        }

        for (TMLChannel ch : channels) {
            if (ch.getName().compareTo(name) == 0) {
                elts.add(ch);
            }
        }

        for (TMLEvent evt : events) {
            if (evt.getName().compareTo(name) == 0) {
                elts.add(evt);
            }
        }

        for (TMLRequest req : requests) {
            if (req.getName().compareTo(name) == 0) {
                elts.add(req);
            }
        }

        return elts;
    }

    public List<TMLTask> getTasks() {
        return tasks;
    }

    public List<TMLTask> getAttackerTasks() {
        List<TMLTask> attackers = new ArrayList<TMLTask>();
        for (TMLTask task : tasks) {
            if (task.isAttacker()) {
                attackers.add(task);
            }
        }
        return attackers;
    }

    public List<String> getPragmas() {
        return pragmas;
    }

    public Iterator<TMLTask> getListIteratorTasks() {
        return tasks.listIterator();
    }

    public List<TMLChannel> getChannels() {
        return channels;
    }

    public List<TMLEvent> getEvents() {
        return events;
    }

    public List<TMLRequest> getRequests() {
        return requests;
    }

    public Iterator<TMLChannel> getListIteratorChannels() {
        return channels.listIterator();
    }

    public Iterator<TMLEvent> getListIteratorEvents() {
        return events.listIterator();
    }

    public Iterator<TMLRequest> getListIteratorRequests() {
        return requests.listIterator();
    }

    public List<TMLChannel> getChannels(TMLTask t) {
        TMLChannel ch;
        List<TMLChannel> list = new ArrayList<TMLChannel>();
        Iterator<TMLChannel> iterator = getListIteratorChannels();

        while (iterator.hasNext()) {
            ch = iterator.next();
            if ((ch.getOriginTask() == t) || (ch.getDestinationTask() == t)) {
                list.add(ch);
            }
            for (TMLTask task : ch.getOriginTasks()) {
                if (task == t) {
                    list.add(ch);
                }
            }
            for (TMLTask task : ch.getDestinationTasks()) {
                if (task == t) {
                    list.add(ch);
                }
            }
        }

        return list;
    }

    public List<TMLChannel> getChannels(TMLTask originTask, TMLTask destTask) {
        TMLChannel ch;
        List<TMLChannel> list = new ArrayList<TMLChannel>();
        Iterator<TMLChannel> iterator = getListIteratorChannels();

        while (iterator.hasNext()) {
            ch = iterator.next();

            if ((ch.getOriginTask() == originTask) && (ch.getDestinationTask() == destTask)) {
                list.add(ch);
            }
        }

        return list;
    }

    public void backtrace(ProVerifOutputAnalyzer pvoa, String mappingName) {
        // TraceManager.addDev("Backtracing Confidentiality");
        Map<AvatarPragmaSecret, ProVerifQueryResult> confResults = pvoa.getConfidentialityResults();

        for (AvatarPragmaSecret pragma : confResults.keySet()) {
            // System.out.println("pragma " +pragma);
            ProVerifQueryResult result = confResults.get(pragma);
            if (!result.isProved())
                continue;
            int r = result.isSatisfied() ? 2 : 3;

            AvatarAttribute attr = pragma.getArg();

            TMLChannel channel = getChannelByShortName(attr.getName().replaceAll("_chData", ""));
            boolean invalidate = false;
            if (channel == null) {
                channel = getChannelByOriginPortName(attr.getName().replaceAll("_chData", ""));
            }
            // If an attribute is confidential because it has never been sent on that
            // channel, do not backtrace that result since it is misleading
            if (channel != null) {
                // Mark the result only if the writechannel operator is reachable
                Map<AvatarPragmaReachability, ProVerifQueryResult> reachResults = pvoa.getReachabilityResults();
                if (reachResults.size() > 0) {
                    for (AvatarPragmaReachability reachPragma : reachResults.keySet()) {
                        if (reachPragma.getState().getName()
                                .equals("aftersignalstate_reachannel_" + channel.getName())) {
                            if (!reachResults.get(reachPragma).isSatisfied()) {
                                invalidate = true;
                            }
                        }
                    }
                }
                // Next check if there exists a writechannel operator that sends unencrypted
                // data
                boolean found = false;
                for (TMLTask task : getTasks()) {
                    TMLActivity act = task.getActivityDiagram();
                    for (TMLActivityElement elem : act.getElements()) {
                        if (elem instanceof TMLWriteChannel) {
                            TMLWriteChannel wr = (TMLWriteChannel) elem;
                            if (wr.getChannel(0).getName().equals(channel.getName())) {
                                if (wr.securityPattern == null) {
                                    found = true;
                                }
                            }
                        }
                    }
                }
                if (!found) {
                    invalidate = true;
                }
                for (TMLPortWithSecurityInformation port : channel.ports) {
                    if (port.getCheckConf() && !invalidate) {
                        port.setConfStatus(r);
                        port.setMappingName(mappingName);
                        // Add Result Trace also
                        ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                        if (trace != null && port.isOrigin()) {
                            port.setResultTrace(trace);
                            port.setPragmaString(pragma.toString());
                        }
                    }
                }
            }
            TMLRequest req = getRequestByName(attr.getName().replaceAll("_reqData", ""));
            if (req != null) {
                for (TMLPortWithSecurityInformation port : req.ports) {
                    if (port.getCheckConf()) {
                        port.setConfStatus(r);
                        port.setMappingName(mappingName);
                    }
                }
            }
            TMLEvent ev = getEventByName(attr.getName().replaceAll("_eventData", ""));
            if (ev != null) {
                if (ev.port.checkConf) {
                    ev.port.checkConfStatus = r;
                    ev.port.mappingName = mappingName;
                }
                if (ev.port2.checkConf) {
                    ev.port2.checkConfStatus = r;
                    ev.port2.mappingName = mappingName;
                }
            }

            List<String> channels = secChannelMap.get(attr.getName());
            if (channels != null) {
                for (String channelName : channels) {
                    channel = getChannelByShortName(channelName);
                    if (channel != null) {
                        for (TMLPortWithSecurityInformation port : channel.ports) {
                            if (port.getCheckConf()) {
                                port.setConfStatus(r);
                                port.setSecName(attr.getName());
                            }
                        }
                    }
                }
            }

        }
        //
    }

    public TGComponent getTGComponent() {
        return (TGComponent) getTasks().get(0).getReferenceObject();
    }

    public void backtraceAuthenticity(ProVerifOutputAnalyzer pvoa, String mappingName) {
        // TraceManager.addDev("Backtracing Authenticity");
        Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authenticityResults = pvoa.getAuthenticityResults();
        for (AvatarPragmaAuthenticity pragma : authenticityResults.keySet()) {
            ProVerifQueryAuthResult result = authenticityResults.get(pragma);
            // TODO: deal directly with pragma instead of s
            String s = pragma.getAttrB().getAttribute().getBlock().getName() + "__"
                    + pragma.getAttrB().getAttribute().getName() + "__" + pragma.getAttrB().getState().getName()
                    + " ==> " + pragma.getAttrA().getAttribute().getBlock().getName() + "__"
                    + pragma.getAttrA().getAttribute().getName() + "__" + pragma.getAttrA().getState().getName();

            if (result.isProved() && !result.isSatisfied()) {
                String signalName = s.toString().split("_chData")[0];
                signalName = signalName.split("__")[signalName.split("__").length - 1];

                TMLChannel channel = getChannelByShortName(signalName);

                if (channel == null) {
                    channel = getChannelByDestinationPortName(signalName);
                }
                if (channel != null) {
                    for (TMLPortWithSecurityInformation port : channel.ports) {
                        if (port.getCheckAuth()) {
                            port.setStrongAuthStatus(3);
                            port.setMappingName(mappingName);
                            ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                            if (trace != null && !port.isOrigin()) {
                                port.setResultTrace(trace);
                                port.setPragmaString(pragma.toString());
                            }
                        }
                    }
                }
                signalName = s.toString().split("_reqData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLRequest req = getRequestByName(signalName);
                if (req != null) {
                    for (TMLPortWithSecurityInformation port : req.ports) {
                        if (port.getCheckAuth()) {
                            port.setStrongAuthStatus(3);
                            port.setMappingName(mappingName);
                        }
                    }
                }
                signalName = s.toString().split("_eventData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLEvent ev = getEventByName(signalName);
                if (ev != null) {
                    if (ev.port.checkAuth) {
                        ev.port.checkStrongAuthStatus = 3;
                        ev.port2.mappingName = mappingName;
                    }
                    if (ev.port2.checkAuth) {
                        ev.port2.checkStrongAuthStatus = 3;
                        ev.port2.mappingName = mappingName;
                    }
                }

                signalName = s.split("__decrypt")[0];
                List<String> channels = secChannelMap.get(signalName);
                if (channels != null) {
                    for (String channelName : channels) {
                        channel = getChannelByShortName(channelName);
                        if (channel != null) {
                            for (TMLPortWithSecurityInformation port : channel.ports) {
                                if (port.getCheckAuth() && port.getCheckStrongAuthStatus() == 1) {
                                    port.setStrongAuthStatus(3);
                                    TraceManager.addDev("not verified " + signalName);
                                    port.setSecName(signalName);
                                    ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                    if (trace != null && !port.isOrigin()) {
                                        port.setResultTrace(trace);
                                        port.setPragmaString(pragma.toString());
                                    }
                                }
                            }
                        }
                    }
                }

                // In case of HSM
                signalName = s.split("__decrypt")[0];
                signalName = signalName.split("__")[1];
                channels = secChannelMap.get(signalName);
                if (channels != null) {
                    for (String channelName : channels) {
                        if (channelName.contains("retData_") || channelName.contains("data_")) {
                            channelName = channelName.replaceAll("retData_", "").replaceAll("data_", "");
                            // String header= channelName.split("__retData_")[0];
                            for (TMLTask t : getTasks()) {
                                if (channelName.contains(t.getName().split("__")[1])) {
                                    channelName = channelName.replace("_" + t.getName().split("__")[1], "");
                                }
                            }
                            channel = getChannelByShortName(channelName);
                            if (channel != null) {
                                for (TMLPortWithSecurityInformation port : channel.ports) {
                                    if (port.getCheckAuth()) {
                                        port.setWeakAuthStatus(3);
                                        port.setSecName(signalName);
                                        ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                        if (trace != null && !port.isOrigin()) {
                                            port.setResultTrace(trace);
                                            port.setPragmaString(pragma.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (result.isWeakProved() && result.isWeakSatisfied()) {
                String signalName = s.split("_chData")[0];
                signalName = signalName.split("__")[1];
                TMLChannel channel = getChannelByShortName(signalName);
                if (channel != null) {
                    for (TMLPortWithSecurityInformation port : channel.ports) {
                        if (port.getCheckAuth()) {
                            port.setWeakAuthStatus(2);
                            port.setMappingName(mappingName);
                            ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                            if (trace != null && !port.isOrigin()) {
                                port.setResultTrace(trace);
                                port.setPragmaString(pragma.toString());
                            }
                        }
                    }
                }
                signalName = s.split("_reqData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLRequest req = getRequestByName(signalName);
                if (req != null) {
                    for (TMLPortWithSecurityInformation port : req.ports) {
                        if (port.getCheckAuth()) {
                            port.setWeakAuthStatus(2);
                            port.setMappingName(mappingName);
                        }
                    }
                }
                signalName = s.split("__eventData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLEvent ev = getEventByName(signalName);
                if (ev != null) {
                    if (ev.port.checkAuth) {
                        ev.port.checkWeakAuthStatus = 2;
                        ev.port2.mappingName = mappingName;
                    }
                    if (ev.port2.checkAuth) {
                        ev.port2.checkWeakAuthStatus = 2;
                        ev.port2.mappingName = mappingName;
                    }
                }
                signalName = s.toString().split("__decrypt")[0];

                /*
                 * for (TMLTask t: getTasks()){ if (signalName.contains(t.getName())){
                 * signalName = signalName.replace(t.getName()+"__",""); } }
                 */
                signalName = signalName.split("__")[1];
                List<String> channels = secChannelMap.get(signalName);
                if (channels != null) {
                    for (String channelName : channels) {
                        channel = getChannelByShortName(channelName);
                        if (channel != null) {
                            for (TMLPortWithSecurityInformation port : channel.ports) {
                                if (port.getCheckAuth()) {
                                    port.setWeakAuthStatus(2);
                                    port.setSecName(signalName);
                                    ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                    if (trace != null && !port.isOrigin()) {
                                        port.setResultTrace(trace);
                                        port.setPragmaString(pragma.toString());
                                    }
                                }
                            }
                        }
                    }
                }

                // In case of HSM
                signalName = s.split("__decrypt")[0];
                signalName = signalName.split("__")[1];
                channels = secChannelMap.get(signalName);
                if (channels != null) {
                    for (String channelName : channels) {
                        if (channelName.contains("retData_") || channelName.contains("data_")) {
                            channelName = channelName.replaceAll("retData_", "").replaceAll("data_", "");
                            // String header= channelName.split("__retData_")[0];
                            for (TMLTask t : getTasks()) {
                                if (channelName.contains(t.getName().split("__")[1])) {
                                    channelName = channelName.replace("_" + t.getName().split("__")[1], "");
                                }
                            }
                            channel = getChannelByShortName(channelName);
                            if (channel != null) {
                                for (TMLPortWithSecurityInformation port : channel.ports) {
                                    if (port.getCheckAuth()) {
                                        port.setWeakAuthStatus(2);
                                        port.setSecName(signalName);
                                        ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                        if (trace != null && !port.isOrigin()) {
                                            port.setResultTrace(trace);
                                            port.setPragmaString(pragma.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (result.isProved() && result.isSatisfied()) {
                String signalName = s.split("_chData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                signalName = signalName.split("__")[1];
                TMLChannel channel = getChannelByShortName(signalName);
                if (channel != null) {
                    for (TMLPortWithSecurityInformation port : channel.ports) {
                        if (port.getCheckAuth()) {
                            port.setStrongAuthStatus(2);
                            port.setMappingName(mappingName);
                            ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                            if (trace != null && !port.isOrigin()) {
                                port.setResultTrace(trace);
                                port.setPragmaString(pragma.toString());
                            }
                        }
                    }
                }
                signalName = s.split("_reqData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLRequest req = getRequestByName(signalName);
                if (req != null) {
                    for (TMLPortWithSecurityInformation port : req.ports) {
                        if (port.getCheckAuth()) {
                            port.setStrongAuthStatus(2);
                            port.setMappingName(mappingName);
                        }
                    }
                }
                signalName = s.split("_eventData")[0];
                for (TMLTask t : getTasks()) {
                    if (signalName.contains(t.getName())) {
                        signalName = signalName.replace(t.getName() + "__", "");
                    }
                }
                TMLEvent ev = getEventByName(signalName);
                if (ev != null) {
                    if (ev.port.checkAuth) {
                        ev.port.checkStrongAuthStatus = 2;
                        ev.port2.mappingName = mappingName;
                    }
                    if (ev.port2.checkAuth) {
                        ev.port2.checkStrongAuthStatus = 2;
                        ev.port2.mappingName = mappingName;
                    }
                }

                signalName = s.split("__decrypt")[0];
                /*
                 * for (TMLTask t: getTasks()){ if (signalName.contains(t.getName())){
                 * signalName = signalName.replace(t.getName()+"__",""); } }
                 */
                signalName = signalName.split("__")[1];
                // TraceManager.addDev("secpattern " + signalName);
                List<String> channels = secChannelMap.get(signalName);
                // TraceManager.addDev("secpattern channels " + channels);
                if (channels != null) {
                    for (String channelName : channels) {
                        channel = getChannelByShortName(channelName);
                        if (channel != null) {
                            for (TMLPortWithSecurityInformation port : channel.ports) {
                                // TraceManager.addDev("adding to port " + channelName);
                                if (port.getCheckAuth()) {
                                    port.setStrongAuthStatus(2);
                                    port.setSecName(signalName);
                                    ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                    if (trace != null && !port.isOrigin()) {
                                        port.setResultTrace(trace);
                                        port.setPragmaString(pragma.toString());
                                    }
                                }
                            }
                        }

                    }
                }

                // In case of HSM
                signalName = s.split("__decrypt")[0];
                signalName = signalName.split("__")[1];
                channels = secChannelMap.get(signalName);
                if (channels != null) {
                    for (String channelName : channels) {
                        if (channelName.contains("retData_") || channelName.contains("data_")) {
                            channelName = channelName.replaceAll("retData_", "").replaceAll("data_", "");
                            // String header= channelName.split("__retData_")[0];
                            for (TMLTask t : getTasks()) {
                                if (channelName.contains(t.getName().split("__")[1])) {
                                    channelName = channelName.replace("_" + t.getName().split("__")[1], "");
                                }
                            }
                            channel = getChannelByShortName(channelName);
                            if (channel != null) {
                                for (TMLPortWithSecurityInformation port : channel.ports) {
                                    if (port.getCheckAuth()) {
                                        port.setStrongAuthStatus(2);
                                        port.setSecName(signalName);
                                        ProVerifResultTrace trace = pvoa.getResults().get(pragma).getTrace();
                                        if (trace != null && !port.isOrigin()) {
                                            port.setResultTrace(trace);
                                            port.setPragmaString(pragma.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public void clearBacktracing() {

        for (TMLChannel channel : getChannels()) {
            for (TMLPortWithSecurityInformation port : channel.ports) {
                if (port.getConfStatus() > 1) {
                    port.setConfStatus(1);
                    port.setMappingName("???");
                    port.setSecName("");
                }
                if (port.getCheckStrongAuthStatus() > 1 || port.getCheckWeakAuthStatus() > 1) {
                    port.setStrongAuthStatus(1);
                    port.setWeakAuthStatus(1);
                }
                port.setResultTrace(null);
                port.setPragmaString("");
            }
        }

        for (TMLRequest req : getRequests()) {
            for (TMLPortWithSecurityInformation port : req.ports) {
                if (port.getConfStatus() > 1) {
                    port.setConfStatus(1);
                    port.setMappingName("???");
                }
                if (port.getCheckStrongAuthStatus() > 1 || port.getCheckWeakAuthStatus() > 1) {
                    port.setStrongAuthStatus(1);
                    port.setWeakAuthStatus(1);
                }
            }
        }

        for (TMLEvent evt : getEvents()) {
            if (evt.port != null && evt.port2 != null) {
                if (evt.port.checkConfStatus > 1) {
                    evt.port.checkConfStatus = 1;
                    evt.port.mappingName = "???";
                }
                if (evt.port.checkStrongAuthStatus > 1 || evt.port.checkWeakAuthStatus > 1) {
                    evt.port.checkStrongAuthStatus = 1;
                    evt.port.checkWeakAuthStatus = 1;
                }
                if (evt.port2.checkConfStatus > 1) {
                    evt.port2.checkConfStatus = 1;
                    evt.port2.mappingName = "???";
                }
                if (evt.port2.checkStrongAuthStatus > 1 || evt.port2.checkWeakAuthStatus > 1) {
                    evt.port2.checkStrongAuthStatus = 1;
                    evt.port2.checkWeakAuthStatus = 1;
                }
            }
        }

        return;
    }

    public List<TMLEvent> getEvents(TMLTask t) {
        TMLEvent evt;
        List<TMLEvent> list = new ArrayList<TMLEvent>();
        Iterator<TMLEvent> iterator = getListIteratorEvents();

        while (iterator.hasNext()) {
            evt = iterator.next();

            if ((evt.origin == t) || (evt.destination == t)) {
                list.add(evt);
            }
        }

        return list;
    }

    public List<TMLRequest> getRequests(TMLTask t) {
        TMLRequest request;
        List<TMLRequest> list = new ArrayList<TMLRequest>();
        Iterator<TMLRequest> iterator = getListIteratorRequests();

        while (iterator.hasNext()) {
            request = iterator.next();

            if ((request.getDestinationTask() == t) || (request.isAnOriginTask(t))) {
                list.add(request);
            }
        }

        return list;
    }

    public List<TMLRequest> getRequestsToMe(TMLTask task) {
        TMLRequest req;

        List<TMLRequest> ll = new LinkedList<TMLRequest>();

        // Must search each task for SendRequest operator, check the request for
        // destination class
        Iterator<TMLRequest> iterator = getListIteratorRequests();

        while (iterator.hasNext()) {
            req = iterator.next();

            if (req.getDestinationTask() == task) {
                ll.add(req);
            }
        }

        return ll;
    }

    public TMLRequest getRequestToMe(TMLTask task) {
        List<TMLRequest> ll = getRequestsToMe(task);

        if ((ll == null) || (ll.size() == 0)) {
            return null;
        }

        return ll.get(0);
    }

    public List<TMLChannel> getChannelsToMe(TMLTask task) {
        TMLChannel chan;

        List<TMLChannel> ll = new LinkedList<TMLChannel>();

        // Must search each task for SendRequest operator, check the request for
        // destination class
        Iterator<TMLChannel> iterator = getListIteratorChannels();

        while (iterator.hasNext()) {
            chan = iterator.next();

            if (chan.hasDestinationTask(task)) {
                ll.add(chan);
            }
        }

        return ll;
    }

    public List<TMLEvent> getEventsToMe(TMLTask task) {
        TMLEvent evt;

        List<TMLEvent> ll = new LinkedList<TMLEvent>();

        // Must search the task for event operator, check the request for destination
        // class
        Iterator<TMLEvent> iterator = getListIteratorEvents();

        while (iterator.hasNext()) {

            evt = iterator.next();
            // TraceManager.addDev("Considering event:" + evt.getName());

            if (evt.hasDestinationTask(task)) {
                ll.add(evt);
            }
        }

        return ll;
    }

    public TMLChannel getChannelToMe(TMLTask task) {
        List<TMLChannel> ll = getChannelsToMe(task);

        if ((ll == null) || (ll.size() == 0)) {
            return null;
        }

        return ll.get(0);
    }

    public TMLEvent getEventToMe(TMLTask task) {
        List<TMLEvent> ll = getEventsToMe(task);

        if ((ll == null) || (ll.size() == 0)) {
            TraceManager.addDev("Returning null event");
            return null;
        }

        return ll.get(0);
    }

    public List<TMLChannel> getChannelsFromMe(TMLTask task) {
        TMLChannel chan;

        List<TMLChannel> ll = new LinkedList<TMLChannel>();

        // Must search each task for SendRequest operator, check the request for
        // destination class
        Iterator<TMLChannel> iterator = getListIteratorChannels();

        while (iterator.hasNext()) {
            chan = iterator.next();

            if (chan.hasOriginTask(task)) {
                ll.add(chan);
            }
        }

        return ll;
    }

    public TMLChannel getChannelFromMe(TMLTask task) {
        List<TMLChannel> ll = getChannelsFromMe(task);

        if ((ll == null) || (ll.size() == 0)) {
            return null;
        }

        return ll.get(0);
    }

    public void prefixAllNamesWith(String _prefix) {
        for (TMLChannel channel : channels) {
            channel.prefixName(_prefix);
        }

        for (TMLEvent evt : events) {
            evt.prefixName(_prefix);
        }

        for (TMLRequest req : requests) {
            req.prefixName(_prefix);
        }

        for (TMLTask task : tasks) {
            task.prefixName(_prefix);
        }
    }

    public void mergeWith(TMLModeling<E> tmlm) {
        channels.addAll(tmlm.getChannels());
        events.addAll(tmlm.getEvents());
        requests.addAll(tmlm.getRequests());
        tasks.addAll(tmlm.getTasks());
        secPatterns.addAll(tmlm.secPatterns);
        securityTaskMap.putAll(tmlm.securityTaskMap);
        checkedActivities.putAll(tmlm.getCheckedComps());
    }

    // Elements with same names are not duplicated
    public void advancedMergeWith(TMLModeling<E> tmlm) {
        TraceManager.addDev("**************** Advanced merge!");
        for (TMLChannel ch : tmlm.getChannels()) {
            if (!(hasSameChannelName(ch))) {
                channels.add(ch);
            }
        }

        for (TMLEvent evt : tmlm.getEvents()) {
            if (!(hasSameEventName(evt))) {
                events.add(evt);
            }
        }

        for (TMLRequest req : tmlm.getRequests()) {
            if (!(hasSameRequestName(req))) {
                requests.add(req);
            }
        }

        for (TMLTask task : tmlm.getTasks()) {
            if (getTMLTaskByName(task.getName()) == null) {

                tasks.add(task);
            }
        }
        for (String s : tmlm.securityPatterns) {
            securityPatterns.add(s);
        }
        for (SecurityPattern sp : tmlm.secPatterns) {
            if (!secPatterns.contains(sp)) {
                secPatterns.add(sp);
            }
        }
        securityTaskMap.putAll(tmlm.securityTaskMap);

        for (E tgc : tmlm.getCheckedComps().keySet()) {
            if (!checkedActivities.containsKey(tgc)) {
                checkedActivities.put(tgc, tmlm.getCheckedComps().get(tgc));
            }
        }

    }

    public void removeChannel(TMLChannel ch) {
        channels.remove(ch);
    }

    public void sortByName() {
        ArrayList<TMLTask> ttasks = new ArrayList<TMLTask>();
        ArrayList<TMLChannel> tchannels = new ArrayList<TMLChannel>();
        ArrayList<TMLRequest> trequests = new ArrayList<TMLRequest>();
        ArrayList<TMLEvent> tevents = new ArrayList<TMLEvent>();

        if (channels.size() > 1) {
            while (channels.size() > 0) {
                TMLChannel ch = channels.get(0);
                for (TMLChannel channel : channels) {
                    if (channel.getName().compareTo(ch.getName()) < 0) {
                        ch = channel;
                    }
                }
                tchannels.add(ch);
                channels.remove(ch);
            }
            channels = tchannels;
        }

        if (events.size() > 1) {
            while (events.size() > 0) {
                TMLEvent evt = events.get(0);
                for (TMLEvent event : events) {
                    if (event.getName().compareTo(evt.getName()) < 0) {
                        evt = event;
                    }
                }
                tevents.add(evt);
                events.remove(evt);
            }
            events = tevents;
        }

        if (requests.size() > 1) {
            while (requests.size() > 0) {
                TMLRequest req = requests.get(0);
                for (TMLRequest request : requests) {
                    if (request.getName().compareTo(req.getName()) < 0) {
                        req = request;
                    }
                }
                trequests.add(req);
                requests.remove(req);
            }
            requests = trequests;
        }

        if (tasks.size() > 1) {
            while (tasks.size() > 0) {
                TMLTask t = tasks.get(0);
                for (TMLTask task : tasks) {
                    if (task.getName().compareTo(t.getName()) < 0) {
                        t = task;
                    }
                }
                ttasks.add(t);
                tasks.remove(t);
            }
            tasks = ttasks;
        }
    }

    public String printSummary(List<TMLError> warnings) {
        String ret = "Optimization warnings:\n";
        int cpt = 1;
        for (TMLError error : warnings) {
            ret += "Warning #" + cpt + " (task " + error.task.getName() + "): " + error.message + "\n";
            cpt++;
        }
        ret += warnings.size() + " optimization warning(s)\n";
        return ret;
    }

    public List<TMLError> optimize() {
        List<TMLError> warnings = new ArrayList<TMLError>();

        if (!optimized) {
            TraceManager.addDev("Optimizing TML modeling");
            optimized = true;
            for (TMLTask task : tasks) {
                optimize(task, warnings);
            }
        }
        return warnings;
    }

    public void optimize(TMLTask task, List<TMLError> warnings) {
        TMLActivity activity = task.getActivityDiagram();
        optimizeVariables(task, activity, warnings);
        optimizeMergeEXECs(activity);
        optimizeMergeDELAYSs(activity);
    }

    /**
     * Search for unused or constant variables. Ununsed variables are removed from
     * the class. Constant variables are also removed from the class, and each time
     * they are used, they are replaced by their respective value.
     * 
     * @param task     : TML task {@link TMLTask}
     * @param activity : TML activity {@link TMLActivity}
     * @param warnings : list of TML errors ({@link TMLError})
     */
    public void optimizeVariables(TMLTask task, TMLActivity activity, List<TMLError> warnings) {
        int i;
        int usage;
        TMLAttribute attr;
        List<TMLAttribute> attributes = task.getAttributes();
        String name;
        TMLError error;

        // TraceManager.addDev("Analyzing attributes in " + task.getName());
        for (i = 0; i < attributes.size(); i++) {
            attr = attributes.get(i);
            // TraceManager.addDev("Analyzing p=" + p.getName() + " i=" + i);

            if (attr.isNat() || attr.isBool()) {
                // TraceManager.addDev("Getting usage");
                name = attr.getName();
                if (!(name.startsWith("arg")) && (name.endsWith("__req"))) {
                    // if ((name.compareTo("arg1__req") != 0) && (name.compareTo("arg2__req") != 0)
                    // && (name.compareTo("arg3__req") != 0)){
                    usage = getUsageOfAttribute(task, activity, attr);
                    // TraceManager.addDev("End getting usage");
                    if (usage == 0) {
                        error = new TMLError(TMLError.WARNING_BEHAVIOR);
                        error.message = "Attribute " + attr.getName() + " of TML task " + task.getName()
                                + " is never used -> removing it";
                        error.task = task;
                        warnings.add(error);
                        // TraceManager.addDev("Attribute " + attr.getName() + " of TML task " +
                        // task.getName() + " is never used -> removing it");
                        attributes.remove(i);
                        i = -1;
                    } else if (usage == 1) {
                        error = new TMLError(TMLError.WARNING_BEHAVIOR);
                        error.message = "Attribute " + attr.getName() + " of TML task " + task.getName()
                                + " is constant -> putting its value in expression where it is used, and removing it from the task";
                        error.task = task;
                        warnings.add(error);
                        // TraceManager.addDev("Attribute " + attr.getName() + " of TML task " +
                        // task.getName() + " is constant -> putting its value in expression where it is
                        // used, and removing it from the task");
                        replaceAttributeWithItsValue(task, activity, attr);
                        attributes.remove(i);
                        i = -1;
                    }
                }
            }
        }
        // TraceManager.addDev("End analyzing attributes in " + task.getName());
    }

    // Returns how a given parameter is used in a class
    // 0 -> never read nor written (i.e. never used)
    // 1 -> read but never written (i.e. constant value)
    // 2 -> read and written (i.e. regular variable)
    public int getUsageOfAttribute(TMLTask task, TMLActivity activity, TMLAttribute attr) {
        // We go through the ad
        // For each component where the param may be involved, we search for it..

        TMLActivityElement element;
        TMLActionState tmlas;
        TMLActivityElementEvent tmlaee;
        TMLChoice choice;
        TMLActivityElementWithAction tmlaewa;
        TMLActivityElementWithIntervalAction tmlint;
        TMLForLoop tmlloop;
        TMLActivityElementChannel tmlaec;
        TMLSendRequest tmlasr;
        TMLSendEvent tmlne;
        TMLRandom tmlrandom;
        int i, j;
        int usage = 0;
        int index;
        String s, s0, s1;
        String name = " " + attr.getName() + " ";
        // String namebis = attr.getName() + " ";
        // String nameter = attr.getName();

        for (i = 0; i < activity.nElements(); i++) {
            element = activity.get(i);
            // TraceManager.addDev("Before element: " + element + " usage=" + usage);

            if (element instanceof TMLActionState) {
                tmlas = (TMLActionState) element;
                s = tmlas.getAction().trim();
                index = s.indexOf('=');
                if (index != -1) {
                    s0 = s.substring(0, index);
                    s0 = " " + s0.trim() + " ";
                    // TraceManager.addDev("s0=" + s0 + " name=" + name);
                    if (s0.compareTo(name) == 0) {
                        return 2;
                    }

                    if (usage == 0) {
                        s1 = s.substring(index, s.length());
                        usage = analyzeStringWithParam(s1, name);
                    }
                }
            } else if (element instanceof TMLRandom) {
                tmlrandom = (TMLRandom) element;
                s = tmlrandom.getVariable().trim();
                s0 = " " + s.trim() + " ";
                if (s0.compareTo(name) == 0) {
                    return 2;
                }

                if (usage == 0) {
                    s1 = tmlrandom.getMinValue().trim();
                    usage = analyzeStringWithParam(s1, name);
                }

                if (usage == 0) {
                    s1 = tmlrandom.getMaxValue().trim();
                    usage = analyzeStringWithParam(s1, name);
                }

            } else if (element instanceof TMLNotifiedEvent) {
                tmlaee = (TMLActivityElementEvent) element;
                s = " " + tmlaee.getVariable() + " ";
                index = analyzeStringWithParam(s, name);
                if (index == 1) {
                    return 2;
                }

            } else if (element instanceof TMLWaitEvent) {
                tmlaee = (TMLActivityElementEvent) element;
                s = " " + tmlaee.getAllParams() + " ";
                index = analyzeStringWithParam(s, name);
                if (index == 1) {
                    return 2;
                }

            } else if (element instanceof TMLForLoop) {
                tmlloop = (TMLForLoop) element;

                s = tmlloop.getInit().trim();
                index = s.indexOf('=');
                if (index != -1) {
                    s0 = s.substring(0, index);
                    s0 = " " + s0.trim() + " ";
                    if (s0.compareTo(name) == 0) {
                        return 2;
                    }

                    if (usage == 0) {
                        s1 = s.substring(index, s.length());
                        usage = analyzeStringWithParam(s1, name);
                    }
                }

                if (usage == 0) {
                    s = tmlloop.getIncrement().trim();
                    index = s.indexOf('=');
                    if (index != -1) {
                        s0 = s.substring(0, index);
                        s0 = " " + s0.trim() + " ";
                        if (s0.compareTo(name) == 0) {
                            return 2;
                        }

                        if (usage == 0) {
                            s1 = s.substring(index, s.length());
                            usage = analyzeStringWithParam(s1, name);
                        }
                    }

                    if (usage == 0) {
                        // TraceManager.addDev("Analyzing condition of loop");
                        usage = analyzeStringWithParam(tmlloop.getCondition(), name);
                    }
                }

            } else if (usage == 0) {

                if (element instanceof TMLChoice) {
                    choice = (TMLChoice) element;
                    for (j = 0; j < choice.getNbGuard(); j++) {
                        if (usage == 0) {
                            usage = analyzeStringWithParam(choice.getGuard(j), name);
                        }
                    }

                } else if (element instanceof TMLActivityElementWithAction) {
                    tmlaewa = (TMLActivityElementWithAction) element;
                    usage = analyzeStringWithParam(tmlaewa.getAction(), name);

                } else if (element instanceof TMLActivityElementWithIntervalAction) {
                    tmlint = (TMLActivityElementWithIntervalAction) element;
                    usage = analyzeStringWithParam(tmlint.getMinDelay(), name);
                    if (usage == 0) {
                        usage = analyzeStringWithParam(tmlint.getMaxDelay(), name);
                    }

                } else if (element instanceof TMLActivityElementChannel) {
                    tmlaec = (TMLActivityElementChannel) element;
                    usage = analyzeStringWithParam(tmlaec.getNbOfSamples(), name);

                } else if (element instanceof TMLSendRequest) {
                    tmlasr = (TMLSendRequest) element;
                    for (j = 0; j < tmlasr.getNbOfParams(); j++) {
                        if (usage == 0) {
                            usage = analyzeStringWithParam(tmlasr.getParam(j), name);
                        }
                    }
                } else if (element instanceof TMLSendEvent) {
                    tmlne = (TMLSendEvent) element;
                    for (j = 0; j < tmlne.getNbOfParams(); j++) {
                        if (usage == 0) {
                            usage = analyzeStringWithParam(tmlne.getParam(j), name);
                        }
                    }
                }
            }
            // TraceManager.addDev("After element: " + element + " usage=" + usage);
        }
        return usage;
    }

    public int analyzeStringWithParam(String s, String name) {
        s = Conversion.replaceAllChar(s, ' ', "");
        s = removeAllActionOps(s);
        s = " " + s + " ";
        int index = s.indexOf(name);
        if (index > -1) {
            return 1;
        }
        return 0;
    }

    public String removeAllActionOps(String s) {
        return Conversion.removeAllActionOps(ops, s, " ");
    }

    public void replaceAttributeWithItsValue(TMLTask task, TMLActivity activity, TMLAttribute attr) {
        // We go through the ad
        // For each component where the param may be involved, we search for it..

        TMLActivityElement element;
        TMLActivityElementWithAction tmlaewa;
        TMLActivityElementEvent tmlaee;
        TMLActivityElementChannel tmlaec;
        TMLActivityElementWithIntervalAction tmlint;
        TMLChoice choice;
        TMLForLoop tmlloop;
        TMLSendRequest tmlasr;
        TMLRandom tmlrandom;
        int i, j;
        // int usage = 0;
        // int index;
        // String s;//, s0, s1;
        // String name = " " + attr.getName() + " ";
        // String namebis = attr.getName() + " ";

        for (i = 0; i < activity.nElements(); i++) {
            element = activity.get(i);
            if (element instanceof TMLActivityElementWithAction) {
                tmlaewa = (TMLActivityElementWithAction) element;
                tmlaewa.setAction(putAttributeValueInString(tmlaewa.getAction(), attr));

            }
            if (element instanceof TMLRandom) {
                tmlrandom = (TMLRandom) element;
                tmlrandom.setMinValue(putAttributeValueInString(tmlrandom.getMinValue(), attr));
                tmlrandom.setMaxValue(putAttributeValueInString(tmlrandom.getMaxValue(), attr));

            } else if (element instanceof TMLChoice) {
                choice = (TMLChoice) element;
                for (j = 0; j < choice.getNbGuard(); j++) {
                    if (choice.getGuard(j) != null) {
                        choice.setGuardAt(j, putAttributeValueInString(choice.getGuard(j), attr));
                    }
                }

                // ExecCInterval and ExecIInterval
            } else if (element instanceof TMLActivityElementWithIntervalAction) {
                tmlint = (TMLActivityElementWithIntervalAction) element;
                tmlint.setMinDelay(putAttributeValueInString(tmlint.getMinDelay(), attr));
                tmlint.setMaxDelay(putAttributeValueInString(tmlint.getMaxDelay(), attr));

            } else if (element instanceof TMLForLoop) {
                tmlloop = (TMLForLoop) element;
                tmlloop.setInit(putAttributeValueInString(tmlloop.getInit(), attr));
                tmlloop.setCondition(putAttributeValueInString(tmlloop.getCondition(), attr));
                tmlloop.setIncrement(putAttributeValueInString(tmlloop.getIncrement(), attr));

            } else if (element instanceof TMLActivityElementChannel) {
                tmlaec = (TMLActivityElementChannel) element;
                tmlaec.setNbOfSamples(putAttributeValueInString(tmlaec.getNbOfSamples(), attr));

            } else if (element instanceof TMLActivityElementEvent) {
                tmlaee = (TMLActivityElementEvent) element;
                for (j = 0; j < tmlaee.getNbOfParams(); j++) {
                    tmlaee.setParam(putAttributeValueInString(tmlaee.getParam(j), attr), j);
                }

            } else if (element instanceof TMLSendRequest) {
                tmlasr = (TMLSendRequest) element;
                for (j = 0; j < tmlasr.getNbOfParams(); j++) {
                    tmlasr.setParam(putAttributeValueInString(tmlasr.getParam(j), attr), j);
                }
            }
        }
    }

    public String putAttributeValueInString(String s, TMLAttribute attr) {
        String newValue;

        if (attr.hasInitialValue()) {
            newValue = attr.getInitialValue();
        } else {
            newValue = attr.getDefaultInitialValue();
        }

        return Conversion.putVariableValueInString(ops, s, attr.getName(), newValue);
    }

    /**
     * Concatenate EXECI and EXEC operations
     * 
     * @param activity : TML activity {@link TMLActivity}
     */
    public void optimizeMergeEXECs(TMLActivity activity) {
        TMLActivityElement elt0, elt1;
        // String action0, action1;
        TMLExecIInterval int0, int1;
        TMLExecCInterval exec0, exec1;

        for (int i = 0; i < activity.nElements(); i++) {
            elt0 = activity.get(i);
            if ((elt0 instanceof TMLExecI) && (elt0.getNbNext() == 1)) {
                elt1 = elt0.getNextElement(0);
                if (elt1 instanceof TMLExecI) {
                    // Concate both elements
                    concateActivityElementWithActions(activity, (TMLActivityElementWithAction) elt0,
                            (TMLActivityElementWithAction) elt1);

                    // We restart from the beginning
                    i = -1;
                }
                if (elt1 instanceof TMLExecIInterval) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt0);

                    // We link the first one to the nexts of the second one
                    setNewNexts(activity, elt0, elt1);

                    // We modify the value of elt1
                    int1 = (TMLExecIInterval) elt1;
                    int1.setMinDelay(addActions(((TMLActivityElementWithAction) elt0).getAction(), int1.getMinDelay()));
                    int1.setMaxDelay(addActions(((TMLActivityElementWithAction) elt0).getAction(), int1.getMaxDelay()));

                    i = -1;
                }
            }

            if ((elt0 instanceof TMLExecIInterval) && (elt0.getNbNext() == 1)) {
                elt1 = elt0.getNextElement(0);
                if (elt1 instanceof TMLExecI) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt1);

                    // We link the first one to the nexts of the second one
                    elt0.setNexts(elt1.getNexts());

                    // We modify the value of elt0
                    int1 = (TMLExecIInterval) elt0;
                    int1.setMinDelay(addActions(((TMLActivityElementWithAction) elt1).getAction(), int1.getMinDelay()));
                    int1.setMaxDelay(addActions(((TMLActivityElementWithAction) elt1).getAction(), int1.getMaxDelay()));

                    i = -1;
                }

                if (elt1 instanceof TMLExecIInterval) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt1);

                    // We link the first one to the nexts of the second one
                    elt0.setNexts(elt1.getNexts());

                    // We modify the value of elt0
                    int1 = (TMLExecIInterval) elt1;
                    int0 = (TMLExecIInterval) elt0;
                    int0.setMinDelay(addActions(int0.getMinDelay(), int1.getMinDelay()));
                    int0.setMaxDelay(addActions(int0.getMaxDelay(), int1.getMaxDelay()));

                    i = -1;
                }
            }

            if ((elt0 instanceof TMLExecC) && (elt0.getNbNext() == 1)) {
                elt1 = elt0.getNextElement(0);
                if (elt1 instanceof TMLExecC && ((TMLExecC) elt1).securityPattern == null) {
                    // Concate both elements
                    concateActivityElementWithActions(activity, (TMLActivityElementWithAction) elt0,
                            (TMLActivityElementWithAction) elt1);

                    // We restart from the beginning
                    i = -1;
                }

                if (elt1 instanceof TMLExecCInterval) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt0);

                    // We link the first one to the nexts of the second one
                    setNewNexts(activity, elt0, elt1);

                    // We modify the value of elt1
                    exec1 = (TMLExecCInterval) elt1;
                    exec1.setMinDelay(
                            addActions(((TMLActivityElementWithAction) elt0).getAction(), exec1.getMinDelay()));
                    exec1.setMaxDelay(
                            addActions(((TMLActivityElementWithAction) elt0).getAction(), exec1.getMaxDelay()));

                    i = -1;
                }
            }

            if ((elt0 instanceof TMLExecCInterval) && (elt0.getNbNext() == 1)) {
                elt1 = elt0.getNextElement(0);
                if (elt1 instanceof TMLExecC) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt1);

                    // We link the first one to the nexts of the second one
                    elt0.setNexts(elt1.getNexts());

                    // We modify the value of elt0
                    exec1 = (TMLExecCInterval) elt0;
                    exec1.setMinDelay(
                            addActions(((TMLActivityElementWithAction) elt1).getAction(), exec1.getMinDelay()));
                    exec1.setMaxDelay(
                            addActions(((TMLActivityElementWithAction) elt1).getAction(), exec1.getMaxDelay()));

                    i = -1;
                }

                if (elt1 instanceof TMLExecCInterval) {
                    // We delete the second i.e. elt1
                    activity.removeElement(elt1);

                    // We link the first one to the nexts of the second one
                    elt0.setNexts(elt1.getNexts());

                    // We modify the value of elt0
                    exec1 = (TMLExecCInterval) elt1;
                    exec0 = (TMLExecCInterval) elt0;
                    exec0.setMinDelay(addActions(exec0.getMinDelay(), exec1.getMinDelay()));
                    exec0.setMaxDelay(addActions(exec0.getMaxDelay(), exec1.getMaxDelay()));

                    i = -1;
                }
            }

        }
    }

    /**
     * Concatenate Delay operations
     * 
     * @param activity : TML actvity {@link TMLActivity}
     */
    public void optimizeMergeDELAYSs(TMLActivity activity) {
        TMLActivityElement elt0, elt1;
        // String action0, action1;
        TMLDelay del0, del1;

        for (int i = 0; i < activity.nElements(); i++) {
            elt0 = activity.get(i);
            if ((elt0 instanceof TMLDelay) && (elt0.getNbNext() == 1)) {
                elt1 = elt0.getNextElement(0);
                if (elt1 instanceof TMLDelay) {
                    del1 = (TMLDelay) elt1;
                    del0 = (TMLDelay) elt0;

                    if (del1.getUnit().equals(del0.getUnit()) && (del1.getActiveDelay() == del0.getActiveDelay())) {
                        // We delete the second i.e. elt1
                        activity.removeElement(elt1);

                        // We link the first one to the nexts of the second one
                        elt0.setNexts(elt1.getNexts());

                        // We modify the value of elt0
                        del0.setMinDelay(addActions(del0.getMinDelay(), del1.getMinDelay()));
                        del0.setMaxDelay(addActions(del0.getMaxDelay(), del1.getMaxDelay()));

                        i = -1;
                    }
                }
            }
        }
    }

    /**
     * Replaces periodic task by two tasks and an event
     */
    public TMLTask[] removePeriodicTasks() {
        Vector<TMLTask> addedTasks = new Vector<>();
        Vector<TMLTask> allTasks = new Vector<>();

        for (TMLTask t : tasks) {
            if (t.isPeriodic()) {

                // Create a new Task
                TMLTask startingTask = new TMLTask("StarterOf" + t.getTaskName(), t.getReferenceObject(),
                        t.getActivityDiagram().getReferenceObject());
                if (t.isDaemon()) {
                    startingTask.setDaemon(true);
                }
                allTasks.add(startingTask);
                addedTasks.add(startingTask);
                addedTasks.add(t);
                // Create an event between the 2
                TMLEvent evt = new TMLEvent("PERIODIC_EVT_" + t.getNameExtension(), t.getReferenceObject(), 1, false);
                events.add(evt);
                evt.setOriginTask(startingTask);
                evt.setDestinationTask(t);
                t.addTMLEvent(evt);
                startingTask.addTMLEvent(evt);

                // Modify the activity diagram of t
                TMLActivity activity = t.getActivityDiagram();
                TMLForLoop mainLoop = new TMLForLoop("LoopForPeriod", activity.getFirst().getReferenceObject());
                activity.addElement(mainLoop);
                TMLActivityElement ae = activity.getFirst().getNextElement(0);
                TMLWaitEvent tmlwe = new TMLWaitEvent("periodEvent", activity.getFirst().getReferenceObject());
                tmlwe.setEvent(evt);
                activity.addElement(tmlwe);
                activity.getFirst().removeNext(0);
                activity.getFirst().addNext(mainLoop);
                mainLoop.addNext(tmlwe);
                tmlwe.addNext(ae);

                // Generate the activity diagram of startingTask
                activity = startingTask.getActivityDiagram();
                TMLStartState start = new TMLStartState("startState", startingTask.getReferenceObject());
                activity.setFirst(start);

                mainLoop = new TMLForLoop("LoopForPeriod", startingTask.getReferenceObject());
                activity.addElement(mainLoop);
                start.addNext(mainLoop);

                TMLSendEvent tmlse = new TMLSendEvent("periodEvent", startingTask.getReferenceObject());
                tmlse.setEvent(evt);
                activity.addElement(tmlse);
                mainLoop.addNext(tmlse);

                TMLDelay delay = new TMLDelay("periodDelay", startingTask.getReferenceObject());
                delay.setUnit(t.getPeriodUnit());
                TraceManager.addDev("Setting delay for Periodic tasks:" + t.getPeriodValue());
                delay.setMinDelay(t.getPeriodValue());
                delay.setMaxDelay(t.getPeriodValue());
                activity.addElement(delay);
                tmlse.addNext(delay);

                TMLStopState stopState = new TMLStopState("periodStop", startingTask.getReferenceObject());
                activity.addElement(stopState);
                delay.addNext(stopState);

                t.setPeriodic(false, "", "ns");

            }
        }

        for (TMLTask t : allTasks) {
            tasks.add(t);
        }

        TMLTask[] returnedTasks = new TMLTask[addedTasks.size()];

        int cpt;
        for (cpt = 0; cpt < addedTasks.size(); cpt++) {
            returnedTasks[cpt] = addedTasks.get(cpt);
        }

        return returnedTasks;
    }

    public void removeAllRandomSequences() {
        for (TMLTask task : tasks) {
            task.removeAllRandomSequences();
        }
    }

    public void setNewNexts(TMLActivity activity, TMLActivityElement elt0, TMLActivityElement elt1) {
        if (elt0 == elt1) {
            return;
        }

        TMLActivityElement elt;
        for (int i = 0; i < activity.nElements(); i++) {
            elt = activity.get(i);
            if (elt != elt0) {
                elt.setNewNext(elt0, elt1);
            }
        }
    }

    public void concateActivityElementWithActions(TMLActivity activity, TMLActivityElementWithAction elt0,
            TMLActivityElementWithAction elt1) {
        // We delete the second i.e. elt1
        activity.removeElement(elt1);

        // We link the first one to the nexts of the second one
        elt0.setNexts(elt1.getNexts());

        // We modify the value of elt0
        elt0.setAction(addActions(elt0.getAction(), elt1.getAction()));
    }

    public String addActions(String action0, String action1) {
        try {
            int val0 = Integer.decode(action0).intValue();
            int val1 = Integer.decode(action1).intValue();
            return "" + (val0 + val1);
        } catch (Exception e) {
            return "(" + action0 + ")+(" + action1 + ")";
        }
    }

    public void splitActionStatesWithUnderscoreVariables() {
        for (TMLTask task : tasks) {
            TMLActivity activity = task.getActivityDiagram();
            if (activity != null) {
                activity.splitActionStatesWithUnderscoreVariables(task);
            }
        }
    }

    public void splitActionStatesWithDollars() {
        for (TMLTask task : tasks) {
            TMLActivity activity = task.getActivityDiagram();
            if (activity != null) {
                activity.splitActionStatesWithDollars(task);
            }
        }
    }

    public TMLElement getCorrespondance(int _id) {
        if (correspondance == null) {
            return null;
        }

        if (_id < 0) {
            return null;
        }

        if (_id >= correspondance.length) {
            return null;
        }

        return correspondance[_id];
    }

    public int computeMaxID() {
        int max = -1;
        for (TMLTask task : tasks) {
            max = Math.max(max, task.computeMaxID());
        }

        for (TMLChannel channel : channels) {
            max = Math.max(max, channel.getID());
        }

        for (TMLRequest request : requests) {
            max = Math.max(max, request.getID());
        }

        for (TMLEvent event : events) {
            max = Math.max(max, event.getID());
        }

        return max;
    }

    public void computeCorrespondance() {
        int max = computeMaxID();

        // TraceManager.addDev("Max ID=" + max);

        correspondance = new TMLElement[max + 1];

        for (TMLTask task : tasks) {
            task.computeCorrespondance(correspondance);
        }

        for (TMLChannel channel : channels) {
            correspondance[channel.getID()] = channel;
        }

        for (TMLRequest request : requests) {
            correspondance[request.getID()] = request;
        }

        for (TMLEvent event : events) {
            correspondance[event.getID()] = event;
        }
    }

    public void printWCETOfTasks() {
        String result = "";
        for (TMLTask task : tasks) {
            result += "\tTask " + task.getTaskName() + ". IComplexity: " + task.getWorstCaseIComplexity() + "\n";
        }
        // TraceManager.addDev("Worst Case I Complexity:\n" + result);
    }

    public void removeForksAndJoins() {

        TraceManager.addDev("\n\n**** Remove forks and joins\n");
        // Exception e = new Exception(); e.printStackTrace();

        removeForks();
        removeJoins();
    }

    // Channels with one origin and several destinations
    // Add a task at sending side
    // Channel is transformed into something else ...
    public void removeForks() {
        // Create new basic channels and tasks
        ArrayList<TMLChannel> newChannels = new ArrayList<TMLChannel>();
        for (TMLChannel channel : channels) {
            if (channel.isAForkChannel()) {
                removeFork(channel, newChannels);
            }
        }

        for (TMLChannel chan : newChannels) {
            addChannel(chan);
        }

        List<TMLEvent> newEvents = new ArrayList<TMLEvent>();
        for (TMLEvent event : events) {
            if (event.isAForkEvent()) {
                removeForkEvent(event, newEvents);
            }
        }

        for (TMLEvent evt : newEvents) {
            addEvent(evt);
        }
    }

    public void removeFork(TMLChannel _ch, ArrayList<TMLChannel> _newChannels) {
        int i;
        ArrayList<TMLTask> originTasks = new ArrayList<TMLTask>();
        ArrayList<TMLTask> destTasks = new ArrayList<TMLTask>();
        for (TMLTask task : _ch.getOriginTasks()) {
            originTasks.add(task);
        }
        for (TMLTask task : _ch.getDestinationTasks()) {
            destTasks.add(task);
        }
        // Create the new task and its activity diagram
        TMLTask forkTask = new TMLTask("FORKTASK" + SEP1 + _ch.getName(), _ch.getReferenceObject(), null);
        forkTask.setDaemon(true);
        TMLActivity forkActivity = forkTask.getActivityDiagram();
        addTask(forkTask);

        // Create the new (basic) channels. The first branch of the fork is reused,
        // others are created
        int nb = _ch.getDestinationTasks().size();
        TMLChannel[] chans = new TMLChannel[nb];
        for (i = 0; i < nb; i++) {
            chans[i] = new TMLChannel("FORKCHANNEL" + SEP1 + i + SEP1 + _ch.getName(), _ch.getReferenceObject());
            chans[i].setTasks(forkTask, _ch.getDestinationTasks().get(i));
            chans[i].setPorts(new TMLPort("FORKPORTORIGIN" + SEP1 + i + SEP1 + _ch.getName(), _ch.getReferenceObject()),
                    _ch.getDestinationPorts().get(i));
            chans[i].setType(_ch.getType());
            chans[i].setMax(_ch.getMax());
            chans[i].setSize(_ch.getSize());
            chans[i].originalOriginTasks = originTasks;
            chans[i].originalDestinationTasks = destTasks;
            _newChannels.add(chans[i]);
        }

        // Modify the activity diagram of tasks making a read in destination channels
        // Modify the channel of red operators to the new channels!
        for (i = 0; i < nb; i++) {
            _ch.getDestinationTasks().get(i).replaceReadChannelWith(_ch, chans[i]);
        }

        // Reworking _ch type: it cannot be non blocking on reading
        if (_ch.getType() == TMLChannel.NBRNBW) {
            _ch.setType(TMLChannel.BRNBW);
        }

        // Transform the original channel into a basic channel
        _ch.setTasks(_ch.getOriginTasks().get(0), forkTask);
        _ch.setPorts(_ch.getOriginPorts().get(0),
                new TMLPort("FORKPORTDESTINATION" + SEP1 + _ch.getName(), _ch.getReferenceObject()));
        _ch.removeComplexInformations();

        // For security generation, provide the orignal tasks
        _ch.originalOriginTasks = originTasks;
        _ch.originalDestinationTasks = destTasks;

        // Make the activity diagram of the fork task
        TMLStartState start = new TMLStartState("startOfFork", null);
        forkActivity.setFirst(start);
        TMLStopState stop = new TMLStopState("stopOfFork", null);
        forkActivity.addElement(stop);
        TMLStopState stop2 = new TMLStopState("stop2OfFork", null);
        forkActivity.addElement(stop2);
        TMLForLoop junction = new TMLForLoop("junctionOfFork", null);
        junction.setInit("i=0");
        junction.setCondition("i<1");
        junction.setIncrement("i=i");
        TMLAttribute attr = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
        forkTask.addAttribute(attr);
        forkActivity.addElement(junction);
        TMLReadChannel read = new TMLReadChannel("ReadOfFork", null);
        forkActivity.addElement(read);
        read.addChannel(_ch);
        read.setNbOfSamples("" + _ch.getNumberOfSamples());

        TMLWriteChannel[] writes = new TMLWriteChannel[nb];

        for (i = 0; i < nb; i++) {
            writes[i] = new TMLWriteChannel("WriteOfFork" + SEP1 + i, null);
            writes[i].addChannel(chans[i]);
            writes[i].setNbOfSamples("" + _ch.getNumberOfSamples());
            forkActivity.addElement(writes[i]);
        }

        start.addNext(junction);
        junction.addNext(read);
        junction.addNext(stop2);
        read.addNext(writes[0]);

        for (i = 0; i < nb - 1; i++) {
            writes[i].addNext(writes[i + 1]);
        }

        writes[nb - 1].addNext(stop);
    }

    public void removeForkEvent(TMLEvent _evt, List<TMLEvent> _newEvents) {
        int i, j;

        // Create the new task and its activity diagram
        TMLTask forkTask = new TMLTask("FORKTASK" + SEP1 + "EVT" + SEP1 + _evt.getName(), _evt.getReferenceObject(),
                null);
        TMLActivity forkActivity = forkTask.getActivityDiagram();
        addTask(forkTask);

        // Create the new (basic) events. The first branch of the fork is reused, others
        // are created
        int nb = _evt.getDestinationTasks().size();
        TMLEvent[] evts = new TMLEvent[nb];

        for (i = 0; i < nb; i++) {
            evts[i] = new TMLEvent("FORKEVENT" + SEP1 + i + SEP1 + _evt.getName(), _evt.getReferenceObject(),
                    _evt.getMaxSize(), _evt.isBlocking());
            evts[i].setTasks(forkTask, _evt.getDestinationTasks().get(i));
            evts[i].setPorts(
                    new TMLPort("FORKPORTORIGIN" + SEP1 + i + SEP1 + _evt.getName(), _evt.getReferenceObject()),
                    _evt.getDestinationPorts().get(i));
            // evts[i].setType(_evt.getType());
            // evts[i].setMax(_evt.getMax());
            // evts[i].setSize(_evt.getSize());
            for (j = 0; j < _evt.getNbOfParams(); j++) {
                evts[i].addParam(_evt.getType(j));
            }

            _newEvents.add(evts[i]);
        }

        // Modify the activity diagram of tasks making a wait in destination events
        // Modify the event of wait operators to the new events
        for (i = 0; i < nb; i++) {
            _evt.getDestinationTasks().get(i).replaceWaitEventWith(_evt, evts[i]);
        }

        // Transform the original event into a basic event
        _evt.setTasks(_evt.getOriginTasks().get(0), forkTask);
        _evt.setPorts(_evt.getOriginPorts().get(0),
                new TMLPort("FORKPORTDESTINATION" + SEP1 + _evt.getName(), _evt.getReferenceObject()));
        _evt.removeComplexInformations();

        // Adding attributes to the task
        for (j = 0; j < _evt.getNbOfParams(); j++) {
            TMLAttribute attr = new TMLAttribute("attr_" + j, _evt.getType(j));
            forkTask.addAttribute(attr);
        }

        // Creating the AD for the fork task
        TMLStartState start = new TMLStartState("startOfFork", null);
        forkActivity.setFirst(start);
        TMLStopState stop = new TMLStopState("stopOfFork", null);
        forkActivity.addElement(stop);
        TMLStopState stop2 = new TMLStopState("stop2OfFork", null);
        forkActivity.addElement(stop2);
        TMLForLoop junction = new TMLForLoop("junctionOfFork", null);
        junction.setInit("fork" + SEP1 + "i=0");
        junction.setCondition("fork" + SEP1 + "i<1");
        junction.setIncrement("fork" + SEP1 + "i=0");
        TMLAttribute attr = new TMLAttribute("fork" + SEP1 + "i", "fork" + SEP1 + "i", new TMLType(TMLType.NATURAL),
                "0");
        forkTask.addAttribute(attr);
        forkActivity.addElement(junction);
        TMLWaitEvent read = new TMLWaitEvent("WaitOfFork", null);
        forkActivity.addElement(read);
        read.setEvent(_evt);

        for (j = 0; j < _evt.getNbOfParams(); j++) {
            read.addParam("attr_" + j);
        }

        TMLSendEvent[] writes = new TMLSendEvent[nb];

        for (i = 0; i < nb; i++) {
            writes[i] = new TMLSendEvent("WriteEvtOfFork" + SEP1 + i, null);
            writes[i].setEvent(evts[i]);
            for (j = 0; j < _evt.getNbOfParams(); j++) {
                writes[i].addParam("attr_" + j);
            }
            forkActivity.addElement(writes[i]);
        }

        start.addNext(junction);
        junction.addNext(read);
        junction.addNext(stop2);
        read.addNext(writes[0]);

        for (i = 0; i < nb - 1; i++) {
            writes[i].addNext(writes[i + 1]);
        }

        writes[nb - 1].addNext(stop);
    }

    // Channels with several origins and one destination
    // Add a task at receiving side
    // Channel is transformed into something else ...
    // Same for events.
    public void removeJoins() {
        // Create new basic channels and tasks
        List<TMLChannel> newChannels = new ArrayList<TMLChannel>();
        for (TMLChannel channel : channels) {
            if (channel.isAJoinChannel()) {
                removeJoin(channel, newChannels);
            }
        }

        for (TMLChannel chan : newChannels) {
            addChannel(chan);
        }

        // Create new basic events and tasks
        ArrayList<TMLEvent> newEvents = new ArrayList<TMLEvent>();
        for (TMLEvent evt : events) {
            // TraceManager.addDev("Event:" + evt);
            if (evt.isAJoinEvent()) {
                TraceManager.addDev("Removing join of this event");
                removeJoinEvent(evt, newEvents);
            }
        }

        for (TMLEvent e : newEvents) {
            addEvent(e);
        }
    }

    public void removeJoin(TMLChannel _ch, List<TMLChannel> _newChannels) {
        int i;
        ArrayList<TMLTask> originTasks = new ArrayList<TMLTask>();
        ArrayList<TMLTask> destTasks = new ArrayList<TMLTask>();
        for (TMLTask task : _ch.getOriginTasks()) {
            originTasks.add(task);
        }
        for (TMLTask task : _ch.getDestinationTasks()) {
            destTasks.add(task);
        }
        // Create the new task and its activity diagram
        TMLTask joinTask = new TMLTask("JOINTASK" + SEP1 + _ch.getName(), _ch.getReferenceObject(), null);
        joinTask.setDaemon(true);
        TMLActivity joinActivity = joinTask.getActivityDiagram();
        addTask(joinTask);

        // Create the new (basic) channels. The last branch of the join is reused,
        // others are created
        int nb = _ch.getOriginTasks().size();
        TMLChannel[] chans = new TMLChannel[nb];
        for (i = 0; i < nb; i++) {
            chans[i] = new TMLChannel("JOINCHANNEL" + SEP1 + i + "__" + _ch.getName(), _ch.getReferenceObject());
            chans[i].setTasks(_ch.getOriginTasks().get(i), joinTask);
            chans[i].setPorts(_ch.getOriginPorts().get(i),
                    new TMLPort("JOINPORTDESTINATION" + SEP1 + i + SEP1 + _ch.getName(), _ch.getReferenceObject()));
            chans[i].setType(_ch.getType());
            chans[i].setMax(_ch.getMax());
            chans[i].setSize(_ch.getSize());
            chans[i].originalOriginTasks = originTasks;
            chans[i].originalDestinationTasks = destTasks;
            _newChannels.add(chans[i]);
        }

        // Modify the activity diagram of tasks making a write in origin channels
        // Modify the channel of write operators to the new channels!
        for (i = 0; i < nb; i++) {
            _ch.getOriginTasks().get(i).replaceWriteChannelWith(_ch, chans[i]);
        }

        if (_ch.getType() == TMLChannel.NBRNBW) {
            _ch.setType(TMLChannel.BRNBW);
        }

        // Transform the original channel into a basic channel
        _ch.setTasks(joinTask, _ch.getDestinationTasks().get(0));
        _ch.setPorts(new TMLPort("JOINPORTORIGIN" + SEP1 + _ch.getName(), _ch.getReferenceObject()),
                _ch.getDestinationPorts().get(0));
        _ch.originalOriginTasks = originTasks;
        _ch.originalDestinationTasks = destTasks;
        _ch.removeComplexInformations();

        // Make the activity diagram of the fork task
        TMLStartState start = new TMLStartState("startOfJoin", null);
        joinActivity.setFirst(start);
        TMLStopState stop = new TMLStopState("stopOfJoin", null);
        joinActivity.addElement(stop);
        TMLStopState stop2 = new TMLStopState("stop2OfFork", null);
        joinActivity.addElement(stop2);
        TMLForLoop junction = new TMLForLoop("junctionOfJoin", null);
        junction.setInit("i=0");
        junction.setCondition("i<1");
        junction.setIncrement("i=i");
        TMLAttribute attr = new TMLAttribute("i", "i", new TMLType(TMLType.NATURAL), "0");
        joinTask.addAttribute(attr);
        joinActivity.addElement(junction);
        TMLWriteChannel write = new TMLWriteChannel("WriteOfJoin", null);
        joinActivity.addElement(write);
        write.addChannel(_ch);
        write.setNbOfSamples("" + _ch.getNumberOfSamples());

        TMLReadChannel[] reads = new TMLReadChannel[nb];
        for (i = 0; i < nb; i++) {
            reads[i] = new TMLReadChannel("ReadOfJoin" + SEP1 + i, null);
            reads[i].addChannel(chans[i]);
            reads[i].setNbOfSamples("" + _ch.getNumberOfSamples());
            joinActivity.addElement(reads[i]);
        }

        // Nexts
        start.addNext(junction);
        junction.addNext(reads[0]);
        junction.addNext(stop2);
        write.addNext(stop);
        for (i = 0; i < nb - 1; i++) {
            reads[i].addNext(reads[i + 1]);
        }
        reads[nb - 1].addNext(write);
    }

    public void removeJoinEvent(TMLEvent _evt, List<TMLEvent> _newEvents) {
        int i, j;

        // Create the new task and its activity diagram
        TMLTask joinTask = new TMLTask("JOINTASK" + SEP1 + "EVT" + SEP1 + _evt.getName(), _evt.getReferenceObject(),
                null);
        TMLActivity joinActivity = joinTask.getActivityDiagram();
        addTask(joinTask);

        // Create the new (basic) channels. The last branch of the join is reused,
        // others are created
        int nb = _evt.getOriginTasks().size();
        TMLEvent[] evts = new TMLEvent[nb];
        for (i = 0; i < nb; i++) {
            evts[i] = new TMLEvent("JOINEVENT" + SEP1 + i + SEP1 + _evt.getName(), _evt.getReferenceObject(),
                    _evt.getMaxSize(), _evt.isBlocking());
            evts[i].setTasks(_evt.getOriginTasks().get(i), joinTask);
            evts[i].setPorts(_evt.getOriginPorts().get(i),
                    new TMLPort("JOINPORTDESTINATION" + SEP1 + i + SEP1 + _evt.getName(), _evt.getReferenceObject()));

            for (j = 0; j < _evt.getNbOfParams(); j++) {
                evts[i].addParam(_evt.getType(j));

            }

            _newEvents.add(evts[i]);
        }

        // Modify the activity diagram of tasks making a write in origin channels
        // Modify the channel of write operators to the new channels!
        for (i = 0; i < nb; i++) {
            _evt.getOriginTasks().get(i).replaceSendEventWith(_evt, evts[i]);
        }

        // Transform the original channel into a basic channel
        _evt.setTasks(joinTask, _evt.getDestinationTasks().get(0));
        _evt.setPorts(new TMLPort("JOINPORTORIGIN" + SEP1 + _evt.getName(), _evt.getReferenceObject()),
                _evt.getDestinationPorts().get(0));
        _evt.removeComplexInformations();

        // Adding attributes to the task
        for (j = 0; j < _evt.getNbOfParams(); j++) {
            TMLAttribute attr = new TMLAttribute("attr_" + j, _evt.getType(j));
            joinTask.addAttribute(attr);
        }

        // Make the activity diagram of the fork task
        TMLStartState start = new TMLStartState("startOfJoin", null);
        joinActivity.setFirst(start);
        TMLStopState stop = new TMLStopState("stopOfJoin", null);
        joinActivity.addElement(stop);
        TMLStopState stop2 = new TMLStopState("stop2OfFork", null);
        joinActivity.addElement(stop2);
        TMLForLoop junction = new TMLForLoop("junctionOfJoin", null);
        junction.setInit("join" + SEP1 + "i=0");
        junction.setCondition("join" + SEP1 + "i<1");
        junction.setIncrement("join" + SEP1 + "i=join" + SEP1 + "i");
        TMLAttribute attr = new TMLAttribute("join" + SEP1 + "i", "join" + SEP1 + "i", new TMLType(TMLType.NATURAL),
                "0");
        joinTask.addAttribute(attr);
        joinActivity.addElement(junction);
        TMLSendEvent notify = new TMLSendEvent("NotifyOfJoin", null);

        for (j = 0; j < _evt.getNbOfParams(); j++) {
            notify.addParam("attr_" + j);
        }

        joinActivity.addElement(notify);
        notify.setEvent(_evt);

        TMLWaitEvent[] waits = new TMLWaitEvent[nb];

        for (i = 0; i < nb; i++) {
            waits[i] = new TMLWaitEvent("WaitOfJoin" + SEP1 + i, null);
            waits[i].setEvent(evts[i]);

            for (j = 0; j < _evt.getNbOfParams(); j++) {
                waits[i].addParam("attr_" + j);
            }

            joinActivity.addElement(waits[i]);
        }

        // Nexts
        start.addNext(junction);
        junction.addNext(waits[0]);
        junction.addNext(stop2);
        notify.addNext(stop);

        for (i = 0; i < nb - 1; i++) {
            waits[i].addNext(waits[i + 1]);
        }

        waits[nb - 1].addNext(notify);
    }

    public void removeEmptyInfiniteLoop() {
        for (TMLTask task : tasks) {
            task.removeEmptyInfiniteLoop();
        }
    }

    public String toXML() {
        String s = "<TMLMODELING>\n";
        for (TMLTask t : tasks) {
            s += t.toXML();
        }
        for (String p : pragmas) {
            s += "<PRAGMA value=\"" + p + "\" />\n";
        }
        for (TMLChannel c : channels) {
            s += c.toXML();
        }
        for (TMLRequest r : requests) {
            s += r.toXML();
        }
        for (TMLEvent e : events) {
            s += e.toXML();
        }
        s += "</TMLMODELING>\n";
        return s;
    }

    public boolean equalSpec(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TMLModeling<?> that = (TMLModeling<?>) o;

        if (!isTaskListEquals(tasks, that.tasks))
            return false;

        if (!isRequestListEquals(requests, that.requests))
            return false;

        if (!isEventListEquals(events, that.events))
            return false;

        if (!isChannelListEquals(channels, that.channels))
            return false;

        if (!isSecurityPatternListEquals(secPatterns, that.secPatterns))
            return false;

        return (new HashSet<>(securityPatterns).equals(new HashSet<>(that.securityPatterns)));
    }

    public boolean isTaskListEquals(List<TMLTask> list1, List<TMLTask> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLTask::getName));
        Collections.sort(list2, Comparator.comparing(TMLTask::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isRequestListEquals(List<TMLRequest> list1, List<TMLRequest> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLRequest::getName));
        Collections.sort(list2, Comparator.comparing(TMLRequest::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isChannelListEquals(List<TMLChannel> list1, List<TMLChannel> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLChannel::getName));
        Collections.sort(list2, Comparator.comparing(TMLChannel::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isEventListEquals(List<TMLEvent> list1, List<TMLEvent> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLEvent::getName));
        Collections.sort(list2, Comparator.comparing(TMLEvent::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isSecurityPatternListEquals(List<SecurityPattern> list1, List<SecurityPattern> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(SecurityPattern::getName));
        Collections.sort(list2, Comparator.comparing(SecurityPattern::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

}

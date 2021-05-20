package tmltranslator.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class DependencyGraphRelations {
    private final HashMap<String, HashSet<String>> sendEventWaitEventEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> readWriteChannelEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> writeReadChannelEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> forkreadEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> forkwriteEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> joinreadEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> joinwriteEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, HashSet<String>> sequenceEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, ArrayList<String>> orderedSequenceList = new HashMap<String, ArrayList<String>>();
    private final HashMap<String, HashSet<String>> unOrderedSequenceEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, ArrayList<String>> unOrderedSequenceList = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<ArrayList<Integer>>> runnableTimePerDevice = new HashMap<String, ArrayList<ArrayList<Integer>>>();
    private HashMap<String, List<String>> allForLoopNextValues = new HashMap<String, List<String>>();
    private HashMap<Vertex, List<Vertex>> allChoiceValues = new HashMap<Vertex, List<Vertex>>();
    private HashMap<Vertex, List<Vertex>> allSelectEvtValues = new HashMap<Vertex, List<Vertex>>();
    private HashMap<Vertex, List<Vertex>> allSeqValues = new HashMap<Vertex, List<Vertex>>();
    private HashMap<Vertex, List<Vertex>> allRandomSeqValues = new HashMap<Vertex, List<Vertex>>();
    private final List<String> forEverLoopList = new ArrayList<String>();
    private final HashMap<String, HashSet<String>> requestEdges = new HashMap<String, HashSet<String>>();
    private final HashMap<String, List<String>> requestsOriginDestination = new HashMap<String, List<String>>();
    private final HashMap<String, List<String>> requestsPorts = new HashMap<String, List<String>>();
    private final HashMap<String, List<String>> requestsDestination = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> forLoopNextValues = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> sendEvt = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> waitEvt = new HashMap<String, List<String>>();
    private HashMap<String, String> sendData = new HashMap<String, String>();
    private HashMap<String, String> receiveData = new HashMap<String, String>();
    private Vector<String> readChannelTransactions = new Vector<String>();
    private Vector<String> writeChannelTransactions = new Vector<String>();
    private HashMap<Vertex, List<Vertex>> ruleAddedEdges = new HashMap<Vertex, List<Vertex>>();
    private HashMap<Vertex, List<Vertex>> ruleAddedEdgesChannels = new HashMap<Vertex, List<Vertex>>();

    public void setRuleAddedEdgesChannels(HashMap<Vertex, List<Vertex>> ruleAddedEdgesChannels) {
        this.ruleAddedEdgesChannels = ruleAddedEdgesChannels;
    }

    private HashMap<String, Integer> cpuIDs = new HashMap<String, Integer>();

    public void setForLoopNextValues(HashMap<String, List<String>> forLoopNextValues) {
        this.forLoopNextValues = forLoopNextValues;
    }

    public void setSendEvt(HashMap<String, List<String>> sendEvt) {
        this.sendEvt = sendEvt;
    }

    public void setWaitEvt(HashMap<String, List<String>> waitEvt) {
        this.waitEvt = waitEvt;
    }

    public void setSendData(HashMap<String, String> sendData) {
        this.sendData = sendData;
    }

    public void setReceiveData(HashMap<String, String> receiveData) {
        this.receiveData = receiveData;
    }

    public List<String> getForEverLoopList() {
        return forEverLoopList;
    }

    public HashMap<String, HashSet<String>> getRequestEdges() {
        return requestEdges;
    }

    public HashMap<String, List<String>> getRequestsOriginDestination() {
        return requestsOriginDestination;
    }

    public HashMap<String, List<String>> getRequestsPorts() {
        return requestsPorts;
    }

    public HashMap<String, List<String>> getRequestsDestination() {
        return requestsDestination;
    }

    public HashMap<String, List<String>> getForLoopNextValues() {
        return forLoopNextValues;
    }

    public HashMap<String, List<String>> getSendEvt() {
        return sendEvt;
    }

    public HashMap<String, List<String>> getWaitEvt() {
        return waitEvt;
    }

    public HashMap<String, String> getSendData() {
        return sendData;
    }

    public HashMap<String, String> getReceiveData() {
        return receiveData;
    }

    public HashMap<String, HashSet<String>> getSendEventWaitEventEdges() {
        return sendEventWaitEventEdges;
    }

    public HashMap<String, HashSet<String>> getReadWriteChannelEdges() {
        return readWriteChannelEdges;
    }

    public HashMap<String, HashSet<String>> getWriteReadChannelEdges() {
        return writeReadChannelEdges;
    }

    public HashMap<String, HashSet<String>> getForkreadEdges() {
        return forkreadEdges;
    }

    public HashMap<String, HashSet<String>> getForkwriteEdges() {
        return forkwriteEdges;
    }

    public HashMap<String, HashSet<String>> getJoinreadEdges() {
        return joinreadEdges;
    }

    public HashMap<String, HashSet<String>> getJoinwriteEdges() {
        return joinwriteEdges;
    }

    public HashMap<String, HashSet<String>> getSequenceEdges() {
        return sequenceEdges;
    }

    public HashMap<String, ArrayList<String>> getOrderedSequenceList() {
        return orderedSequenceList;
    }

    public HashMap<String, HashSet<String>> getUnOrderedSequenceEdges() {
        return unOrderedSequenceEdges;
    }

    public HashMap<String, ArrayList<String>> getUnOrderedSequenceList() {
        return unOrderedSequenceList;
    }

    public HashMap<String, ArrayList<ArrayList<Integer>>> getRunnableTimePerDevice() {
        return runnableTimePerDevice;
    }

    public HashMap<String, List<String>> getAllForLoopNextValues() {
        return allForLoopNextValues;
    }

    public HashMap<Vertex, List<Vertex>> getAllChoiceValues() {
        return allChoiceValues;
    }

    public HashMap<Vertex, List<Vertex>> getAllSelectEvtValues() {
        return allSelectEvtValues;
    }

    public HashMap<Vertex, List<Vertex>> getAllSeqValues() {
        return allSeqValues;
    }

    public HashMap<Vertex, List<Vertex>> getAllRandomSeqValues() {
        return allRandomSeqValues;
    }

    public void setRunnableTimePerDevice(HashMap<String, ArrayList<ArrayList<Integer>>> runnableTimePerDevice) {
        this.runnableTimePerDevice = runnableTimePerDevice;
    }

    public Vector<String> getReadChannelTransactions() {
        return readChannelTransactions;
    }

    public Vector<String> getWriteChannelTransactions() {
        return writeChannelTransactions;
    }

    public HashMap<Vertex, List<Vertex>> getRuleAddedEdges() {
        return ruleAddedEdges;
    }

    public HashMap<Vertex, List<Vertex>> getRuleAddedEdgesChannels() {
        return ruleAddedEdgesChannels;
    }

    public HashMap<String, Integer> getCpuIDs() {
        return cpuIDs;
    }

    public void setRuleAddedEdges(HashMap<Vertex, List<Vertex>> ruleAddedEdges) {
        this.ruleAddedEdges = ruleAddedEdges;
    }
}

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

import java.util.*;

/**
 * Class TMLTask
 * Creation: 17/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 17/11/2005
 */
public class TMLTask extends TMLElement {
    protected TMLActivity activity;
    private boolean isRequested = false;
    private TMLRequest request;
    private List<TMLAttribute> attributes;
    private boolean mustExit = false;
    private int priority;
    private Set<TMLChannel> channelsList;
    private Set<TMLChannel> readTMLChannelsList;
    private Set<TMLChannel> writeTMLChannelsList;
    private Set<TMLEvent> eventsList;
    private int operationType;
    private String operation = "";
    private boolean isDaemon;
    private String operationMEC;
    private boolean isAttacker;

    public TMLTask(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass);
        //TraceManager.addDev("Creating new TMLTask:" + name);
        activity = new TMLActivity(name + "activity_diagram", referenceToActivityDiagram);
        attributes = new ArrayList<TMLAttribute>();
        channelsList = new HashSet<TMLChannel>();
        readTMLChannelsList = new HashSet<TMLChannel>();
        writeTMLChannelsList = new HashSet<TMLChannel>();
        eventsList = new HashSet<TMLEvent>();
    }

    public void setRequested(boolean _b) {
        isRequested = _b;
    }

    public void setDaemon(boolean _b) {
        isDaemon = _b;
    }

    public boolean isDaemon() {return isDaemon;}

    public void setPriority(int _priority) {
        priority = _priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public boolean isAttacker() {
        return isAttacker;
    }

    public void setAttacker(boolean a) {
        isAttacker = a;
    }

    public void setRequest(TMLRequest _request) {
        request = _request;
    }

    public TMLRequest getRequest() {
        return request;
    }

    public void addAttribute(TMLAttribute _tmla) {
        attributes.add(_tmla);
    }


    public void addAttributeIfApplicable(TMLAttribute _tmla) {
        for(TMLAttribute att: attributes) {
            if (att.getName().compareTo(_tmla.getName()) == 0) {
                return;
            }
        }
        attributes.add(_tmla);
    }

    public List<TMLAttribute> getAttributes() {
        return attributes;
    }

    public boolean hasCommand(int commandID) {
        TMLActivityElement tmlae;
        for (int i = 0; i < activity.nElements(); i++) {
            tmlae = activity.get(i);
            if (tmlae.getID() == commandID) {
                return true;
            }
        }
        return false;
    }

    public TMLActivityElement getElementByID(int commandID) {
        TMLActivityElement tmlae;
        for (int i = 0; i < activity.nElements(); i++) {
            tmlae = activity.get(i);
            if (tmlae.getID() == commandID) {
                return tmlae;
            }
        }
        return null;
    }

    public String[] makeCommandIDs() {
        String[] list = new String[activity.nElements()];
        TMLActivityElement tmlae;
        for (int i = 0; i < activity.nElements(); i++) {
            tmlae = activity.get(i);
            list[i] = tmlae.getName() + " (" + tmlae.getID() + ")";
        }
        return list;
    }

    public String[] makeVariableIDs() {
        String[] list = new String[attributes.size()];
        int cpt = 0;

        for (TMLAttribute att : attributes) {
            list[cpt] = att.getName() + " (" + att.getID() + ")";
            cpt++;
        }
        return list;
    }

    public TMLAttribute getAttributeByName(String _name) {
        for (TMLAttribute attribute : attributes) {
            if (attribute.getName().compareTo(_name) == 0) {
                return attribute;
            }
        }
        return null;
    }

    //For SDR operations only 1 channel
    public List<TMLReadChannel> getReadChannels() {

        List<TMLReadChannel> list = new ArrayList<TMLReadChannel>();
        for (int i = 0; i < getActivityDiagram().nElements(); i++) {
            if (getActivityDiagram().get(i) instanceof TMLReadChannel) {
                list.add((TMLReadChannel) getActivityDiagram().get(i));
                //TraceManager.addDev( "Element: " + task.getActivityDiagram().get(i).toString() );
            }
        }

        return list;
    }

    //For SDR operations, only 1 channel
    public List<TMLWriteChannel> getWriteChannels() {

        List<TMLWriteChannel> list = new ArrayList<TMLWriteChannel>();
        for (int i = 0; i < getActivityDiagram().nElements(); i++) {
            if (getActivityDiagram().get(i) instanceof TMLWriteChannel) {
                list.add((TMLWriteChannel) getActivityDiagram().get(i));
                //TraceManager.addDev( "Element: " + task.getActivityDiagram().get(i).toString() );
            }
        }
        return list;
    }

    public List<TMLSendEvent> getSendEvents() {

        List<TMLSendEvent> list = new ArrayList<TMLSendEvent>();

        for (int i = 0; i < getActivityDiagram().nElements(); i++) {
            if (getActivityDiagram().get(i) instanceof TMLSendEvent) {
                list.add((TMLSendEvent) getActivityDiagram().get(i));
            }
        }

        return list;
    }

    public List<TMLWaitEvent> getWaitEvents() {
        List<TMLWaitEvent> list = new ArrayList<TMLWaitEvent>();

        for (int i = 0; i < getActivityDiagram().nElements(); i++) {
            if (getActivityDiagram().get(i) instanceof TMLWaitEvent) {
                list.add((TMLWaitEvent) getActivityDiagram().get(i));
            }
        }

        return list;
    }

    public String getID0() {

        if (getReadChannels().size() > 0) {
            return getReadChannels().get(0).toString().split("__")[1];
        } else {
            return "";
        }
    }

    public String getOD0() {

        if (getWriteChannels().size() > 0) {
            return getWriteChannels().get(0).toString().split("__")[1];
        } else {
            return "";
        }
    }

    public String getTaskName() {
        if (getName().indexOf("__") == -1) {
            return getName();
        }
        return getName().split("__")[1];
    }


    public TMLActivity getActivityDiagram() {
        return activity;
    }

    public void addElement(TMLActivityElement prev, TMLActivityElement succ) {
        if (activity == null) {
            return;
        }
        activity.addElement(succ);
        prev.addNext(succ);
    }


    public void setExit(boolean b) {
        mustExit = b;
    }

    public boolean exits() {
        return mustExit;
    }

    public boolean has(TMLActivityElement _elt) {
        if (activity == null) {
            return false;
        }

        return activity.contains(_elt);
    }

    public String getNameExtension() {
        return "task__";
    }

    public int getMaximumSelectEvtSize() {
        return activity.getMaximumSelectEvtSize();
    }

    public String getAttributeString() {
        String ret = "";
        for (TMLAttribute attribute : attributes) {
            ret += attribute.toString() + " / ";
        }
        return ret;
    }

    public boolean hasTMLRandom() {
        TMLActivityElement element;
        for (int i = 0; i < activity.nElements(); i++) {
            element = activity.get(i);
            if (element instanceof TMLRandom) {
                return true;
            }
        }
        return false;
    }

    public void removeAllRandomSequences() {
        activity.removeAllRandomSequences(this);
    }

    public Vector<String> getAllAttributesStartingWith(String _name) {
        Vector<String> v = new Vector<String>();
        for (TMLAttribute attribute : attributes) {
            if (attribute.getName().startsWith(_name)) {
                v.add(attribute.getName());
            }
        }
        return v;
    }

    public Vector<TMLAttribute> getAllTMLAttributesStartingWith(String _name) {
        Vector<TMLAttribute> v = new Vector<TMLAttribute>();
        for (TMLAttribute attribute : attributes) {
            if (attribute.getName().startsWith(_name)) {
                v.add(attribute);
            }
        }
        return v;
    }

    public int computeMaxID() {
        int max = getID();
        if (activity != null) {
            max = Math.max(max, activity.computeMaxID());
        }
        return max;
    }

    public void computeCorrespondance(TMLElement[] _correspondance) {
        _correspondance[getID()] = this;
        if (activity != null) {
            activity.computeCorrespondance(_correspondance);
        }

    }

    public void replaceWaitEventWith(TMLEvent oldEvt, TMLEvent newEvt) {
        activity.replaceWaitEventWith(oldEvt, newEvt);
    }

    public void replaceSendEventWith(TMLEvent oldEvt, TMLEvent newEvt) {
        activity.replaceSendEventWith(oldEvt, newEvt);
    }

    public void replaceReadChannelWith(TMLChannel oldChan, TMLChannel newChan) {
        activity.replaceReadChannelWith(oldChan, newChan);
    }

    public void replaceWriteChannelWith(TMLChannel oldChan, TMLChannel newChan) {
        activity.replaceWriteChannelWith(oldChan, newChan);
    }

    public void addSendEventAfterWriteIn(TMLChannel chan, TMLEvent evt, String action) {
        activity.addSendEventAfterWriteIn(chan, evt, action);
    }

    public void addSendAndReceiveEventAfterWriteIn(TMLChannel chan, TMLEvent evt1, TMLEvent evt2, String action1, String action2) {
        activity.addSendAndReceiveEventAfterWriteIn(chan, evt1, evt2, action1, action2);
    }

    public void addTMLChannel(TMLChannel _ch) {
        channelsList.add(_ch);
    }

    public void addReadTMLChannel(TMLChannel _ch) {
        readTMLChannelsList.add(_ch);
    }

    public void addWriteTMLChannel(TMLChannel _ch) {
        writeTMLChannelsList.add(_ch);
    }

    /*public List<TMLChannel> getTMLChannels() {
        return new ArrayList<TMLChannel>(channelsList);
    }*/

    public List<TMLChannel> getReadTMLChannels() {
        return new ArrayList<TMLChannel>(readTMLChannelsList);
    }

    public List<TMLChannel> getWriteTMLChannels() {
        return new ArrayList<TMLChannel>(writeTMLChannelsList);
    }

    public void addTMLEvent(TMLEvent _evt) {
        eventsList.add(_evt);
    }

    public List<TMLEvent> getTMLEvents() {
        return new ArrayList<TMLEvent>(eventsList);
    }

    public void addOperationType(int _operationType) {
        operationType = _operationType;
    }

    public int getOperationType() {
        return operationType;
    }

    public void addOperationMEC(String _operation) {
        operationMEC = _operation;
    }

    public String getOperationMEC() {
        return operationMEC;
    }

    public void addOperation(String _operation) {
        operation = _operation;
    }

    public String getOperation() {
        return operation;
    }

    public void removeEmptyInfiniteLoop() {
        activity.removeEmptyInfiniteLoop();
    }

    public String toXML() {
        String s = new String("<TASK name=\"" + name + "\" priority=\"" + priority + "\" >\n");
        for (TMLAttribute attr : attributes) {
            s += attr.toXML();
        }
        s += activity.toXML();
        s += "</TASK>\n";
        return s;
    }


    // returns -1 if the WC cannot be computed
    // The function follows one path randomly and concatenate ExecI operators
    public int getWorstCaseIComplexity() {
        if (activity == null) {
            return -1;
        }
        //TraceManager.addDev("Handling task:" + getTaskName());
        return activity.getWorstCaseIComplexity();
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLTask)) return false;
        if (!super.equalSpec(o)) return false;
        TMLTask tmlTask = (TMLTask) o;
        if(!(new HashSet<>(attributes).equals(new HashSet<>(tmlTask.attributes))))
            return false;
        return operationType == tmlTask.operationType &&
                isDaemon == tmlTask.isDaemon &&
                isAttacker == tmlTask.isAttacker &&
                operation.equals(tmlTask.operation) &&
                operationMEC.equals(tmlTask.operationMEC);
    }
}

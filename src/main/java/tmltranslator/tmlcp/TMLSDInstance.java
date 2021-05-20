/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package tmltranslator.tmlcp;

import tmltranslator.TMLAttribute;
import tmltranslator.TMLElement;
import tmltranslator.TMLType;
import ui.tmldd.TMLArchiNode;
import ui.tmlsd.TGConnectorMessageTMLSD;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class TMLSDInstance, the class for the TML Sequence Diagram Instance in the
 * tmlcp data structure. An instance is composed of actions, messages, global
 * variables and is associated to a mapped unit into the architecture. Creation:
 * 18/02/2014
 * 
 * @version 1.1 03/11/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDInstance extends TMLElement {

    private String type;
    private TMLArchiNode mappedUnit; // the unit of the architecture where the instance is mapped to
    private ArrayList<TMLAttribute> globalVariables;
    private ArrayList<TMLSDMessage> messages;
    private ArrayList<TMLSDAction> actions;
    private ArrayList<TMLSDEvent> events; // used to sort messages and actions according to their order, to produce the
                                          // TMLTxt code

    public TMLSDInstance(String _name, Object _referenceObject, String _type) {
        super(_name, _referenceObject);
        this.type = _type;
        init();
    }

    // The constructor to be used from the parser. No reference to object
    public TMLSDInstance(String _name, String _type) {
        super(_name, null);
        this.type = _type;
        init();
    }

    private void init() {

        globalVariables = new ArrayList<TMLAttribute>();
        messages = new ArrayList<TMLSDMessage>();
        actions = new ArrayList<TMLSDAction>();
        events = new ArrayList<TMLSDEvent>();
    }

    public void setType(String _type) {
        if (_type != "") {
            this.type = _type;
        } else {
            this.type = "NO_TYPE";
        }
    }

    public String getType() {
        return this.type;
    }

    public ArrayList<TMLSDEvent> getEvents() {
        return events;
    }

    public void addAttribute(TMLAttribute _attribute) { // used by the graphical 2 TMLTxt compiler
        globalVariables.add(_attribute);
    }

    public ArrayList<TMLAttribute> getAttributes() {
        return globalVariables;
    }

    public ArrayList<TMLSDAction> getActions() {
        return actions;
    }

    public void addAction(TMLSDAction _action) {
        // TraceManager.addDev("SD: Adding action in " + getName() + " nb of events: " +
        // events.size());
        actions.add(_action);
        events.add(new TMLSDEvent(_action, TMLSDEvent.ACTION_EVENT, _action.getYCoord()));
        Collections.sort(events);
    }

    // Add an action from the parser where there is no notion of yCoord. Events are
    // already ordered.
    public void addActionFromParser(TMLSDAction _action) {
        actions.add(_action);
        events.add(new TMLSDEvent(_action, TMLSDEvent.ACTION_EVENT));
    }

    public void addMappedUnit(TMLArchiNode _mappedUnit) {
        mappedUnit = _mappedUnit;
    }

    public TMLArchiNode getMappedUnit() {
        return mappedUnit;
    }

    public void addMessage(TMLSDMessage _msg, int _type) {

        // TraceManager.addDev("SD: Adding message in " + getName()+ " nb of events: " +
        // events.size());
        messages.add(_msg);
        if (_type == TMLSDEvent.SEND_MESSAGE_EVENT) {
            int yCoord = ((TGConnectorMessageTMLSD) _msg.getReferenceObject()).getTGConnectingPointP1().getY();
            events.add(new TMLSDEvent(_msg, TMLSDEvent.SEND_MESSAGE_EVENT, yCoord));
        }
        if (_type == TMLSDEvent.RECEIVE_MESSAGE_EVENT) {
            int yCoord = ((TGConnectorMessageTMLSD) _msg.getReferenceObject()).getTGConnectingPointP2().getY();
            events.add(new TMLSDEvent(_msg, TMLSDEvent.RECEIVE_MESSAGE_EVENT, yCoord));
        }
        Collections.sort(events);
    }

    // Add a message from the parser where there is no notion of yCoord. Events are
    // already ordered.
    public void addMessageFromParser(TMLSDMessage _msg, int _type) {

        messages.add(_msg);
        if (_type == TMLSDEvent.SEND_MESSAGE_EVENT) {
            events.add(new TMLSDEvent(_msg, TMLSDEvent.SEND_MESSAGE_EVENT));
        }
        if (_type == TMLSDEvent.RECEIVE_MESSAGE_EVENT) {
            events.add(new TMLSDEvent(_msg, TMLSDEvent.RECEIVE_MESSAGE_EVENT));
        }
    }

    public void insertInitialValue(String _name, String value) {

        int i = 0;
        String str;
        TMLAttribute tempAttr;
        TMLType tempType, _attrType;
        TMLAttribute _attr = new TMLAttribute(_name, new TMLType(1));

        for (i = 0; i < globalVariables.size(); i++) {
            tempAttr = globalVariables.get(i);
            str = tempAttr.getName();
            if (str.equals(_attr.getName())) {
                tempType = tempAttr.getType();
                _attrType = _attr.getType();
                if (tempType.getType() == _attrType.getType()) {
                    _attr.initialValue = value;
                    globalVariables.set(i, _attr);
                    return;
                }
            }
        }
    }

    public ArrayList<TMLSDMessage> getMessages() {
        return messages;
    }

    public String toString() {
        return this.name + " : " + this.type;
    }

}

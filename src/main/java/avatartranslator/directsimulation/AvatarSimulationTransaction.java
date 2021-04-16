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


package avatartranslator.directsimulation;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarStateMachineElement;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Class AvatarSimulationTransaction
 * Avatar: notion of transaction in simulation
 * Creation: 14/12/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 14/12/2010
 */
public class AvatarSimulationTransaction {

    public static long ID;
    public static Hashtable<AvatarStateMachineElement, Integer> hashOfAllElements;
    public static LinkedList<AvatarStateMachineElement> allExecutedElements;

    public AvatarBlock block;
    public AvatarSimulationBlock asb;
    public AvatarStateMachineElement executedElement;
    public AvatarStateMachineElement concernedElement; // Used for communication
    public AvatarSimulationTransaction linkedTransaction;
    public long initialClockValue;
    public long duration;
    public long clockValueWhenFinished;
    public long id;
    public long bunchid;

    // Indicates whether the transaction is a silent transaction, or not
    // Silent means that the transaction was automatically selecteed by the simulator ->
    // not assumed to be part of RG or state comparison computation
    public boolean silent;


    public Vector<String> attributeValues;
    public Vector<String> actions;

    public int x, y; // for graphical representation only
    public long stamp;

    public AvatarSimulationAsynchronousTransaction sentMessage;
    public AvatarSimulationAsynchronousTransaction receivedMessage;

    public boolean isBroadcast;
    public boolean isSolo; // Used in broadcast transactions to know whether the signal was forwarded to other elements, or not
    public boolean isLost; // Used in lossy channel to know whether the message was lost or not

    public AvatarSimulationTransaction(AvatarStateMachineElement _executeElement) {
        executedElement = _executeElement;
        addExecutedElement(executedElement);
        duration = 0;
    }

    public int[] getAttributeValues() {
        if (attributeValues == null) {
            return new int[0];
        }

        int[] vals = new int[attributeValues.size()];
        for(int i=0; i<vals.length; i++) {
            String v = attributeValues.get(i);
            if (v.equals("true")) {
                vals[i] = 1;
            } else if (v.equals("false")) {
                vals[i] = 0;
            } else {
                vals[i] = Integer.decode(v);
            }
        }

        return vals;
    }

    public static int[] getAttributeValues(Vector<String> attributeValues) {
        if (attributeValues == null) {
            return new int[0];
        }

        int[] vals = new int[attributeValues.size()];
        for(int i=0; i<vals.length; i++) {
            String v = attributeValues.get(i);
            if (v.equals("true")) {
                vals[i] = 1;
            } else if (v.equals("false")) {
                vals[i] = 0;
            } else {
                vals[i] = Integer.decode(v);
            }
        }

        return vals;
    }

    public static void reinit() {
        ID = 0;
        allExecutedElements = new LinkedList<AvatarStateMachineElement>();
        hashOfAllElements = new Hashtable<AvatarStateMachineElement, Integer>();
    }

    public static void addExecutedElement(AvatarStateMachineElement _asme) {
        if (!allExecutedElements.contains(_asme)) {
            allExecutedElements.add(_asme);
        }

        Integer val = hashOfAllElements.get(_asme);
        if (val == null) {
            hashOfAllElements.put(_asme, 1);
        } else {
            hashOfAllElements.put(_asme, 1 + val);
        }

    }

    public static void removeExecutedElement(AvatarStateMachineElement _asme) {
        if (!allExecutedElements.contains(_asme)) {
            return;
        }

        Integer val = hashOfAllElements.get(_asme);
        if (val == null) {
            return;
        }

        hashOfAllElements.put(_asme, val - 1);


    }

    public static synchronized long setID() {
        long tmp = ID;
        ID++;
        return tmp;
    }

    public static synchronized void setID(long _id) {
        ID = _id;
    }

    public String toString() {
        String res = "" + id + " bunchid:" + bunchid + " @" + clockValueWhenFinished + "/ " + duration + ": " + executedElement + " in block " + block.getName();
        if (silent) {
            res += " (silent)";
        }
        if (isBroadcast) {
            if (isSolo) {
                res += " (solo broadcast)";
            } else {
                res += " (broadcast)";
            }
        }
        res += "\nattributes=";
        for (String s : attributeValues) {
            res += s + " ";
        }
        if (actions != null) {
            int cpt = 0;
            res += "\n";
            for (String action : actions) {
                res += "action#" + cpt + ": " + action + " ";
                cpt++;
            }
        }
        return res;
    }

    public String getAttributesString() {
        String res = "";
        for (String s : attributeValues) {
            res += s + " ";
        }
        return res;
    }

    public String getActionsString() {
        if ((actions == null) || (actions.size() == 0)) {
            return "null";
        }

        String res = "";

        int cpt = 0;
        //res+= "\n";
        for (String action : actions) {
            res += "action#" + cpt + ": " + action + " ";
            cpt++;
        }

        return res;
    }

    public boolean setAttributeValue(int _index, String _value) {
        if (_index >= attributeValues.size()) {
            return false;
        }
        attributeValues.set(_index, _value);
        return true;

    }
}

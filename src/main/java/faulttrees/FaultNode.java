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

package faulttrees;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class AttackNode Creation: 24/01/2018
 *
 * @author Ludovic APVRILLE
 * @version 1.0 24/01/2018
 */
public abstract class FaultNode extends FaultElement {
    private Fault resultingFault; // If no resulting attack -> error!
    private ArrayList<Fault> inputFaults;
    private ArrayList<Integer> inputValues;
    protected String type = "";

    public FaultNode(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        inputFaults = new ArrayList<Fault>();
        inputValues = new ArrayList<Integer>();
    }

    // At least one input and one output
    public boolean isWellFormed() {
        if (resultingFault == null) {
            return false;
        }

        return inputFaults.size() >= 1;

    }

    public void setResultingFault(Fault _fault) {
        resultingFault = _fault;
    }

    public Fault getResultingFault() {
        return resultingFault;
    }

    public ArrayList<Fault> getInputFaults() {
        return inputFaults;
    }

    public void addInputFault(Fault _fault, Integer _val) {
        inputFaults.add(_fault);
        inputValues.add(_val);
    }

    public int hasNegativeAttackNumber() {
        for (int i = 0; i < inputValues.size(); i++) {
            int atti = inputValues.get(i).intValue();
            if (atti < 0) {
                return i;
            }
        }
        return -1;
    }

    public int hasUniqueAttackNumber() {
        for (int i = 0; i < inputValues.size() - 1; i++) {
            int atti = inputValues.get(i).intValue();
            for (int j = i + 1; j < inputValues.size(); j++) {
                // myutil.TraceManager.addDev("i=" + i + " j=" + j + " size=" + attacks.size());
                int attj = inputValues.get(j).intValue();
                // myutil.TraceManager.addDev("i=" + atti.getName() + " j=" + attj.getName() + "
                // size=" + attacks.size());
                if (atti == attj) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        String ret = name + "/" + type + " Incoming faults: ";
        for (Fault att : inputFaults) {
            ret += att.getName() + " ";
        }

        if (resultingFault == null) {
            ret += " No resulting attack";
        } else {
            ret += " Resulting attack:" + resultingFault.getName();
        }

        return ret;
    }

    // Order attacks according to the Integer value
    public void orderFaults() {
        ArrayList<Fault> newFaults = new ArrayList<Fault>();
        ArrayList<Integer> newInputValues = new ArrayList<Integer>();

        for (Integer i : inputValues) {
            newInputValues.add(i);
        }

        // sort newInputValues
        Collections.sort(newInputValues);

        for (Integer i : newInputValues) {
            int index = inputValues.indexOf(i);
            newFaults.add(inputFaults.get(index));
        }

        inputFaults = newFaults;
        inputValues = newInputValues;
    }

}

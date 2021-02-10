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


package attacktrees;

import myutil.TraceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Class AttackNode
 * Creation: 10/04/2015
 *
 * @author Ludovic APVRILLE
 * @version 1.0 10/04/2015
 */
public abstract class AttackNode extends AttackElement {
    protected Attack resultingAttack; // If no resulting attack -> error!
    protected ArrayList<Attack> inputAttacks;
    protected ArrayList<Integer> inputValues;
    protected String type = "";

    public AttackNode(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        inputAttacks = new ArrayList<Attack>();
        inputValues = new ArrayList<Integer>();
    }

    // At least one input and one output
    public boolean isWellFormed() {
        if (resultingAttack == null) {
            return false;
        }

        return inputAttacks.size() >= 1;

    }


    public void setResultingAttack(Attack _attack) {
        resultingAttack = _attack;
    }

    public Attack getResultingAttack() {
        return resultingAttack;
    }

    public ArrayList<Attack> getInputAttacks() {
        return inputAttacks;
    }

    public void addInputAttack(Attack _attack, Integer _val) {
        inputAttacks.add(_attack);
        inputValues.add(_val);
    }

    public String toString() {
        String ret = name + "/" + type + " Incoming attacks: ";
        for (Attack att : inputAttacks) {
            ret += att.getName() + " ";
        }

        if (resultingAttack == null) {
            ret += " No resulting attack";
        } else {
            ret += " Resulting attack:" + resultingAttack.getName();
        }

        return ret;
    }

    public int hasNegativeAttackNumber() {
        for(int i=0; i<inputValues.size(); i++) {
            int atti = inputValues.get(i).intValue();
            if (atti < 0) {
                return i;
            }
        }
        return -1;
    }

    public int hasUniqueAttackNumber() {
        for(int i=0; i<inputValues.size()-1; i++) {
            int atti = inputValues.get(i).intValue();
            for (int j = i + 1; j < inputValues.size(); j++) {
                //myutil.TraceManager.addDev("i=" + i + " j=" + j + " size=" + attacks.size());
                int attj = inputValues.get(j).intValue();
                //myutil.TraceManager.addDev("i=" + atti.getName() + " j=" + attj.getName() + " size=" + attacks.size());
                if (atti == attj) {
                    return i;
                }
            }
        }
        return -1;
    }

    // Order attacks according to the Integer value
    public void orderAttacks() {
        ArrayList<Attack> newAttacks = new ArrayList<Attack>();
        ArrayList<Integer> newInputValues = new ArrayList<Integer>();

        for (Integer i : inputValues) {
            newInputValues.add(i);
        }

        // sort newInputValues
        Collections.sort(newInputValues);

        for (Integer i : newInputValues) {
            int index = inputValues.indexOf(i);
            newAttacks.add(inputAttacks.get(index));
        }

        inputAttacks = newAttacks;
        inputValues = newInputValues;
    }

    public int getLowestCost(int _experience) {

        //TraceManager.addDev("In node: " + this.getClass());

        if ((inputAttacks == null) || (inputAttacks.size() == 0)) {
            return -1;
        }

        // Built from the lower attacks. Assume all attacks are necessary

        int cost = 0;
        for(Attack attack: inputAttacks) {
            int ret = attack.getLowestCost(_experience);
            if (ret == -1) {
                return -1;
            }
            cost += ret;
        }

        return cost;
    }

}

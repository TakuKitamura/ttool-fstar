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

import java.awt.*;
import java.util.ArrayList;


/**
 * Class Attack
 * Creation: 10/04/2015
 *
 * @author Ludovic APVRILLE
 * @version 1.0 10/04/2015
 */
public class Attack extends AttackElement {

    public final static int EXPERIENCE_BEGINNER = 0;
    public final static int EXPERIENCE_AVERAGE = 1;
    public final static int EXPERIENCE_EXPERT = 2;

    public final static String EXPERIENCES [] = {"Beginner", "Intermediate", "Expert"};


    private AttackNode originNode; // If no origin node -> leaf attack
    private ArrayList<AttackNode> destinationNodes;
    private boolean isRoot;
    private boolean isEnabled = true;

    private int attackCost;
    private int attackExperience;


    public Attack(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        destinationNodes = new ArrayList<>();
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean _root) {
        isRoot = _root;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean _enabled) {
        isEnabled = _enabled;
    }

    public void setOriginNode(AttackNode _node) {
        originNode = _node;
    }

    public void addDestinationNode(AttackNode _node) {
        destinationNodes.add(_node);
    }

    public void setAttackCost(int _attackCost) {
        attackCost = _attackCost;
    }

    public void setAttackExperience(int _experience) {
        attackExperience = _experience;
    }

    public int getAttackCost() {
        return attackCost;
    }

    public int getAttackExperience() {
        return attackExperience;
    }


    public boolean isLeaf() {
        return (originNode == null);
    }

    public boolean isFinal() {
        return destinationNodes.size() == 0;

    }

    public boolean canPerformAttack(int _resource, int _expertise) {

        // Leaf attack?
        if (originNode == null) {
            return (attackCost <= _resource) && (attackExperience <= _expertise);
        }

        // Intermediate attack. Needs to compute its resulting cost and experience
        int cost = originNode.getLowestCost(_expertise);
        if (cost == -1) {
            // nothing was found
            return false;
        } else {
            return cost <= _resource;
        }
    }

    public int getLowestCost(int _expertise) {
        if (originNode == null) {
            if (attackExperience <= _expertise) {
                return attackCost;
            } else {
                return -1;
            }
        }

        return originNode.getLowestCost(_expertise);

    }

}

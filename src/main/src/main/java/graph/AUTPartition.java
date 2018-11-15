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


package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Class AUTPartition
 * Creation : 06/01/2017
 * * @version 1.0 06/01/2017
 *
 * @author Ludovic APVRILLE
 */
public class AUTPartition {


    public ArrayList<AUTBlock> blocks;
    // Blocks are expected to be mutually exclusive
    // in terms of states they contain.

    public AUTPartition() {
        blocks = new ArrayList<AUTBlock>();
    }

    public void addBlock(AUTBlock _bl) {
        blocks.add(_bl);
    }

    public void addIfNonEmpty(AUTBlock _b) {
        if (_b.size() > 0) {
            addBlock(_b);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("");
        for (AUTBlock block : blocks) {
            sb.append("(" + block.toString() + ")");
        }
        return sb.toString();
    }


    // List of blocks that has a state that has an
    // output "elt" transition
    public LinkedList<AUTBlock> getI(AUTElement _elt, AUTBlock _b) {
        LinkedList<AUTBlock> listI = new LinkedList<AUTBlock>();
        for (AUTBlock b : blocks) {
            if (b.hasStateOf(_b)) {
                listI.add(b);
            }
        }
        return listI;
    }

    public boolean removeBlock(AUTBlock _b) {
        return blocks.remove(_b);
    }

    public AUTBlock getBlockWithState(int id) {
        for (AUTBlock b : blocks) {
            if (b.hasState(id)) {
                return b;
            }
        }
        return null;
    }

    public int getHashCode() {
        int []values = new int[blocks.size()];
        int cpt = 0;
        for(AUTBlock b: blocks) {
            values[cpt] = b.getHashValue();
            cpt ++;
        }
        return Arrays.hashCode(values);

    }


}

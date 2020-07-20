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





package avatartranslator.modelchecker;


import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
   * Class SpecificationState
   * Coding of a state
   * Creation: 31/05/2016
   * @version 1.0 31/05/2016
   * @author Ludovic APVRILLE
 */
public class SpecificationState implements Comparable<SpecificationState>  {
    public SpecificationBlock [] blocks;
    public int hashValue;
    public boolean hashComputed;
    public long id;
    public LinkedList<SpecificationLink> nexts; // The RG is there
    public boolean property; //trace the property check at this state
    public boolean elaborated; //true only if the elaboration has been completed

    
    public SpecificationState() {
        hashComputed = false;
        property = false;
        elaborated = false;
//        distance = 0;
    }

    // blocks must not be null
    public void computeHash(int blockValues) {
        int[] hash = new int[blockValues];
        int cpt = 0;
        for(int i=0; i<blocks.length; i++) {
            for(int j=0; j<blocks[i].values.length; j++) {
                hash[cpt] = blocks[i].values[j];
                cpt++;
            }
            //TraceManager.addDev("hash[" + i + "]=" + hash[i]);
        }
        hashValue = Arrays.hashCode(hash);
        hashComputed = true;
    }

    public int getHash(int blockValues) {
        if (!hashComputed) {
            computeHash(blockValues);
        }
        return hashValue;
    }
    
    public int getPartialHash(int index) {
        return Arrays.hashCode(blocks[index].values);
    }

    public void setInit(AvatarSpecification _spec, boolean _ignoreEmptyTransitions) {
        int cpt = 0;
        // Initialize blocks
        // Blocks : h to 0, variables to their starting values, state to starting state.
        blocks = new SpecificationBlock[_spec.getListOfBlocks().size()];

        for(AvatarBlock block: _spec.getListOfBlocks()) {
            blocks[cpt] = new SpecificationBlock();
            blocks[cpt].init(block, _ignoreEmptyTransitions, true);
            cpt ++;
        }

        //computeHash(getBlockValues());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("id: " + id);
	if (blocks != null) {
	    for(int i=0; i<blocks.length; i++) {
		sb.append("\n  "+i + ": " + blocks[i].toString());
	    }
	}
        return sb.toString();
    }

    public SpecificationState advancedClone() {
        SpecificationState st = new SpecificationState();
        st.blocks = new SpecificationBlock[blocks.length];
        for(int i=0; i<blocks.length; i++) {
            st.blocks[i] = blocks[i].advancedClone();
        }
        return st;
    }

    // Increase the clock of the blocks not in the transition
    // and having a timed transition.
    // Otherwise, puts the one of others to 0
    public void increaseClockOfBlocksExcept(SpecificationTransition _st) {
        SpecificationBlock sb;
        for(int i=0; i<blocks.length; i++) {
            sb = blocks[i];
            if (!(_st.hasBlockIndex(i))) {
                sb.values[SpecificationBlock.CLOCKMIN_INDEX] += _st.clockMin;
                sb.values[SpecificationBlock.CLOCKMAX_INDEX] += _st.clockMax;
        		sb.values[SpecificationBlock.CLOCKMIN_INDEX] = Math.min(sb.values[SpecificationBlock.CLOCKMIN_INDEX], sb.maxClock);
        		sb.values[SpecificationBlock.CLOCKMAX_INDEX] = Math.min(sb.values[SpecificationBlock.CLOCKMAX_INDEX], sb.maxClock);
            } else {
                sb.values[SpecificationBlock.CLOCKMIN_INDEX] = 0;
                sb.values[SpecificationBlock.CLOCKMAX_INDEX] = 0;
            }
        }
    }

    public int getBlockValues() {
        int cpt = 0;
        for(int i=0; i<blocks.length; i++) {
            cpt += blocks[i].values.length;
            //TraceManager.addDev("hash[" + i + "]=" + hash[i]);
        }
        return cpt;
    }
    
    public int getNextsSize() {
        if (nexts == null) {
            return 0;
        } else {
            return nexts.size();
        }
    }

    public void addNext(SpecificationLink sl) {
	if (nexts == null) {
	    nexts = new LinkedList<SpecificationLink>();
	}
	nexts.add(sl);
    }

    public boolean isDeadlock() {
	if (nexts == null) {
	    return true;
	}
	return (nexts.size() == 0);
    }

    public int getWeightOfTransitionTo(int destinationState) {
	if (nexts == null) {
	    return 0;
	}
	for(SpecificationLink sl: nexts) {
	    if (sl.destinationState.id == destinationState) {
		return 1;
	    }
	}
	return 0;
    }

    public void freeUselessAllocations() {
        blocks = null;
    }

    public int compareTo( SpecificationState _s ) {
	return ((int)id) - (int)(_s.id);
    }

    // Returns false in case of invalid property
    public boolean checkProperty(SafetyProperty _sp) {
	if (_sp.hasError()) {
	    return false;
	}

	// Two cases
	// 1. block.state
	// 2. bool expr
	


	
	
	return true;
    }

}

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
import avatartranslator.AvatarTransition;

/**
   * Class SpecificationTransition
   * Coding of a block
   * Creation: 02/06/2016
   * @version 1.0 02/06/2016
   * @author Ludovic APVRILLE
 */
public class SpecificationTransition  {
    public int clockMin, clockMax;

    public boolean fromStateWithMoreThanOneTransition;
    
    public int[] blocksInt;

    public AvatarTransition[] transitions;

    public String infoForGraph;


    public SpecificationTransition() {
    }

    public void init(int _nbOfElements, AvatarTransition _at, AvatarBlock _ab, SpecificationBlock _sb, int _blockIndex) {
        transitions = new AvatarTransition[_nbOfElements];
        transitions[0] = _at;        

        blocksInt = new int[_nbOfElements];
        blocksInt[0] = _blockIndex;
    }

    public int getType() {
        if (transitions == null) {
            return AvatarTransition.UNDEFINED;
        }
        return transitions[0].type;

    }

    public void makeFromTwoSynchronous(SpecificationTransition _tr1, SpecificationTransition _tr2) {
        int nbOfElements = 2;
        transitions = new AvatarTransition[nbOfElements];
        transitions[0] = _tr1.transitions[0];
        transitions[1] = _tr2.transitions[0];

        blocksInt = new int[nbOfElements];
        blocksInt[0] = _tr1.blocksInt[0];
        blocksInt[1] = _tr2.blocksInt[0];

        clockMin = Math.max(_tr1.clockMin, _tr2.clockMin);
        clockMax = Math.max(_tr1.clockMax, _tr2.clockMax);

    }

    public boolean hasBlockOf(SpecificationTransition _tr) {
        if (transitions == null) {
            return false;
        }

        if (_tr.transitions == null) {
            return false;
        }

        for (int i=0; i<transitions.length; i++) {
            for(int j=0; j<_tr.transitions.length; j++) {
                if (transitions[i].getBlock() == _tr.transitions[j].getBlock()) {
                    return true;
                }
            }
        }

        return false;
    }

    
    public boolean hasBlockIndex(int _index) {
        if (blocksInt == null) {
            return false;
        }

        for(int i=0; i<blocksInt.length; i++) {
            if (blocksInt[i] == _index) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
        String ret = "Trans: ";

//        if (blocks != null) {
//            for (int i=0; i<blocks.length; i++) {
//                ret += "/ Block" + i  + ": " + blocks[i].getName();
//            }
//        }
        if (transitions != null) {
            for (int i = 0; i < transitions.length; i++) {
                ret += "/ Block" + i  + ": " + transitions[i].getBlock().getName() + "/" + transitions[i];
        	}
        }
        
        return ret;
    }
    
} // Class

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

import avatartranslator.AvatarAttribute;
import avatartranslator.AvatarBlock;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Class SpecificationBlock
 * Coding of a block
 * Creation: 31/05/2016
 *
 * @author Ludovic APVRILLE
 * @version 1.0 31/05/2016
 */
public class SpecificationBlock {

    public static final int HEADER_VALUES = 3;

    public static final int STATE_INDEX = 0;
    public static final int CLOCKMIN_INDEX = 1;
    public static final int CLOCKMAX_INDEX = 2;
    public static final int ATTR_INDEX = 3;

    public int[] values; // state in block, clockmin, clockmax, variables
    public int maxClock;

    public SpecificationBlock() {
    }

    public SpecificationBlock(Vector<String> _valuesOfVariables) {
        values = new int[_valuesOfVariables.size() + 3];
        for (int i = 0; i < _valuesOfVariables.size(); i++) {
            try {
                values[i + 3] = Integer.decode(_valuesOfVariables.get(i));
            } catch (Exception e) {
            }
        }
    }

    public int getHash() {
        return Arrays.hashCode(values);
    }

    public void init(AvatarBlock _block, boolean _ignoreEmptyTransitions, boolean compress) {
        List<AvatarAttribute> attrs = _block.getAttributes();
        //TraceManager.addDev("Nb of attributes:" + attrs.size());
        //TraceManager.addDev("in block=" + _block.toString());
        int booleanIndex = _block.getBooleanOffset();
        int optRatio = _block.getAttributeOptRatio();
        if (!compress || booleanIndex == -1) {
            values = new int[HEADER_VALUES + attrs.size()];
        } else {
            if (optRatio > 1) {
                values = new int[HEADER_VALUES + (booleanIndex + optRatio - 1) / optRatio + ((attrs.size() - booleanIndex + 31) / 32)];
            } else {
                values = new int[HEADER_VALUES + booleanIndex + ((attrs.size() - booleanIndex + 31) / 32)];
            }
        }

        // Initial state
        if (_ignoreEmptyTransitions) {
            values[STATE_INDEX] = _block.getIndexOfRealStartState();
        } else {
            values[STATE_INDEX] = _block.getIndexOfStartState();
        }

        // Clock
        values[CLOCKMIN_INDEX] = 0;
        values[CLOCKMAX_INDEX] = 0;

        // Attributes
        int cpt = HEADER_VALUES;
        //String initial;
        if (!compress) {
            for (AvatarAttribute attr : attrs) {
                values[cpt++] = attr.getInitialValueInInt();
            }
        } else {
            int i = 0;
            for (AvatarAttribute attr : attrs) {
                if (i < booleanIndex) {
                    int val = attr.getInitialValueInInt();
                    if (optRatio == 2) {
                        val &= 0xFFFF;
                    } else if (optRatio == 4) {
                        val &= 0xFF;
                    }
                    if (i % optRatio == 0) {
                        values[cpt] = val;
                    } else {
                        values[cpt] |= val << ((i % optRatio) * (32 / optRatio));
                    }
                    if (i % optRatio + 1 == optRatio || i + 1 == booleanIndex) {
                        cpt++;
                    }
                } else if (((i - booleanIndex) % 32) == 0) {
                    values[cpt] = attr.getInitialValueInInt() & 0x1;
                } else {
                    values[cpt] |= (attr.getInitialValueInInt() & 0x1) << ((i - booleanIndex) % 32);
                    if (((i - booleanIndex) % 32) == 31) {
                        cpt++;
                    }
                }
                i++;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Hash=");
        //sb.append(getHash());
        for (int i = 0; i < values.length; i++) {
            sb.append(" ");
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public SpecificationBlock advancedClone() {
        SpecificationBlock sb = new SpecificationBlock();
        sb.values = values.clone();
        sb.maxClock = maxClock;
        return sb;
    }

    public boolean hasTimedTransition() {
        return true;
    }
}

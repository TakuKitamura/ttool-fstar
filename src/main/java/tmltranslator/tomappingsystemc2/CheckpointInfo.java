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

package tmltranslator.tomappingsystemc2;

public class CheckpointInfo {

    public int[] _depChannels = null;
    public int[] _ctrlFlowJoinDefs = null;
    public int[] _killedDefs = null;
    public int[] _varsOutOfScope = null;
    public int[] _depSrcDef = null;
    public int _reasonCode = 0;
    private int _opMode = 0;

    public final static int CHANNEL_DEP = 1;
    public final static int CTRL_FLOW_JOIN = 2;
    public final static int KILLED_DEF = 4;
    public final static int VAR_OUT_OF_SCOPE = 8;
    public final static int DEP_SRC = 16;

    public void setOperationMode(int iOpMode) {
        _opMode = iOpMode;
    }

    public void setDefinitions(int[] iDefs) {
        switch (_opMode) {
            case CTRL_FLOW_JOIN:
                _ctrlFlowJoinDefs = iDefs;
                break;
            case KILLED_DEF:
                _killedDefs = iDefs;
                break;
            default:
        }
    }

    public void setVariableID(int iVarID, int iLength) {
        if (_opMode == VAR_OUT_OF_SCOPE) {
            _varsOutOfScope = new int[iLength];
            _varsOutOfScope[iVarID >>> 5] |= 1 << (iVarID & 0x1F);
        }
    }

    public void incorporate(CheckpointInfo i2ndPoint) {
        _depChannels = mergeArrays(_depChannels, i2ndPoint._depChannels);
        _ctrlFlowJoinDefs = mergeArrays(_ctrlFlowJoinDefs, i2ndPoint._ctrlFlowJoinDefs);
        _killedDefs = mergeArrays(_killedDefs, i2ndPoint._killedDefs);
        _varsOutOfScope = mergeArrays(_varsOutOfScope, i2ndPoint._varsOutOfScope);
        _depSrcDef = mergeArrays(_depSrcDef, i2ndPoint._depSrcDef);
        _reasonCode |= i2ndPoint._reasonCode;
    }

    private int[] mergeArrays(int[] iArray1, int[] iArray2) {
        if (iArray1 == null)
            return iArray2;
        if (iArray2 == null)
            return iArray1;
        int[] aMergedArray = new int[iArray1.length];
        for (int i = 0; i < iArray1.length; i++)
            aMergedArray[i] = iArray1[i] | iArray2[i];
        return aMergedArray;
    }
}

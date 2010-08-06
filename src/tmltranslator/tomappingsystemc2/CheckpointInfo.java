package tmltranslator.tomappingsystemc2;

import java.util.*;
import tmltranslator.*;

public class CheckpointInfo{

	public int[] _depChannels = null;
	public int[] _ctrlFlowJoinDefs = null;
	public int[] _killedDefs = null;
	public int[] _varsOutOfScope = null;
	public int[] _depSrcDef = null;
	public int _reasonCode = 0;
	private int _opMode = 0;

	public final static int CHANNEL_DEP=1;
	public final static int CTRL_FLOW_JOIN=2;
	public final static int KILLED_DEF=4;
	public final static int VAR_OUT_OF_SCOPE=8;
	public final static int DEP_SRC=16;

	public void setOperationMode(int iOpMode){
		_opMode = iOpMode;
	}

	public void setDefinitions(int[] iDefs){
		switch(_opMode){
		case CTRL_FLOW_JOIN:
			_ctrlFlowJoinDefs = iDefs; break;
		case KILLED_DEF:
			_killedDefs = iDefs; break;
		default:
		}
	}

	public void setVariableID(int iVarID, int iLength){
		if(_opMode == VAR_OUT_OF_SCOPE){
			_varsOutOfScope = new int[iLength];
			_varsOutOfScope[iVarID >>> 5] |= 1 << (iVarID & 0x1F);
		}
	}

	public void incorporate(CheckpointInfo i2ndPoint){
		_depChannels = mergeArrays(_depChannels, i2ndPoint._depChannels);
		_ctrlFlowJoinDefs = mergeArrays(_ctrlFlowJoinDefs, i2ndPoint._ctrlFlowJoinDefs);
		_killedDefs = mergeArrays(_killedDefs, i2ndPoint._killedDefs);
		_varsOutOfScope = mergeArrays(_varsOutOfScope, i2ndPoint._varsOutOfScope);
		_depSrcDef = mergeArrays(_depSrcDef, i2ndPoint._depSrcDef);
		_reasonCode |= i2ndPoint._reasonCode;
	}

	private int[] mergeArrays(int[] iArray1, int[] iArray2){
		if (iArray1==null) return iArray2;
		if (iArray2==null) return iArray1;
		int[] aMergedArray = new int[iArray1.length];
		for(int i=0; i< iArray1.length; i++)
			aMergedArray[i] = iArray1[i] | iArray2[i];
		return aMergedArray;
	}
}

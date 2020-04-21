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


package avatartranslator;

import myutil.TraceManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Class AvatarActionOnSignal
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 */
public class AvatarActionOnSignal extends AvatarStateMachineElement {
    private AvatarSignal signal;
    private List<String> values;
	private boolean checkLatency;
	private List<AvatarExpressionAttribute> actionAttr;


	public AvatarActionOnSignal(String _name, AvatarSignal _signal, Object _referenceObject ) {
		this( _name, _signal, _referenceObject, false );
	}

	public AvatarActionOnSignal(	String _name,
									AvatarSignal _signal,
									Object _referenceObject,
									boolean _isCheckable ) {
		super( _name, _referenceObject, _isCheckable, false );
		
		signal = _signal;
		values = new LinkedList<String>();
		actionAttr = null;
	}

    public AvatarSignal getSignal() {
        return signal;
    }

    public void addValue(String _val) {
        values.add(_val);
    }

    public List<String> getValues() {
        return values;
    }

    public int getNbOfValues() {
        return values.size();
    }

    public String getValue(int _index) {
        return values.get(_index);
    }

    public boolean isSending() {
        return signal.isOut();
    }

	public boolean getCheckLatency(){
		return checkLatency;
	}

	public void setCheckLatency(boolean b){
		checkLatency=b;
	}

    public boolean isReceiving() {
        return signal.isIn();
    }
    
    public boolean buildActionSolver(AvatarBlock block) {
        AvatarExpressionAttribute aea;
        boolean res = true;
        int i = 0;
        actionAttr = new ArrayList<AvatarExpressionAttribute>();
        for (String val : values) {
            aea = new AvatarExpressionAttribute(block, val);
            actionAttr.add(aea);
            res &= aea.hasError();
        }
        return res;
    }
    
    public AvatarExpressionAttribute getExpressionAttribute(int index) {
        return actionAttr.get(index);
    }

    public AvatarActionOnSignal basicCloneMe(AvatarStateMachineOwner _block) {
    	//TraceManager.addDev("I HAVE BEEN CLONED: " + this);
    	AvatarSignal sig = _block.getAvatarSignalWithName(getSignal().getName());
    	if (sig != null) {
    		AvatarActionOnSignal aaos = new AvatarActionOnSignal(getName() + "__clone", sig, getReferenceObject(), isCheckable()/*, isChecked()*/);
    		for(int i=0; i<getNbOfValues(); i++) {
    			aaos.addValue(getValue(i));
    		}
    		return aaos;
    	} else {
    		TraceManager.addDev("NULL signal in new spec: " + getSignal().getName());
    	}

    	return null;
    }

    public String getExtendedName() {
    	if (getSignal() == null) {
    		String s = getName() + " refobjt=" + referenceObject.toString();
    		TraceManager.addDev("Null signal" + " res=" + s);
    		return s;
    	}
    	if (getName() == null) {
    		TraceManager.addDev("Null name");
    	}

    	return getName() + ":" + getSignal().getName();
    }

    public String getNiceName() {
        if (signal.isIn()) {
            return "Receiving signal " + signal.getName();
        } else {
            return "Sending signal " + signal.getName();
        }
    }

    public void translate (AvatarTranslator translator, Object arg) {
        translator.translateActionOnSignal (this, arg);
    }
}

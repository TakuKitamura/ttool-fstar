/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class AvatarTransition
 * Creation: 20/05/2010
 * @version 1.0 20/05/2010
 * @author Ludovic APVRILLE
 * @see
 */

package avatartranslator;

import java.util.*;


public class AvatarTransition extends AvatarStateMachineElement {
	private String guard = "[ ]";
	private String minDelay = "", maxDelay = "";
	private String minCompute = "", maxCompute = "";
	
	private LinkedList<String> actions; // actions on variable, or method call
	
    public AvatarTransition(String _name, AvatarSignal _signal, Object _referenceObject) {
        super(_name, _referenceObject);
		actions = new LinkedList<String>();
    }
	
	public String getGuard() {
		return guard;
	}
	
	public void setGuard(String _guard) {
		guard = _guard;
	}
	
	public int getNbOfAction() {
		return actions.size();
	}
	
	public String getAction(int _index) {
		return value.get(_index);
	}
	
	public void setDelays(String _minDelay, String _maxDelay) {
		minDelay = _minDelay;
		maxDelay = _maxDelay;
	}
	
	public void setComputes(String _minCompute, String _maxCompute) {
		minCompute = _minCompute;
		maxCompute = _maxCompute;
	}
	
	public String getMinDelay() {
		return minDelay;
	}
	
	public String getMaxDelay() {
		return maxDelay;
	}
	
	public String getMinCompute() {
		return minCompute;
	}
	
	public String getMaxCompute() {
		return maxCompute;
	}
    
}
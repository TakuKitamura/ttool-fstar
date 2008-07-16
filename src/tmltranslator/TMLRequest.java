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
 * Class TMLRequest
 * Creation: 22/11/2005
 * @version 1.0 22/11/2005
 * @author Ludovic APVRILLE
 * @see
 */

package tmltranslator;

import java.util.*;


public class TMLRequest extends TMLCommunicationElement {
    protected Vector params; // List of various types of parameters
    protected ArrayList<TMLTask> originTasks; // list of tasks from which request starts
    protected TMLTask destinationTask;
    
    public TMLRequest(String name, Object reference) {
        super(name, reference);
        params = new Vector();
        originTasks = new ArrayList<TMLTask>();
    }
    
    public int getNbOfParams() {
        return params.size();
    }
    
    public void addParam(TMLType _type) {
        params.add(_type);
    }
    
    public TMLType getType(int i) {
        if (i<getNbOfParams()) {
            return (TMLType)(params.elementAt(i));
        } else {
            return null;
        }
    }
    
    public void setDestinationTask(TMLTask _task) {
        destinationTask = _task;
    }
    
    public TMLTask getDestinationTask() {
        return destinationTask;
    }
    
    
    public void addOriginTask(TMLTask _task) {
        originTasks.add(_task);
    }
	
	public boolean isAnOriginTask(TMLTask _task) {
		return (originTasks.contains(_task));
	}
    
    public ArrayList<TMLTask> getOriginTasks() {
        return originTasks;
    }
	
	public String getNameExtension() {
		return "request__";
	}
	
	public void addParam(String _list) {
		String []split = _list.split(",");
		TMLType type;
		for(int i=0; i<split.length; i++) {
			if (TMLType.isAValidType(split[i])) {
				type = new TMLType(TMLType.getType(split[i]));
				addParam(type);
			}
		}
    }
	
	public boolean isBlockingAtOrigin() {
		return false;
	}
	
	public boolean isBlockingAtDestination() {
		return true;
	}

}
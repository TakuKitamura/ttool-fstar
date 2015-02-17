/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class TMLChannel
   * Creation: 22/11/2005
   * @version 1.0 22/11/2005
   * @author Ludovic APVRILLE
   * @see
   */

package tmltranslator;

import java.util.*;


public class TMLChannel extends TMLCommunicationElement {

    public static final int BRBW = 0;
    public static final int BRNBW = 1;
    public static final int NBRNBW = 2;

    private int size;
    private int type;
    private int max;

    // Used on for 1 -> 1 channel
    protected TMLTask origin, destination; 
    protected TMLPort originPort, destinationPort; // Not used by the simulator

    // Used for 1 -> many channel, or for many -> 1 channel
    protected ArrayList<TMLTask> originTasks, destinationTasks;
    protected ArrayList<TMLPort> originPorts, destinationPorts;


    private int priority;


    public TMLChannel(String name, Object reference) {
        super(name, reference);
	originTasks = new ArrayList<TMLTask>();
	destinationTasks = new ArrayList<TMLTask>();
	originPorts = new ArrayList<TMLPort>();
	destinationPorts = new ArrayList<TMLPort>();
    }


    // Complex channels
    public boolean isBasicChannel() {
	return (originTasks.size() == 0);
    }

    public boolean isBadComplexChannel() {
	if ((originTasks.size() == 1) && (destinationTasks.size() >= 1)) {
	    return false;
	}

	if ((destinationTasks.size() == 1) && (originTasks.size() >= 1)) {
	    return false;
	}

	return true;
    }

    public void toBasicIfPossible() {
	if ((originTasks.size() ==1) && (destinationTasks.size() ==1)) {
	    origin = originTasks.get(0);
	    destination = destinationTasks.get(0);
	    originPort = originPorts.get(0);
	    destinationPort = destinationPorts.get(0);
	    originTasks = new ArrayList<TMLTask>();
	    destinationTasks = new ArrayList<TMLTask>();
	    originPorts = new ArrayList<TMLPort>();
	    destinationPorts = new ArrayList<TMLPort>();
	}
    }

    public void addTaskPort(TMLTask _task, TMLPort _port, boolean isOrigin) {
	if (isOrigin) {
	    originTasks.add(_task);
	    originPorts.add(_port);
	} else {
	    destinationTasks.add(_task);
	    destinationPorts.add(_port);
	}
    }

    public ArrayList<TMLTask> getOriginTasks() {
	return originTasks;
    }

    public ArrayList<TMLTask> getDestinationTasks() {
	return destinationTasks;
    }

    public ArrayList<TMLPort> getOriginPorts() {
	return originPorts;
    }

    public ArrayList<TMLPort> getDestinationPorts() {
	return destinationPorts;
    }




    // Basic channels
    public void setTasks(TMLTask _origin, TMLTask _destination) {
        origin = _origin;
        destination = _destination;
    }

    public void setPorts(TMLPort _origin, TMLPort _destination) {
	originPort = _origin;
	destinationPort = _destination;
    }
  
    public TMLTask getOriginTask() {
        return origin;
    }

    public TMLTask getDestinationTask() {
        return destination;
    }

    public TMLPort getOriginPort() {
	return originPort;
    }

    public TMLPort getDestinationPort() {
	return destinationPort;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setSize(int _size) {
        size = _size;
    }

    public int getSize() {
        return size;
    }

    public void setMax(int _max) {
        max = _max;
    }

    public int getMax() {
        return max;
    }

    public void setType(int _type) {
        type = _type;
    }

    public void setTypeByName(String _name) {
        if (_name.compareTo("BRBW") == 0) {
            type = BRBW;
        }
        if (_name.compareTo("BRNBW") == 0) {
            type = BRNBW;
        }
        if (_name.compareTo("NBRNBW") == 0) {
            type = NBRNBW;
        }

    }

    public int getType() {
        return type;
    }

    public boolean isInfinite() {
        return (type != 0);
    }

    public String getNameExtension() {
        return "channel__";
    }

    public static String getStringType(int type) {
        switch(type) {
        case BRBW:
            return "BRBW";
        case BRNBW:
            return "BRNBW";
        case NBRNBW:
            return "NBRNBW";
        }
        return "unknown type";
    }

    public boolean isBlockingAtOrigin() {
        switch(type) {
        case BRBW:
            return true;
        case BRNBW:
            return false;
        case NBRNBW:
            return false;
        }
        return false;
    }

    public boolean isBlockingAtDestination() {
        switch(type) {
        case BRBW:
            return true;
        case BRNBW:
            return true;
        case NBRNBW:
            return false;
        }
        return false;
    }
}

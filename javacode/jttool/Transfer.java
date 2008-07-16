
/**
 * Class Transfer
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 28/07/2005
 * @version 1.1 28/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

public class Transfer extends UnicastRemoteObject implements TransferInterface {
    public int port;
    public String hostname;
    private LinkedList data;
    private LinkedList requests;
    private int maxSize = 1500;
    private int nbReq = 0; 

    public Transfer(String _hostname, int _port) throws RemoteException {
	super();
	hostname = _hostname;
	port = _port;
	data = new LinkedList();
	requests = new LinkedList();

	register();
	
    }

    public void register() {
	if (System.getSecurityManager() == null) {
	    //System.setSecurityManager(new RMISecurityManager());
	    System.setSecurityManager(new RMISecurityManager() {
		    public void checkConnect (String host, int port){}
		    public void checkConnect (String host, int port, Object context) {}
		});

	}

	String s = "";
	try {
	    s = "//" + hostname + "/RMI" + port;
	    Naming.rebind(s, this);
	} catch (Exception e) {
	    System.out.println("Error when binding " + s + ": " + e.getMessage());
	    e.printStackTrace();
	}
    }


    public void asynchronousSend(String s) throws RemoteException {
	manageRequest(s);
    }

     public synchronized SynchroSchemes putRequest(SynchroSchemes sss, int provPort, String provHost) {
	
	if (hasCompatiblePacket(sss, provPort, provHost) != -1) {
	    return getAnswer(sss, provPort, provHost);
	}

	nbReq++;
	requests.add(sss);
	notifyAll();

	while(hasCompatiblePacket(sss, provPort, provHost) == -1) {
	    try {
		wait();
	    } catch (InterruptedException ie) {
	    }
	}

	requests.remove(sss);
	return getAnswer(sss, provPort, provHost);
    }

    public int hasCompatiblePacket(SynchroSchemes sss, int provPort, String provHost) {
	SynchroSchemes tmps;
	boolean found = false;
	int index = 0;
	ListIterator iterator = data.listIterator();

	while(iterator.hasNext()) {
	    tmps = (SynchroSchemes)(data.get(index));
	    if (isCompatible(tmps, sss, provPort, provHost)) {
		found = true;
		break;
	    }
	    index ++;
	}

	if (found) {
	    return index;
	}

	return -1;
    }


    public SynchroSchemes getAnswer(SynchroSchemes sss, int provPort, String provHost) {
	int index = hasCompatiblePacket(sss, provPort, provHost);

	if (index == -1) {
	    return null;
	}

	SynchroSchemes tmps = (SynchroSchemes)(data.get(index));
	sss.completeSynchro(tmps);

	data.remove(index);

	return sss;
    }


    /* Code performed by the server once a packet has been received */
    public synchronized void manageRequest(String s) {
	SynchroSchemes sss = new SynchroSchemes(s);;
	System.out.println("Got packet: " + sss);
	data.add(sss);
	if (data.size() > maxSize) {
	    data.removeFirst();
	}

	// Check whether a request can be served. If yes, decrement request nb
	if (checkIfCanBeServed(sss)) {
	    nbReq --;
	    System.out.println("Nb request =" + nbReq);
	}

	notifyAll();
    }

    public boolean isCompatible(SynchroSchemes dpdata, SynchroSchemes sss, int provPort, String provHost) {
	
	if (!(dpdata.isCompatibleWith(sss))) {
	    return false;
	}
	
	return true;
    }

    public boolean checkIfCanBeServed(SynchroSchemes sss) {
	System.out.println("Nb of requests in list: " + requests.size());
	SynchroSchemes tmps;
	ListIterator iterator = requests.listIterator();
	
	while(iterator.hasNext()) {
	    tmps = (SynchroSchemes)(iterator.next());
	    if (tmps.isCompatibleWith(sss)) {
		return true;
	    }
	}
	return false;
    } 

}




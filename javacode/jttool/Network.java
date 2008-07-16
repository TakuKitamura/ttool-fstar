
/**
 * Class Network
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 21/07/2005
 * @version 1.1 21/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.net.*;
import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class Network {

    public static final int NO_PROTOCOL = 0;
    public static final int UDP = 1;
    public static final int TCP = 2;
    public static final int RMI = 3;

    public static final Network net = new Network();

    private LinkedList dataNet;
    private LinkedList receiveUDPThreads; 

    private LinkedList rmiserverobjs;
    private LinkedList rmiclientobjs;
    
    public Network() {
	dataNet = new LinkedList();
	receiveUDPThreads = new LinkedList();
	rmiserverobjs = new LinkedList();
	rmiclientobjs = new LinkedList();
    }

    public SynchroSchemes action(SynchroSchemes sss, int protocol, int localPort, int destPort, String localHost, String destHost) {
	//System.out.println("action on protocol = " + protocol);
	if (sss.isOnlySending()) {
	    System.out.println("Sending with" + protocol);
	    return actionSending(sss, protocol, localPort, destPort, localHost, destHost);
	}
	if (sss.isOnlyReceiving()) {
	    System.out.println("Receiving with " + protocol);
	    return actionReceiving(sss, protocol, localPort, destPort, localHost, destHost);
	}
	return sss;
    }

    public SynchroSchemes actionSending(SynchroSchemes sss, int protocol, int localPort, int destPort, String localHost, String destHost) {
	if (protocol == UDP) {
	    DataNet data = notMatchCreate(dataNet, localPort, destPort, destHost);
	    if (data == null) {
		return sss;
	    }
	    data.sendUDP(sss.getStringPacket());
	} else {
	    if (protocol == RMI) {
		TransferInterface ti = notMatchCreate(destPort, destHost);
		if (ti == null) {
		    return sss;
		}
		try {
		    ti.asynchronousSend(sss.getStringPacket());
		} catch (RemoteException re) {
		    System.out.println("RemoteException occured when sending packet to " + destHost + "/" + destPort + ": " + re.getMessage());
		    re.printStackTrace();
		    return sss;
		}
	    }
	}
	return sss;
    }

    public SynchroSchemes actionReceiving(SynchroSchemes sss, int protocol, int localPort, int destPort, String localHost, String destHost) {
	if (protocol == UDP) {
	    ReceiveUDPThread rut = getCompatibleReceiveUDPThread(localPort, destPort, destHost);
	    if (rut == null) {
		return sss;
	    }
	    return rut.putRequest(sss, destPort, destHost);
	} else { 
	    if (protocol == RMI) {
		Transfer tr = getCompatibleTransfer(localPort, localHost);
		if (tr == null) {
		    return sss;
		}
		return tr.putRequest(sss, destPort, destHost);
	    }
	    
	}
	return sss;
    }


    public synchronized DataNet notMatchCreate(LinkedList ll, int _localPort, int _destPort, String _destHost) {
	DataNet data = null;
	boolean found = false;
	ListIterator iterator = dataNet.listIterator();
	while(iterator.hasNext()) {
	    data = (DataNet)(iterator.next());
	    if (data.match(_localPort, _destPort, _destHost)) {
		found = true;
		break;
	    }
	}

	if (!found) {
	    data = new DataNet();
	    data.host = new String(_destHost);
	    data.localPort = _localPort;
	    data.destPort = _destPort;
	    data.createNet();
	    if (data.socket == null) {
		return null;
	    }   
	    ll.add(data);
	}

	return data;
    }
    
    public synchronized ReceiveUDPThread getCompatibleReceiveUDPThread(int localPort, int _destPort, String _destHost) {
	ListIterator iterator = receiveUDPThreads.listIterator();
	ReceiveUDPThread rut;

	while(iterator.hasNext()) {
	    rut = (ReceiveUDPThread)(iterator.next());
	    if (rut.localPort == localPort) {
		//System.out.println("Found already built rut");
		return rut;
	    }
	}

	DataNet data = notMatchCreate(dataNet, localPort, _destPort, _destHost);
	

	rut = new ReceiveUDPThread();
	receiveUDPThreads.add(rut);
	rut.localPort = localPort;
	rut.socket = data.socket;
	Thread t = new Thread(rut);
	t.setDaemon(true);
	t.start();
	
	return rut;
    }

     public synchronized Transfer getCompatibleTransfer(int localPort, String localHost) {
	ListIterator iterator = rmiserverobjs.listIterator();
	Transfer tr;

	while(iterator.hasNext()) {
	    tr = (Transfer)(iterator.next());
	    if (tr.port == localPort) {
		//System.out.println("Found already built rut");
		return tr;
	    }
	}
	
	try {
	    tr = new Transfer(localHost, localPort);
	} catch (RemoteException re) {
	    System.out.println("Exception in creating new Tranfer object: " + re.getMessage());
	    return null;
	}
	rmiserverobjs.add(tr);
	return tr;
     }

    public synchronized TransferInterface notMatchCreate(int destPort, String destHost) {
	ListIterator iterator = rmiclientobjs.listIterator();
	DataTransferInterface dti;

	while(iterator.hasNext()) {
	    dti = (DataTransferInterface)(iterator.next());
	    if ((dti.port == destPort) && (destHost.compareTo(dti.host) ==0)) {
		return dti.ti;
	    }
	}

	// not found!
	String s = "//" + destHost + "/RMI" + destPort;
	TransferInterface  ti = null;

	try {
	    ti = (TransferInterface)Naming.lookup(s);
	} catch (Exception e) {
	    System.out.println("Creation of stub to remote object on " + destHost + "/" + destPort + " failed: " + e.getMessage());
	    e.printStackTrace();
	    return null;
	}

	dti = new DataTransferInterface(ti, destHost, destPort);
	rmiclientobjs.add(dti);
	return ti;
    }
}




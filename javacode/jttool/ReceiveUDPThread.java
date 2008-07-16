/**
 * Class ReceiveUDPThread
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 25/07/2005
 * @version 1.1 25/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReceiveUDPThread implements Runnable {

    public int localPort;
    public DatagramSocket socket;
    public boolean go;
    private DatagramPacket dp;
    private byte[] buf;
    private int bufsize = 1500;
    private LinkedList dps;
    private LinkedList data;
    private LinkedList requests;
    private int maxSize = 1500;
    private int nbReq = 0; 

    public ReceiveUDPThread() {
	dps = new LinkedList();
	data = new LinkedList();
	requests = new LinkedList();
	go = true;
    }

    public void run() {
	createSocket();

	while(go == true) {
	    waitForRequest();
	    getRequest();
	    manageRequest();
	}
    }

    public void createSocket() {
	if (socket == null) {
	    try { 
		socket = new DatagramSocket(localPort);
	    } catch (SocketException se) {
		System.err.println("Exception 10: " + se.getMessage());
		socket = null;
	    }
	}
    }

    public synchronized void waitForRequest() {
	System.out.println("Waiting for request");
	//Thread.currentThread().yield();
	while(nbReq == 0) {
	    try {
		wait();
	    } catch (InterruptedException ie) {
		
	    }
	} 
	System.out.println("Accepting request");
    }

    public void getRequest() {
	System.out.println("Waiting for a packet");
	buf = new byte[bufsize];
	dp = new DatagramPacket(buf, bufsize);
	try {
	    socket.receive(dp);
	    System.out.println("Got a packet");
	} catch (Exception e) {
	    System.err.println("Exception 11: " + e.getMessage());
	}
	
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


    /* Code performed by the server once a packet has been received */
    public synchronized void manageRequest() {
	SynchroSchemes sss = getData(dp);
	System.out.println("Got packet: " + sss);
	dps.add(dp);
	data.add(sss);
	if (dps.size() > maxSize) {
	    dps.removeFirst();
	    data.removeFirst();
	}

	// Check whether a request can be served. If yes, decrement request nb
	if (checkIfCanBeServed(sss)) {
	    nbReq --;
	    System.out.println("Nb request =" + nbReq);
	}

	notifyAll();
    }

    public int hasCompatiblePacket(SynchroSchemes sss, int provPort, String provHost) {
	DatagramPacket dp;
	SynchroSchemes tmps;
	boolean found = false;
	int index = 0;
	ListIterator iterator = dps.listIterator();

	while(iterator.hasNext()) {
	    dp = (DatagramPacket)(iterator.next());
	    tmps = (SynchroSchemes)(data.get(index));
	    if (isCompatible(dp, tmps, sss, provPort, provHost)) {
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
	dps.remove(index);

	return sss;
    }

    public boolean isCompatible(DatagramPacket dp, SynchroSchemes dpdata, SynchroSchemes sss, int provPort, String provHost) {
	InetAddress inet;

	if (dp.getPort() != provPort) {
	    return false;
	}

	if (!(dpdata.isCompatibleWith(sss))) {
	    return false;
	}

	try {
	    inet = InetAddress.getByName(provHost);
	} catch (UnknownHostException uhe) {
	    System.err.println("Exception 12: " + uhe.getMessage());
	    return false;
	}
	
	if (!(dp.getAddress().equals(inet))) {
	    return false;
	}

	return true;
    }

    public SynchroSchemes getData(DatagramPacket dp) {
	String s = new String(dp.getData());
	
	return new SynchroSchemes(s);
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



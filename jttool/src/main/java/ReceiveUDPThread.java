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




package jttool;

import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Class ReceiveUDPThread
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 25/07/2005
 * @version 1.1 25/07/2005
 * @author Ludovic APVRILLE
 */
public class ReceiveUDPThread implements Runnable {

    public int localPort;
    public DatagramSocket socket;
    public boolean go;
    private DatagramPacket dp;
    private byte[] buf;
    private int bufsize = 1500;
    private LinkedList<DatagramPacket> dps;
    private LinkedList<SynchroSchemes> data;
    private LinkedList<SynchroSchemes> requests;
    private int maxSize = 1500;
    private int nbReq = 0; 

    public ReceiveUDPThread() {
	dps = new LinkedList<>();
	data = new LinkedList<>();
	requests = new LinkedList<>();
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
	SynchroSchemes tmps;
	boolean found = false;
	int index = 0;

        for (DatagramPacket dp: this.dps) {
	    tmps = data.get(index);
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

	SynchroSchemes tmps = data.get(index);
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
	
        for (SynchroSchemes tmps: this.requests) {
	    if (tmps.isCompatibleWith(sss)) {
		return true;
	    }
	}
	return false;
    } 
}



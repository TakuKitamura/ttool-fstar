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


/**
 * Class DataNet
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 21/07/2005
 * @version 1.1 21/07/2005
 * @author Ludovic APVRILLE
 */
public class DataNet {

    public String host;
    public int localPort;
    public int destPort;
    public InetAddress inet;
    public DatagramSocket socket;
    

    public DataNet() {}

    public boolean match(int _localPort, int _destPort, String destHost) {
	if ((localPort == _localPort) && (destPort == _destPort) && (host.compareTo(destHost) == 0)) {
	    if ((inet != null) && (socket != null)) {
		return true;
	    }
	} 
	return false;
    }

    public void createNet() {
	 try {
		inet = InetAddress.getByName(host);
	    } catch (UnknownHostException uhe) {
		System.err.println("Exception: " + uhe.getMessage());
		inet = null;
		socket = null;
	    }
	 try { 
	    socket = new DatagramSocket(localPort);
	 } catch (SocketException se) {
	     System.err.println("Exception: " + se.getMessage());
		inet = null;
		socket = null;
	 }
    }

    public void sendUDP(String packet) {
	System.out.println("Sending " + packet + "to" + inet);
	byte [] buf = packet.getBytes();
	DatagramPacket dp = new DatagramPacket(buf, buf.length, inet, destPort);
	try {
	    socket.send(dp);
	} catch (IOException ioe) {
	    System.err.println("Exception: " + ioe.getMessage());
	}
    }
}

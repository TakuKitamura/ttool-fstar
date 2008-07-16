/**
 * Class DataNet
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 21/07/2005
 * @version 1.1 21/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.io.*;
import java.net.*;

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

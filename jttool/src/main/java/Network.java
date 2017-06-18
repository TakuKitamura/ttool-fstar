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

import java.net.*;
import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;


/**
 * Class Network
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 21/07/2005
 * @version 1.1 21/07/2005
 * @author Ludovic APVRILLE
 */
public class Network {

    public static final int NO_PROTOCOL = 0;
    public static final int UDP = 1;
    public static final int TCP = 2;
    public static final int RMI = 3;

    public static final Network net = new Network();

    private LinkedList<DataNet> dataNet;
    private LinkedList<ReceiveUDPThread> receiveUDPThreads; 

    private LinkedList<Transfer> rmiserverobjs;
    private LinkedList<DataTransferInterface> rmiclientobjs;
    
    public Network() {
	dataNet = new LinkedList<>();
	receiveUDPThreads = new LinkedList<>();
	rmiserverobjs = new LinkedList<>();
	rmiclientobjs = new LinkedList<>();
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


    public synchronized DataNet notMatchCreate(LinkedList<DataNet> ll, int _localPort, int _destPort, String _destHost) {
        for (DataNet data: this.dataNet) {
	    if (data.match(_localPort, _destPort, _destHost)) {
                return data;
	    }
	}

        DataNet data = new DataNet();
        data.host = new String(_destHost);
        data.localPort = _localPort;
        data.destPort = _destPort;
        data.createNet();
        if (data.socket == null) {
            return null;
        }   
        ll.add(data);

	return data;
    }
    
    public synchronized ReceiveUDPThread getCompatibleReceiveUDPThread(int localPort, int _destPort, String _destHost) {
        for (ReceiveUDPThread rut: this.receiveUDPThreads) {
	    if (rut.localPort == localPort) {
		//System.out.println("Found already built rut");
		return rut;
	    }
	}

	DataNet data = notMatchCreate(dataNet, localPort, _destPort, _destHost);
	

	ReceiveUDPThread rut = new ReceiveUDPThread();
	receiveUDPThreads.add(rut);
	rut.localPort = localPort;
	rut.socket = data.socket;
	Thread t = new Thread(rut);
	t.setDaemon(true);
	t.start();
	
	return rut;
    }

     public synchronized Transfer getCompatibleTransfer(int localPort, String localHost) {
        for (Transfer tr: this.rmiserverobjs) {
	    if (tr.port == localPort) {
		//System.out.println("Found already built rut");
		return tr;
	    }
	}
	
	try {
	    Transfer tr = new Transfer(localHost, localPort);
            rmiserverobjs.add(tr);
            return tr;
	} catch (RemoteException re) {
	    System.out.println("Exception in creating new Tranfer object: " + re.getMessage());
	}

        return null;
     }

    public synchronized TransferInterface notMatchCreate(int destPort, String destHost) {
        for (DataTransferInterface dti: this.rmiclientobjs) {
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

	DataTransferInterface dti = new DataTransferInterface(ti, destHost, destPort);
	rmiclientobjs.add(dti);
	return ti;
    }
}




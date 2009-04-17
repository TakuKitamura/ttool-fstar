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
* Class RemoteConnection
* For remote control of the simulator
* Creation: 16/04/2009
* @version 1.1 16/04/2009
* @author Ludovic APVRILLE
* @see
*/

package remotesimulation;

import java.io.*;
import java.net.*;
import javax.swing.*;


public class RemoteConnection {
    
    private static String NO_HOST = "Application has no execution host";
    private static String INET = "Bad internet address for host ";
    private static String SERV_NOT_RESP = "Server not responding on ";
    private static String IO_ERROR = "Communication pb with server ";
    
    private String host;
    //private String cmd;
    private static int port = 3490;
    //private int portString = -1;
    //private int portString2 = -1;
    private Socket clientSocket = null;
    private BufferedReader in;
    //private DataInputStream in2;
    private PrintStream out;
    //private int offset = 0;
    
    private boolean go;
    
    public RemoteConnection(String _host, int _port) {
        host = _host;
		port = _port;
    }
	
	public RemoteConnection(String _host) {
        host = _host;
    }
	
	public void connect() throws RemoteConnectionException {
        InetAddress ina = null;
        
        //System.out.println("Connecting on port " + portNet);
        
        if (host == null) {
            throw new RemoteConnectionException(NO_HOST);
        }
        
        try {
            ina = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RemoteConnectionException(INET + host);
        }
        
        try {
            clientSocket = new Socket(ina, port);
        } catch (IOException io) {
            throw new RemoteConnectionException(SERV_NOT_RESP+host);
        }
        
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //in2 = new DataInputStream(clientSocket.getInputStream());
            out = new PrintStream(clientSocket.getOutputStream());
            //System.out.println("Connected on port " + portNet);
        } catch (Exception e) {
            throw new RemoteConnectionException(SERV_NOT_RESP+host);
        }
    }
	
	public void disconnect() throws RemoteConnectionException {
		try {
			clientSocket.close();
		} catch (IOException io) {
            throw new RemoteConnectionException(IO_ERROR + host);
        }
	}
	
	public void send(String s) throws RemoteConnectionException {
		s = s .trim();
		if (s.length() == 0) {
			return;
		}
        System.out.println("Sending: " + s);
        try {
            out.print(s);
            out.flush();
        } catch (Exception e) {
            throw new RemoteConnectionException(IO_ERROR);
        }
    }
	
	 public String readOneLine() throws RemoteConnectionException {
        int nb;
        String s = null;
        try {
            s = in.readLine();
        } catch(IOException io) {
            throw new RemoteConnectionException(IO_ERROR);
        }
		
		if (s == null) {
			throw new RemoteConnectionException(IO_ERROR);
		}
		
		if (s.equals("null")) {
			throw new RemoteConnectionException(IO_ERROR);
		}
        
        return s;
    }
    
}
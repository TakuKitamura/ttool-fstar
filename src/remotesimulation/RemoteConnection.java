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
	
	
	
	
    
    
    /*public void stopFillJTA() throws RemoteConnectionException {
        sendKillProcessRequest();
        go = false;
        closeConnect();
    }
    
    public int getId() throws LauncherException {
		connect(port);
		send("00");
		int id = readId();
		closeConnect();
		return id;
    }
    
    public int freeId(int id) throws LauncherException {
		connect(port);
		send("0" + id);
		int idret = readId();
		if (idret != id) {
			throw new LauncherException(ID_FAILED);
		}
		return idret;
		
    }
    
    public void sendProcessRequest() throws LauncherException {
        connect(port);
        send("1" + cmd);
        readPortString();
        closeConnect();
    }
    
    public void sendProcessRequest(String cmd1, String cmd2) throws LauncherException {
        connect(port);
        send("3" + cmd1);
        int id1 = readPortString();
        closeConnect();
        
        connect(port);
        send("3" + cmd2);
        int id2 = readPortString();
        closeConnect();
        
        connect(port);
        send("2" + id1 + " " + id2);
        readReturnPipedProcesses();
        closeConnect();
        
        connect(port);
        send("4" + id1);
        readReturnPipedProcesses();
        closeConnect();
        
        connect(port);
        send("4" + id2);
        readReturnPipedProcesses();
        closeConnect();
        
        portString = id2;
        portString2 = id1;
    }
    
    public void sendFileData(String fileName, String data) throws LauncherException {
        connect(port);
        send("7" + fileName);
        sendFileData(data);
        send("7" + fileName);
        readReturn();
        closeConnect();
    }
    
    public String getFileData(String fileName) throws LauncherException {
        connect(port);
        send("8" + fileName);
        String s = readDataUntilCompletion();
        closeConnect();
        return s;
    }
    
    public void deleteFile(String fileName) throws LauncherException {
        connect(port);
        send("9" + fileName);
        readReturn();
        closeConnect();
    }
    
    public void sendKillProcessRequest() throws LauncherException {
        connect(port);
        send("6" + portString);
        closeConnect();
        if(portString2 != -1) {
            connect(port);
            send("6" + portString2);
            closeConnect();
        }
    }
    
    public void sendKillAllProcessRequest() throws LauncherException {
        connect(port);
        send("5");
        closeConnect();
    }
    
    public String getDataFromProcess() throws LauncherException {
        go = true;
        StringBuffer bf = new StringBuffer();
        
        //System.out.println("Connect");
        connect(portString);
        
        String s;
        
        //System.out.println("Waiting for data");
        while (((s = readProcessData()) != null) && (go == true)) {
            bf.append(s + "\n");
        }
        
        //System.out.println("no more data : stopped");
        closeConnect();
        //System.out.println("Closed");
        
        return new String(bf);
    }
    
    public void fillJTA(JTextArea jta) throws LauncherException {
        go = true;
        
        //System.out.println("Connect");
        connect(portString);
        
        String s;
        
        //System.out.println("Waiting for data");
        while (((s = readProcessData()) != null) && (go == true)) {
            jta.append(s + "\n");
        }
        
        //System.out.println("no more data : stopped");
        closeConnect();
        //System.out.println("Closed");
        
    }
    
    public void closeConnect() throws LauncherException {
        try {
            clientSocket.close();
        } catch (IOException io) {
            throw new LauncherException(SERV_NOT_RESP+host);
        }
    }
    
	
    
    
    
    private void send(String s) throws LauncherException {
        //System.out.println("Sending: " + s);
        try {
            out.println(s);
            out.flush();
        } catch (Exception e) {
            throw new LauncherException(IO_ERROR);
        }
    }
    
    private void sendFileData(String data) throws LauncherException {
        //System.out.println("Sending data");
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;
        try {
            while((s = br.readLine()) != null) {
                send("8" + s);
            }
        } catch (Exception e) {
            throw new  LauncherException(FILE_FAILED);
        }
    }
    
	
    
    private String readProcessData() throws LauncherException {
        int nb;
        String s = null;
        try {
            s = in.readLine();
            nb = Integer.decode(s.substring(0,1)).intValue();
        } catch(IOException io) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb == 5) {
            return null;
        }
        
        s = s.substring(1, s.length());
        if (s == null) {
            s = "";
        }
        
        return s;
    }
    
    private String readDataUntilCompletion() throws LauncherException {
        int nb=0, nbTotal, cpt = 0;
        String s = null;
        StringBuffer ret = new StringBuffer();
        char []c = new char[BUFSIZE+1];
        int read;
        
        try {
            //System.out.println("Reading first data ");
            s = in.readLine();
            nb = Integer.decode(s.substring(0,1)).intValue();
            if (nb == 8) {
                nbTotal = Integer.decode(s.substring(1,s.length())).intValue();
                //System.out.println("Total= " + nbTotal);
                while (((cpt < nbTotal) && (read = in.read(c, 0, Math.min(BUFSIZE, nbTotal - cpt))) > -1)) {
                    //s = new String(c, 0, read);
                    //System.out.println("Nb read: " + read + " size of s =" + s.length());
                    //nb = Integer.decode(s.substring(0,1)).intValue();
                    //ret.append(s.substring(0, s.length()));
					ret.append(c, 0, read);
                    cpt += read;
                }
                // Read last info
				//System.out.println("Reading last info");
				nb = readReturn();
				//System.out.println("Return = " + nb);
                //read = in.read(c, 0, 1);
				//s = new String(c, 0, read);
                //System.out.println("Last s=" + s + " read=" + read);
                //nb = Integer.decode(s.substring(0,1)).intValue();
                //System.out.println("Last info=" + nb);
				//nb= 3;
            }
        } catch(IOException io) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb != 3) {
            throw new LauncherException(FILE_FAILED);
        }
        
        return new String(ret);
    }
    
	
    
    private int readReturn() throws LauncherException {
        int nb;
        String s = null;
        
        try {
			//System.out.println("Reading line");
            s = in.readLine();        
			//System.out.println("Line read");
			//System.out.println("Converting nb s=>" + s + "<");
            nb = Integer.decode(s.substring(0,1)).intValue();
			//System.out.println("Nb = " + nb);
        } catch(IOException io) {
			System.out.println("Exception 0");
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb != 3) {
			System.out.println("Exception 1");
            throw new LauncherException(FILE_FAILED);
        }
		return nb;
    }
    
    private int readId() throws LauncherException {
        int nb;
        String s = null;
        
        try {
            s = in.readLine();
            nb = Integer.decode(s.substring(0,1)).intValue();
        } catch(IOException io) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb == 0) {
            throw new LauncherException(ID_FAILED);
        }
        
        return nb;
    }
    
    private void readReturnPipedProcesses() throws LauncherException {
        int nb;
        String s = null;
        
        try {
            s = in.readLine();
            nb = Integer.decode(s.substring(0,1)).intValue();
        } catch(IOException io) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb != 3) {
            throw new LauncherException(PROC_FAILED);
        }
    }
    
    private int readPortString() throws LauncherException {
        int nb;
        String s = null;
        try {
            s = in.readLine();
            nb = Integer.decode(s.substring(0,1)).intValue();
        } catch(IOException io) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (nb == 2) {
            throw new LauncherException(PROC_FAILED);
        }
        
        portString = -1;
        portString2 = -1;
        try {
            portString = Integer.decode(s.substring(1, s.length())).intValue();
        } catch (Exception e) {
            throw new LauncherException(IO_ERROR);
        }
        
        if (portString <1) {
            throw new LauncherException(PROC_FAILED);
        }
        
        return portString;
    }
    
    private void connect(int portNet) throws LauncherException {
        InetAddress ina = null;
        
        //System.out.println("Connecting on port " + portNet);
        
        if (host == null) {
            throw new LauncherException(NO_HOST);
        }
        
        try {
            ina = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new LauncherException(INET + host);
        }
        
        try {
            clientSocket = new Socket(ina, portNet);
        } catch (IOException io) {
            throw new LauncherException(SERV_NOT_RESP+host);
        }
        
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //in2 = new DataInputStream(clientSocket.getInputStream());
            out = new PrintStream(clientSocket.getOutputStream());
            //System.out.println("Connected on port " + portNet);
        } catch (Exception e) {
            throw new LauncherException(SERV_NOT_RESP+host);
        }
    }
    
    /*private void basicConnect() throws LauncherException {
        InetAddress ina = null;
        
        if (host == null) {
            throw new LauncherException(NO_HOST);
        }
        
        try {
            ina = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new LauncherException(INET + host);
        }
        
        try {
            clientSocket = new Socket(ina, port);
        } catch (IOException io) {
            throw new LauncherException(SERV_NOT_RESP+host);
        }
        
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //in2 = new DataInputStream(clientSocket.getInputStream());
            out = new PrintStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            throw new LauncherException(SERV_NOT_RESP+host);
        }
    }*/
    
}
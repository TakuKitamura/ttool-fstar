/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class ExecutionThread
 * For remote execution of processes
 * Creation: 2001
 * @version 1.1 01/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package launcher;


import java.io.*;
import java.net.*;


class ExecutionThread extends Thread {
    private String cmd;
    private int port;
    private RshServer rsh;
    private ServerSocket server = null;
    private boolean go;
    BufferedReader proc_in;
    private Process proc;
    
    private boolean piped;
    private ExecutionThread et;
    
    private boolean mustWaitForPiped;
    private OutputStream pipe;
    
    private boolean isStarted = false;
    
    
    public ExecutionThread(String _cmd, int _port, RshServer _rsh) {
        cmd = _cmd;
        rsh = _rsh;
        port = _port;
        findPortNumber();
        go = true;
    }
    
    public boolean isStarted() {
        return isStarted;
    }
    
    public void setPiped(ExecutionThread _et) {
        et = _et;
        piped = true;
    }
    
    public void setWaitForPipe() {
        mustWaitForPiped = true;
    }
    
    public synchronized void waitingForPipe() {
        while(pipe == null) {
            try {
                System.out.println("Waiting for pipe");
                wait();
            } catch (InterruptedException ie) {
                
            }
        }
    }
    
    public synchronized void setMyPipe(OutputStream os) {
        pipe = os;
        notifyAll();
    }
    
    
    public int getPort() {
        return port;
    }
    
    private void findPortNumber() {
        for(int i=port + 1; i<port + 1000; i++) {
            try {
                server = new ServerSocket(i);
                server.setSoTimeout(60000);
                port = i;
                return;
            } catch (Exception e) {
            }
        }
    }
    
    private Socket waitForClient() {
        Socket s = null;
        System.out.println("process " + port + " is waiting for client");
        try {
            s = server.accept();
        } catch (Exception e) {
            return null;
        }
        System.out.println("processe " + port + " got client");
        return s;
    }
    
    public void closeConnect(Socket s)  {
        try {
            s.close();
        } catch (IOException io) {
        }
    }
    
    public void stopProcess() {
        go = false;
        proc.destroy();
        proc_in = null;
        //System.out.println("Stop process");
    }
    
    private void respond(PrintStream out, String s) {
        try {
            out.println(s);
            out.flush();
        } catch (Exception e) {
        }
    }
    
    
    public void run() {
        isStarted = true;
        System.out.println("Starting process for command " + cmd);
        proc = null;
        BufferedReader in = null;
        String str;
        
        // print output in pipe
        if (mustWaitForPiped) {
            try {
                proc = Runtime.getRuntime().exec(cmd);
                if (piped) {
                    System.out.println("Giving my pipe to the other");
                    et.setMyPipe(proc.getOutputStream());
                }
                System.out.println("Waiting for pipe");
                waitingForPipe();
                System.out.println("Got pipe");
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                try {
                    while (((str = proc_in.readLine()) != null) && (go == true)){
                        pipe.write((str + "\n").getBytes());
                    }
                } catch (IOException e) {
                    
                }
            } catch (Exception e) {
                System.out.println("Exception [" + e.getMessage() + "] occured when executing " + cmd);
            }
            try {
                pipe.flush();
                pipe.close();
            } catch (Exception e) {
                System.out.println("Exception [" + e.getMessage() + "] occured when executing " + cmd);
            }
            System.out.println("Ending command " + cmd);
            
            // print output on socket
        } else {
            Socket s =  waitForClient();
            if (s == null) {
                System.out.println("Client did not connect on time");
                rsh.removeProcess(this);
                return;
            }
            
            PrintStream out = null;
            
            try {
                System.out.println("Going to start command " + cmd);
                out = new PrintStream(s.getOutputStream(), true);
                
                proc = Runtime.getRuntime().exec(cmd);
                
                if (piped) {
                    System.out.println("Giving my pipe to the other");
                    et.setMyPipe(proc.getOutputStream());
                }
                
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                
                
                while (((str = proc_in.readLine()) != null) && (go == true)){
                    System.out.println("out " + str);
                    respond(out, "4" + str);
                }
                
            } catch (Exception e) {
                System.out.println("Exception [" + e.getMessage() + "] occured when executing " + cmd);
            }
            System.out.println("Ending command " + cmd);
            respond(out, "5");
            if (s != null) {
                closeConnect(s);
            }
        }
        
        if (proc != null) {
            proc.destroy();
        }
        
        rsh.removeProcess(this);
    }
}

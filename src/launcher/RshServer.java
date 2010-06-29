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
 * Class RshServer
 * For remote execution of processes
 * Creation: 2003
 * @version 1.1 01/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package launcher;


import java.io.*;
import java.net.*;
import java.util.*;

public class RshServer {
	public static int PORT_NUMBER = 8375;
	
    private int port = PORT_NUMBER;
    private ServerSocket server = null;
    private int id = 0;
    private Vector processes;
    private int MAX_PROC = 255;
    public static final String VERSION = "0.61";
    private int BUFSIZE = 511;
    private boolean []sessions = new boolean[10]; // 0 is never used.

    public RshServer() {
		System.out.println("Using port: " + port);
        processes = new Vector();
        try {
            server = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Server could not start(Socket pb)");
        }
    }
    
    private void printProcessRunning() {
        System.out.println("Process running:" + processes.size());
    }
    
    
    // Returns 0 if no session id could be found;
    private synchronized int getSessionId() {
      for(int i=1; i<10; i++) {
        if (!sessions[i]) {
          sessions[i] = true;
          return i;
        }
      }
      return 0;
    }
    
    private synchronized void freeSessionId(int id) {
      if ((id > 0) && (id<10)) {
        sessions[id] = false;
      }
    }

    private Socket waitForClient() {
        Socket s = null;
        System.out.println("Waiting for command");
        try {
            s = server.accept();
        } catch (Exception e) {
            return null;
        }
        return s;
    }
    
    private void respond(PrintStream out, String s) {
        try {
            out.println(s);
            out.flush();
        } catch (Exception e) {
        }
    }
    
    private void respondNoln(PrintStream out, String s) {
        //System.out.println("Sending: " + s);
        try {
            out.print(s);
            out.flush();
        } catch (Exception e) {
        }
    }
    
    private int startNewProcess(String path) {
        if (processes.size() >= MAX_PROC) {
            return -1;
        }
        
        ExecutionThread et = new ExecutionThread(path, port, this);
        
        if (et.getPort() == -1) {
            return -1;
        }
        
        processes.addElement(et);
        
        et.start();
        
        return et.getPort();
        
    }
    
    private int createNewProcess(String path) {
        if (processes.size() >= MAX_PROC) {
            return -1;
        }
        
        ExecutionThread et = new ExecutionThread(path, port, this);
        
        if (et.getPort() == -1) {
            return -1;
        }
        
        processes.addElement(et);
        
        return et.getPort();
    }
    
    private boolean startProcess(String idp) {
        int id = 0;
        try {
            id = Integer.decode(idp).intValue();
        } catch (Exception e) {
            return false;
        }
        
        return startProcess(id);
    }
    
    private boolean startProcess(int id) {
        ExecutionThread et;
        for(int i=0; i<processes.size(); i++) {
            et = (ExecutionThread)(processes.elementAt(i));
            if (et.getPort() == id) {
                if (et.isStarted()) {
                    return false;
                }
                et.start();
                return true;
            }
        }
        return false;
    }
    
    private ExecutionThread getExecutionThread(int id) {
        ExecutionThread et;
        for(int i=0; i<processes.size(); i++) {
            et = (ExecutionThread)(processes.elementAt(i));
            if (et.getPort() == id) {
                return et;
            }
        }
        return null;
    }
    
    private boolean pipeProcesses(String str1, String str2) {
        int id1 =0, id2 = 0;
        //System.out.println("Toto1");
        try {
            id1 = Integer.decode(str1).intValue();
            id2 = Integer.decode(str2).intValue();
        } catch (Exception e) {
            return false;
        }
        //System.out.println("Toto2");
        if (id1 == id2) {
            return false;
        }
        //System.out.println("Toto3");
        ExecutionThread et1 = getExecutionThread(id1);
        ExecutionThread et2 = getExecutionThread(id2);
        
        //System.out.println("Toto4");
        
        if ((et1 == null) || (et2 == null)) {
            return false;
        }
        //System.out.println("Toto5");
        
        et1.setWaitForPipe();
        et2.setPiped(et1);
        
        return true;
    }
    
    public void removeProcess(ExecutionThread et) {
        processes.removeElement(et);
        printProcessRunning();
    }
    
    public void killProcess(int id) {
        ExecutionThread et;
        for(int i=0; i<processes.size(); i++) {
            et = (ExecutionThread)(processes.elementAt(i));
            if (et.getPort() == id) {
                et.stopProcess();
                processes.removeElement(et);
                System.out.println("Process " + id + " killed");
                return;
            }
        }
        printProcessRunning();
    }
    
    public void killAllProcesses() {
        ExecutionThread et;
        for(int i=0; i<processes.size(); i++) {
            et = (ExecutionThread)(processes.elementAt(i));
            et.stopProcess();
            processes.removeElement(et);
            System.out.println("Process " + id + " killed");
        }
        printProcessRunning();
    }
    
    private void manageClientRequest(Socket s) {
        String info = null;

        BufferedReader in;
        PrintStream out;
        
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintStream(s.getOutputStream(), true);
        } catch (Exception e) {
            return;
        }
        
        try {
            info = in.readLine();
        } catch (Exception e) {
            return;
        }
        
        if (info.substring(0, 1).equals("0")) { // Session id
           if (info.substring(1, 2).equals("0")) { // Get session id
              int id = getSessionId();
              System.out.println("-> New session id = " + id);
              respond(out, ""+id); // A zero response means error
           } else {
             try {
                 int id = Integer.decode(info.substring(1, 2)).intValue();
                 freeSessionId(id);
                 System.out.println("-> Session id=" + id + " terminated");
                 respond(out, ""+id);
             } catch (Exception e) {
               respond(out, "0");
             }
           }
        } else if (info.substring(0, 1).equals("1")) {
            // start process at once
            int id = startNewProcess(info.substring(1, info.length()));
            if (id <0) {
                respond(out, "2");	 // fail
            } else {
                System.out.println("Process accepted on port " + id);
                respond(out, "3" + id);	// process created
            }
        } else if (info.substring(0, 1).equals("2")) {
            // Piped processes
            System.out.println("Piped processes");
            String str = info.substring(1, info.length());
            String str1, str2;
            int index = str.indexOf(' ');
            System.out.println("index = " + index);
            if (index > 0) {
                str1 = str.substring(0, index);
                str2 = str.substring(index + 1, str.length());
                System.out.println("str = " + str + " str1 = *" + str1 + "* str2 = *" + str2 + "*");
                if (pipeProcesses(str1, str2)) {
                    System.out.println("Making piped processes");
                    respond(out, "3");	 // OK
                } else {
                    System.out.println("Making piped processes FAILED");
                    respond(out, "2");	 // fail
                }
            }  else {
                System.out.println("Making piped processes FAILED");
                respond(out, "2");	 // fail
            }
            
        } else if (info.substring(0, 1).equals("3")) {
            // create process
            int id = createNewProcess(info.substring(1, info.length()));
            if (id <0) {
                respond(out, "2");	 // fail
            } else {
                System.out.println("Process accepted on port " + id);
                respond(out, "3" + id);	// process created
            }
        } else if (info.substring(0, 1).equals("4")) {
            // start already created process
            if (startProcess(info.substring(1, info.length()))) {
                System.out.println("Process started on port " + id);
                respond(out, "3" + id);	// process created
            } else {
                respond(out, "2");	 // fail
            }
        } else if (info.substring(0, 1).equals("5")) {
            // kill all processes
            try {
                killAllProcesses();
            } catch (Exception e) {
                
            }
        }else if (info.substring(0, 1).equals("6")) {
            // kill process
            try {
                int id = Integer.decode(info.substring(1, info.length())).intValue();
                System.out.println("Demand to kill: " + id);
                killProcess(id);
            } catch (Exception e) {
                
            }
        } else if (info.substring(0, 1).equals("7")) {
            //file : put
            String fileName = info.substring(1, info.length());
            makeFileFromData(in, out, fileName);
        } else if (info.substring(0, 1).equals("8")) {
            //file : get
            String fileName = info.substring(1, info.length());
            sendDataFile(in, out, fileName);
        } else if (info.substring(0, 1).equals("9")) {
            //file : delete
            String fileName = info.substring(1, info.length());
            deleteFile(in, out, fileName);
        } else {
            System.exit(0);
        }
    }
    
    private void makeFileFromData(BufferedReader in, PrintStream out, String fileName) {
        System.out.println("Making file " + fileName);
        File file = new File(fileName);
        
        if (!isFileOkForSave(file)) {
            System.out.println("Cannot make file");
            respond(out, "2");	 // fail
            return;
        }
        
        StringBuffer fileData = new StringBuffer();
        String info;
        
        System.out.println("Waiting for file data");
        while(true) {
            try {
                info = in.readLine();
            } catch (Exception e) {
                return;
            }
            
            if ((info == null) || (info.length() == 0)) {
              // Assumes it is an EOF
              System.out.println("Wrong EOF -> assumes it is an EOF");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write((new String(fileData)).getBytes());
                    fos.close();
                } catch (Exception e) {
                    System.out.println("Error when feeding file");
                    respond(out, "2");	 // fail
                    return;
                }
                respond(out, "3");	// file created
                return;

            } else if (info.substring(0, 1).equals("8")) {
                // fileData
                fileData.append(info.substring(1, info.length()) + "\n");
            } else if (info.substring(0, 1).equals("7")) {
                // EOF
                System.out.println("EOF");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write((new String(fileData)).getBytes());
                    fos.close();
                } catch (Exception e) {
                    System.out.println("Error when feeding file");
                    respond(out, "2");	 // fail
                    return;
                }
                respond(out, "3");	// file created
                return;
            } else {
                System.out.println("Unknown PDU (file)");
                respond(out, "2");	 // fail
                return;
            }
        }
    }
    
    private void sendDataFile(BufferedReader in, PrintStream out, String fileName) {
        System.out.println("Sending data of file " + fileName);
        File file = new File(fileName);
        
        if (!isFileOkForRead(file)) {
            //System.out.println("Cannot read file");
            respond(out, "2");	 // fail
            return;
        }
        
        try {
            FileInputStream fis = new FileInputStream(file);
            /*int nb = fis.available();
             
            System.out.println("New byte " + fileName);
            byte [] ba = new byte[nb];
            fis.read(ba);
            System.out.println("Reading " + fileName);
            fis.close();*/
            if (sendData(out, fis)) {
				//System.out.println("Sending 3 info to say OK");
                respond(out, "3");
            } else {
                System.out.println("Sending failed");
                respond(out, "2");
            }
        } catch(Exception e) {
            respond(out, "2");	 // fail
            return;
        }
        System.out.println("Sending completed");
    }
    
    private void deleteFile(BufferedReader in, PrintStream out, String fileName) {
        System.out.println("Deleting " + fileName);
        File file = new File(fileName);
        
        try {
            file.delete();
        } catch(Exception e) {
            respond(out, "2");	 // fail
            return;
        }
        respond(out, "3");
    }
    
    public void startServer() {
        Socket s = null;
        
        
        while(true) {
            // Wait for client request
            printProcessRunning();
            
            s = waitForClient();
            
            if (s != null) {
                manageClientRequest(s);
            }
            
        }
    }
    
    private boolean isFileOkForRead(File file) {
        if (file == null) {
            return false;
        }
        
        try {
            if (!file.exists()) {
                return false;
            }
            if (!file.canRead()) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception file " + e.getMessage());
            return false;
        }
        return true;
    }
    
    private boolean isFileOkForSave(File file) {
        
        if (file == null) {
            return false;
        }
        
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    System.out.println("creation pb");
                    return false;
                }
            }
            if (!file.canWrite()) {
                System.out.println("write pb");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Exception file " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    private boolean sendData(PrintStream out, FileInputStream fis) throws LauncherException {
        System.out.println("Send data ");
        
        byte [] ba = new byte[BUFSIZE];
        int nbRead; 
        
        //StringReader sr = new StringReader(data);
        //BufferedReader br = new BufferedReader(sr);
        try {
            // Sending first line : 8 + nbByte
            respond(out, new String("8" + fis.available()));
			int cpt = 0;
            
            while((nbRead = fis.read(ba, 0, BUFSIZE)) > -1) {
                //respondNoln(out, new String(ba, 0, nbRead));
				out.write(ba, 0, nbRead);
				cpt += nbRead;
            }
			System.out.println("Nb written:" + cpt);
            fis.close();
        } catch (Exception e) {
			System.out.println("Exception when sending file: " + e.getMessage());
            return false;
        }
        return true;
    }
}

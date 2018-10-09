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


package launcher;


import myutil.AESEncryptor;
import myutil.TraceManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class RshServer
 * For remote execution of processes
 * Creation: 2003
 *
 * @author Ludovic APVRILLE
 * @version 2 21/05/2015
 */
public class RshServer {
    public static int PORT_NUMBER = 8375;

    private int port = PORT_NUMBER;
    private ServerSocket server = null;
    //private int id = 0;
    private Vector<ExecutionThread> processes;
    private int MAX_PROC = 255;
    public static final String VERSION = "0.61";
    private int BUFSIZE = 511;
    private boolean[] sessions = new boolean[10]; // 0 is never used.

    private static int MAX_NB_TRY = 25;
    private int nbTry = 0;

    private boolean isSecure = true;
    private String sk;
    public static String iv = "Wh4t4b0utThisKe?";

    public RshServer() {
        startingServer();
    }


    public RshServer(String _sk) {
        sk = _sk;
        startingServer();
    }

    private void startingServer() {
        processes = new Vector<ExecutionThread>();
        int i = 0;

        for (i = 0; i < 100; i++) {
            try {
                server = new ServerSocket(port + i);

                break;
            } catch (Exception e) {

            }
        }

        if (i == 100) {
            TraceManager.addDev("Launching external applications is disabled: no socket is available");
        } else {
            port = port + i;
            launcher.RshClient.PORT_NUMBER = port;
            TraceManager.addDev("Using port: " + port);
        }
    }

    public void setNonSecure() {
        isSecure = false;
    }

    public String getSecretKey() {
        return sk;
    }

    public String getIV() {
        return iv;
    }

    private void printProcessRunning() {
        TraceManager.addDev("Process running:" + processes.size());
    }


    // Returns 0 if no session id could be found;
    private synchronized int getSessionId() {
        for (int i = 1; i < 10; i++) {
            if (!sessions[i]) {
                sessions[i] = true;
                return i;
            }
        }
        return 0;
    }

    private synchronized void freeSessionId(int id) {
        if ((id > 0) && (id < 10)) {
            sessions[id] = false;
        }
    }

    private Socket waitForClient() {
        //Socket s = null;
        TraceManager.addDev("Waiting for command");

        try {
            return server.accept();
        } catch (Exception e) {
            nbTry++;

            return null;
        }
        // return s;
    }

    private void respond(final PrintStream out,
                         final ResponseCode code) {
        respond(out, code, null);
    }

    private void respond(final PrintStream out,
                         final String message) {
        respond(out, null, message);
    }

    private void respond(final PrintStream out,
                         final ResponseCode code,
                         final String message) {
        try {
            SocketComHelper.send(out, code, message);
//        	final StringBuilder sentMess = new StringBuilder();
//        	
//        	if ( code != null ) {
//        		sentMess.append( code.name() );
//        	}
//        	
//        	if ( message != null ) {
//        		sentMess.append( message );
//        	}
//
//            out.println( sentMess.toString() );
//            out.flush();
        } catch (Exception e) {
            TraceManager.addError(e);
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

    private int createNewProcess(final String path) {
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
        ExecutionThread et = getExecutionThread(id);

        if (et == null || et.isStarted()) {
            return false;
        }

        et.start();

        return true;
//        for(int i=0; i<processes.size(); i++) {
//            et = (ExecutionThread)(processes.elementAt(i));
//            if (et.getPort() == id) {
//                if (et.isStarted()) {
//                    return false;
//                }
//                et.start();
//                return true;
//            }
//        }
//        return false;
    }

    private ExecutionThread getExecutionThread(int id) {
        ExecutionThread et;
        for (int i = 0; i < processes.size(); i++) {
            et = processes.elementAt(i);
            if (et.getPort() == id) {
                return et;
            }
        }
        return null;
    }

    private boolean pipeProcesses(String str1, String str2) {
        int id1 = 0, id2 = 0;

        try {
            id1 = Integer.decode(str1).intValue();
            id2 = Integer.decode(str2).intValue();
        } catch (Exception e) {
            return false;
        }

        if (id1 == id2) {
            return false;
        }

        ExecutionThread et1 = getExecutionThread(id1);
        ExecutionThread et2 = getExecutionThread(id2);


        if ((et1 == null) || (et2 == null)) {
            return false;
        }

        et1.setWaitForPipe();
        et2.setPiped(et1);

        return true;
    }

    public void removeProcess(ExecutionThread et) {
        processes.removeElement(et);
        printProcessRunning();
    }

    public boolean killProcess(int id) {
        ExecutionThread et;
        for (int i = 0; i < processes.size(); i++) {
            et = processes.elementAt(i);

            if (et.getPort() == id) {
                et.stopProcess();
                processes.removeElement(et);
                TraceManager.addDev("Process " + id + " killed");

                return true;
            }
        }

        printProcessRunning();

        return false;
    }

    public void killAllProcesses() {
        //ExecutionThread et;
        final Iterator<ExecutionThread> procIt = processes.iterator();

        while (procIt.hasNext()) {
            final ExecutionThread et = procIt.next();
            et.stopProcess();
            procIt.remove();

            TraceManager.addDev("Process " + et.getId() + " killed.");
        }

        // DB: Issue #18: This code does not iterate through all the elements because process.size() changes at every iteration
//        ExecutionThread et;
//        for(int i=0; i<processes.size(); i++) {
//            et = processes.elementAt(i);
//            et.stopProcess();
//            processes.removeElement(et);
//            TraceManager.addDev("Process " + id + " killed");
//        }

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
            TraceManager.addError("Exception when reading client information=" + info, e);

            return;
        }

        if (sk != null) {
            info = checkSecurity(info);
        }

        TraceManager.addDev("Got from client:" + info);

        if (info != null) {
            final RequestCode reqCode = SocketComHelper.requestCode(info);
            TraceManager.addDev("Received request code " + reqCode + ".");
            final String message = SocketComHelper.message(reqCode, info);
            TraceManager.addDev("Received request message '" + message + "'.");

            switch (reqCode) {
                // Session id
                case GET_SESSION_ID: {
//            if (info.substring(0, 1).equals("0")) { // Session id
                    //if (info.substring(1, 2).equals("0")) { // Get session id
                    int id = getSessionId();
                    TraceManager.addDev("-> New session id = " + id);
                    respond(out, "" + id); // A zero response means error

                    break;
                }
                case FREE_SESSION_ID: {
                    //} else {
                    try {
                        int id = Integer.decode(message);//.intValue();
                        freeSessionId(id);
                        TraceManager.addDev("-> Free session id=" + id + " terminated");
                        respond(out, "" + id);
                    } catch (Exception e) {
                        respond(out, ResponseCode.FAILED);

                        TraceManager.addError(e);
                    }

                    break;
                }
                case PROCESS_CREATE_START: {
                    //} else if (info.substring(0, 1).equals("1")) {
                    // start process at once
                    int id = startNewProcess(message);//info.substring(1, info.length()));

                    if (id < 0) {
                        respond(out, ResponseCode.FAILED);       // fail
                    } else {
                        TraceManager.addDev("Process accepted on port " + id);
                        respond(out, ResponseCode.SUCCESS, Integer.toString(id)); // process created
                    }

                    break;
                }
                case PROCESS_CHECK_RETURN_CODE: {
                    final ExecutionThread process = getExecutionThread(Integer.decode(message));

                    if (process == null) {
                        TraceManager.addError("Process " + message + " not created!");
                        respond(out, ResponseCode.FAILED);
                    } else {
                        TraceManager.addDev("Process " + message + " will send its return code.");
                        process.setSendReturnCode(true);
                        respond(out, ResponseCode.SUCCESS); // process created
                    }

                    break;
                }
                case PROCESS_GET_RETURN_CODE: {
                    final ExecutionThread process = getExecutionThread(Integer.decode(message));

                    if (process == null) {
                        TraceManager.addError("Process " + message + " not created!");
                        respond(out, ResponseCode.FAILED);
                    } else {
                        final String retCodeStr = String.valueOf(process.getReturnCode());
                        TraceManager.addDev("Sending return code " + retCodeStr + " for process " + message + ".");
                        respond(out, ResponseCode.SUCCESS, retCodeStr);
                    }

                    break;
                }
                case PROCESS_PIPE: {
                    //else if (info.substring(0, 1).equals("2")) {
                    // Piped processes
                    TraceManager.addDev("Piped processes.");
                    //String str = info.substring(1, info.length());
                    //String str1, str2;
                    final int index = message.indexOf(' ');
                    //TraceManager.addDev("index = " + index);

                    if (index > 0) {
                        final String str1 = message.substring(0, index);
                        final String str2 = message.substring(index + 1);
                        TraceManager.addDev("str = " + message + " str1 = *" + str1 + "* str2 = *" + str2 + "*");

                        if (pipeProcesses(str1, str2)) {
                            TraceManager.addDev("Making piped processes...");
                            respond(out, ResponseCode.SUCCESS);   // OK
                        } else {
                            TraceManager.addDev("Making piped processes failed!");
                            respond(out, ResponseCode.FAILED);   // fail
                        }
                    } else {
                        TraceManager.addDev("Making piped processes FAILED");
                        respond(out, ResponseCode.FAILED);       // fail
                    }

                    break;
                }
                case PROCESS_CREATE: {
                    //  } else if (info.substring(0, 1).equals("3")) {
                    // create process
                    int id = createNewProcess(message);

                    if (id < 0) {
                        respond(out, ResponseCode.FAILED);       // fail
                    } else {
                        TraceManager.addDev("Process accepted on port " + id);
                        respond(out, ResponseCode.SUCCESS, Integer.toString(id)); // process created
                    }

                    break;
                }
                case PROCESS_START: {
                    //} else if (info.substring(0, 1).equals("4")) {
                    // start already created process
                    if (startProcess(message)) {
                        TraceManager.addDev("Process started on port " + message);
                        respond(out, ResponseCode.SUCCESS, message); // process created
                    } else {
                        respond(out, ResponseCode.FAILED);       // fail
                    }

                    break;
                }
                case PROCESS_KILL_ALL: {
                    //} else if (info.substring(0, 1).equals("5")) {
                    // kill all processes
                    try {
                        killAllProcesses();

                        respond(out, ResponseCode.SUCCESS);
                    } catch (Exception e) {
                        TraceManager.addError(e);

                        respond(out, ResponseCode.FAILED);       // fail
                    }

                    break;
                }
                case PROCESS_KILL: {
                    //}else if (info.substring(0, 1).equals("6")) {
                    // kill process
                    try {
                        int id = Integer.decode(message);
                        TraceManager.addDev("Demand to kill: " + id);

                        if (killProcess(id)) {
                            respond(out, ResponseCode.SUCCESS);
                        } else {
                            respond(out, ResponseCode.FAILED);
                        }
                    } catch (Exception e) {
                        TraceManager.addError(e);

                        respond(out, ResponseCode.FAILED);       // fail
                    }

                    break;
                }
                case FILE_PUT: {
                    //} else if (info.substring(0, 1).equals("7")) {
                    //file : put
                    //String fileName = info.substring(1, info.length());
                    makeFileFromData(in, out, message);

                    break;
                }
                case FILE_GET: {
//            } else if (info.substring(0, 1).equals("8")) {
                    //file : get
                    //String fileName = info.substring(1, info.length());
                    sendDataFile(in, out, message);

                    break;
                }
                case FILE_DELETE: {
                    //} else if (info.substring(0, 1).equals("9")) {
                    //file : delete
                    //String fileName = info.substring(1, info.length());
                    deleteFile(in, out, message);

                    break;
                }
                default:
                    //} else {
                    // TODO: Is this really a good behavior?
                    System.exit(0);
            }
        }
    }

    private void makeFileFromData(BufferedReader in, PrintStream out, String fileName) {
        TraceManager.addDev("Making file " + fileName);
        File file = new File(fileName);

        if (!isFileOkForSave(file)) {
            TraceManager.addDev("Cannot make file");
            respond(out, ResponseCode.FAILED);  // fail

            return;
        }

        StringBuffer fileData = new StringBuffer();
        String info;

        TraceManager.addDev("Waiting for file data");

        while (true) {
            try {
                info = in.readLine();
            } catch (Exception e) {
                TraceManager.addError(e);

                return;
            }

            if (sk != null) {
                info = checkSecurity(info);
            }

            final RequestCode code = SocketComHelper.requestCode(info);
            final String message = SocketComHelper.message(code, info);

            if (code == null || code == RequestCode.FILE_SAVE) {//|| (info.length() == 0)) {
                // Assumes it is an EOF
                TraceManager.addDev("Wrong EOF -> assumes it is an EOF");

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write((new String(fileData)).getBytes());
                    fos.close();
                } catch (Exception e) {
                    TraceManager.addError("Error when feeding file", e);

                    respond(out, ResponseCode.FAILED);   // fail

                    return;
                }

                respond(out, ResponseCode.SUCCESS);      // file created

                return;

            } else if (code == RequestCode.FILE_APPEND) {//== info.substring(0, 1).equals("8")) {
                // fileData
                fileData.append(message + "\n");
            }
//            else if ( code == RequestCode.FILE_SAVE ) {
//                // EOF
//                TraceManager.addDev("EOF");
//                
//                try {
//                    FileOutputStream fos = new FileOutputStream(file);
//                    fos.write((new String(fileData)).getBytes());
//                    fos.close();
//                }
//                catch (Exception e) {
//                    TraceManager.addError("Error when feeding file", e );
//                    
//                    respond( out, ResponseCode.FAILED );   // fail
//                    
//                    return;
//                }
//                
//                respond( out, ResponseCode.SUCCESS );      // file created
//                
//                return;
//            }
            else {
                TraceManager.addDev("Unknown PDU (file)=" + info);
                respond(out, ResponseCode.FAILED);       // fail

                return;
            }
        }
    }

    private void sendDataFile(BufferedReader in, PrintStream out, String fileName) {
        TraceManager.addDev("Sending data of file " + fileName);
        File file = new File(fileName);

        if (!isFileOkForRead(file)) {
            respond(out, ResponseCode.FAILED);   // fail

            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);

            if (sendData(out, fis)) {
                respond(out, ResponseCode.SUCCESS);
            } else {
                TraceManager.addDev("Sending failed");
                respond(out, ResponseCode.FAILED);
            }
        } catch (Exception e) {
            TraceManager.addError(e);
            respond(out, ResponseCode.FAILED);   // fail

            return;
        }

        TraceManager.addDev("Sending completed");
    }

    private void deleteFile(BufferedReader in, PrintStream out, String fileName) {
        TraceManager.addDev("Deleting " + fileName);
        File file = new File(fileName);

        try {
            file.delete();

            respond(out, ResponseCode.SUCCESS);
        } catch (Exception e) {
            TraceManager.addError(e);
            respond(out, ResponseCode.FAILED);   // fail

            return;
        }
    }

    public void startServer() {
        Socket s = null;


        while (nbTry < MAX_NB_TRY) {
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
            TraceManager.addError("Exception file " + e.getMessage(), e);

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
                    TraceManager.addError("creation pb");
                    return false;
                }
            }
            if (!file.canWrite()) {
                TraceManager.addError("write pb");
                return false;
            }
        } catch (Exception e) {
            TraceManager.addError(e);

            return false;
        }

        return true;
    }

    private boolean sendData(PrintStream out, FileInputStream fis) throws LauncherException {
        TraceManager.addDev("Send data ");

        byte[] ba = new byte[BUFSIZE];
        int nbRead;

        //StringReader sr = new StringReader(data);
        //BufferedReader br = new BufferedReader(sr);
        try {
            // Sending first line : 8 + nbByte
            respond(out, ResponseCode.FILE_DATA, Integer.toString(fis.available()));
//            respond(out, new String("8" + fis.available()));
            int cpt = 0;

            while ((nbRead = fis.read(ba, 0, BUFSIZE)) > -1) {
                //respondNoln(out, new String(ba, 0, nbRead));
                out.write(ba, 0, nbRead);
                cpt += nbRead;
            }

            TraceManager.addDev("Nb written:" + cpt);
            fis.close();
        } catch (Exception e) {
            TraceManager.addError("Exception when sending file: " + e.getMessage(), e);

            return false;
        }

        return true;
    }

    private String checkSecurity(String _ciphered) {
        if (!isSecure) {
            return _ciphered; // The string is in fact not ciphered
        }

        if (sk == null) {
            return null;
        }

        String deciphered = AESEncryptor.decrypt(sk, iv, _ciphered);

        TraceManager.addDev("Deciphered=" + deciphered);

        return deciphered;
    }
}

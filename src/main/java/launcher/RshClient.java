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

import myutil.TraceManager;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class RshClient For remote execution of processes Creation: 2001
 *
 * @author Ludovic APVRILLE
 * @version 2 22/05/2015
 */
public class RshClient {

    public static String sk; // Secret key for communicating with the launcher

    private static String NO_HOST = "Application has no execution host";
    private static String INET = "Bad internet address for host ";
    private static String SERV_NOT_RESP = "Server not responding on ";
    private static String IO_ERROR = "Communication pb with server ";
    private static String PROC_FAILED = "Process could not be launched";
    private static String FILE_FAILED = "File creation failed";
    public static String FAILED = "Request failed";
    private static String ID_FAILED = "Wrong id";

    private static int BUFSIZE = 511;
    public static int PORT_NUMBER = 8375;

    private String host;
    private String cmd;
    private int port = PORT_NUMBER;
    private int portString = -1;
    private int portString2 = -1;
    private Socket clientSocket = null;
    private BufferedReader inReader;
    // private DataInputStream in2;
    private PrintStream out;
    // private int offset = 0;

    // private boolean go;

    public RshClient(String _cmd, String _host) {
        cmd = _cmd;
        host = _host;
    }

    public RshClient(String _host) {
        host = _host;
    }

    public void setPort(int _port) {
        port = _port;
    }

    public void setCmd(String _cmd) {
        cmd = _cmd;
    }

    public void stopCommand() throws LauncherException {
        sendKillProcessRequest();

        // Issue #18: Socket is already closed by kiss process method
        // go = false;
        // closeConnect();
    }

    public int getId() throws LauncherException {
        connect();
        send(RequestCode.GET_SESSION_ID);
        int id = readId();
        closeConnect();

        return id;
    }

    public int freeId(int id) throws LauncherException {
        connect();
        send(RequestCode.FREE_SESSION_ID, Integer.toString(id));
        int idret = readId();

        if (idret != id) {
            throw new LauncherException(ID_FAILED);
        }

        return idret;

    }

    public void sendExecuteCommandRequest() throws LauncherException {
        sendExecuteCommandRequest(false);
    }

    public void sendExecuteCommandRequest(final boolean checkReturnCode) throws LauncherException {
        connect();

        if (checkReturnCode) {
            send(RequestCode.PROCESS_CREATE, cmd);
            readPortString();
            closeConnect();

            final String procIdStr = Integer.toString(portString);
            connect();
            send(RequestCode.PROCESS_CHECK_RETURN_CODE, procIdStr);
            readServerResponse();
            closeConnect();

            connect();
            send(RequestCode.PROCESS_START, procIdStr);
            readServerResponse();
        } else {
            send(RequestCode.PROCESS_CREATE_START, cmd);
            readPortString();
        }

        closeConnect();
    }

    public void sendExecutePipedCommandsRequest(String cmd1, String cmd2) throws LauncherException {
        connect();
        send(RequestCode.PROCESS_CREATE, cmd1);
        readPortString();
        final int id1 = portString;
        closeConnect();

        connect();
        send(RequestCode.PROCESS_CREATE, cmd2);
        readPortString();
        final int id2 = portString;
        closeConnect();

        connect();
        send(RequestCode.PROCESS_PIPE, id1 + " " + id2);
        readServerResponse();
        closeConnect();

        connect();
        send(RequestCode.PROCESS_START, Integer.toString(id1));
        readServerResponse();
        closeConnect();

        connect();
        send(RequestCode.PROCESS_START, Integer.toString(id2));
        readServerResponse();
        closeConnect();

        portString = id2;
        portString2 = id1;
    }

    public Integer getProcessReturnCode() throws LauncherException {
        connect();
        send(RequestCode.PROCESS_GET_RETURN_CODE, Integer.toString(portString));

        try {
            final String s = inReader.readLine();
            final ResponseCode code = SocketComHelper.responseCode(s);

            if (code != ResponseCode.SUCCESS) {
                throw new LauncherException(FAILED);
            }

            final String retCodeStr = SocketComHelper.message(code, s);

            return Integer.decode(retCodeStr);
        } catch (IOException io) {
            throw new LauncherException(IO_ERROR, io);
        } catch (final NumberFormatException ex) {
            return null;
        } finally {
            closeConnect();

            sendKillProcessRequest();
        }
    }

    public void sendFileData(String fileName, String data) throws LauncherException {
        connect();
        send(RequestCode.FILE_PUT, fileName);
        sendFileData(data);
        send(RequestCode.FILE_SAVE, fileName);
        readServerResponse();
        closeConnect();
    }

    public String getFileData(String fileName) throws LauncherException {
        connect();
        send(RequestCode.FILE_GET, fileName);
        String s = readDataUntilCompletion();
        closeConnect();

        return s;
    }

    public void deleteFile(String fileName) throws LauncherException {
        connect();
        send(RequestCode.FILE_DELETE, fileName);
        readServerResponse();
        closeConnect();
    }

    public void sendKillProcessRequest() throws LauncherException {
        connect();
        send(RequestCode.PROCESS_KILL, Integer.toString(portString));

        try {
            readServerResponse();
        } finally {
            closeConnect();

            if (portString2 != -1) {
                connect();
                send(RequestCode.PROCESS_KILL, Integer.toString(portString2));

                try {
                    readServerResponse();
                } finally {
                    closeConnect();
                }
            }
        }
    }

    public void sendKillAllProcessRequest() throws LauncherException {
        connect();
        send(RequestCode.PROCESS_KILL_ALL);
        readServerResponse();
        closeConnect();
    }

    private BufferedReader createBufferedReader(final Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String getDataFromProcess() throws LauncherException {
        // go = true;
        StringBuffer bf = new StringBuffer();

        final Socket socket = connect(portString);

        try {
            final BufferedReader reader = createBufferedReader(socket);

            String data;

            while ((data = readProcessData(reader)) != null /* && go */) {
                bf.append(data + "\n");
            }

            return new String(bf);
        } catch (final IOException ex) {
            throw new LauncherException(IO_ERROR, ex);
        } finally {
            closeConnect(socket);
        }
    }

    public RshClientReader getDataReaderFromProcess() throws LauncherException {
        Socket socket = connect(portString);
        try {
            return new RshClientReader(socket);
        } catch (IOException e) {
            throw new LauncherException(IO_ERROR, e);
        }
    }

    public void writeCommandMessages(final Writer output) throws LauncherException {

        // Issue #18: the interruption is sent on the server side and we just continue
        // writing until there is no more messages
        // to empty the stream.
        // go = true;

        // Issue #18: There was a problem when killing the remote process since the old
        // code would override the socked created
        // by the killing command
        final Socket socket = connect(portString);
        // connect(portString);

        try {
            final BufferedReader reader = createBufferedReader(socket);
            String readLine = reader.readLine();

            while (readLine != null) {
                final ResponseCode code = SocketComHelper.responseCode(readLine);
                final String message = SocketComHelper.message(code, readLine);

                switch (code) {
                    case PROCESS_OUTPUT_ERROR:
                        output.append(message + "\n");

                        break;

                    default:
                        output.append(message + "\n");

                        break;
                }

                readLine = reader.readLine();
            }
        } catch (final IOException io) {
            throw new LauncherException(IO_ERROR, io);
        } finally {
            closeConnect(socket);
        }
    }

    public void closeConnect() throws LauncherException {
        closeConnect(clientSocket);
    }

    private void closeConnect(final Socket socket) throws LauncherException {
        try {
            socket.close();
        } catch (IOException io) {
            throw new LauncherException(SERV_NOT_RESP + host, io);
        }
    }

    private void send(final RequestCode code) throws LauncherException {
        send(code, "");
    }

    private void send(final RequestCode code, String message) throws LauncherException {
        // TraceManager.addDev( "Sending message: " + message );
        // message = code.name() + message;
        //
        // if (sk != null) {
        // // cipher the information
        // message = AESEncryptor.encrypt(sk, RshServer.iv, message);
        // TraceManager.addDev( "Ciphered message to server=" + message );
        // }

        try {
            SocketComHelper.send(out, code, message, sk);
            // out.println( message);
            // out.flush();
        } catch (Throwable th) {
            throw new LauncherException(IO_ERROR, th);
        }
    }

    private void sendFileData(String data) throws LauncherException {
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String s;

        try {
            while ((s = br.readLine()) != null) {
                send(RequestCode.FILE_APPEND, s);
                // send("8" + s);
            }
        } catch (Exception e) {
            throw new LauncherException(FILE_FAILED, e);
        }
    }

    private static String readProcessData(BufferedReader reader) throws LauncherException {
        // int nb;
        // String s = null;

        try {
            final String line = reader.readLine();

            final ResponseCode code = SocketComHelper.responseCode(line);
            // nb = Integer.decode(s.substring(0,1)).intValue();

            if (code == ResponseCode.PROCESS_END) {
                // if (nb == 5) {
                return null;
            }

            String message = SocketComHelper.message(code, line); // s = s.substring(1, s.length());

            // if ( message == null ) {
            // message = "";
            // }

            return message;
        } catch (final IOException io) {
            throw new LauncherException(IO_ERROR, io);
        }
    }

    private String readDataUntilCompletion() throws LauncherException {
        try {
            // int nbTotal, cpt = 0;
            StringBuffer ret = new StringBuffer();

            String s = inReader.readLine();

            final ResponseCode code = SocketComHelper.responseCode(s);
            // nb = Integer.decode(s.substring(0,1)).intValue();

            if (ResponseCode.FILE_DATA == code) {
                // if (nb == 8) {
                char[] c = new char[BUFSIZE + 1];
                int read;
                int cpt = 0;
                int nbTotal = Integer.decode(SocketComHelper.message(code, s));// Integer.decode(s.substring(1,s.length())).intValue();
                while (((cpt < nbTotal) && (read = inReader.read(c, 0, Math.min(BUFSIZE, nbTotal - cpt))) > -1)) {
                    ret.append(c, 0, read);
                    cpt += read;
                }

                // Read last info and check for success
                readServerResponse();
                //
                // if (nb != 3) {
                // throw new LauncherException(FILE_FAILED);
                // }
            }

            return new String(ret);
        } catch (IOException io) {
            throw new LauncherException(IO_ERROR, io);
        }
    }

    private void readServerResponse() throws LauncherException {
        // int nb;
        // String s = null;

        try {
            final String s = inReader.readLine();
            TraceManager.addDev("Got from Server:" + s);

            final ResponseCode code = SocketComHelper.responseCode(s);
            // nb = Integer.decode(s.substring(0,1)).intValue();

            if (code != ResponseCode.SUCCESS) {
                throw new LauncherException(FAILED);
            }
        } catch (IOException io) {
            throw new LauncherException(IO_ERROR, io);
        }

    }

    private int readId() throws LauncherException {
        // int nb;
        // String s = null;

        try {
            final String s = inReader.readLine();
            final int nb = Integer.decode(s.substring(0, 1));

            if (nb == 0) {
                throw new LauncherException(ID_FAILED);
            }

            return nb;
        } catch (IOException io) {
            throw new LauncherException(IO_ERROR, io);
        }
        //
        // return nb;
    }
    //
    // private void readReturnPipedProcesses() throws LauncherException {
    //// int nb;
    //// String s = null;
    // try {
    // final String s = inReader.readLine();
    // final ResponseCode code = SocketComHelper.responseCode( s );
    // //nb = Integer.decode(s.substring(0,1)).intValue();
    //
    // if ( code != ResponseCode.SUCCESS ) {
    // //if (nb != 3) {
    // throw new LauncherException( PROC_FAILED );
    // }
    // }
    // catch(IOException io) {
    // throw new LauncherException( IO_ERROR, io );
    // }
    // }

    private void readPortString() throws LauncherException {
        // int nb;
        // String s = null;

        try {
            final String s = inReader.readLine();
            final ResponseCode code = SocketComHelper.responseCode(s);
            // nb = Integer.decode(s.substring(0,1)).intValue();

            if (code == ResponseCode.FAILED) {
                // if (nb == 2) {
                throw new LauncherException(PROC_FAILED);
            }

            // portString = -1;
            portString2 = -1;

            portString = Integer.decode(SocketComHelper.message(code, s));//// Integer.decode(s.substring(1,
                                                                          //// s.length())).intValue();

            if (portString < 1) {
                throw new LauncherException(PROC_FAILED);
            }
        } catch (IOException io) {
            throw new LauncherException(IO_ERROR, io);
        }
    }

    private void connect() throws LauncherException {
        clientSocket = connect(port);

        try {
            inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            throw new LauncherException(SERV_NOT_RESP + host, ex);
        }
    }

    private Socket connect(final int portNet) throws LauncherException {
        if (host == null) {
            throw new LauncherException(NO_HOST);
        }

        try {
            final InetAddress ina = InetAddress.getByName(host);

            try {
                return new Socket(ina, portNet);
            } catch (IOException ex) {
                throw new LauncherException(SERV_NOT_RESP + host, ex);
            }
        } catch (UnknownHostException e) {
            throw new LauncherException(INET + host, e);
        }
    }
}

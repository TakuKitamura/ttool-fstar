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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class ExecutionThread For remote execution of processes Creation: 2001
 * 
 * @version 1.1 01/12/2003
 * @author Ludovic APVRILLE
 */
class ExecutionThread extends Thread {
    private static final String ERROR_KEY = "error=";
    private static final Pattern ERROR_PATTERN = Pattern.compile(".*(" + ERROR_KEY + "\\d+,).*");

    private final String cmd;
    private final int port;
    private final RshServer rsh;
    private ServerSocket server;// = null;
    private boolean go;
    private BufferedReader proc_in;
    private BufferedReader proc_err;
    private Process proc;

    // private boolean piped;
    private ExecutionThread parentExecThread;

    private boolean mustWaitForPiped;
    private OutputStream pipe;

    private boolean isStarted = false;

    private boolean sendReturnCode;

    private Integer returnCode;

    public ExecutionThread(final String _cmd, final int startPortnumber, final RshServer _rsh) {
        cmd = _cmd;
        // port = _port;
        rsh = _rsh;
        server = null;
        go = true;
        sendReturnCode = false;
        returnCode = null;
        mustWaitForPiped = false;
        parentExecThread = null;
        pipe = null;
        proc = null;
        proc_in = null;
        proc_err = null;

        port = findPortNumber(startPortnumber);
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public boolean isSendReturnCode() {
        return sendReturnCode;
    }

    public void setSendReturnCode(boolean sendReturnCode) {
        this.sendReturnCode = sendReturnCode;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setPiped(ExecutionThread _et) {
        parentExecThread = _et;
        // piped = true;
    }

    public void setWaitForPipe() {
        mustWaitForPiped = true;
    }

    public synchronized void waitingForPipe() {
        while (pipe == null) {
            try {
                TraceManager.addDev("Waiting for pipe");
                wait();
            } catch (InterruptedException ie) {

            }
        }
    }

    public synchronized void setMyPipe(final OutputStream os) {
        pipe = os;
        notifyAll();
    }

    public int getPort() {
        return port;
    }

    private int findPortNumber(final int startPortnumber) {
        for (int i = startPortnumber + 1; i < startPortnumber + 1000; i++) {
            try {
                server = new ServerSocket(i);
                server.setSoTimeout(60000);

                return i;
                // return;
            } catch (Exception e) {
            }
        }

        return startPortnumber;
    }

    private Socket waitForClient() {
        TraceManager.addDev("process # " + port + " is waiting for client...");

        try {
            final Socket socket = server.accept();

            TraceManager.addDev("Process # " + port + " got client.");

            return socket;
        } catch (Exception e) {
            TraceManager.addError(e);

            return null;
        }
    }

    public void closeConnect(Socket s) {
        try {
            s.close();
        } catch (IOException io) {
            TraceManager.addError(io);
        }
    }

    public void stopProcess() {
        go = false;

        // Issue #18: It may happen that the process is requested to be stopped before
        // it had time to start
        // in which case it will be null
        if (proc != null /* && proc.isAlive() */ ) {
            proc.destroy();
        }

        if (proc_in != null) {
            try {
                proc_in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            proc_in = null;
        }

        if (proc_err != null) {
            try {
                proc_err.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            proc_err = null;
        }
    }

    private void respond(final PrintStream out, final ResponseCode code, final String message) {
        SocketComHelper.send(out, code, message);
        // try {
        // out.println( co de.name() + message );
        // out.flush();
        // } catch ( IOException e) {
        // }
    }

    /**
     * Issue #35: Handle the case where the executable to be run does not exists or
     * is not accessible
     * 
     * @param ex
     * @param out
     * @throws InterruptedException
     */
    private void handleReturnCode(final IOException ex, final PrintStream out) throws InterruptedException {
        if (ex.getMessage() == null) {
            returnCode = -1;
        } else {
            returnCode = parseErrorCode(ex.getMessage());
        }

        final String message = "Error executing command " + cmd + " with return code " + returnCode + ".";
        TraceManager.addError(message);
        respond(out, ResponseCode.PROCESS_OUTPUT, message);
        respond(out, ResponseCode.PROCESS_OUTPUT, ex.getMessage());
        respond(out, ResponseCode.PROCESS_END, null);// "5");
    }

    private Integer parseErrorCode(final String message) {
        final Matcher matcher = ERROR_PATTERN.matcher(message);

        if (matcher.matches()) {
            if (matcher.groupCount() > 1) {
                final String expr = matcher.group(1);
                final String errorNum = expr.substring(ERROR_KEY.length(), expr.length() - 1);

                return Integer.decode(errorNum);
            }
        }

        return -1;
    }

    private void handleReturnCode(PrintStream out) throws InterruptedException {
        if (sendReturnCode) {
            returnCode = proc.waitFor();
            final String message = "Ended command " + cmd + " with return code " + returnCode + ".";
            TraceManager.addDev(message);
            respond(out, ResponseCode.PROCESS_OUTPUT, message);
            respond(out, ResponseCode.PROCESS_END, null);// "5");
        } else {
            returnCode = null;
            respond(out, ResponseCode.PROCESS_END, null);// "5");
        }
    }

    @Override
    public void run() {
        isStarted = true;
        TraceManager.addDev("Starting process for command " + cmd);
        proc = null;
        // BufferedReader in = null;
        // String str;

        try {
            // print output in pipe
            if (mustWaitForPiped) {
                // try {
                proc = Runtime.getRuntime().exec(cmd);

                if (parentExecThread != null) {
                    TraceManager.addDev("Giving my pipe to the other...");
                    parentExecThread.setMyPipe(proc.getOutputStream());
                }

                TraceManager.addDev("Waiting for pipe...");

                waitingForPipe();

                TraceManager.addDev("Got pipe.");

                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String str;

                try {
                    while (go && (str = proc_in.readLine()) != null) {
                        TraceManager.addDev("Writing " + str + " to pipe...");
                        pipe.write((str + "\n").getBytes());
                    }

                    while (go && (str = proc_err.readLine()) != null) {
                        TraceManager.addError("Writing " + str + " to pipe...");
                        pipe.write((str + "\n").getBytes());
                    }
                    // }
                    // catch (IOException e) {
                    // TraceManager.addError( e );
                    // }
                    // }
                    // catch (Exception e) {
                    // TraceManager.addError("Exception [" + e.getMessage() + "] occured when
                    // executing " + cmd, e );
                    // }

                    // try {
                } catch (IOException e) {
                    TraceManager.addError("Exception [" + e.getMessage() + "] occured when executing " + cmd + "!", e);
                } finally {
                    pipe.flush();
                    pipe.close();
                }

                TraceManager.addDev("Ending piped command " + cmd + "...");
            } else {
                // print output on socket
                Socket s = waitForClient();

                if (s == null) {
                    TraceManager.addDev("Client did not connect on time!");
                    rsh.removeProcess(this);

                    return;
                }

                // TraceManager.addDev("Going to start command " + cmd + "..." );
                final PrintStream out = new PrintStream(s.getOutputStream(), true);

                try {
                    proc = Runtime.getRuntime().exec(cmd);

                    if (parentExecThread != null) {
                        // TraceManager.addDev( "Giving my pipe to the other..." );
                        parentExecThread.setMyPipe(proc.getOutputStream());
                    }

                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    String str;

                    // TraceManager.addDev("Reading the output stream of the process " + cmd);
                    while (go && (str = proc_in.readLine()) != null) {
                        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                        respond(out, ResponseCode.PROCESS_OUTPUT, str);
                    }

                    proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                    while (go && (str = proc_err.readLine()) != null) {
                        TraceManager.addError(str);
                        respond(out, ResponseCode.PROCESS_OUTPUT_ERROR, str);
                    }

                    handleReturnCode(out);
                } catch (final IOException ex) {
                    handleReturnCode(ex, out);
                } finally {
                    if (s != null) {
                        closeConnect(s);
                    }
                }
            }
        } catch (Throwable ex) {
            TraceManager.addError("Exception occured when executing " + cmd, ex);
        } finally {
            if (proc != null) {
                proc.destroy();
            }

            if (!sendReturnCode) {
                rsh.removeProcess(this);
            }
        }
    }
}

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

import java.io.Reader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * Class RshClientReader
 * Creation: 03/06/2017
 * @version 1 03/06/2017
 * @author Florian LUGOU
 */
public class RshClientReader extends Reader implements Runnable {

    private PipedOutputStream pos;
    private InputStreamReader pis;
    private Socket clientSocket;

    private Thread forwardingThread;
    private boolean go = true;
    private StringBuilder builder;
    private boolean isNewLine = true;
    
    public RshClientReader(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.pos = new PipedOutputStream();
        this.pis = new InputStreamReader(new PipedInputStream(pos));
        this.forwardingThread = new Thread(this);
        try {
            this.forwardingThread.start();
        } catch (IllegalThreadStateException e) {}
    }

    private void consumeOldLine(String s) throws IOException
    {
        int n = s.indexOf('\n');
        if (n >= 0) {
            this.pos.write(s.substring(0, n+1).getBytes());
            this.pos.flush();
            isNewLine = true;
            this.consumeNewLine(s.substring(n+1));
        } else {
            this.pos.write(s.getBytes());
            this.pos.flush();
        }
    }

    private void consumeNewLine(String s) throws IOException
    {
        ResponseCode code = SocketComHelper.responseCode(s);

        if (code == ResponseCode.PROCESS_END) {
            this.go = false;
        }

        else if (code == null) {
            this.builder.append(s);
        }

        else {
            s = SocketComHelper.message(code, s);

            this.isNewLine = false;
            this.consumeOldLine(s);
        }
    }

    @Override
    public void run()
    {
        this.builder = new StringBuilder();
        try {
            this.clientSocket.setSoTimeout(100);
            InputStreamReader socketReader = new InputStreamReader(this.clientSocket.getInputStream());
            try {
                while(this.go)
                {
                    char[] cbuf = new char[50];
                    int n = 0;
                    try {
                        n = socketReader.read(cbuf);
                        if (n < 0)
                            break;
                    } catch(SocketTimeoutException e) {
                        if (n > 0)
                            this.builder.append(cbuf, 0, n);
                        continue;
                    }

                    this.builder.append(cbuf, 0, n);
                    String s = this.builder.toString();
                    this.builder = new StringBuilder();
                    if (isNewLine)
                    {
                        this.consumeNewLine(s);
                    }
                    else
                    {
                        this.consumeOldLine(s);
                    }
                }
            } catch(IOException e) {
            } finally {
                try {
                    socketReader.close();
                } catch(IOException e) {}
                try {
                    this.pos.close();
                } catch(IOException e) {}
            }
        } catch(IOException e) {
        } finally {
            try {
                this.clientSocket.close();
            } catch(IOException e) {}
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        int n = this.pis.read(cbuf, off, len);
        return n;
    }

    @Override
    public void close() throws IOException
    {
        this.go = false;

        try {
            this.pis.close();
        } catch(IOException e) {}
    }
}

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

package web.crawler;

import myutil.externalSearch.Message;

import javax.net.ssl.SSLSocket;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ThreadSocket Socket in a thread ;-) Creation: 2015
 * 
 * @version 2.0 25/03/2016
 * @author Dan Huynh VO, Ludovic APVRILLE
 */
public class ThreadSocket extends Thread {

    SSLSocket socket = null;
    DatabaseQuery database = null;

    /**
     * Constructor
     * 
     * @param socket   : SSL socket
     * @param database : DatabaseQuery
     */
    public ThreadSocket(SSLSocket socket, web.crawler.DatabaseQuery database) {

        this.socket = socket;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
            // Receive from clients
            ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
            // Send to clients

            // Create a new message to prepare getting the message from client
            Message requestMsg = new Message();

            try {
                requestMsg = (Message) fromClient.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Print the result
            // System.out.println(requestMsg.getCmd());
            // System.out.println(requestMsg.getOptions());
            // System.out.println(requestMsg.getValues());

            // Read the message and then modify the content

            Message answerMsg = new Message();
            answerMsg = MultiThreadServer.analyseRequestMessage(requestMsg, database);

            // Send it back to the client
            toClient.writeObject(answerMsg);

            toClient.close();
            fromClient.close();
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ThreadSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

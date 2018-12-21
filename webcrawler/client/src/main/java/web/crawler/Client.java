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

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
   * Class WebCrawler
   * Implement of a webcrawler for CVEs
   * Creation: 2015
   * @version 2.0 25/03/2016
   * @author  Marie FORRAT &Angeliki AKTYPI & Ludovic APVRILLE & Dan Huynh VO
 */
public class Client {

    /**
     *
     * @param cmd     strings in Message class.
     * @param options list of options corresponds to list of values.
     * @param values   list of values
     * @return Message.
     */
    public static Message createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values) {
        Message requestMsg = new Message(cmd, options, values);
        System.out.println("\n" + Message.SUC_CREATE_REQ_MESSAGE);
        return requestMsg;
    }

    /**
     * @param answerMsg the answer message from the server
     */
    public static void analyseAnswerMessage(Message answerMsg) {
        //Analyse the message from the server,
        //Depends on the cmd, we can determine the values
        String cmd = answerMsg.getCmd();
        // System.out.println(cmd);

        if (cmd == null) {
            System.out.println(Message.ERR_CMD);
        }

        else if (cmd.equals(Message.RESULT_SEARCH)) {
            //show GUI for search
            //Call Huy's function
            ArrayList<Object> res = new ArrayList<>();
            res = answerMsg.getContent();
            System.out.println("\n"+res);

        }

        else if (cmd.equals(Message.RESULT_DETAIL)) {
            //show GUI for detail of a specific record
            //Call Huy's function
            ArrayList<Object> res = new ArrayList<>();
            res = answerMsg.getContent();
            System.out.println("\n"+res);

        }

        else if (cmd.equals(Message.RESULT_STATISTIC)) {

            //Show picture-Use a function to convert binary to image
            //Message.convertByteToImage(answerMsg.getImageByte());

            ArrayList<Object> resultContent = new ArrayList<>();
            resultContent = answerMsg.getContent();
            byte[] imgByte = (byte[]) resultContent.get(0);
            Message.convertByteToImage(imgByte,answerMsg);

            //Call Huy's function to load Image
        }

        else if (cmd.equals(Message.RESULT_HISTOGRAM)) {

            //Show picture-Use a function to convert binary to image
            //Message.convertByteToImage(answerMsg.getImageByte());

            ArrayList<Object> resultContent = new ArrayList<>();
            resultContent = answerMsg.getContent();
            byte[] imgByte = (byte[]) resultContent.get(0);
            Message.convertByteToImage(imgByte,answerMsg);

            //Call Huy's function to load Image
        }

        else {
            System.out.print(Message.ERR_CMD2);
        }

    }

    public static void main(String[] args) {
        SSLSocket client = null;

        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            client = (SSLSocket)sslSocketFactory.createSocket("LocalHost",12345);

            client.setEnabledCipherSuites(client.getSupportedCipherSuites());

            System.out.println("Client has been created successfully!");


            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());

            //Create a temporary data to tests
            String cmd;
            ArrayList<String> options = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            //  open up standard input
            BufferedReader br;

            /* Buffer */
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("\n");
            System.out.print("Insert your command (search, details, stat, histo): ");

            /* Read from console */
            cmd = br.readLine();
            System.out.print("\n");

            while (true) {

                if (cmd.equals(Message.CMD_SEARCH)) {
                    options.add(Message.OPTION_KEY_WORDS);
                    options.add(Message.OPTION_DATE);
                    options.add(Message.OPTION_SCORE);
                    options.add(Message.OPTION_SYSTEM);
                    options.add(Message.OPTION_NUMBER);

                    System.out.println("Insert the keyword, the year, the score "
                                       +"the system and the number of results that you wish.");
                    System.out.println("Example:buffer-injection this-year linux 4-5 10\n");
                    String arguments = br.readLine();
                    String[] argument = arguments.split(" ");

                    for (int i = 0; i < 5; i++) {
                        //System.out.println(argument[i]);
                        values.add(argument[i]);
                    }
                    break;
                }

                else if (cmd.equals(Message.CMD_DETAIL)) {

                    options.add(Message.OPTION_KEY_WORDS);
                    System.out.println("Insert the cve-id that you wish.");
                    System.out.println("Example:CVE-2015-0001\n");
                    String argument = br.readLine();
                    values.add(argument);
                    break;
                }

                //Creat a statistic image request
                else if (cmd.equals(Message.CMD_STATISTIC)) {

                    options.add(Message.OPTION_KEY_WORDS);

                    System.out.println("Insert the systems that you wish "
                                       +"to be statistically examined.");
                    System.out.println("Example:linux apache chrome windows sql\n");
                    String arguments = br.readLine();
                    values.add(arguments);
                    //String[] argument = arguments.split(" ");

                    /*  for (int i = 0; i < argument.length; i++) {
                    //System.out.println(argument[i]);
                    values.add(argument[i]);
                    }*/
                    break;
                }

                else if (cmd.equals(Message.CMD_HISTOGRAM)) {

                    options.add(Message.OPTION_KEY_WORDS);

                    System.out.println("Insert one system that you wish "
                                       +"to be statistically examined.");
                    System.out.println("Example:linux\n");
                    String arguments = br.readLine();
                    /*  String[] argument = arguments.split(" ");

                        for (int i = 0; i < argument.length; i++) {
                        //System.out.println(argument[i]);
                        values.add(argument[i]);
                        }*/
                    values.add(arguments);
                    break;
                }

                else {
                    System.out.print(Message.ERR_CMD2);
                    break;
                }

            }

            //Create a msg with constructors
            Message msg = createRequestMessage(cmd, options, values);

            //Push the message to server
            outputStream.writeObject(msg);

            //Get back the message from server
            Message answerMsg = new Message();
            try {
                answerMsg = (Message) inputStream.readObject();
                analyseAnswerMessage(answerMsg);
            }

            catch (ClassNotFoundException ex) {
                Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            outputStream.close();
            inputStream.close();
            client.close();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

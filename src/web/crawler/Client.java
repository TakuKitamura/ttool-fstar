package web.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Marie FORRAT & Angeliki AKTYPI & Dan Huynh VO
 */
public class Client {

    /**
     *
     * @param cmd
     * @param options
     * @param values
     * @return
     */
    public static Message createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values) {
        Message requestMsg = new Message(cmd, options, values);
        System.out.println("\n" + Message.SUC_CREATE_REQ_MESSAGE);
        return requestMsg;
    }
    
    /**
     *
     * @param answerMsg
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
            ArrayList<Object> res = new ArrayList();
            res = answerMsg.getContent();
            System.out.println("\n"+res);
                        
        } 
        
        else if (cmd.equals(Message.RESULT_DETAIL)) {
            //show GUI for detail of a specific record
            //Call Huy's function
            ArrayList<Object> res = new ArrayList();
            res = answerMsg.getContent();
            System.out.println("\n"+res);

        } 
        
        else if (cmd.equals(Message.RESULT_STATISTIC)) {
            
            //Show picture-Use a function to convert binary to image
            //Message.convertByteToImage(answerMsg.getImageByte());    
            
            ArrayList<Object> resultContent = new ArrayList();
            resultContent = answerMsg.getContent();
            byte[] imgByte = (byte[]) resultContent.get(0);
            Message.convertByteToImage(imgByte,answerMsg);

            //Call Huy's function to load Image
        } 
        
        else if (cmd.equals(Message.RESULT_HISTOGRAM)) {
            
            //Show picture-Use a function to convert binary to image
            //Message.convertByteToImage(answerMsg.getImageByte());    
            
            ArrayList<Object> resultContent = new ArrayList();
            resultContent = answerMsg.getContent();
            byte[] imgByte = (byte[]) resultContent.get(0);
            Message.convertByteToImage(imgByte,answerMsg);

            //Call Huy's function to load Image
        } 
        
        else {
            System.out.print(Message.ERR_CMD2);
        }

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Socket client = new Socket("LocalHost", 1234);
            System.out.println("Client has been created successfully!");

            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());

            //Create a temporary data to tests
            String cmd;
            ArrayList<String> options = new ArrayList();
            ArrayList<String> values = new ArrayList();
            
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

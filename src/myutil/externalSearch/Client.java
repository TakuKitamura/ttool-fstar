package myutil.externalSearch;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;

import org.jsoup.nodes.Document;

public class Client {
    public static String dbaddr="localhost";
    public static int dpport=9999;
    public  Object parserAnswerMessage(Message answerMsg) {
        //Analyse the message from the server,
        //Depends on the cmd, we can determine the values
        String cmd = answerMsg.getCmd();
        // System.out.println(cmd);
        if (cmd == null) {
            System.out.println(Message.ERR_CMD);
            return null;
        } else if (cmd.equals(Message.RESULT_SEARCH)) {
            ArrayList<Record> lrecord = new ArrayList<>();
            try {
                //byte[] encoded = Files.readAllBytes(Paths.get("/home/trhuy/Downloads/02-51-34.xml"));


                byte[] encoded = (byte[])answerMsg.getContent().get(0);

                String resultxml = new String(encoded, "UTF-8");

                Document doc = Jsoup.parse(resultxml);
                for (Element e : doc.select("Row")) {
                    Record r = new Record();
                    Document eachRow = Jsoup.parse(e.toString());
                    Element e_id = eachRow.select("cve_id").first();
                    Element e_title = eachRow.select("name").first();
                    Element e_des = eachRow.select("summary").first();
                    r.setCve_id(e_id.text());
                    r.setName(e_title.text());
                    r.setSummary(e_des.text());
                    lrecord.add(r);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(lrecord.toArray());
            return (Object)lrecord;
        } else if (cmd.equals(Message.RESULT_DETAIL)) {
            Record r = new Record();
            try {
                //byte[] encoded = Files.readAllBytes(Paths.get("/home/trhuy/Downloads/02-40-06.xml"));
                byte[] encoded = (byte[])answerMsg.getContent().get(0);
                String resultxml = new String(encoded, "UTF-8");

                Document doc = Jsoup.parse(resultxml);
                for (Element e : doc.select("Row")) {

                    Document eachRow = Jsoup.parse(e.toString());
                    Element cve_id = eachRow.select("cve_id").first();
                    Element pub_date = eachRow.select("pub_date").first();
                    Element mod_date = eachRow.select("mod_date").first();
                    Element score = eachRow.select("score").first();
                    Element access_vector = eachRow.select("access_vector").first();
                    Element access_complexity = eachRow.select("access_complexity").first();
                    Element authentication = eachRow.select("authentication").first();
                    Element confidentiality_impact = eachRow.select("confidentiality_impact").first();
                    Element integrity_impact = eachRow.select("integrity_impact").first();
                    Element availability_impact = eachRow.select("availability_impact").first();
                    Element gen_date = eachRow.select("gen_date").first();
                    Element cwe_id = eachRow.select("cwe_id").first();
                    Element summary = eachRow.select("summary").first();
                    Element ref_type = eachRow.select("ref_type").first();
                    Element source = eachRow.select("source").first();
                    Element link = eachRow.select("link").first();
                    Element name = eachRow.select("name").first();

                    r.setCve_id(cve_id.text());
                    r.setPub_date(pub_date.text());
                    r.setMod_date(mod_date.text());
                    r.setScore(score.text());
                    r.setAccess_vector(access_vector.text());
                    r.setAccess_complexity(access_complexity.text());
                    r.setAuthentication(authentication.text());
                    r.setConfidentiality_impact(confidentiality_impact.text());
                    r.setIntegrity_impact(integrity_impact.text());
                    r.setAvailability_impact(availability_impact.text());
                    r.setGen_date(gen_date.text());
                    r.setCwe_id(cwe_id.text());
                    r.setSummary(summary.text());
                    r.setRef_type(ref_type.text());
                    r.setSource(source.text());
                    r.setLink(link.text());


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return (Object)r;

        } else if (cmd.equals(Message.RESULT_STATISTIC)) {
            //show picture
            //Use a function to convert binary to image
            byte[] encoded = (byte[]) answerMsg.getContent().get(0);

            return (Object)encoded;

            //Call Huy's function to load Image
        } else System.out.print(Message.ERR_CMD2);
        return null;
    }



    public  Message createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values)
    {
        Message requestMsg = new Message(cmd,options,values);
        System.out.println(Message.SUC_CREATE_REQ_MESSAGE);
        return requestMsg;
    }

    public Message send(Message msg){
        SSLSocket sslClient = null;
        try {
           // Socket client = new Socket(dbaddr,dpport);
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            sslClient = (SSLSocket)sslSocketFactory.createSocket("LocalHost",12345);

            sslClient.setEnabledCipherSuites(sslClient.getSupportedCipherSuites());
            System.out.println("Client has been created successfully!");

            ObjectOutputStream outputStream = new ObjectOutputStream(sslClient.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(sslClient.getInputStream());

            outputStream.writeObject(msg);

            //Get back the message from server
            Message answerMsg = new Message();
            try {
                answerMsg = (Message) inputStream.readObject();
            } catch (ClassNotFoundException ex) {
               // Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }


            outputStream.close();
            inputStream.close();
            sslClient.close();
            return answerMsg;

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


    }

    public void main(String[] args){
        try {
            Socket client = new Socket("LocalHost",1234);
            System.out.println("Client has been created successfully!");

            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());

            //Create a temporary data to tests
            String cmd ="stat";
            ArrayList<String> options = new ArrayList();
            ArrayList<String> values = new ArrayList();

            options.add("keyword");
            options.add("year");
            options.add("system");

            values.add("Stuxnet");
            values.add("2014");
            values.add("windows");

            //Create a msg with constructors
            Message msg = createRequestMessage(cmd, options, values);

            //Push the message to server
            outputStream.writeObject(msg);

            //Get back the message from server
            Message answerMsg = new Message();
            try {
                answerMsg = (Message) inputStream.readObject();
            } catch (ClassNotFoundException ex) {
               // Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Print out the content from the server
            // System.out.println(answerMsg.getContent());

            parserAnswerMessage(answerMsg);
            // Message.convertByteToImage(answerMsg.getImageByte());

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

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


package myutil.externalSearch;

import myutil.TraceManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * JDialogSearchBox
 * dialog for external search with key words
 * Creation: 11/03/2015
 *
 * @author Dan VO & Huy TRUONG
 * @version 1.0 11/03/2015
 */
public class Client {
    public byte[] parserAnswerMessageAsBytes(Message answerMsg) {
        if (answerMsg == null) {
            return null;
        }

        String cmd = answerMsg.getCmd();
        if (cmd != null) {
            if (cmd.equals(Message.RESULT_STATISTIC)) {
                // the content are image.
                return (byte[]) answerMsg.getContent().get(0);
            } else if (cmd.equals(Message.RESULT_HISTOGRAM)) {
                // the content are image.
                return (byte[]) answerMsg.getContent().get(0);
            }
        }

        return null;
    }

    public ArrayList<Record> parserAnswerMessage(Message answerMsg) {
        //Analyse the message from the server,
        //Depends on the cmd, we can determine the values
        if (answerMsg == null) {
            return null;
        }

        String cmd = answerMsg.getCmd();

        if (cmd == null) {
            //TraceManager.addDev("Wrong message format - no cmd");
            return null;
        } else if (cmd.equals(Message.RESULT_SEARCH)) {
            ArrayList<Record> lrecord = new ArrayList<>();
            //get content of return result
            String resultxml = (String) answerMsg.getContent().get(0);
            Document doc = Jsoup.parse(resultxml);
            //parser content to get value by tag name.
            for (Element e : doc.select("Row")) {
                Record r = new Record();
                Document eachRow = Jsoup.parse(e.toString());
                Element e_id = eachRow.select("cve_id").first();
                Element e_title = eachRow.select("name").first();
                Element e_des = eachRow.select("summary").first();
                Element e_score = eachRow.select("score").first();
                r.setCve_id(e_id.text());
                r.setName(e_title.text());
                r.setSummary(e_des.text());
                r.setScore(e_score.text());
                lrecord.add(r);
            }
            return lrecord;


        } else if (cmd.equals(Message.RESULT_DETAIL)) {
            Record r = new Record();
            //get content of return result
            String resultxml = (String) answerMsg.getContent().get(0);

            //parser content to get value by tag name.
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
                r.setName(name.text());

                //System.out.print(r.toString());

            }

            ArrayList<Record> lrecord = new ArrayList<>();
            lrecord.add(r);
            return lrecord;
        } else
            //TraceManager.addDev("The command is not supported\n");
            return null;
    }

    /**
     * @param cmd     strings in Message class.
     * @param options list of options corresponds to list of values.
     * @param values
     * @return Message.
     */
    public Message createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values) {
        Message requestMsg = new Message(cmd, options, values);
        return requestMsg;
    }

    /**
     * @param msg    Message
     * @param server server address
     * @param port   port of service
     * @return Message from Server.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Message send(Message msg, String server, int port, boolean ssl) throws IOException, ClassNotFoundException {

        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        Socket client = null;
        SSLSocket sslClient = null;

        if (ssl) {

            //Create a ssl socket.
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslClient = (SSLSocket) sslSocketFactory.createSocket(server, port);
            sslClient.setEnabledCipherSuites(sslClient.getSupportedCipherSuites());
            TraceManager.addDev("Client has been created successfully!");

            outputStream = new ObjectOutputStream(sslClient.getOutputStream());
            inputStream = new ObjectInputStream(sslClient.getInputStream());
        } else {
            client = new Socket("LocalHost", port);
            TraceManager.addDev("Client has been created successfully!");

            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
        }


        outputStream.writeObject(msg);

        //Get back the message from server
        Message answerMsg = (Message) inputStream.readObject();

        outputStream.close();
        inputStream.close();
        if (ssl) {
            sslClient.close();
        } else {
            client.close();
        }

        return answerMsg;
    }

}

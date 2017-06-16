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
import org.apache.commons.io.FileUtils;

import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;


/**
   * Class WebCrawler
   * Implement of a webcrawler for CVEs
   * Creation: 2015
   * @version 2.0 25/03/2016
   * @author  Marie FORRAT, Angeliki AKTYPI, Ludovic APVRILLE
   * @see ui.MainGUI
 */
public class MultiThreadServer {

    /**
     *
     */
    public static String ERR_SVDOWN = "ERR : Cannot run the server, shut down the previous one!";

    /**
     *
     * @param cmd
     * @param msg
     */
    public static Message createImageAnswer(String cmd, Message msg) {
        byte[] byteImg = Message.convertImageToByte(msg);
        Message answerMessage = new Message();
        ArrayList<Object> content = new ArrayList<>();
        content.add(byteImg);

        answerMessage.createAnswerMessage(cmd, content);
        System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        return answerMessage;
    }
        
    /**
     *
     * @param msg
     * @param database
     * @throws IOException
     * @throws SQLException
     * @throws AWTException
     * @throws TransformerException
     */
    public static Message analyseRequestMessage(Message msg, web.crawler.DatabaseQuery database) throws IOException, SQLException, AWTException, TransformerException {
        
        //System.out.println(msg.getCmd());
        
        //We first create a virable "result" to get the result and push it to the content
        File resultfile = null;
        
        String cmd = msg.getCmd();
        Message answerMessage = new Message();
        
        //Depend on the command, we analyse the the message and call the right function
        if (msg.getCmd().equals(Message.CMD_SEARCH)) {

            //Set cmd for the answer message to sent back to the client
            cmd = Message.RESULT_SEARCH;
            //System.out.println(msg.getValues().get(0));
            resultfile = database.GetCVEwithKeywords(msg.getValues());
            String resultstring = FileUtils.readFileToString(resultfile, StandardCharsets.UTF_8);
            
            ArrayList<Object> content = new ArrayList<>();
            content.add(resultstring);
            
            answerMessage.createAnswerMessage(cmd, content);
            
            System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        }

        if (msg.getCmd().equals(Message.CMD_DETAIL)) {
            cmd = Message.RESULT_DETAIL;
            resultfile = database.GetinfofromCVE(msg.getValues().get(0));
            String resultstring = FileUtils.readFileToString(resultfile, StandardCharsets.UTF_8);
            ArrayList<Object> res = new ArrayList<>();
            res.add(resultstring);
            answerMessage.createAnswerMessage(cmd, res);
            System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        }

        if (msg.getCmd().equals(Message.CMD_STATISTIC)) {
            DataVisualisation datavis = new DataVisualisation(database.getDatabase());
            datavis.OpenCloud(msg.getValues().get(0));
            //Set cmd for the answer message to sent back to the client
            cmd = Message.RESULT_STATISTIC;
            answerMessage = createImageAnswer(cmd, msg);
        }
         if (msg.getCmd().equals(Message.CMD_HISTOGRAM)) {
	     DataVisualisation datavis = new DataVisualisation(database.getDatabase());
            datavis.Histogram(msg.getValues().get(0));
            //Set cmd for the answer message to sent back to the client
            cmd = Message.RESULT_HISTOGRAM;
            answerMessage = createImageAnswer(cmd, msg);
        }
        return answerMessage;
    }
    
}

package web.crawler;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import web.crawler.WebCrawler;
import myutil.externalSearch.Message;

/**
 *
 * @author Marie FORRAT & Angeliki AKTYPI & Dan Huynh VO
 */
public class MultiThreadServer {

    public static String ERR_SVDOWN = "ERR : Cannot run the server, shut down the previous one!";

    /**
     *
     * @param cmd
     * @param msg
     * @return
     */
    public static Message createImageAnswer(String cmd, Message msg) {
        byte[] byteImg = Message.convertImageToByte(msg);
        Message answerMessage = new Message();
        ArrayList<Object> content = new ArrayList();
        content.add(byteImg);

        answerMessage.createAnswerMessage(cmd, content);
        System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        return answerMessage;
    }

    /**
     *
     * @param msg
     * @param database
     * @return
     * @throws IOException
     * @throws SQLException
     * @throws AWTException
     * @throws TransformerException
     */
    public static Message analyseRequestMessage(Message msg, web.crawler.Database_query database) throws IOException, SQLException, AWTException, TransformerException {
        
        //System.out.println(msg.getCmd());
        
        //We first create a virable "result" to get the result and push it to the content
        File resultfile = null;
        
        String cmd = msg.getCmd();
        Message answerMessage = new Message();
        
        //Depend on the command, we analyse the the message and call the right function
        if (msg.getCmd().equals(Message.CMD_SEARCH)) {

            //Set cmd for the answer message to sent back to the client
            cmd = msg.RESULT_SEARCH;
            //System.out.println(msg.getValues().get(0));
            resultfile = database.GetCVEwithKeywords(msg.getValues());
            String resultstring = FileUtils.readFileToString(resultfile);
            
            ArrayList<Object> content = new ArrayList();
            content.add(resultstring);
            
            answerMessage.createAnswerMessage(cmd, content);
            
            System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        }

        if (msg.getCmd().equals(Message.CMD_DETAIL)) {
            cmd = msg.RESULT_DETAIL;
            resultfile = database.GetinfofromCVE(msg.getValues().get(0));
            String resultstring = FileUtils.readFileToString(resultfile);
            ArrayList<Object> res = new ArrayList();
            res.add(resultstring);
            answerMessage.createAnswerMessage(cmd, res);
            System.out.println(Message.SUC_CREATE_ANS_MESSAGE);
        }

        if (msg.getCmd().equals(Message.CMD_STATISTIC)) {
            Data_visualisation datavis = new Data_visualisation(WebCrawler.database);
            datavis.OpenCloud(msg.getValues().get(0));
            //Set cmd for the answer message to sent back to the client
            cmd = msg.RESULT_STATISTIC;
            answerMessage = createImageAnswer(cmd, msg);
        }
        if (msg.getCmd().equals(Message.CMD_HISTOGRAM)) {
            Data_visualisation datavis = new Data_visualisation(WebCrawler.database);
            datavis.Histogram(msg.getValues().get(0));
            //Set cmd for the answer message to sent back to the client
            cmd = msg.RESULT_HISTOGRAM;
            answerMessage = createImageAnswer(cmd, msg);
        }
        
        return answerMessage;
    }
    
}

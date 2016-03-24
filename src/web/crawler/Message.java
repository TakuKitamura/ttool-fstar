package web.crawler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.Serializable;
import javax.imageio.ImageIO;

/**
 * @author Dan Huynh VO
 */


//Message format

/**
    CMD Options Values
 *  CMD : A string of command, for example : search, detail, stats
 * search : search with keywords
 * detail : clients send CVE-ID and get back all information
 * stats : -> Images 
 *  Options and Values ArrayLists, 
 *      "Options" contains names 
 *      "Values" contains values respectively 
 * RequestMessage 
 * AnswerMessage
 */


public class Message implements Serializable {

    /**
     *
     */
    public static String PIC_SRC_STAT = "server_visualisation.png";
    public static String PIC_SRC_HIST = "server_visualisation_Hist.png";

    /**
     *
     */
    public static String PIC_DES_STAT = "client_visualisation_Stat.png";
    public static String PIC_DES_HIST = "client_visualisation_Hist.png";

    /**
     *
     */
    public static String ERR_CMD = "Message command is empty\n";

    /**
     *
     */
    public static String ERR_CMD2 = "Wrong message command\n";
    
    /**
     *
     */
    public static String SUC_CREATE_REQ_MESSAGE = "The request message is created\n";

    /**
     *
     */
    public static String SUC_CREATE_ANS_MESSAGE = "The answer message is created\n";
    
    /**
     *
     */
    public static String OPTION_KEY_WORDS = "keywords";

    /**
     *
     */
    public static String OPTION_DATE = "date";

    /**
     *
     */
    public static String OPTION_SCORE = "score";    

    /**
     *
     */
    public static String OPTION_SYSTEM = "system";

    /**
     *
     */
    public static String OPTION_NUMBER = "number";

    /**
     *
     */
    public static String OPTION_FILE_BIN = "filebin";

    /**
     *
     */
    public static String OPTION_FILE_XML = "filexml";

    /**
     *
     */
    public static String CMD_SEARCH = "search";

    /**
     *
     */
    public static String CMD_STATISTIC = "stat";
    public static String CMD_HISTOGRAM = "histo";
    /**
     *
     */
    public static String CMD_DETAIL = "details";

    /**
     *
     */
    public static String RESULT_SEARCH = "resultSearch";

    /**
     *
     */
    public static String RESULT_STATISTIC = "resultStat";
    public static String RESULT_HISTOGRAM = "resultHistogram";
    /**
     *
     */
    public static String RESULT_DETAIL = "resultDetail";


    private String cmd;
    private ArrayList<Object> content;
    //private String content;
    //private byte[] imageByte;
    private ArrayList<String> options;
    private ArrayList<String> values;
    

   
    //Get value of cmd

    /**
     *
     * @return
     */
        public String getCmd() {
        return cmd;
    }
    //Get value of content

    /**
     *
     * @return
     */
        public ArrayList<Object> getContent() {
        return content;
    }
    // Get value of imageByte;
//    public byte[] getImageByte(){
//        return imageByte;
//    }
    //Get value of Options    

    /**
     *
     * @return
     */
    public ArrayList<String> getOptions() {
        return options;
    }
    
   //Get value of values

    /**
     *
     * @return
     */
        public ArrayList<String> getValues() {
        return values;
    } 
    
    //Set value for cmd

    /**
     *
     * @param cmd
     */
        public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    
    //Set value for content

    /**
     *
     * @param content
     */
        public void setContent(ArrayList<Object> content) {
        this.content = content;
    }
    //Set value for imageByte
//    public void setImageByte(byte[] imageByte)
//    {
//        this.imageByte = imageByte;
//    }
    
    //set values for options

    /**
     *
     * @param options
     */
        public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    //Set values for values

    /**
     *
     * @param values
     */
        public void setValues(ArrayList<String> values) {
        this.values = values;
    }
    
    //Constructors
    
    /**
     *
     * @param content
     */
        
    public Message(ArrayList<Object> content){
        this.content = content;
    }
    
    /**
     *
     */
    public Message()
    {
        this.cmd = null;
        this.content = null;
        this.options = null;
        this.values = null;
        
    }
    
    /**
     *
     * @param cmd
     * @param options
     * @param values
     */
    public  Message(String cmd, ArrayList<String> options , ArrayList<String> values){
             
        this.cmd = cmd;
        this.options = options;
        this.values = values;
        
    }
    
    //Create a request message for clients

    /**
     *
     * @param cmd
     * @param options
     * @param values
     */
        public void createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values)
    {
        this.cmd = cmd;
        this.options = options;
        this.values = values;
        
    }
    
    //Create an answer for server to send to the client

    /**
     *
     * @param cmd
     * @param content
     */
        public void createAnswerMessage(String cmd, ArrayList<Object> content){
        this.content = content;
        this.cmd = cmd;
    }
    
//    public void createAnswerMessage(String cmd, byte[] imageByte){
//        this.cmd = cmd;
//        this.imageByte = imageByte;
//    }
    
    /**
     *
     * @param msg
     * @return
     */
    public ArrayList<Object> parseMessage(Message msg)
    {
        //TODO Parse the message
        return msg.content;
    }
   
    /**
     *
     * @return
     */
    public static byte[] convertImageToByte(Message msg) {
    
    byte[] imgByte = null;
    BufferedImage img;
    
        try {
            if(msg.getCmd().equals("stat"))
            {
                 img = ImageIO.read(new File(PIC_SRC_STAT));
            }
           else
            {
                img = ImageIO.read(new File(PIC_SRC_HIST));
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", baos);
                baos.flush();
                imgByte = baos.toByteArray();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Image can't not be found!\n");
        }

        System.out.println("Image has been converted successfully!\n");

        return imgByte;
    }

    //a function to convert a string byte to an image

    /**
     *
     * @param imgByte
     */
        public static void convertByteToImage(byte[] imgByte,Message msg) {

        if (imgByte != null) {

            InputStream inStream = new ByteArrayInputStream(imgByte);
            
            try {
                BufferedImage img = ImageIO.read(inStream);
                 if(msg.getCmd().equals(RESULT_STATISTIC))
                 {
                    ImageIO.write(img, "png", new File(PIC_DES_STAT)); 
                 }
                else
                 {
                     ImageIO.write(img, "png", new File(PIC_DES_HIST));
                 }
                System.out.println("Image has been created successfully!\n");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("Image can't not be created!\n");
            }
        } else {
            System.out.print("Image can't not be created!\n");
        }
    }

}

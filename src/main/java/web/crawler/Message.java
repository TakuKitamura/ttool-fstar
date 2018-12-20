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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


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
 * @author Dan Huynh VO
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
    

   


    /**
     * Get value of command
     * @return : value of command
     */
        public String getCmd() {
        return cmd;
    }

    /**
     * Get value of content
     * @return : value of content
     */
        public ArrayList<Object> getContent() {
        return content;
    }
    // Get value of imageByte;
//    public byte[] getImageByte(){
//        return imageByte;
//    }

    /**
     * Get value of Options
     * @return list of Options
     */
    public ArrayList<String> getOptions() {
        return options;
    }


    /**
     * Get value of values
     * @return : values
     */
        public ArrayList<String> getValues() {
        return values;
    }

    /**
     * Set value for cmd
     * @param cmd : command
     */
        public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    


    /**
     * Set value for content
     * @param content : value of content
     */
        public void setContent(ArrayList<Object> content) {
        this.content = content;
    }
    //Set value for imageByte
//    public void setImageByte(byte[] imageByte)
//    {
//        this.imageByte = imageByte;
//    }
    

    /**
     * set values for options
     * @param options : list of options
     */
        public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    /**
     * Set values for values
     * @param values : list of values
     */
        public void setValues(ArrayList<String> values) {
        this.values = values;
    }
    
    /* Constructors */
    public Message(ArrayList<Object> content){
        this.content = content;
    }
    
    /**
     * Constructor without parameter
     */
    public Message()
    {
        this.cmd = null;
        this.content = null;
        this.options = null;
        this.values = null;
        
    }
    
    /* Constructor */
    public  Message(String cmd, ArrayList<String> options , ArrayList<String> values){
             
        this.cmd = cmd;
        this.options = options;
        this.values = values;
        
    }
    

    /**
     * Create a request message for clients
     * @param cmd       : command
     * @param options   : list of Options
     * @param values    : value of message
     */
        public void createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values)
    {
        this.cmd = cmd;
        this.options = options;
        this.values = values;
        
    }

    /**
     * Create an answer for server to send to the client
     * @param cmd       : command
     * @param content   : content
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
     * parsing message
     * @param msg   : message
     * @return      : content
     */
    public ArrayList<Object> parseMessage(Message msg)
    {
        //TODO Parse the message
        return msg.content;
    }
   
    /**
     * convert image to bytes
     * @param msg : message
     * @return    : array of bytes
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
     * Convert bytes to Image
     * @param imgByte : image bytes
     * @param msg     : message
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

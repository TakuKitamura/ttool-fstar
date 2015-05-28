package myutil.externalSearch;

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
 * Created by Dan on 3/22/15.
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

    public static String PIC_SRC = "test2.png";
    public static String PIC_DES = "clonetest2.png";
    public static String ERR_CMD = "Message command is empty";
    public static String ERR_CMD2 = "Wrong message command";

    public static String SUC_CREATE_REQ_MESSAGE = "The request message is created ";
    public static String SUC_CREATE_ANS_MESSAGE = "The answer message is created ";



    public static String OPTION_KEY_WORDS = "keywords";
    public static String OPTION_YEAR = "year";
    public static String OPTION_SCORE = "score";
    public static String OPTION_NUMBER = "number";
    public static String OPTION_SYSTEM = "system";
    public static String OPTION_FILE_BIN = "filebin";
    public static String OPTION_FILE_XML = "filexml";


    public static String CMD_SEARCH = "search";
    public static String CMD_STATISTIC = "stat";
    public static String CMD_DETAIL = "detail";

    public static String RESULT_SEARCH = "resultSearch";
    public static String RESULT_STATISTIC = "resultStat";
    public static String RESULT_DETAIL = "resultDetail";


    private String cmd;
    private ArrayList<Object> content;
    private ArrayList<String> options;
    private ArrayList<String> values;



    //Constructors

    public Message(ArrayList<Object> content){
        this.content = content;
    }
    public Message()
    {
        this.content = null;
        this.options = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public Message(String cmd){
        this.cmd = cmd;
        this.options = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public  Message(String cmd, ArrayList<String> options , ArrayList<String> values){

        this.cmd = cmd;
        this.options = options;
        this.values = values;

    }
    public void addOptionValueMessage (String option, String value){
        this.options.add(option);
        this.values.add(value);
    }

    public void addKeywordMessage(String value){
        this.addOptionValueMessage("keyword",value);
    }

    //Get value of cmd
    public String getCmd() {
        return cmd;
    }
    //Get value of content
    public ArrayList<Object> getContent() {
        return content;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    //Get value of values
    public ArrayList<String> getValues() {
        return values;
    }

    //Set value for cmd
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    //Set value for content
    public void setContent(ArrayList<Object> content) {
        this.content = content;
    }

    //set values for options
    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    //Set values for values
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    //Create a request message for clients
    public void createRequestMessage(String cmd, ArrayList<String> options, ArrayList<String> values)
    {
        this.cmd = cmd;
        this.options = options;
        this.values = values;
    }

    //Create an answer for server to send to the client
    public void createAnswerMessage(String cmd, ArrayList<Object> content) {
        this.content = content;
        this.cmd = cmd;
    }



    public ArrayList<Object> parseMessage(Message msg)
    {
            //TODO Parse the message
        return msg.content;
    }


    public static byte[] convertImageToByte(){
        byte[] imgByte = null;

        try {
            BufferedImage img = ImageIO.read(new File(PIC_SRC));

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", baos);
                baos.flush();
                imgByte = baos.toByteArray();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Image can't not be found!");
        }

        System.out.println("Image has been converted successfully!");

        return imgByte;
    }

    //a function to convert a string byte to an image
    public static void convertByteToImage(byte[] imgByte){

        if (imgByte != null){

            InputStream inStream = new ByteArrayInputStream(imgByte);
            try {
                BufferedImage img = ImageIO.read(inStream);

                ImageIO.write(img, "png", new File(PIC_DES));
                System.out.println("Image has been created successfully!");


            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("Image can't not be created!");
            }
        }

        else System.out.print("Image can't not be created!");
    }




}
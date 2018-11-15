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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


//Message format


/**
 * JDialogSearchBox
 * unity message for client and server
 * <p>
 * CMD Options Values
 * CMD : A string of command, for example : search, detail, stats
 * search : search with keywords
 * detail : clients send CVE-ID and get back all information
 * stats : -> Images
 * Options and Values ArrayLists,
 * "Options" contains names
 * "Values" contains values respectively
 * RequestMessage
 * AnswerMessage
 * <p>
 * Creation: 22/03/2015
 *
 * @author Dan VO
 * @version 1.0 11/03/2015
 */
public class Message implements Serializable {

    public static String PIC_SRC = "server_visualisation.png";
    public static String PIC_DES = "client_visualisation.png";
    public static String PIC_SRC_HIST = "server_visualisation_Hist.png";
    public static String PIC_SRC_STAT = "server_visualisation.png";
    public static String PIC_DES_STAT = "client_visualisation_Stat.png";
    public static String PIC_DES_HIST = "client_visualisation_Hist.png";
    public static String ERR_CMD = "Message command is empty\n";
    public static String ERR_CMD2 = "Wrong message command\n";

    public static String SUC_CREATE_REQ_MESSAGE = "The request message is created\n";
    public static String SUC_CREATE_ANS_MESSAGE = "The answer message is created\n";


    public static String OPTION_KEY_WORDS = "keywords";
    public static String OPTION_YEAR = "year";
    public static String OPTION_SCORE = "score";
    public static String OPTION_NUMBER = "number";
    public static String OPTION_SYSTEM = "system";
    //public static String OPTION_FILE_BIN = "filebin";
    //public static String OPTION_FILE_XML = "filexml";


    public static String CMD_SEARCH = "search";
    public static String CMD_STATISTIC = "stat";
    public static String CMD_DETAIL = "detail";
    public static String CMD_HISTOGRAM = "histo";

    public static String RESULT_SEARCH = "resultSearch";
    public static String RESULT_STATISTIC = "resultStat";
    public static String RESULT_DETAIL = "resultDetail";
    public static String RESULT_HISTOGRAM = "resultHistogram";


    private String cmd;
    private ArrayList<Object> content;
    private ArrayList<String> options;
    private ArrayList<String> values;


    //Constructors

    public Message(ArrayList<Object> content) {
        this.content = content;
    }

    public Message() {
        this.content = null;
        this.options = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public Message(String cmd) {
        this.cmd = cmd;
        this.options = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public Message(String cmd, ArrayList<String> options, ArrayList<String> values) {

        this.cmd = cmd;
        this.options = options;
        this.values = values;

    }

    public void addOptionValueMessage(String option, String value) {
        this.options.add(option);
        this.values.add(value);
    }

    public void addKeywordMessage(String value) {
        this.addOptionValueMessage("keyword", value);
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


    //Create an answer for server to send to the client
    public void createAnswerMessage(String cmd, ArrayList<Object> content) {
        this.content = content;
        this.cmd = cmd;
    }


    public ArrayList<Object> parseMessage(Message msg) {
        return msg.content;
    }


    public static byte[] convertImageToByte(Message msg) {
        byte[] imgByte = null;
        BufferedImage img;

        try {
            if (msg.getCmd().equals("stat")) {
                img = ImageIO.read(new File(PIC_SRC_STAT));
            } else {
                img = ImageIO.read(new File(PIC_SRC_HIST));
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", baos);
                baos.flush();
                imgByte = baos.toByteArray();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            TraceManager.addDev("Image can't not be found!\n");
        }

        TraceManager.addDev("Image has been converted successfully!");

        return imgByte;
    }

    //a function to convert a string byte to an image
    public static void convertByteToImage(byte[] imgByte, Message msg) {

        if (imgByte != null) {

            InputStream inStream = new ByteArrayInputStream(imgByte);
            try {
                BufferedImage img = ImageIO.read(inStream);
                if (msg.getCmd().equals(RESULT_STATISTIC)) {
                    ImageIO.write(img, "png", new File(PIC_DES_STAT));
                } else {
                    ImageIO.write(img, "png", new File(PIC_DES_HIST));
                }
                TraceManager.addDev("Image has been created successfully!\n");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                TraceManager.addDev("Image can't not be created!\n");
            }
        } else System.out.print("Image can't not be created!");
    }
}

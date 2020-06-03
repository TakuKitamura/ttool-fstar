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


package cli;

import common.ConfigurationTTool;
import launcher.LauncherException;
import launcher.RTLLauncher;
import launcher.RshClient;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.PluginManager;
import myutil.TraceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;
import ui.*;
import ui.interactivesimulation.JFrameInteractiveSimulation;
import ui.interactivesimulation.SimulationTransaction;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.util.IconManager;
import avatartranslator.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.BitSet;
import java.util.*;


/**
 * Class Set
 * Creation: 02/06/2020
 * Version 2.0 02/06/2020
 *
 * @author Ludovic APVRILLE
 */
public class SimulatorScript extends Command  implements Runnable  {
    private static String[] channels = {"wDesignHMAC__send_train_position1_Frame_R__DesignHMAC__framePL1", "wDesignHMAC__framebuffer",
            "rDesignHMAC__framebuffer", "wDesignHMAC__computationResult__DesignHMAC__controlData",
            "rDesignHMAC__computationResult__DesignHMAC__controlData"};


    private long [] times;
    private int currentIndex;
    private RemoteConnection rc;

    public SimulatorScript() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "simulatorscript";
    }

    public String getShortCommand() {
        return "sc";
    }

    public String getUsage() { return "simulatorscript <path_to_simulator_executable> <inputFile> <outputFile>"; }

    public String getDescription() { return "Starting a simulation script test. Reserved for Development purpose"; }

    public String getExample() {
        return "simulatorscript run.x file1.txt file2.txt";
    }


    public  String executeCommand(String command, Interpreter interpreter) {
        try {
            String[] commands = command.split(" ");
            if (commands.length < 3) {
                return Interpreter.BAD;
            }
           return executeSimulatorScript(commands[0], commands[1], commands[2], interpreter);
        } catch (Exception e) {
            TraceManager.addDev("Exception: " + e.getMessage());
            return "Test failed";

        }

    }

    public void fillSubCommands() {

    }

    private String executeSimulatorScript(String simuPath, String file1, String file2, Interpreter interpreter) throws java.io.IOException, java
            .lang.InterruptedException {
        // Checking arguments
        // Test all files
        File simuFile = new File(simuPath);
        if (!simuFile.exists()) {
            return interpreter.BAD_FILE_NAME + ": " + simuPath;
        }

        File inputFile = new File(file1);
        if (!simuFile.exists()) {
            return interpreter.BAD_FILE_NAME + ": " + file1;
        }

        // If the output file does not exist, its is not important: we create it!
        File outputFile = new File(file2);


        // Starts simulation
        Process simuProcess = Runtime.getRuntime().exec(simuPath + " -server");

        // Wait for one second
        Thread.sleep(1000);


        // Connects to the simulator
        rc = new RemoteConnection("localhost");
        try {
            rc.connect();
        } catch (RemoteConnectionException rce) {
            return "Could not connect";
        }


        // Opens the two files
        boolean running = true;
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream( inputFile ) );
        String readString = "";
        String line = "";
        boolean first = true;
        double lastValue = 0;

        try {
            while (running) {
                if (reader.available() > 0) {
                    char c = (char) reader.read();

                    // regular character?
                    if (String.valueOf(c).matches(".")) {
                        readString += c;
                    } else {
                        // End of line
                        // Must handle the line
                        line = readString.trim();
                        TraceManager.addDev("Line read:" + line);
                        readString = "";
                        String lines[] = line.split(" ");
                        if (lines.length > 1) {
                            double value1 = Double.parseDouble(lines[1]);
                            if (first) {
                                first = false;
                                lastValue = value1;
                            } else {
                                if (value1 != lastValue) {
                                    lastValue = value1;
                                    double time1 = Double.parseDouble(lines[0]);
                                    // Run simulation until time1
                                    runSimulationTo(rc, time1);
                                    // Remove all transactions
                                    removeAllTransactions(rc);
                                    currentIndex = 0;
                                    times = new long[channels.length];
                                    // Get time of event1
                                    for(int i=0; i<channels.length; i++) {
                                        // Wait for channel operation
                                        runUntilChannel(rc, channels[0]);
                                        // Get current Time
                                        sendGetSimulationTime(rc);
                                        waitForNextIndex();
                                    }

                                    // Wait for event2 to occur
                                    // Get time of event2.
                                    // Append to file2 time2-time1
                                }
                            }
                        }

                    }
                }

                // Nothing new in the file
                else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        running = false;
                    }
                }
            }
        } catch (RemoteConnectionException rce) {
            return "Connection failure";
        }


        return null;

    }


    public void sendGetSimulationTime(RemoteConnection rc) throws RemoteConnectionException {
        rc.send("13");
    }

    private void runSimulationTo(RemoteConnection rc, double time1) throws RemoteConnectionException {
        // Must convert in clock cycles
        // We assume 200 MHz
        // We assume time is in ms
        long nbOfCycles = (long)(200000 * time1);
        rc.send("1 5 " + nbOfCycles);

    }

    private void removeAllTransactions(RemoteConnection rc) throws RemoteConnectionException {
        rc.send("26");
    }

    private void runUntilChannel(RemoteConnection rc, String channelName) throws RemoteConnectionException {

    }

    private synchronized void waitForNextIndex() {
        int oldValue = currentIndex;
        while( oldValue == currentIndex) {
            try {
                wait();
            } catch (InterruptedException ie) {

            }
        }
    }



    // Listening thread
    public void run() {
        try {
            while (true) {
                String s = rc.readOneLine();
                analyzeServerAnswer(s);
            }
        } catch (Exception e) {

        }
    }


    protected void analyzeServerAnswer(String s) {
        //
        String ssxml = "";
        int index0 = s.indexOf("<?xml");

        if (index0 != -1) {
            //
            ssxml = s.substring(index0, s.length()) + "\n";
        } else {
            //
            ssxml = ssxml + s + "\n";
        }

        index0 = ssxml.indexOf("</siminfo>");

        if (index0 != -1) {
            //
            ssxml = ssxml.substring(0, index0+10);
            loadXMLInfoFromServer(ssxml);
            ssxml = "";
        }
    }


    protected boolean loadXMLInfoFromServer(String xmldata) {
        //jta.append("XML from server:" + xmldata + "\n\n");

        DocumentBuilderFactory dbf;
        DocumentBuilder db;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            dbf = null;
            db = null;
        }

        if ((dbf == null) || (db == null)) {
            return false;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(xmldata).getBytes());
        int i;

        try {
            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;
            Node node;

            nl = doc.getElementsByTagName(JFrameInteractiveSimulation.SIMULATION_HEADER);

            if (nl == null) {
                return false;
            }

            for(i=0; i<nl.getLength(); i++) {
                node = nl.item(i);
                //
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    // create design, and get an index for it
                    return loadConfiguration(node);
                }
            }
        } catch (IOException e) {
            TraceManager.addError("Error when parsing server info:" + e.getMessage());
            return false;
        } catch (SAXException saxe) {
            TraceManager.addError("Error when parsing server info:" + saxe.getMessage());
            TraceManager.addError("xml:" + xmldata);
            return false;
        }

        return true;
    }

    public static String decodeString(String s)  {
        if (s == null)
            return s;
        byte b[] = null;
        try {
            b = s.getBytes("ISO-8859-1");
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }


    protected boolean loadConfiguration(Node node1) {
        NodeList diagramNl = node1.getChildNodes();
        if (diagramNl == null) {
            return false;
        }
        Element elt;
        Node node, node0;
        NodeList nl;


        try {
            for (int j = 0; j < diagramNl.getLength(); j++) {
                node = diagramNl.item(j);

                if (node == null) {
                    TraceManager.addDev("null node");
                    return false;
                }

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    elt = (Element) node;

                    // Status
                    if (elt.getTagName().compareTo(JFrameInteractiveSimulation.SIMULATION_GLOBAL) == 0) {

                        nl = elt.getElementsByTagName("simtime");
                        if ((nl != null) && (nl.getLength() > 0)) {
                            node0 = nl.item(0);
                            if (node0.getTextContent() != null) {
                                String val = node0.getTextContent();
                                //Write value to table
                                writeTimeValue(val);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private synchronized void writeTimeValue(String val) {
        if (times == null) {
            return;
        }
        try {
            long valL = Long.decode(val);
            times[currentIndex] = valL;
            currentIndex ++;
            notify();
        } catch (Exception e) {

        }
    }




}

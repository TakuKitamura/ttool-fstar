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
import tmltranslator.simulation.SimulationTransaction;
import ui.*;
import ui.interactivesimulation.JFrameInteractiveSimulation;
import ui.tmldd.TMLArchiCPUNode;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.util.IconManager;
import avatartranslator.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.rmi.Remote;
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
    private long latestTime = -1;
    private RemoteConnection rc;
    private boolean isReady = false;

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
        //Process simuProcess = Runtime.getRuntime().exec(simuPath + " -server");

        // Wait for one second
        Thread.sleep(1000);


        // Connects to the simulator
        rc = new RemoteConnection("localhost");
        try {
            rc.connect();
            isReady = true;
        } catch (RemoteConnectionException rce) {
            return "Could not connect";
        }

        Thread t = new Thread(this);
        t.start();


        // Opens the input file
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
                        //TraceManager.addDev("Line read:" + line);
                        readString = "";
                        //line = Conversion.replaceAllString(line, "\t", " ");
                        //line = Conversion.replaceAllString(line, "  ", " ");
                        //TraceManager.addDev("Line read:" + line);
                        String lines[] = line.split("\\s+");
                        /*for(String s: lines) {
                            TraceManager.addDev("\t>" + s + "<");
                        }*/
                        if (lines.length > 1) {
                            //TraceManager.addDev("Lines length: " + lines.length);
                            double value1 = Double.parseDouble(lines[1]);
                            if (first) {
                                //TraceManager.addDev("First value");
                                first = false;
                                lastValue = value1;
                            } else {
                                if (value1 != lastValue) {

                                    lastValue = value1;
                                    double time1 = Double.parseDouble(lines[0]);
                                    TraceManager.addDev("Sender. Time: " + time1 + " New value: " + value1);
                                    // Run simulation until time1
                                    //runSimulationTo(rc, time1);
                                    //Thread.sleep(50);
                                    //waitForNextTime(rc);
                                    // Remove all transactions
                                    removeAllTransactions(rc);
                                    currentIndex = 0;
                                    times = new long[channels.length];
                                    // Get time of event1
                                    for(int i=0; i<channels.length; i++) {
                                        // Wait for channel operation
                                        TraceManager.addDev("Sender. i. " + i + " - Running until channel: " + channels[i]);
                                        runUntilChannel(rc, channels[i]);
                                        Thread.sleep(5);
                                        TraceManager.addDev("Sender. Waiting for current time");

                                        // Get current Time
                                        //sendGetSimulationTime(rc);
                                        //Thread.sleep(50);
                                        times[currentIndex++] = waitForNextTime(rc);
                                        TraceManager.addDev("Sender. Simulation time is: " + times[i]);
                                    }
                                    // Compute final time
                                    long finalTime = times[times.length-1] - times[0];
                                    TraceManager.addDev("Sender. Final time: " + finalTime + " clock cycles");

                                    // Compute this time in milliseconds
                                    double physicalTimeMillis = finalTime / 200000; // We assume 200MHz
                                    System.out.println("***********************************\n" +
                                            "Final time: " + physicalTimeMillis + " ms\n" +
                                            "***********************************");

                                    // Append result to output file
                                    FileWriter fw = new FileWriter(outputFile, true);
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    PrintWriter out = new PrintWriter(bw);
                                    out.println(""+physicalTimeMillis);
                                    out.flush();

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
        long nbOfCycles = (long)(200 * time1);
        toServer("1 5 " + nbOfCycles, rc);

    }

    private void removeAllTransactions(RemoteConnection rc) throws RemoteConnectionException {
        toServer("26", rc);
    }

    private void runUntilChannel(RemoteConnection rc, String channelName) throws RemoteConnectionException {
        String realChannelName = channelName.substring(1, channelName.length());
        int cmdVal = 17;
        if (channelName.startsWith("r")) {
            cmdVal = 18;
        }

        String cmd = "1 " + cmdVal + " " + realChannelName;
        toServer(cmd, rc);

    }

    private synchronized void toServer(String s, RemoteConnection rc) throws RemoteConnectionException  {
        while(!isReady) {
            TraceManager.addDev("Server not ready");
            try {
                sendGetSimulationTime(rc);
                wait(250);
            } catch (InterruptedException ie) {

            }
        }
        TraceManager.addDev("Sender. Cmd to server: " + s);
        rc.send(s);
    }

    private synchronized long waitForNextTime(RemoteConnection rc) throws RemoteConnectionException {
        int oldValue = currentIndex;
        latestTime = -1;
        while( latestTime == -1) {
            TraceManager.addDev("Sender. Sending time request.");
            sendGetSimulationTime(rc);
            try {
                wait(250);
            } catch (InterruptedException ie) {
                TraceManager.addDev("Sender. Interrupted");
            }
        }
        long ret = latestTime;
        latestTime = -1;
        return ret;
    }



    // Listening thread
    public void run() {
        try {
            while (true) {
                //TraceManager.addDev("\tReceiver. Waiting from server input.");
                String s = rc.readOneLine();
                //TraceManager.addDev("\tReceiver. Received from server:" + s);
                analyzeServerAnswer(s);
            }
        } catch (Exception e) {

        }
    }


    protected synchronized void  analyzeServerAnswer(String s) {
        if (s.startsWith("<status>")) {
            if (s.contains("ready")) {
                isReady = true;
            } else {
                isReady = false;
            }
            notifyAll();
            TraceManager.addDev("\tReceiver. Status: " + s);
            return;
        }


        int index0 = s.indexOf("<simtime>");
        int index1 = s.indexOf("</simtime>");
        if ((index0 > -1) && (index1 > -1)) {
            String val = s.substring(index0+9, index1).trim();
            //TraceManager.addDev("Reading simulation time:" + val);
            writeTimeValue(val);
        }
    }


    private synchronized void writeTimeValue(String val) {

        long valL = Long.decode(val);
        TraceManager.addDev("\tReceiver. Received simulation time:" + valL);
        latestTime = valL;
        notifyAll();
    }




}


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

package common;

import myutil.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

/**
 * Class ConfigurationTTool Creation: 21/12/2003 Version 1.0
 *
 * @author Ludovic APVRILLE
 */
public class ConfigurationTTool {

    public static String LauncherPort = "";
    public static String RTLHost = "";
    public static String RTLPath = "";
    public static String DTA2DOTPath = "";
    public static String RGSTRAPPath = "";
    public static String RG2TLSAPath = "";
    public static String AldebaranHost = "";
    public static String AldebaranPath = "";
    public static String BcgioPath = ""; // Same host as aldebaran
    public static String BisimulatorPath = "";
    public static String BcgminPath = ""; // Same host as aldebaran
    public static String BcgmergePath = ""; // Same host as aldebaran
    public static String CaesarPath = ""; // Same host as aldebaran
    public static String CaesarOpenPath = ""; // Same host as aldebaran
    public static String DOTTYHost = "";
    public static String DOTTYPath = "";
    public static String FILEPath = "";
    public static String DownloadedFILEPath = "";
    public static String LOTOSPath = "";
    public static String LIBPath = "";
    public static String IMGPath = "";
    public static String DocGenPath = "";
    public static String GGraphPath = "";
    public static String TGraphPath = "";
    public static String TToolUpdateURL = "";
    public static String TToolUpdateProxy = "";
    public static String TToolUpdateProxyPort = "";
    public static String TToolUpdateProxyHost = "";
    public static String JavaCodeDirectory = "";
    public static String JavaCompilerPath = "";
    public static String TToolClassPath = "";
    public static String SimuJavaCodeDirectory = "";
    public static String TToolSimuClassPath = "";
    public static String JavaExecutePath = "";
    public static String JavaHeader = "";
    public static String NCDirectory = "";
    public static String SystemCCodeDirectory = "";
    public static String TMLCodeDirectory = "";
    public static String CCodeDirectory = "";
    public static String SystemCCodeCompileCommand = "";
    public static String SystemCCodeExecuteCommand = "";
    public static String SystemCCodeExecuteXCycle = "";
    public static String SystemCCodeInteractiveExecuteCommand = "";
    public static String SystemCHost = "";
    public static String VCDPath = "";
    public static String GTKWavePath = "";
    public static String UPPAALCodeDirectory = "";
    public static String UPPAALVerifierPath = "";
    public static String UPPAALVerifierHost = "";

    // Issue #35: UPPAAL change in property verification message
    public static String UPPAALPropertyVerifMessage = "";
    public static String UPPAALPropertyNotVerifMessage = "";

    public static String ProVerifCodeDirectory = "";
    public static String ProVerifVerifierPath = "";
    public static String ProVerifVerifierHost = "";
    public static String ExternalCommand1Host = "";
    public static String ExternalCommand2Host = "";
    public static String ExternalCommand1 = "";
    public static String ExternalCommand2 = "";

    // AVATAR Simulation
    /*
     * public static String AVATARSimulationHost = ""; public static String
     * AVATARCPPSIMCodeDirectory = ""; public static String
     * AVATARCPPSIMCompileCommand = ""; public static String
     * AVATARCPPSIMCodeExecuteCommand = ""; public static String
     * AVATARCPPSIMInteractiveExecuteCommand = "";
     */

    // AVATAR Code generation
    public static String AVATARExecutableCodeDirectory = "";
    public static String AVATARMPSoCCodeDirectory = "";
    public static String AVATARMPSoCCompileCommand = "";
    public static String AVATARMPSoCPerformanceEvaluationDirectory = "";
    public static String AVATARExecutableCodeHost = "";
    public static String AVATARExecutableCodeCompileCommand = "";
    public static String AVATARExecutableCodeExecuteCommand = "";
    public static String AVATARExecutableSoclibCodeCompileCommand = "";
    public static String AVATARExecutableSoclibCodeExecuteCommand = "";
    public static String AVATARExecutableSoclibCodeTraceCommand = "";
    public static String AVATARExecutableSoclibTraceFile = "";

    // Z3
    public static String Z3LIBS = "";

    // Ontology
    // public static String RequirementOntologyWebsite = "";
    // public static String AttackOntologyWebsite = "";

    // PLUGINS
    public static String[] PLUGIN_PKG = new String[0];
    public static String PLUGIN_PATH = "";
    public static String[] PLUGIN = new String[0];
    // public static String PLUGIN_JAVA_CODE_GENERATOR = "";
    // public static String[] PLUGIN_GRAPHICAL_COMPONENT = new String[0];

    // URL for models
    public static String URL_MODEL = "http://ttool.telecom-paris.fr/networkmodels/models.txt";

    // Others
    public static String RGStyleSheet = "";

    public static String LastOpenFile = "";
    public static boolean LastOpenFileDefined = false;
    public static int NB_LAST_OPEN_FILE = 10;
    public static String[] LastOpenFiles = new String[NB_LAST_OPEN_FILE];

    public static String LastWindowAttributesX = "", LastWindowAttributesY = "";
    public static String LastWindowAttributesWidth = "", LastWindowAttributesHeight = "";
    public static String LastWindowAttributesMax = "";

    public static String fileName = "";

    public static String ExternalServer = "";
    public static String ProVerifHash = "";

    public static void makeDefaultConfiguration() {
        // TraceManager.addDev(Paths.get("").toAbsolutePath().toString());
        // TraceManager.addDev("User.dir path:" + System.getProperty("user.dir"));
        // Path currentRelativePath = Paths.get("");
        // String s = currentRelativePath.toAbsolutePath().toString();
        // String s = System.getProperty("user.dir");
        // IMGPath = s;

    }

    public static void loadConfiguration(String _fileName, boolean systemcOn) throws MalformedConfigurationException {
        fileName = _fileName;

        File f = new File(fileName);

        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb");
        }

        String data = FileUtils.loadFileData(f);

        if (data == null) {
            throw new MalformedConfigurationException("Filepb");
        }

        loadConfigurationFromXML(data, systemcOn);
        SpecConfigTTool.loadConfiguration();
    }

    public static void saveConfiguration() throws MalformedConfigurationException {
        int index0, index1, index2;// , index3;
        String tmp, tmp1, tmp2, location;
        File f = new File(fileName);
        boolean write = false;

        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb");
        }

        String data = FileUtils.loadFileData(f);

        if (data == null) {
            throw new MalformedConfigurationException("Filepb");
        }

        while ((index0 = data.indexOf("<LastOpenFile")) != -1) {
            index1 = data.indexOf("/>", index0 + 1);
            if (index1 == -1) {
                break; // pb in the configuration?
            }
            data = data.substring(0, index0) + data.substring(index1 + 2, data.length());
        }

        index0 = data.indexOf("</TURTLECONFIGURATION>");

        String toBeAdded = "";
        // Adding configuration there
        for (int i = 0; i < LastOpenFiles.length; i++) {
            String file = LastOpenFiles[i];
            if ((file != null) && (file.length() > 0)) {
                toBeAdded = toBeAdded + "<LastOpenFile data=\"" + file + "\" />\n";
            }
        }
        data = data.substring(0, index0 - 1) + toBeAdded + "\n" + data.substring(index0, data.length());

        // sb.append("data = " + data + " ConfigurationTTool.LastOpenFile=" +
        // ConfigurationTTool.LastOpenFile);

        if (index0 > -1) {
            index1 = data.indexOf('"', index0);
            if (index1 > -1) {
                index2 = data.indexOf('"', index1 + 1);
                if (index2 > -1) {
                    tmp = data.substring(index2, data.length());
                    data = data.substring(0, index1 + 1) + ConfigurationTTool.LastOpenFile + tmp;
                    // sb.append("data = " + data);
                    write = true;
                    /*
                     * try { FileOutputStream fos = new FileOutputStream(f);
                     * fos.write(data.getBytes()); fos.close(); } catch (Exception e) { throw new
                     * MalformedConfigurationException("Saving file failed"); }
                     */
                }
            }
        }
        // ---------------------------------------------
        index0 = data.indexOf("ExternalServer");

        // sb.append("data = " + data + " ConfigurationTTool.LastOpenFile=" +
        // ConfigurationTTool.LastOpenFile);

        if (index0 > -1) {
            index1 = data.indexOf('"', index0);
            if (index1 > -1) {
                index2 = data.indexOf('"', index1 + 1);
                if (index2 > -1) {
                    tmp = data.substring(index2, data.length());
                    data = data.substring(0, index1 + 1) + ConfigurationTTool.ExternalServer + tmp;
                    // sb.append("data = " + data);
                    write = true;
                    /*
                     * try { FileOutputStream fos = new FileOutputStream(f);
                     * fos.write(data.getBytes()); fos.close(); } catch (Exception e) { throw new
                     * MalformedConfigurationException("Saving file failed"); }
                     */
                }
            }
        }
        // --------------------------

        index0 = data.indexOf("LastWindowAttributes");
        if (index0 > -1) {
            tmp1 = data.substring(0, index0 + 20);
            tmp2 = data.substring(index0 + 20, data.length());
            index1 = tmp2.indexOf("/>");
            if (index1 > -1) {
                tmp2 = tmp2.substring(index1, tmp2.length());
                location = " x=\"" + LastWindowAttributesX;
                location += "\" y=\"" + LastWindowAttributesY;
                location += "\" width=\"" + LastWindowAttributesWidth;
                location += "\" height=\"" + LastWindowAttributesHeight;
                location += "\" max=\"" + LastWindowAttributesMax + "\" ";
                data = tmp1 + location + tmp2;
                write = true;
            }
        } else {
            index1 = data.indexOf("</TURTLECONFIGURATION>");
            if (index1 > -1) {
                location = "<LastWindowAttributes x=\"" + LastWindowAttributesX;
                location += "\" y=\"" + LastWindowAttributesY;
                location += "\" width=\"" + LastWindowAttributesWidth;
                location += "\" height=\"" + LastWindowAttributesHeight;
                location += "\" max=\"" + LastWindowAttributesMax + "\"/>\n\n";
                data = data.substring(0, index1) + location + data.substring(index1, data.length());
                write = true;
            }
        }
        index0 = data.indexOf("ProVerifHash");
        if (index0 > -1) {
            index1 = data.indexOf('"', index0);
            if (index1 > -1) {
                index2 = data.indexOf('"', index1 + 1);
                if (index2 > -1) {
                    tmp = data.substring(index2, data.length());
                    data = data.substring(0, index1 + 1) + ConfigurationTTool.ProVerifHash + tmp;
                    write = true;
                }
            }
        } else {
            index1 = data.indexOf("</TURTLECONFIGURATION>");
            if (index1 > -1) {
                location = "<ProVerifHash data=\"" + ConfigurationTTool.ProVerifHash + "\"/>\n\n";
                data = data.substring(0, index1) + location + data.substring(index1, data.length());
                write = true;
            }
        }

        data = Conversion.replaceAllString(data, "\n\n", "\n");

        if (write) {
            // sb.append("Writing data=" + data);
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data.getBytes());
                fos.close();
            } catch (Exception e) {
                throw new MalformedConfigurationException("Saving file failed");
            }
        } else {
            TraceManager.addError("Configuration could not be saved");
        }

    }

    public static boolean configSizeAvailable() {
        if (LastWindowAttributesX.length() == 0) {
            return false;
        }

        if (LastWindowAttributesY.length() == 0) {
            return false;
        }

        if (LastWindowAttributesWidth.length() == 0) {
            return false;
        }

        return LastWindowAttributesHeight.length() != 0;
    }

    public static void printConfiguration(boolean systemcOn) {
        TraceManager.addDev(getConfiguration(systemcOn));
    }

    public static String getConfiguration(boolean systemcOn) {
        StringBuffer sb = new StringBuffer("");

        sb.append("Launcher:\n");
        sb.append("LauncherPort: " + LauncherPort + "\n");
        // Formal verification
        sb.append("RTL:\n");
        sb.append("RTLHost: " + RTLHost + "\n");
        sb.append("RTLPath: " + RTLPath + "\n");
        sb.append("DTA2DOTPath: " + DTA2DOTPath + "\n");
        sb.append("RG2TLSAPath: " + RG2TLSAPath + "\n");
        sb.append("RGSTRAPPath: " + RGSTRAPPath + "\n");
        sb.append("\nCADP:\n");
        sb.append("AldebaranHost: " + AldebaranHost + "\n");
        sb.append("AldebaranPath: " + AldebaranPath + "\n");
        sb.append("BcgioPath: " + BcgioPath + "\n");
        sb.append("BcgminPath: " + BcgminPath + "\n");
        sb.append("BisimulatorPath: " + BisimulatorPath + "\n");
        sb.append("BcgmergePath: " + BcgmergePath + "\n");
        sb.append("CaesarPath: " + CaesarPath + "\n");
        sb.append("CaesarOpenPath: " + CaesarOpenPath + "\n");
        sb.append("\nDotty:\n");
        sb.append("DOTTYHost: " + DOTTYHost + "\n");
        sb.append("DOTTYPath: " + DOTTYPath + "\n");
        // UPPAAL
        sb.append("\nUPPAAL:\n");
        sb.append("UPPAALCodeDirectory: " + UPPAALCodeDirectory + "\n");
        sb.append("UPPAALVerifierPATH: " + UPPAALVerifierPath + "\n");
        sb.append("UPPAALVerifierHOST: " + UPPAALVerifierHost + "\n");

        // Issue #35
        sb.append("UPPAALPropertyVerifMessage: " + UPPAALPropertyVerifMessage + "\n");
        sb.append("UPPAALPropertyNotVerifMessage: " + UPPAALPropertyNotVerifMessage + "\n");

        /*
         * sb.append("AVATARCPPSIMCompileCommand: " + AVATARCPPSIMCompileCommand +
         * "\n"); sb.append("AVATARCPPSIMCodeExecuteCommand: " +
         * AVATARCPPSIMCodeExecuteCommand + "\n");
         * sb.append("AVATARCPPSIMInteractiveExecuteCommand: " +
         * AVATARCPPSIMInteractiveExecuteCommand + "\n");
         * 
         * // AVATAR: simulation sb.append("\nAVATAR (simulation):\n");
         * sb.append("AVATARSimulationHost: " + AVATARSimulationHost + "\n");
         * sb.append("AVATARCPPSIMCodeDirectory: " + AVATARCPPSIMCodeDirectory + "\n");
         */

        // Issue #35: Moved with other UPPAAL properties
        // sb.append("UPPAALVerifierHOST: " + UPPAALVerifierHost + "\n");

        // AVATAR: executable code
        sb.append("\nAVATAR (executable code):\n");
        sb.append("AVATARExecutableCodeDirectory: " + AVATARExecutableCodeDirectory + "\n");
        sb.append("AVATARMPSoCCodeDirectory: " + AVATARMPSoCCodeDirectory + "\n");
        sb.append("AVATARMPSoCCompileCommand: " + AVATARMPSoCCompileCommand + "\n");
        sb.append("AVATARExecutableCodeHost: " + AVATARExecutableCodeHost + "\n");
        sb.append("AVATARExecutableCodeCompileCommand: " + AVATARExecutableCodeCompileCommand + "\n");
        sb.append("AVATARExecutableCodeExecuteCommand: " + AVATARExecutableCodeExecuteCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeCompileCommand: " + AVATARExecutableSoclibCodeCompileCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeExecuteCommand: " + AVATARExecutableSoclibCodeExecuteCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeTraceCommand: " + AVATARExecutableSoclibCodeTraceCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeTraceFile: " + AVATARExecutableSoclibTraceFile + "\n");

        sb.append("\nZ3 Libs:\n");
        sb.append("Z3LIBS: " + Z3LIBS + "\n");

        sb.append("\nProVerif:\n");
        sb.append("ProVerifCodeDirectory: " + ProVerifCodeDirectory + "\n");
        sb.append("ProVerifVerifierPATH: " + ProVerifVerifierPath + "\n");
        sb.append("ProVerifVerifierHOST: " + ProVerifVerifierHost + "\n");

        sb.append("\nYour files (modeling, librairies, etc.):\n");
        sb.append("FILEPath: " + FILEPath + "\n");
        sb.append("DownloadedFILEPath: " + DownloadedFILEPath + "\n");
        sb.append("LOTOSPath: " + LOTOSPath + "\n");
        sb.append("LIBPath: " + LIBPath + "\n");
        sb.append("IMGPath: " + IMGPath + "\n");
        sb.append("DocGenPath: " + DocGenPath + "\n");
        sb.append("GGraphPath: " + GGraphPath + "\n");
        sb.append("TGraphPath: " + TGraphPath + "\n");
        sb.append("\nTTool update:\n");
        sb.append("TToolUpdateURL: " + TToolUpdateURL + "\n");
        sb.append("TToolUpdateProxy: " + TToolUpdateProxy + "\n");
        sb.append("TToolUpdateProxyPort: " + TToolUpdateProxyPort + "\n");
        sb.append("TToolUpdateProxyHost: " + TToolUpdateProxyHost + "\n");
        sb.append("\nJava prototyping:\n");
        sb.append("JavaCodeDirectory: " + JavaCodeDirectory + "\n");
        sb.append("JavaHeader: " + JavaHeader + "\n");
        sb.append("JavaCompilerPath: " + JavaCompilerPath + "\n");
        sb.append("TToolClassPath: " + TToolClassPath + "\n");
        sb.append("JavaExecutePath: " + JavaExecutePath + "\n");
        sb.append("SimuJavaCodeDirectory: " + SimuJavaCodeDirectory + "\n");
        sb.append("TToolSimuClassPath: " + TToolSimuClassPath + "\n");

        sb.append("\nDIPLODOCUS:\n");
        if (systemcOn) {
            sb.append("SystemCCodeDirectory: " + SystemCCodeDirectory + "\n");
            sb.append("SystemCHost: " + SystemCHost + "\n");
            sb.append("SystemCCodeCompileCommand: " + SystemCCodeCompileCommand + "\n");
            sb.append("SystemCCodeExecuteCommand: " + SystemCCodeExecuteCommand + "\n");
            sb.append("SystemCCodeExecuteXCycle: " + SystemCCodeExecuteXCycle + "\n");
            sb.append("SystemCCodeInteractiveExecuteCommand: " + SystemCCodeInteractiveExecuteCommand + "\n");
            sb.append("GTKWavePath: " + GTKWavePath + "\n");
            // TML
            sb.append("TMLCodeDirectory: " + TMLCodeDirectory + "\n");

            // Application C code
            sb.append("CCodeDirectory: " + CCodeDirectory + "\n");
        }

        // VCD
        sb.append("VCDPath: " + VCDPath + "\n");

        // NC
        sb.append("\nNetwork calculus:\n");
        sb.append("NCDirectory: " + NCDirectory + "\n");

        // Ontology
        /*
         * sb.append("\nOntologies:\n"); sb.append("Requirement ontology website: " +
         * RequirementOntologyWebsite + "\n"); sb.append("Attack ontology website: " +
         * AttackOntologyWebsite + "\n");
         */

        // Plugins
        sb.append("\nPlugins:\n");
        // sb.append("Plugin path: " + PLUGIN_PKG + "\n");
        sb.append("Plugin path: " + PLUGIN_PATH + "\n");
        /*
         * sb.append("Plugin for java code generation: " + PLUGIN_JAVA_CODE_GENERATOR +
         * "\n"); for (int i=0; i<PLUGIN_GRAPHICAL_COMPONENT.length; i++) {
         * sb.append("Plugin for graphical component: " + PLUGIN_GRAPHICAL_COMPONENT[i]
         * + "\n"); }
         */
        for (int i = 0; i < PLUGIN.length; i++) {
            sb.append("Plugin: " + PLUGIN[i] + " package:" + PLUGIN_PKG[i] + "\n");
        }

        // URL
        sb.append("\nURLs:\n");
        sb.append("URL for loading models from network: " + URL_MODEL + "\n");

        sb.append("\nCustom external commands:\n");
        sb.append("ExternalCommand1Host: " + ExternalCommand1Host + "\n");
        sb.append("ExternalCommand1: " + ExternalCommand1 + "\n");
        sb.append("ExternalCommand2Host: " + ExternalCommand2Host + "\n");
        sb.append("ExternalCommand2: " + ExternalCommand2 + "\n");

        sb.append("\nInformation saved by TTool:\n");

        if (LastOpenFiles != null) {
            for (int i = 0; i < LastOpenFiles.length; i++) {
                if (LastOpenFiles[i] != null) {
                    sb.append("LastOpenFile #" + i + ": " + LastOpenFiles[i] + "\n");
                }
            }
        }
        sb.append("LastWindowAttributesX: " + LastWindowAttributesX + "\n");
        sb.append("LastWindowAttributesY: " + LastWindowAttributesY + "\n");
        sb.append("LastWindowAttributesWidth: ").append(LastWindowAttributesWidth).append("\n");
        sb.append("LastWindowAttributesHeight: ").append(LastWindowAttributesHeight).append("\n");
        sb.append("LastWindowAttributesMax: ").append(LastWindowAttributesMax).append("\n");

        sb.append("\nRG stylesheet configuration:").append(RGStyleSheet).append("\n");

        // Huy Truong
        sb.append("ExternalServer ").append(ExternalServer).append("\n");

        return sb.toString();

    }

    public static void loadConfigurationFromXML(String data, boolean systemcOn) throws MalformedConfigurationException {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;

            nl = doc.getElementsByTagName("LauncherPort");
            if (nl.getLength() > 0)
                LauncherPort(nl);
            nl = doc.getElementsByTagName("RTLHost");
            if (nl.getLength() > 0)
                RTLHOST(nl);
            nl = doc.getElementsByTagName("RTLPath");
            if (nl.getLength() > 0)
                RTLPath(nl);
            nl = doc.getElementsByTagName("DTA2DOTPath");
            if (nl.getLength() > 0)
                DTA2DOTPath(nl);
            nl = doc.getElementsByTagName("RG2TLSAPath");
            if (nl.getLength() > 0)
                RG2TLSAPath(nl);
            nl = doc.getElementsByTagName("RGSTRAPPath");
            if (nl.getLength() > 0)
                RGSTRAPPath(nl);
            nl = doc.getElementsByTagName("AldebaranPath");
            if (nl.getLength() > 0)
                AldebaranPath(nl);
            nl = doc.getElementsByTagName("AldebaranHost");
            if (nl.getLength() > 0)
                AldebaranHost(nl);
            nl = doc.getElementsByTagName("BcgioPath");
            if (nl.getLength() > 0)
                BcgioPath(nl);
            nl = doc.getElementsByTagName("BcgmergePath");
            if (nl.getLength() > 0)
                BcgmergePath(nl);
            nl = doc.getElementsByTagName("BcgminPath");
            if (nl.getLength() > 0)
                BcgminPath(nl);
            nl = doc.getElementsByTagName("BisimulatorPath");
            if (nl.getLength() > 0)
                BisimulatorPath(nl);
            nl = doc.getElementsByTagName("CaesarPath");
            if (nl.getLength() > 0)
                CaesarPath(nl);
            nl = doc.getElementsByTagName("CaesarOpenPath");
            if (nl.getLength() > 0)
                CaesarOpenPath(nl);
            nl = doc.getElementsByTagName("DOTTYHost");
            if (nl.getLength() > 0)
                DOTTYHost(nl);
            nl = doc.getElementsByTagName("DOTTYPath");
            if (nl.getLength() > 0)
                DOTTYPath(nl);
            nl = doc.getElementsByTagName("FILEPath");
            if (nl.getLength() > 0)
                FILEPath(nl);
            nl = doc.getElementsByTagName("DownloadedFILEPath");
            if (nl.getLength() > 0)
                DownloadedFILEPath(nl);
            nl = doc.getElementsByTagName("LOTOSPath");
            if (nl.getLength() > 0)
                LOTOSPath(nl);
            nl = doc.getElementsByTagName("LIBPath");
            if (nl.getLength() > 0)
                LIBPath(nl);
            nl = doc.getElementsByTagName("IMGPath");
            if (nl.getLength() > 0)
                IMGPath(nl);
            nl = doc.getElementsByTagName("DocGenPath");
            if (nl.getLength() > 0)
                DocGenPath(nl);
            nl = doc.getElementsByTagName("GGraphPath");
            if (nl.getLength() > 0)
                GGraphPath(nl);
            nl = doc.getElementsByTagName("TGraphPath");
            if (nl.getLength() > 0)
                TGraphPath(nl);
            nl = doc.getElementsByTagName("TToolUpdateURL");
            if (nl.getLength() > 0)
                TToolUpdateURL(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxy");
            if (nl.getLength() > 0)
                TToolUpdateProxy(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxyPort");
            if (nl.getLength() > 0)
                TToolUpdateProxyPort(nl);
            nl = doc.getElementsByTagName("TToolUpdateProxyHost");
            if (nl.getLength() > 0)
                TToolUpdateProxyHost(nl);
            nl = doc.getElementsByTagName("JavaCodeDirectory");
            if (nl.getLength() > 0)
                JavaCodeDirectory(nl);
            nl = doc.getElementsByTagName("JavaHeader");
            if (nl.getLength() > 0)
                JavaHeader(nl);
            nl = doc.getElementsByTagName("JavaCompilerPath");
            if (nl.getLength() > 0)
                JavaCompilerPath(nl);
            nl = doc.getElementsByTagName("TToolClassPath");
            if (nl.getLength() > 0)
                TToolClassPath(nl);
            nl = doc.getElementsByTagName("SimuJavaCodeDirectory");
            if (nl.getLength() > 0)
                SimuJavaCodeDirectory(nl);
            nl = doc.getElementsByTagName("TToolSimuClassPath");
            if (nl.getLength() > 0)
                TToolSimuClassPath(nl);
            nl = doc.getElementsByTagName("JavaExecutePath");
            if (nl.getLength() > 0)
                JavaExecutePath(nl);
            nl = doc.getElementsByTagName("NCDirectory");
            if (nl.getLength() > 0)
                NCDirectory(nl);
            nl = doc.getElementsByTagName("SystemCCodeDirectory");
            if (nl.getLength() > 0)
                SystemCCodeDirectory(nl);

            // AVATAR Simulation
            /*
             * nl = doc.getElementsByTagName("AVATARSimulationHost"); if (nl.getLength() >
             * 0) AVATARSimulationHost(nl); nl =
             * doc.getElementsByTagName("AVATARCPPSIMCodeDirectory"); if (nl.getLength() >
             * 0) AVATARCPPSIMCodeDirectory(nl); nl =
             * doc.getElementsByTagName("AVATARCPPSIMCompileCommand"); if (nl.getLength() >
             * 0) AVATARCPPSIMCompileCommand(nl); nl =
             * doc.getElementsByTagName("AVATARCPPSIMCodeExecuteCommand"); if
             * (nl.getLength() > 0) AVATARCPPSIMCodeExecuteCommand(nl); nl =
             * doc.getElementsByTagName("AVATARCPPSIMInteractiveExecuteCommand"); if
             * (nl.getLength() > 0) AVATARCPPSIMInteractiveExecuteCommand(nl);
             */

            // AVATAR Executable code
            nl = doc.getElementsByTagName("AVATARExecutableCodeDirectory");
            if (nl.getLength() > 0)
                AVATARExecutableCodeDirectory(nl);
            nl = doc.getElementsByTagName("AVATARMPSoCCodeDirectory");
            if (nl.getLength() > 0)
                AVATARMPSoCCodeDirectory(nl);
            nl = doc.getElementsByTagName("AVATARMPSoCCompileCommand");
            if (nl.getLength() > 0)
                AVATARMPSoCCompileCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableCodeHost");
            if (nl.getLength() > 0)
                AVATARExecutableCodeHost(nl);
            nl = doc.getElementsByTagName("AVATARExecutableCodeCompileCommand");
            if (nl.getLength() > 0)
                AVATARExecutableCodeCompileCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableCodeExecuteCommand");
            if (nl.getLength() > 0)
                AVATARExecutableCodeExecuteCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableSoclibCodeCompileCommand");
            if (nl.getLength() > 0)
                AVATARExecutableSoclibCodeCompileCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableSoclibCodeExecuteCommand");
            if (nl.getLength() > 0)
                AVATARExecutableSoclibCodeExecuteCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableSoclibCodeTraceCommand");
            if (nl.getLength() > 0)
                AVATARExecutableSoclibCodeTraceCommand(nl);
            nl = doc.getElementsByTagName("AVATARExecutableSoclibTraceFile");
            if (nl.getLength() > 0)
                AVATARExecutableSoclibTraceFile(nl);

            nl = doc.getElementsByTagName("Z3LIBS");
            if (nl.getLength() > 0)
                Z3LIBS(nl);

            if (systemcOn) {
                nl = doc.getElementsByTagName("SystemCHost");
                if (nl.getLength() > 0)
                    SystemCHost(nl);
                nl = doc.getElementsByTagName("SystemCCodeCompileCommand");
                if (nl.getLength() > 0)
                    SystemCCodeCompileCommand(nl);
                nl = doc.getElementsByTagName("SystemCCodeExecuteCommand");
                if (nl.getLength() > 0)
                    SystemCCodeExecuteCommand(nl);
                nl = doc.getElementsByTagName("SystemCCodeExecuteXCycle");
                if (nl.getLength() > 0)
                    SystemCCodeExecuteXCycle(nl);
                nl = doc.getElementsByTagName("SystemCCodeInteractiveExecuteCommand");
                if (nl.getLength() > 0)
                    SystemCCodeInteractiveExecuteCommand(nl);
                nl = doc.getElementsByTagName("GTKWavePath");
                if (nl.getLength() > 0)
                    GTKWavePath(nl);
            }

            nl = doc.getElementsByTagName("TMLCodeDirectory");
            if (nl.getLength() > 0)
                TMLCodeDirectory(nl);

            nl = doc.getElementsByTagName("CCodeDirectory");
            if (nl.getLength() > 0)
                CCodeDirectory(nl);

            nl = doc.getElementsByTagName("VCDPath");
            if (nl.getLength() > 0)
                VCDPath(nl);

            nl = doc.getElementsByTagName("UPPAALCodeDirectory");
            if (nl.getLength() > 0)
                UPPAALCodeDirectory(nl);

            nl = doc.getElementsByTagName("UPPAALVerifierPath");
            if (nl.getLength() > 0)
                UPPAALVerifierPath(nl);

            nl = doc.getElementsByTagName("UPPAALVerifierHost");
            if (nl.getLength() > 0)
                UPPAALVerifierHost(nl);

            nl = doc.getElementsByTagName("UPPAALPropertyVerifMessage");
            if (nl.getLength() > 0) {
                UPPAALPropertyVerifMessage(nl);
            }

            nl = doc.getElementsByTagName("UPPAALPropertyNotVerifMessage");
            if (nl.getLength() > 0) {
                UPPAALPropertyNotVerifMessage(nl);
            }

            nl = doc.getElementsByTagName("ProVerifCodeDirectory");
            if (nl.getLength() > 0)
                ProVerifCodeDirectory(nl);

            nl = doc.getElementsByTagName("ProVerifHash");
            if (nl.getLength() > 0)
                ProVerifHash(nl);

            nl = doc.getElementsByTagName("ProVerifVerifierPath");
            if (nl.getLength() > 0)
                ProVerifVerifierPath(nl);

            nl = doc.getElementsByTagName("ProVerifVerifierHost");
            if (nl.getLength() > 0)
                ProVerifVerifierHost(nl);

            // Ontologies
            /*
             * nl = doc.getElementsByTagName("RequirementOntologyWebsite"); if
             * (nl.getLength() > 0) RequirementOntologyWebsite(nl);
             * 
             * nl = doc.getElementsByTagName("AttackOntologyWebsite"); if (nl.getLength() >
             * 0) AttackOntologyWebsite(nl);
             */

            nl = doc.getElementsByTagName("ExternalCommand1Host");
            if (nl.getLength() > 0)
                ExternalCommand1Host(nl);

            nl = doc.getElementsByTagName("ExternalCommand1");
            if (nl.getLength() > 0)
                ExternalCommand1(nl);

            nl = doc.getElementsByTagName("ExternalCommand2Host");
            if (nl.getLength() > 0)
                ExternalCommand2Host(nl);

            nl = doc.getElementsByTagName("ExternalCommand2");
            if (nl.getLength() > 0)
                ExternalCommand2(nl);

            nl = doc.getElementsByTagName("PLUGIN_PATH");
            if (nl.getLength() > 0)
                PluginPath(nl);

            nl = doc.getElementsByTagName("PLUGIN");
            if (nl.getLength() > 0)
                Plugin(nl);

            /*
             * nl = doc.getElementsByTagName("PLUGIN_JAVA_CODE_GENERATOR"); if
             * (nl.getLength() > 0) PluginJavaCodeGenerator(nl);
             * 
             * nl = doc.getElementsByTagName("PLUGIN_GRAPHICAL_COMPONENT"); if
             * (nl.getLength() > 0) PluginGraphicalComponent(nl);
             */

            nl = doc.getElementsByTagName("URL_MODEL");
            if (nl.getLength() > 0)
                URLModel(nl);

            for (int i = 0; i < NB_LAST_OPEN_FILE; i++) {
                LastOpenFiles[i] = "";
            }
            nl = doc.getElementsByTagName("LastOpenFile");
            if (nl.getLength() > 0)
                LastOpenFile(nl);

            nl = doc.getElementsByTagName("LastWindowAttributes");
            if (nl.getLength() > 0)
                LastWindowAttributes(nl);

            nl = doc.getElementsByTagName("RGStyleSheet");
            if (nl.getLength() > 0)
                RGStyleSheet(nl);

            nl = doc.getElementsByTagName("ExternalServer");
            if (nl.getLength() > 0)
                ExternalServer(nl);

        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LauncherPort(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            LauncherPort = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void RTLHOST(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            RTLHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void RTLPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            RTLPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void DTA2DOTPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            DTA2DOTPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void RG2TLSAPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            RG2TLSAPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void RGSTRAPPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            RGSTRAPPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AldebaranPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AldebaranPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AldebaranHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AldebaranHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void BcgioPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            BcgioPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void BcgmergePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            BcgmergePath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void BcgminPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            BcgminPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void BisimulatorPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            BisimulatorPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void CaesarPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            CaesarPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void CaesarOpenPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            CaesarOpenPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void DOTTYHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            DOTTYHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void DOTTYPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            DOTTYPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void FILEPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            FILEPath = elt.getAttribute("data");
            if (DownloadedFILEPath.isEmpty())
                DownloadedFILEPath = FILEPath;
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void DownloadedFILEPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            DownloadedFILEPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LOTOSPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            LOTOSPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LIBPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            LIBPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void IMGPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            IMGPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void DocGenPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            DocGenPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void GGraphPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            GGraphPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TGraphPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TGraphPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolUpdateURL(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolUpdateURL = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolUpdateProxy(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolUpdateProxy = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolUpdateProxyPort(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolUpdateProxyPort = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolUpdateProxyHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolUpdateProxyHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void JavaCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            JavaCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void JavaHeader(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            JavaHeader = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void JavaCompilerPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            JavaCompilerPath = elt.getAttribute("data");
            if (JavaCompilerPath.startsWith("[")) {
                JavaCompilerPath = "\"" + JavaCompilerPath.substring(1, JavaCompilerPath.length()) + "\"";
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolClassPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolClassPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SimuJavaCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SimuJavaCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TToolSimuClassPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TToolSimuClassPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void JavaExecutePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            JavaExecutePath = elt.getAttribute("data");
            if (JavaExecutePath.startsWith("[")) {
                JavaExecutePath = "\"" + JavaExecutePath.substring(1, JavaExecutePath.length()) + "\"";
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void NCDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            NCDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCCodeCompileCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCCodeCompileCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCCodeExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCCodeExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCCodeExecuteXCycle(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCCodeExecuteXCycle = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void SystemCCodeInteractiveExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            SystemCCodeInteractiveExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void GTKWavePath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            GTKWavePath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void TMLCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            TMLCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void CCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            CCodeDirectory = elt.getAttribute("data") + "/";
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void VCDPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            VCDPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void UPPAALCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            UPPAALCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void UPPAALVerifierPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            UPPAALVerifierPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void UPPAALVerifierHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            UPPAALVerifierHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void UPPAALPropertyVerifMessage(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            UPPAALPropertyVerifMessage = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void UPPAALPropertyNotVerifMessage(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            UPPAALPropertyNotVerifMessage = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    /*
     * private static void AVATARSimulationHost(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AVATARSimulationHost = elt.getAttribute("data"); } catch (Exception e) {
     * throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void AVATARCPPSIMCodeDirectory(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AVATARCPPSIMCodeDirectory = elt.getAttribute("data"); } catch (Exception e) {
     * throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void AVATARCPPSIMCompileCommand(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AVATARCPPSIMCompileCommand = elt.getAttribute("data"); } catch (Exception e)
     * { throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void AVATARCPPSIMCodeExecuteCommand(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AVATARCPPSIMCodeExecuteCommand = elt.getAttribute("data"); } catch (Exception
     * e) { throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void AVATARCPPSIMInteractiveExecuteCommand(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AVATARCPPSIMInteractiveExecuteCommand = elt.getAttribute("data"); } catch
     * (Exception e) { throw new MalformedConfigurationException(e.getMessage()); }
     * }
     */

    private static void AVATARExecutableCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARMPSoCCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARMPSoCCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARMPSoCCompileCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARMPSoCCompileCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableCodeHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableCodeHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableCodeCompileCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableCodeCompileCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableCodeExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableCodeExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableSoclibCodeCompileCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableSoclibCodeCompileCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableSoclibCodeExecuteCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableSoclibCodeExecuteCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableSoclibCodeTraceCommand(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableSoclibCodeTraceCommand = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void AVATARExecutableSoclibTraceFile(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            AVATARExecutableSoclibTraceFile = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void Z3LIBS(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            Z3LIBS = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ProVerifCodeDirectory(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ProVerifCodeDirectory = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ProVerifHash(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ProVerifHash = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ProVerifVerifierPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ProVerifVerifierPath = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ProVerifVerifierHost(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ProVerifVerifierHost = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    /*
     * private static void RequirementOntologyWebsite(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * RequirementOntologyWebsite = elt.getAttribute("data"); } catch (Exception e)
     * { throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void AttackOntologyWebsite(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * AttackOntologyWebsite = elt.getAttribute("data"); } catch (Exception e) {
     * throw new MalformedConfigurationException(e.getMessage()); } }
     */

    private static void ExternalCommand1Host(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ExternalCommand1Host = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ExternalCommand1(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ExternalCommand1 = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ExternalCommand2Host(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ExternalCommand2Host = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ExternalCommand2(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ExternalCommand2 = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void PluginPath(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            PLUGIN_PATH = elt.getAttribute("data");
            PluginManager.PLUGIN_PATH = PLUGIN_PATH;
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void Plugin(NodeList nl) throws MalformedConfigurationException {
        PLUGIN = new String[nl.getLength()];
        PLUGIN_PKG = new String[nl.getLength()];
        try {
            for (int i = 0; i < nl.getLength(); i++) {
                Element elt = (Element) (nl.item(i));
                PLUGIN[i] = elt.getAttribute("file");
                PLUGIN_PKG[i] = elt.getAttribute("package");
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    /*
     * private static void PluginJavaCodeGenerator(NodeList nl) throws
     * MalformedConfigurationException { try { Element elt = (Element)(nl.item(0));
     * PLUGIN_JAVA_CODE_GENERATOR = elt.getAttribute("data"); } catch (Exception e)
     * { throw new MalformedConfigurationException(e.getMessage()); } }
     * 
     * private static void PluginGraphicalComponent(NodeList nl) throws
     * MalformedConfigurationException { PLUGIN_GRAPHICAL_COMPONENT = new
     * String[nl.getLength()]; try { for (int i=0; i<nl.getLength(); i++) { Element
     * elt = (Element)(nl.item(i)); PLUGIN_GRAPHICAL_COMPONENT[i] =
     * elt.getAttribute("data"); } } catch (Exception e) { throw new
     * MalformedConfigurationException(e.getMessage()); } }
     */

    private static void URLModel(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            URL_MODEL = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LastOpenFile(NodeList nl) throws MalformedConfigurationException {
        try {
            for (int i = 0; i < Math.min(nl.getLength(), NB_LAST_OPEN_FILE); i++) {
                Element elt = (Element) (nl.item(i));
                if (i == 0) {
                    LastOpenFile = elt.getAttribute("data");
                    LastOpenFileDefined = true;
                }
                LastOpenFiles[i] = elt.getAttribute("data");
            }
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LastWindowAttributes(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            LastWindowAttributesX = elt.getAttribute("x");
            LastWindowAttributesY = elt.getAttribute("y");
            LastWindowAttributesWidth = elt.getAttribute("width");
            LastWindowAttributesHeight = elt.getAttribute("height");
            LastWindowAttributesMax = elt.getAttribute("max");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void RGStyleSheet(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            RGStyleSheet = elt.getAttribute("data");
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void ExternalServer(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            ExternalServer = elt.getAttribute("data");
            if (ExternalServer == "")
                ExternalServer = "localhost:9999";
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    public static boolean isConfigured(String s) {
        return ((s != null) && (s.trim().length() > 0));
    }

    public static void decLastFiles() {
        String[] tmp = new String[NB_LAST_OPEN_FILE];
        String[] tmp1 = new String[NB_LAST_OPEN_FILE];
        for (int i = 0; i < NB_LAST_OPEN_FILE; i++) {
            tmp[i] = LastOpenFiles[i];
        }

        for (int j = 0; j < NB_LAST_OPEN_FILE; j++) {
            tmp1[j] = "";
            LastOpenFiles[j] = "";
        }

        int cpt = 0;
        for (int k = 0; k < NB_LAST_OPEN_FILE; k++) {
            if (tmp[k].length() > 0) {
                tmp1[cpt] = tmp[k];
                cpt++;
            }
        }

        for (int l = NB_LAST_OPEN_FILE - 1; l > 0; l--) {
            LastOpenFiles[l] = tmp1[l - 1];
        }
    }

    // Returns an error string in case of failure
    public static String loadZ3Libs() {
        if ((ConfigurationTTool.Z3LIBS == null) || (ConfigurationTTool.Z3LIBS.length() == 0)) {
            return "Z3 libraries not configured.\n Set them in configuration file (e.g. config.xml)\n"
                    + "For instance:\n<Z3LIBS data=\"/opt/z3/bin/libz3.so:/opt/z3/bin/libz3java.so\" />\n";
        }

        try {
            TraceManager.addDev("Z3. CONFIG TTOOL. Loading Z3 libs");

            String[] libs = ConfigurationTTool.Z3LIBS.split(":");
            boolean setLibPath = false;

            for (int i = 0; i < libs.length; i++) {
                // get the path and set it as a property of java lib path

                String tmp = libs[i].trim();
                TraceManager.addDev("Z3. Working with lib:" + tmp);
                if (tmp.length() > 0) {
                    if (setLibPath == false) {
                        File f = new File(tmp);
                        String dir = f.getParent();
                        // TraceManager.addDev("Old library path: " +
                        // System.getProperty("java.library.path"));
                        // TraceManager.addDev("Setting java library path to " + dir);
                        // System.setProperty("java.library.path", ".:" + dir);
                        // addToJavaLibraryPath(new File(dir));
                        TraceManager.addDev("Z3. New library path: " + System.getProperty("java.library.path"));
                        setLibPath = true;
                    }
                    TraceManager.addDev("Z3. Loading Z3 lib: " + tmp);
                    System.load(tmp);
                    TraceManager.addDev("Z3. Loaded Z3 lib: " + tmp);
                }
            }

        } catch (UnsatisfiedLinkError e) {
            return ("Z3. UnsatisfiedLinkError/ Z3 libs " + ConfigurationTTool.Z3LIBS + " could not be loaded. "
                    + e.getMessage() + "\n");
        } catch (IllegalArgumentException iae) {
            return ("Z3. IllegalArgumentException/ Z3 libs " + ConfigurationTTool.Z3LIBS + " could not be used\n");
        } catch (SecurityException se) {
            return ("Z3. SecurityException/ Z3 libs " + ConfigurationTTool.Z3LIBS + " could not be used:"
                    + se.getMessage());
        }

        return null;
    }

    /**
     * Adding a new dir to java.library.path.
     * 
     * @param dir The new directory
     */
    public static void addToJavaLibraryPath(File dir) {
        TraceManager.addDev("Adding to lib path: " + dir.getAbsolutePath());
        final String LIBRARY_PATH = "java.library.path";
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " is not a directory.");
        }
        String javaLibraryPath = System.getProperty(LIBRARY_PATH);
        System.setProperty(LIBRARY_PATH, javaLibraryPath + File.pathSeparatorChar + dir.getAbsolutePath());

        resetJavaLibraryPath();
    }

    /**
     * Deletes "java.library.path" cache
     */
    public static void resetJavaLibraryPath() {
        synchronized (Runtime.getRuntime()) {
            try {
                Field field = ClassLoader.class.getDeclaredField("usr_paths");
                field.setAccessible(true);
                field.set(null, null);

                field = ClassLoader.class.getDeclaredField("sys_paths");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

} //

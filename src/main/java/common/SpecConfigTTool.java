
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

import myutil.FileException;
import myutil.FileUtils;
import myutil.MalformedConfigurationException;
import myutil.TraceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Specific configuration for ttool project Creation: 13/10/2017
 *
 * @author Fabien Tessier
 * @version 1.0
 */
public class SpecConfigTTool {

    public static String DEFAULT_CONFIG = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n\n<PROJECTCONFIGURATION>\n<LastVCD data=\"\"/>\n<LastOpenDiagram tab=\"0\" panel=\"0\"/>\n</PROJECTCONFIGURATION>\n";

    public static String SystemCCodeDirectory = "";
    public static String SystemCCodeCompileCommand = "";
    public static String SystemCCodeExecuteCommand = "";
    public static String SystemCCodeExecuteXCycle = "";
    public static String SystemCCodeInteractiveExecuteCommand = "";

    public static String CCodeDirectory = "";

    public static String ProVerifCodeDirectory = "";

    public static String AVATARExecutableCodeDirectory = "";
    public static String AVATARExecutableCodeCompileCommand = "";
    public static String AVATARExecutableCodeExecuteCommand = "";

    public static String AVATARMPSoCCodeDirectory = "";
    public static String AVATARMPSoCCompileCommand = "";
    public static String AVATARExecutableSoclibCodeCompileCommand = "";
    public static String AVATARExecutableSoclibCodeExecuteCommand = "";
    public static String AVATARExecutableSoclibCodeTraceCommand = "";

    public static String TMLCodeDirectory = "";

    public static String IMGPath = "";

    public static String DocGenPath = "";

    public static String GGraphPath = "";
    public static String TGraphPath = "";

    public static String UPPAALCodeDirectory = "";

    public static String VCDPath = "";
    public static String ExternalCommand1 = "";

    public static String NCDirectory = "";

    private static String ProjectSystemCCodeDirectory = "/c++_code/";
    private static String ProjectCCodeDirectory = "/c_code/";
    private static String ProjectProVerifCodeDirectory = "/proverif/";
    private static String ProjectAVATARExecutableCodeDirectory = "/AVATAR_executablecode/";
    private static String ProjectAVATARMPSoCCodeDirectory = "/MPSoC/";
    private static String ProjectTMLCodeDirectory = "/tmlcode/";
    private static String ProjectIMGDirectory = "/figures";
    private static String ProjectDocGenDirectory = "/doc";
    private static String ProjectGGraphDirectory = "/graphs";
    private static String ProjectTGraphDirectory = "/graphs";
    private static String ProjectUPPAALCodeDirectory = "/uppaal/";
    private static String ProjectVCDDirectory = "/c++_code/";
    private static String ProjectNCDirectory = "/nc/";

    public static int lastPanel = -1;
    public static int lastTab = -1;
    public static String lastVCD = "";

    private static String basicConfigPath = "";

    public static void loadConfiguration() {
        SystemCCodeDirectory = ConfigurationTTool.SystemCCodeDirectory;
        SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand;
        SystemCCodeExecuteCommand = ConfigurationTTool.SystemCCodeExecuteCommand;
        SystemCCodeExecuteXCycle = ConfigurationTTool.SystemCCodeExecuteXCycle;
        SystemCCodeInteractiveExecuteCommand = ConfigurationTTool.SystemCCodeInteractiveExecuteCommand;

        CCodeDirectory = ConfigurationTTool.CCodeDirectory;

        ProVerifCodeDirectory = ConfigurationTTool.ProVerifCodeDirectory;

        AVATARExecutableCodeDirectory = ConfigurationTTool.AVATARExecutableCodeDirectory;
        AVATARExecutableCodeCompileCommand = ConfigurationTTool.AVATARExecutableCodeCompileCommand;
        AVATARExecutableCodeExecuteCommand = ConfigurationTTool.AVATARExecutableCodeExecuteCommand;

        AVATARMPSoCCodeDirectory = ConfigurationTTool.AVATARMPSoCCodeDirectory;
        AVATARMPSoCCompileCommand = ConfigurationTTool.AVATARMPSoCCompileCommand;
        AVATARExecutableSoclibCodeCompileCommand = ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand;
        AVATARExecutableSoclibCodeExecuteCommand = ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand;
        AVATARExecutableSoclibCodeTraceCommand = ConfigurationTTool.AVATARExecutableSoclibCodeTraceCommand;

        TMLCodeDirectory = ConfigurationTTool.TMLCodeDirectory;

        IMGPath = ConfigurationTTool.IMGPath;

        DocGenPath = ConfigurationTTool.DocGenPath;

        GGraphPath = ConfigurationTTool.GGraphPath;
        TGraphPath = ConfigurationTTool.TGraphPath;

        UPPAALCodeDirectory = ConfigurationTTool.UPPAALCodeDirectory;

        VCDPath = ConfigurationTTool.VCDPath;
        ExternalCommand1 = ConfigurationTTool.ExternalCommand1;

        NCDirectory = ConfigurationTTool.NCDirectory;
    }

    public static void setDirConfig(File dir) {
        SystemCCodeDirectory = dir.getAbsolutePath() + ProjectSystemCCodeDirectory;
        CCodeDirectory = dir.getAbsolutePath() + ProjectCCodeDirectory;
        ProVerifCodeDirectory = dir.getAbsolutePath() + ProjectProVerifCodeDirectory;
        AVATARExecutableCodeDirectory = dir.getAbsolutePath() + ProjectAVATARExecutableCodeDirectory;
        AVATARMPSoCCodeDirectory = dir.getAbsolutePath() + ProjectAVATARMPSoCCodeDirectory;
        TMLCodeDirectory = dir.getAbsolutePath() + ProjectTMLCodeDirectory;
        IMGPath = dir.getAbsolutePath() + ProjectIMGDirectory;
        DocGenPath = dir.getAbsolutePath() + ProjectDocGenDirectory;
        GGraphPath = dir.getAbsolutePath() + ProjectGGraphDirectory;
        TGraphPath = dir.getAbsolutePath() + ProjectTGraphDirectory;
        UPPAALCodeDirectory = dir.getAbsolutePath() + ProjectUPPAALCodeDirectory;
        VCDPath = dir.getAbsolutePath() + ProjectVCDDirectory;
        NCDirectory = dir.getAbsolutePath() + ProjectNCDirectory;

        // TraceManager.addDev("Before replace SystemCCodeCompileCommand:" +
        // SystemCCodeCompileCommand + " with " +
        // ConfigurationTTool.SystemCCodeDirectory +
        // " to " + SystemCCodeDirectory);

        SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand
                .replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
        // TraceManager.addDev("After replace SystemCCodeCompileCommand:" +
        // SystemCCodeCompileCommand);
        SystemCCodeExecuteCommand = ConfigurationTTool.SystemCCodeExecuteCommand
                .replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
        SystemCCodeExecuteXCycle = ConfigurationTTool.SystemCCodeExecuteXCycle
                .replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);

        SystemCCodeInteractiveExecuteCommand = ConfigurationTTool.SystemCCodeInteractiveExecuteCommand
                .replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);

        AVATARExecutableCodeExecuteCommand = ConfigurationTTool.AVATARExecutableCodeExecuteCommand
                .replace(ConfigurationTTool.AVATARExecutableCodeDirectory, AVATARExecutableCodeDirectory);
        AVATARExecutableCodeCompileCommand = ConfigurationTTool.AVATARExecutableCodeCompileCommand
                .replace(ConfigurationTTool.AVATARExecutableCodeDirectory, AVATARExecutableCodeDirectory);

        AVATARMPSoCCompileCommand = ConfigurationTTool.AVATARMPSoCCompileCommand
                .replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
        AVATARExecutableSoclibCodeCompileCommand = ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand
                .replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
        AVATARExecutableSoclibCodeExecuteCommand = ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand
                .replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
        AVATARExecutableSoclibCodeTraceCommand = ConfigurationTTool.AVATARExecutableSoclibCodeTraceCommand
                .replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
        // ExternalCommand1 =
        // ConfigurationTTool.ExternalCommand1.replace(ConfigurationTTool.VCDPath,
        // SpecConfigTTool.VCDPath);
    }

    public static void setBasicConfig(boolean systemcOn) {
        try {
            ConfigurationTTool.loadConfiguration(basicConfigPath, systemcOn);
        } catch (MalformedConfigurationException e) {
            System.out.println("Couldn't load configuration from file: config.xml");
        }
    }

    public static File createProjectConfig(File dir) {
        File figures = new File(IMGPath);
        figures.mkdir();

        File GGraph = new File(GGraphPath);
        GGraph.mkdir();

        if (!GGraphPath.equals(TGraphPath)) {
            File TGraph = new File(TGraphPath);
            TGraph.mkdir();
        }

        File projectConfig = new File(dir + File.separator + "project_config.xml");
        try {
            FileUtils.saveFile(projectConfig, DEFAULT_CONFIG);
            return projectConfig;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        /*
         * File test = new File("./"); File base; if
         * (test.getAbsolutePath().contains("TTool/bin/")) base = new
         * File("../ttool/project_config.xml"); else base = new
         * File("./project_config.xml"); try { FileUtils.copyFileToDirectory(base, dir,
         * false); return new File(dir + File.separator + "project_config.xml"); } catch
         * (IOException e) { System.err.println(e.getMessage()); }
         */
        return null;
    }

    public static void loadConfigFile(File f) throws MalformedConfigurationException {
        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb 1");
        }

        String data = FileUtils.loadFileData(f);

        if (data == null) {
            throw new MalformedConfigurationException("Filepb 2");
        }

        loadConfigurationFromXML(data);
    }

    public static void loadConfigurationFromXML(String data) throws MalformedConfigurationException {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;

            nl = doc.getElementsByTagName("LastVCD");
            if (nl.getLength() > 0)
                LastVCD(nl);

            nl = doc.getElementsByTagName("LastOpenDiagram");
            if (nl.getLength() > 0)
                LastOpenDiagram(nl);

        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LastOpenDiagram(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            lastTab = Integer.parseInt(elt.getAttribute("tab"));
            lastPanel = Integer.parseInt(elt.getAttribute("panel"));
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private static void LastVCD(NodeList nl) throws MalformedConfigurationException {
        try {
            Element elt = (Element) (nl.item(0));
            lastVCD = elt.getAttribute("data");
            // ExternalCommand1 = "gtkwave " + lastVCD;
        } catch (Exception e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    public static void saveConfiguration(File f) throws MalformedConfigurationException {
        int index0, index1, index2;
        String tmp, tmp1, tmp2, location;
        boolean write = false;

        if (!FileUtils.checkFileForOpen(f)) {
            throw new MalformedConfigurationException("Filepb 3");
        }

        String data = FileUtils.loadFileData(f);

        if (data == null) {
            throw new MalformedConfigurationException("Filepb 4");
        }

        index0 = data.indexOf("LastVCD");

        if (index0 > -1) {
            index1 = data.indexOf('"', index0);
            if (index1 > -1) {
                index2 = data.indexOf('"', index1 + 1);
                if (index2 > -1) {
                    tmp = data.substring(index2, data.length());
                    data = data.substring(0, index1 + 1) + lastVCD + tmp;
                    write = true;
                }
            }
        }

        index0 = data.indexOf("LastOpenDiagram");

        if (index0 > -1) {
            tmp1 = data.substring(0, index0 + 16);
            tmp2 = data.substring(index0 + 20, data.length());
            index1 = tmp2.indexOf("/>");
            if (index1 > -1) {
                tmp2 = tmp2.substring(index1, tmp2.length());
                location = " tab=\"" + lastTab;
                location += "\" panel=\"" + lastPanel + "\" ";
                data = tmp1 + location + tmp2;
                write = true;
            }
        } else {
            index1 = data.indexOf("</PROJECTCONFIGURATION>");
            if (index1 > -1) {
                location = "<LastOpenDiagram tab=\"" + lastTab;
                location += "\" panel=\"" + lastPanel + "\"/>\n\n";
                data = data.substring(0, index1) + location + data.substring(index1, data.length());
                write = true;
            }
        }

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

    public static void setBasicConfigFile(String file) {
        basicConfigPath = file;
    }

    /**
     * Check and create the directory for c++ code generation in DIPLODOCUS
     *
     * @param s directory path
     * @return true if there's no error, false if the directory cannot be created
     * @throws FileException FileException
     * @author Fabien Tessier
     */
    public static boolean checkAndCreateSystemCDir(String s) throws FileException {
        TraceManager.addDev("Diplodocus simulation code to be generated in dir:" + s);
        File f = new File(s);
        try {
            if (!f.exists())
                if (!f.mkdir()) {
                    TraceManager.addDev("Could not create the directory");
                    return false;

                }
        } catch (Exception e) {
            TraceManager.addDev("Exception file creation for simulator: " + e.getMessage());
            throw new FileException(e.getMessage());
        }

        try {
            File make = new File(ConfigurationTTool.SystemCCodeDirectory + "/Makefile");
            File defs = new File(ConfigurationTTool.SystemCCodeDirectory + "/Makefile.defs");
            File src = new File(ConfigurationTTool.SystemCCodeDirectory + "/src_simulator");
            File lic = new File(ConfigurationTTool.SystemCCodeDirectory + "/LICENSE");
            File liceng = new File(ConfigurationTTool.SystemCCodeDirectory + "/LICENSE_CECILL_ENG");
            File licfr = new File(ConfigurationTTool.SystemCCodeDirectory + "/LICENSE_CECILL_FR");

            FileUtils.copyFileToDirectory(make, f, false);
            FileUtils.copyFileToDirectory(defs, f, false);
            FileUtils.copyDirectoryToDirectory(src, f);
            FileUtils.copyFileToDirectory(lic, f, false);
            FileUtils.copyFileToDirectory(liceng, f, false);
            FileUtils.copyFileToDirectory(licfr, f, false);

        } catch (Exception e) {
            TraceManager.addDev("Exception file creation for simulator: " + e.getMessage());
            // throw new FileException(e.getMessage());
        }
        return true;
    }

    /**
     * Check and create the directory for c code generation in AVATAR
     *
     * @param s directory path
     * @return true if there's no error, false if the directory cannot be created
     * @throws FileException FileException
     * @author Fabien Tessier
     */
    public static boolean checkAndCreateAVATARCodeDir(String s) throws FileException {
        TraceManager.addDev("Trying to create the dir:" + s);
        File f = new File(s);
        try {
            if (!f.exists()) {
                // TraceManager.addDev("Does not exist");
                if (!f.mkdir()) {
                    return false;
                }
            }
            if (!s.equals(ConfigurationTTool.AVATARExecutableCodeDirectory)) {
                File make = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "Makefile");
                File defs = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "Makefile.defs");
                File soclib = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "Makefile.forsoclib");
                File src = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "src");
                File lic = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "LICENSE");
                File liceng = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "LICENSE_CECILL_ENG");
                File licfr = new File(ConfigurationTTool.AVATARExecutableCodeDirectory + "LICENSE_CECILL_FR");
                // File topcell = new File(ConfigurationTTool.AVATARExecutableCodeDirectory +
                // "generated_topcell");

                FileUtils.copyFileToDirectory(make, f, false);
                FileUtils.copyFileToDirectory(defs, f, false);
                FileUtils.copyFileToDirectory(soclib, f, false);
                FileUtils.copyDirectoryToDirectory(src, f);
                FileUtils.copyFileToDirectory(lic, f, false);
                FileUtils.copyFileToDirectory(liceng, f, false);
                FileUtils.copyFileToDirectory(licfr, f, false);
                // FileUtils.copyDirectoryToDirectory(topcell, f);
            }
            return true;
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }

    }

    public static boolean checkAndCreateProverifDir(String s) throws FileException {
        File f = new File(s);
        try {
            if (!f.exists())
                if (!f.mkdir())
                    return false;
            if (!s.equals(ConfigurationTTool.ProVerifCodeDirectory)) {
                File readme = new File(ConfigurationTTool.ProVerifCodeDirectory + "README");
                if (readme.exists())
                    FileUtils.copyFileToDirectory(readme, f, false);
            }
            return true;
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
    }

    public static boolean checkAndCreateTMLDir(String s) throws FileException {
        File f = new File(s);
        try {
            if (!f.exists())
                if (!f.mkdir())
                    return false;
            if (!s.equals(ConfigurationTTool.TMLCodeDirectory)) {
                File readme = new File(ConfigurationTTool.TMLCodeDirectory + "README_TML");
                if (readme.exists())
                    FileUtils.copyFileToDirectory(readme, f, false);
            }
            return true;
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
    }

    public static boolean checkAndCreateCCodeDir(String s) throws FileException {
        File f = new File(s);
        try {
            if (!f.exists())
                if (!f.mkdir())
                    return false;
            if (!s.equals(ConfigurationTTool.CCodeDirectory)) {
                File readme = new File(ConfigurationTTool.CCodeDirectory + "README");
                if (readme.exists())
                    FileUtils.copyFileToDirectory(readme, f, false);
            }
            return true;
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
    }

    public static boolean checkAndCreateUPPAALDir(String s) throws FileException {
        File f = new File(s);
        try {
            if (!f.exists())
                if (!f.mkdir())
                    return false;
            if (!s.equals(ConfigurationTTool.UPPAALCodeDirectory)) {
                File readme = new File(ConfigurationTTool.UPPAALCodeDirectory + "README");
                if (readme.exists())
                    FileUtils.copyFileToDirectory(readme, f, false);
            }
            return true;
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }
    }

    public static String getConfiguration(boolean systemcOn) {
        StringBuffer sb = new StringBuffer("");

        // AVATAR: executable code
        sb.append("AVATAR (executable code):\n");
        sb.append("AVATARExecutableCodeDirectory: " + AVATARExecutableCodeDirectory + "\n");
        sb.append("AVATARMPSoCCodeDirectory: " + AVATARMPSoCCodeDirectory + "\n");
        sb.append("AVATARMPSoCCompileCommand: " + AVATARMPSoCCompileCommand + "\n");
        sb.append("AVATARExecutableCodeCompileCommand: " + AVATARExecutableCodeCompileCommand + "\n");
        sb.append("AVATARExecutableCodeExecuteCommand: " + AVATARExecutableCodeExecuteCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeCompileCommand: " + AVATARExecutableSoclibCodeCompileCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeExecuteCommand: " + AVATARExecutableSoclibCodeExecuteCommand + "\n");
        sb.append("AVATARExecutableSocLibCodeTraceCommand: " + AVATARExecutableSoclibCodeTraceCommand + "\n");

        sb.append("\nProVerif:\n");
        sb.append("ProVerifCodeDirectory: " + ProVerifCodeDirectory + "\n");

        sb.append("\nYour files (modeling, librairies, etc.):\n");
        sb.append("IMGPath: " + IMGPath + "\n");
        sb.append("DocGenPath: " + DocGenPath + "\n");
        sb.append("GGraphPath: " + GGraphPath + "\n");
        sb.append("TGraphPath: " + TGraphPath + "\n");
        sb.append("\nTTool update:\n");

        sb.append("\nDIPLODOCUS:\n");
        if (systemcOn) {
            sb.append("SystemCCodeDirectory: " + SystemCCodeDirectory + "\n");
            sb.append("SystemCCodeCompileCommand: " + SystemCCodeCompileCommand + "\n");
            sb.append("SystemCCodeExecuteCommand: " + SystemCCodeExecuteCommand + "\n");
            sb.append("SystemCCodeExecuteXCycleCommand: " + SystemCCodeExecuteXCycle + "\n");
            sb.append("SystemCCodeInteractiveExecuteCommand: " + SystemCCodeInteractiveExecuteCommand + "\n");
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

        return sb.toString();

    }

}

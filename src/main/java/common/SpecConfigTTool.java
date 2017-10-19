package common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import myutil.FileUtils;
import myutil.MalformedConfigurationException;
import myutil.TraceManager;

/**
 * Specific configuration for ttool project
 * Creation: 13/10/2017
 * @version 1.0
 * @author Fabien Tessier
 *
 */
public class SpecConfigTTool {
	public static String SystemCCodeDirectory="";
	public static String SystemCCodeCompileCommand="";
	public static String SystemCCodeExecuteCommand="";
	public static String SystemCCodeInteractiveExecuteCommand="";
	
	public static String CCodeDirectory="";
	
	public static String ProVerifCodeDirectory="";
	
	public static String AVATARExecutableCodeDirectory="";
	public static String AVATARExecutableCodeCompileCommand="";
	public static String AVATARExecutableCodeExecuteCommand="";
	
	public static String AVATARMPSoCCodeDirectory="";
	public static String AVATARMPSoCCompileCommand="";
	public static String AVATARExecutableSoclibCodeCompileCommand="";
	public static String AVATARExecutableSoclibCodeExecuteCommand="";
	public static String AVATARExecutableSoclibCodeTraceCommand="";
	public static String TMLCodeDirectory="";
	
	public static int lastPanel = -1;
	public static int lastTab = -1;
	
	public static void loadConfiguration() {
		SystemCCodeDirectory = ConfigurationTTool.SystemCCodeDirectory;
		SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand;
		SystemCCodeExecuteCommand = ConfigurationTTool.SystemCCodeExecuteCommand;
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
	}
	
	public static void setDirConfig(File dir) {
    	SystemCCodeDirectory = dir.getAbsolutePath() + "/c++_code/";
    	CCodeDirectory = dir.getAbsolutePath() + "/c_code/";
    	ProVerifCodeDirectory = dir.getAbsolutePath() + "/proverif/";
    	AVATARExecutableCodeDirectory = dir.getAbsolutePath() + "/AVATAR_executablecode/";
    	AVATARMPSoCCodeDirectory = dir.getAbsolutePath() + "/MPSoC/";
    	TMLCodeDirectory = dir.getAbsolutePath() + "/tmlcode/";
    	
    	SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	SystemCCodeExecuteCommand = ConfigurationTTool.SystemCCodeExecuteCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	SystemCCodeInteractiveExecuteCommand = ConfigurationTTool.SystemCCodeInteractiveExecuteCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	
    	AVATARExecutableCodeExecuteCommand = ConfigurationTTool.AVATARExecutableCodeExecuteCommand.replace(ConfigurationTTool.AVATARExecutableCodeDirectory,  AVATARExecutableCodeDirectory);
    	AVATARExecutableCodeCompileCommand = ConfigurationTTool.AVATARExecutableCodeCompileCommand.replace(ConfigurationTTool.AVATARExecutableCodeDirectory,  AVATARExecutableCodeDirectory);
    
    	AVATARMPSoCCompileCommand = ConfigurationTTool.AVATARMPSoCCompileCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeCompileCommand = ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeExecuteCommand = ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeTraceCommand = ConfigurationTTool.AVATARExecutableSoclibCodeTraceCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    }
	
	public static void setBasicConfig(boolean systemcOn) {
    	try {
    		lastPanel = -1;
            lastTab = -1;
			ConfigurationTTool.loadConfiguration("./launch_configurations/config.xml", systemcOn);
		} catch (MalformedConfigurationException e) {
			System.out.println("Couldn't load configuration from file: config.xml");
		}
    }
	
	public static File createProjectConfig(File dir) {
		File test = new File ("./");
		System.out.println(test.getAbsolutePath());
		File base;
		if (test.getAbsolutePath().contains("TTool/bin/"))
			base = new File("../ttool/launch_configurations/project_config.xml");
		else
			base = new File("./launch_configurations/project_config.xml");
		try {
			FileUtils.copyFileToDirectory(base, dir, false);
			return new File(dir + File.separator + "project_config.xml");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;	
	}
	
	 public static void loadConfigFile(File f) throws MalformedConfigurationException {
	        if (!FileUtils.checkFileForOpen(f)) {
	            throw new MalformedConfigurationException("Filepb");
	        }

	        String data = FileUtils.loadFileData(f);

	        if (data == null) {
	            throw new MalformedConfigurationException("Filepb");
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

	            nl = doc.getElementsByTagName("LastOpenDiagram");
	            if (nl.getLength() > 0)
	                LastOpenDiagram(nl);
	        } catch (Exception e) {
	            throw new MalformedConfigurationException(e.getMessage());
	        }
	 }
	 
	 private static void LastOpenDiagram(NodeList nl) throws MalformedConfigurationException {
		 try {
			 Element elt = (Element)(nl.item(0));
             lastTab = Integer.parseInt(elt.getAttribute("tab"));
	         lastPanel = Integer.parseInt(elt.getAttribute("panel"));
         } catch (Exception e) {
        	 throw new MalformedConfigurationException(e.getMessage());
	     }
	 }
	 
	 public static void saveConfiguration(File f) throws MalformedConfigurationException {
	        int index0, index1;
	        String tmp1, tmp2, location;
	        boolean write = false;

	        if (!FileUtils.checkFileForOpen(f)) {
	            throw new MalformedConfigurationException("Filepb");
	        }

	        String data = FileUtils.loadFileData(f);

	        if (data == null) {
	            throw new MalformedConfigurationException("Filepb");
	        }

	        index0 = data.indexOf("LastOpenDiagram");

	        if (index0 > -1) {
	            tmp1 = data.substring(0, index0+16);
	            tmp2 = data.substring(index0+20, data.length());
	            index1 = tmp2.indexOf("/>");
	            if (index1 > -1) {
	                tmp2 = tmp2.substring(index1, tmp2.length());
	                location = " tab=\"" + lastTab;
	                location += "\" panel=\"" + lastPanel + "\" ";
	                data = tmp1 + location + tmp2;
	                write = true;
	            }
	        } else {
	            index1= data.indexOf("</PROJECTCONFIGURATION>");
	            if (index1 > -1) {
	                location = "<LastOpenDiagram tab=\"" + lastTab;
	                location += "\" panel=\"" + lastPanel + "\"/>\n\n";
	                data = data.substring(0, index1) + location + data.substring(index1, data.length());
	                write = true;
	            }
	        }
	        
	        if (write) {
	            //sb.append("Writing data=" + data);
	            try {
	                FileOutputStream fos = new FileOutputStream(f);
	                fos.write(data.getBytes());
	                fos.close();
	            } catch (Exception e) {
	                throw new  MalformedConfigurationException("Saving file failed");
	            }
	        } else {
	            TraceManager.addError("Configuration could not be saved");
	        }

	 }
}

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
	
	public static String IMGPath="";
	
	public static String DocGenPath="";
	
	public static String GGraphPath="";
	public static String TGraphPath="";
	
	public static String UPPAALCodeDirectory="";
	
	public static String VCDPath="";
	public static String ExternalCommand1="";
	
	private static String ProjectSystemCCodeDirectory = "/c++_code/";
	private static String ProjectCCodeDirectory = "/c_code/";
	private static String ProjectProVerifCodeDirectory = "/proverif/";
	private static String ProjectAVATARExecutableCodeDirectory = "/AVATAR_executablecode/";
	private static String ProjectAVATARMPSoCCodeDirectory = "/MPSoC/";
	private static String ProjectTMLCodeDirectory = "/tmlcode/";
	private static String ProjectIMGDirectory = "/figures";
	private static String ProjectDocGenDirectory = "/doc";
	private static String ProjectGGraphDirectory="/graphs";
	private static String ProjectTGraphDirectory="/graphs";
	private static String ProjectUPPAALCodeDirectory="/uppaal/";
	private static String ProjectVCDDirectory="/c++_code/";
	
	public static int lastPanel = -1;
	public static int lastTab = -1;
	public static String lastVCD="";
	
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
		
		IMGPath = ConfigurationTTool.IMGPath;
		
		DocGenPath = ConfigurationTTool.DocGenPath;
		
		GGraphPath = ConfigurationTTool.GGraphPath;
		TGraphPath = ConfigurationTTool.TGraphPath;
		
		UPPAALCodeDirectory = ConfigurationTTool.UPPAALCodeDirectory;
		
		VCDPath = ConfigurationTTool.VCDPath;
		ExternalCommand1 = ConfigurationTTool.ExternalCommand1;
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
    	
    	SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	SystemCCodeExecuteCommand = ConfigurationTTool.SystemCCodeExecuteCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	SystemCCodeInteractiveExecuteCommand = ConfigurationTTool.SystemCCodeInteractiveExecuteCommand.replace(ConfigurationTTool.SystemCCodeDirectory, SystemCCodeDirectory);
    	
    	AVATARExecutableCodeExecuteCommand = ConfigurationTTool.AVATARExecutableCodeExecuteCommand.replace(ConfigurationTTool.AVATARExecutableCodeDirectory,  AVATARExecutableCodeDirectory);
    	AVATARExecutableCodeCompileCommand = ConfigurationTTool.AVATARExecutableCodeCompileCommand.replace(ConfigurationTTool.AVATARExecutableCodeDirectory,  AVATARExecutableCodeDirectory);
    
    	AVATARMPSoCCompileCommand = ConfigurationTTool.AVATARMPSoCCompileCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeCompileCommand = ConfigurationTTool.AVATARExecutableSoclibCodeCompileCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeExecuteCommand = ConfigurationTTool.AVATARExecutableSoclibCodeExecuteCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	AVATARExecutableSoclibCodeTraceCommand = ConfigurationTTool.AVATARExecutableSoclibCodeTraceCommand.replace(ConfigurationTTool.AVATARMPSoCCodeDirectory, AVATARMPSoCCompileCommand);
    	ExternalCommand1 = ConfigurationTTool.ExternalCommand1.replace(ConfigurationTTool.VCDPath, SpecConfigTTool.VCDPath);
	}
	
	public static void setBasicConfig(boolean systemcOn) {
    	try {
			ConfigurationTTool.loadConfiguration("./launch_configurations/config.xml", systemcOn);
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
		
		File test = new File ("./");
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
			 Element elt = (Element)(nl.item(0));
             lastTab = Integer.parseInt(elt.getAttribute("tab"));
	         lastPanel = Integer.parseInt(elt.getAttribute("panel"));
         } catch (Exception e) {
        	 throw new MalformedConfigurationException(e.getMessage());
	     }
	 }
	 
	 private static void LastVCD(NodeList nl) throws MalformedConfigurationException {
		 try {
			 Element elt = (Element)(nl.item(0));
             lastVCD = elt.getAttribute("data");
             ExternalCommand1 = "gtkwave " + lastVCD;
         } catch (Exception e) {
        	 throw new MalformedConfigurationException(e.getMessage());
	     }
	 }
	 
	 public static void saveConfiguration(File f) throws MalformedConfigurationException {
	        int index0, index1, index2;
	        String tmp, tmp1, tmp2, location;
	        boolean write = false;

	        if (!FileUtils.checkFileForOpen(f)) {
	            throw new MalformedConfigurationException("Filepb");
	        }

	        String data = FileUtils.loadFileData(f);

	        if (data == null) {
	            throw new MalformedConfigurationException("Filepb");
	        }
	        
	        index0 = data.indexOf("LastVCD");

	        if (index0 > -1) {
	            index1 = data.indexOf('"', index0);
	            if (index1 > -1) {
	                index2 = data.indexOf('"', index1 + 1);
	                if (index2 > -1) {
	                    tmp = data.substring(index2, data.length());
	                    data = data.substring(0, index1+1) + lastVCD + tmp;
	                    write = true;
	                }
	            }
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

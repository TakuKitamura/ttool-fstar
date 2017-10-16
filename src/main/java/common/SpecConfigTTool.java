package common;

import java.io.File;

import myutil.MalformedConfigurationException;

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
			ConfigurationTTool.loadConfiguration("./launch_configurations/config.xml", systemcOn);
		} catch (MalformedConfigurationException e) {
			System.out.println("Couldn't load configuration from file: config.xml");
		}
    }
}

package common;

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
	}

}

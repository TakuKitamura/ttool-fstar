package common;

public class SpecConfigTTool {
	public static String SystemCCodeDirectory="";
	public static String SystemCCodeCompileCommand="";
	
	public static void loadConfiguration() {
		SystemCCodeDirectory = ConfigurationTTool.SystemCCodeDirectory;
		SystemCCodeCompileCommand = ConfigurationTTool.SystemCCodeCompileCommand;
	}

}

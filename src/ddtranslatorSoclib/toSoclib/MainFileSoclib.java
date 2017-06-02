/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toSoclib;

public class MainFileSoclib {
	
    private final static String H_DEF = "#ifndef MAIN_H\n#define MAIN_H\n";
    private final static String H_END_DEF = "#endif\n";

    private final static String INCLUDE_HEADER = "#include <stdio.h>\n#include <pthread.h>\n#include <unistd.h>\n#include <stdlib.h>\n";
    private final static String LOCAL_INCLUDE_HEADER = "#include \"request.h\"\n#include \"myerrors.h\"\n#include \"message.h\"\n#include \"syncchannel.h\"\n#include \"asyncchannel.h\"\n#include \"mytimelib.h\"\n#include \"request_manager.h\"\n#include \"defs.h\"\n#include \"debug.h\"\n#include \"random.h\"\n#include \"tracemanager.h\" \n#include \"mwmr.h\" \n "; 
	
    private final static String MAIN_DEC = "int main(int argc, char *argv[]) {\n";
	
    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    private String name;
    private String hCode;
    private String beforeMainCode;
    private String mainCode;
    private String def_nbprog = "";
	
    public MainFileSoclib(String _name) {
	name = _name;
	hCode = "";
	mainCode = "";
	beforeMainCode = "";
        def_nbprog += "#define NB_PROC "+ TasksAndMainGenerator.avddspec.getNbCPU() + CR+
	    "#define WIDTH 4" +CR+
	    "#define DEPTH 16"+CR2;
	}
	
    public String getName() {
	return name;
    }
	
    public void appendToHCode(String _code) {
	hCode += _code;
    }
	
    public void appendToBeforeMainCode(String _code) {
	beforeMainCode += _code;
    }
	
    public void appendToMainCode(String _code) {
	mainCode += _code;
    }
	
    public String getHeaderCode() {
	return H_DEF + hCode + H_END_DEF;
    }
	
    public String getMainCode() {
	String s = INCLUDE_HEADER + "\n" + LOCAL_INCLUDE_HEADER 
	    + CR + CR + def_nbprog ;
	s += beforeMainCode + CR;
	s += MAIN_DEC + CR;
	s += CR + mainCode + CR + "}" + CR;
	return s;		
    }	
}

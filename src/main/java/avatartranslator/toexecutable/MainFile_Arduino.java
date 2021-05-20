package avatartranslator.toexecutable;

import myutil.Plugin;
import myutil.TraceManager;

public class MainFile_Arduino {

    private final static String SETUP_CODE = "";
    private final static String LOOP_CODE = "";

    private final static String H_DEF = "#ifndef MAIN_H\n#define MAIN_H\n";
    private final static String H_END_DEF = "#endif\n";

    private final static String INCLUDE_HEADER = "#include <stdio.h>\n#include <pthread.h>\n#include <unistd.h>\n#include <stdlib.h>\n";
    private final static String LOCAL_INCLUDE_HEADER = "#include \"request.h\"\n#include \"syncchannel.h\"\n#include \"request_manager.h\"\n#include \"debug.h\"\n#include \"random.h\"\n#include \"tracemanager.h\"";

    private final static String MAIN_DEC = "int main(int argc, char *argv[]) {\n";
    private final static String DISABLE_BUFFERING = "/* disable buffering on stdout */\nsetvbuf(stdout, NULL, _IONBF, 0);\n";

    private final static String CR = "\n";

    private String name;
    private String hCode;
    private String beforeMainCode;
    private String mainCode;

    private Plugin plugin;

    public MainFile_Arduino(String _name, Plugin _plugin) {
        name = _name;
        plugin = _plugin;
        hCode = "";
        mainCode = "";
        beforeMainCode = "";

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

        String mainDec = MAIN_DEC;

        try {
            if (plugin != null) {
                mainDec = plugin.executeRetStringMethod(plugin.getClassAvatarCodeGenerator(), "getMainDeclaration");
            }

        } catch (Exception e) {
            TraceManager.addDev("plugin exception: " + e.getMessage());
        }

        String s = INCLUDE_HEADER + "\n" + LOCAL_INCLUDE_HEADER + CR + CR;

        s += beforeMainCode + CR;
        // s += DISABLE_BUFFERING;
        s += CR + mainCode + CR + CR;

        return s;

    }

}

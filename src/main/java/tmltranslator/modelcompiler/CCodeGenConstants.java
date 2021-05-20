package tmltranslator.modelcompiler;

public interface CCodeGenConstants {

    String CR = "\n";
    String CR2 = "\n\n";
    String TAB = "\t";
    String TAB2 = "\t\t";
    String TAB3 = "\t\t\t";
    String TAB4 = "\t\t\t\t";
    String SP = " ";
    String SC = ";";

    String NATURAL_TYPE = "int";
    String BOOLEAN_TYPE = "bool";

    String DEFAULT_NUM_VAL = "0";
    String DEFAULT_BOOL_VAL = Boolean.FALSE.toString();
    String USER_TO_DO = "/* USER TO DO */";
}

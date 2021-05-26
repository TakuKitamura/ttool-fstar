import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateFstar {
    public static String fstarCode;

    public static void main(String[] args) {
        try {
            fstarCode = readAll("./fstar.templ");
        } catch (Exception e) {
            System.out.println(e);
        }

        fstarCode = fstarCode.replaceAll("@module_name@", "Add");
        fstarCode = fstarCode.replaceAll("@ret_integer_type@", "I32");
        fstarCode = fstarCode.replaceAll("@ret_integer_type_suffix@", "l");
        fstarCode = fstarCode.replaceAll("@arg_constraint@",
                "let xConstraint x = I32.gte x 1l && I32.lte x 100l\nlet yConstraint y = I32.gte y 1l && I32.lte y 100l");
        fstarCode = fstarCode.replaceAll("@ret_constraint@",
                "let retConstraint ret = I32.gte ret 2l && I32.lte ret 200l");
        fstarCode = fstarCode.replaceAll("@interface_func_name@", "add");
        fstarCode = fstarCode.replaceAll("@interface_func_body_arg@", "x: I32.t ->\n  y: I32.t ->");
        fstarCode = fstarCode.replaceAll("@interface_func_body_arg_require@", "xConstraint x && yConstraint y");
        fstarCode = fstarCode.replaceAll("@args@", "x y");
        fstarCode = fstarCode.replaceAll("@unimplemented_func_ret_value@", "2");
        fstarCode = fstarCode.replaceAll("@interface_func_arg@", "x: I32.t ->\n  y: I32.t ->");
        fstarCode = fstarCode.replaceAll("@valid_func_body_call_condition@", "(xConstraint x) && (yConstraint y)");
        System.out.println(fstarCode);
    }

    public static String readAll(String path) throws IOException {
        return Files.lines(Paths.get(path)).reduce("",
                (prev, line) -> prev + line + System.getProperty("line.separator"));
    }
}

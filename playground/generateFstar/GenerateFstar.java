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

        fstarCode = fstarCode.replaceAll("@module_name@", "Add"); // funcName

        fstarCode = fstarCode.replaceAll("@ret_value_type@", "I32.t"); // return.type

        fstarCode = fstarCode.replaceAll("@ng_ret_value@", "0l"); // どうするか検討

        fstarCode = fstarCode.replaceAll("@require@", // パース結果から構成
                "I32.gte x 1l && I32.lte y 100l");

        fstarCode = fstarCode.replaceAll("@ensure@", "I32.gte ret 2l && I32.lte ret 200l"); // パース結果から構成

        fstarCode = fstarCode.replaceAll("@func_name@", "add"); // funcName

        fstarCode = fstarCode.replaceAll("@args@", "x y"); // method.args

        fstarCode = fstarCode.replaceAll("@init_ret_value@", "2l"); // 検討

        fstarCode = fstarCode.replaceAll("@func_arg@", "x: I32.t ->\n  y: I32.t ->"); // method.args

        fstarCode = fstarCode.replaceAll("@ret_args@", "ret"); // パース結果から構成, ret を最後に持ってくる
        System.out.println(fstarCode);
    }

    public static String readAll(String path) throws IOException {
        return Files.lines(Paths.get(path)).reduce("",
                (prev, line) -> prev + line + System.getProperty("line.separator"));
    }
}

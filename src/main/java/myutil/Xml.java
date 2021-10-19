package myutil;

/**
 * Class Xml
 *
 * @author Taku Kitamura
 * @version 2.0 20/05/2021
 */
public class Xml {
    public String sanitize(String s) {
        return s.replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
                .replaceAll("\n", "&return;").replaceAll("&", "&amp;");
    }

    public String decode(String s) {
        return s.replaceAll("&quot;", "\"").replaceAll("&apos;", "'").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&return;", "\n").replaceAll("&amp;", "&");
    }
}

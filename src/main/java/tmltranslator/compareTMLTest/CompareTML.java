package tmltranslator.compareTMLTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
public class CompareTML {

    public CompareTML () {

    }
    //#issue 82
    public boolean compareTML(File expected, File clone) throws Exception {

        BufferedReader expectedReader = new BufferedReader(new FileReader(expected));
        BufferedReader cloneReader = new BufferedReader(new FileReader(clone));

        String expectedString = "";
        String cloneString = "";
        String s1;
        String s2;

        while ((s1 = expectedReader.readLine()) != null) {
            if (!s1.contains("//") && s1.length() > 0) {
                expectedString += s1;
            }
        }

        while ((s2 = cloneReader.readLine()) != null){
            if (!s2.contains("//") && s2.length() > 0) {
                cloneString += s2;
            }
        }
        return expectedString.equals(cloneString);
    }
}

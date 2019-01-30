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
                s1 = s1.trim();
                expectedString += s1;
            }
        }

        while ((s2 = cloneReader.readLine()) != null){
            if (!s2.contains("//") && s2.length() > 0) {
                s2 = s2.trim();
                cloneString += s2;
            }
        }

        String[] expectedStringArray = expectedString.split("\\s+");
        String[] cloneStringArray = cloneString.split("\\s+");
        return checkEquality(expectedStringArray,cloneStringArray);
    }

    public boolean checkEquality(String[] s1, String[] s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        int n = s1.length;
        if (n != s2.length)
            return false;

        for (int i = 0; i < n; i++) {
            if (!s1[i].equals(s2[i]))
                return false;
        }

        return true;
    }
}

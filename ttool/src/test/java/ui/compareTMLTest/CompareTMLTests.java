package ui.compareTMLTest;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CompareTMLTests {

    final static String PATH_TO_TEST_COMPARE_FILE = "test/java/ui/compareTMLTest/";

    final static String COMMENT_AND_CONTEXT_1 = "// File with comment + context 1" +
            "\n// Channels\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1 IN DIPLODOCUS_C_Design__PrimitiveComp2\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel_0 BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1_0 IN DIPLODOCUS_C_Design__PrimitiveComp2_0";
    final static String COMMENT_AND_CONTEXT_2 = "// File with comment + context 2" +
            "\n// Channels\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1 IN DIPLODOCUS_C_Design__PrimitiveComp2\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel_0 BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1_0 IN DIPLODOCUS_C_Design__PrimitiveComp2_0";
    final static String COMMENT_AND_CONTEXT_3 = "// File with comment + context 3" +
            "\n// Channels\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel BRBW 5 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1 IN DIPLODOCUS_C_Design__PrimitiveComp2\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel_0 BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1_0 IN DIPLODOCUS_C_Design__PrimitiveComp2_0";
    final  static String COMMENT_AND_CONTEXT_4 = "// File with comment + context 3" +
            "\n// Channels\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1 IN DIPLODOCUS_C_Design__PrimitiveComp2\n" +
            "CHANNEL DIPLODOCUS_C_Design__channel_0 BRBW 4 8 OUT DIPLODOCUS_C_Design__PrimitiveComp1_0 IN DIPLODOCUS_C_Design__PrimitiveComp2_0";
    final static String ONLY_TITLE_1 = "// comment 1";
    final static String ONLY_TITLE_2 = "// comment 2";
    final static String EMPTY = "";

    @Before
    public void setUp () throws Exception{
        String fileData;
        fileData = EMPTY;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file1.tml"), fileData.getBytes());

        fileData = ONLY_TITLE_1;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file2.tml"),fileData.getBytes());

        fileData = ONLY_TITLE_2;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file3.tml"),fileData.getBytes());

        fileData = COMMENT_AND_CONTEXT_1;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file4.tml"),fileData.getBytes());

        fileData = COMMENT_AND_CONTEXT_2;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file5.tml"),fileData.getBytes());

        fileData = COMMENT_AND_CONTEXT_3;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file6.tml"),fileData.getBytes());

        fileData = COMMENT_AND_CONTEXT_4;
        Files.write(Paths.get(PATH_TO_TEST_COMPARE_FILE + "file7.tml"),fileData.getBytes());
    }

    @Test
    public void onlyCommentAndEmptyTest () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between empty file and the other including only comment",ctml.compareTML(new File(PATH_TO_TEST_COMPARE_FILE + "file1" +
                        ".tml"),
                new File(PATH_TO_TEST_COMPARE_FILE + "file2.tml")));
    }

    @Test
    public void onlyCommentAndOnlyComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between 2 files including only comment",ctml.compareTML(new File(PATH_TO_TEST_COMPARE_FILE + "file2.tml"),
                new File(PATH_TO_TEST_COMPARE_FILE + "file3" +
                        ".tml")));
    }

    @Test
    public void sameContextDifferentComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between 2 files including the same context but different comment",ctml.compareTML(new File(PATH_TO_TEST_COMPARE_FILE +
                        "file4.tml"),
                new File(PATH_TO_TEST_COMPARE_FILE + "file5.tml")));
    }

    @Test
    public void DifferentContextDifferentComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertFalse("comparing between 2 files including the same context but different comment",ctml.compareTML(new File(PATH_TO_TEST_COMPARE_FILE +
                        "file5.tml"),
                new File(PATH_TO_TEST_COMPARE_FILE + "file6.tml")));
    }

    @Test
    public void DifferentContextSameComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertFalse("comparing between 2 files including the same comment but different context",
                ctml.compareTML(new File(PATH_TO_TEST_COMPARE_FILE +
                        "file6.tml"),
                new File(PATH_TO_TEST_COMPARE_FILE + "file7.tml")));
    }
}

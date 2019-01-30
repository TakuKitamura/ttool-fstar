package tmltranslator;

import tmltranslator.compareTMLTest.CompareTML;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CompareTMLTests {

    final static String PATH_TO_TEST_COMPARE_FILE = "test/resources/tmltranslator/input/";

    final static String EMPTY_FILE = PATH_TO_TEST_COMPARE_FILE + "file1.tml";
    final static String ONLY_COMMENT_1 = PATH_TO_TEST_COMPARE_FILE + "file2.tml";
    final static String ONLY_COMMENT_2 = PATH_TO_TEST_COMPARE_FILE + "file3.tml";
    final static String COMMENT_AND_CONTEXT_1 = PATH_TO_TEST_COMPARE_FILE + "file4.tml";
    final static String COMMENT_AND_CONTEXT_2 = PATH_TO_TEST_COMPARE_FILE + "file5.tml";
    final static String COMMENT_AND_CONTEXT_3 = PATH_TO_TEST_COMPARE_FILE + "file6.tml";
    final static String COMMENT_AND_CONTEXT_4 = PATH_TO_TEST_COMPARE_FILE + "file7.tml";


    @Test
    public void onlyCommentAndEmptyTest () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between empty file and another file including only comment",ctml.compareTML(new File(EMPTY_FILE),
                new File(ONLY_COMMENT_1)));
    }

    @Test
    public void onlyCommentAndOnlyComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between 2 files including only comment",ctml.compareTML(new File(ONLY_COMMENT_1),
                new File(ONLY_COMMENT_2)));
    }

    @Test
    public void sameContextDifferentComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertTrue("comparing between 2 files including the same context but different comment",ctml.compareTML(new File(COMMENT_AND_CONTEXT_1),
                new File(COMMENT_AND_CONTEXT_2)));
    }

    @Test
    public void DifferentContextDifferentComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertFalse("comparing between 2 files including the same context but different comment",ctml.compareTML(new File(COMMENT_AND_CONTEXT_2),
                new File(COMMENT_AND_CONTEXT_3)));
    }

    @Test
    public void DifferentContextSameComment () throws Exception {
        CompareTML ctml = new CompareTML();
        assertFalse("comparing between 2 files including the same comment but different context",
                ctml.compareTML(new File(COMMENT_AND_CONTEXT_3),
                new File(COMMENT_AND_CONTEXT_4)));
    }
}

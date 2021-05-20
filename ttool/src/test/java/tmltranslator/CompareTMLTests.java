package tmltranslator;

import test.AbstractTest;
import tmltranslator.compareTMLTest.CompareTML;
import org.junit.Before;
import org.junit.Test;
import ui.AbstractUITest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CompareTMLTests extends AbstractTest {

  final static String PATH_TO_TEST_COMPARE_FILE = "/tmltranslator/input/";

  final static String EMPTY_FILE = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file1.tml";
  final static String ONLY_COMMENT_1 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file2.tml";
  final static String ONLY_COMMENT_2 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file3.tml";
  final static String COMMENT_AND_CONTEXT_1 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file4.tml";
  final static String COMMENT_AND_CONTEXT_2 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file5.tml";
  final static String COMMENT_AND_CONTEXT_3 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file6.tml";
  final static String COMMENT_AND_CONTEXT_4 = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file7.tml";

  // Test true cases
  @Test
  public void onlyCommentAndEmptyTest() throws Exception {
    CompareTML ctml = new CompareTML();
    /*
     * file 1 and file 2 file 1 includes only empty lines file 2 includes only
     * comments
     */
    assertTrue("comparing between empty file and another file including only comment",
        ctml.compareTML(new File(EMPTY_FILE), new File(ONLY_COMMENT_1)));
  }

  @Test
  public void onlyCommentAndOnlyComment() throws Exception {
    CompareTML ctml = new CompareTML();
    /*
     * file 2 and file 3 file 2 and file 3 include only comments and empty lines
     */
    assertTrue("comparing between 2 files including only comment",
        ctml.compareTML(new File(ONLY_COMMENT_1), new File(ONLY_COMMENT_2)));
  }

  @Test
  public void sameContextDifferentComment() throws Exception {
    CompareTML ctml = new CompareTML();
    /*
     * file 4 and file 5 two files includes many comments, empty lines and white
     * spaces before and after strings (comments or context) adding white spaces
     * between two words
     */
    assertTrue("comparing between 2 files including the same context but different comment",
        ctml.compareTML(new File(COMMENT_AND_CONTEXT_1), new File(COMMENT_AND_CONTEXT_2)));
  }

  // Test false cases
  @Test
  public void DifferentContextSameComment() throws Exception {
    CompareTML ctml = new CompareTML();
    /*
     * file 5 and file 6 file 5 has one different line in comparison with file 6
     */
    assertFalse("comparing between 2 files including the same context but different comment",
        ctml.compareTML(new File(COMMENT_AND_CONTEXT_2), new File(COMMENT_AND_CONTEXT_3)));
  }

  @Test
  public void DifferentContextSameComment_2() throws Exception {
    CompareTML ctml = new CompareTML();
    /*
     * file 6 and file 7 adding context in file 7 in comparison with file 6
     */
    assertFalse("comparing between 2 files including the same comment but different context",
        ctml.compareTML(new File(COMMENT_AND_CONTEXT_3), new File(COMMENT_AND_CONTEXT_4)));
  }
}

/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.
*/
package myutil;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoolExpressionEvaluatorTest {

  @Test
  public void testIntegerExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("1"));
    assertTrue(bee.hasError());
  }

  @Test
  public void testFalseSimpleExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("1==0"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueSimpleExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("t==t"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testUnfinishedExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("t=="));
    assertTrue(bee.hasError());
  }

  @Test
  public void testFalseMultipleEqualExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("t==t==f"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueMultipleEqualExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("t==t==t"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueDoubleIntegerEqualityExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("(3==3) == (4==4)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueDoubleIntegerNullEqualityExpr() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    System.out.println("Hello");
    assertTrue(bee.getResultOfWithIntExpr("((0)==(0)) || (0==0)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseLeftAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("(1+2)==4"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueLeftAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("(1+2)==3"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueRightAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("3==1+2"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseMultipleAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("1+2+3+4+5==3+7"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseAdditionResultsComparison() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("(1+2==3)==(5==4)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueLeftMultiplication() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("2*3==6"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueLeftMultiplicationAndAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("2+1*4==6"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueDivisionAndAddition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("2+4/1==6"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueDivisionAndSoustraction() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("8-4/2==6"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueAndCondition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("(3==3) and (4==4)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseAndCondition() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("(3==3) and (3==4)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueNegation() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("not(((1)==(3)))"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueLesserThan() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("1<3"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseLesserThanAndNegation() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("not(1<3)"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testMalformedLesserThan() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("not(1<2<3)"));
    assertTrue(bee.hasError());
  }

  @Test
  public void testTrueComplexGreaterThan() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("(5>=4)==true"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testFalseComplexLesserThan() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertFalse(bee.getResultOfWithIntExpr("(5<=4)==true"));
    assertFalse(bee.hasError());
  }

  @Test
  public void testTrueAdditionAndGreaterThan() {
    BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
    assertTrue(bee.getResultOfWithIntExpr("(1+2==3)==(5>4)"));
    assertFalse(bee.hasError());
  }
}

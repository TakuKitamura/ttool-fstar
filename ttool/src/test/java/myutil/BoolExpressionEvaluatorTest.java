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


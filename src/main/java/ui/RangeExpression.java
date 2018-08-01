package ui;

public class RangeExpression extends Expression {
	
	private final Expression minExpression;

	private final Expression maxExpression;
	
	
	public RangeExpression( final String minExpressionText,
							final String maxExpressionText,
							final String nullText,
							final String labelGlobal,
							final String labelValues ) {
		super( null, nullText, labelGlobal );
		
		
		minExpression = new Expression( minExpressionText, nullText, labelValues );
		maxExpression = new Expression( maxExpressionText, nullText, labelValues );
	}

	public Expression getMinExpression() {
		return minExpression;
	}

	public Expression getMaxExpression() {
		return maxExpression;
	}
	
	@Override
	public void setEnabled( final boolean enabled ) {
		getMinExpression().setEnabled( enabled );
		getMaxExpression().setEnabled( enabled );
	}
	
	@Override
	public boolean isEnabled() {
		return minExpression.isEnabled() || maxExpression.isEnabled();
	}
	
	@Override
	public boolean isNull() {
		return minExpression.isNull() && maxExpression.isNull();
	}
	
	@Override
	public String toString() {
		if ( getLabel() == null ) {
			return "[" + getMinExpression().toString() + ", " + getMaxExpression().toString() + "]";
		}
		
		final Object[] values = new String[] { getMinExpression().toString(), getMaxExpression().toString() };
		
		return String.format( getLabel(), values );
	}
}



package ui;

import myutil.TraceManager;

public class RangeExpression extends Expression {
	
	private final Expression minExpression;

	private final Expression maxExpression;

	private final String labelOneNull;
	
	
	public RangeExpression( final String minExpressionText,
							final String maxExpressionText,
							final String nullText,
							final String labelGlobal,
							final String labelValues,
                            final String labelOneNull) {
		super( null, nullText, labelGlobal );

		this.labelOneNull = labelOneNull;
		
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

	public String getLabelOneNull() {
	    return labelOneNull;
    }

	public Expression isOneNull() {
	    if (isNull()) {
	        return null;
        }

        if (minExpression.isNull()) {
	        return maxExpression;
        }

        if (maxExpression.isNull()) {
            return minExpression;
        }

        return null;


    }
	
	@Override
	public String toString() {
		//TraceManager.addDev("Max Text=" + maxExpression.getText());

		if ( getLabel() == null ) {
		    //TraceManager.addDev("no label");
			return "[" + getMinExpression().toString() + ", " + getMaxExpression().toString() + "]";
		}

		Expression expr = isOneNull();
		if (expr == null) {
            //TraceManager.addDev("max=" + getMaxExpression().toString());
            final Object[] values = new String[]{getMinExpression().toString(), getMaxExpression().toString()};

            return String.format(getLabel(), values);
        }

        Object[] vals = new String[]{expr.toString()};
		//TraceManager.addDev("labelOneNull=" + labelOneNull)
        return String.format(getLabelOneNull(), vals);

	}
}

package ui;

public class Expression {
	
	private String text;
	
	private final String nullText;
	
	private final String label;

	private boolean enabled;
	
	public Expression( final String text ) {
		this( text, null, null );
	}
	
	public Expression( 	final String text,
						final String nullText,
						final String label ) {
		this.text = text;
		this.nullText = nullText;
		this.label = label;
		this.enabled = true;
	}
//
//	public Expression( 	final String text,
//						final boolean enabled,
//						final String nullText ) {
//	}

	public String getText() {
		return text;
	}

	public void setText( String text ) {
		this.text = text;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getEffectiveExpression( final String defaultExpr ) {
		return isEnabled() ? getText() : defaultExpr;
	}
	
	public boolean isNull() {
		if ( nullText == null ) {
			return getText() == null;
		}
		
		return nullText.equals( getText() );
	}
	
	@Override
	public String toString() {
		final String text = isNull() ? "null" : getText();

		return label == null ? text : String.format( label, text );
	}

	public String getLabel() {
		return label;
	}
}

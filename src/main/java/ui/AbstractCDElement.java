package ui;

public abstract class AbstractCDElement implements CDElement {
	
	private boolean enabled;
	
	protected AbstractCDElement() {
		enabled = true;
	}
    
    /**
     * Issue #69
     * @param _enabled  :   boolean data type
     */
    @Override
    public void setEnabled( final boolean _enabled ) {
    	doSetEnabled( _enabled );
    }
    
    /**
     * Issue #69
     * @param _enabled  :   boolean data type
     */
    @Override
    public void doSetEnabled( final boolean _enabled ) {
    	enabled = _enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isEnabled( boolean checkBranch ) {
        return isEnabled();
    }

    /**
     * Issue #69
     * @return  :   Always False
     */
    @Override
    public boolean canBeDisabled() {
    	return false;
    }

    /**
     * Issue #69
     * @param label :   Label
     * @return      :   Always False
     */
    @Override
    public boolean canLabelBeDisabled( TGCOneLineText label ) {
    	return false;
    }
    
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptForward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptForward( final ICDElementVisitor visitor ) {
		visitor.visit( this );
    }
    
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptBackward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptBackward( final ICDElementVisitor visitor ) {
		visitor.visit( this );
    }
//    
//    @Override
//    public boolean isFullDisabler() {
//    	return false;
//    }
}

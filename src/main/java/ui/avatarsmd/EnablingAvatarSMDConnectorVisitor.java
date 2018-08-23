package ui.avatarsmd;

import ui.CDElement;
import ui.TrackingCDElementVisitor;

public class EnablingAvatarSMDConnectorVisitor extends TrackingCDElementVisitor {
	
	private final boolean enabled;
	
	public EnablingAvatarSMDConnectorVisitor( final boolean enabled ) {
		super();
		
		this.enabled = enabled;
	}

	@Override
	public boolean visit( final CDElement element ) {
		if ( !super.visit( element ) ) {
			return false;
		}

		if ( element instanceof AvatarSMDConnector ) {
			( (AvatarSMDConnector) element ).getAvatarSMDTransitionInfo().setEnabled( enabled );
		}
		else if ( element.canBeDisabled() ) {
			element.setEnabled( enabled );
		}
		
		return true;
	}
}

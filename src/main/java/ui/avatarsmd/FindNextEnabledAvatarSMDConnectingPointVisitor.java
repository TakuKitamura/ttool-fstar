package ui.avatarsmd;

import java.util.HashSet;
import java.util.Set;

import ui.CDElement;
import ui.FindNextEnabledConnectingPointVisitor;
import ui.TGComponent;
import ui.TGConnector;

public class FindNextEnabledAvatarSMDConnectingPointVisitor extends FindNextEnabledConnectingPointVisitor {
	
	private final Set<TGComponent> componentsToBeTranslated;

	public FindNextEnabledAvatarSMDConnectingPointVisitor( 	final Set<TGConnector> disabledConnectors,
															final Set<TGComponent> componentsToBeTranslated ) {
		super( disabledConnectors );
		
		this.componentsToBeTranslated = new HashSet<TGComponent>();
		this.componentsToBeTranslated.addAll( componentsToBeTranslated );
	}
	
	@Override
	protected boolean pruneConnector( final TGConnector connector ) {
		final AvatarSMDConnector smdCon = (AvatarSMDConnector) connector;
		
		final AvatarSMDState containingState = smdCon.getContainingState();
    	
		// Do not translate connectors that connect two component contained in a disabled state machine
    	if ( containingState != null && !containingState.isEnabled() ) {
    		return true;
    	}

    	final AvatarSMDTransitionInfo transInfo = smdCon.getAvatarSMDTransitionInfo();

		return !transInfo.isEnabledNotNull() || transInfo.isNull();
	}
	
	@Override
	protected boolean pruneElement( final CDElement diagramElement ) {
		return !componentsToBeTranslated.contains( diagramElement );
	}
}

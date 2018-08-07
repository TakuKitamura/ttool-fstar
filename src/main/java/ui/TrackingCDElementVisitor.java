package ui;

import java.util.HashSet;
import java.util.Set;

public abstract class TrackingCDElementVisitor implements ICDElementVisitor {
	
	private final Set<CDElement> visitedElements;
	
	protected TrackingCDElementVisitor() {
		visitedElements = new HashSet<CDElement>();
	}

	@Override
	public boolean visit(CDElement element) {
		return visitedElements.add( element );
	}
}

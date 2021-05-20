package ui.ad;

import ui.TrackingCDElementVisitor;
import ui.CDElement;
import ui.TGConnectingPoint;

public class CanBeDisabledADElementVisitor extends TrackingCDElementVisitor {

    private boolean canBeDisabled;

    public CanBeDisabledADElementVisitor() {
        super();

        canBeDisabled = true;
    }

    /*
     * Check that the element is not part of the inside loop of a for loop
     * (non-Javadoc)
     * 
     * @see ui.ICDElementVisitor#visit(ui.CDElement)
     */
    @Override
    public boolean visit(final CDElement element) {
        if (!super.visit(element)) {
            return false;
        }

        if (element instanceof TGConnectingPoint) {
            final CDElement father = ((TGConnectingPoint) element).getFather();

            if (father instanceof TADForLoop) {
                if (element == ((TADForLoop) father).getInsideLoopConnectingPoint() && !father.isEnabled()) {
                    canBeDisabled = false;

                    return false;
                }
            }

            // if ( father instanceof TADSequence ) {
            // if ( element != ( (TADSequence) father ).getEnterSequenceConnectionPoint() &&
            // !father.isEnabled() ) {
            // canBeDisabled = false;
            //
            // return false;
            // }
            // }
        }

        return true;
    }

    public boolean isCanBeDisabled() {
        return canBeDisabled;
    }
}

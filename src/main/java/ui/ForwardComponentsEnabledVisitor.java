package ui;

public class ForwardComponentsEnabledVisitor extends TrackingCDElementVisitor {

    private boolean enabled;

    public ForwardComponentsEnabledVisitor() {
        super();

        enabled = true;
    }

    @Override
    public boolean visit(CDElement element) {
        if (!super.visit(element)) {
            return false;
        }

        if (element.canBeDisabled() && !(element instanceof TGConnector)) {
            enabled = element.isEnabled();

            return false;
        }

        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

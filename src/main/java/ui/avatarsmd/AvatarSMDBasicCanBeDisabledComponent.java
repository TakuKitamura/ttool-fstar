package ui.avatarsmd;

import ui.TDiagramPanel;
import ui.TGComponent;

public abstract class AvatarSMDBasicCanBeDisabledComponent extends AvatarSMDBasicComponent {

    public AvatarSMDBasicCanBeDisabledComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY,
            boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
    }

    /*
     * Issue #69 (non-Javadoc)
     * 
     * @see ui.AbstractCDElement#canBeDisabled()
     */
    @Override
    public boolean canBeDisabled() {
        if (getFather() instanceof AvatarSMDState) {
            return getFather().isEnabled();
        }

        return true;
    }
}

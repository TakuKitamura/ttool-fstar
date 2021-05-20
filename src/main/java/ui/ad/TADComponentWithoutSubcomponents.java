package ui.ad;

import ui.TDiagramPanel;
import ui.TGCScalableWithoutInternalComponent;
import ui.TGComponent;

/**
 * Issue #69
 * 
 * @author dblouin
 *
 */
public abstract class TADComponentWithoutSubcomponents
        extends TGCScalableWithoutInternalComponent /* Issue #31 TGCWithoutInternalComponent */ {

    public TADComponentWithoutSubcomponents(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
    }

    /**
     * Issue #69
     * 
     * @return : true if can be able to disable ElementVisitor in Activity Diagram
     */
    @Override
    public boolean canBeDisabled() {
        final CanBeDisabledADElementVisitor visitor = new CanBeDisabledADElementVisitor();
        acceptBackward(visitor);

        return visitor.isCanBeDisabled();
    }
}

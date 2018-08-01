package ui.avatarad;

import ui.TDiagramPanel;
import ui.TGComponent;

public abstract class AvatarADBasicCanBeDisabledComponent extends AvatarADBasicComponent {
    
    public AvatarADBasicCanBeDisabledComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
			TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
	}

	@Override
    public boolean canBeDisabled() {
    	return true;
    }
}

package ui.procsd;

import ui.CDElement;
import ui.TGComponentManager;
import ui.cd.TGConnectingPointCompositionOperator;

public class TGConnectingPointPROCSDInterface extends TGConnectingPointCompositionOperator {

	
	public TGConnectingPointPROCSDInterface(CDElement _container1, int _x, int _y, boolean _in, boolean _out) {
        super(_container1, _x, _y, _in, _out);
    }


	public boolean isCompatibleWith(int type) {
        if (type == TGComponentManager.CONNECTOR_ATTRIBUTE) {
            return true;
        }
     
        if (type == TGComponentManager.CONNECTOR_PROCSD_PORT_INTERFACE) {
            return true;
        }
     
        
        return false;
    }

}

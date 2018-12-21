package ui.ad;

import java.awt.Point;
import java.util.Vector;

import ui.ForwardComponentsEnabledVisitor;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGConnector;

/**
 * Issue #69
 * @author dblouin
 *
 */
public abstract class TADConnector extends TGConnector {

	public TADConnector(	int _x, 
							int _y,
							int _minX,
							int _maxX,
							int _minY,
							int _maxY,
							boolean _pos,
							TGComponent _father,
							TDiagramPanel _tdp,
							TGConnectingPoint _p1,
							TGConnectingPoint _p2,
							Vector<Point> _listPoint ) {
		super( _x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint );
	}
	
    /**
     * Issue #69
     * @param _enabled	:	true for enabling the connector in Activity Diagram
     */
    @Override
    public void setEnabled( final boolean _enabled ) {
    	if ( p2 != null ) {
    		p2.acceptForward( new EnablingADConnectorVisitor( _enabled ) );
    	}
    }
    
    /**
     * Issue #69
     * @return	:	true if can be able to disable the Connector in Activity Diagram
     */
    @Override
    public boolean canBeDisabled() {
    	if ( p2 != null && p2.getFather() instanceof TADStopState ) {
    		return false;
    	}
    	
    	final CanBeDisabledADElementVisitor visitor = new CanBeDisabledADElementVisitor();
    	acceptBackward( visitor );
    	
    	return visitor.isCanBeDisabled();
    }
    
    /** Issue #69
     * @return	:	boolean value allows to verify the connector stage in Activity Diagram
     */
    public boolean isEnabled( boolean checkBranch ) {
    	if ( checkBranch && p2 != null ) {
    		final ForwardComponentsEnabledVisitor visitor = new ForwardComponentsEnabledVisitor();
    		p2.acceptForward( visitor );
    		
    		return visitor.isEnabled();
    	}
    	
    	return super.isEnabled();
    }
}

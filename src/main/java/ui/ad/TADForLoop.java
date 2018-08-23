package ui.ad;

import java.awt.geom.Line2D;

import myutil.GraphicLib;
import ui.BasicErrorHighlight;
import ui.EmbeddedComment;
import ui.ICDElementVisitor;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.util.IconManager;

/**
 * Issue #69
 * @author dblouin
 *
 */
public abstract class TADForLoop extends TADComponentWithoutSubcomponents implements EmbeddedComment, BasicErrorHighlight {

	protected final static String IN_LOOP = "inside loop";
	protected final static String EXIT_LOOP = "exit loop";
	
	protected static final int INDEX_ENTER_LOOP = 0;
	protected static final int INDEX_INSIDE_LOOP = 1;
	protected static final int INDEX_EXIT_LOOP = 2;

	protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;

    protected int stateOfError = 0;

	public TADForLoop(	int _x, 
						int _y,
						int _minX,
						int _maxX,
						int _minY,
						int _maxY,
						boolean _pos,
						TGComponent _father,
						TDiagramPanel _tdp ) {
		super( _x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp );

		width = 30;
        height = 20;
        minWidth = 30;

        moveable = true;
        editable = true;
        removable = true;
        
        myImageIcon = IconManager.imgic912;
	}

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+width, y+height/2, x+width +lineLength, y+height/2, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }
	
	@Override
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.TGComponent#acceptForward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptForward( ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			final TGConnectingPoint insideLoopPoint = getInsideLoopConnectingPoint();
			
			if ( insideLoopPoint != null ) {
				insideLoopPoint.acceptForward( visitor );
			}
			
			final TGConnectingPoint exitPoint = getExitLoopConnectingPoint();
			
			if ( exitPoint != null ) {
				exitPoint.acceptForward( visitor );
			}
		}
    }
    
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.TGComponent#acceptBackward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptBackward( ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			final TGConnectingPoint enterLoopPoint = getEnterLoopConnectingPoint();
			
			if ( enterLoopPoint != null ) {
				enterLoopPoint.acceptBackward( visitor );
			}
		}
    }
	
    /**
     * Issue #69
     * @param _enabled
     */
    @Override
    public void setEnabled( final boolean _enabled ) {
    	super.setEnabled( _enabled );
    	
    	getInsideLoopConnectingPoint().acceptForward( new EnablingADBranchVisitor( _enabled ) );
    }

	public TGConnectingPoint getEnterLoopConnectingPoint() {
		return connectingPoint[ INDEX_ENTER_LOOP ];
	}

	public TGConnectingPoint getInsideLoopConnectingPoint() {
		return connectingPoint[ INDEX_INSIDE_LOOP ];
	}

	public TGConnectingPoint getExitLoopConnectingPoint() {
		return connectingPoint[ INDEX_EXIT_LOOP ];
	}
}

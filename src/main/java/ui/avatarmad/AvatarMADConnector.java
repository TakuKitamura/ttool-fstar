package ui.avatarmad;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import myutil.GraphicLib;
import ui.ForwardComponentsEnabledVisitor;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGConnectingPoint;
import ui.TGConnectorWithCommentConnectionPoints;
import ui.ad.CanBeDisabledADElementVisitor;

public abstract class AvatarMADConnector extends TGConnectorWithCommentConnectionPoints {

    int w, h;

    public AvatarMADConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2,
            Vector<Point> _listPoint) {
        super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
    }

    @Override
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {

        // g.drawLine(x1, y1, x2, y2);
        GraphicLib.dashedArrowWithLine(g, 1, 1, 0, x1, y1, x2, y2, false);

        // Indicate semantics

        Font f = g.getFont();
        Font old = f;
        if (f.getSize() != tdp.getFontSize()) {
            f = f.deriveFont((float) tdp.getFontSize());
            g.setFont(f);
        }

        w = g.getFontMetrics().stringWidth(value);
        h = g.getFontMetrics().getHeight();
        g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
        g.setFont(old);
    }

    @Override
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, (p1.getX() + p2.getX() - w) / 2, (p1.getY() + p2.getY()) / 2 - h, w, h)) {
            return this;
        }
        return null;
    }

    /**
     * Issue #69
     * 
     * @return
     */
    @Override
    public boolean canBeDisabled() {
        // if ( p2 != null && p2.getFather() instanceof AvatarSMDStopState ) {
        // return false;
        // }

        final CanBeDisabledADElementVisitor visitor = new CanBeDisabledADElementVisitor();
        acceptBackward(visitor);

        return visitor.isCanBeDisabled();
    }

    /**
     * Issue #69
     * 
     * @return
     */
    @Override
    public boolean isEnabled(boolean checkBranch) {
        if (checkBranch && p2 != null) {
            final ForwardComponentsEnabledVisitor visitor = new ForwardComponentsEnabledVisitor();
            p2.acceptForward(visitor);

            return visitor.isEnabled();
        }

        return super.isEnabled();
    }
}

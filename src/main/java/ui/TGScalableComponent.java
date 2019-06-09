package ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Issue #31
 * @author dblouin
 *
 */
public abstract class TGScalableComponent extends TGComponent implements ScalableTGComponent {
	
	protected boolean rescaled;
	
	protected double oldScaleFactor;

	protected boolean displayText;
	protected int textX; // border for ports
	protected double dtextX;
	protected int textY;
	protected double dtextY;
	protected int arc;
	protected double darc;

	protected int lineLength;
	protected double dLineLength;
    protected int linebreak;
	protected double dLinebreak;
    
	protected double dx = 0, dy = 0, dwidth, dheight, dMaxWidth, dMaxHeight, dMinWidth, dMinHeight;

	public TGScalableComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
			TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
		
		rescaled = false;
		oldScaleFactor = tdp.getZoom();

		textX = 15;
        textY = 15;

        arc = 5;
        
        lineLength = 5;
        linebreak = 10;
    	displayText = true;
	}

	public static int scale( 	final int value,
								final double factor ) {
		return (int) ( value * factor );
	}

	protected int scale( final int value ) {
		return scale( value, oldScaleFactor );
	}

    protected void initScaling(int w, int h) {
        oldScaleFactor = tdp.getZoom();

        dx = 0;
        dy = 0;
       
        dtextX = textX * oldScaleFactor;
        textX = (int) dtextX;
        dtextX = dtextX - textX;

        dtextY = textY * oldScaleFactor;
        textY = (int) dtextY;
        dtextY = dtextY - textY;

        darc = arc * oldScaleFactor;
        arc = (int) darc;
        darc = darc - arc;

        dwidth = w * oldScaleFactor;
        width = (int)dwidth;
        dwidth = dwidth - width;

        dheight = h * oldScaleFactor;
        height = (int)(dheight);
        dheight = dheight - height;

        darc = arc * oldScaleFactor;
        arc = (int)(darc);
        darc = darc - arc;

        dLineLength = lineLength * oldScaleFactor;
        lineLength = (int) dLineLength;
        dLineLength = dLineLength - lineLength;
        
        dLinebreak = linebreak * oldScaleFactor;
        linebreak = (int) dLinebreak;
        dLinebreak = dLinebreak - linebreak;

        dMinWidth = minWidth * oldScaleFactor;
        dMinHeight = minHeight * oldScaleFactor;
        dMaxWidth = defMaxWidth * oldScaleFactor;
        dMaxHeight = defMaxHeight * oldScaleFactor;

        maxWidth = defMaxWidth;
        maxHeight = defMaxHeight;

        dMinWidth = dMinWidth -minWidth;
        dMinHeight = dMinHeight - minHeight;
        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;

        if (father == null) {
            minX = (int)(tdp.getMinX()/tdp.getZoom());
            maxX = (int)(tdp.getMaxX()/tdp.getZoom());
            minY = (int)(tdp.getMinY()/tdp.getZoom());
            maxY = (int)(tdp.getMaxY()/tdp.getZoom());
        }

        rescaled = true;
    }

    @Override
    public void rescale( final double scaleFactor ) {
        rescaled = true;
        
        final double factor = scaleFactor / oldScaleFactor;

        dwidth = (width + dwidth) * factor;
        dheight = (height + dheight) * factor;
        dx = (dx + x) * factor;
        dy = (dy + y) * factor;
        dMinWidth = (minWidth + dMinWidth) * factor;
        dMinHeight = (minHeight + dMinHeight) * factor;//oldScaleFactor * scaleFactor;
        dMaxWidth = (maxWidth + dMaxWidth) * factor;//oldScaleFactor * scaleFactor;
        dMaxHeight = (maxHeight + dMaxHeight) * factor;//oldScaleFactor * scaleFactor;

        width = (int)(dwidth);
        dwidth = dwidth - width;
        height = (int)(dheight);
        dheight = dheight - height;
        minWidth = (int)(dMinWidth);
        minHeight = (int)(dMinHeight);
        maxWidth = (int)(dMaxWidth);
        maxHeight = (int)(dMaxHeight);

        dMinWidth = dMinWidth - minWidth;
        dMinHeight = dMinHeight - minHeight;
        dMaxWidth = dMaxWidth - maxWidth;
        dMaxHeight = dMaxHeight - maxHeight;
        x = (int)(dx);
        dx = dx - x;
        y = (int)(dy);
        dy = dy - y;
        
        dtextX = (textX + dtextX) * factor;
        textX = (int) (dtextX);
        dtextX = dtextX - textX;

        dtextY = (textY + dtextY) * factor;
        textY = (int) (dtextY);
        dtextY = dtextY - textY;

        darc = (arc + darc) * factor;
        arc = (int) (darc);
        darc = darc - arc;
        
        dLineLength = (lineLength + dLineLength) * factor;
        lineLength = (int) dLineLength;
        dLineLength = dLineLength - lineLength;
        
        dLinebreak = (linebreak + dLinebreak) * factor;
        linebreak = (int) dLinebreak;
        dLinebreak = dLinebreak - linebreak;
        
        // Issue #81: We also need to update max coordinate values
        maxX *= factor;
        maxY *= factor;

        oldScaleFactor = scaleFactor;

        if (father != null) {
            // Must rescale my zone...
            resizeWithFather();
        } else {
            minX = (int)(tdp.getMinX()/tdp.getZoom());
            maxX = (int)(tdp.getMaxX()/tdp.getZoom());
            minY = (int)(tdp.getMinY()/tdp.getZoom());
            maxY = (int)(tdp.getMaxY()/tdp.getZoom());
        }

        setMoveCd(x, y, true);

        //TraceManager.addDev("x=" + x + " y=" + y + " width=" + width + " height=" + height);

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ScalableTGComponent) {
                ((ScalableTGComponent)tgcomponent[i]).rescale(scaleFactor);
            }
        }
        
        hasBeenResized();
    }

    /**
     * Issue #31
     * @return
     */
    @Override
    protected int getReachabilityMargin() {
    	return scale( super.getReachabilityMargin() );
    }

    /**
     * Issue #31
     * @return
     */
    @Override
    protected int getLivenessMargin() {
    	return scale( super.getLivenessMargin() );
    }

    /**
     * Issue #31
     * @return
     */
    @Override
    protected int getExclusionMargin() {
    	return scale( super.getExclusionMargin() );
    }

    /**
     * Issue #31
     * @return
     */
    protected int getUnknownMargin() {
    	return scale( super.getUnknownMargin() );
    }

    /**
     * Issue #31: Shared this check
     * @param graphics
     */
    protected int checkWidth( final Graphics graphics ) {
    	return checkWidth( graphics, value );
    }
    
    protected int checkWidth( 	final Graphics graphics,
    							final String text ) {
        // Issue #31: This is just to increase the width in case the actual width is not enough to display the text. 
        // It is typically used when a component is created
    	final int textWidth = graphics.getFontMetrics().stringWidth( text );
        final int textWidthBorder = Math.max( minWidth, textWidth + 2 * textX );
        
        if ( textWidthBorder > width & !tdp.isScaled() ) {
            setCd(x - ( textWidthBorder - width ) / 2 , y);
            width = textWidthBorder;
        }
        
        return textWidth;
    }
    
    protected Image scale( final Image image ) {
    	if ( image == null ) {
    		return image;
    	}
    	
    	return scale( image, scale( image.getWidth( null ) ) );
    }
    
    protected Image scale( 	final Image image,
    						final int width ) {
    	return new ImageIcon( image.getScaledInstance( width, - 1, Image.SCALE_SMOOTH ) ).getImage();
    }
}

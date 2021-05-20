package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import ui.util.IconManager;

/**
 * Issue #31
 * 
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

  // Issue #31:
  /**
   * Verifies that the text is readable if its not it shall not be drawn fontsize
   * 8 is the limit that is set to be "readable". It can be changed
   * 
   * @param g
   * @return
   */
  protected boolean isTextReadable(Graphics g) {
    int textSize = g.getFont().getSize();
    return textSize > 7;
  }

  /**
   * Gets the center of the rectangle/box
   * 
   * @param g
   * @param str
   * @return
   */
  protected int getCenter(Graphics g, String str) {
    int w = g.getFontMetrics().stringWidth(str);
    return x + (width - w) / 2;
  }

  /**
   * Verifies that the text is small enough to be drawn into the box If The actual
   * box does not have an icon, make sure call the function as following:
   * canTextGoInTheBox(g, fontSize, text, 0);
   * 
   * @param g
   * @param fontSize
   * @param text
   * @param iconSize
   * @return a boolean indicating that the txt can or not be drawn
   */
  protected boolean canTextGoInTheBox(Graphics g, int fontSize, String text, int iconSize) {
    int txtWidth = g.getFontMetrics().stringWidth(text) + (textX * 2);
    int spaceTakenByIcon = iconSize + textX;
    return (fontSize + (textY * 2) < height) // enough space in height
        && (txtWidth + spaceTakenByIcon < width) // enough space in width
    ;
  }

  /**
   * Draw a box of two rectangle: one for the title and one for the content of the
   * box lineHeight defines the height of the first box
   * 
   * @param g
   * @param lineHeight
   */
  protected void drawDoubleRectangleBoxType(Graphics g, int lineHeight, Color cbefore, Color cafter) {
    // Rectangle
    drawSimpleRectangle(g);// g.drawRect(x, y, width, height);
    g.drawLine(x, y + lineHeight, x + width, y + lineHeight);

    // Filling
    g.setColor(cbefore); // for example: ColorManager.AVATAR_ASSUMPTION_TOP
    g.fillRect(x + 1, y + 1, width - 1, lineHeight - 1);
    g.setColor(cafter);
    g.fillRect(x + 1, y + 1 + lineHeight, width - 1, height - 1 - lineHeight);
    ColorManager.setColor(g, getState(), 0);
  }

  /**
   * Draw one box without any color filling
   * 
   * @param g
   */
  protected void drawSimpleRectangle(Graphics g) {
    g.drawRect(x, y, width, height);
  }

  /**
   * used to draw the icon "icon" at position x = x + width - scale(iconsize) -
   * borders and y = y + borders
   * 
   * @param g
   * @param icon
   * @param iconSize
   */
  protected void drawIcon(Graphics g, Image icon, int iconSize) {
    if (!isTextReadable(g))
      return;
    int borders = scale(3);
    g.drawImage(scale(icon), x + width - scale(iconSize) - borders, y + borders, Color.yellow, null);
  }

  protected void drawImageWithCheck(Graphics g, Image image, int xpos, int ypos) {
    if (!isTextReadable(g))
      return;
    g.drawImage(scale(image), xpos, ypos, null);
  }

  /**
   * Draw string if text is readable
   * 
   * @param g
   * @param s
   */
  protected void drawSingleString(Graphics g, String s, int xpos, int ypos) {
    if (!isTextReadable(g))
      return;
    // int currentFontSize = setCurrentFontSize ? g.getFont().getSize() : 0;
    g.drawString(s, xpos, ypos);
  }

  /**
   * Same as drawSingleString
   * 
   * @param g
   * @param s
   * @param xpos
   * @param ypos
   * @param maxWidth
   * @param pos
   */
  protected void drawSingleLimitedString(Graphics g, String s, int xpos, int ypos, int maxWidth, int pos) {
    if (!isTextReadable(g))
      return;
    drawLimitedString(g, s, xpos, ypos, maxWidth, pos);
  }

  /**
   * draw two string one under another:
   * 
   * @param g
   * @param topText
   * @param bottomText
   */
  protected void drawDoubleLimitedString(Graphics g, String topText, String bottomText) {
    if (!isTextReadable(g))
      return;

    Font f = g.getFont();

    int currentFontSize = f.getSize();
    g.setFont(f.deriveFont(Font.BOLD));
    drawLimitedString(g, topText, x, y + currentFontSize + textY, width, 1);
    g.setFont(f.deriveFont(Font.PLAIN));
    drawLimitedString(g, bottomText, y, y + currentFontSize * 2 + textY, width, 1);
  }

  /**
   * draw two string one under another:
   * 
   * @param g
   * @param topText
   * @param bottomText
   */
  protected void drawDoubleString(Graphics g, String topText, String bottomText) {
    if (!isTextReadable(g))
      return;

    Font f = g.getFont();

    int currentFontSize = f.getSize();
    g.setFont(f.deriveFont(Font.BOLD));
    g.drawString(topText, getCenter(g, topText), y + currentFontSize + textY);
    g.setFont(f.deriveFont(Font.PLAIN));
    g.drawString(bottomText, getCenter(g, bottomText), y + currentFontSize * 2 + textY);
  }
  // END Issue #31

  /**
   * Scale from a value and a factor
   * 
   * @param value
   * @param factor
   * @return scaling value of param: value and factor
   */
  public static int scale(final int value, final double factor) {
    return (int) (value * factor);
  }

  /**
   * Scale from a value and the oldScaleFactor previously saved
   * 
   * @param value
   * @return scaling value of param: value and oldScaleFactor
   */
  protected int scale(final int value) {
    return scale(value, oldScaleFactor);
  }

  /**
   * init the scaling values
   * 
   * @param w (width)
   * @param h (height)
   */
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
    width = (int) dwidth;
    dwidth = dwidth - width;

    dheight = h * oldScaleFactor;
    height = (int) (dheight);
    dheight = dheight - height;

    darc = arc * oldScaleFactor;
    arc = (int) (darc);
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

    dMinWidth = dMinWidth - minWidth;
    dMinHeight = dMinHeight - minHeight;
    dMaxWidth = dMaxWidth - maxWidth;
    dMaxHeight = dMaxHeight - maxHeight;

    if (father == null) {
      minX = (int) (tdp.getMinX() / tdp.getZoom());
      maxX = (int) (tdp.getMaxX() / tdp.getZoom());
      minY = (int) (tdp.getMinY() / tdp.getZoom());
      maxY = (int) (tdp.getMaxY() / tdp.getZoom());
    }

    rescaled = true;
  }

  /**
   * Rescale with the help of a scaleFactor
   * 
   * @param scaleFactor
   * 
   */
  @Override
  public void rescale(final double scaleFactor) {
    rescaled = true;

    final double factor = scaleFactor / oldScaleFactor;

    dwidth = (width + dwidth) * factor;
    dheight = (height + dheight) * factor;
    dx = (dx + x) * factor;
    dy = (dy + y) * factor;
    dMinWidth = (minWidth + dMinWidth) * factor;
    dMinHeight = (minHeight + dMinHeight) * factor;// oldScaleFactor * scaleFactor;
    dMaxWidth = (maxWidth + dMaxWidth) * factor;// oldScaleFactor * scaleFactor;
    dMaxHeight = (maxHeight + dMaxHeight) * factor;// oldScaleFactor * scaleFactor;

    width = (int) (dwidth);
    dwidth = dwidth - width;
    height = (int) (dheight);
    dheight = dheight - height;
    minWidth = (int) (dMinWidth);
    minHeight = (int) (dMinHeight);
    maxWidth = (int) (dMaxWidth);
    maxHeight = (int) (dMaxHeight);

    dMinWidth = dMinWidth - minWidth;
    dMinHeight = dMinHeight - minHeight;
    dMaxWidth = dMaxWidth - maxWidth;
    dMaxHeight = dMaxHeight - maxHeight;
    x = (int) (dx);
    dx = dx - x;
    y = (int) (dy);
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
      minX = (int) (tdp.getMinX() / tdp.getZoom());
      maxX = (int) (tdp.getMaxX() / tdp.getZoom());
      minY = (int) (tdp.getMinY() / tdp.getZoom());
      maxY = (int) (tdp.getMaxY() / tdp.getZoom());
    }

    setMoveCd(x, y, true);

    // TraceManager.addDev("x=" + x + " y=" + y + " width=" + width + " height=" +
    // height);

    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ScalableTGComponent) {
        ((ScalableTGComponent) tgcomponent[i]).rescale(scaleFactor);
      }
    }

    hasBeenResized();
  }

  /**
   * Issue #31
   * 
   * @return
   */
  @Override
  protected int getReachabilityMargin() {
    return scale(super.getReachabilityMargin());
  }

  /**
   * Issue #31
   * 
   * @return
   */
  @Override
  protected int getLivenessMargin() {
    return scale(super.getLivenessMargin());
  }

  /**
   * Issue #31
   * 
   * @return
   */
  @Override
  protected int getExclusionMargin() {
    return scale(super.getExclusionMargin());
  }

  /**
   * Issue #31
   * 
   * @return
   */
  protected int getUnknownMargin() {
    return scale(super.getUnknownMargin());
  }

  /**
   * Issue #31: Shared this check
   * 
   * @param graphics
   */
  protected int checkWidth(final Graphics graphics) {
    return checkWidth(graphics, value);
  }

  /**
   * Issue #31: Check the Width, increase the width in case the actual width is
   * not enough to display the text. Used when a component is created
   * 
   * @param graphics
   * @param text
   * @return textWidth
   */
  protected int checkWidth(final Graphics graphics, final String text) {
    final int textWidth = graphics.getFontMetrics().stringWidth(text);
    final int textWidthBorder = Math.max(minWidth, textWidth + 2 * textX);

    if (textWidthBorder > width & !tdp.isScaled()) {
      setCd(x - (textWidthBorder - width) / 2, y);
      width = textWidthBorder;
    }

    return textWidth;
  }

  /**
   * Scale an image directly
   * 
   * @param image
   * @return the scaled image
   */
  protected Image scale(final Image image) {
    if (image == null) {
      return image;
    }

    return scale(image, scale(image.getWidth(null)));
  }

  /**
   * Scale an image directly with a custom width
   * 
   * @param image
   * @param width
   * @return the scaled image
   */
  protected Image scale(final Image image, final int width) {
    return new ImageIcon(image.getScaledInstance(width, -1, Image.SCALE_SMOOTH)).getImage();
  }
}

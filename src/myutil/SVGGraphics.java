/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class SVGGraphics
   *
   * Creation: 29/08/2012
   * @version 1.0 29/08/2012
   * @author Ludovic APVRILLE
   * @see
   */

package myutil;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;



public class SVGGraphics extends Graphics2D {
    private String svgvalue;
    private Graphics2D graphics;

    public  SVGGraphics(Graphics2D _graphics) {
        super();
        svgvalue = "";
        graphics = _graphics;
    }

    /*public void setColor(Color color) {
      currentColor = color;
      }

      public Color getColor() {
      return currentColor;
      }

      public void setFont(Font f) {
      currentFont = f;
      }

      public Font getFont() {
      return currentFont;
      }

      public void setFontMetrics(FontMetrics fm) {
      currentFontMetrics = fm;
      }

      public FontMetrics getFontMetrics() {
      return currentFontMetrics;
      }*/



    // My own functions

    public String getRGBHexaColor() {
        String rgb = Integer.toHexString(graphics.getColor().getRGB());
        return "#" + rgb.substring(2, rgb.length());
    }

    public String makeArg(String arg, int value) {
        return makeArg(arg, "" + value);
    }

    public String makeArg(String arg, String value) {
        return " " + arg + "=\"" + value + "\"";
    }

    public String makeDesc(String title, String content) {
        return "<" + title + " " + content + "/>\n";
    }

    public String makeDescWithValue(String title, String content, String value) {
        return "<" + title + " " + content + ">" + value + "</" + title + ">\n";
    }

    public String getFontInfos(boolean putColor) {
        Font f = graphics.getFont();
        String s = "font-size:" + f.getSize() + "; font-family:" + f.getFontName();
        if ((f.getStyle() == Font.BOLD) || (f.getStyle() == Font.BOLD+Font.ITALIC)) {
            s+= "; font-weight=bold";
        }
        if ((f.getStyle() == Font.ITALIC) || (f.getStyle() == Font.BOLD+Font.ITALIC)) {
            s+= "; font-style=italic";
        }

        if (putColor) {
            s+= "; fill="+getRGBHexaColor();
        }

        return s;

    }


    public void drawRect(int x, int y, int width, int height) {
        //TraceManager.addDev("Drawing svg rect");
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width);
        s += makeArg("height", height);
        s += makeArg("fill", "none");
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);


        svgvalue += makeDesc("rect", s);
    }

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width);
        s += makeArg("height", height);
        s += makeArg("fill", "none");
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);

        svgvalue += makeDesc("rect", s);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        //TraceManager.addDev("Drawing svg 3D line");
        String s = makeArg("x1", x1);
        s += makeArg("y1", y1);
        s += makeArg("x2", x2);
        s += makeArg("y2", y2);
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);
        s += makeArg("fill", "none");

        svgvalue += makeDesc("line", s);
    }

    public void drawOval(int x, int y, int width, int height) {
        String s = makeArg("cx", x+width/2);
        s += makeArg("cy", y+height/2);
        s += makeArg("rx", width/2);
        s += makeArg("ry", height/2);
        s += makeArg("fill", "none");
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);

        svgvalue += makeDesc("ellipse", s);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        String s = "";
        for(int i=0; i<nPoints; i++) {
            s+=xPoints[i]+"," + yPoints[i] + " ";
        }
        s = makeArg("points", s);
        s += makeArg("fill", "none");
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);

        svgvalue += makeDesc("polygon", s);

    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width);
        s += makeArg("height", height);
        s += makeArg("rx", arcWidth);
        s += makeArg("ry", arcHeight);
        s += makeArg("fill", "none");
        s += makeArg("stroke", getRGBHexaColor());
        s += makeArg("stroke-width", 1);

        svgvalue += makeDesc("rect", s);
    }

    public  void drawString(String str, int x, int y){
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("style", getFontInfos(true));




        svgvalue += makeDescWithValue("text", s, Conversion.transformToXMLString(str));
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width-1);
        s += makeArg("height", height-1);
        s += makeArg("fill", getRGBHexaColor());

        svgvalue += makeDesc("rect", s);
    }

    public void fillOval(int x, int y, int width, int height) {
        String s = makeArg("cx", x+width/2);
        s += makeArg("cy", y+height/2);
        s += makeArg("rx", width/2);
        s += makeArg("ry", height/2);
        s += makeArg("fill", getRGBHexaColor());

        svgvalue += makeDesc("ellipse", s);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        String s = "";
        for(int i=0; i<nPoints; i++) {
            s+=xPoints[i]+"," + yPoints[i] + " ";
        }
        s = makeArg("points", s);
        s += makeArg("fill", getRGBHexaColor());

        svgvalue += makeDesc("polygon", s);

    }

    public void fillRect(int x, int y, int width, int height) {
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width-1);
        s += makeArg("height", height-1);
        s += makeArg("fill", getRGBHexaColor());

        svgvalue += makeDesc("rect", s);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        String s = makeArg("x", x);
        s += makeArg("y", y);
        s += makeArg("width", width);
        s += makeArg("height", height);
        s += makeArg("rx", arcWidth);
        s += makeArg("ry", arcHeight);
        s += makeArg("fill", getRGBHexaColor());

        svgvalue += makeDesc("rect", s);
    }

    public String getSVGString() {
        return svgvalue;
    }

    // From Graphics2D

    public void addRenderingHints(Map<?,?> hints) {}

    public void clip(Shape s) {}

    public void draw(Shape s) {}

    public void drawGlyphVector(GlyphVector g, float x, float y) {}

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {}

    public  boolean     drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return true;
    }

    public  void drawRenderableImage(RenderableImage img, AffineTransform xform){}

    public  void drawRenderedImage(RenderedImage img, AffineTransform xform){}

    public  void drawString(AttributedCharacterIterator iterator, float x, float y){}

    public  void drawString(AttributedCharacterIterator iterator, int x, int y){}

    public  void drawString(String str, float x, float y){}



    public  void fill(Shape s){}



    public Color getBackground() {
        return graphics.getBackground();
    }

    public Composite getComposite() {
        return graphics.getComposite();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return graphics.getDeviceConfiguration();
    }

    public FontRenderContext getFontRenderContext() {
        return graphics.getFontRenderContext();
    }

    public Paint getPaint() {
        return graphics.getPaint();
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return graphics.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        return graphics.getRenderingHints();
    }

    public Stroke getStroke() {
        return graphics.getStroke();
    }

    public AffineTransform getTransform() {
        return graphics.getTransform();
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return graphics.hit(rect, s, onStroke);
    }

    public void rotate(double theta) {
    }

    public void rotate(double theta, double x, double y) {
    }

    public void scale(double sx, double sy) {
    }

    public void setBackground(Color color) {
        graphics.setBackground(color);
    }

    public void setComposite(Composite comp) {
        graphics.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        graphics.setPaint(paint);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        graphics.setRenderingHint(hintKey, hintValue);
    }

    public void setRenderingHints(Map<?,?> hints) {
        graphics.setRenderingHints(hints);
    }

    public void setStroke(Stroke s) {
        graphics.setStroke(s);
    }


    public void setTransform(AffineTransform Tx) {
        graphics.setTransform(Tx);
    }

    public void shear(double shx, double shy) {
    }

    public void transform(AffineTransform Tx) {
    }

    public void translate(double tx, double ty) {
    }

    public void translate(int x, int y) {
    }


    // Graphics

    public void clearRect(int x, int y, int width, int height) {}

    public void clipRect(int x, int y, int width, int height) {}

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {}

    public Graphics create() {
        return graphics.create();
    }

    public void dispose() {}

    public  void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return true;
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return true;
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return true;
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return true;
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return true;
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return true;
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    public Shape getClip() {
        return graphics.getClip();
    }

    public Rectangle getClipBounds() {
        return graphics.getClipBounds();
    }

    public Rectangle getClipBounds(Rectangle r) {
        return graphics.getClipBounds(r);
    }

    public Color getColor() {
        return graphics.getColor();
    }

    public Font getFont() {
        return graphics.getFont();
    }

    public FontMetrics getFontMetrics() {
        return graphics.getFontMetrics();
    }

    public FontMetrics getFontMetrics(Font f) {
        return graphics.getFontMetrics(f);
    }

    public void setClip(int x, int y, int width, int height) {
        graphics.setClip(x, y, width, height);
    }

    public void setClip(Shape clip) {
        graphics.setClip(clip);
    }

    public void setColor(Color c) {
        graphics.setColor(c);
    }

    public void setFont(Font font) {
        graphics.setFont(font);
    }

    public void setPaintMode() {
        graphics.setPaintMode();
    }

    public void setXORMode(Color c1) {
        graphics.setXORMode(c1);
    }
















}

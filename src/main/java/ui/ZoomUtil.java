/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui;

/**
 * Class ZoomGraphics Working with zooms on graphics Creation: 19/11/2004
 * 
 * @version 1.0 19/11/2004
 * @author Ludovic APVRILLE
 */
public class ZoomUtil {
  // private Graphics g;
  // private double zoom;

  private ZoomUtil(/* Graphics _g, double _zoom */) {
    // g = _g;
    // zoom = _zoom;
  }
  //
  // public Graphics getGraphics() {
  // return g;
  // }
  //
  // public void drawRoundRect(int x, int y, int w, int h, int arc1, int arc2) {
  // g.drawRoundRect((int)(x*zoom), (int)(y*zoom), (int)(w*zoom), (int)(h*zoom),
  // arc1, arc2);
  // }
  //
  // public void drawLine(int x1, int y1, int x2, int y2) {
  // g.drawLine((int)(x1*zoom), (int)(y1*zoom), (int)(x2*zoom), (int)(y2*zoom));
  // }
  //
  // public void drawString(String value, int x, int y) {
  // g.drawString(value, (int)(x*zoom) , (int)(y*zoom));
  // }

  // public static void rescale( final TGComponent component,
  // final double scaleFactor ) {
  // //rescaled = true;
  //
  // //final double factor = scaleFactor / oldScaleFactor;
  //
  // component.dwidth = (component.getWidth() + component.dwidth) * scaleFactor;//
  // oldScaleFactor * scaleFactor;
  // component.dheight = (component.getHeight() + component.dheight) *
  // scaleFactor;// oldScaleFactor * scaleFactor;
  // component.dx = (component.dx + component.getX()) * scaleFactor;//
  // oldScaleFactor * scaleFactor;
  // component.dy = (component.dy + component.getY()) * scaleFactor;//
  // oldScaleFactor * scaleFactor;
  // component.dMinWidth = (component.getMinWidth() + component.dMinWidth) *
  // scaleFactor;// oldScaleFactor * scaleFactor;
  // component.dMinHeight = (component.getMinHeight() + component.dMinHeight) *
  // scaleFactor;// oldScaleFactor * scaleFactor;
  // component.dMaxWidth = (component.getMaxWidth() + component.dMaxWidth) *
  // scaleFactor;// oldScaleFactor * scaleFactor;
  // component.dMaxHeight = (component.getMaxHeight() + component.dMaxHeight) *
  // scaleFactor;// oldScaleFactor * scaleFactor;
  //
  // component.width = (int)(component.dwidth);
  // component.dwidth = component.dwidth - component.getWidth();
  // component.height = (int)(component.dheight);
  // component.dheight = component.dheight - component.getHeight();
  // component.minWidth = (int)(component.dMinWidth);
  // component.minHeight = (int)(component.dMinHeight);
  // component.maxWidth = (int)(component.dMaxWidth);
  // component.maxHeight = (int)(component.dMaxHeight);
  //
  // component.dMinWidth = component.dMinWidth - component.getMinWidth();
  // component.dMinHeight = component.dMinHeight - component.getMinHeight();
  // component.dMaxWidth = component.dMaxWidth - component.getMaxWidth();
  // component.dMaxHeight = component.dMaxHeight - component.getMaxHeight();
  // component.x = (int)(component.dx);
  // component.dx = component.dx - component.getX();
  // component.y = (int)(component.dy);
  // component.dy = component.dy - component.getY();
  //
  //
  // //oldScaleFactor = scaleFactor;
  //
  // if ( component.getFather() != null) {
  //
  // // Issue #81: We also need to update max coordinate values
  // component.maxX *= scaleFactor;
  // component.maxY *= scaleFactor;
  //
  // // Must rescale my zone...
  // component.resizeWithFather();
  // }
  // else {
  // component.minX = (int)(tdp.getMinX()/tdp.getZoom());
  // component.maxX = (int)(tdp.getMaxX()/tdp.getZoom());
  // component.minY = (int)(tdp.getMinY()/tdp.getZoom());
  // component.maxY = (int)(tdp.getMaxY()/tdp.getZoom());
  //
  // }
  //
  // component.setMoveCd(component.x, component.y, true);
  //
  // for( final TGComponent subCompo : component.tgcomponent ) {
  // if ( subCompo instanceof ScalableTGComponent ) {
  // ( (ScalableTGComponent) subCompo ).rescale( scaleFactor );
  // }
  // }
  // }
}

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

package ui.eln;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.window.*;
import javax.swing.*;
import java.awt.*;

/**
 * Class ELNClusterPortDE Composite port. To be used in ELN diagrams Creation:
 * 03/08/2018
 * 
 * @version 1.0 03/08/2018
 * @author Irina Kit Yan LEE
 */

public class ELNClusterPortDE extends TGCScalableWithInternalComponent
    implements SwallowedTGComponent, LinkedReference {
  protected Color myColor;
  protected int orientation;
  private int maxFontSize = 14;
  private int minFontSize = 4;
  private int currentFontSize = -1;
  protected int oldx, oldy;
  protected int currentOrientation = GraphicLib.NORTH;

  private int textX = 15;
  private double dtextX = 0.0;
  protected int decPoint = 3;

  private String type;
  private String origin;

  public ELNClusterPortDE(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
      TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    initScaling(20, 20);

    dtextX = textX * oldScaleFactor;
    textX = (int) dtextX;
    dtextX = dtextX - textX;

    minWidth = 1;
    minHeight = 1;

    initConnectingPoint(1);

    addTGConnectingPointsComment();

    nbInternalTGComponent = 0;

    moveable = true;
    editable = true;
    removable = true;
    userResizable = false;

    value = "";
    type = "bool";
    origin = "in";
  }

  public void initConnectingPoint(int nb) {
    nbConnectingPoint = nb;
    connectingPoint = new TGConnectingPoint[nb];
    connectingPoint[0] = new ELNConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, "");
  }

  public Color getMyColor() {
    return myColor;
  }

  public void internalDrawing(Graphics g) {
    Font f = g.getFont();
    Font fold = f;

    if ((x != oldx) | (oldy != y)) {
      manageMove(g, f);
      oldx = x;
      oldy = y;
    } else {
      int attributeFontSize = this.currentFontSize * 5 / 6;
      int w = g.getFontMetrics().stringWidth(value);
      int h = g.getFontMetrics().getAscent();
      g.setFont(f.deriveFont((float) attributeFontSize));
      g.setFont(f);
      g.setFont(f.deriveFont(Font.PLAIN));
      switch (currentOrientation) {
        case GraphicLib.NORTH:
          g.drawString(value, x + width + width / 2, y);
          break;
        case GraphicLib.WEST:
          g.drawString(value, x - w, y + height + height / 2 + h);
          break;
        case GraphicLib.SOUTH:
          g.drawString(value, x + width + width / 2, y + height + h);
          break;
        case GraphicLib.EAST:
        default:
          g.drawString(value, x + width, y + height + height / 2 + h);
      }
    }

    if (this.rescaled && !this.tdp.isScaled()) {
      this.rescaled = false;
      int maxCurrentFontSize = Math.max(0, Math.min(this.height, (int) (this.maxFontSize * this.tdp.getZoom())));
      f = f.deriveFont((float) maxCurrentFontSize);

      while (maxCurrentFontSize > (this.minFontSize * this.tdp.getZoom() - 1)) {
        if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
          break;
        }
        maxCurrentFontSize--;
        f = f.deriveFont((float) maxCurrentFontSize);
      }

      if (this.currentFontSize < this.minFontSize * this.tdp.getZoom()) {
        maxCurrentFontSize++;
        f = f.deriveFont((float) maxCurrentFontSize);
      }
      g.setFont(f);
      this.currentFontSize = maxCurrentFontSize;
    } else {
      f = f.deriveFont(this.currentFontSize);
    }

    Color c = g.getColor();
    g.setColor(Color.WHITE);
    g.fillRect(x, y, width, height);
    g.setColor(c);
    g.drawRect(x, y, width, height);
    g.setFont(fold);
  }

  public void manageMove(Graphics g, Font f) {
    if (father != null) {
      Point p = GraphicLib.putPointOnRectangle(x + (width / 2), y + (height / 2), father.getX(), father.getY(),
          father.getWidth(), father.getHeight());

      x = p.x - width / 2;
      y = p.y - height / 2;

      setMoveCd(x, y);

      int orientation = GraphicLib.getCloserOrientation(x + (width / 2), y + (height / 2), father.getX(), father.getY(),
          father.getWidth(), father.getHeight());

      int attributeFontSize = this.currentFontSize * 5 / 6;
      int w = g.getFontMetrics().stringWidth(value);
      int h = g.getFontMetrics().getAscent();
      g.setFont(f.deriveFont((float) attributeFontSize));
      g.setFont(f);
      g.setFont(f.deriveFont(Font.PLAIN));

      switch (orientation) {
        case GraphicLib.NORTH:
          g.drawString(value, x + width + width / 2, y);
          break;
        case GraphicLib.WEST:
          g.drawString(value, x - w, y + height + height / 2 + h);
          break;
        case GraphicLib.SOUTH:
          g.drawString(value, x + width + width / 2, y + height + h);
          break;
        case GraphicLib.EAST:
        default:
          g.drawString(value, x + width, y + height + height / 2 + h);
      }

      if (orientation != currentOrientation) {
        setOrientation(orientation);
      }
    }
  }

  public void setOrientation(int orientation) {
    currentOrientation = orientation;
    double w0, h0;

    switch (orientation) {
      case GraphicLib.NORTH:
        w0 = 0.5;
        h0 = 1.0;
        break;
      case GraphicLib.WEST:
        w0 = 1.0;
        h0 = 0.5;
        break;
      case GraphicLib.SOUTH:
        w0 = 0.5;
        h0 = 0.0;
        break;
      case GraphicLib.EAST:
      default:
        w0 = 0.0;
        h0 = 0.5;
    }

    ((ELNConnectingPoint) connectingPoint[0]).setW(w0);
    ((ELNConnectingPoint) connectingPoint[0]).setH(h0);
  }

  public TGComponent isOnOnlyMe(int _x, int _y) {
    if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
      return this;
    }
    return null;
  }

  public int getType() {
    return TGComponentManager.ELN_CLUSTER_PORT_DE;
  }

  public void wasSwallowed() {
    myColor = null;
  }

  public void wasUnswallowed() {
    myColor = null;
    setFather(null);
    TDiagramPanel tdp = getTDiagramPanel();
    setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
  }

  public void resizeWithFather() {
    if ((father != null) && (father instanceof ELNCluster)) {
      setCdRectangle(0 - getWidth() / 2, father.getWidth() - (getWidth() / 2), 0 - getHeight() / 2,
          father.getHeight() - (getHeight() / 2));
      setMoveCd(x, y);
      oldx = -1;
      oldy = -1;
    }
  }

  public boolean editOnDoubleClick(JFrame frame) {
    JDialogELNClusterPortDE jde = new JDialogELNClusterPortDE(this);
    jde.setVisible(true);
    return true;
  }

  protected String translateExtraParam() {
    StringBuffer sb = new StringBuffer("<extraparam>\n");
    sb.append("<attributes name=\"" + value);
    sb.append("\" type=\"" + type);
    sb.append("\" origin=\"" + origin + "\"");
    sb.append("/>\n");
    sb.append("</extraparam>\n");
    return new String(sb);
  }

  public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
    try {
      NodeList nli;
      Node n1, n2;
      Element elt;

      String name, type, origin;

      for (int i = 0; i < nl.getLength(); i++) {
        n1 = nl.item(i);
        if (n1.getNodeType() == Node.ELEMENT_NODE) {
          nli = n1.getChildNodes();
          for (int j = 0; j < nli.getLength(); j++) {
            n2 = nli.item(j);
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
              elt = (Element) n2;
              if (elt.getTagName().equals("attributes")) {
                name = elt.getAttribute("name");
                type = elt.getAttribute("type");
                origin = elt.getAttribute("origin");
                setValue(name);
                setPortType(type);
                setOrigin(origin);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new MalformedModelingException();
    }
  }

  public int getDefaultConnector() {
    return TGComponentManager.ELN_CONNECTOR;
  }

  public String getPortType() {
    return type;
  }

  public void setPortType(String _type) {
    type = _type;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String _origin) {
    origin = _origin;
  }
}
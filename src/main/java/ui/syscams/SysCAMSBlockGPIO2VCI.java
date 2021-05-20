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

package ui.syscams;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class SysCAMSBlockGPIO2VCI Primitive Component. To be used in SystemC-AMS
 * diagrams Creation: 09/07/2018
 * 
 * @version 1.0 09/07/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSBlockGPIO2VCI extends TGCScalableWithInternalComponent implements SwallowTGComponent {
  private int maxFontSize = 14;
  private int minFontSize = 4;
  private int currentFontSize = -1;
  private Color myColor;

  private int textX = 15;
  private double dtextX = 0.0;

  public String oldValue;

  public SysCAMSBlockGPIO2VCI(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
      TGComponent _father, TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    initScaling(200, 150);

    oldScaleFactor = tdp.getZoom();
    dtextX = textX * oldScaleFactor;
    textX = (int) dtextX;
    dtextX = dtextX - textX;

    minWidth = 1;
    minHeight = 1;

    nbConnectingPoint = 0;

    addTGConnectingPointsComment();

    nbInternalTGComponent = 0;

    moveable = true;
    multieditable = false;
    editable = false;
    removable = true;
    userResizable = true;

    value = "blockGPIO2VCI";
    name = "Primitive component - Block GPIO2VCI";

    myImageIcon = IconManager.imgic1202;

    actionOnAdd();
  }

  public void internalDrawing(Graphics g) {
    int w;
    Font f = g.getFont();
    Font fold = f;

    if (myColor == null) {
      myColor = Color.white;
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
    g.drawRect(x, y, width, height);
    if ((width > 2) && (height > 2)) {
      g.setColor(myColor);
      g.fillRect(x + 1, y + 1, width - 1, height - 1);
      g.setColor(c);
    }

    int attributeFontSize = this.currentFontSize * 5 / 6;
    g.setFont(f.deriveFont((float) attributeFontSize));
    g.setFont(f);
    w = g.getFontMetrics().stringWidth(value);
    if (w > (width - 2 * textX)) {
      g.setFont(f.deriveFont(Font.BOLD));
      g.drawString(value, x + textX + 1, y + currentFontSize + textX);
    } else {
      g.setFont(f.deriveFont(Font.BOLD));
      g.drawString(value, x + (width - w) / 2, y + currentFontSize + textX);
    }

    g.setFont(fold);
  }

  public void rescale(double scaleFactor) {
    dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
    textX = (int) (dtextX);
    dtextX = dtextX - textX;
    super.rescale(scaleFactor);
  }

  public TGComponent isOnOnlyMe(int _x, int _y) {
    if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
      return this;
    }
    return null;
  }

  public int getType() {
    return TGComponentManager.CAMS_BLOCK_GPIO2VCI;
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

  public boolean acceptSwallowedTGComponent(TGComponent tgc) {
    return tgc instanceof SysCAMSPortDE;
  }

  public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
    if (tgc instanceof SysCAMSPortDE) {
      tgc.setFather(this);
      tgc.setDrawingZone(true);
      tgc.resizeWithFather();
      addInternalComponent(tgc, 0);
      return true;
    }
    return false;
  }

  public void removeSwallowedTGComponent(TGComponent tgc) {
    removeInternalComponent(tgc);
  }

  public void hasBeenResized() {
    rescaled = true;
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof SysCAMSPortDE) {
        tgcomponent[i].resizeWithFather();
      }
    }
  }

  public int getCurrentFontSize() {
    return currentFontSize;
  }

  public java.util.List<SysCAMSPortDE> getAllDEOriginPorts() {
    return getAllPorts(1, 1);
  }

  public java.util.List<SysCAMSPortDE> getAllDEDestinationPorts() {
    return getAllPorts(1, 0);
  }

  public java.util.List<SysCAMSPortDE> getAllPorts(int _type, int _isOrigin) {
    java.util.List<SysCAMSPortDE> ret = new LinkedList<SysCAMSPortDE>();
    SysCAMSPortDE port;

    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof SysCAMSPortDE) {
        port = (SysCAMSPortDE) tgcomponent[i];
        if ((port.getPortType() == _type) && (port.getOrigin() == _isOrigin)) {
          ret.add(port);
        }
      }
    }
    return ret;
  }

  public java.util.List<SysCAMSPortDE> getAllInternalPortsDE() {
    java.util.List<SysCAMSPortDE> list = new ArrayList<SysCAMSPortDE>();
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof SysCAMSPortDE) {
        list.add((SysCAMSPortDE) (tgcomponent[i]));
      }
    }
    return list;
  }
}
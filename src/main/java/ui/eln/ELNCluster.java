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
import ui.*;
import ui.util.IconManager;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Class ELNCluster Cluster to be used in ELN diagrams Creation: 28/07/2018
 * 
 * @version 1.0 28/07/2018
 * @author Irina Kit Yan LEE
 */

public class ELNCluster extends TGCScalableWithInternalComponent implements SwallowTGComponent {
  private int maxFontSize = 14;
  private int minFontSize = 4;
  private int currentFontSize = -1;
  protected int orientation;
  private Color myColor;

  private int textX = 15;
  private double dtextX = 0.0;
  protected int decPoint = 3;

  public String oldValue;

  public ELNCluster(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
      TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    initScaling(400, 400);

    oldScaleFactor = tdp.getZoom();
    dtextX = textX * oldScaleFactor;
    textX = (int) dtextX;
    dtextX = dtextX - textX;

    minWidth = 1;
    minHeight = 1;

    addTGConnectingPointsComment();

    moveable = true;
    multieditable = true;
    editable = false;
    removable = true;
    userResizable = true;

    value = "cluster";
  }

  public void internalDrawing(Graphics g) {
    int w;
    Font f = g.getFont();
    Font fold = f;
    MainGUI mgui = getTDiagramPanel().getMainGUI();

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
      if (mgui.getHidden() == false) {
        g.drawString(value, x + textX + 1, y + currentFontSize + textX);
      }
    } else {
      g.setFont(f.deriveFont(Font.BOLD));
      if (mgui.getHidden() == false) {
        g.drawString(value, x + (width - w) / 2, y + currentFontSize + textX);
      }
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

  public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
    // On the name ?
    if (_y <= (y + currentFontSize + textX)) {
      oldValue = value;
      String s = (String) JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
          JOptionPane.PLAIN_MESSAGE, IconManager.imgic100, null, getValue());
      if ((s != null) && (s.length() > 0)) {
        if (!TAttribute.isAValidId(s, false, false, false)) {
          JOptionPane.showMessageDialog(frame,
              "Could not change the name of the component: the new name is not a valid name", "Error",
              JOptionPane.INFORMATION_MESSAGE);
          return false;
        }
        setComponentName(s);
        setValueWithChange(s);
        setValue(s);
        rescaled = true;
        return true;

      }
      return false;
    }
    return false;
  }

  public int getType() {
    return TGComponentManager.ELN_CLUSTER;
  }

  public boolean acceptSwallowedTGComponent(TGComponent tgc) {
    if (tgc instanceof ELNModule) {
      return true;
    } else if (tgc instanceof ELNClusterTerminal) {
      return true;
    } else if (tgc instanceof ELNClusterPortDE) {
      return true;
    } else if (tgc instanceof ELNClusterPortTDF) {
      return true;
    }
    return false;
  }

  public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
    boolean swallowed = false;

    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof SwallowTGComponent) {
        if (((SwallowTGComponent) tgcomponent[i]).acceptSwallowedTGComponent(tgc)) {
          if (tgcomponent[i].isOnMe(x, y) != null) {
            swallowed = true;
            ((SwallowTGComponent) tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
            break;
          }
        }
      }
    }
    if (swallowed) {
      return true;
    }
    if (!acceptSwallowedTGComponent(tgc)) {
      return false;
    }
    tgc.setFather(this);
    tgc.setDrawingZone(true);

    if (tgc instanceof ELNModule) {
      tgc.resizeWithFather();
    }
    if (tgc instanceof ELNClusterTerminal) {
      tgc.resizeWithFather();
    }
    if (tgc instanceof ELNClusterPortDE) {
      tgc.resizeWithFather();
    }
    if (tgc instanceof ELNClusterPortTDF) {
      tgc.resizeWithFather();
    }
    addInternalComponent(tgc, 0);
    return true;
  }

  public void removeSwallowedTGComponent(TGComponent tgc) {
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] == tgc) {
        nbInternalTGComponent = nbInternalTGComponent - 1;
        if (nbInternalTGComponent == 0) {
          tgcomponent = null;
        } else {
          TGComponent[] tgcomponentbis = new TGComponent[nbInternalTGComponent];
          for (int j = 0; j < nbInternalTGComponent; j++) {
            if (j < i) {
              tgcomponentbis[j] = tgcomponent[j];
            }
            if (j >= i) {
              tgcomponentbis[j] = tgcomponent[j + 1];
            }
          }
          tgcomponent = tgcomponentbis;
        }
        break;
      }
    }
  }

  public void hasBeenResized() {
    rescaled = true;
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ELNModule) {
        tgcomponent[i].resizeWithFather();
      }
      if (tgcomponent[i] instanceof ELNClusterTerminal) {
        tgcomponent[i].resizeWithFather();
      }
      if (tgcomponent[i] instanceof ELNClusterPortDE) {
        tgcomponent[i].resizeWithFather();
      }
      if (tgcomponent[i] instanceof ELNClusterPortTDF) {
        tgcomponent[i].resizeWithFather();
      }
    }
  }

  public int getCurrentFontSize() {
    return currentFontSize;
  }

  public java.util.List<ELNModule> getAllModule() {
    java.util.List<ELNModule> list = new ArrayList<ELNModule>();
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ELNModule) {
        list.add((ELNModule) (tgcomponent[i]));
      }
    }
    return list;
  }

  public java.util.List<ELNClusterTerminal> getAllClusterTerminal() {
    java.util.List<ELNClusterTerminal> list = new ArrayList<ELNClusterTerminal>();
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ELNClusterTerminal) {
        list.add((ELNClusterTerminal) (tgcomponent[i]));
      }
    }
    return list;
  }

  public java.util.List<ELNClusterPortDE> getAllClusterPortDE() {
    java.util.List<ELNClusterPortDE> list = new ArrayList<ELNClusterPortDE>();
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ELNClusterPortDE) {
        list.add((ELNClusterPortDE) (tgcomponent[i]));
      }
    }
    return list;
  }

  public java.util.List<ELNClusterPortTDF> getAllClusterPortTDF() {
    java.util.List<ELNClusterPortTDF> list = new ArrayList<ELNClusterPortTDF>();
    for (int i = 0; i < nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof ELNClusterPortTDF) {
        list.add((ELNClusterPortTDF) (tgcomponent[i]));
      }
    }
    return list;
  }
}
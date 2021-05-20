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

package ui.avatarpd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * Class AvatarPDAttribute Attribute to SysML value type Creation: 22/04/2010
 * 
 * @version 1.0 22/04/2010
 * @author Ludovic APVRILLE
 */
public class AvatarPDAttribute extends AvatarPDToggle implements SwallowedTGComponent {
  // private int textY1 = 3;
  // private int textY2 = 3;
  // private int textX = 10;

  private String stereotype = "attribute";

  // private int maxFontSize = 12;
  // private int minFontSize = 4;
  // private int currentFontSize = -1;
  // private boolean displayText = true;

  public AvatarPDAttribute(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
      TGComponent _father, TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    // Issue #31
    width = 125;
    height = 50;
    minWidth = 100;
    textY = 3;
    initScaling(125, 50);

    nbConnectingPoint = 12;
    connectingPoint = new TGConnectingPoint[12];

    connectingPoint[0] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
    connectingPoint[1] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
    connectingPoint[2] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
    connectingPoint[3] = new AvatarPDSignalConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
    connectingPoint[4] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
    connectingPoint[5] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
    connectingPoint[6] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
    connectingPoint[7] = new AvatarPDAttributeConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
    connectingPoint[8] = new AvatarPDSignalConnectingPoint(this, 0, 0, false, true, 0.0, 0.80);
    connectingPoint[9] = new AvatarPDSignalConnectingPoint(this, 0, 0, false, true, 1.0, 0.80);
    connectingPoint[10] = new AvatarPDSignalConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
    connectingPoint[11] = new AvatarPDSignalConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);
    // addTGConnectingPointsComment();

    moveable = true;
    editable = true;
    removable = true;

    value = "attr01";
    toggle = "";

    // currentFontSize = maxFontSize;
    // oldScaleFactor = tdp.getZoom();

    myImageIcon = IconManager.imgic702;
  }

  @Override
  public void internalDrawing(Graphics g) {
    // Rectangle and filling
    Color c = g.getColor();
    g.draw3DRect(x, y, width, height, true);
    g.setColor(ColorManager.AVATARPD_ATTRIBUTE);
    g.fill3DRect(x + 1, y + 1, width - 1, scale(toggleHeight - 1), true);
    g.setColor(ColorManager.AVATARPD_SIGNAL);
    g.fill3DRect(x + 1, y + scale(toggleHeight), width - 1, height - scale(toggleHeight), true);
    g.setColor(c);

    // Strings
    String ster = "<<" + stereotype + ">>";
    Font f = g.getFont();
    g.setFont(f.deriveFont(f.getSize() - 4));
    drawDoubleString(g, ster, value);
    drawSingleString(g, getFullToggle(), getCenter(g, getFullToggle()), y + f.getSize() * 3 + scale(9));
  }

  // public void internalDrawing(Graphics g) {
  // String ster;
  // ster = "<<" + stereotype + ">>";
  // Font f = g.getFont();
  // Font fold = f;
  //
  // if (valueChanged) {
  // setValueWidth(g);
  // }
  //
  // if ((rescaled) && (!tdp.isScaled())) {
  //
  // if (currentFontSize == -1) {
  // currentFontSize = f.getSize();
  // }
  // rescaled = false;
  // // Must set the font size ..
  // // Find the biggest font not greater than max_font size
  // // By Increment of 1
  // // Or decrement of 1
  // // If font is less than 4, no text is displayed
  //
  // int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
  // int w0, w1, w2;
  // f = f.deriveFont((float)maxCurrentFontSize);
  // g.setFont(f);
  // //
  // while(maxCurrentFontSize > (minFontSize-1)) {
  // w0 = g.getFontMetrics().stringWidth(value);
  // w1 = g.getFontMetrics().stringWidth(ster);
  // w2 = Math.min(w0, w1);
  // if (w2 < (width - (2*textX))) {
  // break;
  // }
  // maxCurrentFontSize --;
  // f = f.deriveFont((float)maxCurrentFontSize);
  // g.setFont(f);
  // }
  // currentFontSize = maxCurrentFontSize;
  //
  // if(currentFontSize <minFontSize) {
  // displayText = false;
  // } else {
  // displayText = true;
  // f = f.deriveFont((float)currentFontSize);
  // g.setFont(f);
  // }
  //
  // }
  //
  // Color c = g.getColor();
  // g.draw3DRect(x, y, width, height, true);
  // g.setColor(ColorManager.AVATARPD_ATTRIBUTE);
  // g.fill3DRect(x+1, y+1, width-1, toggleHeight-1, true);
  // g.setColor(ColorManager.AVATARPD_SIGNAL);
  // g.fill3DRect(x+1, y+toggleHeight, width-1, height-toggleHeight, true);
  // g.setColor(c);
  //
  // // Strings
  // int w;
  // if (displayText) {
  // f = f.deriveFont((float)currentFontSize);
  // Font f0 = g.getFont();
  //
  // boolean cannotWriteAttack = (height < (2 * currentFontSize + (int)(textY *
  // tdp.getZoom())));
  //
  // if (cannotWriteAttack) {
  // w = g.getFontMetrics().stringWidth(value);
  // int h = currentFontSize + (int)(textY * tdp.getZoom());
  // if ((w < (2*textX + width)) && (h < height)) {
  // g.drawString(value, x + (width - w)/2, y + h);
  // } else {
  // w = g.getFontMetrics().stringWidth(ster);
  // if ((w < (2*textX + width)) && (h < height)) {
  // g.drawString(ster, x + (width - w)/2, y + h);
  // }
  // }
  // } else {
  // g.setFont(f.deriveFont(Font.BOLD));
  // int h = currentFontSize + (int)(textY * tdp.getZoom());
  // int cumulated = 0;
  // w = g.getFontMetrics().stringWidth(ster);
  // if ((w < (2*textX + width)) && (h < height)) {
  // g.drawString(ster, x + (width - w)/2, y + h);
  // cumulated = h;
  // }
  // g.setFont(f0);
  // w = g.getFontMetrics().stringWidth(value);
  // h = cumulated + currentFontSize + (int)(textY * tdp.getZoom());
  // if ((w < (2*textX + width)) && (h < height)) {
  // g.drawString(value, x + (width - w)/2, y + h);
  // }
  // String s = getFullToggle();
  // w = g.getFontMetrics().stringWidth(s);
  // h = height-toggleDecY;
  // if ((w < (2*textX + width)) && (h < height)) {
  // g.setFont(f.deriveFont(Font.ITALIC));
  // g.drawString(s, x + (width - w)/2, y + h);
  // }
  //
  // }
  // }
  //
  // g.setFont(fold);
  //
  // }

  @Override
  public void resizeWithFather() {
    if ((father != null) && (father instanceof AvatarPDBlock)) {
      //
      setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
      // setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y,
      // father.getHeight() - getHeight()));
      setMoveCd(x, y);
    }
  }

  @Override
  public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {

    // String text = getName() + ": ";
    if (_y < y + toggleHeight) {
      String s = (String) JOptionPane.showInputDialog(frame, "Attribute name", "setting value",
          JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null, getValue());
      if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
        // boolean b;
        if (!TAttribute.isAValidId(s, false, false, false)) {
          JOptionPane.showMessageDialog(frame,
              "Could not change the name of the attribute: the new name is not a valid name", "Error",
              JOptionPane.INFORMATION_MESSAGE);
          return false;
        }
        setValue(s);
        valueChanged = true;
      }
    } else {
      return editToggle(frame);
    }

    return true;

  }

  @Override
  public TGComponent isOnOnlyMe(int x1, int y1) {

    if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
      return this;
    }
    return null;
  }

  @Override
  public int getType() {
    return TGComponentManager.APD_ATTRIBUTE;
  }

  @Override
  public int getDefaultConnector() {
    return TGComponentManager.APD_ATTRIBUTE_CONNECTOR;
  }

  public String getAttributeName() {
    return value;
  }

}

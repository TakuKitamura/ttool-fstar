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

package ui.avatardd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAvatarChannelArtifact;

import javax.swing.*;
import java.awt.*;

/**
 * Class ADDChannelArtifact Artifact of an avatar deployment diagram Creation:
 * 25/08/2014
 * 
 * @version 1.0 25/08/2014
 * @author Ludovic APVRILLE
 */
public class ADDChannelArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent {
  protected int lineLength = 5;
  // protected int textX = 5;
  // protected int textY = 15;
  protected int textY2 = 35;
  protected int space = 5;
  protected int fileX = 20;
  protected int fileY = 25;
  protected int cran = 5;

  protected String oldValue = "";
  protected String referenceDiagram = "referenceToDiagram";
  protected String channelName = "channelName";
  protected String fullChannelName = "fullChannelName";

  public ADDChannelArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
      TGComponent _father, TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    textY = 15;
    textX = 5;
    width = 75;
    height = 40;
    minWidth = 75;

    initScaling(75, 40);

    nbConnectingPoint = 0;
    addTGConnectingPointsComment();

    moveable = true;
    editable = true;
    removable = true;

    value = "AvatarDesign::channel";
    channelName = "channel";
    referenceDiagram = "AvatarDesign";

    makeFullValue();

    myImageIcon = IconManager.imgic702;
  }

  @Override
  public void internalDrawing(Graphics g) {
    if (oldValue.compareTo(value) != 0) {
      setValue(value, g);
    }

    g.drawRect(x, y, width, height);
    Color c = g.getColor();
    g.setColor(ColorManager.CPU_BOX_2);
    g.fillRect(x + 1, y + 1, width - 1, height - 1);
    g.setColor(c);

    // g.drawRoundRect(x, y, width, height, arc, arc);
    g.drawLine(x + width - space - fileX, y + space, x + width - space - fileX, y + space + fileY);
    g.drawLine(x + width - space - fileX, y + space, x + width - space - cran, y + space);
    g.drawLine(x + width - space - cran, y + space, x + width - space, y + space + cran);
    g.drawLine(x + width - space, y + space + cran, x + width - space, y + space + fileY);
    g.drawLine(x + width - space, y + space + fileY, x + width - space - fileX, y + space + fileY);
    g.drawLine(x + width - space - cran, y + space, x + width - space - cran, y + space + cran);
    g.drawLine(x + width - space - cran, y + space + cran, x + width - space, y + space + cran);

    // g.drawImage(scale(IconManager.img9), x+scale(width-space-fileX + 3), y +
    // scale(space + 7), null);
    // g.drawImage(IconManager.img9, x+width-space-fileX + 3, y + space + 7, null);

    drawSingleString(g, value, x + textX, y + textY);
  }

  public void setValue(String val, Graphics g) {
    oldValue = value;
    int w = g.getFontMetrics().stringWidth(value);
    int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);

    //
    if (w1 != width) {
      width = w1;
      resizeWithFather();
    }
    //
  }

  @Override
  public void resizeWithFather() {
    if ((father != null) && ((father instanceof ADDRAMNode))) {
      //
      setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
      // setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y,
      // father.getHeight() - getHeight()));
      setMoveCd(x, y);
    }
  }

  @Override
  public boolean editOnDoubleClick(JFrame frame) {
    String tmp;
    boolean error = false;

    JDialogAvatarChannelArtifact dialog = new JDialogAvatarChannelArtifact(frame, "Setting artifact attributes", this);
    // dialog.setSize(650, 350);
    GraphicLib.centerOnParent(dialog, 650, 350);
    dialog.setVisible(true); // blocked until dialog has been closed

    if (!dialog.isRegularClose()) {
      return false;
    }

    if (dialog.getReferenceDiagram() == null) {
      return false;
    }

    if (dialog.getReferenceDiagram().length() != 0) {
      tmp = dialog.getReferenceDiagram();
      referenceDiagram = tmp;
    }

    if (dialog.getChannelName().length() != 0) {
      channelName = dialog.getChannelName();
      fullChannelName = dialog.getFullChannelName();
    }

    makeFullValue();

    return !error;

  }

  private void makeFullValue() {
    String newChannelName = channelName;
    int pos1 = channelName.indexOf('(');
    if (pos1 != -1) {
      // int pos2=channelName.lastIndexOf(')');
      // int pos3=channelName.indexOf('(');
      // int pos4=channelName.lastIndexOf('(');

      newChannelName = channelName.substring(0, pos1);

      value = newChannelName;
    } else {
      value = channelName;
    }
  }

  /*
   * private void makeFullValue() { value = referenceDiagram + "::" + channelName;
   * }
   */

  @Override
  public TGComponent isOnMe(int _x, int _y) {
    if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
      return this;
    }
    return null;
  }

  public int getType() {
    return TGComponentManager.ADD_CHANNELARTIFACT;
  }

  @Override
  protected String translateExtraParam() {
    StringBuffer sb = new StringBuffer("<extraparam>\n");
    sb.append("<info value=\"" + value + "\" channelName=\"" + channelName + "\" fullChannelName=\"" + fullChannelName
        + "\" referenceDiagram=\"");
    sb.append(referenceDiagram);
    sb.append("\" />\n");
    sb.append("</extraparam>\n");
    return new String(sb);
  }

  @Override
  public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
    //
    try {

      NodeList nli;
      Node n1, n2;
      Element elt;
      // int t1id;
      String svalue = null, sname = null, fname = null, sreferenceTask = null;
      // String prio;

      for (int i = 0; i < nl.getLength(); i++) {
        n1 = nl.item(i);
        //
        if (n1.getNodeType() == Node.ELEMENT_NODE) {
          nli = n1.getChildNodes();
          for (int j = 0; j < nli.getLength(); j++) {
            n2 = nli.item(j);
            //
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
              elt = (Element) n2;
              if (elt.getTagName().equals("info")) {
                svalue = elt.getAttribute("value");
                sname = elt.getAttribute("channelName");
                fname = elt.getAttribute("fullChannelName");
                sreferenceTask = elt.getAttribute("referenceDiagram");
              }

              if (svalue != null) {
                value = svalue;
              }
              if (sname != null) {
                channelName = sname;
              }

              if (fname != null) {
                fullChannelName = fname;
              } else {
                fullChannelName = channelName;
              }
              if (sreferenceTask != null) {
                referenceDiagram = sreferenceTask;
              }
            }
          }
        }
      }

    } catch (Exception e) {
      throw new MalformedModelingException(e);
    }
    makeFullValue();
  }

  public DesignPanel getDesignPanel() {
    return tdp.getGUI().getDesignPanel(value);
  }

  public String getReferenceDiagram() {
    return referenceDiagram;
  }

  public void setReferenceDiagram(String _referenceDiagram) {
    referenceDiagram = _referenceDiagram;
    makeFullValue();
  }

  public String getChannelName() {
    return channelName;
  }

  public String getLongChannelName() {
    return fullChannelName;
  }

  @Override
  public String getStatusInformation() {
    return "Name of the channel: " + fullChannelName;
  }
}

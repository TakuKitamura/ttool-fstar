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

package ui.avatarbd;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.avatarsmd.AvatarSMDPanel;
import ui.util.IconManager;
import ui.window.JDialogAvatarLibraryFunction;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represent a Library Function block on an avatar block diagram.
 *
 * @author Florian LUGOU
 * @version 1.0 04.08.2016
 */
public class AvatarBDLibraryFunction extends TGCScalableWithoutInternalComponent
    implements SwallowedTGComponent, AvatarBDStateMachineOwner, Comparable<AvatarBDLibraryFunction> {

  /**
   * Stereotype for standard library function.
   */
  private static final String stereotype = "library";

  /**
   * Stereotype for cryptographic library function.
   */
  private static final String stereotypeCrypto = "cryptolibrary";

  /**
   * Maximum font size for this compontent.
   */
  private static final int maxFontSize = 12;

  /**
   * Minimum font size for this compontent.
   */
  private static final int minFontSize = 4;

  /**
   * The horizontal spacing between text and left and right borders.
   */
  private static final int paddingHorizontal = 7;

  /**
   * The vertical spacing between lines.
   */
  private static final int paddingVertical = 3;

  /**
   * Used to know where the user double clicked
   */
  private int limitName;

  /**
   * Used to know where the user double clicked
   */
  private int limitParameters;

  /**
   * Used to know where the user double clicked
   */
  private int limitSignals;

  /**
   * Current font size.
   */
  private int currentFontSize = -1;

  /**
   * Equals True when the box is large enough for the icon to be drawn.
   */
  private boolean iconIsDrawn = false;

  /**
   * For function that use cryptographic primitives.
   */
  private boolean isCrypto = false;

  /**
   * The list of parameters of the function.
   */
  private LinkedList<TAttribute> parameters;

  /**
   * The list of variables local to the function.
   */
  private LinkedList<TAttribute> attributes;

  /**
   * The list of signals used by the function.
   */
  private LinkedList<AvatarSignal> signals;

  /**
   * The list of attribute that will hold the return values of the function.
   */
  private LinkedList<TAttribute> returnAttributes;

  /**
   * The list of methods that can be used by the function.
   */
  private LinkedList<AvatarMethod> methods;

  /**
   * Standard constructor for a library function block.
   *
   * @param x      The absolute coordinate of the block along X.
   * @param y      The absolute coordinate of the block along Y.
   * @param minX   The minimum authorized coordinate along X.
   * @param maxX   The maximum authorized coordinate along X.
   * @param minY   The minimum authorized coordinate along Y.
   * @param maxY   The maximum authorized coordinate along Y.
   * @param pos    Indicates whether the position is considered as relative to
   *               this father's component.
   * @param father The father component in the diagram.
   * @param tdp    The diagram panel.
   */
  public AvatarBDLibraryFunction(int x, int y, int minX, int maxX, int minY, int maxY, boolean pos, TGComponent father,
      TDiagramPanel tdp) {

    super(x, y, minX, maxX, minY, maxY, pos, father, tdp);

    this.width = 160;
    this.height = 100;
    this.minWidth = 5;
    this.minHeight = 2;
    initScaling(160, 100);

    this.nbConnectingPoint = 0;
    this.connectingPoint = new TGConnectingPoint[0];
    this.addTGConnectingPointsComment();

    this.nbInternalTGComponent = 0;

    this.moveable = true;
    this.editable = true;
    this.multieditable = true;
    this.removable = true;
    this.userResizable = true;

    // Find a new unused name
    int i;
    for (i = 0; i < 100; i++) {
      String tmpName = "LibraryFunction" + i;
      if (this.tdp.isAvatarBlockNameUnique(tmpName) && true) { // TODO: check if no other tab has same name
        this.name = tmpName;
        this.value = tmpName;
        break;
      }
    }
    if (i == 100) {
      // TODO: throw exception
    }

    this.oldScaleFactor = this.tdp.getZoom();
    this.currentFontSize = (int) (AvatarBDLibraryFunction.maxFontSize * this.oldScaleFactor);

    // TODO: change that
    this.myImageIcon = IconManager.imgic700;

    this.parameters = new LinkedList<TAttribute>();
    this.attributes = new LinkedList<TAttribute>();
    this.signals = new LinkedList<AvatarSignal>();
    this.returnAttributes = new LinkedList<TAttribute>();
    this.methods = new LinkedList<AvatarMethod>();

    // Ask the panel to add a tab for the state machine diagram.
    this.actionOnAdd();
  }

  public TDiagramPanel getDiagramPanel() {
    return this.tdp;
  }

  public String getFunctionName() {
    return this.name;
  }

  public String getFullyQualifiedName() {
    String result = "";
    if (this.father != null && (this.father instanceof AvatarBDBlock)) {
      result = ((AvatarBDBlock) this.father).getFullyQualifiedName() + ".";
    }
    result += this.name;

    return result;
  }

  public LinkedList<TAttribute> getParameters() {
    return this.parameters;
  }

  public void resetParameters() {
    this.parameters = new LinkedList<TAttribute>();
  }

  public void addParameter(TAttribute parameter) {
    this.parameters.add(parameter);
  }

  public LinkedList<AvatarSignal> getSignals() {
    return this.signals;
  }

  public void resetSignals() {
    this.signals = new LinkedList<AvatarSignal>();
  }

  public void addSignal(AvatarSignal signal) {
    this.signals.add(signal);
  }

  public LinkedList<TAttribute> getAttributes() {
    return this.attributes;
  }

  public void resetAttributes() {
    this.attributes = new LinkedList<TAttribute>();
  }

  public void addAttribute(TAttribute attribute) {
    this.attributes.add(attribute);
  }

  public LinkedList<TAttribute> getReturnAttributes() {
    return this.returnAttributes;
  }

  public void resetReturnAttributes() {
    this.returnAttributes = new LinkedList<TAttribute>();
  }

  public void addReturnAttribute(TAttribute returnAttribute) {
    this.returnAttributes.add(returnAttribute);
  }

  public LinkedList<AvatarMethod> getMethods() {
    return this.methods;
  }

  public void resetMethods() {
    this.methods = new LinkedList<AvatarMethod>();
  }

  public void addMethod(AvatarMethod method) {
    this.methods.add(method);
  }

  @Override
  public void internalDrawing(Graphics graph) {
    Font font = graph.getFont();
    this.internalDrawingAux(graph);
    graph.setFont(font);
  }

  /**
   * Draws the Library Function object.
   *
   * @param graph The {@link Graphics} object used to draw this component.
   */
  private void internalDrawingAux(Graphics graph) {
    // Draw outer rectangle (for border)
    Color c = graph.getColor();
    graph.drawRect(this.x, this.y, this.width, this.height);

    // Draw inner rectangle
    graph.setColor(ColorManager.AVATAR_LIBRARYFUNCTION);
    graph.fillRect(this.x + 1, this.y + 1, this.width - 1, this.height - 1);
    graph.setColor(c);

    // limits
    this.limitName = -1;
    this.limitParameters = -1;
    this.limitSignals = -1;

    // h retains the coordinate along X where an element was last drawn
    int h = 0;

    int paddingVertical = (int) (AvatarBDLibraryFunction.paddingVertical * this.tdp.getZoom());
    int paddingHorizontal = (int) (AvatarBDLibraryFunction.paddingHorizontal * this.tdp.getZoom());

    // Draw icon
    this.iconIsDrawn = this.width > IconManager.iconSize + 2 * paddingHorizontal
        && height > IconManager.iconSize + 2 * paddingHorizontal;
    if (this.iconIsDrawn)
      graph.drawImage(scale(IconManager.img5100), this.x + this.width - scale(IconManager.iconSize) - paddingHorizontal,
          this.y + paddingHorizontal, null);

    Font font = graph.getFont();

    String ster;
    if (!this.isCrypto)
      ster = "<<" + stereotype + ">>";
    else
      ster = "<<" + stereotypeCrypto + ">>";

    if (this.rescaled && !this.tdp.isScaled()) {
      this.rescaled = false;
      // Must set the font size...
      // Incrementally find the biggest font not greater than max_font size
      // If font is less than min_font, no text is displayed

      // This is the maximum font size possible
      int maxCurrentFontSize = Math.max(0,
          Math.min(this.height, (int) (AvatarBDLibraryFunction.maxFontSize * this.tdp.getZoom())));
      font = font.deriveFont((float) maxCurrentFontSize);

      // Try to decrease font size until we get below the minimum
      while (maxCurrentFontSize > (AvatarBDLibraryFunction.minFontSize * this.tdp.getZoom() - 1)) {
        // Compute width of name of the function
        int w0 = graph.getFontMetrics(font).stringWidth(this.value);
        // Compute width of string stereotype
        int w1 = graph.getFontMetrics(font).stringWidth(ster);

        // if one of the two width is small enough use this font size
        if (Math.min(w0, w1) < this.width - (2 * paddingHorizontal))
          break;

        // Decrease font size
        maxCurrentFontSize--;
        // Scale the font
        font = font.deriveFont((float) maxCurrentFontSize);
      }

      // Box is too damn small
      if (this.currentFontSize < AvatarBDLibraryFunction.minFontSize * this.tdp.getZoom()) {
        maxCurrentFontSize++;
        // Scale the font
        font = font.deriveFont((float) maxCurrentFontSize);
      }

      // Use this font
      graph.setFont(font);
      this.currentFontSize = maxCurrentFontSize;
    } else
      font = font.deriveFont(this.currentFontSize);

    graph.setFont(font.deriveFont(Font.BOLD));
    h = graph.getFontMetrics().getAscent() + graph.getFontMetrics().getLeading() + paddingVertical;

    if (h + graph.getFontMetrics().getDescent() + paddingVertical >= this.height)
      return;

    // Write stereotype if small enough
    int w = graph.getFontMetrics().stringWidth(ster);
    if (w + 2 * paddingHorizontal < this.width)
      drawSingleString(graph, ster, this.x + (this.width - w) / 2, this.y + h);
    else {
      // try to draw with "..." instead
      if (!this.isCrypto)
        ster = stereotype;
      else
        ster = stereotypeCrypto;

      for (int stringLength = ster.length() - 1; stringLength >= 0; stringLength--) {
        String abbrev = "<<" + ster.substring(0, stringLength) + "...>>";
        w = graph.getFontMetrics().stringWidth(abbrev);
        if (w + 2 * paddingHorizontal < this.width) {
          drawSingleString(graph, abbrev, this.x + (this.width - w) / 2, this.y + h);
          break;
        }
      }
    }

    // Write value if small enough
    graph.setFont(font);
    h += graph.getFontMetrics().getHeight() + paddingVertical;
    if (h + graph.getFontMetrics().getDescent() + paddingVertical >= this.height)
      return;

    w = graph.getFontMetrics().stringWidth(this.value);
    if (w + 2 * paddingHorizontal < this.width)
      drawSingleString(graph, this.value, this.x + (this.width - w) / 2, this.y + h);
    else {
      // try to draw with "..." instead
      for (int stringLength = this.value.length() - 1; stringLength >= 0; stringLength--) {
        String abbrev = this.value.substring(0, stringLength) + "...";
        w = graph.getFontMetrics().stringWidth(abbrev);
        if (w + 2 * paddingHorizontal < this.width) {
          drawSingleString(graph, abbrev, this.x + (this.width - w) / 2, this.y + h);
          break;
        }
      }
    }

    h += graph.getFontMetrics().getDescent() + paddingVertical;

    // Update lower bound of text
    this.limitName = this.y + h;

    if (h + paddingVertical >= this.height)
      return;

    // Draw separator
    graph.drawLine(this.x, this.y + h, this.x + this.width, this.y + h);

    if (!this.tdp.areAttributesVisible())
      return;

    // Set font size
    // int attributeFontSize = Math.min (12, this.currentFontSize - 2);
    int attributeFontSize = this.currentFontSize * 5 / 6;
    graph.setFont(font.deriveFont((float) attributeFontSize));
    int step = graph.getFontMetrics().getHeight();

    h += paddingVertical;

    // Parameters
    for (TAttribute attr : this.parameters) {
      h += step;
      if (h >= this.height - paddingHorizontal) {
        this.limitParameters = this.y + this.height;
        return;
      }

      // Get the string for this parameter
      String attrString = attr.toAvatarString();

      // Try to draw it
      w = graph.getFontMetrics().stringWidth(attrString);
      if (w + 2 * paddingHorizontal < this.width)
        drawSingleString(graph, attrString, this.x + paddingHorizontal, this.y + h);
      else {
        // If we can't, try to draw with "..." instead
        int stringLength;
        for (stringLength = attrString.length() - 1; stringLength >= 0; stringLength--) {
          String abbrev = attrString.substring(0, stringLength) + "...";
          w = graph.getFontMetrics().stringWidth(abbrev);
          if (w + 2 * paddingHorizontal < this.width) {
            drawSingleString(graph, abbrev, this.x + paddingHorizontal, this.y + h);
            break;
          }
        }

        if (stringLength < 0)
          // skip attribute
          h -= step;
      }
    }

    h += graph.getFontMetrics().getDescent() + paddingVertical;

    // Remember the end of parameters
    this.limitParameters = this.y + h;

    if (h + paddingVertical >= this.height)
      return;

    graph.drawLine(this.x, this.y + h, this.x + this.width, this.y + h);
    h += paddingVertical;

    // Signals
    for (AvatarSignal signal : this.signals) {
      h += step;
      if (h >= this.height - paddingHorizontal) {
        this.limitSignals = this.y + this.height;
        return;
      }

      String signalString = "~ " + signal.toString();
      w = graph.getFontMetrics().stringWidth(signalString);
      if (w + 2 * paddingHorizontal < this.width)
        drawSingleString(graph, signalString, this.x + paddingHorizontal, this.y + h);
      else {
        // If we can't, try to draw with "..." instead
        int stringLength;
        for (stringLength = signalString.length() - 1; stringLength >= 0; stringLength--) {
          String abbrev = signalString.substring(0, stringLength) + "...";
          w = graph.getFontMetrics().stringWidth(abbrev);
          if (w + 2 * paddingHorizontal < this.width) {
            drawSingleString(graph, abbrev, this.x + paddingHorizontal, this.y + h);
            break;
          }
        }

        if (stringLength < 0)
          // skip signal
          h -= step;
      }
    }

    h += graph.getFontMetrics().getDescent() + paddingVertical;

    // Remember limit of signals
    this.limitSignals = this.y + h;

    if (h + paddingVertical >= this.height)
      return;

    graph.drawLine(this.x, this.y + h, this.x + this.width, this.y + h);
    h += paddingVertical;

    // Return Attributes
    for (TAttribute attr : this.returnAttributes) {
      h += step;
      if (h >= this.height - paddingHorizontal)
        return;

      // Get the string for this return attribute
      String attrString = attr.toAvatarString();

      w = graph.getFontMetrics().stringWidth(attrString);
      if (w + 2 * paddingHorizontal < this.width)
        drawSingleString(graph, attrString, this.x + paddingHorizontal, this.y + h);
      else {
        // If we can't, try to draw with "..." instead
        int stringLength;
        for (stringLength = attrString.length() - 1; stringLength >= 0; stringLength--) {
          String abbrev = attrString.substring(0, stringLength) + "...";
          w = graph.getFontMetrics().stringWidth(abbrev);
          if (w + 2 * paddingHorizontal < this.width) {
            drawSingleString(graph, abbrev, this.x + paddingHorizontal, this.y + h);
            break;
          }
        }

        if (stringLength < 0)
          // skip signal
          h -= step;
      }
    }
  }

  @Override
  public void setName(String s) {
    this.tdp.changeStateMachineTabName(this.name, s);
    this.name = s;
    this.setValue(s);
  }

  @Override
  public TGComponent isOnMe(int x1, int y1) {

    if (GraphicLib.isInRectangle(x1, y1, this.x, this.y, this.width, this.height))
      return this;

    return null;
  }

  @Override
  public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
    int paddingHorizontal = (int) (AvatarBDLibraryFunction.paddingHorizontal * this.tdp.getZoom());
    if (this.iconIsDrawn
        && GraphicLib.isInRectangle(_x, _y, this.x + this.width - IconManager.iconSize - paddingHorizontal,
            this.y + paddingHorizontal, IconManager.iconSize, IconManager.iconSize)) {
      this.tdp.selectTab(this.getValue());
      return true;
    }

    // Click on the name
    if (_y < limitName) {
      String s = (String) JOptionPane.showInputDialog(frame, "Library Function Name", "setting value",
          JOptionPane.PLAIN_MESSAGE, IconManager.imgic101, null, this.getValue());

      if (s == null || s.isEmpty() || s.equals(this.value))
        return false;

      if (!TAttribute.isAValidId(s, false, false, false)) {
        JOptionPane.showMessageDialog(frame,
            "Could not change the name of the Library Function: the new name is not a valid name", "Error",
            JOptionPane.INFORMATION_MESSAGE);
        return false;
      }

      if (!this.tdp.isAvatarBlockNameUnique(s)) {
        JOptionPane.showMessageDialog(frame,
            "Could not change the name of the Library Function: the new name is already used by another element.",
            "Error", JOptionPane.INFORMATION_MESSAGE);
        return false;
      }

      // Update the name of the tab corresponding to the state machine of the library
      // function
      if (!this.tdp.changeStateMachineTabName(this.value, s)) {
        JOptionPane.showMessageDialog(frame,
            "Could not change the name of the Library Function: this name is already used by another tab.", "Error",
            JOptionPane.INFORMATION_MESSAGE);
        return false;
      }

      this.name = s;
      this.value = s;
      this.recalculateSize();
      this.repaint = true;

      return true;
    }

    // Click on parameters

    // Create a new dialog to change parameters, signals, return values, etc.
    JDialogAvatarLibraryFunction dialog = new JDialogAvatarLibraryFunction(this, frame,
        "Settings of library function " + value, "Library Function");
    this.setJDialogOptions(dialog);
    // dialog.setSize (650, 575);
    GraphicLib.centerOnParent(dialog, 650, 575);

    // Focus on the right input depending on the part that was clicked.
    // FIXME: if nothing is displayed, focus will go on tab 2 instead of tab 0
    if (_y < this.limitParameters)
      dialog.selectTabIndex(0);
    else if (_y < this.limitSignals)
      dialog.selectTabIndex(1);
    else
      dialog.selectTabIndex(2);

    // Set visible and block until dialog is closed
    dialog.setVisible(true);

    ((AvatarBDPanel) tdp).updateAllSignalsOnConnectors();

    // Tag so that it is rescaled
    this.rescaled = true;

    return true;
  }

  protected void setJDialogOptions(JDialogAvatarLibraryFunction jdab) {
    /*
     * jdab.addAccess (TAttribute.getStringAccess (TAttribute.PRIVATE));
     * jdab.addType (TAttribute.getStringAvatarType (TAttribute.BOOLEAN), true);
     * jdab.addType (TAttribute.getStringAvatarType (TAttribute.INTEGER), true);
     * jdab.addType (TAttribute.getStringType (TAttribute.TIMER), false);
     * 
     * for (String s: this.tdp.getAllDataTypes ()) jdab.addType(s, false);
     * 
     * 
     * jdab.enableInitialValue(true); jdab.enableRTLOTOSKeyword(false);
     * jdab.enableJavaKeyword(false);
     */
  }

  @Override
  public int getType() {
    return TGComponentManager.AVATARBD_LIBRARYFUNCTION;
  }

  /**
   * Translate this Library Function into a XML element.
   *
   * @return The string for the corresponding XML element.
   */
  protected String translateExtraParam() {
    StringBuffer sb = new StringBuffer("<extraparam>\n");
    sb.append("<CryptoLibraryFunction value=\"" + isCrypto + "\" />\n");
    for (TAttribute attr : this.parameters) {
      sb.append("<Parameter access=\"");
      sb.append(attr.getAccess());
      sb.append("\" id=\"");
      sb.append(attr.getId());
      sb.append("\" value=\"");
      sb.append(attr.getInitialValue());
      sb.append("\" type=\"");
      sb.append(attr.getType());
      sb.append("\" typeOther=\"");
      sb.append(attr.getTypeOther());
      sb.append("\" />\n");
    }

    for (AvatarSignal signal : this.signals) {
      sb.append("<Signal value=\"");
      sb.append(signal.toString());
      sb.append("\" />\n");
    }

    for (TAttribute attr : this.returnAttributes) {
      sb.append("<ReturnAttribute access=\"");
      sb.append(attr.getAccess());
      sb.append("\" id=\"");
      sb.append(attr.getId());
      sb.append("\" value=\"");
      sb.append(attr.getInitialValue());
      sb.append("\" type=\"");
      sb.append(attr.getType());
      sb.append("\" typeOther=\"");
      sb.append(attr.getTypeOther());
      sb.append("\" />\n");
    }

    for (TAttribute attr : this.attributes) {
      sb.append("<Attribute access=\"");
      sb.append(attr.getAccess());
      sb.append("\" id=\"");
      sb.append(attr.getId());
      sb.append("\" value=\"");
      sb.append(attr.getInitialValue());
      sb.append("\" type=\"");
      sb.append(attr.getType());
      sb.append("\" typeOther=\"");
      sb.append(attr.getTypeOther());
      sb.append("\" />\n");
    }

    for (AvatarMethod method : this.methods) {
      sb.append("<Method value=\"");
      sb.append(method.toSaveString());
      sb.append("\" />\n");
    }

    sb.append("</extraparam>\n");

    return new String(sb);
  }

  /**
   * Load a Library Function element from a XML description.
   * <p>
   * TODO
   *
   * @param nl    The {@link NodeList} representing the XML extraparam node
   * @param decX  Unused.
   * @param decY  Unused.
   * @param decId Unused.
   * @throws MalformedModelingException When the provided XML is corrupted.
   */
  @Override
  public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
    try {
      for (int i = 0; i < nl.getLength(); i++) {
        Node n1 = nl.item(i);

        // Ignore if it's not an element
        if (n1.getNodeType() != Node.ELEMENT_NODE)
          continue;

        // Fetch the children nodes
        NodeList nli = n1.getChildNodes();
        for (int j = 0; j < nli.getLength(); j++) {
          Node n2 = nli.item(j);

          // Ignore if it's not an element
          if (n2.getNodeType() != Node.ELEMENT_NODE)
            continue;
          Element elt = (Element) n2;

          switch (elt.getTagName()) {
            case "CryptoLibraryFunction":
              if (elt.getAttribute("value").equals("true"))
                this.isCrypto = true;
              break;

            case "Parameter": {
              Integer access = Integer.decode(elt.getAttribute("access")).intValue();
              Integer type = Integer.decode(elt.getAttribute("type")).intValue();
              String typeOther = elt.getAttribute("typeOther");
              String id = elt.getAttribute("id");
              String valueAtt = elt.getAttribute("value");
              if (valueAtt.equals("null"))
                valueAtt = "";

              if (TAttribute.isAValidId(id, false, false, false)) {
                if ((valueAtt.length() == 0) || (TAttribute.isAValidInitialValue(type, valueAtt))) {
                  if (type == TAttribute.NATURAL)
                    type = TAttribute.INTEGER;

                  TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                  ta.isAvatar = true;
                  this.parameters.add(ta);
                }
              }
            }

              break;

            case "Signal":
              String signal = elt.getAttribute("value");

              if (signal.equals("null"))
                break;

              AvatarSignal as = AvatarSignal.isAValidSignal(signal);
              if (as != null)
                this.signals.add(as);
              else
                TraceManager.addDev("Invalid signal ignored:" + signal);

              break;

            case "ReturnAttribute": {
              Integer access = Integer.decode(elt.getAttribute("access")).intValue();
              Integer type = Integer.decode(elt.getAttribute("type")).intValue();
              String typeOther = elt.getAttribute("typeOther");
              String id = elt.getAttribute("id");
              String valueAtt = elt.getAttribute("value");
              if (valueAtt.equals("null"))
                valueAtt = "";

              if (TAttribute.isAValidId(id, false, false, false) && TAttribute.isAValidInitialValue(type, valueAtt)) {
                if (type == TAttribute.NATURAL)
                  type = TAttribute.INTEGER;

                TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                ta.isAvatar = true;
                this.returnAttributes.add(ta);
              }
            }

              break;

            case "Attribute": {
              Integer access = Integer.decode(elt.getAttribute("access")).intValue();
              Integer type = Integer.decode(elt.getAttribute("type")).intValue();
              String typeOther = elt.getAttribute("typeOther");
              String id = elt.getAttribute("id");
              String valueAtt = elt.getAttribute("value");
              if (valueAtt.equals("null"))
                valueAtt = "";

              if (TAttribute.isAValidId(id, false, false, false) && TAttribute.isAValidInitialValue(type, valueAtt)) {
                if (type == TAttribute.NATURAL)
                  type = TAttribute.INTEGER;

                TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                ta.isAvatar = true;
                this.attributes.add(ta);
              }
            }

              break;

            case "Method":
              String method = elt.getAttribute("value");

              if (method.equals("null"))
                break;

              boolean implementation = false;
              if (method.startsWith("$")) {
                implementation = true;
                method = method.substring(1);
              }

              if (method.startsWith("aencrypt("))
                this.isCrypto = true;

              AvatarMethod am = AvatarMethod.isAValidMethod(method);
              if (am != null) {
                am.setImplementationProvided(implementation);
                this.methods.add(am);
              }

              break;
          }
        }
      }
    } catch (Exception e) {
      throw new MalformedModelingException();
    }
  }

  /*
   * public int getDefaultConnector() { return
   * TGComponentManager.AVATARBD_PORT_CONNECTOR; }
   */

  /**
   * Returns the panel corresponding to the state machine diagram that describes
   * the behaviour of this function.
   *
   * @return The panel of the corresponding state machine diagram.
   */
  public AvatarSMDPanel getAvatarSMDPanel() {
    return ((AvatarDesignPanel) (this.tdp.tp)).getAvatarSMDPanel(this.value);
  }

  /**
   * Removes the cryptographic primitives from the list of methods.
   */
  public void removeCryptoElements() {
    this.isCrypto = false;

    for (String method : AvatarMethod.cryptoMethods)
      this.removeMethodIfApplicable(method);
  }

  /**
   * Adds the cryptographic primitives to the list of methods.
   */
  public void addCryptoElements() {
    this.isCrypto = true;

    for (String method : AvatarMethod.cryptoMethods)
      this.addMethodIfApplicable(method);
  }

  /**
   * Removes a method from the list of methods if it exists.
   *
   * @param methodString The String corresponding to the method to remove.
   */
  private void removeMethodIfApplicable(String methodString) {
    Iterator<AvatarMethod> iterator = this.methods.iterator();
    while (iterator.hasNext()) {
      AvatarMethod am = iterator.next();
      // TODO: replace by a more OO way...
      if (am.toString().equals(methodString)) {
        iterator.remove();
        break;
      }
    }
  }

  /**
   * Adds a method to the list of methods if it doesn't already exist.
   *
   * @param methodString The String corresponding to the method to add.
   */
  private void addMethodIfApplicable(String methodString) {
    for (AvatarMethod am : this.methods)
      // TODO: replace by a more OO way...
      if (am.toString().equals(methodString))
        return;

    AvatarMethod am = AvatarMethod.isAValidMethod(methodString);
    if (am != null)
      this.methods.add(am);
  }

  @Override
  public String getOwnerName() {
    return this.getFunctionName();
  }

  @Override
  public LinkedList<TAttribute> getAttributeList() {
    LinkedList<TAttribute> list = new LinkedList<TAttribute>();
    list.addAll(this.parameters);
    list.addAll(this.returnAttributes);
    list.addAll(this.attributes);

    return list;
  }

  @Override
  public LinkedList<String> getAllTimerList() {
    LinkedList<String> v = new LinkedList<String>();

    for (TAttribute a : this.parameters)
      if (a.getType() == TAttribute.TIMER)
        v.add(a.getId());
    for (TAttribute a : this.returnAttributes)
      if (a.getType() == TAttribute.TIMER)
        v.add(a.getId());
    for (TAttribute a : this.attributes)
      if (a.getType() == TAttribute.TIMER)
        v.add(a.getId());

    return v;
  }

  @Override
  public TAttribute getAttributeByName(String _name) {
    for (TAttribute a : this.parameters)
      if (a.getId().compareTo(_name) == 0)
        return a;
    for (TAttribute a : this.returnAttributes)
      if (a.getId().compareTo(_name) == 0)
        return a;
    for (TAttribute a : this.attributes)
      if (a.getId().compareTo(_name) == 0)
        return a;
    return null;
  }

  @Override
  public LinkedList<AvatarSignal> getSignalList() {
    return new LinkedList<AvatarSignal>(this.signals);
  }

  @Override
  public LinkedList<AvatarSignal> getAllSignalList() {
    return this.getSignalList();
  }

  @Override
  public AvatarSignal getSignalNameBySignalDef(String _id) {
    int index0 = _id.indexOf('(');
    if (index0 > -1)
      _id = _id.substring(0, index0);

    _id = _id.trim();
    for (AvatarSignal as : this.signals)
      if (as.getId().equals(_id))
        return as;

    return null;
  }

  @Override
  public void resizeWithFather() {
    if (this.father != null && this.father instanceof AvatarBDBlock) {
      // Too large to fit in the father? -> resize it!
      this.resizeToFatherSize();

      this.setCdRectangle(0, this.father.getWidth() - this.getWidth(), 0, this.father.getHeight() - this.getHeight());
      this.setMoveCd(this.x, this.y);
    }
  }

  @Override
  public LinkedList<AvatarMethod> getMethodList() {
    return new LinkedList<AvatarMethod>(this.methods);
  }

  @Override
  public LinkedList<AvatarMethod> getAllMethodList() {
    return this.getMethodList();
  }

  @Override
  public int compareTo(AvatarBDLibraryFunction f) {
    return this.name.compareTo(f.getFunctionName());
  }
}

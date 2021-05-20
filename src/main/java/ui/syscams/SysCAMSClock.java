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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogSysCAMSClock;
import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class SysCAMSClock Primitive Component. To be used in SystemC-AMS diagrams
 * Creation: 13/05/2018
 * 
 * @version 1.0 04/06/2019
 * @author Daniela GENIUS
 */

public class SysCAMSClock extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
  private String nameFn;
  private String code;
  private DefaultListModel<String> listStruct;
  private String nameTemplate;
  private String typeTemplate;
  private String valueTemplate;
  private String name;
  private String unit;
  private String unitStartTime;
  private double frequency;
  private double dutyCycle;
  private double startTime;
  private boolean posFirst;
  private DefaultListModel<String> listTypedef;

  private int maxFontSize = 14;
  private int minFontSize = 4;
  private int currentFontSize = -1;
  private Color myColor;

  private int textX = 15;
  private double dtextX = 0.0;

  public String oldValue;

  public SysCAMSClock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
      TDiagramPanel _tdp) {
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
    multieditable = true;
    editable = true;
    removable = true;
    userResizable = true;

    value = tdp.findSysCAMSPrimitiveComponentName("Clock");
    name = "Primitive component - Clock";

    setFrequency(-1);
    setUnit("");
    setStartTime(-1);
    setUnitStartTime("");
    setNameFn("");
    setCode("");
    setListStruct(new DefaultListModel<String>());
    setNameTemplate("");

    setListTypedef(new DefaultListModel<String>());

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
      g.setFont(f.deriveFont(Font.PLAIN));
    } else {
      g.setFont(f.deriveFont(Font.BOLD));
      g.drawString(value, x + (width - w) / 2, y + currentFontSize + textX);
      g.setFont(f.deriveFont(Font.PLAIN));
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
        if (oldValue.compareTo(s) != 0) {
          if (((SysCAMSComponentTaskDiagramPanel) (tdp)).nameBlockTDFComponentInUse(oldValue, s)) {
            JOptionPane.showMessageDialog(frame, "Error: the name is already in use", "Name modification",
                JOptionPane.ERROR_MESSAGE);
            return false;
          }
        }
        setComponentName(s);
        setValueWithChange(s);
        rescaled = true;
        return true;

      }
      return false;
    }

    JDialogSysCAMSClock jclk = new JDialogSysCAMSClock(this);
    jclk.setVisible(true);
    rescaled = true;
    return true;
  }

  public int getType() {
    return TGComponentManager.CAMS_CLOCK;
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
    return tgc instanceof SysCAMSClock;
  }

  public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
    if (tgc instanceof SysCAMSClock) {
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
      if (tgcomponent[i] instanceof SysCAMSClock) {
        tgcomponent[i].resizeWithFather();
      }
    }
    if (getFather() != null) {
      resizeWithFather();
    }
  }

  public void resizeWithFather() {
    if ((father != null) && (father instanceof SysCAMSCompositeComponent)) {
      resizeToFatherSize();

      setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
      setMoveCd(x, y);
    }
  }

  protected String translateExtraParam() {
    StringBuffer sb = new StringBuffer("<extraparam>\n");
    sb.append("<Attribute name_function=\"" + getNameFn());

    // sb.append("\" code=\"" + encode(getCode()));
    // sb.append("\" listStruct=\"" + splitParameters(getListStruct()));
    sb.append("\" nameTemplate=\"" + getNameTemplate());
    // sb.append("\" name=\"" + getName());
    sb.append("\" frequency =\"" + getFrequency());
    sb.append("\" unit=\"" + getUnit());
    sb.append("\" dutyCycle=\"" + getDutyCycle());
    sb.append("\" startTime=\"" + getStartTime());
    sb.append("\" unitStartTime=\"" + getUnitStartTime());
    sb.append("\" posFirst =\"" + getPosFirst());
    // sb.append("\" listTypedef=\"" + splitParameters(getListTypedef()));
    sb.append("\" />\n");
    sb.append("</extraparam>\n");
    return new String(sb);
  }

  public String splitParameters(DefaultListModel<String> listStruct) {
    String s = "";

    for (int i = 0; i < listStruct.getSize(); i++) {
      if (i < listStruct.getSize() - 1) {
        s = s + listStruct.get(i) + "|";
      } else {
        s = s + listStruct.get(i);
      }
    }
    return s;
  }

  public StringBuffer encode(String data) {
    StringBuffer databuf = new StringBuffer(data);
    StringBuffer buffer = new StringBuffer("");
    for (int pos = 0; pos != data.length(); pos++) {
      char c = databuf.charAt(pos);
      switch (c) {
        case '&':
          buffer.append("&amp;");
          break;
        case '\"':
          buffer.append("&quot;");
          break;
        case '\'':
          buffer.append("&apos;");
          break;
        case '<':
          buffer.append("&lt;");
          break;
        case '>':
          buffer.append("&gt;");
          break;
        default:
          buffer.append(databuf.charAt(pos));
          break;
      }
    }
    return buffer;
  }

  public StringBuffer decode(String data) {
    StringBuffer databuf = new StringBuffer(data);
    StringBuffer buffer = new StringBuffer("");
    int endline = 0;
    int nb_arobase = 0;
    int condition = 0;

    for (int pos = 0; pos != data.length(); pos++) {
      char c = databuf.charAt(pos);
      switch (c) {
        case '\n':
          break;
        case '\t':
          break;
        case '{':
          buffer.append("{\n");
          endline = 1;
          nb_arobase++;
          break;
        case '}':
          if (nb_arobase == 1) {
            buffer.append("}\n");
            endline = 0;
          } else {
            int i = nb_arobase;
            while (i > 1) {
              buffer.append("\t");
              i--;
            }
            buffer.append("}\n");
            endline = 1;
          }
          nb_arobase--;
          break;
        case ';':
          if (condition == 1) {
            buffer.append(";");
          } else {
            buffer.append(";\n");
            endline = 1;
          }
          break;
        case ' ':
          if (endline == 0) {
            buffer.append(databuf.charAt(pos));
          }
          break;
        case '(':
          buffer.append("(");
          condition = 1;
          break;
        case ')':
          buffer.append(")");
          condition = 0;
          break;
        default:
          if (endline == 1) {
            endline = 0;
            int i = nb_arobase;
            while (i >= 1) {
              buffer.append("\t");
              i--;
            }
          }
          buffer.append(databuf.charAt(pos));
          break;
      }
    }
    return buffer;
  }

  public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
    try {
      NodeList nli;
      Node n1, n2;
      Element elt;

      double frequency, dutyCycle, startTime;

      String code, nameFn, listStruct, nameTemplate, typeTemplate, valueTemplate, listTypedef;

      for (int i = 0; i < nl.getLength(); i++) {
        n1 = nl.item(i);
        if (n1.getNodeType() == Node.ELEMENT_NODE) {
          nli = n1.getChildNodes();
          for (int j = 0; j < nli.getLength(); j++) {
            n2 = nli.item(j);
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
              elt = (Element) n2;
              if (elt.getTagName().equals("Attribute")) {
                //
                // unit = elt.getAttribute("unit");
                // unitStartTime = elt.getAttribute("unitStartTime");
                // System.out.println("@@@ clock unit "+unit);
                // System.out.println("@@@ clock unit start time "+unitStartTime);

                frequency = Double.valueOf(elt.getAttribute("frequency")).doubleValue();
                unit = elt.getAttribute("unit");
                dutyCycle = Double.valueOf(elt.getAttribute("dutyCycle")).doubleValue();
                startTime = Double.valueOf(elt.getAttribute("startTime")).doubleValue();
                unitStartTime = elt.getAttribute("unitStartTime");
                posFirst = Boolean.valueOf(elt.getAttribute("posFirst")).booleanValue();
                listStruct = elt.getAttribute("listStruct");
                nameTemplate = elt.getAttribute("nameTemplate");
                typeTemplate = elt.getAttribute("typeTemplate");
                valueTemplate = elt.getAttribute("valueTemplate");
                listTypedef = elt.getAttribute("listTypedef");

                setFrequency(frequency);

                setUnit(unit);
                setDutyCycle(dutyCycle);
                setStartTime(startTime);
                setUnitStartTime(unitStartTime);
                setPosFirst(posFirst);
                String[] splita = listStruct.split("\\|");
                DefaultListModel<String> lista = new DefaultListModel<String>();
                for (String s : splita) {
                  if (!s.equals("")) {
                    lista.addElement(s);
                  }
                }
                setListStruct(lista);
                setNameTemplate(nameTemplate);

                setTypeTemplate(typeTemplate);
                setValueTemplate(valueTemplate);
                String[] splitb = listTypedef.split("\\|");
                DefaultListModel<String> listb = new DefaultListModel<String>();
                for (String s : splitb) {
                  if (!s.equals("")) {
                    listb.addElement(s);
                  }
                }
                setListTypedef(listb);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new MalformedModelingException();
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

  public void setNameFn(String nameFn) {
    this.nameFn = nameFn;
  }

  public void setCode(String _code) {
    code = _code;
  }

  public String getCode() {
    return code;
  }

  public String getNameFn() {
    return nameFn;
  }

  public DefaultListModel<String> getListStruct() {
    return listStruct;
  }

  public void setListStruct(DefaultListModel<String> _listStruct) {
    listStruct = _listStruct;
  }

  // public String getName() {
  // return name;
  // }

  public double getFrequency() {
    // System.out.println("@@@"+frequency);
    return frequency;
  }

  public String getUnit() {
    // System.out.println("@@@ clock unit get "+unit);
    return unit;
  }

  public String getUnitStartTime() {
    // System.out.println("@@@ clock unit start time get "+unitStartTime);
    return unitStartTime;
  }

  public double getDutyCycle() {
    return dutyCycle;
  }

  public double getStartTime() {
    return startTime;
  }

  public boolean getPosFirst() {
    return posFirst;
  }

  public void setName(String _name) {
    name = _name;
  }

  public void setStartTime(double _startTime) {
    startTime = _startTime;
  }

  public void setFrequency(double _frequency) {
    frequency = _frequency;
  }

  public void setUnit(String _unit) {
    unit = _unit;// System.out.println("@@@ clock unit "+unit);
  }

  public void setUnitStartTime(String _unitStartTime) {
    unitStartTime = _unitStartTime; // System.out.println("@@@ clock unit start time "+unitStartTime);
  }

  public void setDutyCycle(double _dutyCycle) {
    dutyCycle = _dutyCycle;
  }

  public void setPosFirst(boolean _posFirst) {
    posFirst = _posFirst;
  }

  public String getNameTemplate() {
    return nameTemplate;
  }

  public void setNameTemplate(String _nameTemplate) {
    nameTemplate = _nameTemplate;
  }

  public String getTypeTemplate() {
    return typeTemplate;
  }

  public void setTypeTemplate(String _typeTemplate) {
    typeTemplate = _typeTemplate;
  }

  public String getValueTemplate() {
    return valueTemplate;
  }

  public void setValueTemplate(String _valueTemplate) {
    valueTemplate = _valueTemplate;
  }

  public DefaultListModel<String> getListTypedef() {
    return listTypedef;
  }

  public void setListTypedef(DefaultListModel<String> _listTypedef) {
    listTypedef = _listTypedef;
  }
}

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

package ui.tmldd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import tmltranslator.modelcompiler.AdaifBuffer;
import tmltranslator.modelcompiler.Buffer;
import tmltranslator.modelcompiler.FepBuffer;
import tmltranslator.modelcompiler.InterleaverBuffer;
import tmltranslator.modelcompiler.MMBuffer;
import tmltranslator.modelcompiler.MapperBuffer;
import ui.ColorManager;
import ui.DesignPanel;
import ui.MalformedModelingException;
import ui.SwallowedTGComponent;
import ui.TAttribute;
import ui.TDiagramPanel;
import ui.TGCWithoutInternalComponent;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.WithAttributes;
import ui.util.IconManager;
import ui.window.JDialogPortArtifact;

/**
 * Class TMLArchiPortArtifact Communication Artifact of a deployment diagram
 * Creation: 22/11/2007
 * 
 * @version 1.0 22/11/2007
 * @author Ludovic APVRILLE
 */
public class TMLArchiPortArtifact extends TGCWithoutInternalComponent
    implements SwallowedTGComponent, WithAttributes, TMLArchiPortInterface {

  // Issue #31
  // protected int lineLength = 5;
  // protected int textX = 5;
  // protected int textY = 15;
  // protected int textY2 = 35;
  // protected int space = 5;
  // protected int fileX = 20;
  // protected int fileY = 25;
  // protected int cran = 5;
  private static final int SPACE = 5;
  private static final int CRAN = 5;
  private static final int FILE_X = 20;
  private static final int FILE_Y = 25;
  protected String mappedMemory = "VOID";
  protected String oldValue = "";
  protected String referenceCommunicationName = "TMLCommunication";
  protected String portName = "name";
  protected String typeName = "port";
  protected String startAddress = "";
  protected String endAddress = "";
  protected List<String> bufferParameters = new ArrayList<String>();
  protected String bufferType = "noBuffer";
  protected int priority = 5; // Between 0 and 10

  public TMLArchiPortArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
      TGComponent _father, TDiagramPanel _tdp) {
    super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

    // Issue #31
    // width = 75;
    // height = 40;
    minWidth = 75;
    textX = 5;
    textY = 15;
    initScaling(75, 40);

    nbConnectingPoint = 0;
    addTGConnectingPointsComment();

    moveable = true;
    editable = true;
    removable = true;

    value = "";
    portName = "name";
    referenceCommunicationName = "TMLCommunication";

    makeFullValue();

    myImageIcon = IconManager.imgic702;
  }

  @Override
  public boolean isHidden() {
    // TraceManager.addDev("Archi task artifact: Am I hidden?" + getValue());
    boolean ret = false;
    if (tdp != null) {
      if (tdp instanceof TMLArchiDiagramPanel) {
        ret = !(((TMLArchiDiagramPanel) (tdp)).inCurrentView(this));
      }
    }
    // TraceManager.addDev("Hidden? -> " + ret);
    return ret;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int _priority) {
    priority = _priority;
  }

  @Override
  protected void internalDrawing(Graphics g) {
    // Issue #31
    // if (oldValue.compareTo(value) != 0) {
    // setValue(value, g);
    // }
    checkWidth(g);

    g.drawRect(x, y, width, height);

    final int space = scale(SPACE);
    final int fileX = scale(FILE_X);
    final int fileY = scale(FILE_Y);
    final int cran = scale(CRAN);
    g.drawLine(x + width - space - fileX, y + space, x + width - space - fileX, y + space + fileY);
    g.drawLine(x + width - space - fileX, y + space, x + width - space - cran, y + space);
    g.drawLine(x + width - space - cran, y + space, x + width - space, y + space + cran);
    g.drawLine(x + width - space, y + space + cran, x + width - space, y + space + fileY);
    g.drawLine(x + width - space, y + space + fileY, x + width - space - fileX, y + space + fileY);
    g.drawLine(x + width - space - cran, y + space, x + width - space - cran, y + space + cran);
    g.drawLine(x + width - space - cran, y + space + cran, x + width - space, y + space + cran);

    // g.drawImage( scale( IconManager.img9 ), x+width - scale( space + fileX - 3 ),
    // y + scale( SPACE + 7 ), null);

    // g.drawImage(IconManager.img9, x+width-space-fileX + 3, y + space + 7, null);

    drawSingleString(g, value, x + textX, y + textY);

    Font f = g.getFont();
    g.setFont(f.deriveFont(Font.ITALIC));
    drawSingleString(g, typeName, x + textX, y + textY + 20);
    g.setFont(f);

    // Link to selected memory
    Color c = g.getColor();
    if (c == ColorManager.POINTER_ON_ME_0) {
      TDiagramPanel tdp = getTDiagramPanel();
      TGComponent tgc;
      if (tdp != null) {
        if (mappedMemory.length() > 0) {
          Iterator<TGComponent> iterator = tdp.getComponentList().listIterator();

          while (iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof TMLArchiMemoryNode) {
              if (tgc.getName().compareTo(mappedMemory) == 0) {
                GraphicLib.dashedLine(g, getX() + getWidth() / 2, getY() + getHeight() / 2,
                    tgc.getX() + tgc.getWidth() / 2, tgc.getY() + tgc.getHeight() / 2);
              }
            }
          }
        }
      }
    }
  }
  //
  // public void setValue(String val, Graphics g) {
  // oldValue = value;
  // int w = g.getFontMetrics().stringWidth(value);
  // int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
  //
  // //
  // if (w1 != width) {
  // width = w1;
  // resizeWithFather();
  // }
  // //
  // }

  @Override
  public void resizeWithFather() {
    if ((father != null) && (father instanceof TMLArchiCommunicationNode)) {
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

    // Get the list of all other TMLArchiPortArtifact.java and retrieve the mapped
    // ports
    Vector<String> portsList = this.getTDiagramPanel().getMGUI().getAllTMLInputPorts();

    // TraceManager.addDev( "bufferParameters before opening the window: " +
    // bufferParameters.toString() );
    JDialogPortArtifact dialog = new JDialogPortArtifact(frame, "Setting port artifact attributes", this, mappedMemory,
        portsList, value);
    dialog.setSize(700, 600);
    GraphicLib.centerOnParent(dialog);
    dialog.setVisible(true); // blocked until dialog has been closed
    mappedMemory = dialog.getMappedMemory();
    // bufferParameters = dialog.getBufferParameters(); //becomes empty if closing
    // the window without pushing Save
    // TraceManager.addDev( "bufferParameters after closing the window: " +
    // bufferParameters.toString() );
    if (bufferParameters.size() > 0) {
      bufferType = bufferParameters.get(Buffer.BUFFER_TYPE_INDEX);
    }
    /*
     * else { bufferType = Buffer.FEP_BUFFER; }
     */

    // TraceManager.addDev( "mapped Port: " + dialog.getMappedPort() );

    if (!dialog.isRegularClose()) {
      return false;
    }

    if (dialog.getReferenceCommunicationName() == null) {
      return false;
    }

    if (dialog.getReferenceCommunicationName().length() != 0) {
      tmp = dialog.getReferenceCommunicationName();
      referenceCommunicationName = tmp;

    }

    if (dialog.getCommunicationName().length() != 0) {
      tmp = dialog.getCommunicationName();

      if (!TAttribute.isAValidId(tmp, false, false, false, false, false)) {
        error = true;
      } else {
        portName = tmp;
      }
    }

    if (dialog.getTypeName().length() != 0) {
      typeName = dialog.getTypeName();
    }

    priority = 0;// dialog.getPriority(); //What is the purpose of priority for a port block?

    ((TMLArchiDiagramPanel) tdp).setPriority(getFullValue(), priority);

    if (error) {
      JOptionPane.showMessageDialog(frame, "Name is non-valid", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    makeFullValue();

    return !error;
  }

  private void makeFullValue() {
    value = referenceCommunicationName + "::" + portName;
  }

  @Override
  public TGComponent isOnMe(int _x, int _y) {
    if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
      return this;
    }
    return null;
  }

  @Override
  public int getType() {
    return TGComponentManager.TMLARCHI_PORT_ARTIFACT;
  }

  @Override
  protected String translateExtraParam() {
    StringBuffer sb = new StringBuffer("<extraparam>\n");
    sb.append("<info value=\"" + value + "\" portName=\"" + portName + "\" referenceCommunicationName=\"");
    sb.append(referenceCommunicationName);
    sb.append("\" typeName=\"" + typeName);
    sb.append("\" mappedMemory=\"" + mappedMemory);
    if (!bufferType.equals("") && !bufferType.equals("noBuffer")) {
      switch (Integer.parseInt(bufferType)) {
        case Buffer.FEP_BUFFER:
          sb.append(FepBuffer.appendBufferParameters(bufferParameters));
          break;
        case Buffer.INTERLEAVER_BUFFER:
          sb.append(InterleaverBuffer.appendBufferParameters(bufferParameters));
          break;
        case Buffer.ADAIF_BUFFER:
          sb.append(AdaifBuffer.appendBufferParameters(bufferParameters));
          break;
        case Buffer.MAPPER_BUFFER:
          sb.append(MapperBuffer.appendBufferParameters(bufferParameters));
          break;
        case Buffer.MAIN_MEMORY_BUFFER:
          sb.append(MMBuffer.appendBufferParameters(bufferParameters));
          break;
        default: // the fep buffer
          sb.append(FepBuffer.appendBufferParameters(bufferParameters));
          break;
      }
    }
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
      String svalue = null, sname = null, sreferenceCommunication = null, stype = null;
      // String prio = null;

      for (int i = 0; i < nl.getLength(); i++) {
        n1 = nl.item(i);
        //
        if (n1.getNodeType() == Node.ELEMENT_NODE) {
          nli = n1.getChildNodes();

          // Issue #17 copy-paste error on j index
          for (int j = 0; j < nli.getLength(); j++) {
            n2 = nli.item(j);
            //
            if (n2.getNodeType() == Node.ELEMENT_NODE) {
              elt = (Element) n2;
              if (elt.getTagName().equals("info")) {
                svalue = elt.getAttribute("value");
                // TraceManager.addDev( svalue );
                sname = elt.getAttribute("portName");
                // TraceManager.addDev( sname );
                sreferenceCommunication = elt.getAttribute("referenceCommunicationName");
                // TraceManager.addDev( referenceCommunicationName );
                stype = elt.getAttribute("typeName");
                // TraceManager.addDev( typeName );
                mappedMemory = elt.getAttribute("mappedMemory");
                // TraceManager.addDev( mappedMemory );
                // TraceManager.addDev( "bufferType = " + elt.getAttribute("bufferType") );
                if ((elt.getAttribute("bufferType") != null) && (elt.getAttribute("bufferType").length() > 0)) {
                  bufferType = elt.getAttribute("bufferType");
                  // TraceManager.addDev( bufferType );
                  // bufferParameters.add( bufferType );
                  switch (Integer.parseInt(bufferType)) {
                    case Buffer.FEP_BUFFER:
                      bufferParameters = FepBuffer.buildBufferParameters(elt);
                      break;
                    case Buffer.INTERLEAVER_BUFFER:
                      bufferParameters = InterleaverBuffer.buildBufferParameters(elt);
                      break;
                    case Buffer.ADAIF_BUFFER:
                      bufferParameters = AdaifBuffer.buildBufferParameters(elt);
                      break;
                    case Buffer.MAPPER_BUFFER:
                      bufferParameters = MapperBuffer.buildBufferParameters(elt);
                      break;
                    case Buffer.MAIN_MEMORY_BUFFER:
                      bufferParameters = MMBuffer.buildBufferParameters(elt);
                      break;
                    default: // the main memory buffer
                      bufferParameters = FepBuffer.buildBufferParameters(elt);
                      break;
                  }
                }
                // TraceManager.addDev( "Buffer parameters of " + sname + ":\n" +
                // bufferParameters.toString() );
                // prio = elt.getAttribute("priority");
              }
              if (svalue != null) {
                value = svalue;
              }
              if (sname != null) {
                portName = sname;
              }
              if (sreferenceCommunication != null) {
                referenceCommunicationName = sreferenceCommunication;
              }
              if (stype != null) {
                typeName = stype;
              }

              /*
               * if ((prio != null) && (prio.trim().length() > 0)) { priority =
               * Integer.decode(prio).intValue(); }
               */
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

  public String getReferenceCommunicationName() {
    return referenceCommunicationName;
  }

  public void setReferenceCommunicationName(String _referenceCommunicationName) {
    referenceCommunicationName = _referenceCommunicationName;
    makeFullValue();
  }

  public String getPortName() {
    return portName;
  }

  public String getFullValue() {
    String tmp = getValue();
    tmp += " (" + getTypeName() + ")";
    return tmp;
  }

  public String getTypeName() {
    return typeName;
  }

  @Override
  public String getAttributes() {
    return "Priority = " + priority;
  }

  public String getMappedMemory() {
    return mappedMemory;
  }

  public String getEndAddress() {
    return endAddress;
  }

  public String getStartAddress() {
    return startAddress;
  }

  public List<String> getBufferParameters() {
    return bufferParameters;
  }
}

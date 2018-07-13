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
import ui.window.JDialogADDCPUNode;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
   * Class ADDCPUNode
   * Node. To be used in AVATAR deployment diagrams.
   * Creation: 30/06/2014
   * @version 1.0 30/06/2014
   * @author Ludovic APVRILLE
 */
public class ADDCPUNode extends ADDNode implements SwallowTGComponent, WithAttributes {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "CPU";

    private int nbOfIrq = 6;
    private int iCacheWays = 0;
    private int iCacheSets = 0;
    private int iCacheWords = 0;
    private int dCacheWays = 0;
    private int dCacheSets = 0;
    private int dCacheWords = 0;

    protected int index = 0;
    protected int monitored = 0;


    public ADDCPUNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = 200;
        minWidth = 150;
        minHeight = 100;

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new ADDConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new ADDConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);

        connectingPoint[8] = new ADDConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new ADDConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new ADDConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new ADDConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("CPU");
        value = "name";

        myImageIcon = IconManager.imgic700;
    }

    public void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);


        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);

        // Filling color
        g.setColor(ColorManager.CPU_BOX_1);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
        g.setFont(f);
        w  = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (width - w)/2, y + textY2);

        // Icon
        g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
        g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
            return this;
        }

        return null;
    }

    public String getStereotype() {
        return stereotype;

    }

    public String getNodeName() {
        return name;
    }

    public boolean editOndoubleClick(JFrame frame) {
        boolean error = false;
        String errors = "";
        int tmp;
        String tmpName;

        JDialogADDCPUNode dialog = new JDialogADDCPUNode(frame, "Setting CPU attributes", this);
     //   dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog, 500, 450);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getNodeName().length() != 0) {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false)) {
                error = true;
                errors += "Name of the node  ";
            } else {
                name = tmpName;
            }
        }


        if (dialog.getNbOFIRQ().length() != 0) {
            try {
                tmp = nbOfIrq;
                nbOfIrq = Integer.decode(dialog.getNbOFIRQ()).intValue();
                if (nbOfIrq < 0) {
                    nbOfIrq = tmp;
                    error = true;
                    errors += "IRQ  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "IRQ  ";
            }
        }

        if (dialog.getICacheWays().length() != 0) {
            try {
                tmp = iCacheWays;
                iCacheWays = Integer.decode(dialog.getICacheWays()).intValue();
                if (iCacheWays < 0) {
                    iCacheWays = tmp;
                    error = true;
                    errors += "iCacheWays  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "iCacheWays  ";
            }
        }

        if (dialog.getICacheSets().length() != 0) {
            try {
                tmp = iCacheSets;
                iCacheSets = Integer.decode(dialog.getICacheSets()).intValue();
                if (iCacheSets < 0) {
                    iCacheSets = tmp;
                    error = true;
                    errors += "iCacheSets  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "iCacheSets  ";
            }
        }

        if (dialog.getICacheWords().length() != 0) {
            try {
                tmp = iCacheWords;
                iCacheWords = Integer.decode(dialog.getICacheWords()).intValue();
                if (iCacheWords < 0) {
                    iCacheWords = tmp;
                    error = true;
                    errors += "iCacheWords  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "iCacheWords  ";
            }
        }

        if (dialog.getDCacheWays().length() != 0) {
            try {
                tmp = dCacheWays;
                dCacheWays = Integer.decode(dialog.getDCacheWays()).intValue();
                if (dCacheWays < 0) {
                    dCacheWays = tmp;
                    error = true;
                    errors += "dCacheWays  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "dCacheWays  ";
            }
        }

        if (dialog.getDCacheSets().length() != 0) {
            try {
                tmp = dCacheSets;
                dCacheSets = Integer.decode(dialog.getDCacheSets()).intValue();
                if (dCacheSets < 0) {
                    dCacheSets = tmp;
                    error = true;
                    errors += "dCacheSets  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "dCacheSets  ";
            }
        }

        if (dialog.getDCacheWords().length() != 0) {
            try {
                tmp = dCacheWords;
                dCacheWords = Integer.decode(dialog.getDCacheWords()).intValue();
                if (dCacheWords < 0) {
                    dCacheWords = tmp;
                    error = true;
                    errors += "dCacheWords  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "dCacheWords  ";
            }
        }



        /*if (dialog.getClockRatio().length() != 0) {
          try {
          tmp = clockRatio;
          clockRatio = Integer.decode(dialog.getClockRatio()).intValue();
          if (clockRatio < 1) {
          clockRatio = tmp;
          error = true;
          errors += "Clock ratio  ";
          }
          } catch (Exception e) {
          error = true;
          errors += "Clock ratio  ";
          }
          }*/

	if (dialog.getIndex().length() != 0) {
            try {
                tmp = index;
                index = Integer.decode(dialog.getIndex()).intValue();
                if (index < 0) {
                    index = tmp;
                    error = true;
                    errors += "index ";
                }
            } catch (Exception e) {
                error = true;
                errors += "index  ";
            }
        }


	if (dialog.getMonitored() != 0) {
            try {
                tmp = monitored;

                monitored = dialog.getMonitored();//Integer.decode(dialog.getMonitored()).intValue();
                if (index < 0) {
                    monitored = tmp;
                    error = true;
                    errors += "monitored ";
                }
            } catch (Exception e) {
                error = true;
                errors += "monitored  ";
            }
        }

        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Invalid value for the following attributes: " + errors,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }


    public int getType() {
        return TGComponentManager.ADD_CPUNODE;
    }

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        //TraceManager.addDev("Accept swallowed?");
        return tgc instanceof ADDBlockArtifact;

    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //TraceManager.addDev("Add swallowed?");
        //Set its coordinates
        if (tgc instanceof ADDBlockArtifact) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            //TraceManager.addDev("Add swallowed!!!");
            addInternalComponent(tgc, 0);
            return true;
        }

        return false;

    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }


    public Vector<ADDBlockArtifact> getArtifactList() {
        Vector<ADDBlockArtifact> v = new Vector<ADDBlockArtifact>();
        
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ADDBlockArtifact) {
                v.add( (ADDBlockArtifact) tgcomponent[i] );
            }
        }
        
        return v;
    }

    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ADDBlockArtifact) {
                tgcomponent[i].resizeWithFather();
            }
        }

    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        sb.append("<attributes nbOfIrq=\"" + nbOfIrq + "\" ");
        sb.append(" iCacheWays=\"" + iCacheWays + "\" ");
        sb.append(" iCacheSets=\"" + iCacheSets + "\" ");
        sb.append(" iCacheWords=\"" + iCacheWords + "\" ");
        sb.append(" dCacheWays=\"" + dCacheWays + "\" ");
        sb.append(" dCacheSets=\"" + dCacheSets + "\" ");
        sb.append(" dCacheWords=\"" + dCacheWords + "\" ");
        //sb.append(" clockRatio=\"" + clockRatio + "\"");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            //int t1id;
            String sstereotype = null, snodeName = null;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null){
                                name = snodeName;
                            }

                            if (elt.getTagName().equals("attributes")) {

                                nbOfIrq = Integer.decode(elt.getAttribute("nbOfIrq")).intValue();
                                iCacheWays =Integer.decode(elt.getAttribute("iCacheWays")).intValue();
                                iCacheSets = Integer.decode(elt.getAttribute("iCacheSets")).intValue();
                                iCacheWords = Integer.decode(elt.getAttribute("iCacheWords")).intValue();
                                dCacheWays =Integer.decode(elt.getAttribute("dCacheWays")).intValue();
                                dCacheSets = Integer.decode(elt.getAttribute("dCacheSets")).intValue();
                                dCacheWords = Integer.decode(elt.getAttribute("dCacheWords")).intValue();

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
        return TGComponentManager.ADD_CONNECTOR;
    }


    public String getAttributes() {
        String attr = "";
        attr += "Nb of irq = " + nbOfIrq + "\n";
        attr += "iCacheWays = " + iCacheWays + "\n";
        attr += "iCacheSets = " + iCacheSets + "\n";
        attr += "iCacheWords = " + iCacheWords + "\n";
        attr += "dCacheWays = " + dCacheWays + "\n";
        attr += "dCacheSets = " + dCacheSets + "\n";
        attr += "dCacheWords = " + dCacheWords + "\n";

        return attr;
    }

    public int getNbOfIRQs() {
        return nbOfIrq;
    }

    public int getICacheWays() {
        return iCacheWays;
    }

    public int getICacheSets() {
        return iCacheSets;
    }

    public int getICacheWords() {
        return iCacheWords;
    }

    public int getDCacheWays() {
        return dCacheWays;
    }

    public int getDCacheSets() {
        return dCacheSets;
    }

    public int getDCacheWords() {
        return dCacheWords;
    }

    public int getIndex() {
        return index;
    }

    public int getMonitored() {
        return monitored;
    }

    public void setMonitored(int _monitored){
	monitored = _monitored;
    }

}

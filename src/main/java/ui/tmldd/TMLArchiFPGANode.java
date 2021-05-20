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

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tmltranslator.HwFPGA;
import tmltranslator.modelcompiler.ArchUnitMEC;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogFPGANode;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Class TMLFPGACPUNode Node. To be used in TML architecture diagrams. Creation:
 * 07/02/2018
 *
 * @author Ludovic APVRILLE, Matteo BERTOLINO
 * @version 1.1 07/02/2018
 */
public class TMLArchiFPGANode extends TMLArchiNode
        implements SwallowTGComponent, WithAttributes, TMLArchiElementInterface {

    // Issue #31
    // Issue #31
    private static final int DERIVATION_X = 2;
    private static final int DERIVATION_Y = 3;
    private static final int MARGIN_Y_2 = 30;
    // private int textY1 = 15;
    // private int textY2 = 30;
    // private int derivationx = 2;
    // private int derivationy = 3;
    private String stereotype = "FPGA";

    private int byteDataSize = HwFPGA.DEFAULT_BYTE_DATA_SIZE;
    private int goIdleTime = HwFPGA.DEFAULT_GO_IDLE_TIME;
    private int maxConsecutiveIdleCycles = HwFPGA.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES;
    private int execiTime = HwFPGA.DEFAULT_EXECI_TIME;
    private int execcTime = HwFPGA.DEFAULT_EXECC_TIME;
    private int capacity = HwFPGA.DEFAULT_CAPACITY;
    private int mappingPenalty = HwFPGA.DEFAULT_MAPPING_PENALTY;
    private int reconfigurationTime = HwFPGA.DEFAULT_RECONFIGURATION_TIME;

    private String operation = "";

    private String scheduling = "";

    public TMLArchiFPGANode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
            TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
        // width = 250;
        // height = 200;
        minWidth = 150;
        minHeight = 100;
        textY = 15;
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);

        connectingPoint[8] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        // Issue #31
        initScaling(250, 200);

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("FPGA");
        value = "name";

        myImageIcon = IconManager.imgic1120;
    }

    @Override
    protected void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        // Top lines
        // Issue #31
        final int derivationX = scale(DERIVATION_X);
        final int derivationY = scale(DERIVATION_Y);
        g.drawLine(x, y, x + derivationX, y - derivationY);
        g.drawLine(x + width, y, x + width + derivationX, y - derivationY);
        g.drawLine(x + derivationX, y - derivationY, x + width + derivationX, y - derivationY);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationX, y - derivationY + height);
        g.drawLine(x + derivationX + width, y - derivationY, x + width + derivationX, y - derivationY + height);

        // Filling color
        g.setColor(ColorManager.CPU_BOX_1);
        g.fill3DRect(x + 1, y + 1, width - 1, height - 1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        drawSingleString(g, ster, x + (width - w) / 2, y + textY); // Issue #31
        g.setFont(f);
        w = g.getFontMetrics().stringWidth(name);
        // Issue #31
        final int marginY2 = scale(MARGIN_Y_2);
        drawSingleString(g, name, x + (width - w) / 2, y + marginY2);

        // Icon
        // Issue #31
        final int margin = scale(4);
        g.drawImage(scale(IconManager.imgic1120.getImage()), x + margin, y + margin, null);
        // g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        Polygon pol = new Polygon();
        pol.addPoint(x, y);

        // Issue #31
        final int derivationx = scale(DERIVATION_X);
        final int derivationy = scale(DERIVATION_Y);
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

    public String getNodeName() {
        return name;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        boolean error = false;
        String errors = "";
        int tmp;
        String tmpName;

        JDialogFPGANode dialog = new JDialogFPGANode(getTDiagramPanel().getMainGUI(), frame, "Setting FPGA attributes",
                this);
        dialog.setSize(700, 500);
        GraphicLib.centerOnParent(dialog, 500, 450);
        // dialog.show(); // blocked until dialog has been closed
        dialog.setVisible(true);

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getNodeName().length() != 0) {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false, false)) {
                error = true;
                errors += "Name of the node  ";
            } else {
                name = tmpName;
            }
        }

        if (dialog.getCapacity().length() != 0) {
            try {
                tmp = capacity;
                capacity = Integer.decode(dialog.getCapacity()).intValue();
                if (capacity <= 0) {
                    capacity = tmp;
                    error = true;
                    errors += "Capacity  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Capacity  ";
            }
        }

        if (dialog.getMappingPenalty().length() != 0) {
            try {
                tmp = mappingPenalty;
                mappingPenalty = Integer.decode(dialog.getMappingPenalty()).intValue();
                if (mappingPenalty < 0) {
                    mappingPenalty = tmp;
                    error = true;
                    errors += "Mapping penalty  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Mapping penalty  ";
            }
        }

        if (dialog.getReconfigurationTime().length() != 0) {
            try {
                tmp = reconfigurationTime;
                reconfigurationTime = Integer.decode(dialog.getReconfigurationTime()).intValue();
                if (reconfigurationTime <= 0) {
                    reconfigurationTime = tmp;
                    error = true;
                    errors += "Reconfiguration time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Reconfiguration time  ";
            }
        }

        if (dialog.getByteDataSize().length() != 0) {
            try {
                tmp = byteDataSize;
                byteDataSize = Integer.decode(dialog.getByteDataSize()).intValue();
                if (byteDataSize <= 0) {
                    byteDataSize = tmp;
                    error = true;
                    errors += "Data size  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Data size  ";
            }
        }

        if (dialog.getGoIdleTime().length() != 0) {
            try {
                tmp = goIdleTime;
                goIdleTime = Integer.decode(dialog.getGoIdleTime()).intValue();
                if (goIdleTime < 0) {
                    goIdleTime = tmp;
                    error = true;
                    errors += "Go idle time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Go idle time  ";
            }
        }

        if (dialog.getMaxConsecutiveIdleCycles().length() != 0) {
            try {
                tmp = goIdleTime;
                maxConsecutiveIdleCycles = Integer.decode(dialog.getMaxConsecutiveIdleCycles()).intValue();
                if (maxConsecutiveIdleCycles < 0) {
                    maxConsecutiveIdleCycles = tmp;
                    error = true;
                    errors += "Max consecutive idle cycles  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Max consecutive idle cycles  ";
            }
        }

        if (dialog.getExeciTime().length() != 0) {
            try {
                tmp = execiTime;
                execiTime = Integer.decode(dialog.getExeciTime()).intValue();
                if (execiTime < 0) {
                    execiTime = tmp;
                    error = true;
                    errors += "execi time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "execi time  ";
            }
        }

        if (dialog.getExeccTime().length() != 0) {
            try {
                tmp = execcTime;
                execcTime = Integer.decode(dialog.getExeccTime()).intValue();
                if (execcTime < 0) {
                    execcTime = tmp;
                    error = true;
                    errors += "execc time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "execc time  ";
            }
        }

        if (dialog.getClockRatio().length() != 0) {
            try {
                tmp = clockRatio;
                clockRatio = Integer.decode(dialog.getClockRatio()).intValue();
                if (clockRatio < 1) {
                    clockRatio = tmp;
                    error = true;
                    errors += "Clock divider  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Clock divider  ";
            }
        }

        customData = dialog.getCustomData();
        TraceManager.addDev("Custom Data=" + customData);

        operation = dialog.getOperation().trim();
        scheduling = dialog.getScheduling().trim();

        if (error) {
            JOptionPane.showMessageDialog(frame, "Invalid value for the following attributes: " + errors, "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_FPGANODE;
    }

    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof TMLArchiArtifact;
    }

    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {

        // Set its coordinates
        if (tgc instanceof TMLArchiArtifact) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }

        return false;
    }

    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public Vector<TMLArchiArtifact> getArtifactList() {
        Vector<TMLArchiArtifact> v = new Vector<TMLArchiArtifact>();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                v.add((TMLArchiArtifact) tgcomponent[i]);
            }
        }
        return v;
    }

    // Issue #31
    // public void hasBeenResized() {
    // for(int i=0; i<nbInternalTGComponent; i++) {
    // if (tgcomponent[i] instanceof TMLArchiArtifact) {
    // tgcomponent[i].resizeWithFather();
    // }
    // }
    // }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        sb.append("<attributes capacity=\"" + capacity + "\" byteDataSize=\"" + byteDataSize + "\" ");
        sb.append(" mappingPenalty=\"" + mappingPenalty + "\" ");
        sb.append(" reconfigurationTime=\"" + reconfigurationTime + "\" ");
        sb.append(" goIdleTime=\"" + goIdleTime + "\" ");
        sb.append(" maxConsecutiveIdleCycles=\"" + maxConsecutiveIdleCycles + "\" ");
        sb.append(" execiTime=\"" + execiTime + "\"");
        sb.append(" execcTime=\"" + execcTime + "\"");
        sb.append(" clockRatio=\"" + clockRatio + "\"");
        sb.append(" operation =\"" + operation + "\" ");
        sb.append(" scheduling =\"" + scheduling + "\" ");
        sb.append("/>\n");
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
            String sstereotype = null, snodeName = null;
            // String operationTypesTmp;

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
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null) {
                                name = snodeName;
                            }

                            if (elt.getTagName().equals("attributes")) {
                                try {
                                    // the "try" statement is for retro compatibility
                                    capacity = Integer.decode(elt.getAttribute("capacity")).intValue();
                                } catch (Exception e) {
                                }
                                byteDataSize = Integer.decode(elt.getAttribute("byteDataSize")).intValue();
                                mappingPenalty = Integer.decode(elt.getAttribute("mappingPenalty")).intValue();
                                goIdleTime = Integer.decode(elt.getAttribute("goIdleTime")).intValue();
                                reconfigurationTime = Integer.decode(elt.getAttribute("reconfigurationTime"))
                                        .intValue();

                                if ((elt.getAttribute("execiTime") != null)
                                        && (elt.getAttribute("execiTime").length() > 0)) {
                                    execiTime = Integer.decode(elt.getAttribute("execiTime")).intValue();
                                }
                                if ((elt.getAttribute("execcTime") != null)
                                        && (elt.getAttribute("execcTime").length() > 0)) {
                                    execcTime = Integer.decode(elt.getAttribute("execcTime")).intValue();
                                }
                                if ((elt.getAttribute("maxConsecutiveIdleCycles") != null)
                                        && (elt.getAttribute("maxConsecutiveIdleCycles").length() > 0)) {
                                    maxConsecutiveIdleCycles = Integer
                                            .decode(elt.getAttribute("maxConsecutiveIdleCycles")).intValue();
                                }
                                if ((elt.getAttribute("clockRatio") != null)
                                        && (elt.getAttribute("clockRatio").length() > 0)) {
                                    clockRatio = Integer.decode(elt.getAttribute("clockRatio")).intValue();
                                }
                                if ((elt.getAttribute("MECType") != null)
                                        && (elt.getAttribute("MECType").length() > 0)) {
                                    if (elt.getAttribute("MECType").length() > 1) { // old format
                                        MECType = ArchUnitMEC.Types.get(0);
                                    } else {
                                        MECType = ArchUnitMEC.Types.get(Integer.valueOf(elt.getAttribute("MECType")));
                                    }
                                }
                                operation = elt.getAttribute("operation");
                                if (operation == null) {
                                    operation = "";
                                }

                                scheduling = elt.getAttribute("scheduling");
                                if (scheduling == null) {
                                    scheduling = "";
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException(e);
        }
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getByteDataSize() {
        return byteDataSize;
    }

    public int getReconfigurationTime() {
        return reconfigurationTime;
    }

    public int getGoIdleTime() {
        return goIdleTime;
    }

    public int getMaxConsecutiveIdleCycles() {
        return maxConsecutiveIdleCycles;
    }

    public int getExeciTime() {
        return execiTime;
    }

    public int getExeccTime() {
        return execcTime;
    }

    public int getMappingPenalty() {
        return mappingPenalty;
    }

    public String getOperation() {
        return operation;
    }

    public String getScheduling() {
        return scheduling;
    }

    @Override
    public String getAttributes() {
        String attr = "";
        attr += "Data size (in byte) = " + byteDataSize + "\n";
        attr += "Capacity = " + capacity + "\n";
        attr += "Mapping penalty (percentage) = " + mappingPenalty + "\n";
        attr += "Reconfiguration time = " + reconfigurationTime + "\n";
        attr += "Go in idle mode (in cycle) = " + goIdleTime + "\n";
        attr += "Idle cycles to go idle = " + maxConsecutiveIdleCycles + "\n";
        attr += "EXECI exec. time (in cycle) = " + execiTime + "\n";
        attr += "EXECC exec. time (in cycle) = " + execcTime + "\n";
        attr += "Operation  = " + operation + "\n";
        attr += "Clock divider = " + clockRatio + "\n";
        return attr;

    }

    @Override
    public int getComponentType() {
        return CONTROLLER;
    }
}

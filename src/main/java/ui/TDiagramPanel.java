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

package ui;

import myutil.*;
import myutilsvg.*;
import ui.atd.ATDAttack;
import ui.atd.ATDBlock;
import ui.avatarad.AvatarADActivity;
import ui.avatarbd.AvatarBDBlock;
import ui.avatarbd.AvatarBDPragma;
import ui.avatarbd.AvatarBDDataType;
import ui.avatarbd.AvatarBDLibraryFunction;
import ui.avatarcd.AvatarCDBlock;
import ui.avatarmad.AvatarMADAssumption;
import ui.avatarrd.AvatarRDRequirement;
import ui.avatarsmd.AvatarSMDState;
import ui.cd.*;
import ui.ftd.FTDFault;
import ui.eln.*;
import ui.eln.sca_eln.*;
import ui.eln.sca_eln_sca_de.*;
import ui.eln.sca_eln_sca_tdf.*;
import ui.syscams.*;
import ui.ncdd.NCEqNode;
import ui.ncdd.NCRouteArtifact;
import ui.ncdd.NCSwitchNode;
import ui.ncdd.NCTrafficArtifact;
import ui.oscd.TOSClass;
import ui.req.Requirement;
import ui.tmlcd.TMLTaskOperator;
import ui.tmlcompd.TMLCCompositeComponent;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLCPrimitivePort;
import ui.tmlcompd.TMLCRecordComponent;
import ui.window.JDialogCode;
import ui.window.JDialogNote;
import ui.window.JDialogSearchBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

// AVATAR

/**
 * Class TDiagramPanel
 * High level panel for all kind of TURTLE diagrams
 * Creation: 21/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/12/2003
 */
public abstract class TDiagramPanel extends JPanel implements GenericTree {

    protected TDiagramMouseManager tdmm;
    protected PanelKeyListener pkl;

    // for tracking changes
    public static final int NEW_COMPONENT = 0;
    public static final int NEW_CONNECTOR = 1;
    public static final int REMOVE_COMPONENT = 2;
    public static final int CHANGE_VALUE_COMPONENT = 3;
    public static final int MOVE_COMPONENT = 4;
    public static final int MOVE_CONNECTOR = 5;

    // Issue #14
    public static final int DIAGRAM_RESIZED = 6;

    // Issue #105
    public static final int SELECT_COMPONENT = 7;

    // For cut/copy/paste
    public static String copyData;
    /*private static int copyX;
      private static int copyY;
      private static int copyMaxId;*/

    protected String name;

    protected List<TGComponent> componentList;
    protected TGConnectingPoint selectedConnectingPoint;
    /*protected CAMSConnectingPoint selectedCAMSConnectingPoints;*/
    protected TGComponent componentPointed;
    protected TGComponent componentPopup;
    protected TToolBar ttb;
    protected TGComponent fatherOfRemoved;
    //author:huytruong
    protected TGComponent componentHovered;

    // popupmenus
    protected ActionListener menuAL;
    protected JPopupMenu diagramMenu;
    protected JPopupMenu componentMenu;
    protected JPopupMenu selectedMenu;
    protected int popupX, popupY;
    protected JMenuItem remove, edit, clone, bringFront, bringBack, makeSquare, setJavaCode, removeJavaCode, setInternalComment, removeInternalComment, attach, detach, hide, unhide, search, enableDisable, setAsCryptoBlock, setAsRegularBlock;
    protected JMenuItem checkAccessibility, checkInvariant, checkMasterMutex, checkLatency;
    protected JMenuItem gotoReference;
    protected JMenuItem showProVerifTrace;
    protected JMenuItem breakpoint;
    protected JMenuItem paste, insertLibrary, upX, upY, downX, downY, fitToContent, backToMainDiagram;
    protected JMenuItem cut, copy, saveAsLibrary, captureSelected;
    //author:huytruong
    //search dialog
    protected JDialogSearchBox j;
    //--

    // Main window
    protected MainGUI mgui;

    // Mouse pointer
    public int currentX;
    public int currentY;

    //drawing area
    private int minLimit = 10;
    private int maxX = 2500;
    private int maxY = 1500;
    private final int limit = 10;
    // Issue #14 Useless data
//    private final int minimumXSize = 900;
//    private final int minimumYSize = 900;
    protected final int increment = 500;

    private double zoom = 1.0;
    //   private boolean zoomed = false;

    private boolean draw;

    // Issue #14 point 10: Always use the current graphics
    //private Graphics lastGraphics;

    // MODE
    public int mode;

    public static final int NORMAL = 0;
    public static final int MOVING_COMPONENT = 1;
    public static final int ADDING_CONNECTOR = 2;
    public static final int MOVE_CONNECTOR_SEGMENT = 3;
    public static final int MOVE_CONNECTOR_HEAD = 4;
    public static final int SELECTING_COMPONENTS = 5;
    public static final int SELECTED_COMPONENTS = 6;
    public static final int MOVING_SELECTED_COMPONENTS = 7;
    public static final int RESIZING_COMPONENT = 8;

    // when adding connector or moving a connector head
    protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;
    protected Vector<Point> listPoint;
    protected TGConnectingPoint p1, p2;
    /* protected CAMSConnectingPoint cp1, cp2;*/
    protected int type;

    // For component selection
    protected int initSelectX;
    protected int initSelectY;
    protected int currentSelectX;
    protected int currentSelectY;
    protected int xSel, ySel, widthSel, heightSel;
    protected int sel = 5;
    protected boolean showSelectionZone = false;
    protected boolean selectedTemp = true;
    protected boolean select = false;

    private boolean isScaled;
    private boolean overcomeShowing = false;
    private boolean drawingMain = true;


    //protected Image offScreenBuffer;

    public JScrollDiagramPanel jsp;

    // for translation process
    public int count = 0;

    // Panels
    public TURTLEPanel tp;

    // For displaying or not parameters
    protected boolean synchroVisible = true, attributesVisible = true, gatesVisible = true, channelVisible = true;
    protected boolean javaVisible = false;
    protected int internalCommentVisible = 0;
    protected boolean channelsVisible = true, eventsVisible = true, requestsVisible = true;
    protected int attributesOn = 0;

    public final int OFF = 0;
    public final int PARTIAL = 1;
    public final int FULL = 2;

    int adjustMode = 0;

    // DIPLO ID -> for simulation purpose
    public static boolean DIPLO_ANIMATE_ON;
    public static boolean DIPLO_ID_ON;
    public static boolean DIPLO_TRANSACTION_PROGRESSION_ON;

    // TEPE ID -> for simulation purpose
    public static boolean TEPE_ID_ON;

    // AVATAR ID -> for simulation purpose
    public static boolean AVATAR_ID_ON;
    public static boolean AVATAR_ANIMATE_ON;


    public boolean drawable = true;
    protected static final int MOVE_SPEED = 1; //Speed of component moving with arrow keys


    // Constructor
    public TDiagramPanel(MainGUI _mgui, TToolBar _ttb) {
        setBackground(ColorManager.DIAGRAM_BACKGROUND);
        //setBackground(Color.red);
        //setMinimumSize(new Dimension(1000, 1000));
        //setMaximumSize(new Dimension(1000, 1000));
        setPreferredSize(new Dimension(maxX + limit, maxY + limit));
        componentList = new LinkedList<>();
        mgui = _mgui;
        ttb = _ttb;
        mode = NORMAL;

        setTdmm(new TDiagramMouseManager(this));
        addMouseListener(getTdmm());
        addMouseMotionListener(getTdmm());

        pkl = new PanelKeyListener(this);
        addKeyListener(pkl);

        setFocusable(true);

        buildPopupMenus();
    }

    // Abstract operations
    public abstract boolean actionOnDoubleClick(TGComponent tgc);

    public abstract boolean actionOnAdd(TGComponent tgc);

    public abstract boolean actionOnRemove(TGComponent tgc);

    public abstract boolean actionOnValueChanged(TGComponent tgc);

    public abstract String getXMLHead();

    public abstract String getXMLTail();

    public abstract String getXMLSelectedHead();

    public abstract String getXMLSelectedTail();

    public abstract String getXMLCloneHead();

    public abstract String getXMLCloneTail();

    public void setName(String _name) {
        name = _name;
    }

    public double getZoom() {
        return zoom;
    }

    /*public int getFontSize() {
      return (int)(Math.round(7.5*zoom+4.5));
      }*/

    public int getFontSize() {
        return (int) (Math.round(12 * zoom));
    }

    private FontMetrics savedFontMetrics = null;

    public int stringWidth(Graphics g, String str) {
        if (this.savedFontMetrics == null)
            this.savedFontMetrics = g.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, this.getFontSize()));
        return this.savedFontMetrics.stringWidth(str);
    }

    public void forceZoom(double _zoom) {
        zoom = _zoom;
    }

    public void setZoom(double _zoom) {
        //TraceManager.addDev("Begin Setting zoom of " + getName() + " to " + zoom + " current zoom=" + zoom + " maxX=" + maxX + " maxY=" + maxY);

        final double zoomChange = _zoom / zoom;

        if (_zoom < zoom) {
            if (zoom > 0.199) {
                zoom = _zoom;
                this.savedFontMetrics = null;
            }
        } else {
            if (zoom < 5) {
                zoom = _zoom;
                this.savedFontMetrics = null;
            }
        }

        // Issue #14: We need to resize the diagram as well
        final int maxXPrev = maxX;
        final int maxYPrev = maxY;
        maxX = (int) Math.round(zoomChange * maxX);
        maxY = (int) Math.round(zoomChange * maxY);

        if (maxXPrev != maxX || maxYPrev != maxY) {
            mgui.changeMade(this, DIAGRAM_RESIZED);
            updateSize();
        }

        updateComponentsAfterZoom();

        //TraceManager.addDev("end Setting zoom of " + getName() + " to " + zoom + " maxX=" + maxX + " maxY=" + maxY);
    }

    public boolean isDrawingMain() {
        return drawingMain;
    }

    protected void updateComponentsAfterZoom() {
        //TraceManager.addDev("Zoom factor=" + zoom);
        boolean change = false;

        for (TGComponent tgc : this.componentList) {
            //TraceManager.addDev("Testing for " + tgc.getClass());

            if (tgc instanceof ScalableTGComponent) {
                ((ScalableTGComponent) tgc).rescale(zoom);
                change = true;
            }
        }

        if (change) {
            mgui.changeMade(this, MOVE_COMPONENT);
            repaint();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void removeAll() {
        this.componentList = new LinkedList<>();
    }

    public void setInternalCommentVisible(int mode) {
        internalCommentVisible = mode;
    }

    public int getInternalCommentVisible() {
        return internalCommentVisible;
    }

    public void structureChanged() {
        for (TGComponent tgc : this.componentList)
            tgc.TDPStructureChanged();
    }

    public void setAttributes(int _attr) {
        attributesOn = _attr;
    }

    public int getAttributeState() {
        return attributesOn;
    }

    public void valueChanged() {
        for (TGComponent tgc : this.componentList)
            tgc.TDPvalueChanged();
    }

    public int makeLovelyIds(int id) {
        for (TGComponent tgc : this.componentList)
            id = tgc.makeLovelyIds(id);

        return id;
    }

    public void selectTab(String name) {
        mgui.selectTab(tp, name);
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintMycomponents(g);
    }

    public void paintMycomponents(Graphics g) {
        paintMycomponents(g, true, 1, 1);
    }

    protected void basicPaintMyComponents(Graphics g) {
        TGComponent tgc;
        for (int i = componentList.size() - 1; i >= 0; i--) {
            tgc = this.componentList.get(i);
            if (!tgc.isHidden()) {
                //TraceManager.addDev("Painting " + tgc.getName() + " x=" + tgc.getX() + " y=" + tgc.getY());
                tgc.draw(g);
            } else {
                //TraceManager.addDev("Ignoring " + tgc.getName() + " x=" + tgc.getX() + " y=" + tgc.getY());
            }
        }
    }

    private Font fontToUse = null;

    public void paintMycomponents(Graphics g, boolean b, double w, double h) {
        if (!drawable) {
            return;
        }

        if (this.fontToUse == null)
            this.fontToUse = g.getFont();
        else
            g.setFont(fontToUse);

        // this.lastGraphics = g;
        this.drawingMain = b;

        if (!this.overcomeShowing && !this.isShowing()) {
            TraceManager.addDev("Not showing!" + tp);
            return;
        }

        try {
            super.paintComponent(g);
        } catch (Exception e) {
            TraceManager.addDev("Got exception: " + e.getMessage());
            return;
        }

        if (!this.draw)
            return;

        // Draw Components
        if (w != 1.0 || h != 1.0) {
            ((Graphics2D) g).scale(w, h);
            this.isScaled = true;
        } else {
            this.isScaled = false;
        }

        // Draw every non hidden component
        TGComponent tgc;
        for (int i = this.componentList.size() - 1; i >= 0; i--) {
            tgc = this.componentList.get(i);
            if (tgc.isHidden())
                continue;

            tgc.draw(g);

            // CONNECTING POINTS
            if (this.mgui.getTypeButtonSelected() != TGComponentManager.EDIT)
                tgc.drawTGConnectingPoint(g, this.mgui.getIdButtonSelected());

            if (this.mode == MOVE_CONNECTOR_HEAD)
                tgc.drawTGConnectingPoint(g, this.type);

            if (this.javaVisible && (tgc.hasPostJavaCode() || tgc.hasPreJavaCode()))
                tgc.drawJavaCode(g);

            /*if (this instanceof CAMSBlockDiagramPanel) //Connecting points should always be visible in System-C AMS panels
                tgc.drawTGConnectingPoint(g, this.type);*/
        }

        // Draw name of component selected
        if (this.componentPointed != null) {
            String name1 = this.componentPointed.getName();
            if (this.componentPointed.hasFather())
                name1 = this.componentPointed.getTopLevelName() + ": " + name1;
        }

        //Draw component being added
        if (this.mode == ADDING_CONNECTOR) {
            // Drawing connector
            g.setColor(Color.red);
            this.drawConnectorBeingAdded(g);
            g.drawLine(x1, y1, x2, y2);
        }

        if (this.mode == SELECTING_COMPONENTS) {
            g.setColor(Color.black);
            GraphicLib.dashedRect(g,
                    Math.min(this.initSelectX, this.currentSelectX),
                    Math.min(this.initSelectY, this.currentSelectY),
                    Math.abs(this.currentSelectX - this.initSelectX),
                    Math.abs(this.currentSelectY - this.initSelectY));
            if (mgui.isExperimentalOn()) {
                g.drawString("" + currentSelectX, this.currentSelectX, initSelectY + 10);
                g.drawString("" + currentSelectY, initSelectX, currentSelectY);
            }
        }

        if ((this.mode == SELECTED_COMPONENTS || this.mode == MOVING_SELECTED_COMPONENTS) && this.selectedTemp) {
            if (this.showSelectionZone) {
                if (this.mode == MOVING_SELECTED_COMPONENTS)
                    g.setColor(ColorManager.MOVING_0);
                else
                    g.setColor(ColorManager.POINTER_ON_ME_0);

                GraphicLib.setMediumStroke(g);
            } else {
                g.setColor(ColorManager.NORMAL_0);
            }
            GraphicLib.dashedRect(g, xSel, ySel, widthSel, heightSel);
            g.fillRect(xSel - sel, ySel - sel, 2 * sel, 2 * sel);
            g.fillRect(xSel - sel + widthSel, ySel - sel, 2 * sel, 2 * sel);
            g.fillRect(xSel - sel, ySel - sel + heightSel, 2 * sel, 2 * sel);
            g.fillRect(xSel - sel + widthSel, ySel - sel + heightSel, 2 * sel, 2 * sel);
            if (showSelectionZone) {
                GraphicLib.setNormalStroke(g);
            }
        }

        // Draw new Component head
        if (mode == MOVE_CONNECTOR_HEAD) {
            g.setColor(ColorManager.MOVING_0);
            GraphicLib.dashedLine(g, x1, y1, x2, y2);
        }

        if ((this instanceof TDPWithAttributes) && (getAttributeState() != 0)) {
            //TraceManager.addDev("Tdp with attributes");
            for (int i = this.componentList.size() - 1; i >= 0; i--) {
                tgc = this.componentList.get(i);
                if (!tgc.isHidden()) {
                    tgc.drawWithAttributes(g);
                }
            }
        }

        if (b)
            mgui.drawBird();
    }

    public boolean isScaled() {
        return isScaled;
    }

    public void drawConnectorBeingAdded(Graphics g) {
        int s = listPoint.size();
        Point p3, p4;
        if (s > 0) {
            p3 = listPoint.elementAt(0);
            g.drawLine(p1.getX(), p1.getY(), p3.x, p3.y);
            for (int i = 0; i < s - 1; i++) {
                p3 = listPoint.elementAt(i);
                p4 = listPoint.elementAt(i + 1);
                g.drawLine(p3.x, p3.y, p4.x, p4.y);
            }
        }
    }

    /*protected void drawDIPLOID(Graphics g) {
      if (!(tdp instanceof TMLActivityDiagramPanel)) {
      return;
      }

      Color c = g.getColor();
      g.setColor(ColorManager.DIPLOID);

      TGComponent tgc;
      for(int i=componentList.size()-1; i>=0; i--) {
      tgc = (TGComponent)(componentList.get(i));
      if (!tgc.isHidden()) {
      if (tgc.getDIPLOID
      g.drawString(tgc.getDIPLOID(), x+width, y.height + 5);
      }
      }

      g.setColor(c);
      }*/

    public void loadFromXML(String s) {
        this.componentList = new LinkedList<>();

        mode = NORMAL;
    }

    public StringBuffer saveSelectedInXML() {
        StringBuffer s = componentsInXML(true);
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(getXMLSelectedHead());
        sb.append("\n");
        sb.append(s);
        sb.append("\n");
        sb.append(getXMLSelectedTail());

        //TraceManager.addDev("xml of selected components:" + sb);

        return sb;
    }

    public StringBuffer saveInXML() {
        StringBuffer s = componentsInXML(false);
        if (s == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer(getXMLHead());
        sb.append("\n");
        sb.append(s);
        sb.append("\n");
        sb.append(getXMLTail());
        return sb;
    }

    public StringBuffer saveComponentInXML(TGComponent tgc) {
        StringBuffer sb = new StringBuffer(getXMLCloneHead());
        sb.append("\n");
        sb.append(tgc.saveInXML());
        sb.append("\n");
        sb.append(getXMLCloneTail());
        //TraceManager.addDev("sb=\n" + sb);
        return sb;
    }

    private StringBuffer componentsInXML(boolean selected) {
        StringBuffer sb = new StringBuffer();
        StringBuffer s;

        //Added by Solange to see the components in the list
        //    LinkedList<TGComponent> ruteoList = this.componentList;
        //
        for (TGComponent tgc : this.componentList) {
            if ((selected == false) || (tgc.isSelected())) {
                s = tgc.saveInXML();
                if (s == null) {
                    return null;
                }
                sb.append(s);
                sb.append("\n");
            }
        }

        return sb;
    }

    public void activateActions(boolean b) {
        ttb.setActive(b);
    }

    // Selecting components
    public int selectComponentInRectangle(int x, int y, int width, int height) {
        //TraceManager.addDev("select components in rectangle x=" + x + " y=" + y + " width=" +width + " height=" + height);
        int cpt = 0;

        for (TGComponent tgc : this.componentList) {
            if (tgc.areAllInRectangle(x, y, width, height)) {
                tgc.select(true);
                //TraceManager.addDev("Selection of " + tgc);
                tgc.setState(TGState.SELECTED);
                cpt++;
            } else {
                //TraceManager.addDev("unselection of " + tgc);
                tgc.select(false);
                tgc.setState(TGState.NORMAL);
            }
        }

        return cpt;
    }

    public void setSelectedTGConnectingPoint(TGConnectingPoint p) {
        selectedConnectingPoint = p;
    }

    //author: huytruong
    public byte hoveredComponent(int x, int y) {
        TGComponent tgcTmp;
        //int state;
        //boolean b = false;
        boolean hoveredElementFound = false;
        byte info = 0;

        TGComponent tmp = componentHovered;
        componentHovered = null;

        for (TGComponent tgc : this.componentList) {
            //state = tgc.getState();
            tgcTmp = tgc.isOnMeHL(x, y);
            if (tgcTmp != null) {
                if (!hoveredElementFound) {
                    componentHovered = tgcTmp;
                    tgc.setState(TGState.POINTER_ON_ME);
                    hoveredElementFound = true;
                    info = 2;
                } else {
                    tgc.setState(TGState.NORMAL);
                }
            } else {
                tgc.setState(TGState.NORMAL);
            }
        }

        if (tmp != componentHovered) {
            info++;
        }

        return info;
    }





    //author:huytruong
    public TGComponent componentHovered() {
        return componentHovered;
    }
    //--

    // Highlighting elements

    // -> 0 No highlighted component, no change
    // -> 1 No highlighted, change
    // -> 2 One component highlighted, no change
    // -> 3 One component highlighted, change
    public byte highlightComponent(int x, int y) {
        TGComponent tgcTmp;
        //int state;
        //boolean b = false;
        boolean pointedElementFound = false;
        byte info = 0;

        TGComponent tmp = componentPointed;
        componentPointed = null;
        this.setToolTipText(null);
        for (TGComponent tgc : this.componentList) {
            //state = tgc.getState();
            tgcTmp = tgc.isOnMeHL(x, y);
            if ((tgcTmp != null && (!select || tgcTmp.isClickSelected())) || tgc.isClickSelected()) {
                if (!pointedElementFound) {
                    componentPointed = tgcTmp;
                    if (componentPointed == null)
                        componentPointed = tgc;
                    tgc.setState(TGState.POINTED);
                    String tooltip = componentPointed.getToolTipText();
                    if (tooltip != null && tooltip.length() > 0) {
                        this.setToolTipText(tooltip);
                    }
                    String tmpinfo = componentPointed.getStatusInformation();
                    if (tmpinfo != null) {
                        mgui.setStatusBarText(tmpinfo);
                    }
                    pointedElementFound = true;
                    info = 2;
                } else {
                    tgc.setState(TGState.NORMAL);
                }
            } else {
                if (tgcTmp != null && tgcTmp.father != null)
                    tgc.setState(TGState.POINTED);
                else
                    tgc.setState(TGState.NORMAL);
            }
        }

        if (tmp != componentPointed) {
            info++;
        }

        return info;
    }

    public void highlightTGComponent(TGComponent tgc) {
        if (!this.componentList.contains(tgc.getTopFather())) {
            return;
        }

        highlightComponent(-1, -1);


        if (tgc.getState() == TGState.NORMAL) {
            if (tgc.getTopFather() == tgc) {
                tgc.setSelectedInternalTGComponent(null);
            } else {
                tgc.getTopFather().setSelectedInternalTGComponent(tgc);
            }
            tgc.getTopFather().setState(TGState.POINTED);
            componentPointed = tgc;
            repaint();
        }
    }


    public TGComponent componentPointed() {
        return componentPointed;
    }

    public void updateJavaCode() {
        mgui.setJavaPreCode(componentPointed);
        mgui.setJavaPostCode(componentPointed);
    }

    public TGConnectingPoint findConnectingPoint(int id) {
        TGConnectingPoint p;

        for (TGComponent tgc : this.componentList) {
            p = tgc.findConnectingPoint(id);
            if (p != null) {
                return p;
            }
        }
        return null;

    }

    public TGComponent findComponentWithId(int id) {
        for (TGComponent tgc1 : this.componentList) {
            TGComponent tgc2 = tgc1.containsLoadedId(id);
            if (tgc2 != null)
                return tgc2;
        }

        return null;
    }

    public TGConnector findTGConnectorStartingAt(CDElement c) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TGConnector) {
                TGConnector tgco = (TGConnector) tgc;
                if (tgco.isP1(c))
                    return tgco;
            }

        return null;
    }

    public TGConnector findTGConnectorEndingAt(CDElement c) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TGConnector) {
                TGConnector tgco = (TGConnector) tgc;
                if (tgco.isP2(c))
                    return tgco;
            }

        return null;
    }


    public TGConnector findTGConnectorUsing(CDElement c) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TGConnector) {
                TGConnector tgco = (TGConnector) tgc;
                if (tgco.isP1(c))
                    return tgco;
                if (tgco.isP2(c))
                    return tgco;
            }

        return null;
    }

    public boolean highlightOutAndFreeConnectingPoint(int x, int y, int type) {
        boolean b = false;
        boolean pointedElementFound = false;
        selectedConnectingPoint = null;

        for (TGComponent tgc : this.componentList)
            if (pointedElementFound)
                b = tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            else {
                b = tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
                TGConnectingPoint cp = tgc.getFreeTGConnectingPointAtAndCompatible(x, y, type);
                if ((cp != null) && (cp.isOut()) && (cp.isFree()) && (cp.isCompatibleWith(type))) {
                    selectedConnectingPoint = cp;
                    pointedElementFound = true;
                    b = cp.setState(TGConnectingPoint.SELECTED) || b;
                } else
                    b = tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            }

        return b;
    }

    public boolean highlightInAndFreeConnectingPoint(int x, int y, int type) {
        TGConnectingPoint cp;
        //   int state;
        boolean b = false;
        boolean pointedElementFound = false;
        selectedConnectingPoint = null;

        for (TGComponent tgc : this.componentList) {
            if (pointedElementFound == true) {
                b = tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            }
            if (pointedElementFound == false) {
                cp = tgc.getFreeTGConnectingPointAtAndCompatible(x, y, type);
                if ((cp != null) && (cp.isIn()) && (cp.isFree())) {
                    selectedConnectingPoint = cp;
                    pointedElementFound = true;
                    b = cp.setState(TGConnectingPoint.SELECTED) || b;
                } else {
                    b = tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
                }
            }
        }
        return b;
    }


    public TGConnectingPoint getSelectedTGConnectingPoint() {
        return selectedConnectingPoint;
    }

    /* CAMSConnectingPoint getSelectedCAMSConnectingPoint() {
        return selectedCAMSConnectingPoints;
    }*/

    // Adding component
    public TGComponent addComponent(int x, int y, boolean swallow) {
        //TraceManager.addDev("Add component");
        TGComponent tgc = addComponent(x, y, mgui.getIdButtonSelected(), swallow);
        if (tgc instanceof ComponentPluginInterface) {
            ((ComponentPluginInterface) tgc).setPlugin(mgui.getPluginSelected());
        }
        return tgc;
    }

    public TGComponent addComponent(int x, int y, int id, boolean swallow) {
        TGComponent tgc = TGComponentManager.addComponent(x, y, id, this);
        addComponent(tgc, x, y, swallow, true);
        return tgc;
    }

    // return true if swallowed
    public boolean addComponent(TGComponent tgc, int x, int y, boolean swallow, boolean addToList) {
        boolean ret = false;
        //TraceManager.addDev("add component " + tgc.getName());
        if (tgc != null) {
            if ((swallow) && (tgc instanceof SwallowedTGComponent)) {
                //TraceManager.addDev("Swallowed component !");
                SwallowTGComponent stgc = findSwallowTGComponent(x, y, tgc);
                if (stgc != null) {
                    if (stgc.addSwallowedTGComponent(tgc, x, y)) {
                        tgc.wasSwallowed();
                        ret = true;
                    } else {
                        if (addToList) {
                            componentList.add(0, tgc);
                        }
                    }
                } else {
                    if (addToList) {
                        componentList.add(0, tgc);
                    }
                }
            } else {
                if (addToList) {
                    componentList.add(0, tgc);
                }
            }
        }

        if (tgc instanceof SpecificActionAfterAdd) {
            ((SpecificActionAfterAdd) tgc).specificActionAfterAdd();
        }

        return ret;
    }

    public SwallowTGComponent findSwallowTGComponent(int x, int y) {
        return findSwallowTGComponent(x, y, null);
    }

    public SwallowTGComponent findSwallowTGComponent(int x, int y, TGComponent tgcdiff) {
        for (TGComponent tgc : this.componentList)
            if ((tgc instanceof SwallowTGComponent) && (tgc.isOnMeHL(x, y) != null) && (tgc != tgcdiff))
                return ((SwallowTGComponent) tgc);

        return null;
    }

    public void addBuiltComponent(TGComponent tgc) {
        if (tgc != null) {
            this.componentList.add(tgc);
        }
    }

    public void addBuiltConnector(TGConnector tgc) {
        if (tgc != null) {
            this.componentList.add(tgc);
        }
    }

    public List<TGComponent> getComponentList() {
        return this.componentList;
    }

    public List<TGComponent> getAllComponentList() {

        List<TGComponent> ll = new LinkedList<TGComponent>();
        ll.addAll(this.componentList);

        for (TGComponent tgc : this.componentList)
            ll.addAll(tgc.getRecursiveAllInternalComponent());

        return ll;
    }

    // Adding connector
    public void addingTGConnector() {
        listPoint = new Vector<Point>();
        p1 = getSelectedTGConnectingPoint();
        x1 = p1.getX();
        y1 = p1.getY();
        selectedConnectingPoint.setFree(false);
    }

    public void setAddingTGConnector(int _x2, int _y2) {
        x2 = _x2;
        y2 = _y2;
    }

    public void addPointToTGConnector(int x, int y) {
        listPoint.addElement(new Point(x, y));
        x1 = x;
        y1 = y;
    }

    public void finishAddingConnector(TGConnectingPoint p2) {
        TGConnector tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), mgui.getIdButtonSelected(), this, p1, p2, listPoint);
        if (tgco != null) {
            TraceManager.addDev("Adding connector");
            p2.setFree(false);
            this.componentList.add(0, tgco);
            if (tgco instanceof SpecificActionAfterAdd) {
                ((SpecificActionAfterAdd) tgco).specificActionAfterAdd();
            }
            stopAddingConnector(false);
            p1.setFree(false);
            p1 = null;
            p2 = null;
        } else {
            TraceManager.addDev("Cancel adding connector");
            p2.setFree(true);
            stopAddingConnector(true);
            p1.setFree(true);
        }
    }

    // true if connector not added
    public void stopAddingConnector(boolean b) {
        if (p1 != null) {
            p1.setFree(true);
        }
        x1 = -1;
        x2 = -1;
        y1 = -1;
        y2 = -1;
        listPoint = null;
    }

    /*public void addingCAMSConnector() {
        listPoint = new Vector<Point>();
        cp1 = getSelectedCAMSConnectingPoint();
        x1 = cp1.getX();
        y1 = cp1.getY();
        selectedConnectingPoint.setFree(false);
    }

    public void setAddingCAMSConnector(int _x2, int _y2) {
        x2 = _x2;
        y2 = _y2;
    }

    public void addPointToCAMSConnector(int x, int y) {
        listPoint.addElement(new Point(x, y));
        x1 = x;
        y1 = y;
    }

    public void finishAddingConnector(CAMSConnectingPoint cp2) {
        CAMSBlockConnector camsco = TGComponentManager.addCAMSConnector(cp1.getX(), cp1.getY(), mgui.getIdButtonSelected(), this, cp1, cp2, listPoint);
        if (camsco != null) {
            TraceManager.addDev("Adding connector");
            cp2.setFree(false);
            this.componentList.add(0, camsco);
            if (camsco instanceof SpecificActionAfterAdd) {
                ((SpecificActionAfterAdd) camsco).specificActionAfterAdd();
            }
            stopAddingConnector(false);
            cp1.setFree(false);
            cp1 = null;
            cp2 = null;
        } else {
            TraceManager.addDev("Cancel adding connector");
            cp2.setFree(true);
            stopAddingConnector(true);
            cp1.setFree(true);
        }
    }*/

// -------------mark


    public void setMovingHead(int _x1, int _y1, int _x2, int _y2) {
        x1 = _x1;
        y1 = _y1;
        x2 = _x2;
        y2 = _y2;
    }

    public void setConnectorHead(TGComponent tgc) {
        type = TGComponentManager.getType(tgc);
        //TraceManager.addDev("class tgc=" + tgc.getClass());
        //TraceManager.addDev("type=" + type);
    }


    // Multi-select
    public void setSelectingComponents(int x, int y) {
        x = Math.min(Math.max((int) (Math.floor(minLimit * zoom)), x), (int) (Math.ceil(maxX * zoom)));
        y = Math.min(Math.max((int) (Math.floor(minLimit * zoom)), y), (int) (Math.ceil(maxY * zoom)));
        //        x = Math.min(Math.max(minLimit*zoom, x), maxX*zoom);
        //y = Math.min(Math.max(minLimit*zoom, y), maxY*zoom);
        initSelectX = x;
        currentSelectX = x;
        initSelectY = y;
        currentSelectY = y;
    }

    public void updateSelectingComponents(int x, int y) {
        //x = Math.min(Math.max((int) Math.floor(minLimit * zoom), x), (int) Math.ceil(maxX * zoom));
        //y = Math.min(Math.max((int) Math.floor(minLimit * zoom), y), (int) Math.ceil(maxY * zoom));
        x = Math.min(Math.max(minLimit, x), maxX);
        y = Math.min(Math.max(minLimit, y), maxY);
        currentSelectX = x;
        currentSelectY = y;

        selectComponentInRectangle(Math.min(currentSelectX, initSelectX), Math.min(currentSelectY, initSelectY), Math.abs(currentSelectX - initSelectX), Math.abs(currentSelectY - initSelectY));
    }

    public void endSelectComponents() {
        int nb = selectComponentInRectangle(Math.min(currentSelectX, initSelectX), Math.min(currentSelectY, initSelectY), Math.abs(currentSelectX - initSelectX), Math.abs(currentSelectY - initSelectY));
        if (nb == 0) {
            mode = NORMAL;
            mgui.setMode(MainGUI.CUTCOPY_KO);
            mgui.setMode(MainGUI.EXPORT_LIB_KO);
            mgui.actions[TGUIAction.MOVE_ENABLED].setEnabled(false);
            mgui.actions[TGUIAction.ACT_DELETE].setEnabled(false);
        } else {
            TraceManager.addDev("Number of selected components:" + nb);
            mode = SELECTED_COMPONENTS;
            mgui.setMode(MainGUI.CUTCOPY_OK);
            mgui.setMode(MainGUI.EXPORT_LIB_OK);
            mgui.actions[TGUIAction.MOVE_ENABLED].setEnabled(true);
            mgui.actions[TGUIAction.ACT_DELETE].setEnabled(true);
            showSelectionZone = true;
            xSel = Math.min(currentSelectX, initSelectX);
            ySel = Math.min(currentSelectY, initSelectY);
            widthSel = Math.abs(currentSelectX - initSelectX);
            heightSel = Math.abs(currentSelectY - initSelectY);
        }
    }

    public void unselectSelectedComponents() {
        for (TGComponent tgc : this.componentList) {
            tgc.select(false);
            tgc.setState(TGState.NORMAL);
        }
    }

    /**
     * Unselect all components (triggered when a single click is done)
     *
     * @author Fabien Tessier
     */
    public void unselectClickSelectedComponents() {
        for (TGComponent tgc : this.componentList) {
            tgc.select(false);
            tgc.clickSelect(false);
            tgc.setState(TGState.NORMAL);
            for (TGComponent tgcTmp : tgc.getRecursiveAllInternalComponent()) {
                tgcTmp.select(false);
                tgcTmp.clickSelect(false);
                tgcTmp.setState(TGState.NORMAL);
            }
        }
    }

    public boolean showSelectionZone(int x, int y) {
        if (GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel)) {
            if (!showSelectionZone) {
                showSelectionZone = true;
                return true;
            } else {
                return false;
            }
        } else {
            if (showSelectionZone) {
                showSelectionZone = false;
                return true;
            } else {
                return false;
            }
        }
    }

    public void setMovingSelectedComponents() {
        for (TGComponent tgc : this.componentList)
            if (tgc.isSelected())
                tgc.setState(TGState.MOVING);
    }

    public void setStopMovingSelectedComponents() {
        for (TGComponent tgc : this.componentList)
            if (tgc.isSelected())
                tgc.setState(TGState.SELECTED);
    }

    public int getXSelected() {
        return xSel;
    }

    public int getYSelected() {
        return ySel;
    }

    public boolean isInSelectedRectangle(int x, int y) {
        return GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel);
    }

    public void moveSelected(int x, int y) {
        x = Math.min(Math.max(minLimit, x), maxX - widthSel);
        y = Math.min(Math.max(minLimit, y), maxY - heightSel);

        int oldX = xSel;
        int oldY = ySel;
        xSel = x;
        ySel = y;
        
        for (TGComponent tgc : this.componentList) {
            if (tgc.isSelected()) {
                if ((xSel - oldX != 0 ) || (ySel - oldY != 0 )) {
                    /*TraceManager.addDev("" + tgc + " is selected oldX=" + xSel +
                            " oldY=" + oldY + " xSel=" + xSel + " ySel=" + ySel);*/
                }
                
                tgc.forceMove(xSel - oldX, ySel - oldY);
            }
        }
    }

    public TGComponent nextSelectedComponent() {
        for (TGComponent tgc : this.componentList)
            if (tgc.isSelected())
                return tgc;

        return null;
    }

    public Vector<TCDTClass> selectedTclasses() {
        Vector<TCDTClass> v = null;

        for (TGComponent tgc : this.componentList)
            if ((tgc.isSelected()) && (tgc instanceof TCDTClass)) {
                if (v == null)
                    v = new Vector<TCDTClass>();

                v.addElement((TCDTClass) tgc);
            }

        return v;
    }

    public Vector<TOSClass> selectedTURTLEOSClasses() {
        Vector<TOSClass> v = null;

        for (TGComponent tgc : this.componentList)
            if ((tgc.isSelected()) && (tgc instanceof TOSClass)) {
                if (v == null)
                    v = new Vector<TOSClass>();
                v.addElement((TOSClass) tgc);
            }

        return v;
    }

    public Vector<TMLTaskOperator> selectedTMLTasks() {
        Vector<TMLTaskOperator> v = null;

        for (TGComponent tgc : this.componentList)
            if ((tgc.isSelected()) && (tgc instanceof TMLTaskOperator)) {
                if (v == null)
                    v = new Vector<TMLTaskOperator>();
                v.addElement((TMLTaskOperator) tgc);
            }

        return v;
    }

    public Vector<AvatarBDBlock> selectedAvatarBDBlocks() {
        Vector<AvatarBDBlock> v = null;

        for (TGComponent tgc : this.componentList)
            if ((tgc.isSelected()) && (tgc instanceof AvatarBDBlock)) {
                if (v == null)
                    v = new Vector<AvatarBDBlock>();
                v.addElement((AvatarBDBlock) tgc);
                v.addAll(((AvatarBDBlock) tgc).getFullBlockList());
            }

        return v;
    }

    public Vector<TMLCPrimitiveComponent> selectedCPrimitiveComponent() {
        Vector<TMLCPrimitiveComponent> v = null;

        for (TGComponent tgc : this.componentList)
            if (tgc.isSelected()) {
                if (tgc instanceof TMLCPrimitiveComponent) {
                    if (v == null)
                        v = new Vector<TMLCPrimitiveComponent>();
                    v.addElement((TMLCPrimitiveComponent) tgc);
                }

                if (tgc instanceof TMLCCompositeComponent) {
                    if (v == null)
                        v = new Vector<TMLCPrimitiveComponent>();
                    v.addAll(((TMLCCompositeComponent) (tgc)).getAllPrimitiveComponents());
                }
            }

        return v;
    }

    // For Design panels (TURTLE, TURTLE-OS, etc.)
    public TClassSynchroInterface getTClass1ToWhichIamConnected(CompositionOperatorInterface coi) {

        TGConnector tgca = getTGConnectorAssociationOf(coi);
        TGComponent tgc;
        if (tgca != null) {
            tgc = getTopComponentToWhichBelongs(tgca.getTGConnectingPointP1());
            if ((tgc != null) && (tgc instanceof TClassInterface)) {
                return (TClassSynchroInterface) tgc;
            }
        }
        return null;
    }

    public TClassSynchroInterface getTClass2ToWhichIamConnected(CompositionOperatorInterface coi) {

        TGConnector tgca = getTGConnectorAssociationOf(coi);
        TGComponent tgc;
        if (tgca != null) {
            tgc = getTopComponentToWhichBelongs(tgca.getTGConnectingPointP2());
            if ((tgc != null) && (tgc instanceof TClassInterface)) {
                return (TClassSynchroInterface) tgc;
            }
        }
        return null;
    }

    public TGConnector getTGConnectorAssociationOf(CompositionOperatorInterface coi) {
        int i;
        TGConnectingPoint p1, p2;
        TGConnector tgco;
        TGConnectorAttribute tgca;
        TGComponent tgc;

        for (i = 0; i < coi.getNbConnectingPoint(); i++) {
            p1 = coi.tgconnectingPointAtIndex(i);
            tgco = getConnectorConnectedTo(p1);
            if ((tgco != null) && (tgco instanceof TGConnectorAttribute)) {
                tgca = (TGConnectorAttribute) tgco;
                if (p1 == tgca.getTGConnectingPointP1()) {
                    p2 = tgca.getTGConnectingPointP2();
                } else {
                    p2 = tgca.getTGConnectingPointP1();
                }

                // p2 now contains the connecting point of a association
                tgc = getComponentToWhichBelongs(p2);
                if ((tgc != null) && (!p2.isFree()) && (tgc instanceof TGConnectorAssociation)) {
                    return (TGConnectorAssociation) tgc;
                }
            }
        }
        return null;
    }


    // Popup menus

    private void buildComponentPopupMenu(TGComponent tgc, int x, int y) {
        // Component Menu
        componentMenu = new JPopupMenu();

        componentMenu.add(remove);
        componentMenu.add(edit);
        componentMenu.add(clone);
        componentMenu.add(bringFront);
        componentMenu.add(bringBack);
        componentMenu.add(enableDisable);
        componentMenu.add(makeSquare);
        componentMenu.addSeparator();
        componentMenu.add(attach);
        componentMenu.add(detach);
        componentMenu.addSeparator();
        componentMenu.add(hide);
        componentMenu.add(unhide);
        componentMenu.addSeparator();
        componentMenu.add(setAsCryptoBlock);
        componentMenu.add(setAsRegularBlock);
        componentMenu.add(setJavaCode);
        componentMenu.add(removeJavaCode);
        componentMenu.add(setInternalComment);
        componentMenu.add(removeInternalComment);
        componentMenu.add(checkAccessibility);
        componentMenu.add(checkInvariant);
        componentMenu.add(checkLatency);
        componentMenu.add(gotoReference);
        componentMenu.add(showProVerifTrace);
        componentMenu.add(checkMasterMutex);
        componentMenu.add(breakpoint);

        //author: huytruong
        componentMenu.add(search);

        tgc.addActionToPopupMenu(componentMenu, menuAL, x, y);
    }

    protected void buildDiagramPopupMenu() {
        // Component Menu
        diagramMenu = new JPopupMenu();

        diagramMenu.add(paste);
        diagramMenu.addSeparator();
        diagramMenu.add(insertLibrary);
        diagramMenu.addSeparator();
        diagramMenu.add(upX);
        diagramMenu.add(upY);
        diagramMenu.add(downX);
        diagramMenu.add(downY);
        diagramMenu.add(fitToContent);
        diagramMenu.add(backToMainDiagram);
    }

    private void buildSelectedPopupMenu() {
        selectedMenu = new JPopupMenu();

        selectedMenu.add(cut);
        selectedMenu.add(copy);
        selectedMenu.addSeparator();
        selectedMenu.add(saveAsLibrary);
        selectedMenu.addSeparator();
        selectedMenu.add(captureSelected);
    }


    private void buildPopupMenus() {
        menuAL = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                popupAction(e);
            }
        };

        remove = new JMenuItem("Remove");
        remove.addActionListener(menuAL);

        edit = new JMenuItem("Edit");
        edit.addActionListener(menuAL);

        clone = new JMenuItem("Clone");
        clone.addActionListener(menuAL);
        clone.setName("Clone");

        enableDisable = new JMenuItem("Enable/Disable");
        enableDisable.addActionListener(menuAL);

        bringFront = new JMenuItem("Bring to front");
        bringFront.addActionListener(menuAL);

        bringBack = new JMenuItem("Send to back");
        bringBack.addActionListener(menuAL);

        makeSquare = new JMenuItem("Make Square");
        makeSquare.addActionListener(menuAL);

        attach = new JMenuItem("Attach to a component");
        attach.addActionListener(menuAL);

        detach = new JMenuItem("Detach from a component");
        detach.addActionListener(menuAL);

        hide = new JMenuItem("Hide internal components");
        hide.addActionListener(menuAL);

        unhide = new JMenuItem("Show internal components");
        unhide.addActionListener(menuAL);


        setAsCryptoBlock = new JMenuItem("Set as crypto block");
        setAsCryptoBlock.addActionListener(menuAL);

        setAsRegularBlock = new JMenuItem("Set as regular block");
        setAsRegularBlock.addActionListener(menuAL);

        setJavaCode = new JMenuItem("Set Java code");
        setJavaCode.addActionListener(menuAL);

        removeJavaCode = new JMenuItem("Remove Java code");
        removeJavaCode.addActionListener(menuAL);

        setInternalComment = new JMenuItem("Set internal comment");
        setInternalComment.addActionListener(menuAL);

        removeInternalComment = new JMenuItem("Remove internal comment");
        removeInternalComment.addActionListener(menuAL);

        checkAccessibility = new JMenuItem("Check for Reachability / Liveness");
        checkAccessibility.addActionListener(menuAL);

        checkInvariant = new JMenuItem("Check for mutual exclusion");
        checkInvariant.addActionListener(menuAL);

        checkLatency = new JMenuItem("Set latency measurement checkpoint");
        checkLatency.addActionListener(menuAL);

        checkMasterMutex = new JMenuItem("Search for other states in mutual exclusion with");
        checkMasterMutex.addActionListener(menuAL);

        breakpoint = new JMenuItem("Add / remove breakpoint");
        breakpoint.addActionListener(menuAL);

        gotoReference = new JMenuItem("Go to reference");
        gotoReference.addActionListener(menuAL);
        
        showProVerifTrace= new JMenuItem("Show ProVerif Trace");
        showProVerifTrace.addActionListener(menuAL);

        search = new JMenuItem("External Search");
        search.addActionListener(menuAL);

        // Diagram Menu

        paste = new JMenuItem("Paste");
        paste.addActionListener(menuAL);

        insertLibrary = new JMenuItem("Insert Library");
        insertLibrary.addActionListener(menuAL);

        upX = new JMenuItem("Increase diagram width");
        upX.addActionListener(menuAL);

        upY = new JMenuItem("Increase diagram height");
        upY.addActionListener(menuAL);

        downX = new JMenuItem("Decrease diagram width");
        downX.addActionListener(menuAL);

        downY = new JMenuItem("Decrease diagram height");
        downY.addActionListener(menuAL);

        fitToContent = new JMenuItem("Adjust diagram size to content");
        fitToContent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fitDiagramSizeToContent();
            }
        });

        //Issue #62: Provide quick navigation to main diagram
        backToMainDiagram = new JMenuItem("Back to main diagram");
        backToMainDiagram.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tp.tabbedPane.setSelectedIndex(0);
            }
        });

        // Selected Menu
        cut = new JMenuItem("Cut");
        cut.addActionListener(menuAL);

        copy = new JMenuItem("Copy");
        copy.addActionListener(menuAL);

        saveAsLibrary = new JMenuItem("Save as library");
        saveAsLibrary.addActionListener(menuAL);

        captureSelected = new JMenuItem("Save as an Image");
        captureSelected.addActionListener(menuAL);
    }

    public int getIncrement() {
        return increment;
    }

    // Issue #14
    private void fitDiagramSizeToContent() {
        maxX = getRealMaxX();
        maxY = getRealMaxY();
        mgui.changeMade(this, DIAGRAM_RESIZED);
        updateSize();
    }

    private boolean canFitDiagramSizeToContent() {
        return maxX != getRealMaxX() || maxY != getRealMaxY();
    }

    private void increaseDiagramWidth() {
        maxX += increment;
        mgui.changeMade(this, DIAGRAM_RESIZED);
        updateSize();
    }

    private void increaseDiagramHeight() {
        maxY += increment;
        mgui.changeMade(this, DIAGRAM_RESIZED);
        updateSize();
    }

    private boolean canDecreaseMaxX() {
        return maxX - increment >= getRealMaxX();
    }

    private void decreaseDiagramWidth() {
        if (canDecreaseMaxX()) {
            maxX -= increment;
            mgui.changeMade(this, DIAGRAM_RESIZED);
            updateSize();
        }
    }

    private boolean canDecreaseMaxY() {
        return maxY - increment >= getRealMaxY();
    }

    private void decreaseDiagramHeight() {
        if (canDecreaseMaxY()) {
            maxY -= increment;
            mgui.changeMade(this, DIAGRAM_RESIZED);
            updateSize();
        }
    }

    private void popupAction(ActionEvent e) {
        if (e.getSource() == remove) {
            removeComponent(componentPopup);
            mgui.changeMade(this, REMOVE_COMPONENT);
            componentPopup = null;
            repaint();
            return;
        }

        if (e.getSource() == clone) {
            cloneComponent(componentPopup.getTopFather());
            repaint();
            return;
        }

        if (e.getSource() == enableDisable) {
            
        	// Issue #69
        	componentPopup.setEnabled( !componentPopup.isEnabled( true ) );
//            componentPopup.setEnabled(!componentPopup.isEnabled());
            getGUI().changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }

        if (e.getSource() == edit) {
            if (componentPopup.doubleClick(mgui.getFrame(), 0, 0)) {
                getGUI().changeMade(this, CHANGE_VALUE_COMPONENT);
                repaint();
            }
            return;
        }

        if (e.getSource() == bringFront) {
            bringToFront(componentPopup);
            mgui.changeMade(this, MOVE_COMPONENT);
            repaint();
            return;
        }

        if (e.getSource() == bringBack) {
            bringToBack(componentPopup);
            mgui.changeMade(this, MOVE_COMPONENT);
            repaint();
            return;
        }

        if (e.getSource() == makeSquare) {
            ((TGConnector) componentPopup).makeSquareWithoutMovingTGComponents();
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }

        if (e.getSource() == attach) {
            attach(componentPopup);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }

        if (e.getSource() == detach) {
            detach(componentPopup);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }

        if (e.getSource() == hide) {
            hide(componentPopup, true);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }

        if (e.getSource() == unhide) {
            hide(componentPopup, false);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }

        if (e.getSource() == setJavaCode) {
            boolean pre, post;
            pre = (componentPopup instanceof PreJavaCode);
            post = (componentPopup instanceof PostJavaCode);
            JDialogCode jdc = new JDialogCode(mgui.getFrame(), "Setting java code", componentPopup.getPreJavaCode(), pre, componentPopup.getPostJavaCode(), post);
            GraphicLib.centerOnParent(jdc);
            jdc.setVisible(true); // blocked until dialog has been closed

            componentPopup.setPreJavaCode(jdc.getPreCode());
            componentPopup.setPostJavaCode(jdc.getPostCode());
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }

        if ((e.getSource() == setAsCryptoBlock) || (e.getSource() == setAsRegularBlock)) {

            if (componentPopup instanceof AvatarBDBlock) {
                AvatarBDBlock bd = (AvatarBDBlock) componentPopup;
                if (bd.isCryptoBlock()) {
                    bd.removeCryptoElements();
                } else {
                    bd.addCryptoElements();
                }
                repaint();
                return;
            }
        }


        if (e.getSource() == removeJavaCode) {
            componentPopup.setPreJavaCode(null);
            componentPopup.setPostJavaCode(null);
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }

        if (e.getSource() == setInternalComment) {
            JDialogNote jdn = new JDialogNote(mgui.getFrame(), "Setting an internal comment", componentPopup.getInternalComment());
            GraphicLib.centerOnParent(jdn);
            jdn.setVisible(true); // blocked until dialog has been closed
            componentPopup.setInternalComment(jdn.getText());
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();

            return;
        }

        if (e.getSource() == removeInternalComment) {
            componentPopup.setInternalComment(null);
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }

        if (e.getSource() == checkAccessibility) {
            if (componentPopup instanceof CheckableAccessibility) {
                componentPopup.setCheckableAccessibility(!componentPopup.getCheckableAccessibility());

                // Issue #35: Enable the save button so that the change can be saved
                mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            }
        }

        if (e.getSource() == checkInvariant) {
            if (componentPopup instanceof CheckableInvariant) {
                componentPopup.setCheckableInvariant(!componentPopup.getCheckableInvariant());
            }
        }
        if (e.getSource() == checkLatency) {
            if (componentPopup instanceof CheckableLatency) {
                componentPopup.setCheckLatency(!componentPopup.getCheckLatency());
            }
        }

        if (e.getSource() == gotoReference) {
            if (componentPopup instanceof LinkedReference) {
                //Code for navigating to the diagram
                if (componentPopup.reference != null) {
                    TDiagramPanel refTDP = componentPopup.reference.getTDiagramPanel();
                    if (refTDP != null) {
                        mgui.selectTab(refTDP.tp);
                        mgui.selectTab(refTDP);
                        refTDP.highlightTGComponent(componentPopup.reference);
                    }

                }

            }
        }
        
        if (e.getSource() == showProVerifTrace) {
        	if (componentPopup instanceof TMLCPrimitivePort){
        		((TMLCPrimitivePort) componentPopup).showTrace();
        	}
        	else if (componentPopup instanceof AvatarBDBlock){
        		((AvatarBDBlock) componentPopup).showTrace(currentY);
        	}
        	else if (componentPopup instanceof AvatarBDPragma){
        		((AvatarBDPragma) componentPopup).showTrace(currentY);
        	}
        }
        
        if (e.getSource() == checkMasterMutex) {

            if (componentPopup instanceof CheckableInvariant) {

                TGComponent tmptgc = mgui.hasCheckableMasterMutex();
                //TraceManager.addDev("Element with Master mutex: " + tmptgc);
                if ((tmptgc != null) && (tmptgc != componentPopup)) {
                    tmptgc.setMasterMutex(false);
                }
                mgui.removeAllMutualExclusionWithMasterMutex();
                componentPopup.setMasterMutex(!componentPopup.getMasterMutex());
                componentPopup.setMutexWith(TGComponent.MUTEX_NOT_YET_STUDIED);
            }
        }


        if (e.getSource() == breakpoint) {
            if (componentPopup instanceof AllowedBreakpoint) {
                componentPopup.setBreakpoint(!componentPopup.getBreakpoint());
                if (componentPopup.getDIPLOID() != -1) {
                    if (componentPopup.getBreakpoint()) {
                        mgui.addBreakPoint(componentPopup.getDIPLOID());
                    } else {
                        mgui.removeBreakPoint(componentPopup.getDIPLOID());
                    }
                }
            }
        }

        //author: huytruong
        //event for selecting "search" option in popup menu
        if (e.getSource() == search) {
            mgui.showExternalSearch();
            return;
        }
        //--

        if (e.getSource() == upX) {
            increaseDiagramWidth();
//            maxX += increment;
//            updateSize();
            return;
        }

        if (e.getSource() == upY) {
            increaseDiagramHeight();
//            maxY += increment;
//            updateSize();
            return;
        }

        if (e.getSource() == downX) {

            // Issue #14
            decreaseDiagramWidth();
//        	maxX -= increment;
//            updateSize();
            return;
        }

        if (e.getSource() == downY) {

            // Issue #14
            decreaseDiagramHeight();
//            maxY -= increment;
//            updateSize();
            return;
        }

        /*if (e.getSource() == rename) {
          mgui.renameTab(this);
          return;
          }

          if (e.getSource() == delete) {
          mgui.deleteTab(this);
          return;
          }*/

        if (e.getSource() == cut) {
            makeCut();
            return;
        }

        if (e.getSource() == copy) {
            makeCopy();
        }

        if (e.getSource() == saveAsLibrary) {
            saveAsLibrary();
        }

        if (e.getSource() == captureSelected) {
            captureSelected();
        }

        if (e.getSource() == paste) {
            makePaste(popupX, popupY);
            return;
        }

        if (e.getSource() == insertLibrary) {
            insertLibrary(popupX, popupY);
            return;
        }

        if (componentPopup != null) {
            if (componentPopup.eventOnPopup(e)) {
                mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
                repaint();
                return;
            }
        }
    }

    public void openPopupMenu(int x, int y) {
        popupX = x;
        popupY = y;
        if (componentPointed != null) {
            componentPopup = componentPointed;
            buildComponentPopupMenu(componentPopup, x, y);
            setComponentPopupMenu();
            componentMenu.show(this, x, y);
            //TraceManager.addDev("closed");
        } else if ((mode == SELECTED_COMPONENTS) && (GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel))) {
            buildSelectedPopupMenu();
            setSelectedPopupMenu();
            selectedMenu.show(this, x, y);
        } else {
            buildDiagramPopupMenu();
            setDiagramPopupMenu();
            diagramMenu.show(this, x, y);
        }
    }

    private void setComponentPopupMenu() {

        //author: huytruong
        search.setEnabled(true);


        if (!componentPointed.isRemovable()) {
            remove.setEnabled(false);
        } else {
            remove.setEnabled(true);
        }

        if (!componentPointed.isEditable()) {
            edit.setEnabled(false);
        } else {
            edit.setEnabled(true);
        }

        if (componentPointed.isCloneable()) {
            /*if (componentPointed.hasFather()) {
              clone.setEnabled(false);
              } else {*/
            clone.setEnabled(true);
            //}
        } else {
            clone.setEnabled(false);
        }

        // Issue #69
        enableDisable.setEnabled( componentPointed.canBeDisabled() );
//        if (componentPointed instanceof CanBeDisabled) {
//            /*if (componentPointed.hasFather()) {
//              clone.setEnabled(false);
//              } else {*/
//            enableDisable.setEnabled(true);
//            //}
//        } else {
//            enableDisable.setEnabled(false);
//        }

        if (componentPointed instanceof SwallowedTGComponent) {
            if (componentPointed.getFather() == null) {
                attach.setEnabled(true);
                detach.setEnabled(false);
            } else {
                attach.setEnabled(false);
                detach.setEnabled(true);
            }
        } else {
            attach.setEnabled(false);
            detach.setEnabled(false);
        }

        if (componentPointed instanceof HiddenInternalComponents) {
            boolean hidden = ((HiddenInternalComponents) componentPointed).areInternalsHidden();
            hide.setEnabled(!hidden);
            unhide.setEnabled(hidden);
        } else {
            hide.setEnabled(false);
            unhide.setEnabled(false);
        }

        if (componentPointed instanceof TGConnector) {
            /*if (componentPointed.hasFather()) {
              clone.setEnabled(false);
              } else {*/
            makeSquare.setEnabled(true);
            //}
        } else {
            makeSquare.setEnabled(false);
        }

        if ((componentPointed instanceof PreJavaCode) || (componentPointed instanceof PostJavaCode)) {
            setJavaCode.setEnabled(true);
            removeJavaCode.setEnabled(true);
        } else {
            setJavaCode.setEnabled(false);
            removeJavaCode.setEnabled(false);
        }

        if (componentPointed instanceof AvatarBDBlock) {
            AvatarBDBlock block = (AvatarBDBlock) componentPointed;
            setAsCryptoBlock.setEnabled(!block.isCryptoBlock());
            setAsRegularBlock.setEnabled(block.isCryptoBlock());
        } else {
            setAsRegularBlock.setEnabled(false);
            setAsCryptoBlock.setEnabled(false);
        }


        if (componentPointed instanceof EmbeddedComment) {
            setInternalComment.setEnabled(true);
            removeInternalComment.setEnabled(true);
        } else {
            setInternalComment.setEnabled(false);
            removeInternalComment.setEnabled(false);
        }

        if (componentPointed instanceof CheckableAccessibility) {
            checkAccessibility.setEnabled(true);
        } else {
            checkAccessibility.setEnabled(false);

        }

        if (componentPointed instanceof CheckableLatency) {
            checkLatency.setEnabled(true);
        } else {
            checkLatency.setEnabled(false);

        }


        if (componentPointed instanceof LinkedReference) {
            gotoReference.setEnabled(true);
        } else {
            gotoReference.setEnabled(false);

        }
        
        if (componentPointed instanceof TMLCPrimitivePort || componentPointed instanceof AvatarBDBlock || componentPointed instanceof AvatarBDPragma){
        	showProVerifTrace.setEnabled(true);
        } else {
        	showProVerifTrace.setEnabled(false);
        }

        

        if (componentPointed instanceof CheckableInvariant) {
            checkInvariant.setEnabled(true);
            checkMasterMutex.setEnabled(true);
        } else {
            checkInvariant.setEnabled(false);
            checkMasterMutex.setEnabled(false);
        }

        if (componentPointed instanceof AllowedBreakpoint) {
            breakpoint.setEnabled(true);
        } else {
            breakpoint.setEnabled(false);
        }
    }

    private void setDiagramPopupMenu() {
        paste.setEnabled(copyData != null);
        insertLibrary.setEnabled(true);

//        if (maxX < minimumXSize + increment) {
//            downX.setEnabled(false);
//        } else {
        // Issue #14
        downX.setEnabled(canDecreaseMaxX());
//        }

//        if (maxY < minimumYSize + increment) {
//            downY.setEnabled(false);
//        } else {
        // Issue #14
        downY.setEnabled(canDecreaseMaxY());
//        }

        fitToContent.setEnabled(canFitDiagramSizeToContent());

        //Issue #62: Provide quick navigation to main diagram 
        backToMainDiagram.setEnabled(tp.tabbedPane.getSelectedIndex() != 0);
    }

    private void setSelectedPopupMenu() {
        cut.setEnabled(true);
        copy.setEnabled(true);
    }

    public void makeCut() {
        copyData = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        removeAllSelectedComponents();
        mgui.changeMade(this, REMOVE_COMPONENT);
        mode = NORMAL;
        mgui.setMode(MainGUI.PASTE_OK);
        repaint();
    }

    public void makeCopy() {
        copyData = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        mgui.setMode(MainGUI.PASTE_OK);
        return;
    }

    public void saveAsLibrary() {
        String data = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        mgui.saveAsLibrary(data);
        return;
    }

    private void captureSelected() {
        mgui.selectedCapture();
    }

    public void makeDelete() {
        //TraceManager.addDev("make delete");
        if (nextSelectedComponent() != null) {
            removeAllSelectedComponents();
        } else if (componentPointed != null) {
            removeComponent(componentPointed);
        } else {
            return;
        }
        mode = NORMAL;
        getTdmm().setSelection(-1, -1);
        mgui.setMode(MainGUI.CUTCOPY_KO);
        mgui.setMode(MainGUI.EXPORT_LIB_KO);
        mgui.changeMade(this, REMOVE_COMPONENT);
        repaint();
    }

    public void makePaste(int X, int Y) {
        if (copyData != null) {
            try {
                //TraceManager.addDev("Data to copy:" + copyData);
                mgui.gtm.copyModelingFromXML(this, copyData, X, Y);
            } catch (MalformedModelingException mme) {
                TraceManager.addDev("Paste Exception: " + mme.getMessage());
                JOptionPane.showMessageDialog(mgui.getFrame(), "Exception", "Paste failed", JOptionPane.INFORMATION_MESSAGE);
            }
            mgui.changeMade(this, NEW_COMPONENT);
            repaint();
        }
    }

    public void insertLibrary(int X, int Y) {
        String data = mgui.loadLibrary();
        //TraceManager.addDev(data);
        if (data != null) {
            try {
                mgui.gtm.copyModelingFromXML(this, data, X, Y);
            } catch (MalformedModelingException mme) {
                TraceManager.addDev("Insert Library Exception: " + mme.getMessage());
                JOptionPane.showMessageDialog(mgui.getFrame(), "Exception", "insertion of library has failed", JOptionPane.INFORMATION_MESSAGE);
            }
            mgui.changeMade(this, NEW_COMPONENT);
            repaint();
            // Added by Solange. It fills the lists of components, interfaces, etc
            if (tp instanceof ProactiveDesignPanel)
                mgui.gtm.generateLists((ProactiveDesignPanel) tp);
            //
        }
    }

    public void bringToBack(TGComponent tgc) {
        if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
            tgc.getFather().bringToBack(tgc);
        } else {
            tgc = tgc.getTopFather();
            //TraceManager.addDev("Bring front: " + tgc.getName());
            int index = componentList.indexOf(tgc);
            if (index > -1) {
                //TraceManager.addDev("Ok bring");
                componentList.remove(index);
                componentList.add(tgc);
            }
        }
    }

    public void bringToFront(TGComponent tgc) {
        if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
            tgc.getFather().bringToFront(tgc);
        } else {
            tgc = tgc.getTopFather();
            int index = componentList.indexOf(tgc);
            if (index > -1) {
                componentList.remove(index);
                componentList.add(0, tgc);
            }
        }
    }

    private void removeAllSelectedComponents() {
        TGComponent tgc = nextSelectedComponent();
        while (tgc != null) {
            removeComponent(tgc);
            tgc = nextSelectedComponent();
        }
    }


    // operations
    public void removeComponent(TGComponent tgc) {
        fatherOfRemoved = tgc.getFather();

        for (TGComponent t : this.componentList)
            if (t == tgc) {
                removeConnectors(tgc);
                if (tgc instanceof TGConnector) {
                    TGConnector tgcon = (TGConnector) tgc;
                    tgcon.getTGConnectingPointP1().setFree(true);
                    tgcon.getTGConnectingPointP2().setFree(true);
                }
                componentList.remove(tgc);
                actionOnRemove(tgc);
                tgc.actionOnRemove();
                return;
            } else {
                //TraceManager.addDev("Testing remove internal component");
                if (t.removeInternalComponent(tgc)) {
                    //TraceManager.addDev("Remove internal component");
                    removeConnectors(tgc);
                    return;
                }
            }
    }

    private void removeConnectors(TGComponent tgc) {
        for (int i = 0; i < tgc.getNbConnectingPoint(); i++) {
            TGConnectingPoint cp = tgc.tgconnectingPointAtIndex(i);
            Iterator<TGComponent> iterator = this.componentList.iterator();
            while (iterator.hasNext()) {
                TGComponent t = iterator.next();
                if (t instanceof TGConnector) {
                    TGConnector tgcon = (TGConnector) t;
                    if ((cp == tgcon.getTGConnectingPointP1()) || (cp == tgcon.getTGConnectingPointP2())) {
                        iterator.remove();
                        actionOnRemove(t);
                        tgcon.getTGConnectingPointP1().setFree(true);
                        tgcon.getTGConnectingPointP2().setFree(true);
                        for (int k = 0; k < tgcon.getNbConnectingPoint(); k++)
                            removeOneConnector(tgcon.tgconnectingPointAtIndex(k));
                    }
                }
            }
        }

        for (int i = 0; i < tgc.getNbInternalTGComponent(); i++)
            removeConnectors(tgc.getInternalTGComponent(i));
    }

    public void removeOneConnector(TGConnectingPoint cp) {
        Iterator<TGComponent> iterator = this.componentList.iterator();
        while (iterator.hasNext()) {
            TGComponent t = iterator.next();
            if (t instanceof TGConnector) {
                TGConnector tgcon = (TGConnector) t;
                if ((cp == tgcon.getTGConnectingPointP1()) || (cp == tgcon.getTGConnectingPointP2())) {
                    iterator.remove();
                    actionOnRemove(t);
                    tgcon.getTGConnectingPointP1().setFree(true);
                    tgcon.getTGConnectingPointP2().setFree(true);
                    TraceManager.addDev("Removed one connector!");
                    for (int k = 0; k < tgcon.getNbConnectingPoint(); k++)
                        removeOneConnector(tgcon.tgconnectingPointAtIndex(k));
                }
            }
        }
    }

    public void cloneComponent(TGComponent _tgc) {
        // copy
        String clone = mgui.gtm.makeXMLFromComponentOfADiagram(this, _tgc, getMaxIdSelected(), _tgc.getX(), _tgc.getY());

        //TraceManager.addDev("clone=\n"+ clone);

        // paste

        try {
            mgui.gtm.copyModelingFromXML(this, clone, _tgc.getX() + 50, _tgc.getY() + 25);
        } catch (MalformedModelingException mme) {
            TraceManager.addDev("Clone Exception: " + mme.getMessage());
            JOptionPane.showMessageDialog(mgui.getFrame(), "Clone creation failed", "Exception", JOptionPane.INFORMATION_MESSAGE);
        }
        bringToBack(_tgc);
        mgui.changeMade(this, NEW_COMPONENT);
    }

    public MainGUI getGUI() {
        return mgui;
    }


    public int getRawMinX() {
        return minLimit;
    }

    public int getRawMaxX() {
        return maxX;
    }

    public int getRawMinY() {
        return minLimit;
    }

    public int getRawMaxY() {
        return maxY;
    }

    public int getMaxX() {
        //return maxX;
        return (int) Math.ceil(maxX * zoom);
    }

    public int getMinX() {
        return (int) Math.floor(minLimit * zoom);
    }

    public int getMinY() {
        return (int) Math.floor(minLimit * zoom);
        //return minLimit*zoom;
    }

    public int getMaxY() {
        //return maxY;
        return (int) Math.ceil(maxY * zoom);
    }

    public void setMaxX(int x) {
        maxX = x;

    }

    public void setMinX(int x) {
        minLimit = x;
    }

    public void setMinY(int y) {
        minLimit = y;
    }

    public void setMaxY(int y) {
        maxY = y;
    }

    public void updateSize() {
        setPreferredSize(new Dimension(maxX + limit, maxY + limit));
        revalidate();
    }

    public void attach(TGComponent tgc) {
        if (tgc instanceof SwallowedTGComponent && tgc.tdp.addComponent(tgc, tgc.getX(), tgc.getY(), true, false))
            // Component was attached -> must be removed from the list
            this.componentList.remove(tgc);
    }

    public void detach(TGComponent tgc) {
        if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
            ((SwallowTGComponent) tgc.getFather()).removeSwallowedTGComponent(tgc);
            tgc.setFather(null);
            this.componentList.add(tgc);
            tgc.wasUnswallowed();
            bringToFront(tgc);
        }
    }

    public void hide(TGComponent tgc, boolean hide) {
        if (tgc instanceof HiddenInternalComponents) {
            ((HiddenInternalComponents) tgc).setInternalsHidden(hide);
        }
    }

    public String sizeParam() {
        String s = " minX=\"" + getRawMinX() + "\"";
        s += " maxX=\"" + getRawMaxX() + "\"";
        s += " minY=\"" + getRawMinY() + "\"";
        s += " maxY=\"" + getRawMaxY() + "\"";
        return s;
    }

    public String zoomParam() {
        String s = " zoom=\"" + getZoom() + "\"";
        return s;
    }

    //returns the highest id amongst its components
    public int getMaxId() {
        int ret = 0;
        for (TGComponent tgc : this.componentList)
            ret = Math.max(ret, tgc.getMaxId());

        return ret;
    }

    public int getMaxIdSelected() {
        int ret = 0;
        for (TGComponent tgc : this.componentList)
            if (tgc.isSelected())
                ret = Math.max(ret, tgc.getMaxId());

        return ret;
    }

    public void setDraw(boolean b) {
        draw = b;
    }

    public boolean getDraw() {
        return draw;
    }

    public TToolBar getToolBar() {
        return ttb;
    }

    // tell the other component connected to this connecting point
    public TGConnector getConnectorConnectedTo(TGConnectingPoint p) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TGConnector) {
                TGConnector tgco = (TGConnector) tgc;
                if ((tgco.getTGConnectingPointP1() == p) || (tgco.getTGConnectingPointP2() == p))
                    return tgco;
            }

        return null;
    }

    public TGComponent getNextTGComponent(TGComponent tgc, int index) {
        TGConnectingPoint pt2;
        TGConnector tgcon = getNextTGConnector(tgc, index);

        if (tgcon == null) {
            //TraceManager.addDev("TGCon is null");
            return null;
        }

        pt2 = tgcon.getTGConnectingPointP2();

        if (pt2 == null) {
            return null;
        }

        return getTopComponentToWhichBelongs(pt2);
    }

    public TGConnector getNextTGConnector(TGComponent tgc, int index) {
        TGConnectingPoint pt1;//, pt2;

        pt1 = tgc.getNextTGConnectingPoint(index);

        if (pt1 == null) {
            //TraceManager.addDev("pt1 is null");
            return null;
        }

        return getConnectorConnectedTo(pt1);
    }

    public TGComponent getTopComponentToWhichBelongs(TGConnectingPoint p) {
        TGComponent tgc = getComponentToWhichBelongs(p);
        if (tgc != null) {
            return tgc.topTGComponent();
        }
        return null;
    }
    
    public List<TGConnector> getConnectors() {
    	final List<TGConnector> connectors = new ArrayList<TGConnector>();
 
        for( final TGComponent compo : componentList ) {
        	if ( compo instanceof TGConnector ) {
        		connectors.add( (TGConnector) compo );
        	}
        }
        
        return connectors;
    }

    public TGComponent getComponentToWhichBelongs(TGConnectingPoint p) {
        for (TGComponent tgc1 : this.componentList) {
            TGComponent tgc2 = tgc1.belongsToMeOrSon(p);
            if (tgc2 != null)
                return tgc2;
        }

        return null;
    }

    public static TGComponent getComponentToWhichBelongs(List<TGComponent> components, TGConnectingPoint p) {
        for (TGComponent tgc1 : components) {
            TGComponent tgc2 = tgc1.belongsToMeOrSon(p);
            if (tgc2 != null)
                return tgc2;
        }

        return null;
    }

    public void getAllLatencyChecks(List<TGComponent> _list) {
        for (TGComponent tgc : this.componentList) {
            if (tgc.getCheckLatency()) {
                _list.add(tgc);
            }
        }
    }

    public void getAllCheckedTGComponent( List<TGComponent> _list) {
        for (TGComponent tgc : this.componentList)
            if (tgc.hasCheckedAccessibility())
                _list.addAll(tgc.getAllCheckedAccessibility());
    }

    public void getAllCheckableTGComponent(List<TGComponent> _list) {
        for (TGComponent tgc : this.componentList) {
            //if (tgc instanceof CheckableAccessibility) {
                _list.addAll(tgc.getAllCheckableAccessibility());
            //}

            //tgc.getAllCheckableTGComponent(_list);
        }

    }

    public void getAllCheckableInvariantTGComponent(List<TGComponent> _list) {
        for (TGComponent tgc : this.componentList)
            if (tgc.hasCheckableInvariant())
                _list.addAll(tgc.getAllCheckableInvariant());
    }

    // Main Tree
    public int getChildCount() {
        return this.componentList.size();
    }

    public Object getChild(int index) {
        return this.componentList.get(index);
    }

    public int getIndexOfChild(Object child) {
        return this.componentList.indexOf(child);
    }

    //Tclass
    private class NameChecker {
        public boolean isNameAlreadyTaken(TGComponent o, String name) {
            // Must deal with the case where mutliple the tested component
            // inherit from multiple classes / interfaces.
            // In such case we execute all check*** functions until one
            // returns true, in which case we can return true;
            return (o instanceof TClassInterface && this.checkTClassInterface((TClassInterface) o, name))
                    || (o instanceof TCDTData && this.checkTCDTData((TCDTData) o, name))
                    || (o instanceof TCDTObject && this.checkTCDTObject((TCDTObject) o, name))
                    || (o instanceof TOSClass && this.checkTOSClass((TOSClass) o, name))
                    || (o instanceof Requirement && this.checkRequirement((Requirement) o, name))
                    || (o instanceof TMLCPrimitiveComponent && this.checkTMLCPrimitiveComponent((TMLCPrimitiveComponent) o, name))
                    || (o instanceof TMLCRecordComponent && this.checkTMLCRecordComponent((TMLCRecordComponent) o, name))
                    || (o instanceof TMLCCompositeComponent && this.checkTMLCCompositeComponent((TMLCCompositeComponent) o, name))
                    || (o instanceof TMLTaskInterface && this.checkTMLTaskInterface((TMLTaskInterface) o, name))
                    || (o instanceof SysCAMSBlockTDF && this.checkSysCAMSBlockTDFComponent((SysCAMSBlockTDF) o, name))
                    || (o instanceof SysCAMSBlockDE && this.checkSysCAMSBlockDEComponent((SysCAMSBlockDE) o, name))
                    || (o instanceof SysCAMSCompositeComponent && this.checkSysCAMSCompositeComponent((SysCAMSCompositeComponent) o, name))
                    || (o instanceof ELNCluster && this.checkELNCluster((ELNCluster) o, name))
                    || (o instanceof ELNModule && this.checkELNModule((ELNModule) o, name))
                    || (o instanceof ELNNodeRef && this.checkELNComponentNodeRef((ELNNodeRef) o, name))
                    || (o instanceof ELNComponentResistor && this.checkELNComponentResistor((ELNComponentResistor) o, name))
                    || (o instanceof ELNComponentCapacitor && this.checkELNComponentCapacitor((ELNComponentCapacitor) o, name))
                    || (o instanceof ELNComponentInductor && this.checkELNComponentInductor((ELNComponentInductor) o, name))
                    || (o instanceof ELNComponentVoltageControlledVoltageSource && this.checkELNComponentVoltageControlledVoltageSource((ELNComponentVoltageControlledVoltageSource) o, name))
                    || (o instanceof ELNComponentVoltageControlledCurrentSource && this.checkELNComponentVoltageControlledCurrentSource((ELNComponentVoltageControlledCurrentSource) o, name))
                    || (o instanceof ELNComponentIdealTransformer && this.checkELNComponentIdealTransformer((ELNComponentIdealTransformer) o, name))
                    || (o instanceof ELNComponentTransmissionLine && this.checkELNComponentTransmissionLine ((ELNComponentTransmissionLine) o, name))
                    || (o instanceof ELNComponentIndependentVoltageSource && this.checkELNComponentIndependentVoltageSource((ELNComponentIndependentVoltageSource) o, name))
                    || (o instanceof ELNComponentIndependentCurrentSource && this.checkELNComponentIndependentCurrentSource((ELNComponentIndependentCurrentSource) o, name))
                    || (o instanceof ELNComponentCurrentSinkTDF && this.checkELNComponentCurrentSinkTDF((ELNComponentCurrentSinkTDF) o, name))
                    || (o instanceof ELNComponentCurrentSourceTDF && this.checkELNComponentCurrentSourceTDF((ELNComponentCurrentSourceTDF) o, name))
                    || (o instanceof ELNComponentVoltageSinkTDF && this.checkELNComponentVoltageSinkTDF((ELNComponentVoltageSinkTDF) o, name))
                    || (o instanceof ELNComponentVoltageSourceTDF && this.checkELNComponentVoltageSourceTDF((ELNComponentVoltageSourceTDF) o, name))
                    || (o instanceof ELNComponentCurrentSinkDE && this.checkELNComponentCurrentSinkDE((ELNComponentCurrentSinkDE) o, name))
                    || (o instanceof ELNComponentCurrentSourceDE && this.checkELNComponentCurrentSourceDE((ELNComponentCurrentSourceDE) o, name))
                    || (o instanceof ELNComponentVoltageSinkDE && this.checkELNComponentVoltageSinkDE((ELNComponentVoltageSinkDE) o, name))
                    || (o instanceof ELNComponentVoltageSourceDE && this.checkELNComponentVoltageSourceDE((ELNComponentVoltageSourceDE) o, name))
                    || (o instanceof ATDBlock && this.checkATDBlock((ATDBlock) o, name))
                    || (o instanceof ATDAttack && this.checkATDAttack((ATDAttack) o, name))
                    || (o instanceof FTDFault && this.checkFTDFault((FTDFault) o, name))
                    || (o instanceof AvatarBDBlock && this.checkAvatarBDBlock((AvatarBDBlock) o, name))
                    || (o instanceof AvatarCDBlock && this.checkAvatarCDBlock((AvatarCDBlock) o, name))
                    || (o instanceof AvatarSMDState && this.checkAvatarSMDState((AvatarSMDState) o, name))
                    || (o instanceof AvatarADActivity && this.checkAvatarADActivity((AvatarADActivity) o, name))
                    || (o instanceof AvatarMADAssumption && this.checkAvatarMADAssumption((AvatarMADAssumption) o, name))
                    || (o instanceof AvatarRDRequirement && this.checkAvatarRDRequirement((AvatarRDRequirement) o, name))
                    || (o instanceof NCEqNode && this.checkNCEqNode((NCEqNode) o, name))
                    || (o instanceof NCSwitchNode && this.checkNCSwitchNode((NCSwitchNode) o, name))
                    || (o instanceof AvatarBDDataType && this.checkAvatarBDDataType((AvatarBDDataType) o, name))
                    || (o instanceof AvatarBDLibraryFunction && this.checkAvatarBDLibraryFunction((AvatarBDLibraryFunction) o, name));
        }

        public boolean checkTClassInterface(TClassInterface o, String name) {
            return false;
        }

        public boolean checkTCDTData(TCDTData o, String name) {
            return false;
        }

        public boolean checkTCDTObject(TCDTObject o, String name) {
            return false;
        }

        public boolean checkTOSClass(TOSClass o, String name) {
            return false;
        }

        public boolean checkRequirement(Requirement o, String name) {
            return false;
        }

        public boolean checkTMLCPrimitiveComponent(TMLCPrimitiveComponent o, String name) {
            return false;
        }

        public boolean checkTMLCRecordComponent(TMLCRecordComponent o, String name) {
            return false;
        }

        public boolean checkTMLCCompositeComponent(TMLCCompositeComponent o, String name) {
            return false;
        }

        public boolean checkTMLTaskInterface(TMLTaskInterface o, String name) {
            return false;
        }
        
        public boolean checkSysCAMSBlockTDFComponent(SysCAMSBlockTDF o, String name) {
      		return false;
        }
        
        public boolean checkSysCAMSBlockDEComponent(SysCAMSBlockDE o, String name) {
        	return false;
        }
        
        public boolean checkSysCAMSCompositeComponent(SysCAMSCompositeComponent o, String name) {
        	return false;
        }

        public boolean checkELNCluster(ELNCluster o, String name) {
        	return false;
        }
        
        public boolean checkELNModule(ELNModule o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentNodeRef(ELNNodeRef o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentResistor(ELNComponentResistor o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentCapacitor(ELNComponentCapacitor o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentInductor(ELNComponentInductor o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageControlledVoltageSource(ELNComponentVoltageControlledVoltageSource o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageControlledCurrentSource(ELNComponentVoltageControlledCurrentSource o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentIdealTransformer(ELNComponentIdealTransformer o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentTransmissionLine(ELNComponentTransmissionLine o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentIndependentVoltageSource(ELNComponentIndependentVoltageSource o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentIndependentCurrentSource(ELNComponentIndependentCurrentSource o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentCurrentSinkTDF(ELNComponentCurrentSinkTDF o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentCurrentSourceTDF(ELNComponentCurrentSourceTDF o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageSinkTDF(ELNComponentVoltageSinkTDF o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageSourceTDF(ELNComponentVoltageSourceTDF o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentCurrentSinkDE(ELNComponentCurrentSinkDE o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentCurrentSourceDE(ELNComponentCurrentSourceDE o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageSinkDE(ELNComponentVoltageSinkDE o, String name) {
        	return false;
        }
        
        public boolean checkELNComponentVoltageSourceDE(ELNComponentVoltageSourceDE o, String name) {
        	return false;
        }

        public boolean checkATDBlock(ATDBlock o, String name) {
            return false;
        }
        
        public boolean checkATDAttack(ATDAttack o, String name) {
            return false;
        }

        public boolean checkFTDFault(FTDFault o, String name) {
            return false;
        }

        public boolean checkAvatarBDBlock(AvatarBDBlock o, String name) {
            return false;
        }

        public boolean checkAvatarCDBlock(AvatarCDBlock o, String name) {
            return false;
        }

        public boolean checkAvatarSMDState(AvatarSMDState o, String name) {
            return false;
        }

        public boolean checkAvatarADActivity(AvatarADActivity o, String name) {
            return false;
        }

        public boolean checkAvatarMADAssumption(AvatarMADAssumption o, String name) {
            return false;
        }

        public boolean checkAvatarRDRequirement(AvatarRDRequirement o, String name) {
            return false;
        }

        public boolean checkNCEqNode(NCEqNode o, String name) {
            return false;
        }

        public boolean checkNCSwitchNode(NCSwitchNode o, String name) {
            return false;
        }

        public boolean checkAvatarBDDataType(AvatarBDDataType o, String name) {
            return false;
        }

        public boolean checkAvatarBDLibraryFunction(AvatarBDLibraryFunction o, String name) {
            return false;
        }
    }

    private boolean isNameUnique(String name, NameChecker checker) {
        for (TGComponent o : this.componentList)
            if (checker.isNameAlreadyTaken(o, name))
                return false;
        return true;
    }

    private String findGoodName(String name, NameChecker checker) {
        // index >= 0 catch overflows
        for (int index = 0; index >= 0; index++) {
            String tryName = name + index;
            if (this.isNameUnique(tryName, checker))
                return tryName;
        }

        throw new RuntimeException("Integer Overflow");
    }

    public String findTClassName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkTClassInterface(TClassInterface o, String name) {
                return o.getClassName().equals(name);
            }

            public boolean checkTCDTData(TCDTData o, String name) {
                return o.getValue().equals(name);
            }
        });
    }

    public String findTOSClassName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkTOSClass(TOSClass o, String name) {
                return o.getClassName().equals(name);
            }
        });
    }

    public String findRequirementName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkRequirement(Requirement o, String name) {
                return o.getRequirementName().equals(name);
            }
        });
    }

    public String findTMLPrimitiveComponentName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkTMLCPrimitiveComponent(TMLCPrimitiveComponent o, String name) {
                return o.getValue().equals(name);
            }

            public boolean checkTMLCRecordComponent(TMLCRecordComponent o, String name) {
                return o.getValue().equals(name);
            }

            public boolean checkTMLCCompositeComponent(TMLCCompositeComponent o, String name) {
                for (int i = 0; i < o.getNbInternalTGComponent(); i++)
                    if (this.isNameAlreadyTaken(o.getInternalTGComponent(i), name))
                        return true;
                return false;
            }
        });
    }

    public String findTMLRecordComponentName(String name) {
        return this.findTMLPrimitiveComponentName(name);
    }

    public String findTMLTaskName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkTMLTaskInterface(TMLTaskInterface o, String name) {
                return o.getTaskName().equals(name);
            }
        });
    }

    public String findSysCAMSPrimitiveComponentName(String name) {
    	return this.findGoodName(name, new NameChecker() {
    		public boolean checkSysCAMSBlockTDFComponent(SysCAMSBlockTDF o, String name) {
    			return o.getValue().equals(name);
    		}
    		
    		public boolean checkSysCAMSBlockDEComponent(SysCAMSBlockDE o, String name) {
    			return o.getValue().equals(name);
    		}
    		
    		public boolean checkSysCAMSCompositeComponent(SysCAMSCompositeComponent o, String name) {
    			for (int i = 0; i < o.getNbInternalTGComponent(); i++)
    				if (this.isNameAlreadyTaken(o.getInternalTGComponent(i), name))
    					return true;
    			return false;
    		}
    	});
    }

    public String findELNComponentName(String name) {
    	return this.findGoodName(name, new NameChecker() {
    		public boolean checkELNComponentNodeRef(ELNNodeRef o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentResistor(ELNComponentResistor o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentCapacitor(ELNComponentCapacitor o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentInductor(ELNComponentInductor o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageControlledVoltageSource(ELNComponentVoltageControlledVoltageSource o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageControlledCurrentSource(ELNComponentVoltageControlledCurrentSource o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentIdealTransformer(ELNComponentIdealTransformer o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentTransmissionLine(ELNComponentTransmissionLine o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentIndependentVoltageSource(ELNComponentIndependentVoltageSource o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentIndependentCurrentSource(ELNComponentIndependentCurrentSource o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentCurrentSinkTDF(ELNComponentCurrentSinkTDF o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentCurrentSourceTDF(ELNComponentCurrentSourceTDF o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageSinkTDF(ELNComponentVoltageSinkTDF o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageSourceTDF(ELNComponentVoltageSourceTDF o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentCurrentSinkDE(ELNComponentCurrentSinkDE o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentCurrentSourceDE(ELNComponentCurrentSourceDE o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageSinkDE(ELNComponentVoltageSinkDE o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNComponentVoltageSourceDE(ELNComponentVoltageSourceDE o, String name) {
    			return o.getValue().equals(name);
    		}
    		public boolean checkELNModule(ELNModule o, String name) {
    			if (o.getValue().equals(name))
    				return true;
    			for (int i = 0; i < o.getNbInternalTGComponent(); i++)
    				if (this.isNameAlreadyTaken(o.getInternalTGComponent(i), name))
    					return true;
    			return false;
    		}
    		public boolean checkELNCluster(ELNCluster o, String name) {
    			if (o.getValue().equals(name))
    				return true;
    			for (int i = 0; i < o.getNbInternalTGComponent(); i++)
    				if (this.isNameAlreadyTaken(o.getInternalTGComponent(i), name))
    					return true;
    			return false;
    		}
    	});
    }
    
    public String findAttackName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkATDAttack(ATDAttack o, String name) {
                return o.getValue().equals(name);
            }
        });
    }

    public String findFaultName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkFTDFault(FTDFault o, String name) {
                return o.getValue().equals(name);
            }
        });
    }
    
    public String findBlockName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkATDBlock(ATDBlock o, String name) {
                return o.getName().equals(name);
            }
        });
    }

    public String findAvatarBDBlockName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarBDBlock(AvatarBDBlock o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasInternalBlockWithName(name);
            }

            public boolean checkAvatarBDLibraryFunction(AvatarBDLibraryFunction o, String name) {
                return o.getFunctionName().equals(name);
            }

            public boolean checkAvatarBDDataType(AvatarBDDataType o, String name) {
                return o.getDataTypeName().equals(name);
            }
        });
    }

    public String findAvatarCDBlockName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarCDBlock(AvatarCDBlock o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasInternalBlockWithName(name);
            }
        });
    }

    /*public String findCAMSBlockName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkCAMSBlock(CAMSBlock o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasBlockWithName();
            }
        });
    }*/

    public String findAvatarSMDStateName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarSMDState(AvatarSMDState o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasInternalStateWithName(name);
            }
        });
    }

    public String findAvatarADActivityName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarADActivity(AvatarADActivity o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasInternalActivityWithName(name);
            }
        });
    }

    public String findAvatarAssumptionName(String name, int start) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarMADAssumption(AvatarMADAssumption o, String name) {
                return o.getValue().equals(name);
            }
        });
    }

    public String findAvatarRequirementName(String name, int start) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkAvatarRDRequirement(AvatarRDRequirement o, String name) {
                return o.getValue().equals(name);
            }
        });
    }

    public String findAvatarRequirementID(String id) {
        try {
            // intid >= 0 catch overflows
            for (int intid = Integer.decode(id).intValue(); intid >= 0; intid++) {
                boolean ok = true;
                for (TGComponent o : this.componentList)
                    if (o instanceof AvatarRDRequirement) {
                        AvatarRDRequirement areq = (AvatarRDRequirement) o;
                        int otherid = Integer.decode(areq.getID()).intValue();
                        if (intid == otherid) {
                            ok = false;
                            break;
                        }
                    }

                if (ok)
                    return Integer.toString(intid);
            }
        } catch (NumberFormatException e) {
            return id;
        }

        throw new RuntimeException("Integer Overflow");
    }

    public String findTObjectName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkTCDTObject(TCDTObject o, String name) {
                return o.getObjectName().equals(name);
            }
            //            public boolean checkTCDTClass (TCDTClass o, String name) {
            //                return o.getClassName ().startsWith (name);
            //            }
        });
    }

    public String findTObjectName(String name1, String name2) {
        // index >= 0 catch overflows
        for (int index = 0; index >= 0; index++) {
            boolean ok = true;
            String tryName = name1 + index;

            for (TGComponent o : this.componentList)
                if (o instanceof TCDTObject && ((TCDTObject) o).getObjectName().equals(tryName))
                    ok = false;

            if (ok && this.isTObjectNameUnique(tryName + name2))
                return tryName;
        }


        throw new RuntimeException("Integer Overflow");
    }

    public String findNodeName(String name) {
        return this.findGoodName(name, new NameChecker() {
            public boolean checkNCEqNode(NCEqNode o, String name) {
                if (o.getName().equals(name))
                    return true;
                for (NCTrafficArtifact arti : o.getArtifactList())
                    if (arti.getValue().equals(name))
                        return true;
                return false;
            }

            public boolean checkNCSwitchNode(NCSwitchNode o, String name) {
                if (o.getName().equals(name))
                    return true;
                for (NCRouteArtifact arti : o.getArtifactList())
                    if (arti.getValue().equals(name))
                        return true;
                return false;
            }
            //            public boolean checkNCConnectorNode (NCConnectorNode o, String name) {
            //                return o.getInterfaceName ().equals (name);
            //            }
        });
    }

    public String findInterfaceName(String name) {
        return this.findNodeName(name);
    }

    public boolean isAlreadyATClassName(String name) {
        return !this.isTClassNameUnique(name);
    }

    public boolean isAlreadyATMLTaskName(String name) {
        return !this.isTMLTaskNameUnique(name);
    }

    public boolean isAlreadyAnAvatarBDBlockName(String name) {
        return !this.isAvatarBlockNameUnique(name);
    }

    public boolean isAlreadyATMLPrimitiveComponentName(String name) {
        return !this.isNameUnique(name, new NameChecker() {
            public boolean checkTMLCPrimitiveComponent(TMLCPrimitiveComponent o, String name) {
                return o.getValue().equals(name);
            }

            public boolean checkTMLCRecordComponent(TMLCRecordComponent o, String name) {
                return o.getValue().equals(name);
            }

            public boolean checkTMLCCompositeComponent(TMLCCompositeComponent o, String name) {
                for (int i = 0; i < o.getNbInternalTGComponent(); i++)
                    if (this.isNameAlreadyTaken(o.getInternalTGComponent(i), name))
                        return true;
                return false;
            }
        });
    }

    public boolean isAlreadyATOSClassName(String name) {
        return !this.isTOSClassNameUnique(name);
    }

    public boolean isTClassNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkTClassInterface(TClassInterface o, String name) {
                return o.getClassName().equals(name);
            }

            public boolean checkTCDTData(TCDTData o, String name) {
                return o.getValue().equals(name);
            }
        });
    }

    public boolean isTOSClassNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkTOSClass(TOSClass o, String name) {
                return o.getClassName().equals(name);
            }
        });
    }

    public boolean isTMLTaskNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkTMLTaskInterface(TMLTaskInterface o, String name) {
                return o.getTaskName().equals(name);
            }
        });
    }

    public boolean isBlockNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkATDBlock(ATDBlock o, String name) {
                return o.getName().equals(name);
            }
        });
    }

    /**
     * Check if any other <b>block, library function or data type</b>
     * (contrary to what the name suggests) has this name.
     *
     * @param name The name to check.
     * @return true if the name is unique, false otherwise.
     */
    public boolean isAvatarBlockNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkAvatarBDBlock(AvatarBDBlock o, String name) {
                if (o.getValue().equals(name))
                    return true;
                return o.hasInternalBlockWithName(name);
            }

            public boolean checkAvatarBDLibraryFunction(AvatarBDLibraryFunction o, String name) {
                return o.getFunctionName().equals(name);
            }

            public boolean checkAvatarBDDataType(AvatarBDDataType o, String name) {
                return o.getDataTypeName().equals(name);
            }
        });
    }

    public boolean isNCNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkNCEqNode(NCEqNode o, String name) {
                if (o.getName().equals(name))
                    return true;
                for (NCTrafficArtifact arti : o.getArtifactList())
                    if (arti.getValue().equals(name))
                        return true;
                return false;
            }

            public boolean checkNCSwitchNode(NCSwitchNode o, String name) {
                if (o.getName().equals(name))
                    return true;
                for (NCRouteArtifact arti : o.getArtifactList())
                    if (arti.getValue().equals(name))
                        return true;
                return false;
            }
            //            public boolean checkNCConnectorNode (NCConnectorNode o, String name) {
            //                return o.getInterfaceName ().equals (name);
            //            }
        });
    }

    public boolean isRequirementNameUnique(String name) {
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkRequirement(Requirement o, String name) {
                return o.getRequirementName().equals(name);
            }
        });
    }

    public boolean isTObjectNameUnique(String name) {
        // FIXME: this is not coherent with findTObjectName !!!
        return this.isNameUnique(name, new NameChecker() {
            public boolean checkTClassInterface(TClassInterface o, String name) {
                return o.getClassName().equals(name);
            }
        });
    }

    public void setMaxPanelSize(int x, int y) {
        maxX = x;
        maxY = y;
        updateSize();
    }

    // For compatibility with ttool v0.41
    // Assumes no internal duplicate id
    public void checkForDuplicateId() {
        for (int i = 0; i < componentList.size(); i++) {
            TGComponent tgc1 = componentList.get(i);
            for (int j = 0; j < componentList.size(); j++)
                if (j != i) {
                    TGComponent tgc2 = componentList.get(j);
                    tgc2 = tgc2.getIfId(tgc1.getId());
                    if (tgc2 != null) {
                        //TraceManager.addDev("*** Same ID ***");
                        //TraceManager.addDev("tgc1" + tgc1.getClass());
                        //TraceManager.addDev("tgc2" + tgc2.getClass());
                    }
                }
        }
    }

    /*public void findTGComponentWithId(int id, int index) {
      TGComponent tgc;

      for(int i=index; i<componentList.size(); i++) {

      }
      }*/

    public Vector<TCDTClass> getTClasses() {
        Vector<TCDTClass> v = new Vector<TCDTClass>();

        for (TGComponent o : this.componentList)
            if (o instanceof TCDTClass)
                v.add((TCDTClass) o);

        return v;
    }

    public Vector<String> getAllDataTypes() {
        Vector<String> v = new Vector<String>();

        for (TGComponent tgc : this.componentList)
            if (tgc instanceof AvatarBDDataType)
                v.add(((AvatarBDDataType) (tgc)).getDataTypeName());

        return v;
    }

    public void removeSynchronizedGates(List<TAttribute> v, TClassInterface t, TCDSynchroGateList tcdsgl) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TCDCompositionOperatorWithSynchro) {
                TCDCompositionOperatorWithSynchro tgso = (TCDCompositionOperatorWithSynchro) tgc;

                if (((tgso.getT1() == t) || (tgso.getT2() == t)) && tgso.getSynchroGateList() != tcdsgl) {
                    LinkedList<TTwoAttributes> ttwoattrib = tgso.getSynchroGateList().getGates();
                    for (TTwoAttributes tt : ttwoattrib)
                        if (tt.t1 == t)
                            v.remove(tt.ta1);
                        else
                            v.remove(tt.ta2);
                }
            }
    }

    public boolean isASynchronizedGate(TAttribute ta) {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TCDCompositionOperatorWithSynchro) {
                TCDCompositionOperatorWithSynchro tgso = (TCDCompositionOperatorWithSynchro) tgc;
                LinkedList<TTwoAttributes> ttwoattrib = tgso.getSynchroGateList().getGates();
                for (TTwoAttributes tt : ttwoattrib)
                    if ((tt.ta1 == ta) || (tt.ta2 == ta))
                        return true;
            }

        return false;
    }

    public boolean hasAlreadyAnInstance(TCDTObject to) {
        for (TGComponent o : this.componentList)
            if ((o instanceof TClassInterface) && (!o.equals(to))) {
                TClassInterface t = (TClassInterface) o;
                if (t.getClassName().compareTo(to.getClassName()) == 0)
                    return true;
            }

        return false;
    }

    // updates attributes and gates
    public void updateInstances(TCDTClass tc) {
        for (TGComponent o : this.componentList)
            if (o instanceof TCDTObject) {
                TCDTObject to = (TCDTObject) o;
                if (to.getMasterTClass() == tc) {
                    to.updateAttributes(tc.getAttributes());
                    to.updateGates(tc.getGates());
                }
            }
    }

    public void resetAllInstancesOf(TCDTClass tc) {
        for (TGComponent o : this.componentList)
            if (o instanceof TCDTObject) {
                TCDTObject to = (TCDTObject) o;
                if (to.getMasterTClass() == tc)
                    to.reset();
            }
    }

    public TCDTClass findTClassByName(String name) {
        for (TGComponent o : this.componentList)
            if (o instanceof TCDTClass) {
                TCDTClass tc = (TCDTClass) o;
                if (tc.getClassName().compareTo(name) == 0)
                    return tc;
            }

        return null;
    }

    public MainGUI getMGUI() {
        return mgui;
    }

    public BufferedImage performMinimalCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        boolean b = draw;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setFont(getGraphics().getFont());
//        if (lastGraphics != null) {
//            g.setFont(lastGraphics.getFont());
//        }
        draw = true;
        //paintMycomponents(g);
        overcomeShowing = true;
        paintMycomponents(g, false, 1, 1);
        //this.paint(g);
        overcomeShowing = false;
        //g.dispose();
        int x = getRealMinX();
        int y = getRealMinY();
        w = getRealMaxX() - x;
        h = getRealMaxY() - y;
        //TraceManager.addDev("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        x = x - 5;
        y = y - 5;
        w = w + 10;
        h = h + 10;
        w = Math.max(0, w);
        h = Math.max(0, h);
        x = Math.max(5, x);
        y = Math.max(5, y);
        w = Math.min(w, getWidth() - x);
        h = Math.min(h, getHeight() - y);
        //TraceManager.addDev("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        image = image.getSubimage(x, y, w, h);
        g.dispose();
        draw = b;
        return image;
    }

    public BufferedImage performSelectedCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Issue #14 point 10: Always use the current graphics
        g.setFont(getGraphics().getFont());
//        if (lastGraphics != null) {
//            g.setFont(lastGraphics.getFont());
//        }
        selectedTemp = false;
        Color colorTmp = ColorManager.SELECTED_0;
        ColorManager.SELECTED_0 = ColorManager.NORMAL_0;
        this.paint(g);
        selectedTemp = true;
        ColorManager.SELECTED_0 = colorTmp;
        g.dispose();
        image = image.getSubimage(xSel, ySel, widthSel, heightSel);
        return image;
    }

    private int getRealMinX() {
        int res = maxX;

        for (TGComponent tgc : this.componentList) {
            int cur = tgc.getCurrentMinX();
            //TraceManager.addDev("cur=" + cur + " res=" + res + " tgc=" + tgc.getName());
            if (cur < res)
                res = cur;
        }

        res = Math.max(0, res);

        if (res == maxX) {
            return 0;
        }

        return res;
    }

    private int getRealMinY() {
        int res = maxY;

        for (TGComponent tgc : this.componentList) {
            int cur = tgc.getCurrentMinY();
            if (cur < res)
                res = cur;
        }

        res = Math.max(0, res);

        if (res == maxY) {
            return 0;
        }

        return res;
    }

    public int getRealMaxX() {
        int res = limit;

        for (TGComponent tgc : this.componentList) {
            res = Math.max(res, tgc.getCurrentMaxX());
//            
//            if (cur > res) {
//                res = cur;
//            }
        }

        return res;
    }

    public int getRealMaxY() {
        int res = limit;

        for (TGComponent tgc : this.componentList) {
            res = Math.max(res, tgc.getCurrentMaxY());
//            int cur = tgc.getCurrentMaxY();
//            if (cur > res)
//                res = cur;
        }

        return res;
    }

    public boolean isSelectedTemp() {
        return selectedTemp;
    }

    public TGComponent getSecondTGComponent(TGConnector tgco) {
        TGConnectingPoint p = tgco.getTGConnectingPointP2();

        for (TGComponent tgc : this.componentList)
            if (tgc.belongsToMe(p))
                return tgc;

        return null;
    }

    public boolean isFree(ArtifactTClassGate atg) {
        return false;
    }

    // Toggle management
    public boolean areAttributesVisible() {
        return true;
    }

    public boolean areGatesVisible() {
        return true;
    }

    public boolean areSynchroVisible() {
        return true;
    }

    public void checkAllMySize() {
        for (TGComponent tgc : this.componentList)
            tgc.checkAllMySize();
    }

    public void enhance() {
    }

    public void autoAdjust() {
        for (TGComponent tgc : this.componentList)
            if (tgc instanceof TGAutoAdjust)
                ((TGAutoAdjust) tgc).autoAdjust(adjustMode);

        adjustMode = (adjustMode + 1) % 2;

        repaint();
    }

    public boolean hasAutoConnect() {
        return false;
    }

    public void autoConnect(TGComponent added) {


        boolean cond = hasAutoConnect();

        if (!cond) {
            return;
        }

        int i, j;

        //TraceManager.addDev("Autoconnect");

        Vector<Point> listPoint = new Vector<Point>();

        // Vector v = new Vector();

        int distance = 100;
        TGConnectingPoint found = null;
        int distanceTmp;

        boolean cd1, cd2;

        TGConnectingPoint tgcp, tgcp1;

        TGConnector tgco;

        for (i = 0; i < added.getNbConnectingPoint(); i++) {

            tgcp = added.getTGConnectingPointAtIndex(i);
            if (tgcp.isFree() && tgcp.isCompatibleWith(added.getDefaultConnector())) {

                // Try to connect that connecting point
                found = null;
                distance = 100;

                for (TGComponent tgc : this.componentList)
                    if (tgc != added) {
                        for (j = 0; j < tgc.getNbConnectingPoint(); j++) {
                            tgcp1 = tgc.getTGConnectingPointAtIndex(j);
                            if ((tgcp1 != null) && tgcp1.isFree()) {
                                if (tgcp1.isCompatibleWith(added.getDefaultConnector())) {
                                    cd1 = tgcp1.isIn() && tgcp.isOut() && (tgcp1.getY() > tgcp.getY());
                                    cd2 = tgcp.isIn() && tgcp1.isOut() && (tgcp1.getY() < tgcp.getY());
                                    if (cd1 || cd2) {
                                        distanceTmp = (int) (Math.sqrt(Math.pow(tgcp1.getX() - tgcp.getX(), 2) + Math.pow(tgcp1.getY() - tgcp.getY(), 2)));
                                        if (distanceTmp < distance) {
                                            distance = distanceTmp;
                                            found = tgcp1;
                                        }
                                    }
                                }
                            }
                        }

                    }
                if (found != null) {
                    //TraceManager.addDev("Adding connector");
                    if (found.isIn()) {
                        tgco = TGComponentManager.addConnector(tgcp.getX(), tgcp.getY(), added.getDefaultConnector(), this, tgcp, found, listPoint);
                    } else {
                        tgco = TGComponentManager.addConnector(found.getX(), found.getY(), added.getDefaultConnector(), this, found, tgcp, listPoint);
                    }
                    found.setFree(false);
                    tgcp.setFree(false);
                    this.componentList.add(tgco);
                    //TraceManager.addDev("Connector added");
                }
            }
        }
        //TraceManager.addDev("End Autoconnect");
    }

    public void resetAllDIPLOIDs() {
        for (TGComponent tgc : this.componentList)
            tgc.setDIPLOID(-1);
    }

    public void resetReachability() {
        for (TGComponent tgc : this.componentList)
            tgc.setHierarchyReachability(TGComponent.ACCESSIBILITY_UNKNOWN);
        ;
    }

    public void resetLiveness() {
        for (TGComponent tgc : this.componentList)
            tgc.setHierarchyLiveness(TGComponent.ACCESSIBILITY_UNKNOWN);
        ;
    }

    public void getListOfBreakPoints(List<Point> points, int taskID) {
        for (TGComponent tgc : this.componentList)
            if (tgc.getBreakpoint() && (tgc.getDIPLOID() != -1)) {
                boolean found = false;
                for (int i = 0; i < points.size(); i++)
                    if (points.get(i).y == tgc.getDIPLOID()) {
                        found = true;
                        break;
                    }

                if (!found) {
                    Point p = new Point(taskID, tgc.getDIPLOID());
                    points.add(p);
                }
            }
    }

    public String svgCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        int x = getRealMinX();
        int y = getRealMinY();
        w = getRealMaxX() - x;
        h = getRealMaxY() - y;
        //TraceManager.addDev("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        x = x - 5;
        y = y - 5;
        w = w + 10;
        h = h + 10;
        w = Math.max(0, w);
        h = Math.max(0, h);
        x = Math.max(5, x);
        y = Math.max(5, y);
        w = Math.min(w, getWidth() - x);
        h = Math.min(h, getHeight() - y);


		/*StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
        //sb.append(" width=\"" + (w+x) + "\" height=\"" + (h+y) + "\" viewbox=\"" + x + " " + y + " " + w + " " + h + "\">\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"");
        sb.append(" width=\"" + (w + x) + "\" height=\"" + (h + y) + "\" viewbox=\"" + x + " " + y + " " + w + " " + h + "\">\n");

        // Issue #14 point 10: Somehow the last graphics that was used is different than the actual one leading
        // to an error in calculating string lengths
        final SVGGraphics svgg = new SVGGraphics((Graphics2D) getGraphics());
//      SVGGraphics svgg = new SVGGraphics((Graphics2D)lastGraphics);

        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        //this.paint(svgg);
        //TraceManager.addDev("Painting for svg");
        basicPaintMyComponents(svgg);
        //TraceManager.addDev("Painting for svg done");
        sb.append(svgg.getSVGString());
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        sb.append("</svg>");

        return sb.toString();*/

        SVGGeneration gen = new SVGGeneration();
        return gen.getSVGString(this);
    }

    public String oldSvgCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        int x = getRealMinX();
        int y = getRealMinY();
        w = getRealMaxX() - x;
        h = getRealMaxY() - y;
        //TraceManager.addDev("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        x = x - 5;
        y = y - 5;
        w = w + 10;
        h = h + 10;
        w = Math.max(0, w);
        h = Math.max(0, h);
        x = Math.max(5, x);
        y = Math.max(5, y);
        w = Math.min(w, getWidth() - x);
        h = Math.min(h, getHeight() - y);


        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
        //sb.append(" width=\"" + (w+x) + "\" height=\"" + (h+y) + "\" viewbox=\"" + x + " " + y + " " + w + " " + h + "\">\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\"");
        sb.append(" width=\"" + (w + x) + "\" height=\"" + (h + y) + "\" viewbox=\"" + x + " " + y + " " + w + " " + h + "\">\n");

        // Issue #14 point 10: Somehow the last graphics that was used is different than the actual one leading
        // to an error in calculating string lengths
        final SVGGraphics svgg = new SVGGraphics((Graphics2D) getGraphics());
//      SVGGraphics svgg = new SVGGraphics((Graphics2D)lastGraphics);

        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        //this.paint(svgg);
        //TraceManager.addDev("Painting for svg");
        basicPaintMyComponents(svgg);
        //TraceManager.addDev("Painting for svg done");
        sb.append(svgg.getSVGString());
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);

        sb.append("</svg>");

        return sb.toString();
    }

    public void stopAddingConnector() {
        //TraceManager.addDev("Stop Adding connector in tdp");
        if (getTdmm() != null) {
            getTdmm().stopAddingConnector();
        }
    }

    public boolean changeStateMachineTabName(String oldValue, String newValue) {
        int stateMachineTab = -1;
        for (int i = 0; i < this.tp.tabbedPane.getTabCount(); i++) {
            if (this.tp.tabbedPane.getTitleAt(i).equals(newValue))
                return false;

            if (this.tp.tabbedPane.getTitleAt(i).equals(oldValue))
                stateMachineTab = i;
        }

        if (stateMachineTab < 0)
            return false;

        this.tp.tabbedPane.setTitleAt(stateMachineTab, newValue);
        this.tp.tabbedPane.setToolTipTextAt(stateMachineTab, "Opens the state machine of " + newValue);

        //change panel name
        for (int j = 0; j < this.tp.panels.size(); j++) {
            TDiagramPanel tdp = this.tp.panels.elementAt(j);
            if (tdp.getName().equals(oldValue))
                tdp.setName(newValue);
        }

        return true;
    }

    public void searchForText(String text, Vector<Object> elements) {
        TraceManager.addDev("Searching for " + text + " in " + this);

        for (TGComponent tgc : this.componentList)
            tgc.searchForText(text, elements);
    }

    public MainGUI getMainGUI() { //Ajout CD pour creation d'un panel depuis un block
        return mgui;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean b) {
        select = b;
    }

    protected void upComponent() {
        TGComponent tgc = componentPointed;
        if (tgc != null && tgc.moveable) {
            tgc.setMoveCd(tgc.x, tgc.y - MOVE_SPEED);
            repaint();
        }
    }

    protected void downComponent() {
        TGComponent tgc = componentPointed;
        if (tgc != null && tgc.moveable) {
            tgc.setMoveCd(tgc.x, tgc.y + MOVE_SPEED);
            repaint();
        }
    }

    protected void leftComponent() {
        TGComponent tgc = componentPointed;
        if (tgc != null && tgc.moveable) {
            tgc.setMoveCd(tgc.x - MOVE_SPEED, tgc.y);
            repaint();
        }
    }

    protected void rightComponent() {
        TGComponent tgc = componentPointed;
        if (tgc != null && tgc.moveable) {
            tgc.setMoveCd(tgc.x + MOVE_SPEED, tgc.y);
            repaint();
        }
    }

    protected void upComponents() {
        moveSelected(xSel, ySel - MOVE_SPEED);
        repaint();
    }

    protected void downComponents() {
        moveSelected(xSel, ySel + MOVE_SPEED);
        repaint();
    }

    protected void leftComponents() {
        moveSelected(xSel - MOVE_SPEED, ySel);
        repaint();
    }

    protected void rightComponents() {
        moveSelected(xSel + MOVE_SPEED, ySel);
        repaint();
    }

    public void setComponentPointed(TGComponent tgc) {
        componentPointed = tgc;
    }

    public TDiagramMouseManager getMouseManager() {
        return getTdmm();
    }

    /**
     * Check if newvalue is already a name of a component.
     *
     * @param newvalue : Checked name value
     * @return true if the name is used
     * @author Fabien Tessier
     */
    public boolean isCompositeNameUsed(String newvalue) {
        for (TGComponent tgc : this.componentList) {
            if (tgc.getValue().equals(newvalue))
                return true;
        }
        return false;
    }

	public TDiagramMouseManager getTdmm() {
		return tdmm;
	}

	public void setTdmm(TDiagramMouseManager tdmm) {
		this.tdmm = tdmm;
	}
}



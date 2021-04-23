/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

import avatartranslator.ElementWithUUID;
import myutil.Conversion;
import myutil.GenericTree;
import myutil.GraphicLib;
import myutil.TraceManager;
import tmltranslator.simulation.SimulationTransaction;

import org.w3c.dom.NodeList;

import ui.procsd.ProCSDComponent;
import ui.procsd.ProCSDPort;
import ui.tmlad.TMLActivityDiagramPanel;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;
import ui.tmldd.*;
import ui.util.IconManager;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Class TGComponent
 * High level TURTLE Graphical Component
 * Creation: 21/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/12/2003
 */

public abstract class TGComponent  extends AbstractCDElement implements /*CDElement,*/ GenericTree, ElementWithUUID {

    protected final static String XML_HEAD = "<COMPONENT type=\"";
    protected final static String XML_ID = "\" id=\"";
    protected final static String XML_UUID = "\" uid=\"";
    protected final static String XML_TAIL = "</COMPONENT>\n";

    protected final static String XML_SUB_HEAD = "<SUBCOMPONENT type=\"";
    protected final static String XML_SUB_TAIL = "</SUBCOMPONENT>\n";

    protected final static int RESIZE = 10;
    protected final static int RESIZE_SPACE = 8;
    protected final static int RESIZE_SPACE2 = 16;

    public static final int ACCESSIBILITY_UNKNOWN = 0;
    public static final int ACCESSIBILITY_OK = 1;
    public static final int ACCESSIBILITY_KO = 2;

    protected ImageIcon myImageIcon = IconManager.imgic8;


    private static int ID = 0;

    private boolean loaded = true;


    protected int x, y; // absolute cd
    protected int width, height;
    protected int minDesiredWidth = 0;
    protected int minDesiredHeight = 0;
    protected int minWidth = 0;
    protected int minHeight = 0;
    protected int maxWidth = 2000;
    protected int maxHeight = 2000;
    protected static int defMaxWidth = 2000;
    protected static int defMaxHeight = 2000;
    protected TGComponent father;
    private boolean moveWithFather = true;

    protected String customData = ""; // Used for e.g. plugins to save custom information

    protected TGComponent reference;

    private int id;

    // DIPLODOCUS ID
    private int DIPLOID = -1;
    //    private boolean DIPLO_running = false;
    private int DIPLO_met = 0;

    // AVATAR ID
    private int AVATARID = -1;
    //    private boolean AVATAR_running = false;
    private int AVATAR_met = 0;

    // TEPE ID
    private int TEPEID = -1;


    // Zone of drawing -> relative to father if applicable
    protected int minX;
    protected int minY;
    protected int maxX;
    protected int maxY;

    // Diagram on which it is drawned
    protected TDiagramPanel tdp;

    // Connecting points
    protected int nbConnectingPoint;
    protected TGConnectingPoint[] connectingPoint;

    // inner components
    protected int nbInternalTGComponent;
    protected TGComponent[] tgcomponent;
    protected TGComponent selectedInternalComponent;

    // characteristics
    //protected boolean enabled = true;
    protected boolean moveable;
    protected boolean removable;
    protected boolean multieditable = false;
    protected boolean editable;
    protected boolean canBeCloned;
    protected boolean drawingZoneRelativeToFather = true;
    protected boolean userResizable;
    protected boolean hidden;

    //Associated transactions
    public String transaction = "";
    public List<SimulationTransaction> transactions = new ArrayList<SimulationTransaction>();

    //If task
    public String runningStatus = "";

    protected String value; //applies if editable
    protected String name = "TGComponent";

    private UUID uniqueID;


    protected boolean repaint;

    protected int state;
    protected boolean selected;
    protected boolean isSelect = false;

    protected int distanceSelected = 5;

    // java code
    protected String preJavaCode = null;
    protected String postJavaCode = null;

    // internal comments
    protected String internalComment = null;


    protected boolean accessibility;
    protected boolean latencyCheck;
    protected int reachability = ACCESSIBILITY_UNKNOWN;
    protected int liveness = ACCESSIBILITY_UNKNOWN;

    // Invariants and mutual exclusion
    protected boolean invariant;
    protected int mutex;
    public final static int MUTEX_NOT_YET_STUDIED = 0;
    public final static int MUTEX_OK = 2;
    public final static int MUTEX_UNKNOWN = 1;


    protected boolean masterMutex;
    protected int mutexWith;
    protected boolean mutualExclusionWithMasterMutex;
    protected String nameOfMasterStateMutex;

    protected boolean breakpoint;

    // Colors
    protected Color currentMainColor;


    // Zoom
    // Issue #31: Moved to scalable component
    //double dx = 0, dy = 0, dwidth, dheight, dMaxWidth, dMaxHeight, dMinWidth, dMinHeight;


    //Constructor
    public TGComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        tgcomponent = new TGComponent[0];
        setCdRectangle(_minX, _maxX, _minY, _maxY);
        drawingZoneRelativeToFather = _pos;
        father = _father;
        tdp = _tdp;
        x = _x;
        y = _y;
        //TraceManager.addDev(name + " x=" + x + " y=" + y);
        //setCd(x, y);
        //TraceManager.addDev(name + " x=" + x + " y=" + y);
        setState(TGState.NORMAL);

        canBeCloned = true;

        id = ID;

        ID++;
        makeUUID();
        //TraceManager.addDev("creation Id:" + id);
    }

    public void makeUUID() {
        uniqueID = java.util.UUID.randomUUID();
    }

    // abstract operations

    protected abstract void internalDrawing(Graphics g);

    public abstract TGComponent isOnMe(int _x, int _y);

    public abstract void setState(int s);

//
//    public void setEnabled(boolean _enabled) {
//        enabled = _enabled;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }

    // Internal component operations
    public void setFather(TGComponent _father) {
        father = _father;
    }

    public void setDrawingZone(boolean _pos) {
        drawingZoneRelativeToFather = _pos;
    }

    // Java code operation
    public boolean hasPreJavaCode() {
        return (preJavaCode != null);
    }

    public boolean hasPostJavaCode() {
        return (postJavaCode != null);
    }

    public String getPreJavaCode() {
        return preJavaCode;
    }

    public String getPostJavaCode() {
        return postJavaCode;
    }

    public void setPostJavaCode(String code) {
        if (this instanceof PostJavaCode) {
            postJavaCode = code;
        }
    }

    public void setPreJavaCode(String code) {
        if (this instanceof PreJavaCode) {
            preJavaCode = code;
        }
    }

    public void setInternalComment(String comment) {
        if (this instanceof EmbeddedComment) {
            internalComment = comment;
        }
    }

    public void setHidden(boolean _hidden) {
        hidden = _hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean hasAnHiddenAncestor() {
        if (isHidden()) {
            return true;
        }
        if (getFather() != null) {
            return getFather().hasAnHiddenAncestor();
        }
        return false;
    }

    public String getStatusInformation() {

        if (this instanceof WithAttributes) {
            String tmp = ((WithAttributes) (this)).getAttributes();
            if (tmp != null)
                return tmp.replaceAll("\n", " / ");
        }

        return " ";
    }


    public String getInternalComment() {
        return internalComment;
    }

    public boolean hasInternalComment() {
        if (internalComment == null) {
            return false;
        }
        return this instanceof EmbeddedComment;

    }

    public int getMyDepth() {
        if (father == null) {
            return 0;
        } else {
            return 1 + father.getMyDepth();
        }
    }

    public void setCheckableAccessibility(boolean b) {
        accessibility = b;
    }

    public boolean getCheckableAccessibility() {
        return accessibility;
    }

    public boolean hasCheckedAccessibility() {
        if (accessibility == true) {
            return true;
        }

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                if (tgcomponent[i].hasCheckedAccessibility()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCheckableAccessibility() {
        if (this instanceof CheckableAccessibility) {
            return true;
        }

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                if (tgcomponent[i].hasCheckableAccessibility()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<TGComponent> getAllCheckedAccessibility() {
        List<TGComponent> list = new LinkedList<TGComponent>();
        getAllCheckedAccessibility(list);

        return list;
    }

    public void getAllCheckedAccessibility(List<TGComponent> _list) {
        if (accessibility) {
            _list.add(this);
        }

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgcomponent[i].getAllCheckedAccessibility(_list);
            }
        }
    }

    public List<TGComponent> getAllCheckableAccessibility() {
        List<TGComponent> list = new LinkedList<TGComponent>();
        getAllCheckableAccessibility(list);

        return list;
    }

    public void getAllCheckableAccessibility(List<TGComponent> _list) {
        TraceManager.addDev("Investigating accessibility of " + this);
        if (this instanceof CheckableAccessibility) {
            _list.add(this);
        }

        if (tgcomponent != null) {
            for (int i = 0; i < tgcomponent.length; i++) {

                tgcomponent[i].getAllCheckableAccessibility(_list);
            }
        }

    }

    public void setCheckableInvariant(boolean b) {
        invariant = b;
    }

    public boolean getCheckableInvariant() {
        return invariant;
    }

    public boolean hasCheckableInvariant() {
        if (invariant == true) {
            return true;
        }

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                if (tgcomponent[i].hasCheckableAccessibility()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setReachability(int reach) {
        reachability = reach;
    }

    public void setHierarchyReachability(int reach) {
        reachability = reach;
        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgcomponent[i].setHierarchyReachability(reach);
            }
        }
    }

    public void setLiveness(int live) {
        liveness = live;
    }

    public void setHierarchyLiveness(int live) {
        liveness = live;
        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgcomponent[i].setHierarchyLiveness(live);
            }
        }

    }

    public boolean getCheckLatency() {
        return latencyCheck;
    }

    public void setCheckLatency(boolean b) {
        latencyCheck = b;
    }

    public List<TGComponent> getAllCheckableInvariant() {
        List<TGComponent> list = new LinkedList<TGComponent>();
        getAllCheckableInvariant(list);
        
        return list;
    }

    public void getAllCheckableInvariant( List<TGComponent> _list) {
        if (invariant) {
            _list.add(this);
        }

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgcomponent[i].getAllCheckableInvariant(_list);
            }
        }
    }

    public void removeAllMutualExclusionWithMasterMutex() {
        mutualExclusionWithMasterMutex = false;

        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgcomponent[i].removeAllMutualExclusionWithMasterMutex();
            }
        }

    }

    public void setMasterMutex(boolean b) {
        masterMutex = b;
    }

    public boolean getMasterMutex() {
        return masterMutex;
    }

    public void setMutexWith(int value) {
        mutexWith = value;
    }

    public void setMutualExclusionWithMasterMutex(String name) {
        mutualExclusionWithMasterMutex = true;
        nameOfMasterStateMutex = name;
    }

    public TGComponent hasCheckableMasterMutex() {
        if (getMasterMutex()) {
            //TraceManager.addDev("Found element with master mutex: " + this);
            return this;
        }

        TGComponent tgc;
        if (nbInternalTGComponent > 0) {
            for (int i = 0; i < tgcomponent.length; i++) {
                tgc = tgcomponent[i].hasCheckableMasterMutex();
                if (tgc != null) {
                    return tgc;
                }
            }
        }

        return null;
    }


    public void setMutexResult(int value) {
        mutex = value;
    }

    public void setBreakpoint(boolean b) {
        breakpoint = b;
    }

    public boolean getBreakpoint() {
        return breakpoint;
    }

    public final TGComponent isOnMeHL(int _x, int _y) {
        if (hidden) {
            return null;
        }
        return isOnMe(_x, _y);
    }

    public String getValuePanel() {
        if (tdp == null) {
            return "Unknown";
        }

        return tdp.getMGUI().getMajorTitle(tdp);
    }


    public void drawJavaCode(Graphics g) {
        int s0 = 4, s1 = 9, s2 = 30, s3 = 10;

        g.fillOval(x + width - s0, y - s0, s1, s1);
        GraphicLib.dashedLine(g, x + width, y, x + width + s2, y);
        GraphicLib.dashedLine(g, x + width + s2, y, x + width + s2 + s3, y + s3);

        Point p1 = drawCode(g, getPreJavaCode(), x + width + s2 + s3, y + s3, true, true, 10);
        Point p2 = drawCode(g, getPostJavaCode(), x + width + s2 + s3, p1.y, false, true, 10);

        int w = Math.max(p1.x, p2.x);
        int h = Math.max(p1.y, p2.y) - y + s3;
        GraphicLib.dashedRect(g, x + width + s2 + s3, y + s3, w + 15, h - 12);

    }

    public void drawInternalComment(Graphics g) {
        int s0 = 4, s1 = 9, s2 = 30, s3 = 10;

        g.fillOval(x + width - s0, y - s0, s1, s1);
        GraphicLib.dashedLine(g, x + width, y, x + width + s2, y);
        GraphicLib.dashedLine(g, x + width + s2, y, x + width + s2 + s3, y + s3);

        Point p1 = drawCode(g, getInternalComment(), x + width + s2 + s3, y + s3, false, false, 0);

        int w = p1.x;
        int h = p1.y - y + s3;
        GraphicLib.dashedRect(g, x + width + s2 + s3, y + s3, w + 15, h - 12);
    }

    protected void drawAttributes(Graphics g, String attr) {
        if (attr == null || this.isHidden()) {
            return;
        }

        int s0 = 4, s1 = 9, s2 = 15, s3 = 7;

        ColorManager.setColor(g, state, 0);

        g.fillOval(x + width - s0, y - s0, s1, s1);
        GraphicLib.dashedLine(g, x + width, y, x + width + s2, y);
        GraphicLib.dashedLine(g, x + width + s2, y, x + width + s2 + s3, y + s3);

        Point p1 = drawCode(g, attr, x + width + s2 + s3, y + s3, true, false, 4);

        int w = p1.x;
        int h = p1.y - y + s3;
        GraphicLib.dashedRect(g, x + width + s2 + s3, y + s3, w + 15, h - 12);
    }

    protected Point drawCode(Graphics g, String s, int x1, int y1, boolean pre, boolean java, int dec) {
        Point p = new Point(0, y1);

        String info;

        if (s == null) {
            return p;
        }

        if (s.length() == 0) {
            return p;
        }

        // Font
        Font font = g.getFont();
        Font fontbis = font.deriveFont((float) 10);
        g.setFont(fontbis);

        String[] codes = Conversion.wrapText(s);

        if (java) {
            info = "";
            if (pre) {
                info = "Pre:";
            } else {
                info = "Post:";
            }
            p.y = p.y + 12;
            g.drawString(info, x1 + 3, p.y);
            p.x = Math.max(p.x, g.getFontMetrics().stringWidth(info));
        }


        for (int i = 0; i < codes.length; i++) {
            p.y = p.y + 12;
            g.drawString(codes[i], x1 + dec + 3, p.y);
            p.x = Math.max(p.x, g.getFontMetrics().stringWidth(codes[i]));
        }
        /*if (codes.length > 0) {
          p.y = p.y + 12;
          }*/

        g.setFont(font);
        return p;
    }


    // drawing operations

    public boolean mustBeRepainted() {
        return (repaint == true) || internalComponentMustBeRepainted();
    }

    public boolean internalComponentMustBeRepainted() {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i].mustBeRepainted()) {
                return true;
            }
        }
        return false;
    }

    public void setInternalLoaded(boolean b) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].setLoaded(b);
            tgcomponent[i].setInternalLoaded(b);
        }
    }

    public void drawDiploID(Graphics g) {
        if (getDIPLOID() != -1) {
            g.setColor(ColorManager.DIPLOID);
            g.drawString("" + getDIPLOID(), x + width, y + height + 5);
        }
    }

    public void drawTEPEID(Graphics g) {
        if (getTEPEID() != -1) {
            g.setColor(ColorManager.TEPEID);
            g.drawString("" + getTEPEID(), x + width, y + height + 5);
        }
    }

    public void drawAVATARID(Graphics g) {
        if (getAVATARID() != -1) {
            g.setColor(ColorManager.AVATARID);
            g.drawString("" + getAVATARID(), x + width, y + height + 5);
        }
    }

    public void setDIPLOMet(int _nb) {
        DIPLO_met = _nb;
    }

    public void drawDIPLOMet(Graphics g) {
        int dech = 8;
        int decw = 12;
        g.setColor(ColorManager.CURRENT_COMMAND_RUNNING);
        int myx, myy, mywidth;
        if (this instanceof TGConnector) {
            TGConnector tgco = (TGConnector) this;
            myx = tgco.getMiddleFirstSegment().x;
            myy = tgco.getMiddleFirstSegment().y;
            mywidth = 2;
        } else {
            myx = getX();
            myy = getY();
            mywidth = width + 1;
        }

        g.drawLine(myx + mywidth, myy + 1 + dech / 2, myx + mywidth + decw / 3, myy + dech);
        g.drawLine(myx + mywidth + decw / 3, myy + dech, myx + mywidth + decw, myy);

        if (DIPLO_met > 1) {
            g.drawString("" + DIPLO_met, myx + mywidth + decw + 1, myy);
        }

    }

    public void setAVATARMet(int _metNb) {
        //TraceManager.addDev("----- MET MET MET:" + this + ": " + _metNb);
        AVATAR_met = _metNb;
    }

    public int getAVATARMet() {
        return AVATAR_met;
    }

    public void setInternalAvatarMet(int _metNb) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].setAVATARMet(_metNb);
            tgcomponent[i].setInternalAvatarMet(_metNb);
        }
    }

    public void drawAVATARMet(Graphics g) {
        int dech = 8;
        int decw = 12;
        g.setColor(ColorManager.CURRENT_COMMAND_RUNNING);
        int myx, myy, mywidth;
        if (this instanceof TGConnector) {
            TGConnector tgco = (TGConnector) this;
            myx = tgco.getMiddleFirstSegment().x;
            myy = tgco.getMiddleFirstSegment().y;
            mywidth = 2;
        } else {
            myx = getX();
            myy = getY();
            mywidth = width + 1;
        }

        g.drawLine(myx + mywidth, myy + 1 + dech / 2, myx + mywidth + decw / 3, myy + dech);
        g.drawLine(myx + mywidth + decw / 3, myy + dech, myx + mywidth + decw, myy);

        g.drawString("" + AVATAR_met, myx + mywidth + decw, myy);

    }

    // _mode: 1 : running
    //       2 : selected for execution
    public void drawAVATARComp(Graphics g, int _mode) {
        //TraceManager.addDev("drawing avatar comp=" + this);
        int wb = 30;
        int hb = 10;
        int wh = 15;
        int hh = 20;
        int sep = 10;

        int[] xp = new int[7];
        int[] yp = new int[7];

        int myx, myy, myheight;
        if (this instanceof TGConnector) {
            TGConnector tgco = (TGConnector) this;
            myx = tgco.getMiddleFirstSegment().x;
            myy = tgco.getMiddleFirstSegment().y;
            myheight = 4;
        } else {
            myx = getX();
            myy = getY();
            myheight = height;
        }

        if (_mode == 1) {
            g.setColor(ColorManager.CURRENT_COMMAND_RUNNING);
        } else {
            g.setColor(ColorManager.CURRENT_COMMAND_SUSPENDED);
        }

        xp[0] = myx - sep - wb - wh;
        yp[0] = myy + ((myheight - hb) / 2);

        xp[1] = myx - sep - wh;
        yp[1] = myy + ((myheight - hb) / 2);

        xp[2] = myx - sep - wh;
        yp[2] = myy + ((myheight - hh) / 2);

        xp[3] = myx - sep;
        yp[3] = myy + (myheight / 2);

        xp[4] = myx - sep - wh;
        yp[4] = myy + ((myheight + hh) / 2);

        xp[5] = myx - sep - wh;
        yp[5] = myy + ((myheight + hb) / 2);

        xp[6] = myx - sep - wb - wh;
        yp[6] = myy + ((myheight + hb) / 2);


        g.fillPolygon(xp, yp, 7);
    }


    public void drawRunningDiploID(Graphics g, RunningInfo ri) {
        //TraceManager.addDev("Drawing running DIPLO");
        int wb = 40;
        int hb = 10;
        int wh = 15;
        int hh = 20;
        int sep = 10;

        int[] xp = new int[7];
        int[] yp = new int[7];

        xp[0] = x - sep - wb - wh;
        yp[0] = y + ((height - hb) / 2);

        xp[1] = x - sep - wh;
        yp[1] = y + ((height - hb) / 2);

        xp[2] = x - sep - wh;
        yp[2] = y + ((height - hh) / 2);

        xp[3] = x - sep;
        yp[3] = y + (height / 2);

        xp[4] = x - sep - wh;
        yp[4] = y + ((height + hh) / 2);

        xp[5] = x - sep - wh;
        yp[5] = y + ((height + hb) / 2);

        xp[6] = x - sep - wb - wh;
        yp[6] = y + ((height + hb) / 2);


        if (ri.state.compareTo("running") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_RUNNING);
        } else if (ri.state.compareTo("runnable") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_RUNNABLE);
        } else if (ri.state.compareTo("suspended") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_SUSPENDED);
        } else if (ri.state.compareTo("terminated") == 0) {
            g.setColor(ColorManager.CURRENT_COMMAND_TERMINATED);
        } else {
            g.setColor(ColorManager.CURRENT_COMMAND_UNKNOWN);
        }

        g.fillPolygon(xp, yp, 7);

        String s, s1, s2;

        if (TDiagramPanel.DIPLO_TRANSACTION_PROGRESSION_ON) {
            if (ri.progression != null) {
                s = ri.progression + "%";
            } else {
                s = "";
            }
            int ww = g.getFontMetrics().stringWidth(s);

            if (ri.startTime != null) {
                g.drawString("start time:", x - sep - wb - wh, y + ((height - hb) / 2) - 11);
                g.drawString(ri.startTime, x - sep - wb - wh, y + ((height - hb) / 2) - 1);
            }
            g.drawString(s, x - sep - wb - wh - ww, y + 4 + ((height) / 2));
            if (ri.finishTime != null) {
                s = ri.finishTime;
                if (s.compareTo("-1") == 0) {
                    s = "?";
                }
                g.drawString("end time:", x - sep - wb - wh, y + ((height + hb) / 2) + 10);
                g.drawString(s, x - sep - wb - wh, y + ((height + hb) / 2) + 20);
            }

            // Transaction
            if (ri.transStartTime != null) {
                g.setColor(ColorManager.DIPLOID);
                s1 = ri.transStartTime;
                if (s1.compareTo("-1") == 0) {
                    s1 = "?/?";
                } else {
                    s2 = ri.transFinishTime;
                    if (s2 == null) {
                        s1 = s1 + "/?";
                    } else {
                        if (s1.compareTo("-1") == 0) {
                            s1 = s1 + "/?";
                        } else {
                            s1 = s1 + "/" + s2;
                        }
                    }
                }
                //g.drawString("Transactions " +s1, x - sep - wb -wh + 2, y + 4 + ((height) / 2));

            }
        }

    }

    public void drawLoadDiploID(Graphics g, LoadInfo li) {
        //TraceManager.addDev("Drawing diplo ID id=" + li.id);
        int sizeRect = 6;
        int sizeOval = 8;
        //   Color c = g.getColor();
        Color myCol = new Color(135, Math.min(255, Math.max(0, (int) (255 - (li.load * 255)))), 0);
        g.setColor(myCol);
        g.fillRect(x, y, width, sizeRect);
        g.fillRect(x, y, sizeRect, height);
        g.fillRect(x + width - sizeRect, y, sizeRect, height);
        g.fillRect(x, y + height - sizeRect, width, sizeRect);

        g.fillOval(x + width, y + ((sizeRect - sizeOval) / 2), sizeOval, sizeOval);
        g.drawLine(x + width + sizeOval, y + (sizeRect / 2), x + width + sizeOval + (sizeOval), y + (sizeRect / 2));
        g.drawString("" + (int) (li.load * 100) + "%", x + width + sizeOval + (sizeOval) + 1, y + (sizeOval / 2));

        if (li.energy != -1) {
            //g.fillOval(x+width, y+((sizeRect-sizeOval)/2)+20, sizeOval, sizeOval);
            //g.drawLine(x+width+sizeOval, y+(sizeRect/2)+20, x+width+sizeOval+(sizeOval), y+(sizeRect/2)+20);
            //g.drawString("" + (int)(li.energy) + " pW", x+width+sizeOval+(sizeOval) + 1, y+(sizeOval/2) + 20);
        }
        //Draw transactions too???
    }

    public void drawTransaction(Graphics g) {
        Color c = g.getColor();
        Color textColor = Color.BLACK;
        g.setColor(textColor);
        //g.drawString(transaction, x, y+height+10);
        g.setColor(c);
    }

    public void drawStatus(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.BLACK);
        //g.drawString(runningStatus, x+width, y);
        internalDrawing(g);
        g.setColor(c);
    }

    public void drawAccessibility(int type, Graphics g, int _x, int _y, String value) {
        Color c;
        Color oldC = g.getColor();
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        switch (type) {
            case ACCESSIBILITY_OK:
                c = ColorManager.ACCESSIBILITY_OK;
                break;
            case ACCESSIBILITY_KO:
                c = ColorManager.ACCESSIBILITY_KO;
                break;
            default:
                c = ColorManager.ACCESSIBILITY_UNKNOWN;
        }

        g.drawString(value, _x - 1, _y - 1);
        g.setColor(c);
        g.drawString(value, _x, _y);
        g.setFont(f);
        g.setColor(oldC);

    }

    public void drawLatencyCheck(Graphics g, int _x, int _y) {
        //      Color c= g.getColor();
        g.setColor(Color.BLUE);
        int[] xs = new int[]{_x - 12, _x - 4, _x - 12};
        int[] ys = new int[]{_y, _y + 5, _y + 10};
        g.fillPolygon(xs, ys, 3);
        g.setColor(Color.BLACK);
        g.drawLine(_x - 12, _y, _x - 12, _y + 20);
        g.drawLine(_x - 12, _y, _x - 4, _y + 5);
        g.drawLine(_x - 12, _y + 10, _x - 4, _y + 5);

    }

    /**
     * Issue #31
     * @return
     */
    protected int getReachabilityMargin() {
    	return 18;
    }

    /**
     * Issue #31
     * @return
     */
    protected int getLivenessMargin() {
    	return 10;
    }

    /**
     * Issue #31
     * @return
     */
    protected int getUnknownMargin() {
    	return 2;
    }

    /**
     * Issue #31
     * @return
     */
    protected int getExclusionMargin() {
    	return 12;
    }

    public void draw(Graphics g) {
        RunningInfo ri;
        LoadInfo li;
        
        // Issue #69: Disabling of components
        ColorManager.setColor(g, state, 0, isEnabled() );
//        ColorManager.setColor(g, state, 0);
  
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, this.tdp.getFontSize());
        g.setFont(font);
        internalDrawing(g);
        g.setFont(font);
        repaint = false;
        drawInternalComponents(g);
        GraphicLib.setNormalStroke(g);

        if ((userResizable) && (state == TGState.POINTER_ON_ME)) {
            drawResizeBorders(g);
        }

        if ((state == TGState.POINTER_ON_ME) && (getDefaultConnector() != -1)) {
            //TraceManager.addDev("pointed");
            drawOutFreeTGConnectingPointsCompatibleWith(g, getDefaultConnector());
        }

        if ((accessibility) || (reachability != ACCESSIBILITY_UNKNOWN) || (liveness != ACCESSIBILITY_UNKNOWN)) {
            drawAccessibility(reachability, g, x + width - getReachabilityMargin() /* Issue # 31 18*/, y - 1, "R");
            drawAccessibility(liveness, g, x + width - getLivenessMargin() /* Issue #31 10*/, y - 1, "L");

            if ((reachability == ACCESSIBILITY_UNKNOWN) && (liveness == ACCESSIBILITY_UNKNOWN)) {
                drawAccessibility(liveness, g, x + width - getUnknownMargin() /* Issue # 31 2 */, y - 1 /* Issue # 31 2*/, "?");
            }

            // Old way to do ..
            /*g.setColor(ColorManager.ACCESSIBILITY);
              GraphicLib.setMediumStroke(g);
              g.drawLine(x+width-2, y+2, x+width-6, y+6);
              g.drawLine(x+width-6, y+2, x+width-2, y+6);
              GraphicLib.setNormalStroke(g);*/
        }

        if (invariant) {
            g.setColor(ColorManager.ACCESSIBILITY);
            //GraphicLib.setMediumStroke(g);
            // Issue #31
            final int exclusionMargin = getExclusionMargin();
            
            if (mutex == MUTEX_NOT_YET_STUDIED) {
                g.drawString("mutual exclusion?", x + width + 1, y - exclusionMargin /* Issue #31 12*/);
            } else if (mutex == MUTEX_UNKNOWN) {
                g.drawString("mutual exclusion: cannot be proved", x + width + 1, y - exclusionMargin);
            } else if (mutex == MUTEX_OK) {
                g.setColor(ColorManager.MUTEX_OK);
                g.drawString("mutual exclusion: OK", x + width + 1, y - exclusionMargin);
            }

            /*g.drawLine(x+width-2, y+2, x+width-6, y+6);
              g.drawLine(x+width-6, y+2, x+width-2, y+6);
              GraphicLib.setNormalStroke(g);*/
        }

        if (masterMutex) {
            g.setColor(ColorManager.ACCESSIBILITY);
            if (mutexWith == MUTEX_NOT_YET_STUDIED) {
                g.drawString("mutual exclusion with others?", x + width + 1, y);
            } else if (mutexWith == MUTEX_UNKNOWN) {
                g.drawString("No mutual exclusion with others found", x + width + 1, y);
            } else if (mutexWith == MUTEX_OK) {
                g.setColor(ColorManager.MUTEX_OK);
                g.drawString("Mutual exclusion with others found", x + width + 1, y);
            }
        }

        if (mutualExclusionWithMasterMutex) {
            g.setColor(ColorManager.MUTEX_OK);
            g.drawString("Mutual exclusion with " + nameOfMasterStateMutex, x + width + 1, y + 12);
        }

        if (breakpoint) {
            //TraceManager.addDev("breakpoint");
            g.setColor(ColorManager.BREAKPOINT);
            Font f = g.getFont();
            g.setFont(f.deriveFont(Font.BOLD));
            g.drawString("bk", x + width, y + 3);
            g.setFont(f);
        }


        if (TDiagramPanel.DIPLO_ANIMATE_ON) {
            if (!((this instanceof TGConnector) || (this instanceof TGCNote) || (this instanceof TMLArchiFirewallNode))) {
                if (tdp instanceof TMLActivityDiagramPanel) {
                    if (getFather() == null) {
                        if (TDiagramPanel.DIPLO_ID_ON) {
                            drawDiploID(g);
                        }

                        if (DIPLO_met > 0) {
                            drawDIPLOMet(g);
                        }

                        ri = tdp.getMGUI().isRunningID(getDIPLOID());
                        if (ri != null) {
                            drawRunningDiploID(g, ri);
                        }
                    }
                } else if (tdp instanceof TMLComponentTaskDiagramPanel) {
                    if (this instanceof TMLCPrimitiveComponent) {
                        if (TDiagramPanel.DIPLO_ID_ON) {
                            drawDiploID(g);
                        }
                    }
                } else if (tdp instanceof TMLTaskDiagramPanel) {
                    if (getDIPLOID() != -1) {
                        if (TDiagramPanel.DIPLO_ID_ON) {
                            drawDiploID(g);
                        }
                    }
                    /*if (this instanceof TMLTaskOperator) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLChannelOperator) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLEventOperator) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLRequestOperator) {
                      drawDiploID(g);
                      }*/
                } else if (tdp instanceof TMLArchiDiagramPanel) {
                    if (getDIPLOID() != -1) {
                        if (TDiagramPanel.DIPLO_ID_ON) {
                            drawDiploID(g);
                        }
                        li = tdp.getMGUI().isLoadID(getDIPLOID());
                        if (li != null) {
                            drawLoadDiploID(g, li);
                        }
                        List<SimulationTransaction> ts = tdp.getMGUI().getTransactions(getDIPLOID());
                        if (ts == null) {
                            //TraceManager.addDev("Null transactions for " + getDIPLOID());
                        } else {
                            //TraceManager.addDev("Number of transactions for id " + getDIPLOID() + " = " + ts.size());
                        }
                        if (ts != null && ts.size() > 0) {
                            transactions = new ArrayList<SimulationTransaction>(ts);
                            transaction = transactions.get(transactions.size() - 1).taskName + ":" + transactions.get(transactions.size() - 1).command;
                            drawTransaction(g);
                            for (int i = 0; i < nbInternalTGComponent; i++) {
                                Object ob = getChild(i);
                                if (ob instanceof TMLArchiArtifact) {
                                    TMLArchiArtifact art = (TMLArchiArtifact) ob;
                                    for (SimulationTransaction st : transactions) {
                                        if (art.getValue().replaceAll(":", "_").equals(st.taskName)) {
                                            art.transactions.add(st);
                                            art.transaction = st.taskName + ":" + st.command;
                                        }
                                    }
                                    art.drawTransaction(g);
                                }
                            }
                        } else {
                            //reset transactions list if remove all trans or reset the simulation
                            transactions = new ArrayList<SimulationTransaction>();
                        }
                        for (int i = 0; i < nbInternalTGComponent; i++) {
                            Object ob = getChild(i);
                            if (ob instanceof TMLArchiArtifact) {
                                TMLArchiArtifact art = (TMLArchiArtifact) ob;
                                String stat = tdp.getMGUI().getStatus(art.getValue().replaceAll(":", "_"));
                                if (stat != null) {
                                    art.runningStatus = stat;
                                    art.drawStatus(g);
                                    tdp.repaint();
                                }
                            }
                        }/*
                         //This code is horrible and I should fix it
                         ConcurrentHashMap<String, String> statMap = tdp.getMGUI().getStatus(getDIPLOID());
                         for (String name:statMap.keySet()){
                         String stat =statMap.get(name);
                         for (int i=0; i< nbInternalTGComponent; i++){
                         Object ob = getChild(i);
                         if (ob instanceof TMLArchiArtifact){
                         TMLArchiArtifact art = (TMLArchiArtifact) ob;
                         if (art.getValue().replaceAll(":", "_").equals(name)){
                         art.runningStatus=stat;
                         art.drawStatus(g);
                         tdp.repaint();
                         }
                         }
                         }

                         }*/

                    }
                    /*if (this instanceof TMLArchiCPUNode) {

                      }
                      if (this instanceof TMLArchiBUSNode) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLArchiBridgeNode) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLArchiHWANode) {
                      drawDiploID(g);
                      }
                      if (this instanceof TMLArchiMemoryNode) {
                      drawDiploID(g);
                      }*/
                }
            }
        } else if (TDiagramPanel.DIPLO_ID_ON) {
            drawDiploID(g);
        } else if (TDiagramPanel.AVATAR_ID_ON) {
        	drawAVATARID(g);
        } else if (TDiagramPanel.TEPE_ID_ON) {
        		drawTEPEID(g);
        } else {
            runningStatus = "";
            transactions.clear();

        }

        if (TDiagramPanel.AVATAR_ANIMATE_ON) {
            //TraceManager.addDev("Avatar animate?");
            if (breakpoint) {
                //TraceManager.addDev("breakpoint");
                g.setColor(ColorManager.BREAKPOINT);
                Font f = g.getFont();
                g.setFont(f.deriveFont(Font.BOLD));
                g.drawString("bk", x + width, y + 3);
                g.setFont(f);
            }
            if (AVATAR_met > 0) {
                drawAVATARMet(g);
            }
            int ret = tdp.getMGUI().isRunningAvatarComponent(this);
            if (ret > 0) {
                //TraceManager.addDev("Avatar animate!");
                drawAVATARComp(g, ret);
            }

        }

        if (this instanceof EmbeddedComment) {
            if ((internalComment != null) && (internalComment.length() > 0)) {
                if (tdp.getInternalCommentVisible() == 2) {
                    drawInternalComment(g);
                } else {
                    if ((state == TGState.POINTER_ON_ME) && (tdp.getInternalCommentVisible() == 1)) {
                        drawInternalComment(g);
                    }
                }
            }
        }

        if (this instanceof PartOfInvariant) {
            drawInvariantInformation(g);

        }
        if (latencyCheck) {
            drawLatencyCheck(g, x, y);
        }
    }

    public void drawInvariantInformation(Graphics g) {
        Invariant inv = tdp.getMGUI().getCurrentInvariant();
        if (inv != null) {
            if (inv.containsComponent(this)) {
                g.setColor(ColorManager.ACCESSIBILITY);
                if (this instanceof PartOfHighInvariant) {
                    g.drawString("inv", x + width + 2, y + (height / 2));
                } else {
                    g.drawString("inv", x + width + 2, y + height);
                }
            }
        }
    }

    public void drawWithAttributes(Graphics g) {
        if (this instanceof WithAttributes) {
            /*TraceManager.addDev( "tdp is of " + tdp.getClass() );
              TraceManager.addDev( "this is of " + this.getClass() );*/
            if (tdp.getAttributeState() == 2) {
                drawAttributes(g, ((WithAttributes) this).getAttributes());
            } else {
                if ((state == TGState.POINTER_ON_ME) && (tdp.getAttributeState() == 1)) {
                    drawAttributes(g, ((WithAttributes) this).getAttributes());
                }
            }
        }
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].drawWithAttributes(g);
        }
    }

    public boolean hasAnUpdateOnPointedComponent() {
        return false;
    }

    public int getDefaultConnector() {
        return -1;
    }

    public void drawResizeBorders(Graphics g) {
        ColorManager.setColor(g, TGState.RESIZE_INDICATIONS, 0);
        GraphicLib.setHighStroke(g);

        //TraceManager.addDev("Min = " + minWidth + " max = " + maxWidth);
        boolean b1 = (minWidth != maxWidth);
        boolean b2 = (minHeight != maxHeight);

        //TraceManager.addDev("b1 = " + b1 + " b2 = " + b2);

        if (b1 && b2) {
            // upper left
            g.drawLine(x, y, x + RESIZE, y);
            g.drawLine(x, y, x, y + RESIZE);
        }

        if (b2) {
            // up & middle
            g.drawLine(x + (width - RESIZE) / 2, y, x + (width + RESIZE) / 2, y);
        }

        if (b1 && b2) {
            // upper right
            g.drawLine(x + width, y, x - RESIZE + width, y);
            g.drawLine(x + width, y, x + width, y + RESIZE);
        }

        if (b1) {
            // left & middle
            g.drawLine(x, y + (height - RESIZE) / 2, x, y + (height + RESIZE) / 2);
        }

        if (b1 && b2) {
            // down & left
            g.drawLine(x, y + height, x + RESIZE, y + height);
            g.drawLine(x, y + height, x, y + height - RESIZE);
        }

        if (b2) {
            // down & middle
            g.drawLine(x + (width - RESIZE) / 2, y + height, x + (width + RESIZE) / 2, y + height);
        }

        // down & right
        if (b1 && b2) {
            g.drawLine(x + width, y + height, x - RESIZE + width, y + height);
            g.drawLine(x + width, y + height, x + width, y + height - RESIZE);
        }

        // right & middle
        if (b1) {
            g.drawLine(x + width, y + (height - RESIZE) / 2, x + width, y + (height + RESIZE) / 2);
        }

        GraphicLib.setNormalStroke(g);
    }

    // 1 2 3
    // 4   5
    // 6 7 8
    public int getResizeZone(int x1, int y1) {
        // upperleft
        boolean b1 = (minWidth != maxWidth);
        boolean b2 = (minHeight != maxHeight);

        if (b1 && b2) {
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE, y - RESIZE_SPACE, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 1;
            }
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE, y - RESIZE_SPACE, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 1;
            }
        }

        if (b2) {
            // up & middle
            if (GraphicLib.isInRectangle(x1, y1, x + (width - RESIZE) / 2 - RESIZE_SPACE, y - RESIZE_SPACE, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 2;
            }
        }

        // upper right
        if (b1 && b2) {
            if (GraphicLib.isInRectangle(x1, y1, x + width - RESIZE - RESIZE_SPACE, y - RESIZE_SPACE, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 3;
            }
            if (GraphicLib.isInRectangle(x1, y1, x + width - RESIZE_SPACE, y - RESIZE_SPACE, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 3;
            }
        }

        // left & middle
        if (b1) {
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE, y + (height - RESIZE) / 2 - RESIZE_SPACE, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 4;
            }
        }

        if (b1) {
            // right & middle
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE + width, y + (height - RESIZE) / 2 - RESIZE_SPACE, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 5;
            }
        }

        // down & left
        if (b1 && b2) {
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE, y - RESIZE_SPACE + height, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 6;
            }
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE, y - RESIZE_SPACE + height, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 6;
            }
        }

        // down & middle
        if (b2) {
            if (GraphicLib.isInRectangle(x1, y1, x + (width - RESIZE) / 2 - RESIZE_SPACE, y - RESIZE_SPACE + height, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 7;
            }
        }

        // down & right
        if (b1 && b2) {
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE + width, y - RESIZE_SPACE + height, RESIZE + RESIZE_SPACE2, RESIZE_SPACE2)) {
                return 8;
            }
            if (GraphicLib.isInRectangle(x1, y1, x - RESIZE_SPACE + width, y - RESIZE_SPACE + height, RESIZE_SPACE2, RESIZE + RESIZE_SPACE2)) {
                return 8;
            }
        }

        return 0;
    }

    // operations on internal components

    public boolean areAllInRectangle(int x1, int y1, int width, int height) {
        // TGComponent tgc;

        if (!isInRectangle(x1, y1, width, height)) {
            return false;
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (!tgcomponent[i].isInRectangle(x1, y1, width, height)) {
                return false;
            }
        }

        return true;
    }

    public int getMyCurrentMinX() {
        return x;
    }

    public int getCurrentMinX() {
        int current = getMyCurrentMinX();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            //if (tgcomponent[i].moveWithFather()) {
            current = Math.min(current, tgcomponent[i].getCurrentMinX());
            //}
        }

        return current;
    }

    public int getMyCurrentMaxX() {
        return x + width;
    }

    public int getCurrentMaxX() {
        int current = getMyCurrentMaxX();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            //TraceManager.addDev("Current=" + current + "name = " + tgcomponent[i].getName());
            //if (tgcomponent[i].moveWithFather()) {
            current = Math.max(current, tgcomponent[i].getCurrentMaxX());
            //}
        }
        return current;
    }

    public int getMyCurrentMinY() {
        return y;
    }

    public int getCurrentMinY() {
        int current = getMyCurrentMinY();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            //if (tgcomponent[i].moveWithFather()) {
            current = Math.min(current, tgcomponent[i].getCurrentMinY());
            //}
        }

        return current;
    }

    public int getMyCurrentMaxY() {
        return y + height;
    }

    public int getCurrentMaxY() {
        int current = getMyCurrentMaxY();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            //if (tgcomponent[i].moveWithFather()) {
            current = Math.max(current, tgcomponent[i].getCurrentMaxY());
            //}
        }

        return current;
    }

    public boolean isInRectangle(int x1, int y1, int width, int height) {
        return !((getX() < x1) || (getY() < y1) || ((getX() + this.width) > (x1 + width)) || ((getY() + this.height) > (y1 + height)));
    }

    //Isssue #73: Hidden component still clickable
    public final void drawInternalComponents(Graphics g) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            //ColorManager.setColor(g, tgcomponent[i].getState(), 0);
            if (!tgcomponent[i].isHidden()) {
                tgcomponent[i].draw(g);
                tgcomponent[i].setHidden(false);
            } else {
            	tgcomponent[i].setHidden(true);
            }
        }
    }


    public int getNbInternalTGComponent() {
        return nbInternalTGComponent;
    }

    public int getNbInternalPoints() {
        int i;
        int cpt = 0;
        for (i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TGCPointOfConnector) {
                cpt++;
            }
        }
        return cpt;
    }

    public void getTGCPointOfConnectors(TGCPointOfConnector[] _points) {
        int index = 0;
        int i;
        for (i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TGCPointOfConnector) {
                _points[index] = ((TGCPointOfConnector) tgcomponent[i]);
                index++;
            }
        }
    }


    public int makeLovelyIds(int firstId) {
        int i;
        for (i = 0; i < nbInternalTGComponent; i++) {
            firstId = tgcomponent[i].makeLovelyIds(firstId);
        }

        if (connectingPoint != null) {
            for (i = 0; i < Math.min(connectingPoint.length, nbConnectingPoint); i++) {
                if (connectingPoint[i] != null) {
                    connectingPoint[i].forceId(firstId);
                }
                firstId++;
            }
        }
        id = firstId;
        return (firstId + 1);
    }

    public TGComponent isOnAnInternalTGComponent(int _x, int _y) {
        TGComponent tgc;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgc = tgcomponent[i].isOnMeHL(_x, _y);
            if (tgc != null) {
                return tgc;
            }
            if (tgcomponent[i].isClickSelected()) {
                return tgcomponent[i];
            }
        }
        return null;
    }

    public void setStateInternalTGComponent(int s) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].setState(s);
        }
    }

    public void setSelectedInternalTGComponent(TGComponent tgc) {
        selectedInternalComponent = tgc;
    }

    public int getMyNum(TGComponent tgc) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                return i;
            }
        }
        return -1;
    }

    public TGComponent getInternalTGComponent(int index) {
        if (index >= nbInternalTGComponent) {
            return null;
        }

        return tgcomponent[index];
    }

    public List<TGComponent> getRecursiveAllInternalComponent() {
        List<TGComponent> ll = new LinkedList<TGComponent>();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            ll.add(tgcomponent[i]);
            ll.addAll(tgcomponent[i].getRecursiveAllInternalComponent());
        }
        return ll;
    }


    public TGComponent getIfId(int checkId) {
        if (id == checkId) {
            return this;
        }
        TGComponent tgc;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgc = tgcomponent[i].getIfId(checkId);
            if (tgc != null) {
                return tgc;
            }
        }
        return null;
    }

    public boolean isUserResizable() {
        return userResizable;
    }

    public void updateMinMaxSize() {

    }

    public Point modifyInY(int newy) {
        Point p = new Point();
        newy = Math.min(newy, y + height);
        newy = Math.max(newy, tdp.getMinY());
        newy = Math.min(newy, y + (height - minHeight));
        newy = Math.max(newy, y + (height - maxHeight));
        if ((father != null) && (drawingZoneRelativeToFather)) {
            newy = Math.min(maxY + father.getY(), Math.max(minY + father.getY(), newy));
        }
        p.x = newy;
        p.y = height + (y - newy);
        return p;
    }

    public Point modifyInX(int newx) {
        Point p = new Point();
        newx = Math.min(newx, x + width);
        newx = Math.max(newx, tdp.getMinX());
        newx = Math.min(newx, x + (width - minWidth));
        newx = Math.max(newx, x + (width - maxWidth));
        if ((father != null) && (drawingZoneRelativeToFather)) {
            newx = Math.min(maxX + father.getX(), Math.max(minX + father.getX(), newx));
        }
        p.x = newx;
        p.y = width + (x - newx);
        return p;
    }

    public Point modifyInWidth(int newx) {
        Point p = new Point();
        p.x = x;
        int newwidth = (newx - x);
        newwidth = Math.max(newwidth, minWidth);
        newwidth = Math.min(newwidth, maxWidth);

        //TraceManager.addDev("width = " + width + " newwidth = " + newwidth + " maxX =" + maxX + " x=" + x + " father.getX()=" +father.getX());
        if ((father != null) && (drawingZoneRelativeToFather)) {
            if ((newwidth + x) > (width + maxX + father.getX())) {
                newwidth = maxX + father.getX() + width - x;
            }
        } else {
            newwidth = Math.min(newwidth, tdp.getMaxX() - x);
        }

        p.y = newwidth;
        return p;
    }

    public Point modifyInHeight(int newy) {
        Point p = new Point();
        p.x = y;
        int newheight = (newy - y);
        newheight = Math.max(newheight, minHeight);
        newheight = Math.min(newheight, maxHeight);

        if ((father != null) && (drawingZoneRelativeToFather)) {
            if ((newheight + y) > (height + maxY + father.getY())) {
                newheight = maxY + father.getY() + height - y;
            }
        } else {
            newheight = Math.min(newheight, tdp.getMaxY() - y);
        }

        p.y = newheight;
        return p;
    }

    public void setUserResize(int desired_x, int desired_y, int desired_width, int desired_height) {
        //TraceManager.addDev("newx = " + desired_x + " newy = " + desired_y + " minWidth = " + minWidth);
        setCd(desired_x, desired_y);
        actionOnUserResize(desired_width, desired_height);
    }

    public void actionOnUserResize(int desired_width, int desired_height) {
        width = desired_width;
        height = desired_height;
        hasBeenResized();
    }


    public int getNbTotalComponent() {
        if (nbInternalTGComponent == 0) {
            return 1;
        } else {
            int cpt = 0;
            for (int i = 0; i < nbInternalTGComponent; i++) {
                cpt += tgcomponent[i].getNbTotalComponent();
            }
            return cpt;
        }
    }

    public TGComponent findTGComponentWithId(int searchedId) {
        if (id == searchedId) {
            return this;
        }

        TGComponent tgc;

        if (nbInternalTGComponent == 0) {
            return null;
        } else {
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i].findTGComponentWithId(searchedId);
                if (tgc != null) {
                    return tgc;
                }
            }
        }

        return null;

    }

    // Operations on Connecting Points

    public void addGroup(TGConnectingPointGroup cpg) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            connectingPoint[i].setGroup(cpg);
        }
    }

    public int getIndexOfTGConnectingPoint(TGConnectingPoint tp) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i] == tp) {
                return i;
            }
        }
        return -1;
    }

    public String getToolTipText() {
        return "";
    }

    public void drawTGConnectingPoint(Graphics g) {

        for (int i = 0; i < nbConnectingPoint; i++) {
            connectingPoint[i].draw(g);
        }

        if (this instanceof HiddenInternalComponents) {
            if (((HiddenInternalComponents) (this)).areInternalsHidden()) {
                return;
            }
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].drawTGConnectingPoint(g);
        }
    }

    public void drawOutFreeTGConnectingPointsCompatibleWith(Graphics g, int connectorID) {
        getTopFather().drawFromTopOutFreeTGConnectingPointsCompatibleWith(g, connectorID);
    }

    public void drawFromTopOutFreeTGConnectingPointsCompatibleWith(Graphics g, int connectorID) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            connectingPoint[i].drawOutAndFreeAndCompatible(g, connectorID);
        }
        if (this instanceof HiddenInternalComponents) {
            if (((HiddenInternalComponents) (this)).areInternalsHidden()) {
                return;
            }
        }
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].drawFromTopOutFreeTGConnectingPointsCompatibleWith(g, connectorID);
        }

    }


    public int makeTGConnectingPointsComment(int nb) {
        int i, len;

        //TraceManager.addDev("Adding comment points to " + this.getName());
        if (connectingPoint != null) {
            TGConnectingPoint[] tmp = connectingPoint;
            len = tmp.length;
            nbConnectingPoint = nbConnectingPoint + nb;
            connectingPoint = new TGConnectingPoint[nbConnectingPoint];
            for (i = 0; i < len; i++) {
                connectingPoint[i] = tmp[i];
            }
        } else {
            nbConnectingPoint = nb;
            connectingPoint = new TGConnectingPoint[nbConnectingPoint];
            len = 0;
        }
        return len;
    }


    public void addTGConnectingPointsCommentMiddle() {
        int len = makeTGConnectingPointsComment(8);
        generateTGConnectingPointsComment(len, -0.5, 0);
    }

    public void addTGConnectingPointsCommentCorner() {
        int len = makeTGConnectingPointsComment(4);
        generateTGConnectingPointsCommentCorner(len, 0, 0);
    }

    public void addTGConnectingPointsCommentTop() {
        int len = makeTGConnectingPointsComment(3);
        generateTGConnectingPointsCommentLine(len, 0, 0);
    }

    public void addTGConnectingPointsCommentDown() {
        int len = makeTGConnectingPointsComment(3);
        generateTGConnectingPointsCommentLine(len, 0, 1.0);
    }

    public void addTGConnectingPointsComment() {
        int len = makeTGConnectingPointsComment(8);
        generateTGConnectingPointsComment(len, 0, 0);
    }

    public void generateTGConnectingPointsComment(int len, double decw, double dech) {
        connectingPoint[len] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoint[len + 1] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
        connectingPoint[len + 2] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoint[len + 3] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.5 + dech);
        connectingPoint[len + 4] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.5 + dech);
        connectingPoint[len + 5] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
        connectingPoint[len + 6] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 1.0 + dech);
        connectingPoint[len + 7] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 1.0 + dech);
    }

    public void generateTGConnectingPointsCommentCorner(int len, double decw, double dech) {
        connectingPoint[len] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoint[len + 1] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoint[len + 2] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
        connectingPoint[len + 3] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 1.0 + dech);
    }

    public void generateTGConnectingPointsCommentLine(int len, double decw, double dech) {
        connectingPoint[len] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
        connectingPoint[len + 1] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
        connectingPoint[len + 2] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
    }

    public void drawTGConnectingPoint(Graphics g, int type) {
        //TraceManager.addDev("I am " + getName());
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i].isCompatibleWith(type)) {
                connectingPoint[i].draw(g);
            }
        }

        if (this instanceof HiddenInternalComponents) {
            if (((HiddenInternalComponents) (this)).areInternalsHidden()) {
                return;
            }
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].drawTGConnectingPoint(g, type);
        }
    }

    public TGConnectingPoint getTGConnectingPointAtIndex(int index) {
        if (index >= nbConnectingPoint) {
            return null;
        }
        return connectingPoint[index];
    }

    public TGConnectingPoint getTGConnectingPointAt(int x, int y) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if ((connectingPoint[i].getX() == x) && (connectingPoint[i].getY() == y)) {
                return connectingPoint[i];
            }
        }

        // look in subcomponents
        TGConnectingPoint p;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            p = tgcomponent[i].getTGConnectingPointAt(x, y);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    /*public TGConnectingPoint getFreeTGConnectingPointAt(int x, int y) {
      for (int i=0; i<nbConnectingPoint; i++) {
      if ((Math.abs(connectingPoint[i].getX() - x) < 3) && (Math.abs(connectingPoint[i].getY() - y) < 3) && (connectingPoint[i].isFree())){
      return connectingPoint[i];
      }
      }

      // look in subcomponents
      TGConnectingPoint p;
      for(int i=0; i<nbInternalTGComponent; i++) {
      p = tgcomponent[i].getFreeTGConnectingPointAt(x, y);
      if (p != null) {
      return p;
      }
      }
      return null;
      }*/

    /*public TGConnectingPoint getFreeTGConnectingPointAtAndCompatible(int x, int y, int type) {
        return getTopFather().getFromTopFreeTGConnectingPointAtAndCompatible(x, y, type);
    }*/

    public TGConnectingPoint getFreeTGConnectingPointAtAndCompatible(int x, int y, int type, TGConnectingPoint outPoint) {
        return getTopFather().getFromTopFreeTGConnectingPointAtAndCompatible(x, y, type, outPoint);
    }

    /*public TGConnectingPoint getFromTopFreeTGConnectingPointAtAndCompatible(int x, int y, int type) {
        //TraceManager.addDev("With null out");
        return getFromTopFreeTGConnectingPointAtAndCompatible(x, y, type, null);
    }*/

    public TGConnectingPoint getFromTopFreeTGConnectingPointAtAndCompatible(int x, int y, int type, TGConnectingPoint outPoint) {
        //TraceManager.addDev("Getting TGConnecting point outPoint=");

        for (int i = 0; i < nbConnectingPoint; i++) {
            if ((Math.abs(connectingPoint[i].getX() - x) < 4) && (Math.abs(connectingPoint[i].getY() - y) < 4) &&
                    (connectingPoint[i].isFree()) && (connectingPoint[i].isCompatibleWith(type, outPoint))) {
                return connectingPoint[i];
            }
        }

        // look in subcomponents
        TGConnectingPoint p;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            p = tgcomponent[i].getFromTopFreeTGConnectingPointAtAndCompatible(x, y, type, outPoint);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    public TGConnectingPoint findFirstFreeTGConnectingPoint(boolean out, boolean in) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if ((connectingPoint[i].isIn() == in) && (connectingPoint[i].isOut() == out) && (connectingPoint[i].isFree())) {
                return connectingPoint[i];
            }
        }

        TGConnectingPoint p;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            p = tgcomponent[i].findFirstFreeTGConnectingPoint(out, in);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    public TGConnectingPoint findConnectingPoint(int id) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i].getId() == id) {
                return connectingPoint[i];
            }
        }

        // look in subcomponents
        TGConnectingPoint p;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            p = tgcomponent[i].findConnectingPoint(id);
            if (p != null) {
                return p;
            }
        }
        return null;

    }

    // Returns the number of out TGConnectingPoints which are not free
    public int getNbNext() {
        int cpt = 0;

        for (int i = 0; i < nbConnectingPoint; i++) {
            if ((!connectingPoint[i].isFree()) && connectingPoint[i].isOut()) {
                cpt++;
            }
        }

        return cpt;
    }

    public TGConnectingPoint getNextTGConnectingPoint(int index) {
        int cpt = 0;

        for (int i = 0; i < nbConnectingPoint; i++) {
            if ((!connectingPoint[i].isFree()) && connectingPoint[i].isOut()) {
                if (index == cpt) {
                    return connectingPoint[i];
                }
                cpt++;
            }
        }
        return null;
    }

    public TGComponent containsId(int id) {
        if (getId() == id) {
            return this;
        } else {
            TGComponent tgc;
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i].containsId(id);
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        return null;
    }

    public TGComponent containsLoadedId(int id) {
        if ((getId() == id) && (loaded)) {
            return this;
        } else {
            TGComponent tgc;
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i].containsLoadedId(id);
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        return null;
    }

    // returns  true if at least the state of one has changed
    public boolean setStateTGConnectingPoint(int state) {
        boolean b = false;
        int stateTmp;
        for (int i = 0; i < nbConnectingPoint; i++) {
            stateTmp = connectingPoint[i].getState();
            if (stateTmp != state) {
                b = true;
            }
            connectingPoint[i].setState(state);
        }
        for (int i = 0; i < nbInternalTGComponent; i++) {
            b = b || tgcomponent[i].setStateTGConnectingPoint(state);
        }
        return b;
    }

    public boolean setIdTGConnectingPoint(int num, int id) {
        //TraceManager.addDev("name= " + name + " nbCP=" + nbConnectingPoint + " num=" + num +  "id=" + id);
        if (num >= nbConnectingPoint) {
            return false;
        }

        connectingPoint[num].forceId(id);
        return true;
    }


    public TGConnectingPoint getFreeTGConnectingPoint(int x, int y) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i].isCloseTo(x, y)) {
                if (connectingPoint[i].isFree())
                    return connectingPoint[i];
            }
        }
        TGConnectingPoint p;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            p = tgcomponent[i].getFreeTGConnectingPoint(x, y);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    public int getNbConnectingPoint() {
        return nbConnectingPoint;
    }

    public TGConnectingPoint tgconnectingPointAtIndex(int i) {
        if (i < nbConnectingPoint) {
            return connectingPoint[i];
        } else {
            return null;
        }
    }

    public TGConnectingPoint closerFreeTGConnectingPoint(int x, int y) {
        TGConnectingPoint currentCloser = null;
        TGConnectingPoint currentp;
        double d1, d2;
        int i;

        for (i = 0; i < nbInternalTGComponent; i++) {
            currentp = tgcomponent[i].closerFreeTGConnectingPoint(x, y);
            if ((currentp != null) && (currentp.isFree())) {
                if (currentCloser == null) {
                    currentCloser = currentp;
                } else {
                    d1 = Point2D.distanceSq(currentp.getX(), currentp.getY(), x, y);
                    d2 = Point2D.distanceSq(currentCloser.getX(), currentCloser.getY(), x, y);
                    //TraceManager.addDev("d1=" + d1 + " d2=" + d2 + "currentx=" + currentp.getX() + "currentcloserx=" + currentCloser.getX());
                    if (d1 < d2) {
                        currentCloser = currentp;
                    }
                }
            }
        }

        //compare currentcloser to my points.
        for (i = 0; i < nbConnectingPoint; i++) {
            currentp = connectingPoint[i];
            if ((currentp != null) && (currentp.isFree())) {
                if (currentCloser == null) {
                    currentCloser = currentp;
                } else {
                    d1 = Point2D.distanceSq(currentp.getX(), currentp.getY(), x, y);
                    d2 = Point2D.distanceSq(currentCloser.getX(), currentCloser.getY(), x, y);
                    if (d1 < d2) {
                        currentCloser = currentp;
                    }
                }
            }
        }
        return currentCloser;
    }

    public TGConnectingPoint closerFreeTGConnectingPoint(int x, int y, boolean out, boolean in) {
        TGConnectingPoint currentCloser = null;
        TGConnectingPoint currentp;
        double d1, d2;
        int i;
        int ref = 0;
        //compare currentcloser to my points.
        for (i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i] instanceof TGConnectingPointComment) {
                continue;
            }
            currentp = connectingPoint[i];
            if ((currentp != null) && (currentp.isFree()) && (currentp.isIn() == in) && (currentp.isOut() == out)) {
                if (currentCloser == null) {
                    currentCloser = currentp;
                    ref = i;
                } else {
                    d1 = Point2D.distanceSq(currentp.getX(), currentp.getY(), x, y);
                    d2 = Point2D.distanceSq(currentCloser.getX(), currentCloser.getY(), x, y);
                    if (d1 < d2) {
                        currentCloser = currentp;
                        ref = i;
                    }
                }
            }
        }
        if (currentCloser != null) {
            connectingPoint[ref].setFree(false);
            return connectingPoint[ref];
        }
        return currentCloser;
    }

    public TGConnectingPoint closerFreeTGConnectingPoint(int x, int y, boolean in) {
        TGConnectingPoint currentCloser = null;
        TGConnectingPoint currentp;
        double d1, d2;
        int i;

        for (i = 0; i < nbInternalTGComponent; i++) {
            currentp = tgcomponent[i].closerFreeTGConnectingPoint(x, y);
            if ((currentp != null) && (currentp.isFree()) && (currentp.isIn() == in)) {
                if (currentCloser == null) {
                    currentCloser = currentp;
                } else {
                    d1 = Point2D.distanceSq(currentp.getX(), currentp.getY(), x, y);
                    d2 = Point2D.distanceSq(currentCloser.getX(), currentCloser.getY(), x, y);
                    //TraceManager.addDev("d1=" + d1 + " d2=" + d2 + "currentx=" + currentp.getX() + "currentcloserx=" + currentCloser.getX());
                    if (d1 < d2) {
                        currentCloser = currentp;
                    }
                }
            }
        }

        //compare currentcloser to my points.
        for (i = 0; i < nbConnectingPoint; i++) {
            currentp = connectingPoint[i];
            if ((currentp != null) && (currentp.isFree()) && (currentp.isIn() == in)) {
                if (currentCloser == null) {
                    currentCloser = currentp;
                } else {
                    d1 = Point2D.distanceSq(currentp.getX(), currentp.getY(), x, y);
                    d2 = Point2D.distanceSq(currentCloser.getX(), currentCloser.getY(), x, y);
                    if (d1 < d2) {
                        currentCloser = currentp;
                    }
                }
            }
        }
        return currentCloser;
    }

    public boolean belongsToMe(TGConnectingPoint tgp) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i] == tgp) {
                return true;
            }
        }
        return false;
    }

    public TGComponent belongsToMeOrSon(TGConnectingPoint tgp) {
        TGComponent tgc;
        if (belongsToMe(tgp)) {
            return this;
        } else {
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i].belongsToMeOrSon(tgp);
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        return null;
    }

    public int indexOf(TGConnectingPoint tgp) {
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i] == tgp) {
                return i;
            }
        }
        return -1;
    }

    // checking and setting values
    public void setCdRectangle(int x1, int x2, int y1, int y2) {
        minX = x1;
        minY = y1;
        maxX = x2;
        maxY = y2;

        /*x = Math.max(minX, x);
          x = Math.min(maxX, x);

          y = Math.max(minY, y);
          y = Math.min(maxY, y);*/
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getZoomFactor() {
    	if ( tdp == null ) {
    		return 1.0;
    	}
    	
    	return tdp.getZoom();
    }
    
    public int getXZoom() {
        if (tdp == null) {
            return x;
        }

        return (int) (x * tdp.getZoom());
    }

    public int getYZoom() {
        if (tdp == null) {
            return y;
        }

        return (int) (y * tdp.getZoom());
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final int getMinWidth() {
        return minWidth;
    }

    public final int getMaxWidth() {
        return maxWidth;
    }

    public final int getMinHeight() {
        return minHeight;
    }

    public final int getMaxHeight() {
        return maxHeight;
    }

    public final int getMinDesiredWidth() {
        return minDesiredWidth;
    }

    public final int getMinDesiredHeight() {
        return minDesiredHeight;
    }

    public void calculateMyDesiredSize() {
    }

    public final void setMinSize(int w, int h) {
        minWidth = w;
        minHeight = h;
    }

    public final void setMaxSize(int w, int h) {
        if (w > -1) {
            maxWidth = w;
        }
        if (h > -1) {
            maxHeight = h;
        }
    }

    public final void setMinDesiredSize(int w, int h) {
        minDesiredWidth = w;
        minDesiredHeight = h;
    }

    public final void resize(int w, int h) {
        width = w;
        height = h;
        if (father != null) {
            //TraceManager.addDev("Resizing (" + w + "," + h + ")");
            father.newSizeForSon(this);
        }
    }

    public void hasBeenResized() {
    }

    public final void forceSize(int w, int h) {
        width = w;
        height = h;
    }

    protected void newSizeForSon(TGComponent tgc) {

    }

    public void setCd(int _x, int _y) {
        //TraceManager.addDev("SetCd -> " + this.getName());
        int oldX = x;
        int oldY = y;

        x = verifyCdX(_x);
        y = verifyCdY(_y);

        repaint = true;
        if ((oldX != x) || (oldY != y)) {
            // every son must be deplaced
            //TraceManager.addDev("Moving son");
            TGComponent tgc;
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i];
                if (tgc.moveWithFather()) {
                    //old: tgc.fatherHasMovedTo(tgc.getX() + x - oldX, tgc.getY() + y - oldY);
                    tgc.fatherHasMoved(x - oldX, y - oldY);
                }
            }
        }
    }

    public void setMoveCd(int _x, int _y, boolean forceMove) {
        //TraceManager.addDev("SetCd -> " + this.getName());
        int oldX = x;
        int oldY = y;

        // if ( father != null && drawingZoneRelativeToFather ) {
        if (!forceMove) {
            x = verifyMoveCdX(_x);
            y = verifyMoveCdY(_y);
        } else {
            x = _x;
            y = _y;
        }
        /*}
        
        else {
        if (oldX > _x)
        	x = verifyMoveCdX(Math.max(Math.max(_x, minX), Math.min(_x, maxX - width)));
        else
        	x = verifyMoveCdX(Math.min(Math.max(_x, minX), Math.min(_x, maxX - width)));
        
        if (oldY > _y)
        	y = verifyMoveCdY(Math.max(Math.max(_y, minY), Math.min(_y, maxY - height)));
        else
        	y = verifyMoveCdY(Math.min(Math.max(_y, minY), Math.min(_y, maxY - height)));
        }*/

        repaint = true;
        if ((oldX != x) || (oldY != y)) {
            // every son must be deplaced
            //TraceManager.addDev("Moving son");
            TGComponent tgc;
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i];
                if (tgc.moveWithFather() || (forceMove)) {
                    // old : tgc.fatherHasMovedTo(tgc.getX() + x - oldX, tgc.getY() + y - oldY);
                    tgc.fatherHasMoved(x - oldX, y - oldY);
                }
            }
        }
    }


    /*
     * @params
     * dx deplacement x
     * dy deplacement y
     *
     *
     *
     */
    public final void fatherHasMoved(int dx, int dy) {  //TraceManager.addDev("father has moved");
        TGComponent tgc;
        int oldX = x;
        int oldY = y;
        x = x + dx;
        y = y + dy;

        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc.moveWithFather()) {
                tgc.fatherHasMoved(x - oldX, y - oldY);

            }
        }


    }

    public void move(int decx, int decy) {
        //TraceManager.addDev("here111111111111");
        setMoveCd(x + decx, y + decy, false);
    }

    public void setMoveCd(int _x, int _y) {
        setMoveCd(_x, _y, false);
    }

    public void forceMove(int decx, int decy) {
        setMoveCd(x + decx, y + decy, true);
    }

    public int getId() {
        return id;
    }

    public UUID getUUID() {
        if (uniqueID == null) {
            makeUUID();
        }
        return uniqueID;
    }

    public void forceUUID(UUID newUUID) {
        uniqueID = newUUID;
    }

    public int getMaxId() {
        int ret = id;
        int i;

        for (i = 0; i < nbInternalTGComponent; i++) {
            ret = Math.max(ret, tgcomponent[i].getMaxId());
        }
        for (i = 0; i < nbConnectingPoint; i++) {
            ret = Math.max(ret, connectingPoint[i].getId());
        }
        return ret;
    }

    public void recalculateSize() {
    }

    public void checkAllMySize() {
        /*for(int i=0; i<nbInternalTGComponent; i++) {
          tgcomponent[i].checkAllMySize();
          }*/
        //checkMySize();
        recalculateSize();
    }

    //public void checkMySize() {}

    public TDiagramPanel getTDiagramPanel() {
        return tdp;
    }

    public void setLoaded(boolean b) {
        loaded = b;
    }

    public void forceId(int _id) {
        id = _id;
        ID = Math.max(ID, id + 1);
    }

    public void forceNewId() {
        id = ID;
        ID++;
    }

    public static void setGeneralId(int id) {
        ID = id;
    }

    public static int getGeneralId() {
        return ID;
    }

    public final boolean moveWithFather() {
        return moveWithFather;
    }

    public final void setMoveWithFather(boolean b) {
        moveWithFather = b;
    }

    private final int verifyCdX(int targetX) {
        // if it has a father, check that it is in its authorized area first
        if ((father != null) && (drawingZoneRelativeToFather)) {
            targetX = Math.min(maxX + father.getX(), Math.max(minX + father.getX(), targetX));
        }
        return targetX;
    }

    public final int verifyCdY(int targetY) {
        // if it has a father, check that it is in its authorized area first
        if ((father != null) && (drawingZoneRelativeToFather)) {
            targetY = Math.min(maxY + father.getY(), Math.max(minY + father.getY(), targetY));
        }
        return targetY;
    }

    protected final int verifyMoveCdX(int targetX) {
        // if it has a father, check that it is in its authorized area first
        if (father != null && drawingZoneRelativeToFather) {
            targetX = Math.min(maxX + father.getX(), Math.max(minX + father.getX(), targetX));
        }
        else {
        	
        	// Issue #174: Use the diagram min and max sizes when the component is not contained
        	final int minVal = Math.max( targetX, tdp.getMinX() );
        	final int maxVal = Math.min( targetX, tdp.getMaxX() - width );
        	targetX = x > targetX ? Math.max( minVal, maxVal ) : Math.min( minVal, maxVal );
//            if (x > targetX)
//                targetX = Math.max(Math.max(targetX, minX), Math.min(targetX, maxX - width));
//            else
//                targetX = Math.min(Math.max(targetX, minX), Math.min(targetX, maxX - width));
        }

        // Issue #46: Added the else.
        // When we are moving a contained component, we should not check for the max of the diagram. This should be done for the father only
        // Issue #14: Do not check the diagram size
        // The problem is that this method is applied after a zoom and readjusts the targeted coordinate if it is outside the diagram area. 
        // However the check involves getting the size of the component, but taking into account the size of its children (getCurrentMaxX) that 
        // have not been zoomed yet therefore leading to an erroneous calculation of size. Disable this verification for now.
//        else {
//            int currentWidthPos = Math.abs(getCurrentMaxX() - x);
//            int currentWidthNeg = Math.abs(getCurrentMinX() - x);
//            targetX = Math.max(Math.min(tdp.getMaxX() - currentWidthPos, targetX), tdp.getMinX() + currentWidthNeg);
//        }

        return targetX;
    }

    protected final int verifyMoveCdY(int targetY) {
        // if it has a father, check that it is in its authorized area first
        if ((father != null) && (drawingZoneRelativeToFather)) {
            targetY = Math.min(maxY + father.getY(), Math.max(minY + father.getY(), targetY));
        }
        else {
        	
        	// Issue #174: Use the diagram min and max sizes when the component is not contained
        	final int minVal = Math.max( targetY, tdp.getMinY() );
        	final int maxVal = Math.min( targetY, tdp.getMaxY() - height );
        	targetY = y > targetY ? Math.max( minVal, maxVal ) : Math.min( minVal, maxVal );
//            if (y > targetY)
//                targetY = Math.max(Math.max(targetY, minY), Math.min(targetY, maxY - height));
//            else
//                targetY = Math.min(Math.max(targetY, minY), Math.min(targetY, maxY - height));
        }
        // Issues #46 and #14: See comment in verifyMoveCdX
//        else {
//	        int currentWidthPos = Math.abs(getCurrentMaxY() - y);
//	        int currentWidthNeg = Math.abs(getCurrentMinY() - y);
//	        targetY = Math.max(Math.min(tdp.getMaxY() - currentWidthPos, targetY), tdp.getMinY() + currentWidthNeg);
//        }

        return targetY;
    }

    /*
     * to be defined within the derived classes if they wish to
     */
    public void resizeWithFather() {
    }

    public void resizeToFatherSize() {
        boolean modified = false;
        if (father != null) {
            if (width > father.getWidth()) {
                width = father.getWidth();
                modified = true;
            }

            if (height > father.getHeight()) {
                height = father.getHeight();
                modified = true;
            }
        }

        if (modified) {
            actionOnUserResize(width, height);
        }
    }

    public void wasSwallowed() {
    }

    public void wasUnswallowed() {
        setFather(null);
        TDiagramPanel tdp = getTDiagramPanel();
        setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
    }


    public final boolean isMoveable() {
        return moveable;
    }

    public final boolean isEditable() {
        return editable;
    }

    public final void setEditable(boolean b) {
        editable = b;
    }

    public final boolean isRemovable() {
        return removable;
    }

    public final boolean isCloneable() {
        return canBeCloned;
    }

    public final String getValue() {
        return value;
    }

    public final String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public void setRelativeDrawingZoneRelativeToFather(boolean b) {
        drawingZoneRelativeToFather = b;
    }

    public boolean getRelativeDrawingZoneRelativeToFather() {
        return drawingZoneRelativeToFather;
    }

    public final String getTopLevelName() {
        if (father == null) {
            return name;
        } else {
            return father.getTopLevelName();
        }
    }

    public boolean hasFather() {
        return (father != null);
    }

    public TGComponent getFather() {
        return father;
    }

    public TGComponent getTopFather() {
        if (father == null) {
            return this;
        } else {
            return father.getTopFather();
        }
    }

    public TGComponent topTGComponent() {
        if (father == null) {
            return this;
        } else {
            return father.topTGComponent();
        }
    }

    public void setValue(String v) {
        value = v;
        repaint = true;
    }

    public void setCustomData(String _custom) {
        customData = _custom;
    }

    public String getCustomData() {
        return customData;
    }



    public void setValueWithChange(String v) {
        value = v;
        tdp.actionOnValueChanged(this);
        repaint = true;
    }




    /**
     * Rename all reference of a primitive component.
     *
     * @param taskName new name
     * @author Fabien Tessier
     */
    public void setComponentName(String taskName) {
        for (TURTLEPanel tp : tdp.mgui.tabs)
            for (TDiagramPanel t : tp.getPanels()) {
                for (TGComponent t2 : t.componentList) {
                    if (t2 instanceof TMLArchiElementWithArtifactList) {
                        TMLArchiElementWithArtifactList tcpu = (TMLArchiElementWithArtifactList) t2;
                        for (TMLArchiArtifact art : tcpu.getArtifactList()) {
                            if (art.getTaskName().equals(value)) {
                                //TraceManager.addDev("Comparing:" + tdp.getName() + " with " + art.getReferenceTaskName());
                                if (art.getReferenceTaskName().equals(tdp.tp.getNameOfTab())) {
                                    art.setTaskName(taskName);
                                    String tmp = art.getValue().replaceAll("(?i)" + value + "$", taskName);
                                    art.setValue(tmp);
                                }
                            }
                        }
                    }
                }
                t.repaint();
            }
    }

    public final int getState() {
        return state;
    }

    public final void select(boolean b) {
        selected = b;
    }

    public final boolean isSelected() {
        return selected;
    }

    public int getType() {
        return -1;
    }

    public final TGComponent getSelectedInternalComponent() {
        return selectedInternalComponent;
    }

    public final TGComponent elementInState(int s) {
        TGComponent tgc;
        if (state == s) {
            return this;
        } else {
            for (int i = 0; i < nbInternalTGComponent; i++) {
                tgc = tgcomponent[i].elementInState(s);
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        return null;
    }

    // Returns true if modification on component
    public final boolean doubleClick(JFrame frame, int _x, int _y) {
        boolean b;
        if (multieditable) {
            b = editOnDoubleClick(frame, _x, _y);
        } else if (editable) {
            b = editOnDoubleClick(frame);
        } else {
            return false;
        }

        if (b && (this instanceof BasicErrorHighlight)) {
            BasicErrorHighlight beh = (BasicErrorHighlight) this;
            beh.setStateAction(0);
        }

        if (b && (this instanceof ActionStateErrorHighlight)) {
            ActionStateErrorHighlight aseh = (ActionStateErrorHighlight) this;
            aseh.setStateAction(0);
        }

        if (tdp instanceof TMLArchiDiagramPanel) {
            ((TMLArchiDiagramPanel) tdp).handleCPOnDoubleClick(this);
        }

        return b;
    }

    /**
     * Highlight the selected component
     *
     * @author Fabien Tessier
     * @param frame     :   Unused
     * @param x         :   X position
     * @param y         :   Y position
     */
    public final void singleClick(JFrame frame, int x, int y) {
        isSelect = true;
        tdp.highlightComponent(x, y);
    }

    public final void actionOnRemove() {
        if (removable) {
            myActionWhenRemoved();
        }
    }

    public void myActionWhenRemoved() {
        //TraceManager.addDev("I am removed:" + value);
    }

    protected boolean editOnDoubleClick(JFrame frame) {
        //  TraceManager.addDev("editOn....");
        return tdp.actionOnDoubleClick(this);
    }

    protected boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
        //  TraceManager.addDev("editOn....");
        return tdp.actionOnDoubleClick(this);
    }

    protected boolean actionOnAdd() {
        return tdp.actionOnAdd(this);
    }

    protected TGComponent makeClone() {
        return null;
    }

    public boolean belongsToMe(TGComponent t) {
        //  TGComponent tgc;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == t) {
                return true;
            } else {
                if (tgcomponent[i].belongsToMe(t)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean removeAllInternalComponents() {
        for(TGComponent tgc: tgcomponent) {
            tgc.actionOnRemove();
            tdp.actionOnRemove(tgc);
        }
        tgcomponent = new TGComponent[0];
        nbInternalTGComponent = 0;
        return true;
    }

    public boolean removeInternalComponent(TGComponent t) {
        //TGComponent tgc;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == t) {
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
                t.actionOnRemove();
                tdp.actionOnRemove(t);
                return true;
            } else {
                if (tgcomponent[i].removeInternalComponent(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addInternalComponent(TGComponent t, int index) {
        if (index < 0) {
            return false;
        }

        if (index > nbInternalTGComponent) {
            index = nbInternalTGComponent;
        }

        nbInternalTGComponent++;
        TGComponent[] tgcomponentbis = new TGComponent[nbInternalTGComponent];

        // TGComponent tgc;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (i < index) {
                tgcomponentbis[i] = tgcomponent[i];
            } else if (i == index) {
                tgcomponentbis[i] = t;
            } else {
                tgcomponentbis[i] = tgcomponent[i - 1];
            }
        }
        tgcomponent = tgcomponentbis;
        //TraceManager.addDev("Nb internal:" + nbInternalTGComponent);
        return true;
    }

    // This method is called when the stucture of a TDiagramPanel is modified

    public void TDPStructureChanged() {
        structureChanged();
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].TDPStructureChanged();
        }
    }

    public void structureChanged() {
    }

    public void TDPvalueChanged() {
        valueChanged();
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].TDPvalueChanged();
        }
    }

    public void bringToFront(TGComponent tgc) {
        if (nbInternalTGComponent < 2) {
            return;
        }

        if (tgcomponent[nbInternalTGComponent - 1] == tgc) {
            return;
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                tgcomponent[i] = tgcomponent[nbInternalTGComponent - 1];
                tgcomponent[nbInternalTGComponent - 1] = tgc;
            }
        }
    }

    public void bringToBack(TGComponent tgc) {
        if (nbInternalTGComponent < 2) {
            return;
        }

        if (tgcomponent[0] == tgc) {
            return;
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                tgcomponent[i] = tgcomponent[0];
                tgcomponent[0] = tgc;
            }
        }
    }

    public void valueChanged() {
    }


    // Pos contains either 0 : printed at x
    //                     1 : printed in the middle of the area
    public void drawLimitedString(Graphics g, String value, int x, int y, int maxW, int pos) {
        int w = g.getFontMetrics().stringWidth(value);
        if (x + w >= x + maxW) {
            value = "...";
            w = g.getFontMetrics().stringWidth(value);
            if (x + w >= x + maxW) {
                return;
            }
        }
        if (pos == 0) {
            g.drawString(value, x, y);
        } else {
            g.drawString(value, x + (maxW - w) / 2, y);
        }
    }

    // DIPLO ID
    public void setDIPLOID(int _ID) {
        DIPLOID = _ID;
    }

    public int getDIPLOID() {
        return DIPLOID;
    }

    // TEPE ID
    public void setTEPEID(int _ID) {
        TEPEID = _ID;
    }

    public int getTEPEID() {
        return TEPEID;
    }

    // AVATAR ID
    public void setAVATARID(int _ID) {
        AVATARID = _ID;
    }

    public int getAVATARID() {
        return AVATARID;
    }


    // saving
    public StringBuffer saveInXML() {
        return saveInXML(true, false);
    }

    protected StringBuffer saveInXML(boolean saveSubComponents, boolean saveAsComponentEvenIfNonNullFather) {
        StringBuffer sb = null;
        boolean b = (father == null) || saveAsComponentEvenIfNonNullFather;
        if (b) {
            sb = new StringBuffer(XML_HEAD);
        } else {
            sb = new StringBuffer(XML_SUB_HEAD);
        }
        sb.append(getType());
        sb.append(XML_ID);
        sb.append(getId());
        if (getUUID() != null) {
            sb.append(XML_UUID);
            sb.append(getUUID().toString());
        }
        sb.append("\" >\n");
        if (!b) {
            sb.append(translateFatherInformation());
        }
        if (reference != null) {
            sb.append(translateReferenceInformation());
        }
        sb.append(translateCDParam());
        sb.append(translateSizeParam());
        sb.append(translateHidden());

        // Issue #69
        if ( canBeDisabled() ) {
        //if (this instanceof CanBeDisabled) {
            sb.append(translateEnabled());
        }
        sb.append(translateCDRectangleParam());
        sb.append(translateNameValue());
        sb.append(translateCustomData());
        sb.append(translateConnectingPoints());
        sb.append(translateJavaCode());
        sb.append(translateInternalComment());
        sb.append(translateAccessibility());
        sb.append(translateCheckLatency());
        sb.append(translateInvariant());
        sb.append(translateMasterMutex());
        sb.append(translateBreakpoint());
        sb.append(translateExtraParam());
        sb.append(translateColor());

        if (b) {
            sb.append(XML_TAIL);
        } else {
            sb.append(XML_SUB_TAIL);
        }

        if (saveSubComponents) {
            sb.append(translateSubComponents());
        }

        return sb;
    }

    protected String translateReferenceInformation() {
        return "<reference id=\"" + reference.getId() + "\" />\n";
    }

    protected String translateFatherInformation() {
        return "<father id=\"" + father.getId() + "\" num=\"" + father.getMyNum(this) + "\" />\n";
    }

    protected String translateCDParam() {
        return "<cdparam x=\"" + x + "\" y=\"" + y + "\" />\n";
    }

    protected String translateSizeParam() {
        StringBuffer sb = new StringBuffer();
        sb.append("<sizeparam width=\"" + width + "\" height=\"" + height);
        //TraceManager.addDev("tgc = " + this + "minWidth=" + minWidth);
        sb.append("\" minWidth=\"" + minWidth + "\" minHeight=\"" + minHeight);
        sb.append("\" maxWidth=\"" + maxWidth + "\" maxHeight=\"" + maxHeight);
        sb.append("\" minDesiredWidth=\"" + minDesiredWidth + "\" minDesiredHeight=\"" + minDesiredHeight);
        sb.append("\" />\n");
        return new String(sb);
    }

    protected String translateEnabled() {
        return "<enabled value=\"" + isEnabled() + "\" />\n";
    }

    protected String translateHidden() {
        return "<hidden value=\"" + hidden + "\" />\n";
    }

    protected String translateAccessibility() {
        StringBuffer sb = new StringBuffer();
        if (accessibility) {
            sb.append("<accessibility />\n");
        }
        return new String(sb);
    }

    protected String translateCheckLatency() {
        StringBuffer sb = new StringBuffer();
        if (latencyCheck) {
            sb.append("<latencyCheck />\n");
        }
        return new String(sb);
    }

    protected String translateInvariant() {
        StringBuffer sb = new StringBuffer();
        if (invariant) {
            sb.append("<invariant />\n");
        }
        return new String(sb);
    }

    protected String translateMasterMutex() {
        StringBuffer sb = new StringBuffer();
        if (masterMutex) {
            sb.append("<mastermutex />\n");
        }
        return new String(sb);
    }

    protected String translateBreakpoint() {
        StringBuffer sb = new StringBuffer();
        if (breakpoint) {
            sb.append("<breakpoint />\n");
        }
        return new String(sb);
    }

    protected String translateCDRectangleParam() {
        StringBuffer sb = new StringBuffer();
        sb.append("<cdrectangleparam minX=\"" + minX);
        sb.append("\" maxX=\"" + maxX);
        sb.append("\" minY=\"" + minY);
        sb.append("\" maxY=\"" + maxY);
        sb.append("\" />\n");
        return new String(sb);
    }

    protected String translateNameValue() {
        String s = "<infoparam name=\"";
        s = s + GTURTLEModeling.transformString(name) + "\" value=\"";
        s = s + GTURTLEModeling.transformString(value);
        return s + "\" />\n";
    }

    protected String translateCustomData() {
        if ((customData != null) && (customData.length() > 0)) {
            String s = "<custom data=\"";
            s = s + GTURTLEModeling.transformString(customData);
            return s + "\" />\n";
        }
        return "";
    }

    protected String translateJavaCode() {
        String s1 = translateJavaCode("prejavacode", preJavaCode);
        String s2 = translateJavaCode("postjavacode", postJavaCode);
        return s1 + s2;
    }

    protected String translateInternalComment() {
        if (internalComment == null) {
            return "";
        }

        String code = GTURTLEModeling.transformString(internalComment);
        StringBuffer sb = new StringBuffer("");
        String[] codes = Conversion.wrapText(code);
        for (int i = 0; i < codes.length; i++) {
            sb.append("<InternalComment value=\"");
            sb.append(codes[i]);
            sb.append("\" />\n");
        }
        return new String(sb);
    }

    protected String translateJavaCode(String id, String code) {
        if (code == null) {
            return "";
        }

        code = GTURTLEModeling.transformString(code);
        StringBuffer sb = new StringBuffer("");
        String[] codes = Conversion.wrapText(code);
        for (int i = 0; i < codes.length; i++) {
            sb.append("<" + id + " value=\"");
            sb.append(codes[i]);
            sb.append("\" />\n");
        }
        return new String(sb);
    }

    protected String translateColor() {
        if (currentMainColor == null) {
            return "";
        }

        if (!(this instanceof ColorCustomizable)) {
            return "";
        }

        return "<color value=\"" + currentMainColor.getRGB() + "\" />\n";
    }

    protected String translateExtraParam() {
        return "";
    }

    protected StringBuffer translateSubComponents() {
        StringBuffer sb = new StringBuffer();
        //Added by Solange the next lines
        ProCSDPort p;
        ProCSDComponent c;

        for (int i = 0; i < nbInternalTGComponent; i++) {
            //Added by Solange
            //    TGComponent ruteo=tgcomponent[i];
            if ((tgcomponent[i].getType() == TGComponentManager.PROCSD_OUT_PORT) || (tgcomponent[i].getType() == TGComponentManager.PROCSD_IN_PORT)) {
                //I need to save the interface of the port too
                p = (ProCSDPort) tgcomponent[i];
                if (p.getMyInterface() != null) {
                    p.getMyInterface().select(true);
                    if (p.getMyInterface().getMyConnector() != null)
                        p.getMyInterface().getMyConnector().select(true);
                }
            }
            //Condition Added by Solange to select subcomponents
            if (tgcomponent[i].getType() == TGComponentManager.PROCSD_COMPONENT) {
                c = (ProCSDComponent) tgcomponent[i];
                c.select(true);
            }

            sb.append(tgcomponent[i].saveInXML());
        }
        return sb;
    }

    protected StringBuffer translateConnectingPoints() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nbConnectingPoint; i++) {
            if (connectingPoint[i] != null) {
                sb.append(connectingPoint[i].saveInXML(i));
            }
        }
        return sb;
    }

    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        return;
    }

    public final void makePostLoading(int decId) throws MalformedModelingException {
        //TraceManager.addDev("Make post loading of " + getName());
        postLoading(decId);
        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].postLoading(decId);
        }
    }

    public void postLoading(int decId) throws MalformedModelingException {
    }

    @Override
    public String toString() {
        String s1 = getName();
        String s2 = getValue();
        if ((s2 == null) || (s2.equals("null"))) {
            return s1 + " / " + id;
        }
        return s1 + ": " + s2 + " / " + id;
    }

    public Color getCurrentColor() {
        if (currentMainColor != null) {
            return currentMainColor;
        }
        if (this instanceof ColorCustomizable) {
            return ((ColorCustomizable)(this)).getMainColor();
        }

        return null;
    }

    public void setCurrentColor(int _rgb) {
        currentMainColor = new Color(_rgb);
    }

    public void setCurrentColor(Color _c) {
        currentMainColor = _c;
    }


    // popup menu

    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {

    }

    public boolean eventOnPopup(ActionEvent e) {
        return false;
    }

    // Main Tree

    public int getChildCount() {
        return nbInternalTGComponent;
    }

    public Object getChild(int index) {
        return tgcomponent[index];
    }

    public int getIndexOfChild(Object child) {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] == child) {
                return i;
            }
        }
        return -1;
    }

    public ImageIcon getImageIcon() {
        return myImageIcon;
    }

    public void searchForText(String text, Vector<Object> elements) {
        String save = saveInXML(false, false).toString().toLowerCase();
        if (save.indexOf(text) >= 0) {
            //TraceManager.addDev("Found " + this);
            elements.add(this);
        }

        for (int i = 0; i < nbInternalTGComponent; i++) {
            tgcomponent[i].searchForText(text, elements);
        }
    }

    public boolean isClickSelected() {
        return isSelect;
    }

    public void clickSelect(boolean b) {
        isSelect = b;
    }
    
    /**
     * Issue #69
     * @param point :   connecting point
     * @return      :   Connector
     */
    public TGConnector getConnectorConnectedTo( final TGConnectingPoint point ) {
    	return tdp.getConnectorConnectedTo( point );
    }
    
    /**
     * Issue #69
     * @return  :   List of connectors
     */
    public List<TGConnector> getConnectors() {
    	return tdp.getConnectors();
    }
    
    /**
     * Issue #69
     * @return  :   List of connectors
     */
    public List<TGConnector> getInputConnectors() {
    	final List<TGConnector> connectors = new ArrayList<TGConnector>();
    	final List<TGConnectingPoint> points = Arrays.asList( getConnectingPoints() );
    	
    	for ( final TGConnector connector : getConnectors() ) {
    		if ( points.contains( connector.getTGConnectingPointP2() ) ) {
    			connectors.add( connector );
    		}
    	}
    	
    	return connectors;
    }
    
    /**
     * Issue #69
     * @return  :   List of output connectors
     */
    public List<TGConnector> getOutputConnectors() {
    	final List<TGConnector> connectors = new ArrayList<TGConnector>();
    	final List<TGConnectingPoint> points = Arrays.asList( getConnectingPoints() );
    	
    	for ( final TGConnector connector : getConnectors() ) {
    		if ( points.contains( connector.getTGConnectingPointP1() ) ) {
    			connectors.add( connector );
    		}
    	}
    	
    	return connectors;
    }

	/**
	 * Issue #69
	 * @return  :   Array of connecting points
	 */
	public TGConnectingPoint[] getConnectingPoints() {
		return connectingPoint;
	}
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptForward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptForward( final ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			if ( tgcomponent !=  null ) {
				for ( final TGComponent subCompo : tgcomponent ) {
					subCompo.acceptForward( visitor );
				}
			}
			
			if ( connectingPoint !=  null ) {
				for ( final TGConnectingPoint point : connectingPoint ) {
					final TGConnector connector = getConnectorConnectedTo( point );
					
					if ( connector != null && point == connector.getTGConnectingPointP1() ) {
						point.acceptForward( visitor );
					}
				}
			}
		}
	}
	
    /* Issue #69
     * (non-Javadoc)
     * @see ui.CDElement#acceptBackward(ui.ICDElementVisitor)
     */
    @Override
	public void acceptBackward( final ICDElementVisitor visitor ) {
		if ( visitor.visit( this ) ) {
			if ( tgcomponent !=  null ) {
				for ( final TGComponent subCompo : tgcomponent ) {
					subCompo.acceptBackward( visitor );
				}
			}
			
			if ( connectingPoint !=  null ) {
				for ( final TGConnectingPoint point : connectingPoint ) {
					final TGConnector connector = getConnectorConnectedTo( point );
					
					if ( connector != null && point == connector.getTGConnectingPointP2() ) {
						point.acceptBackward( visitor );
					}
				}
			}
		}
	}
    
    public void renameTab(String s) {
    	TURTLEPanel tp = this.tdp.tp;
    	for (TDiagramPanel tdpTmp: tp.panels) {
    		if (tdpTmp.name.equals(name)) {
    	    	if (!tp.nameInUse(s)) {
    	            tp.tabbedPane.setTitleAt(tp.getIndexOfChild(tdpTmp), s);
    	            tp.panels.elementAt(tp.getIndexOfChild(tdpTmp)).setName(s);
    	            tp.mgui.changeMade(null, -1);
    	        }
    			break;
    		}
    	}
    }
    
   public boolean nameUsed(String s) {
    	return this.tdp.tp.refNameUsed(s);
   }

    /* #issue 82
     * added by Minh Hiep
     */
    public boolean isInHierarchy(TGComponent mainTgc) {
        TGComponent tgctmp;
        if (father == null) {
            tgctmp = this;
        } else {
            tgctmp = father;
        }
        while(tgctmp != null && tgctmp != mainTgc) {
            tgctmp = tgctmp.getFather();
        }
        return tgctmp == mainTgc;
    }


}

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


package ui.avatarrd;


import myutil.Conversion;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogChoiceSelection;
import ui.window.JDialogIDAndStereotype;
import ui.window.JDialogRequirement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Class AvatarRDRequirementReference
 * Avatar requirement reference: to be used in requirement diagram of AVATAR
 * Creation: 13/07/2018
 *
 * @author Ludovic APVRILLE
 * @version 1.0 13/07/2018
 */
public class AvatarRDRequirementReference extends TGCScalableWithInternalComponent implements WithAttributes /*, TGAutoAdjust*/ {
    public static int SIZE_LIMIT = 35;
    public static String  DEFAULT_REF = "UnsetReference";

    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
    protected int lineHeight = 30;
    private double dlineHeight = 0.0;
    //protected int reqType = 0;
    // 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;

    private Font myFont, myFontB;
    // private int maxFontSize = 30;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;

    private AvatarRDRequirement reference;


    // Icon
    private int iconSize = 18;
    //   private boolean iconIsDrawn = false;

    public AvatarRDRequirementReference(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(200, 120);
        oldScaleFactor = tdp.getZoom();
        dlineHeight = lineHeight * oldScaleFactor;
        lineHeight = (int) dlineHeight;
        dlineHeight = dlineHeight - lineHeight;

        minWidth = 1;
        minHeight = lineHeight;

        nbConnectingPoint = 40;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[1] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[2] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[3] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[4] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[5] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[6] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[7] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[8] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[9] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[10] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[11] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[12] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[13] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[14] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[15] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[16] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[17] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[18] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[19] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[20] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[21] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.25, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[22] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[23] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[24] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[25] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[26] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[27] = new AvatarRDConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        int i = 28;
        connectingPoint[0 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.0, 0.25);
        connectingPoint[1 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[2 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.0, 0.75);
        connectingPoint[3 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 1.0, 0.25);
        connectingPoint[4 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[5 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 1.0, 0.75);
        connectingPoint[6 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[7 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[8 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.75, 0.0);
        connectingPoint[9 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.25, 1.0);
        connectingPoint[10 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.5, 1.0);
        connectingPoint[11 + i] = new AvatarRDConnectingPointSatisfy(this, 0, 0, true, false, 0.75, 1.0);

        addTGConnectingPointsCommentTop();

        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];

//        int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        multieditable = true;

        //reqType = 0;

        value = DEFAULT_REF;

        oldValue = value;

        myImageIcon = IconManager.imgic5074;

    }

    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        //    Font fold = f;
        //  int w, c;
        int size;


        if ((reference == null) && (value.compareTo(DEFAULT_REF) != 0)) {
            ArrayList<TGComponent> comps = tdp.getMainGUI().getAllRequirements();
            String tdpName = getTDPName();
            String reqName = getRequirementName();
            for(TGComponent tgc: comps) {
                if (tgc instanceof AvatarRDRequirement) {
                    AvatarRDRequirement req = (AvatarRDRequirement)tgc;
                    if (req.getTDiagramPanel().getName().compareTo(tdpName) == 0) {
                        if (req.getRequirementName().compareTo(reqName) == 0) {
                            reference = req;
                            req.addReference(this);
                            break;
                        }
                    }
                }
            }
        }

        if (!tdp.isScaled()) {
            graphics = g;
        }

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize();
            //
            myFont = f.deriveFont((float) currentFontSize);
            myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        displayText = currentFontSize >= minFontSize;

        //   int h  = g.getFontMetrics().getHeight();

        g.drawRect(x, y, width, height);

        g.drawLine(x, y + lineHeight, x + width, y + lineHeight);
        Color topColor = null;
        if (reference != null) {
            topColor = reference.getTopColor();
        }
        if (topColor == null) {
            //TraceManager.addDev("Swithing back to default Color for:" + REQ_TYPE_STR.get(reqType));
            topColor = ColorManager.AVATAR_REQUIREMENT_TOP;
        } else {
            //TraceManager.addDev("Using color: " + topColor.getRGB() + "for  " +REQ_TYPE_STR.get(reqType));
        }
        g.setColor(topColor);
        g.fillRect(x + 1, y + 1, width - 1, lineHeight - 1);
        g.setColor(ColorManager.AVATAR_REQUIREMENT_ATTRIBUTES);
        g.fillRect(x + 1, y + 1 + lineHeight, width - 1, height - 1 - lineHeight);
        ColorManager.setColor(g, getState(), 0);
        if ((lineHeight > 23) && (width > 23)) {
            g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
        }

        if (displayText) {
            size = currentFontSize - 2;
            g.setFont(myFont.deriveFont((float) (myFont.getSize() - 2)));

            String req = "RefToRequirement";
            /*if (reference != null) {
                req = AvatarRDRequirement.REQ_TYPE_STR.get(reference.getRequirementType());
            }*/
            drawLimitedString(g, "<<" + req + ">>", x, y + size, width, 1);

            size += currentFontSize;
            g.setFont(myFontB);
            //  w = g.getFontMetrics().stringWidth(value);
            drawLimitedString(g, value, x, y + size, width, 1);

        }

        if (reference != null) {
            if (reference.isVerified()) {
                if (reference.isSatisfied()) {
                    Color tmp = g.getColor();
                    GraphicLib.setMediumStroke(g);
                    g.setColor(Color.green);
                    g.drawLine(x + width - 2, y - 6 + lineHeight, x + width - 6, y - 2 + lineHeight);
                    g.drawLine(x + width - 6, y - 3 + lineHeight, x + width - 8, y - 6 + lineHeight);
                    g.setColor(tmp);
                    GraphicLib.setNormalStroke(g);
                } else {
                    //g.drawString("acc", x + width - 10, y+height-10);
                    Color tmp = g.getColor();
                    GraphicLib.setMediumStroke(g);
                    g.setColor(Color.red);
                    g.drawLine(x + width - 2, y - 2 + lineHeight, x + width - 8, y - 8 + lineHeight);
                    g.drawLine(x + width - 8, y - 2 + lineHeight, x + width - 2, y - 8 + lineHeight);
                    g.setColor(tmp);
                    GraphicLib.setNormalStroke(g);
                }
            }

            g.setFont(myFont);
            String texti = "Text";
            String s;
            int i;
            size = lineHeight + currentFontSize;

            //ID
            if (size < (height - 2)) {
                drawLimitedString(g, "ID=" + reference.getID(), x + textX, y + size, width, 0);
            }
            size += currentFontSize;

            //text
            for (i = 0; i < reference.getTexts().length; i++) {
                if (size < (height - 2)) {
                    s = reference.getTexts()[i];
                    if (i == 0) {
                        s = texti + "=\"" + s;
                    }
                    if (i == (reference.getTexts().length - 1)) {
                        s = s + "\"";
                    }
                    drawLimitedString(g, s, x + textX, y + size, width, 0);
                }
                size += currentFontSize;

            }
            // Type and risk
            if (size < (height - 2)) {
                drawLimitedString(g, "Kind=\"" + reference.getKind() + "\"", x + textX, y + size, width, 0);
                size += currentFontSize;
                if (size < (height - 2)) {
                    drawLimitedString(g, "Risk=\"" + reference.getCriticality() + "\"", x + textX, y + size, width, 0);
                    size += currentFontSize;
                    if (size < (height - 2)) {

                        drawLimitedString(g, "Reference elements=\"" + reference.getReferenceElements() + "\"", x + textX, y + size, width, 0);
                        size += currentFontSize;

                        if (size < (height - 2)) {

                            if (reference.getRequirementType() == AvatarRDRequirement.SECURITY_REQ) {
                                drawLimitedString(g, "Targeted attacks=\"" + reference.getAttackTreeNode() + "\"", x + textX, y + size, width, 0);
                                size += currentFontSize;
                            }

                            if (reference.getRequirementType() == AvatarRDRequirement.SAFETY_REQ) {
                                drawLimitedString(g, "State violating req.=\"" + reference.getViolatedAction() + "\"", x + textX, y + size, width, 0);
                                size += currentFontSize;
                            }
                        }
                    }
                }
            }

            // Extra attributes
            for (i = 0; i < reference.getExtraParamIDs().size(); i++) {
                if (size < (height - 2)) {
                    s = reference.getExtraParamIDs().get(i) + ":" + reference.getExtraParamValues().get(i);
                    drawLimitedString(g, s, x + textX, y + size, width, 0);
                }
                size += currentFontSize;

            }
        }

        g.setFont(f);
    }

    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
        ArrayList<TGComponent> comps = tdp.getMainGUI().getAllRequirements();
        ArrayList<String> allRequirements = new ArrayList<>(comps.size());
        ArrayList<AvatarRDRequirement> realComps = new ArrayList<>(comps.size());
        allRequirements.add(DEFAULT_REF);
        realComps.add(null);
        int currentIndex = 0;
        for(TGComponent tgc: comps) {
            if (tgc instanceof AvatarRDRequirement) {
                AvatarRDRequirement req = (AvatarRDRequirement)tgc;
                realComps.add(req);
                String name = tgc.getTDiagramPanel().getName() + "::" + req.getRequirementName();
                if (name.compareTo(value) == 0) {
                    currentIndex = allRequirements.size();
                }
                allRequirements.add(name);

            }
        }
        String[] tabAllRequirements = allRequirements.toArray(new String[0]);
        JDialogChoiceSelection jdcs = new JDialogChoiceSelection(tdp.getGUI().getFrame(), "Setting Reference to a Requirement ",
                tabAllRequirements, currentIndex);
        // jdr.setSize(750, 400);
        GraphicLib.centerOnParent(jdcs, 500, 150);
        jdcs.setVisible(true);

        if (jdcs.hasBeenCancelled()) {
            return false;
        }

        currentIndex = jdcs.getIndexOfSelectedElement();
        value = tabAllRequirements[currentIndex];
        if (reference != null) {
            reference.removeReference(this);
        }
        reference = realComps.get(currentIndex);

        return true;


    }



    public void rescale(double scaleFactor) {
        dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
        lineHeight = (int) (dlineHeight);
        dlineHeight = dlineHeight - lineHeight;

        minHeight = lineHeight;

        super.rescale(scaleFactor);
    }


    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public String getRequirementReference() {
        return value;
    }



    public String getAttributes() {
        if (reference == null) {
            return "";
        }
        String attr = "ID=" + reference.getID() + "\n";
        attr += "Text= " + reference.getText() + "\n";
        attr += "Kind= " + reference.getKind() + "\n";
        attr += "Risk= " + reference.getCriticality() + "\n";
        attr += "References= " + reference.getReferenceElements() + "\n";
        if (reference.getRequirementType() == AvatarRDRequirement.SAFETY_REQ) {
            attr += "Violated action= " + reference.getViolatedAction() + "\n";
        }
        if (reference.getRequirementType() == AvatarRDRequirement.SECURITY_REQ) {
            attr += "Attack tree node(s)= " + reference.getAttackTreeNode() + "\n";
        }
        for(int i=0; i<reference.getExtraParamIDs().size(); i++) {
            attr += reference.getExtraParamIDs().get(i) + ": " + reference.getExtraParamValues().get(i) + "\n";
        }

        return attr;
    }


    public String getTDPName() {
        int index = value.indexOf("::");
        if (index == -1) {
            return null;
        }
        return value.substring(0, index);
    }

    public String getRequirementName() {
        int index = value.indexOf("::");
        if (index == -1) {
            return null;
        }
        return value.substring(index+2, value.length());
    }

    public AvatarRDRequirement getReference() {
        return reference;
    }

    public int getType() {
        return TGComponentManager.AVATARRD_REQUIREMENT_REFERENCE;
    }


    /*public void autoAdjust(int mode) {
        //

        if (graphics == null) {
            return;
        }

        Font f = graphics.getFont();
        Font f0 = f.deriveFont((float) currentFontSize);
        Font f1 = f0.deriveFont(Font.BOLD);
        Font f2 = f.deriveFont((float) (currentFontSize - 2));

        // Must find for both modes which width is desirable
        String s0, s1;
        s0 = "<<" + REQ_TYPE_STR.get(reqType) + ">>";
        s1 = "Text=";

        graphics.setFont(f2);
        int w0 = graphics.getFontMetrics().stringWidth(s0);
        graphics.setFont(f1);
        int w1 = graphics.getFontMetrics().stringWidth(value);
        int w2 = Math.max(w0, w1) + (2 * iconSize);

        graphics.setFont(f0);
        int w3, w4 = w2;
        int i;

        if (texts.length == 1) {
            w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[0] + "\"");
            w4 = Math.max(w4, w3);
        } else {
            for (i = 0; i < texts.length; i++) {
                if (i == 0) {
                    w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[i]);
                } else if (i == (texts.length - 1)) {
                    w3 = graphics.getFontMetrics().stringWidth(texts[i] + "\"");
                } else {
                    w3 = graphics.getFontMetrics().stringWidth(texts[i]);
                }

                w4 = Math.max(w4, w3 + 2);
            }
        }
        w3 = graphics.getFontMetrics().stringWidth("Kind=\"" + kind + "\"") + 2;
        w4 = Math.max(w4, w3);
        w3 = graphics.getFontMetrics().stringWidth("Risk=\"" + criticality + "\"") + 2;
        w4 = Math.max(w4, w3);
        w3 = graphics.getFontMetrics().stringWidth("ID=\"" + id + "\"") + 2;
        w4 = Math.max(w4, w3);

        if (mode == 1) {
            resize(w4, lineHeight);
            return;
        }

        int h;
        if (mode == 2) {
            h = ((texts.length + 4) * currentFontSize) + lineHeight;
        } else {
            h = ((texts.length + 5) * currentFontSize) + lineHeight;
        }

        resize(w4, h);

    }*/

    /*public ArrayList<AvatarRDProperty> getAllPropertiesVerified() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllPropertiesVerify(this);
    }

    public String getStringOfAllPropertiesVerified() {
        ArrayList<AvatarRDProperty> list = getAllPropertiesVerified();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDElementReference> getAllElementsSatisfied() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllElementsSatified(this);
    }

    public String getStringOfAllElementsSatisfied() {
        ArrayList<AvatarRDElementReference> list = getAllElementsSatisfied();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllImmediateSons() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllImmediateSons(this);
    }

    public String getStringOfAllImmediateSons() {
        ArrayList<AvatarRDRequirement> list = getAllImmediateSons();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllSons() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllSons(this);
    }

    public String getStringOfAllSons() {
        ArrayList<AvatarRDRequirement> list = getAllSons();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllImmediateFathers() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllImmediateFathers(this);
    }

    public String getStringOfAllImmediateFathers() {
        ArrayList<AvatarRDRequirement> list = getAllImmediateFathers();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllFathers() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllFathers(this);
    }

    public String getStringOfAllFathers() {
        ArrayList<AvatarRDRequirement> list = getAllFathers();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllMeRefineOrigin() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllMeRefine(this, 0);
    }

    public String getStringAllMeRefineOrigin() {
        ArrayList<AvatarRDRequirement> list = getAllMeRefineOrigin();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllMeRefineDestination() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllMeRefine(this, 1);
    }

    public String getStringAllMeRefineDestination() {
        ArrayList<AvatarRDRequirement> list = getAllMeRefineDestination();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllMeDeriveOrigin() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllMeDerive(this, 0);
    }

    public String getStringAllMeDeriveOrigin() {
        ArrayList<AvatarRDRequirement> list = getAllMeDeriveOrigin();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }

    public ArrayList<AvatarRDRequirement> getAllMeDeriveDestination() {
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        return myPanel.getAllMeDerive(this, 1);
    }

    public String getStringAllMeDeriveDestination() {
        ArrayList<AvatarRDRequirement> list = getAllMeDeriveDestination();
        String s = "";
        for(int i=0; i<list.size(); i++) {
            if (i>0) s+= " / ";
            s += list.get(i).getValue();
        }
        return s;
    }*/

}

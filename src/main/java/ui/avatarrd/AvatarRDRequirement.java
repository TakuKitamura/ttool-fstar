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
 * Class AvatarRDRequirement
 * Avatar requirement: to be used in requirement diagram of AVATAR
 * Creation: 20/04/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/04/2010
 */
public class AvatarRDRequirement extends TGCScalableWithInternalComponent implements WithAttributes, TGAutoAdjust {
    public static int SIZE_LIMIT = 35;

    public String oldValue;
    //protected int textX = 5;
    //protected int textY = 22;
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

    //protected static String[] DEFAULT_REQ = {"<<Requirement>>", "<<SafetyRequirement>>", "<<SecurityRequirement>>"};
    protected static ArrayList<String> REQ_TYPE_STR = new ArrayList<String>(Arrays.asList("Requirement", "SafetyRequirement",
            "SecurityRequirement"));
    protected static ArrayList<Color> REQ_TYPE_COLOR = new ArrayList<Color>(Arrays.asList(ColorManager.AVATAR_REQUIREMENT_TOP, ColorManager
            .AVATAR_REQUIREMENT_TOP, ColorManager.AVATAR_REQUIREMENT_TOP));
    //protected static int NB_REQ_TYPE = 3;

    protected final static int REGULAR_REQ = 0;
    protected final static int SAFETY_REQ = 1;
    protected final static int SECURITY_REQ = 2;

    public final static int HIGH = 0;
    public final static int MEDIUM = 1;
    public final static int LOW = 2;

    protected String text;
    protected String[] texts = {""};
    protected String kind = "";
    protected String criticality = "";
    protected int reqType = 0; // Type of stereotype
    protected String violatedAction = "";
    protected String attackTreeNode = "";
    protected String referenceElements = "";
    protected String id = "";

    protected ArrayList<String> extraParamIDs;
    protected ArrayList<String> extraParamValues;

    protected boolean satisfied = false;
    protected boolean verified = false;

    /*private JMenuItem isRegular = null;
    private JMenuItem isSafety = null;
    private JMenuItem isSecurity = null;*/
    private JMenuItem menuNonSatisfied = null;
    private JMenuItem menuSatisfied = null;
    private JMenuItem menuNonVerified = null;
    private JMenuItem menuVerified = null;
    private JMenuItem editAttributes = null;


    // Icon
    private int iconSize = 18;
    //   private boolean iconIsDrawn = false;


    // References
    private ArrayList<AvatarRDRequirementReference> references;

    public AvatarRDRequirement(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        //Issue #31
        textY = 22;
        textX = 5;
        minWidth = lineLength;
        minHeight = 10;
        initScaling(200, 120);
        
        oldScaleFactor = tdp.getZoom();
        dlineHeight = lineHeight * oldScaleFactor;
        lineHeight = (int) dlineHeight;
        dlineHeight = dlineHeight - lineHeight;

        minWidth = 1;
        minHeight = lineHeight;

        extraParamIDs = new ArrayList<>();
        extraParamValues = new ArrayList<>();

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

        id = "0";

        // Name of the requirement
        name = "Requirement";
        id = tdp.findAvatarRequirementID(id);
        try {
            value = tdp.findAvatarRequirementName("Requirement_", Integer.decode(id).intValue());
        } catch (Exception e) {
            value = tdp.findAvatarRequirementName("Requirement_", 0);
        }
        oldValue = value;

        myImageIcon = IconManager.imgic5006;

        text = "Requirement description:\nDouble-click to edit";

        references = new ArrayList<>();

        actionOnAdd();
    }

    public void makeValue() {
        texts = Conversion.wrapText(text);
    }
    

    @Override
    public void internalDrawing(Graphics g)
    {
    	// Rectangle and lines
    	g.drawRect(x, y, width, height);
    	g.drawLine(x, y + lineHeight, x + width, y + lineHeight);
    	
    	// Rectangle Filling
    	Color topColor = REQ_TYPE_COLOR.get(reqType);
    	if (topColor == null)
    		topColor = ColorManager.AVATAR_REQUIREMENT_TOP;
    	g.setColor(topColor);
    	g.fillRect(x + 1, y + 1, width - 1, lineHeight - 1);
    	g.setColor(ColorManager.AVATAR_REQUIREMENT_ATTRIBUTES);
    	g.fillRect(x + 1, y + 1 + lineHeight, width - 1, height - 1 - lineHeight);
    	ColorManager.setColor(g, getState(), 0);
	  
    	// check readability
    	if (!isTextReadable(g))
    		return;
    	Font f = g.getFont();
    	int size = f.getSize();

    	// TTool Icon
    	int borders = scale(3);
    	g.drawImage(scale(IconManager.img5100), x + width - scale(iconSize) - borders, y + borders, Color.yellow, null);
    	
    	//String
    	String req = "<<" + REQ_TYPE_STR.get(reqType) + ">>";
    	if (!canTextGoInTheBox(g, size, req, iconSize))
    		return;
    	g.setFont(f.deriveFont(Font.BOLD));
    	drawLimitedString(g, req, x, y + size, width, 1);
    	
    	//g.setFont(myFontB);
    	if (!canTextGoInTheBox(g, size, value, iconSize))
    		return;
    	Font iFont = f.deriveFont(Font.PLAIN);
    	g.setFont(iFont);
    	drawLimitedString(g, value, x, y + size * 2 + scale(2), width, 1);
		
    	internalDrawingAux(g, size);
    }
    
    //FIXME: need to make this function easier
    private void internalDrawingAux(Graphics g, int size)  {
        String texti = "Text";
        String s;
        int i;
        currentFontSize = g.getFont().getSize();
        size = lineHeight + currentFontSize;

        //ID
        if (size < (height - 2)) {
            drawLimitedString(g, "ID=" + id, x + textX, y + size, width, 0);
        }
        size += currentFontSize;

        //text
        for (i = 0; i < texts.length; i++) {
            if (size < (height - 2)) {
                s = texts[i];
                if (i == 0) {
                    s = texti + "=\"" + s;
                }
                if (i == (texts.length - 1)) {
                    s = s + "\"";
                }
                drawLimitedString(g, s, x + textX, y + size, width, 0);
            }
            size += currentFontSize;

        }
        // Type and risk
        if (size < (height - 2)) {
            drawLimitedString(g, "Kind=\"" + kind + "\"", x + textX, y + size, width, 0);
            size += currentFontSize;
            if (size < (height - 2)) {
                drawLimitedString(g, "Risk=\"" + criticality + "\"", x + textX, y + size, width, 0);
                size += currentFontSize;
                if (size < (height - 2)) {

                    drawLimitedString(g, "Reference elements=\"" + referenceElements + "\"", x + textX, y + size, width, 0);
                    size += currentFontSize;

                    if (size < (height - 2)) {

                        if (reqType == SECURITY_REQ) {
                            drawLimitedString(g, "Targeted attacks=\"" + attackTreeNode + "\"", x + textX, y + size, width, 0);
                            size += currentFontSize;
                        }

                        if (reqType == SAFETY_REQ) {
                            drawLimitedString(g, "State violating req.=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
                            size += currentFontSize;
                        }
                    }
                }
            }
        }

        // Extra attributes
        for (i = 0; i < extraParamIDs.size(); i++) {
            if (size < (height - 2)) {
                s = extraParamIDs.get(i) + ":" + extraParamValues.get(i);
                drawLimitedString(g, s, x + textX, y + size, width, 0);
            }
            size += currentFontSize;
        }
    }

//    public void internalDrawing(Graphics g) {
//        Font f = g.getFont();
//        //    Font fold = f;
//        //  int w, c;
//        int size;
//
//        if (texts == null) {
//            makeValue();
//        }
//
//        if (!tdp.isScaled()) {
//            graphics = g;
//        }
//
//        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
//            currentFontSize = tdp.getFontSize();
//            //
//            myFont = f.deriveFont((float) currentFontSize);
//            myFontB = myFont.deriveFont(Font.BOLD);
//
//            if (rescaled) {
//                rescaled = false;
//            }
//        }
//
//        displayText = currentFontSize >= minFontSize;
//
//        //   int h  = g.getFontMetrics().getHeight();
//
//        g.drawRect(x, y, width, height);
//
//        g.drawLine(x, y + lineHeight, x + width, y + lineHeight);
//        Color topColor = REQ_TYPE_COLOR.get(reqType);
//        if (topColor == null) {
//            //TraceManager.addDev("Swithing back to default Color for:" + REQ_TYPE_STR.get(reqType));
//            topColor = ColorManager.AVATAR_REQUIREMENT_TOP;
//        } else {
//            //TraceManager.addDev("Using color: " + topColor.getRGB() + "for  " +REQ_TYPE_STR.get(reqType));
//        }
//        g.setColor(topColor);
//        g.fillRect(x + 1, y + 1, width - 1, lineHeight - 1);
//        g.setColor(ColorManager.AVATAR_REQUIREMENT_ATTRIBUTES);
//        g.fillRect(x + 1, y + 1 + lineHeight, width - 1, height - 1 - lineHeight);
//        ColorManager.setColor(g, getState(), 0);
//        if ((lineHeight > 23) && (width > 23)) {
//            g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
//        }
//
//        if (displayText) {
//            size = currentFontSize - 2;
//            g.setFont(myFont.deriveFont((float) (myFont.getSize() - 2)));
//
//            drawLimitedString(g, "<<" + REQ_TYPE_STR.get(reqType) + ">>", x, y + size, width, 1);
//
//            size += currentFontSize;
//            g.setFont(myFontB);
//            //  w = g.getFontMetrics().stringWidth(value);
//            drawLimitedString(g, value, x, y + size, width, 1);
//
//        }
//
//        if (verified) {
//            if (satisfied) {
//                Color tmp = g.getColor();
//                GraphicLib.setMediumStroke(g);
//                g.setColor(Color.green);
//                g.drawLine(x + width - 2, y - 6 + lineHeight, x + width - 6, y - 2 + lineHeight);
//                g.drawLine(x + width - 6, y - 3 + lineHeight, x + width - 8, y - 6 + lineHeight);
//                g.setColor(tmp);
//                GraphicLib.setNormalStroke(g);
//            } else {
//                //g.drawString("acc", x + width - 10, y+height-10);
//                Color tmp = g.getColor();
//                GraphicLib.setMediumStroke(g);
//                g.setColor(Color.red);
//                g.drawLine(x + width - 2, y - 2 + lineHeight, x + width - 8, y - 8 + lineHeight);
//                g.drawLine(x + width - 8, y - 2 + lineHeight, x + width - 2, y - 8 + lineHeight);
//                g.setColor(tmp);
//                GraphicLib.setNormalStroke(g);
//            }
//        }
//
//        g.setFont(myFont);
//        String texti = "Text";
//        String s;
//        int i;
//        size = lineHeight + currentFontSize;
//
//        //ID
//        if (size < (height - 2)) {
//            drawLimitedString(g, "ID=" + id, x + textX, y + size, width, 0);
//        }
//        size += currentFontSize;
//
//        //text
//        for (i = 0; i < texts.length; i++) {
//            if (size < (height - 2)) {
//                s = texts[i];
//                if (i == 0) {
//                    s = texti + "=\"" + s;
//                }
//                if (i == (texts.length - 1)) {
//                    s = s + "\"";
//                }
//                drawLimitedString(g, s, x + textX, y + size, width, 0);
//            }
//            size += currentFontSize;
//
//        }
//        // Type and risk
//        if (size < (height - 2)) {
//            drawLimitedString(g, "Kind=\"" + kind + "\"", x + textX, y + size, width, 0);
//            size += currentFontSize;
//            if (size < (height - 2)) {
//                drawLimitedString(g, "Risk=\"" + criticality + "\"", x + textX, y + size, width, 0);
//                size += currentFontSize;
//                if (size < (height - 2)) {
//
//                    drawLimitedString(g, "Reference elements=\"" + referenceElements + "\"", x + textX, y + size, width, 0);
//                    size += currentFontSize;
//
//                    if (size < (height - 2)) {
//
//                        if (reqType == SECURITY_REQ) {
//                            drawLimitedString(g, "Targeted attacks=\"" + attackTreeNode + "\"", x + textX, y + size, width, 0);
//                            size += currentFontSize;
//                        }
//
//                        if (reqType == SAFETY_REQ) {
//                            drawLimitedString(g, "State violating req.=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
//                            size += currentFontSize;
//                        }
//                    }
//                }
//            }
//        }
//
//        // Extra attributes
//        for (i = 0; i < extraParamIDs.size(); i++) {
//            if (size < (height - 2)) {
//                s = extraParamIDs.get(i) + ":" + extraParamValues.get(i);
//                drawLimitedString(g, s, x + textX, y + size, width, 0);
//            }
//            size += currentFontSize;
//
//        }
//
//        g.setFont(f);
//    }

    public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
        // On the name ?
        oldValue = value;

        if ((displayText) && (_y <= (y + lineHeight))) {
            String text = getName() + ": ";
            if (hasFather()) {
                text = getTopLevelName() + " / " + text;
            }
            /*String s = (String) JOptionPane.showInputDialog(frame, text,
                    "Setting requirement name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                    null,
                    getValue());*/

            JDialogIDAndStereotype dialog = new JDialogIDAndStereotype(frame, "Setting Requirement ID", REQ_TYPE_STR.toArray(new String[0]), getValue
                    (), reqType,  REQ_TYPE_COLOR.toArray(new Color[0]),ColorManager.AVATAR_REQUIREMENT_TOP);
            //dialog.setSize(400, 300);
            GraphicLib.centerOnParent(dialog, 600, 450);
            // dialog.show(); // blocked until dialog has been closed
            dialog.setVisible(true);

            if (dialog.hasBeenCancelled()) {
                return false;
            }

            String s = dialog.getName();

            if ((s != null) && (s.length() > 0)){
                //boolean b;
                if (!s.equals(oldValue)) {
                    if (!TAttribute.isAValidId(s, false, false, false)) {
                        JOptionPane.showMessageDialog(frame,
                                "Could not change the name of the Requirement: the new name is not a valid name",
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }

                    if (!tdp.isRequirementNameUnique(s)) {
                        JOptionPane.showMessageDialog(frame,
                                "Could not change the name of the Requirement: the new name is already in use",
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }


                    if (graphics != null) {
                        int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
                        minDesiredWidth = Math.max(size, minWidth);
                        if (minDesiredWidth != width) {
                            newSizeForSon(null);
                        }
                    }
                    setValue(s);
                    return true;
                }

                if (!(tdp.actionOnDoubleClick(this))) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not change the name of the Requirement: this name is already in use",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    setValue(oldValue);
                    return false;
                }

                // Setting stereotype
                s = dialog.getStereotype().trim();

                if (!TAttribute.isAValidId(s, false, false, false)) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not use the new stereotype: the new stereotype name is not valid",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }

                int rgb = dialog.getColor();

                //TraceManager.addDev("RGBColor:" + rgb + " vs default color:" + ColorManager.REQ_TOP_BOX.getRGB());

                addStereotype(s, rgb);

            }
            return false;
        }

        return editAttributes();

    }

    public boolean addStereotype(String s, int rgb) {
        int index = -1;
        String sLower = s.toLowerCase();
        for (int i=0; i<REQ_TYPE_STR.size(); i++) {
            if (REQ_TYPE_STR.get(i).toLowerCase().compareTo(sLower) == 0) {
                index = i;
                break;
            }
        }

        // Found stereotype
        if (index != -1) {
            reqType = index;
            if (index > 0) {
                REQ_TYPE_COLOR.set(index, new Color(rgb));
            }
            return false;

        // Must add a new stereotype
        } else {
            REQ_TYPE_STR.add(s);
            REQ_TYPE_COLOR.add(new Color(rgb));
            reqType = REQ_TYPE_STR.size()-1;
            return true;
        }
    }

    public boolean editAttributes() {
        //String oldValue = value;
        String atn = null;
        String va = null;

        if (reqType == SECURITY_REQ) {
            atn = attackTreeNode;
        }


        if (reqType == SAFETY_REQ) {
            va = violatedAction;
        }

        JDialogRequirement jdr = new JDialogRequirement(tdp.getGUI().getFrame(), "Setting attributes of Requirement " + getRequirementName(), id,
                text, kind, criticality, va, reqType, atn, referenceElements, extraParamIDs, extraParamValues);
        // jdr.setSize(750, 400);
        GraphicLib.centerOnParent(jdr, 800, 400);
        jdr.setVisible(true);

        if (!jdr.isRegularClose()) {
            return false;
        }


        if (reqType == SAFETY_REQ) {
            violatedAction = jdr.getViolatedAction();
        }
        if (reqType == SECURITY_REQ) {
            attackTreeNode = jdr.getAttackTreeNode();
        }
        referenceElements = jdr.getReferenceElements();
        id = jdr.getId();
        text = jdr.getText();
        kind = jdr.getKind();
        criticality = jdr.getCriticality();

        // Filling extra attributes
        String extras = jdr.getExtraAttributes();
        extraParamValues.clear();
        extraParamIDs.clear();
        String[] lines = extras.split(System.getProperty("line.separator"));
        for(String line: lines) {
            int index0 = line.indexOf(':');
            if (index0 >  -1) {
                String id = line.substring(0, index0).trim();
                if (id.length() > 0) {
                    String val = line.substring(index0+1, line.length()).trim();
                    if (val.length() > 0) {
                        extraParamIDs.add(id);
                        extraParamValues.add(val);
                    }

                }
            }
        }


        makeValue();
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

    public String getRequirementName() {
        return value;
    }

    public boolean isSafety() {
        return (reqType == 1);
    }

    public void setRequirementType(int _type) {
        reqType = _type;
    }

    public int getRequirementType() {
        return reqType;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public boolean isVerified() {
        return verified;
    }

    public int getType() {
        return TGComponentManager.AVATARRD_REQUIREMENT;
    }

    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {

        componentMenu.addSeparator();

        menuNonSatisfied = new JMenuItem("Set as non satisfied");
        menuSatisfied = new JMenuItem("Set as satisfied");
        menuNonVerified = new JMenuItem("Set as non verified");
        menuVerified = new JMenuItem("Set as verified");


        menuNonSatisfied.addActionListener(menuAL);
        menuSatisfied.addActionListener(menuAL);
        menuNonVerified.addActionListener(menuAL);
        menuVerified.addActionListener(menuAL);

        editAttributes = new JMenuItem("Edit attributes");
        editAttributes.addActionListener(menuAL);


        menuNonSatisfied.setEnabled(satisfied);
        menuSatisfied.setEnabled(!satisfied);

        menuNonVerified.setEnabled(verified);
        menuVerified.setEnabled(!verified);


        componentMenu.addSeparator();
        componentMenu.add(menuNonSatisfied);
        componentMenu.add(menuSatisfied);
        componentMenu.add(menuNonVerified);
        componentMenu.add(menuVerified);
        componentMenu.add(editAttributes);
    }

    public boolean eventOnPopup(ActionEvent e) {
        //   String s = e.getActionCommand();

        if (e.getSource() == menuNonSatisfied) {
            satisfied = false;
        } else if (e.getSource() == menuSatisfied) {
            satisfied = true;
        } else if (e.getSource() == menuNonVerified) {
            verified = false;
        } else if (e.getSource() == menuVerified) {
            verified = true;
        } else {
            return editAttributes();
        }

        return true;
    }

    public String toString() {
        String ret = getValue();

        ret += "ID=" + id;

        ret += " " + text;
        ret += " criticality=" + criticality;

        if (SIZE_LIMIT > 0) {
            ret = ret.substring(0, SIZE_LIMIT) + "...";
        }

        return ret;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");

        if (texts != null) {
            for (int i = 0; i < texts.length; i++) {
                //value = value + texts[i] + "\n";
                sb.append("<textline data=\"");
                sb.append(GTURTLEModeling.transformString(texts[i]));
                sb.append("\" />\n");
            }
        }
        sb.append("<kind data=\"");
        sb.append(kind);
        sb.append("\" />\n");
        sb.append("<criticality data=\"");
        sb.append(criticality);
        sb.append("\" />\n");
        sb.append("<reqType data=\"");
        sb.append(REQ_TYPE_STR.get(reqType));
        sb.append("\" color=\"");
        sb.append(REQ_TYPE_COLOR.get(reqType).getRGB());
        sb.append("\" />\n");
        sb.append("<id data=\"");
        sb.append(id);
        sb.append("\" />\n");
        sb.append("<satisfied data=\"");
        sb.append(satisfied);
        sb.append("\" />\n");
        sb.append("<verified data=\"");
        sb.append(verified);
        sb.append("\" />\n");
        sb.append("<attackTreeNode data=\"");
        sb.append(attackTreeNode);
        sb.append("\" />\n");
        sb.append("<violatedAction data=\"");
        sb.append(violatedAction);
        sb.append("\" />\n");
        sb.append("<referenceElements data=\"");
        sb.append(referenceElements);
        sb.append("\" />\n");
        for(int i=0; i<extraParamIDs.size(); i++) {
            sb.append("<extraAttribute id=\"");
            sb.append(extraParamIDs.get(i));
            sb.append("\" value=\"");
            sb.append(extraParamValues.get(i));
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }


    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String oldtext = text;
            text = "";
            String s;

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("textline")) {
                                //
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                text += GTURTLEModeling.decodeString(s) + "\n";
                            } else if (elt.getTagName().equals("kind")) {
                                //
                                kind = elt.getAttribute("data");
                                if (kind.equals("null")) {
                                    kind = "";
                                }
                            } else if (elt.getTagName().equals("criticality")) {
                                //
                                criticality = elt.getAttribute("data");
                                if (criticality.equals("null")) {
                                    criticality = "";
                                }
                            } else if (elt.getTagName().equals("violatedAction")) {
                                //
                                violatedAction = elt.getAttribute("data");
                                if (violatedAction.equals("null")) {
                                    violatedAction = "";
                                }
                            } else if (elt.getTagName().equals("attackTreeNode")) {
                                //
                                attackTreeNode = elt.getAttribute("data");
                                if (attackTreeNode.equals("null")) {
                                    attackTreeNode = "";
                                }
                            } else if (elt.getTagName().equals("referenceElements")) {
                                //
                                referenceElements = elt.getAttribute("data");
                                if (referenceElements.equals("null")) {
                                    referenceElements = "";
                                }

                            } else if (elt.getTagName().equals("extraAttribute")) {
                                //
                                String tmp1 = elt.getAttribute("id");
                                String tmp2 = elt.getAttribute("value");
                                if ((tmp1 != null) && (tmp2 != null)) {
                                    if (tmp1.length() > 0) {
                                        extraParamIDs.add(tmp1);
                                        extraParamValues.add(tmp2);
                                    }
                                }

                            } else if (elt.getTagName().equals("reqType")) {
                                //
                                s = elt.getAttribute("data");
                                String tmp3 = elt.getAttribute("color");
                                int rgb = ColorManager.AVATAR_REQUIREMENT_TOP.getRGB();
                                try {
                                    rgb = Integer.decode(tmp3).intValue();
                                } catch (Exception e) {
                                }
                                if (s.equals("null")) {
                                    reqType = REGULAR_REQ;
                                } else {
                                    try {
                                        reqType = Integer.decode(s).intValue(); // default stereo: old way
                                    } catch (Exception e) {
                                        addStereotype(s, rgb);
                                    }
                                }
                                if (reqType > (REQ_TYPE_STR.size() - 1)) {
                                    reqType = REGULAR_REQ;
                                }

                            } else if (elt.getTagName().equals("id")) {
                                //
                                id = elt.getAttribute("data");
                                if (id.equals("null")) {
                                    id = "";
                                }
                                //
                            } else if (elt.getTagName().equals("satisfied")) {
                                //
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    satisfied = false;
                                } else {
                                    satisfied = s.equals("true");
                                }
                                //
                            } else if (elt.getTagName().equals("verified")) {
                                //
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    verified = false;
                                } else {
                                    verified = s.equals("true");
                                }
                            }
                            //
                        }
                    }
                }
            }
            if (text.length() == 0) {
                text = oldtext;
            }
        } catch (Exception e) {
            TraceManager.addError("Failed when loading requirement extra parameters (AVATARRD)");
            throw new MalformedModelingException();
        }

        makeValue();
    }


    public String getText() {
        return text;
    }

    public String[] getTexts() {
        return texts;
    }

    public String getID() {
        return id;
    }

    public String getKind() {
        return kind;
    }

    public String getViolatedAction() {
        return violatedAction;
    }

    public String getAttackTreeNode() {
        return attackTreeNode;
    }

    public String getReferenceElements() {
        return referenceElements;
    }

    public int getCriticality() {
        //
        if (criticality.compareTo("High") == 0) {
            return AvatarRDRequirement.HIGH;
        } else if (criticality.compareTo("Medium") == 0) {
            return AvatarRDRequirement.MEDIUM;
        } else {
            return AvatarRDRequirement.LOW;
        }
    }

    public String getStereotype() {
        return REQ_TYPE_STR.get(reqType);
    }

    public String getAttributes() {
        String attr = "ID=" + id + "\n";
        attr += "Text= " + text + "\n";
        attr += "Kind= " + kind + "\n";
        attr += "Risk= " + criticality + "\n";
        attr += "References= " + referenceElements + "\n";
        if (reqType == SAFETY_REQ) {
            attr += "Violated action= " + violatedAction + "\n";
        }
        if (reqType == SECURITY_REQ) {
            attr += "Attack tree node(s)= " + attackTreeNode + "\n";
        }
        for(int i=0; i<extraParamIDs.size(); i++) {
            attr += extraParamIDs.get(i) + ": " + extraParamValues.get(i) + "\n";
        }

        return attr;
    }

    public String getExtraAttributes() {
        String allAttr = "";
        for (int i=0; i<extraParamIDs.size(); i++) {
            if (i > 0) {
                allAttr += " / ";
            }
            allAttr += extraParamIDs.get(i) + ":" + extraParamValues.get(i);
        }
        return allAttr;
    }

    public void autoAdjust(int mode) {
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

    }

    public ArrayList<AvatarRDProperty> getAllPropertiesVerified() {
        ArrayList<AvatarRDProperty> props = new ArrayList<>();

        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        props.addAll(myPanel.getAllPropertiesVerify(this));

        for (AvatarRDRequirementReference ref: references) {
            myPanel = (AvatarRDPanel)(ref.getTDiagramPanel());
            props.addAll(myPanel.getAllPropertiesVerify(ref));
        }
        return props;
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
        ArrayList<AvatarRDElementReference> satis = new ArrayList<>();
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        satis.addAll(myPanel.getAllElementsSatified(this));

        for (AvatarRDRequirementReference ref: references) {
            myPanel = (AvatarRDPanel)(ref.getTDiagramPanel());
            satis.addAll(myPanel.getAllElementsSatified(ref));
        }
        return satis;
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

    public ArrayList<AvatarRDRequirementReference> getAllReferences() {
        return references;
    }

    public ArrayList<AvatarRDRequirement> getAllImmediateSons() {
        ArrayList<AvatarRDRequirement> sons = new ArrayList<>();
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        sons.addAll(myPanel.getAllImmediateSons(this));

        /*for (AvatarRDRequirementReference ref: references) {
            myPanel = (AvatarRDPanel)(ref.getTDiagramPanel());
            sons.addAll(myPanel.getAllImmediateSons(ref));
        }*/
        return sons;
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
        ArrayList<AvatarRDRequirement> sons = new ArrayList<>();
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        sons.addAll(myPanel.getAllSons(this));

        /*for (AvatarRDRequirementReference ref: references) {
            if (ref.getReference() != null) {
                myPanel = (AvatarRDPanel) (ref.getTDiagramPanel());
                sons.addAll(myPanel.getAllSons(ref.getReference()));
            }
        }*/
        return sons;
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

        ArrayList<AvatarRDRequirement> fathers = new ArrayList<>();
        AvatarRDPanel myPanel = (AvatarRDPanel)(getTDiagramPanel());
        fathers.addAll(myPanel.getAllImmediateFathers(this));

        /*for (AvatarRDRequirementReference ref: references) {
            myPanel = (AvatarRDPanel)(ref.getTDiagramPanel());
            fathers.addAll(myPanel.getAllImmediateFathers(ref));
        }*/
        return fathers;

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
    }


    // For references
    public Color getTopColor() {
        return REQ_TYPE_COLOR.get(reqType);
    }

    public ArrayList<String> getExtraParamIDs() {
        return extraParamIDs;
    }

    public ArrayList<String> getExtraParamValues() {
        return extraParamValues;
    }

    public void addReference(AvatarRDRequirementReference ref) {
        if (references.contains(ref)) {
            return;
        }
        references.add(ref);
    }

    public void removeReference(AvatarRDRequirementReference ref) {
        references.remove(ref);
    }

}

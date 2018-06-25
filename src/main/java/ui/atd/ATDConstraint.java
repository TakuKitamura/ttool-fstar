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


package ui.atd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogConstraintText;

import javax.swing.*;
import java.awt.*;

/**
 * Class ATDConstraint
 * Constraint of SysML Parametric diagrams, adapted to attack trees
 * Creation: 11/12/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.0 11/12/2009
 */
public class ATDConstraint extends TGCScalableWithInternalComponent implements SwallowedTGComponent, ConstraintListInterface {
    private int textY1 = 5;
    //private int textY2 = 30;

    public static final String[] STEREOTYPES = {"<<OR>>", "<<XOR>>", "<<AND>>", "<<SEQUENCE>>", "<<BEFORE>>", "<<AFTER>>"};

    protected String oldValue = "";

    private static int maxFontSize = 14;
    private static int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    //   private int textX = 1;

    private static int arc = 7;

    private String equation;

    public ATDConstraint(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = (int) (150 * tdp.getZoom());
        height = (int) (50 * tdp.getZoom());
        minWidth = 100;

        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[12];

        connectingPoint[0] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[2] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[4] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[5] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[6] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[7] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[8] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[9] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[10] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[11] = new ATDAttackConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        //addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "<<OR>>";
        equation = "";

        currentFontSize = -1;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic1078;
    }

    public void internalDrawing(Graphics g) {

        Font f = g.getFont();
        //     Font fold = f;

        if (currentFontSize == -1) {
            currentFontSize = f.getSize();
        }

        if ((rescaled) && (!tdp.isScaled())) {

            rescaled = false;

            float scale = (float) (f.getSize() * tdp.getZoom());
            scale = Math.min(maxFontSize, scale);
            currentFontSize = (int) scale;
            displayText = !(scale < minFontSize);
        }

        Color c = g.getColor();
        //g.draw3DRect(x, y, width, height, true);
        g.setColor(ColorManager.ATD_CONSTRAINT);
        g.fillRoundRect(x, y, width, height, arc, arc);
        g.setColor(c);
        g.drawRoundRect(x, y, width, height, arc, arc);

        g.setColor(ColorManager.ATD_CONSTRAINT);
        //g.fill3DRect(x+1, y+1, width-1, height-1, true);

        g.setColor(c);

        Font f0 = g.getFont();
        if (displayText) {
            f = f.deriveFont(currentFontSize);
            g.setFont(f.deriveFont(Font.BOLD));
            int w = g.getFontMetrics().stringWidth(value);
            g.drawString(value, x + (width - w) / 2, y + currentFontSize + (int) (textY1 * tdp.getZoom()));
            g.setFont(f0.deriveFont(f0.getSize() - 2).deriveFont(Font.ITALIC));
            w = g.getFontMetrics().stringWidth(equation);
            if (w >= width) {
                w = g.getFontMetrics().stringWidth("...");
                g.drawString("...", x + (width - w) / 2, y + (2 * currentFontSize) + (int) (textY1 * tdp.getZoom()));
            } else {
                g.drawString(equation, x + (width - w) / 2, y + (2 * currentFontSize) + (int) (textY1 * tdp.getZoom()));
            }
            g.setFont(f0);
        }

    }

    /* public void setValue(String val, Graphics g) {
       oldValue = value;
       int w  = g.getFontMetrics().stringWidth(value);
       int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);

       //
       if (w1 != width) {
       width = w1;
       resizeWithFather();
       }
       //
       }*/


    public boolean editOndoubleClick(JFrame frame) {
//        String tmp;
//        boolean error = false;

        JDialogConstraintText dialog = new JDialogConstraintText(frame, "Setting constraint attributes", this, equation, "Equation");
        //   dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog, 450, 350);
        dialog.setVisible(true); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getStereotype() == null) {
            return false;
        }

        if (dialog.getStereotype().length() > 0) {
            value = dialog.getStereotype();
        }

        equation = dialog.getText();

        rescaled = true;

        return true;
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public int getType() {
        return TGComponentManager.ATD_CONSTRAINT;
    }

    public String[] getConstraintList() {
        return STEREOTYPES;
    }

    public String getCurrentConstraint() {
        return value;
    }

    public String getEquation() {
        return equation;
    }

    public boolean isOR() {
        return (value.compareTo(STEREOTYPES[0]) == 0);
    }

    public boolean isXOR() {
        return (value.compareTo(STEREOTYPES[1]) == 0);
    }

    public boolean isAND() {
        return (value.compareTo(STEREOTYPES[2]) == 0);
    }

    public boolean isSequence() {
        return (value.compareTo(STEREOTYPES[3]) == 0);
    }

    public boolean isBefore() {
        return (value.compareTo(STEREOTYPES[4]) == 0);
    }

    public boolean isAfter() {
        return (value.compareTo(STEREOTYPES[5]) == 0);
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info equation=\"" + GTURTLEModeling.transformString(getEquation()));
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
//            int t1id;
//            String sdescription = null;
//            String prio;
//            String isRoot = null;

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
                                equation = elt.getAttribute("equation");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public void resizeWithFather() {
        if ((father != null) && (father instanceof ATDBlock)) {
            //
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }


}

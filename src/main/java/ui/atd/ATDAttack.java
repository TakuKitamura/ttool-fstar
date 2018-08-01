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
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttack;

import javax.swing.*;
import java.awt.*;

/**
 * Class ATDAttack
 * Attack -> SysML value type
 * Creation: 09/12/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.0 09/12/2009
 */
public class ATDAttack extends TGCScalableWithInternalComponent implements SwallowedTGComponent, WithAttributes, CheckableAccessibility/*, CanBeDisabled*/ {
    private int textY1 = 3;
    //   private int textY2 = 3;

    // private static int arc = 7;
    //private int textX = 10;

    protected String oldValue = "";
    protected String description = "";
    private String stereotype = "attack";
    private String rootStereotype = "root attack";
    private boolean isRootAttack = false;

    private static int maxFontSize = 14;
    private static int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    private int textX = 10;

    public ATDAttack(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 125;
        height = (int) (40 * tdp.getZoom());
        minWidth = 100;

        nbConnectingPoint = 24;
        connectingPoint = new TGConnectingPoint[24];

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
        connectingPoint[12] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[13] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[14] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[15] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[16] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[17] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[18] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[19] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[20] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[21] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[22] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[23] = new ATDCountermeasureConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        //addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = tdp.findAttackName("attack");
        description = "blah blah blah";

        currentFontSize = -1;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic702;
    }

    @Override
    public void internalDrawing(Graphics g) {
        String ster;
        if (isRootAttack) {
            ster = "<<" + rootStereotype + ">>";
        } else {
            ster = "<<" + stereotype + ">>";
        }
        Font f = g.getFont();
        Font fold = f;

        if (value != oldValue) {
            setValue(value, g);
        }


        if (currentFontSize == -1) {
            currentFontSize = f.getSize();
        }

        if ((rescaled) && (!tdp.isScaled())) {
            rescaled = false;


            // Must set the font size ..
            // Find the biggest font not greater than max_font size
            // By Increment of 1
            // Or decrement of 1
            // If font is less than 4, no text is displayed

            /*int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
              int w0, w1, w2;
              f = f.deriveFont((float)maxCurrentFontSize);
              g.setFont(f);
              //
              while(maxCurrentFontSize > (minFontSize-1)) {
              w0 = g.getFontMetrics().stringWidth(value);
              w1 = g.getFontMetrics().stringWidth(ster);
              w2 = Math.min(w0, w1);
              if (w2 < (width - (2*textX))) {
              break;
              }
              maxCurrentFontSize --;
              f = f.deriveFont((float)maxCurrentFontSize);
              g.setFont(f);
              }
              currentFontSize = maxCurrentFontSize;

              if(currentFontSize <minFontSize) {
              displayText = false;
              } else {
              displayText = true;
              f = f.deriveFont((float)currentFontSize);
              g.setFont(f);
              }*/


            float scale = (float) (f.getSize() * tdp.getZoom());
            scale = Math.min(maxFontSize, scale);
            currentFontSize = (int) scale;
            if (scale < minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                setValue(value, g);
            }
        }
        // Core of the attack
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);
        
        if (isEnabled()) {
            if (isRootAttack) {
                g.setColor(ColorManager.ATD_ROOT_ATTACK);
            } else {
                g.setColor(ColorManager.ATD_ATTACK);
            }
        } 
        else {
    		// Issue #69: Disabled color now managed in TGComponent / ColorManager
	    	// For filled shapes ensure background color is white so that text is
	    	// readable
	    	g.setColor( ColorManager.DISABLED_FILLING );
//            g.setColor(ColorManager.ATD_ATTACK_DISABLED);
        }

        g.fill3DRect(x + 1, y + 1, width - 1, height - 1, true);
        g.setColor(c);

        // Strings
        int w;

        //TraceManager.addDev("display text of attack=" + displayText);

        if (displayText) {
            f = f.deriveFont((float) currentFontSize);
            g.setFont(f);
            //Font f0 = g.getFont();

            boolean cannotWriteAttack = (height < (2 * currentFontSize + (int) (textY1 * tdp.getZoom())));
            //TraceManager.addDev("Zoom=" + tdp.getZoom() + " Cannot write attack=" + cannotWriteAttack + "Font=" + f0);
            if (cannotWriteAttack) {
                w = g.getFontMetrics().stringWidth(value);
                int h = currentFontSize + (int) (textY1 * tdp.getZoom());
                if ((w < (2 * textX + width)) && (h < height)) {
                    g.drawString(value, x + (width - w) / 2, y + h);
                } else {
                    w = g.getFontMetrics().stringWidth(ster);
                    if ((w < (2 * textX + width)) && (h < height)) {
                        g.drawString(ster, x + (width - w) / 2, y + h);
                    }
                }
            } else {
                g.setFont(f.deriveFont(Font.BOLD));
                int h = currentFontSize + (int) (textY1 * tdp.getZoom());
                int cumulated = 0;
                w = g.getFontMetrics().stringWidth(ster);
                if ((w < (2 * textX + width)) && (h < height)) {
                    g.drawString(ster, x + (width - w) / 2, y + h);
                    cumulated = h;
                }
                g.setFont(f);
                w = g.getFontMetrics().stringWidth(value);
                h = cumulated + currentFontSize + (int) (textY1 * tdp.getZoom());
                if ((w < (2 * textX + width)) && (h < height)) {
                    //TraceManager.addDev("Drawing value=" + value);
                    g.drawString(value, x + (width - w) / 2, y + h);
                } else {
                    g.drawString(value, x + (width - w) / 2, y + h);
                    //TraceManager.addDev("--------------------------------------------------- Cannot draw value=" + value);
                    //TraceManager.addDev("w=" + w + " val=" + (2*textX + width) + "h=" + h + " height=" + height + " zoom=" + tdp.getZoom() + " Font=" + f0);
                }
            }

            // Issue #69: Use the same disabling UI as other components
//            if (!isEnabled()) {
//                String val = "disabled";
//                w = g.getFontMetrics().stringWidth(val);
//                //int h =  currentFontSize + (int)(textY1 * tdp.getZoom());
//                g.setFont(f.deriveFont(Font.ITALIC));
//                g.drawString(val, x + (width - w - 5), y + height - 2);
//            }


        } else {
            TraceManager.addDev("-------------------------------------------------- Cannot display text of attack");
        }

        g.setFont(fold);

    }

    private void setValue(String val, Graphics g) {
        oldValue = value;
        String ster;
        if (isRootAttack) {
            ster = "<<" + rootStereotype + ">>";
        } else {
            ster = "<<" + stereotype + ">>";
        }

        Font f0 = g.getFont();

        if (currentFontSize != -1) {
            if (currentFontSize != f0.getSize()) {
                g.setFont(f0.deriveFont((float) currentFontSize));
            }
        }

        int w = Math.max(g.getFontMetrics().stringWidth(value), g.getFontMetrics().stringWidth(ster));
        int w1 = Math.max((int) (minWidth * tdp.getZoom()), w + 2 * textX);

        //
        if (w1 != width) {
            width = w1;
            resizeWithFather();
        }


        g.setFont(f0);
    }

    @Override
    public void resizeWithFather() {
        if ((father != null) && (father instanceof ATDBlock)) {
            //
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }

    @Override
    public boolean editOndoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;

        JDialogAttack dialog = new JDialogAttack(frame, "Setting attack attributes", this);
        //     dialog.setSize(450, 350);
        GraphicLib.centerOnParent(dialog, 450, 350);
        dialog.setVisible(true); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getName() == null) {
            return false;
        }

        if (dialog.getName().length() > 0) {
            tmp = dialog.getName();
            if (!TAttribute.isAValidId(tmp, false, false)) {
                error = true;
            } else {
                value = tmp;
            }
        }


        if (dialog.getDescription() != null) {
            description = dialog.getDescription();
        }

        isRootAttack = dialog.isRootAttack();

        if (error) {
            JOptionPane.showMessageDialog(frame,
                    "Name is non-valid",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        return !error;
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.ATD_ATTACK;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info description=\"" + description);
        sb.append("\" root=\"" + isRootAttack);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //      int t1id;
            String sdescription = null;
            //     String prio;
            String isRoot = null;

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
                                sdescription = elt.getAttribute("description");
                                isRoot = elt.getAttribute("root");
                            }
                            if (sdescription != null) {
                                description = sdescription;
                            }
                            if (isRoot != null) {
                                isRootAttack = isRoot.toUpperCase().compareTo("TRUE") == 0;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        description = _description;
    }

    public String getAttackName() {
        return value;
    }

    public String getAttributes() {
        String s = "Description = " + description + "\n";
        s += "Id=" + getId();
        return s;
    }

    public boolean isRootAttack() {
        return isRootAttack;
    }

    public void wasUnswallowed() {
        setFather(null);
        TDiagramPanel tdp = getTDiagramPanel();
        setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
    }
    
    /**
     * Issue #69
     * @return
     */
    @Override
    public boolean canBeDisabled() {
    	return true;
    }
}

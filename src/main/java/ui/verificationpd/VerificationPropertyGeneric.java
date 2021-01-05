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




package ui.verificationpd;


import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogManageListOfString;
import verification.Property;
import verification.VerificationStorage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
   * Class SysmlsecMethodologyDiagramReference
   * Diagram reference requirement: Used to reference diagrams from the
   * Sysmlsec methodology
   * Creation: 26/01/2016
   * @version 1.0 26/01/2016
   * @author Ludovic APVRILLE
 */
public abstract class VerificationPropertyGeneric extends TGCScalableWithInternalComponent implements VerificationStorage {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
    protected int lineHeight = 30;
    protected double dlineHeight = 0.0;
    //protected int reqType = 0;
    // 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    protected Graphics graphics;
    protected int iconSize = 30;

    protected Font myFont, myFontB;
    protected int maxFontSize = 30;
    protected int minFontSize = 4;
    protected int currentFontSize = -1;
    protected boolean displayText = true;





    protected Vector<Property> properties;


    





    // Icon
    //private int iconSize = 18;
    //private boolean iconIsDrawn = false;

    public VerificationPropertyGeneric(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(200, 120);
        oldScaleFactor = tdp.getZoom();
        dlineHeight = lineHeight * oldScaleFactor;
        lineHeight = (int)dlineHeight;
        dlineHeight = dlineHeight - lineHeight;

        minWidth = 10;
        minHeight = lineHeight;

        addTGConnectingPointsCommentTop();

        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];

        //int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;

        moveable = true;
        editable = true;
        removable = false;
        userResizable = true;
        multieditable = true;



        oldValue = value;

        myImageIcon = IconManager.imgic5006;

        properties = new Vector<>();


        actionOnAdd();
    }


    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
//        Font fold = f;
//        int w, c;
        int size;


        if (!tdp.isScaled()) {
            graphics = g;
        }

        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
            currentFontSize = tdp.getFontSize();
            //
            myFont = f.deriveFont((float)currentFontSize);
            myFontB = myFont.deriveFont(Font.BOLD);

            if (rescaled) {
                rescaled = false;
            }
        }

        displayText = currentFontSize >= minFontSize;

     //   int h  = g.getFontMetrics().getHeight();

        g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        g.fillRect(x, y, width, height);
        ColorManager.setColor(g, getState(), 0);
        g.drawRect(x, y, width, height);

        //g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
        //g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        //g.fillRect(x+1, y+1, width-1, lineHeight-1);
        //g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        //g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
        ColorManager.setColor(g, getState(), 0);
        if ((lineHeight > 23) && (width > 23)){
            g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
        }

        if (displayText) {
            size = currentFontSize - 2;
            g.setFont(myFontB);

            drawLimitedString(g, value, x, y + size + 3, width, 1);
            g.setFont(f);
        }

        /*if (displayText) {
          size = currentFontSize - 2;
          g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));

          drawLimitedString(g, REQ_TYPE_STR[reqType], x, y + size, width, 1);

          size += currentFontSize;
          g.setFont(myFontB);
          w = g.getFontMetrics().stringWidth(value);
          drawLimitedString(g, value, x, y + size, width, 1);

          }

          if (verified) {
          if (satisfied) {
          Color tmp = g.getColor();
          GraphicLib.setMediumStroke(g);
          g.setColor(Color.green);
          g.drawLine(x+width-2, y-6+lineHeight, x+width-6, y-2+lineHeight);
          g.drawLine(x+width-6, y-3+lineHeight, x+width-8, y-6+lineHeight);
          g.setColor(tmp);
          GraphicLib.setNormalStroke(g);
          } else {
          //g.drawString("acc", x + width - 10, y+height-10);
          Color tmp = g.getColor();
          GraphicLib.setMediumStroke(g);
          g.setColor(Color.red);
          g.drawLine(x+width-2, y-2+lineHeight, x+width-8, y-8+lineHeight);
          g.drawLine(x+width-8, y-2+lineHeight, x+width-2, y-8+lineHeight);
          g.setColor(tmp);
          GraphicLib.setNormalStroke(g);
          }
          }

          g.setFont(myFont);
          String texti = "Text";
          String s ;
          int i;
          size = lineHeight + currentFontSize;

          //ID
          if (size < (height - 2)) {
          drawLimitedString(g, "ID=" + id, x + textX, y + size, width, 0);
          }
          size += currentFontSize;

          //text
          for(i=0; i<texts.length; i++) {
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
          }

          if (reqType == SAFETY_REQ) {
          drawLimitedString(g, "Violated action=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
          }
          }
          }
          }
          }


          g.setFont(f);*/
    }

    public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
        //addDiagramReference(frame);
        return true;
        // On the name ?
        /*oldValue = value;

          if ((displayText) && (_y <= (y + lineHeight))) {
          String text = getName() + ": ";
          if (hasFather()) {
          text = getTopLevelName() + " / " + text;
          }
          String s = (String)JOptionPane.showInputDialog(frame, text,
          "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
          null,
          getValue());

          if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
          //boolean b;
          if (!TAttribute.isAValidId(s, false, false)) {
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


          int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
          minDesiredWidth = Math.max(size, minWidth);
          if (minDesiredWidth != width) {
          newSizeForSon(null);
          }
          setValue(s);

          if (tdp.actionOnDoubleClick(this)) {
          return true;
          } else {
          JOptionPane.showMessageDialog(frame,
          "Could not change the name of the Requirement: this name is already in use",
          "Error",
          JOptionPane.INFORMATION_MESSAGE);
          setValue(oldValue);
          }
          }
          return false;
          }

          return editAttributes();*/

    }


    public void rescale(double scaleFactor){
        dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
        lineHeight = (int)(dlineHeight);
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

    public boolean addProperty(Property p) {
        properties.add(p);
        return true;
    }



    /*public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {

        componentMenu.addSeparator();

        diagramReference = new JMenuItem("Add diagram reference");
        diagramReference.addActionListener(menuAL);

        componentMenu.add(diagramReference);
    }

    public boolean eventOnPopup(ActionEvent e) {
       // String s = e.getActionCommand();

        if (e.getSource() == diagramReference) {
        	// DB: Issue #53: Pass the frame on which the dialog should be centered
            addDiagramReference( getTDiagramPanel().getMGUI().frame );
//        	addDiagramReference(null);
        }

        return true;
    }*/



    /*spublic abstract void makeValidationInfos(SysmlsecMethodologyDiagramName dn);

    public boolean hasSysmlsecMethodologyDiagramName(String s) {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i].getValue().compareTo(s) == 0) {
                return true;
            }
        }
        return false;
    }


    public void fillIgnoredSelectedFromInternalComponents(Vector<String> ignored, Vector<String>selected) {
        // Get from mgui the list of all diagrams with type depends from the subclass
        // If diagrams have the same name -> we do not see the difference

       // TURTLEPanel tp;
        Vector<TURTLEPanel> tabs = getTDiagramPanel().getMGUI().getTabs();
        
        for( final TURTLEPanel panel : tabs) {
          //  tp = (TURTLEPanel)o;
            if (isAValidPanelType( panel )) {
                ignored.add(getTDiagramPanel().getMGUI().getTitleAt( panel ));
            }
        }

        Vector<String> newSelected = new Vector<String>();
        TGComponent tgc;
        //Consider internal components (text) to figure out the ones that are selected
        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof SysmlsecMethodologyDiagramName) {
                newSelected.add(tgc.getValue());
            }
        }

        // Remove from selected the one that do not exist anymore
        Vector<String> toBeRemoved = new Vector<String>();
        boolean found;
        for(String s: newSelected) {
            found = false;
            for(String ss: ignored) {
                if (ss.compareTo(s) == 0) {
                    toBeRemoved.add(ss);
                    found = true;
                }
            }
            if (found) {
                selected.add(s);
            }
        }

        for(String s: toBeRemoved) {
            ignored.remove(s);
        }

    }

    public abstract boolean isAValidPanelType(TURTLEPanel panel);

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof SysmlsecMethodologyDiagramName;
    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        tgc.setFather(this);
        addInternalComponent(tgc, 0);
        //tgc.setDrawingZone(true);
        return true;
    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public abstract boolean makeCall(String diagramName, int index);

    protected boolean openDiagram(String tabName) {
        if (!tdp.getMGUI().selectMainTab(tabName)) {
            TraceManager.addDev("Diagram removed?");
            return false;
        }
        return true;
    }

    protected void giveInformation(String info) {
        tdp.getMGUI().setStatusBarText(info);
    }



    /*public String getDiagramReferences() {
      return referenceElements;
      }*/

}

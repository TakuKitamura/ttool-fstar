/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class SDInstance
 * Fixed duration operator. To be used in sequence diagrams
 * Creation: 04/10/2004
 * @version 1.0 04/10/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import myutil.*;
import ui.*;

public class SDInstance extends TGCWithInternalComponent implements SwallowTGComponent {
    //private int lineLength = 5;
    //private int textX, textY;
    private int spacePt = 10;
    private int wText = 10, hText = 15;
    private int increaseSlice = 250;
    
    public SDInstance(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 10;
        height = 500;
        //textX = 0;
        //textY = 2;
        minWidth = 10;
        maxWidth = 10;
        minHeight = 250;
        maxHeight = 1500;
        
        
        makeTGConnectingPoints();
        //addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        value = "Instance name";
        name = "instance";
        
        myImageIcon = IconManager.imgic500;
        
        
    }
    
    public void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            wText  = g.getFontMetrics().stringWidth(value);
            hText = g.getFontMetrics().getHeight();
        }
        g.drawString(value, x - (wText / 2) + width/2, y - 3);
        g.drawLine(x - (wText / 2) + width/2, y-2, x + (wText / 2) + width/2, y-2);
        g.drawLine(x+(width/2), y, x+(width/2), y +height);
    }
    
    public TGComponent isOnOnlyMe(int _x, int _y) {
        //System.out.println("coucou");
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        //System.out.println("youp");
        if (GraphicLib.isInRectangle(_x, _y, x + (width/2) - (wText/2) , y-hText, wText, hText)) {
            return this;
        }
        //System.out.println("KO");
        return null;
    }
    
    public int getMyCurrentMinX() {
        return Math.min(x + (width/2) - (wText/2), x);

    }
    
    public int getMyCurrentMaxX() {
        return Math.max(x + (width/2) + (wText/2), x + width);
    }
    
    public int getMyCurrentMinY() {
        return Math.min(y-hText, y);
    }
    
    public String getInstanceName() {
        return getValue();
    }
    
    public int getType() {
        return TGComponentManager.SD_INSTANCE;
    }
    
    private void makeTGConnectingPoints() {
        
        nbConnectingPoint = ((height - (2 * spacePt)) / spacePt) + 1;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        
        int yh = spacePt;
        
        for(int i=0; i<nbConnectingPoint; i ++, yh+=spacePt) {
            connectingPoint[i] = new TGConnectingPointMessageSD(this, (width/2), yh, true, true);
        }
        
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
        "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
        null,
        getValue());
        
        if (s != null) {
            s = s.trim();
        }
        
        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            if (!TAttribute.isAValidId(s, false, false)) {
                JOptionPane.showMessageDialog(frame,
                "Could not change the name of the instance: the new name is not a valid name",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            setValue(s);
            return true;
        }
        return false;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //System.out.println("Add swallow component");
        // Choose its position
        int realY = Math.max(y, getY() + spacePt);
        realY = Math.min(realY, getY() + height + spacePt);
        int realX = tgc.getX();
        
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        // Set its coordinates
        if ((tgc instanceof SDAbsoluteTimeConstraint) || (tgc instanceof SDRelativeTimeConstraint) || (tgc instanceof SDTimeInterval)){
            realX = getX() + (width/2) - tgc.getWidth();
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //tgc.setCdRectangle(0, -50, 0, 50);
            tgc.setCd(realX, realY);
        }

        if ((tgc instanceof SDActionState) || (tgc instanceof SDCoregion)|| (tgc instanceof SDGuard)) {
            realX = getX()+(width/2);
            //tgc.setCdRectangle((width/2), (width/2), spacePt, height-spacePt-tgc.getHeight());
            tgc.setCd(realX, realY);
        }
        
        if (tgc instanceof SDTimerSetting) {
            realX = getX()+(width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }
        
        if (tgc instanceof SDTimerExpiration) {
            realX = getX()+(width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }
        
        if (tgc instanceof SDTimerCancellation) {
            realX = getX()+(width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2;
            //tgc.setCdRectangle((width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
            tgc.setCd(realX, realY);
        }
        
        setCDRectangleOfSwallowed(tgc);
        
        // coregions -> in the middle !
        
        // else unknown
        
        //add it
        addInternalComponent(tgc, 0);
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    public boolean isInCoregion(int yy) {
        TGComponent tgc;
        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof SDCoregion) {
                //System.out.println("Coregion found from " + tgc.getY() + " to " + (tgc.getY() + tgc.getHeight()));
                if (tgc.isOnMe(tgc.getX(), yy) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isInSameCoregion(int y1, int y2) {
        //System.out.println("Is in same coregion y1=" + y1 + " y2=" + y2);
        int y11 = Math.min(y1, y2);
        int y22 = Math.max(y1, y2) +1;
        for(int i=y11; i<y22; i++) {
            if (!isInCoregion(i)) {
                //System.out.println("No!");
                return false;
            }
        }
        //System.out.println("YES !");
        return true;
    }
    
    public boolean inSameCoregion(TGComponent tgc1, TGComponent tgc2) {
        TGComponent tgctmp;
        if (tgc2.getY() < tgc1.getY()) {
            tgctmp = tgc1;
            tgc1 = tgc2;
            tgc2 = tgctmp;
        }
        
        // each y between the two components should be in a coregion
        for(int i=tgc1.getY(); i<tgc2.getY()+1; i++) {
            if (!isInCoregion(i)) {
                return false;
            }
        }
        
        return true;
        
    }
    
    // previous in the sense of with y the closer and before
    public TGComponent getPreviousTGComponent(TGComponent tgcToAnalyse) {
        int close = Integer.MAX_VALUE;
        TGComponent tgc;
        TGComponent tgcfound = null;
        int diff;
        
        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc != tgcToAnalyse) {
                diff = tgcToAnalyse.getY() - tgc.getY();
                if ((diff > 0) && (diff < close)) {
                    if (!inSameCoregion(tgcToAnalyse, tgc)) {
                        close = diff;
                        tgcfound = tgc;
                    }
                    
                }
            }
        }
        
        return tgcfound;
    }
    
    public TGComponent getTGComponentActionCloserTo(TGComponent tgc) {
        /* action : message send, message receive, other ? */
        /* timers ? */
        /* now: only message! */
        
        return ((SequenceDiagramPanel)tdp).messageActionCloserTo(tgc, this);
        
    }
    
     public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
         componentMenu.addSeparator();
         
         JMenuItem decrease = new JMenuItem("Decrease size");
         decrease.addActionListener(menuAL);
         componentMenu.add(decrease);
         decrease.setEnabled(canDecreaseSize());
         JMenuItem increase = new JMenuItem("Increase size");
         increase.addActionListener(menuAL);
         componentMenu.add(increase);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        if ((e.getActionCommand().compareTo("Decrease size")) == 0) {
            decreaseSize();
        } else {
            increaseSize();
        }
        return true;
    }
    
    public void updateMinMaxSize() {
        minHeight = 250;
        int i;
        
        for(i=0; i<connectingPoint.length ; i++) {
            if (!connectingPoint[i].isFree()) {
                minHeight = Math.max(minHeight, connectingPoint[i].getY() - y);
            }
        } 
        
        for(i=0; i<nbInternalTGComponent ; i++) {
            minHeight = Math.max(minHeight, tgcomponent[i].getY() + tgcomponent[i].getHeight()- y);
        }
    }
    
    public boolean canDecreaseSize() {
        if (height <= increaseSlice) {
            return false;
        }
        
        int newNbConnectingPoint = (((height-increaseSlice) - (2 * spacePt)) / spacePt) + 1;
        int i;
        
        for(i=newNbConnectingPoint; i<connectingPoint.length ; i++) {
            if (!connectingPoint[i].isFree()) {
                //System.out.println("Cannot reduce size because of a connecting point");
                return false;
            }
        } 
        
        //SwallowedComponents
        for(i=0; i<nbInternalTGComponent ; i++) {
            //System.out.println("tgcomponent =" + tgc + "
            if ((tgcomponent[i].getY() + tgcomponent[i].getHeight()) > (getY() + getHeight() - increaseSlice)) {
                //System.out.println("Cannot reduce size because of a swallowed component");
                return false;
            }
        }
        
        return true;
    }
    
    public void decreaseSize() {
        //System.out.println("Decrease size");
        //Check whether it is possible or not (swallowed components and tgconnecting points used
        if (!canDecreaseSize()) {
            return;
        }
        // new nb of connectingPoints
 
        // If ok, do the modification
        height = height - increaseSlice;
        hasBeenResized();
    }
    
    public void increaseSize() {
        //System.out.println("Increase size");
        height = height + increaseSlice;
        hasBeenResized();
    }
    
     public void hasBeenResized(){
         int i;
         
        TGConnectingPoint [] connectingPointTmp = connectingPoint;
        makeTGConnectingPoints();
        for(i=0; i<Math.min(connectingPointTmp.length, connectingPoint.length) ; i++) {
            connectingPoint[i] = connectingPointTmp[i];
        } 
        
        // Increase tdp if necessary?
        
        // Reposition each swallowed component
        for(i=0; i<nbInternalTGComponent ; i++) {
           setCDRectangleOfSwallowed(tgcomponent[i]);
        }
     }
     
     private void setCDRectangleOfSwallowed(TGComponent tgc) {
      if ((tgc instanceof SDAbsoluteTimeConstraint) || (tgc instanceof SDRelativeTimeConstraint)){
            tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
        }
        
        if ((tgc instanceof SDActionState) || (tgc instanceof SDGuard) || (tgc instanceof SDCoregion) || (tgc instanceof SDTimeInterval)) {
            tgc.setCdRectangle((width/2), (width/2), spacePt, height-spacePt-tgc.getHeight());
        }
        
        if (tgc instanceof SDTimerSetting) {
            tgc.setCdRectangle((width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerSetting)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
        }
        
        if (tgc instanceof SDTimerExpiration) {
            tgc.setCdRectangle((width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerExpiration)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
        }
        
        if (tgc instanceof SDTimerCancellation) {
            tgc.setCdRectangle((width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, (width/2) + ((SDTimerCancellation)tgc).getLineLength() - tgc.getWidth()/2, spacePt - tgc.getHeight()/2, height-spacePt-tgc.getHeight() / 2);
        }    
     }
    
}
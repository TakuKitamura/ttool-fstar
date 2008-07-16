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
 * Class IODRefSD
 * Reference to an SD in an interation overviw diagram
 * Creation: 30/09/2004
 * @version 1.0 30/09/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui.iod;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public class IODRefSD extends TGCOneLineText {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    
    public IODRefSD(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 35;
        minWidth = 70;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointIOD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointIOD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "Scenario";
        name = "Reference to a SD";
        
        myImageIcon = IconManager.imgic400;
    }
    
    public void internalDrawing(Graphics g) {
        //int w2 = g.getFontMetrics().stringWidth("ref");
        int w  = g.getFontMetrics().stringWidth(value) /*+ w2*/;
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
        
        g.drawRect(x, y, width, height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        g.drawString(value, x + (width - w) / 2, y + textY + 15);
        g.drawString("sd", x+3, y+12);
        g.drawLine(x, y+15, x+15, y+15);
        g.drawLine(x+25, y, x+25, y+8);
        g.drawLine(x+15, y+15, x+25, y+8);
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
		
		if ((int)(Line2D.ptSegDistSq(x+(width/2), y - lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
		
        return null;
    }
    
    public String getAction() {
        return value;
    }
    
    
    public int getType() {
        return TGComponentManager.IOD_REF_SD;
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        boolean b = ((InteractionOverviewDiagramPanel)tdp).isSDCreated(value);
        JMenuItem isSDCreated;
        
        if (b) { 
            isSDCreated = new JMenuItem("Open diagram");
        } else {
            isSDCreated = new JMenuItem("Create sequence diagram");
        }
        
        isSDCreated.addActionListener(menuAL);
        componentMenu.add(isSDCreated);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        boolean b = ((InteractionOverviewDiagramPanel)tdp).isSDCreated(value);
        if (b) {
            ((InteractionOverviewDiagramPanel)tdp).openSequenceDiagram(value);
        } else {
            ((InteractionOverviewDiagramPanel)tdp).createSequenceDiagram(value);
        }   
        return true;
    }
	
	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_INTERACTION;
    }
}
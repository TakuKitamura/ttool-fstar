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
 * Class TMLCPRefSD
 * Reference to an SD in a communication pattern diagram
 * Creation: 17/02/2014
 * @version 1.0 17/02/2014
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcp;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

import myutil.*;
import ui.*;

import ui.tmlsd.*;

public class TMLCPRefSD extends TGCOneLineText {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
		private TMLSDPanel refToSD;
//		private TGConnectorTMLCP[] connectors = new TGConnectorTMLCP[2];
		//private int index = 0;

    public TMLCPRefSD(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 35;
        minWidth = 70;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 0.5, 1.0);
        
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        value = "Reference to a SD";
        name = "SequenceDiagram";
				refToSD = null;

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
		
		Color c = g.getColor();
		g.setColor(ColorManager.SD_REFERENCE);
		g.drawRect(x+1, y+1, width, height);
		g.setColor(c);
        
        g.drawRect(x, y, width, height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        
        g.drawString(name, x + (width - w) / 2, y + textY + 15);
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
        return TGComponentManager.TMLCP_REF_SD;
    }
    
    public void addActionToPopupMenu( JPopupMenu componentMenu, ActionListener menuAL, int x, int y ) {
			
			componentMenu.addSeparator();
      boolean b = ((TMLCPPanel)tdp).isTMLCPSDCreated( name );
      JMenuItem isSDCreated;
        
      if( b )	{ 
				isSDCreated = new JMenuItem("Open diagram");
			}
			else	{
				isSDCreated = new JMenuItem( "Create Sequence Diagram" );
      }
        
      isSDCreated.addActionListener( menuAL );
      componentMenu.add( isSDCreated );
    }
    
    public boolean eventOnPopup(ActionEvent e) {

			boolean b = ((TMLCPPanel)tdp).isTMLCPSDCreated( name );
      if( b )	{
				( (TMLCPPanel)tdp ).openTMLCPSequenceDiagram( name );
      }
			else {
				( (TMLCPPanel)tdp ).createTMLCPSequenceDiagram( name );
			}   
      return true;
    }
	
	public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_TMLCP;
    }

	/*public void setReferenceToSD( TMLSDPanel _panel )	{
		refToSD = _panel;
	}*/

	@Override public boolean editOndoubleClick(JFrame frame) {
		
		//System.out.println("Double click");
     String text = "Reference to a SD: ";
    if( hasFather() ) {
			text = getTopLevelName() + " / " + text;
    }
    String s = (String) JOptionPane.showInputDialog(frame, text,
		"Setting Name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic100, null, getName() );
    if( (s != null) && (s.length() > 0) )	{
			setName(s);
			return true;
		}
    return false;
    }

}	//End of Class

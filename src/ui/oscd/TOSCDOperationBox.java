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
 * Class TOSCDOperationBox
 * Box for storing the operations of a Tclass
 * To be used in class diagrams
 * Creation: 03/10/2006
 * @version 1.0 03/10/2006
 * @author Ludovic APVRILLE
 * @see 
 */
 
package ui.oscd;

import java.awt.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public class TOSCDOperationBox extends TGCWithoutInternalComponent {
	public String oldValue;
	protected int textX = 5;
	protected int textY = 20;

	public TOSCDOperationBox(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		width = 150; height = 30;
		minWidth = 150; minHeight = 30; 
		minDesiredWidth = 150;
		minDesiredHeight = 30;

		nbConnectingPoint = 2;
		connectingPoint = new TGConnectingPoint[2];
		connectingPoint[0] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 0.0, 0.5);
		connectingPoint[1] = new TGConnectingPointTOSClasses(this, 0, 0, true, true, 1.0, 0.5);
                
                addTGConnectingPointsCommentCorner();
		
		moveable = false;
		editable = true;
		removable = false;
	
		name = "Tclass operations";
		value = "";
		
		myImageIcon = IconManager.imgic122;
	}

	public void internalDrawing(Graphics g) {
		g.drawRect(x, y, width, height);
		g.setColor(ColorManager.OPERATION_BOX);
		g.fillRect(x+1, y+1, width-1, height-1);
		ColorManager.setColor(g, getState(), 0);
                if (value.length() > 0) {
                    g.drawString(value, x + textX, y + textY);
                }
	}

	public boolean editOndoubleClick(JFrame frame) {
		oldValue = value;
		String text = getName() + ": ";
		if (hasFather()) {
			text = getTopLevelName() + " / " + text;
		} 
		String s = (String)JOptionPane.showInputDialog(frame, text,
					"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                    null,
                    getValue());
		if ((s != null) && (!s.equals(oldValue))) {
				setValue(s);
		}
		return false;
	}
		

	public TGComponent isOnMe(int x1, int y1) {
		if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
			return this;
		}
		return null;
	}

}
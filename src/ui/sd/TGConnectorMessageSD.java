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
* Class TGConnectorMessageSD
* Connector used in SD for exchanging messages between instances
* Creation: 04/10/2004
* @version 1.0 04/10/2004
* @author Ludovic APVRILLE
* @see
*/

package ui.sd;



//import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import myutil.*;
import ui.*;

public  abstract class TGConnectorMessageSD extends TGConnector {
    protected int arrowLength = 10;
    protected int widthValue, heightValue;
    
    public TGConnectorMessageSD(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "msg?";
        editable = true;
    }
    
    public String getMessage() {
        return value;
    }
	
	// Part before '()' section
	public String getFirstPartMessage() {
		int index0 = value.indexOf('(');
			if (index0 == -1) {
				return value;
			} else {
				return value.substring(0, index0);
			}		
	}
	
	public String getSecondPartMessage() {
		String tmp = value.trim();
		int index0 = tmp.indexOf('(');
			if (index0 == -1) {
				return "";
			} else {
				return tmp.substring(index0, tmp.length());
			}		
	}
	
	public boolean isMessageWellFormed() {
		//System.out.println("Analyzing message:" + value);
		
		int index0 = value.indexOf('(');
		String name;
		
		if (index0 == -1) {
			name = value;
		} else {
			name = value.substring(0, index0);
		}
		
		if (!TAttribute.isAValidId(name, false, false)) {
			return false;
		}
			
		if (index0 == -1) {
			return true;
		}
		
		String tmp = value.trim();
		if (!tmp.endsWith(")")) {
			return false;
		}
		
		// Check for individual parameters
		index0 = tmp.indexOf('(');
		tmp = tmp.substring(index0+1, tmp.length()-1);
		
		String[] params = tmp.split(",");
		for(int i=0; i<params.length; i++) {
			tmp = params[i].trim();
			//System.out.println("First=" + tmp);
			if (!TAttribute.isAValidId(tmp, false, false)) {
				return false;
			}
		}
		
		return true;
	}
	
	
    
    public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click");
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String)JOptionPane.showInputDialog(frame, text,
			"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
			null,
			getValue());
        if ((s != null) && (s.length() > 0)) {
            setValue(s);
            return true;
        }
        return false;
    }
    
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {  
        //System.out.println("Extra");
        if (GraphicLib.isInRectangle(x1, y1, ((p1.getX() + p2.getX()) / 2)-widthValue/2, ((p1.getY() + p2.getY()) / 2) - 5 - heightValue, widthValue, heightValue)) {
            return this;
        }
        return null;
    }
}








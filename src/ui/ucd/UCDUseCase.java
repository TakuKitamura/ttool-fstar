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
 * Class UCDUseCase
 * Action state of a sequence diagram
 * Creation: 18/02/2005
 * @version 1.0 18/02/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ucd;

import java.awt.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;

import ui.window.*;

public class UCDUseCase extends TGCWithoutInternalComponent {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int w, h, w1, w2; //w1;
    
    protected String extension = "";
    
    public UCDUseCase(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 50;
        height = 40;
        //minWidth = 30;
        
        nbConnectingPoint = 16;
        int index = 0;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        while(index < 16) {
            connectingPoint[index] = new TGConnectingPointUCD(this, 0, 0, true, true, 0.5, 0.0);
            index ++;
            connectingPoint[index] = new TGConnectingPointUCD(this, 0, 0, true, true, 0.0, 0.5);
            index ++;
            connectingPoint[index] = new TGConnectingPointUCD(this, 0, 0, true, true, 1.0, 0.5);
            index ++;
            connectingPoint[index] = new TGConnectingPointUCD(this, 0, 0, true, true, 0.5, 1.0);
            index ++;
        }
        addTGConnectingPointsComment();
           
        moveable = true;
        editable = true;
        removable = true;
        
        value = "My use case";
        name = "Use case";
        
        myImageIcon = IconManager.imgic602;
    }
    
    public void internalDrawing(Graphics g) {
		if (extension.length() > 0) {
			w1  = g.getFontMetrics().stringWidth(value);
			w2 = g.getFontMetrics().stringWidth(extension);
			w = Math.max(w1, w2);
		} else {
			w = g.getFontMetrics().stringWidth(value);
		}
        h = g.getFontMetrics().getHeight();
        
        // Size has changed?
        int w3 = Math.max(minWidth, w + 2 * textX);
        if ((w3 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w3/2, y);
            width = w3;
            //updateConnectingPoints();
        }
        
        g.drawOval(x, y, width, height);
        //name of use case
        
        if (extension.length() > 0) {
			g.drawString(value, x + width / 2 - w1 / 2 , y + height/2 + h / 2 - 8);
	        g.drawLine(x + width / 2 - w / 2, y + height/2 + h / 2 -6, x + width / 2 + w / 2, y + height/2 + h / 2 - 6);
	        g.drawString(extension, x + width / 2 - w2 / 2 , y + height/2 + h / 2 + 5);
        } else {
			g.drawString(value, x + width / 2 - w / 2 , y + height/2 + h / 2 - 3);
		}
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
 
    public String getUCDName() {
        return value;
    }
	
	 public String getUCDExtension() {
        return extension;
    }
    
 
    public int getType() {
        return TGComponentManager.UCD_USECASE;
    } 
    
    public boolean editOndoubleClick(JFrame frame) {
        boolean error = false;
		String errors = "";
		int tmp;
		String tmpName;
        
		JDialogUseCase dialog = new JDialogUseCase(frame, "Setting Use Case attributes", value, extension);
		dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getName().length() != 0) {
			tmpName = dialog.getName();
			value = tmpName.trim();
		}
		
		tmpName = dialog.getExtension();
		extension = tmpName.trim();
		
        return true;
    }
	
	 protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info extension=\"" + extension + "\" ");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
	 public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
            String sextension = null;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sextension = elt.getAttribute("extension");
                            }
                            if (sextension != null) {
                                extension = sextension;
                            } 
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
}
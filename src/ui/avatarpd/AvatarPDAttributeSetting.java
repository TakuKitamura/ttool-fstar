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
 * Class AvatarPDAttributeSetting
 * Setting attributes in SysML Parametric diagrams, for attributes only
 * Creation: 23/04/2010
 * @version 1.0 23/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarpd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class AvatarPDAttributeSetting extends TGCScalableWithInternalComponent implements ConstraintListInterface {
    private int textY1 = 5;
    //private int textY2 = 30;
	
	public static final String[] STEREOTYPES = {"<<setting>>"}; 
	
    protected String oldValue = "";
	
	private int maxFontSize = 12;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private int textX = 1;
    
    public AvatarPDAttributeSetting(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = (int)(125* tdp.getZoom());
        height = (int)(40 * tdp.getZoom());
        minWidth = 100;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarPDAttributeConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        //addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "c = a + b";
		
		currentFontSize = maxFontSize;
		oldScaleFactor = tdp.getZoom();
        
        myImageIcon = IconManager.imgic1078;
    }
    
     public void internalDrawing(Graphics g) {
		String ster;
		ster = STEREOTYPES[0];
		Font f = g.getFont();
		Font fold = f;
		
		if (value != oldValue) {
			setValue(value, g);
		}
		
		if ((rescaled) && (!tdp.isScaled())) {
			
			if (currentFontSize == -1) {
				currentFontSize = f.getSize();
			}
			rescaled = false;
			// Must set the font size ..
			// Find the biggest font not greater than max_font size
			// By Increment of 1
			// Or decrement of 1
			// If font is less than 4, no text is displayed
			
			int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
			int w0, w1, w2;
			f = f.deriveFont((float)maxCurrentFontSize);
			g.setFont(f);
			//System.out.println("max current font size:" + maxCurrentFontSize);
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
			}
			
		}
		
        Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
	
		g.setColor(ColorManager.AVATARPD_ATTRIBUTE_SETTING);
		
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
        // Strings
		int w;
		if (displayText) {
			f = f.deriveFont((float)currentFontSize);
			Font f0 = g.getFont();
			
			boolean cannotWriteAttack = (height < (2 * currentFontSize + (int)(textY1 * tdp.getZoom())));
			
			if (cannotWriteAttack) {
				w  = g.getFontMetrics().stringWidth(value);
				int h =  currentFontSize + (int)(textY1 * tdp.getZoom());
				if ((w < (2*textX + width)) && (h < height)) {
					g.drawString(value, x + (width - w)/2, y + h);
				} else {
					w  = g.getFontMetrics().stringWidth(ster);
					if ((w < (2*textX + width)) && (h < height)) {
						g.drawString(ster, x + (width - w)/2, y + h);
					}
				}
			} else {
				g.setFont(f.deriveFont(Font.BOLD));
				int h =  currentFontSize + (int)(textY1 * tdp.getZoom());
				int cumulated = 0;
				w = g.getFontMetrics().stringWidth(ster);
				if ((w < (2*textX + width)) && (h < height)) {
					g.drawString(ster, x + (width - w)/2, y + h);
					cumulated = h;
				}
				g.setFont(f0);
				w  = g.getFontMetrics().stringWidth(value);
				h = cumulated + currentFontSize + (int)(textY1 * tdp.getZoom());
				if ((w < (2*textX + width)) && (h < height)) {
					g.drawString(value, x + (width - w)/2, y + h);
				}
			}
		}
		
		g.setFont(fold);
        
    }
    
   public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
		int w1 = Math.max((int)(minWidth*tdp.getZoom()), w + 2 * textX);
		
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }
    
     public boolean editOndoubleClick(JFrame frame) {
		String tmp;
		boolean error = false;
		
		//String text = getName() + ": ";
		String s = (String)JOptionPane.showInputDialog(frame, "Attribute name:",
			"Setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
			null,
			getValue());
		
		if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
			//boolean b;
			setValue(s);
			rescaled = true;
			return true;
		}
		return false;
		
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.APD_ATTRIBUTE_SETTING;
    }
	
	public String[] getConstraintList() {
		return STEREOTYPES;
	}
	
	public String getCurrentConstraint() {
		return value;
	}
  
    
}

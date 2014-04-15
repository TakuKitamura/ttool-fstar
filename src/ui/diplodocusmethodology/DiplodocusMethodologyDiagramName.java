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
 * Class DiplodocusMethodologyDiagramName
 * Internal component that shows the diagram name and validation/simu
 * references
 * Creation: 31/03/2014
 * @version 1.0 31/03/2014
 * @author Ludovic APVRILLE
 * @see
 */

package ui.diplodocusmethodology;

import java.awt.*;
//import java.awt.geom.*;
import javax.swing.*;

import ui.*;
import myutil.*;

public class DiplodocusMethodologyDiagramName extends TGCScalableWithoutInternalComponent implements SwallowedTGComponent { 
    //protected boolean emptyText;
    
    public final static int X_MARGIN = 5;
    public final static int Y_MARGIN = 3;
    
    
    protected final static String SIM = "sim";
    protected final static String UPP = "upp";
    protected final static String LOT = "lot";
	protected final static String TML = "tml";
	
	protected String[] validations;
	protected int[] valMinX;
	protected int[] valMaxX;
	
	protected int indexOnMe; // -1 -> on main element. -2: on not precise element; Other: on a validations item.
	
	private int myWidth, myHeight, widthAppli;
	
	
    public DiplodocusMethodologyDiagramName(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        nbConnectingPoint = 0;
        minWidth = 10;
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = false;
        
        name = "value ";
        
        
        
        initScaling(10, 10);
        
        
        myImageIcon = IconManager.imgic302;
    }
    
    public void internalDrawing(Graphics g) {
    	boolean onMe = false;
    	
    	if (tdp.componentPointed() == this) {
    		onMe = true;
    	}
    	
    	if ((y+Y_MARGIN) > (getFather().getY()+getFather().getHeight())) {
    		return;
    	}
    	
    	//TraceManager.addDev("Internal drawing ...");
    	int currentMaxX;
    	String val = value;
        int w = g.getFontMetrics().stringWidth(value);
        int wf = getFather().getWidth();
        int w1;
        int saveCurrentMaxX;
        boolean oneWritten;
        
        if (wf < w+(2*X_MARGIN)) {
        	val = ".";
        }
        
        widthAppli = g.getFontMetrics().stringWidth(val);
        
        Font f = g.getFont();
        
        if (onMe && indexOnMe == -1) {
        	g.setFont(f.deriveFont(Font.BOLD));
        }
        g.drawString(val, x, y);
        g.setFont(f);
        
        if (validations == null) {
        	if (getFather() instanceof DiplodocusMethodologyDiagramReference) {
        		((DiplodocusMethodologyDiagramReference)(getFather())).makeValidationInfos(this);
        	}
        }
        
        if ((validations != null) && (valMinX == null)) {
        	valMinX = new int[validations.length];
        	valMaxX = new int[validations.length];
        }
        
        /*if (validations == null) {
        TraceManager.addDev("null validation");
        } else {
        TraceManager.addDev("Validation size=" + validations.length);
        }*/
        
        currentMaxX = wf + x - 2*(X_MARGIN);
        saveCurrentMaxX = currentMaxX;
        
        if (wf < w+(2*X_MARGIN)) {
        	makeScale(g, w);
        	return;
        }
        
        //TraceManager.addDev("Tracing validation Validation size=" + validations.length);
        oneWritten = false;
        
        g.setFont(f.deriveFont(Font.ITALIC));
        if ((validations != null) & (validations.length >0)) {
			for(int i=validations.length-1; i>=0; i--) {
				//TraceManager.addDev("Validations[" + i + "] = " + validations[i]);
				w1 = g.getFontMetrics().stringWidth(validations[i]);
				
				if ((currentMaxX - w1) > (x + w)) {
					if ((onMe && indexOnMe == i)) {
						g.setFont(f.deriveFont(Font.BOLD));
					}
					g.drawString(validations[i], currentMaxX - w1, y);
					g.setFont(f.deriveFont(Font.ITALIC));
					valMinX[i] = currentMaxX-w1;
					valMaxX[i] = currentMaxX;
					oneWritten = true;
					currentMaxX = currentMaxX - w1 - 5;
				} else {
					break;
				}
				
			}
        }
        
        
        
        g.setFont(f);
        
        if (oneWritten) {
        	makeScale(g, saveCurrentMaxX - x);
        } else {
        	makeScale(g, w);
        }
        
        if (onMe)
        	g.drawRect(x-2, y-12, myWidth+4, 15);
        
        return;
        
        
    }
    
    private void makeScale(Graphics g, int _size) {
    	if (!tdp.isScaled()) {
            myWidth = _size;
            myHeight = g.getFontMetrics().getHeight();
        }
    }
    
    
    public TGComponent isOnMe(int _x, int _y) {
    	int oldIndex = indexOnMe;
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(myWidth, minWidth), myHeight)) {
        	indexOnMe = -2;
        	
        	if (_x <= (x+widthAppli)) {
        		indexOnMe = -1;
        	}
        	if ((validations != null) && (validations.length > 0)) {
        		for(int i=0; i<validations.length; i++) {
        			if ((_x >= valMinX[i]) && (_x <= valMaxX[i])) {
        				indexOnMe = i;
        				//TraceManager.addDev("Index on " + indexOnMe);
        				break;
        			}
        		}
        	}
        	
        	if (oldIndex != indexOnMe) {
        		tdp.repaint();
        	}
        	
        	
            return this;
        }
        return null;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
    	
        if (indexOnMe > -2) {
        	// Opening the diagram
        	if (!tdp.getMGUI().selectMainTab(value)) {
        		TraceManager.addDev("Diagram removed?");
        		return false;
        	}
        	
        }
        
        
        if (indexOnMe > -1) {
        	DiplodocusMethodologyDiagramReference ref = ((DiplodocusMethodologyDiagramReference)(getFather()));
        	ref.makeCall(indexOnMe);
        }
        
         
        return true;
    }
    
    
    public  int getType() {
        return TGComponentManager.DIPLODODUSMETHODOLOGY_DIAGRAM_NAME;
    }
    
   	public int getDefaultConnector() {
      return TGComponentManager.DIPLODOCUSMETHODOLOGY_CONNECTOR;
    }
    
    public void setValidationsNumber(int size) {
    	validations = new String[size];
    }
    
    public void setValidationsInfo(int index, String s) {
    	validations[index] = s;
    }
    
    public void rescale(double scaleFactor){
		
		if ((valMinX != null) && (valMinX.length > 0)) {
			for(int i=0; i<valMinX.length; i++) {
				valMinX[i] = (int)(valMinX[i] / oldScaleFactor * scaleFactor);
				valMaxX[i] = (int)(valMaxX[i] / oldScaleFactor * scaleFactor);
			}
		}
		
		super.rescale(scaleFactor);
	}
}

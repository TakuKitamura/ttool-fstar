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
* Class AvatarADActivity
* State. To be used in AVATAR Actity Diagrams
* Creation: 02/09/2011
* @version 1.1 02/09/2011
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarad;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;


public class AvatarADActivity extends TGCScalableWithInternalComponent implements CheckableAccessibility, SwallowTGComponent, SwallowedTGComponent {
    private int textY1 = 3;
	
	private int maxFontSize = 12;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private int textX = 7;
	
	
	public String oldValue;
    
    public AvatarADActivity(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 100;
        height = 50;
        minWidth = 40;
        minHeight = 30;
        
        nbConnectingPoint = 32;
        connectingPoint = new TGConnectingPoint[32];
        
        connectingPoint[0] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
		
		connectingPoint[16] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.12, 0.0);
		connectingPoint[17] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.37, 0.0);
		
        connectingPoint[18] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.62, 0.0);
		connectingPoint[19] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.87, 0.0);
		
        connectingPoint[20] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.12);
		connectingPoint[21] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.37);
		
        connectingPoint[22] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.12);
		connectingPoint[23] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.37);
		
        connectingPoint[24] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.62);
		connectingPoint[25] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.0, 0.87);
		
        connectingPoint[26] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.62);
		connectingPoint[27] = new AvatarADConnectingPoint(this, 0, 0, true, true, 1.0, 0.87);
		 
        connectingPoint[28] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.12, 1.0);
		connectingPoint[29] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.37, 1.0);
		
        connectingPoint[30] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.62, 1.0);
		 connectingPoint[31] = new AvatarADConnectingPoint(this, 0, 0, true, true, 0.87, 1.0);

        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
		multieditable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findAvatarADActivityName("activity");
		setValue(name);
		oldValue = value;
		
		currentFontSize = maxFontSize;
		oldScaleFactor = tdp.getZoom();
        
        myImageIcon = IconManager.imgic700;
		
		//actionOnAdd();
    }
    
    public void internalDrawing(Graphics g) {
		Font f = g.getFont();
		Font fold = f;
		
		//System.out.println("width=" + width + " height=" + height);
		
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
				if (w0 < (width - (2*textX))) {
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
		
		//System.out.println("Current font size:" + currentFontSize);
		
		//Color c = g.getColor();
		//g.setColor(ColorManager.AVATAR_STATE);
		//Color avat = ColorManager.AVATAR_STATE;
		//g.setColor(new Color(avat.getRed(), avat.getGreen(), avat.getBlue() + (getMyDepth() * 10)));
		//g.fillRoundRect(x, y, width, height, 5, 5);
		//g.setColor(c);
		g.drawRect(x, y, width, height);
		int decY = 15;
		int decX = 20;
		if ((height > decY) && (width > decX)) {
			g.drawLine(x, y+decY, x+decY, y+decY);
			g.drawLine(x+decY, y+decY, x+decX, y+decY-5);
			g.drawLine(x+decX, y+decY-5, x+decX, y);
			g.drawString("act", x+1, y+decY-2);
		}
		//g.drawRoundRect(x, y, width, height, 5, 5);
		
		
        
        // Strings
		int w;
		int h = 0;
		if (displayText) {
			f = f.deriveFont((float)currentFontSize);
			Font f0 = g.getFont();
			g.setFont(f.deriveFont(Font.BOLD));
			
			w = g.getFontMetrics().stringWidth(value);
			h =  currentFontSize + (int)(textY1 * tdp.getZoom());
			if ((w < (2*textX + width)) && (h < height)) {
				g.drawString(value, x + (width - w)/2, y +h);
			}
			g.setFont(f0);
		}
		
		g.setFont(fold);
		
		
		g.setFont(fold);
    }
	
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    
    public String getStateName() {
        return value;
    }
    
	public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		
		oldValue = value;
		
		//String text = getName() + ": ";
		String s = (String)JOptionPane.showInputDialog(frame, "Activity name",
			"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
			null,
			getValue());
		
		if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
			//boolean b;
			if (!TAttribute.isAValidId(s, false, false)) {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the activity: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			setValue(s);
			recalculateSize();
		}
		return true;
		
		
    }
    
    
    public int getType() {
        return TGComponentManager.AAD_ACTIVITY;
    }
	
	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		if (tgc instanceof AvatarADBasicComponent) {
			return true;
		}
		
		if (tgc instanceof AvatarADActivity) {
			return true;
		}
		
		return false;
	}
    
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		boolean swallowed = false;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SwallowTGComponent) {
				if (tgcomponent[i].isOnMe(x, y) != null) {
					swallowed = true;
					((SwallowTGComponent)tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
					break;
				}
			}
        }
		
		if (swallowed) {
			return true;
		}
		
		if (!acceptSwallowedTGComponent(tgc)) {
			return false;
		}
		
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof AvatarADBasicComponent) {
            ((AvatarADBasicComponent)tgc).resizeWithFather();
        }
        
        if (tgc instanceof AvatarADChoice) {
            ((AvatarADChoice)tgc).resizeWithFather();
        }
		
		if (tgc instanceof AvatarADActivity) {
            ((AvatarADActivity)tgc).resizeWithFather();
        }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
		
		return true;
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
		removeMyInternalComponent(tgc, false);
	}
	
	public boolean removeMyInternalComponent(TGComponent tgc, boolean actionOnRemove) {
        //TGComponent tgc;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
                nbInternalTGComponent = nbInternalTGComponent - 1;
                if (nbInternalTGComponent == 0) {
                    tgcomponent = null;
                } else {
                    TGComponent [] tgcomponentbis = new TGComponent[nbInternalTGComponent];
                    for(int j=0; j<nbInternalTGComponent; j++) {
                        if (j<i) {
                            tgcomponentbis[j] = tgcomponent[j];
                        }
                        if (j>=i) {
                            tgcomponentbis[j] = tgcomponent[j+1];
                        }
                    }
                    tgcomponent = tgcomponentbis;
                }
				if (actionOnRemove) {
					tgc.actionOnRemove();
					tdp.actionOnRemove(tgc);
				}
                return true;
            } else {
            	if (tgcomponent[i] instanceof AvatarADActivity) {
					if (((AvatarADActivity)tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
						return true;
					}
				}
            }
        }
        return false;
    }
	
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarADBasicComponent) {
                ((AvatarADBasicComponent)tgcomponent[i]).resizeWithFather();
            }
            if (tgcomponent[i] instanceof AvatarADChoice) {
                ((AvatarADChoice)tgcomponent[i]).resizeWithFather();
            }
            if (tgcomponent[i] instanceof AvatarADActivity) {
                ((AvatarADActivity)tgcomponent[i]).resizeWithFather();
            }
        }
		
		if (getFather() != null) {
			resizeWithFather();
		}
        
    }
	
	public void resizeWithFather() {
		
		if ((father != null) && (father instanceof AvatarADActivity)) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public LinkedList<AvatarADActivity> getActivityList() {
        LinkedList<AvatarADActivity> list = new LinkedList<AvatarADActivity>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarADActivity) {
                list.add((AvatarADActivity)(tgcomponent[i]));
            }
        }
        return list;
    }
	
	public LinkedList<AvatarADActivity> getFullActivityList() {
        LinkedList<AvatarADActivity> list = new LinkedList<AvatarADActivity>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarADActivity) {
                list.add((AvatarADActivity)(tgcomponent[i]));
				list.addAll(((AvatarADActivity)tgcomponent[i]).getFullActivityList());
            }
        }
        return list;
    }
	
	public boolean hasInternalActivityWithName(String name) {
		LinkedList<AvatarADActivity> list  = getFullActivityList();
		for(AvatarADActivity s: list) {
			if (s.getValue().compareTo(name) ==0) {
				return true;
			}
		}
		return false;
	}
    
   	public int getDefaultConnector() {
        return TGComponentManager.AAD_ASSOCIATION_CONNECTOR;
	}
	
	/*public AvatarSMDState checkForStartStateOfCompositeStates() {
		AvatarSMDState tgc;
		LinkedList<AvatarSMDState> list  = getFullStateList();
		for(AvatarSMDState s: list) {
			tgc = s.checkForStartStateOfCompositeStates();
			if (tgc != null) {
				return tgc;
			}
		}
		
		int cpt = 0;
		for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarSMDStartState) {
				cpt ++;
			}
		}
		
		if (cpt > 1) {
			return this;
		}
		return null;
	}*/
	
	public boolean isACompositeActivity() {
		return (nbInternalTGComponent > 0);
	}
	
    
}

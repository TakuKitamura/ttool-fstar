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
* Class AvatarPDBlock
* Node. To be used in AVATAR Parametric Diagrams
* Creation: 22/04/2010
* @version 1.1 22/04/2010
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


public class AvatarPDBlock extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
    private int textY1 = 3;
    private String stereotype = "block";
	
	private int maxFontSize = 12;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private int textX = 7;
	
	private int limitName = -1;
	private int limitAttr = -1;
	private int limitMethod = -1;
	
	// Icon
	private int iconSize = 15;
	private boolean iconIsDrawn = false;
	
	
	public String oldValue;
    
    public AvatarPDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarPDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
		//multieditable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findAvatarBDBlockName("Block");
		setValue(name);
		oldValue = value;
		
		currentFontSize = maxFontSize;
		oldScaleFactor = tdp.getZoom();
        
        myImageIcon = IconManager.imgic700;
		
		actionOnAdd();
    }
    
    public void internalDrawing(Graphics g) {
		String ster = "<<" + stereotype + ">>";
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
		
		//System.out.println("Current font size:" + currentFontSize);
		
		Color c = g.getColor();
		
		g.draw3DRect(x, y, width, height, true);
		
		//g.setColor(ColorManager.AVATAR_BLOCK);
		Color avat = ColorManager.AVATAR_BLOCK;
		int h;
		h = 2* (currentFontSize + (int)(textY1 * tdp.getZoom())) + 2;
		g.setColor(new Color(avat.getRed(), avat.getGreen(), avat.getBlue() + (getMyDepth() * 10)));
		g.fill3DRect(x+1, y+1, width-1, Math.min(h, height)-1, true);
		g.setColor(c);
        
        // Strings
		int w;
		h = 0;
		if (displayText) {
			f = f.deriveFont((float)currentFontSize);
			Font f0 = g.getFont();
			g.setFont(f.deriveFont(Font.BOLD));
			
			w = g.getFontMetrics().stringWidth(ster);
			h =  currentFontSize + (int)(textY1 * tdp.getZoom());
			if ((w < (2*textX + width)) && (h < height)) {
				g.drawString(ster, x + (width - w)/2, y +h);
			}
			g.setFont(f0);
			w  = g.getFontMetrics().stringWidth(value);
			h = 2* (currentFontSize + (int)(textY1 * tdp.getZoom()));
			if ((w < (2*textX + width)) && (h < height)) {
				g.drawString(value, x + (width - w)/2, y + h);
			}
			limitName = y + h;
		} else {
			limitName = -1;
		}
		
		g.setFont(fold);
		
		h = h +2;
		if (h < height) {
			//g.drawLine(x, y+h, x+width, y+h);
			g.setColor(new Color(avat.getRed(), avat.getGreen(), avat.getBlue() + (getMyDepth() * 10)));
			g.fill3DRect(x+1, y+h, width-1, height-1-h, true);
			g.setColor(c);
		}
		
		// Icon
		if ((width>30) && (height > (iconSize + 2*textX))) {
			iconIsDrawn = true;
			g.drawImage(IconManager.img5100, x + width - iconSize - textX, y + textX, null);
		} else {
			iconIsDrawn = false;
		}
		
		/*int cpt = h;
		// Attributes
		if (((AvatarBDPanel)tdp).areAttributesVisible()) {
		limitAttr = -1;
		int index = 0;
		String attr;
		
		TAttribute a;
		
		int si = Math.min(12, (int)((float)currentFontSize - 2));
		
		f = g.getFont();
		f = f.deriveFont((float)si);
		g.setFont(f);
		int step = si + 2;
		
		while(index < myAttributes.size()) {
		cpt += step ;
		if (cpt >= (height - textX)) {
		break;
		}
		a = (TAttribute)(myAttributes.get(index));
		attr = a.toString();
		w = g.getFontMetrics().stringWidth(attr);
		if ((w + (2 * textX) + 1) < width) {
		g.drawString(attr, x + textX, y + cpt);
		limitAttr = y + cpt;
		} else {
		attr = "...";
		w = g.getFontMetrics().stringWidth(attr);
		if ((w + textX + 2) < width) {
		g.drawString(attr, x + textX + 1, y + cpt);
		limitAttr = y + cpt;
		} else {
		// skip attribute
		cpt -= step;
		}
		}
		index ++;
		}
		} else {
		limitAttr = -1;
		}
		
		// Methods
		if (((AvatarBDPanel)tdp).areAttributesVisible()) {
		limitMethod = -1;
		if (myMethods.size() > 0) {
		if (cpt < height) {
		cpt += textY1;
		g.drawLine(x, y+cpt, x+width, y+cpt);
		cpt += textY1;
		}
		}
		
		int index = 0;
		String method;
		AvatarMethod am;
		
		int si = Math.min(12, (int)((float)currentFontSize - 2));
		
		f = g.getFont();
		f = f.deriveFont((float)si);
		g.setFont(f);
		int step = si + 2;
		
		while(index < myMethods.size()) {
		cpt += step ;
		if (cpt >= (height - textX)) {
		break;
		}
		am = (AvatarMethod)(myMethods.get(index));
		method = "- " + am.toString();
		w = g.getFontMetrics().stringWidth(method);
		if ((w + (2 * textX) + 1) < width) {
		g.drawString(method, x + textX, y + cpt);
		limitMethod = y + cpt;
		} else {
		method = "...";
		w = g.getFontMetrics().stringWidth(method);
		if ((w + textX + 2) < width) {
		g.drawString(method, x + textX + 1, y + cpt);
		limitMethod = y + cpt;
		} else {
		// skip attribute
		cpt -= step;
		}
		}
		index ++;
		}
		} else {
		limitMethod = -1;
		}
		
		// Signals
		if (((AvatarBDPanel)tdp).areAttributesVisible()) {
		
		if (mySignals.size() > 0) {
		if (cpt < height) {
		cpt += textY1;
		g.drawLine(x, y+cpt, x+width, y+cpt);
		cpt += textY1;
		}
		}
		
		int index = 0;
		String signal;
		AvatarSignal as;
		
		int si = Math.min(12, (int)((float)currentFontSize - 2));
		
		f = g.getFont();
		f = f.deriveFont((float)si);
		g.setFont(f);
		int step = si + 2;
		
		while(index < mySignals.size()) {
		cpt += step ;
		if (cpt >= (height - textX)) {
		break;
		}
		as = (AvatarSignal)(mySignals.get(index));
		signal = as.toString();
		w = g.getFontMetrics().stringWidth(signal);
		if ((w + (2 * textX) + 1) < width) {
		g.drawString(signal, x + textX, y + cpt);
		} else {
		signal = "...";
		w = g.getFontMetrics().stringWidth(signal);
		if ((w + textX + 2) < width) {
		g.drawString(signal, x + textX + 1, y + cpt);
		} else {
		// skip attribute
		cpt -= step;
		}
		}
		index ++;
		}
		}*/
		
		g.setFont(fold);
		
        /*int w  = g.getFontMetrics().stringWidth(ster);
		Font f = g.getFont();
		g.setFont(f.deriveFont(Font.BOLD));
        g.drawString(ster, x + (width - w)/2, y + textY1);
		g.setFont(f);
        w  = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + (width - w)/2, y + textY2);*/
		
		// Icon
		//g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
		//g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
	
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getStereotype() {
        return stereotype;
        
    }
    
    public String getNodeName() {
        return name;
    }
    
	public boolean editOndoubleClick(JFrame frame) {
		
		oldValue = value;
		
		//String text = getName() + ": ";
		String s = (String)JOptionPane.showInputDialog(frame, "Block name",
			"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
			null,
			getValue());
		
		if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
			//boolean b;
			if (!TAttribute.isAValidId(s, false, false)) {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Block: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if (!tdp.isBlockNameUnique(s)) {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Block: the new name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			setValue(s);
			recalculateSize();
			
			if (tdp.actionOnDoubleClick(this)) {
				return true;
			} else {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Block: this name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				setValue(oldValue);
			}
		}
		return false;
		
    }
    
    
    public int getType() {
        return TGComponentManager.APD_BLOCK;
    }
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
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
			return;
		}
		
        //System.out.println("Add swallow component");
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof AvatarPDBlock) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((AvatarPDBlock)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
		
		if (tgc instanceof AvatarPDAttribute) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((AvatarPDAttribute)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
		
		if (tgc instanceof AvatarPDSignal) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((AvatarPDSignal)tgc).resizeWithFather();
            //tgc.setCdRectangle(0, width - tgc.getWidth(), 0, height - tgc.getHeight());
            //tgc.setCd(x, y);
        }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
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
                if (((AvatarPDBlock)tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
	public String getBlockName() {
		return value;
    }
    
	
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarPDBlock) {
                ((AvatarPDBlock)tgcomponent[i]).resizeWithFather();
            }
			if (tgcomponent[i] instanceof AvatarPDAttribute) {
                ((AvatarPDAttribute)tgcomponent[i]).resizeWithFather();
            }
			if (tgcomponent[i] instanceof AvatarPDSignal) {
                ((AvatarPDSignal)tgcomponent[i]).resizeWithFather();
            }
        }
		
		if (getFather() != null) {
			resizeWithFather();
		}
        
    }
	
	public void resizeWithFather() {
        if ((father != null) && (father instanceof AvatarPDBlock)) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public LinkedList<AvatarPDBlock> getBlockList() {
        LinkedList<AvatarPDBlock> list = new LinkedList<AvatarPDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarPDBlock) {
                list.add((AvatarPDBlock)(tgcomponent[i]));
            }
        }
        return list;
    }
	
	public LinkedList<AvatarPDBlock> getFullBlockList() {
        LinkedList<AvatarPDBlock> list = new LinkedList<AvatarPDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarPDBlock) {
                list.add((AvatarPDBlock)(tgcomponent[i]));
				list.addAll(((AvatarPDBlock)tgcomponent[i]).getFullBlockList());
            }
        }
        return list;
    }
	
	public boolean hasInternalBlockWithName(String name) {
		LinkedList<AvatarPDBlock> list  = getFullBlockList();
		for(AvatarPDBlock b: list) {
			if (b.getValue().compareTo(name) ==0) {
				return true;
			}
		}
		return false;
	}
	
	
    
   	public int getDefaultConnector() {
        return TGComponentManager.APD_COMPOSITION_CONNECTOR;
	}
    
}

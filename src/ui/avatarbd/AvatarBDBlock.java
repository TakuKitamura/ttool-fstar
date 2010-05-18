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
* Class AvatarBDBlock
* Node. To be used in AVATAR Block Diagrams
* Creation: 06/04/2010
* @version 1.1 06/04/2010
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarbd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;
import ui.avatarsmd.*;


public class AvatarBDBlock extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
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
	
	
	// TAttribute, AvatarMethod, AvatarSignal
	protected Vector myAttributes, myMethods, mySignals;
	
	public String oldValue;
    
    public AvatarBDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];
        
        connectingPoint[0] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);
        
        connectingPoint[8] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
		multieditable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findAvatarBDBlockName("Block");
		setValue(name);
		oldValue = value;
		
		currentFontSize = maxFontSize;
		oldScaleFactor = tdp.getZoom();
        
        myImageIcon = IconManager.imgic700;
		
		myAttributes = new Vector();
		myMethods = new Vector();
		mySignals = new Vector();
		
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
		g.setColor(new Color(avat.getRed(), avat.getGreen(), avat.getBlue() + (getMyDepth() * 10)));
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
        // Strings
		int w;
		int h = 0;
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
			g.drawLine(x, y+h, x+width, y+h);
		}
		
		// Icon
		if ((width>30) && (height > (iconSize + 2*textX))) {
			iconIsDrawn = true;
			g.drawImage(IconManager.img5100, x + width - iconSize - textX, y + textX, null);
		} else {
			iconIsDrawn = false;
		}
		
		int cpt = h;
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
		}
		
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
    
	public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		if (iconIsDrawn) {
			if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize - textX, y + textX, iconSize, iconSize)) {
				tdp.selectTab(getValue());
				return true;
			}
		}
		// On the name ?
		if ((((limitName == -1) && (displayText) && (_y <= (y + 2*currentFontSize)))) || ((displayText) && (_y < limitName))) {
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
		
		// And so -> attributes!
		int tab = 0;
		if (limitAttr != -1) {
			if (_y > limitAttr) {
				if (limitMethod == -1) {
					tab = 2;
				} else {
					tab = 1;
				}
			}
		}
		if (limitMethod != -1) {
			if (_y > limitMethod) {
				tab = 2;
			}
		}
		
		if ((limitMethod == -1) && (limitAttr == -1)) {
			if (mySignals.size() > 1) {
				tab = 2;
			}
		}
		
		JDialogAvatarBlock jdab = new JDialogAvatarBlock(myAttributes, myMethods, mySignals, null, frame, "Setting attributes of " + value, "Attribute", tab);
        setJDialogOptions(jdab);
        jdab.setSize(650, 375);
        GraphicLib.centerOnParent(jdab);
        jdab.setVisible(true); // blocked until dialog has been closed
        //makeValue();
        //if (oldValue.equals(value)) {
            //return false;
        //}
		((AvatarBDPanel)tdp).updateAllSignalsOnConnectors();
		rescaled = true;
		return true;
    }
	
	protected void setJDialogOptions(JDialogAvatarBlock _jdab) {
        //jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        _jdab.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        _jdab.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
        _jdab.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);
		_jdab.addType(TAttribute.getStringType(TAttribute.INTEGER), true);
		_jdab.enableInitialValue(true);
        _jdab.enableRTLOTOSKeyword(false);
        _jdab.enableJavaKeyword(false);
    }
    
    
    public int getType() {
        return TGComponentManager.AVATARBD_BLOCK;
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
        if (tgc instanceof AvatarBDBlock) {
            //tgc.setCdRectangle((width/2) - tgc.getWidth(), (width/2), spacePt, height-spacePt);
            //System.out.println("cdRect comp swallow");
            ((AvatarBDBlock)tgc).resizeWithFather();
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
                if (((AvatarBDBlock)tgcomponent[i]).removeMyInternalComponent(tgc, false)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	protected String translateExtraParam() {
        TAttribute a;
		AvatarMethod am;
		AvatarSignal as;
		
		//System.out.println("Loading extra params of " + value);
        //value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(int i=0; i<myAttributes.size(); i++) {
            //System.out.println("Attribute:" + i);
            a = (TAttribute)(myAttributes.elementAt(i));
            //System.out.println("Attribute:" + i + " = " + a.getId());
            //value = value + a + "\n";
            sb.append("<Attribute access=\"");
            sb.append(a.getAccess());
            sb.append("\" id=\"");
            sb.append(a.getId());
            sb.append("\" value=\"");
            sb.append(a.getInitialValue());
            sb.append("\" type=\"");
            sb.append(a.getType());
            sb.append("\" typeOther=\"");
            sb.append(a.getTypeOther());
            sb.append("\" />\n");
        }
		for(int i=0; i<myMethods.size(); i++) {
            //System.out.println("Attribute:" + i);
            am = (AvatarMethod)(myMethods.elementAt(i));
            //System.out.println("Attribute:" + i + " = " + a.getId());
            //value = value + a + "\n";
            sb.append("<Method value=\"");
            sb.append(am.toString());
            sb.append("\" />\n");
        }
		for(int i=0; i<mySignals.size(); i++) {
            //System.out.println("Attribute:" + i);
            as = (AvatarSignal)(mySignals.elementAt(i));
            //System.out.println("Attribute:" + i + " = " + a.getId());
            //value = value + a + "\n";
            sb.append("<Signal value=\"");
            sb.append(as.toString());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int access, type;
            String typeOther;
            String id, valueAtt;
			String method;
			String signal;
			AvatarMethod am;
			AvatarSignal as;
            
            //System.out.println("Loading attributes");
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Attribute")) {
                                //System.out.println("Analyzing attribute");
                                access = Integer.decode(elt.getAttribute("access")).intValue();
                                type = Integer.decode(elt.getAttribute("type")).intValue();
                                try {
                                    typeOther = elt.getAttribute("typeOther");
                                } catch (Exception e) {
                                    typeOther = "";
                                }
                                id = elt.getAttribute("id");
                                valueAtt = elt.getAttribute("value");
                                
                                if (valueAtt.equals("null")) {
                                    valueAtt = "";
                                }
                                if ((TAttribute.isAValidId(id, false, false)) && (TAttribute.isAValidInitialValue(type, valueAtt))) {
                                    //System.out.println("Adding attribute " + id + " typeOther=" + typeOther);
                                    TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                                    myAttributes.addElement(ta);
                                }
                            }
							if (elt.getTagName().equals("Method")) {
                                //System.out.println("Analyzing attribute");
                                method = elt.getAttribute("value");
                                
                                if (method.equals("null")) {
                                    method = "";
                                }
								am = AvatarMethod.isAValidMethod(method);
								if (am != null) {
									myMethods.add(am);
								}
                            }
							if (elt.getTagName().equals("Signal")) {
                                //System.out.println("Analyzing attribute");
                                signal = elt.getAttribute("value");
                                
                                if (signal.equals("null")) {
                                    signal = "";
                                }
								as = AvatarSignal.isAValidSignal(signal);
								if (as != null) {
									mySignals.add(as);
								} else {
									TraceManager.addDev("Invalid signal:" + signal);
								}
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
	public String getBlockName() {
		return value;
    }
    
	
    
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                ((AvatarBDBlock)tgcomponent[i]).resizeWithFather();
            }
        }
		
		if (getFather() != null) {
			resizeWithFather();
		}
        
    }
	
	public void resizeWithFather() {
        if ((father != null) && (father instanceof AvatarBDBlock)) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public LinkedList<AvatarBDBlock> getBlockList() {
        LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                list.add((AvatarBDBlock)(tgcomponent[i]));
            }
        }
        return list;
    }
	
	public LinkedList<AvatarBDBlock> getFullBlockList() {
        LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof AvatarBDBlock) {
                list.add((AvatarBDBlock)(tgcomponent[i]));
				list.addAll(((AvatarBDBlock)tgcomponent[i]).getFullBlockList());
            }
        }
        return list;
    }
	
	public boolean hasInternalBlockWithName(String name) {
		LinkedList<AvatarBDBlock> list  = getFullBlockList();
		for(AvatarBDBlock b: list) {
			if (b.getValue().compareTo(name) ==0) {
				return true;
			}
		}
		return false;
	}
	
	
    
   	public int getDefaultConnector() {
        return TGComponentManager.AVATARBD_PORT_CONNECTOR;
	}
	
	public Vector getAttributeList() {
		return myAttributes;
	}
	
	public Vector getMethodList() {
		return myMethods;
	}
	
	public Vector getSignalList() {
		return mySignals;
	}
	
	public AvatarSignal getAvatarSignalFromName(String _name) {
		AvatarSignal as;
		for(int i=0; i<mySignals.size(); i++) {
			as = (AvatarSignal)(mySignals.get(i));
			if (as.getId().compareTo(_name) == 0) {
				return as;
			}
		}
		return null;
	}
	
	public Vector getListOfAvailableSignals() {
		return ((AvatarBDPanel)(tdp)).getListOfAvailableSignals(this);
	}
	
	// _id may contain the full signal
	public AvatarSignal getSignalNameBySignalDef(String _id) {
		int index0 = _id.indexOf('(');
		if (index0 > -1) {
			_id = _id.substring(0, index0);
		}
		_id = _id.trim();
		TraceManager.addDev("Searching for signal with id=" + _id);
		AvatarSignal as;
		for(int i=0; i<mySignals.size(); i++) {
			as = (AvatarSignal)(mySignals.get(i));
			if (as.getId().compareTo(_id) == 0) {
				TraceManager.addDev("found");
				return as;
			}
		}
		TraceManager.addDev("Not found");
		return null;
	}        
	
	public AvatarSignal getAvatarSignalFromFullName(String _id) {
		if(_id.startsWith("in ")) {
				return getSignalNameBySignalDef(_id.substring(3, _id.length()).trim());
		}
		
		if(_id.startsWith("out ")) {
				return getSignalNameBySignalDef(_id.substring(4, _id.length()).trim());
		}
		return null;
	}
	
	public AvatarSMDPanel getAvatarSMDPanel() {
        return ((AvatarDesignPanel)(tdp.tp)).getAvatarSMDPanel(getBlockName());
    }
	

    
}

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
* Class Requirement
* Turtle requirement: to be used in requirement diagram
* Creation: 30/05/2006
* @version 1.0 30/05/2006
* @author Ludovic APVRILLE
* @see
*/

package ui.req;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class Requirement extends TGCScalableWithInternalComponent implements WithAttributes, TGAutoAdjust {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
	protected int lineHeight = 30;
	private double dlineHeight = 0.0;
    protected boolean formal = false;
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;
	
	private Font myFont, myFontB;
	private int maxFontSize = 30;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
    
    protected final static String REGULAR_REQ = "<<Requirement>>";
    protected final static String FORMAL_REQ = "<<Formal Requirement>>";
    
    public final static int HIGH = 0;
    public final static int MEDIUM = 1;
    public final static int LOW = 2;
	
	protected String text;
    protected String []texts;
    protected String kind = "";
    protected String criticality = "";
    protected String violatedAction = "";
	
	
	
	// Icon
	private int iconSize = 18;
	private boolean iconIsDrawn = false;
    
    public Requirement(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(200, 120);
		oldScaleFactor = tdp.getZoom();
		dlineHeight = lineHeight * oldScaleFactor;
		lineHeight = (int)dlineHeight;
		dlineHeight = dlineHeight - lineHeight;
		
		minWidth = 1;
        minHeight = lineHeight;
        
        nbConnectingPoint = 24;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[1] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[2] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[3] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[4] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[6] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[7] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[8] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[9] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.25, 1.0);
		connectingPoint[10] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0);
		connectingPoint[11] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.75, 1.0);
        connectingPoint[12] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.25);
        connectingPoint[13] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[14] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.75);
        connectingPoint[15] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.25);
        connectingPoint[16] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[17] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.75);
        connectingPoint[18] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[19] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[20] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.75, 0.0);
        connectingPoint[21] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.25, 1.0);
		connectingPoint[22] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.5, 1.0);
		connectingPoint[23] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.75, 1.0);
		
        addTGConnectingPointsCommentTop();    
        
        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];
        
        int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
		multieditable = true;
		
		
        formal = false;
        
        // Name of the requirement
        name = "Requirement";
        value = tdp.findRequirementName("Requirement_");
        oldValue = value;
        
        myImageIcon = IconManager.imgic104;
		
		text = "Requirement description:\nDouble-click to edit";
        
        actionOnAdd();
    }
	
	public void makeValue() {
        texts = Conversion.wrapText(text);
    }
    
    public void internalDrawing(Graphics g) {
		Font f = g.getFont();
		Font fold = f;
		int w, c;
		int size;
		
		if (texts == null) {
			makeValue();
		}
		
        if (!tdp.isScaled()) {
            graphics = g;
        }
		
		if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
			currentFontSize = tdp.getFontSize();
			//System.out.println("Rescaled, font size = " + currentFontSize + " height=" + height);
			myFont = f.deriveFont((float)currentFontSize);
			myFontB = myFont.deriveFont(Font.BOLD);
			
			if (rescaled) {
				rescaled = false;
			}
		}
		
		if(currentFontSize <minFontSize) {
			displayText = false;
		} else {
			displayText = true;
		}
		
		int h  = g.getFontMetrics().getHeight();
        
		g.drawRect(x, y, width, height);
        
		g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
		g.setColor(Color.yellow);
        g.fillRect(x+1, y+1, width-1, lineHeight-1);
		g.setColor(ColorManager.REQ_ATTRIBUTE_BOX);
		g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
		ColorManager.setColor(g, getState(), 0);
		if ((lineHeight > 23) && (width > 23)){
			g.drawImage(IconManager.img8, x + width - iconSize + 1, y + 3, Color.yellow, null);
		}
		
		if (displayText) {
			size = currentFontSize - 2;
			g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));
			if (formal) {
				drawLimitedString(g, FORMAL_REQ, x, y + size, width, 1);
			} else {
				drawLimitedString(g, REGULAR_REQ, x, y + size, width, 1);
			}			
			size += currentFontSize;
			g.setFont(myFontB);
			w = g.getFontMetrics().stringWidth(value);
			drawLimitedString(g, value, x, y + size, width, 1);
			
		}
		
		g.setFont(myFont);
		String texti;
		if (formal) {
			texti = "TRDD";
		} else {
			texti = "Text";
		}
		
		String s ;
		int i;
		size = lineHeight + currentFontSize;
        for(i=0; i<texts.length; i++) {
			if (size < (height - 2)) {
				s = texts[i];
				if (i == 0) {
					s = texti + "=\"" + s;
				}
				if (i == (texts.length - 1)) {
					s = s + "\"";
				}
				drawLimitedString(g, s, x + textX, y + size, width, 0);
			}
			size += currentFontSize;
            
        }
        // Type and risk
		if (size < (height - 2)) {
			drawLimitedString(g, "Type=\"" + kind + "\"", x + textX, y + size, width, 0);
			size += currentFontSize;
			if (size < (height - 2)) {
				drawLimitedString(g, "Risk=\"" + criticality + "\"", x + textX, y + size, width, 0);
			}
		}
		
        
        g.setFont(f);
    }
    
	public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		// On the name ?
        oldValue = value;
		
        if ((displayText) && (_y <= (y + lineHeight))) {
			String text = getName() + ": ";
			if (hasFather()) {
				text = getTopLevelName() + " / " + text;
			}
			String s = (String)JOptionPane.showInputDialog(frame, text,
				"setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
				null,
				getValue());
			
			if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
				//boolean b;
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Requirement: the new name is not a valid name",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				if (!tdp.isRequirementNameUnique(s)) {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Requirement: the new name is already in use",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				
				int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
				minDesiredWidth = Math.max(size, minWidth);
				if (minDesiredWidth != width) {
					newSizeForSon(null);
				}
				setValue(s);
				
				if (tdp.actionOnDoubleClick(this)) {
					return true;
				} else {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the Requirement: this name is already in use",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					setValue(oldValue);
				}
			}
			return false;
		}
		
		return editAttributes();
		
    }
	
	public boolean editAttributes() {
		//String oldValue = value;
        JDialogRequirement jdr = new JDialogRequirement(tdp.getGUI().getFrame(), "Setting attributes of Requirement " + getRequirementName(), text, kind, criticality, violatedAction, isFormal());
        jdr.setSize(750, 400);
        GraphicLib.centerOnParent(jdr);
        jdr.show();
        
        if (!jdr.isRegularClose()) {
            return false;
        }
        
        text = jdr.getText();
        kind = jdr.getKind();
        criticality = jdr.getCriticality();
        violatedAction = jdr.getViolatedAction();
        
        makeValue();
        return true;
	}
	
	public void rescale(double scaleFactor){
		dlineHeight = (lineHeight + dlineHeight) / oldScaleFactor * scaleFactor;
		lineHeight = (int)(dlineHeight);
		dlineHeight = dlineHeight - lineHeight; 
		
		minHeight = lineHeight;
		
		super.rescale(scaleFactor);
	}
    
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public String getRequirementName() {
        return value;
    }
    
    public boolean isFormal() {
        return formal;
    }
    
    public void setFormal(boolean b) {
        formal = b;
    }
    
    public  int getType() {
        return TGComponentManager.TREQ_REQUIREMENT;
    }
    
    public void checkSizeOfSons() {
        ((TAttributeRequirement)(tgcomponent[0])).checkMySize();
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
        componentMenu.addSeparator();
        JMenuItem isFormal = null;
        if (formal) {
            isFormal = new JMenuItem("Set a regular requirement");
        } else {
            isFormal = new JMenuItem("Set as formal requirement");
        }
		isFormal.addActionListener(menuAL);
		
		JMenuItem editAttributes = new JMenuItem("Edit attributes");
		editAttributes.addActionListener(menuAL);
        
        componentMenu.add(isFormal);
		componentMenu.add(editAttributes);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.indexOf("regular") > -1) {
            //System.out.println("Set to regular");
            formal = false;
        } else {
			if (s.indexOf("formal") > 1) {
				//System.out.println("Set to formal");
				formal = true;
			} else {
				return editAttributes();
			}
        }
        return true;
    }
    
    public String toString() {
        String ret =  getValue();
        if (formal) {
            ret = ret + " " + FORMAL_REQ;
        }  else {
            ret = ret + " " + REGULAR_REQ;
        }
		
		ret += " " + text;
		ret += " criticality=" + criticality;
		
		if (formal) {
			ret += " violatedAction=" + violatedAction;
		}
		
        return ret;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Formal isFormal=\"");
        if (isFormal()) {
            sb.append("true\" />\n");
        } else {
            sb.append("false\" />\n");
        }
		if (texts != null) {
            for(int i=0; i<texts.length; i++) {
                //value = value + texts[i] + "\n";
                sb.append("<textline data=\"");
                sb.append(texts[i]);
                sb.append("\" />\n");
            }
        }
        sb.append("<kind data=\"");
        sb.append(kind);
        sb.append("\" />\n");
        sb.append("<criticality data=\"");
        sb.append(criticality);
        sb.append("\" />\n");
        sb.append("<violated data=\"");
        sb.append(violatedAction);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
			String oldtext = text;
            text = "";
			String s;
            
            //System.out.println("Loading tclass " + getValue());
            //System.out.println(nl.toString());
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Formal")) {
                                s = elt.getAttribute("isFormal");
                                if (s.equals("true")) {
                                    formal = true;
                                } else {
                                    formal = false;
                                }
                            } else if (elt.getTagName().equals("textline")) {
                                //System.out.println("Analyzing line0");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                text += GTURTLEModeling.decodeString(s) + "\n";
                            } else if (elt.getTagName().equals("kind")) {
                                //System.out.println("Analyzing line1");
                                kind = elt.getAttribute("data");
                                if (kind.equals("null")) {
                                    kind = "";
                                }
                            } else if (elt.getTagName().equals("criticality")) {
                                //System.out.println("Analyzing line2");
                                criticality = elt.getAttribute("data");
                                if (criticality.equals("null")) {
                                    criticality = "";
                                }
                            } else if (elt.getTagName().equals("violated")) {
                                //System.out.println("Analyzing line3");
                                violatedAction = elt.getAttribute("data");
                                if (violatedAction.equals("null")) {
                                    violatedAction = "";
                                }
								//System.out.println("Analyzing line4");
                            }
                        }
                    }
                }
            }
			if (text.length() == 0) {
                text = oldtext;
            }
        } catch (Exception e) {
			System.out.println("Failed when loading requirement extra parameters");
            throw new MalformedModelingException();
        }
		
		makeValue();
    }
    
    public String getViolatedAction() {
        return violatedAction;
    }
    
    public String getText() {
        return text;
    }
    
    public int getCriticality() {
        //System.out.println("Criticality=" + criticality);
        if (criticality.compareTo("High") == 0) {
            return Requirement.HIGH;
        } else if (criticality.compareTo("Medium") == 0) {
            return Requirement.MEDIUM;
        } else {
            return Requirement.LOW;
        }
    }
	
	public String getAttributes() {
		String attr = "";
		if (formal) {
			attr += "TRDD= " + text + "\n";
		} else {
			attr += "Text= " + text + "\n";
		}
		attr += "Type= " + kind + "\n";
		attr += "Risk= " + criticality + "\n";
		return attr;
	}
	
	public void autoAdjust(int mode) {
		//System.out.println("Auto adjust in mode = " + mode);
		
		if (graphics == null) {
			return;
		}
		
		// Must find for both modes which width is desirable
		String s0, s1;
		if (formal) {
			s0 = FORMAL_REQ;
			s1 = "TRDD=";
		} else {
			s0 = REGULAR_REQ;
			s1 = "Text=";
		}
		
		int w0 = graphics.getFontMetrics().stringWidth(s0);
		int w1 = graphics.getFontMetrics().stringWidth(value);
		int w2 = Math.max(w0, w1) + (2 * iconSize);
		int w3, w4 = w2;
		
		
		int i;
		
		if(texts.length == 1) {
			w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[0] + "\"");
			w4 = Math.max(w4, w3);
		} else {
			for(i=0; i<texts.length; i++) {
				if (i == 0) {
					w3 = graphics.getFontMetrics().stringWidth(s1 + "=\"" + texts[i]);
				} else if (i == (texts.length - 1)) {
					w3 = graphics.getFontMetrics().stringWidth(texts[i] + "\"");
				} else {
					w3 = graphics.getFontMetrics().stringWidth(texts[i]);
				}
				
				w4 = Math.max(w4, w3+2);
			}
		}
		w3 = graphics.getFontMetrics().stringWidth("Type=\"" + kind + "\"") + 2;
		w4 = Math.max(w4, w3);
		w3 = graphics.getFontMetrics().stringWidth("Risk=\"" + criticality + "\"") + 2;
		w4 = Math.max(w4, w3);
		
		
		if (mode == 1) {
			resize(w4, lineHeight);
			return;
		}
		
		int h = ((texts.length + 3) * currentFontSize) + lineHeight;
		
		resize(w4, h);
		
	}
    
}

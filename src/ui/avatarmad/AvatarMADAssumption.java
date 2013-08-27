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
* Class AvatarMADAssumption
* Avatar assumption: to be used in Modeling Assumptions diagram of AVATAR
* Creation: 27/08/2013
* @version 1.0 27/08/2013
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarmad;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class AvatarMADAssumptionRequirement extends TGCScalableWithInternalComponent implements WithAttributes, TGAutoAdjust {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
	protected int lineHeight = 30;
	private double dlineHeight = 0.0;
    //protected int reqType = 0;
	// 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;
	
	private Font myFont, myFontB;
	private int maxFontSize = 30;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
    
    protected final static String[] ASSUMPTION_TYPE_STR = {"<<System Assumption>>", "<<Environment Assumption>>", "<<Method Assumption>>"};
	protected final static int NB_REQ_TYPE = 3;
    
    protected final static String[] DURABILITY_TYPE = {"Permanent", "Temporary"};
	protected final static int NB_DURABILITY_TYPE = 2;
	
	protected final static String[] SOURCE_TYPE = {"End-user", "Stakeholder", "Model creator"};
	protected final static int NB_SOURCE_TYPE = 3;
	
	protected final static String[] STATUS_TYPE = {"Applied", "Alleviated"};
	protected final static int NB_STATUS_TYPE = 2;
	
	protected final static String[] LIMITATION_TYPE = {"Language", "Tool", "Method"};
	protected final static int NB_STATUS_TYPE = 2;
	
	protected String text;
    protected String []texts;
    protected int durability = 0,
    protected int source = 0;
    protected int status = 0;
    protected int limitation = "0";
	
	
	// Icon
	private int iconSize = 18;
	private boolean iconIsDrawn = false;
    
    public AvatarMADRequirement(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(200, 120);
		oldScaleFactor = tdp.getZoom();
		dlineHeight = lineHeight * oldScaleFactor;
		lineHeight = (int)dlineHeight;
		dlineHeight = dlineHeight - lineHeight;
		
		minWidth = 1;
        minHeight = lineHeight;
        
        nbConnectingPoint = 28;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[1] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[2] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[3] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[4] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[5] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[6] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[7] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[8] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[9] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[10] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[11] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[12] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[13] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[14] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[15] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[16] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[17] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[18] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[19] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[20] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[21] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.25, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[22] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[23] = new AvatarRDConnectingPointVerify(this, 0, 0, true, false, 0.75, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[24] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[25] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[26] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[27] = new AvatarMADAssumptionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		
		
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
		
        //reqType = 0;
		
		id = "0";
        
        // Name of the requirement
        name = "Assumption";
		id  = tdp.findAvatarAssumptionID(id);
		try {
			value = tdp.findAvatarAssumptionName("Assumption_", Integer.decode(id).intValue());
		} catch (Exception e) {
			value = tdp.findAvatarAssumptionName("Assumption_", 0);
		}
        oldValue = value;
        
        myImageIcon = IconManager.imgic5100;
		
		text = "Assumption description:\nDouble-click to edit";
        
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
		g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        g.fillRect(x+1, y+1, width-1, lineHeight-1);
		g.setColor(ColorManager.AVATAR_REQUIREMENT_ATTRIBUTES);
		g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
		ColorManager.setColor(g, getState(), 0);
		if ((lineHeight > 23) && (width > 23)){
			g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
		}
		
		if (displayText) {
			size = currentFontSize - 2;
			g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));
		
			drawLimitedString(g, REQ_TYPE_STR[reqType], x, y + size, width, 1);
	
			size += currentFontSize;
			g.setFont(myFontB);
			w = g.getFontMetrics().stringWidth(value);
			drawLimitedString(g, value, x, y + size, width, 1);
			
		}
		
		if (verified) {
			if (satisfied) {
				Color tmp = g.getColor();
				GraphicLib.setMediumStroke(g);
				g.setColor(Color.green);
				g.drawLine(x+width-2, y-6+lineHeight, x+width-6, y-2+lineHeight);
				g.drawLine(x+width-6, y-3+lineHeight, x+width-8, y-6+lineHeight);
				g.setColor(tmp);
				GraphicLib.setNormalStroke(g);
			} else {
				//g.drawString("acc", x + width - 10, y+height-10);
				Color tmp = g.getColor();
				GraphicLib.setMediumStroke(g);
				g.setColor(Color.red);
				g.drawLine(x+width-2, y-2+lineHeight, x+width-8, y-8+lineHeight);
				g.drawLine(x+width-8, y-2+lineHeight, x+width-2, y-8+lineHeight);
				g.setColor(tmp);
				GraphicLib.setNormalStroke(g);
			}
		}
		
		g.setFont(myFont);
		String texti = "Text";
		String s ;
		int i;
		size = lineHeight + currentFontSize;
		
		//ID
		if (size < (height - 2)) {
			drawLimitedString(g, "ID=" + id, x + textX, y + size, width, 0);
		}
		size += currentFontSize;
		
		//text
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
			drawLimitedString(g, "Kind=\"" + kind + "\"", x + textX, y + size, width, 0);
			size += currentFontSize;
			if (size < (height - 2)) {
				drawLimitedString(g, "Risk=\"" + criticality + "\"", x + textX, y + size, width, 0);
				size += currentFontSize;
				if (size < (height - 2)) {
					
					drawLimitedString(g, "Reference elements=\"" + referenceElements + "\"", x + textX, y + size, width, 0);
					
					size += currentFontSize;
					if (size < (height - 2)) {
						
						if (reqType == SECURITY_REQ) {
							drawLimitedString(g, "Targeted attacks=\"" + attackTreeNode + "\"", x + textX, y + size, width, 0);
						}
						
						if (reqType == SAFETY_REQ) {
							drawLimitedString(g, "Violated action=\"" + violatedAction + "\"", x + textX, y + size, width, 0);
						}
					}
				}
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
		String atn = null;
		String va = null;
		
		if (reqType == SECURITY_REQ) {
				atn = attackTreeNode;
		}
		
		
		if (reqType == SAFETY_REQ) {
				va = violatedAction;
		}
		
        JDialogRequirement jdr = new JDialogRequirement(tdp.getGUI().getFrame(), "Setting attributes of Requirement " + getRequirementName(), id, text, kind, criticality, va, reqType, atn, referenceElements);
        jdr.setSize(750, 400);
        GraphicLib.centerOnParent(jdr);
        jdr.show();
        
        if (!jdr.isRegularClose()) {
            return false;
        }
        
		
        if (reqType == SAFETY_REQ) {
        violatedAction = jdr.getViolatedAction();
        }
        if (reqType == SECURITY_REQ) {
		attackTreeNode = jdr.getAttackTreeNode();
		}
		referenceElements = jdr.getReferenceElements();
        id = jdr.getId();
        text = jdr.getText();
        kind = jdr.getKind();
        criticality = jdr.getCriticality();
		
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
    
    public  int getType() {
        return TGComponentManager.AVATARMAD_ASSUMPTION;
    }
    
    
    
    public String toString() {
        String ret =  getValue();
		
		ret += "ID=" + id;
		
		ret += " " + text;
		ret += " criticality=" + criticality;
		
        return ret;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		
		if (texts != null) {
            for(int i=0; i<texts.length; i++) {
                //value = value + texts[i] + "\n";
                sb.append("<textline data=\"");
                sb.append(GTURTLEModeling.transformString(texts[i]));
                sb.append("\" />\n");
            }
        }
        sb.append("<kind data=\"");
        sb.append(kind);
        sb.append("\" />\n");
        sb.append("<criticality data=\"");
        sb.append(criticality);
        sb.append("\" />\n");
		sb.append("<reqType data=\"");
        sb.append(reqType);
        sb.append("\" />\n");
		sb.append("<id data=\"");
        sb.append(id);
        sb.append("\" />\n");
		sb.append("<satisfied data=\"");
        sb.append(satisfied);
        sb.append("\" />\n");
		sb.append("<verified data=\"");
        sb.append(verified);
        sb.append("\" />\n");
        sb.append("<attackTreeNode data=\"");
        sb.append(attackTreeNode);
        sb.append("\" />\n");
        sb.append("<violatedAction data=\"");
        sb.append(violatedAction);
        sb.append("\" />\n");
        sb.append("<referenceElements data=\"");
        sb.append(referenceElements);
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
                            if (elt.getTagName().equals("textline")) {
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
                            } else if (elt.getTagName().equals("violatedAction")) {
                                //System.out.println("Analyzing line2");
                                violatedAction  = elt.getAttribute("data");
                                if (violatedAction.equals("null")) {
                                    violatedAction = "";
                                }
                            } else if (elt.getTagName().equals("attackTreeNode")) {
                                //System.out.println("Analyzing line2");
                                attackTreeNode = elt.getAttribute("data");
                                if (attackTreeNode.equals("null")) {
                                    attackTreeNode = "";
                                }
                            } else if (elt.getTagName().equals("referenceElements")) {
                                //System.out.println("Analyzing line2");
                                referenceElements = elt.getAttribute("data");
                                if (referenceElements.equals("null")) {
                                    referenceElements = "";
                                }
                            } else if (elt.getTagName().equals("reqType")) {
                                //System.out.println("Analyzing line2");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    reqType = REGULAR_REQ;
                                } else {
									try {
										reqType = Integer.decode(s).intValue();
									} catch (Exception e) {
										 reqType = REGULAR_REQ;
									}
								}
								if (reqType > (NB_REQ_TYPE-1)) {
									reqType = REGULAR_REQ;
								}
								
                            } else if (elt.getTagName().equals("id")) {
                                //System.out.println("Analyzing line3");
                                id = elt.getAttribute("data");
                                if (id.equals("null")) {
                                    id = "";
                                }
								//System.out.println("Analyzing line4");
							} else if (elt.getTagName().equals("satisfied")) {
                                //System.out.println("Analyzing line3");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    satisfied = false;
                                } else {
									if (s.equals("true")) {
										satisfied = true;
									} else {
										satisfied = false;
									}
								}
								//System.out.println("Analyzing line4");
							} else if (elt.getTagName().equals("verified")) {
                                //System.out.println("Analyzing line3");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    verified = false;
                                } else {
									if (s.equals("true")) {
										verified = true;
									} else {
										verified = false;
									}
								}
							}
								//System.out.println("Analyzing line4");
                        }
                    }
                }
            }
			if (text.length() == 0) {
                text = oldtext;
            }
        } catch (Exception e) {
			TraceManager.addError("Failed when loading requirement extra parameters (AVATARRD)");
            throw new MalformedModelingException();
        }
		
		makeValue();
    }
    
    
    public String getText() {
        return text;
    }
    
	public String getID() {
		return id;
	}
	
	public String getKind() {
		return kind;
	}
	
	public String getViolatedAction() {
        return violatedAction;
    }
	
    public String getAttackTreeNode() {
        return attackTreeNode;
    }
    
    public String getReferenceElements() {
        return referenceElements;
    }
    
    public int getCriticality() {
        //System.out.println("Criticality=" + criticality);
        if (criticality.compareTo("High") == 0) {
            return AvatarRDRequirement.HIGH;
        } else if (criticality.compareTo("Medium") == 0) {
            return AvatarRDRequirement.MEDIUM;
        } else {
            return AvatarRDRequirement.LOW;
        }
    }
	
	public String getAttributes() {
		String attr = "ID=" + id + "\n";
		attr += "Text= " + text + "\n";
		attr += "Kind= " + kind + "\n";
		attr += "Risk= " + criticality + "\n";
		attr += "References= " + referenceElements + "\n";
		if (reqType == SAFETY_REQ) {
			attr += "Violated action= " + violatedAction + "\n";
		}
		if (reqType == SECURITY_REQ) {
			attr += "Attack tree node(s)= " + attackTreeNode + "\n";
		}
		return attr;
	}
	
	public void autoAdjust(int mode) {
		//System.out.println("Auto adjust in mode = " + mode);
		
		if (graphics == null) {
			return;
		}
		
		Font f = graphics.getFont();
		Font f0 = f.deriveFont((float)currentFontSize);
		Font f1 = f0.deriveFont(Font.BOLD);
		Font f2 = f.deriveFont((float)(currentFontSize - 2));
		
		// Must find for both modes which width is desirable
		String s0, s1;
		s0 = REQ_TYPE_STR[reqType];
		s1 = "Text=";
		
		graphics.setFont(f2);
		int w0 = graphics.getFontMetrics().stringWidth(s0);
		graphics.setFont(f1);
		int w1 = graphics.getFontMetrics().stringWidth(value);
		int w2 = Math.max(w0, w1) + (2 * iconSize);
		
		graphics.setFont(f0);
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
		w3 = graphics.getFontMetrics().stringWidth("Kind=\"" + kind + "\"") + 2;
		w4 = Math.max(w4, w3);
		w3 = graphics.getFontMetrics().stringWidth("Risk=\"" + criticality + "\"") + 2;
		w4 = Math.max(w4, w3);
		w3 = graphics.getFontMetrics().stringWidth("ID=\"" + id + "\"") + 2;
		w4 = Math.max(w4, w3);
		
		if (mode == 1) {
			resize(w4, lineHeight);
			return;
		}
		
		int h;
		if (mode == 2) {
			h = ((texts.length + 4) * currentFontSize) + lineHeight;
		} else {
			h = ((texts.length + 5) * currentFontSize) + lineHeight;
		}
		
		
		resize(w4, h);
		
	}
    
}

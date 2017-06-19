/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package ui.req;


import myutil.Conversion;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogRequirement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
* Class Requirement
* Turtle requirement: to be used in requirement diagram
* Creation: 30/05/2006
* @version 1.0 30/05/2006
* @author Ludovic APVRILLE
 */
public class Requirement extends TGCScalableWithInternalComponent implements WithAttributes, TGAutoAdjust {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 22;
	protected int lineHeight = 30;
	private double dlineHeight = 0.0;
    protected int reqType = 0;
	// 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    protected Graphics graphics;
    //protected int iconSize = 30;
	
	private Font myFont, myFontB;
//	private int maxFontSize = 30;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
    
    protected final static String REGULAR_REQ = "<<Requirement>>";
    protected final static String FORMAL_REQ = "<<Formal Requirement>>";
	protected final static String SECURITY_REQ = "<<Security Requirement>>";
    
    public final static int HIGH = 0;
    public final static int MEDIUM = 1;
    public final static int LOW = 2;
	
	protected String text;
    protected String []texts;
    protected String kind = "";
    protected String criticality = "";
    protected String violatedAction = "";
	protected String attackTreeNode = "";
	protected String id = "";
	
	protected boolean satisfied = false;
	protected boolean verified = false;
	
	private JMenuItem isRegular = null;
    private JMenuItem isFormal = null;
	private JMenuItem isSecurity = null;
	private JMenuItem menuNonSatisfied = null;
	private JMenuItem menuSatisfied = null;
	private JMenuItem menuNonVerified = null;
	private JMenuItem menuVerified = null;
	JMenuItem editAttributes = null;
	
	
	// Icon
	private int iconSize = 18;
//	private boolean iconIsDrawn = false;
    
    public Requirement(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
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
        connectingPoint[0] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[1] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[2] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[3] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[4] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[5] = new TGConnectingPointDerive(this, 0, 0, true, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[6] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[7] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[8] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[9] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[10] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[11] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.75, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[12] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[13] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[14] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[15] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[16] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[17] = new TGConnectingPointVerify(this, 0, 0, true, false, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[18] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[19] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[20] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[21] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.25, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[22] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[23] = new TGConnectingPointVerify(this, 0, 0, true, false, 0.75, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[24] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[25] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[26] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		connectingPoint[27] = new TGConnectingPointDerive(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
		
		
        addTGConnectingPointsCommentTop();    
        
        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];
        
    //    int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
		multieditable = true;
		
        reqType = 0;
		
		id = "0";
        
        // Name of the requirement
        name = "Requirement";
        value = tdp.findRequirementName("Requirement_");
        oldValue = value;
        
        myImageIcon = IconManager.imgic1002;
		
		text = "Requirement description:\nDouble-click to edit";
        
        actionOnAdd();
    }
	
	public void makeValue() {
        texts = Conversion.wrapText(text);
    }
    
    public void internalDrawing(Graphics g) {
		Font f = g.getFont();
	//	Font fold = f;
	//	int w, c;
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

        displayText = currentFontSize >= minFontSize;
		
	//	int h  = g.getFontMetrics().getHeight();
        
		g.drawRect(x, y, width, height);
        
		g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
		g.setColor(ColorManager.REQ_TOP_BOX);
        g.fillRect(x+1, y+1, width-1, lineHeight-1);
		g.setColor(ColorManager.REQ_ATTRIBUTE_BOX);
		g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
		ColorManager.setColor(g, getState(), 0);
		if ((lineHeight > 23) && (width > 23)){
			if (ColorManager.REQ_TOP_BOX != Color.white) {
				g.drawImage(IconManager.img8, x + width - iconSize + 1, y + 3, Color.yellow, null);
			}
		}
		
		if (displayText) {
			size = currentFontSize - 2;
			g.setFont(myFont.deriveFont((float)(myFont.getSize() - 2)));
			if (reqType == 1) {
				drawLimitedString(g, FORMAL_REQ, x, y + size, width, 1);
			} else {
				if (reqType == 0) {
					drawLimitedString(g, REGULAR_REQ, x, y + size, width, 1);
				} else {
					drawLimitedString(g, SECURITY_REQ, x, y + size, width, 1);
				}
			}			
			size += currentFontSize;
			g.setFont(myFontB);
	//		w = g.getFontMetrics().stringWidth(value);
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
		String texti;
		if (reqType == 1) {
			texti = "TRDD";
		} else {
			texti = "Text";
		}
		
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
				if ((size < (height - 2)) && (reqType == 2)) {
					drawLimitedString(g, "Targeted attacks=\"" + attackTreeNode + "\"", x + textX, y + size, width, 0);
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
        JDialogRequirement jdr = new JDialogRequirement(tdp.getGUI().getFrame(), "Setting attributes of Requirement " + getRequirementName(), id, text, kind, criticality, violatedAction, reqType, attackTreeNode, null);
       // jdr.setSize(750, 400);
        GraphicLib.centerOnParent(jdr, 750, 400);
        jdr.setVisible( true );
        
        if (!jdr.isRegularClose()) {
            return false;
        }
        
		id = jdr.getId();
        text = jdr.getText();
        kind = jdr.getKind();
        criticality = jdr.getCriticality();
        violatedAction = jdr.getViolatedAction();
		attackTreeNode = jdr.getAttackTreeNode();
        
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
        return (reqType == 1);
    }
    
    public void setRequirementType(int _type) {
        reqType = _type;
    }
    
    public int getRequirementType() {
	    return reqType;
    }
    
    public boolean isSatisfied() {
	    return satisfied;
    }
	
	public boolean isVerified() {
		return verified;
	}
    
    public  int getType() {
        return TGComponentManager.TREQ_REQUIREMENT;
    }
    
    public void checkSizeOfSons() {
        ((TAttributeRequirement)(tgcomponent[0])).checkMySize();
    }
    
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
		
		componentMenu.addSeparator();
		
		if (isRegular == null) {
			isRegular = new JMenuItem("Set as regular requirement");
			isFormal = new JMenuItem("Set as formal requirement");
			isSecurity = new JMenuItem("Set as security requirement");
			menuNonSatisfied = new JMenuItem("Set as non satisfied");
			menuSatisfied = new JMenuItem("Set as satisfied");
			menuNonVerified = new JMenuItem("Set as non verified");
			menuVerified = new JMenuItem("Set as verified");
			
			isRegular.addActionListener(menuAL);
			isFormal.addActionListener(menuAL);
			isSecurity.addActionListener(menuAL);
			menuNonSatisfied.addActionListener(menuAL);
			menuSatisfied.addActionListener(menuAL);
			menuNonVerified.addActionListener(menuAL);
			menuVerified.addActionListener(menuAL);
			
			editAttributes = new JMenuItem("Edit attributes");
			editAttributes.addActionListener(menuAL);
		}
		
		menuNonSatisfied.setEnabled(satisfied);
		menuSatisfied.setEnabled(!satisfied);
			
		menuNonVerified.setEnabled(verified);
		menuVerified.setEnabled(!verified);
        
        componentMenu.add(isRegular);
		componentMenu.add(isFormal);
		componentMenu.add(isSecurity);
		componentMenu.add(menuNonSatisfied);
		componentMenu.add(menuSatisfied);
		componentMenu.add(menuNonVerified);
		componentMenu.add(menuVerified);
		componentMenu.add(editAttributes);
    }
    
    public boolean eventOnPopup(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.indexOf("regular") > -1) {
            //System.out.println("Set to regular");
            reqType = 0;
        } else {
			if (s.indexOf("formal") > 1) {
				//System.out.println("Set to formal");
				reqType = 1;
			} else {
				if (s.indexOf("security") > 1) {
				//System.out.println("Set to formal");
				reqType = 2;
				} else {
					if (e.getSource() == menuNonSatisfied) {
						satisfied = false;
					} else if (e.getSource() == menuSatisfied) {
						satisfied = true;
					} else if (e.getSource() == menuNonVerified) {
						verified = false;
					} else if (e.getSource() == menuVerified) {
						verified = true;
					} else {
						return editAttributes();
					}
					
				}
			}
        }
        return true;
    }
    
    public String toString() {
        String ret =  getValue();
		
		ret += "ID=" + id;
		
        if (reqType == 1) {
            ret = ret + " " + FORMAL_REQ;
        }  else {
			if (reqType == 0) {
				ret = ret + " " + REGULAR_REQ;
			} else {
				ret = ret + " " + SECURITY_REQ;
			}
        }
		
		ret += " " + text;
		ret += " criticality=" + criticality;
		
		if (reqType == 1) {
			ret += " violatedAction=" + violatedAction;
		}
		
		if (reqType == 2) {
			ret += " attackTreeNode =" + attackTreeNode;
		}
		
        return ret;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        /*sb.append("<Formal isFormal=\"");
        if (isFormal()) {
            sb.append("true\" />\n");
        } else {
            sb.append("false\" />\n");
        }*/
		sb.append("<type data=\"");
		sb.append("" + reqType + "\" />\n");
		
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
		sb.append("<attackTreeNode data=\"");
        sb.append(attackTreeNode);
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
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
    @Override
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
                                    reqType = 1;
                                } else {
                                    reqType = 0;
                                }
                            } else if (elt.getTagName().equals("type")) {
                                //System.out.println("Analyzing line0");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    reqType = 0;
                                }
								reqType = Integer.decode(s).intValue();
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
                            } else if (elt.getTagName().equals("id")) {
                                //System.out.println("Analyzing line3");
                                id = elt.getAttribute("data");
                                if (id.equals("null")) {
                                    id = "";
                                }
								//System.out.println("Analyzing line4");
							} else if (elt.getTagName().equals("attackTreeNode")) {
                                //System.out.println("Analyzing line3");
                                attackTreeNode = elt.getAttribute("data");
                                if (attackTreeNode.equals("null")) {
                                    attackTreeNode = "";
                                }
								//System.out.println("Analyzing line4");
							} else if (elt.getTagName().equals("satisfied")) {
                                //System.out.println("Analyzing line3");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    satisfied = false;
                                } else {
                                    satisfied = s.equals("true");
								}
								//System.out.println("Analyzing line4");
							} else if (elt.getTagName().equals("verified")) {
                                //System.out.println("Analyzing line3");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    verified = false;
                                } else {
                                    verified = s.equals("true");
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
			System.out.println("Failed when loading requirement extra parameters");
            throw new MalformedModelingException();
        }
		
		makeValue();
    }
    
    public String getViolatedAction() {
        return violatedAction;
    }
	
    public String getAttackTreeNode() {
        return attackTreeNode;
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
		String attr = "ID=" + id + "\n";
		if (reqType == 1) {
			attr += "TRDD= " + text + "\n";
		} else {
			attr += "Text= " + text + "\n";
		}
		attr += "Kind= " + kind + "\n";
		attr += "Risk= " + criticality + "\n";
		if (reqType == 2) {
			attr += "AttackTreeNode=" + attackTreeNode + "\n";
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
		if (reqType == 1) {
			s0 = FORMAL_REQ;
			s1 = "TRDD=";
		} else {
			if (reqType == 0) {
				s0 = REGULAR_REQ;
				s1 = "Text=";
			} else {
				s0 = SECURITY_REQ;
				s1 = "Text=";
			}
		}
		
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
		
		if (reqType == 2) {
			w3 = graphics.getFontMetrics().stringWidth("Attack Tree Node=\"" + attackTreeNode + "\"") + 2;
			w4 = Math.max(w4, w3);
		}
		
		
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

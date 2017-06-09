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
 * Class TMLCRecordComponent
 * Record Component. To be used in TML component task diagrams
 * Creation: 20/07/2010
 * @version 1.0 20/07/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttribute;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class TMLCRecordComponent extends TGCScalableWithInternalComponent implements SwallowedTGComponent {
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
//	private int spacePt = 3;
	private Color myColor;
	
	// Icon
	private int iconSize = 15;
	//private boolean iconIsDrawn = false;
	
	// Attributes
	//private boolean attributesAreDrawn = false;
	protected LinkedList<TAttribute> myAttributes;
	private int textX = 15; // border for ports
	private double dtextX = 0.0;
	
	public String oldValue;
    
    public TMLCRecordComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(200, 150);
		
		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 0;
       
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        multieditable = true;
		editable = true;
        removable = true;
        userResizable = true;
        
		value = tdp.findTMLRecordComponentName("Record_");
		oldValue = value;
		setName("Record component");
		
        myImageIcon = IconManager.imgic1202;
		
		myAttributes = new LinkedList<TAttribute> ();
		
		actionOnAdd();
    }
    
    public void internalDrawing(Graphics g) {
		int w;
		Font f = g.getFont();
		Font fold = f;
		
		if (myColor == null) {
			myColor = new Color(193, 218, 241- (getMyDepth() * 10), 200);
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
			
			int maxCurrentFontSize = Math.max(0, Math.min(height-(2*textX), maxFontSize));
			
			f = f.deriveFont((float)maxCurrentFontSize);
			g.setFont(f);
			while(maxCurrentFontSize > (minFontSize-1)) {
				if (g.getFontMetrics().stringWidth(value) < (width - iconSize - (2 * textX))) {
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
		
		// Zoom is assumed to be computed
		Color c = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(c);
		}
		
        // Font size 
		if (displayText) {
			f = f.deriveFont((float)currentFontSize);
			g.setFont(f);
			w = g.getFontMetrics().stringWidth(value);
			if (w > (width - 2 * (iconSize + textX))) {
				g.drawString(value, x + textX + 1, y + currentFontSize + textX);
			} else {
				g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
			}
		}
		
		// Icon
		if ((width>30) && (height > (iconSize + 2*textX))) {
			//iconIsDrawn = true;
			g.drawImage(IconManager.imgic1200.getImage(), x + width - iconSize - textX, y + textX, null);
		} 
//		else {
//			iconIsDrawn = false;
//		}
		
		// Attributes
		if (((TMLComponentTaskDiagramPanel)tdp).areAttributesVisible()) {
			int index = 0;
			int cpt = currentFontSize + 2 * textX;
			String attr;
			
			TAttribute a;
           
			int si = Math.min(12, (int)((float)currentFontSize - 2));
			
			f = g.getFont();
			f = f.deriveFont((float)si);
			g.setFont(f);
			int step = si + 2;
			
			while(index < myAttributes.size()) {
				cpt += step;
				if (cpt >= (height - textX)) {
					break;
				}
				a = myAttributes.get (index);
				attr = a.toString();
				w = g.getFontMetrics().stringWidth(attr);
				if ((w + (2 * textX) + 1) < width) {
					g.drawString(attr, x + textX, y + cpt);
				} else {
					attr = "...";
					w = g.getFontMetrics().stringWidth(attr);
					if ((w + textX + 2) < width) {
						g.drawString(attr, x + textX + 1, y + cpt);
					} else {
						// skip attribute
						cpt -= step;
					}
				}
				index ++;
			}
		}
		
		g.setFont(fold);
		
    }
	
	public void rescale(double scaleFactor){
		dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
		textX = (int)(dtextX);
		dtextX = dtextX - textX; 
		
		super.rescale(scaleFactor);
	}
    
    public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    
    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		
		// On the name ?
		if ((displayText) && (_y <= (y + currentFontSize + textX))) {
			//System.out.println("Edit on double click x=" + _x + " y=" + _y);
			oldValue = value;
			String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
			JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
			null,
			getValue());
			if ((s != null) && (s.length() > 0)) {
				// Check whether this name is already in use, or not 
				
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane.showMessageDialog(frame,
						"Could not change the name of the component: the new name is not a valid name",
						"Error",
						JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				if (((TMLComponentTaskDiagramPanel)(tdp)).nameRecordComponentInUse(oldValue, s)) {
					JOptionPane.showMessageDialog(frame,
						"Error: the name is already in use",
						"Name modification",
						JOptionPane.ERROR_MESSAGE);
					return false;
				} else {
					//System.out.println("Set value with change");
					setValueWithChange(s);
					rescaled = true;
					//System.out.println("return true");
					return true;
				}
			}
			return false;
		}
		
		// And so -> attributes!
		JDialogAttribute jda = new JDialogAttribute(myAttributes, null, frame, "Setting fields of " + value, "Field");
        setJDialogOptions(jda);
      //  jda.setSize(650, 375);
        GraphicLib.centerOnParent(jda, 650, 375);
        jda.setVisible(true); // blocked until dialog has been closed
        //makeValue();
        //if (oldValue.equals(value)) {
            //return false;
        //}
		rescaled = true;
		return true;
		
    }
	
	protected void setJDialogOptions(JDialogAttribute jda) {
        jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        jda.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        jda.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
        jda.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);
		jda.enableInitialValue(true);
        jda.enableRTLOTOSKeyword(true);
        jda.enableJavaKeyword(false);
    }
    
    public int getType() {
        return TGComponentManager.TMLCTD_RCOMPONENT;
    }
	
	public void wasSwallowed() {
		myColor = null;
	}
	
	public void wasUnswallowed() {
		myColor = null;
		setFather(null);
		TDiagramPanel tdp = getTDiagramPanel();
		setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
			
	}
    
    public void hasBeenResized() {
		rescaled = true;
		
		if (getFather() != null) {
			resizeWithFather();
		}
    }
	
	public void resizeWithFather() {
        if ((father != null) && (father instanceof TMLCCompositeComponent)) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public int getChildCount() {
        return myAttributes.size();
    }
    
    public Object getChild(int index) {
		if (index == 0) {
			return value;
		} else {
			return myAttributes.get (index-1);
		}
    }
    
    public int getIndexOfChild(Object child) {
		if (child instanceof String) {
			return 0;
		} else {
			//Object o;
			return myAttributes.indexOf(child) + 1;
		}
    }
    
    protected String translateExtraParam() {
        TAttribute a;
		//System.out.println("Loading extra params of " + value);
        //value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(int i=0; i<myAttributes.size(); i++) {
            //System.out.println("Attribute:" + i);
            a = myAttributes.get (i);
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
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int access, type;
            String typeOther;
            String id, valueAtt;
            
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
                                    myAttributes.add (ta);
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
	
	public int getCurrentFontSize() {
		return currentFontSize;
	}
	
	public LinkedList<TAttribute> getAttributes() {
		return myAttributes;
	}
	  
    
}

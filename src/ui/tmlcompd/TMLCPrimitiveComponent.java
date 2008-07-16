/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TMLCPrimitiveComponent
 * Primitive Component. To be used in TML component task diagrams
 * Creation: 12/03/2008
 * @version 1.0 12/03/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class TMLCPrimitiveComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
	private int maxFontSize = 20;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private int spacePt = 3;
	private Color myColor;
	
	// Icon
	private int iconSize = 15;
	private boolean iconIsDrawn = false;
	
	// Attributes
	private boolean attributesAreDrawn = false;
	protected Vector myAttributes;
	private int textX = 15; // border for ports
	private double dtextX = 0.0;
	
	public String oldValue;
    
    public TMLCPrimitiveComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(250, 200);
		
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
        
		value = tdp.findTMLPrimitiveComponentName("TMLComp_");
		oldValue = value;
		setName("Primitive component");
		
        myImageIcon = IconManager.imgic1202;
		
		myAttributes = new Vector();
		
		actionOnAdd();
    }
    
    public void internalDrawing(Graphics g) {
		int w;
		Font f = g.getFont();
		Font fold = f;
		
		if (myColor == null) {
			myColor = new Color(201, 243, 188- (getMyDepth() * 10));
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
			iconIsDrawn = true;
			g.drawImage(IconManager.imgic1200.getImage(), x + width - iconSize - textX, y + textX, null);
		} else {
			iconIsDrawn = false;
		}
		
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
				a = (TAttribute)(myAttributes.elementAt(index));
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
		// On the icon?
		if (iconIsDrawn) {
			if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize, y + 2, iconSize, iconSize)) {
				tdp.selectTab(getValue());
				return true;
			}
		}
		
		// On the name ?
		if ((displayText) && (_y <= (y + currentFontSize))) {
			//System.out.println("Edit on double click x=" + _x + " y=" + _y);
			String oldValue = value;
			String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
			JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
			null,
			getValue());
			if ((s != null) && (s.length() > 0)) {
				setValue(s);
				((TMLComponentTaskDiagramPanel)(tdp)).renamePrimitiveComponent(oldValue, value);
				rescaled = true;
				return true;
			}
			return false;
		}
		
		// And so -> attributes!
		JDialogAttribute jda = new JDialogAttribute(myAttributes, null, frame, "Setting attributes of " + value, "Attribute");
        setJDialogOptions(jda);
        jda.setSize(650, 375);
        GraphicLib.centerOnParent(jda);
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
        return TGComponentManager.TMLCTD_PCOMPONENT;
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
    
    public void addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		//boolean swallowed = false;
		
        //System.out.println("Add swallow component");
        // Choose its position    
        // Make it an internal component
        // It's one of my son
        //Set its coordinates
		
		 if (tgc instanceof TMLCPrimitivePort) {
			 tgc.setFather(this);
			 tgc.setDrawingZone(true);
			 ((TMLCPrimitivePort)tgc).resizeWithFather();
			 addInternalComponent(tgc, 0);
		 }
        
        
    }
    
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }
    
    
    /*public Vector getArtifactList() {
        Vector v = new Vector();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                v.add(tgcomponent[i]);
            }
        }
        return v;
    }*/
    
    public void hasBeenResized() {
		rescaled = true;
        for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCPrimitivePort) {
				((TMLCPrimitivePort)tgcomponent[i]).resizeWithFather();
			}
        }
		
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
        return myAttributes.size() + nbInternalTGComponent + 1;
    }
    
    public Object getChild(int index) {
		if (index == 0) {
			return value;
		} else {
			if (index <= myAttributes.size()) {
				return myAttributes.elementAt(index-1);
			} else {
				return tgcomponent[index-1-myAttributes.size()];
			}
		}
    }
    
    public int getIndexOfChild(Object child) {
		if (child instanceof String) {
			return 0;
		} else {
			Object o;
			if (myAttributes.indexOf(child) > -1) {
				return myAttributes.indexOf(child) + 1;
			} else {
				for(int i=0; i<nbInternalTGComponent; i++) {
					if (tgcomponent[i] == child) {
						return myAttributes.size() + 1 + i;
					}
				}
			}
		}
		return -1;
    }
    
    protected String translateExtraParam() {
        TAttribute a;
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
	
	public Vector getAttributes() {
		return myAttributes;
	}
	
	public LinkedList getAllChannelsOriginPorts() {
		return getAllPorts(0, true);
	}
	
	public LinkedList getAllEventsOriginPorts() {
		return getAllPorts(1, true);
	}
	
	public LinkedList getAllRequestsDestinationPorts() {
		return getAllPorts(2, false);
	}
	
	public LinkedList getAllPorts(int _type, boolean _isOrigin) {
		LinkedList ret = new LinkedList();
		TMLCPrimitivePort port;
		
		System.out.println("Type = " + _type + " isOrigin=" + _isOrigin);
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCPrimitivePort) {
				port = (TMLCPrimitivePort)tgcomponent[i];
				System.out.println("Found one port:" + port.getPortName() + " type=" + port.getPortType() + " origin=" + port.isOrigin());
				if ((port.getPortType() == _type) && (port.isOrigin() == _isOrigin)) {
					ret.add(port);
					System.out.println("Adding port:" + port.getPortName());
				}
			}
		}
		
		return ret;
	}
	
	public ArrayList<TMLCPrimitivePort> getAllInternalPrimitivePorts() {
		ArrayList<TMLCPrimitivePort> list = new ArrayList<TMLCPrimitivePort>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCPrimitivePort) {
				list.add((TMLCPrimitivePort)(tgcomponent[i]));
			}
		}
		
		return list;
	}
	
    
   	/*public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
      }*/
	  
	 
	  
	  /*public String getAttributes() {
		  String attr = "";
		  attr += "Data size (in byte) = " + byteDataSize + "\n";
		  attr += "Pipeline size = " + pipelineSize + "\n";
		  if (schedulingPolicy == HwCPU.DEFAULT_SCHEDULING) {
			  attr += "Scheduling policy = basic Round Robin\n";
		  }
		  attr += "Task switching time (in cycle) = " + taskSwitchingTime + "\n";
		  attr += "Go in idle mode (in cycle) = " + goIdleTime + "\n";
		  attr += "Max consecutive idle cycles before going in idle mode (in cycle) = " + maxConsecutiveIdleCycles + "\n";
		  attr += "Execi execution time (in cycle) = " + execiTime + "\n";
		  attr += "Branching prediction misrate (in %) = " + branchingPredictionPenalty + "\n";
		  attr += "Cache miss (in %) = " + cacheMiss + "\n";
		  return attr;
		  
		  
	  }*/
	  
    
}

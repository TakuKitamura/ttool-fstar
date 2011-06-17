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
* Class AvatarSMDTransitionInfo
* Internal component that represents a set of parameter for a transition
* e.g., guard, after, compute, set of actions
* Creation: 12/04/2010
* @version 1.0 12/04/2010
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarsmd;

import java.awt.*;
//import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class AvatarSMDTransitionInfo extends TGCWithoutInternalComponent {
	
	protected String guard;
	protected String afterMin;
	protected String afterMax;
	protected String computeMin;
	protected String computeMax;
	protected Vector<String> listOfActions;
	
	protected String [] filesToInclude;
	protected String [] codeToInclude;
    
    protected int minWidth = 10;
    protected int minHeight = 15;
    protected int h;
    
    protected String defaultValue;
    
    public AvatarSMDTransitionInfo(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        moveable = true;
        editable = true;
        removable = false;
        
		guard = "[ ]";
		afterMin = "";
		afterMax = "";
		computeMin = "";
		computeMax = "";
		filesToInclude = null;
		codeToInclude = null;
        
        
		nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[4];
        connectingPoint[0] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[2] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[3] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0, 0.5);
        
        listOfActions = new Vector<String>();
        
        myImageIcon = IconManager.imgic302;
    }
    
    public Vector<String> getListOfActions() {
        return listOfActions;
    }
    
    public void internalDrawing(Graphics g) {
		int step = 0;
		String s;
        h  = g.getFontMetrics().getHeight();
        for (int j=0; j<nbConnectingPoint; j++) {
            connectingPoint[j].setCdY(-h+1);
        }
        
        ColorManager.setColor(g, getState(), 0);
		int inc = h;
        
        boolean atLeastOneThing = false;
		
		g.setColor(ColorManager.AVATAR_GUARD);
		
		if (guard.length() > 0) {
			if (guard.compareTo("[ ]") != 0) {
				g.drawString(guard, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(guard), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			}
		}
		
		g.setColor(ColorManager.AVATAR_TIME);
		
		if (afterMin.length() > 0) {
			if (afterMax.length() > 0) {
				s = "after (" + afterMin + "," + afterMax + ")";
				g.drawString(s, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(s), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			} else {
				s = "after (" + afterMin + ")";
				g.drawString(s, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(s), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			}
		}
		
		if (computeMin.length() > 0) {
			if (computeMax.length() > 0) {
				s = "computeFor (" + computeMin + "," + computeMax + ")";
				g.drawString(s, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(s), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			} else {
				s = "computeFor (" + computeMin + ")";
				g.drawString(s, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(s), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			}
		}
		
		g.setColor(ColorManager.AVATAR_ACTION);
		
		for(int i=0; i<listOfActions.size(); i++) {
			s = listOfActions.get(i);
			if (s.length() > 0) {
				g.drawString(s, x, y + step);
                atLeastOneThing = true;
				if (!tdp.isScaled()) {
					width = Math.max(g.getFontMetrics().stringWidth(s), width);
					width = Math.max(minWidth, width);
				}
				step += inc;
			}
		}
		
		if (!tdp.isScaled()) {
			height = Math.max(step, minHeight);
		}
        
        if (!atLeastOneThing) {
            width=minWidth;
        }
		
		ColorManager.setColor(g, state, 0);
		if ((getState() == TGState.POINTER_ON_ME) ||  (getState() == TGState.POINTED)||  (getState() == TGState.MOVING)){
			g.drawRoundRect(x-1, y-h+2, width+2, height+2, 5, 5);
		}
		
		
	}
	
	public TGComponent isOnMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y - h + 2, width, height)) {
			return this;
		}
		return null;
	}
	
	public boolean isInRectangle(int x1, int y1, int width, int height) {
		if ((getX() < x1) || (getY() < y1) || ((getX() + this.width) > (x1 + width)) || ((getY() + this.height) > (y1 + height))) {
			//TraceManager.addDev("Not in my rectangle " + this);
			return false;
		} else {
			return true;
		}
	}
	
	public boolean editOndoubleClick(JFrame frame) {
		Vector attributes = tdp.getMGUI().getAllAttributes();
		Vector methods = tdp.getMGUI().getAllMethods();
		JDialogAvatarTransition jdat = new JDialogAvatarTransition(frame, "Setting transition parameters", guard, afterMin, afterMax, computeMin, computeMax, listOfActions, attributes, methods, filesToInclude, codeToInclude);
		jdat.setSize(700, 500);
		GraphicLib.centerOnParent(jdat);
		jdat.show(); // blocked until dialog has been closed
		
		
		if (jdat.hasBeenCancelled()) {
			return false;
		}
		
		guard = jdat.getGuard().trim();
		
		int index = guard.indexOf('[');
		if (index == -1) {
			guard = "[ " + guard + " ]";
		}
		
		afterMin = jdat.getAfterMin().trim();
		afterMax = jdat.getAfterMax().trim();
		computeMin = jdat.getComputeMin().trim();
		computeMax = jdat.getComputeMax().trim();
		filesToInclude = jdat.getFilesToInclude();
		codeToInclude =  jdat.getCodeToInclude();
		return true;
	}
	
	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<guard value=\"");
		sb.append(GTURTLEModeling.transformString(guard));
		sb.append("\" />\n");
		
		sb.append("<afterMin value=\"");
		sb.append(GTURTLEModeling.transformString(afterMin));
		sb.append("\" />\n");  
		
		sb.append("<afterMax value=\"");
		sb.append(GTURTLEModeling.transformString(afterMax));
		sb.append("\" />\n");
		
		sb.append("<computeMin value=\"");
		sb.append(GTURTLEModeling.transformString(computeMin));
		sb.append("\" />\n");  
		
		sb.append("<computeMax value=\"");
		sb.append(GTURTLEModeling.transformString(computeMax));
		sb.append("\" />\n");
		
		for(int i=0; i<listOfActions.size(); i++) {
			sb.append("<actions value=\"");
			sb.append(GTURTLEModeling.transformString(listOfActions.get(i)));
			sb.append("\" />\n");
		}
		
		if (filesToInclude != null) {
			for(int i=0; i<filesToInclude.length; i++) {
				sb.append("<filesToIncludeLine value=\"");
				sb.append(GTURTLEModeling.transformString(filesToInclude[i]));
				sb.append("\" />\n");
			}
		}
		
		if (codeToInclude != null) {
			for(int i=0; i<codeToInclude.length; i++) {
				sb.append("<codeToIncludeLine value=\"");
				sb.append(GTURTLEModeling.transformString(codeToInclude[i]));
				sb.append("\" />\n");
			}
		}
		
		sb.append("</extraparam>\n");
		return new String(sb);
	}
	
	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
		//System.out.println("*** load extra synchro *** " + getId());
		String tmpFilesToInclude = "";
		String tmpCodeToInclude = "";
		
		try {
			listOfActions = new Vector();
			
			NodeList nli;
			Node n1, n2;
			Element elt;
			String s;
			for(int i=0; i<nl.getLength(); i++) {
				n1 = nl.item(i);
				//System.out.println(n1);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					nli = n1.getChildNodes();
					for(int j=0; i<nli.getLength(); i++) {
						n2 = nli.item(i);
						//System.out.println(n2);
						if (n2.getNodeType() == Node.ELEMENT_NODE) {
							elt = (Element)n2;
							if (elt.getTagName().equals("guard")) {
								s = elt.getAttribute("value");
								if (s != null) {
									guard = s;
								}
							}
							if (elt.getTagName().equals("afterMin")) {
								s = elt.getAttribute("value");
								if (s != null) {
									afterMin = s;
								}
							}
							if (elt.getTagName().equals("afterMax")) {
								s = elt.getAttribute("value");
								if (s != null) {
									afterMax = s;
								}
							}
							if (elt.getTagName().equals("computeMin")) {
								s = elt.getAttribute("value");
								if (s != null) {
									computeMin = s;
								}
							}
							if (elt.getTagName().equals("computeMax")) {
								s = elt.getAttribute("value");
								if (s != null) {
									computeMax = s;
								}
							}
							if (elt.getTagName().equals("actions")) {
								s = elt.getAttribute("value");
								if (s != null) {
									listOfActions.add(s);
								}
							}
							
							if (elt.getTagName().equals("filesToIncludeLine")) {
                                //System.out.println("Analyzing line");
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                tmpFilesToInclude += GTURTLEModeling.decodeString(s) + "\n";
                            }
							
							if (elt.getTagName().equals("codeToIncludeLine")) {
                                //System.out.println("Analyzing line");
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                tmpCodeToInclude += GTURTLEModeling.decodeString(s) + "\n";
                            }
						}
					}
				}
			}
			
		} catch (Exception e) {
			throw new MalformedModelingException();
		}

		filesToInclude = Conversion.wrapText(tmpFilesToInclude);
		codeToInclude = Conversion.wrapText(tmpCodeToInclude);
	}
	
	public String getGuard() {
		return guard;
	}
	
	public String getAfterMinDelay() {
		return afterMin;
	}
	
	public String getAfterMaxDelay() {
		return afterMax;
	}
	
	public String getComputeMinDelay() {
		return computeMin;
	}
	
	public String getComputeMaxDelay() {
		return computeMax;
	}
	
	public Vector<String> getActions() {
		return listOfActions;
	}
	
	public String[] getFilesToInclude() {
		return filesToInclude;
	}
	
	public String[] getCodeToInclude() {
		return codeToInclude;
	}
}
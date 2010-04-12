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
 * Class AvatarBDPortConnector
 * Connector used in AVATAR Block Diagrams
 * Creation: 06/04/2010
 * @version 1.0 06/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarbd;


import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public  class AvatarBDPortConnector extends TGConnector implements ScalableTGComponent {
    //protected int arrowLength = 10;
    //protected int widthValue, heightValue, maxWidthValue, h;
	protected int c = 10; //square length 
	protected double oldScaleFactor;
	protected int fontSize = 10;
	protected int decY = 20;
	protected int decX = 6;
	
	protected LinkedList<String> inSignalsAtOrigin;
	protected LinkedList<String> outSignalsAtDestination;
	
	protected LinkedList<String> inSignalsAtDestination;
	protected LinkedList<String> outSignalsAtOrigin;
	
	
    
    public AvatarBDPortConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
        value = "";
        editable = true;
		oldScaleFactor = tdp.getZoom();
		inSignalsAtOrigin = new LinkedList<String>();
		inSignalsAtDestination = new LinkedList<String>();
		outSignalsAtOrigin = new LinkedList<String>();
		outSignalsAtDestination = new LinkedList<String>();
    }
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        /*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
		
		//g.drawLine(x1, y1, x2, y2);
		int cz = (int)(tdp.getZoom() * c);
		g.fillRect(x2-(cz/2), y2-(cz/2), cz, cz);
		g.fillRect(p1.getX()-(cz/2), p1.getY()-(cz/2), cz, cz);
		
		Point p = GraphicLib.intersectionRectangleSegment(x2-(cz/2), y2-(cz/2), cz, cz, x1, y1, x2, y2);
		if (p == null) {
			//System.out.println("null point");
		} else {
			g.drawLine(x1, y1, p.x, p.y);
		}
		
		Font f = g.getFont();
		Font fold = f;
		f = f.deriveFont((float)fontSize);
		g.setFont(f);
		int h = - decY;
		int step = fontSize + 1;
		int w;
		String s;
		
		// Signals at origin
		if (inSignalsAtOrigin.size() > 0) {
			//g.drawString("in:", p1.getX() + decX, p1.getY() + h);
			for(String iso: inSignalsAtOrigin) {
				h += step;
				s = getShortName(iso);
				if (p1.getX() <= p2.getX()) {
					g.drawString(s, p1.getX() + decX, p1.getY() + h);
				} else {
					w = g.getFontMetrics().stringWidth(s);
					g.drawString(s, p1.getX() - decX - w, p1.getY() + h);
				}
			}
		}
		if (outSignalsAtOrigin.size() > 0) {
			//h += step;
			//g.drawString("out:", p1.getX() + decX, p1.getY() + h);
			for(String oso: outSignalsAtOrigin) {
				h += step;
				s = getShortName(oso);
				if (p1.getX() <= p2.getX()) {
					g.drawString(s, p1.getX() + decX, p1.getY() + h);
				} else {
					w = g.getFontMetrics().stringWidth(s);
					g.drawString(s, p1.getX() - decX - w, p1.getY() + h);
				}
			}
		}
		// Signals at destination
		h = - decY;
		if (inSignalsAtDestination.size() > 0) {
			//g.drawString("in:", p2.getX() + decX, p2.getY() + h);
			for(String isd: inSignalsAtDestination) {
				h += step;
				s = getShortName(isd);
				if (p1.getX() > p2.getX()) {
					g.drawString(s, p2.getX() + decX, p2.getY() + h);
				} else {
					w = g.getFontMetrics().stringWidth(s);
					g.drawString(s, p2.getX() - decX - w, p2.getY() + h);
				}
			}
		}
		if (outSignalsAtDestination.size() > 0) {
			//h += step;
			//g.drawString("out:", p2.getX() + decX, p2.getY() + h);
			for(String osd: outSignalsAtDestination) {
				h += step;
				s = getShortName(osd);
				if (p1.getX() > p2.getX()) {
					g.drawString(s, p2.getX() + decX, p2.getY() + h);
				} else {
					w = g.getFontMetrics().stringWidth(s);
					g.drawString(s, p2.getX() - decX - w, p2.getY() + h);
				}
			}
		}
		g.setFont(fold);
		
		/*if (value.length() > 0) {
			Font f = g.getFont();
			if (tdp.getZoom() < 1) {
				Font f0 =  f.deriveFont((float)(fontSize*tdp.getZoom()));
				g.setFont(f0);
			}
			g.drawString(value, x2-(cz/2), y2-(cz/2)-1);
			g.setFont(f);
		}*/
		
		
	
    }
	
	public AvatarBDBlock getAvatarBDBlock1() {
		return (AvatarBDBlock)(tdp.getComponentToWhichBelongs(p1));
	}
	
	public AvatarBDBlock getAvatarBDBlock2() {
		return (AvatarBDBlock)(tdp.getComponentToWhichBelongs(p2));
	}
	
	public boolean editOndoubleClick(JFrame frame) {
		// Gets the two concerned blocks
	
		AvatarBDBlock block1 = getAvatarBDBlock1();
		AvatarBDBlock block2 = getAvatarBDBlock2();
		Vector v = getAssociationSignals();
		
		JDialogSignalAssociation jdas = new JDialogSignalAssociation(frame, block1, block2, v, this, "Setting signal association");
        jdas.setSize(750, 400);
        GraphicLib.centerOnParent(jdas);
        jdas.show(); // blocked until dialog has been closed
		
		if (jdas.hasBeenCancelled()) {
			return false;
		}
		
		inSignalsAtOrigin.clear();
		inSignalsAtDestination.clear();
		outSignalsAtOrigin.clear();
		outSignalsAtDestination.clear();
		
		String assoc;
		AvatarSignal as1, as2;
		int index;
		for(int i=0; i<v.size(); i++) {
			assoc = (String)(v.get(i));
			as1 = block1.getSignalNameBySignalDef(getFirstSignalOfSignalAssociation(assoc));
			as2 = block2.getSignalNameBySignalDef(getSecondSignalOfSignalAssociation(assoc));
			
			if ((as1 != null) && (as2 != null)) {
				index = assoc.indexOf("->");
				if (index > -1) {
					outSignalsAtOrigin.add(as1.toString());
					inSignalsAtDestination.add(as2.toString());
				} else {
					inSignalsAtOrigin.add(as1.toString());
					outSignalsAtDestination.add(as2.toString());
				}
			}
		}
		return true;
    }
	
	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		for(String iso: inSignalsAtOrigin) {
			sb.append("<iso value=\"");
            sb.append(iso);
            sb.append("\" />\n");
		}       
		for(String osd: outSignalsAtDestination) {
			sb.append("<osd value=\"");
            sb.append(osd);
            sb.append("\" />\n");
		}
		for(String isd: inSignalsAtDestination) {
			sb.append("<isd value=\"");
            sb.append(isd);
            sb.append("\" />\n");
		}
		for(String oso: outSignalsAtOrigin) {
			sb.append("<oso value=\"");
            sb.append(oso);
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
			String val;
            
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
                            if (elt.getTagName().equals("iso")) {
								val = elt.getAttribute("value");
                                
                                if ((val != null) && (!(val.equals("null")))) {
                                    inSignalsAtOrigin.add(val);
                                }
							}
							if (elt.getTagName().equals("osd")) {
								val = elt.getAttribute("value");
                                
                                if ((val != null) && (!(val.equals("null")))) {
                                    outSignalsAtDestination.add(val);
                                }
							}
							if (elt.getTagName().equals("isd")) {
								val = elt.getAttribute("value");
                                
                                if ((val != null) && (!(val.equals("null")))) {
                                    inSignalsAtDestination.add(val);
                                }
							}
							if (elt.getTagName().equals("oso")) {
								val = elt.getAttribute("value");
                                
                                if ((val != null) && (!(val.equals("null")))) {
                                    outSignalsAtOrigin.add(val);
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
    
    
    public int getType() {
        return TGComponentManager.AVATARBD_PORT_CONNECTOR;
    }
	
	public void rescale(double scaleFactor){
		//System.out.println("Rescale connector");
		int xx, yy;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			xx = tgcomponent[i].getX();
			yy = tgcomponent[i].getY();
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
			tgcomponent[i].dx = (tgcomponent[i].dx + xx) / oldScaleFactor * scaleFactor;
			tgcomponent[i].dy = (tgcomponent[i].dy + yy) / oldScaleFactor * scaleFactor;
			xx = (int)(tgcomponent[i].dx);
			tgcomponent[i].dx = tgcomponent[i].dx - xx;
			yy = (int)(tgcomponent[i].dy);
			tgcomponent[i].dy = tgcomponent[i].dy - yy;
			
			tgcomponent[i].setCd(xx, yy);
			
			//System.out.println("Internal comp xx= " + xx + "  y==" + yy);
        }
		
		oldScaleFactor = scaleFactor;
	}
	
	public LinkedList getListOfSignalsOrigin() {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(inSignalsAtOrigin);
		list.addAll(outSignalsAtOrigin);
		return list;
	}
	
	public LinkedList getListOfSignalsDestination() {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(inSignalsAtDestination);
		list.addAll(outSignalsAtDestination);
		return list;
	}
	
	public Vector getAssociationSignals() {
		AvatarBDBlock block1 = getAvatarBDBlock1();
		AvatarBDBlock block2 = getAvatarBDBlock2();
		
		int i;
		Vector v = new Vector();
		String s;
		
		for(i=0; i<outSignalsAtOrigin.size(); i++) {
			try {
				s = makeSignalAssociation(block1, block1.getAvatarSignalFromFullName(outSignalsAtOrigin.get(i)), block2, block2.getAvatarSignalFromFullName(inSignalsAtDestination.get(i)));
				v.add(s);
			} catch (Exception e) {
				// Probably a signal has been removed
			}
		}
		
		for(i=0; i<inSignalsAtOrigin.size(); i++) {
			try {
				s = makeSignalAssociation(block1, block1.getAvatarSignalFromFullName(inSignalsAtOrigin.get(i)), block2, block2.getAvatarSignalFromFullName(outSignalsAtDestination.get(i)));
				v.add(s);
			} catch (Exception e) {
				// Probably a signal has been removed
			}
		}
		
		return v;
	}
	
	public static String makeSignalAssociation(AvatarBDBlock _block1, AvatarSignal _as1, AvatarBDBlock _block2, AvatarSignal _as2) {
		String s = _block1.getBlockName() + "." + _as1.toBasicString();
		if (_as1.getInOut() == AvatarSignal.OUT) {
			s += " -> ";
		} else {
			s += " <- ";
		}
		s += _block2.getBlockName() + "." + _as2.toBasicString();
		return s;
	}
	
	public String getFirstSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf(".");
		
		
		if (index0 == -1) {
			return null;
		}
		
		int index1 = _assoc.indexOf("->");
		int index2 = _assoc.indexOf("<-");
		
		index1 = Math.max(index1, index2);
		if (index1 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, index1).trim();
	}
	
	public String getSecondSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf("->");
		int index1 = _assoc.indexOf("<-");
		
		if ((index0 == -1) && (index1 == -1)) {
			return null;
		}
		
		index0 = Math.max(index0, index1);
		_assoc = _assoc.substring(index0+2, _assoc.length());
		
		index0 = _assoc.indexOf(".");
		
		if (index0 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, _assoc.length()).trim();
	}
	
	public void updateAllSignals() {
		try {
			Vector v = getAssociationSignals();
			inSignalsAtOrigin.clear();
			inSignalsAtDestination.clear();
			outSignalsAtOrigin.clear();
			outSignalsAtDestination.clear();
			if (v.size() == 0) {
				return;
			}
			
			AvatarBDBlock block1 = getAvatarBDBlock1();
			AvatarBDBlock block2 = getAvatarBDBlock2();
			
			String assoc;
			AvatarSignal as1, as2;
			int index;
			for(int i=0; i<v.size(); i++) {
				assoc = (String)(v.get(i));
				TraceManager.addDev("assoc=" + assoc);
				as1 = block1.getSignalNameBySignalDef(getFirstSignalOfSignalAssociation(assoc));
				as2 = block2.getSignalNameBySignalDef(getSecondSignalOfSignalAssociation(assoc));
				
				if ((as1 != null) && (as2 != null)) {
					index = assoc.indexOf("->");
					if (index > -1) {
						outSignalsAtOrigin.add(as1.toString());
						inSignalsAtDestination.add(as2.toString());
					} else {
						inSignalsAtOrigin.add(as1.toString());
						outSignalsAtDestination.add(as2.toString());
					}
				} else {
					TraceManager.addDev("null signals: removing assoc");
				}
			}
		} catch (Exception e) {
			TraceManager.addDev("Exception on connector");
			// Probably the model is not yet fully loaded...
		}
	}
	
	// remove the parameters in the name of a signal
	public String getShortName(String _s) {
		int index = _s.indexOf('(');
		if (index == -1) {
			return _s;
		}
		
		return _s.substring(0, index).trim();
	}
	
	
    
}

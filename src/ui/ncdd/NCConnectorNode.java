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
* Class NCConnectorNode
* Connector used in NC diagrams
* Creation: 18/11/2008
* @version 1.0 18/11/2008
* @author Ludovic APVRILLE
* @see
*/

package ui.ncdd;



import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public  class NCConnectorNode extends TGConnector implements WithAttributes {
    protected int arrowLength = 10;
    protected int widthValue, heightValue, maxWidthValue, h;
	
	protected boolean hasCapacity = false;
	protected int capacity = 10;
	
	protected boolean hasParameter = false;
	protected int parameter = 1;
	
	protected String capacityUnit = "Mbs";
	protected String interfaceName;
	
    
    public NCConnectorNode(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        myImageIcon = IconManager.imgic202;
		
        editable = true;
		interfaceName = tdp.findNodeName("i");
		makeValue();
    }
	
	public void makeValue() {
		value =  "{" + interfaceName;
			if (hasCapacity) {
				value +=", capacity = " + capacity +  " " + capacityUnit;
			} 
			if (hasParameter) {
				value += ", parameter = " + parameter;
			}
		value += "}";
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
        g.drawLine(x1, y1, x2, y2);
		/*if (Point2D.distance(x1, y1, x2, y2) < GraphicLib.longueur * 1.5) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
        }*/
		
		h  = g.getFontMetrics().getHeight();
        if (!tdp.isScaled()) {
            maxWidthValue = 0;
            heightValue = h;
        }
        
		
		widthValue = g.getFontMetrics().stringWidth(value);
		if (!tdp.isScaled()) {
			maxWidthValue = Math.max(maxWidthValue, widthValue);
		}
		g.drawString(value, ((p1.getX() + p2.getX()) / 2)-widthValue/2, ((p1.getY() + p2.getY()) / 2) - (h/2));
    }
	
	public boolean editOndoubleClick(JFrame frame) {
        //System.out.println("Double click");
        int oldCapacity = capacity;
		int oldParameter = parameter;
        String oldInterfaceName = interfaceName;
		String tmp;
		String interfaceNameTmp;
        
        JDialogLinkNCNode jdlncn = new JDialogLinkNCNode(frame, "Setting link parameters", hasCapacity, capacity, capacityUnit, hasParameter, parameter, interfaceName);
        jdlncn.setSize(350, 250);
        GraphicLib.centerOnParent(jdlncn);
        jdlncn.show(); // Blocked until dialog has been closed
		
        interfaceNameTmp = jdlncn.getInterfaceName().trim();
		tmp = jdlncn.getCapacity();
		try {
			capacity = Integer.decode(tmp).intValue();
		} catch (Exception e) {
			capacity = oldCapacity;
		}
		
		tmp = jdlncn.getParameter();
		try {
			parameter = Integer.decode(tmp).intValue();
		} catch (Exception e) {
			parameter = oldParameter;
		}
        
        if ((interfaceNameTmp == null) || (jdlncn.hasBeenCancelled())) {
			return !jdlncn.hasBeenCancelled();
        }
		
		if ((interfaceNameTmp != null) && (interfaceNameTmp.length() > 0) && (!interfaceNameTmp.equals(oldInterfaceName))) {
			//boolean b;
            if (!TAttribute.isAValidId(interfaceNameTmp, false, false)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Equipment: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (!tdp.isNCNameUnique(interfaceNameTmp)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Equipment: the new name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
		}
        
        if ((capacity < 0) || (jdlncn.hasBeenCancelled())){
            capacity = oldCapacity;
        }
        
        if (!jdlncn.hasBeenCancelled()) {
			interfaceName = interfaceNameTmp;
			capacityUnit = jdlncn.getCapacityUnit();
			hasCapacity = jdlncn.hasCapacity();
			hasParameter = jdlncn.hasParameter();
            makeValue();
        }
		
        return !jdlncn.hasBeenCancelled();
    }
    
    
    public int getType() {
        return TGComponentManager.CONNECTOR_NODE_NC;
    }
	
	public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        //System.out.println("Extra");
        if (GraphicLib.isInRectangle(x1, y1, ((p1.getX() + p2.getX()) / 2)-maxWidthValue/2, ((p1.getY() + p2.getY()) / 2) - h/2 -heightValue + 2, maxWidthValue, heightValue)) {
            return this;
        }
        return null;
    }
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info capacity=\"");
		sb.append(capacity);
        sb.append("\" capacityUnit=\"");
		sb.append(capacityUnit);
		sb.append("\" hasCapacity=\"");
		sb.append(hasCapacity);
		sb.append("\" parameter=\"");
		sb.append(parameter);
		sb.append("\" hasParameter=\"");
		sb.append(hasParameter);
		sb.append("\" interfaceName=\"");
		sb.append(interfaceName);
		sb.append("\"/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
			String prio;
			String unit;
			String has;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
								prio = elt.getAttribute("capacity");
								if (elt != null) {
									capacity = Integer.decode(prio).intValue();
								}
								prio = elt.getAttribute("interfaceName");
								if (elt != null) {
									interfaceName = prio;
								}
								
								unit = elt.getAttribute("capacityUnit");
								if ((unit != null) && (unit.length() > 0)){
									capacityUnit = unit;
								}
								
								has =  elt.getAttribute("hasCapacity");
								if ((has != null) && (has.length() > 0)){
									if (has.equals("true")) {
										hasCapacity = true;
									} else {
										hasCapacity = false;
									}
								}
								
								// for backward compatiblity: exception catching
								try {
									prio = elt.getAttribute("parameter");
									if (elt != null) {
										parameter = Integer.decode(prio).intValue();
									}
									
									has =  elt.getAttribute("hasParameter");
									if ((has != null) && (has.length() > 0)){
										if (has.equals("true")) {
											hasParameter = true;
										} else {
											hasParameter = false;
										}
									}
								} catch (Exception e) {
								}
								
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
			System.out.println("Error in NCNodeLink");
            throw new MalformedModelingException();
        }
    }
	
	
	public int getCapacity() {
		return capacity;
	}
	
	public boolean hasCapacity() {
		return hasCapacity;
	}
	
	public int getParameter() {
		return parameter;
	}
	
	public boolean hasParameter() {
		return hasParameter;
	}
	
	public String getCapacityUnit() {
		return capacityUnit;
	}
    
	public String getAttributes() {
		String ret = "";
		if (hasCapacity) {
			ret += "Capacity = " + capacity + " " + capacityUnit + "\n";
		}
		if (hasParameter) {
			ret +="Parameter = " + parameter + "\n";
		}
		return ret;
	}
	
	
}

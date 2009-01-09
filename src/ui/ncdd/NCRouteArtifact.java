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
 * Class NCRouteArtifact
 * Route artifact of a network calculus diagram
 * Creation: 19/11/2008
 * @version 1.0 19/11/2008
 * @author Ludovic APVRILLE
 * @see
 */

package ui.ncdd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

public class NCRouteArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int textY2 =  40;
    protected int space = 5;
    protected int fileX = 15;
    protected int fileY = 20;
    protected int cran = 5;
	
    protected String oldValue = "";
	
	protected ArrayList<NCRoute> routes;
    
    public NCRouteArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 60;
        height = 38;
        minWidth = 60;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = tdp.findNodeName("R");
		routes = new ArrayList<NCRoute>();
        
        myImageIcon = IconManager.imgic702;
    }
    
    public void internalDrawing(Graphics g) {
        
        if (oldValue.compareTo(value) != 0) {
            setValue(value, g);
        }
        
        g.drawRect(x, y, width, height);
		Color c = g.getColor();
		g.setColor(ColorManager.CPU_BOX_2);
		g.fillRect(x+1, y+1, width-1, height-1);
        g.setColor(c);
		
        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-cran, y+space);
        g.drawLine(x+width-space-cran, y+space, x+width-space, y+space + cran);
        g.drawLine(x+width-space, y+space + cran, x+width-space, y+space+fileY);
        g.drawLine(x+width-space, y+space+fileY, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-cran, y+space, x+width-space-cran, y+space+cran);
        g.drawLine(x+width-space-cran, y+space+cran, x + width-space, y+space+cran);
        
        g.drawString(value, x + textX , y + textY);
        
    }
    
    public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);
		int w1 = Math.max(minWidth, w + 2 * textX + fileX + space);
		
        //System.out.println("width=" + width + " w1=" + w1 + " w2=" + w2 + " value=" + value);
        if (w1 != width) { 
            width = w1;
            resizeWithFather();
        }
        //System.out.println("width=" + width + " w1=" + w1 + " value=" + value);
    }
    
    public void resizeWithFather() {
        if ((father != null) && ((father instanceof NCEqNode) || (father instanceof NCSwitchNode))) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
    
    
     public boolean editOndoubleClick(JFrame frame) {
		String tmp;
		boolean error = false;
		
		Vector<NCRoute> vroutes = new Vector<NCRoute>(routes);
		
		ArrayList<String> inputInterfaces = ((NCDiagramPanel)tdp).getInterfaces((NCSwitchNode)(getFather()));
		ArrayList<String> traffics = ((NCDiagramPanel)tdp).getTraffics();
		ArrayList<String> outputInterfaces = (ArrayList<String>)(inputInterfaces.clone());
		
		JDialogNCRoute dialog = new JDialogNCRoute(frame, "Setting route attributes", value, vroutes, inputInterfaces, traffics, outputInterfaces);
		dialog.setSize(900, 500);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (dialog.hasBeenCancelled()) {
			return false;
		}
		
		tmp = dialog.getValue().trim();
		
		if (tmp == null) {
			return false;
		}
		
		if ((tmp != null) && (tmp.length() > 0) && (!tmp.equals(oldValue))) {
			if (!TAttribute.isAValidId(tmp, false, false)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Route: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                error = true;
            }
            
            if (!tdp.isNCNameUnique(tmp)) {
                JOptionPane.showMessageDialog(frame,
					"Could not change the name of the Route: the new name is already in use",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
                error = true;
            }
		}
		
		if (!error) {
			value = tmp;
		}
		
		routes = new ArrayList<NCRoute>();
		routes.addAll(vroutes);
			
		return true;
      
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.NCDD_ROUTE_ARTIFACT;
    }
    
   protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		for(NCRoute route: routes) {
			sb.append("<route inputInterface=\"");
			sb.append(route.inputInterface);
			sb.append("\" traffic=\"");
			sb.append(route.traffic);
			sb.append("\" outputInterface=\"");
			sb.append(route.outputInterface);
			sb.append("\" />\n");
		}
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
            String s0 = null, s1 = null, s2 = null;
			NCRoute route;
            
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
                            if (elt.getTagName().equals("route")) {
                                s0 = elt.getAttribute("inputInterface");
                                s1 = elt.getAttribute("traffic");
								s2 = elt.getAttribute("outputInterface");
								route = new NCRoute();
								route.inputInterface = s0;
								route.traffic = s1;
								route.outputInterface = s2;
								routes.add(route);
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
			System.out.println("Decoding route: failed");
            throw new MalformedModelingException();
        }
        //makeFullValue();
    }
    
    
    public ArrayList<NCRoute> getRoutes() {
        return routes;
    }
	
	
	public String getAttributes() {
		String ret = "";
		
		for(NCRoute route: routes) {
			ret += "route: " + route.toString() + "\n";
		}
		
		return ret;
	}

    
}

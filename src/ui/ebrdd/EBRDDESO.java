/**
Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
* Class EBRDDESO
* Event Sequencing operator. To be used in EBRDDs
* Creation: 09/09/2009
* @version 1.0 09/09/2009
* @author Ludovic APVRILLE
* @see
*/

package ui.ebrdd;

import java.awt.*;
import javax.swing.*;

import myutil.*;
import ui.*;
import ui.window.*;

import org.w3c.dom.*;

public class EBRDDESO extends TGCWithoutInternalComponent implements SwallowedTGComponent {
    private int lineLength = 0;
	private int textX, textY;
	
	// Type
	public final static String [] ESOS = {"Conjunction", "Disjunction", "Sequence", "Strict sequence", "Simultaneous", "At least/At most"};
	
	protected int id;
	protected int timeout;
	protected boolean oncePerEvent;
	protected int n, m;
	
	
	public EBRDDESO(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
		
		
		width = 150;
		height = 5;
		
		textX = width - 6;
		textY = height + 2;
		
		nbConnectingPoint = 10;
		connectingPoint = new TGConnectingPoint[10];
		connectingPoint[0] = new TGConnectingPointEBRDDERC(this, 0, -lineLength, true, false, 0.5, 0.0);
		connectingPoint[1] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.1, 1.0);
		connectingPoint[2] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.2, 1.0);
		connectingPoint[3] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.3, 1.0);
		connectingPoint[4] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.4, 1.0);
		connectingPoint[5] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.5, 1.0);
		connectingPoint[6] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.6, 1.0);
		connectingPoint[7] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.7, 1.0);
		connectingPoint[8] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.8, 1.0);
		connectingPoint[9] = new TGConnectingPointEBRDDERC(this, 0, lineLength, false, true, 0.9, 1.0);
		
		nbInternalTGComponent = 0;
		
		moveable = true;
		editable = true;
		removable = true;
		
		setValue(ESOS[id] + "(" + timeout + ", " + oncePerEvent + ", " + n + ", " + m + ")");
		name = "ESO";
		id = 0;
		
		myImageIcon = IconManager.imgic1054;
	}
    
    public void internalDrawing(Graphics g) {
		g.drawRect(x, y, width, height);
		g.fillRect(x, y, width, height);
		Font f = g.getFont();
		int w = g.getFontMetrics().stringWidth(ESOS[id]);
		g.drawString(ESOS[id], x+width-w, y-2);
	}
	
	public TGComponent isOnMe(int x1, int y1) {
		if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
			return this;
		}
		return null;
	}
	public boolean editOndoubleClick(JFrame frame) {
		boolean error = false;
		String errors = "";
		int tmp;
		String tmpName;
        
		JDialogESO dialog = new JDialogESO(frame, this);
		dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		int _n = 0, _m = 0, _timeout = 0;
		String val;
		
		try {
			val = dialog.getTimeout();
			_timeout = Integer.decode(val).intValue();
		} catch (Exception e) {
			error = true;
			errors += "timeout ";
		}
		
		try {
			val = dialog.getN();
			_n = Integer.decode(val).intValue();
		} catch (Exception e) {
			error = true;
			errors += "n ";
		}
		
		try {
			val = dialog.getM();
			_m = Integer.decode(val).intValue();
		} catch (Exception e) {
			error = true;
			errors += "m ";
		}
		
		if (error) {
			JOptionPane.showMessageDialog(frame,
                "Invalid value for the following attributes: " + errors,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
		}
		
		id = dialog.getID();
		timeout = _timeout;
		oncePerEvent = dialog.getOncePerEvent();
		n = _n;
		m = _m;
		
		setValue(ESOS[id] + "(" + timeout + ", " + oncePerEvent + ", " + n + ", " + m + ")");
		
		return true;
	}
	
    public int getType() {
        return TGComponentManager.EBRDD_ESO;
    }
    
    public int getDefaultConnector() {
		return TGComponentManager.CONNECTOR_EBRDD_ERC;
    }  
	
	public void resizeWithFather() {
        if ((father != null) && (father instanceof EBRDDERC)) {
            //System.out.println("cdRect comp");
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }
	
	 protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<attributes id=\"" + id + "\" ");
		sb.append(" timeout=\"" + timeout + "\" ");
		sb.append(" oncePerEvent=\"" + oncePerEvent + "\" ");
		sb.append(" n=\"" + n + "\" ");
		sb.append(" m=\"" + m + "\"");
        sb.append("/>\n");
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
            String sstereotype = null, snodeName = null;
            
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
							
							if (elt.getTagName().equals("attributes")) {
								id = Integer.decode(elt.getAttribute("id")).intValue();
                                timeout = Integer.decode(elt.getAttribute("timeout")).intValue();
                                n = Integer.decode(elt.getAttribute("n")).intValue();
								m = Integer.decode(elt.getAttribute("m")).intValue();
								oncePerEvent = elt.getAttribute("m").equals("true");
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
		setValue(ESOS[id] + "(" + timeout + ", " + oncePerEvent + ", " + n + ", " + m + ")");
		
    }
	
	public static boolean hasTimeout(int _id) {
		return true;
	}
	
	public static boolean hasOncePerEvent(int _id) {
		switch(_id) {
		case 0:
			return true;
		case 1:
			return false;
		case 2:
			return  true;
		case 3:
			return false;
		case 4:
			return false;
		case 5:
			return true;
		}
		return false;
	}
	
	public static boolean hasNM(int _id) {
		return (_id == 5);
	}
	
	public int getID() {
		return id;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public boolean getOncePerEvent() {
		return oncePerEvent;
	}
	
	public int getN() {
		return n;
	}
	
	public int getM() {
		return m;
	}
    
}

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




package ui.avatarbd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAvatarFirewall;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Class AvatarBDFirewall
 * Node. To be used in TML architecture diagrams.
 * Creation: 17/10/2016
 * @version 1.0 17/10/2016
 * @author Letitia LI
 */
public class AvatarBDFirewall extends TGCScalableWithoutInternalComponent {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "FIREWALL";
    private ArrayList<String> rules = new ArrayList<String>();
	private int latency = 0;
    
    public AvatarBDFirewall(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 250;
        height = 100;
        minWidth = 100;
        minHeight = 35;
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);

        connectingPoint[8] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new AvatarBDConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        name = tdp.findNodeName("Firewall");
		value = "name";
        
        myImageIcon = IconManager.imgic700;
    }
    
    public void internalDrawing(Graphics g) {
		Color c = g.getColor();
		g.draw3DRect(x, y, width, height, true);
		
        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);
        
        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);
		
		// Filling color
		g.setColor(ColorManager.FIREWALL_BOX);
		g.fill3DRect(x+1, y+1, width-1, height-1, true);
		g.setColor(c);
        
        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        g.drawString(ster, x + (width - w)/2, y + textY1);
        w  = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (width - w)/2, y + textY2);
		
		// Icon
		
		g.drawImage(IconManager.imgic7001.getImage(), x + 4, y + 4, null);
		g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        
        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
            return this;
        }
        
        return null;
    }
    
    public String getStereotype() {
        return stereotype;
        
    }
    
    public String getNodeName() {
        return name;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
		boolean error = false;
		String errors = "";
		String tmpName;

		JDialogAvatarFirewall dialog = new JDialogAvatarFirewall(frame, "Setting firewall attributes", this);
		//dialog.setSize(600, 600);
        GraphicLib.centerOnParent(dialog, 600, 600);
        dialog.setVisible( true ); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getNodeName().length() != 0) {
			tmpName = dialog.getNodeName();
			tmpName = tmpName.trim();
			 if (!TAttribute.isAValidId(tmpName, false, false)) {
                error = true;
				errors += "Name of the node  ";
			 } else {
				 name = tmpName;
			 }
		}
		latency = Integer.valueOf(dialog.getLatency());
		
		rules = dialog.getRules();
		
		if (error) {
			JOptionPane.showMessageDialog(frame,
                "Invalid value for the following attributes: " + errors,
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
                return false;
		}
		
        return true;
    }
    
    
    public int getType() {
        return TGComponentManager.TMLARCHI_FIREWALL;
    }
    
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
	sb.append("<attributes latency=\"" + latency + "\" ");
        sb.append("/>\n");
	for (String rule:rules){
	sb.append("<rule value=\"" + rule + "\" ");
        sb.append("/>\n");
	}
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
       //     int t1id;
            String sstereotype = null, snodeName = null;
            
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
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            } 
                            if (snodeName != null){
                                name = snodeName;
                            }
							
							if (elt.getTagName().equals("attributes")) {
                                latency = Integer.decode(elt.getAttribute("latency")).intValue();
                            }
						    if (elt.getTagName().equals("rule")){
						    	rules.add(elt.getAttribute("value"));
						    }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }
    
	  
	  public int getLatency(){
		  return latency;
	  }
	public ArrayList<String> getRules(){
	    return rules;
	}
	  public String getAttributes() {
		  String attr = "";
		  attr += "latency = " + latency + "\n";
		  return attr;
	  }
	   

	public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
}

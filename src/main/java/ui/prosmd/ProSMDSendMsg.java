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




package ui.prosmd;

import myutil.Conversion;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * Class ProSMDSendMsg
 * Action of sending a message
 * Creation: 05/07/2006
 * @version 1.0 05/07/2006
 * @author Ludovic APVRILLE
 */
public class ProSMDSendMsg extends TGCOneLineText {
    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;
    protected String viaPort="";
    
    public ProSMDSendMsg(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 30;
        height = 20;
        minWidth = 30;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointProSMD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointProSMD(this, 0, lineLength, false, true, 0.5, 1.0);
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "send msg";
        value = "gate";
        
        myImageIcon = IconManager.imgic900;
    }
    
    public void internalDrawing(Graphics g) {
      
    	  if (this.x<=0)
			  this.x=1;
		  if (this.y<=0)
			  this.y=1;
    	
    	int w  = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }
        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        g.drawLine(x, y, x+width-linebreak, y);
        g.drawLine(x, y+height, x+width-linebreak, y+height);
        g.drawLine(x, y, x, y+height);
        g.drawLine(x+width-linebreak, y, x+width, y+height/2);
        g.drawLine(x+width-linebreak, y+height, x+width, y+height/2);
        
        //g.drawString("chl", x+(width-w) / 2, y);
        g.drawString(value, x + (width - w) / 2 , y + textY);
        if (!viaPort.equals("")) 
        {
           g.setColor(Color.BLUE);
       	g.drawString("(via "+viaPort+")", x + textX -5, y + 2*textY+3);	
           g.setColor(Color.BLACK);
        }
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
  
    public boolean editOndoubleClick(JFrame frame) {
        super.editOndoubleClick(frame);
	String oldViaPort = viaPort;
    String text = "Via port: ";
    
    String s = (String)JOptionPane.showInputDialog(frame, text,
    "setting via port", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
    null,
    viaPort);
    
    if (s != null) {
        s = Conversion.removeFirstSpaces(s);
    }
    
    if ((s != null) && (s.length() > 0) && (!s.equals(oldViaPort))) {
        setViaPort(s);
         //System.out.println("Value ok");
        return true;
    }
     

    return false;

}


    public String getViaPort()
    {
    	return this.viaPort;
    	
    }

     public void setViaPort(String p)
     {
    	 viaPort=p;
    	 
     }
    public String getAction() {
        return value;
    }
    

    public int getType() {
        return TGComponentManager.PROSMD_SENDMSG;
    }
    
    protected String translateExtraParam() {
    	  
    	StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Via port=\"");
        sb.append(viaPort);
        sb.append("\" />\n");
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
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Via")) {
                            	 viaPort = elt.getAttribute("port");
                                
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
     
    }
    
    

    
}
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




package ui.tmlad;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogCryptographicConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Class TMLADEncrypt
 * Create security pattern and encrypt. To be used in TML activity diagrams
 * Creation: 21/11/2005
 * @version 1.0 21/11/2005
 * @author Ludovic APVRILLE
 */
public class TMLADEncrypt extends TGCWithoutInternalComponent implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    private int lineLength = 5;
  //  private int textX, textY;
    private int ex=5;
	private int textHeight=8;
    private int ilength = 12;
    private int lineLength1 = 3;
    public String type="";
    public String message_overhead="";
    public String size="";
    public String securityContext="";
    public String encTime="100";
    public String decTime="100";	
    public String key ="";
    public String nonce="";
    public String formula="";
    public String algorithm="";
    protected int stateOfError = 0; // Not yet checked
    
    public TMLADEncrypt(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 15;
        height = 35;
    //    textX = width + 5;
//        textY = height/2 + 5;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, + lineLength+ex, false, true, 0.5, 1.0);
        
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "encrypt";
        
        myImageIcon = IconManager.imgic214;
    }
    
    public void internalDrawing(Graphics g) {
		if (stateOfError > 0)  {
		    Color c = g.getColor();
		    switch(stateOfError) {
				case ErrorHighlight.OK:
				    g.setColor(ColorManager.EXEC);
			    break;
				default:
				    g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
	    	}
	    	g.fillRect(x, y, width, height);
	    	int[] xP = new int[]{x,x+width,x+width/2};
	    	int[] yP = new int[]{y+height, y+height, y+height+ex};
	    	g.fillPolygon(xP,yP,3);
	    	g.setColor(c);
		}	
        g.drawLine(x, y, x+width, y);
        g.drawLine(x, y, x, y+height);
		g.drawLine(x+width, y, x+width, y+height);
        g.drawLine(x, y+height, x+width/2, y+height+ex);
        g.drawLine(x+width/2, y+height+ex, x+width, y+height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height+ex, x+(width/2), y + lineLength + height+ex);

		if (type.equals("Symmetric Encryption")){
	    	//S
	    	g.drawLine(x + ex, y+(height-ilength)/4,  x + width -ex, y+(height-ilength)/4);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength,  x + width-ex, y+(height-ilength)/4 + ilength);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength/2,  x + width-ex, y+(height-ilength)/4 + ilength/2);
	   	    g.drawLine(x + ex, y+(height-ilength)/4, x + ex, y+(height-ilength)/4 + ilength/2);
		    g.drawLine(x + width-ex, y+(height-ilength)/4+ilength/2, x + width -ex, y+(height-ilength)/4 + ilength);
		    //E
		    g.drawLine(x + ex, y+(height-ilength)/4 + height/2 - ex/2,  x + width -ex, y+(height-ilength)/4 + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength + height/2 -ex/2,  x + width -ex, y+(height-ilength)/4 + ilength + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength/2 + height/2 - ex/2,  x + width-ex, y+(height-ilength)/4 + ilength/2 + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + height/2 - ex/2, x + ex, y+(height-ilength)/4 + ilength +  height/2 -ex/2);
		}

		else if (type.equals("Asymmetric Encryption")) {
		    //A
		    g.drawLine(x + (width/2), y+(height-ilength)/4,  x + ex, y+(height-ilength)/4+ilength);
            g.drawLine(x + (width/2), y+(height-ilength)/4,  x + (width) - ex, y+(height-ilength)/4 + ilength);
            g.drawLine(x + 3*ex/2, y+(height-ilength)/4 + ilength/2 + ex/2,  x + width - 3*ex/2, y+(height-ilength)/4 + ilength/2+ex/2);
		    //E
			g.drawLine(x + ex, y+(height-ilength)/4 + height/2 - ex/2,  x +width-ex, y+(height-ilength)/4 + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength + height/2 -ex/2,  x + width -ex, y+(height-ilength)/4 + ilength + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + ilength/2 + height/2 - ex/2,  x + width -ex, y+(height-ilength)/4 + ilength/2 + height/2 -ex/2);
            g.drawLine(x + ex, y+(height-ilength)/4 + height/2 - ex/2, x + ex, y+(height-ilength)/4 + ilength +  height/2 -ex/2);
		}
		else if (type.equals("Nonce")){
		    //N
		    g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2,  x + (width/2) - lineLength1, y+(height-ilength)/2+ilength);
	   	    g.drawLine(x + (width/2) + lineLength1, y+(height-ilength)/2,  x + (width/2) + lineLength1, y+(height-ilength)/2+ilength);
		    g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2, x + (width/2) + lineLength1, y+(height-ilength)/2+ilength);
		}
		else if (type.equals("MAC")){
		    //M
		    g.drawLine(x + ex/2+1, y+(height-ilength)/2,  x + ex/2+1, y+(height-ilength)/2+ilength);
	   	    g.drawLine(x + width -ex/2-1, y+(height-ilength)/2,  x + width - ex/2-1, y+(height-ilength)/2+ilength);
		    g.drawLine(x + ex/2+1, y+(height-ilength)/2, x+width/2, y+(height-ilength)/2+ilength);
		    g.drawLine(x + width - ex/2-1, y+(height-ilength)/2, x + width/2, y+(height-ilength)/2+ilength);
		}
		else if (type.equals("Hash")){
		    g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2,  x + (width/2) - lineLength1, y+(height-ilength)/2+ilength);
	   	    g.drawLine(x + (width/2) + lineLength1, y+(height-ilength)/2,  x + (width/2) + lineLength1, y+(height-ilength)/2+ilength);
		    g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2 + ilength/2,  x + (width/2) + lineLength1, y+(height-ilength)/2 + ilength/2);
		}
		else if (type.equals("Advanced")){
		    //A
		    g.drawLine(x + (width/2), y+(height-ilength)/2,  x + ex, y+(height-ilength)/2+ilength);
            g.drawLine(x + (width/2), y+(height-ilength)/2,  x + (width) - ex, y+(height-ilength)/2 + ilength);
            g.drawLine(x + 3*ex/2, y+(height-ilength)/2 + ilength/2 + ex/2,  x + width - 3*ex/2, y+(height-ilength)/2 + ilength/2+ex/2);
		}
		//Draw security pattern
		g.drawString("sec:"+ securityContext, x+3*width/2, y+ height/2);
		//Draw nonce if it exists
		if (!nonce.isEmpty()){
			g.drawString("nonce:"+ nonce, x+3*width/2, y+ height/2+textHeight);
		}
		//Draw key if it exists
		if (!key.isEmpty()){
			g.drawString("key:"+ key, x+3*width/2, y+ height/2+2*textHeight);
		}
        g.drawImage(IconManager.imgic7000.getImage(), x - 22, y + height/2, null);
    }
    
    public boolean editOndoubleClick(JFrame frame) {

        //JDialogTwoString jdts = new JDialogTwoString(frame, "Setting channel's properties", "Channel name", channelName, "Nb of samples", nbOfSamples);]
		String[] values=new String[]{securityContext, type, message_overhead, encTime, size, nonce, formula, decTime, key,algorithm};
		String[] nonces=tdp.getMGUI().getAllNonce();
		String[] keys = tdp.getMGUI().getAllKeys().toArray(new String[0]);
		JDialogCryptographicConfiguration jdms = new JDialogCryptographicConfiguration(frame, "Setting Cryptographic Configuration properties", values, nonces, keys);
	     //   jdms.setSize(650, 300);
	    GraphicLib.centerOnParent(jdms, 650, 300);
	    jdms.setVisible( true ); // blocked until dialog has been closed

	    if (jdms.hasBeenSet() && (jdms.hasValidString(0))) {
		    securityContext = jdms.getString(0);
	 		type=jdms.getString(1);
		    message_overhead=jdms.getString(2);
		    encTime=jdms.getString(3);
		    size=jdms.getString(4);
		    nonce=jdms.getString(5);
		    formula = jdms.getString(6);
		    decTime = jdms.getString(7);
		    key = jdms.getString(8);
		 	algorithm=jdms.getString(9);
		    return true;
	    }

        return false;
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
			return this;	
		}
		
		if ((int)(Line2D.ptSegDistSq(x+width, y+height/2, x+width +lineLength, y+height/2, _x, _y)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data secContext=\"");
        sb.append(securityContext);
        sb.append("\" type=\"");
        sb.append(type);
        sb.append("\" overhead=\"");
        sb.append(message_overhead);
		sb.append("\" size=\"");
        sb.append(size);
		sb.append("\" encTime=\"");
        sb.append(encTime);
		sb.append("\" decTime=\"");
        sb.append(decTime);
		sb.append("\" nonce=\"");
        sb.append(nonce);
		sb.append("\" key=\"");
        sb.append(key);
        sb.append("\" algorithm=\"");
        sb.append(algorithm);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //System.out.println("*** load extra synchro *** " + getId());
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
        //    int k;
       //     String s;
            
            //System.out.println("Loading Synchronization gates");
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
                            if (elt.getTagName().equals("Data")) {
                                securityContext = elt.getAttribute("secContext");
								type= elt.getAttribute("type");
								message_overhead= elt.getAttribute("overhead");
								size= elt.getAttribute("size");
								encTime = elt.getAttribute("encTime");
								decTime = elt.getAttribute("decTime");
								nonce = elt.getAttribute("nonce");
								key = elt.getAttribute("key");
								algorithm = elt.getAttribute("algorithm");
                                //System.out.println("eventName=" +eventName + " variable=" + result);
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
        return TGComponentManager.TMLAD_ENCRYPT;
    }
    
    public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_TMLAD;
    }
    
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
}


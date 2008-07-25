/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TAttributeObserver
 * Box for storing attributes of observers
 * Creation: 02/06/2006
 * @version 1.1 02/06/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.req;

import java.awt.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;

import ui.*;
import ui.window.*;

public class TAttributeObserver extends TGCWithoutInternalComponent {
    public String oldValue;
    protected String attributeText;
    protected int textX = 5;
    protected int textY = 20;
    protected Graphics myG;
    protected Color myColor;
    
    protected String text;
    protected String []texts;
	protected String violatedAction = "noAction";

    
    public TAttributeObserver(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 200;
        height = 30;
        minWidth = 200;
        minHeight = 30;
        minDesiredWidth = 200;
        minDesiredHeight = 30;
        
        nbConnectingPoint = 5;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.0, .5);
        connectingPoint[1] = new TGConnectingPointVerify(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[2] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[3] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[4] = new TGConnectingPointVerify(this, 0, 0, false, true, 0.75, 1.0);
        addTGConnectingPointsCommentDown();
        
        moveable = false;
        editable = true;
        removable = false;
        
        myColor = ColorManager.OBS_ATTRIBUTE_BOX;
        
        text = "no diagram";
        //makeValue();
    }
    
    public void internalDrawing(Graphics g) {
        int i;
        
        //Graphics tmp = myG;
        if (!tdp.isScaled()) {
            myG = g;
        }
        
        if ((texts == null) || (myG == null)){
            myG = g;
            makeValue();
        }
        
        int h  = g.getFontMetrics().getHeight();
        //h = h + 2;
        //h = h + 1;
        g.drawRect(x, y, width, height);
        g.setColor(myColor);
        g.fillRect(x+1, y+1, width-1, height-1);
        ColorManager.setColor(g, getState(), 0);
        // text
        String s ;
        for(i=0; i<texts.length; i++) {
            s = texts[i];
            if (i == 0) {
                s = "Diagram=\"" + s;
            }
            if (i == (texts.length - 1)) {
                s = s + "\"";
            }
            g.drawString(s, x + textX, y + textY + i* h);
            
        }
		g.drawString("Violated_Action=\"" + violatedAction + "\"", x + textX, y + textY + i* h);
    }
    
    public void makeValue() {
        texts = Conversion.wrapText(text);
        checkMySize();
    }
    
    
    public void calculateMyDesiredSize() {
        if (myG == null) {
            myG = tdp.getGraphics();
        }
        
        if (myG == null) {
            return;
        }
        
        //System.out.println("Regular resize" + toString());
        int desiredWidth = minWidth;
        int h = myG.getFontMetrics().getHeight();
        int desiredHeight =  Math.max(minHeight, h * (texts.length +1) + minHeight);
        
        String s ;
        for(int i=0; i<texts.length; i++) {
             s = texts[i];
              if (i == 0) {
                s = "diagrams=\"" + s;
            }
            if (i == (texts.length - 1)) {
                s = s + "\"";
            }
            desiredWidth = Math.max(desiredWidth,  myG.getFontMetrics().stringWidth(s) + 2 * textX);
			desiredWidth = Math.max(desiredWidth,  myG.getFontMetrics().stringWidth(violatedAction + "Violated action=\"\"") + 2 * textX);
        }
        
        minDesiredWidth = desiredWidth;
        minDesiredHeight = desiredHeight;
    }
    
    public void checkMySize() {
        calculateMyDesiredSize();
        //System.out.println("I check my size");
        
        TGComponent tgc = getTopFather();
        
        if (tgc != null) {
            tgc.recalculateSize();
        }
        
        if (myG == null) {
            myG = tdp.getGraphics();
        }
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        JDialogObserver jdo = new JDialogObserver(frame, "Setting diagrams of Observer " + ((RequirementObserver)(getTopFather())).getRequirementObserverName(), text, violatedAction);
        jdo.setSize(750, 400);
        GraphicLib.centerOnParent(jdo);
        jdo.show();
        
        if (!jdo.isRegularClose()) {
            return false;
        }
        
        text = jdo.getText();
		violatedAction = jdo.getViolatedAction();

        makeValue();
        checkMySize();
        return true;
    }
    
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    protected String translateExtraParam() {
        value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        if (texts != null) {
            for(int i=0; i<texts.length; i++) {
                value = value + texts[i] + "\n";
                sb.append("<textline data=\"");
                sb.append(texts[i]);
                sb.append("\" />\n");
            }
        }
		sb.append("<violated data=\"");
        sb.append(violatedAction);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            //int access, type;
            //String typeOther;
            //String id, valueAtt;
            String s;
            String oldtext = text;
            text = "";
            
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
                            if (elt.getTagName().equals("textline")) {
                                //System.out.println("Analyzing line");
                                s = elt.getAttribute("data");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                text += GTURTLEModeling.decodeString(s) + "\n";
                            } else if (elt.getTagName().equals("violated")) {
                                //System.out.println("Analyzing line");
                                violatedAction = elt.getAttribute("data");
                                if (violatedAction.equals("null")) {
                                    violatedAction = "";
                                }
                            }
                        }
                    }
                }
            }
            if (text.length() == 0) {
                text = oldtext;
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }  
        
        makeValue();
    }
	
	public String getViolatedAction() {
        return violatedAction;
    }
    
}

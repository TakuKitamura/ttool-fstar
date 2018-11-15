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




package ui.ebrdd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttribute;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

//import tmltranslator.*;

/**
 * Class EBRDDAttributeBox
 * AttributeBox. To be used in EBRDDs
 * Creation: 14/10/2009
 * @version 1.0 14/10/2009
 * @author Ludovic APVRILLE
 */
public class EBRDDAttributeBox extends TGCWithoutInternalComponent  {
	public String oldValue;
    protected String attributeText = "Variable";
    protected int textX = 5;
    protected int textY = 20;
    protected LinkedList<TAttribute> myAttributes;
    protected LinkedList<TAttribute> forbiddenNames;
    protected Graphics myG;
    protected Color myColor;
    protected boolean attributes;
    protected boolean lastVisible;
    
    public EBRDDAttributeBox(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		//initScaling(200, 150);
		
		/*oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;*/
		
		minWidth = 50;
        minHeight = 30;
		width = minWidth;
		height = minHeight;
        
        nbConnectingPoint = 0;
       
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
		editable = true;
		removable = true;
        
		setName("Variable declaration");
		
        myImageIcon = IconManager.imgic1060;
		
		myAttributes = new LinkedList<TAttribute> ();
		
		myColor = ColorManager.UML_NOTE_BG;
		
		forbiddenNames = new LinkedList<TAttribute> ();
		
		//actionOnAdd();
    }
    
     public LinkedList<TAttribute> getAttributeList() {
        return myAttributes;
    }
    
    public void setAttributeList(LinkedList<TAttribute> attributes) {
        myAttributes = attributes;
    }
    
    public void setForbiddenNames(LinkedList<TAttribute> v) {
        forbiddenNames = v;
    }
    
    public void internalDrawing(Graphics g) {
        Graphics tmp = myG;
        if (!tdp.isScaled()) {
            myG = g;
        }
        if ((tmp == null) || (lastVisible != areVisible())){
            checkMySize();
        }
        lastVisible = areVisible();
        int h  = g.getFontMetrics().getHeight();
        //h = h + 2;
        //h = h + 1;
        g.drawRect(x, y, width, height);
        g.setColor(myColor);
        g.fillRect(x+1, y+1, width-1, height-1);
        ColorManager.setColor(g, getState(), 0);
        if (areVisible()) {
            TAttribute a;
            for(int i=0; i<myAttributes.size(); i++) {
                a = myAttributes.get (i);
                g.drawString(a.toString(), x + textX, y + textY + i* h);
            }
        } else if (myAttributes.size() >0) {
            g.drawString("...", x + textX, y + textY);
        }
    }
    
    public void makeValue() {
        value = "";
        TAttribute a;
        for(int i=0; i<myAttributes.size(); i++) {
            a = myAttributes.get (i);
            value = value + a + "\n";
        }
        //
    }
    
    public boolean areVisible() {
       return tdp.areAttributesVisible();
    }
    
    public void calculateMyDesiredSize() {
        if (myG == null) {
            myG = tdp.getGraphics();
        }
        
        if (myG == null) {
            return;
        }
        
        if ((myAttributes.size() == 0) || (!areVisible())) {
            //
            minDesiredWidth = minWidth;
            minDesiredHeight = minHeight;
            lastVisible = areVisible();
            return;
        }
        
        lastVisible = areVisible();
        
        //
        int desiredWidth = minWidth;
        int h = myG.getFontMetrics().getHeight();
        int desiredHeight =  Math.max(minHeight, h * (myAttributes.size() -1) + minHeight);
        
        TAttribute a;
        for(int i=0; i<myAttributes.size(); i++) {
            a = myAttributes.get (i);
            desiredWidth = Math.max(desiredWidth,  myG.getFontMetrics().stringWidth(a.toString()) + 2 * textX);
        }
        
        minDesiredWidth = desiredWidth;
        minDesiredHeight = desiredHeight;
		
		width = Math.max(minDesiredWidth, minWidth);
		height = Math.max(minDesiredHeight, minHeight);
    }
    
    public void checkMySize() {
        calculateMyDesiredSize();
    }
    
    public boolean editOndoubleClick(JFrame frame) {
        String oldValue = value;
        JDialogAttribute jda = new JDialogAttribute(myAttributes, forbiddenNames, frame, "Setting variables", attributeText);
        setJDialogOptions(jda);
      //  jda.setSize(650, 375);
        GraphicLib.centerOnParent(jda, 650, 375);
        jda.setVisible(true); // blocked until dialog has been closed
        makeValue();
        if (oldValue.equals(value)) {
            return false;
        }
        checkMySize();
        return true;
    }
	
	protected void setJDialogOptions(JDialogAttribute jda) {
        jda.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        jda.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
        jda.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);
        
        //jda.enableInitialValue(true);
        jda.enableRTLOTOSKeyword(true);
        jda.enableJavaKeyword(true);
        //jda.enableOtherTypes(true);
    }
    
    
    
    public TGComponent isOnMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    protected String translateExtraParam() {
        TAttribute a;
        value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for(int i=0; i<myAttributes.size(); i++) {
            //
            a = myAttributes.get (i);
            //
            value = value + a + "\n";
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
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int access, type;
            String typeOther;
            String id, valueAtt;
            
            //
            //
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Attribute")) {
                                //
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
								
								//
                                if ((TAttribute.isAValidId(id, false, false)) && (TAttribute.isAValidInitialValue(type, valueAtt))) {
                                    //
                                    TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                                    myAttributes.add (ta);
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
        makeValue();
    }
    
    // Main Tree
    
    public int getChildCount() {
        return myAttributes.size();
    }
    
    public Object getChild(int index) {
        return myAttributes.get (index);
    }
    
    public int getIndexOfChild(Object child) {
        return myAttributes.indexOf(child);
    }
	
	public int getType() {
        return TGComponentManager.EBRDD_VARIABLE_DECLARATION;
    }
}

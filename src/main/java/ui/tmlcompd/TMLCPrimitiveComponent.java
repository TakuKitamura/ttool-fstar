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

package ui.tmlcompd;

import myutil.GraphicLib;
import myutil.TraceManager;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAttribute;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Class TMLCPrimitiveComponent
 * Primitive Component. To be used in TML component task diagrams
 * Creation: 12/03/2008
 *
 * @author Ludovic APVRILLE
 * @version 1.0 12/03/2008
 */
public class TMLCPrimitiveComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent, WithAttributes {
    // #FIXME Debugging
	//private int maxFontSize = 14;
    //private int minFontSize = 4;
    private int currentFontSize = -1;
	
    //private boolean displayText = true;
    //    private int spacePt = 3;
    private Color myColor;

    private boolean isAttacker = false;
    private boolean isDaemon = false;
    
    // Icon
    private int iconSize = 15;
    private boolean iconIsDrawn = false;

    // Attributes
    //   private boolean attributesAreDrawn = false;
    //public HashMap<String, Integer> attrMap = new HashMap<String, Integer>();
    //public String mappingName;
    protected List<TAttribute> myAttributes;
    
    // Issue #31
//    private int textX = 15; // border for ports
//    private double dtextX = 0.0;

    private String operation = "";

    public String oldValue;

    public TMLCPrimitiveComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
        //minWidth = 1;
        //minHeight = 1;
        minWidth = 150;
        minHeight = 100;
        
        initScaling(200, 150);

        // Issue #31
//        oldScaleFactor = tdp.getZoom();
//        dtextX = textX * oldScaleFactor;
//        textX = (int) dtextX;
//        dtextX = dtextX - textX;

        nbConnectingPoint = 0;

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        multieditable = true;
        editable = true;
        removable = true;
        userResizable = true;

        value = tdp.findTMLPrimitiveComponentName("TMLComp_");
        oldValue = value;
        setName("Primitive component");

        myImageIcon = IconManager.imgic1202;

        myAttributes = new LinkedList<TAttribute>();

        actionOnAdd();
    }
    
    private Color choosingColor()
    {
        if (ColorManager.TML_COMPOSITE_COMPONENT == Color.white) 
            return Color.white;
        else 
            return new Color(201, 243, 188 - (getMyDepth() * 10), 200);
    }

//    private boolean canTextGoInTheBox(Graphics g, int fontSize, String text)
//    {
//    	int txtWidth = g.getFontMetrics().stringWidth(text) + (textX * 2);
//    	int spaceTakenByIcon = iconSize + textX;
//    	return (fontSize + (textY * 2) < height) // enough space in height
//    			&& (txtWidth + spaceTakenByIcon < width) // enough space in width
//    			;
//    }
    /** 
     * Function which is drawing the box, the text and icon 
     * Issue #31: Fixed zoom on texts and icon + made sure that if the text can't go into the box is does not get drawn
     * @param g
     */
    @Override
    public void internalDrawing(Graphics g)
    {
    	//rectangle + Filling color
    	Color c = g.getColor();
    	myColor = choosingColor();
    	g.drawRect(x, y, width, height);
    	g.setColor(myColor);
    	g.fill3DRect(x, y, width, height, true);
    	g.setColor(c);
    	
    	//String
    	int stringWidth = g.getFontMetrics().stringWidth(value);
    	int centerOfBox = (width - stringWidth) / 2;
    	Font f = g.getFont();
    	currentFontSize = f.getSize();
    	if (canTextGoInTheBox(g, currentFontSize, value, iconSize))
    	{
	    	//put title in bold before drawing then set back to normal after
	    	g.setFont(f.deriveFont(Font.BOLD));
//	    	drawSingleString(g,value, x + centerOfBox, y + currentFontSize + textY);
	    	drawSingleString(g, value, x + centerOfBox, y + currentFontSize + textY);
	    	g.setFont(f);
    	}
    	
    	// Scaled ICON drawing
    	g.drawImage(scale(IconManager.imgic1200.getImage()), x + width - iconSize - textX, y + textX, null);
        if (isAttacker)
            g.drawImage(scale(IconManager.imgic7008.getImage()), x + width - 2 * iconSize - textX, y + 2 * textX, null);
        
        // Attributes printing
        if (tdp.areAttributesVisible())
        {
        	//spaces permits the attributes to not override each other
        	int spaces = currentFontSize + textY * 2; 
        	TAttribute attribute;
        	String attributeStr;
        	for (int i = 0; i < myAttributes.size(); i++)
        	{
        		attribute = myAttributes.get(i);
        		spaces += currentFontSize;
        		attributeStr = attribute.toString();
        		if (canTextGoInTheBox(g, spaces, attributeStr, iconSize))
        		{
	                drawSingleString(g,attributeStr, x + textX, y + spaces);
	                drawVerification(g, x + textX, y + spaces, attribute.getConfidentialityVerification());
        		}
        		else // if we could not display some attributes it will show a ...
        			drawSingleString(g,"...", x + textX, y + height - 15);
        	}
        }
    }
/*
    @Override
    public void internalDrawing(Graphics g) {
        int w;
        Font f = g.getFont();
        Font fold = f;

        if (myColor == null) {
            if (ColorManager.TML_COMPOSITE_COMPONENT == Color.white) {
                myColor = Color.white;
            } else {
                myColor = new Color(201, 243, 188 - (getMyDepth() * 10), 200);
            }
        }

        if ((rescaled) && (!tdp.isScaled())) {

            if (currentFontSize == -1) {
                currentFontSize = f.getSize();
            }
            rescaled = false;
            // Must set the font size ..
            // Find the biggest font not greater than max_font size
            // By Increment of 1
            // Or decrement of 1
            // If font is less than 4, no text is displayed

            int maxCurrentFontSize = Math.max(0, Math.min(height - (2 * textX), maxFontSize));

            f = f.deriveFont((float) maxCurrentFontSize);
            g.setFont(f);
            while (maxCurrentFontSize > (minFontSize - 1)) {
                if (g.getFontMetrics().stringWidth(value) < (width - iconSize - (2 * textX))) {
                    break;
                }
                maxCurrentFontSize--;
                f = f.deriveFont((float) maxCurrentFontSize);
                g.setFont(f);
            }

            currentFontSize = maxCurrentFontSize;

            if (currentFontSize < minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                f = f.deriveFont((float) currentFontSize);
                g.setFont(f);
            }

        }

        // Zoom is assumed to be computed
        Color c = g.getColor();
        g.drawRect(x, y, width, height);
        if ((width > 2) && (height > 2)) {
            g.setColor(myColor);
            g.fillRect(x + 1, y + 1, width - 1, height - 1);
            g.setColor(c);
        }

        // Font size
        if (displayText) {
            f = f.deriveFont((float) currentFontSize);
            g.setFont(f);
            w = g.getFontMetrics().stringWidth(value);
            
            if (w > (width - 2 * (iconSize + textX))) {
                drawSingleString(g,value, x + textX + 1, y + currentFontSize + textX);
            } else {
                drawSingleString(g,value, x + (width - w) / 2, y + currentFontSize + textX);
            }
        }

        // Icon
        if ((width > 30) && (height > (iconSize + 2 * textX))) {
            iconIsDrawn = true;
            g.drawImage(IconManager.imgic1200.getImage(), x + width - iconSize - textX, y + textX, null);
        } else {
            iconIsDrawn = false;
        }
        if (isAttacker) {
            g.drawImage(IconManager.imgic7008.getImage(), x + width - 2 * iconSize - textX, y + 2 * textX, null);
        }

        // Attributes
        if (tdp.areAttributesVisible()) {
            int index = 0;
            int cpt = currentFontSize + 2 * textX;
            String attr;

            TAttribute a;

            int si = Math.min(12, (int) ((float) currentFontSize - 2));

            f = g.getFont();
            f = f.deriveFont((float) si);
            g.setFont(f);
            int step = si + 2;

            while (index < myAttributes.size()) {
                cpt += step;
                if (cpt >= (height - textX)) {
                    break;
                }
                a = myAttributes.get(index);
                attr = a.toString();
                w = g.getFontMetrics().stringWidth(attr);
                if ((w + (2 * textX) + 1) < width) {
                    drawSingleString(g,attr, x + textX, y + cpt);
                    drawVerification(g, x + textX, y + cpt, a.getConfidentialityVerification());
                } else {
                    attr = "...";
                    w = g.getFontMetrics().stringWidth(attr);
                    if ((w + textX + 2) < width) {
                        drawSingleString(g,attr, x + textX + 1, y + cpt);
                    } else {
                        // skip attribute
                        cpt -= step;
                    }
                }
                index++;
            }
        }

        g.setFont(fold);
    }*/

    public void drawVerification(Graphics g, int x, int y, int checkConfStatus) {
        Color c = g.getColor();
        Color c1;
        switch (checkConfStatus) {
            case TAttribute.CONFIDENTIALITY_OK:
                c1 = Color.green;
                break;
            case TAttribute.CONFIDENTIALITY_KO:
                c1 = Color.red;
                break;
            default:
                return;
        }
        g.drawOval(x - 10, y - 10, 6, 9);
        g.setColor(c1);
        g.fillRect(x - 12, y - 5, 9, 7);
        g.setColor(c);
        g.drawRect(x - 12, y - 5, 9, 7);
    }

// Issue #31    
//    @Override
//    public void rescale(double scaleFactor) {
//        dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
//        textX = (int) (dtextX);
//        dtextX = dtextX - textX;
//
//        super.rescale(scaleFactor);
//    }

    @Override
    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public boolean isAttacker() {
        return isAttacker;
    }


    public boolean isDaemon() {
        return isDaemon;
    }
    
    @Override
    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
        // On the icon?
        if (iconIsDrawn) {
            if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize - textX, y + textX, scale(iconSize), scale(iconSize))) {
                tdp.getMouseManager().setSelection(-1, -1);
                tdp.selectTab(getValue());
                return true;
            }
        }

        // On the name ? 
        /*if ((displayText) && (_y <= (y + currentFontSize + textX))) {
            //TraceManager.addDev("Edit on double click x=" + _x + " y=" + _y);
            oldValue = value;
            String s = (String) JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
                    JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
                    null,
                    getValue());
            if ((s != null) && (s.length() > 0)) {
                // Check whether this name is already in use, or not

                if (!TAttribute.isAValidId(s, false, true, false)) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not change the name of the component: the new name is not a valid name",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                if (oldValue.compareTo(s) != 0) {
                    if (((TMLComponentTaskDiagramPanel) (tdp)).namePrimitiveComponentInUse(oldValue, s)) {
                        JOptionPane.showMessageDialog(frame,
                                "Error: the name is already in use",
                                "Name modification",
                                JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }


                //TraceManager.addDev("Set value with change");
                setComponentName(s);
                setValueWithChange(s);
                isAttacker = s.contains("Attacker");
                rescaled = true;
                //TraceManager.addDev("return true");
                return true;

            }
            return false;
        }*/

        // And so -> attributes!
        String oldName = getValue();
        oldValue = getValue();


		JDialogAttribute jda = new JDialogAttribute(myAttributes, null, frame,
                "Setting attributes of " + value, "Attribute", operation, isDaemon, getValue());
        setJDialogOptions(jda);
        // jda.setSize(650, 375);
        GraphicLib.centerOnParent(jda, 750, 375);
        jda.setVisible(true); // blocked until dialog has been closed
        //makeValue();
        //if (oldValue.equals(value)) {
        //return false;
        //}
        operation = jda.getOperation();
        isDaemon = jda.isDaemon();

        TraceManager.addDev("PC1");

        String s = jda.getName();
        if (oldName.compareTo(s) == 0) {
            rescaled = false;
            return true;
        }

        if ((s != null) && (s.length() > 0)) {
            // Check whether this name is already in use, or not

            //TraceManager.addDev("PC2");

            if (!TAttribute.isAValidId(s, false, true, false)) {
                JOptionPane.showMessageDialog(frame,
                        "Could not change the name of the component: the new name is not a valid name",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            //TraceManager.addDev("PC3");

            if (oldValue.compareTo(s) != 0) {
                if (((TMLComponentTaskDiagramPanel) (tdp)).namePrimitiveComponentInUse(oldValue, s)) {
                    JOptionPane.showMessageDialog(frame,
                            "Error: the name is already in use",
                            "Name modification",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            TraceManager.addDev("PC4");


            //TraceManager.addDev("Set value with change");
            setComponentName(s);
            //TraceManager.addDev("PC4.1 oldvalue=" + oldName + " s=" + s);
            setValueWithChange(s);
            //TraceManager.addDev("PC4.2");
            isAttacker = s.contains("Attacker");
            rescaled = true;
            //TraceManager.addDev("return true");

            //TraceManager.addDev("PC5");

            return true;

        }
        return false;



    }

    protected void setJDialogOptions(JDialogAttribute jda) {
        jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        jda.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        jda.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
        jda.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);

        Vector<String> records = ((TMLComponentTaskDiagramPanel) (tdp)).getAllRecords(this);
        for (String s : records) {
            jda.addType(s, false);
        }

        jda.enableInitialValue(true);
        jda.enableRTLOTOSKeyword(true);
        jda.enableJavaKeyword(false);
        jda.enableTMLKeyword(false);
    }

    public Vector<String> getAllRecords() {
        return ((TMLComponentTaskDiagramPanel) (tdp)).getAllRecords(this);
    }

    public TMLCRecordComponent getRecordNamed(String _nameOfRecord) {
        return ((TMLComponentTaskDiagramPanel) (tdp)).getRecordNamed(this, _nameOfRecord);
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLCTD_PCOMPONENT;
    }

    public void wasSwallowed() {
        myColor = null;
    }

    public void wasUnswallowed() {
        myColor = null;
        setFather(null);
        TDiagramPanel tdp = getTDiagramPanel();
        setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
    }

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof TMLCPrimitivePort;
    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //boolean swallowed = false;

        //TraceManager.addDev("Add swallow component");
        // Choose its position
        // Make it an internal component
        // It's one of my son
        //Set its coordinates

        if (tgc instanceof TMLCPrimitivePort) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }

        return false;
    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    /*public Vector getArtifactList() {
      Vector v = new Vector();
      for(int i=0; i<nbInternalTGComponent; i++) {
      if (tgcomponent[i] instanceof TMLArchiArtifact) {
      v.add(tgcomponent[i]);
      }
      }
      return v;
      }*/

    @Override
    public void hasBeenResized() {
        rescaled = true;
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLCPrimitivePort) {
                tgcomponent[i].resizeWithFather();
            }
        }

        if (getFather() != null) {
            resizeWithFather();
        }
    }

    @Override
    public void resizeWithFather() {
        if (/*(father != null) && (*/father instanceof TMLCCompositeComponent ) {
            // Too large to fit in the father? -> resize it!
            resizeToFatherSize();

            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }

    @Override
    public int getChildCount() {
        return myAttributes.size() + nbInternalTGComponent + 1;
    }

    public Object getChild(int index) {
        if (index == 0) {
            return value;
        } else {
            if (index <= myAttributes.size()) {
                return myAttributes.get(index - 1);
            } else {
                return tgcomponent[index - 1 - myAttributes.size()];
            }
        }
    }

    public int getIndexOfChild(Object child) {
        if (child instanceof String) {
            return 0;
        } else {
            // Object o;
            if (myAttributes.indexOf(child) > -1) {
                return myAttributes.indexOf(child) + 1;
            } else {
                for (int i = 0; i < nbInternalTGComponent; i++) {
                    if (tgcomponent[i] == child) {
                        return myAttributes.size() + 1 + i;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    protected String translateExtraParam() {
        TAttribute a;
        //TraceManager.addDev("Loading extra params of " + value);
        //value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data isAttacker=\"");
        sb.append(isAttacker() ? "Yes" : "No");
        sb.append("\" daemon=\"");
        sb.append(isDaemon);
        sb.append("\" Operation=\"");
        sb.append(operation);
        sb.append("\" />\n");
        for (int i = 0; i < myAttributes.size(); i++) {
            //TraceManager.addDev("Attribute:" + i);
            a = myAttributes.get(i);
            //TraceManager.addDev("Attribute:" + i + " = " + a.getId());
            //value = value + a + "\n";
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
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int access, type;
            String typeOther;
            String id, valueAtt;

            //TraceManager.addDev("Loading attributes");
            //TraceManager.addDev(nl.toString());

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //TraceManager.addDev(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //TraceManager.addDev(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                isAttacker = elt.getAttribute("isAttacker").equals("Yes");

                                String tmpO = elt.getAttribute("Operation");
                                if (tmpO == null) {
                                    operation = "";
                                }  else {
                                    operation = tmpO;
                                }


                                tmpO = elt.getAttribute("daemon");
                                if (tmpO == null) {
                                    isDaemon = false;
                                }  else {
                                    isDaemon = tmpO.equals("true");
                                }
                            }
                            if (elt.getTagName().equals("Attribute")) {
                                //TraceManager.addDev("Analyzing attribute");
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
                                if ((TAttribute.isAValidId(id, false, false, false)) && (TAttribute.isAValidInitialValue(type, valueAtt))) {
                                    //TraceManager.addDev("Adding attribute " + id + " typeOther=" + typeOther);
                                    TAttribute ta = new TAttribute(access, id, valueAtt, type, typeOther);
                                    myAttributes.add(ta);
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

    // Issue #31
//    public int getCurrentFontSize() {
//        return currentFontSize;
//    }

    public List<TAttribute> getAttributeList() {
        return myAttributes;
    }

    public List<TMLCPrimitivePort> getAllChannelsOriginPorts() {
        return getAllPorts(0, true);
    }

    public List<TMLCPrimitivePort> getAllChannelsDestinationPorts() {
        return getAllPorts(0, false);
    }

    public List<TMLCPrimitivePort> getAllEventsOriginPorts() {
        return getAllPorts(1, true);
    }

    public List<TMLCPrimitivePort> getAllEventsDestinationPorts() {
        return getAllPorts(1, false);
    }

    public List<TMLCPrimitivePort> getAllRequestsDestinationPorts() {
        return getAllPorts(2, false);
    }

    public List<TMLCPrimitivePort> getAllRequestsOriginPorts() {
        return getAllPorts(2, true);
    }

    public List<TMLCPrimitivePort> getAllPorts(int _type, boolean _isOrigin) {
        List<TMLCPrimitivePort> ret = new LinkedList<TMLCPrimitivePort>();
        TMLCPrimitivePort port;

        //TraceManager.addDev("Type = " + _type + " isOrigin=" + _isOrigin);

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLCPrimitivePort) {
                port = (TMLCPrimitivePort) tgcomponent[i];
                //TraceManager.addDev("Found one port:" + port.getPortName() + " type=" + port.getPortType() + " origin=" + port.isOrigin());
                if ((port.getPortType() == _type) && (port.isOrigin() == _isOrigin)) {
                    ret.add(port);
                    //TraceManager.addDev("Adding port:" + port.getPortName());
                }
            }
        }

        return ret;
    }

    public List<TMLCPrimitivePort> getAllInternalPrimitivePorts() {
        List<TMLCPrimitivePort> list = new ArrayList<TMLCPrimitivePort>();
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLCPrimitivePort) {
                list.add((TMLCPrimitivePort) (tgcomponent[i]));
            }
        }

        return list;
    }

    public String getAttributes() {
        String attr = "";
        for (TAttribute a : myAttributes) {
            attr += a.toAvatarString() + "\n";
        }

        return attr;
    }

    public String getOperation() {
        return operation;
    }

    public TMLCPath findPathWith(TGComponent tgc) {
        return ((TMLComponentTaskDiagramPanel) tdp).findPathWith(tgc);
    }


    /*public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
      }*/



    /*public String getAttributeList() {
      String attr = "";
      attr += "Data size (in byte) = " + byteDataSize + "\n";
      attr += "Pipeline size = " + pipelineSize + "\n";
      if (schedulingPolicy == HwCPU.DEFAULT_SCHEDULING) {
      attr += "Scheduling policy = basic Round Robin\n";
      }
      attr += "Task switching time (in cycle) = " + taskSwitchingTime + "\n";
      attr += "Go in idle mode (in cycle) = " + goIdleTime + "\n";
      attr += "Max consecutive idle cycles before going in idle mode (in cycle) = " + maxConsecutiveIdleCycles + "\n";
      attr += "Execi execution time (in cycle) = " + execiTime + "\n";
      attr += "Branching prediction misrate (in %) = " + branchingPredictionPenalty + "\n";
      attr += "Cache miss (in %) = " + cacheMiss + "\n";
      return attr;


      }*/


}

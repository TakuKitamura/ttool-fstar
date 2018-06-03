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

package ui.syscams;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogSysCAMSBlockTDF;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class SysCAMSBlockTDF
 * Primitive Component. To be used in SystemC-AMSdiagrams
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSBlockTDF extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent, WithAttributes {
	private int period;
	private String time;
	private String processCode;
	
	private int maxFontSize = 14;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
    private Color myColor;

	private boolean isAttacker=false;

    // Attributes
    public HashMap<String, Integer> attrMap = new HashMap<String, Integer>();
    public String mappingName;
    private int textX = 15; // border for ports
    private double dtextX = 0.0;

    public String oldValue;
	
    public SysCAMSBlockTDF(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(200, 150);

        oldScaleFactor = tdp.getZoom();
        dtextX = textX * oldScaleFactor;
        textX = (int)dtextX;
        dtextX = dtextX - textX;

        minWidth = 1;
        minHeight = 1;

        nbConnectingPoint = 0;

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        multieditable = true;
        editable = true;
        removable = true;
        userResizable = true;

        value = tdp.findSysCAMSPrimitiveComponentName("Block_TDF_");
        oldValue = value;
        name = "Primitive component - Block TDF";
        
        // Initialization of port attributes
        setPeriod(-1);
        setProcessCode("void processing() {\n\n}");
        setTime("");
        
        myImageIcon = IconManager.imgic1202;

        actionOnAdd();
    }

    public void internalDrawing(Graphics g) {
        int w;
        Font f = g.getFont();
        Font fold = f;

        if (myColor == null) {
    		myColor = Color.lightGray;
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

            int maxCurrentFontSize = Math.max(0, Math.min(height-(2*textX), maxFontSize));
            f = f.deriveFont((float)maxCurrentFontSize);
            g.setFont(f);
            while(maxCurrentFontSize > (minFontSize-1)) {
                if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
                    break;
                }
                maxCurrentFontSize --;
                f = f.deriveFont((float)maxCurrentFontSize);
                g.setFont(f);
            }
            currentFontSize = maxCurrentFontSize;
            if(currentFontSize <minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                f = f.deriveFont((float)currentFontSize);
                g.setFont(f);
            }
        }

        // Zoom is assumed to be computed
        Color c = g.getColor();
        g.drawRect(x, y, width, height);
        if ((width > 2) && (height > 2)) {
            g.setColor(myColor);
            g.fillRect(x+1, y+1, width-1, height-1);
            g.setColor(c);
        }

        // Font size
        if (displayText) {
            f = f.deriveFont((float)currentFontSize);
            g.setFont(f);
            w = g.getFontMetrics().stringWidth(value);
            if (w > (width - 2 * textX)) {
            	// name
                g.drawString(value, x + textX + 1, y + currentFontSize + textX);
                // period
            	String s = "Tm = " + this.getPeriod();
            	g.drawString(s, x + textX + 1, y + height - currentFontSize - textX);
            } else {
            	// name
                g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
                // period
            	String s = "Tm = " + this.getPeriod();
            	w = g.getFontMetrics().stringWidth(s);
            	g.drawString(s, x + (width - w)/2, y + height - currentFontSize - textX);
            }
        }

        g.setFont(fold);
    }
     public void drawVerification(Graphics g, int x, int y, int checkConfStatus){
        Color c = g.getColor();
        Color c1;
        switch(checkConfStatus) {
        case TAttribute.CONFIDENTIALITY_OK:
            c1 = Color.green;
            break;
        case TAttribute.CONFIDENTIALITY_KO:
            c1 = Color.red;
            break;
        default:
            return;
        }
		g.drawOval(x-10, y-10, 6, 9);
		g.setColor(c1);
		g.fillRect(x-12, y-5, 9, 7);
		g.setColor(c);
		g.drawRect(x-12, y-5, 9, 7);
    }

    public void rescale(double scaleFactor){
        dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
        textX = (int)(dtextX);
        dtextX = dtextX - textX;
        super.rescale(scaleFactor);
    }

    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

	public boolean isAttacker(){
		return isAttacker;
	}

    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
    	// On the name ?
        if ((displayText) && (_y <= (y + currentFontSize + textX))) {
            //TraceManager.addDev("Edit on double click x=" + _x + " y=" + _y);
            oldValue = value;
            String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
                                                           JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
                                                           null,
                                                           getValue());
            if ((s != null) && (s.length() > 0)) {
                // Check whether this name is already in use, or not

                if (!TAttribute.isAValidId(s, false, false)) {
                    JOptionPane.showMessageDialog(frame,
                                                  "Could not change the name of the component: the new name is not a valid name",
                                                  "Error",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                if (oldValue.compareTo(s) != 0) {
                    if (((SysCAMSComponentTaskDiagramPanel)(tdp)).nameBlockTDFComponentInUse(oldValue, s)) {
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
        }
    	
    	JDialogSysCAMSBlockTDF jtdf = new JDialogSysCAMSBlockTDF(this);
    	jtdf.setVisible(true);
        rescaled = true;
        return true;
    }

    public int getType() {
		return TGComponentManager.CAMS_BLOCK_TDF;
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
    	if (tgc instanceof SysCAMSPortTDF) {
    		return tgc instanceof SysCAMSPortTDF;
    	} else if (tgc instanceof SysCAMSPortConverter) {
    		return tgc instanceof SysCAMSPortConverter;
    	} else {
    		return true;
    	}
    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        //TraceManager.addDev("Add swallow component");
        // Choose its position
        // Make it an internal component
        // It's one of my son
        //Set its coordinates
        if (tgc instanceof SysCAMSPortTDF) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }
        if (tgc instanceof SysCAMSPortConverter) {
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

    public void hasBeenResized() {
        rescaled = true;
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SysCAMSPortTDF) {
                tgcomponent[i].resizeWithFather();
            }
            if (tgcomponent[i] instanceof SysCAMSPortConverter) {
            	tgcomponent[i].resizeWithFather();
            }
        }
        if (getFather() != null) {
            resizeWithFather();
        }
    }

    public void resizeWithFather() {
        if ((father != null) && (father instanceof SysCAMSCompositeComponent)) {
            // Too large to fit in the father? -> resize it!
            resizeToFatherSize();

            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }

    protected String translateExtraParam() {
    	StringBuffer proc;
    	
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<Data isAttacker=\"");
        sb.append(isAttacker() ? "Yes": "No");
        sb.append("\" />\n");
        sb.append("<Attribute period=\"");
        sb.append(this.getPeriod());
        sb.append("\" processCode=\"");
        proc = encode(this.getProcessCode());
        sb.append(proc);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    public StringBuffer encode(String data) {
    	StringBuffer databuf = new StringBuffer(data);
    	StringBuffer buffer = new StringBuffer("");
        for(int pos = 0; pos != data.length(); pos++) {
        	char c = databuf.charAt(pos);
            switch(c) {
                case '&':  buffer.append("&amp;");       break;
                case '\"': buffer.append("&quot;");      break;
                case '\'': buffer.append("&apos;");      break;
                case '<':  buffer.append("&lt;");        break;
                case '>':  buffer.append("&gt;");        break;
                default:   buffer.append(databuf.charAt(pos)); break;
            }
        }
        return buffer;
    }
        
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int period;
            String processCode;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
							if (elt.getTagName().equals("Data")) {
                                isAttacker = elt.getAttribute("isAttacker").equals("Yes");
							}
                            if (elt.getTagName().equals("Attribute")) {
                                period = Integer.decode(elt.getAttribute("period")).intValue();
                                processCode = elt.getAttribute("processCode");
                                setPeriod(period);
                                setProcessCode(processCode);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public int getCurrentFontSize() {
        return currentFontSize;
    }

    public java.util.List<SysCAMSPortTDF> getAllTDFOriginPorts() {
        return getAllTDFPorts(0, 1);
    }

    public java.util.List<SysCAMSPortTDF> getAllTDFDestinationPorts() {
        return getAllTDFPorts(0, 0);
    }
    
    public java.util.List<SysCAMSPortConverter> getAllConvOriginPorts() {
    	return getAllConvPorts(0, 1);
    }
    
    public java.util.List<SysCAMSPortConverter> getAllConvDestinationPorts() {
    	return getAllConvPorts(0, 0);
    }

    public java.util.List<SysCAMSPortTDF> getAllTDFPorts(int _type, int _isOrigin) {
    	java.util.List<SysCAMSPortTDF> ret = new LinkedList<SysCAMSPortTDF>();
    	SysCAMSPortTDF port;

        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SysCAMSPortTDF) {
                port = (SysCAMSPortTDF)tgcomponent[i];
                if ((port.getPortType() == _type) && (port.getOrigin() == _isOrigin)) {
                    ret.add(port);
                }
            }
        }
        return ret;
    }
    
    public java.util.List<SysCAMSPortConverter> getAllConvPorts(int _type, int _isOrigin) {
    	java.util.List<SysCAMSPortConverter> ret = new LinkedList<SysCAMSPortConverter>();
    	SysCAMSPortConverter port;
    	
    	for(int i=0; i<nbInternalTGComponent; i++) {
    		if (tgcomponent[i] instanceof SysCAMSPortConverter) {
    			port = (SysCAMSPortConverter)tgcomponent[i];
    			if ((port.getPortType() == _type) && (port.getOrigin() == _isOrigin)) {
    				ret.add(port);
    			}
    		}
    	}
    	return ret;
    }

    public java.util.List<SysCAMSPortTDF> getAllInternalPortsTDF() {
    	java.util.List<SysCAMSPortTDF> list = new ArrayList<SysCAMSPortTDF>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof SysCAMSPortTDF) {
                list.add((SysCAMSPortTDF)(tgcomponent[i]));
            }
        }
        return list;
    }
    
    public java.util.List<SysCAMSPortConverter> getAllInternalPortsConv() {
    	java.util.List<SysCAMSPortConverter> list = new ArrayList<SysCAMSPortConverter>();
    	for(int i=0; i<nbInternalTGComponent; i++) {
    		if (tgcomponent[i] instanceof SysCAMSPortConverter) {
    			list.add((SysCAMSPortConverter)(tgcomponent[i]));
    		}
    	}
    	return list;
    }

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public String getAttributes() {
		return null;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}


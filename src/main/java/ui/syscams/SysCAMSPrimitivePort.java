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
import ui.window.JDialogSysCAMSPortConverter;
import ui.window.JDialogSysCAMSPortDE;
import ui.window.JDialogSysCAMSPortTDF;

import javax.swing.*;
import java.awt.*;

/**
 * Class SysCAMSPrimitivePort
 * Primitive port. To be used in SystemC-AMS diagrams
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 */

public abstract class SysCAMSPrimitivePort extends TGCScalableWithInternalComponent implements SwallowedTGComponent, LinkedReference, WithAttributes {
    protected Color myColor;
    protected int orientation;
    protected int oldx, oldy;
    protected int halfwidth = 13;
    protected int currentOrientation = GraphicLib.NORTH;

    private int isOrigin = -1;
    public int typep = 0;
    protected int oldTypep = typep;
    public String commName;

    protected int decPoint = 3;

    protected boolean conflict = false;
    protected String conflictMessage;
    
    private ImageIcon portImageIconTDF, portImageIconDE;
    private ImageIcon portImageIconW, portImageIconE, portImageIconN, portImageIconS;
    
    public SysCAMSPrimitivePort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(2*halfwidth, 2*halfwidth);

        minWidth = 1;
        minHeight = 1;

        initConnectingPoint(true, true, 1);
                
        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = false;
        commName = "port";
        makeValue();
        setName("Primitive port");

        myImageIcon = IconManager.imgic1206;
        portImageIconTDF = IconManager.imgic8000;
        portImageIconDE = IconManager.imgic8001;
        portImageIconW = IconManager.imgic8002; 
        portImageIconE = IconManager.imgic8003; 
        portImageIconN = IconManager.imgic8004; 
        portImageIconS = IconManager.imgic8005; 
        
        // Initialization of port attributes
        if (this instanceof SysCAMSPortTDF) {
        	((SysCAMSPortTDF) this).setPeriod(-1);
        	((SysCAMSPortTDF) this).setTime("");
        	((SysCAMSPortTDF) this).setRate(-1);
        	((SysCAMSPortTDF) this).setDelay(-1);
        	((SysCAMSPortTDF) this).setTDFType("int");
        } else if (this instanceof SysCAMSPortDE) {
        	((SysCAMSPortDE) this).setPeriod(-1);
        	((SysCAMSPortDE) this).setTime("");
        	((SysCAMSPortDE) this).setRate(-1);
        	((SysCAMSPortDE) this).setDelay(-1);
        	((SysCAMSPortDE) this).setDEType("int");
        } else if (this instanceof SysCAMSPortConverter) {
        	((SysCAMSPortConverter) this).setPeriod(-1);
        	((SysCAMSPortConverter) this).setTime("");
        	((SysCAMSPortConverter) this).setDelay(-1);
        	((SysCAMSPortConverter) this).setRate(-1);
        	((SysCAMSPortConverter) this).setConvType("int");
        }
    }

    public void initConnectingPoint(boolean in, boolean out, int nb) {
        nbConnectingPoint = nb;
        connectingPoint = new TGConnectingPoint[nb];
        int i;
        for (i=0; i<nbConnectingPoint; i++) {
            connectingPoint[i] = new SysCAMSPortConnectingPoint(this, 0, 0, in, out, 0.5, 0.0);
        }
    }

    public Color getMyColor() {
        return myColor;
    }

    public void internalDrawing(Graphics g) {
        if ((x != oldx) | (oldy != y)) {
            // Component has moved!
            manageMove();
            oldx = x;
            oldy = y;
        }

        if (rescaled) {
            rescaled = false;
        }

        // Zoom is assumed to be computed
        Color c = g.getColor();
        g.setColor(c);
         
        TGComponent tgc = getFather();
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
        	if (tgc instanceof SysCAMSBlockTDF && this instanceof SysCAMSPortTDF) {
        		g.drawRect(x+width/2-portImageIconTDF.getIconWidth()/2, y+height/2-portImageIconTDF.getIconHeight()/2, portImageIconTDF.getIconWidth(), portImageIconTDF.getIconHeight());
        		g.drawImage(portImageIconTDF.getImage(), x+width/2-portImageIconTDF.getIconWidth()/2, y+height/2-portImageIconTDF.getIconHeight()/2, null);
        	} 
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockDE)) {
        	if (tgc instanceof SysCAMSBlockDE && this instanceof SysCAMSPortDE) {
        		g.drawRect(x+width/2-portImageIconDE.getIconWidth()/2, y+height/2-portImageIconDE.getIconHeight()/2, portImageIconDE.getIconWidth(), portImageIconDE.getIconHeight());
        		g.drawImage(portImageIconDE.getImage(), x+width/2-portImageIconDE.getIconWidth()/2, y+height/2-portImageIconDE.getIconHeight()/2, null);
        	}
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
        	if (tgc instanceof SysCAMSBlockTDF && this instanceof SysCAMSPortConverter) {
        		switch(currentOrientation) {
                case GraphicLib.NORTH:
            		g.drawRect(x-1+width/2-portImageIconN.getIconWidth()/2, y-1+height/2-portImageIconN.getIconHeight()/2, portImageIconN.getIconWidth()+2, portImageIconN.getIconHeight()+2);
             		g.drawImage(portImageIconN.getImage(), x+width/2-portImageIconN.getIconWidth()/2, y+height/2-portImageIconN.getIconHeight()/2, null);
                	break;
                case GraphicLib.SOUTH:
            		g.drawRect(x+width/2-portImageIconS.getIconWidth()/2, y+height/2-portImageIconS.getIconHeight()/2, portImageIconS.getIconWidth(), portImageIconS.getIconHeight());
             		g.drawImage(portImageIconS.getImage(), x+width/2-portImageIconS.getIconWidth()/2, y+height/2-portImageIconS.getIconHeight()/2, null);
                	break;
                case GraphicLib.WEST:
            		g.drawRect(x+width/2-portImageIconW.getIconWidth()/2, y+height/2-portImageIconW.getIconHeight()/2, portImageIconW.getIconWidth(), portImageIconW.getIconHeight());
             		g.drawImage(portImageIconW.getImage(), x+width/2-portImageIconW.getIconWidth()/2, y+height/2-portImageIconW.getIconHeight()/2, null);
                	break;
                case GraphicLib.EAST:
                default:
            		g.drawRect(x+width/2-portImageIconE.getIconWidth()/2, y+height/2-portImageIconE.getIconHeight()/2, portImageIconE.getIconWidth(), portImageIconE.getIconHeight());
             		g.drawImage(portImageIconE.getImage(), x+width/2-portImageIconE.getIconWidth()/2, y+height/2-portImageIconE.getIconHeight()/2, null);
                }
        	}
        }
        
        int ft = 10;
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
            ft = ((SysCAMSBlockTDF)tgc).getCurrentFontSize();
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockDE)) {
        	ft = ((SysCAMSBlockDE)tgc).getCurrentFontSize();
        }
        int w;
        Font f = g.getFont();
        Font fold = f;

        int si = Math.min(8, (int)((float)ft - 2));
        f = f.deriveFont((float)si);
        g.setFont(f);
        w = g.getFontMetrics().stringWidth(commName);
        if (w < ((int)(width * 1.5))) {
            g.drawString(commName, x, y-1);
        }

        g.setFont(fold);

        drawParticularity(g);
    }

    public abstract void drawParticularity(Graphics g);

    public void manageMove() {
        if (father != null) {
            Point p = GraphicLib.putPointOnRectangle(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());

            x = p.x - width/2;
            y = p.y - height/2;

            setMoveCd(x, y);

            int orientation = GraphicLib.getCloserOrientation(x+(width/2), y+(height/2), father.getX(), father.getY(), father.getWidth(), father.getHeight());
            if (orientation != currentOrientation) {
                setOrientation(orientation);
            }
        }
    }

    // TGConnecting points ..
    public void setOrientation(int orientation) {
        currentOrientation = orientation;
        double w0, h0;

        switch(orientation) {
        case GraphicLib.NORTH:
            w0 = 0.5;
            h0 = 0.0;
            break;
        case GraphicLib.WEST:
            w0 = 0.0;
            h0 = 0.5;
            break;
        case GraphicLib.SOUTH:
            w0 = 0.5;
            h0 = 1.0;
            break;
        case GraphicLib.EAST:
        default:
            w0 = 1.0;
            h0 = 0.5;
        }

        for (int i=0; i<1; i++) {
            ((SysCAMSPortConnectingPoint) connectingPoint[i]).setW(w0);
            ((SysCAMSPortConnectingPoint) connectingPoint[i]).setH(h0);
        }
    }

    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
    	if (this instanceof SysCAMSPortTDF) {
    		return TGComponentManager.CAMS_PORT_TDF;
    	} else if (this instanceof SysCAMSPortDE) {
    		return TGComponentManager.CAMS_PORT_DE;
    	} else if (this instanceof SysCAMSPortConverter) {
    		return TGComponentManager.CAMS_PORT_CONVERTER;
    	}
    	return -1;
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

    public void resizeWithFather() {
        if ((father != null) && (father instanceof SysCAMSBlockTDF)) {
            // Too large to fit in the father? -> resize it!
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
            oldx = -1;
            oldy = -1;
        }
        if ((father != null) && (father instanceof SysCAMSBlockDE)) {
        	// Too large to fit in the father? -> resize it!
        	setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
        	setMoveCd(x, y);
        	oldx = -1;
        	oldy = -1;
        }
    }

    public boolean editOndoubleClick(JFrame frame) {
    	if (this instanceof SysCAMSPortTDF) {
    		JDialogSysCAMSPortTDF jtdf = new JDialogSysCAMSPortTDF((SysCAMSPortTDF) this);
    		jtdf.setVisible(true);
    	} else if (this instanceof SysCAMSPortDE){
    		JDialogSysCAMSPortDE jde = new JDialogSysCAMSPortDE((SysCAMSPortDE) this);
    		jde.setVisible(true);
    	} else if (this instanceof SysCAMSPortConverter) {
    		JDialogSysCAMSPortConverter jconv = new JDialogSysCAMSPortConverter((SysCAMSPortConverter) this);
    		jconv.setVisible(true);
    	}
    	
        ((SysCAMSComponentTaskDiagramPanel)tdp).updatePorts();
        return true;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Prop commName=\"");
        sb.append(commName);
        sb.append("\" commType=\"" + typep);
        sb.append("\" origin=\"");
        sb.append(getOrigin());
        
        if (this instanceof SysCAMSPortTDF) {
        	sb.append("\" period=\"" + ((SysCAMSPortTDF) this).getPeriod());
        	sb.append("\" time=\"" + ((SysCAMSPortTDF) this).getTime());
        	sb.append("\" rate=\"" + ((SysCAMSPortTDF) this).getRate());
        	sb.append("\" delay=\"" + ((SysCAMSPortTDF) this).getDelay());
        	sb.append("\" type=\"" + ((SysCAMSPortTDF) this).getTDFType());
        }
        if (this instanceof SysCAMSPortDE) {
        	sb.append("\" period=\"" + ((SysCAMSPortDE) this).getPeriod());
        	sb.append("\" time=\"" + ((SysCAMSPortDE) this).getTime());
        	sb.append("\" rate=\"" + ((SysCAMSPortDE) this).getRate());
        	sb.append("\" delay=\"" + ((SysCAMSPortDE) this).getDelay());
        	sb.append("\" type=\"" + ((SysCAMSPortDE) this).getDEType());
        }
        if (this instanceof SysCAMSPortConverter) {
        	sb.append("\" period=\"" + ((SysCAMSPortConverter) this).getPeriod());
        	sb.append("\" time=\"" + ((SysCAMSPortConverter) this).getTime());
        	sb.append("\" rate=\"" + ((SysCAMSPortConverter) this).getRate());
        	sb.append("\" delay=\"" + ((SysCAMSPortConverter) this).getDelay());
        	sb.append("\" type=\"" + ((SysCAMSPortConverter) this).getConvType());
        }
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            
            int period, rate, delay;
            String type, time; 

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Prop")) {
                                commName = elt.getAttribute("commName");
                                typep = Integer.decode(elt.getAttribute("commType")).intValue();
                                isOrigin = Integer.decode(elt.getAttribute("origin")).intValue();
                                period = Integer.decode(elt.getAttribute("period")).intValue();
                                time = elt.getAttribute("time");
                                rate = Integer.decode(elt.getAttribute("rate")).intValue();
                                delay = Integer.decode(elt.getAttribute("delay")).intValue();
                                type = elt.getAttribute("type");
                                setPortName(commName);
                                if (this instanceof SysCAMSPortTDF) {
                                	((SysCAMSPortTDF)this).setPeriod(period);
                                	((SysCAMSPortTDF)this).setTime(time);
                                	((SysCAMSPortTDF)this).setRate(rate);
                                	((SysCAMSPortTDF)this).setDelay(delay);
                                	((SysCAMSPortTDF)this).setTDFType(type);
                                } else if (this instanceof SysCAMSPortDE) {
                                	((SysCAMSPortDE)this).setPeriod(period);
                                	((SysCAMSPortDE)this).setTime(time);
                                	((SysCAMSPortDE)this).setRate(rate);
                                	((SysCAMSPortDE)this).setDelay(delay);
                                	((SysCAMSPortDE)this).setDEType(type);
                                } else if (this instanceof SysCAMSPortConverter) {
                                	((SysCAMSPortConverter)this).setPeriod(period);
                                	((SysCAMSPortConverter)this).setTime(time);
                                	((SysCAMSPortConverter)this).setRate(rate);
                                	((SysCAMSPortConverter)this).setDelay(delay);
                                	((SysCAMSPortConverter)this).setConvType(type);
                                }
                            }
                            makeValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public void makeValue() {
        value = getPortTypeName() + " " + getPortName();
    }

    public String getPortName() {
        return commName;
    }

    public void setPortName(String s) {
        commName = s;
     }
    
    public int getPortType() {
        return typep;
    }

    public String getPortTypeName() {
        if (this instanceof SysCAMSPortTDF) {
        	return "Port TDF";
        } else if (this instanceof SysCAMSPortDE) {
        	return "Port DE";
        } else if (this instanceof SysCAMSPortConverter) {
        	return "Port Converter";
        } 
        return "";
    }

    public int getOrigin() {
    	return isOrigin;
    }
    
    public void setOrigin(int orig) {
    	isOrigin = orig;
    }
    
    public int getDefaultConnector() {
        return TGComponentManager.CAMS_CONNECTOR;
    }

    public String getAttributes() {
        String attr = "";
        if (getOrigin() == 1) {
            attr += "out ";
        } else if (getOrigin() == 0) {
            attr += "in ";
        }
        attr += getPortTypeName() + ": ";
        attr += getPortName() + "\n";
        attr += "B";
        if (getOrigin() == 1) {
            attr += "W\n";
        } else if (getOrigin() == 0) {
            attr += "R\n";
        }
        if (conflict) {
            attr += "Error in path=" + conflictMessage;
        }
        return attr;
    }
    
    public boolean getConflict() {
        return conflict;
    }

    public void setConflict(boolean _conflict, String _msg) {
        conflict = _conflict;
        myColor = null;
        conflictMessage = _msg;
    }
}

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
import ui.eln.*;
import ui.util.IconManager;
import ui.window.*;
import javax.swing.*;
import java.awt.*;

/**
 * Class SysCAMSPrimitivePort
 * Primitive port. To be used in SystemC-AMS diagrams
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSPrimitivePort extends TGCScalableWithInternalComponent implements SwallowedTGComponent, LinkedReference {
    protected Color myColor;
    protected int orientation;
	private int maxFontSize = 14;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    protected int oldx, oldy;
    protected int currentOrientation = GraphicLib.NORTH;

    private int isOrigin = -1;
    public int typep = 0;
    protected int oldTypep = typep;
    public String commName;

    private int textX = 15;
    private double dtextX = 0.0;
    protected int decPoint = 3;
    
    public SysCAMSPrimitivePort(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(20, 20);

        dtextX = textX * oldScaleFactor;
        textX = (int)dtextX;
        dtextX = dtextX - textX;
        
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
        name = "Primitive port - " + getPortTypeName();
        
        myImageIcon = IconManager.imgic1206;
        
        if (this instanceof SysCAMSPortTDF) {
        	((SysCAMSPortTDF) this).setPeriod(-1);
        	((SysCAMSPortTDF) this).setTime("");
        	((SysCAMSPortTDF) this).setRate(-1);
        	((SysCAMSPortTDF) this).setDelay(-1);
        	((SysCAMSPortTDF) this).setTDFType("int");
        	((SysCAMSPortTDF) this).setOrigin(0);
        } else if (this instanceof SysCAMSPortDE) {
//        	((SysCAMSPortDE) this).setPeriod(-1);
//        	((SysCAMSPortDE) this).setTime("");
//        	((SysCAMSPortDE) this).setRate(-1);
//        	((SysCAMSPortDE) this).setDelay(-1);
        	((SysCAMSPortDE) this).setDEType("int");
        	((SysCAMSPortDE) this).setOrigin(0);
        	((SysCAMSPortDE) this).setSensitive(false);
        	((SysCAMSPortDE) this).setSensitiveMethod("");
        } else if (this instanceof SysCAMSPortConverter) {
        	((SysCAMSPortConverter) this).setPeriod(-1);
        	((SysCAMSPortConverter) this).setTime("");
        	((SysCAMSPortConverter) this).setDelay(-1);
        	((SysCAMSPortConverter) this).setRate(-1);
        	((SysCAMSPortConverter) this).setConvType("int");
        	((SysCAMSPortConverter) this).setOrigin(0);
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
        Font f = g.getFont();
        Font fold = f;
        
    	if ((x != oldx) | (oldy != y)) {
            manageMove();
            oldx = x;
            oldy = y;
        }

    	if (this.rescaled && !this.tdp.isScaled()) {
            this.rescaled = false;
            int maxCurrentFontSize = Math.max(0, Math.min(this.height, (int) (this.maxFontSize * this.tdp.getZoom())));
            f = f.deriveFont((float) maxCurrentFontSize);

            while (maxCurrentFontSize > (this.minFontSize * this.tdp.getZoom() - 1)) {
            	if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
            		break;
            	}
                maxCurrentFontSize--;
                f = f.deriveFont((float) maxCurrentFontSize);
            }

            if (this.currentFontSize < this.minFontSize * this.tdp.getZoom()) {
                maxCurrentFontSize++;
                f = f.deriveFont((float) maxCurrentFontSize);
            }
            g.setFont(f);
            this.currentFontSize = maxCurrentFontSize;
        } else {
            f = f.deriveFont(this.currentFontSize);
    	}

    	int attributeFontSize = this.currentFontSize * 5 / 6;
    	int w = g.getFontMetrics().stringWidth(commName);
		int h = g.getFontMetrics().getAscent();
        g.setFont(f.deriveFont((float) attributeFontSize));
        g.setFont(f);
    	g.setFont(f.deriveFont(Font.BOLD));
    	
        Color c = g.getColor();
        g.setColor(c);
         
        if (this instanceof SysCAMSPortTDF) {
        	g.setColor(Color.BLACK);
        	g.fillRect(x, y, width, height);
        	g.setColor(c);
        	g.drawRect(x, y, width, height);
        	switch (currentOrientation) {
			case GraphicLib.NORTH:
				g.drawString(commName, x + width + width / 2, y);
				break;
			case GraphicLib.WEST:
				g.drawString(commName, x - w, y + height + height / 2 + h);
				break;
			case GraphicLib.SOUTH:
				g.drawString(commName, x + width + width / 2, y + height + h);
				break;
			case GraphicLib.EAST:
			default:
				g.drawString(commName, x + width, y + height + height / 2 + h);
			}
        } else if (this instanceof SysCAMSPortConverter) {
        	switch(currentOrientation) {
            case GraphicLib.NORTH:
            	g.setColor(Color.WHITE);
            	g.fillRect(x, y, width, height / 2);
            	g.setColor(Color.BLACK);
            	g.fillRect(x, y + height / 2, width, height / 2);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	g.drawString(commName, x + width + width / 2, y);
            	break;
            case GraphicLib.SOUTH:
            	g.setColor(Color.BLACK);
            	g.fillRect(x, y, width, height / 2);
            	g.setColor(Color.WHITE);
            	g.fillRect(x, y + height / 2, width, height / 2);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	g.drawString(commName, x + width + width / 2, y + height + h);
            	break;
            case GraphicLib.WEST:
            	g.setColor(Color.WHITE);
            	g.fillRect(x, y, width / 2, height);
            	g.setColor(Color.BLACK);
            	g.fillRect(x + width / 2, y, width / 2, height);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	g.drawString(commName, x - w, y + height + height / 2 + h);
            	break;
            case GraphicLib.EAST:
            default:
            	g.setColor(Color.BLACK);
            	g.fillRect(x, y, width / 2, height);
            	g.setColor(Color.WHITE);
            	g.fillRect(x + width / 2, y, width / 2, height);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	g.drawString(commName, x + width, y + height + height / 2 + h);
            }
        } else if (this instanceof SysCAMSPortDE) {
        	g.setColor(Color.WHITE);
        	g.fillRect(x, y, width, height);
        	g.setColor(c);
        	g.drawRect(x, y, width, height);
        	switch (currentOrientation) {
			case GraphicLib.NORTH:
				g.drawString(commName, x + width + width / 2, y);
				break;
			case GraphicLib.WEST:
				g.drawString(commName, x - w, y + height + height / 2 + h);
				break;
			case GraphicLib.SOUTH:
				g.drawString(commName, x + width + width / 2, y + height + h);
				break;
			case GraphicLib.EAST:
			default:
				g.drawString(commName, x + width, y + height + height / 2 + h);
			}
    	}
        
        TGComponent tgc = getFather();
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
        	if (tgc instanceof SysCAMSBlockTDF && this instanceof SysCAMSPortTDF) {
        		g.setColor(Color.BLACK);
            	g.fillRect(x, y, width, height);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	switch (currentOrientation) {
    			case GraphicLib.NORTH:
    				g.drawString(commName, x + width + width / 2, y);
    				break;
    			case GraphicLib.WEST:
    				g.drawString(commName, x - w, y + height + height / 2 + h);
    				break;
    			case GraphicLib.SOUTH:
    				g.drawString(commName, x + width + width / 2, y + height + h);
    				break;
    			case GraphicLib.EAST:
    			default:
    				g.drawString(commName, x + width, y + height + height / 2 + h);
    			}
        	} 
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockDE)) {
        	if (tgc instanceof SysCAMSBlockDE && this instanceof SysCAMSPortDE) {
        		g.setColor(Color.WHITE);
            	g.fillRect(x, y, width, height);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	switch (currentOrientation) {
    			case GraphicLib.NORTH:
    				g.drawString(commName, x + width + width / 2, y);
    				break;
    			case GraphicLib.WEST:
    				g.drawString(commName, x - w, y + height + height / 2 + h);
    				break;
    			case GraphicLib.SOUTH:
    				g.drawString(commName, x + width + width / 2, y + height + h);
    				break;
    			case GraphicLib.EAST:
    			default:
    				g.drawString(commName, x + width, y + height + height / 2 + h);
    			}
        	}
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockGPIO2VCI)) {
        	if (tgc instanceof SysCAMSBlockGPIO2VCI && this instanceof SysCAMSPortDE) {
        		g.setColor(Color.WHITE);
            	g.fillRect(x, y, width, height);
            	g.setColor(c);
            	g.drawRect(x, y, width, height);
            	switch (currentOrientation) {
    			case GraphicLib.NORTH:
    				g.drawString(commName, x + width + width / 2, y);
    				break;
    			case GraphicLib.WEST:
    				g.drawString(commName, x - w, y + height + height / 2 + h);
    				break;
    			case GraphicLib.SOUTH:
    				g.drawString(commName, x + width + width / 2, y + height + h);
    				break;
    			case GraphicLib.EAST:
    			default:
    				g.drawString(commName, x + width, y + height + height / 2 + h);
    			}
        	}
        }
        if ((tgc != null) && (tgc instanceof SysCAMSBlockTDF)) {
        	if (tgc instanceof SysCAMSBlockTDF && this instanceof SysCAMSPortConverter) {
        		switch(currentOrientation) {
                case GraphicLib.NORTH:
                	g.setColor(Color.WHITE);
                	g.fillRect(x, y, width, height / 2);
                	g.setColor(Color.BLACK);
                	g.fillRect(x, y + height / 2, width, height / 2);
                	g.setColor(c);
                	g.drawRect(x, y, width, height);
                	g.drawString(commName, x + width + width / 2, y);
                	break;
                case GraphicLib.SOUTH:
                	g.setColor(Color.BLACK);
                	g.fillRect(x, y, width, height / 2);
                	g.setColor(Color.WHITE);
                	g.fillRect(x, y + height / 2, width, height / 2);
                	g.setColor(c);
                	g.drawRect(x, y, width, height);
                	g.drawString(commName, x + width + width / 2, y + height + h);
                	break;
                case GraphicLib.WEST:
                	g.setColor(Color.WHITE);
                	g.fillRect(x, y, width / 2, height);
                	g.setColor(Color.BLACK);
                	g.fillRect(x + width / 2, y, width / 2, height);
                	g.setColor(c);
                	g.drawRect(x, y, width, height);
                	g.drawString(commName, x - w, y + height + height / 2 + h);
                	break;
                case GraphicLib.EAST:
                default:
                	g.setColor(Color.BLACK);
                	g.fillRect(x, y, width / 2, height);
                	g.setColor(Color.WHITE);
                	g.fillRect(x + width / 2, y, width / 2, height);
                	g.setColor(c);
                	g.drawRect(x, y, width, height);
                	g.drawString(commName, x + width, y + height + height / 2 + h);
                }
        	}
        }
        
        g.setFont(fold);
    }

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
            setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
            setMoveCd(x, y);
            oldx = -1;
            oldy = -1;
        }
        if ((father != null) && (father instanceof SysCAMSBlockDE)) {
        	setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
        	setMoveCd(x, y);
        	oldx = -1;
        	oldy = -1;
        }
        if ((father != null) && (father instanceof SysCAMSBlockGPIO2VCI)) {
        	setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
        	setMoveCd(x, y);
        	oldx = -1;
        	oldy = -1;
        }
        if ((father != null) && (father instanceof ELNCluster)) {
        	setCdRectangle(0-getWidth()/2, father.getWidth() - (getWidth()/2), 0-getHeight()/2, father.getHeight() - (getHeight()/2));
        	setMoveCd(x, y);
        	oldx = -1;
        	oldy = -1;
        }
        if ((father != null) && (father instanceof ELNModule)) {
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
    
	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		for(int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch(c) {
			case '&' :  
				buffer.append("&amp;");       
				break;
			case '\"' : 
				buffer.append("&quot;");      
				break;
			case '\'' : 
				buffer.append("&apos;");      
				break;
			case '<' :  
				buffer.append("&lt;");        
				break;
			case '>' :  
				buffer.append("&gt;");        
				break;
			case '\u03BC':
				buffer.append("&#x3BC;");
				break;
			default :   
				buffer.append(databuf.charAt(pos)); 
				break;
			}
		}
		return buffer;
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
        	sb.append("\" type=\"" + encode(((SysCAMSPortTDF) this).getTDFType()));
        }
        if (this instanceof SysCAMSPortDE) {
//        	sb.append("\" period=\"" + ((SysCAMSPortDE) this).getPeriod());
//        	sb.append("\" time=\"" + ((SysCAMSPortDE) this).getTime());
//        	sb.append("\" rate=\"" + ((SysCAMSPortDE) this).getRate());
//        	sb.append("\" delay=\"" + ((SysCAMSPortDE) this).getDelay());
        	sb.append("\" type=\"" + encode(((SysCAMSPortDE) this).getDEType()));
        	sb.append("\" sensitive=\"" + ((SysCAMSPortDE) this).getSensitive());
        	sb.append("\" sensitive_method=\"" + ((SysCAMSPortDE) this).getSensitiveMethod());
        }
        if (this instanceof SysCAMSPortConverter) {
        	sb.append("\" period=\"" + ((SysCAMSPortConverter) this).getPeriod());
        	sb.append("\" time=\"" + ((SysCAMSPortConverter) this).getTime());
        	sb.append("\" rate=\"" + ((SysCAMSPortConverter) this).getRate());
        	sb.append("\" delay=\"" + ((SysCAMSPortConverter) this).getDelay());
        	sb.append("\" type=\"" + encode(((SysCAMSPortConverter) this).getConvType()));
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
            String type, time, sensitiveMethod; 
            Boolean sensitive;

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
								setPortName(commName);
								if (this instanceof SysCAMSPortTDF) {
									period = Integer.decode(elt.getAttribute("period")).intValue();
									time = elt.getAttribute("time");
									rate = Integer.decode(elt.getAttribute("rate")).intValue();
									delay = Integer.decode(elt.getAttribute("delay")).intValue();
									type = elt.getAttribute("type");
									((SysCAMSPortTDF) this).setPeriod(period);
									((SysCAMSPortTDF) this).setTime(time);
									((SysCAMSPortTDF) this).setRate(rate);
									((SysCAMSPortTDF) this).setDelay(delay);
									((SysCAMSPortTDF) this).setTDFType(type);
								} else if (this instanceof SysCAMSPortDE) {
									// ((SysCAMSPortDE)this).setPeriod(period);
									// ((SysCAMSPortDE)this).setTime(time);
									// ((SysCAMSPortDE)this).setRate(rate);
									// ((SysCAMSPortDE)this).setDelay(delay);
									type = elt.getAttribute("type");
									sensitive = Boolean.parseBoolean(elt.getAttribute("sensitive"));
									sensitiveMethod = elt.getAttribute("sensitive_method");
									((SysCAMSPortDE) this).setDEType(type);
									((SysCAMSPortDE) this).setSensitive(sensitive);
									((SysCAMSPortDE) this).setSensitiveMethod(sensitiveMethod);
								} else if (this instanceof SysCAMSPortConverter) {
									period = Integer.decode(elt.getAttribute("period")).intValue();
									time = elt.getAttribute("time");
									rate = Integer.decode(elt.getAttribute("rate")).intValue();
									delay = Integer.decode(elt.getAttribute("delay")).intValue();
									type = elt.getAttribute("type");
									((SysCAMSPortConverter) this).setPeriod(period);
									((SysCAMSPortConverter) this).setTime(time);
									((SysCAMSPortConverter) this).setRate(rate);
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
        value = getPortName();
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
}
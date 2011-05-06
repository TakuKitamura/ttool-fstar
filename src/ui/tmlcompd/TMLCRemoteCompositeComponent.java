/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
* Class TMLCRemoteCompositeComponent
* Composite Component. To be used in TML component task diagrams
* Creation: 12/06/2008
* @version 1.0 12/06/2008
* @author Ludovic APVRILLE
* @see
*/

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import myutil.*;
import ui.*;
import ui.window.*;

import tmltranslator.*;

public class TMLCRemoteCompositeComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
	private int maxFontSize = 20;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private int spacePt = 3;
	private Color myColor;
	private int iconSize = 17;
	private boolean iconIsDrawn = false;
	
	private int textX = 15; // border for ports
	private double dtextX = 0.0;	
	
	private int defaultDiag = 10;
	private double ddefaultDiag = 0.0;
	
	private TMLCCompositeComponent tmlcc;
	private ArrayList<TMLCCompositePort> ports;
	
	private NodeList nl;
    
    public TMLCRemoteCompositeComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(250, 200);
		
		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 0;
        //connectingPoint = new TGConnectingPoint[0];
        //connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        
        //addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
		multieditable = true;
        
		value = "No reference";
		name = "Remote composite component";
		
		ports = new ArrayList<TMLCCompositePort>();
		
        myImageIcon = IconManager.imgic1200;
    }
	
	public void updateReference(TMLCCompositeComponent tmlccc) {
		if (tmlcc == tmlccc) {
			updateReference();
		}
	}
	
	public void updateReference() {
		//TraceManager.addDev("Update reference");
		if (tmlcc != null) {
			if (tmlcc.getTDiagramPanel() == null) {
				tmlcc = null;
				value =  "No reference";
			} else {
				// Update
				value = tmlcc.getExtendedValue();
				updatePorts();
			}
		}
	}
    
    public void internalDrawing(Graphics g) {
		int w;
		int c;
		Font f = g.getFont();
		Font fold = f;
		
		if (tmlcc == null) {
			// Use value to find the component
			//TraceManager.addDev("value=" + value);
			tmlcc = tdp.getMGUI().getCompositeComponent(value);
			if (tmlcc == null) {
				value =  "No reference";
			}
		} else {
			if (tmlcc.getTDiagramPanel() == null) {
				tmlcc = null;
				value =  "No reference";
			} else {
				if (ports.size() != tmlcc.getCompositePortNb()) {
					//TraceManager.addDev("Difference of port number");
					updateReference();
				}
			}
		}
		//FontMetrics fm = g.getFontMetrics();
		
		if (myColor == null) {
			myColor = new Color(251, 252, 200- (getMyDepth() * 10), 200);
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
			
			//f = f.deriveFont((float)maxCurrentFontSize);
			//g.setFont(f);
			while(maxCurrentFontSize > (minFontSize-1)) {
				f = f.deriveFont((float)maxCurrentFontSize);
				g.setFont(f);
				w = g.getFontMetrics().stringWidth(value);
				//w = fm.stringWidth(value);
				c = width - iconSize - (2 * textX);
				//TraceManager.addDev("Font size=" + maxCurrentFontSize + " w=" + w + " c=" + c + "value=" + value);
				if (w < c) {
					break;
				}
				maxCurrentFontSize --;
				
			}
			currentFontSize = maxCurrentFontSize;
			
			if(currentFontSize <minFontSize) {
				displayText = false;
			} else {
				displayText = true;
				//f = f.deriveFont((float)currentFontSize);
				//g.setFont(f);
			}
			
		}
		
		// Zoom is assumed to be computed
		Color col = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(col);
		}
		
        // Font size 
		if (displayText) {
			f = f.deriveFont((float)currentFontSize);
			g.setFont(f);
			w = g.getFontMetrics().stringWidth(value);
			//TraceManager.addDev("Display text: Font size=" + currentFontSize + " w=" + w + " value=" + value);
			if (!(w < (width - 2 * (iconSize + textX)))) {
				g.drawString(value, x + textX + 1, y + currentFontSize + textX);
			} else {
				g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
			}
		}
		
		g.setFont(fold);
		
		
		// Ports
		/*int cpt = 0;
		TGConnectingPoint point;
		int xp, yp;
		Color pc;
		for (TMLCCompositePort port: ports) {
			pc = port.getPortColor();
			if (pc == null) {
				pc = myColor;
			}
			point = connectingPoint[cpt];
			xp = point.getX() - (defaultDiag / 2);
			yp = point.getY() - (defaultDiag / 2);
			//myColor = new Color(251, 252, 155- (port.getMyDepth() * 10));
			g.setColor(pc);
			g.fillOval(xp, yp, defaultDiag+1, defaultDiag+1);
			g.setColor(col);
			g.drawOval(xp, yp, defaultDiag, defaultDiag);
			cpt += 5;
		}*/
		
		
		// Icon
		if ((width>30) && (height > (iconSize + 2*textX))) {
			g.drawImage(IconManager.imgic1200.getImage(), x + width - iconSize - textX, y + textX, null);
			iconIsDrawn = true;
		} else {
			iconIsDrawn = false;
		}
    }
	
	public void rescale(double scaleFactor){
		dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
		textX = (int)(dtextX);
		dtextX = dtextX - textX; 
		
		ddefaultDiag = (defaultDiag + ddefaultDiag) / oldScaleFactor * scaleFactor;
		defaultDiag = (int)(ddefaultDiag);
		ddefaultDiag = ddefaultDiag - defaultDiag; 
		
		super.rescale(scaleFactor);
	}
    
    public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }
	
    
    public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		
		// On the icon?
		if (iconIsDrawn && (tmlcc != null)) {
			if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize - textX, y + textX, iconSize, iconSize)) {
				boolean b = tdp.getMGUI().selectHighLevelTab(tmlcc.getValuePanel());
				if (b) {
					//TraceManager.addDev("got tab");
					return false;
				}
			}
		}
		
		JDialogTMLRemoteCompositeComponent dialog = new JDialogTMLRemoteCompositeComponent(frame, "Setting referenced component", this);
		dialog.setSize(400, 350);
        GraphicLib.centerOnParent(dialog);
        dialog.show(); // blocked until dialog has been closed
        
		if (!dialog.isRegularClose()) {
			return false;
		}
		
		if (dialog.getReferenceComponentName() == null) {
			return false;
		}
		
		if (dialog.getReferenceComponentName().length() != 0) {
			//TraceManager.addDev("reference=" + dialog.getReferenceComponentName());
			tmlcc = getTDiagramPanel().getMGUI().getCompositeComponent(dialog.getReferenceComponentName());
			if (tmlcc != null ){
				updateReference();
				rescaled = true;
			}
		}
		return false;
    }
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        
		sb.append("<info ref=\"" + value + "\" "); 
        sb.append("/>\n");
		
		for (TMLCCompositePort port: ports) {
			sb.append("<port id=\"" + port.getId() + "\" />\n");
		}
		
        sb.append("</extraparam>\n");
		
        return new String(sb);
    }
	
	
	public void loadExtraParam(NodeList _nl, int decX, int decY, int decId) throws MalformedModelingException{
		nl = _nl;
		//delayedLoad();
	}
	
	public void delayedLoad() throws MalformedModelingException {
		
		
        TraceManager.addDev("*** load extra synchro ***");
        try {
            
            NodeList nli;
            Node n1, n2;
            Element elt;
            int t1id;
			int j, k;
			int index;
			int cpt;
			int cptk = 0;
			TMLCReferencePortConnectingPoint point;
			TMLCRemotePortCompositeComponent pcc;
			TGConnectingPoint[] old = null;
			
			ArrayList<TMLCCompositePort> tmp = null;
			ArrayList<TMLCReferencePortConnectingPoint> points = new ArrayList<TMLCReferencePortConnectingPoint>();
			
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //TraceManager.addDev(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(j=0; i<nli.getLength(); i++) {
                        n2 = nli.item(i);
                        //TraceManager.addDev(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
								value = elt.getAttribute("ref");
								tmlcc = getTDiagramPanel().getMGUI().getCompositeComponent(value);
								//TraceManager.addDev("Updating references");
								if (tmlcc != null ){
									//TraceManager.addDev("Found tmlcc");
									updateReference();
									rescaled = true;
									tmp = ports;
									ports = new ArrayList<TMLCCompositePort>();
									for (TMLCCompositePort port: tmp) {
										ports.add(port);
									}
								}
							}
							
							
							if (elt.getTagName().equals("port")) {
								if (old == null) {
									old =  connectingPoint;
									connectingPoint = new TGConnectingPoint[nbConnectingPoint];
								}
								//TraceManager.addDev("Tag port");
								try {
									int portid = Integer.decode(elt.getAttribute("id")).intValue();
									
									for (TMLCCompositePort port: tmp) {
										if (port.getId() == portid) {
											//ports.add(port);
											//TraceManager.addDev("Load: Adding port of id= " + portid);
											index = tmp.indexOf(port);
											
											//TraceManager.addDev("Updating port of id=" + portid );
											for (k=index*5; k<(index+1)*5; k++) {
												//points.add((TMLCReferencePortConnectingPoint)(connectingPoint[k]));
												// Must update position of connecting point
												connectingPoint[k] = old[cptk];
												
												if ((k % 5) == 0) {
													if (nbInternalTGComponent > (k/5)) {
														pcc = (TMLCRemotePortCompositeComponent)(tgcomponent[k/5]);
														if (pcc != null) {
															pcc.setElements(port, (TMLCReferencePortConnectingPoint)(connectingPoint[k]));
														}
													}
												}
												
												((TMLCReferencePortConnectingPoint)(connectingPoint[k])).setPort(port);
												if (connectingPoint[k] == null) {
													TraceManager.addDev("null cp");
												}
												//TraceManager.addDev("k =" + + k + " is set to the id of cptk=" + cptk);
												cptk ++;
												
												//((TMLCReferencePortConnectingPoint)connectingPoint[k]).setPort(port);
												//point = new TMLCReferencePortConnectingPoint(port, this, 0.5, 0.5);
												//points.add(point);
												//TraceManager.addDev("FormerId:" + connectingPoint[k].getId());
												//point.forceId(connectingPoint[k].getId());
												//TraceManager.addDev("Adding point of id: " +  point.getId());
											}
											// Connexion
											//TraceManager.addDev("Adding port of id:" +  portid);
											break;
										}
									}
								} catch (Exception e) {//TraceManager.addDev("Exception TMLCRemote: " + e.getMessage());
								}
							}
							
                        }
                    }
                }
            }
			
			/*nbConnectingPoint = points.size();
			TraceManager.addDev("Size: " + points.size());
			TGConnectingPoint[] old =  connectingPoint;
			connectingPoint = new TGConnectingPoint[nbConnectingPoint];
			cpt = 0;
			for(TMLCPortConnectingPoint pt: points) {
				connectingPoint[cpt] = pt;
				cpt ++;
				connectingPoint[cpt].forceId(old[cpt].getId());
				TraceManager.addDev("Setting id: " + old[cpt].getId());
			}*/
            
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
		
    }
    
    
    public int getType() {
        return TGComponentManager.TMLCTD_CREMOTECOMPONENT;
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
	
    
    public void hasBeenResized() {
		rescaled = true;
        /*for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				((TMLCCompositeComponent)tgcomponent[i]).resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCPrimitiveComponent) {
				((TMLCPrimitiveComponent)tgcomponent[i]).resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCCompositePort) {
				((TMLCCompositePort)tgcomponent[i]).resizeWithFather();
			}
        }*/
		
		if (getFather() != null) {
			resizeWithFather();
		}
    }
	
	public void resizeWithFather() {
        if ((father != null) && ((father instanceof TMLCCompositeComponent) ||(father instanceof TMLCPrimitiveComponent))) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public ArrayList<TMLCPrimitiveComponent> getAllPrimitiveComponents() {
		if (tmlcc == null) {
			return new ArrayList<TMLCPrimitiveComponent>();
		}
		return tmlcc.getAllPrimitiveComponents();
	}
	
	public ArrayList<TMLCRecordComponent> getAllRecordComponents() {
		if (tmlcc == null) {
			return new ArrayList<TMLCRecordComponent>();
		}
		return tmlcc.getAllRecordComponents();
	}
	
	public ArrayList<TMLCCompositePort> getAllInternalCompositePorts() {
		ArrayList<TMLCCompositePort> list = new ArrayList<TMLCCompositePort>();
		if (tmlcc == null) {
			return list;
		}
		return tmlcc.getAllInternalCompositePorts();
	}
	
	public ArrayList<TMLCPrimitivePort> getAllInternalPrimitivePorts() {
		ArrayList<TMLCPrimitivePort> list = new ArrayList<TMLCPrimitivePort>();
		if (tmlcc == null) {
			return list;
		}
		return tmlcc.getAllInternalPrimitivePorts();
	}
	
	public TMLCPrimitiveComponent getPrimitiveComponentByName(String _name) {
		if (tmlcc == null) {
			return null;
		}
		return tmlcc.getPrimitiveComponentByName(_name);
	}
	
	public void drawTGConnectingPoint(Graphics g, int type) {
        //TraceManager.addDev("I am " + getName());
        for (int i=0; i<nbConnectingPoint; i++) {
            if (connectingPoint[i].isCompatibleWith(type)) {
                connectingPoint[i].draw(g);
            }
        }
		
        /*for(int i=0; i<nbInternalTGComponent; i++) {
			if (hiddeni) {
				if (tgcomponent[i] instanceof TMLCCompositePort) {
					tgcomponent[i].drawTGConnectingPoint(g, type);
				}
			} else {
				tgcomponent[i].drawTGConnectingPoint(g, type);
			}
            
        }*/
    }
	
	// 5 tgconnecting per port: we show only them toward the exterior of the component
	// Add to ports the new port and remove to ports the removed ports
	// Update tgconnecting points accordingly. Those points should point to their original ones so as to be sure to be drawn at the right place
	// to a list of those points, keep that list, and then, generate a array of those points.
	public void updatePorts() {
		//TraceManager.addDev("Update my ports");
		ArrayList<TMLCCompositePort> list = tmlcc.getFirstLevelCompositePorts();
		int cpt=0;
		
		/*TraceManager.addDev("list size:" + list.size());
		for (TMLCCompositePort port2: list) {
			TraceManager.addDev("port #" + cpt  + " x = " + port2.getX() + " y = " + port2.getY() + " id= " + port2.getId());
			cpt ++;
		}*/
		
		int i, j;
		TMLCCompositePort tmp;
		TMLCReferencePortConnectingPoint point;
		int x1, y1, x2, y2, w, h;
		
		
		// Close attention to the list
		boolean change = true;
		if (list.size() != ports.size()) {
			change = true;
		} else {
			for (TMLCCompositePort port: ports) {
				if (!list.contains(port)) {
					change = true;
					break;
				}
			}
		}
		
		if (change) {
			TraceManager.addDev("change on  ports!");
			// Delete unused ports and 
			ArrayList<TMLCReferencePortConnectingPoint> points = new ArrayList<TMLCReferencePortConnectingPoint>();
			cpt=0;
			
			for(i=0; i<ports.size(); i++) {
				tmp = ports.get(i);
				if (list.contains(tmp)) {
					for (j=cpt; j<cpt+5; j++) {
						points.add((TMLCReferencePortConnectingPoint)(connectingPoint[cpt]));
					}
				} else {
					//TraceManager.addDev("Port to remove");
					ports.remove(tmp);
					for (j=cpt; j<cpt+5; j++) {
						tdp.removeOneConnector(connectingPoint[cpt]);
						// Shall we remove the connecting points?
					}
					i --;
				}
				cpt = cpt + 5;
			}
			
			// Add new ports
			for (TMLCCompositePort port1: list) {
				if (!ports.contains(port1)) {
					ports.add(port1);
					//TraceManager.addDev("Adding port of id =" + port1.getId()); 
					//Create new connecting points
					/*x1 = port1.getX() + port1.getWidth()/2;
					y1 = port1.getY() + port1.getHeight()/2;
					x2 = port1.getFather().getX();
					y2 = port1.getFather().getY();
					w = port1.getFather().getWidth();
					h = port1.getFather().getHeight();*/
					for(j=0; j<5; j++) {
						//point = new TMLCReferencePortConnectingPoint(port1, this, 0, 0, true, true, ((double)x1 - x2) / w, ((double)y1 - y2) / h);
						point = new TMLCReferencePortConnectingPoint(port1, this, 0.5, 0.5);
						points.add(point);
						//TraceManager.addDev("Adding point on update Ports of id: " +  point.getId());
					}
				}
			}
			
			if (nbConnectingPoint == points.size()) {
				//cpt = 0;
				/*for(TMLCReferencePortConnectingPoint pt: points) {
					((TMLCReferencePortConnectingPoint)(connectingPoint[cpt])).setPort(pt.getPort());
					cpt ++;
				}*/
			} else {
				nbConnectingPoint = points.size();
				/*if (connectingPoint != null) {
					//TraceManager.addDev("Nb of TGconnecting point: " +  connectingPoint.length);
				} else {
					//TraceManager.addDev("No connectingPoint");
				}*/
				TMLCRemotePortCompositeComponent tgp;
				connectingPoint = new TGConnectingPoint[nbConnectingPoint];
				nbInternalTGComponent = nbConnectingPoint/5;
				tgcomponent = new TGComponent[nbInternalTGComponent];
				cpt = 0;
				int cpttg = 0;
				
				for(TMLCPortConnectingPoint pt: points) {
					connectingPoint[cpt] = pt;
					if ((cpt % 5) == 0) {
						tgp = new TMLCRemotePortCompositeComponent(getX(), getY(), 0, 0, 10, 10, false, this, tdp);
						tgp.setElements(ports.get(cpttg), (TMLCReferencePortConnectingPoint)pt);
						tgcomponent[cpttg] = tgp;
						cpttg ++;
					}
					cpt ++;
				}
			}
		}
		
	}
	
	public TGComponent getPortOf(TGConnectingPoint tp) {
		for (int i=0; i<nbConnectingPoint; i++) {
			if (connectingPoint[i] == tp) {
				return ports.get((int)(i/5));
			}
		}
		return null;
	}
	
	public boolean setIdTGConnectingPoint(int num, int id) {
		int i;
        //TraceManager.addDev("name= " + name + " nbCP=" + nbConnectingPoint + " num=" + num +  "id=" + id);
		try {
			
			if (connectingPoint == null) {
				nbConnectingPoint = num + 1;
				connectingPoint = new TGConnectingPoint[nbConnectingPoint];
				for(i=0; i<nbConnectingPoint; i++) {
					connectingPoint[i] = new TMLCReferencePortConnectingPoint(null, this, 0.5, 0.5);
				}
			} else {
				if (num >= nbConnectingPoint) {
					nbConnectingPoint = num + 1;
					TGConnectingPoint[] old = connectingPoint;
					connectingPoint = new TGConnectingPoint[nbConnectingPoint];
					//TraceManager.addDev("old1");
					for(i=0; i<old.length; i++) {
						connectingPoint[i] = old[i];
					}
					//TraceManager.addDev("old2");
					for(i=old.length; i<nbConnectingPoint; i++) {
						connectingPoint[i] = new TMLCReferencePortConnectingPoint(null, this, 0.5, 0.5);
					}
					//TraceManager.addDev("old3");
				}
			}
			
			connectingPoint[num].forceId(id);
			return true;
		} catch (Exception e) {
			TraceManager.addDev("Exception remote 1:" + e.getMessage());
			return false;
		}
    }
	
	/*public void drawWithAttributes(Graphics g) {
		if (this instanceof WithAttributes) {
			if (tdp.getAttributeState() == 2) {
				drawAttributes(g, ((WithAttributes)this).getAttributes(), true, 0, 0);
			} else {
				if((state == TGState.POINTER_ON_ME) && (tdp.getAttributeState() == 1)){
					drawAttributes(g, ((WithAttributes)this).getAttributes(), false, tdp.currentX, tdp.currentY);
				}
			}
		}
	}*/
	
	/*public void drawAttributes(Graphics g, String attr, boolean drawAll, int _x, int _y) {
        int s0=4, s1=9, s2=15, s3=7;
        
		ColorManager.setColor(g, state, 0);
		int cpt = 0;
		TGConnectingPoint point;
		int xp, yp;
		boolean draw;
		for (TMLCCompositePort port: ports) {
			point = connectingPoint[cpt];
			xp = point.getX();
			yp = point.getY();
			
			// Must be drawn?
			draw = false;
			if (drawAll) {
				draw = true;
			} else {
				if ((Math.abs(_x - xp) < 5) && (Math.abs(_y - yp) < 5)) {
					draw = true;
				}
			}
			if (draw) {
				GraphicLib.dashedLine(g, xp, yp, xp+s2, yp);
				GraphicLib.dashedLine(g, xp+s2, yp, xp+s2+s3, yp+s3);
				
				Point p1 = drawCode(g, port.getAttributes(), xp+s2+s3, yp+s3, true, false, 4);
			
				int w = p1.x;
				int h = p1.y-yp+s3;
				GraphicLib.dashedRect(g, xp+s2+s3, yp+s3, w+15, h-12);
				//myColor = new Color(251, 252, 155- (port.getMyDepth() * 10));
				//g.drawOval(xp, yp, defaultDiag, defaultDiag);
			}
			cpt += 5;
		}
    }*/
	
	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		if (tgc instanceof TMLCRemotePortCompositeComponent) {
			return true;
		}
		
		return false;
	}
	
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		if (!acceptSwallowedTGComponent(tgc)) {
			return false;
		}
		
		tgc.setFather(this);
        tgc.setDrawingZone(true);
		
		 addInternalComponent(tgc, 0);
		 
		 return true;
	}
	
    public void removeSwallowedTGComponent(TGComponent tgc) {
	}
	
	public String getAttributes() {
		return "";
	}
    
}

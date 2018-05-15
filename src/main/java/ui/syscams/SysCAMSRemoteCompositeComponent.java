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
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
//import ui.window.JDialogTMLRemoteCompositeComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
* Class SysCAMSRemoteCompositeComponent
* Composite Component. To be used in SystemC-AMS diagrams
 * Creation: 27/04/2018
 * @version 1.0 27/04/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSRemoteCompositeComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent {
	private int maxFontSize = 20;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	private boolean displayText = true;
	private Color myColor;
	private int iconSize = 17;
	private boolean iconIsDrawn = false;
	
	private int textX = 15; // border for ports
	private double dtextX = 0.0;	
	
	private int defaultDiag = 10;
	private double ddefaultDiag = 0.0;
	
	private SysCAMSCompositeComponent syscamscc;
	private ArrayList<SysCAMSCompositePort> ports;
	
	private NodeList nl;
    
    public SysCAMSRemoteCompositeComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(250, 200);
		
		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 0;
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
		multieditable = true;
        
		value = "No reference";
		name = "Remote composite component";
		
		ports = new ArrayList<SysCAMSCompositePort>();
		
        myImageIcon = IconManager.imgic1200;
    }
	
	public void updateReference(SysCAMSCompositeComponent syscamsccc) {
		if (syscamscc == syscamsccc) {
			updateReference();
		}
	}
	
	public void updateReference() {
		if (syscamscc != null) {
			if (syscamscc.getTDiagramPanel() == null) {
				syscamscc = null;
				value =  "No reference";
			} else {
				// Update
				value = syscamscc.getExtendedValue();
				updatePorts();
			}
		}
	}
    
    public void internalDrawing(Graphics g) {
		int w;
		int c;
		Font f = g.getFont();
		Font fold = f;
		
		if (syscamscc == null) {
			// Use value to find the component
			syscamscc = tdp.getMGUI().getSysCAMSCompositeComponent(value);
			if (syscamscc == null) {
				value =  "No reference";
			}
		} else {
			if (syscamscc.getTDiagramPanel() == null) {
				syscamscc = null;
				value =  "No reference";
			} else {
				if (ports.size() != syscamscc.getCompositePortNb()) {
					updateReference();
				}
			}
		}
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
			while(maxCurrentFontSize > (minFontSize-1)) {
				f = f.deriveFont((float)maxCurrentFontSize);
				g.setFont(f);
				w = g.getFontMetrics().stringWidth(value);
				c = width - iconSize - (2 * textX);
				if (w < c) {
					break;
				}
				maxCurrentFontSize --;
			}
			currentFontSize = maxCurrentFontSize;
            displayText = currentFontSize >= minFontSize;
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
			if (!(w < (width - 2 * (iconSize + textX)))) {
				g.drawString(value, x + textX + 1, y + currentFontSize + textX);
			} else {
				g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
			}
		}
		g.setFont(fold);
		
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
		if (iconIsDrawn && (syscamscc != null)) {
			if (GraphicLib.isInRectangle(_x, _y, x + width - iconSize - textX, y + textX, iconSize, iconSize)) {
				boolean b = tdp.getMGUI().selectHighLevelTab(syscamscc.getValuePanel());
				if (b) {
					return false;
				}
			}
		}
		
//		JDialogTMLRemoteCompositeComponent dialog = new JDialogTMLRemoteCompositeComponent(frame, "Setting referenced component", this);
//        GraphicLib.centerOnParent(dialog, 400, 350);
//        dialog.setVisible( true ); // blocked until dialog has been closed
//        
//		if (!dialog.isRegularClose()) {
//			return false;
//		}
//		
//		if (dialog.getReferenceComponentName() == null) {
//			return false;
//		}
//		
//		if (dialog.getReferenceComponentName().length() != 0) {
//			tmlcc = getTDiagramPanel().getMGUI().getCompositeComponent(dialog.getReferenceComponentName());
//			if (tmlcc != null ){
//				updateReference();
//				rescaled = true;
//			}
//			tdp.repaint();
//		}
		return false;
    }
	
	protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<info ref=\"" + value + "\" "); 
        sb.append("/>\n");
		
		for (SysCAMSCompositePort port: ports) {
			sb.append("<port id=\"" + port.getId() + "\" />\n");
		}
        sb.append("</extraparam>\n");
        return new String(sb);
    }
	
	public void loadExtraParam(NodeList _nl, int decX, int decY, int decId) throws MalformedModelingException{
		nl = _nl;
	}
	
	public void delayedLoad() throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
			int j, k;
			int index;
			int cptk = 0;
			SysCAMSRemotePortCompositeComponent pcc;
			TGConnectingPoint[] old = null;
			ArrayList<SysCAMSCompositePort> tmp = null;
			
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
								value = elt.getAttribute("ref");
								syscamscc = getTDiagramPanel().getMGUI().getSysCAMSCompositeComponent(value);
								if (syscamscc != null ){
									updateReference();
									rescaled = true;
									tmp = ports;
									ports = new ArrayList<SysCAMSCompositePort>();
									for (SysCAMSCompositePort port: tmp) {
										ports.add(port);
									}
								}
							}
							
							if (elt.getTagName().equals("port")) {
								if (old == null) {
									old =  connectingPoint;
									connectingPoint = new TGConnectingPoint[nbConnectingPoint];
								}
								try {
									int portid = Integer.decode(elt.getAttribute("id")).intValue();
									
									for (SysCAMSCompositePort port: tmp) {
										if (port.getId() == portid) {
											index = tmp.indexOf(port);
											for (k=index*5; k<(index+1)*5; k++) {
												// Must update position of connecting point
												connectingPoint[k] = old[cptk];
												
												if ((k % 5) == 0) {
													if (nbInternalTGComponent > (k/5)) {
														pcc = (SysCAMSRemotePortCompositeComponent)(tgcomponent[k/5]);
														if (pcc != null) {
															pcc.setElements(port, (SysCAMSReferencePortConnectingPoint)(connectingPoint[k]));
														}
													}
												}
												((SysCAMSReferencePortConnectingPoint)(connectingPoint[k])).setPort(port);
												if (connectingPoint[k] == null) {
													TraceManager.addDev("null cp");
												}
												cptk ++;
											}
											break;
										}
									}
								} catch (Exception e) {
								}
							}
							
                        }
                    }
                }
            }
			// Make paths
			((SysCAMSComponentTaskDiagramPanel)getTDiagramPanel()).makePaths();
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
		if (getFather() != null) {
			resizeWithFather();
		}
    }
	
	public void resizeWithFather() {
        if ((father != null) && ((father instanceof SysCAMSCompositeComponent) ||(father instanceof SysCAMSPrimitiveComponent))) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public java.util.List<SysCAMSPrimitiveComponent> getAllPrimitiveComponents() {
		if (syscamscc == null) {
			return new ArrayList<SysCAMSPrimitiveComponent>();
		}
		return syscamscc.getAllPrimitiveComponents();
	}
	
	public java.util.List<SysCAMSRecordComponent> getAllRecordComponents() {
		if (syscamscc == null) {
			return new ArrayList<SysCAMSRecordComponent>();
		}
		return syscamscc.getAllRecordComponents();
	}
	
	public java.util.List<SysCAMSCompositePort> getAllInternalCompositePorts() {
		java.util.List<SysCAMSCompositePort> list = new ArrayList<SysCAMSCompositePort>();
		if (syscamscc == null) {
			return list;
		}
		return syscamscc.getAllInternalCompositePorts();
	}
	
	public ArrayList<SysCAMSPrimitivePort> getAllInternalPrimitivePorts() {
		ArrayList<SysCAMSPrimitivePort> list = new ArrayList<SysCAMSPrimitivePort>();
		if (syscamscc == null) {
			return list;
		}
		return syscamscc.getAllInternalPrimitivePorts();
	}
	
	public SysCAMSPrimitiveComponent getPrimitiveComponentByName(String _name) {
		if (syscamscc == null) {
			return null;
		}
		return syscamscc.getPrimitiveComponentByName(_name);
	}
	
	public void drawTGConnectingPoint(Graphics g, int type) {
        for (int i=0; i<nbConnectingPoint; i++) {
            if (connectingPoint[i].isCompatibleWith(type)) {
                connectingPoint[i].draw(g);
            }
        }
    }
	
	// 5 tgconnecting per port: we show only them toward the exterior of the component
	// Add to ports the new port and remove to ports the removed ports
	// Update tgconnecting points accordingly. Those points should point to their original ones so as to be sure to be drawn at the right place
	// to a list of those points, keep that list, and then, generate a array of those points.
	public void updatePorts() {
		ArrayList<SysCAMSCompositePort> list = syscamscc.getFirstLevelCompositePorts();
		int cpt=0;
		
		int i, j;
		SysCAMSCompositePort tmp;
		SysCAMSReferencePortConnectingPoint point;
		
		// Close attention to the list
		boolean change = true;
		if (list.size() != ports.size()) {
			change = true;
		} else {
			for (SysCAMSCompositePort port: ports) {
				if (!list.contains(port)) {
					change = true;
					break;
				}
			}
		}
		
		if (change) {
			TraceManager.addDev("change on  ports!");
			// Delete unused ports and 
			ArrayList<SysCAMSReferencePortConnectingPoint> points = new ArrayList<SysCAMSReferencePortConnectingPoint>();
			cpt=0;
			
			for(i=0; i<ports.size(); i++) {
				tmp = ports.get(i);
				if (list.contains(tmp)) {
					for (j=cpt; j<cpt+5; j++) {
						points.add((SysCAMSReferencePortConnectingPoint)(connectingPoint[cpt]));
					}
				} else {
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
			for (SysCAMSCompositePort port1: list) {
				if (!ports.contains(port1)) {
					ports.add(port1);
					for(j=0; j<5; j++) {
						point = new SysCAMSReferencePortConnectingPoint(port1, this, 0.5, 0.5);
						points.add(point);
					}
				}
			}
			
			if (nbConnectingPoint == points.size()) {
			} else {
				nbConnectingPoint = points.size();
				SysCAMSRemotePortCompositeComponent tgp;
				connectingPoint = new TGConnectingPoint[nbConnectingPoint];
				nbInternalTGComponent = nbConnectingPoint/5;
				tgcomponent = new TGComponent[nbInternalTGComponent];
				cpt = 0;
				int cpttg = 0;
				
				for(SysCAMSPortConnectingPoint pt: points) {
					connectingPoint[cpt] = pt;
					if ((cpt % 5) == 0) {
						tgp = new SysCAMSRemotePortCompositeComponent(getX(), getY(), 0, 0, 10, 10, false, this, tdp);
						tgp.setElements(ports.get(cpttg), (SysCAMSReferencePortConnectingPoint)pt);
						tgcomponent[cpttg] = tgp;
						cpttg ++;
					}
					cpt ++;
				}
			}
		}
	}
	
	public TGComponent getPortOf(TGConnectingPoint tp) {
		if (ports == null) {
			return null;
		}
		for (int i=0; i<nbConnectingPoint; i++) {
			if (connectingPoint[i] == tp) {
				if (i/5 < ports.size()) {
					return ports.get(i/5);
				}
			}
		}
		return null;
	}
	
	public boolean setIdTGConnectingPoint(int num, int id) {
		int i;
		try {
			if (connectingPoint == null) {
				nbConnectingPoint = num + 1;
				connectingPoint = new TGConnectingPoint[nbConnectingPoint];
				for(i=0; i<nbConnectingPoint; i++) {
					connectingPoint[i] = new SysCAMSReferencePortConnectingPoint(null, this, 0.5, 0.5);
				}
			} else {
				if (num >= nbConnectingPoint) {
					nbConnectingPoint = num + 1;
					TGConnectingPoint[] old = connectingPoint;
					connectingPoint = new TGConnectingPoint[nbConnectingPoint];
					for(i=0; i<old.length; i++) {
						connectingPoint[i] = old[i];
					}
					for(i=old.length; i<nbConnectingPoint; i++) {
						connectingPoint[i] = new SysCAMSReferencePortConnectingPoint(null, this, 0.5, 0.5);
					}
				}
			}
			connectingPoint[num].forceId(id);
			return true;
		} catch (Exception e) {
			TraceManager.addDev("Exception remote 1:" + e.getMessage());
			return false;
		}
    }

	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof SysCAMSRemotePortCompositeComponent;
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
	
	public TDiagramPanel getReferencedDiagram() {
		if (syscamscc == null) {
			return null;
		}
		return syscamscc.getTDiagramPanel();
	}
	
	public SysCAMSCompositeComponent getReference() {
		return syscamscc;
	}
}
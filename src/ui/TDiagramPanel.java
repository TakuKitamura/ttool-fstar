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
* Class TDiagramPanel
* High level panel for all kind of TURTLE diagrams
* Creation: 21/12/2003
* @version 1.0 21/12/2003
* @author Ludovic APVRILLE
* @see
*/

package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import java.awt.image.*;


import myutil.*;
import ui.oscd.*;
import ui.cd.*;
import ui.window.*;
import ui.tree.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.req.*;
import ui.ncdd.*;
// Added by Solange
import ui.procsd.*;



public abstract class TDiagramPanel extends JPanel implements GenericTree {
    
   	// for tracking changes
    public static final int NEW_COMPONENT = 0;
    public static final int NEW_CONNECTOR = 1;
    public static final int REMOVE_COMPONENT = 2;
    public static final int CHANGE_VALUE_COMPONENT = 3;
    public static final int MOVE_COMPONENT = 4;
    public static final int MOVE_CONNECTOR = 5;
    
    // For cut/copy/paste
    public static String copyData;
    /*private static int copyX;
    private static int copyY;
    private static int copyMaxId;*/
    
    protected String name;
    
    protected LinkedList componentList;
    protected TGConnectingPoint selectedConnectingPoint;
    protected TGComponent componentPointed;
    protected TGComponent componentPopup;
    protected TToolBar ttb;
	protected TGComponent fatherOfRemoved;
    
    // popupmenus
    protected ActionListener menuAL;
    protected JPopupMenu diagramMenu;
    protected JPopupMenu componentMenu;
    protected JPopupMenu selectedMenu;
    protected int popupX, popupY;
    protected JMenuItem remove, edit, clone, bringFront, bringBack, makeSquare, setJavaCode, removeJavaCode, setInternalComment, removeInternalComment, attach, detach, hide, unhide;
	protected JMenuItem checkAccessibility;
	protected JMenuItem breakpoint;
    protected JMenuItem paste, insertLibrary, upX, upY, downX, downY;
    protected JMenuItem cut, copy, saveAsLibrary, captureSelected;
    
    // Main window
    protected MainGUI mgui;
    
    // Mouse pointer
    public int currentX;
    public int currentY;
    
    //drawing area
    private int minLimit = 10;
    private int maxX = 1400;
    private int maxY = 900;
    private final int limit = 10;
    
    private final int minimumXSize = 900;
    private final int minimumYSize = 900;
    private final int increment = 500;
    
    private double zoom = 1.0;
	private boolean zoomed = false;
	
    private boolean draw;
    
    // MODE
    public int mode;
    
    public static final int NORMAL = 0;
    public static final int MOVING_COMPONENT = 1;
    public static final int ADDING_CONNECTOR = 2;
    public static final int MOVE_CONNECTOR_SEGMENT = 3;
    public static final int MOVE_CONNECTOR_HEAD = 4;
    public static final int SELECTING_COMPONENTS = 5;
    public static final int SELECTED_COMPONENTS = 6;
    public static final int MOVING_SELECTED_COMPONENTS = 7;
    public static final int RESIZING_COMPONENT = 8;
    
    // when adding connector or moving a connector head
    protected int x1;
    protected int y1;
    protected int x2;
    protected int y2;
    protected Vector listPoint;
    protected TGConnectingPoint p1, p2;
    protected int type;
    
    // For component selection
    protected int initSelectX;
    protected int initSelectY;
    protected int currentSelectX;
    protected int currentSelectY;
    protected int xSel, ySel, widthSel, heightSel;
    protected int sel = 5;
    protected boolean showSelectionZone = false;
    protected boolean selectedTemp = true;
    
    private boolean isScaled;
    private boolean overcomeShowing = false;
    
    //protected Image offScreenBuffer;
    
    public JScrollDiagramPanel jsp;
    
    // for translation process
    public int count = 0;
    
    // Panels
    public TURTLEPanel tp;
    
    // For displaying or not parameters
    protected boolean synchroVisible = true, attributesVisible = true, gatesVisible = true, channelVisible = true;
    protected boolean javaVisible = false;
	protected int internalCommentVisible = 0;
    protected boolean channelsVisible = true, eventsVisible = true, requestsVisible = true;
	protected int attributesOn = 0;
	
	int adjustMode = 0;
	
	// DIPLO ID -> for simulation purpose
	public static boolean DIPLO_ID_ON;
    
    // Constructor
    public TDiagramPanel(MainGUI _mgui, TToolBar _ttb) {
        setBackground(ColorManager.DIAGRAM_BACKGROUND);
        //setBackground(Color.red);
        //setMinimumSize(new Dimension(1000, 1000));
        //setMaximumSize(new Dimension(1000, 1000));
        setPreferredSize(new Dimension(maxX + limit, maxY + limit));
        componentList = new LinkedList();
        mgui = _mgui;
        ttb = _ttb;
        mode = NORMAL;
        
        buildPopupMenus();
    }
    
    
    // Abstract operations
    public abstract boolean actionOnDoubleClick(TGComponent tgc);
    public abstract boolean actionOnAdd(TGComponent tgc);
    public abstract boolean actionOnRemove(TGComponent tgc);
    public abstract boolean actionOnValueChanged(TGComponent tgc);
    public abstract String getXMLHead();
    public abstract String getXMLTail();
    public abstract String getXMLSelectedHead();
    public abstract String getXMLSelectedTail();
    public abstract String getXMLCloneHead();
    public abstract String getXMLCloneTail();
	
    public void setName(String _name) {
        name = _name;
    }
    
    public double getZoom() {
        return zoom;
    }
	
	/*public int getFontSize() {
		return (int)(Math.round(7.5*zoom+4.5));
	}*/
	
	public int getFontSize() {
		return (int)(Math.round(12*zoom));
	}
    
    public void setZoom(double _zoom) {
		if (_zoom < zoom) {
			if (zoom > 0.199) {
				zoom = _zoom;
			}
		} else {
			if (zoom < 5) {
				zoom = _zoom;
			}
		}
    }
	
	public void updateComponentsAfterZoom() {
		//System.out.println("Zoom factor=" + zoom);
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		boolean change = false;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof ScalableTGComponent) {
				((ScalableTGComponent)tgc).rescale(zoom);
				change = true;
			}
        }
		
		if (change) {
			mgui.changeMade(this, MOVE_COMPONENT);
		}
		
		repaint();
	}
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    public void removeAll() {
        componentList = new LinkedList();
    }
	
	public void setInternalCommentVisible(int mode) {
		internalCommentVisible = mode;
	}
	
	public int getInternalCommentVisible() {
		return internalCommentVisible;
	}
    
    public void structureChanged() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            tgc.TDPStructureChanged();
        }
    }
	
	public void setAttributes(int _attr) {
		attributesOn = _attr;
	}
	
	public int getAttributeState() {
		return attributesOn;
	}
    
    public void valueChanged() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            tgc.TDPvalueChanged();
        }
    }
    
    public int makeLovelyIds(int id) {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            id = tgc.makeLovelyIds(id);
        }
        
        return id;
    }
	
	public void selectTab(String name) {
		mgui.selectTab(tp, name);
	}
    
    
    protected void paintComponent(Graphics g) {
        paintMycomponents(g);
    }
    
    public void paintMycomponents(Graphics g) {
        paintMycomponents(g, true, 1, 1);
    }
    
    public void paintMycomponents(Graphics g, boolean b, double w, double h) {
        
		/*if (!zoomed) {
			zoomed = true;*/
			//Graphics2D g2 = (Graphics2D)g;
			//g2.scale(0.75, 0.75);
			//}
			
			if (!overcomeShowing) {
				if (!isShowing()) {
					System.out.println("Not showing!" + tp);
					return;
				}
			}
			
			//System.out.println("Draw");
			
			try {
				super.paintComponent(g);
			} catch (Exception e) {
				return;
			}
			//ZoomGraphics g = new ZoomGraphics(gr, zoom);
			if (!draw) {
				return;
			}
			
			// Draw Components
			if ((w != 1.0) ||(h != 1.0)) {
				((Graphics2D)g).scale(w, h);
				isScaled = true;
			} else {
				isScaled = false;
			}
			TGComponent tgc;
			for(int i=componentList.size()-1; i>=0; i--) {
				tgc = (TGComponent)(componentList.get(i));
				if (!tgc.isHidden()) {
					//System.out.println("Painting " + tgc.getName() + " x=" + tgc.getX() + " y=" + tgc.getY());
					tgc.draw(g);
					if (mgui.getTypeButtonSelected() != TGComponentManager.EDIT) {
						tgc.drawTGConnectingPoint(g, mgui.getIdButtonSelected());
					}
					if (mode == MOVE_CONNECTOR_HEAD) {
						tgc.drawTGConnectingPoint(g, type);
					}
					
					if (javaVisible) {
						if (tgc.hasPostJavaCode() || tgc.hasPreJavaCode()) {
							tgc.drawJavaCode(g);
						}
					}
					
					
					
					/*if (internalCommentVisible) {
						ifi (tgc.hasInternalComment()) {
							tgc.drawInternalComment(g);
						}
					}*/
					
					/*if ((attributesOn) && (tgc instanceof WithAttributes)) {
						//System.out.println("Attributes to de drawn for" + tgc);
						tgc.drawAttributes(g, ((WithAttributes)tgc).getAttributes());
					}*/
				}
			}
			
			// Draw name of component selected
			if (componentPointed != null) {
				String name1 = componentPointed.getName();
				if (componentPointed.hasFather()) {
					name1 = componentPointed.getTopLevelName() + ": " + name1;
				}
				//g.setColor(Color.black);
				//g.drawString(name, 20, 20);
				mgui.setStatusBarText(name1);
			}
			
			//Draw component being added
			if (mode == ADDING_CONNECTOR) {
				// Drawing connector
				g.setColor(Color.red);
				drawConnectorBeingAdded(g);
				g.drawLine(x1, y1, x2, y2);
			}
			
			if (mode == SELECTING_COMPONENTS) {
				g.setColor(Color.black);
				GraphicLib.dashedRect(g, Math.min(initSelectX, currentSelectX), Math.min(initSelectY, currentSelectY), Math.abs(currentSelectX -  initSelectX), Math.abs(currentSelectY - initSelectY));
			}
			
			if (((mode == SELECTED_COMPONENTS) || (mode == MOVING_SELECTED_COMPONENTS)) && (selectedTemp)) {
				if (showSelectionZone) {
					if (mode == MOVING_SELECTED_COMPONENTS) {
						g.setColor(ColorManager.MOVING_0);
					} else {
						g.setColor(ColorManager.POINTER_ON_ME_0);
					}
					GraphicLib.setMediumStroke(g);
				} else {
					g.setColor(ColorManager.NORMAL_0);
				}
				GraphicLib.dashedRect(g, xSel, ySel, widthSel, heightSel);
				g.fillRect(xSel - sel, ySel - sel, 2*sel, 2*sel);
				g.fillRect(xSel - sel + widthSel, ySel - sel, 2*sel, 2*sel);
				g.fillRect(xSel - sel, ySel - sel + heightSel, 2*sel, 2*sel);
				g.fillRect(xSel - sel + widthSel, ySel - sel + heightSel, 2*sel, 2*sel);
				if (showSelectionZone) {
					GraphicLib.setNormalStroke(g);
				}
			}
			
			// Draw new Component head
			if (mode == MOVE_CONNECTOR_HEAD) {
				g.setColor(ColorManager.MOVING_0);
				GraphicLib.dashedLine(g, x1, y1, x2, y2);
			}
			
			if ((this instanceof TDPWithAttributes) && (getAttributeState() != 0))  {
				//System.out.println("Tdp with attributes");
				for(int i=componentList.size()-1; i>=0; i--) {
					tgc = (TGComponent)(componentList.get(i));
					if (!tgc.isHidden()) {
						tgc.drawWithAttributes(g);
					}
				}
			}
			
			if (b) {
				mgui.drawBird();
			}
			
    }
    
    public boolean isScaled() {
        return isScaled;
    }
    
    public void drawConnectorBeingAdded(Graphics g) {
        int s = listPoint.size();
        Point p3, p4;
        if (s > 0) {
            p3 = (Point)(listPoint.elementAt(0));
            g.drawLine(p1.getX(), p1.getY(), p3.x, p3.y);
            for (int i=0; i< s - 1; i++) {
                p3 = (Point)(listPoint.elementAt(i));
                p4 = (Point)(listPoint.elementAt(i+1));
                g.drawLine(p3.x, p3.y, p4.x, p4.y);
            }
        }
    }
	
	/*protected void drawDIPLOID(Graphics g) {
		if (!(tdp instanceof TMLActivityDiagramPanel)) {
			return;
		}
		
		Color c = g.getColor();
		g.setColor(ColorManager.DIPLOID);
		
		TGComponent tgc;
		for(int i=componentList.size()-1; i>=0; i--) {
			tgc = (TGComponent)(componentList.get(i));
			if (!tgc.isHidden()) {
				if (tgc.getDIPLOID
				g.drawString(tgc.getDIPLOID(), x+width, y.height + 5);
			}
		}
		
		g.setColor(c);
	}*/
    
    public void loadFromXML(String s) {
        componentList = new LinkedList();
		
        mode = NORMAL;
    }
    
    public StringBuffer saveSelectedInXML() {
        StringBuffer s = componentsInXML(true);
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(getXMLSelectedHead());
        sb.append("\n");
        sb.append(s);
        sb.append("\n");
        sb.append(getXMLSelectedTail());
        return sb;
    }
    
    public StringBuffer saveInXML() {
        StringBuffer s = componentsInXML(false);
        if (s == null) {
            return null;
        }
        
        StringBuffer sb = new StringBuffer(getXMLHead());
        sb.append("\n");
        sb.append(s);
        sb.append("\n");
        sb.append(getXMLTail());
        return sb;
    }
    
    public StringBuffer saveComponentInXML(TGComponent tgc) {
        StringBuffer sb = new StringBuffer(getXMLCloneHead());
        sb.append("\n");
        sb.append(tgc.saveInXML());
        sb.append("\n");
        sb.append(getXMLCloneTail());
        //System.out.println("sb=\n" + sb);
        return sb;
    }
    
    private StringBuffer componentsInXML(boolean selected) {
        StringBuffer sb = new StringBuffer("");
        StringBuffer s;
        TGComponent tgc;
		
        //Added by Solange to see the components in the list
        LinkedList ruteoList=componentList;
        //
        Iterator iterator = componentList.listIterator();
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((selected == false) || (tgc.isSelected())) {
                s = tgc.saveInXML();
                if (s == null) {
                    return null;
                }
                sb.append(s);
                sb.append("\n");
            }
            
        }
        //System.out.println("making copy sb=\n" + sb);
        return sb;
    }
    
    
    public void activateActions(boolean b) {
        ttb.setActive(b);
    }
    
    // Selecting components
    public int selectComponentInRectangle(int x, int y, int width, int height) {
        //System.out.println("x=" + x + " y=" + y + " width=" +width + " height=" + height);
        TGComponent tgc;
        int cpt = 0;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.areAllInRectangle(x, y, width, height)) {
                tgc.select(true);
                tgc.setState(TGState.SELECTED);
                cpt ++;
            } else {
                tgc.select(false);
                tgc.setState(TGState.NORMAL);
            }
        }
        
        return cpt;
    }
    
    public void setSelectedTGConnectingPoint(TGConnectingPoint p) {
		selectedConnectingPoint = p;
    }
	
    
    // Highlighting elements
    
    // -> 0 No highlighted component, no change
    // -> 1 No highlighted, change
    // -> 2 One component highlighted, no change
    // -> 3 One component highlighted, change
    public byte highlightComponent(int x, int y) {
        TGComponent tgc, tgcTmp;
        //int state;
        //boolean b = false;
        boolean pointedElementFound = false;
        byte info = 0;
        
        TGComponent tmp = componentPointed;
        componentPointed = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            //state = tgc.getState();
            tgcTmp = tgc.isOnMeHL(x, y);
            if (tgcTmp != null) {
                if (!pointedElementFound) {
                    componentPointed = tgcTmp;
                    tgc.setState(TGState.POINTED);
                    pointedElementFound = true;
                    info = 2;
                } else {
                    tgc.setState(TGState.NORMAL);
                }
            } else {
                tgc.setState(TGState.NORMAL);
            }
        }
        
        if (tmp != componentPointed) {
            info ++;
        }
        
        return info;
    }
    
    public void highlightTGComponent(TGComponent tgc) {
        if (!componentList.contains(tgc.getTopFather())) {
            return;
        }
        
        highlightComponent(-1, -1);
        
        if (tgc.getState() == TGState.NORMAL) {
            if (tgc.getTopFather() == tgc) {
                tgc.setSelectedInternalTGComponent(null);
            } else {
                tgc.getTopFather().setSelectedInternalTGComponent(tgc);
            }
            tgc.getTopFather().setState(TGState.POINTED);
            componentPointed = tgc;
            repaint();
        }
    }
    
    public TGComponent componentPointed() {
        return componentPointed;
    }
    
    public void updateJavaCode() {
        mgui.setJavaPreCode(componentPointed);
        mgui.setJavaPostCode(componentPointed);
    }
    
    public TGConnectingPoint findConnectingPoint(int id) {
        TGComponent tgc;
        TGConnectingPoint p;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            p = tgc.findConnectingPoint(id);
            if (p != null) {
                return p;
            }
        }
        return null;
        
    }
    
    public TGComponent findComponentWithId(int id) {
        TGComponent tgc1, tgc2;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponent)(iterator.next());
            tgc2 = tgc1.containsLoadedId(id);
            if (tgc2 != null) {
                return tgc2;
            }
        }
        return null;
    }
    
    public TGConnector findTGConnectorStartingAt(CDElement c) {
        TGComponent tgc;
        TGConnector tgco;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnector) {
                tgco = (TGConnector) tgc;
                if (tgco.isP1(c)) {
                    return tgco;
                }
                
            }
        }
        return null;
    }
	
	public TGConnector findTGConnectorUsing(CDElement c) {
        TGComponent tgc;
        TGConnector tgco;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnector) {
                tgco = (TGConnector) tgc;
                if (tgco.isP1(c)) {
                    return tgco;
                }
				if (tgco.isP2(c)) {
                    return tgco;
                }
                
            }
        }
        return null;
    }
    
    
    public boolean highlightOutAndFreeConnectingPoint(int x, int y, int type) {
        TGComponent tgc;
        TGConnectingPoint cp;
        int state;
        boolean b = false;
        boolean pointedElementFound = false;
        selectedConnectingPoint = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (pointedElementFound == true) {
                b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            }
            if (pointedElementFound == false) {
                cp = tgc.getFreeTGConnectingPointAtAndCompatible(x, y, type);
                if ((cp != null) && (cp.isOut()) && (cp.isFree()) && (cp.isCompatibleWith(type))) {
                    selectedConnectingPoint = cp;
                    pointedElementFound = true;
                    b = cp.setState(TGConnectingPoint.SELECTED) || b;
                } else {
                    b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
                }
            }
        }
        return b;
    }
    
    /*public boolean highlightOutAndFreeConnectingPoint(int x, int y) {
        TGComponent tgc;
        TGConnectingPoint cp;
        int state;
        boolean b = false;
        boolean pointedElementFound = false;
        selectedConnectingPoint = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (pointedElementFound == true) {
                b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            }
            if (pointedElementFound == false) {
                cp = tgc.getFreeTGConnectingPointAtAndCompatible(x, y, type);
                if ((cp != null) && (cp.isOut()) && (cp.isFree())) {
                    selectedConnectingPoint = cp;
                    pointedElementFound = true;
                    b = cp.setState(TGConnectingPoint.SELECTED) || b;
                } else {
                    b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
                }
            }
        }
        return b;
    }*/
    
    public boolean highlightInAndFreeConnectingPoint(int x, int y, int type) {
        TGComponent tgc;
        TGConnectingPoint cp;
        int state;
        boolean b = false;
        boolean pointedElementFound = false;
        selectedConnectingPoint = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (pointedElementFound == true) {
                b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
            }
            if (pointedElementFound == false) {
                cp = tgc.getFreeTGConnectingPointAtAndCompatible(x, y, type);
                if ((cp != null) && (cp.isIn()) && (cp.isFree())) {
                    selectedConnectingPoint = cp;
                    pointedElementFound = true;
                    b = cp.setState(TGConnectingPoint.SELECTED) || b;
                } else {
                    b =  tgc.setStateTGConnectingPoint(TGConnectingPoint.NORMAL) || b;
                }
            }
        }
        return b;
    }
    
    
    public TGConnectingPoint getSelectedTGConnectingPoint() {
        return selectedConnectingPoint;
    }
    
    // Adding component
    public TGComponent addComponent(int x, int y, boolean swallow) {
        return addComponent(x, y, mgui.getIdButtonSelected(), swallow);
    }
    
	public TGComponent addComponent(int x, int y, int id, boolean swallow) {
		TGComponent tgc = TGComponentManager.addComponent(x, y, id, this);
		addComponent(tgc, x, y, swallow, true);
		return tgc;
	}
	
	// return true if swallowed
    public boolean addComponent(TGComponent tgc, int x, int y, boolean swallow, boolean addToList) {
        boolean ret = false;
        //System.out.println("add component " + tgc.getName());
        if (tgc != null) {
            if ((swallow) && (tgc instanceof SwallowedTGComponent)) {
                //System.out.println("Swallowed component !");
                SwallowTGComponent stgc = findSwallowTGComponent(x, y, tgc);
                if (stgc != null) {
                    stgc.addSwallowedTGComponent(tgc, x, y);
					tgc.wasSwallowed();
					ret = true;
                } else {
					if (addToList) {
						componentList.add(0, tgc);
					}
                }
            } else {
				if (addToList) {
					componentList.add(0, tgc);
				}
            }
        }
		
		if (tgc instanceof SpecificActionAfterAdd) {
			((SpecificActionAfterAdd)tgc).specificActionAfterAdd();
		}
		
		return false;
    }
    
	public SwallowTGComponent findSwallowTGComponent(int x, int y) {
		return findSwallowTGComponent(x, y, null);
	}
	
    public SwallowTGComponent findSwallowTGComponent(int x, int y, TGComponent tgcdiff) {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc instanceof SwallowTGComponent) && (tgc.isOnMeHL(x, y) != null) && (tgc != tgcdiff)) {
                return ((SwallowTGComponent)tgc);
            }
        }
        return null;
    }
    
    public void addBuiltComponent(TGComponent tgc) {
        if (tgc != null) {
            componentList.add(tgc);
        }
    }
    
    public void addBuiltConnector(TGConnector tgc) {
        if (tgc != null) {
            componentList.add(tgc);
        }
    }
    
    public LinkedList getComponentList() {
        return componentList;
    }
    
    // Adding connector
    
    public void addingTGConnector() {
        listPoint = new Vector();
        p1 = getSelectedTGConnectingPoint();
        x1 = p1.getX(); y1 = p1.getY();
        selectedConnectingPoint.setFree(false);
    }
    
    public void setAddingTGConnector(int _x2, int _y2) {
        x2 = _x2; y2 = _y2;
    }
    
    public void addPointToTGConnector(int x, int y) {
        listPoint.addElement(new Point(x, y));
        x1 = x;
        y1 = y;
    }
    
    public void finishAddingConnector(TGConnectingPoint p2) {
        TGConnector tgco = TGComponentManager.addConnector(p1.getX(), p1.getY(), mgui.getIdButtonSelected(), this, p1, p2, listPoint);
        if (tgco != null) {
            p2.setFree(false);
            componentList.add(0, tgco);
			if (tgco instanceof SpecificActionAfterAdd) {
				((SpecificActionAfterAdd)tgco).specificActionAfterAdd();
			}
            stopAddingConnector(false);
            p1.setFree(false);
        } else {
            p2.setFree(true);
            stopAddingConnector(true);
            p1.setFree(true);
        }
    }
    
    // true if connector not added
    public void stopAddingConnector(boolean b) {
        p1.setFree(true);
        x1 = -1; x2= -1; y1 = -1; y2 = -1;
        listPoint = null;
    }
    
    public void setMovingHead(int _x1, int _y1, int _x2, int _y2) {
        x1 = _x1; y1 = _y1;
        x2 = _x2; y2 = _y2;
    }
    
    public void setConnectorHead(TGComponent tgc) {
        type = TGComponentManager.getType(tgc);
        //System.out.println("class tgc=" + tgc.getClass());
        //System.out.println("type=" + type);
    }
    
    
    // Multi-select
    public void setSelectingComponents(int x, int y) {
        x = Math.min(Math.max(minLimit, x), maxX);
        y = Math.min(Math.max(minLimit, y), maxY);
        initSelectX = x;
        currentSelectX = x;
        initSelectY = y;
        currentSelectY = y;
    }
    
    public void updateSelectingComponents(int x, int y) {
        x = Math.min(Math.max(minLimit, x), maxX);
        y = Math.min(Math.max(minLimit, y), maxY);
        currentSelectX = x;
        currentSelectY = y;
        
        selectComponentInRectangle(Math.min(currentSelectX, initSelectX), Math.min(currentSelectY, initSelectY), Math.abs(currentSelectX - initSelectX), Math.abs(currentSelectY - initSelectY));
    }
    
    public void endSelectComponents() {
        int nb = selectComponentInRectangle(Math.min(currentSelectX, initSelectX), Math.min(currentSelectY, initSelectY), Math.abs(currentSelectX - initSelectX), Math.abs(currentSelectY - initSelectY));
        if (nb == 0) {
            mode = NORMAL;
            mgui.setMode(MainGUI.CUTCOPY_KO);
            mgui.setMode(MainGUI.EXPORT_LIB_KO);
        } else {
            mode = SELECTED_COMPONENTS;
            mgui.setMode(MainGUI.CUTCOPY_OK);
            mgui.setMode(MainGUI.EXPORT_LIB_OK);
            showSelectionZone = true;
            xSel = Math.min(currentSelectX, initSelectX);
            ySel = Math.min(currentSelectY, initSelectY);
            widthSel = Math.abs(currentSelectX - initSelectX);
            heightSel = Math.abs(currentSelectY - initSelectY);
        }
    }
    
    public void unselectSelectedComponents() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            tgc.select(false);
            tgc.setState(TGState.NORMAL);
        }
    }
    
    
    
    public boolean showSelectionZone(int x, int y) {
        if (GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel)) {
            if (!showSelectionZone) {
                showSelectionZone = true;
                return true;
            } else {
                return false;
            }
        } else {
            if (showSelectionZone) {
                showSelectionZone = false;
                return true;
            } else {
                return false;
            }
        }
    }
    
    public void setMovingSelectedComponents() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
                tgc.setState(TGState.MOVING);
            }
        }
    }
    
    public void setStopMovingSelectedComponents() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
                tgc.setState(TGState.SELECTED);
            }
        }
    }
    
    public int getXSelected() {
        return xSel;
    }
    
    public int getYSelected() {
        return ySel;
    }
    
    public boolean isInSelectedRectangle(int x, int y) {
        return GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel);
    }
    
    public void moveSelected(int x, int y) {
        x = Math.min(Math.max(minLimit, x), maxX - widthSel);
        y = Math.min(Math.max(minLimit, y), maxY - heightSel);
        
        int oldX = xSel;
        int oldY = ySel;
        xSel = x;
        ySel = y;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
                tgc.forceMove(xSel - oldX, ySel - oldY);
            }
        }
    }
    
    public TGComponent nextSelectedComponent() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
                return tgc;
            }
        }
        return null;
    }
    
    public Vector selectedTclasses() {
        TGComponent tgc;
        TCDTClass t;
        Vector v = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc.isSelected()) && (tgc instanceof TCDTClass)) {
                if (v == null) {
                    v = new Vector();
                }
                v.addElement(tgc);
            }
        }
        return v;
    }
    
    public Vector selectedTURTLEOSClasses() {
        TGComponent tgc;
        Vector v = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc.isSelected()) && (tgc instanceof TOSClass)) {
                if (v == null) {
                    v = new Vector();
                }
                v.addElement(tgc);
            }
        }
        return v;
    }
    
    public Vector selectedTMLTasks() {
        TGComponent tgc;
        TMLTaskOperator t;
        Vector v = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc.isSelected()) && (tgc instanceof TMLTaskOperator)) {
                if (v == null) {
                    v = new Vector();
                }
                v.addElement(tgc);
            }
        }
        return v;
    }
	
	public Vector selectedCPrimitiveComponent() {
        TGComponent tgc;
        TMLCPrimitiveComponent tcomp;
        Vector v = null;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
				if (tgc instanceof TMLCPrimitiveComponent) {
					if (v == null) {
						v = new Vector();
					}
					v.addElement(tgc);
				}
				
				if (tgc instanceof TMLCCompositeComponent) {
					if (v == null) {
						v = new Vector();
					}
					v.addAll(((TMLCCompositeComponent)(tgc)).getAllPrimitiveComponents());
				}
            }
        }
        return v;
	}
	
    // For Design panels (TURTLE, TURTLE-OS, etc.)
    public TClassSynchroInterface getTClass1ToWhichIamConnected(CompositionOperatorInterface coi) {
		
        TGConnector tgca = getTGConnectorAssociationOf(coi);
        TGComponent tgc;
        if (tgca != null) {
            tgc = getTopComponentToWhichBelongs(tgca.getTGConnectingPointP1());
            if ((tgc != null) && (tgc instanceof TClassInterface)) {
                return (TClassSynchroInterface) tgc;
            }
        }
        return null;
    }
    
    public TClassSynchroInterface getTClass2ToWhichIamConnected(CompositionOperatorInterface coi) {
		
        TGConnector tgca = getTGConnectorAssociationOf(coi);
        TGComponent tgc;
        if (tgca != null) {
            tgc = getTopComponentToWhichBelongs(tgca.getTGConnectingPointP2());
            if ((tgc != null) && (tgc instanceof TClassInterface)) {
                return (TClassSynchroInterface) tgc;
            }
        }
        return null;
    }
    
    public TGConnector getTGConnectorAssociationOf(CompositionOperatorInterface coi) {
        int i;
        TGConnectingPoint p1, p2;
        TGConnector tgco;
        TGConnectorAttribute tgca;
        TGComponent tgc;
        
        for(i=0; i<coi.getNbConnectingPoint(); i++) {
            p1 = coi.tgconnectingPointAtIndex(i);
            tgco = getConnectorConnectedTo(p1);
            if ((tgco != null) && (tgco instanceof TGConnectorAttribute)){
                tgca = (TGConnectorAttribute)tgco;
                if (p1 == tgca.getTGConnectingPointP1()) {
                    p2 = tgca.getTGConnectingPointP2();
                } else {
                    p2 = tgca.getTGConnectingPointP1();
                }
                
                // p2 now contains the connecting point of a association
                tgc = getComponentToWhichBelongs(p2);
                if ((tgc != null) && (!p2.isFree()) && (tgc instanceof TGConnectorAssociation)) {
                    return (TGConnectorAssociation)tgc;
                }
            }
        }
        return null;
    }
	
    
    // Popup menus
    
    private void buildComponentPopupMenu(TGComponent tgc, int x, int y) {
        // Component Menu
        componentMenu = new JPopupMenu();
        
        componentMenu.add(remove);
        componentMenu.add(edit);
        componentMenu.add(clone);
        componentMenu.add(bringFront);
        componentMenu.add(bringBack);
        componentMenu.add(makeSquare);
		componentMenu.addSeparator();
		componentMenu.add(attach);
		componentMenu.add(detach);
		componentMenu.addSeparator();
		componentMenu.add(hide);
		componentMenu.add(unhide);
        componentMenu.addSeparator();
        componentMenu.add(setJavaCode);
        componentMenu.add(removeJavaCode);
		componentMenu.add(setInternalComment);
		componentMenu.add(removeInternalComment);
		componentMenu.add(checkAccessibility);
		componentMenu.add(breakpoint);
        
        tgc.addActionToPopupMenu(componentMenu, menuAL, x, y);
    }
    
    protected void buildDiagramPopupMenu() {
        // Component Menu
        diagramMenu = new JPopupMenu();
        
        diagramMenu.add(paste);
        diagramMenu.addSeparator();
        diagramMenu.add(insertLibrary);
        diagramMenu.addSeparator();
        diagramMenu.add(upX);
        diagramMenu.add(upY);
        diagramMenu.add(downX);
        diagramMenu.add(downY);
        /*diagramMenu.addSeparator();
        diagramMenu.add(rename);
        diagramMenu.add(delete);*/
    }
    
    private void buildSelectedPopupMenu() {
        selectedMenu = new JPopupMenu();
        
        selectedMenu.add(cut);
        selectedMenu.add(copy);
        selectedMenu.addSeparator();
        selectedMenu.add(saveAsLibrary);
        selectedMenu.addSeparator();
        selectedMenu.add(captureSelected);
    }
    
    
    
    private void buildPopupMenus() {
        menuAL = new ActionListener() {
            public void actionPerformed(ActionEvent e){
                popupAction(e);
            }
        };
        
        remove = new JMenuItem("Remove");
        remove.addActionListener(menuAL);
        
        edit = new JMenuItem("Edit");
        edit.addActionListener(menuAL);
        
        clone = new JMenuItem("Clone");
        clone.addActionListener(menuAL);
        
        bringFront = new JMenuItem("Bring to front");
        bringFront.addActionListener(menuAL);
        
        bringBack = new JMenuItem("Send to back");
        bringBack.addActionListener(menuAL);
        
        makeSquare = new JMenuItem("Make Square");
        makeSquare.addActionListener(menuAL);
		
		attach = new JMenuItem("Attach to a component");
        attach.addActionListener(menuAL);
		
		detach = new JMenuItem("Detach from a component");
        detach.addActionListener(menuAL);
		
		hide = new JMenuItem("Hide internal components");
        hide.addActionListener(menuAL);
		
		unhide = new JMenuItem("Show internal components");
        unhide.addActionListener(menuAL);
        
        setJavaCode = new JMenuItem("Set Java code");
        setJavaCode.addActionListener(menuAL);
        
        removeJavaCode = new JMenuItem("Remove Java code");
        removeJavaCode.addActionListener(menuAL);
		
		setInternalComment = new JMenuItem("Set internal comment");
        setInternalComment.addActionListener(menuAL);
		
		removeInternalComment = new JMenuItem("Remove internal comment");
        removeInternalComment.addActionListener(menuAL);
		
		checkAccessibility = new JMenuItem("Check for accessibility / liveness with UPPAAL");
        checkAccessibility.addActionListener(menuAL);
		
		breakpoint = new JMenuItem("Add / remove breakpoint");
        breakpoint.addActionListener(menuAL);
        
        // Diagram Menu
        
        paste = new JMenuItem("Paste");
        paste.addActionListener(menuAL);
        
        insertLibrary = new JMenuItem("Insert Library");
        insertLibrary.addActionListener(menuAL);
        
        upX = new JMenuItem("Increase horizontal size");
        upX.addActionListener(menuAL);
        
        upY = new JMenuItem("Increase vertical size");
        upY.addActionListener(menuAL);
        
        downX = new JMenuItem("Decrease horizontal size");
        downX.addActionListener(menuAL);
        
        downY = new JMenuItem("Decrease vertical size");
        downY.addActionListener(menuAL);
        
        /*rename = new JMenuItem("Rename diagram");
        rename.addActionListener(menuAL);
		
        delete = new JMenuItem("Delete diagram");
        delete.addActionListener(menuAL);*/
        
        // Selected Menu
        cut = new JMenuItem("Cut");
        cut.addActionListener(menuAL);
        
        copy = new JMenuItem("Copy");
        copy.addActionListener(menuAL);
        
        saveAsLibrary = new JMenuItem("Save as library");
        saveAsLibrary.addActionListener(menuAL);
        
        captureSelected = new JMenuItem("Save as an Image");
        captureSelected.addActionListener(menuAL);
    }
    
    public void popupAction(ActionEvent e) {
        if (e.getSource() == remove) {
            removeComponent(componentPopup);
            mgui.changeMade(this, REMOVE_COMPONENT);
            componentPopup = null;
            repaint();
            return;
        }
        
        if (e.getSource() == clone) {
            cloneComponent(componentPopup.getTopFather());
            repaint();
            return;
        }
        
        if (e.getSource() == edit) {
            if (componentPopup.doubleClick(mgui.getFrame(), 0, 0)) {
                getGUI().changeMade(this, CHANGE_VALUE_COMPONENT);
                repaint();
            }
            return;
        }
        
        if (e.getSource() == bringFront) {
            bringToFront(componentPopup);
            mgui.changeMade(this, MOVE_COMPONENT);
            repaint();
            return;
        }
        
        if (e.getSource() == bringBack) {
            bringToBack(componentPopup);
            mgui.changeMade(this, MOVE_COMPONENT);
            repaint();
            return;
        }
        
        if (e.getSource() == makeSquare) {
            ((TGConnector)componentPopup).makeSquareWithoutMovingTGComponents();
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }
		
		if (e.getSource() == attach) {
            attach(componentPopup);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }
		
		if (e.getSource() == detach) {
            detach(componentPopup);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }
		
		if (e.getSource() == hide) {
            hide(componentPopup, true);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }
		
		if (e.getSource() == unhide) {
            hide(componentPopup, false);
            mgui.changeMade(this, MOVE_CONNECTOR);
            repaint();
            return;
        }
        
        if (e.getSource() == setJavaCode) {
            boolean pre, post;
            pre = (componentPopup instanceof PreJavaCode);
            post = (componentPopup instanceof PostJavaCode);
            JDialogCode jdc = new JDialogCode(mgui.getFrame(), "Setting java code", componentPopup.getPreJavaCode(), pre, componentPopup.getPostJavaCode(), post);
            GraphicLib.centerOnParent(jdc);
            jdc.show(); // blocked until dialog has been closed
            
            componentPopup.setPreJavaCode(jdc.getPreCode());
            componentPopup.setPostJavaCode(jdc.getPostCode());
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }
        
        if (e.getSource() == removeJavaCode) {  
            componentPopup.setPreJavaCode(null);
            componentPopup.setPostJavaCode(null);
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }
		
		if (e.getSource() == setInternalComment) {
			JDialogNote jdn = new JDialogNote(mgui.getFrame(), "Setting an internal comment", componentPopup.getInternalComment());
			GraphicLib.centerOnParent(jdn);
			jdn.show(); // blocked until dialog has been closed
			componentPopup.setInternalComment(jdn.getText());
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }
		
		if (e.getSource() == removeInternalComment) {
			componentPopup.setInternalComment(null);
            mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
            repaint();
            return;
        }
		
		if (e.getSource() == checkAccessibility) {
			if (componentPopup instanceof CheckableAccessibility) {
				componentPopup.setCheckableAccessibility(!componentPopup.getCheckableAccessibility());
			}
		}
		
		if (e.getSource() == breakpoint) {
			if (componentPopup instanceof AllowedBreakpoint) {
				componentPopup.setBreakpoint(!componentPopup.getBreakpoint());
			}
		}
        
        if (e.getSource() == upX) {
            maxX += increment;
            updateSize();
            return;
        }
        
        if (e.getSource() == upY) {
            maxY += increment;
            updateSize();
            return;
        }
        
        if (e.getSource() == downX) {
            maxX -= increment;
            updateSize();
            return;
        }
        
        if (e.getSource() == downY) {
            maxY -= increment;
            updateSize();
            return;
        }
        
        /*if (e.getSource() == rename) {
            mgui.renameTab(this);
            return;
        }
		
        if (e.getSource() == delete) {
            mgui.deleteTab(this);
            return;
        }*/
        
        if (e.getSource() == cut) {
            makeCut();
            return;
        }
        
        if (e.getSource() == copy) {
            makeCopy();
        }
        
        if (e.getSource() == saveAsLibrary) {
            saveAsLibrary();
        }
        
        if (e.getSource() == captureSelected) {
            captureSelected();
        }
        
        if (e.getSource() == paste) {
            makePaste(popupX, popupY);
            return;
        }
        
        if (e.getSource() == insertLibrary) {
            insertLibrary(popupX, popupY);
            return;
        }
        
        if (componentPopup != null) {
            if (componentPopup.eventOnPopup(e)); {
                mgui.changeMade(this, CHANGE_VALUE_COMPONENT);
                repaint();
                return;
            }
        }
    }
    
    public void openPopupMenu(int x, int y) {
        popupX = x;
        popupY = y;
        if (componentPointed != null) {
            componentPopup = componentPointed;
            buildComponentPopupMenu(componentPopup, x, y);
            setComponentPopupMenu();
            componentMenu.show(this, x, y);
            //System.out.println("closed");
        } else if ((mode == SELECTED_COMPONENTS) && (GraphicLib.isInRectangle(x, y, xSel, ySel, widthSel, heightSel))) {
            buildSelectedPopupMenu();
            setSelectedPopupMenu();
            selectedMenu.show(this, x, y);
        } else {
            buildDiagramPopupMenu();
            setDiagramPopupMenu();
            diagramMenu.show(this, x, y);
        }
    }
    
    public void setComponentPopupMenu() {
        if (!componentPointed.isRemovable()) {
            remove.setEnabled(false);
        } else {
            remove.setEnabled(true);
        }
        
        if (!componentPointed.isEditable()) {
            edit.setEnabled(false);
        } else {
            edit.setEnabled(true);
        }
        
        if (componentPointed.isCloneable()) {
			/*if (componentPointed.hasFather()) {
				clone.setEnabled(false);
			} else {*/
				clone.setEnabled(true);
				//}
        } else {
            clone.setEnabled(false);
        }
		
		if (componentPointed instanceof SwallowedTGComponent) {
			if (componentPointed.getFather() == null) {
				attach.setEnabled(true);
				detach.setEnabled(false);
			} else {
				attach.setEnabled(false);
				detach.setEnabled(true);
			}
		} else {
			attach.setEnabled(false);
			detach.setEnabled(false);
		}
		
		if (componentPointed instanceof HiddenInternalComponents) {
			boolean hidden = ((HiddenInternalComponents)componentPointed).areInternalsHidden();
			hide.setEnabled(!hidden);
			unhide.setEnabled(hidden);
		} else {
			hide.setEnabled(false);
			unhide.setEnabled(false);
		}
        
        if (componentPointed instanceof TGConnector) {
			/*if (componentPointed.hasFather()) {
				clone.setEnabled(false);
			} else {*/
				makeSquare.setEnabled(true);
				//}
        } else {
            makeSquare.setEnabled(false);
        }
        
        if ((componentPointed instanceof PreJavaCode) || (componentPointed instanceof PostJavaCode)){
            setJavaCode.setEnabled(true);
            removeJavaCode.setEnabled(true);
        } else {
            setJavaCode.setEnabled(false);
            removeJavaCode.setEnabled(false);
        }
		
		if (componentPointed instanceof EmbeddedComment) {
			setInternalComment.setEnabled(true);
            removeInternalComment.setEnabled(true);
		} else {
			setInternalComment.setEnabled(false);
            removeInternalComment.setEnabled(false);
		}
		
		if (componentPointed instanceof CheckableAccessibility){
            checkAccessibility.setEnabled(true);
        } else {
            checkAccessibility.setEnabled(false);
        }
		
		if (componentPointed instanceof AllowedBreakpoint){
            breakpoint.setEnabled(true);
        } else {
            breakpoint.setEnabled(false);
        }
        
        
    }
    
    public void setDiagramPopupMenu() {
        paste.setEnabled(copyData != null);
        insertLibrary.setEnabled(true);
        if (maxX < minimumXSize + increment) {
            downX.setEnabled(false);
        } else {
            downX.setEnabled(true);
        }
        
        if (maxY < minimumYSize + increment) {
            downY.setEnabled(false);
        } else {
            downY.setEnabled(true);
        }
        
        /* rename / delete diagram -> Sequence diagram */
        /*if (this instanceof SequenceDiagramPanel) {
            rename.setEnabled(true);
            delete.setEnabled(true);
        } else {
            rename.setEnabled(false);
            delete.setEnabled(false);
        }*/
        
    }
    
    public void setSelectedPopupMenu() {
        cut.setEnabled(true);
        copy.setEnabled(true);
    }
    
    public void makeCut() {
        copyData = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        removeAllSelectedComponents();
        mgui.changeMade(this, REMOVE_COMPONENT);
        mode = NORMAL;
        mgui.setMode(MainGUI.PASTE_OK);
        repaint();
    }
    
    public void makeCopy() {
        copyData = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        mgui.setMode(MainGUI.PASTE_OK);
        return;
    }
    
    public void saveAsLibrary() {
        String data = mgui.gtm.makeXMLFromSelectedComponentOfADiagram(this, getMaxIdSelected(), xSel, ySel);
        mgui.saveAsLibrary(data);
        return;
    }
    
    public void captureSelected() {
        mgui.selectedCapture();
    }
    
    public void makeDelete() {
        removeAllSelectedComponents();
        mode = NORMAL;
        mgui.setMode(MainGUI.CUTCOPY_KO);
        mgui.setMode(MainGUI.EXPORT_LIB_KO);
        mgui.changeMade(this, REMOVE_COMPONENT);
        repaint();
    }
    
    public void makePaste(int X, int Y) {
        if (copyData != null) {
            try {
                mgui.gtm.copyModelingFromXML(this, copyData, X, Y);
            } catch (MalformedModelingException mme) {
                System.out.println("Paste Exception: " + mme.getMessage());
                JOptionPane.showMessageDialog(mgui.getFrame(), "Exception", "Paste failed", JOptionPane.INFORMATION_MESSAGE);
            }
            mgui.changeMade(this, NEW_COMPONENT);
            repaint();
        }
    }
    
    public void insertLibrary(int X, int Y) {
        String data = mgui.loadLibrary();
        //System.out.println(data);
        if (data != null) {
            try {
                mgui.gtm.copyModelingFromXML(this, data, X, Y);
            } catch (MalformedModelingException mme) {
                System.out.println("Insert Library Exception: " + mme.getMessage());
                JOptionPane.showMessageDialog(mgui.getFrame(), "Exception", "insertion of library has failed", JOptionPane.INFORMATION_MESSAGE);
            }
            mgui.changeMade(this, NEW_COMPONENT);
            repaint();
            // Added by Solange. It fills the lists of components, interfaces, etc
			if (tp instanceof ProactiveDesignPanel)
				mgui.gtm.generateLists((ProactiveDesignPanel)tp);
            //
        }
    }
    
    public void bringToBack(TGComponent tgc) {
		if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
			tgc.getFather().bringToBack(tgc);
		} else {
			tgc = tgc.getTopFather();
			//System.out.println("Bring front: " + tgc.getName());
			int index = componentList.indexOf(tgc);
			if (index > -1) {
				//System.out.println("Ok bring");
				componentList.remove(index);
				componentList.add(tgc);
			}
		}
    }
    
    public void bringToFront(TGComponent tgc) {
		if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
			tgc.getFather().bringToFront(tgc);
		} else {
			tgc = tgc.getTopFather();
			int index = componentList.indexOf(tgc);
			if (index > -1) {
				componentList.remove(index);
				componentList.add(0, tgc);
			}
		}
    }
    
    public void removeAllSelectedComponents() {
        TGComponent tgc = nextSelectedComponent();
        while(tgc != null) {
            removeComponent(tgc);
            tgc = nextSelectedComponent();
        }
    }
    
    // operations
    public void removeComponent(TGComponent tgc) {
        TGComponent t;
        Iterator iterator = componentList.listIterator();
        
		fatherOfRemoved = tgc.getFather();
		
        while(iterator.hasNext()) {
            t = (TGComponent)(iterator.next());
            if (t == tgc) {
                removeConnectors(tgc);
                if (tgc instanceof TGConnector) {
                    TGConnector tgcon = (TGConnector) tgc;
                    tgcon.getTGConnectingPointP1().setFree(true);
                    tgcon.getTGConnectingPointP2().setFree(true);
                }
                componentList.remove(tgc);
                actionOnRemove(tgc);
                tgc.actionOnRemove();
                return;
            } else {
				//System.out.println("Testing remove internal component");
                if (t.removeInternalComponent(tgc)) {
					//System.out.println("Remove internal component");
                    removeConnectors(tgc);
                    return;
                }
            }
        }
    }
    
    public void removeConnectors(TGComponent tgc) {
        TGConnector tgcon;
        TGComponent t;
        TGConnectingPoint cp;
        int i, j, k;
        
        for (i = 0; i<tgc.getNbConnectingPoint(); i++) {
            cp = tgc.tgconnectingPointAtIndex(i);
            for(j=0; j<componentList.size(); j++) {
                t = (TGComponent)(componentList.get(j));
                if (t instanceof TGConnector) {
                    tgcon = (TGConnector)t;
                    if ((cp == tgcon.getTGConnectingPointP1()) || (cp == tgcon.getTGConnectingPointP2())) {
                        componentList.remove(j);
                        actionOnRemove(t);
                        j --;
                        tgcon.getTGConnectingPointP1().setFree(true);
                        tgcon.getTGConnectingPointP2().setFree(true);
                        for(k=0; k<tgcon.getNbConnectingPoint(); k++) {
                            removeOneConnector(tgcon.tgconnectingPointAtIndex(k));
                        }
                    }
                }
            }
        }
        
        for(i=0; i<tgc.getNbInternalTGComponent(); i++) {
            removeConnectors(tgc.getInternalTGComponent(i));
        }
    }
    
    public void removeOneConnector(TGConnectingPoint cp) {
		//System.out.println("Remove one connector");
        TGConnector tgcon;
        TGComponent t;
        int j, k;
        
        for(j=0; j<componentList.size(); j++) {
            t = (TGComponent)(componentList.get(j));
            if (t instanceof TGConnector) {
                tgcon = (TGConnector)t;
                if ((cp == tgcon.getTGConnectingPointP1()) || (cp == tgcon.getTGConnectingPointP2())) {
                    componentList.remove(j);
                    actionOnRemove(t);
                    j --;
                    tgcon.getTGConnectingPointP1().setFree(true);
                    tgcon.getTGConnectingPointP2().setFree(true);
                    for(k=0; k<tgcon.getNbConnectingPoint(); k++) {
                        removeOneConnector(tgcon.tgconnectingPointAtIndex(k));
                    }
                }
            }
        }
    }
    
    public void cloneComponent(TGComponent _tgc) {
        // copy
        String clone = mgui.gtm.makeXMLFromComponentOfADiagram(this, _tgc, getMaxIdSelected(), _tgc.getX(), _tgc.getY());
        
        //System.out.println("clone=\n"+ clone);
        
        // paste
        
        try {
            mgui.gtm.copyModelingFromXML(this, clone, _tgc.getX() + 50, _tgc.getY() + 25);
        } catch (MalformedModelingException mme) {
            System.out.println("Clone Exception: " + mme.getMessage());
            JOptionPane.showMessageDialog(mgui.getFrame(), "Exception", "Clone creation failed", JOptionPane.INFORMATION_MESSAGE);
        }
        bringToBack(_tgc);
        mgui.changeMade(this, NEW_COMPONENT);
    }
    
    public MainGUI getGUI() {
        return mgui;
    }
    
    public int getMaxX() {
        return maxX;
    }
    
    public int getMinX() {
        return minLimit;
    }
    
    public int getMinY() {
        return minLimit;
    }
    
    public int getMaxY() {
        return maxY;
    }
    
    public void setMaxX(int x) {
        maxX = x;
        
    }
    
    public void setMinX(int x) {
        minLimit = x;
    }
    
    public void setMinY(int y) {
        minLimit = y;
    }
    
    public void setMaxY(int y) {
        maxY = y;
    }
    
    public void updateSize() {
        setPreferredSize(new Dimension(maxX + limit, maxY + limit));
        revalidate();
    }
	
	public void attach(TGComponent tgc) {
		if (tgc instanceof SwallowedTGComponent) {
			if (tgc.tdp.addComponent(tgc, tgc.getX(), tgc.getY(), true, false)) {
				// Component was attached -> must be removed from the list
				componentList.remove(tgc);
			}
		}
	}
	
	public void detach(TGComponent tgc) {
		if ((tgc instanceof SwallowedTGComponent) && (tgc.getFather() != null)) {
			((SwallowTGComponent)tgc.getFather()).removeSwallowedTGComponent(tgc);
			componentList.add(tgc);
			tgc.wasUnswallowed();
			bringToFront(tgc);
		}
	}
	
	public void hide(TGComponent tgc, boolean hide) {
		if (tgc instanceof HiddenInternalComponents) {
			((HiddenInternalComponents)tgc).setInternalsHidden(hide);
		}
	}
    
    public String sizeParam() {
        String s = " minX=\"" + getMinX() + "\"";
        s += " maxX=\"" + getMaxX() + "\"";
        s += " minY=\"" + getMinY() + "\"";
        s += " maxY=\"" + getMaxY() + "\"";
        return s;
    }
	
	public String zoomParam() {
        String s = " zoom=\"" + getZoom() + "\"";
        return s;
    }
    
    //returns the highest id amongst its components
    public int getMaxId() {
        TGComponent tgc;
        int ret = 0;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            ret = Math.max(ret, tgc.getMaxId());
        }
        return ret;
    }
    
    public int getMaxIdSelected() {
        TGComponent tgc;
        int ret = 0;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.isSelected()) {
                ret = Math.max(ret, tgc.getMaxId());
            }
        }
        return ret;
    }
    
    public void setDraw(boolean b) {
        draw = b;
    }
    
    public boolean getDraw() {
        return draw;
    }
    
    public TToolBar getToolBar() {
        return ttb;
    }
    
    // tell the other component connected to this connecting point
    public TGConnector getConnectorConnectedTo(TGConnectingPoint p) {
        TGComponent tgc;
        TGConnector tgco;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnector) {
                tgco = (TGConnector)tgc;
                if ((tgco.getTGConnectingPointP1() == p) || (tgco.getTGConnectingPointP2() == p)) {
                    return tgco;
                }
            }
        }
        return null;
    }
    
    public TGComponent getNextTGComponent(TGComponent tgc, int index) {
        TGConnectingPoint pt2;
        TGConnector tgcon = getNextTGConnector(tgc, index);
        
        if (tgcon == null) {
            //System.out.println("TGCon is null");
            return null; 
        }
        
        pt2 = tgcon.getTGConnectingPointP2();
        
        if (pt2 == null) {
            return null; 
        }
        
        return getTopComponentToWhichBelongs(pt2);
    }
    
    public TGConnector getNextTGConnector(TGComponent tgc, int index) {
        TGConnectingPoint pt1, pt2;
        
        pt1 = tgc.getNextTGConnectingPoint(index);
        
        if (pt1 == null) {
            //System.out.println("pt1 is null");
            return null;
        }
        
        return getConnectorConnectedTo(pt1);
    }
    
    public TGComponent getTopComponentToWhichBelongs(TGConnectingPoint p) {
        TGComponent tgc = getComponentToWhichBelongs(p);
        if (tgc != null) {
            return tgc.topTGComponent();
        }
        return null;
    }
    
    public TGComponent getComponentToWhichBelongs(TGConnectingPoint p) {
        TGComponent tgc1, tgc2;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponent)(iterator.next());
            tgc2 = tgc1.belongsToMeOrSon(p);
            if (tgc2 != null) {
                return tgc2;
            }
        }
        return null;
    }
	
	public TGComponent getComponentToWhichBelongs(LinkedList components, TGConnectingPoint p) {
        TGComponent tgc1, tgc2;
        Iterator iterator = components.listIterator();
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponent)(iterator.next());
            tgc2 = tgc1.belongsToMeOrSon(p);
            if (tgc2 != null) {
                return tgc2;
            }
        }
        return null;
    }
	
	public void getAllCheckableTGComponent(ArrayList<TGComponent> _list) {
		Iterator iterator = componentList.listIterator();
		TGComponent tgc;
        
        while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc.getCheckableAccessibility()) {
				_list.add(tgc);
				//System.out.println("Adding tgc component=" + tgc);
			}
		}
	}
    
    // Main Tree
    
    public int getChildCount() {
        return componentList.size();
    }
    
    public Object getChild(int index) {
        return componentList.get(index);
    }
    
    public int getIndexOfChild(Object child) {
        return componentList.indexOf(child);
    }
    
    //Tclass
    
    public boolean isAlreadyATClassName(String name) {
        TClassInterface t;
        Object o;
        int i;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TClassInterface) {
                t = (TClassInterface)o;
                if (t.getClassName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isAlreadyATMLTaskName(String name) {
        TMLTaskInterface t;
        Object o;
        int i;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TMLTaskInterface) {
                t = (TMLTaskInterface)o;
                if (t.getTaskName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	public boolean isAlreadyATMLPrimitiveComponentName(String name) {
        TMLCPrimitiveComponent pc;
        Object o;
        int i;
        Iterator iterator = componentList.listIterator();
		ArrayList<TMLCPrimitiveComponent> list;
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TMLCPrimitiveComponent) {
                pc = (TMLCPrimitiveComponent)o;
                if (pc.getValue().equals(name)) {
                    return true;
                }
            }
			if (o instanceof TMLCCompositeComponent) {
				list = ((TMLCCompositeComponent)o).getAllPrimitiveComponents();
				for(TMLCPrimitiveComponent cpc: list) {
					if (cpc.getValue().equals(name)) {
						return true;
					}
				}
			}
        }
        return false;
    }
    
    public boolean isAlreadyATOSClassName(String name) {
        TOSClass t;
        Object o;
        int i;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TOSClass) {
                t = (TOSClass)o;
                if (t.getClassName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String findTClassName(String name) {
        boolean ok;
        int i;
        int index = 0;
        TClassInterface t;
        Object o;
        TCDTData td;
        Iterator iterator;
        
        while(index >= 0) {
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                o = (TGComponent)(iterator.next());
                if (o instanceof TClassInterface) {
                    t = (TClassInterface)o;
                    if (t.getClassName().equals(name + index)) {
                        ok = false;
                    }
                }
                if (o instanceof TCDTData) {
                    td = (TCDTData)o;
                    if (td.getValue().equals(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
    
    public String findTOSClassName(String name) {
        boolean ok;
        int i;
        int index = 0;
        TOSClass t;
        Object o;
        Iterator iterator;
        
        while(index >= 0) {
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                o = (TGComponent)(iterator.next());
                if (o instanceof TOSClass) {
                    t = (TOSClass)o;
                    if (t.getClassName().equals(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
    
	public String findRequirementName(String name) {
        boolean ok;
        int i;
        int index = 0;
        Requirement req;
        Object o;
        Iterator iterator;
        
        while(index >= 0) {
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                o = (TGComponent)(iterator.next());
                if (o instanceof Requirement) {
                    req = (Requirement)o;
                    if (req.getRequirementName().equals(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
	
	public String findTMLPrimitiveComponentName(String name) {
		boolean ok;
        int i;
        int index = 0;
        TGComponent o;
        Iterator iterator;
        
        while(index >= 0) {
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                o = (TGComponent)(iterator.next());
				if (findTMLPrimitiveComponentNameTgc(name, o, index)) {
					ok = false;
					break;
				}
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
	
	public boolean findTMLPrimitiveComponentNameTgc(String name, TGComponent tgc, int index) {
		if (tgc instanceof TMLCPrimitiveComponent) {
			if (tgc.getValue().equals(name+index)) {
				return true;
			}
		}
		for(int i=0; i<tgc.getNbInternalTGComponent(); i++) {
			if (findTMLPrimitiveComponentNameTgc(name, tgc.getInternalTGComponent(i), index)) {
				return true;
			}
		}
		
		return false;
	}
    
    public String findTMLTaskName(String name) {
        boolean ok;
        int i;
        int index = 0;
        TMLTaskInterface t;
        Object o;
        Iterator iterator;
        
        while(index >= 0) {
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                o = (TGComponent)(iterator.next());
                if (o instanceof TMLTaskInterface) {
                    t = (TMLTaskInterface)o;
                    if (t.getTaskName().equals(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
    
    public String findTObjectName(String name) {
        boolean ok;
        int i;
        int index = 0;
        Iterator iterator;
        
        while(index >= 0) {
            TGComponent tgc;
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = (TGComponent)(iterator.next());
                if (tgc instanceof TCDTObject) {
                    if (((TCDTObject)tgc).getObjectName().equals(name + index)) {
                        ok = false;
                    }
                }
                if (tgc instanceof TCDTClass) {
                    if (((TCDTClass)tgc).getClassName().startsWith(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
    
    public String findTObjectName(String name1, String name2) {
        boolean ok;
        int i;
        int index = 0;
        Iterator iterator;
        
        while(index >= 0) {
            TGComponent tgc;
            ok = true;
            iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = (TGComponent)(iterator.next());
                if (tgc instanceof TCDTObject) {
                    if (((TCDTObject)tgc).getObjectName().equals(name + index)) {
                        ok = false;
                    }
                }
            }
            if (ok) {
                if (isTObjectNameUnique(name1+index+name2)) {
                    return name1 + index;
                }
            }
            index ++;
        }
        return name;
    }
	
	public String findNodeName(String name) {
        boolean ok;
        int i;
        int index = 0;
        TGComponent tgc;
		Iterator iterator;
        
        while(index >= 0) {
            //ok = true;
			ok = isNCNameUnique(name + index);
            /*iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = (TGComponent)(iterator.next());
				if (tgc.getName().equals(name + index)) {
					ok = false;
                }                
            }*/
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
	
	public String findInterfaceName(String name) {
        boolean ok;
        int i;
        int index = 0;
        TGComponent tgc;
		Iterator iterator;
        
        while(index >= 0) {
            ok = isNCNameUnique(name + index);
            /*iterator = componentList.listIterator();
            while(iterator.hasNext()) {
                tgc = (TGComponent)(iterator.next());
				if (tgc instanceof NCConnectorNode) {
					if (((NCConnectorNode)tgc).getInterfaceName().equals(name + index)) {
						ok = false;
					}                
				} else {
					if (tgc.getName().equals(name + index)) {
						ok = false;
					}   
				}
            }*/
            if (ok) {
                return name + index;
            }
            index ++;
        }
        return name;
    }
    
    public boolean isTClassNameUnique(String s) {
        Object o;
        TClassInterface t;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TClassInterface) {
                t = (TClassInterface)o;
                if (t.getClassName().equals(s)) {
                    return false;
                }
            }
            if (o instanceof TCDTData) {
                if (((TCDTData)o).getValue().equals(s)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isTOSClassNameUnique(String s) {
        Object o;
        TOSClass t;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TOSClass) {
                t = (TOSClass)o;
                if (t.getClassName().equals(s)) {
                    return false;
                }
            }
        }
        return true;
    }
	
    public boolean isTMLTaskNameUnique(String s) {
        Object o;
        TMLTaskInterface t;
        Iterator iterator = componentList.listIterator();
        
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TMLTaskInterface) {
                t = (TMLTaskInterface)o;
                if (t.getTaskName().equals(s)) {               
                    return false;
                }
            }
        }   
        return true;
    }
	
	public boolean isNCNameUnique(String s) {
        Object o;
		TGComponent tgc;
		Vector v;
		NCTrafficArtifact arti;
		NCRouteArtifact artiroute;
		int i;
		NCConnectorNode link;
        
        Iterator iterator = componentList.listIterator();
        
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if ((tgc instanceof NCEqNode) || (tgc instanceof NCSwitchNode)){
                if (tgc.getName().equals(s)) {               
                    return false;
                }
				
				if (tgc instanceof NCEqNode) {
					v = ((NCEqNode)tgc).getArtifactList();
					for (i=0; i<v.size(); i++) {
						arti = (NCTrafficArtifact)(v.get(i));
						if (arti.getValue().equals(s)) {
							return false;
						}
					}
				}
				
				if (tgc instanceof NCSwitchNode) {
					v = ((NCSwitchNode)tgc).getArtifactList();
					for (i=0; i<v.size(); i++) {
						artiroute = (NCRouteArtifact)(v.get(i));
						if (artiroute.getValue().equals(s)) {
							return false;
						}
					}
				}
            }
			
			if (tgc instanceof NCConnectorNode) {
				link = (NCConnectorNode)tgc;
				if (link.getInterfaceName().equals(s)) {
					return false;
				}
			}
        }   
        return true;
    }
    
    public boolean isRequirementNameUnique(String s) {
        Object o;
        Requirement req;
        Iterator iterator = componentList.listIterator();
		
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof Requirement) {
                req = (Requirement)o;
                //System.out.println("analysing s = " + s + " vs " + req.getRequirementName());
                if (req.getRequirementName().compareTo(s) == 0) {               
                    return false;
                }
            }
        }   
        return true;
    }
    
    public boolean isTObjectNameUnique(String s) {
        Object o;
        TClassInterface t;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = (TGComponent)(iterator.next());
            if (o instanceof TClassInterface) {
                t = (TClassInterface)o;
                if (t.getClassName().equals(s)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // For compatibility with ttool v0.41
    // Assumes no internal duplicate id
    public void checkForDuplicateId() {
        TGComponent tgc1, tgc2;
        int id;
        int i, j;
        
        for(i=0; i<componentList.size(); i++) {
            tgc1 = (TGComponent)(componentList.get(i));
            for(j=0; j<componentList.size(); j++) {
                if (j != i) {
                    tgc2 = (TGComponent)(componentList.get(j));
                    tgc2 = tgc2.getIfId(tgc1.getId());
                    if (tgc2 != null) {
                        System.out.println("*** Same ID ***");
                        System.out.println("tgc1" + tgc1.getClass());
                        System.out.println("tgc2" + tgc2.getClass());
                    }
                }
            }
        }
    }
    
    /*public void findTGComponentWithId(int id, int index) {
        TGComponent tgc;
        
        for(int i=index; i<componentList.size(); i++) {
            
        }
    }*/
    
    public Vector getTClasses() {
        Vector v = new Vector();
        Object o;
        Iterator iterator = componentList.iterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TCDTClass) {
                v.add(o);
            }
        }
        
        return v;
    }
    
    public void removeSynchronizedGates(Vector v, TClassInterface t, TCDSynchroGateList tcdsgl ) {
        TGComponent tgc;
        TCDCompositionOperatorWithSynchro tgso;
        Vector ttwoattrib;
        int j = 0;
        TTwoAttributes tt;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            
            if (tgc instanceof TCDCompositionOperatorWithSynchro) {
                tgso = (TCDCompositionOperatorWithSynchro)tgc;
                
                if ((tgso.getT1() == t) ||(tgso.getT2() == t)) {
                    if (tgso.getSynchroGateList() != tcdsgl) {
                        ttwoattrib = tgso.getSynchroGateList().getGates();
                        for(j=0; j<ttwoattrib.size(); j++) {
                            tt = (TTwoAttributes)(ttwoattrib.elementAt(j));
                            if (tt.t1 == t) {
                                v.removeElement(tt.ta1);
                            } else {
                                v.removeElement(tt.ta2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean isASynchronizedGate(TAttribute ta) {
        TGComponent tgc;
        TCDCompositionOperatorWithSynchro tgso;
        Vector ttwoattrib;
        TTwoAttributes tt;
        Iterator iterator = componentList.listIterator();
        int j;
        
        //System.out.println("Checking " + ta);
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            
            if (tgc instanceof TCDCompositionOperatorWithSynchro) {
                tgso = (TCDCompositionOperatorWithSynchro)tgc;
                ttwoattrib = tgso.getSynchroGateList().getGates();
                for(j=0; j<ttwoattrib.size(); j++) {
                    tt = (TTwoAttributes)(ttwoattrib.elementAt(j));
                    //System.out.println("tt= " + tt);
                    if ((tt.ta1 == ta) || (tt.ta2 == ta)) {
                        //System.out.println("true");
                        return true;
                    }
                    //System.out.println("false!");
                }
            }
        }
        return false;
    }
    
    public boolean hasAlreadyAnInstance(TCDTObject to) {
        Object o;
        TClassInterface t;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if ((o instanceof TClassInterface)  && (!o.equals(to))){
                t = (TClassInterface)o;
                if (t.getClassName().compareTo(to.getClassName()) == 0) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    // updates attributes and gates
    public void updateInstances(TCDTClass tc) {
        Object o;
        TCDTObject to;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TCDTObject){
                to = (TCDTObject)o;
                if (to.getMasterTClass() == tc) {
                    to.updateAttributes(tc.getAttributes());
                    to.updateGates(tc.getGates());
                }
            }
        }
    }
    
    public void resetAllInstancesOf(TCDTClass tc) {
        Object o;
        TCDTObject to;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TCDTObject){
                to = (TCDTObject)o;
                if (to.getMasterTClass() == tc) {
                    to.reset();
                }
            }
        }
    }
    
    public TCDTClass findTClassByName(String name) {
        TCDTClass tc;
        Object o;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            o = iterator.next();
            if (o instanceof TCDTClass){
                tc = (TCDTClass)o;
                if (tc.getClassName().compareTo(name) == 0) {
                    return tc;
                }
            }
        }
        return null;
    }
    
    public MainGUI getMGUI() {
        return mgui;
    }
    
    public BufferedImage performMinimalCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        boolean b = draw;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        draw = true;
        //paintMycomponents(g);
        overcomeShowing = true;
        paintMycomponents(g, false, 1, 1);
        //this.paint(g);
        overcomeShowing = false;
        //g.dispose();
        int x = getRealMinX();
        int y = getRealMinY();
        w = getRealMaxX() - x;
        h = getRealMaxY() - y;
        //System.out.println("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        x = x - 5;
        y = y -5;
        w = w + 10;
        h = h + 10;
        w = Math.max(0, w);
        h = Math.max(0, h);
        x = Math.max(5, x);
        y = Math.max(5, y);
        w = Math.min(w, getWidth() - x);
        h = Math.min(h, getHeight() - y);
        //System.out.println("x=" + x + " y=" + y + " w=" + w + " h=" + h + " getWidth = " + this.getWidth() + " getHeight = " + this.getHeight());
        image = image.getSubimage(x, y, w, h);
        g.dispose();
        draw = b;
        return image;
    }
    
    public BufferedImage performSelectedCapture() {
        int w = this.getWidth();
        int h = this.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        selectedTemp = false;
        Color colorTmp = ColorManager.SELECTED_0;
        ColorManager.SELECTED_0 = ColorManager.NORMAL_0;
        this.paint(g);
        selectedTemp = true;
        ColorManager.SELECTED_0 = colorTmp;
        g.dispose();
        image = image.getSubimage(xSel, ySel, widthSel, heightSel);
        return image;
    }
    
    public int getRealMinX() {
        int res = maxX;
        int cur;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            cur = tgc.getCurrentMinX();
            //System.out.println("cur=" + cur + " res=" + res + " tgc=" + tgc.getName());
            if (cur < res)
                res = cur;
        }
        
        res = Math.max(0, res);
        
        if (res == maxX) {
            return 0;
        }
        
        return res;
    }
    
    public int getRealMinY() {
        int res = maxY;
        int cur;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            cur = tgc.getCurrentMinY();
            if (cur < res)
                res = cur;
        }
        
        res = Math.max(0, res);
        
        if (res == maxY) {
            return 0;
        }
        
        return res;
    }
    
    public int getRealMaxX() {
        int res = limit;
        int cur;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            cur = tgc.getCurrentMaxX();
            if (cur > res)
                res = cur;
        }
        return res;
    }
    
    public int getRealMaxY() {
        int res = limit;
        int cur;
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            cur = tgc.getCurrentMaxY();
            if (cur > res)
                res = cur;
        }
        return res;
    }
    
    public boolean isSelectedTemp() {
        return selectedTemp;
    }
    
    public TGComponent getSecondTGComponent(TGConnector tgco) {
        TGConnectingPoint p = tgco.getTGConnectingPointP2();
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc.belongsToMe(p)) {
                return tgc;
            }
        }
        return null;
    }
    
    public boolean isFree(ArtifactTClassGate atg) {
        return false;
    }
    
    // Toggle management
    public boolean areAttributesVisible() {
        return true;
    }
    
    public boolean areGatesVisible() {
        return true;
    }
    
    public boolean areSynchroVisible() {
        return true;
    }
    
    public void checkAllMySize() {
        TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            tgc.checkAllMySize();
        }
    }
    
    public void enhance() {
        
    }
	
	public void autoAdjust() {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TGAutoAdjust) {
				((TGAutoAdjust)tgc).autoAdjust(adjustMode);
			}
        }
		adjustMode = (adjustMode + 1)% 2;
		
		repaint();
	}
	
	public boolean hasAutoConnect() {
		return false;
	}
	
	public void autoConnect(TGComponent added) {
		
		
		boolean cond = hasAutoConnect();
		
		if (!cond) {
			return;
		}
		
		int i, j;
		
		//System.out.println("Autoconnect");
		
		Vector listPoint = new Vector();
		
		Vector v = new Vector();
		
		int distance = 100;
		TGConnectingPoint found = null;
		int distanceTmp;
		
		boolean cd1, cd2;
		
		TGConnectingPoint tgcp, tgcp1;
		
		TGConnector tgco;
		
		TGComponent tgc;
        Iterator iterator;
		
        for(i=0; i<added.getNbConnectingPoint(); i++) {
			
			tgcp = added.getTGConnectingPointAtIndex(i);
			if (tgcp.isFree() && tgcp.isCompatibleWith(added.getDefaultConnector())) {
				
				// Try to connect that connecting point
				found = null;
				distance = 100;
				
				iterator = componentList.listIterator();
				while(iterator.hasNext()) {
					tgc = (TGComponent)(iterator.next());
					if (tgc != added) {
						for(j=0; j<tgc.getNbConnectingPoint(); j++) {
							tgcp1 = tgc.getTGConnectingPointAtIndex(j);
							if ((tgcp1 != null) && tgcp1.isFree()) {
								if (tgcp1.isCompatibleWith(added.getDefaultConnector())) {
									cd1 = tgcp1.isIn() && tgcp.isOut() && (tgcp1.getY() > tgcp.getY());
									cd2 = tgcp.isIn() && tgcp1.isOut() && (tgcp1.getY() < tgcp.getY());
									if (cd1 || cd2) {
										distanceTmp = (int)(Math.sqrt(   Math.pow(tgcp1.getX() - tgcp.getX(), 2) + Math.pow(tgcp1.getY() - tgcp.getY(), 2)));
										if (distanceTmp < distance) {
											distance = distanceTmp;
											found = tgcp1;
										}
									}
								}
							}
						}
						
					}
				}
				if (found != null) {
					//System.out.println("Adding connector");
					if (found.isIn()) {
						tgco = TGComponentManager.addConnector(tgcp.getX(), tgcp.getY(), added.getDefaultConnector(), this, tgcp, found, listPoint);
					} else {
						tgco = TGComponentManager.addConnector(found.getX(), found.getY(), added.getDefaultConnector(), this, found, tgcp, listPoint);
					}
					componentList.add(tgco);
					//System.out.println("Connector added");
				}
			}
		}
		//System.out.println("End Autoconnect");
	}
	
	public void resetAllDIPLOIDs() {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			tgc.setDIPLOID(-1);
		}
	}
    
    
}

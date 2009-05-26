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
 * Class TDiagramMouseManager
 * Mouse management for all diagrams
 * Creation: 21/12/2003
 * @version 1.1 06/07/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;
import java.awt.event.*;

public class TDiagramMouseManager implements MouseListener, MouseMotionListener  {

	private TDiagramPanel tdp;

	private TGComponent tgc;

	private int oldx;
	private int oldy;
	private int oldwidth;
	private int oldheight;

	private int cdx;
	private int cdy;
	private int cdwidth;
	private int cdheight;

	private int decx;
	private int decy;

	private int resizeInfo;

	private boolean selectedComponent = false;

	//private Point p;
	private TGConnectingPoint cp;

	//private TGConnector tgcon;
	private TGConnector tgco;
	private CDElement [] cde;
	private boolean isOut;


	// Constructor
	public TDiagramMouseManager(TDiagramPanel _tdp) {
		tdp = _tdp;
	}


	// Mouse operations

	public void mousePressed(MouseEvent e) {
		int selected = tdp.getGUI().getTypeButtonSelected();

		//System.out.println("Titi");
		if (e.getButton() == MouseEvent.BUTTON3) {
			//System.out.println("toto");
			if (selected == TGComponentManager.EDIT) {
				tdp.openPopupMenu(e.getX(), e.getY());
			}
		}

		//System.out.println("mode = " + tdp.mode + " selected=" + selected);

		if ((tdp.mode == TDiagramPanel.SELECTED_COMPONENTS) && (tdp.isInSelectedRectangle(e.getX(), e.getY()))){
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			tdp.mode = TDiagramPanel.MOVING_SELECTED_COMPONENTS;
			tdp.setMovingSelectedComponents();
			cdx = tdp.getXSelected();
			cdy = tdp.getYSelected();
			oldx = cdx;
			oldy = cdy;
			decx = e.getX() - cdx;
			decy = e.getY() - cdy;
		}


		if ((tdp.mode == TDiagramPanel.NORMAL) && (selected == TGComponentManager.EDIT)) {
			//search if an element is pointed
			boolean actionMade = false;
			tgc = tdp.componentPointed();
			//System.out.println("Working on TGC=" + tgc);
			if (tgc == null) {
				// making a selection of components
				tdp.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				tdp.mode = TDiagramPanel.SELECTING_COMPONENTS;
				tdp.setSelectingComponents(e.getX(), e.getY());
			}  else {
				// Resize, move, or make a connector
				if (tgc.isUserResizable() && ((resizeInfo=tgc.getResizeZone(e.getX(), e.getY())) != 0)) {
					actionMade = true;
					// Resize
					//System.out.println("Resize");
					tgc.setState(TGState.RESIZING);
					tgc.updateMinMaxSize();
					//tdp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					tdp.mode = TDiagramPanel.RESIZING_COMPONENT;
					cdx = tgc.getX();
					cdy = tgc.getY();
					cdwidth = tgc.getWidth();
					cdheight = tgc.getHeight();
					oldx = cdx;
					oldy = cdy;
					oldwidth = cdwidth;
					oldheight = cdheight;
					decx = e.getX() - cdx;
					decy = e.getY() - cdy;
				}

				if (!actionMade) {
					TGConnectingPoint p1;
					//System.out.println("Working on TGC=" + tgc);
					if (tgc.getDefaultConnector() != -1) {
						p1 = tgc.getFreeTGConnectingPointAtAndCompatible(e.getX(), e.getY(), tgc.getDefaultConnector());
						if ((p1 != null) && (p1.isOut())) {
							// add connector
							actionMade = true;
							tdp.setSelectedTGConnectingPoint(p1);
							tdp.getGUI().actionOnButton(TGComponentManager.CONNECTOR, tgc.getDefaultConnector());
							tdp.mode = TDiagramPanel.ADDING_CONNECTOR;
							tdp.addingTGConnector();
							cp = p1;
							tdp.setAddingTGConnector(e.getX(), e.getY());
							tdp.repaint();
						}
					}
				}

				if ((!actionMade) && (tgc.isMoveable())) {
					actionMade = true;
					//Move
					tgc.setState(TGState.MOVING);
					tdp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					tdp.mode = TDiagramPanel.MOVING_COMPONENT;
					cdx = tgc.getX();
					cdy = tgc.getY();
					oldx = cdx;
					oldy = cdy;
					decx = e.getX() - cdx;
					decy = e.getY() - cdy;
				}

				if ((!actionMade) &&(tgc instanceof TGConnector)) {
					// moving segment of connector ?
					actionMade = true;
					tgco = (TGConnector)tgc;
					cde = tgco.getPointedSegment(e.getX(), e.getY());
					if (cde != null) {
						if ((!tgco.isP1(cde[0])) && (!tgco.isP2(cde[1]))) {
							// moving segment
							tgc.setState(TGState.MOVING);
							tdp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							tdp.mode = TDiagramPanel.MOVE_CONNECTOR_SEGMENT;
							oldx = cde[0].getX();
							oldy = cde[0].getY();
							decx = e.getX();
							decy = e.getY();
						} else {
							if ((tgco.isP1(cde[0])) && (tgco.isP2(cde[1]))) {
								// we choose one of the two	-> the closer one to the click
								cde = tgco.closerPToClickFirst(e.getX(), e.getY());
							} else {
								if (tgco.isP2(cde[1])) {
									cde[1] = cde[0];
									cde[0] = tgco.getTGConnectingPointP2();
								}
							}

							if (cde[0] == tgco.getTGConnectingPointP2()) {
								isOut = false;
							} else {
								isOut = true;
							}

							// moving connector head
							tdp.setMovingHead(e.getX(), e.getY(), cde[1].getX(), cde[1].getY());
							tgc.setState(TGState.MOVING);
							tdp.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							tdp.mode = TDiagramPanel.MOVE_CONNECTOR_HEAD;
							tdp.setConnectorHead(tgco);
							((TGConnectingPoint)cde[0]).setFree(true);
							//System.out.println("Moving connector head");
							if (isOut) {
								tdp.highlightOutAndFreeConnectingPoint(e.getX(), e.getY(), tgc.getType());
							} else {
								tdp.highlightInAndFreeConnectingPoint(e.getX(), e.getY(), tgc.getType());
							}
							oldx = cde[0].getX();
							oldy = cde[0].getY();
							decx = e.getX();
							decy = e.getY();
						}
					}
				}
			}
		}


		if ((tdp.mode == TDiagramPanel.NORMAL) && (selected == TGComponentManager.CONNECTOR)) {
			// connector adding
			// search for an selected connecting point
			TGConnectingPoint p1;
			p1 = tdp.getSelectedTGConnectingPoint();
			if ((p1 != null)  && (p1.isOut())){
				tdp.mode = TDiagramPanel.ADDING_CONNECTOR;
				tdp.addingTGConnector();
				cp = p1;
				tdp.setAddingTGConnector(e.getX(), e.getY());
				tdp.repaint();
			}
		}

	}

	public void mouseReleased(MouseEvent e) {

		if (tdp.mode == TDiagramPanel.MOVING_SELECTED_COMPONENTS) {
			tdp.mode = TDiagramPanel.SELECTED_COMPONENTS;
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			tdp.setStopMovingSelectedComponents();
			if ((oldx != tdp.getXSelected()) || (oldy != tdp.getYSelected())) {
				tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_COMPONENT);
			}
			tdp.repaint();
		}

		if (tdp.mode == TDiagramPanel.MOVING_COMPONENT) {
			tgc.setState(TGState.POINTED);
			tdp.mode = TDiagramPanel.NORMAL;
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if ((oldx != tgc.getX()) || (oldy != tgc.getY())) {
				//System.out.println("change2");
				tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_COMPONENT);
			}
		}

		if (tdp.mode == TDiagramPanel.RESIZING_COMPONENT) {
			tgc.setState(TGState.POINTED);
			tdp.mode = TDiagramPanel.NORMAL;
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if ((oldx != tgc.getX()) || (oldy != tgc.getY()) || (oldwidth != tgc.getWidth()) || (oldheight != tgc.getHeight())) {
				//System.out.println("change2");
				tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_COMPONENT);
			}
		}

		if (tdp.mode == TDiagramPanel.SELECTING_COMPONENTS) {
			tdp.endSelectComponents();
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			tdp.repaint();
		}

		if (tdp.mode == TDiagramPanel.MOVE_CONNECTOR_SEGMENT) {
			tgc.setState(TGState.POINTED);
			tdp.mode = TDiagramPanel.NORMAL;
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if ((oldx != cde[0].getX()) || (oldy != cde[0].getY())) {
				tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_COMPONENT);
			}
		}

		if (tdp.mode == TDiagramPanel.MOVE_CONNECTOR_HEAD) {
			tgc.setState(TGState.POINTED);
			tdp.mode = TDiagramPanel.NORMAL;
			TGConnectingPoint p;
			p = tdp.getSelectedTGConnectingPoint();
			if (p != null) {
				((TGConnectingPoint)cde[0]).setFree(true);
				p.setFree(false);
				if (isOut) {
					tgco.setP1(p);
				} else {
					tgco.setP2(p);
				}
				tdp.getGUI().changeMade(tdp, TDiagramPanel.MOVE_CONNECTOR);
				if (tgco instanceof SpecificActionAfterMove) {
					((SpecificActionAfterMove)tgco).specificActionAfterMove();
				}
			} else {
				((TGConnectingPoint)cde[0]).setFree(false);
			}
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			tdp.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
		//System.out.println("MouseClick: " + e.getClickCount());

		if (tdp.mode == TDiagramPanel.SELECTED_COMPONENTS) {
			if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)){
				tdp.mode = TDiagramPanel.NORMAL;
				tdp.mgui.setMode(MainGUI.CUTCOPY_KO);
				tdp.mgui.setMode(MainGUI.EXPORT_LIB_KO);
				tdp.unselectSelectedComponents();
				tdp.repaint();
			}
			return;
		}

		int selected = tdp.getGUI().getTypeButtonSelected();

		if ((selected == TGComponentManager.EDIT) && (e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)){
			//System.out.println("Double click");
			tgc = tdp.componentPointed();
			if (tgc != null) {
				//System.out.println("Component pointed: " + tgc.getName());
				if (tgc.doubleClick(tdp.getGUI().getFrame(), e.getX(), e.getY())) {
					//System.out.println("Change4");
					tdp.getGUI().changeMade(tdp, TDiagramPanel.CHANGE_VALUE_COMPONENT);
					tdp.repaint();
				}
			}
		}

		if ((selected == TGComponentManager.CONNECTOR) && (e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {
			// if there is a close connecting point
			if (tdp.mode == TDiagramPanel.ADDING_CONNECTOR) {
				TGConnectingPoint p1;
				p1 = tdp.getSelectedTGConnectingPoint();
				if (p1 != null) {
					if ((p1 != cp) && (p1.isIn())) {
						tdp.finishAddingConnector(p1);
						tdp.mode = TDiagramPanel.NORMAL;
						tdp.getGUI().setEditMode();
						//System.out.println("Change3");
						tdp.getGUI().changeMade(tdp, TDiagramPanel.NEW_CONNECTOR);
						tdp.repaint();
					}
				} else {
					// no close connecting point -> adding point
					tdp.addPointToTGConnector(e.getX(), e.getY());
					tdp.repaint();
				}
			}
		}

		if ((selected == TGComponentManager.CONNECTOR) && (e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
			if (tdp.mode == TDiagramPanel.ADDING_CONNECTOR) {
				tdp.mode = TDiagramPanel.NORMAL;
				tdp.stopAddingConnector(true);
				tdp.getGUI().setEditMode();
				tdp.repaint();
			} else {
				tdp.getGUI().setEditMode();
				tdp.repaint();
			}
		}

		if ((selected == TGComponentManager.COMPONENT) &&(e.getButton() == MouseEvent.BUTTON1)){
			TGComponent comp = tdp.addComponent(e.getX(), e.getY(), true);
			tdp.autoConnect(comp);
			tdp.getGUI().setEditMode();
			//System.out.println("change1");
			tdp.getGUI().changeMade(tdp, TDiagramPanel.NEW_COMPONENT);
			tdp.repaint();
		}

	}

	public void setCursor(int info) {
		switch(info) {
		case 1:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			break;
		case 2:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			break;
		case 3:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			break;
		case 4:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			break;
		case 5:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			break;
		case 6:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			break;
		case 7:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			break;
		case 8:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			break;
		default:
			tdp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	public void mouseMoved(MouseEvent e) {
		tdp.currentX = e.getX();
		tdp.currentY = e.getY();

		int selected = tdp.getGUI().getTypeButtonSelected();

		if (tdp.mode == TDiagramPanel.SELECTED_COMPONENTS) {
			if (tdp.showSelectionZone(e.getX(), e.getY())) {
				tdp.repaint();
			}
		}

		if ((tdp.mode == TDiagramPanel.NORMAL) && (selected == TGComponentManager.EDIT) && (selectedComponent == false)){
			byte info = tdp.highlightComponent(e.getX(), e.getY());
			if (info > 1) {
				tgc = tdp.componentPointed();
				if (tgc.isUserResizable()) {
					setCursor(tgc.getResizeZone(e.getX(), e.getY()));
				} else {
					tdp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			} else {
				tdp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			if ((info == 1) || (info == 3)) {
				tdp.updateJavaCode();
				tdp.repaint();
			}
		}

		if ((selected == TGComponentManager.CONNECTOR) && (tdp.mode == TDiagramPanel.NORMAL)) { // is connectingPointShow selected
			if(tdp.highlightOutAndFreeConnectingPoint(e.getX(), e.getY(), tdp.getMGUI().getIdButtonSelected())) {
				tdp.repaint();
			}
		}

		if ((selected == TGComponentManager.CONNECTOR) && (tdp.mode == TDiagramPanel.ADDING_CONNECTOR)) {
			tdp.highlightInAndFreeConnectingPoint(e.getX(), e.getY(), tdp.getMGUI().getIdButtonSelected());
			tdp.setAddingTGConnector(e.getX(), e.getY());
			tdp.repaint();
		}

	}

	public void mouseDragged(MouseEvent e) {
		if (tdp.mode == TDiagramPanel.MOVING_COMPONENT) {
			tgc.setMoveCd(e.getX() - decx, e.getY() - decy);
			tdp.repaint();
			return;
		}

		if (tdp.mode == TDiagramPanel.RESIZING_COMPONENT) {
			applyResize(e.getX(), e.getY());
			tdp.repaint();
			return;
		}

		if (tdp.mode == TDiagramPanel.MOVING_SELECTED_COMPONENTS) {
			tdp.moveSelected(e.getX() - decx, e.getY() - decy);
			tdp.repaint();
			return;
		}


		if (tdp.mode == TDiagramPanel.SELECTING_COMPONENTS) {
			tdp.updateSelectingComponents(e.getX(), e.getY());
			tdp.repaint();
			return;
		}

		if (tdp.mode == TDiagramPanel.MOVE_CONNECTOR_SEGMENT) {
			cde[0].setCd(cde[0].getX() + e.getX() - decx, cde[0].getY() + e.getY() - decy);
			cde[1].setCd(cde[1].getX() + e.getX() - decx, cde[1].getY() + e.getY() - decy);
			decx = e.getX();
			decy = e.getY();
			tdp.repaint();
		}

		if (tdp.mode == TDiagramPanel.MOVE_CONNECTOR_HEAD) {
			tdp.setMovingHead(e.getX(), e.getY(), cde[1].getX(), cde[1].getY());
			if (isOut) {
				tdp.highlightOutAndFreeConnectingPoint(e.getX(), e.getY(), tgc.getType());
			} else {
				tdp.highlightInAndFreeConnectingPoint(e.getX(), e.getY(), tgc.getType());
			}
			tdp.repaint();
		}
	}

	public void applyResize(int choicex, int choicey) {
		Point px, py;
		switch(resizeInfo) {
		case 1:
			px = tgc.modifyInX(choicex);
			py = tgc.modifyInY(choicey);
			tgc.setUserResize(px.x, py.x, px.y, py.y);
			break;
		case 2:
			py = tgc.modifyInY(choicey);
			tgc.setUserResize(cdx, py.x, cdwidth, py.y);
			break;
		case 3:
			px = tgc.modifyInWidth(choicex);
			py = tgc.modifyInY(choicey);
			tgc.setUserResize(px.x, py.x, px.y, py.y);
			break;
		case 4:
			//System.out.println("Modify in X");
			px = tgc.modifyInX(choicex);
			tgc.setUserResize(px.x, cdy, px.y, cdheight);
			break;
		case 5:
			px = tgc.modifyInWidth(choicex);
			tgc.setUserResize(px.x, cdy, px.y, cdheight);
			break;
		case 6:
			px = tgc.modifyInX(choicex);
			py = tgc.modifyInHeight(choicey);
			tgc.setUserResize(px.x, py.x, px.y, py.y);
			break;
		case 7:
			py = tgc.modifyInHeight(choicey);
			tgc.setUserResize(cdx, py.x, cdwidth, py.y);
			break;
		case 8:
			px = tgc.modifyInWidth(choicex);
			py = tgc.modifyInHeight(choicey);
			tgc.setUserResize(px.x, py.x, px.y, py.y);
			break;
		default:
		}


	}
}

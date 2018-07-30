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

import ui.*;
import ui.util.IconManager;
import ui.window.JDialogELNConnector;
import ui.window.JDialogSysCAMSConnector;

import java.awt.*;
import java.util.Vector;

import javax.swing.JFrame;

import myutil.GraphicLib;

/**
 * Class SysCAMSPortConnector 
 * Connector used in SystemC-AMS Component task diagrams 
 * Creation: 27/04/2018
 * @version 1.0 27/04/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSPortConnector extends TGConnector implements ScalableTGComponent, SpecificActionAfterAdd, SpecificActionAfterMove {
	protected double oldScaleFactor;

	public SysCAMSPortConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
		super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
		
		myImageIcon = IconManager.imgic202;
		value = "";
		editable = true;
		oldScaleFactor = tdp.getZoom();
		
		p1 = _p1;
		p2 = _p2;
	}

	public TGConnectingPoint get_p1() {
		return p1;
	}

	public TGConnectingPoint get_p2() {
		return p2;
	}

	public boolean editOndoubleClick(JFrame frame) {
		JDialogSysCAMSConnector jde = new JDialogSysCAMSConnector(this);
		jde.setVisible(true);
		return true;
	}
	
	protected void drawLastSegment(Graphics gr, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) gr;
		
		int w = g.getFontMetrics().stringWidth(value);
		Font fold = g.getFont();
		Font f = fold.deriveFont(Font.ITALIC, (float) (tdp.getFontSize()));
		g.setFont(f);
		g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		g.setFont(fold);

		try {
			SysCAMSPortConnectingPoint pt1 = (SysCAMSPortConnectingPoint) p1;
			SysCAMSPortConnectingPoint pt2 = (SysCAMSPortConnectingPoint) p2;
			if (!pt1.positionned) {
				pt1.positionned = true;
				if (pt1.getFather() instanceof SysCAMSPrimitivePort) {
					pt1.port = (SysCAMSPrimitivePort) (pt1.getFather());
				}
			}
			if (!pt2.positionned) {
				pt2.positionned = true;
				if (pt2.getFather() instanceof SysCAMSPrimitivePort) {
					pt2.port = (SysCAMSPrimitivePort) (pt2.getFather());
				}
			}
			if ((pt1.port != null) && (pt2.port != null)) {
				if ((pt1.port instanceof SysCAMSPortConverter) && (pt2.port instanceof SysCAMSPortDE)) {
					if (pt2.port.getFather().getFather() instanceof SysCAMSCompositeComponent) {
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					} else {
						Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
						g.setStroke(dashed);
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					}
				} else if ((pt2.port instanceof SysCAMSPortConverter) && (pt1.port instanceof SysCAMSPortDE)) {
					if (pt1.port.getFather().getFather() instanceof SysCAMSCompositeComponent) {
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					} else {
						Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
						g.setStroke(dashed);
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					}
				} else if ((pt1.port instanceof SysCAMSPortDE) && (pt2.port instanceof SysCAMSPortDE) 
						|| (pt2.port instanceof SysCAMSPortDE) && (pt1.port instanceof SysCAMSPortDE)) {
					if (pt1.port.getFather().getFather() instanceof SysCAMSCompositeComponent 
							&& pt2.port.getFather().getFather() instanceof SysCAMSCompositeComponent) {
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					} else {
						Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
						g.setStroke(dashed);
						GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
					}
				} else if ((pt1.port instanceof SysCAMSPortTDF) && (pt2.port instanceof SysCAMSPortTDF)
						 || (pt2.port instanceof SysCAMSPortTDF) && (pt1.port instanceof SysCAMSPortTDF)) {
					GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
				}
			}
			return;
		} catch (Exception e) {
		}
	}

	public void rescale(double scaleFactor) {
		int xx, yy;

		for (int i = 0; i < nbInternalTGComponent; i++) {
			xx = tgcomponent[i].getX();
			yy = tgcomponent[i].getY();
			tgcomponent[i].dx = (tgcomponent[i].dx + xx) / oldScaleFactor * scaleFactor;
			tgcomponent[i].dy = (tgcomponent[i].dy + yy) / oldScaleFactor * scaleFactor;
			xx = (int) (tgcomponent[i].dx);
			tgcomponent[i].dx = tgcomponent[i].dx - xx;
			yy = (int) (tgcomponent[i].dy);
			tgcomponent[i].dy = tgcomponent[i].dy - yy;
			tgcomponent[i].setCd(xx, yy);
		}
		oldScaleFactor = scaleFactor;
	}

	public int getType() {
		return TGComponentManager.CAMS_CONNECTOR;
	}

	public void specificActionAfterAdd() {
		((SysCAMSComponentTaskDiagramPanel) tdp).updatePorts();
	}

	public void specificActionAfterMove() {
		((SysCAMSComponentTaskDiagramPanel) tdp).updatePorts();
	}
}
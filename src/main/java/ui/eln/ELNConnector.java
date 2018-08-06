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

package ui.eln;

import ui.*;
import ui.eln.sca_eln_sca_tdf.*;
import ui.util.IconManager;
import ui.window.JDialogELNConnector;
import java.awt.*;
import java.util.*;
import javax.swing.JFrame;
import myutil.GraphicLib;

/**
 * Class ELNConnector 
 * Connector used in ELN diagrams 
 * Creation: 11/06/2018
 * @version 1.0 11/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNConnector extends TGConnector implements ScalableTGComponent {
	protected double oldScaleFactor;

	public ELNConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
		super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);

		myImageIcon = IconManager.imgic202;
		value = "";
		editable = false;
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
		JDialogELNConnector jde = new JDialogELNConnector(this);
		jde.setVisible(true);
		return true;
	}

	protected void drawLastSegment(Graphics gr, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) gr;
		
		int w = g.getFontMetrics().stringWidth(value);
		Font fold = g.getFont();
		Font f = fold.deriveFont(Font.ITALIC, (float) (tdp.getFontSize()));
		g.setFont(f);
		
		ELNConnectingPoint pt1 = (ELNConnectingPoint) p1;
		ELNConnectingPoint pt2 = (ELNConnectingPoint) p2;

		if ((pt1.getFather() instanceof ELNComponent && pt2.getFather() instanceof ELNComponent) || (pt2.getFather() instanceof ELNComponent && pt1.getFather() instanceof ELNComponent)) {
			g.drawLine(x1, y1, x2, y2);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt1.getFather() instanceof ELNComponent && pt2.getFather() instanceof ELNMidPortTerminal) {
			ELNConnector connector = (ELNConnector) ((ELNMidPortTerminal) pt2.getFather()).getFather();
			g.drawLine(x1, y1, x2, y2);
			if (connector.getValue().equals("")) {
				value = searchName(connector);
			} else {
				value = connector.getValue();
			}
		} else if (pt2.getFather() instanceof ELNComponent && pt1.getFather() instanceof ELNMidPortTerminal) {
			g.drawLine(x1, y1, x2, y2);
			ELNConnector connector = (ELNConnector) ((ELNMidPortTerminal) pt1.getFather()).getFather();
			if (connector.getValue().equals("")) {
				value = searchName(connector);
			} else {
				value = connector.getValue();
			}
		} else if ((pt1.getFather() instanceof ELNModuleTerminal && pt2.getFather() instanceof ELNModuleTerminal) || (pt2.getFather() instanceof ELNModuleTerminal && pt1.getFather() instanceof ELNModuleTerminal)) {
			String name1 = ((ELNModuleTerminal) pt1.getFather()).getValue();
			String name2 = ((ELNModuleTerminal) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			g.drawLine(x1, y1, x2, y2);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt1.getFather() instanceof ELNModuleTerminal && pt2.getFather() instanceof ELNClusterTerminal) {
			String name1 = ((ELNModuleTerminal) pt1.getFather()).getValue();
			String name2 = ((ELNClusterTerminal) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			g.drawLine(x1, y1, x2, y2);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt2.getFather() instanceof ELNModuleTerminal && pt1.getFather() instanceof ELNClusterTerminal) {
			String name1 = ((ELNClusterTerminal) pt1.getFather()).getValue();
			String name2 = ((ELNModuleTerminal) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			g.drawLine(x1, y1, x2, y2);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if ((pt1.getFather() instanceof ELNModulePortDE && pt2.getFather() instanceof ELNModulePortDE) || (pt2.getFather() instanceof ELNModulePortDE && pt1.getFather() instanceof ELNModulePortDE)) {
			String name1 = ((ELNModulePortDE) pt1.getFather()).getValue();
			String name2 = ((ELNModulePortDE) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
			g.setStroke(dashed);
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt1.getFather() instanceof ELNModulePortDE && pt2.getFather() instanceof ELNClusterPortDE) {
			((ELNClusterPortDE) pt2.getFather()).setPortType(((ELNModulePortDE) pt1.getFather()).getPortType());
			((ELNClusterPortTDF) pt2.getFather()).setOrigin(((ELNModulePortDE) pt1.getFather()).getOrigin());
			((ELNClusterPortTDF) pt2.getFather()).setValue(((ELNModulePortDE) pt1.getFather()).getValue());
			
			String name1 = ((ELNModulePortDE) pt1.getFather()).getValue();
			String name2 = ((ELNClusterPortDE) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
			g.setStroke(dashed);
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt2.getFather() instanceof ELNModulePortDE && pt1.getFather() instanceof ELNClusterPortDE) {
			((ELNClusterPortDE) pt1.getFather()).setPortType(((ELNModulePortDE) pt2.getFather()).getPortType());
			((ELNClusterPortDE) pt1.getFather()).setOrigin(((ELNModulePortDE) pt2.getFather()).getOrigin());
			((ELNClusterPortDE) pt1.getFather()).setValue(((ELNModulePortDE) pt2.getFather()).getValue());
			
			String name1 = ((ELNClusterPortDE) pt1.getFather()).getValue();
			String name2 = ((ELNModulePortDE) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,	new float[] { 9 }, 0);
			g.setStroke(dashed);
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if ((pt1.getFather() instanceof ELNModulePortTDF && pt2.getFather() instanceof ELNModulePortTDF) || (pt2.getFather() instanceof ELNModulePortTDF && pt1.getFather() instanceof ELNModulePortTDF)) {
			String name1 = ((ELNModulePortTDF) pt1.getFather()).getValue();
			String name2 = ((ELNModulePortTDF) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt1.getFather() instanceof ELNModulePortTDF && pt2.getFather() instanceof ELNClusterPortTDF) {
			((ELNClusterPortTDF) pt2.getFather()).setPortType(((ELNModulePortTDF) pt1.getFather()).getPortType());
			((ELNClusterPortTDF) pt2.getFather()).setOrigin(((ELNModulePortTDF) pt1.getFather()).getOrigin());
			((ELNClusterPortTDF) pt2.getFather()).setValue(((ELNModulePortTDF) pt1.getFather()).getValue());
			
			String name1 = ((ELNModulePortTDF) pt1.getFather()).getValue();
			String name2 = ((ELNClusterPortTDF) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt2.getFather() instanceof ELNModulePortTDF && pt1.getFather() instanceof ELNClusterPortTDF) {
			((ELNClusterPortTDF) pt1.getFather()).setPortType(((ELNModulePortTDF) pt2.getFather()).getPortType());
			((ELNClusterPortTDF) pt1.getFather()).setOrigin(((ELNModulePortTDF) pt2.getFather()).getOrigin());
			((ELNClusterPortTDF) pt1.getFather()).setValue(((ELNModulePortTDF) pt2.getFather()).getValue());
			
			String name1 = ((ELNClusterPortTDF) pt1.getFather()).getValue();
			String name2 = ((ELNModulePortTDF) pt2.getFather()).getValue();
			if (name1.equals(name2)) {
				value = name1;
			}
			GraphicLib.arrowWithLine(g, 1, 0, 10, x1, y1, x2, y2, true);
			editable = true;
			g.drawString(value, (x1 + x2 - w) / 2, (y1 + y2) / 2);
		} else if (pt1.getFather() instanceof ELNModulePortTDF && (pt2.getFather() instanceof ELNComponentVoltageSourceTDF || pt2.getFather() instanceof ELNComponentVoltageSinkTDF || pt2.getFather() instanceof ELNComponentCurrentSourceTDF || pt2.getFather() instanceof ELNComponentCurrentSinkTDF)) {
			g.drawLine(x1, y1, x2, y2);
			value = ((ELNModulePortTDF) pt1.getFather()).getValue();
		} else if (pt2.getFather() instanceof ELNModulePortTDF && (pt1.getFather() instanceof ELNComponentVoltageSourceTDF || pt1.getFather() instanceof ELNComponentVoltageSinkTDF || pt1.getFather() instanceof ELNComponentCurrentSourceTDF || pt1.getFather() instanceof ELNComponentCurrentSinkTDF)) {
			g.drawLine(x1, y1, x2, y2);
			value = ((ELNModulePortTDF) pt2.getFather()).getValue();
		} else {
			g.drawLine(x1, y1, x2, y2);
		}

		g.setFont(fold);
	}
	
	private String searchName(ELNConnector c) {
		if (c.p1.getFather() instanceof ELNComponent) {
			if (c.p2.getFather() instanceof ELNComponent) {
				return c.getValue();
			}
			if (c.p2.getFather() instanceof ELNNodeRef || c.p2.getFather() instanceof ELNModuleTerminal) {
				return "";
			}
			if (c.p2.getFather() instanceof ELNMidPortTerminal) {
				ELNConnector connector = (ELNConnector) ((ELNMidPortTerminal) c.p2.getFather()).getFather();
				if (!connector.getValue().equals("")) {
					return connector.getValue();
				} else {
					return searchName(connector);
				}
			}
		}
		if (c.p1.getFather() instanceof ELNNodeRef || c.p1.getFather() instanceof ELNModuleTerminal) {
			return "";
		}
		if (c.p1.getFather() instanceof ELNMidPortTerminal) {
			if (c.p2.getFather() instanceof ELNNodeRef || c.p2.getFather() instanceof ELNModuleTerminal) {
				return "";
			}
			if (c.p2.getFather() instanceof ELNComponent) {
				ELNConnector connector = (ELNConnector) ((ELNMidPortTerminal) c.p1.getFather()).getFather();
				if (!connector.getValue().equals("")) {
					return connector.getValue();
				} else {
					return searchName(connector);
				}
			}
		}
		return "";
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
		return TGComponentManager.ELN_CONNECTOR;
	}

	public java.util.List<ELNMidPortTerminal> getAllMidPortTerminal() {
		java.util.List<ELNMidPortTerminal> list = new ArrayList<ELNMidPortTerminal>();
		for (int i = 0; i < nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ELNMidPortTerminal) {
				list.add((ELNMidPortTerminal) (tgcomponent[i]));
			}
		}
		return list;
	}
}
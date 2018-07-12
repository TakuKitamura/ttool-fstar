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

package ui.eln.sca_eln;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.eln.*;
import ui.window.JDialogELNComponentTransmissionLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class ELNComponentTransmissionLine 
 * Transmission line to be used in ELN diagrams 
 * Creation: 15/06/2018
 * @version 1.0 15/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNComponentTransmissionLine extends TGCScalableWithInternalComponent implements ActionListener, SwallowTGComponent, ELNComponent {
	protected Color myColor;
	protected int orientation;
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;

	private int textX = 15;
	private double dtextX = 0.0;
	protected int decPoint = 3;

	private double z0, delta0;
	private String delay;
	private String unit0, unit2;

	private int position = 0;
	private boolean fv_0_2 = false, fv_1_3 = false, fh_0_2 = false, fh_1_3 = false;
	private int old;
	private boolean first, f = true;

	private ELNPortTerminal term0;
	private ELNPortTerminal term1;
	private ELNPortTerminal term2;
	private ELNPortTerminal term3;

	public ELNComponentTransmissionLine(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		initScaling(120, 80);

		dtextX = textX * oldScaleFactor;
		textX = (int) dtextX;
		dtextX = dtextX - textX;

		minWidth = 1;
		minHeight = 1;

		addTGConnectingPointsComment();

		moveable = true;
		editable = true;
		removable = true;
		userResizable = false;
		value = tdp.findELNComponentName("TransmissionLine");

		setZ0(100.0);
		setUnit0("\u03A9");
		setDelay("sc_core::SC_ZERO_TIME");
		setDelta0(0.0);
		setUnit2("Hz");
	}

	public Color getMyColor() {
		return myColor;
	}

	public void internalDrawing(Graphics g) {
		if (f == true) {
			term0 = new ELNPortTerminal(x, y, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term0.setValue("a1");
			getTDiagramPanel().getComponentList().add(term0);
			term0.getTDiagramPanel().addComponent(term0, x, y, true, false);

			term1 = new ELNPortTerminal(x + width - width / 12, y, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term1.setValue("a2");
			getTDiagramPanel().getComponentList().add(term1);
			term1.getTDiagramPanel().addComponent(term1, x + width - width / 12, y, true, false);

			term2 = new ELNPortTerminal(x, y + height - height / 8, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term2.setValue("b1");
			getTDiagramPanel().getComponentList().add(term2);
			term2.getTDiagramPanel().addComponent(term2, x, y + height - height / 8, true, false);

			term3 = new ELNPortTerminal(x + width - width / 12, y + height - height / 8, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term3.setValue("b2");
			getTDiagramPanel().getComponentList().add(term3);
			term3.getTDiagramPanel().addComponent(term3, x + width - width / 12, y + height - height / 8, true, false);
			old = width;
			width = height;
			height = old;
			f = false;
		}

		Font f = g.getFont();
		Font fold = f;

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

		Color c = g.getColor();

		if (position == 0) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			rotateTopBottom(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("a1");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("a2");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("b1");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("b2");
			int sh3 = g.getFontMetrics().getAscent();
			int w = g.getFontMetrics().stringWidth(value);
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + (width - w) / 2, y - height / 4);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				term0.setMoveCd(x, y, true);
				term1.setMoveCd(x + width - width / 12, y, true);
				term2.setMoveCd(x, y + height - height / 8, true);
				term3.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term0.getValue(), x - height / 8 - sw0, y);
				g.drawString(term1.getValue(), x + width + height / 8, y);
				g.drawString(term2.getValue(), x - height / 8 - sw2, y + height + sh2);
				g.drawString(term3.getValue(), x + width + height / 8, y + height + sh3);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				term1.setMoveCd(x, y, true);
				term0.setMoveCd(x + width - width / 12, y, true);
				term3.setMoveCd(x, y + height - height / 8, true);
				term2.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term1.getValue(), x - height / 8 - sw1, y);
				g.drawString(term0.getValue(), x + width + height / 8, y);
				g.drawString(term3.getValue(), x - height / 8 - sw3, y + height + sh3);
				g.drawString(term2.getValue(), x + width + height / 8, y + height + sh2);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				term2.setMoveCd(x, y, true);
				term3.setMoveCd(x + width - width / 12, y, true);
				term0.setMoveCd(x, y + height - height / 8, true);
				term1.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term2.getValue(), x - height / 8 - sw2, y);
				g.drawString(term3.getValue(), x + width + height / 8, y);
				g.drawString(term0.getValue(), x - height / 8 - sw0, y + height + sh0);
				g.drawString(term1.getValue(), x + width + height / 8, y + height + sh1);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				term3.setMoveCd(x, y, true);
				term2.setMoveCd(x + width - width / 12, y, true);
				term1.setMoveCd(x, y + height - height / 8, true);
				term0.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term3.getValue(), x - height / 8 - sw3, y);
				g.drawString(term2.getValue(), x + width + height / 8, y);
				g.drawString(term1.getValue(), x - height / 8 - sw1, y + height + sh1);
				g.drawString(term0.getValue(), x + width + height / 8, y + height + sh0);
			}
		} else if (position == 1) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			rotateRightLeft(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("a1");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("a2");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("b1");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("b2");
			int sh3 = g.getFontMetrics().getAscent();
			int w = g.getFontMetrics().stringWidth(value);
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + (width - w) / 2, y - height / 6);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				term2.setMoveCd(x, y, true);
				term0.setMoveCd(x + width - width / 8, y, true);
				term3.setMoveCd(x, y + height - height / 12, true);
				term1.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term2.getValue(), x - width / 8 - sw2, y);
				g.drawString(term0.getValue(), x + width + width / 8, y);
				g.drawString(term3.getValue(), x - width / 8 - sw3, y + height + sh3);
				g.drawString(term1.getValue(), x + width + width / 8, y + height + sh1);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				term3.setMoveCd(x, y, true);
				term1.setMoveCd(x + width - width / 8, y, true);
				term2.setMoveCd(x, y + height - height / 12, true);
				term0.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term3.getValue(), x - width / 8 - sw3, y);
				g.drawString(term1.getValue(), x + width + width / 8, y);
				g.drawString(term2.getValue(), x - width / 8 - sw2, y + height + sh2);
				g.drawString(term0.getValue(), x + width + width / 8, y + height + sh0);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				term0.setMoveCd(x, y, true);
				term2.setMoveCd(x + width - width / 8, y, true);
				term1.setMoveCd(x, y + height - height / 12, true);
				term3.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term0.getValue(), x - width / 8 - sw0, y);
				g.drawString(term2.getValue(), x + width + width / 8, y);
				g.drawString(term1.getValue(), x - width / 8 - sw1, y + height + sh1);
				g.drawString(term3.getValue(), x + width + width / 8, y + height + sh3);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				term1.setMoveCd(x, y, true);
				term3.setMoveCd(x + width - width / 8, y, true);
				term0.setMoveCd(x, y + height - height / 12, true);
				term2.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term1.getValue(), x - width / 8 - sw1, y);
				g.drawString(term3.getValue(), x + width + width / 8, y);
				g.drawString(term0.getValue(), x - width / 8 - sw0, y + height + sh0);
				g.drawString(term2.getValue(), x + width + width / 8, y + height + sh2);
			}
		} else if (position == 2) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			rotateTopBottom(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("a1");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("a2");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("b1");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("b2");
			int sh3 = g.getFontMetrics().getAscent();
			int w = g.getFontMetrics().stringWidth(value);
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + (width - w) / 2, y - height / 4);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				term3.setMoveCd(x, y, true);
				term2.setMoveCd(x + width - width / 12, y, true);
				term1.setMoveCd(x, y + height - height / 8, true);
				term0.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term3.getValue(), x - height / 8 - sw3, y);
				g.drawString(term2.getValue(), x + width + height / 8, y);
				g.drawString(term1.getValue(), x - height / 8 - sw1, y + height + sh1);
				g.drawString(term0.getValue(), x + width + height / 8, y + height + sh0);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				term2.setMoveCd(x, y, true);
				term3.setMoveCd(x + width - width / 12, y, true);
				term0.setMoveCd(x, y + height - height / 8, true);
				term1.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term2.getValue(), x - height / 8 - sw2, y);
				g.drawString(term3.getValue(), x + width + height / 8, y);
				g.drawString(term0.getValue(), x - height / 8 - sw0, y + height + sh0);
				g.drawString(term1.getValue(), x + width + height / 8, y + height + sh1);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				term1.setMoveCd(x, y, true);
				term0.setMoveCd(x + width - width / 12, y, true);
				term3.setMoveCd(x, y + height - height / 8, true);
				term2.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term1.getValue(), x - height / 8 - sw1, y);
				g.drawString(term0.getValue(), x + width + height / 8, y);
				g.drawString(term3.getValue(), x - height / 8 - sw3, y + height + sh3);
				g.drawString(term2.getValue(), x + width + height / 8, y + height + sh2);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				term0.setMoveCd(x, y, true);
				term1.setMoveCd(x + width - width / 12, y, true);
				term2.setMoveCd(x, y + height - height / 8, true);
				term3.setMoveCd(x + width - width / 12, y + height - height / 8, true);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term0.getValue(), x - height / 8 - sw0, y);
				g.drawString(term1.getValue(), x + width + height / 8, y);
				g.drawString(term2.getValue(), x - height / 8 - sw2, y + height + sh2);
				g.drawString(term3.getValue(), x + width + height / 8, y + height + sh3);
			}
		} else if (position == 3) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			rotateRightLeft(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("a1");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("a2");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("b1");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("b2");
			int sh3 = g.getFontMetrics().getAscent();
			int w = g.getFontMetrics().stringWidth(value);
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + (width - w) / 2, y - height / 6);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				term1.setMoveCd(x, y, true);
				term3.setMoveCd(x + width - width / 8, y, true);
				term0.setMoveCd(x, y + height - height / 12, true);
				term2.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term1.getValue(), x - width / 8 - sw1, y);
				g.drawString(term3.getValue(), x + width + width / 8, y);
				g.drawString(term0.getValue(), x - width / 8 - sw0, y + height + sh0);
				g.drawString(term2.getValue(), x + width + width / 8, y + height + sh2);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				term0.setMoveCd(x, y, true);
				term2.setMoveCd(x + width - width / 8, y, true);
				term1.setMoveCd(x, y + height - height / 12, true);
				term3.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term0.getValue(), x - width / 8 - sw0, y);
				g.drawString(term2.getValue(), x + width + width / 8, y);
				g.drawString(term1.getValue(), x - width / 8 - sw1, y + height + sh1);
				g.drawString(term3.getValue(), x + width + width / 8, y + height + sh3);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				term3.setMoveCd(x, y, true);
				term1.setMoveCd(x + width - width / 8, y, true);
				term2.setMoveCd(x, y + height - height / 12, true);
				term0.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term3.getValue(), x - width / 8 - sw3, y);
				g.drawString(term1.getValue(), x + width + width / 8, y);
				g.drawString(term2.getValue(), x - width / 8 - sw2, y + height + sh2);
				g.drawString(term0.getValue(), x + width + width / 8, y + height + sh0);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				term2.setMoveCd(x, y, true);
				term0.setMoveCd(x + width - width / 8, y, true);
				term3.setMoveCd(x, y + height - height / 12, true);
				term1.setMoveCd(x + width - width / 8, y + height - height / 12, true);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term2.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term0.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setW(0.0);
				((ELNConnectingPoint) (term3.getTGConnectingPointAtIndex(0))).setH(0.5);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setW(1.0);
				((ELNConnectingPoint) (term1.getTGConnectingPointAtIndex(0))).setH(0.5);
				g.drawString(term2.getValue(), x - width / 8 - sw2, y);
				g.drawString(term0.getValue(), x + width + width / 8, y);
				g.drawString(term3.getValue(), x - width / 8 - sw3, y + height + sh3);
				g.drawString(term1.getValue(), x + width + width / 8, y + height + sh1);
			}
		}
		g.setColor(c);
		g.setFont(fold);
	}

	private void rotateTopBottom(Graphics g) {
		int[] ptx0 = { x, x + width / 6, x + 5 * width / 6, x + width, x + 5 * width / 6, x + width / 6 };
		int[] pty0 = { y, y + height / 2 - height / 8, y + height / 2 - height / 8, y, y + height / 2 - height / 8,
				y + height / 2 - height / 8 };
		g.drawPolygon(ptx0, pty0, 6);
		int[] ptx1 = { x, x + width / 6, x + 5 * width / 6, x + width, x + 5 * width / 6, x + width / 6 };
		int[] pty1 = { y + height, y + height / 2 + height / 8, y + height / 2 + height / 8, y + height,
				y + height / 2 + height / 8, y + height / 2 + height / 8 };
		g.drawPolygon(ptx1, pty1, 6);
	}

	private void rotateRightLeft(Graphics g) {
		int[] ptx0 = { x, x + width / 2 - width / 8, x + width / 2 - width / 8, x, x + width / 2 - width / 8,
				x + width / 2 - width / 8 };
		int[] pty0 = { y, y + height / 6, y + 5 * height / 6, y + height, y + 5 * height / 6, y + height / 6 };
		g.drawPolygon(ptx0, pty0, 6);
		int[] ptx1 = { x + width, x + width / 2 + width / 8, x + width / 2 + width / 8, x + width,
				x + width / 2 + width / 8, x + width / 2 + width / 8 };
		int[] pty1 = { y, y + height / 6, y + 5 * height / 6, y + height, y + 5 * height / 6, y + height / 6 };
		g.drawPolygon(ptx1, pty1, 6);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	public int getType() {
		return TGComponentManager.ELN_TRANSMISSION_LINE;
	}

	public boolean editOndoubleClick(JFrame frame) {
		JDialogELNComponentTransmissionLine jde = new JDialogELNComponentTransmissionLine(this);
		jde.setVisible(true);
		return true;
	}

	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		for (int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch (c) {
			case '\u03A9':
				buffer.append("&#x3A9;");
				break;
			case '\u03BC':
				buffer.append("&#x3BC;");
				break;
			default:
				buffer.append(databuf.charAt(pos));
				break;
			}
		}
		return buffer;
	}

	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<attributes z0=\"" + z0);
		sb.append("\" unit0=\"" + encode(unit0));
		sb.append("\" delay=\"" + delay);
		sb.append("\" delta0=\"" + delta0);
		sb.append("\" unit2=\"" + encode(unit2));
		sb.append("\" position=\"" + position);
		sb.append("\" width=\"" + width);
		sb.append("\" height=\"" + height);
		sb.append("\" fv_0_2=\"" + fv_0_2);
		sb.append("\" fv_1_3=\"" + fv_1_3);
		sb.append("\" fh_0_2=\"" + fh_0_2);
		sb.append("\" fh_1_3=\"" + fh_1_3);
		sb.append("\" first=\"" + first + "\"");
		sb.append("/>\n");
		sb.append("</extraparam>\n");
		return new String(sb);
	}

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
		try {
			NodeList nli;
			Node n1, n2;
			Element elt;

			double z0, delta0;
			String delay;
			String unit0, unit2;
			int position, width, height;
			boolean fv_0_2, fv_1_3, fh_0_2, fh_1_3, first;

			for (int i = 0; i < nl.getLength(); i++) {
				n1 = nl.item(i);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					nli = n1.getChildNodes();
					for (int j = 0; j < nli.getLength(); j++) {
						n2 = nli.item(j);
						if (n2.getNodeType() == Node.ELEMENT_NODE) {
							elt = (Element) n2;
							if (elt.getTagName().equals("attributes")) {
								z0 = Double.parseDouble(elt.getAttribute("z0"));
								unit0 = elt.getAttribute("unit0");
								delay = elt.getAttribute("delay");
								delta0 = Double.parseDouble(elt.getAttribute("delta0"));
								unit2 = elt.getAttribute("unit2");
								position = Integer.parseInt(elt.getAttribute("position"));
								width = Integer.parseInt(elt.getAttribute("width"));
								height = Integer.parseInt(elt.getAttribute("height"));
								fv_0_2 = Boolean.parseBoolean(elt.getAttribute("fv_0_2"));
								fv_1_3 = Boolean.parseBoolean(elt.getAttribute("fv_1_3"));
								fh_0_2 = Boolean.parseBoolean(elt.getAttribute("fh_0_2"));
								fh_1_3 = Boolean.parseBoolean(elt.getAttribute("fh_1_3"));
								first = Boolean.parseBoolean(elt.getAttribute("first"));
								setZ0(z0);
								setUnit0(unit0);
								setDelay(delay);
								setDelta0(delta0);
								setUnit2(unit2);
								setPosition(position);
								this.width = width;
								this.height = height;
								setFv_0_2(fv_0_2);
								setFv_1_3(fv_1_3);
								setFh_0_2(fh_0_2);
								setFh_1_3(fh_1_3);
								setFirst(first);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new MalformedModelingException();
		}
	}

	public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
		componentMenu.addSeparator();

		JMenuItem rotateright = new JMenuItem("Rotate right 90\u00b0");
		rotateright.addActionListener(this);
		componentMenu.add(rotateright);

		JMenuItem rotateleft = new JMenuItem("Rotate left 90\u00b0");
		rotateleft.addActionListener(this);
		componentMenu.add(rotateleft);

		componentMenu.addSeparator();

		JMenuItem rotatevertically = new JMenuItem("Flip vertically");
		rotatevertically.addActionListener(this);
		componentMenu.add(rotatevertically);

		JMenuItem rotatehorizontally = new JMenuItem("Flip horizontally");
		rotatehorizontally.addActionListener(this);
		componentMenu.add(rotatehorizontally);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Rotate right 90\u00b0")) {
			position++;
			position %= 4;
			first = false;
		}
		if (e.getActionCommand().equals("Rotate left 90\u00b0")) {
			position = position + 3;
			position %= 4;
			first = false;
		}
		if (e.getActionCommand().equals("Flip vertically")) {
			if (position == 0 || position == 2) {
				if (fv_0_2 == false) {
					fv_0_2 = true;
				} else {
					fv_0_2 = false;
				}
			}
			if (position == 1 || position == 3) {
				if (fv_1_3 == false) {
					fv_1_3 = true;
				} else {
					fv_1_3 = false;
				}
			}
		}
		if (e.getActionCommand().equals("Flip horizontally")) {
			if (position == 0 || position == 2) {
				if (fh_0_2 == false) {
					fh_0_2 = true;
				} else {
					fh_0_2 = false;
				}
			}
			if (position == 1 || position == 3) {
				if (fh_1_3 == false) {
					fh_1_3 = true;
				} else {
					fh_1_3 = false;
				}
			}
		}
	}

	public int getDefaultConnector() {
		return TGComponentManager.ELN_CONNECTOR;
	}

	public double getZ0() {
		return z0;
	}

	public void setZ0(double _z0) {
		z0 = _z0;
	}

	public double getDelta0() {
		return delta0;
	}

	public void setDelta0(double _delta0) {
		delta0 = _delta0;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String _delay) {
		delay = _delay;
	}

	public String getUnit0() {
		return unit0;
	}

	public void setUnit0(String _unit0) {
		unit0 = _unit0;
	}

	public String getUnit2() {
		return unit2;
	}

	public void setUnit2(String _unit2) {
		unit2 = _unit2;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int _position) {
		position = _position;
	}

	public boolean isFv_0_2() {
		return fv_0_2;
	}

	public void setFv_0_2(boolean _fv_0_2) {
		fv_0_2 = _fv_0_2;
	}

	public boolean isFv_1_3() {
		return fv_1_3;
	}

	public void setFv_1_3(boolean _fv_1_3) {
		fv_1_3 = _fv_1_3;
	}

	public boolean isFh_0_2() {
		return fh_0_2;
	}

	public void setFh_0_2(boolean _fh_0_2) {
		fh_0_2 = _fh_0_2;
	}

	public boolean isFh_1_3() {
		return fh_1_3;
	}

	public void setFh_1_3(boolean _fh_1_3) {
		fh_1_3 = _fh_1_3;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean _first) {
		first = _first;
	}

	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		return tgc instanceof ELNPortTerminal;
	}

	public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		if (tgc instanceof ELNPortTerminal) {
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
		for (int i = 0; i < nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ELNPortTerminal) {
				tgcomponent[i].resizeWithFather();
			}
		}
	}
	
	public void resizeWithFather() {
		if ((father != null) && (father instanceof ELNModule)) {
			resizeToFatherSize();

			setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
			setMoveCd(x, y);
		}
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
}
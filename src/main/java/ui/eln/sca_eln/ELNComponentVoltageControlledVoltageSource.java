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
import ui.window.JDialogELNComponentVoltageControlledVoltageSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class ELNComponentVoltageControlledVoltageSource 
 * Voltage controlled voltage source to be used in ELN diagrams 
 * Creation: 13/06/2018
 * @version 1.0 13/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNComponentVoltageControlledVoltageSource extends TGCScalableWithInternalComponent implements ActionListener, SwallowTGComponent, ELNComponent {
	protected Color myColor;
	protected int orientation;
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;

	private int textX = 15;
	private double dtextX = 0.0;
	protected int decPoint = 3;

	private double val;

	private int position = 0;
	private boolean fv_0_2 = false, fv_1_3 = false, fh_0_2 = false, fh_1_3 = false;
	private int old;
	private boolean first, f = true;

	private ELNPortTerminal term0;
	private ELNPortTerminal term1;
	private ELNPortTerminal term2;
	private ELNPortTerminal term3;

	public ELNComponentVoltageControlledVoltageSource(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
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
		value = tdp.findELNComponentName("VCVS");

		setVal(1.0);
	}

	public Color getMyColor() {
		return myColor;
	}

	public void internalDrawing(Graphics g) {
		if (f == true) {
			term0 = new ELNPortTerminal(x, y, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term0.setValue("ncp");
			getTDiagramPanel().getComponentList().add(term0);
			term0.getTDiagramPanel().addComponent(term0, x, y, true, false);

			term1 = new ELNPortTerminal(x + width - width / 12, y, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term1.setValue("np");
			getTDiagramPanel().getComponentList().add(term1);
			term1.getTDiagramPanel().addComponent(term1, x + width - width / 12, y, true, false);

			term2 = new ELNPortTerminal(x, y + height - height / 8, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term2.setValue("ncn");
			getTDiagramPanel().getComponentList().add(term2);
			term2.getTDiagramPanel().addComponent(term2, x, y + height - height / 8, true, false);

			term3 = new ELNPortTerminal(x + width - width / 12, y + height - height / 8, this.minX, this.maxX, this.minY, this.maxY, false, this.father, this.tdp);
			term3.setValue("nn");
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

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("ncp");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("np");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("ncn");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("nn");
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
				rotateTop(g);
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
				rotateBottomFlip(g);
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
				rotateTopFlip(g);
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
				rotateBottom(g);
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

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("ncp");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("np");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("ncn");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("nn");
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
				rotateRight(g);
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
				rotateRightFlip(g);
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
				rotateLeftFlip(g);
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
				rotateLeft(g);
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

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("ncp");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("np");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("ncn");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("nn");
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
				rotateBottom(g);
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
				rotateTopFlip(g);
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
				rotateBottomFlip(g);
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
				rotateTop(g);
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

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("ncp");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("np");
			int sh1 = g.getFontMetrics().getAscent();
			int sw2 = g.getFontMetrics().stringWidth("ncn");
			int sh2 = g.getFontMetrics().getAscent();
			int sw3 = g.getFontMetrics().stringWidth("nn");
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
				rotateLeft(g);
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
				rotateLeftFlip(g);
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
				rotateRightFlip(g);
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
				rotateRight(g);
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

	private void rotateTop(Graphics g) {
		int[] ptx0 = { x, x + 2 * width / 6 };
		int[] pty0 = { y + height / 16, y + height / 16 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x, x + 2 * width / 6 };
		int[] pty1 = { y + height - height / 16, y + height - height / 16 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width, x + 4 * width / 6, x + 4 * width / 6, x + 3 * width / 6, x + 4 * width / 6,
				x + 4 * width / 6, x + 5 * width / 6, x + 4 * width / 6, x + 4 * width / 6, x + width,
				x + 4 * width / 6, x + 4 * width / 6 };
		int[] pty2 = { y + height / 16, y + height / 16, y + height / 4, y + 2 * height / 4, y + 3 * height / 4,
				y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height - height / 16,
				y + height - height / 16, y + height - height / 16, y + height / 16 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height / 4, y + height / 4, y + height / 4 - height / 16, y + height / 4 + height / 16,
				y + height / 4, y + height / 4 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + height / 4, y + height / 4, y + height / 4 - height / 16, y + height / 4 + height / 16,
				y + height / 4, y + height / 4 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty6 = { y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateTopFlip(Graphics g) {
		int[] ptx0 = { x, x + 2 * width / 6 };
		int[] pty0 = { y + height / 16, y + height / 16 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x, x + 2 * width / 6 };
		int[] pty1 = { y + height - height / 16, y + height - height / 16 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width, x + 4 * width / 6, x + 4 * width / 6, x + 3 * width / 6, x + 4 * width / 6,
				x + 4 * width / 6, x + 5 * width / 6, x + 4 * width / 6, x + 4 * width / 6, x + width,
				x + 4 * width / 6, x + 4 * width / 6 };
		int[] pty2 = { y + height / 16, y + height / 16, y + height / 4, y + 2 * height / 4, y + 3 * height / 4,
				y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height - height / 16,
				y + height - height / 16, y + height - height / 16, y + height / 16 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height - height / 4, y + height - height / 4, y + height - height / 4 + height / 16,
				y + height - height / 4 - height / 16, y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height / 4, y + height / 4 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + height - height / 4, y + height - height / 4, y + height - height / 4 + height / 16,
				y + height - height / 4 - height / 16, y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty6 = { y + height / 4, y + height / 4 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateBottom(Graphics g) {
		int[] ptx0 = { x + 4 * width / 6, x + width };
		int[] pty0 = { y + height / 16, y + height / 16 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + 4 * width / 6, x + width };
		int[] pty1 = { y + height - height / 16, y + height - height / 16 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x + 2 * width / 6, x + 2 * width / 6, x + width / 6, x + 2 * width / 6, x + 2 * width / 6,
				x + 3 * width / 6, x + 2 * width / 6, x + 2 * width / 6, x, x + 2 * width / 6, x + 2 * width / 6 };
		int[] pty2 = { y + height / 16, y + height / 16, y + height / 4, y + 2 * height / 4, y + 3 * height / 4,
				y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height - height / 16,
				y + height - height / 16, y + height - height / 16, y + height / 16 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height - height / 4, y + height - height / 4, y + height - height / 4 + height / 16,
				y + height - height / 4 - height / 16, y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height / 4, y + height / 4 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + height - height / 4, y + height - height / 4, y + height - height / 4 + height / 16,
				y + height - height / 4 - height / 16, y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty6 = { y + height / 4, y + height / 4 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateBottomFlip(Graphics g) {
		int[] ptx0 = { x + 4 * width / 6, x + width };
		int[] pty0 = { y + height / 16, y + height / 16 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + 4 * width / 6, x + width };
		int[] pty1 = { y + height - height / 16, y + height - height / 16 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x + 2 * width / 6, x + 2 * width / 6, x + width / 6, x + 2 * width / 6, x + 2 * width / 6,
				x + 3 * width / 6, x + 2 * width / 6, x + 2 * width / 6, x, x + 2 * width / 6, x + 2 * width / 6 };
		int[] pty2 = { y + height / 16, y + height / 16, y + height / 4, y + 2 * height / 4, y + 3 * height / 4,
				y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height - height / 16,
				y + height - height / 16, y + height - height / 16, y + height / 16 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height / 4, y + height / 4, y + height / 4 - height / 16, y + height / 4 + height / 16,
				y + height / 4, y + height / 4 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + height / 4, y + height / 4, y + height / 4 - height / 16, y + height / 4 + height / 16,
				y + height / 4, y + height / 4 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty6 = { y + height - height / 4, y + height - height / 4 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateRight(Graphics g) {
		int[] ptx0 = { x + width / 16, x + width / 16 };
		int[] pty0 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width - width / 16, x + width - width / 16 };
		int[] pty1 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width / 16, x + width / 16, x + width / 4, x + 2 * width / 4, x + 3 * width / 4,
				x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width - width / 16, x + width - width / 16,
				x + width - width / 16, x + width / 16 };
		int[] pty2 = { y + height, y + 4 * height / 6, y + 4 * height / 6, y + 3 * height / 6, y + 4 * height / 6,
				y + 4 * height / 6, y + 5 * height / 6, y + 4 * height / 6, y + 4 * height / 6, y + height,
				y + 4 * height / 6, y + 4 * height / 6 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + width - width / 4, x + width - width / 4, x + width - width / 4 + width / 16,
				x + width - width / 4 - width / 16, x + width - width / 4, x + width - width / 4 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width / 4, x + width / 4 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width - width / 4, x + width - width / 4, x + width - width / 4 + width / 16,
				x + width - width / 4 - width / 16, x + width - width / 4, x + width - width / 4 };
		int[] pty5 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + width / 4, x + width / 4 };
		int[] pty6 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateRightFlip(Graphics g) {
		int[] ptx0 = { x + width / 16, x + width / 16 };
		int[] pty0 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width - width / 16, x + width - width / 16 };
		int[] pty1 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width / 16, x + width / 16, x + width / 4, x + 2 * width / 4, x + 3 * width / 4,
				x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width - width / 16, x + width - width / 16,
				x + width - width / 16, x + width / 16 };
		int[] pty2 = { y, y + 2 * height / 6, y + 2 * height / 6, y + height / 6, y + 2 * height / 6,
				y + 2 * height / 6, y + 3 * height / 6, y + 2 * height / 6, y + 2 * height / 6, y, y + 2 * height / 6,
				y + 2 * height / 6 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + width - width / 4, x + width - width / 4, x + width - width / 4 + width / 16,
				x + width - width / 4 - width / 16, x + width - width / 4, x + width - width / 4 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width / 4, x + width / 4 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width - width / 4, x + width - width / 4, x + width - width / 4 + width / 16,
				x + width - width / 4 - width / 16, x + width - width / 4, x + width - width / 4 };
		int[] pty5 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + width / 4, x + width / 4 };
		int[] pty6 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateLeft(Graphics g) {
		int[] ptx0 = { x + width / 16, x + width / 16 };
		int[] pty0 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width - width / 16, x + width - width / 16 };
		int[] pty1 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width / 16, x + width / 16, x + width / 4, x + 2 * width / 4, x + 3 * width / 4,
				x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width - width / 16, x + width - width / 16,
				x + width - width / 16, x + width / 16 };
		int[] pty2 = { y, y + 2 * height / 6, y + 2 * height / 6, y + height / 6, y + 2 * height / 6,
				y + 2 * height / 6, y + 3 * height / 6, y + 2 * height / 6, y + 2 * height / 6, y, y + 2 * height / 6,
				y + 2 * height / 6 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + width / 4, x + width / 4, x + width / 4 - width / 16, x + width / 4 + width / 16,
				x + width / 4, x + width / 4 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width - width / 4, x + width - width / 4 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 4, x + width / 4, x + width / 4 - width / 16, x + width / 4 + width / 16,
				x + width / 4, x + width / 4 };
		int[] pty5 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + width - width / 4, x + width - width / 4 };
		int[] pty6 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	private void rotateLeftFlip(Graphics g) {
		int[] ptx0 = { x + width / 16, x + width / 16 };
		int[] pty0 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width - width / 16, x + width - width / 16 };
		int[] pty1 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width / 16, x + width / 16, x + width / 4, x + 2 * width / 4, x + 3 * width / 4,
				x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width - width / 16, x + width - width / 16,
				x + width - width / 16, x + width / 16 };
		int[] pty2 = { y + height, y + 4 * height / 6, y + 4 * height / 6, y + 3 * height / 6, y + 4 * height / 6,
				y + 4 * height / 6, y + 5 * height / 6, y + 4 * height / 6, y + 4 * height / 6, y + height,
				y + 4 * height / 6, y + 4 * height / 6 };
		g.drawPolygon(ptx2, pty2, 12);
		int[] ptx3 = { x + width / 4, x + width / 4, x + width / 4 - width / 16, x + width / 4 + width / 16,
				x + width / 4, x + width / 4 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width - width / 4, x + width - width / 4 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 4, x + width / 4, x + width / 4 - width / 16, x + width / 4 + width / 16,
				x + width / 4, x + width / 4 };
		int[] pty5 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		int[] ptx6 = { x + width - width / 4, x + width - width / 4 };
		int[] pty6 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx6, pty6, 2);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	public int getType() {
		return TGComponentManager.ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE;
	}

	public boolean editOndoubleClick(JFrame frame) {
		JDialogELNComponentVoltageControlledVoltageSource jde = new JDialogELNComponentVoltageControlledVoltageSource(
				this);
		jde.setVisible(true);
		return true;
	}

	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<attributes value=\"" + val);
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

			double value;
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
								value = Double.parseDouble(elt.getAttribute("value"));
								position = Integer.parseInt(elt.getAttribute("position"));
								width = Integer.parseInt(elt.getAttribute("width"));
								height = Integer.parseInt(elt.getAttribute("height"));
								fv_0_2 = Boolean.parseBoolean(elt.getAttribute("fv_0_2"));
								fv_1_3 = Boolean.parseBoolean(elt.getAttribute("fv_1_3"));
								fh_0_2 = Boolean.parseBoolean(elt.getAttribute("fh_0_2"));
								fh_1_3 = Boolean.parseBoolean(elt.getAttribute("fh_1_3"));
								first = Boolean.parseBoolean(elt.getAttribute("first"));
								setVal(value);
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

	public double getVal() {
		return val;
	}

	public void setVal(double _val) {
		val = _val;
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
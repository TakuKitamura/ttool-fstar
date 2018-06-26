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
import ui.eln.ELNConnectingPoint;
import ui.window.JDialogELNComponentVoltageControlledCurrentSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class ELNComponentVoltageControlledCurrentSource 
 * Voltage controlled current source to be used in ELN diagrams 
 * Creation: 13/06/2018
 * @version 1.0 13/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNComponentVoltageControlledCurrentSource extends TGCScalableWithInternalComponent
		implements ActionListener {
	protected Color myColor;
	protected int orientation;
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;

	private int textX = 15;
	private double dtextX = 0.0;
	protected int decPoint = 3;

	private double val;
	private String unit;

	private int position = 0;
	private boolean fv_0_2 = false, fv_1_3 = false, fh_0_2 = false, fh_1_3 = false;
	private int old;
	private boolean first;

	public ELNComponentVoltageControlledCurrentSource(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY,
			boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		initScaling(120, 80);

		dtextX = textX * oldScaleFactor;
		textX = (int) dtextX;
		dtextX = dtextX - textX;

		minWidth = 1;
		minHeight = 1;

		initConnectingPoint(4);

		addTGConnectingPointsComment();

		moveable = true;
		editable = true;
		removable = true;
		userResizable = false;
		value = tdp.findELNComponentName("VCCS");

		setVal(1.0);
		setUnit("S");

		old = width;
		width = height;
		height = old;
	}

	public void initConnectingPoint(int nb) {
		nbConnectingPoint = nb;
		connectingPoint = new TGConnectingPoint[nb];
		connectingPoint[0] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 0.0, "ncp");
		connectingPoint[1] = new ELNConnectingPoint(this, 0, 0, true, true, 1.0, 0.0, "np");
		connectingPoint[2] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 1.0, "ncn");
		connectingPoint[3] = new ELNConnectingPoint(this, 0, 0, true, true, 1.0, 1.0, "nn");
	}

	public Color getMyColor() {
		return myColor;
	}

	public void internalDrawing(Graphics g) {
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
		double w0 = ((ELNConnectingPoint) connectingPoint[0]).getW();
		double h0 = ((ELNConnectingPoint) connectingPoint[0]).getH();
		double w1 = ((ELNConnectingPoint) connectingPoint[1]).getW();
		double h1 = ((ELNConnectingPoint) connectingPoint[1]).getH();
		double w2 = ((ELNConnectingPoint) connectingPoint[2]).getW();
		double h2 = ((ELNConnectingPoint) connectingPoint[2]).getH();
		double w3 = ((ELNConnectingPoint) connectingPoint[3]).getW();
		double h3 = ((ELNConnectingPoint) connectingPoint[3]).getH();

		if (position == 0) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			((ELNConnectingPoint) connectingPoint[0]).setW(w0);
			((ELNConnectingPoint) connectingPoint[0]).setH(h0);
			((ELNConnectingPoint) connectingPoint[1]).setW(w1);
			((ELNConnectingPoint) connectingPoint[1]).setH(h1);
			((ELNConnectingPoint) connectingPoint[2]).setW(w2);
			((ELNConnectingPoint) connectingPoint[2]).setH(h2);
			((ELNConnectingPoint) connectingPoint[3]).setW(w3);
			((ELNConnectingPoint) connectingPoint[3]).setH(h3);

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
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - height / 8 - sw0, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - height / 8 - sw2,
						y + height + sh2);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + height / 8,
						y + height + sh3);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				rotateBottomFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - height / 8 - sw1, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - height / 8 - sw3,
						y + height + sh3);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + height / 8,
						y + height + sh2);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				rotateTopFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - height / 8 - sw2, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - height / 8 - sw0,
						y + height + sh0);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + height / 8,
						y + height + sh1);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				rotateBottom(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - height / 8 - sw3, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - height / 8 - sw1,
						y + height + sh1);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + height / 8,
						y + height + sh0);
			}
		} else if (position == 1) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			((ELNConnectingPoint) connectingPoint[0]).setW(h0);
			((ELNConnectingPoint) connectingPoint[0]).setH(w0);
			((ELNConnectingPoint) connectingPoint[1]).setW(h1);
			((ELNConnectingPoint) connectingPoint[1]).setH(w1);
			((ELNConnectingPoint) connectingPoint[2]).setW(h2);
			((ELNConnectingPoint) connectingPoint[2]).setH(w2);
			((ELNConnectingPoint) connectingPoint[3]).setW(h3);
			((ELNConnectingPoint) connectingPoint[3]).setH(w3);

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
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - width / 8 - sw2, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - width / 8 - sw3,
						y + height + sh3);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + width / 8,
						y + height + sh1);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				rotateRightFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - width / 8 - sw3, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - width / 8 - sw2,
						y + height + sh2);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + width / 8,
						y + height + sh0);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				rotateLeftFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - width / 8 - sw0, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - width / 8 - sw1,
						y + height + sh1);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + width / 8,
						y + height + sh3);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				rotateLeft(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - width / 8 - sw1, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - width / 8 - sw0,
						y + height + sh0);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + width / 8,
						y + height + sh2);
			}
		} else if (position == 2) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			((ELNConnectingPoint) connectingPoint[0]).setW(w0);
			((ELNConnectingPoint) connectingPoint[0]).setH(h0);
			((ELNConnectingPoint) connectingPoint[1]).setW(w1);
			((ELNConnectingPoint) connectingPoint[1]).setH(h1);
			((ELNConnectingPoint) connectingPoint[2]).setW(w2);
			((ELNConnectingPoint) connectingPoint[2]).setH(h2);
			((ELNConnectingPoint) connectingPoint[3]).setW(w3);
			((ELNConnectingPoint) connectingPoint[3]).setH(h3);

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
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - height / 8 - sw3, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - height / 8 - sw1,
						y + height + sh1);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + height / 8,
						y + height + sh0);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				rotateTopFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - height / 8 - sw2, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - height / 8 - sw0,
						y + height + sh0);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + height / 8,
						y + height + sh1);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				rotateBottomFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - height / 8 - sw1, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - height / 8 - sw3,
						y + height + sh3);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + height / 8,
						y + height + sh2);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				rotateTop(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - height / 8 - sw0, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + height / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - height / 8 - sw2,
						y + height + sh2);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + height / 8,
						y + height + sh3);
			}
		} else if (position == 3) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
			}

			((ELNConnectingPoint) connectingPoint[0]).setW(h0);
			((ELNConnectingPoint) connectingPoint[0]).setH(w0);
			((ELNConnectingPoint) connectingPoint[1]).setW(h1);
			((ELNConnectingPoint) connectingPoint[1]).setH(w1);
			((ELNConnectingPoint) connectingPoint[2]).setW(h2);
			((ELNConnectingPoint) connectingPoint[2]).setH(w2);
			((ELNConnectingPoint) connectingPoint[3]).setW(h3);
			((ELNConnectingPoint) connectingPoint[3]).setH(w3);

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
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - width / 8 - sw1, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - width / 8 - sw0,
						y + height + sh0);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + width / 8,
						y + height + sh2);
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				rotateLeftFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - width / 8 - sw0, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - width / 8 - sw1,
						y + height + sh1);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x + width + width / 8,
						y + height + sh3);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				rotateRightFlip(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - width / 8 - sw3, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - width / 8 - sw2,
						y + height + sh2);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + width / 8,
						y + height + sh0);
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				rotateRight(g);
				g.drawString(((ELNConnectingPoint) connectingPoint[2]).getName(), x - width / 8 - sw2, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width + width / 8, y);
				g.drawString(((ELNConnectingPoint) connectingPoint[3]).getName(), x - width / 8 - sw3,
						y + height + sh3);
				g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width + width / 8,
						y + height + sh1);
			}
		}
		g.setColor(c);
		g.setFont(fold);
	}

	private void rotateTop(Graphics g) {
		int[] ptx0 = { x, x + 2 * width / 6 };
		int[] pty0 = { y, y };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x, x + 2 * width / 6 };
		int[] pty1 = { y + height, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width, x + 4 * width / 6, x + 4 * width / 6, x + 3 * width / 6, x + 4 * width / 6,
				x + 4 * width / 6, x + width, x + 4 * width / 6, x + 4 * width / 6, x + 5 * width / 6,
				x + 3 * width / 6, x + 5 * width / 6, x + 4 * width / 6, x + 4 * width / 6 };
		int[] pty2 = { y, y, y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height, y + height, y + height,
				y + 3 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + height / 4, y };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height / 8, y + height / 8, y + height / 16, y + height / 8 + height / 16, y + height / 8,
				y + height / 8 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height - height / 8, y + height - height / 8 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 5 * width / 6 + width / 12 + width / 24, x + 5 * width / 6 + width / 12 + width / 24,
				x + 5 * width / 6 + width / 12, x + 5 * width / 6 + width / 12 + width / 24,
				x + 5 * width / 6 + width / 6, x + 5 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + height / 4, y + 3 * height / 4 - height / 8, y + 3 * height / 4 - height / 8,
				y + 3 * height / 4, y + 3 * height / 4 - height / 8, y + 3 * height / 4 - height / 8 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateTopFlip(Graphics g) {
		int[] ptx0 = { x, x + 2 * width / 6 };
		int[] pty0 = { y, y };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x, x + 2 * width / 6 };
		int[] pty1 = { y + height, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x + width, x + 4 * width / 6, x + 4 * width / 6, x + 3 * width / 6, x + 4 * width / 6,
				x + 4 * width / 6, x + width, x + 4 * width / 6, x + 4 * width / 6, x + 5 * width / 6,
				x + 3 * width / 6, x + 5 * width / 6, x + 4 * width / 6, x + 4 * width / 6 };
		int[] pty2 = { y, y, y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height, y + height, y + height,
				y + 3 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + height / 4, y };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12, x + 2 * width / 6 - width / 12,
				x + 2 * width / 6 - width / 24 };
		int[] pty3 = { y + height - height / 8, y + height - height / 8, y + height - height / 16,
				y + height - height / 8 - height / 16, y + height - height / 8, y + height - height / 8 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 2 * width / 6 - width / 12 - width / 24, x + 2 * width / 6 - width / 24 };
		int[] pty4 = { y + height / 8, y + height / 8 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 5 * width / 6 + width / 12 + width / 24, x + 5 * width / 6 + width / 12 + width / 24,
				x + 5 * width / 6 + width / 12, x + 5 * width / 6 + width / 12 + width / 24,
				x + 5 * width / 6 + width / 6, x + 5 * width / 6 + width / 12 + width / 24 };
		int[] pty5 = { y + 3 * height / 4, y + height / 4 + height / 8, y + height / 4 + height / 8, y + height / 4,
				y + height / 4 + height / 8, y + height / 4 + height / 8 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateBottom(Graphics g) {
		int[] ptx0 = { x + 4 * width / 6, x + width };
		int[] pty0 = { y, y };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + 4 * width / 6, x + width };
		int[] pty1 = { y + height, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x + 2 * width / 6, x + 2 * width / 6, x + width / 6, x + 2 * width / 6, x + 2 * width / 6, x,
				x + 2 * width / 6, x + 2 * width / 6, x + 3 * width / 6, x + width / 6, x + 3 * width / 6,
				x + 2 * width / 6, x + 2 * width / 6 };
		int[] pty2 = { y, y, y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height, y + height, y + height,
				y + 3 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + height / 4, y };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty3 = { y + height - height / 8, y + height - height / 8, y + height - height / 16,
				y + height - height / 8 - height / 16, y + height - height / 8, y + height - height / 8 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty4 = { y + height / 8, y + height / 8 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 6 - width / 12 - width / 24, x + width / 6 - width / 12 - width / 24,
				x + width / 6 - width / 12, x + width / 6 - width / 12 - width / 24, x + width / 6 - width / 6,
				x + width / 6 - width / 12 - width / 24 };
		int[] pty5 = { y + 3 * height / 4, y + height / 4 + height / 8, y + height / 4 + height / 8, y + height / 4,
				y + height / 4 + height / 8, y + height / 4 + height / 8 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateBottomFlip(Graphics g) {
		int[] ptx0 = { x + 4 * width / 6, x + width };
		int[] pty0 = { y, y };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + 4 * width / 6, x + width };
		int[] pty1 = { y + height, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x + 2 * width / 6, x + 2 * width / 6, x + width / 6, x + 2 * width / 6, x + 2 * width / 6, x,
				x + 2 * width / 6, x + 2 * width / 6, x + 3 * width / 6, x + width / 6, x + 3 * width / 6,
				x + 2 * width / 6, x + 2 * width / 6 };
		int[] pty2 = { y, y, y + height / 4, y + 2 * height / 4, y + 3 * height / 4, y + height, y + height, y + height,
				y + 3 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + 2 * height / 4, y + height / 4, y };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12, x + 4 * width / 6 + width / 12,
				x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty3 = { y + height / 8, y + height / 8, y + height / 16, y + height / 8 + height / 16, y + height / 8,
				y + height / 8 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + 4 * width / 6 + width / 24, x + 4 * width / 6 + width / 12 + width / 24 };
		int[] pty4 = { y + height - height / 8, y + height - height / 8 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 6 - width / 12 - width / 24, x + width / 6 - width / 12 - width / 24,
				x + width / 6 - width / 12, x + width / 6 - width / 12 - width / 24, x + width / 6 - width / 6,
				x + width / 6 - width / 12 - width / 24 };
		int[] pty5 = { y + height / 4, y + 3 * height / 4 - height / 8, y + 3 * height / 4 - height / 8,
				y + 3 * height / 4, y + 3 * height / 4 - height / 8, y + 3 * height / 4 - height / 8 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateRight(Graphics g) {
		int[] ptx0 = { x, x };
		int[] pty0 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width, x + width };
		int[] pty1 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x, x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width, x + width, x + width,
				x + 3 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + width / 4, x };
		int[] pty2 = { y + height, y + 4 * height / 6, y + 4 * height / 6, y + 3 * height / 6, y + 4 * height / 6,
				y + 4 * height / 6, y + height, y + 4 * height / 6, y + 4 * height / 6, y + 5 * height / 6,
				y + 3 * height / 6, y + 5 * height / 6, y + 4 * height / 6, y + 4 * height / 6 };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + width - width / 8, x + width - width / 8, x + width - width / 16,
				x + width - width / 8 - width / 16, x + width - width / 8, x + width - width / 8 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width / 8, x + width / 8 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 3 * width / 4, x + width / 4 + width / 8, x + width / 4 + width / 8, x + width / 4,
				x + width / 4 + width / 8, x + width / 4 + width / 8 };
		int[] pty5 = { y + 5 * height / 6 + height / 12 + height / 24, y + 5 * height / 6 + height / 12 + height / 24,
				y + 5 * height / 6 + height / 12, y + 5 * height / 6 + height / 12 + height / 24,
				y + 5 * height / 6 + height / 6, y + 5 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateRightFlip(Graphics g) {
		int[] ptx0 = { x, x };
		int[] pty0 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width, x + width };
		int[] pty1 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x, x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width, x + width, x + width,
				x + 3 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + width / 4, x };
		int[] pty2 = { y, y + 2 * height / 6, y + 2 * height / 6, y + height / 6, y + 2 * height / 6,
				y + 2 * height / 6, y, y + 2 * height / 6, y + 2 * height / 6, y + 3 * height / 6, y + height / 6,
				y + 3 * height / 6, y + 2 * height / 6, y + 2 * height / 6 };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + width - width / 8, x + width - width / 8, x + width - width / 16,
				x + width - width / 8 - width / 16, x + width - width / 8, x + width - width / 8 };
		int[] pty3 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width / 8, x + width / 8 };
		int[] pty4 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + 3 * width / 4, x + width / 4 + width / 8, x + width / 4 + width / 8, x + width / 4,
				x + width / 4 + width / 8, x + width / 4 + width / 8 };
		int[] pty5 = { y + height / 6 - height / 12 - height / 24, y + height / 6 - height / 12 - height / 24,
				y + height / 6 - height / 12, y + height / 6 - height / 12 - height / 24, y + height / 6 - height / 6,
				y + height / 6 - height / 12 - height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateLeft(Graphics g) {
		int[] ptx0 = { x, x };
		int[] pty0 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width, x + width };
		int[] pty1 = { y + 4 * height / 6, y + height };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x, x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width, x + width, x + width,
				x + 3 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + width / 4, x };
		int[] pty2 = { y, y + 2 * height / 6, y + 2 * height / 6, y + height / 6, y + 2 * height / 6,
				y + 2 * height / 6, y, y + 2 * height / 6, y + 2 * height / 6, y + 3 * height / 6, y + height / 6,
				y + 3 * height / 6, y + 2 * height / 6, y + 2 * height / 6 };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + width / 8, x + width / 8, x + width / 16, x + width / 8 + width / 16, x + width / 8,
				x + width / 8 };
		int[] pty3 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12, y + 4 * height / 6 + height / 12,
				y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width - width / 8, x + width - width / 8 };
		int[] pty4 = { y + 4 * height / 6 + height / 24, y + 4 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 4, x + 3 * width / 4 - width / 8, x + 3 * width / 4 - width / 8, x + 3 * width / 4,
				x + 3 * width / 4 - width / 8, x + 3 * width / 4 - width / 8 };
		int[] pty5 = { y + height / 6 - height / 12 - height / 24, y + height / 6 - height / 12 - height / 24,
				y + height / 6 - height / 12, y + height / 6 - height / 12 - height / 24, y + height / 6 - height / 6,
				y + height / 6 - height / 12 - height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	private void rotateLeftFlip(Graphics g) {
		int[] ptx0 = { x, x };
		int[] pty0 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx0, pty0, 2);
		int[] ptx1 = { x + width, x + width };
		int[] pty1 = { y, y + 2 * height / 6 };
		g.drawPolygon(ptx1, pty1, 2);
		int[] ptx2 = { x, x, x + width / 4, x + 2 * width / 4, x + 3 * width / 4, x + width, x + width, x + width,
				x + 3 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + 2 * width / 4, x + width / 4, x };
		int[] pty2 = { y + height, y + 4 * height / 6, y + 4 * height / 6, y + 3 * height / 6, y + 4 * height / 6,
				y + 4 * height / 6, y + height, y + 4 * height / 6, y + 4 * height / 6, y + 5 * height / 6,
				y + 3 * height / 6, y + 5 * height / 6, y + 4 * height / 6, y + 4 * height / 6 };
		g.drawPolygon(ptx2, pty2, 14);
		int[] ptx3 = { x + width / 8, x + width / 8, x + width / 16, x + width / 8 + width / 16, x + width / 8,
				x + width / 8 };
		int[] pty3 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12, y + 2 * height / 6 - height / 12,
				y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx3, pty3, 6);
		int[] ptx4 = { x + width - width / 8, x + width - width / 8 };
		int[] pty4 = { y + 2 * height / 6 - height / 12 - height / 24, y + 2 * height / 6 - height / 24 };
		g.drawPolygon(ptx4, pty4, 2);
		int[] ptx5 = { x + width / 4, x + 3 * width / 4 - width / 8, x + 3 * width / 4 - width / 8, x + 3 * width / 4,
				x + 3 * width / 4 - width / 8, x + 3 * width / 4 - width / 8 };
		int[] pty5 = { y + 5 * height / 6 + height / 12 + height / 24, y + 5 * height / 6 + height / 12 + height / 24,
				y + 5 * height / 6 + height / 12, y + 5 * height / 6 + height / 12 + height / 24,
				y + 5 * height / 6 + height / 6, y + 5 * height / 6 + height / 12 + height / 24 };
		g.drawPolygon(ptx5, pty5, 6);
		g.fillPolygon(ptx5, pty5, 6);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	public int getType() {
		return TGComponentManager.ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE;
	}

	public boolean editOndoubleClick(JFrame frame) {
		JDialogELNComponentVoltageControlledCurrentSource jde = new JDialogELNComponentVoltageControlledCurrentSource(
				this);
		jde.setVisible(true);
		return true;
	}

	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		for (int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch (c) {
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
		sb.append("<attributes value=\"" + val);
		sb.append("\" unit=\"" + encode(unit) + "\"");
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
			String unit;

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
								unit = elt.getAttribute("unit");
								setVal(value);
								setUnit(unit);
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

		JMenuItem rotateright = new JMenuItem("Rotate right 90°");
		rotateright.addActionListener(this);
		componentMenu.add(rotateright);

		JMenuItem rotateleft = new JMenuItem("Rotate left 90°");
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
		if (e.getActionCommand().equals("Rotate right 90°")) {
			position++;
			position %= 4;
			first = false;
		}
		if (e.getActionCommand().equals("Rotate left 90°")) {
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String _unit) {
		unit = _unit;
	}
}

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

import myutil.GraphicLib;
import ui.*;
import ui.eln.sca_eln.*;
import ui.util.IconManager;
import ui.window.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Class ELNModule
 * Module to be used in ELN diagrams 
 * Creation: 12/07/2018
 * @version 1.0 12/07/2018
 * @author Irina Kit Yan LEE
 */

public class ELNModule extends TGCScalableWithInternalComponent implements SwallowTGComponent {
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;
	protected int orientation;
	private Color myColor;

	private int textX = 15;
	private double dtextX = 0.0;
	protected int decPoint = 3;

	public String oldValue;

	public ELNModule(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		initScaling(200, 150);

		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;

		minWidth = 1;
		minHeight = 1;

		addTGConnectingPointsComment();

		moveable = true;
		multieditable = true;
		editable = true;
		removable = true;
		userResizable = true;

		value = tdp.findELNComponentName("Module_");
	}

	public void internalDrawing(Graphics g) {
		int w;
		Font f = g.getFont();
		Font fold = f;

		if (myColor == null) {
			myColor = Color.white;
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

		Color c = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(c);
		}

		int attributeFontSize = this.currentFontSize * 5 / 6;
		g.setFont(f.deriveFont((float) attributeFontSize));
		g.setFont(f);
		w = g.getFontMetrics().stringWidth(value);
		if (w > (width - 2 * textX)) {
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + textX + 1, y + currentFontSize + textX);
		} else {
			g.setFont(f.deriveFont(Font.BOLD));
			g.drawString(value, x + (width - w)/2, y + currentFontSize + textX);
		}

		g.setFont(fold);
	}

	public void rescale(double scaleFactor){
		dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
		textX = (int)(dtextX);
		dtextX = dtextX - textX;
		super.rescale(scaleFactor);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	public boolean editOndoubleClick(JFrame frame, int _x, int _y) {
		// On the name ?
		if (_y <= (y + currentFontSize + textX)) {
			oldValue = value;
			String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
					JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
					null,
					getValue());
			if ((s != null) && (s.length() > 0)) {
				if (!TAttribute.isAValidId(s, false, false)) {
					JOptionPane.showMessageDialog(frame,
							"Could not change the name of the component: the new name is not a valid name",
							"Error",
							JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				setComponentName(s);
				setValueWithChange(s);
				setValue(s);
				rescaled = true;
				return true;

			}
			return false;
		}

		JDialogELNModule jde = new JDialogELNModule(this);
		jde.setVisible(true);
		rescaled = true;
		return true;
	}

	public int getType() {
		return TGComponentManager.ELN_MODULE;
	}

	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		if (tgc instanceof ELNComponent) {
			return true;
		} 
		if (tgc instanceof ELNComponentNodeRef) {
			return true;
		}
		if (tgc instanceof ELNModuleTerminal) {
			return true;
		}
		return false;
	}

	public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		if (tgc instanceof ELNComponent) {
			tgc.setFather(this);
			tgc.setDrawingZone(true);
			tgc.resizeWithFather();
			addInternalComponent(tgc, 0);
			return true;
		}
		if (tgc instanceof ELNComponentNodeRef) {
			tgc.setFather(this);
			tgc.setDrawingZone(true);
			tgc.resizeWithFather();
			addInternalComponent(tgc, 0);
			return true;
		}
		if (tgc instanceof ELNModuleTerminal) {
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
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ELNComponent) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof ELNComponentNodeRef) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof ELNModuleTerminal) {
				tgcomponent[i].resizeWithFather();
			}
		}
	}

	public int getCurrentFontSize() {
		return currentFontSize;
	}

	public java.util.List<ELNModuleTerminal> getAllPorts() {
		java.util.List<ELNModuleTerminal> list = new ArrayList<ELNModuleTerminal>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof ELNModuleTerminal) {
				list.add((ELNModuleTerminal)(tgcomponent[i]));
			}
		}
		return list;
	}
}
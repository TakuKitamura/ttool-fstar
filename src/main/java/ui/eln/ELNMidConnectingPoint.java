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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.window.*;

import javax.swing.*;
import java.awt.*;

/**
 * Class ELNMidConnectingPoint
 * Primitive port. To be used in ELN diagrams
 * Creation: 17/07/2018
 * @version 1.0 17/07/2018
 * @author Irina Kit Yan LEE
 */

public class ELNMidConnectingPoint extends TGCScalableWithoutInternalComponent {
	private int maxFontSize = 14;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    protected int halfwidth = 5;

    private int textX = 15;
    private double dtextX = 0.0;
    protected int decPoint = 3;
    
    private int width = 10;
    private int height = 10;

    public ELNMidConnectingPoint(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(0, 0);

        dtextX = textX * oldScaleFactor;
        textX = (int)dtextX;
        dtextX = dtextX - textX;
        
        minWidth = 1;
        minHeight = 1;

        initConnectingPoint(1);
                
        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = false;
        canBeCloned = false;
       
        value = "";
    }

    public void initConnectingPoint(int nb) {
        nbConnectingPoint = nb;
        connectingPoint = new TGConnectingPoint[nb];
        int i;
        for (i=0; i<nbConnectingPoint; i++) {
            connectingPoint[i] = new ELNConnectingPoint(this, 0, 0, true, true, 0.5, 0.5);
        }
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
    	g.drawOval(x - width/2, y - height /2, width, height);
    	g.setColor(Color.BLACK);
    	g.fillOval(x - width/2, y - height /2, width, height);
    	g.setColor(c);
    	g.setFont(fold);
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x - width/2, y - height/2, width, height)) {
            return this;
        }
        return null;
    }
    
    public boolean editOndoubleClick(JFrame frame) {
		JDialogELNMidConnectingPoint jde = new JDialogELNMidConnectingPoint(this);
		jde.setVisible(true);
		return true;
	}

    protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<attributes name=\"" + getValue() + "\"");
		sb.append("/>\n");
		sb.append("</extraparam>\n");
		return new String(sb);
	}

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
		try {
			NodeList nli;
			Node n1, n2;
			Element elt;

			String name;

			for (int i = 0; i < nl.getLength(); i++) {
				n1 = nl.item(i);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					nli = n1.getChildNodes();
					for (int j = 0; j < nli.getLength(); j++) {
						n2 = nli.item(j);
						if (n2.getNodeType() == Node.ELEMENT_NODE) {
							elt = (Element) n2;
							if (elt.getTagName().equals("attributes")) {
								name = elt.getAttribute("name");
								setValue(name);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new MalformedModelingException();
		}
	}
    
    public int getDefaultConnector() {
        return TGComponentManager.ELN_CONNECTOR;
    }
    
    public void myActionWhenRemoved() {
        if (father != null) {
            if (father instanceof TGConnector) {
                TGConnector tg = (TGConnector)father;
                tg.pointHasBeenRemoved(this);
            }
        }
    }
    public int getCurrentMaxX() {
    	return getX() + getWidth();
    }

    public int getCurrentMaxY() {
    	return getY() + getHeight();
    }
}
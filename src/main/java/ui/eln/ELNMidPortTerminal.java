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
import ui.window.JDialogELNMidPortTerminal;
import java.awt.*;
import javax.swing.JFrame;

/**
 * Class ELNMidPortTerminal
 * Implements the intermediate points of connectors, when the connector is made of several lines.
 * Creation: 18/07/2018
 * @version 1.0 18/07/2003
 * @author Irina Kit Yan LEE
 */

public class ELNMidPortTerminal extends TGCPointOfConnector {
    public ELNMidPortTerminal(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        initScaling(10, 10);
        
        initConnectingPoint(1);
        
        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = false;
        removable = true;
        canBeCloned = false;
        
        value = "";
    }
    
	public void initConnectingPoint(int nb) {
		nbConnectingPoint = nb;
		connectingPoint = new TGConnectingPoint[nb];
		connectingPoint[0] = new ELNPortTerminal(this, -width/2, -height/2, true, true, 0.5, 0.5, "");
	}
    
    public void internalDrawing(Graphics g) {
    	g.drawOval(x - width/2, y - height /2, width, height);
    	g.fillOval(x - width/2, y - height /2, width, height);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x - width/2, y - height/2, width, height)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
		return TGComponentManager.ELN_MID_PORT_TERMINAL;
	}
    
    public boolean editOndoubleClick(JFrame frame) {
    	JDialogELNMidPortTerminal jde = new JDialogELNMidPortTerminal(this);
		jde.setVisible(true);
		return true;
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
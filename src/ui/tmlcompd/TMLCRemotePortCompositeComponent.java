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
 * Class TMLCRemotePortCompositeComponent
 * Port referencing the port of a composite component
 * Creation: 21/12/2003
 * @version 1.0 21/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcompd;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import myutil.*;
import ui.*;

public class TMLCRemotePortCompositeComponent extends TGCWithInternalComponent implements SwallowedTGComponent, WithAttributes {
    private TMLCCompositePort port;
	TMLCReferencePortConnectingPoint point;
	private int defaultDiag = 10;
	private Color myColor = new Color(251, 252, 200);
	
    public TMLCRemotePortCompositeComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		nbConnectingPoint = 0;
        nbInternalTGComponent = 0;
		
		moveable = false;
        editable = false;
        removable = false;
		userResizable = false;
		multieditable = false;
		
		value = "Reference Port";
		name = "Reference Port";
		
		myImageIcon = IconManager.imgic1200;
    }
	
	public void internalDrawing(Graphics g) {
		int xp, yp;
		Color pc = myColor;
		Color col = g.getColor();
		if (port != null) {
			pc = port.getPortColor();
			if (pc == null) {
				pc = myColor;
			}
		}
		if (point != null) {
			xp = point.getX() - (defaultDiag / 2);
			yp = point.getY() - (defaultDiag / 2);
			x = point.getX();
			y = point.getY() ;
			forceSize(0, 0);
			//myColor = new Color(251, 252, 155- (port.getMyDepth() * 10));
			g.setColor(pc);
			g.fillOval(xp, yp, defaultDiag+1, defaultDiag+1);
			g.setColor(col);
			g.drawOval(xp, yp, defaultDiag, defaultDiag);
		}
		
	 }
	
	public void setElements(TMLCCompositePort _port, TMLCReferencePortConnectingPoint _point) {
		port = _port;
		point = _point;
	}
	
	public TMLCCompositePort getPort() {
		return port;
	}
	
	public String getAttributes() {
		if (port != null) {
			return port.getAttributes();
		}
		return "";
	}
	
	public TGComponent isOnOnlyMe(int x1, int y1) {
        if (point == null) {
			return null;
		}
		
		int xp, yp;
		xp = point.getX() - (defaultDiag / 2);
		yp = point.getY() - (defaultDiag / 2);
        if (GraphicLib.isInRectangle(x1, y1, xp, yp, defaultDiag, defaultDiag)) {
            return this;
        }
        return null;
    }
	
	 public int getType() {
        return TGComponentManager.TMLCTD_CREMOTEPORTCOMPONENT;
    }
	 
}








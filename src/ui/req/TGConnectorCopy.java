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
 * Class TGConnectorCopy
 * Connector to be used in requirement diagram. Connects two requirements
 * Creation: 04/02/2009
 * @version 1.0 04/02/2009
 * @author Ludovic APVRILLE
 * @see
 */

package ui.req;

import java.awt.*;
//import java.awt.geom.*;
import java.util.*;

import myutil.*;

import ui.*;

public  class TGConnectorCopy extends TGConnector {
    int w, h;
    
    public TGConnectorCopy(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
        super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
        value = "<<copy>>";
    }
    
    
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2){
		 
        //g.drawLine(x1, y1, x2, y2);
        GraphicLib.dashedArrowWithLine(g, 1, 1, 0, x1, y1, x2, y2, false);
        
        // Indicate semantics
		
		Font f = g.getFont();
		Font old = f;
		if (f.getSize() != tdp.getFontSize()) {
			f = f.deriveFont((float)tdp.getFontSize());
			g.setFont(f);
		}
		
        w  = g.getFontMetrics().stringWidth(value);
        h = g.getFontMetrics().getHeight();
        g.drawString(value, (p1.getX() + p2.getX() - w) / 2, (p1.getY() + p2.getY())/2);
		g.setFont(old);
    }
    
    public TGComponent extraIsOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, (p1.getX() + p2.getX() - w) / 2, (p1.getY() + p2.getY())/2 - h, w, h)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.CONNECTOR_COPY_REQ;
    }
    
}








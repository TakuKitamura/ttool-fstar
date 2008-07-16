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
 * Class TMLCompositionOperator
 * Composition operator between TML tasks
 * To be used in class diagrams
 * Creation: 28/10/2005
 * @version 1.0 28/10/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlcd;

import java.awt.*;

import myutil.*;

import ui.*;

public abstract class TMLCompositionOperator extends TGCWithInternalComponent {
    public String oldValue;
    protected int textX = 5;
    protected int textY = 16;
    
    public TMLCompositionOperator(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 100; height = 25;
        
        nbConnectingPoint = 4;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new TGConnectingPointTMLCompositionOperator(this, 0, height/2, true, false);
        connectingPoint[1] = new TGConnectingPointTMLCompositionOperator(this, width, height/2, true, false);
        connectingPoint[2] = new TGConnectingPointTMLCompositionOperator(this, width/2, height, true, false);
        connectingPoint[3] = new TGConnectingPointTMLCompositionOperator(this, width/2, 0, true, false);
        
        addGroup(new TGConnectingPointGroup(true));
        
        moveable = true;
        editable = false;
        removable = true;
        
        actionOnAdd();
    }
    
    public void internalDrawing(Graphics g) {
        g.drawRect(x, y, width, height);
        //g.setColor(ColorManager.COMPOSITION_OPERATOR);
		g.setColor(getMyColor());
        g.fillRect(x+1, y+1, width-1, height-1);
        //g.drawImage(IconManager.img8, x + width - 20, y + 3, ColorManager.COMPOSITION_OPERATOR, null);
        ColorManager.setColor(g, getState(), 0);
        g.setFont((g.getFont()).deriveFont(Font.BOLD));
        g.drawString(value, x + textX, y + textY);
        g.setFont((g.getFont()).deriveFont(Font.PLAIN));
		
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }
    
    public abstract boolean isToggledVisible();
	public abstract Color getMyColor();
    
}








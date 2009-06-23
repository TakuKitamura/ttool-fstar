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
 * Class TMLADExecIInterval
 * Non deterministic duration operator. To be used in TML activity diagrams
 * Creation: 02/12/2005
 * @version 1.0 02/12/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.tmlad;

import java.awt.*;
import java.awt.geom.*;

import ui.*;
import myutil.*;

public class TMLADExecIInterval extends TGCWithInternalComponent implements EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    private int lineLength = 5;
    private int textX, textY;
    private int ilength = 10;
    private int lineLength1 = 2;
	
	protected int stateOfError = 0; // Not yet checked
    
    public TMLADExecIInterval(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 10;
        height = 30;
        textX = width + 5;
        textY = height/2 + 5;
        
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, + lineLength, false, true, 0.5, 1.0);
        
        nbInternalTGComponent = 1;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        TGCTimeInterval tgc = new TGCTimeInterval(x+textX, y+textY, -75, 30, textY - 10, textY + 10, true, this, _tdp);
        tgc.setValue("");
        tgc.setName("value of the time interval");
        tgcomponent[0] = tgc;
        
        moveable = true;
        editable = false;
        removable = true;
        
        name = "execIInterval";
        
        myImageIcon = IconManager.imgic214;
    }
    
    public void internalDrawing(Graphics g) {
		if (stateOfError > 0)  {
			Color c = g.getColor();
			switch(stateOfError) {
			case ErrorHighlight.OK:
				g.setColor(ColorManager.EXEC);
				break;
			default:
				g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
			}
			g.fillRect(x, y, width, height);
			g.setColor(c);
		}
        g.drawRect(x, y, width, height);
        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2,  x + (width/2) + lineLength1, y+(height-ilength)/2);
        g.drawLine(x + (width/2) - lineLength1, y+(height-ilength)/2 + ilength,  x + (width/2) + lineLength1, y+(height-ilength)/2 + ilength);
        g.drawLine(x + (width/2), y+(height-ilength)/2, x + (width/2), y+(height+ilength)/2);
    }
    
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        
        if ((int)(Line2D.ptSegDistSq(x +width/2, y- lineLength,  x+width/2, y + lineLength + height, x1, y1)) < distanceSelected) {
			return this;	
		}
        
        return null;
    }
    
    public String getMinDelayValue() {
        return ((TGCTimeInterval)(tgcomponent[0])).getMinDelay();
    }
    
    public String getMaxDelayValue() {
        return ((TGCTimeInterval)(tgcomponent[0])).getMaxDelay();
    }
    
    public void setMinValue(String val) {
        ((TGCTimeInterval)(tgcomponent[0])).setMinDelay(val);
    }
    
    public void setMaxValue(String val) {
        ((TGCTimeInterval)(tgcomponent[0])).setMaxDelay(val);
    }
    
    public int getType() {
        return TGComponentManager.TMLAD_EXECI_INTERVAL;
    }
    
    public int getDefaultConnector() {
      return TGComponentManager.CONNECTOR_TMLAD;
    }
	
	public void setStateAction(int _stateAction) {
		stateOfError = _stateAction;
	}
    
}

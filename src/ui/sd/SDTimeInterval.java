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
 * Class SDTimeInterval
 * Time interval in a scenario
 * Creation: 23/08/2006
 * @version 1.0 23/08/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui.sd;

import java.awt.*;

import myutil.*;
import ui.*;

public class SDTimeInterval extends TGCTimeInterval implements SwallowedTGComponent {
    private int textX, textY;
    private int incrementY = 3;
    private int segment = 4;
    private int wtext = 0;
    private int htext = 0;
    
    public SDTimeInterval(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        width = 10;
        height = 30;
        textX = width/2 + 5;
        textY = height/2 + 5;
        
        nbConnectingPoint = 0;
        addTGConnectingPointsCommentMiddle();
        
        nbInternalTGComponent = 0;
        tgcomponent = new TGComponent[nbInternalTGComponent];
        
        
        moveable = true;
        editable = true;
        removable = true;
        
        name = "time interval";
        makeValue();
        
        myImageIcon = IconManager.imgic512;
    }
    
    public void internalDrawing(Graphics g) {
        g.drawRect(x-width/2, y, width, height);
        //g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        //g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
         //w  = g.getFontMetrics().stringWidth(value);
        
        g.drawString(value, x + textX, y + textY);
        
        if (!tdp.isScaled()) {
            wtext = g.getFontMetrics().stringWidth(value);
            htext = g.getFontMetrics().getHeight();
            //System.out.println("wtext=" + wtext + " htext=" + htext);
        }
        
        int y1 = y + 4;
        int x1 = x + 2 - width/2;
        int width1 = width - 4;
        
        for (int i=0; i<segment; i++) {
            g.drawLine(x1, y1, x1+width1, y1+incrementY);
            y1 += incrementY;
            g.drawLine(x1+width1, y1, x1, y1+incrementY);
            y1 += incrementY;
        }
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x - width/2, y, width, height)) {
            return this;
        }
        if (GraphicLib.isInRectangle(_x, _y, x +textX, y-htext+textY+2, wtext, htext)) {
            return this;
        }
        return null;
    }
    
    public int getType() {
        return TGComponentManager.SD_TIME_INTERVAL;
    }
    
}
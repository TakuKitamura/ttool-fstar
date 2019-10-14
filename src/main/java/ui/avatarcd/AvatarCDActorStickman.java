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




package ui.avatarcd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;

/**
 * Class AvatarCDActorStickman
 * Stickman actor in a context diagram
 * Creation: 31/08/2011
 * @version 1.0 131/08/2011
 * @author Ludovic APVRILLE
 */
public class AvatarCDActorStickman extends TGCScalableOneLineText {
    /*protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;*/
    protected int w, h; //w1;
    
    public AvatarCDActorStickman(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 30;// (int)(30 * tdp.getZoom());
        height = 70; //(int)(70 * tdp.getZoom());
        oldScaleFactor = tdp.getZoom();
	
               
        nbConnectingPoint = 24;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        int i;
        for(int j=0; j<24; j = j + 12) {
            for(i=0; i<5; i++) {
                connectingPoint[i + j] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.0, ((double)(i))/4);
            }
            connectingPoint[5+j] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
            connectingPoint[6+j] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
            for(i=0; i<5; i++) {
                connectingPoint[i+7+j] = new AvatarCDConnectingPoint(this, 0, 0, true, true, 1.0, ((double)i)/4);
            }
        }
        addTGConnectingPointsComment();
        
        moveable = true;
        editable = true;
        removable = true;
        
        value = "Actor";
        name = "actor";
        
        myImageIcon = IconManager.imgic600;
    }
    
    public void internalDrawing(Graphics g) {
        w  = g.getFontMetrics().stringWidth(value);
        h = g.getFontMetrics().getHeight();
        height = height - h;
        //g.drawRoundRect(x - width/2, y, width, height, arc, arc);
        g.drawOval(x + width/4, y, width/2, width/2);
        //Body
        g.drawLine(x+width/2, y+width/2, x+width/2, y+height-width/2);
        //Arms
        g.drawLine(x, y+width/2 + 8, x+width, y+width/2 + 8);
        //Left leg
        g.drawLine(x+width/2, y+height-width/2, x, y+height);
        //right leg
        g.drawLine(x+width/2, y+height-width/2, x+width, y+height);
        //name of actor
        drawSingleString(g, value, x + width / 2 - w / 2 , y + height + h);
        height = height + h;
    }
    
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        if (GraphicLib.isInRectangle(_x, _y, x + width / 2 - w / 2, y +height - h, w, h)) {
            return this;
        }
        return null;
    }
    
    public int getMyCurrentMinX() {
        return Math.min(x + width / 2 - w / 2, x);

    }
    
    public int getMyCurrentMaxX() {
        return Math.max(x + width / 2 + w / 2, x + width);
    }
  
    public String getActorName() {
        return value;
    }
    
    
    public int getType() {
        return TGComponentManager.ACD_ACTOR_STICKMAN;
    }
}

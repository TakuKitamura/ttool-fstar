/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paritech.fr
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




package ui.sd2;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;



/**
   * Class SDPortMessage
   * Handling of port for messages
   * Creation: 09/03/2017
   * @version 1.1 09/03/2017
   * @author Ludovic APVRILLE
 */
public class SDPortForMessage extends TGCScalableWithoutInternalComponent implements SwallowedTGComponent {
    //private int lineLength = 5;
    //private int textX, textY;

    public SDPortForMessage(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

	width = (int)(15 * tdp.getZoom());
        height = (int)(15 * tdp.getZoom());
	//TraceManager.addDev("Init tgc= " + this + " minHeight=" + minHeight);
	//TraceManager.addDev("Init tgc= " + this + " maxHeight=" + maxHeight);
	oldScaleFactor = tdp.getZoom();
	
        makeTGConnectingPoints();
        //addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
	editable = true;
	removable = false;
        userResizable = false;

        value = "port";
        name = "port";

        myImageIcon = IconManager.imgic500;
    }

    
    public void internalDrawing(Graphics g) {
	//TraceManager.addDev("Internal drawing of SDProtForMessage");
	//g.drawString("Coucou", x, y

	if (state == TGState.POINTER_ON_ME){
	    int r = width/2;
	    int xOrigin = x - r/2;
	    int yOrigin = y + (height/2) - (r/2);

	    //TraceManager.addDev("xOrigin=" + xOrigin + " y=" + yOrigin);
	
	    g.fillOval(xOrigin, yOrigin, r, r);
	    g.drawRect(x-width/2, y, width, height);
	}
    }

    
    public TGComponent isOnMe(int _x, int _y) {
	if (GraphicLib.isInRectangle(_x, _y, x - width/2, y, width, height)) {
            return this;
        }
        return null;
    }


    public int getType() {
        return TGComponentManager.SDZV_PORT_MESSAGE;
    }

    private void makeTGConnectingPoints() {
	nbConnectingPoint = 1;
        connectingPoint = new TGConnectingPoint[1];
	connectingPoint[0] = new TGConnectingPointMessageSD(this, 0, 0, true, true, 0, 0.5);
    }
}

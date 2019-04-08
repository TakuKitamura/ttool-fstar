/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package ui.tmlcp;

import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

/**
 * Class TMLCPJoin
 * Join operator. All  incoming activities join in one remaining activity
 * To be used in communication patterns diagrams
 * Creation: 17/02/2014
 * @version 1.0 17/02/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLCPJoin extends TGCWithoutInternalComponent{

	// Issue #31
//	private int lineLength = 0;
	private static int instanceCounter = 0;
	private int counter = 0;
	//private int textX, textY;

	public TMLCPJoin(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		// Issue #31
		nbConnectingPoint = 6;
		connectingPoint = new TGConnectingPoint[6];
		connectingPoint[0] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.167, 0.0);
		connectingPoint[1] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.333, 0.0);
		connectingPoint[2] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.5, 0.0);
		connectingPoint[3] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.667, 0.0);
		connectingPoint[4] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.833, 0.0);
		connectingPoint[5] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 0.5, 1.0);
			
		initSize( 150, 5 );
//		width = 150;
//		height = 5;

		addTGConnectingPointsComment();

		nbInternalTGComponent = 0;

		moveable = true;
		editable = false;
		removable = true;

		instanceCounter++;
		counter = instanceCounter;
		name = "join" + Integer.toString(counter);
		
		myImageIcon = IconManager.imgic206;
	}

	@Override
	protected void internalDrawing(Graphics g) {
		g.drawRect(x, y, width, height);
		g.fillRect(x, y, width, height);
	}

	@Override
	public TGComponent isOnMe(int x1, int y1) {
		if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
			return this;
		}
		return null;
	}
	
	public String getValueGate() {
		return tgcomponent[0].getValue();	
	}
	
	@Override
	public int getType() {
		return TGComponentManager.TMLCP_JOIN;
	}
	
	@Override
	public int getDefaultConnector() {
		return TGComponentManager.CONNECTOR_TMLCP;
    }
	
    public List<TGConnectingPoint> getEnterConnectingPoints() {
    	return Arrays.asList( Arrays.copyOfRange( connectingPoint, 0, connectingPoint.length - 1 ) );
    }

    public TGConnectingPoint getExitConnectingPoint() {
    	return connectingPoint[ 5 ];
    }
}

/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class TGConnectorWithCommentConnectingPoints
 * Generic 
 * Creation: 25/05/2011
 * @version 1.0 25/05/2011
 * @author Ludovic APVRILLE
 * @see 
 */
 
package ui;
  
import java.awt.*;
//import java.awt.geom.*;
import java.util.*;

import myutil.*;


public abstract class TGConnectorWithCommentConnectionPoints extends TGConnector {
	protected TGConnectingPointGroup tg;

	public TGConnectorWithCommentConnectionPoints(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
		super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
		
		// We create a connecting point per segment i.e :
        
		nbConnectingPoint = getNbInternalPoints() + 1;
        TGCPointOfConnector [] points = new TGCPointOfConnector[nbConnectingPoint];
        getTGCPointOfConnectors(points);
		//Connecting points have cd relatives to 2 component
		connectingPoint = new TGConnectingPointTwoFathers[nbConnectingPoint];
		if (nbConnectingPoint == 1) {
			connectingPoint[0] = new TGConnectingPointCommentConnector(p1, p2, 0, 0, true, true);		
		} else {
			connectingPoint[0] = new TGConnectingPointCommentConnector(p1, points[0], 0, 0, true, true);		
			for(int i=1; i<nbInternalTGComponent; i++) {
				connectingPoint[i] = new TGConnectingPointCommentConnector(points[i-1], points[i], 0, 0, true, true);		
			}
			connectingPoint[nbInternalTGComponent] = new TGConnectingPointCommentConnector(points[nbInternalTGComponent-1], p2, 0, 0, true, true);		
		}
		
		tg = new TGConnectingPointGroup(true);
		addGroup(tg);
		
		//myImageIcon = IconManager.imgic102;
	}
	
	public void setP1(TGConnectingPoint p) {
		p1 = p;
		if (nbConnectingPoint > 0) {
			connectingPoint[0].setFather(p);
		}
	}
	
	public void setP2(TGConnectingPoint p) {
		p2 = p;
		if (nbConnectingPoint > 0) {
			((TGConnectingPointCommentConnector)(connectingPoint[getNbInternalPoints()])).setFather2(p);
		}
	}
	
	public void pointHasBeenRemoved(TGCPointOfConnector tgc) {
		TraceManager.addDev("Internal Points:" + nbInternalTGComponent);
		
		int i, index = 0;
		TGConnectingPointTwoFathers cp1, cp2;
		
        boolean found = false;
        
        // We remove the one after tgc, and we keep the previous one
        // I.e. we remove the point which father1 is tgc
        
		// Looking for the connecting point that has been removed
		for(i=0; i<nbConnectingPoint; i++) {
			if (connectingPoint[i].getFather() == tgc) {
                index = i;
                found = true;
                 break;
			}
		}
        
        TraceManager.addDev("Index of points: " + index + " found=" + found);
		
		// Remove potential connector connected to the cp to be removed
		cp1 = (TGConnectingPointTwoFathers)(connectingPoint[index]);
        TGConnector tgcon = tdp.getConnectorConnectedTo(cp1);
        
        if (tgcon == null) {
            TraceManager.addDev("No connector found");
        } else {
             TraceManager.addDev("Connector found");
        }
		//tdp.removeOneConnector(cp1);
		
		// Changing father of points.
        if (index > 0) {
            cp2 = (TGConnectingPointTwoFathers)(connectingPoint[index-1]);
            cp2.setFather2(cp1.getFather2());
            if (tgcon != null) {
                if (tgcon.getTGConnectingPointP1() == cp1) {
                    tgcon.setP1(cp2);
                } else {
                    tgcon.setP2(cp2);
                }
            }
        }
		
		// Modifying array of connecting points
		for (i = index; i<nbConnectingPoint-1; i++) {
			connectingPoint[i] = connectingPoint[i+1];
		}
		nbConnectingPoint --;		
        
	}
	
	public void pointHasBeenAdded(TGCPointOfConnector tgc, int index, int indexCon) {
		int ind = index + indexCon;
		CDElement tg1, tg2;
		nbConnectingPoint = getNbInternalPoints() + 1;
		TGConnectingPoint[] tmpPt = new TGConnectingPointTwoFathers[nbConnectingPoint];
        TGCPointOfConnector [] points = new TGCPointOfConnector[nbConnectingPoint];
        getTGCPointOfConnectors(points);
		
		for(int i=0; i<nbConnectingPoint; i++) {
			if (i < ind) {
				tmpPt[i] = connectingPoint[i]; 
				if ((i == ind - 1) && (indexCon == 1)){
					((TGConnectingPointTwoFathers)(tmpPt[i])).setFather2(points[i]);	
				}
			} else if (i == ind) {
				if (i ==0) {
					tg1 = p1;	
				} else {
					tg1 = points[i-1];
				}
				if (i <nbConnectingPoint - 1) {
					tg2 = points[i];	
				} else {
					tg2 = p2;	
				}
				tmpPt[i] = new TGConnectingPointCommentConnector(tg1, tg2, 0, 0, true, true);
				tmpPt[i].setGroup(tg);
			} else {
				tmpPt[i] = connectingPoint[i-1]; 
				if ((i == ind + 1) && (indexCon == 0)){
					tmpPt[i].setFather(points[i-1]);	
				}
			}
		}
		
		connectingPoint = tmpPt;
			
		return;	
	}
	
	/*public int getType() {
       return TGComponentManager.CONNECTOR_ASSOCIATION;
	}
	
	public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_ATTRIBUTE;
      }*/
	
	
       
}




    



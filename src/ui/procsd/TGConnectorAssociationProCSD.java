/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

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
 * Class TGConnectorAssociation
 * Association to be used in class diagram. Connects two TClasses.
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see 
 */
 
package ui.procsd;
  
import java.awt.*;
//import java.awt.geom.*;
import java.util.*;

//import myutil.*;

import ui.*;

public  class TGConnectorAssociationProCSD extends TGConnector {
	protected TGConnectingPointGroup tg;
	// Added by Solange
    boolean show=true;

	public TGConnectorAssociationProCSD(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector _listPoint) {
		super(_x, _y,  _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
		
		// We create a connecting point per segment i.e :
		nbConnectingPoint = nbInternalTGComponent + 1;
		//Connecting points have cd relatives to 2 component
		connectingPoint = new TGConnectingPointTwoFathers[nbConnectingPoint];
		if (nbConnectingPoint == 1) {
			connectingPoint[0] = new TGConnectingPointAssociationProCSD(p1, p2, 0, 0, false, true);		
		} else {
			connectingPoint[0] = new TGConnectingPointAssociationProCSD(p1, tgcomponent[0], 0, 0, false, true);		
			for(int i=1; i<nbInternalTGComponent; i++) {
				connectingPoint[i] = new TGConnectingPointAssociationProCSD(tgcomponent[i-1], tgcomponent[i], 0, 0, false, true);		
			}
			connectingPoint[nbInternalTGComponent] = new TGConnectingPointAssociationProCSD(tgcomponent[nbInternalTGComponent-1], p2, 0, 0, false, true);		
		}
		
		tg = new TGConnectingPointGroup(true);
		addGroup(tg);
		
		myImageIcon = IconManager.imgic102;
		
		//System.out.println("Connector created: from "+p1.getFather().toString()+" to "+p2.getFather().toString());
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
			((TGConnectingPointTwoFathers)(connectingPoint[nbInternalTGComponent])).setFather2(p);
		}
	}

	protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2)
	{
		if(show) g.drawLine(x1, y1, x2, y2);	
	}
	
	public void pointHasBeenRemoved(TGCPointOfConnector tgc) {
		System.out.println("Internal Points:" + nbInternalTGComponent);
		
		int i, index = 0;
		TGConnectingPointTwoFathers cp1, cp2;
		
		// looking for connecting point to be removed
		for(i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] == tgc) {
				index = i;	
			}
		}
		
		// remove potential connector connected to the cp to be removed
		cp1 = (TGConnectingPointTwoFathers)(connectingPoint[index+1]);
		tdp.removeOneConnector(cp1);
		
		// changing father of points.
		cp2 = (TGConnectingPointTwoFathers)(connectingPoint[index]);
		cp2.setFather2(cp1.getFather2());
		
		// modifying array of connecting points
		for (i = index + 1; i<nbConnectingPoint-1; i++) {
			connectingPoint[i] = connectingPoint[i+1];
		}
		nbConnectingPoint --;		
	}
	
	public void pointHasBeenAdded(TGCPointOfConnector tgc, int index, int indexCon) {
		int ind = index + indexCon;
		CDElement tg1, tg2;
		nbConnectingPoint = nbInternalTGComponent + 1;
		TGConnectingPoint[] tmpPt = new TGConnectingPointTwoFathers[nbConnectingPoint];
		
		for(int i=0; i<nbConnectingPoint; i++) {
			if (i < ind) {
				tmpPt[i] = connectingPoint[i]; 
				if ((i == ind - 1) && (indexCon == 1)){
					((TGConnectingPointTwoFathers)(tmpPt[i])).setFather2(tgcomponent[i]);	
				}
			} else if (i == ind) {
				if (i ==0) {
					tg1 = p1;	
				} else {
					tg1 = tgcomponent[i-1];
				}
				if (i <nbConnectingPoint - 1) {
					tg2 = tgcomponent[i];	
				} else {
					tg2 = p2;	
				}
				tmpPt[i] = new TGConnectingPointAssociationProCSD(tg1, tg2, 0, 0, false, true);
				tmpPt[i].setGroup(tg);
			} else {
				tmpPt[i] = connectingPoint[i-1]; 
				if ((i == ind + 1) && (indexCon == 0)){
					tmpPt[i].setFather(tgcomponent[i-1]);	
				}
			}
		}
		
		connectingPoint = tmpPt;
			
		return;	
	}
	
	public int getType() {
       return TGComponentManager.CONNECTOR_ASSOCIATION;
	}
       
}




    



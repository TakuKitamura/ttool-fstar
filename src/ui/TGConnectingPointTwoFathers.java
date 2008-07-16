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
 * Class TGConnectingPointTwoFathers
 * extends TGConnectingPoint in the sens that the point is drawn is the middle of the fathers'cd + (x, y)
 * Creation: 22/12/2003
 * @version 1.0 22/12/2003
 * @author Ludovic APVRILLE
 * @see 
 */
 
package ui;

import java.awt.*;
import myutil.*;

public class TGConnectingPointTwoFathers extends TGConnectingPoint{
	protected CDElement container2;

	public TGConnectingPointTwoFathers(CDElement _container1, CDElement _container2, int _x, int _y, boolean _in, boolean _out) {
		super(_container1, _x, _y, _in, _out);
		container2 = _container2;
	}

	public void draw(Graphics g) {
		int mx = x + (container.getX() + container2.getX())/2;
		int my = y + (container.getY() + container2.getY())/2;
		if (state == SELECTED) { 
			mx = mx - width / 2;
			my = my - height / 2;
			g.setColor(myColor);
			g.fillRect(mx, my, width, height);
			GraphicLib.doubleColorRect(g, mx, my, width, height, Color.lightGray, Color.black);
		} else {
			g.setColor(myColor);
			g.fillRect(mx - width/4, my - width/4, width/2, height/2);
			GraphicLib.doubleColorRect(g, mx - width/4, my - width/4, width/2, height/2, Color.lightGray, Color.black);
		}
	}

	public boolean isIn() {
		return in;
	}

	public boolean isOut() {
		return out;
	}

	public boolean isCloseTo(int _x, int _y) {
		int mx = x + (container.getX() + container2.getX())/2;
		int my = y + (container.getY() + container2.getY())/2;
		return GraphicLib.isInRectangle(_x, _y, mx - width /2, my - height /2, width, height);
	}

	public void setCdX(int _x) {
		x = _x;
	}

	public void setCdY(int _y) {
		y = _y;
	}

	public int getX() {
		return x + (container.getX() + container2.getX())/2;
	}

	public int getY() {
		return y + (container.getY() + container2.getY())/2;
	}

	public CDElement getFather2() {
		return container2;
	}
	
	public void setFather2(CDElement cd) {
		container2 = cd;
	}

}




    



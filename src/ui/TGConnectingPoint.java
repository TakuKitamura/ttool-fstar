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
 * Class TGConnectingPoint
 * Definition of connecting points on which connectors can be connected
 * Creation: 22/12/2003
 * @version 1.0 22/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.awt.*;

import myutil.*;

public class TGConnectingPoint implements CDElement {
    
	protected TGComponent father;
	
    //private static int ID = 0;
    
    protected int x, y; // relative cd and center of the point
    protected int state;
    protected CDElement container;
    private boolean free = true;
    
    private int id;
    
    protected TGConnectingPointGroup cpg;
    
    protected boolean in;
    protected boolean out;
    
    protected int width = 8;
    protected int height = 8;
    
    protected Color myColor;
    
    public static final int NORMAL = 0;
    public static final int SELECTED = 1;
    
    protected static final Color IN = Color.blue;
    protected static final Color OUT = Color.cyan;
    protected static final Color INOUT = Color.orange;
    protected static final Color NO = Color.white;
	
	protected int orientation;
	
	public static final int NORTH=0;
	public static final int EAST=1;
	public static final int SOUTH=2;
	public static final int WEST=3;
   
    
    public TGConnectingPoint(CDElement _container, int _x, int _y, boolean _in, boolean _out) {
        container = _container;
        x = _x;
        y = _y;
        in = _in;
        out = _out;
        
        if (in) {
            if (out) {
                myColor = INOUT;
            } else {
                myColor = IN;
            }
        } else {
            if (out) {
                myColor = OUT;
            } else {
                myColor = NO;
            }
        }
        
        id = TGComponent.getGeneralId();
        TGComponent.setGeneralId(id + 1);
    }
    
    public void draw(Graphics g) {
        int mx = getX();
        int my = getY();
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
    
    public void drawOutAndFreeAndCompatible(Graphics g, int connectorID) {
      if (isOut()&& isFree()  && isCompatibleWith(connectorID)) {
        int mx = getX();
        int my = getY();
        mx = mx - width / 2;
        my = my - height / 2;
        g.setColor(myColor);
        g.fillRect(mx, my, width, height);
        GraphicLib.doubleColorRect(g, mx, my, width, height, Color.lightGray, Color.black);
      }
    }
    
    public void setGroup(TGConnectingPointGroup _cpg) {
        cpg = _cpg;
    }

    public void removeFromGroup() {
        cpg = null;
    }
    
    public boolean isIn() {
        return in;
    }
    
    public boolean isOut() {
        return out;
    }
    
    public boolean isCloseTo(int _x, int _y) {
        int mx = getX();
        int my = getY();
        return GraphicLib.isInRectangle(_x, _y, mx - width /2, my - height /2, width, height);
    }
    
    public void setCdX(int _x) {
        x = _x;
    }
    
    public void setCdY(int _y) {
        y = _y;
    }
    
    public void setCd(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
		if (container != null) {
			return x + container.getX();
		}
		return x;
    }
    
    public int getY() {
		if (container != null) {
			return y + container.getY();
		}
		return y;
    }
    
    public int getId() {
        return id;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void forceId(int _id) {
        id = _id;
        TGComponent.setGeneralId(Math.max(TGComponent.getGeneralId(), id + 1));
    }
    
    public void forceNewId() {
        id = TGComponent.getGeneralId();
        TGComponent.setGeneralId(id + 1);
    }
    
    
    
    // return true if state _s is different from the previous one
    public boolean setState(int _s){
        boolean b = false;
        if ((_s>-1) && (_s<2)) {
            if (state != _s)
                b = true;
            state = _s;
        }
        return b;
    }
    
    public int getState() {
        return state;
    }
    
    public CDElement getFather() {
        return container;
    }
    
    public void setFather(CDElement cd) {
        container = cd;
    }
    
    public boolean isFree() {
        if (cpg == null) {
            return free;
        } else {
            return cpg.isFree();
        }
    }
    
    public void setFree(boolean b) {
        free = b;
        if (cpg != null) {
            cpg.setFree(b);
        }
    }
    
    public String getName() {
        return container.getName();
    }

    public boolean isCompatibleWith(int type) {
        return true;
    }
    
    protected String saveInXML(int num) {
        return "<TGConnectingPoint num=\"" + num + "\" id=\"" + getId() + "\" />\n";
    }
	
	public int getOrientation() {
		return orientation;
	}
	
	/*protected boolean hasFather() {
		return (father != null);
	}
	
	protected void setFather(TGComponent tgc) {
		father = tgc;
	}
	
	protected TGComponent getFather() {
		return father;
	}*/
}








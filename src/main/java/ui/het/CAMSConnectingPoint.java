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




package ui.het;

import myutil.GraphicLib;
import ui.*;
import ui.window.JDialogCAMSConnectingPoint;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.*;
import java.util.Vector;


/**
 * Class SystemCAMSConnectingPoint
 * Definition of connecting points on which attribute connectors can be connected
 * Creation: 27/06/2017
 * @version 1.0 27/06/2017
 * @author Côme Demarigny
 */
public class CAMSConnectingPoint extends TGConnectingPointWidthHeight {
    
    protected int x, y; // relative cd and center of the point
    protected int state;
    protected CDElement container;
    private boolean free = true;
    protected boolean editable= true;

    private int id;

    protected TGConnectingPointGroup cpg;

    public String type, rate, ccpName = "Connection";

    protected boolean in;
    protected boolean out;
    protected boolean hybrid;

    public JDialogCAMSConnectingPoint dialog;

    protected Color myColor;

    protected static final Color IN = Color.gray;
    protected static final Color OUT = Color.black;
    protected static final Color INOUT = Color.red;
    protected static final Color NO = Color.white;


    protected TGConnector referenceToConnector;

    public CAMSConnectingPoint (CDElement _container, int _x, int _y, boolean _in, boolean _out,boolean _hybrid, double _w, double _h) {
	super(_container, _x, _y, _in, _out, _w, _h);
	hybrid = _hybrid;
	
	//color selection
	if (_in) {
	    if (_out) {
		myColor = INOUT;
	    } else {
		myColor = IN;
	    }
	} else {
	    if (_out) {
		myColor = OUT;
            } else {
		myColor = NO;
	    }
	}	    

        id = TGComponent.getGeneralId();
        TGComponent.setGeneralId(id + 1);

    }

    // public CAMSConnectingPoint (CDElement _container, int _x, int _y, boolean _in, boolean _out, double _w, double _h) {
    // 	super(_container, _x, _y, _in, _out, _w, _h);
    // 	CAMSConnectingPoint camsco = new CAMSConnectingPoint (_container, _x, _y, _in, _out, false, _w, _h);
    // }
      
    public void draw(Graphics g) {
        int mx = getX();
        int my = getY();
	g.setColor(myColor);

	//taking into account hybrid connectors
	if(this.hybrid==false){
	    g.fillRect(mx - width, my - width, width*2, height*2);
	}else if (this.in==true){
	    System.out.println("test");
	    g.setColor(Color.white);
	    g.fillRect(mx - width, my - width, width*2, height*2);
	    g.setColor(myColor);
	    g.fillRect(mx - width, my - width, width, height*2);
	}else {
	    g.setColor(Color.white);
	    g.fillRect(mx - width, my - width, width*2, height*2);
	    g.setColor(myColor);
	    g.fillRect(mx - width, my - width, width, height*2);
	}

	GraphicLib.doubleColorRect(g, mx - width, my - width, width*2, height*2, Color.white, Color.black);
    }
    
    public boolean editOndoubleClick(JFrame frame) {
	
	if(dialog == null){
	    dialog = new JDialogCAMSConnectingPoint(frame, "Setting connector attributes", this);
	}
	dialog.setSize(350, 300);
        GraphicLib.centerOnParent(dialog);
        dialog.setVisible(true); // blocked until dialog has been closed
        
	if (!dialog.isRegularClose()) {
	    return false;
	}		
	return true;
    }

    public String getccpName() {
	return ccpName;
    }
    
    public String getPointType() {
	return type;
    }
    
    public String getRate() {
	return rate;
    }       
    
} //class

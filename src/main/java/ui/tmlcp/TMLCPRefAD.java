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

import myutil.GraphicLib;
import ui.*;
import ui.ad.TADOneLineText;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

/**
 * Class TMLCPRefAD
 * Reference to an Activity Diagram from another Activity Diagram (including the main communication pattern)
 * Creation: 17/02/2014
 * @version 1.0 17/02/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLCPRefAD extends TADOneLineText /* Issue #69 TGCOneLineText*/ {
	
	// Issue #31
//    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
//    protected int arc = 5;

    public TMLCPRefAD(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLCP(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLCP(this, 0, lineLength, false, true, 0.5, 1.0);

        initScaling( 30, 35 );
//        width = 30;
//        height = 35;
        minWidth = scale( 70 );
        textX = scale( 5 );

        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "Reference to an AD";
        name = "ActivityDiagram";

        myImageIcon = IconManager.imgic400;
    }

    @Override
    protected void internalDrawing(Graphics g) {
    	
    	// Issue #31
        final int w = checkWidth( g );//g.getFontMetrics().stringWidth(value);
//        int w1 = Math.max(minWidth, w + 2 * textX);
//        if ((w1 != width) & (!tdp.isScaled())) {
//            setCd(x + width/2 - w1/2, y);
//            width = w1;
//            //updateConnectingPoints();
//        }

        Color c = g.getColor();
        g.setColor(ColorManager.IOD_REFERENCE);
        g.drawRect(x+1, y+1, width, height);
        g.setColor(c);

        g.drawRect(x, y, width, height);

        g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);

        final int offset = scale( 15 );
        drawSingleString(g,name, x + (width - w) / 2, y + textY + offset /* 15 Issue #31 */ );
        
        // Issue #31
        final int adOffsetX = scale( 3 );
        final int adOffsetY = scale( 12 );
        drawSingleString(g,"ad", x + adOffsetX /*3*/, y + adOffsetY );
        g.drawLine(x, y + offset /*15*/, x + offset /* 15 */, y+ offset /*15 Issue #31 */);
       
        final int adBoxOffsetX = scale( 25 );
        final int adBoxOffsetY = scale( 8 );
        g.drawLine(x + adBoxOffsetX /*25*/, y, x + adBoxOffsetX /*25*/, y + adBoxOffsetY /*8*/);
        g.drawLine(x+ offset /*15*/, y+ offset /*15*/, x + adBoxOffsetX /*25*/, y + adBoxOffsetY /*8*/ );
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+(width/2), y - lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }

    public String getAction() {
        return value;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLCP_REF_CP;
    }

    @Override
    public void addActionToPopupMenu( JPopupMenu componentMenu, ActionListener menuAL, int x, int y ) {
        componentMenu.addSeparator();
        boolean b = ( (TMLCPPanel)tdp ).isTMLCPCreated( name );
        JMenuItem isCPCreated;

        if( b ) {
            isCPCreated = new JMenuItem("Open diagram");
        }
        else {
            isCPCreated = new JMenuItem("Create Activity Diagram");
        }

        isCPCreated.addActionListener( menuAL );
        componentMenu.add( isCPCreated );
    }

    @Override
    public boolean eventOnPopup( ActionEvent e ) {
        boolean b = ( (TMLCPPanel)tdp ).isTMLCPCreated( name );
        if (b) {
            ( (TMLCPPanel)tdp ).openTMLCPDiagram( name );
        }
        else {
            ( (TMLCPPanel)tdp ).createTMLCPDiagram( name );
        }
        tdp.getMouseManager().setSelection(-1, -1);
        return true;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLCP;
    }

    @Override
    public boolean editOndoubleClick( JFrame frame ) {
        String text = "Reference to AD: ";
        if( hasFather() ) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String) JOptionPane.showInputDialog(frame, text,
                                                        "Setting Name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic100, null, getName() );
        if( (s != null) && (s.length() > 0) )   {
        	if (nameUsed(s)) {
                JOptionPane.showMessageDialog(frame,
                                              "Error: the name is already in use",
                                              "Name modification",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
        	renameTab(s);
            setName(s);
            return true;
        }
        return false;
    }
}

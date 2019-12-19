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




package ui.tmlcompd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogMultiString;

import javax.swing.*;
import java.awt.*;

/**
   * Class TMLCJoin
   * Join operator. To be used in TML component task diagrams
   * Creation: 4/03/2014
   * @version 1.0 4/03/2014
   * @author Ludovic APVRILLE
 */
public class TMLCJoin extends TMLCChannelFacility implements WithAttributes {
    protected int radius = 11;
    // value Indicates the number of samples being read in each
    // input before being written in output channels

    protected boolean isChannel = false;

    public TMLCJoin(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(2*radius, 2*radius);

        nbConnectingPoint = 7;
        connectingPoint = new TGConnectingPoint[7];

        // output
        connectingPoint[0] = new TMLCPortConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        // input
        connectingPoint[1] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[2] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[3] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.25, 0.134);
        connectingPoint[4] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.25, 0.134);
        connectingPoint[5] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.25, 0.866);
        connectingPoint[6] = new TMLCPortConnectingPoint(this, 0, 0, true, false, 0.25, 0.866);


        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        editable = true;



        value = "1";
        name = "Join";

        //insides = new ArrayList<TMLCPrimitivePort>();
        //outsides = new ArrayList<TMLCPrimitivePort>();

        myImageIcon = IconManager.imgic1204;
    }
    @Override
    public void internalDrawing(Graphics g) {

	radius = width / 2;

        if (rescaled) {
            rescaled = false;
        }
        calculatePortColor();

        // Draw arrow showing the connection if necessary
        //if (outp != null ){
        //      
        //}

        // Zoom is assumed to be computed
        Color c = g.getColor();
        //g.drawRect(x, y, width, height);
        if ((width > 2) && (height > 2)) {
            g.setColor(myColor);
            g.fillOval(x, y, radius*2, radius*2);
            g.setColor(c);
        }
        g.drawOval(x, y, radius*2, radius*2);
        //GraphicLib.arrowWithLine(g, 1, 1, 5, x, y+radius, x+radius, y+radius, false);
        g.drawLine(x, y+radius, x+radius, y+radius);
        g.drawLine(x+radius/2, (int)(y+(0.134*radius)), x+radius, y+radius);
        g.drawLine(x+radius/2, (int)(y+2*radius-(0.134*radius)), x+radius, y+radius);

        //g.drawLine(x+radius, y+radius, x+2*radius, y+radius);
        //g.drawLine(x+radius, y+radius, x+3*radius/2, (int)(y+(0.134*radius)));
        //g.drawLine(x+radius, y+radius, x+3*radius/2, (int)(y+2*radius-(0.134*radius)));

        GraphicLib.arrowWithLine(g, 1, 1, 5, x+radius, y+radius, x+2*radius, y+radius, false);
        //GraphicLib.arrowWithLine(g, 1, 1, 5, x+radius, y+radius, x+3*radius/2, (int)(y+(0.134*radius)), false);
        //GraphicLib.arrowWithLine(g, 1, 1, 5, x+radius, y+radius, x+3*radius/2, (int)(y+2*radius-(0.134*radius)), false);

        /*int w = g.getFontMetrics().stringWidth(value);
          int currentFontSize = g.getFont().getSize();
          g.drawString(value, x+radius-(w/2), y+radius+(currentFontSize/2));*/

    }
    
    @Override
    public boolean editOndoubleClick(JFrame frame) {
        if (isChannel) {
//            String oldValue = value;
            String[] labels = new String[1];
            labels[0] = "Nb of samples (positive int): ";
            String[] values = new String[1];
            values[0] = "" + value;
            String[] keywords = new String[1];
            keywords[0] = "join" ;


            JDialogMultiString jdms = new JDialogMultiString(getTDiagramPanel().getMainGUI().getFrame(), "Join attribute", 1,
                    labels, values, keywords, getTDiagramPanel().getMainGUI().getHelpManager(), getTDiagramPanel().getMainGUI());
            GraphicLib.centerOnParent(jdms, 400, 250);
            jdms.setVisible(true);

            if (jdms.hasBeenSet()) {
                String tmp = jdms.getString(0).trim();
                try {
                    int val = Integer.decode(tmp);
                    if (val > 0) {
                        value = tmp;
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Could not change the number of samples: the number must be >0",
                                "Error",
                                JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not change the number of samples: " + tmp + " is not a number",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                return true;
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Only correctly connected join can be configured",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return true;

    }



    
    @Override
    public int getType() {
        return TGComponentManager.TMLCTD_JOIN;
    }

    public int getNumberOfSamples() {
        try {
            return Integer.decode(value).intValue();
        } catch (Exception e) {
            value = "1";
            return 1;
        }
    }

    public void setAsChannel(boolean b) {
        isChannel = b;
    }






}

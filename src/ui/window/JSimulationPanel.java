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
 * Class JSimulationPanel
 * Panel for displaying simulation traces
 * Creation: 14/12/2003
 * @version 1.0 14/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import translator.*;
import ui.*;
import myutil.*;

public class JSimulationPanel extends JPanel implements JSimulationPanelInterface, MouseListener, MouseMotionListener {
    private JFrameSimulationTrace jfst;
    private Vector trace;
    private int dimx = 500;
    private int dimy = 1000;
    private int spacex = 100;
    private int spacey = 25;
    private int endx = 25;
    private int size;
    private int axisx = 700, axisy;
    private int maxTime;
    private int actiony = 15;
    private int unity = 5;
    
    private int marginx = 5;
    //private int marginy = 5;
    private int texty = 20;
    
    private boolean initCd = false;
    
    private boolean drawTime = false;
    private int drawTimeX = 0;
    private String drawTimeTime = "";
    
    // Constructor
    public JSimulationPanel(JFrameSimulationTrace _jfst, Vector _trace) {
        setBackground(ColorManager.DIAGRAM_BACKGROUND);
        
        trace = _trace;
        size = trace.size();
        
        maxTime = calculateMaxTime();
        
        addMouseListener(this);
        addMouseMotionListener(this);
        
        jfst = _jfst;
    }
    
    private void initCd(Graphics g) {
        initCd = true;
        
        GateSimulationTrace gst;
        GroupOfGates gog;
        int space = 0;
        
        for(int i=0; i<trace.size(); i++) {
            gst = (GateSimulationTrace)(trace.elementAt(i));
            gog = gst.getGroupOfGates();
            space = Math.max(space, g.getFontMetrics().stringWidth(gog.printAll()));
        }
        
        spacex = space + 25;
        
        initCd();
    }
    
    private void initCd() {
        
        dimx = spacex + axisx + endx;
        dimy = (size + 3) * spacey;
        
        axisy = dimy - spacey;
        
        setPreferredSize(new Dimension(dimx, dimy));
    }
    
    public void zoomIn() {
        if (axisx < 10000) {
            axisx = axisx * 2;
            initCd();
            revalidate();
        }
    }
    
    public void zoomOut() {
        if (axisx > 500) {
            axisx = axisx / 2;
            initCd();
            revalidate();
        }
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!initCd) {
            initCd(g);
            revalidate();
        }
        makeUnits(g);
        drawAxis(g);
        if (drawTime) {
            drawTime(g);
        }
        drawActions(g); 
    }
    
    private void drawAxis(Graphics g) {
        g.setColor(ColorManager.NORMAL_0);
        g.drawLine(spacex, spacey, spacex, axisy);
        g.drawLine(spacex, axisy, axisx + spacex + marginx, axisy);
    }
    
    private void makeUnits(Graphics g) {
        g.setColor(ColorManager.NORMAL_0);
        
        int timeSpace = calculateUnitTimeSpace();
        int x;
        int w;
        int i = 1;
        String s;
        
        x = timeToX(timeSpace * i);
        
        while (x < (axisx + spacex)) {
            s = String.valueOf((int)(timeSpace * i));
            w = g.getFontMetrics().stringWidth(s);
            g.drawString(s, x - w/2, axisy + texty);
            g.drawLine(x, axisy + unity, x, axisy - unity);
            i++;
            x = timeToX(timeSpace * i);
            if (x<0) {
                ErrorGUI.exit(ErrorGUI.ERROR_UNIT);
            }
        }
    }
    
    private void drawActions(Graphics g) {
        GateSimulationTrace gst;
        GroupOfGates gog;
        String s;
        int w;
        int y;
        int x;
        
        for(int i=0; i<trace.size(); i++) {
            y = axisy - (spacey * (i + 1));
            g.setColor(ColorManager.NORMAL_0);
            gst = (GateSimulationTrace)(trace.elementAt(i));
            gog = gst.getGroupOfGates();
            s = gog.printAll();
            w = g.getFontMetrics().stringWidth(s);
            
            g.drawString(s, spacex - w - marginx, y);
            
            GraphicLib.setMediumStroke(g);
            g.setColor(ColorManager.POINTER_ON_ME_0);
            
            for(int j=0; j<gst.size(); j++){
                x = timeToX(gst.getTime(j));
                g.drawLine(x, y, x, y - actiony);
            }
            
            g.setColor(ColorManager.NORMAL_0);
            GraphicLib.setNormalStroke(g);
            g.drawLine(spacex, y, axisx + spacex, y);
            
        }
    }
    
    private void drawTime(Graphics g) {
        int w  = g.getFontMetrics().stringWidth(drawTimeTime);
        g.setColor(ColorManager.SELECTED_0);
        g.drawLine(drawTimeX, spacey, drawTimeX, axisy);
        g.drawString(drawTimeTime, drawTimeX - w/2, spacey-1);
    }
    
    private int calculateMaxTime() {
        GateSimulationTrace gst;
        int cpt = 0;
        
        for(int i=0; i<trace.size(); i++) {
            gst = (GateSimulationTrace)(trace.elementAt(i));
            cpt = Math.max(gst.getMaxTime(), cpt);
        }
        
        return cpt + 5;
    }
    
    private int timeToX(int time) {
        return (int)Math.ceil((spacex + (double)(((double)axisx * (double)time) / (double)maxTime)));
    }
    
    private int xToTime(int x) {
        return (int)(((double)maxTime * (x - spacex)) / (double)axisx);
    }
    
    private int calculateUnitTimeSpace() {
        int tmp = maxTime / 20;
        int tmp1 = tmp;
        int k = 0;
        int indice = 0;
        
        while (tmp1 > 10) {
            tmp1 = tmp1 / 10;
            k ++;
        }
        
        if (tmp1 < 5) {
            indice = 5;
        } else {
            indice = 10;
        }
        
        while(k > 0) {
            indice = indice * 10;
            k --;
        }
        
        return indice;
    }
    
    public void mousePressed(MouseEvent e) {
        
    }
    
    public void mouseReleased(MouseEvent e) {
        
    }
    
    public void mouseEntered(MouseEvent e) {
        
    }
    
    public void mouseExited(MouseEvent e) {
        
    }
    
    public void mouseClicked(MouseEvent e) {
        int ex = e.getX();
        int ey = e.getY();
        
        
        if ((ex < spacex) || (ex > spacex + axisx)) {
            return;
        }
        
        if ((ey < spacey) || (ey > spacey + axisy)) {
            return;
        }
        
        GateSimulationTrace gst;
        
        int y;
        int x;
        
        int dist = 100;
        int distTmp;
        
        Gate g = null;
        GroupOfGates gog = null;
        int action = -1;
        int time = -1;
        String values = "";
        
        // Has the click occured close to an action ?
        for(int i=0; i<trace.size(); i++) {
            y = axisy - (spacey * (i + 1));
            gst = (GateSimulationTrace)(trace.elementAt(i));
            
            for(int j=0; j<gst.size(); j++){
                x = timeToX(gst.getTime(j));
                distTmp = (int)(Point2D.distanceSq((double)x, (double)y, (double)ex, (double)ey));
                if (distTmp < dist) {
                    dist = distTmp;
                    time = gst.getTime(j);
                    action = gst.getAction(j);
                    values = gst.getValues(j);
                    g = gst.getGate();
                    gog = gst.getGroupOfGates();
                }
            }
        }
        if (g != null) {
            jfst.setTime(time);
            jfst.setAction(action, g, gog, values);
        }
    }
    
    
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if ((x < spacex) || (x > spacex + axisx)) {
            if (drawTime == true) {
                drawTime = false;
                repaint();
            }
            //jfst.reinitTime();
            return;
        }
        
        if ((y < spacey) || (y > spacey + axisy)) {
            if (drawTime == true) {
                drawTime = false;
                repaint();
            }
            //drawTime = false;
            //jfst.reinitTime();
            return;
        }
        
        drawTime = true;
        drawTimeX = x;
        drawTimeTime = String.valueOf(xToTime(x));
        //jfst.setTime(xToTime(x));
        repaint();
        
    }
    
    public void mouseDragged(MouseEvent e) {
    }
    
    
    
}
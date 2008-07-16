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
 * Class JbirdPanel
 * Panel for displaying bird'eyes view of panels
 * Creation: 21/04/2005
 * @version 1.0 21/04/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



import ui.*;


public class JBirdPanel extends JPanel implements MouseListener, MouseMotionListener {
    private MainGUI mgui;
    private int w, h, wc, wh;
    //private Image image, scaledImage;
    //private Graphics graphics;
    private TDiagramPanel currentTdp;
    //private int startX;
    //private int startY;
    private JScrollBar horizontal, vertical;
    //private int valueH, valueV;
    private double wratio, hratio;
    private boolean go;
    //private Thread t;
    private Rectangle rect;
    private TDiagramPanel tdp;
    
    // Constructor
    public JBirdPanel(MainGUI _mgui) {
        setBackground(ColorManager.DIAGRAM_BACKGROUND);
        mgui = _mgui;
        addMouseListener(this);
        addMouseMotionListener(this);
        startProcess();
    }
    
    protected void paintComponent(Graphics g) {
        if (isShowing()) {
            super.paintComponent(g);
            tdp = mgui.getCurrentTDiagramPanel();
            if ((tdp != null) && (go == true)){
                currentTdp = tdp;
                rect = currentTdp.getVisibleRect();
                wc = getSize().width;
                wh = getSize().height;
                w = currentTdp.getWidth();
                h = currentTdp.getHeight();
                
                wratio = ((double)wc) / w;
                hratio = ((double)wh) / h;
                if (hratio < wratio) {
                    wratio = hratio;
                } else {
                    hratio = wratio;
                }
                currentTdp.paintMycomponents(g, false, wratio, hratio);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
    }
    
    
    public void centerRectangle(int x, int y) {
        if (rect != null) {
            x = (int)(x - rect.width*wratio/2);
            y = (int)(y - rect.height*hratio/2);
            if (horizontal != null) {
                horizontal.setValue((int)(x / wratio));
            }
            if (vertical != null) {
                vertical.setValue((int)(y/hratio));
            }
            
            if ((horizontal != null) || (vertical != null)) {
                repaint();
            }
        }
    }
    
    public void mousePressed(MouseEvent e) {
        //System.out.println("pressed");
        //startX = e.getX();
        //startY = e.getY();
        if ((currentTdp != null) && (currentTdp.jsp != null)) {
            //System.out.println("config ok");
            horizontal = currentTdp.jsp.getHorizontalScrollBar();
            vertical = currentTdp.jsp.getVerticalScrollBar();
            centerRectangle(e.getX(), e.getY());
            //valueH = horizontal.getValue();
            //valueV = vertical.getValue();
        } else {
            //System.out.println("config ko");
            horizontal = null;
            vertical = null;
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        
    }
    
    public void mouseEntered(MouseEvent e) {
        
    }
    
    public void mouseExited(MouseEvent e) {
        
    }
    
    public void mouseClicked(MouseEvent e) {
        
    }
    
    
    public void mouseMoved(MouseEvent e) {
        
        
    }
    
    public void mouseDragged(MouseEvent e) {
        centerRectangle(e.getX(), e.getY());
    }
    
    public void startProcess() {
        setGo(true);
    }
    
    public void setGo(boolean b) {
        go = b;
        repaint();
    }
    
    public boolean getGo() {
        return go;
    }
    
}
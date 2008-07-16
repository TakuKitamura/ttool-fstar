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
 * Class JStartingWindow
 * Creation: 11/12/2003
 * version 1.0 11/12/2003
 * @author modify by Ludovic APVRILLE, from http://www.randelshofer.ch/oop/javasplash/javasplash.html
 * @see 
 */

package ui.window;

import java.awt.*;

public class JStartingWindow extends Window {

    private Image splashImage;
    
    private String message;
    private String licence;
    private String messageMiddle;
    
    private int imgWidth, imgHeight;

    /**

     * This attribute is set to true when method
     * paint(Graphics) has been called at least once since the
     * construction of this window.

     */
    private boolean paintCalled = false;

    /**
     * Constructs a splash window and centers it on the
     * screen.
     *
     * @param owner The frame owning the splash window.
     * @param splashImage The splashImage to be displayed.
     */

    public JStartingWindow(Frame owner, Image splashImage, String message) {
        super(owner);
        this.splashImage = splashImage;
        this.message = message;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(splashImage,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie) {}

        // Center the window on the screen.
        imgWidth = splashImage.getWidth(this);
        imgHeight = splashImage.getHeight(this);  

        setSize(imgWidth, imgHeight);
        Dimension screenDim =

            Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
            (screenDim.width - imgWidth) / 2,
            (screenDim.height - imgHeight) / 2
        );
    }

    /**
     * Updates the display area of the window.
     */

    public void update(Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.

        g.setColor(getForeground());
        paint(g);
    }
    /**
     * Paints the image on the window.
     */

    public void paint(Graphics g) {
	    //int h  = g.getFontMetrics().getHeight();
	g.setFont(g.getFont().deriveFont(Font.BOLD));
        g.drawImage(splashImage, 0, 0, this);
        g.drawRect(0, 0, imgWidth - 1, imgHeight - 1);
        
        if (message != null) {
	     	g.drawString(message, 5,  imgHeight - 5);  
        }
        
         if (messageMiddle != null) {
                int w  = g.getFontMetrics().stringWidth(messageMiddle);
	     	g.drawString(messageMiddle, (imgWidth - w) / 2,  imgHeight - 30);  
        }
        
        
        if (licence != null) {
                int w  = g.getFontMetrics().stringWidth(licence);
	     	g.drawString(licence, imgWidth - w - 100 - 10, imgHeight - 5);  
        }

        // Notify method splash that the window
        // has been painted.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    
    public void setMessage(String s) {
	 	  message = s;
	 	  repaint(); 
    }
    
    public void setMiddleMessage(String s) {
	 	  messageMiddle = s;
	 	  repaint(); 
    }
    
     public void setLicenceMessage(String s) {
	 	  licence = s;
	 	  repaint(); 
    }
    
    /**
     * Constructs and displays a SplashWindow.<p>
     * This method is useful for startup splashs.
     * Dispose the returned frame to get rid of the splash window.<p>
     *
     * @param splashImage The image to be displayed.
     * @return Returns the frame that owns the SplashWindow.
     */

    public static JStartingWindow splash(Image splashImage, String msg) {
        Frame f = new Frame();
        JStartingWindow w = new JStartingWindow(f, splashImage, msg);
        // Show the window.
        w.toFront();
        w.show();

        // Note: To make sure the user gets a chance to see the
        // splash window we wait until its paint method has been
        // called at least once by the AWT event dispatcher thread.
        if (! EventQueue.isDispatchThread()) {
            synchronized (w) {
                while (! w.paintCalled) {
                    try {

                        w.wait();

                    } catch (InterruptedException e) {}
                }
            }
        }
        return w;
    }

} // Class 

	
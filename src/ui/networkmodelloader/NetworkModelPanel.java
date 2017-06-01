/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class NetworkModel
 * Dialog for managing the loading of network models
 * Creation: 30/05/2017
 * @version 1.1 30/05/2017
 * @author Ludovic APVRILLE
 * @author Ludovic APVRILLE
 * @see
 */

package ui.networkmodelloader;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

import myutil.*;
import ui.*;



public class NetworkModelPanel extends JPanel implements MouseListener, MouseMotionListener {

    private static int ImgSizeX = 220;
    private static int ImgSizeY = 120;
    
    private static int buttonSizeX = 250;
    private static int buttonSizeY = 150;
    private static int spaceBetweenButtons = 50;
    private static int nbOfButtonsPerColumn = 2;

    private static int marginX = 20;
    private static int marginY = 20;

    private int indexOfSelected = -1;
    private boolean selectedModel = false;

    private ArrayList<NetworkModel> listOfModels;
    private ActionListener listener;

    private LoaderFacilityInterface loader;

    private JTextArea jta;
    private JScrollPane jsp;
    
    public NetworkModelPanel(LoaderFacilityInterface _loader, ArrayList<NetworkModel> _listOfModels, ActionListener _listener, JTextArea _jta) {
	loader = _loader;
	listOfModels = _listOfModels;
	listener = _listener;
	jta = _jta;
	
	//Dimension pSize = new Dimension(500, 400);
        Dimension mSize = new Dimension(400, 300);

        //setPreferredSize(pSize);
	setMinimumSize(mSize);
	setBackground(new java.awt.Color(250, 250, 250));
	setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	addMouseMotionListener(this);
	addMouseListener(this);
    }

    public void setJSP(JScrollPane _jsp) {
	jsp = _jsp;
	jsp.setViewportView(this);  
    }

    public void preparePanel() {
	//int cptColumn = 0;
	//int cptRow = 0;
	for(NetworkModel button: listOfModels) {
	    //Dimension d = new Dimension(buttonSizeX, buttonSizeY);
	    //button.setPreferredSize(d);
	    //int tmpX = cptColumn * (buttonSizeX + spaceBetweenButtons);
	    //int tmpY = cptRow * (buttonSizeY + spaceBetweenButtons);
	    //TraceManager.addDev("Adding button at x=" + tmpX + "& y=" + tmpY);
	    //button.setBounds(tmpX, tmpY, buttonSizeX, buttonSizeY);
	    /*if (button.description != null) {
		button.setToolTipText(button.description);
		}*/

	    if (button.bi != null) {
		TraceManager.addDev("Adding image");
		
		/*BufferedImage newImage = new BufferedImage(ImgSizeX, ImgSizeY, button.bi.getType());
		Graphics g = newImage.createGraphics();
		g.drawImage(button.bi, 0, 0, ImgSizeX, ImgSizeY, null);
		g.dispose();*/
		button.scaledImg = ImageManager.getScaledImage(button.bi, ImgSizeX, ImgSizeY);
	    }
	    
	    //button.setBorder(BorderFactory.createEmptyBorder());
	    //button.setContentAreaFilled(false);
	    //add(button);
	    /*cptColumn ++;
	    if (cptColumn == nbOfButtonsPerColumn) {
		cptRow ++;
		cptColumn = 0;
		}*/
	}
    }

    


    @Override
    public void paintComponent(Graphics g) {
	super.paintComponent(g);
	int cptColumn = 0;
	int cptRow = 0;	

	int index = 0;
	for(NetworkModel button: listOfModels) {
	    Color c = g.getColor();
	    int tmpX = cptColumn * (buttonSizeX + spaceBetweenButtons) + marginX;
	    int tmpY = cptRow * (buttonSizeY + spaceBetweenButtons) + marginY;
	    if (button.scaledImg != null) {
		g.drawImage(button.scaledImg, tmpX, tmpY, buttonSizeX, buttonSizeY, null);
	    } else {
		g.setColor(ColorManager.AVATAR_BLOCK);
		g.fillRect(tmpX, tmpY, buttonSizeX, buttonSizeY);
		g.setColor(c);
		GraphicLib.centerString(g, "No picture", tmpX, tmpY + buttonSizeY/2, buttonSizeX); 
	    }

	    GraphicLib.centerString(g, button.fileName, tmpX, tmpY + buttonSizeY + 15, buttonSizeX); 
	    
	   
	    
	    cptColumn ++;
	    if (cptColumn == nbOfButtonsPerColumn) {
		cptRow ++;
		cptColumn = 0;
	    }

	    button.x = tmpX;
	    button.y = tmpY;
	    button.width = buttonSizeX;
	    button.height = buttonSizeY + 15;

	    // Must draw the rectangle around
	    if (index == indexOfSelected) {
		if (selectedModel) {
		    g.setColor(ColorManager.SELECTED_ELEMENT);	
		} else {
		    g.setColor(ColorManager.POINTER_ON_ME_0);		    
		}
		Graphics2D g2 = (Graphics2D)g;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
		g2.drawRect(button.x-10, button.y-10, button.width+20, button.height+20);
		g2.setStroke(oldStroke);
		g.setColor(c);
	    }
	    
	    index ++;
	}

	Dimension currentPSize = getPreferredSize();
	Dimension pSize = new Dimension(400, Math.max(300, (cptRow+1)*(buttonSizeY +spaceBetweenButtons) + 2* marginY));
	setPreferredSize(pSize);

	if (!((currentPSize.getWidth() == pSize.getWidth()) && (currentPSize.getHeight() == pSize.getHeight()))) {
	    if (jsp != null) {
		TraceManager.addDev("repainting jsp");
		jsp.setViewportView(this);  
		//jsp.revalidate();
		//jsp.repaint();
	    }
	}
	

	//g.drawString(listOfModels.size() + " model(s) available", 20, 20);
	//g.drawRect(200, 200, 200, 200);
    }

    
    public void mouseDragged(MouseEvent e) {
        
    }

    public void mouseMoved(MouseEvent e) {
	if (!selectedModel)  {
	    int previousIndex = indexOfSelected;
	    boolean found = false;;
	    int index = 0;
	    for(NetworkModel button: listOfModels) {
		if ((e.getX() > button.x) && (e.getX() < button.x + button.width) &&  (e.getY() > button.y) && (e.getY() < button.y + button.height)) {
		    indexOfSelected = index;
		    found = true;
		    break;
		}
		index ++;
	    }
	    if (!found) {
		indexOfSelected = -1;
	    }
	    if (indexOfSelected != previousIndex) {
		if (indexOfSelected != -1) {
		    NetworkModel nm = listOfModels.get(indexOfSelected);
		    jta.append("\n--- " + nm.fileName + "---\n" + nm.description + "\n-------------\n\n");
		}
		repaint();
	    }
	}
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
	if ((indexOfSelected > -1) && (!selectedModel)) {
	    selectedModel = true;
	    repaint();
	    if (loader != null) {
		loader.load(indexOfSelected);
	    }
	}
    }

    public void reactivateSelection() {
	indexOfSelected = -1;
	selectedModel = false;
	repaint();
    }
    
    

}

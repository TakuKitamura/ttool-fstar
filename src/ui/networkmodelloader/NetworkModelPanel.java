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



public class NetworkModelPanel extends JPanel  {

    private static int ImgSizeX = 220;
    private static int ImgSizeY = 120;
    
    private static int buttonSizeX = 250;
    private static int buttonSizeY = 150;
    private static int spaceBetweenButtons = 50;
    private static int nbOfButtonsPerColumn = 2;

    private ArrayList<NetworkModel> listOfModels;
    private ActionListener listener;
    
    public NetworkModelPanel(ArrayList<NetworkModel> _listOfModels, ActionListener _listener) {
	listOfModels = _listOfModels;
	listener = _listener;
	
	//Dimension pSize = new Dimension(500, 400);
        Dimension mSize = new Dimension(400, 300);

        //setPreferredSize(pSize);
	setMinimumSize(mSize);
	setBackground(new java.awt.Color(250, 250, 250));
	setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    public void addPanelWithButtons() {
	int cptColumn = 0;
	int cptRow = 0;
	for(NetworkModel button: listOfModels) {
	    int tmpX = cptColumn * (buttonSizeX + spaceBetweenButtons);
	    int tmpY = cptRow * (buttonSizeY + spaceBetweenButtons);
	    TraceManager.addDev("Adding button at x=" + tmpX + "& y=" + tmpY);
	    button.setBounds(tmpX, tmpY, buttonSizeX, buttonSizeY);
	    if (button.description != null) {
		button.setToolTipText(button.description);
	    }

	    if (button.bi != null) {
		TraceManager.addDev("Adding image");
		
		/*BufferedImage newImage = new BufferedImage(ImgSizeX, ImgSizeY, button.bi.getType());
		Graphics g = newImage.createGraphics();
		g.drawImage(button.bi, 0, 0, ImgSizeX, ImgSizeY, null);
		g.dispose();*/
		button.setIcon(new ImageIcon(ImageManager.getScaledImage(button.bi, ImgSizeX, ImgSizeY)));
	    }
	    
	    Dimension d = new Dimension(buttonSizeX, buttonSizeY);
	    button.setPreferredSize(d);
	    //button.setBorder(BorderFactory.createEmptyBorder());
	    //button.setContentAreaFilled(false);
	    add(button);
	    cptColumn ++;
	    if (cptColumn == nbOfButtonsPerColumn) {
		cptRow ++;
		cptColumn = 0;
	    }
	}
    }

    


    @Override
    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	
	
	//g.drawString(listOfModels.size() + " model(s) available", 20, 20);
	//g.drawRect(200, 200, 200, 200);
    }
    

}

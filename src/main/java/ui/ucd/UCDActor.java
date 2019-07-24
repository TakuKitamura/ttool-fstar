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


package ui.ucd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;

/**
 * Class UCDActor
 * Action state of a sequence diagram
 * Creation: 18/02/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 18/02/2005
 */
public class UCDActor extends TGCScalableWithoutInternalComponentOneLineText {
    /*protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;*/
    protected int w, h; //w1;
    /*private int textX = 7;

    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;*/

    public UCDActor(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = (int) (30 * tdp.getZoom());
        height = (int) (70 * tdp.getZoom());
        oldScaleFactor = tdp.getZoom();
        //minWidth = 30;

        nbConnectingPoint = 24;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        int i;
        for (int j = 0; j < 24; j = j + 12) {
            for (i = 0; i < 5; i++) {
                connectingPoint[i + j] = new TGConnectingPointActorUCD(this, 0, 0, true, true, 0.0, ((double) (i)) / 4);
            }
            connectingPoint[5 + j] = new TGConnectingPointActorUCD(this, 0, 0, true, true, 0.5, 0.0);
            connectingPoint[6 + j] = new TGConnectingPointActorUCD(this, 0, 0, true, true, 0.5, 1.0);
            for (i = 0; i < 5; i++) {
                connectingPoint[i + 7 + j] = new TGConnectingPointActorUCD(this, 0, 0, true, true, 1.0, ((double) i) / 4);
            }
        }
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        super.oldScaleFactor = tdp.getZoom();
        //currentFontSize = (int) (maxFontSize*oldScaleFactor);

        value = "Actor";
        name = "actor";

        myImageIcon = IconManager.imgic600;
    }

    @Override
    public void internalDrawing(Graphics graph) {
        Font font = graph.getFont();
        this.internalDrawingAux(graph);
        graph.setFont(font);
    }

    public void internalDrawingAux(Graphics graph) {
        //Font font = graph.getFont ();
        //  String ster;

	/*if (this.rescaled && !this.tdp.isScaled ()) {
            this.rescaled = false;
            // Must set the font size...
            // Incrementally find the biggest font not greater than max_font size
            // If font is less than min_font, no text is displayed

            // This is the maximum font size possible
            int maxCurrentFontSize = Math.max (0, Math.min (this.height, (int) (this.maxFontSize*this.tdp.getZoom ())));
            font = font.deriveFont ((float) maxCurrentFontSize);

            // Try to decrease font size until we get below the minimum
            while (maxCurrentFontSize > (this.minFontSize*this.tdp.getZoom () - 1)) {
                // Compute width of name of the function
                int w0 = graph.getFontMetrics (font).stringWidth (this.value);
                // Compute width of string stereotype

                // if one of the two width is small enough use this font size
                if (w0 < this.width - (2*this.textX))
                    break;

                // Decrease font size
                maxCurrentFontSize --;
                // Scale the font
                font = font.deriveFont ((float) maxCurrentFontSize);
            }
	    }*/


        w = graph.getFontMetrics().stringWidth(value);
        h = graph.getFontMetrics().getHeight();
        height = height - h;
        //g.drawRoundRect(x - width/2, y, width, height, arc, arc);
        graph.drawOval(x + width / 4, y, width / 2, width / 2);
        //Body
        graph.drawLine(x + width / 2, y + width / 2, x + width / 2, y + height - width / 2);
        //Arms
        graph.drawLine(x, y + width / 2 + 8, x + width, y + width / 2 + 8);
        //Left leg
        graph.drawLine(x + width / 2, y + height - width / 2, x, y + height);
        //right leg
        graph.drawLine(x + width / 2, y + height - width / 2, x + width, y + height);
        //name of actor
        graph.drawString(value, x + width / 2 - w / 2, y + height + h);
        height = height + h;
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        if (GraphicLib.isInRectangle(_x, _y, x + width / 2 - w / 2, y + height - h, w, h)) {
            return this;
        }
        return null;
    }

    public int getMyCurrentMinX() {
        return Math.min(x + width / 2 - w / 2, x);

    }

    public int getMyCurrentMaxX() {
        return Math.max(x + width / 2 + w / 2, x + width);
    }

    public String getActorName() {
        return value;
    }


    public int getType() {
        return TGComponentManager.UCD_ACTOR;
    }
}

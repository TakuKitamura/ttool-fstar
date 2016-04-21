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

package ui.avatarsmd;

import java.util.LinkedList;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

import ui.TAttribute;
import ui.AvatarSignal;
import ui.BasicErrorHighlight;
import ui.TGCScalableWithoutInternalComponent;
import ui.TGComponent;
import ui.TDiagramPanel;
import ui.TGConnectingPoint;
import ui.IconManager;
import ui.ErrorHighlight;
import ui.ColorManager;
import ui.TGComponentManager;
import ui.avatarbd.AvatarBDLibraryFunction;

/**
* @version 1.0 04.18.2016
* @author Florian LUGOU
*/
public class AvatarSMDLibraryFunctionCall extends TGCScalableWithoutInternalComponent implements BasicErrorHighlight {
    private LinkedList<TAttribute> parameters;
    private LinkedList<AvatarSignal> signals;
    private LinkedList<TAttribute> returnAttributes;

    private AvatarBDLibraryFunction libraryFunction;

    protected int lineLength = 5;
    protected int textX =  5;
    protected int textY =  15;
    protected int arc = 5;
    protected int linebreak = 10;

    protected int stateOfError = 0; // Not yet checked

    public AvatarSMDLibraryFunctionCall (int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        this.width = 200;
        this.height = 20;
        this.minWidth = 30;

        this.nbConnectingPoint = 2;
        this.connectingPoint = new TGConnectingPoint[2];
        this.connectingPoint[0] = new AvatarSMDConnectingPoint (this, 0, -lineLength, true, false, 0.5, 0.0);
        this.connectingPoint[1] = new AvatarSMDConnectingPoint (this, 0, lineLength, false, true, 0.5, 1.0);

        this.addTGConnectingPointsComment();

        this.moveable = true;
        this.editable = true;
        this.removable = true;
        this.userResizable = true;

        this.name = "Library function call";
        this.value = "";

        // TODO: change that
        this.myImageIcon = IconManager.imgic904;
    }

    public void internalDrawing(Graphics graph) {
        Font font = graph.getFont ();

        this.value = this.prettyPrint ();

        int stringWidth = graph.getFontMetrics().stringWidth (this.value);
        int elementWidth = Math.max (this.minWidth, stringWidth + 2 * this.textX);
        if (elementWidth != this.width && !this.tdp.isScaled()) {
            this.setCd (x + this.width/2 - elementWidth/2, y);
            this.width = elementWidth;            //updateConnectingPoints();
        }

        Color c = graph.getColor();
        if (this.stateOfError > 0)  {
            switch(stateOfError) {
                case ErrorHighlight.OK:
                    graph.setColor (ColorManager.AVATAR_LIBRARY_FUNCTION_CALL);
                    break;
                default:
                    graph.setColor (ColorManager.UNKNOWN_BOX_ACTION);
            }

            // Making the polygon
            int [] px1 = {this.x, this.x+this.linebreak, this.x+this.width-this.linebreak, this.x+this.width, this.x+this.width-this.linebreak, this.x+this.linebreak};
            int [] py1 = {this.y+this.height/2, this.y, this.y, this.y+this.height/2, this.y+this.height, this.y+this.height};
            graph.fillPolygon(px1, py1, 6);
            graph.setColor(c);
        }

        graph.drawLine(this.x+this.linebreak, this.y, this.x+this.width-this.linebreak, this.y);
        graph.drawLine(this.x+this.linebreak, this.y+this.height, this.x+this.width-this.linebreak, this.y+this.height);
        graph.drawLine(this.x+this.linebreak, this.y, this.x, this.y+this.height/2);
        graph.drawLine(this.x, this.y+this.height/2, this.x+this.linebreak, this.y+this.height);
        graph.drawLine(this.x+this.width-this.linebreak, this.y, this.x+this.width, this.y+this.height/2);
        graph.drawLine(this.x+this.width-this.linebreak, this.y+this.height, this.x+this.width, this.y+this.height/2);

        graph.drawLine (this.x+this.width/2, this.y, this.x+this.width/2, this.y - this.lineLength);
        graph.drawLine (this.x+this.width/2, this.y+this.height, this.x+this.width/2, this.y + this.lineLength + this.height);

        graph.drawString (this.value, this.x + (this.width - stringWidth) / 2 , this.y + this.textY);
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (_x < this.x || _x > this.x + this.width || _y > this.y + this.height || _y < this.y)
            return null;

        if (_x < this.x + this.linebreak) {
            int x0 = _x - this.x;
            int y0 = _y - this.y - this.height/2;
            if (y0 >= - this.height/(2*this.linebreak)*this.x && y0 <= this.height/(2*this.linebreak)*this.x)
                return this;
            return null;
        }

        if (_x > this.x + this.width - this.linebreak) {
            int x0 = _x - this.x - this.width + this.linebreak;
            int y0 = _y - this.y - this.height/2;
            if (y0 >= - this.height/2 + this.height/(2*this.linebreak)*x0 && y0 <= this.height/2 - this.height/(2*this.linebreak)*x0)
                return this;
            return null;
        }

        return this;
    }

    /*

    public String getSignalName() {
        if (value == null) {
            return null;
        }

        if (value.length() == 0) {
            return "";
        }

        int index = value.indexOf('(');
        if (index == -1) {
            return value;
        }
        return value.substring(0, index).trim();
    }

    // Return -1 in case of error
    public int getNbOfValues() {
        return AvatarSignal.getNbOfValues(value);
    }

    // Return null in case of error
    public String getValue(int _index) {
        return AvatarSignal.getValue(value, _index);
    }

    public boolean editOndoubleClick(JFrame frame) {
        Vector signals = tdp.getMGUI().getAllSignals();
        //TraceManager.addDev("Nb of signals:" + signals.size());

        JDialogAvatarSignal jdas = new JDialogAvatarSignal(frame, "Setting send signal",  value, signals, true);
        jdas.setSize(350, 300);
        GraphicLib.centerOnParent(jdas);
        jdas.show(); // blocked until dialog has been closed

        if (jdas.hasBeenCancelled()) {
            return false;
        }

        String val = jdas.getSignal();

        if (val.indexOf('(') == -1) {
            val += "()";
        }

        // valid signal?
        if (AvatarSignal.isAValidUseSignal(val)) {
            value = val;
            return true;
        }

        JOptionPane.showMessageDialog(frame,
                                      "Could not change the setting of the signal: invalid declaration",
                                      "Error",
                                      JOptionPane.INFORMATION_MESSAGE);
        return false;

    }
    */

    public String prettyPrint () {
        if (this.libraryFunction == null)
            return "";

        StringBuilder builder = new StringBuilder ();
        boolean first = true;

        if (!this.returnAttributes.isEmpty ()) {
            for (TAttribute attr: this.returnAttributes) {
                if (first)
                    first = false;
                else
                    builder.append (", ");

                builder.append (attr.getId ());
            }

            builder.append (" = ");
        }

        builder.append (this.libraryFunction.getFunctionName ());
        builder.append (" (");

        first = true;
        for (TAttribute attr: this.parameters) {
            if (first)
                first = false;
            else
                builder.append (", ");

            builder.append (attr.getId ());
        }

        builder.append (")");

        return builder.toString ();
    }

    public int getDefaultConnector() {
        return TGComponentManager.AVATARSMD_CONNECTOR;
    }

    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }
}

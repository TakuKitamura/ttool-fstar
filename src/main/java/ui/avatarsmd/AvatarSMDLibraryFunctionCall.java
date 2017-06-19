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

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.avatarbd.AvatarBDLibraryFunction;
import ui.util.IconManager;
import ui.window.JDialogSMDLibraryFunctionCall;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;


/**
* @version 1.0 04.18.2016
* @author Florian LUGOU
*/
public class AvatarSMDLibraryFunctionCall extends TGCScalableWithoutInternalComponent implements BasicErrorHighlight {
    private LinkedList<TAttribute> parameters;
    private LinkedList<AvatarSignal> signals;
    private LinkedList<TAttribute> returnAttributes;

    private AvatarBDLibraryFunction libraryFunction;

    private static int lineLength = 5;
    private static int paddingHorizontal =  5;
    private static int paddingVertical =  3;
    private static int linebreak = 10;

    private int stateOfError = 0; // Not yet checked

    public AvatarSMDLibraryFunctionCall (int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        this.width = 100;
        this.height = 25;
        this.minWidth = 30;

        this.nbConnectingPoint = 2;
        this.connectingPoint = new TGConnectingPoint[2];
        this.connectingPoint[0] = new AvatarSMDConnectingPoint (this, 0, -AvatarSMDLibraryFunctionCall.lineLength, true, false, 0.5, 0.0);
        this.connectingPoint[1] = new AvatarSMDConnectingPoint (this, 0, AvatarSMDLibraryFunctionCall.lineLength, false, true, 0.5, 1.0);

        this.addTGConnectingPointsComment();

        this.moveable = true;
        this.editable = true;
        this.removable = true;
        this.userResizable = true;

        this.name = "Library function call";
        this.value = "";

        this.libraryFunction = null;
        this.parameters = new LinkedList<TAttribute> ();
        this.signals = new LinkedList<AvatarSignal> ();
        this.returnAttributes = new LinkedList<TAttribute> ();

        // TODO: change that
        this.myImageIcon = IconManager.imgic904;
    }

    public void internalDrawing(Graphics graph) {

        this.value = this.prettyPrint ();

        int [] px1 = {this.x, this.x+this.width-AvatarSMDLibraryFunctionCall.linebreak, this.x+this.width, this.x+this.width-AvatarSMDLibraryFunctionCall.linebreak, this.x, this.x+AvatarSMDLibraryFunctionCall.linebreak};
        int [] py1 = {this.y, this.y, this.y+this.height/2, this.y+this.height, this.y+this.height, this.y+this.height/2};

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
            graph.fillPolygon(px1, py1, 6);
            graph.setColor(c);
        }

        graph.drawPolygon (px1, py1, 6);

        graph.drawLine (this.x+this.width/2, this.y, this.x+this.width/2, this.y - AvatarSMDLibraryFunctionCall.lineLength);
        graph.drawLine (this.x+this.width/2, this.y+this.height, this.x+this.width/2, this.y + AvatarSMDLibraryFunctionCall.lineLength + this.height);

        int h = graph.getFontMetrics ().getAscent () + graph.getFontMetrics ().getDescent ();
        if (h + 2*AvatarSMDLibraryFunctionCall.paddingVertical >= this.height)
            return;

        int stringWidth = this.tdp.stringWidth (graph, this.value);
        if (stringWidth + 2*AvatarSMDLibraryFunctionCall.paddingHorizontal >= this.width-2*linebreak) {
            for (int stringLength = this.value.length ()-1; stringLength >= 0; stringLength--) {
                String abbrev = this.value.substring (0, stringLength) + "...";
                int w = this.tdp.stringWidth (graph, abbrev);
                if (w + 2*AvatarSMDLibraryFunctionCall.paddingHorizontal < this.width-2*linebreak) {
                    graph.drawString (abbrev, this.x + (this.width - w)/2, this.y + (this.height+h)/2);
                    break;
                }
            }
        } else
            graph.drawString (this.value, this.x + (this.width - stringWidth) / 2 , this.y + (this.height+h)/2);
    }

    public TGComponent isOnMe(int _x, int _y) {
        if (_x < this.x || _x > this.x + this.width || _y > this.y + this.height || _y < this.y)
            return null;

        /*
        // This is the exact check.
        if (_x < this.x + AvatarSMDLibraryFunctionCall.linebreak) {
            int x0 = _x - this.x;
            int y0 = _y - this.y - this.height/2;
            if (y0 <= this.height/(2*AvatarSMDLibraryFunctionCall.linebreak)*x0-this.height/2 || y0 >= -this.height/(2*AvatarSMDLibraryFunctionCall.linebreak)*x0+this.height/2)
                return this;
            return null;
        }

        if (_x > this.x + this.width - AvatarSMDLibraryFunctionCall.linebreak) {
            int x0 = _x - this.x - this.width + AvatarSMDLibraryFunctionCall.linebreak;
            int y0 = _y - this.y - this.height/2;
            if (y0 >= this.height/(2*AvatarSMDLibraryFunctionCall.linebreak)*x0-this.height/2 && y0 <= -this.height/(2*AvatarSMDLibraryFunctionCall.linebreak)*x0+this.height/2)
                return this;
            return null;
        }
        */

        return this;
    }

    public boolean editOndoubleClick(JFrame frame) {
        JDialogSMDLibraryFunctionCall dialog = new JDialogSMDLibraryFunctionCall (
                this,
                frame,
                "Setting of library function call");
        //dialog.setSize (650, 590);
        GraphicLib.centerOnParent(dialog, 650, 590);

        dialog.setVisible( true ); // blocked until dialog has been closed

        return true;
    }

    public AvatarBDLibraryFunction getLibraryFunction () {
        return this.libraryFunction;
    }

    public void setLibraryFunction (AvatarBDLibraryFunction libraryFunction) {
        this.libraryFunction = libraryFunction;
    }

    public LinkedList<TAttribute> getParameters () {
        return this.parameters;
    }

    public void setParameters (LinkedList<TAttribute> parameters) {
        this.parameters = parameters;
    }

    public LinkedList<AvatarSignal> getSignals () {
        return this.signals;
    }

    public void setSignals (LinkedList<AvatarSignal> signals) {
        this.signals = signals;
    }

    public LinkedList<TAttribute> getReturnAttributes () {
        return this.returnAttributes;
    }

    public void setReturnAttributes (LinkedList<TAttribute> returnAttributes) {
        this.returnAttributes = returnAttributes;
    }

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

                if (attr == null)
                    builder.append ("_");
                else
                    builder.append (attr.getId ());
            }

            builder.append (" = ");
        }

        builder.append (this.libraryFunction.getFullyQualifiedName ());
        builder.append (" (");

        first = true;
        for (TAttribute attr: this.parameters) {
            if (first)
                first = false;
            else
                builder.append (", ");

            if (attr == null)
                builder.append ("???");
            else
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

    @Override
    protected String translateExtraParam () {
        StringBuffer sb = new StringBuffer ("<extraparam>\n");
        if (this.libraryFunction != null) {
            sb.append ("<LibraryFunction name=\"");
            sb.append (this.libraryFunction.getFullyQualifiedName ());
            sb.append ("\" />\n");
        }

        for (TAttribute attr: this.parameters) {
            sb.append("<Parameter id=\"");
            if (attr == null)
                sb.append ("null");
            else
                sb.append(attr.getId());
            sb.append("\" />\n");
        }

        for(AvatarSignal signal: this.signals) {
            sb.append("<Signal value=\"");
            if (signal == null)
                sb.append ("null");
            else
                sb.append(signal.toString());
            sb.append("\" />\n");
        }

        for (TAttribute attr: this.returnAttributes) {
            sb.append("<ReturnAttribute id=\"");
            if (attr == null)
                sb.append ("null");
            else
                sb.append(attr.getId());
            sb.append("\" />\n");
        }

        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam (NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            for(int i=0; i<nl.getLength(); i++) {
                Node n1 = nl.item(i);

                // Ignore if it's not an element
                if (n1.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                MainGUI mgui = this.tdp.getMGUI ();
                TURTLEPanel tp = mgui.getCurrentTURTLEPanel ();
                String tdpName = this.tdp.getName ();

                // Fetch the children nodes
                NodeList nli = n1.getChildNodes();
                for(int j=0; j<nli.getLength(); j++) {
                    Node n2 = nli.item(j);

                    // Ignore if it's not an element
                    if (n2.getNodeType() != Node.ELEMENT_NODE)
                        continue;
                    Element elt = (Element) n2;

                    switch (elt.getTagName ()) {
                        case "LibraryFunction": {
                            if (this.libraryFunction != null) {
                                throw new MalformedModelingException ();
                            }

                            String name = elt.getAttribute ("name");
                            if (name.equals ("null"))
                                break;

                            for (AvatarBDLibraryFunction func: mgui.getAllLibraryFunctions (tp, tdpName))
                                if (func.getFullyQualifiedName ().equals (name)) {
                                    this.libraryFunction = func;
                                    break;
                                }

                            if (this.libraryFunction == null) {
                                throw new MalformedModelingException ();
                            }
                            break;
                        }

                        case "Parameter": {
                            String name = elt.getAttribute ("id");
                            if (name.equals ("null")) {
                                this.parameters.add (null);
                                break;
                            }

                            boolean found = false;
                            for (TAttribute attr: mgui.getAllAttributes (tp, tdpName))
                                if (attr.getId ().equals (name)) {
                                    this.parameters.add (attr);
                                    found = true;
                                    break;
                                }

                            if (!found) {
                                throw new MalformedModelingException ();
                            }
                            break;
                        }

                        case "Signal": {
                            String value = elt.getAttribute ("value");
                            if (value.equals ("null")) {
                                this.signals.add (null);
                                break;
                            }

                            boolean found = false;
                            for (AvatarSignal signal: mgui.getAllSignals (tp, tdpName))
                                if (signal.toString ().equals (value)) {
                                    this.signals.add (signal);
                                    found = true;
                                    break;
                                }

                            if (!found) {
                                throw new MalformedModelingException ();
                            }
                            break;
                        }

                        case "ReturnAttribute": {
                            String name = elt.getAttribute ("id");
                            if (name.equals ("null")) {
                                this.returnAttributes.add (null);
                                break;
                            }

                            boolean found = false;
                            for (TAttribute attr: mgui.getAllAttributes (tp, tdpName))
                                if (attr.getId ().equals (name)) {
                                    this.returnAttributes.add (attr);
                                    found = true;
                                    break;
                                }

                            if (!found) {
                                throw new MalformedModelingException ();
                            }
                            break;
                        }
                    }
                }
            }
        } catch (MalformedModelingException e) {
            throw e;
        } catch (Exception e) {
            throw new MalformedModelingException();
        }

        if (this.libraryFunction != null &&
           (this.parameters.size () != this.libraryFunction.getParameters ().size () ||
            this.signals.size () != this.libraryFunction.getSignals().size () ||
            this.returnAttributes.size () != this.libraryFunction.getReturnAttributes().size ())) {
            throw new MalformedModelingException ();
        }
    }

    @Override
    public int getType() {
        return TGComponentManager.AVATARSMD_LIBRARY_FUNCTION_CALL;
    }
}

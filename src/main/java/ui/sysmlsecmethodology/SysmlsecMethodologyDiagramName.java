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




package ui.sysmlsecmethodology;

import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

//import java.awt.geom.*;

/**
   * Class SysmlsecMethodologyDiagramName
   * Internal component that shows the diagram name and validation/simu
   * references
   * Creation: 26/01/2016
   * @version 1.0 26/01/2016
   * @author Ludovic APVRILLE
 */
public class SysmlsecMethodologyDiagramName extends TGCScalableWithoutInternalComponent implements SwallowedTGComponent {
    //protected boolean emptyText;

    public final static int X_MARGIN = 5;
    public final static int Y_MARGIN = 3;


    protected final static int SIM_ANIM = 0;
    protected final static int UPP = 1;
    protected final static int PROVERIF = 2;
    protected final static int INVARIANTS = 3;
    protected final static int PROTO = 4;
    protected final static int INTERNAL_MODEL_CHECKER = 14;

    protected final static int SIM_ANIM_APP_DIPLO = 5;
    protected final static int SIM_TRACE_APP_DIPLO = 6;
    protected final static int LOT_APP_DIPLO = 7;
    protected final static int UPP_APP_DIPLO = 8;
    protected final static int TML_APP_DIPLO = 9;

    protected final static int FV_MAPPING_DIPLO = 10;
    protected final static int SIM_TRACE_MAPPING_DIPLO = 11;
    protected final static int SIM_ANIM_MAPPING_DIPLO = 12;
    protected final static int TML_MAPPING_DIPLO = 13;
    


    protected final String[] SHORT_ACTION_NAMES = {
        "simu", "upp", "proverif", "inv",
        "code-gen", "sim-anim", "sim-trace", "lot", "upp", "tml",
        "fv", "sim-trace", "sim-anim", "tmap", "RG"};

    protected final String[] LONG_ACTION_NAMES = {
        /*0*/ "Simulation and animate the model",
        "Verify safety propeties on the model with UPPAAL",
        "Verify security properties on the model with ProVerif",
        "Verify mutual exclusions on the model with invariants",
        "Generate executable code",
	"Generate a vcd simulation trace of a DIPLODOCUS functional model",
        "Verify a DIPLODOCUS functional model with UPPAAL",
        "Generate a Reachability graph of a DIPLODOCUS functional model",
        "Generate a TML text description of a DIPLODOCUS functional model",
        "Formal verify a DIPLODOCUS mapping model",
        "Simulate a DIPLODOCUS mapping model",
        "Generate a TMAP/TARCHI/TML text dscription of a DIPLODOCUS mapping model",
        "Simulate and animate DIPLODOCUS functional models",
        "Simulate and animate DIPLODOCUS mapping models",
            "Use internal model checker to generate a RG, or evaluate reachability properties"

    };

    protected int[] validations;
    protected int[] valMinX;
    protected int[] valMaxX;

    protected int indexOnMe; // -1 -> on main element. -2: on not precise element; Other: on a validations item.

    private int myWidth, myHeight, widthAppli;


    public SysmlsecMethodologyDiagramName(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y,  _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        nbConnectingPoint = 0;
        minWidth = 10;
        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = false;

        name = "value ";



        initScaling(10, 10);


        myImageIcon = IconManager.imgic302;
    }

    public void internalDrawing(Graphics g) {
        boolean onMe = false;

        if (tdp.componentPointed() == this) {
            onMe = true;
        }

        if ((y+Y_MARGIN) > (getFather().getY()+getFather().getHeight())) {
            return;
        }

        //TraceManager.addDev("Internal drawing ...");
        int currentMaxX;
        String val = value;
        int w = g.getFontMetrics().stringWidth(value);
        int wf = getFather().getWidth();
        int w1;
        int saveCurrentMaxX;
        boolean oneWritten;

        if (wf < w+(2*X_MARGIN)) {
            val = ".";
        }

        int curWidth = myWidth;



        Font f = g.getFont();

        if (onMe && indexOnMe == -1) {
            g.setFont(f.deriveFont(Font.BOLD));
        }
        widthAppli = g.getFontMetrics().stringWidth(val);
        curWidth = Math.max(widthAppli, curWidth);
        drawSingleString(g, val, x, y);
        g.setFont(f);

        if (validations == null) {
            if (getFather() instanceof SysmlsecMethodologyDiagramReference) {
                ((SysmlsecMethodologyDiagramReference)(getFather())).makeValidationInfos(this);
            }
        }

        if ((validations != null) && (valMinX == null)) {
            valMinX = new int[validations.length];
            valMaxX = new int[validations.length];
        }

        /*if (validations == null) {
          TraceManager.addDev("null validation");
          } else {
          TraceManager.addDev("Validation size=" + validations.length);
          }*/

        currentMaxX = wf + x - 2*(X_MARGIN);
        saveCurrentMaxX = currentMaxX;

        if (wf < w+(2*X_MARGIN)) {
            makeScale(g, w+(2*X_MARGIN));
            return;
        }

        //TraceManager.addDev("Tracing validation Validation size=" + validations.length);
        oneWritten = false;


        g.setFont(f.deriveFont(Font.ITALIC));
        if ((validations != null) & (validations.length >0)) {
            for(int i=validations.length-1; i>=0; i--) {
                //TraceManager.addDev("Validations[" + i + "] = " + validations[i]);

                w1 = g.getFontMetrics().stringWidth(SHORT_ACTION_NAMES[validations[i]]);


                if ((onMe && indexOnMe == i)) {
                    g.setFont(f.deriveFont(Font.ITALIC));
                }

                if ((currentMaxX - w1) > (x + w)) {
                    if ((onMe && indexOnMe == i)) {
                        g.setFont(f.deriveFont(Font.BOLD));
                    }
                    drawSingleString(g, SHORT_ACTION_NAMES[validations[i]], currentMaxX - w1, y);
                    g.setFont(f.deriveFont(Font.ITALIC));
                    valMinX[i] = currentMaxX-w1;
                    valMaxX[i] = currentMaxX;
                    oneWritten = true;
                    currentMaxX = currentMaxX - w1 - 5;
                } else {
                    break;
                }

            }
        }



        g.setFont(f);

        if (oneWritten) {
            makeScale(g, saveCurrentMaxX - x);
        } else {
            makeScale(g, w);
        }

        //TraceManager.addDev("current width=" + curWidth);
        if (onMe)

            g.drawRect(x-2, y-12, curWidth+5, 15);

        return;


    }

    private void makeScale(Graphics g, int _size) {
        if (!tdp.isScaled()) {
            myWidth = _size;
            myHeight = g.getFontMetrics().getHeight();
        }
    }


    public TGComponent isOnMe(int _x, int _y) {
        int oldIndex = indexOnMe;
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(myWidth, minWidth), myHeight)) {
            indexOnMe = -2;

            if (_x <= (x+widthAppli)) {
                indexOnMe = -1;
                tdp.getMGUI().setStatusBarText("Open the " + value + " model");
            }
            if ((validations != null) && (validations.length > 0)) {
                for(int i=0; i<validations.length; i++) {
                    if ((_x >= valMinX[i]) && (_x <= valMaxX[i])) {
                        indexOnMe = i;
                        tdp.getMGUI().setStatusBarText(LONG_ACTION_NAMES[validations[i]]);
                        //TraceManager.addDev("Index on " + indexOnMe);
                        break;
                    }
                }
            }

            if (oldIndex != indexOnMe) {
                tdp.repaint();
            }


            return this;
        }
        return null;
    }

    public boolean editOndoubleClick(JFrame frame) {

        if (indexOnMe == -1) {
            // Opening the diagram
            if (!tdp.getMGUI().selectMainTab(value)) {
                TraceManager.addDev("Diagram removed?");
                return false;
            }

        }


        if (indexOnMe > -1) {
            SysmlsecMethodologyDiagramReference ref = ((SysmlsecMethodologyDiagramReference)(getFather()));
            ref.makeCall(value, indexOnMe);
        }


        return true;
    }


    public  int getType() {
        return TGComponentManager.SYSMLSEC_METHODOLOGY_DIAGRAM_NAME;
    }

    public int getDefaultConnector() {
        return TGComponentManager.SYSMLSEC_METHODOLOGY_CONNECTOR;
    }

    public void setValidationsNumber(int size) {
        validations = new int[size];
    }

    public void setValidationsInfo(int _index, int _val) {
        validations[_index] = _val;
    }

    public void rescale(double scaleFactor){

        if ((valMinX != null) && (valMinX.length > 0)) {
            for(int i=0; i<valMinX.length; i++) {
                valMinX[i] = (int)(valMinX[i] / oldScaleFactor * scaleFactor);
                valMaxX[i] = (int)(valMaxX[i] / oldScaleFactor * scaleFactor);
            }
        }

        super.rescale(scaleFactor);
    }
}

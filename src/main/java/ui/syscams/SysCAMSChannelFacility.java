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

package ui.syscams;

import myutil.GraphicLib;
import ui.*;

import java.awt.*;

/**
 * Class SysCAMSChannelFacility
 * Channel facility. To be used in SystemC-AMS component task diagrams
 * Creation: 22/04/2018
 * @version 1.0 22/04/2018
 * @author Irina Kit Yan LEE
 */

public abstract class SysCAMSChannelFacility extends TGCScalableWithInternalComponent {
    protected Color myColor, portColor;

    protected SysCAMSPrimitivePort inp, outp;
    protected int inpIndex, outpIndex;
    protected boolean conflict = false;
    protected String conflictMessage;

    public SysCAMSChannelFacility(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        minWidth = 10;
        minHeight = 10;

        moveable = true;
        editable = false;
        removable = true;
        userResizable = false;
    }

    public TGComponent isOnOnlyMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public void calculatePortColor() {
        if (conflict) {
            myColor = Color.red;
        } else {
            SysCAMSPrimitivePort port = inp;
            if (port == null) {
                port = outp;
            }
            if (port == null) {
                portColor = null;
                if (myColor == null) {
                    myColor = new Color(251, 252, 155- (getMyDepth() * 10));
                }
            } else {
                int typep = port.getPortType();
                if (typep == 0) {
                    myColor = ColorManager.TML_PORT_CHANNEL;
                } else if (typep == 1) {
                    myColor = ColorManager.TML_PORT_EVENT;
                } else {
                    myColor = ColorManager.TML_PORT_REQUEST;
                }
            }
        }
        portColor = myColor;
    }

    public void setInPort(SysCAMSPrimitivePort _inp) {
        inp = _inp;
        calculatePortColor();
    }

    public void setOutPort(SysCAMSPrimitivePort _outp) {
        outp = _outp;
        calculatePortColor();
    }

    public SysCAMSPrimitivePort getInPort() {
        return inp;
    }

    public SysCAMSPrimitivePort getOutPort() {
        return outp;
    }

    public int getInpIndex() {
        return inpIndex;
    }

    public int getOutpIndex() {
        return outpIndex;
    }

    public void setInpIndex(int _inpIndex) {
        inpIndex = _inpIndex;
    }

    public void setOutpIndex(int _outpIndex) {
        outpIndex = _outpIndex;
    }

    public boolean getConflict() {
        return conflict;
    }

    public void setConflict(boolean _conflict, String _msg) {
        conflict = _conflict;
        myColor = null;
        conflictMessage = _msg;
        calculatePortColor();
    }

    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_PORT_TMLC;
    }

    public Color getPortColor() {
        return portColor;
    }

    public String getAttributes() {
        if (conflict) {
            return conflictMessage;
        }
        String s = "";
        if (inp != null) {
            s = s + inp.getAttributes();
            if (outp != null) {
                s = s + "\n";
            }
        }
        if (outp != null) {
            s = s + outp.getAttributes();
        }
        if (conflict) {
            s += "Error in path=" + conflict;
        }
        return s;
    }
}
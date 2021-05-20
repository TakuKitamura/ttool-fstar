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

package ui.avatardd;

import ui.*;
import ui.util.IconManager;

import java.util.Vector;

/**
 * Class ADDRAMNode Node. To be used in avatar deployment diagrams. Creation:
 * 01/07/2014
 * 
 * @version 1.0 01/07/2014
 * @author Ludovic APVRILLE
 */

public class ADDRAMNode extends ADDMemoryNode implements SwallowTGComponent, WithAttributes {

    protected int monitored = 0;
    protected int index = 0;
    // protected int cluster_index = 0;

    public ADDRAMNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
            TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 200;
        height = 200;
        minWidth = 100;
        minHeight = 35;

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new ADDConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new ADDConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);

        connectingPoint[8] = new ADDConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new ADDConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new ADDConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new ADDConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new ADDConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new ADDConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        stereotype = "RAM";

        name = tdp.findNodeName("Memory");
        value = "name";

        myImageIcon = IconManager.imgic700;
    }

    public int getType() {
        return TGComponentManager.ADD_RAMNODE;
    }

    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        // TraceManager.addDev("Accept swallowed?");
        return tgc instanceof ADDBlockArtifact;
    }

    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        // TraceManager.addDev("Add swallowed?");
        // Set its coordinates
        if (tgc instanceof ADDChannelArtifact) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            // TraceManager.addDev("Add swallowed!!!");
            addInternalComponent(tgc, 0);
            return true;
        }
        return false;
    }

    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public Vector<ADDChannelArtifact> getArtifactList() {
        Vector<ADDChannelArtifact> v = new Vector<ADDChannelArtifact>();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ADDChannelArtifact) {
                v.add((ADDChannelArtifact) tgcomponent[i]);
            }
        }
        return v;
    }

    public void hasBeenResized() {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof ADDChannelArtifact) {
                tgcomponent[i].resizeWithFather();
            }
        }
    }

    public int getIndex() {
        return index;
    }

    /*
     * public int getClusterIndex() { return cluster_index; }
     */

    public int getMonitored() {
        return monitored;
    }

    public void setMonitored(int _monitored) {
        monitored = _monitored;
    }
}

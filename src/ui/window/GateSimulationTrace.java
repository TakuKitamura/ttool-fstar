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
 * Class GateSimulationTrace
 * Simulation times of a RT-LOTOS action
 * Creation: 12/12/2003
 * @version 1.0 12/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;


import java.util.*;

import translator.*;


public class GateSimulationTrace extends Vector {
    private Gate g;
    private GroupOfGates gog;
    
    public GateSimulationTrace(Gate _g, GroupOfGates _gog) {
        g = _g;
        gog = _gog;
    }
    
    public Gate getGate() {
        return g;
    }
    
    public GroupOfGates getGroupOfGates() {
        return gog;
    }
    
    public void addTimeAction(int t, int action, String values) {
        add(new TraceData(t, action, values));
    }
    
    public int getTime(int i) {
        if (i < size()) {
            TraceData td = (TraceData)(elementAt(i));
            return td.time;
        }
        return -1;
    }
    
    public int getAction(int i) {
        if (i < size()) {
            TraceData td = (TraceData)(elementAt(i));
            return td.action;
        }
        return -1;
    }
    
    public String getValues(int i) {
        if (i < size()) {
            TraceData td = (TraceData)(elementAt(i));
            return td.values;
        }
        return "";
    }
    
    public int getMaxTime() {
        int cpt = 0;
        TraceData td;
        for(int i=0; i<size(); i++) {
            td = (TraceData)(elementAt(i));
            cpt = Math.max(td.time, cpt);
        }
        return cpt;
    }  
    
    public int getMaxAction() {
        int cpt = 0;
        TraceData td;
        for(int i=0; i<size(); i++) {
            td = (TraceData)(elementAt(i));
            cpt = Math.max(td.action, cpt);
        }
        return cpt;
    }
    
    // max time for actions <= actionNb
    public int calculateMaxTimeOf(int actionNb) {
        int maxTime = 0;
        TraceData td;
        for(int i=0; i<size(); i++) {
            td = (TraceData)(elementAt(i));
            if (td.action <= actionNb) {
                maxTime = Math.max(maxTime, td.time);
            }
        }
        return maxTime;
    }
}
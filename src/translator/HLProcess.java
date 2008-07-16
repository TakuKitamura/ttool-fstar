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
 * Class HLProcess
 * Creation: 10/12/2003
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;

public class HLProcess extends Process {
    private TClass tc;
    
    
    public HLProcess(String _name, Vector _gateList, Vector _paramList, TClass _tc, int _languageID) {
        super(_name, _gateList, _paramList, _languageID);
        tc = _tc;
    }
    
    public boolean isNameOfMyTClass(String s) {
        return tc.getLotosName().equals(s);
    }
    
    public TClass getTClass() {
        return tc;
    }
    
    public String getHighLevelCallToMe(MasterGateManager mgm, int languageID) {
        String s = name;
        if (hasNonInternalGates()) {
            s = s + SEP1G + listNonInternalGates(mgm) + SEP3G;
        }
        if(hasParameters()) {
            s = s + SEP1P + listInitParameters(languageID) + SEP3P;
        }
        return s;
    }
    
    public String listNonInternalGates(MasterGateManager mgm) {
        String s = "";
        boolean find = false;
        Gate g;
        
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (!g.isInternal()) {
                //System.out.println("My TC: " + tc.getLotosName() +  " g:" +g.getLotosName());
                g = mgm.getMasterGateOf(tc, g);
                //System.out.println("Master g:" + g.getLotosName());
                if (!find) {
                    find = true;
                    s = s + g.getLotosName();
                } else {
                    s = s + SEP2G + g.getLotosName();
                }
            }
        }
        return s;
    }
    
} // Class


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
 * Class EvtToLink
 * Creation: 17/08/2004
 * @version 1.1 17/08/2004
 * @author Ludovic APVRILLE
 * @see
 */

package sdtranslator;

import sddescription.*;
import translator.*;

public class EvtToLink {
    public static final int SYNC = 0;
    public final static int TIMER_SET = 5;
    public final static int TIMER_RESET = 6;
    public final static int TIMER_EXP = 7;
    public final static int SEND_MSG = 9;
    public final static int RECV_MSG = 10;
    public final static int INTERNAL_ACTION = 8;
    public final static int VARIABLE_SET = 11;
    public final static int TIME_INTERVAL = 12;
    
    public Evt evt;
    public TClass t;
    public Gate g;
    public Param p;
    public int type;
	public int nbOfParams;
    
    
    public EvtToLink(Evt _evt, TClass _t, Gate _g, int _type) {
        evt = _evt;
        t = _t;
        g = _g;
        type = _type;
    }
	
	public EvtToLink(Evt _evt, TClass _t, Gate _g, int _type, int _nbOfParams) {
        evt = _evt;
        t = _t;
        g = _g;
        type = _type;
		nbOfParams = _nbOfParams;
    }
    
    public EvtToLink(Evt _evt, TClass _t, Param _p, int _type) {
        evt = _evt;
        t = _t;
        p = _p;
        type = _type;
    }
    
    public EvtToLink(Evt _evt, TClass _t, int _type) {
        evt = _evt;
        t = _t;
        type = _type;
    }

}
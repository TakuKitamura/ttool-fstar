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
* Class RelationTMLUPPAAL
* Creation: 21/05/2007
* @version 1.1 21/05/2007
* @author Ludovic APVRILLE
* @see
*/

package tmltranslator.touppaal;

import java.awt.*;
import java.util.*;

import uppaaldesc.*;
import myutil.*;
import tmltranslator.*;

public class RelationTMLUPPAAL {
    
	private ArrayList<TMLTaskTemplate> tts;
	private ArrayList<TMLActivityElementLocation> taeltls;
    
    public RelationTMLUPPAAL() {
        tts = new ArrayList<TMLTaskTemplate>();
		taeltls = new ArrayList<TMLActivityElementLocation>();
    }
	
	public void addTMLTaskTemplate(TMLTaskTemplate tt) {
		tts.add(tt);
	}
	
	public void addTMLTaskTemplate(TMLTask _task, UPPAALTemplate _template) {
		tts.add(new TMLTaskTemplate(_task, _template));
	}
	
	public void addADComponentLocation(TMLActivityElementLocation _eltl) {
		taeltls.add(_eltl);
	}
	
	public void addTMLActivityElementLocation(TMLActivityElement _elt, UPPAALLocation _loc1, UPPAALLocation _loc2) {
		taeltls.add(new TMLActivityElementLocation(_elt, _loc1, _loc2));
	}
	
	public TMLTaskTemplate getFirstTMLTaskTemplate(TMLTask _t) {
		for(TMLTaskTemplate ttt:tts) {
			if (ttt.task == _t) {
				return ttt;
			}
		}
		return null;
	}
	
	public TMLActivityElementLocation getFirstTMLActivityElementLocation(TMLActivityElement _elt) {
		for(TMLActivityElementLocation eltl:taeltls) {
			if (eltl.elt == _elt) {
				return eltl;
			}
		}
		return null;
	}
	
	
	
	public void setIds(UPPAALTemplate _template, int _beginid, int _endid) {
		for(TMLTaskTemplate ttt:tts) {
			if (ttt.template == _template) {
				ttt.beginid = _beginid;
				ttt.endid = _endid;
				return;
			}
		}
	}
	
	public String getRQuery(TMLTask _t, TMLActivityElement _elt) {
		TMLActivityElementLocation eltl = getFirstTMLActivityElementLocation(_elt);
		TMLTaskTemplate ttt = getFirstTMLTaskTemplate(_t);
		if ((ttt == null) || (eltl == null)) {
			return null;
		}
		
		return ttt.template.getName() + "__" + ttt.template.getIdInstanciation() + "." + eltl.endloc.name;
		/*String q="";
		for(int i=ttt.beginid; i<ttt.endid+1; i++) {
			q += ttt.template.getName() + "__" + i;
			q += "." + eltl.endloc.name;
			if (i != ttt.endid) {
				q += " || ";
			}
		}
		return q;*/
	}
	
	public String toString() {
		String s="TClass / Templates\n";
		for(TMLTaskTemplate ttt:tts) {
			s+=ttt.toString() + "\n";
		}
		s+="\nADComponents vc locations:\n";
		for(TMLActivityElementLocation eltl:taeltls) {
			s+=eltl.toString() + "\n";
		}
		return s;
	}
    
}
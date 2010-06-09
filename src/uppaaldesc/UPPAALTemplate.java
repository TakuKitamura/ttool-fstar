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
* Class UPPAALTemplate
* Creation: 03/11/2006
* @version 1.0 03/11/2006
* @author Ludovic APVRILLE
* @see
*/

package uppaaldesc;

import java.util.*;

import myutil.*;


public class UPPAALTemplate {
	protected String name;
	protected String parameter = "";
	protected String declaration = "";
	protected UPPAALLocation initLocation = null;
	protected LinkedList locations;
	protected LinkedList transitions;
	protected int idInstanciation = 0;
	
    public UPPAALTemplate() {
		locations = new LinkedList();
		transitions = new LinkedList();
    }
	
	public void setIdInstanciation(int _idInstanciation) {
		idInstanciation = _idInstanciation;
	}
	
	public int getIdInstanciation() {
		return idInstanciation;
	}
	
    public void setName(String _name) {
		name = _name;
    }
    
    public String getName() {
		return name;
    }
    
    public void addLocation(UPPAALLocation loc) {
		locations.add(loc);
    }
    
    public void addTransition(UPPAALTransition tr) {
		transitions.add(tr);
    }
	
    public void addDeclaration(String _dec) {
		declaration += _dec;
    }
	
    public void addParameter(String _dec) {
		parameter += _dec;
    }
	
	public void removeParameter() {
		parameter = "";
    }
	
	
	
    public void setInitLocation(UPPAALLocation _loc) {
		initLocation = _loc;
    }
    
    public UPPAALLocation getInitLocation() {
		return initLocation;
    }
    
	
    public UPPAALLocation getLastLocation() {
		return (UPPAALLocation)(locations.getLast());
    }
    
    public void generateGraphicalPositions() {
		
    }
    
    public StringBuffer makeStringAutomata() {
		StringBuffer ret = new StringBuffer("");
		
		if (initLocation == null) {
			return ret;
		}
		
		ListIterator iterator = locations.listIterator();
		while(iterator.hasNext()) {
			ret.append(((UPPAALLocation)(iterator.next())).getXML());
		}
		
		ret.append("<init ref=\"" +initLocation.id + "\" />\n");
		
		iterator = transitions.listIterator();
		while(iterator.hasNext()) {
			ret.append(((UPPAALTransition)(iterator.next())).getXML());
		}
		return ret;
    }
	
	
    public StringBuffer makeTemplate() {
		StringBuffer fullString = new StringBuffer("<template>\n");
		fullString.append("<name>" + name + "</name>\n");
		fullString.append("<parameter>" + Conversion.transformToXMLString(parameter)  + "</parameter>\n");
		fullString.append("<declaration>" + Conversion.transformToXMLString(declaration) + "</declaration>\n");
		fullString.append(makeStringAutomata());
		fullString.append("</template>\n");
		
		return fullString;
    }
	
	public void enhanceGraphics() {
		if (locations.size() < 2) {
			return;
		}
		
		if (initLocation == null) {
			return;
		}
		
		int y = getMaxYLocations();
		int dec = initLocation.idPoint.y - initLocation.namePoint.y;
		initLocation.idPoint.y = y / 2;
		initLocation.namePoint.y = initLocation.idPoint.y - dec;
	}
	
	public int getMaxYLocations() {
		int ret = 0;
		ListIterator iterator = locations.listIterator();
		UPPAALLocation loc;
		while(iterator.hasNext()) {
			loc = ((UPPAALLocation)(iterator.next()));
			ret = Math.max(ret, loc.idPoint.y);
		}
		return ret;
		
	}
	
	public int nbOfTransitionsExitingFrom(UPPAALLocation _loc) {
		int cpt = 0;
		
		ListIterator iterator = transitions.listIterator();
		UPPAALTransition tr;
		while(iterator.hasNext()) {
			tr = ((UPPAALTransition)(iterator.next()));
			if (tr.sourceLoc == _loc) {
				cpt ++;
			}
		}
		
		return cpt;
		
	}
	
	public void optimize() {
		// Only one exiting empty transition from a location + empty transition
		// In that case, the location and the empty transition are removed, and the
		// automata is updated
		LinkedList<UPPAALTransition> ll = new LinkedList<UPPAALTransition>();
		
		// First step: finding all concernened locs and transitions
		ListIterator iterator = transitions.listIterator();
		UPPAALTransition tr;
		while(iterator.hasNext()) {
			tr = ((UPPAALTransition)(iterator.next()));
			if (tr.isAnEmptyTransition()) {
				if (nbOfTransitionsExitingFrom(tr.sourceLoc) == 1) {
					if (!(tr.sourceLoc.hasInvariant())) {
						ll.add(tr);
					}
				}
			}
		}
		
		// Removing those transitions
		for(UPPAALTransition ut: ll) {
			iterator = transitions.listIterator();
			while(iterator.hasNext()) {
				tr = ((UPPAALTransition)(iterator.next()));
				if (tr.destinationLoc == ut.sourceLoc) {
					tr.destinationLoc = ut.destinationLoc;
				}
				TraceManager.addDev("Removed transition: " + ut + " and location:" + ut.sourceLoc);
			}
			locations.remove(ut.sourceLoc);
			transitions.remove(ut);
			
			if (initLocation == ut.sourceLoc) {
				setInitLocation(ut.destinationLoc);
			}
		}
		
	}
}
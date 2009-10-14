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
 * Class EBRDD
 * Creation: 18/09/2009
 * @version 1.0 18/09/2009
 * @author Ludovic APVRILLE
 * @see
 */

package req.ebrdd;

import java.util.*;

import myutil.*;

public class EBRDD extends ArrayList<EBRDDComponent> {
    private String name;
    protected EBRDDStart ads;
	protected ArrayList<EBRDDAttribute> variables;
    
    public EBRDD(EBRDDStart _ads, String _name) {
		name = _name;
        ads = _ads;
		variables = new ArrayList<EBRDDAttribute>();
        add(ads);
    }
    
    public EBRDD(String _name) {
		name = _name;
        ads = new EBRDDStart("Start", null);
		variables = new ArrayList<EBRDDAttribute>();
        add(ads);
    }
	
	// Returns false if another attribute with the same name has already been defined
	// Returns true otherwise.
	public boolean addAttribute(EBRDDAttribute _attr) {
		for(EBRDDAttribute attr: variables) {
			if (attr.getName().equals(_attr.getName())) {
				return false;
			}
		}
		variables.add(_attr);
		return true;
	}
	
	public int getNbOfAttributes() {
		return variables.size();
	}
	
	public EBRDDAttribute getAttributeByIndex(int _index) {
		if ((_index < variables.size()) && (_index > -1)) {
			return variables.get(_index);
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
    
    public EBRDDStart getStartState() {
        return ads;
    }
	
	public void setStartState(EBRDDStart _ads) {
		remove(ads);
		ads = _ads;
	}
	
	public EBRDDComponent getEBRDDComponent(int index) {
		return get(index);
	}
	
	public void removeAllNonReferencedElts() {
        EBRDDComponent adc;
        while((adc = hasNonReferencedElts()) != null) {
            remove(adc);
        }
    }
    
    public EBRDDComponent hasNonReferencedElts() {
        EBRDDComponent adc;
        EBRDDComponent adc1;
        for(int i=0; i<size(); i++) {
            adc = (EBRDDComponent)(get(i));
            if (adc != ads) {
                adc1 = getFirstComponentLeadingTo(adc);
                if (adc1 == null) {
                    // no component!
                    return adc;
                }
            }
        }
        return null;
    }
	
	public EBRDDComponent getFirstComponentLeadingTo(EBRDDComponent ad) {
        EBRDDComponent ad1;
        int i, j;
        
        for (i=0; i<size(); i++) {
            ad1 = get(i);
            for(j=0; j<ad1.getNbNext(); j++) {
                if (ad1.getNextElement(j) == ad) {
                    return ad1;
                }
            }
        }
        return null;
    }
	
	public String toString() {
		StringBuffer sb = new StringBuffer("EBRDD=\nvariables:\n");
		for(EBRDDAttribute attr: variables) {
			sb.append(attr.toString() + "\n");
		}
		sb.append("Activity diagram:\n");
		
		if (ads != null) {
			exploreString(ads, sb, 0);
		}
		
		return sb.toString();
	}
	
	public void exploreString(EBRDDComponent elt, StringBuffer sb, int tabLevel) {
		int j;
		for(j=0; j<tabLevel; j++) {
			sb.append("\t");
		}
		sb.append(elt.toString() + "\n");
		
		if (elt instanceof EBRDDERC) {
			((EBRDDERC)elt).exploreString(((EBRDDERC)elt).getRoot(), sb, tabLevel+1);
		}
		
		if (elt.getNbNext() == 0) {
			return;
		}
		if (elt.getNbNext() == 1) {
			exploreString(elt.getNextElement(0), sb, tabLevel);
			return;
		}
		
		tabLevel ++;
		for(int i=0; i<elt.getNbNext(); i++) {
			for(j=0; j<tabLevel; j++) {
				sb.append("\t");
			}
			sb.append("#" + i + ":\n");
			exploreString(elt.getNextElement(i), sb, tabLevel+1);
		}
	}
    
   
    
}

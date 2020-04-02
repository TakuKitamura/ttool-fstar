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





package avatartranslator.modelchecker;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarStateMachineElement;

/**
   * Class SpecificationReachability
   * Reachability of an element
   * Creation: 21/06/2016
   * @version 1.0 21/06/2016
   * @author Ludovic APVRILLE
 */
public class SpecificationLiveness  {
    public Object ref1, ref2; // ref1 must be provided, ref2 might be null
    public boolean result; 
    public SpecificationState state;
    
    public SpecificationLiveness(Object _ref1, Object _ref2) {
	ref1 = _ref1;
	ref2 = _ref2;
	result = true;
	state = null;
    }

    public String toString() {
	String name;
	if (ref1 instanceof AvatarStateMachineElement) {
	    name = "Element " + ((AvatarStateMachineElement)ref1).getExtendedName();
	} else {
	    name = ref1.toString();
	}

	if (ref2 != null) {
	    if (ref2 instanceof AvatarBlock) {
		name += " of block " + ((AvatarBlock)ref2).getName();
	    } else {
		name += ref2.toString();
	    }
	}

	
	if (result) {
	    return name + " -> liveness is satisfied\n"; 
	}
	return name + " -> liveness is NOT satisfied\n";
	
    }

}

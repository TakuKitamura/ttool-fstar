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
   * Class SpecificationBlock
   * Coding of a block
   * Creation: 31/05/2016
   * @version 1.0 31/05/2016
   * @author Ludovic APVRILLE
   * @see
   */


package avatartranslator.modelchecker;

import avatartranslator.*;
import java.util.*;

import myutil.*;

public class SpecificationBlock  {
    
    public static final int HEADER_VALUES = 3;
    
    public static final int STATE_INDEX = 0;
    public static final int CLOCKMIN_INDEX = 1;
    public static final int CLOCKMAX_INDEX = 2;
    public static final int ATTR_INDEX = 3;
    
    public int [] values; // state in block, clockmin, clockmax, variables

    public SpecificationBlock() {
    }

    public int getHash() {
	return values.hashCode();
    }

    public void init(AvatarBlock _block) {
	LinkedList<AvatarAttribute> attrs = _block.getAttributes();
	TraceManager.addDev("Nb of attributes:" + attrs.size());
	TraceManager.addDev("in block=" + _block.toString());
	values = new int[HEADER_VALUES+attrs.size()];

	// Initial state
	values[STATE_INDEX] = _block.getIndexOfStartState();
	
	// Clock
	values[CLOCKMIN_INDEX] = 0;
	values[CLOCKMAX_INDEX] = 0;

	// Attributes
	int cpt = HEADER_VALUES;
	String initial;
	for(AvatarAttribute attr: attrs) {
	    values[cpt++] = attr.getInitialValueInInt();
	}	
    }

    public String toString() {
	StringBuffer sb = new StringBuffer("Hash=");
	sb.append(getHash());
	for (int i=0; i<values.length; i++) {
	    sb.append(" ");
	    sb.append(values[i]);
	}
	return sb.toString();
    }
}

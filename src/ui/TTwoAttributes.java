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
 * Class TTwoAttributes
 * Correspondance between data of a Turtle modeling and graphical elements 
 * Creation: 13/12/2003
 * @version 1.0 13/12/2003
 * @author Ludovic APVRILLE
 * @see 
 */


package ui;

public class TTwoAttributes {
	
	public TClassInterface t1;
	public TClassInterface t2;
	public TAttribute ta1;
	public TAttribute ta2;
	
	public int t1id;
	public int t2id;
	public String ta1s;
	public String ta2s;
	
	public TTwoAttributes (TClassInterface _t1, TClassInterface _t2, TAttribute _ta1, TAttribute _ta2) {
		t1 = _t1;
		t2 = _t2;
		ta1 = _ta1;
		ta2 = _ta2;
	}
	
	public TTwoAttributes (int _t1id, int _t2id, String _ta1s, String _ta2s) {
		t1id = _t1id;
		t2id = _t2id;
		ta1s = _ta1s;
		ta2s = _ta2s;
	}
	
	public String toString() {
		if ((t1 == null) || (t2 == null) || (ta1 == null) || (ta2 == null)) {
			return "TClass/TObject of id " + t1 + "." + ta1s + " = " + "TClass/TObject of id " + t2 + "." + ta2s;
		} else {
			return t1.getValue() + "." + ta1.getId() + " = " + t2.getValue() + "." + ta2.getId();
		}
	}
        
        public String toShortString() {
            if ((t1 == null) || (t2 == null) || (ta1 == null) || (ta2 == null)) {
		return "TClass/TObject of id " + t1 + "." + ta1s + " = " + "TClass/TObject of id " + t2 + "." + ta2s;
            } else {
                if (ta1.getId().equals(ta2.getId())) {
                    return ta1.getId();
                }
		return t1.getValue() + "." + ta1.getId() + " = " + t2.getValue() + "." + ta2.getId();
            }
        }
}

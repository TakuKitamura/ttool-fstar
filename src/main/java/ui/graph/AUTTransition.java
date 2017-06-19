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




package ui.graph;

/**
   * Class AUTTransition
   * Creation : 16/09/2004
   ** @version 1.0 16/09/2004
   * @author Ludovic APVRILLE
 */
public class AUTTransition implements Comparable<AUTTransition> {

    public int origin;
    public int destination;
    public String transition;
    public AUTElement elt;
    public boolean isTau;

    public AUTTransition(int _origin, String _transition, int _destination) {
        origin = _origin;
        destination = _destination;
        transition = _transition;
    }

    public AUTTransition(String _origin, String _transition, String _destination) {
        origin = Integer.decode(_origin).intValue();
        destination = Integer.decode(_destination).intValue();
        transition = _transition;
    }

    public int compareTo( AUTTransition _t ) {
	if (origin != _t.origin) {
	    return -1;
	}
	if (destination != _t.destination) {
	    return -1;
	}
	if(elt != null) {
	    if (elt != _t.elt) {
		return -1;
	    } else {
		return 0;
	    }
	}
	
	return transition.compareTo(_t.transition);
    }

    public String toString() {
        return "(" + origin + " ," + transition + ", " + destination + ")";
    }

    public String getLabel() {
        int index0 = transition.indexOf("(");
        int index1 = transition.indexOf(")");
        String s;
        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            s = transition;
        } else {
            s = transition.substring(index0+1, index1);
        }

        index0 = s.indexOf("<");
        index1 = s.indexOf(">");

        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            //System.out.println("0 s=" + s);
            return s;
        }
        //System.out.println("1 s=" + s);
        return s.substring(0, index0);
    }

    public int getNbOfIntParameters() {
        int index0 = transition.indexOf("<");
        int index1 = transition.indexOf(">");
        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            return 0;
        }

        String s = transition.substring(index0+1, index1);
        String[] ss = s.split(",");

        int cpt = 0;
        int a;
        for(int i=0; i<ss.length; i++) {
            //System.out.println("ss[" + i + "] =" + ss[i]);
            try {
                a = Integer.decode(ss[i].trim()).intValue();
                //System.out.println(">" + ss[i] + "< This is an int!");
                cpt ++;
            } catch (Exception e) {
            }
        }
        return cpt;
    }

    public int getIntParameter(int _index) {
        int index0 = transition.indexOf("<");
        int index1 = transition.indexOf(">");
        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            return -1;
        }

        String s = transition.substring(index0+1, index1);
        String[] ss = s.split(",");

        int cpt = 0;
        int a;
        for(int i=0; i<ss.length; i++) {
            //System.out.println("gip ss[" + i + "] =" + ss[i]);
            try {
                a = Integer.decode(ss[i].trim()).intValue();
                if (cpt == _index) {
                    return a;
                }
                cpt ++;
            } catch (Exception e) {
            }
        }
        return -1;
    }

    public AUTTransition basicClone() {
	AUTTransition tr = new AUTTransition(origin, transition, destination);
	tr.isTau = isTau;
	return tr;
    }

    

}

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




package req.ebrdd;

import java.util.ArrayList;

/**
 * Class EBRDDComponent
 * Creation: 18/09/2009
 * @version 1.0 18/09/2009
 * @author Ludovic APVRILLE
 */
public abstract class EBRDDComponent extends EBRDDGeneralComponent implements Cloneable {
	protected int nbNext = 1; // -1 means more than 1
    protected ArrayList<EBRDDComponent> nexts;

    
    public EBRDDComponent(String _name, Object _referenceObject) {
		super(_name, _referenceObject);
        nexts = new ArrayList<EBRDDComponent>();
    }
    
    
    public EBRDDComponent getNextElement(int index) {
        if (index < nexts.size()) {
            return nexts.get(index);
        } else {
            return null;
        }
    }
    
    public int getNbNext() {
        return  nexts.size();
    }
    
    public int getNormalizedNbNext() {
        return  nbNext;
    }
    
    public ArrayList<EBRDDComponent> getNexts() {
        return nexts;
    }
	
	 public void addNext(EBRDDComponent _comp) {
        nexts.add(_comp);
    }
    
     public void addNext(int _index, EBRDDComponent _comp) {
        nexts.add(_index, _comp);
    }
	
	public void removeNext(int index) {
        nexts.remove(index);
    }
    
}
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
 * Class AvatarSimulationAsynchronousTransaction
 * Avatar: notion of asynchronous transaction in simulation
 * Creation: 24/01/2011
 * @version 1.0 24/01/2011
 * @author Ludovic APVRILLE
 * @see
 */


package avatartranslator.directsimulation;

import java.util.*;

import avatartranslator.*;
import myutil.*;

public class AvatarSimulationAsynchronousTransaction  {
  
	private Vector<String> parameters;
    private AvatarRelation relation;
	private int index;
	public AvatarSimulationTransaction firstTransaction;
	//public AvatarSimulationTransaction receivedTransaction;
	
    public AvatarSimulationAsynchronousTransaction(AvatarRelation _ar, int _index) {
		relation = _ar;
		index = _index;
		parameters = new Vector<String>();
    }
	
	public AvatarRelation getRelation() {
		return relation;
	}
	
	public void addParameter(String _s) {
		parameters.add(_s);
	}
	
	public int getNbOfParameters() {
		return parameters.size();
	}
	
	public Vector<String> getParameters() {
		return parameters;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		String blockName;
		if (firstTransaction != null) {
			blockName = firstTransaction.block.getName();
		} else {
			blockName = relation.block1.getName();
		}
		
		AvatarSignal sig1 = relation.getSignal1(index);
		AvatarSignal sig2 = relation.getSignal2(index);
		
		if ((sig1 == null) || (sig2 == null)) {
			return "?";
		}
		
		String ret = blockName + "." + sig1.getName()+"(";
		for(int i=0; i<parameters.size(); i++) {
			if (i !=0) {
				ret += ",";
			}
			ret += parameters.get(i);
		}
		ret += ") -> ";
		
		ret += relation.block2.getName() + "." + sig2.getName();
		
		
		return ret;
	}
	
	public String parametersToString() {
		String ret="(";
		for(int i=0; i<parameters.size(); i++) {
			if (i !=0) {
				ret += ",";
			}
			ret += parameters.get(i);
		}
		ret += ")";
		
		return ret;
	}
}
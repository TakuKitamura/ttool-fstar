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
* Class TClassBasicFIFO
* Creation: 23/05/2007
* @version 1.0 23/05/2007
* @author Ludovic APVRILLE
* @see 
*/

package translator;

import java.util.*;

public class TClassBasicFIFO extends TClassBuffer {
    
    public TClassBasicFIFO(String name) {
		super(name, true);
    }
	
    public void makeTClass() {
        //System.out.println("toto1");
        if ((paramInForExchange.size() == 0) && (paramOutForExchange.size() == 0)) {
            return;
        }
        
        //System.out.println("toto2");
        Param cpt = new Param("cpt", Param.NAT, "0");
        addParameter(cpt);
        
        ActivityDiagram ad = new ActivityDiagram();
        
        ADJunction adj = new ADJunction();
        ad.getStartState().addNext(adj);
        ad.add(adj);
        
        ADChoice adc = new ADChoice();
        ad.add(adc);
        adj.addNext(adc);
        
		Gate g1, g2;
        ADActionStateWithGate adag1, adag2;
        ADActionStateWithParam adap1, adap2;  
		
		for(String m1:paramInForExchange) {
			g1 = addNewGateIfApplicable(m1);
			adag1 = new ADActionStateWithGate(g1);
            adag1.setActionValue("");

            adap1 = new ADActionStateWithParam(cpt);
            adap1.setActionValue("cpt+1");
			
			adc.addNext(adag1);
			adc.addGuard("[]");
			adag1.addNext(adap1);
			adap1.addNext(adj);
			
			ad.add(adag1);
            ad.add(adap1);
		}
		
		for(String m2:paramOutForExchange) {
			g2 = addNewGateIfApplicable(m2);
			
			adag2 = new ADActionStateWithGate(g2);
            adag2.setActionValue("");
			
			adap2 = new ADActionStateWithParam(cpt);
            adap2.setActionValue("cpt-1");
			
			adc.addNext(adag2);
			adc.addGuard("[cpt>0]");
			adag2.addNext(adap2);
			adap2.addNext(adj);
            
            // adding components to AD
   
            ad.add(adag2);
            ad.add(adap2);
		}
        setActivityDiagram(ad);
    }
}  
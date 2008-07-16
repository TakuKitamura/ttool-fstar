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
 * Class TClassInfiniteFIFO
 * Creation: 23/05/2007
 * @version 1.0 23/05/2007
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;
 
import java.util.*;

public class TClassInfiniteFIFO extends TClassBuffer implements FIFOInfiniteAndGetSizeTClass, TClassEventCommon {


    public TClassInfiniteFIFO(String name) {
      super(name, true);
    }
    
    public int getNbPara() {
        return getNbParam();
    }

    public Gate getGateWrite() {
		return getGateByName(getParamInAt(0));
    }
    
    public Gate getGateRead() {
        return getGateByName(getParamOutAt(0));
    }
    
    public Gate getGateSize() {
        return getGateByName(getParamSizeAt(0));
    }
	
	public LinkedList getGates(ArrayList<String> list) {
		Gate g;
		LinkedList ll = new LinkedList();
		
		for(String m: list) {
			g = getGateByName(m);
			if (g != null) {
				ll.add(g);
			}
		}
        return ll;
	}
    
    public LinkedList getGatesWrite() {
		return getGates(paramInForExchange);	
    }
    
    public LinkedList getGatesRead() {
        return getGates(paramOutForExchange);	
    }
    
    public LinkedList getGatesSize() {
        return getGates(paramSizeForExchange);	
    }
    

    public void makeTClass() {
      Gate forward_0, forward_1, g;
        ADActionStateWithGate adag;
        //ADActionStateWithGate adagsize1, adagsize2, adagsize3;
        ADActionStateWithParam adac1, adac2, adac3;
        ADParallel adpar0, adpar1;
        ADStop adstop;
        ADJunction adj1, adj2, adj3, adj4, adj5, adj6, adj7;
        ADChoice adch1, adch2, adch3,adch6, adch7;
        Param params_in[] = new Param[getNbParam()];
		Param params_out[] = new Param[getNbParam()];
		Param index, index_r, nb;
        //String value;
        int i;
        ListIterator iterator;
        String action;

        ActivityDiagram ad = new ActivityDiagram();
        setActivityDiagram(ad);
        
        // Case where not input or output requests...
        if ((getParamInNb() == 0) || (getParamOutNb() == 0)) {
           adstop = new ADStop();
           ad.add(adstop);
           ad.getStartState().addNext(adstop);
           return;
        }
		
		for(i=0; i<getNbParam(); i++) {
			params_in[i] = new Param("pin__" + i, Param.NAT, "0");
			params_out[i] = new Param("pout__" + i, Param.NAT, "0");
			addParameter(params_in[i]);  
			addParameter(params_out[i]);
			
		}
        
        nb = new Param("nb", Param.NAT, "0");
        addParameter(nb);
        
        index = new Param("index", Param.NAT, "0");
        addParameter(index);
        
        index_r = new Param("index_r", Param.NAT, "0");
        addParameter(index_r);
        
        forward_0 = addNewGateIfApplicable("forward_0");
        forward_1 = addNewGateIfApplicable("forward_1");

        adpar0 = new ADParallel();
        adpar0.setValueGate("[forward_0, forward_1]");
        ad.add(adpar0);
        ad.getStartState().addNext(adpar0);


        // Left branch of the main parallel -> storing data in order
        adj1 = new ADJunction();
        ad.add(adj1);
        adpar0.addNext(adj1);
        
        adag = new ADActionStateWithGate(forward_0);
        ad.add(adag);
        action = "";
        for(i=0; i<getNbParam(); i++) {
          action += "?pin__" + i + ":nat";
        }
        action += "?index:nat";
        adag.setActionValue(action);
        adj1.addNext(adag);

        adpar1 = new ADParallel();
        adpar1.setValueGate("[]");
        ad.add(adpar1);
        adag.addNext(adpar1);
        
        adag = new ADActionStateWithGate(forward_1);
        ad.add(adag);
        action = "";
        for(i=0; i<getNbParam(); i++) {
          action += "!pin__" + i;
        }
        action += "!index";
        adag.setActionValue(action);
        adpar1.addNext(adag);

        adstop = new ADStop();
        ad.add(adstop);
        adag.addNext(adstop);
        
        adpar1.addNext(adj1);
        
        // Second branch -> interaction with external classes
        
        adj2 = new ADJunction();
        ad.add(adj2);
        adpar0.addNext(adj2);
        
        adch1 = new ADChoice();
        ad.add(adch1);
        adj2.addNext(adch1);

        // Notify -> to know whether an event is available, or not
        if (getParamSizeNb() >0) {
          adch2 = new ADChoice();
          ad.add(adch2);
          adch1.addNext(adch2);
          adch1.addGuard("[]");
          
          adj3 = new ADJunction();
          ad.add(adj3);
          adj3.addNext(adj2);
  
		  for(String m: paramSizeForExchange) { 
            g = addNewGateIfApplicable(m);
            adag = new ADActionStateWithGate(g);
            ad.add(adag);
            adag.setActionValue("!1");
            adch2.addNext(adag);
            adch2.addGuard("[nb>0]");
            adag.addNext(adj3);
            adag = new ADActionStateWithGate(g);
            ad.add(adag);
            adag.setActionValue("!0");
            adch2.addNext(adag);
            adch2.addGuard("[nb==0]");
            adag.addNext(adj3);
          }
        }
		
        // Sent event
        adch3 = new ADChoice();
        ad.add(adch3);
        adch1.addNext(adch3);
        adch1.addGuard("[]");

        adj4 = new ADJunction();
        ad.add(adj4);

       for(String m: paramInForExchange) { 
          g = addNewGateIfApplicable(m);
          adag = new ADActionStateWithGate(g);
          ad.add(adag);
          action = "";
          for(i=0; i<getNbParam(); i++) {
            action += "?pin__" + i + ":nat";
          }
          adag.setActionValue(action);
          adch3.addNext(adag);
          adch3.addGuard("[]");
          adag.addNext(adj4);
        }
        
        adag = new ADActionStateWithGate(forward_0);
        ad.add(adag);
        action = "";
        for(i=0; i<getNbParam(); i++) {
            action += "!pin__" + i;
        }
        action+="!index";
        adag.setActionValue(action);
        adj4.addNext(adag);

        adac1 = new ADActionStateWithParam(index);
        ad.add(adac1);
        adac1.setActionValue("index+1");
        adag.addNext(adac1);

        adj5 = new ADJunction();
        ad.add(adj5);
        adj5.addNext(adj2);
        
        adj6 = new ADJunction();
        ad.add(adj6);
        //adch4.addNext(adj6);
        //adch4.addGuard("[nb==maxs]");
        
        adag = new ADActionStateWithGate(forward_1);
        ad.add(adag);
        action = "";
        for(i=0; i<getNbParam(); i++) {
            action += "?pout__" + i + ":nat";
        }
        action += "!index_r";
        adag.setActionValue(action);
        adj6.addNext(adag);
        
        adac3 = new ADActionStateWithParam(index_r);
        ad.add(adac3);
        adac3.setActionValue("index_r+1");
        adag.addNext(adac3);
        adac3.addNext(adj5);

        adac2 = new ADActionStateWithParam(nb);
        ad.add(adac2);
        adac2.setActionValue("nb+1");
        adac1.addNext(adac2);

        adch6 = new ADChoice();
        ad.add(adch6);
        adac2.addNext(adch6);

        adch6.addNext(adj5);
        adch6.addGuard("[not(nb==1)]");
        adch6.addNext(adj6);
        adch6.addGuard("[nb==1]");

        // Wait event branch
        adj7 = new ADJunction();
        ad.add(adj7);
        
       for(String m: paramOutForExchange) { 
          g = addNewGateIfApplicable(m);
          adag = new ADActionStateWithGate(g);
          ad.add(adag);
          action = "";
          for(i=0; i<getNbParam(); i++) {
            action += "!pout__" + i + "";
          }
          adag.setActionValue(action);
          adch3.addNext(adag);
          adch3.addGuard("[nb>0]");
          adag.addNext(adj7);
        }
        
        adac1 = new ADActionStateWithParam(nb);
        ad.add(adac1);
        adac1.setActionValue("nb-1");
        adj7.addNext(adac1);

        adch7 = new ADChoice();
        ad.add(adch7);
        adac1.addNext(adch7);

        adch7.addNext(adj6);
        adch7.addGuard("[not(nb==0)]");
        adch7.addNext(adj5);
        adch7.addGuard("[nb==0]");
        
    }
}
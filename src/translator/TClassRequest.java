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
 * Class TClassRequest
 * Creation: 01/12/2005
 * @version 1.0 01/12/2005
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;
 
import java.util.*;

public class TClassRequest extends TClass implements FIFOTClass{
    
    private int nbPara;
    private LinkedList sendReqGates;
    private LinkedList waitGates;
    private int counterW, counterR;
    private String requestName;
    
    public TClassRequest(String name, String _requestName, int _nbPara) {
      super(name, true);
      nbPara = _nbPara;
      sendReqGates = new LinkedList();
      waitGates = new LinkedList();
      requestName = _requestName;
    }
    
    public int getNbPara() {
        return nbPara;
    }

    public Gate getGateWrite() {
        return (Gate)(sendReqGates.get(0));
    }
    
    public Gate getGateRead() {
        return (Gate)(waitGates.get(0));
    }
    
    public LinkedList getGatesWrite() {
        return sendReqGates;
    }
	
	public Gate getGateWrite(String endingName) {
		Gate g;
		endingName = "__" + endingName;
		
		for(int i=0; i<sendReqGates.size(); i++) {
			g = (Gate)(sendReqGates.get(i));
			if (g.getName().endsWith(endingName)) {
				return g;
			}
		}
		
		return null;
	}
    
    public LinkedList getGatesRead() {
        return waitGates;
    }
    
    public Gate addWriteGate() {
           Gate g;
           if (counterW == 0) {
              g = addNewGateIfApplicable("sendReq__" + requestName + counterW);
           } else {
             g = addNewGateIfApplicable("sendReq__" + requestName + counterW);
           }
           sendReqGates.add(g);
           counterW ++;
           return g;
    }
	
	public Gate addWriteGate(String name) {
           Gate g;
           g = addNewGateIfApplicable("sendReq__" + requestName + "__" + name);
           sendReqGates.add(g);
           return g;
    }

    public Gate addReadGate() {
           Gate g ;
           if (counterR == 0) {
           g = addNewGateIfApplicable("waitReq__" + requestName);
           } else {
             g = addNewGateIfApplicable("waitReq__" + requestName + counterR);
           }
           waitGates.add(g);
           counterR ++;
           return g;
    }

    public void makeTClass() {
        //System.out.println("toto1");
        

        Gate forward, g;
        ADActionStateWithGate adag, adag1, adag2;
        ADActionStateWithParam adac1, adac2;
        ADParallel adpar1, adpar2;
        ADStop adstop;
        ADJunction adj1, adj2, adj3, adj4;
        ADChoice adch1, adch2;
        Param p1, p2, p3, n;
        String value;
        int i;
        ListIterator iterator;
        String action;

        ActivityDiagram ad = new ActivityDiagram();
        setActivityDiagram(ad);
        
        // Case where not input or output requests...
        if ((sendReqGates.size() == 0) || (waitGates.size() == 0)) {
           adstop = new ADStop();
           ad.add(adstop);
           ad.getStartState().addNext(adstop);
           return;
        }

        p1 = new Param("p0", Param.NAT, "0");
        p2 = new Param("p1", Param.NAT, "0");
        p3 = new Param("p2", Param.NAT, "0");

        if (nbPara > 0) {
            addParameter(p1);
        }
         if (nbPara > 1) {
            addParameter(p2);
        }
        if (nbPara > 2) {
            addParameter(p3);
        }
        
        n = new Param("n", Param.NAT, "0");
        addParameter(n);
        
        forward = addNewGateIfApplicable("forward");
        
        adpar1 = new ADParallel();
        adpar1.setValueGate("[forward]");
        ad.add(adpar1);
        ad.getStartState().addNext(adpar1);

        // Left branch of the main parallel
        adj1 = new ADJunction();
        ad.add(adj1);
        adpar1.addNext(adj1);

        adch1 = new ADChoice();
        ad.add(adch1);
        adj1.addNext(adch1);

        // Requests "in"
        adj2 = new ADJunction();
        ad.add(adj2);
        iterator = sendReqGates.listIterator();
        while(iterator.hasNext()) {
          g = (Gate)(iterator.next());
          adag = new ADActionStateWithGate(g);
          ad.add(adag);
          action = "";
          for(i=0; i<nbPara; i++) {
            action += "?p" + i + ":nat";
          }
          adag.setActionValue(action);
          adch1.addNext(adag);
          adch1.addGuard("[]");
          adag.addNext(adj2);
        }

        adpar2 = new ADParallel();
        ad.add(adpar2);
        adj2.addNext(adpar2);

        adac1 = new ADActionStateWithParam(n);
        ad.add(adac1);
        adac1.setActionValue("n+1");
        adpar2.addNext(adac1);
        adac1.addNext(adj1);

        adag1 = new ADActionStateWithGate(forward);
        ad.add(adag1);
        action = "";
        for(i=0; i<nbPara; i++) {
          action += "!p" + i;
        }
        action += "!n";
        adag1.setActionValue(action);
        adpar2.addNext(adag1);
        
        adstop = new ADStop();
        ad.add(adstop);
        adag1.addNext(adstop);
        
        // Right branch of the main parallel
        adj3 = new ADJunction();
        ad.add(adj3);
        adpar1.addNext(adj3);

        adag2 = new ADActionStateWithGate(forward);
        ad.add(adag2);
        action = "";
        for(i=0; i<nbPara; i++) {
          action += "?p" + i + ":nat";
        }
        action += "!n";
        adag2.setActionValue(action);
        adj3.addNext(adag2);
        
        adch2 = new ADChoice();
        ad.add(adch2);
        adag2.addNext(adch2);
        
        adj4 = new ADJunction();
        ad.add(adj4);

        // Requests "out"
        
        iterator = waitGates.listIterator();
        while(iterator.hasNext()) {
          g = (Gate)(iterator.next());
          adag = new ADActionStateWithGate(g);
          ad.add(adag);
          action = "";
          for(i=0; i<nbPara; i++) {
            action += "!p" + i;
          }
          adag.setActionValue(action);
          adch2.addNext(adag);
          adch2.addGuard("[]");
          adag.addNext(adj4);
        }
        
        adac2 = new ADActionStateWithParam(n);
        ad.add(adac2);
        adac2.setActionValue("n+1");
        adj4.addNext(adac2);
        adac2.addNext(adj3);







        //wait = addNewGateIfApplicable("wait");
        //sendReq = addNewGateIfApplicable("sendReq");

        //sendReqGates.add(sendReq);
        //waitGates.add(wait);

        /*adj = new ADJunction();
        ad.getStartState().addNext(adj);
        ad.add(adj);

        acsend = new ADActionStateWithGate(sendReq);
        adj.addNext(acsend);
        ad.add(acsend);
        value = "";
        for(i=0; i<nbPara; i++) {
            value = value + "?p" + (i+1) + ":nat";
        }
        acsend.setActionValue(value);

        adpar = new ADParallel();
        adpar.setValueGate("[]");
        acsend.addNext(adpar);
        ad.add(adpar);
        adpar.addNext(adj);



        acwait = new ADActionStateWithGate(wait);
        value = "";
        for(i=0; i<nbPara; i++) {
            value = value + "!p" + (i+1);
        }
        acwait.setActionValue(value);
        ad.add(acwait);
        adpar.addNext(acwait);

        adstop = new ADStop();
        acwait.addNext(adstop);
        ad.add(adstop);

        setActivityDiagram(ad);*/
    }
}
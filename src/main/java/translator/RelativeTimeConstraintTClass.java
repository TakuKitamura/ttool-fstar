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
 * Class RelativeTimeConstraintTClass
 * Creation: 07/09/2004
 * @version 1.0 07/09/2004
 * @version 1.1 25/05/2007
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;
 


public  class RelativeTimeConstraintTClass extends TimeConstraintTClass {
    
    public RelativeTimeConstraintTClass(String name, int time1, int time2) {
	super(name, time1, time2);
    }
      
    protected void makeActivityDiagram(int time1, int time2) {
		
		// External gates
		g1 = new Gate("go_tc", Gate.GATE, false);
        g2 = new Gate("check_tc", Gate.GATE, false);
        g3 = new Gate("end_tc", Gate.GATE, false);
        g4 = new Gate("expire_tc", Gate.GATE, false);
        
        addGate(g1);
        addGate(g2);
        addGate(g3);
		addGate(g4);
		
		// Internal gates
        
		Gate go = new Gate("go", Gate.GATE, false);
		Gate end = new Gate("end", Gate.GATE, false);
		addGate(go);
		addGate(end);
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        setActivityDiagram(ad);
        
        // Building components
		
		ADParallel adp = new ADParallel();
		adp.setValueGate("[go, end]");
		
		// left branch
        ADJunction adj1 = new ADJunction();
        ADActionStateWithGate adg1 = new ADActionStateWithGate(g1);
		ADActionStateWithGate adg2 = new ADActionStateWithGate(go);
        ADDelay add1 = new ADDelay();
        add1.setValue(""+time1);
		ADTLO adtlo1 = new ADTLO(g2);
		adtlo1.setLatency("0");
		adtlo1.setDelay(""+(time2-time1));
		ADStop adstop1 = new ADStop();
		ADActionStateWithGate adg3 = new ADActionStateWithGate(g3);
		ADActionStateWithGate adg4 = new ADActionStateWithGate(end);
        
        // right branch
		ADJunction adj2 = new ADJunction();
		ADActionStateWithGate adg5 = new ADActionStateWithGate(go);
		ADChoice adch = new ADChoice();
		ADActionStateWithGate adg6 = new ADActionStateWithGate(end);
		ADDelay add2 = new ADDelay();
        add2.setValue(""+time2);
		ADActionStateWithGate adg7 = new ADActionStateWithGate(g4);
		adg7.setActionValue("{0}");
		ADStop adstop2 = new ADStop();
        
        // Connecting components
        ads.addNext(adp);
		adp.addNext(adj1);
		adp.addNext(adj2);
		
		adj1.addNext(adg1);  
		adg1.addNext(adg2);
		adg2.addNext(add1);
		add1.addNext(adtlo1);
		adtlo1.addNext(adg3);
		adtlo1.addNext(adstop1);
		adg3.addNext(adg4);
		adg4.addNext(adj1);
		
		adj2.addNext(adg5);  
		adg5.addNext(adch);
		adch.addNext(adg6);
		adg6.addNext(adj2);
		adch.addNext(add2);
		add2.addNext(adg7);
		adg7.addNext(adstop2);
		
        // adding components
        ad.add(adp);
		ad.add(adj1);
		ad.add(adj2);
		ad.add(adg1);
		ad.add(adg2);
		ad.add(adg3);
		ad.add(adg4);
		ad.add(adg5);
		ad.add(adg6);
		ad.add(adg7);
		ad.add(adtlo1);
		ad.add(add1);
		ad.add(add2);
		ad.add(adstop1);
		ad.add(adstop2);
		ad.add(adch);

		
        
        //System.out.println("Activity diagram of TC with time1=" + time1 + " and time2=" + time2);
        
        /*// Gates
        g1 = new Gate("begin_tc", Gate.GATE, false);
        g2 = new Gate("go_tc", Gate.GATE, false);
        g3 = new Gate("end_tc", Gate.GATE, false);
        g4 = g3;
        
        addGate(g1);
        addGate(g2);
        addGate(g3);
        
        // Counter
        cpt = addParameterGenerateName("cpt", Param.NAT, "0");
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        
        // Building components
        
        ADJunction adj = new ADJunction();
        
        ADActionStateWithGate adbegin = new ADActionStateWithGate(g1);
        adbegin.setActionValue("!cpt");
        
        ADDelay add1 = new ADDelay();
        add1.setValue(""+time1);
        
        ADLatency adlat = new ADLatency();
        adlat.setValue(""+(time2-time1));
        
        ADDelay add2 = new ADDelay();
        add2.setValue(""+time1); 
        
        ADParallel adp = new ADParallel();
        adp.setValueGate("[]");
        
        ADActionStateWithGate adgo = new ADActionStateWithGate(g2);
        adgo.setActionValue("{" + (time2-time1) + "}!cpt");
        
        ADActionStateWithGate adend = new ADActionStateWithGate(g3);
        adend.setActionValue("{" + (time2-time1) + "}!cpt");
        
        ADStop ads1 = new ADStop();
        ADStop ads2 = new ADStop();
        
        ADActionStateWithParam adincrement = new ADActionStateWithParam(cpt);
        adincrement.setActionValue(cpt.getName() + "+1");
        
        // Connecting components
        ads.addNext(adj);
        adj.addNext(adbegin);
        adbegin.addNext(adp);
        adp.addNext(add1);
        adp.addNext(add2);
        adp.addNext(adincrement);
        add1.addNext(adlat);
        adlat.addNext(adgo);
        add2.addNext(adend);
        adgo.addNext(ads1);
        adend.addNext(ads2);
        adincrement.addNext(adj);
 
        // adding components
        ad.add(adj);
        ad.add(adbegin);
        ad.add(add1);
        ad.add(adlat);
        ad.add(add2);
        ad.add(adincrement);
        ad.add(adp);
        ad.add(adgo);
        ad.add(adend);
        ad.add(ads1);
        ad.add(ads2);*/
    }
}  
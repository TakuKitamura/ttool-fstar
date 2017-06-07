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
 * Class AbsoluteTimeConstraintTClass
 * Creation: 07/09/2004
 * @version 1.0 07/09/2004
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;
 


public class AbsoluteTimeConstraintTClass extends TimeConstraintTClass {
    
    public AbsoluteTimeConstraintTClass(String name, int time1, int time2) {
	super(name, time1, time2);
    }
      
    protected void makeActivityDiagram(int time1, int time2) {
        
		// Gates
		g1 = new Gate("internal_stop_tc", Gate.GATE, true);
        g2 = new Gate("go_tc", Gate.GATE, false);
        g3 = new Gate("end_tc", Gate.GATE, false);
        g4 = new Gate("expire_tc", Gate.GATE, false);
        
		addGate(g1);
        addGate(g2);
        addGate(g3);
        addGate(g4);
        
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        
        //Building components
        
		ADPreempt adp = new ADPreempt();
		ads.addNext(adp);
		
		// Left branch of preempt
        
        ADDelay add1 = new ADDelay();
        add1.setValue(""+time1);
		adp.addNext(add1);
		
		ADJunction adj = new ADJunction();
		add1.addNext(adj);
		
		ADActionStateWithGate adgo1 = new ADActionStateWithGate(g2);
		adj.addNext(adgo1);
		ADActionStateWithGate adgo2 = new ADActionStateWithGate(g3);
		adgo1.addNext(adgo2);
		adgo2.addNext(adj);
		
		// Right branch of preempt
        
        ADDelay add2 = new ADDelay();
        add2.setValue(""+time2);
		adp.addNext(add2);
		
		ADActionStateWithGate adgo3 = new ADActionStateWithGate(g1);
		add2.addNext(adgo3);
		
		
		ADChoice adch = new ADChoice();
		adgo3.addNext(adch);
		
		ADActionStateWithGate adgo4 = new ADActionStateWithGate(g3);
		adch.addNext(adgo4);
		
		ADStop adstop1 = new ADStop();
		adgo4.addNext(adstop1);
		
		ADActionStateWithGate adgo5 = new ADActionStateWithGate(g4);
		adch.addNext(adgo5);
		
		ADStop adstop2 = new ADStop();
		adgo5.addNext(adstop2);
		
        
        // adding components
        ad.add(adp);
        
		ad.add(adj);
		ad.add(add1);
		ad.add(adgo1);
		ad.add(adgo2);
		ad.add(add2);
		ad.add(adgo3);
		ad.add(adch);
        ad.add(adgo4);
		ad.add(adgo5);
        
        ad.add(adstop1);
		ad.add(adstop2);
       
		
        /*// Gates
        //g1 = new Gate("begin_tc", Gate.GATE, false);
        g2 = new Gate("go_tc", Gate.GATE, false);
        g3 = new Gate("end_tc", Gate.GATE, false);
        //g4 = new Gate("expire_tc", Gate.GATE, false);
        
        //addGate(g1);
        addGate(g2);
        addGate(g3);
        //addGate(g4);
        
        
        // Counter
        cpt = addParameterGenerateName("cpt", Param.NAT, "0");
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        
        //Building components
        
        ADJunction adj = new ADJunction();
        
        ADDelay add1 = new ADDelay();
        add1.setValue(""+time1);
        
        ADDelay add2 = new ADDelay();
        add2.setValue(""+time1);
        
        ADParallel adp = new ADParallel();
        adp.setValueGate("[]");
        
        ADLatency adlat = new ADLatency();
        adlat.setValue(""+(time2-time1));
        
        ADActionStateWithGate adgo = new ADActionStateWithGate(g2);
        adgo.setActionValue("{" + (time2-time1) + "}!cpt");
        
        ADActionStateWithGate adend = new ADActionStateWithGate(g3);
        adend.setActionValue("{" + (time2-time1) + "}!cpt");
        
        ADActionStateWithParam adincrement = new ADActionStateWithParam(cpt);
        adincrement.setActionValue(cpt.getName() + "+1");
        
        ADChoice adch = new ADChoice();
        adch.addGuard("[cpt<10]");
        adch.addGuard("[cpt==10]");
        
        ADStop ads1 = new ADStop();
        ADStop ads2 = new ADStop();
        ADStop ads3 = new ADStop();        
        
        // Connecting components
        ads.addNext(adj);
        adj.addNext(adp);
        
        adp.addNext(adincrement);
        adincrement.addNext(adch);
        adch.addNext(adj);
        adch.addNext(ads1);
        
        adp.addNext(add1);
        add1.addNext(adlat);
        adlat.addNext(adgo);
        adgo.addNext(ads2);
        
        adp.addNext(add2);
        add2.addNext(adend);
        adend.addNext(ads3);
        
        // adding components
        ad.add(adj);
        ad.add(adp);
        
        ad.add(adincrement);
        ad.add(adch);
        ad.add(ads1);
        
        ad.add(add1);
        ad.add(adlat);
        ad.add(adgo);
        ad.add(ads2);
        
        ad.add(add2);
        ad.add(adend);
        ad.add(ads3);*/
        
        /* Gates*/
        /*g2 = new Gate("end_tc", Gate.GATE, false);
        g1 = g2;
      
        addGate(g2); 
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        

        
        ADTimeInterval adti = new ADTimeInterval();
        adti.setValue(""+time1, ""+time2);
        
        ADTLO adtlo = new ADTLO(g2);
        adtlo.setLatency("0");
        adtlo.setDelay(""+ (time2-time1));
        adtlo.setAction("");
        
        ADStop ads1 = new ADStop();
        ADStop ads2 = new ADStop();
        
 
        ads.addNext(adti);
        adti.addNext(adtlo);
        adtlo.addNext(ads1);
        adtlo.addNext(ads2);
 
        ad.add(adti);
        ad.add(adtlo);
        ad.add(ads1);*/
        
        
    }
}  
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
 * Class BasicTimer
 * Creation: 07/09/2004
 * @version 1.0 07/09/2004
 * @author Ludovic APVRILLE
 * @see 
 */

package translator;


public  class BasicTimer extends TClass {
    private Gate set, reset, exp;
    private String instanceSet, instanceReset, instanceExp;
    
    public BasicTimer(String name, boolean isActive) {
	super(name, isActive);
        makeActivityDiagram();
    }
    
    public void setInstanceSet(String ins) {
        instanceSet = ins;
    }
    
    public void setInstanceReset(String ins) {
        instanceReset = ins;
    }
    
    public void setInstanceExp(String ins) {
        instanceExp = ins;
    }
    
    public static String makeTimerSetGate(String s, TClass t) {
        return t.getName() + "__" + s + "__set"; 
    }
    
    public static String makeTimerResetGate(String s, TClass t) {
        return t.getName() + "__" + s + "__reset"; 
    }
    
    public static String makeTimerExpGate(String s, TClass t) {
        return t.getName() + "__" + s + "__exp"; 
    }
    
    public static String makeTimerTClassName(String s) {
        return "Timer__" + s;
    }
    
    public Gate getGateSet() { return set;}
    public Gate getGateReset() { return reset;}
    public Gate getGateExp() { return exp;}
    public String getInstanceSet() {return instanceSet;}
    public String getInstanceReset() {return instanceReset;}
    public String getInstanceExp() {return instanceExp;}
      
    private void makeActivityDiagram() {
        /* Parameter */
        Param delay = new Param("d", Param.NAT, "0");
        addParameter(delay);
        
        /* Gates*/
        set = new Gate("set", Gate.GATE, false);
        reset = new Gate("reset", Gate.GATE, false);
        exp = new Gate("exp", Gate.GATE, false);
        
        addGate(set);
        addGate(reset);
        addGate(exp);
        
        
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        
        /* Building components */
        
        ADJunction adj = new ADJunction();
        
        ADActionStateWithGate adset = new ADActionStateWithGate(set);
        adset.setActionValue("?d:nat");
        
        ADActionStateWithGate adreset = new ADActionStateWithGate(reset);
        adreset.setActionValue("");
        
        ADActionStateWithGate adexp = new ADActionStateWithGate(exp);
        adexp.setActionValue("");
        
        ADChoice adc = new ADChoice();
        
        ADDelay add = new ADDelay();
        add.setValue("d");
        
        /* Connecting components */
        ads.addNext(adj);
        adj.addNext(adset);
        adset.addNext(adc);
        adc.addNext(adreset);
        adc.addNext(add);
        adc.addGuard("[]");
        adc.addGuard("[]");
        adreset.addNext(adj);
        add.addNext(adexp);
        adexp.addNext(adj); 
        
        /* adding components */
        ad.add(adj);
        ad.add(adset);
        ad.add(adc);
        ad.add(adreset);
        ad.add(add);
        ad.add(adexp);
    }
    
    
    public void removeReset() {
        System.out.println("Modifying activity diagram -> no reset");
        ADStart ads = new ADStart();
        ActivityDiagram ad = new ActivityDiagram(ads);
        
        setActivityDiagram(ad);
        
        /* Building components */
        
        ADJunction adj = new ADJunction();
        
        ADActionStateWithGate adset = new ADActionStateWithGate(set);
        adset.setActionValue("?d:nat");
        
        ADActionStateWithGate adexp = new ADActionStateWithGate(exp);
        adexp.setActionValue("");
        
        ADDelay add = new ADDelay();
        add.setValue("d");
        
        /* Connecting components */
        ads.addNext(adj);
        adj.addNext(adset);
        adset.addNext(add);
        add.addNext(adexp);
        adexp.addNext(adj); 
        
        /* adding components */
        ad.add(adj);
        ad.add(adset);
        ad.add(add);
        ad.add(adexp);
    }
}  
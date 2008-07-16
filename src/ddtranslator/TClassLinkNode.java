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
 * Class TClassLinkNode
 * Creation: 01/06/2005
 * @version 1.0 01/06/2005
 * @author Ludovic APVRILLE
 * @see 
 */

package ddtranslator;
 


import myutil.*;
import translator.*;

public class TClassLinkNode extends TClass {
    private static int id = 0;
    
    private String delay = "10";
    
    private ADParallel mainpar;
    private int idGate = 0;
    
    public TClassLinkNode(String name) {
	super(name, true);
    }
    
    public static String generateName() {
        id ++;
        return "Link__" + (id-1);
    }
    
    public int getIdGate() {
        int ret = idGate;
        idGate ++;
        return (idGate - 1);
    }
    
    public static void reinitName() {
        id = 0;
    }

    public void prepareTClass() {
        ActivityDiagram ad = new ActivityDiagram();
        mainpar = new ADParallel();
        ad.getStartState().addNext(mainpar);
        ad.add(mainpar);
        setActivityDiagram(ad);
    }
    
    /*public Gate addInputGate(String name) {
        
    }
    
    public Gate addOutputGate(String name) {
        
    }*/
    
    public void addGateManagement(Gate g1, String call, Gate g2) {
        addGateManagement(g1, call, g2, delay);
    }
    
    public void addGateManagement(Gate g1, String call, Gate g2, String delay) {
        ActivityDiagram ad = getActivityDiagram();
        ADJunction adj1 = new ADJunction();
        
        //System.out.println("Call=" + call);
        ADActionStateWithGate ad1 = new ADActionStateWithGate(g1);
        ad1.setActionValue(call);
        
        ADParallel para = new ADParallel();
        
        ADDelay addelay = new ADDelay();
        addelay.setValue(delay);
        
        ADActionStateWithGate ad2 = new ADActionStateWithGate(g2);
        call = Conversion.replaceAllChar(call, '?', "!");
        call = Conversion.replaceOp(call, ":nat", "");
        //System.out.println("Call=" + call);
        ad2.setActionValue(call);
        
        ADStop adstop = new ADStop();
        
        mainpar.addNext(adj1);
        adj1.addNext(ad1);
        ad1.addNext(para);
        para.addNext(adj1);
        para.addNext(addelay);
        addelay.addNext(ad2);
        ad2.addNext(adstop);
        
        ad.add(adj1);
        ad.add(ad1);
        ad.add(para);
        ad.add(addelay);
        ad.add(ad2);
        ad.add(adstop);
    }
    
    public void finishTClass() {
        // Parallel with more than 5 nexts -> manage it !
       
    }
    
    public int getNbNext() {
        return mainpar.getNbNext();
    }
    
    public void makeBoolParameters(int nb) {
        for(int i=0; i<nb; i++) {
            addNewParamIfApplicable("b" + i, Param.BOOL, "false");
        }
    }
    
    public void makeNatParameters(int nb) {
        for(int i=0; i<nb; i++) {
            addNewParamIfApplicable("x" + i, Param.NAT, "0");
        }
    }
    
    public void setDelay(String _delay) {
        delay = _delay;
    }
}  
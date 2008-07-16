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
 * Class DDTranslator
 * Creation: 31/05/2004
 * @version 1.0 31/05/2004
 * @author Ludovic APVRILLE
 * @see
 */

package ddtranslator;


import java.util.*;


import translator.*;
import ui.*;
import ui.cd.*;
import ui.dd.*;

public class DDTranslator {
    
    private TURTLEModeling tm;
    private DeploymentPanel dp;
    private CorrespondanceTGElement listE;
    
    public DDTranslator(DeploymentPanel _dp, TURTLEModeling _tm, CorrespondanceTGElement _listE) {
        dp = _dp;
        tm = _tm;
        listE = _listE;
    }
    
    
    public void translateLinks() throws DDSyntaxException {
        
        // We go throughout links
        LinkedList ll;
        ListIterator iterator;
        TDDNode node;
        TGConnectorLinkNode link;
        VectorLRArtifactTClassGate assocs;
        LRArtifactTClassGate lratg;
        TClassLinkNode t;
        TClass tcl;
        int i;
        
        ll = dp.tddp.getListOfLinks();
        iterator = ll.listIterator();
        
        TClassLinkNode.reinitName();
        
        // Loop on links
        while(iterator.hasNext()) {
            link = (TGConnectorLinkNode)(iterator.next());
            assocs = link.getList();
            System.out.println("assocs=" + assocs);
            if (assocs.size() > 0) {
                System.out.println("translateLinks : assocs > 0");
                t = new TClassLinkNode(TClassLinkNode.generateName());
                t.setDelay(link.getDelay());
                tm.addTClass(t);
                t.prepareTClass();
                for(i=0; i<assocs.size(); i++) {
                    lratg = assocs.getElementAt(i);
                    makeSynchro(link, lratg, t, link.getOriginNode(), link.getDestinationNode());
                }
                t.finishTClass();
            }
        }
        
        // Loop on TClassLinkNode
       /* for(i=0; i<tm.classNb(); i++) {
            tcl = getTClassAtIndex(i);
            if (tcl instanceof TClassLinkNode) {
                t = (TClassLinkNode)tcl;
        
            }
        }*/
    }
    
    private void makeSynchro(TGConnectorLinkNode link, LRArtifactTClassGate lratg, TClassLinkNode t, TDDNode node1, TDDNode node2) {
        // Find all possible receiving at destination side
        TCDTClass gtclass, gtclass2;
        DDStructSynchro synchro;
        ActivityDiagram ad;
        int i;
        ADComponent adc;
        Gate g = null;
        String actionOnGate = null;
        int maxBool = 0;
        int maxNat = 0;
        int id;
        
        System.out.println("Making synchro");

        // Prepare struct
        DDStructLink ddsl = new DDStructLink();
        
        gtclass = findTClass(lratg.left.art, lratg.left.tcl);
        TClass tclass1 = listE.getTClass(gtclass);
        
        gtclass2 = findTClass(lratg.right.art, lratg.right.tcl);
        TClass tclass2 = listE.getTClass(gtclass2);
        
        System.out.println("Making synchro: step 1 gtclass="+ gtclass.getClassName() + "gtclass2="+ gtclass2.getClassName());
        
        if ((tclass1 == null) || (tclass2 == null)) {
            return;
        }
        
        System.out.println("Making synchro: step 1 tclass1="+ tclass1.getName() + "tclass2="+ tclass2.getName());
        
        
        ddsl.lgate = tclass1.getGateByName(lratg.left.gat);
        ddsl.rgate = tclass2.getGateByName(lratg.right.gat);
        
        if ((ddsl.lgate == null) || (ddsl.rgate == null)) {
            return;
        }
        
        System.out.println("Making synchro: step 2");
        
        // Create gates for the link;
        id = t.getIdGate();
        ddsl.linklg = t.addNewGateIfApplicable("g_l_" + id);
        ddsl.linkrg = t.addNewGateIfApplicable("g_r_" + id);
        
        // Set the right protocol on gates
        ddsl.lgate.setProtocolJava(link.getImplementation());
        ddsl.lgate.setLocalPortJava(link.getOriginPort());
        ddsl.lgate.setDestPortJava(link.getDestinationPort());
        ddsl.lgate.setDestHostJava(tclass2.getName().substring(0, tclass2.getName().indexOf('_')));
        ddsl.lgate.setLocalHostJava(tclass1.getName().substring(0, tclass1.getName().indexOf('_')));
        ddsl.rgate.setProtocolJava(link.getImplementation());
        ddsl.rgate.setLocalPortJava(link.getDestinationPort());
        ddsl.rgate.setDestPortJava(link.getOriginPort());
        ddsl.rgate.setDestHostJava(tclass1.getName().substring(0, tclass1.getName().indexOf('_')));
        ddsl.rgate.setLocalHostJava(tclass2.getName().substring(0, tclass2.getName().indexOf('_')));
        
        System.out.println("*** -> Protocol = " + link.getImplementation());
        System.out.println("*** -> hosts = host1=" + ddsl.lgate.getDestHostJava() + " host2=" +  ddsl.rgate.getDestHostJava());
        
        System.out.println("Toto01 -> looking for gate " + lratg.left.gat);
  
        // Analyse Tclass1
        
        System.out.println("Toto02");
        LinkedList synchros = new LinkedList();
        ad = tclass1.getActivityDiagram();
        
        for(i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.elementAt(i));
            g = null;
            if (adc instanceof ADActionStateWithGate) {
                g = ((ADActionStateWithGate)adc).getGate();
                actionOnGate = ((ADActionStateWithGate)adc).getActionValue();
            }
            if (adc instanceof ADTLO) {
                g = ((ADTLO)adc).getGate();
                actionOnGate = ((ADTLO)adc).getAction();
            }
            if (g != null) {
                if (g.getName().compareTo(lratg.left.gat) == 0) {
                    System.out.println("Gate=" + g.getName() + " action on gate=" + actionOnGate);
                    synchro = new DDStructSynchro(actionOnGate, tclass1);
                    if ((!(synchro.isInList(synchros))) && (synchro.size() > 0)) {
                        synchros.add(synchro);
                        
                        maxBool = Math.max(synchro.nbBool(), maxBool);
                        maxNat = Math.max(synchro.nbNat(), maxNat);
                        
                        System.out.println("Adding gate management");
                        ddsl.added = true;
                        t.addGateManagement(ddsl.linkrg, synchro.getRegularCall(), ddsl.linklg);
                    }
                }
            }
        }
        
        // Do the same at receiving side
        
        //System.out.println("Toto03");
        synchros = new LinkedList();
        ad = tclass2.getActivityDiagram();
        
        for(i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.elementAt(i));
            g = null;
            if (adc instanceof ADActionStateWithGate) {
                g = ((ADActionStateWithGate)adc).getGate();
                actionOnGate = ((ADActionStateWithGate)adc).getActionValue();
            }
            if (adc instanceof ADTLO) {
                g = ((ADTLO)adc).getGate();
                actionOnGate = ((ADTLO)adc).getAction();
            }
            if (g != null) {
                if (g.getName().compareTo(lratg.right.gat) == 0) {
                    synchro = new DDStructSynchro(actionOnGate, tclass2);
                    if ((!(synchro.isInList(synchros))) && (synchro.size() > 0)) {
                        synchros.add(synchro);
                        
                        maxBool = Math.max(synchro.nbBool(), maxBool);
                        maxNat = Math.max(synchro.nbNat(), maxNat);
                        
                        ddsl.added = true;
                        //System.out.println("Adding gate management");
                        t.addGateManagement(ddsl.linklg, synchro.getRegularCall(), ddsl.linkrg);
                    }
                }
            }
        }
        
        
        // Add necessary parameters to the tclass
        t.makeBoolParameters(maxBool);
        t.makeNatParameters(maxNat);
        
        // Add synchro relations
        if (ddsl.added == true) {
            //System.out.println("Adding synchro relation g1_save = " + g1_save + " g1=" + g1);
            //System.out.println("Adding synchro relation g1_save = " + g2_save + " g3=" + g3);
            tm.addSynchroRelation(tclass1, ddsl.lgate, t, ddsl.linklg);
            tm.addSynchroRelation(t, ddsl.linkrg, tclass2, ddsl.rgate);
        }      
    }
    
    private TCDTClass findTClass(String artifact, String tclass) {
        DesignPanel dpan = dp.tddp.getGUI().getDesignPanel(artifact);
        if (dpan == null) {
            return null;
        }
        
        return dpan.getTCDTClass(tclass);
    }
    
    
    
    
}
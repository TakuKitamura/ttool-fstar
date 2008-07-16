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
 * Class MasterGateManager
 * Creation: 10/12/2003
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;


public class MasterGateManager {
    
    private Vector groups; // GroupsOfGates
    private Vector master;
    private Vector forbiddenNames;
    private static LinkedList topMaster = new LinkedList();
    
    public MasterGateManager(TURTLEModeling tm) {
        topMaster.add(this);
        groups = new Vector();
        master = new Vector();
        forbiddenNames = new Vector();
        initGates(tm, false);
        generateGates();
    }
    
    // Observer
    public MasterGateManager() {
        groups = new Vector();
        master = new Vector();
        forbiddenNames = new Vector();
    }
    
    public static void reinitNameRestriction() {
        topMaster = new LinkedList();
    }
    
    public int getTotalGateNumber() {
        MasterGateManager mgm;
        int cpt = 0;
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            cpt += mgm.nbMasterGate();
        }
        return cpt;
    }
    
    public Gate getGate(String name) {
        MasterGateManager mgm;
        int i;
        Gate g;
        
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            for(i=0; i<mgm.nbMasterGate(); i++) {
                //System.out.println("i:" + i + "k:" + k);
                g = mgm.getMasterGateAtIndex(i);
                if ((g.getName()).equals(name)) {
                    return g;
                }
            }
        }
        return null;
    }
    
    public int nbOfPossibleGatesLowerCase(String name) {
        MasterGateManager mgm;
        int i;
        Gate g;
        int cpt = 0;
        
        name = name.toLowerCase();
        
        ListIterator ite = topMaster.listIterator();
        
        while(ite.hasNext()) {
            mgm = (MasterGateManager)(ite.next());
            for(i=0; i<mgm.nbMasterGate(); i++) {
                //System.out.println("i:" + i + "k:" + k);
                g = mgm.getMasterGateAtIndex(i);
                if ((g.getName().toLowerCase()).equals(name)) {
                    cpt ++;
                }
            }
        }
        return cpt;
    }
    
    public Hashtable getGatesUpperCaseHashTable() {
      Hashtable ht = new Hashtable();
      MasterGateManager mgm;
        int i;
        Gate g;

        ListIterator ite = topMaster.listIterator();
        while(ite.hasNext()) {
            mgm = (MasterGateManager)(ite.next());
            for(i=0; i<mgm.nbMasterGate(); i++) {
                //System.out.println("i:" + i + "k:" + k);
                g = mgm.getMasterGateAtIndex(i);
                ht.put(g.getName().toUpperCase(), g);
            }
        }
        return ht;
    }

    
    public Gate getGateLowerCase(String name) {
        MasterGateManager mgm;
        int i;
        Gate g;
        
        name = name.toLowerCase();
        ListIterator ite = topMaster.listIterator();
        while(ite.hasNext()) {
            mgm = (MasterGateManager)(ite.next());
            for(i=0; i<mgm.nbMasterGate(); i++) {
                //System.out.println("i:" + i + "k:" + k);
                g = mgm.getMasterGateAtIndex(i);
                if ((g.getName().toLowerCase()).equals(name)) {
                    return g;
                }
            }
        }
        return null;
    }
    
    public GroupOfGates getGroupOfGatesByGate(Gate g) {
        Gate tmp;
        MasterGateManager mgm;
        int i;
        
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            for(i=0; i<mgm.nbMasterGate(); i++) {
                tmp = mgm.getMasterGateAtIndex(i);
                if (tmp == g) {
                    return mgm.getGroupOfGates(i);
                }
            }
        }
        
        return null;
    }
    
    public GroupOfGates getMasterGroupOfGates(int index) {
        MasterGateManager mgm;
        int cpt = 0;
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            cpt = mgm.nbMasterGate();
            if (index < cpt) {
                return mgm.getGroupOfGates(index);
            } else {
                index = index - cpt;
            }
            
        }
        return null;
    }
    
    public int getMasterIndexOf(GroupOfGates gog) {
        MasterGateManager mgm;
        GroupOfGates gog1;
        int cpt = 0;
        int i;
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            for(i=0; i<groups.size(); i++) {
                gog1 = (GroupOfGates)(groups.elementAt(i));
                if (gog1 == gog) {
                    return cpt;
                }
                cpt ++;
            }
        }
        return -1;
    }
    
    public GroupOfGates getGroupOfGates(int index) {
        return (GroupOfGates)(groups.elementAt(index));
    }
    
    public MasterGateManager(TURTLEModeling tm, boolean startTakenIntoAccount) {
        topMaster.add(this);
        groups = new Vector();
        master = new Vector();
        forbiddenNames = new Vector();
        initGates(tm, startTakenIntoAccount);
        generateGates();
    }
    
    public MasterGateManager(TURTLEModeling tm, Vector forbidden) {
        topMaster.add(this);
        groups = new Vector();
        master = new Vector();
        forbiddenNames = forbidden;
        initGates(tm, false);
        generateGates();
    }
    
    private void initGates(TURTLEModeling tm, boolean start) {
        TClass tc, tmp_tc;
        Gate g, tmp_g;
        Vector list1, list2;
        Relation r;
        GroupOfGates gog, gog1, gog2;
        
        //System.out.println("initGate");
        
        for(int i=0; i<tm.classNb(); i++) {
            tc = tm.getTClassAtIndex(i);
            if ((!start) || (start && tc.isActive())) {
                // for each public gate, we add it a a group of gates if not connected a a gate already added
                list1 = tc.getGateList();
                for(int j=0; j<list1.size(); j++) {
                    g = (Gate)(list1.elementAt(j));
                    //System.out.println("Tclass:" + tc.getLotosName() + " Gate:" + g.getLotosName());
                    
                    //if (!g.isInternal()) {
                    // look for synchro relation with the gate
                    int cpt = 0;
                    for(int k=0; k<tm.relationNb(); k++) {
                        r = tm.getRelationAtIndex(k);
                        tmp_g = r.correspondingGate(g, tc);
                        if (tmp_g != null) {
                            tmp_tc = r.otherTClass(tc);
                            cpt ++;
                            //System.out.println("corresponding Tclass:" + tmp_tc.getLotosName() + " Gate:" + tmp_g.getLotosName());
                            gog1 = groupOf(tc, g);
                            gog2 = groupOf(tmp_tc, tmp_g);
                            //System.out.println("gog nb:" + groups.size());
                            //System.out.println("2 gog");
                            
                            if ((gog1 == null) && (gog2 == null)) {
                                //System.out.println("gog case 1");
                                gog = addToANewGroup(tc, g);
                                gog.addTClassGate(tmp_tc, tmp_g);
                            }
                            
                            if ((gog1 != null) && (gog2 == null)) {
                                //System.out.println("gog case 2");
                                gog1.addTClassGate(tmp_tc, tmp_g);
                            }
                            
                            if ((gog1 == null) && (gog2 != null)) {
                                //System.out.println("gog case 3");
                                gog2.addTClassGate(tc, g);
                            }
                            
                            if ((gog1 != null) && (gog2 != null)) {
                                if (gog1 != gog2) {
                                    //System.out.println("gog case 4");
                                    merge(gog1, gog2);
                                    //System.out.println("end gog case 4");
                                }
                            }
                            
                        } else {
                            gog1 = groupOf(tc, g);
                            if (gog1 == null) {
                                addToANewGroup(tc, g);
                            }
                            cpt ++;
                        }
                    }
                    if (cpt ==0) {
                        addToANewGroup(tc, g);
                    }
                    //}
                }
            }
        }
    }
    
    private void addForbiddenNames(Vector gates) {
        forbiddenNames.addElement(gates);
    }
    
    
    private void generateGates() {
        GroupOfGates gog;
        TClass tc;
        Gate g, master;
        String s;
        int i;
        
        for(i=0; i<groups.size(); i++) {
            gog = (GroupOfGates)(groups.elementAt(i));
            g = gog.getGateAt(0);
            master = createNewGate(g);
            gog.setMasterGate(master);
        }
    }
    
    private Gate createNewGate(Gate g) {
        String s = g.getLotosName();
        if (gateNameAlreadyInUse(s) || RTLOTOSKeyword.isAKeyword(s)){
            s = generateGateName(s);
        }
        //Gate new_g = new Gate(s, g.GATE, false);
        Gate new_g = new Gate(s, g.GATE, g.isInternal());
        new_g.setLotosName(s);
        master.add(new_g);
        return new_g;
    }
    
    private String generateGateName(String s) {
        String name;
        int gateId = 0;
        
        while(gateId>-1) {
            name = s + "_" + gateId;
            if (!gateNameAlreadyInUse(name) && !RTLOTOSKeyword.isAKeyword(name)) {
                return name;
            }
            gateId ++;
            //System.out.println("Gate Id:" +gateId);
        }
        return "WRONG NAME";
    }
    
    public boolean gateNameAlreadyInUse(String s) {
        MasterGateManager mgm;
        boolean b;
        for(int k=0; k<topMaster.size(); k++) {
            mgm = (MasterGateManager)(topMaster.get(k));
            b = mgm.nameInUse(s);
            if (b) {
                return true;
            }
        }
        
        return false;
        //return nameInUse(s);
    }
    
    private boolean nameInUse(String s) {
        Gate g1, g2;
        
        for(int i=0; i<master.size(); i++) {
            g1 = (Gate)(master.elementAt(i));
            if ((g1.getLotosName() != null) && (s != null)){
               if (g1.getLotosName().equals(s)) {
                return true;
                }
            }
        }
        
        for(int j=0; j<forbiddenNames.size(); j++) {
            g2 = (Gate)(forbiddenNames.elementAt(j));
            if ((g2.getLotosName() != null) && (s != null)){
               if (g2.getLotosName().equals(s)) {
                  return true;
               }
            }
        }
        
        return false;
    }
    
    public Gate getMasterGateAtIndex(int i) {
        return (Gate)(master.elementAt(i));
    }
    
    public int nbMasterGate() {
        return master.size();
    }
    
    
    public int nbVisibleMasterGate() {
        Gate g;
        int cpt =0;
        for(int i=0; i<master.size(); i++) {
            g = (Gate)(master.elementAt(i));
            if (!g.isInternal()) {
                cpt ++;
            }
        }
        return cpt;
    }
    
    public Vector getAllMasterGates() {
        return master;
    }
    
    public GroupOfGates groupOf(String tclassLotosName, String gateLotosName) {
        GroupOfGates gog = null;
        TClass t;
        Gate g;
        
        for(int i=0; i<groups.size(); i++) {
            gog = (GroupOfGates)(groups.elementAt(i));
            for(int j=0; j<gog.size(); j++) {
                g = gog.getGateAt(j);
                t = gog.getTClassAt(j);
                if ((t.getLotosName().equals(tclassLotosName)) && (g.getLotosName().equals(gateLotosName))) {
                    return gog;
                }
            }
        }
        return null;
    }
    
    private GroupOfGates groupOf(TClass tc, Gate g) {
        GroupOfGates gog = null;
        TClass tmp_tc;
        
        for(int i=0; i<groups.size(); i++) {
            gog = (GroupOfGates)(groups.elementAt(i));
            tmp_tc = gog.getTClassOf(g);
            if (tmp_tc == tc) {
                return gog;
            }
        }
        return null;
    }
    
    private void merge(GroupOfGates gog1, GroupOfGates gog2) {
        TClass t;
        Gate g;
        
        for(int i=0; i<gog2.size(); i++){
            //System.out.println("gog:" + i);
            g = gog2.getGateAt(i);
            t = gog2.getTClassAt(i);
            gog1.addTClassGate(t, g);
        }
        
        groups.remove(gog2);
    }
    
    
    private GroupOfGates addToANewGroup(TClass tc, Gate g) {
        GroupOfGates gog = new GroupOfGates();
        //System.out.println("New Group TClass:" + tc.getLotosName() + " Gate:" + g.getLotosName());
        gog.addTClassGate(tc, g);
        groups.add(gog);
        return gog;
    }
    
    
    public Gate getMasterGateOf(TClass t, Gate g) {
        GroupOfGates gog = groupOf(t, g);
        if (gog == null) {
            return null;
        }
        int index = groups.indexOf(gog);
        if ((index > -1) && (index < master.size())) {
            return (Gate)(master.elementAt(index));
        }
        return null;
    }
    
    public String gogToString(GroupOfGates gog) {
        String s = "";
        Gate g;
        TClass tc;
        for(int j=0; j<gog.size(); j++) {
            g = gog.getGateAt(j);
            tc = gog.getTClassAt(j);
            s += tc.getLotosName() + "." + g.getLotosName() + " ";
        }
        return s;
    }
    
    public String toString() {
        int i, j;
        GroupOfGates gog;
        Gate g;
        TClass tc;
        String s = "";
        
        for(i=0; i<master.size(); i++) {
            gog = (GroupOfGates)(groups.elementAt(i));
            for(j=0; j<gog.size(); j++) {
                g = gog.getGateAt(j);
                tc = gog.getTClassAt(j);
                s += tc.getLotosName() + "." + g.getLotosName() + " ";
            }
            g = (Gate)(master.elementAt(i));
            s += "-> " + g.getLotosName() + "\n";
        }
        return s;
    }
    
    public String allGatesToString() {
        Gate g;
        String s="";
        
        for(int i=0; i<master.size(); i++) {
            g = (Gate)(master.elementAt(i));
            if (i!=0) {
                s += ", ";
            }
            s += g.getLotosName();
        }
        
        return s;
    }
    
    public String allVisibleGatesToString() {
        Gate g;
        String s="";
        int cpt = 0;
        
        for(int i=0; i<master.size(); i++) {
            g = (Gate)(master.elementAt(i));
            if (!g.isInternal()) {
                if (cpt > 0) {
                    s += ", ";
                }
                s += g.getLotosName();
                cpt ++;
            }
        }
        
        return s;
    }
    
    public void sort() {
        Collections.sort(master);
        Collections.sort(groups);
    }
 
}
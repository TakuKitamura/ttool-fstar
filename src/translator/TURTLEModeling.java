/**Copyright or � or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
*
* ludovic.apvrille AT enst.fr
*
* This software is a computer program whose purpose is to allow the
* edition of TURTLE analysis, design and deployment diagrams, to
* allow the generation of RT-LOTOS or Java code from this diagram,
* and at last to allow the analysis of formal validation traces
* obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
* from INRIA Rhone-Alpes.
*
* This software is governed by the CeCILL  license under French law and
* abiding by the rules of distribution of free software.  You can  use,
* modify and/ or redistribute the software under the terms of the CeCILL
* license as circulated by CEA, CNRS and INRIA at the following URL
* "http://www.cecill.info".
*
* As a counterpart to the access to the source code and  rights to copy,
* modify and redistribute granted by the license, users are provided only
* with a limited warranty  and the software's author,  the holder of the
* economic rights,  and the successive licensors  have only  limited
* liability.
*
* In this respect, the user's attention is drawn to the risks associated
* with loading,  using,  modifying and/or developing or reproducing the
* software by the user in light of its specific status of free software,
* that may mean  that it is complicated to manipulate,  and  that  also
* therefore means  that it is reserved for developers  and  experienced
* professionals having in-depth computer knowledge. Users are therefore
* encouraged to load and test the software's suitability as regards their
* requirements in conditions enabling the security of their systems and/or
* data to be ensured and,  more generally, to use and operate it in the
* same conditions as regards security.
*
* The fact that you are presently reading this means that you have had
* knowledge of the CeCILL license and that you accept its terms.
*
* /**
* Class TURTLEModeling
* Creation: 09/12/2003
* @version 1.0 09/12/2003
* @author Ludovic APVRILLE
* @see
*/

package translator;

import java.util.*;

import myutil.*;
import ui.CheckingError;

public class TURTLEModeling {
	private String[] ops = {">", "<", "+", "-", "*", "/", "[", "]", "(", ")", ":", "=", "==", ",", "!", "?", "{", "}"};
    
	
    private Vector tclass;
    private Vector relation;
	//private ArrayList<ADComponent> componentsRA; //Components tag with reachability analysis
    //private Vector hlprocess;
    private int classIndex = 0;
    
    public TURTLEModeling() {
        tclass = new Vector();
        relation = new Vector();
		//componentsRA = new ArrayList<ADComponent>();
    }
    
    public TClass addNewTClass() {
        TClass t = new TClass("Tclass_"+ classIndex, true);
        classIndex ++;
        tclass.add(t);
        return t;
    }
    
    public void addTClass(TClass t) { tclass.addElement(t);}
    
    public void removeTClass(TClass t) {
        tclass.removeElement(t);
    }
	
	/*public void addRAADComponent(ADComponent adc) {
		componentsRA.add(adc);
	}*/
    
    public void addRelation(Relation r) {relation.addElement(r);}
    
    public void addSynchroRelation(TClass t1, Gate g1, TClass t2, Gate g2) {
        // synchro relation between t1 and t2 ?
        Relation r = syncRelationBetween(t1, t2);
        
        
        //System.out.println("****  TOTOTOTOTOTOTOTOTOTOT\n\n\n");
        
        if (r == null) {
            r = new Relation(Relation.SYN, t1, t2, false);
            r.addGates(g1, g2);
            addRelation(r);
            return;
        }
        
        //System.out.println("TOTOTOTOTOTOTOTOTOTOT\n\n\n");
        
        if (r.type != Relation.SYN) {
            return;
        }
        
        if ((r.gatesOfRelation(g1, t1)) || (r.gatesOfRelation(g2, t2))) {
            return;
        }
        
        if (r.t1 == t1) {
            r.addGates(g1, g2);
        } else {
            r.addGates(g2, g1);
        }
    }
    
    public int classNb() {
        return tclass.size();
    }
    
    public int relationNb() {
        return relation.size();
    }
    
    public TClass getTClassAtIndex(int i) {
        if (i >= tclass.size())
            return null;
        else
            return (TClass)(tclass.elementAt(i));
    }
    
    public TClass getTClassWithName(String s) {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            if (t.getName().equals(s)) {
                return t;
            }
        }
        return null;
    }
	
	public TClass findTClass(ADComponent adc) {
		TClass tmp;
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (tmp.has(adc)) {
                return tmp;
            }
        }
        return null;
	}
    
    public int getIndexOf(TClass t) {
        return tclass.indexOf(t);
    }
    
    public boolean belongsToMe(TClass t) {
        TClass tmp;
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (tmp == t) {
                return true;
            }
        }
        return false;
    }
    
    public Relation getRelationAtIndex(int i) {
        if (i > relation.size())
            return null;
        else
            return (Relation)(relation.elementAt(i));
    }
    
    public Relation syncRelationWith(TClass t1, Gate g1) {
		Relation r;
        
        for(int i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if (r.correspondingGate(g1, t1) != null) {
                return r;
            }
        }
        return null;
    }
    
    public Relation syncRelationBetween(TClass t1, TClass t2) {
        Relation r;
        
        for(int i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if (((t1 == r.t1) && (t2 == r.t2)) || ((t2 == r.t1) && (t1 == r.t2))) {
                return r;
            }
        }
        return null;
    }
    
    public boolean knownAction(String action) {
        TClass tmp;
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (tmp.getGateByName(action) != null) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasSeveralTClasWithAction(String action) {
        TClass tmp;
        int cpt=0;
        
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (tmp.getGateByName(action) != null) {
                cpt ++;
            }
        }
        if (cpt > 1) {
            return true;
        }
        return false;
    }
    
    public TClass getTClassWithAction(String action) {
        TClass tmp;
        //int cpt=0;
        
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (tmp.getGateByName(action) != null) {
                return tmp;
            }
        }
        return null;
    }
	
	// At class diagram level: noseq or preempt
	// All classes are active: no need to begin tasks
	// At Activity diagram level:
	// * No preempt with more than one next
	// * No Sequence with more than one next
	// * No non regular choice (delay choice in fact)
	// * No recursivity over parallel operator
	public boolean isARegularTIFSpec() {
		return isARegularTIFSpec(false);
	}
	
	public boolean isARegularTIFSpec(boolean choicesDeterministic) {
		if (!hasOnlyRegularRelations()) {
			return false;
		}
		
		if (!hasOnlyRegularTClasses(choicesDeterministic)) {
			return false;
		}
		
		return true;
	}
	
	public boolean hasOnlyRegularRelations() {
		Relation r;
        
        for(int i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if (!(r.type < 4)) {
				return false;
            }
        }
        return true;
	}
	
	public boolean hasOnlyRegularTClasses(boolean choicesDeterministic) {
		TClass tmp;
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if (!isRegularTClass(tmp.getActivityDiagram(), choicesDeterministic)) {
                return false;
            }
        }
        return true;
	}
	
	public boolean isRegularTClass(ActivityDiagram ad, boolean choicesDeterministic) {
		ADComponent adc;
		ADChoice adchoice;
		
		for(int i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			
			if ((adc instanceof ADPreempt) && (adc.getNbNext() > 1)){
				return false;
			}
			
			if ((adc instanceof ADSequence) && (adc.getNbNext() > 1)){
				return false;
			}
			
			if (adc instanceof ADChoice) {
				adchoice = (ADChoice)adc;
				if (!choicesDeterministic) {
					if (!adchoice.isSpecialChoiceDelay()) {
						System.out.println("Choice is not regular");
						for(int j=0; j<adchoice.getNbNext(); j++) {
							System.out.println("guard[" + j + "]=" + adchoice.getGuard(j));
						}
						return false;
					}
				}
			}
			
			if (adc instanceof ADParallel) {
				if (hasRecursion(adc)) {
					return false;
				}
			}
		}
		
		return true;
	}
    
    
    public boolean hasOnlyPreempt(TClass t) {
        int cpt1 = nbRelationSeqStartingAt(t);
        int cpt2 = nbRelationPreStartingAt(t);
        
        return ((cpt1 == 0) && (cpt2 > 0));
    }
    
    public boolean hasOnlySequence(TClass t) {
        int cpt1 = nbRelationSeqStartingAt(t);
        int cpt2 = nbRelationPreStartingAt(t);
        
        return ((cpt1 > 0) && (cpt2 == 0));
    }
    
    public boolean hasPremptandSequence(TClass t) {
        int cpt1 = nbRelationSeqStartingAt(t);
        int cpt2 = nbRelationPreStartingAt(t);
        
        return ((cpt1 > 0) & (cpt2 > 0));
    }
    
    public int nbRelationSeqStartingAt(TClass t) {
        Relation r;
        int cpt = 0;
        
        for(int i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if ((t == r.t1) && (r.type == Relation.SEQ)) {
                cpt ++;
            }
        }
        return cpt;
    }
    
    public int nbRelationPreStartingAt(TClass t) {
        Relation r;
        int cpt = 0;
        
        for(int i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if ((t == r.t1) && (r.type == Relation.PRE)) {
                cpt ++;
            }
        }
        return cpt;
    }
    
    public void makeRTLOTOSName() {
        TClass tmp;
        String name;
        Vector v;
        int i, j;
        Gate g;
        Param p;
        
        // setting lotos name = default name
        for(i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            tmp.setLotosName(tmp.getName());
            
            v = tmp.getParamList();
            for(j=0; j<v.size(); j++) {
                p = (Param)(v.elementAt(j));
                p.setLotosName(p.getName());
            }
        }
        
        
        for(i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            // Tclass name
            name = tmp.getLotosName();
            if (RTLOTOSKeyword.isAKeyword(name)) {
                tmp.setLotosName(generateTClassName(name, tmp));
            }
            
            // TClasses gates
            v = tmp.getGateList();
            for(j=0; j<v.size(); j++) {
                g = (Gate)(v.elementAt(j));
                if (g.isInternal()) {
                    g.setLotosName(g.getName() + "_" + tmp.getLotosName());
                } else {
                    g.setLotosName(g.getName());
                }
            }
            
            for(j=0; j<v.size(); j++) {
                g = (Gate)(v.elementAt(j));
                if (!okGateName(g.getLotosName(), g, v)) {
                    g.setLotosName(generateGateName(g, v, tmp));
                }
            }
            
            // TClasses attributes
            v = tmp.getParamList();
            for(j=0; j<v.size(); j++) {
                p = (Param)(v.elementAt(j));
                if (!okParamName(p.getLotosName(), p, v)) {
                    p.setLotosName(generateParamName(p, v, tmp));
                }
            }
            
        }
        
        //makeNameOfHiddenGatesUnic();
        
    }
    
    public void makeLOTOSName() {
        TClass tmp;
        //String name;
        Vector v;
        int i, j;
        //Gate g;
        Param p;
        
        
        for(i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            
            // TClasses attributes
            v = tmp.getParamList();
            for(j=0; j<v.size(); j++) {
                p = (Param)(v.elementAt(j));
                if (!okParamNameLOTOS(p.getLotosName(), p, v)) {
                    p.setLotosName(generateParamNameLOTOS(p, v, tmp));
                }
            }
            
        }
        
        //makeNameOfHiddenGatesUnic();
        
    }
    
	/*public void renameParametersInActions() {
		TClass tmp;
		ActivityDiagram ad;
		
		for(int i=0; i<tclass.size(); i++) {
			tmp = (TClass)(tclass.elementAt(i));
			tmp.renameParametersInActions();
		}
		
	}*/
    
    public void makeNameOfHiddenGatesUnic() {
        Vector v = new Vector();
        Vector gates;
        int i, j;
        TClass t;
        Gate g;
        
        for(i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            gates = t.getGateList();
            for(j=0; j<gates.size(); j++) {
                v.addElement(gates.elementAt(j));
            }
        }
        
        for(i=0; i<v.size(); i++) {
            g = (Gate)(v.elementAt(i));
            if (!okGateName(g.getLotosName(), g, v)) {
                g.setLotosName(generateUnicGateName(g, v));
            }
        }
    }
    
    private String generateTClassName(String name, TClass t) {
        int index = 0;
        String s;
        
        while(index > -1) {
            s = name + "_" + index;
            if (okTClassName(s,t)) {
                return s;
            }
            index ++;
        }
        
        return name + "_xxx";
    }
    
    private boolean okTClassName(String name, TClass t) {
        if (RTLOTOSKeyword.isAKeyword(name)) {
            return false;
        }
        
        TClass tmp;
        
        for(int i=0; i<tclass.size(); i++) {
            tmp = (TClass)(tclass.elementAt(i));
            if ((tmp != t) && (tmp.getLotosName().equals(name))) {
                return false;
            }
        }
        
        return true;
        
    }
    
    
    
    private boolean okGateName(String name, Gate g, Vector v) {
        Gate g1;
        
        if (RTLOTOSKeyword.isAKeyword(name)) {
            return false;
        }
        
        for(int i=0; i<v.size(); i++) {
            g1 = (Gate)(v.elementAt(i));
            if (g1 != g) {
                if (g1.getLotosName().equals(name)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String generateGateName(Gate g, Vector v, TClass t) {
        //Gate g1;
        String name = g.getLotosName() + "_" + t.getLotosName();
        String s;
        int index = 0;
        
        while(index > -1) {
            s = name + index;
            if (okGateName(s, g, v)) {
                return s;
            }
            index ++;
        }
        
        return 	name + "_xxx";
    }
    
    private String generateUnicGateName(Gate g, Vector v) {
        //Gate g1;
        String name = g.getLotosName() + "_";
        String s;
        int index = 0;
        
        while(index > -1) {
            s = name + index;
            if (okGateName(s, g, v)) {
                return s;
            }
            index ++;
        }
        
        return 	name + "_xxx";
    }
    
    private boolean okParamName(String name, Param p, Vector v) {
        Param p1;
        
        if (RTLOTOSKeyword.isAKeyword(name)) {
            return false;
        }
        
        for(int i=0; i<v.size(); i++) {
            p1 = (Param)(v.elementAt(i));
            if (p1 != p) {
                if (p1.getLotosName().equals(name)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean okParamNameLOTOS(String name, Param p, Vector v) {
        Param p1;
        
        if (RTLOTOSKeyword.isAKeyword(name)) {
            return false;
        }
        
        if (name.endsWith("_")) {
            return false;
        }
        
        for(int i=0; i<v.size(); i++) {
            p1 = (Param)(v.elementAt(i));
            if (p1 != p) {
                if (p1.getLotosName().equals(name)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private String generateParamName(Param p, Vector v, TClass t) {
        //Param p1;
        String name = p.getLotosName() + "_" + t.getLotosName();
        String s;
        int index = 0;
        
        while(index > -1) {
            s = name + index;
            if (okParamName(s, p, v)) {
                return s;
            }
            index ++;
        }
        
        return 	name + "_xxx";
    }
    
    private String generateParamNameLOTOS(Param p, Vector v, TClass t) {
        
        //Param p1;
        String name = p.getLotosName();
        String s;
        int index = 0;
        
        while(index > -1) {
            s = name + index;
            if (okParamNameLOTOS(s, p, v)) {
                return s;
            }
            index ++;
        }
        
        return 	name + "_xxx";
    }
    
    public static String manageDataStructures(TClass t, String s) {
        // *.* -> *__*
        s = Conversion.replaceAllChar(s, '.', "__");
        //System.out.println("Returning data: " + s);
        return s;
    }
    
    
    // g!pdu.x -> g!pdu__x
    // g!pdu -> g!pdu__x!pdu_y
    // g?pdu -> g?pdu__x?pdu__y
    public static String manageGateDataStructures(TClass t, String s) {
        // g!pdu.x -> g!pdu__x
        s = Conversion.replaceAllChar(s, '.', "__");
        
        //System.out.println("s=" + s);
        
        // g!pdu -> g!pdu__x!pdu__y
        s = manageGateDataStructuresChar(t, s, '!');
        if (s == null) {
            return null;
        }
        
        //System.out.println("s=" + s);
        
        // g?pdu -> g?pdu__x?pdu__y
        s = manageGateDataStructuresChar(t, s, '?');
        if (s == null) {
            return null;
        }
        
        //System.out.println("Returning " + s);
        return s;
    }
    
    private static String manageGateDataStructuresChar(TClass t, String s, char c) {
        //System.out.println("Manage data structure with " + c + " on " + s);
        String ret = "";
        String stmp, paramName;
        Param p;
        
        int index, index1, index2, index3, index4, index5;
        
        if ((s == null) || (s.compareTo("") == 0))
            return s;
        
			while ( (index = s.indexOf(c)) != -1) {
				if (index >0) {
					ret += s.substring(0, index);
				}
				stmp = s.substring(index+1, s.length());
				index1 = stmp.indexOf('!');
				index2 = stmp.indexOf('?');
				index5 = stmp.indexOf(':');
				index3 = stmp.length();
				if (index1 == -1) {
					index1 = index3;
				}
				if (index2 == -1) {
					index2 = index3;
				}
				if (index5 == -1) {
					index5 = index3;
				}
				index4 = Math.min(index1, index2);
				index4 = Math.min(index4, index3);
				index4 = Math.min(index4, index5);
				
				if (index4 > 0) {
					paramName = s.substring(index+1, index4+index+1);
					paramName = paramName.trim();
					
					//System.out.println("Param = " + paramName);
					
					// Numerical param ?
					int param;
					boolean isAnInt = true;
					try {
						param = (Integer.valueOf(paramName)).intValue();
					} catch (NumberFormatException nfe) {
						isAnInt = false;
					}
					
					if (isAnInt) {
						ret = ret + c + paramName;
					} else {
						if ((p = t.getParamByName(paramName)) == null) {
							Vector v = t.getParamStartingWith(paramName + "__");
							
							if (v.size() == 0) {
								//invalid expression
								return null;
							} else {
								for(int i=0; i<v.size(); i++) {
									p = (Param)(v.elementAt(i));
									ret = ret + c + p.getName();
								}
							}
						} else {
							ret = ret + c + paramName;
						}
					}
					
					//ret = ret + c + paramName;
				}
				s = s.substring(index4+index+1, s.length());
				
			}
			
			ret +=s;
			
			return ret;
    }
    
    // g !a?n?p -> g!a?n:nat?p:nat
    public static String addTypeToDataReceiving(TClass t, String s) {
        String ret = "";
        String stmp, paramName;
        Param p;
        char c = '?';
        
        int index, index1, index2, index3, index4;
        
        
        if ((s == null) || (s == ""))
            return s;
        
			while ( (index = s.indexOf(c)) != -1) {
				if (index >0) {
					ret += s.substring(0, index);
				}
				stmp = s.substring(index+1, s.length());
				index1 = stmp.indexOf('!');
				index2 = stmp.indexOf('?');
				index3 = stmp.length();
				if (index1 == -1) {
					index1 = index3;
				}
				if (index2 == -1) {
					index2 = index3;
				}
				index4 = Math.min(index1, index2);
				index4 = Math.min(index4, index3);
				
				if (index4 > 0) {
					paramName = s.substring(index+1, index4+index+1);
					paramName = paramName.trim();
					if (paramName.indexOf(':') == -1) {
						p = t.getParamByName(paramName);
						if (p != null) {
							paramName = paramName + ":" + p.getType();
						} else {
							return null;
						}
					}
					ret = ret + c + paramName;
				}
				s = s.substring(index4+index+1, s.length());
			}
			ret +=s;
			return ret;
    }
    
    public void translateInvocationIntoSynchronization() {
        Relation r;
        Gate g;
        int i, j;
        
        for(i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if (r.type == Relation.INV) {
                // This is a Invocation relation
                // Let T1 be the invoker tclass
                // For each call to a gate g in the activity diagram of T1
                // g1 !expr1 ...!exprn ?exprn+1 ...?exprn+m -> g1!expr1...!exprn followed by g1?exprn+1 ...?exprn+m
                // And the relation is transformed into a synchronization relation
                r.type = Relation.SYN;
                for(j=0; j<r.gatesOfT1.size(); j++) {
                    g = (Gate)(r.gatesOfT1.elementAt(j));
                    r.t1.getActivityDiagram().distinguishAllCallOn(g);
                }
            }
        }
    }
    
    public void translateWatchdogs() {
        //TClass checker, watchdog; 
        TClass t;
        int i;
        Relation r;
        //String name = "unknown";
        
        for(i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            if (r.type == Relation.WAT) {
                t = r.t1;
                //watchdog = r.t2;
                if (t.isActive()) {
                    
                }
                //checker = new TClass(name, t.isActive());
            }
        }
    }
    
    public void translateActionStatesWithMultipleParams() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            t.getActivityDiagram().translateActionStatesWithMultipleParams(t);
        }
    }
    
    // Assumes only one link is linked to component to which tclass is c
    public void removeAllElement(Class c, ADComponent adc2, ActivityDiagram ad) {
        //System.out.println("Removing all elements of type " + c);
        
        ADComponent adc, adc1;
        int i = 0;
        while(i<ad.size()) {
            adc = (ADComponent)(ad.elementAt(i));
            if (c.isInstance(adc)) {
                //if (adc instanceof ADStop) {
					//System.out.println("Found an addstop");
					adc1 = ad.getFirstComponentLeadingTo(adc);
					if (adc1 != null) {
						adc1.updateNext(adc, adc2);
						ad.removeElement(adc);
						removeAllElement(c, adc2, ad);
					}
            }
            i ++;
        }
    }
    
    // Choice followed by one branch -> choice is removed
    // Junction with one incoming branch -> choice is removed
    // synchronization bar with only one incoming branch and one output branch -> removed
    public void optimize() {
        // synchronization bar with only one incoming branch and one output branch -> removed
        //System.out.println("Working on parallels");
        removeUselessParallel();
        //System.out.println("Working on junctions");
        removeUselessJunction();
        
        removeUselessSequence();
    }
    
    private void removeUselessParallel() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            //System.out.println("t=" + t.getName());
            removeUselessParallel(t.getActivityDiagram());
        }
    }
    
    private void removeUselessJunction() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            //System.out.println("t=" + t.getName());
            removeUselessJunction(t.getActivityDiagram());
        }
    }
    
    private void removeUselessSequence() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            //System.out.println("t=" + t.getName());
            removeUselessSequence(t.getActivityDiagram());
        }
    }
    
    private int removeUselessParallel(ActivityDiagram ad) {
        ADComponent adc, adc1;
        int i = 0;
        
        if (ad == null) {
			return 0;
        }
		
        while(i<ad.size()) {
            adc = (ADComponent)(ad.elementAt(i));
            if (adc instanceof ADParallel) {
                if ((adc.getNbNext() == 1) && (ad.getNbComponentLeadingTo(adc) == 1)) {
                    adc1 = ad.getFirstComponentLeadingTo(adc);
                    //System.out.println(adc1.hashCode() + " leads to " + adc1.getNext(0).hashCode() + " and " + adc.hashCode() + "leads to");
                    if (adc1 != null) {
                        adc1.updateNext(adc, adc.getNext(0));
                        ad.removeElement(adc);
                        return removeUselessParallel(ad);
                    }
                }
            }
            i ++;
        }
        return 0;
    }
    
    private int removeUselessJunction(ActivityDiagram ad) {
        ADComponent adc, adc1;
        int i = 0;
        //boolean found = false;
        while(i<ad.size()) {
            adc = (ADComponent)(ad.elementAt(i));
            if (adc instanceof ADJunction) {
                if (ad.getNbComponentLeadingTo(adc) == 1) {
                    //System.out.println("Found a junction to remove");
                    adc1 = ad.getFirstComponentLeadingTo(adc);
                    //System.out.println("Component leading to junction:" +  adc1);
                    if (adc1 != null) {
                        adc1.updateNext(adc, adc.getNext(0));
                        ad.removeElement(adc);
                        i-- ; //return removeUselessJunction(ad);
                    }
                }
            }
            i ++;
        }
        return 0;
    }
    
    private int removeUselessSequence(ActivityDiagram ad) {
        ADComponent adc, adc1;
        int i = 0;
        boolean found = false;
        while(i<ad.size()) {
            adc = (ADComponent)(ad.elementAt(i));
            if (adc instanceof ADSequence) {
                if ((adc.getNbNext() == 1) && (ad.getNbComponentLeadingTo(adc) == 1)) {
                    //System.out.println("Found a sequence to remove");
                    adc1 = ad.getFirstComponentLeadingTo(adc);
                    //System.out.println("Component leading to junction:" +  adc1);
                    if (adc1 != null) {
                        adc1.updateNext(adc, adc.getNext(0));
                        ad.removeElement(adc);
                        found = true;
                        break;
                    }
                }
            }
            i ++;
        }
        if (found) {
            return removeUselessSequence(ad);
        }
        return 0;
    }
    
    private int removeAllUselessComponent(ActivityDiagram ad, boolean debug) {
		return removeAllUselessComponent(ad, debug, true);
    }
	
    private int removeAllUselessComponent(ActivityDiagram ad, boolean debug, boolean specialChoices) {
        ADComponent adc, adc1, adc2;
        ADStop adstop;
        ADChoice adcc;
        int i = 0;
        int j, k;
        
		//System.out.println("Remove All useless components");
        if (specialChoices) {
			ad.makeSpecialChoices();
        }
		
        while(i<ad.size()) {
            adc = (ADComponent)(ad.elementAt(i));
			
			if ((!(adc instanceof ADStart)) && (ad.getNbComponentLeadingTo(adc) == 0)) {
				ad.remove(adc);
				return removeAllUselessComponent(ad, debug, specialChoices);
			}
			
            if ((adc instanceof ADParallel) || (adc instanceof ADPreempt) || (adc instanceof ADSequence) || (adc instanceof ADJunction)) {
                if (adc instanceof ADParallel) {
					if (debug)
                        System.out.println("Found a parallel " + adc + " nbLeadingto" + ad.getNbComponentLeadingTo(adc) + " nbNext=" + adc.getNbNext());    
                }
				if ((adc.getNbNext() > 1) && ((adc instanceof ADParallel) || (adc instanceof ADSequence))) {
					int branch = oneBranchAlwaysLeadingToStop(adc);
					if (branch > -1) {
						adc.removeNext(branch);
						return removeAllUselessComponent(ad, debug, specialChoices);
					}
					
					if (adc instanceof ADSequence) {
						branch = branchNeverLeadingToStop(adc);
						//System.out.println("branch=" + branch);
						if ((branch > -1) && (branch != adc.getNbNext()-1)){
							System.out.println("Removing nexts branch=" + branch);
							adc.removeAllNextAfter(branch);
							return removeAllUselessComponent(ad, debug, specialChoices);
						}
					}
				}
	
                if ((adc.getNbNext() == 1) && (ad.getNbComponentLeadingTo(adc) == 1)) {
                    if (debug)
                        System.out.println("Only one leading to and exiting from " + adc);
                    adc1 = ad.getFirstComponentLeadingTo(adc);
                    //System.out.println(adc1.hashCode() + " leads to " + adc1.getNext(0).hashCode() + " and " + adc.hashCode() + "leads to");
                    if (adc1 != null) {
                        adc1.updateNext(adc, adc.getNext(0));
                        if (debug)
                            System.out.println("removing " + adc);
                        ad.removeElement(adc);
                        return removeAllUselessComponent(ad, debug, specialChoices);
                    }
                } else if (adc instanceof ADJunction) {
                    // Junction looping on itself?
                    if (debug)
						System.out.println("Managing junction");
					
						if (adc.getNext(0) == adc) {
							adstop = new ADStop();
							ad.addElement(adstop);
							adc.removeAllNext();
							adc.addNext(adstop);
							return removeAllUselessComponent(ad, debug, specialChoices);
						}
						
						// Two junctions are set after the other, and the number of leading components to them may fit on only one junction.
						adc1 = adc.getNext(0);
						if (adc1 instanceof ADJunction) {
							if (debug) {
								System.out.println("Next is also a junction next1st= " + ad.getNbComponentLeadingTo(adc)+ " next 2nd=" + ad.getNbComponentLeadingTo(adc1));
							}
							
							if ((ad.getNbComponentLeadingTo(adc) + ad.getNbComponentLeadingTo(adc1)) < 5) {
								// The first junction is kept, and the second is removed
								adc.updateNext(adc1, adc1.getNext(0));
								while((adc2 = ad.getFirstComponentLeadingTo(adc1)) != null) {
									adc2.updateNext(adc1, adc);
								}
								ad.removeElement(adc1);
							}
						}
                }
            } else if (adc instanceof ADChoice) {
                adcc = (ADChoice)adc;
                // Choice with only one next components
                if ((adc.getNbNext() == 1) && (ad.getNbComponentLeadingTo(adc) == 1)) {
                    // test guards
                    String guard = adcc.getGuard(0);
                    if (guard != null) {
                        guard = guard.trim();
                    }
                    if ((guard == null) ||(guard.compareTo("[]") ==0) || (guard.compareTo("[ ]")==0)) {
                        adc1 = ad.getFirstComponentLeadingTo(adc);
                        //System.out.println(adc1.hashCode() + " leads to " + adc1.getNext(0).hashCode() + " and " + adc.hashCode() + "leads to");
                        if (adc1 != null) {
                            adc1.updateNext(adc, adc.getNext(0));
                            if (debug)
                                System.out.println("removing " + adc);
                            ad.removeElement(adc);
                            return removeAllUselessComponent(ad, debug, specialChoices);
                        }
                    }
                }
                
                
                if (debug) {
                    System.out.println("choice=" + adcc.toString());
                }
                // Choice with the same next components and the same guard
                if (adc.getNbNext() > 1) {
                    for(j=0; j<adc.getNbNext()-1; j++) {
                        for(k=j+1; k<adc.getNbNext(); k++) {
                            adc1 = adc.getNext(j);
                            adc2 = adc.getNext(k);
                            
                            if ((adc1 == adc2) && (adcc.getGuard(j).compareTo(adcc.getGuard(k)) ==0)){
                                if (debug) {
                                    System.out.println("adc1=" + adc1 +  " = adc2=" + adc2);
                                    System.out.println("removing next of choice " + adc1);
                                }
                                adcc.removeNext(adc2);
                                if (debug) {
                                    System.out.println("NOW: choice=" + adcc.toString());
                                }
                                return removeAllUselessComponent(ad, debug, specialChoices);
                            }
                        }
                    }
                    
                }
            }
            
            //removing tasks with only stop activity
            if ((adc instanceof ADParallel) || (adc instanceof ADSequence) || (adc instanceof ADPreempt)) {
                if ((adc.getNbNext() > 1) && (ad.getNbComponentLeadingTo(adc) == 1)) {
                    for(j=0; j<adc.getNbNext(); j++) {
                        adc1 = adc.getNext(j);
                        if (adc1 instanceof ADStop) {
                            ad.removeElement(adc1);
                            adc.removeNext(adc1);
                            return removeAllUselessComponent(ad, debug, specialChoices);
                        }
                    }
                }
            }
            
            i ++;
        }
        return 0;
    }
	
	public void removeUselessVariables() {
		removeUselessVariables(null);
	}
	
	// To identify constant values -> applied to nat and booleans only 
	public void removeUselessVariables(Vector warnings) {
		 TClass t;
		 for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            removeUselessVariables(t, warnings);
		 }
	}
	
	public void removeUselessVariables(TClass t, Vector warnings) {
		int i;
		int usage;
		Param p; 
		CheckingError error;
		
		//System.out.println("Analyzing variables in " + t.getName());
		for(i=0; i<t.paramNb(); i++) {
			p = t.getParam(i);
			//System.out.println("Analyzing p=" + p.getName() + " i=" + i);
			
			if (p.isNat() || p.isBool()) {
				//System.out.println("Getting usage");
				usage = getUsageOfParam(t, p);
				//System.out.println("End getting usage");
				if (usage == 0) {
					if (warnings != null) {
						error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Param " + p.getName() + " of tclass " + t.getName() + " is never used -> removing");
						error.setTClass(t);
						warnings.add(error);
					}
					//System.out.println("Param " + p.getName() + " of tclass " + t.getName() + " is never used -> removing");
					t.removeParam(i);
					i--;
				} else if (usage ==1) {
					if (warnings != null) {
						error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Param " + p.getName() + " of tclass " + t.getName() + " is never modified (i.e. constant) -> changing it by its value");
						error.setTClass(t);
						warnings.add(error);
					}
					replaceParamWithItsValue(t, p);
					t.removeParam(i);
					i--;
				}
			}
		}
		//System.out.println("End analyzing variables in " + t.getName());
	}
	
	// Returns how a given parameter is used in a class
	// 0 -> never read nor written (i.e. never used)
	// 1 -> read but never written (i.e. constant value)
	// 2 -> read and written (i.e. regular variable)
	public int getUsageOfParam(TClass t, Param p) {
		// We go through the ad
		// For each component where the param may be involved, we search for it..
		
		ActivityDiagram ad = t.getActivityDiagram();
		ADComponent adc;
		ADActionStateWithGate adag;
		ADActionStateWithParam adap;
		ADChoice adch;
		ADTLO adtlo;
		int i, j;
		int usage = 0;
		int index;
		String s;
		String name = " " + p.getName() +  " ";
		String namebis = p.getName() +  " ";
		
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADActionStateWithGate) {
				adag = (ADActionStateWithGate)adc;
				s = adag.getActionValue();
				s = Conversion.replaceAllChar(s, ' ', "");
				s = removeReducedActionOps(s);
				index = s.indexOf("?" + namebis);
				if (index > -1) {
					return 2;
				}
				
				if (usage == 0) {
					index = s.indexOf(name);
					if (index > -1){
						usage = 1;
					} else {
						usage = analyzeString2WithParam(adag.getLimitOnGate(), name);
					}
				}
			} else if (adc instanceof ADActionStateWithParam) {
				adap = (ADActionStateWithParam)adc;
				if (adap.getParam() == p) {
					return 2;
				}
				
				if (usage == 0) {
					usage = analyzeString2WithParam(adap.getActionValue(), name);
				}
			}  else if (adc instanceof ADTLO) {
				adtlo = (ADTLO)adc;
				s = adtlo.getAction();
				s = Conversion.replaceAllChar(s, ' ', "");
				s = removeAllActionOps(s);
				index = s.indexOf("?" + name);
				if (index > -1) {
					return 2;
				}
				
				if (usage == 0) {
					index = s.indexOf(name);
					if (index > -1){
						usage = 1;
					} else {
						usage = analyzeString2WithParam(adtlo.getLatency() + "+" + adtlo.getDelay(), name);
					}
				}
			} else if (usage == 0) {
				if (adc instanceof ADChoice) {
					adch = (ADChoice)adc;
					for(j=0; j<adch.getNbGuard(); j++) {
						if (usage == 0) {
							usage = analyzeString2WithParam(adch.getGuard(j), name);
						}
					}
				} else if (adc instanceof ADDelay) {
					usage = analyzeString2WithParam(((ADDelay)(adc)).getValue(), name);
				} else if (adc instanceof ADLatency) {
					usage = analyzeString2WithParam(((ADLatency)(adc)).getValue(), name);
				} else if (adc instanceof ADTimeInterval) {
					
					usage = analyzeString2WithParam(((ADTimeInterval)(adc)).getValue(), name);
				} 
			}
		}
		return usage;
	}
	
	public int analyzeStringWithParam(String s, String name) {
		s = Conversion.replaceAllChar(s, ' ', "");
		s = removeAllActionOps(s);
		s = " " + s + " ";
		int index = s.indexOf(name);
		if (index > -1) {
			return 1;
		}
		return 0;
	}
	
	public int analyzeString2WithParam(String s, String name) {
		String stmp = Conversion.replaceAllStringNonAlphanumerical(s, name, "$");
		if (stmp.compareTo(s) == 0) {
			return 1;
		}
		return 0;
	}
	
	public String removeReducedActionOps(String s) {
		s = Conversion.replaceAllChar(s, '!', " ");
		s = Conversion.replaceAllChar(s, '+', " ");
		s = Conversion.replaceAllChar(s, '-', " ");
		s = Conversion.replaceAllChar(s, '*', " ");
		s = Conversion.replaceAllChar(s, '/', " ");
		s = Conversion.replaceAllChar(s, '[', " ");
		s = Conversion.replaceAllChar(s, ']', " ");
		s = Conversion.replaceAllChar(s, '(', " ");
		s = Conversion.replaceAllChar(s, ')', " ");
		s = Conversion.replaceAllChar(s, ':', " ");
		s = Conversion.replaceAllChar(s, '=', " ");
		s = Conversion.replaceAllString(s, "==", " ");
		return s;
	}
	
	public String removeAllActionOps(String s) {
		s = Conversion.replaceAllChar(s, '?', " ");
		s = Conversion.replaceAllChar(s, '!', " ");
		s = Conversion.replaceAllChar(s, '+', " ");
		s = Conversion.replaceAllChar(s, '-', " ");
		s = Conversion.replaceAllChar(s, '*', " ");
		s = Conversion.replaceAllChar(s, '/', " ");
		s = Conversion.replaceAllChar(s, '[', " ");
		s = Conversion.replaceAllChar(s, ']', " ");
		s = Conversion.replaceAllChar(s, '(', " ");
		s = Conversion.replaceAllChar(s, ')', " ");
		s = Conversion.replaceAllChar(s, ':', " ");
		s = Conversion.replaceAllChar(s, '=', " ");
		s = Conversion.replaceAllString(s, "==", " ");
		return s;
	}
	
	public String removeAllActionOpsGate(String s) {
		s = Conversion.replaceAllChar(s, '!', " ");
		s = Conversion.replaceAllChar(s, '?', " ");
		return s;
	}
	
	public void replaceParamWithItsValue(TClass t, Param p) {
		ActivityDiagram ad = t.getActivityDiagram();
		
		ADComponent adc;
		ADActionStateWithGate adag;
		ADActionStateWithParam adap;
		ADDelay add;
		ADLatency adl;
		ADChoice adch;
		ADTimeInterval adti;
		ADTLO adtlo;
		
		int i, j;
		String v1, v2;
		
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADActionStateWithGate) {
				adag = (ADActionStateWithGate)adc;
				adag.setActionValue(putParamValueInString(adag.getActionValue(), p));
			} else if (adc instanceof ADActionStateWithParam) {
				adap = (ADActionStateWithParam)adc;
				//System.out.println("Param=" + p.getName() + " before=" + adap.getActionValue());
				adap.setActionValue(putParamValueInString(adap.getActionValue(), p));
				//System.out.println("Param=" + p.getName() + " after=" + adap.getActionValue());
			} else if (adc instanceof ADChoice) {
				adch = (ADChoice)adc;
				for(j=0; j<adch.getNbGuard(); j++) {
					//v1 = adch.getGuard(j);
					//System.out.println("Param=" + p.getName() + " before=" + adch.getGuard(j));
					adch.setGuard(putParamValueInString(adch.getGuard(j), p), j);
					//v2 = adch.getGuard(j);
					/*if ((v1.compareTo(v2) != 0) || ((p.getName().indexOf("nbOfComputed") > -1) && v1.indexOf("nbOfComputed") > -1)){
						System.out.println("Param=" + p.getName() + " before=" + v1 + " after=" +v2);
					}*/
				}
			} else if (adc instanceof ADDelay) {
				add = (ADDelay)adc;
				add.setValue(putParamValueInString(add.getValue(), p));
			} else if (adc instanceof ADLatency) {
				adl = (ADLatency)adc;
				adl.setValue(putParamValueInString(adl.getValue(), p));
			} else if (adc instanceof ADTimeInterval) {
				adti = (ADTimeInterval)adc;
				v1 = putParamValueInString(adti.getMinValue(), p);
				v2 = putParamValueInString(adti.getMaxValue(), p);
				adti.setValue(v1, v2);
			} else if (adc instanceof ADTLO) {
				adtlo = (ADTLO)adc;
				adtlo.setAction(putParamValueInString(adtlo.getAction(), p));
				adtlo.setDelay(putParamValueInString(adtlo.getDelay(), p));
				adtlo.setLatency(putParamValueInString(adtlo.getLatency(), p));
			}	
		}
	}
	
	
	public String putParamValueInString(String s, Param p) {
		return Conversion.putVariableValueInString(ops, s, p.getName(), p.getValue());
	}
	
	/*public String putParamValueInString(String s, Param p) {
		String newValue = p.getValue();
		String ret = " " + s + " ";
		String name = " " + p.getName() + " ";
		String s0;
		boolean go = true;
		
		while(go) {
			s0 = removeAllActionOps(ret);
			int index = s0.indexOf(name);
			if (index == -1) {
				go = false;
			} else {
				ret = ret.substring(0, index+1) + newValue + ret.substring(index + name.length() - 1, ret.length());
			}
		}
		
		return ret.trim();
	}*/
	
	
	
	public void removeUselessGates() {
		removeUselessVariables(null);
	}
	
	// To identify constant values -> applied to nat and booleans only 
	public void removeUselessGates(Vector warnings) {
		 TClass t;
		 for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            removeUselessGates(t, warnings);
		 }
	}
	
	public void removeUselessGates(TClass t, Vector warnings) {
		int i;
		Gate g;
		CheckingError error;
		
		//System.out.println("Removing useless gates");
		
		for(i=0; i<t.gateNb(); i++) {
			g = t.getGate(i);
			//System.out.println("Gate=" + g.getName());
			if (usageGate(t, g, warnings) == 0) {
				if (warnings != null) {
					error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Gate " + g.getName() + " of tclass " + t.getName() + " is never used -> removing");
					error.setTClass(t);
					warnings.add(error);
				}
				removeGateFromT(t, g);
				i --;
			}
		}
	}
	
	// Returns 1 if gate is used; 0 otherwise
	public int usageGate(TClass t, Gate g, Vector warnings) {
		Relation r;
		int i, usage;
		Gate og;
		
		// Checks in activity diagram
		usage = checkGateInAD(t, g);
		if (usage == 1) {
			return 1;
		}
		
		// Checks in relations
		for(i=0; i<relationNb(); i++) {
			r = (Relation)(relation.get(i));
			og = r.correspondingGate(g, t);
			if (og != null) {
				usage = checkGateInAD(r.otherTClass(t), og);
				// Other gate is used?
				if (usage == 0) {
					// The two gates may be removed from the relation
					// And the relation may be removed if these are the two only gates
					r.removeGates(g, og);
					if (!r.hasGate()) {
						if (warnings != null) {
							CheckingError error = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Relation between " + g.getName() + " of tclass " + t.getName() + " and " + og.getName() + " of tclass " + r.otherTClass(t).getName() + " is useless -> removing");
							error.setTClass(t);
							warnings.add(error);
						}
						relation.removeElement(r);
					}
				} else {
					return 1;
				}
			}
		}
		return 0;
	}
	
	public int checkGateInAD(TClass t, Gate g) {
		ActivityDiagram ad = t.getActivityDiagram();
		
		ADComponent adc;
		ADActionStateWithGate adag;
	
		int i;

		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADActionStateWithGate) {
				if (((ADActionStateWithGate)(adc)).getGate() == g) {
					return 1;
				}
			} else if (adc instanceof ADTLO) {
				if (((ADTLO)(adc)).getGate() == g) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	public void removeGateFromT(TClass t, Gate g) {
		ActivityDiagram ad = t.getActivityDiagram();
		ADComponent adc;
		int i;
		
		// Checks in activity diagram
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADParallel) {
				((ADParallel)(adc)).removeSynchroGateIfApplicable(g);
			}
		}
		
		t.removeGate(g);
		
	}
    
    
    public void print() {
        TClass t;
        Relation r;
        //ActivityDiagram ad;
        int i;
        
        for(i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            System.out.println("\nTClass " + t.getName());
			t.printParamsValues();
			t.printGates();
			//t.getActivityDiagram().print();
        }
        
        for(i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            //System.out.println("\nTClass " + t.getName());
            r.print();
        }
    }
    
    public void print(String className) {
        TClass t;
        //Relation r;
        //ActivityDiagram ad;
        int i;
        
        for(i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            if (t.getName().compareTo(className) ==0) {
                System.out.println("\nTClass " + t.getName());
                t.getActivityDiagram().print();
            }
        }
    }
    
    public StringBuffer printToStringBuffer() {
        TClass t;
        Relation r;
        //ActivityDiagram ad;
        StringBuffer sb = new StringBuffer();
        int i;
        
        for(i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            sb.append("\nTClass " + t.getName()+ "\n");
            t.getActivityDiagram().printToStringBuffer(sb);
        }
        
        sb.append("\n\nRelations:\n");
        for(i=0; i<relation.size(); i++) {
            r = (Relation)(relation.elementAt(i));
            //System.out.println("\nTClass " + t.getName());
            r.printToStringBuffer(sb);
        }
        return sb;
    }
    
    public void simplify() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            System.out.println("\nSimplifying 1  t=" + t.getName());
            removeAllUselessComponent(t.getActivityDiagram(), false);
        }
    }
    
    public void simplify(boolean debug) {
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
			
            if (debug) {
				System.out.println("\n********************** Simplifying t=" + t.getName());
				System.out.println("***** nbOfjunctions=" + t.getNbOfJunctions());
            }
			System.out.println("Simplify 2");
            removeAllUselessComponent(t.getActivityDiagram(), debug);
            if (debug) {
				System.out.println("\n***** nbOfjunctions=" + t.getNbOfJunctions());
            }
        }
    }
    
    public void simplify(boolean debug, boolean specialChoices) {
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
			
            if (debug) {
				System.out.println("\n********************** Simplifying t=" + t.getName());
				System.out.println("***** nbOfjunctions=" + t.getNbOfJunctions());
            }
            removeAllUselessComponent(t.getActivityDiagram(), debug, specialChoices);
            if (debug) {
				System.out.println("\n***** nbOfjunctions=" + t.getNbOfJunctions());
            }
        }
    }
    
	public void countJunctions() {
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
			System.out.println("\n********************** Counting for t=" + t.getName());
			System.out.println("***** nbOfjunctions=" + t.getNbOfJunctions());
        }
    }
    
    
    
    public void simplify(ActivityDiagram ad, boolean debug) {
		//System.out.println("Simplify 3");
        removeAllUselessComponent(ad, debug, false);
    }
    
    public void removeInfiniteLoops() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            //System.out.println("Testing " + t.getName());
            /*if (t.getName().compareTo("StreamDataServ") == 0) {
                removeInfiniteLoopsAD(t.getActivityDiagram(), true);
            } else {*/
				removeInfiniteLoopsAD(t.getActivityDiagram(), false);
				//}
        }
    }
    
    public void removeInfiniteLoopsAD(ActivityDiagram ad, boolean debug) {
        boolean modified = true;
        int i=0;
        
        while((modified) && (i<5000)) {
            // simplify the ad
            //System.out.println("Simplify");
            simplify(ad, debug);
            // Remove unnecessary loops -> unroll non blocking components
            //System.out.println("Unroll");
            modified = unrollComponents(ad, debug);
            //modified = false; // for debug -> to be removed
            i++;
        }
        ad.RemoveAllNonReferencedElts();
    }
    
    public boolean unrollComponents(ActivityDiagram ad, boolean debug) {
        ADComponent adc;
        Vector path;
        
        for(int i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.elementAt(i));
            if ((adc instanceof ADChoice) ||(adc instanceof ADParallel) ||(adc instanceof ADSequence) || (adc instanceof ADPreempt)){
                //System.out.println("Needs unrolling?");
                path = needsUnrolling(ad, adc);
                if (path != null) {
                    //System.out.println("Necessary unrolling found");
                    unroll(ad, adc, path, debug);
                    return true;
                }
            }
        }
        return false;
    }
    
    public Vector needsUnrolling(ActivityDiagram ad, ADComponent adc) {
        // infinite loop leading to the same choice ?
        Vector tested = new Vector();
        boolean b = infinitePathFromTo(adc, adc, tested);
        if (b) {
            return tested;
        } else {
            return null;
        }
    }
    
    public boolean infinitePathFromTo(ADComponent ad1, ADComponent ad2, Vector tested) {
        
        tested.add(ad1);
        Vector list = ad1.getAllNext();
        ADComponent adc;
        boolean b;
        
        for(int i=0; i<list.size(); i++) {
            adc = (ADComponent)(list.elementAt(i));
            
            if (adc == ad2) {
                return true;
            }
            
            if ((adc instanceof NonBlockingADComponent) && (!tested.contains(adc))){
                b = infinitePathFromTo(adc, ad2, tested);
                if (b) {
                    return true;
                }
            }
        }
        tested.remove(ad1);
        return false;
    }
    
    // In path: first one is the choice, last one is the component just before the choice
    // Philosophy : we remove only one cause of the loop
    public void unroll(ActivityDiagram ad, ADComponent adcStartingLoop, Vector path, boolean debug) {
        int i, j;
        ADJunction adj;
        ADComponent toModify;
        
        //System.out.println("Size of path=" + path.size() + " from " + adcStartingLoop.toString() + "/" + adcStartingLoop.hashCode());
        
        
        if (path.size() <1) {
            System.out.println("Internal error at unroll");
            System.exit(-1);
        }
        
        if (path.size() == 1) {
            // Choice on itself -> remove link from to the choice to itself
            adcStartingLoop.removeNext(adcStartingLoop);
            return;
        }
        
        /*for(int k=0; k<path.size(); k++) {
            System.out.println("" + k + "\t elt = " + path.elementAt(k).toString() + "/" + path.elementAt(k).hashCode());
        }*/
        
        
        // path.size > 1
        // remove next link from the choice
        // adcStartingLoop.removeNext(path.elementAt(1));
        // We assume a junction is in the path
        // Otherwise -> error !
        
        //System.out.println("Complex path");
        
        ADComponent adc = null;
        for(i=path.size()-1; i>0; i--) {
            adc = (ADComponent)(path.elementAt(i));
            if (adc instanceof ADJunction) {
                break;
            }
        }
        if (!(adc instanceof ADJunction)) {
            //System.out.println("AD could not be modified -> no junction");
            return;
        }
        
        adj = (ADJunction)adc;
        toModify = (ADComponent)(path.elementAt(i-1));
        
        //From junction -> rebuild a new AD !
        //System.out.println("Substitute to null");
        ad.setAllSubstituteToNull();
        
        Vector pathOld = new Vector();
        //Vector adNew = new Vector();
        
        pathOld.add(adc.getNext(0)); // A junction has only one next component
        
        /*System.out.println("*** print initial AD ***");
        ad.print();*/
        
        //System.out.println("Rebuild");
        RebuildADFrom(ad, pathOld, adc, debug);
        //System.out.println("End rebuild");
        
        /*System.out.println("*** print AD ***");
        ad.print();*/
        
        // Remove all nexts leading to stop
        //System.out.println("Remove next stop");
        removeNextToADStopInSubstitute(ad);
        
        // Add elements to the AD
        for(j=0; j<ad.size(); j++) {
            adc = ((ADComponent)(ad.elementAt(j))).substitute;
            if (adc != null) {
                ad.add(adc);
            }
        }
        
        /*System.out.println("*** print AD ***");
        ad.print();*/
        
        // Add junctions where necessary
        ad.setRegularJunctions();
        
        /*System.out.println("print");
        ad.print();*/
        
        // Link from the beginning to the new structure
        //System.out.println("Linking to the beginning");
        int index = toModify.getAllNext().indexOf(adj);
        //System.out.println("Linking to the beginning index=" + index);
        if (adj.getNext(0).substitute == null) {
            //System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLL nextADJ:" + adj.getNext(0).toString());
            //toModify.setNextAtIndex(adj.getNext(0), index);
            // Add junctions where necessary
            //ad.setRegularJunctions();
            //System.exit(-1);
            toModify.removeNext(toModify.getNext(index));
            
        } else {
            toModify.setNextAtIndex(adj.getNext(0).substitute, index);
        }
        /*System.out.println("print");
        ad.print();*/
        // Remove all non referenced elements
        ad.RemoveAllNonReferencedElts();
        
        /*System.out.println("Last print:");
        ad.print();*/
    }
    
    public void RebuildADFrom(ActivityDiagram ad, Vector path, ADComponent adcToAvoid, boolean debug) {
        ADComponent adc = (ADComponent)(path.get(path.size()-1));
        //ADComponent cloned;
        ADComponent adctmp;
        Vector nexts = new Vector();
        int i;
        
        if (!(adc instanceof NonBlockingADComponent)) {
            // Create the right path if necessary and add the right components to the new ad, and the old one;
            createAD(path, adc, adcToAvoid, debug);
            //System.out.println("Found path to " + adc.toString() + "/" + adc.hashCode());
            return;
        } else {
            if (adc == adcToAvoid) {
                // bad path -> ignored : return
                return;
            } else {
                nexts = adc.getAllNext();
                for(i=0; i<nexts.size(); i++) {
                    adctmp = (ADComponent)(nexts.elementAt(i));
                    if (!path.contains(adctmp)) {
                        path.add(adctmp);
                        RebuildADFrom(ad, path, adcToAvoid, debug);
                        path.removeElementAt(path.size()-1);
                    }
                }
            }
        }
    }
    
    public void createAD(Vector path, ADComponent finalAdc, ADComponent adcToAvoid, boolean debug) {
        int i, j;
        ADComponent adc;
        //Vector nexts;
        ADChoice adch;
        ADParallel adpar;
        ADSequence adseq;
        ADPreempt adpr;
        ADJunction adj;
        
        // Go though throws path and check whether the component exists or not -> if yes, then goes on
        
        for(i=0; i<path.size()-1; i++) {
            adc = (ADComponent)(path.elementAt(i));
            if (adc.substitute == null) {
                // Must create the substitute and link it to the previous one
                //nexts = adc.getAllNext();
                if (adc instanceof ADChoice) {
                    adch = new ADChoice();
                    for(j=0; j<adc.getNbNext(); j++) {
                        adch.addNext(new ADStop());
                        adch.addGuard(((ADChoice)adc).getGuard(j));
                    }
                    adc.substitute = adch;
                } else if (adc instanceof ADParallel) {
                    adpar = new ADParallel();
                    adpar.setValueGate(((ADParallel)(adc)).getValueGate());
                    for(j=0; j<adc.getNbNext(); j++) {
                        adpar.addNext(new ADStop());
                    }
                    adc.substitute = adpar;
                } else if (adc instanceof ADSequence) {
                    adseq = new ADSequence();
                    for(j=0; j<adc.getNbNext(); j++) {
                        adseq.addNext(new ADStop());
                    }
                    adc.substitute = adseq;
                } else if (adc instanceof ADPreempt) {
                    adpr = new ADPreempt();
                    for(j=0; j<adc.getNbNext(); j++) {
                        adpr.addNext(new ADStop());
                    }
                    adc.substitute = adpr;
                } else if (adc instanceof ADJunction) {
                    adj = new ADJunction();
                    for(j=0; j<adc.getNbNext(); j++) {
                        adj.addNext(new ADStop());
                    }
                    adc.substitute = adj;
                } else {
                    System.out.println("Operator not taken into acount:" + adc);
                    System.exit(-1);
                }
                
            }
        }
        
        
        
        // Add the right link to the substitute elements
        ADComponent adc1, adc2, adc3, adc4;
        int index;
        
        for(i=0; i<path.size()-1; i++) {
            adc1 = (ADComponent)(path.elementAt(i));
            adc2 = (ADComponent)(path.elementAt(i+1));
            adc3 = adc1.substitute;
            adc4 = adc2.substitute;
            index = adc1.getAllNext().indexOf(adc2);
            adc3.setNextAtIndex(adc4, index);
        }
        
        // last one: finalAdc
        adc1 = (ADComponent)(path.elementAt(path.size()-2));
        adc3 = adc1.substitute;
        index = adc1.getAllNext().indexOf(finalAdc);
        //System.out.println("Index:" + index);
        adc3.setNextAtIndex(finalAdc, index);
    }
    
    public void removeNextToADStopInSubstitute(ActivityDiagram ad) {
        Vector list;
        ADComponent adc;
        int i, j;
        ADComponent toRemove;
        boolean oneWithoutStop;
        for(i=0; i<ad.size(); i++) {
            adc = ((ADComponent)(ad.elementAt(i))).substitute;
            if (adc != null) {
                list = adc.getAllNext();
                // removed if all don't point to stop
                oneWithoutStop = false;
                for(j=0; j<list.size(); j++) {
                    toRemove = (ADComponent)(list.elementAt(j));
                    if (!(toRemove instanceof ADStop)) {
                        oneWithoutStop = true;
                        break;
                    }
                }
                if (oneWithoutStop) {
                    for(j=0; j<list.size(); j++) {
                        toRemove = (ADComponent)(list.elementAt(j));
                        if (toRemove instanceof ADStop) {
                            adc.removeNext(toRemove);
                            j--;
                        }
                    }
                }
            }
        }
    }
    
    
    /*public boolean unrollPathFromTo(ADComponent ad1, ADComponent ad2, Vector tested, Vector path, ActivityDiagram ad, boolean debug) {
        Vector list = ad1.getAllNext();
        tested.add(ad1);
        path.add(ad1);
        ADComponent adc, adctest = null, adcclone = null, adtmp;
        boolean b;
        boolean found;
        int i;
		
        for(int j=0; j<list.size(); j++) {
            adc = (ADComponent)(list.elementAt(j));
			
            if (adc == ad2) {
                // loop found
                if (path.size() < 1) {
                    // bug!
                    return true;
                }
				
                if (!(ad2 instanceof ADChoice)) {
                    //other bug!
                    return true;
                }
				
                // Collect all blocking symbols accessible from infinite loop
                Vector symbols = new Vector();
                Vector guards = new Vector();
                collectSymbols(path, symbols, guards);
				
                // link these symbols directly from path[0]
                ADComponent adcomp;
                String guard;
                for(int k=0; k<symbols.size(); k++) {
                    adcomp = (ADComponent)(symbols.elementAt(k));
                    guard = (String)(guards.elementAt(k));
                    guard = makeGuard(guard);
                    adc.addNext(adcomp);
                    ((ADChoice)adc).addGuard(guard);
                }
				
                //Remove link from path[0] to path[1]
                adc.removeNext((ADComponent)(path.elementAt(1)));
				
                return true;
				
				
				
				
                // we go back on the path until we find the first component having several next components or being back to the last one ...
                //System.out.println("Performing unrolling");
                //ad.print();
                found = false;
                i = path.size()-1;
                while(i>1) {
                    adctest = (ADComponent)(path.elementAt(i));
                    if (adctest.getNbNext() > 1) {
                        found = true;
                        break;
                    }
                    i --;
                }
				
                if (found) {
                    // must make a clone
                    // has the same nexts as the original component except the one next in the path ...
                    try {
						adcclone = (ADComponent)(adctest.clone());
                    } catch (Exception e) {
                        System.out.println("Exception cloning");
                        System.exit(0);
                    }
                    ad.add(adcclone);
                    if (debug)
                        System.out.println("Adding component");
						if (i == (path.size()-1)) {
							adcclone.removeNext(ad2);
						} else {
							adcclone.removeNext((ADComponent)(path.elementAt(i+1)));
						}
						
						if (i==0) {
							ad2.removeNext(adctest);
							ad2.addNext(adcclone);
						} else {
							adtmp =  ((ADComponent)(path.elementAt(i-1)));
							adtmp.removeNext(adctest);
							adtmp.addNext(adcclone);
						}
                } else {
                    if (debug)
						System.out.println("Removing component");
						if (path.size() > 1) {
							ad2.removeNext((ADComponent)(path.elementAt(1)));
						} else {
							ad2.removeNext((ADComponent)(path.elementAt(0)));
						}
                }
                if (debug)
					System.out.println("Unrolling done");
                //ad.print();
                return true;*/
            /*}
			
            /*if ((adc instanceof NonBlockingADComponent) && (!tested.contains(adc))){
                b = unrollPathFromTo(adc, ad2, tested, path, ad, debug);
                if (b) {
                    return true;
                }
                path.removeElementAt(path.size() - 1);
            }
        }
        return false;
    }*/
    
    /*public void collectSymbols(Vector path, Vector symbols, Vector guards) {
        ADComponent startcomp = (ADComponent)(path.elementAt(1));
        Vector metElements = new Vector();
        Vector pathGuards = new Vector();
        metElements.add(path.elementAt(0));
        recursiveCollectSymbol(startcomp, metElements, pathGuards, symbols, guards);
    }*/
    
    /*public void recursiveCollectSymbol(ADComponent start, Vector metElements, Vector pathguards, Vector symbols, Vector guards) {
        int i;
        String s = "";
        String guard;
        int index;
		
        // non terminal and met -> return
        if ((start instanceof NonBlockingADComponent) && (metElements.contains(start))) {
            return;
        }
		
        // terminal symbol?
        if (!(start instanceof NonBlockingADComponent)) {
            for(i=0; i<pathguards.size(); i++) {
                s += (String)(pathguards.elementAt(i));
            }
            // met?
            if (metElements.contains(start)) {
                index = metElements.indexOf(start);
                guard = (String)(guards.elementAt(index));
                if (guard.compareTo(s) != 0) {
                    symbols.add(start);
                    guards.add(s);
                }
                return;
            } else {
                // must be added with its guard
                metElements.add(start);
                symbols.add(start);
                guards.add(s);
                return;
				
            }
			
        }
		
        // Else -> standard behavior
        metElements.add(start);
		
        Vector list = start.getAllNext();
        ADComponent adc;
        boolean b;
		
        for(i=0; i<list.size(); i++) {
            adc = (ADComponent)(list.elementAt(i));
            if (start instanceof ADChoice) {
                pathguards.add(((ADChoice)start).getGuard(i));
            }
            recursiveCollectSymbol(adc, metElements, pathguards, symbols, guards);
            if (start instanceof ADChoice) {
                pathguards.removeElementAt(pathguards.size()-1);
            }
        }
		
    }*/
    
    /*public String makeGuard(String g) {
        System.out.println("guard = " + g);
        return "[ ]";
    }*/
    
    // Assume there is no guard ...
    public void removeChoicesLeadingToStop() {
        TClass t;
        for(int i=0; i<tclass.size(); i++) {
            t = (TClass)(tclass.elementAt(i));
            //System.out.println("----------------------- Testing choices of " + t.getName());
            removeChoicesLeadingToStopAD(t.getActivityDiagram());
        }
    }
    
    
    public void removeChoicesLeadingToStopAD(ActivityDiagram ad) {
        boolean modified = true;
        int i=0;
        
        while((modified) && (i<5000)) {
            simplify(ad, false);
            modified = analyseChoiceStop(ad);
            //modified = false; // for debug -> to be removed
            i++;
        }
        ad.RemoveAllNonReferencedElts();
    }
    
    public boolean analyseChoiceStop(ActivityDiagram ad) {
        int adPathAction;
        int adPathStop;
        ADComponent adc;
        ADChoice adch;
        for(int i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.elementAt(i));
            if (adc instanceof ADChoice) {
                adch = (ADChoice)adc;
                //System.out.println("Testing " + adch.toString() + "/" + adch.hashCode());
                adPathAction = getPathAction(adch);
                adPathStop = getPathStop(adch);
                //ystem.out.println("Exploring paths adPathAction=" + adPathAction + " adPathStop=" + adPathStop);
                if ((adPathAction > -1) && (adPathStop > -1) && (adPathAction != adPathStop)) {
                    //System.out.println("Choices with action and stop paths found");
                    if (!adch.isGuarded(adPathStop)) {
                        //System.out.println("Removing path");
                        adch.removeNext(adch.getNext(adPathStop));
                        return true;
                    }
                }
                
            }
        }
        return false;
    }
    
    public int getPathAction(ADChoice adch) {
        Vector list = adch.getAllNext();
        Vector path = new Vector();
        ADComponent adc;
        boolean foundAction;
        for (int i=0; i<list.size(); i++) {
            adc = (ADComponent)(list.elementAt(i));
            foundAction = explorePathAction(adc, path, adch);
            if (foundAction) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean explorePathAction(ADComponent adc, Vector path, ADComponent adch) {
        ADComponent adcbis;
        
        if (adc == null) {
            return false;
        }
        
        if (adc == adch) {
            return false;
        }
        
        if (path.contains(adc)) {
            return false;
        }
        
        if (adc instanceof ADStop) {
            return false;
        }
        
        if (!(adc instanceof NonBlockingADComponent)) {
            return true;
        }
        
        path.add(adc);
        Vector list = adc.getAllNext();
        for(int i=0; i<list.size(); i++) {
            adcbis = (ADComponent)(list.elementAt(i));
            if (explorePathAction(adcbis, path, adch)) {
                return true;
            }
        }
        path.remove(adc);
        return false;
    }
    
    public int getPathStop(ADChoice adch) {
        Vector list = adch.getAllNext();
        Vector path = new Vector();
        ADComponent adc;
        boolean foundAction;
        boolean foundStop;
        for (int i=0; i<list.size(); i++) {
            adc = (ADComponent)(list.elementAt(i));
            foundAction = explorePathAction(adc, path, adch);
            path.removeAllElements();
            foundStop = explorePathStop(adc, path, adch);
            //System.out.println("foundAction=" + foundAction + " foundStop=" + foundStop);
            if ((!foundAction) && (foundStop)){
                return i;
            }
        }
        return -1;
    }
    
    public boolean explorePathStop(ADComponent adc, Vector path, ADComponent adch) {
        ADComponent adcbis;
        if (adc == adch) {
            return false;
        }
        
        if (path.contains(adc)) {
            return false;
        }
        
        if (adc instanceof ADStop) {
            //System.out.println("Stop found");
            return true;
        }
        
        if (!(adc instanceof NonBlockingADComponent)) {
            return false;
        }
        
        
        path.add(adc);
        Vector list = adc.getAllNext();
        for(int i=0; i<list.size(); i++) {
            adcbis = (ADComponent)(list.elementAt(i));
            if (explorePathStop(adcbis, path, adch)) {
                return true;
            }
        }
        path.remove(adc);
        return false;
    }
    
    public int getNbOfSynchroItems(String value) {
        
        int nb1 = myutil.Conversion.nbChar(value, '!');
        int nb2 = myutil.Conversion.nbChar(value, '?');
        //System.out.println("Nb of synchro of " + value + " = " + (nb1 + nb2));
        return nb1 + nb2;
    }
    
    public boolean isSendingSynchro(String value, int index) {
        
        //System.out.println("IsSending value=" + value + " index=" + index);
        value = getSynchroAt(value, index);
        
        if (value.length() < 1) {
            //System.out.println("error");
            return false;
        }
        
        if (value.charAt(0) == '!') {
            //System.out.println("Is sending!");
            return true;
        }
        
        //System.out.println("Is not sending!");
        return false;
    }
    
    public String getSynchroAt(String value, int index) {
        //System.out.println("getSynchroAt value=" + value + " index=" + index);
        //int ind;
        //int total = index;
        String s;
        
        int ind1 = value.indexOf('!');
        int ind2 = value.indexOf('?');
        //int ind3, ind4;
        int ind5;
        
        while (((ind1 != -1) || (ind2 != -1)) && index>-1) {
            if (ind1 == -1)
                ind1 = value.length();
            if (ind2 == -1)
                ind2 = value.length();
            ind5 = Math.min(ind1, ind2);
            
            if (index == 0) {
                value = value.substring(ind5, value.length());
                s = value.substring(1, value.length());
                //System.out.println("value = " + value + " s=" + s);
                ind1 = s.indexOf('!');
                ind2 = s.indexOf('?');
                if (ind1 == -1)
                    ind1 = value.length();
					else {
						ind1 = ind1 + 1;
					}
					if (ind2 == -1)
						ind2 = value.length();
					else
						ind2 = ind2 + 1;
					ind5 = Math.min(ind1, ind2);
					value = value.substring(0, ind5).trim();
					//System.out.println("returning 2 " + value);
					return value;
					
            }
            
            value = value.substring(ind5+1, value.length());
            
            index --;
            
            ind1 = value.indexOf('!');
            ind2 = value.indexOf('?');
        }
        //System.out.println("Error");
        return "";
    }
    
    public boolean isNaturalSynchro(TClass t, String value, int index) {
        value = getSynchroValueAt(value, index);
        
        int ind = value.indexOf(':');
        
        if (ind != -1) {
            value = value.substring(0, ind);
        }
        
        //System.out.println("Nat? : " + value);
        
        if (value.equals("true") || value.equals("false")) {
            //System.out.println("bool");
            return false;
        }
        
        Param p = t.getParamByName(value);
        if (p == null) {
            // numerical value or malformed tm
            //System.out.println("numeric / malformed");
            return true;
        }
        
        if (p.getType() == Param.NAT) {
            //System.out.println("Nat !");
            return true;
        }
        
        //System.out.println("Bool !");
        return false;
    }
    
    public String getSynchroValueAt(String value, int index) {
        value = getSynchroAt(value, index);
        if (value.length() > 0) {
            return value.substring(1, value.length());
        } else {
            return value;
        }
    }
    
    public String getShortSynchroValueAt(String value, int index) {
        value = getSynchroValueAt(value, index);
        
        int ind = value.indexOf(':');
        
        if (ind != -1) {
            value = value.substring(0, ind);
        }
        
        return value;
    }
    
    public boolean canReachSynchroOn(ADParallel adp, Gate g) {
		LinkedList ll = new LinkedList();
		for(int i=0; i<adp.getNbNext(); i++) {
			if (canReachSynchroOn(adp, g, adp.getNext(i), ll)) {
				return true;
			}
		}
		return false;
    }
	
    public boolean canReachSynchroOn(ADParallel adp, Gate g, ADComponent adc, LinkedList ll) {
		if (adc == adp) {
			return true;
		}
		
		if (ll.contains(adc)) {
			return false;
		}
		
		ll.add(adc);
		
		if (adc instanceof ADParallel) {
			ADParallel adp1 = (ADParallel)(adc);
			Gate g1;
			for(int j=0; j<adp1.nbGate(); j++) {
				g1 = adp1.getGate(j);
				if (g1 == g) {
					return true;
				}
			}
		}
		
		for(int i=0; i<adc.getNbNext(); i++) {
			if (canReachSynchroOn(adp, g, adc.getNext(i), ll)) {
				return true;
			}
		}
		return false;
    }
    
    public void unrollRecursions(int n) {
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
			t = (TClass)(tclass.elementAt(i));
			//System.out.println("----------------------- Testing choices of " + t.getName());
			unrollRecursions(t.getActivityDiagram(), n);
        }
    }
    
    public void unrollRecursions(ActivityDiagram ad, int n) {
		boolean recursionUnrolled = true;
		while(recursionUnrolled) {
			recursionUnrolled = false;
			recursionUnrolled = unrollOneRecursion(ad, n);
		}
    }
    
    public boolean unrollOneRecursion(ActivityDiagram ad, int n) {
		ADComponent adc;
		
		for(int i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.elementAt(i));
			if (adc instanceof ADParallel) {
				if (hasRecursion(adc)) {
					System.out.println("Recursion found -> unrolling " + n + " times");
					return unroll(adc, n);
				}
			}
		}
		return false;
    }
    
    public boolean hasRecursion(ADComponent adc) {
		LinkedList ll = new LinkedList();
		for(int i=0; i<adc.getNbNext(); i++) {
			if (hasRecursion(adc, adc.getNext(i), ll)) {
				return true;
			}
		}
		return false;
    }
    
    public int getFirstNextRecursion(ADComponent adc) {
		LinkedList ll = new LinkedList();
		for(int i=0; i<adc.getNbNext(); i++) {
			if (hasRecursion(adc, adc.getNext(i), ll)) {
				return i;
			}
		}
		return -1;
    }
    
    public boolean hasRecursion(ADComponent base, ADComponent current, LinkedList ll) {
		if (base == current) {
			return true;
		}
		if (ll.contains(current)) {
			return false;
		}
		
		ll.add(current);
		
		for(int i=0; i<current.getNbNext(); i++) {
			if (hasRecursion(base, current.getNext(i), ll)) {
				return true;
			}
		}
		return false;
    }
    
    public boolean unroll(ADComponent adc, int n) {
		LinkedList path = new LinkedList();
		calculateOneRecursionPath(adc, new LinkedList(), path);
		
		// A path has been built -> clone it n times
		// Find the ADJunction
		ADJunction adj = lastADJunction(path);
		if (adj == null) {
			return false;
		}
		
		//To be continued...
		
		
		return true;
    }
    
    public ADJunction lastADJunction(LinkedList path) {
		ADComponent adc;
		
		for(int i=path.size()-1; i>-1; i++) {
			adc = (ADComponent)(path.get(i));
			if (adc instanceof ADJunction) {
				return (ADJunction)adc;
			}
		}
		return null;
    }
    
    public void calculateOneRecursionPath(ADComponent adc, LinkedList explored, LinkedList path) {
		explored.add(adc);
		for(int i=0; i<adc.getNbNext(); i++) {
			if (calculateOneRecursionPath(adc, adc.getNext(i), explored, path)) {
				return;
			}
		}
		return;
    }
    
    public boolean calculateOneRecursionPath(ADComponent base, ADComponent current, LinkedList explored, LinkedList path) {
		if (current == base) {
			path.add(current);
			return true;
		}
		
		if (explored.contains(current)) {
			return false;
		}
		
		explored.add(current);
		
		for(int i=0; i<current.getNbNext(); i++) {
			if (calculateOneRecursionPath(base, current.getNext(i), explored, path)) {
				path.addFirst(current);
				return true;
			}
		}
		return false;
    }
	
	public void unmergeChoices() {
		System.out.println("Unmerging choices: algorithm");
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
			t = (TClass)(tclass.elementAt(i));
			unmergeChoices(t.getActivityDiagram());
        }
	}
	
	public void unmergeChoices(ActivityDiagram ad) {
		boolean changeMade = true;
		int i;
		ADComponent adc1;
		ADChoice adch1;
		int index;
		
		while(changeMade) {
			changeMade = false;
			for(i=0; i<ad.size(); i++) {
				adc1 = (ADComponent)(ad.get(i));
				if (adc1 instanceof ADChoice) {
					adch1 = (ADChoice) adc1;
					if (adch1.getNbNext() > 3) {
						unmergeChoices(ad, adch1);
						changeMade = true;
					}
				}
			}
		}
    }
	
	public void unmergeChoices(ActivityDiagram ad, ADChoice adch) {
		//System.out.println("Nb of next=" + adch.getNbNext());
		// We remove the three first ones
		ADChoice tmp = new ADChoice();
		ad.add(tmp);
		for(int i=0; i<3; i++) {
			tmp.addNext(adch.getNext(0));
			tmp.addGuard(adch.getGuard(0));
			adch.removeNext(0);
		}
		adch.addNext(tmp);
		adch.addGuard("[ ]");
	}
	
	public void mergeChoices() {
		mergeChoices(false);
    }
	
	public void mergeChoices(boolean nonDeterministic) {
		System.out.println("Merging choices: algorithm / not guarded only: " + nonDeterministic);
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
			t = (TClass)(tclass.elementAt(i));
			mergeChoices(t.getActivityDiagram(), nonDeterministic);
        }
		System.out.println("End merging choices: algorithm");
    }
	
	
    
    public void mergeChoices(ActivityDiagram ad, boolean nonDeterministic) {
		boolean changeMade = true;
		int i;
		ADComponent adc1;
		ADChoice adch1;
		int index;
		
		while(changeMade) {
			changeMade = false;
			for(i=0; i<ad.size(); i++) {
				adc1 = (ADComponent)(ad.get(i));
				if (adc1 instanceof ADChoice) {
					adch1 = (ADChoice) adc1;
					if ((index = adch1.getNextChoice()) != -1) {
						if ((nonDeterministic) && (!adch1.isGuarded())) {
							mergeChoices(ad, adch1, index);
							changeMade = true;
						} else {
							if (!nonDeterministic) {
								mergeChoices(ad, adch1, index);
								changeMade = true;
							}
						}
					}
				}
			}
		}
    }
	
	public void mergeChoices(ActivityDiagram ad, ADChoice adch1, int index) {
		String g1, g2;
		ADChoice adch2 = (ADChoice)(adch1.getNext(index));
		System.out.println("Merging adch1=" + adch1 + " with adch2=" + adch2 + " of index=" + index);
		
		ADComponent adc;
		for (int i=0; i<adch2.getNbNext(); i++) {
			adc = adch2.getNext(i);
			
			adch1.addNext(adc);
			
			if ((!adch1.isGuarded(index)) && (!adch2.isGuarded(i))) {
				adch1.addGuard("[ ]");
			} else if ((adch1.isGuarded(index)) && (!adch2.isGuarded(i))) {
				adch1.addGuard(adch1.getGuard(index));
			} else if ((!adch1.isGuarded(index)) && (adch2.isGuarded(i))) {
				adch1.addGuard(adch2.getGuard(i));
			} else {
				// Both choices are guarded
				g1 = adch1.getGuard(index);
				g2 = adch2.getGuard(i);	
				
				g1 = Conversion.replaceAllChar(g1, '[', "");
				g2 = Conversion.replaceAllChar(g2, '[', "");
				g1 = Conversion.replaceAllChar(g1, ']', "");
				g2 = Conversion.replaceAllChar(g2, ']', "");
				
				g1 = "[(" + g1 + ") and (" + g2 + ")]";
				adch1.addGuard(g1);
			}
		}
		
		adch1.removeNext(adch2);
		ad.remove(adch1);
	}
	
	public void makeSequenceWithDataSave() {
		// First try to remove sequences
		removeSequencesDataSave();
		
		//Second option: save data with synchro, and load data with synchro
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
			t = (TClass)(tclass.elementAt(i));
			makeSequenceWithDataSave(t);
        }
	}
	
	public void makeSequenceWithDataSave(TClass t) {
		ActivityDiagram ad = t.getActivityDiagram();
		
		if (ad.getNbOfSequence() == 0) {
			return;
		}
		
		ADStart ads = ad.getStartState();
		
		if (ads == null) {
			return;
		}	
		
		Vector v = t.getParamList();
		
		// No parameter to save?
		if ((v == null) || (v.size() == 0)) {
			return;
		}
		
		
		// Modify beginning of activity diagram
		String list1="", list2="";
		Param p;
		int i;
		
		for(i=0; i<v.size(); i++) {
			p=(Param)(v.get(i));
			list1+="!" + p.getName();
			list2+="?" + p.getName() + ":nat";
		}
		
		Gate gput = new Gate("putseq__", Gate.GATE, false);
		Gate gget = new Gate("getseq__", Gate.GATE, false);
		
		t.addGate(gput);
		t.addGate(gget);
		
		ADParallel adp = new ADParallel();
		adp.setValueGate("[putseq__, getseq__]");
		adp.setNewNext(ads.getAllNext());
		ad.add(adp);
		ads.setNewNext(new Vector());
		ads.addNext(adp);
		
		
		ADJunction adj = new ADJunction();
		adp.addNext(adj);
		ad.add(adj);
		
		ADChoice adch = new ADChoice();
		adj.addNext(adch);
		ad.add(adch);
		
		ADActionStateWithGate adsg1 = new ADActionStateWithGate(gput);
		adsg1.setActionValue(list2);
		adch.addNext(adsg1);
		ad.add(adsg1);
		
		ADActionStateWithGate adsg2 = new ADActionStateWithGate(gget);
		adsg2.setActionValue(list1);
		adch.addNext(adsg2);
		ad.add(adsg2);
		
		adsg1.addNext(adj);
		adsg2.addNext(adj);
		
		// All stop state must be preceeded with an action state on put
		ADComponent adc, adc1;
		ADActionStateWithGate adsg;
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADStop) {
				adc1 = ad.getFirstComponentLeadingTo(adc);
				if (adc1 != null ){
					adsg = new ADActionStateWithGate(gput);
					adsg.setActionValue(list1);
					ad.add(adsg);
					adc1.setNewNext(new Vector());
					adc1.addNext(adsg);
					adsg.addNext(adc);
				}
			}
		}
		
		// All next element of sequences must start with an action state on get
		int j;
		for(i=0; i<ad.size(); i++) {
			adc = (ADComponent)(ad.get(i));
			if (adc instanceof ADSequence) {
				v = new Vector();
				v.add(adc.getNext(0));
				for(j=1; j<adc.getNbNext(); j++) {
					adc1 = adc.getNext(j);
					adsg = new ADActionStateWithGate(gget);
					adsg.setActionValue(list2);
					ad.add(adsg);
					adsg.addNext(adc1);
					v.add(adsg);
				}
				adc.setNewNext(v);
			}
		}
	}
	
	public void removeSequencesDataSave() {
		
		//Second option: save data with synchro, and load data with synchro
		TClass t;
        for(int i=0; i<tclass.size(); i++) {
			t = (TClass)(tclass.elementAt(i));
			removeSequencesDataSave(t.getActivityDiagram());
        }
	}
	
	public void removeSequencesDataSave(ActivityDiagram ad) {
		boolean modified = true;
		
		simplify(ad, false);
		
		while(modified) {
			modified = removeSequencesDataSave1(ad);
		}
	}
	
	public boolean removeSequencesDataSave1(ActivityDiagram ad) {
		ADComponent adc;
		boolean ret;
		
		for(int i=0; i<ad.size(); i++) {
			adc = ad.getADComponent(i);
			if (adc instanceof ADSequence) {
				if (adc.getNbNext() > 1) {
					if (removeSequencesDataSave2(ad, (ADSequence)adc)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean removeSequencesDataSave2(ActivityDiagram ad, ADSequence ads) {
		// Test whether it should be removed or not
		if (canBeRemovedDataSave1(ad, ads)) {
			removeSequenceDataSave(ad, ads);
			//System.out.println("Symplifying one sequence");
			simplify(ad, false);
			return true;
		}
		
		return false;
	}
	
	public boolean canBeRemovedDataSave1(ActivityDiagram ad, ADSequence ads) {
		// Select first branch
		ADComponent adc = ads.getNext(0);
		ArrayList<ADComponent> met = new ArrayList<ADComponent>();
		
		//Explore the branch
		return canBeRemovedDataSave1(ad, ads, adc, met); 
	}
	
	public boolean canBeRemovedDataSave1(ActivityDiagram ad, ADSequence ads, ADComponent adc, ArrayList<ADComponent> met) {
		if (adc == null) {
			return false;
		}
		
		if (adc == ads) {
			return true;
		}
		
		if (adc instanceof ADStop) {
			return true;
		}
		
		if (met.indexOf(adc) != -1){
				return true;
		}
		
		if ((adc instanceof ADParallel) || (adc instanceof ADPreempt) || (adc instanceof ADSequence)) {
			if (adc.getNbNext() > 1) {
				return false;
			}
		}
		
		if (adc instanceof ADJunction) {
			if (canBeAccessedNotFrom(ad, ads, adc)) {
				return false;
			}
		}
		
		met.add(adc);
		
		boolean b = true;
		for(int i=0; i<adc.getNbNext(); i++) {
			b = b && canBeRemovedDataSave1(ad, ads, adc.getNext(i), met);
		}
		
		return b;	
	}
	
	public void removeSequenceDataSave(ActivityDiagram ad, ADSequence ads) {
		ADJunction adj = new ADJunction();
		ad.add(adj);
		adj.addNext(ads.getNext(1));
		ads.removeNext(1);
		ArrayList<ADComponent> met = new ArrayList<ADComponent>();
		linkStopTo(ad, ads, adj, met);
	}
	
	public void linkStopTo(ActivityDiagram ad, ADComponent start, ADComponent destination, ArrayList<ADComponent> met) {
		if (met.indexOf(start) != -1) {
			return;
		}
		
		if (start instanceof ADStop) {
			ADComponent adc = ad.getFirstComponentLeadingTo(start);
			adc.updateNext(start, destination);
			return;
		}
		
		met.add(start);
		
		for(int i=0; i<start.getNbNext(); i++) {
			linkStopTo(ad, start.getNext(i), destination, met);
		}
	}
	
	// Test whether adc can be accessed from the beginning without going thru ads
	public boolean canBeAccessedNotFrom(ActivityDiagram ad, ADComponent ads, ADComponent adc) {
		ArrayList<ADComponent> met = new ArrayList<ADComponent>();
		return canBeAccessedNotFrom(ad.getStartState(), ads, adc, met);
	}
	
	public boolean canBeAccessedNotFrom(ADComponent current, ADComponent notToReach, ADComponent toReach, ArrayList<ADComponent> met) {
		if (current == toReach) {
			return true;
		}
		
		if (current == notToReach) {
			return false;
		}
		
		if (met.indexOf(current) != -1) {
			return false;
		}
	
		met.add(current);
		
		for(int i=0; i<current.getNbNext(); i++) {
			if (canBeAccessedNotFrom(current.getNext(i), notToReach, toReach, met)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int oneBranchAlwaysLeadingToStop(ADComponent adc) {
		for(int i=0; i<adc.getNbNext(); i++) {
			if (branchLeadingToStop(adc.getNext(i))){
				return i;
			}
		}
		return -1;
	}
	
	public boolean branchLeadingToStop(ADComponent adc) {
		ArrayList<ADComponent> met = new ArrayList<ADComponent>();
		return branchLeadingToStop(adc, met);
	}
	
	public boolean branchLeadingToStop(ADComponent adc, ArrayList<ADComponent> met) {
		if (adc instanceof ADStop) {
			return true;
		}
		
		if (met.indexOf(adc) != -1) {
			return false;
		}
		
		if ((adc instanceof ADActionState) || (adc instanceof ADDelay) || (adc instanceof ADTLO) || (adc instanceof ADLatency) || (adc instanceof ADTimeInterval)) {
			return false;
		}
		
		if (adc instanceof ADChoice) {
			if (((ADChoice)adc).isGuarded()) {
				return false;
			}
		}
		
		met.add(adc);
		
		for(int i=0; i<adc.getNbNext(); i++) {
			if (!branchLeadingToStop(adc.getNext(i), met)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	public int branchNeverLeadingToStop(ADComponent adc) {
		for(int i=0; i<adc.getNbNext(); i++) {
			if (branchIsNeverLeadingToStop(adc.getNext(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean branchIsNeverLeadingToStop(ADComponent adc) {
		ArrayList<ADComponent> met = new ArrayList<ADComponent>();
		return branchIsNeverLeadingToStop(adc, met);
	}
	
	public boolean branchIsNeverLeadingToStop(ADComponent adc, ArrayList<ADComponent> met) {
		if (adc instanceof ADStop) {
			return false;
		}
		
		if (met.indexOf(adc) != -1) {
			return true;
		}
		
		met.add(adc);
		
		for(int i=0; i<adc.getNbNext(); i++) {
			if (!branchIsNeverLeadingToStop(adc.getNext(i), met)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static String addPrefixInExpression(String prefix, String action) {
		/*TIFExpressionTree tree = new TIFExpressionTree();
		if (!tree.buildTree(action)) {
			return null;
		}
		tree.addPrefixToVariables(prefix);
		return tree.getExpression();*/
		
		// The String is parsed. Each time a letter is met, the prefix is added
		// and we skip to next word
		String ret = "", var = "";
		
		//System.out.println("Analyzing action=" + action);
		action = action + " ";
		char[] chars= action.toCharArray();
		
		int mode = 0; // find variable;
		boolean test;
		
		for(int i=0; i<chars.length; i++) {
			if (mode == 0) {
				test = Character.isLetter(chars[i]);
				if (test) {
					mode = 1;
					var = var + chars[i];
				} else {
					ret = ret + chars[i];
				}
			} else {
				test = Character.isLetter(chars[i]);
				test = test || Character.isDigit(chars[i]);
				test = test || (chars[i] == '_');
				if (!test) {
					if (RTLOTOSKeyword.isAKeyword(var)) {
						ret = ret + var;
					} else {
						ret = ret + prefix + var;
					}
					var = "";
					ret = ret + chars[i];
					mode = 0;
				} else {
					var = var + chars[i];
				}
			}
		}
		
		ret = ret.substring(0, ret.length()-1);
		
		return ret;
	}
	
	
	
}

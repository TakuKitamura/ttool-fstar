/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */







package translator;

import myutil.Conversion;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Class ActivityDiagram
 * Creation: 10/12/2003
 * @version 1.0 10/12/2003
 * @author Ludovic APVRILLE
 */
public class ActivityDiagram extends Vector<ADComponent>{
    
    protected ADStart ads;
    
    public ActivityDiagram(ADStart _ads) {
        ads = _ads;
        add(ads);
    }
    
    public ActivityDiagram() {
        ads = new ADStart();
        add(ads);
    }
    
    public ADStart getStartState() {
        return ads;
    }
	
	public void setStartState(ADStart _ads) {
		remove(ads);
		ads = _ads;
	}
	
	public void setRawStartState(ADStart _ads) {
		ads = _ads;
	}
	
	public ADComponent getADComponent(int index) {
		return get(index);
	}
    
    
    public String getTranslation(TClass parent) {
        return ""  ;
    }
    
    public int getNbParallel() {
        ADComponent ad;
        int nb = 0;
        for (int i=0; i<size(); i++) {
            ad = elementAt(i);
            if (ad instanceof ADParallel) {
                nb ++;
            }
        }
        return nb;
    }
    
    public void setParallelMulti() {
        ADComponent ad;
        ADParallel par;
        int nb;
        
        for(int i=0; i<size(); i++) {
            ad = elementAt(i);
            if (ad instanceof ADParallel) {
                par = (ADParallel)ad;
                nb = getNbComponentLeadingTo(par);
                if (nb >1) {
                    par.setMulti(true);
                } else {
                    par.setMulti(false);
                }
            }
        }
    }
    
    public int getNbMultiParallel() {
        ADComponent ad;
        int nb = 0;
        int nb1;
        for (int i=0; i<size(); i++) {
            ad = elementAt(i);
            if (ad instanceof ADParallel) {
                nb1 = getNbComponentLeadingTo(ad);
                if (nb1 > 1) {
                    nb ++;
                }
            }
        }
        return nb;
    }
    
    public int getNbComponentLeadingTo(ADComponent ad) {
        ADComponent ad1;
        int i, j;
        int nb = 0;
        
        for (i=0; i<size(); i++) {
            ad1 = elementAt(i);
            for(j=0; j<ad1.getNbNext(); j++) {
                if (ad1.getNext(j) == ad) {
                    nb ++;
                }
            }
        }
        return nb;
    }
    
    public ADComponent getFirstComponentLeadingTo(ADComponent ad) {
        ADComponent ad1;
        int i, j;
        
        for (i=0; i<size(); i++) {
            ad1 = elementAt(i);
            for(j=0; j<ad1.getNbNext(); j++) {
                if (ad1.getNext(j) == ad) {
                    return ad1;
                }
            }
        }
        return null;
    }
    
    public int getIndexOfComponentLeadingTo(ADComponent adc, ADComponent adlast) {
        ADComponent ad1;
        int i, j;
        int index = 0;
        
        for (i=0; i<size(); i++) {
            ad1 = elementAt(i);
            for(j=0; j<ad1.getNbNext(); j++) {
                if (ad1.getNext(j) == adc) {
                    if (ad1 == adlast) {
                        return index;
                    } else {
                        index ++;
                    }
                }
            }
        }
        return -1;
    }
    
    public void setSelectedAll(boolean b) {
        if (ads != null) {
            ads.setSelected(b);
        }
        
        ADComponent ad;
        for (int i=0; i<size(); i++) {
            ad = elementAt(i);
            ad.setSelected(b);
        }
    }
    
    
    /** g !expr1 ...!exprn ?exprn+1 ...?exprn+m -> g!expr1...!exprn followed by g?exprn+1 ...?exprn+m
     */
    public void distinguishAllCallOn(Gate g) {
        ADComponent adc;
        ADActionStateWithGate adg, adg1;
        Gate g1;
        Vector<ADComponent> next;
        String s, s1;
        int sizeBegin = size();
        for(int i=0; i<sizeBegin; i++) {
            adc = elementAt(i);
            if (adc instanceof ADActionStateWithGate) {
                adg = (ADActionStateWithGate)adc;
                g1 = adg.getGate();
                if (g1 == g) {
                    // found an action state with the same gate
                    // create the new call
                    adg1 = new ADActionStateWithGate(g);
                    // link the old one to the new one
                    adg1.setNewNext(adg.getAllNext());
                    //link the new one to the previous next of the old one.
                    next = new Vector<ADComponent>();
                    next.add(adg1);
                    adg.setNewNext(next);
                    // add new component to activity diagram
                    add(adg1);
                    //modify action value of the old one
                    s = Conversion.cutSection(adg.getActionValue(), '?', '!');
                    s1 = Conversion.cutSection(adg.getActionValue(), '!', '?');
                    adg.setActionValue(s);
                    adg1.setActionValue(s1);
                }
            }
        }
    }
    
    public void translateActionStatesWithMultipleParams(TClass parent) {
        if (ads == null) {
            return;
        }
        
        ADActionStateWithMultipleParam admp;
        ADComponent adc, adc1;
        ADActionStateWithParam [] adsp;
        String action;
        Param p;
        
        int nbActions;
        boolean found = false;
        int i = 0, j;
        
        while ((found == false) && (i<size())) {
            adc = elementAt(i);
            //System.out.println("i=" + i);
            i++;
            if (adc instanceof ADActionStateWithMultipleParam) {
                found = true;
                admp = (ADActionStateWithMultipleParam) adc;
                
                action = admp.getActionValue();
                nbActions = Conversion.nbChar(action, ';') + 1;
                
                adsp = new ADActionStateWithParam[nbActions];
                
                // creating regular action states
                for(j=0; j<nbActions; j++) {
                    action = admp.getAction(j);
                    //System.out.println("j= " + j + " action =**" + action + "** parent=" + parent);
                    
                    p = parent.getParamFromActionState(action);
                    if (p == null) {
                        System.out.println("Translation error at translateActionStatesWithMultipleParams level");
                        return;
                    }
                    adsp[j] = new ADActionStateWithParam(p);
                    adsp[j].setActionValue(parent.getExprValueFromActionState(action));
                }
                
                // removing action states with multiple params
                removeElement(admp);
                
                // adding new regular action states
                for(j=0; j<nbActions; j++) {
                    //System.out.println("Adding " + adsp[j]);
                    add(adsp[j]);
                }
                
                // updating links
                for (j=1; j<nbActions; j++) {
                    //System.out.println("Updating link j=" + j);
                    adsp[j-1].addNext(adsp[j]);
                }
                adsp[nbActions-1].setNewNext(admp.getAllNext());
                
                // all components pointing to admp should now point to adsp[0]
                for(j=0; j<size(); j++) {
                    adc1 = elementAt(j);
                    adc1.updateNext(admp, adsp[0]);
                }
            }
        }
        
        if (found) {
            translateActionStatesWithMultipleParams(parent);
        }
    }
    
    public void print() {
        StringBuffer sb = new StringBuffer();
        printToStringBuffer(sb);
        System.out.println(sb);
        /*ADComponent adc;
        for(int i=0; i<size(); i++) {
            adc = (ADComponent)(elementAt(i));
            System.out.println(adc.toString() + "/" + adc.hashCode() + " ");
            System.out.println(printNextsToStringBuffer(adc));     
        }*/
        
    }
    
    public void printToStringBuffer(StringBuffer sb) {
        ADComponent adc;
        for(int i=0; i<size(); i++) {
            adc = elementAt(i);
            sb.append(adc.toString() + "/" + adc.hashCode() + " ");
            //System.out.println("appending i main=" + i + " component" + adc.toString());
            sb.append(printNextsToStringBuffer(adc));
        }
    }
    
    public StringBuffer printNextsToStringBuffer(ADComponent adc) {
        String s = "";
        Vector<ADComponent> v = adc.getAllNext();
        ADComponent adcbis;
        //System.out.println("Size of v:" + v.size());
        s = s+"\n\tNext: ";
        if (v.size() == 0) {
            return new StringBuffer(s + "none\n");
        }
        
        for(int i=0; i<v.size(); i++) {
            adcbis = v.elementAt(i);
            //System.out.println("appending i=" + i + " component" + adcbis.toString());
            if (adcbis != null) {
                s = s + adcbis.toString() + "/" + adcbis.hashCode() + " ";
            } else {
                s = s + "null ";
            }
        }
        return new StringBuffer(s + "\n");
    }
    
    public void removeAllNonReferencedElts() {
        ADComponent adc;
        while((adc = hasNonReferencedElts()) != null) {
            remove(adc);
			System.out.println("removed: " + adc);
        }
    }
    
    public ADComponent hasNonReferencedElts() {
        ADComponent adc;
        ADComponent adc1;
        for(int i=0; i<size(); i++) {
            adc = elementAt(i);
            if (adc != ads) {
                adc1 = getFirstComponentLeadingTo(adc);
                if (adc1 == null) {
                    // no component!
                    return adc;
                }
            }
        }
        return null;
    }
    
    public void addAllBlockingFrom(ActivityDiagram ad) {
        ADComponent adc;
        for(int i=0; i<ad.size(); i++) {
            adc = ad.elementAt(i);
            if (!(adc instanceof NonBlockingADComponent)) {
                add(adc);
            }
        }
    }
    
    public void copyFrom(ActivityDiagram ad) {
        removeAllElements();
        ads = ad.getStartState();
        for(int i=0; i<ad.size(); i++) {
            add(ad.elementAt(i));
        }
    }
    
    public void setAllSubstituteToNull() {
        ADComponent adc;
        for(int i=0; i<size(); i++) {
            adc = elementAt(i);
            adc.substitute = null;
        }
    }
    
    public void setRegularJunctions() {
        ADComponent adc, adc2;
        int i, j, cpt, index1, index2;
        Vector<ADComponent> list;
        Vector<ADComponent> junctions = new Vector<ADComponent>();
        ADJunction adj1, adj2;
        for(i=0; i<size(); i++) {
            adc = elementAt(i);
            if (!(adc instanceof MultiIncomingElt)) {
                //nb elt leading to adc ?
                cpt = 0;
                for(j=0; j<size(); j++) {
                    list = elementAt(j).getAllNext();
                    if (list.contains(adc)) {
                        cpt ++;
                    }
                }
                if (cpt > 1) {
                    // multiple links to adc
                    index1 = 0;
                    adj1 = null;
                    for(j=0; j<size(); j++) {
                        adc2 = elementAt(j);
                        list = adc2.getAllNext();
                        index2 = list.indexOf(adc);
                        if (list.contains(adc)) {
                            if (index1 == 0) {
                                adj1 = new ADJunction();
                                junctions.add(adj1);
                                adc2.setNextAtIndex(adj1, index2);
                            } else if (index1 == 1) {
                                adc2.setNextAtIndex(adj1, index2);
                            } else if ((index2 % 3) != 0) {
                                adc2.setNextAtIndex(adj1, index2);
                            } else {
                                adj2 = new ADJunction();
                                adj1.addNext(adj2);
                                adj1 = adj2;
                                junctions.add(adj1);
                                adc2.setNextAtIndex(adj1, index2);
                            }
                            index1 ++;
                        }
                    }
                    adj1.addNext(adc);
                }
            }
        }
        addAll(junctions);
    }
    
    public boolean hasRecursivePath(ADComponent adc) {
      boolean b = false;
      ADComponent adc1;
      setSelectedAll(false);
      for(int i=0; i<adc.getNbNext(); i++) {
        adc1 = adc.getNext(i);
        if (pathLeadsTo(adc, adc1)) {
          b = true;
          break;
        }
        
      }
      
      setSelectedAll(false);
      return b;
    }
    
    public boolean pathLeadsTo(ADComponent dest, ADComponent src) {
      if (src == dest) {
        return true;
      }
      
      if (src.getNbNext() == 0) {
        return false; 
      }
      
      if (src.isSelected()) {
        return false;
      }
      
      src.setSelected(true);
      
      // Explore all nexts
      ADComponent adc1;
      for(int i=0; i<src.getNbNext(); i++) {
        adc1 = src.getNext(i);
        if (pathLeadsTo(dest, adc1)) {
          return true;
        }
      }
      
      return false;
      
    }
    
    public void addADActionStateWithGateAfter(ADComponent adc, Gate g) {
        ADActionStateWithGate adag = new ADActionStateWithGate(g);
        adag.addNext(adc.getNext(0));
        adc.removeNext(adc.getNext(0));
        adc.addNextAtIndex(adag, 0);
        add(adag);
    }
    
    public void makeSpecialChoices(boolean variableAsActions) {
      ADComponent adc;
      boolean changeMade = true;
      while(changeMade) {
        changeMade = false;
        for(int i=0; i<size(); i++) {
          adc = elementAt(i);
          if (adc instanceof ADChoice) {
             if (!(((ADChoice)(adc)).isSpecialChoice(variableAsActions))) {
               if (makeSpecialChoice((ADChoice)(adc), variableAsActions)) {
                 changeMade = true;
               }
             }
          }
        }
      }
    }
    
    public boolean makeSpecialChoice(ADChoice adch, boolean variableAsActions) {
      ADComponent adc;
      ADChoice adch1;
      boolean go = true;
      int delay = 0;
      List<ADComponent> met;
      ADActionStateWithGate adag, adag1;
      ADJunction adj;

      //System.out.println("Working on choice=" + adch);

      for(int i=0; i<adch.getNbGuard(); i++) {
        delay = 0;
        if (!adch.isSpecialChoice(i, variableAsActions)) {
           // Go through the elements
           adc = adch.getNext(i);
           met = new LinkedList<ADComponent>();
           while(go) {
             if (met.contains(adc)) {
               go = false;
             } else {
               met.add(adc);
               if (adc instanceof ADJunction) {
                  adc = adc.getNext(0);
               } if ((adc instanceof ADDelay) || (adc instanceof ADLatency) || (adc instanceof ADTimeInterval)){
                  delay ++;
                  adc = adc.getNext(0);
               } else {
                 go = false;
               }
             }
           }
           //System.out.println("->Found choice=" + adch);
           if ((adc instanceof ADChoice) && (delay <1) && (adc != adch)) {
             // Combine choices together
             adch1 = (ADChoice)adc;
             if (adch1.isSpecialChoice(variableAsActions)) {
               //Good!
               //adch.removeGuard(i);
               //System.out.println("Working on it...");
               for(int j=0; j<adch1.getNbGuard(); j++) {
                 adj = new ADJunction();
                 adag = adch1.getADActionStateWithGate(j);
                 adj.addNext(adag.getNext(0));
                 adag.setNextAtIndex(adj, 0);
                 adag1 = new ADActionStateWithGate(adag.getGate());
                 adag1.setActionValue(adag.getActionValue());
                 adch.addNext(adag1);
                 if (adch.isGuarded(i)) {
                   //String g1 = adch1.getGuard(j);
                   if (adch1.isGuarded(j)) {
                     // Must combine the two guards
                     String g = adch1.getGuard(j) + " and " + adch.getGuard(i);
                     g = Conversion.replaceAllChar(g, '[', "(");
                     g = Conversion.replaceAllChar(g, ']', ")");
                     adch.addGuard(g);
                   } else {
                    adch.addGuard(adch.getGuard(i));
                   }
                 } else {
                   adch.addGuard(adch1.getGuard(j));
                 }
                 add(adag1);
                 add(adj);
                 if (adch.isSpecialChoice(variableAsActions)) {
                    //System.out.println("Now, special choice!");
                 } else {
                   //System.out.println("Still not a special choice!");
                 }
               }
               adch.removeGuard(i);
               adch.removeNext(i);
               i--;
               return true;
             }
           }
           if ((adc instanceof ADActionStateWithGate) && (delay <1)) {
             adag = (ADActionStateWithGate)adc;

             adj = new ADJunction();
             adj.addNext(adag.getNext(0));

             adag1 = new ADActionStateWithGate(adag.getGate());
             adag1.setActionValue(adag.getActionValue());
             adag1.addNext(adj);

             adag.setNextAtIndex(adj, 0);

             adch.addGuard(adch.getGuard(i));
             adch.addNext(adag1);
             adch.removeGuard(i);
             adch.removeNext(i);

             add(adag1);
             add(adj);

             if (adch.isSpecialChoice(variableAsActions)) {
                System.out.println("Now, special choice!");
             } else {
               System.out.println("Still not a special choice!");
             }
           }
        }
      }
      return false;
    }
    
    public int getNbOfJunctions() {
      int nb = 0;
      ADComponent adc;
      for(int i=0; i<size(); i++) {
        adc = elementAt(i);
        if (adc instanceof ADJunction) {
          nb++;
        }
      }
      return nb;
    }
	
	public int getNbOfSequence() {
      int nb = 0;
      ADComponent adc;
      for(int i=0; i<size(); i++) {
        adc = elementAt(i);
        if (adc instanceof ADSequence) {
          nb++;
        }
      }
      return nb;
    }
	
	public int getMaximumNbOfGuardsPerChoice() {
	  int nb = 0;
      ADComponent adc;
      for(int i=0; i<size(); i++) {
        adc = elementAt(i);
        if (adc instanceof ADChoice) {
          nb = Math.max(nb, ((ADChoice)adc).getNbGuard());
        }
      }
      return nb;
		
	}
	
	public int getMaximumNbOfGuardsPerSpecialChoice(boolean variableAsActions) {
	  int nb = 0;
      ADComponent adc;
	  ADChoice adch;
      for(int i=0; i<size(); i++) {
        adc = elementAt(i);
        if (adc instanceof ADChoice) {
			adch = (ADChoice)adc;
			if (adch.isSpecialChoice(variableAsActions) && (!adch.isSpecialChoiceAction(variableAsActions))) {
				nb = Math.max(nb, ((ADChoice)adc).getNbGuard());
			}
        }
      }
      return nb;
		
	}
	
	public ActivityDiagram duplicate(TClass t) {
		int i, j;
		int index;
		ADComponent ad1, ad2, ad3, ad4;
		Gate g;
		Param p;
		
		ActivityDiagram ad = new ActivityDiagram();
		ad.remove(0);
		for(i=0; i<size(); i++) {
			ad.add(get(i).makeSame());
			if (get(i) instanceof ADStart) {
				ad.setRawStartState((ADStart)(ad.get(i)));
			}
			//System.out.println("i=" + i + " component=" + ad.get(i));
		}
		
		// Must link components
		for(i=0; i<ad.size(); i++) {
			ad1 = ad.get(i);
			//System.out.println("Nb opf next of " + ad1 + " = " + ad1.getNbNext());
			ad2 = get(i);
			
			for(j=0; j<ad2.getNbNext(); j++) {
				ad3 = ad2.getNext(j);
				index = indexOf(ad3);
				if (index > -1) {
					//System.out.println("Linking i,j" + i + "," + j);
					ad4 = ad.get(index);
					ad1.addNext(ad4);
					//System.out.println("Next of " + ad1 + " = " + ad4);
				} else {
					//System.out.println("Wrong index");
				}
			}
		}
		
		// Must modify gates, guards of choice, params
		for(i=0; i<ad.size(); i++) {
			ad1 = ad.get(i);
			
			if (ad1 instanceof ADActionStateWithGate) {
				g = ((ADActionStateWithGate)(ad1)).getGate();
				g = t.getGateByName(g.getName());
				((ADActionStateWithGate)(ad1)).setGate(g);
			}
			
			if (ad1 instanceof ADTLO) {
				g = ((ADTLO)(ad1)).getGate();
				g = t.getGateByName(g.getName());
				((ADTLO)(ad1)).setGate(g);
			}
			
			if (ad1 instanceof ADActionStateWithParam) {
				p = ((ADActionStateWithParam)(ad1)).getParam();
				p = t.getParamByName(p.getName());
				((ADActionStateWithParam)(ad1)).setParam(p);
			}
			
			if (ad1 instanceof ADTimeCapture) {
				p = ((ADTimeCapture)(ad1)).getParam();
				p = t.getParamByName(p.getName());
				((ADTimeCapture)(ad1)).setParam(p);
			}
			
			if (ad1 instanceof ADChoice) {
				ADChoice adch = (ADChoice)ad1;
				ADChoice adch1 = (ADChoice)(get(i));
				for(j=0; j<adch1.getNbGuard(); j++) {
					adch.addGuard(adch1.getGuard(j));
				}
			}
			
			if (ad1 instanceof ADParallel) {
				ADParallel adpar = (ADParallel)ad1;
				ADParallel adpar1 = (ADParallel)(get(i));
				
				g = adpar1.getSpecialGate();
				if (g != null) {
					g = t.getGateByName(g.getName());
					adpar.setSpecialGate(g);
				}
				
				/*for(j=0; j<adch1.getNbGuard(); j++) {
					adch.addGuard(adch1.getGuard(j));
				}*/
			}
		}
		
		return ad;
		
		
	}
	
	/*public void replaceAllADActionStatewithMultipleParam(CorrespondanceTGElement _listE) {
		ADComponent adc;
		for(int i=0; i<size(); i++) {
			adc = (ADComponent)(elementAt(i));
			if (adc instanceof ADActionStateWithMultipleParam) {
				replaceADActionStatewithMultipleParam((ADActionStateWithMultipleParam)adc, _listE);
			}
      }
	}
	
	private void replaceADActionStatewithMultipleParam(ADActionStateWithMultipleParam _multi, CorrespondanceTGElement _listE) {
		ADComponent previous;
		ADActionStateWithParam adwp = null;
		String action;
		
		previous = getFirstComponentLeadingTo(_multi);
		
		if (previous == null) {
			return;
		}
		
		for(int i=0; i<_multi.nbOfActions(); i++) {
			action = _multi.getAction(i).trim();
			if (action.length() >0) {
				
				adwp = new ADActionStateWithParam();
				adwp.setActionValue(action);
				add(adwp);
				previous.removeNext(0);
				previous.addNext(adwp);
				previous = adwp;
			}
		}
		
		if (adwp != null) {
			adwp.addNext(_multi.getNext());
		}
		
		remove(_multi);
		
		System.out.println("Multi removed");
		
	}*/
}

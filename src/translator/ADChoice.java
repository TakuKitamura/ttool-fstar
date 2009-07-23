/**Copyright or  or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
* Class ADChoice
* Creation: 11/12/2003
* @version 1.0 11/12/2003
* @author Ludovic APVRILLE
* @see
*/

package translator;

import java.util.*;

import myutil.*;

public class ADChoice extends ADComponent implements NonBlockingADComponent {
    protected Vector guard; // String
    
    public ADChoice() {
        nbNext = 100000;
        guard = new Vector();
    }
    
    public void addGuard(String s) {
		if (s == null) {
			s = "[ ]";
		}
		s = s.trim();
		if (s.length() > 2) {
			String tmp = s.substring(1, s.length()-1);
			tmp = tmp.trim();
			if (tmp.length() == 0) {
				s = "[ ]";
			}
		}
        guard.addElement(s);
    }
    
    public void removeNext(Object o) {
        //System.out.println("removing next");
        int index = next.indexOf(o);
        if (index > -1) {
            //System.out.println("Removed!");
            next.removeElement(o);
            removeGuard(index);
        }
    }
	
	public void removeNext(int index) {
        if (index > -1) {
            //System.out.println("Removed!");
            next.removeElementAt(index);
            removeGuard(index);
        }
    }
    
    public void removeGuard(int index) {
        if (index < guard.size()) {
            guard.removeElementAt(index);
        }
    }
	
	public void setGuard(String s, int index) {
		if (index < guard.size()) {
			guard.setElementAt(s, index);
        }
	}
    
    public String getGuard(int i) {
        if (i<guard.size()) {
            return (String)(guard.elementAt(i));
        }
        return null;
    }
    
    public int getNbGuard() {
        return guard.size();
    }
    
    public boolean isGuarded(int i) {
        if (i>=guard.size()) {
            return false;
        } else {
            String s = (String)(guard.elementAt(i));
			String g = "";
			if (s != null) {
				g = Conversion.replaceAllChar(s.trim(), ' ', "");
			}
            if ((s == null) || (s.length() < 2) || (g.equals("[]"))) {
                return false;
            } else {
                return true;
            }
        }
    }
	
	public boolean isGuarded() {
		for(int i=0; i<getNbGuard(); i++){
			if (isGuarded(i)) {
				return true;
			}
		}
		return false;
	}
    
    public String toString() {
        String s = "Choice ";
        String s1;
        for(int i=0; i<guard.size(); i++) {
            s1 = getGuard(i);
            if (i ==0) {
                s += "(" + s1;
            } else {
                s += ", " + s1;
            }
        }
        if (guard.size() > 0) {
            s += ")";
        }
        return s;
    }
    
    public boolean isSpecialChoice(boolean variableAsActions) {
        // All actions following the choice must either be a action on a gate or a delay - determinitic or not -  followed by an action on a gate
        
        ADComponent adc, adc1;
        
        for(int i=0; i<next.size(); i++) {
            adc = getNext(i);
            
            if (adc instanceof ADActionStateWithGate) {
                
            } else if ((adc instanceof ADDelay) || (adc instanceof ADLatency) ||(adc instanceof ADTimeInterval)) {
                adc1 = adc.getNext(0);
                if (!(adc1 instanceof ADActionStateWithGate)) {
					if (variableAsActions) {
						if (!(adc1 instanceof ADActionStateWithParam)) {
							return false;
						}
					} else {
						return false;
					}
                }
            } else {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isSpecialChoiceDelay(boolean variableAsActions) {
		ADComponent adc, adc1;
		String value;
		
		/*if (isElseChoice()) {
		return true;
		}*/
		
        
        for(int i=0; i<next.size(); i++) {
            adc = getNext(i);
            
            if (adc instanceof ADActionStateWithGate) {
				
			} else if (adc instanceof ADDelay) {
				adc1 = adc.getNext(0);
				if (!(adc1 instanceof ADActionStateWithGate)) {
					if (variableAsActions) {
						if (!(adc1 instanceof ADActionStateWithParam)) {
							return false;
						}
					} else {
						return false;
					}
				}
			} else if (adc instanceof ADLatency) {
				value = ((ADLatency)adc).getValue().trim();
				if (value.equals("0")) {
					adc1 = adc.getNext(0);
					if (!(adc1 instanceof ADActionStateWithGate)) {
						if (variableAsActions) {
							if (!(adc instanceof ADActionStateWithParam)) {
								return false;
							}
						} else {
							return false;
						}
					}
				}
			} else if (adc instanceof ADTimeInterval) {
				ADTimeInterval adt = (ADTimeInterval)adc;
				if (adt.getMinValue().equals(adt.getMaxValue())) {
					adc1 = adc.getNext(0);
					if (!(adc1 instanceof ADActionStateWithGate)) {
						if (variableAsActions) {
							if (!(adc1 instanceof ADActionStateWithParam)) {
								return false;
							}
						} else {
							return false;
						}
					}
				}
			} else {
				return false;
			}
        }
        
        return true;
    }
	
	public boolean isSpecialChoiceAction(boolean variableAsActions) {
		ADComponent adc, adc1;
		String value;
		
        for(int i=0; i<next.size(); i++) {
            adc = getNext(i);
            
            if (!(adc instanceof ADActionStateWithGate)) {
				if (variableAsActions) {
					if (!(adc instanceof ADActionStateWithParam)) {
						return false;
					}
				} else {
					return false;
				}
			}
			
			
        }
        
        return true;
    }
	
	public boolean isSpecialChoice(int index, boolean variableAsActions) {
		ADComponent adc, adc1;
		adc = getNext(index);
		
		if (adc instanceof ADActionStateWithGate) {
			
		} else if ((adc instanceof ADDelay) || (adc instanceof ADLatency) ||(adc instanceof ADTimeInterval)) {
			adc1 = adc.getNext(0);
			if (!(adc1 instanceof ADActionStateWithGate)) {
				if (variableAsActions) {
					if (!(adc1 instanceof ADActionStateWithParam)) {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		
        return true;
    }
	
	
	public boolean choiceFollowedWithADActionStateWithGates() {     
        ADComponent adc;
        
        for(int i=0; i<next.size(); i++) {
            adc = getNext(i);
            
            if (!(adc instanceof ADActionStateWithGate)) {
				return false;
            }
        }
        
        return true;
	}
	
	// Choice with only two guards and one is exactly the other one but
	// with a not() on the condition
	public boolean isElseChoice() {  
		//System.out.println("Testing else choice");
		if (getNbGuard() != 2) {
			return false;
		}
		
		if (!isGuarded(0) && !isGuarded(1)) {
			return false;
		}
		
		String g0 = new String(getGuard(0));
		String g1 = new String(getGuard(1));
		
		//System.out.println("Else choice? g0=" + g0 + " g1=" + g1);
		
		if ((g0.indexOf("not") < 0) && (g1.indexOf("not") < 0)) {
			return false;
		}
		
		g0 = Conversion.replaceAllChar(g0, '[', "");
		g1 = Conversion.replaceAllChar(g1, '[', "");
		g0 = Conversion.replaceAllChar(g0, ' ', "");
		g1 = Conversion.replaceAllChar(g1, ' ', "");
		g0 = Conversion.replaceAllChar(g0, ']', "").trim();
		g1 = Conversion.replaceAllChar(g1, ']', "").trim();
		
		if (g0.startsWith("not(")) {
			g0 = g0.substring(4, g0.length()-1);
		} else if (g1.startsWith("not(")) {
			g1 = g1.substring(4, g1.length()-1);
		} 
		
		if (g0.compareTo(g1) == 0) {
			//System.out.println("Else guards g0=" + g0 + " g1=" + g1);
			return true;
		} else {
			return false;
		}
	}
    
	
    // This function assumes that this is a special choice
    // It returnes the gate of the first action of the choice
    public ADActionStateWithGate getADActionStateWithGate(int index) {
        ADComponent adc, adc1;
        
        adc = getNext(index);
        if (adc instanceof ADActionStateWithGate) {
            return (ADActionStateWithGate)adc;
        } else if ((adc instanceof ADDelay) || (adc instanceof ADLatency) ||(adc instanceof ADTimeInterval)) {
            adc1 = adc.getNext(0);
            if (adc1 instanceof ADActionStateWithGate) {
                return (ADActionStateWithGate)adc1;
            }
        }
        return null;
    }
    
    public String getMinDelay(int index) {
        ADComponent adc;
        
        adc = getNext(index);
        
        if (adc instanceof ADTimeInterval) {
            return ((ADTimeInterval)adc).getMinValue();
        } else if (adc instanceof ADDelay) {
            return ((ADDelay)adc).getValue();
        }
		
        return "-1";
    }
    
    public String getMaxDelay(int index) {
        /*ADComponent adc, adc1;
        
        adc = getNext(index);*/
        
        return "-1";
    }
	
    public ADComponent makeSame() {
		return new ADChoice();
    }
	
	public int getNextChoice() {
		ADComponent adc;
        
        for(int i=0; i<next.size(); i++) {
            adc = getNext(i);
			if (adc instanceof ADChoice) {
				return i;
			}
		}
		return -1;
	}
    
}


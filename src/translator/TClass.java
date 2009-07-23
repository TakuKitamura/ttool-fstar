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
 * Class TClass
 * Creation: 2001
 * @version 1.1 10/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package translator;

import java.util.*;
import myutil.*;

public class TClass {
    private String name;
    private String lotosName;
    private boolean activeClass;
    private Process process;
    private HLProcess hlprocess;
    private Vector gateList; // list of the gate
    private Vector paramList; // list of parameters
    private boolean selected = false;
    private ActivityDiagram ad;
    private String packageName = "";
    
    //private boolean ignoredJava; // For tclasses modeling links
    
    public TClass(String name, boolean isActive) {
        this.name = name;
        activeClass = isActive;
        gateList = new Vector();
        paramList = new Vector();
    }
    
    public void removeAllAttributes() {
      paramList = new Vector();
    }
    
    public void removeAllGates() {
      gateList = new Vector();
    }
	
	public boolean has(ADComponent adc) {
		if (ad == null) {
			return false;
		}
		
		return ad.contains(adc);
	}
    


    public void addGate(Gate g) {
        gateList.addElement(g);
    }
	
	public void removeGate(Gate g) {
		 gateList.removeElement(g);
	}
    
    public Gate addNewGateIfApplicable(String name) {
        Gate g = getGateByName(name);
        if (g != null) {
            return g;
        }
        g = new Gate(name, Gate.GATE, false);
        addGate(g);
        return g;
    }
    
    public Param addNewParamIfApplicable(String name, String type, String value) {
        Param p = getParamByName(name);
        if (p != null) {
            return p;
        }
        p = new Param(name, type, value);
        addParameter(p);
		//System.out.println("Added param:" + name);
        return p;
    }
	
	// The string may contain either '!' or '?' info
	// '{}' sections are removed. It is assumed to be at the end
	public void addParamFromAction(String s) {
		s = Conversion.replaceAllChar(s, ' ', "");
		int index0 = s.indexOf('{');
		int index1;
		if (index0 != -1) {
			s = s.substring(0, index0);
		}
		
		boolean go = true;
		boolean b;
		int index;
		String paramName;
		
		while (go) {
			index0 = s.indexOf('!');
			index1 = s.indexOf('?');
			b = (index0 == -1) && (index1 == -1);
			if (b) {
				go = false;
			} else {
				if (index1 == -1) {
					index = index0;
				} else {
					if (index0 == -1) {
						index = index1;
					} else {
						index = Math.min(index1, index0);
					}
				}
				s = s.substring(index+1, s.length());
				index0 = s.indexOf('!');
				index1 = s.indexOf('?');
				b = (index0 == -1) && (index1 == -1);
				if (!b) {
					if (index1 == -1) {
						index = index0;
					} else {
						if (index0 == -1) {
							index = index1;
						} else {
							index = Math.min(index1, index0);
						}
					}
					paramName = s.substring(0, index);
				} else {
					paramName = s;
				}
				//System.out.println("ParamName=" + paramName);
				if (Param.isAValidParamName(paramName)) {
					addNewParamIfApplicable(paramName, Param.NAT, "0");
				}
			}
		}
	}
    
    public Gate getGateByName(String s) {
        Gate g;
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            if (g.getName().equals(s)) {
                return g;
            }
        }
        return null;
    }
    
    public Param getParamByName(String s) {
        Param p;
        for(int i=0; i<paramList.size(); i++) {
            p = (Param)(paramList.elementAt(i));
            if (p.getName().equals(s)) {
                return p;
            }
        }
        return null;
    }
    
    public Vector getParamStartingWith(String name) {
       Param p;
       Vector v = new Vector();
        for(int i=0; i<paramList.size(); i++) {
            p = (Param)(paramList.elementAt(i));
            //System.out.println("Param=" + p.getName() + " vs " + name);
            if (p.getName().startsWith(name)) {
                v.add(p);
            }
        }
        return v;
        
    }
    
    public void setProcess(Process _process) {
        process = _process;
    }
    
    public void setHLProcess(HLProcess _hlprocess) {
        hlprocess = _hlprocess;
    }
    
    public void setGateList(Vector v) {
        gateList = v;
    }
    
    public void addParameter(Param par) {
        paramList.addElement(par);
    }
    
    public Param addParameterGenerateName(String name, String type, String initValue) {
        if (isParamNameInUse(name)) {
            name = generateParamName(name);
        }
        
        Param p = new Param(name, type, initValue);
        paramList.addElement(p);
        return p;
    }
    
    public boolean isParamNameInUse(String name) {
        Param p;
        
        for(int i=0; i<paramList.size(); i++) {
            p = (Param)(paramList.elementAt(i));
            if (p.getName().compareTo(name) ==0) {
                return true;
            }
        }
        return false;
    }
    
    public String generateParamName(String name) {
        String tmp;
         for(int i=0; i<1000000; i++) {
           tmp = name + "_" + i;
           if (!isParamNameInUse(tmp)) {
               return tmp;
           }
        }
        return name;
        
    }
    
    public void setActive(boolean b) {
        activeClass = b;
    }
    
    
    public void setActivityDiagram(ActivityDiagram _ad) {
        ad = _ad;
    }
    
    public ActivityDiagram getActivityDiagram() {
        return ad;
    }
    
    public Vector getGateList() {
        return gateList;
    }
    
    public int gateNb() {
        return gateList.size();
    }
	
	public Gate getGate(int index) {
		return (Gate)(gateList.get(index));
	}
    
    public Vector getParamList() {
        return paramList;
    }
    
    public int paramNb() {
        return paramList.size();
    }
	
	public Param getParam(int index) {
		return (Param)(paramList.get(index));
	}
	
	public void removeParam(int index) {
		//Param p = getParam(index);
		//System.out.println("Removing param:" + p.getName());
		paramList.removeElementAt(index);
	}
    
    
    public boolean isActive() {
        return activeClass;
    }
    
    public void select(boolean b) {
        selected = b;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String _name) {
        name = _name;
    }
    
    public String getLotosName() {
        return lotosName;
    }
    
    public void setLotosName(String _lotosName) {
        lotosName = _lotosName;
    }
    
    public Process getProcess() {
        return process;
    }
    
    public HLProcess getHLProcess() {
        return hlprocess;
    }
    
    // returns a gate from a string of the form " g !x ..?y"
    public Gate getGateFromActionState(String s) {
        
        // remove data sending and receiving
        int index1 = s.indexOf('!');
        int index2 = s.indexOf('?');     
        if ((index1 > -1) && (index2 > -1)) {
            s = s.substring(0, Math.min(index1, index2));
        } else if (index1 > -1) {
            s = s.substring(0, index1);
        } else if (index2 > -1) {
            s = s.substring(0, index2);
        }
        
        //remove {}
        index1 = s.indexOf('{');
        index2 = s.indexOf('}');
        if ((index1 > -1) && (index2 > -1)) {
            s = s.substring(0, Math.min(index1, index2));
        }
        
        // remove first spaces
        s = s.trim();
        /*int index = 0;
        while(s.charAt(index) == ' ') {
            index ++;
        }
        
        if (index != 0) {
            s = s.substring(index);
        }
        
        // remove last spaces
        index = s.indexOf(' ');
        if (index > -1) {
            s = s.substring(0, index);
        }*/
        
        //remove @t if necessary
        index1 = s.indexOf('@');
        if (index1 > -1) {
            s = s.substring(0, index1);
        }
        
        return getGateByName(s);
    }
    
    public String getGateNameFromActionState(String s) {
        int index1 = s.indexOf('!');
        int index2 = s.indexOf('?');
        
        // remove data sending and receiving
        if ((index1 > -1) && (index2 > -1)) {
            s = s.substring(0, Math.min(index1, index2));
        } else if (index1 > -1) {
            s = s.substring(0, index1);
        } else if (index2 > -1) {
            s = s.substring(0, index2);
        }
        
        //remove {}
        index1 = s.indexOf('{');
        index2 = s.indexOf('}');
        if ((index1 > -1) && (index2 > -1)) {
            s = s.substring(0, Math.min(index1, index2));
        }
        
        // remove first spaces
        int index = 0;
        while(s.charAt(index) == ' ') {
            index ++;
        }
        
        if (index != 0) {
            s = s.substring(index);
        }
        
        // remove last spaces
        index = s.indexOf(' ');
        if (index > -1) {
            s = s.substring(0, index);
        }
        
        return s;
    }
    
    
    public String getActionValueFromActionState(String s) {
        int index1 = s.indexOf('!');
        int index2 = s.indexOf('?');
        int index3 = s.indexOf('{');
        int index4 = s.indexOf('@');
        
        // remove data sending and receiving
        
        if ((index1 == -1) && (index2 == -1) && (index3 == -1) && (index4 == -1)) {
            return "";
        }
        
        if (index1 == -1) {
            index1 = 1500;
        }
        
        if (index2 == -1) {
            index2 = 1500;
        }
        
        if (index3 == -1) {
            index3 = 1500;
        }
        
         if (index4 == -1) {
            index4 = 1500;
        }

        int index = Math.min(index1, index2);
        index = Math.min(index, index3);
        index = Math.min(index, index4);
        return s.substring(index, s.length());
        
        /*if (index1 > -1) {
            if (index2 > -1) {
                return s.substring(Math.min(Math.min(index1, index2), s.length());
            }	else {
                return s.substring(index1, s.length());
            }
        } else {
            return s.substring(index2, s.length());
        }*/
    }
    
    // returns a param from a string of the form " n = expr"
    public Param getParamFromActionState(String s) {
        int index = s.indexOf('=');
        
        // remove data sending and receiving
        if (index > -1) {
            s = s.substring(0, index);
        }
        
        // remove first spaces
        s = s.trim();
        
        return getParamByName(s);
    }
    
    public String getParamNameFromActionState(String s) {
        int index = s.indexOf('=');
        
        // remove data sending and receiving
        if (index > -1) {
            s = s.substring(0, index);
        }  else {
            return null;
        }
        
        // remove first spaces
        index = 0;
        while(s.charAt(index) == ' ') {
            index ++;
        }
        
        if (index != 0) {
            s = s.substring(index);
        }
        
        // remove last spaces
        index = s.indexOf(' ');
        if (index > -1) {
            s = s.substring(0, index);
        }
        
        return s;
    }
    
    
    // + convertir les noms de parametres
    public String getExprValueFromActionState(String s) {
        int index = s.indexOf('=');
        
        // remove data sending and receiving
        if (index == -1) {
            return "";
        } else {
            return 	s.substring(index + 1);
        }
    }
    
    public void printParams() {
        Param p;
        
        for(int i=0; i<paramList.size(); i++) {
            p = (Param)(paramList.elementAt(i));
            System.out.println("Param #" + i + "= |" + p.getName() + "|");
           
        }
       
    }
	
	public void printParamsValues() {
        Param p;
        
        for(int i=0; i<paramList.size(); i++) {
            p = (Param)(paramList.elementAt(i));
            System.out.println("Param #" + i + "= |" + p.getName() + "=" + p.getValue() + "|");
           
        }
       
    }
	
	public void printGates() {
        Gate g;
        
        for(int i=0; i<gateList.size(); i++) {
            g = (Gate)(gateList.elementAt(i));
            System.out.println("Gate #" + i + "= |" + g.getName() + "|");
           
        }
       
    }
    
    public void setPackageName(String _name) {
        packageName = _name;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    // Each call on g0 is followed by a call on g1
    // Returns the number of added calls to g1
    public int duplicateCall(Gate g0, Gate g1) {
        ADComponent adc;
        ADActionStateWithGate adg0;
        ADTLO tlo0;
        int cpt = 0;
        
        for(int i=0; i<ad.size(); i++) {
            adc = (ADComponent)(ad.get(i));
            if (adc instanceof ADActionStateWithGate) {
                adg0 = (ADActionStateWithGate)adc;
                if (adg0.getGate() == g0) {
                    ad.addADActionStateWithGateAfter(adg0, g1);
                    cpt ++;
                }
            } else if (adc instanceof ADTLO) {
                tlo0 = (ADTLO)adc;
                if (tlo0.getGate() == g0) {
                    ad.addADActionStateWithGateAfter(tlo0, g1);
                    cpt ++;
                }
            }
        }
        
        return cpt;
    }
    
    // Used to know whether this performs at least one receiving action on gate g
    public boolean hasReceivingGate(Gate g) {
      ActivityDiagram ad = getActivityDiagram();
      ADComponent adc;
      ADActionStateWithGate adag;
      ADTLO adtlo;

      for(int i=0; i<ad.size(); i++) {
        adc = (ADComponent)(ad.get(i));
        if (adc instanceof ADActionStateWithGate) {
          adag = (ADActionStateWithGate)adc;
          if (adag.getGate() == g) {
             if (adag.getActionValue().indexOf('!') != -1) {
               return true;
             }
          }
        }
        if (adc instanceof ADTLO) {
          adtlo = (ADTLO)adc;
          if (adtlo.getGate() == g) {
             if (adtlo.getAction().indexOf('!') != -1) {
               return true;
             }
          }
        }
      }
      return false;
    }
    
    // g is supposed to be a Gate of the TClass
    public void addTopPreemptOn(Gate g) {
        ADPreempt pre = new ADPreempt();
        ActivityDiagram ad = getActivityDiagram();
        ADStart start = ad.getStartState();
        ADActionStateWithGate action = new ADActionStateWithGate(g);
        ADStop stop = new ADStop();
        
        ad.add(action);
        ad.add(pre);
        ad.add(stop);
        
        pre.addNext(start.getNext(0));
        pre.addNext(action);
        start.removeAllNext();
        start.addNext(pre);
        action.addNext(stop);
    }
    
    public int getNbOfJunctions() {
      return ad.getNbOfJunctions();
    }
	
	public int getMaximumNbOfGuardsPerChoice() {
		if (ad == null) {
			return 0;
		}
		
		return ad.getMaximumNbOfGuardsPerChoice();
	}
	
	public int getMaximumNbOfGuardsPerSpecialChoice(boolean variableAsActions) {
		if (ad == null) {
			return 0;
		}
		
		return ad.getMaximumNbOfGuardsPerSpecialChoice(variableAsActions);
	}
	
    
    /*public void setIgnoredJava(boolean b) {
        ignoredJava = b;
    }
    
    public boolean isIgnoredJava() {
        return ignoredJava;
    }*/
 
}

